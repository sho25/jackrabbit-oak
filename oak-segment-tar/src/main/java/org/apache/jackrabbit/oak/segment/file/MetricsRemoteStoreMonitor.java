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
operator|.
name|file
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
name|segment
operator|.
name|spi
operator|.
name|monitor
operator|.
name|RemoteStoreMonitorAdapter
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
name|TimerStats
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

begin_class
specifier|public
class|class
name|MetricsRemoteStoreMonitor
extends|extends
name|RemoteStoreMonitorAdapter
block|{
specifier|public
specifier|static
specifier|final
name|String
name|REQUEST_COUNT
init|=
literal|"REQUEST_COUNT"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|REQUEST_ERROR
init|=
literal|"REQUEST_ERROR"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|REQUEST_DURATION
init|=
literal|"REQUEST_DURATION"
decl_stmt|;
specifier|private
specifier|final
name|CounterStats
name|requestCountStats
decl_stmt|;
specifier|private
specifier|final
name|CounterStats
name|requestErrorStats
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|requestDurationStats
decl_stmt|;
specifier|public
name|MetricsRemoteStoreMonitor
parameter_list|(
annotation|@
name|NotNull
name|StatisticsProvider
name|statisticsProvider
parameter_list|)
block|{
name|requestCountStats
operator|=
name|statisticsProvider
operator|.
name|getCounterStats
argument_list|(
name|REQUEST_COUNT
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|requestErrorStats
operator|=
name|statisticsProvider
operator|.
name|getCounterStats
argument_list|(
name|REQUEST_ERROR
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|requestDurationStats
operator|=
name|statisticsProvider
operator|.
name|getTimer
argument_list|(
name|REQUEST_DURATION
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|requestCount
parameter_list|()
block|{
name|requestCountStats
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|requestError
parameter_list|()
block|{
name|requestErrorStats
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|requestDuration
parameter_list|(
name|long
name|duration
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
block|{
name|requestDurationStats
operator|.
name|update
argument_list|(
name|duration
argument_list|,
name|timeUnit
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

