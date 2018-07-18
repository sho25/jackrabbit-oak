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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
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

begin_comment
comment|/**  * {@code Revisions} instances provide read and write access to  * the current head state. Implementations are thread safe  * and all setters act atomically.  *<p>  * This is a low level API and it is the callers and implementors  * responsibility to ensure all record id passed to or returned  * from methods of this interface are the ids of node states.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Revisions
block|{
comment|/**      * Implementation specific options for the {@code setHead} methods.      * These options can e.g. be used to specify priority, timeout, etc.      * for individual method calls.      */
interface|interface
name|Option
block|{}
comment|/**      * Returns the record id of the head state. The returned id      * is a valid id for a {@code SegmentNodeState}.      * @return  id of the head state      */
annotation|@
name|NotNull
name|RecordId
name|getHead
parameter_list|()
function_decl|;
comment|/**      * Returns the<b>persisted</b> to disk record id of the head state.       * The returned id is a valid id for a {@code SegmentNodeState}.      * @return  id of the head state      */
annotation|@
name|NotNull
name|RecordId
name|getPersistedHead
parameter_list|()
function_decl|;
comment|/**      * Atomically set the record id of the current head state to the      * given {@code head} state if the current head state matches      * the {@code expected} value.      *<p>      * All record ids must be valid ids for {@code SegmentNodeState}s.      *<p>      * The locking behaviour of this method regarding implementation      * specific.      *      * @param expected  the expected head for the update to take place      * @param head      the new head to update to      * @param options   implementation specific options      * @return          {@code true} if the current head was successfully      *                  updated, {@code false} otherwise.      */
name|boolean
name|setHead
parameter_list|(
annotation|@
name|NotNull
name|RecordId
name|expected
parameter_list|,
annotation|@
name|NotNull
name|RecordId
name|head
parameter_list|,
annotation|@
name|NotNull
name|Option
modifier|...
name|options
parameter_list|)
function_decl|;
comment|/**      * Atomically set the record id of the current head state to the value      * returned from the {@code newHead} function when called with the record      * id of the current head.      *<p>      * All record ids must be valid ids for {@code SegmentNodeState}s.      *<p>      * The behaviour of this method regarding locking and handling      * {@code null} values returned by {@code newHead} is implementation specific.      *      * @param newHead  function mapping an record id to the record id to which      *                 the current head id should be set.      * @param options  implementation specific options      * @return         the record id of the root node if the current head was successfully      *                 updated, {@code null} otherwise.      * @throws InterruptedException      *                 Blocking implementations may throw this exception whe      *                 interrupted.      */
name|RecordId
name|setHead
parameter_list|(
annotation|@
name|NotNull
name|Function
argument_list|<
name|RecordId
argument_list|,
name|RecordId
argument_list|>
name|newHead
parameter_list|,
annotation|@
name|NotNull
name|Option
modifier|...
name|options
parameter_list|)
throws|throws
name|InterruptedException
function_decl|;
block|}
end_interface

end_unit

