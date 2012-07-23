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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_comment
comment|/**  * Basic in-memory node state builder.  */
end_comment

begin_class
specifier|public
class|class
name|MemoryNodeStateBuilder
implements|implements
name|NodeStateBuilder
block|{
specifier|private
specifier|final
name|NodeState
name|base
decl_stmt|;
comment|/**      * Set of added, modified or removed ({@code null} value) property states.      */
specifier|private
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
comment|/**      * Set of builders for added, modified or removed ({@code null} value)      * child nodes.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NodeStateBuilder
argument_list|>
name|builders
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
comment|/**      * Flag to indicate that the current {@link #properties} map is being      * referenced by a {@link ModifiedNodeState} instance returned by a      * previous {@link #getNodeState()} call, and thus should not be      * modified unless first explicitly {@link #unfreeze() unfrozen}.      */
specifier|private
name|boolean
name|frozen
decl_stmt|;
comment|/**      * Creates a new in-memory node state builder.      *      * @param base base state of the new builder, or {@code null}      */
specifier|public
name|MemoryNodeStateBuilder
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
name|MemoryNodeState
operator|.
name|EMPTY_NODE
expr_stmt|;
block|}
block|}
comment|/**      * Factory method for creating new child state builders. Subclasses may      * override this method to control the behavior of child state builders.      *      * @param child base state of the new builder, or {@code null}      * @return new builder      */
specifier|protected
name|MemoryNodeStateBuilder
name|createChildBuilder
parameter_list|(
name|NodeState
name|child
parameter_list|)
block|{
return|return
operator|new
name|MemoryNodeStateBuilder
argument_list|(
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
comment|/**      * Ensures that the current {@link #properties} map is not {@link #frozen}.      */
specifier|private
name|void
name|unfreeze
parameter_list|()
block|{
if|if
condition|(
name|frozen
condition|)
block|{
name|properties
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|frozen
operator|=
literal|false
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getNodeState
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
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|properties
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|frozen
operator|=
literal|true
expr_stmt|;
name|props
operator|=
name|properties
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
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|builders
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|nodes
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
argument_list|(
name|builders
operator|.
name|size
argument_list|()
operator|*
literal|2
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NodeStateBuilder
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
block|}
if|if
condition|(
name|props
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
name|props
argument_list|,
name|nodes
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
name|NodeStateBuilder
argument_list|>
name|entry
range|:
name|builders
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|NodeState
name|before
init|=
name|base
operator|.
name|getChildNode
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|NodeStateBuilder
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
operator|!=
literal|null
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|before
operator|!=
literal|null
operator|&&
name|after
operator|==
literal|null
condition|)
block|{
name|count
operator|--
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
else|else
block|{
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
name|Iterables
operator|.
name|transform
argument_list|(
name|base
operator|.
name|getChildNodeEntries
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|ChildNodeEntry
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|ChildNodeEntry
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
argument_list|)
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
name|void
name|setNode
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|nodeState
parameter_list|)
block|{
if|if
condition|(
name|nodeState
operator|==
literal|null
condition|)
block|{
name|removeNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|nodeState
operator|.
name|equals
argument_list|(
name|base
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
condition|)
block|{
name|builders
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builders
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|createChildBuilder
argument_list|(
name|nodeState
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|updated
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
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
operator|!=
literal|null
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|before
operator|!=
literal|null
operator|&&
name|after
operator|==
literal|null
condition|)
block|{
name|count
operator|--
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
name|Iterable
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
block|{
name|frozen
operator|=
literal|true
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|properties
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
name|predicate
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
return|return
name|Iterables
operator|.
name|concat
argument_list|(
name|Iterables
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
argument_list|,
name|Iterables
operator|.
name|filter
argument_list|(
name|base
operator|.
name|getProperties
argument_list|()
argument_list|,
name|predicate
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
name|unfreeze
argument_list|()
expr_stmt|;
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
name|unfreeze
argument_list|()
expr_stmt|;
if|if
condition|(
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
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
name|unfreeze
argument_list|()
expr_stmt|;
if|if
condition|(
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
operator|==
literal|null
condition|)
block|{
name|NodeState
name|baseState
init|=
name|base
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|builder
operator|=
name|createChildBuilder
argument_list|(
name|baseState
argument_list|)
expr_stmt|;
name|builders
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

