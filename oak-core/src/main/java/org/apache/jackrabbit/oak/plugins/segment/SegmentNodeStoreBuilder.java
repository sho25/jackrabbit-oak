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
name|oak
operator|.
name|plugins
operator|.
name|segment
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
name|checkState
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|SECONDS
import|;
end_import

begin_import
import|import static
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
operator|.
name|CompactionStrategy
operator|.
name|NO_COMPACTION
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
name|compaction
operator|.
name|CompactionStrategy
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
name|compaction
operator|.
name|CompactionStrategy
operator|.
name|CleanupType
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentNodeStoreBuilder
block|{
specifier|private
specifier|final
name|SegmentStore
name|store
decl_stmt|;
specifier|private
name|boolean
name|isCreated
decl_stmt|;
specifier|private
name|boolean
name|hasCompactionStrategy
decl_stmt|;
specifier|private
name|boolean
name|pauseCompaction
decl_stmt|;
specifier|private
name|boolean
name|cloneBinaries
decl_stmt|;
specifier|private
name|String
name|cleanup
decl_stmt|;
specifier|private
name|long
name|cleanupTs
decl_stmt|;
specifier|private
name|byte
name|memoryThreshold
decl_stmt|;
specifier|private
name|int
name|lockWaitTime
decl_stmt|;
specifier|private
name|int
name|retryCount
decl_stmt|;
specifier|private
name|boolean
name|forceAfterFail
decl_stmt|;
specifier|private
name|boolean
name|persistCompactionMap
decl_stmt|;
specifier|private
name|CompactionStrategy
name|compactionStrategy
decl_stmt|;
specifier|static
name|SegmentNodeStoreBuilder
name|newSegmentNodeStore
parameter_list|(
name|SegmentStore
name|store
parameter_list|)
block|{
return|return
operator|new
name|SegmentNodeStoreBuilder
argument_list|(
name|store
argument_list|)
return|;
block|}
specifier|private
name|SegmentNodeStoreBuilder
parameter_list|(
annotation|@
name|Nonnull
name|SegmentStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
specifier|public
name|SegmentNodeStoreBuilder
name|withCompactionStrategy
parameter_list|(
name|boolean
name|pauseCompaction
parameter_list|,
name|boolean
name|cloneBinaries
parameter_list|,
name|String
name|cleanup
parameter_list|,
name|long
name|cleanupTs
parameter_list|,
name|byte
name|memoryThreshold
parameter_list|,
specifier|final
name|int
name|lockWaitTime
parameter_list|,
name|int
name|retryCount
parameter_list|,
name|boolean
name|forceAfterFail
parameter_list|,
name|boolean
name|persistCompactionMap
parameter_list|)
block|{
name|this
operator|.
name|hasCompactionStrategy
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|pauseCompaction
operator|=
name|pauseCompaction
expr_stmt|;
name|this
operator|.
name|cloneBinaries
operator|=
name|cloneBinaries
expr_stmt|;
name|this
operator|.
name|cleanup
operator|=
name|cleanup
expr_stmt|;
name|this
operator|.
name|cleanupTs
operator|=
name|cleanupTs
expr_stmt|;
name|this
operator|.
name|memoryThreshold
operator|=
name|memoryThreshold
expr_stmt|;
name|this
operator|.
name|lockWaitTime
operator|=
name|lockWaitTime
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
name|persistCompactionMap
operator|=
name|persistCompactionMap
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|CompactionStrategy
name|getCompactionStrategy
parameter_list|()
block|{
name|checkState
argument_list|(
name|isCreated
argument_list|)
expr_stmt|;
return|return
name|compactionStrategy
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|SegmentNodeStore
name|create
parameter_list|()
block|{
name|checkState
argument_list|(
operator|!
name|isCreated
argument_list|)
expr_stmt|;
name|isCreated
operator|=
literal|true
expr_stmt|;
specifier|final
name|SegmentNodeStore
name|segmentStore
init|=
operator|new
name|SegmentNodeStore
argument_list|(
name|store
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|hasCompactionStrategy
condition|)
block|{
name|compactionStrategy
operator|=
operator|new
name|CompactionStrategy
argument_list|(
name|pauseCompaction
argument_list|,
name|cloneBinaries
argument_list|,
name|CleanupType
operator|.
name|valueOf
argument_list|(
name|cleanup
argument_list|)
argument_list|,
name|cleanupTs
argument_list|,
name|memoryThreshold
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|compacted
parameter_list|(
name|Callable
argument_list|<
name|Boolean
argument_list|>
name|setHead
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Need to guard against concurrent commits to avoid
comment|// mixed segments. See OAK-2192.
return|return
name|segmentStore
operator|.
name|locked
argument_list|(
name|setHead
argument_list|,
name|lockWaitTime
argument_list|,
name|SECONDS
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|compactionStrategy
operator|.
name|setRetryCount
argument_list|(
name|retryCount
argument_list|)
expr_stmt|;
name|compactionStrategy
operator|.
name|setForceAfterFail
argument_list|(
name|forceAfterFail
argument_list|)
expr_stmt|;
name|compactionStrategy
operator|.
name|setPersistCompactionMap
argument_list|(
name|persistCompactionMap
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|compactionStrategy
operator|=
name|NO_COMPACTION
expr_stmt|;
block|}
return|return
name|segmentStore
return|;
block|}
block|}
end_class

end_unit

