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
name|file
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
import|import static
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|format
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
name|commons
operator|.
name|IOUtils
operator|.
name|humanReadableByteCount
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
name|segment
operator|.
name|compaction
operator|.
name|SegmentGCOptions
import|;
end_import

begin_class
specifier|public
class|class
name|SizeDeltaGcEstimation
implements|implements
name|GCEstimation
block|{
specifier|private
specifier|final
name|long
name|delta
decl_stmt|;
specifier|private
specifier|final
name|GCJournal
name|gcJournal
decl_stmt|;
specifier|private
specifier|final
name|long
name|totalSize
decl_stmt|;
specifier|private
name|boolean
name|gcNeeded
decl_stmt|;
specifier|private
name|String
name|gcInfo
init|=
literal|"unknown"
decl_stmt|;
specifier|private
name|boolean
name|finished
init|=
literal|false
decl_stmt|;
specifier|public
name|SizeDeltaGcEstimation
parameter_list|(
annotation|@
name|Nonnull
name|SegmentGCOptions
name|opts
parameter_list|,
annotation|@
name|Nonnull
name|GCJournal
name|gcJournal
parameter_list|,
name|long
name|totalSize
parameter_list|)
block|{
name|this
operator|.
name|delta
operator|=
name|checkNotNull
argument_list|(
name|opts
argument_list|)
operator|.
name|getGcSizeDeltaEstimation
argument_list|()
expr_stmt|;
name|this
operator|.
name|gcJournal
operator|=
name|checkNotNull
argument_list|(
name|gcJournal
argument_list|)
expr_stmt|;
name|this
operator|.
name|totalSize
operator|=
name|totalSize
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|gcNeeded
parameter_list|()
block|{
if|if
condition|(
operator|!
name|finished
condition|)
block|{
name|run
argument_list|()
expr_stmt|;
block|}
return|return
name|gcNeeded
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|gcLog
parameter_list|()
block|{
if|if
condition|(
operator|!
name|finished
condition|)
block|{
name|run
argument_list|()
expr_stmt|;
block|}
return|return
name|gcInfo
return|;
block|}
specifier|private
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|finished
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|delta
operator|==
literal|0
condition|)
block|{
name|gcNeeded
operator|=
literal|true
expr_stmt|;
name|gcInfo
operator|=
name|format
argument_list|(
literal|"Estimation skipped because the size delta value equals 0"
argument_list|,
name|delta
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|getPreviousCleanupSize
argument_list|()
operator|<
literal|0
condition|)
block|{
name|gcNeeded
operator|=
literal|true
expr_stmt|;
name|gcInfo
operator|=
name|format
argument_list|(
literal|"Estimation skipped because of missing gc journal data"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|long
name|lastGc
init|=
name|getPreviousCleanupSize
argument_list|()
decl_stmt|;
name|long
name|gain
init|=
name|totalSize
operator|-
name|lastGc
decl_stmt|;
name|long
name|gainP
init|=
literal|100
operator|*
operator|(
name|totalSize
operator|-
name|lastGc
operator|)
operator|/
name|totalSize
decl_stmt|;
name|gcNeeded
operator|=
name|gain
operator|>
name|delta
expr_stmt|;
if|if
condition|(
name|gcNeeded
condition|)
block|{
name|gcInfo
operator|=
name|format
argument_list|(
literal|"Size delta is %s%% or %s/%s (%s/%s bytes), so running compaction"
argument_list|,
name|gainP
argument_list|,
name|humanReadableByteCount
argument_list|(
name|lastGc
argument_list|)
argument_list|,
name|humanReadableByteCount
argument_list|(
name|totalSize
argument_list|)
argument_list|,
name|lastGc
argument_list|,
name|totalSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|gcInfo
operator|=
name|format
argument_list|(
literal|"Size delta is %s%% or %s/%s (%s/%s bytes), so skipping compaction for now"
argument_list|,
name|gainP
argument_list|,
name|humanReadableByteCount
argument_list|(
name|lastGc
argument_list|)
argument_list|,
name|humanReadableByteCount
argument_list|(
name|totalSize
argument_list|)
argument_list|,
name|lastGc
argument_list|,
name|totalSize
argument_list|)
expr_stmt|;
block|}
block|}
name|finished
operator|=
literal|true
expr_stmt|;
block|}
specifier|private
name|long
name|getPreviousCleanupSize
parameter_list|()
block|{
return|return
name|gcJournal
operator|.
name|read
argument_list|()
operator|.
name|getSize
argument_list|()
return|;
block|}
block|}
end_class

end_unit

