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
operator|.
name|token
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|credentials
operator|.
name|CredentialsSupport
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
name|TokenConstants
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
name|sling
operator|.
name|testing
operator|.
name|mock
operator|.
name|osgi
operator|.
name|junit
operator|.
name|OsgiContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|ServiceRegistration
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
name|assertEquals
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|TokenConfigurationImplOSGiTest
extends|extends
name|AbstractSecurityTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|OsgiContext
name|context
init|=
operator|new
name|OsgiContext
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|TokenConfigurationImpl
name|tokenConfiguration
init|=
operator|new
name|TokenConfigurationImpl
argument_list|()
decl_stmt|;
specifier|private
name|SimpleCredentials
name|sc
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
name|tokenConfiguration
operator|.
name|setSecurityProvider
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerInjectActivateService
argument_list|(
name|tokenConfiguration
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|of
argument_list|(
name|TokenProvider
operator|.
name|PARAM_TOKEN_EXPIRATION
argument_list|,
literal|25
argument_list|,
name|TokenProvider
operator|.
name|PARAM_TOKEN_LENGTH
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|sc
operator|=
operator|new
name|SimpleCredentials
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
name|TokenConstants
operator|.
name|TOKEN_ATTRIBUTE
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetParameters
parameter_list|()
block|{
name|ConfigurationParameters
name|params
init|=
name|tokenConfiguration
operator|.
name|getParameters
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|25
argument_list|,
name|params
operator|.
name|getConfigValue
argument_list|(
name|TokenProvider
operator|.
name|PARAM_TOKEN_EXPIRATION
argument_list|,
name|TokenProviderImpl
operator|.
name|DEFAULT_TOKEN_EXPIRATION
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|params
operator|.
name|getConfigValue
argument_list|(
name|TokenProvider
operator|.
name|PARAM_TOKEN_LENGTH
argument_list|,
name|TokenProviderImpl
operator|.
name|DEFAULT_KEY_SIZE
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefaultCredentialsSupport
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenProvider
name|tp
init|=
name|tokenConfiguration
operator|.
name|getTokenProvider
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|doCreateToken
argument_list|(
name|sc
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBindCredentialsSupport
parameter_list|()
block|{
name|context
operator|.
name|registerService
argument_list|(
name|CredentialsSupport
operator|.
name|class
argument_list|,
operator|new
name|TestCredentialsSupport
argument_list|(
name|sc
operator|.
name|getUserID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|TokenProvider
name|tp
init|=
name|tokenConfiguration
operator|.
name|getTokenProvider
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|doCreateToken
argument_list|(
name|sc
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|doCreateToken
argument_list|(
operator|new
name|TestCredentialsSupport
operator|.
name|Creds
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnbindCredentialsSupport
parameter_list|()
block|{
name|CredentialsSupport
name|testSupport
init|=
operator|new
name|TestCredentialsSupport
argument_list|(
name|sc
operator|.
name|getUserID
argument_list|()
argument_list|)
decl_stmt|;
name|ServiceRegistration
name|registration
init|=
name|context
operator|.
name|bundleContext
argument_list|()
operator|.
name|registerService
argument_list|(
name|CredentialsSupport
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|testSupport
argument_list|,
operator|new
name|Hashtable
argument_list|()
argument_list|)
decl_stmt|;
name|TokenProvider
name|tp
init|=
name|tokenConfiguration
operator|.
name|getTokenProvider
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|doCreateToken
argument_list|(
name|sc
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|doCreateToken
argument_list|(
operator|new
name|TestCredentialsSupport
operator|.
name|Creds
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|registration
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|tp
operator|=
name|tokenConfiguration
operator|.
name|getTokenProvider
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|doCreateToken
argument_list|(
name|sc
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|doCreateToken
argument_list|(
operator|new
name|TestCredentialsSupport
operator|.
name|Creds
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

