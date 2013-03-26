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
name|server
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
name|oak
operator|.
name|spi
operator|.
name|query
operator|.
name|PropertyValues
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
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * Testcase for {@link DefaultOakSolrProvider}  */
end_comment

begin_class
specifier|public
class|class
name|DefaultOakSolrProviderTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testSolrServerInitializationWithoutConfigurationFiles
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|nodeState
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|nodeState
operator|.
name|getProperty
argument_list|(
name|OakSolrNodeStateConfiguration
operator|.
name|Properties
operator|.
name|CORE_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PropertyValues
operator|.
name|create
argument_list|(
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"oak"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|nodeState
operator|.
name|getProperty
argument_list|(
name|OakSolrNodeStateConfiguration
operator|.
name|Properties
operator|.
name|SOLRHOME_PATH
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PropertyValues
operator|.
name|create
argument_list|(
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"target/solr/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|nodeState
operator|.
name|getProperty
argument_list|(
name|OakSolrNodeStateConfiguration
operator|.
name|Properties
operator|.
name|SOLRCONFIG_PATH
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PropertyValues
operator|.
name|create
argument_list|(
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"target/solr/solr.xml"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|DefaultOakSolrProvider
name|defaultOakSolrProvider
init|=
operator|new
name|DefaultOakSolrProvider
argument_list|(
name|nodeState
argument_list|)
decl_stmt|;
name|SolrServer
name|solrServer
init|=
name|defaultOakSolrProvider
operator|.
name|getSolrServer
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|solrServer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

