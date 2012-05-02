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
name|spi
operator|.
name|state
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
name|oak
operator|.
name|api
operator|.
name|PropertyState
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

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractNodeStore
implements|implements
name|NodeStore
block|{
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
argument_list|()
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
argument_list|()
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

