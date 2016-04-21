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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
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
name|ExternalLoginModuleTestBase
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
name|SyncContext
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
name|SyncHandler
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
name|SyncResult
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
name|SyncedIdentity
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
name|basic
operator|.
name|DefaultSyncConfig
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
name|basic
operator|.
name|DefaultSyncContext
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
name|basic
operator|.
name|DefaultSyncedIdentity
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

begin_comment
comment|/**  * DefaultSyncHandlerTest  */
end_comment

begin_class
specifier|public
class|class
name|DefaultSyncHandlerTest
extends|extends
name|ExternalLoginModuleTestBase
block|{
specifier|private
name|UserManager
name|userManager
decl_stmt|;
specifier|private
name|DefaultSyncHandler
name|syncHandler
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
name|userManager
operator|=
name|getUserManager
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|SyncHandler
name|sh
init|=
name|syncManager
operator|.
name|getSyncHandler
argument_list|(
literal|"default"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|sh
operator|instanceof
name|DefaultSyncHandler
argument_list|)
expr_stmt|;
name|syncHandler
operator|=
operator|(
name|DefaultSyncHandler
operator|)
name|sh
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
annotation|@
name|Override
specifier|protected
name|void
name|setSyncConfig
parameter_list|(
name|DefaultSyncConfig
name|cfg
parameter_list|)
block|{
if|if
condition|(
name|cfg
operator|!=
literal|null
condition|)
block|{
name|cfg
operator|.
name|user
argument_list|()
operator|.
name|setExpirationTime
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|group
argument_list|()
operator|.
name|setExpirationTime
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|setSyncConfig
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sync
parameter_list|(
annotation|@
name|Nonnull
name|String
name|id
parameter_list|,
name|boolean
name|isGroup
parameter_list|)
throws|throws
name|Exception
block|{
name|SyncContext
name|ctx
init|=
name|syncHandler
operator|.
name|createContext
argument_list|(
name|idp
argument_list|,
name|userManager
argument_list|,
name|getValueFactory
argument_list|()
argument_list|)
decl_stmt|;
name|ExternalIdentity
name|exIdentity
init|=
operator|(
name|isGroup
operator|)
condition|?
name|idp
operator|.
name|getGroup
argument_list|(
name|id
argument_list|)
else|:
name|idp
operator|.
name|getUser
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|exIdentity
argument_list|)
expr_stmt|;
name|SyncResult
name|res
init|=
name|ctx
operator|.
name|sync
argument_list|(
name|exIdentity
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|SyncResult
operator|.
name|Status
operator|.
name|ADD
argument_list|,
name|res
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetName
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|syncConfig
operator|.
name|getName
argument_list|()
argument_list|,
name|syncHandler
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateContext
parameter_list|()
throws|throws
name|Exception
block|{
name|SyncContext
name|ctx
init|=
name|syncHandler
operator|.
name|createContext
argument_list|(
name|idp
argument_list|,
name|userManager
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
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|instanceof
name|DefaultSyncContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindMissingIdentity
parameter_list|()
throws|throws
name|Exception
block|{
name|SyncedIdentity
name|id
init|=
name|syncHandler
operator|.
name|findIdentity
argument_list|(
name|userManager
argument_list|,
literal|"foobar"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"unknown authorizable should not exist"
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindLocalIdentity
parameter_list|()
throws|throws
name|Exception
block|{
name|SyncedIdentity
name|id
init|=
name|syncHandler
operator|.
name|findIdentity
argument_list|(
name|userManager
argument_list|,
literal|"admin"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"known authorizable should exist"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"local user should not have external ref"
argument_list|,
name|id
operator|.
name|getExternalIdRef
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindExternalIdentity
parameter_list|()
throws|throws
name|Exception
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
name|SyncedIdentity
name|id
init|=
name|syncHandler
operator|.
name|findIdentity
argument_list|(
name|userManager
argument_list|,
name|USER_ID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"known authorizable should exist"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|ExternalIdentityRef
name|ref
init|=
name|id
operator|.
name|getExternalIdRef
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"external user should have correct external ref.idp"
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|,
name|ref
operator|.
name|getProviderName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"external user should have correct external ref.id"
argument_list|,
name|USER_ID
argument_list|,
name|id
operator|.
name|getExternalIdRef
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindGroupIdentity
parameter_list|()
throws|throws
name|Exception
block|{
name|SyncedIdentity
name|si
init|=
name|syncHandler
operator|.
name|findIdentity
argument_list|(
name|userManager
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|si
argument_list|)
expr_stmt|;
name|sync
argument_list|(
literal|"c"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|si
operator|=
name|syncHandler
operator|.
name|findIdentity
argument_list|(
name|userManager
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|si
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|si
operator|.
name|isGroup
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|si
operator|.
name|getExternalIdRef
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindIdentityWithRemovedExternalId
parameter_list|()
throws|throws
name|Exception
block|{
name|sync
argument_list|(
name|USER_ID
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// NOTE: this is only possible as long the rep:externalId property is not protected
name|Authorizable
name|authorizable
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
decl_stmt|;
name|authorizable
operator|.
name|removeProperty
argument_list|(
name|DefaultSyncContext
operator|.
name|REP_EXTERNAL_ID
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SyncedIdentity
name|si
init|=
name|syncHandler
operator|.
name|findIdentity
argument_list|(
name|userManager
argument_list|,
name|USER_ID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|si
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|si
operator|.
name|getExternalIdRef
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRequiresSyncAfterCreate
parameter_list|()
throws|throws
name|Exception
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
name|SyncedIdentity
name|id
init|=
name|syncHandler
operator|.
name|findIdentity
argument_list|(
name|userManager
argument_list|,
name|USER_ID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Known authorizable should exist"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Freshly synced id should not require sync"
argument_list|,
name|syncHandler
operator|.
name|requiresSync
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRequiresSyncExpiredSyncProperty
parameter_list|()
throws|throws
name|Exception
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
specifier|final
name|Calendar
name|nowCal
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|nowCal
operator|.
name|setTimeInMillis
argument_list|(
name|nowCal
operator|.
name|getTimeInMillis
argument_list|()
operator|-
literal|1000
argument_list|)
expr_stmt|;
name|Value
name|nowValue
init|=
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|nowCal
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
name|setProperty
argument_list|(
name|DefaultSyncContext
operator|.
name|REP_LAST_SYNCED
argument_list|,
name|nowValue
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SyncedIdentity
name|id
init|=
name|syncHandler
operator|.
name|findIdentity
argument_list|(
name|userManager
argument_list|,
name|USER_ID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"known authorizable should exist"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"synced id should require sync"
argument_list|,
name|syncHandler
operator|.
name|requiresSync
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRequiresSyncMissingSyncProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|sync
argument_list|(
name|USER_ID
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
name|DefaultSyncContext
operator|.
name|REP_LAST_SYNCED
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SyncedIdentity
name|si
init|=
name|syncHandler
operator|.
name|findIdentity
argument_list|(
name|userManager
argument_list|,
name|USER_ID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|si
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|syncHandler
operator|.
name|requiresSync
argument_list|(
name|si
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRequiresSyncMissingExternalIDRef
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|syncHandler
operator|.
name|requiresSync
argument_list|(
operator|new
name|DefaultSyncedIdentity
argument_list|(
name|USER_ID
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRequiresSyncNotYetSynced
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|syncHandler
operator|.
name|requiresSync
argument_list|(
operator|new
name|DefaultSyncedIdentity
argument_list|(
name|USER_ID
argument_list|,
name|idp
operator|.
name|getUser
argument_list|(
name|USER_ID
argument_list|)
operator|.
name|getExternalId
argument_list|()
argument_list|,
literal|false
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRequiresSyncGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|sync
argument_list|(
literal|"c"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|SyncedIdentity
name|si
init|=
name|syncHandler
operator|.
name|findIdentity
argument_list|(
name|userManager
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|si
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|si
operator|.
name|isGroup
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|syncHandler
operator|.
name|requiresSync
argument_list|(
name|si
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testListIdentitiesBeforeSync
parameter_list|()
throws|throws
name|Exception
block|{
name|Iterator
argument_list|<
name|SyncedIdentity
argument_list|>
name|identities
init|=
name|syncHandler
operator|.
name|listIdentities
argument_list|(
name|userManager
argument_list|)
decl_stmt|;
while|while
condition|(
name|identities
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SyncedIdentity
name|si
init|=
name|identities
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|si
operator|.
name|getExternalIdRef
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testListIdentitiesAfterSync
parameter_list|()
throws|throws
name|Exception
block|{
name|sync
argument_list|(
name|USER_ID
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// membership-nesting is 1 => expect only 'USER_ID' plus the declared group-membership
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|USER_ID
argument_list|)
decl_stmt|;
for|for
control|(
name|ExternalIdentityRef
name|extRef
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
name|expected
operator|.
name|add
argument_list|(
name|extRef
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|SyncedIdentity
argument_list|>
name|identities
init|=
name|syncHandler
operator|.
name|listIdentities
argument_list|(
name|userManager
argument_list|)
decl_stmt|;
while|while
condition|(
name|identities
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SyncedIdentity
name|si
init|=
name|identities
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|expected
operator|.
name|contains
argument_list|(
name|si
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|expected
operator|.
name|remove
argument_list|(
name|si
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|si
operator|.
name|getExternalIdRef
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNull
argument_list|(
name|si
operator|.
name|getExternalIdRef
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|expected
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

