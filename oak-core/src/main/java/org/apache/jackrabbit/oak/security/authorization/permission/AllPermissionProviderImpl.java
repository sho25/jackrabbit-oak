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
name|java
operator|.
name|util
operator|.
name|Collections
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
name|plugins
operator|.
name|tree
operator|.
name|TreeLocation
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
name|security
operator|.
name|authorization
operator|.
name|ProviderCtx
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
name|AggregatedPermissionProvider
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
name|RepositoryPermission
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
name|security
operator|.
name|privilege
operator|.
name|PrivilegeBits
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
name|privilege
operator|.
name|PrivilegeBitsProvider
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
name|privilege
operator|.
name|PrivilegeConstants
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
comment|/**  * Implementation of the {@code PermissionProvider} interface that grants full  * permission everywhere.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|AllPermissionProviderImpl
implements|implements
name|PermissionProvider
implements|,
name|AggregatedPermissionProvider
block|{
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|ALL
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|private
specifier|final
name|ProviderCtx
name|providerCtx
decl_stmt|;
specifier|private
name|Root
name|immutableRoot
decl_stmt|;
specifier|public
name|AllPermissionProviderImpl
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|ProviderCtx
name|providerCtx
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|providerCtx
operator|=
name|providerCtx
expr_stmt|;
name|this
operator|.
name|immutableRoot
operator|=
name|providerCtx
operator|.
name|getRootProvider
argument_list|()
operator|.
name|createReadOnlyRoot
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|refresh
parameter_list|()
block|{
name|immutableRoot
operator|=
name|providerCtx
operator|.
name|getRootProvider
argument_list|()
operator|.
name|createReadOnlyRoot
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
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
block|{
return|return
name|ALL
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPrivileges
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|,
annotation|@
name|NotNull
name|String
modifier|...
name|privilegeNames
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RepositoryPermission
name|getRepositoryPermission
parameter_list|()
block|{
return|return
name|RepositoryPermission
operator|.
name|ALL
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|TreePermission
name|getTreePermission
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|,
annotation|@
name|NotNull
name|TreePermission
name|parentPermission
parameter_list|)
block|{
return|return
name|TreePermission
operator|.
name|ALL
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
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
annotation|@
name|NotNull
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|String
name|jcrActions
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
comment|//---------------------------------------< AggregatedPermissionProvider>---
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|PrivilegeBits
name|supportedPrivileges
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PrivilegeBits
name|privilegeBits
parameter_list|)
block|{
return|return
operator|(
name|privilegeBits
operator|!=
literal|null
operator|)
condition|?
name|privilegeBits
else|:
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|immutableRoot
argument_list|)
operator|.
name|getBits
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|supportedPermissions
parameter_list|(
annotation|@
name|Nullable
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
block|{
return|return
name|permissions
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|supportedPermissions
parameter_list|(
annotation|@
name|NotNull
name|TreeLocation
name|location
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|permissions
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|supportedPermissions
parameter_list|(
annotation|@
name|NotNull
name|TreePermission
name|treePermission
parameter_list|,
name|PropertyState
name|property
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|permissions
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
annotation|@
name|NotNull
name|TreeLocation
name|location
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|TreePermission
name|getTreePermission
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
parameter_list|,
annotation|@
name|NotNull
name|TreePermission
name|parentPermission
parameter_list|)
block|{
return|return
name|TreePermission
operator|.
name|ALL
return|;
block|}
block|}
end_class

end_unit
