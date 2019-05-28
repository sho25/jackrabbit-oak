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
name|plugins
operator|.
name|document
operator|.
name|UpdateOp
operator|.
name|Key
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
name|UpdateOp
operator|.
name|Operation
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
name|plugins
operator|.
name|document
operator|.
name|util
operator|.
name|Utils
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
name|document
operator|.
name|Collection
operator|.
name|NODES
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
name|document
operator|.
name|TestUtils
operator|.
name|merge
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
name|document
operator|.
name|UpdateOp
operator|.
name|Operation
operator|.
name|Type
operator|.
name|REMOVE_MAP_ENTRY
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
name|OrphanedBranchCleanupTest
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
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
decl_stmt|;
comment|// OAK-8012 / OAK-8353
annotation|@
name|Test
specifier|public
name|void
name|orphanedBranchRace
parameter_list|()
throws|throws
name|Exception
block|{
name|Semaphore
name|branchCleanupInProgress
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Semaphore
name|readHappened
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|DocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|findAndUpdate
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|UpdateOp
name|update
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|NODES
operator|&&
name|isBranchCleanup
argument_list|(
name|update
argument_list|)
condition|)
block|{
name|branchCleanupInProgress
operator|.
name|release
argument_list|()
expr_stmt|;
name|readHappened
operator|.
name|acquireUninterruptibly
argument_list|()
expr_stmt|;
block|}
return|return
name|super
operator|.
name|findAndUpdate
argument_list|(
name|collection
argument_list|,
name|update
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isBranchCleanup
parameter_list|(
name|UpdateOp
name|update
parameter_list|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|Key
argument_list|,
name|Operation
argument_list|>
name|e
range|:
name|update
operator|.
name|getChanges
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|type
operator|==
name|REMOVE_MAP_ENTRY
operator|&&
name|NodeDocument
operator|.
name|isRevisionsEntry
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
decl_stmt|;
name|DocumentNodeStore
name|ns
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|store
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|createOrphanedBranch
argument_list|(
name|ns
argument_list|)
expr_stmt|;
comment|// add a node below the root. this serves two purposes:
comment|// 1) the root state will have hasChildren set to true, otherwise
comment|//    a read will not even try to get children
comment|// 2) push the head revision after the orphaned branch commit revision
name|NodeBuilder
name|builder
init|=
name|ns
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
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
argument_list|)
expr_stmt|;
do|do
block|{
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|submit
argument_list|(
name|ns
operator|::
name|runBackgroundOperations
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
operator|!
name|branchCleanupInProgress
operator|.
name|tryAcquire
argument_list|(
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
do|;
name|ns
operator|.
name|getNodeCache
argument_list|()
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
name|ns
operator|.
name|getNodeChildrenCache
argument_list|()
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
name|boolean
name|hasTestNode
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|hasChildNode
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|readHappened
operator|.
name|release
argument_list|()
expr_stmt|;
operator|new
name|ExecutorCloser
argument_list|(
name|executorService
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|hasTestNode
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createOrphanedBranch
parameter_list|(
name|DocumentNodeStore
name|ns
parameter_list|)
block|{
name|DocumentStore
name|store
init|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|test
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|store
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|id
argument_list|)
operator|==
literal|null
condition|;
name|i
operator|++
control|)
block|{
name|test
operator|.
name|child
argument_list|(
literal|"n-"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

