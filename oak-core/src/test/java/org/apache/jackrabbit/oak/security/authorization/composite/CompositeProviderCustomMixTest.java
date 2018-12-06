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
name|java
operator|.
name|util
operator|.
name|Set
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|security
operator|.
name|authorization
operator|.
name|composite
operator|.
name|CompositeAuthorizationConfiguration
operator|.
name|CompositionType
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
name|Assert
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
name|JCR_NAMESPACE_MANAGEMENT
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
name|JCR_NODE_TYPE_MANAGEMENT
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
name|JCR_WRITE
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

begin_class
specifier|public
class|class
name|CompositeProviderCustomMixTest
extends|extends
name|AbstractSecurityTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|hasPrivilegesTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|supp1
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_READ
argument_list|,
name|JCR_NAMESPACE_MANAGEMENT
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|supp2
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_READ
argument_list|,
name|JCR_WRITE
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|all
init|=
name|Sets
operator|.
name|union
argument_list|(
name|supp1
argument_list|,
name|supp2
argument_list|)
decl_stmt|;
comment|// tests all possible 256 shuffles
for|for
control|(
name|CompositionType
name|type
range|:
name|CompositionType
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|Set
argument_list|<
name|String
argument_list|>
name|granted1
range|:
name|Sets
operator|.
name|powerSet
argument_list|(
name|supp1
argument_list|)
control|)
block|{
for|for
control|(
name|Set
argument_list|<
name|String
argument_list|>
name|granted2
range|:
name|Sets
operator|.
name|powerSet
argument_list|(
name|supp2
argument_list|)
control|)
block|{
for|for
control|(
name|Set
argument_list|<
name|String
argument_list|>
name|ps
range|:
name|Sets
operator|.
name|powerSet
argument_list|(
name|all
argument_list|)
control|)
block|{
name|CompositePermissionProvider
name|cpp
init|=
name|buildCpp
argument_list|(
name|supp1
argument_list|,
name|granted1
argument_list|,
name|supp2
argument_list|,
name|granted2
argument_list|,
name|type
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|boolean
name|expected
init|=
name|expected
argument_list|(
name|ps
argument_list|,
name|supp1
argument_list|,
name|granted1
argument_list|,
name|supp2
argument_list|,
name|granted2
argument_list|,
name|type
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|boolean
name|result
init|=
name|cpp
operator|.
name|hasPrivileges
argument_list|(
literal|null
argument_list|,
name|ps
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|err
init|=
literal|"Checking "
operator|+
name|ps
operator|+
literal|" in {supported: "
operator|+
name|supp1
operator|+
literal|", granted: "
operator|+
name|granted1
operator|+
literal|"} "
operator|+
name|type
operator|+
literal|" {supported: "
operator|+
name|supp2
operator|+
literal|", granted: "
operator|+
name|granted2
operator|+
literal|"}"
decl_stmt|;
name|assertEquals
argument_list|(
name|err
argument_list|,
name|expected
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|isGrantedTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|supp1
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_READ
argument_list|,
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|supp2
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_READ
argument_list|,
name|JCR_WRITE
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|all
init|=
name|Sets
operator|.
name|union
argument_list|(
name|supp1
argument_list|,
name|supp2
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|grantMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|grantMap
operator|.
name|put
argument_list|(
name|JCR_READ
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
expr_stmt|;
name|grantMap
operator|.
name|put
argument_list|(
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|,
name|Permissions
operator|.
name|NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
name|grantMap
operator|.
name|put
argument_list|(
name|JCR_WRITE
argument_list|,
name|Permissions
operator|.
name|WRITE
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|actionMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|actionMap
operator|.
name|put
argument_list|(
name|JCR_READ
argument_list|,
name|JackrabbitSession
operator|.
name|ACTION_READ
argument_list|)
expr_stmt|;
name|actionMap
operator|.
name|put
argument_list|(
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|,
name|JackrabbitSession
operator|.
name|ACTION_NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
name|actionMap
operator|.
name|put
argument_list|(
name|JCR_WRITE
argument_list|,
name|JackrabbitSession
operator|.
name|ACTION_ADD_NODE
argument_list|)
expr_stmt|;
comment|// tests all possible 256 shuffles
for|for
control|(
name|CompositionType
name|type
range|:
name|CompositionType
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|Set
argument_list|<
name|String
argument_list|>
name|granted1
range|:
name|Sets
operator|.
name|powerSet
argument_list|(
name|supp1
argument_list|)
control|)
block|{
for|for
control|(
name|Set
argument_list|<
name|String
argument_list|>
name|granted2
range|:
name|Sets
operator|.
name|powerSet
argument_list|(
name|supp2
argument_list|)
control|)
block|{
for|for
control|(
name|Set
argument_list|<
name|String
argument_list|>
name|ps
range|:
name|Sets
operator|.
name|powerSet
argument_list|(
name|all
argument_list|)
control|)
block|{
name|CompositePermissionProvider
name|cpp
init|=
name|buildCpp
argument_list|(
name|supp1
argument_list|,
name|granted1
argument_list|,
name|supp2
argument_list|,
name|granted2
argument_list|,
name|type
argument_list|,
name|grantMap
argument_list|)
decl_stmt|;
name|boolean
name|expected
init|=
name|expected
argument_list|(
name|ps
argument_list|,
name|supp1
argument_list|,
name|granted1
argument_list|,
name|supp2
argument_list|,
name|granted2
argument_list|,
name|type
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|result1
init|=
name|cpp
operator|.
name|isGranted
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|mapToPermissions
argument_list|(
name|ps
argument_list|,
name|grantMap
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|err1
init|=
literal|"[isGranted1] Checking "
operator|+
name|ps
operator|+
literal|" in {supported: "
operator|+
name|supp1
operator|+
literal|", granted: "
operator|+
name|granted1
operator|+
literal|"} "
operator|+
name|type
operator|+
literal|" {supported: "
operator|+
name|supp2
operator|+
literal|", granted: "
operator|+
name|granted2
operator|+
literal|"}"
decl_stmt|;
name|assertEquals
argument_list|(
name|err1
argument_list|,
name|expected
argument_list|,
name|result1
argument_list|)
expr_stmt|;
comment|// check existing path
name|boolean
name|result2
init|=
name|cpp
operator|.
name|isGranted
argument_list|(
literal|"/"
argument_list|,
name|mapToActions
argument_list|(
name|ps
argument_list|,
name|actionMap
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|err2
init|=
literal|"[isGranted2] Checking "
operator|+
name|ps
operator|+
literal|" in {supported: "
operator|+
name|supp1
operator|+
literal|", granted: "
operator|+
name|granted1
operator|+
literal|"} "
operator|+
name|type
operator|+
literal|" {supported: "
operator|+
name|supp2
operator|+
literal|", granted: "
operator|+
name|granted2
operator|+
literal|"}"
decl_stmt|;
name|assertEquals
argument_list|(
name|err2
argument_list|,
name|expected
argument_list|,
name|result2
argument_list|)
expr_stmt|;
comment|// check non existing path
name|boolean
name|result3
init|=
name|cpp
operator|.
name|isGranted
argument_list|(
literal|"/doesnotexist"
argument_list|,
name|mapToActions
argument_list|(
name|ps
argument_list|,
name|actionMap
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|err3
init|=
literal|"[isGranted3] Checking "
operator|+
name|ps
operator|+
literal|" in {supported: "
operator|+
name|supp1
operator|+
literal|", granted: "
operator|+
name|granted1
operator|+
literal|"} "
operator|+
name|type
operator|+
literal|" {supported: "
operator|+
name|supp2
operator|+
literal|", granted: "
operator|+
name|granted2
operator|+
literal|"}"
decl_stmt|;
name|assertEquals
argument_list|(
name|err3
argument_list|,
name|expected
argument_list|,
name|result3
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|getRepositoryPermissionTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|supp1
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_READ
argument_list|,
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|supp2
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_READ
argument_list|,
name|JCR_WRITE
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|all
init|=
name|Sets
operator|.
name|union
argument_list|(
name|supp1
argument_list|,
name|supp2
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|grantMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|grantMap
operator|.
name|put
argument_list|(
name|JCR_READ
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
expr_stmt|;
name|grantMap
operator|.
name|put
argument_list|(
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|,
name|Permissions
operator|.
name|NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
name|grantMap
operator|.
name|put
argument_list|(
name|JCR_WRITE
argument_list|,
name|Permissions
operator|.
name|WRITE
argument_list|)
expr_stmt|;
comment|// tests all possible 256 shuffles
for|for
control|(
name|CompositionType
name|type
range|:
name|CompositionType
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|Set
argument_list|<
name|String
argument_list|>
name|granted1
range|:
name|Sets
operator|.
name|powerSet
argument_list|(
name|supp1
argument_list|)
control|)
block|{
for|for
control|(
name|Set
argument_list|<
name|String
argument_list|>
name|granted2
range|:
name|Sets
operator|.
name|powerSet
argument_list|(
name|supp2
argument_list|)
control|)
block|{
for|for
control|(
name|Set
argument_list|<
name|String
argument_list|>
name|ps
range|:
name|Sets
operator|.
name|powerSet
argument_list|(
name|all
argument_list|)
control|)
block|{
name|CompositePermissionProvider
name|cpp
init|=
name|buildCpp
argument_list|(
name|supp1
argument_list|,
name|granted1
argument_list|,
name|supp2
argument_list|,
name|granted2
argument_list|,
name|type
argument_list|,
name|grantMap
argument_list|)
decl_stmt|;
name|boolean
name|expected
init|=
name|expected
argument_list|(
name|ps
argument_list|,
name|supp1
argument_list|,
name|granted1
argument_list|,
name|supp2
argument_list|,
name|granted2
argument_list|,
name|type
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|result
init|=
name|cpp
operator|.
name|getRepositoryPermission
argument_list|()
operator|.
name|isGranted
argument_list|(
name|mapToPermissions
argument_list|(
name|ps
argument_list|,
name|grantMap
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|err
init|=
literal|"Checking "
operator|+
name|ps
operator|+
literal|" in {supported: "
operator|+
name|supp1
operator|+
literal|", granted: "
operator|+
name|granted1
operator|+
literal|"} "
operator|+
name|type
operator|+
literal|" {supported: "
operator|+
name|supp2
operator|+
literal|", granted: "
operator|+
name|granted2
operator|+
literal|"}"
decl_stmt|;
name|assertEquals
argument_list|(
name|err
argument_list|,
name|expected
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|getTreePermissionTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|supp1
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_READ
argument_list|,
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|supp2
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_READ
argument_list|,
name|JCR_WRITE
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|all
init|=
name|Sets
operator|.
name|union
argument_list|(
name|supp1
argument_list|,
name|supp2
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|grantMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|grantMap
operator|.
name|put
argument_list|(
name|JCR_READ
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
expr_stmt|;
name|grantMap
operator|.
name|put
argument_list|(
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|,
name|Permissions
operator|.
name|NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
name|grantMap
operator|.
name|put
argument_list|(
name|JCR_WRITE
argument_list|,
name|Permissions
operator|.
name|WRITE
argument_list|)
expr_stmt|;
comment|// tests all possible 256 shuffles
for|for
control|(
name|CompositionType
name|type
range|:
name|CompositionType
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|Set
argument_list|<
name|String
argument_list|>
name|granted1
range|:
name|Sets
operator|.
name|powerSet
argument_list|(
name|supp1
argument_list|)
control|)
block|{
for|for
control|(
name|Set
argument_list|<
name|String
argument_list|>
name|granted2
range|:
name|Sets
operator|.
name|powerSet
argument_list|(
name|supp2
argument_list|)
control|)
block|{
for|for
control|(
name|Set
argument_list|<
name|String
argument_list|>
name|ps
range|:
name|Sets
operator|.
name|powerSet
argument_list|(
name|all
argument_list|)
control|)
block|{
name|CompositePermissionProvider
name|cpp
init|=
name|buildCpp
argument_list|(
name|supp1
argument_list|,
name|granted1
argument_list|,
name|supp2
argument_list|,
name|granted2
argument_list|,
name|type
argument_list|,
name|grantMap
argument_list|)
decl_stmt|;
name|boolean
name|expected
init|=
name|expected
argument_list|(
name|ps
argument_list|,
name|supp1
argument_list|,
name|granted1
argument_list|,
name|supp2
argument_list|,
name|granted2
argument_list|,
name|type
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|result
init|=
name|cpp
operator|.
name|getTreePermission
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
operator|.
name|isGranted
argument_list|(
name|mapToPermissions
argument_list|(
name|ps
argument_list|,
name|grantMap
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|err
init|=
literal|"Checking "
operator|+
name|ps
operator|+
literal|" in {supported: "
operator|+
name|supp1
operator|+
literal|", granted: "
operator|+
name|granted1
operator|+
literal|"} "
operator|+
name|type
operator|+
literal|" {supported: "
operator|+
name|supp2
operator|+
literal|", granted: "
operator|+
name|granted2
operator|+
literal|"}"
decl_stmt|;
name|assertEquals
argument_list|(
name|err
argument_list|,
name|expected
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|private
specifier|static
name|long
name|mapToPermissions
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|items
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|grantMap
parameter_list|)
block|{
name|long
name|perm
init|=
name|Permissions
operator|.
name|NO_PERMISSION
decl_stmt|;
for|for
control|(
name|String
name|i
range|:
name|items
control|)
block|{
name|perm
operator||=
name|grantMap
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|perm
return|;
block|}
specifier|private
specifier|static
name|String
name|mapToActions
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|items
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|actionMap
parameter_list|)
block|{
if|if
condition|(
name|items
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|""
return|;
block|}
name|String
name|actions
init|=
literal|""
decl_stmt|;
for|for
control|(
name|String
name|i
range|:
name|items
control|)
block|{
name|actions
operator|+=
name|actionMap
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|+
literal|","
expr_stmt|;
block|}
return|return
name|actions
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|actions
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|expected
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|check
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|supported1
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|granted1
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|supported2
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|granted2
parameter_list|,
name|CompositionType
name|type
parameter_list|,
name|boolean
name|emptyIsTrue
parameter_list|)
block|{
comment|// Special case handled differently in the composite permissions vs.
comment|// actions
if|if
condition|(
name|check
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|emptyIsTrue
return|;
block|}
if|if
condition|(
name|type
operator|==
name|CompositionType
operator|.
name|OR
condition|)
block|{
return|return
name|Sets
operator|.
name|difference
argument_list|(
name|Sets
operator|.
name|difference
argument_list|(
name|check
argument_list|,
name|granted1
argument_list|)
argument_list|,
name|granted2
argument_list|)
operator|.
name|isEmpty
argument_list|()
return|;
block|}
else|else
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|f1
init|=
name|Sets
operator|.
name|intersection
argument_list|(
name|supported1
argument_list|,
name|check
argument_list|)
decl_stmt|;
name|boolean
name|hasf1
init|=
name|granted1
operator|.
name|containsAll
argument_list|(
name|f1
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|f2
init|=
name|Sets
operator|.
name|intersection
argument_list|(
name|supported2
argument_list|,
name|check
argument_list|)
decl_stmt|;
name|boolean
name|hasf2
init|=
name|granted2
operator|.
name|containsAll
argument_list|(
name|f2
argument_list|)
decl_stmt|;
return|return
name|hasf1
operator|&&
name|hasf2
return|;
block|}
block|}
specifier|private
name|CompositePermissionProvider
name|buildCpp
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|supported1
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|granted1
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|supported2
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|granted2
parameter_list|,
name|CompositionType
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|grantMap
parameter_list|)
block|{
name|AggregatedPermissionProvider
name|a1
init|=
operator|new
name|CustomProvider
argument_list|(
name|root
argument_list|,
name|supported1
argument_list|,
name|granted1
argument_list|,
name|grantMap
argument_list|)
decl_stmt|;
name|AggregatedPermissionProvider
name|a2
init|=
operator|new
name|CustomProvider
argument_list|(
name|root
argument_list|,
name|supported2
argument_list|,
name|granted2
argument_list|,
name|grantMap
argument_list|)
decl_stmt|;
name|AuthorizationConfiguration
name|config
init|=
name|getConfig
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AggregatedPermissionProvider
argument_list|>
name|composite
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|a1
argument_list|,
name|a2
argument_list|)
decl_stmt|;
return|return
operator|new
name|CompositePermissionProvider
argument_list|(
name|root
argument_list|,
name|composite
argument_list|,
name|config
operator|.
name|getContext
argument_list|()
argument_list|,
name|type
argument_list|,
name|getRootProvider
argument_list|()
argument_list|,
name|getTreeProvider
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|CustomProvider
implements|implements
name|AggregatedPermissionProvider
block|{
specifier|private
specifier|final
name|PrivilegeBitsProvider
name|pbp
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|supported
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|granted
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|grantMap
decl_stmt|;
specifier|private
name|CustomProvider
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|supported
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|granted
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|grantMap
parameter_list|)
block|{
name|this
operator|.
name|pbp
operator|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|this
operator|.
name|supported
operator|=
name|supported
expr_stmt|;
name|this
operator|.
name|granted
operator|=
name|granted
expr_stmt|;
name|this
operator|.
name|grantMap
operator|=
name|grantMap
expr_stmt|;
block|}
specifier|private
specifier|static
name|PrivilegeBits
name|toBits
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|supported
parameter_list|,
name|PrivilegeBitsProvider
name|pbp
parameter_list|)
block|{
name|PrivilegeBits
name|suppBits
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|supported
control|)
block|{
name|suppBits
operator|.
name|add
argument_list|(
name|pbp
operator|.
name|getBits
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|suppBits
return|;
block|}
annotation|@
name|NotNull
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
name|toBits
argument_list|(
name|supported
argument_list|,
name|pbp
argument_list|)
operator|.
name|retain
argument_list|(
name|privilegeBits
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPrivileges
parameter_list|(
name|Tree
name|tree
parameter_list|,
annotation|@
name|NotNull
name|String
modifier|...
name|privilegeNames
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|in
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|privilegeNames
argument_list|)
decl_stmt|;
return|return
name|granted
operator|.
name|containsAll
argument_list|(
name|in
argument_list|)
return|;
block|}
specifier|private
name|long
name|supportedPermissions
parameter_list|(
name|long
name|permissions
parameter_list|)
block|{
name|long
name|allperms
init|=
name|mapToPermissions
argument_list|(
name|supported
argument_list|,
name|grantMap
argument_list|)
decl_stmt|;
name|long
name|delta
init|=
name|Permissions
operator|.
name|diff
argument_list|(
name|permissions
argument_list|,
name|allperms
argument_list|)
decl_stmt|;
return|return
name|Permissions
operator|.
name|diff
argument_list|(
name|permissions
argument_list|,
name|delta
argument_list|)
return|;
block|}
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
name|supportedPermissions
argument_list|(
name|permissions
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|supportedPermissions
parameter_list|(
annotation|@
name|NotNull
name|TreeLocation
name|location
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|supportedPermissions
argument_list|(
name|permissions
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|supportedPermissions
parameter_list|(
annotation|@
name|NotNull
name|TreePermission
name|treePermission
parameter_list|,
name|PropertyState
name|property
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|supportedPermissions
argument_list|(
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
name|NotNull
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
name|long
name|myperms
init|=
name|mapToPermissions
argument_list|(
name|granted
argument_list|,
name|grantMap
argument_list|)
decl_stmt|;
return|return
name|Permissions
operator|.
name|includes
argument_list|(
name|myperms
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
name|NotNull
name|TreeLocation
name|location
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
name|long
name|myperms
init|=
name|mapToPermissions
argument_list|(
name|granted
argument_list|,
name|grantMap
argument_list|)
decl_stmt|;
return|return
name|Permissions
operator|.
name|includes
argument_list|(
name|myperms
argument_list|,
name|permissions
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RepositoryPermission
name|getRepositoryPermission
parameter_list|()
block|{
return|return
operator|new
name|RepositoryPermission
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|long
name|repositoryPermissions
parameter_list|)
block|{
name|long
name|myperms
init|=
name|mapToPermissions
argument_list|(
name|granted
argument_list|,
name|grantMap
argument_list|)
decl_stmt|;
return|return
name|Permissions
operator|.
name|includes
argument_list|(
name|myperms
argument_list|,
name|repositoryPermissions
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|TreePermission
name|getTreePermission
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|,
annotation|@
name|NotNull
name|TreeType
name|type
parameter_list|,
annotation|@
name|NotNull
name|TreePermission
name|parentPermission
parameter_list|)
block|{
return|return
operator|new
name|CustomTreePermission
argument_list|(
name|granted
argument_list|,
name|grantMap
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|refresh
parameter_list|()
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"method should not be called"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getPrivileges
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"method should not be called"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|TreePermission
name|getTreePermission
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|,
annotation|@
name|NotNull
name|TreePermission
name|parentPermission
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"method should not be called"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
annotation|@
name|NotNull
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|String
name|jcrActions
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"method should not be called"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CustomProvider [supported="
operator|+
name|supported
operator|+
literal|", granted="
operator|+
name|granted
operator|+
literal|"]"
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|CustomTreePermission
implements|implements
name|TreePermission
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|granted
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|grantMap
decl_stmt|;
specifier|public
name|CustomTreePermission
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|granted
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|grantMap
parameter_list|)
block|{
name|this
operator|.
name|granted
operator|=
name|granted
expr_stmt|;
name|this
operator|.
name|grantMap
operator|=
name|grantMap
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|TreePermission
name|getChildPermission
parameter_list|(
annotation|@
name|NotNull
name|String
name|childName
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|childState
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"method should not be called"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|()
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"method should not be called"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|(
annotation|@
name|NotNull
name|PropertyState
name|property
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"method should not be called"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canReadAll
parameter_list|()
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"method should not be called"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canReadProperties
parameter_list|()
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"method should not be called"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|long
name|permissions
parameter_list|)
block|{
name|long
name|myperms
init|=
name|mapToPermissions
argument_list|(
name|granted
argument_list|,
name|grantMap
argument_list|)
decl_stmt|;
return|return
name|Permissions
operator|.
name|includes
argument_list|(
name|myperms
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
name|long
name|permissions
parameter_list|,
annotation|@
name|NotNull
name|PropertyState
name|property
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"method should not be called"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

