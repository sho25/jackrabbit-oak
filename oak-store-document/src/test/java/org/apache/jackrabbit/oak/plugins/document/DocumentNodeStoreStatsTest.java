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
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Meter
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
name|Snapshot
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
name|Timer
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
name|commons
operator|.
name|concurrent
operator|.
name|ExecutorCloser
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
name|metric
operator|.
name|MetricStatisticsProvider
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MICROSECONDS
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

begin_class
specifier|public
class|class
name|DocumentNodeStoreStatsTest
block|{
specifier|private
name|ScheduledExecutorService
name|executor
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
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
argument_list|,
name|executor
argument_list|)
decl_stmt|;
specifier|private
name|DocumentNodeStoreStats
name|stats
init|=
operator|new
name|DocumentNodeStoreStats
argument_list|(
name|statsProvider
argument_list|)
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|shutDown
parameter_list|()
block|{
name|statsProvider
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|ExecutorCloser
argument_list|(
name|executor
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|backgroundRead
parameter_list|()
block|{
name|BackgroundReadStats
name|readStats
init|=
operator|new
name|BackgroundReadStats
argument_list|()
decl_stmt|;
name|readStats
operator|.
name|numExternalChanges
operator|=
literal|5
expr_stmt|;
name|stats
operator|.
name|doneBackgroundRead
argument_list|(
name|readStats
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|getMeter
argument_list|(
name|DocumentNodeStoreStats
operator|.
name|BGR_NUM_CHANGES_RATE
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|backgroundWrite
parameter_list|()
block|{
name|BackgroundWriteStats
name|writeStats
init|=
operator|new
name|BackgroundWriteStats
argument_list|()
decl_stmt|;
name|writeStats
operator|.
name|num
operator|=
literal|7
expr_stmt|;
name|stats
operator|.
name|doneBackgroundUpdate
argument_list|(
name|writeStats
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|getMeter
argument_list|(
name|DocumentNodeStoreStats
operator|.
name|BGW_NUM_WRITES_RATE
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|leaseUpdate
parameter_list|()
block|{
name|stats
operator|.
name|doneLeaseUpdate
argument_list|(
literal|47
argument_list|)
expr_stmt|;
name|stats
operator|.
name|doneLeaseUpdate
argument_list|(
literal|53
argument_list|)
expr_stmt|;
name|Timer
name|t
init|=
name|getTimer
argument_list|(
name|DocumentNodeStoreStats
operator|.
name|LEASE_UPDATE
argument_list|)
decl_stmt|;
name|Snapshot
name|s
init|=
name|t
operator|.
name|getSnapshot
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|MICROSECONDS
operator|.
name|toNanos
argument_list|(
literal|47
argument_list|)
argument_list|,
name|s
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MICROSECONDS
operator|.
name|toNanos
argument_list|(
literal|53
argument_list|)
argument_list|,
name|s
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MICROSECONDS
operator|.
name|toNanos
argument_list|(
literal|50
argument_list|)
argument_list|,
name|s
operator|.
name|getMean
argument_list|()
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Meter
name|getMeter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|statsProvider
operator|.
name|getRegistry
argument_list|()
operator|.
name|getMeters
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|private
name|Timer
name|getTimer
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|statsProvider
operator|.
name|getRegistry
argument_list|()
operator|.
name|getTimers
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

