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
name|RemovalNotification
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
name|Weigher
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
comment|/**  * A cache for {@link SegmentId#isDataSegmentId() data} {@link Segment} instances by their  * {@link SegmentId}. This cache ignores {@link SegmentId#isBulkSegmentId() bulk} segments.  *<p>  * Conceptually this cache serves as a 2nd level cache for segments. The 1st level cache is  * implemented by memoising the segment in its id (see {@link SegmentId#segment}. Every time  * an segment is evicted from this cache the memoised segment is discarded (see  * {@link SegmentId#unloaded()}) and {@link SegmentId#onAccess}.  */
end_comment

begin_class
specifier|public
class|class
name|SegmentCache
block|{
comment|/** Default maximum weight of this cache in MB */
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SEGMENT_CACHE_MB
init|=
literal|256
decl_stmt|;
comment|/** Weigher to determine the current weight of all items in this cache */
specifier|private
specifier|final
name|Weigher
argument_list|<
name|SegmentId
argument_list|,
name|Segment
argument_list|>
name|weigher
init|=
operator|new
name|SegmentCacheWeigher
argument_list|()
decl_stmt|;
comment|/** Maximum weight of the items in this cache */
specifier|private
specifier|final
name|long
name|maximumWeight
decl_stmt|;
comment|/** Cache of recently accessed segments */
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
comment|/**      * Statistics of this cache. Do to the special access patter (see class comment), we cannot      * rely on {@link Cache#stats()}.      */
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Stats
name|stats
init|=
operator|new
name|Stats
argument_list|(
literal|"Segment Cache"
argument_list|)
decl_stmt|;
comment|/**      * Create a new segment cache of the given size.      * @param cacheSizeMB  size of the cache in megabytes.      */
specifier|public
name|SegmentCache
parameter_list|(
name|long
name|cacheSizeMB
parameter_list|)
block|{
name|this
operator|.
name|maximumWeight
operator|=
name|cacheSizeMB
operator|*
literal|1024
operator|*
literal|1024
expr_stmt|;
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
name|weigher
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
block|}
comment|/**      * Create a new segment cache with the {@link #DEFAULT_SEGMENT_CACHE_MB default size}.      */
specifier|public
name|SegmentCache
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_SEGMENT_CACHE_MB
argument_list|)
expr_stmt|;
block|}
comment|/**      * Removal handler called whenever an item is evicted from the cache. Propagates      * to {@link SegmentId#unloaded()}.      */
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
name|SegmentId
name|id
init|=
name|notification
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|Segment
name|segment
init|=
name|notification
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|segment
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
name|weigher
operator|.
name|weigh
argument_list|(
name|id
argument_list|,
name|segment
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|stats
operator|.
name|evictionCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|id
operator|.
name|unloaded
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Unconditionally put an item in the cache */
specifier|private
name|Segment
name|put
parameter_list|(
annotation|@
name|Nonnull
name|SegmentId
name|id
parameter_list|,
annotation|@
name|Nonnull
name|Segment
name|segment
parameter_list|)
block|{
comment|// Call loaded *before* putting the segment into the cache as the latter
comment|// might cause it to get evicted right away again.
name|id
operator|.
name|loaded
argument_list|(
name|segment
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
name|stats
operator|.
name|currentWeight
operator|.
name|addAndGet
argument_list|(
name|weigher
operator|.
name|weigh
argument_list|(
name|id
argument_list|,
name|segment
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|segment
return|;
block|}
comment|/**      * Retrieve an segment from the cache or load it and cache it if not yet in the cache.      * @param id        the id of the segment      * @param loader    the loader to load the segment if not yet in the cache      * @return          the segment identified by {@code id}      * @throws ExecutionException  when {@code loader} failed to load an segment      */
annotation|@
name|Nonnull
specifier|public
name|Segment
name|getSegment
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|SegmentId
name|id
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Callable
argument_list|<
name|Segment
argument_list|>
name|loader
parameter_list|)
throws|throws
name|ExecutionException
block|{
comment|// Load bulk segment directly without putting it in cache
try|try
block|{
if|if
condition|(
name|id
operator|.
name|isBulkSegmentId
argument_list|()
condition|)
block|{
return|return
name|loader
operator|.
name|call
argument_list|()
return|;
block|}
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
comment|// Load data segment and put it in the cache
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
return|return
name|put
argument_list|(
name|id
argument_list|,
name|segment
argument_list|)
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
block|}
comment|/**      * Put a segment into the cache. This method does nothing for      * {@link SegmentId#isBulkSegmentId() bulk} segments.      * @param segment  the segment to cache      */
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
operator|!
name|id
operator|.
name|isBulkSegmentId
argument_list|()
condition|)
block|{
name|put
argument_list|(
name|id
argument_list|,
name|segment
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Clear all segment from the cache      */
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
comment|/**      * @return  Statistics for this cache.      */
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
comment|/**      * Record a hit in this cache's underlying statistics.      * @see SegmentId#onAccess      */
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
comment|/** We cannot rely on the statistics of the underlying Guava cache as all cache hits      * are taken by {@link SegmentId#getSegment()} and thus never seen by the cache.      */
specifier|private
class|class
name|Stats
extends|extends
name|AbstractCacheStats
block|{
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
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheStats
name|getCurrentStats
parameter_list|()
block|{
return|return
operator|new
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
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
name|cache
operator|.
name|size
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

