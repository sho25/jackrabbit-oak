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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
specifier|protected
name|ConstraintImpl
name|queryConstraint
decl_stmt|;
specifier|protected
name|JoinConditionImpl
name|joinCondition
decl_stmt|;
specifier|protected
name|boolean
name|join
decl_stmt|;
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
comment|/**      * Set the join condition (the ON ... condition).      *      * @param joinCondition the join condition      */
specifier|public
name|void
name|setJoinCondition
parameter_list|(
name|JoinConditionImpl
name|joinCondition
parameter_list|)
block|{
name|this
operator|.
name|joinCondition
operator|=
name|joinCondition
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
comment|/**      * Get the query plan.      *      * @return the query plan      */
specifier|public
specifier|abstract
name|String
name|getPlan
parameter_list|()
function_decl|;
comment|/**      * Prepare executing the query. This method will decide which index to use.      *      * @param mk the MicroKernel      */
specifier|public
specifier|abstract
name|void
name|prepare
parameter_list|(
name|MicroKernel
name|mk
parameter_list|)
function_decl|;
comment|/**      * Execute the query. The current node is set to before the first row.      *      * @param revisionId the revision to use      */
specifier|public
specifier|abstract
name|void
name|execute
parameter_list|(
name|String
name|revisionId
parameter_list|)
function_decl|;
comment|/**      * Go to the next node for the given source. This will also filter the      * result for the right node type if required.      *      * @return true if there is a next row      */
specifier|public
specifier|abstract
name|boolean
name|next
parameter_list|()
function_decl|;
comment|/**      * Get the current absolute path (including workspace name)      *      * @return the path      */
specifier|public
specifier|abstract
name|String
name|currentPath
parameter_list|()
function_decl|;
block|}
end_class

end_unit

