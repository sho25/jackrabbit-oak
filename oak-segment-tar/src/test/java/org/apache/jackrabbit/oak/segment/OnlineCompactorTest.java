begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
name|MultiBinaryPropertyState
operator|.
name|binaryPropertyFromBlob
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
name|base
operator|.
name|Supplier
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
name|Suppliers
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|InvalidFileStoreVersionException
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

begin_comment
comment|// FIXME OAK-6522: Implement unit tests for OnlineCompactor
end_comment

begin_class
specifier|public
class|class
name|OnlineCompactorTest
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
name|FileStore
name|fileStore
decl_stmt|;
specifier|private
name|SegmentNodeStore
name|nodeStore
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|fileStore
operator|=
name|fileStoreBuilder
argument_list|(
name|folder
operator|.
name|getRoot
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|nodeStore
operator|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fileStore
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
block|{
name|fileStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCompact
parameter_list|()
throws|throws
name|Exception
block|{
name|OnlineCompactor
name|compactor
init|=
name|createCompactor
argument_list|(
name|fileStore
argument_list|,
name|Suppliers
operator|.
name|ofInstance
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|addTestContent
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|uncompacted
init|=
operator|(
name|SegmentNodeState
operator|)
name|nodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|SegmentNodeState
name|compacted
init|=
name|compactor
operator|.
name|compact
argument_list|(
name|uncompacted
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|compacted
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|uncompacted
operator|==
name|compacted
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uncompacted
argument_list|,
name|compacted
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uncompacted
operator|.
name|getSegment
argument_list|()
operator|.
name|getGcGeneration
argument_list|()
operator|.
name|nextFull
argument_list|()
argument_list|,
name|compacted
operator|.
name|getSegment
argument_list|()
operator|.
name|getGcGeneration
argument_list|()
argument_list|)
expr_stmt|;
name|modifyTestContent
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
name|NodeState
name|modified
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|compacted
operator|=
name|compactor
operator|.
name|compact
argument_list|(
name|uncompacted
argument_list|,
name|modified
argument_list|,
name|compacted
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|compacted
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|modified
operator|==
name|compacted
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|modified
argument_list|,
name|compacted
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uncompacted
operator|.
name|getSegment
argument_list|()
operator|.
name|getGcGeneration
argument_list|()
operator|.
name|nextFull
argument_list|()
argument_list|,
name|compacted
operator|.
name|getSegment
argument_list|()
operator|.
name|getGcGeneration
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExceedUpdateLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|OnlineCompactor
name|compactor
init|=
name|createCompactor
argument_list|(
name|fileStore
argument_list|,
name|Suppliers
operator|.
name|ofInstance
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|addNodes
argument_list|(
name|nodeStore
argument_list|,
name|OnlineCompactor
operator|.
name|UPDATE_LIMIT
operator|*
literal|2
operator|+
literal|1
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|uncompacted
init|=
operator|(
name|SegmentNodeState
operator|)
name|nodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|SegmentNodeState
name|compacted
init|=
name|compactor
operator|.
name|compact
argument_list|(
name|uncompacted
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|compacted
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|uncompacted
operator|==
name|compacted
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uncompacted
argument_list|,
name|compacted
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uncompacted
operator|.
name|getSegment
argument_list|()
operator|.
name|getGcGeneration
argument_list|()
operator|.
name|nextFull
argument_list|()
argument_list|,
name|compacted
operator|.
name|getSegment
argument_list|()
operator|.
name|getGcGeneration
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCancel
parameter_list|()
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
name|OnlineCompactor
name|compactor
init|=
name|createCompactor
argument_list|(
name|fileStore
argument_list|,
name|Suppliers
operator|.
name|ofInstance
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|addTestContent
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
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
name|setChildNode
argument_list|(
literal|"cancel"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"cancel"
argument_list|,
literal|"cancel"
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
name|assertNull
argument_list|(
name|compactor
operator|.
name|compact
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|private
specifier|static
name|OnlineCompactor
name|createCompactor
parameter_list|(
name|FileStore
name|fileStore
parameter_list|,
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|cancel
parameter_list|)
block|{
name|SegmentWriter
name|writer
init|=
name|defaultSegmentWriterBuilder
argument_list|(
literal|"c"
argument_list|)
operator|.
name|withGeneration
argument_list|(
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|build
argument_list|(
name|fileStore
argument_list|)
decl_stmt|;
return|return
operator|new
name|OnlineCompactor
argument_list|(
name|fileStore
operator|.
name|getReader
argument_list|()
argument_list|,
name|writer
argument_list|,
name|fileStore
operator|.
name|getBlobStore
argument_list|()
argument_list|,
name|cancel
argument_list|,
parameter_list|()
lambda|->
block|{}
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|addNodes
parameter_list|(
name|SegmentNodeStore
name|nodeStore
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|CommitFailedException
block|{
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
name|setChildNode
argument_list|(
literal|"n-"
operator|+
name|k
argument_list|)
expr_stmt|;
block|}
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
block|}
specifier|private
specifier|static
name|void
name|addTestContent
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
throws|throws
name|CommitFailedException
throws|,
name|IOException
block|{
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
name|setChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"aa"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|42
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"bin"
argument_list|,
name|createBlob
argument_list|(
name|nodeStore
argument_list|,
literal|42
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"c"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|binaryPropertyFromBlob
argument_list|(
literal|"bins"
argument_list|,
name|createBlobs
argument_list|(
name|nodeStore
argument_list|,
literal|42
argument_list|,
literal|43
argument_list|,
literal|44
argument_list|)
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
block|}
specifier|private
specifier|static
name|void
name|modifyTestContent
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
throws|throws
name|CommitFailedException
block|{
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
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"aa"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"bin"
argument_list|,
literal|"changed"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"c"
argument_list|)
operator|.
name|removeProperty
argument_list|(
literal|"bins"
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
name|List
argument_list|<
name|Blob
argument_list|>
name|createBlobs
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|int
modifier|...
name|sizes
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Blob
argument_list|>
name|blobs
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|size
range|:
name|sizes
control|)
block|{
name|blobs
operator|.
name|add
argument_list|(
name|createBlob
argument_list|(
name|nodeStore
argument_list|,
name|size
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|blobs
return|;
block|}
block|}
end_class

end_unit

