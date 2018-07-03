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
package|;
end_package

begin_comment
comment|/**  * This handler gets called back when recovery is needed for a clusterId. An  * implementation then tries to perform the recovery and returns whether the  * recovery was successful. Upon successful recovery, the clusterId will have  * transitioned to the inactive state.  */
end_comment

begin_interface
interface|interface
name|RecoveryHandler
block|{
comment|/**      * A no-op recovery handler, always returning false.      */
name|RecoveryHandler
name|NOOP
init|=
name|clusterId
lambda|->
literal|false
decl_stmt|;
comment|/**      * Perform recovery for the given clusterId and return whether the recovery      * was successful.      *      * @param clusterId perform recovery for this clusterId.      * @return {@code true} if recovery was successful, {@code false} otherwise.      */
name|boolean
name|recover
parameter_list|(
name|int
name|clusterId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

