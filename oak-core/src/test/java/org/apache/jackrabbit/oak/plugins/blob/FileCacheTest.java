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
name|io
operator|.
name|Closer
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
name|Files
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
name|SettableFuture
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
name|StringUtils
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
name|junit
operator|.
name|rules
operator|.
name|TestName
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
name|assertNotNull
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
name|assertNull
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

begin_comment
comment|/**  * - Tests for {@link FileCache}  */
end_comment

begin_class
specifier|public
class|class
name|FileCacheTest
extends|extends
name|AbstractDataStoreCacheTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|ID_PREFIX
init|=
literal|"12345"
decl_stmt|;
specifier|private
name|FileCache
name|cache
decl_stmt|;
specifier|private
name|File
name|root
decl_stmt|;
specifier|private
name|TestCacheLoader
name|loader
decl_stmt|;
specifier|private
name|Closer
name|closer
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
annotation|@
name|Rule
specifier|public
name|TestName
name|testName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|root
operator|=
name|folder
operator|.
name|newFolder
argument_list|()
expr_stmt|;
name|closer
operator|=
name|Closer
operator|.
name|create
argument_list|()
expr_stmt|;
name|loader
operator|=
operator|new
name|TestCacheLoader
argument_list|<
name|String
argument_list|,
name|InputStream
argument_list|>
argument_list|(
name|root
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|testName
operator|.
name|getMethodName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"rebuild"
argument_list|)
condition|)
block|{
name|CountDownLatch
name|beforeLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|CountDownLatch
name|afterLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|CountDownLatch
name|afterExecuteLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|TestExecutor
name|executor
init|=
operator|new
name|TestExecutor
argument_list|(
literal|1
argument_list|,
name|beforeLatch
argument_list|,
name|afterLatch
argument_list|,
name|afterExecuteLatch
argument_list|)
decl_stmt|;
name|beforeLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|afterLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|cache
operator|=
name|FileCache
operator|.
name|build
argument_list|(
literal|4
operator|*
literal|1024
comment|/** KB */
argument_list|,
name|root
argument_list|,
name|loader
argument_list|,
name|executor
argument_list|)
expr_stmt|;
name|Futures
operator|.
name|successfulAsList
argument_list|(
operator|(
name|Iterable
argument_list|<
name|?
extends|extends
name|ListenableFuture
argument_list|<
name|?
argument_list|>
argument_list|>
operator|)
name|executor
operator|.
name|futures
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|cache
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
specifier|public
name|void
name|tear
parameter_list|()
block|{
name|closeQuietly
argument_list|(
name|closer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|zeroCache
parameter_list|()
throws|throws
name|Exception
block|{
name|cache
operator|=
name|FileCache
operator|.
name|build
argument_list|(
literal|0
comment|/** KB */
argument_list|,
name|root
argument_list|,
name|loader
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|cache
argument_list|)
expr_stmt|;
name|File
name|f
init|=
name|createFile
argument_list|(
literal|0
argument_list|,
name|loader
argument_list|,
name|cache
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|ID_PREFIX
operator|+
literal|0
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|getIfPresent
argument_list|(
name|ID_PREFIX
operator|+
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|get
argument_list|(
name|ID_PREFIX
operator|+
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cache
operator|.
name|getStats
argument_list|()
operator|.
name|getMaxTotalWeight
argument_list|()
argument_list|)
expr_stmt|;
name|cache
operator|.
name|invalidate
argument_list|(
name|ID_PREFIX
operator|+
literal|0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cache
operator|.
name|containsKey
argument_list|(
name|ID_PREFIX
operator|+
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Load and get from cache.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|add
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Started add"
argument_list|)
expr_stmt|;
name|File
name|f
init|=
name|createFile
argument_list|(
literal|0
argument_list|,
name|loader
argument_list|,
name|cache
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|assertCache
argument_list|(
literal|0
argument_list|,
name|cache
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|assertCacheStats
argument_list|(
name|cache
argument_list|,
literal|1
argument_list|,
literal|4
operator|*
literal|1024
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Memory weight different"
argument_list|,
name|getWeight
argument_list|(
name|ID_PREFIX
operator|+
literal|0
argument_list|,
name|cache
operator|.
name|getIfPresent
argument_list|(
name|ID_PREFIX
operator|+
literal|0
argument_list|)
argument_list|)
argument_list|,
name|cache
operator|.
name|getStats
argument_list|()
operator|.
name|estimateCurrentMemoryWeight
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished add"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Explicitly put in cache.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|put
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Started put"
argument_list|)
expr_stmt|;
comment|//File f = FileIOUtils.copy(randomStream(0, 4 * 1024));
name|cache
operator|.
name|put
argument_list|(
name|ID_PREFIX
operator|+
literal|0
argument_list|,
name|copyToFile
argument_list|(
name|randomStream
argument_list|(
literal|0
argument_list|,
literal|4
operator|*
literal|1024
argument_list|)
argument_list|,
name|folder
operator|.
name|newFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertCacheIfPresent
argument_list|(
literal|0
argument_list|,
name|cache
argument_list|,
name|copyToFile
argument_list|(
name|randomStream
argument_list|(
literal|0
argument_list|,
literal|4
operator|*
literal|1024
argument_list|)
argument_list|,
name|folder
operator|.
name|newFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertCacheStats
argument_list|(
name|cache
argument_list|,
literal|1
argument_list|,
literal|4
operator|*
literal|1024
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished put"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests {@link FileCache#getIfPresent(Object)} when no cache.      */
annotation|@
name|Test
specifier|public
name|void
name|getIfPresentObjectNoCache
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Started getIfPresentObjectNoCache"
argument_list|)
expr_stmt|;
name|File
name|file
init|=
name|cache
operator|.
name|getIfPresent
argument_list|(
call|(
name|Object
call|)
argument_list|(
name|ID_PREFIX
operator|+
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|assertCacheStats
argument_list|(
name|cache
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished getIfPresentObjectNoCache"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Retrieves same file concurrently.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|retrieveSameConcurrent
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Started retrieveSameConcurrent"
argument_list|)
expr_stmt|;
name|File
name|f
init|=
name|createFile
argument_list|(
literal|0
argument_list|,
name|loader
argument_list|,
name|cache
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|ListeningExecutorService
name|executorService
init|=
name|MoreExecutors
operator|.
name|listeningDecorator
argument_list|(
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
operator|new
name|ExecutorCloser
argument_list|(
name|executorService
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|CountDownLatch
name|thread1Start
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|SettableFuture
argument_list|<
name|File
argument_list|>
name|future1
init|=
name|retrieveThread
argument_list|(
name|executorService
argument_list|,
name|ID_PREFIX
operator|+
literal|0
argument_list|,
name|cache
argument_list|,
name|thread1Start
argument_list|)
decl_stmt|;
name|CountDownLatch
name|thread2Start
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|SettableFuture
argument_list|<
name|File
argument_list|>
name|future2
init|=
name|retrieveThread
argument_list|(
name|executorService
argument_list|,
name|ID_PREFIX
operator|+
literal|0
argument_list|,
name|cache
argument_list|,
name|thread2Start
argument_list|)
decl_stmt|;
name|thread1Start
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|thread2Start
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|future1
operator|.
name|get
argument_list|()
expr_stmt|;
name|future2
operator|.
name|get
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Async tasks finished"
argument_list|)
expr_stmt|;
name|assertCacheIfPresent
argument_list|(
literal|0
argument_list|,
name|cache
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|assertCacheStats
argument_list|(
name|cache
argument_list|,
literal|1
argument_list|,
literal|4
operator|*
literal|1024
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished retrieveSameConcurrent"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Retrieves different files concurrently.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|getDifferentConcurrent
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Started getDifferentConcurrent"
argument_list|)
expr_stmt|;
name|File
name|f
init|=
name|createFile
argument_list|(
literal|0
argument_list|,
name|loader
argument_list|,
name|cache
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|File
name|f2
init|=
name|createFile
argument_list|(
literal|1
argument_list|,
name|loader
argument_list|,
name|cache
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|ListeningExecutorService
name|executorService
init|=
name|MoreExecutors
operator|.
name|listeningDecorator
argument_list|(
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
operator|new
name|ExecutorCloser
argument_list|(
name|executorService
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|CountDownLatch
name|thread1Start
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|SettableFuture
argument_list|<
name|File
argument_list|>
name|future1
init|=
name|retrieveThread
argument_list|(
name|executorService
argument_list|,
name|ID_PREFIX
operator|+
literal|0
argument_list|,
name|cache
argument_list|,
name|thread1Start
argument_list|)
decl_stmt|;
name|CountDownLatch
name|thread2Start
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|SettableFuture
argument_list|<
name|File
argument_list|>
name|future2
init|=
name|retrieveThread
argument_list|(
name|executorService
argument_list|,
name|ID_PREFIX
operator|+
literal|1
argument_list|,
name|cache
argument_list|,
name|thread2Start
argument_list|)
decl_stmt|;
name|thread1Start
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|thread2Start
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|future1
operator|.
name|get
argument_list|()
expr_stmt|;
name|future2
operator|.
name|get
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Async tasks finished"
argument_list|)
expr_stmt|;
name|assertCacheIfPresent
argument_list|(
literal|0
argument_list|,
name|cache
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|assertCacheIfPresent
argument_list|(
literal|1
argument_list|,
name|cache
argument_list|,
name|f2
argument_list|)
expr_stmt|;
name|assertCacheStats
argument_list|(
name|cache
argument_list|,
literal|2
argument_list|,
literal|8
operator|*
literal|1024
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished getDifferentConcurrent"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Retrieve and put different files concurrently.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|retrievePutConcurrent
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Started retrievePutConcurrent"
argument_list|)
expr_stmt|;
comment|//Create load
specifier|final
name|File
name|f
init|=
name|createFile
argument_list|(
literal|0
argument_list|,
name|loader
argument_list|,
name|cache
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|File
name|f2
init|=
name|copyToFile
argument_list|(
name|randomStream
argument_list|(
literal|1
argument_list|,
literal|4
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
name|ListeningExecutorService
name|executorService
init|=
name|MoreExecutors
operator|.
name|listeningDecorator
argument_list|(
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
operator|new
name|ExecutorCloser
argument_list|(
name|executorService
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|CountDownLatch
name|thread1Start
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|SettableFuture
argument_list|<
name|File
argument_list|>
name|future1
init|=
name|retrieveThread
argument_list|(
name|executorService
argument_list|,
name|ID_PREFIX
operator|+
literal|0
argument_list|,
name|cache
argument_list|,
name|thread1Start
argument_list|)
decl_stmt|;
name|CountDownLatch
name|thread2Start
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|SettableFuture
argument_list|<
name|Boolean
argument_list|>
name|future2
init|=
name|putThread
argument_list|(
name|executorService
argument_list|,
literal|1
argument_list|,
name|f2
argument_list|,
name|cache
argument_list|,
name|thread2Start
argument_list|)
decl_stmt|;
name|thread1Start
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|thread2Start
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|future1
operator|.
name|get
argument_list|()
expr_stmt|;
name|future2
operator|.
name|get
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Async tasks finished"
argument_list|)
expr_stmt|;
name|assertCacheIfPresent
argument_list|(
literal|0
argument_list|,
name|cache
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|assertCacheIfPresent
argument_list|(
literal|1
argument_list|,
name|cache
argument_list|,
name|copyToFile
argument_list|(
name|randomStream
argument_list|(
literal|1
argument_list|,
literal|4
operator|*
literal|1024
argument_list|)
argument_list|,
name|folder
operator|.
name|newFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertCacheStats
argument_list|(
name|cache
argument_list|,
literal|2
argument_list|,
literal|8
operator|*
literal|1024
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished retrievePutConcurrent"
argument_list|)
expr_stmt|;
block|}
comment|/**      * evict explicitly.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|evictExplicit
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Started evictExplicit"
argument_list|)
expr_stmt|;
name|File
name|f
init|=
name|createFile
argument_list|(
literal|0
argument_list|,
name|loader
argument_list|,
name|cache
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|assertCache
argument_list|(
literal|0
argument_list|,
name|cache
argument_list|,
name|f
argument_list|)
expr_stmt|;
comment|// trigger explicit invalidate
name|cache
operator|.
name|invalidate
argument_list|(
name|ID_PREFIX
operator|+
literal|0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cache
operator|.
name|containsKey
argument_list|(
name|ID_PREFIX
operator|+
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertCacheStats
argument_list|(
name|cache
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished evictExplicit"
argument_list|)
expr_stmt|;
block|}
comment|/**      * evict implicitly.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|evictImplicit
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Started evictImplicit"
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
literal|15
condition|;
name|i
operator|++
control|)
block|{
name|File
name|f
init|=
name|createFile
argument_list|(
name|i
argument_list|,
name|loader
argument_list|,
name|cache
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|assertCache
argument_list|(
name|i
argument_list|,
name|cache
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
name|File
name|f
init|=
name|createFile
argument_list|(
literal|30
argument_list|,
name|loader
argument_list|,
name|cache
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|assertCache
argument_list|(
literal|30
argument_list|,
name|cache
argument_list|,
name|f
argument_list|)
expr_stmt|;
comment|// One of the entries should have been evicted
name|assertTrue
argument_list|(
name|cache
operator|.
name|getStats
argument_list|()
operator|.
name|getElementCount
argument_list|()
operator|==
literal|15
argument_list|)
expr_stmt|;
name|assertCacheStats
argument_list|(
name|cache
argument_list|,
literal|15
argument_list|,
literal|60
operator|*
literal|1024
argument_list|,
literal|16
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished evictImplicit"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Retrieve and invalidate concurrently.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|getInvalidateConcurrent
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Started getInvalidateConcurrent"
argument_list|)
expr_stmt|;
comment|//Create load
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|15
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|!=
literal|4
condition|)
block|{
name|File
name|f
init|=
name|createFile
argument_list|(
name|i
argument_list|,
name|loader
argument_list|,
name|cache
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|assertCache
argument_list|(
name|i
argument_list|,
name|cache
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished creating load"
argument_list|)
expr_stmt|;
name|ListeningExecutorService
name|executorService
init|=
name|MoreExecutors
operator|.
name|listeningDecorator
argument_list|(
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
operator|new
name|ExecutorCloser
argument_list|(
name|executorService
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|CountDownLatch
name|thread1Start
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|SettableFuture
argument_list|<
name|File
argument_list|>
name|future1
init|=
name|retrieveThread
argument_list|(
name|executorService
argument_list|,
name|ID_PREFIX
operator|+
literal|10
argument_list|,
name|cache
argument_list|,
name|thread1Start
argument_list|)
decl_stmt|;
name|thread1Start
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|File
name|f
init|=
name|createFile
argument_list|(
literal|4
argument_list|,
name|loader
argument_list|,
name|cache
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|CountDownLatch
name|thread2Start
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|SettableFuture
argument_list|<
name|File
argument_list|>
name|future2
init|=
name|retrieveThread
argument_list|(
name|executorService
argument_list|,
name|ID_PREFIX
operator|+
literal|4
argument_list|,
name|cache
argument_list|,
name|thread2Start
argument_list|)
decl_stmt|;
name|thread2Start
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|File
name|f10
init|=
name|future1
operator|.
name|get
argument_list|()
decl_stmt|;
name|future2
operator|.
name|get
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Async tasks finished"
argument_list|)
expr_stmt|;
if|if
condition|(
name|f10
operator|.
name|exists
argument_list|()
condition|)
block|{
name|assertCacheIfPresent
argument_list|(
literal|10
argument_list|,
name|cache
argument_list|,
name|f10
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
name|assertCacheIfPresent
argument_list|(
literal|4
argument_list|,
name|cache
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished getInvalidateConcurrent"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Trigger build cache on start.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|rebuild
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Started rebuild"
argument_list|)
expr_stmt|;
name|root
operator|=
name|folder
operator|.
name|newFolder
argument_list|()
expr_stmt|;
name|CountDownLatch
name|beforeLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|CountDownLatch
name|afterLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|CountDownLatch
name|afterExecuteLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|TestExecutor
name|executor
init|=
operator|new
name|TestExecutor
argument_list|(
literal|1
argument_list|,
name|beforeLatch
argument_list|,
name|afterLatch
argument_list|,
name|afterExecuteLatch
argument_list|)
decl_stmt|;
name|beforeLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|afterLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|cache
operator|=
name|FileCache
operator|.
name|build
argument_list|(
literal|4
operator|*
literal|1024
comment|/* bytes */
argument_list|,
name|root
argument_list|,
name|loader
argument_list|,
name|executor
argument_list|)
expr_stmt|;
name|afterExecuteLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|Futures
operator|.
name|successfulAsList
argument_list|(
operator|(
name|Iterable
argument_list|<
name|?
extends|extends
name|ListenableFuture
argument_list|<
name|?
argument_list|>
argument_list|>
operator|)
name|executor
operator|.
name|futures
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Cache built"
argument_list|)
expr_stmt|;
name|File
name|f
init|=
name|createFile
argument_list|(
literal|0
argument_list|,
name|loader
argument_list|,
name|cache
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|assertCache
argument_list|(
literal|0
argument_list|,
name|cache
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
name|beforeLatch
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|afterLatch
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|afterExecuteLatch
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|executor
operator|=
operator|new
name|TestExecutor
argument_list|(
literal|1
argument_list|,
name|beforeLatch
argument_list|,
name|afterLatch
argument_list|,
name|afterExecuteLatch
argument_list|)
expr_stmt|;
name|beforeLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|afterLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|cache
operator|=
name|FileCache
operator|.
name|build
argument_list|(
literal|4
operator|*
literal|1024
comment|/* bytes */
argument_list|,
name|root
argument_list|,
name|loader
argument_list|,
name|executor
argument_list|)
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|cache
argument_list|)
expr_stmt|;
name|afterExecuteLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|Futures
operator|.
name|successfulAsList
argument_list|(
operator|(
name|Iterable
argument_list|<
name|?
extends|extends
name|ListenableFuture
argument_list|<
name|?
argument_list|>
argument_list|>
operator|)
name|executor
operator|.
name|futures
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Cache rebuilt"
argument_list|)
expr_stmt|;
name|assertCacheIfPresent
argument_list|(
literal|0
argument_list|,
name|cache
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|assertCacheStats
argument_list|(
name|cache
argument_list|,
literal|1
argument_list|,
literal|4
operator|*
literal|1024
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished rebuild"
argument_list|)
expr_stmt|;
block|}
comment|/**------------------------------ Helper methods --------------------------------------------**/
specifier|private
specifier|static
name|SettableFuture
argument_list|<
name|File
argument_list|>
name|retrieveThread
parameter_list|(
name|ListeningExecutorService
name|executor
parameter_list|,
specifier|final
name|String
name|id
parameter_list|,
specifier|final
name|FileCache
name|cache
parameter_list|,
specifier|final
name|CountDownLatch
name|start
parameter_list|)
block|{
specifier|final
name|SettableFuture
argument_list|<
name|File
argument_list|>
name|future
init|=
name|SettableFuture
operator|.
name|create
argument_list|()
decl_stmt|;
name|executor
operator|.
name|submit
argument_list|(
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
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for start retrieve"
argument_list|)
expr_stmt|;
name|start
operator|.
name|await
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting retrieve [{}]"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|File
name|cached
init|=
name|cache
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished retrieve"
argument_list|)
expr_stmt|;
name|future
operator|.
name|set
argument_list|(
name|cached
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
name|info
argument_list|(
literal|"Exception in get"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|future
return|;
block|}
specifier|private
specifier|static
name|SettableFuture
argument_list|<
name|Boolean
argument_list|>
name|putThread
parameter_list|(
name|ListeningExecutorService
name|executor
parameter_list|,
specifier|final
name|int
name|seed
parameter_list|,
specifier|final
name|File
name|f
parameter_list|,
specifier|final
name|FileCache
name|cache
parameter_list|,
specifier|final
name|CountDownLatch
name|start
parameter_list|)
block|{
specifier|final
name|SettableFuture
argument_list|<
name|Boolean
argument_list|>
name|future
init|=
name|SettableFuture
operator|.
name|create
argument_list|()
decl_stmt|;
name|executor
operator|.
name|submit
argument_list|(
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
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for start to put"
argument_list|)
expr_stmt|;
name|start
operator|.
name|await
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting put"
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|ID_PREFIX
operator|+
name|seed
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished put"
argument_list|)
expr_stmt|;
name|future
operator|.
name|set
argument_list|(
literal|true
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
name|info
argument_list|(
literal|"Exception in get"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|future
return|;
block|}
specifier|private
specifier|static
name|int
name|getWeight
parameter_list|(
name|String
name|key
parameter_list|,
name|File
name|value
parameter_list|)
block|{
return|return
name|StringUtils
operator|.
name|estimateMemoryUsage
argument_list|(
name|key
argument_list|)
operator|+
name|StringUtils
operator|.
name|estimateMemoryUsage
argument_list|(
name|value
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
operator|+
literal|48
return|;
block|}
specifier|private
specifier|static
name|void
name|assertCacheIfPresent
parameter_list|(
name|int
name|seed
parameter_list|,
name|FileCache
name|cache
parameter_list|,
name|File
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|cached
init|=
name|cache
operator|.
name|getIfPresent
argument_list|(
name|ID_PREFIX
operator|+
name|seed
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|cached
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Files
operator|.
name|equal
argument_list|(
name|f
argument_list|,
name|cached
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertCache
parameter_list|(
name|int
name|seed
parameter_list|,
name|FileCache
name|cache
parameter_list|,
name|File
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|cached
init|=
name|cache
operator|.
name|get
argument_list|(
name|ID_PREFIX
operator|+
name|seed
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|cached
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Files
operator|.
name|equal
argument_list|(
name|f
argument_list|,
name|cached
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|File
name|createFile
parameter_list|(
name|int
name|seed
parameter_list|,
name|TestCacheLoader
name|loader
parameter_list|,
name|FileCache
name|cache
parameter_list|,
name|TemporaryFolder
name|folder
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|f
init|=
name|copyToFile
argument_list|(
name|randomStream
argument_list|(
literal|0
argument_list|,
literal|4
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
name|loader
operator|.
name|write
argument_list|(
name|ID_PREFIX
operator|+
name|seed
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|getIfPresent
argument_list|(
name|ID_PREFIX
operator|+
name|seed
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
specifier|private
specifier|static
name|void
name|assertCacheStats
parameter_list|(
name|FileCache
name|cache
parameter_list|,
name|long
name|elems
parameter_list|,
name|long
name|weight
parameter_list|,
name|long
name|loads
parameter_list|,
name|long
name|loadSuccesses
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|elems
argument_list|,
name|cache
operator|.
name|getStats
argument_list|()
operator|.
name|getElementCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|weight
argument_list|,
name|cache
operator|.
name|getStats
argument_list|()
operator|.
name|estimateCurrentWeight
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|loads
argument_list|,
name|cache
operator|.
name|getStats
argument_list|()
operator|.
name|getLoadCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|loadSuccesses
argument_list|,
name|cache
operator|.
name|getStats
argument_list|()
operator|.
name|getLoadSuccessCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

