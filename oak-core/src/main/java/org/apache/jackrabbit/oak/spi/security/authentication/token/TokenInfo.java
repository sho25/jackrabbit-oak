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
name|authentication
operator|.
name|token
operator|.
name|TokenCredentials
import|;
end_import

begin_comment
comment|/**  * The {@code TokenInfo} provides data associated with a login token and  * basic methods to verify the validity of token credentials at given  * point in time.  */
end_comment

begin_interface
specifier|public
interface|interface
name|TokenInfo
block|{
comment|/**      * Returns the ID of the user associated with this token info object.      *      * @return the ID of the user.      */
annotation|@
name|Nonnull
name|String
name|getUserId
parameter_list|()
function_decl|;
comment|/**      * Returns the login token.      *      * @return the login token.      */
annotation|@
name|Nonnull
name|String
name|getToken
parameter_list|()
function_decl|;
comment|/**      * Returns {@code true} if the token has already expired; {@code false} otherwise.      *      * @param loginTime The login time used to calculate the expiration status.      * @return {@code true} if the token has already expired; {@code false} otherwise.      */
name|boolean
name|isExpired
parameter_list|(
name|long
name|loginTime
parameter_list|)
function_decl|;
comment|/**      * Resets the expiration time of the login token associated with the given      * {@code TokenInfo}. Whether and when the expiration time of a given login      * token is being reset is an implementation detail. Implementations that      * don't allow for resetting the token's expiration time at all will always      * return {@code false}.      *      * @param loginTime The current login time.      * @return {@code true} if the expiration time has been reset, false otherwise.      */
name|boolean
name|resetExpiration
parameter_list|(
name|long
name|loginTime
parameter_list|)
function_decl|;
comment|/**      * Tries to remove the login token and all related information. This method      * returns {@code true} if the removal was successful.      *      * @return {@code true} if the removal was successful, {@code false} otherwise.      */
name|boolean
name|remove
parameter_list|()
function_decl|;
comment|/**      * Returns {@code true} if the specified credentials can be successfully      * validated against the information stored in this instance.      *      * @param tokenCredentials The credentials to validate.      * @return {@code true} if the specified credentials can be successfully      * validated against the information stored in this instance; {@code false}      * otherwise.      */
name|boolean
name|matches
parameter_list|(
name|TokenCredentials
name|tokenCredentials
parameter_list|)
function_decl|;
comment|/**      * Returns the private attributes stored with this info object.      *      * @return the private attributes stored with this info object.      */
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getPrivateAttributes
parameter_list|()
function_decl|;
comment|/**      * Returns the public attributes stored with this info object.      *      * @return the public attributes stored with this info object.      */
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getPublicAttributes
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

