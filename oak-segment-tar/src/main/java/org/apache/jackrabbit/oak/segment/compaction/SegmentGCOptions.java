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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * This class holds configuration options for segment store revision gc.  */
end_comment

begin_class
specifier|public
class|class
name|SegmentGCOptions
block|{
comment|/**      * The gc type.      */
specifier|public
enum|enum
name|GCType
block|{
comment|/**          * Full gc: compaction will compact the full head state.          */
name|FULL
block|,
comment|/**          * Tail gc: compaction will compact the diff between the head state created by          * the previous compaction run and the current head state.          */
name|TAIL
block|}
comment|/**      * Default value for {@link #isPaused()}      */
specifier|public
specifier|static
specifier|final
name|boolean
name|PAUSE_DEFAULT
init|=
literal|false
decl_stmt|;
comment|/**      * Default value for {@link #isEstimationDisabled()}      */
specifier|public
specifier|static
specifier|final
name|boolean
name|DISABLE_ESTIMATION_DEFAULT
init|=
literal|false
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
comment|/**      * Default value for {@link #getForceTimeout()} in seconds.      */
specifier|public
specifier|static
specifier|final
name|int
name|FORCE_TIMEOUT_DEFAULT
init|=
literal|60
decl_stmt|;
comment|/**      * Default value for {@link #getRetainedGenerations()}      */
specifier|public
specifier|static
specifier|final
name|int
name|RETAINED_GENERATIONS_DEFAULT
init|=
literal|2
decl_stmt|;
comment|/**      * Default value for {@link #getGcSizeDeltaEstimation()}.      */
specifier|public
specifier|static
specifier|final
name|long
name|SIZE_DELTA_ESTIMATION_DEFAULT
init|=
literal|1024L
operator|*
literal|1024L
operator|*
literal|1024L
decl_stmt|;
comment|/**      * Default value for the gc progress log      */
specifier|public
specifier|static
specifier|final
name|long
name|GC_PROGRESS_LOG_DEFAULT
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Default value for {@link #getMemoryThreshold()}      */
specifier|public
specifier|static
specifier|final
name|int
name|MEMORY_THRESHOLD_DEFAULT
init|=
literal|15
decl_stmt|;
specifier|private
name|boolean
name|paused
init|=
name|PAUSE_DEFAULT
decl_stmt|;
comment|/**      * Flag controlling whether the estimation phase will run before a GC cycle      */
specifier|private
name|boolean
name|estimationDisabled
init|=
name|DISABLE_ESTIMATION_DEFAULT
decl_stmt|;
specifier|private
name|int
name|retryCount
init|=
name|RETRY_COUNT_DEFAULT
decl_stmt|;
specifier|private
name|int
name|forceTimeout
init|=
name|FORCE_TIMEOUT_DEFAULT
decl_stmt|;
specifier|private
name|int
name|retainedGenerations
init|=
name|RETAINED_GENERATIONS_DEFAULT
decl_stmt|;
annotation|@
name|NotNull
specifier|private
name|GCType
name|gcType
init|=
name|GCType
operator|.
name|FULL
decl_stmt|;
specifier|private
name|boolean
name|offline
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|memoryThreshold
init|=
name|MEMORY_THRESHOLD_DEFAULT
decl_stmt|;
specifier|private
name|long
name|gcSizeDeltaEstimation
init|=
name|Long
operator|.
name|getLong
argument_list|(
literal|"oak.segment.compaction.gcSizeDeltaEstimation"
argument_list|,
name|SIZE_DELTA_ESTIMATION_DEFAULT
argument_list|)
decl_stmt|;
comment|/**      * Number of nodes after which an update about the compaction process is logged.      * -1 for never.      */
specifier|private
name|long
name|gcLogInterval
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|SegmentGCOptions
parameter_list|(
name|boolean
name|paused
parameter_list|,
name|int
name|retryCount
parameter_list|,
name|int
name|forceTimeout
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
name|retryCount
operator|=
name|retryCount
expr_stmt|;
name|this
operator|.
name|forceTimeout
operator|=
name|forceTimeout
expr_stmt|;
block|}
specifier|public
name|SegmentGCOptions
parameter_list|()
block|{
name|this
argument_list|(
name|PAUSE_DEFAULT
argument_list|,
name|RETRY_COUNT_DEFAULT
argument_list|,
name|FORCE_TIMEOUT_DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/**      * Default options: {@link #PAUSE_DEFAULT}, {@link #RETRY_COUNT_DEFAULT},      * {@link #FORCE_TIMEOUT_DEFAULT}.      */
specifier|public
specifier|static
name|SegmentGCOptions
name|defaultGCOptions
parameter_list|()
block|{
return|return
operator|new
name|SegmentGCOptions
argument_list|()
return|;
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
name|SegmentGCOptions
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
name|SegmentGCOptions
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
comment|/**      * Get the number of seconds to attempt to force compact concurrent commits on top of      * already compacted commits after the maximum number of retries has been reached.      * Forced compaction acquires an exclusive write lock on the node store.      * @return  the number of seconds until forced compaction gives up and the exclusive      *          write lock on the node store is released.      */
specifier|public
name|int
name|getForceTimeout
parameter_list|()
block|{
return|return
name|forceTimeout
return|;
block|}
comment|/**      * Set the number of seconds to attempt to force compact concurrent commits on top of      * already compacted commits after the maximum number of retries has been reached.      * Forced compaction acquires an exclusively write lock on the node store.      * @param timeout  the number of seconds until forced compaction gives up and the exclusive      *                 lock on the node store is released.      * @return this instance      */
specifier|public
name|SegmentGCOptions
name|setForceTimeout
parameter_list|(
name|int
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|forceTimeout
operator|=
name|timeout
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Number of segment generations to retain.      * @return  number of gc generations.      */
specifier|public
name|int
name|getRetainedGenerations
parameter_list|()
block|{
return|return
name|retainedGenerations
return|;
block|}
comment|/**      * Set the number of segment generations to retain: each compaction run creates      * a new segment generation. {@code retainGenerations} determines how many of      * those generations are retained during cleanup.      *      * @param retainedGenerations  number of generations to retain. Must be {@code>= 2}.      * @return this instance      * @throws IllegalArgumentException if {@code retainGenerations< 2}      */
specifier|public
name|SegmentGCOptions
name|setRetainedGenerations
parameter_list|(
name|int
name|retainedGenerations
parameter_list|)
block|{
name|this
operator|.
name|retainedGenerations
operator|=
name|retainedGenerations
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * @return the currently set gc type.      */
annotation|@
name|NotNull
specifier|public
name|GCType
name|getGCType
parameter_list|()
block|{
return|return
name|gcType
return|;
block|}
comment|/**      * Set the gc type.      * @param gcType  the type of gc to run.      */
specifier|public
name|void
name|setGCType
parameter_list|(
annotation|@
name|NotNull
name|GCType
name|gcType
parameter_list|)
block|{
name|this
operator|.
name|gcType
operator|=
name|gcType
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|offline
condition|)
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
literal|"offline="
operator|+
name|offline
operator|+
literal|", retainedGenerations="
operator|+
name|retainedGenerations
operator|+
literal|"}"
return|;
block|}
else|else
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
literal|", estimationDisabled="
operator|+
name|estimationDisabled
operator|+
literal|", gcSizeDeltaEstimation="
operator|+
name|gcSizeDeltaEstimation
operator|+
literal|", retryCount="
operator|+
name|retryCount
operator|+
literal|", forceTimeout="
operator|+
name|forceTimeout
operator|+
literal|", retainedGenerations="
operator|+
name|retainedGenerations
operator|+
literal|", gcType="
operator|+
name|gcType
operator|+
literal|"}"
return|;
block|}
block|}
comment|/**      * Check if the approximate repository size is getting too big compared with      * the available space on disk.      *      * @param repositoryDiskSpace Approximate size of the disk space occupied by      *                            the repository.      * @param availableDiskSpace  Currently available disk space.      * @return {@code true} if the available disk space is considered enough for      * normal repository operations.      */
specifier|public
specifier|static
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
specifier|public
name|boolean
name|isOffline
parameter_list|()
block|{
return|return
name|offline
return|;
block|}
comment|/**      * Enables the offline compaction mode, allowing for certain optimizations,      * like reducing the retained generation to 1.      * @return this instance      */
specifier|public
name|SegmentGCOptions
name|setOffline
parameter_list|()
block|{
name|this
operator|.
name|offline
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|retainedGenerations
operator|=
literal|1
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getGcSizeDeltaEstimation
parameter_list|()
block|{
return|return
name|gcSizeDeltaEstimation
return|;
block|}
specifier|public
name|SegmentGCOptions
name|setGcSizeDeltaEstimation
parameter_list|(
name|long
name|gcSizeDeltaEstimation
parameter_list|)
block|{
name|this
operator|.
name|gcSizeDeltaEstimation
operator|=
name|gcSizeDeltaEstimation
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the available memory threshold beyond which revision gc will be      * canceled. Value represents a percentage so an value between {@code 0} and      * {@code 100} will be returned.      * @return memoryThreshold      */
specifier|public
name|int
name|getMemoryThreshold
parameter_list|()
block|{
return|return
name|memoryThreshold
return|;
block|}
comment|/**      * Set the available memory threshold beyond which revision gc will be      * canceled. Value represents a percentage so an input between {@code 0} and      * {@code 100} is expected. Setting this to {@code 0} will disable the      * check.      * @param memoryThreshold      * @return this instance      */
specifier|public
name|SegmentGCOptions
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
specifier|public
name|boolean
name|isEstimationDisabled
parameter_list|()
block|{
return|return
name|estimationDisabled
return|;
block|}
comment|/**      * Disables the estimation phase, thus allowing GC to run every time.      * @return this instance      */
specifier|public
name|SegmentGCOptions
name|setEstimationDisabled
parameter_list|(
name|boolean
name|disabled
parameter_list|)
block|{
name|this
operator|.
name|estimationDisabled
operator|=
name|disabled
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the number of nodes after which an update about the compaction process is logged.      * -1 for never.      * @param gcLogInterval  update interval      * @return this instance      */
specifier|public
name|SegmentGCOptions
name|setGCLogInterval
parameter_list|(
name|long
name|gcLogInterval
parameter_list|)
block|{
name|this
operator|.
name|gcLogInterval
operator|=
name|gcLogInterval
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * @return Number of nodes after which an update about the compaction process is logged.      * -1 for never.      */
specifier|public
name|long
name|getGcLogInterval
parameter_list|()
block|{
return|return
name|gcLogInterval
return|;
block|}
block|}
end_class

end_unit

