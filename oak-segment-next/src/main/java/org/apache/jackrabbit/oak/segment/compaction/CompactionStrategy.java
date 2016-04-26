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
comment|/**  * This class holds configuration options for segment store revision gc.  */
end_comment

begin_class
specifier|public
class|class
name|CompactionStrategy
block|{
comment|/**      * Default options: {@link #PAUSE_DEFAULT}, {@link #MEMORY_THRESHOLD_DEFAULT},      * {@link #GAIN_THRESHOLD_DEFAULT}, {@link #RETRY_COUNT_DEFAULT},      * {@link #FORCE_AFTER_FAIL_DEFAULT}, {@link #LOCK_WAIT_TIME_DEFAULT}.      */
specifier|public
specifier|static
specifier|final
name|CompactionStrategy
name|DEFAULT
init|=
operator|new
name|CompactionStrategy
argument_list|()
decl_stmt|;
comment|/**      * Default value for {@link #isPaused()}      */
specifier|public
specifier|static
specifier|final
name|boolean
name|PAUSE_DEFAULT
init|=
literal|false
decl_stmt|;
comment|/**      * Default value for {@link #getMemoryThreshold()}      */
specifier|public
specifier|static
specifier|final
name|byte
name|MEMORY_THRESHOLD_DEFAULT
init|=
literal|5
decl_stmt|;
comment|/**      * Default value for {@link #getGainThreshold()}      */
specifier|public
specifier|static
specifier|final
name|byte
name|GAIN_THRESHOLD_DEFAULT
init|=
literal|10
decl_stmt|;
comment|/**      * Default value for {@link #getRetryCount()}      */
specifier|public
specifier|static
specifier|final
name|int
name|RETRY_COUNT_DEFAULT
init|=
literal|5
decl_stmt|;
comment|/**      * Default value for {@link #getForceAfterFail()}      */
specifier|public
specifier|static
specifier|final
name|boolean
name|FORCE_AFTER_FAIL_DEFAULT
init|=
literal|false
decl_stmt|;
comment|/**      * Default value for {@link #getLockWaitTime()}      */
specifier|public
specifier|static
specifier|final
name|int
name|LOCK_WAIT_TIME_DEFAULT
init|=
literal|60000
decl_stmt|;
specifier|private
name|boolean
name|paused
init|=
name|PAUSE_DEFAULT
decl_stmt|;
specifier|private
name|int
name|memoryThreshold
init|=
name|MEMORY_THRESHOLD_DEFAULT
decl_stmt|;
specifier|private
name|int
name|gainThreshold
init|=
name|GAIN_THRESHOLD_DEFAULT
decl_stmt|;
specifier|private
name|int
name|retryCount
init|=
name|RETRY_COUNT_DEFAULT
decl_stmt|;
specifier|private
name|boolean
name|forceAfterFail
init|=
name|FORCE_AFTER_FAIL_DEFAULT
decl_stmt|;
specifier|private
name|int
name|lockWaitTime
init|=
name|LOCK_WAIT_TIME_DEFAULT
decl_stmt|;
specifier|public
name|CompactionStrategy
parameter_list|(
name|boolean
name|paused
parameter_list|,
name|int
name|memoryThreshold
parameter_list|,
name|int
name|gainThreshold
parameter_list|,
name|int
name|retryCount
parameter_list|,
name|boolean
name|forceAfterFail
parameter_list|,
name|int
name|lockWaitTime
parameter_list|)
block|{
name|this
operator|.
name|paused
operator|=
name|paused
expr_stmt|;
name|this
operator|.
name|memoryThreshold
operator|=
name|memoryThreshold
expr_stmt|;
name|this
operator|.
name|gainThreshold
operator|=
name|gainThreshold
expr_stmt|;
name|this
operator|.
name|retryCount
operator|=
name|retryCount
expr_stmt|;
name|this
operator|.
name|forceAfterFail
operator|=
name|forceAfterFail
expr_stmt|;
name|this
operator|.
name|lockWaitTime
operator|=
name|lockWaitTime
expr_stmt|;
block|}
specifier|public
name|CompactionStrategy
parameter_list|()
block|{
name|this
argument_list|(
name|PAUSE_DEFAULT
argument_list|,
name|MEMORY_THRESHOLD_DEFAULT
argument_list|,
name|GAIN_THRESHOLD_DEFAULT
argument_list|,
name|RETRY_COUNT_DEFAULT
argument_list|,
name|FORCE_AFTER_FAIL_DEFAULT
argument_list|,
name|LOCK_WAIT_TIME_DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return  {@code true} iff revision gc is paused.      */
specifier|public
name|boolean
name|isPaused
parameter_list|()
block|{
return|return
name|paused
return|;
block|}
comment|/**      * Set revision gc to paused.      * @param paused      * @return this instance      */
specifier|public
name|CompactionStrategy
name|setPaused
parameter_list|(
name|boolean
name|paused
parameter_list|)
block|{
name|this
operator|.
name|paused
operator|=
name|paused
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * @return  the memory threshold below which revision gc will not run.      */
specifier|public
name|int
name|getMemoryThreshold
parameter_list|()
block|{
return|return
name|memoryThreshold
return|;
block|}
comment|/**      * Set the memory threshold below which revision gc will not run.      * @param memoryThreshold      * @return this instance      */
specifier|public
name|CompactionStrategy
name|setMemoryThreshold
parameter_list|(
name|int
name|memoryThreshold
parameter_list|)
block|{
name|this
operator|.
name|memoryThreshold
operator|=
name|memoryThreshold
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the gain estimate threshold beyond which revision gc should run      * @return gainThreshold      */
specifier|public
name|int
name|getGainThreshold
parameter_list|()
block|{
return|return
name|gainThreshold
return|;
block|}
comment|/**      * Set the revision gain estimate threshold beyond which revision gc should run      * @param gainThreshold      * @return this instance      */
specifier|public
name|CompactionStrategy
name|setGainThreshold
parameter_list|(
name|int
name|gainThreshold
parameter_list|)
block|{
name|this
operator|.
name|gainThreshold
operator|=
name|gainThreshold
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the number of tries to compact concurrent commits on top of already      * compacted commits      * @return  retry count      */
specifier|public
name|int
name|getRetryCount
parameter_list|()
block|{
return|return
name|retryCount
return|;
block|}
comment|/**      * Set the number of tries to compact concurrent commits on top of already      * compacted commits      * @param retryCount      * @return this instance      */
specifier|public
name|CompactionStrategy
name|setRetryCount
parameter_list|(
name|int
name|retryCount
parameter_list|)
block|{
name|this
operator|.
name|retryCount
operator|=
name|retryCount
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get whether or not to force compact concurrent commits on top of already      * compacted commits after the maximum number of retries has been reached.      * Force committing tries to exclusively write lock the node store.      * @return  {@code true} if force commit is on, {@code false} otherwise      */
specifier|public
name|boolean
name|getForceAfterFail
parameter_list|()
block|{
return|return
name|forceAfterFail
return|;
block|}
comment|/**      * Set whether or not to force compact concurrent commits on top of already      * compacted commits after the maximum number of retries has been reached.      * Force committing tries to exclusively write lock the node store.      * @param forceAfterFail      * @return this instance      */
specifier|public
name|CompactionStrategy
name|setForceAfterFail
parameter_list|(
name|boolean
name|forceAfterFail
parameter_list|)
block|{
name|this
operator|.
name|forceAfterFail
operator|=
name|forceAfterFail
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the time to wait for the lock when force compacting.      * See {@link #setForceAfterFail(boolean)}      * @return lock wait time in seconds.      */
specifier|public
name|int
name|getLockWaitTime
parameter_list|()
block|{
return|return
name|lockWaitTime
return|;
block|}
comment|/**      * Set the time to wait for the lock when force compacting.      * @param lockWaitTime  lock wait time in seconds      * @return      * @return this instance      */
specifier|public
name|CompactionStrategy
name|setLockWaitTime
parameter_list|(
name|int
name|lockWaitTime
parameter_list|)
block|{
name|this
operator|.
name|lockWaitTime
operator|=
name|lockWaitTime
expr_stmt|;
return|return
name|this
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"{"
operator|+
literal|"paused="
operator|+
name|paused
operator|+
literal|", memoryThreshold="
operator|+
name|memoryThreshold
operator|+
literal|", gainThreshold="
operator|+
name|gainThreshold
operator|+
literal|", retryCount="
operator|+
name|retryCount
operator|+
literal|", forceAfterFail="
operator|+
name|forceAfterFail
operator|+
literal|", lockWaitTime="
operator|+
name|lockWaitTime
operator|+
literal|'}'
return|;
block|}
comment|/**      * Check if the approximate repository size is getting too big compared with      * the available space on disk.      *      * @param repositoryDiskSpace Approximate size of the disk space occupied by      *                            the repository.      * @param availableDiskSpace  Currently available disk space.      * @return {@code true} if the available disk space is considered enough for      * normal repository operations.      */
specifier|public
name|boolean
name|isDiskSpaceSufficient
parameter_list|(
name|long
name|repositoryDiskSpace
parameter_list|,
name|long
name|availableDiskSpace
parameter_list|)
block|{
return|return
name|availableDiskSpace
operator|>
literal|0.25
operator|*
name|repositoryDiskSpace
return|;
block|}
block|}
end_class

end_unit

