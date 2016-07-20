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
name|document
operator|.
name|mongo
package|;
end_package

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
name|Set
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
name|com
operator|.
name|mongodb
operator|.
name|DB
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
name|cache
operator|.
name|CacheStats
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
name|AbstractJournalTest
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
name|DocumentNodeStore
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
name|DocumentStore
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
name|JournalGarbageCollector
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
name|MongoConnectionFactory
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
name|plugins
operator|.
name|document
operator|.
name|memory
operator|.
name|MemoryDocumentStore
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
name|spi
operator|.
name|blob
operator|.
name|MemoryBlobStore
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
name|stats
operator|.
name|Clock
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
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_class
specifier|public
class|class
name|JournalIT
extends|extends
name|AbstractJournalTest
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
name|JournalIT
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|MongoConnectionFactory
name|connectionFactory
init|=
operator|new
name|MongoConnectionFactory
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|checkMongoDbAvailable
parameter_list|()
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|MongoUtils
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|super
operator|.
name|clear
argument_list|()
expr_stmt|;
name|MongoConnection
name|mongoConnection
init|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|mongoConnection
operator|.
name|getDB
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cacheInvalidationTest
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|DocumentNodeStore
name|ns1
init|=
name|createMK
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
specifier|final
name|DocumentNodeStore
name|ns2
init|=
name|createMK
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"cache size 1: "
operator|+
name|getCacheElementCount
argument_list|(
name|ns1
operator|.
name|getDocumentStore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// invalidate cache under test first
name|ns1
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|invalidateCache
argument_list|()
expr_stmt|;
block|{
name|DocumentStore
name|s
init|=
name|ns1
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"m.size="
operator|+
name|getCacheElementCount
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"cache size 2: "
operator|+
name|getCacheElementCount
argument_list|(
name|ns1
operator|.
name|getDocumentStore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// first create child node in instance 1
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|createRandomPaths
argument_list|(
literal|1
argument_list|,
literal|5000000
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"at "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|getOrCreate
argument_list|(
name|ns1
argument_list|,
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|paths2
init|=
name|createRandomPaths
argument_list|(
literal|20
argument_list|,
literal|2345
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|getOrCreate
argument_list|(
name|ns1
argument_list|,
name|paths2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|assertDocCache
argument_list|(
name|ns1
argument_list|,
literal|true
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|{
name|DocumentStore
name|s
init|=
name|ns1
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"m.size="
operator|+
name|getCacheElementCount
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"cache size 2: "
operator|+
name|getCacheElementCount
argument_list|(
name|ns1
operator|.
name|getDocumentStore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"loop "
operator|+
name|j
operator|+
literal|", "
operator|+
operator|(
name|now
operator|-
name|time
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
name|time
operator|=
name|now
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|electedPaths
init|=
name|choose
argument_list|(
name|paths2
argument_list|,
name|random
operator|.
name|nextInt
argument_list|(
literal|30
argument_list|)
argument_list|)
decl_stmt|;
block|{
comment|// choose a random few from above created paths and modify them
specifier|final
name|long
name|t1
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ns2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// make sure ns2 has the latest from ns1
specifier|final
name|long
name|t2
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"ns2 background took "
operator|+
operator|(
name|t2
operator|-
name|t1
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|electedPath
range|:
name|electedPaths
control|)
block|{
comment|// modify /child in another instance 2
name|setProperty
argument_list|(
name|ns2
argument_list|,
name|electedPath
argument_list|,
literal|"p"
argument_list|,
literal|"ns2"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|t3
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"setting props "
operator|+
operator|(
name|t3
operator|-
name|t2
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
name|ns2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
specifier|final
name|long
name|t4
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"ns2 background took2 "
operator|+
operator|(
name|t4
operator|-
name|t3
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
comment|// that should not have changed the fact that we have it cached in 'ns1'
for|for
control|(
name|String
name|electedPath
range|:
name|electedPaths
control|)
block|{
name|assertDocCache
argument_list|(
name|ns1
argument_list|,
literal|true
argument_list|,
name|electedPath
argument_list|)
expr_stmt|;
block|}
comment|// doing a backgroundOp now should trigger invalidation
comment|// which thx to the external modification will remove the entry from the cache:
name|ns1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|electedPath
range|:
name|electedPaths
control|)
block|{
name|assertDocCache
argument_list|(
name|ns1
argument_list|,
literal|false
argument_list|,
name|electedPath
argument_list|)
expr_stmt|;
block|}
comment|// when I access it again with 'ns1', then it gets cached again:
for|for
control|(
name|String
name|electedPath
range|:
name|electedPaths
control|)
block|{
name|getOrCreate
argument_list|(
name|ns1
argument_list|,
name|electedPath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertDocCache
argument_list|(
name|ns1
argument_list|,
literal|true
argument_list|,
name|electedPath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|largeCleanupTest
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create more than DELETE_BATCH_SIZE of entries and clean them up
comment|// should make sure to loop in JournalGarbageCollector.gc such
comment|// that it would find issue described here:
comment|// https://issues.apache.org/jira/browse/OAK-2829?focusedCommentId=14585733&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-14585733
name|doLargeCleanupTest
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|doLargeCleanupTest
argument_list|(
literal|200
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
comment|// using offset as to not make sure to always create new entries
name|doLargeCleanupTest
argument_list|(
literal|2000
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|doLargeCleanupTest
argument_list|(
literal|20000
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
comment|// using 'size' much larger than 30k will be tremendously slow due to ordered node
block|}
annotation|@
name|Test
specifier|public
name|void
name|simpleCacheInvalidationTest
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|DocumentNodeStore
name|ns1
init|=
name|createMK
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
specifier|final
name|DocumentNodeStore
name|ns2
init|=
name|createMK
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
comment|// invalidate cache under test first
name|ns1
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|invalidateCache
argument_list|()
expr_stmt|;
comment|// first create child node in instance 1
name|getOrCreate
argument_list|(
name|ns1
argument_list|,
literal|"/child"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertDocCache
argument_list|(
name|ns1
argument_list|,
literal|true
argument_list|,
literal|"/child"
argument_list|)
expr_stmt|;
block|{
comment|// modify /child in another instance 2
name|ns2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// read latest changes from ns1
name|setProperty
argument_list|(
name|ns2
argument_list|,
literal|"/child"
argument_list|,
literal|"p"
argument_list|,
literal|"ns2"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// that should not have changed the fact that we have it cached in 'ns'
name|assertDocCache
argument_list|(
name|ns1
argument_list|,
literal|true
argument_list|,
literal|"/child"
argument_list|)
expr_stmt|;
comment|// doing a backgroundOp now should trigger invalidation
comment|// which thx to the external modification will remove the entry from the cache:
name|ns1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|assertDocCache
argument_list|(
name|ns1
argument_list|,
literal|false
argument_list|,
literal|"/child"
argument_list|)
expr_stmt|;
comment|// when I access it again with 'ns', then it gets cached again:
name|getOrCreate
argument_list|(
name|ns1
argument_list|,
literal|"/child"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertDocCache
argument_list|(
name|ns1
argument_list|,
literal|true
argument_list|,
literal|"/child"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doLargeCleanupTest
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|Exception
block|{
name|Clock
name|clock
init|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
decl_stmt|;
name|DocumentMK
name|mk1
init|=
name|createMK
argument_list|(
literal|0
comment|/* clusterId: 0 => uses clusterNodes collection */
argument_list|,
literal|0
argument_list|,
operator|new
name|MemoryDocumentStore
argument_list|()
argument_list|,
operator|new
name|MemoryBlobStore
argument_list|()
argument_list|)
decl_stmt|;
name|DocumentNodeStore
name|ns1
init|=
name|mk1
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
comment|// make sure we're visible and marked as active
name|renewClusterIdLease
argument_list|(
name|ns1
argument_list|)
expr_stmt|;
name|JournalGarbageCollector
name|gc
init|=
operator|new
name|JournalGarbageCollector
argument_list|(
name|ns1
argument_list|)
decl_stmt|;
name|clock
operator|.
name|getTimeIncreasing
argument_list|()
expr_stmt|;
name|clock
operator|.
name|getTimeIncreasing
argument_list|()
expr_stmt|;
name|gc
operator|.
name|gc
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
comment|// cleanup everything that might still be there
comment|// create entries as parametrized:
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|size
operator|+
name|offset
condition|;
name|i
operator|++
control|)
block|{
name|mk1
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"regular"
operator|+
name|i
operator|+
literal|"\": {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// always run background ops to 'flush' the change
comment|// into the journal:
name|ns1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// sleep 100millis
name|assertEquals
argument_list|(
name|size
argument_list|,
name|gc
operator|.
name|gc
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// should now be able to clean up everything
block|}
specifier|protected
name|DocumentMK
name|createMK
parameter_list|(
name|int
name|clusterId
parameter_list|,
name|int
name|asyncDelay
parameter_list|)
block|{
name|DB
name|db
init|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
operator|.
name|getDB
argument_list|()
decl_stmt|;
name|builder
operator|=
name|newDocumentMKBuilder
argument_list|()
expr_stmt|;
return|return
name|register
argument_list|(
name|builder
operator|.
name|setMongoDB
argument_list|(
name|db
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
name|asyncDelay
argument_list|)
operator|.
name|open
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|long
name|getCacheElementCount
parameter_list|(
name|DocumentStore
name|ds
parameter_list|)
block|{
if|if
condition|(
name|ds
operator|.
name|getCacheStats
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|long
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|CacheStats
name|cacheStats
range|:
name|ds
operator|.
name|getCacheStats
argument_list|()
control|)
block|{
name|count
operator|+=
name|cacheStats
operator|.
name|getElementCount
argument_list|()
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
block|}
end_class

end_unit

