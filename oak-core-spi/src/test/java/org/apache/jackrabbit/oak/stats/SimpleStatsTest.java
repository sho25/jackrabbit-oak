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
name|assertNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|SimpleStatsTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|usageTest
parameter_list|()
throws|throws
name|Exception
block|{
name|AtomicLong
name|counter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
name|SimpleStats
name|stats
init|=
operator|new
name|SimpleStats
argument_list|(
name|counter
argument_list|,
name|SimpleStats
operator|.
name|Type
operator|.
name|COUNTER
argument_list|)
decl_stmt|;
name|stats
operator|.
name|mark
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|counter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stats
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|inc
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|counter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|dec
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|counter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|inc
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|counter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|dec
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|counter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|mark
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|counter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|counter
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|stats
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
name|MILLISECONDS
operator|.
name|convert
argument_list|(
literal|100
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|,
name|counter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|counter
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|TimerStats
operator|.
name|Context
name|context
init|=
name|stats
operator|.
name|time
argument_list|()
decl_stmt|;
name|long
name|delta
init|=
name|context
operator|.
name|stop
argument_list|()
decl_stmt|;
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
literal|42
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|delta
argument_list|)
argument_list|,
name|counter
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
name|noopTest
parameter_list|()
throws|throws
name|Exception
block|{
name|NoopStats
name|noop
init|=
name|NoopStats
operator|.
name|INSTANCE
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|noop
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|noop
operator|.
name|mark
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|noop
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|noop
operator|.
name|mark
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|noop
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|noop
operator|.
name|dec
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|noop
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|noop
operator|.
name|inc
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|noop
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|noop
operator|.
name|inc
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|noop
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|noop
operator|.
name|dec
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|noop
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|noop
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
literal|0
argument_list|,
name|noop
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|TimerStats
operator|.
name|Context
name|context
init|=
name|noop
operator|.
name|time
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|context
operator|.
name|stop
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noopRepoStatsTest
parameter_list|()
throws|throws
name|Exception
block|{
name|RepositoryStatistics
name|stats
init|=
name|StatisticsProvider
operator|.
name|NOOP
operator|.
name|getStats
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|stats
operator|.
name|getTimeSeries
argument_list|(
literal|"foo"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|stats
operator|.
name|getTimeSeries
argument_list|(
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|QUERY_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|TimeSeries
name|ts
init|=
name|stats
operator|.
name|getTimeSeries
argument_list|(
literal|"foo"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ts
operator|.
name|getValuePerHour
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|ts
operator|.
name|getValuePerMinute
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|ts
operator|.
name|getValuePerSecond
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|ts
operator|.
name|getValuePerWeek
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ts
operator|.
name|getMissingValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|meterResetAndCount
parameter_list|()
throws|throws
name|Exception
block|{
name|AtomicLong
name|counter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
name|MeterStats
name|stats
init|=
operator|new
name|SimpleStats
argument_list|(
name|counter
argument_list|,
name|SimpleStats
operator|.
name|Type
operator|.
name|METER
argument_list|)
decl_stmt|;
name|stats
operator|.
name|mark
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stats
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|mark
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stats
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|mark
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|stats
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|counter
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|stats
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

