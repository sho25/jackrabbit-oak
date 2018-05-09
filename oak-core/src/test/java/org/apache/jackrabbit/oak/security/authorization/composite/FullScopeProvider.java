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
name|composite
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Session
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|commons
operator|.
name|PathUtils
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
name|PrivilegeConstants
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
comment|/**  * Test implementation of the {@code AggregatedPermissionProvider} with following  * characteristics:  *  * This provider supports all permissions  * but only grants {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.Permissions#NAMESPACE_MANAGEMENT} on repository level  * and {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.Permissions#READ_NODE} on regular items.  *  * In this case the provider will always be respected for evaluation and will  * therefore cause the final result to be always restricted to the permissions  * granted by this provider.  *  * NOTE: this provider implementation doesn't properly filter out access  * control content for which {@link Permissions#READ_ACCESS_CONTROL} must be  * enforced. this has been omitted here for the simplicity of the test.  */
end_comment

begin_class
class|class
name|FullScopeProvider
extends|extends
name|AbstractAggrProvider
implements|implements
name|PrivilegeConstants
block|{
name|FullScopeProvider
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|)
block|{
name|super
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
comment|//-------------------------------------------------< PermissionProvider>---
annotation|@
name|Nonnull
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
if|if
condition|(
name|tree
operator|==
literal|null
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_NAMESPACE_MANAGEMENT
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|REP_READ_NODES
argument_list|)
return|;
block|}
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
name|Nonnull
name|String
modifier|...
name|privilegeNames
parameter_list|)
block|{
if|if
condition|(
name|tree
operator|==
literal|null
condition|)
block|{
return|return
name|Arrays
operator|.
name|equals
argument_list|(
operator|new
name|String
index|[]
block|{
name|JCR_NAMESPACE_MANAGEMENT
block|}
argument_list|,
name|privilegeNames
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Arrays
operator|.
name|equals
argument_list|(
operator|new
name|String
index|[]
block|{
name|REP_READ_NODES
block|}
argument_list|,
name|privilegeNames
argument_list|)
return|;
block|}
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RepositoryPermission
name|getRepositoryPermission
parameter_list|()
block|{
return|return
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
name|Permissions
operator|.
name|NAMESPACE_MANAGEMENT
operator|==
name|repositoryPermissions
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|TreePermission
name|getTreePermission
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|TreePermission
name|parentPermission
parameter_list|)
block|{
return|return
operator|new
name|TestTreePermission
argument_list|(
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
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
block|{
return|return
name|property
operator|==
literal|null
operator|&&
name|permissions
operator|==
name|Permissions
operator|.
name|READ_NODE
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
annotation|@
name|Nonnull
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|String
name|jcrActions
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
return|return
name|tree
operator|.
name|exists
argument_list|()
operator|&&
name|Session
operator|.
name|ACTION_READ
operator|.
name|equals
argument_list|(
name|jcrActions
argument_list|)
return|;
block|}
comment|//---------------------------------------< AggregatedPermissionProvider>---
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
annotation|@
name|Nonnull
name|TreeLocation
name|location
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|permissions
operator|==
name|Permissions
operator|.
name|READ_NODE
return|;
block|}
comment|//--------------------------------------------------------------------------
specifier|private
specifier|final
class|class
name|TestTreePermission
implements|implements
name|TreePermission
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
name|TestTreePermission
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|TreePermission
name|getChildPermission
parameter_list|(
annotation|@
name|Nonnull
name|String
name|childName
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|childState
parameter_list|)
block|{
return|return
operator|new
name|TestTreePermission
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|childName
argument_list|)
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
name|Permissions
operator|.
name|READ_NODE
operator|==
name|permissions
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
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

