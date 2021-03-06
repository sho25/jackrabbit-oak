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
name|CloudBlobDirectory
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
name|Buffer
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
name|segment
operator|.
name|SegmentNotFoundException
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
name|RepositoryNotReachableException
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
name|FileStoreMonitor
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
name|IOMonitor
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
name|RemoteStoreMonitor
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
name|persistence
operator|.
name|SegmentArchiveReader
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

begin_class
specifier|public
class|class
name|AzureReadSegmentTest
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
argument_list|(
name|expected
operator|=
name|SegmentNotFoundException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testReadNonExistentSegmentRepositoryReachable
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
throws|,
name|InvalidFileStoreVersionException
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
name|SegmentId
name|id
init|=
operator|new
name|SegmentId
argument_list|(
name|fs
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|readSegment
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryNotReachableException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testReadExistentSegmentRepositoryNotReachable
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
throws|,
name|InvalidFileStoreVersionException
throws|,
name|StorageException
block|{
name|AzurePersistence
name|p
init|=
operator|new
name|ReadFailingAzurePersistence
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
name|SegmentId
name|id
init|=
operator|new
name|SegmentId
argument_list|(
name|fs
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|2
index|]
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|writeSegment
argument_list|(
name|id
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|fs
operator|.
name|readSegment
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|ReadFailingAzurePersistence
extends|extends
name|AzurePersistence
block|{
specifier|public
name|ReadFailingAzurePersistence
parameter_list|(
name|CloudBlobDirectory
name|segmentStoreDirectory
parameter_list|)
block|{
name|super
argument_list|(
name|segmentStoreDirectory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SegmentArchiveManager
name|createArchiveManager
parameter_list|(
name|boolean
name|mmap
parameter_list|,
name|boolean
name|offHeapAccess
parameter_list|,
name|IOMonitor
name|ioMonitor
parameter_list|,
name|FileStoreMonitor
name|fileStoreMonitor
parameter_list|,
name|RemoteStoreMonitor
name|remoteStoreMonitor
parameter_list|)
block|{
return|return
operator|new
name|AzureArchiveManager
argument_list|(
name|segmentstoreDirectory
argument_list|,
name|ioMonitor
argument_list|,
name|fileStoreMonitor
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SegmentArchiveReader
name|open
parameter_list|(
name|String
name|archiveName
parameter_list|)
throws|throws
name|IOException
block|{
name|CloudBlobDirectory
name|archiveDirectory
init|=
name|getDirectory
argument_list|(
name|archiveName
argument_list|)
decl_stmt|;
return|return
operator|new
name|AzureSegmentArchiveReader
argument_list|(
name|archiveDirectory
argument_list|,
name|ioMonitor
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Buffer
name|readSegment
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|RepositoryNotReachableException
argument_list|(
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot access Azure storage"
argument_list|)
argument_list|)
throw|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|SegmentArchiveWriter
name|create
parameter_list|(
name|String
name|archiveName
parameter_list|)
throws|throws
name|IOException
block|{
name|CloudBlobDirectory
name|archiveDirectory
init|=
name|getDirectory
argument_list|(
name|archiveName
argument_list|)
decl_stmt|;
return|return
operator|new
name|AzureSegmentArchiveWriter
argument_list|(
name|archiveDirectory
argument_list|,
name|ioMonitor
argument_list|,
name|fileStoreMonitor
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Buffer
name|readSegment
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|RepositoryNotReachableException
argument_list|(
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot access Azure storage"
argument_list|)
argument_list|)
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

