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
name|instruction
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
name|Map
operator|.
name|Entry
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
operator|.
name|AddNodeInstruction
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
operator|.
name|CopyNodeInstruction
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
operator|.
name|MoveNodeInstruction
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
operator|.
name|RemoveNodeInstruction
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
operator|.
name|SetPropertyInstruction
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
name|InstructionVisitor
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
name|FetchNodesActionNew
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
name|NodeExistsCommand
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
name|exception
operator|.
name|NotFoundException
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
name|oak
operator|.
name|commons
operator|.
name|PathUtils
import|;
end_import

begin_comment
comment|/**  * This class reads in the instructions generated from JSON, applies basic checks  * and creates a node map for {@code CommitCommandMongo} to work on later.  */
end_comment

begin_class
specifier|public
class|class
name|CommitCommandInstructionVisitor
implements|implements
name|InstructionVisitor
block|{
specifier|private
specifier|final
name|long
name|headRevisionId
decl_stmt|;
specifier|private
specifier|final
name|MongoNodeStore
name|nodeStore
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|MongoNode
argument_list|>
name|pathNodeMap
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|validCommits
decl_stmt|;
specifier|private
name|String
name|branchId
decl_stmt|;
comment|/**      * Creates {@code CommitCommandInstructionVisitor}      *      * @param nodeStore Node store.      * @param headRevisionId Head revision.      * @param validCommits      */
specifier|public
name|CommitCommandInstructionVisitor
parameter_list|(
name|MongoNodeStore
name|nodeStore
parameter_list|,
name|long
name|headRevisionId
parameter_list|,
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|validCommits
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
name|this
operator|.
name|headRevisionId
operator|=
name|headRevisionId
expr_stmt|;
name|this
operator|.
name|validCommits
operator|=
name|validCommits
expr_stmt|;
name|pathNodeMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|MongoNode
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sets the branch id associated with the commit. It can be null.      *      * @param branchId Branch id or null.      */
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
comment|/**      * Returns the generated node map after visit methods are called.      *      * @return Node map.      */
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|MongoNode
argument_list|>
name|getPathNodeMap
parameter_list|()
block|{
return|return
name|pathNodeMap
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|AddNodeInstruction
name|instruction
parameter_list|)
block|{
name|String
name|nodePath
init|=
name|instruction
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|checkAbsolutePath
argument_list|(
name|nodePath
argument_list|)
expr_stmt|;
name|String
name|nodeName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|nodePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// This happens in initial commit.
name|getStagedNode
argument_list|(
name|nodePath
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|parentNodePath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|nodePath
argument_list|)
decl_stmt|;
name|MongoNode
name|parent
init|=
name|getStoredNode
argument_list|(
name|parentNodePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|.
name|childExists
argument_list|(
name|nodeName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"There's already a child node with name '"
operator|+
name|nodeName
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|getStagedNode
argument_list|(
name|nodePath
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addChild
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|SetPropertyInstruction
name|instruction
parameter_list|)
block|{
name|String
name|key
init|=
name|instruction
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|instruction
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|MongoNode
name|node
init|=
name|getStoredNode
argument_list|(
name|instruction
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|node
operator|.
name|removeProp
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|node
operator|.
name|addProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|RemoveNodeInstruction
name|instruction
parameter_list|)
block|{
name|String
name|nodePath
init|=
name|instruction
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|checkAbsolutePath
argument_list|(
name|nodePath
argument_list|)
expr_stmt|;
name|String
name|parentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|nodePath
argument_list|)
decl_stmt|;
name|String
name|nodeName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|nodePath
argument_list|)
decl_stmt|;
name|MongoNode
name|parent
init|=
name|getStoredNode
argument_list|(
name|parentPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|parent
operator|.
name|childExists
argument_list|(
name|nodeName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Node "
operator|+
name|nodeName
operator|+
literal|" does not exists at parent path: "
operator|+
name|parentPath
argument_list|)
throw|;
block|}
name|parent
operator|.
name|removeChild
argument_list|(
name|PathUtils
operator|.
name|getName
argument_list|(
name|nodePath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|CopyNodeInstruction
name|instruction
parameter_list|)
block|{
name|String
name|srcPath
init|=
name|instruction
operator|.
name|getSourcePath
argument_list|()
decl_stmt|;
name|checkAbsolutePath
argument_list|(
name|srcPath
argument_list|)
expr_stmt|;
name|String
name|destPath
init|=
name|instruction
operator|.
name|getDestPath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|destPath
argument_list|)
condition|)
block|{
name|destPath
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|instruction
operator|.
name|getPath
argument_list|()
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|checkAbsolutePath
argument_list|(
name|destPath
argument_list|)
expr_stmt|;
block|}
name|String
name|srcParentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
name|String
name|srcNodeName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
name|String
name|destParentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
name|String
name|destNodeName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
name|MongoNode
name|srcParent
init|=
name|getStoredNode
argument_list|(
name|srcParentPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|srcParent
operator|.
name|childExists
argument_list|(
name|srcNodeName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|srcPath
argument_list|)
throw|;
block|}
name|MongoNode
name|destParent
init|=
name|getStoredNode
argument_list|(
name|destParentPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|destParent
operator|.
name|childExists
argument_list|(
name|destNodeName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Node already exists at copy destination path: "
operator|+
name|destPath
argument_list|)
throw|;
block|}
comment|// First, copy the existing nodes.
name|Map
argument_list|<
name|String
argument_list|,
name|MongoNode
argument_list|>
name|nodesToCopy
init|=
operator|new
name|FetchNodesActionNew
argument_list|(
name|nodeStore
argument_list|,
name|srcPath
argument_list|,
name|FetchNodesActionNew
operator|.
name|LIMITLESS_DEPTH
argument_list|,
name|headRevisionId
argument_list|)
operator|.
name|execute
argument_list|()
decl_stmt|;
for|for
control|(
name|MongoNode
name|nodeMongo
range|:
name|nodesToCopy
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|oldPath
init|=
name|nodeMongo
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|oldPathRel
init|=
name|PathUtils
operator|.
name|relativize
argument_list|(
name|srcPath
argument_list|,
name|oldPath
argument_list|)
decl_stmt|;
name|String
name|newPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|destPath
argument_list|,
name|oldPathRel
argument_list|)
decl_stmt|;
name|nodeMongo
operator|.
name|setPath
argument_list|(
name|newPath
argument_list|)
expr_stmt|;
name|nodeMongo
operator|.
name|removeField
argument_list|(
literal|"_id"
argument_list|)
expr_stmt|;
name|pathNodeMap
operator|.
name|put
argument_list|(
name|newPath
argument_list|,
name|nodeMongo
argument_list|)
expr_stmt|;
block|}
comment|// Then, copy any staged changes.
name|MongoNode
name|srcNode
init|=
name|getStoredNode
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
name|MongoNode
name|destNode
init|=
name|getStagedNode
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
name|copyStagedChanges
argument_list|(
name|srcNode
argument_list|,
name|destNode
argument_list|)
expr_stmt|;
comment|// Finally, add to destParent.
name|pathNodeMap
operator|.
name|put
argument_list|(
name|destPath
argument_list|,
name|destNode
argument_list|)
expr_stmt|;
name|destParent
operator|.
name|addChild
argument_list|(
name|destNodeName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|MoveNodeInstruction
name|instruction
parameter_list|)
block|{
name|String
name|srcPath
init|=
name|instruction
operator|.
name|getSourcePath
argument_list|()
decl_stmt|;
name|String
name|destPath
init|=
name|instruction
operator|.
name|getDestPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Target path cannot be descendant of source path: "
operator|+
name|destPath
argument_list|)
throw|;
block|}
name|String
name|srcParentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
name|String
name|srcNodeName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
name|String
name|destParentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
name|String
name|destNodeName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
name|MongoNode
name|srcParent
init|=
name|getStoredNode
argument_list|(
name|srcParentPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|srcParent
operator|.
name|childExists
argument_list|(
name|srcNodeName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|srcPath
argument_list|)
throw|;
block|}
name|MongoNode
name|destParent
init|=
name|getStoredNode
argument_list|(
name|destParentPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|destParent
operator|.
name|childExists
argument_list|(
name|destNodeName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Node already exists at move destination path: "
operator|+
name|destPath
argument_list|)
throw|;
block|}
comment|// First, copy the existing nodes.
name|Map
argument_list|<
name|String
argument_list|,
name|MongoNode
argument_list|>
name|nodesToCopy
init|=
operator|new
name|FetchNodesActionNew
argument_list|(
name|nodeStore
argument_list|,
name|srcPath
argument_list|,
name|FetchNodesActionNew
operator|.
name|LIMITLESS_DEPTH
argument_list|,
name|headRevisionId
argument_list|)
operator|.
name|execute
argument_list|()
decl_stmt|;
for|for
control|(
name|MongoNode
name|nodeMongo
range|:
name|nodesToCopy
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|oldPath
init|=
name|nodeMongo
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|oldPathRel
init|=
name|PathUtils
operator|.
name|relativize
argument_list|(
name|srcPath
argument_list|,
name|oldPath
argument_list|)
decl_stmt|;
name|String
name|newPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|destPath
argument_list|,
name|oldPathRel
argument_list|)
decl_stmt|;
name|nodeMongo
operator|.
name|setPath
argument_list|(
name|newPath
argument_list|)
expr_stmt|;
name|nodeMongo
operator|.
name|removeField
argument_list|(
literal|"_id"
argument_list|)
expr_stmt|;
name|pathNodeMap
operator|.
name|put
argument_list|(
name|newPath
argument_list|,
name|nodeMongo
argument_list|)
expr_stmt|;
block|}
comment|// Then, copy any staged changes.
name|MongoNode
name|srcNode
init|=
name|getStoredNode
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
name|MongoNode
name|destNode
init|=
name|getStagedNode
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
name|copyStagedChanges
argument_list|(
name|srcNode
argument_list|,
name|destNode
argument_list|)
expr_stmt|;
comment|// Finally, add to destParent and remove from srcParent.
name|getStagedNode
argument_list|(
name|destPath
argument_list|)
expr_stmt|;
name|destParent
operator|.
name|addChild
argument_list|(
name|destNodeName
argument_list|)
expr_stmt|;
name|srcParent
operator|.
name|removeChild
argument_list|(
name|srcNodeName
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkAbsolutePath
parameter_list|(
name|String
name|srcPath
parameter_list|)
block|{
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|srcPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Absolute path expected: "
operator|+
name|srcPath
argument_list|)
throw|;
block|}
block|}
specifier|private
name|MongoNode
name|getStagedNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|MongoNode
name|node
init|=
name|pathNodeMap
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
name|node
operator|=
operator|new
name|MongoNode
argument_list|()
expr_stmt|;
name|node
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|pathNodeMap
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
specifier|private
name|MongoNode
name|getStoredNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|MongoNode
name|node
init|=
name|pathNodeMap
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
return|return
name|node
return|;
block|}
comment|// First need to check that the path is indeed valid.
name|NodeExistsCommand
name|existCommand
init|=
operator|new
name|NodeExistsCommand
argument_list|(
name|nodeStore
argument_list|,
name|path
argument_list|,
name|headRevisionId
argument_list|)
decl_stmt|;
name|existCommand
operator|.
name|setBranchId
argument_list|(
name|branchId
argument_list|)
expr_stmt|;
name|existCommand
operator|.
name|setValidCommits
argument_list|(
name|validCommits
argument_list|)
expr_stmt|;
name|boolean
name|exists
init|=
literal|false
decl_stmt|;
try|try
block|{
name|exists
operator|=
name|existCommand
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
if|if
condition|(
operator|!
name|exists
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|path
operator|+
literal|" @rev"
operator|+
name|headRevisionId
argument_list|)
throw|;
block|}
name|node
operator|=
name|existCommand
operator|.
name|getNode
argument_list|()
expr_stmt|;
name|node
operator|.
name|removeField
argument_list|(
literal|"_id"
argument_list|)
expr_stmt|;
name|pathNodeMap
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|node
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
specifier|private
name|void
name|copyStagedChanges
parameter_list|(
name|MongoNode
name|srcNode
parameter_list|,
name|MongoNode
name|destNode
parameter_list|)
block|{
comment|// Copy staged changes at the top level.
name|copyAddedNodes
argument_list|(
name|srcNode
argument_list|,
name|destNode
argument_list|)
expr_stmt|;
name|copyRemovedNodes
argument_list|(
name|srcNode
argument_list|,
name|destNode
argument_list|)
expr_stmt|;
name|copyAddedProperties
argument_list|(
name|srcNode
argument_list|,
name|destNode
argument_list|)
expr_stmt|;
name|copyRemovedProperties
argument_list|(
name|srcNode
argument_list|,
name|destNode
argument_list|)
expr_stmt|;
comment|// Recursively add staged changes of the descendants.
name|List
argument_list|<
name|String
argument_list|>
name|srcChildren
init|=
name|srcNode
operator|.
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|srcChildren
operator|==
literal|null
operator|||
name|srcChildren
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
name|String
name|childName
range|:
name|srcChildren
control|)
block|{
name|String
name|oldChildPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|srcNode
operator|.
name|getPath
argument_list|()
argument_list|,
name|childName
argument_list|)
decl_stmt|;
name|MongoNode
name|oldChild
init|=
name|getStoredNode
argument_list|(
name|oldChildPath
argument_list|)
decl_stmt|;
name|String
name|newChildPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|destNode
operator|.
name|getPath
argument_list|()
argument_list|,
name|childName
argument_list|)
decl_stmt|;
name|MongoNode
name|newChild
init|=
name|getStagedNode
argument_list|(
name|newChildPath
argument_list|)
decl_stmt|;
name|copyStagedChanges
argument_list|(
name|oldChild
argument_list|,
name|newChild
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|copyRemovedProperties
parameter_list|(
name|MongoNode
name|srcNode
parameter_list|,
name|MongoNode
name|destNode
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|removedProps
init|=
name|srcNode
operator|.
name|getRemovedProps
argument_list|()
decl_stmt|;
if|if
condition|(
name|removedProps
operator|==
literal|null
operator|||
name|removedProps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
name|String
name|key
range|:
name|removedProps
operator|.
name|keySet
argument_list|()
control|)
block|{
name|destNode
operator|.
name|removeProp
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|copyAddedNodes
parameter_list|(
name|MongoNode
name|srcNode
parameter_list|,
name|MongoNode
name|destNode
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|addedChildren
init|=
name|srcNode
operator|.
name|getAddedChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|addedChildren
operator|==
literal|null
operator|||
name|addedChildren
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
name|String
name|childName
range|:
name|addedChildren
control|)
block|{
name|getStagedNode
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|destNode
operator|.
name|getPath
argument_list|()
argument_list|,
name|childName
argument_list|)
argument_list|)
expr_stmt|;
name|destNode
operator|.
name|addChild
argument_list|(
name|childName
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|copyRemovedNodes
parameter_list|(
name|MongoNode
name|srcNode
parameter_list|,
name|MongoNode
name|destNode
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|removedChildren
init|=
name|srcNode
operator|.
name|getRemovedChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|removedChildren
operator|==
literal|null
operator|||
name|removedChildren
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
name|String
name|child
range|:
name|removedChildren
control|)
block|{
name|destNode
operator|.
name|removeChild
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|copyAddedProperties
parameter_list|(
name|MongoNode
name|srcNode
parameter_list|,
name|MongoNode
name|destNode
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|addedProps
init|=
name|srcNode
operator|.
name|getAddedProps
argument_list|()
decl_stmt|;
if|if
condition|(
name|addedProps
operator|==
literal|null
operator|||
name|addedProps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|addedProps
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|destNode
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
block|}
block|}
end_class

end_unit

