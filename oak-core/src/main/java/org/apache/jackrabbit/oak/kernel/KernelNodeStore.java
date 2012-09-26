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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|CoreValueFactory
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
name|EmptyObserver
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

begin_comment
comment|/**  * {@code NodeStore} implementations against {@link MicroKernel}.  */
end_comment

begin_class
specifier|public
class|class
name|KernelNodeStore
implements|implements
name|NodeStore
block|{
comment|/**      * The {@link MicroKernel} instance used to store the content tree.      */
specifier|private
specifier|final
name|MicroKernel
name|kernel
decl_stmt|;
comment|/**      * Commit hook.      */
annotation|@
name|Nonnull
specifier|private
specifier|volatile
name|CommitHook
name|hook
init|=
name|EmptyHook
operator|.
name|INSTANCE
decl_stmt|;
comment|/**      * Change observer.      */
annotation|@
name|Nonnull
specifier|private
specifier|volatile
name|Observer
name|observer
init|=
name|EmptyObserver
operator|.
name|INSTANCE
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
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|maximumSize
argument_list|(
literal|10000
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
name|kernel
argument_list|,
name|path
argument_list|,
name|revision
argument_list|,
name|cache
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|/**      * State of the current root node.      */
specifier|private
name|KernelNodeState
name|root
decl_stmt|;
specifier|public
name|KernelNodeStore
parameter_list|(
name|MicroKernel
name|kernel
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
literal|"/"
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
block|}
annotation|@
name|Nonnull
specifier|public
name|CommitHook
name|getHook
parameter_list|()
block|{
return|return
name|hook
return|;
block|}
specifier|public
name|void
name|setHook
parameter_list|(
name|CommitHook
name|hook
parameter_list|)
block|{
name|this
operator|.
name|hook
operator|=
name|checkNotNull
argument_list|(
name|hook
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Observer
name|getObserver
parameter_list|()
block|{
return|return
name|observer
return|;
block|}
specifier|public
name|void
name|setObserver
parameter_list|(
name|Observer
name|observer
parameter_list|)
block|{
name|this
operator|.
name|observer
operator|=
name|checkNotNull
argument_list|(
name|observer
argument_list|)
expr_stmt|;
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
name|NodeState
name|before
init|=
name|root
decl_stmt|;
name|root
operator|=
name|getRootState
argument_list|(
name|revision
argument_list|)
expr_stmt|;
name|observer
operator|.
name|contentChanged
argument_list|(
name|this
argument_list|,
name|before
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
return|return
name|root
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeStoreBranch
name|branch
parameter_list|()
block|{
return|return
operator|new
name|KernelNodeStoreBranch
argument_list|(
name|this
argument_list|,
name|getRoot
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CoreValueFactory
name|getValueFactory
parameter_list|()
block|{
return|return
operator|new
name|CoreValueFactoryImpl
argument_list|(
name|kernel
argument_list|)
return|;
block|}
comment|//-----------------------------------------------------------< internal>---
annotation|@
name|Nonnull
name|MicroKernel
name|getKernel
parameter_list|()
block|{
return|return
name|kernel
return|;
block|}
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
block|}
end_class

end_unit

