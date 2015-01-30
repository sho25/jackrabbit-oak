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
name|QueryImpl
import|;
end_import

begin_comment
comment|/**  * The base class to visit all elements.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AstVisitorBase
implements|implements
name|AstVisitor
block|{
comment|/**      * Calls accept on each of the attached constraints of the AND node.      */
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|AndImpl
name|node
parameter_list|)
block|{
for|for
control|(
name|ConstraintImpl
name|constraint
range|:
name|node
operator|.
name|getConstraints
argument_list|()
control|)
block|{
name|constraint
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Calls accept on the two operands in the comparison node.      */
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|ComparisonImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|getOperand1
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|node
operator|.
name|getOperand2
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * Calls accept on the all operands in the "in" node.      */
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|InImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|getOperand1
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
for|for
control|(
name|StaticOperandImpl
name|s
range|:
name|node
operator|.
name|getOperand2
argument_list|()
control|)
block|{
name|s
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Calls accept on the static operand in the fulltext search constraint.      */
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|FullTextSearchImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|getFullTextSearchExpression
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * Calls accept on the static operand in the native search constraint.      */
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|NativeFunctionImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|getNativeSearchExpression
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * Calls accept on the static operand in the similar search constraint.      */
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|SimilarImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|getPathExpression
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * Calls accept on the static operand in the spellcheck search constraint.      */
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|SpellcheckImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|getExpression
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * Calls accept on the two sources and the join condition in the join node.      */
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|JoinImpl
name|node
parameter_list|)
block|{
name|node
operator|.
name|getLeft
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|node
operator|.
name|getRight
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|node
operator|.
name|getJoinCondition
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * Calls accept on the property value in the length node.      */
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|LengthImpl
name|node
parameter_list|)
block|{
return|return
name|node
operator|.
name|getPropertyValue
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * Calls accept on the dynamic operand in the lower-case node.      */
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|LowerCaseImpl
name|node
parameter_list|)
block|{
return|return
name|node
operator|.
name|getOperand
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * Calls accept on the constraint in the NOT node.      */
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|NotImpl
name|node
parameter_list|)
block|{
return|return
name|node
operator|.
name|getConstraint
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * Calls accept on the dynamic operand in the ordering node.      */
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|OrderingImpl
name|node
parameter_list|)
block|{
return|return
name|node
operator|.
name|getOperand
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * Calls accept on each of the attached constraints of the OR node.      */
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|OrImpl
name|node
parameter_list|)
block|{
for|for
control|(
name|ConstraintImpl
name|constraint
range|:
name|node
operator|.
name|getConstraints
argument_list|()
control|)
block|{
name|constraint
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Calls accept on the following contained QOM nodes:      *<ul>      *<li>Source</li>      *<li>Constraints</li>      *<li>Orderings</li>      *<li>Columns</li>      *</ul>      *      * @param query the query to visit      */
specifier|public
name|void
name|visit
parameter_list|(
name|QueryImpl
name|query
parameter_list|)
block|{
name|query
operator|.
name|getSource
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|ConstraintImpl
name|constraint
init|=
name|query
operator|.
name|getConstraint
argument_list|()
decl_stmt|;
if|if
condition|(
name|constraint
operator|!=
literal|null
condition|)
block|{
name|constraint
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|OrderingImpl
index|[]
name|orderings
init|=
name|query
operator|.
name|getOrderings
argument_list|()
decl_stmt|;
if|if
condition|(
name|orderings
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|OrderingImpl
name|ordering
range|:
name|orderings
control|)
block|{
name|ordering
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
name|ColumnImpl
index|[]
name|columns
init|=
name|query
operator|.
name|getColumns
argument_list|()
decl_stmt|;
for|for
control|(
name|ColumnImpl
name|column
range|:
name|columns
control|)
block|{
name|column
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Calls accept on the dynamic operand in the lower-case node.      */
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|UpperCaseImpl
name|node
parameter_list|)
block|{
return|return
name|node
operator|.
name|getOperand
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

