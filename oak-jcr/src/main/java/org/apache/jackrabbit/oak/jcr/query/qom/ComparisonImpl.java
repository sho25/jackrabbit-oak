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
name|jcr
operator|.
name|query
operator|.
name|qom
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|qom
operator|.
name|Comparison
import|;
end_import

begin_comment
comment|/**  * The implementation of the corresponding JCR interface.  */
end_comment

begin_class
specifier|public
class|class
name|ComparisonImpl
extends|extends
name|ConstraintImpl
implements|implements
name|Comparison
block|{
specifier|private
specifier|final
name|DynamicOperandImpl
name|operand1
decl_stmt|;
specifier|private
specifier|final
name|Operator
name|operator
decl_stmt|;
specifier|private
specifier|final
name|StaticOperandImpl
name|operand2
decl_stmt|;
specifier|public
name|ComparisonImpl
parameter_list|(
name|DynamicOperandImpl
name|operand1
parameter_list|,
name|Operator
name|operator
parameter_list|,
name|StaticOperandImpl
name|operand2
parameter_list|)
block|{
name|this
operator|.
name|operand1
operator|=
name|operand1
expr_stmt|;
name|this
operator|.
name|operator
operator|=
name|operator
expr_stmt|;
name|this
operator|.
name|operand2
operator|=
name|operand2
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|DynamicOperandImpl
name|getOperand1
parameter_list|()
block|{
return|return
name|operand1
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getOperator
parameter_list|()
block|{
return|return
name|operator
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|StaticOperandImpl
name|getOperand2
parameter_list|()
block|{
return|return
name|operand2
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
name|operator
operator|.
name|formatSql
argument_list|(
name|operand1
operator|.
name|toString
argument_list|()
argument_list|,
name|operand2
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|bindVariables
parameter_list|(
name|QueryObjectModelImpl
name|qom
parameter_list|)
block|{
name|operand2
operator|.
name|bindVariables
argument_list|(
name|qom
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

