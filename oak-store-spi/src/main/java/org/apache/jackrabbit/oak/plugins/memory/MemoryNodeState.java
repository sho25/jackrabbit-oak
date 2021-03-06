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
name|EMPTY_NODE
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
name|HashMap
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
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * Basic in-memory node state implementation.  */
end_comment

begin_class
class|class
name|MemoryNodeState
extends|extends
name|AbstractNodeState
block|{
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
comment|/**      * Creates a new node state with the given properties and child nodes.      * The given maps are stored as references, so their contents and      * iteration order must remain unmodified at least for as long as this      * node state instance is in use.      *      * @param properties properties      * @param nodes child nodes      */
specifier|public
name|MemoryNodeState
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|properties
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
name|this
operator|.
name|nodes
operator|=
name|nodes
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasProperty
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
return|return
name|properties
operator|.
name|containsKey
argument_list|(
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
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
return|return
name|properties
operator|.
name|get
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
name|properties
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|NotNull
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
name|properties
operator|.
name|values
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChildNode
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
return|return
name|nodes
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|NodeState
name|getChildNode
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|NodeState
name|state
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
name|state
operator|==
literal|null
condition|)
block|{
name|checkValidName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|state
operator|=
name|MISSING_NODE
expr_stmt|;
block|}
return|return
name|state
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
name|nodes
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|ChildNodeEntry
argument_list|>
name|getChildNodeEntries
parameter_list|()
block|{
return|return
name|MemoryChildNodeEntry
operator|.
name|iterable
argument_list|(
name|nodes
operator|.
name|entrySet
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|NotNull
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
comment|/**      * We don't keep track of a separate base node state for      * {@link MemoryNodeState} instances, so this method will just do      * a generic diff against the given state.      */
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
if|if
condition|(
name|base
operator|==
name|EMPTY_NODE
operator|||
operator|!
name|base
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|EmptyNodeState
operator|.
name|compareAgainstEmptyState
argument_list|(
name|this
argument_list|,
name|diff
argument_list|)
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|newProperties
init|=
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
decl_stmt|;
for|for
control|(
name|PropertyState
name|before
range|:
name|base
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|PropertyState
name|after
init|=
name|newProperties
operator|.
name|remove
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
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
operator|!
name|after
operator|.
name|equals
argument_list|(
name|before
argument_list|)
condition|)
block|{
if|if
condition|(
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
block|}
for|for
control|(
name|PropertyState
name|after
range|:
name|newProperties
operator|.
name|values
argument_list|()
control|)
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
name|Map
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|newNodes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|base
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
name|NodeState
name|before
init|=
name|entry
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|after
init|=
name|newNodes
operator|.
name|remove
argument_list|(
name|name
argument_list|)
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
name|after
operator|!=
name|before
condition|)
block|{
if|if
condition|(
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
name|newNodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|childNodeAdded
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
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|static
name|MemoryNodeState
name|wrap
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|instanceof
name|MemoryNodeState
condition|)
block|{
return|return
operator|(
name|MemoryNodeState
operator|)
name|state
return|;
block|}
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
name|child
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MemoryNodeState
argument_list|(
name|properties
argument_list|,
name|nodes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

