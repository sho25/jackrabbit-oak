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
operator|.
name|token
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|jcr
operator|.
name|Credentials
import|;
end_import

begin_comment
comment|/**  * Interface to manage create and manage login tokens.  */
end_comment

begin_interface
specifier|public
interface|interface
name|TokenProvider
block|{
comment|/**      * Optional configuration parameter to set the token expiration time in ms.      * Implementations that do not support this option will ignore any config      * options with that name.      */
name|String
name|PARAM_TOKEN_EXPIRATION
init|=
literal|"tokenExpiration"
decl_stmt|;
comment|/**      * Optional configuration parameter to define the length of the key.      * Implementations that do not support this option will ignore any config      * options with that name.      */
name|String
name|PARAM_TOKEN_LENGTH
init|=
literal|"tokenLength"
decl_stmt|;
comment|/**      * Optional configuration parameter to define if a given token should be      * refreshed or not. Implementations that do not support this option will      * ignore any config options with that name.      */
name|String
name|PARAM_TOKEN_REFRESH
init|=
literal|"tokenRefresh"
decl_stmt|;
comment|/**      * Returns {@code true} if the given credentials indicate that a new token      * needs to be issued.      *      * @param credentials The current credentials.      * @return {@code true} if a new login token needs to be created, {@code false} otherwise.      */
name|boolean
name|doCreateToken
parameter_list|(
annotation|@
name|Nonnull
name|Credentials
name|credentials
parameter_list|)
function_decl|;
comment|/**      * Issues a new login token for the user with the specified credentials      * and returns the associated {@code TokenInfo}.      *      * @param credentials The current credentials.      * @return The {@code TokenInfo} associated with the new login token or      * {@code null} if no token has been created.      */
annotation|@
name|CheckForNull
name|TokenInfo
name|createToken
parameter_list|(
annotation|@
name|Nonnull
name|Credentials
name|credentials
parameter_list|)
function_decl|;
comment|/**      * Issues a new login token for the user with the given {@code userId}      * and the specified attributes.      *      * @param userId The identifier of the user for which a new token should      * be created.      * @param attributes The attributes associated with the new token.      * @return The {@code TokenInfo} associated with the new login token or      * {@code null} if no token has been created.      */
annotation|@
name|CheckForNull
name|TokenInfo
name|createToken
parameter_list|(
annotation|@
name|Nonnull
name|String
name|userId
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|attributes
parameter_list|)
function_decl|;
comment|/**      * Retrieves the {@code TokenInfo} associated with the specified login token      * or {@code null}.      *      * @param token A valid login token.      * @return the {@code TokenInfo} associated with the specified login token      * or {@code null}.      */
annotation|@
name|CheckForNull
name|TokenInfo
name|getTokenInfo
parameter_list|(
annotation|@
name|Nonnull
name|String
name|token
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

