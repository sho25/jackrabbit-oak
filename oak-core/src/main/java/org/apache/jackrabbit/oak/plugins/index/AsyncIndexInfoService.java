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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_interface
specifier|public
interface|interface
name|AsyncIndexInfoService
block|{
comment|/**      * Returns all the async indexing lanes which are active      * in the setup.      */
name|Iterable
argument_list|<
name|String
argument_list|>
name|getAsyncLanes
parameter_list|()
function_decl|;
comment|/**      * Returns all the async indexing lanes which are active      * in the setup based on given root NodeState      *      * @param root root NodeState from which async index state      *             is read      */
name|Iterable
argument_list|<
name|String
argument_list|>
name|getAsyncLanes
parameter_list|(
name|NodeState
name|root
parameter_list|)
function_decl|;
comment|/**      * Returns the info for async indexer with given name      */
annotation|@
name|CheckForNull
name|AsyncIndexInfo
name|getInfo
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns the info for async indexer with given name      * and based on given root NodeState      */
annotation|@
name|CheckForNull
name|AsyncIndexInfo
name|getInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|root
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

