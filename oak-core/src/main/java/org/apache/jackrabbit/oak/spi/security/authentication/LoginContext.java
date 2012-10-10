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
name|security
operator|.
name|auth
operator|.
name|Subject
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
comment|/**  * Interface version of the JAAS {@link javax.security.auth.login.LoginContext}  * class. It is used to make integration of non-JAAS authentication components  * easier while still retaining full JAAS support. The {@link JaasLoginContext}  * class acts as a bridge that connects the JAAS  * {@link javax.security.auth.login.LoginContext} class with this interface.  */
end_comment

begin_interface
specifier|public
interface|interface
name|LoginContext
block|{
comment|/**      * @see javax.security.auth.login.LoginContext#getSubject()      */
name|Subject
name|getSubject
parameter_list|()
function_decl|;
comment|/**      * @see javax.security.auth.login.LoginContext#login()      */
name|void
name|login
parameter_list|()
throws|throws
name|LoginException
function_decl|;
comment|/**      * @see javax.security.auth.login.LoginContext#logout()      */
name|void
name|logout
parameter_list|()
throws|throws
name|LoginException
function_decl|;
block|}
end_interface

end_unit

