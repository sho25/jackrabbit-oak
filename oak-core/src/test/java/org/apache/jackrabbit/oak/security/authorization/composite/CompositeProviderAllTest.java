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
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|OpenPermissionProvider
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
comment|/**  * Test the effect of the combination of  *  * - default permission provider (which a limited permission setup)  * - custom provider that always grants full access and supports all permissions.  *  * for the {@link #getTestUser()}.  *  * The expected result is only the subset of permissions granted by the default  * provider. The test user must never have full access anywhere.  */
end_comment

begin_class
specifier|public
class|class
name|CompositeProviderAllTest
extends|extends
name|AbstractCompositeProviderTest
block|{
specifier|private
name|CompositePermissionProvider
name|cpp
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
name|cpp
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
name|OpenAggregateProvider
argument_list|(
name|root
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
for|for
control|(
name|String
name|p
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
name|expected
init|=
name|defPrivileges
operator|.
name|get
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|p
argument_list|,
name|expected
argument_list|,
name|cpp
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
name|privilegeNames
init|=
name|cpp
operator|.
name|getPrivileges
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_NAMESPACE_MANAGEMENT
argument_list|,
name|JCR_NODE_TYPE_DEFINITION_MANAGEMENT
argument_list|)
argument_list|,
name|privilegeNames
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
for|for
control|(
name|String
name|p
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
name|expected
init|=
name|defPrivileges
operator|.
name|get
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|Tree
name|tree
init|=
name|root
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
name|cpp
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|expected
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|expected
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
name|cpp
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
name|expected
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
name|assertTrue
argument_list|(
name|p
argument_list|,
name|cpp
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|,
name|expected
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
name|defPermissions
operator|.
name|keySet
argument_list|()
control|)
block|{
name|long
name|expected
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
name|assertTrue
argument_list|(
name|p
argument_list|,
name|cpp
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
name|PROPERTY_STATE
argument_list|,
name|expected
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
name|actionStr
init|=
name|getActionString
argument_list|(
name|defActionsGranted
operator|.
name|get
argument_list|(
name|p
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|p
operator|+
literal|" : "
operator|+
name|actionStr
argument_list|,
name|cpp
operator|.
name|isGranted
argument_list|(
name|p
argument_list|,
name|actionStr
argument_list|)
argument_list|)
expr_stmt|;
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
name|cpp
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
name|testRepositoryPermissionsIsGranted
parameter_list|()
throws|throws
name|Exception
block|{
name|RepositoryPermission
name|rp
init|=
name|cpp
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
name|assertTrue
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
name|assertTrue
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
name|cpp
operator|.
name|getTreePermission
argument_list|(
name|root
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
name|assertTrue
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|toTest
argument_list|)
argument_list|)
expr_stmt|;
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
name|cpp
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
name|assertTrue
argument_list|(
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
name|cpp
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
literal|true
argument_list|)
decl|.
name|put
argument_list|(
name|TEST_A_B_C_PATH
operator|+
literal|"/nonexisting"
argument_list|,
literal|true
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
name|cpp
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
comment|/**      * Custom permission provider that supports all permissions and grants      * full access for everyone.      */
specifier|private
specifier|static
specifier|final
class|class
name|OpenAggregateProvider
implements|implements
name|AggregatedPermissionProvider
block|{
specifier|private
specifier|static
specifier|final
name|PermissionProvider
name|BASE
init|=
name|OpenPermissionProvider
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|private
name|OpenAggregateProvider
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PrivilegeBits
name|supportedPrivileges
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PrivilegeBits
name|privilegeBits
parameter_list|)
block|{
return|return
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|root
argument_list|)
operator|.
name|getBits
argument_list|(
name|JCR_ALL
argument_list|)
return|;
block|}
comment|//-----------------------------------< AggregatedPermissionProvider>---
annotation|@
name|Override
specifier|public
name|long
name|supportedPermissions
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|permissions
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|supportedPermissions
parameter_list|(
annotation|@
name|Nonnull
name|TreeLocation
name|location
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|permissions
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|supportedPermissions
parameter_list|(
annotation|@
name|Nonnull
name|TreePermission
name|treePermission
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|propertyState
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|permissions
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
annotation|@
name|Nonnull
name|TreeLocation
name|location
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
comment|//---------------------------------------------< PermissionProvider>---
annotation|@
name|Override
specifier|public
name|void
name|refresh
parameter_list|()
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getPrivileges
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|BASE
operator|.
name|getPrivileges
argument_list|(
name|tree
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPrivileges
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|String
modifier|...
name|privilegeNames
parameter_list|)
block|{
return|return
name|BASE
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|privilegeNames
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RepositoryPermission
name|getRepositoryPermission
parameter_list|()
block|{
return|return
name|BASE
operator|.
name|getRepositoryPermission
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|TreePermission
name|getTreePermission
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|TreePermission
name|parentPermission
parameter_list|)
block|{
return|return
name|BASE
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|parentPermission
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|BASE
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
name|property
argument_list|,
name|permissions
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
annotation|@
name|Nonnull
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|String
name|jcrActions
parameter_list|)
block|{
return|return
name|BASE
operator|.
name|isGranted
argument_list|(
name|oakPath
argument_list|,
name|jcrActions
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

