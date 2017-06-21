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
name|Arrays
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
name|QueryEngineSettings
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
name|FullTextAnd
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
name|query
operator|.
name|QueryImpl
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
comment|/**  * An AND condition.  */
end_comment

begin_class
specifier|public
class|class
name|AndImpl
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
name|AndImpl
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
name|AndImpl
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
name|AndImpl
condition|)
block|{
comment|// unwind nested AND constraints
name|simplified
operator|.
name|addAll
argument_list|(
operator|(
operator|(
name|AndImpl
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
name|AndImpl
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
comment|// not (X and Y) == (not X) or (not Y)
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
name|constraints
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
name|OrImpl
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
name|Set
argument_list|<
name|PropertyExistenceImpl
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
name|getPropertyExistenceConditions
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
block|}
switch|switch
condition|(
name|list
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
literal|null
return|;
case|case
literal|1
case|:
return|return
name|list
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
return|;
default|default:
return|return
operator|new
name|FullTextAnd
argument_list|(
name|list
argument_list|)
return|;
block|}
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
operator|!
name|constraint
operator|.
name|evaluate
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
specifier|public
name|boolean
name|evaluateStop
parameter_list|()
block|{
comment|// the logic is reversed here:
comment|// if one of the conditions is to stop, then we stop
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
name|evaluateStop
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
for|for
control|(
name|ConstraintImpl
name|constraint
range|:
name|constraints
control|)
block|{
name|constraint
operator|.
name|restrict
argument_list|(
name|f
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
name|ConstraintImpl
name|constraint
range|:
name|constraints
control|)
block|{
name|constraint
operator|.
name|restrictPushDown
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|constraints
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|constraints
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
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
literal|" and "
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
name|AndImpl
condition|)
block|{
return|return
name|constraints
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|AndImpl
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
operator|new
name|ArrayList
argument_list|<
name|ConstraintImpl
argument_list|>
argument_list|(
name|constraints
operator|.
name|size
argument_list|()
argument_list|)
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
name|AndImpl
argument_list|(
name|clone
argument_list|)
return|;
block|}
specifier|public
name|void
name|addToUnionList
parameter_list|(
name|Set
argument_list|<
name|ConstraintImpl
argument_list|>
name|target
parameter_list|)
block|{
comment|// conditions of type
comment|// @a = 1 and (@x = 1 or @y = 2)
comment|// are automatically converted to
comment|// (@a = 1 and @x = 1) union (@a = 1 and @y = 2)
name|AndImpl
name|and
init|=
name|pullOrRight
argument_list|()
decl_stmt|;
name|ConstraintImpl
name|last
init|=
name|and
operator|.
name|getLastConstraint
argument_list|()
decl_stmt|;
if|if
condition|(
name|last
operator|instanceof
name|OrImpl
condition|)
block|{
name|OrImpl
name|or
init|=
operator|(
name|OrImpl
operator|)
name|last
decl_stmt|;
comment|// same as above, but with the added "and"
for|for
control|(
name|ConstraintImpl
name|c
range|:
name|or
operator|.
name|getConstraints
argument_list|()
control|)
block|{
name|ArrayList
argument_list|<
name|ConstraintImpl
argument_list|>
name|list
init|=
name|and
operator|.
name|getFirstConstraints
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
operator|new
name|AndImpl
argument_list|(
name|list
argument_list|)
operator|.
name|addToUnionList
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|target
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ArrayList
argument_list|<
name|ConstraintImpl
argument_list|>
name|getFirstConstraints
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|ConstraintImpl
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|ConstraintImpl
argument_list|>
argument_list|(
name|constraints
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|list
operator|.
name|addAll
argument_list|(
name|constraints
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|constraints
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|list
return|;
block|}
specifier|private
name|ConstraintImpl
name|getLastConstraint
parameter_list|()
block|{
return|return
name|constraints
operator|.
name|get
argument_list|(
name|constraints
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|public
name|AndImpl
name|pullOrRight
parameter_list|()
block|{
if|if
condition|(
name|getLastConstraint
argument_list|()
operator|instanceof
name|OrImpl
condition|)
block|{
return|return
name|this
return|;
block|}
name|ArrayList
argument_list|<
name|ConstraintImpl
argument_list|>
name|andList
init|=
name|getAllAndConditions
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
name|andList
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|ConstraintImpl
name|c
init|=
name|andList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|instanceof
name|OrImpl
condition|)
block|{
name|ArrayList
argument_list|<
name|ConstraintImpl
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|ConstraintImpl
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|addAll
argument_list|(
name|andList
argument_list|)
expr_stmt|;
name|list
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
operator|new
name|AndImpl
argument_list|(
name|list
argument_list|)
return|;
block|}
block|}
return|return
name|this
return|;
block|}
specifier|private
name|ArrayList
argument_list|<
name|ConstraintImpl
argument_list|>
name|getAllAndConditions
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|ConstraintImpl
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|ConstraintImpl
argument_list|>
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
if|if
condition|(
name|c
operator|instanceof
name|AndImpl
condition|)
block|{
name|list
operator|.
name|addAll
argument_list|(
operator|(
operator|(
name|AndImpl
operator|)
name|c
operator|)
operator|.
name|getAllAndConditions
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|list
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
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
comment|// use linked hash sets where needed, so that the order of queries
comment|// within the UNION is always the same (independent of the JVM
comment|// implementation)
name|Set
argument_list|<
name|ConstraintImpl
argument_list|>
name|union
init|=
name|Sets
operator|.
name|newLinkedHashSet
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|ConstraintImpl
argument_list|>
name|result
init|=
name|Sets
operator|.
name|newLinkedHashSet
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|ConstraintImpl
argument_list|>
name|nonUnion
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
name|constraints
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
name|nonUnion
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|union
operator|.
name|addAll
argument_list|(
name|converted
argument_list|)
expr_stmt|;
if|if
condition|(
name|union
operator|.
name|size
argument_list|()
operator|>
name|QueryImpl
operator|.
name|MAX_UNION
condition|)
block|{
throw|throw
name|QueryImpl
operator|.
name|TOO_MANY_UNION
throw|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|union
operator|.
name|isEmpty
argument_list|()
operator|&&
name|nonUnion
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// this is the simplest case where, for example, out of the two AND operands at least
comment|// one is a non-union. For example WHERE (a OR b OR c) AND d
name|ConstraintImpl
name|right
init|=
name|nonUnion
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
for|for
control|(
name|ConstraintImpl
name|c
range|:
name|union
control|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|AndImpl
argument_list|(
name|c
argument_list|,
name|right
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// This could happen when for
comment|// example: WHERE (a OR b) AND (c OR d).
comment|// This can be translated into a AND c, a AND d, b AND c, b AND d.
if|if
condition|(
name|QueryEngineSettings
operator|.
name|SQL2_OPTIMIZATION_2
condition|)
block|{
name|Set
argument_list|<
name|ConstraintImpl
argument_list|>
name|set
init|=
name|Sets
operator|.
name|newLinkedHashSet
argument_list|()
decl_stmt|;
name|addToUnionList
argument_list|(
name|set
argument_list|)
expr_stmt|;
if|if
condition|(
name|set
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// not a union: same condition as before
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
return|return
name|set
return|;
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
name|constraints
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
for|for
control|(
name|ConstraintImpl
name|c
range|:
name|constraints
control|)
block|{
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
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

