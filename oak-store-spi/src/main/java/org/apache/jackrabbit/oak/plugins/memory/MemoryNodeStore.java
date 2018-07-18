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
name|ModifiedNodeState
operator|.
name|squeeze
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|atomic
operator|.
name|AtomicReference
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|ByteStreams
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
name|state
operator|.
name|ConflictAnnotatingRebaseDiff
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
name|NodeStoreBranch
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

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Basic in-memory node store implementation. Useful as a base class for  * more complex functionality.  */
end_comment

begin_class
specifier|public
class|class
name|MemoryNodeStore
implements|implements
name|NodeStore
implements|,
name|Observable
block|{
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|NodeState
argument_list|>
name|root
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Checkpoint
argument_list|>
name|checkpoints
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|Closeable
argument_list|,
name|Observer
argument_list|>
name|observers
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|checkpointCounter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|public
name|MemoryNodeStore
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
operator|new
name|AtomicReference
argument_list|<
name|NodeState
argument_list|>
argument_list|(
name|MemoryNodeState
operator|.
name|wrap
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MemoryNodeStore
parameter_list|()
block|{
name|this
argument_list|(
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns a string representation the head state of this node store.      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getRoot
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Closeable
name|addObserver
parameter_list|(
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
name|Closeable
name|closeable
init|=
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
synchronized|synchronized
init|(
name|MemoryNodeStore
operator|.
name|this
init|)
block|{
name|observers
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|observers
operator|.
name|put
argument_list|(
name|closeable
argument_list|,
name|observer
argument_list|)
expr_stmt|;
return|return
name|closeable
return|;
block|}
specifier|private
specifier|synchronized
name|void
name|setRoot
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|root
operator|.
name|getAndSet
argument_list|(
name|root
argument_list|)
expr_stmt|;
for|for
control|(
name|Observer
name|observer
range|:
name|observers
operator|.
name|values
argument_list|()
control|)
block|{
name|observer
operator|.
name|contentChanged
argument_list|(
name|root
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getRoot
parameter_list|()
block|{
return|return
name|root
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * This implementation is equal to first rebasing the builder and then applying it to a      * new branch and immediately merging it back.      * @param builder  the builder whose changes to apply      * @param commitHook the commit hook to apply while merging changes      * @return the node state resulting from the merge.      * @throws CommitFailedException      * @throws IllegalArgumentException if the builder is not acquired from a root state of      *                                  this store      */
annotation|@
name|Override
specifier|public
specifier|synchronized
name|NodeState
name|merge
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|builder
parameter_list|,
annotation|@
name|NotNull
name|CommitHook
name|commitHook
parameter_list|,
annotation|@
name|NotNull
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
name|MemoryNodeBuilder
argument_list|)
expr_stmt|;
name|MemoryNodeBuilder
name|mnb
init|=
operator|(
name|MemoryNodeBuilder
operator|)
name|builder
decl_stmt|;
name|checkArgument
argument_list|(
name|mnb
operator|.
name|isRoot
argument_list|()
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|commitHook
argument_list|)
expr_stmt|;
name|rebase
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|NodeStoreBranch
name|branch
init|=
operator|new
name|MemoryNodeStoreBranch
argument_list|(
name|this
argument_list|,
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|branch
operator|.
name|setRoot
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|NodeState
name|merged
init|=
name|branch
operator|.
name|merge
argument_list|(
name|commitHook
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|mnb
operator|.
name|reset
argument_list|(
name|merged
argument_list|)
expr_stmt|;
return|return
name|merged
return|;
block|}
comment|/**      * This implementation is equal to applying the differences between the builders base state      * and its head state to a fresh builder on the stores root state using      * {@link org.apache.jackrabbit.oak.spi.state.ConflictAnnotatingRebaseDiff} for resolving      * conflicts.      * @param builder  the builder to rebase      * @return the node state resulting from the rebase.      * @throws IllegalArgumentException if the builder is not acquired from a root state of      *                                  this store      */
annotation|@
name|Override
specifier|public
name|NodeState
name|rebase
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|builder
operator|instanceof
name|MemoryNodeBuilder
argument_list|)
expr_stmt|;
name|NodeState
name|head
init|=
name|checkNotNull
argument_list|(
name|builder
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|base
init|=
name|builder
operator|.
name|getBaseState
argument_list|()
decl_stmt|;
name|NodeState
name|newBase
init|=
name|getRoot
argument_list|()
decl_stmt|;
if|if
condition|(
name|base
operator|!=
name|newBase
condition|)
block|{
operator|(
operator|(
name|MemoryNodeBuilder
operator|)
name|builder
operator|)
operator|.
name|reset
argument_list|(
name|newBase
argument_list|)
expr_stmt|;
name|head
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
operator|new
name|ConflictAnnotatingRebaseDiff
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|head
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
block|}
return|return
name|head
return|;
block|}
comment|/**      * This implementation is equal resetting the builder to the root of the store and returning      * the resulting node state from the builder.      * @param builder the builder to reset      * @return the node state resulting from the reset.      * @throws IllegalArgumentException if the builder is not acquired from a root state of      *                                  this store      */
annotation|@
name|Override
specifier|public
name|NodeState
name|reset
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|builder
operator|instanceof
name|MemoryNodeBuilder
argument_list|)
expr_stmt|;
name|NodeState
name|head
init|=
name|getRoot
argument_list|()
decl_stmt|;
operator|(
operator|(
name|MemoryNodeBuilder
operator|)
name|builder
operator|)
operator|.
name|reset
argument_list|(
name|head
argument_list|)
expr_stmt|;
return|return
name|head
return|;
block|}
comment|/**      * @return An instance of {@link ArrayBasedBlob}.      */
annotation|@
name|Override
specifier|public
name|ArrayBasedBlob
name|createBlob
parameter_list|(
name|InputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
operator|new
name|ArrayBasedBlob
argument_list|(
name|ByteStreams
operator|.
name|toByteArray
argument_list|(
name|inputStream
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|inputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Blob
name|getBlob
parameter_list|(
annotation|@
name|NotNull
name|String
name|reference
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|,
annotation|@
name|NotNull
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|lifetime
operator|>
literal|0
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|String
name|checkpoint
init|=
literal|"checkpoint"
operator|+
name|checkpointCounter
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
name|checkpoints
operator|.
name|put
argument_list|(
name|checkpoint
argument_list|,
operator|new
name|Checkpoint
argument_list|(
name|getRoot
argument_list|()
argument_list|,
name|properties
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|checkpoint
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
specifier|synchronized
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
name|NotNull
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
annotation|@
name|NotNull
name|String
name|checkpoint
parameter_list|)
block|{
name|Checkpoint
name|cp
init|=
name|checkpoints
operator|.
name|get
argument_list|(
name|checkNotNull
argument_list|(
name|checkpoint
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|cp
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|cp
operator|.
name|getProperties
argument_list|()
return|;
block|}
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Iterable
argument_list|<
name|String
argument_list|>
name|checkpoints
parameter_list|()
block|{
return|return
name|Lists
operator|.
name|newArrayList
argument_list|(
name|checkpoints
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
specifier|synchronized
name|NodeState
name|retrieve
parameter_list|(
annotation|@
name|NotNull
name|String
name|checkpoint
parameter_list|)
block|{
name|Checkpoint
name|cp
init|=
name|checkpoints
operator|.
name|get
argument_list|(
name|checkNotNull
argument_list|(
name|checkpoint
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|cp
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
return|return
name|cp
operator|.
name|getRoot
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|release
parameter_list|(
name|String
name|checkpoint
parameter_list|)
block|{
name|checkpoints
operator|.
name|remove
argument_list|(
name|checkpoint
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** test purpose only! */
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|listCheckpoints
parameter_list|()
block|{
return|return
name|Sets
operator|.
name|newHashSet
argument_list|(
name|checkpoints
argument_list|()
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
specifier|static
class|class
name|MemoryNodeStoreBranch
implements|implements
name|NodeStoreBranch
block|{
comment|/** The underlying store to which this branch belongs */
specifier|private
specifier|final
name|MemoryNodeStore
name|store
decl_stmt|;
comment|/** Root state of the base revision of this branch */
specifier|private
specifier|final
name|NodeState
name|base
decl_stmt|;
comment|/** Root state of the head revision of this branch*/
specifier|private
specifier|volatile
name|NodeState
name|root
decl_stmt|;
specifier|public
name|MemoryNodeStoreBranch
parameter_list|(
name|MemoryNodeStore
name|store
parameter_list|,
name|NodeState
name|base
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|base
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getBase
parameter_list|()
block|{
return|return
name|base
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getHead
parameter_list|()
block|{
name|checkNotMerged
argument_list|()
expr_stmt|;
return|return
name|root
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setRoot
parameter_list|(
name|NodeState
name|newRoot
parameter_list|)
block|{
name|checkNotMerged
argument_list|()
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|ModifiedNodeState
operator|.
name|squeeze
argument_list|(
name|newRoot
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|merge
parameter_list|(
annotation|@
name|NotNull
name|CommitHook
name|hook
parameter_list|,
annotation|@
name|NotNull
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|checkNotNull
argument_list|(
name|hook
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
comment|// TODO: rebase();
name|checkNotMerged
argument_list|()
expr_stmt|;
name|NodeState
name|merged
init|=
name|squeeze
argument_list|(
name|hook
operator|.
name|processCommit
argument_list|(
name|base
argument_list|,
name|root
argument_list|,
name|info
argument_list|)
argument_list|)
decl_stmt|;
name|store
operator|.
name|setRoot
argument_list|(
name|merged
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|root
operator|=
literal|null
expr_stmt|;
comment|// Mark as merged
return|return
name|merged
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|rebase
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|//------------------------------------------------------------< Object>---
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|root
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// ----------------------------------------------------< private>---
specifier|private
name|void
name|checkNotMerged
parameter_list|()
block|{
name|checkState
argument_list|(
name|root
operator|!=
literal|null
argument_list|,
literal|"Branch has already been merged"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|Checkpoint
block|{
specifier|private
specifier|final
name|NodeState
name|root
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
decl_stmt|;
specifier|private
name|Checkpoint
parameter_list|(
name|NodeState
name|root
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
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|properties
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NodeState
name|getRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getProperties
parameter_list|()
block|{
return|return
name|properties
return|;
block|}
block|}
block|}
end_class

end_unit

