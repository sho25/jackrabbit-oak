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
name|authorization
operator|.
name|principalbased
operator|.
name|impl
package|;
end_package

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
name|tree
operator|.
name|TreeType
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
name|java
operator|.
name|security
operator|.
name|Principal
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
name|authorization
operator|.
name|principalbased
operator|.
name|impl
operator|.
name|Constants
operator|.
name|REP_PRINCIPAL_POLICY
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
name|assertNotEquals
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

begin_class
specifier|public
class|class
name|RegularTreePermissionTest
extends|extends
name|AbstractPrincipalBasedTest
block|{
specifier|private
name|PrincipalBasedPermissionProvider
name|permissionProvider
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
name|Principal
name|principal
init|=
name|getTestSystemUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|setupContentTrees
argument_list|(
name|TEST_OAK_PATH
argument_list|)
expr_stmt|;
name|setupPrincipalBasedAccessControl
argument_list|(
name|principal
argument_list|,
name|testContentJcrPath
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|permissionProvider
operator|=
name|createPermissionProvider
argument_list|(
name|root
argument_list|,
name|principal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|NamePathMapper
name|getNamePathMapper
parameter_list|()
block|{
return|return
name|NamePathMapper
operator|.
name|DEFAULT
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTreePermissionRootTree
parameter_list|()
block|{
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
name|TreePermission
name|tp
init|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|rootTree
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|instanceof
name|AbstractTreePermission
argument_list|)
expr_stmt|;
name|AbstractTreePermission
name|atp
init|=
operator|(
name|AbstractTreePermission
operator|)
name|tp
decl_stmt|;
name|assertNotEquals
argument_list|(
name|rootTree
argument_list|,
name|atp
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|atp
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTreePermissionReadOnlyRootTree
parameter_list|()
block|{
name|Tree
name|rootTree
init|=
name|getRootProvider
argument_list|()
operator|.
name|createReadOnlyRoot
argument_list|(
name|root
argument_list|)
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
decl_stmt|;
name|TreePermission
name|tp
init|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|rootTree
argument_list|,
name|TreeType
operator|.
name|VERSION
argument_list|,
name|mock
argument_list|(
name|TreePermission
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|instanceof
name|AbstractTreePermission
argument_list|)
expr_stmt|;
name|AbstractTreePermission
name|atp
init|=
operator|(
name|AbstractTreePermission
operator|)
name|tp
decl_stmt|;
name|assertSame
argument_list|(
name|rootTree
argument_list|,
name|atp
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|atp
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGrantedForRootTree
parameter_list|()
throws|throws
name|Exception
block|{
name|TreePermission
name|tp
init|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|mock
argument_list|(
name|TreePermission
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCanReadForRootTree
parameter_list|()
throws|throws
name|Exception
block|{
name|TreePermission
name|tp
init|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|mock
argument_list|(
name|TreePermission
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|canRead
argument_list|(
name|MockUtility
operator|.
name|createPrimaryTypeProperty
argument_list|(
name|NodeTypeConstants
operator|.
name|NT_REP_ROOT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRefreshReflectedOnTreePermission
parameter_list|()
throws|throws
name|Exception
block|{
name|TreePermission
name|tp
init|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|setupPrincipalBasedAccessControl
argument_list|(
name|getTestSystemUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|permissionProvider
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|canRead
argument_list|(
name|MockUtility
operator|.
name|createPrimaryTypeProperty
argument_list|(
name|NodeTypeConstants
operator|.
name|NT_REP_ROOT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTreePermissionMockedParentPermission
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|tree
init|=
name|getRootProvider
argument_list|()
operator|.
name|createReadOnlyRoot
argument_list|(
name|root
argument_list|)
operator|.
name|getTree
argument_list|(
name|getNamePathMapper
argument_list|()
operator|.
name|getOakPath
argument_list|(
name|getTestSystemUser
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|getChild
argument_list|(
name|REP_PRINCIPAL_POLICY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|TreePermission
name|tp
init|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|mock
argument_list|(
name|TreePermission
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|instanceof
name|AbstractTreePermission
argument_list|)
expr_stmt|;
name|AbstractTreePermission
name|atp
init|=
operator|(
name|AbstractTreePermission
operator|)
name|tp
decl_stmt|;
name|assertSame
argument_list|(
name|tree
argument_list|,
name|atp
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TreeType
operator|.
name|ACCESS_CONTROL
argument_list|,
name|atp
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTreePermissionNonExistingTree
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|tree
init|=
name|getRootProvider
argument_list|()
operator|.
name|createReadOnlyRoot
argument_list|(
name|root
argument_list|)
operator|.
name|getTree
argument_list|(
literal|"/nonExisting"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tree
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|TreePermission
name|tp
init|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|instanceof
name|AbstractTreePermission
argument_list|)
expr_stmt|;
name|AbstractTreePermission
name|atp
init|=
operator|(
name|AbstractTreePermission
operator|)
name|tp
decl_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGrantedForTestTree
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|tree
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
name|TreePermission
name|tp
init|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|elem
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|TEST_OAK_PATH
argument_list|)
control|)
block|{
name|tree
operator|=
name|tree
operator|.
name|getChild
argument_list|(
name|elem
argument_list|)
expr_stmt|;
name|tp
operator|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|tp
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|READ
argument_list|,
name|tree
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|WRITE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCanReadForTestTree
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|tree
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
name|TreePermission
name|tp
init|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|elem
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|TEST_OAK_PATH
argument_list|)
control|)
block|{
name|tree
operator|=
name|tree
operator|.
name|getChild
argument_list|(
name|elem
argument_list|)
expr_stmt|;
name|tp
operator|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|tp
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|canRead
argument_list|(
name|tree
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCanReadForTypeAccessControl
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|prinipalPath
init|=
name|getNamePathMapper
argument_list|()
operator|.
name|getOakPath
argument_list|(
name|getTestSystemUser
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|policyPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|prinipalPath
argument_list|,
name|REP_PRINCIPAL_POLICY
argument_list|)
decl_stmt|;
name|Tree
name|tree
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
name|TreePermission
name|tp
init|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|elem
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|policyPath
argument_list|)
control|)
block|{
name|tree
operator|=
name|tree
operator|.
name|getChild
argument_list|(
name|elem
argument_list|)
expr_stmt|;
name|tp
operator|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|tp
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|setupPrincipalBasedAccessControl
argument_list|(
name|getTestSystemUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|getTestSystemUser
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|permissionProvider
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetChildTreePermission
parameter_list|()
block|{
name|Tree
name|readOnly
init|=
name|getRootProvider
argument_list|()
operator|.
name|createReadOnlyRoot
argument_list|(
name|root
argument_list|)
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
decl_stmt|;
name|TreePermission
name|tp
init|=
operator|(
name|AbstractTreePermission
operator|)
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|readOnly
argument_list|,
name|TreePermission
operator|.
name|EMPTY
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
name|readOnly
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|elem
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|TEST_OAK_PATH
argument_list|)
control|)
block|{
name|ns
operator|=
name|ns
operator|.
name|getChildNode
argument_list|(
name|elem
argument_list|)
expr_stmt|;
name|tp
operator|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|elem
argument_list|,
name|ns
argument_list|,
operator|(
name|AbstractTreePermission
operator|)
name|tp
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|instanceof
name|AbstractTreePermission
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|TreeType
operator|.
name|DEFAULT
argument_list|,
operator|(
operator|(
name|AbstractTreePermission
operator|)
name|tp
operator|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

