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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
operator|.
name|copyOf
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
name|Lists
operator|.
name|newArrayList
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
name|uniqueIndex
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
name|MultiplexingContext
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
name|MultiplexingContext
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
name|this
operator|.
name|nodeStoresByMount
operator|=
name|copyOf
argument_list|(
name|uniqueIndex
argument_list|(
name|getAllMountedNodeStores
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|MountedNodeStore
argument_list|,
name|Mount
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Mount
name|apply
parameter_list|(
name|MountedNodeStore
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getMount
argument_list|()
return|;
block|}
block|}
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
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
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
return|return
name|nodeStates
operator|.
name|get
argument_list|(
name|input
argument_list|)
operator|.
name|getChildNodeNames
argument_list|()
return|;
block|}
block|}
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
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
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
return|return
name|nodeBuilders
operator|.
name|get
argument_list|(
name|input
argument_list|)
operator|.
name|getChildNodeNames
argument_list|()
return|;
block|}
block|}
argument_list|)
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
if|if
condition|(
name|mounts
operator|.
name|contains
argument_list|(
name|mountedNodeStore
operator|.
name|getMount
argument_list|()
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
else|else
block|{
if|if
condition|(
name|mountedNodeStore
operator|.
name|hasChildren
argument_list|(
name|childrenProvider
operator|.
name|apply
argument_list|(
name|mountedNodeStore
argument_list|)
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
block|}
return|return
name|mountedStores
return|;
block|}
name|Iterable
argument_list|<
name|MountedNodeStore
argument_list|>
name|getAllMountedNodeStores
parameter_list|()
block|{
return|return
name|concat
argument_list|(
name|singleton
argument_list|(
name|globalStore
argument_list|)
argument_list|,
name|nonDefaultStores
argument_list|)
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
name|int
name|getStoresCount
parameter_list|()
block|{
return|return
name|nonDefaultStores
operator|.
name|size
argument_list|()
operator|+
literal|1
return|;
block|}
name|Predicate
argument_list|<
name|String
argument_list|>
name|belongsToStore
parameter_list|(
specifier|final
name|MountedNodeStore
name|mountedNodeStore
parameter_list|,
specifier|final
name|String
name|parentPath
parameter_list|)
block|{
return|return
operator|new
name|Predicate
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
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
block|}
return|;
block|}
block|}
end_class

end_unit

