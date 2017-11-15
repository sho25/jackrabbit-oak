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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|Type
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
name|QueryConstants
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
operator|.
name|OrderEntry
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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

begin_comment
comment|/**  * The function "coalesce(..)".  */
end_comment

begin_class
specifier|public
class|class
name|CoalesceImpl
extends|extends
name|DynamicOperandImpl
block|{
specifier|private
specifier|final
name|DynamicOperandImpl
name|operand1
decl_stmt|;
specifier|private
specifier|final
name|DynamicOperandImpl
name|operand2
decl_stmt|;
specifier|public
name|CoalesceImpl
parameter_list|(
name|DynamicOperandImpl
name|operand1
parameter_list|,
name|DynamicOperandImpl
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
name|operand2
operator|=
name|operand2
expr_stmt|;
block|}
specifier|public
name|DynamicOperandImpl
name|getOperand1
parameter_list|()
block|{
return|return
name|operand1
return|;
block|}
specifier|public
name|DynamicOperandImpl
name|getOperand2
parameter_list|()
block|{
return|return
name|operand2
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
literal|"coalesce("
operator|+
name|operand1
operator|+
literal|", "
operator|+
name|operand2
operator|+
literal|')'
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyExistenceImpl
name|getPropertyExistence
parameter_list|()
block|{
name|PropertyExistenceImpl
name|pe
init|=
name|operand1
operator|.
name|getPropertyExistence
argument_list|()
decl_stmt|;
return|return
name|pe
operator|!=
literal|null
condition|?
name|pe
else|:
name|operand2
operator|.
name|getPropertyExistence
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|SelectorImpl
argument_list|>
name|getSelectors
parameter_list|()
block|{
return|return
name|Sets
operator|.
name|union
argument_list|(
name|operand1
operator|.
name|getSelectors
argument_list|()
argument_list|,
name|operand2
operator|.
name|getSelectors
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyValue
name|currentProperty
parameter_list|()
block|{
name|PropertyValue
name|p
init|=
name|operand1
operator|.
name|currentProperty
argument_list|()
decl_stmt|;
return|return
name|p
operator|!=
literal|null
condition|?
name|p
else|:
name|operand2
operator|.
name|currentProperty
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restrict
parameter_list|(
name|FilterImpl
name|f
parameter_list|,
name|Operator
name|operator
parameter_list|,
name|PropertyValue
name|v
parameter_list|)
block|{
if|if
condition|(
name|operator
operator|==
name|Operator
operator|.
name|NOT_EQUAL
operator|&&
name|v
operator|!=
literal|null
condition|)
block|{
comment|// not supported
return|return;
block|}
name|String
name|fn
init|=
name|getFunction
argument_list|(
name|f
operator|.
name|getSelector
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fn
operator|!=
literal|null
condition|)
block|{
name|f
operator|.
name|restrictProperty
argument_list|(
name|QueryConstants
operator|.
name|FUNCTION_RESTRICTION_PREFIX
operator|+
name|fn
argument_list|,
name|operator
argument_list|,
name|v
argument_list|,
name|PropertyType
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|restrictList
parameter_list|(
name|FilterImpl
name|f
parameter_list|,
name|List
argument_list|<
name|PropertyValue
argument_list|>
name|list
parameter_list|)
block|{
name|String
name|fn
init|=
name|getFunction
argument_list|(
name|f
operator|.
name|getSelector
argument_list|()
argument_list|)
decl_stmt|;
name|f
operator|.
name|restrictPropertyAsList
argument_list|(
name|QueryConstants
operator|.
name|FUNCTION_RESTRICTION_PREFIX
operator|+
name|fn
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getFunction
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
name|String
name|f1
init|=
name|operand1
operator|.
name|getFunction
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|f1
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|f2
init|=
name|operand2
operator|.
name|getFunction
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|f2
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
literal|"coalesce*"
operator|+
name|f1
operator|+
literal|"*"
operator|+
name|f2
return|;
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
name|operand1
operator|.
name|canRestrictSelector
argument_list|(
name|s
argument_list|)
operator|&&
name|operand2
operator|.
name|canRestrictSelector
argument_list|(
name|s
argument_list|)
return|;
block|}
annotation|@
name|Override
name|int
name|getPropertyType
parameter_list|()
block|{
return|return
name|PropertyType
operator|.
name|STRING
return|;
block|}
annotation|@
name|Override
specifier|public
name|DynamicOperandImpl
name|createCopy
parameter_list|()
block|{
return|return
operator|new
name|CoalesceImpl
argument_list|(
name|operand1
operator|.
name|createCopy
argument_list|()
argument_list|,
name|operand2
operator|.
name|createCopy
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|OrderEntry
name|getOrderEntry
parameter_list|(
name|SelectorImpl
name|s
parameter_list|,
name|OrderingImpl
name|o
parameter_list|)
block|{
name|String
name|fn
init|=
name|getFunction
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|fn
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|OrderEntry
argument_list|(
name|QueryConstants
operator|.
name|FUNCTION_RESTRICTION_PREFIX
operator|+
name|fn
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|o
operator|.
name|isDescending
argument_list|()
condition|?
name|OrderEntry
operator|.
name|Order
operator|.
name|DESCENDING
else|:
name|OrderEntry
operator|.
name|Order
operator|.
name|ASCENDING
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit
