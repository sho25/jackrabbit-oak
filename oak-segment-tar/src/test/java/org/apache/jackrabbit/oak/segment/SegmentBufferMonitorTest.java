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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
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
name|segment
operator|.
name|SegmentBufferMonitor
operator|.
name|DIRECT_BUFFER_CAPACITY
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
name|segment
operator|.
name|SegmentBufferMonitor
operator|.
name|DIRECT_BUFFER_COUNT
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
name|segment
operator|.
name|SegmentBufferMonitor
operator|.
name|HEAP_BUFFER_CAPACITY
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
name|segment
operator|.
name|SegmentBufferMonitor
operator|.
name|HEAP_BUFFER_COUNT
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
name|stats
operator|.
name|SimpleStats
operator|.
name|Type
operator|.
name|COUNTER
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
name|assertTrue
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|oak
operator|.
name|commons
operator|.
name|Buffer
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentBufferMonitorTest
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CounterStats
argument_list|>
name|stats
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SegmentBufferMonitor
name|segmentBufferMonitor
init|=
operator|new
name|SegmentBufferMonitor
argument_list|(
operator|new
name|StatisticsProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RepositoryStatistics
name|getStats
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|MeterStats
name|getMeter
parameter_list|(
name|String
name|name
parameter_list|,
name|StatsOptions
name|options
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|CounterStats
name|getCounterStats
parameter_list|(
name|String
name|name
parameter_list|,
name|StatsOptions
name|options
parameter_list|)
block|{
name|SimpleStats
name|simpleStats
init|=
operator|new
name|SimpleStats
argument_list|(
operator|new
name|AtomicLong
argument_list|()
argument_list|,
name|COUNTER
argument_list|)
decl_stmt|;
name|stats
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|simpleStats
argument_list|)
expr_stmt|;
return|return
name|simpleStats
return|;
block|}
annotation|@
name|Override
specifier|public
name|TimerStats
name|getTimer
parameter_list|(
name|String
name|name
parameter_list|,
name|StatsOptions
name|options
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|HistogramStats
name|getHistogram
parameter_list|(
name|String
name|name
parameter_list|,
name|StatsOptions
name|options
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
block|}
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|emptyStats
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|DIRECT_BUFFER_COUNT
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|DIRECT_BUFFER_CAPACITY
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|HEAP_BUFFER_COUNT
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|HEAP_BUFFER_CAPACITY
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
name|heapBuffer
parameter_list|()
block|{
name|Buffer
name|buffer
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
literal|42
argument_list|)
decl_stmt|;
name|segmentBufferMonitor
operator|.
name|trackAllocation
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|DIRECT_BUFFER_COUNT
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|DIRECT_BUFFER_CAPACITY
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|HEAP_BUFFER_COUNT
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|HEAP_BUFFER_CAPACITY
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|DIRECT_BUFFER_COUNT
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|DIRECT_BUFFER_CAPACITY
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stats
operator|.
name|get
argument_list|(
name|HEAP_BUFFER_COUNT
argument_list|)
operator|.
name|getCount
argument_list|()
operator|<=
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stats
operator|.
name|get
argument_list|(
name|HEAP_BUFFER_CAPACITY
argument_list|)
operator|.
name|getCount
argument_list|()
operator|<=
literal|42
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|directBuffer
parameter_list|()
block|{
name|Buffer
name|buffer
init|=
name|Buffer
operator|.
name|allocateDirect
argument_list|(
literal|42
argument_list|)
decl_stmt|;
name|segmentBufferMonitor
operator|.
name|trackAllocation
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|DIRECT_BUFFER_COUNT
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|DIRECT_BUFFER_CAPACITY
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|HEAP_BUFFER_COUNT
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|HEAP_BUFFER_CAPACITY
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|stats
operator|.
name|get
argument_list|(
name|DIRECT_BUFFER_COUNT
argument_list|)
operator|.
name|getCount
argument_list|()
operator|<=
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stats
operator|.
name|get
argument_list|(
name|DIRECT_BUFFER_CAPACITY
argument_list|)
operator|.
name|getCount
argument_list|()
operator|<=
literal|42
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|HEAP_BUFFER_COUNT
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|HEAP_BUFFER_CAPACITY
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

