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
name|query
operator|.
name|index
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
name|Collection
import|;
end_import

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
operator|.
name|Entry
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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
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
name|ArrayListMultimap
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
name|ListMultimap
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
name|query
operator|.
name|QueryEngineSettings
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
name|ast
operator|.
name|JoinConditionImpl
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
name|ast
operator|.
name|NativeFunctionImpl
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
name|ast
operator|.
name|Operator
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
name|ast
operator|.
name|SelectorImpl
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
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|PermissionProvider
import|;
end_import

begin_comment
comment|/**  * A filter or lookup condition.  */
end_comment

begin_class
specifier|public
class|class
name|FilterImpl
implements|implements
name|Filter
block|{
comment|/**      * The selector this filter applies to.      */
specifier|private
specifier|final
name|SelectorImpl
name|selector
decl_stmt|;
specifier|private
specifier|final
name|String
name|queryStatement
decl_stmt|;
specifier|private
specifier|final
name|QueryEngineSettings
name|settings
decl_stmt|;
comment|/**      * Whether the filter is always false.      */
specifier|private
name|boolean
name|alwaysFalse
decl_stmt|;
comment|/**      * inherited from the selector, duplicated here so it can be over-written by      * other filters      */
specifier|private
name|boolean
name|matchesAllTypes
decl_stmt|;
comment|/**      *  The path, or "/" (the root node, meaning no filter) if not set.      */
specifier|private
name|String
name|path
init|=
literal|"/"
decl_stmt|;
specifier|private
name|PathRestriction
name|pathRestriction
init|=
name|PathRestriction
operator|.
name|NO_RESTRICTION
decl_stmt|;
comment|/**      * Additional path restrictions whose values are known only at runtime,      * for example paths set by other (join-) selectors.      */
specifier|private
name|String
name|pathPlan
decl_stmt|;
comment|/**      * The fulltext search conditions, if any.      */
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|fulltextConditions
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|FullTextExpression
name|fullTextConstraint
decl_stmt|;
comment|/**      * The list of restrictions for each property. A restriction may be x=1.      *<p>      * Each property may have multiple restrictions, which means all      * restrictions must apply, for example x=1 and x=2. For this case, only      * multi-valued properties match if it contains both the values 1 and 2.      */
specifier|private
specifier|final
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|PropertyRestriction
argument_list|>
name|propertyRestrictions
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
comment|/**      * Only return distinct values.      */
specifier|private
name|boolean
name|distinct
decl_stmt|;
comment|/**      * Set during the prepare phase of a query.      */
specifier|private
name|boolean
name|preparing
decl_stmt|;
comment|// TODO support "order by"
specifier|public
name|FilterImpl
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a filter.      *       * @param selector the selector for the given filter      * @param queryStatement the query statement      */
specifier|public
name|FilterImpl
parameter_list|(
name|SelectorImpl
name|selector
parameter_list|,
name|String
name|queryStatement
parameter_list|,
name|QueryEngineSettings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
name|this
operator|.
name|queryStatement
operator|=
name|queryStatement
expr_stmt|;
name|this
operator|.
name|matchesAllTypes
operator|=
name|selector
operator|!=
literal|null
condition|?
name|selector
operator|.
name|matchesAllTypes
argument_list|()
else|:
literal|false
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
block|}
specifier|public
name|FilterImpl
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|FilterImpl
name|impl
init|=
operator|(
name|FilterImpl
operator|)
name|filter
decl_stmt|;
name|this
operator|.
name|alwaysFalse
operator|=
name|impl
operator|.
name|alwaysFalse
expr_stmt|;
name|this
operator|.
name|distinct
operator|=
name|impl
operator|.
name|distinct
expr_stmt|;
name|this
operator|.
name|fullTextConstraint
operator|=
name|impl
operator|.
name|fullTextConstraint
expr_stmt|;
name|this
operator|.
name|matchesAllTypes
operator|=
name|impl
operator|.
name|matchesAllTypes
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|impl
operator|.
name|path
expr_stmt|;
name|this
operator|.
name|pathRestriction
operator|=
name|impl
operator|.
name|pathRestriction
expr_stmt|;
name|this
operator|.
name|propertyRestrictions
operator|.
name|putAll
argument_list|(
name|impl
operator|.
name|propertyRestrictions
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryStatement
operator|=
name|impl
operator|.
name|queryStatement
expr_stmt|;
name|this
operator|.
name|selector
operator|=
name|impl
operator|.
name|selector
expr_stmt|;
name|this
operator|.
name|matchesAllTypes
operator|=
name|selector
operator|!=
literal|null
condition|?
name|selector
operator|.
name|matchesAllTypes
argument_list|()
else|:
literal|false
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|filter
operator|.
name|getQueryEngineSettings
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setPreparing
parameter_list|(
name|boolean
name|preparing
parameter_list|)
block|{
name|this
operator|.
name|preparing
operator|=
name|preparing
expr_stmt|;
block|}
specifier|public
name|boolean
name|isPreparing
parameter_list|()
block|{
return|return
name|preparing
return|;
block|}
comment|/**      * Whether the given selector is already prepared during the prepare phase      * of a join. That means, check whether the passed selector can already      * provide data.      *       * @param selector the selector to test      * @return true if it is already prepared      */
specifier|public
name|boolean
name|isPrepared
parameter_list|(
name|SelectorImpl
name|selector
parameter_list|)
block|{
return|return
name|selector
operator|.
name|isPrepared
argument_list|()
return|;
block|}
comment|/**      * Get the path.      *      * @return the path      */
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
annotation|@
name|Override
specifier|public
name|PathRestriction
name|getPathRestriction
parameter_list|()
block|{
return|return
name|pathRestriction
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPathPlan
parameter_list|()
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|p
init|=
name|path
decl_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|p
operator|=
literal|""
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|p
argument_list|)
operator|.
name|append
argument_list|(
name|pathRestriction
argument_list|)
expr_stmt|;
if|if
condition|(
name|pathPlan
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"&& "
argument_list|)
operator|.
name|append
argument_list|(
name|pathPlan
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
specifier|public
name|void
name|setPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
specifier|public
name|boolean
name|isDistinct
parameter_list|()
block|{
return|return
name|distinct
return|;
block|}
specifier|public
name|void
name|setDistinct
parameter_list|(
name|boolean
name|distinct
parameter_list|)
block|{
name|this
operator|.
name|distinct
operator|=
name|distinct
expr_stmt|;
block|}
specifier|public
name|void
name|setAlwaysFalse
parameter_list|()
block|{
name|propertyRestrictions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|path
operator|=
literal|"/"
expr_stmt|;
name|pathRestriction
operator|=
name|PathRestriction
operator|.
name|EXACT
expr_stmt|;
name|alwaysFalse
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAlwaysFalse
parameter_list|()
block|{
return|return
name|alwaysFalse
return|;
block|}
annotation|@
name|Override
specifier|public
name|SelectorImpl
name|getSelector
parameter_list|()
block|{
return|return
name|selector
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matchesAllTypes
parameter_list|()
block|{
return|return
name|matchesAllTypes
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getSupertypes
parameter_list|()
block|{
return|return
name|selector
operator|==
literal|null
condition|?
name|Collections
operator|.
name|EMPTY_SET
else|:
name|selector
operator|.
name|getSupertypes
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getPrimaryTypes
parameter_list|()
block|{
return|return
name|selector
operator|==
literal|null
condition|?
name|Collections
operator|.
name|EMPTY_SET
else|:
name|selector
operator|.
name|getPrimaryTypes
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getMixinTypes
parameter_list|()
block|{
return|return
name|selector
operator|==
literal|null
condition|?
name|Collections
operator|.
name|EMPTY_SET
else|:
name|selector
operator|.
name|getMixinTypes
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|PropertyRestriction
argument_list|>
name|getPropertyRestrictions
parameter_list|()
block|{
return|return
name|propertyRestrictions
operator|.
name|values
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyRestriction
name|getPropertyRestriction
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
name|List
argument_list|<
name|PropertyRestriction
argument_list|>
name|list
init|=
name|propertyRestrictions
operator|.
name|get
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
name|int
name|bestSort
init|=
operator|-
literal|1
decl_stmt|;
name|PropertyRestriction
name|best
init|=
literal|null
decl_stmt|;
for|for
control|(
name|PropertyRestriction
name|x
range|:
name|list
control|)
block|{
name|int
name|sort
init|=
name|x
operator|.
name|sortOrder
argument_list|()
decl_stmt|;
if|if
condition|(
name|sort
operator|>
name|bestSort
condition|)
block|{
name|bestSort
operator|=
name|sort
expr_stmt|;
name|best
operator|=
name|x
expr_stmt|;
block|}
block|}
return|return
name|best
return|;
block|}
specifier|public
name|boolean
name|testPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|isAlwaysFalse
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
switch|switch
condition|(
name|pathRestriction
condition|)
block|{
case|case
name|NO_RESTRICTION
case|:
return|return
literal|true
return|;
case|case
name|EXACT
case|:
return|return
name|path
operator|.
name|matches
argument_list|(
name|this
operator|.
name|path
argument_list|)
return|;
case|case
name|PARENT
case|:
return|return
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|this
operator|.
name|path
argument_list|)
operator|.
name|equals
argument_list|(
name|path
argument_list|)
return|;
case|case
name|DIRECT_CHILDREN
case|:
return|return
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
operator|.
name|equals
argument_list|(
name|this
operator|.
name|path
argument_list|)
return|;
case|case
name|ALL_CHILDREN
case|:
return|return
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|this
operator|.
name|path
argument_list|,
name|path
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown path restriction: "
operator|+
name|pathRestriction
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|restrictPropertyAsList
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|List
argument_list|<
name|PropertyValue
argument_list|>
name|list
parameter_list|)
block|{
name|PropertyRestriction
name|x
init|=
operator|new
name|PropertyRestriction
argument_list|()
decl_stmt|;
name|x
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
name|x
operator|.
name|list
operator|=
name|list
expr_stmt|;
name|addRestriction
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|restrictProperty
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|Operator
name|op
parameter_list|,
name|PropertyValue
name|v
parameter_list|)
block|{
name|restrictProperty
argument_list|(
name|propertyName
argument_list|,
name|op
argument_list|,
name|v
argument_list|,
name|PropertyType
operator|.
name|UNDEFINED
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|restrictProperty
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|Operator
name|op
parameter_list|,
name|PropertyValue
name|v
parameter_list|,
name|int
name|propertyType
parameter_list|)
block|{
name|PropertyRestriction
name|x
init|=
operator|new
name|PropertyRestriction
argument_list|()
decl_stmt|;
name|x
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
name|x
operator|.
name|propertyType
operator|=
name|propertyType
expr_stmt|;
switch|switch
condition|(
name|op
condition|)
block|{
case|case
name|EQUAL
case|:
name|x
operator|.
name|first
operator|=
name|x
operator|.
name|last
operator|=
name|v
expr_stmt|;
name|x
operator|.
name|firstIncluding
operator|=
name|x
operator|.
name|lastIncluding
operator|=
literal|true
expr_stmt|;
break|break;
case|case
name|NOT_EQUAL
case|:
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"NOT_EQUAL only supported for NOT_EQUAL NULL"
argument_list|)
throw|;
block|}
break|break;
case|case
name|GREATER_THAN
case|:
name|x
operator|.
name|first
operator|=
name|v
expr_stmt|;
name|x
operator|.
name|firstIncluding
operator|=
literal|false
expr_stmt|;
break|break;
case|case
name|GREATER_OR_EQUAL
case|:
name|x
operator|.
name|first
operator|=
name|v
expr_stmt|;
name|x
operator|.
name|firstIncluding
operator|=
literal|true
expr_stmt|;
break|break;
case|case
name|LESS_THAN
case|:
name|x
operator|.
name|last
operator|=
name|v
expr_stmt|;
name|x
operator|.
name|lastIncluding
operator|=
literal|false
expr_stmt|;
break|break;
case|case
name|LESS_OR_EQUAL
case|:
name|x
operator|.
name|last
operator|=
name|v
expr_stmt|;
name|x
operator|.
name|lastIncluding
operator|=
literal|true
expr_stmt|;
break|break;
case|case
name|LIKE
case|:
comment|// LIKE is handled in the fulltext index
name|x
operator|.
name|isLike
operator|=
literal|true
expr_stmt|;
name|x
operator|.
name|first
operator|=
name|v
expr_stmt|;
break|break;
block|}
name|addRestriction
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add a restriction for the given property, unless the exact same      * restriction is already set.      *       * @param restriction the restriction to add      */
specifier|private
name|void
name|addRestriction
parameter_list|(
name|PropertyRestriction
name|restriction
parameter_list|)
block|{
name|List
argument_list|<
name|PropertyRestriction
argument_list|>
name|list
init|=
name|getPropertyRestrictions
argument_list|(
name|restriction
operator|.
name|propertyName
argument_list|)
decl_stmt|;
for|for
control|(
name|PropertyRestriction
name|old
range|:
name|list
control|)
block|{
if|if
condition|(
name|old
operator|.
name|equals
argument_list|(
name|restriction
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
name|list
operator|.
name|add
argument_list|(
name|restriction
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|PropertyRestriction
argument_list|>
name|getPropertyRestrictions
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
return|return
name|propertyRestrictions
operator|.
name|get
argument_list|(
name|propertyName
argument_list|)
return|;
block|}
specifier|static
name|PropertyValue
name|maxValue
parameter_list|(
name|PropertyValue
name|a
parameter_list|,
name|PropertyValue
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
literal|null
condition|)
block|{
return|return
name|b
return|;
block|}
return|return
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
operator|<
literal|0
condition|?
name|b
else|:
name|a
return|;
block|}
specifier|static
name|PropertyValue
name|minValue
parameter_list|(
name|PropertyValue
name|a
parameter_list|,
name|PropertyValue
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
literal|null
condition|)
block|{
return|return
name|b
return|;
block|}
return|return
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
operator|<=
literal|0
condition|?
name|a
else|:
name|b
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|alwaysFalse
condition|)
block|{
return|return
literal|"Filter(always false)"
return|;
block|}
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"Filter("
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryStatement
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"query="
argument_list|)
operator|.
name|append
argument_list|(
name|queryStatement
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fullTextConstraint
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|" fullText="
argument_list|)
operator|.
name|append
argument_list|(
name|fullTextConstraint
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|", path="
argument_list|)
operator|.
name|append
argument_list|(
name|getPathPlan
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|propertyRestrictions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|", property=["
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|PropertyRestriction
argument_list|>
argument_list|>
argument_list|>
name|iterator
init|=
name|propertyRestrictions
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Entry
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|PropertyRestriction
argument_list|>
argument_list|>
name|p
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|p
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
block|}
name|buff
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|restrictPath
parameter_list|(
name|String
name|addedPath
parameter_list|,
name|PathRestriction
name|addedPathRestriction
parameter_list|)
block|{
if|if
condition|(
name|addedPath
operator|==
literal|null
condition|)
block|{
comment|// currently unknown (prepare time)
name|addedPath
operator|=
literal|"/"
expr_stmt|;
block|}
if|if
condition|(
name|addedPath
operator|.
name|startsWith
argument_list|(
name|JoinConditionImpl
operator|.
name|SPECIAL_PATH_PREFIX
argument_list|)
condition|)
block|{
comment|// not a real path, that means we only adapt the plan
comment|// and that's it
if|if
condition|(
name|pathPlan
operator|==
literal|null
condition|)
block|{
name|pathPlan
operator|=
literal|""
expr_stmt|;
block|}
else|else
block|{
name|pathPlan
operator|+=
literal|"&& "
expr_stmt|;
block|}
name|pathPlan
operator|+=
name|addedPath
operator|+
name|addedPathRestriction
expr_stmt|;
return|return;
block|}
comment|// calculating the intersection of path restrictions
comment|// this is ugly code, but I don't currently see a radically simpler method
switch|switch
condition|(
name|addedPathRestriction
condition|)
block|{
case|case
name|NO_RESTRICTION
case|:
break|break;
case|case
name|PARENT
case|:
switch|switch
condition|(
name|pathRestriction
condition|)
block|{
case|case
name|NO_RESTRICTION
case|:
break|break;
case|case
name|PARENT
case|:
comment|// ignore as it's fast anyway
comment|// (would need to loop to find a common ancestor)
break|break;
case|case
name|EXACT
case|:
case|case
name|ALL_CHILDREN
case|:
case|case
name|DIRECT_CHILDREN
case|:
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|path
argument_list|,
name|addedPath
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
name|pathRestriction
operator|=
name|PathRestriction
operator|.
name|PARENT
expr_stmt|;
name|path
operator|=
name|addedPath
expr_stmt|;
break|break;
case|case
name|EXACT
case|:
switch|switch
condition|(
name|pathRestriction
condition|)
block|{
case|case
name|NO_RESTRICTION
case|:
break|break;
case|case
name|PARENT
case|:
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|addedPath
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|EXACT
case|:
if|if
condition|(
operator|!
name|addedPath
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|ALL_CHILDREN
case|:
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|path
argument_list|,
name|addedPath
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|DIRECT_CHILDREN
case|:
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|addedPath
argument_list|)
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
name|path
operator|=
name|addedPath
expr_stmt|;
name|pathRestriction
operator|=
name|PathRestriction
operator|.
name|EXACT
expr_stmt|;
break|break;
case|case
name|ALL_CHILDREN
case|:
switch|switch
condition|(
name|pathRestriction
condition|)
block|{
case|case
name|NO_RESTRICTION
case|:
name|path
operator|=
name|addedPath
expr_stmt|;
name|pathRestriction
operator|=
name|PathRestriction
operator|.
name|ALL_CHILDREN
expr_stmt|;
break|break;
case|case
name|PARENT
case|:
case|case
name|EXACT
case|:
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|addedPath
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|ALL_CHILDREN
case|:
if|if
condition|(
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|path
argument_list|,
name|addedPath
argument_list|)
condition|)
block|{
name|path
operator|=
name|addedPath
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|path
operator|.
name|equals
argument_list|(
name|addedPath
argument_list|)
operator|&&
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|addedPath
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|DIRECT_CHILDREN
case|:
if|if
condition|(
operator|!
name|path
operator|.
name|equals
argument_list|(
name|addedPath
argument_list|)
operator|&&
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|addedPath
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
break|break;
case|case
name|DIRECT_CHILDREN
case|:
switch|switch
condition|(
name|pathRestriction
condition|)
block|{
case|case
name|NO_RESTRICTION
case|:
name|path
operator|=
name|addedPath
expr_stmt|;
name|pathRestriction
operator|=
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
expr_stmt|;
break|break;
case|case
name|PARENT
case|:
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|addedPath
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|EXACT
case|:
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
operator|.
name|equals
argument_list|(
name|addedPath
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|ALL_CHILDREN
case|:
if|if
condition|(
operator|!
name|path
operator|.
name|equals
argument_list|(
name|addedPath
argument_list|)
operator|&&
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|path
argument_list|,
name|addedPath
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
name|addedPath
expr_stmt|;
name|pathRestriction
operator|=
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
expr_stmt|;
block|}
break|break;
case|case
name|DIRECT_CHILDREN
case|:
if|if
condition|(
operator|!
name|path
operator|.
name|equals
argument_list|(
name|addedPath
argument_list|)
condition|)
block|{
name|setAlwaysFalse
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
break|break;
block|}
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getFulltextConditions
parameter_list|()
block|{
comment|// TODO support fulltext conditions on certain properties
return|return
name|fulltextConditions
return|;
block|}
specifier|public
name|void
name|restrictFulltextCondition
parameter_list|(
name|String
name|condition
parameter_list|)
block|{
name|fulltextConditions
operator|.
name|add
argument_list|(
name|condition
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setFullTextConstraint
parameter_list|(
name|FullTextExpression
name|constraint
parameter_list|)
block|{
name|this
operator|.
name|fullTextConstraint
operator|=
name|constraint
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|FullTextExpression
name|getFullTextConstraint
parameter_list|()
block|{
return|return
name|fullTextConstraint
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|containsNativeConstraint
parameter_list|()
block|{
for|for
control|(
name|String
name|p
range|:
name|propertyRestrictions
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|.
name|startsWith
argument_list|(
name|NativeFunctionImpl
operator|.
name|NATIVE_PREFIX
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|String
name|getQueryStatement
parameter_list|()
block|{
return|return
name|queryStatement
return|;
block|}
specifier|public
name|void
name|setMatchesAllTypes
parameter_list|(
name|boolean
name|matchesAllTypes
parameter_list|)
block|{
name|this
operator|.
name|matchesAllTypes
operator|=
name|matchesAllTypes
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|QueryEngineSettings
name|getQueryEngineSettings
parameter_list|()
block|{
return|return
name|settings
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAccessible
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|PermissionProvider
name|permissionProvider
init|=
name|selector
operator|.
name|getQuery
argument_list|()
operator|.
name|getExecutionContext
argument_list|()
operator|.
name|getPermissionProvider
argument_list|()
decl_stmt|;
return|return
name|permissionProvider
operator|!=
literal|null
operator|&&
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|path
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
return|;
block|}
block|}
end_class

end_unit

