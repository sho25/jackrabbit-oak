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
name|jmx
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|Map
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
name|oak
operator|.
name|api
operator|.
name|Blob
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
name|CommitFailedException
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
name|Tree
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
name|ExternalIdentityProvider
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
name|DefaultSyncHandler
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
name|assertFalse
import|;
end_import

begin_class
specifier|public
class|class
name|DelegateeTest
extends|extends
name|AbstractJmxTest
block|{
specifier|private
name|Delegatee
name|delegatee
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|TEST_IDS
init|=
operator|new
name|String
index|[]
block|{
name|TestIdentityProvider
operator|.
name|ID_TEST_USER
block|,
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
block|,
name|TestIdentityProvider
operator|.
name|ID_WILDCARD_USER
block|}
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
name|delegatee
operator|=
name|createDelegatee
argument_list|(
operator|new
name|TestIdentityProvider
argument_list|()
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
try|try
block|{
if|if
condition|(
name|delegatee
operator|!=
literal|null
condition|)
block|{
name|delegatee
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|int
name|getBatchSize
parameter_list|()
block|{
return|return
literal|100
return|;
block|}
specifier|private
name|Delegatee
name|createDelegatee
parameter_list|(
annotation|@
name|NotNull
name|ExternalIdentityProvider
name|idp
parameter_list|)
block|{
return|return
name|Delegatee
operator|.
name|createInstance
argument_list|(
name|getContentRepository
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|,
operator|new
name|DefaultSyncHandler
argument_list|(
name|syncConfig
argument_list|)
argument_list|,
name|idp
argument_list|,
name|getBatchSize
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Root
name|preventRootCommit
parameter_list|(
annotation|@
name|NotNull
name|Delegatee
name|delegatee
parameter_list|)
throws|throws
name|Exception
block|{
name|Field
name|rootField
init|=
name|Delegatee
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"root"
argument_list|)
decl_stmt|;
name|rootField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Root
name|r
init|=
operator|(
name|Root
operator|)
name|rootField
operator|.
name|get
argument_list|(
name|delegatee
argument_list|)
decl_stmt|;
name|r
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|rootField
operator|.
name|set
argument_list|(
name|delegatee
argument_list|,
operator|new
name|ThrowingRoot
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDoubleClose
parameter_list|()
block|{
name|delegatee
operator|.
name|close
argument_list|()
expr_stmt|;
name|delegatee
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncUsersBeforeSaveError
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|r
init|=
name|preventRootCommit
argument_list|(
name|delegatee
argument_list|)
decl_stmt|;
empty_stmt|;
name|String
index|[]
name|result
init|=
name|delegatee
operator|.
name|syncUsers
argument_list|(
name|TEST_IDS
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertResultMessages
argument_list|(
name|result
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|TestIdentityProvider
operator|.
name|ID_TEST_USER
argument_list|,
literal|"nsa"
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|,
literal|"nsa"
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_WILDCARD_USER
argument_list|,
literal|"nsa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncUsersSaveError
parameter_list|()
throws|throws
name|Exception
block|{
name|sync
argument_list|(
name|idp
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_TEST_USER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sync
argument_list|(
name|foreignIDP
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// don't sync ID_WILDCARD_USER
name|Root
name|r
init|=
name|preventRootCommit
argument_list|(
name|delegatee
argument_list|)
decl_stmt|;
name|String
index|[]
name|result
init|=
name|delegatee
operator|.
name|syncUsers
argument_list|(
operator|new
name|String
index|[]
block|{
name|TestIdentityProvider
operator|.
name|ID_TEST_USER
block|,
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
block|,
name|TestIdentityProvider
operator|.
name|ID_WILDCARD_USER
block|}
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertResultMessages
argument_list|(
name|result
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|TestIdentityProvider
operator|.
name|ID_TEST_USER
argument_list|,
literal|"ERR"
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|,
literal|"for"
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_WILDCARD_USER
argument_list|,
literal|"nsa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncAllUsersBeforeSaveError
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|r
init|=
name|preventRootCommit
argument_list|(
name|delegatee
argument_list|)
decl_stmt|;
empty_stmt|;
name|String
index|[]
name|result
init|=
name|delegatee
operator|.
name|syncAllUsers
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertResultMessages
argument_list|(
name|result
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncAllUsersSaveError
parameter_list|()
throws|throws
name|Exception
block|{
name|sync
argument_list|(
name|idp
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_TEST_USER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sync
argument_list|(
name|idp
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sync
argument_list|(
operator|new
name|TestIdentityProvider
operator|.
name|TestUser
argument_list|(
literal|"third"
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|idp
argument_list|)
expr_stmt|;
name|sync
argument_list|(
name|foreignIDP
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_WILDCARD_USER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Root
name|r
init|=
name|preventRootCommit
argument_list|(
name|delegatee
argument_list|)
decl_stmt|;
empty_stmt|;
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|expected
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|String
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
name|TestIdentityProvider
operator|.
name|ID_TEST_USER
argument_list|,
literal|"ERR"
argument_list|)
decl|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"ERR"
argument_list|)
decl|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|"ERR"
argument_list|)
decl|.
name|put
argument_list|(
literal|"c"
argument_list|,
literal|"ERR"
argument_list|)
decl|.
name|put
argument_list|(
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|,
literal|"ERR"
argument_list|)
decl|.
name|put
argument_list|(
literal|"secondGroup"
argument_list|,
literal|"ERR"
argument_list|)
decl|.
name|put
argument_list|(
literal|"third"
argument_list|,
literal|"mis"
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
name|String
index|[]
name|result
init|=
name|delegatee
operator|.
name|syncAllUsers
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertResultMessages
argument_list|(
name|result
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|// NOTE: foreign user is not included in the results
name|assertFalse
argument_list|(
name|r
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncAllUsersPurgeSaveError
parameter_list|()
throws|throws
name|Exception
block|{
name|sync
argument_list|(
name|idp
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_TEST_USER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sync
argument_list|(
name|idp
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sync
argument_list|(
operator|new
name|TestIdentityProvider
operator|.
name|TestUser
argument_list|(
literal|"third"
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|idp
argument_list|)
expr_stmt|;
name|sync
argument_list|(
name|foreignIDP
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_WILDCARD_USER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Root
name|r
init|=
name|preventRootCommit
argument_list|(
name|delegatee
argument_list|)
decl_stmt|;
empty_stmt|;
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|expected
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|String
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
name|TestIdentityProvider
operator|.
name|ID_TEST_USER
argument_list|,
literal|"ERR"
argument_list|)
decl|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"ERR"
argument_list|)
decl|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|"ERR"
argument_list|)
decl|.
name|put
argument_list|(
literal|"c"
argument_list|,
literal|"ERR"
argument_list|)
decl|.
name|put
argument_list|(
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|,
literal|"ERR"
argument_list|)
decl|.
name|put
argument_list|(
literal|"secondGroup"
argument_list|,
literal|"ERR"
argument_list|)
decl|.
name|put
argument_list|(
literal|"third"
argument_list|,
literal|"ERR"
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
name|String
index|[]
name|result
init|=
name|delegatee
operator|.
name|syncAllUsers
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertResultMessages
argument_list|(
name|result
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|// NOTE: foreign user is not included in the results
name|assertFalse
argument_list|(
name|r
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncNonExistingExternalUserSaveError
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|r
init|=
name|preventRootCommit
argument_list|(
name|delegatee
argument_list|)
decl_stmt|;
empty_stmt|;
name|String
index|[]
name|result
init|=
name|delegatee
operator|.
name|syncExternalUsers
argument_list|(
operator|new
name|String
index|[]
block|{
operator|new
name|ExternalIdentityRef
argument_list|(
literal|"nonExisting"
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getString
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|assertResultMessages
argument_list|(
name|result
argument_list|,
literal|""
argument_list|,
literal|"nsi"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncForeignExternalUserSaveError
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|r
init|=
name|preventRootCommit
argument_list|(
name|delegatee
argument_list|)
decl_stmt|;
empty_stmt|;
name|String
index|[]
name|result
init|=
name|delegatee
operator|.
name|syncExternalUsers
argument_list|(
operator|new
name|String
index|[]
block|{
operator|new
name|ExternalIdentityRef
argument_list|(
name|TestIdentityProvider
operator|.
name|ID_TEST_USER
argument_list|,
name|foreignIDP
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getString
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|assertResultMessages
argument_list|(
name|result
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_TEST_USER
argument_list|,
literal|"for"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncThrowingExternalUserSaveError
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|r
init|=
name|preventRootCommit
argument_list|(
name|delegatee
argument_list|)
decl_stmt|;
empty_stmt|;
name|String
index|[]
name|result
init|=
name|delegatee
operator|.
name|syncExternalUsers
argument_list|(
operator|new
name|String
index|[]
block|{
operator|new
name|ExternalIdentityRef
argument_list|(
name|TestIdentityProvider
operator|.
name|ID_EXCEPTION
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getString
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|assertResultMessages
argument_list|(
name|result
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_EXCEPTION
argument_list|,
literal|"ERR"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncExternalUsersSaveError
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|r
init|=
name|preventRootCommit
argument_list|(
name|delegatee
argument_list|)
decl_stmt|;
empty_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|externalIds
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|TEST_IDS
control|)
block|{
name|externalIds
operator|.
name|add
argument_list|(
operator|new
name|ExternalIdentityRef
argument_list|(
name|id
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|result
init|=
name|delegatee
operator|.
name|syncExternalUsers
argument_list|(
name|externalIds
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|externalIds
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|assertResultMessages
argument_list|(
name|result
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|TestIdentityProvider
operator|.
name|ID_TEST_USER
argument_list|,
literal|"ERR"
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|,
literal|"ERR"
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_WILDCARD_USER
argument_list|,
literal|"ERR"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncAllExternalUsersSaveError
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|r
init|=
name|preventRootCommit
argument_list|(
name|delegatee
argument_list|)
decl_stmt|;
empty_stmt|;
name|String
index|[]
name|result
init|=
name|delegatee
operator|.
name|syncAllExternalUsers
argument_list|()
decl_stmt|;
name|assertResultMessages
argument_list|(
name|result
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|TestIdentityProvider
operator|.
name|ID_TEST_USER
argument_list|,
literal|"ERR"
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|,
literal|"ERR"
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_WILDCARD_USER
argument_list|,
literal|"ERR"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SyncRuntimeException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testSyncAllExternalUsersThrowingIDP
parameter_list|()
block|{
name|Delegatee
name|dg
init|=
name|createDelegatee
argument_list|(
operator|new
name|TestIdentityProvider
argument_list|(
literal|"throwing"
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ExternalUser
argument_list|>
name|listUsers
parameter_list|()
throws|throws
name|ExternalIdentityException
block|{
throw|throw
operator|new
name|ExternalIdentityException
argument_list|()
throw|;
block|}
block|}
argument_list|)
decl_stmt|;
name|dg
operator|.
name|syncAllExternalUsers
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPurgeOrphanedSaveError
parameter_list|()
throws|throws
name|Exception
block|{
name|sync
argument_list|(
operator|new
name|TestIdentityProvider
operator|.
name|TestUser
argument_list|(
literal|"third"
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|idp
argument_list|)
expr_stmt|;
name|sync
argument_list|(
operator|new
name|TestIdentityProvider
operator|.
name|TestUser
argument_list|(
literal|"forth"
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|idp
argument_list|)
expr_stmt|;
name|sync
argument_list|(
name|idp
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_TEST_USER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Root
name|r
init|=
name|preventRootCommit
argument_list|(
name|delegatee
argument_list|)
decl_stmt|;
empty_stmt|;
name|String
index|[]
name|result
init|=
name|delegatee
operator|.
name|purgeOrphanedUsers
argument_list|()
decl_stmt|;
name|assertResultMessages
argument_list|(
name|result
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"third"
argument_list|,
literal|"ERR"
argument_list|,
literal|"forth"
argument_list|,
literal|"ERR"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|ThrowingRoot
implements|implements
name|Root
block|{
specifier|private
name|Root
name|base
decl_stmt|;
specifier|private
name|ThrowingRoot
parameter_list|(
annotation|@
name|NotNull
name|Root
name|base
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|move
parameter_list|(
name|String
name|srcAbsPath
parameter_list|,
name|String
name|destAbsPath
parameter_list|)
block|{
return|return
name|base
operator|.
name|move
argument_list|(
name|srcAbsPath
argument_list|,
name|destAbsPath
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Tree
name|getTree
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
block|{
return|return
name|base
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|rebase
parameter_list|()
block|{
name|base
operator|.
name|rebase
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|refresh
parameter_list|()
block|{
name|base
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|commit
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|commit
parameter_list|(
annotation|@
name|NotNull
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|OAK
argument_list|,
literal|0
argument_list|,
literal|"failed"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPendingChanges
parameter_list|()
block|{
return|return
name|base
operator|.
name|hasPendingChanges
argument_list|()
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|QueryEngine
name|getQueryEngine
parameter_list|()
block|{
return|return
name|base
operator|.
name|getQueryEngine
argument_list|()
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Blob
name|createBlob
parameter_list|(
annotation|@
name|NotNull
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|base
operator|.
name|createBlob
argument_list|(
name|stream
argument_list|)
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|Blob
name|getBlob
parameter_list|(
annotation|@
name|NotNull
name|String
name|reference
parameter_list|)
block|{
return|return
name|base
operator|.
name|getBlob
argument_list|(
name|reference
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|ContentSession
name|getContentSession
parameter_list|()
block|{
return|return
name|base
operator|.
name|getContentSession
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

