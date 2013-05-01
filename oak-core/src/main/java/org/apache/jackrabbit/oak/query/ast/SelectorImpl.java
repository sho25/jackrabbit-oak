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
name|ast
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
name|Preconditions
operator|.
name|checkNotNull
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_ISMIXIN
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
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
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
name|JcrConstants
operator|.
name|JCR_NODETYPENAME
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
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
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
name|JcrConstants
operator|.
name|NT_BASE
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
name|api
operator|.
name|Type
operator|.
name|NAME
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
name|api
operator|.
name|Type
operator|.
name|NAMES
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|OAK_MIXIN_SUBTYPES
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|OAK_NAMED_SINGLE_VALUED_PROPERTIES
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|OAK_PRIMARY_SUBTYPES
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|OAK_SUPERTYPES
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
name|Tree
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
name|Query
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
name|index
operator|.
name|FilterImpl
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
name|NodeState
import|;
end_import

begin_comment
comment|/**  * A selector within a query.  */
end_comment

begin_class
specifier|public
class|class
name|SelectorImpl
extends|extends
name|SourceImpl
block|{
comment|// TODO possibly support using multiple indexes (using index intersection / index merge)
specifier|protected
name|QueryIndex
name|index
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|nodeType
decl_stmt|;
specifier|private
specifier|final
name|String
name|selectorName
decl_stmt|;
specifier|private
specifier|final
name|String
name|nodeTypeName
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|matchesAllTypes
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|supertypes
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|primaryTypes
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|mixinTypes
decl_stmt|;
specifier|private
name|Cursor
name|cursor
decl_stmt|;
specifier|private
name|IndexRow
name|currentRow
decl_stmt|;
specifier|private
name|int
name|scanCount
decl_stmt|;
comment|/**      * The selector condition can be evaluated when the given selector is      * evaluated. For example, for the query      * "select * from nt:base a inner join nt:base b where a.x = 1 and b.y = 2",      * the condition "a.x = 1" can be evaluated when evaluating selector a. The      * other part of the condition can't be evaluated until b is available.      */
specifier|private
name|ConstraintImpl
name|selectorCondition
decl_stmt|;
specifier|public
name|SelectorImpl
parameter_list|(
name|NodeState
name|nodeType
parameter_list|,
name|String
name|selectorName
parameter_list|)
block|{
name|this
operator|.
name|nodeType
operator|=
name|checkNotNull
argument_list|(
name|nodeType
argument_list|)
expr_stmt|;
name|this
operator|.
name|selectorName
operator|=
name|checkNotNull
argument_list|(
name|selectorName
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeTypeName
operator|=
name|nodeType
operator|.
name|getName
argument_list|(
name|JCR_NODETYPENAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchesAllTypes
operator|=
name|NT_BASE
operator|.
name|equals
argument_list|(
name|nodeTypeName
argument_list|)
expr_stmt|;
name|this
operator|.
name|supertypes
operator|=
name|newHashSet
argument_list|(
name|nodeType
operator|.
name|getNames
argument_list|(
name|OAK_SUPERTYPES
argument_list|)
argument_list|)
expr_stmt|;
name|supertypes
operator|.
name|add
argument_list|(
name|nodeTypeName
argument_list|)
expr_stmt|;
name|this
operator|.
name|primaryTypes
operator|=
name|newHashSet
argument_list|(
name|nodeType
operator|.
name|getNames
argument_list|(
name|OAK_PRIMARY_SUBTYPES
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|mixinTypes
operator|=
name|newHashSet
argument_list|(
name|nodeType
operator|.
name|getNames
argument_list|(
name|OAK_MIXIN_SUBTYPES
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodeType
operator|.
name|getBoolean
argument_list|(
name|JCR_ISMIXIN
argument_list|)
condition|)
block|{
name|mixinTypes
operator|.
name|add
argument_list|(
name|nodeTypeName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|primaryTypes
operator|.
name|add
argument_list|(
name|nodeTypeName
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getSelectorName
parameter_list|()
block|{
return|return
name|selectorName
return|;
block|}
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
name|supertypes
return|;
block|}
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
name|primaryTypes
return|;
block|}
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
name|mixinTypes
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getWildcardColumns
parameter_list|()
block|{
return|return
name|nodeType
operator|.
name|getNames
argument_list|(
name|OAK_NAMED_SINGLE_VALUED_PROPERTIES
argument_list|)
return|;
block|}
annotation|@
name|Override
name|boolean
name|accept
parameter_list|(
name|AstVisitor
name|v
parameter_list|)
block|{
return|return
name|v
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|nodeTypeName
init|=
name|nodeType
operator|.
name|getName
argument_list|(
name|JCR_NODETYPENAME
argument_list|)
decl_stmt|;
return|return
name|quote
argument_list|(
name|nodeTypeName
argument_list|)
operator|+
literal|" as "
operator|+
name|quote
argument_list|(
name|selectorName
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isPrepared
parameter_list|()
block|{
return|return
name|index
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|prepare
parameter_list|()
block|{
if|if
condition|(
name|queryConstraint
operator|!=
literal|null
condition|)
block|{
name|queryConstraint
operator|.
name|restrictPushDown
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|outerJoinLeftHandSide
operator|&&
operator|!
name|outerJoinRightHandSide
condition|)
block|{
for|for
control|(
name|JoinConditionImpl
name|c
range|:
name|allJoinConditions
control|)
block|{
name|c
operator|.
name|restrictPushDown
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
name|index
operator|=
name|query
operator|.
name|getBestIndex
argument_list|(
name|createFilter
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|NodeState
name|rootState
parameter_list|)
block|{
name|cursor
operator|=
name|index
operator|.
name|query
argument_list|(
name|createFilter
argument_list|(
literal|false
argument_list|)
argument_list|,
name|rootState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPlan
parameter_list|(
name|NodeState
name|rootState
parameter_list|)
block|{
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
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|" /* "
argument_list|)
operator|.
name|append
argument_list|(
name|index
operator|.
name|getPlan
argument_list|(
name|createFilter
argument_list|(
literal|true
argument_list|)
argument_list|,
name|rootState
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|selectorCondition
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|" where "
argument_list|)
operator|.
name|append
argument_list|(
name|selectorCondition
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|" */"
argument_list|)
expr_stmt|;
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Create the filter condition for planning or execution.      *       * @param preparing whether a filter for the prepare phase should be made       * @return the filter      */
specifier|private
name|Filter
name|createFilter
parameter_list|(
name|boolean
name|preparing
parameter_list|)
block|{
name|FilterImpl
name|f
init|=
operator|new
name|FilterImpl
argument_list|(
name|this
argument_list|,
name|query
operator|.
name|getStatement
argument_list|()
argument_list|)
decl_stmt|;
name|f
operator|.
name|setPreparing
argument_list|(
name|preparing
argument_list|)
expr_stmt|;
if|if
condition|(
name|joinCondition
operator|!=
literal|null
condition|)
block|{
name|joinCondition
operator|.
name|restrict
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
comment|// all conditions can be pushed to the selectors -
comment|// except in some cases to "outer joined" selectors,
comment|// but the exceptions are handled in the condition
comment|// itself.
comment|// An example where it *is* a problem:
comment|//  "select * from a left outer join b on a.x = b.y
comment|// where b.y is null" - in this case the selector b
comment|// must not use an index condition on "y is null"
comment|// (".. is null" must be written as "not .. is not null").
if|if
condition|(
name|queryConstraint
operator|!=
literal|null
condition|)
block|{
name|queryConstraint
operator|.
name|restrict
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
return|return
name|f
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|next
parameter_list|()
block|{
while|while
condition|(
name|cursor
operator|!=
literal|null
operator|&&
name|cursor
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|scanCount
operator|++
expr_stmt|;
name|currentRow
operator|=
name|cursor
operator|.
name|next
argument_list|()
expr_stmt|;
name|Tree
name|tree
init|=
name|getTree
argument_list|(
name|currentRow
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|tree
operator|==
literal|null
operator|||
operator|!
name|tree
operator|.
name|exists
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|matchesAllTypes
operator|&&
operator|!
name|evaluateTypeMatch
argument_list|(
name|tree
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|selectorCondition
operator|!=
literal|null
operator|&&
operator|!
name|selectorCondition
operator|.
name|evaluate
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|joinCondition
operator|!=
literal|null
operator|&&
operator|!
name|joinCondition
operator|.
name|evaluate
argument_list|()
condition|)
block|{
continue|continue;
block|}
return|return
literal|true
return|;
block|}
name|cursor
operator|=
literal|null
expr_stmt|;
name|currentRow
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
specifier|private
name|boolean
name|evaluateTypeMatch
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
name|PropertyState
name|primary
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|primary
operator|!=
literal|null
operator|&&
name|primary
operator|.
name|getType
argument_list|()
operator|==
name|NAME
condition|)
block|{
name|String
name|name
init|=
name|primary
operator|.
name|getValue
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|primaryTypes
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
name|PropertyState
name|mixins
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|)
decl_stmt|;
if|if
condition|(
name|mixins
operator|!=
literal|null
operator|&&
name|mixins
operator|.
name|getType
argument_list|()
operator|==
name|NAMES
condition|)
block|{
for|for
control|(
name|String
name|name
range|:
name|mixins
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
control|)
block|{
if|if
condition|(
name|mixinTypes
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
comment|// no matches found
block|}
comment|/**      * Get the current absolute path (including workspace name)      *      * @return the path      */
specifier|public
name|String
name|currentPath
parameter_list|()
block|{
return|return
name|cursor
operator|==
literal|null
condition|?
literal|null
else|:
name|currentRow
operator|.
name|getPath
argument_list|()
return|;
block|}
specifier|public
name|PropertyValue
name|currentProperty
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
name|boolean
name|relative
init|=
name|propertyName
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|>=
literal|0
decl_stmt|;
if|if
condition|(
name|cursor
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|IndexRow
name|r
init|=
name|currentRow
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// TODO support pseudo-properties such as jcr:score using
comment|// r.getValue(columnName)
name|String
name|path
init|=
name|r
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Tree
name|t
init|=
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|relative
condition|)
block|{
for|for
control|(
name|String
name|p
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|propertyName
argument_list|)
argument_list|)
control|)
block|{
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|p
operator|.
name|equals
argument_list|(
literal|".."
argument_list|)
condition|)
block|{
name|t
operator|=
name|t
operator|.
name|isRoot
argument_list|()
condition|?
literal|null
else|:
name|t
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|p
operator|.
name|equals
argument_list|(
literal|"."
argument_list|)
condition|)
block|{
comment|// same node
block|}
else|else
block|{
name|t
operator|=
name|t
operator|.
name|getChild
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
name|propertyName
operator|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|t
operator|==
literal|null
operator|||
operator|!
name|t
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|propertyName
operator|.
name|equals
argument_list|(
name|Query
operator|.
name|JCR_PATH
argument_list|)
condition|)
block|{
name|String
name|local
init|=
name|getLocalPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|local
operator|==
literal|null
condition|)
block|{
comment|// not a local path
return|return
literal|null
return|;
block|}
return|return
name|PropertyValues
operator|.
name|newString
argument_list|(
name|local
argument_list|)
return|;
block|}
return|return
name|PropertyValues
operator|.
name|create
argument_list|(
name|t
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
comment|// nothing to do
block|}
annotation|@
name|Override
specifier|public
name|SelectorImpl
name|getSelector
parameter_list|(
name|String
name|selectorName
parameter_list|)
block|{
if|if
condition|(
name|selectorName
operator|.
name|equals
argument_list|(
name|this
operator|.
name|selectorName
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|long
name|getScanCount
parameter_list|()
block|{
return|return
name|scanCount
return|;
block|}
specifier|public
name|void
name|restrictSelector
parameter_list|(
name|ConstraintImpl
name|constraint
parameter_list|)
block|{
if|if
condition|(
name|selectorCondition
operator|==
literal|null
condition|)
block|{
name|selectorCondition
operator|=
name|constraint
expr_stmt|;
block|}
else|else
block|{
name|selectorCondition
operator|=
operator|new
name|AndImpl
argument_list|(
name|selectorCondition
argument_list|,
name|constraint
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

