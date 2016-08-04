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
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
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
name|util
operator|.
name|NodeUtil
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
name|assertTrue
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
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|ExternalIdentityValidatorTest
extends|extends
name|ExternalLoginModuleTestBase
block|{
name|String
name|testUserPath
decl_stmt|;
name|String
name|externalUserPath
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
name|testUserPath
operator|=
name|getTestUser
argument_list|()
operator|.
name|getPath
argument_list|()
expr_stmt|;
comment|// force an external user to be synchronized into the repo
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
name|Authorizable
name|a
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
name|a
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|isDynamic
argument_list|()
argument_list|,
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
name|externalUserPath
operator|=
name|a
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
name|config
operator|.
name|user
argument_list|()
operator|.
name|setDynamicMembership
argument_list|(
name|isDynamic
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|config
return|;
block|}
specifier|protected
name|boolean
name|isDynamic
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddExternalPrincipalNames
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|userTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
decl_stmt|;
name|NodeUtil
name|userNode
init|=
operator|new
name|NodeUtil
argument_list|(
name|userTree
argument_list|)
decl_stmt|;
try|try
block|{
name|userNode
operator|.
name|setStrings
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|,
literal|"principalName"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Creating rep:externalPrincipalNames must be detected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertEquals
argument_list|(
literal|70
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddExternalPrincipalNamesAsSystemMissingExternalId
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeUtil
name|n
init|=
operator|new
name|NodeUtil
argument_list|(
name|systemRoot
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
argument_list|)
decl_stmt|;
name|n
operator|.
name|setStrings
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|,
literal|"principalName"
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Creating rep:externalPrincipalNames without rep:externalId must be detected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertEquals
argument_list|(
literal|72
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|systemRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddExternalPrincipalNamesAsSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
name|NodeUtil
name|n
init|=
operator|new
name|NodeUtil
argument_list|(
name|systemRoot
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
argument_list|)
decl_stmt|;
name|n
operator|.
name|setString
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
literal|"externalId"
argument_list|)
expr_stmt|;
name|n
operator|.
name|setStrings
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|,
literal|"principalName"
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveExternalPrincipalNames
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|userTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|externalUserPath
argument_list|)
decl_stmt|;
try|try
block|{
name|userTree
operator|.
name|removeProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Removing rep:externalPrincipalNames must be detected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertEquals
argument_list|(
literal|70
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveExternalPrincipalNamesAsSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
name|NodeUtil
name|n
init|=
operator|new
name|NodeUtil
argument_list|(
name|systemRoot
operator|.
name|getTree
argument_list|(
name|externalUserPath
argument_list|)
argument_list|)
decl_stmt|;
comment|// removal with system root must succeed
name|n
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModifyExternalPrincipalNames
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|userTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|externalUserPath
argument_list|)
decl_stmt|;
try|try
block|{
name|userTree
operator|.
name|setProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"principalNames"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Changing rep:externalPrincipalNames must be detected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertEquals
argument_list|(
literal|70
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModifyExternalPrincipalNamesAsSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
name|NodeUtil
name|n
init|=
operator|new
name|NodeUtil
argument_list|(
name|systemRoot
operator|.
name|getTree
argument_list|(
name|externalUserPath
argument_list|)
argument_list|)
decl_stmt|;
comment|// changing with system root must succeed
name|n
operator|.
name|setStrings
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|,
literal|"principalNames"
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExternalPrincipalNamesType
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
name|Tree
name|userTree
init|=
name|systemRoot
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
decl_stmt|;
name|java
operator|.
name|util
operator|.
name|Map
argument_list|<
name|Type
argument_list|,
name|Object
argument_list|>
name|valMap
init|=
name|ImmutableMap
operator|.
expr|<
name|Type
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
name|Type
operator|.
name|BOOLEANS
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
argument_list|,
name|Type
operator|.
name|LONGS
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
operator|new
name|Long
argument_list|(
literal|1234
argument_list|)
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"id"
argument_list|,
literal|"id2"
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Type
name|t
range|:
name|valMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Object
name|val
init|=
name|valMap
operator|.
name|get
argument_list|(
name|t
argument_list|)
decl_stmt|;
try|try
block|{
name|userTree
operator|.
name|setProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|,
name|val
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Creating rep:externalPrincipalNames with type "
operator|+
name|t
operator|+
literal|" must be detected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertEquals
argument_list|(
literal|71
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|systemRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExternalPrincipalNamesSingle
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeUtil
name|n
init|=
operator|new
name|NodeUtil
argument_list|(
name|systemRoot
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
argument_list|)
decl_stmt|;
name|n
operator|.
name|setString
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Creating rep:externalPrincipalNames as single STRING property must be detected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertEquals
argument_list|(
literal|71
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|systemRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRepExternalIdMultiple
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeUtil
name|n
init|=
operator|new
name|NodeUtil
argument_list|(
name|systemRoot
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
argument_list|)
decl_stmt|;
name|n
operator|.
name|setStrings
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
literal|"id"
argument_list|,
literal|"id2"
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Creating rep:externalId as multiple STRING property must be detected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertEquals
argument_list|(
literal|75
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|systemRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRepExternalIdType
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
name|Tree
name|userTree
init|=
name|systemRoot
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
decl_stmt|;
name|java
operator|.
name|util
operator|.
name|Map
argument_list|<
name|Type
argument_list|,
name|Object
argument_list|>
name|valMap
init|=
name|ImmutableMap
operator|.
expr|<
name|Type
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|,
name|Type
operator|.
name|LONG
argument_list|,
operator|new
name|Long
argument_list|(
literal|1234
argument_list|)
argument_list|,
name|Type
operator|.
name|NAME
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
for|for
control|(
name|Type
name|t
range|:
name|valMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Object
name|val
init|=
name|valMap
operator|.
name|get
argument_list|(
name|t
argument_list|)
decl_stmt|;
try|try
block|{
name|userTree
operator|.
name|setProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
name|val
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Creating rep:externalId with type "
operator|+
name|t
operator|+
literal|" must be detected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertEquals
argument_list|(
literal|75
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|systemRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateUserWithRepExternalId
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|u
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createUser
argument_list|(
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|root
operator|.
name|getTree
argument_list|(
name|u
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
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
name|testCreateUserWithRepExternalIdAsSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
name|User
name|u
init|=
name|getUserManager
argument_list|(
name|systemRoot
argument_list|)
operator|.
name|createUser
argument_list|(
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|systemRoot
operator|.
name|getTree
argument_list|(
name|u
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
name|TestIdentityProvider
operator|.
name|ID_SECOND_USER
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddRepExternalId
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|root
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Adding rep:externalId must be detected in the default setup."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success: verify nature of the exception
name|assertTrue
argument_list|(
name|e
operator|.
name|isConstraintViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|74
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddRepExternalIdAsSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
name|systemRoot
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModifyRepExternalId
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|root
operator|.
name|getTree
argument_list|(
name|externalUserPath
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
literal|"anotherValue"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Modification of rep:externalId must be detected in the default setup."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success: verify nature of the exception
name|assertTrue
argument_list|(
name|e
operator|.
name|isConstraintViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|74
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModifyRepExternalIdAsSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
name|systemRoot
operator|.
name|getTree
argument_list|(
name|externalUserPath
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
literal|"anotherValue"
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveRepExternalId
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|root
operator|.
name|getTree
argument_list|(
name|externalUserPath
argument_list|)
operator|.
name|removeProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Removal of rep:externalId must be detected in the default setup."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success: verify nature of the exception
name|assertTrue
argument_list|(
name|e
operator|.
name|isConstraintViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|73
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveRepExternalIdAsSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeUtil
name|n
init|=
operator|new
name|NodeUtil
argument_list|(
name|systemRoot
operator|.
name|getTree
argument_list|(
name|externalUserPath
argument_list|)
argument_list|)
decl_stmt|;
name|n
operator|.
name|removeProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Removing rep:externalId is not allowed if rep:externalPrincipalNames is present."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertEquals
argument_list|(
literal|73
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|systemRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveRepExternalIdWithoutPrincipalNames
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
name|systemRoot
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
literal|"id"
argument_list|)
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
try|try
block|{
name|root
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
operator|.
name|removeProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Removal of rep:externalId must be detected in the default setup."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success: verify nature of the exception
name|assertTrue
argument_list|(
name|e
operator|.
name|isConstraintViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|74
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveRepExternalIdWithoutPrincipalNamesAsSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
name|systemRoot
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|systemRoot
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
operator|.
name|removeProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveExternalUser
parameter_list|()
throws|throws
name|Exception
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
name|remove
argument_list|()
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
name|testRemoveExternalUserTree
parameter_list|()
throws|throws
name|Exception
block|{
name|root
operator|.
name|getTree
argument_list|(
name|externalUserPath
argument_list|)
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
end_class

end_unit

