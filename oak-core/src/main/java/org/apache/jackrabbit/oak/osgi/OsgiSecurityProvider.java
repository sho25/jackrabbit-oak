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
name|osgi
package|;
end_package

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
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Reference
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
name|osgi
operator|.
name|framework
operator|.
name|ServiceReference
import|;
end_import

begin_comment
comment|/**  * OsgiSecurityProvider... TODO  */
end_comment

begin_class
specifier|public
class|class
name|OsgiSecurityProvider
extends|extends
name|AbstractServiceTracker
argument_list|<
name|SecurityConfiguration
argument_list|>
implements|implements
name|SecurityProvider
block|{
annotation|@
name|Reference
argument_list|(
name|bind
operator|=
literal|"bindAuthorizationConfiguration"
argument_list|)
specifier|private
name|AuthorizationConfiguration
name|authorizationConfiguration
decl_stmt|;
annotation|@
name|Reference
argument_list|(
name|bind
operator|=
literal|"bindAuthenticationConfiguration"
argument_list|)
specifier|private
name|AuthenticationConfiguration
name|authenticationConfiguration
decl_stmt|;
annotation|@
name|Reference
argument_list|(
name|bind
operator|=
literal|"bindPrivilegeConfiguration"
argument_list|)
specifier|private
name|PrivilegeConfiguration
name|privilegeConfiguration
decl_stmt|;
annotation|@
name|Reference
argument_list|(
name|bind
operator|=
literal|"bindUserConfiguration"
argument_list|)
specifier|private
name|UserConfiguration
name|userConfiguration
decl_stmt|;
specifier|private
name|CompositePrincipalConfiguration
name|principalConfiguration
init|=
operator|new
name|CompositePrincipalConfiguration
argument_list|()
decl_stmt|;
specifier|private
name|CompositeTokenConfiguration
name|tokenConfiguration
init|=
operator|new
name|CompositeTokenConfiguration
argument_list|()
decl_stmt|;
specifier|private
name|ConfigurationParameters
name|config
decl_stmt|;
specifier|public
name|OsgiSecurityProvider
parameter_list|(
annotation|@
name|Nonnull
name|ConfigurationParameters
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|SecurityConfiguration
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
comment|//---------------------------------------------------< SecurityProvider>---
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
name|config
return|;
block|}
name|ConfigurationParameters
name|params
init|=
name|config
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
comment|//-------------------------------------------< ServiceTrackerCustomizer>---
annotation|@
name|Override
specifier|public
name|Object
name|addingService
parameter_list|(
name|ServiceReference
name|reference
parameter_list|)
block|{
name|Object
name|service
init|=
name|super
operator|.
name|addingService
argument_list|(
name|reference
argument_list|)
decl_stmt|;
if|if
condition|(
name|service
operator|instanceof
name|TokenConfiguration
condition|)
block|{
name|tokenConfiguration
operator|.
name|addConfiguration
argument_list|(
operator|(
name|TokenConfiguration
operator|)
name|service
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|service
operator|instanceof
name|PrincipalConfiguration
condition|)
block|{
name|principalConfiguration
operator|.
name|addConfiguration
argument_list|(
operator|(
name|PrincipalConfiguration
operator|)
name|service
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|service
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removedService
parameter_list|(
name|ServiceReference
name|reference
parameter_list|,
name|Object
name|service
parameter_list|)
block|{
name|super
operator|.
name|removedService
argument_list|(
name|reference
argument_list|,
name|service
argument_list|)
expr_stmt|;
if|if
condition|(
name|service
operator|instanceof
name|TokenConfiguration
condition|)
block|{
name|tokenConfiguration
operator|.
name|removeConfiguration
argument_list|(
operator|(
name|TokenConfiguration
operator|)
name|service
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|service
operator|instanceof
name|PrincipalConfiguration
condition|)
block|{
name|principalConfiguration
operator|.
name|removeConfiguration
argument_list|(
operator|(
name|PrincipalConfiguration
operator|)
name|service
argument_list|)
expr_stmt|;
block|}
block|}
comment|//--------------------------------------------------------------------------
specifier|protected
name|void
name|bindAuthorizationConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|ServiceReference
name|reference
parameter_list|)
block|{
name|authorizationConfiguration
operator|=
operator|(
name|AuthorizationConfiguration
operator|)
name|initConfiguration
argument_list|(
name|reference
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|bindAuthenticationConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|ServiceReference
name|reference
parameter_list|)
block|{
name|authenticationConfiguration
operator|=
operator|(
name|AuthenticationConfiguration
operator|)
name|initConfiguration
argument_list|(
name|reference
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|bindUserConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|ServiceReference
name|reference
parameter_list|)
block|{
name|userConfiguration
operator|=
operator|(
name|UserConfiguration
operator|)
name|initConfiguration
argument_list|(
name|reference
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|bindPrivilegeConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|ServiceReference
name|reference
parameter_list|)
block|{
name|privilegeConfiguration
operator|=
operator|(
name|PrivilegeConfiguration
operator|)
name|initConfiguration
argument_list|(
name|reference
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Object
name|initConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|ServiceReference
name|reference
parameter_list|)
block|{
name|Object
name|service
init|=
name|reference
operator|.
name|getBundle
argument_list|()
operator|.
name|getBundleContext
argument_list|()
operator|.
name|getService
argument_list|(
name|reference
argument_list|)
decl_stmt|;
if|if
condition|(
name|service
operator|instanceof
name|ConfigurationBase
condition|)
block|{
operator|(
operator|(
name|ConfigurationBase
operator|)
name|service
operator|)
operator|.
name|setSecurityProvider
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|service
return|;
block|}
block|}
end_class

end_unit

