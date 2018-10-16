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
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
operator|.
name|wrap
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|filefilter
operator|.
name|SuffixFileFilter
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
name|Index
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
name|SegmentArchiveManager
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
name|SegmentArchiveReader
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
name|file
operator|.
name|Files
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
name|LinkedHashMap
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
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|CRC32
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentTarManager
implements|implements
name|SegmentArchiveManager
block|{
comment|/**      * Pattern of the segment entry names. Note the trailing (\\..*)? group      * that's included for compatibility with possible future extensions.      */
specifier|private
specifier|static
specifier|final
name|Pattern
name|NAME_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})"
operator|+
literal|"(\\.([0-9a-f]{8}))?(\\..*)?"
argument_list|)
decl_stmt|;
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
name|SegmentTarManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|File
name|segmentstoreDir
decl_stmt|;
specifier|private
specifier|final
name|FileStoreMonitor
name|fileStoreMonitor
decl_stmt|;
specifier|private
specifier|final
name|IOMonitor
name|ioMonitor
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|memoryMapping
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|offHeapAccess
decl_stmt|;
specifier|public
name|SegmentTarManager
parameter_list|(
name|File
name|segmentstoreDir
parameter_list|,
name|FileStoreMonitor
name|fileStoreMonitor
parameter_list|,
name|IOMonitor
name|ioMonitor
parameter_list|,
name|boolean
name|memoryMapping
parameter_list|,
name|boolean
name|offHeapAccess
parameter_list|)
block|{
name|this
operator|.
name|segmentstoreDir
operator|=
name|segmentstoreDir
expr_stmt|;
name|this
operator|.
name|fileStoreMonitor
operator|=
name|fileStoreMonitor
expr_stmt|;
name|this
operator|.
name|ioMonitor
operator|=
name|ioMonitor
expr_stmt|;
name|this
operator|.
name|memoryMapping
operator|=
name|memoryMapping
expr_stmt|;
name|this
operator|.
name|offHeapAccess
operator|=
name|offHeapAccess
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|listArchives
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|segmentstoreDir
operator|.
name|list
argument_list|(
operator|new
name|SuffixFileFilter
argument_list|(
literal|".tar"
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SegmentArchiveReader
name|open
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|segmentstoreDir
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|RandomAccessFile
name|access
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"r"
argument_list|)
decl_stmt|;
try|try
block|{
name|Index
name|index
init|=
name|SegmentTarReader
operator|.
name|loadAndValidateIndex
argument_list|(
name|access
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"No index found in tar file {}, skipping..."
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
else|else
block|{
if|if
condition|(
name|memoryMapping
condition|)
block|{
try|try
block|{
name|FileAccess
name|mapped
init|=
operator|new
name|FileAccess
operator|.
name|Mapped
argument_list|(
name|access
argument_list|)
decl_stmt|;
return|return
operator|new
name|SegmentTarReader
argument_list|(
name|file
argument_list|,
name|mapped
argument_list|,
name|index
argument_list|,
name|ioMonitor
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to mmap tar file {}. Falling back to normal file "
operator|+
literal|"IO, which will negatively impact repository performance. "
operator|+
literal|"This problem may have been caused by restrictions on the "
operator|+
literal|"amount of virtual memory available to the JVM. Please make "
operator|+
literal|"sure that a 64-bit JVM is being used and that the process "
operator|+
literal|"has access to unlimited virtual memory (ulimit option -v)."
argument_list|,
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|FileAccess
name|random
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|offHeapAccess
condition|)
block|{
name|random
operator|=
operator|new
name|FileAccess
operator|.
name|RandomOffHeap
argument_list|(
name|access
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|random
operator|=
operator|new
name|FileAccess
operator|.
name|Random
argument_list|(
name|access
argument_list|)
expr_stmt|;
block|}
comment|// prevent the finally block from closing the file
comment|// as the returned TarReader will take care of that
name|access
operator|=
literal|null
expr_stmt|;
return|return
operator|new
name|SegmentTarReader
argument_list|(
name|file
argument_list|,
name|random
argument_list|,
name|index
argument_list|,
name|ioMonitor
argument_list|)
return|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|access
operator|!=
literal|null
condition|)
block|{
name|access
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|SegmentArchiveReader
name|forceOpen
parameter_list|(
name|String
name|archiveName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|open
argument_list|(
name|archiveName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SegmentArchiveWriter
name|create
parameter_list|(
name|String
name|archiveName
parameter_list|)
block|{
return|return
operator|new
name|SegmentTarWriter
argument_list|(
operator|new
name|File
argument_list|(
name|segmentstoreDir
argument_list|,
name|archiveName
argument_list|)
argument_list|,
name|fileStoreMonitor
argument_list|,
name|ioMonitor
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|delete
parameter_list|(
name|String
name|archiveName
parameter_list|)
block|{
try|try
block|{
return|return
name|Files
operator|.
name|deleteIfExists
argument_list|(
operator|new
name|File
argument_list|(
name|segmentstoreDir
argument_list|,
name|archiveName
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Can't remove archive {}"
argument_list|,
name|archiveName
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|renameTo
parameter_list|(
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
block|{
try|try
block|{
name|Files
operator|.
name|move
argument_list|(
operator|new
name|File
argument_list|(
name|segmentstoreDir
argument_list|,
name|from
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|,
operator|new
name|File
argument_list|(
name|segmentstoreDir
argument_list|,
name|to
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Can't move archive {} to {}"
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|copyFile
parameter_list|(
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
throws|throws
name|IOException
block|{
name|Files
operator|.
name|copy
argument_list|(
operator|new
name|File
argument_list|(
name|segmentstoreDir
argument_list|,
name|from
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|,
operator|new
name|File
argument_list|(
name|segmentstoreDir
argument_list|,
name|to
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
name|String
name|archiveName
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|segmentstoreDir
argument_list|,
name|archiveName
argument_list|)
operator|.
name|exists
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|recoverEntries
parameter_list|(
name|String
name|archiveName
parameter_list|,
name|LinkedHashMap
argument_list|<
name|UUID
argument_list|,
name|byte
index|[]
argument_list|>
name|entries
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|segmentstoreDir
argument_list|,
name|archiveName
argument_list|)
decl_stmt|;
name|RandomAccessFile
name|access
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"r"
argument_list|)
decl_stmt|;
try|try
block|{
name|recoverEntries
argument_list|(
name|file
argument_list|,
name|access
argument_list|,
name|entries
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|access
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Scans through the tar file, looking for all segment entries.      *      * @param file    The path of the TAR file.      * @param access  The contents of the TAR file.      * @param entries The map that will contain the recovered entries. The      *                entries are inserted in the {@link LinkedHashMap} in the      *                order they appear in the TAR file.      */
specifier|private
specifier|static
name|void
name|recoverEntries
parameter_list|(
name|File
name|file
parameter_list|,
name|RandomAccessFile
name|access
parameter_list|,
name|LinkedHashMap
argument_list|<
name|UUID
argument_list|,
name|byte
index|[]
argument_list|>
name|entries
parameter_list|)
throws|throws
name|IOException
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
while|while
condition|(
name|access
operator|.
name|getFilePointer
argument_list|()
operator|+
name|BLOCK_SIZE
operator|<=
name|access
operator|.
name|length
argument_list|()
condition|)
block|{
comment|// read the tar header block
name|access
operator|.
name|readFully
argument_list|(
name|header
argument_list|)
expr_stmt|;
comment|// compute the header checksum
name|int
name|sum
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
name|BLOCK_SIZE
condition|;
name|i
operator|++
control|)
block|{
name|sum
operator|+=
name|header
index|[
name|i
index|]
operator|&
literal|0xff
expr_stmt|;
block|}
comment|// identify possible zero block
if|if
condition|(
name|sum
operator|==
literal|0
operator|&&
name|access
operator|.
name|getFilePointer
argument_list|()
operator|+
literal|2
operator|*
name|BLOCK_SIZE
operator|==
name|access
operator|.
name|length
argument_list|()
condition|)
block|{
return|return;
comment|// found the zero blocks at the end of the file
block|}
comment|// replace the actual stored checksum with spaces for comparison
for|for
control|(
name|int
name|i
init|=
literal|148
init|;
name|i
operator|<
literal|148
operator|+
literal|8
condition|;
name|i
operator|++
control|)
block|{
name|sum
operator|-=
name|header
index|[
name|i
index|]
operator|&
literal|0xff
expr_stmt|;
name|sum
operator|+=
literal|' '
expr_stmt|;
block|}
name|byte
index|[]
name|checkbytes
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%06o\0 "
argument_list|,
name|sum
argument_list|)
operator|.
name|getBytes
argument_list|(
name|UTF_8
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
name|checkbytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|checkbytes
index|[
name|i
index|]
operator|!=
name|header
index|[
literal|148
operator|+
name|i
index|]
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Invalid entry checksum at offset {} in tar file {}, skipping..."
argument_list|,
name|access
operator|.
name|getFilePointer
argument_list|()
operator|-
name|BLOCK_SIZE
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
block|}
comment|// The header checksum passes, so read the entry name and size
name|ByteBuffer
name|buffer
init|=
name|wrap
argument_list|(
name|header
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|readString
argument_list|(
name|buffer
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|position
argument_list|(
literal|124
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|readNumber
argument_list|(
name|buffer
argument_list|,
literal|12
argument_list|)
decl_stmt|;
if|if
condition|(
name|access
operator|.
name|getFilePointer
argument_list|()
operator|+
name|size
operator|>
name|access
operator|.
name|length
argument_list|()
condition|)
block|{
comment|// checksum was correct, so the size field should be accurate
name|log
operator|.
name|warn
argument_list|(
literal|"Partial entry {} in tar file {}, ignoring..."
argument_list|,
name|name
argument_list|,
name|file
argument_list|)
expr_stmt|;
return|return;
block|}
name|Matcher
name|matcher
init|=
name|NAME_PATTERN
operator|.
name|matcher
argument_list|(
name|name
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
name|id
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
name|String
name|checksum
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|checksum
operator|!=
literal|null
operator|||
operator|!
name|entries
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|access
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
comment|// skip possible padding to stay at block boundaries
name|long
name|position
init|=
name|access
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|remainder
init|=
name|position
operator|%
name|BLOCK_SIZE
decl_stmt|;
if|if
condition|(
name|remainder
operator|!=
literal|0
condition|)
block|{
name|access
operator|.
name|seek
argument_list|(
name|position
operator|+
operator|(
name|BLOCK_SIZE
operator|-
name|remainder
operator|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|checksum
operator|!=
literal|null
condition|)
block|{
name|CRC32
name|crc
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
name|crc
operator|.
name|update
argument_list|(
name|data
argument_list|)
expr_stmt|;
if|if
condition|(
name|crc
operator|.
name|getValue
argument_list|()
operator|!=
name|Long
operator|.
name|parseLong
argument_list|(
name|checksum
argument_list|,
literal|16
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Checksum mismatch in entry {} of tar file {}, skipping..."
argument_list|,
name|name
argument_list|,
name|file
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
name|entries
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
name|file
operator|.
name|getName
argument_list|()
operator|+
literal|".idx"
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unexpected entry {} in tar file {}, skipping..."
argument_list|,
name|name
argument_list|,
name|file
argument_list|)
expr_stmt|;
name|long
name|position
init|=
name|access
operator|.
name|getFilePointer
argument_list|()
operator|+
name|size
decl_stmt|;
name|long
name|remainder
init|=
name|position
operator|%
name|BLOCK_SIZE
decl_stmt|;
if|if
condition|(
name|remainder
operator|!=
literal|0
condition|)
block|{
name|position
operator|+=
name|BLOCK_SIZE
operator|-
name|remainder
expr_stmt|;
block|}
name|access
operator|.
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|String
name|readString
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|,
name|int
name|fieldSize
parameter_list|)
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|fieldSize
index|]
decl_stmt|;
name|buffer
operator|.
name|get
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|n
operator|<
name|fieldSize
operator|&&
name|b
index|[
name|n
index|]
operator|!=
literal|0
condition|)
block|{
name|n
operator|++
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|n
argument_list|,
name|UTF_8
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|int
name|readNumber
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|,
name|int
name|fieldSize
parameter_list|)
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|fieldSize
index|]
decl_stmt|;
name|buffer
operator|.
name|get
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|int
name|number
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
name|fieldSize
condition|;
name|i
operator|++
control|)
block|{
name|int
name|digit
init|=
name|b
index|[
name|i
index|]
operator|&
literal|0xff
decl_stmt|;
if|if
condition|(
literal|'0'
operator|<=
name|digit
operator|&&
name|digit
operator|<=
literal|'7'
condition|)
block|{
name|number
operator|=
name|number
operator|*
literal|8
operator|+
name|digit
operator|-
literal|'0'
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
return|return
name|number
return|;
block|}
block|}
end_class

end_unit

