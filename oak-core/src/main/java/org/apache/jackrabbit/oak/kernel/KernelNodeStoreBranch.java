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
name|api
operator|.
name|PropertyState
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
name|state
operator|.
name|ChildNodeEntry
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
name|NodeStateDiff
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

begin_comment
comment|/**  * {@code NodeStoreBranch} based on {@link MicroKernel} branching and merging.  * This implementation keeps changes in memory up to a certain limit and writes  * them back when the to the Microkernel branch when the limit is exceeded.  */
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
comment|/** Base state of this branch */
specifier|private
specifier|final
name|NodeState
name|base
decl_stmt|;
comment|/** Revision of this branch in the Microkernel */
specifier|private
name|String
name|branchRevision
decl_stmt|;
comment|/** Current root state of this branch */
specifier|private
name|NodeState
name|currentRoot
decl_stmt|;
comment|/** Last state which was committed to this branch */
specifier|private
name|NodeState
name|committed
decl_stmt|;
name|KernelNodeStoreBranch
parameter_list|(
name|KernelNodeStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|MicroKernel
name|kernel
init|=
name|store
operator|.
name|getKernel
argument_list|()
decl_stmt|;
name|this
operator|.
name|branchRevision
operator|=
name|kernel
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentRoot
operator|=
operator|new
name|KernelNodeState
argument_list|(
name|kernel
argument_list|,
name|getValueFactory
argument_list|()
argument_list|,
literal|"/"
argument_list|,
name|branchRevision
argument_list|)
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|currentRoot
expr_stmt|;
name|this
operator|.
name|committed
operator|=
name|currentRoot
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getRoot
parameter_list|()
block|{
return|return
name|currentRoot
return|;
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
name|void
name|setRoot
parameter_list|(
name|NodeState
name|newRoot
parameter_list|)
block|{
name|currentRoot
operator|=
name|newRoot
expr_stmt|;
name|commit
argument_list|(
name|buildJsop
argument_list|()
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
name|getNode
argument_list|(
name|source
argument_list|)
operator|==
literal|null
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
name|destParent
operator|==
literal|null
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
operator|!=
literal|null
condition|)
block|{
comment|// destination exists already
return|return
literal|false
return|;
block|}
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
name|getNode
argument_list|(
name|source
argument_list|)
operator|==
literal|null
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
name|destParent
operator|==
literal|null
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
operator|!=
literal|null
condition|)
block|{
comment|// destination exists already
return|return
literal|false
return|;
block|}
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
name|Override
specifier|public
name|KernelNodeState
name|merge
parameter_list|()
throws|throws
name|CommitFailedException
block|{
comment|// TODO rebase to current trunk?
name|MicroKernel
name|kernel
init|=
name|store
operator|.
name|getKernel
argument_list|()
decl_stmt|;
name|CommitHook
name|commitHook
init|=
name|store
operator|.
name|getCommitHook
argument_list|()
decl_stmt|;
name|NodeState
name|oldRoot
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeState
name|toCommit
init|=
name|commitHook
operator|.
name|beforeCommit
argument_list|(
name|store
argument_list|,
name|oldRoot
argument_list|,
name|currentRoot
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|currentRoot
operator|.
name|equals
argument_list|(
name|toCommit
argument_list|)
condition|)
block|{
name|setRoot
argument_list|(
name|toCommit
argument_list|)
expr_stmt|;
name|oldRoot
operator|=
name|store
operator|.
name|getRoot
argument_list|()
expr_stmt|;
name|toCommit
operator|=
name|commitHook
operator|.
name|beforeCommit
argument_list|(
name|store
argument_list|,
name|oldRoot
argument_list|,
name|currentRoot
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|String
name|mergedRevision
init|=
name|kernel
operator|.
name|merge
argument_list|(
name|branchRevision
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|branchRevision
operator|=
literal|null
expr_stmt|;
name|currentRoot
operator|=
literal|null
expr_stmt|;
name|committed
operator|=
literal|null
expr_stmt|;
name|KernelNodeState
name|committed
init|=
operator|new
name|KernelNodeState
argument_list|(
name|kernel
argument_list|,
name|getValueFactory
argument_list|()
argument_list|,
literal|"/"
argument_list|,
name|mergedRevision
argument_list|)
decl_stmt|;
name|commitHook
operator|.
name|afterCommit
argument_list|(
name|store
argument_list|,
name|oldRoot
argument_list|,
name|committed
argument_list|)
expr_stmt|;
return|return
name|committed
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
name|e
argument_list|)
throw|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|CoreValueFactory
name|getValueFactory
parameter_list|()
block|{
return|return
name|store
operator|.
name|getValueFactory
argument_list|()
return|;
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
name|getRoot
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
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
break|break;
block|}
block|}
return|return
name|node
return|;
block|}
specifier|private
name|void
name|commit
parameter_list|(
name|String
name|jsop
parameter_list|)
block|{
name|MicroKernel
name|kernel
init|=
name|store
operator|.
name|getKernel
argument_list|()
decl_stmt|;
name|branchRevision
operator|=
name|kernel
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|jsop
argument_list|,
name|branchRevision
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|currentRoot
operator|=
operator|new
name|KernelNodeState
argument_list|(
name|kernel
argument_list|,
name|getValueFactory
argument_list|()
argument_list|,
literal|"/"
argument_list|,
name|branchRevision
argument_list|)
expr_stmt|;
name|committed
operator|=
name|currentRoot
expr_stmt|;
block|}
specifier|private
name|String
name|buildJsop
parameter_list|()
block|{
name|StringBuilder
name|jsop
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|diffToJsop
argument_list|(
name|committed
argument_list|,
name|currentRoot
argument_list|,
literal|""
argument_list|,
name|jsop
argument_list|)
expr_stmt|;
return|return
name|jsop
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|diffToJsop
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|StringBuilder
name|jsop
parameter_list|)
block|{
name|store
operator|.
name|compare
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
operator|new
name|NodeStateDiff
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
name|jsop
operator|.
name|append
argument_list|(
literal|'^'
argument_list|)
operator|.
name|append
argument_list|(
name|buildPath
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
operator|.
name|append
argument_list|(
name|toJson
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
name|jsop
operator|.
name|append
argument_list|(
literal|'^'
argument_list|)
operator|.
name|append
argument_list|(
name|buildPath
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
operator|.
name|append
argument_list|(
name|toJson
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
name|jsop
operator|.
name|append
argument_list|(
literal|'^'
argument_list|)
operator|.
name|append
argument_list|(
name|buildPath
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|":null"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|jsop
operator|.
name|append
argument_list|(
literal|'+'
argument_list|)
operator|.
name|append
argument_list|(
name|buildPath
argument_list|(
name|name
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|toJson
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|jsop
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
operator|.
name|append
argument_list|(
name|buildPath
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|diffToJsop
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
argument_list|,
name|jsop
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|buildPath
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|'"'
operator|+
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
operator|+
literal|'"'
return|;
block|}
specifier|private
name|String
name|toJson
parameter_list|(
name|PropertyState
name|propertyState
parameter_list|)
block|{
return|return
name|propertyState
operator|.
name|isArray
argument_list|()
condition|?
name|CoreValueMapper
operator|.
name|toJsonArray
argument_list|(
name|propertyState
operator|.
name|getValues
argument_list|()
argument_list|)
else|:
name|CoreValueMapper
operator|.
name|toJsonValue
argument_list|(
name|propertyState
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|void
name|toJson
parameter_list|(
name|NodeState
name|nodeState
parameter_list|)
block|{
name|jsop
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|String
name|comma
init|=
literal|""
decl_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|nodeState
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|String
name|value
init|=
name|toJson
argument_list|(
name|property
argument_list|)
decl_stmt|;
name|jsop
operator|.
name|append
argument_list|(
name|comma
argument_list|)
expr_stmt|;
name|comma
operator|=
literal|","
expr_stmt|;
name|jsop
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
operator|.
name|append
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\":"
argument_list|)
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ChildNodeEntry
name|child
range|:
name|nodeState
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|jsop
operator|.
name|append
argument_list|(
name|comma
argument_list|)
expr_stmt|;
name|comma
operator|=
literal|","
expr_stmt|;
name|jsop
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
operator|.
name|append
argument_list|(
name|child
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\":"
argument_list|)
expr_stmt|;
name|toJson
argument_list|(
name|child
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|jsop
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

