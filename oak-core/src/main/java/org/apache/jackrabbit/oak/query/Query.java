begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law  * or agreed to in writing, software distributed under the License is  * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
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
name|Arrays
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
name|HashMap
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
name|Root
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
name|namepath
operator|.
name|NamePathMapper
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
name|AstVisitorBase
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
name|BindVariableValueImpl
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
name|ChildNodeImpl
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
name|ChildNodeJoinConditionImpl
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
name|ColumnImpl
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
name|ComparisonImpl
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
name|ConstraintImpl
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
name|DescendantNodeImpl
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
name|DescendantNodeJoinConditionImpl
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
name|EquiJoinConditionImpl
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
name|FullTextSearchImpl
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
name|FullTextSearchScoreImpl
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
name|LengthImpl
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
name|LiteralImpl
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
name|LowerCaseImpl
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
name|NodeLocalNameImpl
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
name|NodeNameImpl
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
name|OrderingImpl
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
name|PropertyExistenceImpl
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
name|PropertyValueImpl
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
name|SameNodeImpl
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
name|SameNodeJoinConditionImpl
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
name|ast
operator|.
name|SourceImpl
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
name|UpperCaseImpl
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
comment|/**  * Represents a parsed query. Lifecycle: use the constructor to create a new  * object. Call init() to initialize the bind variable map. If the query is  * re-executed, a new instance is created.  */
end_comment

begin_class
specifier|public
class|class
name|Query
block|{
comment|/**      * The "jcr:path" pseudo-property.      */
comment|// TODO jcr:path isn't an official feature, support it?
specifier|public
specifier|static
specifier|final
name|String
name|JCR_PATH
init|=
literal|"jcr:path"
decl_stmt|;
comment|/**      * The "jcr:score" pseudo-property.      */
specifier|public
specifier|static
specifier|final
name|String
name|JCR_SCORE
init|=
literal|"jcr:score"
decl_stmt|;
specifier|final
name|SourceImpl
name|source
decl_stmt|;
specifier|final
name|ConstraintImpl
name|constraint
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|PropertyValue
argument_list|>
name|bindVariableMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|PropertyValue
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|selectorIndexes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|SelectorImpl
argument_list|>
name|selectors
init|=
operator|new
name|ArrayList
argument_list|<
name|SelectorImpl
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|QueryEngineImpl
name|queryEngine
decl_stmt|;
specifier|private
specifier|final
name|OrderingImpl
index|[]
name|orderings
decl_stmt|;
specifier|private
name|ColumnImpl
index|[]
name|columns
decl_stmt|;
specifier|private
name|boolean
name|explain
decl_stmt|,
name|measure
decl_stmt|;
specifier|private
name|long
name|limit
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
specifier|private
name|long
name|offset
decl_stmt|;
specifier|private
name|long
name|size
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|boolean
name|prepared
decl_stmt|;
specifier|private
name|Root
name|root
decl_stmt|;
specifier|private
name|NamePathMapper
name|namePathMapper
decl_stmt|;
name|Query
parameter_list|(
name|SourceImpl
name|source
parameter_list|,
name|ConstraintImpl
name|constraint
parameter_list|,
name|OrderingImpl
index|[]
name|orderings
parameter_list|,
name|ColumnImpl
index|[]
name|columns
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|constraint
operator|=
name|constraint
expr_stmt|;
name|this
operator|.
name|orderings
operator|=
name|orderings
expr_stmt|;
name|this
operator|.
name|columns
operator|=
name|columns
expr_stmt|;
block|}
specifier|public
name|void
name|init
parameter_list|()
block|{
specifier|final
name|Query
name|query
init|=
name|this
decl_stmt|;
operator|new
name|AstVisitorBase
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|BindVariableValueImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|bindVariableMap
operator|.
name|put
argument_list|(
name|node
operator|.
name|getBindVariableName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|ChildNodeImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|node
operator|.
name|bindSelector
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|ChildNodeJoinConditionImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|node
operator|.
name|bindSelector
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|ColumnImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|DescendantNodeImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|node
operator|.
name|bindSelector
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|DescendantNodeJoinConditionImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|node
operator|.
name|bindSelector
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|EquiJoinConditionImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|node
operator|.
name|bindSelector
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|FullTextSearchImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|node
operator|.
name|bindSelector
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|visit
argument_list|(
name|node
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|FullTextSearchScoreImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|node
operator|.
name|bindSelector
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|LiteralImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|NodeLocalNameImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|node
operator|.
name|bindSelector
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|NodeNameImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|node
operator|.
name|bindSelector
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|PropertyExistenceImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|node
operator|.
name|bindSelector
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|PropertyValueImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|node
operator|.
name|bindSelector
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|SameNodeImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|node
operator|.
name|bindSelector
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|SameNodeJoinConditionImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|node
operator|.
name|bindSelector
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|SelectorImpl
name|node
parameter_list|)
block|{
name|String
name|name
init|=
name|node
operator|.
name|getSelectorName
argument_list|()
decl_stmt|;
if|if
condition|(
name|selectorIndexes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|selectors
operator|.
name|size
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Two selectors with the same name: "
operator|+
name|name
argument_list|)
throw|;
block|}
name|selectors
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|LengthImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|visit
argument_list|(
name|node
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|UpperCaseImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|visit
argument_list|(
name|node
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|LowerCaseImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|visit
argument_list|(
name|node
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|ComparisonImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|visit
argument_list|(
name|node
argument_list|)
return|;
block|}
block|}
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|source
operator|.
name|setQueryConstraint
argument_list|(
name|constraint
argument_list|)
expr_stmt|;
name|source
operator|.
name|init
argument_list|(
name|this
argument_list|)
expr_stmt|;
for|for
control|(
name|ColumnImpl
name|column
range|:
name|columns
control|)
block|{
name|column
operator|.
name|bindSelector
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|ColumnImpl
index|[]
name|getColumns
parameter_list|()
block|{
return|return
name|columns
return|;
block|}
specifier|public
name|ConstraintImpl
name|getConstraint
parameter_list|()
block|{
return|return
name|constraint
return|;
block|}
specifier|public
name|OrderingImpl
index|[]
name|getOrderings
parameter_list|()
block|{
return|return
name|orderings
return|;
block|}
specifier|public
name|SourceImpl
name|getSource
parameter_list|()
block|{
return|return
name|source
return|;
block|}
name|void
name|bindValue
parameter_list|(
name|String
name|varName
parameter_list|,
name|PropertyValue
name|value
parameter_list|)
block|{
name|bindVariableMap
operator|.
name|put
argument_list|(
name|varName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setLimit
parameter_list|(
name|long
name|limit
parameter_list|)
block|{
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
block|}
specifier|public
name|void
name|setOffset
parameter_list|(
name|long
name|offset
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
specifier|public
name|void
name|setExplain
parameter_list|(
name|boolean
name|explain
parameter_list|)
block|{
name|this
operator|.
name|explain
operator|=
name|explain
expr_stmt|;
block|}
specifier|public
name|void
name|setMeasure
parameter_list|(
name|boolean
name|measure
parameter_list|)
block|{
name|this
operator|.
name|measure
operator|=
name|measure
expr_stmt|;
block|}
specifier|public
name|ResultImpl
name|executeQuery
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
return|return
operator|new
name|ResultImpl
argument_list|(
name|this
argument_list|,
name|root
argument_list|)
return|;
block|}
name|Iterator
argument_list|<
name|ResultRowImpl
argument_list|>
name|getRows
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
name|prepare
argument_list|()
expr_stmt|;
name|Iterator
argument_list|<
name|ResultRowImpl
argument_list|>
name|it
decl_stmt|;
if|if
condition|(
name|explain
condition|)
block|{
name|String
name|plan
init|=
name|source
operator|.
name|getPlan
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|columns
operator|=
operator|new
name|ColumnImpl
index|[]
block|{
operator|new
name|ColumnImpl
argument_list|(
literal|"explain"
argument_list|,
literal|"plan"
argument_list|,
literal|"plan"
argument_list|)
block|}
expr_stmt|;
name|ResultRowImpl
name|r
init|=
operator|new
name|ResultRowImpl
argument_list|(
name|this
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
operator|new
name|PropertyValue
index|[]
block|{
name|PropertyValues
operator|.
name|newString
argument_list|(
name|plan
argument_list|)
block|}
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|it
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|r
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|it
operator|=
operator|new
name|RowIterator
argument_list|(
name|root
argument_list|,
name|limit
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|long
name|resultCount
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|orderings
operator|!=
literal|null
condition|)
block|{
comment|// TODO "order by" is not necessary if the used index returns
comment|// rows in the same order
name|ArrayList
argument_list|<
name|ResultRowImpl
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|ResultRowImpl
argument_list|>
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
name|ResultRowImpl
name|r
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|resultCount
operator|=
name|size
operator|=
name|list
operator|.
name|size
argument_list|()
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|it
operator|=
name|list
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|measure
condition|)
block|{
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|resultCount
operator|++
expr_stmt|;
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|measure
condition|)
block|{
name|columns
operator|=
operator|new
name|ColumnImpl
index|[]
block|{
operator|new
name|ColumnImpl
argument_list|(
literal|"measure"
argument_list|,
literal|"selector"
argument_list|,
literal|"selector"
argument_list|)
block|,
operator|new
name|ColumnImpl
argument_list|(
literal|"measure"
argument_list|,
literal|"scanCount"
argument_list|,
literal|"scanCount"
argument_list|)
block|}
expr_stmt|;
name|ArrayList
argument_list|<
name|ResultRowImpl
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|ResultRowImpl
argument_list|>
argument_list|()
decl_stmt|;
name|ResultRowImpl
name|r
init|=
operator|new
name|ResultRowImpl
argument_list|(
name|this
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
operator|new
name|PropertyValue
index|[]
block|{
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"query"
argument_list|)
block|,
name|PropertyValues
operator|.
name|newLong
argument_list|(
name|resultCount
argument_list|)
block|}
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
for|for
control|(
name|SelectorImpl
name|selector
range|:
name|selectors
control|)
block|{
name|r
operator|=
operator|new
name|ResultRowImpl
argument_list|(
name|this
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
operator|new
name|PropertyValue
index|[]
block|{
name|PropertyValues
operator|.
name|newString
argument_list|(
name|selector
operator|.
name|getSelectorName
argument_list|()
argument_list|)
block|,
name|PropertyValues
operator|.
name|newLong
argument_list|(
name|selector
operator|.
name|getScanCount
argument_list|()
argument_list|)
block|,                                 }
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|it
operator|=
name|list
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|it
return|;
block|}
specifier|public
name|int
name|compareRows
parameter_list|(
name|PropertyValue
index|[]
name|orderValues
parameter_list|,
name|PropertyValue
index|[]
name|orderValues2
parameter_list|)
block|{
name|int
name|comp
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|size
init|=
name|orderings
operator|.
name|length
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|PropertyValue
name|a
init|=
name|orderValues
index|[
name|i
index|]
decl_stmt|;
name|PropertyValue
name|b
init|=
name|orderValues2
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|a
operator|==
literal|null
operator|||
name|b
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|a
operator|==
name|b
condition|)
block|{
name|comp
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|a
operator|==
literal|null
condition|)
block|{
comment|// TODO order by: nulls first (it looks like), or low?
name|comp
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|comp
operator|=
literal|1
expr_stmt|;
block|}
block|}
else|else
block|{
name|comp
operator|=
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|comp
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|orderings
index|[
name|i
index|]
operator|.
name|isDescending
argument_list|()
condition|)
block|{
name|comp
operator|=
operator|-
name|comp
expr_stmt|;
block|}
break|break;
block|}
block|}
return|return
name|comp
return|;
block|}
name|void
name|prepare
parameter_list|()
block|{
if|if
condition|(
name|prepared
condition|)
block|{
return|return;
block|}
name|prepared
operator|=
literal|true
expr_stmt|;
name|source
operator|.
name|prepare
argument_list|()
expr_stmt|;
block|}
comment|/**      * An iterator over result rows.      */
class|class
name|RowIterator
implements|implements
name|Iterator
argument_list|<
name|ResultRowImpl
argument_list|>
block|{
specifier|private
specifier|final
name|NodeState
name|root
decl_stmt|;
specifier|private
name|ResultRowImpl
name|current
decl_stmt|;
specifier|private
name|boolean
name|started
decl_stmt|,
name|end
decl_stmt|;
specifier|private
name|long
name|limit
decl_stmt|,
name|offset
decl_stmt|,
name|rowIndex
decl_stmt|;
name|RowIterator
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|long
name|limit
parameter_list|,
name|long
name|offset
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
name|limit
operator|=
name|limit
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
specifier|private
name|void
name|fetchNext
parameter_list|()
block|{
if|if
condition|(
name|end
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|rowIndex
operator|>=
name|limit
condition|)
block|{
name|end
operator|=
literal|true
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|started
condition|)
block|{
name|source
operator|.
name|execute
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|started
operator|=
literal|true
expr_stmt|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|source
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
name|constraint
operator|==
literal|null
operator|||
name|constraint
operator|.
name|evaluate
argument_list|()
condition|)
block|{
if|if
condition|(
name|offset
operator|>
literal|0
condition|)
block|{
name|offset
operator|--
expr_stmt|;
continue|continue;
block|}
name|current
operator|=
name|currentRow
argument_list|()
expr_stmt|;
name|rowIndex
operator|++
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
name|end
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|end
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
name|fetchNext
argument_list|()
expr_stmt|;
block|}
return|return
operator|!
name|end
return|;
block|}
annotation|@
name|Override
specifier|public
name|ResultRowImpl
name|next
parameter_list|()
block|{
if|if
condition|(
name|end
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
name|fetchNext
argument_list|()
expr_stmt|;
block|}
name|ResultRowImpl
name|r
init|=
name|current
decl_stmt|;
name|current
operator|=
literal|null
expr_stmt|;
return|return
name|r
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
name|ResultRowImpl
name|currentRow
parameter_list|()
block|{
name|int
name|selectorCount
init|=
name|selectors
operator|.
name|size
argument_list|()
decl_stmt|;
name|String
index|[]
name|paths
init|=
operator|new
name|String
index|[
name|selectorCount
index|]
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
name|selectorCount
condition|;
name|i
operator|++
control|)
block|{
name|SelectorImpl
name|s
init|=
name|selectors
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|paths
index|[
name|i
index|]
operator|=
name|s
operator|.
name|currentPath
argument_list|()
expr_stmt|;
block|}
name|int
name|columnCount
init|=
name|columns
operator|.
name|length
decl_stmt|;
name|PropertyValue
index|[]
name|values
init|=
operator|new
name|PropertyValue
index|[
name|columnCount
index|]
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
name|columnCount
condition|;
name|i
operator|++
control|)
block|{
name|ColumnImpl
name|c
init|=
name|columns
index|[
name|i
index|]
decl_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|PropertyValues
operator|.
name|create
argument_list|(
name|c
operator|.
name|currentProperty
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PropertyValue
index|[]
name|orderValues
decl_stmt|;
if|if
condition|(
name|orderings
operator|==
literal|null
condition|)
block|{
name|orderValues
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|int
name|size
init|=
name|orderings
operator|.
name|length
decl_stmt|;
name|orderValues
operator|=
operator|new
name|PropertyValue
index|[
name|size
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|orderValues
index|[
name|i
index|]
operator|=
name|PropertyValues
operator|.
name|create
argument_list|(
name|orderings
index|[
name|i
index|]
operator|.
name|getOperand
argument_list|()
operator|.
name|currentProperty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ResultRowImpl
argument_list|(
name|this
argument_list|,
name|paths
argument_list|,
name|values
argument_list|,
name|orderValues
argument_list|)
return|;
block|}
specifier|public
name|int
name|getSelectorIndex
parameter_list|(
name|String
name|selectorName
parameter_list|)
block|{
name|Integer
name|index
init|=
name|selectorIndexes
operator|.
name|get
argument_list|(
name|selectorName
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown selector: "
operator|+
name|selectorName
argument_list|)
throw|;
block|}
return|return
name|index
return|;
block|}
specifier|public
name|int
name|getColumnIndex
parameter_list|(
name|String
name|columnName
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|size
init|=
name|columns
operator|.
name|length
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|ColumnImpl
name|c
init|=
name|columns
index|[
name|i
index|]
decl_stmt|;
name|String
name|cn
init|=
name|c
operator|.
name|getColumnName
argument_list|()
decl_stmt|;
if|if
condition|(
name|cn
operator|!=
literal|null
operator|&&
name|cn
operator|.
name|equals
argument_list|(
name|columnName
argument_list|)
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Column not found: "
operator|+
name|columnName
argument_list|)
throw|;
block|}
specifier|public
name|PropertyValue
name|getBindVariableValue
parameter_list|(
name|String
name|bindVariableName
parameter_list|)
block|{
name|PropertyValue
name|v
init|=
name|bindVariableMap
operator|.
name|get
argument_list|(
name|bindVariableName
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bind variable value not set: "
operator|+
name|bindVariableName
argument_list|)
throw|;
block|}
return|return
name|v
return|;
block|}
specifier|public
name|List
argument_list|<
name|SelectorImpl
argument_list|>
name|getSelectors
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|selectors
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getBindVariableNames
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|bindVariableMap
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|setQueryEngine
parameter_list|(
name|QueryEngineImpl
name|queryEngine
parameter_list|)
block|{
name|this
operator|.
name|queryEngine
operator|=
name|queryEngine
expr_stmt|;
block|}
specifier|public
name|QueryIndex
name|getBestIndex
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
return|return
name|queryEngine
operator|.
name|getBestIndex
argument_list|(
name|filter
argument_list|)
return|;
block|}
specifier|public
name|void
name|setRoot
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
specifier|public
name|void
name|setNamePathMapper
parameter_list|(
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
block|}
specifier|public
name|NamePathMapper
name|getNamePathMapper
parameter_list|()
block|{
return|return
name|namePathMapper
return|;
block|}
specifier|public
name|Tree
name|getTree
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|path
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
literal|"select "
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ColumnImpl
name|c
range|:
name|columns
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
literal|0
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
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|" from "
argument_list|)
operator|.
name|append
argument_list|(
name|source
argument_list|)
expr_stmt|;
if|if
condition|(
name|constraint
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
name|constraint
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|orderings
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|" order by "
argument_list|)
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|OrderingImpl
name|o
range|:
name|orderings
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
literal|0
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
name|buff
operator|.
name|append
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
block|}
end_class

end_unit

