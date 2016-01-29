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
name|ArrayList
import|;
end_import

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
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|ConcurrentHashMap
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
name|atomic
operator|.
name|AtomicInteger
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

begin_comment
comment|/**  * A DocumentStore wrapper that can be used to log and also time DocumentStore  * calls.  */
end_comment

begin_class
specifier|public
class|class
name|TimingDocumentStoreWrapper
implements|implements
name|DocumentStore
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|DEBUG
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"base.debug"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|NEXT_ID
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|DocumentStore
name|base
decl_stmt|;
specifier|private
specifier|final
name|int
name|id
init|=
name|NEXT_ID
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
specifier|private
name|long
name|startTime
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Count
argument_list|>
name|counts
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Count
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|long
name|lastLogTime
decl_stmt|;
specifier|private
name|long
name|totalLogTime
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|slowCalls
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|callCount
decl_stmt|;
comment|/**      * A class that keeps track of timing data and call counts.      */
specifier|static
class|class
name|Count
block|{
specifier|public
name|long
name|count
decl_stmt|;
specifier|public
name|long
name|max
decl_stmt|;
specifier|public
name|long
name|total
decl_stmt|;
specifier|public
name|long
name|paramSize
decl_stmt|;
specifier|public
name|long
name|resultSize
decl_stmt|;
name|void
name|update
parameter_list|(
name|long
name|time
parameter_list|,
name|int
name|paramSize
parameter_list|,
name|int
name|resultSize
parameter_list|)
block|{
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|time
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|time
expr_stmt|;
block|}
name|total
operator|+=
name|time
expr_stmt|;
name|this
operator|.
name|paramSize
operator|+=
name|paramSize
expr_stmt|;
name|this
operator|.
name|resultSize
operator|+=
name|resultSize
expr_stmt|;
block|}
block|}
specifier|public
name|TimingDocumentStoreWrapper
parameter_list|(
name|DocumentStore
name|base
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|lastLogTime
operator|=
name|now
argument_list|()
expr_stmt|;
block|}
specifier|private
name|boolean
name|logCommonCall
parameter_list|()
block|{
return|return
name|callCount
operator|%
literal|10
operator|==
literal|0
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
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
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|T
name|result
init|=
name|base
operator|.
name|find
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
decl_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"find"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
name|size
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|logCommonCall
argument_list|()
condition|)
block|{
name|logCommonCall
argument_list|(
name|start
argument_list|,
literal|"find "
operator|+
name|collection
operator|+
literal|" "
operator|+
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
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
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|T
name|result
init|=
name|base
operator|.
name|find
argument_list|(
name|collection
argument_list|,
name|key
argument_list|,
name|maxCacheAge
argument_list|)
decl_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"find2"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
name|size
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|logCommonCall
argument_list|()
condition|)
block|{
name|logCommonCall
argument_list|(
name|start
argument_list|,
literal|"find2 "
operator|+
name|collection
operator|+
literal|" "
operator|+
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
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
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|T
argument_list|>
name|result
init|=
name|base
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
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|updateAndLogTimes
argument_list|(
literal|"query, result=0"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
name|size
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|result
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|updateAndLogTimes
argument_list|(
literal|"query, result=1"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
name|size
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|updateAndLogTimes
argument_list|(
literal|"query, result>1"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
name|size
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|logCommonCall
argument_list|()
condition|)
block|{
name|logCommonCall
argument_list|(
name|start
argument_list|,
literal|"query "
operator|+
name|collection
operator|+
literal|" "
operator|+
name|fromKey
operator|+
literal|" "
operator|+
name|toKey
operator|+
literal|" "
operator|+
name|limit
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
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
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|T
argument_list|>
name|result
init|=
name|base
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
decl_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"query2"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
name|size
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|logCommonCall
argument_list|()
condition|)
block|{
name|logCommonCall
argument_list|(
name|start
argument_list|,
literal|"query2 "
operator|+
name|collection
operator|+
literal|" "
operator|+
name|fromKey
operator|+
literal|" "
operator|+
name|toKey
operator|+
literal|" "
operator|+
name|indexedProperty
operator|+
literal|" "
operator|+
name|startValue
operator|+
literal|" "
operator|+
name|limit
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
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
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|base
operator|.
name|remove
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"remove"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|logCommonCall
argument_list|()
condition|)
block|{
name|logCommonCall
argument_list|(
name|start
argument_list|,
literal|"remove "
operator|+
name|collection
operator|+
literal|" "
operator|+
name|key
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
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
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|base
operator|.
name|remove
argument_list|(
name|collection
argument_list|,
name|keys
argument_list|)
expr_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"remove"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|logCommonCall
argument_list|()
condition|)
block|{
name|logCommonCall
argument_list|(
name|start
argument_list|,
literal|"remove "
operator|+
name|collection
operator|+
literal|" "
operator|+
name|keys
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
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
name|UpdateOp
operator|.
name|Key
argument_list|,
name|UpdateOp
operator|.
name|Condition
argument_list|>
argument_list|>
name|toRemove
parameter_list|)
block|{
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|int
name|result
init|=
name|base
operator|.
name|remove
argument_list|(
name|collection
argument_list|,
name|toRemove
argument_list|)
decl_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"remove"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|logCommonCall
argument_list|()
condition|)
block|{
name|logCommonCall
argument_list|(
name|start
argument_list|,
literal|"remove "
operator|+
name|collection
operator|+
literal|" "
operator|+
name|toRemove
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
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
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|boolean
name|result
init|=
name|base
operator|.
name|create
argument_list|(
name|collection
argument_list|,
name|updateOps
argument_list|)
decl_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"create"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|logCommonCall
argument_list|()
condition|)
block|{
name|logCommonCall
argument_list|(
name|start
argument_list|,
literal|"create "
operator|+
name|collection
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
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
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|base
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
name|updateAndLogTimes
argument_list|(
literal|"update"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|logCommonCall
argument_list|()
condition|)
block|{
name|logCommonCall
argument_list|(
name|start
argument_list|,
literal|"update "
operator|+
name|collection
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
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
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|T
name|result
init|=
name|base
operator|.
name|createOrUpdate
argument_list|(
name|collection
argument_list|,
name|update
argument_list|)
decl_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"createOrUpdate"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
name|size
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|logCommonCall
argument_list|()
condition|)
block|{
name|logCommonCall
argument_list|(
name|start
argument_list|,
literal|"createOrUpdate "
operator|+
name|collection
operator|+
literal|" "
operator|+
name|update
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
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
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|T
argument_list|>
name|result
init|=
name|base
operator|.
name|createOrUpdate
argument_list|(
name|collection
argument_list|,
name|updateOps
argument_list|)
decl_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"createOrUpdate"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
name|size
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|logCommonCall
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|UpdateOp
name|op
range|:
name|updateOps
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|op
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|logCommonCall
argument_list|(
name|start
argument_list|,
literal|"createOrUpdate "
operator|+
name|collection
operator|+
literal|" "
operator|+
name|updateOps
operator|+
literal|" "
operator|+
name|ids
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
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
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|T
name|result
init|=
name|base
operator|.
name|findAndUpdate
argument_list|(
name|collection
argument_list|,
name|update
argument_list|)
decl_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"findAndUpdate"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
name|size
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|logCommonCall
argument_list|()
condition|)
block|{
name|logCommonCall
argument_list|(
name|start
argument_list|,
literal|"findAndUpdate "
operator|+
name|collection
operator|+
literal|" "
operator|+
name|update
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|CacheInvalidationStats
name|invalidateCache
parameter_list|()
block|{
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|CacheInvalidationStats
name|result
init|=
name|base
operator|.
name|invalidateCache
argument_list|()
decl_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"invalidateCache"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|CacheInvalidationStats
name|result
init|=
name|base
operator|.
name|invalidateCache
argument_list|(
name|keys
argument_list|)
decl_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"invalidateCache3"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
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
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|base
operator|.
name|invalidateCache
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"invalidateCache2"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|()
block|{
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|base
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"dispose"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
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
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|T
name|result
init|=
name|base
operator|.
name|getIfCached
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
decl_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"isCached"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|base
operator|.
name|setReadWriteMode
argument_list|(
name|readWriteMode
argument_list|)
expr_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"setReadWriteMode"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|CacheStats
argument_list|>
name|getCacheStats
parameter_list|()
block|{
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|CacheStats
argument_list|>
name|result
init|=
name|base
operator|.
name|getCacheStats
argument_list|()
decl_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"getCacheStats"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
name|base
operator|.
name|getMetadata
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
try|try
block|{
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|long
name|result
init|=
name|base
operator|.
name|determineServerTimeDifferenceMillis
argument_list|()
decl_stmt|;
name|updateAndLogTimes
argument_list|(
literal|"determineServerTimeDifferenceMillis"
argument_list|,
name|start
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|logCommonCall
parameter_list|(
name|long
name|start
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|int
name|time
init|=
call|(
name|int
call|)
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
decl_stmt|;
if|if
condition|(
name|time
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|map
init|=
name|slowCalls
decl_stmt|;
name|Integer
name|oldCount
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldCount
operator|==
literal|null
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|time
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|oldCount
operator|+
name|time
argument_list|)
expr_stmt|;
block|}
name|int
name|maxElements
init|=
literal|1000
decl_stmt|;
name|int
name|minCount
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|map
operator|.
name|size
argument_list|()
operator|>
name|maxElements
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|ei
init|=
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|ei
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|e
init|=
name|ei
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|<=
name|minCount
condition|)
block|{
name|ei
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|map
operator|.
name|size
argument_list|()
operator|>
name|maxElements
condition|)
block|{
name|minCount
operator|++
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|RuntimeException
name|convert
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|RuntimeException
condition|)
block|{
return|return
operator|(
name|RuntimeException
operator|)
name|e
return|;
block|}
return|return
operator|new
name|DocumentStoreException
argument_list|(
literal|"Unexpected exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
return|;
block|}
specifier|private
name|void
name|log
parameter_list|(
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|id
operator|+
literal|"] "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|int
name|size
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|)
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
for|for
control|(
name|T
name|doc
range|:
name|list
control|)
block|{
name|result
operator|+=
name|size
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|int
name|size
parameter_list|(
annotation|@
name|Nullable
name|Document
name|document
parameter_list|)
block|{
if|if
condition|(
name|document
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|document
operator|.
name|getMemory
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
name|long
name|now
parameter_list|()
block|{
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
return|;
block|}
specifier|private
name|void
name|updateAndLogTimes
parameter_list|(
name|String
name|operation
parameter_list|,
name|long
name|start
parameter_list|,
name|int
name|paramSize
parameter_list|,
name|int
name|resultSize
parameter_list|)
block|{
name|long
name|now
init|=
name|now
argument_list|()
decl_stmt|;
if|if
condition|(
name|startTime
operator|==
literal|0
condition|)
block|{
name|startTime
operator|=
name|now
expr_stmt|;
block|}
name|Count
name|c
init|=
name|counts
operator|.
name|get
argument_list|(
name|operation
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
name|c
operator|=
operator|new
name|Count
argument_list|()
expr_stmt|;
name|counts
operator|.
name|put
argument_list|(
name|operation
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
name|c
operator|.
name|update
argument_list|(
name|now
operator|-
name|start
argument_list|,
name|paramSize
argument_list|,
name|resultSize
argument_list|)
expr_stmt|;
name|long
name|t
init|=
name|now
operator|-
name|lastLogTime
decl_stmt|;
if|if
condition|(
name|t
operator|>=
literal|10000
condition|)
block|{
name|totalLogTime
operator|+=
name|t
expr_stmt|;
name|lastLogTime
operator|=
name|now
expr_stmt|;
name|long
name|totalCount
init|=
literal|0
decl_stmt|,
name|totalTime
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Count
name|count
range|:
name|counts
operator|.
name|values
argument_list|()
control|)
block|{
name|totalCount
operator|+=
name|count
operator|.
name|count
expr_stmt|;
name|totalTime
operator|+=
name|count
operator|.
name|total
expr_stmt|;
block|}
name|totalCount
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|totalCount
argument_list|)
expr_stmt|;
name|totalTime
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|totalTime
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Count
argument_list|>
name|e
range|:
name|counts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|c
operator|=
name|e
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|long
name|count
init|=
name|c
operator|.
name|count
decl_stmt|;
name|long
name|total
init|=
name|c
operator|.
name|total
decl_stmt|;
name|long
name|in
init|=
name|c
operator|.
name|paramSize
operator|/
literal|1024
operator|/
literal|1024
decl_stmt|;
name|long
name|out
init|=
name|c
operator|.
name|resultSize
operator|/
literal|1024
operator|/
literal|1024
decl_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|log
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
operator|+
literal|" count "
operator|+
name|count
operator|+
literal|" "
operator|+
operator|(
literal|100
operator|*
name|count
operator|/
name|totalCount
operator|)
operator|+
literal|"%"
operator|+
literal|" in "
operator|+
name|in
operator|+
literal|" out "
operator|+
name|out
operator|+
literal|" time "
operator|+
name|total
operator|+
literal|" "
operator|+
operator|(
literal|100
operator|*
name|total
operator|/
name|totalTime
operator|)
operator|+
literal|"%"
argument_list|)
expr_stmt|;
block|}
block|}
name|log
argument_list|(
literal|"all count "
operator|+
name|totalCount
operator|+
literal|" time "
operator|+
name|totalTime
operator|+
literal|" "
operator|+
operator|(
literal|100
operator|*
name|totalTime
operator|/
name|totalLogTime
operator|)
operator|+
literal|"%"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|map
init|=
name|slowCalls
decl_stmt|;
name|int
name|top
init|=
literal|10
decl_stmt|;
name|int
name|max
init|=
name|Integer
operator|.
name|MAX_VALUE
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
name|top
condition|;
control|)
block|{
name|int
name|best
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|x
range|:
name|map
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|x
argument_list|<
name|max
operator|&&
name|x
argument_list|>
name|best
condition|)
block|{
name|best
operator|=
name|x
expr_stmt|;
block|}
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|e
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|>=
name|best
operator|&&
name|e
operator|.
name|getValue
argument_list|()
operator|<
name|max
condition|)
block|{
name|log
argument_list|(
literal|"slow call "
operator|+
name|e
operator|.
name|getValue
argument_list|()
operator|+
literal|" millis: "
operator|+
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|i
operator|>=
name|top
condition|)
block|{
break|break;
block|}
block|}
block|}
if|if
condition|(
name|i
operator|>=
name|map
operator|.
name|size
argument_list|()
condition|)
block|{
break|break;
block|}
name|max
operator|=
name|best
expr_stmt|;
block|}
name|slowCalls
operator|.
name|clear
argument_list|()
expr_stmt|;
name|log
argument_list|(
literal|"------"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

