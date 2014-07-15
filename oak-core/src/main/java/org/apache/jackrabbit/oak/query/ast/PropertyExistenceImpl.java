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
name|Collections
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
comment|/**  * A condition to check if the property exists ("is not null").  */
end_comment

begin_class
specifier|public
class|class
name|PropertyExistenceImpl
extends|extends
name|ConstraintImpl
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
specifier|private
name|SelectorImpl
name|selector
decl_stmt|;
specifier|public
name|PropertyExistenceImpl
parameter_list|(
name|SelectorImpl
name|selector
parameter_list|,
name|String
name|selectorName
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
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
name|boolean
name|evaluate
parameter_list|()
block|{
return|return
name|selector
operator|.
name|currentProperty
argument_list|(
name|propertyName
argument_list|)
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|PropertyExistenceImpl
argument_list|>
name|getPropertyExistenceConditions
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|SelectorImpl
argument_list|>
name|getSelectors
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|selector
argument_list|)
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
return|return
name|quote
argument_list|(
name|selectorName
argument_list|)
operator|+
literal|'.'
operator|+
name|quote
argument_list|(
name|propertyName
argument_list|)
operator|+
literal|" is not null"
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
name|selector
operator|=
name|source
operator|.
name|getExistingSelector
argument_list|(
name|selectorName
argument_list|)
expr_stmt|;
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
name|selector
argument_list|)
condition|)
block|{
name|String
name|pn
init|=
name|normalizePropertyName
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
name|pn
argument_list|,
name|Operator
operator|.
name|NOT_EQUAL
argument_list|,
literal|null
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
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
name|selector
argument_list|)
condition|)
block|{
name|s
operator|.
name|restrictSelector
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|String
name|pn
init|=
name|normalizePropertyName
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|selectorName
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|selectorName
operator|.
name|hashCode
argument_list|()
operator|)
operator|*
literal|31
operator|+
operator|(
operator|(
name|pn
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|pn
operator|.
name|hashCode
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|PropertyExistenceImpl
name|other
init|=
operator|(
name|PropertyExistenceImpl
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|equalsStrings
argument_list|(
name|selectorName
argument_list|,
name|other
operator|.
name|selectorName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|pn
init|=
name|normalizePropertyName
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
name|String
name|pn2
init|=
name|normalizePropertyName
argument_list|(
name|other
operator|.
name|propertyName
argument_list|)
decl_stmt|;
return|return
name|equalsStrings
argument_list|(
name|pn
argument_list|,
name|pn2
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|equalsStrings
parameter_list|(
name|String
name|a
parameter_list|,
name|String
name|b
parameter_list|)
block|{
return|return
name|a
operator|==
literal|null
operator|||
name|b
operator|==
literal|null
condition|?
name|a
operator|==
name|b
else|:
name|a
operator|.
name|equals
argument_list|(
name|b
argument_list|)
return|;
block|}
block|}
end_class

end_unit

