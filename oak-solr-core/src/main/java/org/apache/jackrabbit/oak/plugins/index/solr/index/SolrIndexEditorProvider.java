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
name|index
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
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|CommitFailedException
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
name|IndexEditorProvider
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
name|IndexUpdateCallback
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
name|query
operator|.
name|SolrQueryIndex
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
name|commit
operator|.
name|Editor
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
name|NodeBuilder
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
name|SolrServer
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
comment|/**  * Solr based {@link IndexEditorProvider}  *  * @see SolrIndexEditor  */
end_comment

begin_class
specifier|public
class|class
name|SolrIndexEditorProvider
implements|implements
name|IndexEditorProvider
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
specifier|final
name|SolrServerProvider
name|solrServerProvider
decl_stmt|;
specifier|private
specifier|final
name|OakSolrConfigurationProvider
name|oakSolrConfigurationProvider
decl_stmt|;
specifier|public
name|SolrIndexEditorProvider
parameter_list|(
name|SolrServerProvider
name|solrServerProvider
parameter_list|,
name|OakSolrConfigurationProvider
name|oakSolrConfigurationProvider
parameter_list|)
block|{
name|this
operator|.
name|solrServerProvider
operator|=
name|solrServerProvider
expr_stmt|;
name|this
operator|.
name|oakSolrConfigurationProvider
operator|=
name|oakSolrConfigurationProvider
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|getIndexEditor
parameter_list|(
annotation|@
name|Nonnull
name|String
name|type
parameter_list|,
annotation|@
name|Nonnull
name|NodeBuilder
name|definition
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|,
annotation|@
name|Nonnull
name|IndexUpdateCallback
name|callback
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|SolrQueryIndex
operator|.
name|TYPE
operator|.
name|equals
argument_list|(
name|type
argument_list|)
operator|&&
name|isConfigurationOk
argument_list|()
condition|)
block|{
try|try
block|{
name|SolrServer
name|solrServer
init|=
name|solrServerProvider
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
return|return
operator|new
name|SolrIndexEditor
argument_list|(
name|definition
argument_list|,
name|solrServer
argument_list|,
name|oakSolrConfigurationProvider
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|callback
argument_list|)
return|;
block|}
else|else
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
literal|"null SolrServer provided, cannot index {}"
argument_list|,
name|definition
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|log
operator|.
name|isErrorEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"unable to create SolrIndexEditor"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|boolean
name|isConfigurationOk
parameter_list|()
block|{
return|return
name|solrServerProvider
operator|!=
literal|null
operator|&&
name|oakSolrConfigurationProvider
operator|!=
literal|null
return|;
block|}
block|}
end_class

end_unit

