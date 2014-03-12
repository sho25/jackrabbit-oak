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
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MILLISECONDS
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
name|observation
operator|.
name|filter
operator|.
name|GlobbingPathFilter
operator|.
name|STAR
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
name|observation
operator|.
name|filter
operator|.
name|GlobbingPathFilter
operator|.
name|STAR_STAR
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|UnsupportedRepositoryOperationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NoSuchNodeTypeException
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
name|EventJournal
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
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|EventListenerIterator
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
name|ObservationManager
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
name|EventListenerIteratorAdapter
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
name|jcr
operator|.
name|delegate
operator|.
name|SessionDelegate
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
name|jcr
operator|.
name|session
operator|.
name|SessionContext
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
name|nodetype
operator|.
name|ReadOnlyNodeTypeManager
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
name|CommitRateLimiter
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
name|ExcludeExternal
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
name|FilterBuilder
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
name|FilterProvider
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
name|Selectors
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
name|stats
operator|.
name|StatisticManager
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Marker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|MarkerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|ObservationManagerImpl
implements|implements
name|ObservationManager
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
name|ObservationManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STOP_TIME_OUT
init|=
literal|1000
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Marker
name|OBSERVATION
init|=
name|MarkerFactory
operator|.
name|getMarker
argument_list|(
literal|"observation"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Marker
name|DEPRECATED
init|=
name|MarkerFactory
operator|.
name|getMarker
argument_list|(
literal|"deprecated"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|EventListener
argument_list|,
name|ChangeProcessor
argument_list|>
name|processors
init|=
operator|new
name|HashMap
argument_list|<
name|EventListener
argument_list|,
name|ChangeProcessor
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|private
specifier|final
name|ReadOnlyNodeTypeManager
name|ntMgr
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
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|private
specifier|final
name|StatisticManager
name|statisticManager
decl_stmt|;
specifier|private
specifier|final
name|int
name|queueLength
decl_stmt|;
specifier|private
specifier|final
name|CommitRateLimiter
name|commitRateLimiter
decl_stmt|;
comment|/**      * Create a new instance based on a {@link ContentSession} that needs to implement      * {@link Observable}.      *      * @param sessionContext   session delegate of the session in whose context this observation manager      *                         operates.      * @param nodeTypeManager  node type manager for the content session      * @param whiteboard      * @throws IllegalArgumentException if {@code contentSession} doesn't implement {@code Observable}.      */
specifier|public
name|ObservationManagerImpl
parameter_list|(
name|SessionContext
name|sessionContext
parameter_list|,
name|ReadOnlyNodeTypeManager
name|nodeTypeManager
parameter_list|,
name|PermissionProvider
name|permissionProvider
parameter_list|,
name|Whiteboard
name|whiteboard
parameter_list|,
name|int
name|queueLength
parameter_list|,
name|CommitRateLimiter
name|commitRateLimiter
parameter_list|)
block|{
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionContext
operator|.
name|getSessionDelegate
argument_list|()
expr_stmt|;
name|this
operator|.
name|ntMgr
operator|=
name|nodeTypeManager
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
name|sessionContext
expr_stmt|;
name|this
operator|.
name|whiteboard
operator|=
name|whiteboard
expr_stmt|;
name|this
operator|.
name|statisticManager
operator|=
name|sessionContext
operator|.
name|getStatisticManager
argument_list|()
expr_stmt|;
name|this
operator|.
name|queueLength
operator|=
name|queueLength
expr_stmt|;
name|this
operator|.
name|commitRateLimiter
operator|=
name|commitRateLimiter
expr_stmt|;
block|}
specifier|public
name|void
name|dispose
parameter_list|()
block|{
name|List
argument_list|<
name|ChangeProcessor
argument_list|>
name|toBeStopped
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|toBeStopped
operator|=
name|newArrayList
argument_list|(
name|processors
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|processors
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|ChangeProcessor
name|processor
range|:
name|toBeStopped
control|)
block|{
name|stop
argument_list|(
name|processor
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|addEventListener
parameter_list|(
name|EventListener
name|listener
parameter_list|,
name|ListenerTracker
name|tracker
parameter_list|,
name|FilterProvider
name|filterProvider
parameter_list|)
block|{
name|ChangeProcessor
name|processor
init|=
name|processors
operator|.
name|get
argument_list|(
name|listener
argument_list|)
decl_stmt|;
if|if
condition|(
name|processor
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|OBSERVATION
argument_list|,
literal|"Registering event listener {} with filter {}"
argument_list|,
name|listener
argument_list|,
name|filterProvider
argument_list|)
expr_stmt|;
name|processor
operator|=
operator|new
name|ChangeProcessor
argument_list|(
name|sessionDelegate
operator|.
name|getContentSession
argument_list|()
argument_list|,
name|namePathMapper
argument_list|,
name|permissionProvider
argument_list|,
name|tracker
argument_list|,
name|filterProvider
argument_list|,
name|statisticManager
argument_list|,
name|queueLength
argument_list|,
name|commitRateLimiter
argument_list|)
expr_stmt|;
name|processors
operator|.
name|put
argument_list|(
name|listener
argument_list|,
name|processor
argument_list|)
expr_stmt|;
name|processor
operator|.
name|start
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|OBSERVATION
argument_list|,
literal|"Changing event listener {} to filter {}"
argument_list|,
name|listener
argument_list|,
name|filterProvider
argument_list|)
expr_stmt|;
name|processor
operator|.
name|setFilterProvider
argument_list|(
name|filterProvider
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Adds an event listener that listens for the events specified      * by the {@code filterProvider} passed to this method.      *<p>      * The set of events will be further filtered by the access rights      * of the current {@code Session}.      *<p>      * The filters of an already-registered {@code EventListener} can be      * changed at runtime by re-registering the same {@code EventListener}      * object (i.e. the same actual Java object) with a new filter provider.      * The implementation must ensure that no events are lost during the      * changeover.      *      * @param listener        an {@link EventListener} object.      * @param filterProvider  filter provider specifying the filter for this listener      */
specifier|public
name|void
name|addEventListener
parameter_list|(
name|EventListener
name|listener
parameter_list|,
name|FilterProvider
name|filterProvider
parameter_list|)
block|{
comment|// FIXME Add support for FilterProvider in ListenerTracker
name|ListenerTracker
name|tracker
init|=
operator|new
name|ListenerTracker
argument_list|(
name|listener
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|warn
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|DEPRECATED
argument_list|,
name|message
argument_list|,
name|initStackTrace
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|beforeEventDelivery
parameter_list|()
block|{
name|sessionDelegate
operator|.
name|refreshAtNextAccess
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|addEventListener
argument_list|(
name|listener
argument_list|,
name|tracker
argument_list|,
name|filterProvider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addEventListener
parameter_list|(
name|EventListener
name|listener
parameter_list|,
name|int
name|eventTypes
parameter_list|,
name|String
name|absPath
parameter_list|,
name|boolean
name|isDeep
parameter_list|,
name|String
index|[]
name|uuids
parameter_list|,
name|String
index|[]
name|nodeTypeName
parameter_list|,
name|boolean
name|noLocal
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|FilterBuilder
name|filterBuilder
init|=
operator|new
name|FilterBuilder
argument_list|()
decl_stmt|;
name|filterBuilder
operator|.
name|basePath
argument_list|(
name|namePathMapper
operator|.
name|getOakPath
argument_list|(
name|absPath
argument_list|)
argument_list|)
operator|.
name|includeSessionLocal
argument_list|(
operator|!
name|noLocal
argument_list|)
operator|.
name|includeClusterExternal
argument_list|(
operator|!
operator|(
name|listener
operator|instanceof
name|ExcludeExternal
operator|)
argument_list|)
operator|.
name|condition
argument_list|(
name|filterBuilder
operator|.
name|all
argument_list|(
name|filterBuilder
operator|.
name|deleteSubtree
argument_list|()
argument_list|,
name|filterBuilder
operator|.
name|moveSubtree
argument_list|()
argument_list|,
name|filterBuilder
operator|.
name|path
argument_list|(
name|isDeep
condition|?
name|STAR_STAR
else|:
name|STAR
argument_list|)
argument_list|,
name|filterBuilder
operator|.
name|eventType
argument_list|(
name|eventTypes
argument_list|)
argument_list|,
name|filterBuilder
operator|.
name|uuid
argument_list|(
name|Selectors
operator|.
name|PARENT
argument_list|,
name|uuids
argument_list|)
argument_list|,
name|filterBuilder
operator|.
name|nodeType
argument_list|(
name|Selectors
operator|.
name|PARENT
argument_list|,
name|validateNodeTypeNames
argument_list|(
name|nodeTypeName
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ListenerTracker
name|tracker
init|=
operator|new
name|ListenerTracker
argument_list|(
name|listener
argument_list|,
name|eventTypes
argument_list|,
name|absPath
argument_list|,
name|isDeep
argument_list|,
name|uuids
argument_list|,
name|nodeTypeName
argument_list|,
name|noLocal
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|warn
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|DEPRECATED
argument_list|,
name|message
argument_list|,
name|initStackTrace
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|beforeEventDelivery
parameter_list|()
block|{
name|sessionDelegate
operator|.
name|refreshAtNextAccess
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|addEventListener
argument_list|(
name|listener
argument_list|,
name|tracker
argument_list|,
name|filterBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeEventListener
parameter_list|(
name|EventListener
name|listener
parameter_list|)
block|{
name|ChangeProcessor
name|processor
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|processor
operator|=
name|processors
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|processor
operator|!=
literal|null
condition|)
block|{
name|stop
argument_list|(
name|processor
argument_list|)
expr_stmt|;
comment|// needs to happen outside synchronization
block|}
block|}
annotation|@
name|Override
specifier|public
name|EventListenerIterator
name|getRegisteredEventListeners
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|EventListenerIteratorAdapter
argument_list|(
name|processors
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setUserData
parameter_list|(
annotation|@
name|Nullable
name|String
name|userData
parameter_list|)
block|{
name|sessionDelegate
operator|.
name|setUserData
argument_list|(
name|userData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|EventJournal
name|getEventJournal
parameter_list|()
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|EventJournal
name|getEventJournal
parameter_list|(
name|int
name|eventTypes
parameter_list|,
name|String
name|absPath
parameter_list|,
name|boolean
name|isDeep
parameter_list|,
name|String
index|[]
name|uuid
parameter_list|,
name|String
index|[]
name|nodeTypeName
parameter_list|)
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|()
throw|;
block|}
comment|//------------------------------------------------------------< private>---
comment|/**      * Validates the given node type names.      *      * @param nodeTypeNames the node type names.      * @return the node type names as oak names.      * @throws javax.jcr.nodetype.NoSuchNodeTypeException if one of the node type names refers to      *                                 an non-existing node type.      * @throws javax.jcr.RepositoryException     if an error occurs while reading from the      *                                 node type manager.      */
annotation|@
name|CheckForNull
specifier|private
name|String
index|[]
name|validateNodeTypeNames
parameter_list|(
annotation|@
name|Nullable
name|String
index|[]
name|nodeTypeNames
parameter_list|)
throws|throws
name|NoSuchNodeTypeException
throws|,
name|RepositoryException
block|{
if|if
condition|(
name|nodeTypeNames
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
index|[]
name|oakNames
init|=
operator|new
name|String
index|[
name|nodeTypeNames
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodeTypeNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ntMgr
operator|.
name|getNodeType
argument_list|(
name|nodeTypeNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|oakNames
index|[
name|i
index|]
operator|=
name|namePathMapper
operator|.
name|getOakName
argument_list|(
name|nodeTypeNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|oakNames
return|;
block|}
specifier|private
specifier|static
name|void
name|stop
parameter_list|(
name|ChangeProcessor
name|processor
parameter_list|)
block|{
if|if
condition|(
operator|!
name|processor
operator|.
name|stopAndWait
argument_list|(
name|STOP_TIME_OUT
argument_list|,
name|MILLISECONDS
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|OBSERVATION
argument_list|,
literal|"Timed out waiting for change processor to stop after "
operator|+
name|STOP_TIME_OUT
operator|+
literal|" milliseconds. Falling back to asynchronous stop."
argument_list|)
expr_stmt|;
name|processor
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

