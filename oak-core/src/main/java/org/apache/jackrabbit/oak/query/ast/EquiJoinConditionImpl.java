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
name|api
operator|.
name|PropertyValue
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
name|PropertyValues
import|;
end_import

begin_comment
comment|/**  * The "a.x = b.y" join condition.  */
end_comment

begin_class
specifier|public
class|class
name|EquiJoinConditionImpl
extends|extends
name|JoinConditionImpl
block|{
specifier|private
specifier|final
name|String
name|property1Name
decl_stmt|;
specifier|private
specifier|final
name|String
name|property2Name
decl_stmt|;
specifier|private
specifier|final
name|String
name|selector1Name
decl_stmt|;
specifier|private
specifier|final
name|String
name|selector2Name
decl_stmt|;
specifier|private
name|SelectorImpl
name|selector1
decl_stmt|;
specifier|private
name|SelectorImpl
name|selector2
decl_stmt|;
specifier|public
name|EquiJoinConditionImpl
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
name|this
operator|.
name|selector1Name
operator|=
name|selector1Name
expr_stmt|;
name|this
operator|.
name|property1Name
operator|=
name|property1Name
expr_stmt|;
name|this
operator|.
name|selector2Name
operator|=
name|selector2Name
expr_stmt|;
name|this
operator|.
name|property2Name
operator|=
name|property2Name
expr_stmt|;
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
name|quote
argument_list|(
name|selector1Name
argument_list|)
operator|+
literal|'.'
operator|+
name|quote
argument_list|(
name|property1Name
argument_list|)
operator|+
literal|" = "
operator|+
name|quote
argument_list|(
name|selector2Name
argument_list|)
operator|+
literal|'.'
operator|+
name|quote
argument_list|(
name|property2Name
argument_list|)
return|;
block|}
specifier|public
name|void
name|bindSelector
parameter_list|(
name|SourceImpl
name|source
parameter_list|)
block|{
name|selector1
operator|=
name|source
operator|.
name|getExistingSelector
argument_list|(
name|selector1Name
argument_list|)
expr_stmt|;
name|selector2
operator|=
name|source
operator|.
name|getExistingSelector
argument_list|(
name|selector2Name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
comment|// 6.7.8 EquiJoinCondition
comment|// A node-tuple satisfies the constraint only if:
name|PropertyValue
name|p1
init|=
name|selector1
operator|.
name|currentProperty
argument_list|(
name|property1Name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p1
operator|==
literal|null
condition|)
block|{
comment|// the selector1Name node has a property named property1Name, and
return|return
literal|false
return|;
block|}
name|PropertyValue
name|p2
init|=
name|selector2
operator|.
name|currentProperty
argument_list|(
name|property2Name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p2
operator|==
literal|null
condition|)
block|{
comment|// the selector2Name node has a property named property2Name, and
return|return
literal|false
return|;
block|}
comment|// the value of property property1Name is equal to the value of property property2Name,
comment|// as defined in §3.6.5 Comparison of Values.
comment|// -> that can be interpreted as follows: if the property types
comment|// don't match, then they don't match, however for compatibility
comment|// with Jackrabbit 2.x, we try to convert the values so the property types match
comment|// (for example, convert reference to string)
comment|// See OAK-3416
if|if
condition|(
operator|!
name|p1
operator|.
name|isArray
argument_list|()
operator|&&
operator|!
name|p2
operator|.
name|isArray
argument_list|()
condition|)
block|{
comment|// both are single valued
comment|// "the value of operand2 is converted to the
comment|// property type of the value of operand1"
name|p2
operator|=
name|convertValueToType
argument_list|(
name|p2
argument_list|,
name|p1
argument_list|)
expr_stmt|;
return|return
name|PropertyValues
operator|.
name|match
argument_list|(
name|p1
argument_list|,
name|p2
argument_list|)
return|;
block|}
comment|// TODO what is the expected result of an equi join for multi-valued properties?
if|if
condition|(
operator|!
name|p1
operator|.
name|isArray
argument_list|()
operator|&&
name|p2
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|p1
operator|=
name|convertValueToType
argument_list|(
name|p1
argument_list|,
name|p2
argument_list|)
expr_stmt|;
if|if
condition|(
name|p1
operator|!=
literal|null
operator|&&
name|PropertyValues
operator|.
name|match
argument_list|(
name|p1
argument_list|,
name|p2
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|p1
operator|.
name|isArray
argument_list|()
operator|&&
operator|!
name|p2
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|p2
operator|=
name|convertValueToType
argument_list|(
name|p2
argument_list|,
name|p1
argument_list|)
expr_stmt|;
if|if
condition|(
name|p2
operator|!=
literal|null
operator|&&
name|PropertyValues
operator|.
name|match
argument_list|(
name|p1
argument_list|,
name|p2
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
return|return
name|PropertyValues
operator|.
name|match
argument_list|(
name|p1
argument_list|,
name|p2
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
name|equals
argument_list|(
name|selector1
argument_list|)
condition|)
block|{
name|PropertyValue
name|p2
init|=
name|selector2
operator|.
name|currentProperty
argument_list|(
name|property2Name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p2
operator|==
literal|null
operator|&&
name|f
operator|.
name|isPreparing
argument_list|()
operator|&&
name|f
operator|.
name|isPrepared
argument_list|(
name|selector2
argument_list|)
condition|)
block|{
comment|// during the prepare phase, if the selector is already
comment|// prepared, then we would know the value
name|p2
operator|=
name|PropertyValues
operator|.
name|newString
argument_list|(
name|KNOWN_VALUE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|p2
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|p2
operator|.
name|isArray
argument_list|()
condition|)
block|{
comment|// TODO support join on multi-valued properties
name|p2
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|String
name|p1n
init|=
name|normalizePropertyName
argument_list|(
name|property1Name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p2
operator|==
literal|null
condition|)
block|{
comment|// always set the condition,
comment|// even if unknown (in which case it is converted to "is not null")
name|f
operator|.
name|restrictProperty
argument_list|(
name|p1n
argument_list|,
name|Operator
operator|.
name|NOT_EQUAL
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|f
operator|.
name|restrictProperty
argument_list|(
name|p1n
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|p2
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|f
operator|.
name|getSelector
argument_list|()
operator|.
name|equals
argument_list|(
name|selector2
argument_list|)
condition|)
block|{
name|PropertyValue
name|p1
init|=
name|selector1
operator|.
name|currentProperty
argument_list|(
name|property1Name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p1
operator|==
literal|null
operator|&&
name|f
operator|.
name|isPreparing
argument_list|()
operator|&&
name|f
operator|.
name|isPrepared
argument_list|(
name|selector1
argument_list|)
condition|)
block|{
comment|// during the prepare phase, if the selector is already
comment|// prepared, then we would know the value
name|p1
operator|=
name|PropertyValues
operator|.
name|newString
argument_list|(
name|KNOWN_VALUE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|p1
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|p1
operator|.
name|isArray
argument_list|()
condition|)
block|{
comment|// TODO support join on multi-valued properties
name|p1
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|// always set the condition, even if unkown ( -> is not null)
name|String
name|p2n
init|=
name|normalizePropertyName
argument_list|(
name|property2Name
argument_list|)
decl_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
name|p2n
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|p1
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
comment|// both properties may not be null
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
name|selector1
argument_list|)
condition|)
block|{
name|PropertyExistenceImpl
name|ex
init|=
operator|new
name|PropertyExistenceImpl
argument_list|(
name|s
operator|.
name|getSelectorName
argument_list|()
argument_list|,
name|property1Name
argument_list|)
decl_stmt|;
name|ex
operator|.
name|bindSelector
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|s
operator|.
name|restrictSelector
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
name|selector2
argument_list|)
condition|)
block|{
name|PropertyExistenceImpl
name|ex
init|=
operator|new
name|PropertyExistenceImpl
argument_list|(
name|s
operator|.
name|getSelectorName
argument_list|()
argument_list|,
name|property2Name
argument_list|)
decl_stmt|;
name|ex
operator|.
name|bindSelector
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|s
operator|.
name|restrictSelector
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isParent
parameter_list|(
name|SourceImpl
name|source
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canEvaluate
parameter_list|(
name|Set
argument_list|<
name|SourceImpl
argument_list|>
name|available
parameter_list|)
block|{
return|return
name|available
operator|.
name|contains
argument_list|(
name|selector1
argument_list|)
operator|&&
name|available
operator|.
name|contains
argument_list|(
name|selector2
argument_list|)
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
block|}
end_class

end_unit

