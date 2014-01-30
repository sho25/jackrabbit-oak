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
name|http
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
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Deactivate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|server
operator|.
name|SolrServerProvider
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
name|util
operator|.
name|OakSolrUtils
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
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|ComponentContext
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
comment|/**  * {@link SolrServerProvider} for remote Solr installations.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|metatype
operator|=
literal|true
argument_list|,
name|immediate
operator|=
literal|true
argument_list|)
annotation|@
name|Service
argument_list|(
name|SolrServerProvider
operator|.
name|class
argument_list|)
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
specifier|static
specifier|final
name|String
name|DEFAULT_COLLECTION
init|=
literal|"oak"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_HTTP_URL
init|=
literal|"http://127.0.0.1:8983/solr/oak"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_ZK_HOST
init|=
literal|"localhost:9983"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_SHARDS_NO
init|=
literal|2
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_REPLICATION_FACTOR
init|=
literal|2
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|DEFAULT_HTTP_URL
argument_list|,
name|label
operator|=
literal|"Solr HTTP URL"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_HTTP_URL
init|=
literal|"solr.http.url"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|DEFAULT_ZK_HOST
argument_list|,
name|label
operator|=
literal|"ZooKeeper host"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_ZK_HOST
init|=
literal|"solr.zk.host"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|DEFAULT_COLLECTION
argument_list|,
name|label
operator|=
literal|"Solr collection"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_COLLECTION
init|=
literal|"solr.collection"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|intValue
operator|=
name|DEFAULT_SHARDS_NO
argument_list|,
name|label
operator|=
literal|"No. of collection shards"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_SHARDS_NO
init|=
literal|"solr.shards.no"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|intValue
operator|=
name|DEFAULT_REPLICATION_FACTOR
argument_list|,
name|label
operator|=
literal|"Replication factor"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_REPLICATION_FACTOR
init|=
literal|"solr.replication.factor"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
literal|""
argument_list|,
name|label
operator|=
literal|"Solr configuration directory"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_CONF_DIR
init|=
literal|"solr.conf.dir"
decl_stmt|;
specifier|private
name|SolrServer
name|solrServer
decl_stmt|;
specifier|private
name|String
name|solrHttpUrl
decl_stmt|;
specifier|private
name|String
name|solrZkHost
decl_stmt|;
specifier|private
name|String
name|solrCollection
decl_stmt|;
specifier|private
name|int
name|solrShardsNo
decl_stmt|;
specifier|private
name|int
name|solrReplicationFactor
decl_stmt|;
specifier|private
name|String
name|solrConfDir
decl_stmt|;
specifier|public
name|RemoteSolrServerProvider
parameter_list|()
block|{
name|this
operator|.
name|solrHttpUrl
operator|=
name|DEFAULT_HTTP_URL
expr_stmt|;
name|this
operator|.
name|solrZkHost
operator|=
name|DEFAULT_ZK_HOST
expr_stmt|;
name|this
operator|.
name|solrCollection
operator|=
name|DEFAULT_COLLECTION
expr_stmt|;
name|this
operator|.
name|solrShardsNo
operator|=
name|DEFAULT_SHARDS_NO
expr_stmt|;
name|this
operator|.
name|solrReplicationFactor
operator|=
name|DEFAULT_REPLICATION_FACTOR
expr_stmt|;
block|}
specifier|public
name|RemoteSolrServerProvider
parameter_list|(
name|String
name|solrHttpUrl
parameter_list|,
name|String
name|solrZkHost
parameter_list|,
name|String
name|solrCollection
parameter_list|,
name|int
name|solrShardsNo
parameter_list|,
name|int
name|solrReplicationFactor
parameter_list|,
name|String
name|solrConfDir
parameter_list|)
block|{
name|this
operator|.
name|solrHttpUrl
operator|=
name|solrHttpUrl
expr_stmt|;
name|this
operator|.
name|solrZkHost
operator|=
name|solrZkHost
expr_stmt|;
name|this
operator|.
name|solrCollection
operator|=
name|solrCollection
expr_stmt|;
name|this
operator|.
name|solrShardsNo
operator|=
name|solrShardsNo
expr_stmt|;
name|this
operator|.
name|solrReplicationFactor
operator|=
name|solrReplicationFactor
expr_stmt|;
name|this
operator|.
name|solrConfDir
operator|=
name|solrConfDir
expr_stmt|;
block|}
annotation|@
name|Activate
specifier|protected
name|void
name|activate
parameter_list|(
name|ComponentContext
name|componentContext
parameter_list|)
throws|throws
name|Exception
block|{
name|solrHttpUrl
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|componentContext
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|SOLR_HTTP_URL
argument_list|)
argument_list|)
expr_stmt|;
name|solrZkHost
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|componentContext
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|SOLR_ZK_HOST
argument_list|)
argument_list|)
expr_stmt|;
name|solrCollection
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|componentContext
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|SOLR_COLLECTION
argument_list|)
argument_list|)
expr_stmt|;
name|solrShardsNo
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|componentContext
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|SOLR_SHARDS_NO
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|solrReplicationFactor
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|componentContext
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|SOLR_REPLICATION_FACTOR
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|solrConfDir
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|componentContext
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|SOLR_CONF_DIR
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deactivate
specifier|protected
name|void
name|deactivate
parameter_list|()
throws|throws
name|Exception
block|{
name|solrHttpUrl
operator|=
literal|null
expr_stmt|;
name|solrZkHost
operator|=
literal|null
expr_stmt|;
name|solrCollection
operator|=
literal|null
expr_stmt|;
name|solrShardsNo
operator|=
literal|0
expr_stmt|;
name|solrReplicationFactor
operator|=
literal|0
expr_stmt|;
name|solrConfDir
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|solrServer
operator|!=
literal|null
condition|)
block|{
name|solrServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|solrServer
operator|=
literal|null
expr_stmt|;
block|}
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
literal|"unable to initialize SolrCloud client"
argument_list|,
name|e
argument_list|)
expr_stmt|;
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
literal|"unable to initialize Solr HTTP client"
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
literal|"could not connect to any HTTP Solr server"
argument_list|)
throw|;
block|}
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
name|solrHttpUrl
argument_list|)
decl_stmt|;
if|if
condition|(
name|OakSolrUtils
operator|.
name|checkServerAlive
argument_list|(
name|httpSolrServer
argument_list|)
condition|)
block|{
comment|// TODO : check if the oak core exists, otherwise create it
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
name|solrZkHost
argument_list|)
decl_stmt|;
name|cloudSolrServer
operator|.
name|connect
argument_list|()
expr_stmt|;
name|cloudSolrServer
operator|.
name|setDefaultCollection
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
comment|// workaround for first request when the needed collection may not exist
comment|// create specified collection if it doesn't exists
name|createCollectionIfNeeded
argument_list|(
name|cloudSolrServer
argument_list|)
expr_stmt|;
name|cloudSolrServer
operator|.
name|setDefaultCollection
argument_list|(
name|solrCollection
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
name|OakSolrUtils
operator|.
name|checkServerAlive
argument_list|(
name|cloudSolrServer
argument_list|)
expr_stmt|;
return|return
name|cloudSolrServer
return|;
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
name|isWarnEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"wait a bit"
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
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|solrConfDir
operator|!=
literal|null
condition|?
name|solrConfDir
else|:
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"/solr-oak-conf"
argument_list|)
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
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
name|solrShardsNo
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
name|solrReplicationFactor
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

