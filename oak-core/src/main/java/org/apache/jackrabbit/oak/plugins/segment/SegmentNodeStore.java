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
name|io
operator|.
name|InputStream
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
name|Semaphore
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|CommitHook
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
name|Observable
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
name|ConflictAnnotatingRebaseDiff
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
name|spi
operator|.
name|state
operator|.
name|NodeStore
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
name|Objects
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentNodeStore
implements|implements
name|NodeStore
implements|,
name|Observable
block|{
specifier|static
specifier|final
name|String
name|ROOT
init|=
literal|"root"
decl_stmt|;
specifier|private
specifier|final
name|SegmentStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|Journal
name|journal
decl_stmt|;
specifier|private
specifier|final
name|ChangeDispatcher
name|changeDispatcher
decl_stmt|;
specifier|volatile
name|SegmentNodeState
name|head
decl_stmt|;
comment|/**      * Semaphore that controls access to the {@link #head} variable.      * Only a single local commit is allowed at a time. When such      * a commit is in progress, no external updates will be seen.      */
specifier|private
specifier|final
name|Semaphore
name|commitSemaphore
init|=
operator|new
name|Semaphore
argument_list|(
literal|1
argument_list|)
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
specifier|public
name|SegmentNodeStore
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|String
name|journal
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|journal
operator|=
name|store
operator|.
name|getJournal
argument_list|(
name|journal
argument_list|)
expr_stmt|;
name|this
operator|.
name|head
operator|=
operator|new
name|SegmentNodeState
argument_list|(
name|store
operator|.
name|getWriter
argument_list|()
operator|.
name|getDummySegment
argument_list|()
argument_list|,
name|this
operator|.
name|journal
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|changeDispatcher
operator|=
operator|new
name|ChangeDispatcher
argument_list|(
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SegmentNodeStore
parameter_list|(
name|SegmentStore
name|store
parameter_list|)
block|{
name|this
argument_list|(
name|store
argument_list|,
literal|"root"
argument_list|)
expr_stmt|;
block|}
name|void
name|setMaximumBackoff
parameter_list|(
name|long
name|max
parameter_list|)
block|{
name|this
operator|.
name|maximumBackoff
operator|=
name|max
expr_stmt|;
block|}
comment|/**      * Refreshes the head state. Does nothing if a concurrent local commit is      * in progress, as that commit will automatically refresh the head state.      */
specifier|private
name|void
name|refreshHead
parameter_list|()
block|{
name|RecordId
name|id
init|=
name|journal
operator|.
name|getHead
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|id
operator|.
name|equals
argument_list|(
name|head
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
name|head
operator|=
operator|new
name|SegmentNodeState
argument_list|(
name|store
operator|.
name|getWriter
argument_list|()
operator|.
name|getDummySegment
argument_list|()
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|changeDispatcher
operator|.
name|contentChanged
argument_list|(
name|head
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
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
return|return
name|changeDispatcher
operator|.
name|addObserver
argument_list|(
name|observer
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getRoot
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
argument_list|()
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
operator|new
name|SegmentRootState
argument_list|(
name|head
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|merge
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|,
annotation|@
name|Nonnull
name|CommitHook
name|commitHook
parameter_list|,
annotation|@
name|Nullable
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|checkNotNull
argument_list|(
name|commitHook
argument_list|)
expr_stmt|;
name|NodeState
name|base
init|=
name|builder
operator|.
name|getBaseState
argument_list|()
decl_stmt|;
name|checkArgument
argument_list|(
name|store
operator|.
name|isInstance
argument_list|(
name|base
argument_list|,
name|SegmentRootState
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|root
init|=
operator|(
operator|(
name|SegmentRootState
operator|)
name|base
operator|)
operator|.
name|getRootState
argument_list|()
decl_stmt|;
try|try
block|{
name|commitSemaphore
operator|.
name|acquire
argument_list|()
expr_stmt|;
try|try
block|{
name|Commit
name|commit
init|=
operator|new
name|Commit
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|commitHook
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|NodeState
name|merged
init|=
name|commit
operator|.
name|execute
argument_list|()
decl_stmt|;
operator|(
operator|(
name|SegmentNodeBuilder
operator|)
name|builder
operator|)
operator|.
name|reset
argument_list|(
name|merged
argument_list|)
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
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|rebase
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|builder
operator|instanceof
name|SegmentNodeBuilder
argument_list|)
expr_stmt|;
name|NodeState
name|newBase
init|=
name|getRoot
argument_list|()
decl_stmt|;
name|NodeState
name|oldBase
init|=
name|builder
operator|.
name|getBaseState
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|fastEquals
argument_list|(
name|oldBase
argument_list|,
name|newBase
argument_list|)
condition|)
block|{
name|NodeState
name|head
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
operator|(
operator|(
name|SegmentNodeBuilder
operator|)
name|builder
operator|)
operator|.
name|reset
argument_list|(
name|newBase
argument_list|)
expr_stmt|;
name|head
operator|.
name|compareAgainstBaseState
argument_list|(
name|oldBase
argument_list|,
operator|new
name|ConflictAnnotatingRebaseDiff
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
specifier|private
name|boolean
name|fastEquals
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
return|return
name|store
operator|.
name|isInstance
argument_list|(
name|a
argument_list|,
name|Record
operator|.
name|class
argument_list|)
operator|&&
name|store
operator|.
name|isInstance
argument_list|(
name|b
argument_list|,
name|Record
operator|.
name|class
argument_list|)
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
operator|(
operator|(
name|Record
operator|)
name|a
operator|)
operator|.
name|getRecordId
argument_list|()
argument_list|,
operator|(
operator|(
name|Record
operator|)
name|b
operator|)
operator|.
name|getRecordId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|reset
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|builder
operator|instanceof
name|SegmentNodeBuilder
argument_list|)
expr_stmt|;
name|NodeState
name|state
init|=
name|getRoot
argument_list|()
decl_stmt|;
operator|(
operator|(
name|SegmentNodeBuilder
operator|)
name|builder
operator|)
operator|.
name|reset
argument_list|(
name|state
argument_list|)
expr_stmt|;
return|return
name|state
return|;
block|}
annotation|@
name|Override
specifier|public
name|Blob
name|createBlob
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|store
operator|.
name|getWriter
argument_list|()
operator|.
name|writeStream
argument_list|(
name|stream
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
specifier|synchronized
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|lifetime
operator|>
literal|0
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
argument_list|()
expr_stmt|;
name|SegmentNodeState
name|ns
init|=
name|head
decl_stmt|;
name|RecordId
name|ri
init|=
name|head
operator|.
name|getRecordId
argument_list|()
decl_stmt|;
name|SegmentNodeBuilder
name|builder
init|=
name|ns
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
name|name
argument_list|)
decl_stmt|;
name|cp
operator|.
name|setProperty
argument_list|(
literal|"timestamp"
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|lifetime
argument_list|)
expr_stmt|;
name|cp
operator|.
name|setChildNode
argument_list|(
name|ROOT
argument_list|,
name|ns
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|journal
operator|.
name|setHead
argument_list|(
name|ri
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
name|refreshHead
argument_list|()
expr_stmt|;
return|return
name|name
return|;
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
name|name
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
specifier|synchronized
name|NodeState
name|retrieve
parameter_list|(
annotation|@
name|Nonnull
name|String
name|checkpoint
parameter_list|)
block|{
name|NodeState
name|cp
init|=
name|head
operator|.
name|getChildNode
argument_list|(
name|checkpoint
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|ROOT
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
return|return
name|cp
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
class|class
name|Commit
block|{
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
name|SegmentNodeState
name|base
decl_stmt|;
specifier|private
name|SegmentNodeState
name|head
decl_stmt|;
specifier|private
specifier|final
name|CommitHook
name|hook
decl_stmt|;
specifier|private
specifier|final
name|CommitInfo
name|info
decl_stmt|;
name|Commit
parameter_list|(
annotation|@
name|Nonnull
name|SegmentNodeState
name|base
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|head
parameter_list|,
annotation|@
name|Nonnull
name|CommitHook
name|hook
parameter_list|,
annotation|@
name|Nullable
name|CommitInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|checkNotNull
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|SegmentNodeBuilder
name|builder
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
name|ROOT
argument_list|,
name|checkNotNull
argument_list|(
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|head
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|this
operator|.
name|hook
operator|=
name|checkNotNull
argument_list|(
name|hook
argument_list|)
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
specifier|private
name|boolean
name|setHead
parameter_list|(
name|SegmentNodeState
name|base
parameter_list|,
name|SegmentNodeState
name|head
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
name|refreshHead
argument_list|()
expr_stmt|;
if|if
condition|(
name|journal
operator|.
name|setHead
argument_list|(
name|base
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|head
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
name|this
operator|.
name|head
operator|=
name|head
expr_stmt|;
name|changeDispatcher
operator|.
name|contentChanged
argument_list|(
name|head
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
argument_list|()
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
specifier|private
name|void
name|rebase
parameter_list|()
block|{
name|SegmentNodeState
name|newBase
init|=
name|SegmentNodeStore
operator|.
name|this
operator|.
name|head
decl_stmt|;
if|if
condition|(
operator|!
name|base
operator|.
name|getRecordId
argument_list|()
operator|.
name|equals
argument_list|(
name|newBase
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
name|NodeBuilder
name|builder
init|=
name|newBase
operator|.
name|builder
argument_list|()
decl_stmt|;
name|head
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|,
operator|new
name|ConflictAnnotatingRebaseDiff
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|ROOT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|base
operator|=
name|newBase
expr_stmt|;
name|head
operator|=
name|store
operator|.
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
expr_stmt|;
block|}
block|}
specifier|private
name|long
name|optimisticMerge
parameter_list|(
name|CommitHook
name|hook
parameter_list|,
name|CommitInfo
name|info
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
name|SegmentNodeState
name|originalBase
init|=
name|base
decl_stmt|;
name|SegmentNodeState
name|originalHead
init|=
name|head
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
name|rebase
argument_list|()
expr_stmt|;
comment|// rebase to latest head, a no-op if already there
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|base
operator|.
name|hasProperty
argument_list|(
literal|"token"
argument_list|)
operator|&&
name|base
operator|.
name|getLong
argument_list|(
literal|"timeout"
argument_list|)
operator|>=
name|System
operator|.
name|currentTimeMillis
argument_list|()
condition|)
block|{
comment|// someone else has a pessimistic lock on the journal,
comment|// so we should not try to commit anything
block|}
else|else
block|{
comment|// apply commit hooks on the rebased changes
name|NodeBuilder
name|builder
init|=
name|head
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
name|ROOT
argument_list|,
name|hook
operator|.
name|processCommit
argument_list|(
name|base
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|,
name|head
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|newHead
init|=
name|store
operator|.
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
decl_stmt|;
comment|// use optimistic locking to update the journal
if|if
condition|(
name|setHead
argument_list|(
name|base
argument_list|,
name|newHead
argument_list|,
name|info
argument_list|)
condition|)
block|{
name|base
operator|=
name|newHead
expr_stmt|;
name|head
operator|=
name|newHead
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|// someone else was faster, so restore state and retry later
name|base
operator|=
name|originalBase
expr_stmt|;
name|head
operator|=
name|originalHead
expr_stmt|;
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
name|CommitHook
name|hook
parameter_list|,
name|long
name|timeout
parameter_list|,
name|CommitInfo
name|info
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
name|SegmentNodeState
name|before
init|=
name|head
decl_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|before
operator|.
name|hasProperty
argument_list|(
literal|"token"
argument_list|)
operator|&&
name|before
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
name|before
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
name|NodeBuilder
name|builder
init|=
name|before
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
name|SegmentNodeState
name|after
init|=
name|store
operator|.
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
decl_stmt|;
if|if
condition|(
name|setHead
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|info
argument_list|)
condition|)
block|{
name|SegmentNodeState
name|originalBase
init|=
name|base
decl_stmt|;
name|SegmentNodeState
name|originalHead
init|=
name|head
decl_stmt|;
comment|// lock acquired; rebase, apply commit hooks, and unlock
name|rebase
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
name|ROOT
argument_list|,
name|hook
operator|.
name|processCommit
argument_list|(
name|base
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|,
name|head
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|)
argument_list|)
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
name|SegmentNodeState
name|newHead
init|=
name|store
operator|.
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
decl_stmt|;
if|if
condition|(
name|setHead
argument_list|(
name|after
argument_list|,
name|newHead
argument_list|,
name|info
argument_list|)
condition|)
block|{
name|base
operator|=
name|newHead
expr_stmt|;
name|head
operator|=
name|newHead
expr_stmt|;
return|return;
block|}
else|else
block|{
comment|// something else happened, perhaps a timeout, so
comment|// undo the previous rebase and try again
name|base
operator|=
name|originalBase
expr_stmt|;
name|head
operator|=
name|originalHead
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Nonnull
name|SegmentRootState
name|execute
parameter_list|()
throws|throws
name|CommitFailedException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|base
operator|!=
name|head
condition|)
block|{
name|long
name|timeout
init|=
name|optimisticMerge
argument_list|(
name|hook
argument_list|,
name|info
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
name|hook
argument_list|,
name|timeout
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|SegmentRootState
argument_list|(
name|head
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

