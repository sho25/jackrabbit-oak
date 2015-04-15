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
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * Record within a segment.  */
end_comment

begin_class
class|class
name|Record
block|{
specifier|static
name|boolean
name|fastEquals
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
return|return
name|a
operator|instanceof
name|Record
operator|&&
name|fastEquals
argument_list|(
operator|(
name|Record
operator|)
name|a
argument_list|,
name|b
argument_list|)
return|;
block|}
specifier|static
name|boolean
name|fastEquals
parameter_list|(
name|Record
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
return|return
name|b
operator|instanceof
name|Record
operator|&&
name|fastEquals
argument_list|(
name|a
argument_list|,
operator|(
name|Record
operator|)
name|b
argument_list|)
return|;
block|}
specifier|static
name|boolean
name|fastEquals
parameter_list|(
name|Record
name|a
parameter_list|,
name|Record
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|offset
operator|==
name|b
operator|.
name|offset
operator|&&
name|a
operator|.
name|segmentId
operator|.
name|equals
argument_list|(
name|b
operator|.
name|segmentId
argument_list|)
return|;
block|}
comment|/**      * Identifier of the segment that contains this record.      */
specifier|private
specifier|final
name|SegmentId
name|segmentId
decl_stmt|;
comment|/**      * Segment offset of this record.      */
specifier|private
specifier|final
name|int
name|offset
decl_stmt|;
comment|/**      * Creates a new object for the identified record.      *      * @param id record identified      */
specifier|protected
name|Record
parameter_list|(
annotation|@
name|Nonnull
name|RecordId
name|id
parameter_list|)
block|{
name|this
argument_list|(
name|id
operator|.
name|getSegmentId
argument_list|()
argument_list|,
name|id
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Record
parameter_list|(
annotation|@
name|Nonnull
name|SegmentId
name|segmentId
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|this
operator|.
name|segmentId
operator|=
name|segmentId
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
specifier|protected
name|boolean
name|wasCompactedTo
parameter_list|(
name|Record
name|after
parameter_list|)
block|{
name|CompactionMap
name|map
init|=
name|segmentId
operator|.
name|getTracker
argument_list|()
operator|.
name|getCompactionMap
argument_list|()
decl_stmt|;
return|return
name|map
operator|.
name|wasCompactedTo
argument_list|(
name|getRecordId
argument_list|()
argument_list|,
name|after
operator|.
name|getRecordId
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns the tracker of the segment that contains this record.      *      * @return segment tracker      */
specifier|protected
name|SegmentTracker
name|getTracker
parameter_list|()
block|{
return|return
name|segmentId
operator|.
name|getTracker
argument_list|()
return|;
block|}
comment|/**      * Returns the segment that contains this record.      *      * @return segment that contains this record      */
specifier|protected
name|Segment
name|getSegment
parameter_list|()
block|{
return|return
name|segmentId
operator|.
name|getSegment
argument_list|()
return|;
block|}
comment|/**      * Returns the identifier of this record.      *      * @return record identifier      */
specifier|public
name|RecordId
name|getRecordId
parameter_list|()
block|{
return|return
operator|new
name|RecordId
argument_list|(
name|segmentId
argument_list|,
name|offset
argument_list|)
return|;
block|}
comment|/**      * Returns the segment offset of this record.      *      * @return segment offset of this record      */
specifier|protected
specifier|final
name|int
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
comment|/**      * Returns the segment offset of the given byte position in this record.      *      * @param position byte position within this record      * @return segment offset of the given byte position      */
specifier|protected
specifier|final
name|int
name|getOffset
parameter_list|(
name|int
name|position
parameter_list|)
block|{
return|return
name|getOffset
argument_list|()
operator|+
name|position
return|;
block|}
comment|/**      * Returns the segment offset of a byte position in this record.      * The position is calculated from the given number of raw bytes and      * record identifiers.      *      * @param bytes number of raw bytes before the position      * @param ids number of record identifiers before the position      * @return segment offset of the specified byte position      */
specifier|protected
specifier|final
name|int
name|getOffset
parameter_list|(
name|int
name|bytes
parameter_list|,
name|int
name|ids
parameter_list|)
block|{
return|return
name|getOffset
argument_list|(
name|bytes
operator|+
name|ids
operator|*
name|Segment
operator|.
name|RECORD_ID_BYTES
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|that
parameter_list|)
block|{
return|return
name|fastEquals
argument_list|(
name|this
argument_list|,
name|that
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|segmentId
operator|.
name|hashCode
argument_list|()
operator|^
name|offset
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
name|getRecordId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

