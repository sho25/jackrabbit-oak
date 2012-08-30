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
name|CoreValue
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
name|SelectorImpl
import|;
end_import

begin_comment
comment|/**  * A query result row that keeps all data in memory.  */
end_comment

begin_class
specifier|public
class|class
name|ResultRowImpl
implements|implements
name|ResultRow
implements|,
name|Comparable
argument_list|<
name|ResultRowImpl
argument_list|>
block|{
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|paths
decl_stmt|;
specifier|private
specifier|final
name|CoreValue
index|[]
name|values
decl_stmt|;
specifier|private
specifier|final
name|CoreValue
index|[]
index|[]
name|orderValues
decl_stmt|;
name|ResultRowImpl
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
index|[]
name|paths
parameter_list|,
name|CoreValue
index|[]
name|values
parameter_list|,
name|CoreValue
index|[]
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
name|paths
operator|=
name|paths
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|orderValues
operator|=
name|orderValues
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
if|if
condition|(
name|paths
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
name|paths
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
name|paths
index|[
literal|0
index|]
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
name|paths
operator|==
literal|null
operator|||
name|index
operator|>=
name|paths
operator|.
name|length
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|paths
index|[
name|index
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|CoreValue
name|getValue
parameter_list|(
name|String
name|columnName
parameter_list|)
block|{
return|return
name|values
index|[
name|query
operator|.
name|getColumnIndex
argument_list|(
name|columnName
argument_list|)
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|CoreValue
index|[]
name|getValues
parameter_list|()
block|{
name|CoreValue
index|[]
name|v2
init|=
operator|new
name|CoreValue
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
name|int
name|compareTo
parameter_list|(
name|ResultRowImpl
name|o
parameter_list|)
block|{
return|return
name|query
operator|.
name|compareRows
argument_list|(
name|orderValues
argument_list|,
name|o
operator|.
name|orderValues
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
for|for
control|(
name|SelectorImpl
name|s
range|:
name|query
operator|.
name|getSelectors
argument_list|()
control|)
block|{
name|String
name|n
init|=
name|s
operator|.
name|getSelectorName
argument_list|()
decl_stmt|;
name|String
name|p
init|=
name|getPath
argument_list|(
name|n
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
block|}
end_class

end_unit

