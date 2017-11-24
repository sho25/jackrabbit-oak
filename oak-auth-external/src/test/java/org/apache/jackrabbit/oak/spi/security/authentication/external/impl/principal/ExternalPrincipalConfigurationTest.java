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
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|principal
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Dictionary
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
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
name|Iterators
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|UserManager
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
name|ContentSession
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
name|lifecycle
operator|.
name|WorkspaceInitializer
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
name|Context
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
name|authentication
operator|.
name|external
operator|.
name|AbstractExternalAuthTest
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
name|authentication
operator|.
name|external
operator|.
name|ExternalIdentityProvider
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
name|authentication
operator|.
name|external
operator|.
name|SyncContext
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
name|authentication
operator|.
name|external
operator|.
name|SyncException
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
name|authentication
operator|.
name|external
operator|.
name|SyncHandler
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
name|authentication
operator|.
name|external
operator|.
name|SyncedIdentity
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
name|authentication
operator|.
name|external
operator|.
name|basic
operator|.
name|DefaultSyncConfig
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
name|authentication
operator|.
name|external
operator|.
name|basic
operator|.
name|DefaultSyncContext
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
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|DefaultSyncConfigImpl
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
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|DefaultSyncHandler
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
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|ExternalIdentityConstants
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
name|PrincipalConfiguration
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
name|PrincipalProvider
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
name|Ignore
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
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|ServiceRegistration
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
name|assertFalse
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
name|assertNotNull
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
name|ExternalPrincipalConfigurationTest
extends|extends
name|AbstractExternalAuthTest
block|{
specifier|private
name|void
name|enable
parameter_list|()
block|{
name|context
operator|.
name|registerService
argument_list|(
name|SyncHandler
operator|.
name|class
argument_list|,
operator|new
name|DefaultSyncHandler
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|of
argument_list|(
name|DefaultSyncConfigImpl
operator|.
name|PARAM_USER_DYNAMIC_MEMBERSHIP
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertIsEnabled
parameter_list|(
name|ExternalPrincipalConfiguration
name|externalPrincipalConfiguration
parameter_list|,
name|boolean
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|PrincipalProvider
name|pp
init|=
name|externalPrincipalConfiguration
operator|.
name|getPrincipalProvider
argument_list|(
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|pp
operator|instanceof
name|ExternalGroupPrincipalProvider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalManager
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|externalPrincipalConfiguration
operator|.
name|getPrincipalManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalManagerEnabled
parameter_list|()
block|{
name|enable
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|externalPrincipalConfiguration
operator|.
name|getPrincipalManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|PrincipalProvider
name|pp
init|=
name|externalPrincipalConfiguration
operator|.
name|getPrincipalProvider
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|pp
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|instanceof
name|ExternalGroupPrincipalProvider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalProviderEnabled
parameter_list|()
block|{
name|enable
argument_list|()
expr_stmt|;
name|PrincipalProvider
name|pp
init|=
name|externalPrincipalConfiguration
operator|.
name|getPrincipalProvider
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|pp
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|instanceof
name|ExternalGroupPrincipalProvider
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
name|PrincipalConfiguration
operator|.
name|NAME
argument_list|,
name|externalPrincipalConfiguration
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|enable
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|PrincipalConfiguration
operator|.
name|NAME
argument_list|,
name|externalPrincipalConfiguration
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
name|Context
operator|.
name|DEFAULT
argument_list|,
name|externalPrincipalConfiguration
operator|.
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|enable
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
name|Context
operator|.
name|DEFAULT
argument_list|,
name|externalPrincipalConfiguration
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
name|assertSame
argument_list|(
name|WorkspaceInitializer
operator|.
name|DEFAULT
argument_list|,
name|externalPrincipalConfiguration
operator|.
name|getWorkspaceInitializer
argument_list|()
argument_list|)
expr_stmt|;
name|enable
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
name|WorkspaceInitializer
operator|.
name|DEFAULT
argument_list|,
name|externalPrincipalConfiguration
operator|.
name|getWorkspaceInitializer
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetRepositoryInitializer
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|externalPrincipalConfiguration
operator|.
name|getRepositoryInitializer
argument_list|()
operator|instanceof
name|ExternalIdentityRepositoryInitializer
argument_list|)
expr_stmt|;
name|enable
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|externalPrincipalConfiguration
operator|.
name|getRepositoryInitializer
argument_list|()
operator|instanceof
name|ExternalIdentityRepositoryInitializer
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
name|ContentSession
name|cs
init|=
name|root
operator|.
name|getContentSession
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|?
extends|extends
name|ValidatorProvider
argument_list|>
name|validatorProviders
init|=
name|externalPrincipalConfiguration
operator|.
name|getValidators
argument_list|(
name|cs
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|cs
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
operator|new
name|MoveTracker
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|validatorProviders
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|validatorProviders
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|validatorProviders
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|ExternalIdentityValidatorProvider
argument_list|)
expr_stmt|;
name|validatorProviders
operator|=
name|externalPrincipalConfiguration
operator|.
name|getValidators
argument_list|(
name|cs
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|cs
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
operator|new
name|MoveTracker
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|validatorProviders
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|validatorProviders
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|validatorProviders
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|ExternalIdentityValidatorProvider
argument_list|)
expr_stmt|;
name|enable
argument_list|()
expr_stmt|;
name|validatorProviders
operator|=
name|externalPrincipalConfiguration
operator|.
name|getValidators
argument_list|(
name|cs
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|cs
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
operator|new
name|MoveTracker
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|validatorProviders
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|validatorProviders
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|validatorProviders
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|ExternalIdentityValidatorProvider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetValidatorsOmitIdProtection
parameter_list|()
throws|throws
name|Exception
block|{
name|externalPrincipalConfiguration
operator|.
name|setParameters
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|ExternalIdentityConstants
operator|.
name|PARAM_PROTECT_EXTERNAL_IDS
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|ContentSession
name|cs
init|=
name|root
operator|.
name|getContentSession
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|?
extends|extends
name|ValidatorProvider
argument_list|>
name|validatorProviders
init|=
name|externalPrincipalConfiguration
operator|.
name|getValidators
argument_list|(
name|cs
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|cs
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
operator|new
name|MoveTracker
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|validatorProviders
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|validatorProviders
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|validatorProviders
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|ExternalIdentityValidatorProvider
argument_list|)
expr_stmt|;
name|enable
argument_list|()
expr_stmt|;
name|validatorProviders
operator|=
name|externalPrincipalConfiguration
operator|.
name|getValidators
argument_list|(
name|cs
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|cs
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
operator|new
name|MoveTracker
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|validatorProviders
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|validatorProviders
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|validatorProviders
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|ExternalIdentityValidatorProvider
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
name|?
extends|extends
name|ProtectedItemImporter
argument_list|>
name|importers
init|=
name|externalPrincipalConfiguration
operator|.
name|getProtectedItemImporters
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|importers
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
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
name|ExternalIdentityImporter
argument_list|)
expr_stmt|;
name|enable
argument_list|()
expr_stmt|;
name|importers
operator|=
name|externalPrincipalConfiguration
operator|.
name|getProtectedItemImporters
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|importers
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
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
name|ExternalIdentityImporter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddingSyncHandler
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|enableProps
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
name|DefaultSyncConfigImpl
operator|.
name|PARAM_USER_DYNAMIC_MEMBERSHIP
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|disableProps
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
name|DefaultSyncConfigImpl
operator|.
name|PARAM_USER_DYNAMIC_MEMBERSHIP
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SyncHandler
name|sh
init|=
operator|new
name|DefaultSyncHandler
argument_list|()
decl_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|SyncHandler
operator|.
name|class
argument_list|,
name|sh
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|assertIsEnabled
argument_list|(
name|externalPrincipalConfiguration
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|SyncHandler
operator|.
name|class
argument_list|,
name|sh
argument_list|,
name|disableProps
argument_list|)
expr_stmt|;
name|assertIsEnabled
argument_list|(
name|externalPrincipalConfiguration
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|SyncHandler
operator|.
name|class
argument_list|,
name|sh
argument_list|,
name|enableProps
argument_list|)
expr_stmt|;
name|assertIsEnabled
argument_list|(
name|externalPrincipalConfiguration
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|DefaultSyncHandler
operator|.
name|class
argument_list|,
operator|new
name|DefaultSyncHandler
argument_list|()
argument_list|,
name|enableProps
argument_list|)
expr_stmt|;
name|assertIsEnabled
argument_list|(
name|externalPrincipalConfiguration
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddingCustomSyncHandler
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|enableProps
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
name|DefaultSyncConfigImpl
operator|.
name|PARAM_USER_DYNAMIC_MEMBERSHIP
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SyncHandler
name|sh
init|=
operator|new
name|TestSyncHandler
argument_list|()
decl_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|SyncHandler
operator|.
name|class
argument_list|,
name|sh
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|assertIsEnabled
argument_list|(
name|externalPrincipalConfiguration
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|SyncHandler
operator|.
name|class
argument_list|,
name|sh
argument_list|,
name|enableProps
argument_list|)
expr_stmt|;
name|assertIsEnabled
argument_list|(
name|externalPrincipalConfiguration
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"TODO: mock doesn't reflect property-changes on the registration."
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testModifySyncHandler
parameter_list|()
throws|throws
name|Exception
block|{
name|Dictionary
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|enableProps
init|=
operator|new
name|Hashtable
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|of
argument_list|(
name|DefaultSyncConfigImpl
operator|.
name|PARAM_USER_DYNAMIC_MEMBERSHIP
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|Dictionary
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|disableProps
init|=
operator|new
name|Hashtable
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|of
argument_list|(
name|DefaultSyncConfigImpl
operator|.
name|PARAM_USER_DYNAMIC_MEMBERSHIP
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|DefaultSyncHandler
name|sh
init|=
operator|new
name|DefaultSyncHandler
argument_list|()
decl_stmt|;
name|BundleContext
name|bundleContext
init|=
name|context
operator|.
name|bundleContext
argument_list|()
decl_stmt|;
name|ServiceRegistration
name|registration
init|=
name|bundleContext
operator|.
name|registerService
argument_list|(
name|DefaultSyncHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|sh
argument_list|,
name|disableProps
argument_list|)
decl_stmt|;
name|assertIsEnabled
argument_list|(
name|externalPrincipalConfiguration
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|registration
operator|.
name|setProperties
argument_list|(
name|enableProps
argument_list|)
expr_stmt|;
name|assertIsEnabled
argument_list|(
name|externalPrincipalConfiguration
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|registration
operator|.
name|setProperties
argument_list|(
name|disableProps
argument_list|)
expr_stmt|;
name|assertIsEnabled
argument_list|(
name|externalPrincipalConfiguration
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveSyncHandler
parameter_list|()
throws|throws
name|Exception
block|{
name|Dictionary
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|enableProps
init|=
operator|new
name|Hashtable
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|of
argument_list|(
name|DefaultSyncConfigImpl
operator|.
name|PARAM_USER_DYNAMIC_MEMBERSHIP
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|Dictionary
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|disableProps
init|=
operator|new
name|Hashtable
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|of
argument_list|(
name|DefaultSyncConfigImpl
operator|.
name|PARAM_USER_DYNAMIC_MEMBERSHIP
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|DefaultSyncHandler
name|sh
init|=
operator|new
name|DefaultSyncHandler
argument_list|()
decl_stmt|;
name|BundleContext
name|bundleContext
init|=
name|context
operator|.
name|bundleContext
argument_list|()
decl_stmt|;
name|ServiceRegistration
name|registration1
init|=
name|bundleContext
operator|.
name|registerService
argument_list|(
name|SyncHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|sh
argument_list|,
name|enableProps
argument_list|)
decl_stmt|;
name|ServiceRegistration
name|registration2
init|=
name|bundleContext
operator|.
name|registerService
argument_list|(
name|SyncHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|sh
argument_list|,
name|enableProps
argument_list|)
decl_stmt|;
name|ServiceRegistration
name|registration3
init|=
name|bundleContext
operator|.
name|registerService
argument_list|(
name|SyncHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|sh
argument_list|,
name|disableProps
argument_list|)
decl_stmt|;
name|assertIsEnabled
argument_list|(
name|externalPrincipalConfiguration
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|registration2
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|assertIsEnabled
argument_list|(
name|externalPrincipalConfiguration
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|registration1
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|assertIsEnabled
argument_list|(
name|externalPrincipalConfiguration
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|registration3
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|assertIsEnabled
argument_list|(
name|externalPrincipalConfiguration
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TestSyncHandler
implements|implements
name|SyncHandler
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"name"
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|SyncContext
name|createContext
parameter_list|(
annotation|@
name|Nonnull
name|ExternalIdentityProvider
name|idp
parameter_list|,
annotation|@
name|Nonnull
name|UserManager
name|userManager
parameter_list|,
annotation|@
name|Nonnull
name|ValueFactory
name|valueFactory
parameter_list|)
throws|throws
name|SyncException
block|{
return|return
operator|new
name|DefaultSyncContext
argument_list|(
operator|new
name|DefaultSyncConfig
argument_list|()
argument_list|,
name|idp
argument_list|,
name|userManager
argument_list|,
name|valueFactory
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SyncedIdentity
name|findIdentity
parameter_list|(
annotation|@
name|Nonnull
name|UserManager
name|userManager
parameter_list|,
annotation|@
name|Nonnull
name|String
name|id
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|requiresSync
parameter_list|(
annotation|@
name|Nonnull
name|SyncedIdentity
name|identity
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|SyncedIdentity
argument_list|>
name|listIdentities
parameter_list|(
annotation|@
name|Nonnull
name|UserManager
name|userManager
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|Iterators
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

