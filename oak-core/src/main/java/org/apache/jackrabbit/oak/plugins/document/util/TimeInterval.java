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
operator|.
name|util
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
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_comment
comment|/**  * A class representing a time interval, with utility methods to derive related  * intervals, check time stamps for containment, etc.  */
end_comment

begin_class
specifier|public
class|class
name|TimeInterval
block|{
specifier|public
specifier|final
name|long
name|fromMs
decl_stmt|;
specifier|public
specifier|final
name|long
name|toMs
decl_stmt|;
specifier|public
name|TimeInterval
parameter_list|(
name|long
name|fromMs
parameter_list|,
name|long
name|toMs
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|fromMs
operator|<=
name|toMs
argument_list|,
literal|"start must be<= end"
argument_list|)
expr_stmt|;
name|this
operator|.
name|fromMs
operator|=
name|fromMs
expr_stmt|;
name|this
operator|.
name|toMs
operator|=
name|toMs
expr_stmt|;
block|}
comment|/**      * Shortens the interval to the specified end value, if is contained in the      * interval. Returns a single point in time interval when the specified time      * is outside the time interval. Return unchanged internal if specified      * value beyond end.      */
specifier|public
name|TimeInterval
name|notLaterThan
parameter_list|(
name|long
name|timestampMs
parameter_list|)
block|{
if|if
condition|(
name|timestampMs
operator|<
name|toMs
condition|)
block|{
return|return
operator|new
name|TimeInterval
argument_list|(
operator|(
name|timestampMs
operator|<
name|fromMs
operator|)
condition|?
name|timestampMs
else|:
name|fromMs
argument_list|,
name|timestampMs
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
comment|/**      * Shortens the interval to the specified start value, if is contained in      * the interval. Returns a single point in time interval when the specified      * time is outside the time interval. Return unchanged internal if specified      * value before start.      */
specifier|public
name|TimeInterval
name|notEarlierThan
parameter_list|(
name|long
name|timestampMs
parameter_list|)
block|{
if|if
condition|(
name|fromMs
operator|<
name|timestampMs
condition|)
block|{
return|return
operator|new
name|TimeInterval
argument_list|(
name|timestampMs
argument_list|,
operator|(
name|timestampMs
operator|>
name|toMs
operator|)
condition|?
name|timestampMs
else|:
name|toMs
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
comment|/**      * Returns a new interval starting at the previous time, but ending after      * the specified duration.      */
specifier|public
name|TimeInterval
name|startAndDuration
parameter_list|(
name|long
name|durationMs
parameter_list|)
block|{
return|return
operator|new
name|TimeInterval
argument_list|(
name|fromMs
argument_list|,
name|fromMs
operator|+
name|durationMs
argument_list|)
return|;
block|}
comment|/**      * Returns the duration in ms.      */
specifier|public
name|long
name|getDurationMs
parameter_list|()
block|{
return|return
name|toMs
operator|-
name|fromMs
return|;
block|}
comment|/**      * Checks whether the interval contains the given time stamp.      */
specifier|public
name|boolean
name|contains
parameter_list|(
name|long
name|timestampMs
parameter_list|)
block|{
return|return
name|fromMs
operator|<=
name|timestampMs
operator|&&
name|timestampMs
operator|<=
name|toMs
return|;
block|}
comment|/**      * Checks whether the interval end after the given time stamp.      */
specifier|public
name|boolean
name|endsAfter
parameter_list|(
name|long
name|timestampMs
parameter_list|)
block|{
return|return
name|toMs
operator|>
name|timestampMs
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|TimeInterval
operator|&&
name|o
operator|!=
literal|null
condition|)
block|{
return|return
name|fromMs
operator|==
operator|(
operator|(
name|TimeInterval
operator|)
name|o
operator|)
operator|.
name|fromMs
operator|&&
name|toMs
operator|==
operator|(
operator|(
name|TimeInterval
operator|)
name|o
operator|)
operator|.
name|toMs
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|fromMs
operator|^
operator|(
name|fromMs
operator|>>>
literal|32
operator|)
operator|^
name|toMs
operator|^
operator|(
name|toMs
operator|>>>
literal|32
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|Utils
operator|.
name|timestampToString
argument_list|(
name|fromMs
argument_list|)
operator|+
literal|", "
operator|+
name|Utils
operator|.
name|timestampToString
argument_list|(
name|toMs
argument_list|)
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit
