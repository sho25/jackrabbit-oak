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
name|cug
operator|.
name|impl
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
name|List
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
name|jcr
operator|.
name|security
operator|.
name|AccessControlManager
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
name|ImmutableList
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
name|ImmutableMap
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
name|AbstractSecurityTest
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
name|namepath
operator|.
name|NamePathMapper
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
name|commit
operator|.
name|CommitHook
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
name|commit
operator|.
name|MoveTracker
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
name|commit
operator|.
name|ValidatorProvider
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
name|ConfigurationParameters
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
name|SecurityProvider
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
name|AuthorizationConfiguration
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
name|EmptyPermissionProvider
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
name|restriction
operator|.
name|RestrictionProvider
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
name|EveryonePrincipal
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
name|SystemPrincipal
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
name|SystemUserPrincipal
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
name|xml
operator|.
name|ProtectedItemImporter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertSame
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|CugConfigurationTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|static
name|CugConfiguration
name|createConfiguration
parameter_list|(
name|ConfigurationParameters
name|params
parameter_list|)
block|{
name|SecurityProvider
name|sp
init|=
operator|new
name|CugSecurityProvider
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|AuthorizationConfiguration
operator|.
name|NAME
argument_list|,
name|params
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|CugConfiguration
argument_list|(
name|sp
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyConstructor
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
operator|new
name|CugConfiguration
argument_list|()
operator|.
name|getParameters
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetName
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|AuthorizationConfiguration
operator|.
name|NAME
argument_list|,
operator|new
name|CugConfiguration
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetRestrictionProvider
parameter_list|()
block|{
name|assertSame
argument_list|(
name|RestrictionProvider
operator|.
name|EMPTY
argument_list|,
operator|new
name|CugConfiguration
argument_list|()
operator|.
name|getRestrictionProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetCommitHooks
parameter_list|()
block|{
name|List
argument_list|<
name|?
extends|extends
name|CommitHook
argument_list|>
name|l
init|=
operator|new
name|CugConfiguration
argument_list|()
operator|.
name|getCommitHooks
argument_list|(
literal|"wspName"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|l
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|instanceof
name|NestedCugHook
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetValidators
parameter_list|()
block|{
name|List
argument_list|<
name|?
extends|extends
name|ValidatorProvider
argument_list|>
name|l
init|=
operator|new
name|CugConfiguration
argument_list|()
operator|.
name|getValidators
argument_list|(
literal|"wspName"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
operator|new
name|MoveTracker
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|l
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|instanceof
name|CugValidatorProvider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetProtectedItemImporters
parameter_list|()
block|{
name|List
argument_list|<
name|ProtectedItemImporter
argument_list|>
name|l
init|=
operator|new
name|CugConfiguration
argument_list|()
operator|.
name|getProtectedItemImporters
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|l
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|instanceof
name|CugImporter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetContext
parameter_list|()
block|{
name|assertSame
argument_list|(
name|CugContext
operator|.
name|INSTANCE
argument_list|,
operator|new
name|CugConfiguration
argument_list|()
operator|.
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPermissionProviderDisabled
parameter_list|()
block|{
name|CugConfiguration
name|cc
init|=
name|createConfiguration
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_ENABLED
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|PermissionProvider
name|pp
init|=
name|cc
operator|.
name|getPermissionProvider
argument_list|(
name|root
argument_list|,
name|root
operator|.
name|getContentSession
argument_list|()
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|EmptyPermissionProvider
operator|.
name|getInstance
argument_list|()
argument_list|,
name|pp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPermissionProviderDisabled2
parameter_list|()
block|{
name|ConfigurationParameters
name|params
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_ENABLED
argument_list|,
literal|false
argument_list|,
name|CugConstants
operator|.
name|PARAM_CUG_SUPPORTED_PATHS
argument_list|,
literal|"/content"
argument_list|)
decl_stmt|;
name|CugConfiguration
name|cc
init|=
name|createConfiguration
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|PermissionProvider
name|pp
init|=
name|cc
operator|.
name|getPermissionProvider
argument_list|(
name|root
argument_list|,
literal|"default"
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|EmptyPermissionProvider
operator|.
name|getInstance
argument_list|()
argument_list|,
name|pp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPermissionProviderDisabled3
parameter_list|()
block|{
name|CugConfiguration
name|cc
init|=
name|createConfiguration
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|PermissionProvider
name|pp
init|=
name|cc
operator|.
name|getPermissionProvider
argument_list|(
name|root
argument_list|,
literal|"default"
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|EmptyPermissionProvider
operator|.
name|getInstance
argument_list|()
argument_list|,
name|pp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPermissionProviderNoSupportedPaths
parameter_list|()
block|{
comment|// enabled but no supported paths specified
name|CugConfiguration
name|cc
init|=
name|createConfiguration
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_ENABLED
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|PermissionProvider
name|pp
init|=
name|cc
operator|.
name|getPermissionProvider
argument_list|(
name|root
argument_list|,
literal|"default"
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|EmptyPermissionProvider
operator|.
name|getInstance
argument_list|()
argument_list|,
name|pp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPermissionProviderSupportedPaths
parameter_list|()
block|{
name|ConfigurationParameters
name|params
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_ENABLED
argument_list|,
literal|true
argument_list|,
name|CugConstants
operator|.
name|PARAM_CUG_SUPPORTED_PATHS
argument_list|,
literal|"/content"
argument_list|)
decl_stmt|;
name|CugConfiguration
name|cc
init|=
name|createConfiguration
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|PermissionProvider
name|pp
init|=
name|cc
operator|.
name|getPermissionProvider
argument_list|(
name|root
argument_list|,
literal|"default"
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|instanceof
name|CugPermissionProvider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAccessControlManagerDisabled
parameter_list|()
block|{
name|CugConfiguration
name|cc
init|=
name|createConfiguration
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_ENABLED
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|cc
operator|.
name|getAccessControlManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|acMgr
operator|instanceof
name|CugAccessControlManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAccessControlManagerNoSupportedPaths
parameter_list|()
block|{
name|CugConfiguration
name|cc
init|=
name|createConfiguration
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_ENABLED
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|cc
operator|.
name|getAccessControlManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|acMgr
operator|instanceof
name|CugAccessControlManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAccessControlManagerSupportedPaths
parameter_list|()
block|{
name|ConfigurationParameters
name|params
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_ENABLED
argument_list|,
literal|true
argument_list|,
name|CugConstants
operator|.
name|PARAM_CUG_SUPPORTED_PATHS
argument_list|,
literal|"/content"
argument_list|)
decl_stmt|;
name|CugConfiguration
name|cc
init|=
name|createConfiguration
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|cc
operator|.
name|getAccessControlManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|acMgr
operator|instanceof
name|CugAccessControlManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExcludedPrincipals
parameter_list|()
block|{
name|ConfigurationParameters
name|params
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_ENABLED
argument_list|,
literal|true
argument_list|,
name|CugConstants
operator|.
name|PARAM_CUG_SUPPORTED_PATHS
argument_list|,
literal|"/content"
argument_list|)
decl_stmt|;
name|CugConfiguration
name|cc
init|=
name|createConfiguration
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Principal
argument_list|>
name|excluded
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|SystemPrincipal
operator|.
name|INSTANCE
argument_list|,
operator|new
name|AdminPrincipal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"admin"
return|;
block|}
block|}
argument_list|,
operator|new
name|SystemUserPrincipal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"systemUser"
return|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|Principal
name|p
range|:
name|excluded
control|)
block|{
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|p
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
decl_stmt|;
name|PermissionProvider
name|pp
init|=
name|cc
operator|.
name|getPermissionProvider
argument_list|(
name|root
argument_list|,
literal|"default"
argument_list|,
name|principals
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|EmptyPermissionProvider
operator|.
name|getInstance
argument_list|()
argument_list|,
name|pp
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testActivate
parameter_list|()
throws|throws
name|Exception
block|{
name|CugConfiguration
name|cugConfiguration
init|=
operator|new
name|CugConfiguration
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
decl_stmt|;
name|cugConfiguration
operator|.
name|activate
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_ENABLED
argument_list|,
literal|false
argument_list|,
name|CugConstants
operator|.
name|PARAM_CUG_SUPPORTED_PATHS
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/content"
block|,
literal|"/anotherContent"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertSupportedPaths
argument_list|(
name|cugConfiguration
argument_list|,
literal|"/content"
argument_list|,
literal|"/anotherContent"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModified
parameter_list|()
throws|throws
name|Exception
block|{
name|CugConfiguration
name|cugConfiguration
init|=
operator|new
name|CugConfiguration
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
decl_stmt|;
name|cugConfiguration
operator|.
name|modified
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_SUPPORTED_PATHS
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/changed"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertSupportedPaths
argument_list|(
name|cugConfiguration
argument_list|,
literal|"/changed"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertSupportedPaths
parameter_list|(
annotation|@
name|Nonnull
name|CugConfiguration
name|configuration
parameter_list|,
annotation|@
name|Nonnull
name|String
modifier|...
name|paths
parameter_list|)
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|paths
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|configuration
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_SUPPORTED_PATHS
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

