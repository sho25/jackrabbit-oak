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
name|base
operator|.
name|Preconditions
operator|.
name|checkState
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
name|Maps
operator|.
name|newHashMap
import|;
end_import

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
name|Map
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|jmx
operator|.
name|CacheStatsMBean
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
name|CachingSegmentReader
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
name|RecordType
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
name|Revisions
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
name|segment
operator|.
name|Segment
operator|.
name|RecordConsumer
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
name|segment
operator|.
name|SegmentCache
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
name|SegmentIdFactory
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
name|SegmentIdProvider
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
name|SegmentReader
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
name|SegmentStore
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
name|SegmentTracker
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
name|spi
operator|.
name|blob
operator|.
name|BlobStore
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
comment|/**  * The storage implementation for tar files.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractFileStore
implements|implements
name|SegmentStore
implements|,
name|Closeable
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
name|AbstractFileStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MANIFEST_FILE_NAME
init|=
literal|"manifest"
decl_stmt|;
comment|/**      * This value can be used as an invalid store version, since the store      * version is defined to be strictly greater than zero.      */
specifier|private
specifier|static
specifier|final
name|int
name|INVALID_STORE_VERSION
init|=
literal|0
decl_stmt|;
comment|/**      * The store version is an always incrementing number, strictly greater than      * zero, that is changed every time there is a backwards incompatible      * modification to the format of the segment store.      */
specifier|static
specifier|final
name|int
name|CURRENT_STORE_VERSION
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|FILE_NAME_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(data)((0|[1-9][0-9]*)[0-9]{4})([a-z])?.tar"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|String
name|FILE_NAME_FORMAT
init|=
literal|"data%05d%s.tar"
decl_stmt|;
annotation|@
name|Nonnull
specifier|final
name|SegmentTracker
name|tracker
decl_stmt|;
annotation|@
name|Nonnull
specifier|final
name|CachingSegmentReader
name|segmentReader
decl_stmt|;
specifier|final
name|File
name|directory
decl_stmt|;
specifier|private
specifier|final
name|BlobStore
name|blobStore
decl_stmt|;
specifier|final
name|boolean
name|memoryMapping
decl_stmt|;
annotation|@
name|Nonnull
specifier|final
name|SegmentCache
name|segmentCache
decl_stmt|;
specifier|final
name|TarRecovery
name|recovery
init|=
operator|new
name|TarRecovery
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|recoverEntry
parameter_list|(
name|UUID
name|uuid
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|TarWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|writeSegment
argument_list|(
name|uuid
argument_list|,
name|data
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|protected
specifier|final
name|IOMonitor
name|ioMonitor
decl_stmt|;
name|AbstractFileStore
parameter_list|(
specifier|final
name|FileStoreBuilder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|builder
operator|.
name|getDirectory
argument_list|()
expr_stmt|;
name|this
operator|.
name|tracker
operator|=
operator|new
name|SegmentTracker
argument_list|(
operator|new
name|SegmentIdFactory
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|SegmentId
name|newSegmentId
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
block|{
return|return
operator|new
name|SegmentId
argument_list|(
name|AbstractFileStore
operator|.
name|this
argument_list|,
name|msb
argument_list|,
name|lsb
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|blobStore
operator|=
name|builder
operator|.
name|getBlobStore
argument_list|()
expr_stmt|;
name|this
operator|.
name|segmentCache
operator|=
operator|new
name|SegmentCache
argument_list|(
name|builder
operator|.
name|getSegmentCacheSize
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|segmentReader
operator|=
operator|new
name|CachingSegmentReader
argument_list|(
operator|new
name|Supplier
argument_list|<
name|SegmentWriter
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SegmentWriter
name|get
parameter_list|()
block|{
return|return
name|getWriter
argument_list|()
return|;
block|}
block|}
argument_list|,
name|blobStore
argument_list|,
name|builder
operator|.
name|getStringCacheSize
argument_list|()
argument_list|,
name|builder
operator|.
name|getTemplateCacheSize
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|memoryMapping
operator|=
name|builder
operator|.
name|getMemoryMapping
argument_list|()
expr_stmt|;
name|this
operator|.
name|ioMonitor
operator|=
name|builder
operator|.
name|getIOMonitor
argument_list|()
expr_stmt|;
block|}
name|File
name|getManifestFile
parameter_list|()
block|{
return|return
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|MANIFEST_FILE_NAME
argument_list|)
return|;
block|}
name|Manifest
name|openManifest
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|file
init|=
name|getManifestFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|Manifest
operator|.
name|load
argument_list|(
name|file
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|static
name|Manifest
name|checkManifest
parameter_list|(
name|Manifest
name|manifest
parameter_list|)
throws|throws
name|InvalidFileStoreVersionException
block|{
if|if
condition|(
name|manifest
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidFileStoreVersionException
argument_list|(
literal|"Using oak-segment-tar, but oak-segment should be used"
argument_list|)
throw|;
block|}
name|int
name|storeVersion
init|=
name|manifest
operator|.
name|getStoreVersion
argument_list|(
name|INVALID_STORE_VERSION
argument_list|)
decl_stmt|;
comment|// A store version less than or equal to the highest invalid value means
comment|// that something or someone is messing up with the manifest. This error
comment|// is not recoverable and is thus represented as an ISE.
if|if
condition|(
name|storeVersion
operator|<=
name|INVALID_STORE_VERSION
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Invalid store version"
argument_list|)
throw|;
block|}
if|if
condition|(
name|storeVersion
operator|<
name|CURRENT_STORE_VERSION
condition|)
block|{
throw|throw
operator|new
name|InvalidFileStoreVersionException
argument_list|(
literal|"Using a too recent version of oak-segment-tar"
argument_list|)
throw|;
block|}
if|if
condition|(
name|storeVersion
operator|>
name|CURRENT_STORE_VERSION
condition|)
block|{
throw|throw
operator|new
name|InvalidFileStoreVersionException
argument_list|(
literal|"Using a too old version of oak-segment tar"
argument_list|)
throw|;
block|}
return|return
name|manifest
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|CacheStatsMBean
name|getSegmentCacheStats
parameter_list|()
block|{
return|return
name|segmentCache
operator|.
name|getCacheStats
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|CacheStatsMBean
name|getStringCacheStats
parameter_list|()
block|{
return|return
name|segmentReader
operator|.
name|getStringCacheStats
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|CacheStatsMBean
name|getTemplateCacheStats
parameter_list|()
block|{
return|return
name|segmentReader
operator|.
name|getTemplateCacheStats
argument_list|()
return|;
block|}
specifier|static
name|Map
argument_list|<
name|Integer
argument_list|,
name|Map
argument_list|<
name|Character
argument_list|,
name|File
argument_list|>
argument_list|>
name|collectFiles
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|Map
argument_list|<
name|Character
argument_list|,
name|File
argument_list|>
argument_list|>
name|dataFiles
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|listFiles
argument_list|(
name|directory
argument_list|)
control|)
block|{
name|Matcher
name|matcher
init|=
name|FILE_NAME_PATTERN
operator|.
name|matcher
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|Integer
name|index
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Character
argument_list|,
name|File
argument_list|>
name|files
init|=
name|dataFiles
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|files
operator|==
literal|null
condition|)
block|{
name|files
operator|=
name|newHashMap
argument_list|()
expr_stmt|;
name|dataFiles
operator|.
name|put
argument_list|(
name|index
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
name|Character
name|generation
init|=
literal|'a'
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|group
argument_list|(
literal|4
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|generation
operator|=
name|matcher
operator|.
name|group
argument_list|(
literal|4
argument_list|)
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|checkState
argument_list|(
name|files
operator|.
name|put
argument_list|(
name|generation
argument_list|,
name|file
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|dataFiles
return|;
block|}
annotation|@
name|Nonnull
specifier|private
specifier|static
name|File
index|[]
name|listFiles
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
name|File
index|[]
name|files
init|=
name|directory
operator|.
name|listFiles
argument_list|()
decl_stmt|;
return|return
name|files
operator|==
literal|null
condition|?
operator|new
name|File
index|[]
block|{}
else|:
name|files
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|SegmentTracker
name|getTracker
parameter_list|()
block|{
return|return
name|tracker
return|;
block|}
annotation|@
name|Nonnull
specifier|public
specifier|abstract
name|SegmentWriter
name|getWriter
parameter_list|()
function_decl|;
annotation|@
name|Nonnull
specifier|public
name|SegmentReader
name|getReader
parameter_list|()
block|{
return|return
name|segmentReader
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|SegmentIdProvider
name|getSegmentIdProvider
parameter_list|()
block|{
return|return
name|tracker
return|;
block|}
comment|/**      * @return the {@link Revisions} object bound to the current store.      */
specifier|public
specifier|abstract
name|Revisions
name|getRevisions
parameter_list|()
function_decl|;
comment|/**      * Convenience method for accessing the root node for the current head.      * This is equivalent to      *<pre>      * fileStore.getReader().readHeadState(fileStore.getRevisions())      *</pre>      * @return the current head node state      */
annotation|@
name|Nonnull
specifier|public
name|SegmentNodeState
name|getHead
parameter_list|()
block|{
return|return
name|segmentReader
operator|.
name|readHeadState
argument_list|(
name|getRevisions
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @return  the external BlobStore (if configured) with this store, {@code null} otherwise.      */
annotation|@
name|CheckForNull
specifier|public
name|BlobStore
name|getBlobStore
parameter_list|()
block|{
return|return
name|blobStore
return|;
block|}
specifier|private
name|void
name|writeSegment
parameter_list|(
name|UUID
name|id
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|TarWriter
name|w
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|msb
init|=
name|id
operator|.
name|getMostSignificantBits
argument_list|()
decl_stmt|;
name|long
name|lsb
init|=
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
decl_stmt|;
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|int
name|generation
init|=
name|Segment
operator|.
name|getGcGeneration
argument_list|(
name|buffer
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|w
operator|.
name|writeEntry
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|generation
argument_list|)
expr_stmt|;
if|if
condition|(
name|SegmentId
operator|.
name|isDataSegmentId
argument_list|(
name|lsb
argument_list|)
condition|)
block|{
name|Segment
name|segment
init|=
operator|new
name|Segment
argument_list|(
name|tracker
argument_list|,
name|segmentReader
argument_list|,
name|tracker
operator|.
name|newSegmentId
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
argument_list|,
name|buffer
argument_list|)
decl_stmt|;
name|populateTarGraph
argument_list|(
name|segment
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|populateTarBinaryReferences
argument_list|(
name|segment
argument_list|,
name|w
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|void
name|populateTarGraph
parameter_list|(
name|Segment
name|segment
parameter_list|,
name|TarWriter
name|w
parameter_list|)
block|{
name|UUID
name|from
init|=
name|segment
operator|.
name|getSegmentId
argument_list|()
operator|.
name|asUUID
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
name|segment
operator|.
name|getReferencedSegmentIdCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addGraphEdge
argument_list|(
name|from
argument_list|,
name|segment
operator|.
name|getReferencedSegmentId
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|void
name|populateTarBinaryReferences
parameter_list|(
specifier|final
name|Segment
name|segment
parameter_list|,
specifier|final
name|TarWriter
name|w
parameter_list|)
block|{
specifier|final
name|int
name|generation
init|=
name|segment
operator|.
name|getGcGeneration
argument_list|()
decl_stmt|;
specifier|final
name|UUID
name|id
init|=
name|segment
operator|.
name|getSegmentId
argument_list|()
operator|.
name|asUUID
argument_list|()
decl_stmt|;
name|segment
operator|.
name|forEachRecord
argument_list|(
operator|new
name|RecordConsumer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|consume
parameter_list|(
name|int
name|number
parameter_list|,
name|RecordType
name|type
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
name|RecordType
operator|.
name|BLOB_ID
condition|)
block|{
name|w
operator|.
name|addBinaryReference
argument_list|(
name|generation
argument_list|,
name|id
argument_list|,
name|SegmentBlob
operator|.
name|readBlobId
argument_list|(
name|segment
argument_list|,
name|number
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|static
name|void
name|closeAndLogOnFail
parameter_list|(
name|Closeable
name|closeable
parameter_list|)
block|{
if|if
condition|(
name|closeable
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|closeable
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// ignore and log
name|log
operator|.
name|error
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

