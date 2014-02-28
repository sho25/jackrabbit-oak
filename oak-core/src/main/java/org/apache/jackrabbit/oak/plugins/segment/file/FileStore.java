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
name|Lists
operator|.
name|newCopyOnWriteArrayList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|SECONDS
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
name|SegmentIdFactory
operator|.
name|isBulkSegmentId
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
name|concurrent
operator|.
name|CountDownLatch
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
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|api
operator|.
name|Blob
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
name|FileStore
extends|extends
name|AbstractStore
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
name|FileStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_MEMORY_CACHE_SIZE
init|=
literal|256
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FILE_NAME_FORMAT
init|=
literal|"%s%05d.tar"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JOURNAL_FILE_NAME
init|=
literal|"journal.log"
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
name|List
argument_list|<
name|TarFile
argument_list|>
name|bulkFiles
init|=
name|newCopyOnWriteArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|TarFile
argument_list|>
name|dataFiles
init|=
name|newCopyOnWriteArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|RandomAccessFile
name|journalFile
decl_stmt|;
comment|/**      * The latest head of the root journal.      */
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|RecordId
argument_list|>
name|head
decl_stmt|;
comment|/**      * The persisted head of the root journal, used to determine whether the      * latest {@link #head} value should be written to the disk.      */
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|RecordId
argument_list|>
name|persistedHead
decl_stmt|;
comment|/**      * The background flush thread. Automatically flushes the TarMK state      * once every five seconds.      */
specifier|private
specifier|final
name|Thread
name|flushThread
decl_stmt|;
comment|/**      * Synchronization aid used by the background flush thread to stop itself      * as soon as the {@link #close()} method is called.      */
specifier|private
specifier|final
name|CountDownLatch
name|timeToClose
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|public
name|FileStore
parameter_list|(
name|File
name|directory
parameter_list|,
name|int
name|maxFileSizeMB
parameter_list|,
name|boolean
name|memoryMapping
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|directory
argument_list|,
name|EMPTY_NODE
argument_list|,
name|maxFileSizeMB
argument_list|,
name|DEFAULT_MEMORY_CACHE_SIZE
argument_list|,
name|memoryMapping
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FileStore
parameter_list|(
name|File
name|directory
parameter_list|,
name|int
name|maxFileSizeMB
parameter_list|,
name|int
name|cacheSizeMB
parameter_list|,
name|boolean
name|memoryMapping
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|directory
argument_list|,
name|EMPTY_NODE
argument_list|,
name|maxFileSizeMB
argument_list|,
name|cacheSizeMB
argument_list|,
name|memoryMapping
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FileStore
parameter_list|(
specifier|final
name|File
name|directory
parameter_list|,
name|NodeState
name|initial
parameter_list|,
name|int
name|maxFileSizeMB
parameter_list|,
name|int
name|cacheSizeMB
parameter_list|,
name|boolean
name|memoryMapping
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|cacheSizeMB
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
name|maxFileSizeMB
operator|*
name|MB
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
literal|"bulk"
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
name|bulkFiles
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
literal|"data"
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
name|dataFiles
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
name|journalFile
operator|=
operator|new
name|RandomAccessFile
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|JOURNAL_FILE_NAME
argument_list|)
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|RecordId
name|id
init|=
literal|null
decl_stmt|;
name|String
name|line
init|=
name|journalFile
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|int
name|space
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
decl_stmt|;
if|if
condition|(
name|space
operator|!=
operator|-
literal|1
condition|)
block|{
name|id
operator|=
name|RecordId
operator|.
name|fromString
argument_list|(
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|space
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|line
operator|=
name|journalFile
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|head
operator|=
operator|new
name|AtomicReference
argument_list|<
name|RecordId
argument_list|>
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|persistedHead
operator|=
operator|new
name|AtomicReference
argument_list|<
name|RecordId
argument_list|>
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
else|else
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
name|initial
argument_list|)
expr_stmt|;
name|head
operator|=
operator|new
name|AtomicReference
argument_list|<
name|RecordId
argument_list|>
argument_list|(
name|getWriter
argument_list|()
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|persistedHead
operator|=
operator|new
name|AtomicReference
argument_list|<
name|RecordId
argument_list|>
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|flushThread
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|timeToClose
operator|.
name|await
argument_list|(
literal|1
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
while|while
condition|(
name|timeToClose
operator|.
name|getCount
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|flush
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
literal|"Failed to flush the TarMK at"
operator|+
name|directory
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|timeToClose
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"TarMK flush thread interrupted"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|flushThread
operator|.
name|setName
argument_list|(
literal|"TarMK flush thread: "
operator|+
name|directory
argument_list|)
expr_stmt|;
name|flushThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|flushThread
operator|.
name|setPriority
argument_list|(
name|Thread
operator|.
name|MIN_PRIORITY
argument_list|)
expr_stmt|;
name|flushThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|persistedHead
init|)
block|{
name|RecordId
name|before
init|=
name|persistedHead
operator|.
name|get
argument_list|()
decl_stmt|;
name|RecordId
name|after
init|=
name|head
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|after
operator|.
name|equals
argument_list|(
name|before
argument_list|)
condition|)
block|{
comment|// needs to happen outside the synchronization block below to
comment|// avoid a deadlock with another thread flushing the writer
name|getWriter
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
for|for
control|(
name|TarFile
name|file
range|:
name|bulkFiles
control|)
block|{
name|file
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|TarFile
name|file
range|:
name|dataFiles
control|)
block|{
name|file
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|journalFile
operator|.
name|writeBytes
argument_list|(
name|after
operator|+
literal|" root\n"
argument_list|)
expr_stmt|;
name|journalFile
operator|.
name|getChannel
argument_list|()
operator|.
name|force
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|persistedHead
operator|.
name|set
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|Segment
name|segment
range|:
name|segments
operator|.
name|asMap
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|Segment
index|[
literal|0
index|]
argument_list|)
control|)
block|{
name|segment
operator|.
name|dropOldCacheEntries
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Iterable
argument_list|<
name|UUID
argument_list|>
name|getSegmentIds
parameter_list|()
block|{
name|List
argument_list|<
name|UUID
argument_list|>
name|ids
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|TarFile
name|file
range|:
name|dataFiles
control|)
block|{
name|ids
operator|.
name|addAll
argument_list|(
name|file
operator|.
name|getUUIDs
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|TarFile
name|file
range|:
name|bulkFiles
control|)
block|{
name|ids
operator|.
name|addAll
argument_list|(
name|file
operator|.
name|getUUIDs
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ids
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
comment|// avoid deadlocks while joining the flush thread
name|timeToClose
operator|.
name|countDown
argument_list|()
expr_stmt|;
try|try
block|{
name|flushThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"Interrupted while joining the TarMK flush thread"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|journalFile
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|TarFile
name|file
range|:
name|bulkFiles
control|)
block|{
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|bulkFiles
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|TarFile
name|file
range|:
name|dataFiles
control|)
block|{
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dataFiles
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
literal|"Failed to close the TarMK at "
operator|+
name|directory
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Journal
name|getJournal
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkArgument
argument_list|(
literal|"root"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
comment|// only root supported for now
return|return
operator|new
name|Journal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RecordId
name|getHead
parameter_list|()
block|{
return|return
name|head
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|setHead
parameter_list|(
name|RecordId
name|before
parameter_list|,
name|RecordId
name|after
parameter_list|)
block|{
name|RecordId
name|id
init|=
name|head
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|id
operator|.
name|equals
argument_list|(
name|before
argument_list|)
operator|&&
name|head
operator|.
name|compareAndSet
argument_list|(
name|id
argument_list|,
name|after
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|merge
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|protected
name|Segment
name|loadSegment
parameter_list|(
name|UUID
name|id
parameter_list|)
block|{
name|List
argument_list|<
name|TarFile
argument_list|>
name|files
init|=
name|dataFiles
decl_stmt|;
if|if
condition|(
name|isBulkSegmentId
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|files
operator|=
name|bulkFiles
expr_stmt|;
block|}
for|for
control|(
name|TarFile
name|file
range|:
name|files
control|)
block|{
try|try
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
name|createSegment
argument_list|(
name|id
argument_list|,
name|buffer
argument_list|)
return|;
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
literal|"Failed to access file "
operator|+
name|file
argument_list|,
name|e
argument_list|)
throw|;
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
comment|// select whether to write a data or a bulk segment
name|List
argument_list|<
name|TarFile
argument_list|>
name|files
init|=
name|dataFiles
decl_stmt|;
name|String
name|base
init|=
literal|"data"
decl_stmt|;
if|if
condition|(
name|isBulkSegmentId
argument_list|(
name|segmentId
argument_list|)
condition|)
block|{
name|files
operator|=
name|bulkFiles
expr_stmt|;
name|base
operator|=
literal|"bulk"
expr_stmt|;
block|}
try|try
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
name|get
argument_list|(
name|files
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
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
name|base
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
name|data
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
annotation|@
name|Override
specifier|public
name|Blob
name|readBlob
parameter_list|(
name|String
name|reference
parameter_list|)
block|{
return|return
operator|new
name|FileBlob
argument_list|(
name|reference
argument_list|)
return|;
comment|// FIXME: proper reference lookup
block|}
block|}
end_class

end_unit

