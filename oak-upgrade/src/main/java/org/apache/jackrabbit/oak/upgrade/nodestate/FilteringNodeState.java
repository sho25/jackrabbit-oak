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
name|base
operator|.
name|Function
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
name|Predicate
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
name|ImmutableSet
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
name|Iterables
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
name|memory
operator|.
name|MemoryChildNodeEntry
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
name|MemoryNodeBuilder
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
name|PropertyStates
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
name|AbstractNodeState
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
name|ChildNodeEntry
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|plugins
operator|.
name|tree
operator|.
name|impl
operator|.
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
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
name|AbstractNodeState
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
name|NodeState
name|delegate
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
comment|/**      * Factory method that conditionally decorates the given node-state      * iff the node-state is (a) hidden itself or (b) has hidden descendants.      *      * @param path The path where the node-state should be assumed to be located.      * @param delegate The node-state to decorate.      * @param includePaths A Set of paths that should be visible. Defaults to ["/"] if {@code null).      * @param excludePaths A Set of paths that should be hidden. Empty if {@code null).      * @return The decorated node-state if required, the original node-state if decoration is unnecessary.      * @param excludePaths      */
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
if|if
condition|(
name|hasHiddenDescendants
argument_list|(
name|path
argument_list|,
name|includes
argument_list|,
name|excludes
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
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
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
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeBuilder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|MemoryNodeBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
operator|!
name|isHidden
argument_list|(
name|path
argument_list|,
name|includedPaths
argument_list|,
name|excludedPaths
argument_list|)
operator|&&
name|delegate
operator|.
name|exists
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getChildNode
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|IllegalArgumentException
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
name|delegate
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|,
name|includedPaths
argument_list|,
name|excludedPaths
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChildNode
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|name
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
operator|!
name|isHidden
argument_list|(
name|childPath
argument_list|,
name|includedPaths
argument_list|,
name|excludedPaths
argument_list|)
operator|&&
name|delegate
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|getChildNodeEntries
parameter_list|()
block|{
specifier|final
name|Iterable
argument_list|<
name|ChildNodeEntry
argument_list|>
name|transformed
init|=
name|Iterables
operator|.
name|transform
argument_list|(
name|delegate
operator|.
name|getChildNodeEntries
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|ChildNodeEntry
argument_list|,
name|ChildNodeEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|ChildNodeEntry
name|apply
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|ChildNodeEntry
name|childNodeEntry
parameter_list|)
block|{
if|if
condition|(
name|childNodeEntry
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|name
init|=
name|childNodeEntry
operator|.
name|getName
argument_list|()
decl_stmt|;
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
if|if
condition|(
operator|!
name|isHidden
argument_list|(
name|childPath
argument_list|,
name|includedPaths
argument_list|,
name|excludedPaths
argument_list|)
condition|)
block|{
specifier|final
name|NodeState
name|nodeState
init|=
name|childNodeEntry
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
specifier|final
name|NodeState
name|state
init|=
name|wrap
argument_list|(
name|childPath
argument_list|,
name|nodeState
argument_list|,
name|includedPaths
argument_list|,
name|excludedPaths
argument_list|)
decl_stmt|;
return|return
operator|new
name|MemoryChildNodeEntry
argument_list|(
name|name
argument_list|,
name|state
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|transformed
argument_list|,
operator|new
name|Predicate
argument_list|<
name|ChildNodeEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|ChildNodeEntry
name|childNodeEntry
parameter_list|)
block|{
return|return
name|childNodeEntry
operator|!=
literal|null
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPropertyCount
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getPropertyCount
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|delegate
operator|.
name|getProperties
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|PropertyState
argument_list|,
name|PropertyState
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|PropertyState
name|apply
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|PropertyState
name|propertyState
parameter_list|)
block|{
return|return
name|fixChildOrderPropertyState
argument_list|(
name|propertyState
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|fixChildOrderPropertyState
argument_list|(
name|delegate
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**      * Utility method to fix the PropertyState of properties called {@code :childOrder}.      *      * @param propertyState A property-state.      * @return The original property-state or if the property name is {@code :childOrder}, a      *         property-state with hidden child names removed from the value.      */
annotation|@
name|CheckForNull
specifier|private
name|PropertyState
name|fixChildOrderPropertyState
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|PropertyState
name|propertyState
parameter_list|)
block|{
if|if
condition|(
name|propertyState
operator|!=
literal|null
operator|&&
name|OAK_CHILD_ORDER
operator|.
name|equals
argument_list|(
name|propertyState
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
init|=
name|Iterables
operator|.
name|filter
argument_list|(
name|propertyState
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|,
operator|new
name|Predicate
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
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
operator|!
name|isHidden
argument_list|(
name|childPath
argument_list|,
name|includedPaths
argument_list|,
name|excludedPaths
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|,
name|values
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
return|;
block|}
return|return
name|propertyState
return|;
block|}
comment|/**      * Utility method to determine whether a given path should is hidden given the      * include paths and exclude paths.      *      * @param path Path to be checked      * @param includes Include paths      * @param excludes Exclude paths      * @return Whether the {@code path} is hidden or not.      */
specifier|public
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
parameter_list|)
block|{
return|return
name|isExcluded
argument_list|(
name|path
argument_list|,
name|excludes
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
comment|/**      * Utility method to determine whether the path itself or any of its descendants should      * be hidden given the include paths and exclude paths.      *      * @param path Path to be checked      * @param includePaths Include paths      * @param excludePaths Exclude paths      * @return Whether the {@code path} or any of its descendants are hidden or not.      */
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
comment|/**      * Utility method to check whether a given set of exclude paths cover the given      * {@code path}. I.e. whether the path is hidden due to the presence of a      * matching exclude path.      *      * @param path Path to be checked      * @param excludePaths Exclude paths      * @return Whether the path is covered by the excldue paths or not.      */
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

