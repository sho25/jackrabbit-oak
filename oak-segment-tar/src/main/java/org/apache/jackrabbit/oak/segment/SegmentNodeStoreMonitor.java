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
name|util
operator|.
name|function
operator|.
name|Supplier
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
comment|/**  * SegmentNodeStoreMonitor is notified for commit related operations performed by SegmentNodeStore.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SegmentNodeStoreMonitor
block|{
name|SegmentNodeStoreMonitor
name|DEFAULT
init|=
operator|new
name|SegmentNodeStoreMonitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onCommit
parameter_list|(
name|Thread
name|t
parameter_list|,
name|long
name|time
parameter_list|)
block|{          }
annotation|@
name|Override
specifier|public
name|void
name|onCommitQueued
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Supplier
argument_list|<
name|GCGeneration
argument_list|>
name|gcGeneration
parameter_list|)
block|{          }
annotation|@
name|Override
specifier|public
name|void
name|onCommitDequeued
parameter_list|(
name|Thread
name|t
parameter_list|,
name|long
name|time
parameter_list|)
block|{                      }
block|}
decl_stmt|;
comment|/**      * Notifies the monitor when a new commit was persisted.      * @param t the thread which initiated the write      * @param time the time spent for persisting the commit      */
name|void
name|onCommit
parameter_list|(
name|Thread
name|t
parameter_list|,
name|long
name|time
parameter_list|)
function_decl|;
comment|/**      * Notifies the monitor when a new commit couldn't be persisted, but was      * queued for later retry.      *       * @param t the thread which initiated the write      * @param gcGeneration the commit's gc generation      */
name|void
name|onCommitQueued
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Supplier
argument_list|<
name|GCGeneration
argument_list|>
name|gcGeneration
parameter_list|)
function_decl|;
comment|/**      * Notifies the monitor when a queued commit was dequeued for processing.      * @param t the thread which initiated the write      * @param time the time spent in the queue      */
name|void
name|onCommitDequeued
parameter_list|(
name|Thread
name|t
parameter_list|,
name|long
name|time
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

