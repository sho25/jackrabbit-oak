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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|Objects
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
name|io
operator|.
name|ByteStreams
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
name|Blob
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
name|MoveDetector
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
comment|/**  * In-memory node state builder.  *<p>  * A {@code MemoryNodeBuilder} instance tracks uncommitted changes without  * relying on weak references or requiring hard references on the entire  * accessed subtree. It does this by relying on {@code MutableNodeState}  * instances for tracking<em>uncommitted changes</em> and on {@code Head}  * instances for tracking the connectedness of the builder. A builder keeps  * a reference to the parent builder and knows its own name, which is used  * to check for relevant changes in its parent builder and update its state  * accordingly.  *<p>  * A builder is in one of three possible states, which is tracked within  * its {@code Head} instance:  *<dl>  *<dt><em>unconnected</em></dt>  *<dd>  *     A child builder with no content changes starts in this state.  *     Before each access the unconnected builder checks its parent for  *     relevant changes.  *</dd>  *<dt><em>connected</em></dt>  *<dd>  *     Once a builder is first modified, it switches to the connected state  *     and records all modification in a shared {@code MutableNodeState}  *     instance. Before each access the connected builder checks whether its  *     parents base state has been reset and if so, resets its own base state  *     accordingly.  *</dd>  *<dt><em>root</em></dt>  *<dd>  *     Same as the connected state but only the root of the builder hierarchy  *     can have this state.  *</dd>  *</dl>  */
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
comment|/**      * Root builder, or {@code this} for the root builder itself.      */
specifier|private
specifier|final
name|MemoryNodeBuilder
name|rootBuilder
decl_stmt|;
comment|/**      * Internal revision counter for the base state of this builder. The counter      * is incremented in the root builder whenever its base state is reset.      * Each builder instance has its own copy of this revision counter for      * quickly checking whether its base state needs updating.      * @see #reset(org.apache.jackrabbit.oak.spi.state.NodeState)      * @see #base()      */
specifier|private
name|long
name|baseRevision
decl_stmt|;
comment|/**      * The base state of this builder, possibly non-existent if this builder      * represents a new node that didn't yet exist in the base content tree.      */
annotation|@
name|Nonnull
specifier|private
name|NodeState
name|base
decl_stmt|;
comment|/**      * Head of this builder. Always use {@link #head()} for accessing to      * ensure the connected state is correctly updated.      */
specifier|private
name|Head
name|head
decl_stmt|;
comment|/**      * Creates a new in-memory child builder.      * @param parent parent builder      * @param name name of this node      */
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
name|rootBuilder
operator|=
name|parent
operator|.
name|rootBuilder
expr_stmt|;
name|this
operator|.
name|baseRevision
operator|=
name|parent
operator|.
name|baseRevision
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|parent
operator|.
name|base
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|head
operator|=
operator|new
name|UnconnectedHead
argument_list|()
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
name|rootBuilder
operator|=
name|this
expr_stmt|;
name|this
operator|.
name|baseRevision
operator|=
literal|0
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
name|this
operator|.
name|head
operator|=
operator|new
name|RootHead
argument_list|()
expr_stmt|;
block|}
comment|/**      * Update the head of this builder to reflect the actual connected state.      * @return  head of this builder      */
specifier|private
name|Head
name|head
parameter_list|()
block|{
name|Head
name|newHead
init|=
name|head
operator|.
name|update
argument_list|()
decl_stmt|;
if|if
condition|(
name|newHead
operator|!=
name|head
condition|)
block|{
name|head
operator|=
name|newHead
expr_stmt|;
block|}
return|return
name|newHead
return|;
block|}
comment|/**      * @return  {@code true} iff this is the root builder      */
specifier|protected
specifier|final
name|boolean
name|isRoot
parameter_list|()
block|{
return|return
name|this
operator|==
name|rootBuilder
return|;
block|}
comment|/**      * Update the base state of this builder by recursively retrieving it      * from its parent builder.      * @return  base state of this builder      */
annotation|@
name|Nonnull
specifier|private
name|NodeState
name|base
parameter_list|()
block|{
if|if
condition|(
name|rootBuilder
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
name|rootBuilder
operator|.
name|baseRevision
expr_stmt|;
block|}
return|return
name|base
return|;
block|}
comment|/**      * Factory method for creating new child state builders. Subclasses may      * override this method to control the behavior of child state builders.      * @return new builder      */
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
comment|/**      * Called whenever<em>this</em> node is modified, i.e. a property is      * added, changed or removed, or a child node is added or removed. Changes      * inside child nodes or the subtrees below are not reported. The default      * implementation triggers an {@link #updated()} call on the root builder      * (unless this is already the root builder), which subclasses can use      * to capture aggregate update information across the whole tree.      */
specifier|protected
name|void
name|updated
parameter_list|()
block|{
if|if
condition|(
name|this
operator|!=
name|rootBuilder
condition|)
block|{
name|rootBuilder
operator|.
name|updated
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Accessor for parent builder      */
specifier|protected
specifier|final
name|MemoryNodeBuilder
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
comment|/**      * Accessor for name      */
specifier|protected
specifier|final
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * Throws away all changes in this builder and resets the base to the      * given node state.      *      * @param newBase new base state      */
specifier|public
name|void
name|reset
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|newBase
parameter_list|)
block|{
name|checkState
argument_list|(
name|parent
operator|==
literal|null
argument_list|)
expr_stmt|;
name|base
operator|=
name|checkNotNull
argument_list|(
name|newBase
argument_list|)
expr_stmt|;
name|baseRevision
operator|=
name|rootHead
argument_list|()
operator|.
name|setState
argument_list|(
name|newBase
argument_list|)
operator|+
literal|1
expr_stmt|;
block|}
comment|/**      * Replaces the current state of this builder with the given node state.      * The base state remains unchanged.      *      * @param newHead new head state      */
specifier|protected
name|void
name|set
parameter_list|(
name|NodeState
name|newHead
parameter_list|)
block|{
name|checkState
argument_list|(
name|parent
operator|==
literal|null
argument_list|)
expr_stmt|;
comment|// updating the base revision forces all sub-builders to refresh
name|baseRevision
operator|=
name|rootHead
argument_list|()
operator|.
name|setState
argument_list|(
name|newHead
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------------< NodeBuilder>---
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getNodeState
parameter_list|()
block|{
return|return
name|head
argument_list|()
operator|.
name|getImmutableNodeState
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
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
return|return
name|head
argument_list|()
operator|.
name|getCurrentNodeState
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
return|return
name|exists
argument_list|()
operator|&&
operator|!
name|getBaseState
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
name|isModified
parameter_list|()
block|{
return|return
name|head
argument_list|()
operator|.
name|isModified
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildNodeCount
parameter_list|(
name|long
name|max
parameter_list|)
block|{
return|return
name|head
argument_list|()
operator|.
name|getCurrentNodeState
argument_list|()
operator|.
name|getChildNodeCount
argument_list|(
name|max
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
name|head
argument_list|()
operator|.
name|getCurrentNodeState
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
return|return
name|head
argument_list|()
operator|.
name|getCurrentNodeState
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
name|checkState
argument_list|(
name|exists
argument_list|()
argument_list|,
literal|"This builder does not exist: "
operator|+
name|this
operator|.
name|name
argument_list|)
expr_stmt|;
name|head
argument_list|()
operator|.
name|getMutableNodeState
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
name|boolean
name|remove
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isRoot
argument_list|()
operator|&&
name|exists
argument_list|()
condition|)
block|{
name|head
argument_list|()
operator|.
name|getMutableNodeState
argument_list|()
expr_stmt|;
comment|// Make sure the removed node is connected
name|parent
operator|.
name|head
argument_list|()
operator|.
name|getMutableNodeState
argument_list|()
operator|.
name|removeChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
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
name|boolean
name|moveTo
parameter_list|(
name|NodeBuilder
name|newParent
parameter_list|,
name|String
name|newName
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|newParent
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|newName
argument_list|)
expr_stmt|;
if|if
condition|(
name|isRoot
argument_list|()
operator|||
operator|!
name|exists
argument_list|()
operator|||
name|newParent
operator|.
name|hasChildNode
argument_list|(
name|newName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
if|if
condition|(
name|newParent
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|isNew
argument_list|()
condition|)
block|{
name|annotateSourcePath
argument_list|(
name|this
argument_list|,
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|NodeState
name|nodeState
init|=
name|getNodeState
argument_list|()
decl_stmt|;
name|newParent
operator|.
name|setChildNode
argument_list|(
name|newName
argument_list|,
name|nodeState
argument_list|)
expr_stmt|;
name|remove
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// Move to descendant
return|return
literal|false
return|;
block|}
block|}
block|}
specifier|protected
specifier|static
name|void
name|annotateSourcePath
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|PropertyState
name|base
init|=
name|builder
operator|.
name|getBaseState
argument_list|()
operator|.
name|getProperty
argument_list|(
name|MoveDetector
operator|.
name|SOURCE_PATH
argument_list|)
decl_stmt|;
name|PropertyState
name|head
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
operator|.
name|getProperty
argument_list|(
name|MoveDetector
operator|.
name|SOURCE_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|Objects
operator|.
name|equal
argument_list|(
name|base
argument_list|,
name|head
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|builder
operator|.
name|hasProperty
argument_list|(
name|MoveDetector
operator|.
name|SOURCE_PATH
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|MoveDetector
operator|.
name|SOURCE_PATH
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|copyTo
parameter_list|(
name|NodeBuilder
name|newParent
parameter_list|,
name|String
name|newName
parameter_list|)
block|{
if|if
condition|(
name|isRoot
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|checkNotNull
argument_list|(
name|newParent
argument_list|)
operator|.
name|setChildNode
argument_list|(
name|checkNotNull
argument_list|(
name|newName
argument_list|)
argument_list|,
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPropertyCount
parameter_list|()
block|{
return|return
name|head
argument_list|()
operator|.
name|getCurrentNodeState
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
name|head
argument_list|()
operator|.
name|getCurrentNodeState
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
return|return
name|head
argument_list|()
operator|.
name|getCurrentNodeState
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
return|return
name|head
argument_list|()
operator|.
name|getCurrentNodeState
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
name|checkState
argument_list|(
name|exists
argument_list|()
argument_list|,
literal|"This builder does not exist: "
operator|+
name|name
argument_list|)
expr_stmt|;
name|head
argument_list|()
operator|.
name|getMutableNodeState
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
name|checkState
argument_list|(
name|exists
argument_list|()
argument_list|,
literal|"This builder does not exist: "
operator|+
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|head
argument_list|()
operator|.
name|getMutableNodeState
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
annotation|@
name|Override
specifier|public
name|Blob
name|createBlob
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
operator|new
name|ArrayBasedBlob
argument_list|(
name|ByteStreams
operator|.
name|toByteArray
argument_list|(
name|stream
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return path of this builder.      */
specifier|public
specifier|final
name|String
name|getPath
parameter_list|()
block|{
return|return
name|parent
operator|==
literal|null
condition|?
literal|"/"
else|:
name|getPath
argument_list|(
operator|new
name|StringBuilder
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|RootHead
name|rootHead
parameter_list|()
block|{
return|return
operator|(
name|RootHead
operator|)
name|rootBuilder
operator|.
name|head
return|;
block|}
specifier|private
name|StringBuilder
name|getPath
parameter_list|(
name|StringBuilder
name|parentPath
parameter_list|)
block|{
return|return
name|parent
operator|==
literal|null
condition|?
name|parentPath
else|:
name|parent
operator|.
name|getPath
argument_list|(
name|parentPath
argument_list|)
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
return|;
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
name|getPath
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//------------------------------------------------------------< Head>---
comment|/**      * Subclasses of this base class represent the different states associated      * builders can have:<em>unconnected</em>,<em>connected</em>, and<em>root</em>.      * Its methods provide access to the node state being built by this builder.      */
specifier|private
specifier|abstract
specifier|static
class|class
name|Head
block|{
comment|/**          * Returns the up-to-date head of the associated builder. In most          * cases the returned value will be the current head instance, but          * a different head can be returned if a state transition is needed.          * The returned value is then used as the new current head of the          * builder.          *          * @return up-to-date head of the associated builder          */
specifier|public
specifier|abstract
name|Head
name|update
parameter_list|()
function_decl|;
comment|/**          * Returns the current node state associated with this head. This state          * is only stable across one method call and must not be passed outside          * the {@code NodeBuilder} API boundary.          * @return  current head state.          */
specifier|public
specifier|abstract
name|NodeState
name|getCurrentNodeState
parameter_list|()
function_decl|;
comment|/**          * Connects the builder to which this head belongs and all its parents          * and return the mutable node state associated with this head. This state          * is only stable across one method call and must not be passed outside          * the {@code NodeBuilder} API boundary.          * @return  current head state.          */
specifier|public
specifier|abstract
name|MutableNodeState
name|getMutableNodeState
parameter_list|()
function_decl|;
comment|/**          * Returns the current nodes state associated with this head.          * @return  current head state.          */
specifier|public
specifier|abstract
name|NodeState
name|getImmutableNodeState
parameter_list|()
function_decl|;
comment|/**          * Check whether the associated builder represents a modified node, which has          * either modified properties or removed or added child nodes.          * @return  {@code true} for a modified node          */
specifier|public
specifier|abstract
name|boolean
name|isModified
parameter_list|()
function_decl|;
block|}
specifier|private
class|class
name|UnconnectedHead
extends|extends
name|Head
block|{
specifier|private
name|long
name|revision
init|=
name|baseRevision
decl_stmt|;
specifier|private
name|NodeState
name|state
init|=
name|base
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Head
name|update
parameter_list|()
block|{
name|long
name|rootRevision
init|=
name|rootHead
argument_list|()
operator|.
name|revision
decl_stmt|;
if|if
condition|(
name|revision
operator|!=
name|rootRevision
condition|)
block|{
comment|// root revision changed: recursively re-get state from parent
name|NodeState
name|parentState
init|=
name|parent
operator|.
name|head
argument_list|()
operator|.
name|getCurrentNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|newState
init|=
name|parentState
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|newState
operator|instanceof
name|MutableNodeState
condition|)
block|{
comment|// transition state to ConnectedHead
return|return
operator|new
name|ConnectedHead
argument_list|(
operator|(
name|MutableNodeState
operator|)
name|newState
argument_list|)
return|;
block|}
else|else
block|{
comment|// update to match the latest revision
name|state
operator|=
name|newState
expr_stmt|;
name|revision
operator|=
name|rootRevision
expr_stmt|;
block|}
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getCurrentNodeState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
annotation|@
name|Override
specifier|public
name|MutableNodeState
name|getMutableNodeState
parameter_list|()
block|{
comment|// switch to connected state recursively up to the parent
name|MutableNodeState
name|parentState
init|=
name|parent
operator|.
name|head
argument_list|()
operator|.
name|getMutableNodeState
argument_list|()
decl_stmt|;
name|MutableNodeState
name|state
init|=
name|parentState
operator|.
name|getMutableChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// triggers a head state transition at next access
return|return
operator|new
name|ConnectedHead
argument_list|(
name|state
argument_list|)
operator|.
name|getMutableNodeState
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getImmutableNodeState
parameter_list|()
block|{
assert|assert
operator|!
operator|(
name|state
operator|instanceof
name|MutableNodeState
operator|)
assert|;
return|return
name|state
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isModified
parameter_list|()
block|{
return|return
literal|false
return|;
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
name|getPath
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|private
class|class
name|ConnectedHead
extends|extends
name|Head
block|{
specifier|protected
name|long
name|revision
init|=
name|rootBuilder
operator|.
name|baseRevision
decl_stmt|;
specifier|protected
name|MutableNodeState
name|state
decl_stmt|;
specifier|public
name|ConnectedHead
parameter_list|(
name|MutableNodeState
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Head
name|update
parameter_list|()
block|{
if|if
condition|(
name|revision
operator|!=
name|rootBuilder
operator|.
name|baseRevision
condition|)
block|{
comment|// the root builder's base state has been reset: transition back
comment|// to unconnected and connect again if necessary.
return|return
operator|new
name|UnconnectedHead
argument_list|()
operator|.
name|update
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getCurrentNodeState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
annotation|@
name|Override
specifier|public
name|MutableNodeState
name|getMutableNodeState
parameter_list|()
block|{
comment|// incrementing the root revision triggers unconnected
comment|// child state to re-get their state on next access
name|rootHead
argument_list|()
operator|.
name|revision
operator|++
expr_stmt|;
return|return
name|state
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getImmutableNodeState
parameter_list|()
block|{
return|return
name|state
operator|.
name|snapshot
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isModified
parameter_list|()
block|{
return|return
name|state
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
name|getPath
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|private
class|class
name|RootHead
extends|extends
name|ConnectedHead
block|{
specifier|public
name|RootHead
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|MutableNodeState
argument_list|(
name|base
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Head
name|update
parameter_list|()
block|{
return|return
name|this
return|;
block|}
specifier|public
specifier|final
name|long
name|setState
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
operator|new
name|MutableNodeState
argument_list|(
name|state
argument_list|)
expr_stmt|;
comment|// To be able to make a distinction between set() and reset(), we
name|revision
operator|++
expr_stmt|;
comment|// increment the revision twice and
return|return
name|revision
operator|++
return|;
comment|// return the intermediate value
block|}
block|}
block|}
end_class

end_unit

