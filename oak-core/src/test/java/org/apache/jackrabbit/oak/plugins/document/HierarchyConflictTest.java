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
name|commit
operator|.
name|AnnotatingConflictHandler
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
name|commit
operator|.
name|ConflictHook
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
name|commit
operator|.
name|ConflictValidatorProvider
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
name|CompositeHook
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
name|Editor
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
name|EditorHook
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
name|EditorProvider
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
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
import|import static
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
name|Uninterruptibles
operator|.
name|awaitUninterruptibly
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
name|util
operator|.
name|concurrent
operator|.
name|Uninterruptibles
operator|.
name|joinUninterruptibly
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
name|CommitFailedException
operator|.
name|OAK
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
comment|/**  * Test for OAK-2151  */
end_comment

begin_class
specifier|public
class|class
name|HierarchyConflictTest
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
name|HierarchyConflictTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Throwable
argument_list|>
name|exceptions
decl_stmt|;
specifier|private
name|CountDownLatch
name|nodeRemoved
decl_stmt|;
specifier|private
name|CountDownLatch
name|nodeAdded
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|store
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|exceptions
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
name|nodeRemoved
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|nodeAdded
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
name|store
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|conflict
parameter_list|()
throws|throws
name|Throwable
block|{
name|NodeBuilder
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|root
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|child
argument_list|(
literal|"baz"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store
argument_list|,
name|root
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|NodeBuilder
name|r1
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|r1
operator|.
name|child
argument_list|(
literal|"addNode"
argument_list|)
expr_stmt|;
specifier|final
name|NodeBuilder
name|r2
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|r2
operator|.
name|child
argument_list|(
literal|"removeNode"
argument_list|)
expr_stmt|;
specifier|final
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
name|merge
argument_list|(
name|store
argument_list|,
name|r2
argument_list|,
operator|new
name|EditorCallback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|edit
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|nodeRemoved
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|awaitUninterruptibly
argument_list|(
name|nodeAdded
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|// wait for r2 to enter merge phase
name|awaitUninterruptibly
argument_list|(
name|nodeRemoved
argument_list|)
expr_stmt|;
try|try
block|{
comment|// must fail because /foo/bar was removed
name|merge
argument_list|(
name|store
argument_list|,
name|r1
argument_list|,
operator|new
name|EditorCallback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|edit
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|child
argument_list|(
literal|"qux"
argument_list|)
expr_stmt|;
name|nodeAdded
operator|.
name|countDown
argument_list|()
expr_stmt|;
comment|// wait until r2 commits
name|joinUninterruptibly
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|Throwable
name|ex
range|:
name|exceptions
control|)
block|{
name|fail
argument_list|(
name|ex
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"Must fail with CommitFailedException. Cannot add child node"
operator|+
literal|" to a removed parent"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// expected
name|LOG
operator|.
name|info
argument_list|(
literal|"expected: {}"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|conflict2
parameter_list|()
throws|throws
name|Throwable
block|{
name|NodeBuilder
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|root
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|child
argument_list|(
literal|"baz"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store
argument_list|,
name|root
argument_list|,
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|NodeBuilder
name|r1
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|r1
operator|.
name|child
argument_list|(
literal|"addNode"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|r2
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|r2
operator|.
name|child
argument_list|(
literal|"removeNode"
argument_list|)
expr_stmt|;
specifier|final
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
name|merge
argument_list|(
name|store
argument_list|,
name|r1
argument_list|,
operator|new
name|EditorCallback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|edit
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|child
argument_list|(
literal|"qux"
argument_list|)
expr_stmt|;
name|nodeAdded
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|awaitUninterruptibly
argument_list|(
name|nodeRemoved
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|// wait for r1 to enter merge phase
name|awaitUninterruptibly
argument_list|(
name|nodeAdded
argument_list|)
expr_stmt|;
try|try
block|{
comment|// must fail because /foo/bar/qux was added
name|merge
argument_list|(
name|store
argument_list|,
name|r2
argument_list|,
operator|new
name|EditorCallback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|edit
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|nodeRemoved
operator|.
name|countDown
argument_list|()
expr_stmt|;
comment|// wait until r1 commits
name|joinUninterruptibly
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|Throwable
name|ex
range|:
name|exceptions
control|)
block|{
name|fail
argument_list|(
name|ex
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"Must fail with CommitFailedException. Cannot remove tree"
operator|+
literal|" when child is added concurrently"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// expected
name|LOG
operator|.
name|info
argument_list|(
literal|"expected: {}"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|merge
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|NodeBuilder
name|root
parameter_list|,
specifier|final
name|EditorCallback
name|callback
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|CompositeHook
name|hooks
init|=
operator|new
name|CompositeHook
argument_list|(
operator|new
name|EditorHook
argument_list|(
operator|new
name|EditorProvider
argument_list|()
block|{
specifier|private
name|int
name|numEdits
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Editor
name|getRootEditor
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|++
name|numEdits
operator|>
literal|1
condition|)
block|{
comment|// this is a retry, fail the commit
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|OAK
argument_list|,
literal|0
argument_list|,
literal|"do not retry merge in this test"
argument_list|)
throw|;
block|}
name|callback
operator|.
name|edit
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
argument_list|,
operator|new
name|ConflictHook
argument_list|(
operator|new
name|AnnotatingConflictHandler
argument_list|()
argument_list|)
argument_list|,
operator|new
name|EditorHook
argument_list|(
operator|new
name|ConflictValidatorProvider
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|root
argument_list|,
name|hooks
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
specifier|private
interface|interface
name|EditorCallback
block|{
specifier|public
name|void
name|edit
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
block|}
block|}
end_class

end_unit

