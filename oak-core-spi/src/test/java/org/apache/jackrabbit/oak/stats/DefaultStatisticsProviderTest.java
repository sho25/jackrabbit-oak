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
name|stats
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
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
name|Executors
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
name|ScheduledExecutorService
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
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
name|api
operator|.
name|stats
operator|.
name|TimeSeries
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
name|stats
operator|.
name|RepositoryStatisticsImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|DefaultStatisticsProviderTest
block|{
specifier|private
name|ScheduledExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
decl_stmt|;
specifier|private
name|DefaultStatisticsProvider
name|statsProvider
init|=
operator|new
name|DefaultStatisticsProvider
argument_list|(
name|executorService
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|basicSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|statsProvider
operator|.
name|getStats
argument_list|()
argument_list|)
expr_stmt|;
name|CounterStats
name|stats
init|=
name|statsProvider
operator|.
name|getCounterStats
argument_list|(
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|SESSION_COUNT
operator|.
name|name
argument_list|()
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|stats
operator|.
name|inc
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|statsImpl
argument_list|(
name|statsProvider
argument_list|)
operator|.
name|getCounter
argument_list|(
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|SESSION_COUNT
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|meter
parameter_list|()
throws|throws
name|Exception
block|{
name|MeterStats
name|meterStats
init|=
name|statsProvider
operator|.
name|getMeter
argument_list|(
literal|"test"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|meterStats
argument_list|)
expr_stmt|;
name|meterStats
operator|.
name|mark
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|statsImpl
argument_list|(
name|statsProvider
argument_list|)
operator|.
name|getCounter
argument_list|(
literal|"test"
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getRegisteredTimeSeries
argument_list|(
name|statsProvider
argument_list|)
operator|.
name|contains
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|counter
parameter_list|()
throws|throws
name|Exception
block|{
name|CounterStats
name|counterStats
init|=
name|statsProvider
operator|.
name|getCounterStats
argument_list|(
literal|"test"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|counterStats
argument_list|)
expr_stmt|;
name|counterStats
operator|.
name|inc
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|statsImpl
argument_list|(
name|statsProvider
argument_list|)
operator|.
name|getCounter
argument_list|(
literal|"test"
argument_list|,
literal|false
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getRegisteredTimeSeries
argument_list|(
name|statsProvider
argument_list|)
operator|.
name|contains
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|timer
parameter_list|()
throws|throws
name|Exception
block|{
name|TimerStats
name|timerStats
init|=
name|statsProvider
operator|.
name|getTimer
argument_list|(
literal|"test"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|timerStats
argument_list|)
expr_stmt|;
name|timerStats
operator|.
name|update
argument_list|(
literal|100
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|100
argument_list|)
argument_list|,
name|statsImpl
argument_list|(
name|statsProvider
argument_list|)
operator|.
name|getCounter
argument_list|(
literal|"test"
argument_list|,
literal|false
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getRegisteredTimeSeries
argument_list|(
name|statsProvider
argument_list|)
operator|.
name|contains
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|metricOnly
parameter_list|()
throws|throws
name|Exception
block|{
name|MeterStats
name|meterStats
init|=
name|statsProvider
operator|.
name|getMeter
argument_list|(
literal|"test"
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|getRegisteredTimeSeries
argument_list|(
name|statsProvider
argument_list|)
operator|.
name|contains
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
specifier|private
name|RepositoryStatisticsImpl
name|statsImpl
parameter_list|(
name|DefaultStatisticsProvider
name|statsProvider
parameter_list|)
block|{
return|return
operator|(
name|RepositoryStatisticsImpl
operator|)
name|statsProvider
operator|.
name|getStats
argument_list|()
return|;
block|}
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getRegisteredTimeSeries
parameter_list|(
name|DefaultStatisticsProvider
name|statsProvider
parameter_list|)
block|{
name|RepositoryStatisticsImpl
name|stats
init|=
operator|(
name|RepositoryStatisticsImpl
operator|)
name|statsProvider
operator|.
name|getStats
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|TimeSeries
argument_list|>
name|e
range|:
name|stats
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|names
return|;
block|}
block|}
end_class

end_unit

