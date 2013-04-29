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
name|state
operator|.
name|ChildNodeEntry
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

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
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|LargeKernelNodeStateTest
block|{
specifier|private
specifier|static
specifier|final
name|int
name|N
init|=
name|KernelNodeState
operator|.
name|MAX_CHILD_NODE_NAMES
decl_stmt|;
specifier|private
name|NodeState
name|state
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeStore
name|store
init|=
operator|new
name|KernelNodeStore
argument_list|(
operator|new
name|MicroKernelImpl
argument_list|()
argument_list|)
decl_stmt|;
name|NodeStoreBranch
name|branch
init|=
name|store
operator|.
name|branch
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|branch
operator|.
name|getHead
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|N
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"x"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|branch
operator|.
name|setRoot
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|state
operator|=
name|branch
operator|.
name|merge
argument_list|(
name|EmptyHook
operator|.
name|INSTANCE
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
name|state
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetChildNodeCount
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|N
operator|+
literal|1
argument_list|,
name|state
operator|.
name|getChildNodeCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetChildNode
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"x0"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"x1"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"x"
operator|+
name|N
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"x"
operator|+
operator|(
name|N
operator|+
literal|1
operator|)
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
name|testGetChildNodeEntries
parameter_list|()
block|{
name|long
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|state
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|N
operator|+
literal|1
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

