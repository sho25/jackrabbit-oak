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
name|embedded
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
name|NodeStore
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

begin_comment
comment|/**  * Testcase for {@link UpToDateNodeStateConfiguration}  */
end_comment

begin_class
specifier|public
class|class
name|UpToDateNodeStateConfigurationTest
block|{
specifier|private
name|NodeStore
name|store
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
name|String
name|jsop
init|=
literal|"^\"a\":1 ^\"b\":2 ^\"c\":3 +\"x\":{} +\"y\":{} +\"z\":{} "
operator|+
literal|"+\"oak:index\":{\"solrIdx\":{\"coreName\":\"cn\", \"solrHomePath\":\"sh\", \"solrConfigPath\":\"sc\"}} "
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
name|testExistingPath
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"oak:index/solrIdx"
decl_stmt|;
name|UpToDateNodeStateConfiguration
name|upToDateNodeStateConfiguration
init|=
operator|new
name|UpToDateNodeStateConfiguration
argument_list|(
name|store
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|SolrServerConfiguration
name|solrServerConfiguration
init|=
name|upToDateNodeStateConfiguration
operator|.
name|getSolrServerConfiguration
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|solrServerConfiguration
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sh"
argument_list|,
name|solrServerConfiguration
operator|.
name|getSolrHomePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// property defined in the node state
name|assertEquals
argument_list|(
literal|"cn"
argument_list|,
name|solrServerConfiguration
operator|.
name|getCoreName
argument_list|()
argument_list|)
expr_stmt|;
comment|// property defined in the node state
name|assertEquals
argument_list|(
literal|"sc"
argument_list|,
name|solrServerConfiguration
operator|.
name|getSolrConfigPath
argument_list|()
argument_list|)
expr_stmt|;
comment|// property defined in the node state
name|assertEquals
argument_list|(
literal|"path_exact"
argument_list|,
name|upToDateNodeStateConfiguration
operator|.
name|getPathField
argument_list|()
argument_list|)
expr_stmt|;
comment|// using default as this property not defined in the node state
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonExistingPath
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"some/path/to/oak:index/solrIdx"
decl_stmt|;
name|UpToDateNodeStateConfiguration
name|upToDateNodeStateConfiguration
init|=
operator|new
name|UpToDateNodeStateConfiguration
argument_list|(
name|store
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|upToDateNodeStateConfiguration
operator|.
name|getSolrServerConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNodeStateNotFound
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"some/path/to/somewhere/unknown"
decl_stmt|;
name|UpToDateNodeStateConfiguration
name|upToDateNodeStateConfiguration
init|=
operator|new
name|UpToDateNodeStateConfiguration
argument_list|(
name|store
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|upToDateNodeStateConfiguration
operator|.
name|getConfigurationNodeState
argument_list|()
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

