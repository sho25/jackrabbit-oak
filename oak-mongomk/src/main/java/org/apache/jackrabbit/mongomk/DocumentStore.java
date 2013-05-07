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
name|mongomk
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
comment|/**      * The list of collections.      */
enum|enum
name|Collection
block|{
comment|/**          * The 'nodes' collection. It contains all the node data, with one document          * per node, and the path as the primary key. Each document possibly          * contains multiple revisions.          *<p>          * Key: the path, value: the node data (possibly multiple revisions)          *<p>          * Old revisions are removed after some time, either by the process that          * removed or updated the node, lazily when reading, or in a background          * process.          */
name|NODES
argument_list|(
literal|"nodes"
argument_list|)
block|,
comment|/**          * The 'clusterNodes' collection contains the list of currently running          * cluster nodes. The key is the clusterNodeId (0, 1, 2,...).          */
name|CLUSTER_NODES
argument_list|(
literal|"clusterNodes"
argument_list|)
block|;
specifier|final
name|String
name|name
decl_stmt|;
name|Collection
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
comment|/**      * Get a document.      *<p>      * The returned map is a clone (the caller can modify it without affecting      * the stored version).      *       * @param collection the collection      * @param key the key      * @return the map, or null if not found      */
annotation|@
name|CheckForNull
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|find
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
function_decl|;
comment|/**      * Get a document, ignoring the cache if the cached entry is older than the      * specified time.      *<p>      * The returned map is a clone (the caller can modify it without affecting      * the stored version).      *       * @param collection the collection      * @param key the key      * @param maxCacheAge the maximum age of the cached document      * @return the map, or null if not found      */
annotation|@
name|CheckForNull
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|find
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|String
name|key
parameter_list|,
name|int
name|maxCacheAge
parameter_list|)
function_decl|;
comment|/**      * Get a list of documents where the key is greater than a start value and      * less than an end value.      *       * @param collection the collection      * @param fromKey the start value (excluding)      * @param toKey the end value (excluding)      * @param limit the maximum number of entries to return      * @return the list (possibly empty)      */
annotation|@
name|Nonnull
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|query
parameter_list|(
name|Collection
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
comment|/**      * Remove a document.      *      * @param collection the collection      * @param key the key      */
name|void
name|remove
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
function_decl|;
comment|/**      * Try to create a list of documents.      *       * @param collection the collection      * @param updateOps the list of documents to add      * @return true if this worked (if none of the documents already existed)      */
name|boolean
name|create
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updateOps
parameter_list|)
function_decl|;
comment|/**      * Create or update a document. For MongoDb, this is using "findAndModify" with      * the "upsert" flag (insert or update).      *      * @param collection the collection      * @param update the update operation      * @return the old document      * @throws MicroKernelException if the operation failed.      */
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|createOrUpdate
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|UpdateOp
name|update
parameter_list|)
throws|throws
name|MicroKernelException
function_decl|;
comment|/**      * Performs a conditional update (e.g. using      * {@link UpdateOp.Operation.Type#CONTAINS_MAP_ENTRY} and only updates the      * document if the condition is<code>true</code>.      *      * @param collection the collection      * @param update the update operation with the condition      * @return the old document or<code>null</code> if the condition is not met.      * @throws MicroKernelException if the operation failed.      */
annotation|@
name|CheckForNull
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|findAndUpdate
parameter_list|(
name|Collection
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
comment|/**      * Invalidate the document cache for the given key.      *       * @param collection the collection      * @param key the key      */
name|void
name|invalidateCache
parameter_list|(
name|Collection
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
comment|/**      * Check whether the given document is in the cache.      *       * @param collection the collection      * @param key the key      * @return true if yes      */
name|boolean
name|isCached
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

