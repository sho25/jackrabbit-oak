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
import|import static
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
operator|.
name|elements
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
name|commons
operator|.
name|PathUtils
operator|.
name|getName
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
name|commons
operator|.
name|PathUtils
operator|.
name|getParentPath
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
name|NodeStoreBranch
import|;
end_import

begin_comment
comment|/**  * {@code NodeStoreBranch} based on {@link MicroKernel} branching and merging.  * This implementation keeps changes in memory up to a certain limit and writes  * them back to the Microkernel branch when the limit is exceeded.  */
end_comment

begin_class
class|class
name|KernelNodeStoreBranch
implements|implements
name|NodeStoreBranch
block|{
comment|/** The underlying store to which this branch belongs */
specifier|private
specifier|final
name|KernelNodeStore
name|store
decl_stmt|;
comment|/** Lock for coordinating concurrent merge operations */
specifier|private
specifier|final
name|Lock
name|mergeLock
decl_stmt|;
comment|/**      * State of the this branch. Either {@link Unmodified}, {@link InMemory}, {@link Persisted}      * or {@link Merged}.      * @see BranchState      */
specifier|private
name|BranchState
name|branchState
decl_stmt|;
specifier|public
name|KernelNodeStoreBranch
parameter_list|(
name|KernelNodeStore
name|kernelNodeStore
parameter_list|,
name|Lock
name|mergeLock
parameter_list|,
name|KernelNodeState
name|base
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|checkNotNull
argument_list|(
name|kernelNodeStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|mergeLock
operator|=
name|checkNotNull
argument_list|(
name|mergeLock
argument_list|)
expr_stmt|;
name|branchState
operator|=
operator|new
name|Unmodified
argument_list|(
name|checkNotNull
argument_list|(
name|base
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|branchState
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|getBase
parameter_list|()
block|{
return|return
name|branchState
operator|.
name|getBase
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|getHead
parameter_list|()
block|{
return|return
name|branchState
operator|.
name|getHead
argument_list|()
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
name|branchState
operator|.
name|setRoot
argument_list|(
name|checkNotNull
argument_list|(
name|newRoot
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
if|if
condition|(
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|checkNotNull
argument_list|(
name|source
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|target
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|source
operator|.
name|equals
argument_list|(
name|target
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
name|getNode
argument_list|(
name|source
argument_list|)
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// source does not exist
return|return
literal|false
return|;
block|}
name|NodeState
name|destParent
init|=
name|getNode
argument_list|(
name|getParentPath
argument_list|(
name|target
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|destParent
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// parent of destination does not exist
return|return
literal|false
return|;
block|}
if|if
condition|(
name|destParent
operator|.
name|getChildNode
argument_list|(
name|getName
argument_list|(
name|target
argument_list|)
argument_list|)
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// destination exists already
return|return
literal|false
return|;
block|}
name|branchState
operator|.
name|persist
argument_list|()
operator|.
name|commit
argument_list|(
literal|">\""
operator|+
name|source
operator|+
literal|"\":\""
operator|+
name|target
operator|+
literal|'"'
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
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
if|if
condition|(
operator|!
name|getNode
argument_list|(
name|checkNotNull
argument_list|(
name|source
argument_list|)
argument_list|)
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// source does not exist
return|return
literal|false
return|;
block|}
name|NodeState
name|destParent
init|=
name|getNode
argument_list|(
name|getParentPath
argument_list|(
name|checkNotNull
argument_list|(
name|target
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|destParent
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// parent of destination does not exist
return|return
literal|false
return|;
block|}
if|if
condition|(
name|destParent
operator|.
name|getChildNode
argument_list|(
name|getName
argument_list|(
name|target
argument_list|)
argument_list|)
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// destination exists already
return|return
literal|false
return|;
block|}
name|branchState
operator|.
name|persist
argument_list|()
operator|.
name|commit
argument_list|(
literal|"*\""
operator|+
name|source
operator|+
literal|"\":\""
operator|+
name|target
operator|+
literal|'"'
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|merge
parameter_list|(
annotation|@
name|Nonnull
name|CommitHook
name|hook
parameter_list|,
name|PostCommitHook
name|committed
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
name|branchState
operator|.
name|merge
argument_list|(
name|checkNotNull
argument_list|(
name|hook
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|committed
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|rebase
parameter_list|()
block|{
name|branchState
operator|.
name|rebase
argument_list|()
expr_stmt|;
block|}
specifier|private
name|NodeState
name|getNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|NodeState
name|node
init|=
name|getHead
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
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
name|node
return|;
block|}
comment|/**      * Sub classes of this class represent a state a branch can be in. See the individual      * sub classes for permissible state transitions.      */
specifier|private
specifier|abstract
class|class
name|BranchState
block|{
comment|/** Root state of the base revision of this branch */
specifier|protected
name|KernelNodeState
name|base
decl_stmt|;
specifier|protected
name|BranchState
parameter_list|(
name|KernelNodeState
name|base
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
comment|/**          * Persist this branch to an underlying branch in the {@code MicroKernel}.          */
name|Persisted
name|persist
parameter_list|()
block|{
name|branchState
operator|=
operator|new
name|Persisted
argument_list|(
name|base
argument_list|,
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|(
name|Persisted
operator|)
name|branchState
return|;
block|}
name|KernelNodeState
name|getBase
parameter_list|()
block|{
return|return
name|base
return|;
block|}
annotation|@
name|Nonnull
specifier|abstract
name|NodeState
name|getHead
parameter_list|()
function_decl|;
specifier|abstract
name|void
name|setRoot
parameter_list|(
name|NodeState
name|root
parameter_list|)
function_decl|;
specifier|abstract
name|void
name|rebase
parameter_list|()
function_decl|;
annotation|@
name|Nonnull
specifier|abstract
name|NodeState
name|merge
parameter_list|(
annotation|@
name|Nonnull
name|CommitHook
name|hook
parameter_list|,
name|PostCommitHook
name|committed
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
block|}
comment|/**      * Instances of this class represent a branch whose base and head are the same.      *<p>      * Transitions to:      *<ul>      *<li>{@link InMemory} on {@link #setRoot(NodeState)} if the new root differs      *         from the current base</li>.      *<li>{@link Merged} on {@link #merge(CommitHook, PostCommitHook)}</li>      *</ul>      */
specifier|private
class|class
name|Unmodified
extends|extends
name|BranchState
block|{
name|Unmodified
parameter_list|(
name|KernelNodeState
name|base
parameter_list|)
block|{
name|super
argument_list|(
name|base
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Unmodified["
operator|+
name|base
operator|+
literal|']'
return|;
block|}
annotation|@
name|Override
name|NodeState
name|getHead
parameter_list|()
block|{
return|return
name|base
return|;
block|}
annotation|@
name|Override
name|void
name|setRoot
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
if|if
condition|(
operator|!
name|base
operator|.
name|equals
argument_list|(
name|root
argument_list|)
condition|)
block|{
name|branchState
operator|=
operator|new
name|InMemory
argument_list|(
name|base
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
name|void
name|rebase
parameter_list|()
block|{
name|base
operator|=
name|store
operator|.
name|getRoot
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
name|branchState
operator|=
operator|new
name|Merged
argument_list|(
name|base
argument_list|)
expr_stmt|;
return|return
name|base
return|;
block|}
block|}
comment|/**      * Instances of this class represent a branch whose base and head differ.      * All changes are kept in memory.      *<p>      * Transitions to:      *<ul>      *<li>{@link Unmodified} on {@link #setRoot(NodeState)} if the new root is the same      *         as the base of this branch or      *<li>{@link Persisted} otherwise.      *<li>{@link Merged} on {@link #merge(CommitHook, PostCommitHook)}</li>      *</ul>      */
specifier|private
class|class
name|InMemory
extends|extends
name|BranchState
block|{
comment|/** Root state of the transient head. */
specifier|private
name|NodeState
name|head
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"InMemory["
operator|+
name|base
operator|+
literal|", "
operator|+
name|head
operator|+
literal|']'
return|;
block|}
name|InMemory
parameter_list|(
name|KernelNodeState
name|base
parameter_list|,
name|NodeState
name|head
parameter_list|)
block|{
name|super
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|this
operator|.
name|head
operator|=
name|head
expr_stmt|;
block|}
annotation|@
name|Override
name|NodeState
name|getHead
parameter_list|()
block|{
return|return
name|head
return|;
block|}
annotation|@
name|Override
name|void
name|setRoot
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
if|if
condition|(
name|base
operator|.
name|equals
argument_list|(
name|root
argument_list|)
condition|)
block|{
name|branchState
operator|=
operator|new
name|Unmodified
argument_list|(
name|base
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|head
operator|.
name|equals
argument_list|(
name|root
argument_list|)
condition|)
block|{
name|head
operator|=
name|root
expr_stmt|;
name|persist
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
name|void
name|rebase
parameter_list|()
block|{
name|KernelNodeState
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
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
name|base
operator|=
name|root
expr_stmt|;
block|}
annotation|@
name|Override
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
name|mergeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|rebase
argument_list|()
expr_stmt|;
name|NodeState
name|toCommit
init|=
name|checkNotNull
argument_list|(
name|hook
argument_list|)
operator|.
name|processCommit
argument_list|(
name|base
argument_list|,
name|head
argument_list|)
decl_stmt|;
name|JsopDiff
name|diff
init|=
operator|new
name|JsopDiff
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|toCommit
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|NodeState
name|newHead
init|=
name|store
operator|.
name|commit
argument_list|(
name|diff
operator|.
name|toString
argument_list|()
argument_list|,
name|base
argument_list|)
decl_stmt|;
name|committed
operator|.
name|contentChanged
argument_list|(
name|base
argument_list|,
name|newHead
argument_list|)
expr_stmt|;
name|branchState
operator|=
operator|new
name|Merged
argument_list|(
name|base
argument_list|)
expr_stmt|;
return|return
name|newHead
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
name|CommitFailedException
argument_list|(
literal|"Kernel"
argument_list|,
literal|1
argument_list|,
literal|"Failed to merge changes to the underlying MicroKernel"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|mergeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Instances of this class represent a branch whose base and head differ.      * All changes are persisted to an underlying branch in the {@code MicroKernel}.      *<p>      * Transitions to:      *<ul>      *<li>{@link Unmodified} on {@link #setRoot(NodeState)} if the new root is the same      *         as the base of this branch.      *<li>{@link Merged} on {@link #merge(CommitHook, PostCommitHook)}</li>      *</ul>      */
specifier|private
class|class
name|Persisted
extends|extends
name|BranchState
block|{
comment|/** Root state of the transient head, top of persisted branch. */
specifier|private
name|KernelNodeState
name|head
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Persisted["
operator|+
name|base
operator|+
literal|", "
operator|+
name|head
operator|+
literal|']'
return|;
block|}
name|Persisted
parameter_list|(
name|KernelNodeState
name|base
parameter_list|,
name|NodeState
name|head
parameter_list|)
block|{
name|super
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|this
operator|.
name|head
operator|=
name|store
operator|.
name|branch
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|persistTransientHead
argument_list|(
name|head
argument_list|)
expr_stmt|;
block|}
name|void
name|commit
parameter_list|(
name|String
name|jsop
parameter_list|)
block|{
if|if
condition|(
operator|!
name|jsop
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|head
operator|=
name|store
operator|.
name|commit
argument_list|(
name|jsop
argument_list|,
name|head
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
name|NodeState
name|getHead
parameter_list|()
block|{
return|return
name|head
return|;
block|}
annotation|@
name|Override
name|void
name|setRoot
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
if|if
condition|(
name|base
operator|.
name|equals
argument_list|(
name|root
argument_list|)
condition|)
block|{
name|branchState
operator|=
operator|new
name|Unmodified
argument_list|(
name|base
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|head
operator|.
name|equals
argument_list|(
name|root
argument_list|)
condition|)
block|{
name|persistTransientHead
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
name|void
name|rebase
parameter_list|()
block|{
name|KernelNodeState
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
if|if
condition|(
name|head
operator|.
name|equals
argument_list|(
name|root
argument_list|)
condition|)
block|{
comment|// Nothing was written to this branch: set new base revision
name|head
operator|=
name|root
expr_stmt|;
name|base
operator|=
name|root
expr_stmt|;
block|}
else|else
block|{
comment|// perform rebase in kernel
name|head
operator|=
name|store
operator|.
name|rebase
argument_list|(
name|head
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|base
operator|=
name|root
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|mergeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|rebase
argument_list|()
expr_stmt|;
name|NodeState
name|toCommit
init|=
name|checkNotNull
argument_list|(
name|hook
argument_list|)
operator|.
name|processCommit
argument_list|(
name|base
argument_list|,
name|head
argument_list|)
decl_stmt|;
if|if
condition|(
name|toCommit
operator|.
name|equals
argument_list|(
name|base
argument_list|)
condition|)
block|{
name|committed
operator|.
name|contentChanged
argument_list|(
name|base
argument_list|,
name|base
argument_list|)
expr_stmt|;
name|branchState
operator|=
operator|new
name|Merged
argument_list|(
name|base
argument_list|)
expr_stmt|;
return|return
name|base
return|;
block|}
else|else
block|{
name|JsopDiff
name|diff
init|=
operator|new
name|JsopDiff
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|toCommit
operator|.
name|compareAgainstBaseState
argument_list|(
name|head
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|commit
argument_list|(
name|diff
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|NodeState
name|newRoot
init|=
name|store
operator|.
name|merge
argument_list|(
name|head
argument_list|)
decl_stmt|;
name|committed
operator|.
name|contentChanged
argument_list|(
name|base
argument_list|,
name|newRoot
argument_list|)
expr_stmt|;
name|branchState
operator|=
operator|new
name|Merged
argument_list|(
name|base
argument_list|)
expr_stmt|;
return|return
name|newRoot
return|;
block|}
block|}
catch|catch
parameter_list|(
name|MicroKernelException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Kernel"
argument_list|,
literal|1
argument_list|,
literal|"Failed to merge changes to the underlying MicroKernel"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|mergeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|persistTransientHead
parameter_list|(
name|NodeState
name|newHead
parameter_list|)
block|{
if|if
condition|(
operator|!
name|newHead
operator|.
name|equals
argument_list|(
name|head
argument_list|)
condition|)
block|{
name|JsopDiff
name|diff
init|=
operator|new
name|JsopDiff
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|newHead
operator|.
name|compareAgainstBaseState
argument_list|(
name|head
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|head
operator|=
name|store
operator|.
name|commit
argument_list|(
name|diff
operator|.
name|toString
argument_list|()
argument_list|,
name|head
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Instances of this class represent a branch that has already been merged.      * All methods throw an {@code IllegalStateException}.      *<p>      * Transitions to: none.      */
specifier|private
class|class
name|Merged
extends|extends
name|BranchState
block|{
specifier|protected
name|Merged
parameter_list|(
name|KernelNodeState
name|base
parameter_list|)
block|{
name|super
argument_list|(
name|base
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Merged["
operator|+
name|base
operator|+
literal|']'
return|;
block|}
annotation|@
name|Override
name|NodeState
name|getHead
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Branch has already been merged"
argument_list|)
throw|;
block|}
annotation|@
name|Override
name|void
name|setRoot
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Branch has already been merged"
argument_list|)
throw|;
block|}
annotation|@
name|Override
name|void
name|rebase
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Branch has already been merged"
argument_list|)
throw|;
block|}
annotation|@
name|Override
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
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Branch has already been merged"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

