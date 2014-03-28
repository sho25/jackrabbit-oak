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

begin_comment
comment|/**  * The {@link RepositoryPermission} allows to evaluate permissions that have  * been defined on the repository level and which consequently are not bound  * to a particular item.  *  * @see org.apache.jackrabbit.oak.spi.security.authorization.permission.PermissionProvider#getRepositoryPermission()  */
end_comment

begin_interface
specifier|public
interface|interface
name|RepositoryPermission
block|{
comment|/**      * Returns {@code true} if the specified repository level permissions are      * {@code granted}; false otherwise.      *      * @param repositoryPermissions Any valid repository level permission such as      * for example:      *<ul>      *<li>{@link Permissions#NAMESPACE_MANAGEMENT}</li>      *<li>{@link Permissions#NODE_TYPE_DEFINITION_MANAGEMENT}</li>      *<li>{@link Permissions#PRIVILEGE_MANAGEMENT}</li>      *<li>{@link Permissions#WORKSPACE_MANAGEMENT}</li>      *</ul>      * @return {@code true} if the specified repository level permissions are      * {@code granted}; false otherwise.      */
name|boolean
name|isGranted
parameter_list|(
name|long
name|repositoryPermissions
parameter_list|)
function_decl|;
comment|/**      * {@code RepositoryPermission} instance that always returns {@code false}.      */
name|RepositoryPermission
name|EMPTY
init|=
operator|new
name|RepositoryPermission
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|long
name|repositoryPermissions
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
decl_stmt|;
comment|/**      * {@code RepositoryPermission} instance that always returns {@code true}.      */
name|RepositoryPermission
name|ALL
init|=
operator|new
name|RepositoryPermission
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|long
name|repositoryPermissions
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
block|}
end_interface

end_unit

