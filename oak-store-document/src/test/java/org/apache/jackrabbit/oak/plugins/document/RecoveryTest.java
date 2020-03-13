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
name|migration
operator|.
name|NodeStateTestUtils
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
name|NodeState
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
name|disposeQuietly
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
name|migration
operator|.
name|NodeStateTestUtils
operator|.
name|assertExists
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
name|migration
operator|.
name|NodeStateTestUtils
operator|.
name|assertMissing
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
name|containsInAnyOrder
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
name|equalTo
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
name|RecoveryTest
extends|extends
name|AbstractTwoNodeTest
block|{
specifier|private
name|FailingDocumentStore
name|fds1
decl_stmt|;
specifier|public
name|RecoveryTest
parameter_list|(
name|DocumentStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|DocumentStore
name|customize
parameter_list|(
name|DocumentStore
name|store
parameter_list|)
block|{
comment|// wrap the first store with a FailingDocumentStore
name|FailingDocumentStore
name|fds
init|=
operator|new
name|FailingDocumentStore
argument_list|(
name|store
argument_list|)
decl_stmt|;
if|if
condition|(
name|fds1
operator|==
literal|null
condition|)
block|{
name|fds1
operator|=
name|fds
expr_stmt|;
block|}
return|return
name|fds
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|recoverOther
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"node"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c1"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|child
argument_list|(
literal|"other"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c1"
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
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|ds2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|builder
operator|=
name|ds2
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"node"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c2"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|child
argument_list|(
literal|"other"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c2"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ds2
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|ds2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|ds1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|waitOneMinute
argument_list|()
expr_stmt|;
name|ds1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|ds2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|ds1
operator|.
name|renewClusterIdLease
argument_list|()
expr_stmt|;
name|ds2
operator|.
name|renewClusterIdLease
argument_list|()
expr_stmt|;
comment|// apply several changes without background update
comment|// and then simulate a killed process
name|builder
operator|=
name|ds1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"node"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c1"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c3"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ds1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|=
name|ds1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|child
argument_list|(
literal|"other"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c3"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ds1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|=
name|ds1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"node"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ds1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|=
name|ds1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"node"
argument_list|)
operator|.
name|child
argument_list|(
literal|"wont-make-it"
argument_list|)
expr_stmt|;
comment|// simulate crashed process
name|fds1
operator|.
name|fail
argument_list|()
operator|.
name|after
argument_list|(
literal|1
argument_list|)
operator|.
name|eternally
argument_list|()
expr_stmt|;
try|try
block|{
name|merge
argument_list|(
name|ds1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"merge must fail"
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
name|disposeQuietly
argument_list|(
name|ds1
argument_list|)
expr_stmt|;
name|waitOneMinute
argument_list|()
expr_stmt|;
name|ds2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|ds2
operator|.
name|renewClusterIdLease
argument_list|()
expr_stmt|;
name|listChildren
argument_list|(
name|ds2
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|listChildren
argument_list|(
name|ds2
argument_list|,
literal|"/parent"
argument_list|)
expr_stmt|;
name|listChildren
argument_list|(
name|ds2
argument_list|,
literal|"/parent/test"
argument_list|)
expr_stmt|;
name|builder
operator|=
name|ds2
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"node"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"q"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ds2
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|waitOneMinute
argument_list|()
expr_stmt|;
name|ds2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|ds2
operator|.
name|renewClusterIdLease
argument_list|()
expr_stmt|;
name|waitOneMinute
argument_list|()
expr_stmt|;
name|ds2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|ds2
operator|.
name|renewClusterIdLease
argument_list|()
expr_stmt|;
comment|// before recovery, changes by ds1 are not visible
name|NodeState
name|root
init|=
name|ds2
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|assertExists
argument_list|(
name|root
argument_list|,
literal|"parent/test/c1"
argument_list|)
expr_stmt|;
name|assertMissing
argument_list|(
name|root
argument_list|,
literal|"parent/test/c3"
argument_list|)
expr_stmt|;
name|assertMissing
argument_list|(
name|root
argument_list|,
literal|"node/wont-make-it"
argument_list|)
expr_stmt|;
comment|// clusterId 1 lease expired
name|assertTrue
argument_list|(
name|ds2
operator|.
name|getLastRevRecoveryAgent
argument_list|()
operator|.
name|isRecoveryNeeded
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|numDocs
init|=
name|ds2
operator|.
name|getLastRevRecoveryAgent
argument_list|()
operator|.
name|recover
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|numDocs
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
comment|// still not visible because background read did not yet happen
name|NodeState
name|root1
init|=
name|ds2
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|assertExists
argument_list|(
name|root1
argument_list|,
literal|"parent/test/c1"
argument_list|)
expr_stmt|;
name|assertMissing
argument_list|(
name|root1
argument_list|,
literal|"parent/test/c3"
argument_list|)
expr_stmt|;
name|assertMissing
argument_list|(
name|root1
argument_list|,
literal|"node/wont-make-it"
argument_list|)
expr_stmt|;
name|ds2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// now changes must be visible
name|NodeState
name|root2
init|=
name|ds2
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|assertMissing
argument_list|(
name|root2
argument_list|,
literal|"parent/test/c1"
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
name|root2
argument_list|,
literal|"parent/test/c3"
argument_list|)
expr_stmt|;
name|assertMissing
argument_list|(
name|root2
argument_list|,
literal|"node/wont-make-it"
argument_list|)
expr_stmt|;
name|TrackingDiff
name|diff
init|=
operator|new
name|TrackingDiff
argument_list|()
decl_stmt|;
name|root2
operator|.
name|compareAgainstBaseState
argument_list|(
name|root1
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|diff
operator|.
name|modified
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/parent"
argument_list|,
literal|"/parent/other"
argument_list|,
literal|"/parent/test"
argument_list|,
literal|"/node"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|diff
operator|.
name|added
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/parent/test/c3"
argument_list|,
literal|"/parent/other/c3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|diff
operator|.
name|deleted
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/parent/test/c1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|waitOneMinute
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
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|listChildren
parameter_list|(
name|NodeStore
name|ns
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|NodeStateTestUtils
operator|.
name|getNodeState
argument_list|(
name|ns
operator|.
name|getRoot
argument_list|()
argument_list|,
name|path
argument_list|)
operator|.
name|getChildNodeEntries
argument_list|()
operator|.
name|forEach
argument_list|(
name|ChildNodeEntry
operator|::
name|getNodeState
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

