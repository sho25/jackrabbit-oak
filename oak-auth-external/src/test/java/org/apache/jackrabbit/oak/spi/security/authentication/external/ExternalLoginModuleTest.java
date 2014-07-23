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
operator|.
name|external
package|;
end_package

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
name|Authorizable
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
name|namepath
operator|.
name|NamePathMapper
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
name|plugins
operator|.
name|value
operator|.
name|ValueFactoryImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
comment|/**  * ExternalLoginModuleTest...  */
end_comment

begin_class
specifier|public
class|class
name|ExternalLoginModuleTest
extends|extends
name|ExternalLoginModuleTestBase
block|{
specifier|protected
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|options
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
name|userId
init|=
literal|"testUser"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_CONSTANT_PROPERTY_NAME
init|=
literal|"profile/constantProperty"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_CONSTANT_PROPERTY_VALUE
init|=
literal|"constant-value"
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
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|ExternalIdentityProvider
name|createIDP
parameter_list|()
block|{
return|return
operator|new
name|TestIdentityProvider
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|destroyIDP
parameter_list|(
name|ExternalIdentityProvider
name|idp
parameter_list|)
block|{
comment|// ignore
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoginFailed
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManager
name|userManager
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
try|try
block|{
name|ContentSession
name|cs
init|=
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"unknown"
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"login failure expected"
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
finally|finally
block|{
name|assertNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncCreateUser
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManager
name|userManager
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|assertNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
argument_list|)
expr_stmt|;
name|cs
operator|=
name|login
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
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|ExternalUser
name|user
init|=
name|idp
operator|.
name|getUser
argument_list|(
name|userId
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|user
operator|.
name|getProperties
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
name|prop
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|TEST_CONSTANT_PROPERTY_VALUE
argument_list|,
name|a
operator|.
name|getProperty
argument_list|(
name|TEST_CONSTANT_PROPERTY_NAME
argument_list|)
index|[
literal|0
index|]
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
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
name|options
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncCreateUserCaseInsensitive
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManager
name|userManager
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|assertNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
argument_list|)
expr_stmt|;
name|cs
operator|=
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|userId
operator|.
name|toUpperCase
argument_list|()
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|ExternalUser
name|user
init|=
name|idp
operator|.
name|getUser
argument_list|(
name|userId
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|user
operator|.
name|getProperties
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
name|prop
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|TEST_CONSTANT_PROPERTY_VALUE
argument_list|,
name|a
operator|.
name|getProperty
argument_list|(
name|TEST_CONSTANT_PROPERTY_NAME
argument_list|)
index|[
literal|0
index|]
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
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
name|options
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncCreateGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManager
name|userManager
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|login
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
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|}
control|)
block|{
name|assertNotNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|id
range|:
operator|new
name|String
index|[]
block|{
literal|"aa"
block|,
literal|"aaa"
block|}
control|)
block|{
name|assertNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
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
name|options
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncCreateGroupNesting
parameter_list|()
throws|throws
name|Exception
block|{
name|syncConfig
operator|.
name|user
argument_list|()
operator|.
name|setMembershipNestingDepth
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|UserManager
name|userManager
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|login
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
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|,
literal|"aa"
block|,
literal|"aaa"
block|}
control|)
block|{
name|assertNotNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
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
name|options
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncUpdate
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create user upfront in order to test update mode
name|UserManager
name|userManager
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|ExternalUser
name|externalUser
init|=
name|idp
operator|.
name|getUser
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|Authorizable
name|user
init|=
name|userManager
operator|.
name|createUser
argument_list|(
name|externalUser
operator|.
name|getId
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|user
operator|.
name|setProperty
argument_list|(
literal|"rep:externalId"
argument_list|,
operator|new
name|ValueFactoryImpl
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
operator|.
name|createValue
argument_list|(
name|externalUser
operator|.
name|getExternalId
argument_list|()
operator|.
name|getString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|login
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
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|externalUser
operator|.
name|getProperties
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
name|prop
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
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
name|options
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

