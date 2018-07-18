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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|gc
operator|.
name|GCMonitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * This MBean exposes the settings from {@link SegmentGCOptions} and  * reflects the GC status as reported by the {@link GCMonitor}.  */
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
name|boolean
name|isEstimationDisabled
parameter_list|()
function_decl|;
comment|/**      * Disables the estimation phase, thus allowing GC to run every time.      * @param disabled      */
name|void
name|setEstimationDisabled
parameter_list|(
name|boolean
name|disabled
parameter_list|)
function_decl|;
name|String
name|getGCType
parameter_list|()
function_decl|;
name|void
name|setGCType
parameter_list|(
name|String
name|gcType
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
comment|/**      * @return  time of the last compaction in milliseconds.      */
name|long
name|getLastCompaction
parameter_list|()
function_decl|;
comment|/**      * @return  time of the last cleanup in milliseconds.      */
name|long
name|getLastCleanup
parameter_list|()
function_decl|;
comment|/**      * @return  repository size after the last cleanup.      */
name|long
name|getLastRepositorySize
parameter_list|()
function_decl|;
comment|/**      * @return  reclaimed size during the last cleanup.      */
name|long
name|getLastReclaimedSize
parameter_list|()
function_decl|;
comment|/**      * @return  last error or {@code null} if none.      */
annotation|@
name|Nullable
name|String
name|getLastError
parameter_list|()
function_decl|;
comment|/**      * @return  last log message or {@code null} if none.      */
annotation|@
name|NotNull
name|String
name|getLastLogMessage
parameter_list|()
function_decl|;
comment|/**      * @return  current status.      */
annotation|@
name|NotNull
name|String
name|getStatus
parameter_list|()
function_decl|;
comment|/**      * Get the available memory threshold beyond which revision gc will be      * canceled. Value represents a percentage so an value between 0 and 100      * will be returned.      * @return memory threshold      */
name|int
name|getMemoryThreshold
parameter_list|()
function_decl|;
comment|/**      * Set the available memory threshold beyond which revision gc will be      * canceled. Value represents a percentage so an input between {@code 0} and      * {@code 100} is expected. Setting this to {@code 0} will disable the      * check.      * @param memoryThreshold      */
name|void
name|setMemoryThreshold
parameter_list|(
name|int
name|memoryThreshold
parameter_list|)
function_decl|;
comment|/**      * @return {@code true} if there is an online compaction cycle running      */
name|boolean
name|isRevisionGCRunning
parameter_list|()
function_decl|;
comment|/**      * @return number of compacted nodes in the current cycle      */
name|long
name|getCompactedNodes
parameter_list|()
function_decl|;
comment|/**      * @return number of estimated nodes to be compacted in the current cycle.      *         Can be {@code -1} if the estimation can't be performed      */
name|long
name|getEstimatedCompactableNodes
parameter_list|()
function_decl|;
comment|/**      * @return percentage of progress for the current compaction cycle. Can be      *         {@code -1} if the estimation can't be performed.      */
name|int
name|getEstimatedRevisionGCCompletion
parameter_list|()
function_decl|;
comment|/**      * @return Number of nodes the monitor will log a message, {@code -1} means disabled      */
name|long
name|getRevisionGCProgressLog
parameter_list|()
function_decl|;
comment|/**      * Set the size of the logging interval, {@code -1} means disabled      * @param gcProgressLog      *            number of nodes      */
name|void
name|setRevisionGCProgressLog
parameter_list|(
name|long
name|gcProgressLog
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

