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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|api
operator|.
name|CommitFailedException
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
name|api
operator|.
name|Tree
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
name|core
operator|.
name|ImmutableRoot
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
name|ImmutableTree
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
name|EditorDiff
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
name|MoveTracker
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
name|Validator
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
name|VisibleValidator
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
name|PermissionProvider
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
name|Permissions
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
name|TreePermission
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
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|CommitFailedException
operator|.
name|ACCESS
import|;
end_import

begin_comment
comment|/**  * MoveAwarePermissionValidator... TODO  */
end_comment

begin_class
specifier|public
class|class
name|MoveAwarePermissionValidator
extends|extends
name|PermissionValidator
block|{
specifier|private
specifier|final
name|MoveContext
name|moveCtx
decl_stmt|;
name|MoveAwarePermissionValidator
parameter_list|(
annotation|@
name|Nonnull
name|ImmutableTree
name|rootBefore
parameter_list|,
annotation|@
name|Nonnull
name|ImmutableTree
name|rootAfter
parameter_list|,
annotation|@
name|Nonnull
name|PermissionProvider
name|permissionProvider
parameter_list|,
annotation|@
name|Nonnull
name|PermissionValidatorProvider
name|provider
parameter_list|,
annotation|@
name|Nonnull
name|MoveTracker
name|moveTracker
parameter_list|)
block|{
name|super
argument_list|(
name|rootBefore
argument_list|,
name|rootAfter
argument_list|,
name|permissionProvider
argument_list|,
name|provider
argument_list|)
expr_stmt|;
name|moveCtx
operator|=
operator|new
name|MoveContext
argument_list|(
name|moveTracker
argument_list|,
name|rootBefore
argument_list|,
name|rootAfter
argument_list|)
expr_stmt|;
block|}
name|MoveAwarePermissionValidator
parameter_list|(
annotation|@
name|Nullable
name|ImmutableTree
name|parentBefore
parameter_list|,
annotation|@
name|Nullable
name|ImmutableTree
name|parentAfter
parameter_list|,
annotation|@
name|Nonnull
name|TreePermission
name|parentPermission
parameter_list|,
annotation|@
name|Nonnull
name|PermissionValidator
name|parentValidator
parameter_list|)
block|{
name|super
argument_list|(
name|parentBefore
argument_list|,
name|parentAfter
argument_list|,
name|parentPermission
argument_list|,
name|parentValidator
argument_list|)
expr_stmt|;
name|MoveAwarePermissionValidator
name|pv
init|=
operator|(
name|MoveAwarePermissionValidator
operator|)
name|parentValidator
decl_stmt|;
name|moveCtx
operator|=
name|pv
operator|.
name|moveCtx
expr_stmt|;
block|}
annotation|@
name|Override
name|PermissionValidator
name|createValidator
parameter_list|(
annotation|@
name|Nullable
name|ImmutableTree
name|parentBefore
parameter_list|,
annotation|@
name|Nullable
name|ImmutableTree
name|parentAfter
parameter_list|,
annotation|@
name|Nonnull
name|TreePermission
name|parentPermission
parameter_list|,
annotation|@
name|Nonnull
name|PermissionValidator
name|parentValidator
parameter_list|)
block|{
if|if
condition|(
name|moveCtx
operator|.
name|containsMove
argument_list|(
name|parentBefore
argument_list|,
name|parentAfter
argument_list|)
condition|)
block|{
return|return
operator|new
name|MoveAwarePermissionValidator
argument_list|(
name|parentBefore
argument_list|,
name|parentAfter
argument_list|,
name|parentPermission
argument_list|,
name|parentValidator
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|createValidator
argument_list|(
name|parentBefore
argument_list|,
name|parentAfter
argument_list|,
name|parentPermission
argument_list|,
name|parentValidator
argument_list|)
return|;
block|}
block|}
specifier|private
name|Validator
name|visibleValidator
parameter_list|(
annotation|@
name|Nonnull
name|ImmutableTree
name|source
parameter_list|,
annotation|@
name|Nonnull
name|ImmutableTree
name|dest
parameter_list|)
block|{
comment|// TODO improve: avoid calculating the 'before' permissions in case the current parent permissions already point to the correct tree.
name|ImmutableTree
name|parent
init|=
name|moveCtx
operator|.
name|rootBefore
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|TreePermission
name|tp
init|=
name|getPermissionProvider
argument_list|()
operator|.
name|getTreePermission
argument_list|(
name|parent
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|n
range|:
name|source
operator|.
name|getPath
argument_list|()
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
control|)
block|{
name|tp
operator|=
name|tp
operator|.
name|getChildPermission
argument_list|(
name|n
argument_list|,
name|parent
operator|.
name|getChild
argument_list|(
name|n
argument_list|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Validator
name|validator
init|=
name|createValidator
argument_list|(
name|source
argument_list|,
name|dest
argument_list|,
name|tp
argument_list|,
name|this
argument_list|)
decl_stmt|;
return|return
operator|new
name|VisibleValidator
argument_list|(
name|validator
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|//----------------------------------------------------------< Validator>---
annotation|@
name|Override
specifier|public
name|Validator
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|moveCtx
operator|.
name|processAdd
argument_list|(
operator|(
name|ImmutableTree
operator|)
name|getParentAfter
argument_list|()
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
argument_list|,
name|this
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Validator
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|moveCtx
operator|.
name|processDelete
argument_list|(
operator|(
name|ImmutableTree
operator|)
name|getParentBefore
argument_list|()
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
argument_list|,
name|this
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
return|;
block|}
block|}
comment|//--------------------------------------------------------------------------
specifier|private
specifier|final
class|class
name|MoveContext
block|{
specifier|private
specifier|final
name|MoveTracker
name|moveTracker
decl_stmt|;
specifier|private
specifier|final
name|ImmutableRoot
name|rootBefore
decl_stmt|;
specifier|private
specifier|final
name|ImmutableRoot
name|rootAfter
decl_stmt|;
specifier|private
name|MoveContext
parameter_list|(
annotation|@
name|Nonnull
name|MoveTracker
name|moveTracker
parameter_list|,
annotation|@
name|Nonnull
name|ImmutableTree
name|before
parameter_list|,
annotation|@
name|Nonnull
name|ImmutableTree
name|after
parameter_list|)
block|{
name|this
operator|.
name|moveTracker
operator|=
name|moveTracker
expr_stmt|;
name|rootBefore
operator|=
operator|new
name|ImmutableRoot
argument_list|(
name|before
argument_list|)
expr_stmt|;
name|rootAfter
operator|=
operator|new
name|ImmutableRoot
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|containsMove
parameter_list|(
name|Tree
name|parentBefore
parameter_list|,
name|Tree
name|parentAfter
parameter_list|)
block|{
return|return
name|moveTracker
operator|.
name|containsMove
argument_list|(
name|PermissionUtil
operator|.
name|getPath
argument_list|(
name|parentBefore
argument_list|,
name|parentAfter
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|processAdd
parameter_list|(
name|ImmutableTree
name|child
parameter_list|,
name|MoveAwarePermissionValidator
name|validator
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// FIXME: respect and properly handle move-operations in the subtree
name|String
name|sourcePath
init|=
name|moveTracker
operator|.
name|getSourcePath
argument_list|(
name|child
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourcePath
operator|!=
literal|null
condition|)
block|{
name|ImmutableTree
name|source
init|=
name|rootBefore
operator|.
name|getTree
argument_list|(
name|sourcePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// check permissions for adding the moved node at the target location.
name|validator
operator|.
name|checkPermissions
argument_list|(
name|child
argument_list|,
literal|false
argument_list|,
name|Permissions
operator|.
name|ADD_NODE
operator||
name|Permissions
operator|.
name|NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
name|checkPermissions
argument_list|(
name|source
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
return|return
name|diff
argument_list|(
name|source
argument_list|,
name|child
argument_list|,
name|validator
argument_list|)
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|boolean
name|processDelete
parameter_list|(
name|ImmutableTree
name|child
parameter_list|,
name|MoveAwarePermissionValidator
name|validator
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// FIXME: respect and properly handle move-operations in the subtree
name|String
name|destPath
init|=
name|moveTracker
operator|.
name|getDestPath
argument_list|(
name|child
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|destPath
operator|!=
literal|null
condition|)
block|{
name|ImmutableTree
name|dest
init|=
name|rootAfter
operator|.
name|getTree
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// check permissions for removing that node.
name|validator
operator|.
name|checkPermissions
argument_list|(
name|child
argument_list|,
literal|true
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
name|checkPermissions
argument_list|(
name|dest
argument_list|,
name|Permissions
operator|.
name|ADD_NODE
operator||
name|Permissions
operator|.
name|NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
return|return
name|diff
argument_list|(
name|child
argument_list|,
name|dest
argument_list|,
name|validator
argument_list|)
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|boolean
name|diff
parameter_list|(
name|ImmutableTree
name|source
parameter_list|,
name|ImmutableTree
name|dest
parameter_list|,
name|MoveAwarePermissionValidator
name|validator
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|Validator
name|nextValidator
init|=
name|validator
operator|.
name|visibleValidator
argument_list|(
name|source
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|CommitFailedException
name|e
init|=
name|EditorDiff
operator|.
name|process
argument_list|(
name|nextValidator
argument_list|,
name|source
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|dest
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
throw|throw
name|e
throw|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|void
name|checkPermissions
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
name|long
name|permissions
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|!
name|getPermissionProvider
argument_list|()
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|,
name|permissions
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|ACCESS
argument_list|,
literal|0
argument_list|,
literal|"Access denied"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

