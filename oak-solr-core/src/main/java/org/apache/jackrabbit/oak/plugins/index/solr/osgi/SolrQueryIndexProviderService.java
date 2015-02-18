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
name|List
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
name|Lists
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
name|ReferencePolicyOption
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
name|commons
operator|.
name|PropertiesUtil
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
name|SolrIndexTracker
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
name|Observer
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
name|osgi
operator|.
name|framework
operator|.
name|ServiceRegistration
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
comment|/**  * Osgi Service that provides Solr based {@link org.apache.jackrabbit.oak.spi.query.QueryIndex}es  *  * @see org.apache.jackrabbit.oak.plugins.index.solr.query.SolrQueryIndexProvider  * @see QueryIndexProvider  */
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
literal|"Oak Solr Query index provider configuration"
argument_list|)
specifier|public
class|class
name|SolrQueryIndexProviderService
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|QUERY_TIME_AGGREGATION_DEFAULT
init|=
literal|false
decl_stmt|;
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
name|List
argument_list|<
name|ServiceRegistration
argument_list|>
name|regs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
annotation|@
name|Reference
specifier|private
name|SolrServerProvider
name|solrServerProvider
decl_stmt|;
annotation|@
name|Reference
specifier|private
name|OakSolrConfigurationProvider
name|oakSolrConfigurationProvider
decl_stmt|;
annotation|@
name|Reference
argument_list|(
name|cardinality
operator|=
name|ReferenceCardinality
operator|.
name|OPTIONAL_UNARY
argument_list|,
name|policyOption
operator|=
name|ReferencePolicyOption
operator|.
name|GREEDY
argument_list|,
name|policy
operator|=
name|ReferencePolicy
operator|.
name|DYNAMIC
argument_list|)
specifier|private
name|NodeAggregator
name|nodeAggregator
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|boolValue
operator|=
name|QUERY_TIME_AGGREGATION_DEFAULT
argument_list|,
name|label
operator|=
literal|"query time aggregation"
argument_list|,
name|description
operator|=
literal|"enable query time aggregation for Solr index"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|QUERY_TIME_AGGREGATION
init|=
literal|"query.aggregation"
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
block|{
name|Object
name|value
init|=
name|componentContext
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|QUERY_TIME_AGGREGATION
argument_list|)
decl_stmt|;
name|boolean
name|queryTimeAggregation
init|=
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|value
argument_list|,
name|QUERY_TIME_AGGREGATION_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
name|solrServerProvider
operator|!=
literal|null
operator|&&
name|oakSolrConfigurationProvider
operator|!=
literal|null
condition|)
block|{
name|QueryIndexProvider
name|solrQueryIndexProvider
init|=
operator|new
name|SolrQueryIndexProvider
argument_list|(
name|solrServerProvider
argument_list|,
name|oakSolrConfigurationProvider
argument_list|,
name|nodeAggregator
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"creating Solr query index provider {} query time aggregation"
argument_list|,
name|queryTimeAggregation
condition|?
literal|"with"
else|:
literal|"without"
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryTimeAggregation
condition|)
block|{
name|solrQueryIndexProvider
operator|=
name|AggregateIndexProvider
operator|.
name|wrap
argument_list|(
name|solrQueryIndexProvider
argument_list|)
expr_stmt|;
block|}
name|regs
operator|.
name|add
argument_list|(
name|componentContext
operator|.
name|getBundleContext
argument_list|()
operator|.
name|registerService
argument_list|(
name|QueryIndexProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|solrQueryIndexProvider
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|regs
operator|.
name|add
argument_list|(
name|componentContext
operator|.
name|getBundleContext
argument_list|()
operator|.
name|registerService
argument_list|(
name|Observer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|solrQueryIndexProvider
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Deactivate
specifier|protected
name|void
name|deactivate
parameter_list|()
block|{
for|for
control|(
name|ServiceRegistration
name|registration
range|:
name|regs
control|)
block|{
name|registration
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

