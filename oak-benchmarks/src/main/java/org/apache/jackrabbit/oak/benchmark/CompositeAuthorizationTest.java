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
name|benchmark
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
name|Collections
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
name|Repository
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|Oak
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
name|fixture
operator|.
name|JcrCreator
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
name|fixture
operator|.
name|OakRepositoryFixture
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
name|fixture
operator|.
name|RepositoryFixture
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
name|Jcr
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
name|plugins
operator|.
name|tree
operator|.
name|RootFactory
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
name|SecurityProviderImpl
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
name|composite
operator|.
name|CompositeAuthorizationConfiguration
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
name|lifecycle
operator|.
name|RepositoryInitializer
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
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * Test the effect of multiple authorization configurations on the general read  * operations.  */
end_comment

begin_class
specifier|public
class|class
name|CompositeAuthorizationTest
extends|extends
name|ReadDeepTreeTest
block|{
specifier|private
name|int
name|cnt
decl_stmt|;
specifier|protected
name|CompositeAuthorizationTest
parameter_list|(
name|boolean
name|runAsAdmin
parameter_list|,
name|int
name|cntConfigurations
parameter_list|)
block|{
name|super
argument_list|(
name|runAsAdmin
argument_list|,
literal|1000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cnt
operator|=
name|cntConfigurations
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Repository
index|[]
name|createRepository
parameter_list|(
name|RepositoryFixture
name|fixture
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|fixture
operator|instanceof
name|OakRepositoryFixture
condition|)
block|{
return|return
operator|(
operator|(
name|OakRepositoryFixture
operator|)
name|fixture
operator|)
operator|.
name|setUpCluster
argument_list|(
literal|1
argument_list|,
operator|new
name|JcrCreator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Jcr
name|customize
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
return|return
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|TmpSecurityProvider
argument_list|(
name|cnt
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|createRepository
argument_list|(
name|fixture
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TmpSecurityProvider
extends|extends
name|SecurityProviderImpl
block|{
specifier|private
name|TmpSecurityProvider
parameter_list|(
name|int
name|cnt
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|AuthorizationConfiguration
name|authorizationConfiguration
init|=
name|getConfiguration
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|authorizationConfiguration
operator|instanceof
name|CompositeAuthorizationConfiguration
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
else|else
block|{
specifier|final
name|AuthorizationConfiguration
name|defConfig
init|=
name|checkNotNull
argument_list|(
operator|(
operator|(
name|CompositeAuthorizationConfiguration
operator|)
name|authorizationConfiguration
operator|)
operator|.
name|getDefaultConfig
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cnt
condition|;
name|i
operator|++
control|)
block|{
name|bindAuthorizationConfiguration
argument_list|(
operator|new
name|TmpAuthorizationConfig
argument_list|(
name|defConfig
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|bindAuthorizationConfiguration
argument_list|(
name|defConfig
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TmpAuthorizationConfig
implements|implements
name|AuthorizationConfiguration
block|{
specifier|private
specifier|final
name|AuthorizationConfiguration
name|defConfig
decl_stmt|;
specifier|private
name|TmpAuthorizationConfig
parameter_list|(
annotation|@
name|Nonnull
name|AuthorizationConfiguration
name|defConfig
parameter_list|)
block|{
name|this
operator|.
name|defConfig
operator|=
name|defConfig
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|AccessControlManager
name|getAccessControlManager
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
name|defConfig
operator|.
name|getAccessControlManager
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RestrictionProvider
name|getRestrictionProvider
parameter_list|()
block|{
return|return
name|defConfig
operator|.
name|getRestrictionProvider
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PermissionProvider
name|getPermissionProvider
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|String
name|workspaceName
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
return|return
operator|new
name|TmpPermissionProvider
argument_list|(
name|root
argument_list|)
return|;
block|}
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
name|defConfig
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|ConfigurationParameters
name|getParameters
parameter_list|()
block|{
return|return
name|defConfig
operator|.
name|getParameters
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|WorkspaceInitializer
name|getWorkspaceInitializer
parameter_list|()
block|{
return|return
name|WorkspaceInitializer
operator|.
name|DEFAULT
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RepositoryInitializer
name|getRepositoryInitializer
parameter_list|()
block|{
return|return
name|RepositoryInitializer
operator|.
name|DEFAULT
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|CommitHook
argument_list|>
name|getCommitHooks
parameter_list|(
annotation|@
name|Nonnull
name|String
name|workspaceName
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|ValidatorProvider
argument_list|>
name|getValidators
parameter_list|(
annotation|@
name|Nonnull
name|String
name|workspaceName
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|,
annotation|@
name|Nonnull
name|MoveTracker
name|moveTracker
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|ProtectedItemImporter
argument_list|>
name|getProtectedItemImporters
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Context
name|getContext
parameter_list|()
block|{
return|return
name|defConfig
operator|.
name|getContext
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TmpPermissionProvider
implements|implements
name|AggregatedPermissionProvider
block|{
specifier|private
specifier|static
specifier|final
name|String
name|POLICY_NAME
init|=
literal|"customPolicy"
decl_stmt|;
specifier|private
name|Root
name|root
decl_stmt|;
specifier|private
name|Root
name|immutableRoot
decl_stmt|;
specifier|private
name|TmpPermissionProvider
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|immutableRoot
operator|=
name|RootFactory
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
name|RootFactory
operator|.
name|createReadOnlyRoot
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
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
name|performSomeRead
argument_list|(
name|tree
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|singleton
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
name|performSomeRead
argument_list|(
name|tree
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
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
name|RepositoryPermission
operator|.
name|ALL
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
name|performSomeRead
argument_list|(
name|tree
argument_list|)
expr_stmt|;
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
name|performSomeRead
argument_list|(
name|tree
argument_list|)
expr_stmt|;
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
name|performSomeRead
argument_list|(
name|immutableRoot
operator|.
name|getTree
argument_list|(
name|oakPath
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|private
name|void
name|performSomeRead
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|)
block|{
comment|//            Tree immutableTree = PermissionUtil.getImmutableTree(tree, immutableRoot);
comment|//            if (immutableTree != null) {
comment|//                immutableTree.hasChild(POLICY_NAME);
comment|//            }
block|}
annotation|@
name|Nonnull
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
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|supportedPermissions
parameter_list|(
annotation|@
name|Nonnull
name|TreePermission
name|treePermission
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
literal|true
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
name|TreeType
name|type
parameter_list|,
annotation|@
name|Nonnull
name|TreePermission
name|parentPermission
parameter_list|)
block|{
return|return
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|parentPermission
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit
