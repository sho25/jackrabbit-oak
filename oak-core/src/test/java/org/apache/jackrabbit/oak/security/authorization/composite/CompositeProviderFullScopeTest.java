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
name|composite
package|;
end_package

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
name|javax
operator|.
name|jcr
operator|.
name|Session
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
name|JackrabbitSession
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
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|AggregatedPermissionProvider
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
name|RepositoryPermission
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
name|PrivilegeBitsProvider
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Test the effect of the combination of  *  * - default permission provider  * - custom provider that grants JCR_NAMESPACE_MANAGEMENT on repository level  *   and REP_READ_NODES only  *  * both for the set of principals associated with the test user and with the admin session.  * The expected outcome is that  * - test user can only read nodes where this is also granted by the default provider  *   but has no other access granted  * - admin user can only read nodes and register namespaces  */
end_comment

begin_class
specifier|public
class|class
name|CompositeProviderFullScopeTest
extends|extends
name|AbstractCompositeProviderTest
block|{
specifier|private
name|CompositePermissionProvider
name|cppTestUser
decl_stmt|;
specifier|private
name|CompositePermissionProvider
name|cppAdminUser
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
name|cppTestUser
operator|=
name|createPermissionProvider
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|cppAdminUser
operator|=
name|createPermissionProvider
argument_list|(
name|root
operator|.
name|getContentSession
argument_list|()
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|AggregatedPermissionProvider
name|getTestPermissionProvider
parameter_list|()
block|{
return|return
operator|new
name|FullScopeProvider
argument_list|(
name|readOnlyRoot
argument_list|)
return|;
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
name|PrivilegeBitsProvider
name|pbp
init|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|readOnlyRoot
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|readNodes
init|=
name|pbp
operator|.
name|getBits
argument_list|(
name|REP_READ_NODES
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|REP_READ_NODES
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|defPrivileges
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|defaultPrivs
init|=
name|defPrivileges
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Tree
name|tree
init|=
name|readOnlyRoot
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|privNames
init|=
name|cppTestUser
operator|.
name|getPrivileges
argument_list|(
name|tree
argument_list|)
decl_stmt|;
if|if
condition|(
name|pbp
operator|.
name|getBits
argument_list|(
name|defaultPrivs
argument_list|)
operator|.
name|includes
argument_list|(
name|readNodes
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|privNames
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|privNames
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegesAdmin
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
name|ImmutableSet
operator|.
name|of
argument_list|(
name|REP_READ_NODES
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|NODE_PATHS
control|)
block|{
name|Tree
name|tree
init|=
name|readOnlyRoot
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|cppAdminUser
operator|.
name|getPrivileges
argument_list|(
name|tree
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegesOnRepo
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
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_NAMESPACE_MANAGEMENT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|cppTestUser
operator|.
name|getPrivileges
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegesOnRepoAdmin
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
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_NAMESPACE_MANAGEMENT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|cppAdminUser
operator|.
name|getPrivileges
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
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
name|PrivilegeBitsProvider
name|pbp
init|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|readOnlyRoot
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|readNodes
init|=
name|pbp
operator|.
name|getBits
argument_list|(
name|REP_READ_NODES
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|defPrivileges
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|defaultPrivs
init|=
name|defPrivileges
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|defaultBits
init|=
name|pbp
operator|.
name|getBits
argument_list|(
name|defaultPrivs
argument_list|)
decl_stmt|;
name|Tree
name|tree
init|=
name|readOnlyRoot
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultPrivs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|assertFalse
argument_list|(
name|path
argument_list|,
name|cppTestUser
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|REP_READ_NODES
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|defaultBits
operator|.
name|includes
argument_list|(
name|readNodes
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|path
argument_list|,
name|cppTestUser
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|REP_READ_NODES
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|readNodes
operator|.
name|equals
argument_list|(
name|defaultBits
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|path
argument_list|,
name|cppTestUser
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|defaultPrivs
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|defaultPrivs
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|path
argument_list|,
name|cppTestUser
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|REP_READ_NODES
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|path
argument_list|,
name|cppTestUser
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|defaultPrivs
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|defaultPrivs
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasPrivilegesAdmin
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|path
range|:
name|NODE_PATHS
control|)
block|{
name|Tree
name|tree
init|=
name|readOnlyRoot
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cppAdminUser
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|REP_READ_NODES
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cppAdminUser
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cppAdminUser
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|JCR_ALL
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cppAdminUser
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|JCR_WRITE
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cppAdminUser
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|REP_READ_NODES
argument_list|,
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cppAdminUser
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|JCR_MODIFY_PROPERTIES
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cppAdminUser
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|JCR_LOCK_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasPrivilegesOnRepo
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|cppTestUser
operator|.
name|hasPrivileges
argument_list|(
literal|null
argument_list|,
name|JCR_NAMESPACE_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cppTestUser
operator|.
name|hasPrivileges
argument_list|(
literal|null
argument_list|,
name|JCR_NAMESPACE_MANAGEMENT
argument_list|,
name|JCR_NODE_TYPE_DEFINITION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cppTestUser
operator|.
name|hasPrivileges
argument_list|(
literal|null
argument_list|,
name|JCR_ALL
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cppTestUser
operator|.
name|hasPrivileges
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasPrivilegeOnRepoAdmin
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|cppAdminUser
operator|.
name|hasPrivileges
argument_list|(
literal|null
argument_list|,
name|JCR_NAMESPACE_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cppAdminUser
operator|.
name|hasPrivileges
argument_list|(
literal|null
argument_list|,
name|JCR_NAMESPACE_MANAGEMENT
argument_list|,
name|JCR_NODE_TYPE_DEFINITION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cppAdminUser
operator|.
name|hasPrivileges
argument_list|(
literal|null
argument_list|,
name|JCR_ALL
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cppAdminUser
operator|.
name|hasPrivileges
argument_list|(
literal|null
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
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|p
range|:
name|defPermissions
operator|.
name|keySet
argument_list|()
control|)
block|{
name|long
name|defaultPerms
init|=
name|defPermissions
operator|.
name|get
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|Tree
name|tree
init|=
name|readOnlyRoot
operator|.
name|getTree
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|Permissions
operator|.
name|READ_NODE
operator|!=
name|defaultPerms
condition|)
block|{
name|assertFalse
argument_list|(
name|p
argument_list|,
name|cppTestUser
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|,
name|defaultPerms
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|boolean
name|expectedReadNode
init|=
name|Permissions
operator|.
name|includes
argument_list|(
name|defaultPerms
argument_list|,
name|Permissions
operator|.
name|READ_NODE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|p
argument_list|,
name|expectedReadNode
argument_list|,
name|cppTestUser
operator|.
name|isGranted
argument_list|(
name|tree
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGrantedAdmin
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|p
range|:
name|NODE_PATHS
control|)
block|{
name|Tree
name|tree
init|=
name|readOnlyRoot
operator|.
name|getTree
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|p
argument_list|,
name|cppAdminUser
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ_NODE
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p
argument_list|,
name|cppAdminUser
operator|.
name|isGranted
argument_list|(
name|tree
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
name|p
argument_list|,
name|cppAdminUser
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|WRITE
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p
argument_list|,
name|cppAdminUser
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGrantedProperty
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|p
range|:
name|NODE_PATHS
control|)
block|{
name|Tree
name|tree
init|=
name|readOnlyRoot
operator|.
name|getTree
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|p
argument_list|,
name|cppTestUser
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
name|PROPERTY_STATE
argument_list|,
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p
argument_list|,
name|cppTestUser
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
name|PROPERTY_STATE
argument_list|,
name|Permissions
operator|.
name|SET_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGrantedPropertyAdmin
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|p
range|:
name|NODE_PATHS
control|)
block|{
name|Tree
name|tree
init|=
name|readOnlyRoot
operator|.
name|getTree
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|p
argument_list|,
name|cppAdminUser
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
name|PROPERTY_STATE
argument_list|,
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p
argument_list|,
name|cppAdminUser
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
name|PROPERTY_STATE
argument_list|,
name|Permissions
operator|.
name|SET_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p
argument_list|,
name|cppAdminUser
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
name|PROPERTY_STATE
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGrantedAction
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|p
range|:
name|defActionsGranted
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
index|[]
name|actions
init|=
name|defActionsGranted
operator|.
name|get
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|actions
argument_list|)
operator|.
name|contains
argument_list|(
name|Session
operator|.
name|ACTION_READ
argument_list|)
condition|)
block|{
name|TreeLocation
name|tl
init|=
name|TreeLocation
operator|.
name|create
argument_list|(
name|readOnlyRoot
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|p
argument_list|,
name|tl
operator|.
name|getTree
argument_list|()
operator|!=
literal|null
argument_list|,
name|cppTestUser
operator|.
name|isGranted
argument_list|(
name|p
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|p
argument_list|,
name|cppTestUser
operator|.
name|isGranted
argument_list|(
name|p
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|actions
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|assertFalse
argument_list|(
name|p
argument_list|,
name|cppTestUser
operator|.
name|isGranted
argument_list|(
name|p
argument_list|,
name|getActionString
argument_list|(
name|actions
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGrantedAction2
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|noAccess
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|String
index|[]
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
name|ROOT_PATH
argument_list|,
operator|new
name|String
index|[]
block|{
name|Session
operator|.
name|ACTION_READ
block|}
argument_list|)
decl|.
name|put
argument_list|(
name|ROOT_PATH
operator|+
literal|"jcr:primaryType"
argument_list|,
operator|new
name|String
index|[]
block|{
name|Session
operator|.
name|ACTION_READ
block|,
name|Session
operator|.
name|ACTION_SET_PROPERTY
block|}
argument_list|)
decl|.
name|put
argument_list|(
literal|"/nonexisting"
argument_list|,
operator|new
name|String
index|[]
block|{
name|Session
operator|.
name|ACTION_READ
block|,
name|Session
operator|.
name|ACTION_ADD_NODE
block|}
argument_list|)
decl|.
name|put
argument_list|(
name|TEST_PATH_2
argument_list|,
operator|new
name|String
index|[]
block|{
name|Session
operator|.
name|ACTION_READ
block|,
name|Session
operator|.
name|ACTION_REMOVE
block|}
argument_list|)
decl|.
name|put
argument_list|(
name|TEST_PATH_2
operator|+
literal|"/jcr:primaryType"
argument_list|,
operator|new
name|String
index|[]
block|{
name|Session
operator|.
name|ACTION_READ
block|,
name|Session
operator|.
name|ACTION_SET_PROPERTY
block|}
argument_list|)
decl|.
name|put
argument_list|(
name|TEST_A_B_C_PATH
argument_list|,
operator|new
name|String
index|[]
block|{
name|Session
operator|.
name|ACTION_READ
block|,
name|Session
operator|.
name|ACTION_REMOVE
block|}
argument_list|)
decl|.
name|put
argument_list|(
name|TEST_A_B_C_PATH
operator|+
literal|"/noneExisting"
argument_list|,
operator|new
name|String
index|[]
block|{
name|Session
operator|.
name|ACTION_READ
block|,
name|JackrabbitSession
operator|.
name|ACTION_REMOVE_NODE
block|}
argument_list|)
decl|.
name|put
argument_list|(
name|TEST_A_B_C_PATH
operator|+
literal|"/jcr:primaryType"
argument_list|,
operator|new
name|String
index|[]
block|{
name|JackrabbitSession
operator|.
name|ACTION_REMOVE_PROPERTY
block|}
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|noAccess
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertFalse
argument_list|(
name|p
argument_list|,
name|cppTestUser
operator|.
name|isGranted
argument_list|(
name|p
argument_list|,
name|getActionString
argument_list|(
name|noAccess
operator|.
name|get
argument_list|(
name|p
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGrantedActionAdmin
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|p
range|:
name|defActionsGranted
operator|.
name|keySet
argument_list|()
control|)
block|{
name|boolean
name|expectedRead
init|=
name|readOnlyRoot
operator|.
name|getTree
argument_list|(
name|p
argument_list|)
operator|.
name|exists
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|p
argument_list|,
name|expectedRead
argument_list|,
name|cppAdminUser
operator|.
name|isGranted
argument_list|(
name|p
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p
argument_list|,
name|cppAdminUser
operator|.
name|isGranted
argument_list|(
name|p
argument_list|,
name|getActionString
argument_list|(
name|ALL_ACTIONS
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRepositoryPermissionIsGranted
parameter_list|()
throws|throws
name|Exception
block|{
name|RepositoryPermission
name|rp
init|=
name|cppTestUser
operator|.
name|getRepositoryPermission
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|rp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|NAMESPACE_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|NODE_TYPE_DEFINITION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|NAMESPACE_MANAGEMENT
operator||
name|Permissions
operator|.
name|NODE_TYPE_DEFINITION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRepositoryPermissionIsGrantedAdminUser
parameter_list|()
throws|throws
name|Exception
block|{
name|RepositoryPermission
name|rp
init|=
name|cppAdminUser
operator|.
name|getRepositoryPermission
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|rp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|NAMESPACE_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|NODE_TYPE_DEFINITION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|NAMESPACE_MANAGEMENT
operator||
name|Permissions
operator|.
name|NODE_TYPE_DEFINITION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|PRIVILEGE_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|NAMESPACE_MANAGEMENT
operator||
name|Permissions
operator|.
name|PRIVILEGE_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rp
operator|.
name|isGranted
argument_list|(
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
name|testTreePermissionIsGranted
parameter_list|()
throws|throws
name|Exception
block|{
name|TreePermission
name|parentPermission
init|=
name|TreePermission
operator|.
name|EMPTY
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|TP_PATHS
control|)
block|{
name|TreePermission
name|tp
init|=
name|cppTestUser
operator|.
name|getTreePermission
argument_list|(
name|readOnlyRoot
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
argument_list|,
name|parentPermission
argument_list|)
decl_stmt|;
name|Long
name|toTest
init|=
operator|(
name|defPermissions
operator|.
name|containsKey
argument_list|(
name|path
argument_list|)
operator|)
condition|?
name|defPermissions
operator|.
name|get
argument_list|(
name|path
argument_list|)
else|:
name|defPermissions
operator|.
name|get
argument_list|(
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|path
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|toTest
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|Permissions
operator|.
name|READ_NODE
operator|==
name|toTest
condition|)
block|{
name|assertTrue
argument_list|(
name|path
argument_list|,
name|tp
operator|.
name|isGranted
argument_list|(
name|toTest
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|canRead
init|=
name|Permissions
operator|.
name|includes
argument_list|(
name|toTest
argument_list|,
name|Permissions
operator|.
name|READ_NODE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|path
argument_list|,
name|canRead
argument_list|,
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
name|path
argument_list|,
name|tp
operator|.
name|isGranted
argument_list|(
name|toTest
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|parentPermission
operator|=
name|tp
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTreePermissionIsGrantedProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|TreePermission
name|parentPermission
init|=
name|TreePermission
operator|.
name|EMPTY
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|TP_PATHS
control|)
block|{
name|TreePermission
name|tp
init|=
name|cppTestUser
operator|.
name|getTreePermission
argument_list|(
name|readOnlyRoot
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
argument_list|,
name|parentPermission
argument_list|)
decl_stmt|;
name|Long
name|toTest
init|=
operator|(
name|defPermissions
operator|.
name|containsKey
argument_list|(
name|path
argument_list|)
operator|)
condition|?
name|defPermissions
operator|.
name|get
argument_list|(
name|path
argument_list|)
else|:
name|defPermissions
operator|.
name|get
argument_list|(
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|path
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|toTest
operator|!=
literal|null
condition|)
block|{
name|boolean
name|granted
init|=
operator|(
name|toTest
operator|==
name|Permissions
operator|.
name|READ_NODE
operator|)
decl_stmt|;
name|assertEquals
argument_list|(
name|path
argument_list|,
name|granted
argument_list|,
name|tp
operator|.
name|isGranted
argument_list|(
name|toTest
argument_list|,
name|PROPERTY_STATE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|,
name|PROPERTY_STATE
argument_list|)
argument_list|)
expr_stmt|;
name|parentPermission
operator|=
name|tp
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTreePermissionCanRead
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|readMap
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Boolean
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
name|ROOT_PATH
argument_list|,
literal|false
argument_list|)
decl|.
name|put
argument_list|(
name|TEST_PATH
argument_list|,
literal|true
argument_list|)
decl|.
name|put
argument_list|(
name|TEST_A_PATH
argument_list|,
literal|true
argument_list|)
decl|.
name|put
argument_list|(
name|TEST_A_B_PATH
argument_list|,
literal|true
argument_list|)
decl|.
name|put
argument_list|(
name|TEST_A_B_C_PATH
argument_list|,
literal|false
argument_list|)
decl|.
name|put
argument_list|(
name|TEST_A_B_C_PATH
operator|+
literal|"/nonexisting"
argument_list|,
literal|false
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
name|TreePermission
name|parentPermission
init|=
name|TreePermission
operator|.
name|EMPTY
decl_stmt|;
for|for
control|(
name|String
name|nodePath
range|:
name|readMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Tree
name|tree
init|=
name|readOnlyRoot
operator|.
name|getTree
argument_list|(
name|nodePath
argument_list|)
decl_stmt|;
name|TreePermission
name|tp
init|=
name|cppTestUser
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|parentPermission
argument_list|)
decl_stmt|;
name|boolean
name|expectedResult
init|=
name|readMap
operator|.
name|get
argument_list|(
name|nodePath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nodePath
argument_list|,
name|expectedResult
argument_list|,
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|parentPermission
operator|=
name|tp
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTreePermissionCanReadProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|TreePermission
name|parentPermission
init|=
name|TreePermission
operator|.
name|EMPTY
decl_stmt|;
for|for
control|(
name|String
name|nodePath
range|:
name|TP_PATHS
control|)
block|{
name|Tree
name|tree
init|=
name|readOnlyRoot
operator|.
name|getTree
argument_list|(
name|nodePath
argument_list|)
decl_stmt|;
name|TreePermission
name|tp
init|=
name|cppTestUser
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|parentPermission
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|nodePath
argument_list|,
name|tp
operator|.
name|canRead
argument_list|(
name|PROPERTY_STATE
argument_list|)
argument_list|)
expr_stmt|;
name|parentPermission
operator|=
name|tp
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTreePermissionCanReadAdmin
parameter_list|()
block|{
name|TreePermission
name|parentPermission
init|=
name|TreePermission
operator|.
name|EMPTY
decl_stmt|;
for|for
control|(
name|String
name|nodePath
range|:
name|TP_PATHS
control|)
block|{
name|Tree
name|tree
init|=
name|readOnlyRoot
operator|.
name|getTree
argument_list|(
name|nodePath
argument_list|)
decl_stmt|;
name|TreePermission
name|tp
init|=
name|cppAdminUser
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|parentPermission
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|nodePath
argument_list|,
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nodePath
argument_list|,
name|tp
operator|.
name|canRead
argument_list|(
name|PROPERTY_STATE
argument_list|)
argument_list|)
expr_stmt|;
name|parentPermission
operator|=
name|tp
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

