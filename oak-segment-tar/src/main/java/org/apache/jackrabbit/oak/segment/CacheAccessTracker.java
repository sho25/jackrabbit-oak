begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
name|CounterStats
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
name|StatisticsProvider
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
name|StatsOptions
import|;
end_import

begin_comment
comment|/**  * {@code Cache} wrapper exposing the number of read accesses and the  * number of misses ot the underlying cache via the {@link StatisticsProvider}.  */
end_comment

begin_class
specifier|public
class|class
name|CacheAccessTracker
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
block|{
specifier|private
specifier|final
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|delegate
decl_stmt|;
specifier|private
specifier|final
name|CounterStats
name|accessCount
decl_stmt|;
specifier|private
specifier|final
name|CounterStats
name|missCount
decl_stmt|;
comment|/**      * Create a new wrapper exposing the access statistics under the given      * {@code name} to the passed {@code statisticsProvider}.      * @param name                 name under which to expose the access statistics      * @param statisticsProvider   statistics provider where the access statistics is recorded to      * @param delegate             the underlying, wrapped cache.      */
specifier|public
name|CacheAccessTracker
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|StatisticsProvider
name|statisticsProvider
parameter_list|,
annotation|@
name|Nonnull
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|accessCount
operator|=
name|statisticsProvider
operator|.
name|getCounterStats
argument_list|(
name|name
operator|+
literal|".access-count"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|missCount
operator|=
name|statisticsProvider
operator|.
name|getCounterStats
argument_list|(
name|name
operator|+
literal|".miss-count"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
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
parameter_list|)
block|{
name|delegate
operator|.
name|put
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
name|void
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
name|byte
name|cost
parameter_list|)
block|{
name|delegate
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
name|cost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|V
name|get
parameter_list|(
annotation|@
name|Nonnull
name|K
name|key
parameter_list|)
block|{
name|V
name|v
init|=
name|delegate
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|accessCount
operator|.
name|inc
argument_list|()
expr_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|missCount
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
return|return
name|v
return|;
block|}
block|}
end_class

end_unit

