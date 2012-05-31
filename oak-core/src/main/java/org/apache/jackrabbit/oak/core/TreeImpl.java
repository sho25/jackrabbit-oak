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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|map
operator|.
name|ReferenceMap
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
name|core
operator|.
name|RootImpl
operator|.
name|PurgeListener
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|util
operator|.
name|Function1
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
name|util
operator|.
name|Iterators
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|MemoryNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_class
specifier|public
class|class
name|TreeImpl
implements|implements
name|Tree
implements|,
name|PurgeListener
block|{
comment|/** Underlying {@code Root} of this {@code Tree} instance */
specifier|private
specifier|final
name|RootImpl
name|root
decl_stmt|;
comment|/** Parent of this tree */
specifier|private
name|TreeImpl
name|parent
decl_stmt|;
comment|/** Name of this tree */
specifier|private
name|String
name|name
decl_stmt|;
comment|/** Lazily initialised {@code NodeStateBuilder} for the underlying node state */
specifier|protected
name|NodeStateBuilder
name|nodeStateBuilder
decl_stmt|;
specifier|private
specifier|final
name|Children
name|children
init|=
operator|new
name|Children
argument_list|()
decl_stmt|;
specifier|private
name|TreeImpl
parameter_list|(
name|RootImpl
name|root
parameter_list|,
name|TreeImpl
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
assert|assert
name|root
operator|!=
literal|null
assert|;
assert|assert
name|name
operator|!=
literal|null
assert|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
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
block|}
annotation|@
name|Nonnull
specifier|static
name|TreeImpl
name|createRoot
parameter_list|(
specifier|final
name|RootImpl
name|root
parameter_list|)
block|{
return|return
operator|new
name|TreeImpl
argument_list|(
name|root
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|NodeState
name|getBaseState
parameter_list|()
block|{
return|return
name|root
operator|.
name|getWorkspaceBaseState
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|NodeState
name|getNodeState
parameter_list|()
block|{
return|return
name|nodeStateBuilder
operator|==
literal|null
condition|?
name|root
operator|.
name|getWorkspaceRootState
argument_list|()
else|:
name|nodeStateBuilder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|updateParentState
parameter_list|(
name|NodeState
name|childState
parameter_list|)
block|{
name|root
operator|.
name|setWorkspaceRootState
argument_list|(
name|childState
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
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
name|String
name|getPath
parameter_list|()
block|{
comment|// Shortcut for root
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
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
annotation|@
name|Override
specifier|public
name|Tree
name|getParent
parameter_list|()
block|{
return|return
name|parent
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
name|getNodeState
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
name|Status
name|getPropertyStatus
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|NodeState
name|baseState
init|=
name|getBaseState
argument_list|()
decl_stmt|;
if|if
condition|(
name|baseState
operator|==
literal|null
condition|)
block|{
comment|// This instance is NEW...
if|if
condition|(
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// ...so all children are new
return|return
name|Status
operator|.
name|NEW
return|;
block|}
else|else
block|{
comment|// ...unless they don't exist.
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// We have the property...
if|if
condition|(
name|baseState
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// ...but didn't have it before. So its NEW.
return|return
name|Status
operator|.
name|NEW
return|;
block|}
else|else
block|{
comment|// ... and did have it before. So...
name|PropertyState
name|base
init|=
name|baseState
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|PropertyState
name|head
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|base
operator|==
literal|null
condition|?
name|head
operator|==
literal|null
else|:
name|base
operator|.
name|equals
argument_list|(
name|head
argument_list|)
condition|)
block|{
comment|// ...it's EXISTING if it hasn't changed
return|return
name|Status
operator|.
name|EXISTING
return|;
block|}
else|else
block|{
comment|// ...and MODIFIED otherwise.
return|return
name|Status
operator|.
name|MODIFIED
return|;
block|}
block|}
block|}
else|else
block|{
comment|// We don't have the property
if|if
condition|(
name|baseState
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// ...and didn't have it before. So it doesn't exist.
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// ...and didn't have it before. So it's REMOVED
return|return
name|Status
operator|.
name|REMOVED
return|;
block|}
block|}
block|}
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
name|getNodeState
argument_list|()
operator|.
name|getProperty
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
name|long
name|getPropertyCount
parameter_list|()
block|{
return|return
name|getNodeState
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
name|getNodeState
argument_list|()
operator|.
name|getProperties
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|TreeImpl
name|getChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|TreeImpl
name|child
init|=
name|children
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
if|if
condition|(
operator|!
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|child
operator|=
operator|new
name|TreeImpl
argument_list|(
name|root
argument_list|,
name|this
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|children
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|child
argument_list|)
expr_stmt|;
return|return
name|child
return|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|getChildStatus
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|NodeState
name|baseState
init|=
name|getBaseState
argument_list|()
decl_stmt|;
if|if
condition|(
name|baseState
operator|==
literal|null
condition|)
block|{
comment|// This instance is NEW...
if|if
condition|(
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// ...so all children are new
return|return
name|Status
operator|.
name|NEW
return|;
block|}
else|else
block|{
comment|// ...unless they don't exist.
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// We have the child...
if|if
condition|(
name|baseState
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// ...but didn't have it before. So its NEW.
return|return
name|Status
operator|.
name|NEW
return|;
block|}
else|else
block|{
comment|// ... and did have it before. So...
if|if
condition|(
name|isSame
argument_list|(
name|baseState
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|,
name|getNodeState
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
condition|)
block|{
comment|// ...it's EXISTING if it hasn't changed
return|return
name|Status
operator|.
name|EXISTING
return|;
block|}
else|else
block|{
comment|// ...and MODIFIED otherwise.
return|return
name|Status
operator|.
name|MODIFIED
return|;
block|}
block|}
block|}
else|else
block|{
comment|// We don't have the child
if|if
condition|(
name|baseState
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// ...and didn't have it before. So it doesn't exist.
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// ...and didn't have it before. So it's REMOVED
return|return
name|Status
operator|.
name|REMOVED
return|;
block|}
block|}
block|}
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
name|getNodeState
argument_list|()
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
name|long
name|getChildrenCount
parameter_list|()
block|{
return|return
name|getNodeState
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
name|Tree
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|Tree
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Tree
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|NodeState
name|nodeState
init|=
name|getNodeState
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|childEntries
init|=
name|nodeState
operator|.
name|getChildNodeEntries
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
name|Iterators
operator|.
name|map
argument_list|(
name|childEntries
argument_list|,
operator|new
name|Function1
argument_list|<
name|ChildNodeEntry
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
name|ChildNodeEntry
name|entry
parameter_list|)
block|{
name|String
name|childName
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
name|TreeImpl
name|child
init|=
name|children
operator|.
name|get
argument_list|(
name|childName
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
name|TreeImpl
argument_list|(
name|root
argument_list|,
name|TreeImpl
operator|.
name|this
argument_list|,
name|childName
argument_list|)
expr_stmt|;
name|children
operator|.
name|put
argument_list|(
name|childName
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
return|return
name|child
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|addChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|NodeStateBuilder
name|builder
init|=
name|getNodeStateBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setNode
argument_list|(
name|name
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
name|updateParentState
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|TreeImpl
name|child
init|=
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
assert|assert
name|child
operator|!=
literal|null
assert|;
return|return
name|child
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|removeChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|NodeStateBuilder
name|builder
init|=
name|getNodeStateBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|removeNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|children
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|updateParentState
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
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
name|PropertyState
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|CoreValue
name|value
parameter_list|)
block|{
name|NodeStateBuilder
name|builder
init|=
name|getNodeStateBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|updateParentState
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|PropertyState
name|property
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
assert|assert
name|property
operator|!=
literal|null
assert|;
return|return
name|property
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyState
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
name|NodeStateBuilder
name|builder
init|=
name|getNodeStateBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|updateParentState
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|PropertyState
name|property
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
assert|assert
name|property
operator|!=
literal|null
assert|;
return|return
name|property
return|;
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
name|NodeStateBuilder
name|builder
init|=
name|getNodeStateBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|updateParentState
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Move this tree to the parent at {@code destParent} with the new name      * {@code destName}.      *      * @param destParent  new parent for this tree      * @param destName  new name for this tree      * @return {@code true} if this tree was moved.      */
specifier|public
name|boolean
name|moveTo
parameter_list|(
name|TreeImpl
name|destParent
parameter_list|,
name|String
name|destName
parameter_list|)
block|{
if|if
condition|(
name|destParent
operator|.
name|hasChild
argument_list|(
name|destName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|parent
operator|.
name|children
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|destParent
operator|.
name|children
operator|.
name|put
argument_list|(
name|destName
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|name
operator|=
name|destName
expr_stmt|;
name|parent
operator|=
name|destParent
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|//--------------------------------------------------< RootImpl.Listener>---
annotation|@
name|Override
specifier|public
name|void
name|purged
parameter_list|()
block|{
name|nodeStateBuilder
operator|=
literal|null
expr_stmt|;
block|}
comment|//----------------------------------------------------------< protected>---
annotation|@
name|CheckForNull
specifier|protected
name|NodeState
name|getBaseState
parameter_list|()
block|{
name|NodeState
name|parentBaseState
init|=
name|parent
operator|.
name|getBaseState
argument_list|()
decl_stmt|;
return|return
name|parentBaseState
operator|==
literal|null
condition|?
literal|null
else|:
name|parentBaseState
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|protected
name|NodeState
name|getNodeState
parameter_list|()
block|{
if|if
condition|(
name|nodeStateBuilder
operator|==
literal|null
condition|)
block|{
name|parent
operator|.
name|getNodeState
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|NodeState
name|nodeState
init|=
name|parent
operator|.
name|getNodeState
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
assert|assert
name|nodeState
operator|!=
literal|null
assert|;
return|return
name|nodeState
return|;
block|}
else|else
block|{
return|return
name|nodeStateBuilder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
block|}
specifier|protected
name|void
name|updateParentState
parameter_list|(
name|NodeState
name|childState
parameter_list|)
block|{
name|NodeStateBuilder
name|parentBuilder
init|=
name|parent
operator|.
name|getNodeStateBuilder
argument_list|()
decl_stmt|;
name|parentBuilder
operator|.
name|setNode
argument_list|(
name|name
argument_list|,
name|childState
argument_list|)
expr_stmt|;
name|parent
operator|.
name|updateParentState
argument_list|(
name|parentBuilder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
specifier|synchronized
name|NodeStateBuilder
name|getNodeStateBuilder
parameter_list|()
block|{
if|if
condition|(
name|nodeStateBuilder
operator|==
literal|null
condition|)
block|{
name|nodeStateBuilder
operator|=
name|root
operator|.
name|getBuilder
argument_list|(
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|addListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeStateBuilder
return|;
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
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|buildPath
argument_list|(
name|sb
argument_list|)
expr_stmt|;
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|isSame
parameter_list|(
name|NodeState
name|state1
parameter_list|,
name|NodeState
name|state2
parameter_list|)
block|{
specifier|final
name|boolean
index|[]
name|isDirty
init|=
block|{
literal|false
block|}
decl_stmt|;
name|root
operator|.
name|compare
argument_list|(
name|state1
argument_list|,
name|state2
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
name|isDirty
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
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
name|isDirty
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
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
name|isDirty
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
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
name|isDirty
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
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
comment|// cut transitivity here
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
name|isDirty
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
operator|!
name|isDirty
index|[
literal|0
index|]
return|;
block|}
specifier|private
specifier|static
class|class
name|Children
implements|implements
name|Iterable
argument_list|<
name|TreeImpl
argument_list|>
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|TreeImpl
argument_list|>
name|children
init|=
operator|new
name|ReferenceMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Lock
name|readLock
decl_stmt|;
specifier|private
specifier|final
name|Lock
name|writeLock
decl_stmt|;
block|{
name|ReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
name|readLock
operator|=
name|lock
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|writeLock
operator|=
name|lock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|put
parameter_list|(
name|String
name|name
parameter_list|,
name|TreeImpl
name|tree
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|children
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|tree
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|TreeImpl
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|children
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|children
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|children
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|TreeImpl
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|children
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

