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
operator|.
name|async
package|;
end_package

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
import|;
end_import

begin_comment
comment|/**  * An invalidate cache action.  *  * @param<K> key type  * @param<V> value type  */
end_comment

begin_class
class|class
name|InvalidateCacheAction
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
implements|implements
name|CacheAction
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
specifier|private
specifier|final
name|PersistentCache
name|cache
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
decl_stmt|;
specifier|private
specifier|final
name|CacheWriteQueue
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|owner
decl_stmt|;
specifier|private
specifier|final
name|Iterable
argument_list|<
name|K
argument_list|>
name|keys
decl_stmt|;
name|InvalidateCacheAction
parameter_list|(
name|CacheWriteQueue
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|cacheWriteQueue
parameter_list|,
name|Iterable
argument_list|<
name|K
argument_list|>
name|keys
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|cacheWriteQueue
expr_stmt|;
name|this
operator|.
name|keys
operator|=
name|keys
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|cacheWriteQueue
operator|.
name|getCache
argument_list|()
expr_stmt|;
name|this
operator|.
name|map
operator|=
name|cacheWriteQueue
operator|.
name|getMap
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|K
name|key
range|:
name|keys
control|)
block|{
name|cache
operator|.
name|switchGenerationIfNeeded
argument_list|()
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|decrement
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|cancel
parameter_list|()
block|{
name|decrement
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|CacheWriteQueue
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|K
argument_list|>
name|getAffectedKeys
parameter_list|()
block|{
return|return
name|keys
return|;
block|}
specifier|private
name|void
name|decrement
parameter_list|()
block|{
for|for
control|(
name|K
name|key
range|:
name|keys
control|)
block|{
name|owner
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

