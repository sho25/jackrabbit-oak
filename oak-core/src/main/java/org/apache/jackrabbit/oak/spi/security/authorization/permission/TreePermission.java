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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * The {@code TreePermission} allow to evaluate permissions defined for a given  * {@code Tree} and it's properties.  *  * @see PermissionProvider#getTreePermission(org.apache.jackrabbit.oak.api.Tree, TreePermission)  */
end_comment

begin_interface
specifier|public
interface|interface
name|TreePermission
block|{
comment|/**      * Retrieve the {@code TreePermission} for the tree identified by the specified      * {@code childName} and {@code childState}, which is a child of the tree      * associated with this instanceof {@code TreePermission}.      *      * @param childName The oak name of the child.      * @param childState The child state.      * @return The tree permission for the child tree identified by {@code childName}      * and {@code childState}.      */
name|TreePermission
name|getChildPermission
parameter_list|(
name|String
name|childName
parameter_list|,
name|NodeState
name|childState
parameter_list|)
function_decl|;
comment|/**      * Return if read access is granted for the {@code Tree} associated with      * this {@code TreePermission} instance.      *      * @return {@code true} if the tree associated with this instance can be      * read; {@code false} otherwise.      */
name|boolean
name|canRead
parameter_list|()
function_decl|;
comment|/**      * Return if read access is granted for the property of the {@code Tree} for      * which this {@code TreePermission} instance has been created.      *      * @param property The property to be tested for read access.      * @return {@code true} If the specified property can be read; {@code false} otherwise.      */
name|boolean
name|canRead
parameter_list|(
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
function_decl|;
comment|/**      * Returns {@code true} if read access is granted to the {@code Tree} associated      * with this instance and the whole subtree defined by it including all      * properties. Note, that this includes access to items which require      * specific read permissions such as e.g. {@link Permissions#READ_ACCESS_CONTROL}.      *      * @return {@code true} if the {@code Tree} associated with this instance as      * well as its properties and the whole subtree can be read; {@code false} otherwise.      */
name|boolean
name|canReadAll
parameter_list|()
function_decl|;
comment|/**      * Returns {@code true} if all properties of the {@code Tree} associated with      * this instance can be read.      *      * @return {@code true} if all properties of the {@code Tree} associated with      * this instance can be read; {@code false} otherwise.      */
name|boolean
name|canReadProperties
parameter_list|()
function_decl|;
comment|/**      * Returns {@code true} if all specified permissions are granted on the      * {@code Tree} associated with this {@code TreePermission} instance;      * {@code false} otherwise.      *      * @param permissions The permissions to be tested. Note, that the implementation      * may restrict the set of valid permissions to those that can be set and      * evaluated for trees.      * @return {@code true} if all permissions are granted; {@code false} otherwise.      */
name|boolean
name|isGranted
parameter_list|(
name|long
name|permissions
parameter_list|)
function_decl|;
comment|/**      * Returns {@code true} if all specified permissions are granted on the      * {@code PropertyState} associated with this {@code TreePermission} instance;      * {@code false} otherwise.      *      * @param permissions The permissions to be tested. Note, that the implementation      * may restrict the set of valid permissions to those that can be set and      * evaluated for properties.      * @param property The property state for which the permissions must be granted.      * @return {@code true} if all permissions are granted; {@code false} otherwise.      */
name|boolean
name|isGranted
parameter_list|(
name|long
name|permissions
parameter_list|,
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
function_decl|;
comment|/**      * {@code TreePermission} which always returns {@code false} not granting      * any permissions.      */
name|TreePermission
name|EMPTY
init|=
operator|new
name|TreePermission
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TreePermission
name|getChildPermission
parameter_list|(
name|String
name|childName
parameter_list|,
name|NodeState
name|childState
parameter_list|)
block|{
return|return
name|EMPTY
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|(
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canReadAll
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canReadProperties
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|long
name|permissions
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|long
name|permissions
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
decl_stmt|;
comment|/**      * {@code TreePermission} which always returns {@code true} and thus grants      * all permissions.      */
name|TreePermission
name|ALL
init|=
operator|new
name|TreePermission
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TreePermission
name|getChildPermission
parameter_list|(
name|String
name|childName
parameter_list|,
name|NodeState
name|childState
parameter_list|)
block|{
return|return
name|ALL
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|(
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canReadAll
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canReadProperties
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|long
name|permissions
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|long
name|permissions
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
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

