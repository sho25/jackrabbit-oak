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
name|Collections
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
name|Iterators
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
name|Lists
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
name|GroupPrincipal
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
name|Query
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
name|principal
operator|.
name|PrincipalImpl
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
name|PrincipalProvider
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|namepath
operator|.
name|NamePathMapper
operator|.
name|DEFAULT
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|PrincipalProviderImplTest
extends|extends
name|AbstractPrincipalProviderTest
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|protected
name|PrincipalProvider
name|createPrincipalProvider
parameter_list|()
block|{
return|return
operator|new
name|PrincipalProviderImpl
argument_list|(
name|root
argument_list|,
name|getUserConfiguration
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
specifier|private
name|PrincipalProviderImpl
name|createPrincipalProvider
parameter_list|(
annotation|@
name|NotNull
name|UserManager
name|um
parameter_list|)
block|{
name|UserConfiguration
name|uc
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getUserManager
argument_list|(
name|any
argument_list|(
name|Root
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|NamePathMapper
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|um
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
return|return
operator|new
name|PrincipalProviderImpl
argument_list|(
name|root
argument_list|,
name|uc
argument_list|,
name|DEFAULT
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEveryoneMembers
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
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|everyoneMembers
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|Collections
operator|.
name|list
argument_list|(
operator|(
operator|(
name|GroupPrincipal
operator|)
name|ep
operator|)
operator|.
name|members
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|all
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
name|all
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Principal
name|p
init|=
name|all
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|everyone
operator|.
name|equals
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|everyoneMembers
operator|.
name|contains
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|everyoneMembers
operator|.
name|contains
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|testGetGroupMembershipNonGroupPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Group.getPrincipal doesn't return a GroupPrincipal
name|Group
name|gr
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|Group
operator|.
name|class
argument_list|)
operator|.
name|getPrincipal
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
literal|"group"
argument_list|)
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|Authorizable
name|mockAuthorizable
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|User
operator|.
name|class
argument_list|)
operator|.
name|memberOf
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Iterators
operator|.
name|singletonIterator
argument_list|(
name|gr
argument_list|)
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|UserManager
name|umMock
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|UserManager
operator|.
name|class
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|any
argument_list|(
name|Principal
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockAuthorizable
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|membership
init|=
name|createPrincipalProvider
argument_list|(
name|umMock
argument_list|)
operator|.
name|getMembershipPrincipals
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
literal|"userPrincipal"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|,
name|membership
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetItemBasedPrincipalNotItemBased
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|mockUser
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|User
operator|.
name|class
argument_list|)
operator|.
name|getPrincipal
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
literal|"noPath"
argument_list|)
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|UserManager
name|umMock
init|=
name|mock
argument_list|(
name|UserManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|umMock
operator|.
name|getAuthorizableByPath
argument_list|(
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockUser
argument_list|)
expr_stmt|;
name|createPrincipalProvider
argument_list|(
name|umMock
argument_list|)
operator|.
name|getItemBasedPrincipal
argument_list|(
literal|"/path/to/authorizable"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|umMock
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getAuthorizableByPath
argument_list|(
literal|"/path/to/authorizable"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindWithUnexpectedNullAuthorizable
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Authorizable
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|UserManager
name|umMock
init|=
name|mock
argument_list|(
name|UserManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|umMock
operator|.
name|findAuthorizables
argument_list|(
name|any
argument_list|(
name|Query
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|l
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|result
init|=
name|createPrincipalProvider
argument_list|(
name|umMock
argument_list|)
operator|.
name|findPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|createPrincipalProvider
argument_list|(
name|umMock
argument_list|)
operator|.
name|findPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterators
operator|.
name|elementsEqual
argument_list|(
name|Iterators
operator|.
name|singletonIterator
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|,
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindWithUnexpectedNullPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|userMock
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|Authorizable
operator|.
name|class
argument_list|)
operator|.
name|getPrincipal
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|UserManager
name|umMock
init|=
name|mock
argument_list|(
name|UserManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|umMock
operator|.
name|findAuthorizables
argument_list|(
name|any
argument_list|(
name|Query
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Iterators
operator|.
name|singletonIterator
argument_list|(
name|userMock
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|result
init|=
name|createPrincipalProvider
argument_list|(
name|umMock
argument_list|)
operator|.
name|findPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|createPrincipalProvider
argument_list|(
name|umMock
argument_list|)
operator|.
name|findPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterators
operator|.
name|elementsEqual
argument_list|(
name|Iterators
operator|.
name|singletonIterator
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|,
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

