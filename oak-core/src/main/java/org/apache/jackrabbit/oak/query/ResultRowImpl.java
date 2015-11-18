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
name|query
package|;
end_package

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
name|ResultRow
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
name|fulltext
operator|.
name|SimpleExcerptProvider
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

begin_comment
comment|/**  * A query result row that keeps all data (for this row only) in memory.  */
end_comment

begin_class
specifier|public
class|class
name|ResultRowImpl
implements|implements
name|ResultRow
block|{
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
specifier|private
specifier|final
name|Tree
index|[]
name|trees
decl_stmt|;
comment|/**      * The column values.      */
specifier|private
specifier|final
name|PropertyValue
index|[]
name|values
decl_stmt|;
comment|/**      * Whether the value at the given index is used for comparing rows (used      * within hashCode and equals). If null, all columns are distinct.      */
specifier|private
specifier|final
name|boolean
index|[]
name|distinctValues
decl_stmt|;
comment|/**      * The values used for ordering.      */
specifier|private
specifier|final
name|PropertyValue
index|[]
name|orderValues
decl_stmt|;
name|ResultRowImpl
parameter_list|(
name|Query
name|query
parameter_list|,
name|Tree
index|[]
name|trees
parameter_list|,
name|PropertyValue
index|[]
name|values
parameter_list|,
name|boolean
index|[]
name|distinctValues
parameter_list|,
name|PropertyValue
index|[]
name|orderValues
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|trees
operator|=
name|trees
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|distinctValues
operator|=
name|distinctValues
expr_stmt|;
name|this
operator|.
name|orderValues
operator|=
name|orderValues
expr_stmt|;
block|}
name|PropertyValue
index|[]
name|getOrderValues
parameter_list|()
block|{
return|return
name|orderValues
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|getPath
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|(
name|String
name|selectorName
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|getTree
argument_list|(
name|selectorName
argument_list|)
decl_stmt|;
if|if
condition|(
name|tree
operator|!=
literal|null
condition|)
block|{
return|return
name|tree
operator|.
name|getPath
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|getTree
parameter_list|(
name|String
name|selectorName
parameter_list|)
block|{
if|if
condition|(
name|selectorName
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|trees
operator|.
name|length
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"More than one selector"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|trees
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"This query does not have a selector"
argument_list|)
throw|;
block|}
return|return
name|trees
index|[
literal|0
index|]
return|;
block|}
name|int
name|index
init|=
name|query
operator|.
name|getSelectorIndex
argument_list|(
name|selectorName
argument_list|)
decl_stmt|;
if|if
condition|(
name|trees
operator|==
literal|null
operator|||
name|index
operator|>=
name|trees
operator|.
name|length
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|trees
index|[
name|index
index|]
return|;
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
name|int
name|index
init|=
name|query
operator|.
name|getColumnIndex
argument_list|(
name|columnName
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
return|return
name|values
index|[
name|index
index|]
return|;
block|}
if|if
condition|(
name|JcrConstants
operator|.
name|JCR_PATH
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
name|getPath
argument_list|()
argument_list|)
return|;
block|}
comment|// OAK-318:
comment|// somebody might call rep:excerpt(text)
comment|// even though the query doesn't contain that column
if|if
condition|(
name|columnName
operator|.
name|startsWith
argument_list|(
name|QueryImpl
operator|.
name|REP_EXCERPT
argument_list|)
condition|)
block|{
name|int
name|columnIndex
init|=
name|query
operator|.
name|getColumnIndex
argument_list|(
name|QueryImpl
operator|.
name|REP_EXCERPT
argument_list|)
decl_stmt|;
name|PropertyValue
name|indexExcerptValue
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|columnIndex
operator|>=
literal|0
condition|)
block|{
name|indexExcerptValue
operator|=
name|values
index|[
name|columnIndex
index|]
expr_stmt|;
if|if
condition|(
name|indexExcerptValue
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|QueryImpl
operator|.
name|REP_EXCERPT
operator|.
name|equals
argument_list|(
name|columnName
argument_list|)
operator|||
name|SimpleExcerptProvider
operator|.
name|REP_EXCERPT_FN
operator|.
name|equals
argument_list|(
name|columnName
argument_list|)
condition|)
block|{
return|return
name|SimpleExcerptProvider
operator|.
name|getExcerpt
argument_list|(
name|indexExcerptValue
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|getFallbackExcerpt
argument_list|(
name|columnName
argument_list|,
name|indexExcerptValue
argument_list|)
return|;
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
specifier|private
name|PropertyValue
name|getFallbackExcerpt
parameter_list|(
name|String
name|columnName
parameter_list|,
name|PropertyValue
name|indexValue
parameter_list|)
block|{
name|String
name|ex
init|=
name|SimpleExcerptProvider
operator|.
name|getExcerpt
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|columnName
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|ex
operator|!=
literal|null
operator|&&
name|ex
operator|.
name|length
argument_list|()
operator|>
literal|24
condition|)
block|{
return|return
name|PropertyValues
operator|.
name|newString
argument_list|(
name|ex
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|indexValue
operator|!=
literal|null
condition|)
block|{
return|return
name|SimpleExcerptProvider
operator|.
name|getExcerpt
argument_list|(
name|indexValue
argument_list|)
return|;
block|}
return|return
name|PropertyValues
operator|.
name|newString
argument_list|(
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyValue
index|[]
name|getValues
parameter_list|()
block|{
name|PropertyValue
index|[]
name|v2
init|=
operator|new
name|PropertyValue
index|[
name|values
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|v2
argument_list|,
literal|0
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|v2
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
for|for
control|(
name|String
name|s
range|:
name|query
operator|.
name|getSelectorNames
argument_list|()
control|)
block|{
name|String
name|p
init|=
name|getPath
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|s
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|p
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
name|ColumnImpl
index|[]
name|cols
init|=
name|query
operator|.
name|getColumns
argument_list|()
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ColumnImpl
name|c
init|=
name|cols
index|[
name|i
index|]
decl_stmt|;
name|String
name|n
init|=
name|c
operator|.
name|getColumnName
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|n
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
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
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|getPaths
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|hashCodeOfValues
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|int
name|hashCodeOfValues
parameter_list|()
block|{
name|int
name|result
init|=
literal|1
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|distinctValues
operator|==
literal|null
operator|||
name|distinctValues
index|[
name|i
index|]
condition|)
block|{
name|PropertyValue
name|v
init|=
name|values
index|[
name|i
index|]
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|v
operator|==
literal|null
condition|?
literal|0
else|:
name|v
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ResultRowImpl
name|other
init|=
operator|(
name|ResultRowImpl
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|getPaths
argument_list|()
argument_list|,
name|other
operator|.
name|getPaths
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|distinctValues
argument_list|,
name|other
operator|.
name|distinctValues
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// if distinctValues are equals, then the number of values
comment|// is also equal
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|distinctValues
operator|==
literal|null
operator|||
name|distinctValues
index|[
name|i
index|]
condition|)
block|{
name|Object
name|o1
init|=
name|values
index|[
name|i
index|]
decl_stmt|;
name|Object
name|o2
init|=
name|other
operator|.
name|values
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|o1
operator|==
literal|null
condition|?
name|o2
operator|==
literal|null
else|:
name|o1
operator|.
name|equals
argument_list|(
name|o2
argument_list|)
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|String
index|[]
name|getPaths
parameter_list|()
block|{
name|String
index|[]
name|paths
init|=
operator|new
name|String
index|[
name|trees
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
name|trees
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|trees
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|paths
index|[
name|i
index|]
operator|=
name|trees
index|[
name|i
index|]
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|paths
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
name|paths
return|;
block|}
specifier|public
specifier|static
name|Comparator
argument_list|<
name|ResultRowImpl
argument_list|>
name|getComparator
parameter_list|(
specifier|final
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
return|return
literal|null
return|;
block|}
return|return
operator|new
name|Comparator
argument_list|<
name|ResultRowImpl
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|ResultRowImpl
name|o1
parameter_list|,
name|ResultRowImpl
name|o2
parameter_list|)
block|{
name|PropertyValue
index|[]
name|orderValues
init|=
name|o1
operator|.
name|getOrderValues
argument_list|()
decl_stmt|;
name|PropertyValue
index|[]
name|orderValues2
init|=
name|o2
operator|.
name|getOrderValues
argument_list|()
decl_stmt|;
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
comment|// TODO order by: nulls first (it looks like), or
comment|// low?
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
block|}
return|;
block|}
block|}
end_class

end_unit

