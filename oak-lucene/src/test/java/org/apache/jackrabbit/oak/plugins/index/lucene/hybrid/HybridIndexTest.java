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
name|Collections
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|Predicate
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|Oak
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
name|ContentRepository
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
name|Tree
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
name|PathUtils
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
name|AsyncIndexUpdate
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
name|IndexConstants
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
name|counter
operator|.
name|NodeCounterEditorProvider
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
name|LuceneIndexProvider
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|reader
operator|.
name|LuceneIndexReaderFactory
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
name|nodetype
operator|.
name|NodeTypeIndexProvider
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
name|property
operator|.
name|PropertyIndexEditorProvider
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
name|MemoryNodeStore
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
name|nodetype
operator|.
name|write
operator|.
name|InitialContent
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
name|query
operator|.
name|AbstractQueryTest
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
name|Observer
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
name|mount
operator|.
name|MountInfoProvider
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
name|query
operator|.
name|QueryIndexProvider
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
name|security
operator|.
name|OpenSecurityProvider
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
name|NodeStore
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
name|whiteboard
operator|.
name|Whiteboard
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
name|whiteboard
operator|.
name|WhiteboardUtils
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
name|collect
operator|.
name|ImmutableList
operator|.
name|of
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
name|api
operator|.
name|Type
operator|.
name|STRINGS
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
name|LucenePropertyIndexTest
operator|.
name|createIndex
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
name|PropertyStates
operator|.
name|createProperty
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
name|assertNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|HybridIndexTest
extends|extends
name|AbstractQueryTest
block|{
specifier|private
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|2
argument_list|)
decl_stmt|;
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
name|NodeStore
name|nodeStore
decl_stmt|;
specifier|private
name|DocumentQueue
name|queue
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
name|Whiteboard
name|wb
decl_stmt|;
comment|//TODO [hybrid] this needs to be obtained from NRTIndexFactory
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
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
name|IndexCopier
name|copier
init|=
literal|null
decl_stmt|;
try|try
block|{
name|copier
operator|=
operator|new
name|IndexCopier
argument_list|(
name|executorService
argument_list|,
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
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
name|MountInfoProvider
name|mip
init|=
name|defaultMountInfoProvider
argument_list|()
decl_stmt|;
name|LuceneIndexEditorProvider
name|editorProvider
init|=
operator|new
name|LuceneIndexEditorProvider
argument_list|(
name|copier
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|mip
argument_list|)
decl_stmt|;
name|NRTIndexFactory
name|nrtIndexFactory
init|=
operator|new
name|NRTIndexFactory
argument_list|(
name|copier
argument_list|)
decl_stmt|;
name|LuceneIndexReaderFactory
name|indexReaderFactory
init|=
operator|new
name|DefaultIndexReaderFactory
argument_list|(
name|mip
argument_list|,
name|copier
argument_list|)
decl_stmt|;
name|IndexTracker
name|tracker
init|=
operator|new
name|IndexTracker
argument_list|(
name|indexReaderFactory
argument_list|,
name|nrtIndexFactory
argument_list|)
decl_stmt|;
name|LuceneIndexProvider
name|provider
init|=
operator|new
name|LuceneIndexProvider
argument_list|(
name|tracker
argument_list|)
decl_stmt|;
name|queue
operator|=
operator|new
name|DocumentQueue
argument_list|(
literal|100
argument_list|,
name|tracker
argument_list|,
name|clock
argument_list|,
name|sameThreadExecutor
argument_list|()
argument_list|)
expr_stmt|;
name|LocalIndexObserver
name|localIndexObserver
init|=
operator|new
name|LocalIndexObserver
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|nodeStore
operator|=
operator|new
name|MemoryNodeStore
argument_list|()
expr_stmt|;
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|(
name|nodeStore
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|(
name|QueryIndexProvider
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
operator|(
name|Observer
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
name|localIndexObserver
argument_list|)
operator|.
name|with
argument_list|(
name|editorProvider
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|PropertyIndexEditorProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|NodeTypeIndexProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|NodeCounterEditorProvider
argument_list|()
argument_list|)
comment|//Effectively disable async indexing auto run
comment|//such that we can control run timing as per test requirement
operator|.
name|withAsyncIndexing
argument_list|(
literal|"async"
argument_list|,
name|TimeUnit
operator|.
name|DAYS
operator|.
name|toSeconds
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|wb
operator|=
name|oak
operator|.
name|getWhiteboard
argument_list|()
expr_stmt|;
return|return
name|oak
operator|.
name|createContentRepository
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|hybridIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|idxName
init|=
literal|"hybridtest"
decl_stmt|;
name|Tree
name|idx
init|=
name|createIndex
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|idxName
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|idx
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"sync"
argument_list|,
literal|"async"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|//Get initial indexing done as local indexing only work
comment|//for incremental indexing
name|createPath
argument_list|(
literal|"/a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|runAsyncIndex
argument_list|()
expr_stmt|;
name|setTraversalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] where [foo] = 'bar'"
argument_list|,
name|of
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Add new node. This would not be reflected in result as local index would not be updated
name|createPath
argument_list|(
literal|"/b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] where [foo] = 'bar'"
argument_list|,
name|of
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Now let some time elapse such that readers can be refreshed
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
comment|//TODO This extra push would not be required once refresh also account for time
name|createPath
argument_list|(
literal|"/c"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|//Now recently added stuff should be visible without async indexing run
name|assertQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] where [foo] = 'bar'"
argument_list|,
name|of
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|,
literal|"/c"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Post async index it should still be upto date
name|runAsyncIndex
argument_list|()
expr_stmt|;
name|assertQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] where [foo] = 'bar'"
argument_list|,
name|of
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|,
literal|"/c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|runAsyncIndex
parameter_list|()
block|{
name|Runnable
name|async
init|=
name|WhiteboardUtils
operator|.
name|getService
argument_list|(
name|wb
argument_list|,
name|Runnable
operator|.
name|class
argument_list|,
operator|new
name|Predicate
argument_list|<
name|Runnable
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Runnable
name|input
parameter_list|)
block|{
return|return
name|input
operator|instanceof
name|AsyncIndexUpdate
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|async
argument_list|)
expr_stmt|;
name|async
operator|.
name|run
argument_list|()
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Tree
name|createPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|Tree
name|base
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|e
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|base
operator|=
name|base
operator|.
name|addChild
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|base
return|;
block|}
block|}
end_class

end_unit

