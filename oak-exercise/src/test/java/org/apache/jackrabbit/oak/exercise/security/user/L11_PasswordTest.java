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
name|user
package|;
end_package

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
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Property
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
name|exercise
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
name|oak
operator|.
name|exercise
operator|.
name|security
operator|.
name|user
operator|.
name|action
operator|.
name|L2_AuthorizableActionTest
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
name|exercise
operator|.
name|ExerciseUtility
operator|.
name|TEST_PW
import|;
end_import

begin_comment
comment|/**  *<pre>  * Module: User Management  * =============================================================================  *  * Title: Password Test  * -----------------------------------------------------------------------------  *  * Goal:  * Become familiar with password related parts of the user management API and  * get to know some implementation details.  *  * Exercises:  *  * - {@link #testGetCredentials()}  *   Understand that the password is not exposed as plain-word property from  *   the user. Look at the return-value of the {@link org.apache.jackrabbit.api.security.user.User#getCredentials()}  *   call and what it looks like. Fix the test-case accordingly.  *  *   Question: Can you use the exposed Credentials to login to the repository?  *  * - {@link #testPasswordInContent()}  *   Creates a new user with a valid password. Inspect how the password is being  *   store in the repository (Note: implementation detail!) and fill in the  *   right property name to get the test-case pass.  *   Explain why the password property doesn't contain the password string.  *  * - {@link #testCreateUserAndLogin()}  *   Same as {@link #testPasswordInContent()} but additional aims to login as  *   the new user.  *   Fix the test by creating the correct {@link javax.jcr.Credentials}.  *  * - {@link #testCreateUserWithoutPassword()}  *   This test creates a new user with a 'null' password. Inspect the user node  *   created by this method and add the correct assertion wrt password.  *  * - {@link #testCreateUserWithoutPasswordAndLogin()}  *   Same as {@link #testCreateUserWithoutPassword()}. This time fix the test  *   case to properly reflect the expected behavior upon login for that new user.  *  * - {@link #testChangePassword()}  *   Change the password of an existing user. Use both variants and get familiar  *   with the implementation specific constraints.  *  *  * Additional Exercises:  * -----------------------------------------------------------------------------  *  * In a OSGI-based Oak installation (Sling|Granite|CQ) you can easily perform the  * following additional test.  * Note: You can also do that in Java by building a new Jcr/Oak repository with  * the corresponding configuration parameters set.  *  * - Go to the system console and change the default configuration parameters  *   in the 'Apache Jackrabbit Oak UserConfiguration' and play with the following  *   configuration parameters:  *   - {@link org.apache.jackrabbit.oak.spi.security.user.UserConstants#PARAM_PASSWORD_HASH_ALGORITHM}  *   - {@link org.apache.jackrabbit.oak.spi.security.user.UserConstants#PARAM_PASSWORD_HASH_ITERATIONS}  *   - {@link org.apache.jackrabbit.oak.spi.security.user.UserConstants#PARAM_PASSWORD_SALT_SIZE}  *   Change the password of a test user and observe the changes.  *  * - Go to the system console and look for the 'Apache Jackrabbit Oak AuthorizableActionProvider'.  *   Enable the password validation action and then change the password of  *   an existing test user.  *  *  * Advanced Exercises:  * -----------------------------------------------------------------------------  *  * - Write a custom password validation action and plug it into your repository.  *   See Oak documentation for some hints.  *  *  * Related Exercises:  * -----------------------------------------------------------------------------  *  * - {@link L12_PasswordExpiryTest ()}  * - {@link L2_AuthorizableActionTest ()}  *  *</pre>  *  * @see User#changePassword(String, String)  * @see User#changePassword(String)  * @see org.apache.jackrabbit.oak.spi.security.user.action.PasswordValidationAction  * @see org.apache.jackrabbit.oak.spi.security.user.util.PasswordUtil  */
end_comment

begin_class
specifier|public
class|class
name|L11_PasswordTest
extends|extends
name|AbstractJCRTest
block|{
specifier|private
name|UserManager
name|userManager
decl_stmt|;
specifier|private
name|String
name|testId
decl_stmt|;
specifier|private
name|User
name|testUser
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
name|testId
operator|=
name|ExerciseUtility
operator|.
name|getTestId
argument_list|(
literal|"testUser"
argument_list|)
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
name|testGetCredentials
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|testUser
operator|=
name|userManager
operator|.
name|createUser
argument_list|(
name|testId
argument_list|,
name|TEST_PW
argument_list|)
expr_stmt|;
name|Credentials
name|creds
init|=
name|testUser
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
comment|// EXERCISE fix the expectation
name|Credentials
name|expected
init|=
literal|null
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|creds
argument_list|)
expr_stmt|;
comment|// EXERCISE : complete and explain the expected behavior
name|getHelper
argument_list|()
operator|.
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|creds
argument_list|)
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testPasswordInContent
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|testUser
operator|=
name|userManager
operator|.
name|createUser
argument_list|(
name|testId
argument_list|,
name|TEST_PW
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|Node
name|userNode
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|testUser
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|pwPropertyName
init|=
literal|null
decl_stmt|;
comment|// EXERCISE: fill in
name|Property
name|pwProperty
init|=
name|userNode
operator|.
name|getProperty
argument_list|(
name|pwPropertyName
argument_list|)
decl_stmt|;
comment|// EXERCISE: explain why the password property doesn't contain the 'pw' string
name|assertFalse
argument_list|(
name|TEST_PW
operator|.
name|equals
argument_list|(
name|pwProperty
operator|.
name|getString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCreateUserAndLogin
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|testUser
operator|=
name|userManager
operator|.
name|createUser
argument_list|(
name|testId
argument_list|,
name|TEST_PW
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|Credentials
name|creds
init|=
literal|null
decl_stmt|;
comment|// EXERCISE build the credentials
name|getHelper
argument_list|()
operator|.
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|creds
argument_list|)
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testCreateUserWithoutPassword
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|testUser
operator|=
name|userManager
operator|.
name|createUser
argument_list|(
name|testId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// EXERCISE: look at the user node. does it have a password property set?
comment|// EXERCISE: add the correct assertion
name|Node
name|userNode
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|testUser
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
block|}
specifier|public
name|void
name|testCreateUserWithoutPasswordAndLogin
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|testUser
operator|=
name|userManager
operator|.
name|createUser
argument_list|(
name|testId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// EXERCISE: build the credentials and fix the test-case such that it no longer fails
name|Credentials
name|creds
init|=
literal|null
decl_stmt|;
name|getHelper
argument_list|()
operator|.
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|creds
argument_list|)
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testChangePassword
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|testUser
operator|=
name|userManager
operator|.
name|createUser
argument_list|(
name|testId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|String
name|newPassword
init|=
literal|null
decl_stmt|;
comment|// EXERCISE : define valid value(s)
name|testUser
operator|.
name|changePassword
argument_list|(
name|newPassword
argument_list|)
expr_stmt|;
name|String
name|oldPassword
init|=
literal|null
decl_stmt|;
comment|// EXERCISE : fill in the correct value
name|newPassword
operator|=
literal|null
expr_stmt|;
comment|// EXERCISE : fill in a valid value; Q: can you use null?
name|testUser
operator|.
name|changePassword
argument_list|(
name|newPassword
argument_list|,
name|oldPassword
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
