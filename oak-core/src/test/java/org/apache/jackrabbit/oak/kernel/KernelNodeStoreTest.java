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
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|mk
operator|.
name|core
operator|.
name|MicroKernelImpl
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
name|CoreValue
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
name|MemoryNodeState
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
name|CommitEditor
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
name|EmptyEditor
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
name|NodeStoreBranch
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
name|assertNull
import|;
end_import

begin_class
specifier|public
class|class
name|KernelNodeStoreTest
block|{
specifier|private
name|KernelNodeStore
name|store
decl_stmt|;
specifier|private
name|NodeState
name|root
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|MicroKernel
name|kernel
init|=
operator|new
name|MicroKernelImpl
argument_list|()
decl_stmt|;
name|String
name|jsop
init|=
literal|"+\"test\":{\"a\":1,\"b\":2,\"c\":3,"
operator|+
literal|"\"x\":{},\"y\":{},\"z\":{}}"
decl_stmt|;
name|kernel
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|jsop
argument_list|,
literal|null
argument_list|,
literal|"test data"
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|KernelNodeStore
argument_list|(
name|kernel
argument_list|)
expr_stmt|;
name|root
operator|=
name|store
operator|.
name|getRoot
argument_list|()
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
name|branch
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeStoreBranch
name|branch
init|=
name|store
operator|.
name|branch
argument_list|()
decl_stmt|;
name|NodeBuilder
name|rootBuilder
init|=
name|store
operator|.
name|getBuilder
argument_list|(
name|branch
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|NodeBuilder
name|testBuilder
init|=
name|rootBuilder
operator|.
name|getChildBuilder
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|NodeBuilder
name|newNodeBuilder
init|=
name|testBuilder
operator|.
name|getChildBuilder
argument_list|(
literal|"newNode"
argument_list|)
decl_stmt|;
name|testBuilder
operator|.
name|removeNode
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|CoreValue
name|fortyTwo
init|=
name|store
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|42
argument_list|)
decl_stmt|;
name|newNodeBuilder
operator|.
name|setProperty
argument_list|(
literal|"n"
argument_list|,
name|fortyTwo
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
name|assertNotNull
argument_list|(
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fortyTwo
argument_list|,
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
argument_list|()
argument_list|)
expr_stmt|;
comment|// Assert changes are not yet present in the branch
name|testState
operator|=
name|branch
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|branch
operator|.
name|setRoot
argument_list|(
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
comment|// Assert changes are present in the branch
name|testState
operator|=
name|branch
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fortyTwo
argument_list|,
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
argument_list|()
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
name|assertNull
argument_list|(
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|branch
operator|.
name|merge
argument_list|(
name|EmptyEditor
operator|.
name|INSTANCE
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
name|assertNotNull
argument_list|(
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|testState
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fortyTwo
argument_list|,
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
argument_list|()
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
name|store
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
name|NodeStore
name|store
parameter_list|,
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
name|store
operator|.
name|getBuilder
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|NodeBuilder
name|testBuilder
init|=
name|rootBuilder
operator|.
name|getChildBuilder
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|NodeBuilder
name|newNodeBuilder
init|=
name|testBuilder
operator|.
name|getChildBuilder
argument_list|(
literal|"newNode"
argument_list|)
decl_stmt|;
name|CoreValue
name|fortyTwo
init|=
name|store
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|42
argument_list|)
decl_stmt|;
name|newNodeBuilder
operator|.
name|setProperty
argument_list|(
literal|"n"
argument_list|,
name|fortyTwo
argument_list|)
expr_stmt|;
name|testBuilder
operator|.
name|removeNode
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|NodeState
name|newRoot
init|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeStoreBranch
name|branch
init|=
name|store
operator|.
name|branch
argument_list|()
decl_stmt|;
name|branch
operator|.
name|setRoot
argument_list|(
name|newRoot
argument_list|)
expr_stmt|;
name|branch
operator|.
name|merge
argument_list|(
name|EmptyEditor
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|store
operator|.
name|getRoot
argument_list|()
expr_stmt|;
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
name|assertNull
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
argument_list|)
expr_stmt|;
name|assertNotNull
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
argument_list|)
expr_stmt|;
name|assertNull
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
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fortyTwo
argument_list|,
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
argument_list|()
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
name|store
operator|.
name|setEditor
argument_list|(
operator|new
name|CommitEditor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeState
name|editCommit
parameter_list|(
name|NodeStore
name|store
parameter_list|,
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
name|store
operator|.
name|getBuilder
argument_list|(
name|after
argument_list|)
decl_stmt|;
name|NodeBuilder
name|testBuilder
init|=
name|store
operator|.
name|getBuilder
argument_list|(
name|after
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|testBuilder
operator|.
name|setNode
argument_list|(
literal|"fromHook"
argument_list|,
name|MemoryNodeState
operator|.
name|EMPTY_NODE
argument_list|)
expr_stmt|;
name|rootBuilder
operator|.
name|setNode
argument_list|(
literal|"test"
argument_list|,
name|testBuilder
operator|.
name|getNodeState
argument_list|()
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
name|store
operator|.
name|getBuilder
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|NodeBuilder
name|testBuilder
init|=
name|rootBuilder
operator|.
name|getChildBuilder
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|NodeBuilder
name|newNodeBuilder
init|=
name|testBuilder
operator|.
name|getChildBuilder
argument_list|(
literal|"newNode"
argument_list|)
decl_stmt|;
specifier|final
name|CoreValue
name|fortyTwo
init|=
name|store
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|42
argument_list|)
decl_stmt|;
name|newNodeBuilder
operator|.
name|setProperty
argument_list|(
literal|"n"
argument_list|,
name|fortyTwo
argument_list|)
expr_stmt|;
name|testBuilder
operator|.
name|removeNode
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|NodeState
name|newRoot
init|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeStoreBranch
name|branch
init|=
name|store
operator|.
name|branch
argument_list|()
decl_stmt|;
name|branch
operator|.
name|setRoot
argument_list|(
name|newRoot
argument_list|)
expr_stmt|;
name|branch
operator|.
name|merge
argument_list|(
name|EmptyEditor
operator|.
name|INSTANCE
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
name|assertNotNull
argument_list|(
name|test
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|test
operator|.
name|getChildNode
argument_list|(
literal|"fromHook"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|test
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fortyTwo
argument_list|,
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
argument_list|()
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
block|}
end_class

end_unit

