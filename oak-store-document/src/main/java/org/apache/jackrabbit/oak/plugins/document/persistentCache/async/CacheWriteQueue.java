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
name|CacheValue
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
specifier|public
class|class
name|CacheWriteQueue
parameter_list|<
name|K
extends|extends
name|CacheValue
parameter_list|,
name|V
extends|extends
name|CacheValue
parameter_list|>
block|{
specifier|private
specifier|final
name|CacheActionDispatcher
name|dispatcher
decl_stmt|;
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
specifier|public
name|CacheWriteQueue
parameter_list|(
name|CacheActionDispatcher
name|dispatcher
parameter_list|,
name|PersistentCache
name|cache
parameter_list|,
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|dispatcher
operator|=
name|dispatcher
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
specifier|public
name|boolean
name|addPut
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
return|return
name|dispatcher
operator|.
name|add
argument_list|(
operator|new
name|PutToCacheAction
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
name|this
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|addInvalidate
parameter_list|(
name|Iterable
argument_list|<
name|K
argument_list|>
name|keys
parameter_list|)
block|{
return|return
name|dispatcher
operator|.
name|add
argument_list|(
operator|new
name|InvalidateCacheAction
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
name|keys
argument_list|,
name|this
argument_list|)
argument_list|)
return|;
block|}
name|PersistentCache
name|getCache
parameter_list|()
block|{
return|return
name|cache
return|;
block|}
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getMap
parameter_list|()
block|{
return|return
name|map
return|;
block|}
block|}
end_class

end_unit

