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
name|api
operator|.
name|Type
operator|.
name|BINARY
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
name|api
operator|.
name|Type
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
name|plugins
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
name|plugins
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
name|plugins
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
name|plugins
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
name|CompactionGainEstimate
parameter_list|(
name|SegmentNodeState
name|node
parameter_list|,
name|int
name|estimatedBulkCount
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
name|collectReferencedSegments
argument_list|(
name|node
argument_list|,
operator|new
name|RecordIdSet
argument_list|()
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
parameter_list|)
block|{
if|if
condition|(
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
name|Type
argument_list|<
name|?
argument_list|>
name|type
init|=
name|property
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|BINARY
condition|)
block|{
name|Blob
name|blob
init|=
name|property
operator|.
name|getValue
argument_list|(
name|BINARY
argument_list|)
decl_stmt|;
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
elseif|else
if|if
condition|(
name|type
operator|==
name|BINARIES
condition|)
block|{
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
comment|/**      * Returns a percentage estimate (scale 0-100) for how much disk space      * running compaction (and cleanup) could potentially release.      *      * @param offset  number of bytes to offset the reachable size with      * @return percentage of disk space that could be freed with compaction      */
specifier|public
name|long
name|estimateCompactionGain
parameter_list|(
name|long
name|offset
parameter_list|)
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
operator|-
name|offset
operator|)
operator|/
name|totalSize
return|;
block|}
specifier|public
name|long
name|getTotalSize
parameter_list|()
block|{
return|return
name|totalSize
return|;
block|}
specifier|public
name|long
name|getReachableSize
parameter_list|()
block|{
return|return
name|reachableSize
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

