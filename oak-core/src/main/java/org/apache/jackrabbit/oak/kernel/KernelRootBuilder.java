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
name|checkNotNull
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
name|json
operator|.
name|JsopBuilder
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
name|state
operator|.
name|NodeState
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

begin_class
class|class
name|KernelRootBuilder
extends|extends
name|MemoryNodeBuilder
block|{
comment|/**      * Number of content updates that need to happen before the updates      * are automatically committed to a branch in the MicroKernel.      */
specifier|private
specifier|static
specifier|final
name|int
name|UPDATE_LIMIT
init|=
literal|10000
decl_stmt|;
specifier|private
specifier|final
name|MicroKernel
name|kernel
decl_stmt|;
specifier|private
name|String
name|baseRevision
decl_stmt|;
specifier|private
name|String
name|branchRevision
decl_stmt|;
specifier|private
name|int
name|updates
init|=
literal|0
decl_stmt|;
specifier|public
name|KernelRootBuilder
parameter_list|(
name|MicroKernel
name|kernel
parameter_list|,
name|KernelNodeState
name|state
parameter_list|)
block|{
name|super
argument_list|(
name|checkNotNull
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|kernel
operator|=
name|checkNotNull
argument_list|(
name|kernel
argument_list|)
expr_stmt|;
name|this
operator|.
name|baseRevision
operator|=
name|state
operator|.
name|getRevision
argument_list|()
expr_stmt|;
name|this
operator|.
name|branchRevision
operator|=
literal|null
expr_stmt|;
block|}
comment|//--------------------------------------------------< MemoryNodeBuilder>---
annotation|@
name|Override
specifier|protected
name|MemoryNodeBuilder
name|createChildBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|child
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
name|child
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
name|CopyAndMoveAwareJsopDiff
name|diff
init|=
operator|new
name|CopyAndMoveAwareJsopDiff
argument_list|()
decl_stmt|;
name|compareAgainstBaseState
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|diff
operator|.
name|processMovesAndCopies
argument_list|()
expr_stmt|;
if|if
condition|(
name|branchRevision
operator|==
literal|null
condition|)
block|{
name|branchRevision
operator|=
name|kernel
operator|.
name|branch
argument_list|(
name|baseRevision
argument_list|)
expr_stmt|;
block|}
name|branchRevision
operator|=
name|kernel
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|diff
operator|.
name|toString
argument_list|()
argument_list|,
name|branchRevision
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|updates
operator|=
literal|0
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|CopyAndMoveAwareJsopDiff
extends|extends
name|JsopDiff
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|added
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|deleted
decl_stmt|;
specifier|public
name|CopyAndMoveAwareJsopDiff
parameter_list|()
block|{
name|super
argument_list|(
name|kernel
argument_list|)
expr_stmt|;
name|added
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
name|deleted
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|()
expr_stmt|;
block|}
specifier|private
name|CopyAndMoveAwareJsopDiff
parameter_list|(
name|JsopBuilder
name|jsop
parameter_list|,
name|String
name|path
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|added
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|deleted
parameter_list|)
block|{
name|super
argument_list|(
name|kernel
argument_list|,
name|jsop
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|added
operator|=
name|added
expr_stmt|;
name|this
operator|.
name|deleted
operator|=
name|deleted
expr_stmt|;
block|}
specifier|public
name|void
name|processMovesAndCopies
parameter_list|()
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|entry
range|:
name|added
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|NodeState
name|state
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|KernelNodeState
name|kstate
init|=
name|getKernelBaseState
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|String
name|kpath
init|=
name|kstate
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|deleted
operator|.
name|remove
argument_list|(
name|kpath
argument_list|)
condition|)
block|{
name|jsop
operator|.
name|tag
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|jsop
operator|.
name|tag
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
name|jsop
operator|.
name|key
argument_list|(
name|kpath
argument_list|)
operator|.
name|value
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|!=
name|kstate
condition|)
block|{
name|state
operator|.
name|compareAgainstBaseState
argument_list|(
name|kstate
argument_list|,
operator|new
name|JsopDiff
argument_list|(
name|kernel
argument_list|,
name|jsop
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|path
range|:
name|deleted
control|)
block|{
name|jsop
operator|.
name|tag
argument_list|(
literal|'-'
argument_list|)
operator|.
name|value
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------< JsopDiff>--
annotation|@
name|Override
specifier|protected
name|JsopDiff
name|createChildDiff
parameter_list|(
name|JsopBuilder
name|jsop
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|CopyAndMoveAwareJsopDiff
argument_list|(
name|jsop
argument_list|,
name|path
argument_list|,
name|added
argument_list|,
name|deleted
argument_list|)
return|;
block|}
comment|//-------------------------------------------------< NodeStateDiff>--
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
name|KernelNodeState
name|kstate
init|=
name|getKernelBaseState
argument_list|(
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|kstate
operator|!=
literal|null
condition|)
block|{
name|added
operator|.
name|put
argument_list|(
name|buildPath
argument_list|(
name|name
argument_list|)
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
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
name|KernelNodeState
name|kstate
init|=
name|getKernelBaseState
argument_list|(
name|after
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|buildPath
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|kstate
operator|!=
literal|null
operator|&&
operator|!
name|path
operator|.
name|equals
argument_list|(
name|kstate
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|deleted
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|added
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
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
name|deleted
operator|.
name|add
argument_list|(
name|buildPath
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//-------------------------------------------------------< private>--
specifier|private
name|KernelNodeState
name|getKernelBaseState
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|instanceof
name|MutableNodeState
condition|)
block|{
name|state
operator|=
operator|(
operator|(
name|MutableNodeState
operator|)
name|state
operator|)
operator|.
name|getBaseState
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|state
operator|instanceof
name|KernelNodeState
condition|)
block|{
name|KernelNodeState
name|kstate
init|=
operator|(
name|KernelNodeState
operator|)
name|state
decl_stmt|;
name|String
name|arev
init|=
name|kstate
operator|.
name|getRevision
argument_list|()
decl_stmt|;
name|String
name|brev
init|=
name|branchRevision
decl_stmt|;
if|if
condition|(
name|brev
operator|==
literal|null
condition|)
block|{
name|brev
operator|=
name|baseRevision
expr_stmt|;
block|}
if|if
condition|(
name|arev
operator|.
name|equals
argument_list|(
name|brev
argument_list|)
condition|)
block|{
return|return
name|kstate
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

