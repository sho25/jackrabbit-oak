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
name|tree
operator|.
name|impl
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
name|Preconditions
operator|.
name|checkNotNull
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
name|checkState
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
name|Predicates
operator|.
name|notNull
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
name|collect
operator|.
name|Iterables
operator|.
name|filter
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
name|collect
operator|.
name|Iterables
operator|.
name|size
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
name|collect
operator|.
name|Iterables
operator|.
name|transform
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
name|collect
operator|.
name|Lists
operator|.
name|newArrayListWithCapacity
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
name|collect
operator|.
name|Sets
operator|.
name|newLinkedHashSet
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
name|api
operator|.
name|Tree
operator|.
name|Status
operator|.
name|MODIFIED
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
name|api
operator|.
name|Tree
operator|.
name|Status
operator|.
name|NEW
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
name|api
operator|.
name|Tree
operator|.
name|Status
operator|.
name|UNCHANGED
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
name|api
operator|.
name|Type
operator|.
name|NAMES
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
name|spi
operator|.
name|state
operator|.
name|NodeStateUtils
operator|.
name|isHidden
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
name|Set
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
name|Tree
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
name|core
operator|.
name|HiddenTree
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
name|plugins
operator|.
name|index
operator|.
name|reference
operator|.
name|NodeReferenceConstants
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
name|ConflictAnnotatingRebaseDiff
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
comment|/**  * {@code AbstractTree} provides default implementations for most  * read methods of {@code Tree}. Furthermore it handles hides hidden  * items.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractTree
implements|implements
name|Tree
block|{
comment|// TODO: make this configurable
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|INTERNAL_NODE_NAMES
init|=
block|{
name|IndexConstants
operator|.
name|INDEX_CONTENT_NODE_NAME
block|,
name|NodeReferenceConstants
operator|.
name|REF_NAME
block|,
name|NodeReferenceConstants
operator|.
name|WEAK_REF_NAME
block|,
name|ConflictAnnotatingRebaseDiff
operator|.
name|CONFLICT
block|}
decl_stmt|;
comment|/**      * Factory method for creating child trees      * @param name  name of the child tree      * @return child tree of this tree with the given {@code name}      * @throws IllegalArgumentException if the given name string is empty      *                                  or contains the forward slash character      */
annotation|@
name|Nonnull
specifier|protected
specifier|abstract
name|AbstractTree
name|createChild
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
throws|throws
name|IllegalArgumentException
function_decl|;
comment|/**      * @return  the parent of this tree or {@code null} for the root      */
annotation|@
name|CheckForNull
specifier|protected
specifier|abstract
name|AbstractTree
name|getParentOrNull
parameter_list|()
function_decl|;
comment|/**      * @return  The {@code NodeBuilder} for the underlying node state      */
annotation|@
name|Nonnull
specifier|protected
specifier|abstract
name|NodeBuilder
name|getNodeBuilder
parameter_list|()
function_decl|;
comment|/**      * @return  the underlying {@code NodeState} of this tree      */
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getNodeState
parameter_list|()
block|{
return|return
name|getNodeBuilder
argument_list|()
operator|.
name|getNodeState
argument_list|()
return|;
block|}
comment|/**      * @return {@code true} if this tree has orderable children;      *         {@code false} otherwise.      */
specifier|protected
name|boolean
name|hasOrderableChildren
parameter_list|()
block|{
return|return
name|getNodeBuilder
argument_list|()
operator|.
name|hasProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|)
return|;
block|}
comment|/**      * Returns the list of child names considering its ordering      * when the {@link TreeConstants#OAK_CHILD_ORDER} property is set.      *      * @return the list of child names.      */
annotation|@
name|Nonnull
specifier|protected
name|Iterable
argument_list|<
name|String
argument_list|>
name|getChildNames
parameter_list|()
block|{
name|NodeBuilder
name|nodeBuilder
init|=
name|getNodeBuilder
argument_list|()
decl_stmt|;
name|PropertyState
name|order
init|=
name|nodeBuilder
operator|.
name|getProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|)
decl_stmt|;
if|if
condition|(
name|order
operator|!=
literal|null
operator|&&
name|order
operator|.
name|getType
argument_list|()
operator|==
name|NAMES
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|newLinkedHashSet
argument_list|(
name|nodeBuilder
operator|.
name|getChildNodeNames
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|ordered
init|=
name|newArrayListWithCapacity
argument_list|(
name|names
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|order
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
control|)
block|{
comment|// only include names of child nodes that actually exist
if|if
condition|(
name|names
operator|.
name|remove
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|ordered
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|// add names of child nodes that are not explicitly ordered
name|ordered
operator|.
name|addAll
argument_list|(
name|names
argument_list|)
expr_stmt|;
return|return
name|ordered
return|;
block|}
else|else
block|{
return|return
name|nodeBuilder
operator|.
name|getChildNodeNames
argument_list|()
return|;
block|}
block|}
comment|//------------------------------------------------------------< Object>---
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyState
name|p
range|:
name|getProperties
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|p
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|n
range|:
name|this
operator|.
name|getChildNames
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|n
argument_list|)
operator|.
name|append
argument_list|(
literal|" = { ... },"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sb
operator|.
name|charAt
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|','
condition|)
block|{
name|sb
operator|.
name|deleteCharAt
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//---------------------------------------------------------------< Tree>---
annotation|@
name|Override
specifier|public
name|boolean
name|isRoot
parameter_list|()
block|{
return|return
name|getParentOrNull
argument_list|()
operator|==
literal|null
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|getPath
parameter_list|()
block|{
if|if
condition|(
name|isRoot
argument_list|()
condition|)
block|{
return|return
literal|"/"
return|;
block|}
else|else
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buildPath
argument_list|(
name|sb
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|protected
name|void
name|buildPath
parameter_list|(
annotation|@
name|Nonnull
name|StringBuilder
name|sb
parameter_list|)
block|{
name|AbstractTree
name|parent
init|=
name|getParentOrNull
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|buildPath
argument_list|(
name|checkNotNull
argument_list|(
name|sb
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
name|NodeBuilder
name|nodeBuilder
init|=
name|getNodeBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeBuilder
operator|.
name|isNew
argument_list|()
operator|||
name|nodeBuilder
operator|.
name|isReplaced
argument_list|()
condition|)
block|{
return|return
name|NEW
return|;
block|}
elseif|else
if|if
condition|(
name|nodeBuilder
operator|.
name|isModified
argument_list|()
condition|)
block|{
return|return
name|MODIFIED
return|;
block|}
else|else
block|{
return|return
name|UNCHANGED
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
name|getNodeBuilder
argument_list|()
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|isHidden
argument_list|(
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|AbstractTree
name|getParent
parameter_list|()
block|{
name|AbstractTree
name|parent
init|=
name|getParentOrNull
argument_list|()
decl_stmt|;
name|checkState
argument_list|(
name|parent
operator|!=
literal|null
argument_list|,
literal|"root tree does not have a parent"
argument_list|)
expr_stmt|;
return|return
name|parent
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Tree
name|getChild
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
operator|!
name|isHidden
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|createChild
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|HiddenTree
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|PropertyState
name|getProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
operator|!
name|isHidden
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
condition|?
name|getNodeBuilder
argument_list|()
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
operator|(
operator|!
name|isHidden
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
operator|)
operator|&&
name|getNodeBuilder
argument_list|()
operator|.
name|hasProperty
argument_list|(
name|name
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
name|size
argument_list|(
name|getProperties
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|Status
name|getPropertyStatus
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|NodeBuilder
name|nodeBuilder
init|=
name|getNodeBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|hasProperty
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|nodeBuilder
operator|.
name|isNew
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|NEW
return|;
block|}
elseif|else
if|if
condition|(
name|nodeBuilder
operator|.
name|isReplaced
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|MODIFIED
return|;
block|}
else|else
block|{
return|return
name|UNCHANGED
return|;
block|}
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
name|filter
argument_list|(
name|getNodeBuilder
argument_list|()
operator|.
name|getProperties
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|PropertyState
name|propertyState
parameter_list|)
block|{
return|return
operator|!
name|isHidden
argument_list|(
name|propertyState
operator|.
name|getName
argument_list|()
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
name|boolean
name|hasChild
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
name|getNodeBuilder
argument_list|()
operator|.
name|hasChildNode
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
operator|&&
operator|!
name|isHidden
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildrenCount
parameter_list|(
name|long
name|max
parameter_list|)
block|{
if|if
condition|(
name|max
operator|+
name|INTERNAL_NODE_NAMES
operator|.
name|length
operator|<
literal|0
condition|)
block|{
comment|// avoid overflow (if max is near Long.MAX_VALUE)
name|max
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
comment|// fetch a few more
name|max
operator|+=
name|INTERNAL_NODE_NAMES
operator|.
name|length
expr_stmt|;
block|}
name|NodeBuilder
name|nodeBuilder
init|=
name|getNodeBuilder
argument_list|()
decl_stmt|;
name|long
name|count
init|=
name|nodeBuilder
operator|.
name|getChildNodeCount
argument_list|(
name|max
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|name
range|:
name|INTERNAL_NODE_NAMES
control|)
block|{
if|if
condition|(
name|nodeBuilder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|count
operator|--
expr_stmt|;
block|}
block|}
block|}
return|return
name|count
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|Tree
argument_list|>
name|getChildren
parameter_list|()
block|{
name|Iterable
argument_list|<
name|Tree
argument_list|>
name|children
init|=
name|transform
argument_list|(
name|getChildNames
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Tree
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Tree
name|apply
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|AbstractTree
name|child
init|=
name|createChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|child
operator|.
name|exists
argument_list|()
condition|?
name|child
else|:
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|filter
argument_list|(
name|children
argument_list|,
name|notNull
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

