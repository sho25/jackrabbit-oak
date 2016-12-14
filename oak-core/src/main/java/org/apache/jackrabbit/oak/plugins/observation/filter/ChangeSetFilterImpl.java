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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|disjoint
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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

begin_class
specifier|public
class|class
name|ChangeSetFilterImpl
implements|implements
name|ChangeSetFilter
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ChangeSetFilterImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_EXCLUDED_PATHS
init|=
literal|11
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_EXCLUDE_PATH_CUTOFF_LEVEL
init|=
literal|6
decl_stmt|;
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
name|String
argument_list|>
name|firstLevelIncludeNames
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
name|Pattern
argument_list|>
name|unpreciseExcludePathPatterns
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
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ChangeSetFilterImpl[rootIncludePaths="
operator|+
name|rootIncludePaths
operator|+
literal|", includePathPatterns="
operator|+
name|includePathPatterns
operator|+
literal|", excludePathPatterns="
operator|+
name|excludePathPatterns
operator|+
literal|", parentNodeNames="
operator|+
name|parentNodeNames
operator|+
literal|", parentNodeTypes="
operator|+
name|parentNodeTypes
operator|+
literal|", propertyNames="
operator|+
name|propertyNames
operator|+
literal|"]"
return|;
block|}
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
annotation|@
name|Nullable
name|Set
argument_list|<
name|String
argument_list|>
name|additionalIncludedParentPaths
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
argument_list|(
name|includedParentPaths
argument_list|,
name|isDeep
argument_list|,
name|additionalIncludedParentPaths
argument_list|,
name|excludedParentPaths
argument_list|,
name|parentNodeNames
argument_list|,
name|parentNodeTypes
argument_list|,
name|propertyNames
argument_list|,
name|MAX_EXCLUDED_PATHS
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|Nullable
name|Set
argument_list|<
name|String
argument_list|>
name|additionalIncludedParentPaths
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
parameter_list|,
name|int
name|maxExcludedPaths
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
name|Set
argument_list|<
name|String
argument_list|>
name|firstLevelIncludePaths
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
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
if|if
condition|(
name|firstLevelIncludePaths
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|firstLevelName
init|=
name|firstLevelName
argument_list|(
name|aRawIncludePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstLevelName
operator|!=
literal|null
operator|&&
operator|!
name|firstLevelName
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|firstLevelIncludePaths
operator|.
name|add
argument_list|(
name|firstLevelName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|firstLevelIncludePaths
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|additionalIncludedParentPaths
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|path
range|:
name|additionalIncludedParentPaths
control|)
block|{
name|this
operator|.
name|rootIncludePaths
operator|.
name|add
argument_list|(
name|path
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
name|path
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|firstLevelIncludePaths
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|firstLevelName
init|=
name|firstLevelName
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstLevelName
operator|!=
literal|null
operator|&&
operator|!
name|firstLevelName
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|firstLevelIncludePaths
operator|.
name|add
argument_list|(
name|firstLevelName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|firstLevelIncludePaths
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
name|this
operator|.
name|firstLevelIncludeNames
operator|=
name|firstLevelIncludePaths
expr_stmt|;
comment|// OAK-5169:
comment|// excludedParentPaths could in theory be a large list, in which case
comment|// the excludes() algorithm becomes non-performing. Reason is, that it
comment|// iterates through the changeSet and then through the excludePaths.
comment|// which means it becomes an O(n*m) operation.
comment|// This should be avoided and one way to avoid this is to make an
comment|// unprecise exclude filtering, where a smaller number of parent
comment|// exclude paths is determined - and if the change is within this
comment|// unprecise set, then we have to include it (with the risk of
comment|// false negative) - but if the change is outside of this unprecise
comment|// set, then we are certain that we are not excluding it.
comment|// one way this unprecise filter can be implemented is by
comment|// starting off with eg 6 levels deep paths, and check if that brings
comment|// down the number far enough (to eg 11), if it's still too high,
comment|// cut off exclude paths at level 5 and repeat until the figure ends
comment|// up under 11.
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
name|this
operator|.
name|unpreciseExcludePathPatterns
operator|=
operator|new
name|HashSet
argument_list|<
name|Pattern
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|excludedParentPaths
operator|.
name|size
argument_list|()
operator|<
name|maxExcludedPaths
condition|)
block|{
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
block|}
else|else
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|unprecisePaths
init|=
name|unprecisePaths
argument_list|(
name|excludedParentPaths
argument_list|,
name|maxExcludedPaths
argument_list|,
name|MAX_EXCLUDE_PATH_CUTOFF_LEVEL
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|anUnprecisePath
range|:
name|unprecisePaths
control|)
block|{
name|this
operator|.
name|unpreciseExcludePathPatterns
operator|.
name|add
argument_list|(
name|asPattern
argument_list|(
name|concat
argument_list|(
name|anUnprecisePath
argument_list|,
literal|"**"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|String
name|firstLevelName
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|isEmpty
argument_list|()
operator|||
name|path
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|secondSlash
init|=
name|path
operator|.
name|indexOf
argument_list|(
literal|"/"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|secondSlash
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|secondSlash
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
return|;
block|}
block|}
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|unprecisePaths
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|int
name|maxExcludedPaths
parameter_list|,
name|int
name|maxExcludePathCutOffLevel
parameter_list|)
block|{
name|int
name|level
init|=
name|maxExcludePathCutOffLevel
decl_stmt|;
while|while
condition|(
name|level
operator|>
literal|1
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|unprecise
init|=
name|unprecisePaths
argument_list|(
name|paths
argument_list|,
name|level
argument_list|)
decl_stmt|;
if|if
condition|(
name|unprecise
operator|.
name|size
argument_list|()
operator|<
name|maxExcludedPaths
condition|)
block|{
return|return
name|unprecise
return|;
block|}
name|level
operator|--
expr_stmt|;
block|}
comment|// worst case: we even have too many top-level paths, so
comment|// the only way out here is by returning a set containing only "/"
name|HashSet
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|unprecisePaths
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|String
name|unprecise
init|=
name|path
decl_stmt|;
while|while
condition|(
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|unprecise
argument_list|)
operator|>
name|level
condition|)
block|{
name|unprecise
operator|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|unprecise
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|unprecise
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/** for testing only **/
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getRootIncludePaths
parameter_list|()
block|{
return|return
name|rootIncludePaths
return|;
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
try|try
block|{
return|return
name|doExcludes
argument_list|(
name|changeSet
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"excludes: got an Exception while evaluating excludes: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|", changeSet="
operator|+
name|changeSet
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
comment|// false is the safer option
block|}
block|}
specifier|private
name|boolean
name|doExcludes
parameter_list|(
name|ChangeSet
name|changeSet
parameter_list|)
block|{
if|if
condition|(
name|changeSet
operator|.
name|anyOverflow
argument_list|()
condition|)
block|{
comment|// in case of an overflow we could
comment|// either try to still determine include/exclude based on non-overflown
comment|// sets - or we can do a fail-stop and determine this as too complex
comment|// to try-to-exclude, and just include
comment|//TODO: optimize this later
return|return
literal|false
return|;
block|}
if|if
condition|(
name|changeSet
operator|.
name|doesHitMaxPathDepth
argument_list|()
condition|)
block|{
comment|// then we might or might not include this - but without
comment|// further complicated checks this can't be determined for sure
comment|// so for simplicity reason: return false here
return|return
literal|false
return|;
block|}
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
comment|// first go through the unprecise excludes. if that has any hit,
comment|// we have to let it pass as include
name|boolean
name|unpreciseExclude
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|unpreciseExcludePathPatterns
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
name|unpreciseExcludePathPatterns
argument_list|,
name|aParentPath
argument_list|)
condition|)
block|{
comment|// if there is an unprecise match we keep track of that fact
comment|// for later in this method
name|unpreciseExclude
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
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
name|firstLevelIncludeNames
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|firstLevelName
init|=
name|firstLevelName
argument_list|(
name|aPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstLevelName
operator|!=
literal|null
operator|&&
operator|!
name|firstLevelIncludeNames
operator|.
name|contains
argument_list|(
name|firstLevelName
argument_list|)
condition|)
block|{
comment|// then the 'first level name check' concluded that
comment|// it's not in any include path - hence we can skip
comment|// the (more expensive) pattern check
continue|continue;
block|}
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
elseif|else
if|if
condition|(
name|unpreciseExclude
condition|)
block|{
comment|// then it might have been excluded but we are not sure
comment|// in which case we return false (as that's safe always)
return|return
literal|false
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
if|if
condition|(
name|disjoint
argument_list|(
name|changeSet
operator|.
name|getPropertyNames
argument_list|()
argument_list|,
name|this
operator|.
name|propertyNames
argument_list|)
condition|)
block|{
comment|// if propertyNames are defined then if we can't find any
comment|// at this stage (if !included) then this equals to filtering out
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
if|if
condition|(
name|disjoint
argument_list|(
name|changeSet
operator|.
name|getParentNodeTypes
argument_list|()
argument_list|,
name|this
operator|.
name|parentNodeTypes
argument_list|)
condition|)
block|{
comment|// same story here: if nodeTypes is defined and we can't find any
comment|// match
comment|// then we're done now
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
comment|// and a 3rd time, if we can't find any nodeName match
comment|// here, then we're filtering out
if|if
condition|(
name|disjoint
argument_list|(
name|changeSet
operator|.
name|getParentNodeNames
argument_list|()
argument_list|,
name|this
operator|.
name|parentNodeNames
argument_list|)
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

