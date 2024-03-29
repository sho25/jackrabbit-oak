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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
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
name|Lists
operator|.
name|newArrayList
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
name|Maps
operator|.
name|newLinkedHashMap
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
name|Sets
operator|.
name|newHashSet
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
name|Sets
operator|.
name|newLinkedHashSet
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
name|query
operator|.
name|ast
operator|.
name|AstElementFactory
operator|.
name|copyElementAndCheckReference
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
name|query
operator|.
name|ast
operator|.
name|Operator
operator|.
name|EQUAL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
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
operator|.
name|Entry
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
name|fulltext
operator|.
name|FullTextExpression
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
name|fulltext
operator|.
name|FullTextOr
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
comment|/**  * An "or" condition.  */
end_comment

begin_class
specifier|public
class|class
name|OrImpl
extends|extends
name|ConstraintImpl
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|ConstraintImpl
argument_list|>
name|constraints
decl_stmt|;
specifier|public
name|OrImpl
parameter_list|(
name|List
argument_list|<
name|ConstraintImpl
argument_list|>
name|constraints
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|constraints
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|constraints
operator|=
name|constraints
expr_stmt|;
block|}
specifier|public
name|OrImpl
parameter_list|(
name|ConstraintImpl
name|constraint1
parameter_list|,
name|ConstraintImpl
name|constraint2
parameter_list|)
block|{
name|this
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|constraint1
argument_list|,
name|constraint2
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|ConstraintImpl
argument_list|>
name|getConstraints
parameter_list|()
block|{
return|return
name|constraints
return|;
block|}
annotation|@
name|Override
specifier|public
name|ConstraintImpl
name|simplify
parameter_list|()
block|{
comment|// Use LinkedHashSet to eliminate duplicate constraints while keeping
comment|// the ordering for test cases (and clients?) that depend on it
name|LinkedHashSet
argument_list|<
name|ConstraintImpl
argument_list|>
name|simplified
init|=
name|newLinkedHashSet
argument_list|()
decl_stmt|;
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
comment|// keep track of changes in simplification
for|for
control|(
name|ConstraintImpl
name|constraint
range|:
name|constraints
control|)
block|{
name|ConstraintImpl
name|simple
init|=
name|constraint
operator|.
name|simplify
argument_list|()
decl_stmt|;
if|if
condition|(
name|simple
operator|instanceof
name|OrImpl
condition|)
block|{
comment|// unwind nested OR constraints
name|simplified
operator|.
name|addAll
argument_list|(
operator|(
operator|(
name|OrImpl
operator|)
name|simple
operator|)
operator|.
name|constraints
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|simplified
operator|.
name|add
argument_list|(
name|simple
argument_list|)
condition|)
block|{
comment|// check if this constraint got simplified
name|changed
operator|=
name|changed
operator|||
name|simple
operator|!=
name|constraint
expr_stmt|;
block|}
else|else
block|{
comment|// this constraint was a duplicate of a previous one
name|changed
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|LinkedHashMap
argument_list|<
name|DynamicOperandImpl
argument_list|,
name|LinkedHashSet
argument_list|<
name|StaticOperandImpl
argument_list|>
argument_list|>
name|in
init|=
name|newLinkedHashMap
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|ConstraintImpl
argument_list|>
name|iterator
init|=
name|simplified
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ConstraintImpl
name|simple
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|simple
operator|instanceof
name|ComparisonImpl
operator|&&
operator|(
operator|(
name|ComparisonImpl
operator|)
name|simple
operator|)
operator|.
name|getOperator
argument_list|()
operator|==
name|EQUAL
condition|)
block|{
name|DynamicOperandImpl
name|o
init|=
operator|(
operator|(
name|ComparisonImpl
operator|)
name|simple
operator|)
operator|.
name|getOperand1
argument_list|()
decl_stmt|;
name|LinkedHashSet
argument_list|<
name|StaticOperandImpl
argument_list|>
name|values
init|=
name|in
operator|.
name|get
argument_list|(
name|o
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|values
operator|=
name|newLinkedHashSet
argument_list|()
expr_stmt|;
name|in
operator|.
name|put
argument_list|(
name|o
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
name|values
operator|.
name|add
argument_list|(
operator|(
operator|(
name|ComparisonImpl
operator|)
name|simple
operator|)
operator|.
name|getOperand2
argument_list|()
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|simple
operator|instanceof
name|InImpl
condition|)
block|{
name|DynamicOperandImpl
name|o
init|=
operator|(
operator|(
name|InImpl
operator|)
name|simple
operator|)
operator|.
name|getOperand1
argument_list|()
decl_stmt|;
name|LinkedHashSet
argument_list|<
name|StaticOperandImpl
argument_list|>
name|values
init|=
name|in
operator|.
name|get
argument_list|(
name|o
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|values
operator|=
name|newLinkedHashSet
argument_list|()
expr_stmt|;
name|in
operator|.
name|put
argument_list|(
name|o
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
name|values
operator|.
name|addAll
argument_list|(
operator|(
operator|(
name|InImpl
operator|)
name|simple
operator|)
operator|.
name|getOperand2
argument_list|()
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
block|}
block|}
for|for
control|(
name|Entry
argument_list|<
name|DynamicOperandImpl
argument_list|,
name|LinkedHashSet
argument_list|<
name|StaticOperandImpl
argument_list|>
argument_list|>
name|entry
range|:
name|in
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|LinkedHashSet
argument_list|<
name|StaticOperandImpl
argument_list|>
name|values
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|simplified
operator|.
name|add
argument_list|(
operator|new
name|ComparisonImpl
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|EQUAL
argument_list|,
name|values
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|simplified
operator|.
name|add
argument_list|(
operator|new
name|InImpl
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|newArrayList
argument_list|(
name|values
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|simplified
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|simplified
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|changed
condition|)
block|{
return|return
operator|new
name|OrImpl
argument_list|(
name|newArrayList
argument_list|(
name|simplified
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
annotation|@
name|Override
name|ConstraintImpl
name|not
parameter_list|()
block|{
comment|// not (X or Y) == (not X) and (not Y)
name|List
argument_list|<
name|ConstraintImpl
argument_list|>
name|list
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|ConstraintImpl
name|constraint
range|:
name|getConstraints
argument_list|()
control|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|NotImpl
argument_list|(
name|constraint
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|AndImpl
argument_list|(
name|list
argument_list|)
operator|.
name|simplify
argument_list|()
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
comment|// for the condition "x=1 or x=2", the existence condition
comment|// "x is not null" be be derived
name|Set
argument_list|<
name|PropertyExistenceImpl
argument_list|>
name|result
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ConstraintImpl
name|constraint
range|:
name|constraints
control|)
block|{
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|newHashSet
argument_list|(
name|constraint
operator|.
name|getPropertyExistenceConditions
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|retainAll
argument_list|(
name|constraint
operator|.
name|getPropertyExistenceConditions
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|FullTextExpression
name|getFullTextConstraint
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
name|List
argument_list|<
name|FullTextExpression
argument_list|>
name|list
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|ConstraintImpl
name|constraint
range|:
name|constraints
control|)
block|{
name|FullTextExpression
name|expression
init|=
name|constraint
operator|.
name|getFullTextConstraint
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|expression
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|expression
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// the full-text index can not be used for conditions
comment|// of the form "contains(a, 'x') or b=123"
return|return
literal|null
return|;
block|}
block|}
return|return
operator|new
name|FullTextOr
argument_list|(
name|list
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
name|Set
argument_list|<
name|SelectorImpl
argument_list|>
name|result
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|ConstraintImpl
name|constraint
range|:
name|constraints
control|)
block|{
name|result
operator|.
name|addAll
argument_list|(
name|constraint
operator|.
name|getSelectors
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
for|for
control|(
name|ConstraintImpl
name|constraint
range|:
name|constraints
control|)
block|{
if|if
condition|(
name|constraint
operator|.
name|evaluate
argument_list|()
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
specifier|public
name|boolean
name|evaluateStop
parameter_list|()
block|{
comment|// the logic is reversed here:
comment|// we stop only if both conditions say we need to
for|for
control|(
name|ConstraintImpl
name|constraint
range|:
name|constraints
control|)
block|{
if|if
condition|(
operator|!
name|constraint
operator|.
name|evaluateStop
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
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
name|void
name|restrict
parameter_list|(
name|FilterImpl
name|f
parameter_list|)
block|{
name|Set
argument_list|<
name|PropertyExistenceImpl
argument_list|>
name|set
init|=
name|getPropertyExistenceConditions
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|set
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|PropertyExistenceImpl
name|p
range|:
name|set
control|)
block|{
name|p
operator|.
name|restrict
argument_list|(
name|f
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
name|restrictPushDownNotExists
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|restrictPushDownInList
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|/**      * Push down the "not exists" conditions to the selector.      *       * @param s the selector      */
specifier|private
name|void
name|restrictPushDownNotExists
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
name|Set
argument_list|<
name|PropertyExistenceImpl
argument_list|>
name|set
init|=
name|getPropertyExistenceConditions
argument_list|()
decl_stmt|;
if|if
condition|(
name|set
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
name|PropertyExistenceImpl
name|p
range|:
name|set
control|)
block|{
name|p
operator|.
name|restrictPushDown
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Push down the "property in(1, 2, 3)" conditions to the selector, if there      * are any that can be derived.      *       * @param s the selector      */
specifier|private
name|void
name|restrictPushDownInList
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
name|DynamicOperandImpl
name|operand
init|=
literal|null
decl_stmt|;
name|LinkedHashSet
argument_list|<
name|StaticOperandImpl
argument_list|>
name|values
init|=
name|newLinkedHashSet
argument_list|()
decl_stmt|;
name|boolean
name|multiPropertyOr
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|AndImpl
argument_list|>
name|ands
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|ConstraintImpl
name|constraint
range|:
name|constraints
control|)
block|{
name|Set
argument_list|<
name|SelectorImpl
argument_list|>
name|selectors
init|=
name|constraint
operator|.
name|getSelectors
argument_list|()
decl_stmt|;
if|if
condition|(
name|selectors
operator|.
name|size
argument_list|()
operator|!=
literal|1
operator|||
operator|!
name|selectors
operator|.
name|contains
argument_list|(
name|s
argument_list|)
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
name|constraint
operator|instanceof
name|AndImpl
condition|)
block|{
name|ands
operator|.
name|add
argument_list|(
operator|(
name|AndImpl
operator|)
name|constraint
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|constraint
operator|instanceof
name|InImpl
condition|)
block|{
name|InImpl
name|in
init|=
operator|(
name|InImpl
operator|)
name|constraint
decl_stmt|;
name|DynamicOperandImpl
name|o
init|=
name|in
operator|.
name|getOperand1
argument_list|()
decl_stmt|;
if|if
condition|(
name|operand
operator|==
literal|null
operator|||
name|operand
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|operand
operator|=
name|o
expr_stmt|;
name|values
operator|.
name|addAll
argument_list|(
name|in
operator|.
name|getOperand2
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|multiPropertyOr
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|constraint
operator|instanceof
name|ComparisonImpl
operator|&&
operator|(
operator|(
name|ComparisonImpl
operator|)
name|constraint
operator|)
operator|.
name|getOperator
argument_list|()
operator|==
name|EQUAL
condition|)
block|{
name|ComparisonImpl
name|comparison
init|=
operator|(
name|ComparisonImpl
operator|)
name|constraint
decl_stmt|;
name|DynamicOperandImpl
name|o
init|=
name|comparison
operator|.
name|getOperand1
argument_list|()
decl_stmt|;
if|if
condition|(
name|operand
operator|==
literal|null
operator|||
name|operand
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|operand
operator|=
name|o
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|comparison
operator|.
name|getOperand2
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|multiPropertyOr
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
return|return;
block|}
block|}
if|if
condition|(
name|multiPropertyOr
operator|&&
name|ands
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|s
operator|.
name|restrictSelector
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|operand
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|AndImpl
name|and
range|:
name|ands
control|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|ConstraintImpl
name|constraint
range|:
name|and
operator|.
name|getConstraints
argument_list|()
control|)
block|{
if|if
condition|(
name|constraint
operator|instanceof
name|InImpl
condition|)
block|{
name|InImpl
name|in
init|=
operator|(
name|InImpl
operator|)
name|constraint
decl_stmt|;
if|if
condition|(
name|operand
operator|.
name|equals
argument_list|(
name|in
operator|.
name|getOperand1
argument_list|()
argument_list|)
condition|)
block|{
name|values
operator|.
name|addAll
argument_list|(
name|in
operator|.
name|getOperand2
argument_list|()
argument_list|)
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
elseif|else
if|if
condition|(
name|constraint
operator|instanceof
name|ComparisonImpl
operator|&&
operator|(
operator|(
name|ComparisonImpl
operator|)
name|constraint
operator|)
operator|.
name|getOperator
argument_list|()
operator|==
name|EQUAL
condition|)
block|{
name|ComparisonImpl
name|comparison
init|=
operator|(
name|ComparisonImpl
operator|)
name|constraint
decl_stmt|;
if|if
condition|(
name|operand
operator|.
name|equals
argument_list|(
name|comparison
operator|.
name|getOperand1
argument_list|()
argument_list|)
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|comparison
operator|.
name|getOperand2
argument_list|()
argument_list|)
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
return|return;
block|}
block|}
name|InImpl
name|in
init|=
operator|new
name|InImpl
argument_list|(
name|operand
argument_list|,
name|newArrayList
argument_list|(
name|values
argument_list|)
argument_list|)
decl_stmt|;
name|in
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|in
operator|.
name|restrictPushDown
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|ConstraintImpl
name|constraint
range|:
name|constraints
control|)
block|{
if|if
condition|(
name|builder
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" or "
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
name|protect
argument_list|(
name|constraint
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|that
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|that
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|that
operator|instanceof
name|OrImpl
condition|)
block|{
return|return
name|constraints
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|OrImpl
operator|)
name|that
operator|)
operator|.
name|constraints
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|constraints
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|AstElement
name|copyOf
parameter_list|()
block|{
name|List
argument_list|<
name|ConstraintImpl
argument_list|>
name|clone
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|ConstraintImpl
name|c
range|:
name|constraints
control|)
block|{
name|clone
operator|.
name|add
argument_list|(
operator|(
name|ConstraintImpl
operator|)
name|copyElementAndCheckReference
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|OrImpl
argument_list|(
name|clone
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|ConstraintImpl
argument_list|>
name|convertToUnion
parameter_list|()
block|{
name|Set
argument_list|<
name|ConstraintImpl
argument_list|>
name|result
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|ConstraintImpl
name|c
range|:
name|getConstraints
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|ConstraintImpl
argument_list|>
name|converted
init|=
name|c
operator|.
name|convertToUnion
argument_list|()
decl_stmt|;
if|if
condition|(
name|converted
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|addAll
argument_list|(
name|converted
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|requiresFullTextIndex
parameter_list|()
block|{
for|for
control|(
name|ConstraintImpl
name|c
range|:
name|getConstraints
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|.
name|requiresFullTextIndex
argument_list|()
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
specifier|public
name|boolean
name|containsUnfilteredFullTextCondition
parameter_list|()
block|{
name|boolean
name|fulltext
init|=
literal|false
decl_stmt|;
name|boolean
name|plain
init|=
literal|false
decl_stmt|;
for|for
control|(
name|ConstraintImpl
name|c
range|:
name|constraints
control|)
block|{
comment|// this part of the condition already contains an unfiltered
comment|// condition, so we don't need to check further
if|if
condition|(
name|c
operator|.
name|containsUnfilteredFullTextCondition
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|c
operator|.
name|requiresFullTextIndex
argument_list|()
condition|)
block|{
comment|// for example "contains(a, 'x')"
name|fulltext
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// for example "b=123"
name|plain
operator|=
literal|true
expr_stmt|;
block|}
comment|// the full-text index contains both typescan not be used for conditions
comment|// of the form "contains(a, 'x') or b=123"
if|if
condition|(
name|fulltext
operator|&&
name|plain
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
block|}
end_class

end_unit

