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
name|mongomk
operator|.
name|impl
operator|.
name|command
package|;
end_package

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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Executors
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
name|mongomk
operator|.
name|BaseMongoMicroKernelTest
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
name|mongomk
operator|.
name|api
operator|.
name|command
operator|.
name|CommandExecutor
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
name|mongomk
operator|.
name|api
operator|.
name|model
operator|.
name|Commit
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
name|mongomk
operator|.
name|api
operator|.
name|model
operator|.
name|Node
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
name|mongomk
operator|.
name|impl
operator|.
name|action
operator|.
name|FetchCommitsAction
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
name|mongomk
operator|.
name|impl
operator|.
name|model
operator|.
name|CommitBuilder
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
name|mongomk
operator|.
name|impl
operator|.
name|model
operator|.
name|MongoCommit
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
name|PathUtils
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
name|ConcurrentCommitCommandTest
extends|extends
name|BaseMongoMicroKernelTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testConflictingConcurrentUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numOfConcurrentThreads
init|=
literal|5
decl_stmt|;
specifier|final
name|Object
name|waitLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|// create the commands
name|List
argument_list|<
name|CommitCommand
argument_list|>
name|commands
init|=
operator|new
name|ArrayList
argument_list|<
name|CommitCommand
argument_list|>
argument_list|(
name|numOfConcurrentThreads
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
name|numOfConcurrentThreads
condition|;
operator|++
name|i
control|)
block|{
name|Commit
name|commit
init|=
name|CommitBuilder
operator|.
name|build
argument_list|(
literal|"/"
argument_list|,
literal|"+\""
operator|+
name|i
operator|+
literal|"\" : {}"
argument_list|,
literal|"This is a concurrent commit"
argument_list|)
decl_stmt|;
name|CommitCommand
name|command
init|=
operator|new
name|CommitCommand
argument_list|(
name|getNodeStore
argument_list|()
argument_list|,
name|commit
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|saveAndSetHeadRevision
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
synchronized|synchronized
init|(
name|waitLock
init|)
block|{
name|waitLock
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
return|return
name|super
operator|.
name|saveAndSetHeadRevision
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
empty_stmt|;
block|}
decl_stmt|;
name|commands
operator|.
name|add
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
comment|// execute the commands
specifier|final
name|CommandExecutor
name|commandExecutor
init|=
operator|new
name|DefaultCommandExecutor
argument_list|()
decl_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|numOfConcurrentThreads
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Long
argument_list|>
name|revisionIds
init|=
operator|new
name|LinkedList
argument_list|<
name|Long
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
name|numOfConcurrentThreads
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|CommitCommand
name|command
init|=
name|commands
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Runnable
name|runnable
init|=
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
name|Long
name|revisionId
init|=
name|commandExecutor
operator|.
name|execute
argument_list|(
name|command
argument_list|)
decl_stmt|;
name|revisionIds
operator|.
name|add
argument_list|(
name|revisionId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|revisionIds
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|executorService
operator|.
name|execute
argument_list|(
name|runnable
argument_list|)
expr_stmt|;
block|}
comment|// notify the wait lock to execute the command concurrently
do|do
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|waitLock
init|)
block|{
name|waitLock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
do|while
condition|(
name|revisionIds
operator|.
name|size
argument_list|()
operator|<
name|numOfConcurrentThreads
condition|)
do|;
comment|// Verify the result by sorting the revision ids and verifying that all
comment|// children are contained in the next revision
name|Collections
operator|.
name|sort
argument_list|(
name|revisionIds
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Long
name|o1
parameter_list|,
name|Long
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|compareTo
argument_list|(
name|o2
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|lastChildren
init|=
operator|new
name|LinkedList
argument_list|<
name|String
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
name|numOfConcurrentThreads
condition|;
operator|++
name|i
control|)
block|{
name|Long
name|revisionId
init|=
name|revisionIds
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|GetNodesCommand
name|command2
init|=
operator|new
name|GetNodesCommand
argument_list|(
name|getNodeStore
argument_list|()
argument_list|,
literal|"/"
argument_list|,
name|revisionId
argument_list|)
decl_stmt|;
name|Node
name|root
init|=
name|command2
operator|.
name|execute
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|lastChild
range|:
name|lastChildren
control|)
block|{
name|boolean
name|contained
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Node
argument_list|>
name|it
init|=
name|root
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Node
name|childNode
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|childName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|childNode
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|childName
operator|.
name|equals
argument_list|(
name|lastChild
argument_list|)
condition|)
block|{
name|contained
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
name|contained
argument_list|)
expr_stmt|;
block|}
name|lastChildren
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Node
argument_list|>
name|it
init|=
name|root
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Node
name|childNode
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|childName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|childNode
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|lastChildren
operator|.
name|add
argument_list|(
name|childName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Assert number of successful commits.
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|commits
init|=
operator|new
name|FetchCommitsAction
argument_list|(
name|getNodeStore
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|numOfConcurrentThreads
operator|+
literal|1
argument_list|,
name|commits
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

