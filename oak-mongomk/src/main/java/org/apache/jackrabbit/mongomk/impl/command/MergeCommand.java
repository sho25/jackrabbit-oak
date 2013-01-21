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
name|Iterator
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
name|mk
operator|.
name|model
operator|.
name|tree
operator|.
name|DiffBuilder
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
name|mk
operator|.
name|model
operator|.
name|tree
operator|.
name|NodeState
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
name|command
operator|.
name|Command
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
name|api
operator|.
name|model
operator|.
name|Node
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
name|FetchCommitAction
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
name|FetchHeadRevisionIdAction
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
name|json
operator|.
name|JsopParser
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
name|json
operator|.
name|NormalizingJsopHandler
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
name|CommitBuilder
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
name|NodeImpl
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
name|tree
operator|.
name|MongoNodeDelta
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
name|tree
operator|.
name|MongoNodeDelta
operator|.
name|Conflict
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
name|tree
operator|.
name|MongoNodeState
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
name|tree
operator|.
name|SimpleMongoNodeStore
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
name|util
operator|.
name|MongoUtil
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
name|oak
operator|.
name|commons
operator|.
name|PathUtils
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

begin_comment
comment|/**  * A {@code Command} for {@code MongoMicroKernel#merge(String, String)}  */
end_comment

begin_class
specifier|public
class|class
name|MergeCommand
extends|extends
name|BaseCommand
argument_list|<
name|String
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MergeCommand
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|branchRevisionId
decl_stmt|;
specifier|private
specifier|final
name|String
name|message
decl_stmt|;
comment|/**      * Constructs a {@code MergeCommandMongo}      *      * @param nodeStore Node store.      * @param branchRevisionId Branch revision id.      * @param message Merge message.      */
specifier|public
name|MergeCommand
parameter_list|(
name|MongoNodeStore
name|nodeStore
parameter_list|,
name|String
name|branchRevisionId
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|branchRevisionId
operator|=
name|branchRevisionId
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|MongoCommit
name|commit
init|=
operator|new
name|FetchCommitAction
argument_list|(
name|nodeStore
argument_list|,
name|MongoUtil
operator|.
name|toMongoRepresentation
argument_list|(
name|branchRevisionId
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
decl_stmt|;
name|String
name|branchId
init|=
name|commit
operator|.
name|getBranchId
argument_list|()
decl_stmt|;
if|if
condition|(
name|branchId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Can only merge a private branch commit"
argument_list|)
throw|;
block|}
name|long
name|rootNodeId
init|=
name|commit
operator|.
name|getRevisionId
argument_list|()
decl_stmt|;
name|FetchHeadRevisionIdAction
name|query2
init|=
operator|new
name|FetchHeadRevisionIdAction
argument_list|(
name|nodeStore
argument_list|)
decl_stmt|;
name|long
name|currentHead
init|=
name|query2
operator|.
name|execute
argument_list|()
decl_stmt|;
name|long
name|branchRootId
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|branchId
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|branchId
operator|.
name|indexOf
argument_list|(
literal|"-"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Commit
name|newCommit
decl_stmt|;
name|String
name|diff
init|=
name|getNonConflictingCommitsDiff
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|currentHead
argument_list|,
name|commit
operator|.
name|getRevisionId
argument_list|()
argument_list|)
argument_list|,
name|branchRootId
argument_list|,
name|branchId
argument_list|)
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|null
condition|)
block|{
name|newCommit
operator|=
name|CommitBuilder
operator|.
name|build
argument_list|(
literal|"/"
argument_list|,
name|diff
operator|.
name|toString
argument_list|()
argument_list|,
name|MongoUtil
operator|.
name|fromMongoRepresentation
argument_list|(
name|currentHead
argument_list|)
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Node
name|ourRoot
init|=
name|getNode
argument_list|(
literal|"/"
argument_list|,
name|rootNodeId
argument_list|,
name|branchId
argument_list|)
decl_stmt|;
comment|// Merge changes, if any, from trunk to branch.
name|Node
name|currentHeadNode
init|=
name|getNode
argument_list|(
literal|"/"
argument_list|,
name|currentHead
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentHead
operator|!=
name|branchRootId
condition|)
block|{
name|ourRoot
operator|=
name|mergeNodes
argument_list|(
name|ourRoot
argument_list|,
name|currentHeadNode
argument_list|,
name|branchRootId
argument_list|)
expr_stmt|;
block|}
name|diff
operator|=
operator|new
name|DiffBuilder
argument_list|(
name|MongoUtil
operator|.
name|wrap
argument_list|(
name|currentHeadNode
argument_list|)
argument_list|,
name|MongoUtil
operator|.
name|wrap
argument_list|(
name|ourRoot
argument_list|)
argument_list|,
literal|"/"
argument_list|,
operator|-
literal|1
argument_list|,
operator|new
name|SimpleMongoNodeStore
argument_list|()
argument_list|,
literal|""
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
if|if
condition|(
name|diff
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Merge of empty branch {} with differing content hashes encountered, "
operator|+
literal|"ignore and keep current head {}"
argument_list|,
name|branchRevisionId
argument_list|,
name|currentHead
argument_list|)
expr_stmt|;
return|return
name|MongoUtil
operator|.
name|fromMongoRepresentation
argument_list|(
name|currentHead
argument_list|)
return|;
block|}
name|newCommit
operator|=
name|CommitBuilder
operator|.
name|build
argument_list|(
literal|""
argument_list|,
name|diff
argument_list|,
name|MongoUtil
operator|.
name|fromMongoRepresentation
argument_list|(
name|currentHead
argument_list|)
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|Command
argument_list|<
name|Long
argument_list|>
name|command
init|=
operator|new
name|CommitCommandNew
argument_list|(
name|nodeStore
argument_list|,
name|newCommit
argument_list|)
decl_stmt|;
name|long
name|revision
init|=
name|command
operator|.
name|execute
argument_list|()
decl_stmt|;
return|return
name|MongoUtil
operator|.
name|fromMongoRepresentation
argument_list|(
name|revision
argument_list|)
return|;
block|}
comment|/**      * Checks whether there have been no conflicting commits on trunk since the      * branch was created and if so, returns the branch diff to be applied against      * trunk. Otherwise, it returns null.      *      * @param currentHead      * @param branchRootId      * @param branchId      * @return      */
specifier|private
name|String
name|getNonConflictingCommitsDiff
parameter_list|(
name|long
name|currentHead
parameter_list|,
name|long
name|branchRootId
parameter_list|,
name|String
name|branchId
parameter_list|)
block|{
name|FetchCommitsAction
name|action
init|=
operator|new
name|FetchCommitsAction
argument_list|(
name|nodeStore
argument_list|,
name|branchRootId
operator|+
literal|1
argument_list|,
name|currentHead
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|commits
init|=
name|action
operator|.
name|execute
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|affectedPathsBranch
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|affectedPathsTrunk
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|StringBuilder
name|diff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|commits
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|MongoCommit
name|commit
init|=
name|commits
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|affectedPaths
init|=
name|commit
operator|.
name|getAffectedPaths
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|affectedPath
range|:
name|affectedPaths
control|)
block|{
if|if
condition|(
name|branchId
operator|.
name|equals
argument_list|(
name|commit
operator|.
name|getBranchId
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|affectedPathsTrunk
operator|.
name|contains
argument_list|(
name|affectedPath
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|affectedPathsBranch
operator|.
name|add
argument_list|(
name|affectedPath
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|commit
operator|.
name|getBranchId
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|affectedPathsBranch
operator|.
name|contains
argument_list|(
name|affectedPath
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|affectedPathsTrunk
operator|.
name|add
argument_list|(
name|affectedPath
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|branchId
operator|.
name|equals
argument_list|(
name|commit
operator|.
name|getBranchId
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|diff
operator|.
name|append
argument_list|(
name|normalizeDiff
argument_list|(
name|commit
operator|.
name|getPath
argument_list|()
argument_list|,
name|commit
operator|.
name|getDiff
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Normalization error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|diff
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|?
name|diff
operator|.
name|toString
argument_list|()
else|:
literal|null
return|;
block|}
comment|/**      * Normalizes a JSOP diff by appending the path to all pathStrings of the      * operations.      *      * @param path the root path of the diff.      * @param diff the JSOP diff.      * @return the JSOP diff based on an empty root path.      */
specifier|private
name|String
name|normalizeDiff
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|diff
parameter_list|)
throws|throws
name|Exception
block|{
name|NormalizingJsopHandler
name|handler
init|=
operator|new
name|NormalizingJsopHandler
argument_list|()
decl_stmt|;
operator|new
name|JsopParser
argument_list|(
name|path
argument_list|,
name|diff
argument_list|,
name|handler
argument_list|)
operator|.
name|parse
argument_list|()
expr_stmt|;
return|return
name|handler
operator|.
name|getDiff
argument_list|()
return|;
block|}
specifier|private
name|NodeImpl
name|mergeNodes
parameter_list|(
name|Node
name|ourRoot
parameter_list|,
name|Node
name|theirRoot
parameter_list|,
name|Long
name|commonAncestorRevisionId
parameter_list|)
throws|throws
name|Exception
block|{
name|Node
name|baseRoot
init|=
name|getNode
argument_list|(
literal|"/"
argument_list|,
name|commonAncestorRevisionId
argument_list|)
decl_stmt|;
name|Node
name|theirRootCopy
init|=
name|copy
argument_list|(
name|theirRoot
argument_list|)
decl_stmt|;
comment|// Recursively merge 'our' changes with 'their' changes...
return|return
name|mergeNode
argument_list|(
name|baseRoot
argument_list|,
name|ourRoot
argument_list|,
name|theirRootCopy
argument_list|,
literal|"/"
argument_list|)
return|;
block|}
specifier|private
name|NodeImpl
name|mergeNode
parameter_list|(
name|Node
name|baseNode
parameter_list|,
name|Node
name|ourNode
parameter_list|,
name|Node
name|theirNode
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|MongoNodeDelta
name|theirChanges
init|=
operator|new
name|MongoNodeDelta
argument_list|(
operator|new
name|SimpleMongoNodeStore
argument_list|()
argument_list|,
name|MongoUtil
operator|.
name|wrap
argument_list|(
name|baseNode
argument_list|)
argument_list|,
name|MongoUtil
operator|.
name|wrap
argument_list|(
name|theirNode
argument_list|)
argument_list|)
decl_stmt|;
name|MongoNodeDelta
name|ourChanges
init|=
operator|new
name|MongoNodeDelta
argument_list|(
operator|new
name|SimpleMongoNodeStore
argument_list|()
argument_list|,
name|MongoUtil
operator|.
name|wrap
argument_list|(
name|baseNode
argument_list|)
argument_list|,
name|MongoUtil
operator|.
name|wrap
argument_list|(
name|ourNode
argument_list|)
argument_list|)
decl_stmt|;
name|NodeImpl
name|stagedNode
init|=
operator|(
name|NodeImpl
operator|)
name|theirNode
decl_stmt|;
comment|//new NodeImpl(path);
comment|// Apply our changes.
name|stagedNode
operator|.
name|getProperties
argument_list|()
operator|.
name|putAll
argument_list|(
name|ourChanges
operator|.
name|getAddedProperties
argument_list|()
argument_list|)
expr_stmt|;
name|stagedNode
operator|.
name|getProperties
argument_list|()
operator|.
name|putAll
argument_list|(
name|ourChanges
operator|.
name|getChangedProperties
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|ourChanges
operator|.
name|getRemovedProperties
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|stagedNode
operator|.
name|getProperties
argument_list|()
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|entry
range|:
name|ourChanges
operator|.
name|getAddedChildNodes
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|MongoNodeState
name|nodeState
init|=
operator|(
name|MongoNodeState
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|stagedNode
operator|.
name|addChildNodeEntry
argument_list|(
name|nodeState
operator|.
name|unwrap
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|entry
range|:
name|ourChanges
operator|.
name|getChangedChildNodes
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|theirChanges
operator|.
name|getChangedChildNodes
argument_list|()
operator|.
name|containsKey
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|MongoNodeState
name|nodeState
init|=
operator|(
name|MongoNodeState
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|stagedNode
operator|.
name|addChildNodeEntry
argument_list|(
name|nodeState
operator|.
name|unwrap
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|name
range|:
name|ourChanges
operator|.
name|getRemovedChildNodes
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|stagedNode
operator|.
name|removeChildNodeEntry
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Conflict
argument_list|>
name|conflicts
init|=
name|theirChanges
operator|.
name|listConflicts
argument_list|(
name|ourChanges
argument_list|)
decl_stmt|;
comment|// resolve/report merge conflicts
for|for
control|(
name|Conflict
name|conflict
range|:
name|conflicts
control|)
block|{
name|String
name|conflictName
init|=
name|conflict
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|conflictPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|conflictName
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|conflict
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|PROPERTY_VALUE_CONFLICT
case|:
throw|throw
operator|new
name|Exception
argument_list|(
literal|"concurrent modification of property "
operator|+
name|conflictPath
operator|+
literal|" with conflicting values: \""
operator|+
name|ourNode
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|conflictName
argument_list|)
operator|+
literal|"\", \""
operator|+
name|theirNode
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|conflictName
argument_list|)
argument_list|)
throw|;
case|case
name|NODE_CONTENT_CONFLICT
case|:
block|{
if|if
condition|(
name|ourChanges
operator|.
name|getChangedChildNodes
argument_list|()
operator|.
name|containsKey
argument_list|(
name|conflictName
argument_list|)
condition|)
block|{
comment|// modified subtrees
name|Node
name|baseChild
init|=
name|baseNode
operator|.
name|getChildNodeEntry
argument_list|(
name|conflictName
argument_list|)
decl_stmt|;
name|Node
name|ourChild
init|=
name|ourNode
operator|.
name|getChildNodeEntry
argument_list|(
name|conflictName
argument_list|)
decl_stmt|;
name|Node
name|theirChild
init|=
name|theirNode
operator|.
name|getChildNodeEntry
argument_list|(
name|conflictName
argument_list|)
decl_stmt|;
comment|// merge the dirty subtrees recursively
name|mergeNode
argument_list|(
name|baseChild
argument_list|,
name|ourChild
argument_list|,
name|theirChild
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|conflictName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// todo handle/merge colliding node creation
throw|throw
operator|new
name|Exception
argument_list|(
literal|"colliding concurrent node creation: "
operator|+
name|conflictPath
argument_list|)
throw|;
block|}
break|break;
block|}
case|case
name|REMOVED_DIRTY_PROPERTY_CONFLICT
case|:
name|stagedNode
operator|.
name|getProperties
argument_list|()
operator|.
name|remove
argument_list|(
name|conflictName
argument_list|)
expr_stmt|;
break|break;
case|case
name|REMOVED_DIRTY_NODE_CONFLICT
case|:
comment|//stagedNode.remove(conflictName);
name|stagedNode
operator|.
name|removeChildNodeEntry
argument_list|(
name|conflictName
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|stagedNode
return|;
block|}
specifier|private
name|Node
name|getNode
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|revisionId
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getNode
argument_list|(
name|path
argument_list|,
name|revisionId
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|private
name|Node
name|getNode
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|revisionId
parameter_list|,
name|String
name|branchId
parameter_list|)
throws|throws
name|Exception
block|{
name|GetNodesCommandNew
name|command
init|=
operator|new
name|GetNodesCommandNew
argument_list|(
name|nodeStore
argument_list|,
name|path
argument_list|,
name|revisionId
argument_list|)
decl_stmt|;
name|command
operator|.
name|setBranchId
argument_list|(
name|branchId
argument_list|)
expr_stmt|;
return|return
name|command
operator|.
name|execute
argument_list|()
return|;
block|}
specifier|private
name|NodeImpl
name|copy
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|NodeImpl
name|copy
init|=
operator|new
name|NodeImpl
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|copy
operator|.
name|setRevisionId
argument_list|(
name|node
operator|.
name|getRevisionId
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|node
operator|.
name|getProperties
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|copy
operator|.
name|addProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|Node
argument_list|>
name|it
init|=
name|node
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Node
name|child
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|copy
operator|.
name|addChildNodeEntry
argument_list|(
name|copy
argument_list|(
name|child
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|copy
return|;
block|}
block|}
end_class

end_unit

