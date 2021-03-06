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
name|io
operator|.
name|InputStream
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
name|TimeUnit
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|util
operator|.
name|concurrent
operator|.
name|Monitor
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
name|concurrent
operator|.
name|ExecutorCloser
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
name|json
operator|.
name|JsopDiff
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
name|plugins
operator|.
name|document
operator|.
name|util
operator|.
name|Utils
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
name|AbstractBlob
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
name|CommitInfo
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
name|stats
operator|.
name|Clock
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
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|SECONDS
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
name|plugins
operator|.
name|document
operator|.
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS_RESOLUTION
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
name|plugins
operator|.
name|document
operator|.
name|TestUtils
operator|.
name|merge
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
name|plugins
operator|.
name|document
operator|.
name|util
operator|.
name|Utils
operator|.
name|getIdFromPath
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Tests DocumentNodeStore on various DocumentStore back-ends.  */
end_comment

begin_class
specifier|public
class|class
name|DocumentNodeStoreIT
extends|extends
name|AbstractDocumentStoreTest
block|{
annotation|@
name|Rule
specifier|public
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
specifier|public
name|DocumentNodeStoreIT
parameter_list|(
name|DocumentStoreFixture
name|dsf
parameter_list|)
block|{
name|super
argument_list|(
name|dsf
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
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
name|markDocumentsForCleanup
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|markDocumentsForCleanup
parameter_list|()
block|{
for|for
control|(
name|NodeDocument
name|doc
range|:
name|Utils
operator|.
name|getAllDocuments
argument_list|(
name|ds
argument_list|)
control|)
block|{
name|removeMe
operator|.
name|add
argument_list|(
name|doc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|modifiedResetWithDiff
parameter_list|()
throws|throws
name|Exception
block|{
name|Clock
name|clock
init|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
decl_stmt|;
name|clock
operator|.
name|waitUntil
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|Revision
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|DocumentStore
name|docStore
init|=
operator|new
name|NonDisposingDocumentStore
argument_list|(
name|ds
argument_list|)
decl_stmt|;
comment|// use a builder with a no-op diff cache to simulate a
comment|// cache miss when the diff is made later in the test
name|DocumentNodeStore
name|ns1
init|=
operator|new
name|TestBuilder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|docStore
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|removeMeClusterNodes
operator|.
name|add
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder1
init|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder1
operator|.
name|child
argument_list|(
literal|"node"
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|getIdFromPath
argument_list|(
literal|"/node"
argument_list|)
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
operator|<
name|DocumentMK
operator|.
name|MANY_CHILDREN_THRESHOLD
condition|;
name|i
operator|++
control|)
block|{
name|builder1
operator|.
name|child
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|getIdFromPath
argument_list|(
literal|"/node/node-"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ns1
operator|.
name|merge
argument_list|(
name|builder1
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
comment|// make sure commit is visible to other node store instance
name|ns1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|DocumentNodeStore
name|ns2
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
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
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|removeMeClusterNodes
operator|.
name|add
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder2
init|=
name|ns2
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder2
operator|.
name|child
argument_list|(
literal|"node"
argument_list|)
operator|.
name|child
argument_list|(
literal|"child-a"
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|getIdFromPath
argument_list|(
literal|"/node/child-a"
argument_list|)
argument_list|)
expr_stmt|;
name|ns2
operator|.
name|merge
argument_list|(
name|builder2
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
comment|// wait at least _modified resolution. in reality the wait may
comment|// not be necessary. e.g. when the clock passes the resolution boundary
comment|// exactly at this time
name|clock
operator|.
name|waitUntil
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|SECONDS
operator|.
name|toMillis
argument_list|(
name|MODIFIED_IN_SECS_RESOLUTION
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|builder1
operator|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder1
operator|.
name|child
argument_list|(
literal|"node"
argument_list|)
operator|.
name|child
argument_list|(
literal|"child-b"
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|getIdFromPath
argument_list|(
literal|"/node/child-b"
argument_list|)
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|merge
argument_list|(
name|builder1
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
comment|// remember root for diff
name|DocumentNodeState
name|root1
init|=
name|ns1
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|builder1
operator|=
name|root1
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder1
operator|.
name|child
argument_list|(
literal|"node"
argument_list|)
operator|.
name|child
argument_list|(
literal|"child-c"
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|getIdFromPath
argument_list|(
literal|"/node/child-c"
argument_list|)
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|merge
argument_list|(
name|builder1
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
comment|// remember root for diff
name|DocumentNodeState
name|root2
init|=
name|ns1
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|ns1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|ns2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|JsopDiff
name|diff
init|=
operator|new
name|JsopDiff
argument_list|(
literal|""
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ns1
operator|.
name|compare
argument_list|(
name|root2
argument_list|,
name|root1
argument_list|,
name|diff
argument_list|)
expr_stmt|;
comment|// must report /node as changed
name|assertEquals
argument_list|(
literal|"^\"node\":{}"
argument_list|,
name|diff
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|ns2
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|blockingBlob
parameter_list|()
throws|throws
name|Exception
block|{
name|ExecutorService
name|updateExecutor
init|=
name|newSingleThreadExecutor
argument_list|()
decl_stmt|;
name|ExecutorService
name|commitExecutor
init|=
name|newSingleThreadExecutor
argument_list|()
decl_stmt|;
name|DocumentStore
name|docStore
init|=
operator|new
name|NonDisposingDocumentStore
argument_list|(
name|ds
argument_list|)
decl_stmt|;
name|DocumentNodeStore
name|store
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|docStore
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|removeMeClusterNodes
operator|.
name|add
argument_list|(
literal|""
operator|+
name|store
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
comment|// A blob whose stream blocks on read
name|BlockingBlob
name|blockingBlob
init|=
operator|new
name|BlockingBlob
argument_list|()
decl_stmt|;
comment|// Use a background thread to add the blocking blob to a property
name|updateExecutor
operator|.
name|submit
argument_list|(
call|(
name|Callable
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
block|{
name|DocumentNodeState
name|root
operator|=
name|store
operator|.
name|getRoot
argument_list|()
block|;
name|NodeBuilder
name|builder
operator|=
name|root
operator|.
name|builder
argument_list|()
block|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"blockingBlob"
argument_list|,
name|blockingBlob
argument_list|)
block|;
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
block|;
return|return
literal|null
return|;
block|}
block|)
empty_stmt|;
comment|// Wait for reading on the blob to block
name|assertTrue
argument_list|(
name|blockingBlob
operator|.
name|waitForRead
argument_list|(
literal|1
argument_list|,
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Commit something else in another background thread
name|Future
argument_list|<
name|Void
argument_list|>
name|committed
init|=
name|commitExecutor
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
block|{
name|DocumentNodeState
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
decl_stmt|;
comment|// Commit should not get blocked by the blob blocked on reading
try|try
block|{
name|committed
operator|.
name|get
argument_list|(
literal|5
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Commit must not block"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|blockingBlob
operator|.
name|unblock
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
operator|new
name|ExecutorCloser
argument_list|(
name|commitExecutor
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|ExecutorCloser
argument_list|(
name|updateExecutor
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|/**      *  A blob that blocks on read until unblocked      */
end_comment

begin_class
class|class
name|BlockingBlob
extends|extends
name|AbstractBlob
block|{
specifier|private
specifier|final
name|AtomicBoolean
name|blocking
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Monitor
name|readMonitor
init|=
operator|new
name|Monitor
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|reading
init|=
literal|false
decl_stmt|;
name|boolean
name|waitForRead
parameter_list|(
name|int
name|time
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|readMonitor
operator|.
name|enter
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|readMonitor
operator|.
name|waitFor
argument_list|(
operator|new
name|Monitor
operator|.
name|Guard
argument_list|(
name|readMonitor
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisfied
parameter_list|()
block|{
return|return
name|reading
return|;
block|}
block|}
argument_list|,
name|time
argument_list|,
name|unit
argument_list|)
return|;
block|}
finally|finally
block|{
name|readMonitor
operator|.
name|leave
argument_list|()
expr_stmt|;
block|}
block|}
name|void
name|unblock
parameter_list|()
block|{
name|blocking
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|InputStream
name|getNewStream
parameter_list|()
block|{
return|return
operator|new
name|InputStream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
block|{
while|while
condition|(
name|blocking
operator|.
name|get
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|reading
condition|)
block|{
name|readMonitor
operator|.
name|enter
argument_list|()
expr_stmt|;
try|try
block|{
name|reading
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|readMonitor
operator|.
name|leave
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
end_class

begin_class
specifier|private
specifier|static
class|class
name|NonDisposingDocumentStore
extends|extends
name|TimingDocumentStoreWrapper
block|{
name|NonDisposingDocumentStore
parameter_list|(
name|DocumentStore
name|base
parameter_list|)
block|{
name|super
argument_list|(
name|base
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|()
block|{
comment|// do not dispose yet
block|}
block|}
end_class

begin_class
specifier|private
class|class
name|TestBuilder
extends|extends
name|DocumentNodeStoreBuilder
argument_list|<
name|TestBuilder
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|DiffCache
name|getDiffCache
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
return|return
name|AmnesiaDiffCache
operator|.
name|INSTANCE
return|;
block|}
block|}
end_class

unit|}
end_unit

