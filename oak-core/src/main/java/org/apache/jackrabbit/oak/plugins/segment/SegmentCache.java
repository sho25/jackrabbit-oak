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

begin_comment
comment|/**  * Combined memory and disk cache for segments.  */
end_comment

begin_class
specifier|public
class|class
name|SegmentCache
block|{
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_MEMORY_CACHE_SIZE
init|=
literal|1
operator|<<
literal|28
decl_stmt|;
comment|// 256MB
specifier|private
specifier|final
name|Cache
argument_list|<
name|UUID
argument_list|,
name|Segment
argument_list|>
name|memoryCache
decl_stmt|;
specifier|private
specifier|final
name|CacheStats
name|cacheStats
decl_stmt|;
comment|// private final Cache<UUID, File> diskCache;
comment|// private final File diskCacheDirectory;
specifier|public
name|SegmentCache
parameter_list|(
name|long
name|memoryCacheSize
parameter_list|)
block|{
comment|//        this.diskCacheDirectory = diskCacheDirectory;
comment|//        this.diskCache = CacheBuilder.newBuilder()
comment|//                .maximumWeight(diskCacheSize)
comment|//                .weigher(new Weigher<UUID, File>() {
comment|//                    @Override
comment|//                    public int weigh(UUID key, File value) {
comment|//                        return (int) value.length(); //<= max segment size
comment|//                    }
comment|//                }).build();
name|this
operator|.
name|memoryCache
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|maximumWeight
argument_list|(
name|memoryCacheSize
argument_list|)
operator|.
name|recordStats
argument_list|()
operator|.
name|weigher
argument_list|(
name|Segment
operator|.
name|WEIGHER
argument_list|)
comment|//                .removalListener(new RemovalListener<UUID, Segment>() {
comment|//                    @Override
comment|//                    public void onRemoval(
comment|//                            RemovalNotification<UUID, Segment> notification) {
comment|//                        notification.getValue();
comment|//                    }
comment|//                })
operator|.
name|build
argument_list|()
expr_stmt|;
name|cacheStats
operator|=
operator|new
name|CacheStats
argument_list|(
name|memoryCache
argument_list|,
literal|"Segment"
argument_list|,
name|Segment
operator|.
name|WEIGHER
argument_list|,
name|memoryCacheSize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SegmentCache
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_MEMORY_CACHE_SIZE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Segment
name|getSegment
parameter_list|(
name|UUID
name|segmentId
parameter_list|,
name|Callable
argument_list|<
name|Segment
argument_list|>
name|loader
parameter_list|)
block|{
try|try
block|{
return|return
name|memoryCache
operator|.
name|get
argument_list|(
name|segmentId
argument_list|,
name|loader
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Failed to load segment "
operator|+
name|segmentId
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|addSegment
parameter_list|(
name|Segment
name|segment
parameter_list|)
block|{
name|memoryCache
operator|.
name|put
argument_list|(
name|segment
operator|.
name|getSegmentId
argument_list|()
argument_list|,
name|segment
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeSegment
parameter_list|(
name|UUID
name|segmentId
parameter_list|)
block|{
name|memoryCache
operator|.
name|invalidate
argument_list|(
name|segmentId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CacheStats
name|getCacheStats
parameter_list|()
block|{
return|return
name|cacheStats
return|;
block|}
block|}
end_class

end_unit

