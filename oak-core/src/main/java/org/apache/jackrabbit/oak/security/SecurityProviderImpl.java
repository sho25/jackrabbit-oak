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
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_class
specifier|public
class|class
name|SecurityProviderImpl
implements|implements
name|SecurityProvider
block|{
specifier|private
specifier|final
name|ConfigurationParameters
name|configuration
decl_stmt|;
comment|// we only need 1 instance of authorization config.
comment|// todo: maybe provide general mechanism to singletons of configs
specifier|private
name|AuthorizationConfiguration
name|authorizationConfiguration
decl_stmt|;
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
specifier|public
name|SecurityProviderImpl
parameter_list|(
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
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|ConfigurationParameters
name|getParameters
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|(
name|name
operator|==
literal|null
operator|)
condition|?
name|configuration
else|:
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
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|getAuthenticationConfiguration
argument_list|()
argument_list|,
name|getAuthorizationConfiguration
argument_list|()
argument_list|,
name|getUserConfiguration
argument_list|()
argument_list|,
name|getPrincipalConfiguration
argument_list|()
argument_list|,
name|getPrivilegeConfiguration
argument_list|()
argument_list|,
name|getTokenConfiguration
argument_list|()
argument_list|)
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
name|getAuthenticationConfiguration
argument_list|()
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
name|getAuthorizationConfiguration
argument_list|()
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
name|getUserConfiguration
argument_list|()
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
name|getPrincipalConfiguration
argument_list|()
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
name|getPrivilegeConfiguration
argument_list|()
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
name|getTokenConfiguration
argument_list|()
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
annotation|@
name|Nonnull
specifier|private
name|AuthenticationConfiguration
name|getAuthenticationConfiguration
parameter_list|()
block|{
return|return
operator|new
name|AuthenticationConfigurationImpl
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|AuthorizationConfiguration
name|getAuthorizationConfiguration
parameter_list|()
block|{
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
name|AuthorizationConfigurationImpl
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|authorizationConfiguration
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|PrivilegeConfiguration
name|getPrivilegeConfiguration
parameter_list|()
block|{
return|return
operator|new
name|PrivilegeConfigurationImpl
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|UserConfiguration
name|getUserConfiguration
parameter_list|()
block|{
return|return
operator|new
name|UserConfigurationImpl
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|PrincipalConfiguration
name|getPrincipalConfiguration
parameter_list|()
block|{
return|return
operator|new
name|PrincipalConfigurationImpl
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|TokenConfiguration
name|getTokenConfiguration
parameter_list|()
block|{
return|return
operator|new
name|TokenConfigurationImpl
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

