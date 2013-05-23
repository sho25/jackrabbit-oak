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
name|index
operator|.
name|solr
operator|.
name|index
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
name|MicroKernel
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
name|core
operator|.
name|MicroKernelImpl
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
name|kernel
operator|.
name|KernelNodeStore
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
name|index
operator|.
name|solr
operator|.
name|SolrBaseTest
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
comment|/**  * Testcase for {@link org.apache.jackrabbit.oak.plugins.index.solr.index.SolrCommitHook}  */
end_comment

begin_class
specifier|public
class|class
name|SolrCommitHookTest
extends|extends
name|SolrBaseTest
block|{
specifier|private
name|KernelNodeStore
name|store
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|MicroKernel
name|microKernel
init|=
operator|new
name|MicroKernelImpl
argument_list|()
decl_stmt|;
name|store
operator|=
operator|new
name|KernelNodeStore
argument_list|(
name|microKernel
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNothingHappened
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCommitHook
name|solrCommitHook
init|=
operator|new
name|SolrCommitHook
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|NodeState
name|before
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|after
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|changedState
init|=
name|solrCommitHook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|after
argument_list|,
name|changedState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddNode
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCommitHook
name|solrCommitHook
init|=
operator|new
name|SolrCommitHook
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|NodeState
name|before
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|after
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|child
argument_list|(
literal|"somechild"
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|changedState
init|=
name|solrCommitHook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|after
argument_list|,
name|changedState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveNode
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCommitHook
name|solrCommitHook
init|=
operator|new
name|SolrCommitHook
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|NodeState
name|before
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|child
argument_list|(
literal|"somechild"
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|before
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"somechild"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|changedState
init|=
name|solrCommitHook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|after
argument_list|,
name|changedState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertyAdded
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCommitHook
name|solrCommitHook
init|=
operator|new
name|SolrCommitHook
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|NodeState
name|before
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|after
init|=
name|before
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"v"
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|changedState
init|=
name|solrCommitHook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|after
argument_list|,
name|changedState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertyRemoved
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCommitHook
name|solrCommitHook
init|=
operator|new
name|SolrCommitHook
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|NodeState
name|before
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"v"
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|after
init|=
name|before
operator|.
name|builder
argument_list|()
operator|.
name|removeProperty
argument_list|(
literal|"p"
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|changedState
init|=
name|solrCommitHook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|after
argument_list|,
name|changedState
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

