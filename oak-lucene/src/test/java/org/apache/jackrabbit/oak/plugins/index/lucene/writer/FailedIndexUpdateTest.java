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
name|writer
package|;
end_package

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Closer
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
name|api
operator|.
name|PropertyState
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
name|Root
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
name|CompositeIndexEditorProvider
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
name|IndexUpdateCallback
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
name|LuceneIndexDefinition
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
name|directory
operator|.
name|CopyOnWriteDirectory
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
name|util
operator|.
name|IndexDefinitionBuilder
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
name|spi
operator|.
name|commit
operator|.
name|*
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
name|lucene
operator|.
name|store
operator|.
name|Directory
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
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|InitialContentHelper
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
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|FulltextIndexConstants
operator|.
name|INDEX_DATA_CHILD_NAME
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|FailedIndexUpdateTest
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
name|Closer
name|closer
decl_stmt|;
specifier|private
name|Root
name|root
decl_stmt|;
specifier|private
name|AsyncIndexUpdate
name|asyncIndexUpdate
decl_stmt|;
specifier|private
name|LocalDirectoryTrackingIndexCopier
name|copier
decl_stmt|;
specifier|private
name|FailOnDemandValidatorProvider
name|failOnDemandValidatorProvider
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_CONTENT_PATH
init|=
literal|"/test"
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|closer
operator|=
name|Closer
operator|.
name|create
argument_list|()
expr_stmt|;
name|createRepository
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createRepository
parameter_list|()
throws|throws
name|IOException
block|{
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
name|closer
operator|.
name|register
argument_list|(
operator|new
name|ExecutorCloser
argument_list|(
name|executorService
argument_list|)
argument_list|)
expr_stmt|;
name|copier
operator|=
operator|new
name|LocalDirectoryTrackingIndexCopier
argument_list|(
name|executorService
argument_list|,
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|FailIfDefinedEditorProvider
name|luceneEditorProvider
init|=
operator|new
name|FailIfDefinedEditorProvider
argument_list|(
name|copier
argument_list|)
decl_stmt|;
name|IndexEditorProvider
name|editorProvider
init|=
operator|new
name|CompositeIndexEditorProvider
argument_list|(
operator|new
name|NodeCounterEditorProvider
argument_list|()
argument_list|,
name|luceneEditorProvider
argument_list|)
decl_stmt|;
name|NodeStore
name|store
init|=
operator|new
name|MemoryNodeStore
argument_list|(
name|INITIAL_CONTENT
argument_list|)
decl_stmt|;
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|(
name|store
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
decl_stmt|;
name|root
operator|=
name|oak
operator|.
name|createRoot
argument_list|()
expr_stmt|;
name|failOnDemandValidatorProvider
operator|=
operator|new
name|FailOnDemandValidatorProvider
argument_list|()
expr_stmt|;
name|asyncIndexUpdate
operator|=
operator|new
name|AsyncIndexUpdate
argument_list|(
literal|"async"
argument_list|,
name|store
argument_list|,
name|editorProvider
argument_list|)
expr_stmt|;
name|asyncIndexUpdate
operator|.
name|setValidatorProviders
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|failOnDemandValidatorProvider
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|IOException
block|{
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|workingReindexDirCleanUpOnFailureOfOtherIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"fails"
argument_list|,
literal|"foo"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|asyncIndexUpdate
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Indexing mustn't be failing"
argument_list|,
name|asyncIndexUpdate
operator|.
name|isFailing
argument_list|()
argument_list|)
expr_stmt|;
name|copier
operator|.
name|clearStats
argument_list|()
expr_stmt|;
name|createIndex
argument_list|(
literal|"reindexing"
argument_list|,
literal|"foo"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addChild
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
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|asyncIndexUpdate
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Indexing must fail"
argument_list|,
name|asyncIndexUpdate
operator|.
name|isFailing
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|File
argument_list|>
name|reindexingDirPaths
init|=
name|copier
operator|.
name|getReindexingDirPaths
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reindexingDirPaths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|reindexingDir
init|=
name|reindexingDirPaths
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Reindexing directories must get cleaned up on failure"
argument_list|,
name|reindexingDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|copier
operator|.
name|getDirs
argument_list|()
operator|.
name|forEach
argument_list|(
parameter_list|(
name|key
parameter_list|,
name|value
parameter_list|)
lambda|->
name|assertTrue
argument_list|(
literal|"Writer for "
operator|+
name|key
operator|+
literal|" must be closed"
argument_list|,
name|value
operator|.
name|isClosed
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|workingReindexDirCleanUpOnFailureOfMerge
parameter_list|()
throws|throws
name|Exception
block|{
name|failOnDemandValidatorProvider
operator|.
name|shouldFail
operator|=
literal|true
expr_stmt|;
name|createIndex
argument_list|(
literal|"reindexing"
argument_list|,
literal|"foo"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addChild
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
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|asyncIndexUpdate
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Indexing must fail"
argument_list|,
name|asyncIndexUpdate
operator|.
name|isFailing
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|File
argument_list|>
name|reindexingDirPaths
init|=
name|copier
operator|.
name|getReindexingDirPaths
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reindexingDirPaths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|reindexingDir
init|=
name|reindexingDirPaths
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Reindexing directories must get cleaned up on failure"
argument_list|,
name|reindexingDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|copier
operator|.
name|getDirs
argument_list|()
operator|.
name|forEach
argument_list|(
parameter_list|(
name|key
parameter_list|,
name|value
parameter_list|)
lambda|->
name|assertTrue
argument_list|(
literal|"Writer for "
operator|+
name|key
operator|+
literal|" must be closed"
argument_list|,
name|value
operator|.
name|isClosed
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|workingIndexDirDoesNotCleanUpOnFailureOfOtherIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"fails"
argument_list|,
literal|"foo"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|createIndex
argument_list|(
literal|"working"
argument_list|,
literal|"foo"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|asyncIndexUpdate
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Indexing mustn't be failing"
argument_list|,
name|asyncIndexUpdate
operator|.
name|isFailing
argument_list|()
argument_list|)
expr_stmt|;
name|copier
operator|.
name|clearStats
argument_list|()
expr_stmt|;
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addChild
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
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|asyncIndexUpdate
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Indexing must fail"
argument_list|,
name|asyncIndexUpdate
operator|.
name|isFailing
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|File
argument_list|>
name|reindexingDirPaths
init|=
name|copier
operator|.
name|getReindexingDirPaths
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"No directories are reindexing"
argument_list|,
literal|0
argument_list|,
name|reindexingDirPaths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Number of open directories aren't as expected"
argument_list|,
literal|2
argument_list|,
name|copier
operator|.
name|getDirPaths
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|copier
operator|.
name|getDirPaths
argument_list|()
operator|.
name|forEach
argument_list|(
parameter_list|(
name|key
parameter_list|,
name|value
parameter_list|)
lambda|->
name|assertTrue
argument_list|(
name|key
operator|+
literal|" must not get cleaned up on failure"
argument_list|,
name|value
operator|.
name|exists
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|copier
operator|.
name|getDirs
argument_list|()
operator|.
name|forEach
argument_list|(
parameter_list|(
name|key
parameter_list|,
name|value
parameter_list|)
lambda|->
name|assertTrue
argument_list|(
literal|"Writer for "
operator|+
name|key
operator|+
literal|" must be closed"
argument_list|,
name|value
operator|.
name|isClosed
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|workingIndexDirDoesNotCleanUpOnFailureOfMerge
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"working"
argument_list|,
literal|"foo"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|asyncIndexUpdate
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Indexing mustn't be failing"
argument_list|,
name|asyncIndexUpdate
operator|.
name|isFailing
argument_list|()
argument_list|)
expr_stmt|;
name|copier
operator|.
name|clearStats
argument_list|()
expr_stmt|;
name|failOnDemandValidatorProvider
operator|.
name|shouldFail
operator|=
literal|true
expr_stmt|;
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addChild
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
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|asyncIndexUpdate
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Indexing must fail"
argument_list|,
name|asyncIndexUpdate
operator|.
name|isFailing
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|File
argument_list|>
name|reindexingDirPaths
init|=
name|copier
operator|.
name|getReindexingDirPaths
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"No directories are reindexing."
argument_list|,
literal|0
argument_list|,
name|reindexingDirPaths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Number of open directories aren't as expected"
argument_list|,
literal|1
argument_list|,
name|copier
operator|.
name|getDirPaths
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|copier
operator|.
name|getDirPaths
argument_list|()
operator|.
name|forEach
argument_list|(
parameter_list|(
name|key
parameter_list|,
name|value
parameter_list|)
lambda|->
name|assertTrue
argument_list|(
name|key
operator|+
literal|" must not get cleaned up on failure"
argument_list|,
name|value
operator|.
name|exists
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|copier
operator|.
name|getDirs
argument_list|()
operator|.
name|forEach
argument_list|(
parameter_list|(
name|key
parameter_list|,
name|value
parameter_list|)
lambda|->
name|assertTrue
argument_list|(
literal|"Writer for "
operator|+
name|key
operator|+
literal|" must be closed"
argument_list|,
name|value
operator|.
name|isClosed
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createIndex
parameter_list|(
name|String
name|idxName
parameter_list|,
name|String
name|propName
parameter_list|,
name|boolean
name|shouldFail
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|IndexDefinitionBuilder
name|idxBuilder
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
decl_stmt|;
name|idxBuilder
operator|.
name|includedPaths
argument_list|(
name|TEST_CONTENT_PATH
argument_list|)
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
name|propName
argument_list|)
operator|.
name|propertyIndex
argument_list|()
expr_stmt|;
name|Tree
name|idx
init|=
name|idxBuilder
operator|.
name|build
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/oak:index"
argument_list|)
operator|.
name|addChild
argument_list|(
name|idxName
argument_list|)
argument_list|)
decl_stmt|;
name|idx
operator|.
name|setProperty
argument_list|(
literal|"shouldFail"
argument_list|,
name|shouldFail
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|static
class|class
name|LocalDirectoryTrackingIndexCopier
extends|extends
name|IndexCopier
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CopyOnWriteDirectory
argument_list|>
name|dirs
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
name|dirPaths
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|File
argument_list|>
name|reindexingDirPaths
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|LocalDirectoryTrackingIndexCopier
parameter_list|(
name|Executor
name|executor
parameter_list|,
name|File
name|indexRootDir
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|executor
argument_list|,
name|indexRootDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Directory
name|wrapForWrite
parameter_list|(
name|LuceneIndexDefinition
name|definition
parameter_list|,
name|Directory
name|remote
parameter_list|,
name|boolean
name|reindexMode
parameter_list|,
name|String
name|dirName
parameter_list|,
name|COWDirectoryTracker
name|cowDirectoryTracker
parameter_list|)
throws|throws
name|IOException
block|{
name|CopyOnWriteDirectory
name|dir
init|=
operator|(
name|CopyOnWriteDirectory
operator|)
name|super
operator|.
name|wrapForWrite
argument_list|(
name|definition
argument_list|,
name|remote
argument_list|,
name|reindexMode
argument_list|,
name|dirName
argument_list|,
name|cowDirectoryTracker
argument_list|)
decl_stmt|;
name|String
name|indexPath
init|=
name|definition
operator|.
name|getIndexPath
argument_list|()
decl_stmt|;
name|dirs
operator|.
name|put
argument_list|(
name|indexPath
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|File
name|dirPath
init|=
name|getIndexDir
argument_list|(
name|definition
argument_list|,
name|indexPath
argument_list|,
name|dirName
argument_list|)
decl_stmt|;
name|dirPaths
operator|.
name|put
argument_list|(
name|indexPath
argument_list|,
name|dirPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|reindexMode
condition|)
block|{
name|reindexingDirPaths
operator|.
name|add
argument_list|(
name|dirPath
argument_list|)
expr_stmt|;
block|}
return|return
name|dir
return|;
block|}
name|void
name|clearStats
parameter_list|()
block|{
name|dirs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|dirPaths
operator|.
name|clear
argument_list|()
expr_stmt|;
name|reindexingDirPaths
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|CopyOnWriteDirectory
argument_list|>
name|getDirs
parameter_list|()
block|{
return|return
name|dirs
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
name|getDirPaths
parameter_list|()
block|{
return|return
name|dirPaths
return|;
block|}
name|Set
argument_list|<
name|File
argument_list|>
name|getReindexingDirPaths
parameter_list|()
block|{
return|return
name|reindexingDirPaths
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|FailIfDefinedEditorProvider
extends|extends
name|LuceneIndexEditorProvider
block|{
name|FailIfDefinedEditorProvider
parameter_list|(
name|IndexCopier
name|copier
parameter_list|)
block|{
name|super
argument_list|(
name|copier
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|getIndexEditor
parameter_list|(
annotation|@
name|NotNull
name|String
name|type
parameter_list|,
annotation|@
name|NotNull
name|NodeBuilder
name|definition
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|root
parameter_list|,
annotation|@
name|NotNull
name|IndexUpdateCallback
name|callback
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|Editor
name|editor
init|=
name|super
operator|.
name|getIndexEditor
argument_list|(
name|type
argument_list|,
name|definition
argument_list|,
name|root
argument_list|,
name|callback
argument_list|)
decl_stmt|;
if|if
condition|(
name|definition
operator|.
name|getBoolean
argument_list|(
literal|"shouldFail"
argument_list|)
condition|)
block|{
name|editor
operator|=
operator|new
name|FailOnLeavePathEditor
argument_list|(
name|editor
argument_list|,
name|TEST_CONTENT_PATH
argument_list|)
expr_stmt|;
block|}
return|return
name|editor
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|FailOnLeavePathEditor
implements|implements
name|Editor
block|{
specifier|private
specifier|final
name|Editor
name|delegate
decl_stmt|;
specifier|private
specifier|final
name|String
name|failingPath
decl_stmt|;
specifier|final
name|String
name|currPath
decl_stmt|;
name|FailOnLeavePathEditor
parameter_list|(
name|Editor
name|delegate
parameter_list|,
name|String
name|failingPath
parameter_list|)
block|{
name|this
argument_list|(
name|delegate
argument_list|,
name|failingPath
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|private
name|FailOnLeavePathEditor
parameter_list|(
name|Editor
name|delegate
parameter_list|,
name|String
name|failingPath
parameter_list|,
name|String
name|parentPath
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
operator|!=
literal|null
condition|?
name|delegate
else|:
operator|new
name|DefaultEditor
argument_list|()
expr_stmt|;
name|this
operator|.
name|failingPath
operator|=
name|failingPath
expr_stmt|;
name|this
operator|.
name|currPath
operator|=
operator|(
literal|"/"
operator|.
name|equals
argument_list|(
name|parentPath
argument_list|)
condition|?
name|parentPath
else|:
name|parentPath
operator|+
literal|"/"
operator|)
operator|+
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|enter
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|delegate
operator|.
name|enter
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|leave
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|delegate
operator|.
name|leave
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
comment|// delegate call before failing
if|if
condition|(
name|failingPath
operator|.
name|equals
argument_list|(
name|currPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"index-fail"
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|delegate
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|delegate
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|delegate
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Editor
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
operator|new
name|FailOnLeavePathEditor
argument_list|(
name|delegate
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
argument_list|,
name|failingPath
argument_list|,
name|currPath
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Editor
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
operator|new
name|FailOnLeavePathEditor
argument_list|(
name|delegate
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
argument_list|,
name|failingPath
argument_list|,
name|currPath
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Editor
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
operator|new
name|FailOnLeavePathEditor
argument_list|(
name|delegate
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
argument_list|,
name|failingPath
argument_list|,
name|currPath
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
specifier|static
class|class
name|FailOnDemandValidatorProvider
extends|extends
name|ValidatorProvider
block|{
name|boolean
name|shouldFail
decl_stmt|;
specifier|static
specifier|final
name|String
name|FAILING_PATH_FRAGMENT
init|=
name|INDEX_DATA_CHILD_NAME
decl_stmt|;
annotation|@
name|Override
specifier|protected
annotation|@
name|Nullable
name|Validator
name|getRootValidator
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
return|return
operator|new
name|FailOnDemandValidator
argument_list|(
operator|new
name|DefaultValidator
argument_list|()
argument_list|)
return|;
block|}
class|class
name|FailOnDemandValidator
extends|extends
name|FailOnLeavePathEditor
implements|implements
name|Validator
block|{
specifier|final
name|Validator
name|delegate
decl_stmt|;
name|FailOnDemandValidator
parameter_list|(
name|Validator
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
specifier|private
name|FailOnDemandValidator
parameter_list|(
name|Validator
name|delegate
parameter_list|,
name|String
name|parentPath
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|,
literal|""
argument_list|,
name|parentPath
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
operator|!=
literal|null
condition|?
name|delegate
else|:
operator|new
name|DefaultValidator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|leave
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|super
operator|.
name|leave
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
if|if
condition|(
name|shouldFail
operator|&&
name|currPath
operator|.
name|contains
argument_list|(
name|FAILING_PATH_FRAGMENT
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"validator-fail"
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Validator
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
operator|new
name|FailOnDemandValidator
argument_list|(
name|delegate
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
argument_list|,
name|currPath
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Validator
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
operator|new
name|FailOnDemandValidator
argument_list|(
name|delegate
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
argument_list|,
name|currPath
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Validator
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
operator|new
name|FailOnDemandValidator
argument_list|(
name|delegate
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
argument_list|,
name|currPath
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit
