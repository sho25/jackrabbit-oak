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

begin_class
specifier|public
specifier|final
class|class
name|SimpleStats
implements|implements
name|TimerStats
implements|,
name|MeterStats
implements|,
name|CounterStats
implements|,
name|HistogramStats
block|{
specifier|public
enum|enum
name|Type
block|{
name|COUNTER
block|,
name|METER
block|,
name|TIMER
block|,
name|HISTOGRAM
block|}
specifier|private
specifier|final
name|AtomicLong
name|statsHolder
decl_stmt|;
specifier|private
name|long
name|counter
decl_stmt|;
comment|/*         Using 2 different variables for managing the sum in meter calls         1. Primitive variant is used for just increment         2. AtomicLong variant is used for increment by 'n'          This is done to ensure that more frequent mark() is fast (used for Session reads)         and overhead of AtomicLong is used only for less critical flows          Once we move to JDK 8 we can probably use LongAdder from that has lesser         impact on performance      */
specifier|private
name|long
name|meterSum
decl_stmt|;
specifier|private
specifier|final
name|AtomicLong
name|meterSumRef
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Type
name|type
decl_stmt|;
specifier|public
name|SimpleStats
parameter_list|(
name|AtomicLong
name|statsHolder
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
name|this
operator|.
name|statsHolder
operator|=
name|statsHolder
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getCount
parameter_list|()
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|HISTOGRAM
case|:
case|case
name|TIMER
case|:
comment|//For timer and histogram we need to manage explicit
comment|//invocation count
return|return
name|counter
return|;
case|case
name|COUNTER
case|:
return|return
name|statsHolder
operator|.
name|get
argument_list|()
return|;
case|case
name|METER
case|:
comment|//For Meter it can happen that backing statsHolder gets
comment|//reset each second. So need to manage that sum separately
return|return
name|meterSum
operator|+
name|meterSumRef
operator|.
name|get
argument_list|()
return|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|inc
parameter_list|()
block|{
name|statsHolder
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dec
parameter_list|()
block|{
name|statsHolder
operator|.
name|getAndDecrement
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|inc
parameter_list|(
name|long
name|n
parameter_list|)
block|{
name|statsHolder
operator|.
name|getAndAdd
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dec
parameter_list|(
name|long
name|n
parameter_list|)
block|{
name|statsHolder
operator|.
name|getAndAdd
argument_list|(
operator|-
name|n
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|mark
parameter_list|()
block|{
name|inc
argument_list|()
expr_stmt|;
name|meterSum
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|mark
parameter_list|(
name|long
name|n
parameter_list|)
block|{
name|meterSumRef
operator|.
name|getAndAdd
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|statsHolder
operator|.
name|getAndAdd
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|update
parameter_list|(
name|long
name|duration
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
name|counter
operator|++
expr_stmt|;
name|statsHolder
operator|.
name|getAndAdd
argument_list|(
name|unit
operator|.
name|toMillis
argument_list|(
name|duration
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Context
name|time
parameter_list|()
block|{
return|return
operator|new
name|SimpleContext
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|update
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|counter
operator|++
expr_stmt|;
name|statsHolder
operator|.
name|getAndAdd
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|SimpleContext
implements|implements
name|Context
block|{
specifier|private
specifier|final
name|TimerStats
name|timer
decl_stmt|;
specifier|private
specifier|final
name|long
name|startTime
decl_stmt|;
specifier|private
name|SimpleContext
parameter_list|(
name|TimerStats
name|timer
parameter_list|)
block|{
name|this
operator|.
name|timer
operator|=
name|timer
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|stop
parameter_list|()
block|{
specifier|final
name|long
name|elapsed
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
decl_stmt|;
name|timer
operator|.
name|update
argument_list|(
name|elapsed
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
return|return
name|elapsed
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
