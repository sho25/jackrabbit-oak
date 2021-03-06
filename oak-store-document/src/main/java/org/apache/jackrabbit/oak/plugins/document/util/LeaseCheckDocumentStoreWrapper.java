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
name|ClusterNodeInfo
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
comment|/**  * Wrapper of another DocumentStore that does a lease check on any method  * invocation (read or update) and fails if the lease is not valid.  *<p>  * @see "https://issues.apache.org/jira/browse/OAK-2739 for more details"  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|LeaseCheckDocumentStoreWrapper
implements|implements
name|DocumentStore
block|{
specifier|private
specifier|final
name|DocumentStore
name|delegate
decl_stmt|;
specifier|private
specifier|final
name|ClusterNodeInfo
name|clusterNodeInfo
decl_stmt|;
specifier|public
name|LeaseCheckDocumentStoreWrapper
parameter_list|(
specifier|final
name|DocumentStore
name|delegate
parameter_list|,
specifier|final
name|ClusterNodeInfo
name|clusterNodeInfo
parameter_list|)
block|{
if|if
condition|(
name|delegate
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"delegate must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
comment|// clusterNodeInfo is allowed to be null - eg for testing
name|this
operator|.
name|clusterNodeInfo
operator|=
name|clusterNodeInfo
expr_stmt|;
block|}
specifier|private
specifier|final
name|void
name|performLeaseCheck
parameter_list|()
block|{
if|if
condition|(
name|clusterNodeInfo
operator|!=
literal|null
condition|)
block|{
name|clusterNodeInfo
operator|.
name|performLeaseCheck
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|final
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|find
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
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
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
specifier|final
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|find
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|,
name|int
name|maxCacheAge
parameter_list|)
block|{
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
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
specifier|public
specifier|final
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
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|fromKey
parameter_list|,
name|String
name|toKey
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
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
specifier|public
specifier|final
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
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|fromKey
parameter_list|,
name|String
name|toKey
parameter_list|,
name|String
name|indexedProperty
parameter_list|,
name|long
name|startValue
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
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
specifier|final
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
name|performLeaseCheck
argument_list|()
expr_stmt|;
name|delegate
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
specifier|final
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
name|performLeaseCheck
argument_list|()
expr_stmt|;
name|delegate
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
specifier|final
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
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
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
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
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
specifier|final
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|boolean
name|create
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
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
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
specifier|final
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|createOrUpdate
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|UpdateOp
name|update
parameter_list|)
block|{
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
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
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
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
specifier|final
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|findAndUpdate
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|UpdateOp
name|update
parameter_list|)
block|{
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
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
specifier|final
name|CacheInvalidationStats
name|invalidateCache
parameter_list|()
block|{
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|invalidateCache
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
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
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
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
specifier|final
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
name|performLeaseCheck
argument_list|()
expr_stmt|;
name|delegate
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
specifier|final
name|void
name|dispose
parameter_list|()
block|{
comment|// this is debatable whether or not a lease check should be done on dispose.
comment|// I'd say the lease must still be valid as on dispose there could be
comment|// stuff written to the document store which should only be done
comment|// when the lease is valid.
comment|// however.. dispose() is also called as a result of the 'failed lease check stopping'
comment|// mechanism - and in that case this would just throw an exception and the
comment|// DocumentNodeStore.dispose() would not correctly finish.
comment|// so: let's let the dispose ignore the lease state
name|delegate
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|getIfCached
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
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
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
specifier|final
name|void
name|setReadWriteMode
parameter_list|(
name|String
name|readWriteMode
parameter_list|)
block|{
name|performLeaseCheck
argument_list|()
expr_stmt|;
name|delegate
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
specifier|final
name|Iterable
argument_list|<
name|CacheStats
argument_list|>
name|getCacheStats
parameter_list|()
block|{
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|getCacheStats
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getMetadata
parameter_list|()
block|{
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
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
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getStats
parameter_list|()
block|{
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|getStats
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|determineServerTimeDifferenceMillis
parameter_list|()
block|{
name|performLeaseCheck
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|determineServerTimeDifferenceMillis
argument_list|()
return|;
block|}
block|}
end_class

end_unit

