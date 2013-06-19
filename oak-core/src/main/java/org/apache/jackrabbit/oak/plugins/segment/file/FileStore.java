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
name|newArrayListWithCapacity
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
operator|.
name|MapMode
operator|.
name|READ_WRITE
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|RandomAccessFile
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
name|nio
operator|.
name|MappedByteBuffer
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
name|Map
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
name|ConcurrentMap
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
name|Journal
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
name|RecordId
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
name|Segment
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
name|SegmentStore
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
name|Template
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
name|NodeState
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
name|Maps
import|;
end_import

begin_class
specifier|public
class|class
name|FileStore
implements|implements
name|SegmentStore
block|{
comment|/** Logger instance */
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FileStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|SEGMENT_MAGIC
init|=
literal|0x4f616b0a527845ddL
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|JOURNAL_MAGIC
init|=
literal|0xdf36544212c0cb24L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|PADDING_MAGIC
init|=
literal|0x786da7779516c12L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JOURNALS_UUID
init|=
operator|new
name|UUID
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PADDING_UUID
init|=
operator|new
name|UUID
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|FILE_SIZE
init|=
literal|256
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FILE_NAME_FORMAT
init|=
literal|"data%05d.tar"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|SEGMENT_SIZE
init|=
literal|0x200
decl_stmt|;
comment|// 512
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|PADDING_BYTES
init|=
operator|new
name|byte
index|[
name|SEGMENT_SIZE
index|]
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Journal
argument_list|>
name|journals
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|File
name|directory
decl_stmt|;
specifier|private
name|int
name|index
decl_stmt|;
specifier|private
name|MappedByteBuffer
name|rw
decl_stmt|;
specifier|private
name|ByteBuffer
name|ro
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|UUID
argument_list|,
name|Segment
argument_list|>
name|segments
init|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
specifier|public
name|FileStore
parameter_list|(
name|File
name|directory
parameter_list|,
name|NodeState
name|root
parameter_list|)
throws|throws
name|IOException
block|{
comment|// http://www.oracle.com/technetwork/java/hotspotfaq-138619.html#64bit_detection
if|if
condition|(
literal|"32"
operator|.
name|equals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"sun.arch.data.model"
argument_list|)
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"TarMK will only work with small repositories"
operator|+
literal|" in a 32 bit JVM. Consider switching to a 64 bit JVM."
argument_list|)
expr_stmt|;
block|}
name|checkNotNull
argument_list|(
name|directory
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|index
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|loadSegments
argument_list|()
condition|)
block|{
name|this
operator|.
name|index
operator|++
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|journals
operator|.
name|containsKey
argument_list|(
literal|"root"
argument_list|)
condition|)
block|{
name|journals
operator|.
name|put
argument_list|(
literal|"root"
argument_list|,
operator|new
name|FileJournal
argument_list|(
name|this
argument_list|,
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|FileStore
parameter_list|(
name|File
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|directory
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FileStore
parameter_list|(
name|String
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|rw
operator|.
name|force
argument_list|()
expr_stmt|;
name|segments
operator|.
name|clear
argument_list|()
expr_stmt|;
name|rw
operator|=
literal|null
expr_stmt|;
name|ro
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
specifier|private
name|boolean
name|loadSegments
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|name
init|=
name|String
operator|.
name|format
argument_list|(
name|FILE_NAME_FORMAT
argument_list|,
name|index
argument_list|)
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|long
name|size
init|=
name|FILE_SIZE
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|size
operator|=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|RandomAccessFile
name|f
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
try|try
block|{
name|rw
operator|=
name|f
operator|.
name|getChannel
argument_list|()
operator|.
name|map
argument_list|(
name|READ_WRITE
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|ro
operator|=
name|rw
operator|.
name|asReadOnlyBuffer
argument_list|()
expr_stmt|;
while|while
condition|(
name|ro
operator|.
name|remaining
argument_list|()
operator|>=
literal|4
operator|*
name|SEGMENT_SIZE
condition|)
block|{
comment|// skip tar header and get the magic bytes; TODO: verify?
name|long
name|magic
init|=
name|ro
operator|.
name|getLong
argument_list|(
name|ro
operator|.
name|position
argument_list|()
operator|+
name|SEGMENT_SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|magic
operator|==
name|SEGMENT_MAGIC
condition|)
block|{
name|ro
operator|.
name|position
argument_list|(
name|ro
operator|.
name|position
argument_list|()
operator|+
name|SEGMENT_SIZE
operator|+
literal|8
argument_list|)
expr_stmt|;
name|int
name|length
init|=
name|ro
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|int
name|count
init|=
name|ro
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|UUID
name|segmentId
init|=
operator|new
name|UUID
argument_list|(
name|ro
operator|.
name|getLong
argument_list|()
argument_list|,
name|ro
operator|.
name|getLong
argument_list|()
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|UUID
argument_list|>
name|referencedSegmentIds
init|=
name|newArrayListWithCapacity
argument_list|(
name|count
argument_list|)
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|referencedSegmentIds
operator|.
name|add
argument_list|(
operator|new
name|UUID
argument_list|(
name|ro
operator|.
name|getLong
argument_list|()
argument_list|,
name|ro
operator|.
name|getLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ro
operator|.
name|limit
argument_list|(
name|ro
operator|.
name|position
argument_list|()
operator|+
name|length
argument_list|)
expr_stmt|;
name|ByteBuffer
name|data
init|=
name|ro
operator|.
name|slice
argument_list|()
decl_stmt|;
name|ro
operator|.
name|limit
argument_list|(
name|rw
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
operator|new
name|Segment
argument_list|(
name|this
argument_list|,
name|segmentId
argument_list|,
name|data
argument_list|,
name|referencedSegmentIds
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|RecordId
operator|>
name|emptyMap
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|Template
argument_list|,
name|RecordId
operator|>
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|segments
operator|.
name|put
argument_list|(
name|segmentId
argument_list|,
name|segment
argument_list|)
expr_stmt|;
comment|// advance to next entry in the file
name|ro
operator|.
name|position
argument_list|(
operator|(
name|ro
operator|.
name|position
argument_list|()
operator|+
name|length
operator|+
literal|0x1ff
operator|)
operator|&
operator|~
literal|0x1ff
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|magic
operator|==
name|JOURNAL_MAGIC
condition|)
block|{
name|ro
operator|.
name|position
argument_list|(
name|ro
operator|.
name|position
argument_list|()
operator|+
name|SEGMENT_SIZE
operator|+
literal|8
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|ro
operator|.
name|getInt
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|n
init|=
operator|new
name|byte
index|[
name|ro
operator|.
name|getInt
argument_list|()
index|]
decl_stmt|;
name|ro
operator|.
name|get
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|h
init|=
operator|new
name|SegmentNodeState
argument_list|(
name|this
argument_list|,
operator|new
name|RecordId
argument_list|(
operator|new
name|UUID
argument_list|(
name|ro
operator|.
name|getLong
argument_list|()
argument_list|,
name|ro
operator|.
name|getLong
argument_list|()
argument_list|)
argument_list|,
name|ro
operator|.
name|getInt
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|journals
operator|.
name|put
argument_list|(
operator|new
name|String
argument_list|(
name|n
argument_list|,
name|UTF_8
argument_list|)
argument_list|,
operator|new
name|FileJournal
argument_list|(
name|this
argument_list|,
name|h
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// advance to next entry in the file
name|ro
operator|.
name|position
argument_list|(
operator|(
name|ro
operator|.
name|position
argument_list|()
operator|+
literal|0x1ff
operator|)
operator|&
operator|~
literal|0x1ff
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|magic
operator|==
name|PADDING_MAGIC
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// still space for more segments: position the write
comment|// buffer at this point and return false to stop looking
comment|// for more entries
name|rw
operator|.
name|position
argument_list|(
name|ro
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
finally|finally
block|{
name|f
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Journal
name|getJournal
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|Journal
name|journal
init|=
name|journals
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|journal
operator|==
literal|null
condition|)
block|{
name|journal
operator|=
operator|new
name|FileJournal
argument_list|(
name|this
argument_list|,
literal|"root"
argument_list|)
expr_stmt|;
name|journals
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|journal
argument_list|)
expr_stmt|;
block|}
return|return
name|journal
return|;
block|}
annotation|@
name|Override
specifier|public
name|Segment
name|readSegment
parameter_list|(
name|UUID
name|id
parameter_list|)
block|{
name|Segment
name|segment
init|=
name|segments
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|segment
operator|!=
literal|null
condition|)
block|{
return|return
name|segment
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Segment not found: "
operator|+
name|id
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|createSegment
parameter_list|(
name|UUID
name|segmentId
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|Collection
argument_list|<
name|UUID
argument_list|>
name|referencedSegmentIds
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RecordId
argument_list|>
name|strings
parameter_list|,
name|Map
argument_list|<
name|Template
argument_list|,
name|RecordId
argument_list|>
name|templates
parameter_list|)
block|{
name|int
name|size
init|=
literal|8
operator|+
literal|4
operator|+
literal|4
operator|+
literal|16
operator|+
literal|16
operator|*
name|referencedSegmentIds
operator|.
name|size
argument_list|()
operator|+
name|length
decl_stmt|;
name|prepare
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|rw
operator|.
name|put
argument_list|(
name|createTarHeader
argument_list|(
name|segmentId
operator|.
name|toString
argument_list|()
argument_list|,
name|size
argument_list|)
argument_list|)
expr_stmt|;
name|rw
operator|.
name|putLong
argument_list|(
name|SEGMENT_MAGIC
argument_list|)
expr_stmt|;
name|rw
operator|.
name|putInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|rw
operator|.
name|putInt
argument_list|(
name|referencedSegmentIds
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|rw
operator|.
name|putLong
argument_list|(
name|segmentId
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|rw
operator|.
name|putLong
argument_list|(
name|segmentId
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|UUID
name|referencedSegmentId
range|:
name|referencedSegmentIds
control|)
block|{
name|rw
operator|.
name|putLong
argument_list|(
name|referencedSegmentId
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|rw
operator|.
name|putLong
argument_list|(
name|referencedSegmentId
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ro
operator|.
name|position
argument_list|(
name|rw
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
name|rw
operator|.
name|put
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|ro
operator|.
name|limit
argument_list|(
name|rw
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
name|ByteBuffer
name|buffer
init|=
name|ro
operator|.
name|slice
argument_list|()
decl_stmt|;
name|ro
operator|.
name|limit
argument_list|(
name|rw
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|n
init|=
name|rw
operator|.
name|position
argument_list|()
operator|%
name|SEGMENT_SIZE
decl_stmt|;
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|rw
operator|.
name|put
argument_list|(
name|PADDING_BYTES
argument_list|,
literal|0
argument_list|,
name|SEGMENT_SIZE
operator|-
name|n
argument_list|)
expr_stmt|;
block|}
name|Segment
name|segment
init|=
operator|new
name|Segment
argument_list|(
name|this
argument_list|,
name|segmentId
argument_list|,
name|buffer
argument_list|,
name|referencedSegmentIds
argument_list|,
name|strings
argument_list|,
name|templates
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|segments
operator|.
name|put
argument_list|(
name|segmentId
argument_list|,
name|segment
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deleteSegment
parameter_list|(
name|UUID
name|segmentId
parameter_list|)
block|{
if|if
condition|(
name|segments
operator|.
name|remove
argument_list|(
name|segmentId
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Missing segment: "
operator|+
name|segmentId
argument_list|)
throw|;
block|}
block|}
specifier|synchronized
name|void
name|writeJournals
parameter_list|()
block|{
name|int
name|size
init|=
literal|8
operator|+
literal|4
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|journals
operator|.
name|keySet
argument_list|()
control|)
block|{
name|size
operator|+=
literal|4
operator|+
name|name
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
operator|.
name|length
operator|+
literal|16
operator|+
literal|4
expr_stmt|;
block|}
name|prepare
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|rw
operator|.
name|put
argument_list|(
name|createTarHeader
argument_list|(
name|JOURNALS_UUID
argument_list|,
name|size
argument_list|)
argument_list|)
expr_stmt|;
name|rw
operator|.
name|putLong
argument_list|(
name|JOURNAL_MAGIC
argument_list|)
expr_stmt|;
name|rw
operator|.
name|putInt
argument_list|(
name|journals
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Journal
argument_list|>
name|entry
range|:
name|journals
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|byte
index|[]
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
name|rw
operator|.
name|putInt
argument_list|(
name|name
operator|.
name|length
argument_list|)
expr_stmt|;
name|rw
operator|.
name|put
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|RecordId
name|head
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|rw
operator|.
name|putLong
argument_list|(
name|head
operator|.
name|getSegmentId
argument_list|()
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|rw
operator|.
name|putLong
argument_list|(
name|head
operator|.
name|getSegmentId
argument_list|()
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|rw
operator|.
name|putInt
argument_list|(
name|head
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|n
init|=
name|rw
operator|.
name|position
argument_list|()
operator|%
name|SEGMENT_SIZE
decl_stmt|;
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|rw
operator|.
name|put
argument_list|(
name|PADDING_BYTES
argument_list|,
literal|0
argument_list|,
name|SEGMENT_SIZE
operator|-
name|n
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|prepare
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|int
name|segments
init|=
operator|(
name|size
operator|+
name|SEGMENT_SIZE
operator|-
literal|1
operator|)
operator|/
name|SEGMENT_SIZE
decl_stmt|;
if|if
condition|(
operator|(
literal|1
operator|+
name|segments
operator|+
literal|2
operator|)
operator|*
name|SEGMENT_SIZE
operator|>
name|rw
operator|.
name|remaining
argument_list|()
condition|)
block|{
if|if
condition|(
name|rw
operator|.
name|remaining
argument_list|()
operator|>=
literal|3
operator|*
name|SEGMENT_SIZE
condition|)
block|{
comment|// Add a padding entry to avoid problems during reopening
name|rw
operator|.
name|put
argument_list|(
name|createTarHeader
argument_list|(
name|PADDING_UUID
argument_list|,
name|rw
operator|.
name|remaining
argument_list|()
operator|-
literal|3
operator|*
name|SEGMENT_SIZE
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|rw
operator|.
name|remaining
argument_list|()
operator|>
literal|2
operator|*
name|SEGMENT_SIZE
condition|)
block|{
name|rw
operator|.
name|putLong
argument_list|(
name|PADDING_MAGIC
argument_list|)
expr_stmt|;
name|rw
operator|.
name|put
argument_list|(
name|PADDING_BYTES
argument_list|,
literal|0
argument_list|,
name|SEGMENT_SIZE
operator|-
literal|8
argument_list|)
expr_stmt|;
block|}
block|}
while|while
condition|(
name|rw
operator|.
name|remaining
argument_list|()
operator|>
literal|0
condition|)
block|{
name|rw
operator|.
name|put
argument_list|(
name|PADDING_BYTES
argument_list|)
expr_stmt|;
block|}
name|rw
operator|.
name|force
argument_list|()
expr_stmt|;
name|String
name|name
init|=
name|String
operator|.
name|format
argument_list|(
name|FILE_NAME_FORMAT
argument_list|,
operator|++
name|index
argument_list|)
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|RandomAccessFile
name|f
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
try|try
block|{
name|rw
operator|=
name|f
operator|.
name|getChannel
argument_list|()
operator|.
name|map
argument_list|(
name|READ_WRITE
argument_list|,
literal|0
argument_list|,
name|FILE_SIZE
argument_list|)
expr_stmt|;
name|ro
operator|=
name|rw
operator|.
name|asReadOnlyBuffer
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|f
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to create a new segment"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
specifier|static
name|byte
index|[]
name|createTarHeader
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|byte
index|[]
name|header
init|=
operator|new
name|byte
index|[
name|SEGMENT_SIZE
index|]
decl_stmt|;
comment|// File name
name|byte
index|[]
name|n
init|=
name|name
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|n
argument_list|,
literal|0
argument_list|,
name|header
argument_list|,
literal|0
argument_list|,
name|n
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// File mode
name|System
operator|.
name|arraycopy
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%07o"
argument_list|,
literal|0400
argument_list|)
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|header
argument_list|,
literal|100
argument_list|,
literal|7
argument_list|)
expr_stmt|;
comment|// User's numeric user ID
name|System
operator|.
name|arraycopy
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%07o"
argument_list|,
literal|0
argument_list|)
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|header
argument_list|,
literal|108
argument_list|,
literal|7
argument_list|)
expr_stmt|;
comment|// Group's numeric user ID
name|System
operator|.
name|arraycopy
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%07o"
argument_list|,
literal|0
argument_list|)
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|header
argument_list|,
literal|116
argument_list|,
literal|7
argument_list|)
expr_stmt|;
comment|// File size in bytes (octal basis)
name|System
operator|.
name|arraycopy
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%011o"
argument_list|,
name|length
argument_list|)
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|header
argument_list|,
literal|124
argument_list|,
literal|11
argument_list|)
expr_stmt|;
comment|// Last modification time in numeric Unix time format (octal)
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|/
literal|1000
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%011o"
argument_list|,
name|time
argument_list|)
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|header
argument_list|,
literal|136
argument_list|,
literal|11
argument_list|)
expr_stmt|;
comment|// Checksum for header record
name|System
operator|.
name|arraycopy
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|}
argument_list|,
literal|0
argument_list|,
name|header
argument_list|,
literal|148
argument_list|,
literal|8
argument_list|)
expr_stmt|;
comment|// Type flag
name|header
index|[
literal|156
index|]
operator|=
literal|'0'
expr_stmt|;
comment|// Compute checksum
name|int
name|checksum
init|=
literal|0
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
name|header
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|checksum
operator|+=
name|header
index|[
name|i
index|]
operator|&
literal|0xff
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%06o"
argument_list|,
name|checksum
argument_list|)
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|header
argument_list|,
literal|148
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|header
index|[
literal|154
index|]
operator|=
literal|0
expr_stmt|;
return|return
name|header
return|;
block|}
block|}
end_class

end_unit

