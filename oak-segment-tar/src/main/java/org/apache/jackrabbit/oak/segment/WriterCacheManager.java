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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Suppliers
operator|.
name|memoize
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
name|Iterators
operator|.
name|transform
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
name|Maps
operator|.
name|newConcurrentMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Integer
operator|.
name|getInteger
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
name|RecordCache
operator|.
name|newRecordCache
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|ConcurrentMap
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|Function
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
name|file
operator|.
name|PriorityCache
import|;
end_import

begin_comment
comment|/**  * Instances of this class manage the deduplication caches used  * by the {@link SegmentWriter} to avoid writing multiple copies  * of the same record. The caches are striped into generations  * with one generation per gc cycle. This avoids records old  * generations being reused.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|WriterCacheManager
block|{
comment|/**      * Default size of the string cache.      * @see #getStringCache(int)      */
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_STRING_CACHE_SIZE
init|=
name|getInteger
argument_list|(
literal|"oak.tar.stringsCacheSize"
argument_list|,
literal|15000
argument_list|)
decl_stmt|;
comment|/**      * Default size of the template cache.      * @see #getTemplateCache(int)      */
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_TEMPLATE_CACHE_SIZE
init|=
name|getInteger
argument_list|(
literal|"oak.tar.templatesCacheSize"
argument_list|,
literal|3000
argument_list|)
decl_stmt|;
comment|/**      * Default size of the node deduplication cache.      * @see #getNodeCache(int)      */
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NODE_CACHE_SIZE
init|=
name|getInteger
argument_list|(
literal|"oak.tar.nodeCacheSize"
argument_list|,
literal|1048576
argument_list|)
decl_stmt|;
comment|/**      * @param generation      * @return  cache for string records of the given {@code generation}.      */
annotation|@
name|Nonnull
specifier|public
specifier|abstract
name|RecordCache
argument_list|<
name|String
argument_list|>
name|getStringCache
parameter_list|(
name|int
name|generation
parameter_list|)
function_decl|;
comment|/**      * @param generation      * @return  cache for template records of the given {@code generation}.      */
annotation|@
name|Nonnull
specifier|public
specifier|abstract
name|RecordCache
argument_list|<
name|Template
argument_list|>
name|getTemplateCache
parameter_list|(
name|int
name|generation
parameter_list|)
function_decl|;
comment|/**      * @param generation      * @return  cache for node records of the given {@code generation}.      */
annotation|@
name|Nonnull
specifier|public
specifier|abstract
name|NodeCache
name|getNodeCache
parameter_list|(
name|int
name|generation
parameter_list|)
function_decl|;
comment|/**      * @return  statistics for the string cache or {@code null} if not available.      */
annotation|@
name|CheckForNull
specifier|public
name|CacheStatsMBean
name|getStringCacheStats
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @return  statistics for the template cache or {@code null} if not available.      */
annotation|@
name|CheckForNull
specifier|public
name|CacheStatsMBean
name|getTemplateCacheStats
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @return  statistics for the node cache or {@code null} if not available.      */
annotation|@
name|CheckForNull
specifier|public
name|CacheStatsMBean
name|getNodeCacheStats
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Get occupancy information for the node deduplication cache indicating occupancy and      * evictions per priority.      * @return  occupancy information for the node deduplication cache.      */
annotation|@
name|CheckForNull
specifier|public
name|String
name|getNodeCacheOccupancyInfo
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * This implementation of {@link WriterCacheManager} returns empty caches      * of size 0.      * @see #INSTANCE      */
specifier|public
specifier|static
class|class
name|Empty
extends|extends
name|WriterCacheManager
block|{
comment|/**          * Singleton instance of {@link Empty}          */
specifier|public
specifier|static
specifier|final
name|WriterCacheManager
name|INSTANCE
init|=
operator|new
name|Empty
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|RecordCache
argument_list|<
name|String
argument_list|>
name|stringCache
init|=
name|newRecordCache
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|RecordCache
argument_list|<
name|Template
argument_list|>
name|templateCache
init|=
name|newRecordCache
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|Empty
parameter_list|()
block|{}
comment|/**          * @return  empty cache of size 0          */
annotation|@
name|Override
specifier|public
name|RecordCache
argument_list|<
name|String
argument_list|>
name|getStringCache
parameter_list|(
name|int
name|generation
parameter_list|)
block|{
return|return
name|stringCache
return|;
block|}
comment|/**          * @return  empty cache of size 0          */
annotation|@
name|Override
specifier|public
name|RecordCache
argument_list|<
name|Template
argument_list|>
name|getTemplateCache
parameter_list|(
name|int
name|generation
parameter_list|)
block|{
return|return
name|templateCache
return|;
block|}
comment|/**          * @return  a {@code NodeCache} cache that is always empty          */
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeCache
name|getNodeCache
parameter_list|(
name|int
name|generation
parameter_list|)
block|{
return|return
operator|new
name|NodeCache
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|put
parameter_list|(
annotation|@
name|Nonnull
name|String
name|stableId
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|recordId
parameter_list|,
name|byte
name|cost
parameter_list|)
block|{ }
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|RecordId
name|get
parameter_list|(
annotation|@
name|Nonnull
name|String
name|stableId
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
block|}
comment|/**      * This implementation of {@link WriterCacheManager} returns      * {@link RecordCache} instances for the string and template cache      * and {@link NodeCache} instance for the node cache.      */
specifier|public
specifier|static
class|class
name|Default
extends|extends
name|WriterCacheManager
block|{
comment|/**          * Cache of recently stored string records, used to avoid storing duplicates          * of frequently occurring data.          */
specifier|private
specifier|final
name|Generations
argument_list|<
name|RecordCache
argument_list|<
name|String
argument_list|>
argument_list|>
name|stringCaches
decl_stmt|;
comment|/**          * Cache of recently stored template records, used to avoid storing          * duplicates of frequently occurring data.          */
specifier|private
specifier|final
name|Generations
argument_list|<
name|RecordCache
argument_list|<
name|Template
argument_list|>
argument_list|>
name|templateCaches
decl_stmt|;
comment|/**          * Cache of recently stored nodes to avoid duplicating linked nodes (i.e. checkpoints)          * during compaction.          */
specifier|private
specifier|final
name|Supplier
argument_list|<
name|PriorityCache
argument_list|<
name|String
argument_list|,
name|RecordId
argument_list|>
argument_list|>
name|nodeCache
decl_stmt|;
comment|/**          * New instance using the passed factories for creating cache instances.          * The factories will be invoked exactly once when a generation of a          * cache is requested that has not been requested before.          *          * @param stringCacheFactory       factory for the string cache          * @param templateCacheFactory     factory for the template cache          * @param nodeCacheFactory         factory for the node cache          */
specifier|public
name|Default
parameter_list|(
annotation|@
name|Nonnull
name|Supplier
argument_list|<
name|RecordCache
argument_list|<
name|String
argument_list|>
argument_list|>
name|stringCacheFactory
parameter_list|,
annotation|@
name|Nonnull
name|Supplier
argument_list|<
name|RecordCache
argument_list|<
name|Template
argument_list|>
argument_list|>
name|templateCacheFactory
parameter_list|,
annotation|@
name|Nonnull
name|Supplier
argument_list|<
name|PriorityCache
argument_list|<
name|String
argument_list|,
name|RecordId
argument_list|>
argument_list|>
name|nodeCacheFactory
parameter_list|)
block|{
name|this
operator|.
name|stringCaches
operator|=
operator|new
name|Generations
argument_list|<>
argument_list|(
name|stringCacheFactory
argument_list|)
expr_stmt|;
name|this
operator|.
name|templateCaches
operator|=
operator|new
name|Generations
argument_list|<>
argument_list|(
name|templateCacheFactory
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeCache
operator|=
name|memoize
argument_list|(
name|nodeCacheFactory
argument_list|)
expr_stmt|;
block|}
comment|/**          * New instance using the default factories {@link RecordCache#factory(int)}          * and {@link PriorityCache#factory(int)} with the sizes          * {@link #DEFAULT_STRING_CACHE_SIZE}, {@link #DEFAULT_TEMPLATE_CACHE_SIZE}          * and {@link #DEFAULT_NODE_CACHE_SIZE}.          */
specifier|public
name|Default
parameter_list|()
block|{
name|this
argument_list|(
name|RecordCache
operator|.
expr|<
name|String
operator|>
name|factory
argument_list|(
name|DEFAULT_STRING_CACHE_SIZE
argument_list|)
argument_list|,
name|RecordCache
operator|.
expr|<
name|Template
operator|>
name|factory
argument_list|(
name|DEFAULT_TEMPLATE_CACHE_SIZE
argument_list|)
argument_list|,
name|PriorityCache
operator|.
expr|<
name|String
argument_list|,
name|RecordId
operator|>
name|factory
argument_list|(
name|DEFAULT_NODE_CACHE_SIZE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|Generations
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Iterable
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|Integer
argument_list|,
name|Supplier
argument_list|<
name|T
argument_list|>
argument_list|>
name|generations
init|=
name|newConcurrentMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Supplier
argument_list|<
name|T
argument_list|>
name|cacheFactory
decl_stmt|;
name|Generations
parameter_list|(
annotation|@
name|Nonnull
name|Supplier
argument_list|<
name|T
argument_list|>
name|cacheFactory
parameter_list|)
block|{
name|this
operator|.
name|cacheFactory
operator|=
name|checkNotNull
argument_list|(
name|cacheFactory
argument_list|)
expr_stmt|;
block|}
name|T
name|getGeneration
parameter_list|(
specifier|final
name|int
name|generation
parameter_list|)
block|{
comment|// Preemptive check to limit the number of wasted (Memoizing)Supplier instances
if|if
condition|(
operator|!
name|generations
operator|.
name|containsKey
argument_list|(
name|generation
argument_list|)
condition|)
block|{
name|generations
operator|.
name|putIfAbsent
argument_list|(
name|generation
argument_list|,
name|memoize
argument_list|(
name|cacheFactory
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|generations
operator|.
name|get
argument_list|(
name|generation
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|transform
argument_list|(
name|generations
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|Supplier
argument_list|<
name|T
argument_list|>
argument_list|,
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|T
name|apply
parameter_list|(
name|Supplier
argument_list|<
name|T
argument_list|>
name|cacheFactory
parameter_list|)
block|{
return|return
name|cacheFactory
operator|.
name|get
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
name|void
name|evictGenerations
parameter_list|(
annotation|@
name|Nonnull
name|Predicate
argument_list|<
name|Integer
argument_list|>
name|evict
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|it
init|=
name|generations
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|evict
operator|.
name|apply
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RecordCache
argument_list|<
name|String
argument_list|>
name|getStringCache
parameter_list|(
name|int
name|generation
parameter_list|)
block|{
return|return
name|stringCaches
operator|.
name|getGeneration
argument_list|(
name|generation
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RecordCache
argument_list|<
name|Template
argument_list|>
name|getTemplateCache
parameter_list|(
name|int
name|generation
parameter_list|)
block|{
return|return
name|templateCaches
operator|.
name|getGeneration
argument_list|(
name|generation
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeCache
name|getNodeCache
parameter_list|(
specifier|final
name|int
name|generation
parameter_list|)
block|{
return|return
operator|new
name|NodeCache
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|put
parameter_list|(
annotation|@
name|Nonnull
name|String
name|stableId
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|recordId
parameter_list|,
name|byte
name|cost
parameter_list|)
block|{
name|nodeCache
operator|.
name|get
argument_list|()
operator|.
name|put
argument_list|(
name|stableId
argument_list|,
name|recordId
argument_list|,
name|generation
argument_list|,
name|cost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|RecordId
name|get
parameter_list|(
annotation|@
name|Nonnull
name|String
name|stableId
parameter_list|)
block|{
return|return
name|nodeCache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
name|stableId
argument_list|,
name|generation
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|CacheStatsMBean
name|getStringCacheStats
parameter_list|()
block|{
return|return
operator|new
name|RecordCacheStats
argument_list|(
literal|"String deduplication cache stats"
argument_list|,
name|accumulateRecordCacheStats
argument_list|(
name|stringCaches
argument_list|)
argument_list|,
name|accumulateRecordCacheSizes
argument_list|(
name|stringCaches
argument_list|)
argument_list|,
name|accumulateRecordCacheWeights
argument_list|(
name|stringCaches
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|CacheStatsMBean
name|getTemplateCacheStats
parameter_list|()
block|{
return|return
operator|new
name|RecordCacheStats
argument_list|(
literal|"Template deduplication cache stats"
argument_list|,
name|accumulateRecordCacheStats
argument_list|(
name|templateCaches
argument_list|)
argument_list|,
name|accumulateRecordCacheSizes
argument_list|(
name|templateCaches
argument_list|)
argument_list|,
name|accumulateRecordCacheWeights
argument_list|(
name|templateCaches
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Supplier
argument_list|<
name|CacheStats
argument_list|>
name|accumulateRecordCacheStats
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|RecordCache
argument_list|<
name|T
argument_list|>
argument_list|>
name|caches
parameter_list|)
block|{
return|return
operator|new
name|Supplier
argument_list|<
name|CacheStats
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|CacheStats
name|get
parameter_list|()
block|{
name|CacheStats
name|stats
init|=
operator|new
name|CacheStats
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|RecordCache
argument_list|<
name|?
argument_list|>
name|cache
range|:
name|caches
control|)
block|{
name|stats
operator|=
name|stats
operator|.
name|plus
argument_list|(
name|cache
operator|.
name|getStats
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|stats
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Supplier
argument_list|<
name|Long
argument_list|>
name|accumulateRecordCacheSizes
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|RecordCache
argument_list|<
name|T
argument_list|>
argument_list|>
name|caches
parameter_list|)
block|{
return|return
operator|new
name|Supplier
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|get
parameter_list|()
block|{
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RecordCache
argument_list|<
name|?
argument_list|>
name|cache
range|:
name|caches
control|)
block|{
name|size
operator|+=
name|cache
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Supplier
argument_list|<
name|Long
argument_list|>
name|accumulateRecordCacheWeights
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|RecordCache
argument_list|<
name|T
argument_list|>
argument_list|>
name|caches
parameter_list|)
block|{
return|return
operator|new
name|Supplier
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|get
parameter_list|()
block|{
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RecordCache
argument_list|<
name|?
argument_list|>
name|cache
range|:
name|caches
control|)
block|{
name|size
operator|+=
name|cache
operator|.
name|estimateCurrentWeight
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
block|}
return|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|CacheStatsMBean
name|getNodeCacheStats
parameter_list|()
block|{
return|return
operator|new
name|RecordCacheStats
argument_list|(
literal|"Node deduplication cache stats"
argument_list|,
operator|new
name|Supplier
argument_list|<
name|CacheStats
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|CacheStats
name|get
parameter_list|()
block|{
return|return
name|nodeCache
operator|.
name|get
argument_list|()
operator|.
name|getStats
argument_list|()
return|;
block|}
block|}
argument_list|,
operator|new
name|Supplier
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|get
parameter_list|()
block|{
return|return
name|nodeCache
operator|.
name|get
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|,
operator|new
name|Supplier
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|get
parameter_list|()
block|{
return|return
name|nodeCache
operator|.
name|get
argument_list|()
operator|.
name|estimateCurrentWeight
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNodeCacheOccupancyInfo
parameter_list|()
block|{
return|return
name|nodeCache
operator|.
name|get
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**          * Remove all cache generations matching the passed {@code generations} predicate.          * @param generations          */
specifier|protected
specifier|final
name|void
name|evictCaches
parameter_list|(
name|Predicate
argument_list|<
name|Integer
argument_list|>
name|generations
parameter_list|)
block|{
name|stringCaches
operator|.
name|evictGenerations
argument_list|(
name|generations
argument_list|)
expr_stmt|;
name|templateCaches
operator|.
name|evictGenerations
argument_list|(
name|generations
argument_list|)
expr_stmt|;
name|nodeCache
operator|.
name|get
argument_list|()
operator|.
name|purgeGenerations
argument_list|(
name|generations
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

