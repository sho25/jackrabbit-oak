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
name|jcr
package|;
end_package

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
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|Calendar
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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Credentials
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
name|FixturesHelper
operator|.
name|Fixture
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
name|OrderedIndex
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

begin_class
specifier|public
class|class
name|OrderedIndexConcurrentClusterIT
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
name|OrderedIndexConcurrentClusterIT
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|CACHE_SIZE
init|=
literal|32
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
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
name|COUNT
init|=
literal|5
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Credentials
name|ADMIN
init|=
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
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|INDEX_NODE_NAME
init|=
literal|"lastModified"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|INDEX_PROPERTY
init|=
literal|"lastModified"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Fixture
argument_list|>
name|FIXTURES
init|=
name|FixturesHelper
operator|.
name|getFixtures
argument_list|()
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
comment|// ----- SHARED WITH ConcurrentAddNodesClusterIT (later refactoring) -----
comment|// vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|fixturesCheck
parameter_list|()
block|{
comment|// running only on DocumentNS case
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|FIXTURES
operator|.
name|contains
argument_list|(
name|Fixture
operator|.
name|DOCUMENT_NS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|mongoDBAvailable
parameter_list|()
block|{
specifier|final
name|boolean
name|mongoAvailable
init|=
name|OakMongoMKRepositoryStub
operator|.
name|isMongoDBAvailable
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|mongoAvailable
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Mongo DB is not available. Skipping the test"
argument_list|)
expr_stmt|;
block|}
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|mongoAvailable
argument_list|)
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
name|OakMongoMKRepositoryStub
operator|.
name|createConnection
argument_list|(
name|OrderedIndexConcurrentClusterIT
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
return|;
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
name|ADMIN
argument_list|)
decl_stmt|;
name|ensureIndex
argument_list|(
name|session
argument_list|)
expr_stmt|;
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
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
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
name|dropDB
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
name|repo
operator|.
name|login
argument_list|(
name|ADMIN
argument_list|)
decl_stmt|;
name|ensureIndex
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|String
name|nodeName
init|=
name|getNodeName
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
decl_stmt|;
name|deleteNodes
argument_list|(
name|session
argument_list|,
name|nodeName
argument_list|,
name|exceptions
argument_list|)
expr_stmt|;
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
comment|// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
comment|// ----- SHARED WITH ConcurrentAddNodesClusterIT (later refactoring) -----
comment|// Slightly modified by the ConcurrentAddNodesClusterIT making the indexes property a Date.
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
name|Calendar
name|calendar
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|SimpleDateFormat
name|sdf
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"dd/MM/yyyy hh:mm:ss.S"
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
name|calendar
operator|=
name|Calendar
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|child
operator|.
name|setProperty
argument_list|(
name|INDEX_PROPERTY
argument_list|,
name|calendar
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
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
operator|&&
name|i
operator|%
literal|10
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{} looped {}. Last calendar: {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|nodeName
block|,
name|i
block|,
name|sdf
operator|.
name|format
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
block|}
argument_list|)
expr_stmt|;
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
name|deleteNodes
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|nodeName
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
decl_stmt|;
try|try
block|{
name|root
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Not found. {}"
argument_list|,
name|nodeName
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|NodeIterator
name|children
init|=
name|root
operator|.
name|getNodes
argument_list|()
decl_stmt|;
while|while
condition|(
name|children
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Node
name|node
init|=
name|children
operator|.
name|nextNode
argument_list|()
decl_stmt|;
name|NodeIterator
name|children2
init|=
name|node
operator|.
name|getNodes
argument_list|()
decl_stmt|;
while|while
condition|(
name|children2
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|children2
operator|.
name|nextNode
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"deleting /{}/{}"
argument_list|,
name|nodeName
argument_list|,
name|node
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|String
name|getNodeName
parameter_list|(
specifier|final
name|Thread
name|t
parameter_list|)
block|{
return|return
literal|"testroot-"
operator|+
name|t
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|void
name|raiseExceptions
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
name|exceptions
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|exceptions
operator|!=
literal|null
condition|)
block|{
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in thread {}"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|entry
operator|.
name|getValue
argument_list|()
throw|;
block|}
block|}
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-2075"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|deleteConcurrently
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|loop
init|=
literal|1400
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|COUNT
decl_stmt|;
specifier|final
name|int
name|clusters
init|=
name|NUM_CLUSTER_NODES
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Adding a total of {} nodes evely spread across cluster. Loop: {}, Count: {}, Cluster nodes: {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|loop
operator|*
name|count
operator|*
name|clusters
block|,
name|loop
block|,
name|count
block|,
name|clusters
block|}
argument_list|)
expr_stmt|;
comment|// creating instances
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|clusters
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
name|memoryCacheSize
argument_list|(
name|CACHE_SIZE
argument_list|)
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
comment|// initialising repositories and creating workers
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
name|Session
name|session
init|=
name|repo
operator|.
name|login
argument_list|(
name|ADMIN
argument_list|)
decl_stmt|;
name|ensureIndex
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
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
comment|// we know we have at least repos[0]
name|Repository
name|repo
init|=
name|repos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Session
name|session
init|=
name|repo
operator|.
name|login
argument_list|(
name|ADMIN
argument_list|)
decl_stmt|;
name|ensureIndex
argument_list|(
name|session
argument_list|)
expr_stmt|;
comment|// initialising the repository sequentially to avoid any possible
comment|// concurrency errors during inserts
for|for
control|(
name|Thread
name|w
range|:
name|workers
control|)
block|{
name|String
name|nodeName
init|=
name|getNodeName
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|createNodes
argument_list|(
name|session
argument_list|,
name|nodeName
argument_list|,
name|loop
argument_list|,
name|count
argument_list|,
name|exceptions
argument_list|)
expr_stmt|;
block|}
comment|// extra save for being sure.
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
if|if
condition|(
name|exceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// ensuring the cluster is aligned before triggering in order to avoid any
comment|// PathNotFoundException
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
name|getNodeStore
argument_list|()
operator|.
name|runBackgroundOperations
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
block|}
else|else
block|{
comment|// something where wrong during the insert. halting
name|LOG
operator|.
name|error
argument_list|(
literal|"Something went wrong during insert"
argument_list|)
expr_stmt|;
block|}
name|raiseExceptions
argument_list|(
name|exceptions
argument_list|)
expr_stmt|;
block|}
comment|/**      * creates the index in the provided session      *       * @param session      */
specifier|private
specifier|static
name|void
name|ensureIndex
parameter_list|(
name|Session
name|session
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
decl_stmt|;
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
name|Node
name|index
decl_stmt|;
if|if
condition|(
operator|!
name|indexDef
operator|.
name|hasNode
argument_list|(
name|INDEX_NODE_NAME
argument_list|)
condition|)
block|{
name|index
operator|=
name|indexDef
operator|.
name|addNode
argument_list|(
name|INDEX_NODE_NAME
argument_list|,
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
argument_list|,
name|OrderedIndex
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
name|INDEX_PROPERTY
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
block|}
block|}
end_class

end_unit

