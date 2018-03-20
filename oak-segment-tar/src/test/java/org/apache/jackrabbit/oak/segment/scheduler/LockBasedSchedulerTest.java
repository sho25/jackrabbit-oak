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
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
name|Executors
operator|.
name|newFixedThreadPool
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
name|assertNotNull
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
name|Future
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
name|commons
operator|.
name|concurrent
operator|.
name|ExecutorCloser
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
name|RecordId
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
name|memory
operator|.
name|MemoryStore
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|LockBasedSchedulerTest
block|{
specifier|private
name|NodeState
name|getRoot
parameter_list|(
name|Scheduler
name|scheduler
parameter_list|)
block|{
return|return
name|scheduler
operator|.
name|getHeadNodeState
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"root"
argument_list|)
return|;
block|}
comment|/**      * OAK-7162      *       * This test guards against race conditions which may happen when the head      * state in {@link Revisions} is changed from outside the scheduler. If a      * race condition happens at that point, data from a single commit will be      * lost.      */
annotation|@
name|Test
specifier|public
name|void
name|testSimulatedRaceOnRevisions
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|MemoryStore
name|ms
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
name|StatisticsProvider
name|statsProvider
init|=
name|StatisticsProvider
operator|.
name|NOOP
decl_stmt|;
name|SegmentNodeStoreStats
name|stats
init|=
operator|new
name|SegmentNodeStoreStats
argument_list|(
name|statsProvider
argument_list|)
decl_stmt|;
specifier|final
name|LockBasedScheduler
name|scheduler
init|=
name|LockBasedScheduler
operator|.
name|builder
argument_list|(
name|ms
operator|.
name|getRevisions
argument_list|()
argument_list|,
name|ms
operator|.
name|getReader
argument_list|()
argument_list|,
name|stats
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RecordId
name|initialHead
init|=
name|ms
operator|.
name|getRevisions
argument_list|()
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|ExecutorService
name|executorService
init|=
name|newFixedThreadPool
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|count
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
try|try
block|{
name|Callable
argument_list|<
name|PropertyState
argument_list|>
name|commitTask
init|=
operator|new
name|Callable
argument_list|<
name|PropertyState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PropertyState
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|property
init|=
literal|"prop"
operator|+
name|count
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
name|Commit
name|commit
init|=
name|createCommit
argument_list|(
name|scheduler
argument_list|,
name|property
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
name|SegmentNodeState
name|result
init|=
operator|(
name|SegmentNodeState
operator|)
name|scheduler
operator|.
name|schedule
argument_list|(
name|commit
argument_list|)
decl_stmt|;
return|return
name|result
operator|.
name|getProperty
argument_list|(
name|property
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|Callable
argument_list|<
name|Void
argument_list|>
name|parallelTask
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
name|Thread
operator|.
name|sleep
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|ms
operator|.
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|ms
operator|.
name|getRevisions
argument_list|()
operator|.
name|getHead
argument_list|()
argument_list|,
name|initialHead
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|?
argument_list|>
argument_list|>
name|results
init|=
name|newArrayList
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|results
operator|.
name|add
argument_list|(
name|executorService
operator|.
name|submit
argument_list|(
name|commitTask
argument_list|)
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|submit
argument_list|(
name|parallelTask
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Future
argument_list|<
name|?
argument_list|>
name|result
range|:
name|results
control|)
block|{
name|assertNotNull
argument_list|(
literal|"PropertyState must not be null! The corresponding commit got lost because of a race condition."
argument_list|,
name|result
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
operator|new
name|ExecutorCloser
argument_list|(
name|executorService
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Commit
name|createCommit
parameter_list|(
specifier|final
name|Scheduler
name|scheduler
parameter_list|,
specifier|final
name|String
name|property
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|NodeBuilder
name|a
init|=
name|getRoot
argument_list|(
name|scheduler
argument_list|)
operator|.
name|builder
argument_list|()
decl_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|Commit
name|commit
init|=
operator|new
name|Commit
argument_list|(
name|a
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
return|return
name|commit
return|;
block|}
block|}
end_class

end_unit

