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
name|Date
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
name|List
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

begin_comment
comment|/**  * TokenProviderImplTest...  */
end_comment

begin_class
specifier|public
class|class
name|TokenProviderImplTest
extends|extends
name|AbstractTokenTest
block|{
specifier|private
name|String
name|userId
decl_stmt|;
annotation|@
name|Override
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
name|Test
specifier|public
name|void
name|testDoCreateToken
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|tokenProvider
operator|.
name|doCreateToken
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tokenProvider
operator|.
name|doCreateToken
argument_list|(
operator|new
name|TokenCredentials
argument_list|(
literal|"token"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tokenProvider
operator|.
name|doCreateToken
argument_list|(
name|getAdminCredentials
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleCredentials
name|sc
init|=
operator|new
name|SimpleCredentials
argument_list|(
literal|"uid"
argument_list|,
literal|"pw"
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tokenProvider
operator|.
name|doCreateToken
argument_list|(
name|sc
argument_list|)
argument_list|)
expr_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|"any_attribute"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tokenProvider
operator|.
name|doCreateToken
argument_list|(
name|sc
argument_list|)
argument_list|)
expr_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|"rep:token_key"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tokenProvider
operator|.
name|doCreateToken
argument_list|(
name|sc
argument_list|)
argument_list|)
expr_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|".token"
argument_list|,
literal|"existing"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tokenProvider
operator|.
name|doCreateToken
argument_list|(
name|sc
argument_list|)
argument_list|)
expr_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|".token"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tokenProvider
operator|.
name|doCreateToken
argument_list|(
name|sc
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateTokenFromInvalidCredentials
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
name|TokenCredentials
argument_list|(
literal|"sometoken"
argument_list|)
argument_list|)
expr_stmt|;
name|invalid
operator|.
name|add
argument_list|(
operator|new
name|ImpersonationCredentials
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|invalid
operator|.
name|add
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"unknownUserId"
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
name|assertNull
argument_list|(
name|tokenProvider
operator|.
name|createToken
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
name|testCreateTokenFromCredentials
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleCredentials
name|sc
init|=
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
decl_stmt|;
name|List
argument_list|<
name|Credentials
argument_list|>
name|valid
init|=
operator|new
name|ArrayList
argument_list|<
name|Credentials
argument_list|>
argument_list|()
decl_stmt|;
name|valid
operator|.
name|add
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|valid
operator|.
name|add
argument_list|(
operator|new
name|ImpersonationCredentials
argument_list|(
name|sc
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Credentials
name|creds
range|:
name|valid
control|)
block|{
name|TokenInfo
name|info
init|=
name|tokenProvider
operator|.
name|createToken
argument_list|(
name|creds
argument_list|)
decl_stmt|;
name|assertTokenInfo
argument_list|(
name|info
argument_list|,
name|userId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateTokenFromInvalidUserId
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
literal|"unknownUserId"
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
name|assertNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateTokenFromUserId
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
name|assertTokenInfo
argument_list|(
name|info
argument_list|,
name|userId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTokenNode
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|reserved
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|reserved
operator|.
name|put
argument_list|(
literal|".token"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|reserved
operator|.
name|put
argument_list|(
literal|"rep:token.key"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|reserved
operator|.
name|put
argument_list|(
literal|"rep:token.exp"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|privateAttributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|privateAttributes
operator|.
name|put
argument_list|(
literal|".token_exp"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|privateAttributes
operator|.
name|put
argument_list|(
literal|".tokenTest"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|privateAttributes
operator|.
name|put
argument_list|(
literal|".token_something"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|publicAttributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|publicAttributes
operator|.
name|put
argument_list|(
literal|"any"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|publicAttributes
operator|.
name|put
argument_list|(
literal|"another"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|attributes
operator|.
name|putAll
argument_list|(
name|reserved
argument_list|)
expr_stmt|;
name|attributes
operator|.
name|putAll
argument_list|(
name|publicAttributes
argument_list|)
expr_stmt|;
name|attributes
operator|.
name|putAll
argument_list|(
name|privateAttributes
argument_list|)
expr_stmt|;
name|TokenInfo
name|info
init|=
name|tokenProvider
operator|.
name|createToken
argument_list|(
name|userId
argument_list|,
name|attributes
argument_list|)
decl_stmt|;
name|Tree
name|userTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|Tree
name|tokens
init|=
name|userTree
operator|.
name|getChild
argument_list|(
literal|".tokens"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tokens
operator|.
name|getChildrenCount
argument_list|()
argument_list|)
expr_stmt|;
name|Tree
name|tokenNode
init|=
name|tokens
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|tokenNode
operator|.
name|getProperty
argument_list|(
literal|"rep:token.key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|tokenNode
operator|.
name|getProperty
argument_list|(
literal|"rep:token.exp"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|key
range|:
name|reserved
operator|.
name|keySet
argument_list|()
control|)
block|{
name|PropertyState
name|p
init|=
name|tokenNode
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|assertFalse
argument_list|(
name|reserved
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|equals
argument_list|(
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|key
range|:
name|privateAttributes
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|privateAttributes
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|,
name|tokenNode
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|key
range|:
name|publicAttributes
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|publicAttributes
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|,
name|tokenNode
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTokenInfoFromInvalidToken
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|invalid
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|invalid
operator|.
name|add
argument_list|(
literal|"/invalid"
argument_list|)
expr_stmt|;
name|invalid
operator|.
name|add
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|token
range|:
name|invalid
control|)
block|{
name|TokenInfo
name|info
init|=
name|tokenProvider
operator|.
name|getTokenInfo
argument_list|(
name|token
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|assertNull
argument_list|(
name|tokenProvider
operator|.
name|getTokenInfo
argument_list|(
literal|"invalidToken"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
name|testGetTokenInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|token
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
operator|.
name|getToken
argument_list|()
decl_stmt|;
name|TokenInfo
name|info
init|=
name|tokenProvider
operator|.
name|getTokenInfo
argument_list|(
name|token
argument_list|)
decl_stmt|;
name|assertTokenInfo
argument_list|(
name|info
argument_list|,
name|userId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveTokenInvalidInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|tokenProvider
operator|.
name|removeToken
argument_list|(
operator|new
name|InvalidTokenInfo
argument_list|()
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
name|tokenProvider
operator|.
name|removeToken
argument_list|(
name|info
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveToken2
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
name|tokenProvider
operator|.
name|removeToken
argument_list|(
name|tokenProvider
operator|.
name|getTokenInfo
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
name|testRemoveTokenRemovesNode
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
name|Tree
name|userTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|Tree
name|tokens
init|=
name|userTree
operator|.
name|getChild
argument_list|(
literal|".tokens"
argument_list|)
decl_stmt|;
name|String
name|tokenNodePath
init|=
name|tokens
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|tokenProvider
operator|.
name|removeToken
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|tokenNodePath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testResetTokenExpirationInvalidToken
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|tokenProvider
operator|.
name|resetTokenExpiration
argument_list|(
operator|new
name|InvalidTokenInfo
argument_list|()
argument_list|,
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testResetTokenExpirationExpiredToken
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
name|long
name|expiredTime
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|+
literal|7200001
decl_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|isExpired
argument_list|(
name|expiredTime
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tokenProvider
operator|.
name|resetTokenExpiration
argument_list|(
name|info
argument_list|,
name|expiredTime
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testResetTokenExpiration
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
name|assertFalse
argument_list|(
name|tokenProvider
operator|.
name|resetTokenExpiration
argument_list|(
name|info
argument_list|,
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|loginTime
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|+
literal|3600000
decl_stmt|;
name|assertFalse
argument_list|(
name|info
operator|.
name|isExpired
argument_list|(
name|loginTime
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tokenProvider
operator|.
name|resetTokenExpiration
argument_list|(
name|info
argument_list|,
name|loginTime
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------------------------------
specifier|private
specifier|static
name|void
name|assertTokenInfo
parameter_list|(
name|TokenInfo
name|info
parameter_list|,
name|String
name|userId
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|info
operator|.
name|getToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|userId
argument_list|,
name|info
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|info
operator|.
name|isExpired
argument_list|(
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
class|class
name|InvalidTokenInfo
implements|implements
name|TokenInfo
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getUserId
parameter_list|()
block|{
return|return
literal|"invalid"
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getToken
parameter_list|()
block|{
return|return
literal|"invalid"
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isExpired
parameter_list|(
name|long
name|loginTime
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|TokenCredentials
name|tokenCredentials
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getPrivateAttributes
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getPublicAttributes
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

