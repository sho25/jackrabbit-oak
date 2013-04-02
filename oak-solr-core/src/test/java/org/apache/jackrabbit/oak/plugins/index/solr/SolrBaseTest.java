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
package|;
end_package

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
name|core
operator|.
name|RootImpl
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrServer
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

begin_comment
comment|/**  * Test base class for Oak-Solr  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SolrBaseTest
block|{
specifier|protected
name|KernelNodeStore
name|store
decl_stmt|;
specifier|protected
name|NodeState
name|state
decl_stmt|;
specifier|protected
name|SolrServer
name|server
decl_stmt|;
specifier|protected
name|OakSolrConfiguration
name|configuration
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
name|state
operator|=
name|createInitialState
argument_list|(
name|microKernel
argument_list|)
expr_stmt|;
name|server
operator|=
name|TestUtils
operator|.
name|createSolrServer
argument_list|()
expr_stmt|;
name|configuration
operator|=
name|TestUtils
operator|.
name|getTestConfiguration
argument_list|()
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
if|if
condition|(
name|server
operator|!=
literal|null
operator|&&
name|server
operator|.
name|ping
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|server
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|NodeState
name|createInitialState
parameter_list|(
name|MicroKernel
name|microKernel
parameter_list|)
block|{
name|String
name|jsop
init|=
literal|"^\"a\":1 ^\"b\":2 ^\"c\":3 +\"x\":{} +\"y\":{} +\"z\":{} "
operator|+
literal|"+\"solrIdx\":{\"core\":\"oak\", \"solrHome\":\""
operator|+
name|TestUtils
operator|.
name|SOLR_HOME_PATH
operator|+
literal|"\", \"solrConfig\":\""
operator|+
name|TestUtils
operator|.
name|SOLRCONFIG_PATH
operator|+
literal|"\"} "
decl_stmt|;
name|microKernel
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|jsop
argument_list|,
name|microKernel
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|"test data"
argument_list|)
expr_stmt|;
return|return
name|store
operator|.
name|getRoot
argument_list|()
return|;
block|}
specifier|protected
name|RootImpl
name|createRootImpl
parameter_list|()
block|{
return|return
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|)
return|;
block|}
block|}
end_class

end_unit

