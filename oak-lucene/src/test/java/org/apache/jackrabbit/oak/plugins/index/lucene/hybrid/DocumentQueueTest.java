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
name|index
operator|.
name|lucene
operator|.
name|hybrid
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Executor
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
name|base
operator|.
name|Stopwatch
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
name|ArrayListMultimap
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
name|ImmutableMap
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
name|ImmutableSet
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
name|ListMultimap
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
name|core
operator|.
name|SimpleCommitContext
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
name|index
operator|.
name|IndexEditorProvider
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
name|index
operator|.
name|IndexUpdateProvider
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
name|index
operator|.
name|lucene
operator|.
name|IndexCopier
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
name|index
operator|.
name|lucene
operator|.
name|IndexNode
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
name|index
operator|.
name|lucene
operator|.
name|IndexTracker
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
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|IndexingMode
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
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexEditorProvider
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
name|index
operator|.
name|lucene
operator|.
name|TestUtil
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
name|index
operator|.
name|lucene
operator|.
name|reader
operator|.
name|DefaultIndexReaderFactory
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
name|CommitContext
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
name|EditorHook
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|stats
operator|.
name|StatisticsProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|StringField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TermQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TopDocs
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
name|rules
operator|.
name|TemporaryFolder
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
name|MoreExecutors
operator|.
name|sameThreadExecutor
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
name|index
operator|.
name|lucene
operator|.
name|FieldFactory
operator|.
name|newPathField
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
name|index
operator|.
name|lucene
operator|.
name|hybrid
operator|.
name|LocalIndexObserverTest
operator|.
name|NOOP_EXECUTOR
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
name|index
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneIndexHelper
operator|.
name|newLucenePropertyIndexDefinition
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|nodetype
operator|.
name|write
operator|.
name|InitialContent
operator|.
name|INITIAL_CONTENT
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
name|spi
operator|.
name|mount
operator|.
name|Mounts
operator|.
name|defaultMountInfoProvider
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
name|DocumentQueueTest
block|{
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|temporaryFolder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|NodeState
name|root
init|=
name|INITIAL_CONTENT
decl_stmt|;
specifier|private
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|private
name|EditorHook
name|asyncHook
decl_stmt|;
specifier|private
name|EditorHook
name|syncHook
decl_stmt|;
specifier|private
name|CommitInfo
name|info
decl_stmt|;
specifier|private
name|IndexTracker
name|tracker
init|=
operator|new
name|IndexTracker
argument_list|()
decl_stmt|;
specifier|private
name|NRTIndexFactory
name|indexFactory
decl_stmt|;
specifier|private
name|Clock
name|clock
init|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
decl_stmt|;
specifier|private
name|long
name|refreshDelta
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexEditorProvider
name|editorProvider
init|=
operator|new
name|LuceneIndexEditorProvider
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|defaultMountInfoProvider
argument_list|()
argument_list|)
decl_stmt|;
name|syncHook
operator|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
name|editorProvider
argument_list|)
argument_list|)
expr_stmt|;
name|asyncHook
operator|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
name|editorProvider
argument_list|,
literal|"async"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|dropDocOnLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentQueue
name|queue
init|=
operator|new
name|DocumentQueue
argument_list|(
literal|2
argument_list|,
name|tracker
argument_list|,
name|NOOP_EXECUTOR
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|add
argument_list|(
name|LuceneDoc
operator|.
name|forDelete
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|add
argument_list|(
name|LuceneDoc
operator|.
name|forDelete
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//3rd one would be dropped as queue size is 2
name|assertFalse
argument_list|(
name|queue
operator|.
name|add
argument_list|(
name|LuceneDoc
operator|.
name|forDelete
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noIssueIfNoIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentQueue
name|queue
init|=
operator|new
name|DocumentQueue
argument_list|(
literal|2
argument_list|,
name|tracker
argument_list|,
name|sameThreadExecutor
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|add
argument_list|(
name|LuceneDoc
operator|.
name|forDelete
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|getQueuedDocs
argument_list|()
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
name|closeQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentQueue
name|queue
init|=
operator|new
name|DocumentQueue
argument_list|(
literal|2
argument_list|,
name|tracker
argument_list|,
name|sameThreadExecutor
argument_list|()
argument_list|)
decl_stmt|;
name|queue
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|queue
operator|.
name|add
argument_list|(
name|LuceneDoc
operator|.
name|forDelete
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ignore
parameter_list|)
block|{          }
block|}
annotation|@
name|Test
specifier|public
name|void
name|noIssueIfNoWriter
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|indexed
init|=
name|createAndPopulateAsyncIndex
argument_list|(
name|IndexingMode
operator|.
name|NRT
argument_list|)
decl_stmt|;
name|DocumentQueue
name|queue
init|=
operator|new
name|DocumentQueue
argument_list|(
literal|2
argument_list|,
name|tracker
argument_list|,
name|sameThreadExecutor
argument_list|()
argument_list|)
decl_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|add
argument_list|(
name|LuceneDoc
operator|.
name|forDelete
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|updateDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexTracker
name|tracker
init|=
name|createTracker
argument_list|()
decl_stmt|;
name|NodeState
name|indexed
init|=
name|createAndPopulateAsyncIndex
argument_list|(
name|IndexingMode
operator|.
name|NRT
argument_list|)
decl_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
name|DocumentQueue
name|queue
init|=
operator|new
name|DocumentQueue
argument_list|(
literal|2
argument_list|,
name|tracker
argument_list|,
name|sameThreadExecutor
argument_list|()
argument_list|)
decl_stmt|;
name|Document
name|d1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newPathField
argument_list|(
literal|"/a/b"
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"foo"
argument_list|,
literal|"a"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|LuceneDoc
operator|.
name|forUpdate
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|,
literal|"/a/b"
argument_list|,
name|d1
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|NRTIndex
argument_list|>
name|indexes
init|=
name|indexFactory
operator|.
name|getIndexes
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|)
decl_stmt|;
name|NRTIndex
name|index
init|=
name|indexes
operator|.
name|get
argument_list|(
name|indexes
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|index
operator|.
name|getPrimaryReader
argument_list|()
operator|.
name|getReader
argument_list|()
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexRefresh
parameter_list|()
throws|throws
name|Exception
block|{
name|tracker
operator|=
name|createTracker
argument_list|()
expr_stmt|;
name|NodeState
name|indexed
init|=
name|createAndPopulateAsyncIndex
argument_list|(
name|IndexingMode
operator|.
name|NRT
argument_list|)
decl_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
name|clock
operator|.
name|waitUntil
argument_list|(
name|refreshDelta
argument_list|)
expr_stmt|;
name|DocumentQueue
name|queue
init|=
operator|new
name|DocumentQueue
argument_list|(
literal|2
argument_list|,
name|tracker
argument_list|,
name|sameThreadExecutor
argument_list|()
argument_list|)
decl_stmt|;
name|TopDocs
name|td
init|=
name|doSearch
argument_list|(
literal|"bar"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|queue
argument_list|,
literal|"/a/b"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
comment|//First update would be picked as base time was zero which would now
comment|//get initialized
name|td
operator|=
name|doSearch
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|queue
argument_list|,
literal|"/a/c"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
comment|//Now it would not update as refresh interval has not exceeded
name|td
operator|=
name|doSearch
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|queue
argument_list|,
literal|"/a/d"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
comment|//Get past the delta time
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|refreshDelta
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|//Now it should show updated result
name|td
operator|=
name|doSearch
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|//Phase 2 - Check affect of async index update cycle
comment|//With that there should only be 2 copies of NRTIndex kept
name|indexed
operator|=
name|doAsyncIndex
argument_list|(
name|indexed
argument_list|,
literal|"a2"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
comment|//Now result would be latest from async + last local
name|td
operator|=
name|doSearch
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|//Now there would be to NRTIndex - previous and current
comment|//so add to current and query again
name|addDoc
argument_list|(
name|queue
argument_list|,
literal|"/a/e"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|td
operator|=
name|doSearch
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|//Now do another async update
name|indexed
operator|=
name|doAsyncIndex
argument_list|(
name|indexed
argument_list|,
literal|"a3"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
comment|//Now total count would be 4
comment|//3 from async and 1 from current
name|td
operator|=
name|doSearch
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addAllSync
parameter_list|()
throws|throws
name|Exception
block|{
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|LuceneDoc
argument_list|>
name|docs
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
name|tracker
operator|=
name|createTracker
argument_list|()
expr_stmt|;
name|NodeState
name|indexed
init|=
name|createAndPopulateAsyncIndex
argument_list|(
name|IndexingMode
operator|.
name|SYNC
argument_list|)
decl_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
name|DocumentQueue
name|queue
init|=
operator|new
name|DocumentQueue
argument_list|(
literal|2
argument_list|,
name|tracker
argument_list|,
name|sameThreadExecutor
argument_list|()
argument_list|)
decl_stmt|;
name|TopDocs
name|td
init|=
name|doSearch
argument_list|(
literal|"bar"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|docs
operator|.
name|get
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|)
operator|.
name|add
argument_list|(
name|createDoc
argument_list|(
literal|"/a/c"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addAllSynchronously
argument_list|(
name|docs
operator|.
name|asMap
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|=
name|doSearch
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|docs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|docs
operator|.
name|get
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|)
operator|.
name|add
argument_list|(
name|createDoc
argument_list|(
literal|"/a/d"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addAllSynchronously
argument_list|(
name|docs
operator|.
name|asMap
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|=
name|doSearch
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
comment|//@Test
specifier|public
name|void
name|benchMarkIndexWriter
parameter_list|()
throws|throws
name|Exception
block|{
name|Executor
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|IndexCopier
name|indexCopier
init|=
operator|new
name|IndexCopier
argument_list|(
name|executor
argument_list|,
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|indexFactory
operator|=
operator|new
name|NRTIndexFactory
argument_list|(
name|indexCopier
argument_list|,
name|clock
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toSeconds
argument_list|(
name|refreshDelta
argument_list|)
argument_list|,
name|StatisticsProvider
operator|.
name|NOOP
argument_list|)
expr_stmt|;
name|tracker
operator|=
operator|new
name|IndexTracker
argument_list|(
operator|new
name|DefaultIndexReaderFactory
argument_list|(
name|defaultMountInfoProvider
argument_list|()
argument_list|,
name|indexCopier
argument_list|)
argument_list|,
name|indexFactory
argument_list|)
expr_stmt|;
name|NodeState
name|indexed
init|=
name|createAndPopulateAsyncIndex
argument_list|(
name|IndexingMode
operator|.
name|NRT
argument_list|)
decl_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
name|DocumentQueue
name|queue
init|=
operator|new
name|DocumentQueue
argument_list|(
literal|1000
argument_list|,
name|tracker
argument_list|,
name|executor
argument_list|)
decl_stmt|;
comment|/*             Sample output             [nrt] Time taken for 10000 is 639.3 ms with waits 1             [sync] Time taken for 10000 is 30.34 s              Refreshing reader after every commit would slow down things          */
name|LuceneDoc
name|doc
init|=
name|createDoc
argument_list|(
literal|"/a/b"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
literal|10000
decl_stmt|;
name|Stopwatch
name|w
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|int
name|waitCount
init|=
literal|0
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
while|while
condition|(
operator|!
name|queue
operator|.
name|add
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|waitCount
operator|++
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"%n[nrt] Time taken for %d is %s with waits %d%n"
argument_list|,
name|numDocs
argument_list|,
name|w
argument_list|,
name|waitCount
argument_list|)
expr_stmt|;
name|indexed
operator|=
name|createAndPopulateAsyncIndex
argument_list|(
name|IndexingMode
operator|.
name|SYNC
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
name|queue
operator|=
operator|new
name|DocumentQueue
argument_list|(
literal|1000
argument_list|,
name|tracker
argument_list|,
name|executor
argument_list|)
expr_stmt|;
name|w
operator|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|LuceneDoc
argument_list|>
name|docs
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
name|docs
operator|.
name|get
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|)
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addAllSynchronously
argument_list|(
name|docs
operator|.
name|asMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"%n[sync] Time taken for %d is %s%n"
argument_list|,
name|numDocs
argument_list|,
name|w
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeState
name|doAsyncIndex
parameter_list|(
name|NodeState
name|current
parameter_list|,
name|String
name|childName
parameter_list|,
name|String
name|fooValue
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|//Have some stuff to be indexed
name|NodeBuilder
name|builder
init|=
name|current
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
name|childName
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
name|fooValue
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
return|return
name|asyncHook
operator|.
name|processCommit
argument_list|(
name|current
argument_list|,
name|after
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|TopDocs
name|doSearch
parameter_list|(
name|String
name|fooValue
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexNode
name|indexNode
init|=
name|tracker
operator|.
name|acquireIndexNode
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|indexNode
operator|.
name|getSearcher
argument_list|()
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
name|fooValue
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
return|;
block|}
finally|finally
block|{
name|indexNode
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addDoc
parameter_list|(
name|DocumentQueue
name|queue
parameter_list|,
name|String
name|docPath
parameter_list|,
name|String
name|fooValue
parameter_list|)
block|{
name|LuceneDoc
name|doc
init|=
name|createDoc
argument_list|(
name|docPath
argument_list|,
name|fooValue
argument_list|)
decl_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|LuceneDoc
name|createDoc
parameter_list|(
name|String
name|docPath
parameter_list|,
name|String
name|fooValue
parameter_list|)
block|{
name|Document
name|d1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newPathField
argument_list|(
name|docPath
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"foo"
argument_list|,
name|fooValue
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|LuceneDoc
operator|.
name|forUpdate
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|,
name|docPath
argument_list|,
name|d1
argument_list|)
return|;
block|}
specifier|private
name|IndexTracker
name|createTracker
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexCopier
name|indexCopier
init|=
operator|new
name|IndexCopier
argument_list|(
name|sameThreadExecutor
argument_list|()
argument_list|,
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|indexFactory
operator|=
operator|new
name|NRTIndexFactory
argument_list|(
name|indexCopier
argument_list|,
name|clock
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toSeconds
argument_list|(
name|refreshDelta
argument_list|)
argument_list|,
name|StatisticsProvider
operator|.
name|NOOP
argument_list|)
expr_stmt|;
return|return
operator|new
name|IndexTracker
argument_list|(
operator|new
name|DefaultIndexReaderFactory
argument_list|(
name|defaultMountInfoProvider
argument_list|()
argument_list|,
name|indexCopier
argument_list|)
argument_list|,
name|indexFactory
argument_list|)
return|;
block|}
specifier|private
name|NodeState
name|createAndPopulateAsyncIndex
parameter_list|(
name|IndexingMode
name|indexingMode
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|createIndexDefinition
argument_list|(
literal|"fooIndex"
argument_list|,
name|indexingMode
argument_list|)
expr_stmt|;
comment|//Have some stuff to be indexed
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
return|return
name|asyncHook
operator|.
name|processCommit
argument_list|(
name|EMPTY_NODE
argument_list|,
name|after
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|CommitInfo
name|newCommitInfo
parameter_list|()
block|{
name|info
operator|=
operator|new
name|CommitInfo
argument_list|(
literal|"admin"
argument_list|,
literal|"s1"
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|of
argument_list|(
name|CommitContext
operator|.
name|NAME
argument_list|,
operator|new
name|SimpleCommitContext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
specifier|private
name|void
name|createIndexDefinition
parameter_list|(
name|String
name|idxName
parameter_list|,
name|IndexingMode
name|indexingMode
parameter_list|)
block|{
name|NodeBuilder
name|idx
init|=
name|newLucenePropertyIndexDefinition
argument_list|(
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
argument_list|,
name|idxName
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"async"
argument_list|)
decl_stmt|;
comment|//Disable compression
comment|//idx.setProperty("codec", "oakCodec");
name|TestUtil
operator|.
name|enableIndexingMode
argument_list|(
name|idx
argument_list|,
name|indexingMode
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

