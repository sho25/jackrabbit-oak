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
name|Predicates
operator|.
name|in
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
name|not
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
name|Collections2
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
name|concat
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
name|Maps
operator|.
name|filterValues
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
name|MemoryChildNodeEntry
operator|.
name|iterable
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

begin_comment
comment|/**  * Immutable snapshot of a mutable node state.  */
end_comment

begin_class
specifier|public
class|class
name|ModifiedNodeState
extends|extends
name|AbstractNodeState
block|{
specifier|public
specifier|static
name|NodeState
name|withProperties
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|PropertyState
argument_list|>
name|properties
parameter_list|)
block|{
if|if
condition|(
name|properties
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
return|;
block|}
block|}
specifier|public
specifier|static
name|NodeState
name|withNodes
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|NodeState
argument_list|>
name|nodes
parameter_list|)
block|{
if|if
condition|(
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
name|nodes
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|NodeState
name|with
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|PropertyState
argument_list|>
name|properties
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|NodeState
argument_list|>
name|nodes
parameter_list|)
block|{
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
specifier|public
specifier|static
name|ModifiedNodeState
name|collapse
parameter_list|(
name|ModifiedNodeState
name|state
parameter_list|)
block|{
name|NodeState
name|base
init|=
name|state
operator|.
name|getBaseState
argument_list|()
decl_stmt|;
if|if
condition|(
name|base
operator|instanceof
name|ModifiedNodeState
condition|)
block|{
name|ModifiedNodeState
name|mbase
init|=
name|collapse
argument_list|(
operator|(
name|ModifiedNodeState
operator|)
name|base
argument_list|)
decl_stmt|;
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
argument_list|(
name|mbase
operator|.
name|properties
argument_list|)
decl_stmt|;
name|properties
operator|.
name|putAll
argument_list|(
name|state
operator|.
name|properties
argument_list|)
expr_stmt|;
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
argument_list|(
name|mbase
operator|.
name|nodes
argument_list|)
decl_stmt|;
name|nodes
operator|.
name|putAll
argument_list|(
name|state
operator|.
name|nodes
argument_list|)
expr_stmt|;
return|return
operator|new
name|ModifiedNodeState
argument_list|(
name|mbase
operator|.
name|getBaseState
argument_list|()
argument_list|,
name|properties
argument_list|,
name|nodes
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|state
return|;
block|}
block|}
comment|/**      * The base state.      */
specifier|private
specifier|final
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
name|?
extends|extends
name|PropertyState
argument_list|>
name|properties
decl_stmt|;
comment|/**      * Set of added, modified or removed ({@code null} value)      * child nodes.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|NodeState
argument_list|>
name|nodes
decl_stmt|;
specifier|public
name|ModifiedNodeState
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|base
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|PropertyState
argument_list|>
name|properties
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|NodeState
argument_list|>
name|nodes
parameter_list|)
block|{
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
name|properties
operator|=
name|checkNotNull
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodes
operator|=
name|checkNotNull
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ModifiedNodeState
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|base
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|PropertyState
argument_list|>
name|properties
parameter_list|)
block|{
name|this
argument_list|(
name|base
argument_list|,
name|properties
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
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getBaseState
parameter_list|()
block|{
return|return
name|base
return|;
block|}
comment|//---------------------------------------------------------< NodeState>--
annotation|@
name|Override
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
name|?
extends|extends
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
condition|)
block|{
return|return
name|property
return|;
block|}
elseif|else
if|if
condition|(
name|properties
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
comment|// removed
block|}
else|else
block|{
return|return
name|base
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
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
if|if
condition|(
name|properties
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|base
operator|.
name|getProperties
argument_list|()
return|;
comment|// shortcut
block|}
else|else
block|{
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
name|properties
operator|.
name|containsKey
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
return|return
name|concat
argument_list|(
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
name|filter
argument_list|(
name|properties
operator|.
name|values
argument_list|()
argument_list|,
name|notNull
argument_list|()
argument_list|)
argument_list|)
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
name|?
extends|extends
name|NodeState
argument_list|>
name|entry
range|:
name|nodes
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
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
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
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|nodes
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
comment|// removed
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
name|NodeState
name|getChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
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
return|;
block|}
elseif|else
if|if
condition|(
name|nodes
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
comment|// removed
block|}
else|else
block|{
return|return
name|base
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
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
name|getChildNodeNames
parameter_list|()
block|{
if|if
condition|(
name|nodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|base
operator|.
name|getChildNodeNames
argument_list|()
return|;
comment|// shortcut
block|}
else|else
block|{
return|return
name|concat
argument_list|(
name|filter
argument_list|(
name|base
operator|.
name|getChildNodeNames
argument_list|()
argument_list|,
name|not
argument_list|(
name|in
argument_list|(
name|nodes
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|filterValues
argument_list|(
name|nodes
argument_list|,
name|notNull
argument_list|()
argument_list|)
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
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
if|if
condition|(
name|nodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|base
operator|.
name|getChildNodeEntries
argument_list|()
return|;
comment|// shortcut
block|}
else|else
block|{
name|Predicate
argument_list|<
name|ChildNodeEntry
argument_list|>
name|filter
init|=
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
name|ChildNodeEntry
name|input
parameter_list|)
block|{
return|return
operator|!
name|nodes
operator|.
name|containsKey
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
return|return
name|concat
argument_list|(
name|filter
argument_list|(
name|base
operator|.
name|getChildNodeEntries
argument_list|()
argument_list|,
name|filter
argument_list|)
argument_list|,
name|iterable
argument_list|(
name|filterValues
argument_list|(
name|nodes
argument_list|,
name|notNull
argument_list|()
argument_list|)
operator|.
name|entrySet
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**      * Since we keep track of an explicit base node state for a      * {@link ModifiedNodeState} instance, we can do this in two steps:      * first compare the base states to each other (often a fast operation),      * ignoring all changed properties and child nodes for which we have      * further modifications, and then compare all the modified properties      * and child nodes to those in the given base state.      */
annotation|@
name|Override
specifier|public
name|void
name|compareAgainstBaseState
parameter_list|(
name|NodeState
name|base
parameter_list|,
specifier|final
name|NodeStateDiff
name|diff
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|base
operator|!=
name|base
condition|)
block|{
name|this
operator|.
name|base
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
operator|new
name|NodeStateDiff
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|properties
operator|.
name|containsKey
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|diff
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|properties
operator|.
name|containsKey
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|diff
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
if|if
condition|(
operator|!
name|properties
operator|.
name|containsKey
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|diff
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|nodes
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|diff
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|nodes
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|diff
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
if|if
condition|(
operator|!
name|nodes
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|diff
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|?
extends|extends
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
name|PropertyState
name|before
init|=
name|base
operator|.
name|getProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|PropertyState
name|after
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|before
operator|==
literal|null
operator|&&
name|after
operator|==
literal|null
condition|)
block|{
comment|// do nothing
block|}
elseif|else
if|if
condition|(
name|after
operator|==
literal|null
condition|)
block|{
name|diff
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|before
operator|==
literal|null
condition|)
block|{
name|diff
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|before
operator|.
name|equals
argument_list|(
name|after
argument_list|)
condition|)
block|{
name|diff
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|NodeState
argument_list|>
name|entry
range|:
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
name|NodeState
name|after
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|before
operator|==
literal|null
operator|&&
name|after
operator|==
literal|null
condition|)
block|{
comment|// do nothing
block|}
elseif|else
if|if
condition|(
name|after
operator|==
literal|null
condition|)
block|{
name|diff
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|before
operator|==
literal|null
condition|)
block|{
name|diff
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|before
operator|.
name|equals
argument_list|(
name|after
argument_list|)
condition|)
block|{
name|diff
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

