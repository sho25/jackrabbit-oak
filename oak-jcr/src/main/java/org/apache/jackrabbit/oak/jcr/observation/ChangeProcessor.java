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
name|jcr
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
name|checkArgument
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
name|base
operator|.
name|Preconditions
operator|.
name|checkState
import|;
end_import

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
name|base
operator|.
name|Predicate
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
name|JcrConstants
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
name|api
operator|.
name|jmx
operator|.
name|EventListenerMBean
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
name|commons
operator|.
name|observation
operator|.
name|ListenerTracker
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
name|ContentSession
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
name|plugins
operator|.
name|observation
operator|.
name|ChangeDispatcher
operator|.
name|ChangeSet
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
name|ChangeDispatcher
operator|.
name|Listener
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
name|EventImpl
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
name|Observable
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
name|RecursingNodeStateDiff
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
name|NodeStateUtils
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
name|VisibleDiff
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
name|whiteboard
operator|.
name|Registration
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
name|whiteboard
operator|.
name|Whiteboard
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
name|whiteboard
operator|.
name|WhiteboardUtils
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
comment|/**  * A {@code ChangeProcessor} generates observation {@link javax.jcr.observation.Event}s  * based on a {@link EventFilter} and delivers them to an {@link javax.jcr.observation.EventListener}.  *<p>  * After instantiation a {@code ChangeProcessor} must be started in order for its  * {@link #run()} methods to be regularly executed and stopped in order to not  * execute its run method anymore.  */
end_comment

begin_class
class|class
name|ChangeProcessor
implements|implements
name|Runnable
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ChangeProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ContentSession
name|contentSession
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|EventFilter
argument_list|>
name|filterRef
decl_stmt|;
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|String
argument_list|>
name|userDataRef
init|=
operator|new
name|AtomicReference
argument_list|<
name|String
argument_list|>
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ListenerTracker
name|tracker
decl_stmt|;
specifier|private
specifier|final
name|EventListener
name|listener
decl_stmt|;
specifier|private
specifier|volatile
name|Thread
name|running
init|=
literal|null
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|stopping
init|=
literal|false
decl_stmt|;
specifier|private
name|Runnable
name|deferredUnregister
decl_stmt|;
specifier|private
name|Registration
name|runnable
decl_stmt|;
specifier|private
name|Registration
name|mbean
decl_stmt|;
specifier|private
name|Listener
name|changeListener
decl_stmt|;
specifier|public
name|ChangeProcessor
parameter_list|(
name|ContentSession
name|contentSession
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|,
name|ListenerTracker
name|tracker
parameter_list|,
name|EventFilter
name|filter
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|contentSession
operator|instanceof
name|Observable
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentSession
operator|=
name|contentSession
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
name|this
operator|.
name|tracker
operator|=
name|tracker
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|tracker
operator|.
name|getTrackedListener
argument_list|()
expr_stmt|;
name|filterRef
operator|=
operator|new
name|AtomicReference
argument_list|<
name|EventFilter
argument_list|>
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the filter for the events this change processor will generate.      * @param filter      */
specifier|public
name|void
name|setFilter
parameter_list|(
name|EventFilter
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
comment|/**      * Set the user data to return with {@link javax.jcr.observation.Event#getUserData()}.      * @param userData      */
specifier|public
name|void
name|setUserData
parameter_list|(
name|String
name|userData
parameter_list|)
block|{
name|userDataRef
operator|.
name|set
argument_list|(
name|userData
argument_list|)
expr_stmt|;
block|}
comment|/**      * Start this change processor      * @param whiteboard  the whiteboard instance to used for scheduling individual      *                    runs of this change processor.      * @throws IllegalStateException if started already      */
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|(
name|Whiteboard
name|whiteboard
parameter_list|)
block|{
name|checkState
argument_list|(
name|runnable
operator|==
literal|null
argument_list|,
literal|"Change processor started already"
argument_list|)
expr_stmt|;
name|stopping
operator|=
literal|false
expr_stmt|;
name|changeListener
operator|=
operator|(
operator|(
name|Observable
operator|)
name|contentSession
operator|)
operator|.
name|newListener
argument_list|()
expr_stmt|;
name|runnable
operator|=
name|WhiteboardUtils
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|whiteboard
argument_list|,
name|this
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|mbean
operator|=
name|WhiteboardUtils
operator|.
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|EventListenerMBean
operator|.
name|class
argument_list|,
name|tracker
operator|.
name|getListenerMBean
argument_list|()
argument_list|,
literal|"EventListener"
argument_list|,
name|tracker
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Stop this change processor if running. After returning from this methods no further      * events will be delivered.      * @throws IllegalStateException if not yet started or stopped already      */
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|stopping
operator|=
literal|true
expr_stmt|;
comment|// do this outside synchronization
if|if
condition|(
name|running
operator|==
name|Thread
operator|.
name|currentThread
argument_list|()
condition|)
block|{
comment|// Defer stopping from event listener, defer unregistering until
comment|// event listener is done
name|deferredUnregister
operator|=
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|unregister
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
comment|// Otherwise wait for the event listener to terminate and unregister immediately
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
while|while
condition|(
name|running
operator|!=
literal|null
condition|)
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
name|unregister
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|unregister
parameter_list|()
block|{
name|checkState
argument_list|(
name|runnable
operator|!=
literal|null
argument_list|,
literal|"Change processor not started"
argument_list|)
expr_stmt|;
name|mbean
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|runnable
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|changeListener
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// guarantee that only one thread is processing changes at a time
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|running
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
else|else
block|{
name|running
operator|=
name|Thread
operator|.
name|currentThread
argument_list|()
expr_stmt|;
block|}
block|}
try|try
block|{
name|ChangeSet
name|changes
init|=
name|changeListener
operator|.
name|getChanges
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|stopping
operator|&&
name|changes
operator|!=
literal|null
condition|)
block|{
name|EventFilter
name|filter
init|=
name|filterRef
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|filter
operator|.
name|excludeLocal
argument_list|()
operator|&&
name|changes
operator|.
name|isLocal
argument_list|(
name|contentSession
argument_list|)
operator|)
condition|)
block|{
name|String
name|path
init|=
name|namePathMapper
operator|.
name|getOakPath
argument_list|(
name|filter
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|EventGeneratingNodeStateDiff
name|diff
init|=
operator|new
name|EventGeneratingNodeStateDiff
argument_list|(
name|changes
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|changes
operator|.
name|diff
argument_list|(
name|VisibleDiff
operator|.
name|wrap
argument_list|(
name|diff
argument_list|)
argument_list|,
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|stopping
condition|)
block|{
name|diff
operator|.
name|sendEvents
argument_list|()
expr_stmt|;
block|}
block|}
name|changes
operator|=
name|changeListener
operator|.
name|getChanges
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to generate or send events"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|running
operator|=
literal|null
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|notifyAll
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|deferredUnregister
operator|!=
literal|null
condition|)
block|{
name|deferredUnregister
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
class|class
name|EventGeneratingNodeStateDiff
extends|extends
name|RecursingNodeStateDiff
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
name|ChangeSet
name|changes
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|beforeParentNode
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|afterParentNode
decl_stmt|;
specifier|private
specifier|final
name|EventGeneratingNodeStateDiff
name|parent
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
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
specifier|private
name|int
name|childNodeCount
decl_stmt|;
name|EventGeneratingNodeStateDiff
parameter_list|(
name|ChangeSet
name|changes
parameter_list|,
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
name|beforeParentNode
parameter_list|,
name|NodeState
name|afterParentNode
parameter_list|,
name|EventGeneratingNodeStateDiff
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|changes
operator|=
name|changes
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|events
operator|=
name|events
expr_stmt|;
name|this
operator|.
name|beforeParentNode
operator|=
name|beforeParentNode
expr_stmt|;
name|this
operator|.
name|afterParentNode
operator|=
name|afterParentNode
expr_stmt|;
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
block|}
specifier|public
name|EventGeneratingNodeStateDiff
parameter_list|(
name|ChangeSet
name|changes
parameter_list|,
name|String
name|path
parameter_list|)
block|{
comment|// FIXME parent nodes should be the root here
name|this
argument_list|(
name|changes
argument_list|,
name|path
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
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|""
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
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|!
name|stopping
operator|&&
name|super
operator|.
name|hasNext
argument_list|()
return|;
block|}
block|}
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
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
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
name|afterParentNode
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
argument_list|,
name|getAfterId
argument_list|()
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
return|return
operator|!
name|stopping
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
if|if
condition|(
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
name|afterParentNode
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
argument_list|,
name|getAfterId
argument_list|()
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
return|return
operator|!
name|stopping
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
name|afterParentNode
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
argument_list|,
name|getBeforeId
argument_list|()
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
return|return
operator|!
name|stopping
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
argument_list|,
name|afterParentNode
argument_list|,
name|getAfterId
argument_list|(
name|after
argument_list|,
name|name
argument_list|)
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
return|return
operator|!
name|stopping
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
argument_list|,
name|beforeParentNode
argument_list|,
name|getBeforeId
argument_list|(
name|before
argument_list|,
name|name
argument_list|)
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
return|return
operator|!
name|stopping
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
return|return
operator|!
name|stopping
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RecursingNodeStateDiff
name|createChildDiff
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
name|changes
argument_list|,
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
name|before
argument_list|,
name|after
argument_list|,
name|this
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|VisibleDiff
operator|.
name|wrap
argument_list|(
name|diff
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|RecursingNodeStateDiff
operator|.
name|EMPTY
return|;
block|}
block|}
specifier|private
name|EventImpl
name|createEvent
parameter_list|(
name|int
name|eventType
parameter_list|,
name|String
name|jcrPath
parameter_list|,
name|String
name|id
parameter_list|)
block|{
comment|// TODO support info
return|return
operator|new
name|EventImpl
argument_list|(
name|eventType
argument_list|,
name|jcrPath
argument_list|,
name|changes
operator|.
name|getUserId
argument_list|()
argument_list|,
name|id
argument_list|,
literal|null
argument_list|,
name|changes
operator|.
name|getDate
argument_list|()
argument_list|,
name|userDataRef
operator|.
name|get
argument_list|()
argument_list|,
name|changes
operator|.
name|isExternal
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|String
name|getRootId
parameter_list|()
block|{
comment|// FIXME return id of root node if available
return|return
literal|"/"
return|;
block|}
specifier|private
name|String
name|getBeforeId
parameter_list|(
name|NodeState
name|childNode
parameter_list|,
name|String
name|childName
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|getRootId
argument_list|()
operator|+
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|childName
argument_list|)
return|;
block|}
name|PropertyState
name|uuid
init|=
name|childNode
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|uuid
operator|==
literal|null
condition|)
block|{
return|return
name|parent
operator|.
name|getBeforeId
argument_list|(
name|beforeParentNode
argument_list|,
name|name
argument_list|)
operator|+
literal|'/'
operator|+
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|childName
argument_list|)
return|;
block|}
return|return
name|uuid
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
specifier|private
name|String
name|getAfterId
parameter_list|(
name|NodeState
name|childNode
parameter_list|,
name|String
name|childName
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|getRootId
argument_list|()
operator|+
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|childName
argument_list|)
return|;
block|}
name|PropertyState
name|uuid
init|=
name|childNode
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|uuid
operator|==
literal|null
condition|)
block|{
return|return
name|parent
operator|.
name|getAfterId
argument_list|(
name|afterParentNode
argument_list|,
name|name
argument_list|)
operator|+
literal|'/'
operator|+
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|childName
argument_list|)
return|;
block|}
return|return
name|uuid
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
specifier|private
name|String
name|getBeforeId
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|getRootId
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|parent
operator|.
name|getBeforeId
argument_list|(
name|beforeParentNode
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
specifier|private
name|String
name|getAfterId
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|getRootId
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|parent
operator|.
name|getBeforeId
argument_list|(
name|beforeParentNode
argument_list|,
name|name
argument_list|)
return|;
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
parameter_list|,
name|String
name|id
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
return|return
name|createEvent
argument_list|(
name|eventType
argument_list|,
name|jcrPath
argument_list|,
name|id
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
name|childName
parameter_list|,
name|NodeState
name|node
parameter_list|,
name|NodeState
name|parentNode
parameter_list|,
specifier|final
name|String
name|id
parameter_list|)
block|{
name|EventFilter
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
name|childName
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
name|parentNode
argument_list|)
condition|)
block|{
name|Event
name|event
init|=
name|createEvent
argument_list|(
name|eventType
argument_list|,
name|jcrPath
argument_list|,
name|id
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
name|parentNode
argument_list|)
condition|)
block|{
name|propertyEvents
operator|=
name|Iterators
operator|.
name|transform
argument_list|(
name|Iterators
operator|.
name|filter
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
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|PropertyState
name|propertyState
parameter_list|)
block|{
return|return
operator|!
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|propertyState
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
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
argument_list|,
name|id
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
argument_list|,
name|id
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
specifier|final
name|NodeState
name|parentNode
parameter_list|,
specifier|final
name|String
name|parentId
parameter_list|)
block|{
return|return
name|Iterators
operator|.
name|transform
argument_list|(
name|Iterators
operator|.
name|filter
argument_list|(
name|parentNode
operator|.
name|getChildNodeEntries
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|ChildNodeEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|ChildNodeEntry
name|entry
parameter_list|)
block|{
return|return
operator|!
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
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
name|NodeState
name|node
init|=
name|entry
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|generateNodeEvents
argument_list|(
name|eventType
argument_list|,
name|parentPath
argument_list|,
name|name
argument_list|,
name|node
argument_list|,
name|parentNode
argument_list|,
name|getId
argument_list|(
name|parentId
argument_list|,
name|node
argument_list|,
name|name
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|String
name|getId
parameter_list|(
name|String
name|parentId
parameter_list|,
name|NodeState
name|node
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|uuid
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|uuid
operator|==
literal|null
condition|)
block|{
return|return
name|parentId
operator|+
literal|'/'
operator|+
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|uuid
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

