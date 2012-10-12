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
name|spi
operator|.
name|query
operator|.
name|PropertyValues
import|;
end_import

begin_comment
comment|/**  * A result column expression.  */
end_comment

begin_class
specifier|public
class|class
name|ColumnImpl
extends|extends
name|AstElement
block|{
specifier|private
specifier|final
name|String
name|selectorName
decl_stmt|,
name|propertyName
decl_stmt|,
name|columnName
decl_stmt|;
specifier|private
name|SelectorImpl
name|selector
decl_stmt|;
specifier|public
name|ColumnImpl
parameter_list|(
name|String
name|selectorName
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|String
name|columnName
parameter_list|)
block|{
name|this
operator|.
name|selectorName
operator|=
name|selectorName
expr_stmt|;
name|this
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
name|this
operator|.
name|columnName
operator|=
name|columnName
expr_stmt|;
block|}
specifier|public
name|String
name|getColumnName
parameter_list|()
block|{
return|return
name|columnName
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
if|if
condition|(
name|propertyName
operator|!=
literal|null
condition|)
block|{
return|return
name|quote
argument_list|(
name|selectorName
argument_list|)
operator|+
literal|'.'
operator|+
name|quote
argument_list|(
name|propertyName
argument_list|)
operator|+
literal|" as "
operator|+
name|quote
argument_list|(
name|columnName
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|quote
argument_list|(
name|selectorName
argument_list|)
operator|+
literal|".*"
return|;
block|}
block|}
specifier|public
name|PropertyValue
name|currentProperty
parameter_list|()
block|{
if|if
condition|(
name|propertyName
operator|==
literal|null
condition|)
block|{
comment|// TODO for SELECT * FROM queries, currently return the path (for testing only)
name|String
name|p
init|=
name|selector
operator|.
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
return|return
name|PropertyValues
operator|.
name|newString
argument_list|(
name|p
argument_list|)
return|;
block|}
return|return
name|selector
operator|.
name|currentProperty
argument_list|(
name|propertyName
argument_list|)
return|;
block|}
specifier|public
name|void
name|bindSelector
parameter_list|(
name|SourceImpl
name|source
parameter_list|)
block|{
name|selector
operator|=
name|source
operator|.
name|getExistingSelector
argument_list|(
name|selectorName
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

