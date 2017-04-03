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
name|benchmark
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
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|NODE_MOVED
import|;
end_import

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
name|NODE_REMOVED
import|;
end_import

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
name|PERSIST
import|;
end_import

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
name|PROPERTY_ADDED
import|;
end_import

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
name|PROPERTY_CHANGED
import|;
end_import

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
name|PROPERTY_REMOVED
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
name|whiteboard
operator|.
name|WhiteboardUtils
operator|.
name|getServices
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
name|ExecutionException
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
name|ExecutorService
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
name|Executors
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
name|Future
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|AtomicReference
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
name|Repository
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
name|SimpleCredentials
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
name|commons
operator|.
name|JcrUtils
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
name|Oak
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
name|JcrCreator
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
name|OakRepositoryFixture
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
name|RepositoryFixture
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
name|spi
operator|.
name|commit
operator|.
name|BackgroundObserverMBean
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

begin_class
specifier|public
class|class
name|ObservationTest
extends|extends
name|Benchmark
block|{
specifier|public
specifier|static
specifier|final
name|int
name|EVENT_TYPES
init|=
name|NODE_ADDED
operator||
name|NODE_REMOVED
operator||
name|NODE_MOVED
operator||
name|PROPERTY_ADDED
operator||
name|PROPERTY_REMOVED
operator||
name|PROPERTY_CHANGED
operator||
name|PERSIST
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|EVENTS_PER_NODE
init|=
literal|2
decl_stmt|;
comment|// NODE_ADDED and PROPERTY_ADDED
specifier|private
specifier|static
specifier|final
name|int
name|SAVE_INTERVAL
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"saveInterval"
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|OUTPUT_RESOLUTION
init|=
literal|100
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|LISTENER_COUNT
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"listenerCount"
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|WRITER_COUNT
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"writerCount"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PATH_FILTER
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"pathFilter"
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|(
name|Iterable
argument_list|<
name|RepositoryFixture
argument_list|>
name|fixtures
parameter_list|)
block|{
for|for
control|(
name|RepositoryFixture
name|fixture
range|:
name|fixtures
control|)
block|{
if|if
condition|(
name|fixture
operator|.
name|isAvailable
argument_list|(
literal|1
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|format
argument_list|(
literal|"%s: Observation throughput benchmark%n"
argument_list|,
name|fixture
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|AtomicReference
argument_list|<
name|Whiteboard
argument_list|>
name|whiteboardRef
init|=
operator|new
name|AtomicReference
argument_list|<
name|Whiteboard
argument_list|>
argument_list|()
decl_stmt|;
name|Repository
index|[]
name|cluster
decl_stmt|;
if|if
condition|(
name|fixture
operator|instanceof
name|OakRepositoryFixture
condition|)
block|{
name|cluster
operator|=
operator|(
operator|(
name|OakRepositoryFixture
operator|)
name|fixture
operator|)
operator|.
name|setUpCluster
argument_list|(
literal|1
argument_list|,
operator|new
name|JcrCreator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Jcr
name|customize
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
name|whiteboardRef
operator|.
name|set
argument_list|(
name|oak
operator|.
name|getWhiteboard
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cluster
operator|=
name|fixture
operator|.
name|setUpCluster
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|run
argument_list|(
name|cluster
index|[
literal|0
index|]
argument_list|,
name|whiteboardRef
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fixture
operator|.
name|tearDownCluster
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|run
parameter_list|(
name|Repository
name|repository
parameter_list|,
annotation|@
name|Nullable
name|Whiteboard
name|whiteboard
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|Session
name|session
init|=
name|createSession
argument_list|(
name|repository
argument_list|)
decl_stmt|;
name|long
name|t0
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
name|observationThroughput
argument_list|(
name|repository
argument_list|,
name|whiteboard
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Time elapsed: "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t0
operator|)
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|observationThroughput
parameter_list|(
specifier|final
name|Repository
name|repository
parameter_list|,
annotation|@
name|Nullable
name|Whiteboard
name|whiteboard
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|long
name|t
init|=
literal|0
decl_stmt|;
specifier|final
name|AtomicInteger
name|eventCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|nodeCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Session
argument_list|>
name|sessions
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|EventListener
argument_list|>
name|listeners
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|testPaths
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|Session
name|s
init|=
name|createSession
argument_list|(
name|repository
argument_list|)
decl_stmt|;
name|String
name|path
init|=
literal|"/path/to/observation/benchmark-"
operator|+
name|AbstractTest
operator|.
name|TEST_ID
decl_stmt|;
try|try
block|{
name|Node
name|testRoot
init|=
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
name|s
argument_list|)
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
name|WRITER_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|testPaths
operator|.
name|add
argument_list|(
name|testRoot
operator|.
name|addNode
argument_list|(
literal|"session-"
operator|+
name|i
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
name|String
name|pathFilter
init|=
name|PATH_FILTER
operator|==
literal|null
condition|?
name|path
else|:
name|PATH_FILTER
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Path filter for event listener: "
operator|+
name|pathFilter
argument_list|)
expr_stmt|;
name|ExecutorService
name|service
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|WRITER_COUNT
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|LISTENER_COUNT
condition|;
name|k
operator|++
control|)
block|{
name|sessions
operator|.
name|add
argument_list|(
name|createSession
argument_list|(
name|repository
argument_list|)
argument_list|)
expr_stmt|;
name|listeners
operator|.
name|add
argument_list|(
operator|new
name|Listener
argument_list|(
name|eventCount
argument_list|)
argument_list|)
expr_stmt|;
name|ObservationManager
name|obsMgr
init|=
name|sessions
operator|.
name|get
argument_list|(
name|k
argument_list|)
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getObservationManager
argument_list|()
decl_stmt|;
name|obsMgr
operator|.
name|addEventListener
argument_list|(
name|listeners
operator|.
name|get
argument_list|(
name|k
argument_list|)
argument_list|,
name|EVENT_TYPES
argument_list|,
name|pathFilter
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
comment|// also add a listener on the root node
name|addRootListener
argument_list|(
name|repository
argument_list|,
name|sessions
argument_list|,
name|listeners
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|Object
argument_list|>
argument_list|>
name|createNodes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|p
range|:
name|testPaths
control|)
block|{
name|createNodes
operator|.
name|add
argument_list|(
name|service
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|Session
name|session
init|=
name|createSession
argument_list|(
name|repository
argument_list|)
decl_stmt|;
specifier|private
name|int
name|numNodes
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Object
name|call
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Node
name|testRoot
init|=
name|session
operator|.
name|getNode
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|createChildren
argument_list|(
name|testRoot
argument_list|,
literal|100
argument_list|)
expr_stmt|;
for|for
control|(
name|Node
name|m
range|:
name|JcrUtils
operator|.
name|getChildNodes
argument_list|(
name|testRoot
argument_list|)
control|)
block|{
name|createChildren
argument_list|(
name|m
argument_list|,
literal|100
operator|/
name|WRITER_COUNT
argument_list|)
expr_stmt|;
for|for
control|(
name|Node
name|n
range|:
name|JcrUtils
operator|.
name|getChildNodes
argument_list|(
name|m
argument_list|)
control|)
block|{
name|createChildren
argument_list|(
name|n
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|void
name|createChildren
parameter_list|(
name|Node
name|node
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|count
condition|;
name|c
operator|++
control|)
block|{
name|node
operator|.
name|addNode
argument_list|(
literal|"n"
operator|+
name|c
argument_list|)
expr_stmt|;
name|nodeCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
operator|++
name|numNodes
operator|%
name|SAVE_INTERVAL
operator|==
literal|0
condition|)
block|{
name|node
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ms      #node   nodes/s #event  event/s event-ratio queue external"
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|isDone
argument_list|(
name|createNodes
argument_list|)
operator|||
operator|(
name|eventCount
operator|.
name|get
argument_list|()
operator|/
name|LISTENER_COUNT
operator|<
name|nodeCount
operator|.
name|get
argument_list|()
operator|*
name|EVENTS_PER_NODE
operator|)
condition|)
block|{
name|long
name|t0
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|OUTPUT_RESOLUTION
argument_list|)
expr_stmt|;
name|t
operator|+=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t0
expr_stmt|;
name|int
name|nc
init|=
name|nodeCount
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|ec
init|=
name|eventCount
operator|.
name|get
argument_list|()
operator|/
name|LISTENER_COUNT
decl_stmt|;
name|int
index|[]
name|ql
init|=
name|getObservationQueueLength
argument_list|(
name|whiteboard
argument_list|)
decl_stmt|;
name|double
name|nps
init|=
operator|(
name|double
operator|)
name|nc
operator|/
name|t
operator|*
literal|1000
decl_stmt|;
name|double
name|eps
init|=
operator|(
name|double
operator|)
name|ec
operator|/
name|t
operator|*
literal|1000
decl_stmt|;
name|double
name|epn
init|=
operator|(
name|double
operator|)
name|ec
operator|/
name|nc
operator|/
name|EVENTS_PER_NODE
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|format
argument_list|(
literal|"%7d %7d %7.1f %7d %7.1f %7.2f %7d %7d%n"
argument_list|,
name|t
argument_list|,
name|nc
argument_list|,
name|nps
argument_list|,
name|ec
argument_list|,
name|eps
argument_list|,
name|epn
argument_list|,
name|ql
index|[
literal|0
index|]
argument_list|,
name|ql
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|get
argument_list|(
name|createNodes
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|sessions
operator|.
name|size
argument_list|()
condition|;
name|k
operator|++
control|)
block|{
name|sessions
operator|.
name|get
argument_list|(
name|k
argument_list|)
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getObservationManager
argument_list|()
operator|.
name|removeEventListener
argument_list|(
name|listeners
operator|.
name|get
argument_list|(
name|k
argument_list|)
argument_list|)
expr_stmt|;
name|sessions
operator|.
name|get
argument_list|(
name|k
argument_list|)
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
name|service
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|service
operator|.
name|awaitTermination
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addRootListener
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|List
argument_list|<
name|Session
argument_list|>
name|sessions
parameter_list|,
name|List
argument_list|<
name|EventListener
argument_list|>
name|listeners
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Session
name|s
init|=
name|createSession
argument_list|(
name|repository
argument_list|)
decl_stmt|;
name|sessions
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|Listener
name|listener
init|=
operator|new
name|Listener
argument_list|(
operator|new
name|AtomicInteger
argument_list|()
argument_list|)
decl_stmt|;
name|ObservationManager
name|obsMgr
init|=
name|s
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getObservationManager
argument_list|()
decl_stmt|;
name|obsMgr
operator|.
name|addEventListener
argument_list|(
name|listener
argument_list|,
name|EVENT_TYPES
argument_list|,
literal|"/"
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
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|int
index|[]
name|getObservationQueueLength
parameter_list|(
annotation|@
name|Nullable
name|Whiteboard
name|wb
parameter_list|)
block|{
if|if
condition|(
name|wb
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|int
index|[]
block|{
operator|-
literal|1
block|,
operator|-
literal|1
block|}
return|;
block|}
name|int
name|len
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|ext
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|BackgroundObserverMBean
name|bean
range|:
name|getServices
argument_list|(
name|wb
argument_list|,
name|BackgroundObserverMBean
operator|.
name|class
argument_list|)
control|)
block|{
name|len
operator|=
name|Math
operator|.
name|max
argument_list|(
name|bean
operator|.
name|getQueueSize
argument_list|()
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|ext
operator|=
name|Math
operator|.
name|max
argument_list|(
name|bean
operator|.
name|getExternalEventCount
argument_list|()
argument_list|,
name|ext
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|int
index|[]
block|{
name|len
block|,
name|ext
block|}
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isDone
parameter_list|(
name|Iterable
argument_list|<
name|Future
argument_list|<
name|Object
argument_list|>
argument_list|>
name|futures
parameter_list|)
block|{
for|for
control|(
name|Future
name|f
range|:
name|futures
control|)
block|{
if|if
condition|(
operator|!
name|f
operator|.
name|isDone
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
specifier|static
name|void
name|get
parameter_list|(
name|Iterable
argument_list|<
name|Future
argument_list|<
name|Object
argument_list|>
argument_list|>
name|futures
parameter_list|)
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
for|for
control|(
name|Future
name|f
range|:
name|futures
control|)
block|{
name|f
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Session
name|createSession
parameter_list|(
name|Repository
name|repository
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|repository
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|Listener
implements|implements
name|EventListener
block|{
specifier|private
specifier|final
name|AtomicInteger
name|eventCount
decl_stmt|;
specifier|public
name|Listener
parameter_list|(
name|AtomicInteger
name|eventCount
parameter_list|)
block|{
name|this
operator|.
name|eventCount
operator|=
name|eventCount
expr_stmt|;
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
for|for
control|(
init|;
name|events
operator|.
name|hasNext
argument_list|()
condition|;
name|events
operator|.
name|nextEvent
argument_list|()
control|)
block|{
name|eventCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
