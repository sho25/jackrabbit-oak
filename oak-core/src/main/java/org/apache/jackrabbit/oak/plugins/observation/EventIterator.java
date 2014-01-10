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
name|checkNotNull
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
name|newLinkedList
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
name|CommitFailedException
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
name|EditorDiff
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
name|VisibleEditor
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
name|MoveDetector
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
comment|/**  * This {@link EventGenerator} implementation provides a traversable view for  * events.  * @param<T> type of the event returned by this iterator  */
end_comment

begin_class
specifier|public
class|class
name|EventIterator
parameter_list|<
name|T
parameter_list|>
extends|extends
name|EventGenerator
implements|implements
name|Iterable
argument_list|<
name|T
argument_list|>
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
name|EventIterator
operator|.
name|class
argument_list|)
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
specifier|private
specifier|final
name|EventFilter
name|filter
decl_stmt|;
specifier|private
specifier|final
name|IterableListener
argument_list|<
name|T
argument_list|>
name|listener
decl_stmt|;
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|EventIterator
argument_list|<
name|T
argument_list|>
argument_list|>
name|childEvents
init|=
name|newLinkedList
argument_list|()
decl_stmt|;
comment|/**      * Specialisation of {@link Listener} that provides the events reported      * to it as an iterator.      *      * @param<S> type of the events in the iterator      */
specifier|public
interface|interface
name|IterableListener
parameter_list|<
name|S
parameter_list|>
extends|extends
name|Listener
extends|,
name|Iterable
argument_list|<
name|S
argument_list|>
block|{
annotation|@
name|Override
annotation|@
name|Nonnull
name|IterableListener
argument_list|<
name|S
argument_list|>
name|create
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
function_decl|;
block|}
comment|/**      * Create a new instance of a {@code EventIterator} reporting events to the      * passed {@code listener} after filtering with the passed {@code filter}.      *      * @param before  before state      * @param after   after state      * @param filter  filter for filtering changes      * @param listener  listener for listening to the filtered changes      */
specifier|public
name|EventIterator
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
annotation|@
name|Nonnull
name|EventFilter
name|filter
parameter_list|,
annotation|@
name|Nonnull
name|IterableListener
argument_list|<
name|T
argument_list|>
name|listener
parameter_list|)
block|{
name|super
argument_list|(
name|filter
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|this
operator|.
name|before
operator|=
name|checkNotNull
argument_list|(
name|before
argument_list|)
expr_stmt|;
name|this
operator|.
name|after
operator|=
name|checkNotNull
argument_list|(
name|after
argument_list|)
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|checkNotNull
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|checkNotNull
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< EventGenerator>---
annotation|@
name|Override
specifier|protected
name|EventGenerator
name|createChildGenerator
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
name|EventFilter
name|childFilter
init|=
name|filter
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|childFilter
operator|!=
literal|null
condition|)
block|{
name|childEvents
operator|.
name|add
argument_list|(
operator|new
name|EventIterator
argument_list|<
name|T
argument_list|>
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|childFilter
argument_list|,
name|listener
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|//----------------------------------------------------------< Iterable>--
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
name|CommitFailedException
name|e
init|=
name|EditorDiff
operator|.
name|process
argument_list|(
operator|new
name|VisibleEditor
argument_list|(
operator|new
name|MoveDetector
argument_list|(
name|this
argument_list|)
argument_list|)
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while extracting observation events"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Iterator
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|private
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
init|=
name|listener
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
while|while
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|childEvents
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|iterator
operator|=
name|childEvents
operator|.
name|removeFirst
argument_list|()
operator|.
name|iterator
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
name|T
name|next
parameter_list|()
block|{
if|if
condition|(
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|iterator
operator|.
name|next
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
return|;
block|}
block|}
end_class

end_unit

