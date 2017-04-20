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
name|ArrayList
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|Result
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
name|LuceneIndexReader
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
name|lucene
operator|.
name|writer
operator|.
name|DefaultIndexWriterFactory
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
name|writer
operator|.
name|LuceneIndexWriter
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
name|writer
operator|.
name|LuceneIndexWriterFactory
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
name|writer
operator|.
name|MultiplexersLucene
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
name|multiplex
operator|.
name|SimpleMountInfoProvider
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
name|query
operator|.
name|NodeStateNodeTypeInfoProvider
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
name|QueryEngineSettings
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
name|ast
operator|.
name|NodeTypeInfo
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
name|ast
operator|.
name|NodeTypeInfoProvider
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
name|ast
operator|.
name|Operator
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
name|ast
operator|.
name|SelectorImpl
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
name|index
operator|.
name|FilterImpl
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
name|PropertyValues
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
name|QueryIndex
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
name|NodeStateUtils
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
name|QueryEngine
operator|.
name|NO_BINDINGS
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
name|LuceneIndexConstants
operator|.
name|ORDERED_PROP_NAMES
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
name|LuceneIndexConstants
operator|.
name|PROP_NODE
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
name|index
operator|.
name|lucene
operator|.
name|TestUtil
operator|.
name|newDoc
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
name|InitialContent
operator|.
name|INITIAL_CONTENT
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

begin_class
specifier|public
class|class
name|MultiplexingLucenePropertyIndexTest
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
name|NodeState
name|initialContent
init|=
name|INITIAL_CONTENT
decl_stmt|;
specifier|private
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|private
name|MountInfoProvider
name|mip
init|=
name|SimpleMountInfoProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"foo"
argument_list|,
literal|"/libs"
argument_list|,
literal|"/apps"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
name|NodeStore
name|nodeStore
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
name|LuceneIndexEditorProvider
name|editorProvider
init|=
operator|new
name|LuceneIndexEditorProvider
argument_list|(
name|copier
argument_list|,
operator|new
name|ExtractedTextCache
argument_list|(
literal|10
operator|*
name|FileUtils
operator|.
name|ONE_MB
argument_list|,
literal|100
argument_list|)
argument_list|,
literal|null
argument_list|,
name|mip
argument_list|)
decl_stmt|;
name|LuceneIndexProvider
name|provider
init|=
operator|new
name|LuceneIndexProvider
argument_list|(
operator|new
name|IndexTracker
argument_list|(
operator|new
name|DefaultIndexReaderFactory
argument_list|(
name|mip
argument_list|,
name|copier
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|nodeStore
operator|=
operator|new
name|MemoryNodeStore
argument_list|()
expr_stmt|;
return|return
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
name|createContentRepository
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|numDocsIsSumOfAllReaders
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|defnBuilder
init|=
name|newLucenePropertyIndexDefinition
argument_list|(
name|builder
argument_list|,
literal|"test"
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
name|IndexDefinition
name|defn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|initialContent
argument_list|,
name|defnBuilder
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/foo"
argument_list|)
decl_stmt|;
comment|//1. Have 2 reader created by writes in 2 diff mounts
name|LuceneIndexWriterFactory
name|factory
init|=
operator|new
name|DefaultIndexWriterFactory
argument_list|(
name|mip
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LuceneIndexWriter
name|writer
init|=
name|factory
operator|.
name|newInstance
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
literal|"/content/en"
argument_list|,
name|newDoc
argument_list|(
literal|"/content/en"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
literal|"/libs/config"
argument_list|,
name|newDoc
argument_list|(
literal|"/libs/config"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|//2. Construct the readers
name|LuceneIndexReaderFactory
name|readerFactory
init|=
operator|new
name|DefaultIndexReaderFactory
argument_list|(
name|mip
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LuceneIndexReader
argument_list|>
name|readers
init|=
name|readerFactory
operator|.
name|createReaders
argument_list|(
name|defn
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/foo"
argument_list|)
decl_stmt|;
name|IndexNode
name|node
init|=
operator|new
name|IndexNode
argument_list|(
literal|"foo"
argument_list|,
name|defn
argument_list|,
name|readers
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|//3 Obtain the plan
name|FilterImpl
name|filter
init|=
name|createFilter
argument_list|(
literal|"nt:base"
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"foo"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexPlanner
name|planner
init|=
operator|new
name|IndexPlanner
argument_list|(
name|node
argument_list|,
literal|"/foo"
argument_list|,
name|filter
argument_list|,
name|Collections
operator|.
expr|<
name|QueryIndex
operator|.
name|OrderEntry
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|QueryIndex
operator|.
name|IndexPlan
name|plan
init|=
name|planner
operator|.
name|getPlan
argument_list|()
decl_stmt|;
comment|//Count should be sum of both readers
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|plan
operator|.
name|getEstimatedEntryCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|propertyIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|idxName
init|=
literal|"multitest"
decl_stmt|;
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
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|createPath
argument_list|(
literal|"/libs/a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|createPath
argument_list|(
literal|"/libs/b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar2"
argument_list|)
expr_stmt|;
name|createPath
argument_list|(
literal|"/content/a"
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
comment|//There should be 2 index dir due to mount
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getIndexDirNames
argument_list|(
name|idxName
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|barQuery
init|=
literal|"select [jcr:path] from [nt:base] where [foo] = 'bar'"
decl_stmt|;
name|assertQuery
argument_list|(
name|barQuery
argument_list|,
name|of
argument_list|(
literal|"/libs/a"
argument_list|,
literal|"/content/a"
argument_list|)
argument_list|)
expr_stmt|;
name|Result
name|result
init|=
name|executeQuery
argument_list|(
name|barQuery
argument_list|,
name|SQL2
argument_list|,
name|NO_BINDINGS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|(
name|Result
operator|.
name|SizePrecision
operator|.
name|FAST_APPROXIMATION
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] where [foo] = 'bar2'"
argument_list|,
name|of
argument_list|(
literal|"/libs/b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|propertyIndexWithBatching
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|idxName
init|=
literal|"multitest"
decl_stmt|;
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
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|int
name|expectedSize
init|=
name|LucenePropertyIndex
operator|.
name|LUCENE_QUERY_BATCH_SIZE
operator|*
literal|2
operator|*
literal|2
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
name|LucenePropertyIndex
operator|.
name|LUCENE_QUERY_BATCH_SIZE
operator|*
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|createPath
argument_list|(
literal|"/libs/a"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|createPath
argument_list|(
literal|"/content/a"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getIndexDirNames
argument_list|(
name|idxName
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertResultSize
argument_list|(
literal|"select [jcr:path] from [nt:base] where [foo] = 'bar'"
argument_list|,
name|SQL2
argument_list|,
name|expectedSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|sortQueriesWithStringAndLong
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"test1"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
decl_stmt|;
name|idx
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|ORDERED_PROP_NAMES
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|propIdx
init|=
name|idx
operator|.
name|addChild
argument_list|(
name|PROP_NODE
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"baz"
argument_list|)
decl_stmt|;
name|propIdx
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_TYPE
argument_list|,
name|PropertyType
operator|.
name|TYPENAME_LONG
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|int
name|firstPropSize
init|=
literal|25
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
name|LucenePropertyIndexTest
operator|.
name|createStrings
argument_list|(
name|firstPropSize
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|longValues
init|=
name|LucenePropertyIndexTest
operator|.
name|createLongs
argument_list|(
name|LucenePropertyIndexTest
operator|.
name|NUMBER_OF_NODES
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LucenePropertyIndexTest
operator|.
name|Tuple2
argument_list|>
name|tuples
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|values
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|Tree
name|libs
init|=
name|createPath
argument_list|(
literal|"/libs"
argument_list|)
decl_stmt|;
name|Tree
name|content
init|=
name|createPath
argument_list|(
literal|"/content"
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
name|values
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|val
init|=
name|values
operator|.
name|get
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|firstPropSize
argument_list|)
argument_list|)
decl_stmt|;
name|Tree
name|base
init|=
operator|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|?
name|libs
else|:
name|content
operator|)
decl_stmt|;
name|Tree
name|child
init|=
name|base
operator|.
name|addChild
argument_list|(
literal|"n"
operator|+
name|i
argument_list|)
decl_stmt|;
name|child
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|child
operator|.
name|setProperty
argument_list|(
literal|"baz"
argument_list|,
name|longValues
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|child
operator|.
name|setProperty
argument_list|(
literal|"bar"
argument_list|,
literal|"baz"
argument_list|)
expr_stmt|;
name|tuples
operator|.
name|add
argument_list|(
operator|new
name|LucenePropertyIndexTest
operator|.
name|Tuple2
argument_list|(
name|val
argument_list|,
name|longValues
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|child
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertOrderedQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] where [bar] = 'baz' order by [foo] asc, [baz] desc"
argument_list|,
name|LucenePropertyIndexTest
operator|.
name|getSortedPaths
argument_list|(
name|tuples
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getIndexDirNames
parameter_list|(
name|String
name|indexName
parameter_list|)
block|{
name|NodeState
name|idxDefn
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"/oak:index/"
operator|+
name|indexName
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|childName
range|:
name|idxDefn
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|MultiplexersLucene
operator|.
name|isIndexDirName
argument_list|(
name|childName
argument_list|)
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|childName
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|names
return|;
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
specifier|private
name|FilterImpl
name|createFilter
parameter_list|(
name|String
name|nodeTypeName
parameter_list|)
block|{
name|NodeTypeInfoProvider
name|nodeTypes
init|=
operator|new
name|NodeStateNodeTypeInfoProvider
argument_list|(
name|initialContent
argument_list|)
decl_stmt|;
name|NodeTypeInfo
name|type
init|=
name|nodeTypes
operator|.
name|getNodeTypeInfo
argument_list|(
name|nodeTypeName
argument_list|)
decl_stmt|;
name|SelectorImpl
name|selector
init|=
operator|new
name|SelectorImpl
argument_list|(
name|type
argument_list|,
name|nodeTypeName
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|"SELECT * FROM ["
operator|+
name|nodeTypeName
operator|+
literal|"]"
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|void
name|assertOrderedQuery
parameter_list|(
name|String
name|sql
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
name|assertOrderedQuery
argument_list|(
name|sql
argument_list|,
name|paths
argument_list|,
name|SQL2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertOrderedQuery
parameter_list|(
name|String
name|sql
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|String
name|language
parameter_list|,
name|boolean
name|skipSort
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
name|executeQuery
argument_list|(
name|sql
argument_list|,
name|language
argument_list|,
literal|true
argument_list|,
name|skipSort
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|paths
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

