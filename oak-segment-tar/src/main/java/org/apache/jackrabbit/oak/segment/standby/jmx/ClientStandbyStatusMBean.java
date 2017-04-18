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
name|standby
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
name|jmx
operator|.
name|Description
import|;
end_import

begin_interface
specifier|public
interface|interface
name|ClientStandbyStatusMBean
extends|extends
name|StandbyStatusMBean
block|{
annotation|@
name|Description
argument_list|(
literal|"number of consecutive failed requests"
argument_list|)
name|int
name|getFailedRequests
parameter_list|()
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"number of seconds since last successful request"
argument_list|)
name|int
name|getSecondsSinceLastSuccess
parameter_list|()
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"Local timestamp of the moment when the last sync cycle was started"
argument_list|)
name|long
name|getSyncStartTimestamp
parameter_list|()
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"Local timestamp of the moment when the last sync cycle ended"
argument_list|)
name|long
name|getSyncEndTimestamp
parameter_list|()
function_decl|;
comment|// expose the informations as operations, too
annotation|@
name|Description
argument_list|(
literal|"number of consecutive failed requests"
argument_list|)
name|int
name|calcFailedRequests
parameter_list|()
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"number of seconds since last successful request"
argument_list|)
name|int
name|calcSecondsSinceLastSuccess
parameter_list|()
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"Runs garbage collection"
argument_list|)
name|void
name|cleanup
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

