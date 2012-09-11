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
name|jcr
operator|.
name|query
operator|.
name|qom
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|qom
operator|.
name|PropertyExistence
import|;
end_import

begin_comment
comment|/**  * The implementation of the corresponding JCR interface.  */
end_comment

begin_class
specifier|public
class|class
name|PropertyExistenceImpl
extends|extends
name|ConstraintImpl
implements|implements
name|PropertyExistence
block|{
specifier|private
specifier|final
name|String
name|selectorName
decl_stmt|;
specifier|private
specifier|final
name|String
name|propertyName
decl_stmt|;
specifier|public
name|PropertyExistenceImpl
parameter_list|(
name|String
name|selectorName
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
name|this
operator|.
name|selectorName
operator|=
name|selectorName
expr_stmt|;
name|this
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPropertyName
parameter_list|()
block|{
return|return
name|propertyName
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSelectorName
parameter_list|()
block|{
return|return
name|selectorName
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
if|if
condition|(
name|selectorName
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|quoteSelectorName
argument_list|(
name|selectorName
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|propertyName
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|quotePropertyName
argument_list|(
name|propertyName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|" IS NOT NULL"
argument_list|)
expr_stmt|;
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
name|void
name|bindVariables
parameter_list|(
name|QueryObjectModelImpl
name|qom
parameter_list|)
block|{
comment|// ignore
block|}
block|}
end_class

end_unit

