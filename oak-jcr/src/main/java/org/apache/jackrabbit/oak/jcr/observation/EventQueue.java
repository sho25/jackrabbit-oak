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
name|newLinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
name|EventGenerator
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
name|plugins
operator|.
name|observation
operator|.
name|FilteredHandler
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
name|EventFilter
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

begin_comment
comment|/**  * Queue of JCR Events generated from a given content change  */
end_comment

begin_class
class|class
name|EventQueue
implements|implements
name|EventIterator
block|{
specifier|private
specifier|final
name|EventGenerator
name|generator
decl_stmt|;
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|Event
argument_list|>
name|queue
init|=
name|newLinkedList
argument_list|()
decl_stmt|;
specifier|private
name|long
name|position
init|=
literal|0
decl_stmt|;
specifier|public
name|EventQueue
parameter_list|(
annotation|@
name|Nonnull
name|NamePathMapper
name|mapper
parameter_list|,
name|CommitInfo
name|info
parameter_list|,
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
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|String
argument_list|>
name|basePaths
parameter_list|,
annotation|@
name|Nonnull
name|EventFilter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|generator
operator|=
operator|new
name|EventGenerator
argument_list|()
expr_stmt|;
name|EventFactory
name|factory
init|=
operator|new
name|EventFactory
argument_list|(
name|mapper
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|EventHandler
name|handler
init|=
operator|new
name|FilteredHandler
argument_list|(
name|filter
argument_list|,
operator|new
name|QueueingHandler
argument_list|(
name|this
argument_list|,
name|factory
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|basePaths
control|)
block|{
name|addHandler
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|path
argument_list|,
name|handler
argument_list|,
name|generator
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|addHandler
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|String
name|path
parameter_list|,
name|EventHandler
name|handler
parameter_list|,
name|EventGenerator
name|generator
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|before
operator|=
name|before
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|after
operator|=
name|after
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|handler
operator|=
name|handler
operator|.
name|getChildHandler
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
return|return;
block|}
block|}
name|generator
operator|.
name|addHandler
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
comment|/**      * Called by the {@link QueueingHandler} to add new events to the queue.      */
name|void
name|addEvent
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
comment|//-----------------------------------------------------< EventIterator>--
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|()
block|{
if|if
condition|(
name|generator
operator|.
name|isDone
argument_list|()
condition|)
block|{
comment|// no more new events will be generated, so count just those
comment|// that have already been iterated and those left in the queue
return|return
name|position
operator|+
name|queue
operator|.
name|size
argument_list|()
return|;
block|}
else|else
block|{
comment|// the generator is not yet done, so there's no way
comment|// to know how many events may still be generated
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPosition
parameter_list|()
block|{
return|return
name|position
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
while|while
condition|(
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|generator
operator|.
name|isDone
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|generator
operator|.
name|generate
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|skip
parameter_list|(
name|long
name|skipNum
parameter_list|)
block|{
comment|// generate events until all events to skip have been queued
while|while
condition|(
name|skipNum
operator|>
name|queue
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// drop all currently queued events as we're skipping them all
name|position
operator|+=
name|queue
operator|.
name|size
argument_list|()
expr_stmt|;
name|skipNum
operator|-=
name|queue
operator|.
name|size
argument_list|()
expr_stmt|;
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// generate more events if possible, otherwise fail
if|if
condition|(
operator|!
name|generator
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|generator
operator|.
name|generate
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"Not enough events to skip"
argument_list|)
throw|;
block|}
block|}
comment|// the remaining events to skip are guaranteed to all be in the
comment|// queue, so we can just drop those events and advance the position
name|queue
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
operator|(
name|int
operator|)
name|skipNum
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
name|position
operator|+=
name|skipNum
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Event
name|nextEvent
parameter_list|()
block|{
if|if
condition|(
name|hasNext
argument_list|()
condition|)
block|{
name|position
operator|++
expr_stmt|;
return|return
name|queue
operator|.
name|removeFirst
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Event
name|next
parameter_list|()
block|{
return|return
name|nextEvent
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

