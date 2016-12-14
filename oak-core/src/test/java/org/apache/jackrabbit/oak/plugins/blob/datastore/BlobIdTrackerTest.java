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
name|blob
operator|.
name|datastore
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|FileInputStream
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
name|ScheduledFuture
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
name|io
operator|.
name|Closer
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
name|DataRecord
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
name|DataStoreException
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
name|FileIOUtils
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
name|concurrent
operator|.
name|ExecutorCloser
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
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|valueOf
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|UUID
operator|.
name|randomUUID
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|closeQuietly
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
name|commons
operator|.
name|FileIOUtils
operator|.
name|readStringsAsSet
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
name|commons
operator|.
name|FileIOUtils
operator|.
name|writeStrings
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
name|plugins
operator|.
name|blob
operator|.
name|datastore
operator|.
name|DataStoreUtils
operator|.
name|getBlobStore
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
name|BLOBREFERENCES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|instanceOf
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeNoException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeThat
import|;
end_import

begin_comment
comment|/**  * Test for BlobIdTracker to test addition, retrieval and removal of blob ids.  */
end_comment

begin_class
specifier|public
class|class
name|BlobIdTrackerTest
block|{
name|File
name|root
decl_stmt|;
name|SharedDataStore
name|dataStore
decl_stmt|;
name|BlobIdTracker
name|tracker
decl_stmt|;
name|Closer
name|closer
init|=
name|Closer
operator|.
name|create
argument_list|()
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|String
name|repoId
decl_stmt|;
specifier|private
name|ScheduledExecutorService
name|scheduler
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUpBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|assumeThat
argument_list|(
name|getBlobStore
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|SharedDataStore
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assumeNoException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|root
operator|=
name|folder
operator|.
name|newFolder
argument_list|()
expr_stmt|;
if|if
condition|(
name|dataStore
operator|==
literal|null
condition|)
block|{
name|dataStore
operator|=
name|getBlobStore
argument_list|(
name|folder
operator|.
name|newFolder
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|repoId
operator|=
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|tracker
operator|=
operator|new
name|BlobIdTracker
argument_list|(
name|root
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|repoId
argument_list|,
literal|100
operator|*
literal|60
argument_list|,
name|dataStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|newSingleThreadScheduledExecutor
argument_list|()
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|tracker
argument_list|)
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
operator|new
name|ExecutorCloser
argument_list|(
name|scheduler
argument_list|)
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
name|IOException
block|{
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|initAdd
init|=
name|add
argument_list|(
name|tracker
argument_list|,
name|range
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
decl_stmt|;
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|scheduledFuture
init|=
name|scheduler
operator|.
name|schedule
argument_list|(
name|tracker
operator|.
expr|new
name|SnapshotJob
argument_list|()
argument_list|,
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|scheduledFuture
operator|.
name|get
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|retrieved
init|=
name|retrieve
argument_list|(
name|tracker
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Extra elements after add"
argument_list|,
name|initAdd
argument_list|,
name|retrieved
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|read
argument_list|(
name|dataStore
operator|.
name|getAllMetadataRecords
argument_list|(
name|BLOBREFERENCES
operator|.
name|getType
argument_list|()
argument_list|)
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
name|addSnapshotRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|initAdd
init|=
name|add
argument_list|(
name|tracker
argument_list|,
name|range
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
decl_stmt|;
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|scheduledFuture
init|=
name|scheduler
operator|.
name|schedule
argument_list|(
name|tracker
operator|.
expr|new
name|SnapshotJob
argument_list|()
argument_list|,
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|scheduledFuture
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Extra elements after add"
argument_list|,
name|initAdd
argument_list|,
name|retrieve
argument_list|(
name|tracker
argument_list|)
argument_list|)
expr_stmt|;
name|remove
argument_list|(
name|tracker
argument_list|,
name|folder
operator|.
name|newFile
argument_list|()
argument_list|,
name|initAdd
argument_list|,
name|range
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Extra elements after remove"
argument_list|,
name|initAdd
argument_list|,
name|retrieve
argument_list|(
name|tracker
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|snapshotRetrieveIgnored
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"oak.datastore.skipTracker"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// Close and open a new object to use the system property
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|tracker
operator|=
operator|new
name|BlobIdTracker
argument_list|(
name|root
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|repoId
argument_list|,
literal|100
operator|*
literal|60
argument_list|,
name|dataStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|newSingleThreadScheduledExecutor
argument_list|()
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|tracker
argument_list|)
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
operator|new
name|ExecutorCloser
argument_list|(
name|scheduler
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|initAdd
init|=
name|add
argument_list|(
name|tracker
argument_list|,
name|range
argument_list|(
literal|0
argument_list|,
literal|10000
argument_list|)
argument_list|)
decl_stmt|;
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|scheduledFuture
init|=
name|scheduler
operator|.
name|schedule
argument_list|(
name|tracker
operator|.
expr|new
name|SnapshotJob
argument_list|()
argument_list|,
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|scheduledFuture
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"References file not empty"
argument_list|,
literal|0
argument_list|,
name|tracker
operator|.
name|store
operator|.
name|getBlobRecordsFile
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|retrieved
init|=
name|retrieveFile
argument_list|(
name|tracker
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|retrieved
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|retrieved
operator|=
name|retrieve
argument_list|(
name|tracker
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|retrieved
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|//reset the skip tracker system prop
name|System
operator|.
name|clearProperty
argument_list|(
literal|"oak.datastore.skipTracker"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|externalAddOffline
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Close and open a new object to use the system property
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
name|root
operator|=
name|folder
operator|.
name|newFolder
argument_list|()
expr_stmt|;
name|File
name|blobIdRoot
init|=
operator|new
name|File
argument_list|(
name|root
argument_list|,
literal|"blobids"
argument_list|)
decl_stmt|;
name|blobIdRoot
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
comment|//Add file offline
name|File
name|offline
init|=
operator|new
name|File
argument_list|(
name|blobIdRoot
argument_list|,
literal|"blob-offline123456.gen"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|offlineLoad
init|=
name|range
argument_list|(
literal|0
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|FileIOUtils
operator|.
name|writeStrings
argument_list|(
name|offlineLoad
operator|.
name|iterator
argument_list|()
argument_list|,
name|offline
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|tracker
operator|=
operator|new
name|BlobIdTracker
argument_list|(
name|root
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|repoId
argument_list|,
literal|100
operator|*
literal|60
argument_list|,
name|dataStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|newSingleThreadScheduledExecutor
argument_list|()
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|tracker
argument_list|)
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
operator|new
name|ExecutorCloser
argument_list|(
name|scheduler
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|initAdd
init|=
name|add
argument_list|(
name|tracker
argument_list|,
name|range
argument_list|(
literal|1001
argument_list|,
literal|1005
argument_list|)
argument_list|)
decl_stmt|;
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|scheduledFuture
init|=
name|scheduler
operator|.
name|schedule
argument_list|(
name|tracker
operator|.
expr|new
name|SnapshotJob
argument_list|()
argument_list|,
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|scheduledFuture
operator|.
name|get
argument_list|()
expr_stmt|;
name|initAdd
operator|.
name|addAll
argument_list|(
name|offlineLoad
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|initAdd
operator|.
name|size
argument_list|()
argument_list|,
name|Iterators
operator|.
name|size
argument_list|(
name|FileUtils
operator|.
name|lineIterator
argument_list|(
name|tracker
operator|.
name|store
operator|.
name|getBlobRecordsFile
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|retrieved
init|=
name|retrieve
argument_list|(
name|tracker
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Extra elements after add"
argument_list|,
name|initAdd
argument_list|,
name|retrieved
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|read
argument_list|(
name|dataStore
operator|.
name|getAllMetadataRecords
argument_list|(
name|BLOBREFERENCES
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|read
parameter_list|(
name|List
argument_list|<
name|DataRecord
argument_list|>
name|recs
parameter_list|)
throws|throws
name|IOException
throws|,
name|DataStoreException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|DataRecord
name|b
range|:
name|recs
control|)
block|{
name|ids
operator|.
name|addAll
argument_list|(
name|readStringsAsSet
argument_list|(
name|b
operator|.
name|getStream
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ids
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|add
parameter_list|(
name|BlobTracker
name|tracker
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|ints
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|s
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|rec
range|:
name|ints
control|)
block|{
name|tracker
operator|.
name|add
argument_list|(
name|rec
argument_list|)
expr_stmt|;
name|s
operator|.
name|add
argument_list|(
name|rec
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|retrieve
parameter_list|(
name|BlobTracker
name|tracker
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|retrieved
init|=
name|newHashSet
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|tracker
operator|.
name|get
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|retrieved
operator|.
name|add
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|iter
operator|instanceof
name|Closeable
condition|)
block|{
name|closeQuietly
argument_list|(
operator|(
name|Closeable
operator|)
name|iter
argument_list|)
expr_stmt|;
block|}
return|return
name|retrieved
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|retrieveFile
parameter_list|(
name|BlobIdTracker
name|tracker
parameter_list|,
name|TemporaryFolder
name|folder
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|f
init|=
name|folder
operator|.
name|newFile
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|retrieved
init|=
name|readStringsAsSet
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|tracker
operator|.
name|get
argument_list|(
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|retrieved
return|;
block|}
specifier|private
specifier|static
name|void
name|remove
parameter_list|(
name|BlobTracker
name|tracker
parameter_list|,
name|File
name|temp
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|initAdd
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|ints
parameter_list|)
throws|throws
name|IOException
block|{
name|writeStrings
argument_list|(
name|ints
operator|.
name|iterator
argument_list|()
argument_list|,
name|temp
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|initAdd
operator|.
name|removeAll
argument_list|(
name|ints
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|remove
argument_list|(
name|temp
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|range
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|min
init|;
name|i
operator|<=
name|max
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
block|}
end_class

end_unit

