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
name|InputStream
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
name|ArrayList
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
name|Collections
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
name|Set
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
name|collect
operator|.
name|Lists
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
name|io
operator|.
name|ByteStreams
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentWriter
block|{
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_BUFFER_SIZE
init|=
literal|1
operator|<<
literal|12
decl_stmt|;
comment|// 4kB
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|1
operator|<<
literal|12
decl_stmt|;
comment|// 4kB
specifier|static
specifier|final
name|int
name|INLINE_BLOCKS
init|=
literal|16
decl_stmt|;
specifier|static
specifier|final
name|int
name|INLINE_SIZE
init|=
name|INLINE_BLOCKS
operator|*
name|BLOCK_SIZE
decl_stmt|;
comment|// 64kB
specifier|private
specifier|final
name|SegmentStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|int
name|blocksPerSegment
decl_stmt|;
specifier|private
specifier|final
name|int
name|blockSegmentSize
decl_stmt|;
specifier|private
name|UUID
name|uuid
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|UUID
argument_list|>
name|uuids
init|=
operator|new
name|ArrayList
argument_list|<
name|UUID
argument_list|>
argument_list|(
literal|255
argument_list|)
decl_stmt|;
specifier|private
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|INITIAL_BUFFER_SIZE
argument_list|)
decl_stmt|;
specifier|public
name|SegmentWriter
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
name|this
operator|.
name|blocksPerSegment
operator|=
name|store
operator|.
name|getMaxSegmentSize
argument_list|()
operator|/
name|BLOCK_SIZE
expr_stmt|;
name|this
operator|.
name|blockSegmentSize
operator|=
name|blocksPerSegment
operator|*
name|BLOCK_SIZE
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|flush
parameter_list|()
block|{
if|if
condition|(
name|buffer
operator|.
name|position
argument_list|()
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|buffer
operator|.
name|position
argument_list|()
index|]
decl_stmt|;
name|buffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|get
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|store
operator|.
name|createSegment
argument_list|(
operator|new
name|Segment
argument_list|(
name|uuid
argument_list|,
name|data
argument_list|,
name|uuids
operator|.
name|toArray
argument_list|(
operator|new
name|UUID
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|uuid
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
expr_stmt|;
name|uuids
operator|.
name|clear
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|RecordId
name|prepare
parameter_list|(
name|int
name|size
parameter_list|)
block|{
return|return
name|prepare
argument_list|(
name|size
argument_list|,
name|Collections
operator|.
expr|<
name|RecordId
operator|>
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|synchronized
name|RecordId
name|prepare
parameter_list|(
name|int
name|size
parameter_list|,
name|Collection
argument_list|<
name|RecordId
argument_list|>
name|ids
parameter_list|)
block|{
name|Set
argument_list|<
name|UUID
argument_list|>
name|segmentIds
init|=
operator|new
name|HashSet
argument_list|<
name|UUID
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RecordId
name|id
range|:
name|ids
control|)
block|{
name|UUID
name|segmentId
init|=
name|id
operator|.
name|getSegmentId
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|uuid
operator|.
name|equals
argument_list|(
name|segmentId
argument_list|)
operator|&&
operator|!
name|uuids
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
name|int
name|fullSize
init|=
name|size
operator|+
literal|4
operator|*
name|ids
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|buffer
operator|.
name|position
argument_list|()
operator|+
name|fullSize
operator|>
name|store
operator|.
name|getMaxSegmentSize
argument_list|()
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|fullSize
operator|>
name|buffer
operator|.
name|remaining
argument_list|()
condition|)
block|{
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|buffer
operator|.
name|capacity
argument_list|()
operator|*
literal|2
argument_list|,
name|store
operator|.
name|getMaxSegmentSize
argument_list|()
argument_list|)
decl_stmt|;
name|ByteBuffer
name|newBuffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|newBuffer
operator|.
name|put
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|buffer
operator|=
name|newBuffer
expr_stmt|;
block|}
return|return
operator|new
name|RecordId
argument_list|(
name|uuid
argument_list|,
name|buffer
operator|.
name|position
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|synchronized
name|void
name|writeRecordId
parameter_list|(
name|RecordId
name|id
parameter_list|)
block|{
name|UUID
name|segmentId
init|=
name|id
operator|.
name|getSegmentId
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|uuids
operator|.
name|indexOf
argument_list|(
name|segmentId
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
operator|-
literal|1
condition|)
block|{
name|index
operator|=
name|uuids
operator|.
name|size
argument_list|()
expr_stmt|;
name|uuids
operator|.
name|add
argument_list|(
name|segmentId
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|putInt
argument_list|(
name|index
operator|<<
literal|24
operator||
name|id
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeInlineBlocks
parameter_list|(
name|List
argument_list|<
name|RecordId
argument_list|>
name|blockIds
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
name|int
name|begin
init|=
name|offset
decl_stmt|;
name|int
name|end
init|=
name|offset
operator|+
name|length
decl_stmt|;
while|while
condition|(
name|begin
operator|+
name|BLOCK_SIZE
operator|<=
name|end
condition|)
block|{
name|blockIds
operator|.
name|add
argument_list|(
name|writeBlock
argument_list|(
name|buffer
argument_list|,
name|begin
argument_list|,
name|BLOCK_SIZE
argument_list|)
argument_list|)
expr_stmt|;
name|begin
operator|+=
name|BLOCK_SIZE
expr_stmt|;
block|}
if|if
condition|(
name|begin
operator|<
name|end
condition|)
block|{
name|blockIds
operator|.
name|add
argument_list|(
name|writeBlock
argument_list|(
name|buffer
argument_list|,
name|begin
argument_list|,
name|end
operator|-
name|begin
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|writeBulkSegment
parameter_list|(
name|List
argument_list|<
name|RecordId
argument_list|>
name|blockIds
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
name|UUID
name|segmentId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|store
operator|.
name|createSegment
argument_list|(
name|segmentId
argument_list|,
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|position
init|=
literal|0
init|;
name|position
operator|<
name|length
condition|;
name|position
operator|+=
name|BLOCK_SIZE
control|)
block|{
name|blockIds
operator|.
name|add
argument_list|(
operator|new
name|RecordId
argument_list|(
name|segmentId
argument_list|,
name|position
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|synchronized
name|RecordId
name|writeListBucket
parameter_list|(
name|List
argument_list|<
name|RecordId
argument_list|>
name|bucket
parameter_list|)
block|{
name|RecordId
name|bucketId
init|=
name|prepare
argument_list|(
literal|0
argument_list|,
name|bucket
argument_list|)
decl_stmt|;
for|for
control|(
name|RecordId
name|id
range|:
name|bucket
control|)
block|{
name|writeRecordId
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
return|return
name|bucketId
return|;
block|}
specifier|private
specifier|synchronized
name|RecordId
name|writeValueRecord
parameter_list|(
name|long
name|length
parameter_list|,
name|RecordId
name|blocks
parameter_list|)
block|{
name|RecordId
name|valueId
init|=
name|prepare
argument_list|(
literal|8
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|blocks
argument_list|)
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|putLong
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|writeRecordId
argument_list|(
name|blocks
argument_list|)
expr_stmt|;
return|return
name|valueId
return|;
block|}
comment|/**      * Writes a block record containing the given block of bytes.      *      * @param bytes source buffer      * @param offset offset within the source buffer      * @param length number of bytes to write      * @return block record identifier      */
specifier|public
specifier|synchronized
name|RecordId
name|writeBlock
parameter_list|(
name|byte
index|[]
name|bytes
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
name|bytes
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
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|RecordId
name|blockId
init|=
name|prepare
argument_list|(
name|length
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|put
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|blockId
return|;
block|}
comment|/**      * Writes a list record containing the given list of record identifiers.      *      * @param ids list of record identifiers      * @return list record identifier      */
specifier|public
name|RecordId
name|writeList
parameter_list|(
name|List
argument_list|<
name|RecordId
argument_list|>
name|ids
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|ids
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|ids
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
name|prepare
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|RecordId
argument_list|>
name|thisLevel
init|=
name|ids
decl_stmt|;
while|while
condition|(
name|thisLevel
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|List
argument_list|<
name|RecordId
argument_list|>
name|nextLevel
init|=
operator|new
name|ArrayList
argument_list|<
name|RecordId
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|RecordId
argument_list|>
name|bucket
range|:
name|Lists
operator|.
name|partition
argument_list|(
name|thisLevel
argument_list|,
name|ListRecord
operator|.
name|LEVEL_SIZE
argument_list|)
control|)
block|{
name|nextLevel
operator|.
name|add
argument_list|(
name|writeListBucket
argument_list|(
name|bucket
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|thisLevel
operator|=
name|nextLevel
expr_stmt|;
block|}
return|return
name|thisLevel
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
return|;
block|}
block|}
comment|/**      * Writes a string value record.      *      * @param string string to be written      * @return value record identifier      */
specifier|public
name|RecordId
name|writeString
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
name|string
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RecordId
argument_list|>
name|blockIds
init|=
operator|new
name|ArrayList
argument_list|<
name|RecordId
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|headLength
init|=
name|Math
operator|.
name|min
argument_list|(
name|data
operator|.
name|length
argument_list|,
name|INLINE_SIZE
argument_list|)
decl_stmt|;
name|writeInlineBlocks
argument_list|(
name|blockIds
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|headLength
argument_list|)
expr_stmt|;
if|if
condition|(
name|data
operator|.
name|length
operator|>
name|headLength
condition|)
block|{
name|int
name|offset
init|=
name|headLength
decl_stmt|;
while|while
condition|(
name|offset
operator|+
name|INLINE_SIZE
operator|<=
name|data
operator|.
name|length
condition|)
block|{
name|int
name|bulkLength
init|=
name|Math
operator|.
name|min
argument_list|(
name|data
operator|.
name|length
operator|-
name|offset
argument_list|,
name|blockSegmentSize
argument_list|)
decl_stmt|;
name|writeBulkSegment
argument_list|(
name|blockIds
argument_list|,
name|data
argument_list|,
name|offset
argument_list|,
name|bulkLength
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|bulkLength
expr_stmt|;
block|}
if|if
condition|(
name|offset
operator|<
name|data
operator|.
name|length
condition|)
block|{
name|writeInlineBlocks
argument_list|(
name|blockIds
argument_list|,
name|data
argument_list|,
name|offset
argument_list|,
name|data
operator|.
name|length
operator|-
name|offset
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|writeValueRecord
argument_list|(
name|data
operator|.
name|length
argument_list|,
name|writeList
argument_list|(
name|blockIds
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Writes a stream value record. The given stream is consumed      *<em>and closed</em> by this method.      *      * @param stream stream to be written      * @return value record identifier      * @throws IOException if the stream could not be read      */
specifier|public
name|RecordId
name|writeStream
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|List
argument_list|<
name|RecordId
argument_list|>
name|blockIds
init|=
operator|new
name|ArrayList
argument_list|<
name|RecordId
argument_list|>
argument_list|()
decl_stmt|;
comment|// First read the head of the stream. This covers most small
comment|// binaries and the frequently accessed head of larger ones.
comment|// The head gets inlined in the current segment.
name|byte
index|[]
name|head
init|=
operator|new
name|byte
index|[
name|INLINE_SIZE
index|]
decl_stmt|;
name|int
name|headLength
init|=
name|ByteStreams
operator|.
name|read
argument_list|(
name|stream
argument_list|,
name|head
argument_list|,
literal|0
argument_list|,
name|head
operator|.
name|length
argument_list|)
decl_stmt|;
name|writeInlineBlocks
argument_list|(
name|blockIds
argument_list|,
name|head
argument_list|,
literal|0
argument_list|,
name|headLength
argument_list|)
expr_stmt|;
name|long
name|length
init|=
name|headLength
decl_stmt|;
comment|// If the stream filled the full head buffer, it's likely that
comment|// the bulk of the data is still to come. Read it in larger
comment|// chunks and save in separate segments.
if|if
condition|(
name|headLength
operator|==
name|head
operator|.
name|length
condition|)
block|{
name|byte
index|[]
name|bulk
init|=
operator|new
name|byte
index|[
name|blockSegmentSize
index|]
decl_stmt|;
name|int
name|bulkLength
init|=
name|ByteStreams
operator|.
name|read
argument_list|(
name|stream
argument_list|,
name|bulk
argument_list|,
literal|0
argument_list|,
name|bulk
operator|.
name|length
argument_list|)
decl_stmt|;
while|while
condition|(
name|bulkLength
operator|>
name|INLINE_SIZE
condition|)
block|{
name|writeBulkSegment
argument_list|(
name|blockIds
argument_list|,
name|bulk
argument_list|,
literal|0
argument_list|,
name|bulkLength
argument_list|)
expr_stmt|;
name|length
operator|+=
name|bulkLength
expr_stmt|;
name|bulkLength
operator|=
name|ByteStreams
operator|.
name|read
argument_list|(
name|stream
argument_list|,
name|bulk
argument_list|,
literal|0
argument_list|,
name|bulk
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|// The tail chunk of the stream is too small to put in
comment|// a separate segment, so we inline also it.
if|if
condition|(
name|bulkLength
operator|>
literal|0
condition|)
block|{
name|writeInlineBlocks
argument_list|(
name|blockIds
argument_list|,
name|bulk
argument_list|,
literal|0
argument_list|,
name|bulkLength
argument_list|)
expr_stmt|;
name|length
operator|+=
name|bulkLength
expr_stmt|;
block|}
block|}
return|return
name|writeValueRecord
argument_list|(
name|length
argument_list|,
name|writeList
argument_list|(
name|blockIds
argument_list|)
argument_list|)
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
block|}
end_class

end_unit

