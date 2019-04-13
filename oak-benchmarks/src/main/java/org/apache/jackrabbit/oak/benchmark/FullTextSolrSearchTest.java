begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|benchmark
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
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|Oak
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
name|fixture
operator|.
name|JcrCreator
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
name|fixture
operator|.
name|OakRepositoryFixture
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
name|fixture
operator|.
name|RepositoryFixture
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
name|jcr
operator|.
name|Jcr
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
name|DefaultSolrConfiguration
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
name|configuration
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
name|nodestate
operator|.
name|NodeStateSolrServersObserver
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
name|index
operator|.
name|SolrIndexEditorProvider
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
name|query
operator|.
name|SolrQueryIndexProvider
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
name|EmbeddedSolrServerProvider
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
name|SolrIndexInitializer
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
name|embedded
operator|.
name|EmbeddedSolrServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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

begin_class
specifier|public
class|class
name|FullTextSolrSearchTest
extends|extends
name|FullTextSearchTest
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
specifier|private
name|SolrServerProvider
name|serverProvider
decl_stmt|;
specifier|private
name|String
name|server
decl_stmt|;
specifier|public
name|FullTextSolrSearchTest
parameter_list|(
name|File
name|dump
parameter_list|,
name|boolean
name|flat
parameter_list|,
name|boolean
name|doReport
parameter_list|,
name|Boolean
name|storageEnabled
parameter_list|,
name|String
name|server
parameter_list|)
block|{
name|super
argument_list|(
name|dump
argument_list|,
name|flat
argument_list|,
name|doReport
argument_list|,
name|storageEnabled
argument_list|)
expr_stmt|;
name|this
operator|.
name|server
operator|=
name|server
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Repository
index|[]
name|createRepository
parameter_list|(
name|RepositoryFixture
name|fixture
parameter_list|)
throws|throws
name|Exception
block|{
name|initializeProvider
argument_list|()
expr_stmt|;
if|if
condition|(
name|fixture
operator|instanceof
name|OakRepositoryFixture
condition|)
block|{
return|return
operator|(
operator|(
name|OakRepositoryFixture
operator|)
name|fixture
operator|)
operator|.
name|setUpCluster
argument_list|(
literal|1
argument_list|,
operator|new
name|JcrCreator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Jcr
name|customize
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
name|OakSolrConfigurationProvider
name|configurationProvider
init|=
operator|new
name|OakSolrConfigurationProvider
argument_list|()
block|{
annotation|@
name|NotNull
specifier|public
name|OakSolrConfiguration
name|getConfiguration
parameter_list|()
block|{
return|return
operator|new
name|DefaultSolrConfiguration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getRows
parameter_list|()
block|{
return|return
literal|50
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|oak
operator|.
name|with
argument_list|(
operator|new
name|SolrQueryIndexProvider
argument_list|(
name|serverProvider
argument_list|,
name|configurationProvider
argument_list|)
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|NodeStateSolrServersObserver
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|SolrIndexEditorProvider
argument_list|(
name|serverProvider
argument_list|,
name|configurationProvider
argument_list|)
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|SolrIndexInitializer
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|createRepository
argument_list|(
name|fixture
argument_list|)
return|;
block|}
specifier|private
name|void
name|initializeProvider
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|server
operator|==
literal|null
operator|||
literal|"default"
operator|.
name|equals
argument_list|(
name|server
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"spawning Solr locally"
argument_list|)
expr_stmt|;
name|serverProvider
operator|=
name|createEmbeddedSolrServerProvider
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|server
operator|!=
literal|null
operator|&&
literal|"embedded"
operator|.
name|equals
argument_list|(
name|server
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"using embedded Solr"
argument_list|)
expr_stmt|;
name|serverProvider
operator|=
name|createEmbeddedSolrServerProvider
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|server
operator|!=
literal|null
operator|&&
operator|(
name|server
operator|.
name|startsWith
argument_list|(
literal|"http"
argument_list|)
operator|||
name|server
operator|.
name|matches
argument_list|(
literal|"\\w+\\:\\d{3,5}"
argument_list|)
operator|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"using remote Solr {}"
argument_list|,
name|server
argument_list|)
expr_stmt|;
name|RemoteSolrServerConfiguration
name|remoteSolrServerConfiguration
init|=
operator|new
name|RemoteSolrServerConfiguration
argument_list|(
name|server
argument_list|,
literal|"oak"
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
name|server
argument_list|)
decl_stmt|;
name|serverProvider
operator|=
name|remoteSolrServerConfiguration
operator|.
name|getProvider
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"server parameter value must be either 'embedded', 'default', an URL or an host:port String"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|EmbeddedSolrServerProvider
name|createEmbeddedSolrServerProvider
parameter_list|(
name|boolean
name|http
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|tempDirectoryPath
init|=
name|FileUtils
operator|.
name|getTempDirectoryPath
argument_list|()
decl_stmt|;
name|File
name|solrHome
init|=
operator|new
name|File
argument_list|(
name|tempDirectoryPath
argument_list|,
literal|"solr"
operator|+
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
decl_stmt|;
name|EmbeddedSolrServerConfiguration
name|embeddedSolrServerConfiguration
init|=
operator|new
name|EmbeddedSolrServerConfiguration
argument_list|(
name|solrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"oak"
argument_list|)
decl_stmt|;
if|if
condition|(
name|http
condition|)
block|{
name|embeddedSolrServerConfiguration
operator|=
name|embeddedSolrServerConfiguration
operator|.
name|withHttpConfiguration
argument_list|(
literal|"/solr"
argument_list|,
literal|8983
argument_list|)
expr_stmt|;
block|}
name|EmbeddedSolrServerProvider
name|embeddedSolrServerProvider
init|=
name|embeddedSolrServerConfiguration
operator|.
name|getProvider
argument_list|()
decl_stmt|;
name|SolrClient
name|solrServer
init|=
name|embeddedSolrServerProvider
operator|.
name|getSolrServer
argument_list|()
decl_stmt|;
if|if
condition|(
name|storageEnabled
operator|!=
literal|null
operator|&&
operator|!
name|storageEnabled
condition|)
block|{
comment|// change schema.xml and reload the core
name|File
name|schemaXML
init|=
operator|new
name|File
argument_list|(
name|solrHome
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"/oak/conf"
argument_list|,
literal|"schema.xml"
argument_list|)
decl_stmt|;
name|InputStream
name|inputStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/solr/oak/conf/schema.xml"
argument_list|)
decl_stmt|;
name|String
name|schemaString
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|inputStream
argument_list|)
operator|.
name|replace
argument_list|(
literal|"<dynamicField name=\"*\" type=\"text_general\" indexed=\"true\" stored=\"true\" multiValued=\"true\"/>"
argument_list|,
literal|"<dynamicField name=\"*\" type=\"text_general\" indexed=\"true\" stored=\"false\" multiValued=\"true\"/>"
argument_list|)
decl_stmt|;
name|FileOutputStream
name|fileOutputStream
init|=
operator|new
name|FileOutputStream
argument_list|(
name|schemaXML
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
operator|new
name|StringReader
argument_list|(
name|schemaString
argument_list|)
argument_list|,
name|fileOutputStream
argument_list|)
expr_stmt|;
name|fileOutputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
operator|(
operator|(
name|EmbeddedSolrServer
operator|)
name|solrServer
operator|)
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|reload
argument_list|(
literal|"oak"
argument_list|)
expr_stmt|;
block|}
return|return
name|embeddedSolrServerProvider
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|afterSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrClient
name|solrServer
init|=
name|serverProvider
operator|.
name|getSolrServer
argument_list|()
decl_stmt|;
if|if
condition|(
name|solrServer
operator|!=
literal|null
condition|)
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

