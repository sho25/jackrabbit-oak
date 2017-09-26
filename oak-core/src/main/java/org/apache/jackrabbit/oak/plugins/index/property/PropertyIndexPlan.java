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
name|base
operator|.
name|Predicates
operator|.
name|in
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
name|collect
operator|.
name|Iterables
operator|.
name|any
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
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptySet
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
name|PROPERTY_NAMES
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
name|IndexConstants
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
name|IndexStoreStrategy
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
name|filter
operator|.
name|PathFilter
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
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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

begin_comment
comment|/**  * Plan for querying a given property index using a given filter.  */
end_comment

begin_class
specifier|public
class|class
name|PropertyIndexPlan
block|{
comment|/**      * The cost overhead to use the index in number of read operations.      */
specifier|public
specifier|static
specifier|final
name|double
name|COST_OVERHEAD
init|=
literal|2
decl_stmt|;
comment|/**      * The maximum cost when the index can be used.      */
specifier|static
specifier|final
name|int
name|MAX_COST
init|=
literal|100
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|definition
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|properties
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|IndexStoreStrategy
argument_list|>
name|strategies
decl_stmt|;
specifier|private
specifier|final
name|Filter
name|filter
decl_stmt|;
specifier|private
name|boolean
name|matchesAllTypes
decl_stmt|;
specifier|private
name|boolean
name|matchesNodeTypes
decl_stmt|;
specifier|private
specifier|final
name|double
name|cost
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|values
decl_stmt|;
specifier|private
specifier|final
name|int
name|depth
decl_stmt|;
specifier|private
specifier|final
name|PathFilter
name|pathFilter
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|unique
decl_stmt|;
name|PropertyIndexPlan
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|root
parameter_list|,
name|NodeState
name|definition
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|root
argument_list|,
name|definition
argument_list|,
name|filter
argument_list|,
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PropertyIndexPlan
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|root
parameter_list|,
name|NodeState
name|definition
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|MountInfoProvider
name|mountInfoProvider
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|unique
operator|=
name|definition
operator|.
name|getBoolean
argument_list|(
name|IndexConstants
operator|.
name|UNIQUE_PROPERTY_NAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|definition
operator|=
name|definition
expr_stmt|;
name|this
operator|.
name|properties
operator|=
name|newHashSet
argument_list|(
name|definition
operator|.
name|getNames
argument_list|(
name|PROPERTY_NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|pathFilter
operator|=
name|PathFilter
operator|.
name|from
argument_list|(
name|definition
operator|.
name|builder
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|strategies
operator|=
name|getStrategies
argument_list|(
name|definition
argument_list|,
name|mountInfoProvider
argument_list|)
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|types
init|=
name|definition
operator|.
name|getNames
argument_list|(
name|DECLARING_NODE_TYPES
argument_list|)
decl_stmt|;
comment|// if there is no such property, then all nodetypes are matched
name|this
operator|.
name|matchesAllTypes
operator|=
operator|!
name|definition
operator|.
name|hasProperty
argument_list|(
name|DECLARING_NODE_TYPES
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchesNodeTypes
operator|=
name|matchesAllTypes
operator|||
name|any
argument_list|(
name|types
argument_list|,
name|in
argument_list|(
name|filter
operator|.
name|getSupertypes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ValuePattern
name|valuePattern
init|=
operator|new
name|ValuePattern
argument_list|(
name|definition
argument_list|)
decl_stmt|;
name|double
name|bestCost
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|bestValues
init|=
name|emptySet
argument_list|()
decl_stmt|;
name|int
name|bestDepth
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|matchesNodeTypes
operator|&&
name|pathFilter
operator|.
name|areAllDescendantsIncluded
argument_list|(
name|filter
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|property
range|:
name|properties
control|)
block|{
name|PropertyRestriction
name|restriction
init|=
name|filter
operator|.
name|getPropertyRestriction
argument_list|(
name|property
argument_list|)
decl_stmt|;
name|int
name|depth
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|restriction
operator|==
literal|null
condition|)
block|{
comment|// no direct restriction, try one with a relative path
comment|// TODO: avoid repeated scans through the restrictions
name|String
name|suffix
init|=
literal|"/"
operator|+
name|property
decl_stmt|;
for|for
control|(
name|PropertyRestriction
name|relative
range|:
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
control|)
block|{
if|if
condition|(
name|relative
operator|.
name|propertyName
operator|.
name|endsWith
argument_list|(
name|suffix
argument_list|)
condition|)
block|{
name|restriction
operator|=
name|relative
expr_stmt|;
name|depth
operator|=
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|relative
operator|.
name|propertyName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|restriction
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|restriction
operator|.
name|isNullRestriction
argument_list|()
condition|)
block|{
comment|// covering indexes are not currently supported
continue|continue;
block|}
if|if
condition|(
name|depth
operator|!=
literal|1
operator|&&
operator|!
name|matchesAllTypes
condition|)
block|{
comment|// OAK-3589
comment|// index has a nodetype condition, and the property condition is
comment|// relative: can not use this index, as we don't know the nodetype
comment|// of the child node (well, we could, for some node types)
continue|continue;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|values
init|=
name|ValuePatternUtil
operator|.
name|getValues
argument_list|(
name|restriction
argument_list|,
operator|new
name|ValuePattern
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|valuePattern
operator|.
name|matchesAll
argument_list|()
condition|)
block|{
comment|// matches all values: not a problem
block|}
elseif|else
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
comment|// "is not null" condition, but we have a value pattern
comment|// that doesn't match everything
name|String
name|prefix
init|=
name|ValuePatternUtil
operator|.
name|getLongestPrefix
argument_list|(
name|filter
argument_list|,
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|valuePattern
operator|.
name|matchesPrefix
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
comment|// region match which is not fully in the pattern
continue|continue;
block|}
block|}
else|else
block|{
comment|// we have a value pattern, for example (a|b),
comment|// but we search (also) for 'c': can't match
if|if
condition|(
operator|!
name|valuePattern
operator|.
name|matchesAll
argument_list|(
name|values
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
name|values
operator|=
name|PropertyIndexUtil
operator|.
name|encode
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|double
name|cost
init|=
name|strategies
operator|.
name|isEmpty
argument_list|()
condition|?
name|MAX_COST
else|:
literal|0
decl_stmt|;
for|for
control|(
name|IndexStoreStrategy
name|strategy
range|:
name|strategies
control|)
block|{
name|cost
operator|+=
name|strategy
operator|.
name|count
argument_list|(
name|filter
argument_list|,
name|root
argument_list|,
name|definition
argument_list|,
name|values
argument_list|,
name|MAX_COST
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|unique
operator|&&
name|cost
operator|<=
literal|1
condition|)
block|{
comment|// for unique index, for the normal case
comment|// (that is, for a regular lookup)
comment|// no further reads are needed
name|cost
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|cost
operator|<
name|bestCost
condition|)
block|{
name|bestDepth
operator|=
name|depth
expr_stmt|;
name|bestValues
operator|=
name|values
expr_stmt|;
name|bestCost
operator|=
name|cost
expr_stmt|;
if|if
condition|(
name|bestCost
operator|==
literal|0
condition|)
block|{
comment|// shortcut: not possible to top this
break|break;
block|}
block|}
block|}
block|}
block|}
name|this
operator|.
name|depth
operator|=
name|bestDepth
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|bestValues
expr_stmt|;
name|this
operator|.
name|cost
operator|=
name|COST_OVERHEAD
operator|+
name|bestCost
expr_stmt|;
block|}
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
name|double
name|getCost
parameter_list|()
block|{
return|return
name|cost
return|;
block|}
name|Cursor
name|execute
parameter_list|()
block|{
name|QueryLimits
name|settings
init|=
name|filter
operator|.
name|getQueryLimits
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|iterables
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexStoreStrategy
name|s
range|:
name|strategies
control|)
block|{
name|iterables
operator|.
name|add
argument_list|(
name|s
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|name
argument_list|,
name|definition
argument_list|,
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Cursor
name|cursor
init|=
name|Cursors
operator|.
name|newPathCursor
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|iterables
argument_list|)
argument_list|,
name|settings
argument_list|)
decl_stmt|;
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
name|settings
argument_list|)
expr_stmt|;
block|}
return|return
name|cursor
return|;
block|}
name|Filter
name|getFilter
parameter_list|()
block|{
return|return
name|filter
return|;
block|}
name|Set
argument_list|<
name|IndexStoreStrategy
argument_list|>
name|getStrategies
parameter_list|(
name|NodeState
name|definition
parameter_list|,
name|MountInfoProvider
name|mountInfoProvider
parameter_list|)
block|{
return|return
name|Multiplexers
operator|.
name|getStrategies
argument_list|(
name|unique
argument_list|,
name|mountInfoProvider
argument_list|,
name|definition
argument_list|,
name|INDEX_CONTENT_NODE_NAME
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"property "
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" IS NOT NULL"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" NOT APPLICABLE"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" = "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|values
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" IN ("
argument_list|)
expr_stmt|;
name|boolean
name|comma
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|comma
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|comma
operator|=
literal|true
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

