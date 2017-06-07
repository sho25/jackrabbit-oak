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
name|api
operator|.
name|Type
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyValues
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
name|QueryIndex
operator|.
name|FulltextQueryIndex
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Support for "similar(...)  */
end_comment

begin_class
specifier|public
class|class
name|SimilarImpl
extends|extends
name|ConstraintImpl
block|{
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NATIVE_LUCENE_LANGUAGE
init|=
literal|"lucene"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MORE_LIKE_THIS_PREFIX
init|=
literal|"mlt?mlt.fl=:path&mlt.mindf=0&stream.body="
decl_stmt|;
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
specifier|final
name|StaticOperandImpl
name|pathExpression
decl_stmt|;
specifier|private
name|SelectorImpl
name|selector
decl_stmt|;
name|SimilarImpl
parameter_list|(
name|String
name|selectorName
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|StaticOperandImpl
name|pathExpression
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
name|this
operator|.
name|pathExpression
operator|=
name|pathExpression
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"similar("
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|quote
argument_list|(
name|selectorName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|propertyName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
operator|.
name|append
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|getPathExpression
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
comment|// disable evaluation if a fulltext index is used,
comment|// and because we don't know how to process native
comment|// conditions
if|if
condition|(
operator|!
operator|(
name|selector
operator|.
name|getIndex
argument_list|()
operator|instanceof
name|FulltextQueryIndex
operator|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No full-text index was found that can process the condition "
operator|+
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// verify the path is readable
name|PropertyValue
name|p
init|=
name|pathExpression
operator|.
name|currentValue
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
if|if
condition|(
name|selector
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
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
name|propertyName
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|selector
operator|.
name|currentProperty
argument_list|(
name|propertyName
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// property not found
return|return
literal|false
return|;
block|}
block|}
comment|// we assume the index only returns the requested entries
return|return
literal|true
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
name|emptySet
argument_list|()
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
name|selector
argument_list|)
condition|)
block|{
name|PropertyValue
name|p
init|=
name|pathExpression
operator|.
name|currentValue
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|String
name|query
init|=
name|MORE_LIKE_THIS_PREFIX
operator|+
name|path
decl_stmt|;
name|PropertyValue
name|v
init|=
name|PropertyValues
operator|.
name|newString
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
name|NativeFunctionImpl
operator|.
name|NATIVE_PREFIX
operator|+
name|NATIVE_LUCENE_LANGUAGE
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|v
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
name|selector
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
name|emptySet
argument_list|()
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
specifier|public
name|StaticOperandImpl
name|getPathExpression
parameter_list|()
block|{
return|return
name|pathExpression
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
name|SimilarImpl
argument_list|(
name|selectorName
argument_list|,
name|propertyName
argument_list|,
name|pathExpression
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|requiresFullTextIndex
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

