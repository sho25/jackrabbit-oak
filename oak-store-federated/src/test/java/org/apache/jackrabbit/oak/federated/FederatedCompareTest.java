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
name|federated
package|;
end_package

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
name|ImmutableSet
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
name|Lists
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
name|api
operator|.
name|Type
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
name|MemoryNodeStore
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
name|commit
operator|.
name|EmptyHook
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
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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
name|DefaultNodeStateDiff
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
name|NodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|FederatedCompareTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|reportedNodesAreWrapped
parameter_list|()
block|{
name|Mounts
operator|.
name|Builder
name|mipBuilder
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|mipBuilder
operator|.
name|readOnlyMount
argument_list|(
literal|"libs"
argument_list|,
literal|"/libs"
argument_list|)
expr_stmt|;
name|MountInfoProvider
name|mip
init|=
name|mipBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|NodeStore
name|globalStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|FederatedNodeStore
operator|.
name|Builder
name|nsBuilder
init|=
operator|new
name|FederatedNodeStore
operator|.
name|Builder
argument_list|(
name|mip
argument_list|,
name|globalStore
argument_list|)
decl_stmt|;
name|nsBuilder
operator|.
name|addMount
argument_list|(
literal|"libs"
argument_list|,
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
expr_stmt|;
name|FederatedNodeStore
name|federatedNodeStore
init|=
name|nsBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|federatedNodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"changed"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"deleted"
argument_list|)
expr_stmt|;
name|NodeState
name|base
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"changed"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"newProp"
argument_list|,
literal|"xyz"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"deleted"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"added"
argument_list|)
expr_stmt|;
specifier|final
name|NodeState
name|modified
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|modifiedNodes
init|=
name|newHashSet
argument_list|()
decl_stmt|;
name|modified
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
operator|new
name|DefaultNodeStateDiff
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|after
operator|instanceof
name|FederatedNodeState
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|name
argument_list|,
literal|"added"
argument_list|)
expr_stmt|;
name|modifiedNodes
operator|.
name|add
argument_list|(
name|name
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
name|assertTrue
argument_list|(
name|before
operator|instanceof
name|FederatedNodeState
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|after
operator|instanceof
name|FederatedNodeState
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|name
argument_list|,
literal|"changed"
argument_list|)
expr_stmt|;
name|modifiedNodes
operator|.
name|add
argument_list|(
name|name
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
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|before
operator|instanceof
name|FederatedNodeState
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|name
argument_list|,
literal|"deleted"
argument_list|)
expr_stmt|;
name|modifiedNodes
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"added"
argument_list|,
literal|"changed"
argument_list|,
literal|"deleted"
argument_list|)
argument_list|,
name|modifiedNodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|onlyPropertiesOnMainNodesAreCompared
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"libs"
argument_list|,
literal|"/libs"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NodeStore
name|globalStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|NodeStore
name|libsStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MountedNodeStore
argument_list|>
name|mounts
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|mounts
operator|.
name|add
argument_list|(
operator|new
name|MountedNodeStore
argument_list|(
name|mip
operator|.
name|getMountByName
argument_list|(
literal|"libs"
argument_list|)
argument_list|,
name|libsStore
argument_list|)
argument_list|)
expr_stmt|;
name|FederatedNodeStore
name|federatedNodeStore
init|=
operator|new
name|FederatedNodeStore
argument_list|(
name|mip
argument_list|,
name|globalStore
argument_list|,
name|mounts
argument_list|)
decl_stmt|;
name|NodeState
name|empty
init|=
name|federatedNodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|globalStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"global-prop-1"
argument_list|,
literal|"val"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"global-prop-2"
argument_list|,
literal|"val"
argument_list|)
expr_stmt|;
name|globalStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|NodeBuilder
name|libsBuilder
init|=
name|libsStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|libsBuilder
operator|.
name|setProperty
argument_list|(
literal|"libs-prop-1"
argument_list|,
literal|"val"
argument_list|)
expr_stmt|;
name|libsBuilder
operator|.
name|setProperty
argument_list|(
literal|"libs-prop-2"
argument_list|,
literal|"val"
argument_list|)
expr_stmt|;
name|libsStore
operator|.
name|merge
argument_list|(
name|libsBuilder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|NodeState
name|modified
init|=
name|federatedNodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|addedProperties
init|=
name|newHashSet
argument_list|()
decl_stmt|;
name|modified
operator|.
name|compareAgainstBaseState
argument_list|(
name|empty
argument_list|,
operator|new
name|DefaultNodeStateDiff
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
name|addedProperties
operator|.
name|add
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"global-prop-1"
argument_list|,
literal|"global-prop-2"
argument_list|)
argument_list|,
name|addedProperties
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodesOutsideTheMountsAreIgnored
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"libs"
argument_list|,
literal|"/libs"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NodeStore
name|globalStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|NodeStore
name|libsStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MountedNodeStore
argument_list|>
name|mounts
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|mounts
operator|.
name|add
argument_list|(
operator|new
name|MountedNodeStore
argument_list|(
name|mip
operator|.
name|getMountByName
argument_list|(
literal|"libs"
argument_list|)
argument_list|,
name|libsStore
argument_list|)
argument_list|)
expr_stmt|;
name|FederatedNodeStore
name|federatedNodeStore
init|=
operator|new
name|FederatedNodeStore
argument_list|(
name|mip
argument_list|,
name|globalStore
argument_list|,
name|mounts
argument_list|)
decl_stmt|;
name|NodeState
name|empty
init|=
name|federatedNodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|globalStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"global-child-1"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"global-child-2"
argument_list|)
expr_stmt|;
name|globalStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|NodeBuilder
name|libsBuilder
init|=
name|libsStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|libsBuilder
operator|.
name|child
argument_list|(
literal|"libs"
argument_list|)
expr_stmt|;
name|libsBuilder
operator|.
name|child
argument_list|(
literal|"libs-child-1"
argument_list|)
expr_stmt|;
name|libsBuilder
operator|.
name|child
argument_list|(
literal|"libs-child-2"
argument_list|)
expr_stmt|;
name|libsStore
operator|.
name|merge
argument_list|(
name|libsBuilder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|NodeState
name|modified
init|=
name|federatedNodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|addedChildren
init|=
name|newHashSet
argument_list|()
decl_stmt|;
name|modified
operator|.
name|compareAgainstBaseState
argument_list|(
name|empty
argument_list|,
operator|new
name|DefaultNodeStateDiff
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|addedChildren
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"global-child-1"
argument_list|,
literal|"global-child-2"
argument_list|,
literal|"libs"
argument_list|)
argument_list|,
name|addedChildren
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
