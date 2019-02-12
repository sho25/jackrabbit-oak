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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|Collection
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
name|permission
operator|.
name|PermissionConstants
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
name|PrivilegeBits
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
name|util
operator|.
name|NodeUtil
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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
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
name|PermissionStoreImplTest
extends|extends
name|AbstractSecurityTest
implements|implements
name|PermissionConstants
block|{
specifier|private
name|PermissionStoreImpl
name|permissionStore
decl_stmt|;
specifier|private
name|Principal
name|testPrincipal
decl_stmt|;
specifier|private
name|String
name|testPath
init|=
literal|"/testPath"
decl_stmt|;
specifier|private
name|String
name|childPath
init|=
literal|"/testPath/childNode"
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
name|testPrincipal
operator|=
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
expr_stmt|;
name|NodeUtil
name|rootNode
init|=
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|namePathMapper
argument_list|)
decl_stmt|;
name|NodeUtil
name|testNode
init|=
name|rootNode
operator|.
name|addChild
argument_list|(
literal|"testPath"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|testNode
operator|.
name|addChild
argument_list|(
literal|"childNode"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|addAcl
argument_list|(
name|testPath
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|addAcl
argument_list|(
name|childPath
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|permissionStore
operator|=
operator|new
name|PermissionStoreImpl
argument_list|(
name|root
argument_list|,
name|root
operator|.
name|getContentSession
argument_list|()
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|getConfig
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getRestrictionProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addAcl
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
parameter_list|)
throws|throws
name|RepositoryException
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
name|addAccessControlEntry
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
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
name|testPath
argument_list|)
decl_stmt|;
name|acMgr
operator|.
name|removePolicy
argument_list|(
name|testPath
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
name|Test
specifier|public
name|void
name|testLoad
parameter_list|()
block|{
name|PrincipalPermissionEntries
name|entries
init|=
name|permissionStore
operator|.
name|load
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|entries
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|entries
operator|.
name|isFullyLoaded
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|entries
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoadMissingPrincipalRoot
parameter_list|()
block|{
name|PrincipalPermissionEntries
name|entries
init|=
name|permissionStore
operator|.
name|load
argument_list|(
name|testPrincipal
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|entries
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|entries
operator|.
name|isFullyLoaded
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|entries
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoadWithNesting
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Tree
name|everyoneTree
init|=
name|getPermissionRoot
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|everyoneTree
operator|.
name|removeProperty
argument_list|(
name|REP_NUM_PERMISSIONS
argument_list|)
expr_stmt|;
for|for
control|(
name|Tree
name|child
range|:
name|everyoneTree
operator|.
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|child
operator|.
name|hasProperty
argument_list|(
name|REP_ACCESS_CONTROLLED_PATH
argument_list|)
condition|)
block|{
name|String
name|name
init|=
name|child
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Tree
name|collision
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|child
argument_list|,
literal|"c_"
operator|+
name|child
operator|.
name|getName
argument_list|()
argument_list|,
name|NT_REP_PERMISSION_STORE
argument_list|)
decl_stmt|;
name|collision
operator|.
name|setProperty
argument_list|(
name|REP_ACCESS_CONTROLLED_PATH
argument_list|,
literal|"/another/path"
argument_list|)
expr_stmt|;
name|Tree
name|entry
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|collision
argument_list|,
literal|"1"
argument_list|,
name|NT_REP_PERMISSIONS
argument_list|)
decl_stmt|;
name|entry
operator|.
name|setProperty
argument_list|(
name|PrivilegeBits
operator|.
name|BUILT_IN
operator|.
name|get
argument_list|(
name|REP_READ_NODES
argument_list|)
operator|.
name|asPropertyState
argument_list|(
name|REP_PRIVILEGE_BITS
argument_list|)
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setProperty
argument_list|(
name|REP_IS_ALLOW
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|PrincipalPermissionEntries
name|entries
init|=
name|permissionStore
operator|.
name|load
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|entries
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|entries
operator|.
name|isFullyLoaded
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|entries
operator|.
name|getSize
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
name|testLoadByPath
parameter_list|()
block|{
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|entries
init|=
name|permissionStore
operator|.
name|load
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|testPath
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|entries
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|entries
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
name|testLoadByPathWithoutEntries
parameter_list|()
block|{
name|assertNull
argument_list|(
name|permissionStore
operator|.
name|load
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|testPath
operator|+
literal|"/notAccessControlled"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoadByPathMissingPrincipalRoot
parameter_list|()
block|{
name|assertNull
argument_list|(
name|permissionStore
operator|.
name|load
argument_list|(
name|testPrincipal
operator|.
name|getName
argument_list|()
argument_list|,
name|testPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetNumEntries
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|NumEntries
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|,
literal|true
argument_list|)
argument_list|,
name|permissionStore
operator|.
name|getNumEntries
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetNumEntriesMissingPrincipalRoot
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|NumEntries
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
argument_list|,
name|permissionStore
operator|.
name|getNumEntries
argument_list|(
name|testPrincipal
operator|.
name|getName
argument_list|()
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetNumEntriesMissingProperty
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Tree
name|everyoneTree
init|=
name|getPermissionRoot
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|everyoneTree
operator|.
name|removeProperty
argument_list|(
name|REP_NUM_PERMISSIONS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NumEntries
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|,
literal|false
argument_list|)
argument_list|,
name|permissionStore
operator|.
name|getNumEntries
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
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
name|testGetNumEntriesMissingPropertyThreshold
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Tree
name|everyoneTree
init|=
name|getPermissionRoot
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|everyoneTree
operator|.
name|removeProperty
argument_list|(
name|REP_NUM_PERMISSIONS
argument_list|)
expr_stmt|;
name|long
name|max
init|=
literal|1
decl_stmt|;
name|assertEquals
argument_list|(
name|NumEntries
operator|.
name|valueOf
argument_list|(
name|everyoneTree
operator|.
name|getChildrenCount
argument_list|(
name|max
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|,
name|permissionStore
operator|.
name|getNumEntries
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|max
argument_list|)
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
name|Nullable
specifier|private
name|Tree
name|getPermissionRoot
parameter_list|(
annotation|@
name|NotNull
name|String
name|principalName
parameter_list|)
throws|throws
name|Exception
block|{
name|Method
name|m
init|=
name|PermissionStoreImpl
operator|.
name|class
operator|.
name|getDeclaredMethod
argument_list|(
literal|"getPrincipalRoot"
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|m
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|(
name|Tree
operator|)
name|m
operator|.
name|invoke
argument_list|(
name|permissionStore
argument_list|,
name|principalName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

