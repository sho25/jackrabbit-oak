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
name|query
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|java
operator|.
name|util
operator|.
name|WeakHashMap
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
name|annotation
operator|.
name|Nullable
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
name|PropertyState
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
name|Type
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
name|NodeAggregator
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
name|configuration
operator|.
name|nodestate
operator|.
name|NodeStateSolrServerConfigurationProvider
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
name|OakSolrNodeStateConfiguration
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
name|OakSolrServer
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
name|query
operator|.
name|QueryIndex
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
name|query
operator|.
name|QueryIndexProvider
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
name|ChildNodeEntry
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

begin_import
import|import static
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
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
import|;
end_import

begin_import
import|import static
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
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
import|;
end_import

begin_comment
comment|/**  * {@link QueryIndexProvider} for {@link SolrQueryIndex}  */
end_comment

begin_class
specifier|public
class|class
name|SolrQueryIndexProvider
implements|implements
name|QueryIndexProvider
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
specifier|private
specifier|final
name|NodeAggregator
name|aggregator
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|NodeState
argument_list|,
name|LMSEstimator
argument_list|>
name|estimators
init|=
operator|new
name|WeakHashMap
argument_list|<
name|NodeState
argument_list|,
name|LMSEstimator
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|SolrQueryIndexProvider
parameter_list|(
annotation|@
name|Nonnull
name|SolrServerProvider
name|solrServerProvider
parameter_list|,
annotation|@
name|Nonnull
name|OakSolrConfigurationProvider
name|oakSolrConfigurationProvider
parameter_list|,
annotation|@
name|Nullable
name|NodeAggregator
name|nodeAggregator
parameter_list|)
block|{
name|this
operator|.
name|oakSolrConfigurationProvider
operator|=
name|oakSolrConfigurationProvider
expr_stmt|;
name|this
operator|.
name|solrServerProvider
operator|=
name|solrServerProvider
expr_stmt|;
name|this
operator|.
name|aggregator
operator|=
name|nodeAggregator
expr_stmt|;
block|}
specifier|public
name|SolrQueryIndexProvider
parameter_list|(
annotation|@
name|Nonnull
name|SolrServerProvider
name|solrServerProvider
parameter_list|,
annotation|@
name|Nonnull
name|OakSolrConfigurationProvider
name|oakSolrConfigurationProvider
parameter_list|)
block|{
name|this
argument_list|(
name|solrServerProvider
argument_list|,
name|oakSolrConfigurationProvider
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|QueryIndex
argument_list|>
name|getQueryIndexes
parameter_list|(
name|NodeState
name|nodeState
parameter_list|)
block|{
name|List
argument_list|<
name|QueryIndex
argument_list|>
name|tempIndexes
init|=
operator|new
name|ArrayList
argument_list|<
name|QueryIndex
argument_list|>
argument_list|()
decl_stmt|;
name|NodeState
name|definitions
init|=
name|nodeState
operator|.
name|getChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|definitions
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|NodeState
name|definition
init|=
name|entry
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
name|PropertyState
name|type
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
operator|&&
name|SolrQueryIndex
operator|.
name|TYPE
operator|.
name|equals
argument_list|(
name|type
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
condition|)
block|{
try|try
block|{
if|if
condition|(
name|isPersistedConfiguration
argument_list|(
name|definition
argument_list|)
condition|)
block|{
name|OakSolrConfiguration
name|configuration
init|=
operator|new
name|OakSolrNodeStateConfiguration
argument_list|(
name|definition
argument_list|)
decl_stmt|;
name|SolrServerConfigurationProvider
name|solrServerConfigurationProvider
init|=
operator|new
name|NodeStateSolrServerConfigurationProvider
argument_list|(
name|definition
operator|.
name|getChildNode
argument_list|(
literal|"server"
argument_list|)
argument_list|)
decl_stmt|;
name|SolrServer
name|solrServer
init|=
operator|new
name|OakSolrServer
argument_list|(
name|solrServerConfigurationProvider
argument_list|)
decl_stmt|;
comment|// if it does not already exist I need to register an observer that updates / closes this SolrServerProvider when the node is updated/removed
name|addQueryIndex
argument_list|(
name|tempIndexes
argument_list|,
name|name
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|,
name|definition
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// otherwise use the default configuration providers
name|OakSolrConfiguration
name|configuration
init|=
name|oakSolrConfigurationProvider
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|addQueryIndex
argument_list|(
name|tempIndexes
argument_list|,
name|name
argument_list|,
name|solrServerProvider
operator|.
name|getSearchingSolrServer
argument_list|()
argument_list|,
name|configuration
argument_list|,
name|definition
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
literal|"could not get Solr query index from node {}"
argument_list|,
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|tempIndexes
return|;
block|}
specifier|private
name|boolean
name|isPersistedConfiguration
parameter_list|(
name|NodeState
name|definition
parameter_list|)
block|{
return|return
name|definition
operator|.
name|hasChildNode
argument_list|(
literal|"server"
argument_list|)
return|;
block|}
specifier|private
name|void
name|addQueryIndex
parameter_list|(
name|List
argument_list|<
name|QueryIndex
argument_list|>
name|tempIndexes
parameter_list|,
name|String
name|name
parameter_list|,
name|SolrServer
name|solrServer
parameter_list|,
name|OakSolrConfiguration
name|configuration
parameter_list|,
name|NodeState
name|definition
parameter_list|)
block|{
try|try
block|{
comment|// the query engine should be returned only if the server is alive, otherwise other indexes should be used
if|if
condition|(
name|solrServer
operator|!=
literal|null
operator|&&
literal|0
operator|==
name|solrServer
operator|.
name|ping
argument_list|()
operator|.
name|getStatus
argument_list|()
condition|)
block|{
name|LMSEstimator
name|estimator
decl_stmt|;
synchronized|synchronized
init|(
name|estimators
init|)
block|{
name|estimator
operator|=
name|estimators
operator|.
name|get
argument_list|(
name|definition
argument_list|)
expr_stmt|;
if|if
condition|(
name|estimator
operator|==
literal|null
condition|)
block|{
name|estimator
operator|=
operator|new
name|LMSEstimator
argument_list|()
expr_stmt|;
name|estimators
operator|.
name|put
argument_list|(
name|definition
argument_list|,
name|estimator
argument_list|)
expr_stmt|;
block|}
block|}
name|tempIndexes
operator|.
name|add
argument_list|(
operator|new
name|SolrQueryIndex
argument_list|(
name|name
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|,
name|aggregator
argument_list|,
name|estimator
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"cannot create Solr query index as SolrServer {} is unreachable"
argument_list|,
name|solrServer
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
literal|"unable to create Solr query index at "
operator|+
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

