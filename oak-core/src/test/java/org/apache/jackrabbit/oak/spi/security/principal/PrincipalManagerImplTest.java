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
name|security
operator|.
name|acl
operator|.
name|Group
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|PrincipalIterator
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
name|assertSame
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
name|PrincipalManagerImplTest
block|{
specifier|private
specifier|final
name|TestPrincipalProvider
name|provider
init|=
operator|new
name|TestPrincipalProvider
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|PrincipalManagerImpl
name|principalMgr
init|=
operator|new
name|PrincipalManagerImpl
argument_list|(
name|provider
argument_list|)
decl_stmt|;
specifier|private
name|Iterable
argument_list|<
name|Principal
argument_list|>
name|testPrincipals
init|=
name|provider
operator|.
name|getTestPrincipals
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|boolean
name|isGroup
parameter_list|(
name|Principal
name|p
parameter_list|)
block|{
return|return
name|p
operator|instanceof
name|Group
return|;
block|}
specifier|private
specifier|static
name|void
name|assertIterator
parameter_list|(
annotation|@
name|Nonnull
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|expected
parameter_list|,
annotation|@
name|Nonnull
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|result
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|expected
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetEveryone
parameter_list|()
block|{
name|Principal
name|principal
init|=
name|principalMgr
operator|.
name|getEveryone
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|isGroup
argument_list|(
name|principal
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetEveryone2
parameter_list|()
block|{
name|Principal
name|principal
init|=
operator|new
name|PrincipalManagerImpl
argument_list|(
operator|new
name|TestPrincipalProvider
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|getEveryone
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|principal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalEveryone
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|principalMgr
operator|.
name|getPrincipal
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasPrincipalEveryone
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|principalMgr
operator|.
name|hasPrincipal
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasPrincipal
parameter_list|()
block|{
for|for
control|(
name|Principal
name|pcpl
range|:
name|testPrincipals
control|)
block|{
name|assertTrue
argument_list|(
name|principalMgr
operator|.
name|hasPrincipal
argument_list|(
name|pcpl
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
name|testHasPrincipalUnknown
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|principalMgr
operator|.
name|hasPrincipal
argument_list|(
name|TestPrincipalProvider
operator|.
name|UNKNOWN
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalUnknown
parameter_list|()
block|{
name|assertNull
argument_list|(
name|principalMgr
operator|.
name|getPrincipal
argument_list|(
name|TestPrincipalProvider
operator|.
name|UNKNOWN
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipal
parameter_list|()
block|{
for|for
control|(
name|Principal
name|principal
range|:
name|testPrincipals
control|)
block|{
name|Principal
name|pp
init|=
name|principalMgr
operator|.
name|getPrincipal
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|pp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"PrincipalManager.getPrincipal returned Principal with different Name"
argument_list|,
name|principal
operator|.
name|getName
argument_list|()
argument_list|,
name|pp
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"PrincipalManager.getPrincipal returned different Principal"
argument_list|,
name|principal
argument_list|,
name|pp
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalsNonGroup
parameter_list|()
block|{
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|expected
init|=
name|provider
operator|.
name|findPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|)
decl_stmt|;
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|)
decl_stmt|;
name|assertIterator
argument_list|(
name|expected
argument_list|,
name|it
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalsNonGroupContainsNoGroups
parameter_list|()
block|{
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
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
name|nextPrincipal
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|isGroup
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalsGroup
parameter_list|()
block|{
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|expected
init|=
name|provider
operator|.
name|findPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
decl_stmt|;
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
decl_stmt|;
name|assertIterator
argument_list|(
name|expected
argument_list|,
name|it
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalsGroupContainsGroups
parameter_list|()
block|{
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
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
name|nextPrincipal
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|isGroup
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalsAll
parameter_list|()
block|{
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|expected
init|=
name|provider
operator|.
name|findPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
decl_stmt|;
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
decl_stmt|;
name|assertIterator
argument_list|(
name|expected
argument_list|,
name|it
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllMembersKnown
parameter_list|()
block|{
for|for
control|(
name|Principal
name|p
range|:
name|testPrincipals
control|)
block|{
if|if
condition|(
name|isGroup
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|Enumeration
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|en
init|=
operator|(
operator|(
name|Group
operator|)
name|p
operator|)
operator|.
name|members
argument_list|()
decl_stmt|;
while|while
condition|(
name|en
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Principal
name|memb
init|=
name|en
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|principalMgr
operator|.
name|hasPrincipal
argument_list|(
name|memb
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupMembershipNonGroup
parameter_list|()
block|{
name|assertMembership
argument_list|(
name|principalMgr
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupMembershipGroup
parameter_list|()
block|{
name|assertMembership
argument_list|(
name|principalMgr
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupMembershipAll
parameter_list|()
block|{
name|assertMembership
argument_list|(
name|principalMgr
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertMembership
parameter_list|(
annotation|@
name|Nonnull
name|PrincipalManager
name|mgr
parameter_list|,
name|int
name|searchType
parameter_list|)
block|{
name|PrincipalIterator
name|it
init|=
name|mgr
operator|.
name|getPrincipals
argument_list|(
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
name|nextPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|equals
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|boolean
name|atleastEveryone
init|=
literal|false
decl_stmt|;
for|for
control|(
name|PrincipalIterator
name|membership
init|=
name|mgr
operator|.
name|getGroupMembership
argument_list|(
name|p
argument_list|)
init|;
name|membership
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Principal
name|gr
init|=
name|membership
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|isGroup
argument_list|(
name|gr
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|gr
operator|.
name|equals
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
condition|)
block|{
name|atleastEveryone
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"All principals (except everyone) must be member of the everyone group."
argument_list|,
name|atleastEveryone
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetGroupMembershipEveryoneEmpty
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|principalMgr
operator|.
name|getGroupMembership
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
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
name|testGetGroupMembershipEveryoneWithoutEveryone
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|Iterators
operator|.
name|contains
argument_list|(
name|principalMgr
operator|.
name|getGroupMembership
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetMembersConsistentWithMembership
parameter_list|()
block|{
name|Principal
name|everyone
init|=
name|principalMgr
operator|.
name|getEveryone
argument_list|()
decl_stmt|;
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
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
name|nextPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|equals
argument_list|(
name|everyone
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|assertTrue
argument_list|(
name|isGroup
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|Enumeration
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|members
init|=
operator|(
operator|(
name|Group
operator|)
name|p
operator|)
operator|.
name|members
argument_list|()
decl_stmt|;
while|while
condition|(
name|members
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Principal
name|memb
init|=
name|members
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|Principal
name|group
init|=
literal|null
decl_stmt|;
name|PrincipalIterator
name|mship
init|=
name|principalMgr
operator|.
name|getGroupMembership
argument_list|(
name|memb
argument_list|)
decl_stmt|;
while|while
condition|(
name|mship
operator|.
name|hasNext
argument_list|()
operator|&&
name|group
operator|==
literal|null
condition|)
block|{
name|Principal
name|gr
init|=
name|mship
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|equals
argument_list|(
name|gr
argument_list|)
condition|)
block|{
name|group
operator|=
name|gr
expr_stmt|;
block|}
block|}
name|assertNotNull
argument_list|(
literal|"Group member "
operator|+
name|memb
operator|.
name|getName
argument_list|()
operator|+
literal|"does not reveal group upon getGroupMembership"
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipal
parameter_list|()
block|{
for|for
control|(
name|Principal
name|pcpl
range|:
name|testPrincipals
control|)
block|{
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"findPrincipals does not find principal with filter '"
operator|+
name|pcpl
operator|.
name|getName
argument_list|()
operator|+
literal|'\''
argument_list|,
name|Iterators
operator|.
name|contains
argument_list|(
name|it
argument_list|,
name|pcpl
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipalByTypeGroup
parameter_list|()
block|{
for|for
control|(
name|Principal
name|pcpl
range|:
name|testPrincipals
control|)
block|{
if|if
condition|(
name|isGroup
argument_list|(
name|pcpl
argument_list|)
condition|)
block|{
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"findPrincipals does not find principal with filter '"
operator|+
name|pcpl
operator|.
name|getName
argument_list|()
operator|+
literal|'\''
argument_list|,
name|Iterators
operator|.
name|contains
argument_list|(
name|it
argument_list|,
name|pcpl
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"findPrincipals does not find principal with filter '"
operator|+
name|pcpl
operator|.
name|getName
argument_list|()
operator|+
literal|'\''
argument_list|,
name|Iterators
operator|.
name|contains
argument_list|(
name|it
argument_list|,
name|pcpl
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipalByType
parameter_list|()
block|{
for|for
control|(
name|Principal
name|pcpl
range|:
name|testPrincipals
control|)
block|{
if|if
condition|(
name|isGroup
argument_list|(
name|pcpl
argument_list|)
condition|)
block|{
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"findPrincipals does not find principal with filter '"
operator|+
name|pcpl
operator|.
name|getName
argument_list|()
operator|+
literal|'\''
argument_list|,
name|Iterators
operator|.
name|contains
argument_list|(
name|it
argument_list|,
name|pcpl
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"findPrincipals does not find principal with filter '"
operator|+
name|pcpl
operator|.
name|getName
argument_list|()
operator|+
literal|'\''
argument_list|,
name|Iterators
operator|.
name|contains
argument_list|(
name|it
argument_list|,
name|pcpl
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipalByTypeAll
parameter_list|()
block|{
for|for
control|(
name|Principal
name|pcpl
range|:
name|testPrincipals
control|)
block|{
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"findPrincipals does not find principal with filter '"
operator|+
name|pcpl
operator|.
name|getName
argument_list|()
operator|+
literal|'\''
argument_list|,
name|Iterators
operator|.
name|contains
argument_list|(
name|it
argument_list|,
name|pcpl
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindEveryone
parameter_list|()
block|{
comment|// untyped search -> everyone must be part of the result set
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"findPrincipals does not find principal with filter '"
operator|+
name|EveryonePrincipal
operator|.
name|NAME
operator|+
literal|'\''
argument_list|,
name|Iterators
operator|.
name|contains
argument_list|(
name|it
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindEveryoneTypeGroup
parameter_list|()
block|{
comment|// search group only -> everyone must be part of the result set
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"findPrincipals does not find principal with filter '"
operator|+
name|EveryonePrincipal
operator|.
name|NAME
operator|+
literal|'\''
argument_list|,
name|Iterators
operator|.
name|contains
argument_list|(
name|it
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindEveryoneTypeNonGroup
parameter_list|()
block|{
comment|// search non-group only -> everyone should not be part of the result set
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"findPrincipals did find principal with filter '"
operator|+
name|EveryonePrincipal
operator|.
name|NAME
operator|+
literal|'\''
argument_list|,
name|Iterators
operator|.
name|contains
argument_list|(
name|it
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindUnknownByTypeAll
parameter_list|()
block|{
name|String
name|unknownHint
init|=
name|TestPrincipalProvider
operator|.
name|UNKNOWN
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|unknownHint
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
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
name|testFindUnknownByTypeGroup
parameter_list|()
block|{
name|String
name|unknownHint
init|=
name|TestPrincipalProvider
operator|.
name|UNKNOWN
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|unknownHint
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
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
name|testFindUnknownByTypeNotGroup
parameter_list|()
block|{
name|String
name|unknownHint
init|=
name|TestPrincipalProvider
operator|.
name|UNKNOWN
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|unknownHint
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|)
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

