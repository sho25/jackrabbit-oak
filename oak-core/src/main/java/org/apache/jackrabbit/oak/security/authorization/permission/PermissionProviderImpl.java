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
name|core
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
name|namepath
operator|.
name|NamePathMapper
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
name|AccessControlConfiguration
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
name|ReadStatus
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
name|util
operator|.
name|TreeUtil
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

begin_comment
comment|/**  * PermissionProviderImpl... TODO  *<p/>  * FIXME: define read/write access patterns on version-store content  * FIXME: proper access permissions on activity-store and configuration-store  */
end_comment

begin_class
specifier|public
class|class
name|PermissionProviderImpl
implements|implements
name|PermissionProvider
implements|,
name|AccessControlConstants
implements|,
name|PermissionConstants
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
name|String
name|workspaceName
decl_stmt|;
specifier|private
specifier|final
name|AccessControlConfiguration
name|acConfig
decl_stmt|;
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
name|root
expr_stmt|;
name|this
operator|.
name|workspaceName
operator|=
name|checkNotNull
argument_list|(
name|ImmutableRoot
operator|.
name|getWorkspaceName
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|acConfig
operator|=
name|securityProvider
operator|.
name|getAccessControlConfiguration
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
name|ImmutableTree
name|permissionsTree
init|=
name|getPermissionsRoot
argument_list|()
decl_stmt|;
if|if
condition|(
name|permissionsTree
operator|==
literal|null
operator|||
name|principals
operator|.
name|isEmpty
argument_list|()
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
name|principals
argument_list|,
name|permissionsTree
argument_list|,
name|getBitsProvider
argument_list|()
argument_list|,
name|acConfig
operator|.
name|getRestrictionProvider
argument_list|(
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|refresh
parameter_list|()
block|{
if|if
condition|(
name|compiledPermissions
operator|instanceof
name|CompiledPermissionImpl
condition|)
block|{
operator|(
operator|(
name|CompiledPermissionImpl
operator|)
name|compiledPermissions
operator|)
operator|.
name|refresh
argument_list|(
name|getPermissionsRoot
argument_list|()
argument_list|,
name|getBitsProvider
argument_list|()
argument_list|)
expr_stmt|;
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
name|getPrivileges
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|compiledPermissions
operator|.
name|getPrivileges
argument_list|(
name|tree
argument_list|)
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
return|return
name|compiledPermissions
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|privilegeNames
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ReadStatus
name|getReadStatus
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|)
block|{
if|if
condition|(
name|isHidden
argument_list|(
name|tree
argument_list|,
name|property
argument_list|)
condition|)
block|{
return|return
name|ReadStatus
operator|.
name|DENY_ALL
return|;
block|}
elseif|else
if|if
condition|(
name|isAccessControlContent
argument_list|(
name|tree
argument_list|)
operator|&&
name|canReadAccessControlContent
argument_list|(
name|tree
argument_list|,
name|property
argument_list|)
condition|)
block|{
comment|// TODO: review if read-ac permission is never fine-granular
return|return
name|ReadStatus
operator|.
name|ALLOW_ALL
return|;
block|}
elseif|else
if|if
condition|(
name|isVersionContent
argument_list|(
name|tree
argument_list|)
operator|&&
name|canReadVersionContent
argument_list|(
name|tree
argument_list|,
name|property
argument_list|)
condition|)
block|{
return|return
name|ReadStatus
operator|.
name|ALLOW_THIS
return|;
block|}
else|else
block|{
return|return
name|compiledPermissions
operator|.
name|getReadStatus
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
name|repositoryPermissions
parameter_list|)
block|{
return|return
name|compiledPermissions
operator|.
name|isGranted
argument_list|(
name|repositoryPermissions
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
annotation|@
name|Nullable
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
name|tree
argument_list|)
condition|)
block|{
name|String
name|path
init|=
name|getVersionablePath
argument_list|(
name|tree
argument_list|,
name|property
argument_list|)
decl_stmt|;
return|return
name|path
operator|!=
literal|null
operator|&&
name|compiledPermissions
operator|.
name|isGranted
argument_list|(
name|path
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
name|isGranted
parameter_list|(
annotation|@
name|Nonnull
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|String
name|jcrActions
parameter_list|)
block|{
name|TreeLocation
name|location
init|=
name|getImmutableRoot
argument_list|()
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
name|PropertyState
name|property
init|=
name|location
operator|.
name|getProperty
argument_list|()
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
name|Tree
name|parent
init|=
name|location
operator|.
name|getParent
argument_list|()
operator|.
name|getTree
argument_list|()
decl_stmt|;
return|return
name|parent
operator|!=
literal|null
operator|&&
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
else|else
block|{
name|Tree
name|tree
init|=
name|location
operator|.
name|getTree
argument_list|()
decl_stmt|;
return|return
name|tree
operator|!=
literal|null
operator|&&
name|isGranted
argument_list|(
name|tree
argument_list|,
literal|null
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
specifier|private
name|ImmutableRoot
name|getImmutableRoot
parameter_list|()
block|{
if|if
condition|(
name|root
operator|instanceof
name|ImmutableRoot
condition|)
block|{
return|return
operator|(
name|ImmutableRoot
operator|)
name|root
return|;
block|}
else|else
block|{
return|return
operator|new
name|ImmutableRoot
argument_list|(
name|root
argument_list|,
operator|new
name|ImmutableTree
operator|.
name|DefaultTypeProvider
argument_list|(
name|acConfig
operator|.
name|getContext
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|CheckForNull
specifier|private
name|ImmutableTree
name|getPermissionsRoot
parameter_list|()
block|{
name|String
name|path
init|=
name|PERMISSIONS_STORE_PATH
operator|+
literal|'/'
operator|+
name|workspaceName
decl_stmt|;
name|Tree
name|tree
init|=
name|getImmutableRoot
argument_list|()
operator|.
name|getLocation
argument_list|(
name|path
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
name|ImmutableTree
operator|)
name|tree
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|PrivilegeBitsProvider
name|getBitsProvider
parameter_list|()
block|{
return|return
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|getImmutableRoot
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isHidden
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|propertyState
parameter_list|)
block|{
return|return
name|ImmutableTree
operator|.
name|TypeProvider
operator|.
name|TYPE_HIDDEN
operator|==
name|ImmutableTree
operator|.
name|getType
argument_list|(
name|tree
argument_list|)
operator|&&
operator|(
name|propertyState
operator|!=
literal|null
operator|&&
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|propertyState
operator|.
name|getName
argument_list|()
argument_list|)
operator|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isAccessControlContent
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|ImmutableTree
operator|.
name|TypeProvider
operator|.
name|TYPE_AC
operator|==
name|ImmutableTree
operator|.
name|getType
argument_list|(
name|tree
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|canReadAccessControlContent
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|acTree
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|acProperty
parameter_list|)
block|{
return|return
name|compiledPermissions
operator|.
name|isGranted
argument_list|(
name|acTree
argument_list|,
name|acProperty
argument_list|,
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
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
return|return
name|ImmutableTree
operator|.
name|TypeProvider
operator|.
name|TYPE_VERSION
operator|==
name|ImmutableTree
operator|.
name|getType
argument_list|(
name|tree
argument_list|)
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
name|relPath
init|=
literal|""
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
name|t
operator|!=
literal|null
operator|&&
operator|!
name|JcrConstants
operator|.
name|JCR_VERSIONSTORAGE
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
name|String
name|name
init|=
name|t
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|ntName
init|=
name|checkNotNull
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|t
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|VersionConstants
operator|.
name|JCR_FROZENNODE
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|t
operator|!=
name|versionStoreTree
condition|)
block|{
name|relPath
operator|=
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
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|JcrConstants
operator|.
name|NT_VERSIONHISTORY
operator|.
name|equals
argument_list|(
name|ntName
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
name|PathUtils
operator|.
name|concat
argument_list|(
name|prop
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|PATH
argument_list|)
argument_list|,
name|relPath
argument_list|,
name|propName
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
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
literal|"Unable to determine path of the version controlled node."
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
block|}
end_class

end_unit

