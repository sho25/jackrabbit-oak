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
name|document
operator|.
name|persistentCache
operator|.
name|broadcast
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Broadcast configuration. Configuration is dynamic, that means can change over  * time. The configuration consists of a list of connected clients. Each client  * can connect and disconnect, and therefore allow other clients to connect to  * it.  */
end_comment

begin_interface
specifier|public
interface|interface
name|DynamicBroadcastConfig
block|{
comment|/**      * The unique id of this client.      */
name|String
name|ID
init|=
literal|"broadcastId"
decl_stmt|;
comment|/**      * The listener address, for example the IP address and port.      */
name|String
name|LISTENER
init|=
literal|"broadcastListener"
decl_stmt|;
comment|/**      * Get the global configuration data that is not associated to a specific client.      *       * @return the configuration      */
name|String
name|getConfig
parameter_list|()
function_decl|;
comment|/**      * Get the client info of all connected clients.      *       * @return the list of client info maps      */
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|getClientInfo
parameter_list|()
function_decl|;
comment|/**      * Announce a new client to others.      *       * @param clientInfo the client info      * @return a unique id (to be used when disconnecting)      */
name|String
name|connect
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|clientInfo
parameter_list|)
function_decl|;
comment|/**      * Sign off.      *       * @param id the unique id      */
name|void
name|disconnect
parameter_list|(
name|String
name|id
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

