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
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|OperationContext
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
name|RequestCompletedEvent
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
name|StorageEvent
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
name|BlobListingDetails
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
name|BlobRequestOptions
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
name|CloudAppendBlob
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
name|CloudBlockBlob
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
name|GCJournalFile
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
name|JournalFile
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
name|ManifestFile
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
name|RepositoryLock
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
name|SegmentNodeStorePersistence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|AzurePersistence
implements|implements
name|SegmentNodeStorePersistence
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AzurePersistence
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|CloudBlobDirectory
name|segmentstoreDirectory
decl_stmt|;
specifier|public
name|AzurePersistence
parameter_list|(
name|CloudBlobDirectory
name|segmentStoreDirectory
parameter_list|)
block|{
name|this
operator|.
name|segmentstoreDirectory
operator|=
name|segmentStoreDirectory
expr_stmt|;
name|BlobRequestOptions
name|defaultRequestOptions
init|=
name|segmentStoreDirectory
operator|.
name|getServiceClient
argument_list|()
operator|.
name|getDefaultRequestOptions
argument_list|()
decl_stmt|;
if|if
condition|(
name|defaultRequestOptions
operator|.
name|getMaximumExecutionTimeInMs
argument_list|()
operator|==
literal|null
condition|)
block|{
name|defaultRequestOptions
operator|.
name|setMaximumExecutionTimeInMs
argument_list|(
operator|(
name|int
operator|)
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|30
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|defaultRequestOptions
operator|.
name|getTimeoutIntervalInMs
argument_list|()
operator|==
literal|null
condition|)
block|{
name|defaultRequestOptions
operator|.
name|setTimeoutIntervalInMs
argument_list|(
operator|(
name|int
operator|)
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|attachRemoteStoreMonitor
argument_list|(
name|remoteStoreMonitor
argument_list|)
expr_stmt|;
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
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|segmentFilesExist
parameter_list|()
block|{
try|try
block|{
for|for
control|(
name|ListBlobItem
name|i
range|:
name|segmentstoreDirectory
operator|.
name|listBlobs
argument_list|(
literal|null
argument_list|,
literal|false
argument_list|,
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|BlobListingDetails
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
control|)
block|{
if|if
condition|(
name|i
operator|instanceof
name|CloudBlobDirectory
condition|)
block|{
name|CloudBlobDirectory
name|dir
init|=
operator|(
name|CloudBlobDirectory
operator|)
name|i
decl_stmt|;
name|String
name|name
init|=
name|Paths
operator|.
name|get
argument_list|(
name|dir
operator|.
name|getPrefix
argument_list|()
argument_list|)
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|".tar"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|StorageException
decl||
name|URISyntaxException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Can't check if the segment archives exists"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|JournalFile
name|getJournalFile
parameter_list|()
block|{
return|return
operator|new
name|AzureJournalFile
argument_list|(
name|segmentstoreDirectory
argument_list|,
literal|"journal.log"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|GCJournalFile
name|getGCJournalFile
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|AzureGCJournalFile
argument_list|(
name|getAppendBlob
argument_list|(
literal|"gc.log"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ManifestFile
name|getManifestFile
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|AzureManifestFile
argument_list|(
name|getBlockBlob
argument_list|(
literal|"manifest"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RepositoryLock
name|lockRepository
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|AzureRepositoryLock
argument_list|(
name|getBlockBlob
argument_list|(
literal|"repo.lock"
argument_list|)
argument_list|,
parameter_list|()
lambda|->
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Lost connection to the Azure. The client will be closed."
argument_list|)
expr_stmt|;
comment|// TODO close the connection
block|}
argument_list|)
operator|.
name|lock
argument_list|()
return|;
block|}
specifier|private
name|CloudBlockBlob
name|getBlockBlob
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|segmentstoreDirectory
operator|.
name|getBlockBlobReference
argument_list|(
name|path
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
decl||
name|StorageException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|CloudAppendBlob
name|getAppendBlob
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|segmentstoreDirectory
operator|.
name|getAppendBlobReference
argument_list|(
name|path
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
decl||
name|StorageException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|void
name|attachRemoteStoreMonitor
parameter_list|(
name|RemoteStoreMonitor
name|remoteStoreMonitor
parameter_list|)
block|{
name|OperationContext
operator|.
name|getGlobalRequestCompletedEventHandler
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|StorageEvent
argument_list|<
name|RequestCompletedEvent
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|eventOccurred
parameter_list|(
name|RequestCompletedEvent
name|e
parameter_list|)
block|{
name|Date
name|startDate
init|=
name|e
operator|.
name|getRequestResult
argument_list|()
operator|.
name|getStartDate
argument_list|()
decl_stmt|;
name|Date
name|stopDate
init|=
name|e
operator|.
name|getRequestResult
argument_list|()
operator|.
name|getStopDate
argument_list|()
decl_stmt|;
if|if
condition|(
name|startDate
operator|!=
literal|null
operator|&&
name|stopDate
operator|!=
literal|null
condition|)
block|{
name|long
name|requestDuration
init|=
name|stopDate
operator|.
name|getTime
argument_list|()
operator|-
name|startDate
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|remoteStoreMonitor
operator|.
name|requestDuration
argument_list|(
name|requestDuration
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
name|Exception
name|exception
init|=
name|e
operator|.
name|getRequestResult
argument_list|()
operator|.
name|getException
argument_list|()
decl_stmt|;
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
name|remoteStoreMonitor
operator|.
name|requestCount
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|remoteStoreMonitor
operator|.
name|requestError
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

