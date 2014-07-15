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
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
name|index
operator|.
name|FilterImpl
import|;
end_import

begin_comment
comment|/**  * A "not" condition.  */
end_comment

begin_class
specifier|public
class|class
name|NotImpl
extends|extends
name|ConstraintImpl
block|{
specifier|private
name|ConstraintImpl
name|constraint
decl_stmt|;
specifier|public
name|NotImpl
parameter_list|(
name|ConstraintImpl
name|constraint
parameter_list|)
block|{
name|this
operator|.
name|constraint
operator|=
name|constraint
expr_stmt|;
block|}
specifier|public
name|ConstraintImpl
name|getConstraint
parameter_list|()
block|{
return|return
name|constraint
return|;
block|}
comment|/**      * Apply DeMorgan's Laws to push AND/OR constraints higher.      */
annotation|@
name|Override
specifier|public
name|ConstraintImpl
name|simplify
parameter_list|()
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
comment|// not (X and Y) == (not X) or (not Y)
name|AndImpl
name|and
init|=
operator|(
name|AndImpl
operator|)
name|simple
decl_stmt|;
name|List
argument_list|<
name|ConstraintImpl
argument_list|>
name|constraints
init|=
name|newArrayList
argument_list|()
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
name|constraints
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
name|constraints
argument_list|)
operator|.
name|simplify
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|simple
operator|instanceof
name|OrImpl
condition|)
block|{
comment|// not (X or Y) == (not X) and (not Y)
name|OrImpl
name|or
init|=
operator|(
name|OrImpl
operator|)
name|simple
decl_stmt|;
name|List
argument_list|<
name|ConstraintImpl
argument_list|>
name|constraints
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|ConstraintImpl
name|constraint
range|:
name|or
operator|.
name|getConstraints
argument_list|()
control|)
block|{
name|constraints
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
name|constraints
argument_list|)
operator|.
name|simplify
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|simple
operator|instanceof
name|NotImpl
condition|)
block|{
comment|// not not X == X
return|return
operator|(
operator|(
name|NotImpl
operator|)
name|simple
operator|)
operator|.
name|constraint
return|;
block|}
elseif|else
if|if
condition|(
name|simple
operator|!=
name|constraint
condition|)
block|{
return|return
operator|new
name|NotImpl
argument_list|(
name|simple
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
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
return|return
operator|!
name|constraint
operator|.
name|evaluate
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
return|return
name|Collections
operator|.
name|emptySet
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
name|constraint
operator|.
name|getSelectors
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
literal|"not "
operator|+
name|protect
argument_list|(
name|constraint
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
if|if
condition|(
name|f
operator|.
name|getSelector
argument_list|()
operator|.
name|isOuterJoinRightHandSide
argument_list|()
condition|)
block|{
comment|// we need to be careful with the condition
comment|// "NOT (property IS NOT NULL)"
comment|// (which is the same as "property IS NULL")
comment|// because this might cause an index
comment|// to ignore the join condition "property = x"
comment|// for example in:
comment|// "select * from a left outer join b on a.x = b.y
comment|// where not b.y is not null"
comment|// must not result in the index to check for
comment|// "b.y is null", because that would alter the
comment|// result
return|return;
block|}
comment|// ignore
comment|// TODO convert NOT conditions
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
comment|// ignore
comment|// TODO convert NOT conditions
block|}
block|}
end_class

end_unit

