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
name|Objects
operator|.
name|equal
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
name|checkState
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
comment|/**      * The segment that contains this record, or initially some other segment      * in the same store. The reference is lazily updated when the      * {@link #getSegment()} method is first called to prevent the potentially      * costly pre-loading of segments that might actually not be needed.      */
specifier|private
name|Segment
name|segment
decl_stmt|;
comment|/**      * Identifier of the segment that contains this record. The value of      * this identifier never changes, but the exact instance reference may      * get updated by the {@link #getSegment()} method to indicate that      * lazy initialization has happened.      */
specifier|private
name|UUID
name|uuid
decl_stmt|;
comment|/**      * Segment offset of this record.      */
specifier|private
specifier|final
name|int
name|offset
decl_stmt|;
comment|/**      * Creates a new object for the identified record. The segment from which      * the record identifier was read is also given as it either directly      * contains the identified record (common case) or can be used to look      * up the segment that contains the record.      *      * @param segment from which the record identifier was read      * @param id record identified      */
specifier|protected
name|Record
parameter_list|(
annotation|@
name|Nonnull
name|Segment
name|segment
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|id
parameter_list|)
block|{
name|this
operator|.
name|segment
operator|=
name|checkNotNull
argument_list|(
name|segment
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|equal
argument_list|(
name|id
operator|.
name|getSegmentId
argument_list|()
argument_list|,
name|segment
operator|.
name|getSegmentId
argument_list|()
argument_list|)
condition|)
block|{
name|this
operator|.
name|uuid
operator|=
name|segment
operator|.
name|getSegmentId
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|uuid
operator|=
name|id
operator|.
name|getSegmentId
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|offset
operator|=
name|id
operator|.
name|getOffset
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|Record
parameter_list|(
annotation|@
name|Nonnull
name|Segment
name|segment
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|this
operator|.
name|segment
operator|=
name|checkNotNull
argument_list|(
name|segment
argument_list|)
expr_stmt|;
name|this
operator|.
name|uuid
operator|=
name|segment
operator|.
name|getSegmentId
argument_list|()
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
comment|/**      * Returns the store that contains this record.      *      * @return containing segment store      */
annotation|@
name|Nonnull
specifier|protected
name|SegmentStore
name|getStore
parameter_list|()
block|{
return|return
name|segment
operator|.
name|getStore
argument_list|()
return|;
block|}
comment|/**      * Returns the segment that contains this record.      *      * @return segment that contains this record      */
specifier|protected
specifier|synchronized
name|Segment
name|getSegment
parameter_list|()
block|{
if|if
condition|(
name|uuid
operator|!=
name|segment
operator|.
name|getSegmentId
argument_list|()
condition|)
block|{
name|segment
operator|=
name|segment
operator|.
name|getSegment
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|uuid
operator|.
name|equals
argument_list|(
name|segment
operator|.
name|getSegmentId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|uuid
operator|=
name|segment
operator|.
name|getSegmentId
argument_list|()
expr_stmt|;
block|}
return|return
name|segment
return|;
block|}
comment|/**      * Returns the identifier of this record.      *      * @return record identifier      */
specifier|public
specifier|synchronized
name|RecordId
name|getRecordId
parameter_list|()
block|{
return|return
operator|new
name|RecordId
argument_list|(
name|uuid
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

