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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|CLUSTER_NODES
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
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
name|assertNotEquals
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
name|assertThat
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
name|LastRevRecoveryTest
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
name|DocumentNodeStore
name|ds1
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ds2
decl_stmt|;
specifier|private
name|int
name|c1Id
decl_stmt|;
specifier|private
name|int
name|c2Id
decl_stmt|;
specifier|private
name|MemoryDocumentStore
name|sharedStore
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
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
comment|// disable lease check because we fiddle with the virtual clock
specifier|final
name|LeaseCheckMode
name|leaseCheck
init|=
name|LeaseCheckMode
operator|.
name|DISABLED
decl_stmt|;
name|sharedStore
operator|=
operator|new
name|MemoryDocumentStore
argument_list|()
expr_stmt|;
name|ds1
operator|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|setLeaseCheckMode
argument_list|(
name|leaseCheck
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|sharedStore
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|c1Id
operator|=
name|ds1
operator|.
name|getClusterId
argument_list|()
expr_stmt|;
name|ds2
operator|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|setLeaseCheckMode
argument_list|(
name|leaseCheck
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|sharedStore
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|2
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|c2Id
operator|=
name|ds2
operator|.
name|getClusterId
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|ds1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|ds2
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|ClusterNodeInfo
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRecover
parameter_list|()
throws|throws
name|Exception
block|{
comment|//1. Create base structure /x/y
name|NodeBuilder
name|b1
init|=
name|ds1
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
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|ds1
operator|.
name|merge
argument_list|(
name|b1
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
name|ds1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|//lastRev are persisted directly for new nodes. In case of
comment|// updates they are persisted via background jobs
comment|//1.2 Get last rev populated for root node for ds2
name|ds2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|NodeBuilder
name|b2
init|=
name|ds2
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
literal|"x"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"f1"
argument_list|,
literal|"b1"
argument_list|)
expr_stmt|;
name|ds2
operator|.
name|merge
argument_list|(
name|b2
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
name|ds2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|//2. Add a new node /x/y/z
name|b2
operator|=
name|ds2
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
operator|.
name|child
argument_list|(
literal|"z"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|ds2
operator|.
name|merge
argument_list|(
name|b2
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
comment|//Refresh DS1
name|ds1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|NodeDocument
name|z1
init|=
name|getDocument
argument_list|(
name|ds1
argument_list|,
literal|"/x/y/z"
argument_list|)
decl_stmt|;
name|NodeDocument
name|y1
init|=
name|getDocument
argument_list|(
name|ds1
argument_list|,
literal|"/x/y"
argument_list|)
decl_stmt|;
name|NodeDocument
name|x1
init|=
name|getDocument
argument_list|(
name|ds1
argument_list|,
literal|"/x"
argument_list|)
decl_stmt|;
name|Revision
name|zlastRev2
init|=
name|z1
operator|.
name|getLastRev
argument_list|()
operator|.
name|get
argument_list|(
name|c2Id
argument_list|)
decl_stmt|;
comment|// /x/y/z is a new node and does not have a _lastRev
name|assertNull
argument_list|(
name|zlastRev2
argument_list|)
expr_stmt|;
name|Revision
name|head2
init|=
name|ds2
operator|.
name|getHeadRevision
argument_list|()
operator|.
name|getRevision
argument_list|(
name|c2Id
argument_list|)
decl_stmt|;
comment|//lastRev should not be updated for C #2
name|assertNull
argument_list|(
name|y1
operator|.
name|getLastRev
argument_list|()
operator|.
name|get
argument_list|(
name|c2Id
argument_list|)
argument_list|)
expr_stmt|;
name|LastRevRecoveryAgent
name|recovery
init|=
operator|new
name|LastRevRecoveryAgent
argument_list|(
name|sharedStore
argument_list|,
name|ds1
argument_list|)
decl_stmt|;
comment|//Do not pass y1 but still y1 should be updated
name|recovery
operator|.
name|recover
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|x1
argument_list|,
name|z1
argument_list|)
argument_list|,
name|c2Id
argument_list|)
expr_stmt|;
comment|//Post recovery the lastRev should be updated for /x/y and /x
name|assertEquals
argument_list|(
name|head2
argument_list|,
name|getDocument
argument_list|(
name|ds1
argument_list|,
literal|"/x/y"
argument_list|)
operator|.
name|getLastRev
argument_list|()
operator|.
name|get
argument_list|(
name|c2Id
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|head2
argument_list|,
name|getDocument
argument_list|(
name|ds1
argument_list|,
literal|"/x"
argument_list|)
operator|.
name|getLastRev
argument_list|()
operator|.
name|get
argument_list|(
name|c2Id
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|head2
argument_list|,
name|getDocument
argument_list|(
name|ds1
argument_list|,
literal|"/"
argument_list|)
operator|.
name|getLastRev
argument_list|()
operator|.
name|get
argument_list|(
name|c2Id
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// OAK-3079
annotation|@
name|Test
specifier|public
name|void
name|recoveryWithoutRootUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clusterId
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|c1Id
argument_list|)
decl_stmt|;
name|ClusterNodeInfoDocument
name|doc
init|=
name|sharedStore
operator|.
name|find
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|clusterId
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|ds1
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
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
operator|.
name|child
argument_list|(
literal|"z"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ds1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|ds1
operator|.
name|dispose
argument_list|()
expr_stmt|;
comment|// reset clusterNodes entry to simulate a crash
name|sharedStore
operator|.
name|remove
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|clusterId
argument_list|)
expr_stmt|;
name|sharedStore
operator|.
name|create
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|newArrayList
argument_list|(
name|updateOpFromDocument
argument_list|(
name|doc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// 'wait' until lease expires
name|clock
operator|.
name|waitUntil
argument_list|(
name|doc
operator|.
name|getLeaseEndTime
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// run recovery on ds2
name|LastRevRecoveryAgent
name|agent
init|=
operator|new
name|LastRevRecoveryAgent
argument_list|(
name|sharedStore
argument_list|,
name|ds2
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|clusterIds
init|=
name|agent
operator|.
name|getRecoveryCandidateNodes
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|contains
argument_list|(
name|clusterIds
argument_list|,
name|c1Id
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"must not recover any documents"
argument_list|,
literal|0
argument_list|,
name|agent
operator|.
name|recover
argument_list|(
name|c1Id
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// OAK-3488
annotation|@
name|Test
specifier|public
name|void
name|recoveryWithTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clusterId
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|c1Id
argument_list|)
decl_stmt|;
name|ClusterNodeInfoDocument
name|doc
init|=
name|sharedStore
operator|.
name|find
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|clusterId
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|ds1
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
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
operator|.
name|child
argument_list|(
literal|"z"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ds1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|ds1
operator|.
name|dispose
argument_list|()
expr_stmt|;
comment|// reset clusterNodes entry to simulate a crash
name|sharedStore
operator|.
name|remove
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|clusterId
argument_list|)
expr_stmt|;
name|sharedStore
operator|.
name|create
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|newArrayList
argument_list|(
name|updateOpFromDocument
argument_list|(
name|doc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// 'wait' until lease expires
name|clock
operator|.
name|waitUntil
argument_list|(
name|doc
operator|.
name|getLeaseEndTime
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// simulate ongoing recovery by cluster node 2
name|MissingLastRevSeeker
name|seeker
init|=
operator|new
name|MissingLastRevSeeker
argument_list|(
name|sharedStore
argument_list|,
name|clock
argument_list|)
decl_stmt|;
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
name|c1Id
argument_list|,
name|c2Id
argument_list|)
expr_stmt|;
comment|// run recovery from ds1
name|LastRevRecoveryAgent
name|a1
init|=
operator|new
name|LastRevRecoveryAgent
argument_list|(
name|sharedStore
argument_list|,
name|ds1
argument_list|)
decl_stmt|;
comment|// use current time -> do not wait for recovery of other agent
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|a1
operator|.
name|recover
argument_list|(
name|c1Id
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|seeker
operator|.
name|releaseRecoveryLock
argument_list|(
name|c1Id
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|a1
operator|.
name|recover
argument_list|(
name|c1Id
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
operator|+
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// OAK-3488
annotation|@
name|Test
specifier|public
name|void
name|failStartupOnRecoveryTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clusterId
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|c1Id
argument_list|)
decl_stmt|;
name|ClusterNodeInfoDocument
name|doc
init|=
name|sharedStore
operator|.
name|find
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|clusterId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|ds1
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
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
operator|.
name|child
argument_list|(
literal|"z"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ds1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|ds1
operator|.
name|dispose
argument_list|()
expr_stmt|;
comment|// reset clusterNodes entry to simulate a crash
name|sharedStore
operator|.
name|remove
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|clusterId
argument_list|)
expr_stmt|;
name|sharedStore
operator|.
name|create
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|newArrayList
argument_list|(
name|updateOpFromDocument
argument_list|(
name|doc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// 'wait' until lease expires
name|clock
operator|.
name|waitUntil
argument_list|(
name|doc
operator|.
name|getLeaseEndTime
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// make sure ds2 lease is still fine
name|assertTrue
argument_list|(
name|ds2
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|renewLease
argument_list|()
argument_list|)
expr_stmt|;
comment|// simulate ongoing recovery by cluster node 2
name|MissingLastRevSeeker
name|seeker
init|=
operator|new
name|MissingLastRevSeeker
argument_list|(
name|sharedStore
argument_list|,
name|clock
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
name|c1Id
argument_list|,
name|c2Id
argument_list|)
argument_list|)
expr_stmt|;
comment|// attempt to restart ds1 while lock is acquired
try|try
block|{
name|ds1
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|sharedStore
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|c1Id
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"DocumentStoreException expected"
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
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"needs recovery"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|seeker
operator|.
name|releaseRecoveryLock
argument_list|(
name|c1Id
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// OAK-3488
annotation|@
name|Test
specifier|public
name|void
name|breakRecoveryLockWithExpiredLease
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clusterId
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|c1Id
argument_list|)
decl_stmt|;
name|ClusterNodeInfoDocument
name|info1
init|=
name|sharedStore
operator|.
name|find
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|clusterId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info1
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|ds1
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
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
operator|.
name|child
argument_list|(
literal|"z"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ds1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|ds1
operator|.
name|dispose
argument_list|()
expr_stmt|;
comment|// reset clusterNodes entry to simulate a crash of ds1
name|sharedStore
operator|.
name|remove
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|clusterId
argument_list|)
expr_stmt|;
name|sharedStore
operator|.
name|create
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|newArrayList
argument_list|(
name|updateOpFromDocument
argument_list|(
name|info1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// 'wait' until lease expires
name|clock
operator|.
name|waitUntil
argument_list|(
name|info1
operator|.
name|getLeaseEndTime
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// make sure ds2 lease is still fine
name|ds2
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|renewLease
argument_list|()
expr_stmt|;
comment|// start of recovery by ds2
name|MissingLastRevSeeker
name|seeker
init|=
operator|new
name|MissingLastRevSeeker
argument_list|(
name|sharedStore
argument_list|,
name|clock
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
name|c1Id
argument_list|,
name|c2Id
argument_list|)
argument_list|)
expr_stmt|;
comment|// simulate crash of ds2
name|ClusterNodeInfoDocument
name|info2
init|=
name|sharedStore
operator|.
name|find
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|c2Id
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info2
argument_list|)
expr_stmt|;
name|ds2
operator|.
name|dispose
argument_list|()
expr_stmt|;
comment|// reset clusterNodes entry
name|sharedStore
operator|.
name|remove
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|c2Id
argument_list|)
argument_list|)
expr_stmt|;
name|sharedStore
operator|.
name|create
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|newArrayList
argument_list|(
name|updateOpFromDocument
argument_list|(
name|info2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// 'wait' until ds2's lease expires
name|clock
operator|.
name|waitUntil
argument_list|(
name|info2
operator|.
name|getLeaseEndTime
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|info1
operator|=
name|sharedStore
operator|.
name|find
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|clusterId
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|info1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info1
operator|.
name|isRecoveryNeeded
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info1
operator|.
name|isBeingRecovered
argument_list|()
argument_list|)
expr_stmt|;
comment|// restart ds1
name|ds1
operator|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|setLeaseCheckMode
argument_list|(
name|LeaseCheckMode
operator|.
name|DISABLED
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|sharedStore
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|info1
operator|=
name|sharedStore
operator|.
name|find
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|clusterId
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|info1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|info1
operator|.
name|isRecoveryNeeded
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|info1
operator|.
name|isBeingRecovered
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|recoveryMustNotPerformInitialSweep
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clusterId
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|c1Id
argument_list|)
decl_stmt|;
name|ClusterNodeInfoDocument
name|info1
init|=
name|sharedStore
operator|.
name|find
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|clusterId
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|ds1
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
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
operator|.
name|child
argument_list|(
literal|"z"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ds1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|ds1
operator|.
name|dispose
argument_list|()
expr_stmt|;
comment|// reset clusterNodes entry to simulate a crash of ds1
name|sharedStore
operator|.
name|remove
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|clusterId
argument_list|)
expr_stmt|;
name|sharedStore
operator|.
name|create
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|newArrayList
argument_list|(
name|updateOpFromDocument
argument_list|(
name|info1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// remove the sweep revision as well
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|op
operator|.
name|removeMapEntry
argument_list|(
literal|"_sweepRev"
argument_list|,
operator|new
name|Revision
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|c1Id
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|sharedStore
operator|.
name|findAndUpdate
argument_list|(
name|NODES
argument_list|,
name|op
argument_list|)
argument_list|)
expr_stmt|;
name|NodeDocument
name|doc
init|=
name|getRootDocument
argument_list|(
name|sharedStore
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|doc
operator|.
name|getSweepRevisions
argument_list|()
operator|.
name|getRevision
argument_list|(
name|c1Id
argument_list|)
argument_list|)
expr_stmt|;
comment|// 'wait' until lease expires
name|clock
operator|.
name|waitUntil
argument_list|(
name|info1
operator|.
name|getLeaseEndTime
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// make sure ds2 lease is still fine
name|ds2
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|renewLease
argument_list|()
expr_stmt|;
comment|// run recovery on ds2 for ds1
name|LastRevRecoveryAgent
name|agent
init|=
operator|new
name|LastRevRecoveryAgent
argument_list|(
name|sharedStore
argument_list|,
name|ds2
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|clusterIds
init|=
name|agent
operator|.
name|getRecoveryCandidateNodes
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|contains
argument_list|(
name|clusterIds
argument_list|,
name|c1Id
argument_list|)
argument_list|)
expr_stmt|;
comment|// nothing to recover
name|assertEquals
argument_list|(
literal|"must not recover any documents"
argument_list|,
literal|0
argument_list|,
name|agent
operator|.
name|recover
argument_list|(
name|c1Id
argument_list|)
argument_list|)
expr_stmt|;
comment|// must not set sweep revision
name|doc
operator|=
name|getRootDocument
argument_list|(
name|sharedStore
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|doc
operator|.
name|getSweepRevisions
argument_list|()
operator|.
name|getRevision
argument_list|(
name|c1Id
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|selfRecoveryPassedDeadline
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clusterId
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|c1Id
argument_list|)
decl_stmt|;
name|ClusterNodeInfoDocument
name|info1
init|=
name|sharedStore
operator|.
name|find
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|clusterId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info1
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|ds1
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
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
operator|.
name|child
argument_list|(
literal|"z"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ds1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|ds1
operator|.
name|dispose
argument_list|()
expr_stmt|;
comment|// reset clusterNodes entry to simulate a crash of ds1
name|sharedStore
operator|.
name|remove
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|clusterId
argument_list|)
expr_stmt|;
name|sharedStore
operator|.
name|create
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|newArrayList
argument_list|(
name|updateOpFromDocument
argument_list|(
name|info1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// 'wait' until lease expires
name|clock
operator|.
name|waitUntil
argument_list|(
name|info1
operator|.
name|getLeaseEndTime
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|AtomicBoolean
name|delay
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|// simulate a startup with self-recovery by acquiring the clusterId
comment|// this will call the recovery handler because the lease is expired
comment|// use a seeker that takes longer than the lease duration
name|MissingLastRevSeeker
name|seeker
init|=
operator|new
name|MissingLastRevSeeker
argument_list|(
name|sharedStore
argument_list|,
name|clock
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|acquireRecoveryLock
parameter_list|(
name|int
name|clusterId
parameter_list|,
name|int
name|recoveredBy
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|super
operator|.
name|acquireRecoveryLock
argument_list|(
name|clusterId
argument_list|,
name|recoveredBy
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|delay
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
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
operator|+
literal|1
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
block|}
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
name|RecoveryHandler
name|recoveryHandler
init|=
operator|new
name|RecoveryHandlerImpl
argument_list|(
name|sharedStore
argument_list|,
name|clock
argument_list|,
name|seeker
argument_list|)
decl_stmt|;
try|try
block|{
comment|// Explicitly acquiring the clusterId must fail
comment|// when it takes too long to recover
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|sharedStore
argument_list|,
name|recoveryHandler
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|c1Id
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must fail with DocumentStoreException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"needs recovery"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// But must succeed with auto-assignment of clusterId
comment|// even if machineId and instanceId match
name|ClusterNodeInfo
name|cni
init|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|sharedStore
argument_list|,
name|recoveryHandler
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// though clusterId must not be the one that took too long to recover
name|assertNotEquals
argument_list|(
name|c1Id
argument_list|,
name|cni
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// hence recovery is still needed for c1Id
name|assertTrue
argument_list|(
name|seeker
operator|.
name|isRecoveryNeeded
argument_list|()
argument_list|)
expr_stmt|;
name|cni
operator|.
name|dispose
argument_list|()
expr_stmt|;
comment|// now run again without delay with the explicit clusterId
name|delay
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// must succeed now
name|cni
operator|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|sharedStore
argument_list|,
name|recoveryHandler
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|c1Id
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|c1Id
argument_list|,
name|cni
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|cni
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
specifier|private
name|NodeDocument
name|getDocument
parameter_list|(
name|DocumentNodeStore
name|nodeStore
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
name|nodeStore
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
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
name|builder
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|store
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
specifier|private
specifier|static
name|UpdateOp
name|updateOpFromDocument
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
name|doc
operator|.
name|getId
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|doc
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|Document
operator|.
name|ID
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|Object
name|obj
init|=
name|doc
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|Map
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
operator|)
name|obj
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|op
operator|.
name|setMapEntry
argument_list|(
name|key
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|obj
operator|instanceof
name|Boolean
condition|)
block|{
name|op
operator|.
name|set
argument_list|(
name|key
argument_list|,
operator|(
name|Boolean
operator|)
name|obj
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|obj
operator|instanceof
name|Number
condition|)
block|{
name|op
operator|.
name|set
argument_list|(
name|key
argument_list|,
operator|(
operator|(
name|Number
operator|)
name|obj
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|obj
operator|!=
literal|null
condition|)
block|{
name|op
operator|.
name|set
argument_list|(
name|key
argument_list|,
name|obj
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|op
operator|.
name|set
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|op
return|;
block|}
block|}
end_class

end_unit

