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
name|Executors
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
name|atomic
operator|.
name|AtomicLong
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
name|VersionGarbageCollector
operator|.
name|VersionGCStats
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
name|gc
operator|.
name|GCMonitor
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
name|AfterClass
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
name|MINUTES
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

begin_import
import|import static
name|org
operator|.
name|slf4j
operator|.
name|helpers
operator|.
name|MessageFormatter
operator|.
name|arrayFormat
import|;
end_import

begin_class
specifier|public
class|class
name|VersionGCTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
specifier|private
name|ExecutorService
name|execService
decl_stmt|;
specifier|private
name|TestStore
name|store
init|=
operator|new
name|TestStore
argument_list|()
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns
decl_stmt|;
specifier|private
name|VersionGarbageCollector
name|gc
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
name|execService
operator|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
expr_stmt|;
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
name|ns
operator|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|setLeaseCheckMode
argument_list|(
name|LeaseCheckMode
operator|.
name|DISABLED
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|store
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
comment|// create test content
name|createNode
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|removeNode
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
comment|// wait one hour
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|HOURS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|gc
operator|=
name|ns
operator|.
name|getVersionGarbageCollector
argument_list|()
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
name|execService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|execService
operator|.
name|awaitTermination
argument_list|(
literal|1
argument_list|,
name|MINUTES
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|resetClock
parameter_list|()
block|{
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
name|failParallelGC
parameter_list|()
throws|throws
name|Exception
block|{
comment|// block gc call
name|store
operator|.
name|semaphore
operator|.
name|acquireUninterruptibly
argument_list|()
expr_stmt|;
name|Future
argument_list|<
name|VersionGCStats
argument_list|>
name|stats
init|=
name|gc
argument_list|()
decl_stmt|;
name|boolean
name|gcBlocked
init|=
literal|false
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|store
operator|.
name|semaphore
operator|.
name|hasQueuedThreads
argument_list|()
condition|)
block|{
name|gcBlocked
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|gcBlocked
argument_list|)
expr_stmt|;
comment|// now try to trigger another GC
try|try
block|{
name|gc
operator|.
name|gc
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must throw an IOException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"already running"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|store
operator|.
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
name|stats
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|cancel
parameter_list|()
throws|throws
name|Exception
block|{
comment|// block gc call
name|store
operator|.
name|semaphore
operator|.
name|acquireUninterruptibly
argument_list|()
expr_stmt|;
name|Future
argument_list|<
name|VersionGCStats
argument_list|>
name|stats
init|=
name|gc
argument_list|()
decl_stmt|;
name|boolean
name|gcBlocked
init|=
literal|false
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|store
operator|.
name|semaphore
operator|.
name|hasQueuedThreads
argument_list|()
condition|)
block|{
name|gcBlocked
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|gcBlocked
argument_list|)
expr_stmt|;
comment|// now cancel the GC
name|gc
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|store
operator|.
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|stats
operator|.
name|get
argument_list|()
operator|.
name|canceled
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cancelMustNotUpdateLastOldestTimeStamp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// get previous entry from SETTINGS
name|String
name|versionGCId
init|=
literal|"versionGC"
decl_stmt|;
name|String
name|lastOldestTimeStampProp
init|=
literal|"lastOldestTimeStamp"
decl_stmt|;
name|Document
name|statusBefore
init|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|SETTINGS
argument_list|,
name|versionGCId
argument_list|)
decl_stmt|;
comment|// block gc call
name|store
operator|.
name|semaphore
operator|.
name|acquireUninterruptibly
argument_list|()
expr_stmt|;
name|Future
argument_list|<
name|VersionGCStats
argument_list|>
name|stats
init|=
name|gc
argument_list|()
decl_stmt|;
name|boolean
name|gcBlocked
init|=
literal|false
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|store
operator|.
name|semaphore
operator|.
name|hasQueuedThreads
argument_list|()
condition|)
block|{
name|gcBlocked
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|gcBlocked
argument_list|)
expr_stmt|;
comment|// now cancel the GC
name|gc
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|store
operator|.
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|stats
operator|.
name|get
argument_list|()
operator|.
name|canceled
argument_list|)
expr_stmt|;
comment|// ensure a canceled GC doesn't update that versionGC SETTINGS entry
name|Document
name|statusAfter
init|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|SETTINGS
argument_list|,
literal|"versionGC"
argument_list|)
decl_stmt|;
if|if
condition|(
name|statusBefore
operator|==
literal|null
condition|)
block|{
name|assertNull
argument_list|(
name|statusAfter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNotNull
argument_list|(
name|statusAfter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"canceled GC shouldn't change the "
operator|+
name|lastOldestTimeStampProp
operator|+
literal|" property on "
operator|+
name|versionGCId
operator|+
literal|" settings entry"
argument_list|,
name|statusBefore
operator|.
name|get
argument_list|(
name|lastOldestTimeStampProp
argument_list|)
argument_list|,
name|statusAfter
operator|.
name|get
argument_list|(
name|lastOldestTimeStampProp
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|getInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|gc
operator|.
name|gc
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
expr_stmt|;
name|gc
operator|.
name|getInfo
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|gcMonitorStatusUpdates
parameter_list|()
throws|throws
name|Exception
block|{
name|TestGCMonitor
name|monitor
init|=
operator|new
name|TestGCMonitor
argument_list|()
decl_stmt|;
name|gc
operator|.
name|setGCMonitor
argument_list|(
name|monitor
argument_list|)
expr_stmt|;
name|gc
operator|.
name|gc
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"INITIALIZING"
argument_list|,
literal|"COLLECTING"
argument_list|,
literal|"CHECKING"
argument_list|,
literal|"COLLECTING"
argument_list|,
literal|"DELETING"
argument_list|,
literal|"SORTING"
argument_list|,
literal|"DELETING"
argument_list|,
literal|"UPDATING"
argument_list|,
literal|"SPLITS_CLEANUP"
argument_list|,
literal|"IDLE"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|monitor
operator|.
name|getStatusMessages
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|gcMonitorInfoMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|TestGCMonitor
name|monitor
init|=
operator|new
name|TestGCMonitor
argument_list|()
decl_stmt|;
name|gc
operator|.
name|setGCMonitor
argument_list|(
name|monitor
argument_list|)
expr_stmt|;
name|gc
operator|.
name|gc
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|infoMessages
init|=
name|monitor
operator|.
name|getInfoMessages
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|infoMessages
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|infoMessages
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Start "
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|infoMessages
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Looking at revisions"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|infoMessages
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Revision garbage collection finished"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|findVersionGC
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|.
name|findVersionGC
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|gc
operator|.
name|gc
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
expr_stmt|;
comment|// must only read once
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|store
operator|.
name|findVersionGC
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|recommendationsOnHugeBacklog
parameter_list|()
throws|throws
name|Exception
block|{
name|VersionGCOptions
name|options
init|=
name|gc
operator|.
name|getOptions
argument_list|()
decl_stmt|;
specifier|final
name|long
name|oneYearAgo
init|=
name|ns
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
name|TimeUnit
operator|.
name|DAYS
operator|.
name|toMillis
argument_list|(
literal|365
argument_list|)
decl_stmt|;
specifier|final
name|long
name|twelveTimesTheLimit
init|=
name|options
operator|.
name|collectLimit
operator|*
literal|12
decl_stmt|;
name|VersionGCSupport
name|localgcsupport
init|=
operator|new
name|VersionGCSupport
argument_list|(
name|ns
operator|.
name|getDocumentStore
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|getOldestDeletedOnceTimestamp
parameter_list|(
name|Clock
name|clock
parameter_list|,
name|long
name|precisionMs
parameter_list|)
block|{
return|return
name|oneYearAgo
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getDeletedOnceCount
parameter_list|()
block|{
return|return
name|twelveTimesTheLimit
return|;
block|}
block|}
decl_stmt|;
name|VersionGCRecommendations
name|rec
init|=
operator|new
name|VersionGCRecommendations
argument_list|(
literal|86400L
argument_list|,
name|ns
argument_list|,
name|localgcsupport
argument_list|,
name|options
argument_list|,
operator|new
name|TestGCMonitor
argument_list|()
argument_list|)
decl_stmt|;
comment|// should select a duration of roughly one month
name|long
name|duration
init|=
name|rec
operator|.
name|scope
operator|.
name|getDurationMs
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|duration
operator|<=
name|TimeUnit
operator|.
name|DAYS
operator|.
name|toMillis
argument_list|(
literal|33
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|duration
operator|>=
name|TimeUnit
operator|.
name|DAYS
operator|.
name|toMillis
argument_list|(
literal|28
argument_list|)
argument_list|)
expr_stmt|;
name|VersionGCStats
name|stats
init|=
operator|new
name|VersionGCStats
argument_list|()
decl_stmt|;
name|stats
operator|.
name|limitExceeded
operator|=
literal|true
expr_stmt|;
name|rec
operator|.
name|evaluate
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stats
operator|.
name|needRepeat
argument_list|)
expr_stmt|;
name|rec
operator|=
operator|new
name|VersionGCRecommendations
argument_list|(
literal|86400L
argument_list|,
name|ns
argument_list|,
name|localgcsupport
argument_list|,
name|options
argument_list|,
operator|new
name|TestGCMonitor
argument_list|()
argument_list|)
expr_stmt|;
comment|// new duration should be half
name|long
name|nduration
init|=
name|rec
operator|.
name|scope
operator|.
name|getDurationMs
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|nduration
operator|==
name|duration
operator|/
literal|2
argument_list|)
expr_stmt|;
block|}
comment|// OAK-7378
annotation|@
name|Test
specifier|public
name|void
name|recommendedInterval
parameter_list|()
throws|throws
name|Exception
block|{
name|AtomicLong
name|deletedOnceCountCalls
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|// override the gc with a custom VersionGCSupport
name|gc
operator|=
operator|new
name|VersionGarbageCollector
argument_list|(
name|ns
argument_list|,
operator|new
name|VersionGCSupport
argument_list|(
name|store
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|getDeletedOnceCount
parameter_list|()
block|{
name|deletedOnceCountCalls
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
name|Iterables
operator|.
name|size
argument_list|(
name|Utils
operator|.
name|getSelectedDocuments
argument_list|(
name|store
argument_list|,
name|NodeDocument
operator|.
name|DELETED_ONCE
argument_list|,
literal|1
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// run first RGC
name|gc
operator|.
name|gc
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
expr_stmt|;
comment|// afterwards there should be no more calls to getDeletedOnceCount()
name|deletedOnceCountCalls
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// try a couple of runs every five seconds to simulate continuous RGC
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|advanceClock
argument_list|(
literal|5
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
name|gc
operator|.
name|gc
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|deletedOnceCountCalls
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Future
argument_list|<
name|VersionGCStats
argument_list|>
name|gc
parameter_list|()
block|{
comment|// run gc in a separate thread
return|return
name|execService
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|VersionGCStats
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|VersionGCStats
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|gc
operator|.
name|gc
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
name|void
name|removeNode
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|ns
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
name|name
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createNode
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|ns
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
name|name
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
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
name|void
name|advanceClock
parameter_list|(
name|long
name|time
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|Clock
name|c
init|=
name|ns
operator|.
name|getClock
argument_list|()
decl_stmt|;
name|c
operator|.
name|waitUntil
argument_list|(
name|c
operator|.
name|getTime
argument_list|()
operator|+
name|unit
operator|.
name|toMillis
argument_list|(
name|time
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|TestStore
extends|extends
name|MemoryDocumentStore
block|{
name|Semaphore
name|semaphore
init|=
operator|new
name|Semaphore
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|AtomicLong
name|findVersionGC
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|query
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|fromKey
parameter_list|,
name|String
name|toKey
parameter_list|,
name|String
name|indexedProperty
parameter_list|,
name|long
name|startValue
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|semaphore
operator|.
name|acquireUninterruptibly
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|super
operator|.
name|query
argument_list|(
name|collection
argument_list|,
name|fromKey
argument_list|,
name|toKey
argument_list|,
name|indexedProperty
argument_list|,
name|startValue
argument_list|,
name|limit
argument_list|)
return|;
block|}
finally|finally
block|{
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
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
name|find
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|SETTINGS
operator|&&
name|key
operator|.
name|equals
argument_list|(
literal|"versionGC"
argument_list|)
condition|)
block|{
name|findVersionGC
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
return|return
name|super
operator|.
name|find
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
return|;
block|}
block|}
specifier|private
class|class
name|TestGCMonitor
implements|implements
name|GCMonitor
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|infoMessages
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|statusMessages
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|info
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{
name|this
operator|.
name|infoMessages
operator|.
name|add
argument_list|(
name|arrayFormat
argument_list|(
name|message
argument_list|,
name|arguments
argument_list|)
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|warn
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|exception
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|skipped
parameter_list|(
name|String
name|reason
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|compacted
parameter_list|()
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|cleaned
parameter_list|(
name|long
name|reclaimedSize
parameter_list|,
name|long
name|currentSize
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|updateStatus
parameter_list|(
name|String
name|status
parameter_list|)
block|{
name|this
operator|.
name|statusMessages
operator|.
name|add
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getInfoMessages
parameter_list|()
block|{
return|return
name|this
operator|.
name|infoMessages
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getStatusMessages
parameter_list|()
block|{
return|return
name|this
operator|.
name|statusMessages
return|;
block|}
block|}
block|}
end_class

end_unit

