begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|api
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
operator|.
name|Status
import|;
end_import

begin_comment
comment|/**  * A {@code TreeLocation} denotes a location inside a tree.  * It can either refer to a inner node (that is a {@link org.apache.jackrabbit.oak.api.Tree})  * or to a leaf (that is a {@link org.apache.jackrabbit.oak.api.PropertyState}).  * {@code TreeLocation} instances provide methods for navigating trees. {@code TreeLocation}  * instances are immutable and navigating a tree always results in new {@code TreeLocation}  * instances. Navigation never fails. Errors are deferred until the underlying item itself is  * accessed. That is, if a {@code TreeLocation} points to an item which does not exist or  * is unavailable otherwise (i.e. due to access control restrictions) accessing the tree  * will return {@code null} at this point.  */
end_comment

begin_interface
specifier|public
interface|interface
name|TreeLocation
block|{
comment|/**      * This {@code TreeLocation} refers to an invalid location in a tree. That is      * to a location where no item resides.      */
name|TreeLocation
name|NULL
init|=
operator|new
name|TreeLocation
argument_list|()
block|{
comment|/**          * @return  {@code NULL}          */
annotation|@
name|Override
specifier|public
name|TreeLocation
name|getParent
parameter_list|()
block|{
return|return
name|NULL
return|;
block|}
comment|/**          * @return  {@code NULL}          */
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|TreeLocation
name|getChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|NULL
return|;
block|}
comment|/**          * @return  {@code NULL}          */
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|TreeLocation
name|getLocation
parameter_list|(
name|PathResolver
name|pathResolver
parameter_list|)
block|{
return|return
name|NULL
return|;
block|}
comment|/**          * @return  {@code null}          */
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**          * @return  {@code null}          */
annotation|@
name|Override
specifier|public
name|Tree
name|getTree
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**          * @return  {@code null}          */
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**          * @return  {@code null}          */
annotation|@
name|Override
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Navigate to the parent      * @return  a {@code TreeLocation} for the parent of this location.      */
annotation|@
name|Nonnull
name|TreeLocation
name|getParent
parameter_list|()
function_decl|;
comment|/**      * Retrieve the child location with the given name.      * @param name  name of the child      * @return  a {@code TreeLocation} for a child with the given {@code name}.      */
annotation|@
name|Nonnull
name|TreeLocation
name|getChild
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Navigate to a child through a {@code pathResolver}.      * @param pathResolver for the path to the location      * @return the tree location for {@code pathResolver}      */
annotation|@
name|Nonnull
name|TreeLocation
name|getLocation
parameter_list|(
name|PathResolver
name|pathResolver
parameter_list|)
function_decl|;
comment|/**      * Get the underlying {@link org.apache.jackrabbit.oak.api.Tree} for this {@code TreeLocation}.      * @return  underlying {@code Tree} instance or {@code null} if not available.      */
annotation|@
name|CheckForNull
name|Tree
name|getTree
parameter_list|()
function_decl|;
comment|/**      * Get the underlying {@link org.apache.jackrabbit.oak.api.PropertyState} for this {@code TreeLocation}.      * @return  underlying {@code PropertyState} instance or {@code null} if not available.      */
annotation|@
name|CheckForNull
name|PropertyState
name|getProperty
parameter_list|()
function_decl|;
comment|/**      * {@link org.apache.jackrabbit.oak.api.Tree.Status} of the underlying item or {@code null} if no      * such item exists.      * @return      */
annotation|@
name|CheckForNull
name|Status
name|getStatus
parameter_list|()
function_decl|;
comment|/**      * The path of the underlying item or {@code null} if no such item exists.      * @return  path      */
annotation|@
name|CheckForNull
name|String
name|getPath
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

