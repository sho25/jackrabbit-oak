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
name|composite
package|;
end_package

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
name|Maps
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
name|Sets
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|checkArgument
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
name|compose
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
name|Iterables
operator|.
name|transform
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
name|asMap
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
name|transformValues
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
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Long
operator|.
name|MAX_VALUE
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
name|singleton
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
name|composite
operator|.
name|CompositeNodeBuilder
operator|.
name|simpleConcat
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
name|spi
operator|.
name|state
operator|.
name|ChildNodeEntry
operator|.
name|GET_NAME
import|;
end_import

begin_class
class|class
name|CompositeNodeState
extends|extends
name|AbstractNodeState
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CompositeNodeState
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// A note on content held by node stores which is outside the mount boundaries
comment|//
comment|// As a matter of design, mounted stores will definitely hold information _above_
comment|// their mounted, path, e.g. a store mounted at /a/b/c will definitely have nodes
comment|// /a and /a/b, which will not be visible through the composite node store.
comment|//
comment|// If a node store holds information _below_ a path which belongs to another
comment|// repository, the composite node store will not consider that information.
comment|//
comment|// For instance, with a node store mounted at /libs and the root store
comment|// having a node at /libs/food, both the /libs and /libs/foo nodes from
comment|// the root store will be ignored
specifier|static
specifier|final
name|String
name|STOP_COUNTING_CHILDREN
init|=
operator|new
name|String
argument_list|(
name|CompositeNodeState
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".stopCountingChildren"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|CompositionContext
name|ctx
decl_stmt|;
specifier|private
specifier|final
name|MountedNodeStore
name|owningStore
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeState
argument_list|>
name|nodeStates
decl_stmt|;
name|CompositeNodeState
parameter_list|(
name|String
name|path
parameter_list|,
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeState
argument_list|>
name|nodeStates
parameter_list|,
name|CompositionContext
name|ctx
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|nodeStates
operator|.
name|size
argument_list|()
operator|==
name|ctx
operator|.
name|getStoresCount
argument_list|()
argument_list|,
literal|"Got %s node states but the context manages %s stores"
argument_list|,
name|nodeStates
operator|.
name|size
argument_list|()
argument_list|,
name|ctx
operator|.
name|getStoresCount
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
name|this
operator|.
name|nodeStates
operator|=
operator|new
name|CopyOnReadIdentityMap
argument_list|<>
argument_list|(
name|nodeStates
argument_list|)
expr_stmt|;
name|this
operator|.
name|owningStore
operator|=
name|ctx
operator|.
name|getOwningStore
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
name|NodeState
name|getNodeState
parameter_list|(
name|MountedNodeStore
name|mns
parameter_list|)
block|{
name|NodeState
name|nodeState
init|=
name|nodeStates
operator|.
name|get
argument_list|(
name|mns
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeState
operator|!=
literal|null
condition|)
block|{
return|return
name|nodeState
return|;
block|}
comment|// this shouldn't happen, so we need to log some more debug info
name|String
name|mountName
init|=
name|mns
operator|.
name|getMount
argument_list|()
operator|.
name|isDefault
argument_list|()
condition|?
literal|"[default]"
else|:
name|mns
operator|.
name|getMount
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can't find node state for path {} and mount {}. The node state map: {}"
argument_list|,
name|path
argument_list|,
name|mountName
argument_list|,
name|nodeStates
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't find the node state for mount "
operator|+
name|mountName
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
name|getWrappedNodeState
argument_list|()
operator|.
name|exists
argument_list|()
return|;
block|}
comment|// delegate all property access to wrapped node
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
name|getWrappedNodeState
argument_list|()
operator|.
name|hasProperty
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
name|String
name|name
parameter_list|)
block|{
return|return
name|getWrappedNodeState
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
name|long
name|getPropertyCount
parameter_list|()
block|{
return|return
name|getWrappedNodeState
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
name|getWrappedNodeState
argument_list|()
operator|.
name|getProperties
argument_list|()
return|;
block|}
comment|// child node operations
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
name|String
name|childPath
init|=
name|simpleConcat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|MountedNodeStore
name|mountedStore
init|=
name|ctx
operator|.
name|getOwningStore
argument_list|(
name|childPath
argument_list|)
decl_stmt|;
return|return
name|getNodeState
argument_list|(
name|mountedStore
argument_list|)
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
name|NodeState
name|getChildNode
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|String
name|childPath
init|=
name|simpleConcat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|ctx
operator|.
name|shouldBeComposite
argument_list|(
name|childPath
argument_list|)
condition|)
block|{
return|return
name|getNodeState
argument_list|(
name|ctx
operator|.
name|getOwningStore
argument_list|(
name|childPath
argument_list|)
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeState
argument_list|>
name|newNodeStates
init|=
name|transformValues
argument_list|(
name|safeGetMap
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|NodeState
argument_list|,
name|NodeState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeState
name|apply
parameter_list|(
name|NodeState
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
operator|new
name|CompositeNodeState
argument_list|(
name|childPath
argument_list|,
name|newNodeStates
argument_list|,
name|ctx
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildNodeCount
parameter_list|(
specifier|final
name|long
name|max
parameter_list|)
block|{
name|List
argument_list|<
name|MountedNodeStore
argument_list|>
name|contributingStores
init|=
name|ctx
operator|.
name|getContributingStoresForNodes
argument_list|(
name|path
argument_list|,
name|safeGetMap
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|contributingStores
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|0
return|;
comment|// this shouldn't happen
block|}
elseif|else
if|if
condition|(
name|contributingStores
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|getWrappedNodeState
argument_list|()
operator|.
name|getChildNodeCount
argument_list|(
name|max
argument_list|)
return|;
block|}
else|else
block|{
comment|// Count the children in each contributing store.
return|return
name|accumulateChildSizes
argument_list|(
name|concat
argument_list|(
name|transform
argument_list|(
name|contributingStores
argument_list|,
operator|new
name|Function
argument_list|<
name|MountedNodeStore
argument_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|apply
parameter_list|(
name|MountedNodeStore
name|mns
parameter_list|)
block|{
name|NodeState
name|contributing
init|=
name|getNodeState
argument_list|(
name|mns
argument_list|)
decl_stmt|;
if|if
condition|(
name|contributing
operator|.
name|getChildNodeCount
argument_list|(
name|max
argument_list|)
operator|==
name|MAX_VALUE
condition|)
block|{
return|return
name|singleton
argument_list|(
name|STOP_COUNTING_CHILDREN
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|filter
argument_list|(
name|contributing
operator|.
name|getChildNodeNames
argument_list|()
argument_list|,
name|ctx
operator|.
name|belongsToStore
argument_list|(
name|mns
argument_list|,
name|path
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
argument_list|)
argument_list|,
name|max
argument_list|)
return|;
block|}
block|}
specifier|static
name|long
name|accumulateChildSizes
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|nodeNames
parameter_list|,
name|long
name|max
parameter_list|)
block|{
name|long
name|totalCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|nodeNames
control|)
block|{
name|totalCount
operator|++
expr_stmt|;
if|if
condition|(
name|name
operator|==
name|STOP_COUNTING_CHILDREN
operator|||
name|totalCount
operator|>=
name|max
condition|)
block|{
return|return
name|MAX_VALUE
return|;
block|}
block|}
return|return
name|totalCount
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
name|Iterable
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|nativeChildren
init|=
name|concat
argument_list|(
name|transform
argument_list|(
name|ctx
operator|.
name|getContributingStoresForNodes
argument_list|(
name|path
argument_list|,
name|safeGetMap
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Function
argument_list|<
name|MountedNodeStore
argument_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|apply
parameter_list|(
specifier|final
name|MountedNodeStore
name|mountedNodeStore
parameter_list|)
block|{
return|return
name|filter
argument_list|(
name|getNodeState
argument_list|(
name|mountedNodeStore
argument_list|)
operator|.
name|getChildNodeEntries
argument_list|()
argument_list|,
name|compose
argument_list|(
name|ctx
operator|.
name|belongsToStore
argument_list|(
name|mountedNodeStore
argument_list|,
name|path
argument_list|)
argument_list|,
name|GET_NAME
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|transform
argument_list|(
name|nativeChildren
argument_list|,
operator|new
name|Function
argument_list|<
name|ChildNodeEntry
argument_list|,
name|ChildNodeEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ChildNodeEntry
name|apply
parameter_list|(
name|ChildNodeEntry
name|input
parameter_list|)
block|{
name|NodeState
name|wrapped
init|=
name|getChildNode
argument_list|(
name|input
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|MemoryChildNodeEntry
argument_list|(
name|input
operator|.
name|getName
argument_list|()
argument_list|,
name|wrapped
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
operator|instanceof
name|CompositeNodeState
condition|)
block|{
name|CompositeNodeState
name|multiBase
init|=
operator|(
name|CompositeNodeState
operator|)
name|base
decl_stmt|;
name|NodeStateDiff
name|wrappingDiff
init|=
operator|new
name|WrappingDiff
argument_list|(
name|diff
argument_list|,
name|multiBase
argument_list|)
decl_stmt|;
name|boolean
name|full
init|=
name|getWrappedNodeState
argument_list|()
operator|.
name|compareAgainstBaseState
argument_list|(
name|multiBase
operator|.
name|getWrappedNodeState
argument_list|()
argument_list|,
operator|new
name|ChildrenDiffFilter
argument_list|(
name|wrappingDiff
argument_list|,
name|owningStore
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|MountedNodeStore
name|mns
range|:
name|ctx
operator|.
name|getContributingStoresForNodes
argument_list|(
name|path
argument_list|,
name|safeGetMap
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
name|owningStore
operator|==
name|mns
condition|)
block|{
continue|continue;
block|}
name|NodeStateDiff
name|childrenDiffFilter
init|=
operator|new
name|ChildrenDiffFilter
argument_list|(
name|wrappingDiff
argument_list|,
name|mns
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|NodeState
name|contributing
init|=
name|getNodeState
argument_list|(
name|mns
argument_list|)
decl_stmt|;
name|NodeState
name|contributingBase
init|=
name|multiBase
operator|.
name|getNodeState
argument_list|(
name|mns
argument_list|)
decl_stmt|;
name|full
operator|=
name|full
operator|&&
name|contributing
operator|.
name|compareAgainstBaseState
argument_list|(
name|contributingBase
argument_list|,
name|childrenDiffFilter
argument_list|)
expr_stmt|;
block|}
return|return
name|full
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
name|diff
argument_list|)
return|;
block|}
block|}
comment|// write operations
annotation|@
name|Override
specifier|public
name|CompositeNodeBuilder
name|builder
parameter_list|()
block|{
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeBuilder
argument_list|>
name|nodeBuilders
init|=
name|transformValues
argument_list|(
name|safeGetMap
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|NodeState
argument_list|,
name|NodeBuilder
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|apply
parameter_list|(
name|NodeState
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|builder
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
operator|new
name|CompositeNodeBuilder
argument_list|(
name|path
argument_list|,
name|nodeBuilders
argument_list|,
name|ctx
argument_list|)
return|;
block|}
specifier|private
name|NodeState
name|getWrappedNodeState
parameter_list|()
block|{
return|return
name|getNodeState
argument_list|(
name|owningStore
argument_list|)
return|;
block|}
specifier|private
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeState
argument_list|>
name|safeGetMap
parameter_list|()
block|{
return|return
name|asMap
argument_list|(
name|ctx
operator|.
name|getAllMountedNodeStores
argument_list|()
argument_list|,
name|this
operator|::
name|getNodeState
argument_list|)
return|;
block|}
specifier|private
class|class
name|ChildrenDiffFilter
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|final
name|NodeStateDiff
name|diff
decl_stmt|;
specifier|private
specifier|final
name|MountedNodeStore
name|mns
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|includeProperties
decl_stmt|;
specifier|public
name|ChildrenDiffFilter
parameter_list|(
name|NodeStateDiff
name|diff
parameter_list|,
name|MountedNodeStore
name|mns
parameter_list|,
name|boolean
name|includeProperties
parameter_list|)
block|{
name|this
operator|.
name|diff
operator|=
name|diff
expr_stmt|;
name|this
operator|.
name|mns
operator|=
name|mns
expr_stmt|;
name|this
operator|.
name|includeProperties
operator|=
name|includeProperties
expr_stmt|;
block|}
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
if|if
condition|(
name|includeProperties
condition|)
block|{
return|return
name|diff
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
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
if|if
condition|(
name|includeProperties
condition|)
block|{
return|return
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
else|else
block|{
return|return
literal|true
return|;
block|}
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
if|if
condition|(
name|includeProperties
condition|)
block|{
return|return
name|diff
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
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
if|if
condition|(
name|belongsToNodeStore
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
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
else|else
block|{
return|return
literal|true
return|;
block|}
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
if|if
condition|(
name|belongsToNodeStore
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
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
else|else
block|{
return|return
literal|true
return|;
block|}
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
if|if
condition|(
name|belongsToNodeStore
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
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
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
specifier|private
name|boolean
name|belongsToNodeStore
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|ctx
operator|.
name|getOwningStore
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
argument_list|)
operator|==
name|mns
return|;
block|}
block|}
specifier|private
class|class
name|WrappingDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|final
name|NodeStateDiff
name|diff
decl_stmt|;
specifier|private
specifier|final
name|CompositeNodeState
name|base
decl_stmt|;
specifier|public
name|WrappingDiff
parameter_list|(
name|NodeStateDiff
name|diff
parameter_list|,
name|CompositeNodeState
name|base
parameter_list|)
block|{
name|this
operator|.
name|diff
operator|=
name|diff
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
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
name|diff
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|wrapAfter
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
name|diff
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|wrapBefore
argument_list|(
name|name
argument_list|)
argument_list|,
name|wrapAfter
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
name|diff
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|wrapBefore
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|NodeState
name|wrapBefore
parameter_list|(
name|String
name|name
parameter_list|)
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
specifier|private
name|NodeState
name|wrapAfter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|CompositeNodeState
operator|.
name|this
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

