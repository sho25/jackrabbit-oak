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
name|data
operator|.
name|SegmentData
operator|.
name|newSegmentData
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
name|Collection
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
name|ExecutionException
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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|EntryRecovery
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
name|segment
operator|.
name|file
operator|.
name|tar
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
name|file
operator|.
name|tar
operator|.
name|TarFiles
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
name|TarRecovery
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
comment|/**      * The minimum supported store version. It is possible for an implementation      * to support in a transparent and backwards-compatible way older versions      * of a repository. In this case, the minimum supported store version      * identifies the store format that can still be processed by the      * implementation. The minimum store version has to be greater than zero and      * less than or equal to the maximum store version.      */
specifier|private
specifier|static
specifier|final
name|int
name|MIN_STORE_VERSION
init|=
literal|1
decl_stmt|;
comment|/**      * The maximum supported store version. It is possible for an implementation      * to support in a transparent and forwards-compatible way newer version of      * a repository. In this case, the maximum supported store version      * identifies the store format that can still be processed by the      * implementation. The maximum supported store version has to be greater      * than zero and greater than or equal to the minimum store version.      */
specifier|private
specifier|static
specifier|final
name|int
name|MAX_STORE_VERSION
init|=
literal|2
decl_stmt|;
specifier|static
name|ManifestChecker
name|newManifestChecker
parameter_list|(
name|File
name|directory
parameter_list|,
name|boolean
name|strictVersionCheck
parameter_list|)
block|{
return|return
name|ManifestChecker
operator|.
name|newManifestChecker
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|MANIFEST_FILE_NAME
argument_list|)
argument_list|,
name|notEmptyDirectory
argument_list|(
name|directory
argument_list|)
argument_list|,
name|strictVersionCheck
condition|?
name|MAX_STORE_VERSION
else|:
name|MIN_STORE_VERSION
argument_list|,
name|MAX_STORE_VERSION
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|notEmptyDirectory
parameter_list|(
name|File
name|path
parameter_list|)
block|{
name|Collection
argument_list|<
name|File
argument_list|>
name|entries
init|=
name|FileUtils
operator|.
name|listFiles
argument_list|(
name|path
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"tar"
block|}
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|!
name|entries
operator|.
name|isEmpty
argument_list|()
return|;
block|}
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
name|EntryRecovery
name|entryRecovery
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
name|entryRecovery
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
argument_list|,
name|segmentCache
operator|::
name|recordHit
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
name|this
operator|::
name|getWriter
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
specifier|static
name|SegmentNotFoundException
name|asSegmentNotFoundException
parameter_list|(
name|ExecutionException
name|e
parameter_list|,
name|SegmentId
name|id
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|SegmentNotFoundException
condition|)
block|{
return|return
operator|(
name|SegmentNotFoundException
operator|)
name|e
operator|.
name|getCause
argument_list|()
return|;
block|}
return|return
operator|new
name|SegmentNotFoundException
argument_list|(
name|id
argument_list|,
name|e
argument_list|)
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
name|EntryRecovery
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
name|GCGeneration
name|generation
init|=
name|Segment
operator|.
name|getGcGeneration
argument_list|(
name|newSegmentData
argument_list|(
name|buffer
argument_list|)
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|w
operator|.
name|recoverEntry
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
specifier|private
specifier|static
name|void
name|populateTarGraph
parameter_list|(
name|Segment
name|segment
parameter_list|,
name|EntryRecovery
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
name|recoverGraphEdge
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
specifier|private
specifier|static
name|void
name|populateTarBinaryReferences
parameter_list|(
specifier|final
name|Segment
name|segment
parameter_list|,
specifier|final
name|EntryRecovery
name|w
parameter_list|)
block|{
specifier|final
name|GCGeneration
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
parameter_list|(
name|number
parameter_list|,
name|type
parameter_list|,
name|offset
parameter_list|)
lambda|->
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
name|recoverBinaryReference
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
argument_list|)
expr_stmt|;
block|}
specifier|static
name|Set
argument_list|<
name|UUID
argument_list|>
name|readReferences
parameter_list|(
name|Segment
name|segment
parameter_list|)
block|{
name|Set
argument_list|<
name|UUID
argument_list|>
name|references
init|=
operator|new
name|HashSet
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
name|segment
operator|.
name|getReferencedSegmentIdCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|references
operator|.
name|add
argument_list|(
name|segment
operator|.
name|getReferencedSegmentId
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|references
return|;
block|}
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|readBinaryReferences
parameter_list|(
specifier|final
name|Segment
name|segment
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|binaryReferences
init|=
operator|new
name|HashSet
argument_list|<>
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
name|binaryReferences
operator|.
name|add
argument_list|(
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
return|return
name|binaryReferences
return|;
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
name|Segment
name|readSegmentUncached
parameter_list|(
name|TarFiles
name|tarFiles
parameter_list|,
name|SegmentId
name|id
parameter_list|)
block|{
name|ByteBuffer
name|buffer
init|=
name|tarFiles
operator|.
name|readSegment
argument_list|(
name|id
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SegmentNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
block|}
return|return
operator|new
name|Segment
argument_list|(
name|tracker
argument_list|,
name|segmentReader
argument_list|,
name|id
argument_list|,
name|buffer
argument_list|)
return|;
block|}
block|}
end_class

end_unit

