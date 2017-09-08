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
name|collect
operator|.
name|ImmutableSet
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
name|spi
operator|.
name|mount
operator|.
name|Mount
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
name|mount
operator|.
name|MountInfoProvider
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
name|NodeStore
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
name|Collection
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
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|StreamSupport
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
name|Lists
operator|.
name|newArrayList
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
name|singletonList
import|;
end_import

begin_class
class|class
name|CompositionContext
block|{
specifier|private
specifier|final
name|MountInfoProvider
name|mip
decl_stmt|;
specifier|private
specifier|final
name|MountedNodeStore
name|globalStore
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|MountedNodeStore
argument_list|>
name|nonDefaultStores
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|Mount
argument_list|,
name|MountedNodeStore
argument_list|>
name|nodeStoresByMount
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|MountedNodeStore
argument_list|>
name|allStores
decl_stmt|;
specifier|private
specifier|final
name|StringCache
name|pathCache
decl_stmt|;
name|CompositionContext
parameter_list|(
name|MountInfoProvider
name|mip
parameter_list|,
name|NodeStore
name|globalStore
parameter_list|,
name|List
argument_list|<
name|MountedNodeStore
argument_list|>
name|nonDefaultStores
parameter_list|)
block|{
name|this
operator|.
name|pathCache
operator|=
operator|new
name|StringCache
argument_list|()
expr_stmt|;
name|this
operator|.
name|mip
operator|=
name|mip
expr_stmt|;
name|this
operator|.
name|globalStore
operator|=
operator|new
name|MountedNodeStore
argument_list|(
name|mip
operator|.
name|getDefaultMount
argument_list|()
argument_list|,
name|globalStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|nonDefaultStores
operator|=
name|nonDefaultStores
expr_stmt|;
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|MountedNodeStore
argument_list|>
name|b
init|=
name|ImmutableSet
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b
operator|.
name|add
argument_list|(
name|this
operator|.
name|globalStore
argument_list|)
expr_stmt|;
name|b
operator|.
name|addAll
argument_list|(
name|this
operator|.
name|nonDefaultStores
argument_list|)
expr_stmt|;
name|allStores
operator|=
name|b
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeStoresByMount
operator|=
name|allStores
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toMap
argument_list|(
name|MountedNodeStore
operator|::
name|getMount
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|MountedNodeStore
name|getGlobalStore
parameter_list|()
block|{
return|return
name|globalStore
return|;
block|}
name|List
argument_list|<
name|MountedNodeStore
argument_list|>
name|getNonDefaultStores
parameter_list|()
block|{
return|return
name|nonDefaultStores
return|;
block|}
name|MountedNodeStore
name|getOwningStore
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|Mount
name|mount
init|=
name|mip
operator|.
name|getMountByPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeStoresByMount
operator|.
name|containsKey
argument_list|(
name|mount
argument_list|)
condition|)
block|{
return|return
name|nodeStoresByMount
operator|.
name|get
argument_list|(
name|mount
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to find an owning store for path "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
name|List
argument_list|<
name|MountedNodeStore
argument_list|>
name|getContributingStoresForNodes
parameter_list|(
name|String
name|path
parameter_list|,
specifier|final
name|NodeMap
argument_list|<
name|NodeState
argument_list|>
name|nodeStates
parameter_list|)
block|{
return|return
name|getContributingStores
argument_list|(
name|path
argument_list|,
name|mns
lambda|->
name|nodeStates
operator|.
name|get
argument_list|(
name|mns
argument_list|)
operator|.
name|getChildNodeNames
argument_list|()
argument_list|)
return|;
block|}
name|List
argument_list|<
name|MountedNodeStore
argument_list|>
name|getContributingStoresForBuilders
parameter_list|(
name|String
name|path
parameter_list|,
specifier|final
name|NodeMap
argument_list|<
name|NodeBuilder
argument_list|>
name|nodeBuilders
parameter_list|)
block|{
return|return
name|getContributingStores
argument_list|(
name|path
argument_list|,
name|mns
lambda|->
name|nodeBuilders
operator|.
name|get
argument_list|(
name|mns
argument_list|)
operator|.
name|getChildNodeNames
argument_list|()
argument_list|)
return|;
block|}
name|boolean
name|shouldBeComposite
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
block|{
name|boolean
name|supportMounts
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|mip
operator|.
name|getNonDefaultMounts
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|m
lambda|->
name|m
operator|.
name|isSupportFragmentUnder
argument_list|(
name|path
argument_list|)
argument_list|)
condition|)
block|{
name|supportMounts
operator|=
literal|true
block|;         }
elseif|else
if|if
condition|(
operator|!
name|mip
operator|.
name|getMountsPlacedUnder
argument_list|(
name|path
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|supportMounts
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|supportMounts
operator|&&
name|mip
operator|.
name|getMountByPath
argument_list|(
name|path
argument_list|)
operator|.
name|isDefault
argument_list|()
return|;
block|}
specifier|private
name|List
argument_list|<
name|MountedNodeStore
argument_list|>
name|getContributingStores
parameter_list|(
name|String
name|path
parameter_list|,
name|Function
argument_list|<
name|MountedNodeStore
argument_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|childrenProvider
parameter_list|)
block|{
name|Mount
name|owningMount
init|=
name|mip
operator|.
name|getMountByPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|owningMount
operator|.
name|isDefault
argument_list|()
operator|&&
name|nodeStoresByMount
operator|.
name|containsKey
argument_list|(
name|owningMount
argument_list|)
condition|)
block|{
name|MountedNodeStore
name|nodeStore
init|=
name|nodeStoresByMount
operator|.
name|get
argument_list|(
name|owningMount
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeStore
operator|!=
name|globalStore
condition|)
block|{
return|return
name|singletonList
argument_list|(
name|nodeStore
argument_list|)
return|;
block|}
block|}
comment|// scenario 2 - multiple mounts participate
name|List
argument_list|<
name|MountedNodeStore
argument_list|>
name|mountedStores
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|mountedStores
operator|.
name|add
argument_list|(
name|globalStore
argument_list|)
expr_stmt|;
comment|// we need mounts placed exactly one level beneath this path
name|Collection
argument_list|<
name|Mount
argument_list|>
name|mounts
init|=
name|mip
operator|.
name|getMountsPlacedDirectlyUnder
argument_list|(
name|path
argument_list|)
decl_stmt|;
comment|// query the mounts next
for|for
control|(
name|MountedNodeStore
name|mountedNodeStore
range|:
name|nonDefaultStores
control|)
block|{
specifier|final
name|Mount
name|mount
init|=
name|mountedNodeStore
operator|.
name|getMount
argument_list|()
decl_stmt|;
if|if
condition|(
name|mounts
operator|.
name|contains
argument_list|(
name|mount
argument_list|)
condition|)
block|{
name|mountedStores
operator|.
name|add
argument_list|(
name|mountedNodeStore
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|hasChildrenContainingPathFragmentName
argument_list|(
name|mountedNodeStore
argument_list|,
name|path
argument_list|,
name|childrenProvider
argument_list|)
condition|)
block|{
name|mountedStores
operator|.
name|add
argument_list|(
name|mountedNodeStore
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|mountedStores
return|;
block|}
specifier|private
name|boolean
name|hasChildrenContainingPathFragmentName
parameter_list|(
name|MountedNodeStore
name|mns
parameter_list|,
name|String
name|parentPath
parameter_list|,
name|Function
argument_list|<
name|MountedNodeStore
argument_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|childrenProvider
parameter_list|)
block|{
specifier|final
name|Mount
name|mount
init|=
name|mns
operator|.
name|getMount
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|mount
operator|.
name|isSupportFragment
argument_list|(
name|parentPath
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|StreamSupport
operator|.
name|stream
argument_list|(
name|childrenProvider
operator|.
name|apply
argument_list|(
name|mns
argument_list|)
operator|.
name|spliterator
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|anyMatch
argument_list|(
name|i
lambda|->
name|i
operator|.
name|contains
argument_list|(
name|mount
operator|.
name|getPathFragmentName
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
name|Set
argument_list|<
name|MountedNodeStore
argument_list|>
name|getAllMountedNodeStores
parameter_list|()
block|{
return|return
name|allStores
return|;
block|}
name|Blob
name|createBlob
parameter_list|(
name|InputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|globalStore
operator|.
name|getNodeStore
argument_list|()
operator|.
name|createBlob
argument_list|(
name|inputStream
argument_list|)
return|;
block|}
name|boolean
name|belongsToStore
parameter_list|(
specifier|final
name|MountedNodeStore
name|mountedNodeStore
parameter_list|,
specifier|final
name|String
name|parentPath
parameter_list|,
specifier|final
name|String
name|childName
parameter_list|)
block|{
return|return
name|getOwningStore
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentPath
argument_list|,
name|childName
argument_list|)
argument_list|)
operator|==
name|mountedNodeStore
return|;
block|}
name|CompositeNodeState
name|createRootNodeState
parameter_list|(
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeState
argument_list|>
name|rootStates
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeState
argument_list|>
name|e
range|:
name|rootStates
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|MountedNodeStore
name|mns
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|NodeState
name|nodeState
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeState
operator|instanceof
name|CompositeNodeState
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Nesting composite node states is not supported"
argument_list|)
throw|;
block|}
if|if
condition|(
name|nodeState
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Passed null as a nodestate for "
operator|+
name|mns
operator|.
name|getMount
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
for|for
control|(
name|MountedNodeStore
name|mns
range|:
name|nonDefaultStores
control|)
block|{
if|if
condition|(
operator|!
name|rootStates
operator|.
name|containsKey
argument_list|(
name|mns
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't find node state for "
operator|+
name|mns
operator|.
name|getMount
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|rootStates
operator|.
name|containsKey
argument_list|(
name|globalStore
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't find node state for the global store"
argument_list|)
throw|;
block|}
if|if
condition|(
name|rootStates
operator|.
name|size
argument_list|()
operator|!=
name|nonDefaultStores
operator|.
name|size
argument_list|()
operator|+
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Too many root states passed: "
operator|+
name|rootStates
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
return|return
operator|new
name|CompositeNodeState
argument_list|(
literal|"/"
argument_list|,
name|NodeMap
operator|.
name|create
argument_list|(
name|rootStates
argument_list|)
argument_list|,
name|this
argument_list|)
return|;
block|}
name|StringCache
name|getPathCache
parameter_list|()
block|{
return|return
name|pathCache
return|;
block|}
block|}
end_class

end_unit

