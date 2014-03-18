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
name|ArrayList
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
name|List
argument_list|<
name|Long
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|10000
decl_stmt|;
comment|// 10 seconds
name|long
name|last
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|current
init|=
name|last
decl_stmt|;
do|do
block|{
name|current
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
if|if
condition|(
name|current
operator|!=
name|last
operator|&&
name|current
operator|!=
name|last
operator|+
literal|1
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
name|last
operator|=
name|current
expr_stmt|;
block|}
do|while
condition|(
name|current
operator|<
name|end
condition|)
do|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|values
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|+
literal|" "
operator|+
operator|(
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|-
name|values
operator|.
name|get
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-1564"
argument_list|)
comment|// FIXME OAK-1564 ClockTest on Windows fails
specifier|public
name|void
name|testClockDrift
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
specifier|final
name|long
name|limit
init|=
literal|20
decl_stmt|;
try|try
block|{
name|Clock
index|[]
name|clocks
init|=
operator|new
name|Clock
index|[]
block|{
name|Clock
operator|.
name|SIMPLE
block|,
name|Clock
operator|.
name|ACCURATE
block|,
operator|new
name|Clock
operator|.
name|Fast
argument_list|(
name|executor
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|Clock
name|clock
range|:
name|clocks
control|)
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
name|assertTrue
argument_list|(
literal|"unexpected drift: "
operator|+
name|Math
operator|.
name|abs
argument_list|(
name|drift
argument_list|)
operator|+
literal|" (limit "
operator|+
name|limit
operator|+
literal|")"
argument_list|,
name|Math
operator|.
name|abs
argument_list|(
name|drift
argument_list|)
operator|<
name|limit
argument_list|)
expr_stmt|;
comment|// Windows can have 15ms gaps
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
for|for
control|(
name|Clock
name|clock
range|:
name|clocks
control|)
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
name|assertTrue
argument_list|(
literal|"unexpected drift: "
operator|+
name|Math
operator|.
name|abs
argument_list|(
name|drift
argument_list|)
operator|+
literal|" (limit "
operator|+
name|limit
operator|+
literal|")"
argument_list|,
name|Math
operator|.
name|abs
argument_list|(
name|drift
argument_list|)
operator|<
name|limit
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|Test
specifier|public
name|void
name|testClockIncreasing
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
name|Clock
index|[]
name|clocks
init|=
operator|new
name|Clock
index|[]
block|{
name|Clock
operator|.
name|SIMPLE
block|,
name|Clock
operator|.
name|ACCURATE
block|,
operator|new
name|Clock
operator|.
name|Fast
argument_list|(
name|executor
argument_list|)
block|}
decl_stmt|;
name|long
index|[]
name|time
init|=
operator|new
name|long
index|[
name|clocks
operator|.
name|length
index|]
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
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|clocks
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|long
name|now
init|=
name|clocks
index|[
name|j
index|]
operator|.
name|getTimeIncreasing
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|time
index|[
name|j
index|]
operator|<
name|now
argument_list|)
expr_stmt|;
name|time
index|[
name|j
index|]
operator|=
name|now
expr_stmt|;
block|}
block|}
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
block|}
end_class

end_unit

