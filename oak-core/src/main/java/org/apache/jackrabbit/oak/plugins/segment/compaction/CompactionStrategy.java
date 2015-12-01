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
name|segment
operator|.
name|compaction
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
name|checkNotNull
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|System
operator|.
name|currentTimeMillis
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
name|Callable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

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
name|plugins
operator|.
name|segment
operator|.
name|SegmentId
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|CompactionStrategy
block|{
specifier|public
enum|enum
name|CleanupType
block|{
comment|/**          * {@code CLEAN_ALL}<em>must</em> be used in conjunction with {@code cloneBinaries}          * otherwise segments can go away ({@code SegmentNotFoundException})          *<p>          * Pros: best compaction results          *<p>          * Cons: larger repo size<em>during</em> compaction (2x). High chances that a currently          * running diff (e.g. observation) fails with {@code SegmentNotFoundException}.          */
name|CLEAN_ALL
block|,
name|CLEAN_NONE
block|,
comment|/**          * {@code CLEAN_OLD} with {@code cloneBinaries}          *<p>          * Pros: better compaction results          *<p>          * Cons: larger repo size {@code during} compaction (2x). {@code SegmentNotFoundException}          * with insufficiently large values for {@code olderThan}.          *<p>          * {@code CLEAN_OLD} without {@code cloneBinaries}          *<p>          * Pros: weakest compaction results, smaller size during compaction (1x + size of          * data-segments).          *<p>          * Cons: {@code SegmentNotFoundException} with insufficiently large values for          * {@code olderThan}.          */
name|CLEAN_OLD
block|}
specifier|public
specifier|static
specifier|final
name|boolean
name|PAUSE_DEFAULT
init|=
literal|false
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|CLONE_BINARIES_DEFAULT
init|=
literal|false
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|CleanupType
name|CLEANUP_DEFAULT
init|=
name|CleanupType
operator|.
name|CLEAN_OLD
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|TIMESTAMP_DEFAULT
init|=
literal|1000
operator|*
literal|60
operator|*
literal|60
operator|*
literal|10
decl_stmt|;
comment|// 10h
specifier|public
specifier|static
specifier|final
name|byte
name|MEMORY_THRESHOLD_DEFAULT
init|=
literal|5
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|PERSIST_COMPACTION_MAP_DEFAULT
init|=
literal|true
decl_stmt|;
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
comment|/**      * No compaction at all      */
specifier|public
specifier|static
specifier|final
name|CompactionStrategy
name|NO_COMPACTION
init|=
operator|new
name|CompactionStrategy
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
name|CleanupType
operator|.
name|CLEAN_NONE
argument_list|,
literal|0
argument_list|,
name|MEMORY_THRESHOLD_DEFAULT
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|compacted
parameter_list|(
annotation|@
name|Nonnull
name|Callable
argument_list|<
name|Boolean
argument_list|>
name|setHead
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|false
return|;
block|}
block|}
decl_stmt|;
specifier|private
name|boolean
name|paused
decl_stmt|;
specifier|private
name|boolean
name|cloneBinaries
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
name|CleanupType
name|cleanupType
decl_stmt|;
comment|/**      * anything that has a lifetime bigger than this will be removed. a value of      * 0 (or very small) acts like a CLEANUP.NONE, a value of -1 (or negative)      * acts like a CLEANUP.ALL      *       */
specifier|private
name|long
name|olderThan
decl_stmt|;
specifier|private
name|byte
name|memoryThreshold
init|=
name|MEMORY_THRESHOLD_DEFAULT
decl_stmt|;
specifier|private
name|boolean
name|persistedCompactionMap
init|=
name|PERSIST_COMPACTION_MAP_DEFAULT
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
name|long
name|compactionStart
init|=
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|/**      * Compaction gain estimate threshold beyond which compaction should run      */
specifier|private
name|byte
name|gainThreshold
init|=
name|GAIN_THRESHOLD_DEFAULT
decl_stmt|;
comment|/**      * Flag that allows turning on an optimized version of the compaction      * process in the case of offline compaction      */
specifier|private
name|boolean
name|offlineCompaction
init|=
literal|false
decl_stmt|;
specifier|protected
name|CompactionStrategy
parameter_list|(
name|boolean
name|paused
parameter_list|,
name|boolean
name|cloneBinaries
parameter_list|,
annotation|@
name|Nonnull
name|CleanupType
name|cleanupType
parameter_list|,
name|long
name|olderThan
parameter_list|,
name|byte
name|memoryThreshold
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|olderThan
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|paused
operator|=
name|paused
expr_stmt|;
name|this
operator|.
name|cloneBinaries
operator|=
name|cloneBinaries
expr_stmt|;
name|this
operator|.
name|cleanupType
operator|=
name|checkNotNull
argument_list|(
name|cleanupType
argument_list|)
expr_stmt|;
name|this
operator|.
name|olderThan
operator|=
name|olderThan
expr_stmt|;
name|this
operator|.
name|memoryThreshold
operator|=
name|memoryThreshold
expr_stmt|;
block|}
specifier|public
name|boolean
name|canRemove
parameter_list|(
name|SegmentId
name|id
parameter_list|)
block|{
switch|switch
condition|(
name|cleanupType
condition|)
block|{
case|case
name|CLEAN_ALL
case|:
return|return
literal|true
return|;
case|case
name|CLEAN_NONE
case|:
return|return
literal|false
return|;
case|case
name|CLEAN_OLD
case|:
return|return
name|compactionStart
operator|-
name|id
operator|.
name|getCreationTime
argument_list|()
operator|>
name|olderThan
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|cloneBinaries
parameter_list|()
block|{
return|return
name|cloneBinaries
return|;
block|}
specifier|public
name|boolean
name|isPaused
parameter_list|()
block|{
return|return
name|paused
return|;
block|}
specifier|public
name|void
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
block|}
specifier|public
name|void
name|setCloneBinaries
parameter_list|(
name|boolean
name|cloneBinaries
parameter_list|)
block|{
name|this
operator|.
name|cloneBinaries
operator|=
name|cloneBinaries
expr_stmt|;
block|}
specifier|public
name|void
name|setCleanupType
parameter_list|(
annotation|@
name|Nonnull
name|CleanupType
name|cleanupType
parameter_list|)
block|{
name|this
operator|.
name|cleanupType
operator|=
name|checkNotNull
argument_list|(
name|cleanupType
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setOlderThan
parameter_list|(
name|long
name|olderThan
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|olderThan
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|olderThan
operator|=
name|olderThan
expr_stmt|;
block|}
name|String
name|getCleanupType
parameter_list|()
block|{
return|return
name|cleanupType
operator|.
name|toString
argument_list|()
return|;
block|}
name|long
name|getOlderThan
parameter_list|()
block|{
return|return
name|olderThan
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
literal|"CompactionStrategy{"
operator|+
literal|"paused="
operator|+
name|paused
operator|+
literal|", cloneBinaries="
operator|+
name|cloneBinaries
operator|+
literal|", cleanupType="
operator|+
name|cleanupType
operator|+
literal|", olderThan="
operator|+
name|olderThan
operator|+
literal|", memoryThreshold="
operator|+
name|memoryThreshold
operator|+
literal|", persistedCompactionMap="
operator|+
name|persistedCompactionMap
operator|+
literal|", retryCount="
operator|+
name|retryCount
operator|+
literal|", forceAfterFail="
operator|+
name|forceAfterFail
operator|+
literal|", compactionStart="
operator|+
name|compactionStart
operator|+
literal|", offlineCompaction="
operator|+
name|offlineCompaction
operator|+
literal|'}'
return|;
block|}
specifier|public
name|void
name|setCompactionStart
parameter_list|(
name|long
name|ms
parameter_list|)
block|{
name|this
operator|.
name|compactionStart
operator|=
name|ms
expr_stmt|;
block|}
specifier|public
name|byte
name|getMemoryThreshold
parameter_list|()
block|{
return|return
name|memoryThreshold
return|;
block|}
specifier|public
name|void
name|setMemoryThreshold
parameter_list|(
name|byte
name|memoryThreshold
parameter_list|)
block|{
name|this
operator|.
name|memoryThreshold
operator|=
name|memoryThreshold
expr_stmt|;
block|}
specifier|public
name|boolean
name|getPersistCompactionMap
parameter_list|()
block|{
return|return
name|persistedCompactionMap
return|;
block|}
specifier|public
name|void
name|setPersistCompactionMap
parameter_list|(
name|boolean
name|persist
parameter_list|)
block|{
name|persistedCompactionMap
operator|=
name|persist
expr_stmt|;
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
comment|/**      * Set whether or not to force compact concurrent commits on top of already      * compacted commits after the maximum number of retries has been reached.      * Force committing tries to exclusively write lock the node store.      * @param forceAfterFail      */
specifier|public
name|void
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
comment|/**      * Set the number of tries to compact concurrent commits on top of already      * compacted commits      * @param retryCount      */
specifier|public
name|void
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
block|}
comment|/**      * Get the compaction gain estimate threshold beyond which compaction should      * run      * @return gainThreshold      */
specifier|public
name|byte
name|getGainThreshold
parameter_list|()
block|{
return|return
name|gainThreshold
return|;
block|}
comment|/**      * Set the compaction gain estimate threshold beyond which compaction should      * run      * @param gainThreshold      */
specifier|public
name|void
name|setGainThreshold
parameter_list|(
name|byte
name|gainThreshold
parameter_list|)
block|{
name|this
operator|.
name|gainThreshold
operator|=
name|gainThreshold
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|boolean
name|compacted
parameter_list|(
annotation|@
name|Nonnull
name|Callable
argument_list|<
name|Boolean
argument_list|>
name|setHead
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|public
name|boolean
name|isOfflineCompaction
parameter_list|()
block|{
return|return
name|offlineCompaction
return|;
block|}
specifier|public
name|void
name|setOfflineCompaction
parameter_list|(
name|boolean
name|offlineCompaction
parameter_list|)
block|{
name|this
operator|.
name|offlineCompaction
operator|=
name|offlineCompaction
expr_stmt|;
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

