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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|api
operator|.
name|JackrabbitSession
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
name|Impersonation
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
name|security
operator|.
name|ExerciseUtility
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
comment|/**  *<pre>  * Module: Authentication  * =============================================================================  *  * Title: Impersonation  * -----------------------------------------------------------------------------  *  * Goal:  * Become familiar with {@link javax.jcr.Session#impersonate(javax.jcr.Credentials)}  * and how this is implemented in Oak.  * Please note that this exercise mixes authentication with user management  * functionality.  *  * Exercises:  *  *  * - {@link #testImpersonateTestUser()}  *   This test illustrates how a given session can be allowed to impersonate  *   another user. Use this test to walk through the impersonation step by step  *   and identify which principal must be granted impersonation in order to  *   get the test pass without exception.  *  *   Question: Can you explain where the impersonation information is being stored in the repository?  *   Question: Can you explain where the impersonation is being evaluated.  *   Question: Why is impersonation being granted to a Principal and not a userID?  *  * - {@link #testImpersonateOneSelf()}  *   Walk through {@link Session#impersonate(javax.jcr.Credentials)} in this  *   test case and test if the test user can impersonate himself; fix the test  *   accordingly.  *  *   Question: Can you identify the location of the code that makes this pass/fail?  *   Question: Can you identify how this behavior could be changed by a different repository configuration?  *  * - {@link #testAdminCanImpersonateEveryone()}  *   Walk through {@link Session#impersonate(javax.jcr.Credentials)} in this  *   test case and explain why the admin user is allowed to impersonate the  *   test user although impersonation is not explicitly granted.  *  *   Question: What kind of security concerns can you identify with this shortcut?  *             Discuss and explain your findings.  *  *  * Advanced Exercise:  * -----------------------------------------------------------------------------  *  * Once you feel familiar with the various security modules (including access  * control and user management), you may want to come back to this advanced  * exercise that requires an understanding of all areas.  *  * - Impersonation and pluggable authentication  *   Once you feel comfortable with the pluggable nature of the authentication  *   module, discuss how replacing the default login module chain will affect  *   how {@link Session#impersonate(javax.jcr.Credentials)} works.  *  *   Question: Can you think of a different mechanism on how to validate if a given  *             impersonation requestion should succeed?  *   Question: What are the classes present with Oak that you need to deal with  *             in your custom implementation?  *  * - {@link #testAdvancedImpersonationTest()}  *   This advanced tests mixes all three security areas involved in the impersonation:  *   1. Authorization: the required permission to write the list of impersonators  *   2. User Management: API to grant (and revoke) impersonation.  *   3. Authentication: The impersonation itself.  *  *</pre>  *  * @see javax.jcr.Session#impersonate(javax.jcr.Credentials)  * @see org.apache.jackrabbit.api.security.user.Impersonation  * @see org.apache.jackrabbit.oak.spi.security.authentication.ImpersonationCredentials  */
end_comment

begin_class
specifier|public
class|class
name|L7_ImpersonationTest
extends|extends
name|AbstractJCRTest
block|{
specifier|private
name|UserManager
name|userManager
decl_stmt|;
specifier|private
name|User
name|testUser
decl_stmt|;
specifier|private
name|User
name|anotherUser
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Session
argument_list|>
name|sessionList
init|=
operator|new
name|ArrayList
argument_list|<
name|Session
argument_list|>
argument_list|()
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
name|userManager
operator|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|superuser
operator|)
operator|.
name|getUserManager
argument_list|()
expr_stmt|;
name|testUser
operator|=
name|ExerciseUtility
operator|.
name|createTestUser
argument_list|(
name|userManager
argument_list|)
expr_stmt|;
name|anotherUser
operator|=
name|ExerciseUtility
operator|.
name|createTestUser
argument_list|(
name|userManager
argument_list|)
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
for|for
control|(
name|Session
name|s
range|:
name|sessionList
control|)
block|{
if|if
condition|(
name|s
operator|.
name|isLive
argument_list|()
condition|)
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|testUser
operator|!=
literal|null
condition|)
block|{
name|testUser
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|anotherUser
operator|!=
literal|null
condition|)
block|{
name|anotherUser
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
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
name|testImpersonateTestUser
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Principal
name|principal
init|=
literal|null
decl_stmt|;
comment|// EXERCISE: fill in the correct principal such that the test passes.
name|Impersonation
name|impersonation
init|=
name|anotherUser
operator|.
name|getImpersonation
argument_list|()
decl_stmt|;
name|impersonation
operator|.
name|grantImpersonation
argument_list|(
name|principal
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|Session
name|testSession
init|=
name|superuser
operator|.
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|ExerciseUtility
operator|.
name|getTestCredentials
argument_list|(
name|testUser
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|sessionList
operator|.
name|add
argument_list|(
name|testSession
argument_list|)
expr_stmt|;
name|Session
name|impersonated
init|=
name|testSession
operator|.
name|impersonate
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|anotherUser
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
argument_list|)
decl_stmt|;
name|sessionList
operator|.
name|add
argument_list|(
name|impersonated
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|anotherUser
operator|.
name|getID
argument_list|()
argument_list|,
name|impersonated
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testImpersonateOneSelf
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// EXERCISE: walk through this impersonation. does it work? if it does: why?
name|Session
name|testSession
init|=
name|superuser
operator|.
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|ExerciseUtility
operator|.
name|getTestCredentials
argument_list|(
name|testUser
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|sessionList
operator|.
name|add
argument_list|(
name|testSession
argument_list|)
expr_stmt|;
name|Session
name|impersonated
init|=
name|testSession
operator|.
name|impersonate
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|testUser
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
argument_list|)
decl_stmt|;
name|sessionList
operator|.
name|add
argument_list|(
name|impersonated
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testUser
operator|.
name|getID
argument_list|()
argument_list|,
name|impersonated
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAdminCanImpersonateEveryone
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// EXERCISE: walk through this impersonation. does it work? if it does: why?
name|Session
name|impersonated
init|=
name|superuser
operator|.
name|impersonate
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|anotherUser
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
argument_list|)
decl_stmt|;
name|sessionList
operator|.
name|add
argument_list|(
name|impersonated
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|anotherUser
operator|.
name|getID
argument_list|()
argument_list|,
name|impersonated
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAdvancedImpersonationTest
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// EXERCISE: change the permission setup such that the test-user is allowed to make himself an impersonator of 'another' user.
name|Session
name|testSession
init|=
name|superuser
operator|.
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|ExerciseUtility
operator|.
name|getTestCredentials
argument_list|(
name|testUser
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|sessionList
operator|.
name|add
argument_list|(
name|testSession
argument_list|)
expr_stmt|;
name|UserManager
name|uMgr
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|testSession
operator|)
operator|.
name|getUserManager
argument_list|()
decl_stmt|;
name|User
name|another
init|=
name|uMgr
operator|.
name|getAuthorizable
argument_list|(
name|anotherUser
operator|.
name|getID
argument_list|()
argument_list|,
name|User
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|another
argument_list|)
expr_stmt|;
name|Principal
name|princ
init|=
name|uMgr
operator|.
name|getAuthorizable
argument_list|(
name|testUser
operator|.
name|getID
argument_list|()
argument_list|)
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|another
operator|.
name|getImpersonation
argument_list|()
operator|.
name|grantImpersonation
argument_list|(
name|princ
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|testSession
operator|.
name|impersonate
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|anotherUser
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
argument_list|)
operator|.
name|logout
argument_list|()
expr_stmt|;
comment|// EXERCISE: change the impersonation of 'anotherUser' again such that the impersonate call fails
comment|// EXERCISE: withouth changing the permission setup. what API calls do you have at hand?
try|try
block|{
name|Session
name|s
init|=
name|testSession
operator|.
name|impersonate
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|anotherUser
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
argument_list|)
decl_stmt|;
name|sessionList
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Test user must no longer be able to edit the impersonation of the test user"
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

