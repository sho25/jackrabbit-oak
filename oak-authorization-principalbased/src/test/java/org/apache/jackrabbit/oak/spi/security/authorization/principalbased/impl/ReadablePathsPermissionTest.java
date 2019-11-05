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
name|Iterables
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
name|Iterators
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
name|TreeLocation
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
name|JCR_SYSTEM
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
name|commons
operator|.
name|PathUtils
operator|.
name|ROOT_PATH
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
name|JCR_READ
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
name|REP_READ_PROPERTIES
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|ReadablePathsPermissionTest
extends|extends
name|AbstractPrincipalBasedTest
block|{
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|readablePaths
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|readableChildPaths
decl_stmt|;
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
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|getConfig
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|PermissionConstants
operator|.
name|PARAM_READ_PATHS
argument_list|,
name|PermissionConstants
operator|.
name|DEFAULT_READ_PATHS
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|paths
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|readablePaths
operator|=
name|Iterators
operator|.
name|cycle
argument_list|(
name|paths
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|childPaths
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|Iterables
operator|.
name|addAll
argument_list|(
name|childPaths
argument_list|,
name|Iterables
operator|.
name|transform
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
operator|.
name|getChildren
argument_list|()
argument_list|,
name|tree
lambda|->
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|readableChildPaths
operator|=
name|Iterators
operator|.
name|cycle
argument_list|(
name|childPaths
argument_list|)
expr_stmt|;
name|permissionProvider
operator|=
operator|new
name|PrincipalBasedPermissionProvider
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
name|Collections
operator|.
name|singleton
argument_list|(
name|getTestSystemUser
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
name|getPrincipalBasedAuthorizationConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|AuthorizationConfiguration
operator|.
name|NAME
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|PermissionConstants
operator|.
name|PARAM_READ_PATHS
argument_list|,
operator|new
name|String
index|[]
block|{
name|NamespaceConstants
operator|.
name|NAMESPACES_PATH
block|,
name|PrivilegeConstants
operator|.
name|PRIVILEGES_PATH
block|}
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|private
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
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasPrivileges
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|hasPrivileges
argument_list|(
name|getTree
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|hasPrivileges
argument_list|(
name|getTree
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|hasPrivileges
argument_list|(
name|getTree
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|REP_READ_NODES
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|hasPrivileges
argument_list|(
name|getTree
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|REP_READ_NODES
argument_list|,
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotHasPrivileges
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|hasPrivileges
argument_list|(
name|getTree
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|hasPrivileges
argument_list|(
name|getTree
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_WRITE
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|hasPrivileges
argument_list|(
name|getTree
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_MODIFY_ACCESS_CONTROL
argument_list|,
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|hasPrivileges
argument_list|(
name|getTree
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|REP_READ_NODES
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|hasPrivileges
argument_list|(
name|getTree
argument_list|(
name|ROOT_PATH
argument_list|)
argument_list|,
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|systemPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|ROOT_PATH
argument_list|,
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|hasPrivileges
argument_list|(
name|getTree
argument_list|(
name|systemPath
argument_list|)
argument_list|,
name|REP_READ_NODES
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivileges
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|JCR_READ
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|permissionProvider
operator|.
name|getPrivileges
argument_list|(
name|getTree
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|permissionProvider
operator|.
name|getPrivileges
argument_list|(
name|getTree
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|getPrivileges
argument_list|(
name|getTree
argument_list|(
name|ROOT_PATH
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|systemPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|ROOT_PATH
argument_list|,
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|getPrivileges
argument_list|(
name|getTree
argument_list|(
name|systemPath
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
name|testIsGrantedPath
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|READ_NODE
operator||
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|READ_NODE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|,
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|,
literal|"nonExisting"
argument_list|)
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotIsGrantedPath
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|READ
operator||
name|Permissions
operator|.
name|VERSION_MANAGEMENT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
operator||
name|Permissions
operator|.
name|READ_NODE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|READ_PROPERTY
operator||
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|ROOT_PATH
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|ROOT_PATH
argument_list|,
name|JCR_SYSTEM
argument_list|)
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|READ_NODE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
literal|"/nonExistingContent"
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGrantedTree
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|getTree
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|getTree
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ_NODE
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|t
init|=
name|getTree
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
name|t
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|,
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|getTree
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|getTree
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ_NODE
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
name|getTree
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
name|t
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|,
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotIsGrantedTree
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|getTree
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
operator||
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|getTree
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|ADD_NODE
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|t
init|=
name|getTree
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
name|t
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|,
name|Permissions
operator|.
name|MODIFY_PROPERTY
operator||
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
name|t
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|,
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|t
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ_NODE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGrantedLocation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|Permissions
operator|.
name|READ_NODE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|getChild
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|,
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotIsGrantedLocation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|Permissions
operator|.
name|READ
operator||
name|Permissions
operator|.
name|WRITE
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|)
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
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|getChild
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|,
name|Permissions
operator|.
name|READ_PROPERTY
operator||
name|Permissions
operator|.
name|MODIFY_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
name|TreeLocation
name|location
init|=
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|location
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|location
operator|.
name|getChild
argument_list|(
name|JCR_SYSTEM
argument_list|)
argument_list|,
name|Permissions
operator|.
name|READ_NODE
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|location
operator|.
name|getChild
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|,
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTreePermission
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|t
init|=
name|getTree
argument_list|(
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
name|t
argument_list|,
name|TreePermission
operator|.
name|EMPTY
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
name|t
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tp
operator|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
name|JCR_SYSTEM
argument_list|)
argument_list|,
name|tp
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
name|READ_NODE
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|canReadProperties
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|canReadAll
argument_list|()
argument_list|)
expr_stmt|;
comment|// readable paths
name|t
operator|=
name|root
operator|.
name|getTree
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|tp
operator|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|t
argument_list|,
name|tp
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
name|t
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|canReadProperties
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
name|t
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|tp
operator|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|t
argument_list|,
name|tp
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
name|READ_NODE
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
name|READ_PROPERTY
argument_list|,
name|t
operator|.
name|getProperty
argument_list|(
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
name|READ_PROPERTY
operator||
name|Permissions
operator|.
name|MODIFY_PROPERTY
argument_list|,
name|t
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
argument_list|)
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
name|canReadAll
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRepositoryPermission
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|permissionProvider
operator|.
name|getRepositoryPermission
argument_list|()
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
block|}
end_class

end_unit
