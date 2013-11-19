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
operator|.
name|filter
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
name|Iterators
operator|.
name|concat
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|util
operator|.
name|LazyValue
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
name|Iterator
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
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|Filter
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
name|List
argument_list|<
name|Iterator
argument_list|<
name|T
argument_list|>
argument_list|>
name|childEvents
init|=
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|LazyValue
argument_list|<
name|Iterator
argument_list|<
name|T
argument_list|>
argument_list|>
name|eventIterator
init|=
operator|new
name|LazyValue
argument_list|<
name|Iterator
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Iterator
argument_list|<
name|T
argument_list|>
name|createValue
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
name|EventIterator
operator|.
name|this
argument_list|,
name|path
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
name|concat
argument_list|(
name|listener
operator|.
name|iterator
argument_list|()
argument_list|,
name|concat
argument_list|(
name|childEvents
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
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
comment|/**      * Create a new instance of a {@code EventIterator} reporting events to the      * passed {@code listener} after filtering with the passed {@code filter}.      *      * @param before  before state      * @param after   after state      * @parem path    common path to the before and after states      * @param filter  filter for filtering changes      * @param listener  listener for listening to the filtered changes      */
specifier|public
name|EventIterator
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
name|Filter
name|filter
parameter_list|,
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
name|before
expr_stmt|;
name|this
operator|.
name|after
operator|=
name|after
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
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
name|Filter
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
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
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
comment|//------------------------------------------------------------< Iterator>---
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|eventIterator
operator|.
name|get
argument_list|()
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|T
name|next
parameter_list|()
block|{
return|return
name|eventIterator
operator|.
name|get
argument_list|()
operator|.
name|next
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

