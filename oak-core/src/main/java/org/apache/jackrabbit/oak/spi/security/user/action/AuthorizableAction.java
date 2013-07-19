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
operator|.
name|action
package|;
end_package

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
name|Authorizable
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
name|user
operator|.
name|Group
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
name|user
operator|.
name|User
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

begin_comment
comment|/**  * The {@code AuthorizableAction} interface provide an implementation  * specific way to execute additional validation or write tasks upon  *  *<ul>  *<li>{@link #onCreate User creation},</li>  *<li>{@link #onCreate Group creation},</li>  *<li>{@link #onRemove Authorizable removal} and</li>  *<li>{@link #onPasswordChange User password modification}.</li>  *</ul>  *  * Note, that in contrast to {@link org.apache.jackrabbit.oak.spi.commit.Validator}  * the authorizable actions will only be enforced when user related content  * modifications are generated by using the user management API.  *  * @see org.apache.jackrabbit.oak.spi.security.ConfigurationParameters  * @since OAK 1.0  */
end_comment

begin_interface
specifier|public
interface|interface
name|AuthorizableAction
block|{
comment|/**      * Allows to add application specific modifications or validation associated      * with the creation of a new group. Note, that this method is called      *<strong>before</strong> any {@code Root#commit()} call.      *      *      * @param group The new group that has not yet been persisted;      * e.g. the associated tree is still 'NEW'.      * @param root The root associated with the user manager.      * @param namePathMapper      * @throws javax.jcr.RepositoryException If an error occurs.      */
name|void
name|onCreate
parameter_list|(
name|Group
name|group
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Allows to add application specific modifications or validation associated      * with the creation of a new user. Note, that this method is called      *<strong>before</strong> any {@code Root#commit()} call.      *      *      * @param user The new user that has not yet been persisted;      * e.g. the associated tree is still 'NEW'.      * @param password The password that was specified upon user creation.      * @param root The root associated with the user manager.      * @param namePathMapper      * @throws RepositoryException If an error occurs.      */
name|void
name|onCreate
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|password
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Allows to add application specific behavior associated with the removal      * of an authorizable. Note, that this method is called<strong>before</strong>      * {@link org.apache.jackrabbit.api.security.user.Authorizable#remove} is executed (and persisted); thus the      * target authorizable still exists.      *      *      * @param authorizable The authorizable to be removed.      * @param root The root associated with the user manager.      * @param namePathMapper      * @throws RepositoryException If an error occurs.      */
name|void
name|onRemove
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Allows to add application specific action or validation associated with      * changing a user password. Note, that this method is called<strong>before</strong>      * the password property is being modified in the content.      *      *      * @param user The user that whose password is going to change.      * @param newPassword The new password as specified in {@link org.apache.jackrabbit.api.security.user.User#changePassword}      * @param root The root associated with the user manager.      * @param namePathMapper      * @throws RepositoryException If an exception or error occurs.      */
name|void
name|onPasswordChange
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|newPassword
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
block|}
end_interface

end_unit

