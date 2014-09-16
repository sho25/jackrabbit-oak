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
name|kernel
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
name|concurrent
operator|.
name|ExecutionException
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
name|ReentrantLock
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|CacheLoader
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
name|LoadingCache
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
name|Weigher
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
name|util
operator|.
name|concurrent
operator|.
name|ListenableFuture
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
name|util
operator|.
name|concurrent
operator|.
name|SettableFuture
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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|mk
operator|.
name|api
operator|.
name|MicroKernelException
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
name|cache
operator|.
name|CacheLIRS
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
name|cache
operator|.
name|CacheStats
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
name|ChangeDispatcher
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

begin_comment
comment|/**  * {@code NodeStore} implementations against {@link MicroKernel}.  */
end_comment

begin_class
specifier|public
class|class
name|KernelNodeStore
implements|implements
name|NodeStore
implements|,
name|Observable
block|{
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_CACHE_SIZE
init|=
literal|16
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|/**      * The {@link MicroKernel} instance used to store the content tree.      */
specifier|private
specifier|final
name|MicroKernel
name|kernel
decl_stmt|;
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|String
argument_list|,
name|KernelNodeState
argument_list|>
name|cache
decl_stmt|;
specifier|private
specifier|final
name|CacheStats
name|cacheStats
decl_stmt|;
comment|/**      * Lock passed to branches for coordinating merges      */
specifier|private
specifier|final
name|Lock
name|mergeLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ChangeDispatcher
name|changeDispatcher
decl_stmt|;
comment|/**      * State of the current root node.      */
specifier|private
name|KernelNodeState
name|root
decl_stmt|;
specifier|public
name|KernelNodeStore
parameter_list|(
specifier|final
name|MicroKernel
name|kernel
parameter_list|,
name|long
name|cacheSize
parameter_list|)
block|{
name|this
operator|.
name|kernel
operator|=
name|checkNotNull
argument_list|(
name|kernel
argument_list|)
expr_stmt|;
name|Weigher
argument_list|<
name|String
argument_list|,
name|KernelNodeState
argument_list|>
name|weigher
init|=
operator|new
name|Weigher
argument_list|<
name|String
argument_list|,
name|KernelNodeState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|weigh
parameter_list|(
name|String
name|key
parameter_list|,
name|KernelNodeState
name|state
parameter_list|)
block|{
return|return
name|state
operator|.
name|getMemory
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|this
operator|.
name|cache
operator|=
name|CacheLIRS
operator|.
name|newBuilder
argument_list|()
operator|.
name|maximumWeight
argument_list|(
name|cacheSize
argument_list|)
operator|.
name|recordStats
argument_list|()
operator|.
name|weigher
argument_list|(
name|weigher
argument_list|)
operator|.
name|build
argument_list|(
operator|new
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|KernelNodeState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|KernelNodeState
name|load
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|int
name|slash
init|=
name|key
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
name|String
name|revision
init|=
name|key
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|slash
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|key
operator|.
name|substring
argument_list|(
name|slash
argument_list|)
decl_stmt|;
return|return
operator|new
name|KernelNodeState
argument_list|(
name|KernelNodeStore
operator|.
name|this
argument_list|,
name|path
argument_list|,
name|revision
argument_list|,
name|cache
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ListenableFuture
argument_list|<
name|KernelNodeState
argument_list|>
name|reload
parameter_list|(
name|String
name|key
parameter_list|,
name|KernelNodeState
name|oldValue
parameter_list|)
block|{
comment|// LoadingCache.reload() is only used to re-calculate the
comment|// memory usage on KernelNodeState.init(). Therefore
comment|// we simply return the old value as is (OAK-643)
name|SettableFuture
argument_list|<
name|KernelNodeState
argument_list|>
name|future
init|=
name|SettableFuture
operator|.
name|create
argument_list|()
decl_stmt|;
name|future
operator|.
name|set
argument_list|(
name|oldValue
argument_list|)
expr_stmt|;
return|return
name|future
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|cacheStats
operator|=
operator|new
name|CacheStats
argument_list|(
name|cache
argument_list|,
literal|"NodeStore"
argument_list|,
name|weigher
argument_list|,
name|cacheSize
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|root
operator|=
name|cache
operator|.
name|get
argument_list|(
name|kernel
operator|.
name|getHeadRevision
argument_list|()
operator|+
literal|'/'
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|changeDispatcher
operator|=
operator|new
name|ChangeDispatcher
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
specifier|public
name|KernelNodeStore
parameter_list|(
name|MicroKernel
name|kernel
parameter_list|)
block|{
name|this
argument_list|(
name|kernel
argument_list|,
name|DEFAULT_CACHE_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns a string representation the head state of this node store.      */
annotation|@
name|Override
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
comment|//------------------------------------------------------------< Observable>---
annotation|@
name|Override
specifier|public
name|Closeable
name|addObserver
parameter_list|(
name|Observer
name|observer
parameter_list|)
block|{
return|return
name|changeDispatcher
operator|.
name|addObserver
argument_list|(
name|observer
argument_list|)
return|;
block|}
comment|//----------------------------------------------------------< NodeStore>---
annotation|@
name|Override
specifier|public
specifier|synchronized
name|KernelNodeState
name|getRoot
parameter_list|()
block|{
name|String
name|revision
init|=
name|kernel
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|revision
operator|.
name|equals
argument_list|(
name|root
operator|.
name|getRevision
argument_list|()
argument_list|)
condition|)
block|{
name|root
operator|=
name|getRootState
argument_list|(
name|revision
argument_list|)
expr_stmt|;
block|}
return|return
name|root
return|;
block|}
comment|/**      * This implementation delegates to {@link KernelRootBuilder#merge(CommitHook, CommitInfo)}      * if {@code builder} is a {@link KernelNodeBuilder} instance. Otherwise it throws      * an {@code IllegalArgumentException}.      */
annotation|@
name|Override
specifier|public
name|NodeState
name|merge
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|,
annotation|@
name|Nonnull
name|CommitHook
name|commitHook
parameter_list|,
annotation|@
name|Nullable
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
name|KernelRootBuilder
argument_list|)
expr_stmt|;
return|return
operator|(
operator|(
name|KernelRootBuilder
operator|)
name|builder
operator|)
operator|.
name|merge
argument_list|(
name|checkNotNull
argument_list|(
name|commitHook
argument_list|)
argument_list|,
name|info
argument_list|)
return|;
block|}
comment|/**      * This implementation delegates to {@link KernelRootBuilder#rebase()} if {@code builder}      * is a {@link KernelNodeBuilder} instance. Otherwise Otherwise it throws an      * {@code IllegalArgumentException}.      * @param builder  the builder to rebase      */
annotation|@
name|Override
specifier|public
name|NodeState
name|rebase
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|builder
operator|instanceof
name|KernelRootBuilder
argument_list|)
expr_stmt|;
return|return
operator|(
operator|(
name|KernelRootBuilder
operator|)
name|builder
operator|)
operator|.
name|rebase
argument_list|()
return|;
block|}
comment|/**      * This implementation delegates to {@link KernelRootBuilder#reset()} if {@code builder}      * is a {@link KernelNodeBuilder} instance. Otherwise it throws an      * {@code IllegalArgumentException}.      * @param builder  the builder to rebase      */
annotation|@
name|Override
specifier|public
name|NodeState
name|reset
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|builder
operator|instanceof
name|KernelRootBuilder
argument_list|)
expr_stmt|;
return|return
operator|(
operator|(
name|KernelRootBuilder
operator|)
name|builder
operator|)
operator|.
name|reset
argument_list|()
return|;
block|}
comment|/**      * @return An instance of {@link KernelBlob}      */
annotation|@
name|Override
specifier|public
name|KernelBlob
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
name|String
name|blobId
init|=
name|kernel
operator|.
name|write
argument_list|(
name|inputStream
argument_list|)
decl_stmt|;
return|return
operator|new
name|KernelBlob
argument_list|(
name|blobId
argument_list|,
name|kernel
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|MicroKernelException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Blob
name|getBlob
parameter_list|(
annotation|@
name|Nonnull
name|String
name|reference
parameter_list|)
block|{
try|try
block|{
name|kernel
operator|.
name|getLength
argument_list|(
name|reference
argument_list|)
expr_stmt|;
comment|// throws if reference doesn't resolve
return|return
operator|new
name|KernelBlob
argument_list|(
name|reference
argument_list|,
name|kernel
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|MicroKernelException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|lifetime
operator|>
literal|0
argument_list|)
expr_stmt|;
return|return
name|kernel
operator|.
name|checkpoint
argument_list|(
name|lifetime
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|NodeState
name|retrieve
parameter_list|(
annotation|@
name|Nonnull
name|String
name|checkpoint
parameter_list|)
block|{
try|try
block|{
return|return
name|getRootState
argument_list|(
name|checkNotNull
argument_list|(
name|checkpoint
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|MicroKernelException
name|e
parameter_list|)
block|{
comment|// TODO: caused by the checkpoint no longer being available?
return|return
literal|null
return|;
block|}
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
comment|// TODO
return|return
literal|true
return|;
block|}
specifier|public
name|CacheStats
name|getCacheStats
parameter_list|()
block|{
return|return
name|cacheStats
return|;
block|}
comment|//-----------------------------------------------------------< internal>---
specifier|private
name|KernelNodeState
name|getRootState
parameter_list|(
name|String
name|revision
parameter_list|)
block|{
try|try
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
name|revision
operator|+
literal|"/"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MicroKernelException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
name|KernelNodeStoreBranch
name|createBranch
parameter_list|(
name|KernelNodeState
name|base
parameter_list|)
block|{
return|return
operator|new
name|KernelNodeStoreBranch
argument_list|(
name|this
argument_list|,
name|changeDispatcher
argument_list|,
name|mergeLock
argument_list|,
name|base
argument_list|)
return|;
block|}
name|MicroKernel
name|getKernel
parameter_list|()
block|{
return|return
name|kernel
return|;
block|}
name|KernelNodeState
name|commit
parameter_list|(
name|String
name|jsop
parameter_list|,
name|KernelNodeState
name|base
parameter_list|)
block|{
if|if
condition|(
name|jsop
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// nothing to commit
return|return
name|base
return|;
block|}
name|KernelNodeState
name|rootState
init|=
name|getRootState
argument_list|(
name|kernel
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
name|jsop
argument_list|,
name|base
operator|.
name|getRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|base
operator|.
name|isBranch
argument_list|()
condition|)
block|{
name|rootState
operator|.
name|setBranch
argument_list|()
expr_stmt|;
block|}
return|return
name|rootState
return|;
block|}
name|KernelNodeState
name|branch
parameter_list|(
name|KernelNodeState
name|base
parameter_list|)
block|{
return|return
name|getRootState
argument_list|(
name|kernel
operator|.
name|branch
argument_list|(
name|base
operator|.
name|getRevision
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setBranch
argument_list|()
return|;
block|}
name|KernelNodeState
name|rebase
parameter_list|(
name|KernelNodeState
name|branchHead
parameter_list|,
name|KernelNodeState
name|base
parameter_list|)
block|{
return|return
name|getRootState
argument_list|(
name|kernel
operator|.
name|rebase
argument_list|(
name|branchHead
operator|.
name|getRevision
argument_list|()
argument_list|,
name|base
operator|.
name|getRevision
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setBranch
argument_list|()
return|;
block|}
name|KernelNodeState
name|merge
parameter_list|(
name|KernelNodeState
name|branchHead
parameter_list|)
block|{
return|return
name|getRootState
argument_list|(
name|kernel
operator|.
name|merge
argument_list|(
name|branchHead
operator|.
name|getRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
name|KernelNodeState
name|reset
parameter_list|(
name|KernelNodeState
name|branchHead
parameter_list|,
name|KernelNodeState
name|ancestor
parameter_list|)
block|{
return|return
name|getRootState
argument_list|(
name|kernel
operator|.
name|reset
argument_list|(
name|branchHead
operator|.
name|getRevision
argument_list|()
argument_list|,
name|ancestor
operator|.
name|getRevision
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

