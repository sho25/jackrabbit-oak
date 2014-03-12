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
name|spi
operator|.
name|commit
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
name|Queues
operator|.
name|newArrayBlockingQueue
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
name|FutureTask
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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

begin_comment
comment|/**  * An observer that uses a change queue and a background thread to forward  * content changes to another observer. The mechanism is designed so that  * the {@link #contentChanged(NodeState, CommitInfo)} method will never block,  * regardless of the behavior of the other observer. If that observer blocks  * or is too slow to consume all content changes, causing the change queue  * to fill up, any further update will automatically be merged into just one  * external content change, causing potential loss of local commit information.  * To help prevent such cases, any sequential external content changes that  * the background observer thread has yet to process are automatically merged  * to just one change.  */
end_comment

begin_class
specifier|public
class|class
name|BackgroundObserver
implements|implements
name|Observer
implements|,
name|Closeable
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BackgroundObserver
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Signal for the background thread to stop processing changes.      */
specifier|private
specifier|static
specifier|final
name|ContentChange
name|STOP
init|=
operator|new
name|ContentChange
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|/**      * The receiving observer being notified off the background thread.      */
specifier|private
specifier|final
name|Observer
name|observer
decl_stmt|;
comment|/**      * Executor used to dispatch events      */
specifier|private
specifier|final
name|Executor
name|executor
decl_stmt|;
comment|/**      * Handler for uncaught exception on the background thread      */
specifier|private
specifier|final
name|UncaughtExceptionHandler
name|exceptionHandler
decl_stmt|;
comment|/**      * The queue of content changes to be processed.      */
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|ContentChange
argument_list|>
name|queue
decl_stmt|;
specifier|private
specifier|static
class|class
name|ContentChange
block|{
specifier|private
specifier|final
name|NodeState
name|root
decl_stmt|;
specifier|private
specifier|final
name|CommitInfo
name|info
decl_stmt|;
name|ContentChange
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
block|}
comment|/**      * The content change that was last added to the queue.      * Used to compact external changes.      */
specifier|private
name|ContentChange
name|last
decl_stmt|;
comment|/**      * Flag to indicate that some content changes were dropped because      * the queue was full.      */
specifier|private
name|boolean
name|full
decl_stmt|;
comment|/**      * Current background task      */
specifier|private
specifier|volatile
name|ListenableFutureTask
name|currentTask
init|=
name|ListenableFutureTask
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
name|ContentChange
name|change
init|=
name|queue
operator|.
name|poll
argument_list|()
decl_stmt|;
while|while
condition|(
name|change
operator|!=
literal|null
operator|&&
name|change
operator|!=
name|STOP
condition|)
block|{
name|observer
operator|.
name|contentChanged
argument_list|(
name|change
operator|.
name|root
argument_list|,
name|change
operator|.
name|info
argument_list|)
expr_stmt|;
name|change
operator|=
name|queue
operator|.
name|poll
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
name|ListenableFutureTask
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
comment|/**      * {@code true} after this observer has been stopped      */
specifier|private
specifier|volatile
name|boolean
name|stopped
decl_stmt|;
specifier|public
name|BackgroundObserver
parameter_list|(
annotation|@
name|Nonnull
name|Observer
name|observer
parameter_list|,
annotation|@
name|Nonnull
name|Executor
name|executor
parameter_list|,
name|int
name|queueLength
parameter_list|,
annotation|@
name|Nonnull
name|UncaughtExceptionHandler
name|exceptionHandler
parameter_list|)
block|{
name|this
operator|.
name|observer
operator|=
name|checkNotNull
argument_list|(
name|observer
argument_list|)
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|checkNotNull
argument_list|(
name|executor
argument_list|)
expr_stmt|;
name|this
operator|.
name|exceptionHandler
operator|=
name|checkNotNull
argument_list|(
name|exceptionHandler
argument_list|)
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|newArrayBlockingQueue
argument_list|(
name|queueLength
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BackgroundObserver
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Observer
name|observer
parameter_list|,
annotation|@
name|Nonnull
name|Executor
name|executor
parameter_list|,
name|int
name|queueLength
parameter_list|)
block|{
name|this
argument_list|(
name|observer
argument_list|,
name|executor
argument_list|,
name|queueLength
argument_list|,
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
name|getLogger
argument_list|(
name|observer
argument_list|)
operator|.
name|error
argument_list|(
literal|"Uncaught exception in "
operator|+
name|observer
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BackgroundObserver
parameter_list|(
annotation|@
name|Nonnull
name|Observer
name|observer
parameter_list|,
annotation|@
name|Nonnull
name|Executor
name|executor
parameter_list|)
block|{
name|this
argument_list|(
name|observer
argument_list|,
name|executor
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|/**      * Clears the change queue and signals the background thread to stop      * without making any further {@link #contentChanged(NodeState, CommitInfo)}      * calls to the background observer. If the thread is currently in the      * middle of such a call, then that call is allowed to complete; i.e.      * the thread is not forcibly interrupted. This method returns immediately      * without blocking to wait for the thread to finish.      *<p>      * After a call to this method further calls to {@link #contentChanged(NodeState, CommitInfo)}      * will throw a {@code IllegalStateException}.      */
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
name|queue
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
comment|//----------------------------------------------------------< Observer>--
comment|/**      * @throws IllegalStateException  if {@link #close()} has already been called.      */
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|contentChanged
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|,
annotation|@
name|Nullable
name|CommitInfo
name|info
parameter_list|)
block|{
name|checkState
argument_list|(
operator|!
name|stopped
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
operator|&&
name|last
operator|!=
literal|null
operator|&&
name|last
operator|.
name|info
operator|==
literal|null
condition|)
block|{
comment|// This is an external change. If the previous change was
comment|// also external, we can drop it from the queue (since external
comment|// changes in any case can cover multiple commits) to help
comment|// prevent the queue from filling up too fast.
name|queue
operator|.
name|remove
argument_list|(
name|last
argument_list|)
expr_stmt|;
name|full
operator|=
literal|false
expr_stmt|;
block|}
name|ContentChange
name|change
decl_stmt|;
if|if
condition|(
name|full
condition|)
block|{
comment|// If the queue is full, some commits have already been skipped
comment|// so we need to drop the possible local commit information as
comment|// only external changes can be merged together to larger chunks.
name|change
operator|=
operator|new
name|ContentChange
argument_list|(
name|root
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|change
operator|=
operator|new
name|ContentChange
argument_list|(
name|root
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
comment|// Try to add this change to the queue without blocking, and
comment|// mark the queue as full if there wasn't enough space
name|boolean
name|wasFull
init|=
name|full
decl_stmt|;
name|full
operator|=
operator|!
name|queue
operator|.
name|offer
argument_list|(
name|change
argument_list|)
expr_stmt|;
if|if
condition|(
name|full
operator|&&
operator|!
name|wasFull
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Revision queue is full. Further revisions will be compacted."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|full
condition|)
block|{
comment|// Keep track of the last change added, so we can do the
comment|// compacting of external changes shown above.
name|last
operator|=
name|change
expr_stmt|;
block|}
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
comment|//------------------------------------------------------------< internal>---
specifier|private
specifier|static
name|Logger
name|getLogger
parameter_list|(
annotation|@
name|Nonnull
name|Observer
name|observer
parameter_list|)
block|{
return|return
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|checkNotNull
argument_list|(
name|observer
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * A future task with a on complete handler.      */
specifier|private
specifier|static
class|class
name|ListenableFutureTask
extends|extends
name|FutureTask
argument_list|<
name|Void
argument_list|>
block|{
specifier|private
specifier|final
name|AtomicBoolean
name|completed
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|volatile
name|Runnable
name|onComplete
decl_stmt|;
specifier|public
name|ListenableFutureTask
parameter_list|(
name|Callable
argument_list|<
name|Void
argument_list|>
name|callable
parameter_list|)
block|{
name|super
argument_list|(
name|callable
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ListenableFutureTask
parameter_list|(
name|Runnable
name|task
parameter_list|)
block|{
name|super
argument_list|(
name|task
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**          * Set the on complete handler. The handler will run exactly once after          * the task terminated. If the task has already terminated at the time of          * this method call the handler will execute immediately.          *<p>          * Note: there is no guarantee to which handler will run when the method          * is called multiple times with different arguments.          * @param onComplete          */
specifier|public
name|void
name|onComplete
parameter_list|(
name|Runnable
name|onComplete
parameter_list|)
block|{
name|this
operator|.
name|onComplete
operator|=
name|onComplete
expr_stmt|;
if|if
condition|(
name|isDone
argument_list|()
condition|)
block|{
name|run
argument_list|(
name|onComplete
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|done
parameter_list|()
block|{
name|run
argument_list|(
name|onComplete
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|run
parameter_list|(
name|Runnable
name|onComplete
parameter_list|)
block|{
if|if
condition|(
name|onComplete
operator|!=
literal|null
operator|&&
name|completed
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|onComplete
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
specifier|final
name|Runnable
name|NOP
init|=
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
block|{             }
block|}
decl_stmt|;
specifier|public
specifier|static
name|ListenableFutureTask
name|completed
parameter_list|()
block|{
name|ListenableFutureTask
name|f
init|=
operator|new
name|ListenableFutureTask
argument_list|(
name|NOP
argument_list|)
decl_stmt|;
name|f
operator|.
name|run
argument_list|()
expr_stmt|;
return|return
name|f
return|;
block|}
block|}
block|}
end_class

end_unit

