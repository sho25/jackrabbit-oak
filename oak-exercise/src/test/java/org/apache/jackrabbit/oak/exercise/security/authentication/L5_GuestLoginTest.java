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
name|LoginException
import|;
end_import

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
name|RepositoryException
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
name|jcr
operator|.
name|SimpleCredentials
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
name|exercise
operator|.
name|security
operator|.
name|user
operator|.
name|L15_RepositoryWithoutAnonymousTest
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
name|test
operator|.
name|AbstractJCRTest
import|;
end_import

begin_comment
comment|/**  *<pre>  * Module: Authentication  * =============================================================================  *  * Title: Guest Login (aka Anonymous Login)  * -----------------------------------------------------------------------------  *  * Goal:  * Understand how to login as guest, the meaning of the {@link javax.jcr.GuestCredentials}  * and how this is linked to the 'anonymous' user.  *  * Exercises:  *  * - {@link #testAnonymousGuestLogin()}  *   Walk through the anonymous login {@link #testAnonymousGuestLogin()}  *   Question: Can you identify ares in the default authentication setup that apply special handling for anonymous?  *  * - {@link #testAnonymousSimpleCredentialsLogin()}  *   Try to login as anonymous with SimpleCredentials  *   Question: Why can't you login as anonymous with {@link javax.jcr.SimpleCredentials}?  *  * - {@link #testAnonymousSimpleCredentialsLoginSuccess}  *   In order to understand what makes the guest-login special compared to a  *   regular user-login, modify the test-case such that a regular login with  *   SimpleCredentials succeeds.  *  * - {@link #testDisableGuestLogin()}  *   Use this test to prevent anonymous access in an existing/running oak repository.  *   Modify the test such that it success. How many variants do you find?  *  *  * Additional Exercises:  * -----------------------------------------------------------------------------  *  * - Run a Sling (Granite|Cq) application and identify how the Sling Authentication  *   deals with guest login.  *   Question: What is the Sling way to disable anonymous (guest) access?  *  *  * Related Exercises:  * -----------------------------------------------------------------------------  *  * - {@link L6_AnonymousIdTest ()}  * - {@link L15_RepositoryWithoutAnonymousTest ()}  * - {@link L9_NullLoginTest ()}  *  *</pre>  *  * @see javax.jcr.GuestCredentials  * @see org.apache.jackrabbit.api.security.user.User#getCredentials()  * @see org.apache.jackrabbit.api.security.user.User#changePassword(String, String)  * @see org.apache.jackrabbit.api.security.user.User#disable(String)  */
end_comment

begin_class
specifier|public
class|class
name|L5_GuestLoginTest
extends|extends
name|AbstractJCRTest
block|{
specifier|private
name|Repository
name|repository
decl_stmt|;
specifier|private
name|Session
name|testSession
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|repository
operator|=
name|getHelper
argument_list|()
operator|.
name|getRepository
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|testSession
operator|!=
literal|null
operator|&&
name|testSession
operator|.
name|isLive
argument_list|()
condition|)
block|{
name|testSession
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testAnonymousGuestLogin
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|testSession
operator|=
name|repository
operator|.
name|login
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAnonymousSimpleCredentialsLogin
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|testSession
operator|=
name|repository
operator|.
name|login
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|anonymousID
init|=
name|testSession
operator|.
name|getUserID
argument_list|()
decl_stmt|;
name|testSession
operator|.
name|logout
argument_list|()
expr_stmt|;
try|try
block|{
name|testSession
operator|=
name|repository
operator|.
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
literal|"Anonymous cannot login with simple credentials."
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
comment|// EXERCISE: explain why
block|}
block|}
specifier|public
name|void
name|testAnonymousSimpleCredentialsLoginSuccess
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|testSession
operator|=
name|repository
operator|.
name|login
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|anonymousID
init|=
name|testSession
operator|.
name|getUserID
argument_list|()
decl_stmt|;
comment|// EXERCISE: how to you need to modify the test-case that this would work?
name|Session
name|anonymousUserSession
init|=
name|repository
operator|.
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
decl_stmt|;
name|assertEquals
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_ANONYMOUS_ID
argument_list|,
name|testSession
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDisableGuestLogin
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// EXERCISE : identify ways to prevent anonymous login with GuestCredentials in an existing repository
comment|//            extend the test here such that the login below fails
comment|// 1:
comment|// 2:
try|try
block|{
name|testSession
operator|=
name|repository
operator|.
name|login
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Anonymous login must fail."
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
block|}
end_class

end_unit
