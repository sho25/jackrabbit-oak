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
name|newLinkedList
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
name|newHashMap
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
name|LinkedList
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
name|AbstractStore
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
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
import|;
end_import

begin_class
specifier|public
class|class
name|FileStore
extends|extends
name|AbstractStore
block|{
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_MEMORY_CACHE_SIZE
init|=
literal|1
operator|<<
literal|28
decl_stmt|;
comment|// 256MB
specifier|private
specifier|static
specifier|final
name|long
name|JOURNAL_MAGIC
init|=
literal|0xdf36544212c0cb24L
decl_stmt|;
specifier|static
specifier|final
name|UUID
name|JOURNALS_UUID
init|=
operator|new
name|UUID
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
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
specifier|final
name|File
name|directory
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxFileSize
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|memoryMapping
decl_stmt|;
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|TarFile
argument_list|>
name|files
init|=
name|newLinkedList
argument_list|()
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
name|newHashMap
argument_list|()
decl_stmt|;
specifier|public
name|FileStore
parameter_list|(
name|File
name|directory
parameter_list|,
name|int
name|maxFileSize
parameter_list|,
name|boolean
name|memoryMapping
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|DEFAULT_MEMORY_CACHE_SIZE
argument_list|)
expr_stmt|;
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
name|maxFileSize
operator|=
name|maxFileSize
expr_stmt|;
name|this
operator|.
name|memoryMapping
operator|=
name|memoryMapping
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
literal|true
condition|;
name|i
operator|++
control|)
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
name|i
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
if|if
condition|(
name|file
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
operator|new
name|TarFile
argument_list|(
name|file
argument_list|,
name|maxFileSize
argument_list|,
name|memoryMapping
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
name|Segment
name|segment
init|=
name|getWriter
argument_list|()
operator|.
name|getDummySegment
argument_list|()
decl_stmt|;
for|for
control|(
name|TarFile
name|tar
range|:
name|files
control|)
block|{
name|ByteBuffer
name|buffer
init|=
name|tar
operator|.
name|readEntry
argument_list|(
name|JOURNALS_UUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|buffer
operator|!=
literal|null
condition|)
block|{
name|checkState
argument_list|(
name|JOURNAL_MAGIC
operator|==
name|buffer
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|buffer
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
name|b
init|=
operator|new
name|byte
index|[
name|buffer
operator|.
name|getInt
argument_list|()
index|]
decl_stmt|;
name|buffer
operator|.
name|get
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|String
name|name
init|=
operator|new
name|String
argument_list|(
name|b
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|RecordId
name|recordId
init|=
operator|new
name|RecordId
argument_list|(
operator|new
name|UUID
argument_list|(
name|buffer
operator|.
name|getLong
argument_list|()
argument_list|,
name|buffer
operator|.
name|getLong
argument_list|()
argument_list|)
argument_list|,
name|buffer
operator|.
name|getInt
argument_list|()
argument_list|)
decl_stmt|;
name|journals
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|FileJournal
argument_list|(
name|this
argument_list|,
operator|new
name|SegmentNodeState
argument_list|(
name|segment
argument_list|,
name|recordId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"root"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
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
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|TarFile
name|file
range|:
name|files
control|)
block|{
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|files
operator|.
name|clear
argument_list|()
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
comment|// for any memory-mappings that are no longer used
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
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
specifier|protected
name|Segment
name|loadSegment
parameter_list|(
name|UUID
name|id
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|TarFile
name|file
range|:
name|files
control|)
block|{
name|ByteBuffer
name|buffer
init|=
name|file
operator|.
name|readEntry
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|buffer
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|Segment
argument_list|(
name|FileStore
operator|.
name|this
argument_list|,
name|id
argument_list|,
name|buffer
argument_list|)
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Segment "
operator|+
name|id
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|writeSegment
parameter_list|(
name|UUID
name|segmentId
parameter_list|,
name|Collection
argument_list|<
name|UUID
argument_list|>
name|referencedSegmentIds
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
parameter_list|)
block|{
if|if
condition|(
operator|!
name|referencedSegmentIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|int
name|size
init|=
literal|16
operator|*
name|referencedSegmentIds
operator|.
name|size
argument_list|()
operator|+
name|length
decl_stmt|;
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|size
argument_list|)
decl_stmt|;
for|for
control|(
name|UUID
name|referencedSegmentId
range|:
name|referencedSegmentIds
control|)
block|{
name|buffer
operator|.
name|putLong
argument_list|(
name|referencedSegmentId
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
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
name|buffer
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
name|data
operator|=
name|buffer
operator|.
name|array
argument_list|()
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
name|length
operator|=
name|size
expr_stmt|;
block|}
try|try
block|{
name|writeEntry
argument_list|(
name|segmentId
argument_list|,
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
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
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|writeEntry
parameter_list|(
name|UUID
name|segmentId
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
throws|throws
name|IOException
block|{
if|if
condition|(
name|files
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|files
operator|.
name|getLast
argument_list|()
operator|.
name|writeEntry
argument_list|(
name|segmentId
argument_list|,
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
condition|)
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
name|files
operator|.
name|size
argument_list|()
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
name|TarFile
name|last
init|=
operator|new
name|TarFile
argument_list|(
name|file
argument_list|,
name|maxFileSize
argument_list|,
name|memoryMapping
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|last
operator|.
name|writeEntry
argument_list|(
name|segmentId
argument_list|,
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|files
operator|.
name|add
argument_list|(
name|last
argument_list|)
expr_stmt|;
block|}
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
comment|// TODO: implement
name|super
operator|.
name|deleteSegment
argument_list|(
name|segmentId
argument_list|)
expr_stmt|;
block|}
specifier|synchronized
name|void
name|writeJournals
parameter_list|()
throws|throws
name|IOException
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
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|putLong
argument_list|(
name|JOURNAL_MAGIC
argument_list|)
expr_stmt|;
name|buffer
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
name|buffer
operator|.
name|putInt
argument_list|(
name|name
operator|.
name|length
argument_list|)
expr_stmt|;
name|buffer
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
name|buffer
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
name|buffer
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
name|buffer
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
name|writeEntry
argument_list|(
name|JOURNALS_UUID
argument_list|,
name|buffer
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

