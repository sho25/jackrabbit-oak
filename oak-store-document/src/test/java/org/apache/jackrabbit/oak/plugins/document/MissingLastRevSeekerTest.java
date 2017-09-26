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
name|DEFAULT_LEASE_DURATION_MILLIS
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

begin_class
specifier|public
class|class
name|MissingLastRevSeekerTest
block|{
specifier|private
name|Clock
name|clock
decl_stmt|;
specifier|private
name|DocumentStore
name|store
decl_stmt|;
specifier|private
name|MissingLastRevSeeker
name|seeker
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
name|MemoryDocumentStore
argument_list|()
expr_stmt|;
name|seeker
operator|=
operator|new
name|MissingLastRevSeeker
argument_list|(
name|store
argument_list|,
name|clock
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
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
name|acquireRecoveryLockOnActiveClusterNode
parameter_list|()
block|{
name|ClusterNodeInfo
name|nodeInfo1
init|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|nodeInfo1
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|acquireRecoveryLockOnInactiveClusterNode
parameter_list|()
block|{
name|ClusterNodeInfo
name|nodeInfo1
init|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|nodeInfo1
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|nodeInfo1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|acquireRecoveryLockOnExpiredLease
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
name|nodeInfo1
init|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|nodeInfo1
operator|.
name|renewLease
argument_list|()
expr_stmt|;
comment|// expire the lease
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|DEFAULT_LEASE_DURATION_MILLIS
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|acquireRecoveryLockOnAlreadyLocked
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
name|nodeInfo1
init|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|nodeInfo1
operator|.
name|renewLease
argument_list|()
expr_stmt|;
comment|// expire the lease
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|DEFAULT_LEASE_DURATION_MILLIS
operator|+
literal|1
argument_list|)
expr_stmt|;
name|ClusterNodeInfo
name|nodeInfo2
init|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|nodeInfo2
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|acquireRecoveryLockAgain
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
name|nodeInfo1
init|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|nodeInfo1
operator|.
name|renewLease
argument_list|()
expr_stmt|;
comment|// expire the lease
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|DEFAULT_LEASE_DURATION_MILLIS
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|releaseRecoveryLockSuccessTrue
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
name|nodeInfo1
init|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|nodeInfo1
operator|.
name|renewLease
argument_list|()
expr_stmt|;
comment|// expire the lease
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|DEFAULT_LEASE_DURATION_MILLIS
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getClusterNodeInfo
argument_list|(
literal|1
argument_list|)
operator|.
name|isBeingRecovered
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getClusterNodeInfo
argument_list|(
literal|1
argument_list|)
operator|.
name|isActive
argument_list|()
argument_list|)
expr_stmt|;
name|seeker
operator|.
name|releaseRecoveryLock
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|getClusterNodeInfo
argument_list|(
literal|1
argument_list|)
operator|.
name|isBeingRecovered
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|getClusterNodeInfo
argument_list|(
literal|1
argument_list|)
operator|.
name|isActive
argument_list|()
argument_list|)
expr_stmt|;
comment|// recovery not needed anymore
name|assertFalse
argument_list|(
name|seeker
operator|.
name|isRecoveryNeeded
argument_list|(
name|getClusterNodeInfo
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|releaseRecoveryLockSuccessFalse
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
name|nodeInfo1
init|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|nodeInfo1
operator|.
name|renewLease
argument_list|()
expr_stmt|;
comment|// expire the lease
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|DEFAULT_LEASE_DURATION_MILLIS
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getClusterNodeInfo
argument_list|(
literal|1
argument_list|)
operator|.
name|isBeingRecovered
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getClusterNodeInfo
argument_list|(
literal|1
argument_list|)
operator|.
name|isActive
argument_list|()
argument_list|)
expr_stmt|;
name|seeker
operator|.
name|releaseRecoveryLock
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|getClusterNodeInfo
argument_list|(
literal|1
argument_list|)
operator|.
name|isBeingRecovered
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getClusterNodeInfo
argument_list|(
literal|1
argument_list|)
operator|.
name|isActive
argument_list|()
argument_list|)
expr_stmt|;
comment|// recovery still needed
name|assertTrue
argument_list|(
name|seeker
operator|.
name|isRecoveryNeeded
argument_list|(
name|getClusterNodeInfo
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isRecoveryNeeded
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
name|nodeInfo1
init|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|nodeInfo1
operator|.
name|renewLease
argument_list|()
expr_stmt|;
comment|// expire the lease
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|DEFAULT_LEASE_DURATION_MILLIS
operator|+
literal|1
argument_list|)
expr_stmt|;
name|ClusterNodeInfo
name|nodeInfo2
init|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|nodeInfo2
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|seeker
operator|.
name|isRecoveryNeeded
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|seeker
operator|.
name|isRecoveryNeeded
argument_list|(
name|getClusterNodeInfo
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|seeker
operator|.
name|isRecoveryNeeded
argument_list|(
name|getClusterNodeInfo
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|seeker
operator|.
name|releaseRecoveryLock
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|seeker
operator|.
name|isRecoveryNeeded
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|seeker
operator|.
name|isRecoveryNeeded
argument_list|(
name|getClusterNodeInfo
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|seeker
operator|.
name|isRecoveryNeeded
argument_list|(
name|getClusterNodeInfo
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getAllClusterNodes
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|Iterables
operator|.
name|size
argument_list|(
name|seeker
operator|.
name|getAllClusters
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterNodeInfo
name|nodeInfo1
init|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|nodeInfo1
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|Iterables
operator|.
name|size
argument_list|(
name|seeker
operator|.
name|getAllClusters
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterNodeInfo
name|nodeInfo2
init|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|nodeInfo2
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|Iterables
operator|.
name|size
argument_list|(
name|seeker
operator|.
name|getAllClusters
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getClusterNodeInfo
parameter_list|()
block|{
name|assertNull
argument_list|(
name|getClusterNodeInfo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterNodeInfo
name|nodeInfo1
init|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|nodeInfo1
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|getClusterNodeInfo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ClusterNodeInfoDocument
name|getClusterNodeInfo
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
return|return
name|seeker
operator|.
name|getClusterNodeInfo
argument_list|(
name|clusterId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

