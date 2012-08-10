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
name|commons
operator|.
name|PathUtils
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
name|name
operator|.
name|NamespaceConstants
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
name|type
operator|.
name|NodeTypeConstants
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
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
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
name|CompiledPermissions
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
name|util
operator|.
name|NodeUtil
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
name|util
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  * PermissionValidator... TODO  */
end_comment

begin_class
class|class
name|PermissionValidator
implements|implements
name|Validator
block|{
comment|/* TODO      * - special permissions for protected items (versioning, access control, etc.)      * - Renaming nodes or Move with same parent are reflected as remove+add -> needs special handling      * - review usage of OAK_CHILD_ORDER property (in particular if the property was removed      */
specifier|private
specifier|final
name|CompiledPermissions
name|compiledPermissions
decl_stmt|;
specifier|private
specifier|final
name|NodeUtil
name|parentBefore
decl_stmt|;
specifier|private
specifier|final
name|NodeUtil
name|parentAfter
decl_stmt|;
name|PermissionValidator
parameter_list|(
name|CompiledPermissions
name|compiledPermissions
parameter_list|,
name|NodeUtil
name|parentBefore
parameter_list|,
name|NodeUtil
name|parentAfter
parameter_list|)
block|{
name|this
operator|.
name|compiledPermissions
operator|=
name|compiledPermissions
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
name|NodeUtil
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
name|NodeUtil
name|childBefore
init|=
name|parentBefore
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|NodeUtil
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
operator|new
name|PermissionValidator
argument_list|(
name|compiledPermissions
argument_list|,
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
name|NodeUtil
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
name|void
name|checkPermissions
parameter_list|(
name|NodeUtil
name|parent
parameter_list|,
name|PropertyState
name|property
parameter_list|,
name|int
name|defaultPermission
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|String
name|parentPath
init|=
name|parent
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
name|int
name|permission
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
comment|// TODO: distinguish between autocreated and user-supplied modification (?)
name|permission
operator|=
name|Permissions
operator|.
name|NODE_TYPE_MANAGEMENT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|PropertyState
operator|.
name|OAK_CHILD_ORDER
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|permission
operator|=
name|Permissions
operator|.
name|MODIFY_CHILD_NODE_COLLECTION
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
name|permission
operator|=
name|Permissions
operator|.
name|LOCK_MANAGEMENT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isNamespaceDefinition
argument_list|(
name|parentPath
argument_list|)
condition|)
block|{
name|permission
operator|=
name|Permissions
operator|.
name|NAMESPACE_MANAGEMENT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isNodeTypeDefinition
argument_list|(
name|parentPath
argument_list|)
condition|)
block|{
name|permission
operator|=
name|Permissions
operator|.
name|NODE_TYPE_DEFINITION_MANAGEMENT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isPrivilegeDefinition
argument_list|(
name|parentPath
argument_list|)
condition|)
block|{
name|permission
operator|=
name|Permissions
operator|.
name|PRIVILEGE_MANAGEMENT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isAccessControl
argument_list|(
name|parent
argument_list|)
condition|)
block|{
name|permission
operator|=
name|Permissions
operator|.
name|MODIFY_ACCESS_CONTROL
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isVersionProperty
argument_list|(
name|parent
argument_list|,
name|property
argument_list|)
condition|)
block|{
name|permission
operator|=
name|Permissions
operator|.
name|VERSION_MANAGEMENT
expr_stmt|;
comment|// FIXME: path to check for permission must be adjusted to be
comment|//        the one of the versionable node instead of the target parent.
block|}
else|else
block|{
comment|// TODO: identify specific permission depending on type of protection
comment|// - user/group property -> user management
name|permission
operator|=
name|defaultPermission
expr_stmt|;
block|}
name|checkPermissions
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentPath
argument_list|,
name|name
argument_list|)
argument_list|,
name|permission
argument_list|)
expr_stmt|;
block|}
specifier|private
name|PermissionValidator
name|checkPermissions
parameter_list|(
name|NodeUtil
name|node
parameter_list|,
name|boolean
name|isBefore
parameter_list|,
name|int
name|defaultPermission
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|String
name|path
init|=
name|node
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|int
name|permission
decl_stmt|;
if|if
condition|(
name|isNamespaceDefinition
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|permission
operator|=
name|Permissions
operator|.
name|NAMESPACE_MANAGEMENT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isNodeTypeDefinition
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|permission
operator|=
name|Permissions
operator|.
name|NODE_TYPE_DEFINITION_MANAGEMENT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isPrivilegeDefinition
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|permission
operator|=
name|Permissions
operator|.
name|PRIVILEGE_MANAGEMENT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isAccessControl
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|permission
operator|=
name|Permissions
operator|.
name|MODIFY_ACCESS_CONTROL
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isVersion
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|permission
operator|=
name|Permissions
operator|.
name|VERSION_MANAGEMENT
expr_stmt|;
comment|// FIXME: path to check for permission must be adjusted to be
comment|// //     the one of the versionable node instead of the target node.
block|}
else|else
block|{
comment|// TODO: identify specific permission depending on additional types of protection
comment|// - user/group -> user management
comment|// - workspace management ???
comment|// TODO: identify renaming/move of nodes that only required MODIFY_CHILD_NODE_COLLECTION permission
name|permission
operator|=
name|defaultPermission
expr_stmt|;
block|}
if|if
condition|(
name|Permissions
operator|.
name|isRepositoryPermissions
argument_list|(
name|permission
argument_list|)
condition|)
block|{
name|checkPermissions
argument_list|(
literal|null
argument_list|,
name|permission
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
comment|// no need for further validation down the subtree
block|}
else|else
block|{
name|checkPermissions
argument_list|(
name|path
argument_list|,
name|permission
argument_list|)
expr_stmt|;
return|return
operator|(
name|isBefore
operator|)
condition|?
operator|new
name|PermissionValidator
argument_list|(
name|compiledPermissions
argument_list|,
name|node
argument_list|,
literal|null
argument_list|)
else|:
operator|new
name|PermissionValidator
argument_list|(
name|compiledPermissions
argument_list|,
literal|null
argument_list|,
name|node
argument_list|)
return|;
block|}
block|}
specifier|private
name|void
name|checkPermissions
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|permissions
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|!
name|compiledPermissions
operator|.
name|isGranted
argument_list|(
name|path
argument_list|,
name|permissions
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
specifier|private
specifier|static
name|boolean
name|isAccessControl
parameter_list|(
name|NodeUtil
name|node
parameter_list|)
block|{
comment|// TODO: depends on ac-model
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isVersion
parameter_list|(
name|NodeUtil
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|getTree
argument_list|()
operator|.
name|isRoot
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// TODO: review again
if|if
condition|(
name|VersionConstants
operator|.
name|VERSION_NODE_NAMES
operator|.
name|contains
argument_list|(
name|node
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|VersionConstants
operator|.
name|VERSION_NODE_TYPE_NAMES
operator|.
name|contains
argument_list|(
name|node
operator|.
name|getName
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
name|String
name|path
init|=
name|node
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
return|return
name|VersionConstants
operator|.
name|SYSTEM_PATHS
operator|.
name|contains
argument_list|(
name|Text
operator|.
name|getAbsoluteParent
argument_list|(
name|path
argument_list|,
literal|1
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|isVersionProperty
parameter_list|(
name|NodeUtil
name|parent
parameter_list|,
name|PropertyState
name|property
parameter_list|)
block|{
if|if
condition|(
name|VersionConstants
operator|.
name|VERSION_PROPERTY_NAMES
operator|.
name|contains
argument_list|(
name|property
operator|.
name|getName
argument_list|()
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
name|isVersion
argument_list|(
name|parent
argument_list|)
return|;
block|}
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
specifier|private
specifier|static
name|boolean
name|isNamespaceDefinition
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|Text
operator|.
name|isDescendant
argument_list|(
name|NamespaceConstants
operator|.
name|NAMESPACES_PATH
argument_list|,
name|path
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isNodeTypeDefinition
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|Text
operator|.
name|isDescendant
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
argument_list|,
name|path
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isPrivilegeDefinition
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|Text
operator|.
name|isDescendant
argument_list|(
name|PrivilegeConstants
operator|.
name|PRIVILEGES_PATH
argument_list|,
name|path
argument_list|)
return|;
block|}
block|}
end_class

end_unit

