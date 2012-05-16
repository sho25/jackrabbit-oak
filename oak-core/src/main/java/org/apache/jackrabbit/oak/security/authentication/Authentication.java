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
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * The {@code Authentication} interface defines methods to validate  * {@link javax.jcr.Credentials Credentials} during the  * {@link javax.security.auth.spi.LoginModule#login() login step} of the  * authentication process. The validation depends on the authentication  * mechanism in place.<p/>  *  * A given implementation may only handle certain types of {@code Credentials}  * as the authentication process is tightly coupled to the semantics of the  * {@code Credentials}.<p/>  *  * For example a implementation may only be able to validate UserID/password  * pairs such as passed with {@link javax.jcr.SimpleCredentials}, while another  * might be responsible for validating login token issued by the repository or  * an external access token generation mechanism.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Authentication
block|{
comment|// TODO: evaluate if that should part of SPI package.
comment|/**      * Validates the specified {@code Credentials} and returns {@code true} if      * the validation was successful.      *      * @param credentials to verify      * @return {@code true} if the validation was successful; {@code false}      * if the specified credentials are not supported or if validation failed.      */
name|boolean
name|authenticate
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
function_decl|;
comment|/**      * Test if the given subject (i.e. any of the principals it contains) is      * allowed to impersonate.      *      * @param principals a set of principals to test.      * @return true if this {@code Impersonation} allows the specified      * set of principals to impersonate.      */
name|boolean
name|impersonate
parameter_list|(
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

