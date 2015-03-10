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
name|Iterables
operator|.
name|isEmpty
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
name|toArray
import|;
end_import

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
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
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
name|jcr
operator|.
name|Value
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
name|plugins
operator|.
name|value
operator|.
name|ValueFactoryImpl
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

begin_comment
comment|/**  * Event factory for generating JCR event instances that are optimized  * for minimum memory overhead. Each factory instance keeps track of the  * event information (like the user identifier and the commit timestamp)  * shared across all events from a single commit. The generated events  * instances postpone things like path mappings and the construction of  * the event info maps to as late as possible to avoid the memory overhead  * of keeping track of pre-computed values.  */
end_comment

begin_class
specifier|public
class|class
name|EventFactory
block|{
specifier|public
specifier|static
specifier|final
name|String
name|USER_DATA
init|=
literal|"user-data"
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|mapper
decl_stmt|;
specifier|private
specifier|final
name|String
name|userID
decl_stmt|;
specifier|private
specifier|final
name|String
name|userData
decl_stmt|;
specifier|private
specifier|final
name|long
name|date
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|external
decl_stmt|;
name|EventFactory
parameter_list|(
name|NamePathMapper
name|mapper
parameter_list|,
name|CommitInfo
name|commitInfo
parameter_list|)
block|{
name|this
operator|.
name|mapper
operator|=
name|mapper
expr_stmt|;
if|if
condition|(
name|commitInfo
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|userID
operator|=
name|commitInfo
operator|.
name|getUserId
argument_list|()
expr_stmt|;
name|Object
name|userData
init|=
name|commitInfo
operator|.
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
name|USER_DATA
argument_list|)
decl_stmt|;
name|this
operator|.
name|userData
operator|=
name|userData
operator|instanceof
name|String
condition|?
operator|(
name|String
operator|)
name|userData
else|:
literal|null
expr_stmt|;
name|this
operator|.
name|date
operator|=
name|commitInfo
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
name|userID
operator|=
name|CommitInfo
operator|.
name|OAK_UNKNOWN
expr_stmt|;
name|this
operator|.
name|userData
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|date
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
comment|// rough estimate
name|this
operator|.
name|external
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|Event
name|propertyAdded
parameter_list|(
specifier|final
name|PropertyState
name|after
parameter_list|,
specifier|final
name|String
name|primaryType
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|mixinTypes
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|identifier
parameter_list|)
block|{
return|return
operator|new
name|EventImpl
argument_list|(
name|path
argument_list|,
name|name
argument_list|,
name|identifier
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
name|builder
argument_list|()
operator|.
name|putAll
argument_list|(
name|createInfoMap
argument_list|(
name|primaryType
argument_list|,
name|mixinTypes
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"afterValue"
argument_list|,
name|createValue
argument_list|(
name|after
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
return|;
block|}
name|Event
name|propertyChanged
parameter_list|(
specifier|final
name|PropertyState
name|before
parameter_list|,
specifier|final
name|PropertyState
name|after
parameter_list|,
specifier|final
name|String
name|primaryType
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|mixinTypes
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|identifier
parameter_list|)
block|{
return|return
operator|new
name|EventImpl
argument_list|(
name|path
argument_list|,
name|name
argument_list|,
name|identifier
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
name|builder
argument_list|()
operator|.
name|putAll
argument_list|(
name|createInfoMap
argument_list|(
name|primaryType
argument_list|,
name|mixinTypes
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"beforeValue"
argument_list|,
name|createValue
argument_list|(
name|before
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"afterValue"
argument_list|,
name|createValue
argument_list|(
name|after
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
return|;
block|}
name|Event
name|propertyDeleted
parameter_list|(
specifier|final
name|PropertyState
name|before
parameter_list|,
specifier|final
name|String
name|primaryType
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|mixinTypes
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|identifier
parameter_list|)
block|{
return|return
operator|new
name|EventImpl
argument_list|(
name|path
argument_list|,
name|name
argument_list|,
name|identifier
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
name|builder
argument_list|()
operator|.
name|putAll
argument_list|(
name|createInfoMap
argument_list|(
name|primaryType
argument_list|,
name|mixinTypes
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"beforeValue"
argument_list|,
name|createValue
argument_list|(
name|before
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|private
name|Object
name|createValue
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
if|if
condition|(
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Value
argument_list|>
name|values
init|=
name|ValueFactoryImpl
operator|.
name|createValues
argument_list|(
name|property
argument_list|,
name|mapper
argument_list|)
decl_stmt|;
return|return
name|values
operator|.
name|toArray
argument_list|(
operator|new
name|Value
index|[
name|values
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ValueFactoryImpl
operator|.
name|createValue
argument_list|(
name|property
argument_list|,
name|mapper
argument_list|)
return|;
block|}
block|}
name|Event
name|nodeAdded
parameter_list|(
specifier|final
name|String
name|primaryType
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|mixinTypes
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|identifier
parameter_list|)
block|{
return|return
operator|new
name|EventImpl
argument_list|(
name|path
argument_list|,
name|name
argument_list|,
name|identifier
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
name|createInfoMap
argument_list|(
name|primaryType
argument_list|,
name|mixinTypes
argument_list|)
return|;
block|}
block|}
return|;
block|}
name|Event
name|nodeDeleted
parameter_list|(
specifier|final
name|String
name|primaryType
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|mixinTypes
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|identifier
parameter_list|)
block|{
return|return
operator|new
name|EventImpl
argument_list|(
name|path
argument_list|,
name|name
argument_list|,
name|identifier
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
name|createInfoMap
argument_list|(
name|primaryType
argument_list|,
name|mixinTypes
argument_list|)
return|;
block|}
block|}
return|;
block|}
name|Event
name|nodeMoved
parameter_list|(
specifier|final
name|String
name|primaryType
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|mixinTypes
parameter_list|,
name|String
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|identifier
parameter_list|,
specifier|final
name|String
name|sourcePath
parameter_list|)
block|{
return|return
operator|new
name|EventImpl
argument_list|(
name|parent
argument_list|,
name|name
argument_list|,
name|identifier
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
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"srcAbsPath"
argument_list|,
name|mapper
operator|.
name|getJcrPath
argument_list|(
name|sourcePath
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"destAbsPath"
argument_list|,
name|getPath
argument_list|()
argument_list|)
operator|.
name|putAll
argument_list|(
name|createInfoMap
argument_list|(
name|primaryType
argument_list|,
name|mixinTypes
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
return|;
block|}
name|Event
name|nodeReordered
parameter_list|(
specifier|final
name|String
name|primaryType
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|mixinTypes
parameter_list|,
name|String
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|identifier
parameter_list|,
specifier|final
name|String
name|destName
parameter_list|)
block|{
return|return
operator|new
name|EventImpl
argument_list|(
name|parent
argument_list|,
name|name
argument_list|,
name|identifier
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
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"srcChildRelPath"
argument_list|,
name|mapper
operator|.
name|getJcrName
argument_list|(
name|name
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"destChildRelPath"
argument_list|,
name|mapper
operator|.
name|getJcrName
argument_list|(
name|destName
argument_list|)
argument_list|)
operator|.
name|putAll
argument_list|(
name|createInfoMap
argument_list|(
name|primaryType
argument_list|,
name|mixinTypes
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|createInfoMap
parameter_list|(
name|String
name|primaryType
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|mixinTypes
parameter_list|)
block|{
if|if
condition|(
name|isEmpty
argument_list|(
name|mixinTypes
argument_list|)
condition|)
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|mapper
operator|.
name|getJcrName
argument_list|(
name|primaryType
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|jcrNames
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|mixinTypes
control|)
block|{
name|jcrNames
operator|.
name|add
argument_list|(
name|mapper
operator|.
name|getJcrName
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ImmutableMap
operator|.
name|of
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|mapper
operator|.
name|getJcrName
argument_list|(
name|primaryType
argument_list|)
argument_list|,
name|JCR_MIXINTYPES
argument_list|,
name|toArray
argument_list|(
name|jcrNames
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|//---------------------------------------------------------< EventImpl>--
specifier|private
specifier|abstract
class|class
name|EventImpl
implements|implements
name|JackrabbitEvent
block|{
comment|/**          * Path of the parent node of the item this event is about.          */
specifier|private
specifier|final
name|String
name|parent
decl_stmt|;
comment|/**          * Name of the item this event is about.          */
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|String
name|identifier
decl_stmt|;
name|EventImpl
parameter_list|(
name|String
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|identifier
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
name|identifier
operator|=
name|identifier
expr_stmt|;
block|}
comment|//---------------------------------------------------------< Event>--
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
name|parent
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
name|identifier
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
name|userID
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
name|userData
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
name|date
return|;
block|}
comment|//-----------------------------------------------< JackrabbitEvent>--
annotation|@
name|Override
specifier|public
name|boolean
name|isExternal
parameter_list|()
block|{
return|return
name|external
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
name|EventImpl
condition|)
block|{
name|EventImpl
name|that
init|=
operator|(
name|EventImpl
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

