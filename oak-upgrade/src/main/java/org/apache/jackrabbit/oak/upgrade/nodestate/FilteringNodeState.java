begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|upgrade
operator|.
name|nodestate
package|;
end_package

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
name|ImmutableSet
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
name|spi
operator|.
name|state
operator|.
name|NodeState
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * NodeState implementation that decorates another node-state instance  * in order to hide subtrees or partial subtrees from the consumer of  * the API.  *<br>  * The set of visible subtrees is defined by two parameters: include paths  * and exclude paths, both of which are sets of absolute paths.  *<br>  * Any paths that are equal or are descendants of one of the  *<b>excluded paths</b> are hidden by this implementation.  *<br>  * For all<b>included paths</b>, the direct ancestors, the node-state at  * the path itself and all descendants are visible. Any siblings of the  * defined path or its ancestors are implicitly hidden (unless made visible  * by another include path).  *<br>  * The implementation delegates to the decorated node-state instance and  * filters out hidden node-states in the following methods:  *<ul>  *<li>{@link #exists()}</li>  *<li>{@link #hasChildNode(String)}</li>  *<li>{@link #getChildNodeEntries()}</li>  *</ul>  * Additionally, hidden node-state names are removed from the property  * {@code :childOrder} in the following two methods:  *<ul>  *<li>{@link #getProperties()}</li>  *<li>{@link #getProperty(String)}</li>  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|FilteringNodeState
extends|extends
name|AbstractDecoratedNodeState
block|{
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|ALL
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|NONE
init|=
name|ImmutableSet
operator|.
name|of
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|includedPaths
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludedPaths
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fragmentPaths
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludedFragments
decl_stmt|;
comment|/**      * Factory method that conditionally decorates the given node-state      * iff the node-state is (a) hidden itself or (b) has hidden descendants.      *      * @param path The path where the node-state should be assumed to be located.      * @param delegate The node-state to decorate.      * @param includePaths A Set of paths that should be visible. Defaults to ["/"] if {@code null}.      * @param excludePaths A Set of paths that should be hidden. Empty if {@code null}.      * @param fragmentPaths A Set of paths that should support the fragments (see below). Empty if {@code null}.      * @param excludedFragments A Set of name fragments that should be hidden. Empty if {@code null}.      * @return The decorated node-state if required, the original node-state if decoration is unnecessary.      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|NodeState
name|wrap
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|NodeState
name|delegate
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|includePaths
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludePaths
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fragmentPaths
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludedFragments
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|includes
init|=
name|defaultIfEmpty
argument_list|(
name|includePaths
argument_list|,
name|ALL
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludes
init|=
name|defaultIfEmpty
argument_list|(
name|excludePaths
argument_list|,
name|NONE
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|safeFragmentPaths
init|=
name|defaultIfEmpty
argument_list|(
name|fragmentPaths
argument_list|,
name|NONE
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|safeExcludedFragments
init|=
name|defaultIfEmpty
argument_list|(
name|excludedFragments
argument_list|,
name|NONE
argument_list|)
decl_stmt|;
if|if
condition|(
name|hasHiddenDescendants
argument_list|(
name|path
argument_list|,
name|includes
argument_list|,
name|excludes
argument_list|,
name|safeFragmentPaths
argument_list|,
name|safeExcludedFragments
argument_list|)
condition|)
block|{
return|return
operator|new
name|FilteringNodeState
argument_list|(
name|path
argument_list|,
name|delegate
argument_list|,
name|includes
argument_list|,
name|excludes
argument_list|,
name|fragmentPaths
argument_list|,
name|safeExcludedFragments
argument_list|)
return|;
block|}
return|return
name|delegate
return|;
block|}
specifier|private
name|FilteringNodeState
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|NodeState
name|delegate
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|includedPaths
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludedPaths
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fragmentPaths
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludedFragments
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|includedPaths
operator|=
name|includedPaths
expr_stmt|;
name|this
operator|.
name|excludedPaths
operator|=
name|excludedPaths
expr_stmt|;
name|this
operator|.
name|fragmentPaths
operator|=
name|fragmentPaths
expr_stmt|;
name|this
operator|.
name|excludedFragments
operator|=
name|excludedFragments
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|protected
name|NodeState
name|decorateChild
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|NodeState
name|child
parameter_list|)
block|{
specifier|final
name|String
name|childPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|wrap
argument_list|(
name|childPath
argument_list|,
name|child
argument_list|,
name|includedPaths
argument_list|,
name|excludedPaths
argument_list|,
name|fragmentPaths
argument_list|,
name|excludedFragments
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|hideChild
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|NodeState
name|delegateChild
parameter_list|)
block|{
return|return
name|isHidden
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
argument_list|,
name|includedPaths
argument_list|,
name|excludedPaths
argument_list|,
name|excludedFragments
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|PropertyState
name|decorateProperty
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|PropertyState
name|propertyState
parameter_list|)
block|{
return|return
name|fixChildOrderPropertyState
argument_list|(
name|this
argument_list|,
name|propertyState
argument_list|)
return|;
block|}
comment|/**      * Utility method to determine whether a given path should is hidden given the      * include paths and exclude paths.      *      * @param path Path to be checked      * @param includes Include paths      * @param excludes Exclude paths      * @param excludedFragments Exclude fragments      * @return Whether the {@code path} is hidden or not.      */
specifier|private
specifier|static
name|boolean
name|isHidden
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|includes
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludes
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludedFragments
parameter_list|)
block|{
return|return
name|isExcluded
argument_list|(
name|path
argument_list|,
name|excludes
argument_list|,
name|excludedFragments
argument_list|)
operator|||
operator|!
name|isIncluded
argument_list|(
name|path
argument_list|,
name|includes
argument_list|)
return|;
block|}
comment|/**      * Utility method to determine whether the path itself or any of its descendants should      * be hidden given the include paths and exclude paths.      *      * @param path Path to be checked      * @param includePaths Include paths      * @param excludePaths Exclude paths      * @param excludedFragments Exclude fragments      * @return Whether the {@code path} or any of its descendants are hidden or not.      */
specifier|private
specifier|static
name|boolean
name|hasHiddenDescendants
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|includePaths
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludePaths
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fragmentPaths
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludedFragments
parameter_list|)
block|{
return|return
name|isHidden
argument_list|(
name|path
argument_list|,
name|includePaths
argument_list|,
name|excludePaths
argument_list|,
name|excludedFragments
argument_list|)
operator|||
name|isAncestorOfAnyPath
argument_list|(
name|path
argument_list|,
name|fragmentPaths
argument_list|)
operator|||
name|isDescendantOfAnyPath
argument_list|(
name|path
argument_list|,
name|fragmentPaths
argument_list|)
operator|||
name|fragmentPaths
operator|.
name|contains
argument_list|(
name|path
argument_list|)
operator|||
name|isAncestorOfAnyPath
argument_list|(
name|path
argument_list|,
name|excludePaths
argument_list|)
operator|||
name|isAncestorOfAnyPath
argument_list|(
name|path
argument_list|,
name|includePaths
argument_list|)
return|;
block|}
comment|/**      * Utility method to check whether a given set of include paths cover the given      * {@code path}. I.e. whether the path is visible or implicitly hidden due to the      * lack of a matching include path.      *<br>      * Note: the ancestors of every include path are considered visible.      *      * @param path Path to be checked      * @param includePaths Include paths      * @return Whether the path is covered by the include paths or not.      */
specifier|private
specifier|static
name|boolean
name|isIncluded
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|includePaths
parameter_list|)
block|{
return|return
name|isAncestorOfAnyPath
argument_list|(
name|path
argument_list|,
name|includePaths
argument_list|)
operator|||
name|includePaths
operator|.
name|contains
argument_list|(
name|path
argument_list|)
operator|||
name|isDescendantOfAnyPath
argument_list|(
name|path
argument_list|,
name|includePaths
argument_list|)
return|;
block|}
comment|/**      * Utility method to check whether a given set of exclude paths cover the given      * {@code path}. I.e. whether the path is hidden due to the presence of a      * matching exclude path.      *      * @param path Path to be checked      * @param excludePaths Exclude paths      * @param excludedFragments Exclude fragments      * @return Whether the path is covered by the excldue paths or not.      */
specifier|private
specifier|static
name|boolean
name|isExcluded
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludePaths
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludedFragments
parameter_list|)
block|{
return|return
name|excludePaths
operator|.
name|contains
argument_list|(
name|path
argument_list|)
operator|||
name|isDescendantOfAnyPath
argument_list|(
name|path
argument_list|,
name|excludePaths
argument_list|)
operator|||
name|containsAnyFragment
argument_list|(
name|path
argument_list|,
name|excludedFragments
argument_list|)
return|;
block|}
comment|/**      * Utility method to check whether any of the provided {@code paths} is a descendant      * of the given ancestor path.      *      * @param ancestor Ancestor path      * @param paths Paths that may be descendants of {@code ancestor}.      * @return true if {@code paths} contains a descendant of {@code ancestor}, false otherwise.      */
specifier|private
specifier|static
name|boolean
name|isAncestorOfAnyPath
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|ancestor
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
for|for
control|(
specifier|final
name|String
name|p
range|:
name|paths
control|)
block|{
if|if
condition|(
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|ancestor
argument_list|,
name|p
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
comment|/**      * Utility method to check whether any of the provided {@code paths} is an ancestor      * of the given descendant path.      *      * @param descendant Descendant path      * @param paths Paths that may be ancestors of {@code descendant}.      * @return true if {@code paths} contains an ancestor of {@code descendant}, false otherwise.      */
specifier|private
specifier|static
name|boolean
name|isDescendantOfAnyPath
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|descendant
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
for|for
control|(
specifier|final
name|String
name|p
range|:
name|paths
control|)
block|{
if|if
condition|(
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|p
argument_list|,
name|descendant
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
comment|/**      * Utility method to check whether the passed path contains any of the provided {@code fragments}.      *      * @param path Path      * @param fragments Fragments, which the path may contain      * @return true if {@code path} contains any of the {@code fragments}, false otherwise.      */
specifier|private
specifier|static
name|boolean
name|containsAnyFragment
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fragments
parameter_list|)
block|{
for|for
control|(
specifier|final
name|String
name|f
range|:
name|fragments
control|)
block|{
if|if
condition|(
name|path
operator|.
name|contains
argument_list|(
name|f
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
comment|/**      * Utility method to return the given {@code Set} if it is not empty and a default Set otherwise.      *      * @param value Value to check for emptiness      * @param defaultValue Default value      * @return return the given {@code Set} if it is not empty and a default Set otherwise      */
annotation|@
name|Nonnull
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Set
argument_list|<
name|T
argument_list|>
name|defaultIfEmpty
parameter_list|(
annotation|@
name|Nullable
name|Set
argument_list|<
name|T
argument_list|>
name|value
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|T
argument_list|>
name|defaultValue
parameter_list|)
block|{
return|return
operator|!
name|isEmpty
argument_list|(
name|value
argument_list|)
condition|?
name|value
else|:
name|defaultValue
return|;
block|}
comment|/**      * Utility method to check whether a Set is empty, i.e. null or of size 0.      *      * @param set The Set to check.      * @return true if empty, false otherwise      */
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|boolean
name|isEmpty
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Set
argument_list|<
name|T
argument_list|>
name|set
parameter_list|)
block|{
return|return
name|set
operator|==
literal|null
operator|||
name|set
operator|.
name|isEmpty
argument_list|()
return|;
block|}
block|}
end_class

end_unit

