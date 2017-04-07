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
name|newArrayListWithCapacity
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
operator|.
name|listFiles
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
name|ArrayList
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
name|HashMap
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
name|locks
operator|.
name|ReadWriteLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
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
name|Supplier
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
name|Closer
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
name|blob
operator|.
name|ReferenceCollector
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
name|SegmentGraph
operator|.
name|SegmentGraphVisitor
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
class|class
name|TarFiles
implements|implements
name|Closeable
block|{
specifier|static
class|class
name|CleanupResult
block|{
specifier|private
name|boolean
name|interrupted
decl_stmt|;
specifier|private
name|long
name|reclaimedSize
decl_stmt|;
specifier|private
name|List
argument_list|<
name|File
argument_list|>
name|removableFiles
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|UUID
argument_list|>
name|reclaimedSegmentIds
decl_stmt|;
specifier|private
name|CleanupResult
parameter_list|()
block|{
comment|// Prevent external instantiation.
block|}
name|long
name|getReclaimedSize
parameter_list|()
block|{
return|return
name|reclaimedSize
return|;
block|}
name|List
argument_list|<
name|File
argument_list|>
name|getRemovableFiles
parameter_list|()
block|{
return|return
name|removableFiles
return|;
block|}
name|Set
argument_list|<
name|UUID
argument_list|>
name|getReclaimedSegmentIds
parameter_list|()
block|{
return|return
name|reclaimedSegmentIds
return|;
block|}
name|boolean
name|isInterrupted
parameter_list|()
block|{
return|return
name|interrupted
return|;
block|}
block|}
specifier|static
class|class
name|Builder
block|{
specifier|private
name|File
name|directory
decl_stmt|;
specifier|private
name|boolean
name|memoryMapping
decl_stmt|;
specifier|private
name|TarRecovery
name|tarRecovery
decl_stmt|;
specifier|private
name|IOMonitor
name|ioMonitor
decl_stmt|;
specifier|private
name|FileStoreStats
name|fileStoreStats
decl_stmt|;
specifier|private
name|long
name|maxFileSize
decl_stmt|;
specifier|private
name|boolean
name|readOnly
decl_stmt|;
name|Builder
name|withDirectory
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|checkNotNull
argument_list|(
name|directory
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
name|Builder
name|withMemoryMapping
parameter_list|(
name|boolean
name|memoryMapping
parameter_list|)
block|{
name|this
operator|.
name|memoryMapping
operator|=
name|memoryMapping
expr_stmt|;
return|return
name|this
return|;
block|}
name|Builder
name|withTarRecovery
parameter_list|(
name|TarRecovery
name|tarRecovery
parameter_list|)
block|{
name|this
operator|.
name|tarRecovery
operator|=
name|checkNotNull
argument_list|(
name|tarRecovery
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
name|Builder
name|withIOMonitor
parameter_list|(
name|IOMonitor
name|ioMonitor
parameter_list|)
block|{
name|this
operator|.
name|ioMonitor
operator|=
name|checkNotNull
argument_list|(
name|ioMonitor
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
name|Builder
name|withFileStoreStats
parameter_list|(
name|FileStoreStats
name|fileStoreStats
parameter_list|)
block|{
name|this
operator|.
name|fileStoreStats
operator|=
name|checkNotNull
argument_list|(
name|fileStoreStats
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
name|Builder
name|withMaxFileSize
parameter_list|(
name|long
name|maxFileSize
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|maxFileSize
operator|>
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxFileSize
operator|=
name|maxFileSize
expr_stmt|;
return|return
name|this
return|;
block|}
name|Builder
name|withReadOnly
parameter_list|()
block|{
name|this
operator|.
name|readOnly
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|TarFiles
name|build
parameter_list|()
throws|throws
name|IOException
block|{
name|checkState
argument_list|(
name|directory
operator|!=
literal|null
argument_list|,
literal|"Directory not specified"
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|tarRecovery
operator|!=
literal|null
argument_list|,
literal|"TAR recovery strategy not specified"
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|ioMonitor
operator|!=
literal|null
argument_list|,
literal|"I/O monitor not specified"
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|readOnly
operator|||
name|fileStoreStats
operator|!=
literal|null
argument_list|,
literal|"File store statistics not specified"
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|readOnly
operator|||
name|maxFileSize
operator|!=
literal|0
argument_list|,
literal|"Max file size not specified"
argument_list|)
expr_stmt|;
return|return
operator|new
name|TarFiles
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
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
name|TarFiles
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|FILE_NAME_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(data)((0|[1-9][0-9]*)[0-9]{4})([a-z])?.tar"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|Map
argument_list|<
name|Integer
argument_list|,
name|Map
argument_list|<
name|Character
argument_list|,
name|File
argument_list|>
argument_list|>
name|collectFiles
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|Map
argument_list|<
name|Character
argument_list|,
name|File
argument_list|>
argument_list|>
name|dataFiles
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|listFiles
argument_list|(
name|directory
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
control|)
block|{
name|Matcher
name|matcher
init|=
name|FILE_NAME_PATTERN
operator|.
name|matcher
argument_list|(
name|file
operator|.
name|getName
argument_list|()
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
name|Integer
name|index
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Character
argument_list|,
name|File
argument_list|>
name|files
init|=
name|dataFiles
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|files
operator|==
literal|null
condition|)
block|{
name|files
operator|=
name|newHashMap
argument_list|()
expr_stmt|;
name|dataFiles
operator|.
name|put
argument_list|(
name|index
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
name|Character
name|generation
init|=
literal|'a'
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|group
argument_list|(
literal|4
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|generation
operator|=
name|matcher
operator|.
name|group
argument_list|(
literal|4
argument_list|)
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|checkState
argument_list|(
name|files
operator|.
name|put
argument_list|(
name|generation
argument_list|,
name|file
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|dataFiles
return|;
block|}
comment|/**      * Include the ids of all segments transitively reachable through forward      * references from {@code referencedIds}. See OAK-3864.      */
specifier|private
specifier|static
name|void
name|includeForwardReferences
parameter_list|(
name|Iterable
argument_list|<
name|TarReader
argument_list|>
name|readers
parameter_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
name|referencedIds
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|UUID
argument_list|>
name|fRefs
init|=
name|newHashSet
argument_list|(
name|referencedIds
argument_list|)
decl_stmt|;
do|do
block|{
comment|// Add direct forward references
for|for
control|(
name|TarReader
name|reader
range|:
name|readers
control|)
block|{
name|reader
operator|.
name|calculateForwardReferences
argument_list|(
name|fRefs
argument_list|)
expr_stmt|;
if|if
condition|(
name|fRefs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
break|break;
comment|// Optimisation: bail out if no references left
block|}
block|}
comment|// ... as long as new forward references are found.
block|}
do|while
condition|(
name|referencedIds
operator|.
name|addAll
argument_list|(
name|fRefs
argument_list|)
condition|)
do|;
block|}
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
specifier|private
specifier|final
name|ReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|long
name|maxFileSize
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|memoryMapping
decl_stmt|;
specifier|private
specifier|final
name|IOMonitor
name|ioMonitor
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|readOnly
decl_stmt|;
specifier|private
name|List
argument_list|<
name|TarReader
argument_list|>
name|readers
decl_stmt|;
specifier|private
name|TarWriter
name|writer
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|shutdown
decl_stmt|;
specifier|private
name|boolean
name|closed
decl_stmt|;
specifier|private
name|TarFiles
parameter_list|(
name|Builder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|maxFileSize
operator|=
name|builder
operator|.
name|maxFileSize
expr_stmt|;
name|memoryMapping
operator|=
name|builder
operator|.
name|memoryMapping
expr_stmt|;
name|ioMonitor
operator|=
name|builder
operator|.
name|ioMonitor
expr_stmt|;
name|readOnly
operator|=
name|builder
operator|.
name|readOnly
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Map
argument_list|<
name|Character
argument_list|,
name|File
argument_list|>
argument_list|>
name|map
init|=
name|collectFiles
argument_list|(
name|builder
operator|.
name|directory
argument_list|)
decl_stmt|;
name|readers
operator|=
name|newArrayListWithCapacity
argument_list|(
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Integer
index|[]
name|indices
init|=
name|map
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|Integer
index|[
name|map
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|indices
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|indices
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
name|readOnly
condition|)
block|{
name|readers
operator|.
name|add
argument_list|(
name|TarReader
operator|.
name|openRO
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|indices
index|[
name|i
index|]
argument_list|)
argument_list|,
name|memoryMapping
argument_list|,
literal|true
argument_list|,
name|builder
operator|.
name|tarRecovery
argument_list|,
name|ioMonitor
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|readers
operator|.
name|add
argument_list|(
name|TarReader
operator|.
name|open
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|indices
index|[
name|i
index|]
argument_list|)
argument_list|,
name|memoryMapping
argument_list|,
name|builder
operator|.
name|tarRecovery
argument_list|,
name|ioMonitor
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|readOnly
condition|)
block|{
name|int
name|writeNumber
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|indices
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|writeNumber
operator|=
name|indices
index|[
name|indices
operator|.
name|length
operator|-
literal|1
index|]
operator|+
literal|1
expr_stmt|;
block|}
name|writer
operator|=
operator|new
name|TarWriter
argument_list|(
name|builder
operator|.
name|directory
argument_list|,
name|builder
operator|.
name|fileStoreStats
argument_list|,
name|writeNumber
argument_list|,
name|builder
operator|.
name|ioMonitor
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkOpen
parameter_list|()
block|{
name|checkState
argument_list|(
operator|!
name|closed
argument_list|,
literal|"This instance has been closed"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkReadWrite
parameter_list|()
block|{
name|checkState
argument_list|(
operator|!
name|readOnly
argument_list|,
literal|"This instance is read-only"
argument_list|)
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
name|shutdown
operator|=
literal|true
expr_stmt|;
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
name|Closer
name|closer
init|=
name|Closer
operator|.
name|create
argument_list|()
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|TarReader
name|reader
range|:
name|readers
control|)
block|{
name|closer
operator|.
name|register
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
name|readers
operator|=
literal|null
expr_stmt|;
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
literal|"TarFiles{readers="
operator|+
name|readers
operator|+
literal|", writer="
operator|+
name|writer
operator|+
literal|"}"
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
name|long
name|size
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
name|long
name|size
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|readOnly
condition|)
block|{
name|size
operator|=
name|writer
operator|.
name|fileLength
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|TarReader
name|reader
range|:
name|readers
control|)
block|{
name|size
operator|+=
name|reader
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
name|int
name|readerCount
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
return|return
name|readers
operator|.
name|size
argument_list|()
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|checkReadWrite
argument_list|()
expr_stmt|;
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
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
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|readOnly
condition|)
block|{
if|if
condition|(
name|writer
operator|.
name|containsEntry
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
for|for
control|(
name|TarReader
name|reader
range|:
name|readers
control|)
block|{
if|if
condition|(
name|reader
operator|.
name|containsEntry
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
name|ByteBuffer
name|readSegment
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|readOnly
condition|)
block|{
name|ByteBuffer
name|buffer
init|=
name|writer
operator|.
name|readEntry
argument_list|(
name|msb
argument_list|,
name|lsb
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
name|buffer
return|;
block|}
block|}
for|for
control|(
name|TarReader
name|reader
range|:
name|readers
control|)
block|{
name|ByteBuffer
name|buffer
init|=
name|reader
operator|.
name|readEntry
argument_list|(
name|msb
argument_list|,
name|lsb
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
name|buffer
return|;
block|}
block|}
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
literal|"Unable to read from TAR file {}"
argument_list|,
name|writer
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
name|void
name|writeSegment
parameter_list|(
name|UUID
name|id
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
parameter_list|,
name|int
name|generation
parameter_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
name|references
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|binaryReferences
parameter_list|)
throws|throws
name|IOException
block|{
name|checkReadWrite
argument_list|()
expr_stmt|;
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
name|long
name|size
init|=
name|writer
operator|.
name|writeEntry
argument_list|(
name|id
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|,
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|generation
argument_list|)
decl_stmt|;
if|if
condition|(
name|references
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|UUID
name|reference
range|:
name|references
control|)
block|{
name|writer
operator|.
name|addGraphEdge
argument_list|(
name|id
argument_list|,
name|reference
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|binaryReferences
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|reference
range|:
name|binaryReferences
control|)
block|{
name|writer
operator|.
name|addBinaryReference
argument_list|(
name|generation
argument_list|,
name|id
argument_list|,
name|reference
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|size
operator|>=
name|maxFileSize
condition|)
block|{
name|newWriter
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|newWriter
parameter_list|()
throws|throws
name|IOException
block|{
name|TarWriter
name|newWriter
init|=
name|writer
operator|.
name|createNextGeneration
argument_list|()
decl_stmt|;
if|if
condition|(
name|newWriter
operator|==
name|writer
condition|)
block|{
return|return;
block|}
name|File
name|writeFile
init|=
name|writer
operator|.
name|getFile
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|TarReader
argument_list|>
name|list
init|=
name|newArrayListWithCapacity
argument_list|(
literal|1
operator|+
name|readers
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|TarReader
operator|.
name|open
argument_list|(
name|writeFile
argument_list|,
name|memoryMapping
argument_list|,
name|ioMonitor
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addAll
argument_list|(
name|readers
argument_list|)
expr_stmt|;
name|readers
operator|=
name|list
expr_stmt|;
name|writer
operator|=
name|newWriter
expr_stmt|;
block|}
name|CleanupResult
name|cleanup
parameter_list|(
name|Supplier
argument_list|<
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|referencesSupplier
parameter_list|,
name|Predicate
argument_list|<
name|Integer
argument_list|>
name|reclaimGeneration
parameter_list|)
throws|throws
name|IOException
block|{
name|checkReadWrite
argument_list|()
expr_stmt|;
name|CleanupResult
name|result
init|=
operator|new
name|CleanupResult
argument_list|()
decl_stmt|;
name|result
operator|.
name|removableFiles
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|result
operator|.
name|reclaimedSegmentIds
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|TarReader
argument_list|,
name|TarReader
argument_list|>
name|cleaned
init|=
name|newLinkedHashMap
argument_list|()
decl_stmt|;
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
name|newWriter
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
comment|// At this point the write lock is downgraded to a read lock for
comment|// better concurrency. It is always necessary to access TarReader
comment|// and TarWriter instances while holding a lock (either in read or
comment|// write mode) to prevent a concurrent #close(). In this case, we
comment|// don't need an exclusive access to the TarReader instances.
comment|// TODO now that the two protected sections have been merged thanks
comment|// to lock downgrading, check if the following code can be
comment|// simplified.
for|for
control|(
name|TarReader
name|reader
range|:
name|readers
control|)
block|{
name|cleaned
operator|.
name|put
argument_list|(
name|reader
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|result
operator|.
name|reclaimedSize
operator|+=
name|reader
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
comment|// The set of references has to be computed while holding the lock.
comment|// This prevents a time-of-check to time-of-use race condition. See
comment|// OAK-6046 for further details.
name|Set
argument_list|<
name|UUID
argument_list|>
name|references
init|=
name|referencesSupplier
operator|.
name|get
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|UUID
argument_list|>
name|reclaim
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|TarReader
name|reader
range|:
name|cleaned
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|shutdown
condition|)
block|{
name|result
operator|.
name|interrupted
operator|=
literal|true
expr_stmt|;
return|return
name|result
return|;
block|}
name|reader
operator|.
name|mark
argument_list|(
name|references
argument_list|,
name|reclaim
argument_list|,
name|reclaimGeneration
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"{}: size of bulk references/reclaim set {}/{}"
argument_list|,
name|reader
argument_list|,
name|references
operator|.
name|size
argument_list|()
argument_list|,
name|reclaim
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|TarReader
name|reader
range|:
name|cleaned
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|shutdown
condition|)
block|{
name|result
operator|.
name|interrupted
operator|=
literal|true
expr_stmt|;
return|return
name|result
return|;
block|}
name|cleaned
operator|.
name|put
argument_list|(
name|reader
argument_list|,
name|reader
operator|.
name|sweep
argument_list|(
name|reclaim
argument_list|,
name|result
operator|.
name|reclaimedSegmentIds
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|TarReader
argument_list|>
name|oldReaders
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// Replace current list of reader with the cleaned readers taking care not to lose
comment|// any new reader that might have come in through concurrent calls to newWriter()
name|checkOpen
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|TarReader
argument_list|>
name|sweptReaders
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|TarReader
name|reader
range|:
name|readers
control|)
block|{
if|if
condition|(
name|cleaned
operator|.
name|containsKey
argument_list|(
name|reader
argument_list|)
condition|)
block|{
name|TarReader
name|newReader
init|=
name|cleaned
operator|.
name|get
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|newReader
operator|!=
literal|null
condition|)
block|{
name|sweptReaders
operator|.
name|add
argument_list|(
name|newReader
argument_list|)
expr_stmt|;
name|result
operator|.
name|reclaimedSize
operator|-=
name|newReader
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
comment|// if these two differ, the former represents the swept version of the latter
if|if
condition|(
name|newReader
operator|!=
name|reader
condition|)
block|{
name|oldReaders
operator|.
name|add
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|sweptReaders
operator|.
name|add
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
name|readers
operator|=
name|sweptReaders
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|TarReader
name|oldReader
range|:
name|oldReaders
control|)
block|{
try|try
block|{
name|oldReader
operator|.
name|close
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
name|error
argument_list|(
literal|"Unable to close swept TAR reader"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|removableFiles
operator|.
name|add
argument_list|(
name|oldReader
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
name|void
name|collectBlobReferences
parameter_list|(
name|ReferenceCollector
name|collector
parameter_list|,
name|int
name|minGeneration
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|readOnly
condition|)
block|{
name|newWriter
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
comment|// At this point the write lock is downgraded to a read lock for
comment|// better concurrency. It is always necessary to access TarReader
comment|// and TarWriter instances while holding a lock (either in read or
comment|// write mode) to prevent a concurrent #close(). In this case, we
comment|// don't need an exclusive access to the TarReader instances.
for|for
control|(
name|TarReader
name|reader
range|:
name|readers
control|)
block|{
name|reader
operator|.
name|collectBlobReferences
argument_list|(
name|collector
argument_list|,
name|minGeneration
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
name|Iterable
argument_list|<
name|UUID
argument_list|>
name|getSegmentIds
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|UUID
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|TarReader
name|reader
range|:
name|readers
control|)
block|{
name|ids
operator|.
name|addAll
argument_list|(
name|reader
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
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|UUID
argument_list|,
name|List
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|getGraph
parameter_list|(
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|UUID
argument_list|>
name|index
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|UUID
argument_list|,
name|List
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|graph
init|=
literal|null
decl_stmt|;
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
for|for
control|(
name|TarReader
name|reader
range|:
name|readers
control|)
block|{
if|if
condition|(
name|fileName
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getFile
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|index
operator|=
name|reader
operator|.
name|getUUIDs
argument_list|()
expr_stmt|;
name|graph
operator|=
name|reader
operator|.
name|getGraph
argument_list|(
literal|false
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|Map
argument_list|<
name|UUID
argument_list|,
name|List
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|UUID
name|uuid
range|:
name|index
control|)
block|{
name|result
operator|.
name|put
argument_list|(
name|uuid
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|graph
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|putAll
argument_list|(
name|graph
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|getIndices
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|index
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|TarReader
name|reader
range|:
name|readers
control|)
block|{
name|index
operator|.
name|put
argument_list|(
name|reader
operator|.
name|getFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|reader
operator|.
name|getUUIDs
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|index
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
name|void
name|traverseSegmentGraph
parameter_list|(
name|Set
argument_list|<
name|UUID
argument_list|>
name|roots
parameter_list|,
name|SegmentGraphVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
name|includeForwardReferences
argument_list|(
name|readers
argument_list|,
name|roots
argument_list|)
expr_stmt|;
for|for
control|(
name|TarReader
name|reader
range|:
name|readers
control|)
block|{
name|reader
operator|.
name|traverseSegmentGraph
argument_list|(
name|roots
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

