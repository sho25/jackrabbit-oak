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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|hybrid
package|;
end_package

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
name|lang
operator|.
name|Thread
operator|.
name|UncaughtExceptionHandler
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
name|Collections
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
name|concurrent
operator|.
name|BlockingQueue
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
name|Callable
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
name|Executor
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
name|LinkedBlockingDeque
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
name|locks
operator|.
name|Lock
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
name|ArrayListMultimap
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
name|ListMultimap
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
name|util
operator|.
name|concurrent
operator|.
name|Striped
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
name|PerfLogger
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
name|concurrent
operator|.
name|NotifyingFutureTask
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
name|index
operator|.
name|lucene
operator|.
name|IndexTracker
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
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexNode
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
name|index
operator|.
name|lucene
operator|.
name|writer
operator|.
name|LuceneIndexWriter
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
name|stats
operator|.
name|CounterStats
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
name|stats
operator|.
name|MeterStats
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
name|stats
operator|.
name|StatisticsProvider
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
name|stats
operator|.
name|StatsOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexableField
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
name|Preconditions
operator|.
name|checkState
import|;
end_import

begin_class
specifier|public
class|class
name|DocumentQueue
implements|implements
name|Closeable
implements|,
name|IndexingQueue
block|{
specifier|private
specifier|static
specifier|final
name|PerfLogger
name|PERF_LOGGER
init|=
operator|new
name|PerfLogger
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DocumentQueue
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".perf"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|LuceneDoc
name|STOP
init|=
name|LuceneDoc
operator|.
name|forUpdate
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
name|Collections
operator|.
expr|<
name|IndexableField
operator|>
name|emptyList
argument_list|()
argument_list|)
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
name|IndexTracker
name|tracker
decl_stmt|;
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|LuceneDoc
argument_list|>
name|docsQueue
decl_stmt|;
specifier|private
specifier|final
name|Executor
name|executor
decl_stmt|;
specifier|private
specifier|final
name|CounterStats
name|queueSizeStats
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|added
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|dropped
decl_stmt|;
specifier|private
specifier|final
name|Striped
argument_list|<
name|Lock
argument_list|>
name|locks
init|=
name|Striped
operator|.
name|lock
argument_list|(
literal|64
argument_list|)
decl_stmt|;
specifier|private
name|UncaughtExceptionHandler
name|delegate
init|=
parameter_list|(
name|t
parameter_list|,
name|e
parameter_list|)
lambda|->
block|{}
decl_stmt|;
comment|/**      * Time in millis for which add call to queue      * would wait before dropping off      */
specifier|private
specifier|final
name|int
name|offerTimeMillis
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|stopped
decl_stmt|;
comment|/**      * Handler for uncaught exception on the background thread      */
specifier|private
specifier|final
name|UncaughtExceptionHandler
name|exceptionHandler
init|=
operator|new
name|UncaughtExceptionHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|uncaughtException
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Uncaught exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**      * Current background task      */
specifier|private
specifier|volatile
name|NotifyingFutureTask
name|currentTask
init|=
name|NotifyingFutureTask
operator|.
name|completed
argument_list|()
decl_stmt|;
comment|/**      * Completion handler: set the current task to the next task and schedules that one      * on the background thread.      */
specifier|private
specifier|final
name|Runnable
name|completionHandler
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|private
specifier|final
name|Callable
argument_list|<
name|Void
argument_list|>
name|task
init|=
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|long
name|start
init|=
name|PERF_LOGGER
operator|.
name|start
argument_list|()
decl_stmt|;
name|int
name|maxSize
init|=
name|docsQueue
operator|.
name|size
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|LuceneDoc
argument_list|>
name|docs
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|maxSize
argument_list|)
decl_stmt|;
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|LuceneDoc
argument_list|>
name|docsPerIndex
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
comment|//Do the processing in batches
name|int
name|count
init|=
name|docsQueue
operator|.
name|drainTo
argument_list|(
name|docs
argument_list|,
name|maxSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|queueSizeStats
operator|.
name|dec
argument_list|(
name|count
argument_list|)
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|LuceneDoc
name|doc
init|=
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
name|STOP
condition|)
block|{
return|return
literal|null
return|;
block|}
name|docsPerIndex
operator|.
name|get
argument_list|(
name|doc
operator|.
name|indexPath
argument_list|)
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|addDocsToIndex
argument_list|(
name|docsPerIndex
operator|.
name|asMap
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|scheduleQueuedDocsProcessing
argument_list|()
expr_stmt|;
name|PERF_LOGGER
operator|.
name|end
argument_list|(
name|start
argument_list|,
literal|1
argument_list|,
literal|"Processed {} docs from queue"
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exceptionHandler
operator|.
name|uncaughtException
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|uncaughtException
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|currentTask
operator|=
operator|new
name|NotifyingFutureTask
argument_list|(
name|task
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|currentTask
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|public
name|DocumentQueue
parameter_list|(
name|int
name|maxQueueSize
parameter_list|,
name|IndexTracker
name|tracker
parameter_list|,
name|Executor
name|executor
parameter_list|)
block|{
name|this
argument_list|(
name|maxQueueSize
argument_list|,
name|tracker
argument_list|,
name|executor
argument_list|,
name|StatisticsProvider
operator|.
name|NOOP
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DocumentQueue
parameter_list|(
name|int
name|maxQueueSize
parameter_list|,
name|IndexTracker
name|tracker
parameter_list|,
name|Executor
name|executor
parameter_list|,
name|StatisticsProvider
name|sp
parameter_list|)
block|{
name|this
operator|.
name|docsQueue
operator|=
operator|new
name|LinkedBlockingDeque
argument_list|<>
argument_list|(
name|maxQueueSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|tracker
operator|=
name|tracker
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
name|this
operator|.
name|offerTimeMillis
operator|=
literal|100
expr_stmt|;
comment|//Wait for at most 100 mills while adding stuff to queue
name|this
operator|.
name|queueSizeStats
operator|=
name|sp
operator|.
name|getCounterStats
argument_list|(
literal|"HYBRID_QUEUE_SIZE"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|added
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
literal|"HYBRID_ADDED"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|dropped
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
literal|"HYBRID_DROPPED"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|addIfNotFullWithoutWait
parameter_list|(
name|LuceneDoc
name|doc
parameter_list|)
block|{
name|checkState
argument_list|(
operator|!
name|stopped
argument_list|)
expr_stmt|;
name|boolean
name|added
init|=
name|docsQueue
operator|.
name|offer
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|added
condition|)
block|{
name|queueSizeStats
operator|.
name|inc
argument_list|()
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"Adding {} without wait to queue at size {}"
argument_list|,
name|doc
argument_list|,
name|docsQueue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|added
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|add
parameter_list|(
name|LuceneDoc
name|doc
parameter_list|)
block|{
name|checkState
argument_list|(
operator|!
name|stopped
argument_list|)
expr_stmt|;
name|boolean
name|added
init|=
literal|false
decl_stmt|;
try|try
block|{
name|added
operator|=
name|docsQueue
operator|.
name|offer
argument_list|(
name|doc
argument_list|,
name|offerTimeMillis
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
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
block|}
name|scheduleQueuedDocsProcessing
argument_list|()
expr_stmt|;
if|if
condition|(
name|added
condition|)
block|{
name|queueSizeStats
operator|.
name|inc
argument_list|()
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"Adding {} to queue at size {}"
argument_list|,
name|doc
argument_list|,
name|docsQueue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|dropped
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
return|return
name|added
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|scheduleQueuedDocsProcessing
parameter_list|()
block|{
comment|// Set the completion handler on the currently running task. Multiple calls
comment|// to onComplete are not a problem here since we always pass the same value.
comment|// Thus there is no question as to which of the handlers will effectively run.
name|currentTask
operator|.
name|onComplete
argument_list|(
name|completionHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addAllSynchronously
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|LuceneDoc
argument_list|>
argument_list|>
name|docsPerIndex
parameter_list|)
block|{
name|addDocsToIndex
argument_list|(
name|docsPerIndex
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Delegate handled which can be used by test to check for      * any exception occurring in queue processing      */
specifier|public
name|void
name|setExceptionHandler
parameter_list|(
name|UncaughtExceptionHandler
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
specifier|private
name|void
name|addDocsToIndex
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|LuceneDoc
argument_list|>
argument_list|>
name|docsPerIndex
parameter_list|,
name|boolean
name|docsFromQueue
parameter_list|)
block|{
comment|//If required it can optimized by indexing diff indexes in parallel
comment|//Something to consider if it becomes a bottleneck
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|LuceneDoc
argument_list|>
argument_list|>
name|e
range|:
name|docsPerIndex
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|//In NRT case the indexing would be single threaded as it always happens via queue
comment|//For sync case it can happen that indexing is requested by LocalIndexObserver and also
comment|//via elements in queue. So we need to lock the indexing path
comment|//Lock contention should not happen much as in most cases elements added
comment|//to queue would get processed before observer is invoked
name|String
name|indexPath
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Lock
name|indexingLock
init|=
name|locks
operator|.
name|get
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
name|indexingLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|processDocs
argument_list|(
name|indexPath
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
name|docsFromQueue
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexingLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|added
operator|.
name|mark
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|LuceneDoc
argument_list|>
name|getQueuedDocs
parameter_list|()
block|{
name|List
argument_list|<
name|LuceneDoc
argument_list|>
name|docs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|docs
operator|.
name|addAll
argument_list|(
name|docsQueue
argument_list|)
expr_stmt|;
return|return
name|docs
return|;
block|}
specifier|private
name|void
name|processDocs
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|Iterable
argument_list|<
name|LuceneDoc
argument_list|>
name|docs
parameter_list|,
name|boolean
name|docsFromQueue
parameter_list|)
block|{
comment|//Drop the write call if stopped
if|if
condition|(
name|stopped
condition|)
block|{
return|return;
block|}
name|LuceneIndexNode
name|indexNode
init|=
name|tracker
operator|.
name|acquireIndexNode
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexNode
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No LuceneIndexNode found for index [{}]."
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|LuceneIndexWriter
name|writer
init|=
name|indexNode
operator|.
name|getLocalWriter
argument_list|()
decl_stmt|;
name|boolean
name|docAdded
init|=
literal|false
decl_stmt|;
for|for
control|(
name|LuceneDoc
name|doc
range|:
name|docs
control|)
block|{
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
comment|//IndexDefinition per LuceneIndexNode might have changed and local
comment|//indexing is disabled. Ignore
name|log
operator|.
name|debug
argument_list|(
literal|"No local IndexWriter found for index [{}]. Skipping index "
operator|+
literal|"entry for [{}]"
argument_list|,
name|indexPath
argument_list|,
name|doc
operator|.
name|docPath
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|doc
operator|.
name|isProcessed
argument_list|()
condition|)
block|{
comment|//Skip already processed doc entry
continue|continue;
block|}
else|else
block|{
name|doc
operator|.
name|markProcessed
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|doc
operator|.
name|delete
condition|)
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|doc
operator|.
name|docPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|updateDocument
argument_list|(
name|doc
operator|.
name|docPath
argument_list|,
name|doc
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
name|docAdded
operator|=
literal|true
expr_stmt|;
name|String
name|prefix
init|=
name|docsFromQueue
condition|?
literal|"Queued"
else|:
literal|"Direct"
decl_stmt|;
name|log
operator|.
name|trace
argument_list|(
literal|"[{}] Updated index with doc {}"
argument_list|,
name|prefix
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|docAdded
condition|)
block|{
name|indexNode
operator|.
name|refreshReadersOnWriteIfRequired
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//For now we just log it. Later we need to see if frequent error then to
comment|//temporarily disable indexing for this index
name|log
operator|.
name|warn
argument_list|(
literal|"Error occurred while indexing index [{}]"
argument_list|,
name|indexPath
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|uncaughtException
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexNode
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
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
comment|//Its fine to "drop" any entry in queue as
comment|//local index is meant for running state only
name|docsQueue
operator|.
name|clear
argument_list|()
expr_stmt|;
name|docsQueue
operator|.
name|add
argument_list|(
name|STOP
argument_list|)
expr_stmt|;
name|stopped
operator|=
literal|true
expr_stmt|;
block|}
block|}
end_class

end_unit

