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
name|io
operator|.
name|Closeable
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

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|annotation
operator|.
name|versioning
operator|.
name|ProviderType
import|;
end_import

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|TimerStats
extends|extends
name|Stats
extends|,
name|Counting
block|{
comment|/**      * A timing context.      *      * @see TimerStats#time()      */
interface|interface
name|Context
extends|extends
name|Closeable
block|{
comment|/**          * Updates the timer with the difference between current and start time. Call to this method will          * not reset the start time. Multiple calls result in multiple updates.          * @return the elapsed time in nanoseconds          */
name|long
name|stop
parameter_list|()
function_decl|;
block|}
comment|/**      * Adds a recorded duration.      *      * @param duration the length of the duration      * @param unit     the scale unit of {@code duration}      */
name|void
name|update
parameter_list|(
name|long
name|duration
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
function_decl|;
comment|/**      * Returns a new {@link Context}.      *      * @return a new {@link Context}      * @see Context      */
name|Context
name|time
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

