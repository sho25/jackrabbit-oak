begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|api
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|MicroKernel
import|;
end_import

begin_comment
comment|/**  * The<code>BlobStore</code> interface deals with all blob related operations of the {@link MicroKernel}.  *  *<p>  * Since binary storage and node storage most likely use different backend technologies two separate interfaces for  * these operations are provided.  *</p>  *  *<p>  * This interface is not only a partly {@code MicroKernel} but also provides a different layer of abstraction by  * converting the {@link String} parameters into higher level objects to ease the development for implementors of the  * {@code MicroKernel}.  *</p>  *  * @see NodeStore  *  * @author<a href="mailto:pmarx@adobe.com>Philipp Marx</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|BlobStore
block|{
comment|/**      * @see MicroKernel#getLength(String)      *      * @param blobId The id of the blob.      * @return The length in bytes.      * @throws Exception If an error occurred while getting the blob lenght.      */
name|long
name|getBlobLength
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * @see MicroKernel#read(String, long, byte[], int, int)      *      * @param blobId The id of the blob.      * @param blobOffset The offset to read from.      * @param buffer The buffer to read the binary data into.      * @param bufferOffset The offset to read into the buffer.      * @param length The length of the data to read.      * @return The actual number of bytes which were read.      * @throws Exception If an error occurred while reading the blob data.      */
name|int
name|readBlob
parameter_list|(
name|String
name|blobId
parameter_list|,
name|long
name|blobOffset
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|bufferOffset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * @see MicroKernel#write(InputStream)      *      * @param is The {@link InputStream} containing the data which should be written.      * @return The id of the blob.      * @throws Exception If an error occurred while writing the data.      */
name|String
name|writeBlob
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

