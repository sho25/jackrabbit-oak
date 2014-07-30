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
name|jcr
operator|.
name|observation
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
name|checkState
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|OBSERVATION_EVENT_COUNTER
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|OBSERVATION_EVENT_DURATION
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
name|observation
operator|.
name|filter
operator|.
name|VisibleFilter
operator|.
name|VISIBLE_FILTER
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
name|spi
operator|.
name|whiteboard
operator|.
name|WhiteboardUtils
operator|.
name|registerMBean
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
name|spi
operator|.
name|whiteboard
operator|.
name|WhiteboardUtils
operator|.
name|registerObserver
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
name|AtomicLong
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|EventIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|EventListener
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
name|Monitor
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
name|Monitor
operator|.
name|Guard
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
name|api
operator|.
name|jmx
operator|.
name|EventListenerMBean
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
name|commons
operator|.
name|observation
operator|.
name|ListenerTracker
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
name|ContentSession
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
name|namepath
operator|.
name|NamePathMapper
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
name|observation
operator|.
name|CommitRateLimiter
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
name|observation
operator|.
name|filter
operator|.
name|EventFilter
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
name|observation
operator|.
name|filter
operator|.
name|FilterProvider
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
name|observation
operator|.
name|filter
operator|.
name|Filters
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
name|commit
operator|.
name|BackgroundObserver
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
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|Observer
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|whiteboard
operator|.
name|CompositeRegistration
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
name|whiteboard
operator|.
name|Registration
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
name|whiteboard
operator|.
name|Whiteboard
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
name|whiteboard
operator|.
name|WhiteboardExecutor
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
name|StatisticManager
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
name|TimeSeriesMax
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
comment|/**  * A {@code ChangeProcessor} generates observation {@link javax.jcr.observation.Event}s  * based on a {@link FilterProvider filter} and delivers them to an {@link EventListener}.  *<p>  * After instantiation a {@code ChangeProcessor} must be started in order to start  * delivering observation events and stopped to stop doing so.  */
end_comment

begin_class
class|class
name|ChangeProcessor
implements|implements
name|Observer
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
name|ChangeProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Fill ratio of the revision queue at which commits should be delayed      * (conditional of {@code commitRateLimiter} being non {@code null}).      */
specifier|public
specifier|static
specifier|final
name|double
name|DELAY_THRESHOLD
init|=
literal|0.8
decl_stmt|;
comment|/**      * Maximal number of milli seconds a commit is delayed once {@code DELAY_THRESHOLD}      * kicks in.      */
specifier|public
specifier|static
specifier|final
name|int
name|MAX_DELAY
init|=
literal|10000
decl_stmt|;
specifier|private
specifier|final
name|ContentSession
name|contentSession
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|ListenerTracker
name|tracker
decl_stmt|;
specifier|private
specifier|final
name|EventListener
name|eventListener
decl_stmt|;
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|FilterProvider
argument_list|>
name|filterProvider
decl_stmt|;
specifier|private
specifier|final
name|AtomicLong
name|eventCount
decl_stmt|;
specifier|private
specifier|final
name|AtomicLong
name|eventDuration
decl_stmt|;
specifier|private
specifier|final
name|TimeSeriesMax
name|maxQueueLength
decl_stmt|;
specifier|private
specifier|final
name|int
name|queueLength
decl_stmt|;
specifier|private
specifier|final
name|CommitRateLimiter
name|commitRateLimiter
decl_stmt|;
specifier|private
name|CompositeRegistration
name|registration
decl_stmt|;
specifier|private
specifier|volatile
name|NodeState
name|previousRoot
decl_stmt|;
specifier|public
name|ChangeProcessor
parameter_list|(
name|ContentSession
name|contentSession
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|,
name|ListenerTracker
name|tracker
parameter_list|,
name|FilterProvider
name|filters
parameter_list|,
name|StatisticManager
name|statisticManager
parameter_list|,
name|int
name|queueLength
parameter_list|,
name|CommitRateLimiter
name|commitRateLimiter
parameter_list|)
block|{
name|this
operator|.
name|contentSession
operator|=
name|contentSession
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
name|this
operator|.
name|tracker
operator|=
name|tracker
expr_stmt|;
name|eventListener
operator|=
name|tracker
operator|.
name|getTrackedListener
argument_list|()
expr_stmt|;
name|filterProvider
operator|=
operator|new
name|AtomicReference
argument_list|<
name|FilterProvider
argument_list|>
argument_list|(
name|filters
argument_list|)
expr_stmt|;
name|this
operator|.
name|eventCount
operator|=
name|statisticManager
operator|.
name|getCounter
argument_list|(
name|OBSERVATION_EVENT_COUNTER
argument_list|)
expr_stmt|;
name|this
operator|.
name|eventDuration
operator|=
name|statisticManager
operator|.
name|getCounter
argument_list|(
name|OBSERVATION_EVENT_DURATION
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxQueueLength
operator|=
name|statisticManager
operator|.
name|maxQueLengthRecorder
argument_list|()
expr_stmt|;
name|this
operator|.
name|queueLength
operator|=
name|queueLength
expr_stmt|;
name|this
operator|.
name|commitRateLimiter
operator|=
name|commitRateLimiter
expr_stmt|;
block|}
comment|/**      * Set the filter for the events this change processor will generate.      * @param filter      */
specifier|public
name|void
name|setFilterProvider
parameter_list|(
name|FilterProvider
name|filter
parameter_list|)
block|{
name|filterProvider
operator|.
name|set
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
comment|/**      * Start this change processor      * @param whiteboard  the whiteboard instance to used for scheduling individual      *                    runs of this change processor.      * @throws IllegalStateException if started already      */
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|(
name|Whiteboard
name|whiteboard
parameter_list|)
block|{
name|checkState
argument_list|(
name|registration
operator|==
literal|null
argument_list|,
literal|"Change processor started already"
argument_list|)
expr_stmt|;
specifier|final
name|WhiteboardExecutor
name|executor
init|=
operator|new
name|WhiteboardExecutor
argument_list|()
decl_stmt|;
name|executor
operator|.
name|start
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
specifier|final
name|BackgroundObserver
name|observer
init|=
name|createObserver
argument_list|(
name|executor
argument_list|)
decl_stmt|;
name|registration
operator|=
operator|new
name|CompositeRegistration
argument_list|(
name|registerObserver
argument_list|(
name|whiteboard
argument_list|,
name|observer
argument_list|)
argument_list|,
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|EventListenerMBean
operator|.
name|class
argument_list|,
name|tracker
operator|.
name|getListenerMBean
argument_list|()
argument_list|,
literal|"EventListener"
argument_list|,
name|tracker
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Registration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|unregister
parameter_list|()
block|{
name|observer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
operator|new
name|Registration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|unregister
parameter_list|()
block|{
name|executor
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|BackgroundObserver
name|createObserver
parameter_list|(
specifier|final
name|WhiteboardExecutor
name|executor
parameter_list|)
block|{
return|return
operator|new
name|BackgroundObserver
argument_list|(
name|this
argument_list|,
name|executor
argument_list|,
name|queueLength
argument_list|)
block|{
specifier|private
specifier|volatile
name|long
name|delay
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|blocking
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|added
parameter_list|(
name|int
name|queueSize
parameter_list|)
block|{
name|maxQueueLength
operator|.
name|recordValue
argument_list|(
name|queueSize
argument_list|)
expr_stmt|;
if|if
condition|(
name|queueSize
operator|==
name|queueLength
condition|)
block|{
if|if
condition|(
name|commitRateLimiter
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|blocking
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Revision queue is full. Further commits will be blocked."
argument_list|)
expr_stmt|;
block|}
name|commitRateLimiter
operator|.
name|blockCommits
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|blocking
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
name|blocking
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|double
name|fillRatio
init|=
operator|(
name|double
operator|)
name|queueSize
operator|/
name|queueLength
decl_stmt|;
if|if
condition|(
name|fillRatio
operator|>
name|DELAY_THRESHOLD
condition|)
block|{
if|if
condition|(
name|commitRateLimiter
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|delay
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Revision queue is becoming full. Further commits will be delayed."
argument_list|)
expr_stmt|;
block|}
comment|// Linear backoff proportional to the number of items exceeding
comment|// DELAY_THRESHOLD. Offset by 1 to trigger the log message in the
comment|// else branch once the queue falls below DELAY_THRESHOLD again.
name|int
name|newDelay
init|=
literal|1
operator|+
call|(
name|int
call|)
argument_list|(
operator|(
name|fillRatio
operator|-
name|DELAY_THRESHOLD
operator|)
operator|/
operator|(
literal|1
operator|-
name|DELAY_THRESHOLD
operator|)
operator|*
name|MAX_DELAY
argument_list|)
decl_stmt|;
if|if
condition|(
name|newDelay
operator|>
name|delay
condition|)
block|{
name|delay
operator|=
name|newDelay
expr_stmt|;
name|commitRateLimiter
operator|.
name|setDelay
argument_list|(
name|delay
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|commitRateLimiter
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|delay
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Revision queue becoming empty. Unblocking commits"
argument_list|)
expr_stmt|;
name|commitRateLimiter
operator|.
name|setDelay
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|delay
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|blocking
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Revision queue becoming empty. Stop delaying commits."
argument_list|)
expr_stmt|;
name|commitRateLimiter
operator|.
name|unblockCommits
argument_list|()
expr_stmt|;
name|blocking
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
return|;
block|}
specifier|private
specifier|final
name|Monitor
name|runningMonitor
init|=
operator|new
name|Monitor
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|RunningGuard
name|running
init|=
operator|new
name|RunningGuard
argument_list|(
name|runningMonitor
argument_list|)
decl_stmt|;
comment|/**      * Try to stop this change processor if running. This method will wait      * the specified time for a pending event listener to complete. If      * no timeout occurred no further events will be delivered after this      * method returns.      *<p>      * Does nothing if stopped already.      *      * @param timeOut time this method will wait for an executing event      *                listener to complete.      * @param unit    time unit for {@code timeOut}      * @return {@code true} if no time out occurred and this change processor      *         could be stopped, {@code false} otherwise.      * @throws IllegalStateException if not yet started      */
specifier|public
specifier|synchronized
name|boolean
name|stopAndWait
parameter_list|(
name|int
name|timeOut
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
name|checkState
argument_list|(
name|registration
operator|!=
literal|null
argument_list|,
literal|"Change processor not started"
argument_list|)
expr_stmt|;
if|if
condition|(
name|running
operator|.
name|stop
argument_list|()
condition|)
block|{
if|if
condition|(
name|runningMonitor
operator|.
name|enter
argument_list|(
name|timeOut
argument_list|,
name|unit
argument_list|)
condition|)
block|{
name|registration
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|runningMonitor
operator|.
name|leave
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// Timed out
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
comment|// Stopped already
return|return
literal|true
return|;
block|}
block|}
comment|/**      * Stop this change processor after all pending events have been      * delivered. In contrast to {@link #stopAndWait(int, java.util.concurrent.TimeUnit)}      * this method returns immediately without waiting for pending listeners to      * complete.      */
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
name|checkState
argument_list|(
name|registration
operator|!=
literal|null
argument_list|,
literal|"Change processor not started"
argument_list|)
expr_stmt|;
if|if
condition|(
name|running
operator|.
name|stop
argument_list|()
condition|)
block|{
name|registration
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|runningMonitor
operator|.
name|leave
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
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
if|if
condition|(
name|previousRoot
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|FilterProvider
name|provider
init|=
name|filterProvider
operator|.
name|get
argument_list|()
decl_stmt|;
comment|// FIXME don't rely on toString for session id
if|if
condition|(
name|provider
operator|.
name|includeCommit
argument_list|(
name|contentSession
operator|.
name|toString
argument_list|()
argument_list|,
name|info
argument_list|)
condition|)
block|{
name|EventFilter
name|filter
init|=
name|provider
operator|.
name|getFilter
argument_list|(
name|previousRoot
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|EventIterator
name|events
init|=
operator|new
name|EventQueue
argument_list|(
name|namePathMapper
argument_list|,
name|info
argument_list|,
name|previousRoot
argument_list|,
name|root
argument_list|,
name|provider
operator|.
name|getSubTrees
argument_list|()
argument_list|,
name|Filters
operator|.
name|all
argument_list|(
name|filter
argument_list|,
name|VISIBLE_FILTER
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|events
operator|.
name|hasNext
argument_list|()
operator|&&
name|runningMonitor
operator|.
name|enterIf
argument_list|(
name|running
argument_list|)
condition|)
block|{
try|try
block|{
name|CountingIterator
name|countingEvents
init|=
operator|new
name|CountingIterator
argument_list|(
name|events
argument_list|)
decl_stmt|;
name|eventListener
operator|.
name|onEvent
argument_list|(
name|countingEvents
argument_list|)
expr_stmt|;
name|countingEvents
operator|.
name|updateCounters
argument_list|(
name|eventCount
argument_list|,
name|eventDuration
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|runningMonitor
operator|.
name|leave
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error while dispatching observation events"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|previousRoot
operator|=
name|root
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|CountingIterator
implements|implements
name|EventIterator
block|{
specifier|private
specifier|final
name|long
name|t0
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|EventIterator
name|events
decl_stmt|;
specifier|private
name|long
name|eventCount
decl_stmt|;
specifier|private
name|long
name|sysTime
decl_stmt|;
specifier|public
name|CountingIterator
parameter_list|(
name|EventIterator
name|events
parameter_list|)
block|{
name|this
operator|.
name|events
operator|=
name|events
expr_stmt|;
block|}
specifier|public
name|void
name|updateCounters
parameter_list|(
name|AtomicLong
name|eventCount
parameter_list|,
name|AtomicLong
name|eventDuration
parameter_list|)
block|{
name|checkState
argument_list|(
name|this
operator|.
name|eventCount
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|eventCount
operator|.
name|addAndGet
argument_list|(
name|this
operator|.
name|eventCount
argument_list|)
expr_stmt|;
name|eventDuration
operator|.
name|addAndGet
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|t0
operator|-
name|sysTime
argument_list|)
expr_stmt|;
name|this
operator|.
name|eventCount
operator|=
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Event
name|next
parameter_list|()
block|{
if|if
condition|(
name|eventCount
operator|==
operator|-
literal|1
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Access to EventIterator outside the onEvent callback detected. This will "
operator|+
literal|"cause observation related values in RepositoryStatistics to become unreliable."
argument_list|)
expr_stmt|;
name|eventCount
operator|=
operator|-
literal|2
expr_stmt|;
block|}
name|long
name|t0
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|events
operator|.
name|nextEvent
argument_list|()
return|;
block|}
finally|finally
block|{
name|eventCount
operator|++
expr_stmt|;
name|sysTime
operator|+=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|t0
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
name|long
name|t0
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|events
operator|.
name|hasNext
argument_list|()
return|;
block|}
finally|finally
block|{
name|sysTime
operator|+=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|t0
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Event
name|nextEvent
parameter_list|()
block|{
return|return
name|next
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|skip
parameter_list|(
name|long
name|skipNum
parameter_list|)
block|{
name|long
name|t0
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
try|try
block|{
name|events
operator|.
name|skip
argument_list|(
name|skipNum
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|sysTime
operator|+=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|t0
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|events
operator|.
name|getSize
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPosition
parameter_list|()
block|{
return|return
name|events
operator|.
name|getPosition
argument_list|()
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
specifier|private
specifier|static
class|class
name|RunningGuard
extends|extends
name|Guard
block|{
specifier|private
name|boolean
name|stopped
decl_stmt|;
specifier|public
name|RunningGuard
parameter_list|(
name|Monitor
name|monitor
parameter_list|)
block|{
name|super
argument_list|(
name|monitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisfied
parameter_list|()
block|{
return|return
operator|!
name|stopped
return|;
block|}
comment|/**          * @return  {@code true} if this call set this guard to stopped,          *          {@code false} if another call set this guard to stopped before.          */
specifier|public
name|boolean
name|stop
parameter_list|()
block|{
name|boolean
name|wasStopped
init|=
name|stopped
decl_stmt|;
name|stopped
operator|=
literal|true
expr_stmt|;
return|return
operator|!
name|wasStopped
return|;
block|}
block|}
block|}
end_class

end_unit

