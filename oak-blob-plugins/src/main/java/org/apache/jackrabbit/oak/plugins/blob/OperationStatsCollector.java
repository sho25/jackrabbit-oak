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
name|blob
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

begin_comment
comment|/**  * Implementations of this can use to mark the relevant statistics.  */
end_comment

begin_interface
specifier|public
interface|interface
name|OperationStatsCollector
block|{
name|OperationStatsCollector
name|NOOP
init|=
operator|new
name|OperationStatsCollector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|finishFailure
parameter_list|()
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|updateNumDeleted
parameter_list|(
name|long
name|num
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|updateNumCandidates
parameter_list|(
name|long
name|num
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|updateTotalSizeDeleted
parameter_list|(
name|long
name|size
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|updateDuration
parameter_list|(
name|long
name|time
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|updateMarkDuration
parameter_list|(
name|long
name|time
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|updateSweepDuration
parameter_list|(
name|long
name|time
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
block|{         }
block|}
decl_stmt|;
comment|/**      * Increment the start counter      */
name|void
name|start
parameter_list|()
function_decl|;
comment|/**      * Increment the finishFailure counter      */
name|void
name|finishFailure
parameter_list|()
function_decl|;
comment|/**      * Update the number deleted      * @param num      */
name|void
name|updateNumDeleted
parameter_list|(
name|long
name|num
parameter_list|)
function_decl|;
comment|/**      * Update the number of candidates found      * @param num      */
name|void
name|updateNumCandidates
parameter_list|(
name|long
name|num
parameter_list|)
function_decl|;
comment|/**      * Update the size deleted      * @param size      */
name|void
name|updateTotalSizeDeleted
parameter_list|(
name|long
name|size
parameter_list|)
function_decl|;
comment|/**      * Increment the duration timer      *      * @param time time recorded for the operation      * @param timeUnit unit of time      */
name|void
name|updateDuration
parameter_list|(
name|long
name|time
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
function_decl|;
comment|/**      * Increment the mark phase duration timer      *      * @param time time recorded for the operation      * @param timeUnit unit of time      */
name|void
name|updateMarkDuration
parameter_list|(
name|long
name|time
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
function_decl|;
comment|/**      * Increment the sweep phase duration timer      *      * @param time time recorded for the operation      * @param timeUnit unit of time      */
name|void
name|updateSweepDuration
parameter_list|(
name|long
name|time
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

