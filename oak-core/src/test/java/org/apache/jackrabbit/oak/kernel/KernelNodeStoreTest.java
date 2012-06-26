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
name|core
operator|.
name|AbstractOakTest
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
name|NodeStateBuilder
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
extends|extends
name|AbstractOakTest
block|{
specifier|private
specifier|final
name|CommitEditorDelegate
name|commitHookDelegate
init|=
operator|new
name|CommitEditorDelegate
argument_list|()
decl_stmt|;
specifier|private
name|NodeState
name|root
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|NodeState
name|createInitialState
parameter_list|(
name|MicroKernel
name|microKernel
parameter_list|)
block|{
name|String
name|jsop
init|=
literal|"+\"test\":{\"a\":1,\"b\":2,\"c\":3,"
operator|+
literal|"\"x\":{},\"y\":{},\"z\":{}}"
decl_stmt|;
name|microKernel
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
name|root
operator|=
name|store
operator|.
name|getRoot
argument_list|()
expr_stmt|;
return|return
name|root
return|;
block|}
annotation|@
name|Override
specifier|protected
name|CommitEditor
name|createCommitEditor
parameter_list|()
block|{
return|return
name|commitHookDelegate
return|;
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
name|NodeStateBuilder
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
name|NodeStateBuilder
name|testBuilder
init|=
name|store
operator|.
name|getBuilder
argument_list|(
name|root
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
literal|"newNode"
argument_list|,
name|MemoryNodeState
operator|.
name|EMPTY_NODE
argument_list|)
expr_stmt|;
name|testBuilder
operator|.
name|removeNode
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|NodeStateBuilder
name|newNodeBuilder
init|=
name|store
operator|.
name|getBuilder
argument_list|(
name|testBuilder
operator|.
name|getNodeState
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"newNode"
argument_list|)
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
name|setNode
argument_list|(
literal|"newNode"
argument_list|,
name|newNodeBuilder
operator|.
name|getNodeState
argument_list|()
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
argument_list|()
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
name|NodeState
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeStateBuilder
name|rootBuilder
init|=
name|store
operator|.
name|getBuilder
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|NodeState
name|test
init|=
name|root
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|NodeStateBuilder
name|testBuilder
init|=
name|store
operator|.
name|getBuilder
argument_list|(
name|test
argument_list|)
decl_stmt|;
name|NodeStateBuilder
name|newNodeBuilder
init|=
name|store
operator|.
name|getBuilder
argument_list|(
name|MemoryNodeState
operator|.
name|EMPTY_NODE
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
name|setNode
argument_list|(
literal|"newNode"
argument_list|,
name|newNodeBuilder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|testBuilder
operator|.
name|removeNode
argument_list|(
literal|"a"
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
specifier|final
name|NodeState
name|newRoot
init|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|commitWithEditor
argument_list|(
name|newRoot
argument_list|,
operator|new
name|EmptyEditor
argument_list|()
block|{
comment|// TODO: OAK-153 - use the Observer interface to observe content changes
comment|//            @Override
comment|//            public void afterCommit(NodeStore store, NodeState before, NodeState after) {
comment|//                assertNull(before.getChildNode("test").getChildNode("newNode"));
comment|//                assertNotNull(after.getChildNode("test").getChildNode("newNode"));
comment|//                assertNull(after.getChildNode("test").getChildNode("a"));
comment|//                assertEquals(fortyTwo, after.getChildNode("test").getChildNode("newNode").getProperty("n").getValue());
comment|//                assertEquals(newRoot, after);
comment|//            }
block|}
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
name|NodeStateBuilder
name|rootBuilder
init|=
name|store
operator|.
name|getBuilder
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|NodeState
name|test
init|=
name|root
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|NodeStateBuilder
name|testBuilder
init|=
name|store
operator|.
name|getBuilder
argument_list|(
name|test
argument_list|)
decl_stmt|;
name|NodeStateBuilder
name|newNodeBuilder
init|=
name|store
operator|.
name|getBuilder
argument_list|(
name|MemoryNodeState
operator|.
name|EMPTY_NODE
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
name|setNode
argument_list|(
literal|"newNode"
argument_list|,
name|newNodeBuilder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|testBuilder
operator|.
name|removeNode
argument_list|(
literal|"a"
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
specifier|final
name|NodeState
name|newRoot
init|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|commitWithEditor
argument_list|(
name|newRoot
argument_list|,
operator|new
name|EmptyEditor
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
name|NodeStateBuilder
name|rootBuilder
init|=
name|store
operator|.
name|getBuilder
argument_list|(
name|after
argument_list|)
decl_stmt|;
name|NodeStateBuilder
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
name|test
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
comment|//------------------------------------------------------------< private>---
specifier|private
name|void
name|commitWithEditor
parameter_list|(
name|NodeState
name|nodeState
parameter_list|,
name|CommitEditor
name|editor
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|commitHookDelegate
operator|.
name|set
argument_list|(
name|editor
argument_list|)
expr_stmt|;
try|try
block|{
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
name|nodeState
argument_list|)
expr_stmt|;
name|branch
operator|.
name|merge
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|commitHookDelegate
operator|.
name|set
argument_list|(
operator|new
name|EmptyEditor
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|CommitEditorDelegate
implements|implements
name|CommitEditor
block|{
specifier|private
name|CommitEditor
name|delegate
init|=
operator|new
name|EmptyEditor
argument_list|()
decl_stmt|;
specifier|public
name|void
name|set
parameter_list|(
name|CommitEditor
name|editor
parameter_list|)
block|{
name|delegate
operator|=
name|editor
expr_stmt|;
block|}
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
throws|throws
name|CommitFailedException
block|{
return|return
name|delegate
operator|.
name|editCommit
argument_list|(
name|store
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

