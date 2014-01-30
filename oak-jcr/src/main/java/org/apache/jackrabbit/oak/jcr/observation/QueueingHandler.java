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
name|namepath
operator|.
name|PathTracker
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
name|identifier
operator|.
name|IdentifierTracker
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
name|EventHandler
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
comment|/**  * Event handler that uses the given {@link EventFactory} and tracked path  * and identifier information to translate change callbacks to corresponding  * JCR events that are then placed in the given {@link EventQueue}.  */
end_comment

begin_class
class|class
name|QueueingHandler
implements|implements
name|EventHandler
block|{
specifier|private
specifier|final
name|EventQueue
name|queue
decl_stmt|;
specifier|private
specifier|final
name|EventFactory
name|factory
decl_stmt|;
specifier|private
specifier|final
name|PathTracker
name|pathTracker
decl_stmt|;
comment|// need to track identifiers for both before and after trees,
comment|// to get correct identifiers for events in removed subtrees
specifier|private
specifier|final
name|IdentifierTracker
name|beforeIdentifierTracker
decl_stmt|;
specifier|private
specifier|final
name|IdentifierTracker
name|identifierTracker
decl_stmt|;
name|QueueingHandler
parameter_list|(
name|EventQueue
name|queue
parameter_list|,
name|EventFactory
name|factory
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
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|pathTracker
operator|=
operator|new
name|PathTracker
argument_list|()
expr_stmt|;
name|this
operator|.
name|beforeIdentifierTracker
operator|=
operator|new
name|IdentifierTracker
argument_list|(
name|before
argument_list|)
expr_stmt|;
if|if
condition|(
name|after
operator|.
name|exists
argument_list|()
condition|)
block|{
name|this
operator|.
name|identifierTracker
operator|=
operator|new
name|IdentifierTracker
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|identifierTracker
operator|=
name|beforeIdentifierTracker
expr_stmt|;
block|}
block|}
specifier|private
name|QueueingHandler
parameter_list|(
name|QueueingHandler
name|parent
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
name|queue
operator|=
name|parent
operator|.
name|queue
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|parent
operator|.
name|factory
expr_stmt|;
name|this
operator|.
name|pathTracker
operator|=
name|parent
operator|.
name|pathTracker
operator|.
name|getChildTracker
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|beforeIdentifierTracker
operator|=
name|parent
operator|.
name|beforeIdentifierTracker
operator|.
name|getChildTracker
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
if|if
condition|(
name|after
operator|.
name|exists
argument_list|()
condition|)
block|{
name|this
operator|.
name|identifierTracker
operator|=
name|parent
operator|.
name|identifierTracker
operator|.
name|getChildTracker
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|identifierTracker
operator|=
name|beforeIdentifierTracker
expr_stmt|;
block|}
block|}
comment|//-----------------------------------------------------< ChangeHandler>--
annotation|@
name|Override
specifier|public
name|EventHandler
name|getChildHandler
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
operator|new
name|QueueingHandler
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|before
argument_list|,
name|after
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
name|queue
operator|.
name|addEvent
argument_list|(
name|factory
operator|.
name|propertyAdded
argument_list|(
name|pathTracker
operator|.
name|getPath
argument_list|()
argument_list|,
name|after
operator|.
name|getName
argument_list|()
argument_list|,
name|identifierTracker
operator|.
name|getIdentifier
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|queue
operator|.
name|addEvent
argument_list|(
name|factory
operator|.
name|propertyChanged
argument_list|(
name|pathTracker
operator|.
name|getPath
argument_list|()
argument_list|,
name|after
operator|.
name|getName
argument_list|()
argument_list|,
name|identifierTracker
operator|.
name|getIdentifier
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|queue
operator|.
name|addEvent
argument_list|(
name|factory
operator|.
name|propertyDeleted
argument_list|(
name|pathTracker
operator|.
name|getPath
argument_list|()
argument_list|,
name|before
operator|.
name|getName
argument_list|()
argument_list|,
name|identifierTracker
operator|.
name|getIdentifier
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|nodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|IdentifierTracker
name|tracker
init|=
name|identifierTracker
operator|.
name|getChildTracker
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|queue
operator|.
name|addEvent
argument_list|(
name|factory
operator|.
name|nodeAdded
argument_list|(
name|pathTracker
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|,
name|tracker
operator|.
name|getIdentifier
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|nodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|IdentifierTracker
name|tracker
init|=
name|beforeIdentifierTracker
operator|.
name|getChildTracker
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|queue
operator|.
name|addEvent
argument_list|(
name|factory
operator|.
name|nodeDeleted
argument_list|(
name|pathTracker
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|,
name|tracker
operator|.
name|getIdentifier
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|nodeMoved
parameter_list|(
specifier|final
name|String
name|sourcePath
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|moved
parameter_list|)
block|{
name|IdentifierTracker
name|tracker
init|=
name|identifierTracker
operator|.
name|getChildTracker
argument_list|(
name|name
argument_list|,
name|moved
argument_list|)
decl_stmt|;
name|queue
operator|.
name|addEvent
argument_list|(
name|factory
operator|.
name|nodeMoved
argument_list|(
name|pathTracker
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|,
name|tracker
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|sourcePath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|nodeReordered
parameter_list|(
specifier|final
name|String
name|destName
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
name|NodeState
name|reordered
parameter_list|)
block|{
name|IdentifierTracker
name|tracker
init|=
name|identifierTracker
operator|.
name|getChildTracker
argument_list|(
name|name
argument_list|,
name|reordered
argument_list|)
decl_stmt|;
name|queue
operator|.
name|addEvent
argument_list|(
name|factory
operator|.
name|nodeReordered
argument_list|(
name|pathTracker
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|,
name|tracker
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|destName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

