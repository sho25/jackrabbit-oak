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
name|plugins
operator|.
name|document
operator|.
name|bundlor
package|;
end_package

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|PathUtils
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkElementIndex
import|;
end_import

begin_comment
comment|/**  * Include represents a single path pattern which captures the path which  * needs to be included in bundling. Path patterns can be like below.  *<ul>  *<li>* - Match any immediate child</li>  *<li>*\/* - Match child with any name upto 2 levels of depth</li>  *<li>jcr:content - Match immediate child with name jcr:content</li>  *<li>jcr:content\/** - Match jcr:content and all its child</li>  *</ul>  *  * The last path element can specify a directive. Supported directive  *<ul>  *<li>all - Include all nodes under given path</li>  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|Include
block|{
specifier|private
specifier|static
specifier|final
name|String
name|STAR
init|=
literal|"*"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|STAR_STAR
init|=
literal|"**"
decl_stmt|;
enum|enum
name|Directive
block|{
name|ALL
block|,
name|NONE
block|}
specifier|private
specifier|final
name|String
index|[]
name|elements
decl_stmt|;
specifier|private
specifier|final
name|Directive
name|directive
decl_stmt|;
specifier|private
specifier|final
name|String
name|pattern
decl_stmt|;
specifier|public
name|Include
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|pathElements
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|PathUtils
operator|.
name|elements
argument_list|(
name|pattern
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|elementList
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|pathElements
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Directive
name|directive
init|=
name|Directive
operator|.
name|NONE
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
name|pathElements
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|e
init|=
name|pathElements
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|indexOfColon
init|=
name|e
operator|.
name|indexOf
argument_list|(
literal|";"
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexOfColon
operator|>
literal|0
condition|)
block|{
name|directive
operator|=
name|Directive
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|substring
argument_list|(
name|indexOfColon
operator|+
literal|1
argument_list|)
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|=
name|e
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|indexOfColon
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|STAR_STAR
operator|.
name|equals
argument_list|(
name|e
argument_list|)
condition|)
block|{
name|e
operator|=
name|STAR
expr_stmt|;
name|directive
operator|=
name|Directive
operator|.
name|ALL
expr_stmt|;
block|}
name|elementList
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|directive
operator|!=
name|Directive
operator|.
name|NONE
operator|&&
name|i
operator|<
name|pathElements
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Directive can only be specified for last path segment ["
operator|+
name|pattern
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|elements
operator|=
name|elementList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|directive
operator|=
name|directive
expr_stmt|;
name|this
operator|.
name|pattern
operator|=
name|pattern
expr_stmt|;
block|}
specifier|public
name|boolean
name|match
parameter_list|(
name|String
name|relativePath
parameter_list|)
block|{
name|Matcher
name|m
init|=
name|createMatcher
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|e
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|relativePath
argument_list|)
control|)
block|{
name|m
operator|=
name|m
operator|.
name|next
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|m
operator|.
name|isMatch
argument_list|()
return|;
block|}
specifier|public
name|String
name|getPattern
parameter_list|()
block|{
return|return
name|pattern
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
name|pattern
return|;
block|}
specifier|public
name|Matcher
name|createMatcher
parameter_list|()
block|{
return|return
operator|new
name|IncludeMatcher
argument_list|(
name|this
argument_list|)
return|;
block|}
name|Directive
name|getDirective
parameter_list|()
block|{
return|return
name|directive
return|;
block|}
specifier|public
name|boolean
name|match
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
name|checkElementIndex
argument_list|(
name|depth
argument_list|,
name|elements
operator|.
name|length
argument_list|)
expr_stmt|;
name|String
name|e
init|=
name|elements
index|[
name|depth
index|]
decl_stmt|;
return|return
name|STAR
operator|.
name|equals
argument_list|(
name|e
argument_list|)
operator|||
name|nodeName
operator|.
name|equals
argument_list|(
name|e
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|elements
operator|.
name|length
return|;
block|}
block|}
end_class

end_unit

