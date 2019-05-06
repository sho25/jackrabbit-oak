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
name|principalbased
operator|.
name|impl
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
name|Permissions
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

begin_class
specifier|abstract
class|class
name|AbstractTreePermission
implements|implements
name|TreePermission
block|{
specifier|private
specifier|final
name|Tree
name|tree
decl_stmt|;
specifier|private
specifier|final
name|TreeType
name|type
decl_stmt|;
name|AbstractTreePermission
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|,
annotation|@
name|NotNull
name|TreeType
name|type
parameter_list|)
block|{
name|this
operator|.
name|tree
operator|=
name|tree
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|abstract
name|PrincipalBasedPermissionProvider
name|getPermissionProvider
parameter_list|()
function_decl|;
annotation|@
name|NotNull
name|Tree
name|getTree
parameter_list|()
block|{
return|return
name|tree
return|;
block|}
annotation|@
name|NotNull
name|TreeType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|NotNull
name|TreePermission
name|getChildPermission
parameter_list|(
annotation|@
name|NotNull
name|String
name|childName
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|childState
parameter_list|)
block|{
return|return
name|getPermissionProvider
argument_list|()
operator|.
name|getTreePermission
argument_list|(
name|childName
argument_list|,
name|childState
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|()
block|{
name|long
name|permission
init|=
operator|(
name|type
operator|==
name|TreeType
operator|.
name|ACCESS_CONTROL
operator|)
condition|?
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
else|:
name|Permissions
operator|.
name|READ_NODE
decl_stmt|;
return|return
name|getPermissionProvider
argument_list|()
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|,
name|permission
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|(
annotation|@
name|NotNull
name|PropertyState
name|property
parameter_list|)
block|{
name|long
name|permission
init|=
operator|(
name|type
operator|==
name|TreeType
operator|.
name|ACCESS_CONTROL
operator|)
condition|?
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
else|:
name|Permissions
operator|.
name|READ_PROPERTY
decl_stmt|;
return|return
name|getPermissionProvider
argument_list|()
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
name|property
argument_list|,
name|permission
argument_list|)
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
name|getPermissionProvider
argument_list|()
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|,
name|permissions
argument_list|)
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
name|NotNull
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|getPermissionProvider
argument_list|()
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
name|property
argument_list|,
name|permissions
argument_list|)
return|;
block|}
block|}
end_class

end_unit
