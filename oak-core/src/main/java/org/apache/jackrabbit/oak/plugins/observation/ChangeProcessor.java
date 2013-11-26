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
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|annotation
operator|.
name|Nullable
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
name|filter
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
name|spi
operator|.
name|commit
operator|.
name|Observer
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
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|PermissionProvider
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
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|TreePermission
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
comment|/**  * A {@code ChangeProcessor} generates observation {@link javax.jcr.observation.Event}s  * based on a {@link JcrFilterProvider filter} and delivers them to an {@link EventListener}.  *<p>  * After instantiation a {@code ChangeProcessor} must be started in order to start  * delivering observation events and stopped to stop doing so.  */
end_comment

begin_class
specifier|public
class|class
name|ChangeProcessor
implements|implements
name|Observer
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
name|PermissionProvider
name|permissionProvider
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|ListenerTracker
name|tracker
decl_stmt|;
specifier|private
specifier|final
name|EventListener
name|eventListener
decl_stmt|;
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|JcrFilterProvider
argument_list|>
name|filterProvider
decl_stmt|;
specifier|private
name|Closeable
name|observer
decl_stmt|;
specifier|private
name|Registration
name|mbean
decl_stmt|;
specifier|private
name|NodeState
name|previousRoot
decl_stmt|;
specifier|public
name|ChangeProcessor
parameter_list|(
name|ContentSession
name|contentSession
parameter_list|,
name|PermissionProvider
name|permissionProvider
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|,
name|ListenerTracker
name|tracker
parameter_list|,
name|JcrFilterProvider
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
name|permissionProvider
operator|=
name|permissionProvider
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
name|eventListener
operator|=
name|tracker
operator|.
name|getTrackedListener
argument_list|()
expr_stmt|;
name|filterProvider
operator|=
operator|new
name|AtomicReference
argument_list|<
name|JcrFilterProvider
argument_list|>
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the filter for the events this change processor will generate.      * @param filter      */
specifier|public
name|void
name|setFilterProvider
parameter_list|(
name|JcrFilterProvider
name|filter
parameter_list|)
block|{
name|filterProvider
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
name|observer
operator|==
literal|null
argument_list|,
literal|"Change processor started already"
argument_list|)
expr_stmt|;
name|observer
operator|=
operator|(
operator|(
name|Observable
operator|)
name|contentSession
operator|)
operator|.
name|addObserver
argument_list|(
name|this
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
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
name|checkState
argument_list|(
name|observer
operator|!=
literal|null
argument_list|,
literal|"Change processor not started"
argument_list|)
expr_stmt|;
try|try
block|{
name|mbean
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|observer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error while stopping change listener"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|contentChanged
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|,
annotation|@
name|Nullable
name|CommitInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|previousRoot
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|JcrFilterProvider
name|provider
init|=
name|filterProvider
operator|.
name|get
argument_list|()
decl_stmt|;
comment|// FIXME don't rely on toString for session id
if|if
condition|(
name|provider
operator|.
name|includeCommit
argument_list|(
name|contentSession
operator|.
name|toString
argument_list|()
argument_list|,
name|info
argument_list|)
condition|)
block|{
name|String
name|path
init|=
name|namePathMapper
operator|.
name|getOakPath
argument_list|(
name|provider
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
name|previousRoot
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|ImmutableTree
name|afterTree
init|=
name|getTree
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|TreePermission
name|treePermission
init|=
name|getTreePermission
argument_list|(
name|afterTree
argument_list|)
decl_stmt|;
name|EventIterator
argument_list|<
name|Event
argument_list|>
name|events
init|=
operator|new
name|EventIterator
argument_list|<
name|Event
argument_list|>
argument_list|(
name|beforeTree
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|afterTree
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|path
argument_list|,
name|provider
operator|.
name|getFilter
argument_list|(
name|beforeTree
argument_list|,
name|afterTree
argument_list|,
name|treePermission
argument_list|)
argument_list|,
operator|new
name|JcrListener
argument_list|(
name|beforeTree
argument_list|,
name|afterTree
argument_list|,
name|namePathMapper
argument_list|,
name|info
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|events
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|eventListener
operator|.
name|onEvent
argument_list|(
operator|new
name|EventIteratorAdapter
argument_list|(
name|events
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|warn
argument_list|(
literal|"Error while dispatching observation events"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|previousRoot
operator|=
name|root
expr_stmt|;
block|}
specifier|private
specifier|static
name|ImmutableTree
name|getTree
parameter_list|(
name|NodeState
name|nodeState
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|ImmutableRoot
argument_list|(
name|nodeState
argument_list|)
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
return|;
block|}
specifier|private
name|TreePermission
name|getTreePermission
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
return|;
block|}
block|}
end_class

end_unit

