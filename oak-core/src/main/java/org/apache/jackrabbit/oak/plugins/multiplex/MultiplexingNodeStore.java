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
name|collect
operator|.
name|Lists
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
name|CommitFailedException
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
name|commit
operator|.
name|CommitHook
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
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|EmptyHook
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
name|commit
operator|.
name|Observable
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
name|commit
operator|.
name|Observer
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
name|ApplyDiff
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
name|Closeable
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
name|Collections
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
name|CopyOnWriteArrayList
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
name|Predicates
operator|.
name|isNull
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
name|any
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
name|filterKeys
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

begin_comment
comment|/**  * A {@link NodeStore} implementation that multiplexes other {@link NodeStore} instances  * mounted under paths defined by {@link MountInfo}.  *  *<p>The main objective of this implementation is to multiplex operations working on  * at most single read-write store with any number of read-only stores. While the  * multiplexing would technically work at the NodeStore level there are several  * less-than-obvious issues which prevent it:  *<ol>  *<li>Thread safety of the write operation can be quite costly, and will come on top  *   of the thread safety measures already put in place by the multiplexed node stores.</li>  *<li>Many JCR subsystems require global state, e.g. the versioning store. This global state  *   can become corrupt if multiple mounts operate on it or if mounts are added and removed.</li>  *</ol>  *   *<p>As such, the only supported configuration is at most a single write-enabled store.  *  *<p>Because of the limitation described above, right now the only correct way to use  * MultiplexingNodeStore is to create a normal repository, split it into parts  * using oak-upgrade {@code --{include,exclude}-paths} and then configure this  * node store implementation to multiplex split parts together.  */
end_comment

begin_class
specifier|public
class|class
name|MultiplexingNodeStore
implements|implements
name|NodeStore
implements|,
name|Observable
block|{
specifier|private
specifier|static
specifier|final
name|String
name|CHECKPOINT_ID_PREFIX
init|=
literal|"multiplexing.checkpoint."
decl_stmt|;
specifier|final
name|MultiplexingContext
name|ctx
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Observer
argument_list|>
name|observers
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// visible for testing only
name|MultiplexingNodeStore
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
name|nonDefaultStore
parameter_list|)
block|{
name|this
operator|.
name|ctx
operator|=
operator|new
name|MultiplexingContext
argument_list|(
name|mip
argument_list|,
name|globalStore
argument_list|,
name|nonDefaultStore
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getRoot
parameter_list|()
block|{
comment|// the multiplexed root state exposes the node states as they are
comment|// at this certain point in time, so we eagerly retrieve them from all stores
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeState
argument_list|>
name|nodeStates
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|MountedNodeStore
name|nodeStore
range|:
name|ctx
operator|.
name|getAllMountedNodeStores
argument_list|()
control|)
block|{
name|nodeStates
operator|.
name|put
argument_list|(
name|nodeStore
argument_list|,
name|nodeStore
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|createRootNodeState
argument_list|(
name|nodeStates
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|merge
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|CommitHook
name|commitHook
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|checkArgument
argument_list|(
name|builder
operator|instanceof
name|MultiplexingNodeBuilder
argument_list|)
expr_stmt|;
name|MultiplexingNodeBuilder
name|nodeBuilder
init|=
operator|(
name|MultiplexingNodeBuilder
operator|)
name|builder
decl_stmt|;
comment|// run commit hooks and apply the changes to the builder instance
name|NodeState
name|processed
init|=
name|commitHook
operator|.
name|processCommit
argument_list|(
name|getRoot
argument_list|()
argument_list|,
name|rebase
argument_list|(
name|nodeBuilder
argument_list|)
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|processed
operator|.
name|compareAgainstBaseState
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
operator|new
name|ApplyDiff
argument_list|(
name|nodeBuilder
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoChangesOnReadOnlyMounts
argument_list|(
name|nodeBuilder
argument_list|)
expr_stmt|;
comment|// apply the accumulated changes on individual NodeStore instances
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeState
argument_list|>
name|resultStates
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|MountedNodeStore
name|mountedNodeStore
range|:
name|ctx
operator|.
name|getAllMountedNodeStores
argument_list|()
control|)
block|{
name|NodeStore
name|nodeStore
init|=
name|mountedNodeStore
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|partialBuilder
init|=
name|nodeBuilder
operator|.
name|getBuilders
argument_list|()
operator|.
name|get
argument_list|(
name|mountedNodeStore
argument_list|)
decl_stmt|;
name|NodeState
name|result
init|=
name|nodeStore
operator|.
name|merge
argument_list|(
name|partialBuilder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|resultStates
operator|.
name|put
argument_list|(
name|mountedNodeStore
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
name|MultiplexingNodeState
name|newRoot
init|=
name|createRootNodeState
argument_list|(
name|resultStates
argument_list|)
decl_stmt|;
for|for
control|(
name|Observer
name|observer
range|:
name|observers
control|)
block|{
name|observer
operator|.
name|contentChanged
argument_list|(
name|newRoot
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
return|return
name|newRoot
return|;
block|}
specifier|private
name|void
name|assertNoChangesOnReadOnlyMounts
parameter_list|(
name|MultiplexingNodeBuilder
name|nodeBuilder
parameter_list|)
throws|throws
name|CommitFailedException
block|{
for|for
control|(
name|MountedNodeStore
name|mountedNodeStore
range|:
name|ctx
operator|.
name|getAllMountedNodeStores
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|mountedNodeStore
operator|.
name|getMount
argument_list|()
operator|.
name|isReadOnly
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|NodeBuilder
name|partialBuilder
init|=
name|nodeBuilder
operator|.
name|getBuilders
argument_list|()
operator|.
name|get
argument_list|(
name|mountedNodeStore
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|partialBuilder
operator|.
name|getNodeState
argument_list|()
operator|.
name|equals
argument_list|(
name|partialBuilder
operator|.
name|getBaseState
argument_list|()
argument_list|)
condition|)
block|{
comment|// TODO - add proper error code
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Multiplex"
argument_list|,
literal|31
argument_list|,
literal|"Unable to perform changes on read-only mount "
operator|+
name|mountedNodeStore
operator|.
name|getMount
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|rebase
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|builder
operator|instanceof
name|MultiplexingNodeBuilder
argument_list|)
expr_stmt|;
name|MultiplexingNodeBuilder
name|nodeBuilder
init|=
operator|(
name|MultiplexingNodeBuilder
operator|)
name|builder
decl_stmt|;
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeState
argument_list|>
name|resultStates
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|MountedNodeStore
name|mountedNodeStore
range|:
name|ctx
operator|.
name|getAllMountedNodeStores
argument_list|()
control|)
block|{
name|NodeStore
name|nodeStore
init|=
name|mountedNodeStore
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|partialBuilder
init|=
name|nodeBuilder
operator|.
name|getBuilders
argument_list|()
operator|.
name|get
argument_list|(
name|mountedNodeStore
argument_list|)
decl_stmt|;
name|NodeState
name|result
init|=
name|nodeStore
operator|.
name|rebase
argument_list|(
name|partialBuilder
argument_list|)
decl_stmt|;
name|resultStates
operator|.
name|put
argument_list|(
name|mountedNodeStore
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|createRootNodeState
argument_list|(
name|resultStates
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|reset
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|builder
operator|instanceof
name|MultiplexingNodeBuilder
argument_list|)
expr_stmt|;
name|MultiplexingNodeBuilder
name|nodeBuilder
init|=
operator|(
name|MultiplexingNodeBuilder
operator|)
name|builder
decl_stmt|;
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeState
argument_list|>
name|resultStates
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|MountedNodeStore
name|mountedNodeStore
range|:
name|ctx
operator|.
name|getAllMountedNodeStores
argument_list|()
control|)
block|{
name|NodeStore
name|nodeStore
init|=
name|mountedNodeStore
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|partialBuilder
init|=
name|nodeBuilder
operator|.
name|getBuilders
argument_list|()
operator|.
name|get
argument_list|(
name|mountedNodeStore
argument_list|)
decl_stmt|;
name|NodeState
name|result
init|=
name|nodeStore
operator|.
name|reset
argument_list|(
name|partialBuilder
argument_list|)
decl_stmt|;
name|resultStates
operator|.
name|put
argument_list|(
name|mountedNodeStore
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|createRootNodeState
argument_list|(
name|resultStates
argument_list|)
return|;
block|}
specifier|private
name|MultiplexingNodeState
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
return|return
operator|new
name|MultiplexingNodeState
argument_list|(
literal|"/"
argument_list|,
name|rootStates
argument_list|,
name|ctx
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Blob
name|createBlob
parameter_list|(
name|InputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
block|{
comment|// since there is no way to infer a path for a blob, we create all blobs in the root store
return|return
name|ctx
operator|.
name|createBlob
argument_list|(
name|inputStream
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Blob
name|getBlob
parameter_list|(
name|String
name|reference
parameter_list|)
block|{
for|for
control|(
name|MountedNodeStore
name|nodeStore
range|:
name|ctx
operator|.
name|getAllMountedNodeStores
argument_list|()
control|)
block|{
name|Blob
name|found
init|=
name|nodeStore
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getBlob
argument_list|(
name|reference
argument_list|)
decl_stmt|;
if|if
condition|(
name|found
operator|!=
literal|null
condition|)
block|{
return|return
name|found
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|checkpoints
parameter_list|()
block|{
specifier|final
name|NodeStore
name|globalNodeStore
init|=
name|ctx
operator|.
name|getGlobalStore
argument_list|()
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
return|return
name|filter
argument_list|(
name|globalNodeStore
operator|.
name|checkpoints
argument_list|()
argument_list|,
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
name|checkpoint
parameter_list|)
block|{
return|return
name|isMultiplexingCheckpoint
argument_list|(
name|checkpoint
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isMultiplexingCheckpoint
parameter_list|(
name|String
name|checkpoint
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
name|ctx
operator|.
name|getGlobalStore
argument_list|()
operator|.
name|getNodeStore
argument_list|()
operator|.
name|checkpointInfo
argument_list|(
name|checkpoint
argument_list|)
decl_stmt|;
if|if
condition|(
name|props
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|MountedNodeStore
name|mns
range|:
name|ctx
operator|.
name|getNonDefaultStores
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|props
operator|.
name|containsKey
argument_list|(
name|CHECKPOINT_ID_PREFIX
operator|+
name|mns
operator|.
name|getMount
argument_list|()
operator|.
name|getName
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
annotation|@
name|Override
specifier|public
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|globalProperties
init|=
name|newHashMap
argument_list|(
name|properties
argument_list|)
decl_stmt|;
for|for
control|(
name|MountedNodeStore
name|mns
range|:
name|ctx
operator|.
name|getNonDefaultStores
argument_list|()
control|)
block|{
name|String
name|checkpoint
init|=
name|mns
operator|.
name|getNodeStore
argument_list|()
operator|.
name|checkpoint
argument_list|(
name|lifetime
argument_list|,
name|properties
argument_list|)
decl_stmt|;
name|globalProperties
operator|.
name|put
argument_list|(
name|CHECKPOINT_ID_PREFIX
operator|+
name|mns
operator|.
name|getMount
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
block|}
return|return
name|ctx
operator|.
name|getGlobalStore
argument_list|()
operator|.
name|getNodeStore
argument_list|()
operator|.
name|checkpoint
argument_list|(
name|lifetime
argument_list|,
name|globalProperties
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|)
block|{
return|return
name|checkpoint
argument_list|(
name|lifetime
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|emptyMap
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|checkpointInfo
parameter_list|(
name|String
name|checkpoint
parameter_list|)
block|{
return|return
name|copyOf
argument_list|(
name|filterKeys
argument_list|(
name|ctx
operator|.
name|getGlobalStore
argument_list|()
operator|.
name|getNodeStore
argument_list|()
operator|.
name|checkpointInfo
argument_list|(
name|checkpoint
argument_list|)
argument_list|,
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
name|input
parameter_list|)
block|{
return|return
operator|!
name|input
operator|.
name|startsWith
argument_list|(
name|CHECKPOINT_ID_PREFIX
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
name|NodeState
name|retrieve
parameter_list|(
name|String
name|checkpoint
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
name|ctx
operator|.
name|getGlobalStore
argument_list|()
operator|.
name|getNodeStore
argument_list|()
operator|.
name|checkpointInfo
argument_list|(
name|checkpoint
argument_list|)
decl_stmt|;
if|if
condition|(
name|props
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeState
argument_list|>
name|nodeStates
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|nodeStates
operator|.
name|put
argument_list|(
name|ctx
operator|.
name|getGlobalStore
argument_list|()
argument_list|,
name|ctx
operator|.
name|getGlobalStore
argument_list|()
operator|.
name|getNodeStore
argument_list|()
operator|.
name|retrieve
argument_list|(
name|checkpoint
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|MountedNodeStore
name|nodeStore
range|:
name|ctx
operator|.
name|getNonDefaultStores
argument_list|()
control|)
block|{
name|String
name|partialCheckpoint
init|=
name|props
operator|.
name|get
argument_list|(
name|CHECKPOINT_ID_PREFIX
operator|+
name|nodeStore
operator|.
name|getMount
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|partialCheckpoint
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|nodeStates
operator|.
name|put
argument_list|(
name|nodeStore
argument_list|,
name|nodeStore
operator|.
name|getNodeStore
argument_list|()
operator|.
name|retrieve
argument_list|(
name|partialCheckpoint
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|any
argument_list|(
name|nodeStates
operator|.
name|values
argument_list|()
argument_list|,
name|isNull
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|MultiplexingNodeState
argument_list|(
literal|"/"
argument_list|,
name|nodeStates
argument_list|,
name|ctx
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|release
parameter_list|(
name|String
name|checkpoint
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
name|ctx
operator|.
name|getGlobalStore
argument_list|()
operator|.
name|getNodeStore
argument_list|()
operator|.
name|checkpointInfo
argument_list|(
name|checkpoint
argument_list|)
decl_stmt|;
if|if
condition|(
name|props
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|result
init|=
name|ctx
operator|.
name|getGlobalStore
argument_list|()
operator|.
name|getNodeStore
argument_list|()
operator|.
name|release
argument_list|(
name|checkpoint
argument_list|)
decl_stmt|;
for|for
control|(
name|MountedNodeStore
name|nodeStore
range|:
name|ctx
operator|.
name|getNonDefaultStores
argument_list|()
control|)
block|{
name|String
name|partialCheckpoint
init|=
name|props
operator|.
name|get
argument_list|(
name|CHECKPOINT_ID_PREFIX
operator|+
name|nodeStore
operator|.
name|getMount
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|partialCheckpoint
operator|==
literal|null
condition|)
block|{
name|result
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|nodeStore
operator|.
name|getNodeStore
argument_list|()
operator|.
name|release
argument_list|(
name|partialCheckpoint
argument_list|)
operator|&&
name|result
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|Closeable
name|addObserver
parameter_list|(
specifier|final
name|Observer
name|observer
parameter_list|)
block|{
name|observer
operator|.
name|contentChanged
argument_list|(
name|getRoot
argument_list|()
argument_list|,
name|CommitInfo
operator|.
name|EMPTY_EXTERNAL
argument_list|)
expr_stmt|;
name|observers
operator|.
name|add
argument_list|(
name|observer
argument_list|)
expr_stmt|;
return|return
operator|new
name|Closeable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|observers
operator|.
name|remove
argument_list|(
name|observer
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
class|class
name|Builder
block|{
specifier|private
specifier|final
name|MountInfoProvider
name|mip
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|globalStore
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|MountedNodeStore
argument_list|>
name|nonDefaultStores
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|public
name|Builder
parameter_list|(
name|MountInfoProvider
name|mip
parameter_list|,
name|NodeStore
name|globalStore
parameter_list|)
block|{
name|this
operator|.
name|mip
operator|=
name|checkNotNull
argument_list|(
name|mip
argument_list|,
literal|"mountInfoProvider"
argument_list|)
expr_stmt|;
name|this
operator|.
name|globalStore
operator|=
name|checkNotNull
argument_list|(
name|globalStore
argument_list|,
literal|"globalStore"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Builder
name|addMount
parameter_list|(
name|String
name|mountName
parameter_list|,
name|NodeStore
name|store
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|store
argument_list|,
literal|"store"
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|mountName
argument_list|,
literal|"mountName"
argument_list|)
expr_stmt|;
name|Mount
name|mount
init|=
name|checkNotNull
argument_list|(
name|mip
operator|.
name|getMountByName
argument_list|(
name|mountName
argument_list|)
argument_list|,
literal|"No mount with name %s found in %s"
argument_list|,
name|mountName
argument_list|,
name|mip
argument_list|)
decl_stmt|;
name|nonDefaultStores
operator|.
name|add
argument_list|(
operator|new
name|MountedNodeStore
argument_list|(
name|mount
argument_list|,
name|store
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|MultiplexingNodeStore
name|build
parameter_list|()
block|{
name|checkReadWriteMountsNumber
argument_list|()
expr_stmt|;
name|checkMountsAreConsistentWithMounts
argument_list|()
expr_stmt|;
return|return
operator|new
name|MultiplexingNodeStore
argument_list|(
name|mip
argument_list|,
name|globalStore
argument_list|,
name|nonDefaultStores
argument_list|)
return|;
block|}
specifier|private
name|void
name|checkReadWriteMountsNumber
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|readWriteMountNames
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|mip
operator|.
name|getDefaultMount
argument_list|()
operator|.
name|isReadOnly
argument_list|()
condition|)
block|{
name|readWriteMountNames
operator|.
name|add
argument_list|(
name|mip
operator|.
name|getDefaultMount
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Mount
name|mount
range|:
name|mip
operator|.
name|getNonDefaultMounts
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|mount
operator|.
name|isReadOnly
argument_list|()
condition|)
block|{
name|readWriteMountNames
operator|.
name|add
argument_list|(
name|mount
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|checkArgument
argument_list|(
name|readWriteMountNames
operator|.
name|size
argument_list|()
operator|<=
literal|1
argument_list|,
literal|"Expected at most 1 write-enabled mount, but got %s: %s."
argument_list|,
name|readWriteMountNames
operator|.
name|size
argument_list|()
argument_list|,
name|readWriteMountNames
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkMountsAreConsistentWithMounts
parameter_list|()
block|{
name|int
name|buildMountCount
init|=
name|nonDefaultStores
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|mipMountCount
init|=
name|mip
operator|.
name|getNonDefaultMounts
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|checkArgument
argument_list|(
name|buildMountCount
operator|==
name|mipMountCount
argument_list|,
literal|"Inconsistent mount configuration. Builder received %s mounts, but MountInfoProvider knows about %s."
argument_list|,
name|buildMountCount
argument_list|,
name|mipMountCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

