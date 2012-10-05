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
name|CoreValues
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
name|Query
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
name|boolean
name|evaluate
parameter_list|()
block|{
comment|// JCR 2.0 spec, 6.7.16 Comparison:
comment|// "operand1 may evaluate to an array of values"
name|PropertyState
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
comment|// "operand2 always evaluates to a scalar value"
name|CoreValue
name|v2
init|=
name|operand2
operator|.
name|currentValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|v2
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
name|boolean
name|isArray
init|=
name|p1
operator|.
name|isArray
argument_list|()
decl_stmt|;
name|int
name|v1Type
init|=
name|Query
operator|.
name|getType
argument_list|(
name|p1
argument_list|,
name|v2
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|v1Type
operator|!=
name|v2
operator|.
name|getType
argument_list|()
condition|)
block|{
comment|// "the value of operand2 is converted to the
comment|// property type of the value of operand1"
name|v2
operator|=
name|query
operator|.
name|convert
argument_list|(
name|v2
argument_list|,
name|v1Type
argument_list|)
expr_stmt|;
if|if
condition|(
name|v2
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
operator|!
name|isArray
condition|)
block|{
return|return
name|evaluate
argument_list|(
name|CoreValues
operator|.
name|getValue
argument_list|(
name|p1
argument_list|)
argument_list|,
name|v2
argument_list|)
return|;
block|}
comment|// for multi-valued properties: if any of the value matches,
comment|// then return true
for|for
control|(
name|CoreValue
name|v1
range|:
name|CoreValues
operator|.
name|getValues
argument_list|(
name|p1
argument_list|)
control|)
block|{
if|if
condition|(
name|evaluate
argument_list|(
name|v1
argument_list|,
name|v2
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
specifier|private
name|boolean
name|evaluate
parameter_list|(
name|CoreValue
name|v1
parameter_list|,
name|CoreValue
name|v2
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
name|v1
operator|.
name|equals
argument_list|(
name|v2
argument_list|)
return|;
case|case
name|GREATER_OR_EQUAL
case|:
return|return
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|>=
literal|0
return|;
case|case
name|GREATER_THAN
case|:
return|return
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|>
literal|0
return|;
case|case
name|LESS_OR_EQUAL
case|:
return|return
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|<=
literal|0
return|;
case|case
name|LESS_THAN
case|:
return|return
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|<
literal|0
return|;
case|case
name|NOT_EQUAL
case|:
return|return
operator|!
name|v1
operator|.
name|equals
argument_list|(
name|v2
argument_list|)
return|;
case|case
name|LIKE
case|:
return|return
name|evaluateLike
argument_list|(
name|v1
argument_list|,
name|v2
argument_list|)
return|;
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
name|CoreValue
name|v1
parameter_list|,
name|CoreValue
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
name|getString
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|like
operator|.
name|matches
argument_list|(
name|v1
operator|.
name|getString
argument_list|()
argument_list|)
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
name|CoreValue
name|v
init|=
name|operand2
operator|.
name|currentValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
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
comment|// TODO OAK-347
comment|//            if (operator == Operator.LIKE) {
comment|//                String pattern;
comment|//                pattern = v.getString();
comment|//                LikePattern p = new LikePattern(pattern);
comment|//                String lowerBound = p.getLowerBound();
comment|//                String upperBound = p.getUpperBound();
comment|//                if (lowerBound == null&& upperBound == null) {
comment|//                    // ignore
comment|//                } else if (operand1.supportsRangeConditions()) {
comment|//                    CoreValueFactory vf = query.getValueFactory();
comment|//                    if (lowerBound != null) {
comment|//                        operand1.restrict(f, Operator.GREATER_OR_EQUAL, vf.createValue(lowerBound));
comment|//                    }
comment|//                    if (upperBound != null) {
comment|//                        operand1.restrict(f, Operator.LESS_OR_EQUAL, vf.createValue(upperBound));
comment|//                    }
comment|//                }
comment|//            } else {
comment|//                operand1.restrict(f, operator, v);
comment|//            }
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
name|CoreValue
name|v
init|=
name|operand2
operator|.
name|currentValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
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
comment|/**      * A pattern matcher.      */
specifier|public
specifier|static
class|class
name|LikePattern
block|{
comment|// TODO LIKE: optimize condition to '=' when no patterns are used, or 'between x and x+1'
comment|// TODO LIKE: what to do for invalid patterns (patterns ending with a backslash)
specifier|private
specifier|static
specifier|final
name|int
name|MATCH
init|=
literal|0
decl_stmt|,
name|ONE
init|=
literal|1
decl_stmt|,
name|ANY
init|=
literal|2
decl_stmt|;
specifier|private
name|String
name|patternString
decl_stmt|;
specifier|private
name|boolean
name|invalidPattern
decl_stmt|;
specifier|private
name|char
index|[]
name|patternChars
decl_stmt|;
specifier|private
name|int
index|[]
name|patternTypes
decl_stmt|;
specifier|private
name|int
name|patternLength
decl_stmt|;
specifier|private
name|String
name|lowerBounds
decl_stmt|,
name|upperBound
decl_stmt|;
specifier|public
name|LikePattern
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|initPattern
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
name|initBounds
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|!
name|invalidPattern
operator|&&
name|compareAt
argument_list|(
name|value
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
argument_list|,
name|patternChars
argument_list|,
name|patternTypes
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|compare
parameter_list|(
name|char
index|[]
name|pattern
parameter_list|,
name|String
name|s
parameter_list|,
name|int
name|pi
parameter_list|,
name|int
name|si
parameter_list|)
block|{
return|return
name|pattern
index|[
name|pi
index|]
operator|==
name|s
operator|.
name|charAt
argument_list|(
name|si
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|compareAt
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|pi
parameter_list|,
name|int
name|si
parameter_list|,
name|int
name|sLen
parameter_list|,
name|char
index|[]
name|pattern
parameter_list|,
name|int
index|[]
name|types
parameter_list|)
block|{
for|for
control|(
init|;
name|pi
operator|<
name|patternLength
condition|;
name|pi
operator|++
control|)
block|{
name|int
name|type
init|=
name|types
index|[
name|pi
index|]
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|MATCH
case|:
if|if
condition|(
name|si
operator|>=
name|sLen
operator|||
operator|!
name|compare
argument_list|(
name|pattern
argument_list|,
name|s
argument_list|,
name|pi
argument_list|,
name|si
operator|++
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
break|break;
case|case
name|ONE
case|:
if|if
condition|(
name|si
operator|++
operator|>=
name|sLen
condition|)
block|{
return|return
literal|false
return|;
block|}
break|break;
case|case
name|ANY
case|:
if|if
condition|(
operator|++
name|pi
operator|>=
name|patternLength
condition|)
block|{
return|return
literal|true
return|;
block|}
while|while
condition|(
name|si
operator|<
name|sLen
condition|)
block|{
if|if
condition|(
name|compare
argument_list|(
name|pattern
argument_list|,
name|s
argument_list|,
name|pi
argument_list|,
name|si
argument_list|)
operator|&&
name|compareAt
argument_list|(
name|s
argument_list|,
name|pi
argument_list|,
name|si
argument_list|,
name|sLen
argument_list|,
name|pattern
argument_list|,
name|types
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|si
operator|++
expr_stmt|;
block|}
return|return
literal|false
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Internal error: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
return|return
name|si
operator|==
name|sLen
return|;
block|}
specifier|private
name|void
name|initPattern
parameter_list|(
name|String
name|p
parameter_list|)
block|{
name|patternLength
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|patternTypes
operator|=
literal|null
expr_stmt|;
name|patternChars
operator|=
literal|null
expr_stmt|;
return|return;
block|}
name|int
name|len
init|=
name|p
operator|.
name|length
argument_list|()
decl_stmt|;
name|patternChars
operator|=
operator|new
name|char
index|[
name|len
index|]
expr_stmt|;
name|patternTypes
operator|=
operator|new
name|int
index|[
name|len
index|]
expr_stmt|;
name|boolean
name|lastAny
init|=
literal|false
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|p
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|type
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\\'
condition|)
block|{
if|if
condition|(
name|i
operator|>=
name|len
operator|-
literal|1
condition|)
block|{
name|invalidPattern
operator|=
literal|true
expr_stmt|;
return|return;
block|}
name|c
operator|=
name|p
operator|.
name|charAt
argument_list|(
operator|++
name|i
argument_list|)
expr_stmt|;
name|type
operator|=
name|MATCH
expr_stmt|;
name|lastAny
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'%'
condition|)
block|{
if|if
condition|(
name|lastAny
condition|)
block|{
continue|continue;
block|}
name|type
operator|=
name|ANY
expr_stmt|;
name|lastAny
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'_'
condition|)
block|{
name|type
operator|=
name|ONE
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|MATCH
expr_stmt|;
name|lastAny
operator|=
literal|false
expr_stmt|;
block|}
name|patternTypes
index|[
name|patternLength
index|]
operator|=
name|type
expr_stmt|;
name|patternChars
index|[
name|patternLength
operator|++
index|]
operator|=
name|c
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|patternLength
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|patternTypes
index|[
name|i
index|]
operator|==
name|ANY
operator|&&
name|patternTypes
index|[
name|i
operator|+
literal|1
index|]
operator|==
name|ONE
condition|)
block|{
name|patternTypes
index|[
name|i
index|]
operator|=
name|ONE
expr_stmt|;
name|patternTypes
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|ANY
expr_stmt|;
block|}
block|}
name|patternString
operator|=
operator|new
name|String
argument_list|(
name|patternChars
argument_list|,
literal|0
argument_list|,
name|patternLength
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|patternString
return|;
block|}
comment|/**          * Get the lower bound if any.          *          * @return return the lower bound, or null if unbound          */
specifier|public
name|String
name|getLowerBound
parameter_list|()
block|{
return|return
name|lowerBounds
return|;
block|}
comment|/**          * Get the upper bound if any.          *          * @return return the upper bound, or null if unbound          */
specifier|public
name|String
name|getUpperBound
parameter_list|()
block|{
return|return
name|upperBound
return|;
block|}
specifier|private
name|void
name|initBounds
parameter_list|()
block|{
if|if
condition|(
name|invalidPattern
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|patternLength
operator|<=
literal|0
operator|||
name|patternTypes
index|[
literal|0
index|]
operator|!=
name|MATCH
condition|)
block|{
comment|// can't use an index
return|return;
block|}
name|int
name|maxMatch
init|=
literal|0
decl_stmt|;
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|maxMatch
operator|<
name|patternLength
operator|&&
name|patternTypes
index|[
name|maxMatch
index|]
operator|==
name|MATCH
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|patternChars
index|[
name|maxMatch
operator|++
index|]
argument_list|)
expr_stmt|;
block|}
name|String
name|lower
init|=
name|buff
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|lower
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|maxMatch
operator|==
name|patternLength
condition|)
block|{
name|lowerBounds
operator|=
name|upperBound
operator|=
name|lower
expr_stmt|;
return|return;
block|}
name|lowerBounds
operator|=
name|lower
expr_stmt|;
name|char
name|next
init|=
name|lower
operator|.
name|charAt
argument_list|(
name|lower
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// search the 'next' unicode character (or at least a character
comment|// that is higher)
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|2000
condition|;
name|i
operator|++
control|)
block|{
name|String
name|upper
init|=
name|lower
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lower
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|+
call|(
name|char
call|)
argument_list|(
name|next
operator|+
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|upper
operator|.
name|compareTo
argument_list|(
name|lower
argument_list|)
operator|>
literal|0
condition|)
block|{
name|upperBound
operator|=
name|upper
expr_stmt|;
return|return;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

