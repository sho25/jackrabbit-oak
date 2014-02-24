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
name|blob
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
name|FileWriter
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
name|util
operator|.
name|ArrayDeque
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
name|NoSuchElementException
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
name|TreeSet
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
name|ConcurrentLinkedQueue
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
name|ExecutorService
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
name|LinkedBlockingQueue
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
name|ThreadFactory
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
name|ThreadPoolExecutor
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|base
operator|.
name|Joiner
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
name|Preconditions
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
name|StandardSystemProperty
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
name|Strings
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
name|Lists
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
name|Sets
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
name|Files
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
name|commons
operator|.
name|io
operator|.
name|LineIterator
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
name|blob
operator|.
name|GarbageCollectableBlobStore
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
name|IOUtils
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
name|document
operator|.
name|DocumentNodeStore
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
name|NodeStore
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
comment|/**  * Mark and sweep garbage collector.  *   * Uses the file system to store internal state while in process to account for huge data.  *   */
end_comment

begin_class
specifier|public
class|class
name|MarkSweepGarbageCollector
implements|implements
name|BlobGarbageCollector
block|{
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MarkSweepGarbageCollector
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NEWLINE
init|=
name|StandardSystemProperty
operator|.
name|LINE_SEPARATOR
operator|.
name|value
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEMP_DIR
init|=
name|StandardSystemProperty
operator|.
name|JAVA_IO_TMPDIR
operator|.
name|value
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_BATCH_COUNT
init|=
literal|2048
decl_stmt|;
comment|/** The max last modified time of blobs to consider for garbage collection. */
specifier|private
name|long
name|maxLastModifiedTime
decl_stmt|;
comment|/** Run concurrently when possible. */
specifier|private
name|boolean
name|runConcurrently
init|=
literal|true
decl_stmt|;
comment|/** The number of sweeper threads to use. */
specifier|private
name|int
name|numSweepers
init|=
literal|1
decl_stmt|;
comment|/** The node store. */
specifier|private
name|DocumentNodeStore
name|nodeStore
decl_stmt|;
comment|/** The garbage collector file state */
specifier|private
name|GarbageCollectorFileState
name|fs
decl_stmt|;
comment|/** The configured root to store gc process files. */
specifier|private
name|String
name|root
init|=
name|TEMP_DIR
decl_stmt|;
comment|/** The batch count. */
specifier|private
name|int
name|batchCount
init|=
name|DEFAULT_BATCH_COUNT
decl_stmt|;
comment|/**      * Gets the max last modified time considered for garbage collection.      *       * @return the max last modified time      */
specifier|protected
name|long
name|getMaxLastModifiedTime
parameter_list|()
block|{
return|return
name|maxLastModifiedTime
return|;
block|}
comment|/**      * Sets the max last modified time considered for garbage collection.      *       * @param maxLastModifiedTime the new max last modified time      */
specifier|protected
name|void
name|setMaxLastModifiedTime
parameter_list|(
name|long
name|maxLastModifiedTime
parameter_list|)
block|{
name|this
operator|.
name|maxLastModifiedTime
operator|=
name|maxLastModifiedTime
expr_stmt|;
block|}
comment|/**      * Gets the root.      *       * @return the root      */
specifier|protected
name|String
name|getRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
comment|/**      * Gets the batch count.      *       * @return the batch count      */
specifier|protected
name|int
name|getBatchCount
parameter_list|()
block|{
return|return
name|batchCount
return|;
block|}
comment|/**      * Checks if run concurrently.      *       * @return true, if is run concurrently      */
specifier|protected
name|boolean
name|isRunConcurrently
parameter_list|()
block|{
return|return
name|runConcurrently
return|;
block|}
comment|/**      * Gets the number sweepers.      *       * @return the number sweepers      */
specifier|protected
name|int
name|getNumSweepers
parameter_list|()
block|{
return|return
name|numSweepers
return|;
block|}
comment|/**      * @param nodeStore the node store      * @param root the root      * @param batchCount the batch count      * @param runBackendConcurrently - run the backend iterate concurrently      * @param maxSweeperThreads the max sweeper threads      * @param maxLastModifiedTime the max last modified time      * @throws IOException Signals that an I/O exception has occurred.      */
specifier|public
name|void
name|init
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|String
name|root
parameter_list|,
name|int
name|batchCount
parameter_list|,
name|boolean
name|runBackendConcurrently
parameter_list|,
name|int
name|maxSweeperThreads
parameter_list|,
name|long
name|maxLastModifiedTime
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|batchCount
operator|=
name|batchCount
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|runConcurrently
operator|=
name|runBackendConcurrently
expr_stmt|;
name|this
operator|.
name|numSweepers
operator|=
name|maxSweeperThreads
expr_stmt|;
name|this
operator|.
name|maxLastModifiedTime
operator|=
name|maxLastModifiedTime
expr_stmt|;
name|init
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
block|}
comment|/**      * Instantiates a new blob garbage collector.      *       * @param nodeStore      *            the node store      * @throws IOException      *             Signals that an I/O exception has occurred.      */
specifier|public
name|void
name|init
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeStore
operator|=
operator|(
name|DocumentNodeStore
operator|)
name|nodeStore
expr_stmt|;
name|fs
operator|=
operator|new
name|GarbageCollectorFileState
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collectGarbage
parameter_list|()
throws|throws
name|Exception
block|{
name|markAndSweep
argument_list|()
expr_stmt|;
block|}
comment|/**      * Mark and sweep. Main method for GC.      *       * @throws Exception      *             the exception      */
specifier|protected
name|void
name|markAndSweep
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Starting garbage collector"
argument_list|)
expr_stmt|;
name|mark
argument_list|()
expr_stmt|;
name|difference
argument_list|()
expr_stmt|;
name|sweep
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"garbage collector finished"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fs
operator|.
name|complete
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Mark phase of the GC.      *       * @throws Exception      *             the exception      */
specifier|protected
name|void
name|mark
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Starting mark phase of the garbage collector"
argument_list|)
expr_stmt|;
comment|// Find all blobs available in the blob store
name|Thread
name|blobIdRetrieverThread
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|runConcurrently
condition|)
block|{
name|blobIdRetrieverThread
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|BlobIdRetriever
argument_list|()
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"-MarkThread"
argument_list|)
expr_stmt|;
name|blobIdRetrieverThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|blobIdRetrieverThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
else|else
block|{
operator|(
operator|new
name|BlobIdRetriever
argument_list|()
operator|)
operator|.
name|retrieve
argument_list|()
expr_stmt|;
block|}
comment|// Find all blob references after iterating over the whole repository
name|iterateNodeTree
argument_list|()
expr_stmt|;
if|if
condition|(
name|runConcurrently
condition|)
block|{
if|if
condition|(
name|blobIdRetrieverThread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|blobIdRetrieverThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Ending mark phase of the garbage collector"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Difference phase where the GC candidates are identified.      *       * @throws IOException      *             Signals that an I/O exception has occurred.      */
specifier|protected
name|void
name|difference
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Starting difference phase of the garbage collector"
argument_list|)
expr_stmt|;
name|FileLineDifferenceIterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
operator|new
name|FileLineDifferenceIterator
argument_list|<
name|String
argument_list|>
argument_list|(
name|fs
operator|.
name|getMarkedRefs
argument_list|()
argument_list|,
name|fs
operator|.
name|getAvailableRefs
argument_list|()
argument_list|)
decl_stmt|;
name|BufferedWriter
name|bufferWriter
init|=
literal|null
decl_stmt|;
try|try
block|{
name|bufferWriter
operator|=
name|Files
operator|.
name|newWriter
argument_list|(
name|fs
operator|.
name|getGcCandidates
argument_list|()
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expiredSet
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|int
name|numCandidates
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|expiredSet
operator|.
name|add
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|expiredSet
operator|.
name|size
argument_list|()
operator|>
name|getBatchCount
argument_list|()
condition|)
block|{
name|numCandidates
operator|+=
name|expiredSet
operator|.
name|size
argument_list|()
expr_stmt|;
name|saveBatchToFile
argument_list|(
name|expiredSet
argument_list|,
name|bufferWriter
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|expiredSet
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|numCandidates
operator|+=
name|expiredSet
operator|.
name|size
argument_list|()
expr_stmt|;
name|saveBatchToFile
argument_list|(
name|expiredSet
argument_list|,
name|bufferWriter
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found GC candidates - "
operator|+
name|numCandidates
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|bufferWriter
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Ending difference phase of the garbage collector"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sweep phase of gc candidate deletion.      *       * @throws IOException      *             Signals that an I/O exception has occurred.      */
specifier|protected
name|void
name|sweep
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Starting sweep phase of the garbage collector"
argument_list|)
expr_stmt|;
name|ConcurrentLinkedQueue
argument_list|<
name|String
argument_list|>
name|exceptionQueue
init|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|ExecutorService
name|executorService
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|getNumSweepers
argument_list|()
argument_list|,
name|getNumSweepers
argument_list|()
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|private
specifier|final
name|AtomicInteger
name|threadCounter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"MarkSweepGarbageCollector-Sweeper-"
operator|+
name|threadCounter
operator|.
name|getAndIncrement
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|r
argument_list|,
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|LineIterator
name|iterator
init|=
name|FileUtils
operator|.
name|lineIterator
argument_list|(
name|fs
operator|.
name|getGcCandidates
argument_list|()
argument_list|,
name|Charsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ids
operator|.
name|size
argument_list|()
operator|>
name|getBatchCount
argument_list|()
condition|)
block|{
name|count
operator|+=
name|ids
operator|.
name|size
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|execute
argument_list|(
operator|new
name|Sweeper
argument_list|(
name|ids
argument_list|,
name|exceptionQueue
argument_list|)
argument_list|)
expr_stmt|;
name|ids
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|ids
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|count
operator|+=
name|ids
operator|.
name|size
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|execute
argument_list|(
operator|new
name|Sweeper
argument_list|(
name|ids
argument_list|,
name|exceptionQueue
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|count
operator|-=
name|exceptionQueue
operator|.
name|size
argument_list|()
expr_stmt|;
name|BufferedWriter
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|exceptionQueue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|writer
operator|=
name|Files
operator|.
name|newWriter
argument_list|(
name|fs
operator|.
name|getGarbage
argument_list|()
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|saveBatchToFile
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|exceptionQueue
argument_list|)
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|LineIterator
operator|.
name|closeQuietly
argument_list|(
name|iterator
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Blobs deleted count - "
operator|+
name|count
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Ending sweep phase of the garbage collector"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Save batch to file.      *       * @param ids      *            the ids      * @param writer      *            the writer      * @throws IOException      *             Signals that an I/O exception has occurred.      */
specifier|static
name|void
name|saveBatchToFile
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|,
name|BufferedWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|append
argument_list|(
name|Joiner
operator|.
name|on
argument_list|(
name|NEWLINE
argument_list|)
operator|.
name|join
argument_list|(
name|ids
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
name|NEWLINE
argument_list|)
expr_stmt|;
name|ids
operator|.
name|clear
argument_list|()
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sweeper thread.      */
class|class
name|Sweeper
implements|implements
name|Runnable
block|{
comment|/** The exception queue. */
specifier|private
name|ConcurrentLinkedQueue
argument_list|<
name|String
argument_list|>
name|exceptionQueue
decl_stmt|;
comment|/** The ids to sweep. */
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|ids
decl_stmt|;
comment|/**          * Instantiates a new sweeper.          *           * @param ids          *            the ids          * @param exceptionQueue          *            the exception queue          */
specifier|public
name|Sweeper
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|,
name|ConcurrentLinkedQueue
argument_list|<
name|String
argument_list|>
name|exceptionQueue
parameter_list|)
block|{
name|this
operator|.
name|exceptionQueue
operator|=
name|exceptionQueue
expr_stmt|;
name|this
operator|.
name|ids
operator|=
name|ids
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
try|try
block|{
name|boolean
name|deleted
init|=
operator|(
operator|(
name|GarbageCollectableBlobStore
operator|)
name|nodeStore
operator|.
name|getBlobStore
argument_list|()
operator|)
operator|.
name|deleteChunk
argument_list|(
name|id
argument_list|,
name|maxLastModifiedTime
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|deleted
condition|)
block|{
name|exceptionQueue
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|exceptionQueue
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Iterates the complete node tree.      *       * @return the list      * @throws Exception      *             the exception      */
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|iterateNodeTree
parameter_list|()
throws|throws
name|Exception
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|referencedBlobs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|BufferedWriter
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|writer
operator|=
name|Files
operator|.
name|newWriter
argument_list|(
name|fs
operator|.
name|getMarkedRefs
argument_list|()
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|fs
operator|.
name|sort
argument_list|(
name|fs
operator|.
name|getMarkedRefs
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Blob
argument_list|>
name|blobIterator
init|=
name|nodeStore
operator|.
name|getReferencedBlobsIterator
argument_list|()
decl_stmt|;
name|referencedBlobs
operator|.
name|ensureCapacity
argument_list|(
name|getBatchCount
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|referencesFound
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|blobIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Blob
name|blob
init|=
name|blobIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|blob
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|idIter
init|=
operator|(
operator|(
name|GarbageCollectableBlobStore
operator|)
name|nodeStore
operator|.
name|getBlobStore
argument_list|()
operator|)
operator|.
name|resolveChunks
argument_list|(
name|blob
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|idIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|referencedBlobs
operator|.
name|add
argument_list|(
name|idIter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|referencedBlobs
operator|.
name|size
argument_list|()
operator|>=
name|getBatchCount
argument_list|()
condition|)
block|{
name|referencesFound
operator|+=
name|referencedBlobs
operator|.
name|size
argument_list|()
expr_stmt|;
name|saveBatchToFile
argument_list|(
name|referencedBlobs
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|referencedBlobs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|referencesFound
operator|+=
name|referencedBlobs
operator|.
name|size
argument_list|()
expr_stmt|;
name|saveBatchToFile
argument_list|(
name|referencedBlobs
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
name|fs
operator|.
name|sort
argument_list|(
name|fs
operator|.
name|getMarkedRefs
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Blob references found (including chunk resolution) "
operator|+
name|referencesFound
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
return|return
name|referencedBlobs
return|;
block|}
comment|/**      * BlobIdRetriever class to retrieve all blob ids.      */
class|class
name|BlobIdRetriever
implements|implements
name|Runnable
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|retrieve
argument_list|()
expr_stmt|;
block|}
comment|/**          * Retrieve.          */
specifier|protected
name|void
name|retrieve
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Starting retrieve of all blobs"
argument_list|)
expr_stmt|;
name|BufferedWriter
name|bufferWriter
init|=
literal|null
decl_stmt|;
try|try
block|{
name|bufferWriter
operator|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|fs
operator|.
name|getAvailableRefs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|idsIter
init|=
operator|(
operator|(
name|GarbageCollectableBlobStore
operator|)
name|nodeStore
operator|.
name|getBlobStore
argument_list|()
operator|)
operator|.
name|getAllChunkIds
argument_list|(
name|maxLastModifiedTime
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|int
name|blobsCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|idsIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|idsIter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ids
operator|.
name|size
argument_list|()
operator|>
name|getBatchCount
argument_list|()
condition|)
block|{
name|blobsCount
operator|+=
name|ids
operator|.
name|size
argument_list|()
expr_stmt|;
name|saveBatchToFile
argument_list|(
name|ids
argument_list|,
name|bufferWriter
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|ids
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|blobsCount
operator|+=
name|ids
operator|.
name|size
argument_list|()
expr_stmt|;
name|saveBatchToFile
argument_list|(
name|ids
argument_list|,
name|bufferWriter
argument_list|)
expr_stmt|;
block|}
comment|// sort the file
name|fs
operator|.
name|sort
argument_list|(
name|fs
operator|.
name|getAvailableRefs
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Ending retrieve of all blobs : "
operator|+
name|blobsCount
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|bufferWriter
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * FileLineDifferenceIterator class which iterates over the difference of 2 files line by line.      *       * @param<T>      *            the generic type      */
class|class
name|FileLineDifferenceIterator
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Iterator
argument_list|<
name|String
argument_list|>
block|{
comment|/** The marked references iterator. */
specifier|private
name|LineIterator
name|markedIter
decl_stmt|;
comment|/** The available references iter. */
specifier|private
name|LineIterator
name|allIter
decl_stmt|;
specifier|private
name|ArrayDeque
argument_list|<
name|String
argument_list|>
name|queue
decl_stmt|;
specifier|private
name|boolean
name|done
decl_stmt|;
comment|/** Temporary buffer. */
specifier|private
name|TreeSet
argument_list|<
name|String
argument_list|>
name|markedBuffer
decl_stmt|;
comment|/**          * Instantiates a new file line difference iterator.          *           * @param marked          *            the marked          * @param available          *            the available          * @throws IOException          *             Signals that an I/O exception has occurred.          */
specifier|public
name|FileLineDifferenceIterator
parameter_list|(
name|File
name|marked
parameter_list|,
name|File
name|available
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|markedIter
operator|=
name|FileUtils
operator|.
name|lineIterator
argument_list|(
name|marked
argument_list|)
expr_stmt|;
name|this
operator|.
name|allIter
operator|=
name|FileUtils
operator|.
name|lineIterator
argument_list|(
name|available
argument_list|)
expr_stmt|;
name|queue
operator|=
operator|new
name|ArrayDeque
argument_list|<
name|String
argument_list|>
argument_list|(
name|getBatchCount
argument_list|()
argument_list|)
expr_stmt|;
name|markedBuffer
operator|=
name|Sets
operator|.
name|newTreeSet
argument_list|()
expr_stmt|;
block|}
comment|/**          * Close.          */
specifier|private
name|void
name|close
parameter_list|()
block|{
name|LineIterator
operator|.
name|closeQuietly
argument_list|(
name|markedIter
argument_list|)
expr_stmt|;
name|LineIterator
operator|.
name|closeQuietly
argument_list|(
name|allIter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|done
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|markedIter
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|allIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
name|queue
operator|.
name|addAll
argument_list|(
name|difference
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
name|done
operator|=
literal|true
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
return|return
name|nextDifference
argument_list|()
return|;
block|}
comment|/**          * Next difference.          *           * @return the string          */
specifier|public
name|String
name|nextDifference
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"No more difference"
argument_list|)
throw|;
block|}
return|return
name|queue
operator|.
name|remove
argument_list|()
return|;
block|}
comment|/**          * Difference.          *           * @return the sets the          */
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|difference
parameter_list|()
block|{
name|TreeSet
argument_list|<
name|String
argument_list|>
name|gcSet
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// Iterate till the gc candidate set is at least SAVE_BATCH_COUNT or
comment|// the
comment|// blob id set iteration is complete
while|while
condition|(
name|allIter
operator|.
name|hasNext
argument_list|()
operator|&&
name|gcSet
operator|.
name|size
argument_list|()
operator|<
name|getBatchCount
argument_list|()
condition|)
block|{
name|TreeSet
argument_list|<
name|String
argument_list|>
name|allBuffer
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|markedIter
operator|.
name|hasNext
argument_list|()
operator|&&
name|markedBuffer
operator|.
name|size
argument_list|()
operator|<
name|getBatchCount
argument_list|()
condition|)
block|{
name|String
name|stre
init|=
name|markedIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|markedBuffer
operator|.
name|add
argument_list|(
name|stre
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|allIter
operator|.
name|hasNext
argument_list|()
operator|&&
name|allBuffer
operator|.
name|size
argument_list|()
operator|<
name|getBatchCount
argument_list|()
condition|)
block|{
name|String
name|stre
init|=
name|allIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|allBuffer
operator|.
name|add
argument_list|(
name|stre
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|markedBuffer
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|gcSet
operator|=
name|allBuffer
expr_stmt|;
block|}
else|else
block|{
name|gcSet
operator|.
name|addAll
argument_list|(
name|Sets
operator|.
name|difference
argument_list|(
name|allBuffer
argument_list|,
name|markedBuffer
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|allBuffer
operator|.
name|last
argument_list|()
operator|.
name|compareTo
argument_list|(
name|markedBuffer
operator|.
name|last
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// filling markedLeftoverBuffer
name|TreeSet
argument_list|<
name|String
argument_list|>
name|markedLeftoverBuffer
init|=
name|Sets
operator|.
name|newTreeSet
argument_list|()
decl_stmt|;
name|markedLeftoverBuffer
operator|.
name|addAll
argument_list|(
name|markedBuffer
operator|.
name|tailSet
argument_list|(
name|allBuffer
operator|.
name|last
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|markedBuffer
operator|=
name|markedLeftoverBuffer
expr_stmt|;
name|markedLeftoverBuffer
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|markedBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|gcSet
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

