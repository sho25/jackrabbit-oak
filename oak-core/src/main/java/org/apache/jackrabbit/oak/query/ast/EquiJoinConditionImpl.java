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
name|query
operator|.
name|ScalarImpl
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
name|Filter
import|;
end_import

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
specifier|public
name|String
name|getSelector1Name
parameter_list|()
block|{
return|return
name|selector1Name
return|;
block|}
specifier|public
name|String
name|getProperty1Name
parameter_list|()
block|{
return|return
name|property1Name
return|;
block|}
specifier|public
name|String
name|getSelector2Name
parameter_list|()
block|{
return|return
name|selector2Name
return|;
block|}
specifier|public
name|String
name|getProperty2Name
parameter_list|()
block|{
return|return
name|property2Name
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
comment|// TODO quote property names?
return|return
name|getSelector1Name
argument_list|()
operator|+
literal|'.'
operator|+
name|getProperty1Name
argument_list|()
operator|+
literal|" = "
operator|+
name|getSelector2Name
argument_list|()
operator|+
literal|'.'
operator|+
name|getProperty2Name
argument_list|()
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
name|getSelector
argument_list|(
name|selector1Name
argument_list|)
expr_stmt|;
if|if
condition|(
name|selector1
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown selector: "
operator|+
name|selector1Name
argument_list|)
throw|;
block|}
name|selector2
operator|=
name|source
operator|.
name|getSelector
argument_list|(
name|selector2Name
argument_list|)
expr_stmt|;
if|if
condition|(
name|selector2
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown selector: "
operator|+
name|selector2Name
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
name|ScalarImpl
name|v1
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
name|v1
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// TODO data type mapping
name|ScalarImpl
name|v2
init|=
name|selector2
operator|.
name|currentProperty
argument_list|(
name|property2Name
argument_list|)
decl_stmt|;
return|return
name|v2
operator|!=
literal|null
operator|&&
name|v1
operator|.
name|equals
argument_list|(
name|v2
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|Filter
name|f
parameter_list|)
block|{
name|ScalarImpl
name|v1
init|=
name|selector1
operator|.
name|currentProperty
argument_list|(
name|property1Name
argument_list|)
decl_stmt|;
name|ScalarImpl
name|v2
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
name|v2
operator|!=
literal|null
condition|)
block|{
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
name|v2
argument_list|)
expr_stmt|;
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
name|v1
operator|!=
literal|null
condition|)
block|{
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
name|v1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

