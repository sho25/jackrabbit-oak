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
name|NodeStateBuilder
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
name|NodeStore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_comment
comment|/**  * {@link MicroKernel}-based {@link NodeStore} implementation.  */
end_comment

begin_class
specifier|public
class|class
name|KernelNodeStore
implements|implements
name|NodeStore
block|{
specifier|final
name|MicroKernel
name|kernel
decl_stmt|;
specifier|final
name|CoreValueFactory
name|valueFactory
decl_stmt|;
specifier|public
name|KernelNodeStore
parameter_list|(
name|MicroKernel
name|kernel
parameter_list|,
name|CoreValueFactory
name|valueFactory
parameter_list|)
block|{
name|this
operator|.
name|kernel
operator|=
name|kernel
expr_stmt|;
name|this
operator|.
name|valueFactory
operator|=
name|valueFactory
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
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeStateBuilder
name|getBuilder
parameter_list|(
name|NodeState
name|base
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|base
operator|instanceof
name|KernelNodeState
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Alien node state"
argument_list|)
throw|;
block|}
name|KernelNodeState
name|kernelNodeState
init|=
operator|(
name|KernelNodeState
operator|)
name|base
decl_stmt|;
name|String
name|branchRevision
init|=
name|kernel
operator|.
name|branch
argument_list|(
name|kernelNodeState
operator|.
name|getRevision
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|kernelNodeState
operator|.
name|getPath
argument_list|()
decl_stmt|;
return|return
name|KernelNodeStateBuilder
operator|.
name|create
argument_list|(
name|kernel
argument_list|,
name|valueFactory
argument_list|,
name|path
argument_list|,
name|branchRevision
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|NodeStateBuilder
name|builder
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|!
operator|(
name|builder
operator|instanceof
name|KernelNodeStateBuilder
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Alien builder"
argument_list|)
throw|;
block|}
name|KernelNodeStateBuilder
name|kernelNodeStateBuilder
init|=
operator|(
name|KernelNodeStateBuilder
operator|)
name|builder
decl_stmt|;
name|kernelNodeStateBuilder
operator|.
name|apply
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|compare
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeStateDiff
name|diff
parameter_list|)
block|{
name|compareProperties
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|compareChildNodes
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|diff
argument_list|)
expr_stmt|;
block|}
comment|/**      * Compares the properties of the given two node states.      *      * @param before node state before changes      * @param after node state after changes      * @param diff handler of node state differences      */
specifier|protected
name|void
name|compareProperties
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeStateDiff
name|diff
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|beforeProperties
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyState
name|beforeProperty
range|:
name|before
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|beforeProperty
operator|.
name|getName
argument_list|()
decl_stmt|;
name|PropertyState
name|afterProperty
init|=
name|after
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|afterProperty
operator|==
literal|null
condition|)
block|{
name|diff
operator|.
name|propertyDeleted
argument_list|(
name|beforeProperty
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|beforeProperties
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|beforeProperty
operator|.
name|equals
argument_list|(
name|afterProperty
argument_list|)
condition|)
block|{
name|diff
operator|.
name|propertyChanged
argument_list|(
name|beforeProperty
argument_list|,
name|afterProperty
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|PropertyState
name|afterProperty
range|:
name|after
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|beforeProperties
operator|.
name|contains
argument_list|(
name|afterProperty
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|diff
operator|.
name|propertyAdded
argument_list|(
name|afterProperty
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Compares the child nodes of the given two node states.      *      * @param before node state before changes      * @param after node state after changes      * @param diff handler of node state differences      */
specifier|protected
name|void
name|compareChildNodes
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeStateDiff
name|diff
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|beforeChildNodes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|beforeCNE
range|:
name|before
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
control|)
block|{
name|String
name|name
init|=
name|beforeCNE
operator|.
name|getName
argument_list|()
decl_stmt|;
name|NodeState
name|beforeChild
init|=
name|beforeCNE
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|afterChild
init|=
name|after
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|afterChild
operator|==
literal|null
condition|)
block|{
name|diff
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|beforeChild
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|beforeChildNodes
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|beforeChild
operator|.
name|equals
argument_list|(
name|afterChild
argument_list|)
condition|)
block|{
name|diff
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|beforeChild
argument_list|,
name|afterChild
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|ChildNodeEntry
name|afterChild
range|:
name|after
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
control|)
block|{
name|String
name|name
init|=
name|afterChild
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|beforeChildNodes
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|diff
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|afterChild
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

