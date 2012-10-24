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
name|api
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

begin_comment
comment|/**  * BlobFactory...  * TODO review again if we really need/want to expose that in the OAK API  * TODO in particular exposing this interface (and Blob) requires additional thoughts on  * TODO - lifecycle of the factory,  * TODO - lifecycle of the Blob,  * TODO - access restrictions and how permissions are enforced on blob creation  * TODO - searchability, versioning and so forth  */
end_comment

begin_interface
specifier|public
interface|interface
name|BlobFactory
block|{
comment|/**      * Create a {@link Blob} from the given input stream. The input stream      * is closed after this method returns.      * @param inputStream  The input stream for the {@code Blob}      * @return  The {@code Blob} representing {@code inputStream}      * @throws java.io.IOException  If an error occurs while reading from the stream      */
name|Blob
name|createBlob
parameter_list|(
name|InputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

