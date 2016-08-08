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
name|credentials
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
name|java
operator|.
name|util
operator|.
name|Set
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

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
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
name|AbstractLoginModule
import|;
end_import

begin_comment
comment|/**  * Simple helper interface that allows to easily plug support for additional or  * custom {@link Credentials} implementations during authentication.  *  * @see AbstractLoginModule#getSupportedCredentials()  * @see SimpleCredentialsSupport  */
end_comment

begin_interface
specifier|public
interface|interface
name|CredentialsSupport
block|{
comment|/**      * Returns all {@link Credentials credentials} classes supported by this      * implemenation.      *      * @return the supported {@link Credentials credentials} classes.      */
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Class
argument_list|>
name|getCredentialClasses
parameter_list|()
function_decl|;
comment|/**      * Retrieves the user identifier from the specified {@code Credentials}.      * If the specified credentials are not supported or don't contain any      * user id information this method will return {@code null}.      *      * @param credentials The credentials as passed to the repository login.      * @return The user id present in the given {@code Credentials} or {@code null}.      */
annotation|@
name|CheckForNull
name|String
name|getUserId
parameter_list|(
annotation|@
name|Nonnull
name|Credentials
name|credentials
parameter_list|)
function_decl|;
comment|/**      * Obtains the attributes as present with the specified {@code Credentials}.      * If the specified credentials are not supported or don't contain any      * attributes this method will return an empty {@code Map}.      *      * @param credentials The credentials as passed to the repository login.      * @return The credential attributes or an empty {@code Map}.      */
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|getAttributes
parameter_list|(
annotation|@
name|Nonnull
name|Credentials
name|credentials
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

