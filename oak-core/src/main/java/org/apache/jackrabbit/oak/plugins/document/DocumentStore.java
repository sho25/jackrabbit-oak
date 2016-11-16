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
name|plugins
operator|.
name|document
operator|.
name|UpdateOp
operator|.
name|Condition
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
name|cache
operator|.
name|CacheInvalidationStats
import|;
end_import

begin_comment
comment|/**  * The interface for the backend storage for documents.  *<p>  * In general atomicity of operations on a DocumentStore are limited to a single  * document. That is, an implementation does not have to guarantee atomicity of  * the entire effect of a method call. A method that fails with an exception may  * have modified just some documents and then abort. However, an implementation  * must not modify a document partially. Either the complete update operation is  * applied to a document or no modification is done at all.  *<p>  * The key is the id of a document. Keys are opaque strings. All characters are  * allowed. Leading and trailing whitespace is allowed. For keys, the maximum  * length is 512 bytes in the UTF-8 representation (in the latest Unicode  * version).  */
end_comment

begin_interface
specifier|public
interface|interface
name|DocumentStore
block|{
comment|/**      * Get the document with the given {@code key}. This is a convenience method      * and equivalent to {@link #find(Collection, String, int)} with a      * {@code maxCacheAge} of {@code Integer.MAX_VALUE}.      *<p>      * The returned document is immutable.      *      * @param<T> the document type      * @param collection the collection      * @param key the key      * @return the document, or null if not found      * @throws DocumentStoreException if the operation failed. E.g. because of      *          an I/O error.      */
annotation|@
name|CheckForNull
argument_list|<
name|T
extends|extends
name|Document
argument_list|>
name|T
name|find
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
throws|throws
name|DocumentStoreException
function_decl|;
comment|/**      * Get the document with the {@code key}. The implementation may serve the      * document from a cache, but the cached document must not be older than      * the given {@code maxCacheAge} in milliseconds. An implementation must      * invalidate a cached document when it detects it is outdated. That is, a      * subsequent call to {@link #find(Collection, String)} must return the      * newer version of the document.      *<p>      * The returned document is immutable.      *      * @param<T> the document type      * @param collection the collection      * @param key the key      * @param maxCacheAge the maximum age of the cached document (in ms)      * @return the document, or null if not found      * @throws DocumentStoreException if the operation failed. E.g. because of      *          an I/O error.      */
annotation|@
name|CheckForNull
argument_list|<
name|T
extends|extends
name|Document
argument_list|>
name|T
name|find
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|,
name|int
name|maxCacheAge
parameter_list|)
throws|throws
name|DocumentStoreException
function_decl|;
comment|/**      * Get a list of documents where the key is greater than a start value and      * less than an end value.      *<p>      * The returned documents are sorted by key and are immutable.      *      * @param<T> the document type      * @param collection the collection      * @param fromKey the start value (excluding)      * @param toKey the end value (excluding)      * @param limit the maximum number of entries to return (starting with the lowest key)      * @return the list (possibly empty)      * @throws DocumentStoreException if the operation failed. E.g. because of      *          an I/O error.      */
annotation|@
name|Nonnull
argument_list|<
name|T
extends|extends
name|Document
argument_list|>
name|List
argument_list|<
name|T
argument_list|>
name|query
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|fromKey
parameter_list|,
name|String
name|toKey
parameter_list|,
name|int
name|limit
parameter_list|)
throws|throws
name|DocumentStoreException
function_decl|;
comment|/**      * Get a list of documents where the key is greater than a start value and      * less than an end value<em>and</em> the given "indexed property" is greater      * or equals the specified value.      *<p>      * The indexed property can either be a {@link Long} value, in which case numeric      * comparison applies, or a {@link Boolean} value, in which case "false" is mapped      * to "0" and "true" is mapped to "1".      *<p>      * The returned documents are sorted by key and are immutable.      *      * @param<T> the document type      * @param collection the collection      * @param fromKey the start value (excluding)      * @param toKey the end value (excluding)      * @param indexedProperty the name of the indexed property (optional)      * @param startValue the minimum value of the indexed property      * @param limit the maximum number of entries to return      * @return the list (possibly empty)      * @throws DocumentStoreException if the operation failed. E.g. because of      *          an I/O error.      */
annotation|@
name|Nonnull
argument_list|<
name|T
extends|extends
name|Document
argument_list|>
name|List
argument_list|<
name|T
argument_list|>
name|query
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|fromKey
parameter_list|,
name|String
name|toKey
parameter_list|,
name|String
name|indexedProperty
parameter_list|,
name|long
name|startValue
parameter_list|,
name|int
name|limit
parameter_list|)
throws|throws
name|DocumentStoreException
function_decl|;
comment|/**      * Remove a document. This method does nothing if there is no document      * with the given key.      *<p>      * In case of a {@code DocumentStoreException}, the document with the given      * key may or may not have been removed from the store. It is the      * responsibility of the caller to check whether it still exists. The      * implementation however ensures that the result of the operation is      * properly reflected in the document cache. That is, an implementation      * could simply evict the document with the given key.      *      * @param<T> the document type      * @param collection the collection      * @param key the key      * @throws DocumentStoreException if the operation failed. E.g. because of      *          an I/O error.      */
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|remove
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
throws|throws
name|DocumentStoreException
function_decl|;
comment|/**      * Batch remove documents with given keys. Keys for documents that do not      * exist are simply ignored. If this method fails with an exception, then      * only some of the documents identified by {@code keys} may have been      * removed.      *<p>      * In case of a {@code DocumentStoreException}, the documents with the given      * keys may or may not have been removed from the store. It may also be      * possible that only some have been removed from the store. It is the      * responsibility of the caller to check which documents still exist. The      * implementation however ensures that the result of the operation is      * properly reflected in the document cache. That is, an implementation      * could simply evict documents with the given keys from the cache.      *      * @param<T> the document type      * @param collection the collection      * @param keys list of keys      * @throws DocumentStoreException if the operation failed. E.g. because of      *          an I/O error.      */
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|remove
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|)
throws|throws
name|DocumentStoreException
function_decl|;
comment|/**      * Batch remove documents with given keys and corresponding conditions. Keys      * for documents that do not exist are simply ignored. A document is only      * removed if the corresponding conditions are met.      *<p>      * In case of a {@code DocumentStoreException}, the documents with the given      * keys may or may not have been removed from the store. It may also be      * possible that only some have been removed from the store. It is the      * responsibility of the caller to check which documents still exist. The      * implementation however ensures that the result of the operation is      * properly reflected in the document cache. That is, an implementation      * could simply evict documents with the given keys from the cache.      *      * @param<T> the document type      * @param collection the collection.      * @param toRemove the keys of the documents to remove with the      *                 corresponding conditions.      * @return the number of removed documents.      * @throws DocumentStoreException if the operation failed. E.g. because of      *          an I/O error.      */
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|int
name|remove
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|UpdateOp
operator|.
name|Key
argument_list|,
name|UpdateOp
operator|.
name|Condition
argument_list|>
argument_list|>
name|toRemove
parameter_list|)
throws|throws
name|DocumentStoreException
function_decl|;
comment|/**      * Batch remove documents where the given "indexed property" is within the given      * range (inclusive) - {@code [startValue, endValue]}.      *<p>      * The indexed property is a {@link Long} value and numeric comparison applies.      *<p>      * In case of a {@code DocumentStoreException}, the documents with the given      * keys may or may not have been removed from the store. It may also be      * possible that only some have been removed from the store. It is the      * responsibility of the caller to check which documents still exist. The      * implementation however ensures that the result of the operation is      * properly reflected in the document cache. That is, an implementation      * could simply evict documents with the given keys from the cache.      *      * @param<T> the document type      * @param collection the collection.      * @param indexedProperty the name of the indexed property      * @param startValue the minimum value of the indexed property      * @param endValue the maximum value of the indexed property      * @return the number of removed documents.      * @throws DocumentStoreException if the operation failed. E.g. because of      *          an I/O error.      */
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|int
name|remove
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|indexedProperty
parameter_list|,
name|long
name|startValue
parameter_list|,
name|long
name|endValue
parameter_list|)
throws|throws
name|DocumentStoreException
function_decl|;
comment|/**      * Try to create a list of documents. This method returns {@code true} iff      * none of the documents existed before and the create was successful. This      * method will return {@code false} if one of the documents already exists      * in the store. Some documents may still have been created in the store.      * An implementation does not have to guarantee an atomic create of all the      * documents described in the {@code updateOps}. It is the responsibility of      * the caller to check, which documents were created and take appropriate      * action. The same is true when this method throws      * {@code DocumentStoreException} (e.g. when a communication error occurs).      * In this case only some documents may have been created.      *      * @param<T> the document type      * @param collection the collection      * @param updateOps the list of documents to add (where {@link Condition}s are not allowed)      * @return true if this worked (if none of the documents already existed)      * @throws IllegalArgumentException when at least one of the {@linkplain UpdateOp}s is conditional      * @throws DocumentStoreException if the operation failed. E.g. because of      *          an I/O error.      */
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|boolean
name|create
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updateOps
parameter_list|)
throws|throws
name|IllegalArgumentException
throws|,
name|DocumentStoreException
function_decl|;
comment|/**      * Update documents with the given keys. Only existing documents are      * updated and keys for documents that do not exist are simply ignored.      * There is no guarantee in which sequence the updates are performed.      *<p>      * If this method fails with a {@code DocumentStoreException}, then only some      * of the documents identified by {@code keys} may have been updated. The      * implementation however ensures that the result of the operation is      * properly reflected in the document cache. That is, an implementation      * could simply evict documents with the given keys from the cache.      *      * @param<T> the document type.      * @param collection the collection.      * @param keys the keys of the documents to update.      * @param updateOp the update operation to apply to each of the documents      *        (where {@link Condition}s are not allowed)      * @throws IllegalArgumentException when the {@linkplain UpdateOp} is conditional      * @throws DocumentStoreException if the operation failed. E.g. because of      *          an I/O error.      */
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|update
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|,
name|UpdateOp
name|updateOp
parameter_list|)
throws|throws
name|IllegalArgumentException
throws|,
name|DocumentStoreException
function_decl|;
comment|/**      * Atomically checks if the document exists and updates it, otherwise the      * document is created (aka upsert). The returned document is immutable.      *<p>      * If this method fails with a {@code DocumentStoreException}, then the      * document may or may not have been created or updated. It is the      * responsibility of the caller to check the result e.g. by calling      * {@link #find(Collection, String)}. The implementation however ensures      * that the result of the operation is properly reflected in the document      * cache. That is, an implementation could simply evict documents with the      * given keys from the cache.      *      * @param<T> the document type      * @param collection the collection      * @param update the update operation (where {@link Condition}s are not allowed)      * @return the old document or<code>null</code> if it didn't exist before.      * @throws IllegalArgumentException when the {@linkplain UpdateOp} is conditional      * @throws DocumentStoreException if the operation failed. E.g. because of      *          an I/O error.      */
annotation|@
name|CheckForNull
argument_list|<
name|T
extends|extends
name|Document
argument_list|>
name|T
name|createOrUpdate
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|UpdateOp
name|update
parameter_list|)
throws|throws
name|IllegalArgumentException
throws|,
name|DocumentStoreException
function_decl|;
comment|/**      * Create or unconditionally update a number of documents. An implementation      * does not have to guarantee that all changes are applied atomically,      * together.      *<p>      * In case of a {@code DocumentStoreException} (e.g. when a communication      * error occurs) only some changes may have been applied. In this case it is      * the responsibility of the caller to check which {@linkplain UpdateOp}s      * were applied and take appropriate action. The implementation however      * ensures that the result of the operations are properly reflected in the      * document cache. That is, an implementation could simply evict documents      * related to the given update operations from the cache.      *      * @param<T> the document type      * @param collection the collection      * @param updateOps the update operation list      * @return the list containing old documents or<code>null</code> values if they didn't exist      *         before (see {@linkplain #createOrUpdate(Collection, UpdateOp)}), where the order      *         reflects the order in the "updateOps" parameter      * @throws DocumentStoreException if the operation failed. E.g. because of      *          an I/O error.      */
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|createOrUpdate
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updateOps
parameter_list|)
throws|throws
name|DocumentStoreException
function_decl|;
comment|/**      * Performs a conditional update (e.g. using      * {@link UpdateOp.Condition.Type#EXISTS} and only updates the      * document if the condition is<code>true</code>. The returned document is      * immutable.      *<p>      * In case of a {@code DocumentStoreException} (e.g. when a communication      * error occurs) the update may or may not have been applied. In this case      * it is the responsibility of the caller to check whether the update was      * applied and take appropriate action. The implementation however ensures      * that the result of the operation is properly reflected in the document      * cache. That is, an implementation could simply evict the document related      * to the given update operation from the cache.      *      * @param<T> the document type      * @param collection the collection      * @param update the update operation with the condition      * @return the old document or<code>null</code> if the condition is not met or      *         if the document wasn't found      * @throws DocumentStoreException if the operation failed. E.g. because of      *          an I/O error.      */
annotation|@
name|CheckForNull
argument_list|<
name|T
extends|extends
name|Document
argument_list|>
name|T
name|findAndUpdate
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|UpdateOp
name|update
parameter_list|)
throws|throws
name|DocumentStoreException
function_decl|;
comment|/**      * Invalidate the document cache. Calling this method instructs the      * implementation to invalidate each document from the cache, which is not      * up to date with the underlying storage at the time this method is called.      * A document is considered in the cache if {@link #getIfCached(Collection, String)}      * returns a non-null value for a key.      *<p>      * An implementation is allowed to perform lazy invalidation and only check      * whether a document is up-to-date when it is accessed after this method      * is called. However, this also includes a call to {@link #getIfCached(Collection, String)},      * which must only return the document if it was up-to-date at the time      * this method was called. Similarly, a call to {@link #find(Collection, String)}      * must guarantee the returned document reflects all the changes done up to      * when {@code invalidateCache()} was called.      *<p>      * In some implementations this method can be a NOP because documents can      * only be modified through a single instance of a {@code DocumentStore}.      *      * @return cache invalidation statistics or {@code null} if none are      *          available.      */
annotation|@
name|CheckForNull
name|CacheInvalidationStats
name|invalidateCache
parameter_list|()
function_decl|;
comment|/**      * Invalidate the document cache but only with entries that match one      * of the keys provided.      *      * See {@link #invalidateCache()} for the general contract of cache      * invalidation.      *      * @param keys the keys of the documents to invalidate.      * @return cache invalidation statistics or {@code null} if none are      *          available.      */
annotation|@
name|CheckForNull
name|CacheInvalidationStats
name|invalidateCache
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|)
function_decl|;
comment|/**      * Invalidate the document cache for the given key.      *      * See {@link #invalidateCache()} for the general contract of cache      * invalidation.      *      * @param collection the collection      * @param key the key      */
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|invalidateCache
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
function_decl|;
comment|/**      * Dispose this instance.      */
name|void
name|dispose
parameter_list|()
function_decl|;
comment|/**      * Fetches the cached document. If the document is not present in the cache      * {@code null} will be returned. This method is consistent with other find      * methods that may return cached documents and will return {@code null}      * even when the implementation has a negative cache for documents that      * do not exist. This method will never return {@link NodeDocument#NULL}.      *      * @param<T> the document type      * @param collection the collection      * @param key the key      * @return cached document if present. Otherwise {@code null}.      */
annotation|@
name|CheckForNull
argument_list|<
name|T
extends|extends
name|Document
argument_list|>
name|T
name|getIfCached
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
function_decl|;
comment|/**      * Set the level of guarantee for read and write operations, if supported by this backend.      *      * @param readWriteMode the read/write mode      */
name|void
name|setReadWriteMode
parameter_list|(
name|String
name|readWriteMode
parameter_list|)
function_decl|;
comment|/**      * @return status information about the cache      */
annotation|@
name|CheckForNull
name|Iterable
argument_list|<
name|CacheStats
argument_list|>
name|getCacheStats
parameter_list|()
function_decl|;
comment|/**      * @return description of the underlying storage.      */
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getMetadata
parameter_list|()
function_decl|;
comment|/**      * @return the estimated time difference in milliseconds between the local      * instance and the (typically common, shared) document server system. The      * value can be zero if the times are estimated to be equal, positive when      * the local instance is ahead of the remote server and negative when the      * local instance is behind the remote server. An invocation is not cached      * and typically requires a round-trip to the server (but that is not a      * requirement).      * @throws UnsupportedOperationException if this DocumentStore does not      *                                       support this method      * @throws DocumentStoreException if an I/O error occurs.      */
name|long
name|determineServerTimeDifferenceMillis
parameter_list|()
throws|throws
name|UnsupportedOperationException
throws|,
name|DocumentStoreException
function_decl|;
block|}
end_interface

end_unit

