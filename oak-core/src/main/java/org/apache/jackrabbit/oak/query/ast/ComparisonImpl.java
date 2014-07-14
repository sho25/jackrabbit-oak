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
name|Collections
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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
name|LikePattern
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

begin_comment
comment|/**  * A comparison operation (including "like").  */
end_comment

begin_class
specifier|public
class|class
name|ComparisonImpl
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
name|Operator
name|getOperator
parameter_list|()
block|{
return|return
name|operator
return|;
block|}
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
if|if
condition|(
name|operator
operator|==
name|Operator
operator|.
name|EQUAL
condition|)
block|{
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
name|operand1
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|operand2
argument_list|)
argument_list|)
return|;
block|}
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
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
comment|// if the property doesn't exist, the result is always false
comment|// even for "null<> 'x'" (same as in SQL)
return|return
literal|false
return|;
block|}
comment|// "the value of operand2 is converted to the
comment|// property type of the value of operand1"
try|try
block|{
name|p2
operator|=
name|convertValueToType
argument_list|(
name|p2
argument_list|,
name|p1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// unable to convert, just skip this node
return|return
literal|false
return|;
block|}
if|if
condition|(
name|p1
operator|.
name|isArray
argument_list|()
condition|)
block|{
comment|// JCR 2.0 spec, 6.7.16 Comparison:
comment|// "... constraint is satisfied as a whole if the comparison
comment|// against any element of the array is satisfied."
name|Type
argument_list|<
name|?
argument_list|>
name|base
init|=
name|p1
operator|.
name|getType
argument_list|()
operator|.
name|getBaseType
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
name|p1
operator|.
name|count
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|PropertyState
name|value
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"value"
argument_list|,
name|p1
operator|.
name|getValue
argument_list|(
name|base
argument_list|,
name|i
argument_list|)
argument_list|,
name|base
argument_list|)
decl_stmt|;
if|if
condition|(
name|evaluate
argument_list|(
name|PropertyValues
operator|.
name|create
argument_list|(
name|value
argument_list|)
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
else|else
block|{
return|return
name|evaluate
argument_list|(
name|p1
argument_list|,
name|p2
argument_list|)
return|;
block|}
block|}
comment|/**      * "operand2 always evaluates to a scalar value"      *       * for multi-valued properties: if any of the value matches, then return true      *       * @param p1      * @param p2      * @return      */
specifier|private
name|boolean
name|evaluate
parameter_list|(
name|PropertyValue
name|p1
parameter_list|,
name|PropertyValue
name|p2
parameter_list|)
block|{
switch|switch
condition|(
name|operator
condition|)
block|{
case|case
name|EQUAL
case|:
return|return
name|PropertyValues
operator|.
name|match
argument_list|(
name|p1
argument_list|,
name|p2
argument_list|)
return|;
case|case
name|NOT_EQUAL
case|:
return|return
name|PropertyValues
operator|.
name|notMatch
argument_list|(
name|p1
argument_list|,
name|p2
argument_list|)
return|;
case|case
name|GREATER_OR_EQUAL
case|:
return|return
name|p1
operator|.
name|compareTo
argument_list|(
name|p2
argument_list|)
operator|>=
literal|0
return|;
case|case
name|GREATER_THAN
case|:
return|return
name|p1
operator|.
name|compareTo
argument_list|(
name|p2
argument_list|)
operator|>
literal|0
return|;
case|case
name|LESS_OR_EQUAL
case|:
return|return
name|p1
operator|.
name|compareTo
argument_list|(
name|p2
argument_list|)
operator|<=
literal|0
return|;
case|case
name|LESS_THAN
case|:
return|return
name|p1
operator|.
name|compareTo
argument_list|(
name|p2
argument_list|)
operator|<
literal|0
return|;
case|case
name|LIKE
case|:
return|return
name|evaluateLike
argument_list|(
name|p1
argument_list|,
name|p2
argument_list|)
return|;
comment|// case IN is not needed here, as this is handled in the class InImpl.
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown operator: "
operator|+
name|operator
argument_list|)
throw|;
block|}
specifier|private
specifier|static
name|boolean
name|evaluateLike
parameter_list|(
name|PropertyValue
name|v1
parameter_list|,
name|PropertyValue
name|v2
parameter_list|)
block|{
name|LikePattern
name|like
init|=
operator|new
name|LikePattern
argument_list|(
name|v2
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|v1
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
control|)
block|{
if|if
condition|(
name|like
operator|.
name|matches
argument_list|(
name|s
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
return|return
name|operand1
operator|+
literal|" "
operator|+
name|operator
operator|+
literal|" "
operator|+
name|operand2
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
name|PropertyValue
name|v
init|=
name|operand2
operator|.
name|currentValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|PropertyValues
operator|.
name|canConvert
argument_list|(
name|operand2
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
name|operand2
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
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|operator
operator|==
name|Operator
operator|.
name|LIKE
condition|)
block|{
name|String
name|pattern
decl_stmt|;
name|pattern
operator|=
name|v
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|LikePattern
name|p
init|=
operator|new
name|LikePattern
argument_list|(
name|pattern
argument_list|)
decl_stmt|;
name|String
name|lowerBound
init|=
name|p
operator|.
name|getLowerBound
argument_list|()
decl_stmt|;
if|if
condition|(
name|lowerBound
operator|!=
literal|null
condition|)
block|{
name|String
name|upperBound
init|=
name|p
operator|.
name|getUpperBound
argument_list|()
decl_stmt|;
if|if
condition|(
name|lowerBound
operator|.
name|equals
argument_list|(
name|upperBound
argument_list|)
condition|)
block|{
comment|// no wildcards
name|operand1
operator|.
name|restrict
argument_list|(
name|f
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|operand1
operator|.
name|supportsRangeConditions
argument_list|()
condition|)
block|{
if|if
condition|(
name|lowerBound
operator|!=
literal|null
condition|)
block|{
name|PropertyValue
name|pv
init|=
name|PropertyValues
operator|.
name|newString
argument_list|(
name|lowerBound
argument_list|)
decl_stmt|;
name|operand1
operator|.
name|restrict
argument_list|(
name|f
argument_list|,
name|Operator
operator|.
name|GREATER_OR_EQUAL
argument_list|,
name|pv
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|upperBound
operator|!=
literal|null
condition|)
block|{
name|PropertyValue
name|pv
init|=
name|PropertyValues
operator|.
name|newString
argument_list|(
name|upperBound
argument_list|)
decl_stmt|;
name|operand1
operator|.
name|restrict
argument_list|(
name|f
argument_list|,
name|Operator
operator|.
name|LESS_OR_EQUAL
argument_list|,
name|pv
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// path conditions
name|operand1
operator|.
name|restrict
argument_list|(
name|f
argument_list|,
name|operator
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// like '%' conditions
name|operand1
operator|.
name|restrict
argument_list|(
name|f
argument_list|,
name|operator
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|operand1
operator|.
name|restrict
argument_list|(
name|f
argument_list|,
name|operator
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|operand2
operator|.
name|currentValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
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
block|}
end_class

end_unit

