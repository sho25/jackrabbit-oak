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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|MISSING_NODE
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Sets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DB
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
name|mk
operator|.
name|api
operator|.
name|MicroKernelException
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
name|blob
operator|.
name|BlobStore
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
name|blob
operator|.
name|MemoryBlobStore
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
name|ChildNodeEntry
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
name|DefaultNodeStateDiff
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
name|json
operator|.
name|simple
operator|.
name|JSONObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|parser
operator|.
name|JSONParser
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

begin_comment
comment|/**  * A set of simple cluster tests.  */
end_comment

begin_class
specifier|public
class|class
name|ClusterTest
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|MONGO_DB
init|=
literal|false
decl_stmt|;
comment|// private static final boolean MONGO_DB = true;
specifier|private
name|MemoryDocumentStore
name|ds
decl_stmt|;
specifier|private
name|MemoryBlobStore
name|bs
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|threeNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryDocumentStore
name|ds
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|MemoryBlobStore
name|bs
init|=
operator|new
name|MemoryBlobStore
argument_list|()
decl_stmt|;
name|DocumentMK
operator|.
name|Builder
name|builder
decl_stmt|;
name|builder
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setDocumentStore
argument_list|(
name|ds
argument_list|)
operator|.
name|setBlobStore
argument_list|(
name|bs
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|DocumentMK
name|mk1
init|=
name|builder
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|builder
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setDocumentStore
argument_list|(
name|ds
argument_list|)
operator|.
name|setBlobStore
argument_list|(
name|bs
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|DocumentMK
name|mk2
init|=
name|builder
operator|.
name|setClusterId
argument_list|(
literal|2
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|builder
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setDocumentStore
argument_list|(
name|ds
argument_list|)
operator|.
name|setBlobStore
argument_list|(
name|bs
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|DocumentMK
name|mk3
init|=
name|builder
operator|.
name|setClusterId
argument_list|(
literal|3
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|mk1
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk2
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk3
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"b\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk2
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|mk2
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|mk3
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|mk3
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|mk1
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|mk1
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|mk2
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|mk2
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|mk3
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|mk3
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|mk2
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"test/x\":1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|n3
init|=
name|mk3
operator|.
name|getNodes
argument_list|(
literal|"/test"
argument_list|,
name|mk3
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// mk3 didn't see the previous change yet;
comment|// it is questionable if this should prevent any changes to this node
comment|// (currently it does not)
name|assertEquals
argument_list|(
literal|"{\":childNodeCount\":0}"
argument_list|,
name|n3
argument_list|)
expr_stmt|;
name|mk3
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"test/y\":2"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk3
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|mk3
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|mk1
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|mk1
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|String
name|r1
init|=
name|mk1
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|n1
init|=
name|mk1
operator|.
name|getNodes
argument_list|(
literal|"/test"
argument_list|,
name|r1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// mk1 only sees the change of mk3 so far
name|assertEquals
argument_list|(
literal|"{\"y\":2,\":childNodeCount\":0}"
argument_list|,
name|n1
argument_list|)
expr_stmt|;
name|mk2
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|mk2
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|mk1
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|mk1
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|String
name|r1b
init|=
name|mk1
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|n1b
init|=
name|mk1
operator|.
name|getNodes
argument_list|(
literal|"/test"
argument_list|,
name|r1b
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|JSONParser
name|parser
init|=
operator|new
name|JSONParser
argument_list|()
decl_stmt|;
name|JSONObject
name|obj
init|=
operator|(
name|JSONObject
operator|)
name|parser
operator|.
name|parse
argument_list|(
name|n1b
argument_list|)
decl_stmt|;
comment|// mk1 now sees both changes
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|obj
operator|.
name|get
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2L
argument_list|,
name|obj
operator|.
name|get
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|obj
operator|.
name|get
argument_list|(
literal|":childNodeCount"
argument_list|)
argument_list|)
expr_stmt|;
name|mk1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk2
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk3
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|clusterNodeInfoLease
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|MemoryDocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|ClusterNodeInfo
name|c1
decl_stmt|,
name|c2
decl_stmt|;
name|c1
operator|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|"m1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|c1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|c1
operator|.
name|setLeaseTime
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// this will quickly expire
name|c1
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|c2
operator|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|"m1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|c2
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|openCloseOpen
parameter_list|()
block|{
name|MemoryDocumentStore
name|ds
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|MemoryBlobStore
name|bs
init|=
operator|new
name|MemoryBlobStore
argument_list|()
decl_stmt|;
name|DocumentMK
name|mk1
init|=
name|createMK
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
name|ds
argument_list|,
name|bs
argument_list|)
decl_stmt|;
name|mk1
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\": {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk1
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"a\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|DocumentMK
name|mk2
init|=
name|createMK
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
name|ds
argument_list|,
name|bs
argument_list|)
decl_stmt|;
name|mk2
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\": {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk2
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"a\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|DocumentMK
name|mk3
init|=
name|createMK
argument_list|(
literal|3
argument_list|,
literal|0
argument_list|,
name|ds
argument_list|,
name|bs
argument_list|)
decl_stmt|;
name|mk3
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\": {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk3
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"a\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk3
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|DocumentMK
name|mk4
init|=
name|createMK
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|,
name|ds
argument_list|,
name|bs
argument_list|)
decl_stmt|;
name|mk4
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\": {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk4
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|DocumentMK
name|mk5
init|=
name|createMK
argument_list|(
literal|5
argument_list|,
literal|0
argument_list|,
name|ds
argument_list|,
name|bs
argument_list|)
decl_stmt|;
name|mk5
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"a\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk5
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\": {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk2
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk3
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk4
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk5
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|clusterNodeId
parameter_list|()
block|{
name|DocumentMK
name|mk1
init|=
name|createMK
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|DocumentMK
name|mk2
init|=
name|createMK
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mk1
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|mk2
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|mk1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk2
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|clusterBranchInVisibility
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|DocumentMK
name|mk1
init|=
name|createMK
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|mk1
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"regular\": {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|b1
init|=
name|mk1
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|String
name|b2
init|=
name|mk1
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|b1
operator|=
name|mk1
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"branchVisible\": {}"
argument_list|,
name|b1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|b2
operator|=
name|mk1
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"branchInvisible\": {}"
argument_list|,
name|b2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk1
operator|.
name|merge
argument_list|(
name|b1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// mk1.merge only becomes visible to mk2 after async delay
comment|// therefore dispose mk1 now to make sure it flushes
comment|// unsaved last revisions
name|mk1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|DocumentMK
name|mk2
init|=
name|createMK
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|String
name|nodes
init|=
name|mk2
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"branchVisible\":{},\"regular\":{},\":childNodeCount\":2}"
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
name|mk2
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
comment|/**      * Test for OAK-1254      */
annotation|@
name|Test
specifier|public
name|void
name|clusterBranchRebase
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentMK
name|mk1
init|=
name|createMK
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|mk1
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|DocumentMK
name|mk2
init|=
name|createMK
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DocumentMK
name|mk3
init|=
name|createMK
argument_list|(
literal|3
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DocumentNodeStore
name|ns3
init|=
name|mk3
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
comment|// the next line is required for the test even if it
comment|// just reads from the node store. do not remove!
name|traverse
argument_list|(
name|ns3
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|String
name|b3
init|=
name|mk3
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|b3
operator|=
name|mk3
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"mk3\":{}"
argument_list|,
name|b3
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk3
operator|.
name|nodeExists
argument_list|(
literal|"/test"
argument_list|,
name|b3
argument_list|)
argument_list|)
expr_stmt|;
name|mk2
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test/mk21\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|mk3
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// pick up changes from mk2
name|String
name|base3
init|=
name|mk3
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|mk3
operator|.
name|nodeExists
argument_list|(
literal|"/test/mk21"
argument_list|,
name|b3
argument_list|)
argument_list|)
expr_stmt|;
name|b3
operator|=
name|mk3
operator|.
name|rebase
argument_list|(
name|b3
argument_list|,
name|base3
argument_list|)
expr_stmt|;
name|mk2
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test/mk22\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|mk3
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// pick up changes from mk2
name|DocumentNodeState
name|base
init|=
name|ns3
operator|.
name|getNode
argument_list|(
literal|"/"
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
name|base3
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|NodeState
name|branchHead
init|=
name|ns3
operator|.
name|getNode
argument_list|(
literal|"/"
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
name|b3
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|branchHead
argument_list|)
expr_stmt|;
name|TrackingDiff
name|diff
init|=
operator|new
name|TrackingDiff
argument_list|()
decl_stmt|;
name|branchHead
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|diff
operator|.
name|added
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"/mk3"
argument_list|)
argument_list|,
name|diff
operator|.
name|added
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|,
name|diff
operator|.
name|deleted
argument_list|)
expr_stmt|;
name|mk1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk2
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk3
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|clusterNodeInfo
parameter_list|()
block|{
name|MemoryDocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|ClusterNodeInfo
name|c1
decl_stmt|,
name|c2
decl_stmt|,
name|c3
decl_stmt|,
name|c4
decl_stmt|;
name|c1
operator|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|"m1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|c1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|c1
operator|.
name|dispose
argument_list|()
expr_stmt|;
comment|// get the same id
name|c1
operator|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|"m1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|c1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|c1
operator|.
name|dispose
argument_list|()
expr_stmt|;
comment|// now try to add another one:
comment|// must get a new id
name|c2
operator|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|"m2"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|c2
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// a different machine
name|c3
operator|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|"m3"
argument_list|,
literal|"/a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|c3
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|c2
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|c3
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|c3
operator|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|"m3"
argument_list|,
literal|"/a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|c3
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|c3
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|c4
operator|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|"m3"
argument_list|,
literal|"/b"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|c4
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|c1
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
block|{
name|DocumentMK
name|mk1
init|=
name|createMK
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DocumentMK
name|mk2
init|=
name|createMK
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|String
name|m1r0
init|=
name|mk1
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|m2r0
init|=
name|mk2
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|mk1
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\":{}"
argument_list|,
name|m1r0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|mk2
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\":{}"
argument_list|,
name|m2r0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MicroKernelException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|mk1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|mk2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// node becomes visible after running background operations
name|String
name|n1
init|=
name|mk1
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|mk1
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|n2
init|=
name|mk2
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|mk2
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|n1
argument_list|,
name|n2
argument_list|)
expr_stmt|;
name|mk1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk2
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|revisionVisibility
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|DocumentMK
name|mk1
init|=
name|createMK
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|DocumentMK
name|mk2
init|=
name|createMK
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|String
name|m2h
decl_stmt|;
name|m2h
operator|=
name|mk2
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|mk2
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\":childNodeCount\":0}"
argument_list|,
name|m2h
argument_list|)
expr_stmt|;
name|String
name|oldHead
init|=
name|mk2
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|mk1
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|m1h
init|=
name|mk1
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|mk1
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"test\":{},\":childNodeCount\":1}"
argument_list|,
name|m1h
argument_list|)
expr_stmt|;
comment|// not available yet...
name|assertEquals
argument_list|(
literal|"{\":childNodeCount\":0}"
argument_list|,
name|m2h
argument_list|)
expr_stmt|;
name|m2h
operator|=
name|mk2
operator|.
name|getNodes
argument_list|(
literal|"/test"
argument_list|,
name|mk2
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// the delay is 10 ms - wait at most 1000 millis
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
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
if|if
condition|(
name|mk1
operator|.
name|getPendingWriteCount
argument_list|()
operator|>
literal|0
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|mk2
operator|.
name|getHeadRevision
argument_list|()
operator|.
name|equals
argument_list|(
name|oldHead
argument_list|)
condition|)
block|{
continue|continue;
block|}
break|break;
block|}
comment|// so now it should be available
name|m2h
operator|=
name|mk2
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|mk2
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\"test\":{},\":childNodeCount\":1}"
argument_list|,
name|m2h
argument_list|)
expr_stmt|;
name|mk1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk2
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rollbackAfterConflict
parameter_list|()
block|{
name|DocumentMK
name|mk1
init|=
name|createMK
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|DocumentMK
name|mk2
init|=
name|createMK
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|String
name|m1r0
init|=
name|mk1
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|m2r0
init|=
name|mk2
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|mk1
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\":{}"
argument_list|,
name|m1r0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|mk2
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\": {} +\"test\":{}"
argument_list|,
name|m2r0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MicroKernelException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|mk2
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\": {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk2
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
annotation|@
name|After
specifier|public
name|void
name|clear
parameter_list|()
block|{
if|if
condition|(
name|MONGO_DB
condition|)
block|{
name|DB
name|db
init|=
name|MongoUtils
operator|.
name|getConnection
argument_list|()
operator|.
name|getDB
argument_list|()
decl_stmt|;
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|db
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|DocumentMK
name|createMK
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
return|return
name|createMK
argument_list|(
name|clusterId
argument_list|,
literal|10
argument_list|)
return|;
block|}
specifier|private
name|DocumentMK
name|createMK
parameter_list|(
name|int
name|clusterId
parameter_list|,
name|int
name|asyncDelay
parameter_list|)
block|{
if|if
condition|(
name|MONGO_DB
condition|)
block|{
name|DB
name|db
init|=
name|MongoUtils
operator|.
name|getConnection
argument_list|()
operator|.
name|getDB
argument_list|()
decl_stmt|;
return|return
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|db
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
name|asyncDelay
argument_list|)
operator|.
name|open
argument_list|()
return|;
block|}
else|else
block|{
if|if
condition|(
name|ds
operator|==
literal|null
condition|)
block|{
name|ds
operator|=
operator|new
name|MemoryDocumentStore
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|bs
operator|==
literal|null
condition|)
block|{
name|bs
operator|=
operator|new
name|MemoryBlobStore
argument_list|()
expr_stmt|;
block|}
return|return
name|createMK
argument_list|(
name|clusterId
argument_list|,
name|asyncDelay
argument_list|,
name|ds
argument_list|,
name|bs
argument_list|)
return|;
block|}
block|}
specifier|private
name|DocumentMK
name|createMK
parameter_list|(
name|int
name|clusterId
parameter_list|,
name|int
name|asyncDelay
parameter_list|,
name|DocumentStore
name|ds
parameter_list|,
name|BlobStore
name|bs
parameter_list|)
block|{
return|return
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|ds
argument_list|)
operator|.
name|setBlobStore
argument_list|(
name|bs
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
name|asyncDelay
argument_list|)
operator|.
name|open
argument_list|()
return|;
block|}
specifier|private
name|void
name|traverse
parameter_list|(
name|NodeState
name|node
parameter_list|,
name|String
name|path
parameter_list|)
block|{
for|for
control|(
name|ChildNodeEntry
name|child
range|:
name|node
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|traverse
argument_list|(
name|child
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|child
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|TrackingDiff
extends|extends
name|DefaultNodeStateDiff
block|{
specifier|final
name|String
name|path
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|added
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|deleted
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|modified
decl_stmt|;
name|TrackingDiff
parameter_list|()
block|{
name|this
argument_list|(
literal|"/"
argument_list|,
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|,
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|,
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|TrackingDiff
parameter_list|(
name|String
name|path
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|added
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|deleted
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|modified
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|added
operator|=
name|added
expr_stmt|;
name|this
operator|.
name|deleted
operator|=
name|deleted
expr_stmt|;
name|this
operator|.
name|modified
operator|=
name|modified
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|added
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
return|return
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|EMPTY_NODE
argument_list|,
operator|new
name|TrackingDiff
argument_list|(
name|p
argument_list|,
name|added
argument_list|,
name|deleted
argument_list|,
name|modified
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|modified
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
return|return
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|TrackingDiff
argument_list|(
name|p
argument_list|,
name|added
argument_list|,
name|deleted
argument_list|,
name|modified
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|deleted
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
return|return
name|MISSING_NODE
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|TrackingDiff
argument_list|(
name|p
argument_list|,
name|added
argument_list|,
name|deleted
argument_list|,
name|modified
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

