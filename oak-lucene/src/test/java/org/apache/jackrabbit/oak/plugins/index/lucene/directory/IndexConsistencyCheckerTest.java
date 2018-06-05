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
name|directory
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|InputStream
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
name|IOUtils
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
name|InitialContentHelper
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
name|Blob
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
name|Type
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
name|OakAnalyzer
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
name|IndexConsistencyChecker
operator|.
name|Level
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
name|IndexConsistencyChecker
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
name|memory
operator|.
name|ArrayBasedBlob
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
name|store
operator|.
name|Directory
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
name|util
operator|.
name|Version
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

begin_class
specifier|public
class|class
name|IndexConsistencyCheckerTest
block|{
specifier|private
name|NodeState
name|rootState
init|=
name|InitialContentHelper
operator|.
name|INITIAL_CONTENT
decl_stmt|;
specifier|private
name|NodeBuilder
name|idx
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
operator|.
name|build
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
annotation|@
name|Rule
specifier|public
specifier|final
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
annotation|@
name|Test
specifier|public
name|void
name|emptyIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexConsistencyChecker
name|checker
init|=
operator|new
name|IndexConsistencyChecker
argument_list|(
name|EMPTY_NODE
argument_list|,
literal|"/foo"
argument_list|,
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|Result
name|result
init|=
name|checker
operator|.
name|check
argument_list|(
name|Level
operator|.
name|BLOBS_ONLY
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|clean
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|typeMismatch
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|result
operator|.
name|indexPath
argument_list|,
literal|"/foo"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|blobsWithError
parameter_list|()
throws|throws
name|Exception
block|{
name|FailingBlob
name|failingBlob
init|=
operator|new
name|FailingBlob
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|idx
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
name|failingBlob
argument_list|)
expr_stmt|;
name|idx
operator|.
name|child
argument_list|(
literal|":index"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
name|failingBlob
argument_list|)
expr_stmt|;
name|idx
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|failingBlob
argument_list|,
name|failingBlob
argument_list|)
argument_list|,
name|Type
operator|.
name|BINARIES
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"a"
argument_list|,
name|idx
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|IndexConsistencyChecker
name|checker
init|=
operator|new
name|IndexConsistencyChecker
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/a"
argument_list|,
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|Result
name|result
init|=
name|checker
operator|.
name|check
argument_list|(
name|Level
operator|.
name|BLOBS_ONLY
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|clean
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|missingBlobs
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|blobSizeMismatch
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|result
operator|.
name|missingBlobIds
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|dumpResult
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|blobsWithSizeMismatch
parameter_list|()
throws|throws
name|Exception
block|{
name|FailingBlob
name|failingBlob
init|=
operator|new
name|FailingBlob
argument_list|(
literal|"foo"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|idx
operator|.
name|child
argument_list|(
literal|":index"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
name|failingBlob
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"a"
argument_list|,
name|idx
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|IndexConsistencyChecker
name|checker
init|=
operator|new
name|IndexConsistencyChecker
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/a"
argument_list|,
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|Result
name|result
init|=
name|checker
operator|.
name|check
argument_list|(
name|Level
operator|.
name|BLOBS_ONLY
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|clean
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|missingBlobs
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|blobSizeMismatch
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|invalidBlobIds
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|dumpResult
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|validIndexTest
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|defn
init|=
name|IndexDefinition
operator|.
name|newBuilder
argument_list|(
name|rootState
argument_list|,
name|idx
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/fooIndex"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
operator|new
name|OakDirectory
argument_list|(
name|idx
argument_list|,
literal|":data"
argument_list|,
name|defn
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|createIndex
argument_list|(
name|dir
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|dir
operator|=
operator|new
name|OakDirectory
argument_list|(
name|idx
argument_list|,
literal|":data2"
operator|+
name|MultiplexersLucene
operator|.
name|INDEX_DIR_SUFFIX
argument_list|,
name|defn
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|createIndex
argument_list|(
name|dir
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|rootState
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"fooIndex"
argument_list|,
name|idx
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|NodeState
name|indexState
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|IndexConsistencyChecker
name|checker
init|=
operator|new
name|IndexConsistencyChecker
argument_list|(
name|indexState
argument_list|,
literal|"/fooIndex"
argument_list|,
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|Result
name|result
init|=
name|checker
operator|.
name|check
argument_list|(
name|Level
operator|.
name|BLOBS_ONLY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|clean
argument_list|)
expr_stmt|;
name|checker
operator|=
operator|new
name|IndexConsistencyChecker
argument_list|(
name|indexState
argument_list|,
literal|"/fooIndex"
argument_list|,
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|checker
operator|.
name|check
argument_list|(
name|Level
operator|.
name|FULL
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|clean
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|dirStatus
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|dumpResult
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|missingFile
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|defn
init|=
name|IndexDefinition
operator|.
name|newBuilder
argument_list|(
name|rootState
argument_list|,
name|idx
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/fooIndex"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
operator|new
name|OakDirectory
argument_list|(
name|idx
argument_list|,
literal|":data"
argument_list|,
name|defn
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|createIndex
argument_list|(
name|dir
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|rootState
operator|.
name|builder
argument_list|()
decl_stmt|;
name|idx
operator|.
name|getChildNode
argument_list|(
literal|":data"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"segments.gen"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"fooIndex"
argument_list|,
name|idx
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|NodeState
name|indexState
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|IndexConsistencyChecker
name|checker
init|=
operator|new
name|IndexConsistencyChecker
argument_list|(
name|indexState
argument_list|,
literal|"/fooIndex"
argument_list|,
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|Result
name|result
init|=
name|checker
operator|.
name|check
argument_list|(
name|Level
operator|.
name|FULL
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|clean
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|dirStatus
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|missingFiles
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|result
operator|.
name|dirStatus
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|status
argument_list|)
expr_stmt|;
name|dumpResult
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|badFile
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|defn
init|=
name|IndexDefinition
operator|.
name|newBuilder
argument_list|(
name|rootState
argument_list|,
name|idx
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/fooIndex"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
operator|new
name|OakDirectory
argument_list|(
name|idx
argument_list|,
literal|":data"
argument_list|,
name|defn
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|createIndex
argument_list|(
name|dir
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|rootState
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|file
init|=
name|idx
operator|.
name|getChildNode
argument_list|(
literal|":data"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"_0.cfe"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Blob
argument_list|>
name|blobs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|file
operator|.
name|getProperty
argument_list|(
literal|"jcr:data"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARIES
argument_list|)
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|blobs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNewStream
argument_list|()
argument_list|,
name|baos
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|bytes
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|bytes
index|[
literal|0
index|]
operator|^
operator|(
literal|1
operator|<<
literal|3
operator|)
argument_list|)
expr_stmt|;
comment|//Flip the 3rd bit to make it corrupt
name|blobs
operator|.
name|set
argument_list|(
literal|0
argument_list|,
operator|new
name|ArrayBasedBlob
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|file
operator|.
name|setProperty
argument_list|(
literal|"jcr:data"
argument_list|,
name|blobs
argument_list|,
name|Type
operator|.
name|BINARIES
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"fooIndex"
argument_list|,
name|idx
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|NodeState
name|indexState
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|IndexConsistencyChecker
name|checker
init|=
operator|new
name|IndexConsistencyChecker
argument_list|(
name|indexState
argument_list|,
literal|"/fooIndex"
argument_list|,
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|Result
name|result
init|=
name|checker
operator|.
name|check
argument_list|(
name|Level
operator|.
name|FULL
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|clean
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|dirStatus
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|missingFiles
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|dirStatus
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|status
operator|.
name|clean
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createIndex
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|int
name|numOfDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Version
operator|.
name|LUCENE_47
argument_list|,
operator|new
name|OakAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_47
argument_list|)
argument_list|)
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
name|numOfDocs
condition|;
name|i
operator|++
control|)
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
literal|"/a/b"
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|dumpResult
parameter_list|(
name|Result
name|result
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|FailingBlob
extends|extends
name|ArrayBasedBlob
block|{
specifier|static
name|int
name|count
decl_stmt|;
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|corruptLength
decl_stmt|;
specifier|public
name|FailingBlob
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|this
argument_list|(
name|s
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FailingBlob
parameter_list|(
name|String
name|s
parameter_list|,
name|boolean
name|corruptLength
parameter_list|)
block|{
name|super
argument_list|(
name|s
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|String
operator|.
name|valueOf
argument_list|(
operator|++
name|count
argument_list|)
expr_stmt|;
name|this
operator|.
name|corruptLength
operator|=
name|corruptLength
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|InputStream
name|getNewStream
parameter_list|()
block|{
if|if
condition|(
name|corruptLength
condition|)
block|{
return|return
name|super
operator|.
name|getNewStream
argument_list|()
return|;
block|}
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getContentIdentity
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|corruptLength
condition|?
name|super
operator|.
name|length
argument_list|()
operator|+
literal|1
else|:
name|super
operator|.
name|length
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

