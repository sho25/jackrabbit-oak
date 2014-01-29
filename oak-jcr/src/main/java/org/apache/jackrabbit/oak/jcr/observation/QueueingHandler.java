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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|observation
operator|.
name|JackrabbitEvent
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
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
comment|/**  * Change handler that generates JCR Event instances and places them  * in an event queue.  */
end_comment

begin_class
class|class
name|QueueingHandler
implements|implements
name|EventHandler
block|{
comment|/**      * Dummy session identifier used to identify external commits.      */
specifier|private
specifier|static
specifier|final
name|String
name|OAK_EXTERNAL
init|=
literal|"oak:external"
decl_stmt|;
specifier|private
specifier|final
name|QueueingHandler
name|parent
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|PathTracker
name|pathTracker
decl_stmt|;
specifier|private
specifier|final
name|IdentifierTracker
name|identifierTracker
decl_stmt|;
specifier|private
specifier|final
name|EventQueue
name|queue
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|mapper
decl_stmt|;
specifier|private
specifier|final
name|CommitInfo
name|info
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|before
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|after
decl_stmt|;
name|QueueingHandler
parameter_list|(
name|EventQueue
name|queue
parameter_list|,
name|NamePathMapper
name|mapper
parameter_list|,
name|CommitInfo
name|info
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
literal|null
expr_stmt|;
name|this
operator|.
name|name
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|pathTracker
operator|=
operator|new
name|PathTracker
argument_list|()
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
operator|new
name|IdentifierTracker
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|mapper
operator|=
name|mapper
expr_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
else|else
block|{
comment|// Generate a dummy CommitInfo object to avoid extra null checks.
comment|// The current time is used as a rough estimate of the commit time.
name|this
operator|.
name|info
operator|=
operator|new
name|CommitInfo
argument_list|(
name|OAK_EXTERNAL
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
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
name|parent
operator|.
name|getBeforeIdentifierTracker
argument_list|()
operator|.
name|getChildTracker
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
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
name|mapper
operator|=
name|parent
operator|.
name|mapper
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|parent
operator|.
name|info
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
block|}
specifier|private
name|IdentifierTracker
name|getBeforeIdentifierTracker
parameter_list|()
block|{
if|if
condition|(
operator|!
name|after
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|identifierTracker
return|;
block|}
elseif|else
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
return|return
name|parent
operator|.
name|getBeforeIdentifierTracker
argument_list|()
operator|.
name|getChildTracker
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|IdentifierTracker
argument_list|(
name|before
argument_list|)
return|;
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
operator|new
name|ItemEvent
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|PROPERTY_ADDED
return|;
block|}
block|}
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
operator|new
name|ItemEvent
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|PROPERTY_CHANGED
return|;
block|}
block|}
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
operator|new
name|ItemEvent
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|PROPERTY_REMOVED
return|;
block|}
block|}
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
name|queue
operator|.
name|addEvent
argument_list|(
operator|new
name|NodeEvent
argument_list|(
name|name
argument_list|,
name|after
argument_list|,
name|identifierTracker
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|NODE_ADDED
return|;
block|}
block|}
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
name|queue
operator|.
name|addEvent
argument_list|(
operator|new
name|NodeEvent
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|getBeforeIdentifierTracker
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|NODE_REMOVED
return|;
block|}
block|}
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
name|queue
operator|.
name|addEvent
argument_list|(
operator|new
name|NodeEvent
argument_list|(
name|name
argument_list|,
name|moved
argument_list|,
name|identifierTracker
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|NODE_MOVED
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|getInfo
parameter_list|()
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"srcAbsPath"
argument_list|,
name|mapper
operator|.
name|getJcrPath
argument_list|(
name|sourcePath
argument_list|)
argument_list|,
literal|"destAbsPath"
argument_list|,
name|getPath
argument_list|()
argument_list|)
return|;
block|}
block|}
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
name|queue
operator|.
name|addEvent
argument_list|(
operator|new
name|NodeEvent
argument_list|(
name|name
argument_list|,
name|reordered
argument_list|,
name|identifierTracker
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|NODE_MOVED
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|getInfo
parameter_list|()
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"srcChildRelPath"
argument_list|,
name|mapper
operator|.
name|getJcrName
argument_list|(
name|name
argument_list|)
argument_list|,
literal|"destChildRelPath"
argument_list|,
name|mapper
operator|.
name|getJcrName
argument_list|(
name|destName
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|//-----------------------------------------------------------< private>--
specifier|private
specifier|abstract
class|class
name|NodeEvent
extends|extends
name|ItemEvent
block|{
specifier|private
specifier|final
name|String
name|identifier
decl_stmt|;
name|NodeEvent
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|node
parameter_list|,
name|IdentifierTracker
name|tracker
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|identifier
operator|=
name|tracker
operator|.
name|getChildTracker
argument_list|(
name|name
argument_list|,
name|node
argument_list|)
operator|.
name|getIdentifier
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIdentifier
parameter_list|()
block|{
return|return
name|identifier
return|;
block|}
block|}
specifier|private
specifier|abstract
class|class
name|ItemEvent
implements|implements
name|JackrabbitEvent
block|{
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
name|ItemEvent
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|mapper
operator|.
name|getJcrPath
argument_list|(
name|pathTracker
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
name|mapper
operator|.
name|getJcrName
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIdentifier
parameter_list|()
block|{
return|return
name|identifierTracker
operator|.
name|getIdentifier
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|getInfo
parameter_list|()
block|{
return|return
name|emptyMap
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getUserID
parameter_list|()
block|{
return|return
name|info
operator|.
name|getUserId
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getUserData
parameter_list|()
block|{
return|return
name|info
operator|.
name|getMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getDate
parameter_list|()
block|{
return|return
name|info
operator|.
name|getDate
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isExternal
parameter_list|()
block|{
return|return
name|info
operator|.
name|getSessionId
argument_list|()
operator|==
name|OAK_EXTERNAL
return|;
block|}
comment|//--------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|object
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|object
operator|instanceof
name|ItemEvent
condition|)
block|{
name|ItemEvent
name|that
init|=
operator|(
name|ItemEvent
operator|)
name|object
decl_stmt|;
return|return
name|getType
argument_list|()
operator|==
name|that
operator|.
name|getType
argument_list|()
operator|&&
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getPath
argument_list|()
argument_list|)
operator|&&
name|getIdentifier
argument_list|()
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getIdentifier
argument_list|()
argument_list|)
operator|&&
name|getInfo
argument_list|()
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getInfo
argument_list|()
argument_list|)
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|getUserID
argument_list|()
argument_list|,
name|that
operator|.
name|getUserID
argument_list|()
argument_list|)
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|getUserData
argument_list|()
argument_list|,
name|that
operator|.
name|getUserData
argument_list|()
argument_list|)
operator|&&
name|getDate
argument_list|()
operator|==
name|that
operator|.
name|getDate
argument_list|()
operator|&&
name|isExternal
argument_list|()
operator|==
name|that
operator|.
name|isExternal
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hashCode
argument_list|(
name|getType
argument_list|()
argument_list|,
name|getPath
argument_list|()
argument_list|,
name|getIdentifier
argument_list|()
argument_list|,
name|getInfo
argument_list|()
argument_list|,
name|getUserID
argument_list|()
argument_list|,
name|getUserData
argument_list|()
argument_list|,
name|getDate
argument_list|()
argument_list|,
name|isExternal
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|toStringHelper
argument_list|(
literal|"Event"
argument_list|)
operator|.
name|add
argument_list|(
literal|"type"
argument_list|,
name|getType
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"path"
argument_list|,
name|getPath
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"identifier"
argument_list|,
name|getIdentifier
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"info"
argument_list|,
name|getInfo
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"userID"
argument_list|,
name|getUserID
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"userData"
argument_list|,
name|getUserData
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"date"
argument_list|,
name|getDate
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"external"
argument_list|,
name|isExternal
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

