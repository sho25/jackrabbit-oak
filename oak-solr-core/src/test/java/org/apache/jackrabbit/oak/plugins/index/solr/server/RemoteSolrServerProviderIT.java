begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|plugins
operator|.
name|index
operator|.
name|solr
operator|.
name|configuration
operator|.
name|RemoteSolrServerConfiguration
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
name|SolrServer
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
name|impl
operator|.
name|CloudSolrClient
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
name|impl
operator|.
name|CloudSolrServer
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
name|request
operator|.
name|UpdateRequest
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
name|common
operator|.
name|util
operator|.
name|NamedList
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

begin_comment
comment|/**  * Testcase for {@link RemoteSolrServerProvider}  */
end_comment

begin_class
specifier|public
class|class
name|RemoteSolrServerProviderIT
block|{
comment|// common local zk hosts
specifier|private
specifier|final
name|String
index|[]
name|zkHosts
init|=
operator|new
name|String
index|[]
block|{
literal|"localhost:9983"
block|}
decl_stmt|;
specifier|private
name|boolean
name|canCreateCollections
parameter_list|(
name|String
name|host
parameter_list|)
throws|throws
name|Exception
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|(
literal|"/admin/collections"
argument_list|)
decl_stmt|;
name|req
operator|.
name|setParam
argument_list|(
literal|"action"
argument_list|,
literal|"CREATE"
argument_list|)
expr_stmt|;
name|String
name|solrCollection
init|=
literal|"solr_"
operator|+
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|req
operator|.
name|setParam
argument_list|(
literal|"name"
argument_list|,
name|solrCollection
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParam
argument_list|(
literal|"numShards"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParam
argument_list|(
literal|"replicationFactor"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParam
argument_list|(
literal|"collection.configName"
argument_list|,
literal|"myconf"
argument_list|)
expr_stmt|;
name|CloudSolrClient
name|cloudSolrServer
init|=
operator|new
name|CloudSolrClient
argument_list|(
name|host
argument_list|)
decl_stmt|;
name|cloudSolrServer
operator|.
name|setZkConnectTimeout
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|request
init|=
name|cloudSolrServer
operator|.
name|request
argument_list|(
name|req
argument_list|)
decl_stmt|;
return|return
name|request
operator|!=
literal|null
operator|&&
name|request
operator|.
name|get
argument_list|(
literal|"success"
argument_list|)
operator|!=
literal|null
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCloudRemoteServerCreation
parameter_list|()
throws|throws
name|Exception
block|{
comment|// do this test only if a Solr Cloud server is available
for|for
control|(
name|String
name|host
range|:
name|zkHosts
control|)
block|{
name|boolean
name|cloudServerAvailable
init|=
literal|false
decl_stmt|;
try|try
block|{
name|cloudServerAvailable
operator|=
name|canCreateCollections
argument_list|(
name|host
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// do nothing
block|}
if|if
condition|(
name|cloudServerAvailable
condition|)
block|{
name|String
name|collection
init|=
literal|"sample_"
operator|+
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|RemoteSolrServerProvider
name|remoteSolrServerProvider
init|=
operator|new
name|RemoteSolrServerProvider
argument_list|(
operator|new
name|RemoteSolrServerConfiguration
argument_list|(
name|host
argument_list|,
name|collection
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|SolrClient
name|solrServer
init|=
name|remoteSolrServerProvider
operator|.
name|getSolrServer
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|solrServer
argument_list|)
expr_stmt|;
name|solrServer
operator|.
name|close
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

