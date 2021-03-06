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
name|authorization
operator|.
name|permission
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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlManager
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
name|JcrConstants
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
name|JackrabbitAccessControlList
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
name|commons
operator|.
name|jackrabbit
operator|.
name|authorization
operator|.
name|AccessControlUtils
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
name|AbstractSecurityTest
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
name|commons
operator|.
name|PathUtils
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
name|memory
operator|.
name|PropertyStates
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
name|tree
operator|.
name|TreeUtil
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
name|security
operator|.
name|authorization
operator|.
name|ProviderCtx
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
name|commit
operator|.
name|MoveTracker
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
name|namespace
operator|.
name|NamespaceConstants
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|authorization
operator|.
name|AuthorizationConfiguration
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
name|authorization
operator|.
name|accesscontrol
operator|.
name|AccessControlConstants
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
name|authorization
operator|.
name|permission
operator|.
name|PermissionProvider
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
name|authorization
operator|.
name|permission
operator|.
name|TreePermission
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
name|privilege
operator|.
name|PrivilegeConstants
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
name|state
operator|.
name|NodeState
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
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_CREATED
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
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
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
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|JCR_CREATEDBY
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|MIX_CREATED
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
name|version
operator|.
name|VersionConstants
operator|.
name|REP_VERSIONSTORAGE
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
name|version
operator|.
name|VersionConstants
operator|.
name|VERSION_STORE_PATH
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
name|assertTrue
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
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|PermissionValidatorTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TEST_ROOT_PATH
init|=
literal|"/testRoot"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_CHILD_PATH
init|=
literal|"/testRoot/child"
decl_stmt|;
specifier|private
name|Principal
name|testPrincipal
decl_stmt|;
annotation|@
name|Before
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
name|Tree
name|rootNode
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|testTree
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|rootNode
argument_list|,
literal|"testRoot"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|testTree
argument_list|,
literal|"child"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|testPrincipal
operator|=
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
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
comment|// revert uncommitted changes
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
comment|// remove all test content
name|root
operator|.
name|getTree
argument_list|(
name|TEST_ROOT_PATH
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
finally|finally
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|grant
parameter_list|(
annotation|@
name|Nullable
name|String
name|path
parameter_list|,
annotation|@
name|NotNull
name|String
modifier|...
name|privilegeNames
parameter_list|)
throws|throws
name|Exception
block|{
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addEntry
argument_list|(
name|testPrincipal
argument_list|,
name|AccessControlUtils
operator|.
name|privilegesFromNames
argument_list|(
name|acMgr
argument_list|,
name|privilegeNames
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|path
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|private
name|PermissionValidator
name|createValidator
parameter_list|(
annotation|@
name|NotNull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|,
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
block|{
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
decl_stmt|;
name|NodeState
name|ns
init|=
name|getTreeProvider
argument_list|()
operator|.
name|asNodeState
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|ProviderCtx
name|ctx
init|=
name|mock
argument_list|(
name|ProviderCtx
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|ctx
operator|.
name|getSecurityProvider
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ctx
operator|.
name|getTreeProvider
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|getTreeProvider
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|wspName
init|=
name|root
operator|.
name|getContentSession
argument_list|()
operator|.
name|getWorkspaceName
argument_list|()
decl_stmt|;
name|PermissionProvider
name|pp
init|=
name|getConfig
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getPermissionProvider
argument_list|(
name|root
argument_list|,
name|wspName
argument_list|,
name|principals
argument_list|)
decl_stmt|;
name|PermissionValidatorProvider
name|pvp
init|=
operator|new
name|PermissionValidatorProvider
argument_list|(
name|wspName
argument_list|,
name|principals
argument_list|,
operator|new
name|MoveTracker
argument_list|()
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|PermissionValidator
name|validator
init|=
operator|new
name|PermissionValidator
argument_list|(
name|ns
argument_list|,
name|ns
argument_list|,
name|pp
argument_list|,
name|pvp
argument_list|)
decl_stmt|;
name|TreePermission
name|tp
init|=
name|pp
operator|.
name|getTreePermission
argument_list|(
name|t
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|t
operator|=
name|t
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|ns
operator|=
name|ns
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|tp
operator|=
name|tp
operator|.
name|getChildPermission
argument_list|(
name|name
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|validator
operator|=
operator|new
name|PermissionValidator
argument_list|(
name|t
argument_list|,
name|t
argument_list|,
name|tp
argument_list|,
name|validator
argument_list|)
expr_stmt|;
block|}
return|return
name|validator
return|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testLockPermissions
parameter_list|()
throws|throws
name|Exception
block|{
comment|// grant the test session the ability to read/write that node but don't allow jcr:lockManagement
name|grant
argument_list|(
name|TEST_ROOT_PATH
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_WRITE
argument_list|)
expr_stmt|;
try|try
init|(
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
init|)
block|{
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|testChild
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
name|TEST_CHILD_PATH
argument_list|)
decl_stmt|;
name|testChild
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_LOCKOWNER
argument_list|,
literal|"lockOwner"
argument_list|)
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRepositoryPermissionsNamespaces
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
init|)
block|{
name|PermissionValidator
name|validator
init|=
name|createValidator
argument_list|(
name|testSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
name|NamespaceConstants
operator|.
name|NAMESPACES_PATH
argument_list|)
decl_stmt|;
name|validator
operator|.
name|childNodeAdded
argument_list|(
literal|"any"
argument_list|,
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRepositoryPermissionsNodeTypes
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
init|)
block|{
name|PermissionValidator
name|validator
init|=
name|createValidator
argument_list|(
name|testSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
argument_list|)
decl_stmt|;
name|validator
operator|.
name|childNodeDeleted
argument_list|(
literal|"any"
argument_list|,
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRepositoryPermissionsPrivileges
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
init|)
block|{
name|PermissionValidator
name|validator
init|=
name|createValidator
argument_list|(
name|testSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
name|PrivilegeConstants
operator|.
name|PRIVILEGES_PATH
argument_list|)
decl_stmt|;
name|validator
operator|.
name|propertyAdded
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"any"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRemoveVersionStorageTree
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
decl_stmt|;
name|NodeState
name|ns
init|=
name|getTreeProvider
argument_list|()
operator|.
name|asNodeState
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|ProviderCtx
name|ctx
init|=
name|mock
argument_list|(
name|ProviderCtx
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|ctx
operator|.
name|getSecurityProvider
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ctx
operator|.
name|getTreeProvider
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|getTreeProvider
argument_list|()
argument_list|)
expr_stmt|;
name|PermissionValidatorProvider
name|pvp
init|=
operator|new
name|PermissionValidatorProvider
argument_list|(
literal|"wspName"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
operator|new
name|MoveTracker
argument_list|()
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|PermissionValidator
name|validator
init|=
operator|new
name|PermissionValidator
argument_list|(
name|ns
argument_list|,
name|ns
argument_list|,
name|mock
argument_list|(
name|PermissionProvider
operator|.
name|class
argument_list|)
argument_list|,
name|pvp
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|VERSION_STORE_PATH
argument_list|)
control|)
block|{
name|t
operator|=
name|t
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|ns
operator|=
name|ns
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|validator
operator|=
operator|new
name|PermissionValidator
argument_list|(
name|t
argument_list|,
name|t
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|,
name|validator
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|t
argument_list|,
literal|"any"
argument_list|,
name|REP_VERSIONSTORAGE
argument_list|)
expr_stmt|;
name|validator
operator|.
name|childNodeDeleted
argument_list|(
literal|"any"
argument_list|,
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|22
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testAddVersionStorageTreeWithoutHistory
parameter_list|()
throws|throws
name|Exception
block|{
name|PermissionValidator
name|validator
init|=
name|createValidator
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|VERSION_STORE_PATH
argument_list|)
decl_stmt|;
try|try
block|{
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
name|VERSION_STORE_PATH
argument_list|)
decl_stmt|;
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|t
argument_list|,
literal|"any"
argument_list|,
name|REP_VERSIONSTORAGE
argument_list|)
expr_stmt|;
name|validator
operator|.
name|childNodeAdded
argument_list|(
literal|"any"
argument_list|,
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|21
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testAddVersionStorageTreeUnexpectedNode
parameter_list|()
throws|throws
name|Exception
block|{
name|PermissionValidator
name|validator
init|=
name|createValidator
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|VERSION_STORE_PATH
argument_list|)
decl_stmt|;
try|try
block|{
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
name|VERSION_STORE_PATH
argument_list|)
decl_stmt|;
name|Tree
name|storageT
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|t
argument_list|,
literal|"any"
argument_list|,
name|REP_VERSIONSTORAGE
argument_list|)
decl_stmt|;
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|storageT
argument_list|,
literal|"unexpected"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|validator
operator|.
name|childNodeAdded
argument_list|(
literal|"any"
argument_list|,
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isOfType
argument_list|(
literal|"Misc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testChangePrimaryTypeToPolicyNode
parameter_list|()
throws|throws
name|Exception
block|{
comment|// grant the test session the ability to read/write at test node but don't
comment|// allow to modify access control content
name|grant
argument_list|(
name|TEST_ROOT_PATH
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_WRITE
argument_list|)
expr_stmt|;
comment|// create a rep:policy node that is not detected as access control content
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|TEST_CHILD_PATH
argument_list|)
argument_list|,
name|AccessControlConstants
operator|.
name|REP_POLICY
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
init|(
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
init|)
block|{
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|testChild
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
name|TEST_CHILD_PATH
argument_list|)
decl_stmt|;
name|testChild
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|AccessControlConstants
operator|.
name|MIX_REP_ACCESS_CONTROLLABLE
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|testPolicy
init|=
name|testChild
operator|.
name|getChild
argument_list|(
name|AccessControlConstants
operator|.
name|REP_POLICY
argument_list|)
decl_stmt|;
name|testPolicy
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|testPolicy
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|AccessControlConstants
operator|.
name|NT_REP_ACL
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddImmutablePropertyWithDeclaringMixin
parameter_list|()
throws|throws
name|Exception
block|{
comment|// grant the test session the ability to read/write at test node but don't
comment|// allow to modify access control content
name|grant
argument_list|(
name|TEST_ROOT_PATH
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
try|try
init|(
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
init|)
block|{
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|testTree
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
name|TEST_ROOT_PATH
argument_list|)
decl_stmt|;
name|TreeUtil
operator|.
name|addMixin
argument_list|(
name|testTree
argument_list|,
name|MIX_CREATED
argument_list|,
name|root
operator|.
name|getTree
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
argument_list|)
argument_list|,
literal|"uid"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testTree
operator|.
name|hasProperty
argument_list|(
name|JCR_CREATEDBY
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testTree
operator|.
name|hasProperty
argument_list|(
name|JCR_CREATED
argument_list|)
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testAddImmutablePropertyWithoutDeclaringMixin
parameter_list|()
throws|throws
name|Exception
block|{
comment|// grant the test session the ability to read/write at test node but don't
comment|// allow to modify access control content
name|grant
argument_list|(
name|TEST_ROOT_PATH
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
try|try
init|(
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
init|)
block|{
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
comment|// adding jcr:created and jcr:createdBy without mix:created present will trigger regular permission eval
comment|// as without mixin they are not considered immutable properties
name|Tree
name|testTree
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
name|TEST_ROOT_PATH
argument_list|)
decl_stmt|;
name|testTree
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|JCR_CREATED
argument_list|,
literal|"mixCreatedIsMissing"
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
argument_list|)
expr_stmt|;
name|testTree
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|JCR_CREATEDBY
argument_list|,
literal|"mixCreatedIsMissing"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
end_class

end_unit

