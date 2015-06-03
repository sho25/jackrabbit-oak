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
name|AuthInfo
import|;
end_import

begin_comment
comment|/**  * MBean providing basic {@code Session} information and statistics.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SessionMBean
block|{
name|String
name|TYPE
init|=
literal|"SessionStatistics"
decl_stmt|;
comment|/**      * @return stack trace from where the session was acquired.      */
name|String
name|getInitStackTrace
parameter_list|()
function_decl|;
comment|/**      * @return {@code AuthInfo} for the user associated with the session.      */
name|AuthInfo
name|getAuthInfo
parameter_list|()
function_decl|;
comment|/**      * @return time stamp from when the session was acquired.      */
name|String
name|getLoginTimeStamp
parameter_list|()
function_decl|;
comment|/**      * @return time stamp from the last read access      */
name|String
name|getLastReadAccess
parameter_list|()
function_decl|;
comment|/**      * @return number of read accesses      */
name|long
name|getReadCount
parameter_list|()
function_decl|;
comment|/**      * @return read operations per time      */
name|double
name|getReadRate
parameter_list|()
function_decl|;
comment|/**      * @return time stamp from the last write access      */
name|String
name|getLastWriteAccess
parameter_list|()
function_decl|;
comment|/**      * @return number of write accesses      */
name|long
name|getWriteCount
parameter_list|()
function_decl|;
comment|/**      * @return write operations per time      */
name|double
name|getWriteRate
parameter_list|()
function_decl|;
comment|/**      * @return time stamp from the last refresh      */
name|String
name|getLastRefresh
parameter_list|()
function_decl|;
comment|/**      * @return description of the refresh strategy      */
name|String
name|getRefreshStrategy
parameter_list|()
function_decl|;
comment|/**      * @return {@code true} iff the session will be refreshed on next access.      */
name|boolean
name|getRefreshPending
parameter_list|()
function_decl|;
comment|/**      * @return number of refresh operations      */
name|long
name|getRefreshCount
parameter_list|()
function_decl|;
comment|/**      * @return refresh operations per time      */
name|double
name|getRefreshRate
parameter_list|()
function_decl|;
comment|/**      * @return time stamp from the last save      */
name|String
name|getLastSave
parameter_list|()
function_decl|;
comment|/**      * @return number of save operations      */
name|long
name|getSaveCount
parameter_list|()
function_decl|;
comment|/**      * @return save operations per time      */
name|double
name|getSaveRate
parameter_list|()
function_decl|;
comment|/**      * @return attributes associated with the session      */
name|String
index|[]
name|getSessionAttributes
parameter_list|()
function_decl|;
comment|/**      * @return  stack trace of the last exception that occurred during a save operation      */
name|String
name|getLastFailedSave
parameter_list|()
function_decl|;
comment|/**      * Refresh this session.      *<em>Warning</em>: this operation might be disruptive to the owner of this session      */
name|void
name|refresh
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

