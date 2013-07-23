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
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|fail
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
name|plugins
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
name|PostCommitHook
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
name|NodeStoreBranch
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
name|MergeTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testSequentialMerge
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeStore
name|store
init|=
operator|new
name|SegmentNodeStore
argument_list|(
operator|new
name|MemoryStore
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeStoreBranch
name|a
init|=
name|store
operator|.
name|branch
argument_list|()
decl_stmt|;
name|a
operator|.
name|setRoot
argument_list|(
name|a
operator|.
name|getHead
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|.
name|merge
argument_list|(
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|PostCommitHook
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeStoreBranch
name|b
init|=
name|store
operator|.
name|branch
argument_list|()
decl_stmt|;
name|b
operator|.
name|setRoot
argument_list|(
name|b
operator|.
name|getHead
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"bar"
argument_list|,
literal|"xyz"
argument_list|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|merge
argument_list|(
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|PostCommitHook
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOptimisticMerge
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeStore
name|store
init|=
operator|new
name|SegmentNodeStore
argument_list|(
operator|new
name|MemoryStore
argument_list|()
argument_list|)
decl_stmt|;
name|NodeStoreBranch
name|a
init|=
name|store
operator|.
name|branch
argument_list|()
decl_stmt|;
name|a
operator|.
name|setRoot
argument_list|(
name|a
operator|.
name|getHead
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|NodeStoreBranch
name|b
init|=
name|store
operator|.
name|branch
argument_list|()
decl_stmt|;
name|b
operator|.
name|setRoot
argument_list|(
name|b
operator|.
name|getHead
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"bar"
argument_list|,
literal|"xyz"
argument_list|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|a
operator|.
name|merge
argument_list|(
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|PostCommitHook
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|merge
argument_list|(
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|PostCommitHook
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPessimisticMerge
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SegmentNodeStore
name|store
init|=
operator|new
name|SegmentNodeStore
argument_list|(
operator|new
name|MemoryStore
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Semaphore
name|semaphore
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|running
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Thread
name|background
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|running
operator|.
name|get
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|SegmentNodeStoreBranch
name|a
init|=
name|store
operator|.
name|branch
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|a
operator|.
name|getHead
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
operator|+
name|i
argument_list|)
expr_stmt|;
name|a
operator|.
name|setRoot
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|.
name|merge
argument_list|(
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|PostCommitHook
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|background
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// wait for the first commit
name|semaphore
operator|.
name|acquire
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|SegmentNodeStoreBranch
name|b
init|=
name|store
operator|.
name|branch
argument_list|()
decl_stmt|;
name|b
operator|.
name|setMaximumBackoff
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|b
operator|.
name|setRoot
argument_list|(
name|b
operator|.
name|getHead
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"bar"
argument_list|,
literal|"xyz"
argument_list|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|merge
argument_list|(
operator|new
name|CommitHook
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|processCommit
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
return|return
name|after
return|;
block|}
block|}
argument_list|,
name|PostCommitHook
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|running
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|background
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

