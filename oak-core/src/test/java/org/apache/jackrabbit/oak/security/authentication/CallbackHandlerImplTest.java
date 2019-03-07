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
name|NameCallback
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
name|PasswordCallback
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
name|authentication
operator|.
name|LoginModuleMonitor
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
name|authentication
operator|.
name|callback
operator|.
name|RepositoryCallback
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
name|WhiteboardCallback
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
name|whiteboard
operator|.
name|DefaultWhiteboard
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
name|whiteboard
operator|.
name|Whiteboard
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
name|assertArrayEquals
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
name|assertSame
import|;
end_import

begin_class
specifier|public
class|class
name|CallbackHandlerImplTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|final
name|SimpleCredentials
name|simpleCreds
init|=
operator|new
name|SimpleCredentials
argument_list|(
literal|"id"
argument_list|,
literal|"pw"
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Whiteboard
name|whiteboard
init|=
operator|new
name|DefaultWhiteboard
argument_list|()
decl_stmt|;
specifier|private
name|CallbackHandlerImpl
name|callbackHandler
decl_stmt|;
annotation|@
name|Override
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
name|callbackHandler
operator|=
name|create
argument_list|(
name|simpleCreds
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CallbackHandlerImpl
name|create
parameter_list|(
annotation|@
name|NotNull
name|Credentials
name|creds
parameter_list|)
block|{
return|return
operator|new
name|CallbackHandlerImpl
argument_list|(
name|creds
argument_list|,
name|root
operator|.
name|getContentSession
argument_list|()
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|getContentRepository
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|,
name|whiteboard
argument_list|,
name|LoginModuleMonitor
operator|.
name|NOOP
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCredentialsCallback
parameter_list|()
throws|throws
name|Exception
block|{
name|CredentialsCallback
name|cb
init|=
operator|new
name|CredentialsCallback
argument_list|()
decl_stmt|;
name|callbackHandler
operator|.
name|handle
argument_list|(
operator|new
name|Callback
index|[]
block|{
name|cb
block|}
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|simpleCreds
argument_list|,
name|cb
operator|.
name|getCredentials
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|handlePasswordCallback
parameter_list|()
throws|throws
name|Exception
block|{
name|PasswordCallback
name|cb
init|=
operator|new
name|PasswordCallback
argument_list|(
literal|"prompt"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|callbackHandler
operator|.
name|handle
argument_list|(
operator|new
name|Callback
index|[]
block|{
name|cb
block|}
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|simpleCreds
operator|.
name|getPassword
argument_list|()
argument_list|,
name|cb
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|handlePasswordCallback2
parameter_list|()
throws|throws
name|Exception
block|{
name|PasswordCallback
name|cb
init|=
operator|new
name|PasswordCallback
argument_list|(
literal|"prompt"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|create
argument_list|(
operator|new
name|Credentials
argument_list|()
block|{}
argument_list|)
operator|.
name|handle
argument_list|(
operator|new
name|Callback
index|[]
block|{
name|cb
block|}
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cb
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|handleNameCallback
parameter_list|()
throws|throws
name|Exception
block|{
name|NameCallback
name|cb
init|=
operator|new
name|NameCallback
argument_list|(
literal|"prompt"
argument_list|)
decl_stmt|;
name|callbackHandler
operator|.
name|handle
argument_list|(
operator|new
name|Callback
index|[]
block|{
name|cb
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id"
argument_list|,
name|cb
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|handleNameCallback2
parameter_list|()
throws|throws
name|Exception
block|{
name|NameCallback
name|cb
init|=
operator|new
name|NameCallback
argument_list|(
literal|"prompt"
argument_list|)
decl_stmt|;
name|create
argument_list|(
operator|new
name|Credentials
argument_list|()
block|{}
argument_list|)
operator|.
name|handle
argument_list|(
operator|new
name|Callback
index|[]
block|{
name|cb
block|}
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cb
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|handleWhiteboardCallback
parameter_list|()
throws|throws
name|Exception
block|{
name|WhiteboardCallback
name|cb
init|=
operator|new
name|WhiteboardCallback
argument_list|()
decl_stmt|;
name|callbackHandler
operator|.
name|handle
argument_list|(
operator|new
name|Callback
index|[]
block|{
name|cb
block|}
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|whiteboard
argument_list|,
name|cb
operator|.
name|getWhiteboard
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|handleRepositoryCallback
parameter_list|()
throws|throws
name|Exception
block|{
name|RepositoryCallback
name|cb
init|=
operator|new
name|RepositoryCallback
argument_list|()
decl_stmt|;
name|callbackHandler
operator|.
name|handle
argument_list|(
operator|new
name|Callback
index|[]
block|{
name|cb
block|}
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|getContentRepository
argument_list|()
argument_list|,
name|cb
operator|.
name|getContentRepository
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|,
name|cb
operator|.
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|root
operator|.
name|getContentSession
argument_list|()
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|cb
operator|.
name|getWorkspaceName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|UnsupportedCallbackException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|handleUnknownCallback
parameter_list|()
throws|throws
name|Exception
block|{
name|callbackHandler
operator|.
name|handle
argument_list|(
operator|new
name|Callback
index|[]
block|{
operator|new
name|Callback
argument_list|()
block|{
block|}
block|}
block|)
class|;
end_class

unit|}  }
end_unit

