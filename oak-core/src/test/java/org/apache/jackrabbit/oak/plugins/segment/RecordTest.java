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
name|ListRecord
operator|.
name|LEVEL_SIZE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Random
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
name|NodeBuilder
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
name|junit
operator|.
name|Test
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
name|ImmutableMap
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
name|RecordTest
block|{
specifier|private
name|String
name|hello
init|=
literal|"Hello, World!"
decl_stmt|;
specifier|private
name|byte
index|[]
name|bytes
init|=
name|hello
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
specifier|private
name|SegmentStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
specifier|private
name|SegmentReader
name|reader
init|=
operator|new
name|SegmentReader
argument_list|(
name|store
argument_list|)
decl_stmt|;
specifier|private
name|SegmentWriter
name|writer
init|=
operator|new
name|SegmentWriter
argument_list|(
name|store
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
literal|0xcafefaceL
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testBlockRecord
parameter_list|()
block|{
name|RecordId
name|blockId
init|=
name|writer
operator|.
name|writeBlock
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|BlockRecord
name|block
init|=
operator|new
name|BlockRecord
argument_list|(
name|blockId
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
decl_stmt|;
comment|// Check reading with all valid positions and lengths
for|for
control|(
name|int
name|n
init|=
literal|1
init|;
name|n
operator|<
name|bytes
operator|.
name|length
condition|;
name|n
operator|++
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|+
name|n
operator|<=
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|bytes
argument_list|,
name|i
argument_list|,
name|i
operator|+
name|n
argument_list|,
operator|(
name|byte
operator|)
literal|'.'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|n
argument_list|,
name|block
operator|.
name|read
argument_list|(
name|reader
argument_list|,
name|i
argument_list|,
name|bytes
argument_list|,
name|i
argument_list|,
name|n
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hello
argument_list|,
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Check reading with a too long length
name|byte
index|[]
name|large
init|=
operator|new
name|byte
index|[
name|bytes
operator|.
name|length
operator|*
literal|2
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|bytes
operator|.
name|length
argument_list|,
name|block
operator|.
name|read
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|large
argument_list|,
literal|0
argument_list|,
name|large
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hello
argument_list|,
operator|new
name|String
argument_list|(
name|large
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testListRecord
parameter_list|()
block|{
name|RecordId
name|blockId
init|=
name|writer
operator|.
name|writeBlock
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
decl_stmt|;
name|ListRecord
name|one
init|=
name|writeList
argument_list|(
literal|1
argument_list|,
name|blockId
argument_list|)
decl_stmt|;
name|ListRecord
name|level1
init|=
name|writeList
argument_list|(
name|LEVEL_SIZE
argument_list|,
name|blockId
argument_list|)
decl_stmt|;
name|ListRecord
name|level1p
init|=
name|writeList
argument_list|(
name|LEVEL_SIZE
operator|+
literal|1
argument_list|,
name|blockId
argument_list|)
decl_stmt|;
name|ListRecord
name|level2
init|=
name|writeList
argument_list|(
name|LEVEL_SIZE
operator|*
name|LEVEL_SIZE
argument_list|,
name|blockId
argument_list|)
decl_stmt|;
name|ListRecord
name|level2p
init|=
name|writeList
argument_list|(
name|LEVEL_SIZE
operator|*
name|LEVEL_SIZE
operator|+
literal|1
argument_list|,
name|blockId
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|one
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockId
argument_list|,
name|one
operator|.
name|getEntry
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LEVEL_SIZE
argument_list|,
name|level1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockId
argument_list|,
name|level1
operator|.
name|getEntry
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockId
argument_list|,
name|level1
operator|.
name|getEntry
argument_list|(
name|reader
argument_list|,
name|LEVEL_SIZE
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LEVEL_SIZE
operator|+
literal|1
argument_list|,
name|level1p
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockId
argument_list|,
name|level1p
operator|.
name|getEntry
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockId
argument_list|,
name|level1p
operator|.
name|getEntry
argument_list|(
name|reader
argument_list|,
name|LEVEL_SIZE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LEVEL_SIZE
operator|*
name|LEVEL_SIZE
argument_list|,
name|level2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockId
argument_list|,
name|level2
operator|.
name|getEntry
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockId
argument_list|,
name|level2
operator|.
name|getEntry
argument_list|(
name|reader
argument_list|,
name|LEVEL_SIZE
operator|*
name|LEVEL_SIZE
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LEVEL_SIZE
operator|*
name|LEVEL_SIZE
operator|+
literal|1
argument_list|,
name|level2p
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockId
argument_list|,
name|level2p
operator|.
name|getEntry
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockId
argument_list|,
name|level2p
operator|.
name|getEntry
argument_list|(
name|reader
argument_list|,
name|LEVEL_SIZE
operator|*
name|LEVEL_SIZE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ListRecord
name|writeList
parameter_list|(
name|int
name|size
parameter_list|,
name|RecordId
name|id
parameter_list|)
block|{
name|List
argument_list|<
name|RecordId
argument_list|>
name|list
init|=
name|Collections
operator|.
name|nCopies
argument_list|(
name|size
argument_list|,
name|id
argument_list|)
decl_stmt|;
return|return
operator|new
name|ListRecord
argument_list|(
name|writer
operator|.
name|writeList
argument_list|(
name|list
argument_list|)
argument_list|,
name|size
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStreamRecord
parameter_list|()
throws|throws
name|IOException
block|{
name|checkRandomStreamRecord
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|checkRandomStreamRecord
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|checkRandomStreamRecord
argument_list|(
literal|0x79
argument_list|)
expr_stmt|;
name|checkRandomStreamRecord
argument_list|(
literal|0x80
argument_list|)
expr_stmt|;
name|checkRandomStreamRecord
argument_list|(
literal|0x4079
argument_list|)
expr_stmt|;
name|checkRandomStreamRecord
argument_list|(
literal|0x4080
argument_list|)
expr_stmt|;
name|checkRandomStreamRecord
argument_list|(
name|SegmentWriter
operator|.
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|checkRandomStreamRecord
argument_list|(
name|SegmentWriter
operator|.
name|BLOCK_SIZE
operator|+
literal|1
argument_list|)
expr_stmt|;
name|checkRandomStreamRecord
argument_list|(
name|Segment
operator|.
name|MAX_SEGMENT_SIZE
argument_list|)
expr_stmt|;
name|checkRandomStreamRecord
argument_list|(
name|Segment
operator|.
name|MAX_SEGMENT_SIZE
operator|+
literal|1
argument_list|)
expr_stmt|;
name|checkRandomStreamRecord
argument_list|(
name|Segment
operator|.
name|MAX_SEGMENT_SIZE
operator|*
literal|2
argument_list|)
expr_stmt|;
name|checkRandomStreamRecord
argument_list|(
name|Segment
operator|.
name|MAX_SEGMENT_SIZE
operator|*
literal|2
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkRandomStreamRecord
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|source
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|RecordId
name|valueId
init|=
name|writer
operator|.
name|writeStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|source
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|InputStream
name|stream
init|=
name|reader
operator|.
name|readStream
argument_list|(
name|valueId
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|349
index|]
decl_stmt|;
comment|// prime number
name|int
name|offset
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
name|stream
operator|.
name|read
argument_list|(
name|b
argument_list|)
init|;
name|n
operator|!=
operator|-
literal|1
condition|;
name|n
operator|=
name|stream
operator|.
name|read
argument_list|(
name|b
argument_list|)
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|source
index|[
name|offset
operator|+
name|i
index|]
argument_list|,
name|b
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|offset
operator|+=
name|n
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|offset
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
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
annotation|@
name|Test
specifier|public
name|void
name|testStringRecord
parameter_list|()
block|{
name|RecordId
name|empty
init|=
name|writer
operator|.
name|writeString
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|RecordId
name|space
init|=
name|writer
operator|.
name|writeString
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
name|RecordId
name|hello
init|=
name|writer
operator|.
name|writeString
argument_list|(
literal|"Hello, World!"
argument_list|)
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
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
literal|100000
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
call|(
name|char
call|)
argument_list|(
literal|'0'
operator|+
name|i
operator|%
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|RecordId
name|large
init|=
name|writer
operator|.
name|writeString
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|large
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|segment
operator|.
name|readString
argument_list|(
name|empty
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|" "
argument_list|,
name|segment
operator|.
name|readString
argument_list|(
name|space
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Hello, World!"
argument_list|,
name|segment
operator|.
name|readString
argument_list|(
name|hello
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|,
name|segment
operator|.
name|readString
argument_list|(
name|large
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMapRecord
parameter_list|()
block|{
name|RecordId
name|blockId
init|=
name|writer
operator|.
name|writeBlock
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
decl_stmt|;
name|MapRecord
name|zero
init|=
name|writer
operator|.
name|writeMap
argument_list|(
literal|null
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|RecordId
operator|>
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|MapRecord
name|one
init|=
name|writer
operator|.
name|writeMap
argument_list|(
literal|null
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"one"
argument_list|,
name|blockId
argument_list|)
argument_list|)
decl_stmt|;
name|MapRecord
name|two
init|=
name|writer
operator|.
name|writeMap
argument_list|(
literal|null
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"one"
argument_list|,
name|blockId
argument_list|,
literal|"two"
argument_list|,
name|blockId
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|RecordId
argument_list|>
name|map
init|=
name|Maps
operator|.
name|newHashMap
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|map
operator|.
name|put
argument_list|(
literal|"key"
operator|+
name|i
argument_list|,
name|blockId
argument_list|)
expr_stmt|;
block|}
name|MapRecord
name|many
init|=
name|writer
operator|.
name|writeMap
argument_list|(
literal|null
argument_list|,
name|map
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|Iterator
argument_list|<
name|MapEntry
argument_list|>
name|iterator
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|zero
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|zero
operator|.
name|getEntry
argument_list|(
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|iterator
operator|=
name|zero
operator|.
name|getEntries
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|one
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockId
argument_list|,
name|one
operator|.
name|getEntry
argument_list|(
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|one
operator|.
name|getEntry
argument_list|(
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|iterator
operator|=
name|one
operator|.
name|getEntries
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one"
argument_list|,
name|iterator
operator|.
name|next
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|two
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockId
argument_list|,
name|two
operator|.
name|getEntry
argument_list|(
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockId
argument_list|,
name|two
operator|.
name|getEntry
argument_list|(
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|two
operator|.
name|getEntry
argument_list|(
literal|"three"
argument_list|)
argument_list|)
expr_stmt|;
name|iterator
operator|=
name|two
operator|.
name|getEntries
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|many
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|iterator
operator|=
name|many
operator|.
name|getEntries
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockId
argument_list|,
name|iterator
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockId
argument_list|,
name|many
operator|.
name|getEntry
argument_list|(
literal|"key"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|many
operator|.
name|getEntry
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|RecordId
argument_list|>
name|changes
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|changes
operator|.
name|put
argument_list|(
literal|"key0"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|changes
operator|.
name|put
argument_list|(
literal|"key1000"
argument_list|,
name|blockId
argument_list|)
expr_stmt|;
name|MapRecord
name|modified
init|=
name|writer
operator|.
name|writeMap
argument_list|(
name|many
argument_list|,
name|changes
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|modified
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|iterator
operator|=
name|modified
operator|.
name|getEntries
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockId
argument_list|,
name|iterator
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockId
argument_list|,
name|modified
operator|.
name|getEntry
argument_list|(
literal|"key"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|many
operator|.
name|getEntry
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWorstCaseMap
parameter_list|()
block|{
name|RecordId
name|blockId
init|=
name|writer
operator|.
name|writeBlock
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|RecordId
argument_list|>
name|map
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|char
index|[]
name|key
init|=
operator|new
name|char
index|[
literal|2
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
operator|<=
name|MapRecord
operator|.
name|BUCKETS_PER_LEVEL
condition|;
name|i
operator|++
control|)
block|{
name|key
index|[
literal|0
index|]
operator|=
call|(
name|char
call|)
argument_list|(
literal|'A'
operator|+
name|i
argument_list|)
expr_stmt|;
name|key
index|[
literal|1
index|]
operator|=
call|(
name|char
call|)
argument_list|(
literal|'\u1000'
operator|-
name|key
index|[
literal|0
index|]
operator|*
literal|31
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
operator|new
name|String
argument_list|(
name|key
argument_list|)
argument_list|,
name|blockId
argument_list|)
expr_stmt|;
block|}
name|MapRecord
name|bad
init|=
name|writer
operator|.
name|writeMap
argument_list|(
literal|null
argument_list|,
name|map
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|map
operator|.
name|size
argument_list|()
argument_list|,
name|bad
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|MapEntry
argument_list|>
name|iterator
init|=
name|bad
operator|.
name|getEntries
argument_list|()
operator|.
name|iterator
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
name|map
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'\u1000'
argument_list|,
name|iterator
operator|.
name|next
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyNode
parameter_list|()
block|{
name|NodeState
name|before
init|=
name|EMPTY_NODE
decl_stmt|;
name|NodeState
name|after
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|before
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleNode
parameter_list|()
block|{
name|NodeState
name|before
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"bar"
argument_list|,
literal|123
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"baz"
argument_list|,
name|Math
operator|.
name|PI
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|after
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|before
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeepNode
parameter_list|()
block|{
name|NodeBuilder
name|root
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|=
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|after
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|before
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testManyMapDeletes
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|NodeState
name|before
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|before
argument_list|)
expr_stmt|;
name|builder
operator|=
name|before
operator|.
name|builder
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|900
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|removeChildNode
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|NodeState
name|after
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

