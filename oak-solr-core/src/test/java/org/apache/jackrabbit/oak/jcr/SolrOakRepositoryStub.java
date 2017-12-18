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
name|jcr
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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|aggregate
operator|.
name|AggregateIndexProvider
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
name|DefaultSolrConfigurationProvider
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
name|SolrServer
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

begin_class
specifier|public
class|class
name|SolrOakRepositoryStub
extends|extends
name|OakSegmentTarRepositoryStub
block|{
specifier|public
name|SolrOakRepositoryStub
parameter_list|(
name|Properties
name|settings
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|preCreateRepository
parameter_list|(
name|Jcr
name|jcr
parameter_list|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
literal|"target"
operator|+
name|File
operator|.
name|pathSeparator
operator|+
literal|"queryjcrtest-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|SolrClient
name|solrServer
decl_stmt|;
try|try
block|{
name|solrServer
operator|=
operator|new
name|EmbeddedSolrServerProvider
argument_list|(
operator|new
name|EmbeddedSolrServerConfiguration
argument_list|(
name|f
operator|.
name|getPath
argument_list|()
argument_list|,
literal|"oak"
argument_list|)
argument_list|)
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|SolrServerProvider
name|solrServerProvider
init|=
operator|new
name|SolrServerProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{              }
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|SolrClient
name|getSolrServer
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|solrServer
return|;
block|}
annotation|@
name|Override
specifier|public
name|SolrClient
name|getIndexingSolrServer
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|solrServer
return|;
block|}
annotation|@
name|Override
specifier|public
name|SolrClient
name|getSearchingSolrServer
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|solrServer
return|;
block|}
block|}
decl_stmt|;
try|try
block|{
name|assertNotNull
argument_list|(
name|solrServer
argument_list|)
expr_stmt|;
comment|// safely remove any previous document on the index
name|solrServer
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|solrServer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|OakSolrConfiguration
name|configuration
init|=
operator|new
name|DefaultSolrConfiguration
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|CommitPolicy
name|getCommitPolicy
parameter_list|()
block|{
return|return
name|CommitPolicy
operator|.
name|HARD
return|;
block|}
block|}
decl_stmt|;
name|OakSolrConfigurationProvider
name|oakSolrConfigurationProvider
init|=
operator|new
name|DefaultSolrConfigurationProvider
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
name|jcr
operator|.
name|with
argument_list|(
operator|new
name|SolrIndexInitializer
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|with
argument_list|(
name|AggregateIndexProvider
operator|.
name|wrap
argument_list|(
operator|new
name|SolrQueryIndexProvider
argument_list|(
name|solrServerProvider
argument_list|,
name|oakSolrConfigurationProvider
argument_list|)
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
name|solrServerProvider
argument_list|,
name|oakSolrConfigurationProvider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

