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
name|plugins
operator|.
name|index
operator|.
name|property
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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
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
name|state
operator|.
name|NodeBuilder
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
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * A value pattern.  */
end_comment

begin_class
specifier|public
class|class
name|ValuePattern
block|{
specifier|private
specifier|final
name|Pattern
name|pattern
decl_stmt|;
specifier|private
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|includePrefixes
decl_stmt|;
specifier|private
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|excludePrefixes
decl_stmt|;
specifier|public
name|ValuePattern
parameter_list|(
name|NodeBuilder
name|node
parameter_list|)
block|{
name|this
argument_list|(
name|node
operator|.
name|getString
argument_list|(
name|IndexConstants
operator|.
name|VALUE_PATTERN
argument_list|)
argument_list|,
name|getStrings
argument_list|(
name|node
argument_list|,
name|IndexConstants
operator|.
name|VALUE_INCLUDED_PREFIXES
argument_list|)
argument_list|,
name|getStrings
argument_list|(
name|node
argument_list|,
name|IndexConstants
operator|.
name|VALUE_EXCLUDED_PREFIXES
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ValuePattern
parameter_list|(
name|NodeState
name|node
parameter_list|)
block|{
name|this
argument_list|(
name|node
operator|.
name|getString
argument_list|(
name|IndexConstants
operator|.
name|VALUE_PATTERN
argument_list|)
argument_list|,
name|getStrings
argument_list|(
name|node
argument_list|,
name|IndexConstants
operator|.
name|VALUE_INCLUDED_PREFIXES
argument_list|)
argument_list|,
name|getStrings
argument_list|(
name|node
argument_list|,
name|IndexConstants
operator|.
name|VALUE_EXCLUDED_PREFIXES
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ValuePattern
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ValuePattern
parameter_list|(
name|String
name|pattern
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|includePrefixes
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|excludePrefixes
parameter_list|)
block|{
name|Pattern
name|p
init|=
name|pattern
operator|==
literal|null
condition|?
literal|null
else|:
name|Pattern
operator|.
name|compile
argument_list|(
name|pattern
argument_list|)
decl_stmt|;
name|this
operator|.
name|includePrefixes
operator|=
name|includePrefixes
expr_stmt|;
name|this
operator|.
name|excludePrefixes
operator|=
name|excludePrefixes
expr_stmt|;
name|this
operator|.
name|pattern
operator|=
name|p
expr_stmt|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|v
parameter_list|)
block|{
if|if
condition|(
name|matchesAll
argument_list|()
operator|||
name|v
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|includePrefixes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|inc
range|:
name|includePrefixes
control|)
block|{
if|if
condition|(
name|v
operator|.
name|startsWith
argument_list|(
name|inc
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
if|if
condition|(
name|excludePrefixes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|exc
range|:
name|excludePrefixes
control|)
block|{
if|if
condition|(
name|v
operator|.
name|startsWith
argument_list|(
name|exc
argument_list|)
operator|||
name|exc
operator|.
name|startsWith
argument_list|(
name|v
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
if|if
condition|(
name|includePrefixes
operator|!=
literal|null
operator|&&
name|pattern
operator|==
literal|null
condition|)
block|{
comment|// we have include prefixes and no pattern
return|return
literal|false
return|;
block|}
return|return
name|pattern
operator|==
literal|null
operator|||
name|pattern
operator|.
name|matcher
argument_list|(
name|v
argument_list|)
operator|.
name|matches
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|matchesAll
parameter_list|()
block|{
return|return
name|includePrefixes
operator|==
literal|null
operator|&&
name|excludePrefixes
operator|==
literal|null
operator|&&
name|pattern
operator|==
literal|null
return|;
block|}
specifier|public
specifier|static
name|Iterable
argument_list|<
name|String
argument_list|>
name|getStrings
parameter_list|(
name|NodeBuilder
name|node
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
if|if
condition|(
operator|!
name|node
operator|.
name|hasProperty
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|PropertyState
name|s
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
name|node
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
return|;
block|}
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|node
operator|.
name|getString
argument_list|(
name|propertyName
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Iterable
argument_list|<
name|String
argument_list|>
name|getStrings
parameter_list|(
name|NodeState
name|node
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
if|if
condition|(
operator|!
name|node
operator|.
name|hasProperty
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|PropertyState
name|s
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
name|node
operator|.
name|getStrings
argument_list|(
name|propertyName
argument_list|)
return|;
block|}
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|node
operator|.
name|getString
argument_list|(
name|propertyName
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|matchesAll
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
if|if
condition|(
name|matchesAll
argument_list|()
operator|||
name|values
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
for|for
control|(
name|String
name|v
range|:
name|values
control|)
block|{
if|if
condition|(
operator|!
name|matches
argument_list|(
name|v
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
specifier|public
name|boolean
name|matchesPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
if|if
condition|(
name|pattern
operator|!=
literal|null
condition|)
block|{
comment|// with a regular expression pattern, we don't know
return|return
literal|false
return|;
block|}
if|if
condition|(
name|includePrefixes
operator|==
literal|null
operator|&&
name|excludePrefixes
operator|==
literal|null
condition|)
block|{
comment|// no includes and excludes
return|return
literal|true
return|;
block|}
if|if
condition|(
name|prefix
operator|==
literal|null
operator|||
name|prefix
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// we just have "> x" or "< y":
comment|// comparison is not supported
return|return
literal|false
return|;
block|}
if|if
condition|(
name|includePrefixes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|inc
range|:
name|includePrefixes
control|)
block|{
if|if
condition|(
name|prefix
operator|.
name|startsWith
argument_list|(
name|inc
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
if|if
condition|(
name|excludePrefixes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|exc
range|:
name|excludePrefixes
control|)
block|{
if|if
condition|(
name|prefix
operator|.
name|startsWith
argument_list|(
name|exc
argument_list|)
operator|||
name|exc
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

