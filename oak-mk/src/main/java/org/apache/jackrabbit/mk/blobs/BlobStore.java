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
name|mk
operator|.
name|blobs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
comment|/**      * Write a blob from a temporary file. The temporary file is removed      * afterwards. A file based blob stores might simply rename the file, so      * that no additional writes are necessary.      *      * @param tempFilePath the temporary file      * @return the blob id      */
name|String
name|addBlob
parameter_list|(
name|String
name|tempFilePath
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Write a blob from an input stream.      * This method closes the input stream.      *      * @param in the input stream      * @return the blob id      */
name|String
name|writeBlob
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|Exception
function_decl|;
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
name|Exception
function_decl|;
name|long
name|getBlobLength
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

