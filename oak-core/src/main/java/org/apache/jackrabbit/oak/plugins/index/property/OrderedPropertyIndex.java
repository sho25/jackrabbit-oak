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
name|property
package|;
end_package

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
name|property
operator|.
name|OrderedIndex
operator|.
name|TYPE
import|;
end_import

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
name|Collection
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
name|api
operator|.
name|PropertyValue
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
name|commons
operator|.
name|PathUtils
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
name|Cursors
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
name|Filter
operator|.
name|PropertyRestriction
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
operator|.
name|AdvancedQueryIndex
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

begin_comment
comment|/**  * A property index that supports ordering keys.  */
end_comment

begin_class
specifier|public
class|class
name|OrderedPropertyIndex
extends|extends
name|PropertyIndex
implements|implements
name|AdvancedQueryIndex
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OrderedPropertyIndex
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|getIndexName
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
annotation|@
name|Override
name|OrderedPropertyIndexLookup
name|getLookup
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
return|return
operator|new
name|OrderedPropertyIndexLookup
argument_list|(
name|root
argument_list|)
return|;
block|}
comment|/**      * retrieve the cost for the query.      *       * !!! for now we want to skip the use-case of NON range-queries !!!      */
annotation|@
name|Override
specifier|public
name|double
name|getCost
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|root
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported as implementing AdvancedQueryIndex"
argument_list|)
throw|;
block|}
comment|/**      * @return an builder with some initial common settings      */
specifier|private
specifier|static
name|IndexPlan
operator|.
name|Builder
name|getIndexPlanBuilder
parameter_list|(
specifier|final
name|Filter
name|filter
parameter_list|)
block|{
name|IndexPlan
operator|.
name|Builder
name|b
init|=
operator|new
name|IndexPlan
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|b
operator|.
name|setCostPerExecution
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// we're local. Low-cost
comment|// we're local but slightly more expensive than a standard PropertyIndex
name|b
operator|.
name|setCostPerEntry
argument_list|(
literal|1.3
argument_list|)
expr_stmt|;
name|b
operator|.
name|setFulltextIndex
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// we're never full-text
name|b
operator|.
name|setIncludesNodeData
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// we should not include node data
name|b
operator|.
name|setFilter
argument_list|(
name|filter
argument_list|)
expr_stmt|;
comment|// TODO it's synch for now but we should investigate the indexMeta
name|b
operator|.
name|setDelayed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|b
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
name|root
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"getPlans(Filter, List<OrderEntry>, NodeState)"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"getPlans() - filter: {} - "
argument_list|,
name|filter
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"getPlans() - sortOrder: {} - "
argument_list|,
name|sortOrder
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"getPlans() - rootState: {} - "
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|IndexPlan
argument_list|>
name|plans
init|=
operator|new
name|ArrayList
argument_list|<
name|IndexPlan
argument_list|>
argument_list|()
decl_stmt|;
name|PropertyIndexLookup
name|pil
init|=
name|getLookup
argument_list|(
name|root
argument_list|)
decl_stmt|;
if|if
condition|(
name|pil
operator|instanceof
name|OrderedPropertyIndexLookup
condition|)
block|{
name|OrderedPropertyIndexLookup
name|lookup
init|=
operator|(
name|OrderedPropertyIndexLookup
operator|)
name|pil
decl_stmt|;
name|Collection
argument_list|<
name|PropertyRestriction
argument_list|>
name|restrictions
init|=
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
decl_stmt|;
comment|// first we process the sole orders as we could be in a situation where we don't have
comment|// a where condition indexed but we do for order. In that case we will return always the
comment|// whole index
if|if
condition|(
name|sortOrder
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|OrderEntry
name|oe
range|:
name|sortOrder
control|)
block|{
name|String
name|propertyName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|oe
operator|.
name|getPropertyName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|lookup
operator|.
name|isIndexed
argument_list|(
name|propertyName
argument_list|,
literal|"/"
argument_list|,
name|filter
argument_list|)
condition|)
block|{
name|IndexPlan
operator|.
name|Builder
name|b
init|=
name|getIndexPlanBuilder
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|b
operator|.
name|setSortOrder
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|OrderEntry
argument_list|(
name|oe
operator|.
name|getPropertyName
argument_list|()
argument_list|,
name|Type
operator|.
name|UNDEFINED
argument_list|,
name|lookup
operator|.
name|isAscending
argument_list|(
name|root
argument_list|,
name|propertyName
argument_list|,
name|filter
argument_list|)
condition|?
name|OrderEntry
operator|.
name|Order
operator|.
name|ASCENDING
else|:
name|OrderEntry
operator|.
name|Order
operator|.
name|DESCENDING
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|setEstimatedEntryCount
argument_list|(
name|lookup
operator|.
name|getEstimatedEntryCount
argument_list|(
name|propertyName
argument_list|,
literal|null
argument_list|,
name|filter
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|IndexPlan
name|plan
init|=
name|b
operator|.
name|build
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"plan: {}"
argument_list|,
name|plan
argument_list|)
expr_stmt|;
name|plans
operator|.
name|add
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// then we add plans for each restriction that could apply to us
for|for
control|(
name|Filter
operator|.
name|PropertyRestriction
name|pr
range|:
name|restrictions
control|)
block|{
name|String
name|propertyName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|lookup
operator|.
name|isIndexed
argument_list|(
name|propertyName
argument_list|,
literal|"/"
argument_list|,
name|filter
argument_list|)
condition|)
block|{
name|PropertyValue
name|value
init|=
literal|null
decl_stmt|;
name|boolean
name|createPlan
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|pr
operator|.
name|first
operator|==
literal|null
operator|&&
name|pr
operator|.
name|last
operator|==
literal|null
condition|)
block|{
comment|// open query: [property] is not null
name|value
operator|=
literal|null
expr_stmt|;
name|createPlan
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pr
operator|.
name|first
operator|!=
literal|null
operator|&&
name|pr
operator|.
name|first
operator|.
name|equals
argument_list|(
name|pr
operator|.
name|last
argument_list|)
operator|&&
name|pr
operator|.
name|firstIncluding
operator|&&
name|pr
operator|.
name|lastIncluding
condition|)
block|{
comment|// [property]=[value]
name|value
operator|=
name|pr
operator|.
name|first
expr_stmt|;
name|createPlan
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pr
operator|.
name|first
operator|!=
literal|null
operator|&&
operator|!
name|pr
operator|.
name|first
operator|.
name|equals
argument_list|(
name|pr
operator|.
name|last
argument_list|)
condition|)
block|{
comment|// '>'& '>=' use cases
if|if
condition|(
name|lookup
operator|.
name|isAscending
argument_list|(
name|root
argument_list|,
name|propertyName
argument_list|,
name|filter
argument_list|)
condition|)
block|{
name|value
operator|=
name|pr
operator|.
name|first
expr_stmt|;
name|createPlan
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|createPlan
operator|=
literal|false
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|pr
operator|.
name|last
operator|!=
literal|null
operator|&&
operator|!
name|pr
operator|.
name|last
operator|.
name|equals
argument_list|(
name|pr
operator|.
name|first
argument_list|)
condition|)
block|{
comment|// '<'& '<='
if|if
condition|(
operator|!
name|lookup
operator|.
name|isAscending
argument_list|(
name|root
argument_list|,
name|propertyName
argument_list|,
name|filter
argument_list|)
condition|)
block|{
name|value
operator|=
name|pr
operator|.
name|last
expr_stmt|;
name|createPlan
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|createPlan
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
name|createPlan
condition|)
block|{
comment|// we always return a sorted set
name|IndexPlan
operator|.
name|Builder
name|b
init|=
name|getIndexPlanBuilder
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|b
operator|.
name|setSortOrder
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|OrderEntry
argument_list|(
name|propertyName
argument_list|,
name|Type
operator|.
name|UNDEFINED
argument_list|,
name|lookup
operator|.
name|isAscending
argument_list|(
name|root
argument_list|,
name|propertyName
argument_list|,
name|filter
argument_list|)
condition|?
name|OrderEntry
operator|.
name|Order
operator|.
name|ASCENDING
else|:
name|OrderEntry
operator|.
name|Order
operator|.
name|DESCENDING
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|count
init|=
name|lookup
operator|.
name|getEstimatedEntryCount
argument_list|(
name|propertyName
argument_list|,
name|value
argument_list|,
name|filter
argument_list|,
name|pr
argument_list|)
decl_stmt|;
name|b
operator|.
name|setEstimatedEntryCount
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"estimatedCount: {}"
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|IndexPlan
name|plan
init|=
name|b
operator|.
name|build
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"plan: {}"
argument_list|,
name|plan
argument_list|)
expr_stmt|;
name|plans
operator|.
name|add
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Without an OrderedPropertyIndexLookup you should not end here"
argument_list|)
expr_stmt|;
block|}
return|return
name|plans
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPlanDescription
parameter_list|(
name|IndexPlan
name|plan
parameter_list|,
name|NodeState
name|root
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"getPlanDescription({}, {})"
argument_list|,
name|plan
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"ordered"
argument_list|)
decl_stmt|;
name|OrderedPropertyIndexLookup
name|lookup
init|=
name|getLookup
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|Filter
name|filter
init|=
name|plan
operator|.
name|getFilter
argument_list|()
decl_stmt|;
name|int
name|depth
init|=
literal|1
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|PropertyRestriction
name|pr
range|:
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
control|)
block|{
name|String
name|propertyName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|lookup
operator|.
name|isIndexed
argument_list|(
name|propertyName
argument_list|,
literal|"/"
argument_list|,
name|filter
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|String
name|operation
init|=
literal|null
decl_stmt|;
name|PropertyValue
name|value
init|=
literal|null
decl_stmt|;
comment|// TODO support pr.list
if|if
condition|(
name|pr
operator|.
name|first
operator|==
literal|null
operator|&&
name|pr
operator|.
name|last
operator|==
literal|null
condition|)
block|{
comment|// open query: [property] is not null
name|operation
operator|=
literal|"is not null"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pr
operator|.
name|first
operator|!=
literal|null
operator|&&
name|pr
operator|.
name|first
operator|.
name|equals
argument_list|(
name|pr
operator|.
name|last
argument_list|)
operator|&&
name|pr
operator|.
name|firstIncluding
operator|&&
name|pr
operator|.
name|lastIncluding
condition|)
block|{
comment|// [property]=[value]
name|operation
operator|=
literal|"="
expr_stmt|;
name|value
operator|=
name|pr
operator|.
name|first
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pr
operator|.
name|first
operator|!=
literal|null
operator|&&
operator|!
name|pr
operator|.
name|first
operator|.
name|equals
argument_list|(
name|pr
operator|.
name|last
argument_list|)
condition|)
block|{
comment|// '>'& '>=' use cases
if|if
condition|(
name|lookup
operator|.
name|isAscending
argument_list|(
name|root
argument_list|,
name|propertyName
argument_list|,
name|filter
argument_list|)
condition|)
block|{
name|value
operator|=
name|pr
operator|.
name|first
expr_stmt|;
name|operation
operator|=
name|pr
operator|.
name|firstIncluding
condition|?
literal|">="
else|:
literal|">"
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|pr
operator|.
name|last
operator|!=
literal|null
operator|&&
operator|!
name|pr
operator|.
name|last
operator|.
name|equals
argument_list|(
name|pr
operator|.
name|first
argument_list|)
condition|)
block|{
comment|// '<'& '<='
if|if
condition|(
operator|!
name|lookup
operator|.
name|isAscending
argument_list|(
name|root
argument_list|,
name|propertyName
argument_list|,
name|filter
argument_list|)
condition|)
block|{
name|value
operator|=
name|pr
operator|.
name|last
expr_stmt|;
name|operation
operator|=
name|pr
operator|.
name|lastIncluding
condition|?
literal|"<="
else|:
literal|"<"
expr_stmt|;
block|}
block|}
if|if
condition|(
name|operation
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|propertyName
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|operation
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
continue|continue;
block|}
comment|// stop with the first property that is indexed
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|List
argument_list|<
name|OrderEntry
argument_list|>
name|sortOrder
init|=
name|plan
operator|.
name|getSortOrder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|found
operator|&&
name|sortOrder
operator|!=
literal|null
operator|&&
operator|!
name|sortOrder
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// we could be here if we have a query where the ORDER BY makes us play it.
for|for
control|(
name|OrderEntry
name|oe
range|:
name|sortOrder
control|)
block|{
name|String
name|propertyName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|oe
operator|.
name|getPropertyName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|lookup
operator|.
name|isIndexed
argument_list|(
name|propertyName
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|depth
operator|=
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|oe
operator|.
name|getPropertyName
argument_list|()
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|" order by "
argument_list|)
operator|.
name|append
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
comment|// stop with the first property that is indexed
break|break;
block|}
block|}
if|if
condition|(
name|depth
operator|>
literal|1
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|" ancestor "
argument_list|)
operator|.
name|append
argument_list|(
name|depth
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
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
name|root
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"query(IndexPlan, NodeState)"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"query() - plan: {}"
argument_list|,
name|plan
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"query() - rootState: {}"
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|Filter
name|filter
init|=
name|plan
operator|.
name|getFilter
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|OrderEntry
argument_list|>
name|sortOrder
init|=
name|plan
operator|.
name|getSortOrder
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|paths
init|=
literal|null
decl_stmt|;
name|Cursor
name|cursor
init|=
literal|null
decl_stmt|;
name|PropertyIndexLookup
name|pil
init|=
name|getLookup
argument_list|(
name|root
argument_list|)
decl_stmt|;
if|if
condition|(
name|pil
operator|instanceof
name|OrderedPropertyIndexLookup
condition|)
block|{
name|OrderedPropertyIndexLookup
name|lookup
init|=
operator|(
name|OrderedPropertyIndexLookup
operator|)
name|pil
decl_stmt|;
name|Collection
argument_list|<
name|PropertyRestriction
argument_list|>
name|prs
init|=
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
decl_stmt|;
name|int
name|depth
init|=
literal|1
decl_stmt|;
for|for
control|(
name|PropertyRestriction
name|pr
range|:
name|prs
control|)
block|{
name|String
name|propertyName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|)
decl_stmt|;
name|depth
operator|=
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|)
expr_stmt|;
if|if
condition|(
name|lookup
operator|.
name|isIndexed
argument_list|(
name|propertyName
argument_list|,
literal|"/"
argument_list|,
name|filter
argument_list|)
condition|)
block|{
name|paths
operator|=
name|lookup
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|propertyName
argument_list|,
name|pr
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|paths
operator|==
literal|null
operator|&&
name|sortOrder
operator|!=
literal|null
operator|&&
operator|!
name|sortOrder
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// we could be here if we have a query where the ORDER BY makes us play it.
for|for
control|(
name|OrderEntry
name|oe
range|:
name|sortOrder
control|)
block|{
name|String
name|propertyName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|oe
operator|.
name|getPropertyName
argument_list|()
argument_list|)
decl_stmt|;
name|depth
operator|=
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|oe
operator|.
name|getPropertyName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|lookup
operator|.
name|isIndexed
argument_list|(
name|propertyName
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|)
condition|)
block|{
name|paths
operator|=
name|lookup
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|propertyName
argument_list|,
operator|new
name|PropertyRestriction
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|paths
operator|==
literal|null
condition|)
block|{
comment|// if still here then something went wrong.
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"OrderedPropertyIndex index is used even when no index is available for filter "
operator|+
name|filter
argument_list|)
throw|;
block|}
name|cursor
operator|=
name|Cursors
operator|.
name|newPathCursor
argument_list|(
name|paths
argument_list|,
name|filter
operator|.
name|getQueryEngineSettings
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|depth
operator|>
literal|1
condition|)
block|{
name|cursor
operator|=
name|Cursors
operator|.
name|newAncestorCursor
argument_list|(
name|cursor
argument_list|,
name|depth
operator|-
literal|1
argument_list|,
name|filter
operator|.
name|getQueryEngineSettings
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// if for some reasons it's not an Ordered Lookup we delegate up the chain
name|cursor
operator|=
name|super
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
return|return
name|cursor
return|;
block|}
block|}
end_class

end_unit

