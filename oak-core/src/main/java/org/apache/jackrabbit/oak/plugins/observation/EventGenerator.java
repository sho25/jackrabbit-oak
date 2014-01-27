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
name|plugins
operator|.
name|observation
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
name|newLinkedList
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|NODE_ADDED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|NODE_MOVED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|NODE_REMOVED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|PROPERTY_ADDED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|PROPERTY_CHANGED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|PROPERTY_REMOVED
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
name|api
operator|.
name|Type
operator|.
name|STRING
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
name|core
operator|.
name|AbstractTree
operator|.
name|OAK_CHILD_ORDER
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|MISSING_NODE
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
name|MoveDetector
operator|.
name|SOURCE_PATH
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|EventIterator
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
name|core
operator|.
name|ImmutableTree
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
name|namepath
operator|.
name|NamePathMapper
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
name|observation
operator|.
name|filter
operator|.
name|EventFilter
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
name|observation
operator|.
name|filter
operator|.
name|Filters
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
name|observation
operator|.
name|filter
operator|.
name|VisibleFilter
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

begin_comment
comment|/**  * Generator of a traversable view of events.  */
end_comment

begin_class
specifier|public
class|class
name|EventGenerator
implements|implements
name|EventIterator
block|{
specifier|private
specifier|final
name|EventContext
name|context
decl_stmt|;
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|Event
argument_list|>
name|events
init|=
name|newLinkedList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|Runnable
argument_list|>
name|generators
init|=
name|newLinkedList
argument_list|()
decl_stmt|;
specifier|private
name|long
name|position
init|=
literal|0
decl_stmt|;
comment|/**      * Create a new instance of a {@code EventGenerator} reporting events to the      * passed {@code listener} after filtering with the passed {@code filter}.      *      * @param filter filter for filtering changes      */
specifier|public
name|EventGenerator
parameter_list|(
annotation|@
name|Nonnull
name|NamePathMapper
name|namePathMapper
parameter_list|,
name|CommitInfo
name|info
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|before
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|after
parameter_list|,
annotation|@
name|Nonnull
name|String
name|basePath
parameter_list|,
annotation|@
name|Nonnull
name|EventFilter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
operator|new
name|EventContext
argument_list|(
name|namePathMapper
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|filter
operator|=
name|Filters
operator|.
name|all
argument_list|(
operator|new
name|VisibleFilter
argument_list|()
argument_list|,
name|checkNotNull
argument_list|(
name|filter
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|EventDiff
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|basePath
argument_list|,
name|filter
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|private
class|class
name|EventDiff
implements|implements
name|NodeStateDiff
implements|,
name|Runnable
block|{
comment|/**          * The diff handler of the parent node, or {@code null} for the root.          */
specifier|private
specifier|final
name|EventDiff
name|parent
decl_stmt|;
comment|/**          * The name of this node, or the empty string for the root.          */
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**          * Before state, or {@code MISSING_NODE} if this node was added.          */
specifier|private
specifier|final
name|NodeState
name|before
decl_stmt|;
comment|/**          * After state, or {@code MISSING_NODE} if this node was removed.          */
specifier|private
specifier|final
name|NodeState
name|after
decl_stmt|;
comment|/**          * Filter for selecting which events to produce.          */
specifier|private
specifier|final
name|EventFilter
name|filter
decl_stmt|;
specifier|private
specifier|final
name|ImmutableTree
name|beforeTree
decl_stmt|;
specifier|private
specifier|final
name|ImmutableTree
name|afterTree
decl_stmt|;
name|EventDiff
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|String
name|path
parameter_list|,
name|EventFilter
name|filter
parameter_list|)
block|{
name|String
name|name
init|=
literal|null
decl_stmt|;
name|ImmutableTree
name|btree
init|=
operator|new
name|ImmutableTree
argument_list|(
name|before
argument_list|)
decl_stmt|;
name|ImmutableTree
name|atree
init|=
operator|new
name|ImmutableTree
argument_list|(
name|after
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|element
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|name
operator|=
name|element
expr_stmt|;
name|before
operator|=
name|before
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|after
operator|=
name|after
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|btree
operator|=
operator|new
name|ImmutableTree
argument_list|(
name|btree
argument_list|,
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
name|atree
operator|=
operator|new
name|ImmutableTree
argument_list|(
name|atree
argument_list|,
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|parent
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|before
operator|=
name|before
expr_stmt|;
name|this
operator|.
name|after
operator|=
name|after
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|beforeTree
operator|=
name|btree
expr_stmt|;
name|this
operator|.
name|afterTree
operator|=
name|atree
expr_stmt|;
block|}
specifier|private
name|EventDiff
parameter_list|(
name|EventDiff
name|parent
parameter_list|,
name|EventFilter
name|filter
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
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|before
operator|=
name|before
expr_stmt|;
name|this
operator|.
name|after
operator|=
name|after
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|beforeTree
operator|=
operator|new
name|ImmutableTree
argument_list|(
name|parent
operator|.
name|beforeTree
argument_list|,
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
name|this
operator|.
name|afterTree
operator|=
operator|new
name|ImmutableTree
argument_list|(
name|parent
operator|.
name|afterTree
argument_list|,
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------< Runnable>--
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|before
operator|==
name|MISSING_NODE
condition|)
block|{
name|parent
operator|.
name|handleAddedNode
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
comment|// postponed handling of added nodes
block|}
elseif|else
if|if
condition|(
name|after
operator|==
name|MISSING_NODE
condition|)
block|{
name|parent
operator|.
name|handleDeletedNode
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
comment|// postponed handling of removed nodes
block|}
block|}
comment|// process changes below this node
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
comment|//-------------------------------------------------< NodeStateDiff>--
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
if|if
condition|(
name|filter
operator|.
name|includeAdd
argument_list|(
name|after
argument_list|)
condition|)
block|{
name|events
operator|.
name|add
argument_list|(
operator|new
name|EventImpl
argument_list|(
name|context
argument_list|,
name|PROPERTY_ADDED
argument_list|,
name|afterTree
argument_list|,
name|after
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
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
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
comment|// check for reordering of child nodes
if|if
condition|(
name|OAK_CHILD_ORDER
operator|.
name|equals
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|filter
operator|.
name|includeChange
argument_list|(
name|this
operator|.
name|name
argument_list|,
name|this
operator|.
name|before
argument_list|,
name|this
operator|.
name|after
argument_list|)
condition|)
block|{
name|handleReorderedNodes
argument_list|(
name|before
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
argument_list|,
name|after
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filter
operator|.
name|includeChange
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
condition|)
block|{
name|events
operator|.
name|add
argument_list|(
operator|new
name|EventImpl
argument_list|(
name|context
argument_list|,
name|PROPERTY_CHANGED
argument_list|,
name|afterTree
argument_list|,
name|after
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
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
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|.
name|includeDelete
argument_list|(
name|before
argument_list|)
condition|)
block|{
name|events
operator|.
name|add
argument_list|(
operator|new
name|EventImpl
argument_list|(
name|context
argument_list|,
name|PROPERTY_REMOVED
argument_list|,
name|beforeTree
argument_list|,
name|before
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
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
operator|!
name|addChildEventGenerator
argument_list|(
name|name
argument_list|,
name|MISSING_NODE
argument_list|,
name|after
argument_list|)
condition|)
block|{
name|handleAddedNode
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
comment|// not postponed
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
name|addChildEventGenerator
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
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
if|if
condition|(
operator|!
name|addChildEventGenerator
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|MISSING_NODE
argument_list|)
condition|)
block|{
name|handleDeletedNode
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
comment|// not postponed
block|}
return|return
literal|true
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|boolean
name|addChildEventGenerator
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
name|EventFilter
name|childFilter
init|=
name|filter
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|childFilter
operator|!=
literal|null
condition|)
block|{
name|generators
operator|.
name|add
argument_list|(
operator|new
name|EventDiff
argument_list|(
name|this
argument_list|,
name|childFilter
argument_list|,
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|private
name|void
name|handleAddedNode
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|PropertyState
name|sourceProperty
init|=
name|after
operator|.
name|getProperty
argument_list|(
name|SOURCE_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceProperty
operator|!=
literal|null
condition|)
block|{
name|String
name|sourcePath
init|=
name|sourceProperty
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|includeMove
argument_list|(
name|sourcePath
argument_list|,
name|name
argument_list|,
name|after
argument_list|)
condition|)
block|{
name|ImmutableTree
name|tree
init|=
operator|new
name|ImmutableTree
argument_list|(
name|afterTree
argument_list|,
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|info
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"srcAbsPath"
argument_list|,
name|context
operator|.
name|getJcrPath
argument_list|(
name|sourcePath
argument_list|)
argument_list|,
literal|"destAbsPath"
argument_list|,
name|context
operator|.
name|getJcrPath
argument_list|(
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|events
operator|.
name|add
argument_list|(
operator|new
name|EventImpl
argument_list|(
name|context
argument_list|,
name|NODE_MOVED
argument_list|,
name|tree
argument_list|,
name|info
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|filter
operator|.
name|includeAdd
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
condition|)
block|{
name|ImmutableTree
name|tree
init|=
operator|new
name|ImmutableTree
argument_list|(
name|afterTree
argument_list|,
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|events
operator|.
name|add
argument_list|(
operator|new
name|EventImpl
argument_list|(
name|context
argument_list|,
name|NODE_ADDED
argument_list|,
name|tree
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|handleDeletedNode
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|.
name|includeDelete
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
condition|)
block|{
name|ImmutableTree
name|tree
init|=
operator|new
name|ImmutableTree
argument_list|(
name|beforeTree
argument_list|,
name|name
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|events
operator|.
name|add
argument_list|(
operator|new
name|EventImpl
argument_list|(
name|context
argument_list|,
name|NODE_REMOVED
argument_list|,
name|tree
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|handleReorderedNodes
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|before
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|after
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|afterNames
init|=
name|newArrayList
argument_list|(
name|after
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|beforeNames
init|=
name|newArrayList
argument_list|(
name|before
argument_list|)
decl_stmt|;
name|afterNames
operator|.
name|retainAll
argument_list|(
name|beforeNames
argument_list|)
expr_stmt|;
name|beforeNames
operator|.
name|retainAll
argument_list|(
name|afterNames
argument_list|)
expr_stmt|;
comment|// Selection sort beforeNames into afterNames recording the swaps as we go
for|for
control|(
name|int
name|a
init|=
literal|0
init|;
name|a
operator|<
name|afterNames
operator|.
name|size
argument_list|()
condition|;
name|a
operator|++
control|)
block|{
name|String
name|afterName
init|=
name|afterNames
operator|.
name|get
argument_list|(
name|a
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|b
init|=
name|a
init|;
name|b
operator|<
name|beforeNames
operator|.
name|size
argument_list|()
condition|;
name|b
operator|++
control|)
block|{
name|String
name|beforeName
init|=
name|beforeNames
operator|.
name|get
argument_list|(
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
name|b
operator|&&
name|beforeName
operator|.
name|equals
argument_list|(
name|afterName
argument_list|)
condition|)
block|{
name|beforeNames
operator|.
name|set
argument_list|(
name|b
argument_list|,
name|beforeNames
operator|.
name|get
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
name|beforeNames
operator|.
name|set
argument_list|(
name|a
argument_list|,
name|beforeName
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|info
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"srcChildRelPath"
argument_list|,
name|context
operator|.
name|getJcrName
argument_list|(
name|beforeNames
operator|.
name|get
argument_list|(
name|a
argument_list|)
argument_list|)
argument_list|,
literal|"destChildRelPath"
argument_list|,
name|context
operator|.
name|getJcrName
argument_list|(
name|beforeNames
operator|.
name|get
argument_list|(
name|a
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|ImmutableTree
name|tree
init|=
name|afterTree
operator|.
name|getChild
argument_list|(
name|afterName
argument_list|)
decl_stmt|;
name|events
operator|.
name|add
argument_list|(
operator|new
name|EventImpl
argument_list|(
name|context
argument_list|,
name|NODE_MOVED
argument_list|,
name|tree
argument_list|,
name|info
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|//-----------------------------------------------------< EventIterator>--
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|()
block|{
if|if
condition|(
name|generators
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|position
operator|+
name|events
operator|.
name|size
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPosition
parameter_list|()
block|{
return|return
name|position
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
while|while
condition|(
name|events
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|generators
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|generators
operator|.
name|removeFirst
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|skip
parameter_list|(
name|long
name|skipNum
parameter_list|)
block|{
while|while
condition|(
name|skipNum
operator|>
name|events
operator|.
name|size
argument_list|()
condition|)
block|{
name|position
operator|+=
name|events
operator|.
name|size
argument_list|()
expr_stmt|;
name|skipNum
operator|-=
name|events
operator|.
name|size
argument_list|()
expr_stmt|;
name|events
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// the remove below throws NoSuchElementException if there
comment|// are no more generators, which is correct as then we can't
comment|// skip over enough events
name|generators
operator|.
name|removeFirst
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
name|position
operator|+=
name|skipNum
expr_stmt|;
name|events
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
operator|(
name|int
operator|)
name|skipNum
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Event
name|nextEvent
parameter_list|()
block|{
if|if
condition|(
name|hasNext
argument_list|()
condition|)
block|{
name|position
operator|++
expr_stmt|;
return|return
name|events
operator|.
name|removeFirst
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Event
name|next
parameter_list|()
block|{
return|return
name|nextEvent
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

