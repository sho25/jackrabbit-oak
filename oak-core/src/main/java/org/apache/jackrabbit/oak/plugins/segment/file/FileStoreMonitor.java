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
name|plugins
operator|.
name|segment
operator|.
name|file
package|;
end_package

begin_comment
comment|/**  * FileStoreMonitor are notified for any writes or deletes  * performed by FileStore  */
end_comment

begin_interface
interface|interface
name|FileStoreMonitor
block|{
name|FileStoreMonitor
name|DEFAULT
init|=
operator|new
name|FileStoreMonitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|written
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{          }
annotation|@
name|Override
specifier|public
name|void
name|reclaimed
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{          }
block|}
decl_stmt|;
comment|/**      * Notifies the monitor when data is written      *      * @param bytes number of bytes written      */
name|void
name|written
parameter_list|(
name|long
name|bytes
parameter_list|)
function_decl|;
comment|/**      * Notifies the monitor when memory is reclaimed      *      * @param bytes number of bytes reclaimed      */
name|void
name|reclaimed
parameter_list|(
name|long
name|bytes
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

