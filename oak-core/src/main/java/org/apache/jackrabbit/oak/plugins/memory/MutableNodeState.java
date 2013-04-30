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
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
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
name|MISSING_NODE
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

begin_comment
comment|/**  * A<em>mutable</em> state being built.  *  * Instances of this class are never passed beyond the containing  * {@link MemoryNodeBuilder}, so it's not a problem that we intentionally  * break the immutability assumption of the  * {@link org.apache.jackrabbit.oak.spi.state.NodeState} interface.  */
end_comment

begin_class
class|class
name|MutableNodeState
extends|extends
name|AbstractNodeState
block|{
comment|/**      * The immutable base state.      */
specifier|private
name|NodeState
name|base
decl_stmt|;
comment|/**      * Set of added, modified or removed ({@code null} value)      * property states.      */
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
name|newHashMap
argument_list|()
decl_stmt|;
comment|/**      * Set of added, modified or removed (non-existent value)      * child nodes.      */
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
name|newHashMap
argument_list|()
decl_stmt|;
name|MutableNodeState
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|base
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|ModifiedNodeState
operator|.
name|unwrap
argument_list|(
name|base
argument_list|,
name|properties
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
block|}
name|NodeState
name|snapshot
parameter_list|()
block|{
assert|assert
name|base
operator|!=
literal|null
assert|;
if|if
condition|(
name|properties
operator|.
name|isEmpty
argument_list|()
operator|&&
name|nodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|base
return|;
block|}
else|else
block|{
return|return
operator|new
name|ModifiedNodeState
argument_list|(
name|base
argument_list|,
name|properties
argument_list|,
name|nodes
argument_list|)
return|;
block|}
block|}
name|void
name|reset
parameter_list|(
name|NodeState
name|newBase
parameter_list|)
block|{
assert|assert
name|base
operator|!=
literal|null
assert|;
name|checkNotNull
argument_list|(
name|newBase
argument_list|)
expr_stmt|;
name|base
operator|=
name|ModifiedNodeState
operator|.
name|unwrap
argument_list|(
name|newBase
argument_list|,
name|properties
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get and optionally connect a potentially non existing child      * node of a given {@code name}. Connected child nodes are kept      * in the list of modified child nodes of this node.      */
name|MutableNodeState
name|getChildNode
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|connect
parameter_list|)
block|{
assert|assert
name|base
operator|!=
literal|null
assert|;
name|MutableNodeState
name|child
init|=
name|nodes
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|==
literal|null
condition|)
block|{
name|child
operator|=
operator|new
name|MutableNodeState
argument_list|(
name|base
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|connect
condition|)
block|{
name|nodes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|child
return|;
block|}
comment|/**      * Equivalent to      *<pre>      *   MutableNodeState child = getChildNode(name, true);      *   child.reset(state);      *   return child;      *</pre>      */
annotation|@
name|Nonnull
name|MutableNodeState
name|setChildNode
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
assert|assert
name|base
operator|!=
literal|null
assert|;
name|MutableNodeState
name|child
init|=
name|nodes
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|==
literal|null
condition|)
block|{
name|child
operator|=
operator|new
name|MutableNodeState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|child
operator|.
name|reset
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
return|return
name|child
return|;
block|}
comment|/**      * Determine whether this node state is modified wrt. the passed      * {@code before} state.      *<p>      * A node state is modified if it either has not the same properties      * or has not the same child nodes as a {@code before} state. A node      * state has the same properties as a {@code before} state iff its      * set of properties is equal to the set of properties of      * {@code before}. A node state has the same child nodes as a      * {@code before} state iff its set of child node names is equal to      * the set of child node names of {@code before}.      */
name|boolean
name|isModified
parameter_list|(
name|NodeState
name|before
parameter_list|)
block|{
if|if
condition|(
operator|!
name|exists
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|nodes
operator|.
name|isEmpty
argument_list|()
operator|&&
name|properties
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// was a child node added or removed?
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
operator|.
name|exists
argument_list|()
operator|!=
name|before
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
comment|// was a property added, removed or modified
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
operator|||
operator|!
name|pState
operator|.
name|equals
argument_list|(
name|before
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
comment|/**      * Remove the child node with the given {@code name}.      * @param name  name of the child node to remove      * @return  {@code true} if a child node {@code name} existed, {@code false} otherwise.      */
name|boolean
name|removeChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
assert|assert
name|base
operator|!=
literal|null
assert|;
name|MutableNodeState
name|child
init|=
name|nodes
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
name|boolean
name|existed
init|=
name|child
operator|.
name|exists
argument_list|()
decl_stmt|;
name|child
operator|.
name|reset
argument_list|(
name|MISSING_NODE
argument_list|)
expr_stmt|;
return|return
name|existed
return|;
block|}
else|else
block|{
name|nodes
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|MutableNodeState
argument_list|(
name|MISSING_NODE
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|base
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
comment|/**      * Remove the property of the given {@code name}.      * @param name  name of the property to remove      * @return  {@code true} if a property {@code name} existed, {@code false} otherwise.      */
name|boolean
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
assert|assert
name|base
operator|!=
literal|null
assert|;
if|if
condition|(
name|base
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
name|properties
operator|.
name|remove
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
comment|/**      * Set the value of a property      */
name|void
name|setProperty
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
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
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
assert|assert
name|base
operator|!=
literal|null
assert|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|base
argument_list|)
operator|.
name|append
argument_list|(
literal|" + {"
argument_list|)
expr_stmt|;
name|String
name|separator
init|=
literal|" "
decl_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|properties
operator|.
name|values
argument_list|()
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|separator
argument_list|)
expr_stmt|;
name|separator
operator|=
literal|", "
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|property
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|MutableNodeState
argument_list|>
name|entry
range|:
name|nodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|separator
argument_list|)
expr_stmt|;
name|separator
operator|=
literal|", "
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" : "
argument_list|)
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|" }"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//-----------------------------------------------------< NodeState>--
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
assert|assert
name|base
operator|!=
literal|null
assert|;
return|return
name|base
operator|.
name|exists
argument_list|()
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
name|ModifiedNodeState
operator|.
name|getPropertyCount
argument_list|(
name|base
argument_list|,
name|properties
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
name|ModifiedNodeState
operator|.
name|hasProperty
argument_list|(
name|base
argument_list|,
name|properties
argument_list|,
name|name
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
name|ModifiedNodeState
operator|.
name|getProperty
argument_list|(
name|base
argument_list|,
name|properties
argument_list|,
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
return|return
name|ModifiedNodeState
operator|.
name|getProperties
argument_list|(
name|base
argument_list|,
name|properties
argument_list|,
literal|true
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
assert|assert
name|base
operator|!=
literal|null
assert|;
return|return
name|ModifiedNodeState
operator|.
name|getChildNodeCount
argument_list|(
name|base
argument_list|,
name|nodes
argument_list|)
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
assert|assert
name|base
operator|!=
literal|null
assert|;
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
comment|// checkArgument(!name.isEmpty()); TODO: should be caught earlier
name|NodeState
name|child
init|=
name|nodes
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
return|return
name|child
operator|.
name|exists
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|base
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|MutableNodeState
name|getChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
assert|assert
name|base
operator|!=
literal|null
assert|;
return|return
name|ModifiedNodeState
operator|.
name|getChildNodeNames
argument_list|(
name|base
argument_list|,
name|nodes
argument_list|,
literal|true
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|compareAgainstBaseState
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|NodeStateDiff
name|diff
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
end_class

end_unit

