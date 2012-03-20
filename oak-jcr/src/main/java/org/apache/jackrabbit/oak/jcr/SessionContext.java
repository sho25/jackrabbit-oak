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
name|jcr
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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|jcr
operator|.
name|state
operator|.
name|NodeStateProvider
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
import|;
end_import

begin_interface
specifier|public
interface|interface
name|SessionContext
parameter_list|<
name|T
extends|extends
name|Session
parameter_list|>
block|{
name|T
name|getSession
parameter_list|()
function_decl|;
name|GlobalContext
name|getGlobalContext
parameter_list|()
function_decl|;
name|String
name|getWorkspaceName
parameter_list|()
function_decl|;
name|MicroKernel
name|getMicrokernel
parameter_list|()
function_decl|;
name|String
name|getRevision
parameter_list|()
function_decl|;
name|ValueFactory
name|getValueFactory
parameter_list|()
function_decl|;
name|NodeStateProvider
name|getNodeStateProvider
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

