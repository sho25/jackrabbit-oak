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
name|api
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_comment
comment|/**  * MBean interface for exposing information about a registered observation  * listener.  *  * @see<a href="https://issues.apache.org/jira/browse/JCR-3608">JCR-3608</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|EventListenerMBean
block|{
comment|/** Class name of the event listener */
name|String
name|getClassName
parameter_list|()
function_decl|;
comment|/** toString of the event listener */
name|String
name|getToString
parameter_list|()
function_decl|;
comment|/** Stack trace of where the listener was registered */
name|String
name|getInitStackTrace
parameter_list|()
function_decl|;
comment|/** Event types of the listener registration */
name|int
name|getEventTypes
parameter_list|()
function_decl|;
comment|/** Absolute path of the listener registration */
name|String
name|getAbsPath
parameter_list|()
function_decl|;
comment|/** Whether the listener registration is deep */
name|boolean
name|isDeep
parameter_list|()
function_decl|;
comment|/** UUIDs of the listener registration */
name|String
index|[]
name|getUuid
parameter_list|()
function_decl|;
comment|/** Node types of the listener registration */
name|String
index|[]
name|getNodeTypeName
parameter_list|()
function_decl|;
comment|/** Whether the listener registration is non-local */
name|boolean
name|isNoLocal
parameter_list|()
function_decl|;
comment|/** Number of {@code onEvent()} calls made on the listener */
name|long
name|getEventDeliveries
parameter_list|()
function_decl|;
comment|/** Average number of {@code onEvent()} calls per hour */
name|long
name|getEventDeliveriesPerHour
parameter_list|()
function_decl|;
comment|/** Average time (in microseconds) taken per {@code onEvent()} call */
name|long
name|getMicrosecondsPerEventDelivery
parameter_list|()
function_decl|;
comment|/** Number of individual events delivered to the listener */
name|long
name|getEventsDelivered
parameter_list|()
function_decl|;
comment|/** Average number of individual events delivered per hour */
name|long
name|getEventsDeliveredPerHour
parameter_list|()
function_decl|;
comment|/** Average time (in microseconds) taken per event delivered */
name|long
name|getMicrosecondsPerEventDelivered
parameter_list|()
function_decl|;
comment|/** Ratio of time spent in event processing */
name|double
name|getRatioOfTimeSpentProcessingEvents
parameter_list|()
function_decl|;
comment|/** Ratio of time spent in event listener vs. the overall event processing */
name|double
name|getEventConsumerTimeRatio
parameter_list|()
function_decl|;
comment|/** Is user information accessed without checking if an event is external? */
name|boolean
name|isUserInfoAccessedWithoutExternalsCheck
parameter_list|()
function_decl|;
comment|/** Is user information accessed from an external event? */
name|boolean
name|isUserInfoAccessedFromExternalEvent
parameter_list|()
function_decl|;
comment|/** Is date information accessed without checking if an event is external? */
name|boolean
name|isDateAccessedWithoutExternalsCheck
parameter_list|()
function_decl|;
comment|/** Is date information accessed from an external event? */
name|boolean
name|isDateAccessedFromExternalEvent
parameter_list|()
function_decl|;
comment|/**      * The time difference between the current system time and the head (oldest)      * element in the queue in milliseconds. This method returns zero if the      * queue is empty.      */
name|long
name|getQueueBacklogMillis
parameter_list|()
function_decl|;
comment|/**      * {@link org.apache.jackrabbit.api.stats.TimeSeries time series} of the number of      * items related to generating observation events that are currently queued by the      * system. The exact nature of these items is implementation specific and might not      * be in a one to one relation with the number of pending JCR events.      * @return  time series of the queue length      */
name|CompositeData
name|getQueueLength
parameter_list|()
function_decl|;
comment|/**      * @return  time series of the number of JCR events      */
name|CompositeData
name|getEventCount
parameter_list|()
function_decl|;
comment|/**      * @return  time series of the time it took an event listener to process JCR events.      */
name|CompositeData
name|getEventConsumerTime
parameter_list|()
function_decl|;
comment|/**      * @return  time series of the time it took the system to produce JCR events.      */
name|CompositeData
name|getEventProducerTime
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

