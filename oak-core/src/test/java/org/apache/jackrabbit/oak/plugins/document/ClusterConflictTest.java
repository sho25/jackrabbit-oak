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
name|atomic
operator|.
name|AtomicLong
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
name|plugins
operator|.
name|document
operator|.
name|memory
operator|.
name|MemoryDocumentStore
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
name|DefaultEditor
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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
operator|.
name|EMPTY
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

begin_class
specifier|public
class|class
name|ClusterConflictTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
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
name|ClusterConflictTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns1
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns2
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|MemoryDocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|ns1
operator|=
name|newDocumentNodeStore
argument_list|(
name|store
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ns2
operator|=
name|newDocumentNodeStore
argument_list|(
name|store
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
specifier|private
name|DocumentNodeStore
name|newDocumentNodeStore
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|int
name|clusterId
parameter_list|)
block|{
comment|// use high async delay and run background ops manually
comment|// asyncDelay set to zero prevents commits from suspending
return|return
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|60000
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|store
argument_list|)
operator|.
name|setLeaseCheck
argument_list|(
literal|false
argument_list|)
comment|// disabled for debugging purposes
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|getNodeStore
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|suspendUntilVisible
parameter_list|()
throws|throws
name|Exception
block|{
name|suspendUntilVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|suspendUntilVisibleWithBranch
parameter_list|()
throws|throws
name|Exception
block|{
name|suspendUntilVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|suspendUntilVisible
parameter_list|(
name|boolean
name|withBranch
parameter_list|)
throws|throws
name|Exception
block|{
name|NodeBuilder
name|b1
init|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"counter"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"value"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns1
argument_list|,
name|b1
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|ns2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|b1
operator|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|merge
argument_list|(
name|b1
argument_list|,
operator|new
name|TestHook
argument_list|()
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Exception
argument_list|>
name|exceptions
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|final
name|NodeBuilder
name|b2
init|=
name|ns2
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
if|if
condition|(
name|withBranch
condition|)
block|{
name|purge
argument_list|(
name|b2
argument_list|)
expr_stmt|;
block|}
name|b2
operator|.
name|child
argument_list|(
literal|"baz"
argument_list|)
expr_stmt|;
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
name|LOG
operator|.
name|info
argument_list|(
literal|"initiating merge"
argument_list|)
expr_stmt|;
name|ns2
operator|.
name|merge
argument_list|(
name|b2
argument_list|,
operator|new
name|TestHook
argument_list|()
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"merge succeeded"
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
name|ns2
operator|.
name|commitQueue
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
name|ns2
operator|.
name|commitQueue
operator|.
name|numSuspendedThreads
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"commit suspended"
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"ran background ops on ns1"
argument_list|)
expr_stmt|;
name|ns2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"ran background ops on ns2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ns2
operator|.
name|commitQueue
operator|.
name|numSuspendedThreads
argument_list|()
argument_list|)
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
for|for
control|(
name|Exception
name|e
range|:
name|exceptions
control|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
comment|// OAK-3859
annotation|@
name|Ignore
argument_list|(
literal|"OAK-3859"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|mixedConflictAndCollision
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|b1
init|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns1
argument_list|,
name|b1
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|ns2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|AtomicLong
name|counter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Exception
argument_list|>
name|exceptions
init|=
name|Collections
operator|.
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
comment|// the writers perform conflicting changes
name|List
argument_list|<
name|Thread
argument_list|>
name|writers
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|writers
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|Writer
argument_list|(
name|exceptions
argument_list|,
name|ns1
argument_list|,
name|counter
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writers
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|Writer
argument_list|(
name|exceptions
argument_list|,
name|ns1
argument_list|,
name|counter
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
name|NodeBuilder
name|b21
init|=
name|ns2
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// this change does not conflict with changes on ns1 but
comment|// will be considered a collision
name|b21
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"q"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns2
argument_list|,
name|b21
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
name|join
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Exception
name|e
range|:
name|exceptions
control|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
specifier|private
specifier|static
class|class
name|Writer
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|Exception
argument_list|>
name|exceptions
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|ns
decl_stmt|;
specifier|private
specifier|final
name|AtomicLong
name|counter
decl_stmt|;
specifier|public
name|Writer
parameter_list|(
name|List
argument_list|<
name|Exception
argument_list|>
name|exceptions
parameter_list|,
name|NodeStore
name|ns
parameter_list|,
name|AtomicLong
name|counter
parameter_list|)
block|{
name|this
operator|.
name|exceptions
operator|=
name|exceptions
expr_stmt|;
name|this
operator|.
name|ns
operator|=
name|ns
expr_stmt|;
name|this
operator|.
name|counter
operator|=
name|counter
expr_stmt|;
block|}
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
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|NodeBuilder
name|b
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
name|counter
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
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
specifier|private
specifier|static
class|class
name|TestHook
extends|extends
name|EditorHook
block|{
name|TestHook
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|EditorProvider
argument_list|()
block|{
annotation|@
name|CheckForNull
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
return|return
operator|new
name|TestEditor
argument_list|(
name|builder
operator|.
name|child
argument_list|(
literal|"counter"
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|TestEditor
extends|extends
name|DefaultEditor
block|{
specifier|private
name|NodeBuilder
name|counter
decl_stmt|;
name|TestEditor
parameter_list|(
name|NodeBuilder
name|counter
parameter_list|)
block|{
name|this
operator|.
name|counter
operator|=
name|counter
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|counter
operator|.
name|setProperty
argument_list|(
literal|"value"
argument_list|,
name|counter
operator|.
name|getProperty
argument_list|(
literal|"value"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|NodeState
name|merge
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|NodeBuilder
name|root
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
name|store
operator|.
name|merge
argument_list|(
name|root
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|EMPTY
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|purge
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
operator|(
operator|(
name|DocumentRootBuilder
operator|)
name|builder
operator|)
operator|.
name|purge
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

