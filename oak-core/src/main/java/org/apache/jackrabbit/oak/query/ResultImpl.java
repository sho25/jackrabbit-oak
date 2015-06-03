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
name|ArrayList
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
comment|/**  * A query result.  */
end_comment

begin_class
specifier|public
class|class
name|ResultImpl
implements|implements
name|Result
block|{
specifier|protected
specifier|final
name|Query
name|query
decl_stmt|;
name|ResultImpl
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getColumnNames
parameter_list|()
block|{
name|ColumnImpl
index|[]
name|cols
init|=
name|query
operator|.
name|getColumns
argument_list|()
decl_stmt|;
name|String
index|[]
name|names
init|=
operator|new
name|String
index|[
name|cols
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
name|cols
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|names
index|[
name|i
index|]
operator|=
name|cols
index|[
name|i
index|]
operator|.
name|getColumnName
argument_list|()
expr_stmt|;
block|}
return|return
name|names
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getColumnSelectorNames
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ColumnImpl
name|c
range|:
name|query
operator|.
name|getColumns
argument_list|()
control|)
block|{
name|SelectorImpl
name|selector
init|=
name|c
operator|.
name|getSelector
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|selector
operator|==
literal|null
condition|?
literal|null
else|:
name|selector
operator|.
name|getSelectorName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|list
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
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
name|query
operator|.
name|getSelectorNames
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|getRows
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|ResultRowImpl
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ResultRowImpl
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|query
operator|.
name|getRows
argument_list|()
return|;
block|}
block|}
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
name|query
operator|.
name|getSize
argument_list|()
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
return|return
name|query
operator|.
name|getSize
argument_list|(
name|precision
argument_list|,
name|max
argument_list|)
return|;
block|}
block|}
end_class

end_unit

