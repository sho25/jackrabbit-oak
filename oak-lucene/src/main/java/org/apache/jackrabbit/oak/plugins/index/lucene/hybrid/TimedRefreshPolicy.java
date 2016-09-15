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
name|index
operator|.
name|lucene
operator|.
name|hybrid
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|AtomicBoolean
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
name|stats
operator|.
name|Clock
import|;
end_import

begin_class
specifier|public
class|class
name|TimedRefreshPolicy
implements|implements
name|ReaderRefreshPolicy
block|{
specifier|private
specifier|final
name|AtomicBoolean
name|dirty
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|syncIndexingMode
decl_stmt|;
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
specifier|private
specifier|final
name|long
name|refreshDelta
decl_stmt|;
specifier|private
specifier|volatile
name|long
name|lastRefreshTime
decl_stmt|;
specifier|public
name|TimedRefreshPolicy
parameter_list|(
name|boolean
name|syncIndexingMode
parameter_list|,
name|Clock
name|clock
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|long
name|refreshDelta
parameter_list|)
block|{
name|this
operator|.
name|syncIndexingMode
operator|=
name|syncIndexingMode
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
name|this
operator|.
name|refreshDelta
operator|=
name|unit
operator|.
name|toMillis
argument_list|(
name|refreshDelta
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|refreshOnReadIfRequired
parameter_list|(
name|Runnable
name|refreshCallback
parameter_list|)
block|{
if|if
condition|(
name|syncIndexingMode
condition|)
block|{
comment|//As writer itself refreshes the index. No refresh done
comment|//on read
return|return;
block|}
name|refreshIfRequired
argument_list|(
name|refreshCallback
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|refreshOnWriteIfRequired
parameter_list|(
name|Runnable
name|refreshCallback
parameter_list|)
block|{
if|if
condition|(
name|syncIndexingMode
condition|)
block|{
comment|//For sync indexing mode we refresh the reader immediately
comment|//on the writer thread. So that any read call later sees upto date index
comment|//Another possibility is to refresh the readers upon first query post index update
comment|//but that would mean that if multiple queries get invoked simultaneously then
comment|//others would get blocked. So here we take hit on write side. If that proves to
comment|//be problematic query side refresh can be looked into
if|if
condition|(
name|dirty
operator|.
name|get
argument_list|()
condition|)
block|{
name|refreshCallback
operator|.
name|run
argument_list|()
expr_stmt|;
name|dirty
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|refreshIfRequired
argument_list|(
name|refreshCallback
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|updated
parameter_list|()
block|{
name|dirty
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|refreshIfRequired
parameter_list|(
name|Runnable
name|refreshCallback
parameter_list|)
block|{
if|if
condition|(
name|dirty
operator|.
name|get
argument_list|()
condition|)
block|{
name|long
name|currentTime
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentTime
operator|-
name|lastRefreshTime
operator|>
name|refreshDelta
operator|&&
name|dirty
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|lastRefreshTime
operator|=
name|currentTime
expr_stmt|;
name|refreshCallback
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

