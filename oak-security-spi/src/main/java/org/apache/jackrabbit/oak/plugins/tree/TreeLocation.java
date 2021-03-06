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
name|plugins
operator|.
name|tree
package|;
end_package

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
name|commons
operator|.
name|PathUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
operator|.
name|toStringHelper
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|PathUtils
operator|.
name|elements
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|PathUtils
operator|.
name|isAbsolute
import|;
end_import

begin_comment
comment|/**  * A {@code TreeLocation} denotes a location inside a tree.  *<p>  * It can either refer to a inner node (that is a {@link Tree}), to a leaf (that is a  * {@link PropertyState}) or to an invalid location which refers to neither of the former.  * {@code TreeLocation} instances provide methods for navigating trees such that navigation  * always results in new {@code TreeLocation} instances. Navigation never fails. Errors are  * deferred until the underlying item itself is accessed. That is, if a {@code TreeLocation}  * points to an item which does not exist or is unavailable otherwise (i.e. due to access  * control restrictions) accessing the tree will return {@code null} at this point.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TreeLocation
block|{
comment|/**      * Create a new {@code TreeLocation} instance for a {@code tree}      */
annotation|@
name|NotNull
specifier|public
specifier|static
name|TreeLocation
name|create
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
block|{
return|return
operator|new
name|NodeLocation
argument_list|(
name|tree
argument_list|)
return|;
block|}
comment|/**      * Create a new {@code TreeLocation} instance for the item      * at the given {@code path} in {@code root}.      */
annotation|@
name|NotNull
specifier|public
specifier|static
name|TreeLocation
name|create
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|isAbsolute
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|TreeLocation
name|location
init|=
name|create
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|location
operator|=
name|location
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|location
return|;
block|}
comment|/**      * Equivalent to {@code create(root, "/")}      */
annotation|@
name|NotNull
specifier|public
specifier|static
name|TreeLocation
name|create
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|)
block|{
return|return
name|create
argument_list|(
name|root
argument_list|,
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
return|;
block|}
comment|/**      * Navigate to the parent or an invalid location for the root of the hierarchy.      * @return a {@code TreeLocation} for the parent of this location.      */
annotation|@
name|NotNull
specifier|public
specifier|abstract
name|TreeLocation
name|getParent
parameter_list|()
function_decl|;
comment|/**      * Determine whether the underlying {@link org.apache.jackrabbit.oak.api.Tree} or      * {@link org.apache.jackrabbit.oak.api.PropertyState} for this {@code TreeLocation}      * is available.      * @return  {@code true} if the underlying item is available and has not been disconnected.      * @see org.apache.jackrabbit.oak.api.Tree#exists()      */
specifier|public
specifier|abstract
name|boolean
name|exists
parameter_list|()
function_decl|;
comment|/**      * The name of this location      * @return name      */
annotation|@
name|NotNull
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * The path of this location      * @return  path      */
annotation|@
name|NotNull
specifier|public
specifier|abstract
name|String
name|getPath
parameter_list|()
function_decl|;
comment|/**      * Remove the underlying item.      *      * @return {@code true} if the item was removed, {@code false} otherwise.      */
specifier|public
specifier|abstract
name|boolean
name|remove
parameter_list|()
function_decl|;
comment|/**      * Navigate to a child of the given {@code name}.      * @param name  name of the child      * @return  this default implementation return a non existing location      */
annotation|@
name|NotNull
specifier|public
name|TreeLocation
name|getChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|NullLocation
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
comment|/**      * Get the underlying {@link org.apache.jackrabbit.oak.api.Tree} for this {@code TreeLocation}.      * @return  this default implementation return {@code null}.      */
annotation|@
name|Nullable
specifier|public
name|Tree
name|getTree
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Get the underlying {@link org.apache.jackrabbit.oak.api.PropertyState} for this {@code TreeLocation}.      * @return  this default implementation return {@code null}.      */
annotation|@
name|Nullable
specifier|public
name|PropertyState
name|getProperty
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|add
argument_list|(
literal|"path"
argument_list|,
name|getPath
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * This {@code TreeLocation} refers to child tree in a      * {@code Tree}.      */
specifier|private
specifier|static
class|class
name|NodeLocation
extends|extends
name|TreeLocation
block|{
specifier|private
specifier|final
name|Tree
name|tree
decl_stmt|;
specifier|public
name|NodeLocation
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
name|this
operator|.
name|tree
operator|=
name|tree
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|TreeLocation
name|getParent
parameter_list|()
block|{
return|return
name|tree
operator|.
name|isRoot
argument_list|()
condition|?
name|NullLocation
operator|.
name|NULL
else|:
operator|new
name|NodeLocation
argument_list|(
name|tree
operator|.
name|getParent
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|NotNull
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
if|if
condition|(
name|tree
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
operator|new
name|PropertyLocation
argument_list|(
name|tree
argument_list|,
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|NodeLocation
argument_list|(
name|tree
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
name|tree
operator|.
name|exists
argument_list|()
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|tree
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|Tree
name|getTree
parameter_list|()
block|{
return|return
name|exists
argument_list|()
condition|?
name|tree
else|:
literal|null
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|tree
operator|.
name|getPath
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|remove
parameter_list|()
block|{
return|return
name|exists
argument_list|()
operator|&&
name|tree
operator|.
name|remove
argument_list|()
return|;
block|}
block|}
comment|/**      * This {@code TreeLocation} refers to property in a      * {@code Tree}.      */
specifier|private
specifier|static
class|class
name|PropertyLocation
extends|extends
name|TreeLocation
block|{
specifier|private
specifier|final
name|Tree
name|parent
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|PropertyLocation
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|parent
parameter_list|,
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|TreeLocation
name|getParent
parameter_list|()
block|{
return|return
operator|new
name|NodeLocation
argument_list|(
name|parent
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
name|parent
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|()
block|{
return|return
name|parent
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|remove
parameter_list|()
block|{
name|parent
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|/**      * This {@code TreeLocation} refers to an invalid location in a tree. That is      * to a location where no item resides.      */
specifier|private
specifier|static
specifier|final
class|class
name|NullLocation
extends|extends
name|TreeLocation
block|{
specifier|public
specifier|static
specifier|final
name|NullLocation
name|NULL
init|=
operator|new
name|NullLocation
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|TreeLocation
name|parent
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|NullLocation
parameter_list|(
annotation|@
name|NotNull
name|TreeLocation
name|parent
parameter_list|,
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|private
name|NullLocation
parameter_list|()
block|{
name|this
operator|.
name|parent
operator|=
name|this
expr_stmt|;
name|this
operator|.
name|name
operator|=
literal|""
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|TreeLocation
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
comment|/**          * @return {@code false}          */
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|parent
operator|==
name|this
condition|?
literal|""
else|:
name|PathUtils
operator|.
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
comment|/**          * @return Always {@code false}.          */
annotation|@
name|Override
specifier|public
name|boolean
name|remove
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

