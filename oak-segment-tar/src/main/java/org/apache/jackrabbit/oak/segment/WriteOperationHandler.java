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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|GCGeneration
import|;
end_import

begin_comment
comment|/**  * A {@code WriteOperationHandler} executes {@link WriteOperation  * WriteOperation}s and as such serves as a bridge between a {@link  * SegmentWriter} and {@link SegmentBufferWriter}.  */
end_comment

begin_interface
interface|interface
name|WriteOperationHandler
block|{
comment|/**      * @return the current {@code GCGeneration} of the store.      */
annotation|@
name|NotNull
name|GCGeneration
name|getGCGeneration
parameter_list|()
function_decl|;
comment|/**      * A {@code WriteOperation} encapsulates an operation on a {@link      * SegmentWriter}. Executing it performs the actual act of persisting      * changes to a {@link SegmentBufferWriter}.      */
interface|interface
name|WriteOperation
block|{
comment|/**          * Persist any changes represented by the {@code WriteOperation} to the          * passed {@code writer}.          * @param writer  writer which must be used to persist any changes          * @return        {@code RecordId} that resulted from persisting the changes.          * @throws IOException          */
annotation|@
name|NotNull
name|RecordId
name|execute
parameter_list|(
annotation|@
name|NotNull
name|SegmentBufferWriter
name|writer
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**      * Execute the passed {@code writeOperation} by passing it a {@link SegmentBufferWriter}.      * @param gcGeneration    the {@code GCGeneration} the changes should persisted with.      * @param writeOperation  {@link WriteOperation} to execute      * @return                {@code RecordId} that resulted from persisting the changes.      * @throws IOException      */
annotation|@
name|NotNull
name|RecordId
name|execute
parameter_list|(
annotation|@
name|NotNull
name|GCGeneration
name|gcGeneration
parameter_list|,
annotation|@
name|NotNull
name|WriteOperation
name|writeOperation
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Flush any pending changes on any {@link SegmentBufferWriter} managed by this instance.      * @param store  the {@code SegmentStore} instance to write the {@code Segment} to      * @throws IOException      */
name|void
name|flush
parameter_list|(
annotation|@
name|NotNull
name|SegmentStore
name|store
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

