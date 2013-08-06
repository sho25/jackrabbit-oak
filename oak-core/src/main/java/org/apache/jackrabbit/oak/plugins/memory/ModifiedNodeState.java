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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
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
comment|/**      * Unwraps the given {@code NodeState} instance into the given internals      * of a {@link MutableNodeState} instance that is being created or reset.      *<p>      * If the given base state is a {@code ModifiedNodeState} instance,      * then the contained modifications are applied to the given properties      * property and child node maps and the contained base state is returned      * for use as the base state of the {@link MutableNodeState} instance.      *<p>      * If the given base state is not a {@code ModifiedNodeState}, then      * the given property and child node maps are simply reset and the given      * base state is returned as-is for use as the base state of the      * {@link MutableNodeState} instance.      *      * @param base new base state      * @param properties {@link MutableNodeState} property map      * @param nodes {@link MutableNodeState} child node map      * @return new {@link MutableNodeState} base state      */
specifier|static
name|NodeState
name|unwrap
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
name|MutableNodeState
argument_list|>
name|nodes
parameter_list|)
block|{
name|properties
operator|.
name|clear
argument_list|()
expr_stmt|;
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
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|reset
argument_list|(
name|base
operator|.
name|getChildNode
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|base
operator|instanceof
name|ModifiedNodeState
condition|)
block|{
name|ModifiedNodeState
name|modified
init|=
operator|(
name|ModifiedNodeState
operator|)
name|base
decl_stmt|;
name|properties
operator|.
name|putAll
argument_list|(
name|modified
operator|.
name|properties
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|entry
range|:
name|modified
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
name|nodes
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|MutableNodeState
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|modified
operator|.
name|base
return|;
block|}
else|else
block|{
return|return
name|base
return|;
block|}
block|}
comment|/**      * "Squeezes" {@link ModifiedNodeState} instances into equivalent      * {@link MemoryNodeState}s. Other kinds of states are returned as-is.      */
specifier|public
specifier|static
name|NodeState
name|squeeze
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|instanceof
name|ModifiedNodeState
condition|)
block|{
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
for|for
control|(
name|PropertyState
name|property
range|:
name|state
operator|.
name|getProperties
argument_list|()
control|)
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
name|Map
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|nodes
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|child
range|:
name|state
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|nodes
operator|.
name|put
argument_list|(
name|child
operator|.
name|getName
argument_list|()
argument_list|,
name|squeeze
argument_list|(
name|child
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|state
operator|=
operator|new
name|MemoryNodeState
argument_list|(
name|properties
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
block|}
return|return
name|state
return|;
block|}
specifier|static
name|long
name|getPropertyCount
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|properties
parameter_list|)
block|{
name|long
name|count
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|base
operator|.
name|exists
argument_list|()
condition|)
block|{
name|count
operator|=
name|base
operator|.
name|getPropertyCount
argument_list|()
expr_stmt|;
for|for
control|(
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
name|hasProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
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
block|}
return|return
name|count
return|;
block|}
specifier|static
name|boolean
name|hasProperty
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|properties
parameter_list|,
name|String
name|name
parameter_list|)
block|{
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
name|properties
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
else|else
block|{
return|return
name|base
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
specifier|static
name|PropertyState
name|getProperty
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|properties
parameter_list|,
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
operator|==
literal|null
operator|&&
operator|!
name|properties
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|property
operator|=
name|base
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|property
return|;
block|}
specifier|static
name|Iterable
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|getProperties
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|properties
parameter_list|,
name|boolean
name|copy
parameter_list|)
block|{
if|if
condition|(
operator|!
name|base
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|emptyList
argument_list|()
return|;
block|}
elseif|else
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
if|if
condition|(
name|copy
condition|)
block|{
name|properties
operator|=
name|newHashMap
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
name|predicate
init|=
name|Predicates
operator|.
name|compose
argument_list|(
name|not
argument_list|(
name|in
argument_list|(
name|properties
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|PropertyState
operator|.
name|GET_NAME
argument_list|)
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
name|predicate
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
specifier|static
name|long
name|getChildNodeCount
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
parameter_list|,
name|long
name|max
parameter_list|)
block|{
if|if
condition|(
operator|!
name|base
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|0
return|;
block|}
name|long
name|deleted
init|=
literal|0
decl_stmt|,
name|added
init|=
literal|0
decl_stmt|;
for|for
control|(
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
operator|!
name|base
operator|.
name|hasChildNode
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|added
operator|++
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
name|deleted
operator|++
expr_stmt|;
block|}
block|}
comment|// if we deleted 100 entries, then we need to
comment|// be sure there are 100 more entries than max
if|if
condition|(
name|max
operator|+
name|deleted
operator|<
literal|0
condition|)
block|{
comment|// avoid overflow
name|max
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|max
operator|+=
name|deleted
expr_stmt|;
block|}
name|long
name|count
init|=
name|base
operator|.
name|getChildNodeCount
argument_list|(
name|max
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|+
name|added
operator|-
name|deleted
operator|<
literal|0
condition|)
block|{
name|count
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|count
operator|=
name|count
operator|+
name|added
operator|-
name|deleted
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
specifier|static
name|Iterable
argument_list|<
name|String
argument_list|>
name|getChildNodeNames
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
parameter_list|,
name|boolean
name|copy
parameter_list|)
block|{
if|if
condition|(
operator|!
name|base
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|emptyList
argument_list|()
return|;
block|}
elseif|else
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
if|if
condition|(
name|copy
condition|)
block|{
name|nodes
operator|=
name|newHashMap
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
block|}
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
name|NodeState
operator|.
name|EXISTS
argument_list|)
operator|.
name|keySet
argument_list|()
argument_list|)
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
name|PropertyState
argument_list|>
name|properties
decl_stmt|;
comment|/**      * Set of added, modified or removed (non-existent value)      * child nodes.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|nodes
decl_stmt|;
comment|/**      * Creates an immutable snapshot of the given internal state of a      * {@link MutableNodeState} instance.      *      * @param base base state      * @param properties current property modifications      * @param nodes current child node modifications      */
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
name|MutableNodeState
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
if|if
condition|(
name|checkNotNull
argument_list|(
name|properties
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|properties
operator|=
name|emptyMap
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|properties
operator|=
name|newHashMap
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|checkNotNull
argument_list|(
name|nodes
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|nodes
operator|=
name|emptyMap
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|nodes
operator|=
name|newHashMap
argument_list|()
expr_stmt|;
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
name|this
operator|.
name|nodes
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|snapshot
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|boolean
name|exists
parameter_list|()
block|{
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
name|getProperties
argument_list|(
name|base
argument_list|,
name|properties
argument_list|,
literal|false
argument_list|)
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
name|getChildNodeCount
argument_list|(
name|base
argument_list|,
name|nodes
argument_list|,
name|max
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
comment|// checkArgument(!checkNotNull(name).isEmpty());  // TODO: should be caught earlier
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
operator|==
literal|null
condition|)
block|{
name|child
operator|=
name|base
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|child
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
name|getChildNodeNames
argument_list|(
name|base
argument_list|,
name|nodes
argument_list|,
literal|false
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
if|if
condition|(
operator|!
name|base
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|emptyList
argument_list|()
return|;
block|}
elseif|else
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
name|predicate
init|=
name|Predicates
operator|.
name|compose
argument_list|(
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
argument_list|,
name|ChildNodeEntry
operator|.
name|GET_NAME
argument_list|)
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
name|predicate
argument_list|)
argument_list|,
name|iterable
argument_list|(
name|filterValues
argument_list|(
name|nodes
argument_list|,
name|NodeState
operator|.
name|EXISTS
argument_list|)
operator|.
name|entrySet
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**      * Since we keep track of an explicit base node state for a      * {@link ModifiedNodeState} instance, we can do this in two steps:      * first compare all the modified properties and child nodes to those      * of the given base state, and then compare the base states to each      * other, ignoring all changed properties and child nodes that were      * already covered earlier.      */
annotation|@
name|Override
specifier|public
name|boolean
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
operator|==
name|base
condition|)
block|{
return|return
literal|true
return|;
comment|// no differences
block|}
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
name|after
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|before
operator|!=
literal|null
operator|&&
operator|!
name|diff
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|before
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
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
operator|&&
operator|!
name|diff
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
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
operator|!
name|after
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|before
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|diff
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|before
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|before
operator|!=
name|after
comment|// TODO: fastEquals?
operator|&&
operator|!
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
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
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
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
return|return
name|properties
operator|.
name|containsKey
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
operator|||
name|diff
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
return|return
name|properties
operator|.
name|containsKey
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
operator|||
name|diff
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
return|return
name|properties
operator|.
name|containsKey
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
operator|||
name|diff
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|nodes
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
operator|||
name|diff
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
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
return|return
name|nodes
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
operator|||
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
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
return|return
name|nodes
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
operator|||
name|diff
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
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

