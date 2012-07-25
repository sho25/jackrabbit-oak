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
name|jcr
operator|.
name|observation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|TimerTask
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|EventListener
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
name|base
operator|.
name|Function
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
name|Iterators
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
name|commons
operator|.
name|iterator
operator|.
name|EventIteratorAdapter
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
name|ChangeExtractor
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
name|NodeStateUtils
import|;
end_import

begin_class
class|class
name|ChangeProcessor
extends|extends
name|TimerTask
block|{
specifier|private
specifier|final
name|ObservationManagerImpl
name|observationManager
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|ChangeExtractor
name|changeExtractor
decl_stmt|;
specifier|private
specifier|final
name|EventListener
name|listener
decl_stmt|;
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|ChangeFilter
argument_list|>
name|filterRef
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|stopped
decl_stmt|;
specifier|public
name|ChangeProcessor
parameter_list|(
name|ObservationManagerImpl
name|observationManager
parameter_list|,
name|EventListener
name|listener
parameter_list|,
name|ChangeFilter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|observationManager
operator|=
name|observationManager
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|observationManager
operator|.
name|getNamePathMapper
argument_list|()
expr_stmt|;
name|this
operator|.
name|changeExtractor
operator|=
name|observationManager
operator|.
name|getChangeExtractor
argument_list|()
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|filterRef
operator|=
operator|new
name|AtomicReference
argument_list|<
name|ChangeFilter
argument_list|>
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setFilter
parameter_list|(
name|ChangeFilter
name|filter
parameter_list|)
block|{
name|filterRef
operator|.
name|set
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|cancel
argument_list|()
expr_stmt|;
name|stopped
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|EventGeneratingNodeStateDiff
name|diff
init|=
operator|new
name|EventGeneratingNodeStateDiff
argument_list|()
decl_stmt|;
name|changeExtractor
operator|.
name|getChanges
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|observationManager
operator|.
name|setHasEvents
argument_list|()
expr_stmt|;
name|diff
operator|.
name|sendEvents
argument_list|()
expr_stmt|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
class|class
name|EventGeneratingNodeStateDiff
implements|implements
name|NodeStateDiff
block|{
specifier|public
specifier|static
specifier|final
name|int
name|PURGE_LIMIT
init|=
literal|8192
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|associatedParentNode
decl_stmt|;
specifier|private
name|int
name|childNodeCount
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Iterator
argument_list|<
name|Event
argument_list|>
argument_list|>
name|events
decl_stmt|;
name|EventGeneratingNodeStateDiff
parameter_list|(
name|String
name|path
parameter_list|,
name|List
argument_list|<
name|Iterator
argument_list|<
name|Event
argument_list|>
argument_list|>
name|events
parameter_list|,
name|NodeState
name|associatedParentNode
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|associatedParentNode
operator|=
name|associatedParentNode
expr_stmt|;
name|this
operator|.
name|events
operator|=
name|events
expr_stmt|;
block|}
specifier|public
name|EventGeneratingNodeStateDiff
parameter_list|()
block|{
name|this
argument_list|(
literal|"/"
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Iterator
argument_list|<
name|Event
argument_list|>
argument_list|>
argument_list|(
name|PURGE_LIMIT
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|sendEvents
parameter_list|()
block|{
name|Iterator
argument_list|<
name|Event
argument_list|>
name|eventIt
init|=
name|Iterators
operator|.
name|concat
argument_list|(
name|events
operator|.
name|iterator
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|eventIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|listener
operator|.
name|onEvent
argument_list|(
operator|new
name|EventIteratorAdapter
argument_list|(
name|eventIt
argument_list|)
argument_list|)
expr_stmt|;
name|events
operator|=
operator|new
name|ArrayList
argument_list|<
name|Iterator
argument_list|<
name|Event
argument_list|>
argument_list|>
argument_list|(
name|PURGE_LIMIT
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|jcrPath
parameter_list|()
block|{
return|return
name|namePathMapper
operator|.
name|getJcrPath
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|stopped
operator|&&
name|filterRef
operator|.
name|get
argument_list|()
operator|.
name|include
argument_list|(
name|Event
operator|.
name|PROPERTY_ADDED
argument_list|,
name|jcrPath
argument_list|()
argument_list|,
name|associatedParentNode
argument_list|)
condition|)
block|{
name|Event
name|event
init|=
name|generatePropertyEvent
argument_list|(
name|Event
operator|.
name|PROPERTY_ADDED
argument_list|,
name|path
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|events
operator|.
name|add
argument_list|(
name|Iterators
operator|.
name|singletonIterator
argument_list|(
name|event
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|stopped
operator|&&
name|filterRef
operator|.
name|get
argument_list|()
operator|.
name|include
argument_list|(
name|Event
operator|.
name|PROPERTY_CHANGED
argument_list|,
name|jcrPath
argument_list|()
argument_list|,
name|associatedParentNode
argument_list|)
condition|)
block|{
name|Event
name|event
init|=
name|generatePropertyEvent
argument_list|(
name|Event
operator|.
name|PROPERTY_CHANGED
argument_list|,
name|path
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|events
operator|.
name|add
argument_list|(
name|Iterators
operator|.
name|singletonIterator
argument_list|(
name|event
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
if|if
condition|(
operator|!
name|stopped
operator|&&
name|filterRef
operator|.
name|get
argument_list|()
operator|.
name|include
argument_list|(
name|Event
operator|.
name|PROPERTY_REMOVED
argument_list|,
name|jcrPath
argument_list|()
argument_list|,
name|associatedParentNode
argument_list|)
condition|)
block|{
name|Event
name|event
init|=
name|generatePropertyEvent
argument_list|(
name|Event
operator|.
name|PROPERTY_REMOVED
argument_list|,
name|path
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|events
operator|.
name|add
argument_list|(
name|Iterators
operator|.
name|singletonIterator
argument_list|(
name|event
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|stopped
operator|&&
name|filterRef
operator|.
name|get
argument_list|()
operator|.
name|includeChildren
argument_list|(
name|jcrPath
argument_list|()
argument_list|)
condition|)
block|{
name|Iterator
argument_list|<
name|Event
argument_list|>
name|events
init|=
name|generateNodeEvents
argument_list|(
name|Event
operator|.
name|NODE_ADDED
argument_list|,
name|path
argument_list|,
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|this
operator|.
name|events
operator|.
name|add
argument_list|(
name|events
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|childNodeCount
operator|>
name|PURGE_LIMIT
condition|)
block|{
name|sendEvents
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|stopped
operator|&&
name|filterRef
operator|.
name|get
argument_list|()
operator|.
name|includeChildren
argument_list|(
name|jcrPath
argument_list|()
argument_list|)
condition|)
block|{
name|Iterator
argument_list|<
name|Event
argument_list|>
name|events
init|=
name|generateNodeEvents
argument_list|(
name|Event
operator|.
name|NODE_REMOVED
argument_list|,
name|path
argument_list|,
name|name
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|this
operator|.
name|events
operator|.
name|add
argument_list|(
name|events
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
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|stopped
operator|&&
name|filterRef
operator|.
name|get
argument_list|()
operator|.
name|includeChildren
argument_list|(
name|jcrPath
argument_list|()
argument_list|)
condition|)
block|{
name|EventGeneratingNodeStateDiff
name|diff
init|=
operator|new
name|EventGeneratingNodeStateDiff
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
argument_list|,
name|events
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
if|if
condition|(
name|events
operator|.
name|size
argument_list|()
operator|>
name|PURGE_LIMIT
condition|)
block|{
name|diff
operator|.
name|sendEvents
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|Event
name|generatePropertyEvent
parameter_list|(
name|int
name|eventType
parameter_list|,
name|String
name|parentPath
parameter_list|,
name|PropertyState
name|property
parameter_list|)
block|{
name|String
name|jcrPath
init|=
name|namePathMapper
operator|.
name|getJcrPath
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentPath
argument_list|,
name|property
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// TODO support userId, identifier, info, date
return|return
operator|new
name|EventImpl
argument_list|(
name|eventType
argument_list|,
name|jcrPath
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|private
name|Iterator
argument_list|<
name|Event
argument_list|>
name|generateNodeEvents
parameter_list|(
name|int
name|eventType
parameter_list|,
name|String
name|parentPath
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|node
parameter_list|)
block|{
name|ChangeFilter
name|filter
init|=
name|filterRef
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|String
name|path
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentPath
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|String
name|jcrParentPath
init|=
name|namePathMapper
operator|.
name|getJcrPath
argument_list|(
name|parentPath
argument_list|)
decl_stmt|;
name|String
name|jcrPath
init|=
name|namePathMapper
operator|.
name|getJcrPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Event
argument_list|>
name|nodeEvent
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|include
argument_list|(
name|eventType
argument_list|,
name|jcrParentPath
argument_list|,
name|associatedParentNode
argument_list|)
condition|)
block|{
comment|// TODO support userId, identifier, info, date
name|Event
name|event
init|=
operator|new
name|EventImpl
argument_list|(
name|eventType
argument_list|,
name|jcrPath
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|nodeEvent
operator|=
name|Iterators
operator|.
name|singletonIterator
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nodeEvent
operator|=
name|Iterators
operator|.
name|emptyIterator
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|propertyEventType
init|=
name|eventType
operator|==
name|Event
operator|.
name|NODE_ADDED
condition|?
name|Event
operator|.
name|PROPERTY_ADDED
else|:
name|Event
operator|.
name|PROPERTY_REMOVED
decl_stmt|;
name|Iterator
argument_list|<
name|Event
argument_list|>
name|propertyEvents
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|include
argument_list|(
name|propertyEventType
argument_list|,
name|jcrPath
argument_list|,
name|associatedParentNode
argument_list|)
condition|)
block|{
name|propertyEvents
operator|=
name|Iterators
operator|.
name|transform
argument_list|(
name|node
operator|.
name|getProperties
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|PropertyState
argument_list|,
name|Event
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Event
name|apply
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|generatePropertyEvent
argument_list|(
name|propertyEventType
argument_list|,
name|path
argument_list|,
name|property
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|propertyEvents
operator|=
name|Iterators
operator|.
name|emptyIterator
argument_list|()
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|Event
argument_list|>
name|childNodeEvents
init|=
operator|!
name|stopped
operator|&&
name|filter
operator|.
name|includeChildren
argument_list|(
name|jcrPath
argument_list|)
condition|?
name|Iterators
operator|.
name|concat
argument_list|(
name|generateChildEvents
argument_list|(
name|eventType
argument_list|,
name|path
argument_list|,
name|node
argument_list|)
argument_list|)
else|:
name|Iterators
operator|.
expr|<
name|Event
operator|>
name|emptyIterator
argument_list|()
decl_stmt|;
return|return
name|Iterators
operator|.
name|concat
argument_list|(
name|nodeEvent
argument_list|,
name|propertyEvents
argument_list|,
name|childNodeEvents
argument_list|)
return|;
block|}
specifier|private
name|Iterator
argument_list|<
name|Iterator
argument_list|<
name|Event
argument_list|>
argument_list|>
name|generateChildEvents
parameter_list|(
specifier|final
name|int
name|eventType
parameter_list|,
specifier|final
name|String
name|parentPath
parameter_list|,
name|NodeState
name|node
parameter_list|)
block|{
return|return
name|Iterators
operator|.
name|transform
argument_list|(
name|node
operator|.
name|getChildNodeEntries
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|ChildNodeEntry
argument_list|,
name|Iterator
argument_list|<
name|Event
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Event
argument_list|>
name|apply
parameter_list|(
name|ChildNodeEntry
name|entry
parameter_list|)
block|{
return|return
name|generateNodeEvents
argument_list|(
name|eventType
argument_list|,
name|parentPath
argument_list|,
name|entry
operator|.
name|getName
argument_list|()
argument_list|,
name|entry
operator|.
name|getNodeState
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

