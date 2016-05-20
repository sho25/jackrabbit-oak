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
name|SimpleCredentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
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
name|impl
operator|.
name|DefaultSyncConfigImpl
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
name|ExternalIdentityConstants
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
name|whiteboard
operator|.
name|WhiteboardUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sling
operator|.
name|testing
operator|.
name|mock
operator|.
name|osgi
operator|.
name|junit
operator|.
name|OsgiContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|ExternalLoginModuleDynamicMembershipTest
extends|extends
name|ExternalLoginModuleTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|OsgiContext
name|context
init|=
operator|new
name|OsgiContext
argument_list|()
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
name|syncConfig
operator|.
name|user
argument_list|()
operator|.
name|setDynamicMembership
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// register the ExternalPrincipal configuration in order to have it's
comment|// activate method invoked.
name|context
operator|.
name|registerInjectActivateService
argument_list|(
name|externalPrincipalConfiguration
argument_list|)
expr_stmt|;
comment|// now register the sync-handler with the dynamic membership config
comment|// in order to enable dynamic membership with the external principal configuration
name|Map
name|props
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|DefaultSyncConfigImpl
operator|.
name|PARAM_USER_DYNAMIC_MEMBERSHIP
argument_list|,
name|syncConfig
operator|.
name|user
argument_list|()
operator|.
name|getDynamicMembership
argument_list|()
argument_list|)
decl_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|SyncHandler
operator|.
name|class
argument_list|,
name|WhiteboardUtils
operator|.
name|getService
argument_list|(
name|whiteboard
argument_list|,
name|SyncHandler
operator|.
name|class
argument_list|)
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertExternalPrincipalNames
parameter_list|(
annotation|@
name|Nonnull
name|UserManager
name|userMgr
parameter_list|,
annotation|@
name|Nonnull
name|String
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|calcExpectedPrincipalNames
argument_list|(
name|idp
operator|.
name|getUser
argument_list|(
name|id
argument_list|)
argument_list|,
name|syncConfig
operator|.
name|user
argument_list|()
operator|.
name|getMembershipNestingDepth
argument_list|()
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|extPrincNames
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Value
name|v
range|:
name|a
operator|.
name|getProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|)
control|)
block|{
name|extPrincNames
operator|.
name|add
argument_list|(
name|v
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|extPrincNames
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|calcExpectedPrincipalNames
parameter_list|(
annotation|@
name|Nonnull
name|ExternalIdentity
name|identity
parameter_list|,
name|long
name|depth
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|expected
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
name|identity
operator|.
name|getDeclaredGroups
argument_list|()
control|)
block|{
name|ExternalIdentity
name|groupIdentity
init|=
name|idp
operator|.
name|getIdentity
argument_list|(
name|ref
argument_list|)
decl_stmt|;
name|expected
operator|.
name|add
argument_list|(
name|groupIdentity
operator|.
name|getPrincipalName
argument_list|()
argument_list|)
expr_stmt|;
name|calcExpectedPrincipalNames
argument_list|(
name|groupIdentity
argument_list|,
name|depth
operator|-
literal|1
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoginPopulatesPrincipals
parameter_list|()
throws|throws
name|Exception
block|{
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
name|USER_ID
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expectedExternal
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|calcExpectedPrincipalNames
argument_list|(
name|idp
operator|.
name|getUser
argument_list|(
name|USER_ID
argument_list|)
argument_list|,
name|syncConfig
operator|.
name|user
argument_list|()
operator|.
name|getMembershipNestingDepth
argument_list|()
argument_list|,
name|expectedExternal
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|new
name|HashSet
argument_list|<
name|Principal
argument_list|>
argument_list|(
name|cs
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|)
decl_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|PrincipalManager
name|principalManager
init|=
name|getPrincipalManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|pName
range|:
name|expectedExternal
control|)
block|{
name|Principal
name|p
init|=
name|principalManager
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
name|assertTrue
argument_list|(
name|principals
operator|.
name|remove
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|UserManager
name|uMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|User
name|u
init|=
name|uMgr
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
name|assertTrue
argument_list|(
name|principals
operator|.
name|remove
argument_list|(
name|u
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Group
argument_list|>
name|it
init|=
name|u
operator|.
name|memberOf
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|remove
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
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
name|testSyncCreatesRepExternalPrincipals
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|assertExternalPrincipalNames
argument_list|(
name|getUserManager
argument_list|(
name|root
argument_list|)
argument_list|,
name|USER_ID
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
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
name|testSyncCreatesRepExternalPrincipalsDepthInfinite
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
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
try|try
block|{
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|assertExternalPrincipalNames
argument_list|(
name|getUserManager
argument_list|(
name|root
argument_list|)
argument_list|,
name|USER_ID
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
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
try|try
block|{
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|UserManager
name|userManager
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
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
try|try
block|{
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
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
name|assertNull
argument_list|(
name|getUserManager
argument_list|(
name|root
argument_list|)
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
name|testSyncUpdateAfterXmlImport
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|// force initial sync
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// remove properties according to the behavior in the XML-import
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
name|UserManager
name|userManager
init|=
name|getUserManager
argument_list|(
name|systemRoot
argument_list|)
decl_stmt|;
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
decl_stmt|;
name|a
operator|.
name|removeProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|)
expr_stmt|;
name|a
operator|.
name|removeProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_LAST_SYNCED
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// login again to force sync of the user (and it's group membership)
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|systemRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|a
operator|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_LAST_SYNCED
argument_list|)
argument_list|)
expr_stmt|;
name|assertExternalPrincipalNames
argument_list|(
name|userManager
argument_list|,
name|USER_ID
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
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
name|testSyncUpdateWithRemovedPrincipalNames
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|// force initial sync
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// removing the rep:externalPrincipalNames property only will have the same
comment|// effect as the compatibility behavior that respects previously
comment|// synchronized users with full membership sync.
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
name|UserManager
name|userManager
init|=
name|getUserManager
argument_list|(
name|systemRoot
argument_list|)
decl_stmt|;
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
decl_stmt|;
name|a
operator|.
name|removeProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// login again
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|systemRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|a
operator|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_LAST_SYNCED
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|)
argument_list|)
expr_stmt|;
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
name|assertNotNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|ref
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
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

