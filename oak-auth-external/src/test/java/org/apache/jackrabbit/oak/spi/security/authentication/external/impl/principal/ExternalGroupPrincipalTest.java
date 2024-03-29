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
operator|.
name|impl
operator|.
name|principal
package|;
end_package

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
name|ImmutableMap
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
name|api
operator|.
name|QueryEngine
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
name|external
operator|.
name|ExternalIdentityRef
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
name|external
operator|.
name|ExternalUser
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
name|text
operator|.
name|ParseException
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
name|Map
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
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
name|spy
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
name|ExternalGroupPrincipalTest
extends|extends
name|AbstractPrincipalTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testIsMember
parameter_list|()
throws|throws
name|Exception
block|{
name|ExternalUser
name|externalUser
init|=
name|idp
operator|.
name|getUser
argument_list|(
name|USER_ID
argument_list|)
decl_stmt|;
name|GroupPrincipal
name|principal
init|=
name|getGroupPrincipal
argument_list|(
name|externalUser
operator|.
name|getDeclaredGroups
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|principal
operator|.
name|isMember
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|externalUser
operator|.
name|getPrincipalName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|principal
operator|.
name|isMember
argument_list|(
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotIsMember
parameter_list|()
throws|throws
name|Exception
block|{
name|GroupPrincipal
name|principal
init|=
name|getGroupPrincipal
argument_list|(
name|idp
operator|.
name|getUser
argument_list|(
name|USER_ID
argument_list|)
operator|.
name|getDeclaredGroups
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|Authorizable
name|notMember
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|ID_SECOND_USER
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|principal
operator|.
name|isMember
argument_list|(
name|notMember
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|getTree
argument_list|(
name|notMember
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|setProperty
argument_list|(
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"secondGroup"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principal
operator|.
name|isMember
argument_list|(
name|notMember
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|getTree
argument_list|(
name|notMember
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|setProperty
argument_list|(
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principal
operator|.
name|isMember
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|notMember
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
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
name|testIsMemberExternalGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|GroupPrincipal
name|principal
init|=
name|getGroupPrincipal
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|exGroupPrincNames
init|=
name|Iterables
operator|.
name|transform
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|idp
operator|.
name|listGroups
argument_list|()
argument_list|)
argument_list|,
name|input
lambda|->
name|input
operator|.
name|getPrincipalName
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|principalName
range|:
name|exGroupPrincNames
control|)
block|{
name|assertFalse
argument_list|(
name|principal
operator|.
name|isMember
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|principalName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsMemberLocalUser
parameter_list|()
throws|throws
name|Exception
block|{
name|GroupPrincipal
name|principal
init|=
name|getGroupPrincipal
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|principal
operator|.
name|isMember
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principal
operator|.
name|isMember
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
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
name|testIsMemberLocalGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|Group
name|gr
init|=
name|createTestGroup
argument_list|()
decl_stmt|;
name|GroupPrincipal
name|principal
init|=
name|getGroupPrincipal
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|gr
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|principal
operator|.
name|isMember
argument_list|(
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principal
operator|.
name|isMember
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ItemBasedPrincipal
name|ibp
init|=
operator|new
name|ItemBasedPrincipal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
annotation|@
name|NotNull
name|String
name|getPath
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|gr
operator|.
name|getPath
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
decl_stmt|;
name|assertFalse
argument_list|(
name|principal
operator|.
name|isMember
argument_list|(
name|ibp
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMembers
parameter_list|()
throws|throws
name|Exception
block|{
name|GroupPrincipal
name|principal
init|=
name|getGroupPrincipal
argument_list|()
decl_stmt|;
name|Principal
index|[]
name|expectedMembers
init|=
operator|new
name|Principal
index|[]
block|{
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
operator|.
name|getPrincipal
argument_list|()
block|,
operator|new
name|PrincipalImpl
argument_list|(
name|idp
operator|.
name|getUser
argument_list|(
name|USER_ID
argument_list|)
operator|.
name|getPrincipalName
argument_list|()
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|Principal
name|expected
range|:
name|expectedMembers
control|)
block|{
name|Enumeration
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|members
init|=
name|principal
operator|.
name|members
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|members
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|members
operator|.
name|nextElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|members
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMembersUserLookupFails
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManager
name|um
init|=
name|spy
argument_list|(
name|getUserManager
argument_list|(
name|root
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|userPath
init|=
name|um
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|um
operator|.
name|getAuthorizableByPath
argument_list|(
name|userPath
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
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
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
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
name|ExternalGroupPrincipalProvider
name|pp
init|=
operator|new
name|ExternalGroupPrincipalProvider
argument_list|(
name|root
argument_list|,
name|uc
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|idp
operator|.
name|getName
argument_list|()
argument_list|,
name|getAutoMembership
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ExternalIdentityRef
name|ref
init|=
name|idp
operator|.
name|getUser
argument_list|(
name|USER_ID
argument_list|)
operator|.
name|getDeclaredGroups
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|groupName
init|=
name|idp
operator|.
name|getIdentity
argument_list|(
name|ref
argument_list|)
operator|.
name|getPrincipalName
argument_list|()
decl_stmt|;
name|Principal
name|gp
init|=
name|pp
operator|.
name|getPrincipal
argument_list|(
name|groupName
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|gp
operator|instanceof
name|GroupPrincipal
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|(
operator|(
name|GroupPrincipal
operator|)
name|gp
operator|)
operator|.
name|members
argument_list|()
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMembersQueryFails
parameter_list|()
throws|throws
name|Exception
block|{
name|QueryEngine
name|qe
init|=
name|mock
argument_list|(
name|QueryEngine
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|qe
operator|.
name|executeQuery
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|Map
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|Map
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
operator|new
name|ParseException
argument_list|(
literal|"fail"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Root
name|r
init|=
name|spy
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|r
operator|.
name|getQueryEngine
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|qe
argument_list|)
expr_stmt|;
name|ExternalGroupPrincipalProvider
name|pp
init|=
operator|new
name|ExternalGroupPrincipalProvider
argument_list|(
name|r
argument_list|,
name|getUserConfiguration
argument_list|()
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|idp
operator|.
name|getName
argument_list|()
argument_list|,
name|getAutoMembership
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Principal
name|gp
init|=
name|pp
operator|.
name|getMembershipPrincipals
argument_list|(
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
operator|.
name|getPrincipal
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|gp
operator|instanceof
name|GroupPrincipal
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|(
operator|(
name|GroupPrincipal
operator|)
name|gp
operator|)
operator|.
name|members
argument_list|()
operator|.
name|hasMoreElements
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

