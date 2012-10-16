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

begin_comment
comment|/**  * UserConstants...  */
end_comment

begin_interface
specifier|public
interface|interface
name|UserConstants
block|{
name|String
name|NT_REP_AUTHORIZABLE
init|=
literal|"rep:Authorizable"
decl_stmt|;
name|String
name|NT_REP_AUTHORIZABLE_FOLDER
init|=
literal|"rep:AuthorizableFolder"
decl_stmt|;
name|String
name|NT_REP_USER
init|=
literal|"rep:User"
decl_stmt|;
name|String
name|NT_REP_GROUP
init|=
literal|"rep:Group"
decl_stmt|;
name|String
name|NT_REP_MEMBERS
init|=
literal|"rep:Members"
decl_stmt|;
name|String
name|REP_PRINCIPAL_NAME
init|=
literal|"rep:principalName"
decl_stmt|;
name|String
name|REP_AUTHORIZABLE_ID
init|=
literal|"rep:authorizableId"
decl_stmt|;
name|String
name|REP_PASSWORD
init|=
literal|"rep:password"
decl_stmt|;
name|String
name|REP_DISABLED
init|=
literal|"rep:disabled"
decl_stmt|;
name|String
name|REP_MEMBERS
init|=
literal|"rep:members"
decl_stmt|;
name|String
name|REP_IMPERSONATORS
init|=
literal|"rep:impersonators"
decl_stmt|;
comment|/**      * Configuration option defining the ID of the administrator user.      */
name|String
name|PARAM_ADMIN_ID
init|=
literal|"adminId"
decl_stmt|;
comment|/**      * Default value for {@link #PARAM_ADMIN_ID}      */
name|String
name|DEFAULT_ADMIN_ID
init|=
literal|"admin"
decl_stmt|;
comment|/**      * Configuration option defining the ID of the anonymous user. The ID      * might be {@code null} of no anonymous user exists. In this case      * Session#getUserID() may return {@code null} if it has been obtained      * using {@link javax.jcr.GuestCredentials}.      */
name|String
name|PARAM_ANONYMOUS_ID
init|=
literal|"anonymousId"
decl_stmt|;
comment|/**      * Default value for {@link #PARAM_ANONYMOUS_ID}      */
name|String
name|DEFAULT_ANONYMOUS_ID
init|=
literal|"anonymous"
decl_stmt|;
comment|/**      * Configuration option to define the path underneath which user nodes      * are being created.      */
name|String
name|PARAM_USER_PATH
init|=
literal|"usersPath"
decl_stmt|;
comment|/**      * Default value for {@link #PARAM_USER_PATH}      */
name|String
name|DEFAULT_USER_PATH
init|=
literal|"/rep:security/rep:authorizables/rep:users"
decl_stmt|;
comment|/**      * Configuration option to define the path underneath which group nodes      * are being created.      */
name|String
name|PARAM_GROUP_PATH
init|=
literal|"groupsPath"
decl_stmt|;
comment|/**      * Default value for {@link #PARAM_GROUP_PATH}      */
name|String
name|DEFAULT_GROUP_PATH
init|=
literal|"/rep:security/rep:authorizables/rep:groups"
decl_stmt|;
comment|/**      * Parameter used to change the number of levels that are used by default      * store authorizable nodes.<br>The default number of levels is 2.      */
name|String
name|PARAM_DEFAULT_DEPTH
init|=
literal|"defaultDepth"
decl_stmt|;
comment|/**      * Default value for {@link #PARAM_DEFAULT_DEPTH}      */
name|int
name|DEFAULT_DEPTH
init|=
literal|2
decl_stmt|;
comment|/**      * Its value determines the maximum number of members within a given      * content structure until additional intermediate structuring is being      * added. This may for example be used to      *<ul>      *<li>switch storing group members in JCR properties or nodes</li>      *<li>define maximum number of members is a multivalued property</li>      *<li>define maximum number of member properties within a given      *     node structure</li>      *</ul>      */
name|String
name|PARAM_GROUP_MEMBERSHIP_SPLIT_SIZE
init|=
literal|"groupMembershipSplitSize"
decl_stmt|;
comment|/**      * Configuration parameter to change the default algorithm used to generate      * password hashes.      */
name|String
name|PARAM_PASSWORD_HASH_ALGORITHM
init|=
literal|"passwordHashAlgorithm"
decl_stmt|;
comment|/**      * Configuration parameter to change the number of iterations used for      * password hash generation.      */
name|String
name|PARAM_PASSWORD_HASH_ITERATIONS
init|=
literal|"passwordHashIterations"
decl_stmt|;
comment|/**      * Configuration parameter to change the number of iterations used for      * password hash generation.      */
name|String
name|PARAM_PASSWORD_SALT_SIZE
init|=
literal|"passwordSaltSize"
decl_stmt|;
comment|/**      * Configuration parameter to set the authorizable actions.      *      * @see org.apache.jackrabbit.oak.spi.security.user.action.AuthorizableAction      */
name|String
name|PARAM_AUTHORIZABLE_ACTIONS
init|=
literal|"authorizableActions"
decl_stmt|;
block|}
end_interface

end_unit

