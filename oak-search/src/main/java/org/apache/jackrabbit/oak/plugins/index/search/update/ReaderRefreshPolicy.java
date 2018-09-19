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
name|index
operator|.
name|search
operator|.
name|update
package|;
end_package

begin_comment
comment|/**  * The refresh policy interface.  *  * A class that implements this interface decides when to refresh an index, if  * there was a change.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ReaderRefreshPolicy
block|{
name|ReaderRefreshPolicy
name|NEVER
init|=
operator|new
name|ReaderRefreshPolicy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|refreshOnReadIfRequired
parameter_list|(
name|Runnable
name|refreshCallback
parameter_list|)
block|{
comment|//Never refresh
block|}
annotation|@
name|Override
specifier|public
name|void
name|refreshOnWriteIfRequired
parameter_list|(
name|Runnable
name|refreshCallback
parameter_list|)
block|{
comment|//Never refresh
block|}
block|}
decl_stmt|;
comment|/**      * Invoked before any query is performed to provide a chance for IndexNode      * to refresh the readers      *      * The index may or may not be updated when this method is invoked.      *      * @param refreshCallback callback to refresh the readers      */
name|void
name|refreshOnReadIfRequired
parameter_list|(
name|Runnable
name|refreshCallback
parameter_list|)
function_decl|;
comment|/**      * Invoked after some writes have been performed and as a final step refresh      * request is being made.      *      * If invoked, it can be assumed that index has been updated.      *      * @param refreshCallback callback to refresh the readers      */
name|void
name|refreshOnWriteIfRequired
parameter_list|(
name|Runnable
name|refreshCallback
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

