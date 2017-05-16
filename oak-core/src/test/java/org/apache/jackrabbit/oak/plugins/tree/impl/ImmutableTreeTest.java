begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|plugins
operator|.
name|tree
operator|.
name|impl
package|;
end_package

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
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
name|Lists
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
name|util
operator|.
name|NodeUtil
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
name|util
operator|.
name|Text
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

begin_class
specifier|public
class|class
name|ImmutableTreeTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|HIDDEN_PATH
init|=
literal|"/oak:index/acPrincipalName/:index"
decl_stmt|;
specifier|private
name|ImmutableTree
name|immutable
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
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
literal|"/"
argument_list|)
decl_stmt|;
name|NodeUtil
name|node
init|=
operator|new
name|NodeUtil
argument_list|(
name|tree
argument_list|)
decl_stmt|;
name|node
operator|.
name|addChild
argument_list|(
literal|"x"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"y"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"z"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|Tree
name|orderable
init|=
name|node
operator|.
name|addChild
argument_list|(
literal|"orderable"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|orderable
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|immutable
operator|=
operator|new
name|ImmutableTree
argument_list|(
operator|(
operator|(
name|AbstractTree
operator|)
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|root
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPath
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|immutable
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|immutable
operator|=
name|immutable
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/x"
argument_list|,
name|immutable
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|immutable
operator|=
name|immutable
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/x/y"
argument_list|,
name|immutable
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|immutable
operator|=
name|immutable
operator|.
name|getChild
argument_list|(
literal|"z"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/x/y/z"
argument_list|,
name|immutable
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetNodeState
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|immutable
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Tree
name|child
range|:
name|immutable
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|child
operator|instanceof
name|ImmutableTree
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
operator|(
operator|(
name|ImmutableTree
operator|)
name|child
operator|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRootIsRoot
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|immutable
operator|.
name|isRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRootGetParent
parameter_list|()
block|{
name|immutable
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetParent
parameter_list|()
block|{
name|ImmutableTree
name|child
init|=
name|immutable
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|child
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|child
operator|.
name|getParent
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|UnsupportedOperationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetParentDisconnected
parameter_list|()
block|{
name|ImmutableTree
name|child
init|=
name|immutable
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|ImmutableTree
name|disconnected
init|=
operator|new
name|ImmutableTree
argument_list|(
name|ImmutableTree
operator|.
name|ParentProvider
operator|.
name|UNSUPPORTED
argument_list|,
name|child
operator|.
name|getName
argument_list|()
argument_list|,
name|child
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|disconnected
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetName
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
name|immutable
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHiddenGetName
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Text
operator|.
name|getName
argument_list|(
name|HIDDEN_PATH
argument_list|)
argument_list|,
name|getHiddenTree
argument_list|(
name|immutable
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonExistingGetName
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"nonExisting"
argument_list|,
name|immutable
operator|.
name|getChild
argument_list|(
literal|"nonExisting"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRootGetName
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|immutable
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExists
parameter_list|()
block|{
name|ImmutableTree
name|child
init|=
name|immutable
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|child
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHiddenExists
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|getHiddenTree
argument_list|(
name|immutable
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonExisting
parameter_list|()
block|{
name|ImmutableTree
name|child
init|=
name|immutable
operator|.
name|getChild
argument_list|(
literal|"nonExisting"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|child
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRootGetStatus
parameter_list|()
block|{
name|assertSame
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|UNCHANGED
argument_list|,
name|immutable
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetStatus
parameter_list|()
block|{
name|assertSame
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|UNCHANGED
argument_list|,
name|immutable
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHiddenGetStatus
parameter_list|()
block|{
name|assertSame
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|UNCHANGED
argument_list|,
name|getHiddenTree
argument_list|(
name|immutable
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonExistingGetStatus
parameter_list|()
block|{
name|assertSame
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|UNCHANGED
argument_list|,
name|immutable
operator|.
name|getChild
argument_list|(
literal|"nonExisting"
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasChild
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|immutable
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasHiddenChild
parameter_list|()
block|{
name|ImmutableTree
name|parent
init|=
operator|(
name|ImmutableTree
operator|)
name|TreeUtil
operator|.
name|getTree
argument_list|(
name|immutable
argument_list|,
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|HIDDEN_PATH
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|parent
operator|.
name|hasChild
argument_list|(
name|Text
operator|.
name|getName
argument_list|(
name|HIDDEN_PATH
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetHiddenNode
parameter_list|()
block|{
name|ImmutableTree
name|hidden
init|=
name|getHiddenTree
argument_list|(
name|immutable
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|hidden
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasHiddenProperty
parameter_list|()
block|{
name|ImmutableTree
name|orderable
init|=
name|immutable
operator|.
name|getChild
argument_list|(
literal|"orderable"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|orderable
operator|.
name|hasProperty
argument_list|(
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetHiddenProperty
parameter_list|()
block|{
name|ImmutableTree
name|orderable
init|=
name|immutable
operator|.
name|getChild
argument_list|(
literal|"orderable"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|orderable
operator|.
name|getProperty
argument_list|(
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPropertyStatus
parameter_list|()
block|{
name|ImmutableTree
name|orderable
init|=
name|immutable
operator|.
name|getChild
argument_list|(
literal|"orderable"
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|UNCHANGED
argument_list|,
name|orderable
operator|.
name|getPropertyStatus
argument_list|(
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetProperties
parameter_list|()
block|{
name|ImmutableTree
name|orderable
init|=
name|immutable
operator|.
name|getChild
argument_list|(
literal|"orderable"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|propNames
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
argument_list|,
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
for|for
control|(
name|PropertyState
name|ps
range|:
name|orderable
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|propNames
operator|.
name|remove
argument_list|(
name|ps
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|orderable
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPropertyCount
parameter_list|()
block|{
name|ImmutableTree
name|orderable
init|=
name|immutable
operator|.
name|getChild
argument_list|(
literal|"orderable"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|orderable
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|orderBefore
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/x/y/z"
argument_list|)
decl_stmt|;
name|NodeUtil
name|n
init|=
operator|new
name|NodeUtil
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|n
operator|.
name|addChild
argument_list|(
literal|"node1"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|n
operator|.
name|addChild
argument_list|(
literal|"node2"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|n
operator|.
name|addChild
argument_list|(
literal|"node3"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|t
operator|.
name|getChild
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|t
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ImmutableTree
name|tree
init|=
operator|new
name|ImmutableTree
argument_list|(
operator|(
operator|(
name|AbstractTree
operator|)
name|t
operator|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|assertSequence
argument_list|(
name|tree
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node1"
argument_list|,
literal|"node2"
argument_list|,
literal|"node3"
argument_list|)
expr_stmt|;
name|t
operator|.
name|getChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
operator|new
name|ImmutableTree
argument_list|(
operator|(
operator|(
name|AbstractTree
operator|)
name|t
operator|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|tree
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node1"
argument_list|,
literal|"node3"
argument_list|,
literal|"node2"
argument_list|)
expr_stmt|;
name|t
operator|.
name|getChild
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
operator|new
name|ImmutableTree
argument_list|(
operator|(
operator|(
name|AbstractTree
operator|)
name|t
operator|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|assertSequence
argument_list|(
name|tree
operator|.
name|getChildren
argument_list|()
argument_list|,
literal|"node3"
argument_list|,
literal|"node2"
argument_list|,
literal|"node1"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|ImmutableTree
name|getHiddenTree
parameter_list|(
annotation|@
name|Nonnull
name|ImmutableTree
name|immutable
parameter_list|)
block|{
return|return
operator|(
name|ImmutableTree
operator|)
name|TreeUtil
operator|.
name|getTree
argument_list|(
name|immutable
argument_list|,
name|HIDDEN_PATH
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|assertSequence
parameter_list|(
name|Iterable
argument_list|<
name|Tree
argument_list|>
name|trees
parameter_list|,
name|String
modifier|...
name|names
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|actual
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|trees
argument_list|,
operator|new
name|Function
argument_list|<
name|Tree
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|Tree
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|names
argument_list|)
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

