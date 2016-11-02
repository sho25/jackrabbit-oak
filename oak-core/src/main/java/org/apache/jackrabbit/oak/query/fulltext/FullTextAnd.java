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
name|fulltext
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A fulltext "and" condition.  */
end_comment

begin_class
specifier|public
class|class
name|FullTextAnd
extends|extends
name|FullTextExpression
block|{
specifier|public
specifier|final
name|List
argument_list|<
name|FullTextExpression
argument_list|>
name|list
decl_stmt|;
specifier|public
name|FullTextAnd
parameter_list|(
name|List
argument_list|<
name|FullTextExpression
argument_list|>
name|list
parameter_list|)
block|{
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|String
name|value
parameter_list|)
block|{
for|for
control|(
name|FullTextExpression
name|e
range|:
name|list
control|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|evaluate
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|FullTextExpression
name|simplify
parameter_list|()
block|{
name|Set
argument_list|<
name|FullTextExpression
argument_list|>
name|set
init|=
name|FullTextOr
operator|.
name|getUniqueSet
argument_list|(
name|list
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|set
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
return|;
block|}
name|ArrayList
argument_list|<
name|FullTextExpression
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|FullTextExpression
argument_list|>
argument_list|(
name|set
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|l
operator|.
name|addAll
argument_list|(
name|set
argument_list|)
expr_stmt|;
return|return
operator|new
name|FullTextAnd
argument_list|(
name|l
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
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FullTextExpression
name|e
range|:
name|list
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
literal|0
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|.
name|getPrecedence
argument_list|()
operator|<
name|getPrecedence
argument_list|()
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|.
name|getPrecedence
argument_list|()
operator|<
name|getPrecedence
argument_list|()
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
block|}
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
name|int
name|getPrecedence
parameter_list|()
block|{
return|return
name|PRECEDENCE_AND
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|FullTextVisitor
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
block|}
end_class

end_unit

