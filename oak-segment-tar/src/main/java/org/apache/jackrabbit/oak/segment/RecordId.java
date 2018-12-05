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
name|Integer
operator|.
name|parseInt
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
name|segment
operator|.
name|CacheWeights
operator|.
name|OBJECT_HEADER_SIZE
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|spi
operator|.
name|persistence
operator|.
name|Buffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * The record id. This includes the segment id and the offset within the  * segment.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|RecordId
implements|implements
name|Comparable
argument_list|<
name|RecordId
argument_list|>
block|{
comment|/**      * A {@code null} record id not identifying any record.      */
specifier|public
specifier|static
specifier|final
name|RecordId
name|NULL
init|=
operator|new
name|RecordId
argument_list|(
name|SegmentId
operator|.
name|NULL
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})"
operator|+
literal|"(:(0|[1-9][0-9]*)|\\.([0-9a-f]{8}))"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|int
name|SERIALIZED_RECORD_ID_BYTES
init|=
literal|20
decl_stmt|;
specifier|public
specifier|static
name|RecordId
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|RecordId
index|[
literal|0
index|]
decl_stmt|;
specifier|public
specifier|static
name|RecordId
name|fromString
parameter_list|(
name|SegmentIdProvider
name|idProvider
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
name|PATTERN
operator|.
name|matcher
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|UUID
name|uuid
init|=
name|UUID
operator|.
name|fromString
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|SegmentId
name|segmentId
init|=
name|idProvider
operator|.
name|newSegmentId
argument_list|(
name|uuid
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|uuid
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|offset
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|offset
operator|=
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|offset
operator|=
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|4
argument_list|)
argument_list|,
literal|16
argument_list|)
expr_stmt|;
block|}
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
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bad record identifier: "
operator|+
name|id
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|final
name|SegmentId
name|segmentId
decl_stmt|;
specifier|private
specifier|final
name|int
name|offset
decl_stmt|;
specifier|public
name|RecordId
parameter_list|(
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
name|checkNotNull
argument_list|(
name|segmentId
argument_list|)
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
specifier|public
name|SegmentId
name|getSegmentId
parameter_list|()
block|{
return|return
name|segmentId
return|;
block|}
specifier|public
name|int
name|getRecordNumber
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
comment|/**      * @return  the segment id part of this record id as UUID      */
specifier|public
name|UUID
name|asUUID
parameter_list|()
block|{
return|return
name|segmentId
operator|.
name|asUUID
argument_list|()
return|;
block|}
annotation|@
name|NotNull
specifier|public
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
comment|/**      * Serialise this record id into an array of bytes: {@code (msb, lsb, offset>> 2)}      * @return  this record id as byte array      */
annotation|@
name|NotNull
name|Buffer
name|getBytes
parameter_list|()
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|SERIALIZED_RECORD_ID_BYTES
index|]
decl_stmt|;
name|BinaryUtils
operator|.
name|writeLong
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|segmentId
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|BinaryUtils
operator|.
name|writeLong
argument_list|(
name|buffer
argument_list|,
literal|8
argument_list|,
name|segmentId
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|BinaryUtils
operator|.
name|writeInt
argument_list|(
name|buffer
argument_list|,
literal|16
argument_list|,
name|offset
argument_list|)
expr_stmt|;
return|return
name|Buffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|)
return|;
block|}
comment|//--------------------------------------------------------< Comparable>--
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
annotation|@
name|NotNull
name|RecordId
name|that
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|that
argument_list|)
expr_stmt|;
name|int
name|diff
init|=
name|segmentId
operator|.
name|compareTo
argument_list|(
name|that
operator|.
name|segmentId
argument_list|)
decl_stmt|;
if|if
condition|(
name|diff
operator|==
literal|0
condition|)
block|{
name|diff
operator|=
name|offset
operator|-
name|that
operator|.
name|offset
expr_stmt|;
block|}
return|return
name|diff
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
name|String
operator|.
name|format
argument_list|(
literal|"%s.%08x"
argument_list|,
name|segmentId
argument_list|,
name|offset
argument_list|)
return|;
block|}
comment|/**      * Returns the record id string representation used in Oak 1.0.      */
specifier|public
name|String
name|toString10
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s:%d"
argument_list|,
name|segmentId
argument_list|,
name|offset
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
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|object
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|object
operator|instanceof
name|RecordId
condition|)
block|{
name|RecordId
name|that
init|=
operator|(
name|RecordId
operator|)
name|object
decl_stmt|;
return|return
name|offset
operator|==
name|that
operator|.
name|offset
operator|&&
name|segmentId
operator|.
name|equals
argument_list|(
name|that
operator|.
name|segmentId
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|int
name|estimateMemoryUsage
parameter_list|()
block|{
return|return
name|OBJECT_HEADER_SIZE
operator|+
literal|16
operator|+
name|segmentId
operator|.
name|estimateMemoryUsage
argument_list|()
return|;
block|}
block|}
end_class

end_unit

