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
name|plugins
operator|.
name|tree
operator|.
name|RootFactory
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
name|plugins
operator|.
name|tree
operator|.
name|TreeType
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
name|security
operator|.
name|ConfigurationParameters
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
name|AggregatedPermissionProvider
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
name|RepositoryPermission
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
name|PrivilegeBits
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
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
import|;
end_import

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
implements|,
name|AggregatedPermissionProvider
block|{
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
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
decl_stmt|;
specifier|private
specifier|final
name|RestrictionProvider
name|restrictionProvider
decl_stmt|;
specifier|private
specifier|final
name|ConfigurationParameters
name|options
decl_stmt|;
specifier|private
specifier|final
name|Context
name|ctx
decl_stmt|;
specifier|private
name|CompiledPermissions
name|compiledPermissions
decl_stmt|;
specifier|private
name|Root
name|immutableRoot
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
name|String
name|workspaceName
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
name|RestrictionProvider
name|restrictionProvider
parameter_list|,
annotation|@
name|Nonnull
name|ConfigurationParameters
name|options
parameter_list|,
annotation|@
name|Nonnull
name|Context
name|ctx
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
name|workspaceName
expr_stmt|;
name|this
operator|.
name|principals
operator|=
name|principals
expr_stmt|;
name|this
operator|.
name|restrictionProvider
operator|=
name|restrictionProvider
expr_stmt|;
name|this
operator|.
name|options
operator|=
name|options
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
name|immutableRoot
operator|=
name|RootFactory
operator|.
name|createReadOnlyRoot
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|refresh
parameter_list|()
block|{
name|immutableRoot
operator|=
name|RootFactory
operator|.
name|createReadOnlyRoot
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|getCompiledPermissions
argument_list|()
operator|.
name|refresh
argument_list|(
name|immutableRoot
argument_list|,
name|workspaceName
argument_list|)
expr_stmt|;
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
name|getCompiledPermissions
argument_list|()
operator|.
name|getPrivileges
argument_list|(
name|PermissionUtil
operator|.
name|getImmutableTree
argument_list|(
name|tree
argument_list|,
name|immutableRoot
argument_list|)
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
annotation|@
name|Nonnull
name|String
modifier|...
name|privilegeNames
parameter_list|)
block|{
return|return
name|getCompiledPermissions
argument_list|()
operator|.
name|hasPrivileges
argument_list|(
name|PermissionUtil
operator|.
name|getImmutableTree
argument_list|(
name|tree
argument_list|,
name|immutableRoot
argument_list|)
argument_list|,
name|privilegeNames
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RepositoryPermission
name|getRepositoryPermission
parameter_list|()
block|{
return|return
name|getCompiledPermissions
argument_list|()
operator|.
name|getRepositoryPermission
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|TreePermission
name|getTreePermission
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|TreePermission
name|parentPermission
parameter_list|)
block|{
return|return
name|getCompiledPermissions
argument_list|()
operator|.
name|getTreePermission
argument_list|(
name|PermissionUtil
operator|.
name|getImmutableTree
argument_list|(
name|tree
argument_list|,
name|immutableRoot
argument_list|)
argument_list|,
name|parentPermission
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
return|return
name|getCompiledPermissions
argument_list|()
operator|.
name|isGranted
argument_list|(
name|PermissionUtil
operator|.
name|getImmutableTree
argument_list|(
name|tree
argument_list|,
name|immutableRoot
argument_list|)
argument_list|,
name|property
argument_list|,
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
name|TreeLocation
operator|.
name|create
argument_list|(
name|immutableRoot
argument_list|,
name|oakPath
argument_list|)
decl_stmt|;
name|boolean
name|isAcContent
init|=
name|ctx
operator|.
name|definesLocation
argument_list|(
name|location
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
argument_list|,
name|isAcContent
argument_list|)
decl_stmt|;
return|return
name|isGranted
argument_list|(
name|location
argument_list|,
name|oakPath
argument_list|,
name|permissions
argument_list|)
return|;
block|}
comment|//---------------------------------------< AggregatedPermissionProvider>---
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PrivilegeBits
name|supportedPrivileges
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PrivilegeBits
name|privilegeBits
parameter_list|)
block|{
return|return
operator|(
name|privilegeBits
operator|!=
literal|null
operator|)
condition|?
name|privilegeBits
else|:
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|immutableRoot
argument_list|)
operator|.
name|getBits
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|supportedPermissions
parameter_list|(
annotation|@
name|Nullable
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
return|return
name|permissions
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|supportedPermissions
parameter_list|(
annotation|@
name|Nonnull
name|TreeLocation
name|location
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|permissions
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|supportedPermissions
parameter_list|(
annotation|@
name|Nonnull
name|TreePermission
name|treePermission
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
return|return
name|permissions
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
name|TreeLocation
name|location
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|isGranted
argument_list|(
name|location
argument_list|,
name|location
operator|.
name|getPath
argument_list|()
argument_list|,
name|permissions
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|TreePermission
name|getTreePermission
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|TreeType
name|type
parameter_list|,
annotation|@
name|Nonnull
name|TreePermission
name|parentPermission
parameter_list|)
block|{
return|return
name|getCompiledPermissions
argument_list|()
operator|.
name|getTreePermission
argument_list|(
name|PermissionUtil
operator|.
name|getImmutableTree
argument_list|(
name|tree
argument_list|,
name|immutableRoot
argument_list|)
argument_list|,
name|type
argument_list|,
name|parentPermission
argument_list|)
return|;
block|}
comment|//--------------------------------------------------------------------------
specifier|private
name|CompiledPermissions
name|getCompiledPermissions
parameter_list|()
block|{
name|CompiledPermissions
name|cp
init|=
name|compiledPermissions
decl_stmt|;
if|if
condition|(
name|cp
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|PermissionUtil
operator|.
name|isAdminOrSystem
argument_list|(
name|principals
argument_list|,
name|options
argument_list|)
condition|)
block|{
name|cp
operator|=
name|AllPermissions
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|cp
operator|=
name|CompiledPermissionImpl
operator|.
name|create
argument_list|(
name|immutableRoot
argument_list|,
name|workspaceName
argument_list|,
name|principals
argument_list|,
name|restrictionProvider
argument_list|,
name|options
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
block|}
name|compiledPermissions
operator|=
name|cp
expr_stmt|;
block|}
return|return
name|cp
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isVersionStorePath
parameter_list|(
annotation|@
name|Nonnull
name|String
name|oakPath
parameter_list|)
block|{
return|return
name|oakPath
operator|.
name|startsWith
argument_list|(
name|VersionConstants
operator|.
name|VERSION_STORE_PATH
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isGranted
parameter_list|(
annotation|@
name|Nonnull
name|TreeLocation
name|location
parameter_list|,
annotation|@
name|Nonnull
name|String
name|oakPath
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
name|boolean
name|isGranted
init|=
literal|false
decl_stmt|;
name|PropertyState
name|property
init|=
name|location
operator|.
name|getProperty
argument_list|()
decl_stmt|;
name|Tree
name|tree
init|=
operator|(
name|property
operator|==
literal|null
operator|)
condition|?
name|location
operator|.
name|getTree
argument_list|()
else|:
name|location
operator|.
name|getParent
argument_list|()
operator|.
name|getTree
argument_list|()
decl_stmt|;
if|if
condition|(
name|tree
operator|!=
literal|null
condition|)
block|{
name|isGranted
operator|=
name|isGranted
argument_list|(
name|tree
argument_list|,
name|property
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|isVersionStorePath
argument_list|(
name|location
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|isGranted
operator|=
name|getCompiledPermissions
argument_list|()
operator|.
name|isGranted
argument_list|(
name|oakPath
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
block|}
return|return
name|isGranted
return|;
block|}
block|}
end_class

end_unit

