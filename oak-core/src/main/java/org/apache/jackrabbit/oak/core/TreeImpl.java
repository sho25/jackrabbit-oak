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
name|cache
operator|.
name|CacheBuilder
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
name|api
operator|.
name|TreeLocation
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
name|commons
operator|.
name|PathUtils
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|PathUtils
operator|.
name|elements
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
comment|/** Parent of this tree. Null for the root and this for removed trees. */
specifier|private
name|TreeImpl
name|parent
decl_stmt|;
comment|/** Name of this tree */
specifier|private
name|String
name|name
decl_stmt|;
comment|/** Lazily initialised {@code NodeBuilder} for the underlying node state */
name|NodeBuilder
name|nodeBuilder
decl_stmt|;
comment|/**      * Cache for child trees that have been accessed before.      */
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
name|getBaseState
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
specifier|synchronized
name|NodeBuilder
name|getNodeBuilder
parameter_list|()
block|{
if|if
condition|(
name|nodeBuilder
operator|==
literal|null
condition|)
block|{
name|nodeBuilder
operator|=
name|root
operator|.
name|createRootBuilder
argument_list|()
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
name|nodeBuilder
return|;
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
name|boolean
name|isRoot
parameter_list|()
block|{
return|return
name|parent
operator|==
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
if|if
condition|(
name|isRoot
argument_list|()
condition|)
block|{
comment|// shortcut
return|return
literal|"/"
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
if|if
condition|(
name|parent
operator|!=
literal|null
operator|&&
name|canRead
argument_list|(
name|parent
argument_list|)
condition|)
block|{
return|return
name|parent
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
name|PropertyState
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|canReadProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|internalGetProperty
argument_list|(
name|name
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
name|Status
name|getPropertyStatus
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// TODO: see OAK-212
if|if
condition|(
name|canReadProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|internalGetPropertyStatus
argument_list|(
name|name
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
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
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
comment|// TODO: make sure cnt respects access control
return|return
name|getNodeBuilder
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
name|Iterables
operator|.
name|filter
argument_list|(
name|getNodeBuilder
argument_list|()
operator|.
name|getProperties
argument_list|()
argument_list|,
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
name|propertyState
parameter_list|)
block|{
return|return
name|propertyState
operator|!=
literal|null
operator|&&
name|canReadProperty
argument_list|(
name|propertyState
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
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
name|internalGetChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|!=
literal|null
operator|&&
name|canRead
argument_list|(
name|child
argument_list|)
condition|)
block|{
return|return
name|child
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
name|Status
name|getStatus
parameter_list|()
block|{
if|if
condition|(
name|isRemoved
argument_list|()
condition|)
block|{
return|return
name|Status
operator|.
name|REMOVED
return|;
block|}
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
comment|// Did not exist before, so its NEW
return|return
name|Status
operator|.
name|NEW
return|;
block|}
else|else
block|{
comment|// Did exit it before. So...
if|if
condition|(
name|isSame
argument_list|(
name|baseState
argument_list|,
name|getNodeState
argument_list|()
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
name|getChild
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
comment|// TODO: make sure cnt respects access control
return|return
name|getNodeBuilder
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
name|Iterables
operator|.
name|filter
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|getNodeBuilder
argument_list|()
operator|.
name|getChildNodeNames
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|String
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
name|String
name|input
parameter_list|)
block|{
name|TreeImpl
name|child
init|=
name|children
operator|.
name|get
argument_list|(
name|input
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
name|input
argument_list|)
expr_stmt|;
name|children
operator|.
name|put
argument_list|(
name|input
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
argument_list|,
operator|new
name|Predicate
argument_list|<
name|Tree
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|tree
operator|!=
literal|null
operator|&&
name|canRead
argument_list|(
name|tree
argument_list|)
return|;
block|}
block|}
argument_list|)
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
name|NodeBuilder
name|builder
init|=
name|getNodeBuilder
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
name|root
operator|.
name|purge
argument_list|()
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
name|remove
parameter_list|()
block|{
if|if
condition|(
name|isRemoved
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot remove removed tree"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|isRoot
argument_list|()
operator|&&
name|parent
operator|.
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|NodeBuilder
name|builder
init|=
name|parent
operator|.
name|getNodeBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|removeNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|parent
operator|.
name|children
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|parent
operator|=
name|this
expr_stmt|;
name|root
operator|.
name|purge
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
name|NodeBuilder
name|builder
init|=
name|getNodeBuilder
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
name|root
operator|.
name|purge
argument_list|()
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
name|NodeBuilder
name|builder
init|=
name|getNodeBuilder
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
name|root
operator|.
name|purge
argument_list|()
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
name|NodeBuilder
name|builder
init|=
name|getNodeBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|root
operator|.
name|purge
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TreeLocation
name|getLocation
parameter_list|()
block|{
return|return
operator|new
name|NodeLocation
argument_list|(
name|this
argument_list|)
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
name|nodeBuilder
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
if|if
condition|(
name|isRemoved
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot get the base state of a removed tree"
argument_list|)
throw|;
block|}
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
specifier|synchronized
name|NodeBuilder
name|getNodeBuilder
parameter_list|()
block|{
if|if
condition|(
name|isRemoved
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot get a builder for a removed tree"
argument_list|)
throw|;
block|}
if|if
condition|(
name|nodeBuilder
operator|==
literal|null
condition|)
block|{
name|nodeBuilder
operator|=
name|parent
operator|.
name|getNodeBuilder
argument_list|()
operator|.
name|getChildBuilder
argument_list|(
name|name
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
name|nodeBuilder
return|;
block|}
comment|//-----------------------------------------------------------< internal>---
comment|/**      * Move this tree to the parent at {@code destParent} with the new name      * {@code destName}.      *      * @param destParent  new parent for this tree      * @param destName  new name for this tree      */
name|void
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
name|isRemoved
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot move removed tree"
argument_list|)
throw|;
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
block|}
annotation|@
name|Nonnull
name|NodeState
name|getNodeState
parameter_list|()
block|{
return|return
name|getNodeBuilder
argument_list|()
operator|.
name|getNodeState
argument_list|()
return|;
block|}
comment|/**      * Get a tree for the tree identified by {@code path}.      *      * @param path the path to the child      * @return a {@link Tree} instance for the child at {@code path} or      * {@code null} if no such tree exits or if the tree is not accessible.      */
annotation|@
name|CheckForNull
name|TreeImpl
name|getTree
parameter_list|(
name|String
name|path
parameter_list|)
block|{
assert|assert
name|path
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
assert|;
name|TreeImpl
name|child
init|=
name|this
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|child
operator|=
name|child
operator|.
name|internalGetChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|child
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
operator|(
name|canRead
argument_list|(
name|child
argument_list|)
operator|)
condition|?
name|child
else|:
literal|null
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|TreeImpl
name|internalGetChild
parameter_list|(
name|String
name|childName
parameter_list|)
block|{
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
operator|&&
name|getNodeBuilder
argument_list|()
operator|.
name|hasChildNode
argument_list|(
name|childName
argument_list|)
condition|)
block|{
name|child
operator|=
operator|new
name|TreeImpl
argument_list|(
name|root
argument_list|,
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
specifier|private
name|PropertyState
name|internalGetProperty
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
return|return
name|getNodeBuilder
argument_list|()
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
return|;
block|}
specifier|private
name|Status
name|internalGetPropertyStatus
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
name|boolean
name|exists
init|=
name|internalGetProperty
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
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
name|exists
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
name|exists
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
comment|// ...but did have it before. So it's REMOVED
return|return
name|Status
operator|.
name|REMOVED
return|;
block|}
block|}
block|}
block|}
specifier|private
name|boolean
name|isRemoved
parameter_list|()
block|{
return|return
name|parent
operator|==
name|this
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
name|isRemoved
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot build the path of a removed tree"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|isRoot
argument_list|()
condition|)
block|{
name|parent
operator|.
name|buildPath
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|sb
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
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|canRead
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
comment|// FIXME: special handling for access control item and version content
return|return
name|root
operator|.
name|getPermissions
argument_list|()
operator|.
name|canRead
argument_list|(
name|tree
operator|.
name|getPath
argument_list|()
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|canReadProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|path
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
comment|// FIXME: special handling for access control item and version content
return|return
name|root
operator|.
name|getPermissions
argument_list|()
operator|.
name|canRead
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|private
specifier|static
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
name|state2
operator|.
name|compareAgainstBaseState
argument_list|(
name|state1
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
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|weakValues
argument_list|()
operator|.
operator|<
name|String
decl_stmt|,
name|TreeImpl
decl|>
name|build
argument_list|()
decl|.
name|asMap
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
comment|//------------------------------------------------------------< TreeLocation>---
specifier|private
class|class
name|NodeLocation
implements|implements
name|TreeLocation
block|{
specifier|private
specifier|final
name|TreeImpl
name|tree
decl_stmt|;
specifier|public
name|NodeLocation
parameter_list|(
name|TreeImpl
name|tree
parameter_list|)
block|{
assert|assert
name|tree
operator|!=
literal|null
assert|;
name|this
operator|.
name|tree
operator|=
name|tree
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TreeLocation
name|getParent
parameter_list|()
block|{
return|return
name|tree
operator|.
name|parent
operator|==
literal|null
condition|?
name|NullLocation
operator|.
name|INSTANCE
else|:
operator|new
name|NodeLocation
argument_list|(
name|tree
operator|.
name|parent
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|TreeLocation
name|getChild
parameter_list|(
name|String
name|relPath
parameter_list|)
block|{
if|if
condition|(
name|relPath
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
name|TreeImpl
name|child
init|=
name|tree
decl_stmt|;
name|String
name|parentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|parentPath
argument_list|)
control|)
block|{
name|child
operator|=
name|child
operator|.
name|internalGetChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|child
operator|==
literal|null
condition|)
block|{
return|return
name|NullLocation
operator|.
name|INSTANCE
return|;
block|}
block|}
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
name|PropertyState
name|property
init|=
name|child
operator|.
name|internalGetProperty
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
operator|new
name|PropertyLocation
argument_list|(
operator|new
name|NodeLocation
argument_list|(
name|child
argument_list|)
argument_list|,
name|property
argument_list|)
return|;
block|}
else|else
block|{
name|child
operator|=
name|child
operator|.
name|internalGetChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|child
operator|==
literal|null
condition|?
name|NullLocation
operator|.
name|INSTANCE
else|:
operator|new
name|NodeLocation
argument_list|(
name|child
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|tree
operator|.
name|getPath
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|getTree
parameter_list|()
block|{
return|return
name|canRead
argument_list|(
name|tree
argument_list|)
condition|?
name|tree
else|:
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
return|return
name|tree
operator|.
name|getStatus
argument_list|()
return|;
block|}
block|}
specifier|private
class|class
name|PropertyLocation
implements|implements
name|TreeLocation
block|{
specifier|private
specifier|final
name|NodeLocation
name|parent
decl_stmt|;
specifier|private
specifier|final
name|PropertyState
name|property
decl_stmt|;
specifier|public
name|PropertyLocation
parameter_list|(
name|NodeLocation
name|parent
parameter_list|,
name|PropertyState
name|property
parameter_list|)
block|{
assert|assert
name|parent
operator|!=
literal|null
assert|;
assert|assert
name|property
operator|!=
literal|null
assert|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|property
operator|=
name|property
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TreeLocation
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
name|TreeLocation
name|getChild
parameter_list|(
name|String
name|relPath
parameter_list|)
block|{
return|return
name|NullLocation
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|property
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|getTree
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|()
block|{
return|return
name|root
operator|.
name|getPermissions
argument_list|()
operator|.
name|canRead
argument_list|(
name|getPath
argument_list|()
argument_list|,
literal|true
argument_list|)
condition|?
name|property
else|:
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
return|return
name|parent
operator|.
name|tree
operator|.
name|internalGetPropertyStatus
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|NullLocation
implements|implements
name|TreeLocation
block|{
specifier|public
specifier|static
specifier|final
name|NullLocation
name|INSTANCE
init|=
operator|new
name|NullLocation
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|TreeLocation
name|getParent
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|TreeLocation
name|getChild
parameter_list|(
name|String
name|relPath
parameter_list|)
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|getTree
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

