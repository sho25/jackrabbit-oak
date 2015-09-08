begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Thread
operator|.
name|UncaughtExceptionHandler
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
name|jcr
operator|.
name|AbstractRepositoryTest
operator|.
name|dispose
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|PropertyType
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Uninterruptibles
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
name|document
operator|.
name|DocumentMK
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
name|document
operator|.
name|util
operator|.
name|MongoConnection
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
name|index
operator|.
name|IndexConstants
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
name|index
operator|.
name|property
operator|.
name|PropertyIndexEditorProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * Concurrently add nodes with multiple sessions on multiple cluster nodes.  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentAddNodesClusterIT
block|{
specifier|private
specifier|static
specifier|final
name|int
name|NUM_CLUSTER_NODES
init|=
literal|5
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NODE_COUNT
init|=
literal|100
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|LOOP_COUNT
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|WORKER_COUNT
init|=
literal|20
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PROP_NAME
init|=
literal|"testcount"
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Repository
argument_list|>
name|repos
init|=
operator|new
name|ArrayList
argument_list|<
name|Repository
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|DocumentMK
argument_list|>
name|mks
init|=
operator|new
name|ArrayList
argument_list|<
name|DocumentMK
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Thread
argument_list|>
name|workers
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|mongoDBAvailable
parameter_list|()
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|OakMongoNSRepositoryStub
operator|.
name|isMongoDBAvailable
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|dropDB
argument_list|()
expr_stmt|;
name|initRepository
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|workers
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|Repository
name|repo
range|:
name|repos
control|)
block|{
name|dispose
argument_list|(
name|repo
argument_list|)
expr_stmt|;
block|}
name|repos
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|DocumentMK
name|mk
range|:
name|mks
control|)
block|{
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
name|mks
operator|.
name|clear
argument_list|()
expr_stmt|;
name|dropDB
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodesConcurrent
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_CLUSTER_NODES
condition|;
name|i
operator|++
control|)
block|{
name|DocumentMK
name|mk
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|createConnection
argument_list|()
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|mks
operator|.
name|add
argument_list|(
name|mk
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
name|exceptions
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
argument_list|()
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
name|mks
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|DocumentMK
name|mk
init|=
name|mks
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Repository
name|repo
init|=
operator|new
name|Jcr
argument_list|(
name|mk
operator|.
name|getNodeStore
argument_list|()
argument_list|)
operator|.
name|createRepository
argument_list|()
decl_stmt|;
name|repos
operator|.
name|add
argument_list|(
name|repo
argument_list|)
expr_stmt|;
name|workers
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|Worker
argument_list|(
name|repo
argument_list|,
name|exceptions
argument_list|)
argument_list|,
literal|"Worker-"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|workers
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|workers
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
name|entry
range|:
name|exceptions
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|// System.out.println("exception in thread " + entry.getKey());
throw|throw
name|entry
operator|.
name|getValue
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-1807"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|addNodesConcurrent2
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Thread
name|mainThread
init|=
name|Thread
operator|.
name|currentThread
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
name|NUM_CLUSTER_NODES
condition|;
name|i
operator|++
control|)
block|{
name|DocumentMK
name|mk
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|createConnection
argument_list|()
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|mks
operator|.
name|add
argument_list|(
name|mk
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
name|exceptions
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|stop
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|final
name|UncaughtExceptionHandler
name|ueh
init|=
operator|new
name|UncaughtExceptionHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|uncaughtException
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|RuntimeException
name|r
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"Exception in thread "
operator|+
name|t
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|r
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
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
name|mks
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|DocumentMK
name|mk
init|=
name|mks
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|Repository
name|repo
init|=
operator|new
name|Jcr
argument_list|(
name|mk
operator|.
name|getNodeStore
argument_list|()
argument_list|)
operator|.
name|createRepository
argument_list|()
decl_stmt|;
name|repos
operator|.
name|add
argument_list|(
name|repo
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|w
init|=
literal|0
init|;
name|w
operator|<=
name|WORKER_COUNT
condition|;
name|w
operator|++
control|)
block|{
specifier|final
name|String
name|name
init|=
literal|"Worker-"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|"-"
operator|+
operator|(
name|w
operator|+
literal|1
operator|)
decl_stmt|;
specifier|final
name|Runnable
name|r
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|final
name|Session
name|session
init|=
name|createAdminSession
argument_list|(
name|repo
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
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
name|Uninterruptibles
operator|.
name|awaitUninterruptibly
argument_list|(
name|latch
argument_list|)
expr_stmt|;
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Node
name|node
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|name
operator|+
name|count
operator|++
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
name|NODE_COUNT
operator|&&
operator|!
name|stop
operator|.
name|get
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|node
operator|.
name|addNode
argument_list|(
literal|"node"
operator|+
name|j
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|RuntimeException
name|r
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"Exception in thread "
operator|+
name|name
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|r
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|exceptions
operator|.
name|put
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|stop
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|mainThread
operator|.
name|interrupt
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
block|}
block|}
decl_stmt|;
comment|//Last runnable would be a long running one
name|Runnable
name|runnable
init|=
name|r
decl_stmt|;
if|if
condition|(
name|w
operator|==
name|WORKER_COUNT
condition|)
block|{
name|runnable
operator|=
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|stop
operator|.
name|get
argument_list|()
condition|)
block|{
name|r
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
block|}
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|runnable
argument_list|)
decl_stmt|;
name|t
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|t
operator|.
name|setUncaughtExceptionHandler
argument_list|(
name|ueh
argument_list|)
expr_stmt|;
name|workers
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Thread
name|t
range|:
name|workers
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|stop
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|Thread
name|t
range|:
name|workers
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
name|entry
range|:
name|exceptions
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|// System.out.println("exception in thread " + entry.getKey());
throw|throw
name|entry
operator|.
name|getValue
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodes
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|DocumentMK
name|mk
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|createConnection
argument_list|()
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|mks
operator|.
name|add
argument_list|(
name|mk
argument_list|)
expr_stmt|;
block|}
specifier|final
name|DocumentMK
name|mk1
init|=
name|mks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|DocumentMK
name|mk2
init|=
name|mks
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Repository
name|r1
init|=
operator|new
name|Jcr
argument_list|(
name|mk1
operator|.
name|getNodeStore
argument_list|()
argument_list|)
operator|.
name|createRepository
argument_list|()
decl_stmt|;
name|repos
operator|.
name|add
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|Repository
name|r2
init|=
operator|new
name|Jcr
argument_list|(
name|mk2
operator|.
name|getNodeStore
argument_list|()
argument_list|)
operator|.
name|createRepository
argument_list|()
decl_stmt|;
name|repos
operator|.
name|add
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|Session
name|s1
init|=
name|r1
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
name|Session
name|s2
init|=
name|r2
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
name|ensureIndex
argument_list|(
name|s1
operator|.
name|getRootNode
argument_list|()
argument_list|,
name|PROP_NAME
argument_list|)
expr_stmt|;
name|ensureIndex
argument_list|(
name|s2
operator|.
name|getRootNode
argument_list|()
argument_list|,
name|PROP_NAME
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
name|exceptions
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|createNodes
argument_list|(
name|s1
argument_list|,
literal|"testroot-1"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|exceptions
argument_list|)
expr_stmt|;
name|createNodes
argument_list|(
name|s2
argument_list|,
literal|"testroot-2"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|exceptions
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
name|entry
range|:
name|exceptions
operator|.
name|entrySet
argument_list|()
control|)
block|{
throw|throw
name|entry
operator|.
name|getValue
argument_list|()
throw|;
block|}
name|s1
operator|.
name|logout
argument_list|()
expr_stmt|;
name|s2
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodes2
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|DocumentMK
name|mk
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|createConnection
argument_list|()
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|mks
operator|.
name|add
argument_list|(
name|mk
argument_list|)
expr_stmt|;
block|}
specifier|final
name|DocumentMK
name|mk1
init|=
name|mks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|DocumentMK
name|mk2
init|=
name|mks
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|DocumentMK
name|mk3
init|=
name|mks
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|Repository
name|r1
init|=
operator|new
name|Jcr
argument_list|(
name|mk1
operator|.
name|getNodeStore
argument_list|()
argument_list|)
operator|.
name|createRepository
argument_list|()
decl_stmt|;
name|repos
operator|.
name|add
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|Repository
name|r2
init|=
operator|new
name|Jcr
argument_list|(
name|mk2
operator|.
name|getNodeStore
argument_list|()
argument_list|)
operator|.
name|createRepository
argument_list|()
decl_stmt|;
name|repos
operator|.
name|add
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|Repository
name|r3
init|=
operator|new
name|Jcr
argument_list|(
name|mk3
operator|.
name|getNodeStore
argument_list|()
argument_list|)
operator|.
name|createRepository
argument_list|()
decl_stmt|;
name|repos
operator|.
name|add
argument_list|(
name|r3
argument_list|)
expr_stmt|;
name|Session
name|s1
init|=
name|r1
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
name|Session
name|s2
init|=
name|r2
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
name|Session
name|s3
init|=
name|r3
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
name|ensureIndex
argument_list|(
name|s1
operator|.
name|getRootNode
argument_list|()
argument_list|,
name|PROP_NAME
argument_list|)
expr_stmt|;
name|runBackgroundOps
argument_list|(
name|mk1
argument_list|)
expr_stmt|;
name|runBackgroundOps
argument_list|(
name|mk2
argument_list|)
expr_stmt|;
name|runBackgroundOps
argument_list|(
name|mk3
argument_list|)
expr_stmt|;
comment|// begin test
name|Node
name|root2
init|=
name|s2
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"testroot-Worker-2"
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|createNodes
argument_list|(
name|root2
argument_list|,
literal|"testnode0"
argument_list|)
expr_stmt|;
name|s2
operator|.
name|save
argument_list|()
expr_stmt|;
name|createNodes
argument_list|(
name|root2
argument_list|,
literal|"testnode1"
argument_list|)
expr_stmt|;
name|runBackgroundOps
argument_list|(
name|mk1
argument_list|)
expr_stmt|;
name|runBackgroundOps
argument_list|(
name|mk3
argument_list|)
expr_stmt|;
name|runBackgroundOps
argument_list|(
name|mk2
argument_list|)
expr_stmt|;
comment|// publish 'testroot-Worker-2/testnode0'
name|Node
name|root3
init|=
name|s3
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"testroot-Worker-3"
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|createNodes
argument_list|(
name|root3
argument_list|,
literal|"testnode0"
argument_list|)
expr_stmt|;
name|s2
operator|.
name|save
argument_list|()
expr_stmt|;
name|createNodes
argument_list|(
name|root2
argument_list|,
literal|"testnode2"
argument_list|)
expr_stmt|;
name|runBackgroundOps
argument_list|(
name|mk1
argument_list|)
expr_stmt|;
comment|// sees 'testroot-Worker-2/testnode0'
name|runBackgroundOps
argument_list|(
name|mk3
argument_list|)
expr_stmt|;
comment|// sees 'testroot-Worker-2/testnode0'
name|runBackgroundOps
argument_list|(
name|mk2
argument_list|)
expr_stmt|;
comment|// publish 'testroot-Worker-2/testnode1'
comment|// subsequent read on mk3 will read already published docs from mk2
name|s3
operator|.
name|save
argument_list|()
expr_stmt|;
name|createNodes
argument_list|(
name|root3
argument_list|,
literal|"testnode1"
argument_list|)
expr_stmt|;
name|Node
name|root1
init|=
name|s1
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"testroot-Worker-1"
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|createNodes
argument_list|(
name|root1
argument_list|,
literal|"testnode0"
argument_list|)
expr_stmt|;
name|s2
operator|.
name|save
argument_list|()
expr_stmt|;
name|createNodes
argument_list|(
name|root2
argument_list|,
literal|"testnode3"
argument_list|)
expr_stmt|;
name|runBackgroundOps
argument_list|(
name|mk1
argument_list|)
expr_stmt|;
name|runBackgroundOps
argument_list|(
name|mk3
argument_list|)
expr_stmt|;
name|runBackgroundOps
argument_list|(
name|mk2
argument_list|)
expr_stmt|;
name|s1
operator|.
name|save
argument_list|()
expr_stmt|;
name|createNodes
argument_list|(
name|root1
argument_list|,
literal|"testnode1"
argument_list|)
expr_stmt|;
name|s3
operator|.
name|save
argument_list|()
expr_stmt|;
name|createNodes
argument_list|(
name|root3
argument_list|,
literal|"testnode2"
argument_list|)
expr_stmt|;
name|runBackgroundOps
argument_list|(
name|mk1
argument_list|)
expr_stmt|;
name|s1
operator|.
name|save
argument_list|()
expr_stmt|;
name|s1
operator|.
name|logout
argument_list|()
expr_stmt|;
name|s2
operator|.
name|logout
argument_list|()
expr_stmt|;
name|s3
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rebaseVisibility
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|DocumentMK
name|mk
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|createConnection
argument_list|()
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|mks
operator|.
name|add
argument_list|(
name|mk
argument_list|)
expr_stmt|;
block|}
specifier|final
name|DocumentMK
name|mk1
init|=
name|mks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|DocumentMK
name|mk2
init|=
name|mks
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Repository
name|r1
init|=
operator|new
name|Jcr
argument_list|(
name|mk1
operator|.
name|getNodeStore
argument_list|()
argument_list|)
operator|.
name|createRepository
argument_list|()
decl_stmt|;
name|repos
operator|.
name|add
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|Repository
name|r2
init|=
operator|new
name|Jcr
argument_list|(
name|mk2
operator|.
name|getNodeStore
argument_list|()
argument_list|)
operator|.
name|createRepository
argument_list|()
decl_stmt|;
name|repos
operator|.
name|add
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|Session
name|s1
init|=
name|r1
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
name|Session
name|s2
init|=
name|r2
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
name|Node
name|root1
init|=
name|s1
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"session-1"
argument_list|)
decl_stmt|;
name|s1
operator|.
name|save
argument_list|()
expr_stmt|;
name|Node
name|root2
init|=
name|s2
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"session-2"
argument_list|)
decl_stmt|;
name|s2
operator|.
name|save
argument_list|()
expr_stmt|;
name|runBackgroundOps
argument_list|(
name|mk1
argument_list|)
expr_stmt|;
name|runBackgroundOps
argument_list|(
name|mk2
argument_list|)
expr_stmt|;
name|runBackgroundOps
argument_list|(
name|mk1
argument_list|)
expr_stmt|;
name|createNodes
argument_list|(
name|root1
argument_list|,
literal|"nodes"
argument_list|)
expr_stmt|;
name|createNodes
argument_list|(
name|root2
argument_list|,
literal|"nodes"
argument_list|)
expr_stmt|;
name|s2
operator|.
name|save
argument_list|()
expr_stmt|;
name|runBackgroundOps
argument_list|(
name|mk2
argument_list|)
expr_stmt|;
name|runBackgroundOps
argument_list|(
name|mk1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|s1
operator|.
name|getRootNode
argument_list|()
operator|.
name|hasNode
argument_list|(
literal|"session-2/nodes"
argument_list|)
argument_list|)
expr_stmt|;
name|s1
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s1
operator|.
name|getRootNode
argument_list|()
operator|.
name|hasNode
argument_list|(
literal|"session-2/nodes"
argument_list|)
argument_list|)
expr_stmt|;
name|s1
operator|.
name|logout
argument_list|()
expr_stmt|;
name|s2
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|MongoConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|OakMongoNSRepositoryStub
operator|.
name|createConnection
argument_list|(
name|ConcurrentAddNodesClusterIT
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|dropDB
parameter_list|()
throws|throws
name|Exception
block|{
name|MongoConnection
name|con
init|=
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|con
operator|.
name|getDB
argument_list|()
operator|.
name|dropDatabase
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|initRepository
parameter_list|()
throws|throws
name|Exception
block|{
name|MongoConnection
name|con
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|DocumentMK
name|mk
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|con
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|Repository
name|repository
init|=
operator|new
name|Jcr
argument_list|(
name|mk
operator|.
name|getNodeStore
argument_list|()
argument_list|)
operator|.
name|createRepository
argument_list|()
decl_stmt|;
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
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
name|dispose
argument_list|(
name|repository
argument_list|)
expr_stmt|;
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
comment|// closes connection as well
block|}
specifier|private
specifier|static
name|void
name|ensureIndex
parameter_list|(
name|Node
name|root
parameter_list|,
name|String
name|propertyName
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|indexDef
init|=
name|root
operator|.
name|getNode
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexDef
operator|.
name|hasNode
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
return|return;
block|}
name|Node
name|index
init|=
name|indexDef
operator|.
name|addNode
argument_list|(
name|propertyName
argument_list|,
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
decl_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
argument_list|,
name|PropertyIndexEditorProvider
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|PROPERTY_NAMES
argument_list|,
operator|new
name|String
index|[]
block|{
name|propertyName
block|}
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
expr_stmt|;
try|try
block|{
name|root
operator|.
name|getSession
argument_list|()
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
comment|// created by other thread -> ignore
name|root
operator|.
name|getSession
argument_list|()
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|runBackgroundOps
parameter_list|(
name|DocumentMK
name|mk
parameter_list|)
throws|throws
name|Exception
block|{
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|final
class|class
name|Worker
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|Repository
name|repo
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
name|exceptions
decl_stmt|;
name|Worker
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
name|exceptions
parameter_list|)
block|{
name|this
operator|.
name|repo
operator|=
name|repo
expr_stmt|;
name|this
operator|.
name|exceptions
operator|=
name|exceptions
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Session
name|session
init|=
name|createAdminSession
argument_list|(
name|repo
argument_list|)
decl_stmt|;
try|try
block|{
name|ensureIndex
argument_list|(
name|session
operator|.
name|getRootNode
argument_list|()
argument_list|,
name|PROP_NAME
argument_list|)
expr_stmt|;
name|String
name|nodeName
init|=
literal|"testroot-"
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|createNodes
argument_list|(
name|session
argument_list|,
name|nodeName
argument_list|,
name|LOOP_COUNT
argument_list|,
name|NODE_COUNT
argument_list|,
name|exceptions
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|put
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|Session
name|createAdminSession
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
name|void
name|createNodes
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|int
name|loopCount
parameter_list|,
name|int
name|nodeCount
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
name|exceptions
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|nodeName
argument_list|,
literal|"nt:unstructured"
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
name|loopCount
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|root
operator|.
name|addNode
argument_list|(
literal|"testnode"
operator|+
name|i
argument_list|,
literal|"nt:unstructured"
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
name|nodeCount
condition|;
name|j
operator|++
control|)
block|{
name|Node
name|child
init|=
name|node
operator|.
name|addNode
argument_list|(
literal|"node"
operator|+
name|j
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|child
operator|.
name|setProperty
argument_list|(
name|PROP_NAME
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|exceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
break|break;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|createNodes
parameter_list|(
name|Node
name|parent
parameter_list|,
name|String
name|child
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|node
init|=
name|parent
operator|.
name|addNode
argument_list|(
name|child
argument_list|,
literal|"nt:unstructured"
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
name|NODE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|c
init|=
name|node
operator|.
name|addNode
argument_list|(
literal|"node"
operator|+
name|i
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|c
operator|.
name|setProperty
argument_list|(
name|PROP_NAME
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

