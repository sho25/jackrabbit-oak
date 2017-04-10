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
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|CountDownLatch
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
name|Iterators
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

begin_class
specifier|public
class|class
name|VersionGCDeletionTest
block|{
specifier|private
name|Clock
name|clock
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|store
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|clock
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
comment|// baseline the clock
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
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
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
name|deleteParentLast
parameter_list|()
throws|throws
name|Exception
block|{
name|TestDocumentStore
name|ts
init|=
operator|new
name|TestDocumentStore
argument_list|()
decl_stmt|;
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
name|ts
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
name|NodeBuilder
name|b1
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b1
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
name|long
name|maxAge
init|=
literal|1
decl_stmt|;
comment|//hours
name|long
name|delta
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|//Remove x/y
name|NodeBuilder
name|b2
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b2
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
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|//3. Check that deleted doc does get collected post maxAge
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
name|maxAge
operator|*
literal|2
argument_list|)
operator|+
name|delta
argument_list|)
expr_stmt|;
name|VersionGarbageCollector
name|gc
init|=
name|store
operator|.
name|getVersionGarbageCollector
argument_list|()
decl_stmt|;
comment|//4. Ensure that while GC is being run /x gets removed but failure occurs
comment|//for /x/y. At least attempt that! Once issue is fixed the list would be
comment|//sorted again by VersionGC and then /x would always come after /x/y
try|try
block|{
name|ts
operator|.
name|throwException
operator|=
literal|true
expr_stmt|;
name|gc
operator|.
name|gc
argument_list|(
name|maxAge
operator|*
literal|2
argument_list|,
name|HOURS
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception should be thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|ignore
parameter_list|)
block|{          }
name|ts
operator|.
name|throwException
operator|=
literal|false
expr_stmt|;
name|gc
operator|.
name|gc
argument_list|(
name|maxAge
operator|*
literal|2
argument_list|,
name|HOURS
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|ts
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|"2:/x/y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|ts
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|"1:/x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|leaveResurrectedNodesAlone
parameter_list|()
throws|throws
name|Exception
block|{
name|TestDocumentStore
name|ts
init|=
operator|new
name|TestDocumentStore
argument_list|()
decl_stmt|;
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
name|ts
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
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/x"
argument_list|)
decl_stmt|;
name|NodeBuilder
name|b1
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b1
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
comment|// Remove x
name|NodeBuilder
name|b2
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b2
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
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|NodeDocument
name|d2
init|=
name|ts
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|d2
operator|.
name|wasDeletedOnce
argument_list|()
argument_list|)
expr_stmt|;
comment|// Re-add x
name|NodeBuilder
name|b3
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b3
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b3
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
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|NodeDocument
name|d3
init|=
name|ts
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|d3
operator|.
name|wasDeletedOnce
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|maxAge
init|=
literal|1
decl_stmt|;
comment|//hours
name|long
name|delta
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|// 3. Check that resurrected doc does not get collected post maxAge
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
name|maxAge
operator|*
literal|2
argument_list|)
operator|+
name|delta
argument_list|)
expr_stmt|;
name|VersionGarbageCollector
name|gc
init|=
name|store
operator|.
name|getVersionGarbageCollector
argument_list|()
decl_stmt|;
name|VersionGCStats
name|stats
init|=
name|gc
operator|.
name|gc
argument_list|(
name|maxAge
operator|*
literal|2
argument_list|,
name|HOURS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stats
operator|.
name|updateResurrectedGCCount
argument_list|)
expr_stmt|;
name|NodeDocument
name|d4
init|=
name|ts
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|d4
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|d4
operator|.
name|wasDeletedOnce
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|deleteLargeNumber
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|noOfDocsToDelete
init|=
literal|10000
decl_stmt|;
name|DocumentStore
name|ts
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
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
operator|new
name|MemoryDocumentStore
argument_list|()
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
name|NodeBuilder
name|b1
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
name|xb
init|=
name|b1
operator|.
name|child
argument_list|(
literal|"x"
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
operator|<
name|noOfDocsToDelete
condition|;
name|i
operator|++
control|)
block|{
name|xb
operator|.
name|child
argument_list|(
literal|"a"
operator|+
name|i
argument_list|)
operator|.
name|child
argument_list|(
literal|"b"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|merge
argument_list|(
name|b1
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
name|long
name|maxAge
init|=
literal|1
decl_stmt|;
comment|//hours
name|long
name|delta
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|//Remove x/y
name|NodeBuilder
name|b2
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b2
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
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|//3. Check that deleted doc does get collected post maxAge
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
name|maxAge
operator|*
literal|2
argument_list|)
operator|+
name|delta
argument_list|)
expr_stmt|;
name|VersionGarbageCollector
name|gc
init|=
name|store
operator|.
name|getVersionGarbageCollector
argument_list|()
decl_stmt|;
name|gc
operator|.
name|setOptions
argument_list|(
name|gc
operator|.
name|getOptions
argument_list|()
operator|.
name|withOverflowToDiskThreshold
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|VersionGCStats
name|stats
init|=
name|gc
operator|.
name|gc
argument_list|(
name|maxAge
operator|*
literal|2
argument_list|,
name|HOURS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|noOfDocsToDelete
operator|*
literal|2
operator|+
literal|1
argument_list|,
name|stats
operator|.
name|deletedDocGCCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|noOfDocsToDelete
argument_list|,
name|stats
operator|.
name|deletedLeafDocGCCount
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|ts
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|"1:/x"
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
name|noOfDocsToDelete
condition|;
name|i
operator|++
control|)
block|{
name|assertNull
argument_list|(
name|ts
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|"2:/a"
operator|+
name|i
operator|+
literal|"/b"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|ts
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|"1:/a"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|gcWithPathsHavingNewLine
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|noOfDocsToDelete
init|=
literal|200
decl_stmt|;
name|DocumentStore
name|ts
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
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
operator|new
name|MemoryDocumentStore
argument_list|()
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
name|NodeBuilder
name|b1
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
name|xb
init|=
name|b1
operator|.
name|child
argument_list|(
literal|"x"
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
operator|<
name|noOfDocsToDelete
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|xb
operator|.
name|child
argument_list|(
literal|"a"
operator|+
name|i
argument_list|)
operator|.
name|child
argument_list|(
literal|"b"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|xb
operator|.
name|child
argument_list|(
literal|"a-1"
argument_list|)
operator|.
name|child
argument_list|(
literal|"b\r"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b1
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
name|long
name|maxAge
init|=
literal|1
decl_stmt|;
comment|//hours
name|long
name|delta
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|//Remove x/y
name|NodeBuilder
name|b2
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b2
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
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|//3. Check that deleted doc does get collected post maxAge
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
name|maxAge
operator|*
literal|2
argument_list|)
operator|+
name|delta
argument_list|)
expr_stmt|;
name|VersionGarbageCollector
name|gc
init|=
name|store
operator|.
name|getVersionGarbageCollector
argument_list|()
decl_stmt|;
name|gc
operator|.
name|setOptions
argument_list|(
name|gc
operator|.
name|getOptions
argument_list|()
operator|.
name|withOverflowToDiskThreshold
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|VersionGCStats
name|stats
init|=
name|gc
operator|.
name|gc
argument_list|(
name|maxAge
operator|*
literal|2
argument_list|,
name|HOURS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|noOfDocsToDelete
operator|*
literal|2
operator|+
literal|1
argument_list|,
name|stats
operator|.
name|deletedDocGCCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|noOfDocsToDelete
argument_list|,
name|stats
operator|.
name|deletedLeafDocGCCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|gcForPreviousDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|ts
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
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
name|ts
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
name|NodeBuilder
name|b1
decl_stmt|;
name|NodeBuilder
name|xb
decl_stmt|;
comment|//Create/remove "/x/split" sufficient times to split it
name|boolean
name|create
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|create
operator|||
name|i
operator|<
name|NodeDocument
operator|.
name|NUM_REVS_THRESHOLD
condition|;
name|i
operator|++
control|)
block|{
name|b1
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|xb
operator|=
name|b1
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"split"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|create
condition|)
block|{
name|xb
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|store
operator|.
name|merge
argument_list|(
name|b1
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
name|create
operator|=
operator|!
name|create
expr_stmt|;
block|}
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|//Count split docs
name|NodeDocument
name|doc
init|=
name|ts
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|"2:/x/split"
argument_list|)
decl_stmt|;
name|int
name|splitDocCount
init|=
name|Iterators
operator|.
name|size
argument_list|(
name|doc
operator|.
name|getAllPreviousDocs
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|maxAge
init|=
literal|1
decl_stmt|;
comment|//hours
name|long
name|delta
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|//Remove "/x"
name|NodeBuilder
name|b2
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b2
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
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|//Pass some time and run GC
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
name|maxAge
operator|*
literal|2
argument_list|)
operator|+
name|delta
argument_list|)
expr_stmt|;
name|VersionGarbageCollector
name|gc
init|=
name|store
operator|.
name|getVersionGarbageCollector
argument_list|()
decl_stmt|;
name|VersionGCStats
name|stats
init|=
name|gc
operator|.
name|gc
argument_list|(
name|maxAge
operator|*
literal|2
argument_list|,
name|HOURS
argument_list|)
decl_stmt|;
comment|//Asset GC stats
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stats
operator|.
name|deletedDocGCCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|splitDocCount
argument_list|,
name|stats
operator|.
name|splitDocGCCount
argument_list|)
expr_stmt|;
comment|//check if the deleted docs are really gone after GC
name|assertNull
argument_list|(
name|ts
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|"1:/x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|ts
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|"2:/x/split"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// OAK-2420
annotation|@
name|Test
specifier|public
name|void
name|queryWhileDocsAreRemoved
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Thread
name|currentThread
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
specifier|final
name|Semaphore
name|queries
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|ready
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|MemoryDocumentStore
name|ms
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
block|{
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
name|int
name|limit
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
operator|&&
name|Thread
operator|.
name|currentThread
argument_list|()
operator|!=
name|currentThread
condition|)
block|{
name|ready
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|queries
operator|.
name|acquireUninterruptibly
argument_list|()
expr_stmt|;
block|}
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
name|limit
argument_list|)
return|;
block|}
block|}
decl_stmt|;
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
name|ms
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
comment|// create nodes
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
name|node
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"node"
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
operator|<
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|node
operator|.
name|child
argument_list|(
literal|"c-"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
expr_stmt|;
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
comment|// remove nodes
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
name|node
operator|=
name|builder
operator|.
name|child
argument_list|(
literal|"node"
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
literal|90
condition|;
name|i
operator|++
control|)
block|{
name|node
operator|.
name|getChildNode
argument_list|(
literal|"c-"
operator|+
name|i
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
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
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
comment|// fill caches
name|NodeState
name|n
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"node"
argument_list|)
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|n
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|expected
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|110
argument_list|,
name|expected
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// invalidate the nodeChildren cache only
name|store
operator|.
name|invalidateNodeChildrenCache
argument_list|()
expr_stmt|;
name|Future
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|f
init|=
name|newSingleThreadExecutor
argument_list|()
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|NodeState
name|n
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"node"
argument_list|)
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|n
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|names
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// run GC once the reader thread is collecting documents
name|ready
operator|.
name|await
argument_list|()
expr_stmt|;
name|VersionGarbageCollector
name|gc
init|=
name|store
operator|.
name|getVersionGarbageCollector
argument_list|()
decl_stmt|;
name|VersionGCStats
name|stats
init|=
name|gc
operator|.
name|gc
argument_list|(
literal|30
argument_list|,
name|MINUTES
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|90
argument_list|,
name|stats
operator|.
name|deletedDocGCCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|90
argument_list|,
name|stats
operator|.
name|deletedLeafDocGCCount
argument_list|)
expr_stmt|;
name|queries
operator|.
name|release
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|f
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|names
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
specifier|static
class|class
name|TestDocumentStore
extends|extends
name|MemoryDocumentStore
block|{
name|boolean
name|throwException
decl_stmt|;
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|remove
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
name|throwException
operator|&&
literal|"2:/x/y"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
name|super
operator|.
name|remove
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|List
argument_list|<
name|T
argument_list|>
name|result
init|=
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
decl_stmt|;
comment|//Ensure that /x comes before /x/y
if|if
condition|(
name|NodeDocument
operator|.
name|DELETED_ONCE
operator|.
name|equals
argument_list|(
name|indexedProperty
argument_list|)
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
operator|(
name|List
argument_list|<
name|NodeDocument
argument_list|>
operator|)
name|result
argument_list|,
operator|new
name|NodeDocComparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
comment|/**      * Ensures that NodeDocument with path  /x/y /x/y/z /x get sorted to      * /x /x/y /x/y/z      */
specifier|private
specifier|static
class|class
name|NodeDocComparator
implements|implements
name|Comparator
argument_list|<
name|NodeDocument
argument_list|>
block|{
specifier|private
specifier|static
name|Comparator
argument_list|<
name|String
argument_list|>
name|reverse
init|=
name|Collections
operator|.
name|reverseOrder
argument_list|(
name|PathComparator
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|NodeDocument
name|o1
parameter_list|,
name|NodeDocument
name|o2
parameter_list|)
block|{
return|return
name|reverse
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getPath
argument_list|()
argument_list|,
name|o2
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

