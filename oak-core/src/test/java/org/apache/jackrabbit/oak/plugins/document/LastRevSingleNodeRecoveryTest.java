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
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Assume
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
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_comment
comment|/**  * Tests the restore of potentially missing _lastRev updates.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|LastRevSingleNodeRecoveryTest
block|{
specifier|private
name|DocumentStoreFixture
name|fixture
decl_stmt|;
specifier|private
name|Clock
name|clock
decl_stmt|;
specifier|private
name|DocumentMK
name|mk
decl_stmt|;
specifier|private
name|DocumentMK
name|mk2
decl_stmt|;
specifier|public
name|LastRevSingleNodeRecoveryTest
parameter_list|(
name|DocumentStoreFixture
name|fixture
parameter_list|)
block|{
name|this
operator|.
name|fixture
operator|=
name|fixture
expr_stmt|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|fixtures
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|fixtures
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|DocumentStoreFixture
name|mongo
init|=
operator|new
name|DocumentStoreFixture
operator|.
name|MongoFixture
argument_list|()
decl_stmt|;
if|if
condition|(
name|mongo
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
name|fixtures
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|mongo
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|fixtures
return|;
block|}
specifier|private
name|DocumentMK
name|createMK
parameter_list|(
name|int
name|clusterId
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|clock
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
return|return
name|openMK
argument_list|(
name|clusterId
argument_list|,
name|fixture
operator|.
name|createDocumentStore
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|DocumentMK
name|openMK
parameter_list|(
name|int
name|clusterId
parameter_list|,
name|DocumentStore
name|store
parameter_list|)
throws|throws
name|InterruptedException
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
comment|// Sets the clock for testing
name|ClusterNodeInfo
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|Revision
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|DocumentMK
operator|.
name|Builder
name|builder
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setAsyncDelay
argument_list|(
literal|0
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
name|setLeaseCheck
argument_list|(
literal|false
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|mk
operator|=
name|builder
operator|.
name|open
argument_list|()
expr_stmt|;
name|clock
operator|.
name|waitUntil
argument_list|(
name|Revision
operator|.
name|getCurrentTimestamp
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|mk
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|InterruptedException
block|{
try|try
block|{
name|mk
operator|=
name|createMK
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Assume
operator|.
name|assumeNotNull
argument_list|(
name|mk
argument_list|)
expr_stmt|;
comment|// initialize node hierarchy
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"x\" : { \"y\": {\"z\":{} } }"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : { \"b\": {\"c\": {}} }"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assume
operator|.
name|assumeNoException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLastRevRestoreOnNodeStart
parameter_list|()
throws|throws
name|Exception
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
name|mk
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getLeaseTime
argument_list|()
operator|+
literal|10
argument_list|)
expr_stmt|;
comment|// pending updates
name|setupScenario
argument_list|()
expr_stmt|;
comment|// renew lease
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|mk
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getLeaseTime
argument_list|()
operator|+
literal|10
argument_list|)
expr_stmt|;
name|mk
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|renewLease
argument_list|()
expr_stmt|;
comment|// so that the current time is more than the current lease end
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|mk
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getLeaseTime
argument_list|()
operator|+
literal|1000
argument_list|)
expr_stmt|;
comment|// Recreate mk instance, to simulate fail condition and recovery on start
comment|// Make sure to use a different variable for cleanup ; mk should not be disposed here
name|mk2
operator|=
name|openMK
argument_list|(
literal|0
argument_list|,
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getDocumentStore
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|pendingCount
init|=
name|mk2
operator|.
name|getPendingWriteCount
argument_list|()
decl_stmt|;
comment|// Immediately check again, now should not have done any changes.
name|LastRevRecoveryAgent
name|recoveryAgent
init|=
name|mk2
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getLastRevRecoveryAgent
argument_list|()
decl_stmt|;
comment|/** Now there should have been pendingCount updates **/
name|assertEquals
argument_list|(
name|pendingCount
argument_list|,
name|recoveryAgent
operator|.
name|recover
argument_list|(
name|mk2
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLastRevRestore
parameter_list|()
throws|throws
name|Exception
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
name|mk
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getLeaseTime
argument_list|()
operator|+
literal|10
argument_list|)
expr_stmt|;
name|setupScenario
argument_list|()
expr_stmt|;
name|int
name|pendingCount
init|=
name|mk
operator|.
name|getPendingWriteCount
argument_list|()
decl_stmt|;
comment|// so that the current time is more than the current lease end
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|mk
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getLeaseTime
argument_list|()
operator|+
literal|1000
argument_list|)
expr_stmt|;
name|LastRevRecoveryAgent
name|recoveryAgent
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getLastRevRecoveryAgent
argument_list|()
decl_stmt|;
comment|/** All pending updates should have been restored **/
name|assertEquals
argument_list|(
name|pendingCount
argument_list|,
name|recoveryAgent
operator|.
name|recover
argument_list|(
name|mk
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNoMissingUpdates
parameter_list|()
throws|throws
name|Exception
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
name|mk
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getLeaseTime
argument_list|()
operator|+
literal|10
argument_list|)
expr_stmt|;
name|setupScenario
argument_list|()
expr_stmt|;
name|mk
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
comment|// move the time forward and do another update of the root node so that only 2 nodes are
comment|// candidates
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
literal|5000
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"a/key2\" : \"value2\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|mk
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getLeaseTime
argument_list|()
argument_list|)
expr_stmt|;
name|mk
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|renewLease
argument_list|()
expr_stmt|;
comment|// Should be 0
name|int
name|pendingCount
init|=
name|mk
operator|.
name|getPendingWriteCount
argument_list|()
decl_stmt|;
name|LastRevRecoveryAgent
name|recoveryAgent
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getLastRevRecoveryAgent
argument_list|()
decl_stmt|;
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|mk
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getLeaseTime
argument_list|()
argument_list|)
expr_stmt|;
comment|/** There should have been no updates **/
name|assertEquals
argument_list|(
name|pendingCount
argument_list|,
name|recoveryAgent
operator|.
name|recover
argument_list|(
name|mk
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNodeRecoveryNeeded
parameter_list|()
throws|throws
name|InterruptedException
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
name|mk
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getLeaseTime
argument_list|()
operator|+
literal|10
argument_list|)
expr_stmt|;
name|setupScenario
argument_list|()
expr_stmt|;
comment|// so that the current time is more than the current lease end
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|mk
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getLeaseTime
argument_list|()
operator|+
literal|1000
argument_list|)
expr_stmt|;
name|LastRevRecoveryAgent
name|recoveryAgent
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getLastRevRecoveryAgent
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|cids
init|=
name|recoveryAgent
operator|.
name|getRecoveryCandidateNodes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|Iterables
operator|.
name|size
argument_list|(
name|cids
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Iterables
operator|.
name|get
argument_list|(
name|cids
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setupScenario
parameter_list|()
throws|throws
name|InterruptedException
block|{
comment|// add some nodes which won't be returned
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"u\" : { \"v\": {}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/u"
argument_list|,
literal|"^\"v/key1\" : \"value1\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// move the time forward so that the root gets updated
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
literal|5000
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"a/key1\" : \"value1\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
comment|// move the time forward to have a new node under root
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
literal|5000
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"p\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// move the time forward to write all pending changes
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
literal|5000
argument_list|)
expr_stmt|;
name|mk
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
comment|// renew lease one last time
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|mk
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|getLeaseTime
argument_list|()
argument_list|)
expr_stmt|;
name|mk
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
literal|5000
argument_list|)
expr_stmt|;
comment|// add nodes won't trigger _lastRev updates
name|addNodes
argument_list|()
expr_stmt|;
block|}
comment|/**      * Should have the      */
specifier|private
name|void
name|addNodes
parameter_list|()
block|{
comment|// change node /a/b/c by adding a property
name|mk
operator|.
name|commit
argument_list|(
literal|"/a/b"
argument_list|,
literal|"^\"c/key1\" : \"value1\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// add node /a/b/c/d
name|mk
operator|.
name|commit
argument_list|(
literal|"/a/b/c"
argument_list|,
literal|"+\"d\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// add node /a/b/f
name|mk
operator|.
name|commit
argument_list|(
literal|"/a/b"
argument_list|,
literal|"+\"f\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// add node /a/b/f/e
name|mk
operator|.
name|commit
argument_list|(
literal|"/a/b/f"
argument_list|,
literal|"+\"e\": {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// change node /x/y/z
name|mk
operator|.
name|commit
argument_list|(
literal|"/x/y"
argument_list|,
literal|"^\"z/key1\" : \"value1\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
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
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
if|if
condition|(
name|mk2
operator|!=
literal|null
condition|)
block|{
name|mk2
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
name|fixture
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

