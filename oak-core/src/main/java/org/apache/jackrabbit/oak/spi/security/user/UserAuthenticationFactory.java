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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|Authentication
import|;
end_import

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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Provides a user management specific implementation of the  * {@link org.apache.jackrabbit.oak.spi.security.authentication.Authentication}  * interface to those {@link javax.security.auth.spi.LoginModule}s that verify  * a given authentication request by evaluation information exposed by the  * Jackrabbit user management API.  */
end_comment

begin_interface
specifier|public
interface|interface
name|UserAuthenticationFactory
block|{
comment|/**      * Returns an implementation {@link org.apache.jackrabbit.oak.spi.security.authentication.Authentication}      * for the specified {@code userId}.      *      * @param userConfiguration The user configuration.      * @param root   The {@link org.apache.jackrabbit.oak.api.Root} that provides repository access.      * @param userId The userId for which a user authentication is provided.      * @return The authentication object specific to the provided user.      */
annotation|@
name|Nonnull
name|Authentication
name|getAuthentication
parameter_list|(
annotation|@
name|Nonnull
name|UserConfiguration
name|userConfiguration
parameter_list|,
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nullable
name|String
name|userId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

