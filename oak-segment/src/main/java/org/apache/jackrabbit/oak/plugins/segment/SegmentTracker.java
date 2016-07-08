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
name|collect
operator|.
name|Queues
operator|.
name|newArrayDeque
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
name|Boolean
operator|.
name|getBoolean
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
name|Queue
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|plugins
operator|.
name|blob
operator|.
name|ReferenceCollector
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
name|segment
operator|.
name|compaction
operator|.
name|CompactionStrategy
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
comment|/**      * Disable the {@link #stringCache} if {@code true} and fall back to      * the previous {@link Segment#strings} caching mechanism.      */
specifier|private
specifier|static
specifier|final
name|boolean
name|DISABLE_STRING_CACHE
init|=
name|getBoolean
argument_list|(
literal|"oak.segment.disableStringCache"
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
comment|/**      * Serialized map that contains the link between old record      * identifiers and identifiers of the corresponding records      * after compaction.      */
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|CompactionMap
argument_list|>
name|compactionMap
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
comment|/**      * Number of segments      */
specifier|private
specifier|final
name|AtomicInteger
name|segmentCounter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SegmentVersion
name|segmentVersion
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
name|this
operator|.
name|segmentVersion
operator|=
name|version
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
name|compactionMap
operator|=
operator|new
name|AtomicReference
argument_list|<
name|CompactionMap
argument_list|>
argument_list|(
name|CompactionMap
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|createSegmentWriter
argument_list|(
literal|"sys"
argument_list|)
expr_stmt|;
name|StringCache
name|c
decl_stmt|;
if|if
condition|(
name|DISABLE_STRING_CACHE
condition|)
block|{
name|c
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|long
name|cache
init|=
name|Long
operator|.
name|getLong
argument_list|(
name|STRING_CACHE_SIZE
argument_list|,
operator|(
name|long
operator|)
name|cacheSizeMB
argument_list|)
decl_stmt|;
name|c
operator|=
operator|new
name|StringCache
argument_list|(
name|cache
operator|*
name|MB
argument_list|)
expr_stmt|;
block|}
name|stringCache
operator|=
name|c
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
name|V_11
argument_list|)
expr_stmt|;
block|}
comment|/**      * Increment and get the number of segments      * @return      */
name|int
name|getNextSegmentNo
parameter_list|()
block|{
return|return
name|segmentCounter
operator|.
name|incrementAndGet
argument_list|()
return|;
block|}
comment|/**      * @return  a new {@link SegmentWriter} instance for writing to this store.      */
specifier|public
specifier|final
name|SegmentWriter
name|createSegmentWriter
parameter_list|(
name|String
name|wid
parameter_list|)
block|{
return|return
operator|new
name|SegmentWriter
argument_list|(
name|store
argument_list|,
name|segmentVersion
argument_list|,
name|wid
argument_list|)
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
operator|==
literal|null
condition|?
literal|null
else|:
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
if|if
condition|(
name|stringCache
operator|!=
literal|null
condition|)
block|{
name|stringCache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
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
specifier|public
name|void
name|setCompactionMap
parameter_list|(
name|PartialCompactionMap
name|map
parameter_list|)
block|{
name|compactionMap
operator|.
name|set
argument_list|(
name|compactionMap
operator|.
name|get
argument_list|()
operator|.
name|cons
argument_list|(
name|map
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
name|CompactionMap
name|getCompactionMap
parameter_list|()
block|{
return|return
name|compactionMap
operator|.
name|get
argument_list|()
return|;
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
comment|/**      * Finds all external blob references that are currently accessible      * in this repository and adds them to the given collector. Useful      * for collecting garbage in an external data store.      *<p>      * Note that this method only collects blob references that are already      * stored in the repository (at the time when this method is called), so      * the garbage collector will need some other mechanism for tracking      * in-memory references and references stored while this method is      * running.      */
specifier|public
name|void
name|collectBlobReferences
parameter_list|(
name|ReferenceCollector
name|collector
parameter_list|)
block|{
try|try
block|{
name|Set
argument_list|<
name|UUID
argument_list|>
name|processed
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|SegmentId
name|sid
range|:
name|getReferencedSegmentIds
argument_list|()
control|)
block|{
if|if
condition|(
name|sid
operator|.
name|isDataSegmentId
argument_list|()
condition|)
block|{
name|processed
operator|.
name|add
argument_list|(
name|sid
operator|.
name|asUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Queue
argument_list|<
name|UUID
argument_list|>
name|queue
init|=
name|newArrayDeque
argument_list|(
name|processed
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// force the current segment to have root record info
while|while
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|UUID
name|uid
init|=
name|queue
operator|.
name|remove
argument_list|()
decl_stmt|;
name|SegmentId
name|id
init|=
name|getSegmentId
argument_list|(
name|uid
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|uid
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
decl_stmt|;
name|Segment
name|segment
init|=
literal|null
decl_stmt|;
try|try
block|{
name|segment
operator|=
name|id
operator|.
name|getSegment
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SegmentNotFoundException
name|ex
parameter_list|)
block|{
comment|// gc'ed
block|}
if|if
condition|(
name|segment
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|segment
operator|.
name|collectBlobReferences
argument_list|(
name|collector
argument_list|)
expr_stmt|;
for|for
control|(
name|SegmentId
name|refid
range|:
name|segment
operator|.
name|getReferencedIds
argument_list|()
control|)
block|{
name|UUID
name|rid
init|=
name|refid
operator|.
name|asUUID
argument_list|()
decl_stmt|;
if|if
condition|(
name|refid
operator|.
name|isDataSegmentId
argument_list|()
operator|&&
operator|!
name|processed
operator|.
name|contains
argument_list|(
name|rid
argument_list|)
condition|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|rid
argument_list|)
expr_stmt|;
name|processed
operator|.
name|add
argument_list|(
name|rid
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error while flushing pending segments"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unexpected IOException"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
specifier|public
specifier|synchronized
name|void
name|clearSegmentIdTables
parameter_list|(
name|CompactionStrategy
name|strategy
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
name|strategy
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

