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
name|exercise
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
name|io
operator|.
name|IOException
import|;
end_import

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
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NoSuchWorkspaceException
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
name|security
operator|.
name|authentication
operator|.
name|user
operator|.
name|LoginModuleImpl
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
name|user
operator|.
name|UserConstants
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

begin_comment
comment|/**  *<pre>  * Module: Authentication  * =============================================================================  *  * Title: Pre-Authentication with LoginModule Chain  * -----------------------------------------------------------------------------  *  * Goal:  * Understand how a pre-authentication can be used in combination with the  * {@link LoginModule} chain according to the description provided in  * http://jackrabbit.apache.org/oak/docs/security/authentication/preauthentication.html  *  * Exercises:  *  * - {@link #testPreAuthenticatedLogin()}  *   Modify the {@link CustomLoginModule}  *   such that the simplified pre-auth in the test-case passes.  *  * - With the same setup at hand explain why the {@code CustomCredentials} must  *   be package protected. Come up with a vulnerability/exploit if this credentials  *   implemenation was exposed to the public.  *  *  * Additional Exercises:  * -----------------------------------------------------------------------------  *  * In a Sling base repository installation (Granite|CQ) make use of your  * understanding of pre-authentication with LoginModule chain involvement  * and defined a dedicated bundle that comes with a package that contains the  * following classes  *  * - A Credentials implemenation that is package private and cannot be abused  *   outside of the scope of this bundle.  * - Sling AuthenticationHandler implementation that performs the pre-auth and  *   passes the package private Credentials to the repository login  * - LoginModule implementation (that receives the package private Credentials  *   and updates the shared state accordingly).  *  *  * Related Exercises:  * -----------------------------------------------------------------------------  *  * - {@link L3_LoginModuleTest}  * - {@link L9_NullLoginTest}  *  *</pre>  *  * @see org.apache.jackrabbit.oak.spi.security.authentication.PreAuthenticatedLogin  * @see<a href="http://jackrabbit.apache.org/oak/docs/security/authentication/preauthentication.html">Pre-Authentication Documentation</a>  */
end_comment

begin_class
specifier|public
class|class
name|L8_PreAuthTest
extends|extends
name|AbstractSecurityTest
block|{
annotation|@
name|Override
specifier|protected
name|Configuration
name|getConfiguration
parameter_list|()
block|{
specifier|final
name|ConfigurationParameters
name|config
init|=
name|getSecurityConfigParameters
argument_list|()
decl_stmt|;
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
name|applicationName
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|options
init|=
name|getSecurityConfigParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|applicationName
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|AppConfigurationEntry
index|[]
block|{
operator|new
name|AppConfigurationEntry
argument_list|(
name|CustomLoginModule
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
name|options
argument_list|)
block|,
operator|new
name|AppConfigurationEntry
argument_list|(
name|LoginModuleImpl
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
block|}
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPreAuthenticatedLogin
parameter_list|()
throws|throws
name|IOException
throws|,
name|LoginException
throws|,
name|NoSuchWorkspaceException
block|{
comment|// EXERCISE: adjust the CustomLoginModule such that the following test passes, the jaas configuration has already been adjusted for you above.
comment|// login as admin with CustomCredentials and without a password
comment|// -> no password verification in the module required as this is expected
comment|//    to have already happened during the pre-auth setp (which is missing here)
name|String
name|loginID
init|=
name|getUserConfiguration
argument_list|()
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_ADMIN_ID
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_ADMIN_ID
argument_list|)
decl_stmt|;
name|ContentSession
name|contentSession
init|=
name|login
argument_list|(
operator|new
name|CustomCredentials
argument_list|(
name|loginID
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
name|EMPTY_MAP
argument_list|)
argument_list|)
decl_stmt|;
comment|// EXERCISE: add verification of the AuthInfo according to your implementation of the custom login module.
name|contentSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

