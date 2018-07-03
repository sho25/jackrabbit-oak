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
name|Semaphore
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
name|ClusterNodeInfo
operator|.
name|DEFAULT_LEASE_UPDATE_INTERVAL_MILLIS
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|lessThan
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
name|RecoveryLockTest
block|{
specifier|private
name|DocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
specifier|private
name|Clock
name|clock
init|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
decl_stmt|;
specifier|private
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
decl_stmt|;
specifier|private
name|RecoveryLock
name|lock1
init|=
operator|new
name|RecoveryLock
argument_list|(
name|store
argument_list|,
name|clock
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|private
name|RecoveryLock
name|lock2
init|=
operator|new
name|RecoveryLock
argument_list|(
name|store
argument_list|,
name|clock
argument_list|,
literal|2
argument_list|)
decl_stmt|;
specifier|private
name|ClusterNodeInfo
name|info1
decl_stmt|;
specifier|private
name|ClusterNodeInfo
name|info2
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
operator|.
name|waitUntil
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterNodeInfo
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|info1
operator|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
name|RecoveryHandler
operator|.
name|NOOP
argument_list|,
literal|null
argument_list|,
literal|"node1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
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
name|recoveryNotNeeded
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|lock1
operator|.
name|acquireRecoveryLock
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|acquireUnknown
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|lock2
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|releaseRemovedClusterNodeInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|clock
operator|.
name|waitUntil
argument_list|(
name|info1
operator|.
name|getLeaseEndTime
argument_list|()
operator|+
name|DEFAULT_LEASE_UPDATE_INTERVAL_MILLIS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|lock1
operator|.
name|acquireRecoveryLock
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|remove
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|info1
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|lock1
operator|.
name|releaseRecoveryLock
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Must fail with DocumentStoreException"
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
literal|"does not exist"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|acquireAfterLeaseEnd
parameter_list|()
throws|throws
name|Exception
block|{
name|clock
operator|.
name|waitUntil
argument_list|(
name|info1
operator|.
name|getLeaseEndTime
argument_list|()
operator|+
name|DEFAULT_LEASE_UPDATE_INTERVAL_MILLIS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|lock1
operator|.
name|acquireRecoveryLock
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterNodeInfoDocument
name|c
init|=
name|infoDocument
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isActive
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isBeingRecovered
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|)
argument_list|,
name|c
operator|.
name|getRecoveryBy
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|c
operator|.
name|get
argument_list|(
name|ClusterNodeInfo
operator|.
name|LEASE_END_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|successfulRecovery
parameter_list|()
throws|throws
name|Exception
block|{
name|clock
operator|.
name|waitUntil
argument_list|(
name|info1
operator|.
name|getLeaseEndTime
argument_list|()
operator|+
name|DEFAULT_LEASE_UPDATE_INTERVAL_MILLIS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|lock1
operator|.
name|acquireRecoveryLock
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|lock1
operator|.
name|releaseRecoveryLock
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ClusterNodeInfoDocument
name|c
init|=
name|infoDocument
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|isActive
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|isBeingRecovered
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|isBeingRecoveredBy
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|c
operator|.
name|get
argument_list|(
name|ClusterNodeInfo
operator|.
name|LEASE_END_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|unsuccessfulRecovery
parameter_list|()
throws|throws
name|Exception
block|{
name|clock
operator|.
name|waitUntil
argument_list|(
name|info1
operator|.
name|getLeaseEndTime
argument_list|()
operator|+
name|DEFAULT_LEASE_UPDATE_INTERVAL_MILLIS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|lock1
operator|.
name|acquireRecoveryLock
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|lock1
operator|.
name|releaseRecoveryLock
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ClusterNodeInfoDocument
name|c
init|=
name|infoDocument
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isActive
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|isBeingRecovered
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|isBeingRecoveredBy
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|c
operator|.
name|get
argument_list|(
name|ClusterNodeInfo
operator|.
name|LEASE_END_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getLeaseEndTime
argument_list|()
argument_list|,
name|lessThan
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|inactive
parameter_list|()
block|{
name|info1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|lock1
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|lock1
operator|.
name|acquireRecoveryLock
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|selfRecoveryWithinDeadline
parameter_list|()
throws|throws
name|Exception
block|{
comment|// expire clusterId 1
name|clock
operator|.
name|waitUntil
argument_list|(
name|info1
operator|.
name|getLeaseEndTime
argument_list|()
operator|+
name|DEFAULT_LEASE_UPDATE_INTERVAL_MILLIS
argument_list|)
expr_stmt|;
name|ClusterNodeInfoDocument
name|c
init|=
name|infoDocument
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|MissingLastRevSeeker
name|seeker
init|=
operator|new
name|MissingLastRevSeeker
argument_list|(
name|store
argument_list|,
name|clock
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|c
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
name|c
operator|.
name|isBeingRecovered
argument_list|()
argument_list|)
expr_stmt|;
name|Semaphore
name|recovering
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Semaphore
name|recovered
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// simulate new startup and get info again
name|Future
argument_list|<
name|ClusterNodeInfo
argument_list|>
name|infoFuture
init|=
name|executor
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
name|clusterId
lambda|->
block|{
name|assertTrue
argument_list|(
name|lock1
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|;
name|recovering
operator|.
name|release
argument_list|()
argument_list|;
name|recovered
operator|.
name|acquireUninterruptibly
argument_list|()
argument_list|;
name|lock1
operator|.
name|releaseRecoveryLock
argument_list|(
literal|true
argument_list|)
argument_list|;                     return
literal|true
argument_list|;
block|}
operator|,
literal|null
operator|,
literal|"node1"
operator|,
literal|1
block|)
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_comment
comment|// wait until submitted task is in recovery
end_comment

begin_expr_stmt
name|recovering
operator|.
name|acquireUninterruptibly
argument_list|()
expr_stmt|;
end_expr_stmt

begin_comment
comment|// check state again
end_comment

begin_expr_stmt
name|c
operator|=
name|infoDocument
argument_list|(
literal|1
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|c
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
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|c
operator|.
name|isBeingRecovered
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|c
operator|.
name|isBeingRecoveredBy
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// clusterId 2 must not be able to acquire (break) the recovery lock
end_comment

begin_expr_stmt
name|assertFalse
argument_list|(
name|lock1
operator|.
name|acquireRecoveryLock
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// signal recovery to continue
end_comment

begin_expr_stmt
name|recovered
operator|.
name|release
argument_list|()
expr_stmt|;
end_expr_stmt

begin_decl_stmt
name|ClusterNodeInfo
name|info1
init|=
name|infoFuture
operator|.
name|get
argument_list|()
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|info1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// check state again
end_comment

begin_expr_stmt
name|c
operator|=
name|infoDocument
argument_list|(
literal|1
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertFalse
argument_list|(
name|c
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
end_expr_stmt

begin_expr_stmt
name|assertFalse
argument_list|(
name|c
operator|.
name|isBeingRecovered
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertFalse
argument_list|(
name|c
operator|.
name|isBeingRecoveredBy
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// neither must be able to acquire a recovery lock on
end_comment

begin_comment
comment|// an active entry with a valid lease
end_comment

begin_expr_stmt
name|assertFalse
argument_list|(
name|lock1
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertFalse
argument_list|(
name|lock1
operator|.
name|acquireRecoveryLock
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_function
unit|}      private
name|ClusterNodeInfoDocument
name|infoDocument
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
name|ClusterNodeInfoDocument
name|doc
init|=
name|store
operator|.
name|find
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|clusterId
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
end_function

unit|}
end_unit

