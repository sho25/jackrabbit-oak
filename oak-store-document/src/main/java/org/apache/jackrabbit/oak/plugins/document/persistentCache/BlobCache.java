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
name|document
operator|.
name|persistentCache
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Iterator
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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|document
operator|.
name|persistentCache
operator|.
name|PersistentCache
operator|.
name|GenerationCache
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
name|blob
operator|.
name|BlobOptions
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
name|blob
operator|.
name|GarbageCollectableBlobStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|h2
operator|.
name|mvstore
operator|.
name|MVMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|h2
operator|.
name|mvstore
operator|.
name|StreamStore
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
comment|/**  * A persistent blob cache. Only blobs that are smaller than 10% of the maximum  * cache size are stored.  */
end_comment

begin_class
specifier|public
class|class
name|BlobCache
implements|implements
name|GarbageCollectableBlobStore
implements|,
name|GenerationCache
implements|,
name|Closeable
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BlobCache
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|GarbageCollectableBlobStore
name|base
decl_stmt|;
specifier|private
specifier|final
name|PersistentCache
name|cache
decl_stmt|;
specifier|private
specifier|final
name|MultiGenerationMap
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|meta
decl_stmt|;
specifier|private
name|MultiGenerationMap
argument_list|<
name|Long
argument_list|,
name|byte
index|[]
argument_list|>
name|data
decl_stmt|;
specifier|private
name|StreamStore
name|streamStore
decl_stmt|;
specifier|private
name|long
name|maxEntrySize
decl_stmt|;
specifier|public
name|BlobCache
parameter_list|(
name|PersistentCache
name|cache
parameter_list|,
name|GarbageCollectableBlobStore
name|base
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|data
operator|=
operator|new
name|MultiGenerationMap
argument_list|<
name|Long
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|()
expr_stmt|;
name|meta
operator|=
operator|new
name|MultiGenerationMap
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|()
expr_stmt|;
name|maxEntrySize
operator|=
name|cache
operator|.
name|getMaxBinaryEntrySize
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|CacheType
name|getType
parameter_list|()
block|{
return|return
name|CacheType
operator|.
name|BLOB
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addGeneration
parameter_list|(
name|int
name|generation
parameter_list|,
name|boolean
name|readOnly
parameter_list|)
block|{
name|CacheMap
argument_list|<
name|Long
argument_list|,
name|byte
index|[]
argument_list|>
name|d
init|=
name|cache
operator|.
name|openMap
argument_list|(
name|generation
argument_list|,
literal|"data"
argument_list|,
operator|new
name|MVMap
operator|.
name|Builder
argument_list|<
name|Long
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|data
operator|.
name|addReadMap
argument_list|(
name|generation
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|CacheMap
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|m
init|=
name|cache
operator|.
name|openMap
argument_list|(
name|generation
argument_list|,
literal|"meta"
argument_list|,
operator|new
name|MVMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|meta
operator|.
name|addReadMap
argument_list|(
name|generation
argument_list|,
name|m
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|readOnly
condition|)
block|{
comment|// the order is important:
comment|// if we switch the data first,
comment|// we could end up with the data in store 1
comment|// but the metadata in store 2 - which could
comment|// result in a data block not found if store 1
comment|// is removed later on
name|meta
operator|.
name|setWriteMap
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|data
operator|.
name|setWriteMap
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|streamStore
operator|==
literal|null
condition|)
block|{
name|streamStore
operator|=
operator|new
name|StreamStore
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeGeneration
parameter_list|(
name|int
name|generation
parameter_list|)
block|{
name|data
operator|.
name|removeReadMap
argument_list|(
name|generation
argument_list|)
expr_stmt|;
name|meta
operator|.
name|removeReadMap
argument_list|(
name|generation
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getInputStream
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|streamStore
operator|==
literal|null
condition|)
block|{
return|return
name|base
operator|.
name|getInputStream
argument_list|(
name|blobId
argument_list|)
return|;
block|}
name|cache
operator|.
name|switchGenerationIfNeeded
argument_list|()
expr_stmt|;
name|byte
index|[]
name|id
init|=
name|meta
operator|.
name|get
argument_list|(
name|blobId
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
name|long
name|length
init|=
name|base
operator|.
name|getBlobLength
argument_list|(
name|blobId
argument_list|)
decl_stmt|;
name|InputStream
name|in
init|=
name|base
operator|.
name|getInputStream
argument_list|(
name|blobId
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|<
name|base
operator|.
name|getBlockSizeMin
argument_list|()
condition|)
block|{
comment|// in-place
return|return
name|in
return|;
block|}
if|if
condition|(
name|length
operator|>
name|maxEntrySize
condition|)
block|{
comment|// too large, don't cache
return|return
name|in
return|;
block|}
name|id
operator|=
name|streamStore
operator|.
name|put
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|meta
operator|.
name|put
argument_list|(
name|blobId
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
return|return
name|streamStore
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|writeBlob
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO maybe copy the binary to the cache in a background thread
return|return
name|base
operator|.
name|writeBlob
argument_list|(
name|in
argument_list|)
return|;
block|}
comment|/**      * Ignores the options provided and delegates to {@link #writeBlob(InputStream)}.      *      * @param in the input stream to write      * @param options the options to use      * @return      * @throws IOException      */
annotation|@
name|Override
specifier|public
name|String
name|writeBlob
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|BlobOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|writeBlob
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|readBlob
parameter_list|(
name|String
name|blobId
parameter_list|,
name|long
name|pos
parameter_list|,
name|byte
index|[]
name|buff
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|in
init|=
name|getInputStream
argument_list|(
name|blobId
argument_list|)
decl_stmt|;
name|long
name|remainingSkip
init|=
name|pos
decl_stmt|;
while|while
condition|(
name|remainingSkip
operator|>
literal|0
condition|)
block|{
name|remainingSkip
operator|-=
name|in
operator|.
name|skip
argument_list|(
name|remainingSkip
argument_list|)
expr_stmt|;
block|}
return|return
name|in
operator|.
name|read
argument_list|(
name|buff
argument_list|,
name|off
argument_list|,
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getBlobLength
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|base
operator|.
name|getBlobLength
argument_list|(
name|blobId
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|String
name|getBlobId
parameter_list|(
annotation|@
name|Nonnull
name|String
name|reference
parameter_list|)
block|{
return|return
name|base
operator|.
name|getBlobId
argument_list|(
name|reference
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|String
name|getReference
parameter_list|(
annotation|@
name|Nonnull
name|String
name|blobId
parameter_list|)
block|{
return|return
name|base
operator|.
name|getReference
argument_list|(
name|blobId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clearCache
parameter_list|()
block|{
name|base
operator|.
name|clearCache
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clearInUse
parameter_list|()
block|{
name|base
operator|.
name|clearInUse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Deprecated
specifier|public
name|boolean
name|deleteChunks
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|chunkIds
parameter_list|,
name|long
name|maxLastModifiedTime
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|base
operator|.
name|deleteChunks
argument_list|(
name|chunkIds
argument_list|,
name|maxLastModifiedTime
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|countDeleteChunks
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|chunkIds
parameter_list|,
name|long
name|maxLastModifiedTime
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|base
operator|.
name|countDeleteChunks
argument_list|(
name|chunkIds
argument_list|,
name|maxLastModifiedTime
argument_list|)
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
return|return
name|base
operator|.
name|getAllChunkIds
argument_list|(
name|maxLastModifiedTime
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getBlockSizeMin
parameter_list|()
block|{
return|return
name|base
operator|.
name|getBlockSizeMin
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|resolveChunks
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|base
operator|.
name|resolveChunks
argument_list|(
name|blobId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setBlockSize
parameter_list|(
name|int
name|x
parameter_list|)
block|{
name|base
operator|.
name|setBlockSize
argument_list|(
name|x
argument_list|)
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
name|base
operator|.
name|startMark
argument_list|()
expr_stmt|;
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
name|base
operator|.
name|sweep
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|writeBlob
parameter_list|(
name|String
name|tempFileName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|base
operator|.
name|writeBlob
argument_list|(
name|tempFileName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|receive
parameter_list|(
name|ByteBuffer
name|buff
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
name|base
operator|instanceof
name|Closeable
condition|)
block|{
operator|(
operator|(
name|Closeable
operator|)
name|base
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

