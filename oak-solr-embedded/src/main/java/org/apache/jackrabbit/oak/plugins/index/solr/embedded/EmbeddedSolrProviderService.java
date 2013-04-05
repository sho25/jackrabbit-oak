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
name|embedded
package|;
end_package

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
name|Reference
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
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Services
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
name|OakSolrConfiguration
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
name|OakSolrConfigurationProvider
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
comment|/**  * OSGi service for the embedded Solr server module.  */
end_comment

begin_class
annotation|@
name|Component
annotation|@
name|Services
argument_list|(
block|{
annotation|@
name|Service
argument_list|(
name|value
operator|=
name|SolrServerProvider
operator|.
name|class
argument_list|)
block|,
annotation|@
name|Service
argument_list|(
name|value
operator|=
name|OakSolrConfigurationProvider
operator|.
name|class
argument_list|)
block|}
argument_list|)
specifier|public
class|class
name|EmbeddedSolrProviderService
implements|implements
name|SolrServerProvider
implements|,
name|OakSolrConfigurationProvider
block|{
annotation|@
name|Property
argument_list|(
name|value
operator|=
literal|"/oak:index/solrIdx"
argument_list|,
name|name
operator|=
literal|"configuration path"
argument_list|,
name|description
operator|=
literal|"path to node holding Solr configuration"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|CONFIGURATION_PATH
init|=
literal|"solr.configuration.node.path"
decl_stmt|;
annotation|@
name|Reference
specifier|private
name|NodeStore
name|nodeStore
decl_stmt|;
annotation|@
name|Reference
specifier|private
name|SolrServerConfigurationProvider
name|solrServerConfigurationProvider
decl_stmt|;
specifier|private
name|OakSolrConfigurationProvider
name|oakSolrConfigurationProvider
decl_stmt|;
specifier|private
name|SolrServerProvider
name|solrServerProvider
decl_stmt|;
annotation|@
name|Activate
specifier|public
name|void
name|activate
parameter_list|(
name|ComponentContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
comment|// try reading configuration from the configured repository path
name|UpToDateNodeStateConfiguration
name|nodeStateConfiguration
init|=
operator|new
name|UpToDateNodeStateConfiguration
argument_list|(
name|nodeStore
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|context
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|CONFIGURATION_PATH
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|solrServerProvider
operator|=
operator|new
name|DefaultOakSolrProvider
argument_list|(
name|nodeStateConfiguration
operator|.
name|getSolrServerConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|oakSolrConfigurationProvider
operator|=
operator|new
name|EmbeddedSolrConfigurationProvider
argument_list|(
name|nodeStateConfiguration
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// use the default config and the OSGi based server configuration
name|solrServerProvider
operator|=
operator|new
name|DefaultOakSolrProvider
argument_list|(
name|solrServerConfigurationProvider
operator|.
name|getSolrServerConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|oakSolrConfigurationProvider
operator|=
operator|new
name|EmbeddedSolrConfigurationProvider
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|OakSolrConfiguration
name|getConfiguration
parameter_list|()
block|{
return|return
name|oakSolrConfigurationProvider
operator|.
name|getConfiguration
argument_list|()
return|;
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
return|return
name|solrServerProvider
operator|.
name|getSolrServer
argument_list|()
return|;
block|}
block|}
end_class

end_unit

