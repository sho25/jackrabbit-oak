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
name|principal
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Set
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
name|principal
operator|.
name|PrincipalManager
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
name|Group
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
name|AbstractSecurityTest
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

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractPrincipalProviderTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|protected
name|PrincipalProvider
name|principalProvider
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
name|principalProvider
operator|=
name|createPrincipalProvider
argument_list|()
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|PrincipalProvider
name|createPrincipalProvider
parameter_list|()
function_decl|;
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipals
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|adminId
init|=
name|adminSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getUserID
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|principals
init|=
name|principalProvider
operator|.
name|getPrincipals
argument_list|(
name|adminId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|principals
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Principal
name|principal
range|:
name|principals
control|)
block|{
name|assertNotNull
argument_list|(
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserPrincipal
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
name|Principal
name|principal
init|=
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|user
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|principal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAdminPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|userId
init|=
name|adminSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getUserID
argument_list|()
decl_stmt|;
name|Authorizable
name|admin
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
if|if
condition|(
name|admin
operator|!=
literal|null
operator|&&
name|admin
operator|.
name|getPrincipal
argument_list|()
operator|instanceof
name|AdminPrincipal
condition|)
block|{
name|Principal
name|principal
init|=
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|admin
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|principal
operator|instanceof
name|AdminPrincipal
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|principals
init|=
name|principalProvider
operator|.
name|getPrincipals
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|boolean
name|containsAdminPrincipal
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Principal
name|p
range|:
name|principals
control|)
block|{
if|if
condition|(
name|p
operator|instanceof
name|AdminPrincipal
condition|)
block|{
name|containsAdminPrincipal
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
name|containsAdminPrincipal
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSystemUserPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|user
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createSystemUser
argument_list|(
literal|"testSystemUser"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
name|Principal
name|principal
init|=
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|user
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|principal
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|principal
operator|instanceof
name|SystemUserPrincipal
argument_list|)
expr_stmt|;
block|}
finally|finally
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
annotation|@
name|Test
specifier|public
name|void
name|testGroupPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|Group
name|group
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createGroup
argument_list|(
literal|"testGroup"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
name|Principal
name|principal
init|=
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|group
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|principal
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|principal
operator|instanceof
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|group
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
annotation|@
name|Test
specifier|public
name|void
name|testEveryone
parameter_list|()
throws|throws
name|Exception
block|{
name|Principal
name|everyone
init|=
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|everyone
operator|instanceof
name|EveryonePrincipal
argument_list|)
expr_stmt|;
name|Group
name|everyoneGroup
init|=
literal|null
decl_stmt|;
try|try
block|{
name|UserManager
name|userMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|everyoneGroup
operator|=
name|userMgr
operator|.
name|createGroup
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Principal
name|ep
init|=
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|ep
operator|instanceof
name|EveryonePrincipal
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|everyoneGroup
operator|!=
literal|null
condition|)
block|{
name|everyoneGroup
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
name|testFindUserPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|testUser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|UserManager
name|userMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|testUser
operator|=
name|userMgr
operator|.
name|createUser
argument_list|(
literal|"TestUser"
argument_list|,
literal|"pw"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|principalName
init|=
name|testUser
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|principalName
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|nameHints
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|nameHints
operator|.
name|add
argument_list|(
literal|"TestUser"
argument_list|)
expr_stmt|;
name|nameHints
operator|.
name|add
argument_list|(
literal|"Test"
argument_list|)
expr_stmt|;
name|nameHints
operator|.
name|add
argument_list|(
literal|"User"
argument_list|)
expr_stmt|;
name|nameHints
operator|.
name|add
argument_list|(
literal|"stUs"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|principalProvider
argument_list|,
name|nameHints
argument_list|,
name|principalName
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|principalProvider
argument_list|,
name|nameHints
argument_list|,
name|principalName
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|principalProvider
argument_list|,
name|nameHints
argument_list|,
name|principalName
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
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
name|testFindGroupPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|Group
name|testGroup
init|=
literal|null
decl_stmt|;
try|try
block|{
name|UserManager
name|userMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|testGroup
operator|=
name|userMgr
operator|.
name|createGroup
argument_list|(
literal|"TestGroup"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|principalName
init|=
name|testGroup
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|principalName
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|nameHints
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|nameHints
operator|.
name|add
argument_list|(
literal|"TestGroup"
argument_list|)
expr_stmt|;
name|nameHints
operator|.
name|add
argument_list|(
literal|"Test"
argument_list|)
expr_stmt|;
name|nameHints
operator|.
name|add
argument_list|(
literal|"Group"
argument_list|)
expr_stmt|;
name|nameHints
operator|.
name|add
argument_list|(
literal|"stGr"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|principalProvider
argument_list|,
name|nameHints
argument_list|,
name|principalName
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|principalProvider
argument_list|,
name|nameHints
argument_list|,
name|principalName
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|principalProvider
argument_list|,
name|nameHints
argument_list|,
name|principalName
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|testGroup
operator|!=
literal|null
condition|)
block|{
name|testGroup
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
name|testFindEveryone
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Boolean
argument_list|>
name|tests
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
name|tests
operator|.
name|put
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tests
operator|.
name|put
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tests
operator|.
name|put
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
for|for
control|(
name|Integer
name|searchType
range|:
name|tests
operator|.
name|keySet
argument_list|()
control|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|it
init|=
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|searchType
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Principal
name|p
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|Boolean
name|expected
init|=
name|tests
operator|.
name|get
argument_list|(
name|searchType
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|found
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindEveryoneHint
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|nameHints
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|nameHints
operator|.
name|add
argument_list|(
literal|"everyone"
argument_list|)
expr_stmt|;
name|nameHints
operator|.
name|add
argument_list|(
literal|"every"
argument_list|)
expr_stmt|;
name|nameHints
operator|.
name|add
argument_list|(
literal|"one"
argument_list|)
expr_stmt|;
name|nameHints
operator|.
name|add
argument_list|(
literal|"very"
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|principalProvider
argument_list|,
name|nameHints
argument_list|,
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|principalProvider
argument_list|,
name|nameHints
argument_list|,
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertResult
argument_list|(
name|principalProvider
argument_list|,
name|nameHints
argument_list|,
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindWithoutHint
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|testUser
init|=
literal|null
decl_stmt|;
name|Group
name|testGroup
init|=
literal|null
decl_stmt|;
try|try
block|{
name|UserManager
name|userMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|testUser
operator|=
name|userMgr
operator|.
name|createUser
argument_list|(
literal|"TestUser"
argument_list|,
literal|"pw"
argument_list|)
expr_stmt|;
name|testGroup
operator|=
name|userMgr
operator|.
name|createGroup
argument_list|(
literal|"TestGroup"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|resultNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|principals
init|=
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
decl_stmt|;
while|while
condition|(
name|principals
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|resultNames
operator|.
name|add
argument_list|(
name|principals
operator|.
name|next
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|resultNames
operator|.
name|contains
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|resultNames
operator|.
name|contains
argument_list|(
literal|"TestUser"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|resultNames
operator|.
name|contains
argument_list|(
literal|"TestGroup"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
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
if|if
condition|(
name|testGroup
operator|!=
literal|null
condition|)
block|{
name|testGroup
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|assertResult
parameter_list|(
name|PrincipalProvider
name|principalProvider
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|nameHints
parameter_list|,
name|String
name|expectedName
parameter_list|,
name|int
name|searchType
parameter_list|,
name|boolean
name|toBeFound
parameter_list|)
block|{
for|for
control|(
name|String
name|nameHint
range|:
name|nameHints
control|)
block|{
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|result
init|=
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|nameHint
argument_list|,
name|searchType
argument_list|)
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|result
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Principal
name|p
init|=
name|result
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|expectedName
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|toBeFound
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Expected principal to be found by name hint "
operator|+
name|expectedName
argument_list|,
name|found
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
literal|"Expected principal NOT to be found by name hint "
operator|+
name|expectedName
argument_list|,
name|found
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

