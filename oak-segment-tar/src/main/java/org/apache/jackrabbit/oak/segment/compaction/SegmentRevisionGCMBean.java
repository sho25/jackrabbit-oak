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
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|commons
operator|.
name|jmx
operator|.
name|AnnotatedStandardMBean
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|segment
operator|.
name|file
operator|.
name|FileStoreGCMonitor
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
name|segment
operator|.
name|file
operator|.
name|GCType
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentRevisionGCMBean
extends|extends
name|AnnotatedStandardMBean
implements|implements
name|SegmentRevisionGC
block|{
annotation|@
name|Nonnull
specifier|private
specifier|final
name|FileStore
name|fileStore
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|SegmentGCOptions
name|gcOptions
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|FileStoreGCMonitor
name|fileStoreGCMonitor
decl_stmt|;
specifier|public
name|SegmentRevisionGCMBean
parameter_list|(
annotation|@
name|Nonnull
name|FileStore
name|fileStore
parameter_list|,
annotation|@
name|Nonnull
name|SegmentGCOptions
name|gcOptions
parameter_list|,
annotation|@
name|Nonnull
name|FileStoreGCMonitor
name|fileStoreGCMonitor
parameter_list|)
block|{
name|super
argument_list|(
name|SegmentRevisionGC
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|fileStore
operator|=
name|checkNotNull
argument_list|(
name|fileStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|gcOptions
operator|=
name|checkNotNull
argument_list|(
name|gcOptions
argument_list|)
expr_stmt|;
name|this
operator|.
name|fileStoreGCMonitor
operator|=
name|checkNotNull
argument_list|(
name|fileStoreGCMonitor
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< SegmentRevisionGC>---
annotation|@
name|Override
specifier|public
name|boolean
name|isPausedCompaction
parameter_list|()
block|{
return|return
name|gcOptions
operator|.
name|isPaused
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPausedCompaction
parameter_list|(
name|boolean
name|paused
parameter_list|)
block|{
name|gcOptions
operator|.
name|setPaused
argument_list|(
name|paused
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getRetryCount
parameter_list|()
block|{
return|return
name|gcOptions
operator|.
name|getRetryCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setRetryCount
parameter_list|(
name|int
name|retryCount
parameter_list|)
block|{
name|gcOptions
operator|.
name|setRetryCount
argument_list|(
name|retryCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getForceTimeout
parameter_list|()
block|{
return|return
name|gcOptions
operator|.
name|getForceTimeout
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setForceTimeout
parameter_list|(
name|int
name|timeout
parameter_list|)
block|{
name|gcOptions
operator|.
name|setForceTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getRetainedGenerations
parameter_list|()
block|{
return|return
name|gcOptions
operator|.
name|getRetainedGenerations
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setRetainedGenerations
parameter_list|(
name|int
name|retainedGenerations
parameter_list|)
block|{
name|gcOptions
operator|.
name|setRetainedGenerations
argument_list|(
name|retainedGenerations
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getGcSizeDeltaEstimation
parameter_list|()
block|{
return|return
name|gcOptions
operator|.
name|getGcSizeDeltaEstimation
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setGcSizeDeltaEstimation
parameter_list|(
name|long
name|gcSizeDeltaEstimation
parameter_list|)
block|{
name|gcOptions
operator|.
name|setGcSizeDeltaEstimation
argument_list|(
name|gcSizeDeltaEstimation
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEstimationDisabled
parameter_list|()
block|{
return|return
name|gcOptions
operator|.
name|isEstimationDisabled
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setEstimationDisabled
parameter_list|(
name|boolean
name|disabled
parameter_list|)
block|{
name|gcOptions
operator|.
name|setEstimationDisabled
argument_list|(
name|disabled
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getGCType
parameter_list|()
block|{
return|return
name|fileStore
operator|.
name|getGcType
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setGCType
parameter_list|(
name|String
name|gcType
parameter_list|)
block|{
name|fileStore
operator|.
name|setGcType
argument_list|(
name|GCType
operator|.
name|valueOf
argument_list|(
name|gcType
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startRevisionGC
parameter_list|()
block|{
name|fileStore
operator|.
name|getGCRunner
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|cancelRevisionGC
parameter_list|()
block|{
name|fileStore
operator|.
name|cancelGC
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLastCompaction
parameter_list|()
block|{
return|return
name|fileStoreGCMonitor
operator|.
name|getLastCompaction
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLastCleanup
parameter_list|()
block|{
return|return
name|fileStoreGCMonitor
operator|.
name|getLastCleanup
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLastRepositorySize
parameter_list|()
block|{
return|return
name|fileStoreGCMonitor
operator|.
name|getLastRepositorySize
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLastReclaimedSize
parameter_list|()
block|{
return|return
name|fileStoreGCMonitor
operator|.
name|getLastReclaimedSize
argument_list|()
return|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|String
name|getLastError
parameter_list|()
block|{
return|return
name|fileStoreGCMonitor
operator|.
name|getLastError
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getLastLogMessage
parameter_list|()
block|{
return|return
name|fileStoreGCMonitor
operator|.
name|getLastLogMessage
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getStatus
parameter_list|()
block|{
return|return
name|fileStoreGCMonitor
operator|.
name|getStatus
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMemoryThreshold
parameter_list|()
block|{
return|return
name|gcOptions
operator|.
name|getMemoryThreshold
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMemoryThreshold
parameter_list|(
name|int
name|memoryThreshold
parameter_list|)
block|{
name|gcOptions
operator|.
name|setMemoryThreshold
argument_list|(
name|memoryThreshold
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isRevisionGCRunning
parameter_list|()
block|{
return|return
name|gcOptions
operator|.
name|getGCNodeWriteMonitor
argument_list|()
operator|.
name|isCompactionRunning
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getCompactedNodes
parameter_list|()
block|{
return|return
name|gcOptions
operator|.
name|getGCNodeWriteMonitor
argument_list|()
operator|.
name|getCompactedNodes
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getEstimatedCompactableNodes
parameter_list|()
block|{
return|return
name|gcOptions
operator|.
name|getGCNodeWriteMonitor
argument_list|()
operator|.
name|getEstimatedTotal
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getEstimatedRevisionGCCompletion
parameter_list|()
block|{
return|return
name|gcOptions
operator|.
name|getGCNodeWriteMonitor
argument_list|()
operator|.
name|getEstimatedPercentage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getRevisionGCProgressLog
parameter_list|()
block|{
return|return
name|gcOptions
operator|.
name|getGCNodeWriteMonitor
argument_list|()
operator|.
name|getGcProgressLog
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setRevisionGCProgressLog
parameter_list|(
name|long
name|gcProgressLog
parameter_list|)
block|{
name|gcOptions
operator|.
name|getGCNodeWriteMonitor
argument_list|()
operator|.
name|setGcProgressLog
argument_list|(
name|gcProgressLog
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

