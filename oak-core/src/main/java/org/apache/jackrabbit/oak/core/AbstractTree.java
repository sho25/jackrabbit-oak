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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|spi
operator|.
name|state
operator|.
name|NodeStateUtils
operator|.
name|isHidden
import|;
end_import

begin_comment
comment|/**  * {@code AbstractTree} provides default implementations for most  * read methods of {@code Tree}. Furthermore it handles the  * {@link #setOrderableChildren(boolean) ordering} of child nodes  * and hides internal items.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractTree
implements|implements
name|Tree
block|{
comment|/**      * Name of the internal property that contains the child order      */
specifier|public
specifier|static
specifier|final
name|String
name|OAK_CHILD_ORDER
init|=
literal|":childOrder"
decl_stmt|;
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
name|MicroKernel
operator|.
name|CONFLICT_NAME
block|}
decl_stmt|;
comment|/**      * Name of this tree      */
specifier|protected
name|String
name|name
decl_stmt|;
comment|/**      * The {@code NodeBuilder} for the underlying node state      */
specifier|protected
name|NodeBuilder
name|nodeBuilder
decl_stmt|;
comment|/**      * Create a new {@code AbstractTree} instance      * @param name  name of the tree      * @param nodeBuilder  {@code NodeBuilder} for the underlying node state      */
specifier|protected
name|AbstractTree
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|NodeBuilder
name|nodeBuilder
parameter_list|)
block|{
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
name|checkNotNull
argument_list|(
name|nodeBuilder
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return  the underlying {@code NodeState} of this tree      */
annotation|@
name|Nonnull
specifier|public
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
comment|/**      * Factory method for creating child trees      * @param name  name of the child tree      * @return  child tree of this tree with the given {@code name}      */
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
function_decl|;
comment|/**      * @return  {@code true} iff {@code getStatus() == Status.NEW}      */
specifier|protected
specifier|abstract
name|boolean
name|isNew
parameter_list|()
function_decl|;
comment|/**      * @return  {@code true} iff {@code getStatus() == Status.MODIFIED}      */
specifier|protected
specifier|abstract
name|boolean
name|isModified
parameter_list|()
function_decl|;
comment|/**      * @return {@code true} if this tree has orderable children;      *         {@code false} otherwise.      */
specifier|protected
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
comment|/**      * Returns the list of child names considering its ordering      * when the {@link #OAK_CHILD_ORDER} property is set.      *      * @return the list of child names.      */
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
if|if
condition|(
name|hasOrderableChildren
argument_list|()
condition|)
block|{
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
comment|//------------------------------------------------------------< Tree>---
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
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
return|return
name|name
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
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
operator|!
name|isRoot
argument_list|()
condition|)
block|{
name|getParent
argument_list|()
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
annotation|@
name|Override
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
if|if
condition|(
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
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
operator|!
name|isHidden
argument_list|(
name|name
argument_list|)
operator|&&
name|nodeBuilder
operator|.
name|exists
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|abstract
name|AbstractTree
name|getParent
parameter_list|()
function_decl|;
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
operator|!
name|isHidden
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
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
name|nodeBuilder
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
name|boolean
name|hasChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|createChild
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
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
specifier|public
name|Iterable
argument_list|<
name|Tree
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|transform
argument_list|(
name|filter
argument_list|(
name|getChildNames
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
return|;
block|}
block|}
argument_list|)
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
return|return
name|createChild
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

