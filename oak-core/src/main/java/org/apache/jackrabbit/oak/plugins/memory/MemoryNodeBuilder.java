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
name|memory
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
name|Map
operator|.
name|Entry
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
name|collect
operator|.
name|ImmutableMap
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
name|Maps
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
name|NodeStateDiff
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
name|plugins
operator|.
name|memory
operator|.
name|ModifiedNodeState
operator|.
name|with
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
name|memory
operator|.
name|ModifiedNodeState
operator|.
name|withNodes
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
name|memory
operator|.
name|ModifiedNodeState
operator|.
name|withProperties
import|;
end_import

begin_comment
comment|/**  * In-memory node state builder.  *<p>  * TODO: The following description is somewhat out of date  *<p>  * The following two builder states are used  * to properly track uncommitted chances without relying on weak references  * or requiring hard references on the entire accessed subtree:  *<dl>  *<dt>unmodified</dt>  *<dd>  *     A child builder with no content changes starts in this state.  *     It keeps a reference to the parent builder and knows it's name for  *     use when connecting. Before each access the unconnected builder  *     checks the parent for relevant changes to connect to. As long as  *     there are no such changes, the builder remains unconnected and  *     uses the immutable base state to respond to any content accesses.  *</dd>  *<dt>connected</dt>  *<dd>  *     Once a child node is first modified, it switches it's internal  *     state from the immutable base state to a mutable one and records  *     a hard reference to that state in the mutable parent state. After  *     that the parent reference is cleared and no more state checks are  *     made. Any other builder instances that refer to the same child node  *     will update their internal states to point to that same mutable  *     state instance and thus become connected at next access.  *     A root state builder is always connected.  *</dd>  *</dl>  */
end_comment

begin_class
specifier|public
class|class
name|MemoryNodeBuilder
implements|implements
name|NodeBuilder
block|{
specifier|private
specifier|static
specifier|final
name|NodeState
name|NULL_STATE
init|=
operator|new
name|MemoryNodeState
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|PropertyState
operator|>
name|of
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|NodeState
operator|>
name|of
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * Parent builder, or {@code null} for a root builder.      */
specifier|private
specifier|final
name|MemoryNodeBuilder
name|parent
decl_stmt|;
comment|/**      * Name of this child node within the parent builder,      * or {@code null} for a root builder.      */
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**      * Root builder, or {@code this} for the root itself.      */
specifier|private
specifier|final
name|MemoryNodeBuilder
name|root
decl_stmt|;
comment|/**      * Internal revision counter that is incremented in the root builder      * whenever anything changes in the tree below. Each builder instance      * has its own copy of the revision counter, for quickly checking whether      * any state changes are needed.      */
specifier|private
name|long
name|revision
decl_stmt|;
comment|/**      * The base state of this builder, or {@code null} if this builder      * represents a new node that didn't yet exist in the base content tree.      */
specifier|private
name|NodeState
name|baseState
decl_stmt|;
comment|/**      * The shared mutable state of connected builder instances, or      * {@code null} until this builder has been connected.      */
specifier|private
name|MutableNodeState
name|writeState
decl_stmt|;
comment|/**      * Creates a new in-memory node state builder.      *      * @param parent parent node state builder      * @param name name of this node      */
specifier|protected
name|MemoryNodeBuilder
parameter_list|(
name|MemoryNodeBuilder
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
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
name|root
operator|=
name|parent
operator|.
name|root
expr_stmt|;
name|this
operator|.
name|revision
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|baseState
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|writeState
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Creates a new in-memory node state builder.      *      * @param base base state of the new builder      */
specifier|public
name|MemoryNodeBuilder
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|base
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|name
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|this
expr_stmt|;
name|this
operator|.
name|revision
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|baseState
operator|=
name|checkNotNull
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeState
operator|=
operator|new
name|MutableNodeState
argument_list|(
name|baseState
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeState
name|read
parameter_list|()
block|{
if|if
condition|(
name|revision
operator|!=
name|root
operator|.
name|revision
condition|)
block|{
assert|assert
operator|(
name|parent
operator|!=
literal|null
operator|)
assert|;
comment|// root never gets here
name|parent
operator|.
name|read
argument_list|()
expr_stmt|;
comment|// The builder could have been reset, need to re-get base state
if|if
condition|(
name|parent
operator|.
name|baseState
operator|!=
literal|null
condition|)
block|{
name|baseState
operator|=
name|parent
operator|.
name|baseState
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|baseState
operator|=
literal|null
expr_stmt|;
block|}
comment|// ... same for the write state
if|if
condition|(
name|parent
operator|.
name|writeState
operator|!=
literal|null
condition|)
block|{
name|writeState
operator|=
name|parent
operator|.
name|writeState
operator|.
name|nodes
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|writeState
operator|==
literal|null
operator|&&
name|parent
operator|.
name|writeState
operator|.
name|nodes
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This node has been removed"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|writeState
operator|=
literal|null
expr_stmt|;
block|}
name|revision
operator|=
name|root
operator|.
name|revision
expr_stmt|;
block|}
if|if
condition|(
name|writeState
operator|!=
literal|null
condition|)
block|{
return|return
name|writeState
return|;
block|}
elseif|else
if|if
condition|(
name|baseState
operator|!=
literal|null
condition|)
block|{
return|return
name|baseState
return|;
block|}
else|else
block|{
return|return
name|NULL_STATE
return|;
block|}
block|}
specifier|private
name|MutableNodeState
name|write
parameter_list|()
block|{
return|return
name|write
argument_list|(
name|root
operator|.
name|revision
operator|+
literal|1
argument_list|)
return|;
block|}
specifier|private
name|MutableNodeState
name|write
parameter_list|(
name|long
name|newRevision
parameter_list|)
block|{
if|if
condition|(
name|writeState
operator|==
literal|null
operator|||
name|revision
operator|!=
name|root
operator|.
name|revision
condition|)
block|{
assert|assert
operator|(
name|parent
operator|!=
literal|null
operator|)
assert|;
comment|// root never gets here
name|parent
operator|.
name|write
argument_list|(
name|newRevision
argument_list|)
expr_stmt|;
comment|// The builder could have been reset, need to re-get base state
if|if
condition|(
name|parent
operator|.
name|baseState
operator|!=
literal|null
condition|)
block|{
name|baseState
operator|=
name|parent
operator|.
name|baseState
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|baseState
operator|=
literal|null
expr_stmt|;
block|}
assert|assert
name|parent
operator|.
name|writeState
operator|!=
literal|null
assert|;
comment|// we just called parent.write()
name|writeState
operator|=
name|parent
operator|.
name|writeState
operator|.
name|nodes
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|writeState
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|parent
operator|.
name|writeState
operator|.
name|nodes
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This node has been removed"
argument_list|)
throw|;
block|}
else|else
block|{
comment|// need to make this node writable
name|NodeState
name|base
init|=
name|baseState
decl_stmt|;
if|if
condition|(
name|base
operator|==
literal|null
condition|)
block|{
name|base
operator|=
name|NULL_STATE
expr_stmt|;
block|}
name|writeState
operator|=
operator|new
name|MutableNodeState
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|parent
operator|.
name|writeState
operator|.
name|nodes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|writeState
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
comment|// make sure that all revision numbers up to the root gets updated
name|parent
operator|.
name|write
argument_list|(
name|newRevision
argument_list|)
expr_stmt|;
block|}
name|revision
operator|=
name|newRevision
expr_stmt|;
return|return
name|writeState
return|;
block|}
comment|/**      * Factory method for creating new child state builders. Subclasses may      * override this method to control the behavior of child state builders.      *      * @return new builder      */
specifier|protected
name|MemoryNodeBuilder
name|createChildBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|MemoryNodeBuilder
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
comment|/**      * Called whenever<em>this</em> node is modified, i.e. a property is      * added, changed or removed, or a child node is added or removed. Changes      * inside child nodes or the subtrees below are not reported. The default      * implementation does nothing, but subclasses may override this method      * to better track changes.      */
specifier|protected
name|void
name|updated
parameter_list|()
block|{
comment|// do nothing
block|}
specifier|protected
name|void
name|compareAgainstBaseState
parameter_list|(
name|NodeStateDiff
name|diff
parameter_list|)
block|{
name|NodeState
name|state
init|=
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|writeState
operator|!=
literal|null
condition|)
block|{
name|writeState
operator|.
name|compareAgainstBaseState
argument_list|(
name|state
argument_list|,
name|diff
argument_list|)
expr_stmt|;
block|}
block|}
comment|//--------------------------------------------------------< NodeBuilder>---
annotation|@
name|Override
specifier|public
name|boolean
name|isNew
parameter_list|()
block|{
return|return
name|this
operator|!=
name|root
operator|&&
name|parent
operator|.
name|isNew
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isNew
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|(
name|getBaseState
argument_list|()
operator|==
literal|null
operator|||
operator|!
name|getBaseState
argument_list|()
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
operator|)
operator|&&
name|hasChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isRemoved
parameter_list|()
block|{
return|return
name|this
operator|!=
name|root
operator|&&
operator|(
name|parent
operator|.
name|isRemoved
argument_list|()
operator|||
name|parent
operator|.
name|isRemoved
argument_list|(
name|name
argument_list|)
operator|)
return|;
block|}
specifier|private
name|boolean
name|isRemoved
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getBaseState
argument_list|()
operator|!=
literal|null
operator|&&
name|getBaseState
argument_list|()
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|hasChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isModified
parameter_list|()
block|{
if|if
condition|(
name|writeState
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|NodeState
name|baseState
init|=
name|getBaseState
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|MutableNodeState
argument_list|>
name|n
range|:
name|writeState
operator|.
name|nodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|n
operator|.
name|getValue
argument_list|()
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
name|baseState
operator|!=
literal|null
operator|&&
operator|!
name|baseState
operator|.
name|hasChildNode
argument_list|(
name|n
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|p
range|:
name|writeState
operator|.
name|properties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|PropertyState
name|pState
init|=
name|p
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|pState
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
name|baseState
operator|!=
literal|null
operator|&&
operator|!
name|pState
operator|.
name|equals
argument_list|(
name|baseState
operator|.
name|getProperty
argument_list|(
name|p
operator|.
name|getKey
argument_list|()
argument_list|)
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
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getNodeState
parameter_list|()
block|{
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|writeState
operator|!=
literal|null
condition|)
block|{
return|return
name|writeState
operator|.
name|snapshot
argument_list|()
return|;
block|}
else|else
block|{
comment|// FIXME this assertion might fail when getNodeState() is called on a removed node.
assert|assert
name|baseState
operator|!=
literal|null
assert|;
comment|// guaranteed by read()
return|return
name|baseState
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getBaseState
parameter_list|()
block|{
return|return
name|baseState
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|(
name|NodeState
name|newBase
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|root
condition|)
block|{
name|baseState
operator|=
name|checkNotNull
argument_list|(
name|newBase
argument_list|)
expr_stmt|;
name|writeState
operator|=
operator|new
name|MutableNodeState
argument_list|(
name|baseState
argument_list|)
expr_stmt|;
name|revision
operator|++
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot reset a non-root builder"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildNodeCount
parameter_list|()
block|{
return|return
name|read
argument_list|()
operator|.
name|getChildNodeCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|read
argument_list|()
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getChildNodeNames
parameter_list|()
block|{
return|return
name|read
argument_list|()
operator|.
name|getChildNodeNames
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeBuilder
name|setNode
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|write
argument_list|()
expr_stmt|;
name|MutableNodeState
name|childState
init|=
name|writeState
operator|.
name|nodes
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|childState
operator|==
literal|null
condition|)
block|{
name|writeState
operator|.
name|nodes
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|childState
operator|=
name|createChildBuilder
argument_list|(
name|name
argument_list|)
operator|.
name|write
argument_list|()
expr_stmt|;
block|}
name|childState
operator|.
name|reset
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|updated
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeBuilder
name|removeNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|MutableNodeState
name|mstate
init|=
name|write
argument_list|()
decl_stmt|;
if|if
condition|(
name|mstate
operator|.
name|base
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|mstate
operator|.
name|nodes
operator|.
name|put
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mstate
operator|.
name|nodes
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|updated
argument_list|()
expr_stmt|;
return|return
name|this
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
name|read
argument_list|()
operator|.
name|getPropertyCount
argument_list|()
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
name|read
argument_list|()
operator|.
name|getProperties
argument_list|()
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
name|read
argument_list|()
operator|.
name|getProperty
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
name|NodeBuilder
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|MutableNodeState
name|mstate
init|=
name|write
argument_list|()
decl_stmt|;
if|if
condition|(
name|mstate
operator|.
name|base
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|mstate
operator|.
name|properties
operator|.
name|put
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mstate
operator|.
name|properties
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|updated
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|setProperty
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
name|MutableNodeState
name|mstate
init|=
name|write
argument_list|()
decl_stmt|;
name|mstate
operator|.
name|properties
operator|.
name|put
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|,
name|property
argument_list|)
expr_stmt|;
name|updated
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|NodeBuilder
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|value
parameter_list|)
block|{
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|NodeBuilder
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
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|child
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|read
argument_list|()
expr_stmt|;
comment|// shortcut when dealing with a read-only child node
if|if
condition|(
name|baseState
operator|!=
literal|null
operator|&&
name|baseState
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
operator|&&
operator|(
name|writeState
operator|==
literal|null
operator|||
operator|!
name|writeState
operator|.
name|nodes
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
operator|)
condition|)
block|{
return|return
name|createChildBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|// no read-only child node found, switch to write mode
name|write
argument_list|()
expr_stmt|;
assert|assert
name|writeState
operator|!=
literal|null
assert|;
comment|// guaranteed by write()
name|NodeState
name|childBase
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|baseState
operator|!=
literal|null
condition|)
block|{
name|childBase
operator|=
name|baseState
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|writeState
operator|.
name|nodes
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|writeState
operator|.
name|nodes
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// The child node was removed earlier and we're creating
comment|// a new child with the same name. Use the null state to
comment|// prevent the previous child state from re-surfacing.
name|childBase
operator|=
literal|null
expr_stmt|;
block|}
name|writeState
operator|.
name|nodes
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|MutableNodeState
argument_list|(
name|childBase
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|MemoryNodeBuilder
name|builder
init|=
name|createChildBuilder
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|builder
operator|.
name|write
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
comment|/**      * The<em>mutable</em> state being built. Instances of this class      * are never passed beyond the containing {@code MemoryNodeBuilder},      * so it's not a problem that we intentionally break the immutability      * assumption of the {@link NodeState} interface.      */
specifier|private
class|class
name|MutableNodeState
extends|extends
name|AbstractNodeState
block|{
comment|/**          * The immutable base state.          */
specifier|private
name|NodeState
name|base
decl_stmt|;
comment|/**          * Set of added, modified or removed ({@code null} value)          * property states.          */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|properties
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
comment|/**          * Set of added, modified or removed ({@code null} value)          * child nodes.          */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|MutableNodeState
argument_list|>
name|nodes
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|public
name|MutableNodeState
parameter_list|(
name|NodeState
name|base
parameter_list|)
block|{
if|if
condition|(
name|base
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|base
operator|=
name|MemoryNodeBuilder
operator|.
name|NULL_STATE
expr_stmt|;
block|}
block|}
specifier|public
name|NodeState
name|snapshot
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|nodes
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|MutableNodeState
argument_list|>
name|entry
range|:
name|this
operator|.
name|nodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|MutableNodeState
name|node
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|NodeState
name|before
init|=
name|base
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|before
operator|!=
literal|null
condition|)
block|{
name|nodes
operator|.
name|put
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|NodeState
name|after
init|=
name|node
operator|.
name|snapshot
argument_list|()
decl_stmt|;
if|if
condition|(
name|after
operator|!=
name|before
condition|)
block|{
name|nodes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|with
argument_list|(
name|base
argument_list|,
name|Maps
operator|.
name|newHashMap
argument_list|(
name|this
operator|.
name|properties
argument_list|)
argument_list|,
name|nodes
argument_list|)
return|;
block|}
name|void
name|reset
parameter_list|(
name|NodeState
name|newBase
parameter_list|)
block|{
name|base
operator|=
name|newBase
expr_stmt|;
name|properties
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|MutableNodeState
argument_list|>
argument_list|>
name|iterator
init|=
name|nodes
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|MutableNodeState
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|MutableNodeState
name|cstate
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|NodeState
name|cbase
init|=
name|newBase
operator|.
name|getChildNode
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cbase
operator|==
literal|null
operator|||
name|cstate
operator|==
literal|null
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|cstate
operator|.
name|reset
argument_list|(
name|cbase
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//-----------------------------------------------------< NodeState>--
annotation|@
name|Override
specifier|public
name|long
name|getPropertyCount
parameter_list|()
block|{
return|return
name|withProperties
argument_list|(
name|base
argument_list|,
name|properties
argument_list|)
operator|.
name|getPropertyCount
argument_list|()
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
name|withProperties
argument_list|(
name|base
argument_list|,
name|properties
argument_list|)
operator|.
name|getProperty
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
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|copy
init|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|properties
argument_list|)
decl_stmt|;
return|return
name|withProperties
argument_list|(
name|base
argument_list|,
name|copy
argument_list|)
operator|.
name|getProperties
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildNodeCount
parameter_list|()
block|{
return|return
name|withNodes
argument_list|(
name|base
argument_list|,
name|nodes
argument_list|)
operator|.
name|getChildNodeCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|withNodes
argument_list|(
name|base
argument_list|,
name|nodes
argument_list|)
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|withNodes
argument_list|(
name|base
argument_list|,
name|nodes
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
return|;
comment|// mutable
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getChildNodeNames
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|MutableNodeState
argument_list|>
name|copy
init|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
return|return
name|withNodes
argument_list|(
name|base
argument_list|,
name|copy
argument_list|)
operator|.
name|getChildNodeNames
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|compareAgainstBaseState
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|NodeStateDiff
name|diff
parameter_list|)
block|{
name|with
argument_list|(
name|this
operator|.
name|base
argument_list|,
name|properties
argument_list|,
name|nodes
argument_list|)
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
name|diff
argument_list|)
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

