begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|index
operator|.
name|search
operator|.
name|spi
operator|.
name|editor
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

begin_comment
comment|/**  * A {@link FulltextIndexWriter} is responsible for writing / deleting documents of type D to the index  * implementation underlying persistence layer.  *  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|FulltextIndexWriter
parameter_list|<
name|D
parameter_list|>
block|{
comment|/**      * Updates the document having given path      *      * @param path path of the NodeState which the Document represents      * @param doc updated document      */
name|void
name|updateDocument
parameter_list|(
name|String
name|path
parameter_list|,
name|D
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Deletes documents which are same or child of given path      *      * @param path path whose children need to be deleted      */
name|void
name|deleteDocuments
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Closes the underlying resources.      *      * @param timestamp timestamp to be used for recording at status in NodeBuilder      * @return true if index was updated or any write happened.      */
name|boolean
name|close
parameter_list|(
name|long
name|timestamp
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

