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
name|user
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|CredentialExpiredException
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
name|PropertyState
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
name|Tree
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
name|Type
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
name|Authentication
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
operator|.
name|CREDENTIALS_ATTRIBUTE_NEWPASSWORD
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
name|spi
operator|.
name|security
operator|.
name|user
operator|.
name|UserConstants
operator|.
name|REP_PWD
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
comment|/**  * @see<a href="https://issues.apache.org/jira/browse/OAK-1922">OAK-1922</a>  */
end_comment

begin_class
specifier|public
class|class
name|PasswordExpiryAndForceInitialChangeTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|String
name|userId
decl_stmt|;
annotation|@
name|Before
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
name|userId
operator|=
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
name|ConfigurationParameters
name|parameters
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_MAX_AGE
argument_list|,
literal|10
argument_list|,
name|UserConstants
operator|.
name|PARAM_PASSWORD_INITIAL_CHANGE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|,
name|parameters
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|Tree
name|getUserTree
parameter_list|(
annotation|@
name|NotNull
name|User
name|user
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateUser
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|newUserId
init|=
literal|"newuser"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
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
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createUser
argument_list|(
name|newUserId
argument_list|,
name|newUserId
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|getUserTree
argument_list|(
name|user
argument_list|)
operator|.
name|hasChild
argument_list|(
name|UserConstants
operator|.
name|REP_PWD
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|user
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PWD
operator|+
literal|"/"
operator|+
name|UserConstants
operator|.
name|REP_PASSWORD_LAST_MODIFIED
argument_list|)
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthenticateMustChangePassword
parameter_list|()
throws|throws
name|Exception
block|{
name|Authentication
name|a
init|=
operator|new
name|UserAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
name|userId
argument_list|)
decl_stmt|;
try|try
block|{
comment|// the user should need to change the password on first login
name|a
operator|.
name|authenticate
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|userId
argument_list|,
name|userId
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Credentials should be expired"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CredentialExpiredException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChangePasswordReset
parameter_list|()
throws|throws
name|Exception
block|{
comment|// once the user changes the password, the login should succeed
name|User
name|user
init|=
name|getTestUser
argument_list|()
decl_stmt|;
name|user
operator|.
name|changePassword
argument_list|(
name|userId
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|PropertyState
name|p
init|=
name|getUserTree
argument_list|(
name|user
argument_list|)
operator|.
name|getChild
argument_list|(
name|UserConstants
operator|.
name|REP_PWD
argument_list|)
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD_LAST_MODIFIED
argument_list|)
decl_stmt|;
name|long
name|newModTime
init|=
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newModTime
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Authentication
name|a
init|=
operator|new
name|UserAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
name|userId
argument_list|)
decl_stmt|;
comment|// during user creation pw last modified is set, thus it shouldn't expire
name|a
operator|.
name|authenticate
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|userId
argument_list|,
name|userId
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * rep:passwordLastModified must NOT be created otherwise the user might never be forced to change pw upon first login.      */
annotation|@
name|Test
specifier|public
name|void
name|testSetPasswordImportExistingUser
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManagerImpl
name|userManager
init|=
operator|(
name|UserManagerImpl
operator|)
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|Tree
name|userTree
init|=
name|getUserTree
argument_list|(
name|getTestUser
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|userTree
operator|.
name|hasChild
argument_list|(
name|REP_PWD
argument_list|)
argument_list|)
expr_stmt|;
name|userManager
operator|.
name|setPassword
argument_list|(
name|userTree
argument_list|,
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|,
literal|"pwd"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|userTree
operator|.
name|hasChild
argument_list|(
name|REP_PWD
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * rep:passwordLastModified must NOT be created in accordance to UserManager.createUser      */
annotation|@
name|Test
specifier|public
name|void
name|testSetPasswordImportNewUser
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManagerImpl
name|userManager
init|=
operator|(
name|UserManagerImpl
operator|)
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|User
name|u
init|=
name|userManager
operator|.
name|createUser
argument_list|(
literal|"uNew"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Tree
name|userTree
init|=
name|getUserTree
argument_list|(
name|u
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|userTree
operator|.
name|hasChild
argument_list|(
name|REP_PWD
argument_list|)
argument_list|)
expr_stmt|;
name|userManager
operator|.
name|setPassword
argument_list|(
name|userTree
argument_list|,
literal|"uNew"
argument_list|,
literal|"pwd"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|userTree
operator|.
name|hasChild
argument_list|(
name|REP_PWD
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthenticateWithNewPasswordAttribute
parameter_list|()
throws|throws
name|Exception
block|{
name|Authentication
name|a
init|=
operator|new
name|UserAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|SimpleCredentials
name|sc
init|=
operator|new
name|SimpleCredentials
argument_list|(
name|userId
argument_list|,
name|userId
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
name|CREDENTIALS_ATTRIBUTE_NEWPASSWORD
argument_list|,
literal|"SureChangedMyPassword!"
argument_list|)
expr_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|a
operator|.
name|authenticate
argument_list|(
name|sc
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|assertNull
argument_list|(
name|sc
operator|.
name|getAttribute
argument_list|(
name|CREDENTIALS_ATTRIBUTE_NEWPASSWORD
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

