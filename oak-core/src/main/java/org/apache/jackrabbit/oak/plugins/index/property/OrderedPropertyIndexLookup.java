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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|contains
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
name|DECLARING_NODE_TYPES
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
name|INDEX_CONTENT_NODE_NAME
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
name|PROPERTY_NAMES
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
name|property
operator|.
name|PropertyIndex
operator|.
name|encode
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
name|OrderEntry
operator|.
name|Order
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
name|Set
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
name|plugins
operator|.
name|index
operator|.
name|property
operator|.
name|OrderedIndex
operator|.
name|OrderDirection
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
name|property
operator|.
name|strategy
operator|.
name|OrderedContentMirrorStoreStrategy
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
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|OrderedPropertyIndexLookup
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
name|OrderedPropertyIndexLookup
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * the standard Ascending ordered index      */
specifier|private
specifier|static
specifier|final
name|OrderedContentMirrorStoreStrategy
name|STORE
init|=
operator|new
name|OrderedContentMirrorStoreStrategy
argument_list|()
decl_stmt|;
comment|/**      * the descending ordered index      */
specifier|private
specifier|static
specifier|final
name|OrderedContentMirrorStoreStrategy
name|REVERSED_STORE
init|=
operator|new
name|OrderedContentMirrorStoreStrategy
argument_list|(
name|OrderDirection
operator|.
name|DESC
argument_list|)
decl_stmt|;
comment|/**      * we're slightly more expensive than the standard PropertyIndex.      */
specifier|private
specifier|static
specifier|final
name|double
name|COST_OVERHEAD
init|=
literal|3
decl_stmt|;
comment|/**      * The maximum cost when the index can be used.      */
specifier|private
specifier|static
specifier|final
name|int
name|MAX_COST
init|=
literal|100
decl_stmt|;
specifier|private
name|NodeState
name|root
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|OrderedPropertyIndexLookup
name|parent
decl_stmt|;
specifier|public
name|OrderedPropertyIndexLookup
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
name|this
argument_list|(
name|root
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|OrderedPropertyIndexLookup
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|String
name|name
parameter_list|,
name|OrderedPropertyIndexLookup
name|parent
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
comment|/**      * Get the node with the index definition for the given property, if there      * is an applicable index with data.      *      * @param propertyName the property name      * @param filter the filter (which contains information of all supertypes,      *            unless the filter matches all types)      * @return the node where the index definition (metadata) is stored (the      *         parent of ":index"), or null if no index definition or index data      *         node was found      */
annotation|@
name|Nullable
name|NodeState
name|getIndexNode
parameter_list|(
name|NodeState
name|node
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
comment|// keep a fallback to a matching index def that has *no* node type constraints
comment|// (initially, there is no fallback)
name|NodeState
name|fallback
init|=
literal|null
decl_stmt|;
name|NodeState
name|state
init|=
name|node
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
name|state
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|NodeState
name|index
init|=
name|entry
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|PropertyState
name|type
init|=
name|index
operator|.
name|getProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
operator|||
name|type
operator|.
name|isArray
argument_list|()
operator|||
operator|!
name|getType
argument_list|()
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
continue|continue;
block|}
if|if
condition|(
name|contains
argument_list|(
name|index
operator|.
name|getNames
argument_list|(
name|PROPERTY_NAMES
argument_list|)
argument_list|,
name|propertyName
argument_list|)
condition|)
block|{
name|NodeState
name|indexContent
init|=
name|index
operator|.
name|getChildNode
argument_list|(
name|INDEX_CONTENT_NODE_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|indexContent
operator|.
name|exists
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|supertypes
init|=
name|getSuperTypes
argument_list|(
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|.
name|hasProperty
argument_list|(
name|DECLARING_NODE_TYPES
argument_list|)
condition|)
block|{
if|if
condition|(
name|supertypes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|typeName
range|:
name|index
operator|.
name|getNames
argument_list|(
name|DECLARING_NODE_TYPES
argument_list|)
control|)
block|{
if|if
condition|(
name|supertypes
operator|.
name|contains
argument_list|(
name|typeName
argument_list|)
condition|)
block|{
comment|// TODO: prefer the most specific type restriction
return|return
name|index
return|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|supertypes
operator|==
literal|null
condition|)
block|{
return|return
name|index
return|;
block|}
elseif|else
if|if
condition|(
name|fallback
operator|==
literal|null
condition|)
block|{
comment|// update the fallback
name|fallback
operator|=
name|index
expr_stmt|;
block|}
block|}
block|}
return|return
name|fallback
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getSuperTypes
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|!=
literal|null
operator|&&
operator|!
name|filter
operator|.
name|matchesAllTypes
argument_list|()
condition|)
block|{
return|return
name|filter
operator|.
name|getSupertypes
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|static
name|OrderedContentMirrorStoreStrategy
name|getStrategy
parameter_list|(
name|NodeState
name|indexMeta
parameter_list|)
block|{
if|if
condition|(
name|OrderDirection
operator|.
name|isAscending
argument_list|(
name|indexMeta
argument_list|)
condition|)
block|{
return|return
name|STORE
return|;
block|}
else|else
block|{
return|return
name|REVERSED_STORE
return|;
block|}
block|}
specifier|public
name|boolean
name|isAscending
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
name|NodeState
name|indexMeta
init|=
name|getIndexNode
argument_list|(
name|root
argument_list|,
name|propertyName
argument_list|,
name|filter
argument_list|)
decl_stmt|;
return|return
name|OrderDirection
operator|.
name|isAscending
argument_list|(
name|indexMeta
argument_list|)
return|;
block|}
comment|/**      * retrieve the type of the index      *      * @return the type      */
name|String
name|getType
parameter_list|()
block|{
return|return
name|OrderedIndex
operator|.
name|TYPE
return|;
block|}
specifier|public
name|double
name|getCost
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|PropertyValue
name|value
parameter_list|)
block|{
name|double
name|cost
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|NodeState
name|indexMeta
init|=
name|getIndexNode
argument_list|(
name|root
argument_list|,
name|propertyName
argument_list|,
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMeta
operator|!=
literal|null
condition|)
block|{
comment|// we relay then on the standard property index for the cost
name|cost
operator|=
name|COST_OVERHEAD
operator|+
name|getStrategy
argument_list|(
name|indexMeta
argument_list|)
operator|.
name|count
argument_list|(
name|indexMeta
argument_list|,
name|PropertyIndex
operator|.
name|encode
argument_list|(
name|value
argument_list|)
argument_list|,
name|MAX_COST
argument_list|)
expr_stmt|;
block|}
return|return
name|cost
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|query
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|PropertyValue
name|value
parameter_list|)
block|{
name|NodeState
name|indexMeta
init|=
name|getIndexNode
argument_list|(
name|root
argument_list|,
name|propertyName
argument_list|,
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMeta
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No index for "
operator|+
name|propertyName
argument_list|)
throw|;
block|}
return|return
name|getStrategy
argument_list|(
name|indexMeta
argument_list|)
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|propertyName
argument_list|,
name|indexMeta
argument_list|,
name|encode
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * query the strategy for the provided constrains      *       * @param filter      * @param propertyName      * @param pr      * @return the result set      */
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|query
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|PropertyRestriction
name|pr
parameter_list|)
block|{
name|NodeState
name|indexMeta
init|=
name|getIndexNode
argument_list|(
name|root
argument_list|,
name|propertyName
argument_list|,
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMeta
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No index for "
operator|+
name|propertyName
argument_list|)
throw|;
block|}
return|return
name|getStrategy
argument_list|(
name|indexMeta
argument_list|)
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|propertyName
argument_list|,
name|indexMeta
argument_list|,
name|pr
argument_list|)
return|;
block|}
comment|/**      * Collect plans for ordered indexes along the given path and order entry.      *      * @param filter a filter description.      * @param path a relative path from this lookup to the filter path.      * @param oe an order entry.      * @param plans collected plans are added to this list.      */
name|void
name|collectPlans
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|String
name|path
parameter_list|,
name|QueryIndex
operator|.
name|OrderEntry
name|oe
parameter_list|,
name|List
argument_list|<
name|QueryIndex
operator|.
name|IndexPlan
argument_list|>
name|plans
parameter_list|)
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
name|NodeState
name|definition
init|=
name|getIndexNode
argument_list|(
name|root
argument_list|,
name|propertyName
argument_list|,
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|definition
operator|!=
literal|null
condition|)
block|{
name|Order
name|order
init|=
name|OrderDirection
operator|.
name|isAscending
argument_list|(
name|definition
argument_list|)
condition|?
name|Order
operator|.
name|ASCENDING
else|:
name|Order
operator|.
name|DESCENDING
decl_stmt|;
name|long
name|entryCount
init|=
name|getStrategy
argument_list|(
name|definition
argument_list|)
operator|.
name|count
argument_list|(
name|definition
argument_list|,
operator|(
name|PropertyRestriction
operator|)
literal|null
argument_list|,
name|MAX_COST
argument_list|)
decl_stmt|;
name|QueryIndex
operator|.
name|IndexPlan
operator|.
name|Builder
name|b
init|=
name|OrderedPropertyIndex
operator|.
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
name|QueryIndex
operator|.
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
name|order
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|setEstimatedEntryCount
argument_list|(
name|entryCount
argument_list|)
expr_stmt|;
name|b
operator|.
name|setDefinition
argument_list|(
name|definition
argument_list|)
expr_stmt|;
name|b
operator|.
name|setPathPrefix
argument_list|(
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|QueryIndex
operator|.
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
comment|// walk down path
name|String
name|remainder
init|=
literal|""
decl_stmt|;
name|OrderedPropertyIndexLookup
name|lookup
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|element
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
if|if
condition|(
name|lookup
operator|==
literal|null
condition|)
block|{
name|lookup
operator|=
operator|new
name|OrderedPropertyIndexLookup
argument_list|(
name|root
operator|.
name|getChildNode
argument_list|(
name|element
argument_list|)
argument_list|,
name|element
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|remainder
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|remainder
argument_list|,
name|element
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|lookup
operator|!=
literal|null
condition|)
block|{
name|lookup
operator|.
name|collectPlans
argument_list|(
name|filter
argument_list|,
name|remainder
argument_list|,
name|oe
argument_list|,
name|plans
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Collect plans for ordered indexes along the given path and property      * restriction.      *      * @param filter a filter description.      * @param path a relative path from this lookup to the filter path.      * @param pr a property restriction.      * @param plans collected plans are added to this list.      */
name|void
name|collectPlans
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|String
name|path
parameter_list|,
name|PropertyRestriction
name|pr
parameter_list|,
name|List
argument_list|<
name|QueryIndex
operator|.
name|IndexPlan
argument_list|>
name|plans
parameter_list|)
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
name|NodeState
name|definition
init|=
name|getIndexNode
argument_list|(
name|root
argument_list|,
name|propertyName
argument_list|,
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|definition
operator|!=
literal|null
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
if|if
condition|(
name|createPlan
condition|)
block|{
comment|// we always return a sorted set
name|Order
name|order
init|=
name|OrderDirection
operator|.
name|isAscending
argument_list|(
name|definition
argument_list|)
condition|?
name|Order
operator|.
name|ASCENDING
else|:
name|Order
operator|.
name|DESCENDING
decl_stmt|;
name|QueryIndex
operator|.
name|IndexPlan
operator|.
name|Builder
name|b
init|=
name|OrderedPropertyIndex
operator|.
name|getIndexPlanBuilder
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|b
operator|.
name|setDefinition
argument_list|(
name|definition
argument_list|)
expr_stmt|;
name|b
operator|.
name|setSortOrder
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|QueryIndex
operator|.
name|OrderEntry
argument_list|(
name|propertyName
argument_list|,
name|Type
operator|.
name|UNDEFINED
argument_list|,
name|order
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|count
init|=
name|getStrategy
argument_list|(
name|definition
argument_list|)
operator|.
name|count
argument_list|(
name|definition
argument_list|,
name|pr
argument_list|,
name|MAX_COST
argument_list|)
decl_stmt|;
name|b
operator|.
name|setEstimatedEntryCount
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|b
operator|.
name|setPropertyRestriction
argument_list|(
name|pr
argument_list|)
expr_stmt|;
name|b
operator|.
name|setPathPrefix
argument_list|(
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|QueryIndex
operator|.
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
comment|// walk down path
name|String
name|remainder
init|=
literal|""
decl_stmt|;
name|OrderedPropertyIndexLookup
name|lookup
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|element
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
if|if
condition|(
name|lookup
operator|==
literal|null
condition|)
block|{
name|lookup
operator|=
operator|new
name|OrderedPropertyIndexLookup
argument_list|(
name|root
operator|.
name|getChildNode
argument_list|(
name|element
argument_list|)
argument_list|,
name|element
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|remainder
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|remainder
argument_list|,
name|element
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|lookup
operator|!=
literal|null
condition|)
block|{
name|lookup
operator|.
name|collectPlans
argument_list|(
name|filter
argument_list|,
name|remainder
argument_list|,
name|pr
argument_list|,
name|plans
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|getPath
parameter_list|()
block|{
return|return
name|buildPath
argument_list|(
operator|new
name|StringBuilder
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|StringBuilder
name|buildPath
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|buildPath
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
return|;
block|}
block|}
end_class

end_unit

