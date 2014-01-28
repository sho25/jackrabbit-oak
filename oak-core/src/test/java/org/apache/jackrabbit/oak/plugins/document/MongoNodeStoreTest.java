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
name|plugins
operator|.
name|document
package|;
end_package

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
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Semaphore
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
name|kernel
operator|.
name|KernelNodeState
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
name|document
operator|.
name|memory
operator|.
name|MemoryDocumentStore
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
name|document
operator|.
name|util
operator|.
name|TimingDocumentStoreWrapper
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
name|junit
operator|.
name|Test
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

begin_class
specifier|public
class|class
name|MongoNodeStoreTest
block|{
comment|// OAK-1254
annotation|@
name|Test
specifier|public
name|void
name|backgroundRead
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Semaphore
name|semaphore
init|=
operator|new
name|Semaphore
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|DocumentStore
name|docStore
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|DocumentStore
name|testStore
init|=
operator|new
name|TimingDocumentStoreWrapper
argument_list|(
name|docStore
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|invalidateCache
parameter_list|()
block|{
name|super
operator|.
name|invalidateCache
argument_list|()
expr_stmt|;
name|semaphore
operator|.
name|acquireUninterruptibly
argument_list|()
expr_stmt|;
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|final
name|MongoNodeStore
name|store1
init|=
operator|new
name|MongoMK
operator|.
name|Builder
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|testStore
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|MongoNodeStore
name|store2
init|=
operator|new
name|MongoMK
operator|.
name|Builder
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|docStore
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|2
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|store2
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"node2"
argument_list|)
expr_stmt|;
name|store2
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
comment|// force update of _lastRevs
name|store2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// at this point only node2 must not be visible
name|assertFalse
argument_list|(
name|store1
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
name|builder
operator|=
name|store1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|store1
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
decl_stmt|;
name|semaphore
operator|.
name|acquireUninterruptibly
argument_list|()
expr_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|store1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// sleep until 'background thread' invalidated cache
comment|// and is waiting for semaphore
while|while
condition|(
operator|!
name|semaphore
operator|.
name|hasQueuedThreads
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
comment|// must still not be visible at this state
try|try
block|{
name|assertFalse
argument_list|(
name|root
operator|.
name|hasChildNode
argument_list|(
literal|"node2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
comment|// background operations completed
name|root
operator|=
name|store1
operator|.
name|getRoot
argument_list|()
expr_stmt|;
comment|// now node2 is visible
name|assertTrue
argument_list|(
name|root
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
name|childNodeCache
parameter_list|()
throws|throws
name|Exception
block|{
name|MongoNodeStore
name|store
init|=
operator|new
name|MongoMK
operator|.
name|Builder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
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
name|int
name|max
init|=
call|(
name|int
call|)
argument_list|(
name|KernelNodeState
operator|.
name|MAX_CHILD_NODE_NAMES
operator|*
literal|1.5
argument_list|)
decl_stmt|;
name|SortedSet
argument_list|<
name|String
argument_list|>
name|children
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|max
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
literal|"c"
operator|+
name|i
decl_stmt|;
name|children
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
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
name|builder
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|String
name|name
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|children
argument_list|)
operator|.
name|get
argument_list|(
name|KernelNodeState
operator|.
name|MAX_CHILD_NODE_NAMES
operator|/
literal|2
argument_list|)
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
operator|.
name|remove
argument_list|()
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
name|int
name|numEntries
init|=
name|Iterables
operator|.
name|size
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNodeEntries
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|max
operator|-
literal|1
argument_list|,
name|numEntries
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

