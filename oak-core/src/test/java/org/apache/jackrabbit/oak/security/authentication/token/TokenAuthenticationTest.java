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
name|ArrayList
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
name|List
import|;
end_import

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
name|Credentials
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
comment|/**  * TokenAuthenticationTest...  */
end_comment

begin_class
specifier|public
class|class
name|TokenAuthenticationTest
extends|extends
name|AbstractSecurityTest
block|{
name|TokenAuthentication
name|authentication
decl_stmt|;
name|TokenProviderImpl
name|tokenProvider
decl_stmt|;
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
name|tokenProvider
operator|=
operator|new
name|TokenProviderImpl
argument_list|(
name|root
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
name|getUserConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|authentication
operator|=
operator|new
name|TokenAuthentication
argument_list|(
name|tokenProvider
argument_list|)
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
name|Test
specifier|public
name|void
name|testAuthenticateWithoutTokenProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|Authentication
name|authentication
init|=
operator|new
name|TokenAuthentication
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|authentication
operator|.
name|authenticate
argument_list|(
operator|new
name|TokenCredentials
argument_list|(
literal|"token"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthenticateWithInvalidCredentials
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Credentials
argument_list|>
name|invalid
init|=
operator|new
name|ArrayList
argument_list|<
name|Credentials
argument_list|>
argument_list|()
decl_stmt|;
name|invalid
operator|.
name|add
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|)
expr_stmt|;
name|invalid
operator|.
name|add
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|userId
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Credentials
name|creds
range|:
name|invalid
control|)
block|{
name|assertFalse
argument_list|(
name|authentication
operator|.
name|authenticate
argument_list|(
name|creds
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthenticateWithInvalidTokenCredentials
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|authentication
operator|.
name|authenticate
argument_list|(
operator|new
name|TokenCredentials
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"LoginException expected"
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
annotation|@
name|Test
specifier|public
name|void
name|testAuthenticate
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenInfo
name|info
init|=
name|tokenProvider
operator|.
name|createToken
argument_list|(
name|userId
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
name|assertTrue
argument_list|(
name|authentication
operator|.
name|authenticate
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTokenInfoBeforeAuthenticate
parameter_list|()
block|{
try|try
block|{
name|authentication
operator|.
name|getTokenInfo
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"IllegalStateException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
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
name|testGetTokenInfoAfterAuthenticate
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenInfo
name|info
init|=
name|tokenProvider
operator|.
name|createToken
argument_list|(
name|userId
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
name|authentication
operator|.
name|authenticate
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
expr_stmt|;
name|TokenInfo
name|info2
init|=
name|authentication
operator|.
name|getTokenInfo
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|info2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|info
operator|.
name|getUserId
argument_list|()
argument_list|,
name|info2
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthenticateNotMatchingToken
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenInfo
name|info
init|=
name|tokenProvider
operator|.
name|createToken
argument_list|(
name|userId
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|TokenConstants
operator|.
name|TOKEN_ATTRIBUTE
operator|+
literal|"_mandatory"
argument_list|,
literal|"val"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|authentication
operator|.
name|authenticate
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
expr_stmt|;
name|fail
argument_list|(
literal|"LoginException expected"
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
annotation|@
name|Test
specifier|public
name|void
name|testAuthenticateExpiredToken
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenProvider
name|tp
init|=
operator|new
name|TokenProviderImpl
argument_list|(
name|root
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|TokenProvider
operator|.
name|PARAM_TOKEN_EXPIRATION
argument_list|,
literal|1
argument_list|)
argument_list|,
name|getUserConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|TokenInfo
name|info
init|=
name|tp
operator|.
name|createToken
argument_list|(
name|userId
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
name|waitUntilExpired
argument_list|(
name|info
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|TokenAuthentication
argument_list|(
name|tp
argument_list|)
operator|.
name|authenticate
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
expr_stmt|;
name|fail
argument_list|(
literal|"LoginException expected"
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
comment|// expired token must have been removed
name|assertNull
argument_list|(
name|tp
operator|.
name|getTokenInfo
argument_list|(
name|info
operator|.
name|getToken
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|waitUntilExpired
parameter_list|(
annotation|@
name|Nonnull
name|TokenInfo
name|info
parameter_list|)
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|info
operator|.
name|isExpired
argument_list|(
name|now
argument_list|)
condition|)
block|{
name|now
operator|=
name|waitForSystemTimeIncrement
argument_list|(
name|now
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

