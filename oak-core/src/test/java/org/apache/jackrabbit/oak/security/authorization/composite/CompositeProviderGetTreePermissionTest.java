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
name|plugins
operator|.
name|tree
operator|.
name|TreeTypeProvider
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
name|security
operator|.
name|authorization
operator|.
name|composite
operator|.
name|CompositeAuthorizationConfiguration
operator|.
name|CompositionType
operator|.
name|AND
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
name|security
operator|.
name|authorization
operator|.
name|composite
operator|.
name|CompositeAuthorizationConfiguration
operator|.
name|CompositionType
operator|.
name|OR
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
name|ArgumentMatchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyString
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
name|CompositeProviderGetTreePermissionTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|CompositePermissionProvider
name|createProvider
parameter_list|(
annotation|@
name|NotNull
name|CompositeAuthorizationConfiguration
operator|.
name|CompositionType
name|compositionType
parameter_list|,
annotation|@
name|NotNull
name|AggregatedPermissionProvider
modifier|...
name|providers
parameter_list|)
block|{
return|return
operator|new
name|CompositePermissionProvider
argument_list|(
name|root
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|providers
argument_list|)
argument_list|,
name|Context
operator|.
name|DEFAULT
argument_list|,
name|compositionType
argument_list|,
name|getRootProvider
argument_list|()
argument_list|,
name|getTreeProvider
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyProvidersRootTree
parameter_list|()
block|{
name|AggregatedPermissionProvider
name|composite
init|=
name|createProvider
argument_list|(
name|OR
argument_list|)
decl_stmt|;
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
name|assertSame
argument_list|(
name|TreePermission
operator|.
name|EMPTY
argument_list|,
name|composite
operator|.
name|getTreePermission
argument_list|(
name|rootTree
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyProvidersCompositeParentPermission
parameter_list|()
block|{
name|TreePermission
name|tp
init|=
name|mock
argument_list|(
name|TreePermission
operator|.
name|class
argument_list|)
decl_stmt|;
name|AggregatedPermissionProvider
name|aggr
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|AggregatedPermissionProvider
operator|.
name|class
argument_list|)
operator|.
name|getTreePermission
argument_list|(
name|any
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TreeType
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TreePermission
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|tp
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
comment|// parent-TreePermission is CompositeTreePermission
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
name|AggregatedPermissionProvider
index|[]
name|providers
init|=
operator|new
name|AggregatedPermissionProvider
index|[]
block|{
name|aggr
block|,
operator|new
name|FullScopeProvider
argument_list|(
name|root
argument_list|)
block|}
decl_stmt|;
name|CompositeTreePermission
name|parentTp
init|=
operator|(
name|CompositeTreePermission
operator|)
name|CompositeTreePermission
operator|.
name|create
argument_list|(
name|rootTree
argument_list|,
name|getTreeProvider
argument_list|()
argument_list|,
operator|new
name|TreeTypeProvider
argument_list|(
name|Context
operator|.
name|DEFAULT
argument_list|)
argument_list|,
name|providers
argument_list|,
name|OR
argument_list|)
decl_stmt|;
comment|// getTreePermission from compositePP -> aggregated-providers as taken from parent-permission
name|AggregatedPermissionProvider
name|composite
init|=
name|createProvider
argument_list|(
name|AND
argument_list|)
decl_stmt|;
name|Tree
name|systemTree
init|=
name|rootTree
operator|.
name|getChild
argument_list|(
name|JcrConstants
operator|.
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|composite
operator|.
name|getTreePermission
argument_list|(
name|systemTree
argument_list|,
name|TreeType
operator|.
name|HIDDEN
argument_list|,
name|parentTp
argument_list|)
operator|instanceof
name|CompositeTreePermission
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|aggr
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getTreePermission
argument_list|(
name|rootTree
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|aggr
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getTreePermission
argument_list|(
name|systemTree
argument_list|,
name|TreeType
operator|.
name|HIDDEN
argument_list|,
name|tp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyProvidersMockParentPermission
parameter_list|()
block|{
name|TreePermission
name|tp
init|=
name|mock
argument_list|(
name|TreePermission
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|tp
operator|.
name|getChildPermission
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|tp
argument_list|)
expr_stmt|;
name|AggregatedPermissionProvider
name|composite
init|=
name|createProvider
argument_list|(
name|OR
argument_list|)
decl_stmt|;
name|Tree
name|ntTree
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
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|tp
argument_list|,
name|composite
operator|.
name|getTreePermission
argument_list|(
name|ntTree
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|tp
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|tp
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getChildPermission
argument_list|(
name|ntTree
operator|.
name|getName
argument_list|()
argument_list|,
name|getTreeProvider
argument_list|()
operator|.
name|asNodeState
argument_list|(
name|ntTree
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingleProvider
parameter_list|()
block|{
name|TreePermission
name|tp
init|=
name|mock
argument_list|(
name|TreePermission
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|tp
operator|.
name|getChildPermission
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|tp
argument_list|)
expr_stmt|;
name|AggregatedPermissionProvider
name|aggr
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|AggregatedPermissionProvider
operator|.
name|class
argument_list|)
operator|.
name|getTreePermission
argument_list|(
name|any
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TreeType
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TreePermission
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|tp
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|AggregatedPermissionProvider
name|composite
init|=
name|createProvider
argument_list|(
name|AND
argument_list|,
name|aggr
argument_list|)
decl_stmt|;
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
name|rootTreePermission
init|=
name|composite
operator|.
name|getTreePermission
argument_list|(
name|rootTree
argument_list|,
name|TreeType
operator|.
name|HIDDEN
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|tp
argument_list|,
name|rootTreePermission
argument_list|)
expr_stmt|;
comment|// type param is ignored for the root tree
name|verify
argument_list|(
name|aggr
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getTreePermission
argument_list|(
name|rootTree
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|aggr
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|getTreePermission
argument_list|(
name|rootTree
argument_list|,
name|TreeType
operator|.
name|HIDDEN
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|Tree
name|systemTree
init|=
name|rootTree
operator|.
name|getChild
argument_list|(
name|JcrConstants
operator|.
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|tp
argument_list|,
name|composite
operator|.
name|getTreePermission
argument_list|(
name|systemTree
argument_list|,
name|TreeType
operator|.
name|INTERNAL
argument_list|,
name|rootTreePermission
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|aggr
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|getTreePermission
argument_list|(
name|systemTree
argument_list|,
name|TreeType
operator|.
name|INTERNAL
argument_list|,
name|rootTreePermission
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|aggr
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|getTreePermission
argument_list|(
name|systemTree
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|rootTreePermission
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|tp
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getChildPermission
argument_list|(
name|systemTree
operator|.
name|getName
argument_list|()
argument_list|,
name|getTreeProvider
argument_list|()
operator|.
name|asNodeState
argument_list|(
name|systemTree
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTwoProvider
parameter_list|()
block|{
name|TreePermission
name|tp
init|=
name|mock
argument_list|(
name|TreePermission
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|tp
operator|.
name|getChildPermission
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|tp
argument_list|)
expr_stmt|;
name|AggregatedPermissionProvider
name|aggr
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|AggregatedPermissionProvider
operator|.
name|class
argument_list|)
operator|.
name|getTreePermission
argument_list|(
name|any
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TreeType
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TreePermission
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|tp
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|AggregatedPermissionProvider
name|composite
init|=
name|createProvider
argument_list|(
name|OR
argument_list|,
name|aggr
argument_list|,
operator|new
name|FullScopeProvider
argument_list|(
name|root
argument_list|)
argument_list|)
decl_stmt|;
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
name|rootTreePermission
init|=
name|composite
operator|.
name|getTreePermission
argument_list|(
name|rootTree
argument_list|,
name|TreeType
operator|.
name|VERSION
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rootTreePermission
operator|instanceof
name|CompositeTreePermission
argument_list|)
expr_stmt|;
comment|// type param is ignored for the root tree
name|verify
argument_list|(
name|aggr
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getTreePermission
argument_list|(
name|rootTree
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|aggr
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|getTreePermission
argument_list|(
name|rootTree
argument_list|,
name|TreeType
operator|.
name|VERSION
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|Tree
name|systemTree
init|=
name|rootTree
operator|.
name|getChild
argument_list|(
name|JcrConstants
operator|.
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|composite
operator|.
name|getTreePermission
argument_list|(
name|systemTree
argument_list|,
name|TreeType
operator|.
name|ACCESS_CONTROL
argument_list|,
name|rootTreePermission
argument_list|)
operator|instanceof
name|CompositeTreePermission
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|aggr
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getTreePermission
argument_list|(
name|systemTree
argument_list|,
name|TreeType
operator|.
name|ACCESS_CONTROL
argument_list|,
name|tp
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|aggr
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|getTreePermission
argument_list|(
name|systemTree
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|rootTreePermission
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|tp
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|getChildPermission
argument_list|(
name|systemTree
operator|.
name|getName
argument_list|()
argument_list|,
name|getTreeProvider
argument_list|()
operator|.
name|asNodeState
argument_list|(
name|systemTree
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

