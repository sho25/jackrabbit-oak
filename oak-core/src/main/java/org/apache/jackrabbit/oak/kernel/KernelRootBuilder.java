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
name|checkNotNull
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
name|PostCommitHook
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

begin_comment
comment|/**  * This implementation tracks the number of pending changes and purges them to  * a private branch of the underlying store if a certain threshold is met.  */
end_comment

begin_class
class|class
name|KernelRootBuilder
extends|extends
name|MemoryNodeBuilder
block|{
comment|/**      * Number of content updates that need to happen before the updates      * are automatically purged to the private branch.      */
specifier|private
specifier|static
specifier|final
name|int
name|UPDATE_LIMIT
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"update.limit"
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
comment|/**      * The underlying store      */
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
comment|/**      * The base state of this builder, possibly non-existent if this builder      * represents a new node that didn't yet exist in the base content tree.      * This differs from the base state of super since the latter one reflects      * the base created by the last purge.      */
specifier|private
name|NodeState
name|base
decl_stmt|;
comment|/**      * Private branch used to hold pending changes exceeding {@link #UPDATE_LIMIT}      */
specifier|private
name|NodeStoreBranch
name|branch
decl_stmt|;
comment|/**      * Number of updated not yet persisted to the private {@link #branch}      */
specifier|private
name|int
name|updates
init|=
literal|0
decl_stmt|;
name|KernelRootBuilder
parameter_list|(
name|KernelNodeState
name|base
parameter_list|,
name|KernelNodeStore
name|store
parameter_list|)
block|{
name|super
argument_list|(
name|checkNotNull
argument_list|(
name|base
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|branch
operator|=
name|store
operator|.
name|createBranch
argument_list|(
name|base
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------< MemoryNodeBuilder>---
annotation|@
name|Override
specifier|public
name|NodeState
name|getBaseState
parameter_list|()
block|{
return|return
name|base
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|(
name|NodeState
name|newBase
parameter_list|)
block|{
name|base
operator|=
name|newBase
expr_stmt|;
name|super
operator|.
name|reset
argument_list|(
name|newBase
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|MemoryNodeBuilder
name|createChildBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|KernelNodeBuilder
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|updated
parameter_list|()
block|{
if|if
condition|(
name|updates
operator|++
operator|>
name|UPDATE_LIMIT
condition|)
block|{
name|purge
argument_list|()
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< internal>---
comment|/**      * Rebase this builder on top of the head of the underlying store      */
name|NodeState
name|rebase
parameter_list|()
block|{
name|purge
argument_list|()
expr_stmt|;
name|branch
operator|.
name|rebase
argument_list|()
expr_stmt|;
name|NodeState
name|head
init|=
name|branch
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|reset
argument_list|(
name|head
argument_list|)
expr_stmt|;
return|return
name|head
return|;
block|}
comment|/**      * Reset this builder by creating a new branch and setting the head      * state of that branch as the new base state of this builder.      */
name|NodeState
name|reset
parameter_list|()
block|{
name|branch
operator|=
name|store
operator|.
name|branch
argument_list|()
expr_stmt|;
name|NodeState
name|head
init|=
name|branch
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|reset
argument_list|(
name|head
argument_list|)
expr_stmt|;
return|return
name|head
return|;
block|}
comment|/**      * Merge all changes tracked in this builder into the underlying store.      */
name|NodeState
name|merge
parameter_list|(
name|CommitHook
name|hook
parameter_list|,
name|PostCommitHook
name|committed
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|purge
argument_list|()
expr_stmt|;
name|branch
operator|.
name|merge
argument_list|(
name|hook
argument_list|,
name|committed
argument_list|)
expr_stmt|;
return|return
name|reset
argument_list|()
return|;
block|}
comment|/**      * Applied all pending changes to the underlying branch and then      * move the node as a separate operation on the underlying store.      * This allows stores to optimise move operations instead of      * seeing them as an added node followed by a deleted node.      */
name|boolean
name|move
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|target
parameter_list|)
block|{
name|purge
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
name|branch
operator|.
name|move
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
decl_stmt|;
name|super
operator|.
name|reset
argument_list|(
name|branch
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|success
return|;
block|}
comment|/**      * Applied all pending changes to the underlying branch and then      * copy the node as a separate operation on the underlying store.      * This allows stores to optimise copy operations instead of      * seeing them as an added node.      */
name|boolean
name|copy
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|target
parameter_list|)
block|{
name|purge
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
name|branch
operator|.
name|copy
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
decl_stmt|;
name|super
operator|.
name|reset
argument_list|(
name|branch
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|success
return|;
block|}
specifier|private
name|void
name|purge
parameter_list|()
block|{
name|branch
operator|.
name|setRoot
argument_list|(
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|reset
argument_list|(
name|branch
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
name|updates
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

