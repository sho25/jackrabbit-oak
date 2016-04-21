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
name|cache
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
name|locks
operator|.
name|Lock
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
name|Nonnegative
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
name|collect
operator|.
name|Iterables
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
name|cache
operator|.
name|CacheValue
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
name|Document
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
name|NodeDocument
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
name|locks
operator|.
name|NodeDocumentLocks
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
name|StringValue
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
name|Objects
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
name|document
operator|.
name|util
operator|.
name|Utils
operator|.
name|isLeafPreviousDocId
import|;
end_import

begin_comment
comment|/**  * Cache for the NodeDocuments. This class is thread-safe and uses the provided NodeDocumentLock.  */
end_comment

begin_class
specifier|public
class|class
name|NodeDocumentCache
implements|implements
name|Closeable
block|{
specifier|private
specifier|final
name|Cache
argument_list|<
name|CacheValue
argument_list|,
name|NodeDocument
argument_list|>
name|nodeDocumentsCache
decl_stmt|;
specifier|private
specifier|final
name|CacheStats
name|nodeDocumentsCacheStats
decl_stmt|;
comment|/**      * The previous documents cache      *      * Key: StringValue, value: NodeDocument      */
specifier|private
specifier|final
name|Cache
argument_list|<
name|StringValue
argument_list|,
name|NodeDocument
argument_list|>
name|prevDocumentsCache
decl_stmt|;
specifier|private
specifier|final
name|CacheStats
name|prevDocumentsCacheStats
decl_stmt|;
specifier|private
specifier|final
name|NodeDocumentLocks
name|locks
decl_stmt|;
specifier|public
name|NodeDocumentCache
parameter_list|(
annotation|@
name|Nonnull
name|Cache
argument_list|<
name|CacheValue
argument_list|,
name|NodeDocument
argument_list|>
name|nodeDocumentsCache
parameter_list|,
annotation|@
name|Nonnull
name|CacheStats
name|nodeDocumentsCacheStats
parameter_list|,
annotation|@
name|Nonnull
name|Cache
argument_list|<
name|StringValue
argument_list|,
name|NodeDocument
argument_list|>
name|prevDocumentsCache
parameter_list|,
annotation|@
name|Nonnull
name|CacheStats
name|prevDocumentsCacheStats
parameter_list|,
annotation|@
name|Nonnull
name|NodeDocumentLocks
name|locks
parameter_list|)
block|{
name|this
operator|.
name|nodeDocumentsCache
operator|=
name|nodeDocumentsCache
expr_stmt|;
name|this
operator|.
name|nodeDocumentsCacheStats
operator|=
name|nodeDocumentsCacheStats
expr_stmt|;
name|this
operator|.
name|prevDocumentsCache
operator|=
name|prevDocumentsCache
expr_stmt|;
name|this
operator|.
name|prevDocumentsCacheStats
operator|=
name|prevDocumentsCacheStats
expr_stmt|;
name|this
operator|.
name|locks
operator|=
name|locks
expr_stmt|;
block|}
comment|/**      * Invalidate document with given key.      *      * @param key to invalidate      */
specifier|public
name|void
name|invalidate
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|)
block|{
name|Lock
name|lock
init|=
name|locks
operator|.
name|acquire
argument_list|(
name|key
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|isLeafPreviousDocId
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|prevDocumentsCache
operator|.
name|invalidate
argument_list|(
operator|new
name|StringValue
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nodeDocumentsCache
operator|.
name|invalidate
argument_list|(
operator|new
name|StringValue
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Invalidate document with given keys iff their mod counts are different as      * passed in the map.      *      * @param modCounts map where key is the document id and the value is the mod count      * @return number of invalidated entries      */
annotation|@
name|Nonnegative
specifier|public
name|int
name|invalidateOutdated
parameter_list|(
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|modCounts
parameter_list|)
block|{
name|int
name|invalidatedCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|e
range|:
name|modCounts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|id
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Long
name|modCount
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|NodeDocument
name|doc
init|=
name|getIfPresent
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|Objects
operator|.
name|equal
argument_list|(
name|modCount
argument_list|,
name|doc
operator|.
name|getModCount
argument_list|()
argument_list|)
condition|)
block|{
name|invalidate
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|invalidatedCount
operator|++
expr_stmt|;
block|}
block|}
return|return
name|invalidatedCount
return|;
block|}
comment|/**      * Return the cached value or null.      *      * @param key document key      * @return cached value of null if there's no document with given key cached      */
annotation|@
name|CheckForNull
specifier|public
name|NodeDocument
name|getIfPresent
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|isLeafPreviousDocId
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
name|prevDocumentsCache
operator|.
name|getIfPresent
argument_list|(
operator|new
name|StringValue
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|nodeDocumentsCache
operator|.
name|getIfPresent
argument_list|(
operator|new
name|StringValue
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**      * Return the document matching given key, optionally loading it from an      * external source.      *<p>      * This method can modify the cache, so it's synchronized. The {@link #getIfPresent(String)}      * is not synchronized and will be faster if you need to get the cached value      * outside the critical section.      *      * @see Cache#get(Object, Callable)      * @param key document key      * @param valueLoader object used to retrieve the document      * @return document matching given key      */
annotation|@
name|Nonnull
specifier|public
name|NodeDocument
name|get
parameter_list|(
annotation|@
name|Nonnull
name|String
name|key
parameter_list|,
annotation|@
name|Nonnull
name|Callable
argument_list|<
name|NodeDocument
argument_list|>
name|valueLoader
parameter_list|)
throws|throws
name|ExecutionException
block|{
name|Lock
name|lock
init|=
name|locks
operator|.
name|acquire
argument_list|(
name|key
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|isLeafPreviousDocId
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
name|prevDocumentsCache
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
name|key
argument_list|)
argument_list|,
name|valueLoader
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|nodeDocumentsCache
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
name|key
argument_list|)
argument_list|,
name|valueLoader
argument_list|)
return|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Puts document into cache.      *      * @param doc document to put      */
specifier|public
name|void
name|put
parameter_list|(
annotation|@
name|Nonnull
name|NodeDocument
name|doc
parameter_list|)
block|{
if|if
condition|(
name|doc
operator|!=
name|NodeDocument
operator|.
name|NULL
condition|)
block|{
name|Lock
name|lock
init|=
name|locks
operator|.
name|acquire
argument_list|(
name|doc
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|putInternal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Puts document into cache iff no entry with the given key is cached      * already or the cached document is older (has smaller {@link Document#MOD_COUNT}).      *      * @param doc the document to add to the cache      * @return either the given<code>doc</code> or the document already present      *         in the cache if it's newer      */
annotation|@
name|Nonnull
specifier|public
name|NodeDocument
name|putIfNewer
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|NodeDocument
name|doc
parameter_list|)
block|{
if|if
condition|(
name|doc
operator|==
name|NodeDocument
operator|.
name|NULL
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"doc must not be NULL document"
argument_list|)
throw|;
block|}
name|doc
operator|.
name|seal
argument_list|()
expr_stmt|;
name|NodeDocument
name|newerDoc
decl_stmt|;
name|String
name|id
init|=
name|doc
operator|.
name|getId
argument_list|()
decl_stmt|;
name|Lock
name|lock
init|=
name|locks
operator|.
name|acquire
argument_list|(
name|id
argument_list|)
decl_stmt|;
try|try
block|{
name|NodeDocument
name|cachedDoc
init|=
name|getIfPresent
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|cachedDoc
operator|==
literal|null
operator|||
name|cachedDoc
operator|==
name|NodeDocument
operator|.
name|NULL
condition|)
block|{
name|newerDoc
operator|=
name|doc
expr_stmt|;
name|putInternal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Long
name|cachedModCount
init|=
name|cachedDoc
operator|.
name|getModCount
argument_list|()
decl_stmt|;
name|Long
name|modCount
init|=
name|doc
operator|.
name|getModCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|cachedModCount
operator|==
literal|null
operator|||
name|modCount
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Missing "
operator|+
name|Document
operator|.
name|MOD_COUNT
argument_list|)
throw|;
block|}
if|if
condition|(
name|modCount
operator|>
name|cachedModCount
condition|)
block|{
name|newerDoc
operator|=
name|doc
expr_stmt|;
name|putInternal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newerDoc
operator|=
name|cachedDoc
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
name|newerDoc
return|;
block|}
comment|/**      * Puts document into cache iff no entry with the given key is cached      * already. This operation is atomic.      *      * @param doc the document to add to the cache.      * @return either the given<code>doc</code> or the document already present      *         in the cache.      */
annotation|@
name|Nonnull
specifier|public
name|NodeDocument
name|putIfAbsent
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|NodeDocument
name|doc
parameter_list|)
block|{
if|if
condition|(
name|doc
operator|==
name|NodeDocument
operator|.
name|NULL
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"doc must not be NULL document"
argument_list|)
throw|;
block|}
name|doc
operator|.
name|seal
argument_list|()
expr_stmt|;
name|String
name|id
init|=
name|doc
operator|.
name|getId
argument_list|()
decl_stmt|;
comment|// make sure we only cache the document if it wasn't
comment|// changed and cached by some other thread in the
comment|// meantime. That is, use get() with a Callable,
comment|// which is only used when the document isn't there
name|Lock
name|lock
init|=
name|locks
operator|.
name|acquire
argument_list|(
name|id
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|NodeDocument
name|cached
init|=
name|get
argument_list|(
name|id
argument_list|,
operator|new
name|Callable
argument_list|<
name|NodeDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeDocument
name|call
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|cached
operator|!=
name|NodeDocument
operator|.
name|NULL
condition|)
block|{
return|return
name|cached
return|;
block|}
else|else
block|{
name|invalidate
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
comment|// will never happen because call() just returns
comment|// the already available doc
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Replaces the cached value if the old document is currently present in      * the cache. If the {@code oldDoc} is not cached, nothing will happen. If      * {@code oldDoc} does not match the document currently in the cache, then      * the cached document is invalidated.      *      * @param oldDoc the old document      * @param newDoc the replacement      */
specifier|public
name|void
name|replaceCachedDocument
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|NodeDocument
name|oldDoc
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|NodeDocument
name|newDoc
parameter_list|)
block|{
if|if
condition|(
name|newDoc
operator|==
name|NodeDocument
operator|.
name|NULL
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"doc must not be NULL document"
argument_list|)
throw|;
block|}
name|String
name|key
init|=
name|oldDoc
operator|.
name|getId
argument_list|()
decl_stmt|;
name|Lock
name|lock
init|=
name|locks
operator|.
name|acquire
argument_list|(
name|key
argument_list|)
decl_stmt|;
try|try
block|{
name|NodeDocument
name|cached
init|=
name|getIfPresent
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|cached
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|Objects
operator|.
name|equal
argument_list|(
name|cached
operator|.
name|getModCount
argument_list|()
argument_list|,
name|oldDoc
operator|.
name|getModCount
argument_list|()
argument_list|)
condition|)
block|{
name|putInternal
argument_list|(
name|newDoc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// the cache entry was modified by some other thread in
comment|// the meantime. the updated cache entry may or may not
comment|// include this update. we cannot just apply our update
comment|// on top of the cached entry.
comment|// therefore we must invalidate the cache entry
name|invalidate
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return keys stored in cache      */
specifier|public
name|Iterable
argument_list|<
name|CacheValue
argument_list|>
name|keys
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|concat
argument_list|(
name|nodeDocumentsCache
operator|.
name|asMap
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|,
name|prevDocumentsCache
operator|.
name|asMap
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @return values stored in cache      */
specifier|public
name|Iterable
argument_list|<
name|NodeDocument
argument_list|>
name|values
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|concat
argument_list|(
name|nodeDocumentsCache
operator|.
name|asMap
argument_list|()
operator|.
name|values
argument_list|()
argument_list|,
name|prevDocumentsCache
operator|.
name|asMap
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|CacheStats
argument_list|>
name|getCacheStats
parameter_list|()
block|{
return|return
name|Lists
operator|.
name|newArrayList
argument_list|(
name|nodeDocumentsCacheStats
argument_list|,
name|prevDocumentsCacheStats
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
name|IOException
block|{
if|if
condition|(
name|prevDocumentsCache
operator|instanceof
name|Closeable
condition|)
block|{
operator|(
operator|(
name|Closeable
operator|)
name|prevDocumentsCache
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|nodeDocumentsCache
operator|instanceof
name|Closeable
condition|)
block|{
operator|(
operator|(
name|Closeable
operator|)
name|nodeDocumentsCache
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|//----------------------------< internal>----------------------------------
comment|/**      * Puts a document into the cache without acquiring a lock.      *      * @param doc the document to put into the cache.      */
specifier|protected
specifier|final
name|void
name|putInternal
parameter_list|(
annotation|@
name|Nonnull
name|NodeDocument
name|doc
parameter_list|)
block|{
if|if
condition|(
name|isLeafPreviousDocId
argument_list|(
name|doc
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|prevDocumentsCache
operator|.
name|put
argument_list|(
operator|new
name|StringValue
argument_list|(
name|doc
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nodeDocumentsCache
operator|.
name|put
argument_list|(
operator|new
name|StringValue
argument_list|(
name|doc
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

