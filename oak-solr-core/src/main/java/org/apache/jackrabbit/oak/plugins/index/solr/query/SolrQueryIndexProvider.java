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
name|ImmutableList
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
name|NodeState
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
name|QueryIndex
operator|.
name|NodeAggregator
name|aggregator
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
name|QueryIndex
operator|.
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
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|SolrQueryIndex
argument_list|(
name|aggregator
argument_list|,
name|oakSolrConfigurationProvider
argument_list|,
name|solrServerProvider
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

