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
comment|/**      * Navigate to the parent      * @return  a {@code TreeLocation} for the parent of this location.      */
annotation|@
name|Nonnull
name|TreeLocation
name|getParent
parameter_list|()
function_decl|;
comment|/**      * Navigate to a child through a relative path. A relative path consists of a      * possibly empty lists of names separated by forward slashes.      * @param relPath  relative path to the child      * @return  a {@code TreeLocation} for a child with the given {@code name}.      */
annotation|@
name|Nonnull
name|TreeLocation
name|getChild
parameter_list|(
name|String
name|relPath
parameter_list|)
function_decl|;
comment|/**      * Determine whether the underlying {@link org.apache.jackrabbit.oak.api.Tree} or      * {@link org.apache.jackrabbit.oak.api.PropertyState} for this {@code TreeLocation}      * is available.      * @return  {@code true} if either {@link #getTree()} or {@link #getParent()} is non {@code null}.      * {@code false} otherwise.      */
name|boolean
name|exists
parameter_list|()
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
comment|/**      * Remove the underlying item.      *      * @return {@code true} if the item was removed, {@code false} otherwise.      */
name|boolean
name|remove
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

