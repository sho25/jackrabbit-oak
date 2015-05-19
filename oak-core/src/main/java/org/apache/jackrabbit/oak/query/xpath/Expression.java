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
operator|.
name|xpath
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
name|SQL2Parser
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
name|util
operator|.
name|ISO9075
import|;
end_import

begin_comment
comment|/**  * An expression.  */
end_comment

begin_class
specifier|abstract
class|class
name|Expression
block|{
specifier|static
specifier|final
name|int
name|PRECEDENCE_OR
init|=
literal|1
decl_stmt|,
name|PRECEDENCE_AND
init|=
literal|2
decl_stmt|,
name|PRECEDENCE_CONDITION
init|=
literal|3
decl_stmt|,
name|PRECEDENCE_OPERAND
init|=
literal|4
decl_stmt|;
comment|/**      * The "and" combination of two conditions.      *       * @param old the first expression (may be null)      * @param add the second expression (may be null)      * @return the combined expression (may be null)      */
specifier|public
specifier|static
name|Expression
name|and
parameter_list|(
name|Expression
name|old
parameter_list|,
name|Expression
name|add
parameter_list|)
block|{
if|if
condition|(
name|old
operator|==
literal|null
condition|)
block|{
return|return
name|add
return|;
block|}
elseif|else
if|if
condition|(
name|add
operator|==
literal|null
condition|)
block|{
return|return
name|old
return|;
block|}
return|return
operator|new
name|Expression
operator|.
name|AndCondition
argument_list|(
name|old
argument_list|,
name|add
argument_list|)
return|;
block|}
comment|/**      * Get the optimized expression.      *       * @return the optimized expression      */
name|Expression
name|optimize
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/**      * Whether this is a condition.      *       * @return true if it is       */
name|boolean
name|isCondition
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Whether this is a or contains a full-text condition.      *       * @return true if it is      */
name|boolean
name|containsFullTextCondition
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Get the left-hand-side expression for equality conditions.       * For example, for x=1, it is x. If it is not equality, return null.      *       * @return the left-hand-side expression, or null      */
name|String
name|getCommonLeftPart
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Get the left hand side of an expression.      *       * @return the left hand side      */
name|Expression
name|getLeft
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Get the list of the right hand side of an expression.      *       * @return the list      */
name|List
argument_list|<
name|Expression
argument_list|>
name|getRight
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Pull an OR condition up to the right hand side of an AND condition.      *       * @return the (possibly rotated) expression      */
name|Expression
name|pullOrRight
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/**      * Get the operator / operation precedence. The JCR specification uses:      * 1=OR, 2=AND, 3=condition, 4=operand        *       * @return the precedence (as an example, multiplication needs to return      *         a higher number than addition)      */
name|int
name|getPrecedence
parameter_list|()
block|{
return|return
name|PRECEDENCE_OPERAND
return|;
block|}
comment|/**      * Get the column alias name of an expression. For a property, this is the      * property name (no matter how many selectors the query contains); for      * other expressions it matches the toString() method.      *       * @return the simple column name      */
name|String
name|getColumnAliasName
parameter_list|()
block|{
return|return
name|toString
argument_list|()
return|;
block|}
comment|/**      * Whether the result of this expression is a name. Names are subject to      * ISO9075 encoding.      *       * @return whether this expression is a name.      */
name|boolean
name|isName
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * A literal expression.      */
specifier|static
class|class
name|Literal
extends|extends
name|Expression
block|{
specifier|final
name|String
name|value
decl_stmt|;
specifier|final
name|String
name|rawText
decl_stmt|;
name|Literal
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|rawText
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|rawText
operator|=
name|rawText
expr_stmt|;
block|}
specifier|public
specifier|static
name|Expression
name|newBoolean
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
return|return
operator|new
name|Literal
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
specifier|static
name|Literal
name|newNumber
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
operator|new
name|Literal
argument_list|(
name|s
argument_list|,
name|s
argument_list|)
return|;
block|}
specifier|static
name|Literal
name|newString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
operator|new
name|Literal
argument_list|(
name|SQL2Parser
operator|.
name|escapeStringLiteral
argument_list|(
name|s
argument_list|)
argument_list|,
name|s
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
name|value
return|;
block|}
block|}
comment|/**      * A condition.      */
specifier|static
class|class
name|Condition
extends|extends
name|Expression
block|{
specifier|final
name|Expression
name|left
decl_stmt|;
specifier|final
name|String
name|operator
decl_stmt|;
name|Expression
name|right
decl_stmt|;
specifier|final
name|int
name|precedence
decl_stmt|;
comment|/**          * Create a new condition.          *           * @param left the left hand side operator, or null          * @param operator the operator          * @param right the right hand side operator, or null          * @param precedence the operator precedence (Expression.PRECEDENCE_...)          */
name|Condition
parameter_list|(
name|Expression
name|left
parameter_list|,
name|String
name|operator
parameter_list|,
name|Expression
name|right
parameter_list|,
name|int
name|precedence
parameter_list|)
block|{
name|this
operator|.
name|left
operator|=
name|left
expr_stmt|;
name|this
operator|.
name|operator
operator|=
name|operator
expr_stmt|;
name|this
operator|.
name|right
operator|=
name|right
expr_stmt|;
name|this
operator|.
name|precedence
operator|=
name|precedence
expr_stmt|;
block|}
annotation|@
name|Override
name|int
name|getPrecedence
parameter_list|()
block|{
return|return
name|precedence
return|;
block|}
annotation|@
name|Override
name|String
name|getCommonLeftPart
parameter_list|()
block|{
if|if
condition|(
operator|!
literal|"="
operator|.
name|equals
argument_list|(
name|operator
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|left
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
name|Expression
name|getLeft
parameter_list|()
block|{
return|return
name|left
return|;
block|}
annotation|@
name|Override
name|List
argument_list|<
name|Expression
argument_list|>
name|getRight
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|right
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
name|String
name|leftExpr
decl_stmt|;
name|boolean
name|leftExprIsName
decl_stmt|;
if|if
condition|(
name|left
operator|==
literal|null
condition|)
block|{
name|leftExprIsName
operator|=
literal|false
expr_stmt|;
name|leftExpr
operator|=
literal|""
expr_stmt|;
block|}
else|else
block|{
name|leftExprIsName
operator|=
name|left
operator|.
name|isName
argument_list|()
expr_stmt|;
name|leftExpr
operator|=
name|left
operator|.
name|toString
argument_list|()
expr_stmt|;
if|if
condition|(
name|left
operator|.
name|getPrecedence
argument_list|()
operator|<
name|precedence
condition|)
block|{
name|leftExpr
operator|=
literal|"("
operator|+
name|leftExpr
operator|+
literal|")"
expr_stmt|;
block|}
block|}
name|boolean
name|impossible
init|=
literal|false
decl_stmt|;
name|String
name|rightExpr
decl_stmt|;
if|if
condition|(
name|right
operator|==
literal|null
condition|)
block|{
name|rightExpr
operator|=
literal|""
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|leftExprIsName
operator|&&
operator|!
literal|"like"
operator|.
name|equals
argument_list|(
name|operator
argument_list|)
condition|)
block|{
comment|// need to de-escape _x0020_ and so on
if|if
condition|(
operator|!
operator|(
name|right
operator|instanceof
name|Literal
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can only compare a name against a string literal, not "
operator|+
name|right
argument_list|)
throw|;
block|}
name|Literal
name|l
init|=
operator|(
name|Literal
operator|)
name|right
decl_stmt|;
name|String
name|raw
init|=
name|l
operator|.
name|rawText
decl_stmt|;
name|String
name|decoded
init|=
name|ISO9075
operator|.
name|decode
argument_list|(
name|raw
argument_list|)
decl_stmt|;
name|String
name|encoded
init|=
name|ISO9075
operator|.
name|encode
argument_list|(
name|decoded
argument_list|)
decl_stmt|;
name|rightExpr
operator|=
name|SQL2Parser
operator|.
name|escapeStringLiteral
argument_list|(
name|decoded
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|encoded
operator|.
name|equalsIgnoreCase
argument_list|(
name|raw
argument_list|)
condition|)
block|{
comment|// nothing can potentially match
name|impossible
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
name|rightExpr
operator|=
name|right
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|right
operator|.
name|getPrecedence
argument_list|()
operator|<
name|precedence
condition|)
block|{
name|rightExpr
operator|=
literal|"("
operator|+
name|right
operator|+
literal|")"
expr_stmt|;
block|}
block|}
if|if
condition|(
name|impossible
condition|)
block|{
comment|// a condition that can not possibly be true
return|return
literal|"upper("
operator|+
name|leftExpr
operator|+
literal|") = 'never matches'"
return|;
block|}
return|return
operator|(
name|leftExpr
operator|+
literal|" "
operator|+
name|operator
operator|+
literal|" "
operator|+
name|rightExpr
operator|)
operator|.
name|trim
argument_list|()
return|;
block|}
annotation|@
name|Override
name|boolean
name|isCondition
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
name|Expression
name|optimize
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
comment|/**      * An "or" condition.      */
specifier|static
class|class
name|OrCondition
extends|extends
name|Condition
block|{
name|OrCondition
parameter_list|(
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|)
block|{
name|super
argument_list|(
name|left
argument_list|,
literal|"or"
argument_list|,
name|right
argument_list|,
name|Expression
operator|.
name|PRECEDENCE_OR
argument_list|)
expr_stmt|;
block|}
comment|/**          * Get the left-hand-side expression if it is the same for          * both sides. For example, for x=1 or x=2, it is x,          * but for x=1 or y=2, it is null          *           * @return the left-hand-side expression, or null          */
annotation|@
name|Override
specifier|public
name|String
name|getCommonLeftPart
parameter_list|()
block|{
name|String
name|l
init|=
name|left
operator|.
name|getCommonLeftPart
argument_list|()
decl_stmt|;
name|String
name|r
init|=
name|right
operator|.
name|getCommonLeftPart
argument_list|()
decl_stmt|;
if|if
condition|(
name|l
operator|!=
literal|null
operator|&&
name|r
operator|!=
literal|null
operator|&&
name|l
operator|.
name|equals
argument_list|(
name|r
argument_list|)
condition|)
block|{
return|return
name|l
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
name|Expression
name|optimize
parameter_list|()
block|{
name|Expression
name|l
init|=
name|left
operator|.
name|optimize
argument_list|()
decl_stmt|;
name|Expression
name|r
init|=
name|right
operator|.
name|optimize
argument_list|()
decl_stmt|;
if|if
condition|(
name|l
operator|!=
name|left
operator|||
name|r
operator|!=
name|right
condition|)
block|{
return|return
operator|new
name|OrCondition
argument_list|(
name|l
argument_list|,
name|r
argument_list|)
operator|.
name|optimize
argument_list|()
return|;
block|}
name|String
name|commonLeft
init|=
name|getCommonLeftPart
argument_list|()
decl_stmt|;
if|if
condition|(
name|commonLeft
operator|==
literal|null
condition|)
block|{
comment|// the case:
comment|// (other>0 or x=1) or x=2
comment|// can be converted to:
comment|// other>0 or (x=1 or x=2)
comment|// which can then be optimized
if|if
condition|(
name|left
operator|instanceof
name|OrCondition
condition|)
block|{
name|OrCondition
name|orLeft
init|=
operator|(
name|OrCondition
operator|)
name|left
decl_stmt|;
name|Expression
name|l1
init|=
name|orLeft
operator|.
name|left
decl_stmt|;
name|Expression
name|l2
init|=
name|orLeft
operator|.
name|right
decl_stmt|;
name|OrCondition
name|orRight
init|=
operator|new
name|OrCondition
argument_list|(
name|l2
argument_list|,
name|right
argument_list|)
decl_stmt|;
name|Expression
name|o2
init|=
name|orRight
operator|.
name|optimize
argument_list|()
decl_stmt|;
if|if
condition|(
name|o2
operator|!=
name|orRight
condition|)
block|{
return|return
operator|new
name|OrCondition
argument_list|(
name|l1
argument_list|,
name|o2
argument_list|)
return|;
block|}
block|}
return|return
name|this
return|;
block|}
comment|// "@x = 1 or @x = 2" is converted to "@x in (1, 2)"
if|if
condition|(
name|left
operator|instanceof
name|InCondition
condition|)
block|{
name|InCondition
name|in
init|=
operator|(
name|InCondition
operator|)
name|left
decl_stmt|;
name|in
operator|.
name|list
operator|.
name|addAll
argument_list|(
name|right
operator|.
name|getRight
argument_list|()
argument_list|)
expr_stmt|;
comment|// return a new instance, because we changed
comment|// the list
name|in
operator|=
operator|new
name|InCondition
argument_list|(
name|in
operator|.
name|getLeft
argument_list|()
argument_list|,
name|in
operator|.
name|list
argument_list|)
expr_stmt|;
return|return
name|in
return|;
block|}
name|ArrayList
argument_list|<
name|Expression
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|addAll
argument_list|(
name|left
operator|.
name|getRight
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|addAll
argument_list|(
name|right
operator|.
name|getRight
argument_list|()
argument_list|)
expr_stmt|;
name|Expression
name|le
init|=
name|left
operator|.
name|getLeft
argument_list|()
decl_stmt|;
name|InCondition
name|in
init|=
operator|new
name|InCondition
argument_list|(
name|le
argument_list|,
name|list
argument_list|)
decl_stmt|;
return|return
name|in
return|;
block|}
annotation|@
name|Override
name|boolean
name|containsFullTextCondition
parameter_list|()
block|{
return|return
name|left
operator|.
name|containsFullTextCondition
argument_list|()
operator|||
name|right
operator|.
name|containsFullTextCondition
argument_list|()
return|;
block|}
block|}
comment|/**      * An "or" condition.      */
specifier|static
class|class
name|InCondition
extends|extends
name|Expression
block|{
specifier|final
name|Expression
name|left
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|list
decl_stmt|;
name|InCondition
parameter_list|(
name|Expression
name|left
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|list
parameter_list|)
block|{
name|this
operator|.
name|left
operator|=
name|left
expr_stmt|;
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
block|}
annotation|@
name|Override
name|String
name|getCommonLeftPart
parameter_list|()
block|{
return|return
name|left
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
name|Expression
name|getLeft
parameter_list|()
block|{
return|return
name|left
return|;
block|}
annotation|@
name|Override
name|List
argument_list|<
name|Expression
argument_list|>
name|getRight
parameter_list|()
block|{
return|return
name|list
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
name|left
argument_list|)
operator|.
name|append
argument_list|(
literal|" in("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
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
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|buff
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
name|boolean
name|isCondition
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
comment|/**      * An "and" condition.      */
specifier|static
class|class
name|AndCondition
extends|extends
name|Condition
block|{
name|AndCondition
parameter_list|(
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|)
block|{
name|super
argument_list|(
name|left
argument_list|,
literal|"and"
argument_list|,
name|right
argument_list|,
name|Expression
operator|.
name|PRECEDENCE_AND
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|Expression
name|optimize
parameter_list|()
block|{
name|Expression
name|l
init|=
name|left
operator|.
name|optimize
argument_list|()
decl_stmt|;
name|Expression
name|r
init|=
name|right
operator|.
name|optimize
argument_list|()
decl_stmt|;
if|if
condition|(
name|l
operator|!=
name|left
operator|||
name|r
operator|!=
name|right
condition|)
block|{
return|return
operator|new
name|AndCondition
argument_list|(
name|l
argument_list|,
name|r
argument_list|)
return|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
name|AndCondition
name|pullOrRight
parameter_list|()
block|{
if|if
condition|(
name|right
operator|instanceof
name|OrCondition
condition|)
block|{
return|return
name|this
return|;
block|}
elseif|else
if|if
condition|(
name|left
operator|instanceof
name|OrCondition
condition|)
block|{
return|return
operator|new
name|AndCondition
argument_list|(
name|right
argument_list|,
name|left
argument_list|)
return|;
block|}
if|if
condition|(
name|right
operator|instanceof
name|AndCondition
condition|)
block|{
comment|// pull up x:
comment|// a and (b and (x)) -> (a and b) and (x)
name|AndCondition
name|r2
init|=
operator|(
name|AndCondition
operator|)
name|right
decl_stmt|;
name|r2
operator|=
name|r2
operator|.
name|pullOrRight
argument_list|()
expr_stmt|;
name|AndCondition
name|l2
init|=
operator|new
name|AndCondition
argument_list|(
name|left
argument_list|,
name|r2
operator|.
name|left
argument_list|)
decl_stmt|;
name|l2
operator|=
name|l2
operator|.
name|pullOrRight
argument_list|()
expr_stmt|;
return|return
operator|new
name|AndCondition
argument_list|(
name|l2
argument_list|,
name|r2
operator|.
name|right
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|left
operator|instanceof
name|AndCondition
condition|)
block|{
return|return
operator|new
name|AndCondition
argument_list|(
name|right
argument_list|,
name|left
argument_list|)
operator|.
name|pullOrRight
argument_list|()
return|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
name|boolean
name|containsFullTextCondition
parameter_list|()
block|{
return|return
name|left
operator|.
name|containsFullTextCondition
argument_list|()
operator|||
name|right
operator|.
name|containsFullTextCondition
argument_list|()
return|;
block|}
block|}
comment|/**      * A contains call.      */
specifier|static
class|class
name|Contains
extends|extends
name|Expression
block|{
specifier|final
name|Expression
name|left
decl_stmt|,
name|right
decl_stmt|;
name|Contains
parameter_list|(
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|)
block|{
name|this
operator|.
name|left
operator|=
name|left
expr_stmt|;
name|this
operator|.
name|right
operator|=
name|right
expr_stmt|;
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
argument_list|(
literal|"contains("
argument_list|)
decl_stmt|;
name|Expression
name|l
init|=
name|left
decl_stmt|;
if|if
condition|(
name|l
operator|instanceof
name|Property
condition|)
block|{
name|Property
name|p
init|=
operator|(
name|Property
operator|)
name|l
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|thereWasNoAt
condition|)
block|{
name|l
operator|=
operator|new
name|Property
argument_list|(
name|p
operator|.
name|selector
argument_list|,
name|p
operator|.
name|name
operator|+
literal|"/*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
name|buff
operator|.
name|append
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|right
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
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
name|boolean
name|isCondition
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
name|boolean
name|containsFullTextCondition
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
name|boolean
name|isName
parameter_list|()
block|{
return|return
name|left
operator|.
name|isName
argument_list|()
return|;
block|}
block|}
comment|/**      * A native call.      */
specifier|static
class|class
name|NativeFunction
extends|extends
name|Expression
block|{
specifier|final
name|String
name|selector
decl_stmt|;
specifier|final
name|Expression
name|language
decl_stmt|,
name|expression
decl_stmt|;
name|NativeFunction
parameter_list|(
name|String
name|selector
parameter_list|,
name|Expression
name|language
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
name|this
operator|.
name|language
operator|=
name|language
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
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
argument_list|(
literal|"native("
argument_list|)
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|selector
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|language
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|expression
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
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
name|boolean
name|isCondition
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
name|boolean
name|containsFullTextCondition
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
name|boolean
name|isName
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * A rep:similar condition.      */
specifier|static
class|class
name|Similar
extends|extends
name|Expression
block|{
specifier|final
name|Expression
name|property
decl_stmt|,
name|path
decl_stmt|;
name|Similar
parameter_list|(
name|Expression
name|property
parameter_list|,
name|Expression
name|path
parameter_list|)
block|{
name|this
operator|.
name|property
operator|=
name|property
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
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
argument_list|(
literal|"similar("
argument_list|)
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
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
name|boolean
name|isCondition
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
name|boolean
name|isName
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * A rep:spellcheck condition.      */
specifier|static
class|class
name|Spellcheck
extends|extends
name|Expression
block|{
specifier|final
name|Expression
name|term
decl_stmt|;
name|Spellcheck
parameter_list|(
name|Expression
name|term
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
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
argument_list|(
literal|"spellcheck("
argument_list|)
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|')'
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
name|boolean
name|isCondition
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
name|boolean
name|isName
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * A rep:suggest condition.      */
specifier|static
class|class
name|Suggest
extends|extends
name|Expression
block|{
specifier|final
name|Expression
name|term
decl_stmt|;
name|Suggest
parameter_list|(
name|Expression
name|term
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
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
argument_list|(
literal|"suggest("
argument_list|)
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|')'
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
name|boolean
name|isCondition
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
name|boolean
name|isName
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * A function call.      */
specifier|static
class|class
name|Function
extends|extends
name|Expression
block|{
specifier|final
name|String
name|name
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|Expression
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
name|Function
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
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
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
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
name|params
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|')'
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
name|boolean
name|isCondition
parameter_list|()
block|{
return|return
name|name
operator|.
name|equals
argument_list|(
literal|"contains"
argument_list|)
operator|||
name|name
operator|.
name|equals
argument_list|(
literal|"not"
argument_list|)
return|;
block|}
annotation|@
name|Override
name|boolean
name|isName
parameter_list|()
block|{
if|if
condition|(
literal|"upper"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
literal|"lower"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|params
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isName
argument_list|()
return|;
block|}
return|return
literal|"name"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
comment|/**      * A cast operation.      */
specifier|static
class|class
name|Cast
extends|extends
name|Expression
block|{
specifier|final
name|Expression
name|expr
decl_stmt|;
specifier|final
name|String
name|type
decl_stmt|;
name|Cast
parameter_list|(
name|Expression
name|expr
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|expr
operator|=
name|expr
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
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
argument_list|(
literal|"cast("
argument_list|)
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|expr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|" as "
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
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
name|boolean
name|isCondition
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * A selector parameter.      */
specifier|static
class|class
name|SelectorExpr
extends|extends
name|Expression
block|{
specifier|private
specifier|final
name|Selector
name|selector
decl_stmt|;
name|SelectorExpr
parameter_list|(
name|Selector
name|selector
parameter_list|)
block|{
name|this
operator|.
name|selector
operator|=
name|selector
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
name|selector
operator|.
name|name
return|;
block|}
block|}
comment|/**      * A property expression.      */
specifier|static
class|class
name|Property
extends|extends
name|Expression
block|{
specifier|final
name|Selector
name|selector
decl_stmt|;
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|String
name|cacheString
decl_stmt|;
specifier|private
name|boolean
name|cacheOnlySelector
decl_stmt|;
comment|/**          * If there was no "@" character in front of the property name. If that          * was the case, then it is still considered a property, except for          * "contains(x, 'y')", where "x" is considered to be a node.          */
specifier|final
name|boolean
name|thereWasNoAt
decl_stmt|;
name|Property
parameter_list|(
name|Selector
name|selector
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|thereWasNoAt
parameter_list|)
block|{
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|thereWasNoAt
operator|=
name|thereWasNoAt
expr_stmt|;
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
name|cacheString
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|cacheOnlySelector
operator|==
name|selector
operator|.
name|onlySelector
condition|)
block|{
return|return
name|cacheString
return|;
block|}
block|}
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|selector
operator|.
name|onlySelector
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|selector
operator|.
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
name|cacheString
operator|=
name|buff
operator|.
name|toString
argument_list|()
expr_stmt|;
name|cacheOnlySelector
operator|=
name|selector
operator|.
name|onlySelector
expr_stmt|;
return|return
name|cacheString
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getColumnAliasName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
block|}
end_class

end_unit

