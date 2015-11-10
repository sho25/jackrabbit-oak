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
name|IOException
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
class|class
name|ExtractedTextCache
block|{
specifier|private
specifier|static
specifier|final
name|String
name|EMPTY_STRING
init|=
literal|""
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
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
name|CacheStats
name|cacheStats
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
if|if
condition|(
name|reindexMode
operator|&&
name|extractedTextProvider
operator|!=
literal|null
condition|)
block|{
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
name|result
operator|=
name|text
operator|.
name|getExtractedText
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
break|break;
case|case
name|ERROR
case|:
name|result
operator|=
name|LuceneIndexEditor
operator|.
name|TEXT_EXTRACTION_ERROR
expr_stmt|;
break|break;
case|case
name|EMPTY
case|:
name|result
operator|=
name|EMPTY_STRING
expr_stmt|;
break|break;
block|}
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
name|extractedText
operator|.
name|getExtractionResult
argument_list|()
operator|==
name|ExtractedText
operator|.
name|ExtractionResult
operator|.
name|SUCCESS
operator|&&
name|cache
operator|!=
literal|null
operator|&&
name|id
operator|!=
literal|null
condition|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|extractedText
operator|.
name|getExtractedText
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|int
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
name|int
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
return|return
name|size
return|;
block|}
block|}
block|}
end_class

end_unit

