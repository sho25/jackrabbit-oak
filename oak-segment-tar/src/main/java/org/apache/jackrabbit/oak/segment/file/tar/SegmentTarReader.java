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
name|SegmentTarWriter
operator|.
name|getPaddingSize
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
name|index
operator|.
name|IndexLoader
operator|.
name|newIndexLoader
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
name|List
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
name|binaries
operator|.
name|BinaryReferencesIndexLoader
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
name|InvalidBinaryReferencesIndexException
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
name|IndexLoader
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
name|InvalidIndexException
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
name|SegmentArchiveEntry
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
name|util
operator|.
name|ReaderAtEnd
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
name|SegmentTarReader
implements|implements
name|SegmentArchiveReader
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
name|SegmentTarReader
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|IndexLoader
name|indexLoader
init|=
name|newIndexLoader
argument_list|(
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|FileAccess
name|access
decl_stmt|;
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
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|Index
name|index
decl_stmt|;
specifier|private
specifier|volatile
name|Boolean
name|hasGraph
decl_stmt|;
specifier|public
name|SegmentTarReader
parameter_list|(
name|File
name|file
parameter_list|,
name|FileAccess
name|access
parameter_list|,
name|Index
name|index
parameter_list|,
name|IOMonitor
name|ioMonitor
parameter_list|)
block|{
name|this
operator|.
name|access
operator|=
name|access
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|file
operator|.
name|getName
argument_list|()
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
name|int
name|i
init|=
name|index
operator|.
name|findEntry
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
name|IndexEntry
name|indexEntry
init|=
name|index
operator|.
name|entry
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|ioMonitor
operator|.
name|beforeSegmentRead
argument_list|(
name|file
argument_list|,
name|msb
argument_list|,
name|lsb
argument_list|,
name|indexEntry
operator|.
name|getLength
argument_list|()
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
name|Buffer
name|buffer
init|=
name|access
operator|.
name|read
argument_list|(
name|indexEntry
operator|.
name|getPosition
argument_list|()
argument_list|,
name|indexEntry
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|elapsed
init|=
name|stopwatch
operator|.
name|elapsed
argument_list|(
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|ioMonitor
operator|.
name|afterSegmentRead
argument_list|(
name|file
argument_list|,
name|msb
argument_list|,
name|lsb
argument_list|,
name|indexEntry
operator|.
name|getLength
argument_list|()
argument_list|,
name|elapsed
argument_list|)
expr_stmt|;
return|return
name|buffer
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
name|findEntry
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
operator|!=
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|SegmentArchiveEntry
argument_list|>
name|listSegments
parameter_list|()
block|{
name|IndexEntry
index|[]
name|entries
init|=
operator|new
name|IndexEntry
index|[
name|index
operator|.
name|count
argument_list|()
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
name|index
operator|.
name|count
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|entries
index|[
name|i
index|]
operator|=
name|index
operator|.
name|entry
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|entries
argument_list|,
name|IndexEntry
operator|.
name|POSITION_ORDER
argument_list|)
expr_stmt|;
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|entries
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Index
name|loadAndValidateIndex
parameter_list|(
name|RandomAccessFile
name|file
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|length
init|=
name|file
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|%
name|BLOCK_SIZE
operator|!=
literal|0
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to load index of file {}: Invalid alignment"
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|length
operator|<
literal|6
operator|*
name|BLOCK_SIZE
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to load index of file {}: File too short"
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|length
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to load index of file {}: File too long"
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|ReaderAtEnd
name|r
init|=
parameter_list|(
name|whence
parameter_list|,
name|size
parameter_list|)
lambda|->
block|{
name|Buffer
name|buffer
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|file
operator|.
name|seek
argument_list|(
name|length
operator|-
literal|2
operator|*
name|BLOCK_SIZE
operator|-
name|whence
argument_list|)
expr_stmt|;
name|file
operator|.
name|readFully
argument_list|(
name|buffer
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|buffer
return|;
block|}
decl_stmt|;
try|try
block|{
return|return
name|indexLoader
operator|.
name|loadIndex
argument_list|(
name|r
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidIndexException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to load index of file {}: {}"
argument_list|,
name|name
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Buffer
name|getGraph
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|end
init|=
name|access
operator|.
name|length
argument_list|()
operator|-
literal|2
operator|*
name|BLOCK_SIZE
operator|-
name|getIndexEntrySize
argument_list|()
decl_stmt|;
name|Buffer
name|graph
init|=
name|GraphLoader
operator|.
name|loadGraph
argument_list|(
parameter_list|(
name|whence
parameter_list|,
name|amount
parameter_list|)
lambda|->
name|access
operator|.
name|read
argument_list|(
name|end
operator|-
name|whence
argument_list|,
name|amount
argument_list|)
argument_list|)
decl_stmt|;
name|hasGraph
operator|=
name|graph
operator|!=
literal|null
expr_stmt|;
return|return
name|graph
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasGraph
parameter_list|()
block|{
if|if
condition|(
name|hasGraph
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|getGraph
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{ }
block|}
return|return
name|hasGraph
return|;
block|}
annotation|@
name|Override
specifier|public
name|Buffer
name|getBinaryReferences
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|int
name|end
init|=
name|access
operator|.
name|length
argument_list|()
operator|-
literal|2
operator|*
name|BLOCK_SIZE
operator|-
name|getIndexEntrySize
argument_list|()
operator|-
name|getGraphEntrySize
argument_list|()
decl_stmt|;
return|return
name|BinaryReferencesIndexLoader
operator|.
name|loadBinaryReferencesIndex
argument_list|(
parameter_list|(
name|whence
parameter_list|,
name|amount
parameter_list|)
lambda|->
name|access
operator|.
name|read
argument_list|(
name|end
operator|-
name|whence
argument_list|,
name|amount
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidBinaryReferencesIndexException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|file
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
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
name|access
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getEntrySize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
return|return
name|BLOCK_SIZE
operator|+
name|size
operator|+
name|getPaddingSize
argument_list|(
name|size
argument_list|)
return|;
block|}
specifier|private
name|int
name|getIndexEntrySize
parameter_list|()
block|{
return|return
name|getEntrySize
argument_list|(
name|index
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|int
name|getGraphEntrySize
parameter_list|()
block|{
name|Buffer
name|buffer
decl_stmt|;
try|try
block|{
name|buffer
operator|=
name|getGraph
argument_list|()
expr_stmt|;
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
literal|"Exception while loading pre-compiled tar graph"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|getEntrySize
argument_list|(
name|buffer
operator|.
name|getInt
argument_list|(
name|buffer
operator|.
name|limit
argument_list|()
operator|-
literal|8
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

