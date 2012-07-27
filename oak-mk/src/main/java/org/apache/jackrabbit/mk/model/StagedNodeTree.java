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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|json
operator|.
name|JsonObject
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
name|NodeDelta
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
name|oak
operator|.
name|commons
operator|.
name|PathUtils
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

begin_comment
comment|/**  * A {@code StagedNodeTree} provides methods to manipulate a specific revision  * of the tree. The changes are recorded and can be persisted by calling  {@link #persist(RevisionStore.PutToken)}.  */
end_comment

begin_class
specifier|public
class|class
name|StagedNodeTree
block|{
specifier|private
specifier|final
name|RevisionStore
name|store
decl_stmt|;
specifier|private
name|StagedNode
name|root
decl_stmt|;
specifier|private
name|Id
name|baseRevisionId
decl_stmt|;
comment|/**      * Creates a new {@code StagedNodeTree} instance.      *      * @param store revision store used to read from and persist changes      * @param baseRevisionId id of revision the changes should be based upon      */
specifier|public
name|StagedNodeTree
parameter_list|(
name|RevisionStore
name|store
parameter_list|,
name|Id
name|baseRevisionId
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|baseRevisionId
operator|=
name|baseRevisionId
expr_stmt|;
block|}
comment|/**      * Discards all staged changes and resets the base revision to the      * specified new revision id.      *      * @param newBaseRevisionId id of revision the changes should be based upon      */
specifier|public
name|void
name|reset
parameter_list|(
name|Id
name|newBaseRevisionId
parameter_list|)
block|{
name|root
operator|=
literal|null
expr_stmt|;
name|baseRevisionId
operator|=
name|newBaseRevisionId
expr_stmt|;
block|}
comment|/**      * Returns {@code true} if there are no staged changes, otherwise returns {@code false}.      * @return {@code true} if there are no staged changes, otherwise returns {@code false}.      */
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|root
operator|==
literal|null
return|;
block|}
comment|/**      * Persists the staged nodes and returns the {@code Id} of new root node.      *      * @param token      * @return {@code Id} of new root node      * @throws Exception      */
specifier|public
name|Id
comment|/* new id of root node */
name|persist
parameter_list|(
name|RevisionStore
operator|.
name|PutToken
name|token
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|root
operator|!=
literal|null
condition|?
name|root
operator|.
name|persist
argument_list|(
name|token
argument_list|)
else|:
literal|null
return|;
block|}
comment|/**      * Performs a three-way merge merging<i>our</i> tree (rooted at {@code ourRoot})      * and<i>their</i> tree (identified by {@code newBaseRevisionId}),      * using the common ancestor revision {@code commonAncestorRevisionId} as      * base reference.      *<p/>      * This instance will be initially reset to {@code newBaseRevisionId}, discarding      * all currently staged changes.      *      * @param ourRoot      * @param newBaseRevisionId      * @param commonAncestorRevisionId      * @param token      * @return {@code Id} of new root node      * @throws Exception      */
specifier|public
name|Id
comment|/* new id of merged root node */
name|merge
parameter_list|(
name|StoredNode
name|ourRoot
parameter_list|,
name|Id
name|newBaseRevisionId
parameter_list|,
name|Id
name|commonAncestorRevisionId
parameter_list|,
name|RevisionStore
operator|.
name|PutToken
name|token
parameter_list|)
throws|throws
name|Exception
block|{
comment|// reset staging area to new base revision
name|reset
argument_list|(
name|newBaseRevisionId
argument_list|)
expr_stmt|;
name|StoredNode
name|baseRoot
init|=
name|store
operator|.
name|getRootNode
argument_list|(
name|commonAncestorRevisionId
argument_list|)
decl_stmt|;
name|StoredNode
name|theirRoot
init|=
name|store
operator|.
name|getRootNode
argument_list|(
name|newBaseRevisionId
argument_list|)
decl_stmt|;
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
comment|// persist staged nodes
return|return
name|persist
argument_list|(
name|token
argument_list|)
return|;
block|}
comment|//-----------------------------------------< tree manipulation operations>
specifier|public
name|void
name|add
parameter_list|(
name|String
name|parentNodePath
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|JsonObject
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|StagedNode
name|parent
init|=
name|getStagedNode
argument_list|(
name|parentNodePath
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
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
name|parent
operator|.
name|add
argument_list|(
name|nodeName
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|nodePath
parameter_list|)
throws|throws
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
name|StagedNode
name|parent
init|=
name|getStagedNode
argument_list|(
name|parentPath
argument_list|,
literal|true
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
comment|// discard any staged changes at nodePath
name|unstageNode
argument_list|(
name|nodePath
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
name|StagedNode
name|node
init|=
name|getStagedNode
argument_list|(
name|nodePath
argument_list|,
literal|true
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
block|}
specifier|public
name|void
name|move
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
name|StagedNode
name|srcParent
init|=
name|getStagedNode
argument_list|(
name|srcParentPath
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcParent
operator|.
name|getChildNodeEntry
argument_list|(
name|srcNodeName
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
name|StagedNode
name|destParent
init|=
name|getStagedNode
argument_list|(
name|destParentPath
argument_list|,
literal|true
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
comment|// rename
name|srcParent
operator|.
name|rename
argument_list|(
name|srcNodeName
argument_list|,
name|destNodeName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// move
name|srcParent
operator|.
name|move
argument_list|(
name|srcNodeName
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|copy
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
name|StagedNode
name|srcParent
init|=
name|getStagedNode
argument_list|(
name|srcParentPath
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcParent
operator|==
literal|null
condition|)
block|{
comment|// the subtree to be copied has not been modified
name|ChildNodeEntry
name|entry
init|=
name|getStoredNode
argument_list|(
name|srcParentPath
argument_list|)
operator|.
name|getChildNodeEntry
argument_list|(
name|srcNodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
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
name|StagedNode
name|destParent
init|=
name|getStagedNode
argument_list|(
name|destParentPath
argument_list|,
literal|true
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
literal|"node already exists at copy destination path: "
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
name|ChildNodeEntry
argument_list|(
name|destNodeName
argument_list|,
name|entry
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|ChildNodeEntry
name|srcEntry
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
name|srcEntry
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
name|StagedNode
name|destParent
init|=
name|getStagedNode
argument_list|(
name|destParentPath
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|StagedNode
name|srcNode
init|=
name|getStagedNode
argument_list|(
name|srcPath
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcNode
operator|!=
literal|null
condition|)
block|{
comment|// copy the modified subtree
name|destParent
operator|.
name|add
argument_list|(
name|destNodeName
argument_list|,
name|srcNode
operator|.
name|copy
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|destParent
operator|.
name|add
argument_list|(
operator|new
name|ChildNodeEntry
argument_list|(
name|destNodeName
argument_list|,
name|srcEntry
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|//-------------------------------------------------------< implementation>
specifier|private
name|StagedNode
name|getStagedNode
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|createIfNotStaged
parameter_list|)
throws|throws
name|Exception
block|{
assert|assert
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|path
argument_list|)
assert|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|createIfNotStaged
condition|)
block|{
return|return
literal|null
return|;
block|}
name|root
operator|=
operator|new
name|StagedNode
argument_list|(
name|store
operator|.
name|getRootNode
argument_list|(
name|baseRevisionId
argument_list|)
argument_list|,
name|store
argument_list|)
expr_stmt|;
block|}
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
return|return
name|root
return|;
block|}
name|StagedNode
name|parent
init|=
name|root
decl_stmt|,
name|node
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|node
operator|=
name|parent
operator|.
name|getStagedChildNode
argument_list|(
name|name
argument_list|,
name|createIfNotStaged
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|parent
operator|=
name|node
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
specifier|private
name|StagedNode
name|unstageNode
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
assert|assert
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|path
argument_list|)
assert|;
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
name|StagedNode
name|unstaged
init|=
name|root
decl_stmt|;
name|root
operator|=
literal|null
expr_stmt|;
return|return
name|unstaged
return|;
block|}
name|String
name|parentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|StagedNode
name|parent
init|=
name|getStagedNode
argument_list|(
name|parentPath
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|parent
operator|.
name|unstageChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|private
name|StoredNode
name|getStoredNode
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
assert|assert
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|path
argument_list|)
assert|;
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
return|return
name|store
operator|.
name|getRootNode
argument_list|(
name|baseRevisionId
argument_list|)
return|;
block|}
name|StoredNode
name|parent
init|=
name|store
operator|.
name|getRootNode
argument_list|(
name|baseRevisionId
argument_list|)
decl_stmt|,
name|node
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|ChildNodeEntry
name|entry
init|=
name|parent
operator|.
name|getChildNodeEntry
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|path
argument_list|)
throw|;
block|}
name|node
operator|=
name|store
operator|.
name|getNode
argument_list|(
name|entry
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|path
argument_list|)
throw|;
block|}
name|parent
operator|=
name|node
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
comment|/**      * Performs a three-way merge of the trees rooted at {@code ourRoot},      * {@code theirRoot}, using the tree at {@code baseRoot} as reference.      */
specifier|private
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
name|StagedNode
name|stagedNode
init|=
name|getStagedNode
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// merge non-conflicting changes
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
name|stagedNode
operator|.
name|add
argument_list|(
operator|new
name|ChildNodeEntry
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
name|stagedNode
operator|.
name|add
argument_list|(
operator|new
name|ChildNodeEntry
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
name|stagedNode
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
name|stagedNode
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
specifier|private
class|class
name|StagedNode
extends|extends
name|MutableNode
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|StagedNode
argument_list|>
name|stagedChildNodes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|StagedNode
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|StagedNode
parameter_list|(
name|RevisionStore
name|store
parameter_list|)
block|{
name|super
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
specifier|private
name|StagedNode
parameter_list|(
name|Node
name|base
parameter_list|,
name|RevisionStore
name|store
parameter_list|)
block|{
name|super
argument_list|(
name|base
argument_list|,
name|store
argument_list|)
expr_stmt|;
block|}
comment|/**          * Returns a {@code StagedNode} representation of the given child node.          * If a {@code StagedNode} representation doesn't exist yet a new          * {@code StagedNode} instance will be returned if {@code createIfNotStaged == true},          * otherwise {@code null} will be returned.          *<p/>          * A {@code NotFoundException} will be thrown if there's no child node          * with the given name.          *          * @param name child node name          * @param createIfNotStaged flag controlling whether a new {@code StagedNode}          *                          instance should be created on demand          * @return a {@code StagedNode} instance or null if there's no {@code StagedNode}          *         representation of the given child node and {@code createIfNotStaged == false}          * @throws NotFoundException if there's no child node with the given name          * @throws Exception if another error occurs          */
name|StagedNode
name|getStagedChildNode
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|createIfNotStaged
parameter_list|)
throws|throws
name|Exception
block|{
name|StagedNode
name|child
init|=
name|stagedChildNodes
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|==
literal|null
condition|)
block|{
name|ChildNodeEntry
name|entry
init|=
name|getChildNodeEntry
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|createIfNotStaged
condition|)
block|{
name|child
operator|=
operator|new
name|StagedNode
argument_list|(
name|store
operator|.
name|getNode
argument_list|(
name|entry
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|stagedChildNodes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|name
argument_list|)
throw|;
block|}
block|}
return|return
name|child
return|;
block|}
name|StagedNode
name|unstageChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|stagedChildNodes
operator|.
name|remove
argument_list|(
name|name
argument_list|)
return|;
block|}
name|StagedNode
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|StagedNode
name|node
parameter_list|)
block|{
name|stagedChildNodes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|node
argument_list|)
expr_stmt|;
comment|// child id will be computed on persist
name|add
argument_list|(
operator|new
name|ChildNodeEntry
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
name|StagedNode
name|copy
parameter_list|()
block|{
name|StagedNode
name|copy
init|=
operator|new
name|StagedNode
argument_list|(
name|this
argument_list|,
name|store
argument_list|)
decl_stmt|;
comment|// recursively copy staged child nodes
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|StagedNode
argument_list|>
name|entry
range|:
name|stagedChildNodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|copy
operator|.
name|add
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
operator|.
name|copy
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|copy
return|;
block|}
name|StagedNode
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|JsonObject
name|obj
parameter_list|)
block|{
name|StagedNode
name|node
init|=
operator|new
name|StagedNode
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|node
operator|.
name|getProperties
argument_list|()
operator|.
name|putAll
argument_list|(
name|obj
operator|.
name|getProperties
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
name|JsonObject
argument_list|>
name|entry
range|:
name|obj
operator|.
name|getChildren
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|node
operator|.
name|add
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
name|stagedChildNodes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|node
argument_list|)
expr_stmt|;
comment|// child id will be computed on persist
name|add
argument_list|(
operator|new
name|ChildNodeEntry
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
name|void
name|move
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|destPath
parameter_list|)
throws|throws
name|Exception
block|{
name|ChildNodeEntry
name|srcEntry
init|=
name|getChildNodeEntry
argument_list|(
name|name
argument_list|)
decl_stmt|;
assert|assert
name|srcEntry
operator|!=
literal|null
assert|;
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
name|destName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
name|StagedNode
name|destParent
init|=
name|getStagedNode
argument_list|(
name|destParentPath
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|StagedNode
name|target
init|=
name|stagedChildNodes
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|destParent
operator|.
name|add
argument_list|(
operator|new
name|ChildNodeEntry
argument_list|(
name|destName
argument_list|,
name|srcEntry
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|target
operator|!=
literal|null
condition|)
block|{
comment|// move staged child node
name|destParent
operator|.
name|add
argument_list|(
name|destName
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|ChildNodeEntry
name|remove
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|stagedChildNodes
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|remove
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ChildNodeEntry
name|rename
parameter_list|(
name|String
name|oldName
parameter_list|,
name|String
name|newName
parameter_list|)
block|{
name|StagedNode
name|child
init|=
name|stagedChildNodes
operator|.
name|remove
argument_list|(
name|oldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
name|stagedChildNodes
operator|.
name|put
argument_list|(
name|newName
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|rename
argument_list|(
name|oldName
argument_list|,
name|newName
argument_list|)
return|;
block|}
name|Id
name|persist
parameter_list|(
name|RevisionStore
operator|.
name|PutToken
name|token
parameter_list|)
throws|throws
name|Exception
block|{
comment|// recursively persist staged nodes
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|StagedNode
argument_list|>
name|entry
range|:
name|stagedChildNodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|StagedNode
name|childNode
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// todo decide whether to inline/store child node separately based on some filter criteria
name|Id
name|id
init|=
name|childNode
operator|.
name|persist
argument_list|(
name|token
argument_list|)
decl_stmt|;
comment|// update child node entry
name|add
argument_list|(
operator|new
name|ChildNodeEntry
argument_list|(
name|name
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// persist this node
return|return
name|store
operator|.
name|putNode
argument_list|(
name|token
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

