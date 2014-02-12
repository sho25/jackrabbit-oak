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
name|blob
operator|.
name|cloud
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|jclouds
operator|.
name|blobstore
operator|.
name|options
operator|.
name|ListContainerOptions
operator|.
name|Builder
operator|.
name|maxResults
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|jclouds
operator|.
name|blobstore
operator|.
name|options
operator|.
name|PutOptions
operator|.
name|Builder
operator|.
name|multipart
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
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|NoSuchElementException
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
name|Preconditions
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
name|Maps
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
name|io
operator|.
name|ByteStreams
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
name|mk
operator|.
name|blobs
operator|.
name|AbstractBlobStore
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
name|mk
operator|.
name|util
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jclouds
operator|.
name|ContextBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jclouds
operator|.
name|blobstore
operator|.
name|BlobStoreContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jclouds
operator|.
name|blobstore
operator|.
name|domain
operator|.
name|Blob
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jclouds
operator|.
name|blobstore
operator|.
name|domain
operator|.
name|PageSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jclouds
operator|.
name|blobstore
operator|.
name|domain
operator|.
name|StorageMetadata
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jclouds
operator|.
name|io
operator|.
name|Payload
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

begin_comment
comment|/**  * Implementation of the {@link BlobStore} to store blobs in a cloud blob store.  *<p>  * Extends {@link AbstractBlobStore} and breaks the the binary to chunks for easier management.  */
end_comment

begin_class
specifier|public
class|class
name|CloudBlobStore
extends|extends
name|AbstractBlobStore
block|{
comment|/**      * Logger instance.      */
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CloudBlobStore
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Cloud Store context */
specifier|private
name|BlobStoreContext
name|context
decl_stmt|;
comment|/** The bucket. */
specifier|private
name|String
name|cloudContainer
decl_stmt|;
specifier|private
name|String
name|accessKey
decl_stmt|;
specifier|private
name|String
name|secretKey
decl_stmt|;
specifier|private
name|String
name|cloudProvider
decl_stmt|;
specifier|protected
name|String
name|getCloudContainer
parameter_list|()
block|{
return|return
name|cloudContainer
return|;
block|}
specifier|public
name|void
name|setCloudContainer
parameter_list|(
name|String
name|cloudContainer
parameter_list|)
block|{
name|this
operator|.
name|cloudContainer
operator|=
name|cloudContainer
expr_stmt|;
block|}
specifier|public
name|String
name|getAccessKey
parameter_list|()
block|{
return|return
name|accessKey
return|;
block|}
specifier|public
name|void
name|setAccessKey
parameter_list|(
name|String
name|accessKey
parameter_list|)
block|{
name|this
operator|.
name|accessKey
operator|=
name|accessKey
expr_stmt|;
block|}
specifier|public
name|String
name|getSecretKey
parameter_list|()
block|{
return|return
name|secretKey
return|;
block|}
specifier|public
name|void
name|setSecretKey
parameter_list|(
name|String
name|secretKey
parameter_list|)
block|{
name|this
operator|.
name|secretKey
operator|=
name|secretKey
expr_stmt|;
block|}
specifier|public
name|String
name|getCloudProvider
parameter_list|()
block|{
return|return
name|cloudProvider
return|;
block|}
specifier|public
name|void
name|setCloudProvider
parameter_list|(
name|String
name|cloudProvider
parameter_list|)
block|{
name|this
operator|.
name|cloudProvider
operator|=
name|cloudProvider
expr_stmt|;
block|}
comment|/**      * Instantiates a connection to the cloud blob store.      *       * @param cloudProvider      *            the cloud provider      * @param accessKey      *            the access key      * @param secretKey      *            the secret key      * @param cloudContainer      *            the bucket      * @throws Exception      */
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|this
operator|.
name|context
operator|=
name|ContextBuilder
operator|.
name|newBuilder
argument_list|(
name|cloudProvider
argument_list|)
operator|.
name|credentials
argument_list|(
name|accessKey
argument_list|,
name|secretKey
argument_list|)
operator|.
name|buildView
argument_list|(
name|BlobStoreContext
operator|.
name|class
argument_list|)
expr_stmt|;
name|context
operator|.
name|getBlobStore
argument_list|()
operator|.
name|createContainerInLocation
argument_list|(
literal|null
argument_list|,
name|cloudContainer
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using container : "
operator|+
name|cloudContainer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error creating CloudBlobStore : "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/**      * Uploads the block to the cloud service.      */
annotation|@
name|Override
specifier|protected
name|void
name|storeBlock
parameter_list|(
name|byte
index|[]
name|digest
parameter_list|,
name|int
name|level
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|digest
argument_list|)
decl_stmt|;
name|org
operator|.
name|jclouds
operator|.
name|blobstore
operator|.
name|BlobStore
name|blobStore
init|=
name|context
operator|.
name|getBlobStore
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|blobStore
operator|.
name|blobExists
argument_list|(
name|cloudContainer
argument_list|,
name|id
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|put
argument_list|(
literal|"level"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|level
argument_list|)
argument_list|)
expr_stmt|;
name|Blob
name|blob
init|=
name|blobStore
operator|.
name|blobBuilder
argument_list|(
name|id
argument_list|)
operator|.
name|payload
argument_list|(
name|data
argument_list|)
operator|.
name|userMetadata
argument_list|(
name|metadata
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|etag
init|=
name|blobStore
operator|.
name|putBlob
argument_list|(
name|cloudContainer
argument_list|,
name|blob
argument_list|,
name|multipart
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Blob "
operator|+
name|id
operator|+
literal|" created with cloud tag : "
operator|+
name|etag
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Blob "
operator|+
name|id
operator|+
literal|" already exists"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Reads the data from the actual cloud service.      */
annotation|@
name|Override
specifier|protected
name|byte
index|[]
name|readBlockFromBackend
parameter_list|(
name|BlockId
name|blockId
parameter_list|)
throws|throws
name|Exception
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|blockId
operator|.
name|getDigest
argument_list|()
argument_list|)
decl_stmt|;
name|Blob
name|cloudBlob
init|=
name|context
operator|.
name|getBlobStore
argument_list|()
operator|.
name|getBlob
argument_list|(
name|cloudContainer
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|cloudBlob
operator|==
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"Did not find block "
operator|+
name|id
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|)
throw|;
block|}
name|Payload
name|payload
init|=
name|cloudBlob
operator|.
name|getPayload
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|data
init|=
name|ByteStreams
operator|.
name|toByteArray
argument_list|(
name|payload
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|blockId
operator|.
name|getPos
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|data
return|;
block|}
name|int
name|len
init|=
call|(
name|int
call|)
argument_list|(
name|data
operator|.
name|length
operator|-
name|blockId
operator|.
name|getPos
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|<
literal|0
condition|)
block|{
return|return
operator|new
name|byte
index|[
literal|0
index|]
return|;
block|}
name|byte
index|[]
name|d2
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
operator|(
name|int
operator|)
name|blockId
operator|.
name|getPos
argument_list|()
argument_list|,
name|d2
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|d2
return|;
block|}
finally|finally
block|{
name|payload
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Delete the cloud container and all its contents.      *       */
specifier|public
name|void
name|deleteBucket
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|getBlobStore
argument_list|()
operator|.
name|containerExists
argument_list|(
name|cloudContainer
argument_list|)
condition|)
block|{
name|context
operator|.
name|getBlobStore
argument_list|()
operator|.
name|deleteContainer
argument_list|(
name|cloudContainer
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startMark
parameter_list|()
throws|throws
name|IOException
block|{
comment|// No-op
block|}
annotation|@
name|Override
specifier|protected
name|void
name|mark
parameter_list|(
name|BlockId
name|id
parameter_list|)
throws|throws
name|Exception
block|{
comment|// No-op
block|}
annotation|@
name|Override
specifier|public
name|int
name|sweep
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isMarkEnabled
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getAllChunkIds
parameter_list|(
name|long
name|maxLastModifiedTime
parameter_list|)
throws|throws
name|Exception
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|context
argument_list|)
expr_stmt|;
specifier|final
name|org
operator|.
name|jclouds
operator|.
name|blobstore
operator|.
name|BlobStore
name|blobStore
init|=
name|context
operator|.
name|getBlobStore
argument_list|()
decl_stmt|;
return|return
operator|new
name|CloudStoreIterator
argument_list|(
name|blobStore
argument_list|,
name|maxLastModifiedTime
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|deleteChunk
parameter_list|(
name|String
name|chunkId
parameter_list|)
throws|throws
name|Exception
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|context
argument_list|)
expr_stmt|;
specifier|final
name|org
operator|.
name|jclouds
operator|.
name|blobstore
operator|.
name|BlobStore
name|blobStore
init|=
name|context
operator|.
name|getBlobStore
argument_list|()
decl_stmt|;
name|blobStore
operator|.
name|removeBlob
argument_list|(
name|cloudContainer
argument_list|,
name|chunkId
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
class|class
name|CloudStoreIterator
implements|implements
name|Iterator
argument_list|<
name|String
argument_list|>
block|{
specifier|static
specifier|final
name|int
name|BATCH
init|=
literal|1000
decl_stmt|;
name|org
operator|.
name|jclouds
operator|.
name|blobstore
operator|.
name|BlobStore
name|store
decl_stmt|;
name|long
name|maxLastModifiedTime
decl_stmt|;
name|PageSet
argument_list|<
name|?
extends|extends
name|StorageMetadata
argument_list|>
name|set
decl_stmt|;
name|ArrayDeque
argument_list|<
name|String
argument_list|>
name|queue
decl_stmt|;
specifier|public
name|CloudStoreIterator
parameter_list|(
name|org
operator|.
name|jclouds
operator|.
name|blobstore
operator|.
name|BlobStore
name|store
parameter_list|,
name|long
name|maxLastModifiedTime
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|maxLastModifiedTime
operator|=
name|maxLastModifiedTime
expr_stmt|;
name|this
operator|.
name|queue
operator|=
operator|new
name|ArrayDeque
argument_list|<
name|String
argument_list|>
argument_list|(
name|BATCH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
operator|(
name|set
operator|==
literal|null
operator|)
operator|||
operator|(
name|queue
operator|==
literal|null
operator|)
condition|)
block|{
name|set
operator|=
name|store
operator|.
name|list
argument_list|(
name|cloudContainer
argument_list|,
name|maxResults
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|loadElements
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|set
operator|.
name|getNextMarker
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|set
operator|=
name|store
operator|.
name|list
argument_list|(
name|cloudContainer
argument_list|,
name|maxResults
argument_list|(
name|BATCH
argument_list|)
operator|.
name|afterMarker
argument_list|(
name|set
operator|.
name|getNextMarker
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|loadElements
argument_list|(
name|set
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|void
name|loadElements
parameter_list|(
name|PageSet
argument_list|<
name|?
extends|extends
name|StorageMetadata
argument_list|>
name|set
parameter_list|)
block|{
name|Iterator
argument_list|<
name|?
extends|extends
name|StorageMetadata
argument_list|>
name|iter
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|StorageMetadata
name|metadata
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|maxLastModifiedTime
operator|==
literal|0
operator|||
name|maxLastModifiedTime
operator|==
operator|-
literal|1
operator|)
operator|||
operator|(
name|metadata
operator|.
name|getLastModified
argument_list|()
operator|.
name|getTime
argument_list|()
operator|<=
name|maxLastModifiedTime
operator|)
condition|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|metadata
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queue
operator|.
name|add
argument_list|(
name|metadata
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"No more elements"
argument_list|)
throw|;
block|}
return|return
name|queue
operator|.
name|poll
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

