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
name|Set
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
name|api
operator|.
name|security
operator|.
name|authentication
operator|.
name|token
operator|.
name|TokenCredentials
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
name|AuthInfo
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
name|authentication
operator|.
name|token
operator|.
name|TokenLoginModule
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
name|authentication
operator|.
name|AuthInfoImpl
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
name|ImpersonationCredentials
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
name|authentication
operator|.
name|token
operator|.
name|TokenInfo
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
name|assertTrue
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Test login behavior with the following configuration:  *  *<pre>  *     jackrabbit.oak {  *            org.apache.jackrabbit.oak.spi.security.authentication.token.TokenLoginModule sufficient;  *            org.apache.jackrabbit.oak.security.authentication.user.LoginModuleImpl required;  *     };  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|TokenDefaultLoginModuleTest
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
name|tokenEntry
init|=
operator|new
name|AppConfigurationEntry
argument_list|(
name|TokenLoginModule
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
name|SUFFICIENT
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
name|AppConfigurationEntry
name|defaultEntry
init|=
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
name|tokenEntry
block|,
name|defaultEntry
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
name|testNullLogin
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|login
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Null login should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
comment|// success
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGuestLogin
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|login
argument_list|(
operator|new
name|GuestCredentials
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInvalidSimpleCredentials
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|SimpleCredentials
name|sc
init|=
operator|new
name|SimpleCredentials
argument_list|(
literal|"test"
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|cs
operator|=
name|login
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Invalid simple credentials login should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
comment|// success
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInvalidSimpleCredentialsWithAttribute
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|SimpleCredentials
name|sc
init|=
operator|new
name|SimpleCredentials
argument_list|(
literal|"test"
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|".token"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|cs
operator|=
name|login
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Invalid simple credentials login should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
comment|// success
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleCredentials
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|login
argument_list|(
name|getAdminCredentials
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleCredentialsWithAttribute
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|SimpleCredentials
name|sc
init|=
operator|(
name|SimpleCredentials
operator|)
name|getAdminCredentials
argument_list|()
decl_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|".token"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|cs
operator|=
name|login
argument_list|(
name|sc
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTokenAuthInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|SimpleCredentials
name|sc
init|=
operator|(
name|SimpleCredentials
operator|)
name|getAdminCredentials
argument_list|()
decl_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|".token"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|cs
operator|=
name|login
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"userid must be correct"
argument_list|,
literal|"admin"
argument_list|,
name|cs
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getUserID
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTokenCreationAndLogin
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|SimpleCredentials
name|sc
init|=
operator|(
name|SimpleCredentials
operator|)
name|getAdminCredentials
argument_list|()
decl_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|".token"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|cs
operator|=
name|login
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|Object
name|token
init|=
name|sc
operator|.
name|getAttribute
argument_list|(
literal|".token"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|TokenCredentials
name|tc
init|=
operator|new
name|TokenCredentials
argument_list|(
name|token
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
name|cs
operator|=
name|login
argument_list|(
name|tc
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTokenCreationAndImpersonation
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|SimpleCredentials
name|sc
init|=
operator|(
name|SimpleCredentials
operator|)
name|getAdminCredentials
argument_list|()
decl_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|".token"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|ImpersonationCredentials
name|ic
init|=
operator|new
name|ImpersonationCredentials
argument_list|(
name|sc
argument_list|,
operator|new
name|AuthInfoImpl
argument_list|(
operator|(
operator|(
name|SimpleCredentials
operator|)
name|getAdminCredentials
argument_list|()
operator|)
operator|.
name|getUserID
argument_list|()
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
argument_list|,
name|Collections
operator|.
expr|<
name|Principal
operator|>
name|emptySet
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|cs
operator|=
name|login
argument_list|(
name|ic
argument_list|)
expr_stmt|;
name|Object
name|token
init|=
name|sc
operator|.
name|getAttribute
argument_list|(
literal|".token"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|TokenCredentials
name|tc
init|=
operator|new
name|TokenCredentials
argument_list|(
name|token
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
name|cs
operator|=
name|login
argument_list|(
name|tc
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInvalidTokenCredentials
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|login
argument_list|(
operator|new
name|TokenCredentials
argument_list|(
literal|"invalid"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Invalid token credentials login should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
comment|// success
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidTokenCredentials
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|root
init|=
name|adminSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|TokenConfiguration
name|tc
init|=
name|getSecurityProvider
argument_list|()
operator|.
name|getConfiguration
argument_list|(
name|TokenConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|TokenProvider
name|tp
init|=
name|tc
operator|.
name|getTokenProvider
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|SimpleCredentials
name|sc
init|=
operator|(
name|SimpleCredentials
operator|)
name|getAdminCredentials
argument_list|()
decl_stmt|;
name|TokenInfo
name|info
init|=
name|tp
operator|.
name|createToken
argument_list|(
name|sc
operator|.
name|getUserID
argument_list|()
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
name|ContentSession
name|cs
init|=
name|login
argument_list|(
operator|new
name|TokenCredentials
argument_list|(
name|info
operator|.
name|getToken
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|sc
operator|.
name|getUserID
argument_list|()
argument_list|,
name|cs
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTokenCreationWithAttributes
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|SimpleCredentials
name|sc
init|=
operator|(
name|SimpleCredentials
operator|)
name|getAdminCredentials
argument_list|()
decl_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|".token"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|".token.mandatory"
argument_list|,
literal|"something"
argument_list|)
expr_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|"attr"
argument_list|,
literal|"val"
argument_list|)
expr_stmt|;
name|cs
operator|=
name|login
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|AuthInfo
name|ai
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|attrNames
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|ai
operator|.
name|getAttributeNames
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|attrNames
operator|.
name|contains
argument_list|(
literal|"attr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|attrNames
operator|.
name|contains
argument_list|(
literal|".token"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|attrNames
operator|.
name|contains
argument_list|(
literal|".token.mandatory"
argument_list|)
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTokenCreationWithImpersonationAttributes
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|SimpleCredentials
name|sc
init|=
operator|(
name|SimpleCredentials
operator|)
name|getAdminCredentials
argument_list|()
decl_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|".token"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|".token.mandatory"
argument_list|,
literal|"something"
argument_list|)
expr_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|"attr"
argument_list|,
literal|"val"
argument_list|)
expr_stmt|;
name|ImpersonationCredentials
name|ic
init|=
operator|new
name|ImpersonationCredentials
argument_list|(
name|sc
argument_list|,
operator|new
name|AuthInfoImpl
argument_list|(
operator|(
operator|(
name|SimpleCredentials
operator|)
name|getAdminCredentials
argument_list|()
operator|)
operator|.
name|getUserID
argument_list|()
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
argument_list|,
name|Collections
operator|.
expr|<
name|Principal
operator|>
name|emptySet
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|cs
operator|=
name|login
argument_list|(
name|ic
argument_list|)
expr_stmt|;
name|AuthInfo
name|ai
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|attrNames
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|ai
operator|.
name|getAttributeNames
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|attrNames
operator|.
name|contains
argument_list|(
literal|"attr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|attrNames
operator|.
name|contains
argument_list|(
literal|".token"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|attrNames
operator|.
name|contains
argument_list|(
literal|".token.mandatory"
argument_list|)
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
block|}
block|}
block|}
end_class

end_unit

