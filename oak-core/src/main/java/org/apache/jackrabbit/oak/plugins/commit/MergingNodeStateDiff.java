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
name|commit
package|;
end_package

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
name|ConflictAnnotatingRebaseDiff
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
name|ConflictAnnotatingRebaseDiff
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
name|ConflictAnnotatingRebaseDiff
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
name|ConflictAnnotatingRebaseDiff
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
name|ConflictAnnotatingRebaseDiff
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
name|ConflictAnnotatingRebaseDiff
operator|.
name|CONFLICT
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
name|ConflictAnnotatingRebaseDiff
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
name|ConflictAnnotatingRebaseDiff
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
name|ConflictAnnotatingRebaseDiff
operator|.
name|DELETE_DELETED_NODE
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
name|ConflictAnnotatingRebaseDiff
operator|.
name|DELETE_DELETED_PROPERTY
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|core
operator|.
name|TreeImpl
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
name|MemoryPropertyBuilder
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
name|ConflictHandler
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
name|ConflictHandler
operator|.
name|Resolution
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
name|PropertyBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * MergingNodeStateDiff... TODO  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|MergingNodeStateDiff
extends|extends
name|DefaultNodeStateDiff
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MergingNodeStateDiff
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|parent
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|target
decl_stmt|;
specifier|private
specifier|final
name|ConflictHandler
name|conflictHandler
decl_stmt|;
specifier|private
name|MergingNodeStateDiff
parameter_list|(
name|NodeState
name|parent
parameter_list|,
name|NodeBuilder
name|target
parameter_list|,
name|ConflictHandler
name|conflictHandler
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
name|this
operator|.
name|conflictHandler
operator|=
name|conflictHandler
expr_stmt|;
block|}
specifier|static
name|NodeState
name|merge
parameter_list|(
name|NodeState
name|fromState
parameter_list|,
name|NodeState
name|toState
parameter_list|,
name|ConflictHandler
name|conflictHandler
parameter_list|)
block|{
return|return
name|merge
argument_list|(
name|fromState
argument_list|,
name|toState
argument_list|,
name|toState
operator|.
name|builder
argument_list|()
argument_list|,
name|conflictHandler
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|NodeState
name|merge
parameter_list|(
name|NodeState
name|fromState
parameter_list|,
name|NodeState
name|toState
parameter_list|,
name|NodeBuilder
name|target
parameter_list|,
name|ConflictHandler
name|conflictHandler
parameter_list|)
block|{
name|toState
operator|.
name|compareAgainstBaseState
argument_list|(
name|fromState
argument_list|,
operator|new
name|MergingNodeStateDiff
argument_list|(
name|toState
argument_list|,
name|target
argument_list|,
name|conflictHandler
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|target
operator|.
name|getNodeState
argument_list|()
return|;
block|}
comment|//------------------------------------------------------< NodeStateDiff>---
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
if|if
condition|(
name|CONFLICT
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
for|for
control|(
name|ChildNodeEntry
name|conflict
range|:
name|after
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|resolveConflict
argument_list|(
name|conflict
operator|.
name|getName
argument_list|()
argument_list|,
name|conflict
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|target
operator|.
name|getChildNode
argument_list|(
name|CONFLICT
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
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
name|merge
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|target
operator|.
name|child
argument_list|(
name|name
argument_list|)
argument_list|,
name|conflictHandler
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|void
name|resolveConflict
parameter_list|(
name|String
name|conflictName
parameter_list|,
name|NodeState
name|conflictInfo
parameter_list|)
block|{
name|PropertyConflictHandler
name|propertyConflictHandler
init|=
name|propertyConflictHandlers
operator|.
name|get
argument_list|(
name|conflictName
argument_list|)
decl_stmt|;
if|if
condition|(
name|propertyConflictHandler
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|PropertyState
name|ours
range|:
name|conflictInfo
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|PropertyState
name|theirs
init|=
name|parent
operator|.
name|getProperty
argument_list|(
name|ours
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Resolution
name|resolution
init|=
name|propertyConflictHandler
operator|.
name|resolve
argument_list|(
name|ours
argument_list|,
name|theirs
argument_list|)
decl_stmt|;
name|applyResolution
argument_list|(
name|resolution
argument_list|,
name|conflictName
argument_list|,
name|ours
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|NodeConflictHandler
name|nodeConflictHandler
init|=
name|nodeConflictHandlers
operator|.
name|get
argument_list|(
name|conflictName
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeConflictHandler
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ChildNodeEntry
name|oursCNE
range|:
name|conflictInfo
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|oursCNE
operator|.
name|getName
argument_list|()
decl_stmt|;
name|NodeState
name|ours
init|=
name|oursCNE
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|theirs
init|=
name|parent
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Resolution
name|resolution
init|=
name|nodeConflictHandler
operator|.
name|resolve
argument_list|(
name|name
argument_list|,
name|ours
argument_list|,
name|theirs
argument_list|)
decl_stmt|;
name|applyResolution
argument_list|(
name|resolution
argument_list|,
name|conflictName
argument_list|,
name|name
argument_list|,
name|ours
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ignoring unknown conflict '"
operator|+
name|conflictName
operator|+
literal|'\''
argument_list|)
expr_stmt|;
block|}
block|}
name|NodeBuilder
name|conflictMarker
init|=
name|getConflictMarker
argument_list|(
name|conflictName
argument_list|)
decl_stmt|;
if|if
condition|(
name|conflictMarker
operator|!=
literal|null
condition|)
block|{
assert|assert
name|conflictMarker
operator|.
name|getChildNodeCount
argument_list|()
operator|==
literal|0
assert|;
block|}
block|}
specifier|private
name|void
name|applyResolution
parameter_list|(
name|Resolution
name|resolution
parameter_list|,
name|String
name|conflictName
parameter_list|,
name|PropertyState
name|ours
parameter_list|)
block|{
name|String
name|name
init|=
name|ours
operator|.
name|getName
argument_list|()
decl_stmt|;
name|NodeBuilder
name|conflictMarker
init|=
name|getConflictMarker
argument_list|(
name|conflictName
argument_list|)
decl_stmt|;
if|if
condition|(
name|resolution
operator|==
name|Resolution
operator|.
name|OURS
condition|)
block|{
if|if
condition|(
name|DELETE_CHANGED_PROPERTY
operator|.
name|equals
argument_list|(
name|conflictName
argument_list|)
condition|)
block|{
name|target
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|target
operator|.
name|setProperty
argument_list|(
name|ours
argument_list|)
expr_stmt|;
block|}
block|}
name|conflictMarker
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|applyResolution
parameter_list|(
name|Resolution
name|resolution
parameter_list|,
name|String
name|conflictName
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|ours
parameter_list|)
block|{
name|NodeBuilder
name|conflictMarker
init|=
name|getConflictMarker
argument_list|(
name|conflictName
argument_list|)
decl_stmt|;
if|if
condition|(
name|resolution
operator|==
name|Resolution
operator|.
name|OURS
condition|)
block|{
if|if
condition|(
name|DELETE_CHANGED_NODE
operator|.
name|equals
argument_list|(
name|conflictName
argument_list|)
condition|)
block|{
name|removeChild
argument_list|(
name|target
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addChild
argument_list|(
name|target
argument_list|,
name|name
argument_list|,
name|ours
argument_list|)
expr_stmt|;
block|}
block|}
name|conflictMarker
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
specifier|private
name|NodeBuilder
name|getConflictMarker
parameter_list|(
name|String
name|conflictName
parameter_list|)
block|{
if|if
condition|(
name|target
operator|.
name|hasChildNode
argument_list|(
name|CONFLICT
argument_list|)
condition|)
block|{
name|NodeBuilder
name|conflict
init|=
name|target
operator|.
name|child
argument_list|(
name|CONFLICT
argument_list|)
decl_stmt|;
if|if
condition|(
name|conflict
operator|.
name|hasChildNode
argument_list|(
name|conflictName
argument_list|)
condition|)
block|{
return|return
name|conflict
operator|.
name|child
argument_list|(
name|conflictName
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
interface|interface
name|PropertyConflictHandler
block|{
name|Resolution
name|resolve
parameter_list|(
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
function_decl|;
block|}
specifier|private
interface|interface
name|NodeConflictHandler
block|{
name|Resolution
name|resolve
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|ours
parameter_list|,
name|NodeState
name|theirs
parameter_list|)
function_decl|;
block|}
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyConflictHandler
argument_list|>
name|propertyConflictHandlers
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|ADD_EXISTING_PROPERTY
argument_list|,
operator|new
name|PropertyConflictHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Resolution
name|resolve
parameter_list|(
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
block|{
return|return
name|conflictHandler
operator|.
name|addExistingProperty
argument_list|(
name|target
argument_list|,
name|ours
argument_list|,
name|theirs
argument_list|)
return|;
block|}
block|}
argument_list|,
name|CHANGE_DELETED_PROPERTY
argument_list|,
operator|new
name|PropertyConflictHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Resolution
name|resolve
parameter_list|(
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
block|{
return|return
name|conflictHandler
operator|.
name|changeDeletedProperty
argument_list|(
name|target
argument_list|,
name|ours
argument_list|)
return|;
block|}
block|}
argument_list|,
name|CHANGE_CHANGED_PROPERTY
argument_list|,
operator|new
name|PropertyConflictHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Resolution
name|resolve
parameter_list|(
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
block|{
return|return
name|conflictHandler
operator|.
name|changeChangedProperty
argument_list|(
name|target
argument_list|,
name|ours
argument_list|,
name|theirs
argument_list|)
return|;
block|}
block|}
argument_list|,
name|DELETE_DELETED_PROPERTY
argument_list|,
operator|new
name|PropertyConflictHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Resolution
name|resolve
parameter_list|(
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
block|{
return|return
name|conflictHandler
operator|.
name|deleteDeletedProperty
argument_list|(
name|target
argument_list|,
name|ours
argument_list|)
return|;
block|}
block|}
argument_list|,
name|DELETE_CHANGED_PROPERTY
argument_list|,
operator|new
name|PropertyConflictHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Resolution
name|resolve
parameter_list|(
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
block|{
return|return
name|conflictHandler
operator|.
name|deleteChangedProperty
argument_list|(
name|target
argument_list|,
name|theirs
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NodeConflictHandler
argument_list|>
name|nodeConflictHandlers
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|ADD_EXISTING_NODE
argument_list|,
operator|new
name|NodeConflictHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Resolution
name|resolve
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|ours
parameter_list|,
name|NodeState
name|theirs
parameter_list|)
block|{
return|return
name|conflictHandler
operator|.
name|addExistingNode
argument_list|(
name|target
argument_list|,
name|name
argument_list|,
name|ours
argument_list|,
name|theirs
argument_list|)
return|;
block|}
block|}
argument_list|,
name|CHANGE_DELETED_NODE
argument_list|,
operator|new
name|NodeConflictHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Resolution
name|resolve
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|ours
parameter_list|,
name|NodeState
name|theirs
parameter_list|)
block|{
return|return
name|conflictHandler
operator|.
name|changeDeletedNode
argument_list|(
name|target
argument_list|,
name|name
argument_list|,
name|ours
argument_list|)
return|;
block|}
block|}
argument_list|,
name|DELETE_CHANGED_NODE
argument_list|,
operator|new
name|NodeConflictHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Resolution
name|resolve
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|ours
parameter_list|,
name|NodeState
name|theirs
parameter_list|)
block|{
return|return
name|conflictHandler
operator|.
name|deleteChangedNode
argument_list|(
name|target
argument_list|,
name|name
argument_list|,
name|theirs
argument_list|)
return|;
block|}
block|}
argument_list|,
name|DELETE_DELETED_NODE
argument_list|,
operator|new
name|NodeConflictHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Resolution
name|resolve
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|ours
parameter_list|,
name|NodeState
name|theirs
parameter_list|)
block|{
return|return
name|conflictHandler
operator|.
name|deleteDeletedNode
argument_list|(
name|target
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|void
name|addChild
parameter_list|(
name|NodeBuilder
name|target
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|target
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|PropertyState
name|childOrder
init|=
name|target
operator|.
name|getProperty
argument_list|(
name|TreeImpl
operator|.
name|OAK_CHILD_ORDER
argument_list|)
decl_stmt|;
if|if
condition|(
name|childOrder
operator|!=
literal|null
condition|)
block|{
name|PropertyBuilder
argument_list|<
name|String
argument_list|>
name|builder
init|=
name|MemoryPropertyBuilder
operator|.
name|copy
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|childOrder
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addValue
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|target
operator|.
name|setProperty
argument_list|(
name|builder
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|removeChild
parameter_list|(
name|NodeBuilder
name|target
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|target
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|PropertyState
name|childOrder
init|=
name|target
operator|.
name|getProperty
argument_list|(
name|TreeImpl
operator|.
name|OAK_CHILD_ORDER
argument_list|)
decl_stmt|;
if|if
condition|(
name|childOrder
operator|!=
literal|null
condition|)
block|{
name|PropertyBuilder
argument_list|<
name|String
argument_list|>
name|builder
init|=
name|MemoryPropertyBuilder
operator|.
name|copy
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|childOrder
argument_list|)
decl_stmt|;
name|builder
operator|.
name|removeValue
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|target
operator|.
name|setProperty
argument_list|(
name|builder
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

