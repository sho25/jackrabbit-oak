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
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|plugins
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
name|plugins
operator|.
name|tree
operator|.
name|RootFactory
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
name|ImmutableTree
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
name|CompositeTreePermissionTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|Root
name|readOnlyRoot
decl_stmt|;
specifier|private
name|ImmutableTree
name|rootTree
decl_stmt|;
specifier|private
name|AggregatedPermissionProvider
name|fullScopeProvider
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
argument_list|)
decl_stmt|;
name|rootNode
operator|.
name|addChild
argument_list|(
literal|"test"
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
name|readOnlyRoot
operator|=
name|RootFactory
operator|.
name|createReadOnlyRoot
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|rootTree
operator|=
operator|(
name|ImmutableTree
operator|)
name|readOnlyRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|fullScopeProvider
operator|=
operator|new
name|FullScopeProvider
argument_list|(
name|readOnlyRoot
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|root
operator|.
name|getTree
argument_list|(
literal|"/test"
argument_list|)
operator|.
name|remove
argument_list|()
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
specifier|private
name|AggregatedPermissionProvider
index|[]
name|getProviders
parameter_list|(
name|AggregatedPermissionProvider
modifier|...
name|providers
parameter_list|)
block|{
return|return
name|providers
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{
name|TreePermission
name|rootTp
init|=
name|CompositeTreePermission
operator|.
name|create
argument_list|(
name|rootTree
argument_list|,
name|getProviders
argument_list|()
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|TreePermission
operator|.
name|EMPTY
argument_list|,
name|rootTp
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rootTp
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
name|testSingle
parameter_list|()
block|{
name|Class
argument_list|<
name|?
extends|extends
name|TreePermission
argument_list|>
name|expected
init|=
name|fullScopeProvider
operator|.
name|getTreePermission
argument_list|(
name|rootTree
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|TreePermission
name|rootTp
init|=
name|CompositeTreePermission
operator|.
name|create
argument_list|(
name|rootTree
argument_list|,
name|getProviders
argument_list|(
name|fullScopeProvider
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|rootTp
operator|instanceof
name|CompositeTreePermission
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|rootTp
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|TreePermission
name|testTp
init|=
name|rootTp
operator|.
name|getChildPermission
argument_list|(
literal|"test"
argument_list|,
name|rootTree
operator|.
name|getChild
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|testTp
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultiple
parameter_list|()
block|{
name|TreePermission
name|rootTp
init|=
name|CompositeTreePermission
operator|.
name|create
argument_list|(
name|rootTree
argument_list|,
name|getProviders
argument_list|(
name|fullScopeProvider
argument_list|,
name|fullScopeProvider
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rootTp
operator|instanceof
name|CompositeTreePermission
argument_list|)
expr_stmt|;
name|TreePermission
name|testTp
init|=
name|rootTp
operator|.
name|getChildPermission
argument_list|(
literal|"test"
argument_list|,
name|rootTree
operator|.
name|getChild
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|testTp
operator|instanceof
name|CompositeTreePermission
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultipleNoRecurse
parameter_list|()
block|{
name|TreePermission
name|rootTp
init|=
name|CompositeTreePermission
operator|.
name|create
argument_list|(
name|rootTree
argument_list|,
name|getProviders
argument_list|(
operator|new
name|NoScopeProvider
argument_list|()
argument_list|,
operator|new
name|NoScopeProvider
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rootTp
operator|instanceof
name|CompositeTreePermission
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|TreePermission
operator|.
name|EMPTY
argument_list|,
name|rootTp
operator|.
name|getChildPermission
argument_list|(
literal|"test"
argument_list|,
name|rootTree
operator|.
name|getChild
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultipleToSingle
parameter_list|()
block|{
name|TreePermission
name|rootTp
init|=
name|CompositeTreePermission
operator|.
name|create
argument_list|(
name|rootTree
argument_list|,
name|getProviders
argument_list|(
name|fullScopeProvider
argument_list|,
operator|new
name|NoScopeProvider
argument_list|()
argument_list|,
operator|new
name|NoScopeProvider
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rootTp
operator|instanceof
name|CompositeTreePermission
argument_list|)
expr_stmt|;
name|NodeState
name|childState
init|=
name|rootTree
operator|.
name|getChild
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|TreePermission
name|testTp
init|=
name|rootTp
operator|.
name|getChildPermission
argument_list|(
literal|"test"
argument_list|,
name|childState
argument_list|)
decl_stmt|;
name|TreePermission
name|expected
init|=
name|fullScopeProvider
operator|.
name|getTreePermission
argument_list|(
name|rootTree
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
operator|.
name|getChildPermission
argument_list|(
literal|"test"
argument_list|,
name|childState
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|getClass
argument_list|()
argument_list|,
name|testTp
operator|.
name|getClass
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
throws|throws
name|Exception
block|{
name|TreePermission
name|rootTp
init|=
name|CompositeTreePermission
operator|.
name|create
argument_list|(
name|rootTree
argument_list|,
name|getProviders
argument_list|(
name|fullScopeProvider
argument_list|,
name|fullScopeProvider
argument_list|)
argument_list|)
decl_stmt|;
name|Field
name|f
init|=
name|CompositeTreePermission
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"canRead"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Object
name|canRead
init|=
name|f
operator|.
name|get
argument_list|(
name|rootTp
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|canRead
argument_list|)
expr_stmt|;
name|rootTp
operator|.
name|canRead
argument_list|()
expr_stmt|;
name|canRead
operator|=
name|f
operator|.
name|get
argument_list|(
name|rootTp
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|canRead
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParentNoRecourse
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|f
init|=
name|CompositeTreePermission
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"providers"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TreePermission
name|rootTp
init|=
name|CompositeTreePermission
operator|.
name|create
argument_list|(
name|rootTree
argument_list|,
name|getProviders
argument_list|(
operator|new
name|NoScopeProvider
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|TreePermission
operator|.
name|NO_RECOURSE
argument_list|,
name|rootTp
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

