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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
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

begin_comment
comment|/**  * Predicate used to evaluation if a given {@code PermissionEntry} matches  * the specified tree, property or path.  */
end_comment

begin_interface
interface|interface
name|EntryPredicate
extends|extends
name|Predicate
argument_list|<
name|PermissionEntry
argument_list|>
block|{
annotation|@
name|Nullable
name|String
name|getPath
parameter_list|()
function_decl|;
specifier|default
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|PermissionEntry
name|entry
parameter_list|)
block|{
return|return
name|entry
operator|!=
literal|null
operator|&&
name|apply
argument_list|(
name|entry
argument_list|,
literal|true
argument_list|)
return|;
block|}
name|boolean
name|apply
parameter_list|(
annotation|@
name|NotNull
name|PermissionEntry
name|entry
parameter_list|,
name|boolean
name|respectParent
parameter_list|)
function_decl|;
specifier|static
name|EntryPredicate
name|create
parameter_list|()
block|{
return|return
operator|new
name|EntryPredicate
argument_list|()
block|{
annotation|@
name|Nullable
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
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|NotNull
name|PermissionEntry
name|entry
parameter_list|,
name|boolean
name|respectParent
parameter_list|)
block|{
return|return
name|entry
operator|.
name|matches
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|static
name|EntryPredicate
name|create
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|,
name|boolean
name|respectParent
parameter_list|)
block|{
name|Tree
name|parent
init|=
operator|(
operator|!
name|respectParent
operator|||
name|tree
operator|.
name|isRoot
argument_list|()
operator|)
condition|?
literal|null
else|:
name|tree
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|boolean
name|rp
init|=
name|respectParent
operator|&&
name|parent
operator|!=
literal|null
decl_stmt|;
return|return
operator|new
name|EntryPredicate
argument_list|()
block|{
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
name|apply
parameter_list|(
annotation|@
name|NotNull
name|PermissionEntry
name|entry
parameter_list|,
name|boolean
name|respectParent
parameter_list|)
block|{
name|respectParent
operator|&=
name|rp
expr_stmt|;
return|return
name|entry
operator|.
name|matches
argument_list|(
name|tree
argument_list|,
name|property
argument_list|)
operator|||
operator|(
name|respectParent
operator|&&
name|entry
operator|.
name|matches
argument_list|(
name|parent
argument_list|,
literal|null
argument_list|)
operator|)
return|;
block|}
block|}
return|;
block|}
specifier|static
name|EntryPredicate
name|create
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|,
name|boolean
name|respectParent
parameter_list|)
block|{
name|String
name|parentPath
init|=
operator|(
operator|!
name|respectParent
operator|||
name|PathUtils
operator|.
name|ROOT_PATH
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|)
condition|?
literal|null
else|:
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|boolean
name|rp
init|=
name|respectParent
operator|&&
name|parentPath
operator|!=
literal|null
decl_stmt|;
return|return
operator|new
name|EntryPredicate
argument_list|()
block|{
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
name|path
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|NotNull
name|PermissionEntry
name|entry
parameter_list|,
name|boolean
name|respectParent
parameter_list|)
block|{
name|respectParent
operator|&=
name|rp
expr_stmt|;
return|return
name|entry
operator|.
name|matches
argument_list|(
name|path
argument_list|)
operator|||
operator|(
name|respectParent
operator|&&
name|entry
operator|.
name|matches
argument_list|(
name|parentPath
argument_list|)
operator|)
return|;
block|}
block|}
return|;
block|}
block|}
end_interface

end_unit

