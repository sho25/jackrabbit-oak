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
name|Charsets
operator|.
name|UTF_8
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
name|checkState
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
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|System
operator|.
name|arraycopy
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|System
operator|.
name|currentTimeMillis
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|System
operator|.
name|identityHashCode
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
name|Segment
operator|.
name|GC_FULL_GENERATION_OFFSET
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
name|Segment
operator|.
name|GC_GENERATION_OFFSET
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
name|Segment
operator|.
name|HEADER_SIZE
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
name|Segment
operator|.
name|MAX_SEGMENT_SIZE
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
name|Segment
operator|.
name|RECORD_ID_BYTES
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
name|Segment
operator|.
name|RECORD_SIZE
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
name|Segment
operator|.
name|SEGMENT_REFERENCE_SIZE
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
name|Segment
operator|.
name|align
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
name|SegmentId
operator|.
name|isDataSegmentId
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
name|SegmentVersion
operator|.
name|LATEST_VERSION
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
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
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|HexDump
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
name|RecordNumbers
operator|.
name|Entry
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
name|tar
operator|.
name|GCGeneration
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

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * This class encapsulates the state of a segment being written. It provides methods  * for writing primitive data types and for pre-allocating buffer space in the current  * segment. Should the current segment not have enough space left the current segment  * is flushed and a fresh one is allocated.  *<p>  * The common usage pattern is:  *<pre>  *    SegmentBufferWriter writer = ...  *    writer.prepare(...)  // allocate buffer  *    writer.writeXYZ(...)  *</pre>  * The behaviour of this class is undefined should the pre-allocated buffer be  * overrun be calling any of the write methods.  *<p>  * Instances of this class are<em>not thread safe</em>  */
end_comment

begin_class
specifier|public
class|class
name|SegmentBufferWriter
implements|implements
name|WriteOperationHandler
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SegmentBufferWriter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
class|class
name|Statistics
block|{
name|int
name|segmentIdCount
decl_stmt|;
name|int
name|recordIdCount
decl_stmt|;
name|int
name|recordCount
decl_stmt|;
name|int
name|size
decl_stmt|;
name|SegmentId
name|id
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"id="
operator|+
name|id
operator|+
literal|",size="
operator|+
name|size
operator|+
literal|",segmentIdCount="
operator|+
name|segmentIdCount
operator|+
literal|",recordIdCount="
operator|+
name|recordIdCount
operator|+
literal|",recordCount="
operator|+
name|recordCount
return|;
block|}
block|}
specifier|private
name|MutableRecordNumbers
name|recordNumbers
init|=
operator|new
name|MutableRecordNumbers
argument_list|()
decl_stmt|;
specifier|private
name|MutableSegmentReferences
name|segmentReferences
init|=
operator|new
name|MutableSegmentReferences
argument_list|()
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|SegmentIdProvider
name|idProvider
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|SegmentReader
name|reader
decl_stmt|;
comment|/**      * Id of this writer.      */
annotation|@
name|NotNull
specifier|private
specifier|final
name|String
name|wid
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|GCGeneration
name|gcGeneration
decl_stmt|;
comment|/**      * The segment write buffer, filled from the end to the beginning      * (see OAK-629).      */
specifier|private
name|byte
index|[]
name|buffer
decl_stmt|;
specifier|private
name|Segment
name|segment
decl_stmt|;
comment|/**      * The number of bytes already written (or allocated). Counted from      * the<em>end</em> of the buffer.      */
specifier|private
name|int
name|length
decl_stmt|;
comment|/**      * Current write position within the buffer. Grows up when raw data      * is written, but shifted downwards by the prepare methods.      */
specifier|private
name|int
name|position
decl_stmt|;
specifier|private
name|Statistics
name|statistics
decl_stmt|;
comment|/**      * Mark this buffer as dirty. A dirty buffer needs to be flushed to disk      * regularly to avoid data loss.      */
specifier|private
name|boolean
name|dirty
decl_stmt|;
specifier|public
name|SegmentBufferWriter
parameter_list|(
annotation|@
name|NotNull
name|SegmentIdProvider
name|idProvider
parameter_list|,
annotation|@
name|NotNull
name|SegmentReader
name|reader
parameter_list|,
annotation|@
name|Nullable
name|String
name|wid
parameter_list|,
annotation|@
name|NotNull
name|GCGeneration
name|gcGeneration
parameter_list|)
block|{
name|this
operator|.
name|idProvider
operator|=
name|checkNotNull
argument_list|(
name|idProvider
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|checkNotNull
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|this
operator|.
name|wid
operator|=
operator|(
name|wid
operator|==
literal|null
condition|?
literal|"w-"
operator|+
name|identityHashCode
argument_list|(
name|this
argument_list|)
else|:
name|wid
operator|)
expr_stmt|;
name|this
operator|.
name|gcGeneration
operator|=
name|checkNotNull
argument_list|(
name|gcGeneration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RecordId
name|execute
parameter_list|(
annotation|@
name|NotNull
name|GCGeneration
name|gcGeneration
parameter_list|,
annotation|@
name|NotNull
name|WriteOperation
name|writeOperation
parameter_list|)
throws|throws
name|IOException
block|{
name|checkState
argument_list|(
name|gcGeneration
operator|.
name|equals
argument_list|(
name|this
operator|.
name|gcGeneration
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|writeOperation
operator|.
name|execute
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|GCGeneration
name|getGCGeneration
parameter_list|()
block|{
return|return
name|gcGeneration
return|;
block|}
comment|/**      * Allocate a new segment and write the segment meta data.      * The segment meta data is a string of the format {@code "{wid=W,sno=S,t=T}"}      * where:      *<ul>      *<li>{@code W} is the writer id {@code wid},</li>      *<li>{@code S} is a unique, increasing sequence number corresponding to the allocation order      * of the segments in this store,</li>      *<li>{@code T} is a time stamp according to {@link System#currentTimeMillis()}.</li>      *</ul>      * The segment meta data is guaranteed to be the first string record in a segment.      */
specifier|private
name|void
name|newSegment
parameter_list|(
name|SegmentStore
name|store
parameter_list|)
throws|throws
name|IOException
block|{
name|buffer
operator|=
operator|new
name|byte
index|[
name|MAX_SEGMENT_SIZE
index|]
expr_stmt|;
name|buffer
index|[
literal|0
index|]
operator|=
literal|'0'
expr_stmt|;
name|buffer
index|[
literal|1
index|]
operator|=
literal|'a'
expr_stmt|;
name|buffer
index|[
literal|2
index|]
operator|=
literal|'K'
expr_stmt|;
name|buffer
index|[
literal|3
index|]
operator|=
name|SegmentVersion
operator|.
name|asByte
argument_list|(
name|LATEST_VERSION
argument_list|)
expr_stmt|;
name|buffer
index|[
literal|4
index|]
operator|=
literal|0
expr_stmt|;
comment|// reserved
name|buffer
index|[
literal|5
index|]
operator|=
literal|0
expr_stmt|;
comment|// reserved
name|int
name|generation
init|=
name|gcGeneration
operator|.
name|getGeneration
argument_list|()
decl_stmt|;
name|buffer
index|[
name|GC_GENERATION_OFFSET
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|generation
operator|>>
literal|24
argument_list|)
expr_stmt|;
name|buffer
index|[
name|GC_GENERATION_OFFSET
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|generation
operator|>>
literal|16
argument_list|)
expr_stmt|;
name|buffer
index|[
name|GC_GENERATION_OFFSET
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|generation
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|buffer
index|[
name|GC_GENERATION_OFFSET
operator|+
literal|3
index|]
operator|=
operator|(
name|byte
operator|)
name|generation
expr_stmt|;
name|int
name|fullGeneration
init|=
name|gcGeneration
operator|.
name|getFullGeneration
argument_list|()
decl_stmt|;
if|if
condition|(
name|gcGeneration
operator|.
name|isCompacted
argument_list|()
condition|)
block|{
comment|// Set highest order bit to mark segment created by compaction
name|fullGeneration
operator||=
literal|0x80000000
expr_stmt|;
block|}
name|buffer
index|[
name|GC_FULL_GENERATION_OFFSET
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|fullGeneration
operator|>>
literal|24
argument_list|)
expr_stmt|;
name|buffer
index|[
name|GC_FULL_GENERATION_OFFSET
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|fullGeneration
operator|>>
literal|16
argument_list|)
expr_stmt|;
name|buffer
index|[
name|GC_FULL_GENERATION_OFFSET
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|fullGeneration
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|buffer
index|[
name|GC_FULL_GENERATION_OFFSET
operator|+
literal|3
index|]
operator|=
operator|(
name|byte
operator|)
name|fullGeneration
expr_stmt|;
name|length
operator|=
literal|0
expr_stmt|;
name|position
operator|=
name|buffer
operator|.
name|length
expr_stmt|;
name|recordNumbers
operator|=
operator|new
name|MutableRecordNumbers
argument_list|()
expr_stmt|;
name|segmentReferences
operator|=
operator|new
name|MutableSegmentReferences
argument_list|()
expr_stmt|;
name|String
name|metaInfo
init|=
literal|"{\"wid\":\""
operator|+
name|wid
operator|+
literal|'"'
operator|+
literal|",\"sno\":"
operator|+
name|idProvider
operator|.
name|getSegmentIdCount
argument_list|()
operator|+
literal|",\"t\":"
operator|+
name|currentTimeMillis
argument_list|()
operator|+
literal|"}"
decl_stmt|;
name|segment
operator|=
operator|new
name|Segment
argument_list|(
name|idProvider
operator|.
name|newDataSegmentId
argument_list|()
argument_list|,
name|reader
argument_list|,
name|buffer
argument_list|,
name|recordNumbers
argument_list|,
name|segmentReferences
argument_list|,
name|metaInfo
argument_list|)
expr_stmt|;
name|statistics
operator|=
operator|new
name|Statistics
argument_list|()
expr_stmt|;
name|statistics
operator|.
name|id
operator|=
name|segment
operator|.
name|getSegmentId
argument_list|()
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|metaInfo
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
name|RecordWriters
operator|.
name|newValueWriter
argument_list|(
name|data
operator|.
name|length
argument_list|,
name|data
argument_list|)
operator|.
name|write
argument_list|(
name|this
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|dirty
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
name|position
operator|=
name|BinaryUtils
operator|.
name|writeByte
argument_list|(
name|buffer
argument_list|,
name|position
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|writeShort
parameter_list|(
name|short
name|value
parameter_list|)
block|{
name|position
operator|=
name|BinaryUtils
operator|.
name|writeShort
argument_list|(
name|buffer
argument_list|,
name|position
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|writeInt
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|position
operator|=
name|BinaryUtils
operator|.
name|writeInt
argument_list|(
name|buffer
argument_list|,
name|position
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|writeLong
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|position
operator|=
name|BinaryUtils
operator|.
name|writeLong
argument_list|(
name|buffer
argument_list|,
name|position
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
comment|/**      * Write a record ID.      *      * @param recordId  the record ID.      */
specifier|public
name|void
name|writeRecordId
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
name|checkState
argument_list|(
name|segmentReferences
operator|.
name|size
argument_list|()
operator|+
literal|1
operator|<
literal|0xffff
argument_list|,
literal|"Segment cannot have more than 0xffff references"
argument_list|)
expr_stmt|;
name|writeShort
argument_list|(
name|toShort
argument_list|(
name|writeSegmentIdReference
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
name|recordId
operator|.
name|getRecordNumber
argument_list|()
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|recordIdCount
operator|++
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
specifier|private
specifier|static
name|short
name|toShort
parameter_list|(
name|int
name|value
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
name|value
return|;
block|}
specifier|private
name|int
name|writeSegmentIdReference
parameter_list|(
name|SegmentId
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|segment
operator|.
name|getSegmentId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|segmentReferences
operator|.
name|addOrReference
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|info
parameter_list|(
name|Segment
name|segment
parameter_list|)
block|{
name|String
name|info
init|=
name|segment
operator|.
name|getSegmentId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|isDataSegmentId
argument_list|(
name|segment
operator|.
name|getSegmentId
argument_list|()
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
condition|)
block|{
name|info
operator|+=
operator|(
literal|" "
operator|+
name|segment
operator|.
name|getSegmentInfo
argument_list|()
operator|)
expr_stmt|;
block|}
return|return
name|info
return|;
block|}
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|arraycopy
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|buffer
argument_list|,
name|position
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|position
operator|+=
name|length
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
specifier|private
name|String
name|dumpSegmentBuffer
parameter_list|()
block|{
return|return
name|SegmentDump
operator|.
name|dumpSegment
argument_list|(
name|segment
operator|!=
literal|null
condition|?
name|segment
operator|.
name|getSegmentId
argument_list|()
else|:
literal|null
argument_list|,
name|length
argument_list|,
name|segment
operator|!=
literal|null
condition|?
name|segment
operator|.
name|getSegmentInfo
argument_list|()
else|:
literal|null
argument_list|,
name|gcGeneration
argument_list|,
name|segmentReferences
argument_list|,
name|recordNumbers
argument_list|,
name|stream
lambda|->
block|{
try|try
block|{
name|HexDump
operator|.
name|dump
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|stream
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|stream
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * Adds a segment header to the buffer and writes a segment to the segment      * store. This is done automatically (called from prepare) when there is not      * enough space for a record. It can also be called explicitly.      */
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|(
annotation|@
name|NotNull
name|SegmentStore
name|store
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dirty
condition|)
block|{
name|int
name|referencedSegmentIdCount
init|=
name|segmentReferences
operator|.
name|size
argument_list|()
decl_stmt|;
name|BinaryUtils
operator|.
name|writeInt
argument_list|(
name|buffer
argument_list|,
name|Segment
operator|.
name|REFERENCED_SEGMENT_ID_COUNT_OFFSET
argument_list|,
name|referencedSegmentIdCount
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|segmentIdCount
operator|=
name|referencedSegmentIdCount
expr_stmt|;
name|int
name|recordNumberCount
init|=
name|recordNumbers
operator|.
name|size
argument_list|()
decl_stmt|;
name|BinaryUtils
operator|.
name|writeInt
argument_list|(
name|buffer
argument_list|,
name|Segment
operator|.
name|RECORD_NUMBER_COUNT_OFFSET
argument_list|,
name|recordNumberCount
argument_list|)
expr_stmt|;
name|int
name|totalLength
init|=
name|align
argument_list|(
name|HEADER_SIZE
operator|+
name|referencedSegmentIdCount
operator|*
name|SEGMENT_REFERENCE_SIZE
operator|+
name|recordNumberCount
operator|*
name|RECORD_SIZE
operator|+
name|length
argument_list|,
literal|16
argument_list|)
decl_stmt|;
if|if
condition|(
name|totalLength
operator|>
name|buffer
operator|.
name|length
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Segment buffer corruption detected\n{}"
argument_list|,
name|dumpSegmentBuffer
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Too much data for a segment %s (referencedSegmentIdCount=%d, recordNumberCount=%d, length=%d, totalLength=%d)"
argument_list|,
name|segment
operator|.
name|getSegmentId
argument_list|()
argument_list|,
name|referencedSegmentIdCount
argument_list|,
name|recordNumberCount
argument_list|,
name|length
argument_list|,
name|totalLength
argument_list|)
argument_list|)
throw|;
block|}
name|statistics
operator|.
name|size
operator|=
name|length
operator|=
name|totalLength
expr_stmt|;
name|int
name|pos
init|=
name|HEADER_SIZE
decl_stmt|;
if|if
condition|(
name|pos
operator|+
name|length
operator|<=
name|buffer
operator|.
name|length
condition|)
block|{
comment|// the whole segment fits to the space *after* the referenced
comment|// segment identifiers we've already written, so we can safely
comment|// copy those bits ahead even if concurrent code is still
comment|// reading from that part of the buffer
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
argument_list|,
name|buffer
operator|.
name|length
operator|-
name|length
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|buffer
operator|.
name|length
operator|-
name|length
expr_stmt|;
block|}
else|else
block|{
comment|// this might leave some empty space between the header and
comment|// the record data, but this case only occurs when the
comment|// segment is>252kB in size and the maximum overhead is<<4kB,
comment|// which is acceptable
name|length
operator|=
name|buffer
operator|.
name|length
expr_stmt|;
block|}
for|for
control|(
name|SegmentId
name|segmentId
range|:
name|segmentReferences
control|)
block|{
name|pos
operator|=
name|BinaryUtils
operator|.
name|writeLong
argument_list|(
name|buffer
argument_list|,
name|pos
argument_list|,
name|segmentId
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|pos
operator|=
name|BinaryUtils
operator|.
name|writeLong
argument_list|(
name|buffer
argument_list|,
name|pos
argument_list|,
name|segmentId
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
name|entry
range|:
name|recordNumbers
control|)
block|{
name|pos
operator|=
name|BinaryUtils
operator|.
name|writeInt
argument_list|(
name|buffer
argument_list|,
name|pos
argument_list|,
name|entry
operator|.
name|getRecordNumber
argument_list|()
argument_list|)
expr_stmt|;
name|pos
operator|=
name|BinaryUtils
operator|.
name|writeByte
argument_list|(
name|buffer
argument_list|,
name|pos
argument_list|,
operator|(
name|byte
operator|)
name|entry
operator|.
name|getType
argument_list|()
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
name|pos
operator|=
name|BinaryUtils
operator|.
name|writeInt
argument_list|(
name|buffer
argument_list|,
name|pos
argument_list|,
name|entry
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|SegmentId
name|segmentId
init|=
name|segment
operator|.
name|getSegmentId
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Writing data segment: {} "
argument_list|,
name|statistics
argument_list|)
expr_stmt|;
name|store
operator|.
name|writeSegment
argument_list|(
name|segmentId
argument_list|,
name|buffer
argument_list|,
name|buffer
operator|.
name|length
operator|-
name|length
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|newSegment
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Before writing a record (which are written backwards, from the end of the      * file to the beginning), this method is called, to ensure there is enough      * space. A new segment is also created if there is not enough space in the      * segment lookup table or elsewhere.      *<p>      * This method does not actually write into the segment, just allocates the      * space (flushing the segment if needed and starting a new one), and sets      * the write position (records are written from the end to the beginning,      * but within a record from left to right).      *      * @param type  the record type (only used for root records)      * @param size  the size of the record, excluding the size used for the      *              record ids      * @param ids   the record ids      * @param store the {@code SegmentStore} instance to write full segments to      * @return a new record id      */
specifier|public
name|RecordId
name|prepare
parameter_list|(
name|RecordType
name|type
parameter_list|,
name|int
name|size
parameter_list|,
name|Collection
argument_list|<
name|RecordId
argument_list|>
name|ids
parameter_list|,
name|SegmentStore
name|store
parameter_list|)
throws|throws
name|IOException
block|{
name|checkArgument
argument_list|(
name|size
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|ids
argument_list|)
expr_stmt|;
if|if
condition|(
name|segment
operator|==
literal|null
condition|)
block|{
comment|// Create a segment first if this is the first time this segment buffer writer is used.
name|newSegment
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
name|int
name|idCount
init|=
name|ids
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|recordSize
init|=
name|align
argument_list|(
name|size
operator|+
name|idCount
operator|*
name|RECORD_ID_BYTES
argument_list|,
literal|1
operator|<<
name|Segment
operator|.
name|RECORD_ALIGN_BITS
argument_list|)
decl_stmt|;
comment|// First compute the header and segment sizes based on the assumption
comment|// that *all* identifiers stored in this record point to previously
comment|// unreferenced segments.
name|int
name|recordNumbersCount
init|=
name|recordNumbers
operator|.
name|size
argument_list|()
operator|+
literal|1
decl_stmt|;
name|int
name|referencedIdCount
init|=
name|segmentReferences
operator|.
name|size
argument_list|()
operator|+
name|ids
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|headerSize
init|=
name|HEADER_SIZE
operator|+
name|referencedIdCount
operator|*
name|SEGMENT_REFERENCE_SIZE
operator|+
name|recordNumbersCount
operator|*
name|RECORD_SIZE
decl_stmt|;
name|int
name|segmentSize
init|=
name|align
argument_list|(
name|headerSize
operator|+
name|recordSize
operator|+
name|length
argument_list|,
literal|16
argument_list|)
decl_stmt|;
comment|// If the size estimate looks too big, recompute it with a more
comment|// accurate refCount value. We skip doing this when possible to
comment|// avoid the somewhat expensive list and set traversals.
if|if
condition|(
name|segmentSize
operator|>
name|buffer
operator|.
name|length
condition|)
block|{
comment|// Collect the newly referenced segment ids
name|Set
argument_list|<
name|SegmentId
argument_list|>
name|segmentIds
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|RecordId
name|recordId
range|:
name|ids
control|)
block|{
name|SegmentId
name|segmentId
init|=
name|recordId
operator|.
name|getSegmentId
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|segmentReferences
operator|.
name|contains
argument_list|(
name|segmentId
argument_list|)
condition|)
block|{
name|segmentIds
operator|.
name|add
argument_list|(
name|segmentId
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Adjust the estimation of the new referenced segment ID count.
name|referencedIdCount
operator|=
name|segmentReferences
operator|.
name|size
argument_list|()
operator|+
name|segmentIds
operator|.
name|size
argument_list|()
expr_stmt|;
name|headerSize
operator|=
name|HEADER_SIZE
operator|+
name|referencedIdCount
operator|*
name|SEGMENT_REFERENCE_SIZE
operator|+
name|recordNumbersCount
operator|*
name|RECORD_SIZE
expr_stmt|;
name|segmentSize
operator|=
name|align
argument_list|(
name|headerSize
operator|+
name|recordSize
operator|+
name|length
argument_list|,
literal|16
argument_list|)
expr_stmt|;
block|}
comment|// If the resulting segment buffer would be too big we need to allocate
comment|// additional space. Allocating additional space is a recursive
comment|// operation guarded by the `dirty` flag. The recursion can iterate at
comment|// most two times. The base case happens when the `dirty` flag is
comment|// `false`: the current buffer is empty, the record is too big to fit in
comment|// an empty segment, and we fail with an `IllegalArgumentException`. The
comment|// recursive step happens when the `dirty` flag is `true`:
comment|// the current buffer is non-empty, we flush it, allocate a new buffer
comment|// for an empty segment, and invoke `prepare()` once more.
if|if
condition|(
name|segmentSize
operator|>
name|buffer
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|dirty
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Flushing full segment {} (headerSize={}, recordSize={}, length={}, segmentSize={})"
argument_list|,
name|segment
operator|.
name|getSegmentId
argument_list|()
argument_list|,
name|headerSize
argument_list|,
name|recordSize
argument_list|,
name|length
argument_list|,
name|segmentSize
argument_list|)
expr_stmt|;
name|flush
argument_list|(
name|store
argument_list|)
expr_stmt|;
return|return
name|prepare
argument_list|(
name|type
argument_list|,
name|size
argument_list|,
name|ids
argument_list|,
name|store
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Record too big: type=%s, size=%s, recordIds=%s, total=%s"
argument_list|,
name|type
argument_list|,
name|size
argument_list|,
name|ids
operator|.
name|size
argument_list|()
argument_list|,
name|recordSize
argument_list|)
argument_list|)
throw|;
block|}
name|statistics
operator|.
name|recordCount
operator|++
expr_stmt|;
name|length
operator|+=
name|recordSize
expr_stmt|;
name|position
operator|=
name|buffer
operator|.
name|length
operator|-
name|length
expr_stmt|;
name|int
name|recordNumber
init|=
name|recordNumbers
operator|.
name|addRecord
argument_list|(
name|type
argument_list|,
name|position
argument_list|)
decl_stmt|;
return|return
operator|new
name|RecordId
argument_list|(
name|segment
operator|.
name|getSegmentId
argument_list|()
argument_list|,
name|recordNumber
argument_list|)
return|;
block|}
block|}
end_class

end_unit

