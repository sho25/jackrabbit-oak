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
name|azure
package|;
end_package

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|StorageException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|blob
operator|.
name|CloudBlob
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|blob
operator|.
name|CloudBlobContainer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|blob
operator|.
name|ListBlobItem
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
name|segment
operator|.
name|SegmentNodeStoreBuilders
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
name|FileStoreBuilder
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
name|segment
operator|.
name|spi
operator|.
name|monitor
operator|.
name|RemoteStoreMonitorAdapter
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
name|spi
operator|.
name|persistence
operator|.
name|SegmentArchiveManager
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
name|spi
operator|.
name|monitor
operator|.
name|FileStoreMonitorAdapter
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
name|spi
operator|.
name|monitor
operator|.
name|IOMonitorAdapter
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
name|spi
operator|.
name|persistence
operator|.
name|SegmentArchiveWriter
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
name|ClassRule
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|InvalidKeyException
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
name|LinkedHashMap
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
name|UUID
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

begin_class
specifier|public
class|class
name|AzureArchiveManagerTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
name|AzuriteDockerRule
name|azurite
init|=
operator|new
name|AzuriteDockerRule
argument_list|()
decl_stmt|;
specifier|private
name|CloudBlobContainer
name|container
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|StorageException
throws|,
name|InvalidKeyException
throws|,
name|URISyntaxException
block|{
name|container
operator|=
name|azurite
operator|.
name|getContainer
argument_list|(
literal|"oak-test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRecovery
parameter_list|()
throws|throws
name|StorageException
throws|,
name|URISyntaxException
throws|,
name|IOException
block|{
name|SegmentArchiveManager
name|manager
init|=
operator|new
name|AzurePersistence
argument_list|(
name|container
operator|.
name|getDirectoryReference
argument_list|(
literal|"oak"
argument_list|)
argument_list|)
operator|.
name|createArchiveManager
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|,
operator|new
name|FileStoreMonitorAdapter
argument_list|()
argument_list|,
operator|new
name|RemoteStoreMonitorAdapter
argument_list|()
argument_list|)
decl_stmt|;
name|SegmentArchiveWriter
name|writer
init|=
name|manager
operator|.
name|create
argument_list|(
literal|"data00000a.tar"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|UUID
argument_list|>
name|uuids
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|UUID
name|u
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|writer
operator|.
name|writeSegment
argument_list|(
name|u
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|u
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|,
operator|new
name|byte
index|[
literal|10
index|]
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|uuids
operator|.
name|add
argument_list|(
name|u
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|container
operator|.
name|getBlockBlobReference
argument_list|(
literal|"oak/data00000a.tar/0005."
operator|+
name|uuids
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
name|LinkedHashMap
argument_list|<
name|UUID
argument_list|,
name|byte
index|[]
argument_list|>
name|recovered
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|manager
operator|.
name|recoverEntries
argument_list|(
literal|"data00000a.tar"
argument_list|,
name|recovered
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uuids
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|,
name|newArrayList
argument_list|(
name|recovered
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUncleanStop
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
throws|,
name|InvalidFileStoreVersionException
throws|,
name|CommitFailedException
throws|,
name|StorageException
block|{
name|AzurePersistence
name|p
init|=
operator|new
name|AzurePersistence
argument_list|(
name|container
operator|.
name|getDirectoryReference
argument_list|(
literal|"oak"
argument_list|)
argument_list|)
decl_stmt|;
name|FileStore
name|fs
init|=
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
operator|.
name|withCustomPersistence
argument_list|(
name|p
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SegmentNodeStore
name|segmentNodeStore
init|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fs
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|segmentNodeStore
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
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|segmentNodeStore
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
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|container
operator|.
name|getBlockBlobReference
argument_list|(
literal|"oak/data00000a.tar/closed"
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
name|container
operator|.
name|getBlockBlobReference
argument_list|(
literal|"oak/data00000a.tar/data00000a.tar.brf"
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
name|container
operator|.
name|getBlockBlobReference
argument_list|(
literal|"oak/data00000a.tar/data00000a.tar.gph"
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
name|fs
operator|=
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
operator|.
name|withCustomPersistence
argument_list|(
name|p
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|segmentNodeStore
operator|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fs
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|segmentNodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|getString
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
comment|// see OAK-8566
specifier|public
name|void
name|testUncleanStopWithEmptyArchive
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
throws|,
name|InvalidFileStoreVersionException
throws|,
name|CommitFailedException
throws|,
name|StorageException
block|{
name|AzurePersistence
name|p
init|=
operator|new
name|AzurePersistence
argument_list|(
name|container
operator|.
name|getDirectoryReference
argument_list|(
literal|"oak"
argument_list|)
argument_list|)
decl_stmt|;
name|FileStore
name|fs
init|=
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
operator|.
name|withCustomPersistence
argument_list|(
name|p
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SegmentNodeStore
name|segmentNodeStore
init|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fs
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|segmentNodeStore
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
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|segmentNodeStore
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
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// make sure there are 2 archives
name|fs
operator|=
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
operator|.
name|withCustomPersistence
argument_list|(
name|p
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|segmentNodeStore
operator|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fs
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|builder
operator|=
name|segmentNodeStore
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
literal|"foo2"
argument_list|,
literal|"bar2"
argument_list|)
expr_stmt|;
name|segmentNodeStore
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
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// remove the segment 0000 from the second archive
name|ListBlobItem
name|segment0000
init|=
name|container
operator|.
name|listBlobs
argument_list|(
literal|"oak/data00001a.tar/0000."
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
operator|(
operator|(
name|CloudBlob
operator|)
name|segment0000
operator|)
operator|.
name|delete
argument_list|()
expr_stmt|;
name|container
operator|.
name|getBlockBlobReference
argument_list|(
literal|"oak/data00001a.tar/closed"
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
name|fs
operator|=
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
operator|.
name|withCustomPersistence
argument_list|(
name|p
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|segmentNodeStore
operator|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fs
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|segmentNodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|getString
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

