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
name|plugins
operator|.
name|segment
operator|.
name|file
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
name|Lists
operator|.
name|newArrayList
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
name|Sets
operator|.
name|newTreeSet
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
operator|.
name|deleteDirectory
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
name|SEGMENT_MK
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
name|segment
operator|.
name|SegmentVersion
operator|.
name|V_11
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
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|FileOutputStream
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
name|RandomAccessFile
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|plugins
operator|.
name|segment
operator|.
name|Compactor
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
name|segment
operator|.
name|RecordId
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
name|segment
operator|.
name|Segment
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
name|segment
operator|.
name|SegmentBlob
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
name|segment
operator|.
name|SegmentNodeBuilder
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
name|segment
operator|.
name|SegmentNodeState
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
name|segment
operator|.
name|SegmentWriter
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
name|segment
operator|.
name|file
operator|.
name|FileStore
operator|.
name|ReadOnlyStore
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
name|BeforeClass
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

begin_class
specifier|public
class|class
name|FileStoreIT
block|{
specifier|private
name|File
name|directory
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|assumptions
parameter_list|()
block|{
name|assumeTrue
argument_list|(
name|getFixtures
argument_list|()
operator|.
name|contains
argument_list|(
name|SEGMENT_MK
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|directory
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"FileStoreIT"
argument_list|,
literal|"dir"
argument_list|,
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
expr_stmt|;
name|directory
operator|.
name|delete
argument_list|()
expr_stmt|;
name|directory
operator|.
name|mkdir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
try|try
block|{
name|deleteDirectory
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRestartAndGCWithoutMM
parameter_list|()
throws|throws
name|IOException
block|{
name|testRestartAndGC
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRestartAndGCWithMM
parameter_list|()
throws|throws
name|IOException
block|{
name|testRestartAndGC
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRestartAndGC
parameter_list|(
name|boolean
name|memoryMapping
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStore
name|store
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
name|memoryMapping
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
name|memoryMapping
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|SegmentNodeState
name|base
init|=
name|store
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|SegmentNodeBuilder
name|builder
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|10
operator|*
literal|1024
operator|*
literal|1024
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|Blob
name|blob
init|=
name|builder
operator|.
name|createBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
name|blob
argument_list|)
expr_stmt|;
name|store
operator|.
name|setHead
argument_list|(
name|base
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|flush
argument_list|()
expr_stmt|;
name|store
operator|.
name|setHead
argument_list|(
name|store
operator|.
name|getHead
argument_list|()
argument_list|,
name|base
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
name|memoryMapping
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|store
operator|.
name|gc
argument_list|()
expr_stmt|;
name|store
operator|.
name|flush
argument_list|()
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
name|memoryMapping
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCompaction
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|largeBinarySize
init|=
literal|10
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
name|FileStore
name|store
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SegmentWriter
name|writer
init|=
name|store
operator|.
name|getTracker
argument_list|()
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|SegmentNodeState
name|base
init|=
name|store
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|SegmentNodeBuilder
name|builder
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|largeBinarySize
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|SegmentBlob
name|blob
init|=
name|writer
operator|.
name|writeStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
name|blob
argument_list|)
expr_stmt|;
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
comment|// write the blob reference to the segment
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|head
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|setHead
argument_list|(
name|base
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|store
operator|.
name|getHead
argument_list|()
operator|.
name|getString
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|Compactor
name|compactor
init|=
operator|new
name|Compactor
argument_list|(
name|store
operator|.
name|getTracker
argument_list|()
argument_list|)
decl_stmt|;
name|SegmentNodeState
name|compacted
init|=
name|compactor
operator|.
name|compact
argument_list|(
name|EMPTY_NODE
argument_list|,
name|head
argument_list|,
name|EMPTY_NODE
argument_list|)
decl_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// First simulate the case where during compaction a reference to the
comment|// older segments is added to a segment that the compactor is writing
name|store
operator|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|head
operator|=
name|store
operator|.
name|getHead
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|size
argument_list|()
operator|>
name|largeBinarySize
argument_list|)
expr_stmt|;
name|builder
operator|=
name|head
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"old"
argument_list|,
name|head
argument_list|)
expr_stmt|;
comment|// reference to pre-compacted state
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|setHead
argument_list|(
name|head
argument_list|,
name|compacted
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// In this case the revision cleanup is unable to reclaim the old data
name|store
operator|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|size
argument_list|()
operator|>
name|largeBinarySize
argument_list|)
expr_stmt|;
name|store
operator|.
name|cleanup
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|size
argument_list|()
operator|>
name|largeBinarySize
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now we do the same thing, but let the compactor use a different
comment|// SegmentWriter
name|store
operator|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|head
operator|=
name|store
operator|.
name|getHead
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|size
argument_list|()
operator|>
name|largeBinarySize
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|SegmentWriter
argument_list|(
name|store
argument_list|,
name|V_11
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|compactor
operator|=
operator|new
name|Compactor
argument_list|(
name|store
operator|.
name|getTracker
argument_list|()
argument_list|)
expr_stmt|;
name|compacted
operator|=
name|compactor
operator|.
name|compact
argument_list|(
name|EMPTY_NODE
argument_list|,
name|head
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
name|builder
operator|=
name|head
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"old"
argument_list|,
name|head
argument_list|)
expr_stmt|;
comment|// reference to pre-compacted state
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|setHead
argument_list|(
name|head
argument_list|,
name|compacted
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Revision cleanup is now able to reclaim the extra space (OAK-1932)
name|store
operator|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|size
argument_list|()
operator|>
name|largeBinarySize
argument_list|)
expr_stmt|;
name|store
operator|.
name|cleanup
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|size
argument_list|()
operator|<
name|largeBinarySize
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRecovery
parameter_list|()
throws|throws
name|IOException
block|{
name|FileStore
name|store
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|store
operator|.
name|flush
argument_list|()
expr_stmt|;
name|RandomAccessFile
name|data0
init|=
operator|new
name|RandomAccessFile
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00000a.tar"
argument_list|)
argument_list|,
literal|"r"
argument_list|)
decl_stmt|;
name|long
name|pos0
init|=
name|data0
operator|.
name|length
argument_list|()
decl_stmt|;
name|SegmentNodeState
name|base
init|=
name|store
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|SegmentNodeBuilder
name|builder
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"step"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|store
operator|.
name|setHead
argument_list|(
name|base
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|flush
argument_list|()
expr_stmt|;
name|long
name|pos1
init|=
name|data0
operator|.
name|length
argument_list|()
decl_stmt|;
name|data0
operator|.
name|close
argument_list|()
expr_stmt|;
name|base
operator|=
name|store
operator|.
name|getHead
argument_list|()
expr_stmt|;
name|builder
operator|=
name|base
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"step"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|store
operator|.
name|setHead
argument_list|(
name|base
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|store
operator|.
name|getHead
argument_list|()
operator|.
name|getString
argument_list|(
literal|"step"
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|RandomAccessFile
name|file
init|=
operator|new
name|RandomAccessFile
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00000a.tar"
argument_list|)
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
name|file
operator|.
name|setLength
argument_list|(
name|pos1
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|store
operator|.
name|getHead
argument_list|()
operator|.
name|getString
argument_list|(
literal|"step"
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|file
operator|=
operator|new
name|RandomAccessFile
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00000a.tar"
argument_list|)
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|file
operator|.
name|setLength
argument_list|(
name|pos0
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|getHead
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"step"
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRearrangeOldData
parameter_list|()
throws|throws
name|IOException
block|{
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00000.tar"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00010a.tar"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00030.tar"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"bulk00002.tar"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"bulk00005a.tar"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|?
argument_list|>
name|files
init|=
name|FileStore
operator|.
name|collectFiles
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|newArrayList
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|31
argument_list|,
literal|32
argument_list|,
literal|33
argument_list|)
argument_list|,
name|newArrayList
argument_list|(
name|newTreeSet
argument_list|(
name|files
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00000a.tar"
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00001a.tar"
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00031a.tar"
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00032a.tar"
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00033a.tar"
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|files
operator|=
name|FileStore
operator|.
name|collectFiles
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newArrayList
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|31
argument_list|,
literal|32
argument_list|,
literal|33
argument_list|)
argument_list|,
name|newArrayList
argument_list|(
name|newTreeSet
argument_list|(
name|files
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|// See OAK-2049
specifier|public
name|void
name|segmentOverflow
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|n
init|=
literal|1
init|;
name|n
operator|<
literal|255
condition|;
name|n
operator|++
control|)
block|{
comment|// 255 = ListRecord.LEVEL_SIZE
name|FileStore
name|store
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SegmentWriter
name|writer
init|=
name|store
operator|.
name|getTracker
argument_list|()
operator|.
name|getWriter
argument_list|()
decl_stmt|;
comment|// writer.length == 32  (from the root node)
comment|// adding 15 strings with 16516 bytes each
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|15
condition|;
name|k
operator|++
control|)
block|{
comment|// 16516 = (Segment.MEDIUM_LIMIT - 1 + 2 + 3)
comment|// 1 byte per char, 2 byte to store the length and 3 bytes for the
comment|// alignment to the integer boundary
name|writer
operator|.
name|writeString
argument_list|(
name|Strings
operator|.
name|repeat
argument_list|(
literal|"abcdefghijklmno"
operator|.
name|substring
argument_list|(
name|k
argument_list|,
name|k
operator|+
literal|1
argument_list|)
argument_list|,
name|Segment
operator|.
name|MEDIUM_LIMIT
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// adding 14280 bytes. 1 byte per char, and 2 bytes to store the length
name|RecordId
name|x
init|=
name|writer
operator|.
name|writeString
argument_list|(
name|Strings
operator|.
name|repeat
argument_list|(
literal|"x"
argument_list|,
literal|14278
argument_list|)
argument_list|)
decl_stmt|;
comment|// writer.length == 262052
comment|// Adding 765 bytes (255 recordIds)
comment|// This should cause the current segment to flush
name|List
argument_list|<
name|RecordId
argument_list|>
name|list
init|=
name|Collections
operator|.
name|nCopies
argument_list|(
name|n
argument_list|,
name|x
argument_list|)
decl_stmt|;
name|writer
operator|.
name|writeList
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// Don't close the store in a finally clause as if a failure happens
comment|// this will also fail an cover up the earlier exception
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|nonBlockingROStore
parameter_list|()
throws|throws
name|IOException
block|{
name|FileStore
name|store
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|store
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// first 1kB
name|SegmentNodeState
name|base
init|=
name|store
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|SegmentNodeBuilder
name|builder
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"step"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|store
operator|.
name|setHead
argument_list|(
name|base
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// second 1kB
name|ReadOnlyStore
name|ro
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ro
operator|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|store
operator|.
name|getHead
argument_list|()
argument_list|,
name|ro
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|ro
operator|!=
literal|null
condition|)
block|{
name|ro
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

