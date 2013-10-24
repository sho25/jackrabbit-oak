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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterators
operator|.
name|emptyIterator
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
name|Iterators
operator|.
name|singletonIterator
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
name|Iterators
operator|.
name|transform
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
name|plugins
operator|.
name|identifier
operator|.
name|IdentifierManager
operator|.
name|getIdentifier
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
name|Tree
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
name|ImmutableRoot
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
specifier|public
class|class
name|ChangeProcessor
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
name|ListenerTracker
name|tracker
decl_stmt|;
specifier|private
specifier|final
name|EventListener
name|listener
decl_stmt|;
comment|/**      * Background thread used to wait for and deliver events.      */
specifier|private
name|ListenerThread
name|thread
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
name|thread
operator|==
literal|null
argument_list|,
literal|"Change processor started already"
argument_list|)
expr_stmt|;
name|thread
operator|=
operator|new
name|ListenerThread
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setPriority
argument_list|(
name|Thread
operator|.
name|MIN_PRIORITY
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * Stop this change processor if running. After returning from this methods no further      * events will be delivered.      * @throws IllegalStateException if not yet started or stopped already      */
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
name|checkState
argument_list|(
name|thread
operator|!=
literal|null
argument_list|,
literal|"Change processor not started"
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
operator|!
name|stopping
argument_list|,
literal|"Change processor already stopped"
argument_list|)
expr_stmt|;
name|stopping
operator|=
literal|true
expr_stmt|;
block|}
specifier|private
specifier|static
name|ImmutableTree
name|getTree
parameter_list|(
name|NodeState
name|beforeState
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|ImmutableRoot
argument_list|(
name|beforeState
argument_list|)
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
class|class
name|ListenerThread
extends|extends
name|Thread
block|{
specifier|private
specifier|final
name|Listener
name|changeListener
init|=
operator|(
operator|(
name|Observable
operator|)
name|contentSession
operator|)
operator|.
name|newListener
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Registration
name|mbean
decl_stmt|;
name|ListenerThread
parameter_list|(
name|Whiteboard
name|whiteboard
parameter_list|)
block|{
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
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|ChangeSet
name|changes
init|=
name|changeListener
operator|.
name|getChanges
argument_list|(
literal|100
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|stopping
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
comment|// FIXME don't rely on toString for session id
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
operator|.
name|toString
argument_list|()
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
name|ImmutableTree
name|beforeTree
init|=
name|getTree
argument_list|(
name|changes
operator|.
name|getBeforeState
argument_list|()
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|ImmutableTree
name|afterTree
init|=
name|getTree
argument_list|(
name|changes
operator|.
name|getAfterState
argument_list|()
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|EventGeneratingNodeStateDiff
name|diff
init|=
operator|new
name|EventGeneratingNodeStateDiff
argument_list|(
name|changes
operator|.
name|getCommitInfo
argument_list|()
argument_list|,
name|beforeTree
argument_list|,
name|afterTree
argument_list|)
decl_stmt|;
name|SecureNodeStateDiff
operator|.
name|compare
argument_list|(
name|VisibleDiff
operator|.
name|wrap
argument_list|(
name|diff
argument_list|)
argument_list|,
name|beforeTree
argument_list|,
name|afterTree
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
argument_list|(
literal|100
argument_list|)
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
name|debug
argument_list|(
literal|"Error while dispatching observation events"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|mbean
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
block|}
block|}
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
name|EVENT_LIMIT
init|=
literal|8192
decl_stmt|;
specifier|private
specifier|final
name|String
name|userId
decl_stmt|;
specifier|private
specifier|final
name|String
name|message
decl_stmt|;
specifier|private
specifier|final
name|long
name|timestamp
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|external
decl_stmt|;
specifier|private
specifier|final
name|Tree
name|beforeTree
decl_stmt|;
specifier|private
specifier|final
name|Tree
name|afterTree
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
name|eventCount
decl_stmt|;
name|EventGeneratingNodeStateDiff
parameter_list|(
name|CommitInfo
name|info
parameter_list|,
name|Tree
name|beforeTree
parameter_list|,
name|Tree
name|afterTree
parameter_list|)
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|userId
operator|=
name|info
operator|.
name|getUserId
argument_list|()
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|info
operator|.
name|getMessage
argument_list|()
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|info
operator|.
name|getDate
argument_list|()
expr_stmt|;
name|this
operator|.
name|external
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|userId
operator|=
name|CommitInfo
operator|.
name|OAK_UNKNOWN
expr_stmt|;
name|this
operator|.
name|message
operator|=
literal|null
expr_stmt|;
comment|// we can't tell exactly when external changes were committed,
comment|// so we just use a rough estimate like this
name|this
operator|.
name|timestamp
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|this
operator|.
name|external
operator|=
literal|true
expr_stmt|;
block|}
name|this
operator|.
name|beforeTree
operator|=
name|beforeTree
expr_stmt|;
name|this
operator|.
name|afterTree
operator|=
name|afterTree
expr_stmt|;
name|this
operator|.
name|events
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
block|}
specifier|private
name|EventGeneratingNodeStateDiff
parameter_list|(
name|EventGeneratingNodeStateDiff
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|userId
operator|=
name|parent
operator|.
name|userId
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|parent
operator|.
name|message
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|parent
operator|.
name|timestamp
expr_stmt|;
name|this
operator|.
name|external
operator|=
name|parent
operator|.
name|external
expr_stmt|;
name|this
operator|.
name|beforeTree
operator|=
name|parent
operator|.
name|beforeTree
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|afterTree
operator|=
name|parent
operator|.
name|afterTree
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|events
operator|=
name|parent
operator|.
name|events
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
try|try
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
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unhandled exception in observation listener: "
operator|+
name|listener
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
name|EVENT_LIMIT
argument_list|)
expr_stmt|;
block|}
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
name|PROPERTY_ADDED
argument_list|,
name|afterTree
argument_list|)
condition|)
block|{
name|Event
name|event
init|=
name|generatePropertyEvent
argument_list|(
name|PROPERTY_ADDED
argument_list|,
name|afterTree
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|events
operator|.
name|add
argument_list|(
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
name|afterTree
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
name|afterTree
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|events
operator|.
name|add
argument_list|(
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
name|PROPERTY_REMOVED
argument_list|,
name|afterTree
argument_list|)
condition|)
block|{
name|Event
name|event
init|=
name|generatePropertyEvent
argument_list|(
name|PROPERTY_REMOVED
argument_list|,
name|beforeTree
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|events
operator|.
name|add
argument_list|(
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
name|afterTree
operator|.
name|getPath
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
name|NODE_ADDED
argument_list|,
name|afterTree
operator|.
name|getChild
argument_list|(
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
name|eventCount
operator|>
name|EVENT_LIMIT
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
name|beforeTree
operator|.
name|getPath
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
name|NODE_REMOVED
argument_list|,
name|beforeTree
operator|.
name|getChild
argument_list|(
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
name|afterTree
operator|.
name|getPath
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
name|path
parameter_list|,
name|String
name|id
parameter_list|)
block|{
return|return
operator|new
name|EventImpl
argument_list|(
name|eventType
argument_list|,
name|namePathMapper
operator|.
name|getJcrPath
argument_list|(
name|path
argument_list|)
argument_list|,
name|userId
argument_list|,
name|id
argument_list|,
literal|null
comment|/* TODO: info map */
argument_list|,
name|timestamp
argument_list|,
name|message
argument_list|,
name|external
argument_list|)
return|;
block|}
specifier|private
name|Event
name|generatePropertyEvent
parameter_list|(
name|int
name|eventType
parameter_list|,
name|Tree
name|parent
parameter_list|,
name|PropertyState
name|property
parameter_list|)
block|{
name|String
name|path
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|property
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|createEvent
argument_list|(
name|eventType
argument_list|,
name|path
argument_list|,
name|getIdentifier
argument_list|(
name|parent
argument_list|)
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
specifier|final
name|Tree
name|tree
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
name|tree
operator|.
name|isRoot
argument_list|()
condition|?
literal|null
else|:
name|tree
operator|.
name|getParent
argument_list|()
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
name|tree
operator|.
name|getPath
argument_list|()
argument_list|,
name|getIdentifier
argument_list|(
name|tree
argument_list|)
argument_list|)
decl_stmt|;
name|nodeEvent
operator|=
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
name|NODE_ADDED
condition|?
name|PROPERTY_ADDED
else|:
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
name|tree
argument_list|)
condition|)
block|{
name|propertyEvents
operator|=
name|transform
argument_list|(
name|tree
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
name|tree
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
name|tree
operator|.
name|getPath
argument_list|()
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
name|tree
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
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|transform
argument_list|(
name|tree
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|Tree
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
name|Tree
name|child
parameter_list|)
block|{
return|return
name|generateNodeEvents
argument_list|(
name|eventType
argument_list|,
name|child
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

