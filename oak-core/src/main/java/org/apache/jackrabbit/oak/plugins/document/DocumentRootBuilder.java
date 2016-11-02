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
name|plugins
operator|.
name|document
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
name|NodeState
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

begin_comment
comment|/**  * This implementation tracks the number of pending changes and purges them to  * a private branch of the underlying store if a certain threshold is met.  */
end_comment

begin_class
class|class
name|DocumentRootBuilder
extends|extends
name|AbstractDocumentNodeBuilder
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DocumentRootBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Number of content updates that need to happen before the updates      * are automatically purged to the private branch.      */
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
literal|100000
argument_list|)
decl_stmt|;
static|static
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Update limit set to {}"
argument_list|,
name|UPDATE_LIMIT
argument_list|)
expr_stmt|;
block|}
comment|/**      * The underlying store      */
specifier|protected
specifier|final
name|DocumentNodeStore
name|store
decl_stmt|;
comment|/**      * The base state of this builder, possibly non-existent if this builder      * represents a new node that didn't yet exist in the base content tree.      * This differs from the base state of super since the latter one reflects      * the base created by the last purge.      */
annotation|@
name|Nonnull
specifier|private
name|NodeState
name|base
decl_stmt|;
comment|/**      * Private branch used to hold pending changes exceeding {@link #UPDATE_LIMIT}      */
specifier|private
name|DocumentNodeStoreBranch
name|branch
decl_stmt|;
comment|/**      * Number of updated not yet persisted to the private {@link #branch}      */
specifier|private
name|int
name|updates
decl_stmt|;
name|DocumentRootBuilder
parameter_list|(
annotation|@
name|Nonnull
name|DocumentNodeState
name|base
parameter_list|,
annotation|@
name|Nonnull
name|DocumentNodeStore
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
name|store
operator|=
name|checkNotNull
argument_list|(
name|store
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
annotation|@
name|Nonnull
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
annotation|@
name|Nonnull
name|NodeState
name|newBase
parameter_list|)
block|{
name|base
operator|=
name|checkNotNull
argument_list|(
name|newBase
argument_list|)
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
name|DocumentNodeBuilder
name|createChildBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|DocumentNodeBuilder
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
operator|++
name|updates
operator|>
name|UPDATE_LIMIT
condition|)
block|{
name|purge
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|getNodeState
parameter_list|()
block|{
if|if
condition|(
name|DocumentNodeStoreBranch
operator|.
name|getCurrentBranch
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|purge
argument_list|()
expr_stmt|;
return|return
name|branch
operator|.
name|getHead
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getNodeState
argument_list|()
return|;
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
name|store
operator|.
name|createBlob
argument_list|(
name|stream
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< internal>---
comment|/**      * Rebase this builder on top of the head of the underlying store      */
name|NodeState
name|rebase
parameter_list|()
block|{
name|NodeState
name|head
init|=
name|super
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|inMemBase
init|=
name|super
operator|.
name|getBaseState
argument_list|()
decl_stmt|;
comment|// Rebase branch
name|branch
operator|.
name|rebase
argument_list|()
expr_stmt|;
comment|// Rebase in memory changes on top of the head of the rebased branch
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
name|head
operator|.
name|compareAgainstBaseState
argument_list|(
name|inMemBase
argument_list|,
operator|new
name|ConflictAnnotatingRebaseDiff
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set new base and return rebased head
name|base
operator|=
name|branch
operator|.
name|getBase
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|getNodeState
argument_list|()
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
name|createBranch
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|)
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
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|purge
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|branch
operator|.
name|merge
argument_list|(
name|hook
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
comment|// need to adjust base and head of this builder
comment|// in case branch.merge() did a rebase and then
comment|// a commit hook failed the merge
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
name|this
operator|.
name|base
operator|=
name|branch
operator|.
name|getBase
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|reset
argument_list|()
return|;
block|}
name|void
name|purge
parameter_list|()
block|{
name|branch
operator|.
name|setRoot
argument_list|(
name|super
operator|.
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

