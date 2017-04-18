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
name|SortedMap
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|stats
operator|.
name|Clock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|util
operator|.
name|Utils
operator|.
name|getAllDocuments
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
name|util
operator|.
name|Utils
operator|.
name|getRootDocument
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
name|assertNotNull
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
name|assertNull
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

begin_class
specifier|public
class|class
name|DocumentNodeStoreSweepTest
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
name|Clock
name|clock
decl_stmt|;
specifier|private
name|FailingDocumentStore
name|store
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|clock
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
name|clock
operator|.
name|waitUntil
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|Revision
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|ClusterNodeInfo
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|FailingDocumentStore
argument_list|(
operator|new
name|MemoryDocumentStore
argument_list|()
argument_list|)
expr_stmt|;
name|ns
operator|=
name|createDocumentNodeStore
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|resetClock
parameter_list|()
block|{
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
name|ClusterNodeInfo
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|simple
parameter_list|()
throws|throws
name|Exception
block|{
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
name|RevisionVector
name|head
init|=
name|ns
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|ns
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|NodeDocument
name|rootDoc
init|=
name|getRootDocument
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|rootDoc
argument_list|)
expr_stmt|;
comment|// after dispose, the sweep revision must be at least the head revision
name|Revision
name|localHead
init|=
name|head
operator|.
name|getRevision
argument_list|(
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|localHead
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rootDoc
operator|.
name|getSweepRevisions
argument_list|()
operator|.
name|isRevisionNewer
argument_list|(
name|localHead
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rollbackFailed
parameter_list|()
throws|throws
name|Exception
block|{
name|createUncommittedChanges
argument_list|()
expr_stmt|;
comment|// after a new head and a background sweep, the
comment|// uncommitted changes must be cleaned up
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
name|ns
operator|.
name|runBackgroundUpdateOperations
argument_list|()
expr_stmt|;
name|ns
operator|.
name|runBackgroundSweepOperation
argument_list|()
expr_stmt|;
name|assertCleanStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rollbackFailedWithDispose
parameter_list|()
throws|throws
name|Exception
block|{
name|createUncommittedChanges
argument_list|()
expr_stmt|;
comment|// dispose must trigger sweeper
name|ns
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|assertCleanStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|sameClusterNodeRecovery
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|clusterId
init|=
name|ns
operator|.
name|getClusterId
argument_list|()
decl_stmt|;
name|createUncommittedChanges
argument_list|()
expr_stmt|;
comment|// simulate a crashed node store
name|crashDocumentNodeStore
argument_list|()
expr_stmt|;
comment|// store must be clean after restart
name|ns
operator|=
name|createDocumentNodeStore
argument_list|(
name|clusterId
argument_list|)
expr_stmt|;
name|assertCleanStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|otherClusterNodeRecovery
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|clusterId
init|=
name|ns
operator|.
name|getClusterId
argument_list|()
decl_stmt|;
name|createUncommittedChanges
argument_list|()
expr_stmt|;
comment|// simulate a crashed node store
name|crashDocumentNodeStore
argument_list|()
expr_stmt|;
comment|// start a new node store with a different clusterId
name|ns
operator|=
name|createDocumentNodeStore
argument_list|(
name|clusterId
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// wait for lease to expire
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|ClusterNodeInfo
operator|.
name|DEFAULT_LEASE_DURATION_MILLIS
argument_list|)
expr_stmt|;
comment|// then run recovery for the other cluster node
name|assertTrue
argument_list|(
name|ns
operator|.
name|getLastRevRecoveryAgent
argument_list|()
operator|.
name|recover
argument_list|(
name|clusterId
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// now the store must be clean
name|assertCleanStore
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createUncommittedChanges
parameter_list|()
throws|throws
name|Exception
block|{
name|ns
operator|.
name|setMaxBackOffMillis
argument_list|(
literal|0
argument_list|)
expr_stmt|;
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
name|builder
operator|.
name|child
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|fail
argument_list|()
operator|.
name|after
argument_list|(
literal|5
argument_list|)
operator|.
name|eternally
argument_list|()
expr_stmt|;
try|try
block|{
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must fail with exception"
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
block|}
name|store
operator|.
name|fail
argument_list|()
operator|.
name|never
argument_list|()
expr_stmt|;
comment|// store must now contain uncommitted changes
name|NodeDocument
name|doc
init|=
literal|null
decl_stmt|;
for|for
control|(
name|NodeDocument
name|d
range|:
name|Utils
operator|.
name|getAllDocuments
argument_list|(
name|store
argument_list|)
control|)
block|{
if|if
condition|(
name|d
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"/node-"
argument_list|)
condition|)
block|{
name|doc
operator|=
name|d
expr_stmt|;
break|break;
block|}
block|}
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|doc
operator|.
name|getNodeAtRevision
argument_list|(
name|ns
argument_list|,
name|ns
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|SortedMap
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|deleted
init|=
name|doc
operator|.
name|getLocalDeleted
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|deleted
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|ns
operator|.
name|getCommitValue
argument_list|(
name|deleted
operator|.
name|firstKey
argument_list|()
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertCleanStore
parameter_list|()
block|{
for|for
control|(
name|NodeDocument
name|doc
range|:
name|getAllDocuments
argument_list|(
name|store
argument_list|)
control|)
block|{
for|for
control|(
name|Revision
name|c
range|:
name|doc
operator|.
name|getAllChanges
argument_list|()
control|)
block|{
name|String
name|commitValue
init|=
name|ns
operator|.
name|getCommitValue
argument_list|(
name|c
argument_list|,
name|doc
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Revision "
operator|+
name|c
operator|+
literal|" on "
operator|+
name|doc
operator|.
name|getId
argument_list|()
operator|+
literal|" is not committed: "
operator|+
name|commitValue
argument_list|,
name|Utils
operator|.
name|isCommitted
argument_list|(
name|commitValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|DocumentNodeStore
name|createDocumentNodeStore
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
return|return
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
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeStore
argument_list|()
return|;
block|}
specifier|private
name|void
name|crashDocumentNodeStore
parameter_list|()
block|{
comment|// prevent writing anything in dispose
name|store
operator|.
name|fail
argument_list|()
operator|.
name|after
argument_list|(
literal|0
argument_list|)
operator|.
name|eternally
argument_list|()
expr_stmt|;
try|try
block|{
name|ns
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must fail with an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
comment|// expected
block|}
comment|// allow writes again
name|store
operator|.
name|fail
argument_list|()
operator|.
name|never
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

