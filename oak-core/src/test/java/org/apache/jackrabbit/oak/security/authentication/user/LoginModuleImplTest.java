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
name|user
package|;
end_package

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
name|User
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
name|util
operator|.
name|UserUtil
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
name|fail
import|;
end_import

begin_comment
comment|/**  * LoginTest...  */
end_comment

begin_class
specifier|public
class|class
name|LoginModuleImplTest
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
name|ConfigurationUtil
operator|.
name|getDefaultConfiguration
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
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
name|login
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|AuthInfo
name|authInfo
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
decl_stmt|;
name|String
name|anonymousID
init|=
name|UserUtil
operator|.
name|getAnonymousId
argument_list|(
name|getUserConfiguration
argument_list|()
operator|.
name|getParameters
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|anonymousID
argument_list|,
name|authInfo
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
name|testAnonymousLogin
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|anonymousID
init|=
name|UserUtil
operator|.
name|getAnonymousId
argument_list|(
name|getUserConfiguration
argument_list|()
operator|.
name|getParameters
argument_list|()
argument_list|)
decl_stmt|;
name|UserManager
name|userMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
comment|// verify initial user-content looks like expected
name|Authorizable
name|anonymous
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|anonymousID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|anonymous
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|anonymous
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
argument_list|)
expr_stmt|;
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
name|SimpleCredentials
argument_list|(
name|anonymousID
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Login with anonymousID should fail since the initial setup doesn't provide a password."
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
name|testUserLogin
parameter_list|()
throws|throws
name|Exception
block|{
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
name|User
name|user
init|=
literal|null
decl_stmt|;
try|try
block|{
name|user
operator|=
name|userManager
operator|.
name|createUser
argument_list|(
literal|"test"
argument_list|,
literal|"pw"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|cs
operator|=
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"test"
argument_list|,
literal|"pw"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|AuthInfo
name|authInfo
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|authInfo
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
name|user
operator|!=
literal|null
condition|)
block|{
name|user
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
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
name|testSelfImpersonation
parameter_list|()
throws|throws
name|Exception
block|{
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
name|User
name|user
init|=
literal|null
decl_stmt|;
try|try
block|{
name|user
operator|=
name|userManager
operator|.
name|createUser
argument_list|(
literal|"test"
argument_list|,
literal|"pw"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SimpleCredentials
name|sc
init|=
operator|new
name|SimpleCredentials
argument_list|(
literal|"test"
argument_list|,
literal|"pw"
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
name|cs
operator|=
name|login
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|AuthInfo
name|authInfo
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|authInfo
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
name|sc
operator|=
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
expr_stmt|;
name|ImpersonationCredentials
name|ic
init|=
operator|new
name|ImpersonationCredentials
argument_list|(
name|sc
argument_list|,
name|authInfo
argument_list|)
decl_stmt|;
name|cs
operator|=
name|login
argument_list|(
name|ic
argument_list|)
expr_stmt|;
name|authInfo
operator|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|authInfo
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
name|user
operator|!=
literal|null
condition|)
block|{
name|user
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
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
name|testInvalidImpersonation
parameter_list|()
throws|throws
name|Exception
block|{
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
name|User
name|user
init|=
literal|null
decl_stmt|;
try|try
block|{
name|user
operator|=
name|userManager
operator|.
name|createUser
argument_list|(
literal|"test"
argument_list|,
literal|"pw"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SimpleCredentials
name|sc
init|=
operator|new
name|SimpleCredentials
argument_list|(
literal|"test"
argument_list|,
literal|"pw"
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
name|cs
operator|=
name|login
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|AuthInfo
name|authInfo
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|authInfo
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
name|cs
operator|=
literal|null
expr_stmt|;
name|ConfigurationParameters
name|config
init|=
name|securityProvider
operator|.
name|getConfiguration
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getParameters
argument_list|()
decl_stmt|;
name|String
name|adminId
init|=
name|UserUtil
operator|.
name|getAdminId
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|sc
operator|=
operator|new
name|SimpleCredentials
argument_list|(
name|adminId
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
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
name|authInfo
argument_list|)
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|login
argument_list|(
name|ic
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"User 'test' should not be allowed to impersonate "
operator|+
name|adminId
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
block|}
finally|finally
block|{
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|user
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
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

