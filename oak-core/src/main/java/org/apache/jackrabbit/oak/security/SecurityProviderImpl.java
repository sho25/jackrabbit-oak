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
package|;
end_package

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
name|ImmutableMap
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
name|osgi
operator|.
name|OsgiWhiteboard
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
name|whiteboard
operator|.
name|Whiteboard
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
name|whiteboard
operator|.
name|WhiteboardAuthorizableActionProvider
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
name|whiteboard
operator|.
name|WhiteboardAuthorizableNodeName
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
name|WhiteboardAware
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
name|WhiteboardRestrictionProvider
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
name|whiteboard
operator|.
name|WhiteboardUserAuthenticationFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
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
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_class
annotation|@
name|Deprecated
specifier|public
class|class
name|SecurityProviderImpl
implements|implements
name|SecurityProvider
implements|,
name|WhiteboardAware
block|{
specifier|private
specifier|volatile
name|AuthenticationConfiguration
name|authenticationConfiguration
decl_stmt|;
specifier|private
specifier|volatile
name|PrivilegeConfiguration
name|privilegeConfiguration
decl_stmt|;
specifier|private
specifier|volatile
name|UserConfiguration
name|userConfiguration
decl_stmt|;
specifier|private
specifier|final
name|CompositeAuthorizationConfiguration
name|authorizationConfiguration
init|=
operator|new
name|CompositeAuthorizationConfiguration
argument_list|(
name|this
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|CompositePrincipalConfiguration
name|principalConfiguration
init|=
operator|new
name|CompositePrincipalConfiguration
argument_list|(
name|this
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|CompositeTokenConfiguration
name|tokenConfiguration
init|=
operator|new
name|CompositeTokenConfiguration
argument_list|(
name|this
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|WhiteboardAuthorizableNodeName
name|authorizableNodeName
init|=
operator|new
name|WhiteboardAuthorizableNodeName
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|WhiteboardAuthorizableActionProvider
name|authorizableActionProvider
init|=
operator|new
name|WhiteboardAuthorizableActionProvider
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|WhiteboardRestrictionProvider
name|restrictionProvider
init|=
operator|new
name|WhiteboardRestrictionProvider
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|WhiteboardUserAuthenticationFactory
name|userAuthenticationFactory
init|=
operator|new
name|WhiteboardUserAuthenticationFactory
argument_list|(
name|UserConfigurationImpl
operator|.
name|getDefaultAuthenticationFactory
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|ConfigurationParameters
name|configuration
decl_stmt|;
specifier|private
name|Whiteboard
name|whiteboard
decl_stmt|;
comment|/**      * Default constructor used in OSGi environments.      */
specifier|public
name|SecurityProviderImpl
parameter_list|()
block|{
name|this
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new {@code SecurityProvider} instance with the given configuration      * parameters.      *      * @param configuration security configuration      */
specifier|public
name|SecurityProviderImpl
parameter_list|(
annotation|@
name|Nonnull
name|ConfigurationParameters
name|configuration
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
name|authenticationConfiguration
operator|=
operator|new
name|AuthenticationConfigurationImpl
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|userConfiguration
operator|=
operator|new
name|UserConfigurationImpl
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|privilegeConfiguration
operator|=
operator|new
name|PrivilegeConfigurationImpl
argument_list|()
expr_stmt|;
name|authorizationConfiguration
operator|.
name|setDefaultConfig
argument_list|(
operator|new
name|AuthorizationConfigurationImpl
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|principalConfiguration
operator|.
name|setDefaultConfig
argument_list|(
operator|new
name|PrincipalConfigurationImpl
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|tokenConfiguration
operator|.
name|setDefaultConfig
argument_list|(
operator|new
name|TokenConfigurationImpl
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setWhiteboard
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
block|}
annotation|@
name|Override
specifier|public
name|Whiteboard
name|getWhiteboard
parameter_list|()
block|{
return|return
name|whiteboard
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|ConfigurationParameters
name|getParameters
parameter_list|(
annotation|@
name|Nullable
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
return|return
name|configuration
return|;
block|}
name|ConfigurationParameters
name|params
init|=
name|configuration
operator|.
name|getConfigValue
argument_list|(
name|name
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
for|for
control|(
name|SecurityConfiguration
name|sc
range|:
name|getConfigurations
argument_list|()
control|)
block|{
if|if
condition|(
name|sc
operator|!=
literal|null
operator|&&
name|sc
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|params
argument_list|,
name|sc
operator|.
name|getParameters
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
name|params
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|SecurityConfiguration
argument_list|>
name|getConfigurations
parameter_list|()
block|{
name|Set
argument_list|<
name|SecurityConfiguration
argument_list|>
name|scs
init|=
operator|new
name|HashSet
argument_list|<
name|SecurityConfiguration
argument_list|>
argument_list|()
decl_stmt|;
name|scs
operator|.
name|add
argument_list|(
name|authenticationConfiguration
argument_list|)
expr_stmt|;
name|scs
operator|.
name|add
argument_list|(
name|authorizationConfiguration
argument_list|)
expr_stmt|;
name|scs
operator|.
name|add
argument_list|(
name|userConfiguration
argument_list|)
expr_stmt|;
name|scs
operator|.
name|add
argument_list|(
name|principalConfiguration
argument_list|)
expr_stmt|;
name|scs
operator|.
name|add
argument_list|(
name|privilegeConfiguration
argument_list|)
expr_stmt|;
name|scs
operator|.
name|add
argument_list|(
name|tokenConfiguration
argument_list|)
expr_stmt|;
return|return
name|scs
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|Class
argument_list|<
name|T
argument_list|>
name|configClass
parameter_list|)
block|{
if|if
condition|(
name|AuthenticationConfiguration
operator|.
name|class
operator|==
name|configClass
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|authenticationConfiguration
return|;
block|}
elseif|else
if|if
condition|(
name|AuthorizationConfiguration
operator|.
name|class
operator|==
name|configClass
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|authorizationConfiguration
return|;
block|}
elseif|else
if|if
condition|(
name|UserConfiguration
operator|.
name|class
operator|==
name|configClass
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|userConfiguration
return|;
block|}
elseif|else
if|if
condition|(
name|PrincipalConfiguration
operator|.
name|class
operator|==
name|configClass
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|principalConfiguration
return|;
block|}
elseif|else
if|if
condition|(
name|PrivilegeConfiguration
operator|.
name|class
operator|==
name|configClass
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|privilegeConfiguration
return|;
block|}
elseif|else
if|if
condition|(
name|TokenConfiguration
operator|.
name|class
operator|==
name|configClass
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|tokenConfiguration
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported security configuration class "
operator|+
name|configClass
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|void
name|activate
parameter_list|(
name|BundleContext
name|context
parameter_list|)
block|{
name|whiteboard
operator|=
operator|new
name|OsgiWhiteboard
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|authorizableActionProvider
operator|.
name|start
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|authorizableNodeName
operator|.
name|start
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|restrictionProvider
operator|.
name|start
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|userAuthenticationFactory
operator|.
name|start
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|initializeConfigurations
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|deactivate
parameter_list|()
block|{
name|authorizableActionProvider
operator|.
name|stop
argument_list|()
expr_stmt|;
name|authorizableNodeName
operator|.
name|stop
argument_list|()
expr_stmt|;
name|restrictionProvider
operator|.
name|stop
argument_list|()
expr_stmt|;
name|userAuthenticationFactory
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|protected
name|void
name|bindPrincipalConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|PrincipalConfiguration
name|reference
parameter_list|)
block|{
name|principalConfiguration
operator|.
name|addConfiguration
argument_list|(
name|initConfiguration
argument_list|(
name|reference
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|protected
name|void
name|unbindPrincipalConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|PrincipalConfiguration
name|reference
parameter_list|)
block|{
name|principalConfiguration
operator|.
name|removeConfiguration
argument_list|(
name|reference
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|protected
name|void
name|bindTokenConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|TokenConfiguration
name|reference
parameter_list|)
block|{
name|tokenConfiguration
operator|.
name|addConfiguration
argument_list|(
name|initConfiguration
argument_list|(
name|reference
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|protected
name|void
name|unbindTokenConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|TokenConfiguration
name|reference
parameter_list|)
block|{
name|tokenConfiguration
operator|.
name|removeConfiguration
argument_list|(
name|reference
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|protected
name|void
name|bindAuthorizationConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|AuthorizationConfiguration
name|reference
parameter_list|)
block|{
name|authorizationConfiguration
operator|.
name|addConfiguration
argument_list|(
name|initConfiguration
argument_list|(
name|reference
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|protected
name|void
name|unbindAuthorizationConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|AuthorizationConfiguration
name|reference
parameter_list|)
block|{
name|authorizationConfiguration
operator|.
name|removeConfiguration
argument_list|(
name|reference
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|void
name|initializeConfigurations
parameter_list|()
block|{
name|initConfiguration
argument_list|(
name|authorizationConfiguration
argument_list|,
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
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|userMap
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
name|UserConstants
operator|.
name|PARAM_AUTHORIZABLE_ACTION_PROVIDER
argument_list|,
name|authorizableActionProvider
argument_list|,
name|UserConstants
operator|.
name|PARAM_AUTHORIZABLE_NODE_NAME
argument_list|,
name|authorizableNodeName
argument_list|,
name|UserConstants
operator|.
name|PARAM_USER_AUTHENTICATION_FACTORY
argument_list|,
name|userAuthenticationFactory
argument_list|)
decl_stmt|;
name|initConfiguration
argument_list|(
name|userConfiguration
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|userMap
argument_list|)
argument_list|)
expr_stmt|;
name|initConfiguration
argument_list|(
name|authenticationConfiguration
argument_list|)
expr_stmt|;
name|initConfiguration
argument_list|(
name|privilegeConfiguration
argument_list|)
expr_stmt|;
block|}
specifier|private
parameter_list|<
name|T
extends|extends
name|SecurityConfiguration
parameter_list|>
name|T
name|initConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|T
name|config
parameter_list|)
block|{
if|if
condition|(
name|config
operator|instanceof
name|ConfigurationBase
condition|)
block|{
name|ConfigurationBase
name|cfg
init|=
operator|(
name|ConfigurationBase
operator|)
name|config
decl_stmt|;
name|cfg
operator|.
name|setSecurityProvider
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setParameters
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
name|cfg
operator|.
name|getParameters
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|config
return|;
block|}
specifier|private
parameter_list|<
name|T
extends|extends
name|SecurityConfiguration
parameter_list|>
name|T
name|initConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|T
name|config
parameter_list|,
annotation|@
name|Nonnull
name|ConfigurationParameters
name|params
parameter_list|)
block|{
if|if
condition|(
name|config
operator|instanceof
name|ConfigurationBase
condition|)
block|{
name|ConfigurationBase
name|cfg
init|=
operator|(
name|ConfigurationBase
operator|)
name|config
decl_stmt|;
name|cfg
operator|.
name|setSecurityProvider
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setParameters
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|params
argument_list|,
name|cfg
operator|.
name|getParameters
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|config
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|protected
name|void
name|bindAuthenticationConfiguration
parameter_list|(
name|AuthenticationConfiguration
name|authenticationConfiguration
parameter_list|)
block|{
name|this
operator|.
name|authenticationConfiguration
operator|=
name|authenticationConfiguration
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|protected
name|void
name|unbindAuthenticationConfiguration
parameter_list|(
name|AuthenticationConfiguration
name|authenticationConfiguration
parameter_list|)
block|{
name|this
operator|.
name|authenticationConfiguration
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|protected
name|void
name|bindPrivilegeConfiguration
parameter_list|(
name|PrivilegeConfiguration
name|privilegeConfiguration
parameter_list|)
block|{
name|this
operator|.
name|privilegeConfiguration
operator|=
name|privilegeConfiguration
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|protected
name|void
name|unbindPrivilegeConfiguration
parameter_list|(
name|PrivilegeConfiguration
name|privilegeConfiguration
parameter_list|)
block|{
name|this
operator|.
name|privilegeConfiguration
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|protected
name|void
name|bindUserConfiguration
parameter_list|(
name|UserConfiguration
name|userConfiguration
parameter_list|)
block|{
name|this
operator|.
name|userConfiguration
operator|=
name|userConfiguration
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|protected
name|void
name|unbindUserConfiguration
parameter_list|(
name|UserConfiguration
name|userConfiguration
parameter_list|)
block|{
name|this
operator|.
name|userConfiguration
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

