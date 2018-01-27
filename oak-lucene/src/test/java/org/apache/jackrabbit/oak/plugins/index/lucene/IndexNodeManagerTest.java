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
name|Collections
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
name|OakDirectory
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
name|hybrid
operator|.
name|NRTIndex
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
name|hybrid
operator|.
name|NRTIndexFactory
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
name|hybrid
operator|.
name|ReaderRefreshPolicy
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
name|index
operator|.
name|lucene
operator|.
name|writer
operator|.
name|IndexWriterUtils
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
name|Mounts
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
name|index
operator|.
name|IndexWriter
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
name|IndexWriterConfig
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
name|FieldNames
operator|.
name|PATH
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doReturn
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|spy
import|;
end_import

begin_class
specifier|public
class|class
name|IndexNodeManagerTest
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
name|IndexCopier
name|indexCopier
decl_stmt|;
specifier|private
name|NRTIndexFactory
name|nrtFactory
decl_stmt|;
specifier|private
name|LuceneIndexReaderFactory
name|readerFactory
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
name|indexCopier
operator|=
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
expr_stmt|;
name|nrtFactory
operator|=
operator|new
name|NRTIndexFactory
argument_list|(
name|indexCopier
argument_list|,
name|StatisticsProvider
operator|.
name|NOOP
argument_list|)
expr_stmt|;
name|readerFactory
operator|=
operator|new
name|DefaultIndexReaderFactory
argument_list|(
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
argument_list|,
name|indexCopier
argument_list|)
expr_stmt|;
name|LuceneIndexEditorContext
operator|.
name|configureUniqueId
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{
name|nrtFactory
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexCopier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nullIndexNode
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|IndexNodeManager
operator|.
name|open
argument_list|(
literal|"/foo"
argument_list|,
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|readerFactory
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|IndexNodeManager
operator|.
name|open
argument_list|(
literal|"/foo"
argument_list|,
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|readerFactory
argument_list|,
name|nrtFactory
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nonNullIndex_OnlyNRT
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexNodeManager
name|nodeManager
init|=
name|IndexNodeManager
operator|.
name|open
argument_list|(
literal|"/foo"
argument_list|,
name|root
argument_list|,
name|createNRTIndex
argument_list|()
argument_list|,
name|readerFactory
argument_list|,
name|nrtFactory
argument_list|)
decl_stmt|;
name|IndexNode
name|node
init|=
name|nodeManager
operator|.
name|acquire
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|node
operator|.
name|getSearcher
argument_list|()
argument_list|)
expr_stmt|;
name|TopDocs
name|docs
init|=
name|node
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
name|PATH
argument_list|,
literal|"/content/en"
argument_list|)
argument_list|)
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|node
operator|.
name|release
argument_list|()
expr_stmt|;
name|node
operator|.
name|getLocalWriter
argument_list|()
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
name|node
operator|.
name|refreshReadersOnWriteIfRequired
argument_list|()
expr_stmt|;
name|node
operator|=
name|nodeManager
operator|.
name|acquire
argument_list|()
expr_stmt|;
name|docs
operator|=
name|node
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
name|PATH
argument_list|,
literal|"/content/en"
argument_list|)
argument_list|)
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nullIndex_NonFreshIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|builder
init|=
name|createNRTIndex
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|rootBuilder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|rootBuilder
operator|.
name|child
argument_list|(
name|IndexNodeManager
operator|.
name|ASYNC
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"async"
argument_list|,
literal|"async"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|IndexNodeManager
operator|.
name|open
argument_list|(
literal|"/foo"
argument_list|,
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|readerFactory
argument_list|,
name|nrtFactory
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|lockAndRefreshPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|state
init|=
name|createNRTIndex
argument_list|()
decl_stmt|;
name|IndexDefinition
name|definition
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|state
argument_list|,
literal|"/foo"
argument_list|)
decl_stmt|;
name|NRTIndex
name|nrtIndex
init|=
name|nrtFactory
operator|.
name|createIndex
argument_list|(
name|definition
argument_list|)
decl_stmt|;
name|NRTIndex
name|mock
init|=
name|spy
argument_list|(
name|nrtIndex
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
operator|new
name|FailingPolicy
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|mock
argument_list|)
operator|.
name|getRefreshPolicy
argument_list|()
expr_stmt|;
name|IndexNodeManager
name|node
init|=
operator|new
name|IndexNodeManager
argument_list|(
literal|"/foo"
argument_list|,
name|definition
argument_list|,
name|Collections
operator|.
expr|<
name|LuceneIndexReader
operator|>
name|emptyList
argument_list|()
argument_list|,
name|mock
argument_list|)
decl_stmt|;
try|try
block|{
name|node
operator|.
name|acquire
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{          }
name|node
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexOpenedBeforeFistCycle
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|nrtIndex
init|=
name|createNRTIndex
argument_list|()
decl_stmt|;
name|NodeState
name|asyncIndex
init|=
name|nrtIndex
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"async"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"async"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|nonAsyncIndex
decl_stmt|;
block|{
name|NodeBuilder
name|builder
init|=
name|nrtIndex
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|removeProperty
argument_list|(
literal|"async"
argument_list|)
expr_stmt|;
name|nonAsyncIndex
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
literal|"nrtIndex; Non existing /:async"
argument_list|,
name|IndexNodeManager
operator|.
name|open
argument_list|(
literal|"/foo"
argument_list|,
name|root
argument_list|,
name|nrtIndex
argument_list|,
name|readerFactory
argument_list|,
name|nrtFactory
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"asyncIndex; Non existing /:async"
argument_list|,
name|IndexNodeManager
operator|.
name|open
argument_list|(
literal|"/foo"
argument_list|,
name|root
argument_list|,
name|asyncIndex
argument_list|,
name|readerFactory
argument_list|,
name|nrtFactory
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"nonAsyncIndex; Non existing /:async"
argument_list|,
name|IndexNodeManager
operator|.
name|open
argument_list|(
literal|"/foo"
argument_list|,
name|root
argument_list|,
name|nonAsyncIndex
argument_list|,
name|readerFactory
argument_list|,
name|nrtFactory
argument_list|)
argument_list|)
expr_stmt|;
comment|// Fake an empty /:async - first indexing cycle isn't done yet
name|builder
operator|.
name|child
argument_list|(
literal|":async"
argument_list|)
expr_stmt|;
name|root
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"nrtIndex; empty /:async"
argument_list|,
name|IndexNodeManager
operator|.
name|open
argument_list|(
literal|"/foo"
argument_list|,
name|root
argument_list|,
name|nrtIndex
argument_list|,
name|readerFactory
argument_list|,
name|nrtFactory
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"asyncIndex; empty /:async"
argument_list|,
name|IndexNodeManager
operator|.
name|open
argument_list|(
literal|"/foo"
argument_list|,
name|root
argument_list|,
name|asyncIndex
argument_list|,
name|readerFactory
argument_list|,
name|nrtFactory
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"nonAsyncIndex; empty /:async"
argument_list|,
name|IndexNodeManager
operator|.
name|open
argument_list|(
literal|"/foo"
argument_list|,
name|root
argument_list|,
name|nonAsyncIndex
argument_list|,
name|readerFactory
argument_list|,
name|nrtFactory
argument_list|)
argument_list|)
expr_stmt|;
comment|// Fake async indexing cycle done with no data
name|builder
operator|.
name|child
argument_list|(
literal|":async"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"async"
argument_list|,
literal|"some-random-id"
argument_list|)
expr_stmt|;
name|root
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"nrtIndex; fake async cycle run"
argument_list|,
name|IndexNodeManager
operator|.
name|open
argument_list|(
literal|"/foo"
argument_list|,
name|root
argument_list|,
name|nrtIndex
argument_list|,
name|readerFactory
argument_list|,
name|nrtFactory
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"asyncIndex; fake async cycle run"
argument_list|,
name|IndexNodeManager
operator|.
name|open
argument_list|(
literal|"/foo"
argument_list|,
name|root
argument_list|,
name|asyncIndex
argument_list|,
name|readerFactory
argument_list|,
name|nrtFactory
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"nonAsyncIndex; fake async cycle run"
argument_list|,
name|IndexNodeManager
operator|.
name|open
argument_list|(
literal|"/foo"
argument_list|,
name|root
argument_list|,
name|nonAsyncIndex
argument_list|,
name|readerFactory
argument_list|,
name|nrtFactory
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexWithIndexedDataOpenedBeforeFistCycle
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|nrtIndex
init|=
name|createNRTIndex
argument_list|()
decl_stmt|;
block|{
name|NodeBuilder
name|indexBuilder
init|=
name|nrtIndex
operator|.
name|builder
argument_list|()
decl_stmt|;
name|IndexDefinition
name|indexDefinition
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|indexBuilder
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/foo"
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|config
init|=
name|IndexWriterUtils
operator|.
name|getIndexWriterConfig
argument_list|(
name|indexDefinition
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|OakDirectory
name|directory
init|=
operator|new
name|OakDirectory
argument_list|(
name|indexBuilder
argument_list|,
name|indexDefinition
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|newDoc
argument_list|(
literal|"/content/en"
argument_list|)
operator|.
name|getFields
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|nrtIndex
operator|=
name|indexBuilder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
block|}
name|NodeState
name|asyncIndex
init|=
name|nrtIndex
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"async"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"async"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|nonAsyncIndex
decl_stmt|;
block|{
name|NodeBuilder
name|builder
init|=
name|nrtIndex
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|removeProperty
argument_list|(
literal|"async"
argument_list|)
expr_stmt|;
name|nonAsyncIndex
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
block|}
comment|// absent or empty /:async doesn't make sense with already indexed data available.
comment|// So, we're considering only this case.
comment|// Fake async indexing cycle done with no data
name|builder
operator|.
name|child
argument_list|(
literal|":async"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"async"
argument_list|,
literal|"some-random-id"
argument_list|)
expr_stmt|;
name|root
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"nrtIndex; fake async cycle run"
argument_list|,
name|IndexNodeManager
operator|.
name|open
argument_list|(
literal|"/foo"
argument_list|,
name|root
argument_list|,
name|nrtIndex
argument_list|,
name|readerFactory
argument_list|,
name|nrtFactory
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"asyncIndex; fake async cycle run"
argument_list|,
name|IndexNodeManager
operator|.
name|open
argument_list|(
literal|"/foo"
argument_list|,
name|root
argument_list|,
name|asyncIndex
argument_list|,
name|readerFactory
argument_list|,
name|nrtFactory
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"nonAsyncIndex; fake async cycle run"
argument_list|,
name|IndexNodeManager
operator|.
name|open
argument_list|(
literal|"/foo"
argument_list|,
name|root
argument_list|,
name|nonAsyncIndex
argument_list|,
name|readerFactory
argument_list|,
name|nrtFactory
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|hasIndexingRun
parameter_list|()
block|{
name|NodeState
name|nrtIndex
init|=
name|createNRTIndex
argument_list|()
decl_stmt|;
name|NodeState
name|asyncIndex
init|=
name|nrtIndex
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"async"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"async"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|nonAsyncIndex
decl_stmt|;
block|{
name|NodeBuilder
name|builder
init|=
name|nrtIndex
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|removeProperty
argument_list|(
literal|"async"
argument_list|)
expr_stmt|;
name|nonAsyncIndex
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"nrtIndex; Non existing /:async"
argument_list|,
name|IndexNodeManager
operator|.
name|hasAsyncIndexerRun
argument_list|(
name|root
argument_list|,
literal|"/foo"
argument_list|,
name|nrtIndex
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"asyncIndex; Non existing /:async"
argument_list|,
name|IndexNodeManager
operator|.
name|hasAsyncIndexerRun
argument_list|(
name|root
argument_list|,
literal|"/foo"
argument_list|,
name|asyncIndex
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"nonAsyncIndex; Non existing /:async"
argument_list|,
name|IndexNodeManager
operator|.
name|hasAsyncIndexerRun
argument_list|(
name|root
argument_list|,
literal|"/foo"
argument_list|,
name|nonAsyncIndex
argument_list|)
argument_list|)
expr_stmt|;
comment|// Fake an empty /:async - first indexing cycle isn't done yet
name|builder
operator|.
name|child
argument_list|(
literal|":async"
argument_list|)
expr_stmt|;
name|root
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"nrtIndex; Empty /:async"
argument_list|,
name|IndexNodeManager
operator|.
name|hasAsyncIndexerRun
argument_list|(
name|root
argument_list|,
literal|"/foo"
argument_list|,
name|nrtIndex
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"asyncIndex; Non existing /:async"
argument_list|,
name|IndexNodeManager
operator|.
name|hasAsyncIndexerRun
argument_list|(
name|root
argument_list|,
literal|"/foo"
argument_list|,
name|asyncIndex
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"nonAsyncIndex; Non existing /:async"
argument_list|,
name|IndexNodeManager
operator|.
name|hasAsyncIndexerRun
argument_list|(
name|root
argument_list|,
literal|"/foo"
argument_list|,
name|nonAsyncIndex
argument_list|)
argument_list|)
expr_stmt|;
comment|// Fake async indexing cycle done
name|builder
operator|.
name|child
argument_list|(
literal|":async"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"async"
argument_list|,
literal|"some-random-id"
argument_list|)
expr_stmt|;
name|root
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"nrtIndex; fake async cycle run"
argument_list|,
name|IndexNodeManager
operator|.
name|hasAsyncIndexerRun
argument_list|(
name|root
argument_list|,
literal|"/foo"
argument_list|,
name|nrtIndex
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"asyncIndex; fake async cycle run"
argument_list|,
name|IndexNodeManager
operator|.
name|hasAsyncIndexerRun
argument_list|(
name|root
argument_list|,
literal|"/foo"
argument_list|,
name|asyncIndex
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"nonAsyncIndex; fake async cycle run"
argument_list|,
name|IndexNodeManager
operator|.
name|hasAsyncIndexerRun
argument_list|(
name|root
argument_list|,
literal|"/foo"
argument_list|,
name|nonAsyncIndex
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|NodeState
name|createNRTIndex
parameter_list|()
block|{
name|IndexDefinitionBuilder
name|idx
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
decl_stmt|;
name|idx
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|propertyIndex
argument_list|()
expr_stmt|;
name|idx
operator|.
name|async
argument_list|(
literal|"async"
argument_list|,
literal|"sync"
argument_list|)
expr_stmt|;
return|return
name|idx
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
specifier|static
class|class
name|FailingPolicy
implements|implements
name|ReaderRefreshPolicy
block|{
annotation|@
name|Override
specifier|public
name|void
name|refreshOnReadIfRequired
parameter_list|(
name|Runnable
name|refreshCallback
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|refreshOnWriteIfRequired
parameter_list|(
name|Runnable
name|refreshCallback
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

