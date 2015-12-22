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
name|AtomicLong
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
name|Counter
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
name|ExponentiallyDecayingReservoir
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
name|Histogram
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
name|MetricRegistry
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|CompositeStatsTest
block|{
specifier|private
name|MetricRegistry
name|registry
init|=
operator|new
name|MetricRegistry
argument_list|()
decl_stmt|;
specifier|private
name|SimpleStats
name|simpleStats
init|=
operator|new
name|SimpleStats
argument_list|(
operator|new
name|AtomicLong
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|counter
parameter_list|()
throws|throws
name|Exception
block|{
name|Counter
name|counter
init|=
name|registry
operator|.
name|counter
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|CompositeStats
name|counterStats
init|=
operator|new
name|CompositeStats
argument_list|(
name|simpleStats
argument_list|,
name|counter
argument_list|)
decl_stmt|;
name|counterStats
operator|.
name|inc
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|simpleStats
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|counter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|counterStats
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|counterStats
operator|.
name|inc
argument_list|()
expr_stmt|;
name|counterStats
operator|.
name|inc
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|simpleStats
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|counterStats
operator|.
name|dec
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|simpleStats
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|counter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|counterStats
operator|.
name|inc
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|simpleStats
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|counter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|counterStats
operator|.
name|dec
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|simpleStats
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|counter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|counterStats
operator|.
name|isMeter
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|counterStats
operator|.
name|isTimer
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|counterStats
operator|.
name|isCounter
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|counterStats
operator|.
name|getCounter
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
name|Meter
name|meter
init|=
name|registry
operator|.
name|meter
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|CompositeStats
name|meterStats
init|=
operator|new
name|CompositeStats
argument_list|(
name|simpleStats
argument_list|,
name|meter
argument_list|)
decl_stmt|;
name|meterStats
operator|.
name|mark
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|simpleStats
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|meter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|meterStats
operator|.
name|mark
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|simpleStats
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|meter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|meterStats
operator|.
name|isMeter
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|meterStats
operator|.
name|isTimer
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|meterStats
operator|.
name|isCounter
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|meterStats
operator|.
name|getMeter
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
name|Timer
name|time
init|=
name|registry
operator|.
name|timer
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|CompositeStats
name|timerStats
init|=
operator|new
name|CompositeStats
argument_list|(
name|simpleStats
argument_list|,
name|time
argument_list|)
decl_stmt|;
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
literal|1
argument_list|,
name|time
operator|.
name|getCount
argument_list|()
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
name|simpleStats
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|timerStats
operator|.
name|isMeter
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|timerStats
operator|.
name|isTimer
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|timerStats
operator|.
name|isCounter
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|timerStats
operator|.
name|getTimer
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
name|Histogram
name|histo
init|=
name|registry
operator|.
name|histogram
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|CompositeStats
name|histoStats
init|=
operator|new
name|CompositeStats
argument_list|(
name|simpleStats
argument_list|,
name|histo
argument_list|)
decl_stmt|;
name|histoStats
operator|.
name|update
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|histo
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|histo
operator|.
name|getSnapshot
argument_list|()
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|histoStats
operator|.
name|isMeter
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|histoStats
operator|.
name|isTimer
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|histoStats
operator|.
name|isCounter
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|histoStats
operator|.
name|isHistogram
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|histoStats
operator|.
name|getHistogram
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|timerContext
parameter_list|()
throws|throws
name|Exception
block|{
name|VirtualClock
name|clock
init|=
operator|new
name|VirtualClock
argument_list|()
decl_stmt|;
name|Timer
name|time
init|=
operator|new
name|Timer
argument_list|(
operator|new
name|ExponentiallyDecayingReservoir
argument_list|()
argument_list|,
name|clock
argument_list|)
decl_stmt|;
name|TimerStats
name|timerStats
init|=
operator|new
name|CompositeStats
argument_list|(
name|simpleStats
argument_list|,
name|time
argument_list|)
decl_stmt|;
name|TimerStats
operator|.
name|Context
name|context
init|=
name|timerStats
operator|.
name|time
argument_list|()
decl_stmt|;
name|clock
operator|.
name|tick
operator|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toNanos
argument_list|(
literal|314
argument_list|)
expr_stmt|;
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|time
operator|.
name|getCount
argument_list|()
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
literal|314
argument_list|)
argument_list|,
name|simpleStats
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|VirtualClock
extends|extends
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Clock
block|{
name|long
name|tick
decl_stmt|;
annotation|@
name|Override
specifier|public
name|long
name|getTick
parameter_list|()
block|{
return|return
name|tick
return|;
block|}
block|}
block|}
end_class

end_unit

