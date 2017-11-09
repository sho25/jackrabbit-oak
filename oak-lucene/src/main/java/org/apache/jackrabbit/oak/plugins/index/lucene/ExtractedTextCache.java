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
name|index
operator|.
name|lucene
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
name|FileInputStream
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
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|ConcurrentHashMap
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
name|Future
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
name|ThreadFactory
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|CacheBuilder
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
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|fulltext
operator|.
name|ExtractedText
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
name|fulltext
operator|.
name|ExtractedText
operator|.
name|ExtractionResult
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
name|fulltext
operator|.
name|PreExtractedTextProvider
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
name|commons
operator|.
name|PathUtils
operator|.
name|concat
import|;
end_import

begin_class
specifier|public
class|class
name|ExtractedTextCache
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|CACHE_ONLY_SUCCESS
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"oak.extracted.cacheOnlySuccess"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|EXTRACTION_TIMEOUT_SECONDS
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.extraction.timeoutSeconds"
argument_list|,
literal|60
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|EXTRACTION_MAX_THREADS
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.extraction.maxThreads"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|EXTRACT_IN_CALLER_THREAD
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"oak.extraction.inCallerThread"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|EXTRACT_FORGET_TIMEOUT
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"oak.extraction.forgetTimeout"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TIMEOUT_MAP
init|=
literal|"textExtractionTimeout.properties"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|EMPTY_STRING
init|=
literal|""
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
name|ExtractedTextCache
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|volatile
name|PreExtractedTextProvider
name|extractedTextProvider
decl_stmt|;
specifier|private
name|int
name|textExtractionCount
decl_stmt|;
specifier|private
name|long
name|totalBytesRead
decl_stmt|;
specifier|private
name|long
name|totalTextSize
decl_stmt|;
specifier|private
name|long
name|totalTime
decl_stmt|;
specifier|private
name|int
name|preFetchedCount
decl_stmt|;
specifier|private
specifier|final
name|Cache
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|cache
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|timeoutMap
decl_stmt|;
specifier|private
specifier|final
name|File
name|indexDir
decl_stmt|;
specifier|private
specifier|final
name|CacheStats
name|cacheStats
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|alwaysUsePreExtractedCache
decl_stmt|;
specifier|private
specifier|volatile
name|ExecutorService
name|executorService
decl_stmt|;
specifier|private
specifier|volatile
name|int
name|timeoutCount
decl_stmt|;
specifier|private
name|long
name|extractionTimeoutMillis
init|=
name|EXTRACTION_TIMEOUT_SECONDS
operator|*
literal|1000
decl_stmt|;
specifier|public
name|ExtractedTextCache
parameter_list|(
name|long
name|maxWeight
parameter_list|,
name|long
name|expiryTimeInSecs
parameter_list|)
block|{
name|this
argument_list|(
name|maxWeight
argument_list|,
name|expiryTimeInSecs
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ExtractedTextCache
parameter_list|(
name|long
name|maxWeight
parameter_list|,
name|long
name|expiryTimeInSecs
parameter_list|,
name|boolean
name|alwaysUsePreExtractedCache
parameter_list|,
name|File
name|indexDir
parameter_list|)
block|{
if|if
condition|(
name|maxWeight
operator|>
literal|0
condition|)
block|{
name|cache
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|weigher
argument_list|(
name|EmpiricalWeigher
operator|.
name|INSTANCE
argument_list|)
operator|.
name|maximumWeight
argument_list|(
name|maxWeight
argument_list|)
operator|.
name|expireAfterAccess
argument_list|(
name|expiryTimeInSecs
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|.
name|recordStats
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
name|cacheStats
operator|=
operator|new
name|CacheStats
argument_list|(
name|cache
argument_list|,
literal|"ExtractedTextCache"
argument_list|,
name|EmpiricalWeigher
operator|.
name|INSTANCE
argument_list|,
name|maxWeight
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cache
operator|=
literal|null
expr_stmt|;
name|cacheStats
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|alwaysUsePreExtractedCache
operator|=
name|alwaysUsePreExtractedCache
expr_stmt|;
name|this
operator|.
name|timeoutMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|indexDir
operator|=
name|indexDir
expr_stmt|;
name|loadTimeoutMap
argument_list|()
expr_stmt|;
block|}
comment|/**      * Get the pre extracted text for given blob      * @return null if no pre extracted text entry found. Otherwise returns the pre extracted      *  text      */
annotation|@
name|CheckForNull
specifier|public
name|String
name|get
parameter_list|(
name|String
name|nodePath
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|Blob
name|blob
parameter_list|,
name|boolean
name|reindexMode
parameter_list|)
block|{
name|String
name|result
init|=
literal|null
decl_stmt|;
comment|//Consult the PreExtractedTextProvider only in reindex mode and not in
comment|//incremental indexing mode. As that would only contain older entries
comment|//That also avoid loading on various state (See DataStoreTextWriter)
name|String
name|propertyPath
init|=
name|concat
argument_list|(
name|nodePath
argument_list|,
name|propertyName
argument_list|)
decl_stmt|;
name|log
operator|.
name|trace
argument_list|(
literal|"Looking for extracted text for [{}] with blobId [{}]"
argument_list|,
name|propertyPath
argument_list|,
name|blob
operator|.
name|getContentIdentity
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|reindexMode
operator|||
name|alwaysUsePreExtractedCache
operator|)
operator|&&
name|extractedTextProvider
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|ExtractedText
name|text
init|=
name|extractedTextProvider
operator|.
name|getText
argument_list|(
name|propertyPath
argument_list|,
name|blob
argument_list|)
decl_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|preFetchedCount
operator|++
expr_stmt|;
name|result
operator|=
name|getText
argument_list|(
name|text
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
name|log
operator|.
name|warn
argument_list|(
literal|"Error occurred while fetching pre extracted text for {}"
argument_list|,
name|propertyPath
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|id
init|=
name|blob
operator|.
name|getContentIdentity
argument_list|()
decl_stmt|;
if|if
condition|(
name|cache
operator|!=
literal|null
operator|&&
name|id
operator|!=
literal|null
operator|&&
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|cache
operator|.
name|getIfPresent
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|==
literal|null
operator|&&
name|id
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|timeoutMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|void
name|put
parameter_list|(
annotation|@
name|Nonnull
name|Blob
name|blob
parameter_list|,
annotation|@
name|Nonnull
name|ExtractedText
name|extractedText
parameter_list|)
block|{
name|String
name|id
init|=
name|blob
operator|.
name|getContentIdentity
argument_list|()
decl_stmt|;
if|if
condition|(
name|cache
operator|!=
literal|null
operator|&&
name|id
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|extractedText
operator|.
name|getExtractionResult
argument_list|()
operator|==
name|ExtractionResult
operator|.
name|SUCCESS
operator|||
operator|!
name|CACHE_ONLY_SUCCESS
condition|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|getText
argument_list|(
name|extractedText
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|putTimeout
parameter_list|(
annotation|@
name|Nonnull
name|Blob
name|blob
parameter_list|,
annotation|@
name|Nonnull
name|ExtractedText
name|extractedText
parameter_list|)
block|{
if|if
condition|(
name|EXTRACT_FORGET_TIMEOUT
condition|)
block|{
return|return;
block|}
name|String
name|id
init|=
name|blob
operator|.
name|getContentIdentity
argument_list|()
decl_stmt|;
name|timeoutMap
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|getText
argument_list|(
name|extractedText
argument_list|)
argument_list|)
expr_stmt|;
name|storeTimeoutMap
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|getText
parameter_list|(
name|ExtractedText
name|text
parameter_list|)
block|{
switch|switch
condition|(
name|text
operator|.
name|getExtractionResult
argument_list|()
condition|)
block|{
case|case
name|SUCCESS
case|:
return|return
name|text
operator|.
name|getExtractedText
argument_list|()
operator|.
name|toString
argument_list|()
return|;
case|case
name|ERROR
case|:
return|return
name|LuceneIndexEditor
operator|.
name|TEXT_EXTRACTION_ERROR
return|;
case|case
name|EMPTY
case|:
return|return
name|EMPTY_STRING
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|addStats
parameter_list|(
name|int
name|count
parameter_list|,
name|long
name|timeInMillis
parameter_list|,
name|long
name|bytesRead
parameter_list|,
name|long
name|textLength
parameter_list|)
block|{
name|this
operator|.
name|textExtractionCount
operator|+=
name|count
expr_stmt|;
name|this
operator|.
name|totalTime
operator|+=
name|timeInMillis
expr_stmt|;
name|this
operator|.
name|totalBytesRead
operator|+=
name|bytesRead
expr_stmt|;
name|this
operator|.
name|totalTextSize
operator|+=
name|textLength
expr_stmt|;
block|}
specifier|public
name|TextExtractionStatsMBean
name|getStatsMBean
parameter_list|()
block|{
return|return
operator|new
name|TextExtractionStatsMBean
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isPreExtractedTextProviderConfigured
parameter_list|()
block|{
return|return
name|extractedTextProvider
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getTextExtractionCount
parameter_list|()
block|{
return|return
name|textExtractionCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getTotalTime
parameter_list|()
block|{
return|return
name|totalTime
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getPreFetchedCount
parameter_list|()
block|{
return|return
name|preFetchedCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getExtractedTextSize
parameter_list|()
block|{
return|return
name|IOUtils
operator|.
name|humanReadableByteCount
argument_list|(
name|totalTextSize
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getBytesRead
parameter_list|()
block|{
return|return
name|IOUtils
operator|.
name|humanReadableByteCount
argument_list|(
name|totalBytesRead
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAlwaysUsePreExtractedCache
parameter_list|()
block|{
return|return
name|alwaysUsePreExtractedCache
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getTimeoutCount
parameter_list|()
block|{
return|return
name|timeoutCount
return|;
block|}
block|}
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|CacheStats
name|getCacheStats
parameter_list|()
block|{
return|return
name|cacheStats
return|;
block|}
specifier|public
name|void
name|setExtractedTextProvider
parameter_list|(
name|PreExtractedTextProvider
name|extractedTextProvider
parameter_list|)
block|{
name|this
operator|.
name|extractedTextProvider
operator|=
name|extractedTextProvider
expr_stmt|;
block|}
specifier|public
name|PreExtractedTextProvider
name|getExtractedTextProvider
parameter_list|()
block|{
return|return
name|extractedTextProvider
return|;
block|}
name|void
name|resetCache
parameter_list|()
block|{
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
name|cache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
block|}
name|boolean
name|isAlwaysUsePreExtractedCache
parameter_list|()
block|{
return|return
name|alwaysUsePreExtractedCache
return|;
block|}
comment|//Taken from DocumentNodeStore and cache packages as they are private
specifier|private
specifier|static
class|class
name|EmpiricalWeigher
implements|implements
name|Weigher
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|EmpiricalWeigher
name|INSTANCE
init|=
operator|new
name|EmpiricalWeigher
argument_list|()
decl_stmt|;
specifier|private
name|EmpiricalWeigher
parameter_list|()
block|{         }
specifier|private
specifier|static
name|long
name|getMemory
parameter_list|(
annotation|@
name|Nonnull
name|String
name|s
parameter_list|)
block|{
return|return
literal|16
comment|// shallow size
operator|+
literal|40
operator|+
operator|(
name|long
operator|)
name|s
operator|.
name|length
argument_list|()
operator|*
literal|2
return|;
comment|// value
block|}
annotation|@
name|Override
specifier|public
name|int
name|weigh
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|long
name|size
init|=
literal|168
decl_stmt|;
comment|// overhead for each cache entry
name|size
operator|+=
name|getMemory
argument_list|(
name|key
argument_list|)
expr_stmt|;
comment|// key
name|size
operator|+=
name|getMemory
argument_list|(
name|value
argument_list|)
expr_stmt|;
comment|// value
if|if
condition|(
name|size
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Calculated weight larger than Integer.MAX_VALUE: {}."
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|size
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
return|return
operator|(
name|int
operator|)
name|size
return|;
block|}
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|resetCache
argument_list|()
expr_stmt|;
comment|// don't clean the persistent map on purpose, so we don't re-try
comment|// after restarting the service or so
name|closeExecutorService
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|process
parameter_list|(
name|String
name|name
parameter_list|,
name|Callable
argument_list|<
name|Void
argument_list|>
name|callable
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|Throwable
block|{
name|Callable
argument_list|<
name|Void
argument_list|>
name|callable2
init|=
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|t
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
name|String
name|oldThreadName
init|=
name|t
operator|.
name|getName
argument_list|()
decl_stmt|;
name|t
operator|.
name|setName
argument_list|(
name|oldThreadName
operator|+
literal|": "
operator|+
name|name
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|callable
operator|.
name|call
argument_list|()
return|;
block|}
finally|finally
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setName
argument_list|(
name|oldThreadName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
try|try
block|{
if|if
condition|(
name|EXTRACT_IN_CALLER_THREAD
condition|)
block|{
name|callable2
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Future
argument_list|<
name|Void
argument_list|>
name|future
init|=
name|getExecutor
argument_list|()
operator|.
name|submit
argument_list|(
name|callable2
argument_list|)
decl_stmt|;
name|future
operator|.
name|get
argument_list|(
name|extractionTimeoutMillis
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
name|timeoutCount
operator|++
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
block|}
specifier|public
name|void
name|setExtractionTimeoutMillis
parameter_list|(
name|int
name|extractionTimeoutMillis
parameter_list|)
block|{
name|this
operator|.
name|extractionTimeoutMillis
operator|=
name|extractionTimeoutMillis
expr_stmt|;
block|}
specifier|private
name|ExecutorService
name|getExecutor
parameter_list|()
block|{
if|if
condition|(
name|executorService
operator|==
literal|null
condition|)
block|{
name|createExecutor
argument_list|()
expr_stmt|;
block|}
return|return
name|executorService
return|;
block|}
specifier|private
specifier|synchronized
name|void
name|createExecutor
parameter_list|()
block|{
if|if
condition|(
name|executorService
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"ExtractedTextCache createExecutor "
operator|+
name|this
argument_list|)
expr_stmt|;
name|ThreadPoolExecutor
name|executor
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|1
argument_list|,
name|EXTRACTION_MAX_THREADS
argument_list|,
literal|60L
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|private
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Thread
operator|.
name|UncaughtExceptionHandler
name|handler
init|=
operator|new
name|Thread
operator|.
name|UncaughtExceptionHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|uncaughtException
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error occurred in asynchronous processing "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
annotation|@
name|Nonnull
name|Runnable
name|r
parameter_list|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|r
argument_list|,
name|createName
argument_list|()
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setPriority
argument_list|(
name|Thread
operator|.
name|MIN_PRIORITY
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setUncaughtExceptionHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
specifier|private
name|String
name|createName
parameter_list|()
block|{
name|int
name|index
init|=
name|counter
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
return|return
literal|"oak binary text extractor"
operator|+
operator|(
name|index
operator|==
literal|0
condition|?
literal|""
else|:
literal|" "
operator|+
name|index
operator|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|executor
operator|.
name|setKeepAliveTime
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|executor
operator|.
name|allowCoreThreadTimeOut
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|executorService
operator|=
name|executor
expr_stmt|;
block|}
specifier|private
specifier|synchronized
name|void
name|closeExecutorService
parameter_list|()
block|{
if|if
condition|(
name|executorService
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"ExtractedTextCache closeExecutorService "
operator|+
name|this
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|executorService
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|loadTimeoutMap
parameter_list|()
block|{
if|if
condition|(
name|indexDir
operator|==
literal|null
operator|||
operator|!
name|indexDir
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return;
block|}
try|try
init|(
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|indexDir
argument_list|,
name|TIMEOUT_MAP
argument_list|)
argument_list|)
init|)
block|{
name|Properties
name|prop
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|prop
operator|.
name|load
argument_list|(
name|in
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|prop
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|timeoutMap
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Could not load timeout map {} from {}"
argument_list|,
name|TIMEOUT_MAP
argument_list|,
name|indexDir
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|storeTimeoutMap
parameter_list|()
block|{
if|if
condition|(
name|indexDir
operator|==
literal|null
operator|||
operator|!
name|indexDir
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return;
block|}
try|try
init|(
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|indexDir
argument_list|,
name|TIMEOUT_MAP
argument_list|)
argument_list|)
init|)
block|{
name|Properties
name|prop
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|prop
operator|.
name|putAll
argument_list|(
name|timeoutMap
argument_list|)
expr_stmt|;
name|prop
operator|.
name|store
argument_list|(
name|out
argument_list|,
literal|"Text extraction timed out for the following binaries, and will not be retried"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Could not store timeout map {} from {}"
argument_list|,
name|TIMEOUT_MAP
argument_list|,
name|indexDir
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

