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
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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

begin_comment
comment|/**  * Mechanism for keeping track of time at millisecond accuracy.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Clock
block|{
comment|/**      * Maximum amount (in ms) of random noise to include in the time      * signal reported by the {@link #SIMPLE} clock. Configurable by the      * "simple.clock.noise" system property to make it easier to test      * the effect of an inaccurate system clock.      */
specifier|private
specifier|static
specifier|final
name|int
name|SIMPLE_CLOCK_NOISE
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"simple.clock.noise"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|/**      * Millisecond granularity of the {@link #ACCURATE} clock.      * Configurable by the "accurate.clock.granularity" system property      * to make it easier to test the effect of a slow-moving clock on      * code that relies on millisecond timestamps.      */
specifier|private
specifier|static
specifier|final
name|long
name|ACCURATE_CLOCK_GRANULARITY
init|=
name|Long
operator|.
name|getLong
argument_list|(
literal|"accurate.clock.granularity"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|private
name|long
name|monotonic
init|=
literal|0
decl_stmt|;
specifier|private
name|long
name|increasing
init|=
literal|0
decl_stmt|;
comment|/**      * Returns the current time in milliseconds since the epoch.      *      * @see System#currentTimeMillis()      * @return current time in milliseconds since the epoch      */
specifier|public
specifier|abstract
name|long
name|getTime
parameter_list|()
function_decl|;
comment|/**      * Returns a monotonically increasing timestamp based on the current time.      * A call to this method will always return a value that is greater than      * or equal to a value returned by any previous call. This contract holds      * even across multiple threads and in cases when the system time is      * adjusted backwards. In the latter case the returned value will remain      * constant until the previously reported timestamp is again reached.      *      * @return monotonically increasing timestamp      */
specifier|public
specifier|synchronized
name|long
name|getTimeMonotonic
parameter_list|()
block|{
name|long
name|now
init|=
name|getTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|>
name|monotonic
condition|)
block|{
name|monotonic
operator|=
name|now
expr_stmt|;
block|}
else|else
block|{
name|now
operator|=
name|monotonic
expr_stmt|;
block|}
return|return
name|now
return|;
block|}
comment|/**      * Returns a strictly increasing timestamp based on the current time.      * This method is like {@link #getTimeMonotonic()}, with the exception      * that two calls of this method will never return the same timestamp.      * Instead this method will explicitly wait until the current time      * increases beyond any previously returned value. Note that the wait      * may last long if this method is called frequently from many concurrent      * thread or if the system time is adjusted backwards. The caller should      * be prepared to deal with an explicit interrupt in such cases.      *      * @return strictly increasing timestamp      * @throws InterruptedException if the wait was interrupted      */
specifier|public
specifier|synchronized
name|long
name|getTimeIncreasing
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|long
name|now
init|=
name|getTime
argument_list|()
decl_stmt|;
while|while
condition|(
name|now
operator|<=
name|increasing
condition|)
block|{
name|wait
argument_list|(
literal|0
argument_list|,
literal|100000
argument_list|)
expr_stmt|;
comment|// 0.1ms
name|now
operator|=
name|getTime
argument_list|()
expr_stmt|;
block|}
name|increasing
operator|=
name|now
expr_stmt|;
return|return
name|now
return|;
block|}
comment|/**      * Convenience method that returns the {@link #getTime()} value      * as a {@link Date} instance.      *      * @return current time      */
specifier|public
name|Date
name|getDate
parameter_list|()
block|{
return|return
operator|new
name|Date
argument_list|(
name|getTime
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Convenience method that returns the {@link #getTimeMonotonic()} value      * as a {@link Date} instance.      *      * @return monotonically increasing time      */
specifier|public
name|Date
name|getDateMonotonic
parameter_list|()
block|{
return|return
operator|new
name|Date
argument_list|(
name|getTimeMonotonic
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Convenience method that returns the {@link #getTimeIncreasing()} value      * as a {@link Date} instance.      *      * @return strictly increasing time      */
specifier|public
name|Date
name|getDateIncreasing
parameter_list|()
throws|throws
name|InterruptedException
block|{
return|return
operator|new
name|Date
argument_list|(
name|getTimeIncreasing
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Simple clock implementation based on {@link System#currentTimeMillis()},      * which is known to be rather slow on some platforms.      */
specifier|public
specifier|static
name|Clock
name|SIMPLE
init|=
name|createSimpleClock
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|Clock
name|createSimpleClock
parameter_list|()
block|{
specifier|final
name|int
name|noise
init|=
name|SIMPLE_CLOCK_NOISE
decl_stmt|;
if|if
condition|(
name|noise
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|Clock
argument_list|()
block|{
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|synchronized
name|long
name|getTime
parameter_list|()
block|{
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|noise
argument_list|)
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
operator|new
name|Clock
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|getTime
parameter_list|()
block|{
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
comment|/**      * Accurate clock implementation that uses interval timings from the      * {@link System#nanoTime()} method to calculate an as accurate as possible      * time based on occasional calls to {@link System#currentTimeMillis()}      * to prevent clock drift.      */
specifier|public
specifier|static
name|Clock
name|ACCURATE
init|=
operator|new
name|Clock
argument_list|()
block|{
specifier|private
specifier|static
specifier|final
name|long
name|NS_IN_MS
init|=
literal|1000000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|SYNC_INTERVAL
init|=
literal|1000
decl_stmt|;
comment|// ms
specifier|private
name|long
name|ms
init|=
name|SIMPLE
operator|.
name|getTime
argument_list|()
decl_stmt|;
specifier|private
name|long
name|ns
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|synchronized
name|long
name|getTime
parameter_list|()
block|{
name|long
name|nsIncrease
init|=
name|Math
operator|.
name|max
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|ns
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|//>= 0
name|long
name|msIncrease
init|=
name|nsIncrease
operator|/
name|NS_IN_MS
decl_stmt|;
if|if
condition|(
name|ACCURATE_CLOCK_GRANULARITY
operator|>
literal|1
condition|)
block|{
name|msIncrease
operator|-=
name|msIncrease
operator|%
name|ACCURATE_CLOCK_GRANULARITY
expr_stmt|;
block|}
name|long
name|now
init|=
name|ms
operator|+
name|msIncrease
decl_stmt|;
if|if
condition|(
name|now
operator|>
name|ms
operator|+
name|SYNC_INTERVAL
condition|)
block|{
name|ms
operator|=
name|SIMPLE
operator|.
name|getTime
argument_list|()
expr_stmt|;
name|ns
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
comment|// Check whether the system time jumped ahead or back
comment|// from what we'd expect based on the nanosecond interval.
comment|// If the jump was small, it was probably caused by low
comment|// granularity of the system time. In that case we reduce
comment|// the jump to just 0.5ms to smoothen the reported time.
comment|// This should still keep clock drift in check as long as
comment|// the nanosecond timings drift on average less than 0.5ms
comment|// per second.
name|long
name|jump
init|=
name|ms
operator|-
name|now
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|jump
operator|&&
name|jump
operator|<
literal|1000
condition|)
block|{
name|ms
operator|=
name|now
expr_stmt|;
name|ns
operator|-=
name|NS_IN_MS
operator|/
literal|2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|0
operator|>
name|jump
operator|&&
name|jump
operator|>
operator|-
literal|1000
condition|)
block|{
comment|// Note that the Math.max(..., 0) above will cause the
comment|// reported time to stay constant for a while instead
comment|// of going backwards because of this.
name|ms
operator|=
name|now
expr_stmt|;
name|ns
operator|+=
name|NS_IN_MS
operator|/
literal|2
expr_stmt|;
block|}
block|}
return|return
name|now
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Fast clock implementation whose {@link #getTime()} method returns      * instantaneously thanks to a background task that takes care of the      * actual time-keeping work.      */
specifier|public
specifier|static
class|class
name|Fast
extends|extends
name|Clock
block|{
specifier|private
specifier|volatile
name|long
name|time
init|=
name|ACCURATE
operator|.
name|getTime
argument_list|()
decl_stmt|;
specifier|public
name|Fast
parameter_list|(
name|ScheduledExecutorService
name|executor
parameter_list|)
block|{
name|executor
operator|.
name|scheduleAtFixedRate
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
name|time
operator|=
name|ACCURATE
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getTime
parameter_list|()
block|{
return|return
name|time
return|;
block|}
block|}
block|}
end_class

end_unit

