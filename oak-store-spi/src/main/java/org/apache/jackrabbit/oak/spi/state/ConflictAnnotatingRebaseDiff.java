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
import|import static
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
name|ConflictType
operator|.
name|DELETE_CHANGED_PROPERTY
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
name|spi
operator|.
name|state
operator|.
name|ConflictType
operator|.
name|DELETE_CHANGED_NODE
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
name|spi
operator|.
name|state
operator|.
name|ConflictType
operator|.
name|ADD_EXISTING_PROPERTY
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
name|spi
operator|.
name|state
operator|.
name|ConflictType
operator|.
name|CHANGE_DELETED_PROPERTY
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
name|spi
operator|.
name|state
operator|.
name|ConflictType
operator|.
name|CHANGE_CHANGED_PROPERTY
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
name|spi
operator|.
name|state
operator|.
name|ConflictType
operator|.
name|DELETE_DELETED_PROPERTY
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
name|spi
operator|.
name|state
operator|.
name|ConflictType
operator|.
name|ADD_EXISTING_NODE
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
name|spi
operator|.
name|state
operator|.
name|ConflictType
operator|.
name|CHANGE_DELETED_NODE
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
name|spi
operator|.
name|state
operator|.
name|ConflictType
operator|.
name|DELETE_DELETED_NODE
import|;
end_import

begin_comment
comment|/**  * This implementation of {@code AbstractRebaseDiff} implements a {@link NodeStateDiff},  * which performs the conflict handling as defined in {@link NodeStore#rebase(NodeBuilder)}  * on the Oak SPI state level by annotating conflicting items with conflict  * markers.  */
end_comment

begin_class
specifier|public
class|class
name|ConflictAnnotatingRebaseDiff
extends|extends
name|AbstractRebaseDiff
block|{
specifier|public
specifier|static
specifier|final
name|String
name|CONFLICT
init|=
literal|":conflict"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|BASE
init|=
literal|":base"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OURS
init|=
literal|":ours"
decl_stmt|;
specifier|public
name|ConflictAnnotatingRebaseDiff
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|super
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ConflictAnnotatingRebaseDiff
name|createDiff
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ConflictAnnotatingRebaseDiff
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|addExistingProperty
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
name|NodeBuilder
name|cb
init|=
name|conflictMarker
argument_list|(
name|builder
argument_list|,
name|ADD_EXISTING_PROPERTY
argument_list|)
decl_stmt|;
name|cb
operator|.
name|child
argument_list|(
name|BASE
argument_list|)
operator|.
name|setProperty
argument_list|(
name|before
argument_list|)
expr_stmt|;
name|cb
operator|.
name|child
argument_list|(
name|OURS
argument_list|)
operator|.
name|setProperty
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|changeDeletedProperty
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|PropertyState
name|after
parameter_list|,
name|PropertyState
name|base
parameter_list|)
block|{
name|NodeBuilder
name|cb
init|=
name|conflictMarker
argument_list|(
name|builder
argument_list|,
name|CHANGE_DELETED_PROPERTY
argument_list|)
decl_stmt|;
name|cb
operator|.
name|child
argument_list|(
name|BASE
argument_list|)
operator|.
name|setProperty
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|cb
operator|.
name|child
argument_list|(
name|OURS
argument_list|)
operator|.
name|setProperty
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|changeChangedProperty
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
name|NodeBuilder
name|cb
init|=
name|conflictMarker
argument_list|(
name|builder
argument_list|,
name|CHANGE_CHANGED_PROPERTY
argument_list|)
decl_stmt|;
name|cb
operator|.
name|child
argument_list|(
name|BASE
argument_list|)
operator|.
name|setProperty
argument_list|(
name|before
argument_list|)
expr_stmt|;
name|cb
operator|.
name|child
argument_list|(
name|OURS
argument_list|)
operator|.
name|setProperty
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|deleteDeletedProperty
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|PropertyState
name|before
parameter_list|)
block|{
name|NodeBuilder
name|cb
init|=
name|conflictMarker
argument_list|(
name|builder
argument_list|,
name|DELETE_DELETED_PROPERTY
argument_list|)
decl_stmt|;
name|cb
operator|.
name|child
argument_list|(
name|BASE
argument_list|)
operator|.
name|setProperty
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|deleteChangedProperty
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|PropertyState
name|before
parameter_list|)
block|{
name|NodeBuilder
name|cb
init|=
name|conflictMarker
argument_list|(
name|builder
argument_list|,
name|DELETE_CHANGED_PROPERTY
argument_list|)
decl_stmt|;
name|cb
operator|.
name|child
argument_list|(
name|BASE
argument_list|)
operator|.
name|setProperty
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|addExistingNode
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
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
name|NodeBuilder
name|cb
init|=
name|conflictMarker
argument_list|(
name|builder
argument_list|,
name|ADD_EXISTING_NODE
argument_list|)
decl_stmt|;
name|cb
operator|.
name|child
argument_list|(
name|BASE
argument_list|)
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
name|cb
operator|.
name|child
argument_list|(
name|OURS
argument_list|)
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|changeDeletedNode
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeState
name|base
parameter_list|)
block|{
name|NodeBuilder
name|cb
init|=
name|conflictMarker
argument_list|(
name|builder
argument_list|,
name|CHANGE_DELETED_NODE
argument_list|)
decl_stmt|;
name|cb
operator|.
name|child
argument_list|(
name|BASE
argument_list|)
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
name|base
argument_list|)
expr_stmt|;
name|cb
operator|.
name|child
argument_list|(
name|OURS
argument_list|)
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|deleteDeletedNode
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|NodeBuilder
name|cb
init|=
name|conflictMarker
argument_list|(
name|builder
argument_list|,
name|DELETE_DELETED_NODE
argument_list|)
decl_stmt|;
name|cb
operator|.
name|child
argument_list|(
name|BASE
argument_list|)
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|deleteChangedNode
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|NodeBuilder
name|cb
init|=
name|conflictMarker
argument_list|(
name|builder
argument_list|,
name|DELETE_CHANGED_NODE
argument_list|)
decl_stmt|;
name|cb
operator|.
name|child
argument_list|(
name|BASE
argument_list|)
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|conflictMarker
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|ConflictType
name|ct
parameter_list|)
block|{
return|return
name|builder
operator|.
name|child
argument_list|(
name|CONFLICT
argument_list|)
operator|.
name|child
argument_list|(
name|ct
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

