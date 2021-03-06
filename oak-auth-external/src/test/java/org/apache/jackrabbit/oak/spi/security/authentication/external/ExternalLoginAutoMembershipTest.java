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
name|Collections
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
name|ValueFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|AppConfigurationEntry
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
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
name|ImmutableSet
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
name|DefaultSyncHandler
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
name|ExternalLoginModule
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
name|SyncHandlerMapping
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
name|whiteboard
operator|.
name|Registration
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
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
name|ExternalLoginAutoMembershipTest
extends|extends
name|ExternalLoginTestBase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|NON_EXISTING_NAME
init|=
literal|"nonExisting"
decl_stmt|;
specifier|private
name|Root
name|r
decl_stmt|;
specifier|private
name|UserManager
name|userManager
decl_stmt|;
specifier|private
name|ValueFactory
name|valueFactory
decl_stmt|;
specifier|private
name|ExternalSetup
name|setup1
decl_stmt|;
specifier|private
name|ExternalSetup
name|setup2
decl_stmt|;
specifier|private
name|ExternalSetup
name|setup3
decl_stmt|;
specifier|private
name|ExternalSetup
name|setup4
decl_stmt|;
specifier|private
name|ExternalSetup
name|setup5
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
name|r
operator|=
name|getSystemRoot
argument_list|()
expr_stmt|;
name|userManager
operator|=
name|getUserManager
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|valueFactory
operator|=
name|getValueFactory
argument_list|(
name|r
argument_list|)
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
comment|// first configuration based on test base-setup with
comment|// - dynamic membership = true
comment|// - auto-membership = 'gr_default' and 'nonExisting'
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
name|setup1
operator|=
operator|new
name|ExternalSetup
argument_list|(
name|idp
argument_list|,
name|syncConfig
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
literal|"gr"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
expr_stmt|;
comment|// second configuration with different IDP ('idp2') and
comment|// - dynamic membership = true
comment|// - auto-membership = 'gr_name2' and 'nonExisting'
name|DefaultSyncConfig
name|sc2
init|=
operator|new
name|DefaultSyncConfig
argument_list|()
decl_stmt|;
name|sc2
operator|.
name|setName
argument_list|(
literal|"name2"
argument_list|)
operator|.
name|user
argument_list|()
operator|.
name|setDynamicMembership
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setup2
operator|=
operator|new
name|ExternalSetup
argument_list|(
operator|new
name|TestIdentityProvider
argument_list|(
literal|"idp2"
argument_list|)
argument_list|,
name|sc2
argument_list|)
expr_stmt|;
comment|// third configuration with different IDP  ('idp3') and
comment|// - dynamic membership = false
comment|// - auto-membership = 'gr_name3' and 'nonExisting'
name|DefaultSyncConfig
name|sc3
init|=
operator|new
name|DefaultSyncConfig
argument_list|()
decl_stmt|;
name|sc3
operator|.
name|setName
argument_list|(
literal|"name3"
argument_list|)
expr_stmt|;
name|setup3
operator|=
operator|new
name|ExternalSetup
argument_list|(
operator|new
name|TestIdentityProvider
argument_list|(
literal|"idp3"
argument_list|)
argument_list|,
name|sc3
argument_list|)
expr_stmt|;
comment|// forth configuration based on different IDP ('idp4') but re-using
comment|// sync-handler configuration (sc2)
name|setup4
operator|=
operator|new
name|ExternalSetup
argument_list|(
operator|new
name|TestIdentityProvider
argument_list|(
literal|"idp4"
argument_list|)
argument_list|,
name|sc2
argument_list|)
expr_stmt|;
comment|// fifth configuration with different IDP ('idp5') and
comment|// - dynamic membership = true
comment|// - auto-membership => nothing configured
name|DefaultSyncConfig
name|sc5
init|=
operator|new
name|DefaultSyncConfig
argument_list|()
decl_stmt|;
name|sc5
operator|.
name|setName
argument_list|(
literal|"name5"
argument_list|)
operator|.
name|user
argument_list|()
operator|.
name|setDynamicMembership
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setup5
operator|=
operator|new
name|ExternalSetup
argument_list|(
operator|new
name|TestIdentityProvider
argument_list|(
literal|"idp5"
argument_list|)
argument_list|,
name|sc5
argument_list|,
operator|new
name|DefaultSyncHandler
argument_list|(
name|sc5
argument_list|)
argument_list|,
literal|null
argument_list|)
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
name|options
operator|.
name|clear
argument_list|()
expr_stmt|;
try|try
block|{
name|syncConfig
operator|.
name|user
argument_list|()
operator|.
name|setAutoMembership
argument_list|()
operator|.
name|setExpirationTime
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|setup1
operator|.
name|close
argument_list|()
expr_stmt|;
name|setup2
operator|.
name|close
argument_list|()
expr_stmt|;
name|setup3
operator|.
name|close
argument_list|()
expr_stmt|;
name|setup4
operator|.
name|close
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
name|Override
specifier|protected
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
operator|new
name|Configuration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AppConfigurationEntry
index|[]
name|getAppConfigurationEntry
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|AppConfigurationEntry
index|[]
name|entries
init|=
operator|new
name|AppConfigurationEntry
index|[
literal|5
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ExternalSetup
name|setup
range|:
operator|new
name|ExternalSetup
index|[]
block|{
name|setup1
block|,
name|setup2
block|,
name|setup3
block|,
name|setup4
block|,
name|setup5
block|}
control|)
block|{
name|entries
index|[
name|i
operator|++
index|]
operator|=
name|setup
operator|.
name|asConfigurationEntry
argument_list|()
expr_stmt|;
block|}
return|return
name|entries
return|;
block|}
block|}
return|;
block|}
specifier|private
specifier|static
name|void
name|registerSyncHandlerMapping
parameter_list|(
annotation|@
name|NotNull
name|OsgiContext
name|ctx
parameter_list|,
annotation|@
name|NotNull
name|ExternalSetup
name|setup
parameter_list|)
block|{
name|String
name|syncHandlerName
init|=
name|setup
operator|.
name|sc
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|DefaultSyncConfigImpl
operator|.
name|PARAM_NAME
argument_list|,
name|syncHandlerName
argument_list|,
name|DefaultSyncConfigImpl
operator|.
name|PARAM_USER_DYNAMIC_MEMBERSHIP
argument_list|,
name|setup
operator|.
name|sc
operator|.
name|user
argument_list|()
operator|.
name|getDynamicMembership
argument_list|()
argument_list|,
name|DefaultSyncConfigImpl
operator|.
name|PARAM_USER_AUTO_MEMBERSHIP
argument_list|,
name|setup
operator|.
name|sc
operator|.
name|user
argument_list|()
operator|.
name|getAutoMembership
argument_list|()
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|registerService
argument_list|(
name|SyncHandler
operator|.
name|class
argument_list|,
name|setup
operator|.
name|sh
argument_list|,
name|props
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mappingProps
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|SyncHandlerMapping
operator|.
name|PARAM_IDP_NAME
argument_list|,
name|setup
operator|.
name|idp
operator|.
name|getName
argument_list|()
argument_list|,
name|SyncHandlerMapping
operator|.
name|PARAM_SYNC_HANDLER_NAME
argument_list|,
name|syncHandlerName
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|registerService
argument_list|(
name|SyncHandlerMapping
operator|.
name|class
argument_list|,
operator|new
name|SyncHandlerMapping
argument_list|()
block|{}
argument_list|,
name|mappingProps
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoginSyncAutoMembershipSetup1
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ContentSession
name|cs
init|=
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
init|)
block|{
comment|// the login must set the existing auto-membership principals to the subject
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup1
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|NON_EXISTING_NAME
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup2
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup3
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// however, the existing auto-membership group must _not_ have changed
comment|// and the test user must not be a stored member of this group.
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|UserManager
name|uMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|User
name|user
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
name|Group
name|gr
init|=
name|uMgr
operator|.
name|getAuthorizable
argument_list|(
name|setup1
operator|.
name|gr
operator|.
name|getID
argument_list|()
argument_list|,
name|Group
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|gr
operator|.
name|isDeclaredMember
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|gr
operator|.
name|isMember
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoginAfterSyncSetup1
parameter_list|()
throws|throws
name|Exception
block|{
name|setup1
operator|.
name|sync
argument_list|(
name|USER_ID
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
init|(
name|ContentSession
name|cs
init|=
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
init|)
block|{
comment|// the login must set the configured + existing auto-membership principals
comment|// to the subject; non-existing auto-membership entries must be ignored.
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup1
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|NON_EXISTING_NAME
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup2
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup3
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// however, the existing auto-membership group must _not_ have changed
comment|// and the test user must not be a stored member of this group.
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|UserManager
name|uMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|User
name|user
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
name|Group
name|gr
init|=
name|uMgr
operator|.
name|getAuthorizable
argument_list|(
name|setup1
operator|.
name|gr
operator|.
name|getID
argument_list|()
argument_list|,
name|Group
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|gr
operator|.
name|isDeclaredMember
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|gr
operator|.
name|isMember
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoginAfterSyncSetup2
parameter_list|()
throws|throws
name|Exception
block|{
name|setup2
operator|.
name|sync
argument_list|(
name|USER_ID
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
init|(
name|ContentSession
name|cs
init|=
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
init|)
block|{
comment|// the login must set the existing auto-membership principals to the subject
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup2
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|NON_EXISTING_NAME
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup1
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup3
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// however, the existing auto-membership group must _not_ have changed
comment|// and the test user must not be a stored member of this group.
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|UserManager
name|uMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|User
name|user
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
name|Group
name|gr
init|=
name|uMgr
operator|.
name|getAuthorizable
argument_list|(
name|setup2
operator|.
name|gr
operator|.
name|getID
argument_list|()
argument_list|,
name|Group
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|gr
operator|.
name|isDeclaredMember
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|gr
operator|.
name|isMember
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoginAfterSyncSetup3
parameter_list|()
throws|throws
name|Exception
block|{
name|setup3
operator|.
name|sync
argument_list|(
name|USER_ID
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
init|(
name|ContentSession
name|cs
init|=
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
init|)
block|{
comment|// the login must set the existing auto-membership principals to the subject
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup3
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|NON_EXISTING_NAME
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup1
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup2
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// however, the existing auto-membership group must _not_ have changed
comment|// and the test user must not be a stored member of this group.
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|UserManager
name|uMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|User
name|user
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
name|Group
name|gr
init|=
name|uMgr
operator|.
name|getAuthorizable
argument_list|(
name|setup3
operator|.
name|gr
operator|.
name|getID
argument_list|()
argument_list|,
name|Group
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|gr
operator|.
name|isDeclaredMember
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|gr
operator|.
name|isMember
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoginAfterSyncSetup4
parameter_list|()
throws|throws
name|Exception
block|{
name|setup4
operator|.
name|sync
argument_list|(
name|USER_ID
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
init|(
name|ContentSession
name|cs
init|=
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
init|)
block|{
comment|// the login must set the existing auto-membership principals to the subject
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup4
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup2
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|NON_EXISTING_NAME
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup1
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup3
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// however, the existing auto-membership group must _not_ have changed
comment|// and the test user must not be a stored member of this group.
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|UserManager
name|uMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|User
name|user
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
name|Group
name|gr
init|=
name|uMgr
operator|.
name|getAuthorizable
argument_list|(
name|setup4
operator|.
name|gr
operator|.
name|getID
argument_list|()
argument_list|,
name|Group
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|gr
operator|.
name|isDeclaredMember
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|gr
operator|.
name|isMember
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoginAfterSyncSetup5
parameter_list|()
throws|throws
name|Exception
block|{
name|setup5
operator|.
name|sync
argument_list|(
name|USER_ID
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
init|(
name|ContentSession
name|cs
init|=
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
init|)
block|{
comment|// the login must not set any auto-membership principals to the subject
comment|// as auto-membership is not configured on this setup.
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
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
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|NON_EXISTING_NAME
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup1
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup2
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup3
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|setup4
operator|.
name|gr
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
class|class
name|ExternalSetup
block|{
specifier|private
specifier|final
name|ExternalIdentityProvider
name|idp
decl_stmt|;
specifier|private
specifier|final
name|Registration
name|idpRegistration
decl_stmt|;
specifier|private
specifier|final
name|DefaultSyncConfig
name|sc
decl_stmt|;
specifier|private
specifier|final
name|SyncHandler
name|sh
decl_stmt|;
specifier|private
specifier|final
name|Registration
name|shRegistration
decl_stmt|;
specifier|private
specifier|final
name|Group
name|gr
decl_stmt|;
specifier|private
name|SyncContext
name|ctx
decl_stmt|;
specifier|private
name|ExternalSetup
parameter_list|(
annotation|@
name|NotNull
name|ExternalIdentityProvider
name|idp
parameter_list|,
annotation|@
name|NotNull
name|DefaultSyncConfig
name|sc
parameter_list|)
throws|throws
name|Exception
block|{
name|this
argument_list|(
name|idp
argument_list|,
name|sc
argument_list|,
operator|new
name|DefaultSyncHandler
argument_list|(
name|sc
argument_list|)
argument_list|,
literal|"gr_"
operator|+
name|sc
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ExternalSetup
parameter_list|(
annotation|@
name|NotNull
name|ExternalIdentityProvider
name|idp
parameter_list|,
annotation|@
name|NotNull
name|DefaultSyncConfig
name|sc
parameter_list|,
annotation|@
name|NotNull
name|SyncHandler
name|sh
parameter_list|,
annotation|@
name|Nullable
name|String
name|groupId
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|idp
operator|=
name|idp
expr_stmt|;
name|this
operator|.
name|sc
operator|=
name|sc
expr_stmt|;
name|this
operator|.
name|sh
operator|=
name|sh
expr_stmt|;
if|if
condition|(
name|groupId
operator|!=
literal|null
condition|)
block|{
name|Group
name|g
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|groupId
argument_list|,
name|Group
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|g
operator|!=
literal|null
condition|)
block|{
name|gr
operator|=
name|g
expr_stmt|;
block|}
else|else
block|{
name|gr
operator|=
name|userManager
operator|.
name|createGroup
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|commit
argument_list|()
expr_stmt|;
name|sc
operator|.
name|user
argument_list|()
operator|.
name|setAutoMembership
argument_list|(
name|gr
operator|.
name|getID
argument_list|()
argument_list|,
name|NON_EXISTING_NAME
argument_list|)
operator|.
name|setExpirationTime
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|gr
operator|=
literal|null
expr_stmt|;
block|}
name|idpRegistration
operator|=
name|whiteboard
operator|.
name|register
argument_list|(
name|ExternalIdentityProvider
operator|.
name|class
argument_list|,
name|idp
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
name|shRegistration
operator|=
name|whiteboard
operator|.
name|register
argument_list|(
name|SyncHandler
operator|.
name|class
argument_list|,
name|sh
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|DefaultSyncConfigImpl
operator|.
name|PARAM_NAME
argument_list|,
name|sh
operator|.
name|getName
argument_list|()
argument_list|,
name|DefaultSyncConfigImpl
operator|.
name|PARAM_USER_DYNAMIC_MEMBERSHIP
argument_list|,
name|sc
operator|.
name|user
argument_list|()
operator|.
name|getDynamicMembership
argument_list|()
argument_list|,
name|DefaultSyncConfigImpl
operator|.
name|PARAM_GROUP_AUTO_MEMBERSHIP
argument_list|,
name|sc
operator|.
name|user
argument_list|()
operator|.
name|getAutoMembership
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|registerSyncHandlerMapping
argument_list|(
name|context
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sync
parameter_list|(
annotation|@
name|NotNull
name|String
name|id
parameter_list|,
name|boolean
name|isGroup
parameter_list|)
throws|throws
name|Exception
block|{
name|ctx
operator|=
name|sh
operator|.
name|createContext
argument_list|(
name|idp
argument_list|,
name|userManager
argument_list|,
name|valueFactory
argument_list|)
expr_stmt|;
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
name|assertEquals
argument_list|(
name|idp
operator|.
name|getName
argument_list|()
argument_list|,
name|res
operator|.
name|getIdentity
argument_list|()
operator|.
name|getExternalIdRef
argument_list|()
operator|.
name|getProviderName
argument_list|()
argument_list|)
expr_stmt|;
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
name|r
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|ctx
operator|!=
literal|null
condition|)
block|{
name|ctx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|idpRegistration
operator|!=
literal|null
condition|)
block|{
name|idpRegistration
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|shRegistration
operator|!=
literal|null
condition|)
block|{
name|shRegistration
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|AppConfigurationEntry
name|asConfigurationEntry
parameter_list|()
block|{
return|return
operator|new
name|AppConfigurationEntry
argument_list|(
name|ExternalLoginModule
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|SUFFICIENT
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|of
argument_list|(
name|SyncHandlerMapping
operator|.
name|PARAM_SYNC_HANDLER_NAME
argument_list|,
name|sh
operator|.
name|getName
argument_list|()
argument_list|,
name|SyncHandlerMapping
operator|.
name|PARAM_IDP_NAME
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

