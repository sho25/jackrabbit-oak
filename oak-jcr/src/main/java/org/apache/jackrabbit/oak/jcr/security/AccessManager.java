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
name|jcr
operator|.
name|security
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
name|javax
operator|.
name|jcr
operator|.
name|AccessDeniedException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|jcr
operator|.
name|delegate
operator|.
name|SessionDelegate
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
name|jcr
operator|.
name|session
operator|.
name|operation
operator|.
name|SessionOperation
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
name|PermissionProvider
import|;
end_import

begin_comment
comment|/**  * AccessManager  */
end_comment

begin_class
specifier|public
class|class
name|AccessManager
block|{
specifier|private
specifier|final
name|SessionDelegate
name|delegate
decl_stmt|;
specifier|private
specifier|final
name|PermissionProvider
name|permissionProvider
decl_stmt|;
specifier|public
name|AccessManager
parameter_list|(
name|SessionDelegate
name|delegate
parameter_list|,
name|PermissionProvider
name|permissionProvider
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|permissionProvider
operator|=
name|permissionProvider
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasPermissions
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|String
name|actions
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|safePerform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|(
literal|"hasPermissions"
argument_list|)
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
block|{
return|return
name|permissionProvider
operator|.
name|isGranted
argument_list|(
name|oakPath
argument_list|,
name|actions
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|hasPermissions
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|PropertyState
name|property
parameter_list|,
specifier|final
name|long
name|permissions
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|delegate
operator|.
name|safePerform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|(
literal|"hasPermissions"
argument_list|)
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
block|{
return|return
name|permissionProvider
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
argument_list|)
return|;
block|}
specifier|public
name|void
name|checkPermissions
parameter_list|(
annotation|@
name|Nonnull
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|String
name|actions
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|hasPermissions
argument_list|(
name|oakPath
argument_list|,
name|actions
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AccessDeniedException
argument_list|(
literal|"Access denied."
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|checkPermissions
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
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|hasPermissions
argument_list|(
name|tree
argument_list|,
name|property
argument_list|,
name|permissions
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AccessDeniedException
argument_list|(
literal|"Access denied."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

