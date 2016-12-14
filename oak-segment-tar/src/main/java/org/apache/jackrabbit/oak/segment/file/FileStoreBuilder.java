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
operator|.
name|DEFAULT_STRING_CACHE_MB
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
name|CachingSegmentReader
operator|.
name|DEFAULT_TEMPLATE_CACHE_MB
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
name|SegmentCache
operator|.
name|DEFAULT_SEGMENT_CACHE_MB
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
name|SegmentNotFoundExceptionListener
operator|.
name|LOG_SNFE
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
name|WriterCacheManager
operator|.
name|DEFAULT_NODE_CACHE_SIZE
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
name|WriterCacheManager
operator|.
name|DEFAULT_STRING_CACHE_SIZE
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
name|WriterCacheManager
operator|.
name|DEFAULT_TEMPLATE_CACHE_SIZE
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
name|CacheWeights
operator|.
name|NodeCacheWeigher
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
name|CacheWeights
operator|.
name|StringCacheWeigher
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
name|CacheWeights
operator|.
name|TemplateCacheWeigher
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
name|RecordCache
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
name|SegmentNotFoundExceptionListener
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
specifier|private
specifier|static
specifier|final
name|boolean
name|MEMORY_MAPPING_DEFAULT
init|=
literal|"64"
operator|.
name|equals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"sun.arch.data.model"
argument_list|,
literal|"32"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_FILE_SIZE
init|=
literal|256
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
name|DEFAULT_MAX_FILE_SIZE
decl_stmt|;
specifier|private
name|int
name|segmentCacheSize
init|=
name|DEFAULT_SEGMENT_CACHE_MB
decl_stmt|;
specifier|private
name|int
name|stringCacheSize
init|=
name|DEFAULT_STRING_CACHE_MB
decl_stmt|;
specifier|private
name|int
name|templateCacheSize
init|=
name|DEFAULT_TEMPLATE_CACHE_MB
decl_stmt|;
specifier|private
name|int
name|stringDeduplicationCacheSize
init|=
name|DEFAULT_STRING_CACHE_SIZE
decl_stmt|;
specifier|private
name|int
name|templateDeduplicationCacheSize
init|=
name|DEFAULT_TEMPLATE_CACHE_SIZE
decl_stmt|;
specifier|private
name|int
name|nodeDeduplicationCacheSize
init|=
name|DEFAULT_NODE_CACHE_SIZE
decl_stmt|;
specifier|private
name|boolean
name|memoryMapping
init|=
name|MEMORY_MAPPING_DEFAULT
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
name|CheckForNull
specifier|private
name|EvictingWriteCacheManager
name|cacheManager
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|GCListener
name|gcListener
init|=
operator|new
name|GCListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|compactionSucceeded
parameter_list|(
name|int
name|newGeneration
parameter_list|)
block|{
name|compacted
argument_list|()
expr_stmt|;
name|cacheManager
operator|.
name|evictOldGeneration
argument_list|(
name|newGeneration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|compactionFailed
parameter_list|(
name|int
name|failedGeneration
parameter_list|)
block|{
name|cacheManager
operator|.
name|evictGeneration
argument_list|(
name|failedGeneration
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
name|SegmentNotFoundExceptionListener
name|snfeListener
init|=
name|LOG_SNFE
decl_stmt|;
specifier|private
name|boolean
name|built
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
name|this
operator|.
name|gcListener
operator|.
name|registerGCMonitor
argument_list|(
operator|new
name|LoggingGCMonitor
argument_list|(
name|LOG
argument_list|)
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
comment|/**      * Size of the segment cache in MB.      * @param segmentCacheSize  None negative cache size      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FileStoreBuilder
name|withSegmentCacheSize
parameter_list|(
name|int
name|segmentCacheSize
parameter_list|)
block|{
name|this
operator|.
name|segmentCacheSize
operator|=
name|segmentCacheSize
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Size of the string cache in MB.      * @param stringCacheSize  None negative cache size      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FileStoreBuilder
name|withStringCacheSize
parameter_list|(
name|int
name|stringCacheSize
parameter_list|)
block|{
name|this
operator|.
name|stringCacheSize
operator|=
name|stringCacheSize
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Size of the template cache in MB.      * @param templateCacheSize  None negative cache size      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FileStoreBuilder
name|withTemplateCacheSize
parameter_list|(
name|int
name|templateCacheSize
parameter_list|)
block|{
name|this
operator|.
name|templateCacheSize
operator|=
name|templateCacheSize
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Number of items to keep in the string deduplication cache      * @param stringDeduplicationCacheSize  None negative cache size      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FileStoreBuilder
name|withStringDeduplicationCacheSize
parameter_list|(
name|int
name|stringDeduplicationCacheSize
parameter_list|)
block|{
name|this
operator|.
name|stringDeduplicationCacheSize
operator|=
name|stringDeduplicationCacheSize
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Number of items to keep in the template deduplication cache      * @param templateDeduplicationCacheSize  None negative cache size      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FileStoreBuilder
name|withTemplateDeduplicationCacheSize
parameter_list|(
name|int
name|templateDeduplicationCacheSize
parameter_list|)
block|{
name|this
operator|.
name|templateDeduplicationCacheSize
operator|=
name|templateDeduplicationCacheSize
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Number of items to keep in the node deduplication cache      * @param nodeDeduplicationCacheSize  None negative cache size. Must be a power of 2.      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FileStoreBuilder
name|withNodeDeduplicationCacheSize
parameter_list|(
name|int
name|nodeDeduplicationCacheSize
parameter_list|)
block|{
name|this
operator|.
name|nodeDeduplicationCacheSize
operator|=
name|nodeDeduplicationCacheSize
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
name|gcListener
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
comment|/**      * {@link SegmentNotFoundExceptionListener} listener for  {@code SegmentNotFoundException}      * @param snfeListener, the actual listener      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FileStoreBuilder
name|withSnfeListener
parameter_list|(
annotation|@
name|Nonnull
name|SegmentNotFoundExceptionListener
name|snfeListener
parameter_list|)
block|{
name|this
operator|.
name|snfeListener
operator|=
name|checkNotNull
argument_list|(
name|snfeListener
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Create a new {@link FileStore} instance with the settings specified in this      * builder. If none of the {@code with} methods have been called before calling      * this method, a file store with the following default settings is returned:      *<ul>      *<li>blob store: inline</li>      *<li>max file size: 256MB</li>      *<li>cache size: 256MB</li>      *<li>memory mapping: on for 64 bit JVMs off otherwise</li>      *<li>whiteboard: none. No {@link GCMonitor} tracking</li>      *<li>statsProvider: {@link StatisticsProvider#NOOP}</li>      *<li>GC options: {@link SegmentGCOptions#defaultGCOptions()}</li>      *</ul>      *      * @return a new file store instance      * @throws IOException      */
annotation|@
name|Nonnull
specifier|public
name|FileStore
name|build
parameter_list|()
throws|throws
name|InvalidFileStoreVersionException
throws|,
name|IOException
block|{
name|checkState
argument_list|(
operator|!
name|built
argument_list|,
literal|"Cannot re-use builder"
argument_list|)
expr_stmt|;
name|built
operator|=
literal|true
expr_stmt|;
name|directory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|TarRevisions
name|revisions
init|=
operator|new
name|TarRevisions
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating file store {}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
return|return
operator|new
name|FileStore
argument_list|(
name|this
argument_list|)
operator|.
name|bind
argument_list|(
name|revisions
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link ReadOnlyFileStore} instance with the settings specified in this      * builder. If none of the {@code with} methods have been called before calling      * this method, a file store with the following default settings is returned:      *<ul>      *<li>blob store: inline</li>      *<li>max file size: 256MB</li>      *<li>cache size: 256MB</li>      *<li>memory mapping: on for 64 bit JVMs off otherwise</li>      *<li>whiteboard: none. No {@link GCMonitor} tracking</li>      *<li>statsProvider: {@link StatisticsProvider#NOOP}</li>      *<li>GC options: {@link SegmentGCOptions#defaultGCOptions()}</li>      *</ul>      *      * @return a new file store instance      * @throws IOException      */
annotation|@
name|Nonnull
specifier|public
name|ReadOnlyFileStore
name|buildReadOnly
parameter_list|()
throws|throws
name|InvalidFileStoreVersionException
throws|,
name|IOException
block|{
name|checkState
argument_list|(
operator|!
name|built
argument_list|,
literal|"Cannot re-use builder"
argument_list|)
expr_stmt|;
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
name|built
operator|=
literal|true
expr_stmt|;
name|ReadOnlyRevisions
name|revisions
init|=
operator|new
name|ReadOnlyRevisions
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating file store {}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
return|return
operator|new
name|ReadOnlyFileStore
argument_list|(
name|this
argument_list|)
operator|.
name|bind
argument_list|(
name|revisions
argument_list|)
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
name|getSegmentCacheSize
parameter_list|()
block|{
return|return
name|segmentCacheSize
return|;
block|}
name|int
name|getStringCacheSize
parameter_list|()
block|{
return|return
name|stringCacheSize
return|;
block|}
name|int
name|getTemplateCacheSize
parameter_list|()
block|{
return|return
name|templateCacheSize
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
name|SegmentNotFoundExceptionListener
name|getSnfeListener
parameter_list|()
block|{
return|return
name|snfeListener
return|;
block|}
annotation|@
name|Nonnull
name|WriterCacheManager
name|getCacheManager
parameter_list|()
block|{
if|if
condition|(
name|cacheManager
operator|==
literal|null
condition|)
block|{
name|cacheManager
operator|=
operator|new
name|EvictingWriteCacheManager
argument_list|(
name|stringDeduplicationCacheSize
argument_list|,
name|templateDeduplicationCacheSize
argument_list|,
name|nodeDeduplicationCacheSize
argument_list|)
expr_stmt|;
block|}
return|return
name|cacheManager
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"FileStoreBuilder{"
operator|+
literal|"directory="
operator|+
name|directory
operator|+
literal|", blobStore="
operator|+
name|blobStore
operator|+
literal|", maxFileSize="
operator|+
name|maxFileSize
operator|+
literal|", segmentCacheSize="
operator|+
name|segmentCacheSize
operator|+
literal|", stringCacheSize="
operator|+
name|stringCacheSize
operator|+
literal|", templateCacheSize="
operator|+
name|templateCacheSize
operator|+
literal|", stringDeduplicationCacheSize="
operator|+
name|stringDeduplicationCacheSize
operator|+
literal|", templateDeduplicationCacheSize="
operator|+
name|templateDeduplicationCacheSize
operator|+
literal|", nodeDeduplicationCacheSize="
operator|+
name|nodeDeduplicationCacheSize
operator|+
literal|", memoryMapping="
operator|+
name|memoryMapping
operator|+
literal|", gcOptions="
operator|+
name|gcOptions
operator|+
literal|'}'
return|;
block|}
specifier|private
specifier|static
class|class
name|EvictingWriteCacheManager
extends|extends
name|WriterCacheManager
operator|.
name|Default
block|{
specifier|public
name|EvictingWriteCacheManager
parameter_list|(
name|int
name|stringCacheSize
parameter_list|,
name|int
name|templateCacheSize
parameter_list|,
name|int
name|nodeCacheSize
parameter_list|)
block|{
name|super
argument_list|(
name|RecordCache
operator|.
name|factory
argument_list|(
name|stringCacheSize
argument_list|,
operator|new
name|StringCacheWeigher
argument_list|()
argument_list|)
argument_list|,
name|RecordCache
operator|.
name|factory
argument_list|(
name|templateCacheSize
argument_list|,
operator|new
name|TemplateCacheWeigher
argument_list|()
argument_list|)
argument_list|,
name|PriorityCache
operator|.
name|factory
argument_list|(
name|nodeCacheSize
argument_list|,
operator|new
name|NodeCacheWeigher
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|void
name|evictOldGeneration
parameter_list|(
specifier|final
name|int
name|newGeneration
parameter_list|)
block|{
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
block|}
name|void
name|evictGeneration
parameter_list|(
specifier|final
name|int
name|newGeneration
parameter_list|)
block|{
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
block|}
block|}
block|}
end_class

end_unit

