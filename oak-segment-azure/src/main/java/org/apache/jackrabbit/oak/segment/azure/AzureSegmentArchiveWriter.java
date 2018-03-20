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
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Stopwatch
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
name|azure
operator|.
name|queue
operator|.
name|SegmentWriteAction
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
name|azure
operator|.
name|queue
operator|.
name|SegmentWriteQueue
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
name|SegmentArchiveEntry
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
name|Collections
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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
name|TimeUnit
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
name|azure
operator|.
name|AzureUtilities
operator|.
name|getSegmentFileName
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
name|azure
operator|.
name|AzureUtilities
operator|.
name|readBufferFully
import|;
end_import

begin_class
specifier|public
class|class
name|AzureSegmentArchiveWriter
implements|implements
name|SegmentArchiveWriter
block|{
specifier|private
specifier|final
name|CloudBlobDirectory
name|archiveDirectory
decl_stmt|;
specifier|private
specifier|final
name|IOMonitor
name|ioMonitor
decl_stmt|;
specifier|private
specifier|final
name|FileStoreMonitor
name|monitor
decl_stmt|;
specifier|private
specifier|final
name|Optional
argument_list|<
name|SegmentWriteQueue
argument_list|>
name|queue
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|UUID
argument_list|,
name|AzureSegmentArchiveEntry
argument_list|>
name|index
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|int
name|entries
decl_stmt|;
specifier|private
name|long
name|totalLength
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|created
init|=
literal|false
decl_stmt|;
specifier|public
name|AzureSegmentArchiveWriter
parameter_list|(
name|CloudBlobDirectory
name|archiveDirectory
parameter_list|,
name|IOMonitor
name|ioMonitor
parameter_list|,
name|FileStoreMonitor
name|monitor
parameter_list|)
block|{
name|this
operator|.
name|archiveDirectory
operator|=
name|archiveDirectory
expr_stmt|;
name|this
operator|.
name|ioMonitor
operator|=
name|ioMonitor
expr_stmt|;
name|this
operator|.
name|monitor
operator|=
name|monitor
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|SegmentWriteQueue
operator|.
name|THREADS
operator|>
literal|0
condition|?
name|Optional
operator|.
name|of
argument_list|(
operator|new
name|SegmentWriteQueue
argument_list|(
name|this
operator|::
name|doWriteEntry
argument_list|)
argument_list|)
else|:
name|Optional
operator|.
name|empty
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeSegment
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|generation
parameter_list|,
name|int
name|fullGeneration
parameter_list|,
name|boolean
name|compacted
parameter_list|)
throws|throws
name|IOException
block|{
name|created
operator|=
literal|true
expr_stmt|;
name|AzureSegmentArchiveEntry
name|entry
init|=
operator|new
name|AzureSegmentArchiveEntry
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|,
name|entries
operator|++
argument_list|,
name|size
argument_list|,
name|generation
argument_list|,
name|fullGeneration
argument_list|,
name|compacted
argument_list|)
decl_stmt|;
if|if
condition|(
name|queue
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|queue
operator|.
name|get
argument_list|()
operator|.
name|addToQueue
argument_list|(
name|entry
argument_list|,
name|data
argument_list|,
name|offset
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doWriteEntry
argument_list|(
name|entry
argument_list|,
name|data
argument_list|,
name|offset
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
name|index
operator|.
name|put
argument_list|(
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
argument_list|,
name|entry
argument_list|)
expr_stmt|;
name|totalLength
operator|+=
name|size
expr_stmt|;
name|monitor
operator|.
name|written
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doWriteEntry
parameter_list|(
name|AzureSegmentArchiveEntry
name|indexEntry
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|msb
init|=
name|indexEntry
operator|.
name|getMsb
argument_list|()
decl_stmt|;
name|long
name|lsb
init|=
name|indexEntry
operator|.
name|getLsb
argument_list|()
decl_stmt|;
name|ioMonitor
operator|.
name|beforeSegmentWrite
argument_list|(
name|pathAsFile
argument_list|()
argument_list|,
name|msb
argument_list|,
name|lsb
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|Stopwatch
name|stopwatch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
try|try
block|{
name|CloudBlockBlob
name|blob
init|=
name|getBlob
argument_list|(
name|getSegmentFileName
argument_list|(
name|indexEntry
argument_list|)
argument_list|)
decl_stmt|;
name|blob
operator|.
name|setMetadata
argument_list|(
name|AzureBlobMetadata
operator|.
name|toSegmentMetadata
argument_list|(
name|indexEntry
argument_list|)
argument_list|)
expr_stmt|;
name|blob
operator|.
name|uploadFromByteArray
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|blob
operator|.
name|uploadMetadata
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
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
name|ioMonitor
operator|.
name|afterSegmentWrite
argument_list|(
name|pathAsFile
argument_list|()
argument_list|,
name|msb
argument_list|,
name|lsb
argument_list|,
name|size
argument_list|,
name|stopwatch
operator|.
name|elapsed
argument_list|(
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ByteBuffer
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
name|UUID
name|uuid
init|=
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
decl_stmt|;
name|Optional
argument_list|<
name|SegmentWriteAction
argument_list|>
name|segment
init|=
name|queue
operator|.
name|map
argument_list|(
name|q
lambda|->
name|q
operator|.
name|read
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|segment
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|segment
operator|.
name|get
argument_list|()
operator|.
name|toByteBuffer
argument_list|()
return|;
block|}
name|AzureSegmentArchiveEntry
name|indexEntry
init|=
name|index
operator|.
name|get
argument_list|(
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexEntry
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|indexEntry
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|readBufferFully
argument_list|(
name|getBlob
argument_list|(
name|getSegmentFileName
argument_list|(
name|indexEntry
argument_list|)
argument_list|)
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
return|return
name|buffer
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|containsSegment
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
block|{
name|UUID
name|uuid
init|=
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
decl_stmt|;
name|Optional
argument_list|<
name|SegmentWriteAction
argument_list|>
name|segment
init|=
name|queue
operator|.
name|map
argument_list|(
name|q
lambda|->
name|q
operator|.
name|read
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|segment
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|index
operator|.
name|containsKey
argument_list|(
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeGraph
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|writeDataFile
argument_list|(
name|data
argument_list|,
literal|".gph"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeBinaryReferences
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|writeDataFile
argument_list|(
name|data
argument_list|,
literal|".brf"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeDataFile
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|String
name|extension
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|getBlob
argument_list|(
name|getName
argument_list|()
operator|+
name|extension
argument_list|)
operator|.
name|uploadFromByteArray
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
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
name|totalLength
operator|+=
name|data
operator|.
name|length
expr_stmt|;
name|monitor
operator|.
name|written
argument_list|(
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|totalLength
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|queue
operator|.
name|isPresent
argument_list|()
condition|)
block|{
comment|// required to handle IOException
name|SegmentWriteQueue
name|q
init|=
name|queue
operator|.
name|get
argument_list|()
decl_stmt|;
name|q
operator|.
name|flush
argument_list|()
expr_stmt|;
name|q
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|getBlob
argument_list|(
literal|"closed"
argument_list|)
operator|.
name|uploadFromByteArray
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
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
annotation|@
name|Override
specifier|public
name|boolean
name|isCreated
parameter_list|()
block|{
return|return
name|created
operator|||
operator|!
name|queueIsEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|queue
operator|.
name|isPresent
argument_list|()
condition|)
block|{
comment|// required to handle IOException
name|queue
operator|.
name|get
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|queueIsEmpty
parameter_list|()
block|{
return|return
name|queue
operator|.
name|map
argument_list|(
name|SegmentWriteQueue
operator|::
name|isEmpty
argument_list|)
operator|.
name|orElse
argument_list|(
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|AzureUtilities
operator|.
name|getName
argument_list|(
name|archiveDirectory
argument_list|)
return|;
block|}
specifier|private
name|File
name|pathAsFile
parameter_list|()
block|{
return|return
operator|new
name|File
argument_list|(
name|archiveDirectory
operator|.
name|getUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|CloudBlockBlob
name|getBlob
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|archiveDirectory
operator|.
name|getBlockBlobReference
argument_list|(
name|name
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
block|}
end_class

end_unit

