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
name|security
operator|.
name|authorization
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|plugins
operator|.
name|name
operator|.
name|NamespaceConstants
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
name|plugins
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
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
name|privilege
operator|.
name|PrivilegeConstants
import|;
end_import

begin_comment
comment|/**  * Constants for this access control management implementation.  */
end_comment

begin_interface
specifier|public
interface|interface
name|AccessControlConstants
block|{
name|String
name|REP_POLICY
init|=
literal|"rep:policy"
decl_stmt|;
name|String
name|REP_REPO_POLICY
init|=
literal|"rep:repoPolicy"
decl_stmt|;
name|String
name|REP_PRIVILEGES
init|=
literal|"rep:privileges"
decl_stmt|;
name|String
name|REP_PRINCIPAL_NAME
init|=
literal|"rep:principalName"
decl_stmt|;
name|String
name|REP_GLOB
init|=
literal|"rep:glob"
decl_stmt|;
name|String
name|REP_NODE_PATH
init|=
literal|"rep:nodePath"
decl_stmt|;
comment|/**      * @since OAK 1.0      */
name|String
name|REP_RESTRICTIONS
init|=
literal|"rep:restrictions"
decl_stmt|;
name|String
name|MIX_REP_ACCESS_CONTROLLABLE
init|=
literal|"rep:AccessControllable"
decl_stmt|;
name|String
name|MIX_REP_REPO_ACCESS_CONTROLLABLE
init|=
literal|"rep:RepoAccessControllable"
decl_stmt|;
name|String
name|NT_REP_POLICY
init|=
literal|"rep:Policy"
decl_stmt|;
name|String
name|NT_REP_ACL
init|=
literal|"rep:ACL"
decl_stmt|;
name|String
name|NT_REP_ACE
init|=
literal|"rep:ACE"
decl_stmt|;
name|String
name|NT_REP_GRANT_ACE
init|=
literal|"rep:GrantACE"
decl_stmt|;
name|String
name|NT_REP_DENY_ACE
init|=
literal|"rep:DenyACE"
decl_stmt|;
comment|/**      * @since OAK 1.0      */
name|String
name|NT_REP_RESTRICTIONS
init|=
literal|"rep:Restrictions"
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|POLICY_NODE_NAMES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|REP_POLICY
argument_list|,
name|REP_REPO_POLICY
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|ACE_PROPERTY_NAMES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|REP_PRINCIPAL_NAME
argument_list|,
name|REP_PRIVILEGES
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|AC_NODETYPE_NAMES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|NT_REP_POLICY
argument_list|,
name|NT_REP_ACL
argument_list|,
name|NT_REP_ACE
argument_list|,
name|NT_REP_DENY_ACE
argument_list|,
name|NT_REP_GRANT_ACE
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|)
decl_stmt|;
comment|/**      * Configuration parameter to enforce backwards compatible permission      * validation with respect to user/group creation, modification and removal.      * As of OAK 1.0 those actions require      * {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.Permissions#USER_MANAGEMENT USER_MANAGEMENT}      * permissions while in Jackrabbit 2.0 they were covered by regular item      * write permissions.      *      * @since OAK 1.0      */
name|String
name|PARAM_PERMISSIONS_JR2
init|=
literal|"permissionsJr2"
decl_stmt|;
comment|/**      * Configuration parameter to enable full read access to regular nodes and      * properties at the specified paths.      */
name|String
name|PARAM_READ_PATHS
init|=
literal|"readPaths"
decl_stmt|;
comment|/**      * Default value for the {@link #PARAM_READ_PATHS} configuration parameter.      */
name|Set
argument_list|<
name|String
argument_list|>
name|DEFAULT_READ_PATHS
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|NamespaceConstants
operator|.
name|NAMESPACES_PATH
argument_list|,
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
argument_list|,
name|PrivilegeConstants
operator|.
name|PRIVILEGES_PATH
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

