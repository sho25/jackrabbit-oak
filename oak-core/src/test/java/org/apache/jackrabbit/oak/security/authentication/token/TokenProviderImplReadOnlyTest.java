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
name|security
operator|.
name|AccessControlManager
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
name|JackrabbitAccessControlList
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
name|commons
operator|.
name|jackrabbit
operator|.
name|authorization
operator|.
name|AccessControlUtils
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
name|privilege
operator|.
name|PrivilegeConstants
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
name|assertNull
import|;
end_import

begin_class
specifier|public
class|class
name|TokenProviderImplReadOnlyTest
extends|extends
name|AbstractTokenTest
block|{
specifier|private
name|ContentSession
name|cs
decl_stmt|;
specifier|private
name|Root
name|readOnlyRoot
decl_stmt|;
specifier|private
name|TokenProvider
name|readOnlyTp
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
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|String
name|userPath
init|=
name|getTestUser
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|userPath
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|userPath
argument_list|,
name|acl
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
name|GuestCredentials
argument_list|()
argument_list|)
expr_stmt|;
name|readOnlyRoot
operator|=
name|cs
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|readOnlyTp
operator|=
operator|new
name|TokenProviderImpl
argument_list|(
name|readOnlyRoot
argument_list|,
name|getTokenConfig
argument_list|()
argument_list|,
name|getUserConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
try|try
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
finally|finally
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|generateToken
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenInfo
name|info
init|=
name|createTokenInfo
argument_list|(
name|tokenProvider
argument_list|,
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|token
init|=
name|info
operator|.
name|getToken
argument_list|()
decl_stmt|;
name|readOnlyRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
return|return
name|token
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateToken
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|userId
init|=
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
decl_stmt|;
name|readOnlyRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|readOnlyTp
operator|.
name|createToken
argument_list|(
name|userId
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateToken2
parameter_list|()
throws|throws
name|Exception
block|{
comment|// make sure user already has a token-parent node.
name|generateToken
argument_list|()
expr_stmt|;
comment|// now generate a new token with the read-only root
name|assertNull
argument_list|(
name|readOnlyTp
operator|.
name|createToken
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTokenInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenInfo
name|readOnlyInfo
init|=
name|readOnlyTp
operator|.
name|getTokenInfo
argument_list|(
name|generateToken
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|readOnlyInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRefreshToken
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenInfo
name|readOnlyInfo
init|=
name|readOnlyTp
operator|.
name|getTokenInfo
argument_list|(
name|generateToken
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|readOnlyInfo
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|readOnlyInfo
operator|.
name|resetExpiration
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|TokenProviderImpl
operator|.
name|DEFAULT_TOKEN_EXPIRATION
operator|-
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveToken
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenInfo
name|readOnlyInfo
init|=
name|readOnlyTp
operator|.
name|getTokenInfo
argument_list|(
name|generateToken
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|readOnlyInfo
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|readOnlyInfo
operator|.
name|remove
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

