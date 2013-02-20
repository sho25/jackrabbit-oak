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
name|authentication
package|;
end_package

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
name|security
operator|.
name|ConfigurationUtil
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
name|state
operator|.
name|NodeStore
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
comment|/**  * AuthenticationConfigurationImpl... TODO  */
end_comment

begin_class
specifier|public
class|class
name|AuthenticationConfigurationImpl
extends|extends
name|SecurityConfiguration
operator|.
name|Default
implements|implements
name|AuthenticationConfiguration
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
name|AuthenticationConfigurationImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_AUTHENTICATION_OPTIONS
init|=
literal|"org.apache.jackrabbit.oak.authentication.options"
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
name|PARAM_TOKEN_OPTIONS
init|=
literal|"org.apache.jackrabbit.oak.token.options"
decl_stmt|;
specifier|private
specifier|final
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
specifier|final
name|ConfigurationParameters
name|config
decl_stmt|;
specifier|public
name|AuthenticationConfigurationImpl
parameter_list|(
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|this
operator|.
name|securityProvider
operator|=
name|securityProvider
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|securityProvider
operator|.
name|getConfiguration
argument_list|(
name|PARAM_AUTHENTICATION_OPTIONS
argument_list|)
expr_stmt|;
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
name|CommitHook
name|commitHook
parameter_list|,
name|QueryIndexProvider
name|indexProvider
parameter_list|)
block|{
name|String
name|appName
init|=
name|config
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
init|=
literal|null
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
comment|// FIXME: workaround for Java7 behavior. needs clean up (see OAK-497)
if|if
condition|(
name|loginConfig
operator|.
name|getAppConfigurationEntry
argument_list|(
name|appName
argument_list|)
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No login configuration available for {}; using default"
argument_list|,
name|appName
argument_list|)
expr_stmt|;
name|loginConfig
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Failed to retrieve login configuration: using default. "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|loginConfig
operator|==
literal|null
condition|)
block|{
comment|// TODO: define configuration structure
comment|// TODO: review if having a default is desirable or if login should fail without valid login configuration.
name|loginConfig
operator|=
name|ConfigurationUtil
operator|.
name|getDefaultConfiguration
argument_list|(
name|config
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
name|commitHook
argument_list|,
name|indexProvider
argument_list|,
name|securityProvider
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
name|tokenOptions
init|=
name|config
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
name|tokenOptions
argument_list|,
name|securityProvider
operator|.
name|getUserConfiguration
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

