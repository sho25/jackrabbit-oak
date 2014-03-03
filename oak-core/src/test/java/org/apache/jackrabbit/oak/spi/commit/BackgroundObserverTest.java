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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|assertTrue
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|BackgroundObserverTest
block|{
specifier|private
specifier|static
specifier|final
name|CommitInfo
name|COMMIT_INFO
init|=
operator|new
name|CommitInfo
argument_list|(
literal|"no-session"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Runnable
argument_list|>
name|assertions
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
name|CountDownLatch
name|doneCounter
decl_stmt|;
comment|/**      * Assert that each observer of many running concurrently sees the same      * linearly sequence of commits (i.e. sees the commits in the correct order).      */
annotation|@
name|Test
specifier|public
name|void
name|concurrentObservers
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Observer
name|observer
init|=
name|createCompositeObserver
argument_list|(
name|newFixedThreadPool
argument_list|(
literal|16
argument_list|)
argument_list|,
literal|128
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|1024
condition|;
name|k
operator|++
control|)
block|{
name|contentChanged
argument_list|(
name|observer
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
name|done
argument_list|(
name|observer
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doneCounter
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Runnable
name|assertion
range|:
name|assertions
control|)
block|{
name|assertion
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|contentChanged
parameter_list|(
name|Observer
name|observer
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|NodeState
name|node
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
name|value
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|observer
operator|.
name|contentChanged
argument_list|(
name|node
argument_list|,
name|COMMIT_INFO
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|done
parameter_list|(
name|Observer
name|observer
parameter_list|)
block|{
name|NodeState
name|node
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"done"
argument_list|,
literal|true
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|observer
operator|.
name|contentChanged
argument_list|(
name|node
argument_list|,
name|COMMIT_INFO
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CompositeObserver
name|createCompositeObserver
parameter_list|(
name|ExecutorService
name|executor
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|CompositeObserver
name|observer
init|=
operator|new
name|CompositeObserver
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|count
condition|;
name|k
operator|++
control|)
block|{
name|observer
operator|.
name|addObserver
argument_list|(
name|createBackgroundObserver
argument_list|(
name|executor
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|doneCounter
operator|=
operator|new
name|CountDownLatch
argument_list|(
name|count
argument_list|)
expr_stmt|;
return|return
name|observer
return|;
block|}
specifier|private
specifier|synchronized
name|void
name|done
parameter_list|(
name|List
argument_list|<
name|Runnable
argument_list|>
name|assertions
parameter_list|)
block|{
name|this
operator|.
name|assertions
operator|.
name|addAll
argument_list|(
name|assertions
argument_list|)
expr_stmt|;
name|doneCounter
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Observer
name|createBackgroundObserver
parameter_list|(
name|ExecutorService
name|executor
parameter_list|)
block|{
return|return
operator|new
name|BackgroundObserver
argument_list|(
operator|new
name|Observer
argument_list|()
block|{
comment|// Need synchronised list here to maintain correct memory barrier
comment|// when this is passed on to done(List<Runnable>)
specifier|final
name|List
argument_list|<
name|Runnable
argument_list|>
name|assertions
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
name|Lists
operator|.
expr|<
name|Runnable
operator|>
name|newArrayList
argument_list|()
argument_list|)
decl_stmt|;
specifier|volatile
name|NodeState
name|previous
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|contentChanged
parameter_list|(
annotation|@
name|Nonnull
specifier|final
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
name|root
operator|.
name|hasProperty
argument_list|(
literal|"done"
argument_list|)
condition|)
block|{
name|done
argument_list|(
name|assertions
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
comment|// Copy previous to avoid closing over it
specifier|final
name|NodeState
name|p
init|=
name|previous
decl_stmt|;
name|assertions
operator|.
name|add
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
name|assertEquals
argument_list|(
name|getP
argument_list|(
name|p
argument_list|)
operator|+
literal|1
argument_list|,
operator|(
name|long
operator|)
name|getP
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|previous
operator|=
name|root
expr_stmt|;
block|}
specifier|private
name|Long
name|getP
parameter_list|(
name|NodeState
name|previous
parameter_list|)
block|{
return|return
name|previous
operator|.
name|getProperty
argument_list|(
literal|"p"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
return|;
block|}
block|}
argument_list|,
name|executor
argument_list|,
literal|1024
argument_list|)
return|;
block|}
block|}
end_class

end_unit

