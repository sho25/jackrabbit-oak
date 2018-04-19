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
name|internal
package|;
end_package

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
name|security
operator|.
name|internal
operator|.
name|ConfigurationInitializer
operator|.
name|initializeConfiguration
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
name|security
operator|.
name|internal
operator|.
name|ConfigurationInitializer
operator|.
name|initializeConfigurations
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
name|security
operator|.
name|ConfigurationParameters
operator|.
name|EMPTY
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
name|plugins
operator|.
name|tree
operator|.
name|impl
operator|.
name|RootProviderService
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
name|TreeProviderService
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
name|authentication
operator|.
name|AuthenticationConfigurationImpl
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
name|authentication
operator|.
name|token
operator|.
name|TokenConfigurationImpl
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
name|AuthorizationConfigurationImpl
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
name|composite
operator|.
name|CompositeAuthorizationConfiguration
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
name|security
operator|.
name|principal
operator|.
name|PrincipalConfigurationImpl
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
name|PrivilegeConfigurationImpl
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
name|user
operator|.
name|UserConfigurationImpl
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
name|authentication
operator|.
name|AuthenticationConfiguration
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
name|authentication
operator|.
name|token
operator|.
name|CompositeTokenConfiguration
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
name|authentication
operator|.
name|token
operator|.
name|TokenConfiguration
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
name|principal
operator|.
name|CompositePrincipalConfiguration
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
name|PrincipalConfiguration
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
name|PrivilegeConfiguration
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
name|user
operator|.
name|AuthorizableNodeName
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
name|user
operator|.
name|UserAuthenticationFactory
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
name|user
operator|.
name|UserConfiguration
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
name|user
operator|.
name|UserConstants
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
name|user
operator|.
name|action
operator|.
name|AuthorizableActionProvider
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
name|user
operator|.
name|action
operator|.
name|DefaultAuthorizableActionProvider
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
name|whiteboard
operator|.
name|Whiteboard
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|SecurityProviderBuilder
block|{
specifier|private
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|private
name|RootProvider
name|rootProvider
decl_stmt|;
specifier|private
name|TreeProvider
name|treeProvider
decl_stmt|;
specifier|private
name|ConfigurationParameters
name|authenticationParams
init|=
name|EMPTY
decl_stmt|;
specifier|private
name|AuthenticationConfiguration
name|authenticationConfiguration
decl_stmt|;
specifier|private
name|ConfigurationParameters
name|privilegeParams
init|=
name|EMPTY
decl_stmt|;
specifier|private
name|PrivilegeConfiguration
name|privilegeConfiguration
decl_stmt|;
specifier|private
name|ConfigurationParameters
name|userParams
init|=
name|EMPTY
decl_stmt|;
specifier|private
name|UserConfiguration
name|userConfiguration
decl_stmt|;
specifier|private
name|ConfigurationParameters
name|authorizationParams
init|=
name|EMPTY
decl_stmt|;
specifier|private
name|CompositeAuthorizationConfiguration
name|authorizationConfiguration
decl_stmt|;
specifier|private
name|ConfigurationParameters
name|principalParams
init|=
name|EMPTY
decl_stmt|;
specifier|private
name|CompositePrincipalConfiguration
name|principalConfiguration
decl_stmt|;
specifier|private
name|ConfigurationParameters
name|tokenParams
init|=
name|EMPTY
decl_stmt|;
specifier|private
name|CompositeTokenConfiguration
name|tokenConfiguration
decl_stmt|;
specifier|private
name|ConfigurationParameters
name|configuration
decl_stmt|;
annotation|@
name|Nonnull
specifier|public
specifier|static
name|SecurityProviderBuilder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|SecurityProviderBuilder
argument_list|()
return|;
block|}
specifier|private
name|SecurityProviderBuilder
parameter_list|()
block|{
name|this
operator|.
name|configuration
operator|=
name|ConfigurationParameters
operator|.
name|EMPTY
expr_stmt|;
block|}
specifier|public
name|SecurityProviderBuilder
name|with
parameter_list|(
annotation|@
name|Nonnull
name|ConfigurationParameters
name|configuration
parameter_list|)
block|{
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
name|authenticationParams
operator|=
name|configuration
operator|.
name|getConfigValue
argument_list|(
name|AuthenticationConfiguration
operator|.
name|NAME
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
name|privilegeParams
operator|=
name|configuration
operator|.
name|getConfigValue
argument_list|(
name|PrivilegeConfiguration
operator|.
name|NAME
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
if|if
condition|(
name|configuration
operator|.
name|contains
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|)
condition|)
block|{
name|userParams
operator|=
name|configuration
operator|.
name|getConfigValue
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|AuthorizableActionProvider
name|authorizableActionProvider
init|=
operator|new
name|DefaultAuthorizableActionProvider
argument_list|()
decl_stmt|;
name|AuthorizableNodeName
name|authorizableNodeName
init|=
name|AuthorizableNodeName
operator|.
name|DEFAULT
decl_stmt|;
name|UserAuthenticationFactory
name|userAuthenticationFactory
init|=
name|UserConfigurationImpl
operator|.
name|getDefaultAuthenticationFactory
argument_list|()
decl_stmt|;
name|userParams
operator|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConstants
operator|.
name|PARAM_AUTHORIZABLE_ACTION_PROVIDER
argument_list|,
name|authorizableActionProvider
argument_list|)
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConstants
operator|.
name|PARAM_AUTHORIZABLE_NODE_NAME
argument_list|,
name|authorizableNodeName
argument_list|)
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConstants
operator|.
name|PARAM_USER_AUTHENTICATION_FACTORY
argument_list|,
name|userAuthenticationFactory
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|configuration
operator|.
name|contains
argument_list|(
name|AuthorizationConfiguration
operator|.
name|NAME
argument_list|)
condition|)
block|{
name|authorizationParams
operator|=
name|configuration
operator|.
name|getConfigValue
argument_list|(
name|AuthorizationConfiguration
operator|.
name|NAME
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|RestrictionProvider
name|restrictionProvider
init|=
operator|new
name|RestrictionProviderImpl
argument_list|()
decl_stmt|;
name|authorizationParams
operator|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|AccessControlConstants
operator|.
name|PARAM_RESTRICTION_PROVIDER
argument_list|,
name|restrictionProvider
argument_list|)
expr_stmt|;
block|}
name|principalParams
operator|=
name|configuration
operator|.
name|getConfigValue
argument_list|(
name|PrincipalConfiguration
operator|.
name|NAME
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
name|tokenParams
operator|=
name|configuration
operator|.
name|getConfigValue
argument_list|(
name|TokenConfiguration
operator|.
name|NAME
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|SecurityProviderBuilder
name|with
parameter_list|(
annotation|@
name|Nonnull
name|AuthenticationConfiguration
name|authenticationConfiguration
parameter_list|,
annotation|@
name|Nonnull
name|ConfigurationParameters
name|authenticationParams
parameter_list|,
annotation|@
name|Nonnull
name|PrivilegeConfiguration
name|privilegeConfiguration
parameter_list|,
annotation|@
name|Nonnull
name|ConfigurationParameters
name|privilegeParams
parameter_list|,
annotation|@
name|Nonnull
name|UserConfiguration
name|userConfiguration
parameter_list|,
annotation|@
name|Nonnull
name|ConfigurationParameters
name|userParams
parameter_list|,
annotation|@
name|Nonnull
name|CompositeAuthorizationConfiguration
name|authorizationConfiguration
parameter_list|,
annotation|@
name|Nonnull
name|ConfigurationParameters
name|authorizationParams
parameter_list|,
annotation|@
name|Nonnull
name|CompositePrincipalConfiguration
name|principalConfiguration
parameter_list|,
annotation|@
name|Nonnull
name|ConfigurationParameters
name|principalParams
parameter_list|,
annotation|@
name|Nonnull
name|CompositeTokenConfiguration
name|tokenConfiguration
parameter_list|,
annotation|@
name|Nonnull
name|ConfigurationParameters
name|tokenParams
parameter_list|)
block|{
name|this
operator|.
name|authenticationConfiguration
operator|=
name|authenticationConfiguration
expr_stmt|;
name|this
operator|.
name|authenticationParams
operator|=
name|authenticationParams
expr_stmt|;
name|this
operator|.
name|privilegeConfiguration
operator|=
name|privilegeConfiguration
expr_stmt|;
name|this
operator|.
name|privilegeParams
operator|=
name|privilegeParams
expr_stmt|;
name|this
operator|.
name|userConfiguration
operator|=
name|userConfiguration
expr_stmt|;
name|this
operator|.
name|userParams
operator|=
name|userParams
expr_stmt|;
name|this
operator|.
name|authorizationConfiguration
operator|=
name|authorizationConfiguration
expr_stmt|;
name|this
operator|.
name|authorizationParams
operator|=
name|authorizationParams
expr_stmt|;
name|this
operator|.
name|principalConfiguration
operator|=
name|principalConfiguration
expr_stmt|;
name|this
operator|.
name|principalParams
operator|=
name|principalParams
expr_stmt|;
name|this
operator|.
name|tokenConfiguration
operator|=
name|tokenConfiguration
expr_stmt|;
name|this
operator|.
name|tokenParams
operator|=
name|tokenParams
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|SecurityProvider
name|build
parameter_list|()
block|{
name|InternalSecurityProvider
name|securityProvider
init|=
operator|new
name|InternalSecurityProvider
argument_list|()
decl_stmt|;
if|if
condition|(
name|rootProvider
operator|==
literal|null
condition|)
block|{
name|rootProvider
operator|=
operator|new
name|RootProviderService
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|treeProvider
operator|==
literal|null
condition|)
block|{
name|treeProvider
operator|=
operator|new
name|TreeProviderService
argument_list|()
expr_stmt|;
block|}
comment|// authentication
if|if
condition|(
name|authenticationConfiguration
operator|==
literal|null
condition|)
block|{
name|authenticationConfiguration
operator|=
operator|new
name|AuthenticationConfigurationImpl
argument_list|()
expr_stmt|;
block|}
name|securityProvider
operator|.
name|setAuthenticationConfiguration
argument_list|(
name|initializeConfiguration
argument_list|(
name|authenticationConfiguration
argument_list|,
name|securityProvider
argument_list|,
name|authenticationParams
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
argument_list|)
expr_stmt|;
comment|// privilege
if|if
condition|(
name|privilegeConfiguration
operator|==
literal|null
condition|)
block|{
name|privilegeConfiguration
operator|=
operator|new
name|PrivilegeConfigurationImpl
argument_list|()
expr_stmt|;
block|}
name|securityProvider
operator|.
name|setPrivilegeConfiguration
argument_list|(
name|initializeConfiguration
argument_list|(
name|privilegeConfiguration
argument_list|,
name|securityProvider
argument_list|,
name|privilegeParams
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
argument_list|)
expr_stmt|;
comment|// user
if|if
condition|(
name|userConfiguration
operator|==
literal|null
condition|)
block|{
name|userConfiguration
operator|=
operator|new
name|UserConfigurationImpl
argument_list|()
expr_stmt|;
block|}
name|securityProvider
operator|.
name|setUserConfiguration
argument_list|(
name|initializeConfiguration
argument_list|(
name|userConfiguration
argument_list|,
name|securityProvider
argument_list|,
name|userParams
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
argument_list|)
expr_stmt|;
comment|// authorization
if|if
condition|(
name|authorizationConfiguration
operator|==
literal|null
condition|)
block|{
name|authorizationConfiguration
operator|=
operator|new
name|CompositeAuthorizationConfiguration
argument_list|()
expr_stmt|;
operator|(
operator|(
name|CompositeAuthorizationConfiguration
operator|)
name|authorizationConfiguration
operator|)
operator|.
name|withCompositionType
argument_list|(
name|configuration
operator|.
name|getConfigValue
argument_list|(
literal|"authorizationCompositionType"
argument_list|,
name|CompositeAuthorizationConfiguration
operator|.
name|CompositionType
operator|.
name|AND
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|authorizationConfiguration
operator|.
name|setDefaultConfig
argument_list|(
name|initializeConfiguration
argument_list|(
operator|new
name|AuthorizationConfigurationImpl
argument_list|()
argument_list|,
name|securityProvider
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|initializeConfigurations
argument_list|(
name|authorizationConfiguration
argument_list|,
name|securityProvider
argument_list|,
name|authorizationParams
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
expr_stmt|;
name|securityProvider
operator|.
name|setAuthorizationConfiguration
argument_list|(
name|authorizationConfiguration
argument_list|)
expr_stmt|;
comment|// principal
if|if
condition|(
name|principalConfiguration
operator|==
literal|null
condition|)
block|{
name|principalConfiguration
operator|=
operator|new
name|CompositePrincipalConfiguration
argument_list|()
expr_stmt|;
name|principalConfiguration
operator|.
name|setDefaultConfig
argument_list|(
name|initializeConfiguration
argument_list|(
operator|new
name|PrincipalConfigurationImpl
argument_list|()
argument_list|,
name|securityProvider
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|initializeConfigurations
argument_list|(
name|principalConfiguration
argument_list|,
name|securityProvider
argument_list|,
name|principalParams
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
expr_stmt|;
name|securityProvider
operator|.
name|setPrincipalConfiguration
argument_list|(
name|principalConfiguration
argument_list|)
expr_stmt|;
comment|// token
if|if
condition|(
name|tokenConfiguration
operator|==
literal|null
condition|)
block|{
name|tokenConfiguration
operator|=
operator|new
name|CompositeTokenConfiguration
argument_list|()
expr_stmt|;
name|tokenConfiguration
operator|.
name|setDefaultConfig
argument_list|(
name|initializeConfiguration
argument_list|(
operator|new
name|TokenConfigurationImpl
argument_list|()
argument_list|,
name|securityProvider
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|initializeConfigurations
argument_list|(
name|tokenConfiguration
argument_list|,
name|securityProvider
argument_list|,
name|tokenParams
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
expr_stmt|;
name|securityProvider
operator|.
name|setTokenConfiguration
argument_list|(
name|tokenConfiguration
argument_list|)
expr_stmt|;
comment|// whiteboard
if|if
condition|(
name|whiteboard
operator|!=
literal|null
condition|)
block|{
name|securityProvider
operator|.
name|setWhiteboard
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
block|}
return|return
name|securityProvider
return|;
block|}
specifier|public
name|SecurityProviderBuilder
name|withWhiteboard
parameter_list|(
annotation|@
name|Nonnull
name|Whiteboard
name|whiteboard
parameter_list|)
block|{
name|this
operator|.
name|whiteboard
operator|=
name|whiteboard
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|SecurityProviderBuilder
name|withRootProvider
parameter_list|(
annotation|@
name|Nonnull
name|RootProvider
name|rootProvider
parameter_list|)
block|{
name|this
operator|.
name|rootProvider
operator|=
name|rootProvider
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|SecurityProviderBuilder
name|withTreeProvider
parameter_list|(
annotation|@
name|Nonnull
name|TreeProvider
name|treeProvider
parameter_list|)
block|{
name|this
operator|.
name|treeProvider
operator|=
name|treeProvider
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

