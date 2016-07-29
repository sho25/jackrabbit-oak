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
name|commons
operator|.
name|jmx
operator|.
name|AnnotatedStandardMBean
import|;
end_import

begin_comment
comment|// FIXME OAK-4617: Align SegmentRevisionGC MBean with new generation based GC
end_comment

begin_class
specifier|public
class|class
name|SegmentRevisionGCMBean
extends|extends
name|AnnotatedStandardMBean
implements|implements
name|SegmentRevisionGC
block|{
specifier|private
specifier|final
name|SegmentGCOptions
name|gcOptions
decl_stmt|;
specifier|public
name|SegmentRevisionGCMBean
parameter_list|(
name|SegmentGCOptions
name|gcOptions
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
name|gcOptions
operator|=
name|gcOptions
expr_stmt|;
block|}
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
name|getGainThreshold
parameter_list|()
block|{
return|return
name|gcOptions
operator|.
name|getGainThreshold
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setGainThreshold
parameter_list|(
name|int
name|gainThreshold
parameter_list|)
block|{
name|gcOptions
operator|.
name|setGainThreshold
argument_list|(
name|gainThreshold
argument_list|)
expr_stmt|;
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
name|boolean
name|getForceAfterFail
parameter_list|()
block|{
return|return
name|gcOptions
operator|.
name|getForceAfterFail
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setForceAfterFail
parameter_list|(
name|boolean
name|forceAfterFail
parameter_list|)
block|{
name|gcOptions
operator|.
name|setForceAfterFail
argument_list|(
name|forceAfterFail
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getLockWaitTime
parameter_list|()
block|{
return|return
name|gcOptions
operator|.
name|getLockWaitTime
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setLockWaitTime
parameter_list|(
name|int
name|lockWaitTime
parameter_list|)
block|{
name|gcOptions
operator|.
name|setLockWaitTime
argument_list|(
name|lockWaitTime
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
block|}
end_class

end_unit

