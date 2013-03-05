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
name|checkPositionIndexes
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
name|SegmentWriter
operator|.
name|BLOCK_SIZE
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentReader
block|{
specifier|private
specifier|final
name|SegmentStore
name|store
decl_stmt|;
specifier|public
name|SegmentReader
parameter_list|(
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
name|long
name|readLength
parameter_list|(
name|RecordId
name|recordId
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|readLength
argument_list|(
name|segment
argument_list|,
name|recordId
operator|.
name|getOffset
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|long
name|readLength
parameter_list|(
name|Segment
name|segment
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|segment
operator|.
name|readLength
argument_list|(
name|offset
argument_list|)
return|;
block|}
specifier|public
name|SegmentStream
name|readStream
parameter_list|(
name|RecordId
name|recordId
parameter_list|)
block|{
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
name|recordId
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|long
name|length
init|=
name|readLength
argument_list|(
name|segment
argument_list|,
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|<
name|Segment
operator|.
name|MEDIUM_LIMIT
condition|)
block|{
if|if
condition|(
name|length
operator|<
name|Segment
operator|.
name|SMALL_LIMIT
condition|)
block|{
name|offset
operator|+=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|offset
operator|+=
literal|2
expr_stmt|;
block|}
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|length
index|]
decl_stmt|;
name|segment
operator|.
name|readBytes
argument_list|(
name|offset
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|SegmentStream
argument_list|(
name|recordId
argument_list|,
name|data
argument_list|)
return|;
block|}
else|else
block|{
name|int
name|size
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|length
operator|+
name|BLOCK_SIZE
operator|-
literal|1
operator|)
operator|/
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|ListRecord
name|list
init|=
operator|new
name|ListRecord
argument_list|(
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
operator|+
literal|8
argument_list|)
argument_list|,
name|size
argument_list|)
decl_stmt|;
return|return
operator|new
name|SegmentStream
argument_list|(
name|this
argument_list|,
name|recordId
argument_list|,
name|list
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
specifier|public
name|byte
name|readByte
parameter_list|(
name|RecordId
name|recordId
parameter_list|,
name|int
name|position
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|position
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|segment
operator|.
name|readByte
argument_list|(
name|recordId
operator|.
name|getOffset
argument_list|()
operator|+
name|position
argument_list|)
return|;
block|}
specifier|public
name|int
name|readInt
parameter_list|(
name|RecordId
name|recordId
parameter_list|,
name|int
name|position
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|position
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|segment
operator|.
name|readInt
argument_list|(
name|recordId
operator|.
name|getOffset
argument_list|()
operator|+
name|position
argument_list|)
return|;
block|}
specifier|public
name|void
name|readBytes
parameter_list|(
name|RecordId
name|recordId
parameter_list|,
name|int
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|position
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|checkPositionIndexes
argument_list|(
name|offset
argument_list|,
name|offset
operator|+
name|length
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
name|segment
operator|.
name|readBytes
argument_list|(
name|recordId
operator|.
name|getOffset
argument_list|()
operator|+
name|position
argument_list|,
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RecordId
name|readRecordId
parameter_list|(
name|RecordId
name|recordId
parameter_list|,
name|int
name|position
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|position
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|segment
operator|.
name|readRecordId
argument_list|(
name|recordId
operator|.
name|getOffset
argument_list|()
operator|+
name|position
argument_list|)
return|;
block|}
specifier|public
name|ListRecord
name|readList
parameter_list|(
name|RecordId
name|recordId
parameter_list|,
name|int
name|numberOfEntries
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|numberOfEntries
operator|>=
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|numberOfEntries
operator|>
literal|0
condition|)
block|{
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
name|RecordId
name|id
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|recordId
operator|.
name|getOffset
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|ListRecord
argument_list|(
name|id
argument_list|,
name|numberOfEntries
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ListRecord
argument_list|(
name|recordId
argument_list|,
name|numberOfEntries
argument_list|)
return|;
block|}
block|}
specifier|public
name|BlockRecord
name|readBlock
parameter_list|(
name|RecordId
name|recordId
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|size
operator|>
literal|0
argument_list|)
expr_stmt|;
return|return
operator|new
name|BlockRecord
argument_list|(
name|recordId
argument_list|,
name|size
argument_list|)
return|;
block|}
name|SegmentStore
name|getStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
block|}
end_class

end_unit

