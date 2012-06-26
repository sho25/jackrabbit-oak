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
name|plugins
operator|.
name|memory
operator|.
name|MemoryNodeStore
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
name|CommitEditor
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
name|NodeStoreBranch
import|;
end_import

begin_comment
comment|/**  * {@code NodeStore} implementations against {@link MicroKernel}.  */
end_comment

begin_class
specifier|public
class|class
name|KernelNodeStore
extends|extends
name|MemoryNodeStore
block|{
comment|/**      * The {@link MicroKernel} instance used to store the content tree.      */
specifier|private
specifier|final
name|MicroKernel
name|kernel
decl_stmt|;
comment|/**      * Commit hook.      */
specifier|private
specifier|final
name|CommitEditor
name|commitHook
decl_stmt|;
comment|/**      * Value factory backed by the {@link #kernel} instance.      */
specifier|private
specifier|final
name|CoreValueFactory
name|valueFactory
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
parameter_list|,
name|CommitEditor
name|commitHook
parameter_list|)
block|{
assert|assert
name|kernel
operator|!=
literal|null
assert|;
assert|assert
name|commitHook
operator|!=
literal|null
assert|;
name|this
operator|.
name|kernel
operator|=
name|kernel
expr_stmt|;
name|this
operator|.
name|commitHook
operator|=
name|commitHook
expr_stmt|;
name|this
operator|.
name|valueFactory
operator|=
operator|new
name|CoreValueFactoryImpl
argument_list|(
name|kernel
argument_list|)
expr_stmt|;
name|this
operator|.
name|root
operator|=
operator|new
name|KernelNodeState
argument_list|(
name|kernel
argument_list|,
name|valueFactory
argument_list|,
literal|"/"
argument_list|,
name|kernel
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|NodeState
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
operator|new
name|KernelNodeState
argument_list|(
name|kernel
argument_list|,
name|valueFactory
argument_list|,
literal|"/"
argument_list|,
name|kernel
operator|.
name|getHeadRevision
argument_list|()
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
name|valueFactory
return|;
block|}
comment|//------------------------------------------------------------< internal>---
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
annotation|@
name|Nonnull
name|CommitEditor
name|getCommitHook
parameter_list|()
block|{
return|return
name|commitHook
return|;
block|}
block|}
end_class

end_unit

