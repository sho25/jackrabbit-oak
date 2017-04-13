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
name|scheduler
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
name|java
operator|.
name|lang
operator|.
name|System
operator|.
name|currentTimeMillis
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Thread
operator|.
name|currentThread
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
name|MILLISECONDS
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
name|NANOSECONDS
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
name|api
operator|.
name|Type
operator|.
name|LONG
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|Semaphore
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
name|CommitFailedException
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
name|PropertyState
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
name|Revisions
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
name|SegmentNodeBuilder
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
name|SegmentNodeState
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
name|SegmentNodeStoreStats
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
name|SegmentOverflowException
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
name|SegmentReader
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
name|ChangeDispatcher
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
name|LockBasedScheduler
implements|implements
name|Scheduler
block|{
specifier|private
specifier|static
specifier|final
name|Closeable
name|NOOP
init|=
operator|new
name|Closeable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// This method was intentionally left blank.
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
class|class
name|LockBasedSchedulerBuilder
block|{
annotation|@
name|Nonnull
specifier|private
specifier|final
name|SegmentReader
name|reader
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Revisions
name|revisions
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
name|StatisticsProvider
name|statsProvider
init|=
name|StatisticsProvider
operator|.
name|NOOP
decl_stmt|;
specifier|private
name|boolean
name|dispatchChanges
init|=
literal|true
decl_stmt|;
specifier|private
name|long
name|maximumBackoff
init|=
name|MILLISECONDS
operator|.
name|convert
argument_list|(
literal|10
argument_list|,
name|SECONDS
argument_list|)
decl_stmt|;
specifier|private
name|LockBasedSchedulerBuilder
parameter_list|(
annotation|@
name|Nonnull
name|Revisions
name|revisions
parameter_list|,
annotation|@
name|Nonnull
name|SegmentReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|revisions
operator|=
name|revisions
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
comment|/**          * {@link StatisticsProvider} for collecting statistics related to SegmentStore          * @param statisticsProvider          * @return this instance          */
annotation|@
name|Nonnull
specifier|public
name|LockBasedSchedulerBuilder
name|withStatisticsProvider
parameter_list|(
annotation|@
name|Nonnull
name|StatisticsProvider
name|statisticsProvider
parameter_list|)
block|{
name|this
operator|.
name|statsProvider
operator|=
name|checkNotNull
argument_list|(
name|statisticsProvider
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|LockBasedSchedulerBuilder
name|dispatchChanges
parameter_list|(
name|boolean
name|dispatchChanges
parameter_list|)
block|{
name|this
operator|.
name|dispatchChanges
operator|=
name|dispatchChanges
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|LockBasedSchedulerBuilder
name|withMaximumBackoff
parameter_list|(
name|long
name|maximumBackoff
parameter_list|)
block|{
name|this
operator|.
name|maximumBackoff
operator|=
name|maximumBackoff
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|LockBasedScheduler
name|build
parameter_list|()
block|{
return|return
operator|new
name|LockBasedScheduler
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|LockBasedSchedulerBuilder
name|builder
parameter_list|(
annotation|@
name|Nonnull
name|Revisions
name|revisions
parameter_list|,
annotation|@
name|Nonnull
name|SegmentReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|LockBasedSchedulerBuilder
argument_list|(
name|checkNotNull
argument_list|(
name|revisions
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|reader
argument_list|)
argument_list|)
return|;
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
name|LockBasedScheduler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Flag controlling the commit lock fairness      */
specifier|private
specifier|static
specifier|final
name|boolean
name|COMMIT_FAIR_LOCK
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"oak.segmentNodeStore.commitFairLock"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      * Sets the number of seconds to wait for the attempt to grab the lock to      * create a checkpoint      */
specifier|private
specifier|final
name|int
name|checkpointsLockWaitTime
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.checkpoints.lockWaitTime"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|String
name|ROOT
init|=
literal|"root"
decl_stmt|;
comment|/**      * Semaphore that controls access to the {@link #head} variable. Only a      * single local commit is allowed at a time. When such a commit is in      * progress, no external updates will be seen.      */
specifier|private
specifier|final
name|Semaphore
name|commitSemaphore
init|=
operator|new
name|Semaphore
argument_list|(
literal|1
argument_list|,
name|COMMIT_FAIR_LOCK
argument_list|)
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|SegmentReader
name|reader
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Revisions
name|revisions
decl_stmt|;
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|SegmentNodeState
argument_list|>
name|head
decl_stmt|;
specifier|private
specifier|final
name|ChangeDispatcher
name|changeDispatcher
decl_stmt|;
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SegmentNodeStoreStats
name|stats
decl_stmt|;
specifier|private
specifier|final
name|long
name|maximumBackoff
decl_stmt|;
specifier|public
name|LockBasedScheduler
parameter_list|(
name|LockBasedSchedulerBuilder
name|builder
parameter_list|)
block|{
if|if
condition|(
name|COMMIT_FAIR_LOCK
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Initializing SegmentNodeStore with the commitFairLock option enabled."
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|reader
operator|=
name|builder
operator|.
name|reader
expr_stmt|;
name|this
operator|.
name|revisions
operator|=
name|builder
operator|.
name|revisions
expr_stmt|;
name|this
operator|.
name|head
operator|=
operator|new
name|AtomicReference
argument_list|<
name|SegmentNodeState
argument_list|>
argument_list|(
name|reader
operator|.
name|readHeadState
argument_list|(
name|revisions
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|builder
operator|.
name|dispatchChanges
condition|)
block|{
name|this
operator|.
name|changeDispatcher
operator|=
operator|new
name|ChangeDispatcher
argument_list|(
name|getHeadNodeState
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|changeDispatcher
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|stats
operator|=
operator|new
name|SegmentNodeStoreStats
argument_list|(
name|builder
operator|.
name|statsProvider
argument_list|)
expr_stmt|;
name|this
operator|.
name|maximumBackoff
operator|=
name|builder
operator|.
name|maximumBackoff
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Closeable
name|addObserver
parameter_list|(
name|Observer
name|observer
parameter_list|)
block|{
if|if
condition|(
name|changeDispatcher
operator|!=
literal|null
condition|)
block|{
return|return
name|changeDispatcher
operator|.
name|addObserver
argument_list|(
name|observer
argument_list|)
return|;
block|}
return|return
name|NOOP
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getHeadNodeState
parameter_list|()
block|{
if|if
condition|(
name|commitSemaphore
operator|.
name|tryAcquire
argument_list|()
condition|)
block|{
try|try
block|{
name|refreshHead
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|commitSemaphore
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|head
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * Refreshes the head state. Should only be called while holding a permit      * from the {@link #commitSemaphore}.      *       * @param dispatchChanges      *            if set to true the changes would also be dispatched      */
specifier|private
name|void
name|refreshHead
parameter_list|(
name|boolean
name|dispatchChanges
parameter_list|)
block|{
name|SegmentNodeState
name|state
init|=
name|reader
operator|.
name|readHeadState
argument_list|(
name|revisions
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|state
operator|.
name|getRecordId
argument_list|()
operator|.
name|equals
argument_list|(
name|head
operator|.
name|get
argument_list|()
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
name|head
operator|.
name|set
argument_list|(
name|state
argument_list|)
expr_stmt|;
if|if
condition|(
name|dispatchChanges
condition|)
block|{
name|contentChanged
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|,
name|CommitInfo
operator|.
name|EMPTY_EXTERNAL
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|contentChanged
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|changeDispatcher
operator|!=
literal|null
condition|)
block|{
name|changeDispatcher
operator|.
name|contentChanged
argument_list|(
name|root
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|schedule
parameter_list|(
annotation|@
name|Nonnull
name|Commit
name|commit
parameter_list|,
name|SchedulerOption
modifier|...
name|schedulingOptions
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|boolean
name|queued
init|=
literal|false
decl_stmt|;
try|try
block|{
name|long
name|queuedTime
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|commitSemaphore
operator|.
name|availablePermits
argument_list|()
operator|<
literal|1
condition|)
block|{
name|queuedTime
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
name|stats
operator|.
name|onCommitQueued
argument_list|()
expr_stmt|;
name|queued
operator|=
literal|true
expr_stmt|;
block|}
name|commitSemaphore
operator|.
name|acquire
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|queued
condition|)
block|{
name|long
name|dequeuedTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|stats
operator|.
name|dequeuedAfter
argument_list|(
name|dequeuedTime
operator|-
name|queuedTime
argument_list|)
expr_stmt|;
name|stats
operator|.
name|onCommitDequeued
argument_list|()
expr_stmt|;
block|}
name|long
name|beforeCommitTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|SegmentNodeState
name|merged
init|=
operator|(
name|SegmentNodeState
operator|)
name|execute
argument_list|(
name|commit
argument_list|)
decl_stmt|;
name|commit
operator|.
name|applied
argument_list|(
name|merged
argument_list|)
expr_stmt|;
name|long
name|afterCommitTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|stats
operator|.
name|committedAfter
argument_list|(
name|afterCommitTime
operator|-
name|beforeCommitTime
argument_list|)
expr_stmt|;
name|stats
operator|.
name|onCommit
argument_list|()
expr_stmt|;
return|return
name|merged
return|;
block|}
finally|finally
block|{
name|commitSemaphore
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Segment"
argument_list|,
literal|2
argument_list|,
literal|"Merge interrupted"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SegmentOverflowException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Segment"
argument_list|,
literal|3
argument_list|,
literal|"Merge failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|NodeState
name|execute
parameter_list|(
name|Commit
name|commit
parameter_list|)
throws|throws
name|CommitFailedException
throws|,
name|InterruptedException
block|{
comment|// only do the merge if there are some changes to commit
if|if
condition|(
name|commit
operator|.
name|hasChanges
argument_list|()
condition|)
block|{
name|long
name|timeout
init|=
name|optimisticMerge
argument_list|(
name|commit
argument_list|)
decl_stmt|;
if|if
condition|(
name|timeout
operator|>=
literal|0
condition|)
block|{
name|pessimisticMerge
argument_list|(
name|commit
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|head
operator|.
name|get
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
return|;
block|}
specifier|private
name|long
name|optimisticMerge
parameter_list|(
name|Commit
name|commit
parameter_list|)
throws|throws
name|CommitFailedException
throws|,
name|InterruptedException
block|{
name|long
name|timeout
init|=
literal|1
decl_stmt|;
comment|// use exponential backoff in case of concurrent commits
for|for
control|(
name|long
name|backoff
init|=
literal|1
init|;
name|backoff
operator|<
name|maximumBackoff
condition|;
name|backoff
operator|*=
literal|2
control|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|refreshHead
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|state
init|=
name|head
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|hasProperty
argument_list|(
literal|"token"
argument_list|)
operator|&&
name|state
operator|.
name|getLong
argument_list|(
literal|"timeout"
argument_list|)
operator|>=
name|currentTimeMillis
argument_list|()
condition|)
block|{
comment|// someone else has a pessimistic lock on the journal,
comment|// so we should not try to commit anything yet
block|}
else|else
block|{
comment|// use optimistic locking to update the journal
if|if
condition|(
name|setHead
argument_list|(
name|state
argument_list|,
name|commit
operator|.
name|apply
argument_list|(
name|state
argument_list|)
argument_list|,
name|commit
operator|.
name|info
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|// someone else was faster, so wait a while and retry later
name|Thread
operator|.
name|sleep
argument_list|(
name|backoff
argument_list|,
name|random
operator|.
name|nextInt
argument_list|(
literal|1000000
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|stop
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|stop
operator|-
name|start
operator|>
name|timeout
condition|)
block|{
name|timeout
operator|=
name|stop
operator|-
name|start
expr_stmt|;
block|}
block|}
return|return
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|timeout
argument_list|,
name|NANOSECONDS
argument_list|)
return|;
block|}
specifier|private
name|void
name|pessimisticMerge
parameter_list|(
name|Commit
name|commit
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|CommitFailedException
throws|,
name|InterruptedException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|long
name|now
init|=
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|SegmentNodeState
name|state
init|=
name|head
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|hasProperty
argument_list|(
literal|"token"
argument_list|)
operator|&&
name|state
operator|.
name|getLong
argument_list|(
literal|"timeout"
argument_list|)
operator|>=
name|now
condition|)
block|{
comment|// locked by someone else, wait until unlocked or expired
name|Thread
operator|.
name|sleep
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|state
operator|.
name|getLong
argument_list|(
literal|"timeout"
argument_list|)
operator|-
name|now
argument_list|,
literal|1000
argument_list|)
argument_list|,
name|random
operator|.
name|nextInt
argument_list|(
literal|1000000
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// attempt to acquire the lock
name|SegmentNodeBuilder
name|builder
init|=
name|state
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"token"
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"timeout"
argument_list|,
name|now
operator|+
name|timeout
argument_list|)
expr_stmt|;
if|if
condition|(
name|setHead
argument_list|(
name|state
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|commit
operator|.
name|info
argument_list|()
argument_list|)
condition|)
block|{
comment|// lock acquired; rebase, apply commit hooks, and unlock
name|builder
operator|=
name|commit
operator|.
name|apply
argument_list|(
name|state
argument_list|)
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|removeProperty
argument_list|(
literal|"token"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|removeProperty
argument_list|(
literal|"timeout"
argument_list|)
expr_stmt|;
comment|// complete the commit
if|if
condition|(
name|setHead
argument_list|(
name|state
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|commit
operator|.
name|info
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
block|}
block|}
block|}
specifier|private
name|boolean
name|setHead
parameter_list|(
name|SegmentNodeState
name|before
parameter_list|,
name|SegmentNodeState
name|after
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
name|refreshHead
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|revisions
operator|.
name|setHead
argument_list|(
name|before
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|after
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
name|head
operator|.
name|set
argument_list|(
name|after
argument_list|)
expr_stmt|;
name|contentChanged
argument_list|(
name|after
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|refreshHead
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|lifetime
operator|>
literal|0
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
block|{
name|CPCreator
name|cpc
init|=
operator|new
name|CPCreator
argument_list|(
name|name
argument_list|,
name|lifetime
argument_list|,
name|properties
argument_list|)
decl_stmt|;
if|if
condition|(
name|commitSemaphore
operator|.
name|tryAcquire
argument_list|(
name|checkpointsLockWaitTime
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
try|try
block|{
if|if
condition|(
name|cpc
operator|.
name|call
argument_list|()
condition|)
block|{
return|return
name|name
return|;
block|}
block|}
finally|finally
block|{
comment|// Explicitly give up reference to the previous root state
comment|// otherwise they would block cleanup. See OAK-3347
name|refreshHead
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|commitSemaphore
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to create checkpoint {} in {} seconds."
argument_list|,
name|name
argument_list|,
name|checkpointsLockWaitTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"Failed to create checkpoint {}."
argument_list|,
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to create checkpoint {}."
argument_list|,
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|removeCheckpoint
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
comment|// try 5 times
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|commitSemaphore
operator|.
name|tryAcquire
argument_list|()
condition|)
block|{
try|try
block|{
name|refreshHead
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|state
init|=
name|head
operator|.
name|get
argument_list|()
decl_stmt|;
name|SegmentNodeBuilder
name|builder
init|=
name|state
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|cp
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"checkpoints"
argument_list|)
operator|.
name|child
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|cp
operator|.
name|exists
argument_list|()
condition|)
block|{
name|cp
operator|.
name|remove
argument_list|()
expr_stmt|;
name|SegmentNodeState
name|newState
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
if|if
condition|(
name|revisions
operator|.
name|setHead
argument_list|(
name|state
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|newState
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
name|refreshHead
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
finally|finally
block|{
name|commitSemaphore
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
specifier|final
class|class
name|CPCreator
implements|implements
name|Callable
argument_list|<
name|Boolean
argument_list|>
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|long
name|lifetime
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
decl_stmt|;
name|CPCreator
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|lifetime
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|lifetime
operator|=
name|lifetime
expr_stmt|;
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|refreshHead
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|state
init|=
name|head
operator|.
name|get
argument_list|()
decl_stmt|;
name|SegmentNodeBuilder
name|builder
init|=
name|state
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|checkpoints
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"checkpoints"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|n
range|:
name|checkpoints
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
name|NodeBuilder
name|cp
init|=
name|checkpoints
operator|.
name|getChildNode
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|PropertyState
name|ts
init|=
name|cp
operator|.
name|getProperty
argument_list|(
literal|"timestamp"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ts
operator|==
literal|null
operator|||
name|ts
operator|.
name|getType
argument_list|()
operator|!=
name|LONG
operator|||
name|now
operator|>
name|ts
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|)
condition|)
block|{
name|cp
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
name|NodeBuilder
name|cp
init|=
name|checkpoints
operator|.
name|child
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|Long
operator|.
name|MAX_VALUE
operator|-
name|now
operator|>
name|lifetime
condition|)
block|{
name|cp
operator|.
name|setProperty
argument_list|(
literal|"timestamp"
argument_list|,
name|now
operator|+
name|lifetime
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cp
operator|.
name|setProperty
argument_list|(
literal|"timestamp"
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
name|cp
operator|.
name|setProperty
argument_list|(
literal|"created"
argument_list|,
name|now
argument_list|)
expr_stmt|;
name|NodeBuilder
name|props
init|=
name|cp
operator|.
name|setChildNode
argument_list|(
literal|"properties"
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|p
range|:
name|properties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
name|p
operator|.
name|getKey
argument_list|()
argument_list|,
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|cp
operator|.
name|setChildNode
argument_list|(
name|ROOT
argument_list|,
name|state
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|newState
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
if|if
condition|(
name|revisions
operator|.
name|setHead
argument_list|(
name|state
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|newState
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
name|refreshHead
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

