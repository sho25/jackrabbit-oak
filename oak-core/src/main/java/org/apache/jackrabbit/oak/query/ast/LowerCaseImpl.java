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
name|PropertyValues
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|transform
import|;
end_import

begin_import
import|import static
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
operator|.
name|STRING
import|;
end_import

begin_import
import|import static
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
operator|.
name|STRINGS
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
name|PropertyExistenceImpl
name|getPropertyExistence
parameter_list|()
block|{
return|return
name|operand
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
name|operand
operator|.
name|getSelectors
argument_list|()
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
comment|// TODO toLowerCase(): document the Turkish locale problem
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|Iterable
argument_list|<
name|String
argument_list|>
name|lowerCase
init|=
name|transform
argument_list|(
name|p
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|toLowerCase
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|PropertyValues
operator|.
name|newString
argument_list|(
name|lowerCase
argument_list|)
return|;
block|}
else|else
block|{
name|String
name|value
init|=
name|p
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
decl_stmt|;
return|return
name|PropertyValues
operator|.
name|newString
argument_list|(
name|value
operator|.
name|toLowerCase
argument_list|()
argument_list|)
return|;
block|}
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
comment|// LOWER(x) implies x is not null
name|operand
operator|.
name|restrict
argument_list|(
name|f
argument_list|,
name|Operator
operator|.
name|NOT_EQUAL
argument_list|,
literal|null
argument_list|)
expr_stmt|;
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
comment|// "LOWER(x) IN (A, B)" implies x is not null
name|operand
operator|.
name|restrict
argument_list|(
name|f
argument_list|,
name|Operator
operator|.
name|NOT_EQUAL
argument_list|,
literal|null
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
name|f
init|=
name|operand
operator|.
name|getFunction
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
literal|"lower*"
operator|+
name|f
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
name|operand
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
name|LowerCaseImpl
argument_list|(
name|operand
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

