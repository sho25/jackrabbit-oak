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
name|segment
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newConcurrentMap
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|intersection
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Uninterruptibles
operator|.
name|sleepUninterruptibly
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
operator|.
name|newSingleThreadExecutor
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MILLISECONDS
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Callable
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
name|ConcurrentMap
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
name|ExecutionException
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
name|ExecutorService
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
name|Future
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
name|TimeoutException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|base
operator|.
name|Suppliers
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
name|segment
operator|.
name|WriteOperationHandler
operator|.
name|WriteOperation
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
name|segment
operator|.
name|memory
operator|.
name|MemoryStore
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
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentBufferWriterPoolTest
block|{
specifier|private
specifier|final
name|MemoryStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|RecordId
name|rootId
init|=
name|store
operator|.
name|getRevisions
argument_list|()
operator|.
name|getHead
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SegmentBufferWriterPool
name|pool
init|=
operator|new
name|SegmentBufferWriterPool
argument_list|(
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
argument_list|,
name|store
operator|.
name|getReader
argument_list|()
argument_list|,
literal|""
argument_list|,
name|Suppliers
operator|.
name|ofInstance
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ExecutorService
index|[]
name|executors
init|=
operator|new
name|ExecutorService
index|[]
block|{
name|newSingleThreadExecutor
argument_list|()
block|,
name|newSingleThreadExecutor
argument_list|()
block|,
name|newSingleThreadExecutor
argument_list|()
block|}
decl_stmt|;
specifier|public
name|SegmentBufferWriterPoolTest
parameter_list|()
throws|throws
name|IOException
block|{ }
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
for|for
control|(
name|ExecutorService
name|executor
range|:
name|executors
control|)
block|{
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Future
argument_list|<
name|RecordId
argument_list|>
name|execute
parameter_list|(
specifier|final
name|WriteOperation
name|op
parameter_list|,
name|int
name|executor
parameter_list|)
block|{
return|return
name|executors
index|[
name|executor
index|]
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|RecordId
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RecordId
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|pool
operator|.
name|execute
argument_list|(
name|op
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
name|WriteOperation
name|createOp
parameter_list|(
specifier|final
name|String
name|key
parameter_list|,
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|SegmentBufferWriter
argument_list|>
name|map
parameter_list|)
block|{
return|return
operator|new
name|WriteOperation
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RecordId
name|execute
parameter_list|(
annotation|@
name|Nonnull
name|SegmentBufferWriter
name|writer
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|writer
argument_list|)
expr_stmt|;
return|return
name|rootId
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testThreadAffinity
parameter_list|()
throws|throws
name|IOException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|SegmentBufferWriter
argument_list|>
name|map1
init|=
name|newConcurrentMap
argument_list|()
decl_stmt|;
name|Future
argument_list|<
name|RecordId
argument_list|>
name|res1
init|=
name|execute
argument_list|(
name|createOp
argument_list|(
literal|"a"
argument_list|,
name|map1
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|RecordId
argument_list|>
name|res2
init|=
name|execute
argument_list|(
name|createOp
argument_list|(
literal|"b"
argument_list|,
name|map1
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|RecordId
argument_list|>
name|res3
init|=
name|execute
argument_list|(
name|createOp
argument_list|(
literal|"c"
argument_list|,
name|map1
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
comment|// Give the tasks some time to complete
name|sleepUninterruptibly
argument_list|(
literal|10
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rootId
argument_list|,
name|res1
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rootId
argument_list|,
name|res2
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rootId
argument_list|,
name|res3
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|map1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|SegmentBufferWriter
argument_list|>
name|map2
init|=
name|newConcurrentMap
argument_list|()
decl_stmt|;
name|Future
argument_list|<
name|RecordId
argument_list|>
name|res4
init|=
name|execute
argument_list|(
name|createOp
argument_list|(
literal|"a"
argument_list|,
name|map2
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|RecordId
argument_list|>
name|res5
init|=
name|execute
argument_list|(
name|createOp
argument_list|(
literal|"b"
argument_list|,
name|map2
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|RecordId
argument_list|>
name|res6
init|=
name|execute
argument_list|(
name|createOp
argument_list|(
literal|"c"
argument_list|,
name|map2
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
comment|// Give the tasks some time to complete
name|sleepUninterruptibly
argument_list|(
literal|10
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rootId
argument_list|,
name|res4
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rootId
argument_list|,
name|res5
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rootId
argument_list|,
name|res6
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|map2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|map1
argument_list|,
name|map2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFlush
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|SegmentBufferWriter
argument_list|>
name|map1
init|=
name|newConcurrentMap
argument_list|()
decl_stmt|;
name|Future
argument_list|<
name|RecordId
argument_list|>
name|res1
init|=
name|execute
argument_list|(
name|createOp
argument_list|(
literal|"a"
argument_list|,
name|map1
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|RecordId
argument_list|>
name|res2
init|=
name|execute
argument_list|(
name|createOp
argument_list|(
literal|"b"
argument_list|,
name|map1
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|RecordId
argument_list|>
name|res3
init|=
name|execute
argument_list|(
name|createOp
argument_list|(
literal|"c"
argument_list|,
name|map1
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
comment|// Give the tasks some time to complete
name|sleepUninterruptibly
argument_list|(
literal|10
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rootId
argument_list|,
name|res1
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rootId
argument_list|,
name|res2
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rootId
argument_list|,
name|res3
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|map1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|pool
operator|.
name|flush
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|SegmentBufferWriter
argument_list|>
name|map2
init|=
name|newConcurrentMap
argument_list|()
decl_stmt|;
name|Future
argument_list|<
name|RecordId
argument_list|>
name|res4
init|=
name|execute
argument_list|(
name|createOp
argument_list|(
literal|"a"
argument_list|,
name|map2
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|RecordId
argument_list|>
name|res5
init|=
name|execute
argument_list|(
name|createOp
argument_list|(
literal|"b"
argument_list|,
name|map2
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|RecordId
argument_list|>
name|res6
init|=
name|execute
argument_list|(
name|createOp
argument_list|(
literal|"c"
argument_list|,
name|map2
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
comment|// Give the tasks some time to complete
name|sleepUninterruptibly
argument_list|(
literal|10
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rootId
argument_list|,
name|res4
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rootId
argument_list|,
name|res5
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rootId
argument_list|,
name|res6
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|map2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|intersection
argument_list|(
name|newHashSet
argument_list|(
name|map1
operator|.
name|values
argument_list|()
argument_list|)
argument_list|,
name|newHashSet
argument_list|(
name|map2
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFlushBlocks
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|Future
argument_list|<
name|RecordId
argument_list|>
name|res
init|=
name|execute
argument_list|(
operator|new
name|WriteOperation
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|RecordId
name|execute
parameter_list|(
annotation|@
name|Nonnull
name|SegmentBufferWriter
name|writer
parameter_list|)
block|{
try|try
block|{
comment|// This should deadlock as flush waits for this write
comment|// operation to finish, which in this case contains the
comment|// call to flush itself.
name|executors
index|[
literal|1
index|]
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|pool
operator|.
name|flush
argument_list|(
name|store
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
operator|.
name|get
argument_list|(
literal|100
argument_list|,
name|MILLISECONDS
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
comment|// No deadlock -> null indicates test failure
block|}
catch|catch
parameter_list|(
name|InterruptedException
decl||
name|ExecutionException
name|ignore
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// No deadlock -> null indicates test failure
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|ignore
parameter_list|)
block|{
return|return
name|rootId
return|;
comment|// Deadlock -> rootId indicates test pass
block|}
block|}
block|}
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|rootId
argument_list|,
name|res
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

