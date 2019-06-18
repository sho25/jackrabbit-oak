begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|stats
package|;
end_package

begin_comment
comment|/**  * Interface for a time series of the measured values per  * second, minute, hour and day. The type of the value is arbitrary; it  * could be cache hits or misses, disk reads or writes, created sessions,  * completed transactions, or pretty much anything of interest.  *  * @since Apache Jackrabbit 2.3.2  */
end_comment

begin_interface
specifier|public
interface|interface
name|TimeSeries
block|{
comment|/**      * Returns the measured value per second over the last minute.      *      * @return measured value per second, in chronological order      */
name|long
index|[]
name|getValuePerSecond
parameter_list|()
function_decl|;
comment|/**      * Returns the measured value per minute over the last hour.      *      * @return measured value per minute, in chronological order      */
name|long
index|[]
name|getValuePerMinute
parameter_list|()
function_decl|;
comment|/**      * Returns the measured value per hour over the last week.      *      * @return measured value per hour, in chronological order      */
name|long
index|[]
name|getValuePerHour
parameter_list|()
function_decl|;
comment|/**      * Returns the measured value per week over the last three years.      *      * @return measured value per week, in chronological order      */
name|long
index|[]
name|getValuePerWeek
parameter_list|()
function_decl|;
comment|/**      * The value used to encode missing values i.e. for a period where no value was recorded.      *      * @return  default value      */
name|long
name|getMissingValue
parameter_list|()
function_decl|;
block|}
end_interface

end_unit
