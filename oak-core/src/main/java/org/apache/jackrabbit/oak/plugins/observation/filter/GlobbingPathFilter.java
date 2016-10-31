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
name|observation
operator|.
name|filter
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
name|base
operator|.
name|Objects
operator|.
name|toStringHelper
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
name|checkNotNull
import|;
end_import

begin_import
import|import static
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
operator|.
name|elements
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|base
operator|.
name|Joiner
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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * This {@code Filter} implementation supports filtering on paths using  * simple glob patterns. Such a pattern is a string denoting a path. Each  * element of the pattern is matched against the corresponding element of  * a path. Elements of the pattern are matched literally except for the special  * elements {@code *} and {@code **} where the former matches an arbitrary  * path element and the latter matches any number of path elements (including none).  *<p>  * Note: an empty path pattern matches no path.  *<p>  * Note: path patterns only match against the corresponding elements of the path  * and<em>do not</em> distinguish between absolute and relative paths.  *<p>  * Note: there is no way to escape {@code *} and {@code **}.  *<p>  * Examples:  *<pre>  *    q matches q only  *    * matches character matches zero or more characters of a name component without crossing path boundaries (ie without crossing /)  *    ** matches every path  *    a/b/c matches a/b/c only  *    a/*&#47;c matches a/x/c for every element x  *    a/*.html&#47;c matches a/x.html/c for every character sequence x (that doesn't include /)  *    a/*.*&#47;c matches a/x.y/c for every character sequence x and y (that don't include /)  *    **&#47;y/z match every path ending in y/z  *    r/s/t&#47;** matches r/s/t and all its descendants  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|GlobbingPathFilter
implements|implements
name|EventFilter
block|{
specifier|public
specifier|static
specifier|final
name|String
name|STAR
init|=
literal|"*"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|STAR_STAR
init|=
literal|"**"
decl_stmt|;
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|pattern
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Pattern
argument_list|>
name|patternMap
decl_stmt|;
specifier|private
name|GlobbingPathFilter
parameter_list|(
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|String
argument_list|>
name|pattern
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Pattern
argument_list|>
name|patternMap
parameter_list|)
block|{
name|this
operator|.
name|pattern
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|checkNotNull
argument_list|(
name|pattern
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|patternMap
operator|=
name|checkNotNull
argument_list|(
name|patternMap
argument_list|)
expr_stmt|;
block|}
specifier|public
name|GlobbingPathFilter
parameter_list|(
annotation|@
name|Nonnull
name|String
name|pattern
parameter_list|)
block|{
name|this
argument_list|(
name|elements
argument_list|(
name|pattern
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Pattern
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeAdd
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
return|return
name|includeItem
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeChange
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
return|return
name|includeItem
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeDelete
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
return|return
name|includeItem
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeAdd
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|includeItem
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeDelete
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
return|return
name|includeItem
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeMove
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|moved
parameter_list|)
block|{
return|return
name|includeItem
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeReorder
parameter_list|(
name|String
name|destName
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|reordered
parameter_list|)
block|{
return|return
name|includeItem
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|private
name|Pattern
name|getPattern
parameter_list|(
name|String
name|wildcardStr
parameter_list|)
block|{
name|Pattern
name|p
init|=
name|patternMap
operator|.
name|get
argument_list|(
name|wildcardStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|p
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\Q"
operator|+
name|wildcardStr
operator|.
name|replace
argument_list|(
literal|"*"
argument_list|,
literal|"\\E[^/]*\\Q"
argument_list|)
operator|+
literal|"\\E"
argument_list|)
expr_stmt|;
name|patternMap
operator|.
name|put
argument_list|(
name|wildcardStr
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
return|return
name|p
return|;
block|}
specifier|private
name|boolean
name|wildcardMatch
parameter_list|(
name|String
name|pathElement
parameter_list|,
name|String
name|wildcardStr
parameter_list|)
block|{
if|if
condition|(
name|STAR_STAR
operator|.
name|equals
argument_list|(
name|wildcardStr
argument_list|)
operator|||
operator|!
name|wildcardStr
operator|.
name|contains
argument_list|(
name|STAR
argument_list|)
condition|)
block|{
return|return
name|pathElement
operator|.
name|equals
argument_list|(
name|wildcardStr
argument_list|)
return|;
block|}
name|Pattern
name|regexPattern
init|=
name|getPattern
argument_list|(
name|wildcardStr
argument_list|)
decl_stmt|;
return|return
name|regexPattern
operator|.
name|matcher
argument_list|(
name|pathElement
argument_list|)
operator|.
name|matches
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|EventFilter
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|pattern
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|head
init|=
name|pattern
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|pattern
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
operator|!
name|STAR_STAR
operator|.
name|equals
argument_list|(
name|head
argument_list|)
condition|)
block|{
comment|// shortcut when no further matches are possible
return|return
literal|null
return|;
block|}
if|if
condition|(
name|wildcardMatch
argument_list|(
name|name
argument_list|,
name|head
argument_list|)
condition|)
block|{
return|return
operator|new
name|GlobbingPathFilter
argument_list|(
name|pattern
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|pattern
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
name|patternMap
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|STAR_STAR
operator|.
name|equals
argument_list|(
name|head
argument_list|)
condition|)
block|{
if|if
condition|(
name|pattern
operator|.
name|size
argument_list|()
operator|>=
literal|2
operator|&&
name|pattern
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// ** matches empty list of elements and pattern.get(1) matches name
comment|// match the rest of the pattern against the rest of the path and
comment|// match the whole pattern against the rest of the path
return|return
name|Filters
operator|.
name|any
argument_list|(
operator|new
name|GlobbingPathFilter
argument_list|(
name|pattern
operator|.
name|subList
argument_list|(
literal|2
argument_list|,
name|pattern
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
name|patternMap
argument_list|)
argument_list|,
operator|new
name|GlobbingPathFilter
argument_list|(
name|pattern
argument_list|,
name|patternMap
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
comment|// ** matches name, match the whole pattern against the rest of the path
return|return
operator|new
name|GlobbingPathFilter
argument_list|(
name|pattern
argument_list|,
name|patternMap
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|add
argument_list|(
literal|"path"
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|'/'
argument_list|)
operator|.
name|join
argument_list|(
name|pattern
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|boolean
name|includeItem
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|pattern
operator|.
name|isEmpty
argument_list|()
operator|&&
name|pattern
operator|.
name|size
argument_list|()
operator|<=
literal|2
condition|)
block|{
name|String
name|head
init|=
name|pattern
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|boolean
name|headMatches
init|=
name|wildcardMatch
argument_list|(
name|name
argument_list|,
name|head
argument_list|)
operator|||
name|STAR_STAR
operator|.
name|equals
argument_list|(
name|head
argument_list|)
decl_stmt|;
return|return
name|pattern
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
name|headMatches
else|:
name|headMatches
operator|&&
name|STAR_STAR
operator|.
name|equals
argument_list|(
name|pattern
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

