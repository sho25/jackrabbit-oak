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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|SegmentId
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
name|SegmentNodeStore
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
name|SegmentPropertyState
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
name|EmptyHook
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
name|Ignore
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
name|CompactionAndCleanupTest
block|{
specifier|private
name|File
name|directory
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
name|directory
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"FileStoreTest"
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
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-2045"
argument_list|)
specifier|public
name|void
name|compactionAndWeakReferenceMagic
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|final
name|int
name|blobSize
init|=
literal|5
operator|*
name|MB
decl_stmt|;
name|FileStore
name|fileStore
init|=
operator|new
name|FileStore
argument_list|(
name|directory
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SegmentNodeStore
name|nodeStore
init|=
operator|new
name|SegmentNodeStore
argument_list|(
name|fileStore
argument_list|)
decl_stmt|;
comment|//1. Create a property with 5 MB blob
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"a1"
argument_list|,
name|createBlob
argument_list|(
name|nodeStore
argument_list|,
name|blobSize
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"b"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
comment|//Keep a reference to this nodeState to simulate long
comment|//running session
name|NodeState
name|ns1
init|=
name|nodeStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"File store pre removal %d%n"
argument_list|,
name|mb
argument_list|(
name|fileStore
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|mb
argument_list|(
name|fileStore
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
name|mb
argument_list|(
name|blobSize
argument_list|)
argument_list|)
expr_stmt|;
comment|//2. Now remove the property
name|builder
operator|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|removeProperty
argument_list|(
literal|"a1"
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
comment|//Size remains same
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"File store pre compaction %d%n"
argument_list|,
name|mb
argument_list|(
name|fileStore
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|mb
argument_list|(
name|fileStore
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
name|mb
argument_list|(
name|blobSize
argument_list|)
argument_list|)
expr_stmt|;
comment|//3. Compact
name|fileStore
operator|.
name|compact
argument_list|()
expr_stmt|;
comment|//Size still remains same
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"File store post compaction %d%n"
argument_list|,
name|mb
argument_list|(
name|fileStore
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|mb
argument_list|(
name|fileStore
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
name|mb
argument_list|(
name|blobSize
argument_list|)
argument_list|)
expr_stmt|;
comment|//4. Add some more property to flush the current TarWriter
name|builder
operator|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"a2"
argument_list|,
name|createBlob
argument_list|(
name|nodeStore
argument_list|,
name|blobSize
argument_list|)
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
comment|//Size is double
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"File store pre cleanup %d%n"
argument_list|,
name|mb
argument_list|(
name|fileStore
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|mb
argument_list|(
name|fileStore
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
literal|2
operator|*
name|mb
argument_list|(
name|blobSize
argument_list|)
argument_list|)
expr_stmt|;
comment|//5. Cleanup
name|cleanup
argument_list|(
name|fileStore
argument_list|)
expr_stmt|;
comment|//Size is still double. Deleted space not reclaimed
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"File store post cleanup %d%n"
argument_list|,
name|mb
argument_list|(
name|fileStore
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|mb
argument_list|(
name|fileStore
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
literal|2
operator|*
name|mb
argument_list|(
name|blobSize
argument_list|)
argument_list|)
expr_stmt|;
comment|//6. Null out any hard reference
name|ns1
operator|=
literal|null
expr_stmt|;
name|builder
operator|=
literal|null
expr_stmt|;
name|cleanup
argument_list|(
name|fileStore
argument_list|)
expr_stmt|;
comment|//Size should not come back to 5 and deleted data
comment|//space reclaimed
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"File store post cleanup and nullification %d%n"
argument_list|,
name|mb
argument_list|(
name|fileStore
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|mb
argument_list|(
name|fileStore
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
name|mb
argument_list|(
name|blobSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanDir
parameter_list|()
throws|throws
name|IOException
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|cleanup
parameter_list|(
name|FileStore
name|fileStore
parameter_list|)
throws|throws
name|IOException
block|{
name|fileStore
operator|.
name|getTracker
argument_list|()
operator|.
name|setCompactionMap
argument_list|(
operator|new
name|Compactor
argument_list|(
literal|null
argument_list|)
operator|.
name|getCompactionMap
argument_list|()
argument_list|)
expr_stmt|;
name|fileStore
operator|.
name|getTracker
argument_list|()
operator|.
name|getWriter
argument_list|()
operator|.
name|dropCache
argument_list|()
expr_stmt|;
name|fileStore
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|Blob
name|createBlob
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
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
return|return
name|nodeStore
operator|.
name|createBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|long
name|mb
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|size
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGainEstimator
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|final
name|int
name|blobSize
init|=
literal|2
operator|*
name|MB
decl_stmt|;
name|FileStore
name|fileStore
init|=
operator|new
name|FileStore
argument_list|(
name|directory
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SegmentNodeStore
name|nodeStore
init|=
operator|new
name|SegmentNodeStore
argument_list|(
name|fileStore
argument_list|)
decl_stmt|;
comment|// 1. Create some blob properties
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|c1
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"c1"
argument_list|)
decl_stmt|;
name|c1
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
name|createBlob
argument_list|(
name|nodeStore
argument_list|,
name|blobSize
argument_list|)
argument_list|)
expr_stmt|;
name|c1
operator|.
name|setProperty
argument_list|(
literal|"b"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|c2
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"c2"
argument_list|)
decl_stmt|;
name|c2
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
name|createBlob
argument_list|(
name|nodeStore
argument_list|,
name|blobSize
argument_list|)
argument_list|)
expr_stmt|;
name|c2
operator|.
name|setProperty
argument_list|(
literal|"b"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|c3
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"c3"
argument_list|)
decl_stmt|;
name|c3
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
name|createBlob
argument_list|(
name|nodeStore
argument_list|,
name|blobSize
argument_list|)
argument_list|)
expr_stmt|;
name|c3
operator|.
name|setProperty
argument_list|(
literal|"b"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
comment|// 2. Now remove the property
name|builder
operator|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"c1"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"c2"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|fileStore
operator|.
name|flush
argument_list|()
expr_stmt|;
try|try
block|{
comment|// should be at 66%
name|assertTrue
argument_list|(
name|fileStore
operator|.
name|estimateCompactionGain
argument_list|()
operator|.
name|estimateCompactionGain
argument_list|()
operator|>
literal|60
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fileStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-2192"
argument_list|)
comment|// FIXME OAK-2192
annotation|@
name|Test
specifier|public
name|void
name|testMixedSegments
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStore
name|store
init|=
operator|new
name|FileStore
argument_list|(
name|directory
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|SegmentNodeStore
name|nodeStore
init|=
operator|new
name|SegmentNodeStore
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|NodeBuilder
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createNodes
argument_list|(
name|root
operator|.
name|setChildNode
argument_list|(
literal|"test"
argument_list|)
argument_list|,
literal|10
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|root
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|UUID
argument_list|>
name|beforeSegments
init|=
operator|new
name|HashSet
argument_list|<
name|UUID
argument_list|>
argument_list|()
decl_stmt|;
name|collectSegments
argument_list|(
name|store
operator|.
name|getHead
argument_list|()
argument_list|,
name|beforeSegments
argument_list|)
expr_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Boolean
argument_list|>
name|run
init|=
operator|new
name|AtomicReference
argument_list|<
name|Boolean
argument_list|>
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|failedCommits
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|run
operator|.
name|get
argument_list|()
condition|;
name|k
operator|++
control|)
block|{
try|try
block|{
name|NodeBuilder
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|root
operator|.
name|setChildNode
argument_list|(
literal|"b"
operator|+
name|k
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|root
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|failedCommits
operator|.
name|add
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|interrupted
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|store
operator|.
name|compact
argument_list|()
expr_stmt|;
name|run
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|failedCommits
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|UUID
argument_list|>
name|afterSegments
init|=
operator|new
name|HashSet
argument_list|<
name|UUID
argument_list|>
argument_list|()
decl_stmt|;
name|collectSegments
argument_list|(
name|store
operator|.
name|getHead
argument_list|()
argument_list|,
name|afterSegments
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|UUID
name|u
range|:
name|beforeSegments
control|)
block|{
name|assertFalse
argument_list|(
literal|"Mixed segments found: "
operator|+
name|u
argument_list|,
name|afterSegments
operator|.
name|contains
argument_list|(
name|u
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|collectSegments
parameter_list|(
name|SegmentNodeState
name|s
parameter_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
name|segmentIds
parameter_list|)
block|{
name|SegmentId
name|sid
init|=
name|s
operator|.
name|getRecordId
argument_list|()
operator|.
name|getSegmentId
argument_list|()
decl_stmt|;
name|UUID
name|id
init|=
operator|new
name|UUID
argument_list|(
name|sid
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|sid
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
decl_stmt|;
name|segmentIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
for|for
control|(
name|ChildNodeEntry
name|cne
range|:
name|s
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|collectSegments
argument_list|(
operator|(
name|SegmentNodeState
operator|)
name|cne
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|segmentIds
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|PropertyState
name|propertyState
range|:
name|s
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|sid
operator|=
operator|(
operator|(
name|SegmentPropertyState
operator|)
name|propertyState
operator|)
operator|.
name|getRecordId
argument_list|()
operator|.
name|getSegmentId
argument_list|()
expr_stmt|;
name|id
operator|=
operator|new
name|UUID
argument_list|(
name|sid
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|sid
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|segmentIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|createNodes
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|int
name|count
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
if|if
condition|(
name|depth
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|count
condition|;
name|k
operator|++
control|)
block|{
name|NodeBuilder
name|child
init|=
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"node"
operator|+
name|k
argument_list|)
decl_stmt|;
name|createProperties
argument_list|(
name|child
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|createNodes
argument_list|(
name|child
argument_list|,
name|count
argument_list|,
name|depth
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|createProperties
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|int
name|count
parameter_list|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|count
condition|;
name|k
operator|++
control|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
literal|"property-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"value-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

