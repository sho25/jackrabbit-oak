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
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|List
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
name|Set
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
name|CoreValue
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
name|NodeStateBuilder
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
name|base
operator|.
name|Predicates
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
name|Collections2
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
name|ImmutableList
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

begin_comment
comment|/**  * In-memory node state builder.   */
end_comment

begin_class
specifier|public
class|class
name|MemoryNodeStateBuilder
implements|implements
name|NodeStateBuilder
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
comment|/**      * Parent state builder reference, or {@code null} for a connected      * builder.      */
specifier|private
name|MemoryNodeStateBuilder
name|parent
decl_stmt|;
comment|/**      * Name of this child node within the parent builder, or {@code null}      * for a connected builder.      */
specifier|private
name|String
name|name
decl_stmt|;
comment|/**      * The current state of this builder. Initially set to the immutable      * base state until this builder gets<em>connected</em>, after which      * this reference will point to the {@link MutableNodeState} instance      * that records all the changes to this node.      */
specifier|private
name|NodeState
name|state
decl_stmt|;
comment|/**      * Creates a new in-memory node state builder.      *      * @param parent parent node state builder      * @param name name of this node      * @param base base state of this node      */
specifier|protected
name|MemoryNodeStateBuilder
parameter_list|(
name|MemoryNodeStateBuilder
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|base
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
name|state
operator|=
name|checkNotNull
argument_list|(
name|base
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new in-memory node state builder.      *      * @param base base state of the new builder      */
specifier|public
name|MemoryNodeStateBuilder
parameter_list|(
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
name|state
operator|=
operator|new
name|MutableNodeState
argument_list|(
name|checkNotNull
argument_list|(
name|base
argument_list|)
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
name|parent
operator|!=
literal|null
condition|)
block|{
name|NodeState
name|pstate
init|=
name|parent
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|pstate
operator|instanceof
name|MutableNodeState
condition|)
block|{
name|MutableNodeState
name|mstate
init|=
operator|(
name|MutableNodeState
operator|)
name|pstate
decl_stmt|;
name|MemoryNodeStateBuilder
name|existing
init|=
name|mstate
operator|.
name|builders
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|!=
literal|null
condition|)
block|{
name|state
operator|=
name|existing
operator|.
name|state
expr_stmt|;
name|parent
operator|=
literal|null
expr_stmt|;
name|name
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
return|return
name|state
return|;
block|}
specifier|private
name|MutableNodeState
name|write
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|MutableNodeState
name|mstate
init|=
name|parent
operator|.
name|write
argument_list|()
decl_stmt|;
name|MemoryNodeStateBuilder
name|existing
init|=
name|mstate
operator|.
name|builders
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|!=
literal|null
condition|)
block|{
name|state
operator|=
name|existing
operator|.
name|state
expr_stmt|;
block|}
else|else
block|{
name|state
operator|=
operator|new
name|MutableNodeState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|mstate
operator|.
name|builders
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
name|parent
operator|=
literal|null
expr_stmt|;
name|name
operator|=
literal|null
expr_stmt|;
block|}
return|return
operator|(
name|MutableNodeState
operator|)
name|state
return|;
block|}
specifier|private
name|void
name|reset
parameter_list|(
name|NodeState
name|newBase
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
name|base
operator|=
name|newBase
expr_stmt|;
name|mstate
operator|.
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
name|MemoryNodeStateBuilder
argument_list|>
argument_list|>
name|iterator
init|=
name|mstate
operator|.
name|builders
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
name|MemoryNodeStateBuilder
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|MemoryNodeStateBuilder
name|childBuilder
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|NodeState
name|childBase
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
name|childBase
operator|==
literal|null
operator|||
name|childBuilder
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
name|childBuilder
operator|.
name|reset
argument_list|(
name|childBase
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Factory method for creating new child state builders. Subclasses may      * override this method to control the behavior of child state builders.      *      * @param child base state of the new builder, or {@code null}      * @return new builder      */
specifier|protected
name|MemoryNodeStateBuilder
name|createChildBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|child
parameter_list|)
block|{
return|return
operator|new
name|MemoryNodeStateBuilder
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|child
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
name|NodeState
name|getBaseState
parameter_list|()
block|{
name|NodeState
name|state
init|=
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|instanceof
name|MutableNodeState
condition|)
block|{
return|return
operator|(
operator|(
name|MutableNodeState
operator|)
name|state
operator|)
operator|.
name|base
return|;
block|}
else|else
block|{
return|return
name|state
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
name|NodeState
name|state
init|=
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|instanceof
name|MutableNodeState
condition|)
block|{
return|return
operator|(
operator|(
name|MutableNodeState
operator|)
name|state
operator|)
operator|.
name|snapshot
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|state
return|;
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
specifier|public
name|void
name|setNode
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|MutableNodeState
name|mstate
init|=
name|write
argument_list|()
decl_stmt|;
name|MemoryNodeStateBuilder
name|builder
init|=
name|mstate
operator|.
name|builders
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|builder
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|reset
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|createChildBuilder
argument_list|(
name|name
argument_list|,
name|state
argument_list|)
operator|.
name|write
argument_list|()
expr_stmt|;
block|}
name|updated
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
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
name|builders
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
name|builders
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
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|CoreValue
name|value
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
name|name
argument_list|,
operator|new
name|SinglePropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|updated
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
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
name|values
operator|.
name|isEmpty
argument_list|()
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
operator|new
name|EmptyPropertyState
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mstate
operator|.
name|properties
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|MultiPropertyState
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
block|}
annotation|@
name|Override
specifier|public
name|NodeStateBuilder
name|getChildBuilder
parameter_list|(
name|String
name|name
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
operator|!
operator|(
name|state
operator|instanceof
name|MutableNodeState
operator|)
condition|)
block|{
name|NodeState
name|base
init|=
name|state
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|base
operator|!=
literal|null
condition|)
block|{
return|return
name|createChildBuilder
argument_list|(
name|name
argument_list|,
name|base
argument_list|)
return|;
comment|// shortcut
block|}
block|}
name|MutableNodeState
name|mstate
init|=
name|write
argument_list|()
decl_stmt|;
name|MemoryNodeStateBuilder
name|builder
init|=
name|mstate
operator|.
name|builders
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|mstate
operator|.
name|builders
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|builder
operator|=
name|createChildBuilder
argument_list|(
name|name
argument_list|,
name|NULL_STATE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|write
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|NodeState
name|base
init|=
name|mstate
operator|.
name|base
operator|.
name|getChildNode
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
name|base
operator|=
name|NULL_STATE
expr_stmt|;
block|}
name|builder
operator|=
name|createChildBuilder
argument_list|(
name|name
argument_list|,
name|base
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
return|;
block|}
comment|/**      * The<em>mutable</em> state being built. Instances of this class      * are never passed beyond the containing MemoryNodeStateBuilder,      * so it's not a problem that we intentionally break the immutability      * assumption of the {@link NodeState} interface.      */
specifier|private
specifier|static
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
comment|/**          * Set of builders for added, modified or removed          * ({@code null} value) child nodes.          */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|MemoryNodeStateBuilder
argument_list|>
name|builders
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
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
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
name|PropertyState
argument_list|>
name|props
init|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|properties
argument_list|)
decl_stmt|;
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
name|MemoryNodeStateBuilder
argument_list|>
name|entry
range|:
name|builders
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|NodeStateBuilder
name|builder
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|builder
operator|!=
literal|null
condition|)
block|{
name|nodes
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nodes
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ModifiedNodeState
argument_list|(
name|base
argument_list|,
name|props
argument_list|,
name|nodes
argument_list|)
return|;
block|}
comment|//-----------------------------------------------------< NodeState>--
annotation|@
name|Override
specifier|public
name|long
name|getPropertyCount
parameter_list|()
block|{
name|long
name|count
init|=
name|base
operator|.
name|getPropertyCount
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
name|PropertyState
argument_list|>
name|entry
range|:
name|properties
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|base
operator|.
name|getProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|count
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
return|return
name|count
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
name|PropertyState
name|property
init|=
name|properties
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
operator|||
name|properties
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|property
return|;
block|}
return|return
name|base
operator|.
name|getProperty
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
name|?
extends|extends
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|properties
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
name|filter
init|=
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
name|input
parameter_list|)
block|{
return|return
operator|!
name|names
operator|.
name|contains
argument_list|(
name|input
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|Collection
argument_list|<
name|PropertyState
argument_list|>
name|modified
init|=
name|Collections2
operator|.
name|filter
argument_list|(
name|properties
operator|.
name|values
argument_list|()
argument_list|,
name|Predicates
operator|.
name|notNull
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Iterables
operator|.
name|concat
argument_list|(
name|Iterables
operator|.
name|filter
argument_list|(
name|base
operator|.
name|getProperties
argument_list|()
argument_list|,
name|filter
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|modified
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildNodeCount
parameter_list|()
block|{
name|long
name|count
init|=
name|base
operator|.
name|getChildNodeCount
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
name|MemoryNodeStateBuilder
argument_list|>
name|entry
range|:
name|builders
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|base
operator|.
name|getChildNode
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|count
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
return|return
name|count
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
name|NodeStateBuilder
name|builder
init|=
name|builders
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|builder
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|builders
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|base
operator|.
name|getChildNode
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
name|Iterable
argument_list|<
name|String
argument_list|>
name|getChildNodeNames
parameter_list|()
block|{
name|Iterable
argument_list|<
name|String
argument_list|>
name|unmodified
init|=
name|base
operator|.
name|getChildNodeNames
argument_list|()
decl_stmt|;
name|Predicate
argument_list|<
name|String
argument_list|>
name|unmodifiedFilter
init|=
name|Predicates
operator|.
name|not
argument_list|(
name|Predicates
operator|.
name|in
argument_list|(
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|builders
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|modified
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|Maps
operator|.
name|filterValues
argument_list|(
name|builders
argument_list|,
name|Predicates
operator|.
name|notNull
argument_list|()
argument_list|)
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Iterables
operator|.
name|concat
argument_list|(
name|Iterables
operator|.
name|filter
argument_list|(
name|unmodified
argument_list|,
name|unmodifiedFilter
argument_list|)
argument_list|,
name|modified
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
name|ChildNodeEntry
argument_list|>
name|getChildNodeEntries
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

