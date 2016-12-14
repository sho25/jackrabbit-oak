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
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
comment|/**  * Partial mapping of keys of type {@code T} to values of type {@link RecordId}. This is  * typically used for de-duplicating values that have already been persisted and thus  * already have a {@code RecordId}.  * @param<T>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RecordCache
parameter_list|<
name|T
parameter_list|>
block|{
specifier|private
name|long
name|hitCount
decl_stmt|;
specifier|private
name|long
name|missCount
decl_stmt|;
specifier|private
name|long
name|loadCount
decl_stmt|;
specifier|private
name|long
name|evictionCount
decl_stmt|;
comment|/**      * Add a mapping from {@code key} to {@code value}. Any existing mapping is replaced.      */
specifier|public
specifier|abstract
name|void
name|put
parameter_list|(
annotation|@
name|Nonnull
name|T
name|key
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|value
parameter_list|)
function_decl|;
comment|/**      * @return  The mapping for {@code key}, or {@code null} if none.      */
annotation|@
name|CheckForNull
specifier|public
specifier|abstract
name|RecordId
name|get
parameter_list|(
annotation|@
name|Nonnull
name|T
name|key
parameter_list|)
function_decl|;
comment|/**      * @return number of mappings      */
specifier|public
specifier|abstract
name|long
name|size
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|long
name|estimateCurrentWeight
parameter_list|()
function_decl|;
comment|/**      * @return  access statistics for this cache      */
annotation|@
name|Nonnull
specifier|public
name|CacheStats
name|getStats
parameter_list|()
block|{
return|return
operator|new
name|CacheStats
argument_list|(
name|hitCount
argument_list|,
name|missCount
argument_list|,
name|loadCount
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|evictionCount
argument_list|)
return|;
block|}
comment|/**      * Factory method for creating {@code RecordCache} instances. The returned      * instances are all thread safe. They implement a simple LRU behaviour where      * the least recently accessed mapping would be replaced when inserting a      * new mapping would exceed {@code size}.      *      * @return  A new {@code RecordCache} instance of the given {@code size}.      */
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|RecordCache
argument_list|<
name|T
argument_list|>
name|newRecordCache
parameter_list|(
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<=
literal|0
condition|)
block|{
return|return
operator|new
name|Empty
argument_list|<>
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|new
name|Default
argument_list|<>
argument_list|(
name|size
argument_list|,
name|CacheWeights
operator|.
expr|<
name|T
argument_list|,
name|RecordId
operator|>
name|noopWeigher
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * @param size size of the cache      * @param weigher   Needed to provide an estimation of the cache weight in memory      * @return  A factory returning {@code RecordCache} instances of the given {@code size}      *          when invoked.      * @see #newRecordCache(int)      */
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Supplier
argument_list|<
name|RecordCache
argument_list|<
name|T
argument_list|>
argument_list|>
name|factory
parameter_list|(
name|int
name|size
parameter_list|,
annotation|@
name|Nonnull
name|Weigher
argument_list|<
name|T
argument_list|,
name|RecordId
argument_list|>
name|weigher
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<=
literal|0
condition|)
block|{
return|return
name|Empty
operator|.
name|emptyFactory
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Default
operator|.
name|defaultFactory
argument_list|(
name|size
argument_list|,
name|checkNotNull
argument_list|(
name|weigher
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**      * @param size size of the cache      * @return  A factory returning {@code RecordCache} instances of the given {@code size}      *          when invoked.      * @see #newRecordCache(int)      */
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Supplier
argument_list|<
name|RecordCache
argument_list|<
name|T
argument_list|>
argument_list|>
name|factory
parameter_list|(
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<=
literal|0
condition|)
block|{
return|return
name|Empty
operator|.
name|emptyFactory
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Default
operator|.
name|defaultFactory
argument_list|(
name|size
argument_list|,
name|CacheWeights
operator|.
expr|<
name|T
argument_list|,
name|RecordId
operator|>
name|noopWeigher
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|Empty
parameter_list|<
name|T
parameter_list|>
extends|extends
name|RecordCache
argument_list|<
name|T
argument_list|>
block|{
specifier|static
specifier|final
parameter_list|<
name|T
parameter_list|>
name|Supplier
argument_list|<
name|RecordCache
argument_list|<
name|T
argument_list|>
argument_list|>
name|emptyFactory
parameter_list|()
block|{
return|return
operator|new
name|Supplier
argument_list|<
name|RecordCache
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RecordCache
argument_list|<
name|T
argument_list|>
name|get
parameter_list|()
block|{
return|return
operator|new
name|Empty
argument_list|<>
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|put
parameter_list|(
annotation|@
name|Nonnull
name|T
name|key
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|value
parameter_list|)
block|{ }
annotation|@
name|Override
specifier|public
specifier|synchronized
name|RecordId
name|get
parameter_list|(
annotation|@
name|Nonnull
name|T
name|key
parameter_list|)
block|{
name|super
operator|.
name|missCount
operator|++
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
literal|0
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
operator|-
literal|1
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|Default
parameter_list|<
name|T
parameter_list|>
extends|extends
name|RecordCache
argument_list|<
name|T
argument_list|>
block|{
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Map
argument_list|<
name|T
argument_list|,
name|RecordId
argument_list|>
name|records
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Weigher
argument_list|<
name|T
argument_list|,
name|RecordId
argument_list|>
name|weigher
decl_stmt|;
specifier|private
name|long
name|weight
init|=
operator|-
literal|1
decl_stmt|;
specifier|static
specifier|final
parameter_list|<
name|T
parameter_list|>
name|Supplier
argument_list|<
name|RecordCache
argument_list|<
name|T
argument_list|>
argument_list|>
name|defaultFactory
parameter_list|(
specifier|final
name|int
name|size
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Weigher
argument_list|<
name|T
argument_list|,
name|RecordId
argument_list|>
name|weigher
parameter_list|)
block|{
return|return
operator|new
name|Supplier
argument_list|<
name|RecordCache
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RecordCache
argument_list|<
name|T
argument_list|>
name|get
parameter_list|()
block|{
return|return
operator|new
name|Default
argument_list|<>
argument_list|(
name|size
argument_list|,
name|checkNotNull
argument_list|(
name|weigher
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
name|Default
parameter_list|(
specifier|final
name|int
name|size
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Weigher
argument_list|<
name|T
argument_list|,
name|RecordId
argument_list|>
name|weigher
parameter_list|)
block|{
name|this
operator|.
name|weigher
operator|=
name|checkNotNull
argument_list|(
name|weigher
argument_list|)
expr_stmt|;
name|records
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|T
argument_list|,
name|RecordId
argument_list|>
argument_list|(
name|size
operator|*
literal|4
operator|/
literal|3
argument_list|,
literal|0.75f
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|T
argument_list|,
name|RecordId
argument_list|>
name|eldest
parameter_list|)
block|{
name|boolean
name|remove
init|=
name|super
operator|.
name|size
argument_list|()
operator|>
name|size
decl_stmt|;
if|if
condition|(
name|remove
condition|)
block|{
name|Default
operator|.
name|super
operator|.
name|evictionCount
operator|++
expr_stmt|;
name|weight
operator|-=
name|weigher
operator|.
name|weigh
argument_list|(
name|eldest
operator|.
name|getKey
argument_list|()
argument_list|,
name|eldest
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|remove
return|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|put
parameter_list|(
annotation|@
name|Nonnull
name|T
name|key
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|value
parameter_list|)
block|{
name|super
operator|.
name|loadCount
operator|++
expr_stmt|;
name|records
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|weight
operator|+=
name|weigher
operator|.
name|weigh
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|RecordId
name|get
parameter_list|(
annotation|@
name|Nonnull
name|T
name|key
parameter_list|)
block|{
name|RecordId
name|value
init|=
name|records
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|super
operator|.
name|missCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|hitCount
operator|++
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|long
name|size
parameter_list|()
block|{
return|return
name|records
operator|.
name|size
argument_list|()
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
name|weight
return|;
block|}
block|}
block|}
end_class

end_unit

