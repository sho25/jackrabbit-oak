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
name|jcr
operator|.
name|Credentials
import|;
end_import

begin_comment
comment|/**  * User is a special {@link Authorizable} that can be authenticated and  * impersonated.  *  * @see #getCredentials()  * @see #getImpersonation()  */
end_comment

begin_interface
specifier|public
interface|interface
name|User
extends|extends
name|Authorizable
block|{
comment|/**      * @return true if the current user represents the administrator.      */
name|boolean
name|isAdmin
parameter_list|()
function_decl|;
comment|/**      * @return true if the current user represents a system user.      */
name|boolean
name|isSystemUser
parameter_list|()
function_decl|;
comment|/**      * Returns the internal<code>Credentials</code> representation for this      * user. This method is expected to be used for validation during the      * login process. However, the return value should neither be usable nor      * used for {@link javax.jcr.Repository#login}.      *      * @return<code>Credentials</code> for this user.      * @throws javax.jcr.RepositoryException If an error occurs.      */
annotation|@
name|NotNull
name|Credentials
name|getCredentials
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * @return<code>Impersonation</code> for this<code>User</code>.      * @throws javax.jcr.RepositoryException If an error occurs.      */
annotation|@
name|NotNull
name|Impersonation
name|getImpersonation
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Change the password of this user.      *      * @param password The new password.      * @throws RepositoryException If an error occurs.      */
name|void
name|changePassword
parameter_list|(
annotation|@
name|Nullable
name|String
name|password
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Change the password of this user.      *      * @param password The new password.      * @param oldPassword The old password.      * @throws RepositoryException If the old password doesn't match or if      * an error occurs.      */
name|void
name|changePassword
parameter_list|(
annotation|@
name|Nullable
name|String
name|password
parameter_list|,
annotation|@
name|NotNull
name|String
name|oldPassword
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Disable this user thus preventing future login if the<code>reason</code>      * is a non-null String.<br>      * Note however, that this user will still be accessible by      * {@link UserManager#getAuthorizable}.      *      * @param reason String describing the reason for disable this user or      *<code>null</code> if the user account should be enabled again.      * @throws RepositoryException If an error occurs.      */
name|void
name|disable
parameter_list|(
annotation|@
name|Nullable
name|String
name|reason
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Returns<code>true</code> if this user is disabled,<code>false</code>      * otherwise.      *      * @return<code>true</code> if this user is disabled,<code>false</code>      * otherwise.      * @throws RepositoryException If an error occurs.      */
name|boolean
name|isDisabled
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Returns the String specified upon disabling this user or<code>null</code>      * if {@link #isDisabled()} returns<code>false</code>.      *       * @return The reason specified upon disabling this user or<code>null</code>      * if this user is not disabled.      * @throws RepositoryException If an error occurs.      */
annotation|@
name|Nullable
name|String
name|getDisabledReason
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
block|}
end_interface

end_unit

