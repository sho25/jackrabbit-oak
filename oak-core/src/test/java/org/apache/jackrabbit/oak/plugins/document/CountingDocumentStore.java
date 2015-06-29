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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|UpdateOp
operator|.
name|Condition
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
operator|.
name|Key
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

begin_class
specifier|public
class|class
name|CountingDocumentStore
implements|implements
name|DocumentStore
block|{
specifier|private
name|DocumentStore
name|delegate
decl_stmt|;
comment|//TODO: remove mec
name|boolean
name|printStacks
decl_stmt|;
class|class
name|Stats
block|{
specifier|private
name|int
name|numFindCalls
decl_stmt|;
specifier|private
name|int
name|numQueryCalls
decl_stmt|;
specifier|private
name|int
name|numRemoveCalls
decl_stmt|;
specifier|private
name|int
name|numCreateOrUpdateCalls
decl_stmt|;
block|}
specifier|private
name|Map
argument_list|<
name|Collection
argument_list|,
name|Stats
argument_list|>
name|collectionStats
init|=
operator|new
name|HashMap
argument_list|<
name|Collection
argument_list|,
name|Stats
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|CountingDocumentStore
parameter_list|(
name|DocumentStore
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
specifier|public
name|void
name|resetCounters
parameter_list|()
block|{
name|collectionStats
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getNumFindCalls
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
return|return
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numFindCalls
return|;
block|}
specifier|public
name|int
name|getNumQueryCalls
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
return|return
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numQueryCalls
return|;
block|}
specifier|public
name|int
name|getNumRemoveCalls
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
return|return
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numRemoveCalls
return|;
block|}
specifier|public
name|int
name|getNumCreateOrUpdateCalls
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
return|return
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numCreateOrUpdateCalls
return|;
block|}
specifier|private
name|Stats
name|getStats
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
if|if
condition|(
operator|!
name|collectionStats
operator|.
name|containsKey
argument_list|(
name|collection
argument_list|)
condition|)
block|{
name|Stats
name|s
init|=
operator|new
name|Stats
argument_list|()
decl_stmt|;
name|collectionStats
operator|.
name|put
argument_list|(
name|collection
argument_list|,
name|s
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
else|else
block|{
return|return
name|collectionStats
operator|.
name|get
argument_list|(
name|collection
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
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
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numFindCalls
operator|++
expr_stmt|;
if|if
condition|(
name|printStacks
condition|)
block|{
operator|new
name|Exception
argument_list|(
literal|"find ["
operator|+
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numFindCalls
operator|+
literal|"] ("
operator|+
name|collection
operator|+
literal|") "
operator|+
name|key
argument_list|)
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
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
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numFindCalls
operator|++
expr_stmt|;
if|if
condition|(
name|printStacks
condition|)
block|{
operator|new
name|Exception
argument_list|(
literal|"find ["
operator|+
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numFindCalls
operator|+
literal|"] ("
operator|+
name|collection
operator|+
literal|") "
operator|+
name|key
operator|+
literal|" [max: "
operator|+
name|maxCacheAge
operator|+
literal|"]"
argument_list|)
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
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
name|Nonnull
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
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numQueryCalls
operator|++
expr_stmt|;
if|if
condition|(
name|printStacks
condition|)
block|{
operator|new
name|Exception
argument_list|(
literal|"query1 ["
operator|+
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numQueryCalls
operator|+
literal|"] ("
operator|+
name|collection
operator|+
literal|") "
operator|+
name|fromKey
operator|+
literal|", to "
operator|+
name|toKey
operator|+
literal|". limit "
operator|+
name|limit
argument_list|)
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
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
name|Nonnull
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
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numQueryCalls
operator|++
expr_stmt|;
if|if
condition|(
name|printStacks
condition|)
block|{
operator|new
name|Exception
argument_list|(
literal|"query2 ["
operator|+
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numQueryCalls
operator|+
literal|"] ("
operator|+
name|collection
operator|+
literal|") "
operator|+
name|fromKey
operator|+
literal|", to "
operator|+
name|toKey
operator|+
literal|". limit "
operator|+
name|limit
argument_list|)
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
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
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numRemoveCalls
operator|++
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
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numRemoveCalls
operator|++
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
name|Map
argument_list|<
name|Key
argument_list|,
name|Condition
argument_list|>
argument_list|>
name|toRemove
parameter_list|)
block|{
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numRemoveCalls
operator|++
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
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numCreateOrUpdateCalls
operator|++
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
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|update
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
parameter_list|,
name|UpdateOp
name|updateOp
parameter_list|)
block|{
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numCreateOrUpdateCalls
operator|++
expr_stmt|;
name|delegate
operator|.
name|update
argument_list|(
name|collection
argument_list|,
name|keys
argument_list|,
name|updateOp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numCreateOrUpdateCalls
operator|++
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
name|getStats
argument_list|(
name|collection
argument_list|)
operator|.
name|numCreateOrUpdateCalls
operator|++
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
name|CacheInvalidationStats
name|invalidateCache
parameter_list|()
block|{
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
name|void
name|dispose
parameter_list|()
block|{
name|delegate
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
name|void
name|setReadWriteMode
parameter_list|(
name|String
name|readWriteMode
parameter_list|)
block|{
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
name|CacheStats
name|getCacheStats
parameter_list|()
block|{
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
name|delegate
operator|.
name|getMetadata
argument_list|()
return|;
block|}
block|}
end_class

end_unit

