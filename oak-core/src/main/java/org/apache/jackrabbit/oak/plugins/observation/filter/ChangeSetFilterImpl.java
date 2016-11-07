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
name|concat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|observation
operator|.
name|ChangeSet
import|;
end_import

begin_class
specifier|public
class|class
name|ChangeSetFilterImpl
implements|implements
name|ChangeSetFilter
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|rootIncludePaths
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|Pattern
argument_list|>
name|includePathPatterns
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|Pattern
argument_list|>
name|excludePathPatterns
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|parentNodeNames
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|parentNodeTypes
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|propertyNames
decl_stmt|;
specifier|public
name|ChangeSetFilterImpl
parameter_list|(
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|includedParentPaths
parameter_list|,
name|boolean
name|isDeep
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|excludedParentPaths
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|parentNodeNames
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|parentNodeTypes
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|propertyNames
parameter_list|)
block|{
name|this
operator|.
name|rootIncludePaths
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|includePathPatterns
operator|=
operator|new
name|HashSet
argument_list|<
name|Pattern
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|aRawIncludePath
range|:
name|includedParentPaths
control|)
block|{
specifier|final
name|String
name|aGlobbingIncludePath
decl_stmt|;
if|if
condition|(
name|aRawIncludePath
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
comment|// then isDeep is not applicable, it is already a glob path
name|aGlobbingIncludePath
operator|=
name|aRawIncludePath
expr_stmt|;
block|}
else|else
block|{
name|aGlobbingIncludePath
operator|=
operator|!
name|isDeep
condition|?
name|aRawIncludePath
else|:
name|concat
argument_list|(
name|aRawIncludePath
argument_list|,
literal|"**"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|rootIncludePaths
operator|.
name|add
argument_list|(
name|aRawIncludePath
argument_list|)
expr_stmt|;
name|this
operator|.
name|includePathPatterns
operator|.
name|add
argument_list|(
name|asPattern
argument_list|(
name|aGlobbingIncludePath
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|excludePathPatterns
operator|=
operator|new
name|HashSet
argument_list|<
name|Pattern
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|aRawExcludePath
range|:
name|excludedParentPaths
control|)
block|{
name|this
operator|.
name|excludePathPatterns
operator|.
name|add
argument_list|(
name|asPattern
argument_list|(
name|concat
argument_list|(
name|aRawExcludePath
argument_list|,
literal|"**"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|propertyNames
operator|=
name|propertyNames
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|propertyNames
argument_list|)
expr_stmt|;
name|this
operator|.
name|parentNodeTypes
operator|=
name|parentNodeTypes
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|parentNodeTypes
argument_list|)
expr_stmt|;
name|this
operator|.
name|parentNodeNames
operator|=
name|parentNodeNames
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|parentNodeNames
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Pattern
name|asPattern
parameter_list|(
name|String
name|patternWithGlobs
parameter_list|)
block|{
return|return
name|Pattern
operator|.
name|compile
argument_list|(
name|GlobbingPathHelper
operator|.
name|globPathAsRegex
argument_list|(
name|patternWithGlobs
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|excludes
parameter_list|(
name|ChangeSet
name|changeSet
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|parentPaths
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|changeSet
operator|.
name|getParentPaths
argument_list|()
argument_list|)
decl_stmt|;
comment|// first go through excludes to remove those that are explicitly
comment|// excluded
if|if
condition|(
name|this
operator|.
name|excludePathPatterns
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|parentPaths
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|String
name|aParentPath
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|patternsMatch
argument_list|(
name|this
operator|.
name|excludePathPatterns
argument_list|,
name|aParentPath
argument_list|)
condition|)
block|{
comment|// if an exclude pattern matches, remove the parentPath
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// note that cut-off paths are not applied with excludes,
comment|// eg if excludePaths contains /var/foo/bar and path contains /var/foo
comment|// with a maxPathLevel of 2, that might very well mean that
comment|// the actual path would have been /var/foo/bar, but we don't know.
comment|// so we cannot exclude it here and thus have a potential false negative
comment|// (ie we didn't exclude it in the prefilter)
comment|// now remainingPaths contains what is not excluded,
comment|// then check if it is included
name|boolean
name|included
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|aPath
range|:
name|parentPaths
control|)
block|{
comment|// direct set contains is fastest, lets try that first
if|if
condition|(
name|this
operator|.
name|rootIncludePaths
operator|.
name|contains
argument_list|(
name|aPath
argument_list|)
condition|)
block|{
name|included
operator|=
literal|true
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|patternsMatch
argument_list|(
name|this
operator|.
name|includePathPatterns
argument_list|,
name|aPath
argument_list|)
condition|)
block|{
name|included
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|included
condition|)
block|{
comment|// well then we can definitely say that this commit is excluded
return|return
literal|true
return|;
block|}
if|if
condition|(
name|this
operator|.
name|propertyNames
operator|!=
literal|null
operator|&&
name|this
operator|.
name|propertyNames
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|included
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|String
name|aProperty
range|:
name|changeSet
operator|.
name|getPropertyNames
argument_list|()
control|)
block|{
if|if
condition|(
name|this
operator|.
name|propertyNames
operator|.
name|contains
argument_list|(
name|aProperty
argument_list|)
condition|)
block|{
name|included
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
comment|// if propertyNames are defined then if we can't find any
comment|// at this stage (if !included) then this equals to filtering out
if|if
condition|(
operator|!
name|included
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// otherwise we have found a match, but one of the
comment|// nodeType/nodeNames
comment|// could still filter out, so we have to continue...
block|}
if|if
condition|(
name|this
operator|.
name|parentNodeTypes
operator|!=
literal|null
operator|&&
name|this
operator|.
name|parentNodeTypes
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|included
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|String
name|aNodeType
range|:
name|changeSet
operator|.
name|getParentNodeTypes
argument_list|()
control|)
block|{
if|if
condition|(
name|this
operator|.
name|parentNodeTypes
operator|.
name|contains
argument_list|(
name|aNodeType
argument_list|)
condition|)
block|{
name|included
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
comment|// same story here: if nodeTypes is defined and we can't find any
comment|// match
comment|// then we're done now
if|if
condition|(
operator|!
name|included
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// otherwise, again, continue
block|}
if|if
condition|(
name|this
operator|.
name|parentNodeNames
operator|!=
literal|null
operator|&&
name|this
operator|.
name|parentNodeNames
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|included
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|String
name|aNodeName
range|:
name|changeSet
operator|.
name|getParentNodeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|this
operator|.
name|parentNodeNames
operator|.
name|contains
argument_list|(
name|aNodeName
argument_list|)
condition|)
block|{
name|included
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
comment|// and a 3rd time, if we can't find any nodeName match
comment|// here, then we're filtering out
if|if
condition|(
operator|!
name|included
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
comment|// at this stage we haven't found any exclude, so we're likely including
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
name|boolean
name|patternsMatch
parameter_list|(
name|Set
argument_list|<
name|Pattern
argument_list|>
name|pathPatterns
parameter_list|,
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|Pattern
name|pathPattern
range|:
name|pathPatterns
control|)
block|{
if|if
condition|(
name|pathPattern
operator|.
name|matcher
argument_list|(
name|path
argument_list|)
operator|.
name|matches
argument_list|()
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
block|}
end_class

end_unit

