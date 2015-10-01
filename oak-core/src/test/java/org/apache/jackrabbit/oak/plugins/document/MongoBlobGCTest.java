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
name|plugins
operator|.
name|blob
operator|.
name|datastore
operator|.
name|SharedDataStoreUtils
operator|.
name|SharedStoreRecordType
operator|.
name|REPOSITORY
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
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Executor
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
name|ThreadPoolExecutor
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
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Stopwatch
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
name|ImmutableList
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|io
operator|.
name|Closeables
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBCollection
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|Blob
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
name|blob
operator|.
name|BlobReferenceRetriever
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
name|blob
operator|.
name|GarbageCollectorFileState
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
name|blob
operator|.
name|MarkSweepGarbageCollector
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
name|blob
operator|.
name|ReferencedBlob
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
name|blob
operator|.
name|SharedDataStore
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
name|blob
operator|.
name|datastore
operator|.
name|SharedDataStoreUtils
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
name|VersionGarbageCollector
operator|.
name|VersionGCStats
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
name|mongo
operator|.
name|MongoBlobReferenceIterator
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
name|identifier
operator|.
name|ClusterRepositoryInfo
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
name|GarbageCollectableBlobStore
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
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Tests for MongoMK GC  */
end_comment

begin_class
specifier|public
class|class
name|MongoBlobGCTest
extends|extends
name|AbstractMongoConnectionTest
block|{
specifier|private
name|Clock
name|clock
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MongoBlobGCTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|DataStoreState
name|setUp
parameter_list|(
name|boolean
name|deleteDirect
parameter_list|)
throws|throws
name|Exception
block|{
name|DocumentNodeStore
name|s
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|a
init|=
name|s
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|int
name|number
init|=
literal|10
decl_stmt|;
name|int
name|maxDeleted
init|=
literal|5
decl_stmt|;
comment|// track the number of the assets to be deleted
name|List
argument_list|<
name|Integer
argument_list|>
name|processed
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
literal|47
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
name|maxDeleted
condition|;
name|i
operator|++
control|)
block|{
name|int
name|n
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|number
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|processed
operator|.
name|contains
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|processed
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
name|DataStoreState
name|state
init|=
operator|new
name|DataStoreState
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
name|number
condition|;
name|i
operator|++
control|)
block|{
name|Blob
name|b
init|=
name|s
operator|.
name|createBlob
argument_list|(
name|randomStream
argument_list|(
name|i
argument_list|,
literal|16516
argument_list|)
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|idIter
init|=
operator|(
operator|(
name|GarbageCollectableBlobStore
operator|)
name|s
operator|.
name|getBlobStore
argument_list|()
operator|)
operator|.
name|resolveChunks
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|idIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|chunk
init|=
name|idIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|state
operator|.
name|blobsAdded
operator|.
name|add
argument_list|(
name|chunk
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|processed
operator|.
name|contains
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|state
operator|.
name|blobsPresent
operator|.
name|add
argument_list|(
name|chunk
argument_list|)
expr_stmt|;
block|}
block|}
name|a
operator|.
name|child
argument_list|(
literal|"c"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
name|b
argument_list|)
expr_stmt|;
comment|// Add a duplicated entry
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|a
operator|.
name|child
argument_list|(
literal|"cdup"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
name|s
operator|.
name|merge
argument_list|(
name|a
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
if|if
condition|(
name|deleteDirect
condition|)
block|{
for|for
control|(
name|int
name|id
range|:
name|processed
control|)
block|{
name|deleteFromMongo
argument_list|(
literal|"c"
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|a
operator|=
name|s
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|id
range|:
name|processed
control|)
block|{
name|a
operator|.
name|child
argument_list|(
literal|"c"
operator|+
name|id
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|s
operator|.
name|merge
argument_list|(
name|a
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
name|long
name|maxAge
init|=
literal|10
decl_stmt|;
comment|// hours
comment|// 1. Go past GC age and check no GC done as nothing deleted
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
name|maxAge
argument_list|)
argument_list|)
expr_stmt|;
name|VersionGarbageCollector
name|vGC
init|=
name|s
operator|.
name|getVersionGarbageCollector
argument_list|()
decl_stmt|;
name|VersionGCStats
name|stats
init|=
name|vGC
operator|.
name|gc
argument_list|(
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|processed
operator|.
name|size
argument_list|()
argument_list|,
name|stats
operator|.
name|deletedDocGCCount
argument_list|)
expr_stmt|;
block|}
return|return
name|state
return|;
block|}
specifier|private
class|class
name|DataStoreState
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|blobsAdded
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|blobsPresent
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
block|}
specifier|private
name|HashSet
argument_list|<
name|String
argument_list|>
name|addInlined
parameter_list|()
throws|throws
name|Exception
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|DocumentNodeStore
name|s
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|a
init|=
name|s
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|int
name|number
init|=
literal|12
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
name|number
condition|;
name|i
operator|++
control|)
block|{
name|Blob
name|b
init|=
name|s
operator|.
name|createBlob
argument_list|(
name|randomStream
argument_list|(
name|i
argument_list|,
literal|50
argument_list|)
argument_list|)
decl_stmt|;
name|a
operator|.
name|child
argument_list|(
literal|"cinline"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|merge
argument_list|(
name|a
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
return|return
name|set
return|;
block|}
specifier|private
name|void
name|deleteFromMongo
parameter_list|(
name|String
name|nodeId
parameter_list|)
block|{
name|DBCollection
name|coll
init|=
name|mongoConnection
operator|.
name|getDB
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|"nodes"
argument_list|)
decl_stmt|;
name|BasicDBObject
name|blobNodeObj
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|blobNodeObj
operator|.
name|put
argument_list|(
literal|"_id"
argument_list|,
literal|"1:/"
operator|+
name|nodeId
argument_list|)
expr_stmt|;
name|coll
operator|.
name|remove
argument_list|(
name|blobNodeObj
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|gcDirectMongoDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStoreState
name|state
init|=
name|setUp
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|existingAfterGC
init|=
name|gc
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Sets
operator|.
name|symmetricDifference
argument_list|(
name|state
operator|.
name|blobsPresent
argument_list|,
name|existingAfterGC
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noGc
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStoreState
name|state
init|=
name|setUp
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|existingAfterGC
init|=
name|gc
argument_list|(
literal|86400
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Sets
operator|.
name|symmetricDifference
argument_list|(
name|state
operator|.
name|blobsAdded
argument_list|,
name|existingAfterGC
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|gcVersionDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStoreState
name|state
init|=
name|setUp
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|existingAfterGC
init|=
name|gc
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Sets
operator|.
name|symmetricDifference
argument_list|(
name|state
operator|.
name|blobsPresent
argument_list|,
name|existingAfterGC
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|gcDirectMongoDeleteWithInlined
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStoreState
name|state
init|=
name|setUp
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|addInlined
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|existingAfterGC
init|=
name|gc
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Sets
operator|.
name|symmetricDifference
argument_list|(
name|state
operator|.
name|blobsPresent
argument_list|,
name|existingAfterGC
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|gcVersionDeleteWithInlined
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStoreState
name|state
init|=
name|setUp
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|addInlined
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|existingAfterGC
init|=
name|gc
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Sets
operator|.
name|symmetricDifference
argument_list|(
name|state
operator|.
name|blobsPresent
argument_list|,
name|existingAfterGC
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|consistencyCheckInlined
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStoreState
name|state
init|=
name|setUp
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|addInlined
argument_list|()
expr_stmt|;
name|ThreadPoolExecutor
name|executor
init|=
operator|(
name|ThreadPoolExecutor
operator|)
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|MarkSweepGarbageCollector
name|gcObj
init|=
name|init
argument_list|(
literal|86400
argument_list|,
name|executor
argument_list|)
decl_stmt|;
name|long
name|candidates
init|=
name|gcObj
operator|.
name|checkConsistency
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|executor
operator|.
name|getTaskCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|candidates
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|consistencyCheckInit
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStoreState
name|state
init|=
name|setUp
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|ThreadPoolExecutor
name|executor
init|=
operator|(
name|ThreadPoolExecutor
operator|)
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|MarkSweepGarbageCollector
name|gcObj
init|=
name|init
argument_list|(
literal|86400
argument_list|,
name|executor
argument_list|)
decl_stmt|;
name|long
name|candidates
init|=
name|gcObj
operator|.
name|checkConsistency
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|executor
operator|.
name|getTaskCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|candidates
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|consistencyCheckWithGc
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStoreState
name|state
init|=
name|setUp
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|existingAfterGC
init|=
name|gc
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Sets
operator|.
name|symmetricDifference
argument_list|(
name|state
operator|.
name|blobsPresent
argument_list|,
name|existingAfterGC
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|ThreadPoolExecutor
name|executor
init|=
operator|(
name|ThreadPoolExecutor
operator|)
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|MarkSweepGarbageCollector
name|gcObj
init|=
name|init
argument_list|(
literal|86400
argument_list|,
name|executor
argument_list|)
decl_stmt|;
name|long
name|candidates
init|=
name|gcObj
operator|.
name|checkConsistency
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|executor
operator|.
name|getTaskCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|candidates
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|consistencyCheckWithRenegadeDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStoreState
name|state
init|=
name|setUp
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|// Simulate faulty state by deleting some blobs directly
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
literal|87
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|existing
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|state
operator|.
name|blobsPresent
argument_list|)
decl_stmt|;
name|GarbageCollectableBlobStore
name|store
init|=
operator|(
name|GarbageCollectableBlobStore
operator|)
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getBlobStore
argument_list|()
decl_stmt|;
name|long
name|count
init|=
name|store
operator|.
name|countDeleteChunks
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|existing
operator|.
name|get
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
name|existing
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ThreadPoolExecutor
name|executor
init|=
operator|(
name|ThreadPoolExecutor
operator|)
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|MarkSweepGarbageCollector
name|gcObj
init|=
name|init
argument_list|(
literal|86400
argument_list|,
name|executor
argument_list|)
decl_stmt|;
name|long
name|candidates
init|=
name|gcObj
operator|.
name|checkConsistency
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|executor
operator|.
name|getTaskCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|candidates
argument_list|)
expr_stmt|;
block|}
comment|// OAK-3390
annotation|@
name|Test
specifier|public
name|void
name|referencedBlobs
parameter_list|()
throws|throws
name|Exception
block|{
name|Iterator
argument_list|<
name|ReferencedBlob
argument_list|>
name|blobs
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getReferencedBlobsIterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|blobs
operator|instanceof
name|MongoBlobReferenceIterator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|gcLongRunningBlobCollection
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStoreState
name|state
init|=
name|setUp
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"{} Blobs added {}"
argument_list|,
name|state
operator|.
name|blobsAdded
operator|.
name|size
argument_list|()
argument_list|,
name|state
operator|.
name|blobsAdded
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"{} Blobs should be present {}"
argument_list|,
name|state
operator|.
name|blobsPresent
operator|.
name|size
argument_list|()
argument_list|,
name|state
operator|.
name|blobsPresent
argument_list|)
expr_stmt|;
name|ThreadPoolExecutor
name|executor
init|=
operator|(
name|ThreadPoolExecutor
operator|)
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|DocumentNodeStore
name|store
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|String
name|repoId
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|SharedDataStoreUtils
operator|.
name|isShared
argument_list|(
name|store
operator|.
name|getBlobStore
argument_list|()
argument_list|)
condition|)
block|{
name|repoId
operator|=
name|ClusterRepositoryInfo
operator|.
name|createId
argument_list|(
name|store
argument_list|)
expr_stmt|;
operator|(
operator|(
name|SharedDataStore
operator|)
name|store
operator|.
name|getBlobStore
argument_list|()
operator|)
operator|.
name|addMetadataRecord
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|REPOSITORY
operator|.
name|getNameFromId
argument_list|(
name|repoId
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|TestGarbageCollector
name|gc
init|=
operator|new
name|TestGarbageCollector
argument_list|(
operator|new
name|DocumentBlobReferenceRetriever
argument_list|(
name|store
argument_list|)
argument_list|,
operator|(
name|GarbageCollectableBlobStore
operator|)
name|store
operator|.
name|getBlobStore
argument_list|()
argument_list|,
name|executor
argument_list|,
literal|"./target"
argument_list|,
literal|5
argument_list|,
literal|5000
argument_list|,
name|repoId
argument_list|)
decl_stmt|;
name|gc
operator|.
name|collectGarbage
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|existingAfterGC
init|=
name|iterate
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"{} Blobs existing after gc {}"
argument_list|,
name|existingAfterGC
operator|.
name|size
argument_list|()
argument_list|,
name|existingAfterGC
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Sets
operator|.
name|difference
argument_list|(
name|state
operator|.
name|blobsPresent
argument_list|,
name|existingAfterGC
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|gc
operator|.
name|additionalBlobs
argument_list|,
name|Sets
operator|.
name|symmetricDifference
argument_list|(
name|state
operator|.
name|blobsPresent
argument_list|,
name|existingAfterGC
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|gc
parameter_list|(
name|int
name|blobGcMaxAgeInSecs
parameter_list|)
throws|throws
name|Exception
block|{
name|ThreadPoolExecutor
name|executor
init|=
operator|(
name|ThreadPoolExecutor
operator|)
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|MarkSweepGarbageCollector
name|gc
init|=
name|init
argument_list|(
name|blobGcMaxAgeInSecs
argument_list|,
name|executor
argument_list|)
decl_stmt|;
name|gc
operator|.
name|collectGarbage
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|executor
operator|.
name|getTaskCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|iterate
argument_list|()
return|;
block|}
specifier|private
name|MarkSweepGarbageCollector
name|init
parameter_list|(
name|int
name|blobGcMaxAgeInSecs
parameter_list|,
name|ThreadPoolExecutor
name|executor
parameter_list|)
throws|throws
name|Exception
block|{
name|DocumentNodeStore
name|store
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|String
name|repoId
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|SharedDataStoreUtils
operator|.
name|isShared
argument_list|(
name|store
operator|.
name|getBlobStore
argument_list|()
argument_list|)
condition|)
block|{
name|repoId
operator|=
name|ClusterRepositoryInfo
operator|.
name|createId
argument_list|(
name|store
argument_list|)
expr_stmt|;
operator|(
operator|(
name|SharedDataStore
operator|)
name|store
operator|.
name|getBlobStore
argument_list|()
operator|)
operator|.
name|addMetadataRecord
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|REPOSITORY
operator|.
name|getNameFromId
argument_list|(
name|repoId
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|MarkSweepGarbageCollector
name|gc
init|=
operator|new
name|MarkSweepGarbageCollector
argument_list|(
operator|new
name|DocumentBlobReferenceRetriever
argument_list|(
name|store
argument_list|)
argument_list|,
operator|(
name|GarbageCollectableBlobStore
operator|)
name|store
operator|.
name|getBlobStore
argument_list|()
argument_list|,
name|executor
argument_list|,
literal|"./target"
argument_list|,
literal|5
argument_list|,
name|blobGcMaxAgeInSecs
argument_list|,
name|repoId
argument_list|)
decl_stmt|;
return|return
name|gc
return|;
block|}
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|iterate
parameter_list|()
throws|throws
name|Exception
block|{
name|GarbageCollectableBlobStore
name|store
init|=
operator|(
name|GarbageCollectableBlobStore
operator|)
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getBlobStore
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|cur
init|=
name|store
operator|.
name|getAllChunkIds
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|existing
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
while|while
condition|(
name|cur
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|existing
operator|.
name|add
argument_list|(
name|cur
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|existing
return|;
block|}
specifier|static
name|InputStream
name|randomStream
parameter_list|(
name|int
name|seed
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|r
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Clock
name|getTestClock
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|clock
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
name|clock
operator|.
name|waitUntil
argument_list|(
name|Revision
operator|.
name|getCurrentTimestamp
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|clock
return|;
block|}
comment|/**      * Waits for some time and adds additional blobs after blob referenced identified to simulate      * long running blob id collection phase.      */
class|class
name|TestGarbageCollector
extends|extends
name|MarkSweepGarbageCollector
block|{
name|long
name|maxLastModifiedInterval
decl_stmt|;
name|String
name|root
decl_stmt|;
name|GarbageCollectableBlobStore
name|blobStore
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|additionalBlobs
decl_stmt|;
specifier|public
name|TestGarbageCollector
parameter_list|(
name|BlobReferenceRetriever
name|marker
parameter_list|,
name|GarbageCollectableBlobStore
name|blobStore
parameter_list|,
name|Executor
name|executor
parameter_list|,
name|String
name|root
parameter_list|,
name|int
name|batchCount
parameter_list|,
name|long
name|maxLastModifiedInterval
parameter_list|,
annotation|@
name|Nullable
name|String
name|repositoryId
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|marker
argument_list|,
name|blobStore
argument_list|,
name|executor
argument_list|,
name|root
argument_list|,
name|batchCount
argument_list|,
name|maxLastModifiedInterval
argument_list|,
name|repositoryId
argument_list|)
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|blobStore
operator|=
name|blobStore
expr_stmt|;
name|this
operator|.
name|maxLastModifiedInterval
operator|=
name|maxLastModifiedInterval
expr_stmt|;
name|this
operator|.
name|additionalBlobs
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|markAndSweep
parameter_list|(
name|boolean
name|markOnly
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|threw
init|=
literal|true
decl_stmt|;
name|GarbageCollectorFileState
name|fs
init|=
operator|new
name|GarbageCollectorFileState
argument_list|(
name|root
argument_list|)
decl_stmt|;
try|try
block|{
name|Stopwatch
name|sw
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting Test Blob garbage collection"
argument_list|)
expr_stmt|;
comment|// Sleep a little more than the max interval to get over the interval for valid blobs
name|Thread
operator|.
name|sleep
argument_list|(
name|maxLastModifiedInterval
operator|+
literal|1000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Slept {} to make blobs old"
argument_list|,
name|maxLastModifiedInterval
operator|+
literal|1000
argument_list|)
expr_stmt|;
name|long
name|markStart
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|mark
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Mark finished"
argument_list|)
expr_stmt|;
name|additionalBlobs
operator|=
name|createAdditional
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|markOnly
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|maxLastModifiedInterval
operator|+
literal|100
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Slept {} to make additional blobs old"
argument_list|,
name|maxLastModifiedInterval
operator|+
literal|100
argument_list|)
expr_stmt|;
name|long
name|deleteCount
init|=
name|sweep
argument_list|(
name|fs
argument_list|,
name|markStart
argument_list|)
decl_stmt|;
name|threw
operator|=
literal|false
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Blob garbage collection completed in {}. Number of blobs deleted [{}]"
argument_list|,
name|sw
operator|.
name|toString
argument_list|()
argument_list|,
name|deleteCount
argument_list|,
name|maxLastModifiedInterval
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|Closeables
operator|.
name|close
argument_list|(
name|fs
argument_list|,
name|threw
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|HashSet
argument_list|<
name|String
argument_list|>
name|createAdditional
parameter_list|()
throws|throws
name|Exception
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|blobSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|DocumentNodeStore
name|s
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|a
init|=
name|s
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|int
name|number
init|=
literal|5
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
name|number
condition|;
name|i
operator|++
control|)
block|{
name|Blob
name|b
init|=
name|s
operator|.
name|createBlob
argument_list|(
name|randomStream
argument_list|(
literal|100
operator|+
name|i
argument_list|,
literal|16516
argument_list|)
argument_list|)
decl_stmt|;
name|a
operator|.
name|child
argument_list|(
literal|"cafter"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|idIter
init|=
operator|(
operator|(
name|GarbageCollectableBlobStore
operator|)
name|s
operator|.
name|getBlobStore
argument_list|()
operator|)
operator|.
name|resolveChunks
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|idIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|chunk
init|=
name|idIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|blobSet
operator|.
name|add
argument_list|(
name|chunk
argument_list|)
expr_stmt|;
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"{} Additional created {}"
argument_list|,
name|blobSet
operator|.
name|size
argument_list|()
argument_list|,
name|blobSet
argument_list|)
expr_stmt|;
name|s
operator|.
name|merge
argument_list|(
name|a
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
return|return
name|blobSet
return|;
block|}
block|}
block|}
end_class

end_unit

