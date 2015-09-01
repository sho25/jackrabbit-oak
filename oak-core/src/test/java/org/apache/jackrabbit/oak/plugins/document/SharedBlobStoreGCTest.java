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
name|document
package|;
end_package

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
name|File
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
name|Date
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
name|TimeUnit
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
name|util
operator|.
name|concurrent
operator|.
name|MoreExecutors
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|core
operator|.
name|data
operator|.
name|DataStore
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
name|BlobGarbageCollector
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
name|GarbageCollectionRepoStats
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
name|DataStoreBlobStore
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
operator|.
name|SharedStoreRecordType
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
name|blob
operator|.
name|datastore
operator|.
name|DataStoreUtils
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
name|BlobStore
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
name|After
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
comment|/**  * Test for gc in a shared data store among hetrogeneous oak node stores.  */
end_comment

begin_class
specifier|public
class|class
name|SharedBlobStoreGCTest
block|{
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
name|SharedBlobStoreGCTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Cluster
name|cluster1
decl_stmt|;
specifier|private
name|Cluster
name|cluster2
decl_stmt|;
specifier|private
name|Clock
name|clock
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"In setUp()"
argument_list|)
expr_stmt|;
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
name|DataStoreUtils
operator|.
name|time
operator|=
name|clock
operator|.
name|getTime
argument_list|()
expr_stmt|;
name|BlobStore
name|blobeStore1
init|=
name|DataStoreUtils
operator|.
name|getBlobStore
argument_list|()
decl_stmt|;
name|DocumentNodeStore
name|ds1
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
operator|new
name|MemoryDocumentStore
argument_list|()
argument_list|)
operator|.
name|setBlobStore
argument_list|(
name|blobeStore1
argument_list|)
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|String
name|repoId1
init|=
name|ClusterRepositoryInfo
operator|.
name|createId
argument_list|(
name|ds1
argument_list|)
decl_stmt|;
comment|// Register the unique repository id in the data store
operator|(
operator|(
name|SharedDataStore
operator|)
name|blobeStore1
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
name|SharedStoreRecordType
operator|.
name|REPOSITORY
operator|.
name|getNameFromId
argument_list|(
name|repoId1
argument_list|)
argument_list|)
expr_stmt|;
name|BlobStore
name|blobeStore2
init|=
name|DataStoreUtils
operator|.
name|getBlobStore
argument_list|()
decl_stmt|;
name|DocumentNodeStore
name|ds2
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
operator|new
name|MemoryDocumentStore
argument_list|()
argument_list|)
operator|.
name|setBlobStore
argument_list|(
name|blobeStore2
argument_list|)
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|String
name|repoId2
init|=
name|ClusterRepositoryInfo
operator|.
name|createId
argument_list|(
name|ds2
argument_list|)
decl_stmt|;
comment|// Register the unique repository id in the data store
operator|(
operator|(
name|SharedDataStore
operator|)
name|blobeStore2
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
name|SharedStoreRecordType
operator|.
name|REPOSITORY
operator|.
name|getNameFromId
argument_list|(
name|repoId2
argument_list|)
argument_list|)
expr_stmt|;
name|cluster1
operator|=
operator|new
name|Cluster
argument_list|(
name|ds1
argument_list|,
name|repoId1
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|cluster1
operator|.
name|init
argument_list|()
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Initialized {}"
argument_list|,
name|cluster1
argument_list|)
expr_stmt|;
name|cluster2
operator|=
operator|new
name|Cluster
argument_list|(
name|ds2
argument_list|,
name|repoId2
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|cluster2
operator|.
name|init
argument_list|()
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Initialized {}"
argument_list|,
name|cluster2
argument_list|)
expr_stmt|;
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
name|Test
specifier|public
name|void
name|testGC
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Running testGC()"
argument_list|)
expr_stmt|;
comment|// Only run the mark phase on both the clusters
name|cluster1
operator|.
name|gc
operator|.
name|collectGarbage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cluster2
operator|.
name|gc
operator|.
name|collectGarbage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Execute the gc with sweep
name|cluster1
operator|.
name|gc
operator|.
name|collectGarbage
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|Sets
operator|.
name|symmetricDifference
argument_list|(
name|Sets
operator|.
name|union
argument_list|(
name|cluster1
operator|.
name|getInitBlobs
argument_list|()
argument_list|,
name|cluster2
operator|.
name|getInitBlobs
argument_list|()
argument_list|)
argument_list|,
name|cluster1
operator|.
name|getExistingBlobIds
argument_list|()
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
name|testGCStats
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Running testGCStats()"
argument_list|)
expr_stmt|;
comment|// Only run the mark phase on both the clusters to get the stats
name|cluster1
operator|.
name|gc
operator|.
name|collectGarbage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cluster2
operator|.
name|gc
operator|.
name|collectGarbage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|actualRepoIds
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|actualRepoIds
operator|.
name|add
argument_list|(
name|cluster1
operator|.
name|repoId
argument_list|)
expr_stmt|;
name|actualRepoIds
operator|.
name|add
argument_list|(
name|cluster2
operator|.
name|repoId
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Integer
argument_list|>
name|actualNumBlobs
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|actualNumBlobs
operator|.
name|add
argument_list|(
name|cluster1
operator|.
name|initBlobs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|actualNumBlobs
operator|.
name|add
argument_list|(
name|cluster2
operator|.
name|initBlobs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|GarbageCollectionRepoStats
argument_list|>
name|statsList
init|=
name|cluster1
operator|.
name|gc
operator|.
name|getStats
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Integer
argument_list|>
name|observedNumBlobs
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
name|observedRepoIds
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|GarbageCollectionRepoStats
name|stat
range|:
name|statsList
control|)
block|{
name|observedNumBlobs
operator|.
name|add
argument_list|(
name|stat
operator|.
name|getNumLines
argument_list|()
argument_list|)
expr_stmt|;
name|observedRepoIds
operator|.
name|add
argument_list|(
name|stat
operator|.
name|getRepositoryId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Sets
operator|.
name|difference
argument_list|(
name|actualNumBlobs
argument_list|,
name|observedNumBlobs
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Sets
operator|.
name|difference
argument_list|(
name|actualRepoIds
argument_list|,
name|observedRepoIds
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|// GC should fail
specifier|public
name|void
name|testOnly1ClusterMark
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Running testOnly1ClusterMark()"
argument_list|)
expr_stmt|;
comment|// Only run the mark phase on one cluster
name|cluster1
operator|.
name|gc
operator|.
name|collectGarbage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Execute the gc with sweep
name|cluster1
operator|.
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
name|existing
init|=
name|cluster1
operator|.
name|getExistingBlobIds
argument_list|()
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Existing blobs {}"
argument_list|,
name|existing
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|(
name|cluster1
operator|.
name|getInitBlobs
argument_list|()
operator|.
name|size
argument_list|()
operator|+
name|cluster2
operator|.
name|getInitBlobs
argument_list|()
operator|.
name|size
argument_list|()
operator|)
operator|<=
name|existing
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|existing
operator|.
name|containsAll
argument_list|(
name|cluster2
operator|.
name|getInitBlobs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|existing
operator|.
name|containsAll
argument_list|(
name|cluster1
operator|.
name|getInitBlobs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRepeatedMarkWithSweep
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Running testRepeatedMarkWithSweep()"
argument_list|)
expr_stmt|;
comment|// Only run the mark phase on one cluster
name|cluster1
operator|.
name|gc
operator|.
name|collectGarbage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cluster2
operator|.
name|gc
operator|.
name|collectGarbage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cluster2
operator|.
name|gc
operator|.
name|collectGarbage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Execute the gc with sweep
name|cluster2
operator|.
name|gc
operator|.
name|collectGarbage
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Sets
operator|.
name|symmetricDifference
argument_list|(
name|Sets
operator|.
name|union
argument_list|(
name|cluster1
operator|.
name|getInitBlobs
argument_list|()
argument_list|,
name|cluster2
operator|.
name|getInitBlobs
argument_list|()
argument_list|)
argument_list|,
name|cluster1
operator|.
name|getExistingBlobIds
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStoreUtils
operator|.
name|cleanup
argument_list|(
name|cluster1
operator|.
name|getDataStore
argument_list|()
argument_list|,
name|cluster1
operator|.
name|getDate
argument_list|()
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|cleanDirectory
argument_list|(
operator|(
operator|new
name|File
argument_list|(
name|DataStoreUtils
operator|.
name|getHomeDir
argument_list|()
argument_list|)
operator|)
operator|.
name|getParentFile
argument_list|()
argument_list|)
expr_stmt|;
name|DataStoreUtils
operator|.
name|time
operator|=
operator|-
literal|1
expr_stmt|;
block|}
class|class
name|Cluster
block|{
specifier|private
name|DocumentNodeStore
name|ds
decl_stmt|;
specifier|private
name|int
name|seed
decl_stmt|;
specifier|private
name|BlobGarbageCollector
name|gc
decl_stmt|;
specifier|private
name|Date
name|startDate
decl_stmt|;
specifier|private
name|String
name|repoId
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|initBlobs
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|getInitBlobs
parameter_list|()
block|{
return|return
name|initBlobs
return|;
block|}
specifier|public
name|Cluster
parameter_list|(
specifier|final
name|DocumentNodeStore
name|ds
parameter_list|,
specifier|final
name|String
name|repoId
parameter_list|,
name|int
name|seed
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|ds
operator|=
name|ds
expr_stmt|;
name|this
operator|.
name|gc
operator|=
operator|new
name|MarkSweepGarbageCollector
argument_list|(
operator|new
name|DocumentBlobReferenceRetriever
argument_list|(
name|ds
argument_list|)
argument_list|,
operator|(
name|GarbageCollectableBlobStore
operator|)
name|ds
operator|.
name|getBlobStore
argument_list|()
argument_list|,
name|MoreExecutors
operator|.
name|sameThreadExecutor
argument_list|()
argument_list|,
literal|"./target"
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
name|repoId
argument_list|)
expr_stmt|;
name|this
operator|.
name|startDate
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|this
operator|.
name|seed
operator|=
name|seed
expr_stmt|;
name|this
operator|.
name|repoId
operator|=
name|repoId
expr_stmt|;
block|}
comment|/**          * Creates the setup load with deletions.          *           * @throws Exception          */
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|a
init|=
name|ds
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
comment|// track the number of the assets to be deleted
name|List
argument_list|<
name|Integer
argument_list|>
name|deletes
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
literal|5
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
name|deletes
operator|.
name|contains
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|deletes
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
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
name|number
condition|;
name|i
operator|++
control|)
block|{
name|Blob
name|b
init|=
name|ds
operator|.
name|createBlob
argument_list|(
name|randomStream
argument_list|(
name|i
operator|+
name|seed
argument_list|,
literal|16516
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|deletes
operator|.
name|contains
argument_list|(
name|i
argument_list|)
condition|)
block|{
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
name|ds
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
name|initBlobs
operator|.
name|add
argument_list|(
name|idIter
operator|.
name|next
argument_list|()
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
block|}
name|ds
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
name|a
operator|=
name|ds
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
name|deletes
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
name|ds
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
name|ds
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
name|deletes
operator|.
name|size
argument_list|()
argument_list|,
name|stats
operator|.
name|deletedDocGCCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|DataStoreUtils
operator|.
name|isS3DataStore
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getExistingBlobIds
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
name|ds
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
specifier|public
name|DataStore
name|getDataStore
parameter_list|()
block|{
return|return
operator|(
operator|(
name|DataStoreBlobStore
operator|)
name|ds
operator|.
name|getBlobStore
argument_list|()
operator|)
operator|.
name|getDataStore
argument_list|()
return|;
block|}
specifier|public
name|Date
name|getDate
parameter_list|()
block|{
return|return
name|startDate
return|;
block|}
block|}
block|}
end_class

end_unit

