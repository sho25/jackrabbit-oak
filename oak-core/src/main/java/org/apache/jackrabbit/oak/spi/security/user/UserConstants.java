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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_comment
comment|/**  * User management related constants. Please note that all names and paths  * are OAK names/paths and therefore are not suited to be used in JCR context  * with remapped namespaces.  */
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
annotation|@
name|Deprecated
name|String
name|NT_REP_MEMBERS
init|=
literal|"rep:Members"
decl_stmt|;
name|String
name|NT_REP_MEMBER_REFERENCES_LIST
init|=
literal|"rep:MemberReferencesList"
decl_stmt|;
name|String
name|NT_REP_MEMBER_REFERENCES
init|=
literal|"rep:MemberReferences"
decl_stmt|;
name|String
name|MIX_REP_IMPERSONATABLE
init|=
literal|"rep:Impersonatable"
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
name|REP_MEMBERS_LIST
init|=
literal|"rep:membersList"
decl_stmt|;
name|String
name|REP_IMPERSONATORS
init|=
literal|"rep:impersonators"
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|GROUP_PROPERTY_NAMES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|REP_PRINCIPAL_NAME
argument_list|,
name|REP_AUTHORIZABLE_ID
argument_list|,
name|REP_MEMBERS
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|USER_PROPERTY_NAMES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|REP_PRINCIPAL_NAME
argument_list|,
name|REP_AUTHORIZABLE_ID
argument_list|,
name|REP_PASSWORD
argument_list|,
name|REP_DISABLED
argument_list|,
name|REP_IMPERSONATORS
argument_list|)
decl_stmt|;
comment|/**      * TODO: remove?      */
name|Collection
argument_list|<
name|String
argument_list|>
name|NODE_TYPE_NAMES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|NT_REP_AUTHORIZABLE
argument_list|,
name|NT_REP_USER
argument_list|,
name|NT_REP_GROUP
argument_list|,
name|NT_REP_MEMBERS
argument_list|,
name|NT_REP_MEMBER_REFERENCES
argument_list|,
name|NT_REP_MEMBER_REFERENCES_LIST
argument_list|)
decl_stmt|;
comment|/**      * Configuration option defining the ID of the administrator user.      */
name|String
name|PARAM_ADMIN_ID
init|=
literal|"adminId"
decl_stmt|;
comment|/**      * Configuration option defining if the admin password should be omitted      * upon user creation.      */
name|String
name|PARAM_OMIT_ADMIN_PW
init|=
literal|"omitAdminPw"
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
comment|/**      * Its value determines the maximum number of members within a given      * content structure until additional intermediate structuring is being      * added. This may for example be used to      *<ul>      *<li>switch storing group members in JCR properties or nodes</li>      *<li>define maximum number of members is a multivalued property</li>      *<li>define maximum number of member properties within a given      *     node structure</li>      *</ul>      *      * @deprecated since oak 1.0      */
annotation|@
name|Deprecated
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
comment|/**      * Optional configuration parameter defining how to generate the name of the      * authorizable node from the ID of the new authorizable that is being created.      * The value is expected to be an instance of {@link AuthorizableNodeName}.      * By default {@link AuthorizableNodeName#DEFAULT} is used.      */
name|String
name|PARAM_AUTHORIZABLE_NODE_NAME
init|=
literal|"authorizableNodeName"
decl_stmt|;
comment|/**      * Optional configuration parameter to set the      * {@link org.apache.jackrabbit.oak.spi.security.user.action.AuthorizableActionProvider}      * to be used with the given user management implementation.      * Unless otherwise specified in the configuration      * {@link org.apache.jackrabbit.oak.spi.security.user.action.DefaultAuthorizableActionProvider}      * is used.      */
name|String
name|PARAM_AUTHORIZABLE_ACTION_PROVIDER
init|=
literal|"authorizableActionProvider"
decl_stmt|;
comment|/**      * Optional configuration parameter that might be used to get back support      * for the auto-save behavior which has been dropped in the default      * user management implementation present with OAK.      *      *<p>Note that this option has been added for those cases where API consumers      * rely on the implementation specific behavior present with Jackrabbit 2.x.      * In general using this option should not be required as the Jackrabbit      * User Management API expects that API consumers tests the auto-save      * mode is enabled. Therefore this option should be considered a temporary      * workaround after upgrading a repository to OAK; the affected code should      * be reviewed and adjusted accordingly.</p>      */
name|String
name|PARAM_SUPPORT_AUTOSAVE
init|=
literal|"supportAutoSave"
decl_stmt|;
block|}
end_interface

end_unit

