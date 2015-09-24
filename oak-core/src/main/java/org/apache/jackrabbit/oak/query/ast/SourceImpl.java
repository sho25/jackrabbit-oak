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
comment|/**  * The base class of a selector and a join.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SourceImpl
extends|extends
name|AstElement
block|{
comment|/**      * Set the complete constraint of the query (the WHERE ... condition).      *      * @param queryConstraint the constraint      */
specifier|public
specifier|abstract
name|void
name|setQueryConstraint
parameter_list|(
name|ConstraintImpl
name|queryConstraint
parameter_list|)
function_decl|;
comment|/**      * Add the join condition (the ON ... condition).      *      * @param joinCondition the join condition      * @param forThisSelector if set, the join condition can only be evaluated      *        when all previous selectors are executed.      */
specifier|public
specifier|abstract
name|void
name|addJoinCondition
parameter_list|(
name|JoinConditionImpl
name|joinCondition
parameter_list|,
name|boolean
name|forThisSelector
parameter_list|)
function_decl|;
comment|/**      * Set whether this source is the left hand side or right hand side of a left outer join.      *      * @param outerJoinLeftHandSide true if yes      * @param outerJoinRightHandSide true if yes      */
specifier|public
specifier|abstract
name|void
name|setOuterJoin
parameter_list|(
name|boolean
name|outerJoinLeftHandSide
parameter_list|,
name|boolean
name|outerJoinRightHandSide
parameter_list|)
function_decl|;
comment|/**      * Get the selector with the given name, or null if not found.      *      * @param selectorName the selector name      * @return the selector, or null      */
specifier|public
specifier|abstract
name|SelectorImpl
name|getSelector
parameter_list|(
name|String
name|selectorName
parameter_list|)
function_decl|;
comment|/**      * Get the selector with the given name, or fail if not found.      *      * @param selectorName the selector name      * @return the selector (never null)      */
specifier|public
name|SelectorImpl
name|getExistingSelector
parameter_list|(
name|String
name|selectorName
parameter_list|)
block|{
name|SelectorImpl
name|s
init|=
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
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown selector: "
operator|+
name|selectorName
argument_list|)
throw|;
block|}
return|return
name|s
return|;
block|}
comment|/**      * Get the query plan.      *      * @param rootState the root      * @return the query plan      */
specifier|public
specifier|abstract
name|String
name|getPlan
parameter_list|(
name|NodeState
name|rootState
parameter_list|)
function_decl|;
comment|/**      * Get the index cost as a JSON string.      *      * @param rootState the root      * @return the cost      */
specifier|public
specifier|abstract
name|String
name|getIndexCostInfo
parameter_list|(
name|NodeState
name|rootState
parameter_list|)
function_decl|;
comment|/**      * Prepare executing the query (recursively). This will 'wire' the      * selectors with the join constraints, and decide which index to use.      *       * @return the execution plan      */
specifier|public
specifier|abstract
name|ExecutionPlan
name|prepare
parameter_list|()
function_decl|;
comment|/**      * Undo a prepare.      */
specifier|public
specifier|abstract
name|void
name|unprepare
parameter_list|()
function_decl|;
comment|/**      * Re-apply a previously prepared plan. This will also 're-wire' the      * selectors with the join constraints      *       * @param p the plan to use      */
specifier|public
specifier|abstract
name|void
name|prepare
parameter_list|(
name|ExecutionPlan
name|p
parameter_list|)
function_decl|;
comment|/**      * Execute the query. The current node is set to before the first row.      *      * @param rootState root state of the given revision      */
specifier|public
specifier|abstract
name|void
name|execute
parameter_list|(
name|NodeState
name|rootState
parameter_list|)
function_decl|;
comment|/**      * Go to the next node for the given source. This will also filter the      * result for the right node type if required.      *      * @return true if there is a next row      */
specifier|public
specifier|abstract
name|boolean
name|next
parameter_list|()
function_decl|;
comment|/**      *<b>!Test purpose only!<b>      *       * this creates a filter for the given query      *       * @param preparing whether this this the prepare phase      * @return a new filter      */
specifier|public
specifier|abstract
name|Filter
name|createFilter
parameter_list|(
name|boolean
name|preparing
parameter_list|)
function_decl|;
comment|/**      * Get all sources that are joined via inner join. (These can be swapped.)      *       * @return the list of selectors (sorted from left to right)      */
specifier|public
specifier|abstract
name|List
argument_list|<
name|SourceImpl
argument_list|>
name|getInnerJoinSelectors
parameter_list|()
function_decl|;
comment|/**      * Get the list of inner join conditions. (These match the inner join selectors.)      *       * @return the list of join conditions      */
specifier|public
name|List
argument_list|<
name|JoinConditionImpl
argument_list|>
name|getInnerJoinConditions
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|/**      * Whether any selector is the outer-join right hand side.      *       * @return true if there is any      */
specifier|public
specifier|abstract
name|boolean
name|isOuterJoinRightHandSide
parameter_list|()
function_decl|;
comment|/**      * Get the size if known.      *       * @param precision the required precision      * @param max the maximum nodes read (for an exact size)      * @return the size, or -1 if unknown      */
specifier|public
specifier|abstract
name|long
name|getSize
parameter_list|(
name|SizePrecision
name|precision
parameter_list|,
name|long
name|max
parameter_list|)
function_decl|;
block|}
end_class

end_unit

