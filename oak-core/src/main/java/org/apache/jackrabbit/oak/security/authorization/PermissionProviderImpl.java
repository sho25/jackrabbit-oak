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
name|java
operator|.
name|security
operator|.
name|Principal
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
name|Set
import|;
end_import

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|Root
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
name|api
operator|.
name|TreeLocation
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
name|Type
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
name|core
operator|.
name|ReadOnlyRoot
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
name|ReadOnlyTree
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
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|AllPermissions
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
name|authorization
operator|.
name|permission
operator|.
name|CompiledPermissionImpl
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
name|authorization
operator|.
name|permission
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
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|NoPermissions
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
name|Context
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
name|SecurityProvider
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
name|security
operator|.
name|principal
operator|.
name|AdminPrincipal
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
name|principal
operator|.
name|SystemPrincipal
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
name|TreeUtil
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
comment|/**  * PermissionProviderImpl... TODO  *<p/>  * FIXME: permissions need to be refreshed if something changes in the permission tree  * FIXME: define read/write access patterns on version-store content  * FIXME: proper access permissions on activity-store and configuration-store  */
end_comment

begin_class
specifier|public
class|class
name|PermissionProviderImpl
implements|implements
name|PermissionProvider
implements|,
name|AccessControlConstants
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PermissionProviderImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|private
specifier|final
name|Context
name|acContext
decl_stmt|;
specifier|private
specifier|final
name|String
name|workspaceName
init|=
literal|"default"
decl_stmt|;
comment|// FIXME: use proper workspace as associated with the root
specifier|private
specifier|final
name|CompiledPermissions
name|compiledPermissions
decl_stmt|;
specifier|public
name|PermissionProviderImpl
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|,
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
operator|new
name|ReadOnlyRoot
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|this
operator|.
name|acContext
operator|=
name|securityProvider
operator|.
name|getAccessControlConfiguration
argument_list|()
operator|.
name|getContext
argument_list|()
expr_stmt|;
if|if
condition|(
name|principals
operator|.
name|contains
argument_list|(
name|SystemPrincipal
operator|.
name|INSTANCE
argument_list|)
operator|||
name|isAdmin
argument_list|(
name|principals
argument_list|)
condition|)
block|{
name|compiledPermissions
operator|=
name|AllPermissions
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|String
name|relativePath
init|=
name|PERMISSIONS_STORE_PATH
operator|+
literal|'/'
operator|+
name|workspaceName
decl_stmt|;
name|ReadOnlyTree
name|rootTree
init|=
name|ReadOnlyTree
operator|.
name|createFromRoot
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|ReadOnlyTree
name|permissionsTree
init|=
name|getPermissionsRoot
argument_list|(
name|rootTree
argument_list|,
name|relativePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|permissionsTree
operator|==
literal|null
condition|)
block|{
name|compiledPermissions
operator|=
name|NoPermissions
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|compiledPermissions
operator|=
operator|new
name|CompiledPermissionImpl
argument_list|(
name|permissionsTree
argument_list|,
name|principals
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getPrivilegeNames
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|)
block|{
comment|// TODO
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPrivileges
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|,
name|String
modifier|...
name|privilegeNames
parameter_list|)
block|{
comment|// TODO
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
if|if
condition|(
name|acContext
operator|.
name|definesTree
argument_list|(
name|tree
argument_list|)
condition|)
block|{
return|return
name|compiledPermissions
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|isVersionContent
argument_list|(
name|tree
argument_list|)
condition|)
block|{
return|return
name|canReadVersionContent
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|compiledPermissions
operator|.
name|canRead
argument_list|(
name|tree
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
if|if
condition|(
name|acContext
operator|.
name|definesTree
argument_list|(
name|tree
argument_list|)
condition|)
block|{
return|return
name|compiledPermissions
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
name|property
argument_list|,
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|isVersionContent
argument_list|(
name|tree
argument_list|)
condition|)
block|{
return|return
name|canReadVersionContent
argument_list|(
name|tree
argument_list|,
name|property
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|compiledPermissions
operator|.
name|canRead
argument_list|(
name|tree
argument_list|,
name|property
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|long
name|permissions
parameter_list|)
block|{
return|return
name|compiledPermissions
operator|.
name|isGranted
argument_list|(
name|permissions
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
if|if
condition|(
name|isVersionContent
argument_list|(
name|tree
argument_list|)
condition|)
block|{
return|return
name|compiledPermissions
operator|.
name|isGranted
argument_list|(
name|getVersionablePath
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|)
argument_list|,
name|permissions
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|compiledPermissions
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
name|permissions
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
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
name|permissions
parameter_list|)
block|{
if|if
condition|(
name|isVersionContent
argument_list|(
name|parent
argument_list|)
condition|)
block|{
return|return
name|compiledPermissions
operator|.
name|isGranted
argument_list|(
name|getVersionablePath
argument_list|(
name|parent
argument_list|,
name|property
argument_list|)
argument_list|,
name|permissions
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|compiledPermissions
operator|.
name|isGranted
argument_list|(
name|parent
argument_list|,
name|property
argument_list|,
name|permissions
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPermission
parameter_list|(
annotation|@
name|Nonnull
name|String
name|oakPath
parameter_list|,
name|String
name|jcrActions
parameter_list|)
block|{
name|TreeLocation
name|location
init|=
name|root
operator|.
name|getLocation
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
name|long
name|permissions
init|=
name|Permissions
operator|.
name|getPermissions
argument_list|(
name|jcrActions
argument_list|,
name|location
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|location
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// TODO: deal with version content
return|return
name|compiledPermissions
operator|.
name|isGranted
argument_list|(
name|oakPath
argument_list|,
name|permissions
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|location
operator|.
name|getProperty
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|isGranted
argument_list|(
name|location
operator|.
name|getTree
argument_list|()
argument_list|,
name|location
operator|.
name|getProperty
argument_list|()
argument_list|,
name|permissions
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|isGranted
argument_list|(
name|location
operator|.
name|getTree
argument_list|()
argument_list|,
name|permissions
argument_list|)
return|;
block|}
block|}
comment|//--------------------------------------------------------------------------
specifier|private
specifier|static
name|boolean
name|isAdmin
parameter_list|(
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
for|for
control|(
name|Principal
name|principal
range|:
name|principals
control|)
block|{
if|if
condition|(
name|principal
operator|instanceof
name|AdminPrincipal
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
specifier|static
name|ReadOnlyTree
name|getPermissionsRoot
parameter_list|(
name|ReadOnlyTree
name|rootTree
parameter_list|,
name|String
name|relativePath
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|rootTree
operator|.
name|getLocation
argument_list|()
operator|.
name|getChild
argument_list|(
name|relativePath
argument_list|)
operator|.
name|getTree
argument_list|()
decl_stmt|;
return|return
operator|(
name|tree
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|(
name|ReadOnlyTree
operator|)
name|tree
return|;
block|}
specifier|private
name|boolean
name|canReadVersionContent
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|versionStoreTree
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|)
block|{
name|String
name|versionablePath
init|=
name|getVersionablePath
argument_list|(
name|versionStoreTree
argument_list|,
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|versionablePath
operator|!=
literal|null
condition|)
block|{
name|long
name|permission
init|=
operator|(
name|property
operator|==
literal|null
operator|)
condition|?
name|Permissions
operator|.
name|READ_NODE
else|:
name|Permissions
operator|.
name|READ_PROPERTY
decl_stmt|;
return|return
name|compiledPermissions
operator|.
name|isGranted
argument_list|(
name|versionablePath
argument_list|,
name|permission
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|CheckForNull
specifier|private
name|String
name|getVersionablePath
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|versionStoreTree
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|)
block|{
name|String
name|versionablePath
init|=
literal|null
decl_stmt|;
name|Tree
name|t
init|=
name|versionStoreTree
decl_stmt|;
while|while
condition|(
operator|!
name|JcrConstants
operator|.
name|JCR_SYSTEM
operator|.
name|equals
argument_list|(
name|t
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|JcrConstants
operator|.
name|NT_VERSIONHISTORY
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|t
argument_list|)
argument_list|)
condition|)
block|{
name|PropertyState
name|prop
init|=
name|t
operator|.
name|getProperty
argument_list|(
name|workspaceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|prop
operator|!=
literal|null
condition|)
block|{
name|versionablePath
operator|=
name|prop
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|PATH
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|!=
name|versionStoreTree
condition|)
block|{
name|String
name|rel
init|=
name|PathUtils
operator|.
name|relativize
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|,
name|versionStoreTree
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|propName
init|=
operator|(
name|property
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
name|versionablePath
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|versionablePath
argument_list|,
name|rel
argument_list|,
name|propName
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
block|}
comment|// FIXME: handle activities and configurations
name|t
operator|=
name|t
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|versionablePath
operator|==
literal|null
operator|||
name|versionablePath
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to determine path of the versionable node."
argument_list|)
expr_stmt|;
block|}
return|return
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|versionablePath
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isVersionContent
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
operator|.
name|isRoot
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|VersionConstants
operator|.
name|VERSION_NODE_NAMES
operator|.
name|contains
argument_list|(
name|tree
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
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|tree
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
return|return
name|isVersionContent
argument_list|(
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|isVersionContent
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
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
end_class

end_unit

