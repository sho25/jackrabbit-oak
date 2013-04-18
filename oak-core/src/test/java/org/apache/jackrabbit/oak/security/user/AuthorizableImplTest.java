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

begin_comment
comment|/**  * Implementation specific tests for {@code AuthorizableImpl} and subclasses.  */
end_comment

begin_class
specifier|public
class|class
name|AuthorizableImplTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|UserManager
name|userMgr
decl_stmt|;
specifier|private
name|User
name|testUser
decl_stmt|;
specifier|private
name|Group
name|testGroup
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
name|userMgr
operator|=
name|getUserManager
argument_list|()
expr_stmt|;
name|testUser
operator|=
name|getTestUser
argument_list|()
expr_stmt|;
name|testGroup
operator|=
name|userMgr
operator|.
name|createGroup
argument_list|(
literal|"testGroup"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
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
finally|finally
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @since OAK 1.0      */
annotation|@
name|Test
specifier|public
name|void
name|testEqualAuthorizables
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|user
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|testUser
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|Authorizable
name|group
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|testGroup
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Authorizable
argument_list|,
name|Authorizable
argument_list|>
name|equalAuthorizables
init|=
operator|new
name|HashMap
argument_list|<
name|Authorizable
argument_list|,
name|Authorizable
argument_list|>
argument_list|()
decl_stmt|;
name|equalAuthorizables
operator|.
name|put
argument_list|(
name|testUser
argument_list|,
name|testUser
argument_list|)
expr_stmt|;
name|equalAuthorizables
operator|.
name|put
argument_list|(
name|testGroup
argument_list|,
name|testGroup
argument_list|)
expr_stmt|;
name|equalAuthorizables
operator|.
name|put
argument_list|(
name|user
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|equalAuthorizables
operator|.
name|put
argument_list|(
name|group
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|equalAuthorizables
operator|.
name|put
argument_list|(
name|testUser
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|equalAuthorizables
operator|.
name|put
argument_list|(
name|testGroup
argument_list|,
name|group
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
name|equalAuthorizables
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @since OAK 1.0      */
annotation|@
name|Test
specifier|public
name|void
name|testNotEqualAuthorizables
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManager
name|otherUserManager
init|=
name|getSecurityProvider
argument_list|()
operator|.
name|getUserConfiguration
argument_list|()
operator|.
name|getUserManager
argument_list|(
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
decl_stmt|;
name|Authorizable
name|user
init|=
name|otherUserManager
operator|.
name|getAuthorizable
argument_list|(
name|testUser
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|Authorizable
name|group
init|=
name|otherUserManager
operator|.
name|getAuthorizable
argument_list|(
name|testGroup
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Authorizable
argument_list|,
name|Authorizable
argument_list|>
name|notEqual
init|=
operator|new
name|HashMap
argument_list|<
name|Authorizable
argument_list|,
name|Authorizable
argument_list|>
argument_list|()
decl_stmt|;
name|notEqual
operator|.
name|put
argument_list|(
name|testUser
argument_list|,
name|testGroup
argument_list|)
expr_stmt|;
name|notEqual
operator|.
name|put
argument_list|(
name|user
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|notEqual
operator|.
name|put
argument_list|(
name|testUser
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|notEqual
operator|.
name|put
argument_list|(
name|testGroup
argument_list|,
name|group
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
name|notEqual
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertFalse
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @since OAK 1.0      */
annotation|@
name|Test
specifier|public
name|void
name|testHashCode
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|user
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|testUser
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|Authorizable
name|group
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|testGroup
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Authorizable
argument_list|,
name|Authorizable
argument_list|>
name|sameHashCode
init|=
operator|new
name|HashMap
argument_list|<
name|Authorizable
argument_list|,
name|Authorizable
argument_list|>
argument_list|()
decl_stmt|;
name|sameHashCode
operator|.
name|put
argument_list|(
name|testUser
argument_list|,
name|testUser
argument_list|)
expr_stmt|;
name|sameHashCode
operator|.
name|put
argument_list|(
name|testGroup
argument_list|,
name|testGroup
argument_list|)
expr_stmt|;
name|sameHashCode
operator|.
name|put
argument_list|(
name|user
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|sameHashCode
operator|.
name|put
argument_list|(
name|group
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|sameHashCode
operator|.
name|put
argument_list|(
name|testUser
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|sameHashCode
operator|.
name|put
argument_list|(
name|testGroup
argument_list|,
name|group
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
name|sameHashCode
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|hashCode
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|UserManager
name|otherUserManager
init|=
name|getSecurityProvider
argument_list|()
operator|.
name|getUserConfiguration
argument_list|()
operator|.
name|getUserManager
argument_list|(
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
decl_stmt|;
name|user
operator|=
name|otherUserManager
operator|.
name|getAuthorizable
argument_list|(
name|testUser
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|group
operator|=
name|otherUserManager
operator|.
name|getAuthorizable
argument_list|(
name|testGroup
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Authorizable
argument_list|,
name|Authorizable
argument_list|>
name|notSameHashCode
init|=
operator|new
name|HashMap
argument_list|<
name|Authorizable
argument_list|,
name|Authorizable
argument_list|>
argument_list|()
decl_stmt|;
name|notSameHashCode
operator|.
name|put
argument_list|(
name|testUser
argument_list|,
name|testGroup
argument_list|)
expr_stmt|;
name|notSameHashCode
operator|.
name|put
argument_list|(
name|user
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|notSameHashCode
operator|.
name|put
argument_list|(
name|testUser
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|notSameHashCode
operator|.
name|put
argument_list|(
name|testGroup
argument_list|,
name|group
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
name|notSameHashCode
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertFalse
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|==
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

