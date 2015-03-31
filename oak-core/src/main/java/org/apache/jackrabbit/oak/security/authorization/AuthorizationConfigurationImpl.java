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
name|jcr
operator|.
name|security
operator|.
name|AccessControlManager
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
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|PropertyOption
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|nodetype
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
name|plugins
operator|.
name|version
operator|.
name|VersionablePathHook
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
name|accesscontrol
operator|.
name|AccessControlImporter
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
name|accesscontrol
operator|.
name|AccessControlManagerImpl
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
name|accesscontrol
operator|.
name|AccessControlValidatorProvider
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
name|PermissionHook
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
name|PermissionProviderImpl
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
name|PermissionStoreValidatorProvider
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
name|PermissionValidatorProvider
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
name|restriction
operator|.
name|RestrictionProviderImpl
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
name|CommitHook
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
name|ValidatorProvider
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
name|lifecycle
operator|.
name|WorkspaceInitializer
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
name|CompositeConfiguration
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
name|ConfigurationBase
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
name|SecurityConfiguration
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
name|AuthorizationConfiguration
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
name|xml
operator|.
name|ImportBehavior
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
name|xml
operator|.
name|ProtectedItemImporter
import|;
end_import

begin_comment
comment|/**  * Default implementation of the {@code AccessControlConfiguration}.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|metatype
operator|=
literal|true
argument_list|,
name|label
operator|=
literal|"Apache Jackrabbit Oak AuthorizationConfiguration"
argument_list|)
annotation|@
name|Service
argument_list|(
block|{
name|AuthorizationConfiguration
operator|.
name|class
block|,
name|SecurityConfiguration
operator|.
name|class
block|}
argument_list|)
annotation|@
name|Properties
argument_list|(
block|{
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|PermissionConstants
operator|.
name|PARAM_PERMISSIONS_JR2
argument_list|,
name|label
operator|=
literal|"Jackrabbit 2.x Permissions"
argument_list|,
name|description
operator|=
literal|"Enforce backwards compatible permission validation with respect to the configurable options."
argument_list|,
name|cardinality
operator|=
literal|2
argument_list|,
name|options
operator|=
block|{
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
literal|"USER_MANAGEMENT"
argument_list|,
name|value
operator|=
literal|"USER_MANAGEMENT"
argument_list|)
block|,
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
literal|"REMOVE_NODE"
argument_list|,
name|value
operator|=
literal|"REMOVE_NODE"
argument_list|)
block|}
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|ProtectedItemImporter
operator|.
name|PARAM_IMPORT_BEHAVIOR
argument_list|,
name|label
operator|=
literal|"Import Behavior"
argument_list|,
name|description
operator|=
literal|"Behavior for access control related items upon XML import."
argument_list|,
name|options
operator|=
block|{
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
name|ImportBehavior
operator|.
name|NAME_ABORT
argument_list|,
name|value
operator|=
name|ImportBehavior
operator|.
name|NAME_ABORT
argument_list|)
block|,
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
name|ImportBehavior
operator|.
name|NAME_BESTEFFORT
argument_list|,
name|value
operator|=
name|ImportBehavior
operator|.
name|NAME_BESTEFFORT
argument_list|)
block|,
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
name|ImportBehavior
operator|.
name|NAME_IGNORE
argument_list|,
name|value
operator|=
name|ImportBehavior
operator|.
name|NAME_IGNORE
argument_list|)
block|}
argument_list|,
name|value
operator|=
name|ImportBehavior
operator|.
name|NAME_ABORT
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|PermissionConstants
operator|.
name|PARAM_READ_PATHS
argument_list|,
name|label
operator|=
literal|"Readable Paths"
argument_list|,
name|description
operator|=
literal|"Enable full read access to regular nodes and properties at the specified paths irrespective of other policies that may take effective."
argument_list|,
name|value
operator|=
block|{
name|NamespaceConstants
operator|.
name|NAMESPACES_PATH
block|,
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
block|,
name|PrivilegeConstants
operator|.
name|PRIVILEGES_PATH
block|}
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|PermissionConstants
operator|.
name|PARAM_ADMINISTRATIVE_PRINCIPALS
argument_list|,
name|label
operator|=
literal|"Administrative Principals"
argument_list|,
name|description
operator|=
literal|"Allows to specify principals that should be granted full permissions on the complete repository content."
argument_list|,
name|cardinality
operator|=
literal|10
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|CompositeConfiguration
operator|.
name|PARAM_RANKING
argument_list|,
name|label
operator|=
literal|"Ranking"
argument_list|,
name|description
operator|=
literal|"Ranking of this configuration in a setup with multiple authorization configurations."
argument_list|,
name|intValue
operator|=
literal|100
argument_list|)
block|}
argument_list|)
specifier|public
class|class
name|AuthorizationConfigurationImpl
extends|extends
name|ConfigurationBase
implements|implements
name|AuthorizationConfiguration
block|{
specifier|public
name|AuthorizationConfigurationImpl
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
annotation|@
name|Activate
specifier|private
name|void
name|activate
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
block|{
name|setParameters
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|properties
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AuthorizationConfigurationImpl
parameter_list|(
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|super
argument_list|(
name|securityProvider
argument_list|,
name|securityProvider
operator|.
name|getParameters
argument_list|(
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//----------------------------------------------< SecurityConfiguration>---
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Context
name|getContext
parameter_list|()
block|{
return|return
name|AuthorizationContext
operator|.
name|getInstance
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|WorkspaceInitializer
name|getWorkspaceInitializer
parameter_list|()
block|{
return|return
operator|new
name|AuthorizationInitializer
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|CommitHook
argument_list|>
name|getCommitHooks
parameter_list|(
annotation|@
name|Nonnull
name|String
name|workspaceName
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|VersionablePathHook
argument_list|(
name|workspaceName
argument_list|)
argument_list|,
operator|new
name|PermissionHook
argument_list|(
name|workspaceName
argument_list|,
name|getRestrictionProvider
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|ValidatorProvider
argument_list|>
name|getValidators
parameter_list|(
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
name|MoveTracker
name|moveTracker
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|PermissionStoreValidatorProvider
argument_list|()
argument_list|,
operator|new
name|PermissionValidatorProvider
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|,
name|workspaceName
argument_list|,
name|principals
argument_list|,
name|moveTracker
argument_list|)
argument_list|,
operator|new
name|AccessControlValidatorProvider
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|ProtectedItemImporter
argument_list|>
name|getProtectedItemImporters
parameter_list|()
block|{
return|return
name|Collections
operator|.
expr|<
name|ProtectedItemImporter
operator|>
name|singletonList
argument_list|(
operator|new
name|AccessControlImporter
argument_list|()
argument_list|)
return|;
block|}
comment|//-----------------------------------------< AccessControlConfiguration>---
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|AccessControlManager
name|getAccessControlManager
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
operator|new
name|AccessControlManagerImpl
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RestrictionProvider
name|getRestrictionProvider
parameter_list|()
block|{
name|RestrictionProvider
name|restrictionProvider
init|=
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|AccessControlConstants
operator|.
name|PARAM_RESTRICTION_PROVIDER
argument_list|,
literal|null
argument_list|,
name|RestrictionProvider
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|restrictionProvider
operator|==
literal|null
condition|)
block|{
comment|// default
name|restrictionProvider
operator|=
operator|new
name|RestrictionProviderImpl
argument_list|()
expr_stmt|;
block|}
return|return
name|restrictionProvider
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PermissionProvider
name|getPermissionProvider
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
parameter_list|)
block|{
return|return
operator|new
name|PermissionProviderImpl
argument_list|(
name|root
argument_list|,
name|workspaceName
argument_list|,
name|principals
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

