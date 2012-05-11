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
name|spi
operator|.
name|security
operator|.
name|authentication
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Credentials
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
name|LoginContext
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
comment|/**  * Configurable provider taking care of building a {@code LoginContext} for  * the desired authentication mechanism.<p/>  *  * This provider defines a single method {@link #getLoginContext(javax.jcr.Credentials, String)}  * that takes the {@link Credentials credentials} and the workspace name such  * as passed to {@link org.apache.jackrabbit.oak.api.ContentRepository#login(javax.jcr.Credentials, String)}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|LoginContextProvider
block|{
comment|/**      * Returns a new instance of {@link LoginContext} that handles authentication.      *      * @param credentials The {@link Credentials} such as passed to the      * {@link org.apache.jackrabbit.oak.api.ContentRepository#login(javax.jcr.Credentials, String) login}      * method of the repository.      * @param workspaceName The name of the workspace that is being accessed by      * the login called.      * @return A new {@code LoginContext}      * @throws LoginException If an error occurs while creating a new context.      */
name|LoginContext
name|getLoginContext
parameter_list|(
name|Credentials
name|credentials
parameter_list|,
name|String
name|workspaceName
parameter_list|)
throws|throws
name|LoginException
function_decl|;
block|}
end_interface

end_unit

