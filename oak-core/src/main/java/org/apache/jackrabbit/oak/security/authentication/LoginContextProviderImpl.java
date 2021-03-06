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
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchProviderException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Credentials
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
name|Subject
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
name|callback
operator|.
name|CallbackHandler
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
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
name|ContentRepository
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|JaasLoginContext
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
name|LoginContext
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
name|LoginModuleMonitor
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
name|PreAuthContext
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
operator|.
name|PARAM_CONFIG_SPI_NAME
import|;
end_import

begin_comment
comment|/**  * {@code LoginContextProvider}  */
end_comment

begin_class
class|class
name|LoginContextProviderImpl
implements|implements
name|LoginContextProvider
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
name|LoginContextProviderImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|appName
decl_stmt|;
specifier|private
specifier|final
name|ConfigurationParameters
name|params
decl_stmt|;
specifier|private
specifier|final
name|ContentRepository
name|contentRepository
decl_stmt|;
specifier|private
specifier|final
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
specifier|final
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|private
specifier|final
name|LoginModuleMonitor
name|loginModuleMonitor
decl_stmt|;
specifier|private
name|Configuration
name|configuration
decl_stmt|;
name|LoginContextProviderImpl
parameter_list|(
name|String
name|appName
parameter_list|,
name|ConfigurationParameters
name|params
parameter_list|,
name|ContentRepository
name|contentRepository
parameter_list|,
name|SecurityProvider
name|securityProvider
parameter_list|,
name|Whiteboard
name|whiteboard
parameter_list|,
name|LoginModuleMonitor
name|loginModuleMonitor
parameter_list|)
block|{
name|this
operator|.
name|appName
operator|=
name|appName
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|contentRepository
operator|=
name|contentRepository
expr_stmt|;
name|this
operator|.
name|securityProvider
operator|=
name|securityProvider
expr_stmt|;
name|this
operator|.
name|whiteboard
operator|=
name|whiteboard
expr_stmt|;
name|this
operator|.
name|loginModuleMonitor
operator|=
name|loginModuleMonitor
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|LoginContext
name|getLoginContext
parameter_list|(
name|Credentials
name|credentials
parameter_list|,
name|String
name|workspaceName
parameter_list|)
throws|throws
name|LoginException
block|{
name|Subject
name|subject
init|=
name|getSubject
argument_list|()
decl_stmt|;
if|if
condition|(
name|subject
operator|!=
literal|null
operator|&&
name|credentials
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Found pre-authenticated subject: No further login actions required."
argument_list|)
expr_stmt|;
return|return
operator|new
name|PreAuthContext
argument_list|(
name|subject
argument_list|)
return|;
block|}
if|if
condition|(
name|subject
operator|==
literal|null
condition|)
block|{
name|subject
operator|=
operator|new
name|Subject
argument_list|()
expr_stmt|;
block|}
name|CallbackHandler
name|handler
init|=
name|getCallbackHandler
argument_list|(
name|credentials
argument_list|,
name|workspaceName
argument_list|)
decl_stmt|;
return|return
operator|new
name|JaasLoginContext
argument_list|(
name|appName
argument_list|,
name|subject
argument_list|,
name|handler
argument_list|,
name|getConfiguration
argument_list|()
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< private>---
annotation|@
name|Nullable
specifier|private
specifier|static
name|Subject
name|getSubject
parameter_list|()
block|{
name|Subject
name|subject
init|=
literal|null
decl_stmt|;
try|try
block|{
name|subject
operator|=
name|Subject
operator|.
name|getSubject
argument_list|(
name|AccessController
operator|.
name|getContext
argument_list|()
argument_list|)
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
name|debug
argument_list|(
literal|"Can't check for pre-authenticated subject. Reason: {}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|subject
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|CallbackHandler
name|getCallbackHandler
parameter_list|(
name|Credentials
name|credentials
parameter_list|,
name|String
name|workspaceName
parameter_list|)
block|{
return|return
operator|new
name|CallbackHandlerImpl
argument_list|(
name|credentials
argument_list|,
name|workspaceName
argument_list|,
name|contentRepository
argument_list|,
name|securityProvider
argument_list|,
name|whiteboard
argument_list|,
name|loginModuleMonitor
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|Configuration
name|getConfiguration
parameter_list|()
block|{
if|if
condition|(
name|configuration
operator|==
literal|null
condition|)
block|{
name|Configuration
name|loginConfig
init|=
literal|null
decl_stmt|;
name|String
name|configSpiName
init|=
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_CONFIG_SPI_NAME
argument_list|,
literal|null
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|configSpiName
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|/*                      Create a configuration instance with the following characteristics                      - Algorithm name : "JavaLoginConfig"                      - Extra parameters : 'null' for this impl                      - Name of the config provider : 'configSpiName' as retrieved from the PARAM_CONFIG_SPI_NAME configuration (default: null)                      */
name|loginConfig
operator|=
name|Configuration
operator|.
name|getInstance
argument_list|(
literal|"JavaLoginConfig"
argument_list|,
literal|null
argument_list|,
name|configSpiName
argument_list|)
expr_stmt|;
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
name|warn
argument_list|(
literal|"No configuration found for application {} though fetching JAAS "
operator|+
literal|"configuration from SPI {} is enabled."
argument_list|,
name|appName
argument_list|,
name|configSpiName
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
decl||
name|NoSuchProviderException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error fetching JAAS config from SPI {}"
argument_list|,
name|configSpiName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|loginConfig
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|loginConfig
operator|=
name|Configuration
operator|.
name|getConfiguration
argument_list|()
expr_stmt|;
comment|// NOTE: workaround for Java7 behavior (see OAK-497)
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
literal|"Failed to retrieve login configuration: using default. {}"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|loginConfig
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
name|ConfigurationUtil
operator|.
name|getDefaultConfiguration
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
name|configuration
operator|=
name|loginConfig
expr_stmt|;
block|}
return|return
name|configuration
return|;
block|}
block|}
end_class

end_unit

