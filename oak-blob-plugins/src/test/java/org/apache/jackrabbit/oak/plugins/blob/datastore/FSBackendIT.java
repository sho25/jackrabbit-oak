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
name|Properties
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
name|Executors
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
name|Maps
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
name|Futures
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
name|ListenableFuture
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
name|ListeningExecutorService
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
name|DataIdentifier
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
name|core
operator|.
name|data
operator|.
name|util
operator|.
name|NamedThreadFactory
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
name|PropertiesUtil
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
name|plugins
operator|.
name|blob
operator|.
name|datastore
operator|.
name|DataStoreUtils
operator|.
name|randomStream
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
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|FSBackendIT
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FSBackendIT
operator|.
name|class
argument_list|)
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
name|Properties
name|props
decl_stmt|;
specifier|private
name|FSBackend
name|backend
decl_stmt|;
specifier|private
name|String
name|dataStoreDir
decl_stmt|;
specifier|private
name|DataStore
name|ds
decl_stmt|;
specifier|private
name|ListeningExecutorService
name|executor
decl_stmt|;
specifier|private
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
literal|0
argument_list|)
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
name|dataStoreDir
operator|=
name|folder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|props
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"cacheSize"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"fsBackendPath"
argument_list|,
name|dataStoreDir
argument_list|)
expr_stmt|;
name|ds
operator|=
name|createDataStore
argument_list|()
expr_stmt|;
name|backend
operator|=
call|(
name|FSBackend
call|)
argument_list|(
operator|(
name|CachingFileDataStore
operator|)
name|ds
argument_list|)
operator|.
name|getBackend
argument_list|()
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|MoreExecutors
operator|.
name|listeningDecorator
argument_list|(
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|25
argument_list|,
operator|new
name|NamedThreadFactory
argument_list|(
literal|"oak-backend-test-write-thread"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|DataStore
name|createDataStore
parameter_list|()
block|{
name|CachingFileDataStore
name|ds
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ds
operator|=
operator|new
name|CachingFileDataStore
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
init|=
name|DataStoreUtils
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|props
operator|.
name|putAll
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|PropertiesUtil
operator|.
name|populate
argument_list|(
name|ds
argument_list|,
name|Maps
operator|.
name|fromProperties
argument_list|(
name|props
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ds
operator|.
name|setProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|ds
operator|.
name|init
argument_list|(
name|dataStoreDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception creating DataStore"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|ds
return|;
block|}
comment|/**      * Test for write single threaded to FSBackend.      */
annotation|@
name|Test
specifier|public
name|void
name|testSingleThreadFSBackend
parameter_list|()
block|{
try|try
block|{
name|long
name|start
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
literal|"Testcase: "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"#testSingleThread, testDir="
operator|+
name|dataStoreDir
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|ds
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Testcase: "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"#testSingleThread finished, time taken = ["
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|+
literal|"]ms"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Tests for multi-threaded write of same file to FSBackend      */
annotation|@
name|Test
specifier|public
name|void
name|testMultiThreadedSame
parameter_list|()
block|{
try|try
block|{
name|long
name|start
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
literal|"Testcase: "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"#testMultiThreadedSame, testDir="
operator|+
name|dataStoreDir
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|ds
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Testcase: "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"#testMultiThreadedSame finished, time taken = ["
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|+
literal|"]ms"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Tests for multi-threaded write of same file to FSBackend      */
annotation|@
name|Test
specifier|public
name|void
name|testMultiThreadedSameLarge
parameter_list|()
block|{
try|try
block|{
name|long
name|start
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
literal|"Testcase: "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"#testMultiThreadedSameLarge, testDir="
operator|+
name|dataStoreDir
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|ds
argument_list|,
literal|100
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Testcase: "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"#testMultiThreadedSameLarge finished, time taken = ["
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|+
literal|"]ms"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Tests for multi-threaded write of different file to FSBackend      */
annotation|@
name|Test
specifier|public
name|void
name|testMultiThreadedDifferent
parameter_list|()
block|{
try|try
block|{
name|long
name|start
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
literal|"Testcase: "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"#testMultiThreadedDifferent, testDir="
operator|+
name|dataStoreDir
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|ds
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Testcase: "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"#testMultiThreadedDifferent finished, time taken = ["
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|+
literal|"]ms"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Tests for multi-threaded write of different file to FSBackend      */
annotation|@
name|Test
specifier|public
name|void
name|testMultiThreadedDifferentLarge
parameter_list|()
block|{
try|try
block|{
name|long
name|start
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
literal|"Testcase: "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"#testMultiThreadedDifferentLarge, testDir="
operator|+
name|dataStoreDir
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|ds
argument_list|,
literal|100
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Testcase: "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"#testMultiThreadedDifferentLarge finished, time taken = ["
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|+
literal|"]ms"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
try|try
block|{
operator|new
name|ExecutorCloser
argument_list|(
name|executor
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|ds
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DataStoreException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Method to assert record while writing and deleting record from FSBackend      */
name|void
name|doTest
parameter_list|(
name|DataStore
name|ds
parameter_list|,
name|int
name|concurrency
parameter_list|,
name|boolean
name|same
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|ListenableFuture
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|futures
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|concurrency
argument_list|)
decl_stmt|;
name|int
name|seed
init|=
literal|0
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
name|concurrency
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|same
condition|)
block|{
name|seed
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|put
argument_list|(
name|folder
argument_list|,
name|futures
argument_list|,
name|seed
argument_list|,
name|latch
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
name|concurrency
condition|;
name|i
operator|++
control|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
name|assertFuture
argument_list|(
name|futures
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|ListenableFuture
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|put
parameter_list|(
name|TemporaryFolder
name|folder
parameter_list|,
name|List
argument_list|<
name|ListenableFuture
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|futures
parameter_list|,
name|int
name|seed
parameter_list|,
name|CountDownLatch
name|writeLatch
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|f
init|=
name|copyToFile
argument_list|(
name|randomStream
argument_list|(
name|seed
argument_list|,
literal|4
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
argument_list|,
name|folder
operator|.
name|newFile
argument_list|()
argument_list|)
decl_stmt|;
name|ListenableFuture
argument_list|<
name|Integer
argument_list|>
name|future
init|=
name|executor
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|writeLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|backend
operator|.
name|write
argument_list|(
operator|new
name|DataIdentifier
argument_list|(
literal|"0000ID"
operator|+
name|seed
argument_list|)
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Added file to backend"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error adding file to backend"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|seed
return|;
block|}
argument_list|)
decl_stmt|;
name|futures
operator|.
name|add
argument_list|(
name|future
argument_list|)
expr_stmt|;
return|return
name|futures
return|;
block|}
specifier|private
name|void
name|waitFinish
parameter_list|(
name|List
argument_list|<
name|ListenableFuture
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|futures
parameter_list|)
block|{
name|ListenableFuture
argument_list|<
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|listenableFutures
init|=
name|Futures
operator|.
name|successfulAsList
argument_list|(
name|futures
argument_list|)
decl_stmt|;
try|try
block|{
name|listenableFutures
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in finishing threads"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertFuture
parameter_list|(
name|List
argument_list|<
name|ListenableFuture
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|futures
parameter_list|)
throws|throws
name|Exception
block|{
name|waitFinish
argument_list|(
name|futures
argument_list|)
expr_stmt|;
for|for
control|(
name|ListenableFuture
name|future
range|:
name|futures
control|)
block|{
name|assertFile
argument_list|(
operator|(
name|Integer
operator|)
name|future
operator|.
name|get
argument_list|()
argument_list|,
name|folder
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertFile
parameter_list|(
name|int
name|seed
parameter_list|,
name|TemporaryFolder
name|folder
parameter_list|)
throws|throws
name|IOException
throws|,
name|DataStoreException
block|{
name|DataRecord
name|backendRecord
init|=
name|backend
operator|.
name|getRecord
argument_list|(
operator|new
name|DataIdentifier
argument_list|(
literal|"0000ID"
operator|+
name|seed
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|backendRecord
operator|.
name|getLength
argument_list|()
argument_list|,
literal|4
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|File
name|original
init|=
name|copyToFile
argument_list|(
name|randomStream
argument_list|(
name|seed
argument_list|,
literal|4
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
argument_list|,
name|folder
operator|.
name|newFile
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Backend file content differs"
argument_list|,
name|FileUtils
operator|.
name|contentEquals
argument_list|(
name|original
argument_list|,
name|copyToFile
argument_list|(
name|backendRecord
operator|.
name|getStream
argument_list|()
argument_list|,
name|folder
operator|.
name|newFile
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|static
name|File
name|copyToFile
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|FileIOUtils
operator|.
name|copyInputStreamToFile
argument_list|(
name|stream
argument_list|,
name|file
argument_list|)
expr_stmt|;
return|return
name|file
return|;
block|}
block|}
end_class

end_unit
