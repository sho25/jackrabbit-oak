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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

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
name|javax
operator|.
name|jcr
operator|.
name|AccessDeniedException
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
name|JcrConstants
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
name|PropertyState
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
name|TreeImpl
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
name|version
operator|.
name|VersionConstants
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
name|security
operator|.
name|authorization
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

begin_comment
comment|/**  * Validator implementation that checks for sufficient permission for all  * write operations executed by a given content session.  */
end_comment

begin_class
class|class
name|PermissionValidator
implements|implements
name|Validator
block|{
comment|/* TODO      * - Renaming nodes or Move with same parent are reflected as remove+add -> needs special handling      * - Proper handling of jcr:nodeTypeManagement privilege.      */
specifier|private
specifier|final
name|Tree
name|parentBefore
decl_stmt|;
specifier|private
specifier|final
name|Tree
name|parentAfter
decl_stmt|;
specifier|private
specifier|final
name|PermissionProvider
name|permissionProvider
decl_stmt|;
specifier|private
specifier|final
name|PermissionValidatorProvider
name|provider
decl_stmt|;
specifier|private
specifier|final
name|long
name|permission
decl_stmt|;
name|PermissionValidator
parameter_list|(
name|Tree
name|parentBefore
parameter_list|,
name|Tree
name|parentAfter
parameter_list|,
name|PermissionProvider
name|permissionProvider
parameter_list|,
name|PermissionValidatorProvider
name|provider
parameter_list|)
block|{
name|this
argument_list|(
name|parentBefore
argument_list|,
name|parentAfter
argument_list|,
name|permissionProvider
argument_list|,
name|provider
argument_list|,
name|Permissions
operator|.
name|getPermission
argument_list|(
name|getPath
argument_list|(
name|parentBefore
argument_list|,
name|parentAfter
argument_list|)
argument_list|,
name|Permissions
operator|.
name|NO_PERMISSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|PermissionValidator
parameter_list|(
name|Tree
name|parentBefore
parameter_list|,
name|Tree
name|parentAfter
parameter_list|,
name|PermissionProvider
name|permissionProvider
parameter_list|,
name|PermissionValidatorProvider
name|provider
parameter_list|,
name|long
name|permission
parameter_list|)
block|{
name|this
operator|.
name|permissionProvider
operator|=
name|permissionProvider
expr_stmt|;
name|this
operator|.
name|provider
operator|=
name|provider
expr_stmt|;
name|this
operator|.
name|parentBefore
operator|=
name|parentBefore
expr_stmt|;
name|this
operator|.
name|parentAfter
operator|=
name|parentAfter
expr_stmt|;
name|this
operator|.
name|permission
operator|=
name|permission
expr_stmt|;
block|}
comment|//----------------------------------------------------------< Validator>---
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|checkPermissions
argument_list|(
name|parentAfter
argument_list|,
name|after
argument_list|,
name|Permissions
operator|.
name|ADD_PROPERTY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|TreeImpl
operator|.
name|OAK_CHILD_ORDER
operator|.
name|equals
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|checkPermissions
argument_list|(
name|parentAfter
argument_list|,
literal|false
argument_list|,
name|Permissions
operator|.
name|MODIFY_CHILD_NODE_COLLECTION
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|checkPermissions
argument_list|(
name|parentAfter
argument_list|,
name|after
argument_list|,
name|Permissions
operator|.
name|MODIFY_PROPERTY
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|checkPermissions
argument_list|(
name|parentBefore
argument_list|,
name|before
argument_list|,
name|Permissions
operator|.
name|REMOVE_PROPERTY
argument_list|)
expr_stmt|;
block|}
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
name|Tree
name|child
init|=
name|parentAfter
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|checkPermissions
argument_list|(
name|child
argument_list|,
literal|false
argument_list|,
name|Permissions
operator|.
name|ADD_NODE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
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
throws|throws
name|CommitFailedException
block|{
name|Tree
name|childBefore
init|=
name|parentBefore
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Tree
name|childAfter
init|=
name|parentAfter
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// TODO
return|return
name|nextValidator
argument_list|(
name|childBefore
argument_list|,
name|childAfter
argument_list|)
return|;
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
name|Tree
name|child
init|=
name|parentBefore
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
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
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|Validator
name|nextValidator
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|parentBefore
parameter_list|,
annotation|@
name|Nullable
name|Tree
name|parentAfter
parameter_list|)
block|{
return|return
operator|new
name|PermissionValidator
argument_list|(
name|parentBefore
argument_list|,
name|parentAfter
argument_list|,
name|permissionProvider
argument_list|,
name|provider
argument_list|,
name|permission
argument_list|)
return|;
block|}
specifier|private
name|Validator
name|checkPermissions
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
name|boolean
name|isBefore
parameter_list|,
name|long
name|defaultPermission
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|long
name|toTest
init|=
name|getPermission
argument_list|(
name|tree
argument_list|,
name|defaultPermission
argument_list|)
decl_stmt|;
if|if
condition|(
name|Permissions
operator|.
name|isRepositoryPermission
argument_list|(
name|toTest
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|toTest
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
operator|new
name|AccessDeniedException
argument_list|()
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
comment|// no need for further validation down the subtree
block|}
else|else
block|{
if|if
condition|(
operator|!
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
name|toTest
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
operator|new
name|AccessDeniedException
argument_list|()
argument_list|)
throw|;
block|}
return|return
operator|(
name|isBefore
operator|)
condition|?
name|nextValidator
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|)
else|:
name|nextValidator
argument_list|(
literal|null
argument_list|,
name|tree
argument_list|)
return|;
block|}
block|}
specifier|private
name|void
name|checkPermissions
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|,
name|long
name|defaultPermission
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|!
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
operator|(
name|property
operator|.
name|getName
argument_list|()
operator|)
argument_list|)
condition|)
block|{
name|long
name|toTest
init|=
name|getPermission
argument_list|(
name|parent
argument_list|,
name|property
argument_list|,
name|defaultPermission
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|parent
argument_list|,
name|property
argument_list|,
name|toTest
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
operator|new
name|AccessDeniedException
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|CheckForNull
specifier|private
specifier|static
name|String
name|getPath
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|parentBefore
parameter_list|,
annotation|@
name|Nullable
name|Tree
name|parentAfter
parameter_list|)
block|{
name|String
name|path
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|parentBefore
operator|!=
literal|null
condition|)
block|{
name|path
operator|=
name|parentBefore
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parentAfter
operator|!=
literal|null
condition|)
block|{
name|path
operator|=
name|parentAfter
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
specifier|public
name|long
name|getPermission
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
name|long
name|defaultPermission
parameter_list|)
block|{
if|if
condition|(
name|permission
operator|!=
name|Permissions
operator|.
name|NO_PERMISSION
condition|)
block|{
return|return
name|permission
return|;
block|}
name|long
name|perm
decl_stmt|;
if|if
condition|(
name|provider
operator|.
name|getAccessControlContext
argument_list|()
operator|.
name|definesTree
argument_list|(
name|tree
argument_list|)
condition|)
block|{
name|perm
operator|=
name|Permissions
operator|.
name|MODIFY_ACCESS_CONTROL
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|provider
operator|.
name|getUserContext
argument_list|()
operator|.
name|definesTree
argument_list|(
name|tree
argument_list|)
condition|)
block|{
name|perm
operator|=
name|Permissions
operator|.
name|USER_MANAGEMENT
expr_stmt|;
block|}
else|else
block|{
comment|// TODO: identify renaming/move of nodes that only required MODIFY_CHILD_NODE_COLLECTION permission
name|perm
operator|=
name|defaultPermission
expr_stmt|;
block|}
return|return
name|perm
return|;
block|}
specifier|public
name|long
name|getPermission
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|PropertyState
name|propertyState
parameter_list|,
name|long
name|defaultPermission
parameter_list|)
block|{
if|if
condition|(
name|permission
operator|!=
name|Permissions
operator|.
name|NO_PERMISSION
condition|)
block|{
return|return
name|permission
return|;
block|}
name|String
name|name
init|=
name|propertyState
operator|.
name|getName
argument_list|()
decl_stmt|;
name|long
name|perm
decl_stmt|;
if|if
condition|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// FIXME: distinguish between autocreated and user-supplied modification (?)
name|perm
operator|=
name|Permissions
operator|.
name|NODE_TYPE_MANAGEMENT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isLockProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|perm
operator|=
name|Permissions
operator|.
name|LOCK_MANAGEMENT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|VersionConstants
operator|.
name|VERSION_PROPERTY_NAMES
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|perm
operator|=
name|Permissions
operator|.
name|VERSION_MANAGEMENT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|provider
operator|.
name|getAccessControlContext
argument_list|()
operator|.
name|definesProperty
argument_list|(
name|parent
argument_list|,
name|propertyState
argument_list|)
condition|)
block|{
name|perm
operator|=
name|Permissions
operator|.
name|MODIFY_ACCESS_CONTROL
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|provider
operator|.
name|getUserContext
argument_list|()
operator|.
name|definesProperty
argument_list|(
name|parent
argument_list|,
name|propertyState
argument_list|)
condition|)
block|{
name|perm
operator|=
name|Permissions
operator|.
name|USER_MANAGEMENT
expr_stmt|;
block|}
else|else
block|{
name|perm
operator|=
name|defaultPermission
expr_stmt|;
block|}
return|return
name|perm
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isLockProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|JcrConstants
operator|.
name|JCR_LOCKISDEEP
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
name|JcrConstants
operator|.
name|JCR_LOCKOWNER
operator|.
name|equals
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

