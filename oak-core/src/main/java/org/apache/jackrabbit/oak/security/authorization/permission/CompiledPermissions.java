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
operator|.
name|permission
package|;
end_package

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
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|PropertyState
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
name|api
operator|.
name|Tree
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
name|tree
operator|.
name|TreeType
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
name|authorization
operator|.
name|permission
operator|.
name|RepositoryPermission
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
name|authorization
operator|.
name|permission
operator|.
name|TreePermission
import|;
end_import

begin_comment
comment|/**  * Internal interface to process methods defined by  * {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.PermissionProvider}.  * Depending on the set of {@link java.security.Principal}s a given implementation  * be may able to simplify the evaluation. See e.g. {@link org.apache.jackrabbit.oak.security.authorization.permission.AllPermissions}  * and {@link org.apache.jackrabbit.oak.security.authorization.permission.NoPermissions}  */
end_comment

begin_interface
interface|interface
name|CompiledPermissions
block|{
comment|/**      * Refresh this instance to reflect the permissions as present with the      * specified {@code Root}.      *      *      * @param root The root      * @param workspaceName The workspace name.      * @see {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.PermissionProvider#refresh()}      */
name|void
name|refresh
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|String
name|workspaceName
parameter_list|)
function_decl|;
comment|/**      * Returns the {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.RepositoryPermission}      * associated with the {@code Root} as specified in {@link #refresh(org.apache.jackrabbit.oak.api.Root, String)}      *      * @return an instance of {@code RepositoryPermission}.      * @see {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.PermissionProvider#getRepositoryPermission()}      */
annotation|@
name|Nonnull
name|RepositoryPermission
name|getRepositoryPermission
parameter_list|()
function_decl|;
comment|/**      * Returns the tree permissions for the specified {@code tree}.      *      *      * @param tree The tree for which to obtain the permissions.      * @param parentPermission The permissions as present with the parent.      * @return The permissions for the specified tree.      * @see {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.TreePermission#getChildPermission(String, org.apache.jackrabbit.oak.spi.state.NodeState)}      */
annotation|@
name|Nonnull
name|TreePermission
name|getTreePermission
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|TreePermission
name|parentPermission
parameter_list|)
function_decl|;
comment|/**      * Returns the tree permissions for the specified {@code tree}.      *      *      * @param tree The tree for which to obtain the permissions.      * @param parentPermission The permissions as present with the parent.      * @return The permissions for the specified tree.      * @see {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.TreePermission#getChildPermission(String, org.apache.jackrabbit.oak.spi.state.NodeState)}      */
annotation|@
name|Nonnull
name|TreePermission
name|getTreePermission
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|TreeType
name|type
parameter_list|,
annotation|@
name|Nonnull
name|TreePermission
name|parentPermission
parameter_list|)
function_decl|;
comment|/**      * Returns {@code true} if the given {@code permissions} are granted on the      * item identified by {@code parent} and optionally {@code property}.      *      *      * @param tree The tree (or parent tree) for which the permissions should be evaluated.      * @param property An optional property state.      * @param permissions The permissions to be tested.      * @return {@code true} if granted.      * @see {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.PermissionProvider#isGranted(org.apache.jackrabbit.oak.api.Tree, org.apache.jackrabbit.oak.api.PropertyState, long)}      */
name|boolean
name|isGranted
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|,
name|long
name|permissions
parameter_list|)
function_decl|;
comment|/**      * Returns {@code true} if the given {@code permissions} are granted on the      * tree identified by the specified {@code path}.      *      * @param path Path of a tree      * @param permissions The permissions to be tested.      * @return {@code true} if granted.      * @see {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.PermissionProvider#isGranted(String, String)}      */
name|boolean
name|isGranted
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
name|long
name|permissions
parameter_list|)
function_decl|;
comment|/**      * Retrieve the privileges granted at the specified {@code tree}.      *      *      * @param tree The tree for which to retrieve the granted privileges.      * @return the set of privileges or an empty set if no privileges are granted.      * @see {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.PermissionProvider#getPrivileges(org.apache.jackrabbit.oak.api.Tree)}      */
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|getPrivileges
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|)
function_decl|;
comment|/**      * Returns {@code true} if all privileges identified by the given {@code privilegeNames}      * are granted at the given {@code tree}.      *      *      * @param tree The target tree.      * @param privilegeNames The privilege names to be tested.      * @return {@code true} if the tree has privileges      */
name|boolean
name|hasPrivileges
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|String
modifier|...
name|privilegeNames
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

