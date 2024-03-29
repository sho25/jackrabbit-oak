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
name|segment
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Blob
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
name|oak
operator|.
name|commons
operator|.
name|Buffer
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
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Converts nodes, properties, values, etc. to records and persists them.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SegmentWriter
block|{
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Write a blob (as list of block records)      *      * @param blob blob to write      * @return the record id of the blob written      * @throws IOException      */
annotation|@
name|NotNull
name|RecordId
name|writeBlob
parameter_list|(
annotation|@
name|NotNull
name|Blob
name|blob
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Writes a stream value record. The given stream is consumed<em>and      * closed</em> by this method.      *      * @param stream stream to be written      * @return the record id of the stream written      * @throws IOException if the input stream could not be read or the output      *                     could not be written      */
annotation|@
name|NotNull
name|RecordId
name|writeStream
parameter_list|(
annotation|@
name|NotNull
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Write a node state. If non null, the passed {@code stableId} will be assigned to      * the persisted node. Otherwise the stable id will be inferred from {@code state}.      *      * @param state node state to write      * @param stableIdBytes the stableId that should be assigned to the node or {@code null}.      * @return the record id of the segment node state written      * @throws IOException      */
annotation|@
name|NotNull
name|RecordId
name|writeNode
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|state
parameter_list|,
annotation|@
name|Nullable
name|Buffer
name|stableIdBytes
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Write a node state.      *<p>      * Equivalent to {@code writeNode(state, null)}      *      * @see #writeNode(NodeState, Buffer)      */
annotation|@
name|NotNull
specifier|default
name|RecordId
name|writeNode
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|writeNode
argument_list|(
name|state
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_interface

end_unit

