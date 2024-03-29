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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|security
operator|.
name|authorization
operator|.
name|accesscontrol
operator|.
name|AccessControlImporter
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
name|accesscontrol
operator|.
name|AccessControlValidatorProvider
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
name|permission
operator|.
name|MountPermissionProvider
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
name|permission
operator|.
name|PermissionHook
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
name|permission
operator|.
name|PermissionProviderImpl
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
name|permission
operator|.
name|PermissionStoreValidatorProvider
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
name|permission
operator|.
name|PermissionValidatorProvider
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
name|permission
operator|.
name|VersionablePathHook
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
name|mount
operator|.
name|MountInfoProvider
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
name|PermissionConstants
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
name|xml
operator|.
name|ProtectedItemImporter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sling
operator|.
name|testing
operator|.
name|mock
operator|.
name|osgi
operator|.
name|junit
operator|.
name|OsgiContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|Map
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|PermissionConstants
operator|.
name|PARAM_ADMINISTRATIVE_PRINCIPALS
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|PermissionConstants
operator|.
name|PARAM_READ_PATHS
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
name|assertNull
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|AuthorizationConfigurationImplOSGiTest
extends|extends
name|AbstractSecurityTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|OsgiContext
name|context
init|=
operator|new
name|OsgiContext
argument_list|()
decl_stmt|;
specifier|private
name|AuthorizationConfigurationImpl
name|authorizationConfiguration
decl_stmt|;
specifier|private
name|MountInfoProvider
name|mip
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|authorizationConfiguration
operator|=
operator|new
name|AuthorizationConfigurationImpl
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
name|authorizationConfiguration
operator|.
name|setTreeProvider
argument_list|(
name|getTreeProvider
argument_list|()
argument_list|)
expr_stmt|;
name|authorizationConfiguration
operator|.
name|setRootProvider
argument_list|(
name|getRootProvider
argument_list|()
argument_list|)
expr_stmt|;
name|mip
operator|=
name|when
argument_list|(
name|mock
argument_list|(
name|MountInfoProvider
operator|.
name|class
argument_list|)
operator|.
name|hasNonDefaultMounts
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|getMock
argument_list|()
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|MountInfoProvider
operator|.
name|class
argument_list|,
name|mip
argument_list|)
expr_stmt|;
name|Map
name|options
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|PARAM_ADMINISTRATIVE_PRINCIPALS
argument_list|,
literal|"administrators"
argument_list|)
decl_stmt|;
name|context
operator|.
name|registerInjectActivateService
argument_list|(
name|authorizationConfiguration
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetParameters
parameter_list|()
block|{
name|ConfigurationParameters
name|params
init|=
name|authorizationConfiguration
operator|.
name|getParameters
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"administrators"
argument_list|,
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_ADMINISTRATIVE_PRINCIPALS
argument_list|,
literal|"undefined"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PermissionConstants
operator|.
name|DEFAULT_READ_PATHS
argument_list|,
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_READ_PATHS
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|)
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
name|authorizationConfiguration
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
name|testGetContext
parameter_list|()
block|{
name|assertSame
argument_list|(
name|AuthorizationContext
operator|.
name|getInstance
argument_list|()
argument_list|,
name|authorizationConfiguration
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
name|testGetWorkspaceInitializer
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|authorizationConfiguration
operator|.
name|getWorkspaceInitializer
argument_list|()
operator|instanceof
name|AuthorizationInitializer
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
name|Class
argument_list|>
name|expected
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|VersionablePathHook
operator|.
name|class
argument_list|,
name|PermissionHook
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|elementsEqual
argument_list|(
name|expected
argument_list|,
name|Iterables
operator|.
name|transform
argument_list|(
name|authorizationConfiguration
operator|.
name|getCommitHooks
argument_list|(
name|adminSession
operator|.
name|getWorkspaceName
argument_list|()
argument_list|)
argument_list|,
name|commitHook
lambda|->
name|commitHook
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
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
name|Class
argument_list|>
name|expected
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|PermissionStoreValidatorProvider
operator|.
name|class
argument_list|,
name|PermissionValidatorProvider
operator|.
name|class
argument_list|,
name|AccessControlValidatorProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|elementsEqual
argument_list|(
name|expected
argument_list|,
name|Iterables
operator|.
name|transform
argument_list|(
name|authorizationConfiguration
operator|.
name|getValidators
argument_list|(
name|adminSession
operator|.
name|getWorkspaceName
argument_list|()
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
argument_list|,
name|commitHook
lambda|->
name|commitHook
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
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
name|importers
init|=
name|authorizationConfiguration
operator|.
name|getProtectedItemImporters
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|importers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|importers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|AccessControlImporter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefaultMountInfoProvider
parameter_list|()
block|{
name|AuthorizationConfigurationImpl
name|ac
init|=
operator|new
name|AuthorizationConfigurationImpl
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
decl_stmt|;
name|ac
operator|.
name|setRootProvider
argument_list|(
name|getRootProvider
argument_list|()
argument_list|)
expr_stmt|;
name|ac
operator|.
name|setTreeProvider
argument_list|(
name|getTreeProvider
argument_list|()
argument_list|)
expr_stmt|;
name|PermissionProvider
name|pp
init|=
name|ac
operator|.
name|getPermissionProvider
argument_list|(
name|root
argument_list|,
name|adminSession
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|ImmutableSet
operator|.
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
name|PermissionProviderImpl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBindMountInfoProvider
parameter_list|()
block|{
name|PermissionProvider
name|pp
init|=
name|authorizationConfiguration
operator|.
name|getPermissionProvider
argument_list|(
name|root
argument_list|,
name|adminSession
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|ImmutableSet
operator|.
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
name|MountPermissionProvider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnbindMountInfoProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|authorizationConfiguration
operator|.
name|unbindMountInfoProvider
argument_list|(
name|mip
argument_list|)
expr_stmt|;
name|Field
name|f
init|=
name|AuthorizationConfigurationImpl
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"mountInfoProvider"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|f
operator|.
name|get
argument_list|(
name|authorizationConfiguration
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

