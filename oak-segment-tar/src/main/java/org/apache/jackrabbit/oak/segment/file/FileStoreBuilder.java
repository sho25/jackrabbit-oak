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
name|checkNotNull
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
name|base
operator|.
name|Preconditions
operator|.
name|checkState
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singleton
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
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|SegmentWriterBuilder
operator|.
name|segmentWriterBuilder
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
name|compaction
operator|.
name|SegmentGCOptions
operator|.
name|defaultGCOptions
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
name|Predicate
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
name|segment
operator|.
name|RecordId
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
name|WriterCacheManager
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
name|compaction
operator|.
name|LoggingGCMonitor
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
name|compaction
operator|.
name|SegmentGCOptions
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
operator|.
name|ReadOnlyStore
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|gc
operator|.
name|DelegatingGCMonitor
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
name|gc
operator|.
name|GCMonitor
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
name|stats
operator|.
name|StatisticsProvider
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
comment|/**  * Builder for creating {@link FileStore} instances.  */
end_comment

begin_comment
comment|// FIXME OAK-4449: SegmentNodeStore and SegmentStore builders should log their parameters on build()
end_comment

begin_class
specifier|public
class|class
name|FileStoreBuilder
block|{
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
name|FileStore
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|File
name|directory
decl_stmt|;
annotation|@
name|CheckForNull
specifier|private
name|BlobStore
name|blobStore
decl_stmt|;
comment|// null ->  store blobs inline
specifier|private
name|int
name|maxFileSize
init|=
literal|256
decl_stmt|;
specifier|private
name|int
name|cacheSize
decl_stmt|;
comment|// 0 -> DEFAULT_MEMORY_CACHE_SIZE
specifier|private
name|boolean
name|memoryMapping
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|DelegatingGCMonitor
name|gcMonitor
init|=
operator|new
name|DelegatingGCMonitor
argument_list|(
name|singleton
argument_list|(
operator|new
name|LoggingGCMonitor
argument_list|(
name|LOG
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
name|StatisticsProvider
name|statsProvider
init|=
name|StatisticsProvider
operator|.
name|NOOP
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
name|SegmentGCOptions
name|gcOptions
init|=
name|defaultGCOptions
argument_list|()
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
name|GCListener
name|gcListener
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|WriterCacheManager
name|cacheManager
init|=
operator|new
name|WriterCacheManager
operator|.
name|Default
argument_list|()
block|{
block|{
name|gcListener
operator|=
operator|new
name|GCListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|info
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{
name|gcMonitor
operator|.
name|info
argument_list|(
name|message
argument_list|,
name|arguments
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|warn
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{
name|gcMonitor
operator|.
name|warn
argument_list|(
name|message
argument_list|,
name|arguments
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|exception
parameter_list|)
block|{
name|gcMonitor
operator|.
name|error
argument_list|(
name|message
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|skipped
parameter_list|(
name|String
name|reason
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{
name|gcMonitor
operator|.
name|skipped
argument_list|(
name|reason
argument_list|,
name|arguments
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|compacted
parameter_list|(
name|long
index|[]
name|segmentCounts
parameter_list|,
name|long
index|[]
name|recordCounts
parameter_list|,
name|long
index|[]
name|compactionMapWeights
parameter_list|)
block|{
name|gcMonitor
operator|.
name|compacted
argument_list|(
name|segmentCounts
argument_list|,
name|recordCounts
argument_list|,
name|compactionMapWeights
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|cleaned
parameter_list|(
name|long
name|reclaimedSize
parameter_list|,
name|long
name|currentSize
parameter_list|)
block|{
name|gcMonitor
operator|.
name|cleaned
argument_list|(
name|reclaimedSize
argument_list|,
name|currentSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|compacted
parameter_list|(
annotation|@
name|Nonnull
name|Status
name|status
parameter_list|,
specifier|final
name|int
name|newGeneration
parameter_list|)
block|{
switch|switch
condition|(
name|status
condition|)
block|{
case|case
name|SUCCESS
case|:
comment|// FIXME OAK-4283: Align GCMonitor API with implementation
comment|// This call is still needed to ensure upstream consumers
comment|// of GCMonitor callback get properly notified. See
comment|// RepositoryImpl.RefreshOnGC and
comment|// LuceneIndexProviderService.registerGCMonitor().
name|gcMonitor
operator|.
name|compacted
argument_list|(
operator|new
name|long
index|[
literal|0
index|]
argument_list|,
operator|new
name|long
index|[
literal|0
index|]
argument_list|,
operator|new
name|long
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|evictCaches
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Integer
name|generation
parameter_list|)
block|{
return|return
name|generation
operator|<
name|newGeneration
return|;
block|}
block|}
argument_list|)
expr_stmt|;
break|break;
case|case
name|FAILURE
case|:
name|evictCaches
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Integer
name|generation
parameter_list|)
block|{
return|return
name|generation
operator|==
name|newGeneration
return|;
block|}
block|}
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
expr_stmt|;
block|}
block|}
decl_stmt|;
annotation|@
name|CheckForNull
specifier|private
name|TarRevisions
name|revisions
decl_stmt|;
comment|/**      * Create a new instance of a {@code FileStoreBuilder} for a file store.      * @param directory  directory where the tar files are stored      * @return a new {@code FileStoreBuilder} instance.      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|FileStoreBuilder
name|fileStoreBuilder
parameter_list|(
annotation|@
name|Nonnull
name|File
name|directory
parameter_list|)
block|{
return|return
operator|new
name|FileStoreBuilder
argument_list|(
name|directory
argument_list|)
return|;
block|}
specifier|private
name|FileStoreBuilder
parameter_list|(
annotation|@
name|Nonnull
name|File
name|directory
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|checkNotNull
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
comment|/**      * Specify the {@link BlobStore}.      * @param blobStore      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FileStoreBuilder
name|withBlobStore
parameter_list|(
annotation|@
name|Nonnull
name|BlobStore
name|blobStore
parameter_list|)
block|{
name|this
operator|.
name|blobStore
operator|=
name|checkNotNull
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Maximal size of the generated tar files in MB.      * @param maxFileSize      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FileStoreBuilder
name|withMaxFileSize
parameter_list|(
name|int
name|maxFileSize
parameter_list|)
block|{
name|this
operator|.
name|maxFileSize
operator|=
name|maxFileSize
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Size of the cache in MB.      * @param cacheSize      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FileStoreBuilder
name|withCacheSize
parameter_list|(
name|int
name|cacheSize
parameter_list|)
block|{
name|this
operator|.
name|cacheSize
operator|=
name|cacheSize
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Turn caching off      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FileStoreBuilder
name|withNoCache
parameter_list|()
block|{
name|this
operator|.
name|cacheSize
operator|=
operator|-
literal|1
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Turn memory mapping on or off      * @param memoryMapping      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FileStoreBuilder
name|withMemoryMapping
parameter_list|(
name|boolean
name|memoryMapping
parameter_list|)
block|{
name|this
operator|.
name|memoryMapping
operator|=
name|memoryMapping
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set memory mapping to the default value based on OS properties      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FileStoreBuilder
name|withDefaultMemoryMapping
parameter_list|()
block|{
name|this
operator|.
name|memoryMapping
operator|=
name|FileStore
operator|.
name|MEMORY_MAPPING_DEFAULT
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * {@link GCMonitor} for monitoring this files store's gc process.      * @param gcMonitor      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FileStoreBuilder
name|withGCMonitor
parameter_list|(
annotation|@
name|Nonnull
name|GCMonitor
name|gcMonitor
parameter_list|)
block|{
name|this
operator|.
name|gcMonitor
operator|.
name|registerGCMonitor
argument_list|(
name|checkNotNull
argument_list|(
name|gcMonitor
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * {@link StatisticsProvider} for collecting statistics related to FileStore      * @param statisticsProvider      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FileStoreBuilder
name|withStatisticsProvider
parameter_list|(
annotation|@
name|Nonnull
name|StatisticsProvider
name|statisticsProvider
parameter_list|)
block|{
name|this
operator|.
name|statsProvider
operator|=
name|checkNotNull
argument_list|(
name|statisticsProvider
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * {@link SegmentGCOptions} the garbage collection options of the store      * @param gcOptions      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FileStoreBuilder
name|withGCOptions
parameter_list|(
name|SegmentGCOptions
name|gcOptions
parameter_list|)
block|{
name|this
operator|.
name|gcOptions
operator|=
name|checkNotNull
argument_list|(
name|gcOptions
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Create a new {@link FileStore} instance with the settings specified in this      * builder. If none of the {@code with} methods have been called before calling      * this method, a file store with the following default settings is returned:      *<ul>      *<li>blob store: inline</li>      *<li>max file size: 256MB</li>      *<li>cache size: 256MB</li>      *<li>memory mapping: on for 64 bit JVMs off otherwise</li>      *<li>whiteboard: none. No {@link GCMonitor} tracking</li>      *<li>statsProvider: {@link StatisticsProvider#NOOP}</li>      *<li>GC options: {@link SegmentGCOptions#DEFAULT}</li>      *</ul>      *      * @return a new file store instance      * @throws IOException      */
annotation|@
name|Nonnull
specifier|public
name|FileStore
name|build
parameter_list|()
throws|throws
name|IOException
block|{
name|directory
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|revisions
operator|=
operator|new
name|TarRevisions
argument_list|(
literal|false
argument_list|,
name|directory
argument_list|)
expr_stmt|;
name|FileStore
name|store
init|=
operator|new
name|FileStore
argument_list|(
name|this
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|revisions
operator|.
name|bind
argument_list|(
name|store
argument_list|,
name|initialNode
argument_list|(
name|store
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|store
return|;
block|}
comment|/**      * Create a new {@link ReadOnlyStore} instance with the settings specified in this      * builder. If none of the {@code with} methods have been called before calling      * this method, a file store with the following default settings is returned:      *<ul>      *<li>blob store: inline</li>      *<li>max file size: 256MB</li>      *<li>cache size: 256MB</li>      *<li>memory mapping: on for 64 bit JVMs off otherwise</li>      *<li>whiteboard: none. No {@link GCMonitor} tracking</li>      *<li>statsProvider: {@link StatisticsProvider#NOOP}</li>      *<li>GC options: {@link SegmentGCOptions#DEFAULT}</li>      *</ul>      *      * @return a new file store instance      * @throws IOException      */
annotation|@
name|Nonnull
specifier|public
name|ReadOnlyStore
name|buildReadOnly
parameter_list|()
throws|throws
name|IOException
block|{
name|checkState
argument_list|(
name|directory
operator|.
name|exists
argument_list|()
operator|&&
name|directory
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|revisions
operator|=
operator|new
name|TarRevisions
argument_list|(
literal|true
argument_list|,
name|directory
argument_list|)
expr_stmt|;
name|ReadOnlyStore
name|store
init|=
operator|new
name|ReadOnlyStore
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|revisions
operator|.
name|bind
argument_list|(
name|store
argument_list|,
name|initialNode
argument_list|(
name|store
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|store
return|;
block|}
annotation|@
name|Nonnull
specifier|private
specifier|static
name|Supplier
argument_list|<
name|RecordId
argument_list|>
name|initialNode
parameter_list|(
specifier|final
name|FileStore
name|store
parameter_list|)
block|{
return|return
operator|new
name|Supplier
argument_list|<
name|RecordId
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RecordId
name|get
parameter_list|()
block|{
try|try
block|{
name|SegmentWriter
name|writer
init|=
name|segmentWriterBuilder
argument_list|(
literal|"init"
argument_list|)
operator|.
name|build
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"root"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|node
operator|.
name|getRecordId
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Failed to write initial node"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Nonnull
name|File
name|getDirectory
parameter_list|()
block|{
return|return
name|directory
return|;
block|}
annotation|@
name|CheckForNull
name|BlobStore
name|getBlobStore
parameter_list|()
block|{
return|return
name|blobStore
return|;
block|}
specifier|public
name|int
name|getMaxFileSize
parameter_list|()
block|{
return|return
name|maxFileSize
return|;
block|}
name|int
name|getCacheSize
parameter_list|()
block|{
return|return
name|cacheSize
return|;
block|}
name|boolean
name|getMemoryMapping
parameter_list|()
block|{
return|return
name|memoryMapping
return|;
block|}
annotation|@
name|Nonnull
name|GCListener
name|getGcListener
parameter_list|()
block|{
return|return
name|gcListener
return|;
block|}
annotation|@
name|Nonnull
name|StatisticsProvider
name|getStatsProvider
parameter_list|()
block|{
return|return
name|statsProvider
return|;
block|}
annotation|@
name|Nonnull
name|SegmentGCOptions
name|getGcOptions
parameter_list|()
block|{
return|return
name|gcOptions
return|;
block|}
annotation|@
name|Nonnull
name|TarRevisions
name|getRevisions
parameter_list|()
block|{
name|checkState
argument_list|(
name|revisions
operator|!=
literal|null
argument_list|,
literal|"File store not yet built"
argument_list|)
expr_stmt|;
return|return
name|revisions
return|;
block|}
annotation|@
name|Nonnull
name|WriterCacheManager
name|getCacheManager
parameter_list|()
block|{
return|return
name|cacheManager
return|;
block|}
block|}
end_class

end_unit

