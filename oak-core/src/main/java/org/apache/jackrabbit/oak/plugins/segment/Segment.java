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
name|checkPositionIndexes
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|concurrent
operator|.
name|Callable
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
name|memory
operator|.
name|PropertyStates
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
name|Charsets
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
name|cache
operator|.
name|Weigher
import|;
end_import

begin_class
specifier|public
class|class
name|Segment
block|{
comment|/**      * Number of bytes used for storing a record identifier. One byte      * is used for identifying the segment and two for the record offset      * within that segment.      */
specifier|static
specifier|final
name|int
name|RECORD_ID_BYTES
init|=
literal|1
operator|+
literal|2
decl_stmt|;
comment|/**      * The limit on segment references within one segment. Since record      * identifiers use one byte to indicate the referenced segment, a single      * segment can hold references to up to 256 segments.      */
specifier|static
specifier|final
name|int
name|SEGMENT_REFERENCE_LIMIT
init|=
literal|1
operator|<<
literal|8
decl_stmt|;
comment|// 256
comment|/**      * The number of bytes (or bits of address space) to use for the      * alignment boundary of segment records.      */
specifier|static
specifier|final
name|int
name|RECORD_ALIGN_BITS
init|=
literal|2
decl_stmt|;
specifier|static
specifier|final
name|int
name|RECORD_ALIGN_BYTES
init|=
literal|1
operator|<<
name|RECORD_ALIGN_BITS
decl_stmt|;
comment|// 4
comment|/**      * Maximum segment size. Record identifiers are stored as three-byte      * sequences with the first byte indicating the segment and the next      * two the offset within that segment. Since all records are aligned      * at four-byte boundaries, the two bytes can address up to 256kB of      * record data.      */
specifier|static
specifier|final
name|int
name|MAX_SEGMENT_SIZE
init|=
literal|1
operator|<<
operator|(
literal|16
operator|+
name|RECORD_ALIGN_BITS
operator|)
decl_stmt|;
comment|// 256kB
comment|/**      * The size limit for small values. The variable length of small values      * is encoded as a single byte with the high bit as zero, which gives us      * seven bits for encoding the length of the value.      */
specifier|static
specifier|final
name|int
name|SMALL_LIMIT
init|=
literal|1
operator|<<
literal|7
decl_stmt|;
comment|/**      * The size limit for medium values. The variable length of medium values      * is encoded as two bytes with the highest bits of the first byte set to      * one and zero, which gives us 14 bits for encoding the length of the      * value. And since small values are never stored as medium ones, we can      * extend the size range to cover that many longer values.      */
specifier|static
specifier|final
name|int
name|MEDIUM_LIMIT
init|=
operator|(
literal|1
operator|<<
operator|(
literal|16
operator|-
literal|2
operator|)
operator|)
operator|+
name|SMALL_LIMIT
decl_stmt|;
specifier|static
specifier|final
name|Weigher
argument_list|<
name|UUID
argument_list|,
name|Segment
argument_list|>
name|WEIGHER
init|=
operator|new
name|Weigher
argument_list|<
name|UUID
argument_list|,
name|Segment
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|weigh
parameter_list|(
name|UUID
name|key
parameter_list|,
name|Segment
name|value
parameter_list|)
block|{
return|return
name|value
operator|.
name|data
operator|.
name|remaining
argument_list|()
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|SegmentStore
name|store
decl_stmt|;
comment|// TODO: should be private
specifier|private
specifier|final
name|UUID
name|uuid
decl_stmt|;
specifier|private
specifier|final
name|ByteBuffer
name|data
decl_stmt|;
specifier|public
name|Segment
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|UUID
name|uuid
parameter_list|,
name|ByteBuffer
name|data
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|uuid
operator|=
name|checkNotNull
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|checkNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Segment
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|UUID
name|uuid
parameter_list|,
name|Collection
argument_list|<
name|UUID
argument_list|>
name|refids
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
name|this
operator|.
name|store
operator|=
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|uuid
operator|=
name|checkNotNull
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|refids
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
name|this
operator|.
name|data
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|refids
operator|.
name|size
argument_list|()
operator|*
literal|16
operator|+
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|UUID
name|refid
range|:
name|refids
control|)
block|{
name|data
operator|.
name|putLong
argument_list|(
name|refid
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|data
operator|.
name|putLong
argument_list|(
name|refid
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|data
operator|.
name|put
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|data
operator|.
name|rewind
argument_list|()
expr_stmt|;
block|}
comment|/**      * Maps the given record offset to the respective position within the      * internal {@link #data} array. The validity of a record with the given      * length at the given offset is also verified.      *      * @param offset record offset      * @param length record length      * @return position within the data array      */
specifier|private
name|int
name|pos
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|checkPositionIndexes
argument_list|(
name|offset
argument_list|,
name|offset
operator|+
name|length
argument_list|,
name|MAX_SEGMENT_SIZE
argument_list|)
expr_stmt|;
name|int
name|pos
init|=
name|data
operator|.
name|limit
argument_list|()
operator|-
name|MAX_SEGMENT_SIZE
operator|+
name|offset
decl_stmt|;
name|checkState
argument_list|(
name|pos
operator|>=
name|data
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|pos
return|;
block|}
specifier|public
name|UUID
name|getSegmentId
parameter_list|()
block|{
return|return
name|uuid
return|;
block|}
name|byte
name|readByte
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|data
operator|.
name|get
argument_list|(
name|pos
argument_list|(
name|offset
argument_list|,
literal|1
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns the identified segment.      *      * @param uuid segment identifier      * @return identified segment      */
name|Segment
name|getSegment
parameter_list|(
name|UUID
name|uuid
parameter_list|)
block|{
if|if
condition|(
name|equal
argument_list|(
name|uuid
argument_list|,
name|this
operator|.
name|uuid
argument_list|)
condition|)
block|{
return|return
name|this
return|;
comment|// optimization for the common case (OAK-1031)
block|}
else|else
block|{
return|return
name|store
operator|.
name|readSegment
argument_list|(
name|uuid
argument_list|)
return|;
block|}
block|}
comment|/**      * Returns the segment that contains the identified record.      *      * @param id record identifier      * @return segment that contains the identified record      */
name|Segment
name|getSegment
parameter_list|(
name|RecordId
name|id
parameter_list|)
block|{
return|return
name|getSegment
argument_list|(
name|checkNotNull
argument_list|(
name|id
argument_list|)
operator|.
name|getSegmentId
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Reads the given number of bytes starting from the given position      * in this segment.      *      * @param position position within segment      * @param buffer target buffer      * @param offset offset within target buffer      * @param length number of bytes to read      */
name|void
name|readBytes
parameter_list|(
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
name|ByteBuffer
name|d
init|=
name|data
operator|.
name|duplicate
argument_list|()
decl_stmt|;
name|d
operator|.
name|position
argument_list|(
name|pos
argument_list|(
name|position
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|get
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
name|RecordId
name|readRecordId
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|int
name|pos
init|=
name|pos
argument_list|(
name|offset
argument_list|,
name|RECORD_ID_BYTES
argument_list|)
decl_stmt|;
return|return
name|internalReadRecordId
argument_list|(
name|pos
argument_list|)
return|;
block|}
specifier|private
name|RecordId
name|internalReadRecordId
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|int
name|refpos
init|=
name|data
operator|.
name|position
argument_list|()
operator|+
operator|(
name|data
operator|.
name|get
argument_list|(
name|pos
argument_list|)
operator|&
literal|0xff
operator|)
operator|*
literal|16
decl_stmt|;
name|UUID
name|refid
init|=
operator|new
name|UUID
argument_list|(
name|data
operator|.
name|getLong
argument_list|(
name|refpos
argument_list|)
argument_list|,
name|data
operator|.
name|getLong
argument_list|(
name|refpos
operator|+
literal|8
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
operator|(
operator|(
operator|(
name|data
operator|.
name|get
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|data
operator|.
name|get
argument_list|(
name|pos
operator|+
literal|2
argument_list|)
operator|&
literal|0xff
operator|)
operator|)
operator|<<
name|RECORD_ALIGN_BITS
decl_stmt|;
return|return
operator|new
name|RecordId
argument_list|(
name|refid
argument_list|,
name|offset
argument_list|)
return|;
block|}
name|int
name|readInt
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|int
name|pos
init|=
name|pos
argument_list|(
name|offset
argument_list|,
literal|4
argument_list|)
decl_stmt|;
return|return
operator|(
name|data
operator|.
name|get
argument_list|(
name|pos
argument_list|)
operator|&
literal|0xff
operator|)
operator|<<
literal|24
operator||
operator|(
name|data
operator|.
name|get
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator||
operator|(
name|data
operator|.
name|get
argument_list|(
name|pos
operator|+
literal|2
argument_list|)
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator||
operator|(
name|data
operator|.
name|get
argument_list|(
name|pos
operator|+
literal|3
argument_list|)
operator|&
literal|0xff
operator|)
return|;
block|}
name|String
name|readString
parameter_list|(
specifier|final
name|RecordId
name|id
parameter_list|)
block|{
return|return
name|store
operator|.
name|getRecord
argument_list|(
name|id
argument_list|,
operator|new
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|call
parameter_list|()
block|{
return|return
name|getSegment
argument_list|(
name|id
argument_list|)
operator|.
name|readString
argument_list|(
name|id
operator|.
name|getOffset
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
name|String
name|readString
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|int
name|pos
init|=
name|pos
argument_list|(
name|offset
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|long
name|length
init|=
name|internalReadLength
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|<
name|SMALL_LIMIT
condition|)
block|{
name|byte
index|[]
name|bytes
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
name|ByteBuffer
name|buffer
init|=
name|data
operator|.
name|duplicate
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|position
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|get
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|length
operator|<
name|MEDIUM_LIMIT
condition|)
block|{
name|byte
index|[]
name|bytes
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
name|ByteBuffer
name|buffer
init|=
name|data
operator|.
name|duplicate
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|position
argument_list|(
name|pos
operator|+
literal|2
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|get
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|length
operator|<
name|Integer
operator|.
name|MAX_VALUE
condition|)
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
name|this
argument_list|,
name|internalReadRecordId
argument_list|(
name|pos
operator|+
literal|8
argument_list|)
argument_list|,
name|size
argument_list|)
decl_stmt|;
name|SegmentStream
name|stream
init|=
operator|new
name|SegmentStream
argument_list|(
name|store
argument_list|,
operator|new
name|RecordId
argument_list|(
name|uuid
argument_list|,
name|offset
argument_list|)
argument_list|,
name|list
argument_list|,
name|length
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|stream
operator|.
name|getString
argument_list|()
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"String is too long: "
operator|+
name|length
argument_list|)
throw|;
block|}
block|}
name|MapRecord
name|readMap
parameter_list|(
name|RecordId
name|id
parameter_list|)
block|{
return|return
operator|new
name|MapRecord
argument_list|(
name|this
argument_list|,
name|id
argument_list|)
return|;
block|}
name|Template
name|readTemplate
parameter_list|(
specifier|final
name|RecordId
name|id
parameter_list|)
block|{
return|return
name|store
operator|.
name|getRecord
argument_list|(
name|id
argument_list|,
operator|new
name|Callable
argument_list|<
name|Template
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Template
name|call
parameter_list|()
block|{
return|return
name|getSegment
argument_list|(
name|id
argument_list|)
operator|.
name|readTemplate
argument_list|(
name|id
operator|.
name|getOffset
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
name|Template
name|readTemplate
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|int
name|head
init|=
name|readInt
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|boolean
name|hasPrimaryType
init|=
operator|(
name|head
operator|&
operator|(
literal|1
operator|<<
literal|31
operator|)
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|hasMixinTypes
init|=
operator|(
name|head
operator|&
operator|(
literal|1
operator|<<
literal|30
operator|)
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|zeroChildNodes
init|=
operator|(
name|head
operator|&
operator|(
literal|1
operator|<<
literal|29
operator|)
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|manyChildNodes
init|=
operator|(
name|head
operator|&
operator|(
literal|1
operator|<<
literal|28
operator|)
operator|)
operator|!=
literal|0
decl_stmt|;
name|int
name|mixinCount
init|=
operator|(
name|head
operator|>>
literal|18
operator|)
operator|&
operator|(
operator|(
literal|1
operator|<<
literal|10
operator|)
operator|-
literal|1
operator|)
decl_stmt|;
name|int
name|propertyCount
init|=
name|head
operator|&
operator|(
operator|(
literal|1
operator|<<
literal|18
operator|)
operator|-
literal|1
operator|)
decl_stmt|;
name|offset
operator|+=
literal|4
expr_stmt|;
name|PropertyState
name|primaryType
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|hasPrimaryType
condition|)
block|{
name|RecordId
name|primaryId
init|=
name|readRecordId
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|primaryType
operator|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
name|readString
argument_list|(
name|primaryId
argument_list|)
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|Segment
operator|.
name|RECORD_ID_BYTES
expr_stmt|;
block|}
name|PropertyState
name|mixinTypes
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|hasMixinTypes
condition|)
block|{
name|String
index|[]
name|mixins
init|=
operator|new
name|String
index|[
name|mixinCount
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|mixins
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|RecordId
name|mixinId
init|=
name|readRecordId
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|mixins
index|[
name|i
index|]
operator|=
name|readString
argument_list|(
name|mixinId
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|Segment
operator|.
name|RECORD_ID_BYTES
expr_stmt|;
block|}
name|mixinTypes
operator|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"jcr:mixinTypes"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|mixins
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
block|}
name|String
name|childName
init|=
name|Template
operator|.
name|ZERO_CHILD_NODES
decl_stmt|;
if|if
condition|(
name|manyChildNodes
condition|)
block|{
name|childName
operator|=
name|Template
operator|.
name|MANY_CHILD_NODES
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|zeroChildNodes
condition|)
block|{
name|RecordId
name|childNameId
init|=
name|readRecordId
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|childName
operator|=
name|readString
argument_list|(
name|childNameId
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|Segment
operator|.
name|RECORD_ID_BYTES
expr_stmt|;
block|}
name|PropertyTemplate
index|[]
name|properties
init|=
operator|new
name|PropertyTemplate
index|[
name|propertyCount
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|properties
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|RecordId
name|propertyNameId
init|=
name|readRecordId
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|offset
operator|+=
name|Segment
operator|.
name|RECORD_ID_BYTES
expr_stmt|;
name|byte
name|type
init|=
name|readByte
argument_list|(
name|offset
operator|++
argument_list|)
decl_stmt|;
name|properties
index|[
name|i
index|]
operator|=
operator|new
name|PropertyTemplate
argument_list|(
name|i
argument_list|,
name|readString
argument_list|(
name|propertyNameId
argument_list|)
argument_list|,
name|Type
operator|.
name|fromTag
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|type
argument_list|)
argument_list|,
name|type
operator|<
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Template
argument_list|(
name|primaryType
argument_list|,
name|mixinTypes
argument_list|,
name|properties
argument_list|,
name|childName
argument_list|)
return|;
block|}
name|long
name|readLength
parameter_list|(
name|RecordId
name|id
parameter_list|)
block|{
return|return
name|getSegment
argument_list|(
name|id
argument_list|)
operator|.
name|readLength
argument_list|(
name|id
operator|.
name|getOffset
argument_list|()
argument_list|)
return|;
block|}
name|long
name|readLength
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|internalReadLength
argument_list|(
name|pos
argument_list|(
name|offset
argument_list|,
literal|1
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|long
name|internalReadLength
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|int
name|length
init|=
name|data
operator|.
name|get
argument_list|(
name|pos
operator|++
argument_list|)
operator|&
literal|0xff
decl_stmt|;
if|if
condition|(
operator|(
name|length
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
block|{
return|return
name|length
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|length
operator|&
literal|0x40
operator|)
operator|==
literal|0
condition|)
block|{
return|return
operator|(
operator|(
name|length
operator|&
literal|0x3f
operator|)
operator|<<
literal|8
operator||
name|data
operator|.
name|get
argument_list|(
name|pos
operator|++
argument_list|)
operator|&
literal|0xff
operator|)
operator|+
name|SMALL_LIMIT
return|;
block|}
else|else
block|{
return|return
operator|(
operator|(
operator|(
name|long
operator|)
name|length
operator|&
literal|0x3f
operator|)
operator|<<
literal|56
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|data
operator|.
name|get
argument_list|(
name|pos
operator|++
argument_list|)
operator|&
literal|0xff
argument_list|)
operator|)
operator|<<
literal|48
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|data
operator|.
name|get
argument_list|(
name|pos
operator|++
argument_list|)
operator|&
literal|0xff
argument_list|)
operator|)
operator|<<
literal|40
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|data
operator|.
name|get
argument_list|(
name|pos
operator|++
argument_list|)
operator|&
literal|0xff
argument_list|)
operator|)
operator|<<
literal|32
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|data
operator|.
name|get
argument_list|(
name|pos
operator|++
argument_list|)
operator|&
literal|0xff
argument_list|)
operator|)
operator|<<
literal|24
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|data
operator|.
name|get
argument_list|(
name|pos
operator|++
argument_list|)
operator|&
literal|0xff
argument_list|)
operator|)
operator|<<
literal|16
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|data
operator|.
name|get
argument_list|(
name|pos
operator|++
argument_list|)
operator|&
literal|0xff
argument_list|)
operator|)
operator|<<
literal|8
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|data
operator|.
name|get
argument_list|(
name|pos
operator|++
argument_list|)
operator|&
literal|0xff
argument_list|)
operator|)
operator|)
operator|+
name|MEDIUM_LIMIT
return|;
block|}
block|}
name|SegmentStream
name|readStream
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|RecordId
name|id
init|=
operator|new
name|RecordId
argument_list|(
name|uuid
argument_list|,
name|offset
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
name|pos
argument_list|(
name|offset
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|long
name|length
init|=
name|internalReadLength
argument_list|(
name|pos
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
name|byte
index|[]
name|inline
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
name|ByteBuffer
name|buffer
init|=
name|data
operator|.
name|duplicate
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|<
name|Segment
operator|.
name|SMALL_LIMIT
condition|)
block|{
name|buffer
operator|.
name|position
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|position
argument_list|(
name|pos
operator|+
literal|2
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|get
argument_list|(
name|inline
argument_list|)
expr_stmt|;
return|return
operator|new
name|SegmentStream
argument_list|(
name|id
argument_list|,
name|inline
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
name|this
argument_list|,
name|internalReadRecordId
argument_list|(
name|pos
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
name|store
argument_list|,
name|id
argument_list|,
name|list
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

