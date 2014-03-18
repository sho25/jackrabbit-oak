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
name|mk
operator|.
name|api
operator|.
name|MicroKernelException
import|;
end_import

begin_comment
comment|/**  * The interface for the backend storage for documents.  */
end_comment

begin_interface
specifier|public
interface|interface
name|DocumentStore
block|{
comment|/**      * Get the document with the given {@code key}. This is a convenience method      * and equivalent to {@link #find(Collection, String, int)} with a      * {@code maxCacheAge} of {@code Integer.MAX_VALUE}.      *<p>      * The returned document is immutable.      *      * @param<T> the document type      * @param collection the collection      * @param key the key      * @return the document, or null if not found      */
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
function_decl|;
comment|/**      * Get the document with the {@code key}. The implementation may serve the      * document from a cache, but the cached document must not be older than      * the given {@code maxCacheAge} in milliseconds. An implementation must      * invalidate a cached document when it detects it is outdated. That is, a      * subsequent call to {@link #find(Collection, String)} must return the      * newer version of the document.      *<p>      * The returned document is immutable.      *      * @param<T> the document type      * @param collection the collection      * @param key the key      * @param maxCacheAge the maximum age of the cached document (in ms)      * @return the document, or null if not found      */
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
function_decl|;
comment|/**      * Get a list of documents where the key is greater than a start value and      * less than an end value, sorted by the key.      *<p>      * The returned documents are immutable.      *      * @param<T> the document type      * @param collection the collection      * @param fromKey the start value (excluding)      * @param toKey the end value (excluding)      * @param limit the maximum number of entries to return (starting with the lowest key)      * @return the list (possibly empty)      */
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
function_decl|;
comment|/**      * Get a list of documents where the key is greater than a start value and      * less than an end value. The returned documents are immutable.      *      * @param<T> the document type      * @param collection the collection      * @param fromKey the start value (excluding)      * @param toKey the end value (excluding)      * @param indexedProperty the name of the indexed property (optional)      * @param startValue the minimum value of the indexed property      * @param limit the maximum number of entries to return      * @return the list (possibly empty)      */
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
function_decl|;
comment|/**      * Remove a document.      *      * @param<T> the document type      * @param collection the collection      * @param key the key      */
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
function_decl|;
comment|/**      * Try to create a list of documents.      *      * @param<T> the document type      * @param collection the collection      * @param updateOps the list of documents to add      * @return true if this worked (if none of the documents already existed)      */
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
function_decl|;
comment|/**      * Update documents with the given keys. Only existing documents are      * updated.      *      * @param<T> the document type.      * @param collection the collection.      * @param keys the keys of the documents to update.      * @param updateOp the update operation to apply to each of the documents.      */
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
function_decl|;
comment|/**      * Create or update a document. For MongoDB, this is using "findAndModify" with      * the "upsert" flag (insert or update). The returned document is immutable.      *      * @param<T> the document type      * @param collection the collection      * @param update the update operation      * @return the old document or<code>null</code> if it didn't exist before.      * @throws MicroKernelException if the operation failed.      */
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
name|MicroKernelException
function_decl|;
comment|/**      * Performs a conditional update (e.g. using      * {@link UpdateOp.Operation.Type#CONTAINS_MAP_ENTRY} and only updates the      * document if the condition is<code>true</code>. The returned document is      * immutable.      *      * @param<T> the document type      * @param collection the collection      * @param update the update operation with the condition      * @return the old document or<code>null</code> if the condition is not met or      *         if the document wasn't found      * @throws MicroKernelException if the operation failed.      */
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
name|MicroKernelException
function_decl|;
comment|/**      * Invalidate the document cache.      */
name|void
name|invalidateCache
parameter_list|()
function_decl|;
comment|/**      * Invalidate the document cache for the given key.      *      * @param<T> the document type      * @param collection the collection      * @param key the key      */
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
comment|/**      * Fetches the cached document. If document is not present in cache<code>null</code> would be returned      *      * @param<T> the document type      * @param collection the collection      * @param key the key      * @return cached document if present. Otherwise null      */
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
block|}
end_interface

end_unit

