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
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

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
name|LoginException
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
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|annotation
operator|.
name|versioning
operator|.
name|ProviderType
import|;
end_import

begin_comment
comment|/**  * The {@code Authentication} interface defines methods to validate  * {@link javax.jcr.Credentials Credentials} during the  * {@link javax.security.auth.spi.LoginModule#login() login step} of the  * authentication process. The validation depends on the authentication  * mechanism in place.<p>  *  * A given implementation may only handle certain types of {@code Credentials}  * as the authentication process is tightly coupled to the semantics of the  * {@code Credentials}.<p>  *  * For example a implementation may only be able to validate UserID/password  * pairs such as passed with {@link javax.jcr.SimpleCredentials}, while another  * might be responsible for validating login token issued by the repository or  * an external access token generation mechanism.  */
end_comment

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|Authentication
block|{
comment|/**      * Validates the specified {@code Credentials} and returns {@code true} if      * the validation was successful.      *      * @param credentials to verify      * @return {@code true} if the validation was successful; {@code false}      * if the specified credentials are not supported and this authentication      * implementation cannot verify their validity.      * @throws LoginException if the authentication failed.      */
name|boolean
name|authenticate
parameter_list|(
annotation|@
name|Nullable
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|LoginException
function_decl|;
comment|/**      * Optional method that return the userID extracted upon {@link #authenticate(Credentials)}.      * It is expected to return {@code null} if the implementation doesn't support this.      *      * An {@link IllegalStateException} may be thrown if called prior to {@link #authenticate(Credentials)}.      *      * @return a user identifier or {@code null}      */
annotation|@
name|Nullable
name|String
name|getUserId
parameter_list|()
function_decl|;
comment|/**      * Optional method that return the {@link Principal} of the authenticating user      * extracted upon {@link #authenticate(Credentials)}. It is expected to return      * {@code null} if the implementation doesn't support this.      *      * An {@link IllegalStateException} may be thrown if called prior to {@link #authenticate(Credentials)}.      *      * @return a valid {@code Principal} or {@code null}      */
annotation|@
name|Nullable
name|Principal
name|getUserPrincipal
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

