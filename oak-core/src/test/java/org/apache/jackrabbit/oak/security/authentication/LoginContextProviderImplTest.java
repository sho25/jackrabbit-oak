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
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|GuestCredentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
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
name|login
operator|.
name|AppConfigurationEntry
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|AbstractSecurityTest
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
name|GuestLoginModule
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
name|DefaultWhiteboard
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertSame
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|LoginContextProviderImplTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|LoginContextProviderImpl
name|lcProvider
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|lcProvider
operator|=
name|newLoginContextProviderImpl
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
specifier|private
name|LoginContextProviderImpl
name|newLoginContextProviderImpl
parameter_list|(
name|ConfigurationParameters
name|params
parameter_list|)
block|{
return|return
operator|new
name|LoginContextProviderImpl
argument_list|(
name|AuthenticationConfiguration
operator|.
name|DEFAULT_APP_NAME
argument_list|,
name|params
argument_list|,
name|getContentRepository
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|,
operator|new
name|DefaultWhiteboard
argument_list|()
argument_list|,
name|LoginModuleMonitor
operator|.
name|NOOP
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetLoginContext
parameter_list|()
throws|throws
name|Exception
block|{
name|LoginContext
name|ctx
init|=
name|lcProvider
operator|.
name|getLoginContext
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|,
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|,
name|root
operator|.
name|getContentSession
argument_list|()
operator|.
name|getWorkspaceName
argument_list|()
argument_list|)
decl_stmt|;
name|Subject
name|subject
init|=
name|ctx
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|subject
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|subject
operator|.
name|isReadOnly
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getLoginContextWithoutCredentials
parameter_list|()
throws|throws
name|Exception
block|{
name|LoginContext
name|ctx
init|=
name|lcProvider
operator|.
name|getLoginContext
argument_list|(
literal|null
argument_list|,
name|root
operator|.
name|getContentSession
argument_list|()
operator|.
name|getWorkspaceName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|instanceof
name|JaasLoginContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPreAuthLoginContext
parameter_list|()
block|{
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|(
literal|true
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|()
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|LoginContext
name|ctx
init|=
name|Subject
operator|.
name|doAs
argument_list|(
name|subject
argument_list|,
operator|new
name|PrivilegedAction
argument_list|<
name|LoginContext
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|LoginContext
name|run
parameter_list|()
block|{
try|try
block|{
return|return
name|lcProvider
operator|.
name|getLoginContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|instanceof
name|PreAuthContext
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|subject
argument_list|,
name|ctx
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetLoginContextWithInvalidProviderConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|ConfigurationParameters
name|params
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|AuthenticationConfiguration
operator|.
name|PARAM_CONFIG_SPI_NAME
argument_list|,
literal|"invalid"
argument_list|)
decl_stmt|;
name|LoginContextProvider
name|provider
init|=
name|newLoginContextProviderImpl
argument_list|(
name|params
argument_list|)
decl_stmt|;
comment|// invalid configuration falls back to default configuration
name|LoginContext
name|ctx
init|=
name|provider
operator|.
name|getLoginContext
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|,
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|login
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetLoginContextWithConfigurationPreset
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
operator|.
name|setConfiguration
argument_list|(
operator|new
name|Configuration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AppConfigurationEntry
index|[]
name|getAppConfigurationEntry
parameter_list|(
name|String
name|applicationName
parameter_list|)
block|{
return|return
operator|new
name|AppConfigurationEntry
index|[]
block|{
operator|new
name|AppConfigurationEntry
argument_list|(
name|GuestLoginModule
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|OPTIONAL
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|LoginContextProvider
name|provider
init|=
name|newLoginContextProviderImpl
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|LoginContext
name|ctx
init|=
name|provider
operator|.
name|getLoginContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|login
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|getSubject
argument_list|()
operator|.
name|getPublicCredentials
argument_list|(
name|GuestCredentials
operator|.
name|class
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetLoginContextTwice
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
operator|.
name|setConfiguration
argument_list|(
operator|new
name|Configuration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AppConfigurationEntry
index|[]
name|getAppConfigurationEntry
parameter_list|(
name|String
name|applicationName
parameter_list|)
block|{
return|return
operator|new
name|AppConfigurationEntry
index|[]
block|{
operator|new
name|AppConfigurationEntry
argument_list|(
name|GuestLoginModule
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|OPTIONAL
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|LoginContextProvider
name|provider
init|=
name|newLoginContextProviderImpl
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|provider
operator|.
name|getLoginContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|LoginContext
name|ctx
init|=
name|provider
operator|.
name|getLoginContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|login
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|getSubject
argument_list|()
operator|.
name|getPublicCredentials
argument_list|(
name|GuestCredentials
operator|.
name|class
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

