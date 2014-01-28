begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|plugins
operator|.
name|memory
operator|.
name|MemoryChildNodeEntry
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
name|plugins
operator|.
name|memory
operator|.
name|MemoryNodeBuilder
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
name|security
operator|.
name|Context
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
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|PermissionProvider
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
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|TreePermission
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
name|Iterables
operator|.
name|transform
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

begin_comment
comment|/**  * SecureNodeState...  *  * TODO: clarify if HIDDEN items should be filtered by this NodeState implementation  * TODO: clarify usage of ReadStatus in getChildNodeEntries  */
end_comment

begin_class
class|class
name|SecureNodeState
extends|extends
name|AbstractNodeState
block|{
comment|/**      * Underlying node state.      */
specifier|private
specifier|final
name|NodeState
name|state
decl_stmt|;
comment|/**      * Tree permissions of this subtree.      */
specifier|private
specifier|final
name|TreePermission
name|treePermission
decl_stmt|;
specifier|private
name|long
name|childNodeCount
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|long
name|propertyCount
init|=
operator|-
literal|1
decl_stmt|;
name|SecureNodeState
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|state
parameter_list|,
annotation|@
name|Nonnull
name|TreePermission
name|treePermission
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|checkNotNull
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|this
operator|.
name|treePermission
operator|=
name|checkNotNull
argument_list|(
name|treePermission
argument_list|)
expr_stmt|;
block|}
name|SecureNodeState
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|,
annotation|@
name|Nonnull
name|PermissionProvider
name|permissionProvider
parameter_list|,
annotation|@
name|Nonnull
name|Context
name|acContext
parameter_list|)
block|{
name|this
argument_list|(
name|root
argument_list|,
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
operator|new
name|ImmutableTree
argument_list|(
name|root
argument_list|,
operator|new
name|TreeTypeProviderImpl
argument_list|(
name|acContext
argument_list|)
argument_list|)
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
argument_list|)
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
name|treePermission
operator|.
name|canRead
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
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
name|state
operator|.
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
name|treePermission
operator|.
name|canRead
argument_list|(
name|property
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
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|long
name|getPropertyCount
parameter_list|()
block|{
if|if
condition|(
name|propertyCount
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|treePermission
operator|.
name|canReadProperties
argument_list|()
condition|)
block|{
name|propertyCount
operator|=
name|state
operator|.
name|getPropertyCount
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|propertyCount
operator|=
name|count
argument_list|(
name|filter
argument_list|(
name|state
operator|.
name|getProperties
argument_list|()
argument_list|,
operator|new
name|ReadablePropertyPredicate
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|propertyCount
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
if|if
condition|(
name|treePermission
operator|.
name|canReadProperties
argument_list|()
condition|)
block|{
return|return
name|state
operator|.
name|getProperties
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|filter
argument_list|(
name|state
operator|.
name|getProperties
argument_list|()
argument_list|,
operator|new
name|ReadablePropertyPredicate
argument_list|()
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
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|NodeState
name|child
init|=
name|state
operator|.
name|getChildNode
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|treePermission
operator|.
name|canReadAll
argument_list|()
condition|)
block|{
name|ChildNodeEntry
name|entry
init|=
operator|new
name|MemoryChildNodeEntry
argument_list|(
name|name
argument_list|,
name|child
argument_list|)
decl_stmt|;
return|return
operator|new
name|WrapChildEntryFunction
argument_list|()
operator|.
name|apply
argument_list|(
name|entry
argument_list|)
operator|.
name|getNodeState
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|child
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|long
name|getChildNodeCount
parameter_list|(
name|long
name|max
parameter_list|)
block|{
if|if
condition|(
name|childNodeCount
operator|==
operator|-
literal|1
condition|)
block|{
name|long
name|count
decl_stmt|;
if|if
condition|(
name|treePermission
operator|.
name|canReadAll
argument_list|()
condition|)
block|{
name|count
operator|=
name|state
operator|.
name|getChildNodeCount
argument_list|(
name|max
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|count
operator|=
name|super
operator|.
name|getChildNodeCount
argument_list|(
name|max
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
name|count
return|;
block|}
name|childNodeCount
operator|=
name|count
expr_stmt|;
block|}
return|return
name|childNodeCount
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
if|if
condition|(
name|treePermission
operator|.
name|canReadAll
argument_list|()
condition|)
block|{
comment|// everything is readable including ac-content -> no secure wrapper needed
return|return
name|state
operator|.
name|getChildNodeEntries
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|treePermission
operator|.
name|canRead
argument_list|()
condition|)
block|{
comment|// TODO: check DENY_CHILDREN?
name|Iterable
argument_list|<
name|ChildNodeEntry
argument_list|>
name|readable
init|=
name|transform
argument_list|(
name|state
operator|.
name|getChildNodeEntries
argument_list|()
argument_list|,
operator|new
name|WrapChildEntryFunction
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|filter
argument_list|(
name|readable
argument_list|,
operator|new
name|IterableNodePredicate
argument_list|()
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
annotation|@
name|Nonnull
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
comment|//------------------------------------------------------< inner classes>---
comment|/**      * Predicate for testing whether a given property is readable.      */
specifier|private
class|class
name|ReadablePropertyPredicate
implements|implements
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|treePermission
operator|.
name|canRead
argument_list|(
name|property
argument_list|)
return|;
block|}
block|}
comment|/**      * Predicate for testing whether the node state in a child node entry is iterable.      */
specifier|private
specifier|static
class|class
name|IterableNodePredicate
implements|implements
name|Predicate
argument_list|<
name|ChildNodeEntry
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nonnull
name|ChildNodeEntry
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getNodeState
argument_list|()
operator|.
name|exists
argument_list|()
return|;
block|}
block|}
comment|/**      * Function that that adds a security wrapper to node states from      * in child node entries. The {@link IterableNodePredicate} predicate should be      * used on the result to filter out non-existing/iterable child nodes.      *<p>      * Note that the SecureNodeState wrapper is needed only when the child      * or any of its descendants has read access restrictions. Otherwise      * we can optimize access by skipping the security wrapper entirely.      */
specifier|private
class|class
name|WrapChildEntryFunction
implements|implements
name|Function
argument_list|<
name|ChildNodeEntry
argument_list|,
name|ChildNodeEntry
argument_list|>
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|ChildNodeEntry
name|apply
parameter_list|(
annotation|@
name|Nonnull
name|ChildNodeEntry
name|input
parameter_list|)
block|{
name|String
name|name
init|=
name|input
operator|.
name|getName
argument_list|()
decl_stmt|;
name|NodeState
name|child
init|=
name|input
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|TreePermission
name|childContext
init|=
name|treePermission
operator|.
name|getChildPermission
argument_list|(
name|name
argument_list|,
name|child
argument_list|)
decl_stmt|;
name|SecureNodeState
name|secureChild
init|=
operator|new
name|SecureNodeState
argument_list|(
name|child
argument_list|,
name|childContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|getChildNodeCount
argument_list|(
literal|1
argument_list|)
operator|==
literal|0
operator|&&
name|secureChild
operator|.
name|treePermission
operator|.
name|canRead
argument_list|()
operator|&&
name|secureChild
operator|.
name|treePermission
operator|.
name|canReadProperties
argument_list|()
condition|)
block|{
comment|// Since this is an accessible leaf node whose all properties
comment|// are readable, we don't need the SecureNodeState wrapper
comment|// TODO: A further optimization would be to return the raw
comment|// underlying node state even for non-leaf nodes if we can
comment|// tell in advance that the full subtree is readable. Then
comment|// we also wouldn't need the above getChildNodeCount() call
comment|// that's somewhat expensive on the DocumentMK.
return|return
name|input
return|;
block|}
else|else
block|{
return|return
operator|new
name|MemoryChildNodeEntry
argument_list|(
name|name
argument_list|,
name|secureChild
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

