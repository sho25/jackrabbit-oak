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
name|plugins
operator|.
name|index
operator|.
name|lucene
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * A LuceneIndexProvider that return a LuceneIndex with a really low cost, so  * that it tries to guarantee its usage in the queries  *   */
end_comment

begin_class
specifier|public
class|class
name|LowCostLuceneIndexProvider
extends|extends
name|LuceneIndexProvider
block|{
annotation|@
name|Override
specifier|protected
name|LuceneIndex
name|newLuceneIndex
parameter_list|()
block|{
return|return
operator|new
name|LowCostLuceneIndex
argument_list|(
name|tracker
argument_list|,
name|aggregator
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|LowCostLuceneIndex
extends|extends
name|LuceneIndex
block|{
specifier|public
name|LowCostLuceneIndex
parameter_list|(
name|IndexTracker
name|tracker
parameter_list|,
name|NodeAggregator
name|aggregator
parameter_list|)
block|{
name|super
argument_list|(
name|tracker
argument_list|,
name|aggregator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getMinimumCost
parameter_list|()
block|{
return|return
literal|1e-3
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|IndexPlan
argument_list|>
name|getPlans
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|List
argument_list|<
name|OrderEntry
argument_list|>
name|sortOrder
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
block|{
name|String
name|indexPath
init|=
name|LuceneIndexLookupUtil
operator|.
name|getOldFullTextIndexPath
argument_list|(
name|rootState
argument_list|,
name|filter
argument_list|,
name|tracker
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexPath
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|planBuilder
argument_list|(
name|filter
argument_list|)
operator|.
name|setCostPerExecution
argument_list|(
name|getMinimumCost
argument_list|()
argument_list|)
operator|.
name|setAttribute
argument_list|(
name|ATTR_INDEX_PATH
argument_list|,
name|indexPath
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

