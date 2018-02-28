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
name|String
operator|.
name|format
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
name|FILE_NAME_FORMAT
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
name|GRAPH_MAGIC
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
name|binaries
operator|.
name|BinaryReferencesIndexWriter
operator|.
name|newBinaryReferencesIndexWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|file
operator|.
name|tar
operator|.
name|binaries
operator|.
name|BinaryReferencesIndexWriter
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
comment|/**  * A writer for tar files. It is also used to read entries while the file is  * still open.  */
end_comment

begin_class
class|class
name|TarWriter
implements|implements
name|Closeable
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
name|TarWriter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|writeIndex
decl_stmt|;
comment|/**      * Flag to indicate a closed writer. Accessing a closed writer is illegal.      * Should only be accessed from synchronized code.      */
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
comment|/**      * Map of the entries that have already been written. Used by the      * {@link #containsEntry(long, long)} and {@link #readEntry(long, long)}      * methods to retrieve data from this file while it's still being written,      * and finally by the {@link #close()} method to generate the tar index.      * The map is ordered in the order that entries have been written.      *<p>      * Should only be accessed from synchronized code.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|UUID
argument_list|,
name|TarEntry
argument_list|>
name|index
init|=
name|newLinkedHashMap
argument_list|()
decl_stmt|;
comment|/**      * List of binary references contained in this TAR file.      */
specifier|private
specifier|final
name|BinaryReferencesIndexWriter
name|binaryReferences
init|=
name|newBinaryReferencesIndexWriter
argument_list|()
decl_stmt|;
comment|/**      * Graph of references between segments.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|graph
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SegmentArchiveManager
name|archiveManager
decl_stmt|;
specifier|private
specifier|final
name|SegmentArchiveManager
operator|.
name|SegmentArchiveWriter
name|archive
decl_stmt|;
comment|/** This object is used as an additional      *  synchronization point by {@link #flush()} and {@link #close()} to      *  allow {@link #flush()} to work concurrently with normal reads and      *  writes, but not with a concurrent {@link #close()}. */
specifier|private
specifier|final
name|Object
name|closeMonitor
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|/**      * Used for maintenance operations (GC or recovery) via the TarReader and      * tests      */
name|TarWriter
parameter_list|(
name|SegmentArchiveManager
name|archiveManager
parameter_list|,
name|String
name|archiveName
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|archiveManager
operator|=
name|archiveManager
expr_stmt|;
name|this
operator|.
name|archive
operator|=
name|archiveManager
operator|.
name|create
argument_list|(
name|archiveName
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeIndex
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|TarWriter
parameter_list|(
name|SegmentArchiveManager
name|archiveManager
parameter_list|,
name|int
name|writeIndex
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|archiveManager
operator|=
name|archiveManager
expr_stmt|;
name|this
operator|.
name|archive
operator|=
name|archiveManager
operator|.
name|create
argument_list|(
name|format
argument_list|(
name|FILE_NAME_FORMAT
argument_list|,
name|writeIndex
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeIndex
operator|=
name|writeIndex
expr_stmt|;
block|}
specifier|synchronized
name|boolean
name|containsEntry
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
block|{
name|checkState
argument_list|(
operator|!
name|closed
argument_list|)
expr_stmt|;
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
comment|/**      * If the given segment is in this file, get the byte buffer that allows      * reading it.      *       * @param msb the most significant bits of the segment id      * @param lsb the least significant bits of the segment id      * @return the byte buffer, or null if not in this file      */
name|ByteBuffer
name|readEntry
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
name|TarEntry
name|entry
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|checkState
argument_list|(
operator|!
name|closed
argument_list|)
expr_stmt|;
name|entry
operator|=
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
expr_stmt|;
block|}
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
return|return
name|archive
operator|.
name|readSegment
argument_list|(
name|entry
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
name|long
name|writeEntry
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
name|GCGeneration
name|generation
parameter_list|)
throws|throws
name|IOException
block|{
name|checkNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|checkPositionIndexes
argument_list|(
name|offset
argument_list|,
name|offset
operator|+
name|size
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|checkState
argument_list|(
operator|!
name|closed
argument_list|)
expr_stmt|;
name|TarEntry
name|entry
init|=
name|archive
operator|.
name|writeSegment
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|,
name|data
argument_list|,
name|offset
argument_list|,
name|size
argument_list|,
name|generation
argument_list|)
decl_stmt|;
name|long
name|currentLength
init|=
name|archive
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|checkState
argument_list|(
name|currentLength
operator|<=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
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
name|entry
argument_list|)
expr_stmt|;
return|return
name|currentLength
return|;
block|}
block|}
name|void
name|addBinaryReference
parameter_list|(
name|GCGeneration
name|generation
parameter_list|,
name|UUID
name|segmentId
parameter_list|,
name|String
name|reference
parameter_list|)
block|{
name|binaryReferences
operator|.
name|addEntry
argument_list|(
name|generation
operator|.
name|getGeneration
argument_list|()
argument_list|,
name|generation
operator|.
name|getFullGeneration
argument_list|()
argument_list|,
name|generation
operator|.
name|isCompacted
argument_list|()
argument_list|,
name|segmentId
argument_list|,
name|reference
argument_list|)
expr_stmt|;
block|}
name|void
name|addGraphEdge
parameter_list|(
name|UUID
name|from
parameter_list|,
name|UUID
name|to
parameter_list|)
block|{
name|graph
operator|.
name|computeIfAbsent
argument_list|(
name|from
argument_list|,
name|k
lambda|->
name|newHashSet
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|to
argument_list|)
expr_stmt|;
block|}
comment|/**      * Flushes the entries that have so far been written to the disk.      * This method is<em>not</em> synchronized to allow concurrent reads      * and writes to proceed while the file is being flushed. However,      * this method<em>is</em> carefully synchronized with {@link #close()}      * to prevent accidental flushing of an already closed file.      *      * @throws IOException if the tar file could not be flushed      */
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|closeMonitor
init|)
block|{
name|boolean
name|doFlush
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|doFlush
operator|=
name|archive
operator|.
name|isCreated
argument_list|()
operator|&&
operator|!
name|closed
expr_stmt|;
block|}
if|if
condition|(
name|doFlush
condition|)
block|{
name|archive
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Closes this tar file.      *      * @throws IOException if the tar file could not be closed      */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Mark this writer as closed. Note that we only need to synchronize
comment|// this part, as no other synchronized methods should get invoked
comment|// once close() has been initiated (see related checkState calls).
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
block|}
comment|// If nothing was written to this file, then we're already done.
if|if
condition|(
operator|!
name|archive
operator|.
name|isCreated
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// Complete the tar file by adding the graph, the index and the
comment|// trailing two zero blocks. This code is synchronized on the closeMonitor
comment|// to ensure that no concurrent thread is still flushing
comment|// the file when we close the file handle.
synchronized|synchronized
init|(
name|closeMonitor
init|)
block|{
name|writeBinaryReferences
argument_list|()
expr_stmt|;
name|writeGraph
argument_list|()
expr_stmt|;
name|writeIndex
argument_list|()
expr_stmt|;
name|archive
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * If the current instance is dirty, this will return a new TarWriter based      * on the next generation of the file being written to by incrementing the      * internal {@link #writeIndex} counter. Otherwise it will return the      * current instance.      */
name|TarWriter
name|createNextGeneration
parameter_list|()
throws|throws
name|IOException
block|{
name|checkState
argument_list|(
name|writeIndex
operator|>=
literal|0
argument_list|)
expr_stmt|;
comment|// If nothing was written to this file, then we're already done.
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|!
name|archive
operator|.
name|isCreated
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
block|}
name|close
argument_list|()
expr_stmt|;
name|int
name|newIndex
init|=
name|writeIndex
operator|+
literal|1
decl_stmt|;
return|return
operator|new
name|TarWriter
argument_list|(
name|archiveManager
argument_list|,
name|newIndex
argument_list|)
return|;
block|}
specifier|private
name|void
name|writeBinaryReferences
parameter_list|()
throws|throws
name|IOException
block|{
name|archive
operator|.
name|writeBinaryReferences
argument_list|(
name|binaryReferences
operator|.
name|write
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeGraph
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|graphSize
init|=
literal|0
decl_stmt|;
comment|// The following information are stored in the footer as meta-
comment|// information about the entry.
comment|// 4 bytes to store a magic number identifying this entry as containing
comment|// references to binary values.
name|graphSize
operator|+=
literal|4
expr_stmt|;
comment|// 4 bytes to store the CRC32 checksum of the data in this entry.
name|graphSize
operator|+=
literal|4
expr_stmt|;
comment|// 4 bytes to store the length of this entry, without including the
comment|// optional padding.
name|graphSize
operator|+=
literal|4
expr_stmt|;
comment|// 4 bytes to store the number of entries in the graph map.
name|graphSize
operator|+=
literal|4
expr_stmt|;
comment|// The following information are stored as part of the main content of
comment|// this entry, after the optional padding.
for|for
control|(
name|Entry
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|entry
range|:
name|graph
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|// 16 bytes to store the key of the map.
name|graphSize
operator|+=
literal|16
expr_stmt|;
comment|// 4 bytes for the number of entries in the adjacency list.
name|graphSize
operator|+=
literal|4
expr_stmt|;
comment|// 16 bytes for every element in the adjacency list.
name|graphSize
operator|+=
literal|16
operator|*
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|graphSize
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|entry
range|:
name|graph
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|UUID
name|from
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|putLong
argument_list|(
name|from
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putLong
argument_list|(
name|from
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|UUID
argument_list|>
name|adj
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
name|adj
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|UUID
name|to
range|:
name|adj
control|)
block|{
name|buffer
operator|.
name|putLong
argument_list|(
name|to
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putLong
argument_list|(
name|to
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|buffer
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
operator|(
name|int
operator|)
name|checksum
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
name|graph
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
name|graphSize
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
name|GRAPH_MAGIC
argument_list|)
expr_stmt|;
name|archive
operator|.
name|writeGraph
argument_list|(
name|buffer
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
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
name|TarEntry
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
name|msb
argument_list|()
argument_list|,
name|entry
operator|.
name|lsb
argument_list|()
argument_list|,
name|entry
operator|.
name|offset
argument_list|()
argument_list|,
name|entry
operator|.
name|size
argument_list|()
argument_list|,
name|entry
operator|.
name|generation
argument_list|()
operator|.
name|getGeneration
argument_list|()
argument_list|,
name|entry
operator|.
name|generation
argument_list|()
operator|.
name|getFullGeneration
argument_list|()
argument_list|,
name|entry
operator|.
name|generation
argument_list|()
operator|.
name|isCompacted
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|index
init|=
name|writer
operator|.
name|write
argument_list|()
decl_stmt|;
name|archive
operator|.
name|writeIndex
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
specifier|synchronized
name|long
name|fileLength
parameter_list|()
block|{
return|return
name|archive
operator|.
name|getLength
argument_list|()
return|;
block|}
specifier|synchronized
name|String
name|getFileName
parameter_list|()
block|{
return|return
name|archive
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|synchronized
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|closed
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
name|getFileName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

