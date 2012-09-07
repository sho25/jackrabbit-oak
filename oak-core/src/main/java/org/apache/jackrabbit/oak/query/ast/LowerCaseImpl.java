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
name|plugins
operator|.
name|memory
operator|.
name|SinglePropertyState
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

begin_comment
comment|/**  * The function "lower(..)".  */
end_comment

begin_class
specifier|public
class|class
name|LowerCaseImpl
extends|extends
name|DynamicOperandImpl
block|{
specifier|private
specifier|final
name|DynamicOperandImpl
name|operand
decl_stmt|;
specifier|public
name|LowerCaseImpl
parameter_list|(
name|DynamicOperandImpl
name|operand
parameter_list|)
block|{
name|this
operator|.
name|operand
operator|=
name|operand
expr_stmt|;
block|}
specifier|public
name|DynamicOperandImpl
name|getOperand
parameter_list|()
block|{
return|return
name|operand
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
literal|"lower("
operator|+
name|operand
operator|+
literal|')'
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyState
name|currentProperty
parameter_list|()
block|{
name|PropertyState
name|p
init|=
name|operand
operator|.
name|currentProperty
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
comment|// TODO what is the expected result of LOWER(x) for an array property?
comment|// currently throws an exception
name|String
name|value
init|=
name|p
operator|.
name|getValue
argument_list|()
operator|.
name|getString
argument_list|()
decl_stmt|;
name|CoreValue
name|v
init|=
name|query
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
operator|.
name|toLowerCase
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|SinglePropertyState
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|v
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|FilterImpl
name|f
parameter_list|,
name|Operator
name|operator
parameter_list|,
name|CoreValue
name|v
parameter_list|)
block|{
comment|// ignore
comment|// TODO UPPER(x) conditions: can use IS NOT NULL?
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRestrictSelector
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
return|return
name|operand
operator|.
name|canRestrictSelector
argument_list|(
name|s
argument_list|)
return|;
block|}
block|}
end_class

end_unit

