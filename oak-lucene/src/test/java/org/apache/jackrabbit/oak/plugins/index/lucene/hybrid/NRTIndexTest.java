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
name|IndexDefinition
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
name|LuceneIndexEditorContext
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
name|document
operator|.
name|Document
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
name|assertNotEquals
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
name|assertNotSame
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
name|assertSame
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
name|NRTIndexTest
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
name|indexFactory
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
name|indexFactory
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
name|indexFactory
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
name|getReaderWithoutWriter
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|idxDefn
init|=
name|getSyncIndexDefinition
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|NRTIndex
name|idx1
init|=
name|indexFactory
operator|.
name|createIndex
argument_list|(
name|idxDefn
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LuceneIndexReader
argument_list|>
name|readers
init|=
name|idx1
operator|.
name|getReaders
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|readers
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|readers
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|idx1
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|idx1
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
comment|//Closing multiple times should not raise exception
name|idx1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|writerCreation
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|idxDefn
init|=
name|getSyncIndexDefinition
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|NRTIndex
name|idx
init|=
name|indexFactory
operator|.
name|createIndex
argument_list|(
name|idxDefn
argument_list|)
decl_stmt|;
name|LuceneIndexWriter
name|writer
init|=
name|idx
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|idx
operator|.
name|getIndexDir
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LuceneIndexReader
argument_list|>
name|readers
init|=
name|idx
operator|.
name|getReaders
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|readers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LuceneIndexWriter
name|writer2
init|=
name|idx
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|writer
argument_list|,
name|writer2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|dirDeletedUponClose
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|idxDefn
init|=
name|getSyncIndexDefinition
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|NRTIndex
name|idx
init|=
name|indexFactory
operator|.
name|createIndex
argument_list|(
name|idxDefn
argument_list|)
decl_stmt|;
name|LuceneIndexWriter
name|writer
init|=
name|idx
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|File
name|indexDir
init|=
name|idx
operator|.
name|getIndexDir
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|indexDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|idx
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|indexDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|idx
operator|.
name|getReaders
argument_list|()
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
try|try
block|{
name|idx
operator|.
name|getWriter
argument_list|()
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
name|multipleUpdateForSamePath
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|idxDefn
init|=
name|getSyncIndexDefinition
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|NRTIndex
name|idx
init|=
name|indexFactory
operator|.
name|createIndex
argument_list|(
name|idxDefn
argument_list|)
decl_stmt|;
name|LuceneIndexWriter
name|writer
init|=
name|idx
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newPathField
argument_list|(
literal|"/a/b"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
literal|"/a/b"
argument_list|,
name|document
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|idx
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
name|writer
operator|.
name|updateDocument
argument_list|(
literal|"/a/b"
argument_list|,
name|document
argument_list|)
expr_stmt|;
comment|//Update for same path should not lead to deletion
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|idx
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
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|idx
operator|.
name|getPrimaryReader
argument_list|()
operator|.
name|getReader
argument_list|()
operator|.
name|numDeletedDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|previousIndexInitialized
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|idxDefn
init|=
name|getSyncIndexDefinition
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|NRTIndex
name|idx1
init|=
name|indexFactory
operator|.
name|createIndex
argument_list|(
name|idxDefn
argument_list|)
decl_stmt|;
name|LuceneIndexWriter
name|w1
init|=
name|idx1
operator|.
name|getWriter
argument_list|()
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
name|w1
operator|.
name|updateDocument
argument_list|(
literal|"/a/b"
argument_list|,
name|d1
argument_list|)
expr_stmt|;
name|NRTIndex
name|idx2
init|=
name|indexFactory
operator|.
name|createIndex
argument_list|(
name|idxDefn
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|idx2
operator|.
name|getReaders
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LuceneIndexWriter
name|w2
init|=
name|idx2
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|idx2
operator|.
name|getReaders
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|idx1
operator|.
name|getIndexDir
argument_list|()
argument_list|,
name|idx2
operator|.
name|getIndexDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|sameReaderIfNoChange
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|idxDefn
init|=
name|getSyncIndexDefinition
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|NRTIndex
name|idx1
init|=
name|indexFactory
operator|.
name|createIndex
argument_list|(
name|idxDefn
argument_list|)
decl_stmt|;
name|LuceneIndexWriter
name|w1
init|=
name|idx1
operator|.
name|getWriter
argument_list|()
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
name|w1
operator|.
name|updateDocument
argument_list|(
literal|"/a/b"
argument_list|,
name|d1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LuceneIndexReader
argument_list|>
name|readers
init|=
name|idx1
operator|.
name|getReaders
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|LuceneIndexReader
argument_list|>
name|readers2
init|=
name|idx1
operator|.
name|getReaders
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|readers
argument_list|,
name|readers2
argument_list|)
expr_stmt|;
name|w1
operator|.
name|updateDocument
argument_list|(
literal|"/a/b"
argument_list|,
name|d1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LuceneIndexReader
argument_list|>
name|readers3
init|=
name|idx1
operator|.
name|getReaders
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|readers2
argument_list|,
name|readers3
argument_list|)
expr_stmt|;
block|}
specifier|private
name|IndexDefinition
name|getSyncIndexDefinition
parameter_list|(
name|String
name|indexPath
parameter_list|)
block|{
name|TestUtil
operator|.
name|enableIndexingMode
argument_list|(
name|builder
argument_list|,
name|IndexingMode
operator|.
name|NRT
argument_list|)
expr_stmt|;
return|return
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|indexPath
argument_list|)
return|;
block|}
block|}
end_class

end_unit

