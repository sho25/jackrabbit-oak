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
comment|/**      * Set revision gc to paused.      * @param paused      */
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
comment|/**      * Get the number of seconds to attempt to force compact concurrent commits on top of      * already compacted commits after the maximum number of retries has been reached.      * Forced compaction acquires an exclusive write lock on the node store.      * @return  the number of seconds until forced compaction gives up and the exclusive      *          write lock on the node store is released.      */
name|int
name|getForceTimeout
parameter_list|()
function_decl|;
comment|/**      * Set the number of seconds to attempt to force compact concurrent commits on top of      * already compacted commits after the maximum number of retries has been reached.      * Forced compaction acquires an exclusively write lock on the node store.      * @param timeout  the number of seconds until forced compaction gives up and the exclusive      *                 lock on the node store is released.      */
name|void
name|setForceTimeout
parameter_list|(
name|int
name|timeout
parameter_list|)
function_decl|;
comment|/**      * Number of segment generations to retain.      * @see #setRetainedGenerations(int)      * @return  number of gc generations.      */
name|int
name|getRetainedGenerations
parameter_list|()
function_decl|;
comment|/**      * Set the number of segment generations to retain: each compaction run creates      * a new segment generation. {@code retainGenerations} determines how many of      * those generations are retained during cleanup.      *      * @param retainedGenerations  number of generations to retain. Must be {@code>= 2}.      * @throws IllegalArgumentException if {@code retainGenerations< 2}      */
name|void
name|setRetainedGenerations
parameter_list|(
name|int
name|retainedGenerations
parameter_list|)
function_decl|;
name|long
name|getGcSizeDeltaEstimation
parameter_list|()
function_decl|;
name|void
name|setGcSizeDeltaEstimation
parameter_list|(
name|long
name|gcSizeDeltaEstimation
parameter_list|)
function_decl|;
comment|/**      * Initiate a revision garbage collection operation      */
name|void
name|startRevisionGC
parameter_list|()
function_decl|;
comment|/**      * Cancel a running revision garbage collection operation. Does nothing      * if revision garbage collection is not running.      */
name|void
name|cancelRevisionGC
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

