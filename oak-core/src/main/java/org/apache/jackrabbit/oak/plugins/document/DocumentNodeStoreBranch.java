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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
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
name|state
operator|.
name|AbstractNodeStoreBranch
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
operator|.
name|MERGE
import|;
end_import

begin_comment
comment|/**  * Implementation of a DocumentMK based node store branch.  */
end_comment

begin_class
specifier|public
class|class
name|DocumentNodeStoreBranch
extends|extends
name|AbstractNodeStoreBranch
argument_list|<
name|DocumentNodeStore
argument_list|,
name|DocumentNodeState
argument_list|>
block|{
comment|/** Lock for coordinating concurrent merge operations */
specifier|private
specifier|final
name|ReadWriteLock
name|mergeLock
decl_stmt|;
specifier|public
name|DocumentNodeStoreBranch
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|,
name|DocumentNodeState
name|base
parameter_list|,
name|ReadWriteLock
name|mergeLock
parameter_list|)
block|{
comment|// maximum back off is twice the async delay, but at least 2 seconds.
name|super
argument_list|(
name|store
argument_list|,
operator|new
name|ChangeDispatcher
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|)
argument_list|,
name|mergeLock
operator|.
name|readLock
argument_list|()
argument_list|,
name|base
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|store
operator|.
name|getAsyncDelay
argument_list|()
argument_list|,
literal|1000
argument_list|)
operator|*
literal|2
argument_list|)
expr_stmt|;
name|this
operator|.
name|mergeLock
operator|=
name|mergeLock
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|DocumentNodeState
name|getRoot
parameter_list|()
block|{
return|return
name|store
operator|.
name|getRoot
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|DocumentNodeState
name|createBranch
parameter_list|(
name|DocumentNodeState
name|state
parameter_list|)
block|{
return|return
name|store
operator|.
name|getRoot
argument_list|(
name|state
operator|.
name|getRevision
argument_list|()
operator|.
name|asBranchRevision
argument_list|()
argument_list|)
operator|.
name|setBranch
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|DocumentNodeState
name|rebase
parameter_list|(
name|DocumentNodeState
name|branchHead
parameter_list|,
name|DocumentNodeState
name|base
parameter_list|)
block|{
return|return
name|store
operator|.
name|getRoot
argument_list|(
name|store
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
annotation|@
name|Override
specifier|protected
name|DocumentNodeState
name|merge
parameter_list|(
name|DocumentNodeState
name|branchHead
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
name|store
operator|.
name|getRoot
argument_list|(
name|store
operator|.
name|merge
argument_list|(
name|branchHead
operator|.
name|getRevision
argument_list|()
argument_list|,
name|info
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|protected
name|DocumentNodeState
name|reset
parameter_list|(
annotation|@
name|Nonnull
name|DocumentNodeState
name|branchHead
parameter_list|,
annotation|@
name|Nonnull
name|DocumentNodeState
name|ancestor
parameter_list|)
block|{
return|return
name|store
operator|.
name|getRoot
argument_list|(
name|store
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
operator|.
name|setBranch
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|DocumentNodeState
name|persist
parameter_list|(
specifier|final
name|NodeState
name|toPersist
parameter_list|,
specifier|final
name|DocumentNodeState
name|base
parameter_list|,
specifier|final
name|CommitInfo
name|info
parameter_list|)
block|{
name|DocumentNodeState
name|state
init|=
name|persist
argument_list|(
operator|new
name|Changes
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|with
parameter_list|(
name|Commit
name|c
parameter_list|)
block|{
name|toPersist
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
operator|new
name|CommitDiff
argument_list|(
name|store
argument_list|,
name|c
argument_list|,
name|store
operator|.
name|getBlobSerializer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|base
argument_list|,
name|info
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
name|state
operator|.
name|setBranch
argument_list|()
expr_stmt|;
block|}
return|return
name|state
return|;
block|}
annotation|@
name|Override
specifier|protected
name|DocumentNodeState
name|copy
parameter_list|(
specifier|final
name|String
name|source
parameter_list|,
specifier|final
name|String
name|target
parameter_list|,
name|DocumentNodeState
name|base
parameter_list|)
block|{
specifier|final
name|DocumentNodeState
name|src
init|=
name|store
operator|.
name|getNode
argument_list|(
name|source
argument_list|,
name|base
operator|.
name|getRevision
argument_list|()
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|src
operator|!=
literal|null
argument_list|,
literal|"Source node %s@%s does not exist"
argument_list|,
name|source
argument_list|,
name|base
operator|.
name|getRevision
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|persist
argument_list|(
operator|new
name|Changes
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|with
parameter_list|(
name|Commit
name|c
parameter_list|)
block|{
name|store
operator|.
name|copyNode
argument_list|(
name|src
argument_list|,
name|target
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|base
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|DocumentNodeState
name|move
parameter_list|(
specifier|final
name|String
name|source
parameter_list|,
specifier|final
name|String
name|target
parameter_list|,
name|DocumentNodeState
name|base
parameter_list|)
block|{
specifier|final
name|DocumentNodeState
name|src
init|=
name|store
operator|.
name|getNode
argument_list|(
name|source
argument_list|,
name|base
operator|.
name|getRevision
argument_list|()
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|src
operator|!=
literal|null
argument_list|,
literal|"Source node %s@%s does not exist"
argument_list|,
name|source
argument_list|,
name|base
operator|.
name|getRevision
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|persist
argument_list|(
operator|new
name|Changes
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|with
parameter_list|(
name|Commit
name|c
parameter_list|)
block|{
name|store
operator|.
name|moveNode
argument_list|(
name|src
argument_list|,
name|target
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|base
argument_list|,
literal|null
argument_list|)
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
annotation|@
name|Nonnull
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
try|try
block|{
return|return
name|super
operator|.
name|merge
argument_list|(
name|hook
argument_list|,
name|info
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|isOfType
argument_list|(
name|MERGE
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
comment|// retry with exclusive lock, blocking other
comment|// concurrent writes
name|mergeLock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|super
operator|.
name|merge
argument_list|(
name|hook
argument_list|,
name|info
argument_list|)
return|;
block|}
finally|finally
block|{
name|mergeLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|//------------------------------< internal>--------------------------------
comment|/**      * Persist some changes on top of the given base state.      *      * @param op the changes to persist.      * @param base the base state.      * @param info the commit info.      * @return the result state.      */
specifier|private
name|DocumentNodeState
name|persist
parameter_list|(
name|Changes
name|op
parameter_list|,
name|DocumentNodeState
name|base
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|Commit
name|c
init|=
name|store
operator|.
name|newCommit
argument_list|(
name|base
operator|.
name|getRevision
argument_list|()
argument_list|)
decl_stmt|;
name|Revision
name|rev
decl_stmt|;
try|try
block|{
name|op
operator|.
name|with
argument_list|(
name|c
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// no changes to persist. return base state and let
comment|// finally clause cancel the commit
return|return
name|base
return|;
block|}
name|rev
operator|=
name|c
operator|.
name|apply
argument_list|()
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
name|success
condition|)
block|{
name|store
operator|.
name|done
argument_list|(
name|c
argument_list|,
name|base
operator|.
name|getRevision
argument_list|()
operator|.
name|isBranch
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|store
operator|.
name|canceled
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|store
operator|.
name|getRoot
argument_list|(
name|rev
argument_list|)
return|;
block|}
specifier|private
interface|interface
name|Changes
block|{
name|void
name|with
parameter_list|(
name|Commit
name|c
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

