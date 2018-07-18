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
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * Instances of {@code SegmentReader} are responsible for reading records from segments.  *<p>  * Passing a record id that cannot be resolved to any of the read methods will eventually  * result in a {@link SegmentNotFoundException}. Implementations are however free to choose  * to defer such an exception. For example by returning cached data or a thunk to a specific  * record such that the exception is only thrown when actually accessing the returned record.  *<p>  * The behaviour of the read methods is implementation specific when passing a record id  * that does not match the type of the expected record.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SegmentReader
block|{
comment|/**      * Read the string identified by {@code id}.      * @throws SegmentNotFoundException  see class comment for exception semantics      */
annotation|@
name|NotNull
name|String
name|readString
parameter_list|(
annotation|@
name|NotNull
name|RecordId
name|id
parameter_list|)
function_decl|;
comment|/**      * Read the map identified by {@code id}.      * @throws SegmentNotFoundException  see class comment for exception semantics      */
annotation|@
name|NotNull
name|MapRecord
name|readMap
parameter_list|(
annotation|@
name|NotNull
name|RecordId
name|id
parameter_list|)
function_decl|;
comment|/**      * Read the template identified by {@code id}.      * @throws SegmentNotFoundException  see class comment for exception semantics      */
annotation|@
name|NotNull
name|Template
name|readTemplate
parameter_list|(
annotation|@
name|NotNull
name|RecordId
name|id
parameter_list|)
function_decl|;
comment|/**      * Read the node identified by {@code id}.      * @throws SegmentNotFoundException  see class comment for exception semantics      */
annotation|@
name|NotNull
name|SegmentNodeState
name|readNode
parameter_list|(
annotation|@
name|NotNull
name|RecordId
name|id
parameter_list|)
function_decl|;
comment|/**      * Read the current head state based on the head of {@code revisions}      * @param revisions      * @throws SegmentNotFoundException  see class comment for exception semantics      */
annotation|@
name|NotNull
name|SegmentNodeState
name|readHeadState
parameter_list|(
annotation|@
name|NotNull
name|Revisions
name|revisions
parameter_list|)
function_decl|;
comment|/**      * Read the property identified by {@code id} and {@code template}      * @throws SegmentNotFoundException  see class comment for exception semantics      */
annotation|@
name|NotNull
name|SegmentPropertyState
name|readProperty
parameter_list|(
annotation|@
name|NotNull
name|RecordId
name|id
parameter_list|,
annotation|@
name|NotNull
name|PropertyTemplate
name|template
parameter_list|)
function_decl|;
comment|/**      * Read the blob identified by {@code id}.      * @throws SegmentNotFoundException  see class comment for exception semantics      */
annotation|@
name|NotNull
name|SegmentBlob
name|readBlob
parameter_list|(
annotation|@
name|NotNull
name|RecordId
name|id
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

