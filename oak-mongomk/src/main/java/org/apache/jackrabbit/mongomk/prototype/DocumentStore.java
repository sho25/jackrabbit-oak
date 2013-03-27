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
operator|.
name|prototype
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
name|NODES
block|}
comment|/**      * Get a document. The returned map is a clone (the caller      * can modify it without affecting the stored version).      *      * @param collection the collection      * @param key the key      * @return the map, or null if not found      */
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
name|void
name|dispose
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

