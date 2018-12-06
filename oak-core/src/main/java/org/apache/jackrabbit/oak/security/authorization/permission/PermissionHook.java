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
name|oak
operator|.
name|security
operator|.
name|authorization
operator|.
name|permission
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
name|oak
operator|.
name|plugins
operator|.
name|nodetype
operator|.
name|TypePredicate
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
name|plugins
operator|.
name|tree
operator|.
name|RootProvider
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
name|plugins
operator|.
name|tree
operator|.
name|TreeProvider
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
name|spi
operator|.
name|commit
operator|.
name|CommitInfo
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
name|spi
operator|.
name|commit
operator|.
name|PostValidationHook
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
name|spi
operator|.
name|mount
operator|.
name|Mount
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
name|spi
operator|.
name|mount
operator|.
name|MountInfoProvider
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|accesscontrol
operator|.
name|AccessControlConstants
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|PermissionConstants
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionProvider
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
name|spi
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeBitsProvider
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
name|spi
operator|.
name|state
operator|.
name|DefaultNodeStateDiff
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
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
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
name|spi
operator|.
name|state
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
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeStateUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_SYSTEM
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_comment
comment|/**  * {@code CommitHook} implementation that processes any modification made to  * access control content and updates persisted permission store associated  * with access control related data stored in the repository.  *<p>  * The permission entries are grouped by principal and stored below the store root based on the hash value of the  * access controllable path. hash collisions are handled by adding subnodes accordingly.  *<pre>  *   /jcr:system/rep:permissionStore/workspace-name  *      /everyone  *          /552423  [rep:PermissionStore]  *              /0     [rep:Permissions]  *              /1     [rep:Permissions]  *              /c0     [rep:PermissionStore]  *                  /0      [rep:Permissions]  *                  /1      [rep:Permissions]  *                  /2      [rep:Permissions]  *              /c1     [rep:PermissionStore]  *                  /0      [rep:Permissions]  *                  /1      [rep:Permissions]  *                  /2      [rep:Permissions]  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|PermissionHook
implements|implements
name|PostValidationHook
implements|,
name|AccessControlConstants
implements|,
name|PermissionConstants
block|{
specifier|private
specifier|final
name|RestrictionProvider
name|restrictionProvider
decl_stmt|;
specifier|private
specifier|final
name|String
name|workspaceName
decl_stmt|;
specifier|private
specifier|final
name|MountInfoProvider
name|mountInfoProvider
decl_stmt|;
specifier|private
specifier|final
name|RootProvider
name|rootProvider
decl_stmt|;
specifier|private
specifier|final
name|TreeProvider
name|treeProvider
decl_stmt|;
specifier|private
name|NodeBuilder
name|permissionStore
decl_stmt|;
specifier|private
name|PrivilegeBitsProvider
name|bitsProvider
decl_stmt|;
specifier|private
name|TypePredicate
name|isACL
decl_stmt|;
specifier|private
name|TypePredicate
name|isACE
decl_stmt|;
specifier|private
name|TypePredicate
name|isGrantACE
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|PermissionStoreEditor
argument_list|>
name|modified
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|PermissionStoreEditor
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|PermissionStoreEditor
argument_list|>
name|deleted
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|PermissionStoreEditor
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|PermissionHook
parameter_list|(
annotation|@
name|NotNull
name|String
name|workspaceName
parameter_list|,
annotation|@
name|NotNull
name|RestrictionProvider
name|restrictionProvider
parameter_list|,
annotation|@
name|NotNull
name|MountInfoProvider
name|mountInfoProvider
parameter_list|,
annotation|@
name|NotNull
name|RootProvider
name|rootProvider
parameter_list|,
annotation|@
name|NotNull
name|TreeProvider
name|treeProvider
parameter_list|)
block|{
name|this
operator|.
name|workspaceName
operator|=
name|workspaceName
expr_stmt|;
name|this
operator|.
name|restrictionProvider
operator|=
name|restrictionProvider
expr_stmt|;
name|this
operator|.
name|mountInfoProvider
operator|=
name|mountInfoProvider
expr_stmt|;
name|this
operator|.
name|rootProvider
operator|=
name|rootProvider
expr_stmt|;
name|this
operator|.
name|treeProvider
operator|=
name|treeProvider
expr_stmt|;
block|}
comment|//---------------------------------------------------------< CommitHook>---
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|NodeState
name|processCommit
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
name|NodeBuilder
name|rootAfter
init|=
name|after
operator|.
name|builder
argument_list|()
decl_stmt|;
name|permissionStore
operator|=
name|getPermissionStore
argument_list|(
name|rootAfter
argument_list|)
expr_stmt|;
name|bitsProvider
operator|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|rootProvider
operator|.
name|createReadOnlyRoot
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
name|isACL
operator|=
operator|new
name|TypePredicate
argument_list|(
name|after
argument_list|,
name|NT_REP_ACL
argument_list|)
expr_stmt|;
name|isACE
operator|=
operator|new
name|TypePredicate
argument_list|(
name|after
argument_list|,
name|NT_REP_ACE
argument_list|)
expr_stmt|;
name|isGrantACE
operator|=
operator|new
name|TypePredicate
argument_list|(
name|after
argument_list|,
name|NT_REP_GRANT_ACE
argument_list|)
expr_stmt|;
name|Diff
name|diff
init|=
operator|new
name|Diff
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|apply
argument_list|()
expr_stmt|;
return|return
name|rootAfter
operator|.
name|getNodeState
argument_list|()
return|;
block|}
comment|//-------------------------------------------------------------< Object>---
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PermissionHook"
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|void
name|apply
parameter_list|()
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|PermissionStoreEditor
argument_list|>
name|entry
range|:
name|deleted
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|removePermissionEntries
argument_list|()
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
name|PermissionStoreEditor
argument_list|>
name|entry
range|:
name|modified
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|updatePermissionEntries
argument_list|()
expr_stmt|;
block|}
name|modified
operator|.
name|clear
argument_list|()
expr_stmt|;
name|deleted
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|NotNull
specifier|private
specifier|static
name|NodeBuilder
name|getPermissionStore
parameter_list|(
name|NodeBuilder
name|rootBuilder
parameter_list|)
block|{
comment|// permission root has been created during workspace initialization
return|return
name|rootBuilder
operator|.
name|getChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|REP_PERMISSION_STORE
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|NodeBuilder
name|getPermissionRoot
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|Mount
name|m
init|=
name|mountInfoProvider
operator|.
name|getMountByPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|permissionStore
operator|.
name|getChildNode
argument_list|(
name|MountPermissionProvider
operator|.
name|getPermissionRootName
argument_list|(
name|m
argument_list|,
name|workspaceName
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|final
class|class
name|Diff
extends|extends
name|DefaultNodeStateDiff
block|{
specifier|private
specifier|final
name|String
name|parentPath
decl_stmt|;
specifier|private
name|Diff
parameter_list|(
name|String
name|parentPath
parameter_list|)
block|{
name|this
operator|.
name|parentPath
operator|=
name|parentPath
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// ignore hidden nodes
return|return
literal|true
return|;
block|}
name|String
name|path
init|=
name|parentPath
operator|+
literal|'/'
operator|+
name|name
decl_stmt|;
if|if
condition|(
name|isACL
operator|.
name|apply
argument_list|(
name|after
argument_list|)
condition|)
block|{
name|PermissionStoreEditor
name|psEditor
init|=
name|createPermissionStoreEditor
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|modified
operator|.
name|put
argument_list|(
name|psEditor
operator|.
name|getPath
argument_list|()
argument_list|,
name|psEditor
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|EMPTY_NODE
argument_list|,
operator|new
name|Diff
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// ignore hidden nodes
return|return
literal|true
return|;
block|}
name|String
name|path
init|=
name|parentPath
operator|+
literal|'/'
operator|+
name|name
decl_stmt|;
if|if
condition|(
name|isACL
operator|.
name|apply
argument_list|(
name|before
argument_list|)
condition|)
block|{
if|if
condition|(
name|isACL
operator|.
name|apply
argument_list|(
name|after
argument_list|)
condition|)
block|{
name|PermissionStoreEditor
name|psEditor
init|=
name|createPermissionStoreEditor
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|modified
operator|.
name|put
argument_list|(
name|psEditor
operator|.
name|getPath
argument_list|()
argument_list|,
name|psEditor
argument_list|)
expr_stmt|;
comment|// also consider to remove the ACL from removed entries of other principals
name|PermissionStoreEditor
name|beforeEditor
init|=
name|createPermissionStoreEditor
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|beforeEditor
operator|.
name|removePermissionEntries
argument_list|(
name|psEditor
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|beforeEditor
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|deleted
operator|.
name|put
argument_list|(
name|parentPath
argument_list|,
name|beforeEditor
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|PermissionStoreEditor
name|psEditor
init|=
name|createPermissionStoreEditor
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|deleted
operator|.
name|put
argument_list|(
name|psEditor
operator|.
name|getPath
argument_list|()
argument_list|,
name|psEditor
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|isACL
operator|.
name|apply
argument_list|(
name|after
argument_list|)
condition|)
block|{
name|PermissionStoreEditor
name|psEditor
init|=
name|createPermissionStoreEditor
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|modified
operator|.
name|put
argument_list|(
name|psEditor
operator|.
name|getPath
argument_list|()
argument_list|,
name|psEditor
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|Diff
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// ignore hidden nodes
return|return
literal|true
return|;
block|}
name|String
name|path
init|=
name|parentPath
operator|+
literal|'/'
operator|+
name|name
decl_stmt|;
if|if
condition|(
name|isACL
operator|.
name|apply
argument_list|(
name|before
argument_list|)
condition|)
block|{
name|PermissionStoreEditor
name|psEditor
init|=
name|createPermissionStoreEditor
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|deleted
operator|.
name|put
argument_list|(
name|psEditor
operator|.
name|getPath
argument_list|()
argument_list|,
name|psEditor
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|EMPTY_NODE
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|Diff
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|PermissionStoreEditor
name|createPermissionStoreEditor
parameter_list|(
annotation|@
name|NotNull
name|String
name|nodeName
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|nodeState
parameter_list|)
block|{
return|return
operator|new
name|PermissionStoreEditor
argument_list|(
name|parentPath
argument_list|,
name|nodeName
argument_list|,
name|nodeState
argument_list|,
name|getPermissionRoot
argument_list|(
name|parentPath
argument_list|)
argument_list|,
name|isACE
argument_list|,
name|isGrantACE
argument_list|,
name|bitsProvider
argument_list|,
name|restrictionProvider
argument_list|,
name|treeProvider
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

