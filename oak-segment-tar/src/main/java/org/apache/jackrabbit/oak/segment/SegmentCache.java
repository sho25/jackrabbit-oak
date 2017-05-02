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
name|CacheWeights
operator|.
name|SegmentCacheWeigher
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
name|RemovalListener
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

begin_comment
comment|/**  * A cache for {@link Segment} instances by their {@link SegmentId}.  *<p>  * Conceptually this cache serves as a 2nd level cache for segments. The 1st level cache is  * implemented by memoising the segment in its id (see {@link SegmentId#segment}. Every time  * an segment is evicted from this cache the memoised segment is discarded (see  * {@link SegmentId#unloaded()}).  *<p>  * As a consequence this cache is actually only queried for segments it does not contain,  * which are then loaded through the loader passed to {@link #getSegment(SegmentId, Callable)}.  * This behaviour is eventually reflected in the cache statistics (see {@link #getCacheStats()}),  * which always reports a {@link CacheStats#getHitRate()} () miss rate} of 1.  */
end_comment

begin_class
specifier|public
class|class
name|SegmentCache
block|{
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SEGMENT_CACHE_MB
init|=
literal|256
decl_stmt|;
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
specifier|private
specifier|final
name|long
name|maximumWeight
decl_stmt|;
comment|/**      * Cache of recently accessed segments      */
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
name|recordStats
argument_list|()
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
operator|new
name|RemovalListener
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
name|onRemoval
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
name|id
operator|.
name|unloaded
argument_list|()
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
specifier|private
name|void
name|put
parameter_list|(
annotation|@
name|Nonnull
name|SegmentId
name|id
parameter_list|,
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
try|try
block|{
name|Segment
name|segment
init|=
name|loader
operator|.
name|call
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|isBulkSegmentId
argument_list|()
condition|)
block|{
return|return
name|segment
return|;
block|}
name|put
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
comment|/**      * Put a segment into the cache      * @param segment  the segment to cache      */
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
name|segmentId
init|=
name|segment
operator|.
name|getSegmentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|segmentId
operator|.
name|isBulkSegmentId
argument_list|()
condition|)
block|{
return|return;
block|}
name|put
argument_list|(
name|segmentId
argument_list|,
name|segment
argument_list|)
expr_stmt|;
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
comment|/**      * See the class comment regarding some peculiarities of this cache's statistics      * @return  statistics for this cache.      */
annotation|@
name|Nonnull
specifier|public
name|CacheStats
name|getCacheStats
parameter_list|()
block|{
return|return
operator|new
name|CacheStats
argument_list|(
name|cache
argument_list|,
literal|"Segment Cache"
argument_list|,
name|weigher
argument_list|,
name|maximumWeight
argument_list|)
return|;
block|}
block|}
end_class

end_unit

