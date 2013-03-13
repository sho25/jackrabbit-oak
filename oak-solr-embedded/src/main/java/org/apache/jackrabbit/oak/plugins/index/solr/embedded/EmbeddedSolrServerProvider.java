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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|OakSolrUtils
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
name|lucene
operator|.
name|codecs
operator|.
name|Codec
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
name|embedded
operator|.
name|EmbeddedSolrServer
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
name|embedded
operator|.
name|JettySolrRunner
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
name|core
operator|.
name|CoreContainer
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * {@link SolrServerProvider} which (spawns if needed and) exposes an embedded Solr server  */
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
name|EmbeddedSolrServerProvider
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
name|EmbeddedSolrServerProvider
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_PORT
init|=
literal|"8983"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_HOME_PATH
init|=
literal|"/"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_CORE_NAME
init|=
literal|"oak"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_HOME_PROPERTY_NAME
init|=
literal|"solr.solr.home"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|LOCAL_BASE_URL
init|=
literal|"http://127.0.0.1"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONTEXT
init|=
literal|"/solr"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|DEFAULT_HOME_PATH
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
name|DEFAULT_PORT
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_HTTP_PORT
init|=
literal|"solr.http.port"
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
name|Integer
name|solrHttpPort
decl_stmt|;
specifier|public
name|EmbeddedSolrServerProvider
parameter_list|()
block|{
name|this
operator|.
name|solrHome
operator|=
name|DEFAULT_HOME_PATH
expr_stmt|;
name|this
operator|.
name|solrHttpPort
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|DEFAULT_PORT
argument_list|)
expr_stmt|;
block|}
specifier|public
name|EmbeddedSolrServerProvider
parameter_list|(
name|String
name|solrHome
parameter_list|,
name|Integer
name|solrHttpPort
parameter_list|)
block|{
name|this
operator|.
name|solrHome
operator|=
name|solrHome
expr_stmt|;
name|this
operator|.
name|solrHttpPort
operator|=
name|solrHttpPort
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
comment|// TODO : add the possibility to inject solrconfig and schema files
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
comment|// hack needed to let lucene SPIs work in an OSGi deploy
comment|//        Thread thread = Thread.currentThread();
comment|//        ClassLoader loader = thread.getContextClassLoader();
comment|//        thread.setContextClassLoader(Lucene40Codec.class.getClassLoader());
comment|//        thread.setContextClassLoader(loader);
name|Codec
operator|.
name|reloadCodecs
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
argument_list|)
expr_stmt|;
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
name|initializeWithNewHttpServer
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
literal|"unable to spawn a new Solr server and initialize the default Solr HTTP client"
argument_list|)
expr_stmt|;
try|try
block|{
name|solrServer
operator|=
name|initializeWithEmbeddedSolrServer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"unable to initialize embedded Solr client"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"unable to initialize an embedded Solr server"
argument_list|,
name|e2
argument_list|)
throw|;
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
literal|"could not connect to any embedded Solr server"
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
name|initializeWithNewHttpServer
parameter_list|()
throws|throws
name|Exception
block|{
comment|// try spawning a new Solr server using Jetty and connect to it via HTTP
name|enableSolrCloud
argument_list|(
name|solrHome
argument_list|,
name|DEFAULT_CORE_NAME
argument_list|)
expr_stmt|;
name|JettySolrRunner
name|jettySolrRunner
init|=
operator|new
name|JettySolrRunner
argument_list|(
name|solrHome
argument_list|,
name|CONTEXT
argument_list|,
name|solrHttpPort
argument_list|,
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|jettySolrRunner
operator|.
name|start
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|HttpSolrServer
name|httpSolrServer
init|=
operator|new
name|HttpSolrServer
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|LOCAL_BASE_URL
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
operator|.
name|append
argument_list|(
name|solrHttpPort
argument_list|)
operator|.
name|append
argument_list|(
name|CONTEXT
argument_list|)
operator|.
name|toString
argument_list|()
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
literal|"the spawn HTTP Solr server is not alive"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|SolrServer
name|initializeWithEmbeddedSolrServer
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|SolrServerException
block|{
comment|// fallback to creating an in memory bundled Solr instance
name|System
operator|.
name|setProperty
argument_list|(
name|SOLR_HOME_PROPERTY_NAME
argument_list|,
name|solrHome
argument_list|)
expr_stmt|;
name|enableSolrCloud
argument_list|(
name|solrHome
argument_list|,
name|DEFAULT_CORE_NAME
argument_list|)
expr_stmt|;
name|CoreContainer
operator|.
name|Initializer
name|initializer
init|=
operator|new
name|CoreContainer
operator|.
name|Initializer
argument_list|()
decl_stmt|;
name|EmbeddedSolrServer
name|embeddedSolrServer
init|=
operator|new
name|EmbeddedSolrServer
argument_list|(
name|initializer
operator|.
name|initialize
argument_list|()
argument_list|,
name|DEFAULT_CORE_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|OakSolrUtils
operator|.
name|checkServerAlive
argument_list|(
name|embeddedSolrServer
argument_list|)
condition|)
block|{
return|return
name|embeddedSolrServer
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"the found embedded Solr server is not alive"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|enableSolrCloud
parameter_list|(
name|String
name|solrHome
parameter_list|,
name|String
name|coreName
parameter_list|)
block|{
comment|// TODO : expose such properties via OSGi conf
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"solrcloud"
argument_list|)
operator|!=
literal|null
operator|&&
name|System
operator|.
name|getProperty
argument_list|(
literal|"solrcloud"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
condition|)
block|{
comment|// enable embedded SolrCloud by setting needed params
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkRun"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|//        System.setProperty("bootstrap_conf", "true");
name|System
operator|.
name|setProperty
argument_list|(
literal|"bootstrap_confdir"
argument_list|,
name|solrHome
operator|+
literal|"/"
operator|+
name|coreName
operator|+
literal|"/conf"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"collection.configName"
argument_list|,
literal|"oakconf"
argument_list|)
expr_stmt|;
comment|//        System.setProperty("numShards", "1");
block|}
block|}
block|}
end_class

end_unit

