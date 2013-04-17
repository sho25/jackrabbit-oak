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
name|List
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
name|Iterables
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|addAll
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
name|Lists
operator|.
name|newArrayList
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
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
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
name|api
operator|.
name|Type
operator|.
name|NAMES
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|ADD_EXISTING
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|CHANGE_CHANGED
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|CHANGE_DELETED
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|DELETE_CHANGED
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|DELETE_DELETED
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|MIX_REP_MERGE_CONFLICT
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|REP_OURS
import|;
end_import

begin_comment
comment|/**  * This {@link ConflictHandler} implementation resolves conflicts to  * {@link Resolution#THEIRS} and in addition marks nodes where a conflict  * occurred with the mixin {@code rep:MergeConflict}:  *  *<pre>  * [rep:MergeConflict]  *   mixin  *   primaryitem rep:ours  *   + rep:ours (nt:unstructured) protected IGNORE  *</pre>  *  * The {@code rep:ours} sub node contains our version of the node prior to  * the conflict.  *  * @see ConflictValidator  */
end_comment

begin_class
specifier|public
class|class
name|AnnotatingConflictHandler
implements|implements
name|ConflictHandler
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
name|NodeBuilder
name|marker
init|=
name|addConflictMarker
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|marker
operator|.
name|child
argument_list|(
name|ADD_EXISTING
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ours
argument_list|)
expr_stmt|;
return|return
name|Resolution
operator|.
name|THEIRS
return|;
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
name|NodeBuilder
name|marker
init|=
name|addConflictMarker
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|marker
operator|.
name|child
argument_list|(
name|CHANGE_DELETED
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ours
argument_list|)
expr_stmt|;
return|return
name|Resolution
operator|.
name|THEIRS
return|;
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
name|NodeBuilder
name|marker
init|=
name|addConflictMarker
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|marker
operator|.
name|child
argument_list|(
name|CHANGE_CHANGED
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ours
argument_list|)
expr_stmt|;
return|return
name|Resolution
operator|.
name|THEIRS
return|;
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
name|NodeBuilder
name|marker
init|=
name|addConflictMarker
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|marker
operator|.
name|child
argument_list|(
name|DELETE_CHANGED
argument_list|)
operator|.
name|setProperty
argument_list|(
name|theirs
argument_list|)
expr_stmt|;
return|return
name|Resolution
operator|.
name|THEIRS
return|;
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
name|NodeBuilder
name|marker
init|=
name|addConflictMarker
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|marker
operator|.
name|child
argument_list|(
name|DELETE_DELETED
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ours
argument_list|)
expr_stmt|;
return|return
name|Resolution
operator|.
name|THEIRS
return|;
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
name|NodeBuilder
name|marker
init|=
name|addConflictMarker
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|marker
operator|.
name|child
argument_list|(
name|ADD_EXISTING
argument_list|)
operator|.
name|setNode
argument_list|(
name|name
argument_list|,
name|ours
argument_list|)
expr_stmt|;
return|return
name|Resolution
operator|.
name|THEIRS
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
name|NodeBuilder
name|marker
init|=
name|addConflictMarker
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|marker
operator|.
name|child
argument_list|(
name|CHANGE_DELETED
argument_list|)
operator|.
name|setNode
argument_list|(
name|name
argument_list|,
name|ours
argument_list|)
expr_stmt|;
return|return
name|Resolution
operator|.
name|THEIRS
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
name|NodeBuilder
name|marker
init|=
name|addConflictMarker
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|markChild
argument_list|(
name|marker
operator|.
name|child
argument_list|(
name|DELETE_CHANGED
argument_list|)
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|Resolution
operator|.
name|THEIRS
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
name|NodeBuilder
name|marker
init|=
name|addConflictMarker
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|markChild
argument_list|(
name|marker
operator|.
name|child
argument_list|(
name|DELETE_DELETED
argument_list|)
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|Resolution
operator|.
name|THEIRS
return|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|addConflictMarker
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|mixins
init|=
name|newArrayList
argument_list|()
decl_stmt|;
empty_stmt|;
name|boolean
name|hasConflictType
init|=
literal|false
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|types
init|=
name|parent
operator|.
name|getNames
argument_list|(
name|JCR_MIXINTYPES
argument_list|)
decl_stmt|;
if|if
condition|(
name|types
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|type
range|:
name|types
control|)
block|{
name|mixins
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|MIX_REP_MERGE_CONFLICT
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|hasConflictType
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|hasConflictType
condition|)
block|{
name|mixins
operator|.
name|add
argument_list|(
name|MIX_REP_MERGE_CONFLICT
argument_list|)
expr_stmt|;
name|parent
operator|.
name|setProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|,
name|mixins
argument_list|,
name|NAMES
argument_list|)
expr_stmt|;
block|}
return|return
name|parent
operator|.
name|child
argument_list|(
name|REP_OURS
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|markChild
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|parent
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

