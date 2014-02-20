begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|kernel
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
name|regex
operator|.
name|Pattern
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
name|commons
operator|.
name|json
operator|.
name|JsopTokenizer
import|;
end_import

begin_comment
comment|/**  * Utility class for deciding which nodes and properties to serialize.  */
end_comment

begin_class
class|class
name|JsonFilter
block|{
specifier|private
specifier|static
specifier|final
name|Pattern
name|EVERYTHING
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Pattern
argument_list|>
name|nodeIncludes
init|=
name|newArrayList
argument_list|(
name|EVERYTHING
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Pattern
argument_list|>
name|nodeExcludes
init|=
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Pattern
argument_list|>
name|propertyIncludes
init|=
name|newArrayList
argument_list|(
name|EVERYTHING
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Pattern
argument_list|>
name|propertyExcludes
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|JsonFilter
parameter_list|(
name|String
name|filter
parameter_list|)
block|{
name|JsopTokenizer
name|tokenizer
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
for|for
control|(
name|boolean
name|first
init|=
literal|true
init|;
operator|!
name|tokenizer
operator|.
name|matches
argument_list|(
literal|'}'
argument_list|)
condition|;
name|first
operator|=
literal|false
control|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|tokenizer
operator|.
name|read
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|String
name|key
init|=
name|tokenizer
operator|.
name|readString
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Pattern
argument_list|>
name|includes
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Pattern
argument_list|>
name|excludes
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|readPatterns
argument_list|(
name|tokenizer
argument_list|,
name|includes
argument_list|,
name|excludes
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"nodes"
argument_list|)
condition|)
block|{
name|nodeIncludes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|nodeIncludes
operator|.
name|addAll
argument_list|(
name|includes
argument_list|)
expr_stmt|;
name|nodeExcludes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|nodeExcludes
operator|.
name|addAll
argument_list|(
name|excludes
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"properties"
argument_list|)
condition|)
block|{
name|propertyIncludes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|propertyIncludes
operator|.
name|addAll
argument_list|(
name|includes
argument_list|)
expr_stmt|;
name|propertyExcludes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|propertyExcludes
operator|.
name|addAll
argument_list|(
name|excludes
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|key
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|readPatterns
parameter_list|(
name|JsopTokenizer
name|tokenizer
parameter_list|,
name|List
argument_list|<
name|Pattern
argument_list|>
name|includes
parameter_list|,
name|List
argument_list|<
name|Pattern
argument_list|>
name|excludes
parameter_list|)
block|{
name|tokenizer
operator|.
name|read
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
for|for
control|(
name|boolean
name|first
init|=
literal|true
init|;
operator|!
name|tokenizer
operator|.
name|matches
argument_list|(
literal|']'
argument_list|)
condition|;
name|first
operator|=
literal|false
control|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|tokenizer
operator|.
name|read
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|String
name|pattern
init|=
name|tokenizer
operator|.
name|readString
argument_list|()
decl_stmt|;
if|if
condition|(
name|pattern
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|excludes
operator|.
name|add
argument_list|(
name|glob
argument_list|(
name|pattern
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pattern
operator|.
name|startsWith
argument_list|(
literal|"\\-"
argument_list|)
condition|)
block|{
name|includes
operator|.
name|add
argument_list|(
name|glob
argument_list|(
name|pattern
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|includes
operator|.
name|add
argument_list|(
name|glob
argument_list|(
name|pattern
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|Pattern
name|glob
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|star
init|=
name|pattern
operator|.
name|indexOf
argument_list|(
literal|'*'
argument_list|)
decl_stmt|;
while|while
condition|(
name|star
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|star
operator|>
literal|0
operator|&&
name|pattern
operator|.
name|charAt
argument_list|(
name|star
operator|-
literal|1
argument_list|)
operator|==
literal|'\\'
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
name|pattern
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|star
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
literal|"*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
name|pattern
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|star
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|".*"
argument_list|)
expr_stmt|;
block|}
name|pattern
operator|=
name|pattern
operator|.
name|substring
argument_list|(
name|star
operator|+
literal|1
argument_list|)
expr_stmt|;
name|star
operator|=
name|pattern
operator|.
name|indexOf
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
name|pattern
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Pattern
operator|.
name|compile
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
name|boolean
name|includeNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|include
argument_list|(
name|name
argument_list|,
name|nodeIncludes
argument_list|,
name|nodeExcludes
argument_list|)
return|;
block|}
name|boolean
name|includeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|include
argument_list|(
name|name
argument_list|,
name|propertyIncludes
argument_list|,
name|propertyExcludes
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|include
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Pattern
argument_list|>
name|includes
parameter_list|,
name|List
argument_list|<
name|Pattern
argument_list|>
name|excludes
parameter_list|)
block|{
for|for
control|(
name|Pattern
name|include
range|:
name|includes
control|)
block|{
if|if
condition|(
name|include
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
for|for
control|(
name|Pattern
name|exclude
range|:
name|excludes
control|)
block|{
if|if
condition|(
name|exclude
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
operator|.
name|matches
argument_list|()
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
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

