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
name|blob
operator|.
name|datastore
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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

begin_comment
comment|/**  * Track the blob ids.  */
end_comment

begin_interface
specifier|public
interface|interface
name|BlobTracker
extends|extends
name|Closeable
block|{
comment|/**      * Adds the given id.      *      * @param id the record id to be tracked      * @throws IOException      */
name|void
name|add
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Adds the given ids.      *      * @param recs      * @throws IOException      */
name|void
name|add
parameter_list|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|recs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Adds the ids in the given file.      *      * @param recs      * @throws IOException      */
name|void
name|add
parameter_list|(
name|File
name|recs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Remove the given ids.      *      * @param recs      * @throws IOException      */
name|void
name|remove
parameter_list|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|recs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Remove the ids in the given file and deletes the file.      *      * @param recs      * @throws IOException      */
name|void
name|remove
parameter_list|(
name|File
name|recs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Fetches an iterator of records available.      *      * @return      * @throws IOException      */
name|Iterator
argument_list|<
name|String
argument_list|>
name|get
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Fetches a File object which having all the sorted records.      * The lifecycle of the returned {@link File} handle is the responsibility of the handler.      *      * @return      * @throws IOException      */
name|File
name|get
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

