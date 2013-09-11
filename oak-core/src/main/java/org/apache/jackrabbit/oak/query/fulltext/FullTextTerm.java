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

begin_comment
comment|/**  * A fulltext term, or a "not" term.  */
end_comment

begin_class
specifier|public
class|class
name|FullTextTerm
extends|extends
name|FullTextExpression
block|{
specifier|private
specifier|final
name|boolean
name|not
decl_stmt|;
specifier|private
specifier|final
name|String
name|propertyName
decl_stmt|;
specifier|private
specifier|final
name|String
name|text
decl_stmt|;
specifier|private
specifier|final
name|String
name|filteredText
decl_stmt|;
specifier|private
specifier|final
name|String
name|boost
decl_stmt|;
specifier|private
specifier|final
name|LikePattern
name|like
decl_stmt|;
specifier|public
name|FullTextTerm
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|FullTextTerm
name|copy
parameter_list|)
block|{
name|this
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
name|this
operator|.
name|not
operator|=
name|copy
operator|.
name|not
expr_stmt|;
name|this
operator|.
name|text
operator|=
name|copy
operator|.
name|text
expr_stmt|;
name|this
operator|.
name|filteredText
operator|=
name|copy
operator|.
name|filteredText
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|copy
operator|.
name|boost
expr_stmt|;
name|this
operator|.
name|like
operator|=
name|copy
operator|.
name|like
expr_stmt|;
block|}
specifier|public
name|FullTextTerm
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|String
name|text
parameter_list|,
name|boolean
name|not
parameter_list|,
name|boolean
name|escaped
parameter_list|,
name|String
name|boost
parameter_list|)
block|{
name|this
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|not
operator|=
name|not
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
comment|// for testFulltextIntercapSQL
comment|// filter special characters such as '
comment|// to make tests pass, for example the
comment|// FulltextQueryTest.testFulltextExcludeSQL,
comment|// which searches for:
comment|// "text ''fox jumps'' -other"
comment|// (please note the two single quotes instead of
comment|// double quotes before for and after jumps)
name|boolean
name|pattern
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|escaped
condition|)
block|{
name|filteredText
operator|=
name|text
expr_stmt|;
block|}
else|else
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|text
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|text
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'*'
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'%'
argument_list|)
expr_stmt|;
name|pattern
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'?'
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'_'
argument_list|)
expr_stmt|;
name|pattern
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'_'
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"\\_"
argument_list|)
expr_stmt|;
name|pattern
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|c
argument_list|)
operator|||
literal|" +-:&"
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|filteredText
operator|=
name|buff
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|pattern
condition|)
block|{
name|like
operator|=
operator|new
name|LikePattern
argument_list|(
literal|"%"
operator|+
name|filteredText
operator|+
literal|"%"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|like
operator|=
literal|null
expr_stmt|;
block|}
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
comment|// toLowerCase for testFulltextIntercapSQL
name|value
operator|=
name|value
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
if|if
condition|(
name|like
operator|!=
literal|null
condition|)
block|{
return|return
name|like
operator|.
name|matches
argument_list|(
name|value
argument_list|)
return|;
block|}
if|if
condition|(
name|not
condition|)
block|{
return|return
name|value
operator|.
name|indexOf
argument_list|(
name|filteredText
argument_list|)
operator|<
literal|0
return|;
block|}
return|return
name|value
operator|.
name|indexOf
argument_list|(
name|filteredText
argument_list|)
operator|>=
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|FullTextExpression
name|simplify
parameter_list|()
block|{
return|return
name|this
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
name|not
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|propertyName
operator|!=
literal|null
operator|&&
operator|!
literal|"*"
operator|.
name|equals
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|propertyName
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|'\"'
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|text
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|text
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\\'
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'\"'
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|'\"'
argument_list|)
expr_stmt|;
if|if
condition|(
name|boost
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'^'
argument_list|)
operator|.
name|append
argument_list|(
name|boost
argument_list|)
expr_stmt|;
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|getPropertyName
parameter_list|()
block|{
return|return
name|propertyName
return|;
block|}
specifier|public
name|String
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
specifier|public
name|boolean
name|isNot
parameter_list|()
block|{
return|return
name|not
return|;
block|}
specifier|public
name|String
name|getText
parameter_list|()
block|{
return|return
name|text
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
name|PRECEDENCE_TERM
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

