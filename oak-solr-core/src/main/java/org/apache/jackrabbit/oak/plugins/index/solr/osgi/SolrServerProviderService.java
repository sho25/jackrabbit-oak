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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|PropertyOption
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
name|ReferenceCardinality
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
name|ReferencePolicy
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
name|References
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
name|SolrServerProvider
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
comment|/**  * OSGi service for {@link org.apache.jackrabbit.oak.plugins.index.solr.server.SolrServerProvider}  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|metatype
operator|=
literal|true
argument_list|,
name|label
operator|=
literal|"Oak Solr server provider"
argument_list|,
name|immediate
operator|=
literal|true
argument_list|)
annotation|@
name|References
argument_list|(
block|{
annotation|@
name|Reference
argument_list|(
name|name
operator|=
literal|"solrServerConfigurationProvider"
argument_list|,
name|referenceInterface
operator|=
name|SolrServerConfigurationProvider
operator|.
name|class
argument_list|,
name|cardinality
operator|=
name|ReferenceCardinality
operator|.
name|MANDATORY_MULTIPLE
argument_list|,
name|policy
operator|=
name|ReferencePolicy
operator|.
name|DYNAMIC
argument_list|,
name|bind
operator|=
literal|"bindSolrServerConfigurationProvider"
argument_list|,
name|unbind
operator|=
literal|"unbindSolrServerConfigurationProvider"
argument_list|,
name|updated
operator|=
literal|"updateSolrServerConfigurationProvider"
argument_list|)
block|}
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
name|SolrServerProviderService
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
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|options
operator|=
block|{
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
literal|"none"
argument_list|,
name|value
operator|=
literal|"None"
argument_list|)
block|,
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
literal|"embedded"
argument_list|,
name|value
operator|=
literal|"Embedded Solr"
argument_list|)
block|,
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
literal|"remote"
argument_list|,
name|value
operator|=
literal|"Remote Solr"
argument_list|)
block|}
argument_list|,
name|value
operator|=
literal|"none"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|SERVER_TYPE
init|=
literal|"server.type"
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SolrServerConfigurationProvider
argument_list|>
name|solrServerConfigurationProviders
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SolrServerConfigurationProvider
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|String
name|serverType
decl_stmt|;
specifier|private
name|SolrServer
name|cachedSolrServer
decl_stmt|;
annotation|@
name|Activate
specifier|protected
name|void
name|activate
parameter_list|(
name|ComponentContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|serverType
operator|=
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
name|SERVER_TYPE
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
name|solrServerConfigurationProviders
operator|.
name|clear
argument_list|()
expr_stmt|;
name|shutdownSolrServer
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|shutdownSolrServer
parameter_list|()
block|{
if|if
condition|(
name|cachedSolrServer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|cachedSolrServer
operator|.
name|shutdown
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
name|error
argument_list|(
literal|"could not correctly shutdown Solr {} server {}"
argument_list|,
name|serverType
argument_list|,
name|cachedSolrServer
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cachedSolrServer
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|bindSolrServerConfigurationProvider
parameter_list|(
specifier|final
name|SolrServerConfigurationProvider
name|solrServerConfigurationProvider
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
block|{
synchronized|synchronized
init|(
name|solrServerConfigurationProviders
init|)
block|{
name|String
name|name
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|properties
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
decl_stmt|;
name|solrServerConfigurationProviders
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|solrServerConfigurationProvider
argument_list|)
expr_stmt|;
name|shutdownSolrServer
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|unbindSolrServerConfigurationProvider
parameter_list|(
specifier|final
name|SolrServerConfigurationProvider
name|solrServerConfigurationProvider
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
block|{
synchronized|synchronized
init|(
name|solrServerConfigurationProviders
init|)
block|{
name|String
name|name
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|properties
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
decl_stmt|;
name|solrServerConfigurationProviders
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|shutdownSolrServer
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|updatedSolrServerConfigurationProvider
parameter_list|(
specifier|final
name|SolrServerConfigurationProvider
name|solrServerConfigurationProvider
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
block|{
synchronized|synchronized
init|(
name|solrServerConfigurationProviders
init|)
block|{
name|String
name|name
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|properties
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
decl_stmt|;
name|solrServerConfigurationProviders
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|solrServerConfigurationProvider
argument_list|)
expr_stmt|;
name|shutdownSolrServer
argument_list|()
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
synchronized|synchronized
init|(
name|solrServerConfigurationProviders
init|)
block|{
if|if
condition|(
name|cachedSolrServer
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|serverType
operator|!=
literal|null
operator|&&
operator|!
literal|"none"
operator|.
name|equals
argument_list|(
name|serverType
argument_list|)
condition|)
block|{
name|SolrServerConfigurationProvider
name|solrServerConfigurationProvider
init|=
name|solrServerConfigurationProviders
operator|.
name|get
argument_list|(
name|serverType
argument_list|)
decl_stmt|;
if|if
condition|(
name|solrServerConfigurationProvider
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|SolrServerConfiguration
name|solrServerConfiguration
init|=
name|solrServerConfigurationProvider
operator|.
name|getSolrServerConfiguration
argument_list|()
decl_stmt|;
name|SolrServerProvider
name|solrServerProvider
init|=
name|solrServerConfiguration
operator|.
name|getProvider
argument_list|()
decl_stmt|;
name|cachedSolrServer
operator|=
name|solrServerProvider
operator|.
name|getSolrServer
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
name|error
argument_list|(
literal|"could not get a SolrServerProvider of type {}"
argument_list|,
name|serverType
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|cachedSolrServer
return|;
block|}
block|}
block|}
end_class

end_unit

