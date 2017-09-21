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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
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
name|TreeFactory
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
name|impl
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
name|plugins
operator|.
name|lock
operator|.
name|LockConstants
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
name|impl
operator|.
name|TreeConstants
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
name|DefaultValidator
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
name|impl
operator|.
name|ChildOrderDiff
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
name|TreeUtil
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|JCR_CREATED
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
name|MIX_REFERENCEABLE
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
name|spi
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|JCR_CREATEDBY
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
name|spi
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|MIX_CREATED
import|;
end_import

begin_comment
comment|/**  * Validator implementation that checks for sufficient permission for all  * write operations executed by a given content session.  */
end_comment

begin_class
class|class
name|PermissionValidator
extends|extends
name|DefaultValidator
block|{
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
name|TreePermission
name|parentPermission
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
name|TypePredicate
name|isReferenceable
decl_stmt|;
specifier|private
specifier|final
name|TypePredicate
name|isCreated
decl_stmt|;
specifier|private
specifier|final
name|long
name|permission
decl_stmt|;
name|PermissionValidator
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|rootBefore
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
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
parameter_list|)
block|{
name|this
operator|.
name|parentBefore
operator|=
name|TreeFactory
operator|.
name|createReadOnlyTree
argument_list|(
name|rootBefore
argument_list|)
expr_stmt|;
name|this
operator|.
name|parentAfter
operator|=
name|TreeFactory
operator|.
name|createReadOnlyTree
argument_list|(
name|rootAfter
argument_list|)
expr_stmt|;
name|this
operator|.
name|parentPermission
operator|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|parentBefore
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
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
name|isReferenceable
operator|=
operator|new
name|TypePredicate
argument_list|(
name|rootAfter
argument_list|,
name|MIX_REFERENCEABLE
argument_list|)
expr_stmt|;
name|this
operator|.
name|isCreated
operator|=
operator|new
name|TypePredicate
argument_list|(
name|rootAfter
argument_list|,
name|MIX_CREATED
argument_list|)
expr_stmt|;
name|permission
operator|=
name|Permissions
operator|.
name|getPermission
argument_list|(
name|PermissionUtil
operator|.
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
expr_stmt|;
block|}
name|PermissionValidator
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
parameter_list|,
annotation|@
name|Nullable
name|TreePermission
name|parentPermission
parameter_list|,
annotation|@
name|Nonnull
name|PermissionValidator
name|parentValidator
parameter_list|)
block|{
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
name|parentPermission
operator|=
name|parentPermission
expr_stmt|;
name|permissionProvider
operator|=
name|parentValidator
operator|.
name|permissionProvider
expr_stmt|;
name|provider
operator|=
name|parentValidator
operator|.
name|provider
expr_stmt|;
name|this
operator|.
name|isReferenceable
operator|=
name|parentValidator
operator|.
name|isReferenceable
expr_stmt|;
name|this
operator|.
name|isCreated
operator|=
name|parentValidator
operator|.
name|isCreated
expr_stmt|;
if|if
condition|(
name|Permissions
operator|.
name|NO_PERMISSION
operator|==
name|parentValidator
operator|.
name|permission
condition|)
block|{
name|this
operator|.
name|permission
operator|=
name|Permissions
operator|.
name|getPermission
argument_list|(
name|PermissionUtil
operator|.
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
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|permission
operator|=
name|parentValidator
operator|.
name|permission
expr_stmt|;
block|}
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
name|String
name|name
init|=
name|after
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|isImmutableProperty
argument_list|(
name|name
argument_list|,
name|parentAfter
argument_list|)
condition|)
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
name|String
name|name
init|=
name|after
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|String
name|childName
init|=
name|ChildOrderDiff
operator|.
name|firstReordered
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|childName
operator|!=
literal|null
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
comment|// else: no re-order but only internal update
block|}
elseif|else
if|if
condition|(
name|isImmutableProperty
argument_list|(
name|name
argument_list|,
name|parentAfter
argument_list|)
condition|)
block|{
comment|// parent node has been removed and and re-added as
name|checkPermissions
argument_list|(
name|parentAfter
argument_list|,
literal|false
argument_list|,
name|Permissions
operator|.
name|ADD_NODE
operator||
name|Permissions
operator|.
name|REMOVE_NODE
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
name|String
name|name
init|=
name|before
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|isImmutableProperty
argument_list|(
name|name
argument_list|,
name|parentBefore
argument_list|)
condition|)
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
name|checkNotNull
argument_list|(
name|parentAfter
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|isVersionstorageTree
argument_list|(
name|child
argument_list|)
condition|)
block|{
name|child
operator|=
name|getVersionHistoryTree
argument_list|(
name|child
argument_list|)
expr_stmt|;
if|if
condition|(
name|child
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|ACCESS
argument_list|,
literal|21
argument_list|,
literal|"New version storage node without version history: cannot verify permissions."
argument_list|)
throw|;
block|}
block|}
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
return|return
name|nextValidator
argument_list|(
name|childBefore
argument_list|,
name|childAfter
argument_list|,
name|parentPermission
operator|.
name|getChildPermission
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
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
if|if
condition|(
name|isVersionstorageTree
argument_list|(
name|child
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|ACCESS
argument_list|,
literal|22
argument_list|,
literal|"Attempt to remove versionstorage node: Fail to verify delete permission."
argument_list|)
throw|;
block|}
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
comment|//-------------------------------------------------< internal / private>---
annotation|@
name|Nonnull
name|PermissionValidator
name|createValidator
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
return|return
operator|new
name|PermissionValidator
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
annotation|@
name|CheckForNull
name|Tree
name|getParentAfter
parameter_list|()
block|{
return|return
name|parentAfter
return|;
block|}
annotation|@
name|CheckForNull
name|Tree
name|getParentBefore
parameter_list|()
block|{
return|return
name|parentBefore
return|;
block|}
annotation|@
name|Nonnull
name|PermissionProvider
name|getPermissionProvider
parameter_list|()
block|{
return|return
name|permissionProvider
return|;
block|}
annotation|@
name|CheckForNull
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
name|getRepositoryPermission
argument_list|()
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
name|ACCESS
argument_list|,
literal|0
argument_list|,
literal|"Access denied"
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
name|NodeState
name|ns
init|=
name|getNodeState
argument_list|(
name|tree
argument_list|)
decl_stmt|;
if|if
condition|(
name|ns
operator|==
literal|null
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
name|TreePermission
name|tp
init|=
name|parentPermission
operator|.
name|getChildPermission
argument_list|(
name|tree
operator|.
name|getName
argument_list|()
argument_list|,
name|ns
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|tp
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
name|ACCESS
argument_list|,
literal|0
argument_list|,
literal|"Access denied"
argument_list|)
throw|;
block|}
if|if
condition|(
name|noTraverse
argument_list|(
name|toTest
argument_list|,
name|defaultPermission
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
operator|(
name|isBefore
operator|)
condition|?
name|nextValidator
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|,
name|tp
argument_list|)
else|:
name|nextValidator
argument_list|(
literal|null
argument_list|,
name|tree
argument_list|,
name|tp
argument_list|)
return|;
block|}
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
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// ignore any hidden properties (except for OAK_CHILD_ORDER which has
comment|// been covered in "propertyChanged"
return|return;
block|}
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
name|toTest
operator|!=
name|Permissions
operator|.
name|NO_PERMISSION
condition|)
block|{
name|boolean
name|isGranted
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
name|isGranted
operator|=
name|permissionProvider
operator|.
name|getRepositoryPermission
argument_list|()
operator|.
name|isGranted
argument_list|(
name|toTest
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|isGranted
operator|=
name|parentPermission
operator|.
name|isGranted
argument_list|(
name|toTest
argument_list|,
name|property
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isGranted
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
annotation|@
name|Nonnull
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
parameter_list|,
annotation|@
name|Nonnull
name|TreePermission
name|treePermission
parameter_list|)
block|{
name|Validator
name|validator
init|=
name|createValidator
argument_list|(
name|parentBefore
argument_list|,
name|parentAfter
argument_list|,
name|treePermission
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
specifier|private
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
name|testAccessControlPermission
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
name|testUserPermission
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
elseif|else
if|if
condition|(
name|isIndexDefinition
argument_list|(
name|tree
argument_list|)
condition|)
block|{
name|perm
operator|=
name|Permissions
operator|.
name|INDEX_DEFINITION_MANAGEMENT
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
condition|)
block|{
if|if
condition|(
name|defaultPermission
operator|==
name|Permissions
operator|.
name|MODIFY_PROPERTY
condition|)
block|{
name|perm
operator|=
name|getPermission
argument_list|(
name|parent
argument_list|,
name|Permissions
operator|.
name|NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// can't determine if this was  a user supplied modification of
comment|// the primary type -> omit permission check.
comment|// Node#addNode(String, String) and related methods need to
comment|// perform the permission check (as it used to be in jackrabbit 2.x).
name|perm
operator|=
name|Permissions
operator|.
name|NO_PERMISSION
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
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
name|LockConstants
operator|.
name|LOCK_PROPERTY_NAMES
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
operator|&&
operator|!
name|provider
operator|.
name|requiresJr2Permissions
argument_list|(
name|Permissions
operator|.
name|USER_MANAGEMENT
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
elseif|else
if|if
condition|(
name|isIndexDefinition
argument_list|(
name|parent
argument_list|)
condition|)
block|{
name|perm
operator|=
name|Permissions
operator|.
name|INDEX_DEFINITION_MANAGEMENT
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
name|boolean
name|noTraverse
parameter_list|(
name|long
name|permission
parameter_list|,
name|long
name|defaultPermission
parameter_list|)
block|{
if|if
condition|(
name|defaultPermission
operator|==
name|Permissions
operator|.
name|REMOVE_NODE
operator|&&
name|provider
operator|.
name|requiresJr2Permissions
argument_list|(
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|permission
operator|==
name|Permissions
operator|.
name|MODIFY_ACCESS_CONTROL
operator|||
name|permission
operator|==
name|Permissions
operator|.
name|VERSION_MANAGEMENT
operator|||
name|permission
operator|==
name|Permissions
operator|.
name|REMOVE_NODE
operator|||
name|defaultPermission
operator|==
name|Permissions
operator|.
name|REMOVE_NODE
return|;
block|}
block|}
specifier|private
name|boolean
name|isImmutableProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|parent
parameter_list|)
block|{
comment|// NOTE: we cannot rely on autocreated/protected definition as this
comment|// doesn't reveal if a given property is expected to be never modified
comment|// after creation.
name|NodeState
name|parentNs
init|=
name|getNodeState
argument_list|(
name|parent
argument_list|)
decl_stmt|;
if|if
condition|(
name|JcrConstants
operator|.
name|JCR_UUID
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|isReferenceable
operator|.
name|apply
argument_list|(
name|parentNs
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
operator|(
name|JCR_CREATED
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
name|JCR_CREATEDBY
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|)
operator|&&
name|isCreated
operator|.
name|apply
argument_list|(
name|parentNs
argument_list|)
return|;
block|}
block|}
specifier|private
name|boolean
name|testUserPermission
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|provider
operator|.
name|getUserContext
argument_list|()
operator|.
name|definesTree
argument_list|(
name|tree
argument_list|)
operator|&&
operator|!
name|provider
operator|.
name|requiresJr2Permissions
argument_list|(
name|Permissions
operator|.
name|USER_MANAGEMENT
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|testAccessControlPermission
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|provider
operator|.
name|getAccessControlContext
argument_list|()
operator|.
name|definesTree
argument_list|(
name|tree
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isVersionstorageTree
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|permission
operator|==
name|Permissions
operator|.
name|VERSION_MANAGEMENT
operator|&&
name|VersionConstants
operator|.
name|REP_VERSIONSTORAGE
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|tree
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
name|Tree
name|getVersionHistoryTree
parameter_list|(
name|Tree
name|versionstorageTree
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|Tree
name|versionHistory
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Tree
name|child
range|:
name|versionstorageTree
operator|.
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|VersionConstants
operator|.
name|NT_VERSIONHISTORY
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|child
argument_list|)
argument_list|)
condition|)
block|{
name|versionHistory
operator|=
name|child
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isVersionstorageTree
argument_list|(
name|child
argument_list|)
condition|)
block|{
name|versionHistory
operator|=
name|getVersionHistoryTree
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Misc"
argument_list|,
literal|0
argument_list|,
literal|"unexpected node"
argument_list|)
throw|;
block|}
block|}
return|return
name|versionHistory
return|;
block|}
specifier|private
name|boolean
name|isIndexDefinition
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|tree
operator|.
name|getPath
argument_list|()
operator|.
name|contains
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
specifier|static
name|NodeState
name|getNodeState
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
if|if
condition|(
name|tree
operator|instanceof
name|ImmutableTree
condition|)
block|{
return|return
operator|(
operator|(
name|ImmutableTree
operator|)
name|tree
operator|)
operator|.
name|getNodeState
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

