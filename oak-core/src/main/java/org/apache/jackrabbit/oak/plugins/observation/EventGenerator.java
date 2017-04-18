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
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
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
name|api
operator|.
name|Type
operator|.
name|NAMES
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
name|api
operator|.
name|Type
operator|.
name|STRING
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
name|tree
operator|.
name|impl
operator|.
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|MISSING_NODE
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
name|spi
operator|.
name|state
operator|.
name|MoveDetector
operator|.
name|SOURCE_PATH
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
name|commons
operator|.
name|benchmark
operator|.
name|PerfLogger
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
comment|/**  * Continuation-based content diff implementation that generates  * {@link EventHandler} callbacks by recursing down a content diff  * in a way that guarantees that only a finite number of callbacks  * will be made during a {@link #generate()} method call, regardless  * of how large or complex the content diff is.  *<p>  * A simple usage pattern would look like this:  *<pre>  * EventGenerator generator = new EventGenerator(before, after, handler);  * while (!generator.isDone()) {  *     generator.generate();  * }  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|EventGenerator
block|{
specifier|private
specifier|static
specifier|final
name|PerfLogger
name|perfLogger
init|=
operator|new
name|PerfLogger
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|EventGenerator
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".perf"
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      * Maximum number of content changes to process during the      * execution of a single diff continuation.      */
specifier|private
specifier|static
specifier|final
name|int
name|MAX_CHANGES_PER_CONTINUATION
init|=
literal|10000
decl_stmt|;
comment|/**      * Maximum number of continuations queued for future processing.      * Once this limit has been reached, we'll start pushing for the      * processing of property-only diffs, which will automatically      * help reduce the backlog.      */
specifier|private
specifier|static
specifier|final
name|int
name|MAX_QUEUED_CONTINUATIONS
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|Continuation
argument_list|>
name|continuations
init|=
name|newLinkedList
argument_list|()
decl_stmt|;
comment|/**      * Creates a new generator instance. Changes to process need to be added      * through {@link #addHandler(NodeState, NodeState, EventHandler)}      */
specifier|public
name|EventGenerator
parameter_list|()
block|{}
comment|/**      * Creates a new generator instance for processing the given changes.      */
specifier|public
name|EventGenerator
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
name|EventHandler
name|handler
parameter_list|)
block|{
name|continuations
operator|.
name|addFirst
argument_list|(
operator|new
name|Continuation
argument_list|(
name|handler
argument_list|,
name|before
argument_list|,
name|after
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addHandler
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|EventHandler
name|handler
parameter_list|)
block|{
name|continuations
operator|.
name|addFirst
argument_list|(
operator|new
name|Continuation
argument_list|(
name|handler
argument_list|,
name|before
argument_list|,
name|after
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks whether there are no more content changes to be processed.      */
specifier|public
name|boolean
name|isDone
parameter_list|()
block|{
return|return
name|continuations
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**      * Generates a finite number of {@link EventHandler} callbacks based      * on the content changes that have yet to be processed. Further processing      * (even if no callbacks were made) may be postponed to a future      * {@link #generate()} call, until the {@link #isDone()} method finally      * return {@code true}.      */
specifier|public
name|void
name|generate
parameter_list|()
block|{
if|if
condition|(
operator|!
name|continuations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|Continuation
name|c
init|=
name|continuations
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
specifier|final
name|long
name|start
init|=
name|perfLogger
operator|.
name|start
argument_list|(
literal|"generate: Starting event generation"
argument_list|)
decl_stmt|;
name|c
operator|.
name|run
argument_list|()
expr_stmt|;
name|perfLogger
operator|.
name|end
argument_list|(
name|start
argument_list|,
literal|1
argument_list|,
literal|"generate: Generated {} events"
argument_list|,
name|c
operator|.
name|counter
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|Continuation
implements|implements
name|NodeStateDiff
implements|,
name|Runnable
block|{
comment|/**          * Filtered handler of detected content changes.          */
specifier|private
specifier|final
name|EventHandler
name|handler
decl_stmt|;
comment|/**          * Before state, possibly non-existent.          */
specifier|private
specifier|final
name|NodeState
name|before
decl_stmt|;
comment|/**          * After state, possibly non-existent.          */
specifier|private
specifier|final
name|NodeState
name|after
decl_stmt|;
comment|/**          * Number of initial changes to skip.          */
specifier|private
specifier|final
name|int
name|skip
decl_stmt|;
comment|/**          * Number of changes seen so far.          */
specifier|private
name|int
name|counter
init|=
literal|0
decl_stmt|;
specifier|private
name|Continuation
parameter_list|(
name|EventHandler
name|handler
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|int
name|skip
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|handler
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
name|skip
operator|=
name|skip
expr_stmt|;
block|}
comment|//------------------------------------------------------< Runnable>--
comment|/**          * Continues the content diff from the point where this          * continuation was created.          */
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|skip
operator|==
literal|0
condition|)
block|{
comment|// Only call enter if this is not a continuation that hit
comment|// the MAX_CHANGES_PER_CONTINUATION limit before
name|handler
operator|.
name|enter
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|this
argument_list|)
condition|)
block|{
comment|// Only call leave if this continuation exists normally and not
comment|// as a result of hitting the MAX_CHANGES_PER_CONTINUATION limit
name|handler
operator|.
name|leave
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
comment|//-------------------------------------------------< NodeStateDiff>--
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
name|beforeEvent
argument_list|()
condition|)
block|{
name|handler
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
expr_stmt|;
return|return
name|afterEvent
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
name|beforeEvent
argument_list|()
condition|)
block|{
comment|// check for reordering of child nodes
if|if
condition|(
name|OAK_CHILD_ORDER
operator|.
name|equals
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// list the child node names before and after the change
name|List
argument_list|<
name|String
argument_list|>
name|beforeNames
init|=
name|newArrayList
argument_list|(
name|before
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|afterNames
init|=
name|newArrayList
argument_list|(
name|after
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
argument_list|)
decl_stmt|;
comment|// check only those names that weren't added or removed
name|beforeNames
operator|.
name|retainAll
argument_list|(
name|newHashSet
argument_list|(
name|afterNames
argument_list|)
argument_list|)
expr_stmt|;
name|afterNames
operator|.
name|retainAll
argument_list|(
name|newHashSet
argument_list|(
name|beforeNames
argument_list|)
argument_list|)
expr_stmt|;
comment|// Selection sort beforeNames into afterNames,
comment|// recording the swaps as we go
for|for
control|(
name|int
name|a
init|=
literal|0
init|;
name|a
operator|<
name|afterNames
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|a
operator|++
control|)
block|{
name|String
name|beforeName
init|=
name|beforeNames
operator|.
name|get
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|String
name|afterName
init|=
name|afterNames
operator|.
name|get
argument_list|(
name|a
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|afterName
operator|.
name|equals
argument_list|(
name|beforeName
argument_list|)
condition|)
block|{
comment|// Find afterName in the beforeNames list.
comment|// This loop is guaranteed to stop because both
comment|// lists contain the same names and we've already
comment|// processed all previous names.
name|int
name|b
init|=
name|a
operator|+
literal|1
decl_stmt|;
while|while
condition|(
operator|!
name|afterName
operator|.
name|equals
argument_list|(
name|beforeNames
operator|.
name|get
argument_list|(
name|b
argument_list|)
argument_list|)
condition|)
block|{
name|b
operator|++
expr_stmt|;
block|}
comment|// Swap the non-matching before name forward.
comment|// No need to beforeNames.set(a, afterName),
comment|// as we won't look back there anymore.
name|beforeNames
operator|.
name|set
argument_list|(
name|b
argument_list|,
name|beforeName
argument_list|)
expr_stmt|;
comment|// find the destName of the orderBefore operation
name|String
name|destName
init|=
literal|null
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
init|=
name|after
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|destName
operator|==
literal|null
operator|&&
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|afterName
operator|.
name|equals
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|destName
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// deliver the reordering event
name|handler
operator|.
name|nodeReordered
argument_list|(
name|destName
argument_list|,
name|afterName
argument_list|,
name|this
operator|.
name|after
operator|.
name|getChildNode
argument_list|(
name|afterName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|handler
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
return|return
name|afterEvent
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
if|if
condition|(
name|beforeEvent
argument_list|()
condition|)
block|{
name|handler
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
expr_stmt|;
return|return
name|afterEvent
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|fullQueue
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|beforeEvent
argument_list|()
condition|)
block|{
name|PropertyState
name|sourceProperty
init|=
name|after
operator|.
name|getProperty
argument_list|(
name|SOURCE_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceProperty
operator|!=
literal|null
condition|)
block|{
name|String
name|sourcePath
init|=
name|sourceProperty
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
decl_stmt|;
name|handler
operator|.
name|nodeMoved
argument_list|(
name|sourcePath
argument_list|,
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
name|handler
operator|.
name|nodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|addChildDiff
argument_list|(
name|name
argument_list|,
name|MISSING_NODE
argument_list|,
name|after
argument_list|)
expr_stmt|;
return|return
name|afterEvent
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeChanged
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
if|if
condition|(
name|fullQueue
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|beforeEvent
argument_list|()
condition|)
block|{
name|addChildDiff
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
return|return
name|afterEvent
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
if|if
condition|(
name|fullQueue
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|beforeEvent
argument_list|()
condition|)
block|{
name|handler
operator|.
name|nodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
name|addChildDiff
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|MISSING_NODE
argument_list|)
expr_stmt|;
return|return
name|afterEvent
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
comment|//-------------------------------------------------------< private>--
comment|/**          * Schedules a continuation for processing changes within the given          * child node, if changes within that subtree should be processed.          */
specifier|private
name|void
name|addChildDiff
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
name|EventHandler
name|h
init|=
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
decl_stmt|;
if|if
condition|(
name|h
operator|!=
literal|null
condition|)
block|{
name|continuations
operator|.
name|addFirst
argument_list|(
operator|new
name|Continuation
argument_list|(
name|h
argument_list|,
name|before
argument_list|,
name|after
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**          * Increases the event counter and checks whether the event should          * be processed, i.e. whether the initial skip count has been reached.          */
specifier|private
name|boolean
name|beforeEvent
parameter_list|()
block|{
return|return
operator|++
name|counter
operator|>
name|skip
return|;
block|}
comment|/**          * Checks whether the diff queue has reached the maximum size limit,          * and postpones further processing of the current diff to later.          * Even though this postponement increases the size of the queue          * beyond the limit, doing so ultimately forces property-only          * diffs to the beginning of the queue, and thus helps to          * automatically clean up the backlog.          */
specifier|private
name|boolean
name|fullQueue
parameter_list|()
block|{
if|if
condition|(
name|counter
operator|>
name|skip
comment|// must have processed at least one event
operator|&&
name|continuations
operator|.
name|size
argument_list|()
operator|>=
name|MAX_QUEUED_CONTINUATIONS
condition|)
block|{
name|continuations
operator|.
name|add
argument_list|(
operator|new
name|Continuation
argument_list|(
name|handler
argument_list|,
name|this
operator|.
name|before
argument_list|,
name|this
operator|.
name|after
argument_list|,
name|counter
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**          * Checks whether enough events have already been processed in this          * continuation. If that is the case, we postpone further processing          * to a new continuation that will first skip all the initial events          * we've already seen. Otherwise we let the current diff continue.          */
specifier|private
name|boolean
name|afterEvent
parameter_list|()
block|{
if|if
condition|(
name|counter
operator|>=
name|skip
operator|+
name|MAX_CHANGES_PER_CONTINUATION
condition|)
block|{
name|continuations
operator|.
name|addFirst
argument_list|(
operator|new
name|Continuation
argument_list|(
name|handler
argument_list|,
name|before
argument_list|,
name|after
argument_list|,
name|counter
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

