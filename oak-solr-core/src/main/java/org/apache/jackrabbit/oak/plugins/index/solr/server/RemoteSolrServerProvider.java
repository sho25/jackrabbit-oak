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
name|io
operator|.
name|IOException
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
name|SolrServerException
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
name|impl
operator|.
name|HttpSolrServer
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
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|ZkController
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
name|cloud
operator|.
name|SolrZkClient
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
name|cloud
operator|.
name|ZkStateReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * {@link org.apache.jackrabbit.oak.plugins.index.solr.server.SolrServerProvider} for remote Solr installations.  */
end_comment

begin_class
specifier|public
class|class
name|RemoteSolrServerProvider
implements|implements
name|SolrServerProvider
block|{
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RemoteSolrServerProvider
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|RemoteSolrServerConfiguration
name|remoteSolrServerConfiguration
decl_stmt|;
specifier|private
name|SolrServer
name|solrServer
decl_stmt|;
specifier|public
name|RemoteSolrServerProvider
parameter_list|(
name|RemoteSolrServerConfiguration
name|remoteSolrServerConfiguration
parameter_list|)
block|{
name|this
operator|.
name|remoteSolrServerConfiguration
operator|=
name|remoteSolrServerConfiguration
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SolrServer
name|getSolrServer
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|solrServer
operator|==
literal|null
operator|&&
name|remoteSolrServerConfiguration
operator|.
name|getSolrZkHost
argument_list|()
operator|!=
literal|null
operator|&&
name|remoteSolrServerConfiguration
operator|.
name|getSolrZkHost
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|solrServer
operator|=
name|initializeWithCloudSolrServer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"unable to initialize SolrCloud client for {}"
argument_list|,
name|remoteSolrServerConfiguration
operator|.
name|getSolrZkHost
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|solrServer
operator|==
literal|null
operator|&&
name|remoteSolrServerConfiguration
operator|.
name|getSolrHttpUrls
argument_list|()
operator|!=
literal|null
operator|&&
name|remoteSolrServerConfiguration
operator|.
name|getSolrHttpUrls
argument_list|()
operator|.
name|length
operator|==
literal|1
operator|&&
name|remoteSolrServerConfiguration
operator|.
name|getSolrHttpUrls
argument_list|()
index|[
literal|0
index|]
operator|!=
literal|null
operator|&&
name|remoteSolrServerConfiguration
operator|.
name|getSolrHttpUrls
argument_list|()
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|solrServer
operator|=
name|initializeWithExistingHttpServer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"unable to initialize Solr HTTP client for {}"
argument_list|,
name|remoteSolrServerConfiguration
operator|.
name|getSolrHttpUrls
argument_list|()
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|solrServer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"could not connect to any remote Solr server"
argument_list|)
throw|;
block|}
return|return
name|solrServer
return|;
block|}
specifier|private
name|SolrServer
name|initializeWithExistingHttpServer
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
comment|// try basic Solr HTTP client
name|HttpSolrServer
name|httpSolrServer
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|remoteSolrServerConfiguration
operator|.
name|getSolrHttpUrls
argument_list|()
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|SolrPingResponse
name|ping
init|=
name|httpSolrServer
operator|.
name|ping
argument_list|()
decl_stmt|;
if|if
condition|(
name|ping
operator|!=
literal|null
operator|&&
literal|0
operator|==
name|ping
operator|.
name|getStatus
argument_list|()
condition|)
block|{
return|return
name|httpSolrServer
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"the found HTTP Solr server is not alive"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|SolrServer
name|initializeWithCloudSolrServer
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
comment|// try SolrCloud client
name|CloudSolrServer
name|cloudSolrServer
init|=
operator|new
name|CloudSolrServer
argument_list|(
name|remoteSolrServerConfiguration
operator|.
name|getSolrZkHost
argument_list|()
argument_list|)
decl_stmt|;
name|cloudSolrServer
operator|.
name|setZkConnectTimeout
argument_list|(
literal|100
argument_list|)
expr_stmt|;
if|if
condition|(
name|connectToZK
argument_list|(
name|cloudSolrServer
argument_list|)
condition|)
block|{
name|cloudSolrServer
operator|.
name|setDefaultCollection
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
comment|// workaround for first request when the needed collection may not exist
comment|// create specified collection if it doesn't exists
try|try
block|{
name|createCollectionIfNeeded
argument_list|(
name|cloudSolrServer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|log
operator|.
name|isWarnEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"could not create the collection on {}"
argument_list|,
name|remoteSolrServerConfiguration
operator|.
name|getSolrZkHost
argument_list|()
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
name|cloudSolrServer
operator|.
name|setDefaultCollection
argument_list|(
name|remoteSolrServerConfiguration
operator|.
name|getSolrCollection
argument_list|()
argument_list|)
expr_stmt|;
comment|// SolrCloud may need some time to sync on collection creation (to spread it over the shards / replicas)
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
literal|3
condition|)
block|{
try|try
block|{
name|SolrPingResponse
name|ping
init|=
name|cloudSolrServer
operator|.
name|ping
argument_list|()
decl_stmt|;
if|if
condition|(
name|ping
operator|!=
literal|null
operator|&&
literal|0
operator|==
name|ping
operator|.
name|getStatus
argument_list|()
condition|)
block|{
return|return
name|cloudSolrServer
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"the found SolrCloud server is not alive"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// wait a bit
try|try
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"server is not alive yet, wait a bit"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
name|i
operator|++
expr_stmt|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"the found SolrCloud server is not alive"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"could not connect to Zookeeper hosted at "
operator|+
name|remoteSolrServerConfiguration
operator|.
name|getSolrZkHost
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
name|boolean
name|connectToZK
parameter_list|(
name|CloudSolrServer
name|cloudSolrServer
parameter_list|)
block|{
name|boolean
name|connected
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|cloudSolrServer
operator|.
name|connect
argument_list|()
expr_stmt|;
name|connected
operator|=
literal|true
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"could not connect to ZK"
argument_list|,
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
block|}
return|return
name|connected
return|;
block|}
specifier|private
name|void
name|createCollectionIfNeeded
parameter_list|(
name|CloudSolrServer
name|cloudSolrServer
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|String
name|solrCollection
init|=
name|remoteSolrServerConfiguration
operator|.
name|getSolrCollection
argument_list|()
decl_stmt|;
try|try
block|{
name|ZkStateReader
name|zkStateReader
init|=
name|cloudSolrServer
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
if|if
condition|(
name|zkClient
operator|.
name|isConnected
argument_list|()
operator|&&
operator|!
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/configs/"
operator|+
name|solrCollection
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|String
name|solrConfDir
init|=
name|remoteSolrServerConfiguration
operator|.
name|getSolrConfDir
argument_list|()
decl_stmt|;
name|File
name|dir
decl_stmt|;
if|if
condition|(
name|solrConfDir
operator|!=
literal|null
operator|&&
name|solrConfDir
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|dir
operator|=
operator|new
name|File
argument_list|(
name|solrConfDir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dir
operator|=
operator|new
name|File
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"/solr/oak/conf"
argument_list|)
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ZkController
operator|.
name|uploadConfigDir
argument_list|(
name|zkClient
argument_list|,
name|dir
argument_list|,
name|solrCollection
argument_list|)
expr_stmt|;
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
name|req
operator|.
name|setParam
argument_list|(
literal|"numShards"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|remoteSolrServerConfiguration
operator|.
name|getSolrShardsNo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParam
argument_list|(
literal|"replicationFactor"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|remoteSolrServerConfiguration
operator|.
name|getSolrReplicationFactor
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParam
argument_list|(
literal|"collection.configName"
argument_list|,
name|solrCollection
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParam
argument_list|(
literal|"name"
argument_list|,
name|solrCollection
argument_list|)
expr_stmt|;
name|cloudSolrServer
operator|.
name|request
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"could not create collection {}"
argument_list|,
name|solrCollection
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrServerException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

