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
name|Maps
operator|.
name|newLinkedHashMap
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
name|Executors
operator|.
name|newSingleThreadExecutor
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
name|SECONDS
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
name|DefaultSegmentWriterBuilder
operator|.
name|defaultSegmentWriterBuilder
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
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|CountDownLatch
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
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
name|atomic
operator|.
name|AtomicBoolean
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
name|util
operator|.
name|concurrent
operator|.
name|Monitor
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
name|util
operator|.
name|concurrent
operator|.
name|Monitor
operator|.
name|Guard
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
name|memory
operator|.
name|AbstractBlob
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
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
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
name|DefaultSegmentWriter
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
name|segment
operator|.
name|SegmentTestConstants
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
name|GCGeneration
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
name|ChildNodeEntry
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
name|junit
operator|.
name|Ignore
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
name|FileStoreIT
block|{
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
specifier|private
name|File
name|getFileStoreFolder
parameter_list|()
block|{
return|return
name|folder
operator|.
name|getRoot
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRestartAndGCWithoutMM
parameter_list|()
throws|throws
name|Exception
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
name|Exception
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
name|Exception
block|{
name|FileStore
name|store
init|=
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
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
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
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
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|base
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
operator|.
name|getRecordId
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
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|store
operator|.
name|getRevisions
argument_list|()
operator|.
name|getHead
argument_list|()
argument_list|,
name|base
operator|.
name|getRecordId
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
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
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
name|fullGC
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
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
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
name|testRecovery
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStore
name|store
init|=
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
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
name|getFileStoreFolder
argument_list|()
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
name|ArrayBasedBlob
name|blob
init|=
operator|new
name|ArrayBasedBlob
argument_list|(
operator|new
name|byte
index|[
name|SegmentTestConstants
operator|.
name|MEDIUM_LIMIT
index|]
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"blob"
argument_list|,
name|blob
argument_list|)
expr_stmt|;
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
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|base
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
operator|.
name|getRecordId
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
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|base
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
operator|.
name|getRecordId
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
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
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
name|getFileStoreFolder
argument_list|()
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
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
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
name|getFileStoreFolder
argument_list|()
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
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
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
name|nonBlockingROStore
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStore
name|store
init|=
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
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
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|base
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// second 1kB
name|ReadOnlyFileStore
name|ro
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ro
operator|=
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|store
operator|.
name|getRevisions
argument_list|()
operator|.
name|getHead
argument_list|()
argument_list|,
name|ro
operator|.
name|getRevisions
argument_list|()
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
annotation|@
name|Test
specifier|public
name|void
name|setRevisionTest
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|FileStore
name|store
init|=
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
name|RecordId
name|id1
init|=
name|store
operator|.
name|getRevisions
argument_list|()
operator|.
name|getHead
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
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|base
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|RecordId
name|id2
init|=
name|store
operator|.
name|getRevisions
argument_list|()
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|store
operator|.
name|flush
argument_list|()
expr_stmt|;
try|try
init|(
name|ReadOnlyFileStore
name|roStore
init|=
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
init|)
block|{
name|assertEquals
argument_list|(
name|id2
argument_list|,
name|roStore
operator|.
name|getRevisions
argument_list|()
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
name|roStore
operator|.
name|setRevision
argument_list|(
name|id1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|id1
argument_list|,
name|roStore
operator|.
name|getRevisions
argument_list|()
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
name|roStore
operator|.
name|setRevision
argument_list|(
name|id2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|id2
argument_list|,
name|roStore
operator|.
name|getRevisions
argument_list|()
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|snfeAfterOnRC
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
throws|,
name|InterruptedException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|roots
init|=
name|newLinkedHashMap
argument_list|()
decl_stmt|;
try|try
init|(
name|FileStore
name|rwStore
init|=
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
comment|// Block scheduled journal updates
name|CountDownLatch
name|blockJournalUpdates
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Ensure we have a non empty journal
name|rwStore
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// Add a revisions
name|roots
operator|.
name|putIfAbsent
argument_list|(
name|addNode
argument_list|(
name|rwStore
argument_list|,
literal|"g"
argument_list|)
argument_list|,
literal|"g"
argument_list|)
expr_stmt|;
comment|// Simulate compaction by writing a new head state of the next generation
name|SegmentNodeState
name|base
init|=
name|rwStore
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|GCGeneration
name|gcGeneration
init|=
name|base
operator|.
name|getRecordId
argument_list|()
operator|.
name|getSegmentId
argument_list|()
operator|.
name|getGcGeneration
argument_list|()
decl_stmt|;
name|DefaultSegmentWriter
name|nextGenerationWriter
init|=
name|defaultSegmentWriterBuilder
argument_list|(
literal|"c"
argument_list|)
operator|.
name|withGeneration
argument_list|(
name|gcGeneration
operator|.
name|nextFull
argument_list|()
argument_list|)
operator|.
name|build
argument_list|(
name|rwStore
argument_list|)
decl_stmt|;
name|RecordId
name|headId
init|=
name|nextGenerationWriter
operator|.
name|writeNode
argument_list|(
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|)
decl_stmt|;
name|rwStore
operator|.
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|base
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|headId
argument_list|)
expr_stmt|;
comment|// Add another revisions
name|roots
operator|.
name|putIfAbsent
argument_list|(
name|addNode
argument_list|(
name|rwStore
argument_list|,
literal|"g"
argument_list|)
argument_list|,
literal|"g"
argument_list|)
expr_stmt|;
name|blockJournalUpdates
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
comment|// Open the store again in read only mode and check all revisions.
comment|// This simulates accessing the store after an unclean shutdown.
try|try
init|(
name|ReadOnlyFileStore
name|roStore
init|=
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
init|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|revision
range|:
name|roots
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|roStore
operator|.
name|setRevision
argument_list|(
name|revision
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|checkNode
argument_list|(
name|roStore
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|String
name|addNode
parameter_list|(
name|FileStore
name|store
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|InterruptedException
block|{
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
name|setChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|store
operator|.
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|base
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|store
operator|.
name|getRevisions
argument_list|()
operator|.
name|getHead
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|void
name|checkNode
parameter_list|(
name|NodeState
name|node
parameter_list|)
block|{
for|for
control|(
name|ChildNodeEntry
name|cne
range|:
name|node
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|checkNode
argument_list|(
name|cne
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-7867"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|blockingBlob
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* A blob that blocks on read until unblocked */
class|class
name|BlockingBlob
extends|extends
name|AbstractBlob
block|{
specifier|private
specifier|final
name|AtomicBoolean
name|blocking
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Monitor
name|readMonitor
init|=
operator|new
name|Monitor
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|reading
init|=
literal|false
decl_stmt|;
specifier|public
name|boolean
name|waitForRead
parameter_list|(
name|int
name|time
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|readMonitor
operator|.
name|enter
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|readMonitor
operator|.
name|waitFor
argument_list|(
operator|new
name|Guard
argument_list|(
name|readMonitor
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisfied
parameter_list|()
block|{
return|return
name|reading
return|;
block|}
block|}
argument_list|,
name|time
argument_list|,
name|unit
argument_list|)
return|;
block|}
finally|finally
block|{
name|readMonitor
operator|.
name|leave
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|unblock
parameter_list|()
block|{
name|blocking
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|InputStream
name|getNewStream
parameter_list|()
block|{
return|return
operator|new
name|InputStream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|readOrEnd
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
annotation|@
name|NotNull
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readOrEnd
argument_list|()
return|;
block|}
specifier|private
name|int
name|readOrEnd
parameter_list|()
block|{
if|if
condition|(
name|blocking
operator|.
name|get
argument_list|()
condition|)
block|{
name|reading
operator|=
literal|true
expr_stmt|;
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
block|}
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
literal|1
return|;
block|}
block|}
name|ExecutorService
name|updateExecutor
init|=
name|newSingleThreadExecutor
argument_list|()
decl_stmt|;
name|ExecutorService
name|flushExecutor
init|=
name|newSingleThreadExecutor
argument_list|()
decl_stmt|;
try|try
init|(
name|FileStore
name|store
init|=
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
comment|// A blob whose stream blocks on read
name|BlockingBlob
name|blockingBlob
init|=
operator|new
name|BlockingBlob
argument_list|()
decl_stmt|;
comment|// Use a background thread to add the blocking blob to a property
name|updateExecutor
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
block|{
name|SegmentNodeState
name|root
init|=
name|store
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|SegmentNodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"blockingBlob"
argument_list|,
name|blockingBlob
argument_list|)
expr_stmt|;
name|store
operator|.
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|root
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
comment|// Wait for reading on the blob to block
name|assertTrue
argument_list|(
name|blockingBlob
operator|.
name|waitForRead
argument_list|(
literal|1
argument_list|,
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// In another background thread flush the file store
name|Future
argument_list|<
name|Void
argument_list|>
name|flushed
init|=
name|flushExecutor
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
block|{
name|store
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
decl_stmt|;
comment|// Flush should not get blocked by the blob blocked on reading
try|try
block|{
name|flushed
operator|.
name|get
argument_list|(
literal|10
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Flush must not block"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|blockingBlob
operator|.
name|unblock
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|flushExecutor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|updateExecutor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

