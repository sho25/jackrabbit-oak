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
name|authorization
operator|.
name|accesscontrol
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
comment|/**  * Constants for the default access control management implementation and  * and for built-in access control related node types.  */
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
comment|/**      * Name of the optional multivalued access control restriction by node type name.      * The corresponding restriction type is {@link org.apache.jackrabbit.oak.api.Type#NAMES}.      *      * @since OAK 1.0      */
name|String
name|REP_NT_NAMES
init|=
literal|"rep:ntNames"
decl_stmt|;
comment|/**      * Name of the optional multivalued access control restriction which matches by name space prefix.      * The corresponding restriction type is {@link org.apache.jackrabbit.oak.api.Type#STRINGS}.      *      * @since OAK 1.0      */
name|String
name|REP_PREFIXES
init|=
literal|"rep:prefixes"
decl_stmt|;
comment|/**      * Name of the optional multivalued access control restriction by item name.      * The corresponding restriction type is {@link org.apache.jackrabbit.oak.api.Type#NAMES}.      *      * @since OAK 1.3.8      */
name|String
name|REP_ITEM_NAMES
init|=
literal|"rep:itemNames"
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
name|String
name|PARAM_RESTRICTION_PROVIDER
init|=
literal|"restrictionProvider"
decl_stmt|;
name|String
name|PARAM_MOUNT_PROVIDER
init|=
literal|"mountInfoProvider"
decl_stmt|;
block|}
end_interface

end_unit

