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
name|metric
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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
name|Queue
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
name|ConcurrentLinkedDeque
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
name|CountDownLatch
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
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MalformedObjectNameException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectInstance
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|QueryExp
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|JmxReporter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Uninterruptibles
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
operator|.
name|Type
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
name|HistogramStats
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
name|MeterStats
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
name|NoopStats
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
name|SimpleStats
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
name|assertNotEquals
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
name|assertTrue
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
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|MetricStatisticsProviderTest
block|{
specifier|private
name|MBeanServer
name|server
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
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
name|MetricStatisticsProvider
name|statsProvider
init|=
operator|new
name|MetricStatisticsProvider
argument_list|(
name|server
argument_list|,
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
comment|//By default avg counters would be configured. So check if they are
comment|//configured
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|statsProvider
operator|.
name|getRegistry
argument_list|()
operator|.
name|getMeters
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|statsProvider
operator|.
name|getRegistry
argument_list|()
operator|.
name|getTimers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|statsProvider
operator|.
name|getStats
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|statsProvider
operator|.
name|getRegistry
argument_list|()
operator|.
name|getMetrics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|getMetricMbeans
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|statsProvider
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getMetricMbeans
argument_list|()
operator|.
name|size
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
name|assertNotNull
argument_list|(
name|statsProvider
operator|.
name|getRegistry
argument_list|()
operator|.
name|getMeters
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|CompositeStats
operator|)
name|meterStats
operator|)
operator|.
name|isMeter
argument_list|()
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
name|assertNotNull
argument_list|(
name|statsProvider
operator|.
name|getRegistry
argument_list|()
operator|.
name|getCounters
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|CompositeStats
operator|)
name|counterStats
operator|)
operator|.
name|isCounter
argument_list|()
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
name|assertNotNull
argument_list|(
name|statsProvider
operator|.
name|getRegistry
argument_list|()
operator|.
name|getTimers
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|CompositeStats
operator|)
name|timerStats
operator|)
operator|.
name|isTimer
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|histogram
parameter_list|()
throws|throws
name|Exception
block|{
name|HistogramStats
name|histoStats
init|=
name|statsProvider
operator|.
name|getHistogram
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
name|histoStats
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|statsProvider
operator|.
name|getRegistry
argument_list|()
operator|.
name|getHistograms
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|CompositeStats
operator|)
name|histoStats
operator|)
operator|.
name|isHistogram
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|timeSeriesIntegration
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
name|meterStats
operator|.
name|mark
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|statsProvider
operator|.
name|getRepoStats
argument_list|()
operator|.
name|getCounter
argument_list|(
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
name|jmxNaming
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
literal|"hello"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|server
operator|.
name|getObjectInstance
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.jackrabbit.oak:type=Metrics,name=hello"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noopMeter
parameter_list|()
throws|throws
name|Exception
block|{
name|assertInstanceOf
argument_list|(
name|statsProvider
operator|.
name|getTimer
argument_list|(
name|Type
operator|.
name|SESSION_READ_DURATION
operator|.
name|name
argument_list|()
argument_list|,
name|StatsOptions
operator|.
name|TIME_SERIES_ONLY
argument_list|)
argument_list|,
name|SimpleStats
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|statsProvider
operator|.
name|getMeter
argument_list|(
name|Type
operator|.
name|OBSERVATION_EVENT_COUNTER
operator|.
name|name
argument_list|()
argument_list|,
name|StatsOptions
operator|.
name|TIME_SERIES_ONLY
argument_list|)
argument_list|,
name|NoopStats
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|statsOptions_MetricOnly
parameter_list|()
throws|throws
name|Exception
block|{
name|assertInstanceOf
argument_list|(
name|statsProvider
operator|.
name|getTimer
argument_list|(
literal|"fooTimer"
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
argument_list|,
name|TimerImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertInstanceOf
argument_list|(
name|statsProvider
operator|.
name|getCounterStats
argument_list|(
literal|"fooCounter"
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
argument_list|,
name|CounterImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertInstanceOf
argument_list|(
name|statsProvider
operator|.
name|getMeter
argument_list|(
literal|"fooMeter"
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
argument_list|,
name|MeterImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertInstanceOf
argument_list|(
name|statsProvider
operator|.
name|getHistogram
argument_list|(
literal|"fooHisto"
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
argument_list|,
name|HistogramImpl
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|statsOptions_TimeSeriesOnly
parameter_list|()
throws|throws
name|Exception
block|{
name|assertInstanceOf
argument_list|(
name|statsProvider
operator|.
name|getTimer
argument_list|(
literal|"fooTimer"
argument_list|,
name|StatsOptions
operator|.
name|TIME_SERIES_ONLY
argument_list|)
argument_list|,
name|SimpleStats
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|statsOptions_Default
parameter_list|()
throws|throws
name|Exception
block|{
name|assertInstanceOf
argument_list|(
name|statsProvider
operator|.
name|getTimer
argument_list|(
literal|"fooTimer"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
argument_list|,
name|CompositeStats
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|concurrentAccess
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Queue is used to collect instances with minimal overhead in concurrent scenario
specifier|final
name|Queue
argument_list|<
name|MeterStats
argument_list|>
name|statsQueue
init|=
operator|new
name|ConcurrentLinkedDeque
argument_list|<
name|MeterStats
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|int
name|numWorker
init|=
literal|5
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
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
name|numWorker
condition|;
name|i
operator|++
control|)
block|{
name|threads
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Uninterruptibles
operator|.
name|awaitUninterruptibly
argument_list|(
name|latch
argument_list|)
expr_stmt|;
name|statsQueue
operator|.
name|add
argument_list|(
name|statsProvider
operator|.
name|getMeter
argument_list|(
literal|"foo"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
comment|//Assert that we get same reference for every call
name|Set
argument_list|<
name|MeterStats
argument_list|>
name|statsSet
init|=
name|Sets
operator|.
name|newIdentityHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|MeterStats
name|m
range|:
name|statsQueue
control|)
block|{
name|statsSet
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|statsSet
operator|.
name|size
argument_list|()
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
name|statsProvider
operator|.
name|close
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Set
argument_list|<
name|ObjectInstance
argument_list|>
name|getMetricMbeans
parameter_list|()
throws|throws
name|MalformedObjectNameException
block|{
name|QueryExp
name|q
init|=
name|Query
operator|.
name|isInstanceOf
argument_list|(
name|Query
operator|.
name|value
argument_list|(
name|JmxReporter
operator|.
name|MetricMBean
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|server
operator|.
name|queryMBeans
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.jackrabbit.oak:*"
argument_list|)
argument_list|,
name|q
argument_list|)
return|;
block|}
specifier|private
name|void
name|assertInstanceOf
parameter_list|(
name|Object
name|o
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
operator|!
name|clazz
operator|.
name|isInstance
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s is not an instance of %s"
argument_list|,
name|o
operator|.
name|getClass
argument_list|()
argument_list|,
name|clazz
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

