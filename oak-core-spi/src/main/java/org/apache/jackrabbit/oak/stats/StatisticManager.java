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
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|whiteboard
operator|.
name|WhiteboardUtils
operator|.
name|registerMBean
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|whiteboard
operator|.
name|WhiteboardUtils
operator|.
name|scheduleWithFixedDelay
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|jmx
operator|.
name|QueryStatManagerMBean
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
name|api
operator|.
name|jmx
operator|.
name|RepositoryStatsMBean
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
name|spi
operator|.
name|whiteboard
operator|.
name|CompositeRegistration
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
name|spi
operator|.
name|whiteboard
operator|.
name|Whiteboard
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
name|spi
operator|.
name|whiteboard
operator|.
name|WhiteboardUtils
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
name|QueryStatImpl
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
name|TimeSeriesMax
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
name|jmx
operator|.
name|QueryStatManager
import|;
end_import

begin_comment
comment|/**  * Manager for all repository wide statistics.  * @see org.apache.jackrabbit.api.stats.RepositoryStatistics  * @see org.apache.jackrabbit.api.stats.QueryStat  */
end_comment

begin_class
specifier|public
class|class
name|StatisticManager
block|{
specifier|private
specifier|final
name|QueryStatImpl
name|queryStat
init|=
operator|new
name|QueryStatImpl
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|StatisticsProvider
name|repoStats
decl_stmt|;
specifier|private
specifier|final
name|TimeSeriesMax
name|maxQueueLength
decl_stmt|;
specifier|private
specifier|final
name|CounterStats
name|maxQueueLengthCounter
decl_stmt|;
specifier|private
specifier|final
name|CompositeRegistration
name|registration
decl_stmt|;
comment|/**      * Types for which Metrics based stats would not be collected      * and only default stats would be collected      */
specifier|private
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|Type
argument_list|>
name|NOOP_METRIC_TYPES
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|Type
operator|.
name|SESSION_READ_COUNTER
argument_list|,
name|Type
operator|.
name|SESSION_READ_DURATION
argument_list|,
name|Type
operator|.
name|SESSION_WRITE_DURATION
argument_list|,
name|Type
operator|.
name|QUERY_COUNT
argument_list|)
decl_stmt|;
comment|/**      * Create a new instance of this class registering all repository wide      * statistics with the passed {@code whiteboard}.      * @param whiteboard   whiteboard for registering the individual statistics with      */
specifier|public
name|StatisticManager
parameter_list|(
name|Whiteboard
name|whiteboard
parameter_list|,
name|ScheduledExecutorService
name|executor
parameter_list|)
block|{
name|queryStat
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|repoStats
operator|=
name|getStatsProvider
argument_list|(
name|whiteboard
argument_list|,
name|executor
argument_list|)
expr_stmt|;
name|maxQueueLengthCounter
operator|=
name|repoStats
operator|.
name|getCounterStats
argument_list|(
name|RepositoryStats
operator|.
name|OBSERVATION_QUEUE_MAX_LENGTH
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|maxQueueLength
operator|=
operator|new
name|TimeSeriesMax
argument_list|(
operator|-
literal|1
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|recordValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|super
operator|.
name|recordValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|long
name|currentValue
init|=
name|maxQueueLengthCounter
operator|.
name|getCount
argument_list|()
decl_stmt|;
name|maxQueueLengthCounter
operator|.
name|inc
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|value
argument_list|,
name|currentValue
argument_list|)
operator|-
name|currentValue
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
name|registration
operator|=
operator|new
name|CompositeRegistration
argument_list|(
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|QueryStatManagerMBean
operator|.
name|class
argument_list|,
operator|new
name|QueryStatManager
argument_list|(
name|queryStat
argument_list|)
argument_list|,
literal|"QueryStat"
argument_list|,
literal|"Oak Query Statistics"
argument_list|)
argument_list|,
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|RepositoryStatsMBean
operator|.
name|class
argument_list|,
operator|new
name|RepositoryStats
argument_list|(
name|repoStats
operator|.
name|getStats
argument_list|()
argument_list|,
name|maxQueueLength
argument_list|)
argument_list|,
name|RepositoryStats
operator|.
name|TYPE
argument_list|,
literal|"Oak Repository Statistics"
argument_list|)
argument_list|,
name|scheduleWithFixedDelay
argument_list|(
name|whiteboard
argument_list|,
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
name|maxQueueLength
operator|.
name|recordOneSecond
argument_list|()
expr_stmt|;
comment|// reset counter to missing value (-1)
name|maxQueueLengthCounter
operator|.
name|dec
argument_list|(
name|maxQueueLengthCounter
operator|.
name|getCount
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Logs the call of each query ran on the repository.      * @param language   the query language      * @param statement  the query      * @param millis     time it took to evaluate the query in milli seconds.      * @see org.apache.jackrabbit.stats.QueryStatCore#logQuery(java.lang.String, java.lang.String, long)      */
specifier|public
name|void
name|logQueryEvaluationTime
parameter_list|(
name|String
name|language
parameter_list|,
name|String
name|statement
parameter_list|,
name|long
name|millis
parameter_list|)
block|{
name|queryStat
operator|.
name|logQuery
argument_list|(
name|language
argument_list|,
name|statement
argument_list|,
name|millis
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MeterStats
name|getMeter
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
return|return
name|repoStats
operator|.
name|getMeter
argument_list|(
name|type
operator|.
name|name
argument_list|()
argument_list|,
name|getOption
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|CounterStats
name|getStatsCounter
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
return|return
name|repoStats
operator|.
name|getCounterStats
argument_list|(
name|type
operator|.
name|name
argument_list|()
argument_list|,
name|getOption
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|TimerStats
name|getTimer
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
return|return
name|repoStats
operator|.
name|getTimer
argument_list|(
name|type
operator|.
name|name
argument_list|()
argument_list|,
name|getOption
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|TimeSeriesMax
name|maxQueLengthRecorder
parameter_list|()
block|{
return|return
name|maxQueueLength
return|;
block|}
comment|/**      * Unregister all statistics previously registered with the whiteboard passed      * to the constructor.      */
specifier|public
name|void
name|dispose
parameter_list|()
block|{
name|registration
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
specifier|private
name|StatisticsProvider
name|getStatsProvider
parameter_list|(
name|Whiteboard
name|wb
parameter_list|,
name|ScheduledExecutorService
name|executor
parameter_list|)
block|{
name|StatisticsProvider
name|provider
init|=
name|WhiteboardUtils
operator|.
name|getService
argument_list|(
name|wb
argument_list|,
name|StatisticsProvider
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|provider
operator|==
literal|null
condition|)
block|{
name|provider
operator|=
operator|new
name|DefaultStatisticsProvider
argument_list|(
name|executor
argument_list|)
expr_stmt|;
block|}
return|return
name|provider
return|;
block|}
specifier|private
specifier|static
name|StatsOptions
name|getOption
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
if|if
condition|(
name|NOOP_METRIC_TYPES
operator|.
name|contains
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|StatsOptions
operator|.
name|TIME_SERIES_ONLY
return|;
block|}
return|return
name|StatsOptions
operator|.
name|DEFAULT
return|;
block|}
block|}
end_class

end_unit

