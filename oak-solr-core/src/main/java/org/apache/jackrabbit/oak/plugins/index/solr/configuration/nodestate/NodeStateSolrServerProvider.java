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
name|configuration
operator|.
name|nodestate
package|;
end_package

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|spi
operator|.
name|state
operator|.
name|NodeState
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

begin_comment
comment|/**  * {@link org.apache.jackrabbit.oak.plugins.index.solr.server.SolrServerProvider} using configuration stored in a repository  * node named "server" as a child of a {@code oak:queryIndexDefinition} node (e.g. under /../a/b/solrIndex/server)  * having {@code type = solr}  */
end_comment

begin_class
specifier|public
class|class
name|NodeStateSolrServerProvider
implements|implements
name|SolrServerProvider
block|{
specifier|private
specifier|final
name|NodeState
name|nodeState
decl_stmt|;
specifier|private
name|SolrServerProvider
name|provider
decl_stmt|;
specifier|public
name|NodeStateSolrServerProvider
parameter_list|(
name|NodeState
name|nodeState
parameter_list|)
block|{
name|this
operator|.
name|nodeState
operator|=
name|nodeState
expr_stmt|;
block|}
specifier|private
name|void
name|checkProviderInitialization
parameter_list|()
throws|throws
name|IllegalAccessException
throws|,
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
throws|,
name|InstantiationException
block|{
synchronized|synchronized
init|(
name|nodeState
init|)
block|{
if|if
condition|(
name|provider
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|provider
operator|=
operator|new
name|NodeStateSolrServerConfigurationProvider
argument_list|(
name|nodeState
argument_list|)
operator|.
name|getSolrServerConfiguration
argument_list|()
operator|.
name|getProvider
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|SolrClient
name|getSolrServer
parameter_list|()
throws|throws
name|Exception
block|{
name|checkProviderInitialization
argument_list|()
expr_stmt|;
return|return
name|provider
operator|.
name|getSolrServer
argument_list|()
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
name|checkProviderInitialization
argument_list|()
expr_stmt|;
return|return
name|provider
operator|.
name|getIndexingSolrServer
argument_list|()
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
name|checkProviderInitialization
argument_list|()
expr_stmt|;
return|return
name|provider
operator|.
name|getSearchingSolrServer
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|getSolrServer
argument_list|()
operator|.
name|close
argument_list|()
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
try|try
block|{
name|getIndexingSolrServer
argument_list|()
operator|.
name|close
argument_list|()
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
try|try
block|{
name|getSearchingSolrServer
argument_list|()
operator|.
name|close
argument_list|()
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
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"NodeStateSolrServerProvider{"
operator|+
literal|"nodeStateChildren="
operator|+
name|Iterables
operator|.
name|toString
argument_list|(
name|nodeState
operator|.
name|getChildNodeNames
argument_list|()
argument_list|)
operator|+
literal|", provider="
operator|+
name|provider
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

