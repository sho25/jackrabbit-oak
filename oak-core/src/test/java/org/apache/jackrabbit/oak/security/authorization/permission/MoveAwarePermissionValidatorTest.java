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
name|commit
operator|.
name|Validator
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
name|VisibleValidator
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
name|ConfigurationParameters
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
name|Context
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
name|Permissions
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
name|restriction
operator|.
name|RestrictionProvider
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
name|never
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
name|times
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
name|verify
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
name|MoveAwarePermissionValidatorTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|Tree
name|t
decl_stmt|;
specifier|private
name|PermissionProvider
name|pp
decl_stmt|;
specifier|private
name|JackrabbitAccessControlList
name|acl
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
name|Tree
name|rootTree
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
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|rootTree
argument_list|,
literal|"src"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|rootTree
argument_list|,
literal|"dest"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
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
try|try
block|{
if|if
condition|(
name|acl
operator|!=
literal|null
condition|)
block|{
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
operator|.
name|removePolicy
argument_list|(
name|acl
operator|.
name|getPath
argument_list|()
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
name|Tree
name|src
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/src"
argument_list|)
decl_stmt|;
if|if
condition|(
name|src
operator|.
name|exists
argument_list|()
condition|)
block|{
name|src
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|Tree
name|dest
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/dest"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|.
name|exists
argument_list|()
condition|)
block|{
name|dest
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
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
name|NotNull
name|String
name|path
parameter_list|,
annotation|@
name|NotNull
name|Principal
name|principal
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
name|principal
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
name|this
operator|.
name|acl
operator|=
name|acl
expr_stmt|;
block|}
annotation|@
name|NotNull
specifier|private
name|MoveAwarePermissionValidator
name|createRootValidator
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
name|MoveTracker
name|tracker
parameter_list|)
block|{
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
name|when
argument_list|(
name|ctx
operator|.
name|getRootProvider
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|getRootProvider
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
name|Root
name|readonlyRoot
init|=
name|getRootProvider
argument_list|()
operator|.
name|createReadOnlyRoot
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|t
operator|=
name|readonlyRoot
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
expr_stmt|;
name|pp
operator|=
name|spy
argument_list|(
operator|new
name|PermissionProviderImpl
argument_list|(
name|readonlyRoot
argument_list|,
name|wspName
argument_list|,
name|principals
argument_list|,
name|RestrictionProvider
operator|.
name|EMPTY
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
name|Context
operator|.
name|DEFAULT
argument_list|,
name|ctx
argument_list|)
argument_list|)
expr_stmt|;
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
name|tracker
argument_list|,
name|ctx
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
return|return
operator|new
name|MoveAwarePermissionValidator
argument_list|(
name|ns
argument_list|,
name|ns
argument_list|,
name|pp
argument_list|,
name|pvp
argument_list|,
name|tracker
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChildNodeAddedNoMatchingMove
parameter_list|()
throws|throws
name|Exception
block|{
name|MoveAwarePermissionValidator
name|maValidator
init|=
name|spy
argument_list|(
name|createRootValidator
argument_list|(
name|adminSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
operator|new
name|MoveTracker
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Validator
name|validator
init|=
name|maValidator
operator|.
name|childNodeAdded
argument_list|(
literal|"name"
argument_list|,
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|validator
operator|instanceof
name|VisibleValidator
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|maValidator
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|checkPermissions
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"name"
argument_list|)
argument_list|,
literal|false
argument_list|,
name|Permissions
operator|.
name|ADD_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChildNodeAddedNonExistingSrc
parameter_list|()
throws|throws
name|Exception
block|{
name|MoveTracker
name|moveTracker
init|=
operator|new
name|MoveTracker
argument_list|()
decl_stmt|;
name|moveTracker
operator|.
name|addMove
argument_list|(
literal|"/srcNonExisting"
argument_list|,
literal|"/dest"
argument_list|)
expr_stmt|;
name|MoveAwarePermissionValidator
name|maValidator
init|=
name|spy
argument_list|(
name|createRootValidator
argument_list|(
name|adminSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
name|moveTracker
argument_list|)
argument_list|)
decl_stmt|;
name|Validator
name|validator
init|=
name|maValidator
operator|.
name|childNodeAdded
argument_list|(
literal|"dest"
argument_list|,
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|validator
operator|instanceof
name|VisibleValidator
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|maValidator
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|checkPermissions
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"dest"
argument_list|)
argument_list|,
literal|false
argument_list|,
name|Permissions
operator|.
name|ADD_NODE
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|pp
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|isGranted
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"src"
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChildNodeAddedExistingSrc
parameter_list|()
throws|throws
name|Exception
block|{
name|MoveTracker
name|moveTracker
init|=
operator|new
name|MoveTracker
argument_list|()
decl_stmt|;
name|moveTracker
operator|.
name|addMove
argument_list|(
literal|"/src"
argument_list|,
literal|"/dest"
argument_list|)
expr_stmt|;
name|MoveAwarePermissionValidator
name|maValidator
init|=
name|spy
argument_list|(
name|createRootValidator
argument_list|(
name|adminSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
name|moveTracker
argument_list|)
argument_list|)
decl_stmt|;
name|Validator
name|validator
init|=
name|maValidator
operator|.
name|childNodeAdded
argument_list|(
literal|"dest"
argument_list|,
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|validator
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|maValidator
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|checkPermissions
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"dest"
argument_list|)
argument_list|,
literal|false
argument_list|,
name|Permissions
operator|.
name|ADD_NODE
operator||
name|Permissions
operator|.
name|NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|pp
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|isGranted
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"src"
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChildNodeAddedNullPraent
parameter_list|()
throws|throws
name|Exception
block|{
name|MoveTracker
name|moveTracker
init|=
operator|new
name|MoveTracker
argument_list|()
decl_stmt|;
name|moveTracker
operator|.
name|addMove
argument_list|(
literal|"/src"
argument_list|,
literal|"/dest"
argument_list|)
expr_stmt|;
name|MoveAwarePermissionValidator
name|maValidator
init|=
name|spy
argument_list|(
name|createRootValidator
argument_list|(
name|adminSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
name|moveTracker
argument_list|)
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|maValidator
operator|.
name|getParentAfter
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Validator
name|validator
init|=
name|maValidator
operator|.
name|childNodeAdded
argument_list|(
literal|"dest"
argument_list|,
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|validator
operator|instanceof
name|VisibleValidator
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|maValidator
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|checkPermissions
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"dest"
argument_list|)
argument_list|,
literal|false
argument_list|,
name|Permissions
operator|.
name|ADD_NODE
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|pp
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|isGranted
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"src"
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
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
name|testChildNodeAddedMissingPermissionAtSrc
parameter_list|()
throws|throws
name|Exception
block|{
name|grant
argument_list|(
literal|"/"
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
name|MoveTracker
name|moveTracker
init|=
operator|new
name|MoveTracker
argument_list|()
decl_stmt|;
name|moveTracker
operator|.
name|addMove
argument_list|(
literal|"/src"
argument_list|,
literal|"/dest"
argument_list|)
expr_stmt|;
name|MoveAwarePermissionValidator
name|maValidator
init|=
name|spy
argument_list|(
name|createRootValidator
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|,
name|moveTracker
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|maValidator
operator|.
name|childNodeAdded
argument_list|(
literal|"dest"
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
name|verify
argument_list|(
name|maValidator
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|checkPermissions
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"dest"
argument_list|)
argument_list|,
literal|false
argument_list|,
name|Permissions
operator|.
name|ADD_NODE
operator||
name|Permissions
operator|.
name|NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|pp
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|isGranted
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"src"
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
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
name|testChildNodeDeletedNoMatchingMove
parameter_list|()
throws|throws
name|Exception
block|{
name|MoveAwarePermissionValidator
name|maValidator
init|=
name|spy
argument_list|(
name|createRootValidator
argument_list|(
name|adminSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
operator|new
name|MoveTracker
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Validator
name|validator
init|=
name|maValidator
operator|.
name|childNodeDeleted
argument_list|(
literal|"name"
argument_list|,
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|validator
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|maValidator
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|checkPermissions
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"name"
argument_list|)
argument_list|,
literal|true
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChildNodeDeletedNonExistingDestination
parameter_list|()
throws|throws
name|Exception
block|{
name|MoveTracker
name|moveTracker
init|=
operator|new
name|MoveTracker
argument_list|()
decl_stmt|;
name|moveTracker
operator|.
name|addMove
argument_list|(
literal|"/src"
argument_list|,
literal|"/nonExistingDest"
argument_list|)
expr_stmt|;
name|MoveAwarePermissionValidator
name|maValidator
init|=
name|spy
argument_list|(
name|createRootValidator
argument_list|(
name|adminSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
name|moveTracker
argument_list|)
argument_list|)
decl_stmt|;
name|Validator
name|validator
init|=
name|maValidator
operator|.
name|childNodeDeleted
argument_list|(
literal|"src"
argument_list|,
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|validator
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|maValidator
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|checkPermissions
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"src"
argument_list|)
argument_list|,
literal|true
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|pp
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|isGranted
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"nonExistingDest"
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|ADD_NODE
operator||
name|Permissions
operator|.
name|NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChildNodeDeletedExistingDestination
parameter_list|()
throws|throws
name|Exception
block|{
name|MoveTracker
name|moveTracker
init|=
operator|new
name|MoveTracker
argument_list|()
decl_stmt|;
name|moveTracker
operator|.
name|addMove
argument_list|(
literal|"/src"
argument_list|,
literal|"/dest"
argument_list|)
expr_stmt|;
name|MoveAwarePermissionValidator
name|maValidator
init|=
name|spy
argument_list|(
name|createRootValidator
argument_list|(
name|adminSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
name|moveTracker
argument_list|)
argument_list|)
decl_stmt|;
name|Validator
name|validator
init|=
name|maValidator
operator|.
name|childNodeDeleted
argument_list|(
literal|"src"
argument_list|,
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|validator
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|maValidator
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|checkPermissions
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"src"
argument_list|)
argument_list|,
literal|true
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|pp
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|isGranted
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"dest"
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|ADD_NODE
operator||
name|Permissions
operator|.
name|NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChildNodeDeletedNullParent
parameter_list|()
throws|throws
name|Exception
block|{
name|MoveTracker
name|moveTracker
init|=
operator|new
name|MoveTracker
argument_list|()
decl_stmt|;
name|moveTracker
operator|.
name|addMove
argument_list|(
literal|"/src"
argument_list|,
literal|"/dest"
argument_list|)
expr_stmt|;
name|MoveAwarePermissionValidator
name|maValidator
init|=
name|spy
argument_list|(
name|createRootValidator
argument_list|(
name|adminSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
name|moveTracker
argument_list|)
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|maValidator
operator|.
name|getParentBefore
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Validator
name|validator
init|=
name|maValidator
operator|.
name|childNodeDeleted
argument_list|(
literal|"src"
argument_list|,
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|validator
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|maValidator
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|checkPermissions
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"src"
argument_list|)
argument_list|,
literal|true
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|pp
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|isGranted
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"dest"
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|ADD_NODE
operator||
name|Permissions
operator|.
name|NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
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
name|testChildNodeDeletedMissingPermissionAtDestination
parameter_list|()
throws|throws
name|Exception
block|{
name|grant
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_CHILD_NODES
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
expr_stmt|;
name|MoveTracker
name|moveTracker
init|=
operator|new
name|MoveTracker
argument_list|()
decl_stmt|;
name|moveTracker
operator|.
name|addMove
argument_list|(
literal|"/src"
argument_list|,
literal|"/dest"
argument_list|)
expr_stmt|;
name|MoveAwarePermissionValidator
name|maValidator
init|=
name|spy
argument_list|(
name|createRootValidator
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|,
name|moveTracker
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|maValidator
operator|.
name|childNodeDeleted
argument_list|(
literal|"src"
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
name|verify
argument_list|(
name|maValidator
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|checkPermissions
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"src"
argument_list|)
argument_list|,
literal|true
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|pp
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|isGranted
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"dest"
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|ADD_NODE
operator||
name|Permissions
operator|.
name|NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
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

