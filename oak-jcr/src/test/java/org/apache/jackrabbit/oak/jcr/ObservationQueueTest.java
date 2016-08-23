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
name|java
operator|.
name|util
operator|.
name|Random
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
name|AtomicLong
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
name|NodeIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PathNotFoundException
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
name|Value
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
name|security
operator|.
name|AccessControlManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|Privilege
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
name|Iterables
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
name|api
operator|.
name|JackrabbitSession
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
name|api
operator|.
name|security
operator|.
name|JackrabbitAccessControlList
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|User
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|UserManager
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
name|commons
operator|.
name|jackrabbit
operator|.
name|authorization
operator|.
name|AccessControlUtils
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
name|fixture
operator|.
name|DocumentMongoFixture
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
name|cluster
operator|.
name|AbstractClusterTest
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
name|MongoUtils
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

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"Ignore long running ObservationQueueTest"
argument_list|)
specifier|public
class|class
name|ObservationQueueTest
extends|extends
name|AbstractClusterTest
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
name|ObservationQueueTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|long
name|RUNTIME
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|int
name|NUM_WRITERS
init|=
literal|10
decl_stmt|;
specifier|static
specifier|final
name|int
name|NUM_READERS
init|=
literal|10
decl_stmt|;
specifier|static
specifier|final
name|int
name|NUM_OBSERVERS
init|=
literal|10
decl_stmt|;
specifier|static
specifier|final
name|int
name|MAX_NODES_PER_WRITE
init|=
literal|30
decl_stmt|;
specifier|static
specifier|final
name|int
name|QUEUE_LENGTH
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|USER
init|=
literal|"user"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PASSWORD
init|=
literal|"password"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
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
specifier|private
name|List
argument_list|<
name|Thread
argument_list|>
name|writers
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Thread
argument_list|>
name|readers
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Thread
argument_list|>
name|observers
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Thread
argument_list|>
name|loggers
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|dropDB
parameter_list|()
block|{
name|MongoUtils
operator|.
name|dropDatabase
argument_list|(
literal|"oak-test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|logoutSessions
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Session
name|s
range|:
name|sessions
control|)
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|heavyLoad
parameter_list|()
throws|throws
name|Throwable
block|{
name|List
argument_list|<
name|Whiteboard
argument_list|>
name|whiteboards
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|w1
argument_list|,
name|w2
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Repository
argument_list|>
name|repos
init|=
name|Iterators
operator|.
name|cycle
argument_list|(
name|r1
argument_list|,
name|r2
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
name|NUM_WRITERS
condition|;
name|i
operator|++
control|)
block|{
name|Session
name|s
init|=
name|loginUser
argument_list|(
name|repos
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|s
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"session-"
operator|+
name|i
argument_list|,
literal|"oak:Unstructured"
argument_list|)
decl_stmt|;
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
name|writers
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|Writer
argument_list|(
name|n
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_READERS
condition|;
name|i
operator|++
control|)
block|{
name|Session
name|s
init|=
name|loginUser
argument_list|(
name|repos
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|readers
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|Reader
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|AtomicInteger
name|queueLength
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|loggers
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|QueueLogger
argument_list|(
name|whiteboards
argument_list|,
name|queueLength
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_OBSERVERS
condition|;
name|i
operator|++
control|)
block|{
name|Session
name|s
init|=
name|loginUser
argument_list|(
name|repos
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|observers
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|Observer
argument_list|(
name|s
argument_list|,
name|queueLength
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|Iterables
operator|.
name|concat
argument_list|(
name|writers
argument_list|,
name|readers
argument_list|,
name|observers
argument_list|,
name|loggers
argument_list|)
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
name|Iterables
operator|.
name|concat
argument_list|(
name|writers
argument_list|,
name|readers
argument_list|)
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Writes stopped. Waiting for observers..."
argument_list|)
expr_stmt|;
for|for
control|(
name|Thread
name|t
range|:
name|Iterables
operator|.
name|concat
argument_list|(
name|observers
argument_list|,
name|loggers
argument_list|)
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
name|Throwable
name|t
range|:
name|exceptions
control|)
block|{
throw|throw
name|t
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|NodeStoreFixture
name|getFixture
parameter_list|()
block|{
return|return
operator|new
name|DocumentMongoFixture
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeStore
name|createNodeStore
parameter_list|(
name|int
name|clusterNodeId
parameter_list|)
block|{
return|return
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setClusterId
argument_list|(
name|clusterNodeId
argument_list|)
operator|.
name|setMongoDB
argument_list|(
name|MongoUtils
operator|.
name|getConnection
argument_list|(
literal|"oak-test"
argument_list|)
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|setPersistentCache
argument_list|(
literal|"target/persistentCache"
operator|+
name|clusterNodeId
operator|+
literal|",time,size=128"
argument_list|)
operator|.
name|memoryCacheSize
argument_list|(
literal|256
operator|*
name|MB
argument_list|)
operator|.
name|getNodeStore
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|prepareTestData
parameter_list|(
name|Session
name|s
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|UserManager
name|uMgr
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|s
operator|)
operator|.
name|getUserManager
argument_list|()
decl_stmt|;
name|User
name|user
init|=
name|uMgr
operator|.
name|createUser
argument_list|(
name|USER
argument_list|,
name|PASSWORD
argument_list|)
decl_stmt|;
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|s
operator|.
name|getAccessControlManager
argument_list|()
decl_stmt|;
name|JackrabbitAccessControlList
name|tmpl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|tmpl
operator|.
name|addEntry
argument_list|(
name|user
operator|.
name|getPrincipal
argument_list|()
argument_list|,
operator|new
name|Privilege
index|[]
block|{
name|acMgr
operator|.
name|privilegeFromName
argument_list|(
name|Privilege
operator|.
name|JCR_ALL
argument_list|)
block|}
argument_list|,
literal|true
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Value
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|tmpl
operator|.
name|getPath
argument_list|()
argument_list|,
name|tmpl
argument_list|)
expr_stmt|;
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Jcr
name|customize
parameter_list|(
name|Jcr
name|jcr
parameter_list|)
block|{
return|return
name|super
operator|.
name|customize
argument_list|(
name|jcr
argument_list|)
operator|.
name|withObservationQueueLength
argument_list|(
name|QUEUE_LENGTH
argument_list|)
return|;
block|}
specifier|private
name|Session
name|loginUser
parameter_list|(
name|Repository
name|repo
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Session
name|s
init|=
name|repo
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER
argument_list|,
name|PASSWORD
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|sessions
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
specifier|private
specifier|static
name|void
name|log
parameter_list|(
name|String
name|msg
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|msg
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|logRead
parameter_list|(
name|Node
name|n
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|log
argument_list|(
literal|"Read node {}"
argument_list|,
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|Reader
extends|extends
name|Task
block|{
specifier|private
specifier|final
name|Random
name|r
decl_stmt|;
specifier|public
name|Reader
parameter_list|(
name|Session
name|s
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|this
operator|.
name|r
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|perform
parameter_list|()
throws|throws
name|Exception
block|{
name|s
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|nodes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeIterator
name|it
init|=
name|s
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNodes
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Node
name|n
init|=
name|it
operator|.
name|nextNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"session-"
argument_list|)
condition|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|nodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|Node
name|node
init|=
name|nodes
operator|.
name|get
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
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
literal|2
operator|&&
name|node
operator|!=
literal|null
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|node
operator|=
name|node
operator|.
name|getNode
argument_list|(
literal|"node-"
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|logRead
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
if|if
condition|(
name|node
operator|!=
literal|null
operator|&&
name|node
operator|.
name|hasProperty
argument_list|(
literal|"c"
argument_list|)
condition|)
block|{
name|long
name|count
init|=
name|node
operator|.
name|getProperty
argument_list|(
literal|"c"
argument_list|)
operator|.
name|getLong
argument_list|()
decl_stmt|;
name|logRead
argument_list|(
name|node
operator|.
name|getNode
argument_list|(
literal|"node-"
operator|+
name|r
operator|.
name|nextInt
argument_list|(
operator|(
name|int
operator|)
name|count
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|Writer
extends|extends
name|Task
block|{
specifier|private
specifier|final
name|Random
name|r
decl_stmt|;
specifier|private
specifier|final
name|Node
name|node
decl_stmt|;
specifier|public
name|Writer
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|super
argument_list|(
name|node
operator|.
name|getSession
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|r
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|perform
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|p
init|=
name|node
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|p
operator|=
name|JcrUtils
operator|.
name|getOrAddNode
argument_list|(
name|p
argument_list|,
literal|"node-"
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
argument_list|,
literal|"oak:Unstructured"
argument_list|)
expr_stmt|;
block|}
comment|// set property or add node?
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|long
name|v
init|=
name|p
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
operator|.
name|getLong
argument_list|()
decl_stmt|;
name|log
argument_list|(
literal|"Set property to {} on {}"
argument_list|,
name|v
argument_list|,
name|p
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|numNodes
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|MAX_NODES_PER_WRITE
argument_list|)
operator|+
literal|1
decl_stmt|;
name|int
name|depth
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|numNodes
argument_list|)
operator|/
name|Math
operator|.
name|log
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|count
init|=
name|JcrUtils
operator|.
name|getLongProperty
argument_list|(
name|p
argument_list|,
literal|"c"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|p
operator|.
name|addNode
argument_list|(
literal|"node-"
operator|+
name|count
argument_list|,
literal|"oak:Unstructured"
argument_list|)
decl_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"c"
argument_list|,
operator|++
name|count
argument_list|)
expr_stmt|;
name|createNodes
argument_list|(
name|n
argument_list|,
operator|new
name|AtomicInteger
argument_list|(
operator|--
name|numNodes
argument_list|)
argument_list|,
name|depth
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Add node {}"
argument_list|,
name|n
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
name|void
name|createNodes
parameter_list|(
name|Node
name|parent
parameter_list|,
name|AtomicInteger
name|remaining
parameter_list|,
name|int
name|depth
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|depth
operator|--
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
argument_list|<
literal|3
operator|&&
name|remaining
operator|.
name|get
operator|(
operator|)
argument_list|>
literal|0
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|n
init|=
name|parent
operator|.
name|addNode
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"Add node {}"
argument_list|,
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|remaining
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|depth
operator|>
literal|0
condition|)
block|{
name|createNodes
argument_list|(
name|n
argument_list|,
name|remaining
argument_list|,
name|depth
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
class|class
name|Observer
extends|extends
name|Task
implements|implements
name|EventListener
block|{
specifier|private
specifier|final
name|AtomicLong
name|numEvents
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|queueLength
decl_stmt|;
specifier|public
name|Observer
parameter_list|(
name|Session
name|s
parameter_list|,
name|AtomicInteger
name|queueLength
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|this
operator|.
name|queueLength
operator|=
name|queueLength
expr_stmt|;
name|s
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getObservationManager
argument_list|()
operator|.
name|addEventListener
argument_list|(
name|this
argument_list|,
name|Event
operator|.
name|NODE_ADDED
operator||
name|Event
operator|.
name|PROPERTY_ADDED
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
block|}
annotation|@
name|Override
name|void
name|perform
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|running
parameter_list|()
block|{
return|return
name|super
operator|.
name|running
argument_list|()
operator|||
name|queueLength
operator|.
name|get
argument_list|()
operator|>
literal|0
return|;
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
name|events
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|numEvents
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
try|try
block|{
name|Event
name|e
init|=
name|events
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
name|String
name|p
init|=
name|e
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|log
argument_list|(
literal|"Event received {}"
argument_list|,
name|p
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|.
name|getType
argument_list|()
operator|==
name|Event
operator|.
name|PROPERTY_ADDED
condition|)
block|{
name|p
operator|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|getNode
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
class|class
name|QueueLogger
extends|extends
name|Task
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|Whiteboard
argument_list|>
name|whiteboards
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|queueLength
decl_stmt|;
name|QueueLogger
parameter_list|(
name|List
argument_list|<
name|Whiteboard
argument_list|>
name|whiteboards
parameter_list|,
name|AtomicInteger
name|queueLength
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|whiteboards
operator|=
name|whiteboards
expr_stmt|;
name|this
operator|.
name|queueLength
operator|=
name|queueLength
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|perform
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|stats
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Whiteboard
name|w
range|:
name|whiteboards
control|)
block|{
name|stats
operator|.
name|add
argument_list|(
name|queueStats
argument_list|(
name|w
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Observation queue stats: {}"
argument_list|,
name|stats
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|running
parameter_list|()
block|{
return|return
name|super
operator|.
name|running
argument_list|()
operator|||
name|queueLength
operator|.
name|get
argument_list|()
operator|>
literal|0
return|;
block|}
specifier|private
name|String
name|queueStats
parameter_list|(
name|Whiteboard
name|w
parameter_list|)
block|{
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
name|w
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
if|if
condition|(
name|len
operator|>=
literal|0
condition|)
block|{
name|queueLength
operator|.
name|set
argument_list|(
name|len
argument_list|)
expr_stmt|;
block|}
return|return
literal|""
operator|+
name|len
operator|+
literal|" ("
operator|+
name|ext
operator|+
literal|")"
return|;
block|}
block|}
specifier|abstract
class|class
name|Task
implements|implements
name|Runnable
block|{
specifier|final
name|Session
name|s
decl_stmt|;
name|long
name|end
decl_stmt|;
specifier|public
name|Task
parameter_list|(
name|Session
name|s
parameter_list|)
block|{
name|this
operator|.
name|s
operator|=
name|s
expr_stmt|;
block|}
specifier|protected
name|boolean
name|running
parameter_list|()
block|{
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|end
return|;
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
name|end
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|RUNTIME
expr_stmt|;
while|while
condition|(
name|running
argument_list|()
condition|)
block|{
name|perform
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
specifier|abstract
name|void
name|perform
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
block|}
end_class

end_unit

