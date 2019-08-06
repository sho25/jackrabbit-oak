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
name|elasticsearch
operator|.
name|query
package|;
end_package

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
name|elasticsearch
operator|.
name|ElasticsearchIndexCoordinateFactory
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
name|search
operator|.
name|IndexNode
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
name|search
operator|.
name|SizeEstimator
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
name|search
operator|.
name|spi
operator|.
name|query
operator|.
name|FulltextIndex
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
name|search
operator|.
name|spi
operator|.
name|query
operator|.
name|FulltextIndexPlanner
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
name|Cursor
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
name|Filter
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
name|QueryLimits
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
name|elasticsearch
operator|.
name|common
operator|.
name|Strings
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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
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
name|elasticsearch
operator|.
name|ElasticsearchIndexConstants
operator|.
name|TYPE_ELASTICSEARCH
import|;
end_import

begin_class
specifier|public
class|class
name|ElasticsearchIndex
extends|extends
name|FulltextIndex
block|{
specifier|private
specifier|static
specifier|final
name|Predicate
argument_list|<
name|NodeState
argument_list|>
name|ELASTICSEARH_INDEX_DEFINITION_PREDICATE
init|=
name|state
lambda|->
name|TYPE_ELASTICSEARCH
operator|.
name|equals
argument_list|(
name|state
operator|.
name|getString
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|)
argument_list|)
decl_stmt|;
comment|// higher than some threshold below which the query should rather be answered by something else if possible
specifier|private
specifier|static
name|double
name|MIN_COST
init|=
literal|100.1
decl_stmt|;
specifier|private
specifier|final
name|ElasticsearchIndexCoordinateFactory
name|esIndexCoordFactory
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|root
decl_stmt|;
name|ElasticsearchIndex
parameter_list|(
annotation|@
name|NotNull
name|ElasticsearchIndexCoordinateFactory
name|esIndexCoordFactory
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|root
parameter_list|)
block|{
name|this
operator|.
name|esIndexCoordFactory
operator|=
name|esIndexCoordFactory
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getType
parameter_list|()
block|{
return|return
name|TYPE_ELASTICSEARCH
return|;
block|}
annotation|@
name|Override
specifier|protected
name|SizeEstimator
name|getSizeEstimator
parameter_list|(
name|IndexPlan
name|plan
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
block|{
comment|// TODO: implement nicely - possible use solr impl's LMSEstimator
return|return
literal|2L
operator|*
operator|(
name|long
operator|)
name|MIN_COST
return|;
block|}
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Predicate
argument_list|<
name|NodeState
argument_list|>
name|getIndexDefinitionPredicate
parameter_list|()
block|{
return|return
name|ELASTICSEARH_INDEX_DEFINITION_PREDICATE
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getMinimumCost
parameter_list|()
block|{
return|return
name|MIN_COST
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexName
parameter_list|()
block|{
return|return
literal|"elasticsearch"
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ElasticsearchIndexNode
name|acquireIndexNode
parameter_list|(
name|IndexPlan
name|plan
parameter_list|)
block|{
return|return
operator|(
name|ElasticsearchIndexNode
operator|)
name|super
operator|.
name|acquireIndexNode
argument_list|(
name|plan
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|IndexNode
name|acquireIndexNode
parameter_list|(
name|String
name|indexPath
parameter_list|)
block|{
return|return
name|ElasticsearchIndexNode
operator|.
name|fromIndexPath
argument_list|(
name|root
argument_list|,
name|indexPath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getFulltextRequestString
parameter_list|(
name|IndexPlan
name|plan
parameter_list|,
name|IndexNode
name|indexNode
parameter_list|)
block|{
return|return
name|Strings
operator|.
name|toString
argument_list|(
name|ElasticsearchResultRowIterator
operator|.
name|getESRequest
argument_list|(
name|plan
argument_list|,
name|getPlanResult
argument_list|(
name|plan
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Cursor
name|query
parameter_list|(
name|IndexPlan
name|plan
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
block|{
specifier|final
name|Filter
name|filter
init|=
name|plan
operator|.
name|getFilter
argument_list|()
decl_stmt|;
comment|// TODO: sorting
specifier|final
name|FulltextIndexPlanner
operator|.
name|PlanResult
name|pr
init|=
name|getPlanResult
argument_list|(
name|plan
argument_list|)
decl_stmt|;
name|QueryLimits
name|settings
init|=
name|filter
operator|.
name|getQueryLimits
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|FulltextResultRow
argument_list|>
name|itr
init|=
operator|new
name|ElasticsearchResultRowIterator
argument_list|(
name|esIndexCoordFactory
argument_list|,
name|filter
argument_list|,
name|pr
argument_list|,
name|plan
argument_list|,
name|acquireIndexNode
argument_list|(
name|plan
argument_list|)
argument_list|,
name|FulltextIndex
operator|::
name|shouldInclude
argument_list|)
decl_stmt|;
name|SizeEstimator
name|sizeEstimator
init|=
name|getSizeEstimator
argument_list|(
name|plan
argument_list|)
decl_stmt|;
comment|/*         TODO: sync (nrt too??)         if (pr.hasPropertyIndexResult() || pr.evaluateSyncNodeTypeRestriction()) {             itr = mergePropertyIndexResult(plan, rootState, itr);         }         */
comment|// no concept of rewound in ES (even if it might be doing it internally, we can't do much about it
name|IteratorRewoundStateProvider
name|rewoundStateProvider
init|=
parameter_list|()
lambda|->
literal|0
decl_stmt|;
return|return
operator|new
name|FulltextPathCursor
argument_list|(
name|itr
argument_list|,
name|rewoundStateProvider
argument_list|,
name|plan
argument_list|,
name|settings
argument_list|,
name|sizeEstimator
argument_list|)
return|;
block|}
block|}
end_class

end_unit
