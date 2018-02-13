begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|segment
operator|.
name|file
operator|.
name|tar
package|;
end_package

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
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptySet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singleton
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|UUID
operator|.
name|randomUUID
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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|GCGeneration
operator|.
name|newGCGeneration
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
name|assertArrayEquals
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
name|nio
operator|.
name|ByteBuffer
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Random
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
name|UUID
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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|TarFiles
operator|.
name|CleanupResult
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

begin_class
specifier|public
class|class
name|TarFilesTest
block|{
specifier|public
specifier|static
specifier|final
name|int
name|MAX_FILE_SIZE
init|=
literal|512
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|byte
index|[]
name|randomData
parameter_list|()
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|512
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
return|return
name|buffer
return|;
block|}
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|folder
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
specifier|protected
name|TarFiles
name|tarFiles
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|tarFiles
operator|=
name|TarFiles
operator|.
name|builder
argument_list|()
operator|.
name|withDirectory
argument_list|(
name|folder
operator|.
name|getRoot
argument_list|()
argument_list|)
operator|.
name|withTarRecovery
argument_list|(
parameter_list|(
name|id
parameter_list|,
name|data
parameter_list|,
name|recovery
parameter_list|)
lambda|->
block|{
comment|// Intentionally left blank
block|}
argument_list|)
operator|.
name|withIOMonitor
argument_list|(
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
operator|.
name|withFileStoreMonitor
argument_list|(
operator|new
name|FileStoreMonitorAdapter
argument_list|()
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
name|MAX_FILE_SIZE
argument_list|)
operator|.
name|build
argument_list|()
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
if|if
condition|(
name|tarFiles
operator|!=
literal|null
condition|)
block|{
name|tarFiles
operator|.
name|close
argument_list|()
expr_stmt|;
name|tarFiles
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|writeSegment
parameter_list|(
name|UUID
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|writeSegment
argument_list|(
name|id
argument_list|,
name|randomData
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeSegmentWithReferences
parameter_list|(
name|UUID
name|id
parameter_list|,
name|UUID
modifier|...
name|references
parameter_list|)
throws|throws
name|IOException
block|{
name|writeSegmentWithReferences
argument_list|(
name|id
argument_list|,
name|randomData
argument_list|()
argument_list|,
name|references
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeSegmentWithReferences
parameter_list|(
name|UUID
name|id
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|UUID
modifier|...
name|references
parameter_list|)
throws|throws
name|IOException
block|{
name|tarFiles
operator|.
name|writeSegment
argument_list|(
name|id
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|,
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|asList
argument_list|(
name|references
argument_list|)
argument_list|)
argument_list|,
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeSegmentWithBinaryReferences
parameter_list|(
name|UUID
name|id
parameter_list|,
name|String
modifier|...
name|references
parameter_list|)
throws|throws
name|IOException
block|{
name|writeSegmentWithBinaryReferences
argument_list|(
name|id
argument_list|,
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
argument_list|,
name|references
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeSegmentWithBinaryReferences
parameter_list|(
name|UUID
name|id
parameter_list|,
name|GCGeneration
name|generation
parameter_list|,
name|String
modifier|...
name|references
parameter_list|)
throws|throws
name|IOException
block|{
name|writeSegmentWithBinaryReferences
argument_list|(
name|id
argument_list|,
name|randomData
argument_list|()
argument_list|,
name|generation
argument_list|,
name|references
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeSegmentWithBinaryReferences
parameter_list|(
name|UUID
name|id
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|GCGeneration
name|generation
parameter_list|,
name|String
modifier|...
name|binaryReferences
parameter_list|)
throws|throws
name|IOException
block|{
name|tarFiles
operator|.
name|writeSegment
argument_list|(
name|id
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|,
name|generation
argument_list|,
name|emptySet
argument_list|()
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|asList
argument_list|(
name|binaryReferences
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeSegment
parameter_list|(
name|UUID
name|id
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
name|tarFiles
operator|.
name|writeSegment
argument_list|(
name|id
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|,
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
argument_list|,
name|emptySet
argument_list|()
argument_list|,
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|containsSegment
parameter_list|(
name|UUID
name|id
parameter_list|)
block|{
return|return
name|tarFiles
operator|.
name|containsSegment
argument_list|(
name|id
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|byte
index|[]
name|readSegment
parameter_list|(
name|UUID
name|id
parameter_list|)
block|{
name|ByteBuffer
name|buffer
init|=
name|tarFiles
operator|.
name|readSegment
argument_list|(
name|id
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|buffer
operator|.
name|remaining
argument_list|()
index|]
decl_stmt|;
name|buffer
operator|.
name|get
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitialSize
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tarFiles
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWriterSize
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|id
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|writeSegment
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tarFiles
operator|.
name|size
argument_list|()
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReaderWriterSize
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|id
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|writeSegment
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|tarFiles
operator|.
name|newWriter
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|tarFiles
operator|.
name|size
argument_list|()
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitialReaderCount
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tarFiles
operator|.
name|readerCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReaderCount
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|id
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|writeSegment
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|tarFiles
operator|.
name|newWriter
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tarFiles
operator|.
name|readerCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitialContainsSegment
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|id
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|containsSegment
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testContainsSegmentInWriter
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|id
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|writeSegment
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|containsSegment
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testContainsSegmentInReader
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|id
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|writeSegment
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|tarFiles
operator|.
name|newWriter
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|containsSegment
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitialReadSegment
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|readSegment
argument_list|(
name|randomUUID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadSegmentFromWriter
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|id
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|randomData
argument_list|()
decl_stmt|;
name|writeSegment
argument_list|(
name|id
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|data
argument_list|,
name|readSegment
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadSegmentFromReader
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|id
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|randomData
argument_list|()
decl_stmt|;
name|writeSegment
argument_list|(
name|id
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|tarFiles
operator|.
name|newWriter
argument_list|()
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|data
argument_list|,
name|readSegment
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetIndices
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|a
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|UUID
name|b
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|UUID
name|c
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|UUID
name|d
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|writeSegment
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|writeSegment
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|tarFiles
operator|.
name|newWriter
argument_list|()
expr_stmt|;
name|writeSegment
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|writeSegment
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|tarFiles
operator|.
name|newWriter
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|expected
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|expected
operator|.
name|add
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|asList
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|asList
argument_list|(
name|c
argument_list|,
name|d
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|tarFiles
operator|.
name|getIndices
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetGraph
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|a
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|UUID
name|b
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|UUID
name|c
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|UUID
name|d
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|writeSegment
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|writeSegmentWithReferences
argument_list|(
name|b
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|writeSegmentWithReferences
argument_list|(
name|c
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|writeSegmentWithReferences
argument_list|(
name|d
argument_list|,
name|b
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|tarFiles
operator|.
name|newWriter
argument_list|()
expr_stmt|;
name|String
name|file
init|=
name|tarFiles
operator|.
name|getIndices
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|graph
init|=
name|tarFiles
operator|.
name|getGraph
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|expected
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|expected
operator|.
name|put
argument_list|(
name|a
argument_list|,
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
name|b
argument_list|,
name|singleton
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
name|c
argument_list|,
name|singleton
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
name|d
argument_list|,
name|newHashSet
argument_list|(
name|b
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|graph
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCollectBlobReferences
parameter_list|()
throws|throws
name|Exception
block|{
name|writeSegmentWithBinaryReferences
argument_list|(
name|randomUUID
argument_list|()
argument_list|)
expr_stmt|;
name|writeSegmentWithBinaryReferences
argument_list|(
name|randomUUID
argument_list|()
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|writeSegmentWithBinaryReferences
argument_list|(
name|randomUUID
argument_list|()
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|references
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|tarFiles
operator|.
name|collectBlobReferences
argument_list|(
name|references
operator|::
name|add
argument_list|,
name|gen
lambda|->
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|references
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCollectBlobReferencesWithGenerationFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|GCGeneration
name|ok
init|=
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|GCGeneration
name|ko
init|=
name|newGCGeneration
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|writeSegmentWithBinaryReferences
argument_list|(
name|randomUUID
argument_list|()
argument_list|,
name|ok
argument_list|,
literal|"ok"
argument_list|)
expr_stmt|;
name|writeSegmentWithBinaryReferences
argument_list|(
name|randomUUID
argument_list|()
argument_list|,
name|ko
argument_list|,
literal|"ko"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|references
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|tarFiles
operator|.
name|collectBlobReferences
argument_list|(
name|references
operator|::
name|add
argument_list|,
name|ko
operator|::
name|equals
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|references
argument_list|,
name|singleton
argument_list|(
literal|"ok"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetSegmentId
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|a
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|UUID
name|b
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|UUID
name|c
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|writeSegment
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|writeSegment
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|writeSegment
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|tarFiles
operator|.
name|newWriter
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|UUID
argument_list|>
name|segmentIds
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|tarFiles
operator|.
name|getSegmentIds
argument_list|()
operator|.
name|forEach
argument_list|(
name|segmentIds
operator|::
name|add
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|asList
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
name|c
argument_list|)
argument_list|)
argument_list|,
name|segmentIds
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|a
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|UUID
name|b
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|UUID
name|c
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|UUID
name|d
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|UUID
name|e
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|writeSegment
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|writeSegment
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|writeSegmentWithReferences
argument_list|(
name|c
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|writeSegment
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|writeSegmentWithReferences
argument_list|(
name|e
argument_list|,
name|a
argument_list|,
name|d
argument_list|)
expr_stmt|;
comment|// Traverse graph of segments starting with `e`. Mark as reclaimable
comment|// every segment that are not traversed. The two segments `b` and `c`
comment|// will be reclaimed.
name|CleanupResult
name|result
init|=
name|tarFiles
operator|.
name|cleanup
argument_list|(
operator|new
name|CleanupContext
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|UUID
argument_list|>
name|initialReferences
parameter_list|()
block|{
return|return
name|singletonList
argument_list|(
name|e
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|shouldReclaim
parameter_list|(
name|UUID
name|id
parameter_list|,
name|GCGeneration
name|generation
parameter_list|,
name|boolean
name|referenced
parameter_list|)
block|{
return|return
operator|!
name|referenced
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|shouldFollow
parameter_list|(
name|UUID
name|from
parameter_list|,
name|UUID
name|to
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isInterrupted
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|getRemovableFiles
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|asList
argument_list|(
name|c
argument_list|,
name|b
argument_list|)
argument_list|)
argument_list|,
name|result
operator|.
name|getReclaimedSegmentIds
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|getReclaimedSize
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCleanupConnectedSegments
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|a
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|UUID
name|b
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|UUID
name|c
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|UUID
name|d
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|UUID
name|e
init|=
name|randomUUID
argument_list|()
decl_stmt|;
name|writeSegment
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|writeSegment
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|writeSegmentWithReferences
argument_list|(
name|c
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|writeSegment
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|writeSegmentWithReferences
argument_list|(
name|e
argument_list|,
name|c
argument_list|,
name|d
argument_list|)
expr_stmt|;
comment|// Traverse graph of segments starting with `e`. Mark as reclaimable
comment|// every segment that are not traversed. The segments are all connected,
comment|// though. No segments will be removed.
name|CleanupResult
name|result
init|=
name|tarFiles
operator|.
name|cleanup
argument_list|(
operator|new
name|CleanupContext
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|UUID
argument_list|>
name|initialReferences
parameter_list|()
block|{
return|return
name|singletonList
argument_list|(
name|e
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|shouldReclaim
parameter_list|(
name|UUID
name|id
parameter_list|,
name|GCGeneration
name|generation
parameter_list|,
name|boolean
name|referenced
parameter_list|)
block|{
return|return
operator|!
name|referenced
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|shouldFollow
parameter_list|(
name|UUID
name|from
parameter_list|,
name|UUID
name|to
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isInterrupted
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|getRemovableFiles
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|getReclaimedSegmentIds
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|getReclaimedSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

