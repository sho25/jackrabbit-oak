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
name|Lists
operator|.
name|newArrayList
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
name|Maps
operator|.
name|newLinkedHashMap
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
name|GC_GEN_OFFSET
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
name|SEGMENT_REFERENCE_LIMIT
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
comment|/**  * This class encapsulates the state of a segment being written. It provides methods  * for writing primitive data types and for pre-allocating buffer space in the current  * segment. Should the current segment not have enough space left the current segment  * is flushed and a fresh one is allocated.  *<p>  * The common usage pattern is:  *<pre>  *    SegmentBufferWriter writer = ...  *    writer.prepare(...)  // allocate buffer  *    writer.writeXYZ(...)  *</pre>  * The behaviour of this class is undefined should the pre-allocated buffer be  * overrun be calling any of the write methods.  */
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
comment|/**      * The set of root records (i.e. ones not referenced by other records)      * in this segment.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|RecordId
argument_list|,
name|RecordType
argument_list|>
name|roots
init|=
name|newLinkedHashMap
argument_list|()
decl_stmt|;
comment|/**      * Identifiers of the external blob references stored in this segment.      */
specifier|private
specifier|final
name|List
argument_list|<
name|RecordId
argument_list|>
name|blobrefs
init|=
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SegmentStore
name|store
decl_stmt|;
comment|/**      * Version of the segment storage format.      */
specifier|private
specifier|final
name|SegmentVersion
name|version
decl_stmt|;
comment|/**      * Id of this writer.      */
specifier|private
specifier|final
name|String
name|wid
decl_stmt|;
specifier|private
specifier|final
name|SegmentTracker
name|tracker
decl_stmt|;
specifier|private
specifier|final
name|int
name|generation
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
specifier|public
name|SegmentBufferWriter
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|SegmentVersion
name|version
parameter_list|,
name|String
name|wid
parameter_list|,
name|int
name|generation
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
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
name|tracker
operator|=
name|store
operator|.
name|getTracker
argument_list|()
expr_stmt|;
name|this
operator|.
name|generation
operator|=
name|generation
expr_stmt|;
name|newSegment
argument_list|()
expr_stmt|;
block|}
specifier|public
name|SegmentBufferWriter
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|SegmentVersion
name|version
parameter_list|,
name|String
name|wid
parameter_list|)
block|{
name|this
argument_list|(
name|store
argument_list|,
name|version
argument_list|,
name|wid
argument_list|,
name|store
operator|.
name|getTracker
argument_list|()
operator|.
name|getGcGen
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RecordId
name|execute
parameter_list|(
name|WriteOperation
name|writeOperation
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|writeOperation
operator|.
name|execute
argument_list|(
name|this
argument_list|)
return|;
block|}
name|int
name|getGeneration
parameter_list|()
block|{
return|return
name|generation
return|;
block|}
comment|/**      * Allocate a new segment and write the segment meta data.      * The segment meta data is a string of the format {@code "{wid=W,sno=S,gc=G,t=T}"}      * where:      *<ul>      *<li>{@code W} is the writer id {@code wid},</li>      *<li>{@code S} is a unique, increasing sequence number corresponding to the allocation order      * of the segments in this store,</li>      *<li>{@code G} is the garbage collection generation (i.e. the number of compaction cycles      * that have been run),</li>      *<li>{@code T} is a time stamp according to {@link System#currentTimeMillis()}.</li>      *</ul>      * The segment meta data is guaranteed to be the first string record in a segment.      */
specifier|private
name|void
name|newSegment
parameter_list|()
block|{
name|buffer
operator|=
operator|new
name|byte
index|[
name|Segment
operator|.
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
name|version
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
comment|// refcount
comment|// FIXME OAK-3348 document change in format
name|buffer
index|[
name|GC_GEN_OFFSET
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
name|GC_GEN_OFFSET
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
name|GC_GEN_OFFSET
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
name|GC_GEN_OFFSET
operator|+
literal|3
index|]
operator|=
operator|(
name|byte
operator|)
name|generation
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
name|roots
operator|.
name|clear
argument_list|()
expr_stmt|;
name|blobrefs
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// FIXME OAK-4289: Remove the gc generation from the segment meta data
comment|// Don't write the GC generation into the segment info
comment|// as it is now available from the segment header. Update the tooling
comment|// accordingly (SegmentGraph).
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
name|tracker
operator|.
name|getNextSegmentNo
argument_list|()
operator|+
literal|",\"gc\":"
operator|+
name|generation
operator|+
literal|",\"t\":"
operator|+
name|currentTimeMillis
argument_list|()
operator|+
literal|"}"
decl_stmt|;
try|try
block|{
name|segment
operator|=
operator|new
name|Segment
argument_list|(
name|tracker
argument_list|,
name|buffer
argument_list|,
name|metaInfo
argument_list|)
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
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to write meta info to segment {} {}"
argument_list|,
name|segment
operator|.
name|getSegmentId
argument_list|()
argument_list|,
name|metaInfo
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
name|buffer
index|[
name|position
operator|++
index|]
operator|=
name|value
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
name|buffer
index|[
name|position
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|buffer
index|[
name|position
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|value
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
name|buffer
index|[
name|position
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|>>
literal|24
argument_list|)
expr_stmt|;
name|buffer
index|[
name|position
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|>>
literal|16
argument_list|)
expr_stmt|;
name|buffer
index|[
name|position
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|buffer
index|[
name|position
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|value
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
name|writeInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|value
operator|>>
literal|32
argument_list|)
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
operator|(
name|int
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Write a record id, and marks the record id as referenced (removes it from      * the unreferenced set).      *      * @param recordId the record id      */
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
name|roots
operator|.
name|remove
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|int
name|offset
init|=
name|recordId
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|checkState
argument_list|(
literal|0
operator|<=
name|offset
operator|&&
name|offset
operator|<
name|MAX_SEGMENT_SIZE
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|offset
operator|==
name|align
argument_list|(
name|offset
argument_list|,
literal|1
operator|<<
name|Segment
operator|.
name|RECORD_ALIGN_BITS
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
index|[
name|position
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|getSegmentRef
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
index|[
name|position
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|offset
operator|>>
operator|(
literal|8
operator|+
name|Segment
operator|.
name|RECORD_ALIGN_BITS
operator|)
argument_list|)
expr_stmt|;
name|buffer
index|[
name|position
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|offset
operator|>>
name|Segment
operator|.
name|RECORD_ALIGN_BITS
argument_list|)
expr_stmt|;
block|}
comment|// FIXME OAK-4287: Disable / remove SegmentBufferWriter#checkGCGen
comment|// Disable/remove this in production
specifier|private
name|void
name|checkGCGen
parameter_list|(
name|SegmentId
name|id
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|isDataSegmentId
argument_list|(
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|id
operator|.
name|getSegment
argument_list|()
operator|.
name|getGcGen
argument_list|()
operator|<
name|generation
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Detected reference from {} to segment {} from a previous gc generation."
argument_list|,
name|info
argument_list|(
name|this
operator|.
name|segment
argument_list|)
argument_list|,
name|info
argument_list|(
name|id
operator|.
name|getSegment
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Exception
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|SegmentNotFoundException
name|snfe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Detected reference from {} to non existing segment {}"
argument_list|,
name|info
argument_list|(
name|this
operator|.
name|segment
argument_list|)
argument_list|,
name|id
argument_list|,
name|snfe
argument_list|)
expr_stmt|;
block|}
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
specifier|private
name|int
name|getSegmentRef
parameter_list|(
name|SegmentId
name|segmentId
parameter_list|)
block|{
name|checkGCGen
argument_list|(
name|segmentId
argument_list|)
expr_stmt|;
name|int
name|refCount
init|=
name|segment
operator|.
name|getRefCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|refCount
operator|>
name|SEGMENT_REFERENCE_LIMIT
condition|)
block|{
throw|throw
operator|new
name|SegmentOverflowException
argument_list|(
literal|"Segment cannot have more than 255 references "
operator|+
name|segment
operator|.
name|getSegmentId
argument_list|()
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|refCount
condition|;
name|index
operator|++
control|)
block|{
if|if
condition|(
name|segmentId
operator|.
name|equals
argument_list|(
name|segment
operator|.
name|getRefId
argument_list|(
name|index
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|index
return|;
block|}
block|}
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|,
name|refCount
operator|*
literal|16
argument_list|,
literal|16
argument_list|)
operator|.
name|putLong
argument_list|(
name|segmentId
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|)
operator|.
name|putLong
argument_list|(
name|segmentId
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
index|[
name|Segment
operator|.
name|REF_COUNT_OFFSET
index|]
operator|=
operator|(
name|byte
operator|)
name|refCount
expr_stmt|;
return|return
name|refCount
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
block|}
specifier|public
name|void
name|addBlobRef
parameter_list|(
name|RecordId
name|blobId
parameter_list|)
block|{
name|blobrefs
operator|.
name|add
argument_list|(
name|blobId
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds a segment header to the buffer and writes a segment to the segment      * store. This is done automatically (called from prepare) when there is not      * enough space for a record. It can also be called explicitly.      */
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|int
name|refcount
init|=
name|segment
operator|.
name|getRefCount
argument_list|()
decl_stmt|;
name|int
name|rootcount
init|=
name|roots
operator|.
name|size
argument_list|()
decl_stmt|;
name|buffer
index|[
name|Segment
operator|.
name|ROOT_COUNT_OFFSET
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|rootcount
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|buffer
index|[
name|Segment
operator|.
name|ROOT_COUNT_OFFSET
operator|+
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
name|rootcount
expr_stmt|;
name|int
name|blobrefcount
init|=
name|blobrefs
operator|.
name|size
argument_list|()
decl_stmt|;
name|buffer
index|[
name|Segment
operator|.
name|BLOBREF_COUNT_OFFSET
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|blobrefcount
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|buffer
index|[
name|Segment
operator|.
name|BLOBREF_COUNT_OFFSET
operator|+
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
name|blobrefcount
expr_stmt|;
name|length
operator|=
name|align
argument_list|(
name|refcount
operator|*
literal|16
operator|+
name|rootcount
operator|*
literal|3
operator|+
name|blobrefcount
operator|*
literal|2
operator|+
name|length
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|length
operator|<=
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|pos
init|=
name|refcount
operator|*
literal|16
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
name|Map
operator|.
name|Entry
argument_list|<
name|RecordId
argument_list|,
name|RecordType
argument_list|>
name|entry
range|:
name|roots
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|int
name|offset
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|buffer
index|[
name|pos
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|ordinal
argument_list|()
expr_stmt|;
name|buffer
index|[
name|pos
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|offset
operator|>>
operator|(
literal|8
operator|+
name|Segment
operator|.
name|RECORD_ALIGN_BITS
operator|)
argument_list|)
expr_stmt|;
name|buffer
index|[
name|pos
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|offset
operator|>>
name|Segment
operator|.
name|RECORD_ALIGN_BITS
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|RecordId
name|blobref
range|:
name|blobrefs
control|)
block|{
name|int
name|offset
init|=
name|blobref
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|buffer
index|[
name|pos
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|offset
operator|>>
operator|(
literal|8
operator|+
name|Segment
operator|.
name|RECORD_ALIGN_BITS
operator|)
argument_list|)
expr_stmt|;
name|buffer
index|[
name|pos
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|offset
operator|>>
name|Segment
operator|.
name|RECORD_ALIGN_BITS
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
name|int
name|segmentOffset
init|=
name|buffer
operator|.
name|length
operator|-
name|length
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Writing data segment {} ({} bytes)"
argument_list|,
name|segmentId
argument_list|,
name|length
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
name|segmentOffset
argument_list|,
name|length
argument_list|)
expr_stmt|;
comment|// Keep this segment in memory as it's likely to be accessed soon
name|ByteBuffer
name|data
decl_stmt|;
if|if
condition|(
name|segmentOffset
operator|>
literal|4096
condition|)
block|{
name|data
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|data
operator|.
name|put
argument_list|(
name|buffer
argument_list|,
name|segmentOffset
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
else|else
block|{
name|data
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|,
name|segmentOffset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|// It is important to put the segment into the cache only *after* it has been
comment|// written to the store since as soon as it is in the cache it becomes eligible
comment|// for eviction, which might lead to SNFEs when it is not yet in the store at that point.
name|tracker
operator|.
name|setSegment
argument_list|(
name|segmentId
argument_list|,
operator|new
name|Segment
argument_list|(
name|tracker
argument_list|,
name|segmentId
argument_list|,
name|data
argument_list|)
argument_list|)
expr_stmt|;
name|newSegment
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Before writing a record (which are written backwards, from the end of the      * file to the beginning), this method is called, to ensure there is enough      * space. A new segment is also created if there is not enough space in the      * segment lookup table or elsewhere.      *<p>      * This method does not actually write into the segment, just allocates the      * space (flushing the segment if needed and starting a new one), and sets      * the write position (records are written from the end to the beginning,      * but within a record from left to right).      *      * @param type the record type (only used for root records)      * @param size the size of the record, excluding the size used for the      *            record ids      * @param ids the record ids      * @return a new record id      */
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
name|refCount
init|=
name|segment
operator|.
name|getRefCount
argument_list|()
operator|+
name|idCount
decl_stmt|;
name|int
name|blobRefCount
init|=
name|blobrefs
operator|.
name|size
argument_list|()
operator|+
literal|1
decl_stmt|;
name|int
name|rootCount
init|=
name|roots
operator|.
name|size
argument_list|()
operator|+
literal|1
decl_stmt|;
name|int
name|headerSize
init|=
name|refCount
operator|*
literal|16
operator|+
name|rootCount
operator|*
literal|3
operator|+
name|blobRefCount
operator|*
literal|2
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
operator|-
literal|1
operator|||
name|refCount
operator|>
name|Segment
operator|.
name|SEGMENT_REFERENCE_LIMIT
condition|)
block|{
name|refCount
operator|-=
name|idCount
expr_stmt|;
name|Set
argument_list|<
name|SegmentId
argument_list|>
name|segmentIds
init|=
name|newHashSet
argument_list|()
decl_stmt|;
comment|// The set of old record ids in this segment
comment|// that were previously root record ids, but will no longer be,
comment|// because the record to be written references them.
comment|// This needs to be a set, because the list of ids can
comment|// potentially reference the same record multiple times
name|Set
argument_list|<
name|RecordId
argument_list|>
name|notRoots
init|=
operator|new
name|HashSet
argument_list|<
name|RecordId
argument_list|>
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
operator|(
name|segmentId
operator|.
name|equals
argument_list|(
name|segment
operator|.
name|getSegmentId
argument_list|()
argument_list|)
operator|)
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
elseif|else
if|if
condition|(
name|roots
operator|.
name|containsKey
argument_list|(
name|recordId
argument_list|)
condition|)
block|{
name|notRoots
operator|.
name|add
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
block|}
block|}
name|rootCount
operator|-=
name|notRoots
operator|.
name|size
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|segmentIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|refid
init|=
literal|1
init|;
name|refid
operator|<
name|refCount
condition|;
name|refid
operator|++
control|)
block|{
name|segmentIds
operator|.
name|remove
argument_list|(
name|segment
operator|.
name|getRefId
argument_list|(
name|refid
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|refCount
operator|+=
name|segmentIds
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|headerSize
operator|=
name|refCount
operator|*
literal|16
operator|+
name|rootCount
operator|*
literal|3
operator|+
name|blobRefCount
operator|*
literal|2
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
if|if
condition|(
name|segmentSize
operator|>
name|buffer
operator|.
name|length
operator|-
literal|1
operator|||
name|blobRefCount
operator|>
literal|0xffff
operator|||
name|rootCount
operator|>
literal|0xffff
operator|||
name|refCount
operator|>
name|Segment
operator|.
name|SEGMENT_REFERENCE_LIMIT
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
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
name|checkState
argument_list|(
name|position
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|RecordId
name|id
init|=
operator|new
name|RecordId
argument_list|(
name|segment
operator|.
name|getSegmentId
argument_list|()
argument_list|,
name|position
argument_list|)
decl_stmt|;
name|roots
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|type
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
block|}
end_class

end_unit

