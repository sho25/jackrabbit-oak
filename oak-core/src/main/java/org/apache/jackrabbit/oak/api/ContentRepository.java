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
name|api
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NoSuchWorkspaceException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
import|;
end_import

begin_comment
comment|/**  * The {@code RepositoryService} is the main access point of the oak-api. It  * serves the following purposes:  *  *<ul>  *<li> validating a given login request and providing a {@link ContentSession}  *   that is used for further communication with the persistent layer (i.e.  *   Microkernel).</li>  *</ul>  */
end_comment

begin_interface
specifier|public
interface|interface
name|ContentRepository
block|{
comment|/**      * Try to login a user identified by the passed {@code credentials}. On success      * this method returns a {@link ContentSession} to the given {@code workspace}.      *      * @param credentials      * @param workspaceName      * @return the connection      * @throws LoginException      * @throws NoSuchWorkspaceException      */
name|ContentSession
name|login
parameter_list|(
name|Object
name|credentials
parameter_list|,
name|String
name|workspaceName
parameter_list|)
throws|throws
name|LoginException
throws|,
name|NoSuchWorkspaceException
function_decl|;
block|}
end_interface

end_unit

