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
name|segment
operator|.
name|compaction
package|;
end_package

begin_comment
comment|/**  * This MBean exposes the settings from {@link SegmentGCOptions}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SegmentRevisionGC
block|{
name|String
name|TYPE
init|=
literal|"SegmentRevisionGarbageCollection"
decl_stmt|;
comment|/**      * @return  {@code true} iff revision gc is paused.      */
name|boolean
name|isPausedCompaction
parameter_list|()
function_decl|;
comment|/**      * Set revision gc to paused.      * @param paused      * @return this instance      */
name|void
name|setPausedCompaction
parameter_list|(
name|boolean
name|paused
parameter_list|)
function_decl|;
comment|/**      * Get the gain estimate threshold beyond which revision gc should run      * @return gainThreshold      */
name|int
name|getGainThreshold
parameter_list|()
function_decl|;
comment|/**      * Set the revision gain estimate threshold beyond which revision gc should run      * @param gainThreshold      */
name|void
name|setGainThreshold
parameter_list|(
name|int
name|gainThreshold
parameter_list|)
function_decl|;
comment|/**      * @return  the memory threshold below which revision gc will not run.      */
name|int
name|getMemoryThreshold
parameter_list|()
function_decl|;
comment|/**      * Set the memory threshold below which revision gc will not run.      * @param memoryThreshold      */
name|void
name|setMemoryThreshold
parameter_list|(
name|int
name|memoryThreshold
parameter_list|)
function_decl|;
comment|/**      * Get the number of tries to compact concurrent commits on top of already      * compacted commits      * @return  retry count      */
name|int
name|getRetryCount
parameter_list|()
function_decl|;
comment|/**      * Set the number of tries to compact concurrent commits on top of already      * compacted commits      * @param retryCount      */
name|void
name|setRetryCount
parameter_list|(
name|int
name|retryCount
parameter_list|)
function_decl|;
comment|/**      * Get whether or not to force compact concurrent commits on top of already      * compacted commits after the maximum number of retries has been reached.      * Force committing tries to exclusively write lock the node store.      * @return  {@code true} if force commit is on, {@code false} otherwise      */
name|boolean
name|getForceAfterFail
parameter_list|()
function_decl|;
comment|/**      * Set whether or not to force compact concurrent commits on top of already      * compacted commits after the maximum number of retries has been reached.      * Force committing tries to exclusively write lock the node store.      * @param forceAfterFail      */
name|void
name|setForceAfterFail
parameter_list|(
name|boolean
name|forceAfterFail
parameter_list|)
function_decl|;
comment|/**      * Get the time to wait for the lock when force compacting.      * See {@link #setForceAfterFail(boolean)}      * @return lock wait time in seconds.      */
name|int
name|getLockWaitTime
parameter_list|()
function_decl|;
comment|/**      * Set the time to wait for the lock when force compacting.      * @param lockWaitTime  lock wait time in seconds      */
name|void
name|setLockWaitTime
parameter_list|(
name|int
name|lockWaitTime
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

