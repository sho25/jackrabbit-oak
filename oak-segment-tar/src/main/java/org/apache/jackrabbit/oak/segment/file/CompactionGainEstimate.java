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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Type
operator|.
name|BINARIES
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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|hash
operator|.
name|BloomFilter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|hash
operator|.
name|Funnel
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|hash
operator|.
name|PrimitiveSink
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
name|api
operator|.
name|Blob
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
name|api
operator|.
name|PropertyState
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
name|RecordIdSet
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
name|SegmentBlob
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
name|SegmentId
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
name|SegmentNodeState
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
name|SegmentPropertyState
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
name|spi
operator|.
name|state
operator|.
name|ChildNodeEntry
import|;
end_import

begin_class
class|class
name|CompactionGainEstimate
implements|implements
name|TarEntryVisitor
implements|,
name|GCEstimation
block|{
specifier|private
specifier|static
specifier|final
name|Funnel
argument_list|<
name|UUID
argument_list|>
name|UUID_FUNNEL
init|=
operator|new
name|Funnel
argument_list|<
name|UUID
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|funnel
parameter_list|(
name|UUID
name|from
parameter_list|,
name|PrimitiveSink
name|into
parameter_list|)
block|{
name|into
operator|.
name|putLong
argument_list|(
name|from
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|into
operator|.
name|putLong
argument_list|(
name|from
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|BloomFilter
argument_list|<
name|UUID
argument_list|>
name|uuids
decl_stmt|;
specifier|private
specifier|final
name|int
name|gainThreshold
decl_stmt|;
specifier|private
name|long
name|totalSize
init|=
literal|0
decl_stmt|;
specifier|private
name|long
name|reachableSize
init|=
literal|0
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
comment|/**      * Create a new instance of gain estimator. The estimation process can be stopped      * by switching the supplier {@code stop} to {@code true}, in which case the returned      * estimates are undefined.      *      * @param node  root node state      * @param estimatedBulkCount      * @param stop  stop signal      */
name|CompactionGainEstimate
parameter_list|(
name|SegmentNodeState
name|node
parameter_list|,
name|int
name|estimatedBulkCount
parameter_list|,
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|stop
parameter_list|,
name|int
name|gainThreshold
parameter_list|)
block|{
name|uuids
operator|=
name|BloomFilter
operator|.
name|create
argument_list|(
name|UUID_FUNNEL
argument_list|,
name|estimatedBulkCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|gainThreshold
operator|=
name|gainThreshold
expr_stmt|;
name|collectReferencedSegments
argument_list|(
name|node
argument_list|,
operator|new
name|RecordIdSet
argument_list|()
argument_list|,
name|stop
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|collectReferencedSegments
parameter_list|(
name|SegmentNodeState
name|node
parameter_list|,
name|RecordIdSet
name|visited
parameter_list|,
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|stop
parameter_list|)
block|{
if|if
condition|(
operator|!
name|stop
operator|.
name|get
argument_list|()
operator|&&
name|visited
operator|.
name|addIfNotPresent
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
name|collectUUID
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
operator|.
name|getSegmentId
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|node
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
name|property
operator|instanceof
name|SegmentPropertyState
condition|)
block|{
name|collectUUID
argument_list|(
operator|(
operator|(
name|SegmentPropertyState
operator|)
name|property
operator|)
operator|.
name|getRecordId
argument_list|()
operator|.
name|getSegmentId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Get the underlying value as stream so we can collect
comment|// the segments ids involved in storing the value.
comment|// This works as primitives are stored as strings and strings
comment|// as binaries of their UTF-8 encoding.
for|for
control|(
name|Blob
name|blob
range|:
name|property
operator|.
name|getValue
argument_list|(
name|BINARIES
argument_list|)
control|)
block|{
for|for
control|(
name|SegmentId
name|id
range|:
name|SegmentBlob
operator|.
name|getBulkSegmentIds
argument_list|(
name|blob
argument_list|)
control|)
block|{
name|collectUUID
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|ChildNodeEntry
name|child
range|:
name|node
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|collectReferencedSegments
argument_list|(
operator|(
name|SegmentNodeState
operator|)
name|child
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|visited
argument_list|,
name|stop
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|collectUUID
parameter_list|(
name|SegmentId
name|segmentId
parameter_list|)
block|{
name|uuids
operator|.
name|put
argument_list|(
operator|new
name|UUID
argument_list|(
name|segmentId
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|segmentId
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns a percentage estimate (scale 0-100) for how much disk space      * running compaction (and cleanup) could potentially release.      *      * @return percentage of disk space that could be freed with compaction      */
specifier|public
name|long
name|estimateCompactionGain
parameter_list|()
block|{
if|if
condition|(
name|totalSize
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
literal|100
operator|*
operator|(
name|totalSize
operator|-
name|reachableSize
operator|)
operator|/
name|totalSize
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
name|long
name|gain
init|=
name|estimateCompactionGain
argument_list|()
decl_stmt|;
name|gcNeeded
operator|=
name|gain
operator|>=
name|gainThreshold
expr_stmt|;
if|if
condition|(
name|gcNeeded
condition|)
block|{
name|gcInfo
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"Gain is %s%% or %s/%s (%s/%s bytes), so running compaction"
argument_list|,
name|gain
argument_list|,
name|humanReadableByteCount
argument_list|(
name|reachableSize
argument_list|)
argument_list|,
name|humanReadableByteCount
argument_list|(
name|totalSize
argument_list|)
argument_list|,
name|reachableSize
argument_list|,
name|totalSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|totalSize
operator|==
literal|0
condition|)
block|{
name|gcInfo
operator|=
literal|"Skipping compaction for now as repository consists of a single tar file only"
expr_stmt|;
block|}
else|else
block|{
name|gcInfo
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"Gain is %s%% or %s/%s (%s/%s bytes), so skipping compaction for now"
argument_list|,
name|gain
argument_list|,
name|humanReadableByteCount
argument_list|(
name|reachableSize
argument_list|)
argument_list|,
name|humanReadableByteCount
argument_list|(
name|totalSize
argument_list|)
argument_list|,
name|reachableSize
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
comment|// ---------------------------------------------------< TarEntryVisitor>--
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|,
name|File
name|file
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|UUID
name|uuid
init|=
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
decl_stmt|;
name|int
name|entrySize
init|=
name|TarReader
operator|.
name|getEntrySize
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|totalSize
operator|+=
name|entrySize
expr_stmt|;
if|if
condition|(
name|uuids
operator|.
name|mightContain
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
name|reachableSize
operator|+=
name|entrySize
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

