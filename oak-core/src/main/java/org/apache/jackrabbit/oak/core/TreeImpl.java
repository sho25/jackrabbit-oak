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
name|core
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
name|checkArgument
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
name|indexOf
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_UUID
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
name|EXISTING
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
name|Type
operator|.
name|STRING
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
name|isAbsolute
import|;
end_import

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
name|Iterables
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
name|Sets
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
name|api
operator|.
name|TreeLocation
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
name|core
operator|.
name|RootImpl
operator|.
name|Move
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
name|MemoryPropertyBuilder
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
name|MultiStringPropertyState
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
name|PropertyBuilder
import|;
end_import

begin_class
specifier|public
class|class
name|TreeImpl
implements|implements
name|Tree
block|{
comment|/**      * Internal and hidden property that contains the child order      */
specifier|public
specifier|static
specifier|final
name|String
name|OAK_CHILD_ORDER
init|=
literal|":childOrder"
decl_stmt|;
comment|/**      * Underlying {@code Root} of this {@code Tree} instance      */
specifier|private
specifier|final
name|RootImpl
name|root
decl_stmt|;
comment|/**      * Parent of this tree. Null for the root.      */
specifier|private
name|TreeImpl
name|parent
decl_stmt|;
comment|/**      * Name of this tree      */
specifier|private
name|String
name|name
decl_stmt|;
comment|/**      * The {@code NodeBuilder} for the underlying node state      */
specifier|private
name|NodeBuilder
name|nodeBuilder
decl_stmt|;
comment|/** Pointer into the list of pending moves */
specifier|private
name|Move
name|pendingMoves
decl_stmt|;
name|TreeImpl
parameter_list|(
name|RootImpl
name|root
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|Move
name|pendingMoves
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|checkNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
literal|""
expr_stmt|;
name|this
operator|.
name|nodeBuilder
operator|=
name|checkNotNull
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|this
operator|.
name|pendingMoves
operator|=
name|checkNotNull
argument_list|(
name|pendingMoves
argument_list|)
expr_stmt|;
block|}
specifier|private
name|TreeImpl
parameter_list|(
name|RootImpl
name|root
parameter_list|,
name|TreeImpl
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|Move
name|pendingMoves
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|checkNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|checkNotNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeBuilder
operator|=
name|parent
operator|.
name|nodeBuilder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|pendingMoves
operator|=
name|checkNotNull
argument_list|(
name|pendingMoves
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
name|enter
argument_list|()
expr_stmt|;
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isRoot
parameter_list|()
block|{
name|enter
argument_list|()
expr_stmt|;
return|return
name|parent
operator|==
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
name|enter
argument_list|()
expr_stmt|;
return|return
name|getPathInternal
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
name|enter
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeBuilder
operator|.
name|isNew
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
name|EXISTING
return|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Deprecated
specifier|public
name|TreeLocation
name|getLocation
parameter_list|()
block|{
name|enter
argument_list|()
expr_stmt|;
return|return
operator|new
name|NodeLocation
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
name|enter
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|getParent
parameter_list|()
block|{
name|checkState
argument_list|(
name|parent
operator|!=
literal|null
argument_list|,
literal|"root tree does not have a parent"
argument_list|)
expr_stmt|;
name|root
operator|.
name|checkLive
argument_list|()
expr_stmt|;
return|return
name|parent
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
name|enter
argument_list|()
expr_stmt|;
return|return
name|getVisibleProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|getPropertyStatus
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// TODO: see OAK-212
name|Status
name|nodeStatus
init|=
name|getStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeStatus
operator|==
name|NEW
condition|)
block|{
return|return
operator|(
name|hasProperty
argument_list|(
name|name
argument_list|)
operator|)
condition|?
name|NEW
else|:
literal|null
return|;
block|}
name|PropertyState
name|head
init|=
name|getVisibleProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|head
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|PropertyState
name|base
init|=
name|getSecureBase
argument_list|()
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|base
operator|==
literal|null
condition|)
block|{
return|return
name|NEW
return|;
block|}
elseif|else
if|if
condition|(
name|head
operator|.
name|equals
argument_list|(
name|base
argument_list|)
condition|)
block|{
return|return
name|EXISTING
return|;
block|}
else|else
block|{
return|return
name|MODIFIED
return|;
block|}
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
name|getProperty
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
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
name|enter
argument_list|()
expr_stmt|;
return|return
name|filter
argument_list|(
name|nodeBuilder
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
name|TreeImpl
name|getChild
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|enter
argument_list|()
expr_stmt|;
return|return
operator|new
name|TreeImpl
argument_list|(
name|root
argument_list|,
name|this
argument_list|,
name|name
argument_list|,
name|pendingMoves
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
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|enter
argument_list|()
expr_stmt|;
name|TreeImpl
name|child
init|=
operator|new
name|TreeImpl
argument_list|(
name|root
argument_list|,
name|this
argument_list|,
name|name
argument_list|,
name|pendingMoves
argument_list|)
decl_stmt|;
return|return
name|child
operator|.
name|nodeBuilder
operator|.
name|exists
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildrenCount
parameter_list|()
block|{
name|enter
argument_list|()
expr_stmt|;
return|return
name|nodeBuilder
operator|.
name|getChildNodeCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|Tree
argument_list|>
name|getChildren
parameter_list|()
block|{
name|enter
argument_list|()
expr_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|childNames
decl_stmt|;
if|if
condition|(
name|hasOrderableChildren
argument_list|()
condition|)
block|{
name|childNames
operator|=
name|getOrderedChildNames
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|childNames
operator|=
name|nodeBuilder
operator|.
name|getChildNodeNames
argument_list|()
expr_stmt|;
block|}
return|return
name|transform
argument_list|(
name|childNames
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
name|input
parameter_list|)
block|{
return|return
operator|new
name|TreeImpl
argument_list|(
name|root
argument_list|,
name|TreeImpl
operator|.
name|this
argument_list|,
name|input
argument_list|,
name|pendingMoves
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
name|remove
parameter_list|()
block|{
name|checkExists
argument_list|()
expr_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
operator|&&
name|parent
operator|.
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|NodeBuilder
name|parentBuilder
init|=
name|parent
operator|.
name|nodeBuilder
decl_stmt|;
name|parentBuilder
operator|.
name|removeChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|parent
operator|.
name|hasOrderableChildren
argument_list|()
condition|)
block|{
name|parentBuilder
operator|.
name|setProperty
argument_list|(
name|MemoryPropertyBuilder
operator|.
name|copy
argument_list|(
name|STRING
argument_list|,
name|parent
operator|.
name|nodeBuilder
operator|.
name|getProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|)
argument_list|)
operator|.
name|removeValue
argument_list|(
name|name
argument_list|)
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|updated
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|addChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkExists
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|nodeBuilder
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasOrderableChildren
argument_list|()
condition|)
block|{
name|nodeBuilder
operator|.
name|setProperty
argument_list|(
name|MemoryPropertyBuilder
operator|.
name|copy
argument_list|(
name|STRING
argument_list|,
name|nodeBuilder
operator|.
name|getProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|)
argument_list|)
operator|.
name|addValue
argument_list|(
name|name
argument_list|)
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|updated
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|TreeImpl
argument_list|(
name|root
argument_list|,
name|this
argument_list|,
name|name
argument_list|,
name|pendingMoves
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOrderableChildren
parameter_list|(
name|boolean
name|enable
parameter_list|)
block|{
name|checkExists
argument_list|()
expr_stmt|;
if|if
condition|(
name|enable
condition|)
block|{
name|ensureChildOrderProperty
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|nodeBuilder
operator|.
name|removeProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|orderBefore
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|checkExists
argument_list|()
expr_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
comment|// root does not have siblings
return|return
literal|false
return|;
block|}
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
operator|!
name|parent
operator|.
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// so such sibling or not accessible
return|return
literal|false
return|;
block|}
comment|// perform the reorder
name|parent
operator|.
name|ensureChildOrderProperty
argument_list|()
expr_stmt|;
comment|// all siblings but not this one
name|Iterable
argument_list|<
name|String
argument_list|>
name|siblings
init|=
name|filter
argument_list|(
name|parent
operator|.
name|getOrderedChildNames
argument_list|()
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
name|String
name|name
parameter_list|)
block|{
return|return
operator|!
name|TreeImpl
operator|.
name|this
operator|.
name|name
operator|.
name|equals
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// create head and tail
name|Iterable
argument_list|<
name|String
argument_list|>
name|head
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|tail
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|head
operator|=
name|siblings
expr_stmt|;
name|tail
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|int
name|idx
init|=
name|indexOf
argument_list|(
name|siblings
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
name|String
name|sibling
parameter_list|)
block|{
return|return
name|name
operator|.
name|equals
argument_list|(
name|sibling
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|head
operator|=
name|Iterables
operator|.
name|limit
argument_list|(
name|siblings
argument_list|,
name|idx
argument_list|)
expr_stmt|;
name|tail
operator|=
name|Iterables
operator|.
name|skip
argument_list|(
name|siblings
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
comment|// concatenate head, this name and tail
name|parent
operator|.
name|nodeBuilder
operator|.
name|setProperty
argument_list|(
name|MultiStringPropertyState
operator|.
name|stringProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|,
name|Iterables
operator|.
name|concat
argument_list|(
name|head
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|getName
argument_list|()
argument_list|)
argument_list|,
name|tail
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|updated
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
name|checkExists
argument_list|()
expr_stmt|;
name|nodeBuilder
operator|.
name|setProperty
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|root
operator|.
name|updated
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|value
parameter_list|)
block|{
name|checkExists
argument_list|()
expr_stmt|;
name|nodeBuilder
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|root
operator|.
name|updated
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|value
parameter_list|,
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
name|checkExists
argument_list|()
expr_stmt|;
name|nodeBuilder
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|root
operator|.
name|updated
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkExists
argument_list|()
expr_stmt|;
name|nodeBuilder
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|root
operator|.
name|updated
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getPathInternal
argument_list|()
operator|+
literal|": "
operator|+
name|getNodeState
argument_list|()
return|;
block|}
comment|//-----------------------------------------------------------< internal>---
annotation|@
name|Nonnull
name|NodeState
name|getNodeState
parameter_list|()
block|{
return|return
name|nodeBuilder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
comment|/**      * Move this tree to the parent at {@code destParent} with the new name      * {@code destName}.      * @param destParent new parent for this tree      * @param destName   new name for this tree      */
name|void
name|moveTo
parameter_list|(
name|TreeImpl
name|destParent
parameter_list|,
name|String
name|destName
parameter_list|)
block|{
name|name
operator|=
name|destName
expr_stmt|;
name|parent
operator|=
name|destParent
expr_stmt|;
block|}
comment|/**      * Reset this (root) tree instance's underlying node state to the passed {@code state}.      * @param state      * @throws IllegalStateException  if {@code isRoot()} is {@code false}.      */
name|void
name|reset
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|checkState
argument_list|(
name|parent
operator|==
literal|null
argument_list|)
expr_stmt|;
name|nodeBuilder
operator|.
name|reset
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get a possibly non existing tree.      * @param path the path to the tree      * @return a {@link Tree} instance for the child at {@code path}.      */
annotation|@
name|CheckForNull
name|TreeImpl
name|getTree
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|isAbsolute
argument_list|(
name|checkNotNull
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|TreeImpl
name|child
init|=
name|this
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|child
operator|=
operator|new
name|TreeImpl
argument_list|(
name|child
operator|.
name|root
argument_list|,
name|child
argument_list|,
name|name
argument_list|,
name|child
operator|.
name|pendingMoves
argument_list|)
expr_stmt|;
block|}
return|return
name|child
return|;
block|}
comment|/**      * Update the child order with children that have been removed or added.      * Added children are appended to the end of the {@link #OAK_CHILD_ORDER}      * property.      */
name|void
name|updateChildOrder
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasOrderableChildren
argument_list|()
condition|)
block|{
return|return;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|Sets
operator|.
name|newLinkedHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|getOrderedChildNames
argument_list|()
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
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|name
range|:
name|nodeBuilder
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|PropertyBuilder
argument_list|<
name|String
argument_list|>
name|builder
init|=
name|MemoryPropertyBuilder
operator|.
name|array
argument_list|(
name|STRING
argument_list|,
name|OAK_CHILD_ORDER
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setValues
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|nodeBuilder
operator|.
name|setProperty
argument_list|(
name|builder
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
name|String
name|getIdentifier
parameter_list|()
block|{
name|PropertyState
name|property
init|=
name|nodeBuilder
operator|.
name|getProperty
argument_list|(
name|JCR_UUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
literal|"/"
return|;
block|}
else|else
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|parent
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
name|String
name|getPathInternal
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
literal|"/"
return|;
block|}
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
comment|//------------------------------------------------------------< private>---
specifier|private
name|boolean
name|reconnect
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
operator|&&
name|parent
operator|.
name|reconnect
argument_list|()
condition|)
block|{
name|nodeBuilder
operator|=
name|parent
operator|.
name|nodeBuilder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeBuilder
operator|.
name|exists
argument_list|()
return|;
block|}
specifier|private
name|void
name|checkExists
parameter_list|()
block|{
name|checkState
argument_list|(
name|enter
argument_list|()
argument_list|,
literal|"This tree does not exist"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|enter
parameter_list|()
block|{
name|root
operator|.
name|checkLive
argument_list|()
expr_stmt|;
name|applyPendingMoves
argument_list|()
expr_stmt|;
return|return
name|reconnect
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isHidden
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// FIXME clarify handling of hidden items (OAK-753).
return|return
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * The (possibly non-existent) node state this tree is based on.      * @return the base node state of this tree      */
annotation|@
name|Nonnull
specifier|private
name|NodeState
name|getSecureBase
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|root
operator|.
name|getSecureBase
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|parent
operator|.
name|getSecureBase
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
specifier|private
name|void
name|applyPendingMoves
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|applyPendingMoves
argument_list|()
expr_stmt|;
block|}
name|pendingMoves
operator|=
name|pendingMoves
operator|.
name|apply
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|private
name|PropertyState
name|getVisibleProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|!
name|isHidden
argument_list|(
name|name
argument_list|)
condition|?
name|nodeBuilder
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
else|:
literal|null
return|;
block|}
specifier|private
name|void
name|buildPath
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
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
name|sb
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
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return {@code true} if this tree has orderable children;      *         {@code false} otherwise.      */
specifier|private
name|boolean
name|hasOrderableChildren
parameter_list|()
block|{
return|return
name|nodeBuilder
operator|.
name|hasProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|)
return|;
block|}
comment|/**      * Returns the ordered child names. This method must only be called when      * this tree {@link #hasOrderableChildren()}.      *      * @return the ordered child names.      */
specifier|private
name|Iterable
argument_list|<
name|String
argument_list|>
name|getOrderedChildNames
parameter_list|()
block|{
assert|assert
name|hasOrderableChildren
argument_list|()
assert|;
return|return
operator|new
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|final
name|PropertyState
name|childOrder
init|=
name|nodeBuilder
operator|.
name|getProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|)
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|index
operator|<
name|childOrder
operator|.
name|count
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
return|return
name|childOrder
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|,
name|index
operator|++
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|/**      * Ensures that the {@link #OAK_CHILD_ORDER} exists. This method will create      * the property if it doesn't exist and initialize the value with the names      * of the children as returned by {@link NodeBuilder#getChildNodeNames()}.      */
specifier|private
name|void
name|ensureChildOrderProperty
parameter_list|()
block|{
if|if
condition|(
operator|!
name|nodeBuilder
operator|.
name|hasProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|)
condition|)
block|{
name|nodeBuilder
operator|.
name|setProperty
argument_list|(
name|MultiStringPropertyState
operator|.
name|stringProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|,
name|nodeBuilder
operator|.
name|getChildNodeNames
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|//-------------------------------------------------------< TreeLocation>---
annotation|@
name|Deprecated
specifier|private
specifier|final
class|class
name|NodeLocation
extends|extends
name|AbstractNodeLocation
argument_list|<
name|TreeImpl
argument_list|>
block|{
specifier|private
name|NodeLocation
parameter_list|(
name|TreeImpl
name|tree
parameter_list|)
block|{
name|super
argument_list|(
name|tree
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|NodeLocation
name|createNodeLocation
parameter_list|(
name|TreeImpl
name|tree
parameter_list|)
block|{
return|return
operator|new
name|NodeLocation
argument_list|(
name|tree
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|TreeLocation
name|createPropertyLocation
parameter_list|(
name|AbstractNodeLocation
argument_list|<
name|TreeImpl
argument_list|>
name|parentLocation
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|PropertyLocation
argument_list|(
name|parentLocation
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|TreeImpl
name|getParentTree
parameter_list|()
block|{
return|return
name|tree
operator|.
name|parent
return|;
block|}
annotation|@
name|Override
specifier|protected
name|TreeImpl
name|getChildTree
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|TreeImpl
argument_list|(
name|tree
operator|.
name|root
argument_list|,
name|tree
argument_list|,
name|name
argument_list|,
name|tree
operator|.
name|pendingMoves
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|PropertyState
name|getPropertyState
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|tree
operator|.
name|getVisibleProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|canRead
parameter_list|(
name|TreeImpl
name|tree
parameter_list|)
block|{
return|return
name|tree
operator|.
name|nodeBuilder
operator|.
name|exists
argument_list|()
return|;
block|}
block|}
annotation|@
name|Deprecated
specifier|private
specifier|static
specifier|final
class|class
name|PropertyLocation
extends|extends
name|AbstractPropertyLocation
argument_list|<
name|TreeImpl
argument_list|>
block|{
specifier|private
name|PropertyLocation
parameter_list|(
name|AbstractNodeLocation
argument_list|<
name|TreeImpl
argument_list|>
name|parentLocation
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|parentLocation
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|canRead
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
return|return
operator|!
name|isHidden
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

