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
name|multiplex
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
name|base
operator|.
name|Objects
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
name|Blob
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
name|Type
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
name|EmptyNodeState
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
name|MoveDetector
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
name|Preconditions
operator|.
name|checkState
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
name|transformValues
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
name|multiplex
operator|.
name|MultiplexingNodeState
operator|.
name|STOP_COUNTING_CHILDREN
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
name|multiplex
operator|.
name|MultiplexingNodeState
operator|.
name|accumulateChildSizes
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
name|AbstractNodeState
operator|.
name|checkValidName
import|;
end_import

begin_class
class|class
name|MultiplexingNodeBuilder
implements|implements
name|NodeBuilder
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|MultiplexingContext
name|ctx
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeBuilder
argument_list|>
name|nodeBuilders
decl_stmt|;
specifier|private
specifier|final
name|MountedNodeStore
name|owningStore
decl_stmt|;
specifier|private
specifier|final
name|MultiplexingNodeBuilder
name|parent
decl_stmt|;
specifier|private
specifier|final
name|MultiplexingNodeBuilder
name|rootBuilder
decl_stmt|;
name|MultiplexingNodeBuilder
parameter_list|(
name|String
name|path
parameter_list|,
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeBuilder
argument_list|>
name|nodeBuilders
parameter_list|,
name|MultiplexingContext
name|ctx
parameter_list|)
block|{
name|this
argument_list|(
name|path
argument_list|,
name|nodeBuilders
argument_list|,
name|ctx
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|private
name|MultiplexingNodeBuilder
parameter_list|(
name|String
name|path
parameter_list|,
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeBuilder
argument_list|>
name|nodeBuilders
parameter_list|,
name|MultiplexingContext
name|ctx
parameter_list|,
name|MultiplexingNodeBuilder
name|parent
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|nodeBuilders
operator|.
name|size
argument_list|()
operator|==
name|ctx
operator|.
name|getStoresCount
argument_list|()
argument_list|,
literal|"Got %s builders but the context manages %s stores"
argument_list|,
name|nodeBuilders
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
name|nodeBuilders
operator|=
operator|new
name|CopyOnReadIdentityMap
argument_list|<>
argument_list|(
name|nodeBuilders
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
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|rootBuilder
operator|=
name|this
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|rootBuilder
operator|=
name|parent
operator|.
name|rootBuilder
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeBuilder
argument_list|>
name|getBuilders
parameter_list|()
block|{
return|return
name|nodeBuilders
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getNodeState
parameter_list|()
block|{
return|return
operator|new
name|MultiplexingNodeState
argument_list|(
name|path
argument_list|,
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|(
name|buildersToNodeStates
argument_list|(
name|nodeBuilders
argument_list|)
argument_list|)
argument_list|,
name|ctx
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getBaseState
parameter_list|()
block|{
return|return
operator|new
name|MultiplexingNodeState
argument_list|(
name|path
argument_list|,
name|buildersToBaseStates
argument_list|(
name|nodeBuilders
argument_list|)
argument_list|,
name|ctx
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeState
argument_list|>
name|buildersToNodeStates
parameter_list|(
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeBuilder
argument_list|>
name|builders
parameter_list|)
block|{
return|return
name|transformValues
argument_list|(
name|builders
argument_list|,
operator|new
name|Function
argument_list|<
name|NodeBuilder
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
name|NodeBuilder
name|input
parameter_list|)
block|{
if|if
condition|(
name|input
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|input
operator|.
name|getNodeState
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|MISSING_NODE
return|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeState
argument_list|>
name|buildersToBaseStates
parameter_list|(
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeBuilder
argument_list|>
name|builders
parameter_list|)
block|{
return|return
name|transformValues
argument_list|(
name|builders
argument_list|,
operator|new
name|Function
argument_list|<
name|NodeBuilder
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
name|NodeBuilder
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getBaseState
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|// node or property-related methods ; directly delegate to wrapped builder
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
name|getWrappedNodeBuilder
argument_list|()
operator|.
name|exists
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isNew
parameter_list|()
block|{
return|return
name|getWrappedNodeBuilder
argument_list|()
operator|.
name|isNew
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isNew
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getWrappedNodeBuilder
argument_list|()
operator|.
name|isNew
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isModified
parameter_list|()
block|{
return|return
name|getWrappedNodeBuilder
argument_list|()
operator|.
name|isModified
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isReplaced
parameter_list|()
block|{
return|return
name|getWrappedNodeBuilder
argument_list|()
operator|.
name|isReplaced
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isReplaced
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getWrappedNodeBuilder
argument_list|()
operator|.
name|isReplaced
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
name|getWrappedNodeBuilder
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
name|getWrappedNodeBuilder
argument_list|()
operator|.
name|getProperties
argument_list|()
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
name|getWrappedNodeBuilder
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
name|getWrappedNodeBuilder
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
name|boolean
name|getBoolean
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getWrappedNodeBuilder
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getWrappedNodeBuilder
argument_list|()
operator|.
name|getString
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getWrappedNodeBuilder
argument_list|()
operator|.
name|getName
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
name|getNames
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getWrappedNodeBuilder
argument_list|()
operator|.
name|getNames
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|setProperty
parameter_list|(
name|PropertyState
name|property
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|getWrappedNodeBuilder
argument_list|()
operator|.
name|setProperty
argument_list|(
name|property
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|NodeBuilder
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|value
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|getWrappedNodeBuilder
argument_list|()
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|NodeBuilder
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|value
parameter_list|,
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|getWrappedNodeBuilder
argument_list|()
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|type
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|getWrappedNodeBuilder
argument_list|()
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|// child-related methods, require multiplexing
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
name|getContributingStoresForBuilders
argument_list|(
name|path
argument_list|,
name|nodeBuilders
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
name|getWrappedNodeBuilder
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
name|input
parameter_list|)
block|{
name|NodeBuilder
name|contributing
init|=
name|nodeBuilders
operator|.
name|get
argument_list|(
name|input
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
name|input
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
name|concat
argument_list|(
name|transform
argument_list|(
name|ctx
operator|.
name|getContributingStoresForBuilders
argument_list|(
name|path
argument_list|,
name|nodeBuilders
argument_list|)
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
specifier|final
name|MountedNodeStore
name|mountedNodeStore
parameter_list|)
block|{
return|return
name|filter
argument_list|(
name|nodeBuilders
operator|.
name|get
argument_list|(
name|mountedNodeStore
argument_list|)
operator|.
name|getChildNodeNames
argument_list|()
argument_list|,
name|ctx
operator|.
name|belongsToStore
argument_list|(
name|mountedNodeStore
argument_list|,
name|path
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
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
name|nodeBuilders
operator|.
name|get
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
name|NodeBuilder
name|child
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|getChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|setChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
specifier|private
name|void
name|createAncestors
parameter_list|(
name|MountedNodeStore
name|mountedNodeStore
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|rootBuilder
operator|.
name|nodeBuilders
operator|.
name|get
argument_list|(
name|mountedNodeStore
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|element
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|builder
operator|=
name|builder
operator|.
name|child
argument_list|(
name|element
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nodeBuilders
operator|instanceof
name|CopyOnReadIdentityMap
condition|)
block|{
name|nodeBuilders
operator|=
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|(
name|nodeBuilders
argument_list|)
expr_stmt|;
block|}
name|nodeBuilders
operator|.
name|put
argument_list|(
name|mountedNodeStore
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeBuilder
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
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeBuilder
argument_list|>
name|newNodeBuilders
init|=
name|Maps
operator|.
name|transformValues
argument_list|(
name|nodeBuilders
argument_list|,
operator|new
name|Function
argument_list|<
name|NodeBuilder
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
name|NodeBuilder
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
name|MultiplexingNodeBuilder
argument_list|(
name|childPath
argument_list|,
name|newNodeBuilders
argument_list|,
name|ctx
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|setChildNode
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
return|return
name|setChildNode
argument_list|(
name|name
argument_list|,
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|setChildNode
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
name|NodeState
name|nodeState
parameter_list|)
block|{
name|checkState
argument_list|(
name|exists
argument_list|()
argument_list|,
literal|"This builder does not exist: "
operator|+
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
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
specifier|final
name|MountedNodeStore
name|childStore
init|=
name|ctx
operator|.
name|getOwningStore
argument_list|(
name|childPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|childStore
operator|!=
name|owningStore
operator|&&
operator|!
name|nodeBuilders
operator|.
name|get
argument_list|(
name|childStore
argument_list|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|createAncestors
argument_list|(
name|childStore
argument_list|)
expr_stmt|;
block|}
specifier|final
name|NodeBuilder
name|childBuilder
init|=
name|nodeBuilders
operator|.
name|get
argument_list|(
name|childStore
argument_list|)
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
name|nodeState
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeBuilder
argument_list|>
name|newNodeBuilders
init|=
name|Maps
operator|.
name|transformEntries
argument_list|(
name|nodeBuilders
argument_list|,
operator|new
name|Maps
operator|.
name|EntryTransformer
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeBuilder
argument_list|,
name|NodeBuilder
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|transformEntry
parameter_list|(
name|MountedNodeStore
name|key
parameter_list|,
name|NodeBuilder
name|value
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
name|childStore
condition|)
block|{
return|return
name|childBuilder
return|;
block|}
else|else
block|{
return|return
name|value
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
return|return
operator|new
name|MultiplexingNodeBuilder
argument_list|(
name|childPath
argument_list|,
name|newNodeBuilders
argument_list|,
name|ctx
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|remove
parameter_list|()
block|{
return|return
name|getWrappedNodeBuilder
argument_list|()
operator|.
name|remove
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|moveTo
parameter_list|(
name|NodeBuilder
name|newParent
parameter_list|,
name|String
name|newName
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|newParent
argument_list|)
expr_stmt|;
name|checkValidName
argument_list|(
name|newName
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"/"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|||
operator|!
name|exists
argument_list|()
operator|||
name|newParent
operator|.
name|hasChildNode
argument_list|(
name|newName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
if|if
condition|(
name|newParent
operator|.
name|exists
argument_list|()
condition|)
block|{
name|annotateSourcePath
argument_list|()
expr_stmt|;
name|NodeState
name|nodeState
init|=
name|getNodeState
argument_list|()
decl_stmt|;
name|newParent
operator|.
name|setChildNode
argument_list|(
name|newName
argument_list|,
name|nodeState
argument_list|)
expr_stmt|;
name|remove
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
block|}
annotation|@
name|Override
specifier|public
name|Blob
name|createBlob
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ctx
operator|.
name|createBlob
argument_list|(
name|stream
argument_list|)
return|;
block|}
specifier|private
name|NodeBuilder
name|getWrappedNodeBuilder
parameter_list|()
block|{
return|return
name|nodeBuilders
operator|.
name|get
argument_list|(
name|owningStore
argument_list|)
return|;
block|}
specifier|private
name|void
name|annotateSourcePath
parameter_list|()
block|{
name|String
name|sourcePath
init|=
name|getSourcePath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isTransientlyAdded
argument_list|(
name|sourcePath
argument_list|)
condition|)
block|{
name|setProperty
argument_list|(
name|MoveDetector
operator|.
name|SOURCE_PATH
argument_list|,
name|sourcePath
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
name|String
name|getSourcePath
parameter_list|()
block|{
comment|// Traverse up the hierarchy until we encounter the first builder
comment|// having a source path annotation or until we hit the root
name|MultiplexingNodeBuilder
name|builder
init|=
name|this
decl_stmt|;
name|String
name|sourcePath
init|=
name|getSourcePathAnnotation
argument_list|(
name|builder
argument_list|)
decl_stmt|;
while|while
condition|(
name|sourcePath
operator|==
literal|null
operator|&&
name|builder
operator|.
name|parent
operator|!=
literal|null
condition|)
block|{
name|builder
operator|=
name|builder
operator|.
name|parent
expr_stmt|;
name|sourcePath
operator|=
name|getSourcePathAnnotation
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sourcePath
operator|==
literal|null
condition|)
block|{
comment|// Neither self nor any parent has a source path annotation. The source
comment|// path is just the path of this builder
return|return
name|getPath
argument_list|()
return|;
block|}
else|else
block|{
comment|// The source path is the source path of the first parent having a source
comment|// path annotation with the relative path from this builder up to that
comment|// parent appended.
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|sourcePath
argument_list|,
name|PathUtils
operator|.
name|relativize
argument_list|(
name|builder
operator|.
name|getPath
argument_list|()
argument_list|,
name|getPath
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|String
name|getSourcePathAnnotation
parameter_list|(
name|MultiplexingNodeBuilder
name|builder
parameter_list|)
block|{
name|PropertyState
name|base
init|=
name|builder
operator|.
name|getBaseState
argument_list|()
operator|.
name|getProperty
argument_list|(
name|MoveDetector
operator|.
name|SOURCE_PATH
argument_list|)
decl_stmt|;
name|PropertyState
name|head
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
operator|.
name|getProperty
argument_list|(
name|MoveDetector
operator|.
name|SOURCE_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|Objects
operator|.
name|equal
argument_list|(
name|base
argument_list|,
name|head
argument_list|)
condition|)
block|{
comment|// Both null: no source path annotation
comment|// Both non null but equals: source path annotation is from a previous commit
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|head
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
block|}
specifier|private
name|boolean
name|isTransientlyAdded
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|NodeState
name|node
init|=
name|rootBuilder
operator|.
name|getBaseState
argument_list|()
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
name|path
argument_list|)
control|)
block|{
name|node
operator|=
name|node
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
operator|!
name|node
operator|.
name|exists
argument_list|()
return|;
block|}
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|/**      * This simplified version of {@link PathUtils#concat(String, String)} method      * assumes that the parentPath is valid and not null, while the second argument      * is just a name (not a subpath).      *      * @param parentPath the parent path      * @param name       name to concatenate      * @return the parentPath concatenated with name      */
specifier|static
name|String
name|simpleConcat
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|checkValidName
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|parentPath
argument_list|)
condition|)
block|{
return|return
name|parentPath
operator|+
name|name
return|;
block|}
else|else
block|{
return|return
operator|new
name|StringBuilder
argument_list|(
name|parentPath
operator|.
name|length
argument_list|()
operator|+
name|name
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
operator|.
name|append
argument_list|(
name|parentPath
argument_list|)
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
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

