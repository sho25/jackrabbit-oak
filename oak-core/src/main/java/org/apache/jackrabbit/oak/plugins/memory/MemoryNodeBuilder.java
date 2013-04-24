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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
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
name|BOOLEAN
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
name|NAME
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
comment|/**  * In-memory node state builder.  *<p>  * A {@code MemoryNodeBuilder} instance tracks uncommitted changes without  * relying on weak references or requiring hard references on the entire  * accessed subtree. It does this by relying on {@code MutableNodeState}  * instances for tracking<em>uncommitted changes</em>. A child builders  * keeps a reference to its parent builder and knows it's name. Before  * each access the builder checks the mutable state of its parent for  * relevant changes and updates its own mutable state.  *<p>  * A {@code MutableNodeState} instance does not keep a reference to its  * parent state. It only keeps references to children that have been  * modified. Instances representing an unmodified child are created on  * the fly without keeping a reference. This effectively ensures that  * such an instance can be GC'ed once no node builder references it  * anymore.  */
end_comment

begin_class
specifier|public
class|class
name|MemoryNodeBuilder
implements|implements
name|NodeBuilder
block|{
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
comment|/**      * Internal revision counter for the base state of this builder. The counter      * is incremented in the root builder whenever its base state is reset.      * Each builder instance has its own copy of this revision counter for      * quickly checking whether its base state needs updating.      * @see #reset(org.apache.jackrabbit.oak.spi.state.NodeState)      */
specifier|private
name|long
name|baseRevision
decl_stmt|;
comment|/**      * The base state of this builder, possibly non-existent if this builder      * represents a new node that didn't yet exist in the base content tree.      * @see #base()      */
specifier|private
name|NodeState
name|base
decl_stmt|;
comment|/**      * Internal revision counter for the head state of this builder. The counter      * is incremented in the root builder whenever anything changes in the tree      * below. Each builder instance has its own copy of this revision counter for      * quickly checking whether its head state needs updating.      */
specifier|private
name|long
name|headRevision
decl_stmt|;
comment|/**      * The shared mutable state this builder.      * @see #write()      * @see #read()      */
specifier|private
name|MutableNodeState
name|head
decl_stmt|;
comment|/**      * Creates a new in-memory child builder.      * @param parent parent builder      * @param name name of this node      */
specifier|private
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
name|parent
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|parent
operator|.
name|root
expr_stmt|;
block|}
comment|/**      * Creates a new in-memory node state builder rooted at      * and based on the passed {@code base} state.      * @param base base state of the new builder      */
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
comment|// ensure base is updated on next call to base()
name|this
operator|.
name|baseRevision
operator|=
literal|1
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|checkNotNull
argument_list|(
name|base
argument_list|)
expr_stmt|;
comment|// ensure head is updated on next call to read() or write()
name|this
operator|.
name|headRevision
operator|=
literal|1
expr_stmt|;
name|this
operator|.
name|head
operator|=
operator|new
name|MutableNodeState
argument_list|(
name|this
operator|.
name|base
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|isRoot
parameter_list|()
block|{
return|return
name|this
operator|==
name|root
return|;
block|}
specifier|private
name|void
name|checkConnected
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isConnected
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This builder is not connected"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Update the base state of this builder by recursively retrieving it      * from the parent builder.      * @return  base state of this builder      */
annotation|@
name|Nonnull
specifier|private
name|NodeState
name|base
parameter_list|()
block|{
if|if
condition|(
name|root
operator|.
name|baseRevision
operator|!=
name|baseRevision
condition|)
block|{
name|base
operator|=
name|parent
operator|.
name|base
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|baseRevision
operator|=
name|root
operator|.
name|baseRevision
expr_stmt|;
block|}
return|return
name|base
return|;
block|}
comment|/**      * Update the head state of this builder by recursively retrieving it      * from the parent builder.      * @return  head state of this builder      */
annotation|@
name|Nonnull
specifier|private
name|MutableNodeState
name|read
parameter_list|()
block|{
if|if
condition|(
name|headRevision
operator|!=
name|root
operator|.
name|headRevision
condition|)
block|{
assert|assert
operator|!
name|isRoot
argument_list|()
operator|:
literal|"root should have headRevision == root.headRevision"
assert|;
name|head
operator|=
name|parent
operator|.
name|read
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|headRevision
operator|=
name|root
operator|.
name|headRevision
expr_stmt|;
block|}
return|return
name|head
return|;
block|}
comment|/**      * Update the head state of this builder by recursively retrieving it      * from the parent builder and increment the head revision of the root      * builder ensuring subsequent calls to {@link #read()} result in updating      * of the respective head states.      * @return  head state of this builder      */
annotation|@
name|Nonnull
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
name|headRevision
operator|+
literal|1
argument_list|)
return|;
block|}
comment|/**      * Recursive helper method to {@link #write()}. Don't call directly.      */
annotation|@
name|Nonnull
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
name|headRevision
operator|!=
name|newRevision
operator|&&
operator|!
name|isRoot
argument_list|()
condition|)
block|{
name|head
operator|=
name|parent
operator|.
name|write
argument_list|(
name|newRevision
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|headRevision
operator|=
name|newRevision
expr_stmt|;
block|}
name|root
operator|.
name|headRevision
operator|=
name|newRevision
expr_stmt|;
return|return
name|head
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
comment|//--------------------------------------------------------< NodeBuilder>---
annotation|@
name|Override
specifier|public
name|NodeState
name|getNodeState
parameter_list|()
block|{
name|checkConnected
argument_list|()
expr_stmt|;
return|return
name|read
argument_list|()
operator|.
name|snapshot
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getBaseState
parameter_list|()
block|{
return|return
name|base
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
name|checkConnected
argument_list|()
expr_stmt|;
return|return
name|read
argument_list|()
operator|.
name|exists
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isNew
parameter_list|()
block|{
name|checkConnected
argument_list|()
expr_stmt|;
return|return
operator|!
name|isRoot
argument_list|()
operator|&&
operator|!
name|parent
operator|.
name|base
argument_list|()
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
operator|&&
name|parent
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
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
name|isRoot
argument_list|()
operator|||
name|parent
operator|.
name|read
argument_list|()
operator|.
name|isConnected
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
name|checkConnected
argument_list|()
expr_stmt|;
return|return
name|read
argument_list|()
operator|.
name|isModified
argument_list|(
name|base
argument_list|()
argument_list|)
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
name|checkState
argument_list|(
name|isRoot
argument_list|()
argument_list|,
literal|"Cannot reset a non-root builder"
argument_list|)
expr_stmt|;
name|base
operator|=
name|checkNotNull
argument_list|(
name|newBase
argument_list|)
expr_stmt|;
name|root
operator|.
name|baseRevision
operator|++
expr_stmt|;
name|root
operator|.
name|headRevision
operator|++
expr_stmt|;
name|head
operator|=
operator|new
name|MutableNodeState
argument_list|(
name|base
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildNodeCount
parameter_list|()
block|{
name|checkConnected
argument_list|()
expr_stmt|;
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
name|Iterable
argument_list|<
name|String
argument_list|>
name|getChildNodeNames
parameter_list|()
block|{
name|checkConnected
argument_list|()
expr_stmt|;
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
specifier|public
name|boolean
name|hasChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkConnected
argument_list|()
expr_stmt|;
return|return
name|read
argument_list|()
operator|.
name|hasChildNode
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
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
if|if
condition|(
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|getChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|setChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|getChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkConnected
argument_list|()
expr_stmt|;
return|return
name|createChildBuilder
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|setChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|setChildNode
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|,
name|EMPTY_NODE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|setChildNode
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|checkConnected
argument_list|()
expr_stmt|;
name|write
argument_list|()
operator|.
name|setChildNode
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
name|MemoryNodeBuilder
name|builder
init|=
name|createChildBuilder
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|updated
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|removeChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkConnected
argument_list|()
expr_stmt|;
if|if
condition|(
name|write
argument_list|()
operator|.
name|removeChildNode
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
condition|)
block|{
name|updated
argument_list|()
expr_stmt|;
block|}
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
name|checkConnected
argument_list|()
expr_stmt|;
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
name|checkConnected
argument_list|()
expr_stmt|;
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
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkConnected
argument_list|()
expr_stmt|;
return|return
name|read
argument_list|()
operator|.
name|hasProperty
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
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
name|checkConnected
argument_list|()
expr_stmt|;
return|return
name|read
argument_list|()
operator|.
name|getProperty
argument_list|(
name|checkNotNull
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
name|getBoolean
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|property
operator|!=
literal|null
operator|&&
name|property
operator|.
name|getType
argument_list|()
operator|==
name|BOOLEAN
operator|&&
name|property
operator|.
name|getValue
argument_list|(
name|BOOLEAN
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
operator|&&
name|property
operator|.
name|getType
argument_list|()
operator|==
name|NAME
condition|)
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
name|NAME
argument_list|)
return|;
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
name|Iterable
argument_list|<
name|String
argument_list|>
name|getNames
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
operator|&&
name|property
operator|.
name|getType
argument_list|()
operator|==
name|NAMES
condition|)
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|emptyList
argument_list|()
return|;
block|}
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
name|checkConnected
argument_list|()
expr_stmt|;
name|write
argument_list|()
operator|.
name|setProperty
argument_list|(
name|checkNotNull
argument_list|(
name|property
argument_list|)
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
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkConnected
argument_list|()
expr_stmt|;
if|if
condition|(
name|write
argument_list|()
operator|.
name|removeProperty
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
condition|)
block|{
name|updated
argument_list|()
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

