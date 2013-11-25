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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Iterators
import|;
end_import

begin_comment
comment|/**  * Represents a union query.  */
end_comment

begin_class
specifier|public
class|class
name|UnionQueryImpl
implements|implements
name|Query
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
name|QueryImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|unionAll
decl_stmt|;
specifier|private
specifier|final
name|Query
name|left
decl_stmt|,
name|right
decl_stmt|;
specifier|private
name|ColumnImpl
index|[]
name|columns
decl_stmt|;
specifier|private
name|OrderingImpl
index|[]
name|orderings
decl_stmt|;
specifier|private
name|boolean
name|explain
decl_stmt|;
specifier|private
name|boolean
name|measure
decl_stmt|;
specifier|private
name|long
name|limit
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
name|UnionQueryImpl
parameter_list|(
name|boolean
name|unionAll
parameter_list|,
name|Query
name|left
parameter_list|,
name|Query
name|right
parameter_list|)
block|{
name|this
operator|.
name|unionAll
operator|=
name|unionAll
expr_stmt|;
name|this
operator|.
name|left
operator|=
name|left
expr_stmt|;
name|this
operator|.
name|right
operator|=
name|right
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setExecutionContext
parameter_list|(
name|ExecutionContext
name|context
parameter_list|)
block|{
name|left
operator|.
name|setExecutionContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|right
operator|.
name|setExecutionContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOrderings
parameter_list|(
name|OrderingImpl
index|[]
name|orderings
parameter_list|)
block|{
if|if
condition|(
name|orderings
operator|==
literal|null
condition|)
block|{
name|left
operator|.
name|setOrderings
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|right
operator|.
name|setOrderings
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return;
block|}
name|OrderingImpl
index|[]
name|l
init|=
operator|new
name|OrderingImpl
index|[
name|orderings
operator|.
name|length
index|]
decl_stmt|;
name|OrderingImpl
index|[]
name|r
init|=
operator|new
name|OrderingImpl
index|[
name|orderings
operator|.
name|length
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
name|orderings
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|OrderingImpl
name|o
init|=
name|orderings
index|[
name|i
index|]
decl_stmt|;
name|l
index|[
name|i
index|]
operator|=
name|o
operator|.
name|createCopy
argument_list|()
expr_stmt|;
name|r
index|[
name|i
index|]
operator|=
name|o
operator|.
name|createCopy
argument_list|()
expr_stmt|;
block|}
name|left
operator|.
name|setOrderings
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|right
operator|.
name|setOrderings
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|this
operator|.
name|orderings
operator|=
name|orderings
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setNamePathMapper
parameter_list|(
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|left
operator|.
name|setNamePathMapper
argument_list|(
name|namePathMapper
argument_list|)
expr_stmt|;
name|right
operator|.
name|setNamePathMapper
argument_list|(
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|void
name|bindValue
parameter_list|(
name|String
name|key
parameter_list|,
name|PropertyValue
name|value
parameter_list|)
block|{
name|left
operator|.
name|bindValue
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|right
operator|.
name|bindValue
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setTraversalFallback
parameter_list|(
name|boolean
name|traversal
parameter_list|)
block|{
name|left
operator|.
name|setTraversalFallback
argument_list|(
name|traversal
argument_list|)
expr_stmt|;
name|right
operator|.
name|setTraversalFallback
argument_list|(
name|traversal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|prepare
parameter_list|()
block|{
name|left
operator|.
name|prepare
argument_list|()
expr_stmt|;
name|right
operator|.
name|prepare
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getBindVariableNames
parameter_list|()
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|left
operator|.
name|getBindVariableNames
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|right
operator|.
name|getBindVariableNames
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|set
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ColumnImpl
index|[]
name|getColumns
parameter_list|()
block|{
return|return
name|left
operator|.
name|getColumns
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getSelectorNames
parameter_list|()
block|{
return|return
name|left
operator|.
name|getSelectorNames
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getSelectorIndex
parameter_list|(
name|String
name|selectorName
parameter_list|)
block|{
return|return
name|left
operator|.
name|getSelectorIndex
argument_list|(
name|selectorName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|void
name|setMeasure
parameter_list|(
name|boolean
name|measure
parameter_list|)
block|{
name|left
operator|.
name|setMeasure
argument_list|(
name|measure
argument_list|)
expr_stmt|;
name|right
operator|.
name|setMeasure
argument_list|(
name|measure
argument_list|)
expr_stmt|;
name|this
operator|.
name|measure
operator|=
name|measure
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|()
block|{
name|left
operator|.
name|init
argument_list|()
expr_stmt|;
name|right
operator|.
name|init
argument_list|()
expr_stmt|;
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
name|left
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|" union "
argument_list|)
expr_stmt|;
if|if
condition|(
name|unionAll
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"all "
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|right
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|int
name|i
init|=
literal|0
decl_stmt|;
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
annotation|@
name|Override
specifier|public
name|Result
name|executeQuery
parameter_list|()
block|{
return|return
operator|new
name|ResultImpl
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPlan
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
name|left
operator|.
name|getPlan
argument_list|()
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|" union "
argument_list|)
expr_stmt|;
if|if
condition|(
name|unionAll
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"all "
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|right
operator|.
name|getPlan
argument_list|()
argument_list|)
expr_stmt|;
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
name|Tree
name|getTree
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|left
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
name|int
name|getColumnIndex
parameter_list|(
name|String
name|columnName
parameter_list|)
block|{
if|if
condition|(
name|columns
operator|==
literal|null
condition|)
block|{
name|columns
operator|=
name|left
operator|.
name|getColumns
argument_list|()
expr_stmt|;
block|}
return|return
name|QueryImpl
operator|.
name|getColumnIndex
argument_list|(
name|columns
argument_list|,
name|columnName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ResultRowImpl
argument_list|>
name|getRows
parameter_list|()
block|{
name|prepare
argument_list|()
expr_stmt|;
if|if
condition|(
name|explain
condition|)
block|{
name|String
name|plan
init|=
name|getPlan
argument_list|()
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
name|Tree
operator|.
name|EMPTY_ARRAY
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
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|r
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"query execute union"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"query plan {}"
argument_list|,
name|getPlan
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|ResultRowImpl
argument_list|>
name|it
init|=
name|Iterators
operator|.
name|concat
argument_list|(
name|left
operator|.
name|getRows
argument_list|()
argument_list|,
name|right
operator|.
name|getRows
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|measure
condition|)
block|{
comment|// both queries measure themselves
return|return
name|it
return|;
block|}
name|boolean
name|distinct
init|=
operator|!
name|unionAll
decl_stmt|;
name|Comparator
argument_list|<
name|ResultRowImpl
argument_list|>
name|orderBy
init|=
name|ResultRowImpl
operator|.
name|getComparator
argument_list|(
name|orderings
argument_list|)
decl_stmt|;
name|it
operator|=
name|FilterIterators
operator|.
name|newCombinedFilter
argument_list|(
name|it
argument_list|,
name|distinct
argument_list|,
name|limit
argument_list|,
name|offset
argument_list|,
name|orderBy
argument_list|)
expr_stmt|;
return|return
name|it
return|;
block|}
block|}
end_class

end_unit

