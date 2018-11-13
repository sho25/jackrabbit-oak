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
name|Set
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
name|ImmutableSet
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
name|Sets
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
name|ItemBasedPrincipal
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Testing special behavior of the everyone group.  *  * @since OAK 1.0  */
end_comment

begin_class
specifier|public
class|class
name|EveryoneGroupTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|Group
name|everyoneGroup
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|Authorizable
argument_list|>
name|authorizables
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
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|authorizables
operator|=
operator|new
name|HashSet
argument_list|<
name|Authorizable
argument_list|>
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|authorizables
operator|.
name|add
argument_list|(
name|userMgr
operator|.
name|createGroup
argument_list|(
literal|"testGroup"
argument_list|)
argument_list|)
expr_stmt|;
name|authorizables
operator|.
name|add
argument_list|(
name|userMgr
operator|.
name|createUser
argument_list|(
literal|"testUser"
argument_list|,
literal|"pw"
argument_list|)
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
block|}
for|for
control|(
name|Authorizable
name|a
range|:
name|authorizables
control|)
block|{
name|a
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
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|everyoneGroup
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|everyoneGroup
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|everyoneGroup
operator|.
name|getPrincipal
argument_list|()
operator|instanceof
name|ItemBasedPrincipal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEveryoneIsMember
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|everyoneGroup
operator|.
name|isMember
argument_list|(
name|everyoneGroup
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEveryoneIsDeclaredMember
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|everyoneGroup
operator|.
name|isDeclaredMember
argument_list|(
name|everyoneGroup
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsMember
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Authorizable
name|a
range|:
name|authorizables
control|)
block|{
name|assertTrue
argument_list|(
name|everyoneGroup
operator|.
name|isMember
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsDeclaredMember
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Authorizable
name|a
range|:
name|authorizables
control|)
block|{
name|assertTrue
argument_list|(
name|everyoneGroup
operator|.
name|isDeclaredMember
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetMembers
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|Authorizable
argument_list|>
name|members
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|everyoneGroup
operator|.
name|getMembers
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|members
operator|.
name|contains
argument_list|(
name|everyoneGroup
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Authorizable
name|a
range|:
name|authorizables
control|)
block|{
name|assertTrue
argument_list|(
name|members
operator|.
name|contains
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetDeclaredMembers
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|Authorizable
argument_list|>
name|members
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|everyoneGroup
operator|.
name|getDeclaredMembers
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|members
operator|.
name|contains
argument_list|(
name|everyoneGroup
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Authorizable
name|a
range|:
name|authorizables
control|)
block|{
name|assertTrue
argument_list|(
name|members
operator|.
name|contains
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddEveryoneAsMember
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|everyoneGroup
operator|.
name|addMember
argument_list|(
name|everyoneGroup
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddMember
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Authorizable
name|a
range|:
name|authorizables
control|)
block|{
name|assertFalse
argument_list|(
name|everyoneGroup
operator|.
name|addMember
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddMembers
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|,
name|everyoneGroup
operator|.
name|addMembers
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveEveryoneFromMembers
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|everyoneGroup
operator|.
name|removeMember
argument_list|(
name|everyoneGroup
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveMember
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Authorizable
name|a
range|:
name|authorizables
control|)
block|{
name|assertFalse
argument_list|(
name|everyoneGroup
operator|.
name|removeMember
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveEveryoneMembership
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
literal|"testGroup"
argument_list|,
name|Group
operator|.
name|class
argument_list|)
operator|.
name|removeMember
argument_list|(
name|everyoneGroup
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveMembers
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|,
name|everyoneGroup
operator|.
name|removeMembers
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEveryoneMemberOf
parameter_list|()
throws|throws
name|Exception
block|{
name|Iterator
argument_list|<
name|Group
argument_list|>
name|groups
init|=
name|everyoneGroup
operator|.
name|memberOf
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|groups
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEveryoneDeclaredMemberOf
parameter_list|()
throws|throws
name|Exception
block|{
name|Iterator
argument_list|<
name|Group
argument_list|>
name|groups
init|=
name|everyoneGroup
operator|.
name|declaredMemberOf
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|groups
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMemberOfIncludesEveryone
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Authorizable
name|a
range|:
name|authorizables
control|)
block|{
name|Set
argument_list|<
name|Group
argument_list|>
name|groups
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|a
operator|.
name|memberOf
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|contains
argument_list|(
name|everyoneGroup
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeclaredMemberOfIncludesEveryone
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Authorizable
name|a
range|:
name|authorizables
control|)
block|{
name|Set
argument_list|<
name|Group
argument_list|>
name|groups
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|a
operator|.
name|declaredMemberOf
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|contains
argument_list|(
name|everyoneGroup
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

