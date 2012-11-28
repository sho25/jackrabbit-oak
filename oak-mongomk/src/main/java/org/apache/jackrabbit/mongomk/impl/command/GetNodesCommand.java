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
name|HashMap
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
name|ListIterator
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
name|command
operator|.
name|exception
operator|.
name|InconsistentNodeHierarchyException
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
comment|/**  * {@code Command} for {@code MongoMicroKernel#getNodes(String, String, int, long, int, String)}  */
end_comment

begin_class
specifier|public
class|class
name|GetNodesCommand
extends|extends
name|BaseCommand
argument_list|<
name|Node
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
name|GetNodesCommand
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
name|String
name|branchId
decl_stmt|;
specifier|private
name|int
name|depth
init|=
name|FetchNodesAction
operator|.
name|LIMITLESS_DEPTH
decl_stmt|;
specifier|private
name|Long
name|revisionId
decl_stmt|;
specifier|private
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|lastCommits
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|MongoNode
argument_list|>
name|pathAndNodeMap
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|problematicNodes
decl_stmt|;
specifier|private
name|Node
name|rootNode
decl_stmt|;
comment|/**      * Constructs a new {@code GetNodesCommandMongo}.      *      * @param nodeStore Node store.      * @param path The root path of the nodes to get.      * @param revisionId The revision id or null for head revision.      */
specifier|public
name|GetNodesCommand
parameter_list|(
name|MongoNodeStore
name|nodeStore
parameter_list|,
name|String
name|path
parameter_list|,
name|Long
name|revisionId
parameter_list|)
block|{
name|super
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|revisionId
operator|=
name|revisionId
expr_stmt|;
block|}
comment|/**      * Sets the branchId for the command.      *      * @param branchId Branch id.      */
specifier|public
name|void
name|setBranchId
parameter_list|(
name|String
name|branchId
parameter_list|)
block|{
name|this
operator|.
name|branchId
operator|=
name|branchId
expr_stmt|;
block|}
comment|/**      * Sets the depth for the command.      *      * @param depth The depth for the command or -1 for limitless depth.      */
specifier|public
name|void
name|setDepth
parameter_list|(
name|int
name|depth
parameter_list|)
block|{
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|readLastCommits
argument_list|()
expr_stmt|;
name|deriveProblematicNodes
argument_list|()
expr_stmt|;
name|readRootNode
argument_list|()
expr_stmt|;
return|return
name|rootNode
return|;
block|}
specifier|private
name|void
name|readRootNode
parameter_list|()
throws|throws
name|InconsistentNodeHierarchyException
block|{
name|readNodesByPath
argument_list|()
expr_stmt|;
name|boolean
name|verified
init|=
name|verifyProblematicNodes
argument_list|()
operator|&&
name|verifyNodeHierarchy
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|verified
condition|)
block|{
throw|throw
operator|new
name|InconsistentNodeHierarchyException
argument_list|()
throw|;
block|}
name|buildNodeStructure
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
literal|3
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
name|InconsistentNodeHierarchyException
return|;
block|}
specifier|private
name|void
name|buildNodeStructure
parameter_list|()
block|{
name|MongoNode
name|nodeMongoRootOfPath
init|=
name|pathAndNodeMap
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|rootNode
operator|=
name|buildNodeStructure
argument_list|(
name|nodeMongoRootOfPath
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeImpl
name|buildNodeStructure
parameter_list|(
name|MongoNode
name|nodeMongo
parameter_list|)
block|{
if|if
condition|(
name|nodeMongo
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|NodeImpl
name|node
init|=
name|MongoNode
operator|.
name|toNode
argument_list|(
name|nodeMongo
argument_list|)
decl_stmt|;
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
name|MongoNode
name|nodeMongoChild
init|=
name|pathAndNodeMap
operator|.
name|get
argument_list|(
name|child
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeMongoChild
operator|!=
literal|null
condition|)
block|{
name|NodeImpl
name|nodeChild
init|=
name|buildNodeStructure
argument_list|(
name|nodeMongoChild
argument_list|)
decl_stmt|;
name|node
operator|.
name|addChildNodeEntry
argument_list|(
name|nodeChild
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|node
return|;
block|}
specifier|private
name|void
name|deriveProblematicNodes
parameter_list|()
block|{
name|problematicNodes
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ListIterator
argument_list|<
name|MongoCommit
argument_list|>
name|iterator
init|=
name|lastCommits
operator|.
name|listIterator
argument_list|()
init|;
name|iterator
operator|.
name|hasPrevious
argument_list|()
condition|;
control|)
block|{
name|MongoCommit
name|commitMongo
init|=
name|iterator
operator|.
name|previous
argument_list|()
decl_stmt|;
name|long
name|revisionId
init|=
name|commitMongo
operator|.
name|getRevisionId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|affectedPaths
init|=
name|commitMongo
operator|.
name|getAffectedPaths
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|affectedPaths
control|)
block|{
name|problematicNodes
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|readLastCommits
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|revisionId
operator|==
literal|null
condition|)
block|{
name|revisionId
operator|=
operator|new
name|GetHeadRevisionCommand
argument_list|(
name|nodeStore
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
name|boolean
name|commitExists
init|=
literal|false
decl_stmt|;
name|lastCommits
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
for|for
control|(
name|MongoCommit
name|commit
range|:
name|lastCommits
control|)
block|{
if|if
condition|(
name|commit
operator|.
name|getRevisionId
argument_list|()
operator|.
name|equals
argument_list|(
name|revisionId
argument_list|)
condition|)
block|{
name|commitExists
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|commitExists
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Commit with revision %d could not be found"
argument_list|,
name|revisionId
argument_list|)
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|readNodesByPath
parameter_list|()
block|{
name|FetchNodesAction
name|query
init|=
operator|new
name|FetchNodesAction
argument_list|(
name|nodeStore
argument_list|,
name|path
argument_list|,
literal|true
argument_list|,
name|revisionId
argument_list|)
decl_stmt|;
name|query
operator|.
name|setBranchId
argument_list|(
name|branchId
argument_list|)
expr_stmt|;
name|query
operator|.
name|setValidCommits
argument_list|(
name|lastCommits
argument_list|)
expr_stmt|;
name|query
operator|.
name|setDepth
argument_list|(
name|depth
argument_list|)
expr_stmt|;
name|pathAndNodeMap
operator|=
name|query
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
specifier|private
name|boolean
name|verifyNodeHierarchy
parameter_list|()
block|{
name|boolean
name|verified
init|=
literal|false
decl_stmt|;
name|verified
operator|=
name|verifyNodeHierarchyRec
argument_list|(
name|path
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|verified
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Node hierarchy could not be verified because"
operator|+
literal|" some nodes were inconsistent: %s"
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|verified
return|;
block|}
specifier|private
name|boolean
name|verifyNodeHierarchyRec
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|currentDepth
parameter_list|)
block|{
name|boolean
name|verified
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|pathAndNodeMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|MongoNode
name|nodeMongo
init|=
name|pathAndNodeMap
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeMongo
operator|!=
literal|null
condition|)
block|{
name|verified
operator|=
literal|true
expr_stmt|;
if|if
condition|(
operator|(
name|depth
operator|==
operator|-
literal|1
operator|)
operator|||
operator|(
name|currentDepth
operator|<
name|depth
operator|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|childNames
init|=
name|nodeMongo
operator|.
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|childNames
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|childName
range|:
name|childNames
control|)
block|{
name|String
name|childPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|childName
argument_list|)
decl_stmt|;
name|verified
operator|=
name|verifyNodeHierarchyRec
argument_list|(
name|childPath
argument_list|,
operator|++
name|currentDepth
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|verified
condition|)
block|{
break|break;
block|}
block|}
block|}
block|}
block|}
return|return
name|verified
return|;
block|}
specifier|private
name|boolean
name|verifyProblematicNodes
parameter_list|()
block|{
name|boolean
name|verified
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|problematicNodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|path
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Long
name|revisionId
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|MongoNode
name|nodeMongo
init|=
name|pathAndNodeMap
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeMongo
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|revisionId
operator|.
name|equals
argument_list|(
name|nodeMongo
operator|.
name|getRevisionId
argument_list|()
argument_list|)
condition|)
block|{
name|verified
operator|=
literal|false
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Node could not be verified because the expected revisionId did not match: %d (expected) vs %d (actual)"
argument_list|,
name|revisionId
argument_list|,
name|nodeMongo
operator|.
name|getRevisionId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|verified
return|;
block|}
block|}
end_class

end_unit

