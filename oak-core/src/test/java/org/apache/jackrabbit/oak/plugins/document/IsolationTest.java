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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests if commits to branches and trunk are properly isolated and repository  * state on a given revision is stable.  */
end_comment

begin_class
specifier|public
class|class
name|IsolationTest
extends|extends
name|BaseDocumentMKTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|phantomReadOnBranch
parameter_list|()
block|{
name|String
name|base
init|=
name|mk
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
decl_stmt|;
name|String
name|branchRev1
init|=
name|mk
operator|.
name|branch
argument_list|(
name|base
argument_list|)
decl_stmt|;
name|String
name|branchRev2
init|=
name|mk
operator|.
name|branch
argument_list|(
name|base
argument_list|)
decl_stmt|;
name|branchRev1
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/test"
argument_list|,
literal|"+\"node1\":{}"
argument_list|,
name|branchRev1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|branchRev2
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/test"
argument_list|,
literal|"+\"node2\":{}"
argument_list|,
name|branchRev2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|r
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/test"
argument_list|,
literal|"+\"node3\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// branchRev1 must not see node3 at this point
name|assertNodesNotExist
argument_list|(
name|branchRev1
argument_list|,
literal|"/test/node3"
argument_list|)
expr_stmt|;
comment|// this will make node3 visible to branchRev1
name|branchRev1
operator|=
name|mk
operator|.
name|rebase
argument_list|(
name|branchRev1
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|assertNodesExist
argument_list|(
name|branchRev1
argument_list|,
literal|"/test/node1"
argument_list|,
literal|"/test/node3"
argument_list|)
expr_stmt|;
name|assertNodesNotExist
argument_list|(
name|branchRev1
argument_list|,
literal|"/test/node2"
argument_list|)
expr_stmt|;
comment|// merging second branch must not have an effect on
comment|// rebased first branch
name|mk
operator|.
name|merge
argument_list|(
name|branchRev2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertNodesExist
argument_list|(
name|branchRev1
argument_list|,
literal|"/test/node1"
argument_list|,
literal|"/test/node3"
argument_list|)
expr_stmt|;
name|assertNodesNotExist
argument_list|(
name|branchRev1
argument_list|,
literal|"/test/node2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|phantomReadOnTrunk
parameter_list|()
block|{
name|String
name|base
init|=
name|mk
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
decl_stmt|;
name|String
name|branchRev1
init|=
name|mk
operator|.
name|branch
argument_list|(
name|base
argument_list|)
decl_stmt|;
name|branchRev1
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/test"
argument_list|,
literal|"+\"node1\":{}"
argument_list|,
name|branchRev1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|rev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/test"
argument_list|,
literal|"+\"node2\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// rev must not see node1
name|assertNodesNotExist
argument_list|(
name|rev
argument_list|,
literal|"/test/node1"
argument_list|)
expr_stmt|;
name|mk
operator|.
name|merge
argument_list|(
name|branchRev1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// rev still must not see node1
name|assertNodesNotExist
argument_list|(
name|rev
argument_list|,
literal|"/test/node1"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

