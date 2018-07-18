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
operator|.
name|scheduler
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|CommitFailedException
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

begin_comment
comment|/**  * A {@code Scheduler} instance transforms changes to the content tree  * into a queue of {@link Commit commits}.  *<p>  * An implementation is free to employ any scheduling strategy as long  * as it guarantees all changes are applied atomically without changing  * the semantics of the changes recorded in the {@code NodeBuilder} or  * the semantics of the {@code CommitHook} contained in the actual {@code Commit}   * passed to the {@link #schedule(Commit, SchedulerOption...) schedule}  * method.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Scheduler
block|{
comment|/**      * Scheduling options for parametrizing individual commits.      * (E.g. expedite, prioritize, defer, collapse, coalesce, parallelize, etc).      *      */
interface|interface
name|SchedulerOption
block|{}
comment|/**      * Schedule a {@code commit}. This method blocks until the changes in this      * {@code commit} have been processed and persisted. That is, until a call      * to {@link Scheduler#getHeadNodeState()} would return a node state reflecting those      * changes.      *      * @param commit    the commit      * @param schedulingOptions       implementation specific scheduling options      * @throws CommitFailedException  if the commit failed and none of the changes      *                                have been applied.      */
name|NodeState
name|schedule
parameter_list|(
annotation|@
name|NotNull
name|Commit
name|commit
parameter_list|,
name|SchedulerOption
modifier|...
name|schedulingOptions
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
comment|/**      * Creates a new checkpoint of the latest root of the tree. The checkpoint      * remains valid for at least as long as requested and allows that state      * of the repository to be retrieved using the returned opaque string      * reference.      *<p>      * The {@code properties} passed to this methods are associated with the      * checkpoint and can be retrieved through the      * {@link org.apache.jackrabbit.oak.spi.state.NodeStore#checkpointInfo(String)}      * method. Its semantics is entirely application specific.      *      * @param lifetime time (in milliseconds,&gt; 0) that the checkpoint      *                 should remain available      * @param properties properties to associate with the checkpoint      * @return string reference of this checkpoint      */
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|,
annotation|@
name|NotNull
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|)
function_decl|;
comment|/**      * Releases the provided checkpoint. If the provided checkpoint doesn't exist this method should return {@code true}.      *      * @param name string reference of a checkpoint      * @return {@code true} if the checkpoint was successfully removed, or if it doesn't exist      */
name|boolean
name|removeCheckpoint
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns the latest state of the tree.      * @return the latest state.      */
name|NodeState
name|getHeadNodeState
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

