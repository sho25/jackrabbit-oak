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
name|oak
operator|.
name|api
operator|.
name|CoreValue
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
name|PropertyState
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
name|query
operator|.
name|index
operator|.
name|FilterImpl
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
name|PropertyState
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
return|return
literal|false
return|;
block|}
name|PropertyState
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
return|return
literal|false
return|;
block|}
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
return|return
name|p1
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|p2
operator|.
name|getValue
argument_list|()
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
name|CoreValue
name|x
init|=
name|p1
operator|.
name|getValue
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|y
range|:
name|p2
operator|.
name|getValues
argument_list|()
control|)
block|{
if|if
condition|(
name|y
operator|.
name|getType
argument_list|()
operator|!=
name|x
operator|.
name|getType
argument_list|()
condition|)
block|{
name|y
operator|=
name|query
operator|.
name|convert
argument_list|(
name|y
argument_list|,
name|x
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|y
operator|!=
literal|null
operator|&&
name|x
operator|.
name|equals
argument_list|(
name|y
argument_list|)
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
name|CoreValue
name|x
init|=
name|p2
operator|.
name|getValue
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|y
range|:
name|p1
operator|.
name|getValues
argument_list|()
control|)
block|{
if|if
condition|(
name|y
operator|.
name|getType
argument_list|()
operator|!=
name|x
operator|.
name|getType
argument_list|()
condition|)
block|{
name|y
operator|=
name|query
operator|.
name|convert
argument_list|(
name|y
argument_list|,
name|x
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|x
operator|.
name|equals
argument_list|(
name|y
argument_list|)
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
name|CoreValue
index|[]
name|l1
init|=
name|p1
operator|.
name|getValues
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|CoreValue
index|[
name|p1
operator|.
name|getValues
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|CoreValue
index|[]
name|l2
init|=
name|p2
operator|.
name|getValues
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|CoreValue
index|[
name|p2
operator|.
name|getValues
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
return|return
name|Query
operator|.
name|compareValues
argument_list|(
name|l1
argument_list|,
name|l2
argument_list|)
operator|==
literal|0
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
name|PropertyState
name|p1
init|=
name|selector1
operator|.
name|currentProperty
argument_list|(
name|property1Name
argument_list|)
decl_stmt|;
name|PropertyState
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
name|f
operator|.
name|getSelector
argument_list|()
operator|==
name|selector1
operator|&&
name|p2
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|p2
operator|.
name|isArray
argument_list|()
condition|)
block|{
comment|// TODO support join on multi-valued properties
name|f
operator|.
name|restrictProperty
argument_list|(
name|property1Name
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|p2
operator|.
name|getValue
argument_list|()
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
operator|==
name|selector2
operator|&&
name|p1
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|p1
operator|.
name|isArray
argument_list|()
condition|)
block|{
comment|// TODO support join on multi-valued properties
name|f
operator|.
name|restrictProperty
argument_list|(
name|property2Name
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|p1
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
operator|==
name|selector1
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
operator|==
name|selector2
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
block|}
end_class

end_unit

