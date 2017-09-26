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
name|document
package|;
end_package

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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|collect
operator|.
name|Lists
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
name|Maps
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
name|Iterables
operator|.
name|size
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
name|HOURS
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
name|Collection
operator|.
name|NODES
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
name|NodeDocument
operator|.
name|NUM_REVS_THRESHOLD
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
name|PREV_SPLIT_FACTOR
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

begin_comment
comment|/**  * Runs a DocumentMK revision GC interleaved with a document split.  * Test for OAK-1791.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|VersionGCWithSplitTest
block|{
specifier|private
name|DocumentStoreFixture
name|fixture
decl_stmt|;
specifier|private
name|Clock
name|clock
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Thread
argument_list|,
name|Semaphore
argument_list|>
name|updateLocks
init|=
name|Maps
operator|.
name|newIdentityHashMap
argument_list|()
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|store
decl_stmt|;
specifier|private
name|VersionGarbageCollector
name|gc
decl_stmt|;
specifier|public
name|VersionGCWithSplitTest
parameter_list|(
name|DocumentStoreFixture
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
name|Parameterized
operator|.
name|Parameters
specifier|public
specifier|static
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|fixtures
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|fixtures
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|fixtures
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|DocumentStoreFixture
operator|.
name|MemoryFixture
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|DocumentStoreFixture
name|mongo
init|=
operator|new
name|DocumentStoreFixture
operator|.
name|MongoFixture
argument_list|()
decl_stmt|;
if|if
condition|(
name|mongo
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
name|fixtures
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|mongo
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|fixtures
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|DocumentStore
name|docStore
init|=
name|fixture
operator|.
name|createDocumentStore
argument_list|()
decl_stmt|;
name|DocumentStore
name|testStore
init|=
operator|new
name|TestStore
argument_list|(
name|docStore
argument_list|)
decl_stmt|;
name|clock
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
name|store
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|testStore
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|gc
operator|=
name|store
operator|.
name|getVersionGarbageCollector
argument_list|()
expr_stmt|;
comment|//Baseline the clock
name|clock
operator|.
name|waitUntil
argument_list|(
name|Revision
operator|.
name|getCurrentTimestamp
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
throws|throws
name|Exception
block|{
name|store
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|fixture
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|gcWithConcurrentSplit
parameter_list|()
throws|throws
name|Exception
block|{
name|Revision
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
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
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
expr_stmt|;
specifier|final
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|DocumentStore
name|docStore
init|=
name|store
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|docStore
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|id
argument_list|)
operator|.
name|getPreviousRanges
argument_list|()
operator|.
name|size
argument_list|()
operator|<
name|PREV_SPLIT_FACTOR
condition|)
block|{
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
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|++
operator|%
name|NUM_REVS_THRESHOLD
operator|==
literal|0
condition|)
block|{
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
block|}
block|}
comment|// wait one hour
name|clock
operator|.
name|waitUntil
argument_list|(
name|Revision
operator|.
name|getCurrentTimestamp
argument_list|()
operator|+
name|HOURS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
operator|+
literal|2
operator|*
name|SECONDS
operator|.
name|toMillis
argument_list|(
name|MODIFIED_IN_SECS_RESOLUTION
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|VersionGarbageCollector
operator|.
name|VersionGCStats
argument_list|>
name|stats
init|=
operator|new
name|AtomicReference
argument_list|<
name|VersionGarbageCollector
operator|.
name|VersionGCStats
argument_list|>
argument_list|()
decl_stmt|;
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
try|try
block|{
name|stats
operator|.
name|set
argument_list|(
name|gc
operator|.
name|gc
argument_list|(
literal|1
argument_list|,
name|HOURS
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
comment|// block gc thread when it attempts to write
name|Semaphore
name|gcLock
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|updateLocks
operator|.
name|put
argument_list|(
name|t
argument_list|,
name|gcLock
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|gcLock
operator|.
name|hasQueuedThreads
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// perform more changes until intermediate docs are created
while|while
condition|(
name|docStore
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|id
argument_list|)
operator|.
name|getPreviousRanges
argument_list|()
operator|.
name|size
argument_list|()
operator|>=
name|PREV_SPLIT_FACTOR
condition|)
block|{
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
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|++
operator|%
name|NUM_REVS_THRESHOLD
operator|==
literal|0
condition|)
block|{
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
block|}
block|}
comment|// let gc thread continue
name|gcLock
operator|.
name|release
argument_list|()
expr_stmt|;
comment|// the first split doc disconnect will be based on the main document
comment|// with 10 previous entries (before the intermediate doc was created).
comment|// the next 9 disconnect calls will see the updated main document
comment|// pointing to the intermediate doc. those 9 split docs will be
comment|// disconnected from there.
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|stats
operator|.
name|get
argument_list|()
operator|.
name|splitDocGCCount
argument_list|)
expr_stmt|;
name|NodeDocument
name|doc
init|=
name|docStore
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|id
argument_list|)
decl_stmt|;
comment|// there must only be one stale entry because 9 other split docs were
comment|// disconnected from the new intermediate split doc
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|doc
operator|.
name|getStalePrev
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|addSplitCandidate
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// now there must not be any stale prev entries
name|doc
operator|=
name|docStore
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|getStalePrev
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|valueMap
init|=
name|doc
operator|.
name|getValueMap
argument_list|(
literal|"prop"
argument_list|)
decl_stmt|;
comment|// there must be 101 revisions left. one in the main document and one
comment|// split document with 100 revisions created after the GC was triggered
name|assertEquals
argument_list|(
name|NUM_REVS_THRESHOLD
operator|+
literal|1
argument_list|,
name|valueMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// also count them individually
name|assertEquals
argument_list|(
name|NUM_REVS_THRESHOLD
operator|+
literal|1
argument_list|,
name|size
argument_list|(
name|valueMap
operator|.
name|entrySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|merge
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
throws|throws
name|CommitFailedException
block|{
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
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
class|class
name|TestStore
extends|extends
name|TimingDocumentStoreWrapper
block|{
specifier|private
specifier|final
name|DocumentStore
name|docStore
decl_stmt|;
specifier|public
name|TestStore
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
name|this
operator|.
name|docStore
operator|=
name|base
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|update
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|,
specifier|final
name|UpdateOp
name|updateOp
parameter_list|)
block|{
name|runLocked
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|docStore
operator|.
name|update
argument_list|(
name|collection
argument_list|,
name|keys
argument_list|,
name|updateOp
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|createOrUpdate
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
specifier|final
name|UpdateOp
name|update
parameter_list|)
block|{
specifier|final
name|AtomicReference
argument_list|<
name|T
argument_list|>
name|ref
init|=
operator|new
name|AtomicReference
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
name|runLocked
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ref
operator|.
name|set
argument_list|(
name|docStore
operator|.
name|createOrUpdate
argument_list|(
name|collection
argument_list|,
name|update
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|ref
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|findAndUpdate
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
specifier|final
name|UpdateOp
name|update
parameter_list|)
block|{
specifier|final
name|AtomicReference
argument_list|<
name|T
argument_list|>
name|ref
init|=
operator|new
name|AtomicReference
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
name|runLocked
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ref
operator|.
name|set
argument_list|(
name|docStore
operator|.
name|findAndUpdate
argument_list|(
name|collection
argument_list|,
name|update
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|ref
operator|.
name|get
argument_list|()
return|;
block|}
specifier|private
name|void
name|runLocked
parameter_list|(
name|Runnable
name|run
parameter_list|)
block|{
name|Thread
name|t
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
name|Semaphore
name|s
init|=
name|updateLocks
operator|.
name|get
argument_list|(
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|s
operator|.
name|acquireUninterruptibly
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|run
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|s
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

