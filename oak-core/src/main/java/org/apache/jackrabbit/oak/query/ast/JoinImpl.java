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
return|return
name|left
operator|.
name|getPlan
argument_list|(
name|rootState
argument_list|)
operator|+
literal|' '
operator|+
name|joinType
operator|+
literal|" "
operator|+
name|right
operator|.
name|getPlan
argument_list|(
name|rootState
argument_list|)
operator|+
literal|" on "
operator|+
name|joinCondition
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
name|init
parameter_list|(
name|Query
name|query
parameter_list|)
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
name|right
operator|.
name|setOuterJoin
argument_list|(
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
name|right
operator|.
name|setOuterJoin
argument_list|(
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
name|right
operator|.
name|init
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|left
operator|.
name|init
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|prepare
parameter_list|()
block|{
name|left
operator|.
name|prepare
argument_list|()
expr_stmt|;
name|right
operator|.
name|prepare
argument_list|()
expr_stmt|;
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
name|outerJoin
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
block|}
end_class

end_unit

