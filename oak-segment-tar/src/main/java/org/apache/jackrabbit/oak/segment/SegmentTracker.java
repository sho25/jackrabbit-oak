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
name|checkArgument
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
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Long
operator|.
name|getLong
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandom
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
name|concurrent
operator|.
name|ExecutionException
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
name|atomic
operator|.
name|AtomicInteger
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
name|cache
operator|.
name|RemovalCause
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
name|cache
operator|.
name|CacheLIRS
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
name|cache
operator|.
name|CacheLIRS
operator|.
name|EvictionCallback
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
name|cache
operator|.
name|CacheStats
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
comment|/**  * Tracker of references to segment identifiers and segment instances  * that are currently kept in memory.  *<p>  * It is also responsible to cache segment objects in memory.  */
end_comment

begin_class
specifier|public
class|class
name|SegmentTracker
block|{
comment|/** Logger instance */
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
name|SegmentTracker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|String
name|STRING_CACHE_SIZE
init|=
literal|"oak.segment.stringCache"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|MSB_MASK
init|=
operator|~
operator|(
literal|0xfL
operator|<<
literal|12
operator|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|VERSION
init|=
operator|(
literal|0x4L
operator|<<
literal|12
operator|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|LSB_MASK
init|=
operator|~
operator|(
literal|0xfL
operator|<<
literal|60
operator|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|DATA
init|=
literal|0xAL
operator|<<
literal|60
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|BULK
init|=
literal|0xBL
operator|<<
literal|60
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_MEMORY_CACHE_SIZE
init|=
literal|256
decl_stmt|;
comment|/**      * The random number source for generating new segment identifiers.      */
specifier|private
specifier|final
name|SecureRandom
name|random
init|=
operator|new
name|SecureRandom
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SegmentStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|SegmentWriter
name|writer
decl_stmt|;
comment|/**      * Hash table of weak references to segment identifiers that are      * currently being accessed. The size of the table is always a power      * of two, which optimizes the {@code refresh()} operation. The table is      * indexed by the random identifier bits, which guarantees uniform      * distribution of entries. Each table entry is either {@code null}      * (when there are no matching identifiers) or a list of weak references      * to the matching identifiers.      */
specifier|private
specifier|final
name|SegmentIdTable
index|[]
name|tables
init|=
operator|new
name|SegmentIdTable
index|[
literal|32
index|]
decl_stmt|;
comment|/**      * Cache for string records      */
specifier|private
specifier|final
name|StringCache
name|stringCache
decl_stmt|;
comment|/**      * Cache of recently accessed segments      */
specifier|private
specifier|final
name|CacheLIRS
argument_list|<
name|SegmentId
argument_list|,
name|Segment
argument_list|>
name|segmentCache
decl_stmt|;
comment|/**      * Number of segment tracked since this tracker was instantiated      */
specifier|private
specifier|final
name|AtomicInteger
name|segmentCounter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|public
name|SegmentTracker
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|int
name|cacheSizeMB
parameter_list|,
name|SegmentVersion
name|version
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|SegmentVersion
operator|.
name|isValid
argument_list|(
name|version
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|tables
index|[
name|i
index|]
operator|=
operator|new
name|SegmentIdTable
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|writer
operator|=
operator|new
name|SegmentWriter
argument_list|(
name|store
argument_list|,
name|version
argument_list|,
operator|new
name|SegmentBufferWriterPool
argument_list|(
name|store
argument_list|,
name|version
argument_list|,
literal|"sys"
argument_list|)
argument_list|)
expr_stmt|;
name|stringCache
operator|=
operator|new
name|StringCache
argument_list|(
name|getLong
argument_list|(
name|STRING_CACHE_SIZE
argument_list|,
operator|(
name|long
operator|)
name|cacheSizeMB
argument_list|)
operator|*
name|MB
argument_list|)
expr_stmt|;
name|segmentCache
operator|=
name|CacheLIRS
operator|.
expr|<
name|SegmentId
operator|,
name|Segment
operator|>
name|newBuilder
argument_list|()
operator|.
name|module
argument_list|(
literal|"SegmentTracker"
argument_list|)
operator|.
name|maximumWeight
argument_list|(
operator|(
name|long
operator|)
name|cacheSizeMB
operator|*
name|MB
argument_list|)
operator|.
name|averageWeight
argument_list|(
name|Segment
operator|.
name|MAX_SEGMENT_SIZE
operator|/
literal|2
argument_list|)
operator|.
name|evictionCallback
argument_list|(
operator|new
name|EvictionCallback
argument_list|<
name|SegmentId
argument_list|,
name|Segment
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|evicted
parameter_list|(
name|SegmentId
name|segmentId
parameter_list|,
name|Segment
name|segment
parameter_list|,
name|RemovalCause
name|cause
parameter_list|)
block|{
if|if
condition|(
name|segment
operator|!=
literal|null
condition|)
block|{
name|segmentId
operator|.
name|setSegment
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
specifier|public
name|SegmentTracker
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|SegmentVersion
name|version
parameter_list|)
block|{
name|this
argument_list|(
name|store
argument_list|,
name|DEFAULT_MEMORY_CACHE_SIZE
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SegmentTracker
parameter_list|(
name|SegmentStore
name|store
parameter_list|)
block|{
name|this
argument_list|(
name|store
argument_list|,
name|DEFAULT_MEMORY_CACHE_SIZE
argument_list|,
name|SegmentVersion
operator|.
name|LATEST_VERSION
argument_list|)
expr_stmt|;
block|}
comment|/**      * Number of segment tracked since this tracker was instantiated      * @return count      */
name|int
name|getSegmentCount
parameter_list|()
block|{
return|return
name|segmentCounter
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isTracking
parameter_list|(
name|SegmentId
name|segmentId
parameter_list|)
block|{
return|return
name|this
operator|==
name|segmentId
operator|.
name|getTracker
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|CacheStats
name|getSegmentCacheStats
parameter_list|()
block|{
return|return
operator|new
name|CacheStats
argument_list|(
name|segmentCache
argument_list|,
literal|"Segment Cache"
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|CacheStats
name|getStringCacheStats
parameter_list|()
block|{
return|return
name|stringCache
operator|.
name|getStats
argument_list|()
return|;
block|}
specifier|public
name|SegmentWriter
name|getWriter
parameter_list|()
block|{
return|return
name|writer
return|;
block|}
specifier|public
name|SegmentStore
name|getStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
comment|/**      * Clear the caches      */
specifier|public
specifier|synchronized
name|void
name|clearCache
parameter_list|()
block|{
name|segmentCache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
name|stringCache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * Get the string cache, if there is one.      *      * @return the string cache or {@code null} if none is configured      */
name|StringCache
name|getStringCache
parameter_list|()
block|{
return|return
name|stringCache
return|;
block|}
comment|/**      * Get a segment from the cache      * @param id  segment id      * @return  segment with the given {@code id} or {@code null} if not in the cache      */
name|Segment
name|getCachedSegment
parameter_list|(
name|SegmentId
name|id
parameter_list|)
block|{
try|try
block|{
return|return
name|segmentCache
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error reading from segment cache"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Read a segment from the underlying segment store.      * @param id  segment id      * @return  segment with the given id      * @throws SegmentNotFoundException  if no segment with the given {@code id} exists.      */
name|Segment
name|readSegment
parameter_list|(
name|SegmentId
name|id
parameter_list|)
block|{
try|try
block|{
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|setSegment
argument_list|(
name|id
argument_list|,
name|segment
argument_list|)
expr_stmt|;
return|return
name|segment
return|;
block|}
catch|catch
parameter_list|(
name|SegmentNotFoundException
name|snfe
parameter_list|)
block|{
name|long
name|delta
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|id
operator|.
name|getCreationTime
argument_list|()
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"Segment not found: {}. Creation date delta is {} ms."
argument_list|,
name|id
argument_list|,
name|delta
argument_list|,
name|snfe
argument_list|)
expr_stmt|;
throw|throw
name|snfe
throw|;
block|}
block|}
name|void
name|setSegment
parameter_list|(
name|SegmentId
name|id
parameter_list|,
name|Segment
name|segment
parameter_list|)
block|{
name|id
operator|.
name|setSegment
argument_list|(
name|segment
argument_list|)
expr_stmt|;
name|segmentCache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|segment
argument_list|,
name|segment
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// FIXME OAK-4102: Break cyclic dependency of FileStore and SegmentTracker
comment|// Improve retrieving current GC generation. (OAK-4102)
comment|// See also the comments in FileStore regarding initialisation and cyclic dependencies.
specifier|public
name|int
name|getGcGeneration
parameter_list|()
block|{
if|if
condition|(
name|store
operator|instanceof
name|FileStore
condition|)
block|{
return|return
operator|(
operator|(
name|FileStore
operator|)
name|store
operator|)
operator|.
name|getGcGeneration
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/**      * Returns all segment identifiers that are currently referenced in memory.      *      * @return referenced segment identifiers      */
specifier|public
specifier|synchronized
name|Set
argument_list|<
name|SegmentId
argument_list|>
name|getReferencedSegmentIds
parameter_list|()
block|{
name|Set
argument_list|<
name|SegmentId
argument_list|>
name|ids
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|SegmentIdTable
name|table
range|:
name|tables
control|)
block|{
name|table
operator|.
name|collectReferencedIds
argument_list|(
name|ids
argument_list|)
expr_stmt|;
block|}
return|return
name|ids
return|;
block|}
comment|/**      *       * @param msb      * @param lsb      * @return the segment id      */
specifier|public
name|SegmentId
name|getSegmentId
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
block|{
name|int
name|index
init|=
operator|(
operator|(
name|int
operator|)
name|msb
operator|)
operator|&
operator|(
name|tables
operator|.
name|length
operator|-
literal|1
operator|)
decl_stmt|;
return|return
name|tables
index|[
name|index
index|]
operator|.
name|getSegmentId
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
return|;
block|}
name|SegmentId
name|newDataSegmentId
parameter_list|()
block|{
return|return
name|newSegmentId
argument_list|(
name|DATA
argument_list|)
return|;
block|}
name|SegmentId
name|newBulkSegmentId
parameter_list|()
block|{
return|return
name|newSegmentId
argument_list|(
name|BULK
argument_list|)
return|;
block|}
specifier|private
name|SegmentId
name|newSegmentId
parameter_list|(
name|long
name|type
parameter_list|)
block|{
name|segmentCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|long
name|msb
init|=
operator|(
name|random
operator|.
name|nextLong
argument_list|()
operator|&
name|MSB_MASK
operator|)
operator||
name|VERSION
decl_stmt|;
name|long
name|lsb
init|=
operator|(
name|random
operator|.
name|nextLong
argument_list|()
operator|&
name|LSB_MASK
operator|)
operator||
name|type
decl_stmt|;
return|return
name|getSegmentId
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
return|;
block|}
comment|// FIXME OAK-4285: Align cleanup of segment id tables with the new cleanup strategy
comment|// ith clean brutal we need to remove those ids that have been cleaned
comment|// i.e. those whose segment was from an old generation
specifier|public
specifier|synchronized
name|void
name|clearSegmentIdTables
parameter_list|(
name|Predicate
argument_list|<
name|SegmentId
argument_list|>
name|canRemove
parameter_list|)
block|{
for|for
control|(
name|SegmentIdTable
name|table
range|:
name|tables
control|)
block|{
name|table
operator|.
name|clearSegmentIdTables
argument_list|(
name|canRemove
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

