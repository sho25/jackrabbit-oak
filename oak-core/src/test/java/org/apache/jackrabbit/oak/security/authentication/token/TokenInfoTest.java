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
name|Map
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * TokenInfoTest...  */
end_comment

begin_class
specifier|public
class|class
name|TokenInfoTest
extends|extends
name|AbstractTokenTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testGetUserId
parameter_list|()
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
name|info
operator|=
name|tokenProvider
operator|.
name|getTokenInfo
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetToken
parameter_list|()
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
name|assertNotNull
argument_list|(
name|info
operator|.
name|getToken
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|=
name|tokenProvider
operator|.
name|getTokenInfo
argument_list|(
name|info
operator|.
name|getToken
argument_list|()
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsExpired
parameter_list|()
block|{
name|long
name|loginTime
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
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
name|info
operator|.
name|isExpired
argument_list|(
name|loginTime
argument_list|)
argument_list|)
expr_stmt|;
name|loginTime
operator|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|+
literal|3600000
expr_stmt|;
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
literal|7200000
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatches
parameter_list|()
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
name|info
operator|.
name|matches
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
name|put
argument_list|(
literal|"something"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|info
operator|=
name|tokenProvider
operator|.
name|createToken
argument_list|(
name|userId
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|matches
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
name|attributes
operator|.
name|put
argument_list|(
literal|".token-something"
argument_list|,
literal|"mandatory"
argument_list|)
expr_stmt|;
name|info
operator|=
name|tokenProvider
operator|.
name|createToken
argument_list|(
name|userId
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|info
operator|.
name|matches
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
name|TokenCredentials
name|tc
init|=
operator|new
name|TokenCredentials
argument_list|(
name|info
operator|.
name|getToken
argument_list|()
argument_list|)
decl_stmt|;
name|tc
operator|.
name|setAttribute
argument_list|(
literal|".token-something"
argument_list|,
literal|"mandatory"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|matches
argument_list|(
name|tc
argument_list|)
argument_list|)
expr_stmt|;
name|tc
operator|.
name|setAttribute
argument_list|(
literal|"another"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|matches
argument_list|(
name|tc
argument_list|)
argument_list|)
expr_stmt|;
name|tc
operator|.
name|setAttribute
argument_list|(
literal|".token_ignored"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|matches
argument_list|(
name|tc
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAttributes
parameter_list|()
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
literal|".token.key"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|reserved
operator|.
name|put
argument_list|(
literal|".token.exp"
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
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|pubAttr
init|=
name|info
operator|.
name|getPublicAttributes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|publicAttributes
operator|.
name|size
argument_list|()
argument_list|,
name|pubAttr
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
name|assertTrue
argument_list|(
name|pubAttr
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|publicAttributes
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|,
name|pubAttr
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|privAttr
init|=
name|info
operator|.
name|getPrivateAttributes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|privateAttributes
operator|.
name|size
argument_list|()
argument_list|,
name|privAttr
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
name|assertTrue
argument_list|(
name|privAttr
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|privateAttributes
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|,
name|privAttr
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|assertFalse
argument_list|(
name|privAttr
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pubAttr
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

