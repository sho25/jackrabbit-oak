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
name|spi
operator|.
name|state
package|;
end_package

begin_comment
comment|/**  * Provides a NodeStore instance for specific role. A role indicates what type  * store is it e.g. role set to 'secondary' means that this NodeStore acts like  * replica of some primary NodeStore  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeStoreProvider
block|{
comment|/**      * Service property name which determine what role this NodeStore is playing.      */
name|String
name|ROLE
init|=
literal|"role"
decl_stmt|;
name|NodeStore
name|getNodeStore
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

