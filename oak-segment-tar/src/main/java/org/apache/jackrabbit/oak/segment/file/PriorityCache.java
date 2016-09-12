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
operator|.
name|file
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
name|java
operator|.
name|lang
operator|.
name|Integer
operator|.
name|bitCount
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
name|numberOfTrailingZeros
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|fill
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

begin_comment
comment|/**  * {@code PriorityCache} implements a partial mapping from keys of type {@code K} to values  * of type  {@code V}. Mappings are associates with a cost, which states how expensive it is  * to recreate that mapping. This cache uses the cost such that mappings with a higher cost  * have a lower chance of being evicted than mappings with a lower cost. When an item from  * this cache is successfully looked up its cost is incremented by one, unless it has reached  * its maximum cost of {@link Byte#MAX_VALUE} already.  *<p>  * Additionally this cache tracks a generation for mappings. Mappings of later generations  * always take precedence over mappings of earlier generations. That is, putting a mapping of  * a later generation into the cache can cause any mapping of an earlier generation to be evicted  * regardless of its cost.  *<p>  * This cache uses rehashing to resolve clashes. The number of rehashes is configurable. When  * a clash cannot be resolved by rehashing the given number of times the put operation fails.  *<p>  * This cache is thread safe.  * @param<K>  type of the keys  * @param<V>  type of the values  */
end_comment

begin_class
specifier|public
class|class
name|PriorityCache
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
specifier|private
specifier|final
name|int
name|rehash
decl_stmt|;
specifier|private
specifier|final
name|Entry
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
index|[]
name|entries
decl_stmt|;
specifier|private
specifier|final
name|int
index|[]
name|costs
init|=
operator|new
name|int
index|[
literal|256
index|]
decl_stmt|;
specifier|private
specifier|final
name|int
index|[]
name|evictions
init|=
operator|new
name|int
index|[
literal|256
index|]
decl_stmt|;
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
specifier|private
name|long
name|size
decl_stmt|;
comment|/**      * Static factory for creating new {@code PriorityCache} instances.      * @param size  size of the cache. Must be a power of 2.      * @return  a new {@code PriorityCache} instance of the given {@code size}.      */
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|Supplier
argument_list|<
name|PriorityCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|factory
parameter_list|(
specifier|final
name|int
name|size
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|bitCount
argument_list|(
name|size
argument_list|)
operator|==
literal|1
argument_list|)
expr_stmt|;
return|return
operator|new
name|Supplier
argument_list|<
name|PriorityCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PriorityCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|get
parameter_list|()
block|{
return|return
operator|new
name|PriorityCache
argument_list|<>
argument_list|(
name|size
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|private
specifier|static
class|class
name|Entry
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
specifier|static
name|Entry
argument_list|<
name|Void
argument_list|,
name|Void
argument_list|>
name|NULL
init|=
operator|new
name|Entry
argument_list|<>
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
name|Byte
operator|.
name|MIN_VALUE
argument_list|)
decl_stmt|;
specifier|final
name|K
name|key
decl_stmt|;
specifier|final
name|V
name|value
decl_stmt|;
specifier|final
name|int
name|generation
decl_stmt|;
name|byte
name|cost
decl_stmt|;
specifier|public
name|Entry
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|,
name|int
name|generation
parameter_list|,
name|byte
name|cost
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|generation
operator|=
name|generation
expr_stmt|;
name|this
operator|.
name|cost
operator|=
name|cost
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|==
name|NULL
condition|?
literal|"NULL"
else|:
literal|"Entry{"
operator|+
name|key
operator|+
literal|"->"
operator|+
name|value
operator|+
literal|" @"
operator|+
name|generation
operator|+
literal|", $"
operator|+
name|cost
operator|+
literal|"}"
return|;
block|}
block|}
comment|/**      * Create a new instance of the given {@code size}. {@code rehash} specifies the number      * of rehashes to resolve a clash.      * @param size      Size of the cache. Must be a power of {@code 2}.      * @param rehash    Number of rehashes. Must be greater or equal to {@code 0} and      *                  smaller than {@code 32 - numberOfTrailingZeros(size)}.      */
specifier|public
name|PriorityCache
parameter_list|(
name|int
name|size
parameter_list|,
name|int
name|rehash
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|bitCount
argument_list|(
name|size
argument_list|)
operator|==
literal|1
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|rehash
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|rehash
operator|<
literal|32
operator|-
name|numberOfTrailingZeros
argument_list|(
name|size
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|rehash
operator|=
name|rehash
expr_stmt|;
name|entries
operator|=
operator|new
name|Entry
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
index|[
name|size
index|]
expr_stmt|;
name|fill
argument_list|(
name|entries
argument_list|,
name|Entry
operator|.
name|NULL
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new instance of the given {@code size}. The number of rehashes is      * the maximum number allowed by the given {@code size}. ({@code 31 - numberOfTrailingZeros(size)}.      * @param size      Size of the cache. Must be a power of {@code 2}.      */
specifier|public
name|PriorityCache
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
argument_list|(
name|size
argument_list|,
literal|31
operator|-
name|numberOfTrailingZeros
argument_list|(
name|size
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|project
parameter_list|(
name|int
name|hashCode
parameter_list|,
name|int
name|iteration
parameter_list|)
block|{
return|return
operator|(
name|hashCode
operator|>>
name|iteration
operator|)
operator|&
operator|(
name|entries
operator|.
name|length
operator|-
literal|1
operator|)
return|;
block|}
comment|/**      * @return  the number of mappings in this cache.      */
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**      * Add a mapping to the cache.      * @param key            the key of the mapping      * @param value          the value of the mapping      * @param generation     the generation of the mapping      * @param initialCost    the initial cost associated with this mapping      * @return  {@code true} if the mapping has been added, {@code false} otherwise.      */
specifier|public
specifier|synchronized
name|boolean
name|put
parameter_list|(
annotation|@
name|Nonnull
name|K
name|key
parameter_list|,
annotation|@
name|Nonnull
name|V
name|value
parameter_list|,
name|int
name|generation
parameter_list|,
name|byte
name|initialCost
parameter_list|)
block|{
name|int
name|hashCode
init|=
name|key
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|byte
name|cheapest
init|=
name|initialCost
decl_stmt|;
name|int
name|index
init|=
operator|-
literal|1
decl_stmt|;
name|boolean
name|eviction
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<=
name|rehash
condition|;
name|k
operator|++
control|)
block|{
name|int
name|i
init|=
name|project
argument_list|(
name|hashCode
argument_list|,
name|k
argument_list|)
decl_stmt|;
name|Entry
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|entry
init|=
name|entries
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|entry
operator|==
name|Entry
operator|.
name|NULL
condition|)
block|{
comment|// Empty slot -> use this index
name|index
operator|=
name|i
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|entry
operator|.
name|generation
operator|<=
name|generation
operator|&&
name|key
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|key
argument_list|)
condition|)
block|{
comment|// Key exists and generation is greater or equal -> use this index and boost the cost
name|index
operator|=
name|i
expr_stmt|;
name|initialCost
operator|=
name|entry
operator|.
name|cost
expr_stmt|;
if|if
condition|(
name|initialCost
operator|<
name|Byte
operator|.
name|MAX_VALUE
condition|)
block|{
name|initialCost
operator|++
expr_stmt|;
block|}
break|break;
block|}
elseif|else
if|if
condition|(
name|entry
operator|.
name|generation
operator|<
name|generation
condition|)
block|{
comment|// Old generation -> use this index
name|index
operator|=
name|i
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|entry
operator|.
name|cost
operator|<
name|cheapest
condition|)
block|{
comment|// Candidate slot, keep on searching for even cheaper slots
name|cheapest
operator|=
name|entry
operator|.
name|cost
expr_stmt|;
name|index
operator|=
name|i
expr_stmt|;
name|eviction
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
name|Entry
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|old
init|=
name|entries
index|[
name|index
index|]
decl_stmt|;
name|entries
index|[
name|index
index|]
operator|=
operator|new
name|Entry
argument_list|<>
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
name|generation
argument_list|,
name|initialCost
argument_list|)
expr_stmt|;
name|loadCount
operator|++
expr_stmt|;
name|costs
index|[
name|initialCost
operator|-
name|Byte
operator|.
name|MIN_VALUE
index|]
operator|++
expr_stmt|;
if|if
condition|(
name|old
operator|!=
name|Entry
operator|.
name|NULL
condition|)
block|{
name|costs
index|[
name|old
operator|.
name|cost
operator|-
name|Byte
operator|.
name|MIN_VALUE
index|]
operator|--
expr_stmt|;
if|if
condition|(
name|eviction
condition|)
block|{
name|evictions
index|[
name|old
operator|.
name|cost
operator|-
name|Byte
operator|.
name|MIN_VALUE
index|]
operator|++
expr_stmt|;
name|evictionCount
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
name|size
operator|++
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * Look up a mapping from this cache by its {@code key} and {@code generation}.      * @param key         key of the mapping to look up      * @param generation  generation of the mapping to look up      * @return  the mapping for {@code key} and {@code generation} or {@code null} if this      *          cache does not contain such a mapping.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|CheckForNull
specifier|public
specifier|synchronized
name|V
name|get
parameter_list|(
annotation|@
name|Nonnull
name|K
name|key
parameter_list|,
name|int
name|generation
parameter_list|)
block|{
name|int
name|hashCode
init|=
name|key
operator|.
name|hashCode
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<=
name|rehash
condition|;
name|k
operator|++
control|)
block|{
name|int
name|i
init|=
name|project
argument_list|(
name|hashCode
argument_list|,
name|k
argument_list|)
decl_stmt|;
name|Entry
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|entry
init|=
name|entries
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|generation
operator|==
name|entry
operator|.
name|generation
operator|&&
name|key
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|key
argument_list|)
condition|)
block|{
if|if
condition|(
name|entry
operator|.
name|cost
operator|<
name|Byte
operator|.
name|MAX_VALUE
condition|)
block|{
name|costs
index|[
name|entry
operator|.
name|cost
operator|-
name|Byte
operator|.
name|MIN_VALUE
index|]
operator|--
expr_stmt|;
name|entry
operator|.
name|cost
operator|++
expr_stmt|;
name|costs
index|[
name|entry
operator|.
name|cost
operator|-
name|Byte
operator|.
name|MIN_VALUE
index|]
operator|++
expr_stmt|;
block|}
name|hitCount
operator|++
expr_stmt|;
return|return
operator|(
name|V
operator|)
name|entry
operator|.
name|value
return|;
block|}
block|}
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
specifier|synchronized
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PriorityCache"
operator|+
literal|"{ costs="
operator|+
name|toString
argument_list|(
name|costs
argument_list|)
operator|+
literal|", evictions="
operator|+
name|toString
argument_list|(
name|evictions
argument_list|)
operator|+
literal|" }"
return|;
block|}
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
name|int
index|[]
name|ints
parameter_list|)
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"["
argument_list|)
decl_stmt|;
name|String
name|sep
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ints
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|ints
index|[
name|i
index|]
operator|>
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
name|sep
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|"->"
argument_list|)
operator|.
name|append
argument_list|(
name|ints
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|sep
operator|=
literal|","
expr_stmt|;
block|}
block|}
return|return
name|b
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
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
block|}
end_class

end_unit

