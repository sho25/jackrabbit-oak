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
name|authentication
operator|.
name|external
operator|.
name|AbstractExternalAuthTest
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
name|UUID
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractPrincipalTest
extends|extends
name|AbstractExternalAuthTest
block|{
name|PrincipalProvider
name|principalProvider
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
comment|// sync external users into the system using the 2 different sync-context implementations
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
name|SyncContext
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
name|idp
operator|.
name|getUser
argument_list|(
name|USER_ID
argument_list|)
argument_list|)
expr_stmt|;
name|syncContext
operator|.
name|close
argument_list|()
expr_stmt|;
name|syncContext
operator|=
operator|new
name|DefaultSyncContext
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
expr_stmt|;
name|syncContext
operator|.
name|sync
argument_list|(
name|idp
operator|.
name|getUser
argument_list|(
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|)
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
name|principalProvider
operator|=
name|createPrincipalProvider
argument_list|()
expr_stmt|;
block|}
annotation|@
name|NotNull
specifier|private
name|PrincipalProvider
name|createPrincipalProvider
parameter_list|()
block|{
return|return
operator|new
name|ExternalGroupPrincipalProvider
argument_list|(
name|root
argument_list|,
name|getSecurityProvider
argument_list|()
operator|.
name|getConfiguration
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
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
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|protected
name|DefaultSyncConfig
name|createSyncConfig
parameter_list|()
block|{
name|DefaultSyncConfig
name|config
init|=
name|super
operator|.
name|createSyncConfig
argument_list|()
decl_stmt|;
name|DefaultSyncConfig
operator|.
name|User
name|u
init|=
name|config
operator|.
name|user
argument_list|()
decl_stmt|;
name|u
operator|.
name|setDynamicMembership
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|config
return|;
block|}
name|String
index|[]
name|getAutoMembership
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|toArray
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|syncConfig
operator|.
name|user
argument_list|()
operator|.
name|getAutoMembership
argument_list|()
argument_list|,
name|syncConfig
operator|.
name|group
argument_list|()
operator|.
name|getAutoMembership
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|class
argument_list|)
return|;
block|}
name|GroupPrincipal
name|getGroupPrincipal
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
return|return
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
return|;
block|}
name|GroupPrincipal
name|getGroupPrincipal
parameter_list|(
annotation|@
name|NotNull
name|ExternalIdentityRef
name|ref
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|principalName
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
name|principalName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
operator|instanceof
name|GroupPrincipal
argument_list|)
expr_stmt|;
return|return
operator|(
name|GroupPrincipal
operator|)
name|p
return|;
block|}
name|Group
name|createTestGroup
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
name|createGroup
argument_list|(
literal|"group"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
name|gr
return|;
block|}
block|}
end_class

end_unit

