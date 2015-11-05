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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|/**  * A broadcast mechanism that is able to send and receive commands.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Broadcaster
block|{
comment|/**      * Send a message.      *       * @param buff the buffer      */
name|void
name|send
parameter_list|(
name|ByteBuffer
name|buff
parameter_list|)
function_decl|;
comment|/**      * Add a listener for new messages.      *       * @param listener the listener      */
name|void
name|addListener
parameter_list|(
name|Listener
name|listener
parameter_list|)
function_decl|;
comment|/**      * Remove a listener.      *       * @param listener the listener      */
name|void
name|removeListener
parameter_list|(
name|Listener
name|listener
parameter_list|)
function_decl|;
comment|/**      * Close the broadcaster.      */
name|void
name|close
parameter_list|()
function_decl|;
comment|/**      * A listener for new messages.      */
specifier|public
interface|interface
name|Listener
block|{
comment|/**          * Receive a message.          *           * @param buff the buffer          */
name|void
name|receive
parameter_list|(
name|ByteBuffer
name|buff
parameter_list|)
function_decl|;
block|}
block|}
end_interface

end_unit

