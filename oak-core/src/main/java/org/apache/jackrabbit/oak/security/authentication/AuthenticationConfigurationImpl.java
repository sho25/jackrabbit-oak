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
comment|/**  * Default implementation of the {@code AuthenticationConfiguration} with the  * following characteristics:  *  *<ul>  *<li>  *     {@link LoginContextProvider}: Returns the default implementation of  *     {@code LoginContextProvider} that handles standard JAAS based logins and  *     deals with pre-authenticated subjects.</li>  *</ul>  *  */
end_comment

begin_class
annotation|@
name|Component
annotation|@
name|Service
argument_list|(
block|{
name|AuthenticationConfiguration
operator|.
name|class
block|,
name|SecurityConfiguration
operator|.
name|class
block|}
argument_list|)
specifier|public
class|class
name|AuthenticationConfigurationImpl
extends|extends
name|ConfigurationBase
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
comment|/**      * Constructor for OSGi      */
specifier|public
name|AuthenticationConfigurationImpl
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * Constructor for non-OSGi      * @param securityProvider      */
specifier|public
name|AuthenticationConfigurationImpl
parameter_list|(
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|super
argument_list|(
name|securityProvider
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
comment|//----------------------------------------< AuthenticationConfiguration>---
comment|/**      * Create a {@code LoginContextProvider} using standard      * {@link javax.security.auth.login.Configuration#getConfiguration() JAAS}      * functionality. In case no login configuration for the specified app name      * can be retrieve this implementation uses the default as defined by      * {@link ConfigurationUtil#getDefaultConfiguration(org.apache.jackrabbit.oak.spi.security.ConfigurationParameters)}.      *<p>      * The {@link LoginContextProvider} implementation is intended to be used with      *<ul>      *<li>Regular login using JAAS {@link javax.security.auth.spi.LoginModule} or</li>      *<li>Pre-authenticated subjects in which case any authentication      *     related validation is omitted</li>      *</ul>      *      *<h4>Configuration Options</h4>      *<ul>      *<li>{@link #PARAM_APP_NAME}: The appName passed to      *     {@code Configuration#getAppConfigurationEntry(String)}. The default      *     value is {@link #DEFAULT_APP_NAME}.</li>      *</ul>      *      * @param contentRepository The content repository.      * @return An new instance of {@link LoginContextProvider}.      */
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|LoginContextProvider
name|getLoginContextProvider
parameter_list|(
name|ContentRepository
name|contentRepository
parameter_list|)
block|{
name|String
name|appName
init|=
name|getParameters
argument_list|()
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
name|getParameters
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// todo: temporary workaround
name|SecurityProvider
name|provider
init|=
name|getSecurityProvider
argument_list|()
decl_stmt|;
name|Whiteboard
name|whiteboard
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|provider
operator|instanceof
name|WhiteboardAware
condition|)
block|{
name|whiteboard
operator|=
operator|(
operator|(
name|WhiteboardAware
operator|)
name|provider
operator|)
operator|.
name|getWhiteboard
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to obtain whiteboard from SecurityProvider"
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
name|contentRepository
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|,
name|whiteboard
argument_list|)
return|;
block|}
block|}
end_class

end_unit

