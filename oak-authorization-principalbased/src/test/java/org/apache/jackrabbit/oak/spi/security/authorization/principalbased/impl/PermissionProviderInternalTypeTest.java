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
name|principal
operator|.
name|ItemBasedPrincipal
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
name|PropertyState
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
name|MockUtility
operator|.
name|mockReadOnlyTree
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
name|PermissionProviderInternalTypeTest
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
name|permissionProvider
operator|=
name|createPermissionProvider
argument_list|(
name|root
argument_list|,
name|getTestSystemUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
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
name|testGetPrivileges
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|getPrivileges
argument_list|(
name|mockReadOnlyTree
argument_list|(
name|TreeType
operator|.
name|INTERNAL
argument_list|)
argument_list|)
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
name|testHasPrivileges
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|hasPrivileges
argument_list|(
name|mockReadOnlyTree
argument_list|(
name|TreeType
operator|.
name|INTERNAL
argument_list|)
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGranted
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|mockReadOnlyTree
argument_list|(
name|TreeType
operator|.
name|INTERNAL
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|mockReadOnlyTree
argument_list|(
name|TreeType
operator|.
name|INTERNAL
argument_list|)
argument_list|,
name|mock
argument_list|(
name|PropertyState
operator|.
name|class
argument_list|)
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTreePermission
parameter_list|()
throws|throws
name|Exception
block|{
name|assertSame
argument_list|(
name|TreePermission
operator|.
name|EMPTY
argument_list|,
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|mockReadOnlyTree
argument_list|(
name|TreeType
operator|.
name|INTERNAL
argument_list|)
argument_list|,
name|TreeType
operator|.
name|INTERNAL
argument_list|,
name|mock
argument_list|(
name|AbstractTreePermission
operator|.
name|class
argument_list|)
argument_list|)
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
name|PermissionConstants
operator|.
name|PERMISSIONS_STORE_PATH
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
block|}
name|assertSame
argument_list|(
name|TreePermission
operator|.
name|EMPTY
argument_list|,
name|tp
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

