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
name|cug
operator|.
name|impl
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|TreeProvider
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
name|impl
operator|.
name|AbstractTree
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
name|state
operator|.
name|NodeState
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

begin_class
specifier|public
class|class
name|CugTreePermissionTest
extends|extends
name|AbstractCugTest
block|{
specifier|private
name|CugTreePermission
name|allowedTp
decl_stmt|;
specifier|private
name|CugTreePermission
name|deniedTp
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
name|createCug
argument_list|(
name|SUPPORTED_PATH
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
name|allowedTp
operator|=
name|getCugTreePermission
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
name|deniedTp
operator|=
name|getCugTreePermission
argument_list|()
expr_stmt|;
block|}
specifier|private
name|CugTreePermission
name|getCugTreePermission
parameter_list|(
annotation|@
name|Nonnull
name|Principal
modifier|...
name|principals
parameter_list|)
block|{
return|return
name|getCugTreePermission
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|principals
argument_list|)
return|;
block|}
specifier|private
name|CugTreePermission
name|getCugTreePermission
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|Principal
modifier|...
name|principals
parameter_list|)
block|{
name|CugPermissionProvider
name|pp
init|=
name|createCugPermissionProvider
argument_list|(
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|SUPPORTED_PATHS
argument_list|)
argument_list|,
name|principals
argument_list|)
decl_stmt|;
name|TreePermission
name|targetTp
init|=
name|getTreePermission
argument_list|(
name|root
argument_list|,
name|path
argument_list|,
name|pp
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|targetTp
operator|instanceof
name|CugTreePermission
argument_list|)
expr_stmt|;
return|return
operator|(
name|CugTreePermission
operator|)
name|targetTp
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetChildPermission
parameter_list|()
throws|throws
name|Exception
block|{
name|TreeProvider
name|treeProvider
init|=
name|getTreeProvider
argument_list|()
decl_stmt|;
name|NodeState
name|ns
init|=
name|treeProvider
operator|.
name|asNodeState
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH
operator|+
literal|"/subtree"
argument_list|)
argument_list|)
decl_stmt|;
name|TreePermission
name|child
init|=
name|allowedTp
operator|.
name|getChildPermission
argument_list|(
literal|"subtree"
argument_list|,
name|ns
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|child
operator|instanceof
name|CugTreePermission
argument_list|)
expr_stmt|;
name|child
operator|=
name|deniedTp
operator|.
name|getChildPermission
argument_list|(
literal|"subtree"
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|child
operator|instanceof
name|CugTreePermission
argument_list|)
expr_stmt|;
name|NodeState
name|cugNs
init|=
name|treeProvider
operator|.
name|asNodeState
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|REP_CUG_POLICY
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TreePermission
name|cugChild
init|=
name|allowedTp
operator|.
name|getChildPermission
argument_list|(
name|REP_CUG_POLICY
argument_list|,
name|cugNs
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|TreePermission
operator|.
name|NO_RECOURSE
argument_list|,
name|cugChild
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsAllow
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|allowedTp
operator|.
name|isAllow
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|deniedTp
operator|.
name|isAllow
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsAllowNestedCug
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|childPath
init|=
name|SUPPORTED_PATH
operator|+
literal|"/subtree"
decl_stmt|;
comment|// before creating nested CUG
name|CugTreePermission
name|tp
init|=
name|getCugTreePermission
argument_list|(
name|childPath
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|isAllow
argument_list|()
argument_list|)
expr_stmt|;
name|tp
operator|=
name|getCugTreePermission
argument_list|(
name|childPath
argument_list|,
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
name|assertTrue
argument_list|(
name|tp
operator|.
name|isAllow
argument_list|()
argument_list|)
expr_stmt|;
comment|// create nested CUG for same principal
name|createCug
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
name|tp
operator|=
name|getCugTreePermission
argument_list|(
name|childPath
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|isAllow
argument_list|()
argument_list|)
expr_stmt|;
name|tp
operator|=
name|getCugTreePermission
argument_list|(
name|childPath
argument_list|,
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
name|assertTrue
argument_list|(
name|tp
operator|.
name|isAllow
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsInCug
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|allowedTp
operator|.
name|isInCug
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|deniedTp
operator|.
name|isInCug
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsInCugChild
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|childPath
init|=
name|SUPPORTED_PATH
operator|+
literal|"/subtree"
decl_stmt|;
name|CugTreePermission
name|tp
init|=
name|getCugTreePermission
argument_list|(
name|childPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|isInCug
argument_list|()
argument_list|)
expr_stmt|;
name|tp
operator|=
name|getCugTreePermission
argument_list|(
name|childPath
argument_list|,
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
name|assertTrue
argument_list|(
name|tp
operator|.
name|isInCug
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsInCugNestedCug
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|childPath
init|=
name|SUPPORTED_PATH
operator|+
literal|"/subtree"
decl_stmt|;
comment|// create nested CUG for same principal
name|createCug
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
name|CugTreePermission
name|tp
init|=
name|getCugTreePermission
argument_list|(
name|childPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|isInCug
argument_list|()
argument_list|)
expr_stmt|;
name|tp
operator|=
name|getCugTreePermission
argument_list|(
name|childPath
argument_list|,
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
name|assertTrue
argument_list|(
name|tp
operator|.
name|isInCug
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsInCugSupportedPathWithoutCug
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeUtil
name|node
init|=
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH2
argument_list|)
argument_list|)
decl_stmt|;
name|Tree
name|c1
init|=
name|node
operator|.
name|addChild
argument_list|(
literal|"c1"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|Tree
name|c2
init|=
name|node
operator|.
name|addChild
argument_list|(
literal|"c2"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|String
name|cugPath
init|=
name|c2
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|createCug
argument_list|(
name|cugPath
argument_list|,
name|getTestGroupPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|getCugTreePermission
argument_list|(
name|cugPath
argument_list|)
operator|.
name|isInCug
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|getCugTreePermission
argument_list|(
name|c1
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|isInCug
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasNested
parameter_list|()
block|{
name|CugTreePermission
name|tp
init|=
name|getCugTreePermission
argument_list|(
name|SUPPORTED_PATH
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|hasNestedCug
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|childPath
init|=
name|SUPPORTED_PATH
operator|+
literal|"/subtree"
decl_stmt|;
name|tp
operator|=
name|getCugTreePermission
argument_list|(
name|childPath
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|hasNestedCug
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasNestedWithNestedCug
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create nested CUG for same principal
name|String
name|childPath
init|=
name|SUPPORTED_PATH
operator|+
literal|"/subtree"
decl_stmt|;
name|createCug
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
name|CugTreePermission
name|tp
init|=
name|getCugTreePermission
argument_list|(
name|SUPPORTED_PATH
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|hasNestedCug
argument_list|()
argument_list|)
expr_stmt|;
name|tp
operator|=
name|getCugTreePermission
argument_list|(
name|childPath
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|hasNestedCug
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCanRead
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|allowedTp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|deniedTp
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
name|testCanReadProperty
parameter_list|()
block|{
name|PropertyState
name|ps
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"prop"
argument_list|,
literal|"val"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|allowedTp
operator|.
name|canRead
argument_list|(
name|ps
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|deniedTp
operator|.
name|canRead
argument_list|(
name|ps
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCanReadAll
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|allowedTp
operator|.
name|canReadAll
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|deniedTp
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
name|testCanReadProperties
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|allowedTp
operator|.
name|canReadProperties
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|deniedTp
operator|.
name|canReadProperties
argument_list|()
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
name|allowedTp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|allowedTp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|WRITE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|allowedTp
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
name|deniedTp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|deniedTp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|WRITE
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|deniedTp
operator|.
name|isGranted
argument_list|(
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
name|testIsGrantedProperty
parameter_list|()
block|{
name|PropertyState
name|ps
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"prop"
argument_list|,
literal|"val"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|allowedTp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|ALL
argument_list|,
name|ps
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|allowedTp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|WRITE
argument_list|,
name|ps
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|allowedTp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|,
name|ps
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|deniedTp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|ALL
argument_list|,
name|ps
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|deniedTp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|WRITE
argument_list|,
name|ps
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|deniedTp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|,
name|ps
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

