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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|plugins
operator|.
name|document
operator|.
name|Collection
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
name|Document
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
name|DocumentStoreException
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
name|UpdateOp
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
name|cache
operator|.
name|CacheInvalidationStats
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

begin_comment
comment|/**  * Implements a<code>DocumentStore</code> wrapper which synchronizes on all  * methods.  */
end_comment

begin_class
specifier|public
class|class
name|SynchronizingDocumentStoreWrapper
implements|implements
name|DocumentStore
block|{
specifier|final
name|DocumentStore
name|store
decl_stmt|;
specifier|public
name|SynchronizingDocumentStoreWrapper
parameter_list|(
name|DocumentStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|find
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
specifier|final
name|String
name|key
parameter_list|)
block|{
return|return
name|store
operator|.
name|find
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|find
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
specifier|final
name|String
name|key
parameter_list|,
specifier|final
name|int
name|maxCacheAge
parameter_list|)
block|{
return|return
name|store
operator|.
name|find
argument_list|(
name|collection
argument_list|,
name|key
argument_list|,
name|maxCacheAge
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
specifier|synchronized
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|query
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
specifier|final
name|String
name|fromKey
parameter_list|,
specifier|final
name|String
name|toKey
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|)
block|{
return|return
name|store
operator|.
name|query
argument_list|(
name|collection
argument_list|,
name|fromKey
argument_list|,
name|toKey
argument_list|,
name|limit
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
specifier|synchronized
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|query
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
specifier|final
name|String
name|fromKey
parameter_list|,
specifier|final
name|String
name|toKey
parameter_list|,
specifier|final
name|String
name|indexedProperty
parameter_list|,
specifier|final
name|long
name|startValue
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|)
block|{
return|return
name|store
operator|.
name|query
argument_list|(
name|collection
argument_list|,
name|fromKey
argument_list|,
name|toKey
argument_list|,
name|indexedProperty
argument_list|,
name|startValue
argument_list|,
name|limit
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|remove
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|store
operator|.
name|remove
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|remove
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|)
block|{
name|store
operator|.
name|remove
argument_list|(
name|collection
argument_list|,
name|keys
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|int
name|remove
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|toRemove
parameter_list|)
block|{
return|return
name|store
operator|.
name|remove
argument_list|(
name|collection
argument_list|,
name|toRemove
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|int
name|remove
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|indexedProperty
parameter_list|,
name|long
name|startValue
parameter_list|,
name|long
name|endValue
parameter_list|)
throws|throws
name|DocumentStoreException
block|{
return|return
name|store
operator|.
name|remove
argument_list|(
name|collection
argument_list|,
name|indexedProperty
argument_list|,
name|startValue
argument_list|,
name|endValue
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|boolean
name|create
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
specifier|final
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updateOps
parameter_list|)
block|{
return|return
name|store
operator|.
name|create
argument_list|(
name|collection
argument_list|,
name|updateOps
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|createOrUpdate
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
specifier|final
name|UpdateOp
name|update
parameter_list|)
block|{
return|return
name|store
operator|.
name|createOrUpdate
argument_list|(
name|collection
argument_list|,
name|update
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|createOrUpdate
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updateOps
parameter_list|)
block|{
return|return
name|store
operator|.
name|createOrUpdate
argument_list|(
name|collection
argument_list|,
name|updateOps
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|findAndUpdate
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
specifier|final
name|UpdateOp
name|update
parameter_list|)
block|{
return|return
name|store
operator|.
name|findAndUpdate
argument_list|(
name|collection
argument_list|,
name|update
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|CacheInvalidationStats
name|invalidateCache
parameter_list|()
block|{
return|return
name|store
operator|.
name|invalidateCache
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|CacheInvalidationStats
name|invalidateCache
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|)
block|{
return|return
name|store
operator|.
name|invalidateCache
argument_list|(
name|keys
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|invalidateCache
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|store
operator|.
name|invalidateCache
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|dispose
parameter_list|()
block|{
name|store
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|getIfCached
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
specifier|final
name|String
name|key
parameter_list|)
block|{
return|return
name|store
operator|.
name|getIfCached
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|setReadWriteMode
parameter_list|(
name|String
name|readWriteMode
parameter_list|)
block|{
name|store
operator|.
name|setReadWriteMode
argument_list|(
name|readWriteMode
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Iterable
argument_list|<
name|CacheStats
argument_list|>
name|getCacheStats
parameter_list|()
block|{
return|return
name|store
operator|.
name|getCacheStats
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|long
name|determineServerTimeDifferenceMillis
parameter_list|()
block|{
return|return
name|store
operator|.
name|determineServerTimeDifferenceMillis
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getMetadata
parameter_list|()
block|{
return|return
name|store
operator|.
name|getMetadata
argument_list|()
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getStats
parameter_list|()
block|{
return|return
name|store
operator|.
name|getStats
argument_list|()
return|;
block|}
block|}
end_class

end_unit

