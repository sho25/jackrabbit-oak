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
name|document
operator|.
name|persistentCache
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|ConcurrentMap
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
name|Nullable
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
name|document
operator|.
name|DocumentNodeStore
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
name|document
operator|.
name|DocumentStore
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
name|document
operator|.
name|persistentCache
operator|.
name|PersistentCache
operator|.
name|GenerationCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|h2
operator|.
name|mvstore
operator|.
name|MVMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|h2
operator|.
name|mvstore
operator|.
name|WriteBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|h2
operator|.
name|mvstore
operator|.
name|type
operator|.
name|DataType
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
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_class
class|class
name|NodeCache
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
implements|implements
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
implements|,
name|GenerationCache
block|{
specifier|private
specifier|final
name|PersistentCache
name|cache
decl_stmt|;
specifier|private
specifier|final
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|memCache
decl_stmt|;
specifier|private
specifier|final
name|MultiGenerationMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
decl_stmt|;
specifier|private
specifier|final
name|CacheType
name|type
decl_stmt|;
specifier|private
specifier|final
name|DataType
name|keyType
decl_stmt|;
specifier|private
specifier|final
name|DataType
name|valueType
decl_stmt|;
name|NodeCache
parameter_list|(
name|PersistentCache
name|cache
parameter_list|,
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|memCache
parameter_list|,
name|DocumentNodeStore
name|docNodeStore
parameter_list|,
name|DocumentStore
name|docStore
parameter_list|,
name|CacheType
name|type
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
name|this
operator|.
name|memCache
operator|=
name|memCache
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|PersistentCache
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"wrapping map "
operator|+
name|this
operator|.
name|type
argument_list|)
expr_stmt|;
name|map
operator|=
operator|new
name|MultiGenerationMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|()
expr_stmt|;
name|keyType
operator|=
operator|new
name|KeyDataType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|valueType
operator|=
operator|new
name|ValueDataType
argument_list|(
name|docNodeStore
argument_list|,
name|docStore
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|CacheType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addGeneration
parameter_list|(
name|int
name|generation
parameter_list|,
name|boolean
name|readOnly
parameter_list|)
block|{
name|MVMap
operator|.
name|Builder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|b
init|=
operator|new
name|MVMap
operator|.
name|Builder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|()
operator|.
name|keyType
argument_list|(
name|keyType
argument_list|)
operator|.
name|valueType
argument_list|(
name|valueType
argument_list|)
decl_stmt|;
name|String
name|mapName
init|=
name|type
operator|.
name|name
argument_list|()
decl_stmt|;
name|CacheMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|m
init|=
name|cache
operator|.
name|openMap
argument_list|(
name|generation
argument_list|,
name|mapName
argument_list|,
name|b
argument_list|)
decl_stmt|;
name|map
operator|.
name|addReadMap
argument_list|(
name|generation
argument_list|,
name|m
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|readOnly
condition|)
block|{
name|map
operator|.
name|setWriteMap
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeGeneration
parameter_list|(
name|int
name|generation
parameter_list|)
block|{
name|map
operator|.
name|removeReadMap
argument_list|(
name|generation
argument_list|)
expr_stmt|;
block|}
specifier|private
name|V
name|readIfPresent
parameter_list|(
name|K
name|key
parameter_list|)
block|{
name|cache
operator|.
name|switchGenerationIfNeeded
argument_list|()
expr_stmt|;
name|V
name|v
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|v
return|;
block|}
specifier|private
name|void
name|write
parameter_list|(
specifier|final
name|K
name|key
parameter_list|,
specifier|final
name|V
name|value
parameter_list|)
block|{
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeWithoutBroadcast
parameter_list|(
specifier|final
name|K
name|key
parameter_list|,
specifier|final
name|V
name|value
parameter_list|)
block|{
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|write
parameter_list|(
specifier|final
name|K
name|key
parameter_list|,
specifier|final
name|V
name|value
parameter_list|,
name|boolean
name|broadcast
parameter_list|)
block|{
name|cache
operator|.
name|switchGenerationIfNeeded
argument_list|()
expr_stmt|;
if|if
condition|(
name|broadcast
condition|)
block|{
name|cache
operator|.
name|broadcast
argument_list|(
name|type
argument_list|,
operator|new
name|Function
argument_list|<
name|WriteBuffer
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Void
name|apply
parameter_list|(
annotation|@
name|Nullable
name|WriteBuffer
name|buffer
parameter_list|)
block|{
name|keyType
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|buffer
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|valueType
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|MultiGenerationMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|m
init|=
name|map
decl_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|m
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|m
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|V
name|getIfPresent
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|V
name|value
init|=
name|memCache
operator|.
name|getIfPresent
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
return|;
block|}
name|value
operator|=
name|readIfPresent
argument_list|(
operator|(
name|K
operator|)
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|memCache
operator|.
name|put
argument_list|(
operator|(
name|K
operator|)
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|V
name|get
parameter_list|(
name|K
name|key
parameter_list|,
name|Callable
argument_list|<
name|?
extends|extends
name|V
argument_list|>
name|valueLoader
parameter_list|)
throws|throws
name|ExecutionException
block|{
name|V
name|value
init|=
name|getIfPresent
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
return|;
block|}
name|value
operator|=
name|memCache
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|valueLoader
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|ImmutableMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getAllPresent
parameter_list|(
name|Iterable
argument_list|<
name|?
argument_list|>
name|keys
parameter_list|)
block|{
return|return
name|memCache
operator|.
name|getAllPresent
argument_list|(
name|keys
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|put
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
name|memCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
name|void
name|invalidate
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|memCache
operator|.
name|invalidate
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|write
argument_list|(
operator|(
name|K
operator|)
name|key
argument_list|,
operator|(
name|V
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|putAll
parameter_list|(
name|Map
argument_list|<
name|?
extends|extends
name|K
argument_list|,
name|?
extends|extends
name|V
argument_list|>
name|m
parameter_list|)
block|{
name|memCache
operator|.
name|putAll
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|invalidateAll
parameter_list|(
name|Iterable
argument_list|<
name|?
argument_list|>
name|keys
parameter_list|)
block|{
name|memCache
operator|.
name|invalidateAll
argument_list|(
name|keys
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|invalidateAll
parameter_list|()
block|{
name|memCache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|memCache
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|CacheStats
name|stats
parameter_list|()
block|{
return|return
name|memCache
operator|.
name|stats
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ConcurrentMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|asMap
parameter_list|()
block|{
return|return
name|memCache
operator|.
name|asMap
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|cleanUp
parameter_list|()
block|{
name|memCache
operator|.
name|cleanUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|receive
parameter_list|(
name|ByteBuffer
name|buff
parameter_list|)
block|{
name|K
name|key
init|=
operator|(
name|K
operator|)
name|keyType
operator|.
name|read
argument_list|(
name|buff
argument_list|)
decl_stmt|;
name|V
name|value
decl_stmt|;
if|if
condition|(
name|buff
operator|.
name|get
argument_list|()
operator|==
literal|0
condition|)
block|{
name|value
operator|=
literal|null
expr_stmt|;
name|memCache
operator|.
name|invalidate
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
operator|(
name|V
operator|)
name|valueType
operator|.
name|read
argument_list|(
name|buff
argument_list|)
expr_stmt|;
name|memCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|writeWithoutBroadcast
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

