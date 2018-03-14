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
name|checkNotNull
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
name|CacheWeights
operator|.
name|segmentWeight
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
name|Callable
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
name|AtomicLong
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
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
name|cache
operator|.
name|Cache
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
name|CacheBuilder
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
name|CacheStats
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
name|RemovalNotification
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
name|AbstractCacheStats
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
name|SegmentCacheWeigher
import|;
end_import

begin_comment
comment|/**  * A cache for {@link SegmentId#isDataSegmentId() data} {@link Segment}  * instances by their {@link SegmentId}. This cache ignores {@link  * SegmentId#isBulkSegmentId() bulk} segments.  *<p>  * Conceptually this cache serves as a 2nd level cache for segments. The 1st  * level cache is implemented by memoising the segment in its id (see {@link  * SegmentId#segment}. Every time an segment is evicted from this cache the  * memoised segment is discarded (see {@link SegmentId#onAccess}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SegmentCache
block|{
comment|/**      * Default maximum weight of this cache in MB      */
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SEGMENT_CACHE_MB
init|=
literal|256
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"Segment Cache"
decl_stmt|;
comment|/**      * Create a new segment cache of the given size. Returns an always empty      * cache for {@code cacheSizeMB<= 0}.      *      * @param cacheSizeMB size of the cache in megabytes.      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|SegmentCache
name|newSegmentCache
parameter_list|(
name|long
name|cacheSizeMB
parameter_list|)
block|{
if|if
condition|(
name|cacheSizeMB
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|NonEmptyCache
argument_list|(
name|cacheSizeMB
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|EmptyCache
argument_list|()
return|;
block|}
block|}
comment|/**      * Retrieve an segment from the cache or load it and cache it if not yet in      * the cache.      *      * @param id     the id of the segment      * @param loader the loader to load the segment if not yet in the cache      * @return the segment identified by {@code id}      * @throws ExecutionException when {@code loader} failed to load an segment      */
annotation|@
name|Nonnull
specifier|public
specifier|abstract
name|Segment
name|getSegment
parameter_list|(
annotation|@
name|Nonnull
name|SegmentId
name|id
parameter_list|,
annotation|@
name|Nonnull
name|Callable
argument_list|<
name|Segment
argument_list|>
name|loader
parameter_list|)
throws|throws
name|ExecutionException
function_decl|;
comment|/**      * Put a segment into the cache. This method does nothing for {@link      * SegmentId#isBulkSegmentId() bulk} segments.      *      * @param segment the segment to cache      */
specifier|public
specifier|abstract
name|void
name|putSegment
parameter_list|(
annotation|@
name|Nonnull
name|Segment
name|segment
parameter_list|)
function_decl|;
comment|/**      * Clear all segment from the cache      */
specifier|public
specifier|abstract
name|void
name|clear
parameter_list|()
function_decl|;
comment|/**      * @return Statistics for this cache.      */
annotation|@
name|Nonnull
specifier|public
specifier|abstract
name|AbstractCacheStats
name|getCacheStats
parameter_list|()
function_decl|;
comment|/**      * Record a hit in this cache's underlying statistics.      *      * @see SegmentId#onAccess      */
specifier|public
specifier|abstract
name|void
name|recordHit
parameter_list|()
function_decl|;
specifier|private
specifier|static
class|class
name|NonEmptyCache
extends|extends
name|SegmentCache
block|{
comment|/**          * Cache of recently accessed segments          */
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Cache
argument_list|<
name|SegmentId
argument_list|,
name|Segment
argument_list|>
name|cache
decl_stmt|;
comment|/**          * Statistics of this cache. Do to the special access patter (see class          * comment), we cannot rely on {@link Cache#stats()}.          */
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Stats
name|stats
decl_stmt|;
comment|/**          * Create a new cache of the given size.          *          * @param cacheSizeMB size of the cache in megabytes.          */
specifier|private
name|NonEmptyCache
parameter_list|(
name|long
name|cacheSizeMB
parameter_list|)
block|{
name|long
name|maximumWeight
init|=
name|cacheSizeMB
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
name|this
operator|.
name|cache
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|concurrencyLevel
argument_list|(
literal|16
argument_list|)
operator|.
name|maximumWeight
argument_list|(
name|maximumWeight
argument_list|)
operator|.
name|weigher
argument_list|(
operator|new
name|SegmentCacheWeigher
argument_list|()
argument_list|)
operator|.
name|removalListener
argument_list|(
name|this
operator|::
name|onRemove
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|stats
operator|=
operator|new
name|Stats
argument_list|(
name|NAME
argument_list|,
name|maximumWeight
argument_list|,
name|cache
operator|::
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**          * Removal handler called whenever an item is evicted from the cache.          */
specifier|private
name|void
name|onRemove
parameter_list|(
annotation|@
name|Nonnull
name|RemovalNotification
argument_list|<
name|SegmentId
argument_list|,
name|Segment
argument_list|>
name|notification
parameter_list|)
block|{
name|stats
operator|.
name|evictionCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|notification
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|stats
operator|.
name|currentWeight
operator|.
name|addAndGet
argument_list|(
operator|-
name|segmentWeight
argument_list|(
name|notification
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|notification
operator|.
name|getKey
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|notification
operator|.
name|getKey
argument_list|()
operator|.
name|unloaded
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Segment
name|getSegment
parameter_list|(
annotation|@
name|Nonnull
name|SegmentId
name|id
parameter_list|,
annotation|@
name|Nonnull
name|Callable
argument_list|<
name|Segment
argument_list|>
name|loader
parameter_list|)
throws|throws
name|ExecutionException
block|{
if|if
condition|(
name|id
operator|.
name|isDataSegmentId
argument_list|()
condition|)
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
name|id
argument_list|,
parameter_list|()
lambda|->
block|{
try|try
block|{
name|long
name|t0
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|Segment
name|segment
init|=
name|loader
operator|.
name|call
argument_list|()
decl_stmt|;
name|stats
operator|.
name|loadSuccessCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|stats
operator|.
name|loadTime
operator|.
name|addAndGet
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|t0
argument_list|)
expr_stmt|;
name|stats
operator|.
name|missCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|stats
operator|.
name|currentWeight
operator|.
name|addAndGet
argument_list|(
name|segmentWeight
argument_list|(
name|segment
argument_list|)
argument_list|)
expr_stmt|;
name|id
operator|.
name|loaded
argument_list|(
name|segment
argument_list|)
expr_stmt|;
return|return
name|segment
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|stats
operator|.
name|loadExceptionCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
argument_list|)
return|;
block|}
else|else
block|{
try|try
block|{
return|return
name|loader
operator|.
name|call
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ExecutionException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|putSegment
parameter_list|(
annotation|@
name|Nonnull
name|Segment
name|segment
parameter_list|)
block|{
name|SegmentId
name|id
init|=
name|segment
operator|.
name|getSegmentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|isDataSegmentId
argument_list|()
condition|)
block|{
comment|// Putting the segment into the cache can cause it to be evicted
comment|// right away again. Therefore we need to call loaded and update
comment|// the current weight *before* putting the segment into the cache.
comment|// This ensures that the eviction call back is always called
comment|// *after* a call to loaded and that the current weight is only
comment|// decremented *after* it was incremented.
name|id
operator|.
name|loaded
argument_list|(
name|segment
argument_list|)
expr_stmt|;
name|stats
operator|.
name|currentWeight
operator|.
name|addAndGet
argument_list|(
name|segmentWeight
argument_list|(
name|segment
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|segment
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|cache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|AbstractCacheStats
name|getCacheStats
parameter_list|()
block|{
return|return
name|stats
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|recordHit
parameter_list|()
block|{
name|stats
operator|.
name|hitCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** An always empty cache */
specifier|private
specifier|static
class|class
name|EmptyCache
extends|extends
name|SegmentCache
block|{
specifier|private
specifier|final
name|Stats
name|stats
init|=
operator|new
name|Stats
argument_list|(
name|NAME
argument_list|,
literal|0
argument_list|,
parameter_list|()
lambda|->
literal|0L
argument_list|)
decl_stmt|;
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Segment
name|getSegment
parameter_list|(
annotation|@
name|Nonnull
name|SegmentId
name|id
parameter_list|,
annotation|@
name|Nonnull
name|Callable
argument_list|<
name|Segment
argument_list|>
name|loader
parameter_list|)
throws|throws
name|ExecutionException
block|{
name|long
name|t0
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
try|try
block|{
name|stats
operator|.
name|missCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|Segment
name|segment
init|=
name|loader
operator|.
name|call
argument_list|()
decl_stmt|;
name|stats
operator|.
name|loadSuccessCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
name|segment
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|stats
operator|.
name|loadExceptionCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|ExecutionException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|stats
operator|.
name|loadTime
operator|.
name|addAndGet
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|t0
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|putSegment
parameter_list|(
annotation|@
name|Nonnull
name|Segment
name|segment
parameter_list|)
block|{
name|segment
operator|.
name|getSegmentId
argument_list|()
operator|.
name|unloaded
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|()
block|{}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|AbstractCacheStats
name|getCacheStats
parameter_list|()
block|{
return|return
name|stats
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|recordHit
parameter_list|()
block|{
name|stats
operator|.
name|hitCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * We cannot rely on the statistics of the underlying Guava cache as all      * cache hits are taken by {@link SegmentId#getSegment()} and thus never      * seen by the cache.      */
specifier|private
specifier|static
class|class
name|Stats
extends|extends
name|AbstractCacheStats
block|{
specifier|private
specifier|final
name|long
name|maximumWeight
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Supplier
argument_list|<
name|Long
argument_list|>
name|elementCount
decl_stmt|;
annotation|@
name|Nonnull
specifier|final
name|AtomicLong
name|currentWeight
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
annotation|@
name|Nonnull
specifier|final
name|AtomicLong
name|loadSuccessCount
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
annotation|@
name|Nonnull
specifier|final
name|AtomicInteger
name|loadExceptionCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
annotation|@
name|Nonnull
specifier|final
name|AtomicLong
name|loadTime
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
annotation|@
name|Nonnull
specifier|final
name|AtomicLong
name|evictionCount
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
annotation|@
name|Nonnull
specifier|final
name|AtomicLong
name|hitCount
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
annotation|@
name|Nonnull
specifier|final
name|AtomicLong
name|missCount
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|protected
name|Stats
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
name|long
name|maximumWeight
parameter_list|,
annotation|@
name|Nonnull
name|Supplier
argument_list|<
name|Long
argument_list|>
name|elementCount
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|maximumWeight
operator|=
name|maximumWeight
expr_stmt|;
name|this
operator|.
name|elementCount
operator|=
name|checkNotNull
argument_list|(
name|elementCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|CacheStats
name|getCurrentStats
parameter_list|()
block|{
return|return
operator|new
name|CacheStats
argument_list|(
name|hitCount
operator|.
name|get
argument_list|()
argument_list|,
name|missCount
operator|.
name|get
argument_list|()
argument_list|,
name|loadSuccessCount
operator|.
name|get
argument_list|()
argument_list|,
name|loadExceptionCount
operator|.
name|get
argument_list|()
argument_list|,
name|loadTime
operator|.
name|get
argument_list|()
argument_list|,
name|evictionCount
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getElementCount
parameter_list|()
block|{
return|return
name|elementCount
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getMaxTotalWeight
parameter_list|()
block|{
return|return
name|maximumWeight
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|estimateCurrentWeight
parameter_list|()
block|{
return|return
name|currentWeight
operator|.
name|get
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

