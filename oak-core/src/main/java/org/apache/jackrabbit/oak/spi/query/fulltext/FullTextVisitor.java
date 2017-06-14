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
name|spi
operator|.
name|query
operator|.
name|fulltext
package|;
end_package

begin_comment
comment|/**  * A visitor for full-text expressions. This class is abstract because at least  * one of the methods needs to be implemented to make anything useful, most  * likely visit(FullTextTerm).  */
end_comment

begin_interface
specifier|public
interface|interface
name|FullTextVisitor
block|{
comment|/**      * Visit an "contains" expression.      *       * @param contains the "contains" expression      * @return true if visiting should continue      */
name|boolean
name|visit
parameter_list|(
name|FullTextContains
name|contains
parameter_list|)
function_decl|;
comment|/**      * Visit an "and" expression.      *       * @param and the "and" expression      * @return true if visiting should continue      */
name|boolean
name|visit
parameter_list|(
name|FullTextAnd
name|and
parameter_list|)
function_decl|;
comment|/**      * Visit an "or" expression.      *       * @param or the "or" expression      * @return true if visiting should continue      */
name|boolean
name|visit
parameter_list|(
name|FullTextOr
name|or
parameter_list|)
function_decl|;
comment|/**      * Visit a term      *       * @param term the term      * @return true if visiting should continue      */
name|boolean
name|visit
parameter_list|(
name|FullTextTerm
name|term
parameter_list|)
function_decl|;
comment|/**      * The base implementation of a full-text visitor.      */
specifier|public
specifier|abstract
specifier|static
class|class
name|FullTextVisitorBase
implements|implements
name|FullTextVisitor
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|FullTextContains
name|contains
parameter_list|)
block|{
return|return
name|contains
operator|.
name|getBase
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|FullTextAnd
name|and
parameter_list|)
block|{
for|for
control|(
name|FullTextExpression
name|e
range|:
name|and
operator|.
name|list
control|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|accept
argument_list|(
name|this
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
name|boolean
name|visit
parameter_list|(
name|FullTextOr
name|or
parameter_list|)
block|{
for|for
control|(
name|FullTextExpression
name|e
range|:
name|or
operator|.
name|list
control|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|accept
argument_list|(
name|this
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
block|}
block|}
end_interface

end_unit
