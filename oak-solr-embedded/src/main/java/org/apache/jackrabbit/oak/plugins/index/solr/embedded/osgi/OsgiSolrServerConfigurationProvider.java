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
operator|.
name|osgi
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
comment|/**  * An OSGi service {@link SolrServerConfigurationProvider}  */
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
literal|"OSGi Embedded Solr server configuration provider"
argument_list|)
annotation|@
name|Service
argument_list|(
name|value
operator|=
name|SolrServerConfigurationProvider
operator|.
name|class
argument_list|)
specifier|public
class|class
name|OsgiSolrServerConfigurationProvider
implements|implements
name|SolrServerConfigurationProvider
block|{
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|SolrServerConfigurationDefaults
operator|.
name|SOLR_HOME_PATH
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_HOME_PATH
init|=
literal|"solr.home.path"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|SolrServerConfigurationDefaults
operator|.
name|CORE_NAME
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_CORE_NAME
init|=
literal|"solr.core.name"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|SolrServerConfigurationDefaults
operator|.
name|SOLR_CONFIG_PATH
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_CONFIG_FILE
init|=
literal|"solr.config.path"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|SolrServerConfigurationDefaults
operator|.
name|HTTP_PORT
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_HTTP_PORT
init|=
literal|"solr.http.port"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|SolrServerConfigurationDefaults
operator|.
name|CONTEXT
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_CONTEXT
init|=
literal|"solr.context"
decl_stmt|;
specifier|private
specifier|static
name|SolrServer
name|solrServer
decl_stmt|;
specifier|private
name|String
name|solrHome
decl_stmt|;
specifier|private
name|String
name|solrConfigFile
decl_stmt|;
specifier|private
name|String
name|solrCoreName
decl_stmt|;
specifier|private
name|Integer
name|solrHttpPort
decl_stmt|;
specifier|private
name|String
name|solrContext
decl_stmt|;
specifier|private
name|SolrServerConfiguration
name|solrServerConfiguration
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
name|solrHome
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
name|SOLR_HOME_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|solrHome
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
assert|assert
name|file
operator|.
name|createNewFile
argument_list|()
assert|;
block|}
name|solrConfigFile
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
name|SOLR_CONFIG_FILE
argument_list|)
argument_list|)
expr_stmt|;
name|solrCoreName
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
name|SOLR_CORE_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|solrHttpPort
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
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
name|SOLR_HTTP_PORT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|solrContext
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
name|SOLR_CONTEXT
argument_list|)
argument_list|)
expr_stmt|;
name|solrServerConfiguration
operator|=
operator|new
name|SolrServerConfiguration
argument_list|(
name|solrHome
argument_list|,
name|solrConfigFile
argument_list|,
name|solrCoreName
argument_list|)
operator|.
name|withHttpConfiguration
argument_list|(
name|solrContext
argument_list|,
name|solrHttpPort
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
name|solrHome
operator|=
literal|null
expr_stmt|;
name|solrHttpPort
operator|=
literal|null
expr_stmt|;
name|solrConfigFile
operator|=
literal|null
expr_stmt|;
name|solrCoreName
operator|=
literal|null
expr_stmt|;
name|solrContext
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
name|SolrServerConfiguration
name|getSolrServerConfiguration
parameter_list|()
block|{
return|return
name|solrServerConfiguration
return|;
block|}
block|}
end_class

end_unit

