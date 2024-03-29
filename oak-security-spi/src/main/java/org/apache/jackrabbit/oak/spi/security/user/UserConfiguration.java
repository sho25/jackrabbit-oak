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
name|user
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|UserManager
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
name|principal
operator|.
name|PrincipalProvider
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

begin_comment
comment|/**  * Configuration interface for user management.  */
end_comment

begin_interface
specifier|public
interface|interface
name|UserConfiguration
extends|extends
name|SecurityConfiguration
block|{
name|String
name|NAME
init|=
literal|"org.apache.jackrabbit.oak.user"
decl_stmt|;
comment|/**      * Create a new {@code UserManager} instance      *      * @param root The root associated with the user manager.      * @param namePathMapper A name path mapper used for conversion of jcr/oak names/paths.      * @return a new instance of {@code UserManager}      */
annotation|@
name|NotNull
name|UserManager
name|getUserManager
parameter_list|(
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
function_decl|;
comment|/**      * Optional method that allows a given user management implementation to      * provide a specific and optimized implementation of the {@link PrincipalProvider}      * interface for the principals represented by the user/groups known to      * this implementation.      *      * If this method returns {@code null} the security setup will by default      * use a basic {@code PrincipalProvider} implementation based on public      * user management API or a combination of other {@link PrincipalProvider}s      * as configured with the repository setup.      *      * @param root The root used to read the principal information from.      * @param namePathMapper The {@code NamePathMapper} to convert oak paths to JCR paths.      * @return An implementation of {@code PrincipalProvider} or {@code null} if      * principal discovery is provided by other means of if the default principal      * provider implementation should be used that acts on public user management      * API.      *      * @see org.apache.jackrabbit.oak.spi.security.principal.PrincipalConfiguration      */
annotation|@
name|Nullable
name|PrincipalProvider
name|getUserPrincipalProvider
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

