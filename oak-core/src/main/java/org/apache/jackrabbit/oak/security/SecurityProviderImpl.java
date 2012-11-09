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
name|Collections
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
name|List
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
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
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
name|api
operator|.
name|security
operator|.
name|principal
operator|.
name|PrincipalManager
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
name|security
operator|.
name|authentication
operator|.
name|LoginContextProviderImpl
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
name|TokenProviderImpl
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
name|AccessControlConfigurationImpl
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
name|PrincipalManagerImpl
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
name|PrincipalProviderImpl
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
name|query
operator|.
name|QueryIndexProvider
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
name|LoginContextProvider
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
name|TokenProvider
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
name|principal
operator|.
name|PrincipalProvider
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
name|state
operator|.
name|NodeStore
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

begin_class
specifier|public
class|class
name|SecurityProviderImpl
implements|implements
name|SecurityProvider
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
name|SecurityProviderImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_APP_NAME
init|=
literal|"org.apache.jackrabbit.oak.auth.appName"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_APP_NAME
init|=
literal|"jackrabbit.oak"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_USER_OPTIONS
init|=
literal|"org.apache.jackrabbit.oak.user.options"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_TOKEN_OPTIONS
init|=
literal|"org.apache.jackrabbit.oak.token.options"
decl_stmt|;
specifier|private
specifier|final
name|ConfigurationParameters
name|configuration
decl_stmt|;
specifier|public
name|SecurityProviderImpl
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|ConfigurationParameters
argument_list|()
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
name|Iterable
argument_list|<
name|SecurityConfiguration
argument_list|>
name|getSecurityConfigurations
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
name|getAccessControlProvider
argument_list|()
argument_list|)
expr_stmt|;
name|scs
operator|.
name|add
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|scs
operator|.
name|add
argument_list|(
name|getPrincipalConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|scs
operator|.
name|add
argument_list|(
name|getPrivilegeConfiguration
argument_list|()
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
name|LoginContextProvider
name|getLoginContextProvider
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|QueryIndexProvider
name|indexProvider
parameter_list|)
block|{
name|String
name|appName
init|=
name|configuration
operator|.
name|getConfigValue
argument_list|(
name|PARAM_APP_NAME
argument_list|,
name|DEFAULT_APP_NAME
argument_list|)
decl_stmt|;
name|Configuration
name|loginConfig
decl_stmt|;
try|try
block|{
name|loginConfig
operator|=
name|Configuration
operator|.
name|getConfiguration
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to retrieve login configuration: using default."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|loginConfig
operator|=
operator|new
name|OakConfiguration
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
comment|// TODO: define configuration structure
name|Configuration
operator|.
name|setConfiguration
argument_list|(
name|loginConfig
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LoginContextProviderImpl
argument_list|(
name|appName
argument_list|,
name|loginConfig
argument_list|,
name|nodeStore
argument_list|,
name|indexProvider
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|TokenProvider
name|getTokenProvider
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
name|ConfigurationParameters
name|options
init|=
name|configuration
operator|.
name|getConfigValue
argument_list|(
name|PARAM_TOKEN_OPTIONS
argument_list|,
operator|new
name|ConfigurationParameters
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenProviderImpl
argument_list|(
name|root
argument_list|,
name|options
argument_list|,
name|getUserConfiguration
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|AccessControlConfiguration
name|getAccessControlProvider
parameter_list|()
block|{
return|return
operator|new
name|AccessControlConfigurationImpl
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
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
annotation|@
name|Override
specifier|public
name|UserConfiguration
name|getUserConfiguration
parameter_list|()
block|{
name|ConfigurationParameters
name|options
init|=
name|configuration
operator|.
name|getConfigValue
argument_list|(
name|PARAM_USER_OPTIONS
argument_list|,
operator|new
name|ConfigurationParameters
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|UserConfigurationImpl
argument_list|(
name|options
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PrincipalConfiguration
name|getPrincipalConfiguration
parameter_list|()
block|{
return|return
operator|new
name|PrincipalConfigurationImpl
argument_list|()
return|;
block|}
specifier|private
class|class
name|PrincipalConfigurationImpl
extends|extends
name|SecurityConfiguration
operator|.
name|Default
implements|implements
name|PrincipalConfiguration
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PrincipalManager
name|getPrincipalManager
parameter_list|(
name|Session
name|session
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|PrincipalProvider
name|principalProvider
init|=
name|getPrincipalProvider
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
decl_stmt|;
return|return
operator|new
name|PrincipalManagerImpl
argument_list|(
name|principalProvider
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PrincipalProvider
name|getPrincipalProvider
parameter_list|(
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
operator|new
name|PrincipalProviderImpl
argument_list|(
name|root
argument_list|,
name|getUserConfiguration
argument_list|()
argument_list|,
name|namePathMapper
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
name|getValidatorProviders
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
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
name|ProtectedItemImporter
argument_list|>
name|getProtectedItemImporters
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

