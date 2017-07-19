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
name|Iterables
operator|.
name|filter
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
name|Iterables
operator|.
name|size
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
name|TimeUnit
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
comment|/**  * An observer that uses a change queue and a background thread to forward  * content changes to another observer. The mechanism is designed so that  * the {@link #contentChanged(NodeState, CommitInfo)} method will never block,  * regardless of the behavior of the other observer. If that observer blocks  * or is too slow to consume all content changes, causing the change queue  * to fill up, any further update will automatically be merged into just one  * external content change, causing potential loss of local commit information.  * To help prevent such cases, any sequential external content changes that  * the background observer thread has yet to process are optionally  * (see {@code alwaysCollapseExternalEvents} and {@code oak.observation.alwaysCollapseExternal})  * automatically merged to just one change.  */
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
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_QUEUE_SIZE
init|=
literal|10000
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
comment|/**      * The max queue length used for this observer's queue      */
specifier|private
specifier|final
name|int
name|maxQueueLength
decl_stmt|;
comment|/**      * Whether external events should be collapsed even if queue isn't full yet.      */
specifier|private
specifier|final
name|boolean
name|alwaysCollapseExternalEvents
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"oak.observation.alwaysCollapseExternal"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
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
specifier|private
specifier|final
name|long
name|created
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
name|ContentChange
name|change
init|=
name|queue
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
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
name|removed
argument_list|(
name|queue
operator|.
name|size
argument_list|()
argument_list|,
name|change
operator|.
name|created
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
name|maxQueueLength
operator|=
name|queueLength
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|newArrayBlockingQueue
argument_list|(
name|maxQueueLength
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
name|DEFAULT_QUEUE_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**      * Called when ever an item has been added to the queue      * @param queueSize  size of the queue      */
specifier|protected
name|void
name|added
parameter_list|(
name|int
name|queueSize
parameter_list|)
block|{ }
comment|/**      * Called when ever an item has been removed from the queue.      *      * @param queueSize the size of the queue after the item was removed.      * @param created the time in milliseconds when the removed item was put      *                into the queue.      */
specifier|protected
name|void
name|removed
parameter_list|(
name|int
name|queueSize
parameter_list|,
name|long
name|created
parameter_list|)
block|{ }
comment|/**      * @return  The max queue length used for this observer's queue      */
specifier|public
name|int
name|getMaxQueueLength
parameter_list|()
block|{
return|return
name|maxQueueLength
return|;
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
annotation|@
name|Nonnull
specifier|public
name|BackgroundObserverMBean
name|getMBean
parameter_list|()
block|{
return|return
operator|new
name|BackgroundObserverMBean
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getClassName
parameter_list|()
block|{
return|return
name|observer
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getQueueSize
parameter_list|()
block|{
return|return
name|queue
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMaxQueueSize
parameter_list|()
block|{
return|return
name|getMaxQueueLength
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getLocalEventCount
parameter_list|()
block|{
return|return
name|size
argument_list|(
name|filter
argument_list|(
name|queue
argument_list|,
operator|new
name|Predicate
argument_list|<
name|ContentChange
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|ContentChange
name|input
parameter_list|)
block|{
return|return
operator|!
name|input
operator|.
name|info
operator|.
name|isExternal
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getExternalEventCount
parameter_list|()
block|{
return|return
name|size
argument_list|(
name|filter
argument_list|(
name|queue
argument_list|,
operator|new
name|Predicate
argument_list|<
name|ContentChange
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|ContentChange
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|info
operator|.
name|isExternal
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
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
name|Nonnull
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
name|checkNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
if|if
condition|(
name|alwaysCollapseExternalEvents
operator|&&
name|info
operator|.
name|isExternal
argument_list|()
operator|&&
name|last
operator|!=
literal|null
operator|&&
name|last
operator|.
name|info
operator|.
name|isExternal
argument_list|()
condition|)
block|{
comment|// This is an external change. If the previous change was
comment|// also external, we can drop it from the queue (since external
comment|// changes in any case can cover multiple commits) to help
comment|// prevent the queue from filling up too fast.
comment|//TODO - Support for merging ChangeSet for external changes
name|queue
operator|.
name|remove
argument_list|(
name|last
argument_list|)
expr_stmt|;
block|}
name|ContentChange
name|change
init|=
operator|new
name|ContentChange
argument_list|(
name|root
argument_list|,
name|info
argument_list|)
decl_stmt|;
comment|// Try to add this change to the queue without blocking
name|boolean
name|full
init|=
operator|!
name|queue
operator|.
name|offer
argument_list|(
name|change
argument_list|)
decl_stmt|;
if|if
condition|(
name|full
operator|&&
name|last
operator|!=
literal|null
condition|)
block|{
comment|// last is only null at the beginning
comment|// queue is full.
comment|// when the change can't be added to the queue because it's full
comment|// remove the last entry and add an explicit overflow entry instead.
name|queue
operator|.
name|remove
argument_list|(
name|last
argument_list|)
expr_stmt|;
comment|// by removing the last entry we have to drop the possible
comment|// local commit information of the current change,
comment|// as we're doing collapsing here and the commit information
comment|// no longer represents an individual commit
name|change
operator|=
operator|new
name|ContentChange
argument_list|(
name|root
argument_list|,
name|CommitInfo
operator|.
name|EMPTY_EXTERNAL
argument_list|)
expr_stmt|;
name|queue
operator|.
name|offer
argument_list|(
name|change
argument_list|)
expr_stmt|;
block|}
comment|// Keep track of the last change added, so we can do the
comment|// compacting of external changes shown above.
name|last
operator|=
name|change
expr_stmt|;
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
name|added
argument_list|(
name|queue
operator|.
name|size
argument_list|()
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
comment|/** FOR TESTING ONLY       * @throws InterruptedException **/
name|boolean
name|waitUntilStopped
parameter_list|(
name|int
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|done
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|unit
operator|.
name|toMillis
argument_list|(
name|timeout
argument_list|)
decl_stmt|;
name|boolean
name|added
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|done
operator|>
name|System
operator|.
name|currentTimeMillis
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|!
name|added
condition|)
block|{
name|added
operator|=
name|queue
operator|.
name|offer
argument_list|(
name|STOP
argument_list|)
expr_stmt|;
if|if
condition|(
name|added
condition|)
block|{
name|currentTask
operator|.
name|onComplete
argument_list|(
name|completionHandler
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|added
operator|&&
name|queue
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
name|wait
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

