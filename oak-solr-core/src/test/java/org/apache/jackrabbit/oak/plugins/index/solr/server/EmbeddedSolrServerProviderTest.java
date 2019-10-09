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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|configuration
operator|.
name|EmbeddedSolrServerConfiguration
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
name|SolrClient
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
name|response
operator|.
name|SolrPingResponse
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

begin_comment
comment|/**  * Testcase for {@link EmbeddedSolrServerProvider}  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|EmbeddedSolrServerProviderTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testEmbeddedSolrServerInitialization
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|uri
init|=
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"/solr"
argument_list|)
operator|.
name|toURI
argument_list|()
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|EmbeddedSolrServerConfiguration
name|solrServerConfiguration
init|=
operator|new
name|EmbeddedSolrServerConfiguration
argument_list|(
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"oak"
argument_list|)
decl_stmt|;
name|EmbeddedSolrServerProvider
name|embeddedSolrServerProvider
init|=
operator|new
name|EmbeddedSolrServerProvider
argument_list|(
name|solrServerConfiguration
argument_list|)
decl_stmt|;
name|SolrClient
name|solrServer
init|=
name|embeddedSolrServerProvider
operator|.
name|getSolrServer
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|solrServer
argument_list|)
expr_stmt|;
try|try
block|{
name|SolrPingResponse
name|ping
init|=
name|solrServer
operator|.
name|ping
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|ping
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ping
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|solrServer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

