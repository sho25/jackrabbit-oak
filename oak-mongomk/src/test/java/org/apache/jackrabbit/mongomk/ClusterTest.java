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
name|fail
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
name|mk
operator|.
name|blobs
operator|.
name|MemoryBlobStore
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
name|com
operator|.
name|mongodb
operator|.
name|DB
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
argument_list|(
literal|1
argument_list|)
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
name|MongoMK
operator|.
name|Builder
name|builder
decl_stmt|;
name|builder
operator|=
operator|new
name|MongoMK
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
expr_stmt|;
name|MongoMK
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
name|builder
operator|=
operator|new
name|MongoMK
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
expr_stmt|;
name|MongoMK
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
name|builder
operator|=
operator|new
name|MongoMK
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
expr_stmt|;
name|MongoMK
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
name|builder
operator|=
operator|new
name|MongoMK
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
expr_stmt|;
name|MongoMK
name|mk4
init|=
name|builder
operator|.
name|setClusterId
argument_list|(
literal|4
argument_list|)
operator|.
name|open
argument_list|()
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
name|builder
operator|=
operator|new
name|MongoMK
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
expr_stmt|;
name|MongoMK
name|mk5
init|=
name|builder
operator|.
name|setClusterId
argument_list|(
literal|5
argument_list|)
operator|.
name|open
argument_list|()
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
name|MongoMK
name|mk1
init|=
name|createMK
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|MongoMK
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
block|{
name|MongoMK
name|mk1
init|=
name|createMK
argument_list|(
literal|0
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
name|MongoMK
name|mk2
init|=
name|createMK
argument_list|(
literal|0
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
name|MongoMK
name|mk1
init|=
name|createMK
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|MongoMK
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
comment|// now, after the conflict, both cluster nodes see the node
comment|// (before the conflict, this isn't necessarily the case for mk2)
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
name|MongoMK
name|mk1
init|=
name|createMK
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|MongoMK
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
literal|1
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
name|MongoMK
name|mk1
init|=
name|createMK
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|MongoMK
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
name|MongoMK
name|createMK
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
name|MongoMK
operator|.
name|Builder
name|builder
init|=
operator|new
name|MongoMK
operator|.
name|Builder
argument_list|()
decl_stmt|;
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
name|builder
operator|.
name|setMongoDB
argument_list|(
name|db
argument_list|)
expr_stmt|;
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
expr_stmt|;
block|}
name|builder
operator|.
name|setAsyncDelay
argument_list|(
literal|10
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|open
argument_list|()
return|;
block|}
block|}
end_class

end_unit

