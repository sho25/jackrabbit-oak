begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|command
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|api
operator|.
name|instruction
operator|.
name|Instruction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|api
operator|.
name|model
operator|.
name|Commit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|MongoNodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|action
operator|.
name|FetchCommitsAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|action
operator|.
name|FetchNodesAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|action
operator|.
name|ReadAndIncHeadRevisionAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|action
operator|.
name|SaveAndSetHeadRevisionAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|action
operator|.
name|SaveCommitAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|action
operator|.
name|SaveNodesAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|command
operator|.
name|exception
operator|.
name|ConflictingCommitException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|instruction
operator|.
name|CommitCommandInstructionVisitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|model
operator|.
name|MongoCommit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|model
operator|.
name|MongoNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|model
operator|.
name|MongoSync
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|QueryBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|WriteResult
import|;
end_import

begin_comment
comment|/**  * {@code Command} for {@code MongoMicroKernel#commit(String, String, String, String)}  */
end_comment

begin_class
specifier|public
class|class
name|CommitCommand
extends|extends
name|BaseCommand
argument_list|<
name|Long
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CommitCommand
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|MongoCommit
name|commit
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|affectedPaths
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|MongoNode
argument_list|>
name|existingNodes
decl_stmt|;
specifier|private
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|validCommits
decl_stmt|;
specifier|private
name|MongoSync
name|mongoSync
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|MongoNode
argument_list|>
name|nodes
decl_stmt|;
specifier|private
name|Long
name|revisionId
decl_stmt|;
specifier|private
name|String
name|branchId
decl_stmt|;
comment|/**      * Constructs a new {@code CommitCommandMongo}.      *      * @param nodeStore Node store.      * @param commit {@link Commit}      */
specifier|public
name|CommitCommand
parameter_list|(
name|MongoNodeStore
name|nodeStore
parameter_list|,
name|Commit
name|commit
parameter_list|)
block|{
name|super
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|commit
operator|=
operator|(
name|MongoCommit
operator|)
name|commit
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Long
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
do|do
block|{
name|mongoSync
operator|=
operator|new
name|ReadAndIncHeadRevisionAction
argument_list|(
name|nodeStore
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|revisionId
operator|=
name|mongoSync
operator|.
name|getNextRevisionId
argument_list|()
operator|-
literal|1
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Trying to commit: {} @rev{}"
argument_list|,
name|commit
operator|.
name|getDiff
argument_list|()
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
name|readValidCommits
argument_list|()
expr_stmt|;
name|readBranchIdFromBaseCommit
argument_list|()
expr_stmt|;
name|createMongoNodes
argument_list|()
expr_stmt|;
name|prepareCommit
argument_list|()
expr_stmt|;
name|readExistingNodes
argument_list|()
expr_stmt|;
name|mergeNodes
argument_list|()
expr_stmt|;
name|prepareMongoNodes
argument_list|()
expr_stmt|;
operator|new
name|SaveNodesAction
argument_list|(
name|nodeStore
argument_list|,
name|nodes
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
operator|new
name|SaveCommitAction
argument_list|(
name|nodeStore
argument_list|,
name|commit
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|success
operator|=
name|saveAndSetHeadRevision
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
operator|!
name|success
condition|)
do|;
return|return
name|revisionId
return|;
block|}
specifier|private
name|void
name|readValidCommits
parameter_list|()
block|{
name|validCommits
operator|=
operator|new
name|FetchCommitsAction
argument_list|(
name|nodeStore
argument_list|,
name|revisionId
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getNumOfRetries
parameter_list|()
block|{
return|return
literal|100
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsRetry
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
name|e
operator|instanceof
name|ConflictingCommitException
return|;
block|}
specifier|private
name|void
name|readBranchIdFromBaseCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|commitBranchId
init|=
name|commit
operator|.
name|getBranchId
argument_list|()
decl_stmt|;
if|if
condition|(
name|commitBranchId
operator|!=
literal|null
condition|)
block|{
comment|// This is a newly created branch, so no need to check the base
comment|// commit's branch id.
name|branchId
operator|=
name|commitBranchId
expr_stmt|;
return|return;
block|}
name|Long
name|baseRevisionId
init|=
name|commit
operator|.
name|getBaseRevisionId
argument_list|()
decl_stmt|;
if|if
condition|(
name|baseRevisionId
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|MongoCommit
name|commit
range|:
name|validCommits
control|)
block|{
if|if
condition|(
name|baseRevisionId
operator|.
name|equals
argument_list|(
name|commit
operator|.
name|getRevisionId
argument_list|()
argument_list|)
condition|)
block|{
name|branchId
operator|=
name|commit
operator|.
name|getBranchId
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|createMongoNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|CommitCommandInstructionVisitor
name|visitor
init|=
operator|new
name|CommitCommandInstructionVisitor
argument_list|(
name|nodeStore
argument_list|,
name|mongoSync
operator|.
name|getHeadRevisionId
argument_list|()
argument_list|,
name|validCommits
argument_list|)
decl_stmt|;
name|visitor
operator|.
name|setBranchId
argument_list|(
name|branchId
argument_list|)
expr_stmt|;
for|for
control|(
name|Instruction
name|instruction
range|:
name|commit
operator|.
name|getInstructions
argument_list|()
control|)
block|{
name|instruction
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|MongoNode
argument_list|>
name|pathNodeMap
init|=
name|visitor
operator|.
name|getPathNodeMap
argument_list|()
decl_stmt|;
name|affectedPaths
operator|=
name|pathNodeMap
operator|.
name|keySet
argument_list|()
expr_stmt|;
name|nodes
operator|=
operator|new
name|HashSet
argument_list|<
name|MongoNode
argument_list|>
argument_list|(
name|pathNodeMap
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|prepareCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|commit
operator|.
name|setAffectedPaths
argument_list|(
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|(
name|affectedPaths
argument_list|)
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setBaseRevisionId
argument_list|(
name|mongoSync
operator|.
name|getHeadRevisionId
argument_list|()
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setRevisionId
argument_list|(
name|revisionId
argument_list|)
expr_stmt|;
if|if
condition|(
name|commit
operator|.
name|getBranchId
argument_list|()
operator|==
literal|null
operator|&&
name|branchId
operator|!=
literal|null
condition|)
block|{
name|commit
operator|.
name|setBranchId
argument_list|(
name|branchId
argument_list|)
expr_stmt|;
block|}
name|commit
operator|.
name|removeField
argument_list|(
literal|"_id"
argument_list|)
expr_stmt|;
comment|// In case this is a retry.
block|}
specifier|private
name|void
name|readExistingNodes
parameter_list|()
block|{
name|FetchNodesAction
name|action
init|=
operator|new
name|FetchNodesAction
argument_list|(
name|nodeStore
argument_list|,
name|affectedPaths
argument_list|,
name|mongoSync
operator|.
name|getHeadRevisionId
argument_list|()
argument_list|)
decl_stmt|;
name|action
operator|.
name|setBranchId
argument_list|(
name|branchId
argument_list|)
expr_stmt|;
name|action
operator|.
name|setValidCommits
argument_list|(
name|validCommits
argument_list|)
expr_stmt|;
name|existingNodes
operator|=
name|action
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|mergeNodes
parameter_list|()
block|{
for|for
control|(
name|MongoNode
name|existingNode
range|:
name|existingNodes
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|MongoNode
name|committingNode
range|:
name|nodes
control|)
block|{
if|if
condition|(
name|existingNode
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|committingNode
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Found existing node to merge: {}"
argument_list|,
name|existingNode
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Existing node: {}"
argument_list|,
name|existingNode
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Committing node: {}"
argument_list|,
name|committingNode
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|existingProperties
init|=
name|existingNode
operator|.
name|getProperties
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|existingProperties
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|committingNode
operator|.
name|setProperties
argument_list|(
name|existingProperties
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Merged properties for {}: {}"
argument_list|,
name|existingNode
operator|.
name|getPath
argument_list|()
argument_list|,
name|existingProperties
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|existingChildren
init|=
name|existingNode
operator|.
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|existingChildren
operator|!=
literal|null
condition|)
block|{
name|committingNode
operator|.
name|setChildren
argument_list|(
name|existingChildren
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Merged children for {}: {}"
argument_list|,
name|existingNode
operator|.
name|getPath
argument_list|()
argument_list|,
name|existingChildren
argument_list|)
expr_stmt|;
block|}
name|committingNode
operator|.
name|setBaseRevisionId
argument_list|(
name|existingNode
operator|.
name|getRevisionId
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Merged node for {}: {}"
argument_list|,
name|existingNode
operator|.
name|getPath
argument_list|()
argument_list|,
name|committingNode
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|prepareMongoNodes
parameter_list|()
block|{
for|for
control|(
name|MongoNode
name|committingNode
range|:
name|nodes
control|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Preparing children (added and removed) of {}"
argument_list|,
name|committingNode
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Committing node: {}"
argument_list|,
name|committingNode
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|children
init|=
name|committingNode
operator|.
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
name|children
operator|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|addedChildren
init|=
name|committingNode
operator|.
name|getAddedChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|addedChildren
operator|!=
literal|null
condition|)
block|{
name|children
operator|.
name|addAll
argument_list|(
name|addedChildren
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|removedChildren
init|=
name|committingNode
operator|.
name|getRemovedChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|removedChildren
operator|!=
literal|null
condition|)
block|{
name|children
operator|.
name|removeAll
argument_list|(
name|removedChildren
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|children
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|temp
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|children
argument_list|)
decl_stmt|;
comment|// remove all duplicates
name|committingNode
operator|.
name|setChildren
argument_list|(
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|(
name|temp
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|committingNode
operator|.
name|setChildren
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
init|=
name|committingNode
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|addedProperties
init|=
name|committingNode
operator|.
name|getAddedProps
argument_list|()
decl_stmt|;
if|if
condition|(
name|addedProperties
operator|!=
literal|null
condition|)
block|{
name|properties
operator|.
name|putAll
argument_list|(
name|addedProperties
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|removedProperties
init|=
name|committingNode
operator|.
name|getRemovedProps
argument_list|()
decl_stmt|;
if|if
condition|(
name|removedProperties
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|removedProperties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|properties
operator|.
name|remove
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|properties
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|committingNode
operator|.
name|setProperties
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|committingNode
operator|.
name|setProperties
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|committingNode
operator|.
name|setRevisionId
argument_list|(
name|revisionId
argument_list|)
expr_stmt|;
if|if
condition|(
name|branchId
operator|!=
literal|null
condition|)
block|{
name|committingNode
operator|.
name|setBranchId
argument_list|(
name|branchId
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"Prepared committing node: {}"
argument_list|,
name|committingNode
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Protected for testing purposed only.      *      * @return True if the operation was successful.      * @throws Exception If an exception happens.      */
specifier|protected
name|boolean
name|saveAndSetHeadRevision
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|assumedHeadRevision
init|=
name|this
operator|.
name|mongoSync
operator|.
name|getHeadRevisionId
argument_list|()
decl_stmt|;
name|MongoSync
name|mongoSync
init|=
operator|new
name|SaveAndSetHeadRevisionAction
argument_list|(
name|nodeStore
argument_list|,
name|assumedHeadRevision
argument_list|,
name|revisionId
argument_list|)
operator|.
name|execute
argument_list|()
decl_stmt|;
if|if
condition|(
name|mongoSync
operator|==
literal|null
condition|)
block|{
comment|// There have been commit(s) in the meantime. If it's a conflicting
comment|// update, retry the whole operation and count against number of retries.
comment|// If not, need to retry again (in order to write commits and nodes properly)
comment|// but don't count these retries against number of retries.
if|if
condition|(
name|conflictingCommitsExist
argument_list|(
name|assumedHeadRevision
argument_list|)
condition|)
block|{
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Encountered a concurrent conflicting update"
operator|+
literal|", thus can't commit revision %s with affected paths %s."
operator|+
literal|" Retry with a new revision."
argument_list|,
name|revisionId
argument_list|,
name|commit
operator|.
name|getAffectedPaths
argument_list|()
argument_list|)
decl_stmt|;
name|logger
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|markAsFailed
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|ConflictingCommitException
argument_list|(
name|message
argument_list|)
throw|;
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Encountered a concurrent but non-conflicting update"
operator|+
literal|", thus can't commit revision {} with affected paths {}."
operator|+
literal|" Retry with a new revision."
argument_list|,
name|revisionId
argument_list|,
name|commit
operator|.
name|getAffectedPaths
argument_list|()
argument_list|)
expr_stmt|;
name|markAsFailed
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|boolean
name|conflictingCommitsExist
parameter_list|(
name|long
name|baseRevisionId
parameter_list|)
block|{
name|QueryBuilder
name|queryBuilder
init|=
name|QueryBuilder
operator|.
name|start
argument_list|(
name|MongoCommit
operator|.
name|KEY_FAILED
argument_list|)
operator|.
name|notEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
operator|.
name|and
argument_list|(
name|MongoCommit
operator|.
name|KEY_BASE_REVISION_ID
argument_list|)
operator|.
name|is
argument_list|(
name|baseRevisionId
argument_list|)
operator|.
name|and
argument_list|(
name|MongoCommit
operator|.
name|KEY_REVISION_ID
argument_list|)
operator|.
name|greaterThan
argument_list|(
literal|0L
argument_list|)
operator|.
name|and
argument_list|(
name|MongoCommit
operator|.
name|KEY_REVISION_ID
argument_list|)
operator|.
name|notEquals
argument_list|(
name|revisionId
argument_list|)
decl_stmt|;
name|DBObject
name|query
init|=
name|queryBuilder
operator|.
name|get
argument_list|()
decl_stmt|;
name|DBCollection
name|collection
init|=
name|nodeStore
operator|.
name|getCommitCollection
argument_list|()
decl_stmt|;
name|MongoCommit
name|conflictingCommit
init|=
operator|(
name|MongoCommit
operator|)
name|collection
operator|.
name|findOne
argument_list|(
name|query
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|affectedPath
range|:
name|conflictingCommit
operator|.
name|getAffectedPaths
argument_list|()
control|)
block|{
if|if
condition|(
name|affectedPaths
operator|.
name|contains
argument_list|(
name|affectedPath
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|void
name|markAsFailed
parameter_list|()
throws|throws
name|Exception
block|{
name|DBCollection
name|commitCollection
init|=
name|nodeStore
operator|.
name|getCommitCollection
argument_list|()
decl_stmt|;
name|DBObject
name|query
init|=
name|QueryBuilder
operator|.
name|start
argument_list|(
literal|"_id"
argument_list|)
operator|.
name|is
argument_list|(
name|commit
operator|.
name|getObjectId
argument_list|(
literal|"_id"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|DBObject
name|update
init|=
operator|new
name|BasicDBObject
argument_list|(
literal|"$set"
argument_list|,
operator|new
name|BasicDBObject
argument_list|(
name|MongoCommit
operator|.
name|KEY_FAILED
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
argument_list|)
decl_stmt|;
name|WriteResult
name|writeResult
init|=
name|commitCollection
operator|.
name|update
argument_list|(
name|query
argument_list|,
name|update
argument_list|)
decl_stmt|;
if|if
condition|(
name|writeResult
operator|.
name|getError
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// FIXME This is potentially a bug that we need to handle.
throw|throw
operator|new
name|Exception
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Update wasn't successful: %s"
argument_list|,
name|writeResult
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

