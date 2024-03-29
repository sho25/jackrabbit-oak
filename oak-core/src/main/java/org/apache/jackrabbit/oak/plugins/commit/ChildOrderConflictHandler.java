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
name|PropertyBuilder
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
name|tree
operator|.
name|TreeConstants
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
name|PartialConflictHandler
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

begin_comment
comment|/**  * This conflict handler instance takes care of properly merging conflicts  * occurring by concurrent reorder operations.  *  * @see TreeConstants#OAK_CHILD_ORDER  */
end_comment

begin_class
specifier|public
class|class
name|ChildOrderConflictHandler
implements|implements
name|PartialConflictHandler
block|{
annotation|@
name|Override
specifier|public
name|Resolution
name|addExistingProperty
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
block|{
if|if
condition|(
name|isChildOrderProperty
argument_list|(
name|ours
argument_list|)
condition|)
block|{
comment|// two sessions concurrently called orderBefore() on a Tree
comment|// that was previously unordered.
name|merge
argument_list|(
name|parent
argument_list|,
name|ours
argument_list|,
name|theirs
argument_list|)
expr_stmt|;
return|return
name|Resolution
operator|.
name|MERGED
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|changeDeletedProperty
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|PropertyState
name|ours
parameter_list|)
block|{
if|if
condition|(
name|isChildOrderProperty
argument_list|(
name|ours
argument_list|)
condition|)
block|{
comment|// orderBefore() on trees that were deleted
return|return
name|Resolution
operator|.
name|THEIRS
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|changeChangedProperty
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
block|{
if|if
condition|(
name|isChildOrderProperty
argument_list|(
name|ours
argument_list|)
condition|)
block|{
name|merge
argument_list|(
name|parent
argument_list|,
name|ours
argument_list|,
name|theirs
argument_list|)
expr_stmt|;
return|return
name|Resolution
operator|.
name|MERGED
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
name|void
name|merge
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|theirOrder
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|theirs
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
decl_stmt|;
name|PropertyBuilder
argument_list|<
name|String
argument_list|>
name|merged
init|=
name|PropertyBuilder
operator|.
name|array
argument_list|(
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|assignFrom
argument_list|(
name|theirs
argument_list|)
decl_stmt|;
comment|// Append child node names from ours that are not in theirs
for|for
control|(
name|String
name|ourChild
range|:
name|ours
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|theirOrder
operator|.
name|contains
argument_list|(
name|ourChild
argument_list|)
condition|)
block|{
name|merged
operator|.
name|addValue
argument_list|(
name|ourChild
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Remove child node names of nodes that have been removed
for|for
control|(
name|String
name|child
range|:
name|merged
operator|.
name|getValues
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|parent
operator|.
name|hasChildNode
argument_list|(
name|child
argument_list|)
condition|)
block|{
name|merged
operator|.
name|removeValue
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
name|parent
operator|.
name|setProperty
argument_list|(
name|merged
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|deleteDeletedProperty
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|PropertyState
name|ours
parameter_list|)
block|{
if|if
condition|(
name|isChildOrderProperty
argument_list|(
name|ours
argument_list|)
condition|)
block|{
comment|// concurrent remove of ordered trees
return|return
name|Resolution
operator|.
name|THEIRS
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|deleteChangedProperty
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
block|{
if|if
condition|(
name|isChildOrderProperty
argument_list|(
name|theirs
argument_list|)
condition|)
block|{
comment|// remove trees that were reordered by another session
return|return
name|Resolution
operator|.
name|THEIRS
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|addExistingNode
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
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
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|changeDeletedNode
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|ours
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|deleteChangedNode
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|theirs
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resolution
name|deleteDeletedNode
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|//----------------------------< internal>----------------------------------
specifier|private
specifier|static
name|boolean
name|isChildOrderProperty
parameter_list|(
name|PropertyState
name|p
parameter_list|)
block|{
return|return
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
operator|.
name|equals
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

