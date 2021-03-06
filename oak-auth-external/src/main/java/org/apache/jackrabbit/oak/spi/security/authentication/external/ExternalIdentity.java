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
name|external
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
comment|/**  * {@code ExternalIdentity} defines an identity provided by an external system.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ExternalIdentity
block|{
comment|/**      * Returns the id of this identity as used in the external system.      * @return the external id.      */
annotation|@
name|NotNull
name|ExternalIdentityRef
name|getExternalId
parameter_list|()
function_decl|;
comment|/**      * Returns the local id of this identity as it would be used in this repository. This usually corresponds to      * {@link org.apache.jackrabbit.api.security.user.Authorizable#getID()}      *      * @return the internal id.      */
annotation|@
name|NotNull
name|String
name|getId
parameter_list|()
function_decl|;
comment|/**      * Returns the principal name of this identity. This usually corresponds to      * {@link org.apache.jackrabbit.api.security.user.Authorizable#getPrincipal()}.{@link java.security.Principal#getName()}      * @return the principal name      */
annotation|@
name|NotNull
name|String
name|getPrincipalName
parameter_list|()
function_decl|;
comment|/**      * Returns the desired intermediate relative path of the authorizable to be created. For example, one could map      * an external hierarchy into the local users and groups hierarchy. The provider must escape all characters so that      * the path is a valid JCR path. The path is always considered relative, even if it starts with a '/'.      *      * @return the intermediate path or {@code null} or empty.      */
annotation|@
name|Nullable
name|String
name|getIntermediatePath
parameter_list|()
function_decl|;
comment|/**      * Returns an iterable of the declared groups of this external identity.      * @return the declared groups      * @throws ExternalIdentityException if an error occurs      */
annotation|@
name|NotNull
name|Iterable
argument_list|<
name|ExternalIdentityRef
argument_list|>
name|getDeclaredGroups
parameter_list|()
throws|throws
name|ExternalIdentityException
function_decl|;
comment|/**      * Returns a map of properties of this external identity.      * @return the properties      */
annotation|@
name|NotNull
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|getProperties
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

