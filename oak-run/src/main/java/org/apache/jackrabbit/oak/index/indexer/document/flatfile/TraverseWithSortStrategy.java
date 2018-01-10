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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|flatfile
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|lang
operator|.
name|management
operator|.
name|MemoryNotificationInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|MemoryPoolMXBean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|MemoryUsage
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|Comparator
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|Notification
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|NotificationEmitter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|NotificationListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
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
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|sort
operator|.
name|ExternalSort
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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|NodeStateEntry
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
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
operator|.
name|getMemoryMXBean
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
operator|.
name|getMemoryPoolMXBeans
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|MemoryType
operator|.
name|HEAP
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
name|ONE_GB
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
name|commons
operator|.
name|IOUtils
operator|.
name|humanReadableByteCount
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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|flatfile
operator|.
name|FlatFileNodeStoreBuilder
operator|.
name|OAK_INDEXER_MAX_SORT_MEMORY_IN_GB
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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|flatfile
operator|.
name|FlatFileNodeStoreBuilder
operator|.
name|OAK_INDEXER_MAX_SORT_MEMORY_IN_GB_DEFAULT
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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|flatfile
operator|.
name|FlatFileStoreUtils
operator|.
name|createWriter
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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|flatfile
operator|.
name|FlatFileStoreUtils
operator|.
name|getSortedStoreFileName
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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|flatfile
operator|.
name|FlatFileStoreUtils
operator|.
name|sizeOf
import|;
end_import

begin_class
class|class
name|TraverseWithSortStrategy
implements|implements
name|SortStrategy
block|{
specifier|private
specifier|static
specifier|final
name|String
name|OAK_INDEXER_MIN_MEMORY
init|=
literal|"oak.indexer.minMemoryForWork"
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|sufficientMemory
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Iterable
argument_list|<
name|NodeStateEntry
argument_list|>
name|nodeStates
decl_stmt|;
specifier|private
specifier|final
name|NodeStateEntryWriter
name|entryWriter
decl_stmt|;
specifier|private
specifier|final
name|File
name|storeDir
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|compressionEnabled
decl_stmt|;
specifier|private
specifier|final
name|Charset
name|charset
init|=
name|UTF_8
decl_stmt|;
specifier|private
specifier|final
name|Comparator
argument_list|<
name|NodeStateHolder
argument_list|>
name|comparator
decl_stmt|;
specifier|private
name|NotificationEmitter
name|emitter
decl_stmt|;
specifier|private
name|MemoryListener
name|listener
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxMemory
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
name|OAK_INDEXER_MAX_SORT_MEMORY_IN_GB
argument_list|,
name|OAK_INDEXER_MAX_SORT_MEMORY_IN_GB_DEFAULT
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|long
name|minMemory
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
name|OAK_INDEXER_MIN_MEMORY
argument_list|,
literal|2
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|long
name|maxMemoryBytes
init|=
name|maxMemory
operator|*
name|ONE_GB
decl_stmt|;
specifier|private
specifier|final
name|long
name|minMemoryBytes
init|=
name|minMemory
operator|*
name|ONE_GB
decl_stmt|;
specifier|private
name|boolean
name|useMaxMemory
decl_stmt|;
specifier|private
name|long
name|entryCount
decl_stmt|;
specifier|private
name|long
name|memoryUsed
decl_stmt|;
specifier|private
name|File
name|sortWorkDir
decl_stmt|;
specifier|private
name|List
argument_list|<
name|File
argument_list|>
name|sortedFiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|ArrayList
argument_list|<
name|NodeStateHolder
argument_list|>
name|entryBatch
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|TraverseWithSortStrategy
parameter_list|(
name|Iterable
argument_list|<
name|NodeStateEntry
argument_list|>
name|nodeStates
parameter_list|,
name|PathElementComparator
name|pathComparator
parameter_list|,
name|NodeStateEntryWriter
name|entryWriter
parameter_list|,
name|File
name|storeDir
parameter_list|,
name|boolean
name|compressionEnabled
parameter_list|)
block|{
name|this
operator|.
name|nodeStates
operator|=
name|nodeStates
expr_stmt|;
name|this
operator|.
name|entryWriter
operator|=
name|entryWriter
expr_stmt|;
name|this
operator|.
name|storeDir
operator|=
name|storeDir
expr_stmt|;
name|this
operator|.
name|compressionEnabled
operator|=
name|compressionEnabled
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
parameter_list|(
name|e1
parameter_list|,
name|e2
parameter_list|)
lambda|->
name|pathComparator
operator|.
name|compare
argument_list|(
name|e1
operator|.
name|getPathElements
argument_list|()
argument_list|,
name|e2
operator|.
name|getPathElements
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|File
name|createSortedStoreFile
parameter_list|()
throws|throws
name|IOException
block|{
name|logFlags
argument_list|()
expr_stmt|;
name|configureMemoryListener
argument_list|()
expr_stmt|;
name|sortWorkDir
operator|=
name|createdSortWorkDir
argument_list|(
name|storeDir
argument_list|)
expr_stmt|;
name|writeToSortedFiles
argument_list|()
expr_stmt|;
return|return
name|sortStoreFile
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getEntryCount
parameter_list|()
block|{
return|return
name|entryCount
return|;
block|}
specifier|private
name|File
name|sortStoreFile
parameter_list|()
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Proceeding to perform merge of {} sorted files"
argument_list|,
name|sortedFiles
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Stopwatch
name|w
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|File
name|sortedFile
init|=
operator|new
name|File
argument_list|(
name|storeDir
argument_list|,
name|getSortedStoreFileName
argument_list|(
name|compressionEnabled
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
name|BufferedWriter
name|writer
init|=
name|createWriter
argument_list|(
name|sortedFile
argument_list|,
name|compressionEnabled
argument_list|)
init|)
block|{
name|Function
argument_list|<
name|String
argument_list|,
name|NodeStateHolder
argument_list|>
name|func1
init|=
operator|(
name|line
operator|)
operator|->
name|line
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|SimpleNodeStateHolder
argument_list|(
name|line
argument_list|)
decl_stmt|;
name|Function
argument_list|<
name|NodeStateHolder
argument_list|,
name|String
argument_list|>
name|func2
init|=
name|holder
lambda|->
name|holder
operator|==
literal|null
condition|?
literal|null
else|:
name|holder
operator|.
name|getLine
argument_list|()
decl_stmt|;
name|ExternalSort
operator|.
name|mergeSortedFiles
argument_list|(
name|sortedFiles
argument_list|,
name|writer
argument_list|,
name|comparator
argument_list|,
name|charset
argument_list|,
literal|true
argument_list|,
comment|//distinct
name|compressionEnabled
argument_list|,
comment|//useZip
name|func2
argument_list|,
name|func1
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Merging of sorted files completed in {}"
argument_list|,
name|w
argument_list|)
expr_stmt|;
return|return
name|sortedFile
return|;
block|}
specifier|private
name|void
name|writeToSortedFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|Stopwatch
name|w
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeStateEntry
name|e
range|:
name|nodeStates
control|)
block|{
name|entryCount
operator|++
expr_stmt|;
name|addEntry
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
comment|//Save the last batch
name|sortAndSaveBatch
argument_list|()
expr_stmt|;
comment|//Free up the batch
name|entryBatch
operator|.
name|clear
argument_list|()
expr_stmt|;
name|entryBatch
operator|.
name|trimToSize
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Dumped {} nodestates in json format in {}"
argument_list|,
name|entryCount
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created {} sorted files of size {} to merge"
argument_list|,
name|sortedFiles
operator|.
name|size
argument_list|()
argument_list|,
name|humanReadableByteCount
argument_list|(
name|sizeOf
argument_list|(
name|sortedFiles
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addEntry
parameter_list|(
name|NodeStateEntry
name|e
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isMemoryLow
argument_list|()
condition|)
block|{
name|sortAndSaveBatch
argument_list|()
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
name|String
name|jsonText
init|=
name|entryWriter
operator|.
name|asJson
argument_list|(
name|e
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
comment|//Here logic differs from NodeStateEntrySorter in sense that
comment|//Holder line consist only of json and not 'path|json'
name|NodeStateHolder
name|h
init|=
operator|new
name|StateInBytesHolder
argument_list|(
name|e
operator|.
name|getPath
argument_list|()
argument_list|,
name|jsonText
argument_list|)
decl_stmt|;
name|entryBatch
operator|.
name|add
argument_list|(
name|h
argument_list|)
expr_stmt|;
name|updateMemoryUsed
argument_list|(
name|h
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|reset
parameter_list|()
block|{
name|entryBatch
operator|.
name|clear
argument_list|()
expr_stmt|;
name|memoryUsed
operator|=
literal|0
expr_stmt|;
name|sufficientMemory
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sortAndSaveBatch
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|entryBatch
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|entryBatch
operator|.
name|sort
argument_list|(
name|comparator
argument_list|)
expr_stmt|;
name|Stopwatch
name|w
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|File
name|newtmpfile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"sortInBatch"
argument_list|,
literal|"flatfile"
argument_list|,
name|sortWorkDir
argument_list|)
decl_stmt|;
name|long
name|textSize
init|=
literal|0
decl_stmt|;
try|try
init|(
name|BufferedWriter
name|writer
init|=
name|FlatFileStoreUtils
operator|.
name|createWriter
argument_list|(
name|newtmpfile
argument_list|,
name|compressionEnabled
argument_list|)
init|)
block|{
for|for
control|(
name|NodeStateHolder
name|h
range|:
name|entryBatch
control|)
block|{
comment|//Here holder line only contains nodeState json
name|String
name|text
init|=
name|entryWriter
operator|.
name|toString
argument_list|(
name|h
operator|.
name|getPathElements
argument_list|()
argument_list|,
name|h
operator|.
name|getLine
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|writer
operator|.
name|newLine
argument_list|()
expr_stmt|;
name|textSize
operator|+=
name|text
operator|.
name|length
argument_list|()
operator|+
literal|1
expr_stmt|;
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Sorted and stored batch of size {} (uncompressed {}) with {} entries in {}"
argument_list|,
name|humanReadableByteCount
argument_list|(
name|newtmpfile
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|humanReadableByteCount
argument_list|(
name|textSize
argument_list|)
argument_list|,
name|entryBatch
operator|.
name|size
argument_list|()
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|sortedFiles
operator|.
name|add
argument_list|(
name|newtmpfile
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|isMemoryLow
parameter_list|()
block|{
if|if
condition|(
name|useMaxMemory
condition|)
block|{
return|return
name|memoryUsed
operator|>
name|maxMemoryBytes
return|;
block|}
return|return
operator|!
name|sufficientMemory
operator|.
name|get
argument_list|()
return|;
block|}
specifier|private
name|void
name|updateMemoryUsed
parameter_list|(
name|NodeStateHolder
name|h
parameter_list|)
block|{
name|memoryUsed
operator|+=
name|h
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|File
name|createdSortWorkDir
parameter_list|(
name|File
name|storeDir
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|sortedFileDir
init|=
operator|new
name|File
argument_list|(
name|storeDir
argument_list|,
literal|"sort-work-dir"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|forceMkdir
argument_list|(
name|sortedFileDir
argument_list|)
expr_stmt|;
return|return
name|sortedFileDir
return|;
block|}
specifier|private
name|void
name|logFlags
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Min heap memory (GB) to be required : {} ({})"
argument_list|,
name|minMemory
argument_list|,
name|OAK_INDEXER_MIN_MEMORY
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Max heap memory (GB) to be used for merge sort : {} ({})"
argument_list|,
name|maxMemory
argument_list|,
name|OAK_INDEXER_MAX_SORT_MEMORY_IN_GB
argument_list|)
expr_stmt|;
block|}
comment|//~-------------------------------------< memory management>
specifier|private
name|void
name|configureMemoryListener
parameter_list|()
block|{
name|MemoryPoolMXBean
name|pool
init|=
name|getMemoryPool
argument_list|()
decl_stmt|;
if|if
condition|(
name|pool
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to setup monitoring of available memory. "
operator|+
literal|"Would use configured maxMemory limit of {} GB"
argument_list|,
name|maxMemory
argument_list|)
expr_stmt|;
name|useMaxMemory
operator|=
literal|true
expr_stmt|;
return|return;
block|}
name|emitter
operator|=
operator|(
name|NotificationEmitter
operator|)
name|getMemoryMXBean
argument_list|()
expr_stmt|;
name|listener
operator|=
operator|new
name|MemoryListener
argument_list|()
expr_stmt|;
name|emitter
operator|.
name|addNotificationListener
argument_list|(
name|listener
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|MemoryUsage
name|usage
init|=
name|pool
operator|.
name|getCollectionUsage
argument_list|()
decl_stmt|;
name|long
name|maxMemory
init|=
name|usage
operator|.
name|getMax
argument_list|()
decl_stmt|;
name|long
name|warningThreshold
init|=
name|minMemory
operator|*
name|ONE_GB
decl_stmt|;
if|if
condition|(
name|warningThreshold
operator|>
name|maxMemory
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Configured minimum memory {} GB more than available memory ({})."
operator|+
literal|"Overriding configuration accordingly."
argument_list|,
name|minMemory
argument_list|,
name|humanReadableByteCount
argument_list|(
name|maxMemory
argument_list|)
argument_list|)
expr_stmt|;
name|warningThreshold
operator|=
name|maxMemory
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Setting up a listener to monitor pool '{}' and trigger batch save "
operator|+
literal|"if memory drop below {} GB (max {})"
argument_list|,
name|pool
operator|.
name|getName
argument_list|()
argument_list|,
name|minMemory
argument_list|,
name|humanReadableByteCount
argument_list|(
name|maxMemory
argument_list|)
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setCollectionUsageThreshold
argument_list|(
name|warningThreshold
argument_list|)
expr_stmt|;
name|checkMemory
argument_list|(
name|usage
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkMemory
parameter_list|(
name|MemoryUsage
name|usage
parameter_list|)
block|{
name|long
name|maxMemory
init|=
name|usage
operator|.
name|getMax
argument_list|()
decl_stmt|;
name|long
name|usedMemory
init|=
name|usage
operator|.
name|getUsed
argument_list|()
decl_stmt|;
name|long
name|avail
init|=
name|maxMemory
operator|-
name|usedMemory
decl_stmt|;
if|if
condition|(
name|avail
operator|>
name|minMemoryBytes
condition|)
block|{
name|sufficientMemory
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Available memory level {} is good. Current batch size {}"
argument_list|,
name|humanReadableByteCount
argument_list|(
name|avail
argument_list|)
argument_list|,
name|entryBatch
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sufficientMemory
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Available memory level {} (required {}) is low. Enabling flag to trigger batch save"
argument_list|,
name|humanReadableByteCount
argument_list|(
name|avail
argument_list|)
argument_list|,
name|minMemory
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Taken from GCMemoryBarrier
specifier|private
class|class
name|MemoryListener
implements|implements
name|NotificationListener
block|{
annotation|@
name|Override
specifier|public
name|void
name|handleNotification
parameter_list|(
name|Notification
name|notification
parameter_list|,
name|Object
name|handback
parameter_list|)
block|{
if|if
condition|(
name|notification
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|MemoryNotificationInfo
operator|.
name|MEMORY_COLLECTION_THRESHOLD_EXCEEDED
argument_list|)
condition|)
block|{
if|if
condition|(
name|sufficientMemory
operator|.
name|get
argument_list|()
condition|)
block|{
name|CompositeData
name|cd
init|=
operator|(
name|CompositeData
operator|)
name|notification
operator|.
name|getUserData
argument_list|()
decl_stmt|;
name|MemoryNotificationInfo
name|info
init|=
name|MemoryNotificationInfo
operator|.
name|from
argument_list|(
name|cd
argument_list|)
decl_stmt|;
name|checkMemory
argument_list|(
name|info
operator|.
name|getUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
specifier|static
name|MemoryPoolMXBean
name|getMemoryPool
parameter_list|()
block|{
name|long
name|maxSize
init|=
literal|0
decl_stmt|;
name|MemoryPoolMXBean
name|maxPool
init|=
literal|null
decl_stmt|;
for|for
control|(
name|MemoryPoolMXBean
name|pool
range|:
name|getMemoryPoolMXBeans
argument_list|()
control|)
block|{
if|if
condition|(
name|HEAP
operator|==
name|pool
operator|.
name|getType
argument_list|()
operator|&&
name|pool
operator|.
name|isCollectionUsageThresholdSupported
argument_list|()
condition|)
block|{
comment|// Get usage after a GC, which is more stable, if available
name|long
name|poolSize
init|=
name|pool
operator|.
name|getCollectionUsage
argument_list|()
operator|.
name|getMax
argument_list|()
decl_stmt|;
comment|// Keep the pool with biggest size, by default it should be Old Gen Space
if|if
condition|(
name|poolSize
operator|>
name|maxSize
condition|)
block|{
name|maxPool
operator|=
name|pool
expr_stmt|;
block|}
block|}
block|}
return|return
name|maxPool
return|;
block|}
block|}
end_class

end_unit
