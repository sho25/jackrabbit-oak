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
name|segment
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MILLISECONDS
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|NANOSECONDS
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
name|segment
operator|.
name|SegmentNodeStore
operator|.
name|ROOT
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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

begin_class
class|class
name|SegmentNodeStoreBranch
extends|extends
name|AbstractNodeStoreBranch
block|{
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SegmentNodeStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|SegmentWriter
name|writer
decl_stmt|;
specifier|private
name|SegmentNodeState
name|base
decl_stmt|;
specifier|private
name|SegmentNodeState
name|head
decl_stmt|;
specifier|private
name|long
name|maximumBackoff
decl_stmt|;
name|SegmentNodeStoreBranch
parameter_list|(
name|SegmentNodeStore
name|store
parameter_list|,
name|SegmentWriter
name|writer
parameter_list|,
name|SegmentNodeState
name|base
parameter_list|,
name|long
name|maximumBackoff
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
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|this
operator|.
name|head
operator|=
name|base
expr_stmt|;
name|this
operator|.
name|maximumBackoff
operator|=
name|maximumBackoff
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getBase
parameter_list|()
block|{
return|return
name|base
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
specifier|synchronized
name|NodeState
name|getHead
parameter_list|()
block|{
return|return
name|head
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|setRoot
parameter_list|(
name|NodeState
name|newRoot
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|head
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
name|ROOT
argument_list|,
name|newRoot
argument_list|)
expr_stmt|;
name|head
operator|=
name|writer
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|rebase
parameter_list|()
block|{
name|SegmentNodeState
name|newBase
init|=
name|store
operator|.
name|getHead
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|base
operator|.
name|getRecordId
argument_list|()
operator|.
name|equals
argument_list|(
name|newBase
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
name|NodeBuilder
name|builder
init|=
name|newBase
operator|.
name|builder
argument_list|()
decl_stmt|;
name|head
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|,
operator|new
name|ConflictAnnotatingRebaseDiff
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|ROOT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|base
operator|=
name|newBase
expr_stmt|;
name|head
operator|=
name|writer
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|synchronized
name|long
name|optimisticMerge
parameter_list|(
name|CommitHook
name|hook
parameter_list|,
name|PostCommitHook
name|committed
parameter_list|)
throws|throws
name|CommitFailedException
throws|,
name|InterruptedException
block|{
name|long
name|timeout
init|=
literal|1
decl_stmt|;
name|SegmentNodeState
name|originalBase
init|=
name|base
decl_stmt|;
name|SegmentNodeState
name|originalHead
init|=
name|head
decl_stmt|;
comment|// use exponential backoff in case of concurrent commits
for|for
control|(
name|long
name|backoff
init|=
literal|1
init|;
name|backoff
operator|<
name|maximumBackoff
condition|;
name|backoff
operator|*=
literal|2
control|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
comment|// apply commit hooks on the rebased changes
name|NodeBuilder
name|builder
init|=
name|head
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
name|ROOT
argument_list|,
name|hook
operator|.
name|processCommit
argument_list|(
name|base
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|,
name|head
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|newHead
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// use optimistic locking to update the journal
if|if
condition|(
name|base
operator|.
name|hasProperty
argument_list|(
literal|"token"
argument_list|)
operator|&&
name|base
operator|.
name|getLong
argument_list|(
literal|"timeout"
argument_list|)
operator|>=
name|System
operator|.
name|currentTimeMillis
argument_list|()
condition|)
block|{
comment|// someone else has a pessimistic lock on the journal,
comment|// so we should not try to commit anything
block|}
elseif|else
if|if
condition|(
name|store
operator|.
name|setHead
argument_list|(
name|base
argument_list|,
name|newHead
argument_list|)
condition|)
block|{
name|NodeState
name|previousBase
init|=
name|base
decl_stmt|;
name|base
operator|=
name|newHead
expr_stmt|;
name|head
operator|=
name|newHead
expr_stmt|;
name|committed
operator|.
name|contentChanged
argument_list|(
name|previousBase
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|,
name|newHead
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
comment|// someone else was faster, so restore state and retry later
name|base
operator|=
name|originalBase
expr_stmt|;
name|head
operator|=
name|originalHead
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|backoff
argument_list|,
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|1000000
argument_list|)
argument_list|)
expr_stmt|;
comment|// rebase to latest head before trying again
name|rebase
argument_list|()
expr_stmt|;
name|long
name|stop
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|stop
operator|-
name|start
operator|>
name|timeout
condition|)
block|{
name|timeout
operator|=
name|stop
operator|-
name|start
expr_stmt|;
block|}
block|}
return|return
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|timeout
argument_list|,
name|NANOSECONDS
argument_list|)
return|;
block|}
specifier|private
specifier|synchronized
name|void
name|pessimisticMerge
parameter_list|(
name|CommitHook
name|hook
parameter_list|,
name|PostCommitHook
name|committed
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|CommitFailedException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|SegmentNodeState
name|before
init|=
name|store
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|before
operator|.
name|hasProperty
argument_list|(
literal|"token"
argument_list|)
operator|&&
name|before
operator|.
name|getLong
argument_list|(
literal|"timeout"
argument_list|)
operator|>=
name|now
condition|)
block|{
comment|// locked by someone else, wait until unlocked or expired
comment|// TODO: explicit sleep needed to avoid spinning?
block|}
else|else
block|{
comment|// attempt to acquire the lock
name|NodeBuilder
name|builder
init|=
name|before
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"token"
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"timeout"
argument_list|,
name|now
operator|+
name|timeout
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|after
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|store
operator|.
name|setHead
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
condition|)
block|{
name|SegmentNodeState
name|originalBase
init|=
name|base
decl_stmt|;
name|SegmentNodeState
name|originalHead
init|=
name|head
decl_stmt|;
comment|// lock acquired; rebase, apply commit hooks, and unlock
name|rebase
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
name|ROOT
argument_list|,
name|hook
operator|.
name|processCommit
argument_list|(
name|base
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|,
name|head
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|removeProperty
argument_list|(
literal|"token"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|removeProperty
argument_list|(
literal|"timeout"
argument_list|)
expr_stmt|;
comment|// complete the commit
name|SegmentNodeState
name|newHead
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|store
operator|.
name|setHead
argument_list|(
name|after
argument_list|,
name|newHead
argument_list|)
condition|)
block|{
name|NodeState
name|previousBase
init|=
name|base
decl_stmt|;
name|base
operator|=
name|newHead
expr_stmt|;
name|head
operator|=
name|newHead
expr_stmt|;
name|committed
operator|.
name|contentChanged
argument_list|(
name|previousBase
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|,
name|newHead
operator|.
name|getChildNode
argument_list|(
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
comment|// something else happened, perhaps a timeout, so
comment|// undo the previous rebase and try again
name|base
operator|=
name|originalBase
expr_stmt|;
name|head
operator|=
name|originalHead
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
specifier|synchronized
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
if|if
condition|(
name|base
operator|!=
name|head
condition|)
block|{
try|try
block|{
name|long
name|timeout
init|=
name|optimisticMerge
argument_list|(
name|hook
argument_list|,
name|committed
argument_list|)
decl_stmt|;
if|if
condition|(
name|timeout
operator|>=
literal|0
condition|)
block|{
name|pessimisticMerge
argument_list|(
name|hook
argument_list|,
name|committed
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Segment"
argument_list|,
literal|1
argument_list|,
literal|"Commit interrupted"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|getHead
argument_list|()
return|;
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
name|source
argument_list|,
name|target
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
name|NodeBuilder
name|builder
init|=
name|getHead
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|targetBuilder
init|=
name|builder
decl_stmt|;
name|String
name|targetParent
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|target
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|targetParent
argument_list|)
control|)
block|{
if|if
condition|(
name|targetBuilder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|targetBuilder
operator|=
name|targetBuilder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
name|String
name|targetName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetBuilder
operator|.
name|hasChildNode
argument_list|(
name|targetName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NodeBuilder
name|sourceBuilder
init|=
name|builder
decl_stmt|;
name|String
name|sourceParent
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|source
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|sourceParent
argument_list|)
control|)
block|{
if|if
condition|(
name|sourceBuilder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|sourceBuilder
operator|=
name|sourceBuilder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
name|String
name|sourceName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|sourceBuilder
operator|.
name|hasChildNode
argument_list|(
name|sourceName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NodeState
name|sourceState
init|=
name|sourceBuilder
operator|.
name|child
argument_list|(
name|sourceName
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|targetBuilder
operator|.
name|setChildNode
argument_list|(
name|targetName
argument_list|,
name|sourceState
argument_list|)
expr_stmt|;
name|sourceBuilder
operator|.
name|getChildNode
argument_list|(
name|sourceName
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|setRoot
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
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
name|NodeBuilder
name|builder
init|=
name|getHead
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|targetBuilder
init|=
name|builder
decl_stmt|;
name|String
name|targetParent
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|target
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|targetParent
argument_list|)
control|)
block|{
if|if
condition|(
name|targetBuilder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|targetBuilder
operator|=
name|targetBuilder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
name|String
name|targetName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetBuilder
operator|.
name|hasChildNode
argument_list|(
name|targetName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NodeBuilder
name|sourceBuilder
init|=
name|builder
decl_stmt|;
name|String
name|sourceParent
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|source
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|sourceParent
argument_list|)
control|)
block|{
if|if
condition|(
name|sourceBuilder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|sourceBuilder
operator|=
name|sourceBuilder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
name|String
name|sourceName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|sourceBuilder
operator|.
name|hasChildNode
argument_list|(
name|sourceName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NodeState
name|sourceState
init|=
name|sourceBuilder
operator|.
name|child
argument_list|(
name|sourceName
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|targetBuilder
operator|.
name|setChildNode
argument_list|(
name|targetName
argument_list|,
name|sourceState
argument_list|)
expr_stmt|;
name|setRoot
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

