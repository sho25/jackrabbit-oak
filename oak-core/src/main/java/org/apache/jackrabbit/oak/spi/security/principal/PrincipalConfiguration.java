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
name|principal
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|api
operator|.
name|security
operator|.
name|principal
operator|.
name|PrincipalManager
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
name|api
operator|.
name|Root
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
name|namepath
operator|.
name|NamePathMapper
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
name|security
operator|.
name|SecurityConfiguration
import|;
end_import

begin_comment
comment|/**  * Configuration interface for principal management.  */
end_comment

begin_interface
specifier|public
interface|interface
name|PrincipalConfiguration
extends|extends
name|SecurityConfiguration
block|{
name|String
name|NAME
init|=
literal|"org.apache.jackrabbit.oak.principal"
decl_stmt|;
comment|/**      * Returns an instance of {@link PrincipalManager} that can be used      * to query and retrieve principals such as needed for JCR access control      * management.      *      * @param root The target root.      * @param namePathMapper The {@code NamePathMapper} to be used.      * @return An instance of {@link PrincipalManager}.      * @see org.apache.jackrabbit.api.JackrabbitSession#getPrincipalManager()      */
annotation|@
name|Nonnull
name|PrincipalManager
name|getPrincipalManager
parameter_list|(
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
function_decl|;
comment|/**      * Returns an instance of the OAK {@link PrincipalProvider}.      *      *<h3>Backwards compatibility with Jackrabbit 2.x</h3>      *<h4>Configuration of Principal Providers</h4>      * In Jackrabbit 2.x the configuration of principal providers was tied to      * the LoginModule configuration and thus mixing authentication concerns      * with the principal management. Since OAK makes the {@code PrincipalProvider}      * a public interface of the SPI, it's configuration goes along with the      * configuration of the JCR level {@link PrincipalManager}. The authentication      * setup may have access to the principal configuration if the      * {@link org.apache.jackrabbit.oak.spi.security.SecurityProvider} is      * made available in the {@link org.apache.jackrabbit.oak.spi.security.authentication.AuthenticationConfiguration}.      *      *<h4>Multiple Sources for Principals</h4>      * In Jackrabbit 2.x it was possible to configure multiple principal providers.      * As of OAK there is only one single principal provider implementation      * responsible for a given workspace. If principals originate from different      * sources it is recommended to use the {@link CompositePrincipalProvider}      * to combine the different sources.      *      * @param root The target {@code Root}.      * @param namePathMapper The {@code NamePathMapper} to be used.      * @return An instance of {@link PrincipalProvider}.      */
annotation|@
name|Nonnull
name|PrincipalProvider
name|getPrincipalProvider
parameter_list|(
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

