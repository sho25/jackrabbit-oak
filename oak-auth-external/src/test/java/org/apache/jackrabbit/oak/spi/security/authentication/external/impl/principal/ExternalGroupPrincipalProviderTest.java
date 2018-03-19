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
name|Collections
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
name|annotation
operator|.
name|Nullable
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
name|base
operator|.
name|Function
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|ExternalGroup
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
name|ExternalIdentity
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
name|ExternalIdentityException
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
name|authentication
operator|.
name|external
operator|.
name|TestIdentityProvider
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
name|impl
operator|.
name|DynamicSyncContext
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
name|ExternalGroupPrincipalProviderTest
extends|extends
name|AbstractPrincipalTest
block|{
name|void
name|sync
parameter_list|(
annotation|@
name|Nonnull
name|ExternalUser
name|externalUser
parameter_list|)
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
name|DynamicSyncContext
name|syncContext
init|=
operator|new
name|DynamicSyncContext
argument_list|(
name|syncConfig
argument_list|,
name|idp
argument_list|,
name|getUserManager
argument_list|(
name|systemRoot
argument_list|)
argument_list|,
name|getValueFactory
argument_list|(
name|systemRoot
argument_list|)
argument_list|)
decl_stmt|;
name|syncContext
operator|.
name|sync
argument_list|(
name|externalUser
argument_list|)
expr_stmt|;
name|syncContext
operator|.
name|close
argument_list|()
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
name|Set
argument_list|<
name|Principal
argument_list|>
name|getExpectedGroupPrincipals
parameter_list|(
annotation|@
name|Nonnull
name|String
name|userId
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|syncConfig
operator|.
name|user
argument_list|()
operator|.
name|getMembershipNestingDepth
argument_list|()
operator|==
literal|1
condition|)
block|{
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|idp
operator|.
name|getUser
argument_list|(
name|userId
argument_list|)
operator|.
name|getDeclaredGroups
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|ExternalIdentityRef
argument_list|,
name|Principal
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|Principal
name|apply
parameter_list|(
name|ExternalIdentityRef
name|input
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|PrincipalImpl
argument_list|(
name|idp
operator|.
name|getIdentity
argument_list|(
name|input
argument_list|)
operator|.
name|getPrincipalName
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExternalIdentityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
empty_stmt|;
block|}
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|principals
return|;
block|}
else|else
block|{
name|Set
argument_list|<
name|Principal
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|collectExpectedPrincipals
argument_list|(
name|set
argument_list|,
name|idp
operator|.
name|getUser
argument_list|(
name|userId
argument_list|)
operator|.
name|getDeclaredGroups
argument_list|()
argument_list|,
name|syncConfig
operator|.
name|user
argument_list|()
operator|.
name|getMembershipNestingDepth
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|set
return|;
block|}
block|}
specifier|private
name|void
name|collectExpectedPrincipals
parameter_list|(
name|Set
argument_list|<
name|Principal
argument_list|>
name|grPrincipals
parameter_list|,
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|ExternalIdentityRef
argument_list|>
name|declaredGroups
parameter_list|,
name|long
name|depth
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|depth
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
for|for
control|(
name|ExternalIdentityRef
name|ref
range|:
name|declaredGroups
control|)
block|{
name|ExternalIdentity
name|ei
init|=
name|idp
operator|.
name|getIdentity
argument_list|(
name|ref
argument_list|)
decl_stmt|;
name|grPrincipals
operator|.
name|add
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|ei
operator|.
name|getPrincipalName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|collectExpectedPrincipals
argument_list|(
name|grPrincipals
argument_list|,
name|ei
operator|.
name|getDeclaredGroups
argument_list|()
argument_list|,
name|depth
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalLocalUser
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|principalProvider
operator|.
name|getPrincipal
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
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalLocalGroup
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
name|assertNull
argument_list|(
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|gr
operator|.
name|getPrincipal
argument_list|()
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
name|testGetPrincipalExternalUser
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
comment|// synced by principal-sync-ctx
name|User
name|syncedUser
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|,
name|User
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|syncedUser
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// synced by default-sync-ctx
name|syncedUser
operator|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|,
name|User
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|syncedUser
operator|.
name|getPrincipal
argument_list|()
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
name|testGetPrincipalExternalGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|Group
name|gr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
literal|"secondGroup"
argument_list|,
name|Group
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|gr
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|gr
operator|.
name|getPrincipal
argument_list|()
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
name|testGetPrincipalDynamicGroup
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|ExternalIdentityRef
name|ref
range|:
name|idp
operator|.
name|getUser
argument_list|(
name|USER_ID
argument_list|)
operator|.
name|getDeclaredGroups
argument_list|()
control|)
block|{
name|String
name|princName
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
name|principal
init|=
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|princName
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
name|GroupPrincipal
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalInheritedGroups
parameter_list|()
throws|throws
name|Exception
block|{
name|ImmutableSet
argument_list|<
name|ExternalIdentityRef
argument_list|>
name|declared
init|=
name|ImmutableSet
operator|.
expr|<
name|ExternalIdentityRef
operator|>
name|copyOf
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
argument_list|)
decl_stmt|;
for|for
control|(
name|ExternalIdentityRef
name|ref
range|:
name|declared
control|)
block|{
for|for
control|(
name|ExternalIdentityRef
name|inheritedGroupRef
range|:
name|idp
operator|.
name|getIdentity
argument_list|(
name|ref
argument_list|)
operator|.
name|getDeclaredGroups
argument_list|()
control|)
block|{
if|if
condition|(
name|declared
operator|.
name|contains
argument_list|(
name|inheritedGroupRef
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|String
name|inheritedPrincName
init|=
name|idp
operator|.
name|getIdentity
argument_list|(
name|inheritedGroupRef
argument_list|)
operator|.
name|getPrincipalName
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|inheritedPrincName
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
name|testGetPrincipalUnderscoreSign
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
for|for
control|(
name|ExternalIdentityRef
name|ref
range|:
name|externalUser
operator|.
name|getDeclaredGroups
argument_list|()
control|)
block|{
name|String
name|pName
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
for|for
control|(
name|String
name|n
range|:
operator|new
name|String
index|[]
block|{
literal|"_"
block|,
literal|"_"
operator|+
name|pName
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
block|,
name|pName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pName
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|+
literal|"_"
block|}
control|)
block|{
name|assertNull
argument_list|(
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|n
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
name|testGetPrincipalPercentSign
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
for|for
control|(
name|ExternalIdentityRef
name|ref
range|:
name|externalUser
operator|.
name|getDeclaredGroups
argument_list|()
control|)
block|{
name|String
name|pName
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
for|for
control|(
name|String
name|n
range|:
operator|new
name|String
index|[]
block|{
literal|"%"
block|,
literal|"%"
operator|+
name|pName
block|,
name|pName
operator|+
literal|"%"
block|,
name|pName
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|+
literal|"%"
block|}
control|)
block|{
name|assertNull
argument_list|(
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|n
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
name|testGetPrincipalGroupsWithQueryWildCard
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
name|TestIdentityProvider
operator|.
name|ID_WILDCARD_USER
argument_list|)
decl_stmt|;
name|sync
argument_list|(
name|externalUser
argument_list|)
expr_stmt|;
for|for
control|(
name|ExternalIdentityRef
name|ref
range|:
name|externalUser
operator|.
name|getDeclaredGroups
argument_list|()
control|)
block|{
name|String
name|pName
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
name|p
init|=
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|pName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pName
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetGroupMembershipLocalPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
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
name|getMembershipPrincipals
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetGroupMembershipLocalGroupPrincipal
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
name|getMembershipPrincipals
argument_list|(
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// same if the principal is not marked as 'GroupPrincipal' and not tree-based-principal
name|principals
operator|=
name|principalProvider
operator|.
name|getMembershipPrincipals
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|gr
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetGroupMembershipExternalUser
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|user
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|expected
init|=
name|getExpectedGroupPrincipals
argument_list|(
name|USER_ID
argument_list|)
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
name|getMembershipPrincipals
argument_list|(
name|user
operator|.
name|getPrincipal
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|principals
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetGroupMembershipExternalUser2
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|user
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|expected
init|=
name|getExpectedGroupPrincipals
argument_list|(
name|USER_ID
argument_list|)
decl_stmt|;
comment|// same as in test before even if the principal is not a tree-based-principal
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
name|getMembershipPrincipals
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|user
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|principals
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetGroupMembershipDefaultSync
parameter_list|()
throws|throws
name|Exception
block|{
comment|// synchronized by default sync-context => no 'dynamic' group principals
name|Authorizable
name|user
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|user
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
name|getMembershipPrincipals
argument_list|(
name|user
operator|.
name|getPrincipal
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetGroupMembershipDefaultSync2
parameter_list|()
throws|throws
name|Exception
block|{
comment|// synchronized by default sync-context => no 'dynamic' group principals
name|Authorizable
name|user
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|user
argument_list|)
expr_stmt|;
comment|// same as in test before even if the principal is not a tree-based-principal
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
name|getMembershipPrincipals
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|user
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetGroupMembershipExternalGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|group
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
literal|"secondGroup"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|group
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
name|getMembershipPrincipals
argument_list|(
name|group
operator|.
name|getPrincipal
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// same if the principal is not marked as 'GroupPrincipal' and not tree-based-principal
name|principals
operator|=
name|principalProvider
operator|.
name|getMembershipPrincipals
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|group
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalsLocalUser
parameter_list|()
throws|throws
name|Exception
block|{
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
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalsLocalGroup
parameter_list|()
throws|throws
name|Exception
block|{
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
name|createTestGroup
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalsExternalUser
parameter_list|()
throws|throws
name|Exception
block|{
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
name|USER_ID
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getExpectedGroupPrincipals
argument_list|(
name|USER_ID
argument_list|)
argument_list|,
name|principals
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalsExternalUser2
parameter_list|()
block|{
comment|// synchronized by default sync-context => no 'dynamic' group principals
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
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalsExternalGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|authorizable
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
literal|"secondGroup"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|authorizable
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
name|authorizable
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalsNonExistingUser
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
literal|"nonExistingUser"
argument_list|)
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
literal|"nonExistingUser"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipalsByHintTypeNotGroup
parameter_list|()
block|{
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|iter
init|=
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
literal|"a"
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|Collections
operator|.
name|emptyIterator
argument_list|()
argument_list|,
name|iter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipalsByHintTypeGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
literal|"a"
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|res
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
literal|"a"
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipalsByHintTypeAll
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
literal|"a"
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|res
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
literal|"a"
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipalsContainingUnderscore
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
name|TestIdentityProvider
operator|.
name|ID_WILDCARD_USER
argument_list|)
decl_stmt|;
name|sync
argument_list|(
name|externalUser
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
literal|"_gr_u_"
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|res
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
literal|"_"
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipalsContainingPercentSign
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
name|TestIdentityProvider
operator|.
name|ID_WILDCARD_USER
argument_list|)
decl_stmt|;
name|sync
argument_list|(
name|externalUser
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
literal|"g%r%"
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|res
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
literal|"%"
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipalsByTypeNotGroup
parameter_list|()
block|{
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|iter
init|=
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|Collections
operator|.
name|emptyIterator
argument_list|()
argument_list|,
name|iter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipalsByTypeGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|res
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getExpectedGroupPrincipals
argument_list|(
name|USER_ID
argument_list|)
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipalsByTypeAll
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|res
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getExpectedGroupPrincipals
argument_list|(
name|USER_ID
argument_list|)
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipalsFiltersDuplicates
parameter_list|()
throws|throws
name|Exception
block|{
name|ExternalGroup
name|gr
init|=
name|idp
operator|.
name|getGroup
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|ExternalUser
name|otherUser
init|=
operator|new
name|TestUser
argument_list|(
literal|"anotherUser"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|gr
operator|.
name|getExternalId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|sync
argument_list|(
name|otherUser
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|expected
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|expected
operator|.
name|add
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|gr
operator|.
name|getPrincipalName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|depth
init|=
name|syncConfig
operator|.
name|user
argument_list|()
operator|.
name|getMembershipNestingDepth
argument_list|()
decl_stmt|;
if|if
condition|(
name|depth
operator|>
literal|1
condition|)
block|{
name|collectExpectedPrincipals
argument_list|(
name|expected
argument_list|,
name|gr
operator|.
name|getDeclaredGroups
argument_list|()
argument_list|,
operator|--
name|depth
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|res
init|=
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
literal|"a"
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|res
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|res
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TestUser
extends|extends
name|TestIdentityProvider
operator|.
name|TestIdentity
implements|implements
name|ExternalUser
block|{
specifier|private
specifier|final
name|Iterable
argument_list|<
name|ExternalIdentityRef
argument_list|>
name|declaredGroups
decl_stmt|;
specifier|private
name|TestUser
parameter_list|(
annotation|@
name|Nonnull
name|String
name|id
parameter_list|,
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|ExternalIdentityRef
argument_list|>
name|declaredGroups
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|this
operator|.
name|declaredGroups
operator|=
name|declaredGroups
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|ExternalIdentityRef
argument_list|>
name|getDeclaredGroups
parameter_list|()
block|{
return|return
name|declaredGroups
return|;
block|}
block|}
block|}
end_class

end_unit

