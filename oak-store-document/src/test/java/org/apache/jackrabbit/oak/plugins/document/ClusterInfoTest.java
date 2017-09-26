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
name|ClusterNodeState
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
name|Test
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|ReadPreference
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|WriteConcern
import|;
end_import

begin_comment
comment|/**  * Test the ClusterInfo class  */
end_comment

begin_class
specifier|public
class|class
name|ClusterInfoTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|readWriteMode
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|MemoryDocumentStore
name|mem
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|Clock
name|clock
init|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
decl_stmt|;
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
name|DocumentNodeStore
name|ns1
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|mem
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setLeaseCheck
argument_list|(
literal|false
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|DocumentNodeStore
name|ns2
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|mem
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setLeaseCheck
argument_list|(
literal|false
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|2
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
comment|// Bring the current time forward to after the leaseTime which would have been
comment|// updated in the DocumentNodeStore initialization.
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|ns1
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getLeaseTime
argument_list|()
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|setLeaseTime
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|setLeaseUpdateInterval
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ns2
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|setLeaseTime
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ns2
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|setLeaseUpdateInterval
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ClusterNodeInfoDocument
argument_list|>
name|list
init|=
name|mem
operator|.
name|query
argument_list|(
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
literal|"0"
argument_list|,
literal|"a"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|mem
operator|.
name|getReadPreference
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|mem
operator|.
name|getWriteConcern
argument_list|()
argument_list|)
expr_stmt|;
name|mem
operator|.
name|setReadWriteMode
argument_list|(
literal|"read:primary, write:majority"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ReadPreference
operator|.
name|primary
argument_list|()
argument_list|,
name|mem
operator|.
name|getReadPreference
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|WriteConcern
operator|.
name|MAJORITY
argument_list|,
name|mem
operator|.
name|getWriteConcern
argument_list|()
argument_list|)
expr_stmt|;
name|UpdateOp
name|op
decl_stmt|;
comment|// unknown modes: ignore
name|op
operator|=
operator|new
name|UpdateOp
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|op
operator|.
name|set
argument_list|(
literal|"readWriteMode"
argument_list|,
literal|"read:xyz, write:abc"
argument_list|)
expr_stmt|;
name|mem
operator|.
name|findAndUpdate
argument_list|(
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|renewClusterIdLease
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|ReadPreference
operator|.
name|primary
argument_list|()
argument_list|,
name|mem
operator|.
name|getReadPreference
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|WriteConcern
operator|.
name|MAJORITY
argument_list|,
name|mem
operator|.
name|getWriteConcern
argument_list|()
argument_list|)
expr_stmt|;
name|op
operator|=
operator|new
name|UpdateOp
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|op
operator|.
name|set
argument_list|(
literal|"readWriteMode"
argument_list|,
literal|"read:nearest, write:fsynced"
argument_list|)
expr_stmt|;
name|mem
operator|.
name|findAndUpdate
argument_list|(
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|renewClusterIdLease
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|ReadPreference
operator|.
name|nearest
argument_list|()
argument_list|,
name|mem
operator|.
name|getReadPreference
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|WriteConcern
operator|.
name|FSYNCED
argument_list|,
name|mem
operator|.
name|getWriteConcern
argument_list|()
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|ns2
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|renewLease
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|MemoryDocumentStore
name|mem
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|Clock
name|clock
init|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
decl_stmt|;
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
name|DocumentNodeStore
name|ns
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|mem
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setLeaseCheck
argument_list|(
literal|false
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|ClusterNodeInfo
name|info
init|=
name|ns
operator|.
name|getClusterInfo
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
comment|// current lease end
name|long
name|leaseEnd
init|=
name|getLeaseEndTime
argument_list|(
name|ns
argument_list|)
decl_stmt|;
comment|// wait a bit, 1sec less than leaseUpdateTime (10sec-1sec by default)
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
name|DEFAULT_LEASE_UPDATE_INTERVAL_MILLIS
operator|-
literal|1000
argument_list|)
expr_stmt|;
comment|// must not renew lease right now
name|ns
operator|.
name|renewClusterIdLease
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|leaseEnd
argument_list|,
name|getLeaseEndTime
argument_list|(
name|ns
argument_list|)
argument_list|)
expr_stmt|;
comment|// wait some more time
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
literal|2000
argument_list|)
expr_stmt|;
comment|// now the lease must be renewed
name|ns
operator|.
name|renewClusterIdLease
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|getLeaseEndTime
argument_list|(
name|ns
argument_list|)
operator|>
name|leaseEnd
argument_list|)
expr_stmt|;
name|ns
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|long
name|getLeaseEndTime
parameter_list|(
name|DocumentNodeStore
name|nodeStore
parameter_list|)
block|{
name|ClusterNodeInfoDocument
name|doc
init|=
name|nodeStore
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|nodeStore
operator|.
name|getClusterId
argument_list|()
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
operator|.
name|getLeaseEndTime
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|useAbandoned
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Clock
name|clock
init|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
decl_stmt|;
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
name|MemoryDocumentStore
name|mem
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|DocumentNodeStore
name|ns1
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|mem
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
name|setLeaseCheck
argument_list|(
literal|false
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|DocumentStore
name|ds
init|=
name|ns1
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|int
name|cid
init|=
name|ns1
operator|.
name|getClusterId
argument_list|()
decl_stmt|;
name|ClusterNodeInfoDocument
name|cnid
init|=
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
literal|""
operator|+
name|cid
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|cnid
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ClusterNodeState
operator|.
name|ACTIVE
operator|.
name|toString
argument_list|()
argument_list|,
name|cnid
operator|.
name|get
argument_list|(
name|ClusterNodeInfo
operator|.
name|STATE
argument_list|)
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|long
name|waitFor
init|=
literal|2000
decl_stmt|;
comment|// modify record to indicate "active" with a lease end in the future
name|UpdateOp
name|up
init|=
operator|new
name|UpdateOp
argument_list|(
literal|""
operator|+
name|cid
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|up
operator|.
name|set
argument_list|(
name|ClusterNodeInfo
operator|.
name|STATE
argument_list|,
name|ClusterNodeState
operator|.
name|ACTIVE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|now
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|up
operator|.
name|set
argument_list|(
name|ClusterNodeInfo
operator|.
name|LEASE_END_KEY
argument_list|,
name|now
operator|+
name|waitFor
argument_list|)
expr_stmt|;
name|ds
operator|.
name|findAndUpdate
argument_list|(
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
name|up
argument_list|)
expr_stmt|;
comment|// try restart
name|ns1
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|mem
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
name|setLeaseCheck
argument_list|(
literal|false
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"should have re-used existing cluster id"
argument_list|,
name|cid
argument_list|,
name|ns1
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|dispose
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
name|ClusterNodeInfo
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
