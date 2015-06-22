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
name|java
operator|.
name|util
operator|.
name|Map
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
name|Maps
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
name|query
operator|.
name|QueryImpl
operator|.
name|MeasuringIterator
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
specifier|final
name|QueryEngineSettings
name|settings
decl_stmt|;
specifier|private
name|boolean
name|isInternal
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
parameter_list|,
name|QueryEngineSettings
name|settings
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
name|this
operator|.
name|settings
operator|=
name|settings
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
name|left
operator|.
name|setLimit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
name|right
operator|.
name|setLimit
argument_list|(
name|limit
argument_list|)
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
name|setTraversalEnabled
parameter_list|(
name|boolean
name|traversal
parameter_list|)
block|{
name|left
operator|.
name|setTraversalEnabled
argument_list|(
name|traversal
argument_list|)
expr_stmt|;
name|right
operator|.
name|setTraversalEnabled
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
name|double
name|getEstimatedCost
parameter_list|()
block|{
return|return
name|left
operator|.
name|getEstimatedCost
argument_list|()
operator|+
name|right
operator|.
name|getEstimatedCost
argument_list|()
return|;
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
if|if
condition|(
name|columns
operator|!=
literal|null
condition|)
block|{
return|return
name|columns
return|;
block|}
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
comment|// Note: for "unionAll == false", overlapping entries are counted twice
comment|// (this can result in a larger reported size, but it is not a security problem)
comment|// ensure the queries are both executed, otherwise the cursor is not set,
comment|// and so the size would be -1
name|left
operator|.
name|executeQuery
argument_list|()
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
expr_stmt|;
name|right
operator|.
name|executeQuery
argument_list|()
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
expr_stmt|;
name|long
name|a
init|=
name|left
operator|.
name|getSize
argument_list|(
name|precision
argument_list|,
name|max
argument_list|)
decl_stmt|;
name|long
name|b
init|=
name|right
operator|.
name|getSize
argument_list|(
name|precision
argument_list|,
name|max
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|<
literal|0
operator|||
name|b
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|long
name|total
init|=
name|QueryImpl
operator|.
name|saturatedAdd
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
decl_stmt|;
return|return
name|Math
operator|.
name|min
argument_list|(
name|limit
argument_list|,
name|total
argument_list|)
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
name|boolean
name|isMeasureOrExplainEnabled
parameter_list|()
block|{
return|return
name|explain
operator|||
name|measure
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
if|if
condition|(
name|isInternal
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"query union plan {}"
argument_list|,
name|getPlan
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"query union plan {}"
argument_list|,
name|getPlan
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|Iterator
argument_list|<
name|ResultRowImpl
argument_list|>
name|it
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|ResultRowImpl
argument_list|>
name|leftRows
init|=
name|left
operator|.
name|getRows
argument_list|()
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|ResultRowImpl
argument_list|>
name|rightRows
init|=
name|right
operator|.
name|getRows
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|ResultRowImpl
argument_list|>
name|leftIter
init|=
name|leftRows
decl_stmt|;
name|Iterator
argument_list|<
name|ResultRowImpl
argument_list|>
name|rightIter
init|=
name|rightRows
decl_stmt|;
comment|// if measure retrieve the backing delegate iterator instead
if|if
condition|(
name|measure
condition|)
block|{
name|leftIter
operator|=
operator|(
operator|(
name|MeasuringIterator
operator|)
name|leftRows
operator|)
operator|.
name|getDelegate
argument_list|()
expr_stmt|;
name|rightIter
operator|=
operator|(
operator|(
name|MeasuringIterator
operator|)
name|rightRows
operator|)
operator|.
name|getDelegate
argument_list|()
expr_stmt|;
block|}
comment|// Since sorted by index use a merge iterator
if|if
condition|(
name|isSortedByIndex
argument_list|()
condition|)
block|{
name|it
operator|=
name|FilterIterators
operator|.
name|newCombinedFilter
argument_list|(
name|Iterators
operator|.
name|mergeSorted
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|leftIter
argument_list|,
name|rightIter
argument_list|)
argument_list|,
name|orderBy
argument_list|)
argument_list|,
name|distinct
argument_list|,
name|limit
argument_list|,
name|offset
argument_list|,
literal|null
argument_list|,
name|settings
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|it
operator|=
name|FilterIterators
operator|.
name|newCombinedFilter
argument_list|(
name|Iterators
operator|.
name|concat
argument_list|(
name|leftIter
argument_list|,
name|rightIter
argument_list|)
argument_list|,
name|distinct
argument_list|,
name|limit
argument_list|,
name|offset
argument_list|,
name|orderBy
argument_list|,
name|settings
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|measure
condition|)
block|{
comment|// return the measuring iterator for the union
name|it
operator|=
operator|new
name|MeasuringIterator
argument_list|(
name|this
argument_list|,
name|it
argument_list|)
block|{
name|MeasuringIterator
name|left
init|=
operator|(
name|MeasuringIterator
operator|)
name|leftRows
decl_stmt|;
name|MeasuringIterator
name|right
init|=
operator|(
name|MeasuringIterator
operator|)
name|rightRows
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setColumns
parameter_list|(
name|ColumnImpl
index|[]
name|cols
parameter_list|)
block|{
name|columns
operator|=
name|cols
expr_stmt|;
name|left
operator|.
name|setColumns
argument_list|(
name|cols
argument_list|)
expr_stmt|;
name|right
operator|.
name|setColumns
argument_list|(
name|cols
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getSelectorScanCount
parameter_list|()
block|{
comment|// Merge the 2 maps from the left and right queries to get the selector counts
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|leftSelectorScan
init|=
name|left
operator|.
name|getSelectorScanCount
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|rightSelectorScan
init|=
name|right
operator|.
name|getSelectorScanCount
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|unionScan
init|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|leftSelectorScan
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|rightSelectorScan
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|unionScan
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|unionScan
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|rightSelectorScan
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|+
name|unionScan
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|unionScan
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|rightSelectorScan
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|unionScan
return|;
block|}
annotation|@
name|Override
specifier|protected
name|long
name|getReadCount
parameter_list|()
block|{
return|return
name|left
operator|.
name|getReadCount
argument_list|()
operator|+
name|right
operator|.
name|getReadCount
argument_list|()
return|;
block|}
block|}
expr_stmt|;
block|}
return|return
name|it
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setInternal
parameter_list|(
name|boolean
name|isInternal
parameter_list|)
block|{
name|this
operator|.
name|isInternal
operator|=
name|isInternal
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSortedByIndex
parameter_list|()
block|{
return|return
name|left
operator|.
name|isSortedByIndex
argument_list|()
operator|&&
name|right
operator|.
name|isSortedByIndex
argument_list|()
return|;
block|}
block|}
end_class

end_unit

