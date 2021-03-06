begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law  * or agreed to in writing, software distributed under the License is  * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
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
name|api
operator|.
name|Result
operator|.
name|SizePrecision
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
name|plan
operator|.
name|ExecutionPlan
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
name|plan
operator|.
name|JoinExecutionPlan
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
name|Filter
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
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * A join. This object contains the left hand side source, the right hand side  * source, the join type, and the join condition.  */
end_comment

begin_class
specifier|public
class|class
name|JoinImpl
extends|extends
name|SourceImpl
block|{
specifier|private
specifier|final
name|JoinConditionImpl
name|joinCondition
decl_stmt|;
specifier|private
name|JoinType
name|joinType
decl_stmt|;
specifier|private
name|SourceImpl
name|left
decl_stmt|;
specifier|private
name|SourceImpl
name|right
decl_stmt|;
specifier|private
name|boolean
name|leftNeedExecute
decl_stmt|,
name|rightNeedExecute
decl_stmt|;
specifier|private
name|boolean
name|leftNeedNext
decl_stmt|;
specifier|private
name|boolean
name|foundJoinedRow
decl_stmt|;
specifier|private
name|boolean
name|end
decl_stmt|;
specifier|private
name|NodeState
name|rootState
decl_stmt|;
specifier|private
name|JoinExecutionPlan
name|plan
decl_stmt|;
specifier|public
name|JoinImpl
parameter_list|(
name|SourceImpl
name|left
parameter_list|,
name|SourceImpl
name|right
parameter_list|,
name|JoinType
name|joinType
parameter_list|,
name|JoinConditionImpl
name|joinCondition
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
name|this
operator|.
name|joinType
operator|=
name|joinType
expr_stmt|;
name|this
operator|.
name|joinCondition
operator|=
name|joinCondition
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ArrayList
argument_list|<
name|SourceImpl
argument_list|>
name|getInnerJoinSelectors
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|SourceImpl
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|SourceImpl
argument_list|>
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|joinType
condition|)
block|{
case|case
name|INNER
case|:
name|list
operator|.
name|addAll
argument_list|(
name|left
operator|.
name|getInnerJoinSelectors
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|addAll
argument_list|(
name|right
operator|.
name|getInnerJoinSelectors
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|LEFT_OUTER
case|:
name|list
operator|.
name|addAll
argument_list|(
name|left
operator|.
name|getInnerJoinSelectors
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|RIGHT_OUTER
case|:
name|list
operator|.
name|addAll
argument_list|(
name|right
operator|.
name|getInnerJoinSelectors
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|JoinConditionImpl
argument_list|>
name|getInnerJoinConditions
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|JoinConditionImpl
argument_list|>
name|set
init|=
operator|new
name|ArrayList
argument_list|<
name|JoinConditionImpl
argument_list|>
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|joinType
condition|)
block|{
case|case
name|INNER
case|:
name|set
operator|.
name|add
argument_list|(
name|joinCondition
argument_list|)
expr_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|left
operator|.
name|getInnerJoinConditions
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|right
operator|.
name|getInnerJoinConditions
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
return|return
name|set
return|;
block|}
specifier|public
name|JoinConditionImpl
name|getJoinCondition
parameter_list|()
block|{
return|return
name|joinCondition
return|;
block|}
specifier|public
name|SourceImpl
name|getLeft
parameter_list|()
block|{
return|return
name|left
return|;
block|}
specifier|public
name|SourceImpl
name|getRight
parameter_list|()
block|{
return|return
name|right
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
name|getPlan
parameter_list|(
name|NodeState
name|rootState
parameter_list|)
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
operator|.
name|getPlan
argument_list|(
name|rootState
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|joinType
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|right
operator|.
name|getPlan
argument_list|(
name|rootState
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|" on "
argument_list|)
operator|.
name|append
argument_list|(
name|joinCondition
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
name|String
name|getIndexCostInfo
parameter_list|(
name|NodeState
name|rootState
parameter_list|)
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
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"{ "
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|left
operator|.
name|getIndexCostInfo
argument_list|(
name|rootState
argument_list|)
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|right
operator|.
name|getIndexCostInfo
argument_list|(
name|rootState
argument_list|)
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|" }"
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
name|String
name|toString
parameter_list|()
block|{
return|return
name|left
operator|+
literal|" "
operator|+
name|joinType
operator|+
literal|" "
operator|+
name|right
operator|+
literal|" on "
operator|+
name|joinCondition
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unprepare
parameter_list|()
block|{
name|left
operator|.
name|unprepare
argument_list|()
expr_stmt|;
name|right
operator|.
name|unprepare
argument_list|()
expr_stmt|;
name|plan
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
name|void
name|applyJoinConditions
parameter_list|()
block|{
switch|switch
condition|(
name|joinType
condition|)
block|{
case|case
name|INNER
case|:
name|left
operator|.
name|addJoinCondition
argument_list|(
name|joinCondition
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|right
operator|.
name|addJoinCondition
argument_list|(
name|joinCondition
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|LEFT_OUTER
case|:
name|left
operator|.
name|setOuterJoin
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|right
operator|.
name|setOuterJoin
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|left
operator|.
name|addJoinCondition
argument_list|(
name|joinCondition
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|right
operator|.
name|addJoinCondition
argument_list|(
name|joinCondition
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|RIGHT_OUTER
case|:
comment|// swap left and right
comment|// TODO right outer join: verify whether converting
comment|// to left outer join is always correct (given the current restrictions)
name|joinType
operator|=
name|JoinType
operator|.
name|LEFT_OUTER
expr_stmt|;
name|SourceImpl
name|temp
init|=
name|left
decl_stmt|;
name|left
operator|=
name|right
expr_stmt|;
name|right
operator|=
name|temp
expr_stmt|;
name|left
operator|.
name|setOuterJoin
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|right
operator|.
name|setOuterJoin
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|left
operator|.
name|addJoinCondition
argument_list|(
name|joinCondition
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|right
operator|.
name|addJoinCondition
argument_list|(
name|joinCondition
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|prepare
parameter_list|(
name|ExecutionPlan
name|p
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|p
operator|instanceof
name|JoinExecutionPlan
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not a join plan"
argument_list|)
throw|;
block|}
name|JoinExecutionPlan
name|joinPlan
init|=
operator|(
name|JoinExecutionPlan
operator|)
name|p
decl_stmt|;
if|if
condition|(
name|joinPlan
operator|.
name|getJoin
argument_list|()
operator|!=
name|this
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not a plan for this join"
argument_list|)
throw|;
block|}
name|this
operator|.
name|plan
operator|=
name|joinPlan
expr_stmt|;
name|applyJoinConditions
argument_list|()
expr_stmt|;
name|left
operator|.
name|prepare
argument_list|(
name|joinPlan
operator|.
name|getLeftPlan
argument_list|()
argument_list|)
expr_stmt|;
name|right
operator|.
name|prepare
argument_list|(
name|joinPlan
operator|.
name|getRightPlan
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ExecutionPlan
name|prepare
parameter_list|()
block|{
if|if
condition|(
name|plan
operator|!=
literal|null
condition|)
block|{
return|return
name|plan
return|;
block|}
name|applyJoinConditions
argument_list|()
expr_stmt|;
comment|// the estimated cost is the cost of the left selector,
comment|// plus twice the cost of the right selector (we expect
comment|// two rows for the right selector for each node
comment|// on the left selector)
name|ExecutionPlan
name|leftPlan
init|=
name|left
operator|.
name|prepare
argument_list|()
decl_stmt|;
name|ExecutionPlan
name|rightPlan
init|=
name|right
operator|.
name|prepare
argument_list|()
decl_stmt|;
name|double
name|cost
init|=
name|leftPlan
operator|.
name|getEstimatedCost
argument_list|()
operator|+
literal|2
operator|*
name|rightPlan
operator|.
name|getEstimatedCost
argument_list|()
decl_stmt|;
name|plan
operator|=
operator|new
name|JoinExecutionPlan
argument_list|(
name|this
argument_list|,
name|leftPlan
argument_list|,
name|rightPlan
argument_list|,
name|cost
argument_list|)
expr_stmt|;
return|return
name|plan
return|;
block|}
annotation|@
name|Override
specifier|public
name|SelectorImpl
name|getSelector
parameter_list|(
name|String
name|selectorName
parameter_list|)
block|{
name|SelectorImpl
name|s
init|=
name|left
operator|.
name|getSelector
argument_list|(
name|selectorName
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
name|s
operator|=
name|right
operator|.
name|getSelector
argument_list|(
name|selectorName
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|NodeState
name|rootState
parameter_list|)
block|{
name|this
operator|.
name|rootState
operator|=
name|rootState
expr_stmt|;
name|leftNeedExecute
operator|=
literal|true
expr_stmt|;
name|end
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Filter
name|createFilter
parameter_list|(
name|boolean
name|preparing
parameter_list|)
block|{
comment|// TODO is a join filter needed?
return|return
name|left
operator|.
name|createFilter
argument_list|(
name|preparing
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setQueryConstraint
parameter_list|(
name|ConstraintImpl
name|queryConstraint
parameter_list|)
block|{
name|left
operator|.
name|setQueryConstraint
argument_list|(
name|queryConstraint
argument_list|)
expr_stmt|;
name|right
operator|.
name|setQueryConstraint
argument_list|(
name|queryConstraint
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOuterJoin
parameter_list|(
name|boolean
name|outerJoinLeftHandSide
parameter_list|,
name|boolean
name|outerJoinRightHandSide
parameter_list|)
block|{
name|left
operator|.
name|setOuterJoin
argument_list|(
name|outerJoinLeftHandSide
argument_list|,
name|outerJoinRightHandSide
argument_list|)
expr_stmt|;
name|right
operator|.
name|setOuterJoin
argument_list|(
name|outerJoinLeftHandSide
argument_list|,
name|outerJoinRightHandSide
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addJoinCondition
parameter_list|(
name|JoinConditionImpl
name|joinCondition
parameter_list|,
name|boolean
name|forThisSelector
parameter_list|)
block|{
name|left
operator|.
name|addJoinCondition
argument_list|(
name|joinCondition
argument_list|,
name|forThisSelector
argument_list|)
expr_stmt|;
name|right
operator|.
name|addJoinCondition
argument_list|(
name|joinCondition
argument_list|,
name|forThisSelector
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|next
parameter_list|()
block|{
if|if
condition|(
name|end
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|leftNeedExecute
condition|)
block|{
name|left
operator|.
name|execute
argument_list|(
name|rootState
argument_list|)
expr_stmt|;
name|leftNeedExecute
operator|=
literal|false
expr_stmt|;
name|leftNeedNext
operator|=
literal|true
expr_stmt|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|leftNeedNext
condition|)
block|{
if|if
condition|(
operator|!
name|left
operator|.
name|next
argument_list|()
condition|)
block|{
name|end
operator|=
literal|true
expr_stmt|;
return|return
literal|false
return|;
block|}
name|leftNeedNext
operator|=
literal|false
expr_stmt|;
name|rightNeedExecute
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|rightNeedExecute
condition|)
block|{
name|right
operator|.
name|execute
argument_list|(
name|rootState
argument_list|)
expr_stmt|;
name|foundJoinedRow
operator|=
literal|false
expr_stmt|;
name|rightNeedExecute
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|right
operator|.
name|next
argument_list|()
condition|)
block|{
name|leftNeedNext
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|joinCondition
operator|.
name|evaluate
argument_list|()
condition|)
block|{
name|foundJoinedRow
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|// for an outer join, if no matching result was found,
comment|// one row returned (with all values set to null)
if|if
condition|(
name|right
operator|.
name|isOuterJoinRightHandSide
argument_list|()
operator|&&
name|leftNeedNext
operator|&&
operator|!
name|foundJoinedRow
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isOuterJoinRightHandSide
parameter_list|()
block|{
return|return
name|left
operator|.
name|isOuterJoinRightHandSide
argument_list|()
operator|||
name|right
operator|.
name|isOuterJoinRightHandSide
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|(
name|NodeState
name|rootState
parameter_list|,
name|SizePrecision
name|precision
parameter_list|,
name|long
name|max
parameter_list|)
block|{
comment|// we don't know
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|AstElement
name|copyOf
parameter_list|()
block|{
return|return
operator|new
name|JoinImpl
argument_list|(
operator|(
name|SourceImpl
operator|)
name|copyElementAndCheckReference
argument_list|(
name|left
argument_list|)
argument_list|,
operator|(
name|SourceImpl
operator|)
name|copyElementAndCheckReference
argument_list|(
name|right
argument_list|)
argument_list|,
name|joinType
argument_list|,
operator|(
name|JoinConditionImpl
operator|)
name|copyElementAndCheckReference
argument_list|(
name|joinCondition
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

