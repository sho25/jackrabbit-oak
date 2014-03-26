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
name|io
operator|.
name|InputStream
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

begin_comment
comment|/**  * An interface to store and read large binary objects.  */
end_comment

begin_interface
specifier|public
interface|interface
name|BlobStore
block|{
comment|/**      * Write a blob from an input stream.      * This method closes the input stream.      *      * @param in the input stream      * @return the blob id      */
name|String
name|writeBlob
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Read a number of bytes from a blob.      *       * @param blobId the blob id      * @param pos the position within the blob      * @param buff the target byte array      * @param off the offset within the target array      * @param length the number of bytes to read      * @return the number of bytes read      */
name|int
name|readBlob
parameter_list|(
name|String
name|blobId
parameter_list|,
name|long
name|pos
parameter_list|,
name|byte
index|[]
name|buff
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Get the length of the blob.      *       * @param blobId the blob id      * @return the length      */
name|long
name|getBlobLength
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Returns a new stream for given blobId. The streams returned from      * multiple calls to this method are byte wise equals. That is,      * subsequent calls to {@link java.io.InputStream#read() read}      * return the same sequence of bytes as long as neither call throws      * an exception.      *      * @param blobId the blob id      * @return a new stream for given blobId      */
name|InputStream
name|getInputStream
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Returns the blobId that referred by the given binary reference.      * Returns {@code null} if the reference is invalid, for example if it      * points to a blob that does not exist.      *      * @param reference binary reference      * @return matching blobId, or {@code null}      */
annotation|@
name|CheckForNull
name|String
name|getBlobId
parameter_list|(
name|String
name|reference
parameter_list|)
function_decl|;
comment|/**      * Returns a secure reference to blob referred by blobid, or {@code null} if no such      * reference is available.      *      * @param blobId blobId referring the blob for which reference is required      * @return binary reference, or {@code null}      */
annotation|@
name|CheckForNull
name|String
name|getReference
parameter_list|(
name|String
name|blobId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

