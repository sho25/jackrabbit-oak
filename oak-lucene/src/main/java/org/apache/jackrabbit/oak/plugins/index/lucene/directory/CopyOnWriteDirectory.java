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
name|directory
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|Arrays
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
name|Set
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
name|ConcurrentMap
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
name|RejectedExecutionException
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
name|AtomicReference
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
name|Iterables
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
name|IndexCopier
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
name|IndexCopierClosedException
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
name|util
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
name|lucene
operator|.
name|store
operator|.
name|Directory
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
name|store
operator|.
name|FilterDirectory
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
name|store
operator|.
name|IOContext
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
name|store
operator|.
name|IndexInput
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
name|store
operator|.
name|IndexOutput
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
name|collect
operator|.
name|Maps
operator|.
name|newConcurrentMap
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

begin_class
specifier|public
class|class
name|CopyOnWriteDirectory
extends|extends
name|FilterDirectory
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
name|CopyOnWriteDirectory
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|log
operator|.
name|getName
argument_list|()
operator|+
literal|".perf"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|IndexCopier
name|indexCopier
decl_stmt|;
comment|/**      * Signal for the background thread to stop processing changes.      */
specifier|private
specifier|final
name|Callable
argument_list|<
name|Void
argument_list|>
name|STOP
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
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|Directory
name|remote
decl_stmt|;
specifier|private
specifier|final
name|Directory
name|local
decl_stmt|;
specifier|private
specifier|final
name|Executor
name|executor
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|COWFileReference
argument_list|>
name|fileMap
init|=
name|newConcurrentMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|deletedFilesLocal
init|=
name|Sets
operator|.
name|newConcurrentHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|skippedFiles
init|=
name|Sets
operator|.
name|newConcurrentHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|>
name|queue
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
name|errorInCopy
init|=
operator|new
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|CountDownLatch
name|copyDone
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|reindexMode
decl_stmt|;
specifier|private
specifier|final
name|String
name|indexPathForLogging
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|sharedWorkingSet
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
name|Callable
argument_list|<
name|Void
argument_list|>
name|task
init|=
name|queue
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|task
operator|!=
literal|null
operator|&&
name|task
operator|!=
name|STOP
condition|)
block|{
if|if
condition|(
name|errorInCopy
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"[COW][{}] Skipping task {} as some exception occurred in previous run"
argument_list|,
name|indexPathForLogging
argument_list|,
name|task
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|task
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
name|currentTask
operator|.
name|onComplete
argument_list|(
name|completionHandler
argument_list|)
expr_stmt|;
block|}
comment|//Signal that all tasks completed
if|if
condition|(
name|task
operator|==
name|STOP
condition|)
block|{
name|copyDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|errorInCopy
operator|.
name|set
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"[COW][{}] Error occurred while copying files. Further processing would "
operator|+
literal|"be skipped"
argument_list|,
name|indexPathForLogging
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|currentTask
operator|.
name|onComplete
argument_list|(
name|completionHandler
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
try|try
block|{
name|executor
operator|.
name|execute
argument_list|(
name|currentTask
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RejectedExecutionException
name|e
parameter_list|)
block|{
name|checkIfClosed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
decl_stmt|;
specifier|public
name|CopyOnWriteDirectory
parameter_list|(
name|IndexCopier
name|indexCopier
parameter_list|,
name|Directory
name|remote
parameter_list|,
name|Directory
name|local
parameter_list|,
name|boolean
name|reindexMode
parameter_list|,
name|String
name|indexPathForLogging
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|sharedWorkingSet
parameter_list|,
name|Executor
name|executor
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|local
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexCopier
operator|=
name|indexCopier
expr_stmt|;
name|this
operator|.
name|remote
operator|=
name|remote
expr_stmt|;
name|this
operator|.
name|local
operator|=
name|local
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
name|this
operator|.
name|indexPathForLogging
operator|=
name|indexPathForLogging
expr_stmt|;
name|this
operator|.
name|reindexMode
operator|=
name|reindexMode
expr_stmt|;
name|this
operator|.
name|sharedWorkingSet
operator|=
name|sharedWorkingSet
expr_stmt|;
name|initialize
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Iterables
operator|.
name|toArray
argument_list|(
name|fileMap
operator|.
name|keySet
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fileMap
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"[COW][{}] Deleted file {}"
argument_list|,
name|indexPathForLogging
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|COWFileReference
name|ref
init|=
name|fileMap
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
name|ref
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|COWFileReference
name|ref
init|=
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
block|}
return|return
name|ref
operator|.
name|fileLength
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|COWFileReference
name|ref
init|=
name|fileMap
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
name|ref
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|ref
operator|=
operator|new
name|COWLocalFileReference
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|fileMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|sharedWorkingSet
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|ref
operator|.
name|createOutput
argument_list|(
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|COWFileReference
name|file
init|=
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
name|file
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|COWFileReference
name|ref
init|=
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
block|}
return|return
name|ref
operator|.
name|openInput
argument_list|(
name|context
argument_list|)
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
name|int
name|pendingCopies
init|=
name|queue
operator|.
name|size
argument_list|()
decl_stmt|;
name|addTask
argument_list|(
name|STOP
argument_list|)
expr_stmt|;
comment|//Wait for all pending copy task to finish
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
comment|//Loop untill queue finished or IndexCopier
comment|//found to be closed. Doing it with timeout to
comment|//prevent any bug causing the thread to wait indefinitely
while|while
condition|(
operator|!
name|copyDone
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
if|if
condition|(
name|indexCopier
operator|.
name|isClosed
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IndexCopierClosedException
argument_list|(
literal|"IndexCopier found to be closed "
operator|+
literal|"while processing copy task for"
operator|+
name|remote
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|PERF_LOGGER
operator|.
name|end
argument_list|(
name|start
argument_list|,
operator|-
literal|1
argument_list|,
literal|"[COW][{}] Completed pending copying task {}"
argument_list|,
name|indexPathForLogging
argument_list|,
name|pendingCopies
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
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|Throwable
name|t
init|=
name|errorInCopy
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error occurred while copying files for "
operator|+
name|indexPathForLogging
argument_list|,
name|t
argument_list|)
throw|;
block|}
comment|//Sanity check
name|checkArgument
argument_list|(
name|queue
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"Copy queue still "
operator|+
literal|"has pending task left [%d]. %s"
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|,
name|queue
argument_list|)
expr_stmt|;
name|long
name|skippedFilesSize
init|=
name|getSkippedFilesSize
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fileName
range|:
name|deletedFilesLocal
control|)
block|{
name|deleteLocalFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
name|indexCopier
operator|.
name|skippedUpload
argument_list|(
name|skippedFilesSize
argument_list|)
expr_stmt|;
name|String
name|msg
init|=
literal|"[COW][{}] CopyOnWrite stats : Skipped copying {} files with total size {}"
decl_stmt|;
if|if
condition|(
operator|(
name|reindexMode
operator|&&
name|skippedFilesSize
operator|>
literal|0
operator|)
operator|||
name|skippedFilesSize
operator|>
literal|10
operator|*
name|FileUtils
operator|.
name|ONE_MB
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|msg
argument_list|,
name|indexPathForLogging
argument_list|,
name|skippedFiles
operator|.
name|size
argument_list|()
argument_list|,
name|humanReadableByteCount
argument_list|(
name|skippedFilesSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
name|msg
argument_list|,
name|indexPathForLogging
argument_list|,
name|skippedFiles
operator|.
name|size
argument_list|()
argument_list|,
name|humanReadableByteCount
argument_list|(
name|skippedFilesSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"[COW][{}] File listing - Upon completion {}"
argument_list|,
name|indexPathForLogging
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|remote
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|local
operator|.
name|close
argument_list|()
expr_stmt|;
name|remote
operator|.
name|close
argument_list|()
expr_stmt|;
name|sharedWorkingSet
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"[COW][%s] Local %s, Remote %s"
argument_list|,
name|indexPathForLogging
argument_list|,
name|local
argument_list|,
name|remote
argument_list|)
return|;
block|}
specifier|private
name|long
name|getSkippedFilesSize
parameter_list|()
block|{
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|skippedFiles
control|)
block|{
try|try
block|{
if|if
condition|(
name|local
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|size
operator|+=
name|local
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{              }
block|}
return|return
name|size
return|;
block|}
specifier|private
name|void
name|deleteLocalFile
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
name|indexCopier
operator|.
name|deleteFile
argument_list|(
name|local
argument_list|,
name|fileName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|initialize
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|name
range|:
name|remote
operator|.
name|listAll
argument_list|()
control|)
block|{
name|fileMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|COWRemoteFileReference
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"[COW][{}] File listing - At start {}"
argument_list|,
name|indexPathForLogging
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|remote
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addCopyTask
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|indexCopier
operator|.
name|scheduledForCopy
argument_list|()
expr_stmt|;
name|addTask
argument_list|(
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
name|indexCopier
operator|.
name|copyDone
argument_list|()
expr_stmt|;
if|if
condition|(
name|deletedFilesLocal
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|skippedFiles
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|log
operator|.
name|trace
argument_list|(
literal|"[COW][{}] Skip copying of deleted file {}"
argument_list|,
name|indexPathForLogging
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|long
name|fileSize
init|=
name|local
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|LocalIndexFile
name|file
init|=
operator|new
name|LocalIndexFile
argument_list|(
name|local
argument_list|,
name|name
argument_list|,
name|fileSize
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|long
name|perfStart
init|=
name|PERF_LOGGER
operator|.
name|start
argument_list|()
decl_stmt|;
name|long
name|start
init|=
name|indexCopier
operator|.
name|startCopy
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|local
operator|.
name|copy
argument_list|(
name|remote
argument_list|,
name|name
argument_list|,
name|name
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|indexCopier
operator|.
name|doneCopy
argument_list|(
name|file
argument_list|,
name|start
argument_list|)
expr_stmt|;
name|PERF_LOGGER
operator|.
name|end
argument_list|(
name|perfStart
argument_list|,
literal|0
argument_list|,
literal|"[COW][{}] Copied to remote {} -- size: {}"
argument_list|,
name|indexPathForLogging
argument_list|,
name|name
argument_list|,
name|IOUtils
operator|.
name|humanReadableByteCount
argument_list|(
name|fileSize
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Copy: "
operator|+
name|name
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addDeleteTask
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|addTask
argument_list|(
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
if|if
condition|(
operator|!
name|skippedFiles
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"[COW][{}] Marking as deleted {}"
argument_list|,
name|indexPathForLogging
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|remote
operator|.
name|deleteFile
argument_list|(
name|name
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
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Delete : "
operator|+
name|name
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addTask
parameter_list|(
name|Callable
argument_list|<
name|Void
argument_list|>
name|task
parameter_list|)
block|{
name|checkIfClosed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|task
argument_list|)
expr_stmt|;
name|currentTask
operator|.
name|onComplete
argument_list|(
name|completionHandler
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkIfClosed
parameter_list|(
name|boolean
name|throwException
parameter_list|)
block|{
if|if
condition|(
name|indexCopier
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|IndexCopierClosedException
name|e
init|=
operator|new
name|IndexCopierClosedException
argument_list|(
literal|"IndexCopier found to be closed "
operator|+
literal|"while processing"
operator|+
name|remote
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|errorInCopy
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|copyDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|throwException
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
specifier|private
specifier|abstract
class|class
name|COWFileReference
block|{
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|COWFileReference
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|long
name|fileLength
parameter_list|()
throws|throws
name|IOException
function_decl|;
specifier|public
specifier|abstract
name|IndexInput
name|openInput
parameter_list|(
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|public
specifier|abstract
name|IndexOutput
name|createOutput
parameter_list|(
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|public
specifier|abstract
name|void
name|delete
parameter_list|()
throws|throws
name|IOException
function_decl|;
specifier|public
name|void
name|sync
parameter_list|()
throws|throws
name|IOException
block|{          }
block|}
specifier|private
class|class
name|COWRemoteFileReference
extends|extends
name|COWFileReference
block|{
specifier|private
name|boolean
name|validLocalCopyPresent
decl_stmt|;
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
specifier|public
name|COWRemoteFileReference
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|remote
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|fileLength
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|checkIfLocalValid
argument_list|()
expr_stmt|;
if|if
condition|(
name|validLocalCopyPresent
operator|&&
operator|!
name|IndexCopier
operator|.
name|REMOTE_ONLY
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|indexCopier
operator|.
name|readFromLocal
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|local
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
name|indexCopier
operator|.
name|readFromRemote
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|remote
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot create output for existing remote file "
operator|+
name|name
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|delete
parameter_list|()
throws|throws
name|IOException
block|{
comment|//Remote file should not be deleted locally as it might be
comment|//in use by existing opened IndexSearcher. It would anyway
comment|//get deleted by CopyOnRead later
comment|//For now just record that these need to be deleted to avoid
comment|//potential concurrent access of the NodeBuilder
name|addDeleteTask
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkIfLocalValid
parameter_list|()
throws|throws
name|IOException
block|{
name|validLocalCopyPresent
operator|=
name|local
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
operator|&&
name|local
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
operator|==
name|remote
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|COWLocalFileReference
extends|extends
name|COWFileReference
block|{
specifier|public
name|COWLocalFileReference
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|fileLength
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|local
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|local
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"[COW][{}] Creating output {}"
argument_list|,
name|indexPathForLogging
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
operator|new
name|COWLocalFileReference
operator|.
name|CopyOnCloseIndexOutput
argument_list|(
name|local
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|delete
parameter_list|()
throws|throws
name|IOException
block|{
name|addDeleteTask
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|deletedFilesLocal
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|sync
parameter_list|()
throws|throws
name|IOException
block|{
name|local
operator|.
name|sync
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**          * Implementation note - As we are decorating existing implementation          * we would need to ensure that we also override methods (non abstract)          * which might be implemented in say FSIndexInput like setLength          */
specifier|private
class|class
name|CopyOnCloseIndexOutput
extends|extends
name|IndexOutput
block|{
specifier|private
specifier|final
name|IndexOutput
name|delegate
decl_stmt|;
specifier|public
name|CopyOnCloseIndexOutput
parameter_list|(
name|IndexOutput
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
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|flush
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
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//Schedule this file to be copied in background
name|addCopyTask
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getFilePointer
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|writeByte
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
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
name|delegate
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setLength
parameter_list|(
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

