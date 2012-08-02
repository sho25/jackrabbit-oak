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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

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
name|CompiledPermissions
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
name|AccessControlContext
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
name|principal
operator|.
name|AdminPrincipal
import|;
end_import

begin_comment
comment|/**  * PermissionProviderImpl... TODO  */
end_comment

begin_class
specifier|public
class|class
name|AccessControlContextImpl
implements|implements
name|AccessControlContext
block|{
specifier|private
specifier|static
specifier|final
name|CompiledPermissions
name|NO_PERMISSIONS
init|=
operator|new
name|SimplePermissions
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|CompiledPermissions
name|ADMIN_PERMISSIONS
init|=
operator|new
name|SimplePermissions
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
name|this
operator|.
name|principals
operator|=
name|principals
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|CompiledPermissions
name|getPermissions
parameter_list|()
block|{
if|if
condition|(
name|principals
operator|==
literal|null
operator|||
name|principals
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|NO_PERMISSIONS
return|;
block|}
elseif|else
if|if
condition|(
name|principals
operator|.
name|contains
argument_list|(
name|AdminPrincipal
operator|.
name|INSTANCE
argument_list|)
condition|)
block|{
return|return
name|ADMIN_PERMISSIONS
return|;
block|}
else|else
block|{
comment|// TODO: replace with permissions based on ac evaluation
return|return
operator|new
name|CompiledPermissionImpl
argument_list|(
name|principals
argument_list|)
return|;
block|}
block|}
comment|/**      * Trivial implementation of the {@code CompiledPermissions} interface that      * either allows or denies all permissions.      */
specifier|private
specifier|static
specifier|final
class|class
name|SimplePermissions
implements|implements
name|CompiledPermissions
block|{
specifier|private
specifier|final
name|boolean
name|allowed
decl_stmt|;
specifier|private
name|SimplePermissions
parameter_list|(
name|boolean
name|allowed
parameter_list|)
block|{
name|this
operator|.
name|allowed
operator|=
name|allowed
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|isProperty
parameter_list|)
block|{
return|return
name|allowed
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|isProperty
parameter_list|,
name|int
name|permissions
parameter_list|)
block|{
return|return
name|allowed
return|;
block|}
block|}
block|}
end_class

end_unit

