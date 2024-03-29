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
operator|.
name|file
operator|.
name|tar
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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|TarConstants
operator|.
name|BLOCK_SIZE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|channels
operator|.
name|FileChannel
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
name|LinkedHashMap
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|CRC32
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
name|Stopwatch
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
name|commons
operator|.
name|Buffer
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
name|index
operator|.
name|IndexEntry
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
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|SimpleIndexEntry
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
name|monitor
operator|.
name|FileStoreMonitor
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
name|monitor
operator|.
name|IOMonitor
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
name|SegmentArchiveWriter
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

begin_class
specifier|public
class|class
name|SegmentTarWriter
implements|implements
name|SegmentArchiveWriter
block|{
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
name|SegmentTarWriter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|ZERO_BYTES
init|=
operator|new
name|byte
index|[
name|BLOCK_SIZE
index|]
decl_stmt|;
specifier|private
specifier|final
name|FileStoreMonitor
name|monitor
decl_stmt|;
comment|/**      * The file being written. This instance is also used as an additional      * synchronization point by {@link #flush()} and {@link #close()} to      * allow {@link #flush()} to work concurrently with normal reads and      * writes, but not with a concurrent {@link #close()}.      */
specifier|private
specifier|final
name|File
name|file
decl_stmt|;
specifier|private
specifier|final
name|IOMonitor
name|ioMonitor
decl_stmt|;
comment|/**      * Map of the entries that have already been written. Used by the      * {@link #containsSegment(long, long)} and {@link #readSegment(long, long)}      * methods to retrieve data from this file while it's still being written,      * and finally by the {@link #close()} method to generate the tar index.      * The map is ordered in the order that entries have been written.      *<p>      * The MutableIndex implementation is thread-safe.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|UUID
argument_list|,
name|IndexEntry
argument_list|>
name|index
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * File handle. Initialized lazily in {@link #writeSegment(long, long, byte[], int, int, int, int, boolean)}      * to avoid creating an extra empty file when just reading from the repository.      * Should only be accessed from synchronized code.      */
specifier|private
name|RandomAccessFile
name|access
init|=
literal|null
decl_stmt|;
specifier|private
name|FileChannel
name|channel
init|=
literal|null
decl_stmt|;
specifier|private
specifier|volatile
name|long
name|length
decl_stmt|;
specifier|public
name|SegmentTarWriter
parameter_list|(
name|File
name|file
parameter_list|,
name|FileStoreMonitor
name|monitor
parameter_list|,
name|IOMonitor
name|ioMonitor
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|monitor
operator|=
name|monitor
expr_stmt|;
name|this
operator|.
name|ioMonitor
operator|=
name|ioMonitor
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeSegment
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|generation
parameter_list|,
name|int
name|fullGeneration
parameter_list|,
name|boolean
name|compacted
parameter_list|)
throws|throws
name|IOException
block|{
name|UUID
name|uuid
init|=
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
decl_stmt|;
name|CRC32
name|checksum
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
name|checksum
operator|.
name|update
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|String
name|entryName
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s.%08x"
argument_list|,
name|uuid
argument_list|,
name|checksum
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|header
init|=
name|newEntryHeader
argument_list|(
name|entryName
argument_list|,
name|size
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Writing segment {} to {}"
argument_list|,
name|uuid
argument_list|,
name|file
argument_list|)
expr_stmt|;
if|if
condition|(
name|access
operator|==
literal|null
condition|)
block|{
name|access
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|channel
operator|=
name|access
operator|.
name|getChannel
argument_list|()
expr_stmt|;
block|}
name|int
name|padding
init|=
name|getPaddingSize
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|long
name|initialLength
init|=
name|access
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|access
operator|.
name|write
argument_list|(
name|header
argument_list|)
expr_stmt|;
name|long
name|dataOffset
init|=
name|access
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|ioMonitor
operator|.
name|beforeSegmentWrite
argument_list|(
name|file
argument_list|,
name|msb
argument_list|,
name|lsb
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|Stopwatch
name|stopwatch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|access
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|ioMonitor
operator|.
name|afterSegmentWrite
argument_list|(
name|file
argument_list|,
name|msb
argument_list|,
name|lsb
argument_list|,
name|size
argument_list|,
name|stopwatch
operator|.
name|elapsed
argument_list|(
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|padding
operator|>
literal|0
condition|)
block|{
name|access
operator|.
name|write
argument_list|(
name|ZERO_BYTES
argument_list|,
literal|0
argument_list|,
name|padding
argument_list|)
expr_stmt|;
block|}
name|long
name|currentLength
init|=
name|access
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|monitor
operator|.
name|written
argument_list|(
name|currentLength
operator|-
name|initialLength
argument_list|)
expr_stmt|;
name|length
operator|=
name|currentLength
expr_stmt|;
name|index
operator|.
name|put
argument_list|(
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
argument_list|,
operator|new
name|SimpleIndexEntry
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|,
operator|(
name|int
operator|)
name|dataOffset
argument_list|,
name|size
argument_list|,
name|generation
argument_list|,
name|fullGeneration
argument_list|,
name|compacted
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Buffer
name|readSegment
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexEntry
name|indexEntry
init|=
name|index
operator|.
name|get
argument_list|(
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexEntry
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|checkState
argument_list|(
name|channel
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|// implied by entry != null
name|Buffer
name|data
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
name|indexEntry
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|readFully
argument_list|(
name|channel
argument_list|,
name|indexEntry
operator|.
name|getPosition
argument_list|()
argument_list|)
operator|<
name|indexEntry
operator|.
name|getLength
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
name|data
operator|.
name|rewind
argument_list|()
expr_stmt|;
return|return
name|data
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|containsSegment
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
block|{
return|return
name|index
operator|.
name|containsKey
argument_list|(
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeGraph
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|paddingSize
init|=
name|getPaddingSize
argument_list|(
name|data
operator|.
name|length
argument_list|)
decl_stmt|;
name|byte
index|[]
name|header
init|=
name|newEntryHeader
argument_list|(
name|file
operator|.
name|getName
argument_list|()
operator|+
literal|".gph"
argument_list|,
name|data
operator|.
name|length
operator|+
name|paddingSize
argument_list|)
decl_stmt|;
name|access
operator|.
name|write
argument_list|(
name|header
argument_list|)
expr_stmt|;
if|if
condition|(
name|paddingSize
operator|>
literal|0
condition|)
block|{
name|access
operator|.
name|write
argument_list|(
name|ZERO_BYTES
argument_list|,
literal|0
argument_list|,
name|paddingSize
argument_list|)
expr_stmt|;
block|}
name|access
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|written
argument_list|(
name|header
operator|.
name|length
operator|+
name|paddingSize
operator|+
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|length
operator|=
name|access
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeBinaryReferences
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|paddingSize
init|=
name|getPaddingSize
argument_list|(
name|data
operator|.
name|length
argument_list|)
decl_stmt|;
name|byte
index|[]
name|header
init|=
name|newEntryHeader
argument_list|(
name|file
operator|.
name|getName
argument_list|()
operator|+
literal|".brf"
argument_list|,
name|data
operator|.
name|length
operator|+
name|paddingSize
argument_list|)
decl_stmt|;
name|access
operator|.
name|write
argument_list|(
name|header
argument_list|)
expr_stmt|;
if|if
condition|(
name|paddingSize
operator|>
literal|0
condition|)
block|{
name|access
operator|.
name|write
argument_list|(
name|ZERO_BYTES
argument_list|,
literal|0
argument_list|,
name|paddingSize
argument_list|)
expr_stmt|;
block|}
name|access
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|written
argument_list|(
name|header
operator|.
name|length
operator|+
name|paddingSize
operator|+
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|length
operator|=
name|access
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getEntryCount
parameter_list|()
block|{
return|return
name|index
operator|.
name|size
argument_list|()
return|;
block|}
specifier|private
name|void
name|writeIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
init|=
name|IndexWriter
operator|.
name|newIndexWriter
argument_list|(
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
for|for
control|(
name|IndexEntry
name|entry
range|:
name|index
operator|.
name|values
argument_list|()
control|)
block|{
name|writer
operator|.
name|addEntry
argument_list|(
name|entry
operator|.
name|getMsb
argument_list|()
argument_list|,
name|entry
operator|.
name|getLsb
argument_list|()
argument_list|,
name|entry
operator|.
name|getPosition
argument_list|()
argument_list|,
name|entry
operator|.
name|getLength
argument_list|()
argument_list|,
name|entry
operator|.
name|getGeneration
argument_list|()
argument_list|,
name|entry
operator|.
name|getFullGeneration
argument_list|()
argument_list|,
name|entry
operator|.
name|isCompacted
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|data
init|=
name|writer
operator|.
name|write
argument_list|()
decl_stmt|;
name|byte
index|[]
name|header
init|=
name|newEntryHeader
argument_list|(
name|file
operator|.
name|getName
argument_list|()
operator|+
literal|".idx"
argument_list|,
name|data
operator|.
name|length
argument_list|)
decl_stmt|;
name|access
operator|.
name|write
argument_list|(
name|header
argument_list|)
expr_stmt|;
name|access
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|written
argument_list|(
name|header
operator|.
name|length
operator|+
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|length
operator|=
name|access
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|writeIndex
argument_list|()
expr_stmt|;
name|access
operator|.
name|write
argument_list|(
name|ZERO_BYTES
argument_list|)
expr_stmt|;
name|access
operator|.
name|write
argument_list|(
name|ZERO_BYTES
argument_list|)
expr_stmt|;
name|access
operator|.
name|close
argument_list|()
expr_stmt|;
name|monitor
operator|.
name|written
argument_list|(
name|BLOCK_SIZE
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCreated
parameter_list|()
block|{
return|return
name|access
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|access
operator|.
name|getFD
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|file
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|byte
index|[]
name|newEntryHeader
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|byte
index|[]
name|header
init|=
operator|new
name|byte
index|[
name|BLOCK_SIZE
index|]
decl_stmt|;
comment|// File name
name|byte
index|[]
name|nameBytes
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
name|nameBytes
argument_list|,
literal|0
argument_list|,
name|header
argument_list|,
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|nameBytes
operator|.
name|length
argument_list|,
literal|100
argument_list|)
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
name|size
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
name|byte
name|aHeader
range|:
name|header
control|)
block|{
name|checksum
operator|+=
name|aHeader
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
literal|"%06o\0 "
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
literal|8
argument_list|)
expr_stmt|;
return|return
name|header
return|;
block|}
specifier|static
name|int
name|getPaddingSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|int
name|remainder
init|=
name|size
operator|%
name|BLOCK_SIZE
decl_stmt|;
if|if
condition|(
name|remainder
operator|>
literal|0
condition|)
block|{
return|return
name|BLOCK_SIZE
operator|-
name|remainder
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
end_class

end_unit

