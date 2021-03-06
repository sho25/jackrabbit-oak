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
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|JOURNAL
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
name|TestUtils
operator|.
name|merge
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|greaterThan
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

begin_class
specifier|public
class|class
name|JournalGCTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JournalGCTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ThreadLocal
argument_list|<
name|Boolean
argument_list|>
name|shouldWait
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
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
name|Before
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
name|shouldWait
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|shouldWait
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
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
annotation|@
name|Test
specifier|public
name|void
name|gcWithCheckpoint
parameter_list|()
throws|throws
name|Exception
block|{
name|Clock
name|c
init|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
decl_stmt|;
name|c
operator|.
name|waitUntil
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|DocumentNodeStore
name|ns
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setJournalGCMaxAge
argument_list|(
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|clock
argument_list|(
name|c
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
comment|// perform some change
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
literal|"foo"
argument_list|)
expr_stmt|;
name|ns
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
name|Revision
name|head
init|=
name|ns
operator|.
name|getHeadRevision
argument_list|()
operator|.
name|getRevision
argument_list|(
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|head
argument_list|)
expr_stmt|;
comment|// trigger creation of journal entry
name|ns
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|String
name|cp
init|=
name|ns
operator|.
name|checkpoint
argument_list|(
name|TimeUnit
operator|.
name|DAYS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|JournalEntry
name|entry
init|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|JOURNAL
argument_list|,
name|JournalEntry
operator|.
name|asId
argument_list|(
name|head
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|entry
argument_list|)
expr_stmt|;
comment|// wait two hours
name|c
operator|.
name|waitUntil
argument_list|(
name|c
operator|.
name|getTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// instruct journal collector to remove entries older than one hour
name|ns
operator|.
name|getJournalGarbageCollector
argument_list|()
operator|.
name|gc
argument_list|()
expr_stmt|;
comment|// must not remove existing entry, because checkpoint is still valid
name|entry
operator|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|JOURNAL
argument_list|,
name|JournalEntry
operator|.
name|asId
argument_list|(
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|ns
operator|.
name|release
argument_list|(
name|cp
argument_list|)
expr_stmt|;
name|ns
operator|.
name|getJournalGarbageCollector
argument_list|()
operator|.
name|gc
argument_list|()
expr_stmt|;
comment|// now journal GC can remove the entry
name|entry
operator|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|JOURNAL
argument_list|,
name|JournalEntry
operator|.
name|asId
argument_list|(
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
comment|// OAK-5602
annotation|@
name|Test
specifier|public
name|void
name|gcWithCheckpoint2
parameter_list|()
throws|throws
name|Exception
block|{
name|Clock
name|c
init|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
decl_stmt|;
name|c
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
name|c
argument_list|)
expr_stmt|;
name|MemoryDocumentStore
name|docStore
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|DocumentNodeStore
name|ns
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
name|setUpdateLimit
argument_list|(
literal|100
argument_list|)
operator|.
name|setJournalGCMaxAge
argument_list|(
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|setLeaseCheckMode
argument_list|(
name|LeaseCheckMode
operator|.
name|LENIENT
argument_list|)
operator|.
name|clock
argument_list|(
name|c
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
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
name|String
name|testId
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
condition|;
name|i
operator|++
control|)
block|{
name|NodeBuilder
name|child
init|=
name|test
operator|.
name|child
argument_list|(
literal|"child-"
operator|+
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|child
operator|.
name|setProperty
argument_list|(
literal|"p-"
operator|+
name|j
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|docStore
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|testId
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// branch was created
break|break;
block|}
block|}
comment|// simulate a long running commit taking 20 minutes
name|c
operator|.
name|waitUntil
argument_list|(
name|c
operator|.
name|getTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|ns
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|Revision
name|head
init|=
name|ns
operator|.
name|getHeadRevision
argument_list|()
operator|.
name|getRevision
argument_list|(
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|head
argument_list|)
expr_stmt|;
name|ns
operator|.
name|checkpoint
argument_list|(
name|TimeUnit
operator|.
name|DAYS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|JournalEntry
name|entry
init|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|JOURNAL
argument_list|,
name|JournalEntry
operator|.
name|asId
argument_list|(
name|head
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|entry
argument_list|)
expr_stmt|;
comment|// wait two hours
name|c
operator|.
name|waitUntil
argument_list|(
name|c
operator|.
name|getTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// instruct journal collector to remove entries older than one hour
name|ns
operator|.
name|getJournalGarbageCollector
argument_list|()
operator|.
name|gc
argument_list|()
expr_stmt|;
comment|// must not remove existing entry, because checkpoint is still valid
name|entry
operator|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|JOURNAL
argument_list|,
name|JournalEntry
operator|.
name|asId
argument_list|(
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|entry
argument_list|)
expr_stmt|;
comment|// referenced branch commits must also be available
name|assertThat
argument_list|(
name|Iterables
operator|.
name|size
argument_list|(
name|entry
operator|.
name|getBranchCommits
argument_list|()
argument_list|)
argument_list|,
name|greaterThan
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
name|getTailRevision
parameter_list|()
throws|throws
name|Exception
block|{
name|Clock
name|c
init|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
decl_stmt|;
name|c
operator|.
name|waitUntil
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|DocumentNodeStore
name|ns
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setJournalGCMaxAge
argument_list|(
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|clock
argument_list|(
name|c
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|JournalGarbageCollector
name|jgc
init|=
name|ns
operator|.
name|getJournalGarbageCollector
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Revision
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
argument_list|,
name|jgc
operator|.
name|getTailRevision
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"foo"
argument_list|)
expr_stmt|;
name|ns
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
name|ns
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|jgc
operator|.
name|gc
argument_list|()
argument_list|)
expr_stmt|;
comment|// current time, but without the increment done by getTime()
name|long
name|now
init|=
name|c
operator|.
name|getTime
argument_list|()
operator|-
literal|1
decl_stmt|;
name|Revision
name|tail
init|=
operator|new
name|Revision
argument_list|(
name|now
operator|-
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|,
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
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
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tail
argument_list|,
name|jgc
operator|.
name|getTailRevision
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|waitUntil
argument_list|(
name|c
operator|.
name|getTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// must collect the journal entry created by the background update
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|jgc
operator|.
name|gc
argument_list|()
argument_list|)
expr_stmt|;
comment|// current time, but without the increment done by getTime()
name|now
operator|=
name|c
operator|.
name|getTime
argument_list|()
operator|-
literal|1
expr_stmt|;
name|tail
operator|=
operator|new
name|Revision
argument_list|(
name|now
operator|-
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|,
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tail
argument_list|,
name|jgc
operator|.
name|getTailRevision
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * reproducing OAK-5601:      *<ul>      *<li>have two documentMk's, one to make changes, one does only read</li>      *<li>make a commit, let 1.2 seconds pass, run gc, then read it from the other documentMk</li>      *<li>the gc (1sec timeout) will have cleaned up that 1.2sec old journal entry, resulting in      *      a missing journal entry exception when reading from the 2nd documentMk</li>      *</ul>      * What the test has to ensure is that the JournalEntry does the query, then blocks that      * thread to let the GC happen, then continues on with find(). This results in those      * revisions that the JournalEntry got back from the query to be removed and      * thus end up missing by later on in addTo.      */
annotation|@
name|Test
specifier|public
name|void
name|gcCausingMissingJournalEntries
parameter_list|()
throws|throws
name|Exception
block|{
comment|// cluster setup
specifier|final
name|Semaphore
name|enteringFind
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Semaphore
name|continuingFind
init|=
operator|new
name|Semaphore
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|DocumentStore
name|sharedDocStore
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
block|{
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
name|JOURNAL
operator|&&
operator|(
name|shouldWait
operator|.
name|get
argument_list|()
operator|==
literal|null
operator|||
name|shouldWait
operator|.
name|get
argument_list|()
operator|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"find(JOURNAL,..): entered... releasing enteringFind semaphore"
argument_list|)
expr_stmt|;
name|enteringFind
operator|.
name|release
argument_list|()
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"find(JOURNAL,..): waiting for OK to continue"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|continuingFind
operator|.
name|tryAcquire
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"could not continue within 5 sec"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"find(JOURNAL,..): continuing"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
decl_stmt|;
specifier|final
name|DocumentNodeStore
name|writingNs
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|sharedDocStore
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
name|getNodeStore
argument_list|()
decl_stmt|;
name|DocumentNodeStore
name|readingNs
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|sharedDocStore
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
name|getNodeStore
argument_list|()
decl_stmt|;
comment|// 'proper cluster sync': do it a bit too many times
name|readingNs
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|writingNs
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|readingNs
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|writingNs
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// perform some change in writingNs - not yet seen by readingNs
name|NodeBuilder
name|builder
init|=
name|writingNs
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|foo
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
comment|// cause a branch commit
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
name|UPDATE_LIMIT
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|foo
operator|.
name|setProperty
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"foobar"
argument_list|)
expr_stmt|;
block|}
name|writingNs
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
specifier|final
name|Revision
name|head
init|=
name|writingNs
operator|.
name|getHeadRevision
argument_list|()
operator|.
name|getRevision
argument_list|(
name|writingNs
operator|.
name|getClusterId
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|head
argument_list|)
expr_stmt|;
comment|// trigger creation of journal entry - still not yet seen by readingNs
name|writingNs
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|JournalEntry
name|entry
init|=
name|writingNs
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|JOURNAL
argument_list|,
name|JournalEntry
operator|.
name|asId
argument_list|(
name|head
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|entry
argument_list|)
expr_stmt|;
comment|// wait slightly more than 1 sec - readingNs does nothing during this time
name|Thread
operator|.
name|sleep
argument_list|(
literal|1200
argument_list|)
expr_stmt|;
comment|// clear up the semaphore
name|enteringFind
operator|.
name|drainPermits
argument_list|()
expr_stmt|;
name|continuingFind
operator|.
name|drainPermits
argument_list|()
expr_stmt|;
specifier|final
name|StringBuffer
name|errorMsg
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|Runnable
name|r
init|=
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
comment|// wait for find(JOURNAL,..) to be entered
name|LOG
operator|.
name|info
argument_list|(
literal|"waiting for find(JOURNAL,... to be called..."
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|enteringFind
operator|.
name|tryAcquire
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|errorMsg
operator|.
name|append
argument_list|(
literal|"find(JOURNAL,..) did not get called within 5sec"
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|errorMsg
operator|.
name|append
argument_list|(
literal|"Got interrupted: "
operator|+
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"find(JOURNAL,..) got called, running GC."
argument_list|)
expr_stmt|;
comment|// avoid find to block in this thread - via a ThreadLocal
name|shouldWait
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// instruct journal GC to remove entries older than one hour - readingNs hasn't seen it
operator|new
name|JournalGarbageCollector
argument_list|(
name|writingNs
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|gc
argument_list|()
expr_stmt|;
comment|// entry should be removed
name|JournalEntry
name|entry
init|=
name|writingNs
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|JOURNAL
argument_list|,
name|JournalEntry
operator|.
name|asId
argument_list|(
name|head
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|entry
argument_list|)
expr_stmt|;
comment|// now release the waiting find(JOURNAL,..) thread
name|continuingFind
operator|.
name|release
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|Thread
name|th
init|=
operator|new
name|Thread
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|th
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// verify that readingNs doesn't have /foo yet
name|assertFalse
argument_list|(
name|readingNs
operator|.
name|getRoot
argument_list|()
operator|.
name|hasChildNode
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
comment|// now run background ops on readingNs - it should be able to see 'foo'
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|readingNs
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|readingNs
operator|.
name|getRoot
argument_list|()
operator|.
name|hasChildNode
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

