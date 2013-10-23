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
name|Collections
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|PropertyValues
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
name|Sets
import|;
end_import

begin_comment
comment|/**  * A "in" comparison operation.  */
end_comment

begin_class
specifier|public
class|class
name|InImpl
extends|extends
name|ConstraintImpl
block|{
specifier|private
specifier|final
name|DynamicOperandImpl
name|operand1
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|StaticOperandImpl
argument_list|>
name|operand2List
decl_stmt|;
specifier|public
name|InImpl
parameter_list|(
name|DynamicOperandImpl
name|operand1
parameter_list|,
name|List
argument_list|<
name|StaticOperandImpl
argument_list|>
name|operand2List
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
name|operand2List
operator|=
name|operand2List
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
name|List
argument_list|<
name|StaticOperandImpl
argument_list|>
name|getOperand2List
parameter_list|()
block|{
return|return
name|operand2List
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|PropertyExistenceImpl
argument_list|>
name|getPropertyExistenceConditions
parameter_list|()
block|{
name|PropertyExistenceImpl
name|p
init|=
name|operand1
operator|.
name|getPropertyExistence
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
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|p
argument_list|)
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
name|operand1
operator|.
name|getSelectors
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|DynamicOperandImpl
argument_list|,
name|Set
argument_list|<
name|StaticOperandImpl
argument_list|>
argument_list|>
name|getInMap
parameter_list|()
block|{
name|Set
argument_list|<
name|StaticOperandImpl
argument_list|>
name|set
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|operand2List
argument_list|)
decl_stmt|;
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
name|operand1
argument_list|,
name|set
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
comment|// JCR 2.0 spec, 6.7.16 Comparison:
comment|// "operand1 may evaluate to an array of values"
name|PropertyValue
name|p1
init|=
name|operand1
operator|.
name|currentProperty
argument_list|()
decl_stmt|;
if|if
condition|(
name|p1
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|StaticOperandImpl
name|operand2
range|:
name|operand2List
control|)
block|{
name|PropertyValue
name|p2
init|=
name|operand2
operator|.
name|currentValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|p2
operator|==
literal|null
condition|)
block|{
comment|// if the property doesn't exist, the result is false
continue|continue;
block|}
comment|// "the value of operand2 is converted to the
comment|// property type of the value of operand1"
name|p2
operator|=
name|convertValueToType
argument_list|(
name|p2
argument_list|,
name|p1
argument_list|)
expr_stmt|;
if|if
condition|(
name|PropertyValues
operator|.
name|match
argument_list|(
name|p1
argument_list|,
name|p2
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
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
name|operand1
argument_list|)
operator|.
name|append
argument_list|(
literal|" in("
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|StaticOperandImpl
name|operand2
range|:
name|operand2List
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
name|operand2
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|")"
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
name|void
name|restrict
parameter_list|(
name|FilterImpl
name|f
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|PropertyValue
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|PropertyValue
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|StaticOperandImpl
name|s
range|:
name|operand2List
control|)
block|{
if|if
condition|(
operator|!
name|PropertyValues
operator|.
name|canConvert
argument_list|(
name|s
operator|.
name|getPropertyType
argument_list|()
argument_list|,
name|operand1
operator|.
name|getPropertyType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported conversion from property type "
operator|+
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|s
operator|.
name|getPropertyType
argument_list|()
argument_list|)
operator|+
literal|" to property type "
operator|+
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|operand1
operator|.
name|getPropertyType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|list
operator|.
name|add
argument_list|(
name|s
operator|.
name|currentValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
block|{
name|operand1
operator|.
name|restrictList
argument_list|(
name|f
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|restrictPushDown
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
for|for
control|(
name|StaticOperandImpl
name|op
range|:
name|operand2List
control|)
block|{
if|if
condition|(
name|op
operator|.
name|currentValue
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// one unknown value means it is not pushed down
return|return;
block|}
block|}
if|if
condition|(
name|operand1
operator|.
name|canRestrictSelector
argument_list|(
name|s
argument_list|)
condition|)
block|{
name|s
operator|.
name|restrictSelector
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

