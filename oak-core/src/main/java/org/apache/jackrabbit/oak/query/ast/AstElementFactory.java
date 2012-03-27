begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law  * or agreed to in writing, software distributed under the License is  * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
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
name|CoreValue
import|;
end_import

begin_comment
comment|/**  * A factory for syntax tree elements.  */
end_comment

begin_class
specifier|public
class|class
name|AstElementFactory
block|{
specifier|public
name|AndImpl
name|and
parameter_list|(
name|ConstraintImpl
name|constraint1
parameter_list|,
name|ConstraintImpl
name|constraint2
parameter_list|)
block|{
return|return
operator|new
name|AndImpl
argument_list|(
operator|(
name|ConstraintImpl
operator|)
name|constraint1
argument_list|,
operator|(
name|ConstraintImpl
operator|)
name|constraint2
argument_list|)
return|;
block|}
specifier|public
name|OrderingImpl
name|ascending
parameter_list|(
name|DynamicOperandImpl
name|operand
parameter_list|)
block|{
return|return
operator|new
name|OrderingImpl
argument_list|(
operator|(
name|DynamicOperandImpl
operator|)
name|operand
argument_list|,
name|Order
operator|.
name|ASCENDING
argument_list|)
return|;
block|}
specifier|public
name|BindVariableValueImpl
name|bindVariable
parameter_list|(
name|String
name|bindVariableName
parameter_list|)
block|{
return|return
operator|new
name|BindVariableValueImpl
argument_list|(
name|bindVariableName
argument_list|)
return|;
block|}
specifier|public
name|ChildNodeImpl
name|childNode
parameter_list|(
name|String
name|selectorName
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|ChildNodeImpl
argument_list|(
name|selectorName
argument_list|,
name|path
argument_list|)
return|;
block|}
specifier|public
name|ChildNodeJoinConditionImpl
name|childNodeJoinCondition
parameter_list|(
name|String
name|childSelectorName
parameter_list|,
name|String
name|parentSelectorName
parameter_list|)
block|{
return|return
operator|new
name|ChildNodeJoinConditionImpl
argument_list|(
name|childSelectorName
argument_list|,
name|parentSelectorName
argument_list|)
return|;
block|}
specifier|public
name|ColumnImpl
name|column
parameter_list|(
name|String
name|selectorName
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|String
name|columnName
parameter_list|)
block|{
return|return
operator|new
name|ColumnImpl
argument_list|(
name|selectorName
argument_list|,
name|propertyName
argument_list|,
name|columnName
argument_list|)
return|;
block|}
specifier|public
name|ComparisonImpl
name|comparison
parameter_list|(
name|DynamicOperandImpl
name|operand1
parameter_list|,
name|Operator
name|operator
parameter_list|,
name|StaticOperandImpl
name|operand2
parameter_list|)
block|{
return|return
operator|new
name|ComparisonImpl
argument_list|(
operator|(
name|DynamicOperandImpl
operator|)
name|operand1
argument_list|,
name|operator
argument_list|,
operator|(
name|StaticOperandImpl
operator|)
name|operand2
argument_list|)
return|;
block|}
specifier|public
name|DescendantNodeImpl
name|descendantNode
parameter_list|(
name|String
name|selectorName
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|DescendantNodeImpl
argument_list|(
name|selectorName
argument_list|,
name|path
argument_list|)
return|;
block|}
specifier|public
name|DescendantNodeJoinConditionImpl
name|descendantNodeJoinCondition
parameter_list|(
name|String
name|descendantSelectorName
parameter_list|,
name|String
name|ancestorSelectorName
parameter_list|)
block|{
return|return
operator|new
name|DescendantNodeJoinConditionImpl
argument_list|(
name|descendantSelectorName
argument_list|,
name|ancestorSelectorName
argument_list|)
return|;
block|}
specifier|public
name|OrderingImpl
name|descending
parameter_list|(
name|DynamicOperandImpl
name|operand
parameter_list|)
block|{
return|return
operator|new
name|OrderingImpl
argument_list|(
operator|(
name|DynamicOperandImpl
operator|)
name|operand
argument_list|,
name|Order
operator|.
name|DESCENDING
argument_list|)
return|;
block|}
specifier|public
name|EquiJoinConditionImpl
name|equiJoinCondition
parameter_list|(
name|String
name|selector1Name
parameter_list|,
name|String
name|property1Name
parameter_list|,
name|String
name|selector2Name
parameter_list|,
name|String
name|property2Name
parameter_list|)
block|{
return|return
operator|new
name|EquiJoinConditionImpl
argument_list|(
name|selector1Name
argument_list|,
name|property1Name
argument_list|,
name|selector2Name
argument_list|,
name|property2Name
argument_list|)
return|;
block|}
specifier|public
name|FullTextSearchImpl
name|fullTextSearch
parameter_list|(
name|String
name|selectorName
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|StaticOperandImpl
name|fullTextSearchExpression
parameter_list|)
block|{
return|return
operator|new
name|FullTextSearchImpl
argument_list|(
name|selectorName
argument_list|,
name|propertyName
argument_list|,
name|fullTextSearchExpression
argument_list|)
return|;
block|}
specifier|public
name|FullTextSearchScoreImpl
name|fullTextSearchScore
parameter_list|(
name|String
name|selectorName
parameter_list|)
block|{
return|return
operator|new
name|FullTextSearchScoreImpl
argument_list|(
name|selectorName
argument_list|)
return|;
block|}
specifier|public
name|JoinImpl
name|join
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
return|return
operator|new
name|JoinImpl
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|joinType
argument_list|,
name|joinCondition
argument_list|)
return|;
block|}
specifier|public
name|LengthImpl
name|length
parameter_list|(
name|PropertyValueImpl
name|propertyValue
parameter_list|)
block|{
return|return
operator|new
name|LengthImpl
argument_list|(
name|propertyValue
argument_list|)
return|;
block|}
specifier|public
name|LiteralImpl
name|literal
parameter_list|(
name|CoreValue
name|literalValue
parameter_list|)
block|{
return|return
operator|new
name|LiteralImpl
argument_list|(
name|literalValue
argument_list|)
return|;
block|}
specifier|public
name|LowerCaseImpl
name|lowerCase
parameter_list|(
name|DynamicOperandImpl
name|operand
parameter_list|)
block|{
return|return
operator|new
name|LowerCaseImpl
argument_list|(
operator|(
name|DynamicOperandImpl
operator|)
name|operand
argument_list|)
return|;
block|}
specifier|public
name|NodeLocalNameImpl
name|nodeLocalName
parameter_list|(
name|String
name|selectorName
parameter_list|)
block|{
return|return
operator|new
name|NodeLocalNameImpl
argument_list|(
name|selectorName
argument_list|)
return|;
block|}
specifier|public
name|NodeNameImpl
name|nodeName
parameter_list|(
name|String
name|selectorName
parameter_list|)
block|{
return|return
operator|new
name|NodeNameImpl
argument_list|(
name|selectorName
argument_list|)
return|;
block|}
specifier|public
name|NotImpl
name|not
parameter_list|(
name|ConstraintImpl
name|constraint
parameter_list|)
block|{
return|return
operator|new
name|NotImpl
argument_list|(
operator|(
name|ConstraintImpl
operator|)
name|constraint
argument_list|)
return|;
block|}
specifier|public
name|OrImpl
name|or
parameter_list|(
name|ConstraintImpl
name|constraint1
parameter_list|,
name|ConstraintImpl
name|constraint2
parameter_list|)
block|{
return|return
operator|new
name|OrImpl
argument_list|(
operator|(
name|ConstraintImpl
operator|)
name|constraint1
argument_list|,
operator|(
name|ConstraintImpl
operator|)
name|constraint2
argument_list|)
return|;
block|}
specifier|public
name|PropertyExistenceImpl
name|propertyExistence
parameter_list|(
name|String
name|selectorName
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
return|return
operator|new
name|PropertyExistenceImpl
argument_list|(
name|selectorName
argument_list|,
name|propertyName
argument_list|)
return|;
block|}
specifier|public
name|PropertyValueImpl
name|propertyValue
parameter_list|(
name|String
name|selectorName
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
return|return
operator|new
name|PropertyValueImpl
argument_list|(
name|selectorName
argument_list|,
name|propertyName
argument_list|)
return|;
block|}
specifier|public
name|SameNodeImpl
name|sameNode
parameter_list|(
name|String
name|selectorName
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|SameNodeImpl
argument_list|(
name|selectorName
argument_list|,
name|path
argument_list|)
return|;
block|}
specifier|public
name|SameNodeJoinConditionImpl
name|sameNodeJoinCondition
parameter_list|(
name|String
name|selector1Name
parameter_list|,
name|String
name|selector2Name
parameter_list|,
name|String
name|selector2Path
parameter_list|)
block|{
return|return
operator|new
name|SameNodeJoinConditionImpl
argument_list|(
name|selector1Name
argument_list|,
name|selector2Name
argument_list|,
name|selector2Path
argument_list|)
return|;
block|}
specifier|public
name|SelectorImpl
name|selector
parameter_list|(
name|String
name|nodeTypeName
parameter_list|,
name|String
name|selectorName
parameter_list|)
block|{
return|return
operator|new
name|SelectorImpl
argument_list|(
name|nodeTypeName
argument_list|,
name|selectorName
argument_list|)
return|;
block|}
specifier|public
name|UpperCaseImpl
name|upperCase
parameter_list|(
name|DynamicOperandImpl
name|operand
parameter_list|)
block|{
return|return
operator|new
name|UpperCaseImpl
argument_list|(
operator|(
name|DynamicOperandImpl
operator|)
name|operand
argument_list|)
return|;
block|}
block|}
end_class

end_unit

