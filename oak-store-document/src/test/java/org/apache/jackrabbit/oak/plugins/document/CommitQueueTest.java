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
name|document
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
name|Collections
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
name|Random
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
name|EmptyHook
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
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|collect
operator|.
name|ImmutableSet
operator|.
name|of
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
name|union
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|synchronizedList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Tests for {@link CommitQueue}.  */
end_comment

begin_class
specifier|public
class|class
name|CommitQueueTest
block|{
annotation|@
name|Rule
specifier|public
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
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
name|CommitQueueTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_WRITERS
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|COMMITS_PER_WRITER
init|=
literal|100
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Exception
argument_list|>
name|exceptions
init|=
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Exception
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|concurrentCommits
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|DocumentNodeStore
name|store
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|AtomicBoolean
name|running
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Closeable
name|observer
init|=
name|store
operator|.
name|addObserver
argument_list|(
operator|new
name|Observer
argument_list|()
block|{
specifier|private
name|RevisionVector
name|before
init|=
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|store
operator|.
name|getClusterId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
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
name|DocumentNodeState
name|after
init|=
operator|(
name|DocumentNodeState
operator|)
name|root
decl_stmt|;
name|RevisionVector
name|r
init|=
name|after
operator|.
name|getRootRevision
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"seen: {}"
argument_list|,
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|compareTo
argument_list|(
name|before
argument_list|)
operator|<
literal|0
condition|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
operator|new
name|Exception
argument_list|(
literal|"Inconsistent revision sequence. Before: "
operator|+
name|before
operator|+
literal|", after: "
operator|+
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|before
operator|=
name|r
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// perform commits with multiple threads
name|List
argument_list|<
name|Thread
argument_list|>
name|writers
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_WRITERS
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|writers
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
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
block|{
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|COMMITS_PER_WRITER
condition|;
name|i
operator|++
control|)
block|{
name|Commit
name|commit
init|=
name|store
operator|.
name|newCommit
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|0
argument_list|,
name|random
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|0
condition|)
block|{
comment|// cancel 20% of the commits
name|store
operator|.
name|canceled
argument_list|(
name|commit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|isBranch
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|0
decl_stmt|;
name|store
operator|.
name|done
argument_list|(
name|commit
argument_list|,
name|isBranch
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|writers
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|writers
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|running
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|observer
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|assertNoExceptions
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|concurrentCommits2
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CommitQueue
name|queue
init|=
operator|new
name|CommitQueue
argument_list|(
name|DummyRevisionContext
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
specifier|final
name|CommitQueue
operator|.
name|Callback
name|c
init|=
operator|new
name|CommitQueue
operator|.
name|Callback
argument_list|()
block|{
specifier|private
name|Revision
name|before
init|=
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|headOfQueue
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|r
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"seen: {}"
argument_list|,
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|compareRevisionTime
argument_list|(
name|before
argument_list|)
operator|<
literal|0
condition|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
operator|new
name|Exception
argument_list|(
literal|"Inconsistent revision sequence. Before: "
operator|+
name|before
operator|+
literal|", after: "
operator|+
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|before
operator|=
name|r
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|// perform commits with multiple threads
name|List
argument_list|<
name|Thread
argument_list|>
name|writers
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_WRITERS
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|writers
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
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
block|{
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|COMMITS_PER_WRITER
condition|;
name|i
operator|++
control|)
block|{
name|Revision
name|r
init|=
name|queue
operator|.
name|createRevision
argument_list|()
decl_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|0
argument_list|,
name|random
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|0
condition|)
block|{
comment|// cancel 20% of the commits
name|queue
operator|.
name|canceled
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queue
operator|.
name|done
argument_list|(
name|r
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|writers
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|writers
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|assertNoExceptions
argument_list|()
expr_stmt|;
block|}
comment|// OAK-2868
annotation|@
name|Test
specifier|public
name|void
name|branchCommitMustNotBlockTrunkCommit
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|DocumentNodeStore
name|ds
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
comment|// simulate start of a branch commit
name|Commit
name|c
init|=
name|ds
operator|.
name|newCommit
argument_list|(
name|ds
operator|.
name|getHeadRevision
argument_list|()
operator|.
name|asBranchRevision
argument_list|(
name|ds
operator|.
name|getClusterId
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
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
block|{
try|try
block|{
name|NodeBuilder
name|builder
init|=
name|ds
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|ds
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|t
operator|.
name|join
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Commit did not succeed within 3 seconds"
argument_list|,
name|t
operator|.
name|isAlive
argument_list|()
argument_list|)
expr_stmt|;
name|ds
operator|.
name|canceled
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|assertNoExceptions
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|suspendUntil
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicReference
argument_list|<
name|RevisionVector
argument_list|>
name|headRevision
init|=
operator|new
name|AtomicReference
argument_list|<
name|RevisionVector
argument_list|>
argument_list|()
decl_stmt|;
name|RevisionContext
name|context
init|=
operator|new
name|DummyRevisionContext
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RevisionVector
name|getHeadRevision
parameter_list|()
block|{
return|return
name|headRevision
operator|.
name|get
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|headRevision
operator|.
name|set
argument_list|(
operator|new
name|RevisionVector
argument_list|(
name|context
operator|.
name|newRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|CommitQueue
name|queue
init|=
operator|new
name|CommitQueue
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|Revision
name|newHeadRev
init|=
name|context
operator|.
name|newRevision
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Revision
argument_list|>
name|revisions
init|=
name|queue
operator|.
name|createRevisions
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
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
block|{
name|queue
operator|.
name|suspendUntilAll
argument_list|(
name|union
argument_list|(
name|of
argument_list|(
name|newHeadRev
argument_list|)
argument_list|,
name|revisions
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// wait until t is suspended
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|queue
operator|.
name|numSuspendedThreads
argument_list|()
operator|>
literal|0
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|numSuspendedThreads
argument_list|()
argument_list|)
expr_stmt|;
name|queue
operator|.
name|headRevisionChanged
argument_list|()
expr_stmt|;
comment|// must still be suspended
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|numSuspendedThreads
argument_list|()
argument_list|)
expr_stmt|;
name|headRevision
operator|.
name|set
argument_list|(
operator|new
name|RevisionVector
argument_list|(
name|newHeadRev
argument_list|)
argument_list|)
expr_stmt|;
name|queue
operator|.
name|headRevisionChanged
argument_list|()
expr_stmt|;
comment|// must still be suspended
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|numSuspendedThreads
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Revision
name|rev
range|:
name|revisions
control|)
block|{
name|queue
operator|.
name|canceled
argument_list|(
name|rev
argument_list|)
expr_stmt|;
block|}
comment|// must not be suspended anymore
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|numSuspendedThreads
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|suspendUntilTimeout
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicReference
argument_list|<
name|RevisionVector
argument_list|>
name|headRevision
init|=
operator|new
name|AtomicReference
argument_list|<
name|RevisionVector
argument_list|>
argument_list|()
decl_stmt|;
name|RevisionContext
name|context
init|=
operator|new
name|DummyRevisionContext
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RevisionVector
name|getHeadRevision
parameter_list|()
block|{
return|return
name|headRevision
operator|.
name|get
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|headRevision
operator|.
name|set
argument_list|(
operator|new
name|RevisionVector
argument_list|(
name|context
operator|.
name|newRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|CommitQueue
name|queue
init|=
operator|new
name|CommitQueue
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|queue
operator|.
name|setSuspendTimeoutMillis
argument_list|(
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|Revision
name|r
init|=
name|context
operator|.
name|newRevision
argument_list|()
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
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
block|{
name|queue
operator|.
name|suspendUntilAll
argument_list|(
name|of
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|t
operator|.
name|join
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|t
operator|.
name|isAlive
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|concurrentSuspendUntil
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicReference
argument_list|<
name|RevisionVector
argument_list|>
name|headRevision
init|=
operator|new
name|AtomicReference
argument_list|<
name|RevisionVector
argument_list|>
argument_list|()
decl_stmt|;
name|RevisionContext
name|context
init|=
operator|new
name|DummyRevisionContext
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RevisionVector
name|getHeadRevision
parameter_list|()
block|{
return|return
name|headRevision
operator|.
name|get
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|headRevision
operator|.
name|set
argument_list|(
operator|new
name|RevisionVector
argument_list|(
name|context
operator|.
name|newRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Revision
argument_list|>
name|allRevisions
init|=
operator|new
name|ArrayList
argument_list|<
name|Revision
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|CommitQueue
name|queue
init|=
operator|new
name|CommitQueue
argument_list|(
name|context
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
comment|// threads count
specifier|final
name|Set
argument_list|<
name|Revision
argument_list|>
name|revisions
init|=
operator|new
name|HashSet
argument_list|<
name|Revision
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
comment|// revisions per thread
name|Revision
name|r
init|=
name|queue
operator|.
name|createRevision
argument_list|()
decl_stmt|;
name|revisions
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|allRevisions
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|queue
operator|.
name|suspendUntilAll
argument_list|(
name|revisions
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|queue
operator|.
name|numSuspendedThreads
argument_list|()
operator|==
literal|10
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|queue
operator|.
name|numSuspendedThreads
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|allRevisions
argument_list|)
expr_stmt|;
for|for
control|(
name|Revision
name|r
range|:
name|allRevisions
control|)
block|{
name|queue
operator|.
name|canceled
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|queue
operator|.
name|numSuspendedThreads
argument_list|()
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|numSuspendedThreads
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|join
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|t
operator|.
name|isAlive
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// OAK-4540
annotation|@
name|Test
specifier|public
name|void
name|headOfQueueMustNotBlockNewRevision
parameter_list|()
throws|throws
name|Exception
block|{
name|RevisionContext
name|context
init|=
operator|new
name|DummyRevisionContext
argument_list|()
decl_stmt|;
specifier|final
name|CommitQueue
name|queue
init|=
operator|new
name|CommitQueue
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|Revision
name|r1
init|=
name|queue
operator|.
name|createRevision
argument_list|()
decl_stmt|;
specifier|final
name|Semaphore
name|s1
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|CommitQueue
operator|.
name|Callback
name|c
init|=
operator|new
name|CommitQueue
operator|.
name|Callback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|headOfQueue
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|revision
parameter_list|)
block|{
name|s1
operator|.
name|acquireUninterruptibly
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|Thread
name|t1
init|=
operator|new
name|Thread
argument_list|(
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
block|{
name|queue
operator|.
name|done
argument_list|(
name|r1
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|t1
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|t2
init|=
operator|new
name|Thread
argument_list|(
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
block|{
name|queue
operator|.
name|createRevision
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|t2
operator|.
name|start
argument_list|()
expr_stmt|;
name|t2
operator|.
name|join
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|t2
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"CommitQueue.Callback.headOfQueue() must not "
operator|+
literal|"block CommitQueue.createRevision()"
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|s1
operator|.
name|release
argument_list|()
expr_stmt|;
name|t1
operator|.
name|join
argument_list|()
expr_stmt|;
name|t2
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertNoExceptions
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|exceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
name|exceptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit
