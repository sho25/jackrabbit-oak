begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|model
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
name|mk
operator|.
name|model
operator|.
name|NodeStateEditor
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
name|model
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
name|mk
operator|.
name|util
operator|.
name|PathUtils
import|;
end_import

begin_comment
comment|/**  * This {@code NodeStateEditor} implementation accumulates all changes into a json diff  * and applies them to the microkernel on  * {@link org.apache.jackrabbit.mk.model.NodeStore#merge(NodeStateEditor, NodeState)}.  *  * TODO: review/rewrite when OAK-45 is resolved  * When the MicroKernel has support for branching and merging private working copies,  * this implementation could:  * - directly write every operation through to the private working copy  * - batch write operations through to the private working copy when the  *   transient space gets too big.  * - spool write operations through to the private working copy on a background thread  */
end_comment

begin_class
specifier|public
class|class
name|KernelNodeStateEditor
implements|implements
name|NodeStateEditor
block|{
specifier|private
specifier|final
name|NodeState
name|base
decl_stmt|;
specifier|private
specifier|final
name|TransientNodeState
name|transientState
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|jsop
decl_stmt|;
name|KernelNodeStateEditor
parameter_list|(
name|NodeState
name|base
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|transientState
operator|=
operator|new
name|TransientNodeState
argument_list|(
name|base
argument_list|,
name|this
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|jsop
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
name|KernelNodeStateEditor
parameter_list|(
name|KernelNodeStateEditor
name|parentEditor
parameter_list|,
name|TransientNodeState
name|state
parameter_list|)
block|{
name|base
operator|=
name|parentEditor
operator|.
name|base
expr_stmt|;
name|transientState
operator|=
name|state
expr_stmt|;
name|jsop
operator|=
name|parentEditor
operator|.
name|jsop
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|hasNode
argument_list|(
name|transientState
argument_list|,
name|name
argument_list|)
condition|)
block|{
name|transientState
operator|.
name|addNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|jsop
operator|.
name|append
argument_list|(
literal|"+\""
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|(
name|name
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\":{}"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|hasNode
argument_list|(
name|transientState
argument_list|,
name|name
argument_list|)
condition|)
block|{
name|transientState
operator|.
name|removeNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|jsop
operator|.
name|append
argument_list|(
literal|"-\""
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|(
name|name
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|PropertyState
name|state
parameter_list|)
block|{
name|transientState
operator|.
name|setProperty
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|jsop
operator|.
name|append
argument_list|(
literal|"^\""
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|(
name|state
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\":"
argument_list|)
operator|.
name|append
argument_list|(
name|state
operator|.
name|getEncodedValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|transientState
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|jsop
operator|.
name|append
argument_list|(
literal|"^\""
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|(
name|name
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\":null"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|move
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|)
block|{
name|TransientNodeState
name|sourceParent
init|=
name|getTransientState
argument_list|(
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|sourcePath
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|sourceName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|sourcePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceParent
operator|==
literal|null
operator|||
operator|!
name|hasNode
argument_list|(
name|sourceParent
argument_list|,
name|sourceName
argument_list|)
condition|)
block|{
return|return;
block|}
name|TransientNodeState
name|destParent
init|=
name|getTransientState
argument_list|(
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|destPath
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|destName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|destParent
operator|==
literal|null
operator|||
name|hasNode
argument_list|(
name|destParent
argument_list|,
name|destName
argument_list|)
condition|)
block|{
return|return;
block|}
name|sourceParent
operator|.
name|move
argument_list|(
name|sourceName
argument_list|,
name|destParent
argument_list|,
name|destName
argument_list|)
expr_stmt|;
name|jsop
operator|.
name|append
argument_list|(
literal|">\""
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|(
name|sourcePath
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\":\""
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|(
name|destPath
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|copy
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|)
block|{
name|TransientNodeState
name|sourceParent
init|=
name|getTransientState
argument_list|(
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|sourcePath
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|sourceName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|sourcePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceParent
operator|==
literal|null
operator|||
operator|!
name|hasNode
argument_list|(
name|sourceParent
argument_list|,
name|sourceName
argument_list|)
condition|)
block|{
return|return;
block|}
name|TransientNodeState
name|destParent
init|=
name|getTransientState
argument_list|(
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|destPath
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|destName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|destParent
operator|==
literal|null
operator|||
name|hasNode
argument_list|(
name|destParent
argument_list|,
name|destName
argument_list|)
condition|)
block|{
return|return;
block|}
name|sourceParent
operator|.
name|copy
argument_list|(
name|sourceName
argument_list|,
name|destParent
argument_list|,
name|destName
argument_list|)
expr_stmt|;
name|jsop
operator|.
name|append
argument_list|(
literal|"*\""
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|(
name|sourcePath
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\":\""
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|(
name|destPath
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|KernelNodeStateEditor
name|edit
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|TransientNodeState
name|childState
init|=
name|transientState
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|childState
operator|==
literal|null
condition|?
literal|null
else|:
name|childState
operator|.
name|getEditor
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getNodeState
parameter_list|()
block|{
comment|// todo implement getNodeState
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getBaseNodeState
parameter_list|()
block|{
return|return
name|base
return|;
block|}
comment|//------------------------------------------------------------< internal>---
name|NodeState
name|mergeInto
parameter_list|(
name|MicroKernel
name|microkernel
parameter_list|,
name|KernelNodeState
name|target
parameter_list|)
block|{
name|String
name|targetPath
init|=
name|target
operator|.
name|getRevision
argument_list|()
decl_stmt|;
name|String
name|targetRevision
init|=
name|target
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|rev
init|=
name|microkernel
operator|.
name|commit
argument_list|(
name|targetPath
argument_list|,
name|jsop
operator|.
name|toString
argument_list|()
argument_list|,
name|targetRevision
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
operator|new
name|KernelNodeState
argument_list|(
name|microkernel
argument_list|,
name|targetPath
argument_list|,
name|rev
argument_list|)
return|;
block|}
name|TransientNodeState
name|getTransientState
parameter_list|()
block|{
return|return
name|transientState
return|;
block|}
specifier|private
name|TransientNodeState
name|getTransientState
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|TransientNodeState
name|state
init|=
name|transientState
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
name|path
argument_list|)
control|)
block|{
name|state
operator|=
name|state
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|state
return|;
block|}
specifier|private
name|String
name|path
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|path
init|=
name|transientState
operator|.
name|getPath
argument_list|()
decl_stmt|;
return|return
name|path
operator|.
name|isEmpty
argument_list|()
condition|?
name|name
else|:
name|path
operator|+
literal|'/'
operator|+
name|name
return|;
block|}
specifier|private
specifier|static
name|boolean
name|hasNode
parameter_list|(
name|TransientNodeState
name|state
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|state
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
end_class

end_unit

