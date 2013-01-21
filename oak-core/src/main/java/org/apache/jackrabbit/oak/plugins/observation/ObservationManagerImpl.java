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
name|observation
package|;
end_package

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
name|Map
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
name|ScheduledExecutorService
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
name|AtomicBoolean
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|oak
operator|.
name|api
operator|.
name|Root
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
name|RootImpl
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
name|NodeTypeConstants
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
name|spi
operator|.
name|observation
operator|.
name|ChangeExtractor
import|;
end_import

begin_comment
comment|/**  * TODO document  */
end_comment

begin_class
specifier|public
class|class
name|ObservationManagerImpl
implements|implements
name|ObservationManager
block|{
specifier|private
specifier|final
name|RootImpl
name|root
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|ScheduledExecutorService
name|executor
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
name|AtomicBoolean
name|hasEvents
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ReadOnlyNodeTypeManager
name|ntMgr
decl_stmt|;
specifier|public
name|ObservationManagerImpl
parameter_list|(
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|,
name|ScheduledExecutorService
name|executor
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|root
operator|instanceof
name|RootImpl
argument_list|,
literal|"root must be of actual type RootImpl"
argument_list|)
expr_stmt|;
name|this
operator|.
name|root
operator|=
operator|(
operator|(
name|RootImpl
operator|)
name|root
operator|)
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
name|this
operator|.
name|ntMgr
operator|=
operator|new
name|NTMgr
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|dispose
parameter_list|()
block|{
for|for
control|(
name|ChangeProcessor
name|processor
range|:
name|processors
operator|.
name|values
argument_list|()
control|)
block|{
name|processor
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|processors
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * Determine whether events have been generated since the time this method has been called.      * @return  {@code true} if this {@code ObservationManager} instance has generated events      *          since the last time this method has been called, {@code false} otherwise.      */
specifier|public
name|boolean
name|hasEvents
parameter_list|()
block|{
return|return
name|hasEvents
operator|.
name|getAndSet
argument_list|(
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
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
name|uuid
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
name|ChangeFilter
name|filter
init|=
operator|new
name|ChangeFilter
argument_list|(
name|ntMgr
argument_list|,
name|namePathMapper
argument_list|,
name|eventTypes
argument_list|,
name|absPath
argument_list|,
name|isDeep
argument_list|,
name|uuid
argument_list|,
name|nodeTypeName
argument_list|,
name|noLocal
argument_list|)
decl_stmt|;
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
name|processor
operator|=
operator|new
name|ChangeProcessor
argument_list|(
name|this
argument_list|,
name|listener
argument_list|,
name|filter
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
name|executor
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|processor
operator|.
name|setFilter
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|removeEventListener
parameter_list|(
name|EventListener
name|listener
parameter_list|)
block|{
name|ChangeProcessor
name|processor
init|=
name|processors
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
decl_stmt|;
if|if
condition|(
name|processor
operator|!=
literal|null
condition|)
block|{
name|processor
operator|.
name|stop
argument_list|()
expr_stmt|;
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
name|String
name|userData
parameter_list|)
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"User data not supported"
argument_list|)
throw|;
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
comment|//------------------------------------------------------------< internal>---
name|NamePathMapper
name|getNamePathMapper
parameter_list|()
block|{
return|return
name|namePathMapper
return|;
block|}
name|ChangeExtractor
name|getChangeExtractor
parameter_list|()
block|{
return|return
name|root
operator|.
name|getChangeExtractor
argument_list|()
return|;
block|}
name|void
name|setHasEvents
parameter_list|()
block|{
name|hasEvents
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
class|class
name|NTMgr
extends|extends
name|ReadOnlyNodeTypeManager
block|{
annotation|@
name|Override
specifier|protected
name|Tree
name|getTypes
parameter_list|()
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|protected
name|NamePathMapper
name|getNamePathMapper
parameter_list|()
block|{
return|return
name|namePathMapper
return|;
block|}
block|}
block|}
end_class

end_unit

