begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|junit
operator|.
name|framework
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
name|Clock
operator|.
name|Fast
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|ClockTest
block|{
specifier|private
specifier|static
name|long
name|SYSTEM_CLOCK_GRANULARITY
decl_stmt|;
specifier|private
specifier|static
name|Long
name|FAST_CLOCK_GRANULARITY
decl_stmt|;
comment|/**      * Helper for checking how accurate the system clock is.      */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"average clock granularity: "
operator|+
name|getAverageClockGranularity
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
block|{
name|SYSTEM_CLOCK_GRANULARITY
operator|=
name|getAverageClockGranularity
argument_list|()
expr_stmt|;
name|FAST_CLOCK_GRANULARITY
operator|=
literal|1000
operator|*
name|Clock
operator|.
name|FAST_CLOCK_INTERVAL
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClockDriftSimple
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|testClockDrift
argument_list|(
name|Clock
operator|.
name|SIMPLE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClockDriftAccurate
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|testClockDrift
argument_list|(
name|Clock
operator|.
name|ACCURATE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-3220"
argument_list|)
specifier|public
name|void
name|testClockDriftFast
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ScheduledExecutorService
name|executor
init|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
decl_stmt|;
try|try
block|{
name|testClockDrift
argument_list|(
operator|new
name|Clock
operator|.
name|Fast
argument_list|(
name|executor
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|testClockDrift
parameter_list|(
name|Clock
name|clock
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|drift
init|=
name|clock
operator|.
name|getTime
argument_list|()
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// Set the drift limit to twice as high as granularity,
comment|// plus 3ms for Thread.sleep() inaccuracy in the fast clock
name|long
name|granularity
init|=
name|getGranularity
argument_list|(
name|clock
argument_list|)
decl_stmt|;
name|long
name|limit
init|=
operator|(
literal|2
operator|*
name|granularity
operator|)
operator|/
literal|1000
operator|+
literal|3
decl_stmt|;
name|assertTrue
argument_list|(
name|clock
operator|+
literal|" unexpected drift: "
operator|+
name|drift
operator|+
literal|"ms (estimated limit was "
operator|+
name|limit
operator|+
literal|"ms, measured granularity was "
operator|+
operator|(
name|granularity
operator|/
literal|1000f
operator|)
operator|+
literal|"ms)"
argument_list|,
name|Math
operator|.
name|abs
argument_list|(
name|drift
argument_list|)
operator|<=
name|limit
argument_list|)
expr_stmt|;
name|long
name|waittime
init|=
literal|100
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|waittime
argument_list|)
expr_stmt|;
name|drift
operator|=
name|clock
operator|.
name|getTime
argument_list|()
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
comment|// Set the drift limit to twice as high as granularity,
comment|// plus 3ms for Thread.sleep() inaccuracy in the fast clock
name|granularity
operator|=
name|getGranularity
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|limit
operator|=
operator|(
literal|2
operator|*
name|granularity
operator|)
operator|/
literal|1000
operator|+
literal|3
expr_stmt|;
name|assertTrue
argument_list|(
name|clock
operator|+
literal|" unexpected drift after "
operator|+
name|waittime
operator|+
literal|"ms: "
operator|+
name|drift
operator|+
literal|"ms (estimated limit was "
operator|+
name|limit
operator|+
literal|"ms, measured granularity was "
operator|+
operator|(
name|granularity
operator|/
literal|1000f
operator|)
operator|+
literal|"ms)"
argument_list|,
name|Math
operator|.
name|abs
argument_list|(
name|drift
argument_list|)
operator|<=
name|limit
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|long
name|getGranularity
parameter_list|(
name|Clock
name|clock
parameter_list|)
block|{
return|return
name|clock
operator|instanceof
name|Fast
condition|?
name|FAST_CLOCK_GRANULARITY
else|:
name|SYSTEM_CLOCK_GRANULARITY
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClockIncreasingSimple
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|testClockIncreasing
argument_list|(
name|Clock
operator|.
name|SIMPLE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClockIncreasingAccurate
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|testClockIncreasing
argument_list|(
name|Clock
operator|.
name|SIMPLE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClockIncreasingFast
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ScheduledExecutorService
name|executor
init|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
decl_stmt|;
try|try
block|{
name|testClockIncreasing
argument_list|(
operator|new
name|Clock
operator|.
name|Fast
argument_list|(
name|executor
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|testClockIncreasing
parameter_list|(
name|Clock
name|clock
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|time
init|=
literal|0
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|long
name|now
init|=
name|clock
operator|.
name|getTimeIncreasing
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|time
operator|<
name|now
argument_list|)
expr_stmt|;
name|time
operator|=
name|now
expr_stmt|;
block|}
block|}
comment|/**      * On some systems (for instance Windows), the granularity of {@code System.currentTimeMillis} depends      * on system-wide settings that can change depending on what applications are running      * (see, for instance<a href="http://www.lifehacker.com.au/2009/05/hidden-windows-7-tool-troubleshoots-sleep-mode-problems/">http://www.lifehacker.com.au/2009/05/hidden-windows-7-tool-troubleshoots-sleep-mode-problems/</a>).      * This method tries to measure the granularity.      * @return average granularity of {@code System.currentTimeMillis} in 1/1000 of milliseconds      */
specifier|private
specifier|static
name|long
name|getAverageClockGranularity
parameter_list|()
block|{
name|long
name|sum
init|=
literal|0
decl_stmt|;
name|int
name|samples
init|=
literal|20
decl_stmt|;
comment|// number of samples to take
name|long
name|last
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
name|samples
condition|;
name|i
operator|++
control|)
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
name|now
operator|==
name|last
condition|)
block|{
comment|// busy-wait until return value changes
name|now
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
name|sum
operator|+=
operator|(
name|now
operator|-
name|last
operator|)
expr_stmt|;
comment|// add the actual difference
name|last
operator|=
name|now
expr_stmt|;
block|}
comment|// return average in 1/1000ms
return|return
operator|(
name|sum
operator|*
literal|1000
operator|)
operator|/
name|samples
return|;
block|}
block|}
end_class

end_unit

