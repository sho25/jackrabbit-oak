begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
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
name|spi
operator|.
name|LoginModule
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
name|jaas
operator|.
name|LoginModuleFactory
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
name|jaas
operator|.
name|boot
operator|.
name|ProxyLoginModule
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
name|user
operator|.
name|Authorizable
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
name|user
operator|.
name|UserManager
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
name|Oak
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
name|ContentSession
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
name|external
operator|.
name|ExternalIdentityProviderManager
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
name|external
operator|.
name|ExternalLoginModuleTestBase
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
name|external
operator|.
name|ExternalUser
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
name|external
operator|.
name|SyncManager
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
name|easymock
operator|.
name|EasyMock
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
name|assertNull
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
name|ExternalLoginModuleFactoryTest
extends|extends
name|ExternalLoginModuleTestBase
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
annotation|@
name|Override
specifier|protected
name|Oak
name|withEditors
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
name|super
operator|.
name|withEditors
argument_list|(
name|oak
argument_list|)
expr_stmt|;
comment|//Just grab the whiteboard but do not register any manager here
comment|//This would ensure that LoginModule would only work if the required managers
comment|//are preset
name|whiteboard
operator|=
name|oak
operator|.
name|getWhiteboard
argument_list|()
expr_stmt|;
return|return
name|oak
return|;
block|}
specifier|protected
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
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
name|s
parameter_list|)
block|{
name|AppConfigurationEntry
name|entry
init|=
operator|new
name|AppConfigurationEntry
argument_list|(
comment|//Use ProxyLoginModule so that factory mode can be used
name|ProxyLoginModule
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
name|REQUIRED
argument_list|,
name|options
argument_list|)
decl_stmt|;
return|return
operator|new
name|AppConfigurationEntry
index|[]
block|{
name|entry
block|}
return|;
block|}
block|}
return|;
block|}
comment|//~-------------------------------------------------------------< tests>---
annotation|@
name|Test
specifier|public
name|void
name|testSyncCreateUser
parameter_list|()
throws|throws
name|Exception
block|{
name|setUpJaasFactoryWithInjection
argument_list|()
expr_stmt|;
name|UserManager
name|userManager
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|assertNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
argument_list|)
expr_stmt|;
name|cs
operator|=
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|ExternalUser
name|user
init|=
name|idp
operator|.
name|getUser
argument_list|(
name|USER_ID
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|user
operator|.
name|getProperties
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
name|prop
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|TEST_CONSTANT_PROPERTY_VALUE
argument_list|,
name|a
operator|.
name|getProperty
argument_list|(
name|TEST_CONSTANT_PROPERTY_NAME
argument_list|)
index|[
literal|0
index|]
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|options
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Prepares the OSGi part with required services injected and configures      * the factory in JAAS options which then delegates to ExternalLoginModuleFactory      */
specifier|private
name|void
name|setUpJaasFactoryWithInjection
parameter_list|()
throws|throws
name|Exception
block|{
name|context
operator|.
name|registerService
argument_list|(
name|Repository
operator|.
name|class
argument_list|,
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Repository
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|SyncManager
operator|.
name|class
argument_list|,
operator|new
name|SyncManagerImpl
argument_list|(
name|whiteboard
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|ExternalIdentityProviderManager
operator|.
name|class
argument_list|,
operator|new
name|ExternalIDPManagerImpl
argument_list|(
name|whiteboard
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|LoginModuleFactory
name|lmf
init|=
name|context
operator|.
name|registerInjectActivateService
argument_list|(
operator|new
name|ExternalLoginModuleFactory
argument_list|()
argument_list|)
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
name|ProxyLoginModule
operator|.
name|PROP_LOGIN_MODULE_FACTORY
argument_list|,
operator|new
name|ProxyLoginModule
operator|.
name|BootLoginModuleFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|LoginModule
name|createLoginModule
parameter_list|()
block|{
return|return
name|lmf
operator|.
name|createLoginModule
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

