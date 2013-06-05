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
name|Objects
operator|.
name|toStringHelper
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
name|checkNotNull
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
name|ObservationConstants
operator|.
name|OAK_UNKNOWN
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Nonnull
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
name|Queues
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
name|Sets
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
name|spi
operator|.
name|commit
operator|.
name|PostCommitHook
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
name|NodeStore
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
name|util
operator|.
name|TODO
import|;
end_import

begin_class
specifier|public
class|class
name|ChangeDispatcher
block|{
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|Listener
argument_list|>
name|listeners
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
name|NodeState
name|previous
decl_stmt|;
specifier|public
name|ChangeDispatcher
parameter_list|(
name|NodeStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|previous
operator|=
name|checkNotNull
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Hook
name|createHook
parameter_list|(
name|ContentSession
name|contentSession
parameter_list|)
block|{
return|return
operator|new
name|Hook
argument_list|(
name|contentSession
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Listener
name|newListener
parameter_list|()
block|{
name|Listener
name|listener
init|=
operator|new
name|Listener
argument_list|()
decl_stmt|;
name|register
argument_list|(
name|listener
argument_list|)
expr_stmt|;
return|return
name|listener
return|;
block|}
specifier|private
name|void
name|contentChanged
parameter_list|(
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
name|ContentSession
name|contentSession
parameter_list|)
block|{
name|ChangeSet
name|extChanges
decl_stmt|;
name|ChangeSet
name|intChange
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|extChanges
operator|=
name|externalChange
argument_list|(
name|checkNotNull
argument_list|(
name|before
argument_list|)
argument_list|)
expr_stmt|;
name|intChange
operator|=
name|internalChange
argument_list|(
name|checkNotNull
argument_list|(
name|after
argument_list|)
argument_list|,
name|contentSession
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|extChanges
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|extChanges
argument_list|)
expr_stmt|;
block|}
name|add
argument_list|(
name|intChange
argument_list|)
expr_stmt|;
block|}
annotation|@
name|CheckForNull
specifier|private
specifier|synchronized
name|ChangeSet
name|externalChange
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
if|if
condition|(
name|root
operator|!=
name|previous
condition|)
block|{
name|ChangeSet
name|changeSet
init|=
name|ChangeSet
operator|.
name|external
argument_list|(
name|previous
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|previous
operator|=
name|root
expr_stmt|;
return|return
name|changeSet
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Nonnull
specifier|private
specifier|synchronized
name|ChangeSet
name|internalChange
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|ContentSession
name|contentSession
parameter_list|)
block|{
name|ChangeSet
name|changeSet
init|=
name|ChangeSet
operator|.
name|local
argument_list|(
name|previous
argument_list|,
name|root
argument_list|,
name|contentSession
argument_list|)
decl_stmt|;
name|previous
operator|=
name|root
expr_stmt|;
return|return
name|changeSet
return|;
block|}
specifier|private
name|void
name|register
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
synchronized|synchronized
init|(
name|listeners
init|)
block|{
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|unregister
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
synchronized|synchronized
init|(
name|listeners
init|)
block|{
name|listeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|add
parameter_list|(
name|ChangeSet
name|changeSet
parameter_list|)
block|{
for|for
control|(
name|Listener
name|l
range|:
name|getListeners
argument_list|()
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|changeSet
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Listener
index|[]
name|getListeners
parameter_list|()
block|{
synchronized|synchronized
init|(
name|listeners
init|)
block|{
return|return
name|listeners
operator|.
name|toArray
argument_list|(
operator|new
name|Listener
index|[
name|listeners
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
comment|//------------------------------------------------------------< Sink>---
specifier|public
class|class
name|Hook
implements|implements
name|PostCommitHook
block|{
specifier|private
specifier|final
name|ContentSession
name|contentSession
decl_stmt|;
specifier|private
name|Hook
parameter_list|(
name|ContentSession
name|contentSession
parameter_list|)
block|{
name|this
operator|.
name|contentSession
operator|=
name|contentSession
expr_stmt|;
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
name|before
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|after
parameter_list|)
block|{
name|ChangeDispatcher
operator|.
name|this
operator|.
name|contentChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|contentSession
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< Listener>---
specifier|public
class|class
name|Listener
block|{
specifier|private
specifier|final
name|Queue
argument_list|<
name|ChangeSet
argument_list|>
name|changeSets
init|=
name|Queues
operator|.
name|newLinkedBlockingQueue
argument_list|()
decl_stmt|;
specifier|public
name|void
name|dispose
parameter_list|()
block|{
name|unregister
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|ChangeSet
name|getChanges
parameter_list|()
block|{
if|if
condition|(
name|changeSets
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|add
argument_list|(
name|externalChange
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|changeSets
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|changeSets
operator|.
name|remove
argument_list|()
return|;
block|}
specifier|private
name|void
name|add
parameter_list|(
name|ChangeSet
name|changeSet
parameter_list|)
block|{
name|changeSets
operator|.
name|add
argument_list|(
name|changeSet
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< ChangeSet>---
specifier|public
specifier|abstract
specifier|static
class|class
name|ChangeSet
block|{
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
specifier|public
specifier|static
name|ChangeSet
name|local
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|NodeState
name|head
parameter_list|,
name|ContentSession
name|contentSession
parameter_list|)
block|{
return|return
operator|new
name|InternalChangeSet
argument_list|(
name|base
argument_list|,
name|head
argument_list|,
name|contentSession
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ChangeSet
name|external
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|NodeState
name|head
parameter_list|)
block|{
return|return
operator|new
name|ExternalChangeSet
argument_list|(
name|base
argument_list|,
name|head
argument_list|)
return|;
block|}
specifier|protected
name|ChangeSet
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
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
specifier|public
specifier|abstract
name|boolean
name|isExternal
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|isLocal
parameter_list|(
name|ContentSession
name|contentSession
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|String
name|getUserId
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|long
name|getDate
parameter_list|()
function_decl|;
specifier|public
name|void
name|diff
parameter_list|(
name|NodeStateDiff
name|diff
parameter_list|)
block|{
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|add
argument_list|(
literal|"base"
argument_list|,
name|before
argument_list|)
operator|.
name|add
argument_list|(
literal|"head"
argument_list|,
name|after
argument_list|)
operator|.
name|add
argument_list|(
literal|"userId"
argument_list|,
name|getUserId
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
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ChangeSet
name|that
init|=
operator|(
name|ChangeSet
operator|)
name|other
decl_stmt|;
return|return
name|before
operator|.
name|equals
argument_list|(
name|that
operator|.
name|before
argument_list|)
operator|&&
name|after
operator|.
name|equals
argument_list|(
name|that
operator|.
name|after
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|31
operator|*
name|before
operator|.
name|hashCode
argument_list|()
operator|+
name|after
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|private
specifier|static
class|class
name|InternalChangeSet
extends|extends
name|ChangeSet
block|{
specifier|private
specifier|static
specifier|final
name|String
name|DUMMY_USER_DATA
init|=
name|TODO
operator|.
name|dummyImplementation
argument_list|()
operator|.
name|returnValueOrNull
argument_list|(
literal|"oak:not implemented"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ContentSession
name|contentSession
decl_stmt|;
specifier|private
specifier|final
name|String
name|userId
decl_stmt|;
specifier|private
specifier|final
name|long
name|date
decl_stmt|;
name|InternalChangeSet
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|NodeState
name|head
parameter_list|,
name|ContentSession
name|contentSession
parameter_list|,
name|long
name|date
parameter_list|)
block|{
name|super
argument_list|(
name|base
argument_list|,
name|head
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
name|userId
operator|=
name|contentSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getUserID
argument_list|()
expr_stmt|;
name|this
operator|.
name|date
operator|=
name|date
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isExternal
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isLocal
parameter_list|(
name|ContentSession
name|contentSession
parameter_list|)
block|{
return|return
name|this
operator|.
name|contentSession
operator|==
name|contentSession
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getUserId
parameter_list|()
block|{
return|return
name|userId
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
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|InternalChangeSet
name|that
init|=
operator|(
name|InternalChangeSet
operator|)
name|other
decl_stmt|;
return|return
name|date
operator|==
name|that
operator|.
name|date
operator|&&
name|contentSession
operator|==
name|that
operator|.
name|contentSession
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|ExternalChangeSet
extends|extends
name|ChangeSet
block|{
name|ExternalChangeSet
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|NodeState
name|head
parameter_list|)
block|{
name|super
argument_list|(
name|base
argument_list|,
name|head
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isExternal
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isLocal
parameter_list|(
name|ContentSession
name|contentSession
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getUserId
parameter_list|()
block|{
return|return
name|OAK_UNKNOWN
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
literal|0
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

