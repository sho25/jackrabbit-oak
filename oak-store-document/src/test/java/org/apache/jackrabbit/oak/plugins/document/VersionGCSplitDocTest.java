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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|FixturesHelper
operator|.
name|getFixtures
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
name|commons
operator|.
name|FixturesHelper
operator|.
name|Fixture
operator|.
name|DOCUMENT_MEM
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
name|commons
operator|.
name|FixturesHelper
operator|.
name|Fixture
operator|.
name|DOCUMENT_NS
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
name|commons
operator|.
name|FixturesHelper
operator|.
name|Fixture
operator|.
name|DOCUMENT_RDB
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
name|NO_BINARY
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
name|TimeUnit
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
name|VersionGCSplitDocTest
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
name|DocumentStoreFixture
name|fixture
decl_stmt|;
specifier|private
name|ExecutorService
name|execService
decl_stmt|;
specifier|private
name|DocumentStore
name|store
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns
decl_stmt|;
specifier|private
name|VersionGarbageCollector
name|gc
decl_stmt|;
specifier|private
name|String
name|longpath
decl_stmt|;
specifier|private
name|Clock
name|clock
decl_stmt|;
specifier|public
name|VersionGCSplitDocTest
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
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
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
name|getFixtures
argument_list|()
operator|.
name|contains
argument_list|(
name|DOCUMENT_NS
argument_list|)
operator|&&
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
name|DocumentStoreFixture
name|rdb
init|=
operator|new
name|DocumentStoreFixture
operator|.
name|RDBFixture
argument_list|()
decl_stmt|;
if|if
condition|(
name|getFixtures
argument_list|()
operator|.
name|contains
argument_list|(
name|DOCUMENT_RDB
argument_list|)
operator|&&
name|rdb
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
name|rdb
block|}
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fixtures
operator|.
name|isEmpty
argument_list|()
operator|||
name|getFixtures
argument_list|()
operator|.
name|contains
argument_list|(
name|DOCUMENT_MEM
argument_list|)
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
operator|new
name|DocumentStoreFixture
operator|.
name|MemoryFixture
argument_list|()
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
name|Exception
block|{
name|StringBuffer
name|longpath
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
name|longpath
operator|.
name|length
argument_list|()
operator|<
literal|380
condition|)
block|{
name|longpath
operator|.
name|append
argument_list|(
literal|"thisisaverylongpath"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|longpath
operator|=
name|longpath
operator|.
name|toString
argument_list|()
expr_stmt|;
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
name|fixture
operator|.
name|createDocumentStore
argument_list|()
expr_stmt|;
if|if
condition|(
name|fixture
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"MongoDB"
argument_list|)
condition|)
block|{
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|MongoUtils
operator|.
name|DB
argument_list|)
expr_stmt|;
block|}
name|execService
operator|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
expr_stmt|;
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
name|gc
operator|=
name|ns
operator|.
name|getVersionGarbageCollector
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createDefaultNoBranchSplitDocument
parameter_list|(
name|DocumentNodeStore
name|ns
parameter_list|,
name|String
name|parent
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
literal|"createNoBranchSplitDocument"
operator|+
name|longpath
argument_list|)
operator|.
name|child
argument_list|(
name|parent
argument_list|)
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|=
name|ns
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
literal|"createNoBranchSplitDocument"
operator|+
name|longpath
argument_list|)
operator|.
name|child
argument_list|(
name|parent
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"value-"
operator|+
name|i
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
name|ns
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/"
operator|+
literal|"createNoBranchSplitDocument"
operator|+
name|longpath
operator|+
literal|"/"
operator|+
name|parent
argument_list|)
decl_stmt|;
name|NodeDocument
name|doc
init|=
name|store
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
for|for
control|(
name|UpdateOp
name|op
range|:
name|SplitOperations
operator|.
name|forDocument
argument_list|(
name|doc
argument_list|,
name|ns
argument_list|,
name|ns
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
name|NO_BINARY
argument_list|,
literal|5
argument_list|)
control|)
block|{
name|ns
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|createOrUpdate
argument_list|(
name|NODES
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|createCommitOnlyAndNoChildSplitDocument
parameter_list|(
name|DocumentNodeStore
name|ns
parameter_list|,
name|String
name|parent1
parameter_list|,
name|String
name|parent2
parameter_list|,
name|String
name|child
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|b1
init|=
name|ns
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
literal|"createCommitOnlyAndNoChildSplitDocument"
operator|+
name|longpath
argument_list|)
operator|.
name|child
argument_list|(
name|parent1
argument_list|)
operator|.
name|child
argument_list|(
name|child
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
literal|"createCommitOnlyAndNoChildSplitDocument"
operator|+
name|longpath
argument_list|)
operator|.
name|child
argument_list|(
name|parent2
argument_list|)
operator|.
name|child
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|ns
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
name|ns
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
literal|"createCommitOnlyAndNoChildSplitDocument"
operator|+
name|longpath
argument_list|)
operator|.
name|child
argument_list|(
name|parent1
argument_list|)
operator|.
name|child
argument_list|(
name|child
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
literal|"createCommitOnlyAndNoChildSplitDocument"
operator|+
name|longpath
argument_list|)
operator|.
name|child
argument_list|(
name|parent2
argument_list|)
operator|.
name|child
argument_list|(
name|child
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|ns
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
block|}
specifier|private
name|void
name|createCommitOnlySplitDocument
parameter_list|(
name|DocumentNodeStore
name|ns
parameter_list|,
name|String
name|parent1
parameter_list|,
name|String
name|parent2
parameter_list|,
name|String
name|child
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|b1
init|=
name|ns
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
literal|"createCommitOnlySplitDocument"
operator|+
name|longpath
argument_list|)
operator|.
name|child
argument_list|(
name|parent1
argument_list|)
operator|.
name|child
argument_list|(
name|child
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
literal|"createCommitOnlySplitDocument"
operator|+
name|longpath
argument_list|)
operator|.
name|child
argument_list|(
name|parent2
argument_list|)
operator|.
name|child
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|ns
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
literal|2
operator|*
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
name|ns
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
literal|"createCommitOnlySplitDocument"
operator|+
name|longpath
argument_list|)
operator|.
name|child
argument_list|(
name|parent1
argument_list|)
operator|.
name|child
argument_list|(
name|child
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"createCommitOnlySplitDocument"
operator|+
name|longpath
argument_list|)
operator|.
name|child
argument_list|(
literal|"child-"
operator|+
name|i
argument_list|)
expr_stmt|;
name|ns
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
block|}
specifier|private
name|void
name|createDefaultLeafSplitDocument
parameter_list|(
name|DocumentNodeStore
name|ns
parameter_list|,
name|String
name|parent1
parameter_list|,
name|String
name|parent2
parameter_list|,
name|String
name|child
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|b1
init|=
name|ns
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
literal|"createDefaultLeafSplitDocument"
operator|+
name|longpath
argument_list|)
operator|.
name|child
argument_list|(
name|parent1
argument_list|)
operator|.
name|child
argument_list|(
name|child
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
literal|"createDefaultLeafSplitDocument"
operator|+
name|longpath
argument_list|)
operator|.
name|child
argument_list|(
name|parent2
argument_list|)
operator|.
name|child
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|ns
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
name|NodeDocument
operator|.
name|NUM_REVS_THRESHOLD
condition|;
name|i
operator|++
control|)
block|{
comment|//This should result in SplitDoc of type DEFAULT_NO_CHILD (aka DEFAULT_LEAF)
name|b1
operator|=
name|ns
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
literal|"createDefaultLeafSplitDocument"
operator|+
name|longpath
argument_list|)
operator|.
name|child
argument_list|(
name|parent2
argument_list|)
operator|.
name|child
argument_list|(
name|child
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|ns
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
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
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
annotation|@
name|Test
specifier|public
name|void
name|emptyGC
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|gc
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|splitDocGCCount
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|countNodeDocuments
parameter_list|()
block|{
return|return
name|store
operator|.
name|query
argument_list|(
name|NODES
argument_list|,
name|NodeDocument
operator|.
name|MIN_ID_VALUE
argument_list|,
name|NodeDocument
operator|.
name|MAX_ID_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|size
argument_list|()
return|;
block|}
specifier|private
name|int
name|countStalePrev
parameter_list|()
block|{
name|int
name|cnt
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|NodeDocument
argument_list|>
name|nodes
init|=
name|store
operator|.
name|query
argument_list|(
name|NODES
argument_list|,
name|NodeDocument
operator|.
name|MIN_ID_VALUE
argument_list|,
name|NodeDocument
operator|.
name|MAX_ID_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
for|for
control|(
name|NodeDocument
name|nodeDocument
range|:
name|nodes
control|)
block|{
name|cnt
operator|+=
name|nodeDocument
operator|.
name|getStalePrev
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|cnt
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|commitOnlyAndNoChild
parameter_list|()
throws|throws
name|Exception
block|{
name|createCommitOnlyAndNoChildSplitDocument
argument_list|(
name|ns
argument_list|,
literal|"parent1"
argument_list|,
literal|"parent2"
argument_list|,
literal|"child"
argument_list|)
expr_stmt|;
comment|// perform a change to make sure the sweep rev will be newer than
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS_RESOLUTION
operator|*
literal|2
argument_list|)
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
literal|"qux"
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
name|int
name|nodesBeforeGc
init|=
name|countNodeDocuments
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countStalePrev
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|VersionGCStats
name|stats
init|=
name|gc
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|nodesAfterGc
init|=
name|countNodeDocuments
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|countStalePrev
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|nodesBeforeGc
operator|-
name|nodesAfterGc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|stats
operator|.
name|splitDocGCCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|commitOnly
parameter_list|()
throws|throws
name|Exception
block|{
name|createCommitOnlySplitDocument
argument_list|(
name|ns
argument_list|,
literal|"parent1"
argument_list|,
literal|"parent2"
argument_list|,
literal|"child"
argument_list|)
expr_stmt|;
comment|// perform a change to make sure the sweep rev will be newer than
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS_RESOLUTION
operator|*
literal|2
argument_list|)
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
literal|"qux"
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
name|int
name|nodesBeforeGc
init|=
name|countNodeDocuments
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countStalePrev
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|VersionGCStats
name|stats
init|=
name|gc
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|nodesAfterGc
init|=
name|countNodeDocuments
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"before gc : "
operator|+
name|nodesBeforeGc
operator|+
literal|", after gc : "
operator|+
name|nodesAfterGc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|countStalePrev
argument_list|()
operator|>=
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodesBeforeGc
operator|-
name|nodesAfterGc
operator|>=
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stats
operator|.
name|splitDocGCCount
operator|>=
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|defaultLeaf
parameter_list|()
throws|throws
name|Exception
block|{
name|createDefaultLeafSplitDocument
argument_list|(
name|ns
argument_list|,
literal|"parent1"
argument_list|,
literal|"parent2"
argument_list|,
literal|"child"
argument_list|)
expr_stmt|;
comment|// perform a change to make sure the sweep rev will be newer than
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS_RESOLUTION
operator|*
literal|2
argument_list|)
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
literal|"qux"
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
name|int
name|nodesBeforeGc
init|=
name|countNodeDocuments
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countStalePrev
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|VersionGCStats
name|stats
init|=
name|gc
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|nodesAfterGc
init|=
name|countNodeDocuments
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countStalePrev
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nodesBeforeGc
operator|-
name|nodesAfterGc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stats
operator|.
name|splitDocGCCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|defaultNoBranch
parameter_list|()
throws|throws
name|Exception
block|{
name|createDefaultNoBranchSplitDocument
argument_list|(
name|ns
argument_list|,
literal|"aparent"
argument_list|)
expr_stmt|;
comment|// perform a change to make sure the sweep rev will be newer than
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS_RESOLUTION
operator|*
literal|2
argument_list|)
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
literal|"qux"
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
name|ns
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|int
name|nodesBeforeGc
init|=
name|countNodeDocuments
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countStalePrev
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|VersionGCStats
name|stats
init|=
name|gc
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|nodesAfterGc
init|=
name|countNodeDocuments
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countStalePrev
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nodesBeforeGc
operator|-
name|nodesAfterGc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stats
operator|.
name|splitDocGCCount
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

