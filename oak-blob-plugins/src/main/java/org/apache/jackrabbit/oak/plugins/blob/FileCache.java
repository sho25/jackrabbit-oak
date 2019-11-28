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
name|Iterator
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
name|ExecutionException
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
name|base
operator|.
name|Predicate
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
name|cache
operator|.
name|AbstractCache
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
name|Cache
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
name|cache
operator|.
name|RemovalCause
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
name|Weigher
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
name|oak
operator|.
name|cache
operator|.
name|CacheLIRS
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
name|CacheLIRS
operator|.
name|EvictionCallback
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
name|commons
operator|.
name|io
operator|.
name|FilenameUtils
operator|.
name|normalizeNoEndSeparator
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
name|copyInputStreamToFile
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|FileCache
extends|extends
name|AbstractCache
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
implements|implements
name|Closeable
block|{
comment|/**      * Logger instance.      */
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
name|FileCache
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|DOWNLOAD_DIR
init|=
literal|"download"
decl_stmt|;
comment|/**      * Parent of the cache root directory      */
specifier|private
name|File
name|parent
decl_stmt|;
comment|/**      * The cacheRoot directory of the cache.      */
specifier|private
name|File
name|cacheRoot
decl_stmt|;
specifier|private
name|CacheLIRS
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
name|cache
decl_stmt|;
specifier|private
name|FileCacheStats
name|cacheStats
decl_stmt|;
specifier|private
name|ExecutorService
name|executor
decl_stmt|;
specifier|private
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
name|cacheLoader
decl_stmt|;
comment|/**      * Convert the size calculation to KB to support max file size of 2 TB      */
specifier|private
specifier|static
specifier|final
name|Weigher
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
name|weigher
init|=
operator|new
name|Weigher
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|weigh
parameter_list|(
name|String
name|key
parameter_list|,
name|File
name|value
parameter_list|)
block|{
return|return
name|Math
operator|.
name|round
argument_list|(
name|value
operator|.
name|length
argument_list|()
operator|/
operator|(
literal|4
operator|*
literal|1024
operator|)
argument_list|)
return|;
comment|// convert to KB
block|}
block|}
decl_stmt|;
comment|//Rough estimate of the in-memory key, value pair
specifier|private
specifier|static
specifier|final
name|Weigher
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
name|memWeigher
init|=
operator|new
name|Weigher
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|weigh
parameter_list|(
name|String
name|key
parameter_list|,
name|File
name|value
parameter_list|)
block|{
return|return
operator|(
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
operator|)
return|;
block|}
block|}
decl_stmt|;
specifier|private
name|FileCache
parameter_list|(
name|long
name|maxSize
comment|/* bytes */
parameter_list|,
name|File
name|root
parameter_list|,
specifier|final
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|InputStream
argument_list|>
name|loader
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|ExecutorService
name|executor
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|cacheRoot
operator|=
operator|new
name|File
argument_list|(
name|root
argument_list|,
name|DOWNLOAD_DIR
argument_list|)
expr_stmt|;
comment|/* convert to 4 KB block */
name|long
name|size
init|=
name|Math
operator|.
name|round
argument_list|(
name|maxSize
operator|/
operator|(
literal|1024L
operator|*
literal|4
operator|)
argument_list|)
decl_stmt|;
name|cacheLoader
operator|=
operator|new
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|File
name|load
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Fetch from local cache directory and if not found load from backend
name|File
name|cachedFile
init|=
name|DataStoreCacheUtils
operator|.
name|getFile
argument_list|(
name|key
argument_list|,
name|cacheRoot
argument_list|)
decl_stmt|;
if|if
condition|(
name|cachedFile
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|cachedFile
return|;
block|}
else|else
block|{
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
name|boolean
name|threw
init|=
literal|true
decl_stmt|;
try|try
block|{
name|is
operator|=
name|loader
operator|.
name|load
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|copyInputStreamToFile
argument_list|(
name|is
argument_list|,
name|cachedFile
argument_list|)
expr_stmt|;
name|threw
operator|=
literal|false
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
name|warn
argument_list|(
literal|"Error reading object for id [{}] from backend"
argument_list|,
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|Closeables
operator|.
name|close
argument_list|(
name|is
argument_list|,
name|threw
argument_list|)
expr_stmt|;
block|}
return|return
name|cachedFile
return|;
block|}
block|}
block|}
expr_stmt|;
name|cache
operator|=
operator|new
name|CacheLIRS
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
argument_list|()
operator|.
name|maximumWeight
argument_list|(
name|size
argument_list|)
operator|.
name|recordStats
argument_list|()
operator|.
name|weigher
argument_list|(
name|weigher
argument_list|)
operator|.
name|evictionCallback
argument_list|(
operator|new
name|EvictionCallback
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|evicted
parameter_list|(
annotation|@
name|NotNull
name|String
name|key
parameter_list|,
annotation|@
name|Nullable
name|File
name|cachedFile
parameter_list|,
annotation|@
name|NotNull
name|RemovalCause
name|cause
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|cachedFile
operator|!=
literal|null
operator|&&
name|cachedFile
operator|.
name|exists
argument_list|()
operator|&&
name|cause
operator|!=
name|RemovalCause
operator|.
name|REPLACED
condition|)
block|{
name|DataStoreCacheUtils
operator|.
name|recursiveDelete
argument_list|(
name|cachedFile
argument_list|,
name|cacheRoot
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"File [{}] evicted with reason [{}]"
argument_list|,
name|cachedFile
argument_list|,
name|cause
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cached file deletion failed after eviction"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|cacheStats
operator|=
operator|new
name|FileCacheStats
argument_list|(
name|cache
argument_list|,
name|weigher
argument_list|,
name|memWeigher
argument_list|,
name|maxSize
argument_list|)
expr_stmt|;
comment|//  TODO: Check persisting the in-memory map and initializing Vs building from fs
comment|// Build in-memory cache asynchronously from the file system entries
if|if
condition|(
name|executor
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|executor
operator|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
block|}
name|this
operator|.
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|CacheBuildJob
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|FileCache
parameter_list|()
block|{     }
specifier|public
specifier|static
name|FileCache
name|build
parameter_list|(
name|long
name|maxSize
comment|/* bytes */
parameter_list|,
name|File
name|root
parameter_list|,
specifier|final
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|InputStream
argument_list|>
name|loader
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|ExecutorService
name|executor
parameter_list|)
block|{
if|if
condition|(
name|maxSize
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|FileCache
argument_list|(
name|maxSize
argument_list|,
name|root
argument_list|,
name|loader
argument_list|,
name|executor
argument_list|)
return|;
block|}
return|return
operator|new
name|FileCache
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|put
parameter_list|(
name|String
name|key
parameter_list|,
name|File
name|file
parameter_list|)
block|{             }
annotation|@
name|Override
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|File
name|getIfPresent
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|File
name|get
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|invalidate
parameter_list|(
name|Object
name|key
parameter_list|)
block|{             }
annotation|@
name|Override
specifier|public
name|DataStoreCacheStatsMBean
name|getStats
parameter_list|()
block|{
return|return
operator|new
name|FileCacheStats
argument_list|(
name|this
argument_list|,
name|weigher
argument_list|,
name|memWeigher
argument_list|,
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{             }
block|}
return|;
block|}
comment|/**      * Puts the given key and file into the cache.      * The file is moved to the cache. So, the original file      * won't be available after this operation. It can be retrieved      * using {@link #getIfPresent(String)}.      *      * @param key of the file      * @param file to put into cache      */
annotation|@
name|Override
specifier|public
name|void
name|put
parameter_list|(
name|String
name|key
parameter_list|,
name|File
name|file
parameter_list|)
block|{
name|put
argument_list|(
name|key
argument_list|,
name|file
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|put
parameter_list|(
name|String
name|key
parameter_list|,
name|File
name|file
parameter_list|,
name|boolean
name|copy
parameter_list|)
block|{
try|try
block|{
name|File
name|cached
init|=
name|DataStoreCacheUtils
operator|.
name|getFile
argument_list|(
name|key
argument_list|,
name|cacheRoot
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|cached
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|copy
condition|)
block|{
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|file
argument_list|,
name|cached
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FileUtils
operator|.
name|moveFile
argument_list|(
name|file
argument_list|,
name|cached
argument_list|)
expr_stmt|;
block|}
block|}
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|cached
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception adding id [{}] with file [{}] to cache"
argument_list|,
name|key
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|cache
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**      * Retrieves the file handle from the cache if present and null otherwise.      *      * @param key of the file to retrieve      * @return File handle if available      */
annotation|@
name|Nullable
specifier|public
name|File
name|getIfPresent
parameter_list|(
name|String
name|key
parameter_list|)
block|{
try|try
block|{
return|return
name|cache
operator|.
name|getIfPresent
argument_list|(
name|key
argument_list|)
return|;
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
literal|"Error in retrieving [{}] from cache"
argument_list|,
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|File
name|getIfPresent
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|getIfPresent
argument_list|(
operator|(
name|String
operator|)
name|key
argument_list|)
return|;
block|}
specifier|public
name|File
name|get
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
comment|// get from cache and download if not available
return|return
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|,
parameter_list|()
lambda|->
name|cacheLoader
operator|.
name|load
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error loading [{}] from cache"
argument_list|,
name|key
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
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
name|invalidate
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|cache
operator|.
name|invalidate
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DataStoreCacheStatsMBean
name|getStats
parameter_list|()
block|{
return|return
name|cacheStats
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cache stats on close [{}]"
argument_list|,
name|cacheStats
operator|.
name|cacheInfoAsString
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|ExecutorCloser
argument_list|(
name|executor
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Called to initialize the in-memory cache from the fs folder      */
specifier|private
class|class
name|CacheBuildJob
implements|implements
name|Callable
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|call
parameter_list|()
block|{
name|Stopwatch
name|watch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|int
name|count
init|=
name|build
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Cache built with [{}] files from file system in [{}] seconds"
argument_list|,
name|count
argument_list|,
name|watch
operator|.
name|elapsed
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|count
return|;
block|}
block|}
comment|/**      * Retrieves all the files present in the fs cache folder and builds the in-memory cache.      */
specifier|private
name|int
name|build
parameter_list|()
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
comment|// Move older generation cache downloaded files to the new folder
name|DataStoreCacheUpgradeUtils
operator|.
name|moveDownloadCache
argument_list|(
name|parent
argument_list|)
expr_stmt|;
comment|// Iterate over all files in the cache folder
name|Iterator
argument_list|<
name|File
argument_list|>
name|iter
init|=
name|Files
operator|.
name|fileTreeTraverser
argument_list|()
operator|.
name|postOrderTraversal
argument_list|(
name|cacheRoot
argument_list|)
operator|.
name|filter
argument_list|(
operator|new
name|Predicate
argument_list|<
name|File
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|File
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|isFile
argument_list|()
operator|&&
operator|!
name|normalizeNoEndSeparator
argument_list|(
name|input
operator|.
name|getParent
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|cacheRoot
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|iterator
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
name|File
name|toBeSyncedFile
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|put
argument_list|(
name|toBeSyncedFile
operator|.
name|getName
argument_list|()
argument_list|,
name|toBeSyncedFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Added file [{}} to in-memory cache"
argument_list|,
name|toBeSyncedFile
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
literal|"Error in putting cached file in map[{}]"
argument_list|,
name|toBeSyncedFile
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|trace
argument_list|(
literal|"[{}] files put in im-memory cache"
argument_list|,
name|count
argument_list|)
expr_stmt|;
return|return
name|count
return|;
block|}
block|}
end_class

begin_class
class|class
name|FileCacheStats
extends|extends
name|CacheStats
implements|implements
name|DataStoreCacheStatsMBean
block|{
specifier|private
specifier|static
specifier|final
name|long
name|KB
init|=
literal|4
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|final
name|Weigher
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|memWeigher
decl_stmt|;
specifier|private
specifier|final
name|Weigher
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|weigher
decl_stmt|;
specifier|private
specifier|final
name|Cache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|cache
decl_stmt|;
comment|/**      * Construct the cache stats object.      *  @param cache     the cache      * @param weigher   the weigher used to estimate the current weight      * @param maxWeight the maximum weight      */
specifier|public
name|FileCacheStats
parameter_list|(
name|Cache
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|cache
parameter_list|,
name|Weigher
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|weigher
parameter_list|,
name|Weigher
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|memWeigher
parameter_list|,
name|long
name|maxWeight
parameter_list|)
block|{
name|super
argument_list|(
name|cache
argument_list|,
literal|"DataStore-DownloadCache"
argument_list|,
name|weigher
argument_list|,
name|maxWeight
argument_list|)
expr_stmt|;
name|this
operator|.
name|memWeigher
operator|=
operator|(
name|Weigher
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
operator|)
name|memWeigher
expr_stmt|;
name|this
operator|.
name|weigher
operator|=
operator|(
name|Weigher
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
operator|)
name|weigher
expr_stmt|;
name|this
operator|.
name|cache
operator|=
operator|(
name|Cache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
operator|)
name|cache
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|estimateCurrentMemoryWeight
parameter_list|()
block|{
if|if
condition|(
name|memWeigher
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
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|e
range|:
name|cache
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|k
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|v
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|size
operator|+=
name|memWeigher
operator|.
name|weigh
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|estimateCurrentWeight
parameter_list|()
block|{
if|if
condition|(
name|weigher
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
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|e
range|:
name|cache
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|k
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|v
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|size
operator|+=
name|weigher
operator|.
name|weigh
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
operator|*
name|KB
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
block|}
end_class

end_unit

