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
name|Collection
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
name|Set
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
name|SplitDocType
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
name|VersionGarbageCollector
operator|.
name|VersionGCStats
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|VersionGarbageCollectorTest
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
name|DocumentNodeStore
name|store
decl_stmt|;
specifier|private
name|VersionGarbageCollector
name|gc
decl_stmt|;
specifier|public
name|VersionGarbageCollectorTest
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
name|fixture
operator|.
name|createDocumentStore
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
name|gcIgnoredForCheckpoint
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|expiryTime
init|=
literal|100
decl_stmt|,
name|maxAge
init|=
literal|20
decl_stmt|;
name|Revision
name|cp
init|=
name|Revision
operator|.
name|fromString
argument_list|(
name|store
operator|.
name|checkpoint
argument_list|(
name|expiryTime
argument_list|)
argument_list|)
decl_stmt|;
comment|//Fast forward time to future but before expiry of checkpoint
name|clock
operator|.
name|waitUntil
argument_list|(
name|cp
operator|.
name|getTimestamp
argument_list|()
operator|+
name|expiryTime
operator|-
name|maxAge
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
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|stats
operator|.
name|ignoredGCDueToCheckPoint
argument_list|)
expr_stmt|;
comment|//Fast forward time to future such that checkpoint get expired
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|expiryTime
operator|+
literal|1
argument_list|)
expr_stmt|;
name|stats
operator|=
name|gc
operator|.
name|gc
argument_list|(
name|maxAge
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"GC should be performed"
argument_list|,
name|stats
operator|.
name|ignoredGCDueToCheckPoint
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGCDeletedDocument
parameter_list|()
throws|throws
name|Exception
block|{
comment|//1. Create nodes
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
name|b1
operator|.
name|child
argument_list|(
literal|"z"
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
comment|//1. Go past GC age and check no GC done as nothing deleted
name|clock
operator|.
name|waitUntil
argument_list|(
name|Revision
operator|.
name|getCurrentTimestamp
argument_list|()
operator|+
name|maxAge
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
argument_list|,
name|HOURS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|deletedDocGCCount
argument_list|)
expr_stmt|;
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
name|child
argument_list|(
literal|"y"
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
comment|//2. Check that a deleted doc is not collected before
comment|//maxAge
comment|//Clock cannot move back (it moved forward in #1) so double the maxAge
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|delta
argument_list|)
expr_stmt|;
name|stats
operator|=
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
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|deletedDocGCCount
argument_list|)
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
name|stats
operator|=
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
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stats
operator|.
name|deletedDocGCCount
argument_list|)
expr_stmt|;
comment|//4. Check that a revived doc (deleted and created again) does not get gc
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
literal|"z"
argument_list|)
operator|.
name|remove
argument_list|()
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
name|NodeBuilder
name|b4
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b4
operator|.
name|child
argument_list|(
literal|"z"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b4
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
name|stats
operator|=
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
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|deletedDocGCCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|gcSplitDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|maxAge
init|=
literal|1
decl_stmt|;
comment|//hrs
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
literal|"test"
argument_list|)
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|child
argument_list|(
literal|"foo"
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
comment|//Commit on a node which has a child and where the commit root
comment|// is parent
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
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
comment|//This updates a middle node i.e. one which has child bar
comment|//Should result in SplitDoc of type PROP_COMMIT_ONLY
name|b1
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|i
argument_list|)
expr_stmt|;
comment|//This should result in SplitDoc of type DEFAULT_NO_CHILD
name|b1
operator|.
name|child
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|i
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
block|}
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|NodeDocument
argument_list|>
name|previousDocTestFoo
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|getDoc
argument_list|(
literal|"/test/foo"
argument_list|)
operator|.
name|getAllPreviousDocs
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|NodeDocument
argument_list|>
name|previousDocTestFoo2
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|getDoc
argument_list|(
literal|"/test2/foo"
argument_list|)
operator|.
name|getAllPreviousDocs
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|previousDocTestFoo
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|previousDocTestFoo2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SplitDocType
operator|.
name|COMMIT_ROOT_ONLY
argument_list|,
name|previousDocTestFoo
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getSplitDocType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SplitDocType
operator|.
name|DEFAULT_LEAF
argument_list|,
name|previousDocTestFoo2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getSplitDocType
argument_list|()
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
name|maxAge
argument_list|)
operator|+
name|delta
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
argument_list|,
name|HOURS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stats
operator|.
name|splitDocGCCount
argument_list|)
expr_stmt|;
comment|//Previous doc should be removed
name|assertNull
argument_list|(
name|getDoc
argument_list|(
name|previousDocTestFoo
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getDoc
argument_list|(
name|previousDocTestFoo2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//Following would not work for Mongo as the delete happened on the server side
comment|//And entries from cache are not evicted
comment|//assertTrue(ImmutableList.copyOf(getDoc("/test2/foo").getAllPreviousDocs()).isEmpty());
block|}
comment|// OAK-1729
annotation|@
name|Test
specifier|public
name|void
name|gcIntermediateDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|maxAge
init|=
literal|1
decl_stmt|;
comment|//hrs
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
comment|// adding the test node will cause the commit root to be placed
comment|// on the root document, because the children flag is set on the
comment|// root document
name|b1
operator|.
name|child
argument_list|(
literal|"test"
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
name|assertTrue
argument_list|(
name|getDoc
argument_list|(
literal|"/test"
argument_list|)
operator|.
name|getLocalRevisions
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// setting the test property afterwards will use the new test document
comment|// as the commit root. this what we want for the test.
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
name|b1
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
literal|"value"
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
name|assertTrue
argument_list|(
operator|!
name|getDoc
argument_list|(
literal|"/test"
argument_list|)
operator|.
name|getLocalRevisions
argument_list|()
operator|.
name|isEmpty
argument_list|()
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
name|PREV_SPLIT_FACTOR
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|NUM_REVS_THRESHOLD
condition|;
name|j
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
name|b1
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
name|i
operator|*
name|NUM_REVS_THRESHOLD
operator|+
name|j
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
block|}
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
block|}
comment|// trigger another split, now that we have 10 previous docs
comment|// this will create an intermediate previous doc
name|store
operator|.
name|addSplitCandidate
argument_list|(
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/test"
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|Revision
argument_list|,
name|Range
argument_list|>
name|prevRanges
init|=
name|getDoc
argument_list|(
literal|"/test"
argument_list|)
operator|.
name|getPreviousRanges
argument_list|()
decl_stmt|;
name|boolean
name|hasIntermediateDoc
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|Range
argument_list|>
name|entry
range|:
name|prevRanges
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getHeight
argument_list|()
operator|>
literal|0
condition|)
block|{
name|hasIntermediateDoc
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Test data does not have intermediate previous docs"
argument_list|,
name|hasIntermediateDoc
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
name|maxAge
argument_list|)
operator|+
name|delta
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
argument_list|,
name|HOURS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|stats
operator|.
name|splitDocGCCount
argument_list|)
expr_stmt|;
name|DocumentNodeState
name|test
init|=
name|getDoc
argument_list|(
literal|"/test"
argument_list|)
operator|.
name|getNodeAtRevision
argument_list|(
name|store
argument_list|,
name|store
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|.
name|hasProperty
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// OAK-1779
annotation|@
name|Test
specifier|public
name|void
name|cacheConsistency
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|maxAge
init|=
literal|1
decl_stmt|;
comment|//hrs
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
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
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
name|String
name|name
init|=
literal|"test-"
operator|+
name|i
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|name
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
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|entry
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
block|}
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
name|b1
operator|.
name|getChildNode
argument_list|(
literal|"test-7"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|names
operator|.
name|remove
argument_list|(
literal|"test-7"
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
argument_list|)
operator|+
name|delta
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
name|deletedDocGCCount
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|children
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|children
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
name|names
argument_list|,
name|children
argument_list|)
expr_stmt|;
block|}
comment|// OAK-1793
annotation|@
name|Test
specifier|public
name|void
name|gcPrevWithMostRecentModification
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|maxAge
init|=
literal|1
decl_stmt|;
comment|//hrs
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_REVS_THRESHOLD
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
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
literal|"foo"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
literal|"v"
operator|+
name|i
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
literal|"v"
operator|+
name|i
argument_list|)
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
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
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
name|maxAge
argument_list|)
operator|+
name|delta
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
argument_list|,
name|HOURS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stats
operator|.
name|splitDocGCCount
argument_list|)
expr_stmt|;
name|NodeDocument
name|doc
init|=
name|getDoc
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|DocumentNodeState
name|state
init|=
name|doc
operator|.
name|getNodeAtRevision
argument_list|(
name|store
argument_list|,
name|store
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
comment|// OAK-1791
annotation|@
name|Test
specifier|public
name|void
name|gcDefaultLeafSplitDocs
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
name|long
name|start
init|=
name|Revision
operator|.
name|getCurrentTimestamp
argument_list|()
decl_stmt|;
comment|// simulate continuous writes once a second for one day
comment|// collect garbage older than one hour
name|int
name|hours
init|=
literal|24
decl_stmt|;
if|if
condition|(
name|fixture
operator|instanceof
name|DocumentStoreFixture
operator|.
name|MongoFixture
condition|)
block|{
comment|// only run for 6 hours on MongoDB to
comment|// keep time to run on a reasonable level
name|hours
operator|=
literal|6
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3600
operator|*
name|hours
condition|;
name|i
operator|++
control|)
block|{
name|clock
operator|.
name|waitUntil
argument_list|(
name|start
operator|+
name|i
operator|*
literal|1000
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
name|i
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
name|i
operator|%
literal|10
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
comment|// trigger GC twice an hour
if|if
condition|(
name|i
operator|%
literal|1800
operator|==
literal|0
condition|)
block|{
name|gc
operator|.
name|gc
argument_list|(
literal|1
argument_list|,
name|HOURS
argument_list|)
expr_stmt|;
name|NodeDocument
name|doc
init|=
name|store
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|int
name|numPrevDocs
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
name|assertTrue
argument_list|(
literal|"too many previous docs: "
operator|+
name|numPrevDocs
argument_list|,
name|numPrevDocs
operator|<
literal|70
argument_list|)
expr_stmt|;
block|}
block|}
name|NodeDocument
name|doc
init|=
name|store
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|int
name|numRevs
init|=
name|size
argument_list|(
name|doc
operator|.
name|getValueMap
argument_list|(
literal|"prop"
argument_list|)
operator|.
name|entrySet
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"too many revisions: "
operator|+
name|numRevs
argument_list|,
name|numRevs
operator|<
literal|6000
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
name|NodeDocument
name|getDoc
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|store
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|path
argument_list|)
argument_list|,
literal|0
argument_list|)
return|;
block|}
block|}
end_class

end_unit

