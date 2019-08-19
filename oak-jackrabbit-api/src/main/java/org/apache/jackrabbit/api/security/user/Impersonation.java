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
name|api
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
name|principal
operator|.
name|PrincipalIterator
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

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|Subject
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

begin_comment
comment|/**  * The<code>Impersonation</code> maintains Principals that are allowed to  * impersonate. Principals can be added or removed using  * {@link #grantImpersonation(Principal)} and  * {@link #revokeImpersonation(Principal)}, respectively.  *  * @see User#getImpersonation()  */
end_comment

begin_interface
specifier|public
interface|interface
name|Impersonation
block|{
comment|/**      * @return An iterator over the<code>Principal</code>s that are allowed      * to impersonate the<code>User</code> this<code>Impersonation</code>      * object has been created for.      * @throws RepositoryException If an error occurs.      */
annotation|@
name|NotNull
name|PrincipalIterator
name|getImpersonators
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * @param principal The principal that should be allowed to impersonate      * the<code>User</code> this<code>Impersonation</code> has been built for.      * @return true if the specified<code>Principal</code> has not been allowed      * to impersonate before and if impersonation has been successfully      * granted to it, false otherwise.      * @throws RepositoryException If an error occurs.      */
name|boolean
name|grantImpersonation
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * @param principal The principal that should no longer be allowed to      * impersonate.      * @return If the granted impersonation has been successfully revoked for      * the given principal; false otherwise.      * @throws RepositoryException If an error occurs.      */
name|boolean
name|revokeImpersonation
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Test if the given subject (i.e. any of the principals it contains) is      * allowed to impersonate.      *      * @param subject to impersonate.      * @return true if this<code>Impersonation</code> allows the specified      * Subject to impersonate.      * @throws RepositoryException If an error occurs.      */
name|boolean
name|allows
parameter_list|(
annotation|@
name|NotNull
name|Subject
name|subject
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
block|}
end_interface

end_unit

