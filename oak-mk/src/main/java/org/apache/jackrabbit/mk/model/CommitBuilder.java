begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

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
name|mk
operator|.
name|store
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
name|mk
operator|.
name|store
operator|.
name|RevisionStore
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
name|util
operator|.
name|PathUtils
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|CommitBuilder
block|{
specifier|private
name|Id
name|baseRevId
decl_stmt|;
specifier|private
specifier|final
name|String
name|msg
decl_stmt|;
specifier|private
specifier|final
name|RevisionStore
name|store
decl_stmt|;
comment|// key is a path
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|MutableNode
argument_list|>
name|staged
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|MutableNode
argument_list|>
argument_list|()
decl_stmt|;
comment|// change log
specifier|private
specifier|final
name|List
argument_list|<
name|Change
argument_list|>
name|changeLog
init|=
operator|new
name|ArrayList
argument_list|<
name|Change
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|CommitBuilder
parameter_list|(
name|Id
name|baseRevId
parameter_list|,
name|String
name|msg
parameter_list|,
name|RevisionStore
name|store
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|baseRevId
operator|=
name|baseRevId
expr_stmt|;
name|this
operator|.
name|msg
operator|=
name|msg
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
specifier|public
name|void
name|addNode
parameter_list|(
name|String
name|parentNodePath
parameter_list|,
name|String
name|nodeName
parameter_list|)
throws|throws
name|Exception
block|{
name|addNode
argument_list|(
name|parentNodePath
argument_list|,
name|nodeName
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addNode
parameter_list|(
name|String
name|parentNodePath
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|)
throws|throws
name|Exception
block|{
name|MutableNode
name|modParent
init|=
name|getOrCreateStagedNode
argument_list|(
name|parentNodePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|modParent
operator|.
name|getChildNodeEntry
argument_list|(
name|nodeName
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"there's already a child node with name '"
operator|+
name|nodeName
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|MutableNode
name|newChild
init|=
operator|new
name|MutableNode
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|newChild
operator|.
name|getProperties
argument_list|()
operator|.
name|putAll
argument_list|(
name|properties
argument_list|)
expr_stmt|;
comment|// id will be computed on commit
name|modParent
operator|.
name|add
argument_list|(
operator|new
name|ChildNode
argument_list|(
name|nodeName
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|newPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentNodePath
argument_list|,
name|nodeName
argument_list|)
decl_stmt|;
name|staged
operator|.
name|put
argument_list|(
name|newPath
argument_list|,
name|newChild
argument_list|)
expr_stmt|;
comment|// update change log
name|changeLog
operator|.
name|add
argument_list|(
operator|new
name|AddNode
argument_list|(
name|parentNodePath
argument_list|,
name|nodeName
argument_list|,
name|properties
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeNode
parameter_list|(
name|String
name|nodePath
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
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
name|MutableNode
name|parent
init|=
name|getOrCreateStagedNode
argument_list|(
name|parentPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|.
name|remove
argument_list|(
name|nodeName
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|nodePath
argument_list|)
throw|;
block|}
comment|// update staging area
name|removeStagedNodes
argument_list|(
name|nodePath
argument_list|)
expr_stmt|;
comment|// update change log
name|changeLog
operator|.
name|add
argument_list|(
operator|new
name|RemoveNode
argument_list|(
name|nodePath
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|moveNode
parameter_list|(
name|String
name|srcPath
parameter_list|,
name|String
name|destPath
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
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
name|Exception
argument_list|(
literal|"target path cannot be descendant of source path: "
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
name|MutableNode
name|srcParent
init|=
name|getOrCreateStagedNode
argument_list|(
name|srcParentPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcParentPath
operator|.
name|equals
argument_list|(
name|destParentPath
argument_list|)
condition|)
block|{
if|if
condition|(
name|srcParent
operator|.
name|getChildNodeEntry
argument_list|(
name|destNodeName
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"node already exists at move destination path: "
operator|+
name|destPath
argument_list|)
throw|;
block|}
if|if
condition|(
name|srcParent
operator|.
name|rename
argument_list|(
name|srcNodeName
argument_list|,
name|destNodeName
argument_list|)
operator|==
literal|null
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
block|}
else|else
block|{
name|ChildNode
name|srcCNE
init|=
name|srcParent
operator|.
name|remove
argument_list|(
name|srcNodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcCNE
operator|==
literal|null
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
name|MutableNode
name|destParent
init|=
name|getOrCreateStagedNode
argument_list|(
name|destParentPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|destParent
operator|.
name|getChildNodeEntry
argument_list|(
name|destNodeName
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"node already exists at move destination path: "
operator|+
name|destPath
argument_list|)
throw|;
block|}
name|destParent
operator|.
name|add
argument_list|(
operator|new
name|ChildNode
argument_list|(
name|destNodeName
argument_list|,
name|srcCNE
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// update staging area
name|moveStagedNodes
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
comment|// update change log
name|changeLog
operator|.
name|add
argument_list|(
operator|new
name|MoveNode
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|copyNode
parameter_list|(
name|String
name|srcPath
parameter_list|,
name|String
name|destPath
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
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
name|MutableNode
name|srcParent
init|=
name|getOrCreateStagedNode
argument_list|(
name|srcParentPath
argument_list|)
decl_stmt|;
name|ChildNode
name|srcCNE
init|=
name|srcParent
operator|.
name|getChildNodeEntry
argument_list|(
name|srcNodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcCNE
operator|==
literal|null
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
name|MutableNode
name|destParent
init|=
name|getOrCreateStagedNode
argument_list|(
name|destParentPath
argument_list|)
decl_stmt|;
name|destParent
operator|.
name|add
argument_list|(
operator|new
name|ChildNode
argument_list|(
name|destNodeName
argument_list|,
name|srcCNE
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// update change log
name|changeLog
operator|.
name|add
argument_list|(
operator|new
name|CopyNode
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|nodePath
parameter_list|,
name|String
name|propName
parameter_list|,
name|String
name|propValue
parameter_list|)
throws|throws
name|Exception
block|{
name|MutableNode
name|node
init|=
name|getOrCreateStagedNode
argument_list|(
name|nodePath
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
name|node
operator|.
name|getProperties
argument_list|()
decl_stmt|;
if|if
condition|(
name|propValue
operator|==
literal|null
condition|)
block|{
name|properties
operator|.
name|remove
argument_list|(
name|propName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|properties
operator|.
name|put
argument_list|(
name|propName
argument_list|,
name|propValue
argument_list|)
expr_stmt|;
block|}
comment|// update change log
name|changeLog
operator|.
name|add
argument_list|(
operator|new
name|SetProperty
argument_list|(
name|nodePath
argument_list|,
name|propName
argument_list|,
name|propValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setProperties
parameter_list|(
name|String
name|nodePath
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|)
throws|throws
name|Exception
block|{
name|MutableNode
name|node
init|=
name|getOrCreateStagedNode
argument_list|(
name|nodePath
argument_list|)
decl_stmt|;
name|node
operator|.
name|getProperties
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|node
operator|.
name|getProperties
argument_list|()
operator|.
name|putAll
argument_list|(
name|properties
argument_list|)
expr_stmt|;
comment|// update change log
name|changeLog
operator|.
name|add
argument_list|(
operator|new
name|SetProperties
argument_list|(
name|nodePath
argument_list|,
name|properties
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Id
comment|/* new revId */
name|doCommit
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|staged
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// nothing to commit
return|return
name|baseRevId
return|;
block|}
name|Id
name|currentHead
init|=
name|store
operator|.
name|getHeadCommitId
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|currentHead
operator|.
name|equals
argument_list|(
name|baseRevId
argument_list|)
condition|)
block|{
comment|// todo gracefully handle certain conflicts (e.g. changes on moved sub-trees, competing deletes etc)
comment|// update base revision to new head
name|baseRevId
operator|=
name|currentHead
expr_stmt|;
comment|// clear staging area
name|staged
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// replay change log on new base revision
comment|// copy log in order to avoid concurrent modifications
name|List
argument_list|<
name|Change
argument_list|>
name|log
init|=
operator|new
name|ArrayList
argument_list|<
name|Change
argument_list|>
argument_list|(
name|changeLog
argument_list|)
decl_stmt|;
for|for
control|(
name|Change
name|change
range|:
name|log
control|)
block|{
name|change
operator|.
name|apply
argument_list|()
expr_stmt|;
block|}
block|}
name|Id
name|rootNodeId
init|=
name|persistStagedNodes
argument_list|()
decl_stmt|;
name|Id
name|newRevId
decl_stmt|;
name|store
operator|.
name|lockHead
argument_list|()
expr_stmt|;
try|try
block|{
name|currentHead
operator|=
name|store
operator|.
name|getHeadCommitId
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|currentHead
operator|.
name|equals
argument_list|(
name|baseRevId
argument_list|)
condition|)
block|{
name|StoredNode
name|baseRoot
init|=
name|store
operator|.
name|getRootNode
argument_list|(
name|baseRevId
argument_list|)
decl_stmt|;
name|StoredNode
name|theirRoot
init|=
name|store
operator|.
name|getRootNode
argument_list|(
name|currentHead
argument_list|)
decl_stmt|;
name|StoredNode
name|ourRoot
init|=
name|store
operator|.
name|getNode
argument_list|(
name|rootNodeId
argument_list|)
decl_stmt|;
name|rootNodeId
operator|=
name|mergeTree
argument_list|(
name|baseRoot
argument_list|,
name|ourRoot
argument_list|,
name|theirRoot
argument_list|)
expr_stmt|;
name|baseRevId
operator|=
name|currentHead
expr_stmt|;
block|}
if|if
condition|(
name|store
operator|.
name|getCommit
argument_list|(
name|currentHead
argument_list|)
operator|.
name|getRootNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|rootNodeId
argument_list|)
condition|)
block|{
comment|// the commit didn't cause any changes,
comment|// no need to create new commit object/update head revision
return|return
name|currentHead
return|;
block|}
name|MutableCommit
name|newCommit
init|=
operator|new
name|MutableCommit
argument_list|()
decl_stmt|;
name|newCommit
operator|.
name|setParentId
argument_list|(
name|baseRevId
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setCommitTS
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setMsg
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|newCommit
operator|.
name|setRootNodeId
argument_list|(
name|rootNodeId
argument_list|)
expr_stmt|;
name|newRevId
operator|=
name|store
operator|.
name|putHeadCommit
argument_list|(
name|newCommit
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|store
operator|.
name|unlockHead
argument_list|()
expr_stmt|;
block|}
comment|// reset instance in order to be reusable
name|staged
operator|.
name|clear
argument_list|()
expr_stmt|;
name|changeLog
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|newRevId
return|;
block|}
name|MutableNode
name|getOrCreateStagedNode
parameter_list|(
name|String
name|nodePath
parameter_list|)
throws|throws
name|Exception
block|{
name|MutableNode
name|node
init|=
name|staged
operator|.
name|get
argument_list|(
name|nodePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
name|MutableNode
name|parent
init|=
name|staged
operator|.
name|get
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
name|parent
operator|=
operator|new
name|MutableNode
argument_list|(
name|store
operator|.
name|getRootNode
argument_list|(
name|baseRevId
argument_list|)
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|staged
operator|.
name|put
argument_list|(
literal|"/"
argument_list|,
name|parent
argument_list|)
expr_stmt|;
block|}
name|node
operator|=
name|parent
expr_stmt|;
name|String
name|names
index|[]
init|=
name|PathUtils
operator|.
name|split
argument_list|(
name|nodePath
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|names
operator|.
name|length
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
name|String
name|path
init|=
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|nodePath
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|node
operator|=
name|staged
operator|.
name|get
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
comment|// not yet staged, resolve id using staged parent
comment|// to allow for staged move operations
name|ChildNode
name|cne
init|=
name|parent
operator|.
name|getChildNodeEntry
argument_list|(
name|names
index|[
name|names
operator|.
name|length
operator|-
name|i
operator|-
literal|1
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|cne
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|nodePath
argument_list|)
throw|;
block|}
name|node
operator|=
operator|new
name|MutableNode
argument_list|(
name|store
operator|.
name|getNode
argument_list|(
name|cne
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|staged
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
name|parent
operator|=
name|node
expr_stmt|;
block|}
block|}
return|return
name|node
return|;
block|}
name|void
name|moveStagedNodes
parameter_list|(
name|String
name|srcPath
parameter_list|,
name|String
name|destPath
parameter_list|)
throws|throws
name|Exception
block|{
name|MutableNode
name|node
init|=
name|staged
operator|.
name|get
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|staged
operator|.
name|remove
argument_list|(
name|srcPath
argument_list|)
expr_stmt|;
name|staged
operator|.
name|put
argument_list|(
name|destPath
argument_list|,
name|node
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|node
operator|.
name|getChildNodeNames
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
name|String
name|childName
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|moveStagedNodes
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|srcPath
argument_list|,
name|childName
argument_list|)
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|destPath
argument_list|,
name|childName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|void
name|removeStagedNodes
parameter_list|(
name|String
name|nodePath
parameter_list|)
throws|throws
name|Exception
block|{
name|MutableNode
name|node
init|=
name|staged
operator|.
name|get
argument_list|(
name|nodePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|staged
operator|.
name|remove
argument_list|(
name|nodePath
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|node
operator|.
name|getChildNodeNames
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
name|String
name|childName
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|removeStagedNodes
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|nodePath
argument_list|,
name|childName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Id
comment|/* new id of root node */
name|persistStagedNodes
parameter_list|()
throws|throws
name|Exception
block|{
comment|// sort paths in in depth-descending order
name|ArrayList
argument_list|<
name|String
argument_list|>
name|orderedPaths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|staged
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|orderedPaths
argument_list|,
operator|new
name|Comparator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|String
name|path1
parameter_list|,
name|String
name|path2
parameter_list|)
block|{
comment|// paths should be ordered by depth, descending
name|int
name|result
init|=
name|getDepth
argument_list|(
name|path2
argument_list|)
operator|-
name|getDepth
argument_list|(
name|path1
argument_list|)
decl_stmt|;
return|return
operator|(
name|result
operator|!=
literal|0
operator|)
condition|?
name|result
else|:
literal|1
return|;
block|}
name|int
name|getDepth
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// iterate over staged entries in depth-descending order
name|Id
name|rootNodeId
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|orderedPaths
control|)
block|{
comment|// persist node
name|Id
name|id
init|=
name|store
operator|.
name|putNode
argument_list|(
name|staged
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|rootNodeId
operator|=
name|id
expr_stmt|;
block|}
else|else
block|{
name|staged
operator|.
name|get
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|ChildNode
argument_list|(
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|rootNodeId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"internal error: inconsistent staging area content"
argument_list|)
throw|;
block|}
return|return
name|rootNodeId
return|;
block|}
comment|/**      * Performs a three-way merge of the trees rooted at<code>ourRoot</code>,      *<code>theirRoot</code>, using the tree at<code>baseRoot</code> as reference.      *      * @param baseRoot      * @param ourRoot      * @param theirRoot      * @return id of merged root node      * @throws Exception      */
name|Id
comment|/* id of merged root node */
name|mergeTree
parameter_list|(
name|StoredNode
name|baseRoot
parameter_list|,
name|StoredNode
name|ourRoot
parameter_list|,
name|StoredNode
name|theirRoot
parameter_list|)
throws|throws
name|Exception
block|{
comment|// as we're going to use the staging area for the merge process,
comment|// we need to clear it first
name|staged
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// recursively merge 'our' changes with 'their' changes...
name|mergeNode
argument_list|(
name|baseRoot
argument_list|,
name|ourRoot
argument_list|,
name|theirRoot
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
return|return
name|persistStagedNodes
argument_list|()
return|;
block|}
name|void
name|mergeNode
parameter_list|(
name|StoredNode
name|baseNode
parameter_list|,
name|StoredNode
name|ourNode
parameter_list|,
name|StoredNode
name|theirNode
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|NodeDelta
name|theirChanges
init|=
operator|new
name|NodeDelta
argument_list|(
name|store
argument_list|,
name|store
operator|.
name|getNodeState
argument_list|(
name|baseNode
argument_list|)
argument_list|,
name|store
operator|.
name|getNodeState
argument_list|(
name|theirNode
argument_list|)
argument_list|)
decl_stmt|;
name|NodeDelta
name|ourChanges
init|=
operator|new
name|NodeDelta
argument_list|(
name|store
argument_list|,
name|store
operator|.
name|getNodeState
argument_list|(
name|baseNode
argument_list|)
argument_list|,
name|store
operator|.
name|getNodeState
argument_list|(
name|ourNode
argument_list|)
argument_list|)
decl_stmt|;
comment|// merge non-conflicting changes
name|MutableNode
name|mergedNode
init|=
operator|new
name|MutableNode
argument_list|(
name|theirNode
argument_list|,
name|store
argument_list|)
decl_stmt|;
name|staged
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|mergedNode
argument_list|)
expr_stmt|;
name|mergedNode
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
name|mergedNode
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
name|mergedNode
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
name|Id
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
name|mergedNode
operator|.
name|add
argument_list|(
operator|new
name|ChildNode
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
name|Id
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
name|mergedNode
operator|.
name|add
argument_list|(
operator|new
name|ChildNode
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
argument_list|)
expr_stmt|;
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
name|mergedNode
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|NodeDelta
operator|.
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
name|NodeDelta
operator|.
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
name|StoredNode
name|baseChild
init|=
name|store
operator|.
name|getNode
argument_list|(
name|baseNode
operator|.
name|getChildNodeEntry
argument_list|(
name|conflictName
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|StoredNode
name|ourChild
init|=
name|store
operator|.
name|getNode
argument_list|(
name|ourNode
operator|.
name|getChildNodeEntry
argument_list|(
name|conflictName
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|StoredNode
name|theirChild
init|=
name|store
operator|.
name|getNode
argument_list|(
name|theirNode
operator|.
name|getChildNodeEntry
argument_list|(
name|conflictName
argument_list|)
operator|.
name|getId
argument_list|()
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
name|mergedNode
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
name|mergedNode
operator|.
name|remove
argument_list|(
name|conflictName
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|//--------------------------------------------------------< inner classes>
specifier|abstract
class|class
name|Change
block|{
specifier|abstract
name|void
name|apply
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
class|class
name|AddNode
extends|extends
name|Change
block|{
name|String
name|parentNodePath
decl_stmt|;
name|String
name|nodeName
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
decl_stmt|;
name|AddNode
parameter_list|(
name|String
name|parentNodePath
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|)
block|{
name|this
operator|.
name|parentNodePath
operator|=
name|parentNodePath
expr_stmt|;
name|this
operator|.
name|nodeName
operator|=
name|nodeName
expr_stmt|;
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
block|}
name|void
name|apply
parameter_list|()
throws|throws
name|Exception
block|{
name|addNode
argument_list|(
name|parentNodePath
argument_list|,
name|nodeName
argument_list|,
name|properties
argument_list|)
expr_stmt|;
block|}
block|}
class|class
name|RemoveNode
extends|extends
name|Change
block|{
name|String
name|nodePath
decl_stmt|;
name|RemoveNode
parameter_list|(
name|String
name|nodePath
parameter_list|)
block|{
name|this
operator|.
name|nodePath
operator|=
name|nodePath
expr_stmt|;
block|}
name|void
name|apply
parameter_list|()
throws|throws
name|Exception
block|{
name|removeNode
argument_list|(
name|nodePath
argument_list|)
expr_stmt|;
block|}
block|}
class|class
name|MoveNode
extends|extends
name|Change
block|{
name|String
name|srcPath
decl_stmt|;
name|String
name|destPath
decl_stmt|;
name|MoveNode
parameter_list|(
name|String
name|srcPath
parameter_list|,
name|String
name|destPath
parameter_list|)
block|{
name|this
operator|.
name|srcPath
operator|=
name|srcPath
expr_stmt|;
name|this
operator|.
name|destPath
operator|=
name|destPath
expr_stmt|;
block|}
name|void
name|apply
parameter_list|()
throws|throws
name|Exception
block|{
name|moveNode
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
block|}
block|}
class|class
name|CopyNode
extends|extends
name|Change
block|{
name|String
name|srcPath
decl_stmt|;
name|String
name|destPath
decl_stmt|;
name|CopyNode
parameter_list|(
name|String
name|srcPath
parameter_list|,
name|String
name|destPath
parameter_list|)
block|{
name|this
operator|.
name|srcPath
operator|=
name|srcPath
expr_stmt|;
name|this
operator|.
name|destPath
operator|=
name|destPath
expr_stmt|;
block|}
name|void
name|apply
parameter_list|()
throws|throws
name|Exception
block|{
name|copyNode
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
block|}
block|}
class|class
name|SetProperty
extends|extends
name|Change
block|{
name|String
name|nodePath
decl_stmt|;
name|String
name|propName
decl_stmt|;
name|String
name|propValue
decl_stmt|;
name|SetProperty
parameter_list|(
name|String
name|nodePath
parameter_list|,
name|String
name|propName
parameter_list|,
name|String
name|propValue
parameter_list|)
block|{
name|this
operator|.
name|nodePath
operator|=
name|nodePath
expr_stmt|;
name|this
operator|.
name|propName
operator|=
name|propName
expr_stmt|;
name|this
operator|.
name|propValue
operator|=
name|propValue
expr_stmt|;
block|}
name|void
name|apply
parameter_list|()
throws|throws
name|Exception
block|{
name|setProperty
argument_list|(
name|nodePath
argument_list|,
name|propName
argument_list|,
name|propValue
argument_list|)
expr_stmt|;
block|}
block|}
class|class
name|SetProperties
extends|extends
name|Change
block|{
name|String
name|nodePath
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
decl_stmt|;
name|SetProperties
parameter_list|(
name|String
name|nodePath
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|)
block|{
name|this
operator|.
name|nodePath
operator|=
name|nodePath
expr_stmt|;
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
block|}
name|void
name|apply
parameter_list|()
throws|throws
name|Exception
block|{
name|setProperties
argument_list|(
name|nodePath
argument_list|,
name|properties
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

