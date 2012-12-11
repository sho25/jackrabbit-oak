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
name|STRINGS
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
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeManager
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
name|JcrConstants
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
name|plugins
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
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
name|nodetype
operator|.
name|ReadOnlyNodeTypeManager
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
name|ImmutableSet
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
name|String
name|nodeTypeName
decl_stmt|,
name|selectorName
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
comment|/**      * Iterable over selected node type and its subtypes      */
specifier|private
name|Iterable
argument_list|<
name|NodeType
argument_list|>
name|nodeTypes
decl_stmt|;
comment|/**      * The selector condition can be evaluated when the given selector is      * evaluated. For example, for the query      * "select * from nt:base a inner join nt:base b where a.x = 1 and b.y = 2",      * the condition "a.x = 1" can be evaluated when evaluating selector a. The      * other part of the condition can't be evaluated until b is available.      */
specifier|private
name|ConstraintImpl
name|selectorCondition
decl_stmt|;
specifier|public
name|SelectorImpl
parameter_list|(
name|String
name|nodeTypeName
parameter_list|,
name|String
name|selectorName
parameter_list|)
block|{
name|this
operator|.
name|nodeTypeName
operator|=
name|nodeTypeName
expr_stmt|;
name|this
operator|.
name|selectorName
operator|=
name|selectorName
expr_stmt|;
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
name|index
operator|=
name|query
operator|.
name|getBestIndex
argument_list|(
name|createFilter
argument_list|()
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
argument_list|()
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
argument_list|()
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
specifier|private
name|Filter
name|createFilter
parameter_list|()
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
name|validateNodeType
argument_list|(
name|nodeTypeName
argument_list|)
expr_stmt|;
name|f
operator|.
name|setNodeType
argument_list|(
name|nodeTypeName
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
specifier|private
name|void
name|validateNodeType
parameter_list|(
name|String
name|nodeType
parameter_list|)
block|{
comment|// this looks a bit weird, but it should be correct - the code
comment|// assumes that paths and node type names have the same format
comment|// restrictions (characters such as "[" are not allowed and so on)
name|query
operator|.
name|validatePath
argument_list|(
name|nodeType
argument_list|)
expr_stmt|;
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
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|nodeTypeName
operator|!=
literal|null
operator|&&
operator|!
name|nodeTypeName
operator|.
name|equals
argument_list|(
name|JcrConstants
operator|.
name|NT_BASE
argument_list|)
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
name|Set
argument_list|<
name|String
argument_list|>
name|primary
init|=
name|getStrings
argument_list|(
name|tree
argument_list|,
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|mixins
init|=
name|getStrings
argument_list|(
name|tree
argument_list|,
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|NodeType
name|type
range|:
name|getNodeTypes
argument_list|()
control|)
block|{
if|if
condition|(
name|evaluateTypeMatch
argument_list|(
name|type
argument_list|,
name|primary
argument_list|,
name|mixins
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to evaluate node type constraints"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getStrings
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|String
argument_list|>
name|builder
init|=
name|ImmutableSet
operator|.
name|builder
argument_list|()
decl_stmt|;
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|boolean
name|evaluateTypeMatch
parameter_list|(
name|NodeType
name|type
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|primary
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|mixins
parameter_list|)
block|{
name|String
name|name
init|=
name|type
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|isMixin
argument_list|()
condition|)
block|{
return|return
name|mixins
operator|.
name|contains
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|primary
operator|.
name|contains
argument_list|(
name|name
argument_list|)
return|;
block|}
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
name|p
init|=
name|currentPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|local
init|=
name|getLocalPath
argument_list|(
name|p
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
return|return
name|t
operator|==
literal|null
condition|?
literal|null
else|:
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
specifier|private
name|Iterable
argument_list|<
name|NodeType
argument_list|>
name|getNodeTypes
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|nodeTypes
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|NodeType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeType
argument_list|>
argument_list|()
decl_stmt|;
name|NodeTypeManager
name|manager
init|=
operator|new
name|ReadOnlyNodeTypeManager
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|protected
name|Tree
name|getTypes
parameter_list|()
block|{
return|return
name|getTree
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|NodeType
name|type
init|=
name|manager
operator|.
name|getNodeType
argument_list|(
name|nodeTypeName
argument_list|)
decl_stmt|;
name|types
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|NodeTypeIterator
name|it
init|=
name|type
operator|.
name|getSubtypes
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|types
operator|.
name|add
argument_list|(
name|it
operator|.
name|nextNodeType
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|nodeTypes
operator|=
name|types
expr_stmt|;
block|}
return|return
name|nodeTypes
return|;
block|}
block|}
end_class

end_unit

