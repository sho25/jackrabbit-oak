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
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|NODE_ADDED
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|jcr
operator|.
name|Node
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
name|Session
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
name|ObservationManager
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
name|fixture
operator|.
name|NodeStoreFixture
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
name|jcr
operator|.
name|AbstractRepositoryTest
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
name|jcr
operator|.
name|Jcr
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
name|CommitRateLimiter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|h2
operator|.
name|util
operator|.
name|Profiler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
comment|/**  * An slow test case that that tries to test the commit rate limiter.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
comment|// Don't run "Parallelized" as this causes tests to timeout in "weak" environments
specifier|public
class|class
name|SlowObservationIT
extends|extends
name|AbstractRepositoryTest
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SlowObservationIT
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|OBSERVER_COUNT
init|=
literal|3
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|NO_DELAY_JUST_BLOCK
init|=
literal|false
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|PROFILE
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"oak.profile"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_NODE
init|=
literal|"test_node"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_PATH
init|=
literal|'/'
operator|+
name|TEST_NODE
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST2_NODE
init|=
literal|"test_node2"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST2_PATH
init|=
literal|'/'
operator|+
name|TEST2_NODE
decl_stmt|;
specifier|public
name|SlowObservationIT
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Jcr
name|initJcr
parameter_list|(
name|Jcr
name|jcr
parameter_list|)
block|{
name|CommitRateLimiter
name|limiter
init|=
operator|new
name|CommitRateLimiter
argument_list|()
block|{
name|long
name|lastLog
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setDelay
parameter_list|(
name|long
name|delay
parameter_list|)
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|>
name|lastLog
operator|+
literal|1000
condition|)
block|{
name|log
argument_list|(
literal|"Delay "
operator|+
name|delay
argument_list|)
expr_stmt|;
name|lastLog
operator|=
name|now
expr_stmt|;
block|}
name|super
operator|.
name|setDelay
argument_list|(
name|delay
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|delay
parameter_list|()
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|!
name|NO_DELAY_JUST_BLOCK
condition|)
block|{
comment|// default behavior
name|super
operator|.
name|delay
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|getBlockCommits
argument_list|()
operator|&&
name|isThreadBlocking
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
while|while
condition|(
name|getBlockCommits
argument_list|()
condition|)
block|{
name|wait
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|OAK
argument_list|,
literal|2
argument_list|,
literal|"Interrupted while waiting to commit"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
decl_stmt|;
return|return
name|super
operator|.
name|initJcr
argument_list|(
name|jcr
argument_list|)
operator|.
name|with
argument_list|(
name|limiter
argument_list|)
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|isDocumentNodeStore
argument_list|()
condition|)
block|{
return|return;
block|}
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|nodetypeIndex
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|getNode
argument_list|(
literal|"nodetype"
argument_list|)
decl_stmt|;
name|nodetypeIndex
operator|.
name|remove
argument_list|()
expr_stmt|;
name|Node
name|testNode
decl_stmt|;
name|testNode
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|TEST_NODE
argument_list|,
literal|"oak:Unstructured"
argument_list|)
expr_stmt|;
name|testNode
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testNode
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|TEST2_NODE
argument_list|,
literal|"oak:Unstructured"
argument_list|)
expr_stmt|;
name|testNode
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
specifier|private
name|boolean
name|isDocumentNodeStore
parameter_list|()
block|{
comment|// SegmentNodeStore can result in deadlocks, because
comment|// the CommitRateLimiter may be blocking inside a sychronized block
return|return
name|fixture
operator|.
name|toString
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"DocumentNodeStore"
argument_list|)
operator|>=
literal|0
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|observation
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|isDocumentNodeStore
argument_list|()
condition|)
block|{
return|return;
block|}
name|AtomicBoolean
name|saveInObservation
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|saveInObservation
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|MyListener
argument_list|>
name|listeners
init|=
operator|new
name|ArrayList
argument_list|<
name|MyListener
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|OBSERVER_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Session
name|observingSession
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
name|MyListener
name|listener
init|=
operator|new
name|MyListener
argument_list|(
name|i
argument_list|,
name|observingSession
argument_list|,
name|saveInObservation
argument_list|)
decl_stmt|;
name|listener
operator|.
name|open
argument_list|()
expr_stmt|;
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
name|log
argument_list|(
literal|"Starting..."
argument_list|)
expr_stmt|;
name|Profiler
name|prof
init|=
literal|null
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|prof
operator|==
literal|null
operator|&&
name|PROFILE
condition|)
block|{
comment|// prof = new Profiler().startCollecting();
block|}
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|time
operator|>
literal|20
operator|*
literal|1000
condition|)
block|{
if|if
condition|(
name|saveInObservation
operator|.
name|get
argument_list|()
condition|)
block|{
name|log
argument_list|(
literal|"Disable saves in observation now"
argument_list|)
expr_stmt|;
name|saveInObservation
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|time
operator|>
literal|30
operator|*
literal|1000
condition|)
block|{
break|break;
block|}
name|Node
name|testNode
decl_stmt|;
if|if
condition|(
name|i
operator|%
literal|100
operator|<
literal|52
condition|)
block|{
comment|// in 52% of the cases, use testNode
name|testNode
operator|=
name|getNode
argument_list|(
name|TEST_PATH
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// in 48% of the cases, use testNode2
name|testNode
operator|=
name|getNode
argument_list|(
name|TEST2_PATH
argument_list|)
expr_stmt|;
block|}
name|String
name|a
init|=
literal|"c-"
operator|+
operator|(
name|i
operator|/
literal|40
operator|)
decl_stmt|;
name|String
name|b
init|=
literal|"c-"
operator|+
operator|(
name|i
operator|%
literal|40
operator|)
decl_stmt|;
name|Node
name|x
decl_stmt|;
if|if
condition|(
name|testNode
operator|.
name|hasNode
argument_list|(
name|a
argument_list|)
condition|)
block|{
name|x
operator|=
name|testNode
operator|.
name|getNode
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|x
operator|=
name|testNode
operator|.
name|addNode
argument_list|(
name|a
argument_list|,
literal|"oak:Unstructured"
argument_list|)
expr_stmt|;
block|}
name|Node
name|t
init|=
name|x
operator|.
name|addNode
argument_list|(
name|b
argument_list|,
literal|"oak:Unstructured"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|t
operator|.
name|addNode
argument_list|(
literal|"c-"
operator|+
name|j
argument_list|,
literal|"oak:Unstructured"
argument_list|)
expr_stmt|;
block|}
name|long
name|saveTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|getAdminSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|saveTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|saveTime
expr_stmt|;
if|if
condition|(
name|saveTime
operator|>
literal|100
operator|||
name|i
operator|%
literal|200
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|prof
operator|!=
literal|null
condition|)
block|{
name|log
argument_list|(
name|prof
operator|.
name|getTop
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|prof
operator|=
literal|null
expr_stmt|;
block|}
name|log
argument_list|(
literal|"Save #"
operator|+
name|i
operator|+
literal|" took "
operator|+
name|saveTime
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
block|}
block|}
name|log
argument_list|(
literal|"Stopping..."
argument_list|)
expr_stmt|;
for|for
control|(
name|MyListener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|stop
argument_list|()
expr_stmt|;
name|listener
operator|.
name|waitUntilDone
argument_list|()
expr_stmt|;
name|listener
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|log
argument_list|(
literal|"Done"
argument_list|)
expr_stmt|;
if|if
condition|(
name|PROFILE
condition|)
block|{
name|printFullThreadDump
argument_list|()
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|Node
name|getNode
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|getAdminSession
argument_list|()
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**      * A simple listener that writes in 10% of the cases.      */
specifier|private
specifier|static
class|class
name|MyListener
implements|implements
name|EventListener
block|{
specifier|private
specifier|final
name|AtomicBoolean
name|saveInObservation
decl_stmt|;
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
specifier|private
specifier|final
name|Session
name|session
decl_stmt|;
specifier|private
name|ObservationManager
name|observationManager
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|stopped
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|done
decl_stmt|;
specifier|private
name|Exception
name|exception
decl_stmt|;
name|MyListener
parameter_list|(
name|int
name|id
parameter_list|,
name|Session
name|session
parameter_list|,
name|AtomicBoolean
name|saveInObservation
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|this
operator|.
name|saveInObservation
operator|=
name|saveInObservation
expr_stmt|;
block|}
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|observationManager
operator|=
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getObservationManager
argument_list|()
expr_stmt|;
name|observationManager
operator|.
name|addEventListener
argument_list|(
name|this
argument_list|,
name|NODE_ADDED
argument_list|,
literal|"/"
operator|+
name|TEST_NODE
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|observationManager
operator|.
name|removeEventListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|stopped
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|waitUntilDone
parameter_list|()
throws|throws
name|Exception
block|{
while|while
condition|(
operator|!
name|done
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|wait
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
name|exception
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onEvent
parameter_list|(
name|EventIterator
name|events
parameter_list|)
block|{
while|while
condition|(
operator|!
name|stopped
operator|&&
name|events
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Event
name|event
init|=
name|events
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|path
init|=
name|event
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|// Thread.currentThread().setName("Observer path " + path);
comment|// System.out.println("observer " + path);
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|path
operator|.
name|hashCode
argument_list|()
operator|%
name|OBSERVER_COUNT
argument_list|)
operator|!=
name|id
condition|)
block|{
comment|// if it's not for "my" observer, ignore
continue|continue;
block|}
if|if
condition|(
name|Math
operator|.
name|random
argument_list|()
operator|>
literal|0.5
condition|)
block|{
comment|// do nothing 50% of the time
continue|continue;
block|}
comment|// else save something as well: 5 times setProperty
if|if
condition|(
name|saveInObservation
operator|.
name|get
argument_list|()
condition|)
block|{
if|if
condition|(
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|hasNode
argument_list|(
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
condition|)
block|{
name|Node
name|n
init|=
name|session
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
comment|// System.out.println("observer save "+ path);
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|n
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
argument_list|(
literal|"Error "
operator|+
name|e
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Observation listener error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
name|done
operator|=
literal|true
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|static
name|void
name|log
parameter_list|(
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|PROFILE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|printFullThreadDump
parameter_list|()
block|{
name|log
argument_list|(
operator|new
name|Timestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|19
argument_list|)
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Full thread dump "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vm.name"
argument_list|)
operator|+
literal|" ("
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vm.version"
argument_list|)
operator|+
literal|"):"
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|""
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Thread
argument_list|,
name|StackTraceElement
index|[]
argument_list|>
name|e
range|:
name|Thread
operator|.
name|getAllStackTraces
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Thread
name|t
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|log
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"\"%s\"%s prio=%d tid=0x%x"
argument_list|,
name|t
operator|.
name|getName
argument_list|()
argument_list|,
name|t
operator|.
name|isDaemon
argument_list|()
condition|?
literal|" daemon"
else|:
literal|""
argument_list|,
name|t
operator|.
name|getPriority
argument_list|()
argument_list|,
name|t
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"    java.lang.Thread.State: "
operator|+
name|t
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|StackTraceElement
name|s
range|:
name|e
operator|.
name|getValue
argument_list|()
control|)
block|{
name|log
argument_list|(
literal|"\tat "
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
name|log
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

