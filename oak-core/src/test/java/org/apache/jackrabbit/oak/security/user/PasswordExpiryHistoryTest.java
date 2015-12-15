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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|SecurityProvider
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
name|action
operator|.
name|AuthorizableAction
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
name|action
operator|.
name|AuthorizableActionProvider
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
name|action
operator|.
name|PasswordValidationAction
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
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|java
operator|.
name|util
operator|.
name|List
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
name|fail
import|;
end_import

begin_comment
comment|/**  * @see<a href="https://issues.apache.org/jira/browse/OAK-3463">OAK-3463</a>  */
end_comment

begin_class
specifier|public
class|class
name|PasswordExpiryHistoryTest
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
specifier|final
name|PasswordValidationAction
name|pwAction
init|=
operator|new
name|PasswordValidationAction
argument_list|()
decl_stmt|;
name|pwAction
operator|.
name|init
argument_list|(
literal|null
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|PasswordValidationAction
operator|.
name|CONSTRAINT
argument_list|,
literal|"^.*(?=.{4,}).*"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|AuthorizableActionProvider
name|actionProvider
init|=
operator|new
name|AuthorizableActionProvider
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|AuthorizableAction
argument_list|>
name|getAuthorizableActions
parameter_list|(
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|pwAction
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|ConfigurationParameters
name|userConfig
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|UserConstants
operator|.
name|PARAM_AUTHORIZABLE_ACTION_PROVIDER
argument_list|,
name|actionProvider
argument_list|,
name|UserConstants
operator|.
name|PARAM_PASSWORD_MAX_AGE
argument_list|,
literal|10
argument_list|,
name|UserConstants
operator|.
name|PARAM_PASSWORD_HISTORY_SIZE
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|,
name|userConfig
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthenticatePasswordExpiredAndSame
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|user
init|=
name|getTestUser
argument_list|()
decl_stmt|;
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
comment|// set password last modified to beginning of epoch
name|root
operator|.
name|getTree
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getChild
argument_list|(
name|UserConstants
operator|.
name|REP_PWD
argument_list|)
operator|.
name|setProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD_LAST_MODIFIED
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
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
comment|// success, credentials are expired
comment|// try to change password to the same one, this should fail due pw history
name|SimpleCredentials
name|pwChangeCreds
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
try|try
block|{
name|pwChangeCreds
operator|.
name|setAttribute
argument_list|(
name|UserConstants
operator|.
name|CREDENTIALS_ATTRIBUTE_NEWPASSWORD
argument_list|,
name|user
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|.
name|authenticate
argument_list|(
name|pwChangeCreds
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"User password changed in spite of enabled pw history"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CredentialExpiredException
name|c
parameter_list|)
block|{
comment|// success, pw found in history
name|Object
name|attr
init|=
name|pwChangeCreds
operator|.
name|getAttribute
argument_list|(
name|PasswordHistoryException
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"credentials should contain pw change failure reason"
argument_list|,
literal|"New password is identical to the current password."
argument_list|,
name|attr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthenticatePasswordExpiredAndInHistory
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"pw12345678"
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
comment|// set password last modified to beginning of epoch
name|root
operator|.
name|getTree
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getChild
argument_list|(
name|UserConstants
operator|.
name|REP_PWD
argument_list|)
operator|.
name|setProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD_LAST_MODIFIED
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
name|a
operator|.
name|authenticate
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|userId
argument_list|,
literal|"pw12345678"
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
comment|// success, credentials are expired
comment|// try to change password to the same one, this should fail due pw history
name|SimpleCredentials
name|pwChangeCreds
init|=
operator|new
name|SimpleCredentials
argument_list|(
name|userId
argument_list|,
literal|"pw12345678"
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|pwChangeCreds
operator|.
name|setAttribute
argument_list|(
name|UserConstants
operator|.
name|CREDENTIALS_ATTRIBUTE_NEWPASSWORD
argument_list|,
name|user
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|.
name|authenticate
argument_list|(
name|pwChangeCreds
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"User password changed in spite of enabled pw history"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CredentialExpiredException
name|c
parameter_list|)
block|{
comment|// success, pw found in history
name|Object
name|attr
init|=
name|pwChangeCreds
operator|.
name|getAttribute
argument_list|(
name|PasswordHistoryException
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"credentials should contain pw change failure reason"
argument_list|,
literal|"New password was found in password history."
argument_list|,
name|attr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthenticatePasswordExpiredAndValidationFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|user
init|=
name|getTestUser
argument_list|()
decl_stmt|;
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
comment|// set password last modified to beginning of epoch
name|root
operator|.
name|getTree
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getChild
argument_list|(
name|UserConstants
operator|.
name|REP_PWD
argument_list|)
operator|.
name|setProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD_LAST_MODIFIED
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
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
comment|// success, credentials are expired
comment|// try to change password to the same one, this should fail due pw history
name|SimpleCredentials
name|pwChangeCreds
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
try|try
block|{
name|pwChangeCreds
operator|.
name|setAttribute
argument_list|(
name|UserConstants
operator|.
name|CREDENTIALS_ATTRIBUTE_NEWPASSWORD
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|a
operator|.
name|authenticate
argument_list|(
name|pwChangeCreds
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"User password changed in spite of expected validation failure"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CredentialExpiredException
name|c
parameter_list|)
block|{
comment|// success, pw found in history
name|assertNull
argument_list|(
name|pwChangeCreds
operator|.
name|getAttribute
argument_list|(
name|PasswordHistoryException
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

