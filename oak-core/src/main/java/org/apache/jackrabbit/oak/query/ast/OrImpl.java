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
name|Map
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
name|Lists
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
name|Maps
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
name|ConstraintImpl
name|constraint1
decl_stmt|,
name|constraint2
decl_stmt|;
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
operator|.
name|constraint1
operator|=
name|constraint1
expr_stmt|;
name|this
operator|.
name|constraint2
operator|=
name|constraint2
expr_stmt|;
block|}
specifier|public
name|ConstraintImpl
name|getConstraint1
parameter_list|()
block|{
return|return
name|constraint1
return|;
block|}
specifier|public
name|ConstraintImpl
name|getConstraint2
parameter_list|()
block|{
return|return
name|constraint2
return|;
block|}
annotation|@
name|Override
specifier|public
name|ConstraintImpl
name|simplify
parameter_list|()
block|{
name|constraint1
operator|=
name|constraint1
operator|.
name|simplify
argument_list|()
expr_stmt|;
name|constraint2
operator|=
name|constraint2
operator|.
name|simplify
argument_list|()
expr_stmt|;
if|if
condition|(
name|constraint1
operator|.
name|equals
argument_list|(
name|constraint2
argument_list|)
condition|)
block|{
return|return
name|constraint1
return|;
block|}
return|return
name|this
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
name|s1
init|=
name|constraint1
operator|.
name|getPropertyExistenceConditions
argument_list|()
decl_stmt|;
if|if
condition|(
name|s1
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|s1
return|;
block|}
name|Set
argument_list|<
name|PropertyExistenceImpl
argument_list|>
name|s2
init|=
name|constraint2
operator|.
name|getPropertyExistenceConditions
argument_list|()
decl_stmt|;
if|if
condition|(
name|s2
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|s2
return|;
block|}
return|return
name|Sets
operator|.
name|intersection
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
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
name|FullTextExpression
name|f1
init|=
name|constraint1
operator|.
name|getFullTextConstraint
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|FullTextExpression
name|f2
init|=
name|constraint2
operator|.
name|getFullTextConstraint
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|f1
operator|==
literal|null
operator|||
name|f2
operator|==
literal|null
condition|)
block|{
comment|// the full-text index can not be used for conditions of the form
comment|// "contains(a, 'x') or b=123"
return|return
literal|null
return|;
block|}
name|ArrayList
argument_list|<
name|FullTextExpression
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|FullTextExpression
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|f2
argument_list|)
expr_stmt|;
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
name|s1
init|=
name|constraint1
operator|.
name|getSelectors
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|SelectorImpl
argument_list|>
name|s2
init|=
name|constraint2
operator|.
name|getSelectors
argument_list|()
decl_stmt|;
if|if
condition|(
name|s1
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|s2
return|;
block|}
elseif|else
if|if
condition|(
name|s2
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|s1
return|;
block|}
return|return
name|Sets
operator|.
name|union
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
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
name|Map
argument_list|<
name|DynamicOperandImpl
argument_list|,
name|Set
argument_list|<
name|StaticOperandImpl
argument_list|>
argument_list|>
name|m1
init|=
name|constraint1
operator|.
name|getInMap
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|DynamicOperandImpl
argument_list|,
name|Set
argument_list|<
name|StaticOperandImpl
argument_list|>
argument_list|>
name|m2
init|=
name|constraint2
operator|.
name|getInMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|m1
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|m1
return|;
block|}
elseif|else
if|if
condition|(
name|m2
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|m2
return|;
block|}
name|Map
argument_list|<
name|DynamicOperandImpl
argument_list|,
name|Set
argument_list|<
name|StaticOperandImpl
argument_list|>
argument_list|>
name|result
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|DynamicOperandImpl
argument_list|,
name|Set
argument_list|<
name|StaticOperandImpl
argument_list|>
argument_list|>
name|e2
range|:
name|m2
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|StaticOperandImpl
argument_list|>
name|l2
init|=
name|e2
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|StaticOperandImpl
argument_list|>
name|l1
init|=
name|m1
operator|.
name|get
argument_list|(
name|e2
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|l1
operator|!=
literal|null
operator|&&
operator|!
name|l1
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|l2
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Set
argument_list|<
name|StaticOperandImpl
argument_list|>
name|list
init|=
name|Sets
operator|.
name|union
argument_list|(
name|l1
argument_list|,
name|l2
argument_list|)
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|e2
operator|.
name|getKey
argument_list|()
argument_list|,
name|list
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
name|evaluate
parameter_list|()
block|{
return|return
name|constraint1
operator|.
name|evaluate
argument_list|()
operator|||
name|constraint2
operator|.
name|evaluate
argument_list|()
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
name|protect
argument_list|(
name|constraint1
argument_list|)
operator|+
literal|" or "
operator|+
name|protect
argument_list|(
name|constraint2
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
comment|/**      * Push down the "property in(1, 2, 3)" conditions to the selector, if there      * are any that can be derived.      *       * @param s the selector      */
specifier|private
name|void
name|restrictPushDownInList
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
if|if
condition|(
name|isOnlySelector
argument_list|(
name|s
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|DynamicOperandImpl
argument_list|,
name|Set
argument_list|<
name|StaticOperandImpl
argument_list|>
argument_list|>
name|m
init|=
name|getInMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|DynamicOperandImpl
argument_list|,
name|Set
argument_list|<
name|StaticOperandImpl
argument_list|>
argument_list|>
name|e
range|:
name|m
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|StaticOperandImpl
argument_list|>
name|set
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|set
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|InImpl
name|in
init|=
operator|new
name|InImpl
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|set
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
block|}
block|}
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
comment|/**      * Check whether there are no other selectors in this "or" condition.      *       * @param s the selector      * @return true if there are no other selectors      */
specifier|private
name|boolean
name|isOnlySelector
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
name|Set
argument_list|<
name|SelectorImpl
argument_list|>
name|set
init|=
name|getSelectors
argument_list|()
decl_stmt|;
if|if
condition|(
name|set
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// conditions without selectors, for example "1=0":
comment|// the condition can be pushed down;
comment|// (currently there are no such conditions,
comment|// but in the future we might add them)
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|set
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
comment|// "x.a=1 or y.a=2" can't be pushed down to either "x" or "y"
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// exactly one selector: check if it's the right one
name|SelectorImpl
name|s2
init|=
name|set
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|s2
operator|!=
name|s
condition|)
block|{
comment|// a different selector
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

