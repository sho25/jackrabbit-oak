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
name|osgi
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|SolrServerConfiguration
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
name|SolrServerConfigurationDefaults
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
name|SolrServerConfigurationProvider
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
name|RemoteSolrServerProvider
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

begin_comment
comment|/**  * {@link org.apache.jackrabbit.oak.plugins.index.solr.server.SolrServerProvider} for remote Solr installations.  */
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
argument_list|,
name|label
operator|=
literal|"Oak Solr remote server configuration"
argument_list|)
annotation|@
name|Service
argument_list|(
name|SolrServerConfigurationProvider
operator|.
name|class
argument_list|)
annotation|@
name|Property
argument_list|(
name|name
operator|=
literal|"name"
argument_list|,
name|value
operator|=
literal|"remote"
argument_list|,
name|propertyPrivate
operator|=
literal|true
argument_list|)
specifier|public
class|class
name|RemoteSolrServerConfigurationProvider
implements|implements
name|SolrServerConfigurationProvider
argument_list|<
name|RemoteSolrServerProvider
argument_list|>
block|{
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|SolrServerConfigurationDefaults
operator|.
name|HTTP_URL
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
name|SolrServerConfigurationDefaults
operator|.
name|ZK_HOST
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
name|SolrServerConfigurationDefaults
operator|.
name|COLLECTION
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
name|SolrServerConfigurationDefaults
operator|.
name|SHARDS_NO
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
name|SolrServerConfigurationDefaults
operator|.
name|REPLICATION_FACTOR
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
name|SolrServerConfigurationDefaults
operator|.
name|CONFIGURATION_DIRECTORY
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
specifier|private
name|RemoteSolrServerConfiguration
name|remoteSolrServerConfiguration
decl_stmt|;
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
name|remoteSolrServerConfiguration
operator|=
operator|new
name|RemoteSolrServerConfiguration
argument_list|(
name|solrZkHost
argument_list|,
name|solrCollection
argument_list|,
name|solrShardsNo
argument_list|,
name|solrReplicationFactor
argument_list|,
name|solrConfDir
argument_list|,
name|solrHttpUrl
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
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|SolrServerConfiguration
argument_list|<
name|RemoteSolrServerProvider
argument_list|>
name|getSolrServerConfiguration
parameter_list|()
block|{
return|return
name|remoteSolrServerConfiguration
return|;
block|}
block|}
end_class

end_unit

