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

begin_interface
specifier|public
interface|interface
name|StandbyStatusMBean
block|{
name|String
name|JMX_NAME
init|=
literal|"org.apache.jackrabbit.oak:name=Status,type=\"Standby\""
decl_stmt|;
name|String
name|STATUS_INITIALIZING
init|=
literal|"initializing"
decl_stmt|;
name|String
name|STATUS_STOPPED
init|=
literal|"stopped"
decl_stmt|;
name|String
name|STATUS_STARTING
init|=
literal|"starting"
decl_stmt|;
name|String
name|STATUS_RUNNING
init|=
literal|"running"
decl_stmt|;
name|String
name|STATUS_CLOSING
init|=
literal|"closing"
decl_stmt|;
name|String
name|STATUS_CLOSED
init|=
literal|"closed"
decl_stmt|;
annotation|@
name|NotNull
annotation|@
name|Description
argument_list|(
literal|"primary or standby"
argument_list|)
name|String
name|getMode
parameter_list|()
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"current status of the service"
argument_list|)
name|String
name|getStatus
parameter_list|()
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"instance is running"
argument_list|)
name|boolean
name|isRunning
parameter_list|()
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"stop the communication"
argument_list|)
name|void
name|stop
parameter_list|()
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"start the communication"
argument_list|)
name|void
name|start
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

