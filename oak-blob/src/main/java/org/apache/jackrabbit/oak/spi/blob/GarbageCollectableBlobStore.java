begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|spi
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
name|IOException
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

begin_comment
comment|/**  * A blob store that support garbage collection.  */
end_comment

begin_interface
specifier|public
interface|interface
name|GarbageCollectableBlobStore
extends|extends
name|BlobStore
block|{
comment|/**      * Set the block size used by this blob store, if the blob store splits      * binaries into blocks. If not, this setting is ignored.      *       * @param x the block size in bytes.      */
name|void
name|setBlockSize
parameter_list|(
name|int
name|x
parameter_list|)
function_decl|;
comment|/**      * Write a blob from a temporary file. The temporary file is removed      * afterwards. A file based blob stores might simply rename the file, so      * that no additional writes are necessary.      *      * @param tempFileName the temporary file name      * @return the blob id      */
name|String
name|writeBlob
parameter_list|(
name|String
name|tempFileName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Remove all unused blocks.      *       * @return the number of removed blocks      */
name|int
name|sweep
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Start the mark phase.      */
name|void
name|startMark
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Clear all objects marked as "transiently in use".      */
name|void
name|clearInUse
parameter_list|()
function_decl|;
comment|/**      * Clear the cache.      */
name|void
name|clearCache
parameter_list|()
function_decl|;
comment|/**      * Get the minimum block size (if there is any).      *       * @return the block size      */
name|long
name|getBlockSizeMin
parameter_list|()
function_decl|;
comment|/**      * Gets all the identifiers.      *       * @param maxLastModifiedTime      *            the max last modified time to consider for retrieval      * @return the identifiers      * @throws Exception      *             the exception      */
name|Iterator
argument_list|<
name|String
argument_list|>
name|getAllChunkIds
parameter_list|(
name|long
name|maxLastModifiedTime
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Deletes the blobs with the given ids.      *      * @param chunkIds the chunk ids      * @param maxLastModifiedTime the max last modified time to consider for retrieval      * @return true, if successful      * @throws Exception the exception      */
name|boolean
name|deleteChunks
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|chunkIds
parameter_list|,
name|long
name|maxLastModifiedTime
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Resolve chunks from the given Id.      *       * @param blobId the blob id      * @return the iterator      * @throws IOException      *             Signals that an I/O exception has occurred.      */
name|Iterator
argument_list|<
name|String
argument_list|>
name|resolveChunks
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

