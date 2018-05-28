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
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|spi
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
name|Collection
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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Chars
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
name|Result
operator|.
name|SizePrecision
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
name|commons
operator|.
name|PerfLogger
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
name|json
operator|.
name|JsopBuilder
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
name|json
operator|.
name|JsopWriter
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
name|plugins
operator|.
name|index
operator|.
name|Cursors
operator|.
name|PathCursor
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
name|IndexLookup
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
name|PropertyDefinition
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
name|FulltextIndexPlanner
operator|.
name|PlanResult
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
name|memory
operator|.
name|PropertyValues
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
name|query
operator|.
name|facet
operator|.
name|FacetResult
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
name|IndexRow
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
name|QueryConstants
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
name|QueryIndex
operator|.
name|AdvanceFulltextQueryIndex
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
name|query
operator|.
name|fulltext
operator|.
name|FullTextExpression
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
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
import|import static
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
name|NativeQueryIndex
import|;
end_import

begin_comment
comment|/**  * Provides an abstract QueryIndex that does lookups against a fulltext index  *  * @see QueryIndex  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|FulltextIndex
implements|implements
name|AdvancedQueryIndex
implements|,
name|QueryIndex
implements|,
name|NativeQueryIndex
implements|,
name|AdvanceFulltextQueryIndex
block|{
specifier|private
specifier|final
name|Logger
name|LOG
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
name|PerfLogger
name|PERF_LOGGER
init|=
operator|new
name|PerfLogger
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
operator|+
literal|".perf"
argument_list|)
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|String
name|ATTR_PLAN_RESULT
init|=
literal|"oak.fulltext.planResult"
decl_stmt|;
specifier|protected
specifier|abstract
name|FulltextIndexTracker
name|getIndexTracker
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|String
name|getType
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|SizeEstimator
name|getSizeEstimator
parameter_list|(
name|IndexPlan
name|plan
parameter_list|)
function_decl|;
specifier|protected
specifier|abstract
name|String
name|getFulltextRequestString
parameter_list|(
name|IndexPlan
name|plan
parameter_list|,
name|IndexNode
name|indexNode
parameter_list|)
function_decl|;
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
name|Collection
argument_list|<
name|String
argument_list|>
name|indexPaths
init|=
operator|new
name|IndexLookup
argument_list|(
name|rootState
argument_list|)
operator|.
name|collectIndexNodePaths
argument_list|(
name|filter
argument_list|,
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IndexPlan
argument_list|>
name|plans
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|indexPaths
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|indexPaths
control|)
block|{
name|IndexNode
name|indexNode
init|=
literal|null
decl_stmt|;
try|try
block|{
name|indexNode
operator|=
name|getIndexTracker
argument_list|()
operator|.
name|acquireIndexNode
argument_list|(
name|path
argument_list|,
name|getType
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexNode
operator|!=
literal|null
condition|)
block|{
name|IndexPlan
name|plan
init|=
operator|new
name|FulltextIndexPlanner
argument_list|(
name|indexNode
argument_list|,
name|path
argument_list|,
name|filter
argument_list|,
name|sortOrder
argument_list|)
operator|.
name|getPlan
argument_list|()
decl_stmt|;
if|if
condition|(
name|plan
operator|!=
literal|null
condition|)
block|{
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error getting plan for {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|indexNode
operator|!=
literal|null
condition|)
block|{
name|indexNode
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|plans
return|;
block|}
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
annotation|@
name|Override
specifier|public
name|String
name|getPlan
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
name|Filter
name|filter
init|=
name|plan
operator|.
name|getFilter
argument_list|()
decl_stmt|;
name|IndexNode
name|index
init|=
name|getIndexTracker
argument_list|()
operator|.
name|acquireIndexNode
argument_list|(
name|getPlanResult
argument_list|(
name|plan
argument_list|)
operator|.
name|indexPath
argument_list|,
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|index
operator|!=
literal|null
argument_list|,
literal|"The Fulltext of type "
operator|+
name|getType
argument_list|()
operator|+
literal|"  index is not available"
argument_list|)
expr_stmt|;
try|try
block|{
name|FullTextExpression
name|ft
init|=
name|filter
operator|.
name|getFullTextConstraint
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|getPlanResult
argument_list|(
name|plan
argument_list|)
operator|.
name|indexPath
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getIndexName
argument_list|(
name|plan
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|)
operator|.
name|append
argument_list|(
literal|") "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getFulltextRequestString
argument_list|(
name|plan
argument_list|,
name|index
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|plan
operator|.
name|getSortOrder
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|plan
operator|.
name|getSortOrder
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" ordering:"
argument_list|)
operator|.
name|append
argument_list|(
name|plan
operator|.
name|getSortOrder
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ft
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" ft:("
argument_list|)
operator|.
name|append
argument_list|(
name|ft
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
name|addSyncIndexPlan
argument_list|(
name|plan
argument_list|,
name|sb
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
finally|finally
block|{
name|index
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|addSyncIndexPlan
parameter_list|(
name|IndexPlan
name|plan
parameter_list|,
name|StringBuilder
name|sb
parameter_list|)
block|{
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
if|if
condition|(
name|pr
operator|.
name|hasPropertyIndexResult
argument_list|()
condition|)
block|{
name|FulltextIndexPlanner
operator|.
name|PropertyIndexResult
name|pres
init|=
name|pr
operator|.
name|getPropertyIndexResult
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" sync:("
argument_list|)
operator|.
name|append
argument_list|(
name|pres
operator|.
name|propertyName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|pres
operator|.
name|propertyName
operator|.
name|equals
argument_list|(
name|pres
operator|.
name|pr
operator|.
name|propertyName
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
operator|.
name|append
argument_list|(
name|pres
operator|.
name|pr
operator|.
name|propertyName
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|pres
operator|.
name|pr
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pr
operator|.
name|evaluateSyncNodeTypeRestriction
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" sync:(nodeType"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" primaryTypes : "
argument_list|)
operator|.
name|append
argument_list|(
name|plan
operator|.
name|getFilter
argument_list|()
operator|.
name|getPrimaryTypes
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" mixinTypes : "
argument_list|)
operator|.
name|append
argument_list|(
name|plan
operator|.
name|getFilter
argument_list|()
operator|.
name|getMixinTypes
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Cursor
name|query
parameter_list|(
specifier|final
name|Filter
name|filter
parameter_list|,
specifier|final
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
specifier|private
specifier|static
name|boolean
name|shouldInclude
parameter_list|(
name|String
name|docPath
parameter_list|,
name|IndexPlan
name|plan
parameter_list|)
block|{
name|String
name|path
init|=
name|getPathRestriction
argument_list|(
name|plan
argument_list|)
decl_stmt|;
name|boolean
name|include
init|=
literal|true
decl_stmt|;
name|Filter
name|filter
init|=
name|plan
operator|.
name|getFilter
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|filter
operator|.
name|getPathRestriction
argument_list|()
condition|)
block|{
case|case
name|EXACT
case|:
name|include
operator|=
name|path
operator|.
name|equals
argument_list|(
name|docPath
argument_list|)
expr_stmt|;
break|break;
case|case
name|DIRECT_CHILDREN
case|:
name|include
operator|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|docPath
argument_list|)
operator|.
name|equals
argument_list|(
name|path
argument_list|)
expr_stmt|;
break|break;
case|case
name|ALL_CHILDREN
case|:
name|include
operator|=
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|path
argument_list|,
name|docPath
argument_list|)
expr_stmt|;
break|break;
block|}
return|return
name|include
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeAggregator
name|getNodeAggregator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * In a fulltext term for jcr:contains(foo, 'bar') 'foo'      * is the property name. While in jcr:contains(foo/*, 'bar')      * 'foo' is node name      *      * @return true if the term is related to node      */
specifier|public
specifier|static
name|boolean
name|isNodePath
parameter_list|(
name|String
name|fulltextTermPath
parameter_list|)
block|{
return|return
name|fulltextTermPath
operator|.
name|endsWith
argument_list|(
literal|"/*"
argument_list|)
return|;
block|}
specifier|protected
name|IndexNode
name|acquireIndexNode
parameter_list|(
name|IndexPlan
name|plan
parameter_list|)
block|{
return|return
name|getIndexTracker
argument_list|()
operator|.
name|acquireIndexNode
argument_list|(
name|getPlanResult
argument_list|(
name|plan
argument_list|)
operator|.
name|indexPath
argument_list|,
name|getType
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
specifier|static
name|String
name|getIndexName
parameter_list|(
name|IndexPlan
name|plan
parameter_list|)
block|{
return|return
name|PathUtils
operator|.
name|getName
argument_list|(
name|getPlanResult
argument_list|(
name|plan
argument_list|)
operator|.
name|indexPath
argument_list|)
return|;
block|}
specifier|protected
specifier|static
name|int
name|determinePropertyType
parameter_list|(
name|PropertyDefinition
name|defn
parameter_list|,
name|PropertyRestriction
name|pr
parameter_list|)
block|{
name|int
name|typeFromRestriction
init|=
name|pr
operator|.
name|propertyType
decl_stmt|;
if|if
condition|(
name|typeFromRestriction
operator|==
name|PropertyType
operator|.
name|UNDEFINED
condition|)
block|{
comment|//If no explicit type defined then determine the type from restriction
comment|//value
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
name|getType
argument_list|()
operator|!=
name|Type
operator|.
name|UNDEFINED
condition|)
block|{
name|typeFromRestriction
operator|=
name|pr
operator|.
name|first
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
expr_stmt|;
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
name|pr
operator|.
name|last
operator|.
name|getType
argument_list|()
operator|!=
name|Type
operator|.
name|UNDEFINED
condition|)
block|{
name|typeFromRestriction
operator|=
name|pr
operator|.
name|last
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pr
operator|.
name|list
operator|!=
literal|null
operator|&&
operator|!
name|pr
operator|.
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|typeFromRestriction
operator|=
name|pr
operator|.
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|getPropertyType
argument_list|(
name|defn
argument_list|,
name|pr
operator|.
name|propertyName
argument_list|,
name|typeFromRestriction
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|int
name|getPropertyType
parameter_list|(
name|PropertyDefinition
name|defn
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|defaultVal
parameter_list|)
block|{
if|if
condition|(
name|defn
operator|.
name|isTypeDefined
argument_list|()
condition|)
block|{
return|return
name|defn
operator|.
name|getType
argument_list|()
return|;
block|}
return|return
name|defaultVal
return|;
block|}
specifier|protected
specifier|static
name|PlanResult
name|getPlanResult
parameter_list|(
name|IndexPlan
name|plan
parameter_list|)
block|{
return|return
operator|(
name|PlanResult
operator|)
name|plan
operator|.
name|getAttribute
argument_list|(
name|ATTR_PLAN_RESULT
argument_list|)
return|;
block|}
comment|/**      * Following chars are used as operators in Lucene Query and should be escaped      */
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|QUERY_OPERATORS
init|=
block|{
literal|':'
block|,
literal|'/'
block|,
literal|'!'
block|,
literal|'&'
block|,
literal|'|'
block|,
literal|'='
block|}
decl_stmt|;
comment|/**      * Following logic is taken from org.apache.jackrabbit.core.query.lucene.JackrabbitQueryParser#parse(java.lang.String)      */
specifier|static
name|String
name|rewriteQueryText
parameter_list|(
name|String
name|textsearch
parameter_list|)
block|{
comment|// replace escaped ' with just '
name|StringBuilder
name|rewritten
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// most query parsers recognize 'AND' and 'NOT' as
comment|// keywords.
name|textsearch
operator|=
name|textsearch
operator|.
name|replaceAll
argument_list|(
literal|"AND"
argument_list|,
literal|"and"
argument_list|)
expr_stmt|;
name|textsearch
operator|=
name|textsearch
operator|.
name|replaceAll
argument_list|(
literal|"NOT"
argument_list|,
literal|"not"
argument_list|)
expr_stmt|;
name|boolean
name|escaped
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|textsearch
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|textsearch
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\\'
condition|)
block|{
if|if
condition|(
name|escaped
condition|)
block|{
name|rewritten
operator|.
name|append
argument_list|(
literal|"\\\\"
argument_list|)
expr_stmt|;
name|escaped
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|escaped
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'\''
condition|)
block|{
if|if
condition|(
name|escaped
condition|)
block|{
name|escaped
operator|=
literal|false
expr_stmt|;
block|}
name|rewritten
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Chars
operator|.
name|contains
argument_list|(
name|QUERY_OPERATORS
argument_list|,
name|c
argument_list|)
condition|)
block|{
name|rewritten
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|escaped
condition|)
block|{
name|rewritten
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|escaped
operator|=
literal|false
expr_stmt|;
block|}
name|rewritten
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|rewritten
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
specifier|static
name|String
name|getPathRestriction
parameter_list|(
name|IndexPlan
name|plan
parameter_list|)
block|{
name|Filter
name|f
init|=
name|plan
operator|.
name|getFilter
argument_list|()
decl_stmt|;
name|String
name|pathPrefix
init|=
name|plan
operator|.
name|getPathPrefix
argument_list|()
decl_stmt|;
if|if
condition|(
name|pathPrefix
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|f
operator|.
name|getPath
argument_list|()
return|;
block|}
name|String
name|relativePath
init|=
name|PathUtils
operator|.
name|relativize
argument_list|(
name|pathPrefix
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
return|return
literal|"/"
operator|+
name|relativePath
return|;
block|}
specifier|static
class|class
name|FulltextResultRow
block|{
specifier|final
name|String
name|path
decl_stmt|;
specifier|final
name|double
name|score
decl_stmt|;
specifier|final
name|String
name|suggestion
decl_stmt|;
specifier|final
name|boolean
name|isVirutal
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|excerpts
decl_stmt|;
specifier|final
name|String
name|explanation
decl_stmt|;
specifier|final
name|List
argument_list|<
name|FacetResult
operator|.
name|Facet
argument_list|>
name|facets
decl_stmt|;
name|FulltextResultRow
parameter_list|(
name|String
name|path
parameter_list|,
name|double
name|score
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|excerpts
parameter_list|,
name|List
argument_list|<
name|FacetResult
operator|.
name|Facet
argument_list|>
name|facets
parameter_list|,
name|String
name|explanation
parameter_list|)
block|{
name|this
operator|.
name|explanation
operator|=
name|explanation
expr_stmt|;
name|this
operator|.
name|excerpts
operator|=
name|excerpts
expr_stmt|;
name|this
operator|.
name|facets
operator|=
name|facets
expr_stmt|;
name|this
operator|.
name|isVirutal
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|this
operator|.
name|suggestion
operator|=
literal|null
expr_stmt|;
block|}
name|FulltextResultRow
parameter_list|(
name|String
name|suggestion
parameter_list|,
name|long
name|weight
parameter_list|)
block|{
name|this
operator|.
name|isVirutal
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|path
operator|=
literal|"/"
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|weight
expr_stmt|;
name|this
operator|.
name|suggestion
operator|=
name|suggestion
expr_stmt|;
name|this
operator|.
name|excerpts
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|facets
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|explanation
operator|=
literal|null
expr_stmt|;
block|}
name|FulltextResultRow
parameter_list|(
name|String
name|suggestion
parameter_list|)
block|{
name|this
argument_list|(
name|suggestion
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s (%1.2f)"
argument_list|,
name|path
argument_list|,
name|score
argument_list|)
return|;
block|}
block|}
comment|/**      * A cursor over Fulltext results. The result includes the path,      * and the jcr:score pseudo-property as returned by Lucene.      */
specifier|static
class|class
name|FulltextPathCursor
implements|implements
name|Cursor
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
specifier|static
specifier|final
name|int
name|TRAVERSING_WARNING
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.traversing.warning"
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Cursor
name|pathCursor
decl_stmt|;
specifier|private
specifier|final
name|String
name|pathPrefix
decl_stmt|;
name|FulltextResultRow
name|currentRow
decl_stmt|;
specifier|private
specifier|final
name|SizeEstimator
name|sizeEstimator
decl_stmt|;
specifier|private
name|long
name|estimatedSize
decl_stmt|;
specifier|private
name|int
name|numberOfFacets
decl_stmt|;
name|FulltextPathCursor
parameter_list|(
specifier|final
name|Iterator
argument_list|<
name|FulltextResultRow
argument_list|>
name|it
parameter_list|,
specifier|final
name|IndexPlan
name|plan
parameter_list|,
name|QueryLimits
name|settings
parameter_list|,
name|SizeEstimator
name|sizeEstimator
parameter_list|)
block|{
name|pathPrefix
operator|=
name|plan
operator|.
name|getPathPrefix
argument_list|()
expr_stmt|;
name|this
operator|.
name|sizeEstimator
operator|=
name|sizeEstimator
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|pathIterator
init|=
operator|new
name|Iterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|private
name|int
name|readCount
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|it
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
name|currentRow
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|readCount
operator|++
expr_stmt|;
if|if
condition|(
name|readCount
operator|%
name|TRAVERSING_WARNING
operator|==
literal|0
condition|)
block|{
name|Cursors
operator|.
name|checkReadLimit
argument_list|(
name|readCount
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"Index-Traversed {} nodes with filter {}"
argument_list|,
name|readCount
argument_list|,
name|plan
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|currentRow
operator|.
name|path
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|PlanResult
name|planResult
init|=
name|getPlanResult
argument_list|(
name|plan
argument_list|)
decl_stmt|;
name|pathCursor
operator|=
operator|new
name|PathCursor
argument_list|(
name|pathIterator
argument_list|,
name|planResult
operator|.
name|isUniquePathsRequired
argument_list|()
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|numberOfFacets
operator|=
name|planResult
operator|.
name|indexDefinition
operator|.
name|getNumberOfTopFacets
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|pathCursor
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|pathCursor
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|IndexRow
name|next
parameter_list|()
block|{
specifier|final
name|IndexRow
name|pathRow
init|=
name|pathCursor
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|IndexRow
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isVirtualRow
parameter_list|()
block|{
return|return
name|currentRow
operator|.
name|isVirutal
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
name|String
name|sub
init|=
name|pathRow
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|isVirtualRow
argument_list|()
condition|)
block|{
return|return
name|sub
return|;
block|}
elseif|else
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|pathPrefix
argument_list|)
operator|&&
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|sub
argument_list|)
condition|)
block|{
return|return
name|pathPrefix
return|;
block|}
elseif|else
if|if
condition|(
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|sub
argument_list|)
condition|)
block|{
return|return
name|pathPrefix
operator|+
name|sub
return|;
block|}
else|else
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|pathPrefix
argument_list|,
name|sub
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|PropertyValue
name|getValue
parameter_list|(
name|String
name|columnName
parameter_list|)
block|{
comment|// overlay the score
if|if
condition|(
name|QueryConstants
operator|.
name|JCR_SCORE
operator|.
name|equals
argument_list|(
name|columnName
argument_list|)
condition|)
block|{
return|return
name|PropertyValues
operator|.
name|newDouble
argument_list|(
name|currentRow
operator|.
name|score
argument_list|)
return|;
block|}
if|if
condition|(
name|QueryConstants
operator|.
name|REP_SPELLCHECK
operator|.
name|equals
argument_list|(
name|columnName
argument_list|)
operator|||
name|QueryConstants
operator|.
name|REP_SUGGEST
operator|.
name|equals
argument_list|(
name|columnName
argument_list|)
condition|)
block|{
return|return
name|PropertyValues
operator|.
name|newString
argument_list|(
name|currentRow
operator|.
name|suggestion
argument_list|)
return|;
block|}
if|if
condition|(
name|QueryConstants
operator|.
name|OAK_SCORE_EXPLANATION
operator|.
name|equals
argument_list|(
name|columnName
argument_list|)
condition|)
block|{
return|return
name|PropertyValues
operator|.
name|newString
argument_list|(
name|currentRow
operator|.
name|explanation
argument_list|)
return|;
block|}
if|if
condition|(
name|columnName
operator|.
name|startsWith
argument_list|(
name|QueryConstants
operator|.
name|REP_EXCERPT
argument_list|)
condition|)
block|{
name|String
name|excerpt
init|=
name|currentRow
operator|.
name|excerpts
operator|.
name|get
argument_list|(
name|columnName
argument_list|)
decl_stmt|;
if|if
condition|(
name|excerpt
operator|!=
literal|null
condition|)
block|{
return|return
name|PropertyValues
operator|.
name|newString
argument_list|(
name|excerpt
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|columnName
operator|.
name|startsWith
argument_list|(
name|QueryConstants
operator|.
name|REP_FACET
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|FacetResult
operator|.
name|Facet
argument_list|>
name|facets
init|=
name|currentRow
operator|.
name|facets
decl_stmt|;
try|try
block|{
if|if
condition|(
name|facets
operator|!=
literal|null
condition|)
block|{
name|JsopWriter
name|writer
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|writer
operator|.
name|object
argument_list|()
expr_stmt|;
for|for
control|(
name|FacetResult
operator|.
name|Facet
name|f
range|:
name|facets
control|)
block|{
name|writer
operator|.
name|key
argument_list|(
name|f
operator|.
name|getLabel
argument_list|()
argument_list|)
operator|.
name|value
argument_list|(
name|f
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|PropertyValues
operator|.
name|newString
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
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
block|}
return|return
name|pathRow
operator|.
name|getValue
argument_list|(
name|columnName
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|(
name|SizePrecision
name|precision
parameter_list|,
name|long
name|max
parameter_list|)
block|{
if|if
condition|(
name|estimatedSize
operator|!=
literal|0
condition|)
block|{
return|return
name|estimatedSize
return|;
block|}
return|return
name|estimatedSize
operator|=
name|sizeEstimator
operator|.
name|getSize
argument_list|()
return|;
block|}
block|}
specifier|static
name|String
name|parseFacetField
parameter_list|(
name|String
name|columnName
parameter_list|)
block|{
return|return
name|columnName
operator|.
name|substring
argument_list|(
name|QueryConstants
operator|.
name|REP_FACET
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|,
name|columnName
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
end_class

end_unit
