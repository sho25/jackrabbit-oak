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
name|Tree
import|;
end_import

begin_comment
comment|/**  * PermissionProvider... TODO  */
end_comment

begin_interface
specifier|public
interface|interface
name|PermissionProvider
block|{
comment|/**      *      */
name|void
name|refresh
parameter_list|()
function_decl|;
comment|/**      *      * @param tree      * @return      */
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
comment|/**      *      * @param tree      * @param privilegeNames      * @return      */
name|boolean
name|hasPrivileges
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|,
name|String
modifier|...
name|privilegeNames
parameter_list|)
function_decl|;
comment|/**      *      * @param tree      * @param property      * @return      */
name|ReadStatus
name|getReadStatus
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
parameter_list|)
function_decl|;
comment|/**      * Returns {@code true} if the specified repository level permissions are      * {@code granted}; false otherwise.      *      * @param repositoryPermissions Any valid repository level permission such as      * for example:      *<ul>      *<li>{@link Permissions#NAMESPACE_MANAGEMENT}</li>      *<li>{@link Permissions#NODE_TYPE_DEFINITION_MANAGEMENT}</li>      *<li>{@link Permissions#PRIVILEGE_MANAGEMENT}</li>      *<li>{@link Permissions#WORKSPACE_MANAGEMENT}</li>      *</ul>      * @return {@code true} if the specified repository level permissions are      * {@code granted}; false otherwise.      */
name|boolean
name|isGranted
parameter_list|(
name|long
name|repositoryPermissions
parameter_list|)
function_decl|;
comment|/**      *      * @param parent      * @param property      * @param permissions      * @return      */
name|boolean
name|isGranted
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|parent
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
comment|/**      *      * @param oakPath      * @param jcrActions      * @return      */
name|boolean
name|isGranted
parameter_list|(
annotation|@
name|Nonnull
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|String
name|jcrActions
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

