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
name|atomic
operator|.
name|AtomicInteger
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
name|fixture
operator|.
name|RepositoryFixture
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
literal|100
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|OUTPUT_RESOLUTION
init|=
literal|100
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
name|Repository
index|[]
name|cluster
init|=
name|fixture
operator|.
name|setUpCluster
argument_list|(
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
name|run
argument_list|(
name|cluster
index|[
literal|0
index|]
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
decl_stmt|;
try|try
block|{
name|observationThroughput
argument_list|(
name|repository
argument_list|,
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getObservationManager
argument_list|()
argument_list|)
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
block|}
specifier|public
name|void
name|observationThroughput
parameter_list|(
specifier|final
name|Repository
name|repository
parameter_list|,
name|ObservationManager
name|observationManager
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
name|EventListener
name|listener
init|=
operator|new
name|EventListener
argument_list|()
block|{
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
decl_stmt|;
try|try
block|{
name|observationManager
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
name|Future
argument_list|<
name|?
argument_list|>
name|createNodes
init|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
operator|.
name|submit
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|private
specifier|final
name|Session
name|session
init|=
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
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Node
name|testRoot
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"observationBenchmark"
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
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|nodeCount
operator|.
name|incrementAndGet
argument_list|()
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
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ms      #node   nodes/s #event  event/s event ratio"
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|createNodes
operator|.
name|isDone
argument_list|()
operator|||
operator|(
name|eventCount
operator|.
name|get
argument_list|()
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
literal|"%7d %7d %7.1f %7d %7.1f %1.2f%n"
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
argument_list|)
expr_stmt|;
block|}
name|createNodes
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|observationManager
operator|.
name|removeEventListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

