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
operator|.
name|action
package|;
end_package

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
name|Set
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
name|RepositoryException
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
name|ImmutableList
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
name|Iterables
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
name|SecurityProvider
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
name|user
operator|.
name|UserConfiguration
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
name|user
operator|.
name|UserConstants
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
name|user
operator|.
name|action
operator|.
name|AbstractGroupAction
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
name|user
operator|.
name|action
operator|.
name|AuthorizableAction
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
name|user
operator|.
name|action
operator|.
name|AuthorizableActionProvider
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
name|xml
operator|.
name|ImportBehavior
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
name|xml
operator|.
name|ProtectedItemImporter
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

begin_class
specifier|public
class|class
name|GroupActionTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TEST_GROUP_ID
init|=
literal|"testGroup"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_USER_PREFIX
init|=
literal|"testUser"
decl_stmt|;
specifier|final
name|TestGroupAction
name|groupAction
init|=
operator|new
name|TestGroupAction
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AuthorizableActionProvider
name|actionProvider
init|=
operator|new
name|AuthorizableActionProvider
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|AuthorizableAction
argument_list|>
name|getAuthorizableActions
parameter_list|(
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|groupAction
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|private
name|User
name|testUser01
decl_stmt|;
specifier|private
name|User
name|testUser02
decl_stmt|;
name|Group
name|testGroup
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
name|testGroup
operator|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createGroup
argument_list|(
name|TEST_GROUP_ID
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
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
if|if
condition|(
name|testUser01
operator|!=
literal|null
condition|)
block|{
name|testUser01
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
if|if
condition|(
name|testUser02
operator|!=
literal|null
condition|)
block|{
name|testUser02
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
name|root
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
name|ConfigurationParameters
name|userParams
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConstants
operator|.
name|PARAM_AUTHORIZABLE_ACTION_PROVIDER
argument_list|,
name|actionProvider
argument_list|,
name|ProtectedItemImporter
operator|.
name|PARAM_IMPORT_BEHAVIOR
argument_list|,
name|getImportBehavior
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|,
name|userParams
argument_list|)
return|;
block|}
name|String
name|getImportBehavior
parameter_list|()
block|{
return|return
name|ImportBehavior
operator|.
name|NAME_IGNORE
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMemberAdded
parameter_list|()
throws|throws
name|Exception
block|{
name|testUser01
operator|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createUser
argument_list|(
name|TEST_USER_PREFIX
operator|+
literal|"01"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|testGroup
operator|.
name|addMember
argument_list|(
name|testUser01
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|groupAction
operator|.
name|onMemberAddedCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testGroup
argument_list|,
name|groupAction
operator|.
name|group
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testUser01
argument_list|,
name|groupAction
operator|.
name|member
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMemberRemoved
parameter_list|()
throws|throws
name|Exception
block|{
name|testUser01
operator|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createUser
argument_list|(
name|TEST_USER_PREFIX
operator|+
literal|"01"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|testGroup
operator|.
name|addMember
argument_list|(
name|testUser01
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|testGroup
operator|.
name|removeMember
argument_list|(
name|testUser01
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|groupAction
operator|.
name|onMemberRemovedCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testGroup
argument_list|,
name|groupAction
operator|.
name|group
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testUser01
argument_list|,
name|groupAction
operator|.
name|member
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMembersAdded
parameter_list|()
throws|throws
name|Exception
block|{
name|testUser01
operator|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createUser
argument_list|(
name|TEST_USER_PREFIX
operator|+
literal|"01"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|testUser02
operator|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createUser
argument_list|(
name|TEST_USER_PREFIX
operator|+
literal|"02"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|testGroup
operator|.
name|addMember
argument_list|(
name|testUser02
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|memberIds
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|testUser01
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|failedIds
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|testUser02
operator|.
name|getID
argument_list|()
argument_list|,
name|testGroup
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|Iterables
operator|.
name|concat
argument_list|(
name|memberIds
argument_list|,
name|failedIds
argument_list|)
decl_stmt|;
name|testGroup
operator|.
name|addMembers
argument_list|(
name|Iterables
operator|.
name|toArray
argument_list|(
name|ids
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|groupAction
operator|.
name|onMembersAddedCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testGroup
argument_list|,
name|groupAction
operator|.
name|group
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|memberIds
argument_list|,
name|groupAction
operator|.
name|memberIds
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|failedIds
argument_list|,
name|groupAction
operator|.
name|failedIds
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMembersAddedNonExisting
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|nonExisting
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"blinder"
argument_list|,
literal|"passagier"
argument_list|)
decl_stmt|;
name|testGroup
operator|.
name|addMembers
argument_list|(
name|nonExisting
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|nonExisting
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|groupAction
operator|.
name|memberIds
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nonExisting
argument_list|,
name|groupAction
operator|.
name|failedIds
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMembersRemoved
parameter_list|()
throws|throws
name|Exception
block|{
name|testUser01
operator|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createUser
argument_list|(
name|TEST_USER_PREFIX
operator|+
literal|"01"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|testUser02
operator|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createUser
argument_list|(
name|TEST_USER_PREFIX
operator|+
literal|"02"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|testGroup
operator|.
name|addMember
argument_list|(
name|testUser01
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|memberIds
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|testUser01
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|failedIds
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|testUser02
operator|.
name|getID
argument_list|()
argument_list|,
name|testGroup
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|Iterables
operator|.
name|concat
argument_list|(
name|memberIds
argument_list|,
name|failedIds
argument_list|)
decl_stmt|;
name|testGroup
operator|.
name|removeMembers
argument_list|(
name|Iterables
operator|.
name|toArray
argument_list|(
name|ids
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|groupAction
operator|.
name|onMembersRemovedCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testGroup
argument_list|,
name|groupAction
operator|.
name|group
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|memberIds
argument_list|,
name|groupAction
operator|.
name|memberIds
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|failedIds
argument_list|,
name|groupAction
operator|.
name|failedIds
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMembersRemovedNonExisting
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|nonExisting
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"blinder"
argument_list|,
literal|"passagier"
argument_list|)
decl_stmt|;
name|testGroup
operator|.
name|removeMembers
argument_list|(
name|nonExisting
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|nonExisting
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|groupAction
operator|.
name|memberIds
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nonExisting
argument_list|,
name|groupAction
operator|.
name|failedIds
argument_list|)
expr_stmt|;
block|}
class|class
name|TestGroupAction
extends|extends
name|AbstractGroupAction
block|{
name|boolean
name|onMemberAddedCalled
init|=
literal|false
decl_stmt|;
name|boolean
name|onMembersAddedCalled
init|=
literal|false
decl_stmt|;
name|boolean
name|onMemberRemovedCalled
init|=
literal|false
decl_stmt|;
name|boolean
name|onMembersRemovedCalled
init|=
literal|false
decl_stmt|;
name|Group
name|group
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|memberIds
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|failedIds
decl_stmt|;
name|Authorizable
name|member
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|onMemberAdded
parameter_list|(
annotation|@
name|Nonnull
name|Group
name|group
parameter_list|,
annotation|@
name|Nonnull
name|Authorizable
name|member
parameter_list|,
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
name|this
operator|.
name|member
operator|=
name|member
expr_stmt|;
name|onMemberAddedCalled
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMembersAdded
parameter_list|(
annotation|@
name|Nonnull
name|Group
name|group
parameter_list|,
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|String
argument_list|>
name|memberIds
parameter_list|,
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|String
argument_list|>
name|failedIds
parameter_list|,
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
name|this
operator|.
name|memberIds
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|memberIds
argument_list|)
expr_stmt|;
name|this
operator|.
name|failedIds
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|failedIds
argument_list|)
expr_stmt|;
name|onMembersAddedCalled
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMemberRemoved
parameter_list|(
annotation|@
name|Nonnull
name|Group
name|group
parameter_list|,
annotation|@
name|Nonnull
name|Authorizable
name|member
parameter_list|,
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
name|this
operator|.
name|member
operator|=
name|member
expr_stmt|;
name|onMemberRemovedCalled
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMembersRemoved
parameter_list|(
annotation|@
name|Nonnull
name|Group
name|group
parameter_list|,
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|String
argument_list|>
name|memberIds
parameter_list|,
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|String
argument_list|>
name|failedIds
parameter_list|,
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
name|this
operator|.
name|memberIds
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|memberIds
argument_list|)
expr_stmt|;
name|this
operator|.
name|failedIds
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|failedIds
argument_list|)
expr_stmt|;
name|onMembersRemovedCalled
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

