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
name|spi
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
name|HashMap
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
name|Subject
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
name|callback
operator|.
name|Callback
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
name|callback
operator|.
name|CallbackHandler
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
name|callback
operator|.
name|UnsupportedCallbackException
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
name|callback
operator|.
name|CredentialsCallback
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
name|principal
operator|.
name|EveryonePrincipal
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

begin_comment
comment|/**  * GuestLoginModuleTest...  */
end_comment

begin_class
specifier|public
class|class
name|GuestLoginModuleTest
block|{
specifier|private
name|LoginModule
name|guestLoginModule
init|=
operator|new
name|GuestLoginModule
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testNullLogin
parameter_list|()
throws|throws
name|LoginException
block|{
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|CallbackHandler
name|cbh
init|=
operator|new
name|TestCallbackHandler
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Map
name|sharedState
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|guestLoginModule
operator|.
name|initialize
argument_list|(
name|subject
argument_list|,
name|cbh
argument_list|,
name|sharedState
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
expr_stmt|;
name|assertTrue
argument_list|(
name|guestLoginModule
operator|.
name|login
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|sharedCreds
init|=
name|sharedState
operator|.
name|get
argument_list|(
name|AbstractLoginModule
operator|.
name|SHARED_KEY_CREDENTIALS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|sharedCreds
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sharedCreds
operator|instanceof
name|GuestCredentials
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|guestLoginModule
operator|.
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|subject
operator|.
name|getPrincipals
argument_list|(
name|EveryonePrincipal
operator|.
name|class
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|subject
operator|.
name|getPublicCredentials
argument_list|(
name|GuestCredentials
operator|.
name|class
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|guestLoginModule
operator|.
name|logout
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGuestCredentials
parameter_list|()
throws|throws
name|LoginException
block|{
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|CallbackHandler
name|cbh
init|=
operator|new
name|TestCallbackHandler
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|)
decl_stmt|;
name|Map
name|sharedState
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|guestLoginModule
operator|.
name|initialize
argument_list|(
name|subject
argument_list|,
name|cbh
argument_list|,
name|sharedState
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
expr_stmt|;
name|assertFalse
argument_list|(
name|guestLoginModule
operator|.
name|login
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sharedState
operator|.
name|containsKey
argument_list|(
name|AbstractLoginModule
operator|.
name|SHARED_KEY_CREDENTIALS
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|guestLoginModule
operator|.
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|subject
operator|.
name|getPublicCredentials
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|guestLoginModule
operator|.
name|logout
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleCredentials
parameter_list|()
throws|throws
name|LoginException
block|{
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|CallbackHandler
name|cbh
init|=
operator|new
name|TestCallbackHandler
argument_list|(
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
argument_list|)
decl_stmt|;
name|Map
name|sharedState
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|guestLoginModule
operator|.
name|initialize
argument_list|(
name|subject
argument_list|,
name|cbh
argument_list|,
name|sharedState
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
expr_stmt|;
name|assertFalse
argument_list|(
name|guestLoginModule
operator|.
name|login
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sharedState
operator|.
name|containsKey
argument_list|(
name|AbstractLoginModule
operator|.
name|SHARED_KEY_CREDENTIALS
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|guestLoginModule
operator|.
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|subject
operator|.
name|getPublicCredentials
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|guestLoginModule
operator|.
name|logout
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testThrowingCallbackhandler
parameter_list|()
throws|throws
name|LoginException
block|{
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|CallbackHandler
name|cbh
init|=
operator|new
name|ThrowingCallbackHandler
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Map
name|sharedState
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|guestLoginModule
operator|.
name|initialize
argument_list|(
name|subject
argument_list|,
name|cbh
argument_list|,
name|sharedState
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
expr_stmt|;
name|assertFalse
argument_list|(
name|guestLoginModule
operator|.
name|login
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sharedState
operator|.
name|containsKey
argument_list|(
name|AbstractLoginModule
operator|.
name|SHARED_KEY_CREDENTIALS
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|guestLoginModule
operator|.
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|subject
operator|.
name|getPublicCredentials
argument_list|(
name|GuestCredentials
operator|.
name|class
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|guestLoginModule
operator|.
name|logout
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testThrowingCallbackhandler2
parameter_list|()
throws|throws
name|LoginException
block|{
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|CallbackHandler
name|cbh
init|=
operator|new
name|ThrowingCallbackHandler
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Map
name|sharedState
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|guestLoginModule
operator|.
name|initialize
argument_list|(
name|subject
argument_list|,
name|cbh
argument_list|,
name|sharedState
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
expr_stmt|;
name|assertFalse
argument_list|(
name|guestLoginModule
operator|.
name|login
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sharedState
operator|.
name|containsKey
argument_list|(
name|AbstractLoginModule
operator|.
name|SHARED_KEY_CREDENTIALS
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|guestLoginModule
operator|.
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|subject
operator|.
name|getPublicCredentials
argument_list|(
name|GuestCredentials
operator|.
name|class
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|guestLoginModule
operator|.
name|logout
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAbort
parameter_list|()
throws|throws
name|LoginException
block|{
name|assertTrue
argument_list|(
name|guestLoginModule
operator|.
name|abort
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLogout
parameter_list|()
throws|throws
name|LoginException
block|{
name|assertFalse
argument_list|(
name|guestLoginModule
operator|.
name|logout
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------------------------------
specifier|private
class|class
name|TestCallbackHandler
implements|implements
name|CallbackHandler
block|{
specifier|private
specifier|final
name|Credentials
name|creds
decl_stmt|;
specifier|private
name|TestCallbackHandler
parameter_list|(
name|Credentials
name|creds
parameter_list|)
block|{
name|this
operator|.
name|creds
operator|=
name|creds
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|Callback
index|[]
name|callbacks
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnsupportedCallbackException
block|{
for|for
control|(
name|Callback
name|callback
range|:
name|callbacks
control|)
block|{
if|if
condition|(
name|callback
operator|instanceof
name|CredentialsCallback
condition|)
block|{
operator|(
operator|(
name|CredentialsCallback
operator|)
name|callback
operator|)
operator|.
name|setCredentials
argument_list|(
name|creds
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedCallbackException
argument_list|(
name|callback
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

