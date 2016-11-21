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
name|segment
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
name|stats
operator|.
name|TimeSeriesStatsUtil
operator|.
name|asCompositeData
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
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
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

begin_class
specifier|public
class|class
name|SegmentNodeStoreStats
implements|implements
name|SegmentNodeStoreStatsMBean
implements|,
name|SegmentNodeStoreMonitor
block|{
specifier|public
specifier|static
specifier|final
name|String
name|COMMITS_COUNT
init|=
literal|"COMMITS_COUNT"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT_QUEUE_SIZE
init|=
literal|"COMMIT_QUEUE_SIZE"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT_TIME
init|=
literal|"COMMIT_TIME"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|QUEUEING_TIME
init|=
literal|"QUEUEING_TIME"
decl_stmt|;
specifier|private
specifier|final
name|StatisticsProvider
name|statisticsProvider
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|commitsCount
decl_stmt|;
specifier|private
specifier|final
name|CounterStats
name|commitQueueSize
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|commitTime
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|queueingTime
decl_stmt|;
specifier|public
name|SegmentNodeStoreStats
parameter_list|(
name|StatisticsProvider
name|statisticsProvider
parameter_list|)
block|{
name|this
operator|.
name|statisticsProvider
operator|=
name|statisticsProvider
expr_stmt|;
name|this
operator|.
name|commitsCount
operator|=
name|statisticsProvider
operator|.
name|getMeter
argument_list|(
name|COMMITS_COUNT
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|commitQueueSize
operator|=
name|statisticsProvider
operator|.
name|getCounterStats
argument_list|(
name|COMMIT_QUEUE_SIZE
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|commitTime
operator|=
name|statisticsProvider
operator|.
name|getTimer
argument_list|(
name|COMMIT_TIME
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|queueingTime
operator|=
name|statisticsProvider
operator|.
name|getTimer
argument_list|(
name|QUEUEING_TIME
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|//~--------------------------------< SegmentStoreMonitor>
annotation|@
name|Override
specifier|public
name|void
name|onCommit
parameter_list|()
block|{
name|commitsCount
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onCommitQueued
parameter_list|()
block|{
name|commitQueueSize
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|onCommitDequeued
parameter_list|()
block|{
name|commitQueueSize
operator|.
name|dec
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|committedAfter
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|commitTime
operator|.
name|update
argument_list|(
name|time
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dequeuedAfter
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|queueingTime
operator|.
name|update
argument_list|(
name|time
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
comment|//~--------------------------------< SegmentStoreStatsMBean>
annotation|@
name|Override
specifier|public
name|CompositeData
name|getCommitsCount
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|getTimeSeries
argument_list|(
name|COMMITS_COUNT
argument_list|)
argument_list|,
name|COMMITS_COUNT
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getQueuingCommitsCount
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|getTimeSeries
argument_list|(
name|COMMIT_QUEUE_SIZE
argument_list|)
argument_list|,
name|COMMIT_QUEUE_SIZE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getCommitTimes
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|getTimeSeries
argument_list|(
name|COMMIT_TIME
argument_list|)
argument_list|,
name|COMMIT_TIME
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getQueuingTimes
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|getTimeSeries
argument_list|(
name|QUEUEING_TIME
argument_list|)
argument_list|,
name|QUEUEING_TIME
argument_list|)
return|;
block|}
specifier|private
name|TimeSeries
name|getTimeSeries
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|statisticsProvider
operator|.
name|getStats
argument_list|()
operator|.
name|getTimeSeries
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
end_class

end_unit

