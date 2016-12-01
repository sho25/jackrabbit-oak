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
name|BufferedOutputStream
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
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|io
operator|.
name|ObjectOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|Map
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
name|Callable
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
name|ExecutorService
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
name|LinkedBlockingQueue
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheLoader
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
name|AbstractListeningExecutorService
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
name|FutureCallback
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
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|spi
operator|.
name|blob
operator|.
name|AbstractDataRecord
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
name|AbstractSharedBackend
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
comment|/**  * Abstract class for DataStore cache related tests.  */
end_comment

begin_class
specifier|public
class|class
name|AbstractDataStoreCacheTest
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractDataStoreCacheTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
class|class
name|TestStagingUploader
implements|implements
name|StagingUploader
block|{
specifier|private
specifier|final
name|File
name|root
decl_stmt|;
specifier|public
name|TestStagingUploader
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"datastore"
argument_list|)
expr_stmt|;
name|root
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|String
name|id
parameter_list|,
name|File
name|f
parameter_list|)
throws|throws
name|DataStoreException
block|{
try|try
block|{
name|File
name|move
init|=
name|getFile
argument_list|(
name|id
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|move
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|f
argument_list|,
name|move
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"In TestStagingUploader after write [{}]"
argument_list|,
name|move
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataStoreException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|File
name|read
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|getFile
argument_list|(
name|id
argument_list|,
name|root
argument_list|)
return|;
block|}
block|}
specifier|static
class|class
name|TestCacheLoader
parameter_list|<
name|S
parameter_list|,
name|I
parameter_list|>
extends|extends
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|FileInputStream
argument_list|>
block|{
specifier|private
specifier|final
name|File
name|root
decl_stmt|;
specifier|public
name|TestCacheLoader
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"datastore"
argument_list|)
expr_stmt|;
name|root
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|String
name|id
parameter_list|,
name|File
name|f
parameter_list|)
throws|throws
name|DataStoreException
block|{
try|try
block|{
name|File
name|move
init|=
name|getFile
argument_list|(
name|id
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|move
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|f
argument_list|,
name|move
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"In TestCacheLoader after write [{}], [{}]"
argument_list|,
name|id
argument_list|,
name|move
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataStoreException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|FileInputStream
name|load
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|FileUtils
operator|.
name|openInputStream
argument_list|(
name|getFile
argument_list|(
name|key
argument_list|,
name|root
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|static
class|class
name|TestPoolExecutor
extends|extends
name|ThreadPoolExecutor
block|{
specifier|private
specifier|final
name|CountDownLatch
name|beforeLatch
decl_stmt|;
specifier|private
specifier|final
name|CountDownLatch
name|afterLatch
decl_stmt|;
name|TestPoolExecutor
parameter_list|(
name|int
name|threads
parameter_list|,
name|CountDownLatch
name|beforeLatch
parameter_list|,
name|CountDownLatch
name|afterLatch
parameter_list|)
block|{
name|super
argument_list|(
name|threads
argument_list|,
name|threads
argument_list|,
literal|0L
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|NamedThreadFactory
argument_list|(
literal|"oak-async-thread"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|beforeLatch
operator|=
name|beforeLatch
expr_stmt|;
name|this
operator|.
name|afterLatch
operator|=
name|afterLatch
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeExecute
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Runnable
name|command
parameter_list|)
block|{
try|try
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Before execution....waiting for latch"
argument_list|)
expr_stmt|;
name|beforeLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Before execution....after acquiring latch"
argument_list|)
expr_stmt|;
name|super
operator|.
name|beforeExecute
argument_list|(
name|t
argument_list|,
name|command
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Completed beforeExecute"
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
name|trace
argument_list|(
literal|"Error in before execute"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|afterExecute
parameter_list|(
name|Runnable
name|r
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
try|try
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"After execution....counting down latch"
argument_list|)
expr_stmt|;
name|afterLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"After execution....after counting down latch"
argument_list|)
expr_stmt|;
name|super
operator|.
name|afterExecute
argument_list|(
name|r
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Completed afterExecute"
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
name|trace
argument_list|(
literal|"Error in after execute"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|static
class|class
name|TestExecutor
extends|extends
name|AbstractListeningExecutorService
block|{
specifier|private
specifier|final
name|CountDownLatch
name|afterLatch
decl_stmt|;
specifier|private
specifier|final
name|ExecutorService
name|delegate
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ListenableFuture
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|futures
decl_stmt|;
specifier|public
name|TestExecutor
parameter_list|(
name|int
name|threads
parameter_list|,
name|CountDownLatch
name|beforeLatch
parameter_list|,
name|CountDownLatch
name|afterLatch
parameter_list|,
name|CountDownLatch
name|afterExecuteLatch
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
operator|new
name|TestPoolExecutor
argument_list|(
name|threads
argument_list|,
name|beforeLatch
argument_list|,
name|afterExecuteLatch
argument_list|)
expr_stmt|;
name|this
operator|.
name|futures
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
name|this
operator|.
name|afterLatch
operator|=
name|afterLatch
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|ListenableFuture
argument_list|<
name|?
argument_list|>
name|submit
parameter_list|(
annotation|@
name|Nonnull
name|Callable
name|task
parameter_list|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Before submitting to super...."
argument_list|)
expr_stmt|;
name|ListenableFuture
argument_list|<
name|Integer
argument_list|>
name|submit
init|=
name|super
operator|.
name|submit
argument_list|(
name|task
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"After submitting to super...."
argument_list|)
expr_stmt|;
name|futures
operator|.
name|add
argument_list|(
name|submit
argument_list|)
expr_stmt|;
name|Futures
operator|.
name|addCallback
argument_list|(
name|submit
argument_list|,
operator|new
name|TestFutureCallback
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|afterLatch
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Added callback"
argument_list|)
expr_stmt|;
return|return
name|submit
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
annotation|@
name|Nonnull
name|Runnable
name|command
parameter_list|)
block|{
name|delegate
operator|.
name|execute
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|delegate
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|List
argument_list|<
name|Runnable
argument_list|>
name|shutdownNow
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|shutdownNow
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isShutdown
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|isShutdown
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isTerminated
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|isTerminated
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|awaitTermination
parameter_list|(
name|long
name|timeout
parameter_list|,
annotation|@
name|Nonnull
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
block|{
return|return
name|delegate
operator|.
name|awaitTermination
argument_list|(
name|timeout
argument_list|,
name|unit
argument_list|)
return|;
block|}
specifier|static
class|class
name|TestFutureCallback
parameter_list|<
name|Integer
parameter_list|>
implements|implements
name|FutureCallback
block|{
specifier|private
specifier|final
name|CountDownLatch
name|latch
decl_stmt|;
specifier|public
name|TestFutureCallback
parameter_list|(
name|CountDownLatch
name|latch
parameter_list|)
block|{
name|this
operator|.
name|latch
operator|=
name|latch
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
annotation|@
name|Nullable
name|Object
name|result
parameter_list|)
block|{
try|try
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Waiting for latch in callback"
argument_list|)
expr_stmt|;
name|latch
operator|.
name|await
argument_list|(
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Acquired latch in onSuccess"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
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
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
annotation|@
name|Nonnull
name|Throwable
name|t
parameter_list|)
block|{
try|try
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Waiting for latch onFailure in callback"
argument_list|)
expr_stmt|;
name|latch
operator|.
name|await
argument_list|(
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Acquired latch in onFailure"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
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
comment|// A mock Backend implementation that uses a Map to keep track of what
comment|// records have been added and removed, for test purposes only.
specifier|static
class|class
name|TestMemoryBackend
extends|extends
name|AbstractSharedBackend
block|{
specifier|final
name|Map
argument_list|<
name|DataIdentifier
argument_list|,
name|File
argument_list|>
name|_backend
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|InputStream
name|read
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
throws|throws
name|DataStoreException
block|{
try|try
block|{
return|return
operator|new
name|FileInputStream
argument_list|(
name|_backend
operator|.
name|get
argument_list|(
name|identifier
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataStoreException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|DataStoreException
block|{
if|if
condition|(
name|file
operator|!=
literal|null
operator|&&
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|_backend
operator|.
name|put
argument_list|(
name|identifier
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|DataStoreException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"file %s of id %s"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|file
block|,
name|identifier
operator|.
name|toString
argument_list|()
block|}
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|DataRecord
name|getRecord
parameter_list|(
name|DataIdentifier
name|id
parameter_list|)
throws|throws
name|DataStoreException
block|{
if|if
condition|(
name|_backend
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
condition|)
block|{
specifier|final
name|File
name|f
init|=
name|_backend
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
return|return
operator|new
name|AbstractDataRecord
argument_list|(
name|this
argument_list|,
name|id
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|getLength
parameter_list|()
throws|throws
name|DataStoreException
block|{
return|return
name|f
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|DataStoreException
block|{
try|try
block|{
return|return
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLastModified
parameter_list|()
block|{
return|return
name|f
operator|.
name|lastModified
argument_list|()
return|;
block|}
block|}
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|DataIdentifier
argument_list|>
name|getAllIdentifiers
parameter_list|()
throws|throws
name|DataStoreException
block|{
return|return
name|_backend
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|DataRecord
argument_list|>
name|getAllRecords
parameter_list|()
throws|throws
name|DataStoreException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
throws|throws
name|DataStoreException
block|{
return|return
name|_backend
operator|.
name|containsKey
argument_list|(
name|identifier
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|DataStoreException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|deleteRecord
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
throws|throws
name|DataStoreException
block|{
if|if
condition|(
name|_backend
operator|.
name|containsKey
argument_list|(
name|identifier
argument_list|)
condition|)
block|{
name|_backend
operator|.
name|remove
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|addMetadataRecord
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|DataStoreException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|addMetadataRecord
parameter_list|(
name|File
name|input
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|DataStoreException
block|{         }
annotation|@
name|Override
specifier|public
name|DataRecord
name|getMetadataRecord
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|DataRecord
argument_list|>
name|getAllMetadataRecords
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|deleteMetadataRecord
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deleteAllMetadataRecords
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|DataStoreException
block|{          }
annotation|@
name|Override
specifier|public
name|String
name|getReferenceFromIdentifier
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
block|{
return|return
name|super
operator|.
name|getReferenceFromIdentifier
argument_list|(
name|identifier
argument_list|)
return|;
block|}
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
specifier|protected
specifier|static
name|File
name|getFile
parameter_list|(
name|String
name|id
parameter_list|,
name|File
name|root
parameter_list|)
block|{
name|File
name|file
init|=
name|root
decl_stmt|;
name|file
operator|=
operator|new
name|File
argument_list|(
name|file
argument_list|,
name|id
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|file
operator|=
operator|new
name|File
argument_list|(
name|file
argument_list|,
name|id
operator|.
name|substring
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|File
argument_list|(
name|file
argument_list|,
name|id
argument_list|)
return|;
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
name|FileUtils
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
specifier|static
name|void
name|serializeMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|pendingupload
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|OutputStream
name|buffer
init|=
operator|new
name|BufferedOutputStream
argument_list|(
name|fos
argument_list|)
decl_stmt|;
name|ObjectOutput
name|output
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
try|try
block|{
name|output
operator|.
name|writeObject
argument_list|(
name|pendingupload
argument_list|)
expr_stmt|;
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

