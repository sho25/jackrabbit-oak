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
name|api
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_comment
comment|/**  * MBean for managing {@link org.apache.jackrabbit.oak.spi.state.NodeStore#checkpoint checkpoints}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|CheckpointMBean
block|{
name|String
name|TYPE
init|=
literal|"CheckpointManger"
decl_stmt|;
comment|/**      * List the checkpoints that are currently present along with      * its id, creation time and expiry time.      * @return      */
name|TabularData
name|listCheckpoints
parameter_list|()
function_decl|;
comment|/**      * Create a new checkpoint with the given {@code lifetime}.      * @param lifetime      * @return the id of the newly created checkpoint      * @see org.apache.jackrabbit.oak.spi.state.NodeStore#checkpoint      */
name|String
name|createCheckpoint
parameter_list|(
name|long
name|lifetime
parameter_list|)
function_decl|;
comment|/**      * Release the checkpoint with the given {@code id}.      * @param id      * @return  {@code true} on success, {@code false} otherwise.      * @see org.apache.jackrabbit.oak.spi.state.NodeStore#checkpoint      */
name|boolean
name|releaseCheckpoint
parameter_list|(
name|String
name|id
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

