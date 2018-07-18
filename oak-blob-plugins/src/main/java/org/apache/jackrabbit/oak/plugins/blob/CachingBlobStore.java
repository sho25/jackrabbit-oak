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
name|blob
package|;
end_package

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
name|commons
operator|.
name|StringUtils
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
name|AbstractBlobStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
comment|/**  * A blob store with a cache.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|CachingBlobStore
extends|extends
name|AbstractBlobStore
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
name|CachingBlobStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|long
name|DEFAULT_CACHE_SIZE
init|=
literal|16
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|protected
specifier|final
name|CacheLIRS
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|cache
decl_stmt|;
specifier|protected
specifier|final
name|long
name|blobCacheSize
decl_stmt|;
specifier|private
specifier|final
name|Weigher
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|weigher
init|=
operator|new
name|Weigher
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|weigh
parameter_list|(
annotation|@
name|NotNull
name|String
name|key
parameter_list|,
annotation|@
name|NotNull
name|byte
index|[]
name|value
parameter_list|)
block|{
name|long
name|weight
init|=
operator|(
name|long
operator|)
name|StringUtils
operator|.
name|estimateMemoryUsage
argument_list|(
name|key
argument_list|)
operator|+
name|value
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|weight
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Calculated weight larger than Integer.MAX_VALUE: {}."
argument_list|,
name|weight
argument_list|)
expr_stmt|;
name|weight
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
return|return
operator|(
name|int
operator|)
name|weight
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|CacheStats
name|cacheStats
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MEM_CACHE_NAME
init|=
literal|"BlobStore-MemCache"
decl_stmt|;
specifier|public
name|CachingBlobStore
parameter_list|(
name|long
name|cacheSize
parameter_list|)
block|{
name|this
operator|.
name|blobCacheSize
operator|=
name|cacheSize
expr_stmt|;
name|cache
operator|=
name|CacheLIRS
operator|.
expr|<
name|String
operator|,
name|byte
index|[]
operator|>
name|newBuilder
argument_list|()
operator|.
name|recordStats
argument_list|()
operator|.
name|module
argument_list|(
name|MEM_CACHE_NAME
argument_list|)
operator|.
name|maximumWeight
argument_list|(
name|cacheSize
argument_list|)
operator|.
name|averageWeight
argument_list|(
name|getBlockSize
argument_list|()
operator|/
literal|2
argument_list|)
operator|.
name|weigher
argument_list|(
name|weigher
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cacheStats
operator|=
operator|new
name|CacheStats
argument_list|(
name|cache
argument_list|,
name|MEM_CACHE_NAME
argument_list|,
name|weigher
argument_list|,
name|cacheSize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CachingBlobStore
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_CACHE_SIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clearCache
parameter_list|()
block|{
name|cache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
specifier|public
name|long
name|getBlobCacheSize
parameter_list|()
block|{
comment|//Required for testcase to validate the configured cache size
return|return
name|blobCacheSize
return|;
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

