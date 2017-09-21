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
name|atomic
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
name|oak
operator|.
name|api
operator|.
name|Type
operator|.
name|LONG
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
name|spi
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|MIX_ATOMIC_COUNTER
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
name|UUID
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
name|Callable
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
name|TimeUnit
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
name|annotation
operator|.
name|Nullable
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
name|Strings
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
name|Iterators
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
name|api
operator|.
name|Type
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
name|plugins
operator|.
name|memory
operator|.
name|LongPropertyState
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
name|spi
operator|.
name|commit
operator|.
name|CommitContext
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
name|CommitHook
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
name|commit
operator|.
name|DefaultEditor
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
name|Editor
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
name|EmptyHook
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
name|SimpleCommitContext
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
name|NodeBuilder
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
name|spi
operator|.
name|whiteboard
operator|.
name|WhiteboardUtils
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
comment|/**  *<p>  * Manages a node as<em>Atomic Counter</em>: a node which will handle at low level a protected  * property ({@link #PROP_COUNTER}) in an atomic way. This will represent an increment or decrement  * of a counter in the case, for example, of<em>Likes</em> or<em>Voting</em>.  *</p>  *   *<p>  * Whenever you add a {@link NodeTypeConstants#MIX_ATOMIC_COUNTER} mixin to a node it will turn it  * into an atomic counter. Then in order to increment or decrement the {@code oak:counter} property  * you'll need to set the {@code oak:increment} one ({@link #PROP_INCREMENT}). Please note that the  *<strong>{@code oak:incremement} will never be saved</strong>, only the {@code oak:counter} will  * be amended accordingly.  *</p>  *   *<p>  * So in order to deal with the counter from a JCR point of view you'll do something as follows  *</p>  *   *<pre>  *  Session session = ...  *    *  // creating a counter node  *  Node counter = session.getRootNode().addNode("mycounter");  *  counter.addMixin("mix:atomicCounter"); // or use the NodeTypeConstants  *  session.save();  *    *  // Will output 0. the default value  *  System.out.println("counter now: " + counter.getProperty("oak:counter").getLong());  *    *  // incrementing by 5 the counter  *  counter.setProperty("oak:increment", 5);  *  session.save();  *    *  // Will output 5  *  System.out.println("counter now: " + counter.getProperty("oak:counter").getLong());  *    *  // decreasing by 1  *  counter.setProperty("oak:increment", -1);  *  session.save();  *    *  // Will output 4  *  System.out.println("counter now: " + counter.getProperty("oak:counter").getLong());  *    *  session.logout();  *</pre>  *   *<h3>Internal behavioural details</h3>  *   *<p>  * The related jira ticket is<a href="https://issues.apache.org/jira/browse/OAK-2472">OAK-2472</a>.  * In a nutshell when you save an {@code oak:increment} behind the scene it takes its value and  * increment an internal counter. There will be an individual counter for each cluster node.  *</p>  *   *<p>  * Then it will consolidate all the internal counters into a single one: {@code oak:counter}. The  * consolidation process can happen either synchronously or asynchronously. Refer to  * {@link #AtomicCounterEditor(NodeBuilder, String, ScheduledExecutorService, NodeStore, Whiteboard)}  * for details on when it consolidate one way or the other.  *</p>  *   *<p>  *<strong>synchronous</strong>. It means the consolidation, sum of all the internal counters, will  * happen in the same thread. During the lifecycle of the same commit.  *</p>  *   *<p>  *<strong>asynchronous</strong>. It means the internal counters will be set during the same commit;  * but it will eventually schedule a separate thread in which will retry some times to consolidate  * them.  *</p>  */
end_comment

begin_class
specifier|public
class|class
name|AtomicCounterEditor
extends|extends
name|DefaultEditor
block|{
comment|/**      * property to be set for incrementing/decrementing the counter      */
specifier|public
specifier|static
specifier|final
name|String
name|PROP_INCREMENT
init|=
literal|"oak:increment"
decl_stmt|;
comment|/**      * property with the consolidated counter      */
specifier|public
specifier|static
specifier|final
name|String
name|PROP_COUNTER
init|=
literal|"oak:counter"
decl_stmt|;
comment|/**      * prefix used internally for tracking the counting requests      */
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX_PROP_COUNTER
init|=
literal|":oak-counter-"
decl_stmt|;
comment|/**      * prefix used internally for tracking the cluster node related revision numbers      */
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX_PROP_REVISION
init|=
literal|":rev-"
decl_stmt|;
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
name|AtomicCounterEditor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|String
name|instanceId
decl_stmt|;
specifier|private
specifier|final
name|ScheduledExecutorService
name|executor
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|Whiteboard
name|board
decl_stmt|;
comment|/**      * the current counter property name      */
specifier|private
specifier|final
name|String
name|counterName
decl_stmt|;
comment|/**      * the current revision property name      */
specifier|private
specifier|final
name|String
name|revisionName
decl_stmt|;
comment|/**      * instruct whether to update the node on leave.      */
specifier|private
name|boolean
name|update
decl_stmt|;
comment|/**      *<p>      * Create an instance of the editor for atomic increments. It can works synchronously as well as      * asynchronously. See class javadoc for details around it.      *</p>      *<p>      * If {@code instanceId} OR {@code executor} OR {@code store} OR {@code board} are null, the      * editor will switch to synchronous behaviour for consolidation. If no {@link CommitHook} will      * be found in the whiteboard, a {@link EmptyHook} will be provided to the {@link NodeStore} for      * merging.      *</p>      *       * @param builder the build on which to work. Cannot be null.      * @param instanceId the current Oak instance Id. If null editor will be synchronous.      * @param executor the current Oak executor service. If null editor will be synchronous.      * @param store the current Oak node store. If null the editor will be synchronous.      * @param board the current Oak {@link Whiteboard}.      */
specifier|public
name|AtomicCounterEditor
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|NodeBuilder
name|builder
parameter_list|,
annotation|@
name|Nullable
name|String
name|instanceId
parameter_list|,
annotation|@
name|Nullable
name|ScheduledExecutorService
name|executor
parameter_list|,
annotation|@
name|Nullable
name|NodeStore
name|store
parameter_list|,
annotation|@
name|Nullable
name|Whiteboard
name|board
parameter_list|)
block|{
name|this
argument_list|(
literal|""
argument_list|,
name|checkNotNull
argument_list|(
name|builder
argument_list|)
argument_list|,
name|instanceId
argument_list|,
name|executor
argument_list|,
name|store
argument_list|,
name|board
argument_list|)
expr_stmt|;
block|}
specifier|private
name|AtomicCounterEditor
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|NodeBuilder
name|builder
parameter_list|,
annotation|@
name|Nullable
name|String
name|instanceId
parameter_list|,
annotation|@
name|Nullable
name|ScheduledExecutorService
name|executor
parameter_list|,
annotation|@
name|Nullable
name|NodeStore
name|store
parameter_list|,
annotation|@
name|Nullable
name|Whiteboard
name|board
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|checkNotNull
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|instanceId
operator|=
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|instanceId
argument_list|)
condition|?
literal|null
else|:
name|instanceId
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|board
operator|=
name|board
expr_stmt|;
name|counterName
operator|=
name|instanceId
operator|==
literal|null
condition|?
name|PREFIX_PROP_COUNTER
else|:
name|PREFIX_PROP_COUNTER
operator|+
name|instanceId
expr_stmt|;
name|revisionName
operator|=
name|instanceId
operator|==
literal|null
condition|?
name|PREFIX_PROP_REVISION
else|:
name|PREFIX_PROP_REVISION
operator|+
name|instanceId
expr_stmt|;
block|}
specifier|private
specifier|static
name|boolean
name|shallWeProcessProperty
parameter_list|(
specifier|final
name|PropertyState
name|property
parameter_list|,
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|boolean
name|process
init|=
literal|false
decl_stmt|;
name|PropertyState
name|mixin
init|=
name|checkNotNull
argument_list|(
name|builder
argument_list|)
operator|.
name|getProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|)
decl_stmt|;
if|if
condition|(
name|mixin
operator|!=
literal|null
operator|&&
name|PROP_INCREMENT
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|Iterators
operator|.
name|contains
argument_list|(
name|mixin
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|,
name|MIX_ATOMIC_COUNTER
argument_list|)
condition|)
block|{
if|if
condition|(
name|LONG
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|process
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"although the {} property is set is not of the right value: LONG. Not processing node: {}."
argument_list|,
name|PROP_INCREMENT
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|process
return|;
block|}
comment|/**      *<p>      * consolidate the {@link #PREFIX_PROP_COUNTER} properties and sum them into the      * {@link #PROP_COUNTER}      *</p>      *       *<p>      * The passed in {@code NodeBuilder} must have      * {@link org.apache.jackrabbit.JcrConstants#JCR_MIXINTYPES JCR_MIXINTYPES} with      * {@link NodeTypeConstants#MIX_ATOMIC_COUNTER MIX_ATOMIC_COUNTER}.      * If not it will be silently ignored.      *</p>      *       * @param builder the builder to work on. Cannot be null.      */
specifier|public
specifier|static
name|void
name|consolidateCount
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|long
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|PropertyState
name|p
range|:
name|builder
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|PREFIX_PROP_COUNTER
argument_list|)
condition|)
block|{
name|count
operator|+=
name|p
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|setProperty
argument_list|(
name|PROP_COUNTER
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setUniqueCounter
parameter_list|(
specifier|final
name|long
name|value
parameter_list|)
block|{
name|update
operator|=
literal|true
expr_stmt|;
name|PropertyState
name|counter
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|counterName
argument_list|)
decl_stmt|;
name|PropertyState
name|revision
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|revisionName
argument_list|)
decl_stmt|;
name|long
name|currentValue
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|counter
operator|!=
literal|null
condition|)
block|{
name|currentValue
operator|=
name|counter
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
name|long
name|currentRevision
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|revision
operator|!=
literal|null
condition|)
block|{
name|currentRevision
operator|=
name|revision
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
name|currentValue
operator|+=
name|value
expr_stmt|;
name|currentRevision
operator|+=
literal|1
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|counterName
argument_list|,
name|currentValue
argument_list|,
name|LONG
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|revisionName
argument_list|,
name|currentRevision
argument_list|,
name|LONG
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
specifier|final
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|shallWeProcessProperty
argument_list|(
name|after
argument_list|,
name|path
argument_list|,
name|builder
argument_list|)
condition|)
block|{
name|setUniqueCounter
argument_list|(
name|after
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|removeProperty
argument_list|(
name|PROP_INCREMENT
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeAdded
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
operator|new
name|AtomicCounterEditor
argument_list|(
name|path
operator|+
literal|'/'
operator|+
name|name
argument_list|,
name|builder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|,
name|instanceId
argument_list|,
name|executor
argument_list|,
name|store
argument_list|,
name|board
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeChanged
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|NodeState
name|before
parameter_list|,
specifier|final
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
operator|new
name|AtomicCounterEditor
argument_list|(
name|path
operator|+
literal|'/'
operator|+
name|name
argument_list|,
name|builder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|,
name|instanceId
argument_list|,
name|executor
argument_list|,
name|store
argument_list|,
name|board
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|leave
parameter_list|(
specifier|final
name|NodeState
name|before
parameter_list|,
specifier|final
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|update
condition|)
block|{
if|if
condition|(
name|instanceId
operator|==
literal|null
operator|||
name|store
operator|==
literal|null
operator|||
name|executor
operator|==
literal|null
operator|||
name|board
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Executing synchronously. instanceId: {}, store: {}, executor: {}, board: {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|instanceId
block|,
name|store
block|,
name|executor
block|,
name|board
block|}
argument_list|)
expr_stmt|;
name|consolidateCount
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|CommitHook
name|hook
init|=
name|WhiteboardUtils
operator|.
name|getService
argument_list|(
name|board
argument_list|,
name|CommitHook
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|hook
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"CommitHook not registered with Whiteboard. Falling back to sync."
argument_list|)
expr_stmt|;
name|consolidateCount
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|long
name|delay
init|=
literal|500
decl_stmt|;
name|ConsolidatorTask
name|t
init|=
operator|new
name|ConsolidatorTask
argument_list|(
name|path
argument_list|,
name|builder
operator|.
name|getProperty
argument_list|(
name|revisionName
argument_list|)
argument_list|,
name|store
argument_list|,
name|executor
argument_list|,
name|delay
argument_list|,
name|hook
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"[{}] Scheduling process by {}ms"
argument_list|,
name|t
operator|.
name|getName
argument_list|()
argument_list|,
name|delay
argument_list|)
expr_stmt|;
name|executor
operator|.
name|schedule
argument_list|(
name|t
argument_list|,
name|delay
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
specifier|static
class|class
name|ConsolidatorTask
implements|implements
name|Callable
argument_list|<
name|Void
argument_list|>
block|{
comment|/**          * millis over which the task will timeout          */
specifier|public
specifier|static
specifier|final
name|long
name|MAX_TIMEOUT
init|=
name|Long
operator|.
name|getLong
argument_list|(
literal|"oak.atomiccounter.task.timeout"
argument_list|,
literal|32000
argument_list|)
decl_stmt|;
comment|/**          * millis below which the next delay will schedule at this amount.           */
specifier|public
specifier|static
specifier|final
name|long
name|MIN_TIMEOUT
init|=
literal|500
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|String
name|p
decl_stmt|;
specifier|private
specifier|final
name|PropertyState
name|rev
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|s
decl_stmt|;
specifier|private
specifier|final
name|ScheduledExecutorService
name|exec
decl_stmt|;
specifier|private
specifier|final
name|long
name|delay
decl_stmt|;
specifier|private
specifier|final
name|long
name|start
decl_stmt|;
specifier|private
specifier|final
name|CommitHook
name|hook
decl_stmt|;
specifier|public
name|ConsolidatorTask
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|revision
parameter_list|,
annotation|@
name|Nonnull
name|NodeStore
name|store
parameter_list|,
annotation|@
name|Nonnull
name|ScheduledExecutorService
name|exec
parameter_list|,
name|long
name|delay
parameter_list|,
annotation|@
name|Nonnull
name|CommitHook
name|hook
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|p
operator|=
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|rev
operator|=
name|revision
expr_stmt|;
name|s
operator|=
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|exec
operator|=
name|checkNotNull
argument_list|(
name|exec
argument_list|)
expr_stmt|;
name|this
operator|.
name|delay
operator|=
name|delay
expr_stmt|;
name|this
operator|.
name|hook
operator|=
name|checkNotNull
argument_list|(
name|hook
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
specifier|private
name|ConsolidatorTask
parameter_list|(
annotation|@
name|Nonnull
name|ConsolidatorTask
name|task
parameter_list|,
name|long
name|delay
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|task
argument_list|)
expr_stmt|;
name|this
operator|.
name|p
operator|=
name|task
operator|.
name|p
expr_stmt|;
name|this
operator|.
name|rev
operator|=
name|task
operator|.
name|rev
expr_stmt|;
name|this
operator|.
name|s
operator|=
name|task
operator|.
name|s
expr_stmt|;
name|this
operator|.
name|exec
operator|=
name|task
operator|.
name|exec
expr_stmt|;
name|this
operator|.
name|delay
operator|=
name|delay
expr_stmt|;
name|this
operator|.
name|hook
operator|=
name|task
operator|.
name|hook
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|task
operator|.
name|name
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|task
operator|.
name|start
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"[{}] Async consolidation running: path: {}, revision: {}"
argument_list|,
name|name
argument_list|,
name|p
argument_list|,
name|rev
argument_list|)
expr_stmt|;
name|NodeBuilder
name|root
init|=
name|s
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|b
init|=
name|builderFromPath
argument_list|(
name|root
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|dumpNode
argument_list|(
name|b
argument_list|,
name|p
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|b
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"[{}] Builder for '{}' from NodeStore not available. Rescheduling."
argument_list|,
name|name
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|reschedule
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|checkRevision
argument_list|(
name|b
argument_list|,
name|rev
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"[{}] Missing or not yet a valid revision for '{}'. Rescheduling."
argument_list|,
name|name
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|reschedule
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|isConsolidate
argument_list|(
name|b
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"[{}] consolidating."
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|consolidateCount
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|s
operator|.
name|merge
argument_list|(
name|root
argument_list|,
name|hook
argument_list|,
name|createCommitInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"[{}] Someone else consolidated. Skipping any operation."
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"[{}] caught Exception. Rescheduling. {}"
argument_list|,
name|name
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
comment|// duplicating message in logs; but avoiding unnecessary stacktrace generation
name|LOG
operator|.
name|trace
argument_list|(
literal|"[{}] caught Exception. Rescheduling."
argument_list|,
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|reschedule
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"[{}] Consolidation for '{}', '{}' completed in {}ms"
argument_list|,
name|name
argument_list|,
name|p
argument_list|,
name|rev
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|private
name|void
name|dumpNode
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|b
parameter_list|,
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|checkNotNull
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|StringBuilder
name|s
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyState
name|p
range|:
name|b
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|s
operator|.
name|append
argument_list|(
name|p
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|trace
argument_list|(
literal|"[{}] Node status for {}:\n{}"
argument_list|,
name|this
operator|.
name|name
argument_list|,
name|path
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|reschedule
parameter_list|()
block|{
name|long
name|d
init|=
name|nextDelay
argument_list|(
name|delay
argument_list|)
decl_stmt|;
if|if
condition|(
name|isTimedOut
argument_list|(
name|d
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"[{}] The consolidator task for '{}' timed out. Cancelling the retry."
argument_list|,
name|name
argument_list|,
name|p
argument_list|)
expr_stmt|;
return|return;
block|}
name|ConsolidatorTask
name|task
init|=
operator|new
name|ConsolidatorTask
argument_list|(
name|this
argument_list|,
name|d
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"[{}] Rescheduling '{}' by {}ms"
argument_list|,
name|task
operator|.
name|getName
argument_list|()
argument_list|,
name|p
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|exec
operator|.
name|schedule
argument_list|(
name|task
argument_list|,
name|d
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|long
name|nextDelay
parameter_list|(
name|long
name|currentDelay
parameter_list|)
block|{
if|if
condition|(
name|currentDelay
operator|<
name|MIN_TIMEOUT
condition|)
block|{
return|return
name|MIN_TIMEOUT
return|;
block|}
if|if
condition|(
name|currentDelay
operator|>=
name|MAX_TIMEOUT
condition|)
block|{
return|return
name|Long
operator|.
name|MAX_VALUE
return|;
block|}
return|return
name|currentDelay
operator|*
literal|2
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isTimedOut
parameter_list|(
name|long
name|delay
parameter_list|)
block|{
return|return
name|delay
operator|>
name|MAX_TIMEOUT
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
comment|/**      * checks that the revision provided in the PropertyState is less or equal than the one within      * the builder.      *       * if {@code revision} is null it will always be {@code true}.      *       * If {@code builder} does not contain the property it will always return false.      *       * @param builder      * @param revision      * @return      */
specifier|static
name|boolean
name|checkRevision
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|revision
parameter_list|)
block|{
if|if
condition|(
name|revision
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
name|String
name|pName
init|=
name|revision
operator|.
name|getName
argument_list|()
decl_stmt|;
name|PropertyState
name|builderRev
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|pName
argument_list|)
decl_stmt|;
if|if
condition|(
name|builderRev
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|long
name|brValue
init|=
name|builderRev
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|long
name|rValue
init|=
name|revision
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|brValue
operator|>=
name|rValue
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|builderFromPath
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|ancestor
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
name|NodeBuilder
name|b
init|=
name|checkNotNull
argument_list|(
name|ancestor
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|checkNotNull
argument_list|(
name|path
argument_list|)
argument_list|)
control|)
block|{
name|b
operator|=
name|b
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|b
return|;
block|}
comment|/**      * check whether the provided builder has to be consolidated or not. A node has to be      * consolidate if the sum of all the hidden counter does not match the exposed one. It could      * happen that some other nodes previously saw our change and already consolidated it.      *       * @param b the builde to check. Canno be null.      * @return true if the sum of the hidden counters does not match the exposed one.      */
specifier|static
name|boolean
name|isConsolidate
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|b
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|PropertyState
name|counter
init|=
name|b
operator|.
name|getProperty
argument_list|(
name|PROP_COUNTER
argument_list|)
decl_stmt|;
if|if
condition|(
name|counter
operator|==
literal|null
condition|)
block|{
name|counter
operator|=
name|LongPropertyState
operator|.
name|createLongProperty
argument_list|(
name|PROP_COUNTER
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|long
name|hiddensum
init|=
literal|0
decl_stmt|;
for|for
control|(
name|PropertyState
name|p
range|:
name|b
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|PREFIX_PROP_COUNTER
argument_list|)
condition|)
block|{
name|hiddensum
operator|+=
name|p
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|counter
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
operator|!=
name|hiddensum
return|;
block|}
specifier|private
specifier|static
name|CommitInfo
name|createCommitInfo
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|info
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
name|CommitContext
operator|.
name|NAME
argument_list|,
operator|new
name|SimpleCommitContext
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|CommitInfo
argument_list|(
name|CommitInfo
operator|.
name|OAK_UNKNOWN
argument_list|,
name|CommitInfo
operator|.
name|OAK_UNKNOWN
argument_list|,
name|info
argument_list|)
return|;
block|}
block|}
end_class

end_unit

