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
name|kernel
package|;
end_package

begin_import
import|import static
name|junit
operator|.
name|framework
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Type
operator|.
name|LONG
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
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|NodeStoreFixture
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
name|CommitFailedException
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
name|plugins
operator|.
name|memory
operator|.
name|MultiStringPropertyState
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
name|commit
operator|.
name|CommitHook
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
name|commit
operator|.
name|EmptyHook
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
name|commit
operator|.
name|Observer
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
name|DefaultNodeStateDiff
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
name|NodeBuilder
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
name|spi
operator|.
name|state
operator|.
name|NodeStore
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|value
operator|=
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|NodeStoreTest
block|{
annotation|@
name|Parameters
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|fixtures
parameter_list|()
block|{
name|Object
index|[]
index|[]
name|fixtures
init|=
operator|new
name|Object
index|[]
index|[]
block|{
block|{
name|NodeStoreFixture
operator|.
name|MK_IMPL
block|}
block|,
block|{
name|NodeStoreFixture
operator|.
name|MONGO_MK
block|}
block|,
block|{
name|NodeStoreFixture
operator|.
name|SEGMENT_MK
block|}
block|,         }
decl_stmt|;
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|fixtures
argument_list|)
return|;
block|}
specifier|private
name|NodeStore
name|store
decl_stmt|;
specifier|private
name|NodeState
name|root
decl_stmt|;
specifier|private
name|NodeStoreFixture
name|fixture
decl_stmt|;
specifier|public
name|NodeStoreTest
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|this
operator|.
name|fixture
operator|=
name|fixture
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|=
name|fixture
operator|.
name|createNodeStore
argument_list|()
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|test
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|test
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|test
operator|.
name|setProperty
argument_list|(
literal|"b"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|test
operator|.
name|setProperty
argument_list|(
literal|"c"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|test
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|test
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|test
operator|.
name|child
argument_list|(
literal|"z"
argument_list|)
expr_stmt|;
name|root
operator|=
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|root
operator|=
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|fixture
operator|.
name|dispose
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getRoot
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|root
argument_list|,
name|store
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|root
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|root
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
argument_list|,
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|root
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"any"
argument_list|)
argument_list|,
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"any"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|root
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|root
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"any"
argument_list|)
argument_list|,
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"any"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|simpleMerge
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|testBuilder
init|=
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|NodeBuilder
name|newNodeBuilder
init|=
name|testBuilder
operator|.
name|child
argument_list|(
literal|"newNode"
argument_list|)
decl_stmt|;
name|testBuilder
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|newNodeBuilder
operator|.
name|setProperty
argument_list|(
literal|"n"
argument_list|,
literal|42
argument_list|)
expr_stmt|;
comment|// Assert changes are present in the builder
name|NodeState
name|testState
init|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42
argument_list|,
operator|(
name|long
operator|)
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"n"
argument_list|)
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
comment|// Assert changes are not yet present in the trunk
name|testState
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|rootBuilder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Assert changes are present in the trunk
name|testState
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42
argument_list|,
operator|(
name|long
operator|)
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"n"
argument_list|)
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|afterCommitHook
parameter_list|()
throws|throws
name|CommitFailedException
block|{
comment|// this test only works with a KernelNodeStore
name|assumeTrue
argument_list|(
name|store
operator|instanceof
name|KernelNodeStore
argument_list|)
expr_stmt|;
specifier|final
name|NodeState
index|[]
name|states
init|=
operator|new
name|NodeState
index|[
literal|2
index|]
decl_stmt|;
comment|// { before, after }
operator|(
operator|(
name|KernelNodeStore
operator|)
name|store
operator|)
operator|.
name|setObserver
argument_list|(
operator|new
name|Observer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|contentChanged
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|states
index|[
literal|0
index|]
operator|=
name|before
expr_stmt|;
name|states
index|[
literal|1
index|]
operator|=
name|after
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|rootBuilder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|testBuilder
init|=
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|NodeBuilder
name|newNodeBuilder
init|=
name|testBuilder
operator|.
name|child
argument_list|(
literal|"newNode"
argument_list|)
decl_stmt|;
name|newNodeBuilder
operator|.
name|setProperty
argument_list|(
literal|"n"
argument_list|,
literal|42
argument_list|)
expr_stmt|;
name|testBuilder
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|rootBuilder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|NodeState
name|newRoot
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
comment|// triggers the observer
name|NodeState
name|before
init|=
name|states
index|[
literal|0
index|]
decl_stmt|;
name|NodeState
name|after
init|=
name|states
index|[
literal|1
index|]
decl_stmt|;
name|assertNotNull
argument_list|(
name|before
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|after
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|before
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|after
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|after
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42
argument_list|,
operator|(
name|long
operator|)
name|after
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"n"
argument_list|)
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newRoot
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|beforeCommitHook
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeState
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|rootBuilder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|testBuilder
init|=
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|NodeBuilder
name|newNodeBuilder
init|=
name|testBuilder
operator|.
name|child
argument_list|(
literal|"newNode"
argument_list|)
decl_stmt|;
name|newNodeBuilder
operator|.
name|setProperty
argument_list|(
literal|"n"
argument_list|,
literal|42
argument_list|)
expr_stmt|;
name|testBuilder
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|rootBuilder
argument_list|,
operator|new
name|CommitHook
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeState
name|processCommit
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|after
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|testBuilder
init|=
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|testBuilder
operator|.
name|child
argument_list|(
literal|"fromHook"
argument_list|)
expr_stmt|;
return|return
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|NodeState
name|test
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|test
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|.
name|getChildNode
argument_list|(
literal|"fromHook"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|test
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42
argument_list|,
operator|(
name|long
operator|)
name|test
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"n"
argument_list|)
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|test
argument_list|,
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|manyChildNodes
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|parent
init|=
name|root
operator|.
name|child
argument_list|(
literal|"parent"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|KernelNodeState
operator|.
name|MAX_CHILD_NODE_NAMES
condition|;
name|i
operator|++
control|)
block|{
name|parent
operator|.
name|child
argument_list|(
literal|"child-"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|merge
argument_list|(
name|root
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|NodeState
name|base
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|root
operator|=
name|base
operator|.
name|builder
argument_list|()
expr_stmt|;
name|parent
operator|=
name|root
operator|.
name|child
argument_list|(
literal|"parent"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|child
argument_list|(
literal|"child-new"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|root
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Diff
name|diff
init|=
operator|new
name|Diff
argument_list|()
decl_stmt|;
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|diff
operator|.
name|removed
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|diff
operator|.
name|added
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"child-new"
argument_list|,
name|diff
operator|.
name|added
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|base
operator|=
name|store
operator|.
name|getRoot
argument_list|()
expr_stmt|;
name|root
operator|=
name|base
operator|.
name|builder
argument_list|()
expr_stmt|;
name|parent
operator|=
name|root
operator|.
name|getChildNode
argument_list|(
literal|"parent"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|getChildNode
argument_list|(
literal|"child-new"
argument_list|)
operator|.
name|moveTo
argument_list|(
name|parent
argument_list|,
literal|"child-moved"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|root
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|diff
operator|=
operator|new
name|Diff
argument_list|()
expr_stmt|;
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|diff
operator|.
name|removed
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"child-new"
argument_list|,
name|diff
operator|.
name|removed
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|diff
operator|.
name|added
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"child-moved"
argument_list|,
name|diff
operator|.
name|added
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|base
operator|=
name|store
operator|.
name|getRoot
argument_list|()
expr_stmt|;
name|root
operator|=
name|base
operator|.
name|builder
argument_list|()
expr_stmt|;
name|parent
operator|=
name|root
operator|.
name|child
argument_list|(
literal|"parent"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|child
argument_list|(
literal|"child-moved"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|child
argument_list|(
literal|"child-moved"
argument_list|)
operator|.
name|setProperty
argument_list|(
operator|new
name|MultiStringPropertyState
argument_list|(
literal|"bar"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"value"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|root
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|diff
operator|=
operator|new
name|Diff
argument_list|()
expr_stmt|;
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|diff
operator|.
name|removed
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|diff
operator|.
name|added
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|diff
operator|.
name|addedProperties
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|diff
operator|.
name|addedProperties
operator|.
name|contains
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|diff
operator|.
name|addedProperties
operator|.
name|contains
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|base
operator|=
name|store
operator|.
name|getRoot
argument_list|()
expr_stmt|;
name|root
operator|=
name|base
operator|.
name|builder
argument_list|()
expr_stmt|;
name|parent
operator|=
name|root
operator|.
name|child
argument_list|(
literal|"parent"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|setProperty
argument_list|(
operator|new
name|MultiStringPropertyState
argument_list|(
literal|"bar"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"value"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|root
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|diff
operator|=
operator|new
name|Diff
argument_list|()
expr_stmt|;
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|diff
operator|.
name|removed
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|diff
operator|.
name|added
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|diff
operator|.
name|addedProperties
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|diff
operator|.
name|addedProperties
operator|.
name|contains
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|diff
operator|.
name|addedProperties
operator|.
name|contains
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|base
operator|=
name|store
operator|.
name|getRoot
argument_list|()
expr_stmt|;
name|root
operator|=
name|base
operator|.
name|builder
argument_list|()
expr_stmt|;
name|parent
operator|=
name|root
operator|.
name|child
argument_list|(
literal|"parent"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|getChildNode
argument_list|(
literal|"child-moved"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|root
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|diff
operator|=
operator|new
name|Diff
argument_list|()
expr_stmt|;
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|diff
operator|.
name|removed
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|diff
operator|.
name|added
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"child-moved"
argument_list|,
name|diff
operator|.
name|removed
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|moveToSelf
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
operator|.
name|moveTo
argument_list|(
name|builder
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|oak965
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeStore
name|store1
init|=
name|init
argument_list|(
name|fixture
operator|.
name|createNodeStore
argument_list|()
argument_list|)
decl_stmt|;
name|NodeStore
name|store2
init|=
name|init
argument_list|(
name|fixture
operator|.
name|createNodeStore
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|NodeState
name|tree1
init|=
name|store1
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeState
name|tree2
init|=
name|store2
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|tree1
operator|.
name|equals
argument_list|(
name|tree2
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fixture
operator|.
name|dispose
argument_list|(
name|store1
argument_list|)
expr_stmt|;
name|fixture
operator|.
name|dispose
argument_list|(
name|store2
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|NodeStore
name|init
parameter_list|(
name|NodeStore
name|store
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"root"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|store
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|merge
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeState
name|base
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder1
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder2
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder1
operator|.
name|setChildNode
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|builder2
operator|.
name|setChildNode
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder1
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasChildNode
argument_list|(
literal|"node1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasChildNode
argument_list|(
literal|"node2"
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder2
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasChildNode
argument_list|(
literal|"node1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasChildNode
argument_list|(
literal|"node2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|compareAgainstBaseState0
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|compareAgainstBaseState
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|compareAgainstBaseState20
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|compareAgainstBaseState
argument_list|(
literal|20
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|compareAgainstBaseState100
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|compareAgainstBaseState
argument_list|(
name|KernelNodeState
operator|.
name|MAX_CHILD_NODE_NAMES
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|compareAgainstBaseState
parameter_list|(
name|int
name|childNodeCount
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeState
name|before
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|before
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|childNodeCount
condition|;
name|k
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"c"
operator|+
name|k
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|child
argument_list|(
literal|":bar"
argument_list|)
operator|.
name|child
argument_list|(
literal|"quz"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|Diff
name|diff
init|=
operator|new
name|Diff
argument_list|()
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|diff
operator|.
name|removed
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|childNodeCount
operator|+
literal|1
argument_list|,
name|diff
operator|.
name|added
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|diff
operator|.
name|addedProperties
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|Diff
extends|extends
name|DefaultNodeStateDiff
block|{
name|List
argument_list|<
name|String
argument_list|>
name|addedProperties
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|added
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|removed
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|added
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|removed
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
name|addedProperties
operator|.
name|add
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

