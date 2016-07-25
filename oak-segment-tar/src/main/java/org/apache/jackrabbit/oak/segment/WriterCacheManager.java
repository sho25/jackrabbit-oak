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

begin_comment
comment|// FIXME OAK-4277: Finalise de-duplication caches
end_comment

begin_comment
comment|// implement configuration, monitoring and management
end_comment

begin_comment
comment|// add unit tests
end_comment

begin_comment
comment|// document, nullability
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|WriterCacheManager
block|{
specifier|private
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
specifier|private
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
specifier|public
specifier|static
class|class
name|Empty
extends|extends
name|WriterCacheManager
block|{
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
specifier|final
name|NodeCache
name|nodeCache
init|=
name|NodeCache
operator|.
name|newNodeCache
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|Empty
parameter_list|()
block|{}
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
name|nodeCache
return|;
block|}
block|}
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
name|Generation
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
name|Generation
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
name|Generation
argument_list|<
name|NodeCache
argument_list|>
name|nodeCaches
decl_stmt|;
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
name|NodeCache
argument_list|>
name|nodeCacheFactory
parameter_list|)
block|{
name|this
operator|.
name|stringCaches
operator|=
operator|new
name|Generation
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
name|Generation
argument_list|<>
argument_list|(
name|templateCacheFactory
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeCaches
operator|=
operator|new
name|Generation
argument_list|<>
argument_list|(
name|nodeCacheFactory
argument_list|)
expr_stmt|;
block|}
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
name|NodeCache
operator|.
name|factory
argument_list|(
literal|1000000
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|Generation
parameter_list|<
name|T
parameter_list|>
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
name|Generation
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
name|nodeCaches
operator|.
name|getGeneration
argument_list|(
name|generation
argument_list|)
return|;
block|}
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
name|nodeCaches
operator|.
name|evictGenerations
argument_list|(
name|generations
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

