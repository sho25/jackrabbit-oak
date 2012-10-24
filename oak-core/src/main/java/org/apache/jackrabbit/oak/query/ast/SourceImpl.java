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
comment|/**      * The WHERE clause of the query.      */
specifier|protected
name|ConstraintImpl
name|queryConstraint
decl_stmt|;
comment|/**      * The join condition of this selector that can be evaluated at execution      * time. For the query "select * from nt:base as a inner join nt:base as b      * on a.x = b.x", the join condition "a.x = b.x" is only set for the      * selector b, as selector a can't evaluate it if it is executed first      * (until b is executed).      */
specifier|protected
name|JoinConditionImpl
name|joinCondition
decl_stmt|;
comment|/**      * The list of all join conditions this selector is involved. For the query      * "select * from nt:base as a inner join nt:base as b on a.x =      * b.x", the join condition "a.x = b.x" is set for both selectors a and b,      * so both can check if the property x is set.      */
specifier|protected
name|ArrayList
argument_list|<
name|JoinConditionImpl
argument_list|>
name|allJoinConditions
init|=
operator|new
name|ArrayList
argument_list|<
name|JoinConditionImpl
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Whether this selector is the right hand side of a join.      */
specifier|protected
name|boolean
name|join
decl_stmt|;
comment|/**      * Whether this selector is the right hand side of a left outer join.      * Right outer joins are converted to left outer join.      */
specifier|protected
name|boolean
name|outerJoin
decl_stmt|;
comment|/**      * Set the complete constraint of the query (the WHERE ... condition).      *      * @param queryConstraint the constraint      */
specifier|public
name|void
name|setQueryConstraint
parameter_list|(
name|ConstraintImpl
name|queryConstraint
parameter_list|)
block|{
name|this
operator|.
name|queryConstraint
operator|=
name|queryConstraint
expr_stmt|;
block|}
comment|/**      * Add the join condition (the ON ... condition).      *      * @param joinCondition the join condition      * @param forThisSelector if set, the join condition can only be evaluated      *        when all previous selectors are executed.      */
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
if|if
condition|(
name|forThisSelector
condition|)
block|{
name|this
operator|.
name|joinCondition
operator|=
name|joinCondition
expr_stmt|;
block|}
name|allJoinConditions
operator|.
name|add
argument_list|(
name|joinCondition
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set whether this source is the right hand side of a left outer join.      *      * @param outerJoin true if yes      */
specifier|public
name|void
name|setOuterJoin
parameter_list|(
name|boolean
name|outerJoin
parameter_list|)
block|{
name|this
operator|.
name|outerJoin
operator|=
name|outerJoin
expr_stmt|;
block|}
comment|/**      * Initialize the query. This will 'wire' the selectors with the      * constraints.      *      * @param query the query      */
specifier|public
specifier|abstract
name|void
name|init
parameter_list|(
name|Query
name|query
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
comment|/**      * Get the query plan.      *      * @param root the root      * @return the query plan      */
specifier|public
specifier|abstract
name|String
name|getPlan
parameter_list|(
name|NodeState
name|root
parameter_list|)
function_decl|;
comment|/**      * Prepare executing the query. This method will decide which index to use.      *      */
specifier|public
specifier|abstract
name|void
name|prepare
parameter_list|()
function_decl|;
comment|/**      * Execute the query. The current node is set to before the first row.      *      * @param root root state of the given revision      */
specifier|public
specifier|abstract
name|void
name|execute
parameter_list|(
name|NodeState
name|root
parameter_list|)
function_decl|;
comment|/**      * Go to the next node for the given source. This will also filter the      * result for the right node type if required.      *      * @return true if there is a next row      */
specifier|public
specifier|abstract
name|boolean
name|next
parameter_list|()
function_decl|;
block|}
end_class

end_unit

