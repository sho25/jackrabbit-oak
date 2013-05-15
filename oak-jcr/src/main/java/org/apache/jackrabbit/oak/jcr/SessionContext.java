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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|CheckForNull
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
name|PathNotFoundException
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
name|RepositoryException
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
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Workspace
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|lock
operator|.
name|LockManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|ObservationManager
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
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|VersionManager
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
name|JcrConstants
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
name|authorization
operator|.
name|PrivilegeManager
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
name|principal
operator|.
name|PrincipalManager
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
name|jcr
operator|.
name|delegate
operator|.
name|NodeDelegate
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
name|PropertyDelegate
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
name|delegate
operator|.
name|VersionManagerDelegate
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
name|security
operator|.
name|AccessManager
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
name|version
operator|.
name|VersionHistoryImpl
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
name|version
operator|.
name|VersionImpl
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
name|LocalNameMapper
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
name|namepath
operator|.
name|NamePathMapperImpl
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
name|name
operator|.
name|Namespaces
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
name|nodetype
operator|.
name|DefinitionProvider
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
name|nodetype
operator|.
name|EffectiveNodeTypeProvider
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
name|observation
operator|.
name|ObservationManagerImpl
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
name|observation2
operator|.
name|ObservationManagerImpl2
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
name|value
operator|.
name|ValueFactoryImpl
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
name|SecurityConfiguration
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
comment|/**  * Instances of this class are passed to all JCR implementation classes  * (e.g. {@code SessionImpl}, {@code NodeImpl}, etc.) and provide access to  * the session scoped instances generally needed (e.g. {@code NamePathMapper},  * {@code ValueFactory}, etc.).  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SessionContext
implements|implements
name|NamePathMapper
block|{
comment|// FIXME remove OAK-775 feature flag and unify ObservationManager implementations
specifier|private
specifier|static
specifier|final
name|boolean
name|OAK_775
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"OAK-775"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|RepositoryImpl
name|repository
decl_stmt|;
specifier|private
specifier|final
name|SessionDelegate
name|delegate
decl_stmt|;
specifier|private
specifier|final
name|SessionNamespaces
name|namespaces
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|ValueFactory
name|valueFactory
decl_stmt|;
specifier|private
name|AccessControlManager
name|accessControlManager
decl_stmt|;
specifier|private
name|PermissionProvider
name|permissionProvider
decl_stmt|;
specifier|private
name|PrincipalManager
name|principalManager
decl_stmt|;
specifier|private
name|UserManager
name|userManager
decl_stmt|;
specifier|private
name|PrivilegeManager
name|privilegeManager
decl_stmt|;
specifier|private
name|ObservationManager
name|observationManager
decl_stmt|;
specifier|private
name|SessionContext
parameter_list|(
name|RepositoryImpl
name|repository
parameter_list|,
specifier|final
name|SessionDelegate
name|delegate
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
name|repository
operator|=
name|repository
expr_stmt|;
name|this
operator|.
name|namespaces
operator|=
operator|new
name|SessionNamespaces
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|LocalNameMapper
name|nameMapper
init|=
operator|new
name|LocalNameMapper
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getNamespaceMap
parameter_list|()
block|{
return|return
name|Namespaces
operator|.
name|getNamespaceMap
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getSessionLocalMappings
parameter_list|()
block|{
return|return
name|namespaces
operator|.
name|getSessionLocalMappings
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
operator|new
name|NamePathMapperImpl
argument_list|(
name|nameMapper
argument_list|,
name|delegate
operator|.
name|getIdManager
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|valueFactory
operator|=
operator|new
name|ValueFactoryImpl
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
operator|.
name|getBlobFactory
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|SessionContext
name|create
parameter_list|(
specifier|final
name|SessionDelegate
name|delegate
parameter_list|,
name|RepositoryImpl
name|repository
parameter_list|)
block|{
return|return
operator|new
name|SessionContext
argument_list|(
name|checkNotNull
argument_list|(
name|repository
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|delegate
argument_list|)
argument_list|)
block|{
specifier|private
specifier|final
name|SessionImpl
name|session
init|=
operator|new
name|SessionImpl
argument_list|(
name|this
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|WorkspaceImpl
name|workspace
init|=
operator|new
name|WorkspaceImpl
argument_list|(
name|this
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Session
name|getSession
parameter_list|()
block|{
return|return
name|session
return|;
block|}
annotation|@
name|Override
specifier|public
name|Workspace
name|getWorkspace
parameter_list|()
block|{
return|return
name|workspace
return|;
block|}
annotation|@
name|Override
specifier|public
name|LockManager
name|getLockManager
parameter_list|()
block|{
return|return
name|workspace
operator|.
name|getLockManager
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeManager
name|getNodeTypeManager
parameter_list|()
block|{
return|return
name|workspace
operator|.
name|getNodeTypeManager
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|VersionManager
name|getVersionManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|workspace
operator|.
name|getVersionManager
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|EffectiveNodeTypeProvider
name|getEffectiveNodeTypeProvider
parameter_list|()
block|{
return|return
name|workspace
operator|.
name|getReadWriteNodeTypeManager
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|DefinitionProvider
name|getDefinitionProvider
parameter_list|()
block|{
return|return
name|workspace
operator|.
name|getReadWriteNodeTypeManager
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|public
name|Repository
name|getRepository
parameter_list|()
block|{
return|return
name|repository
return|;
block|}
specifier|public
name|SessionDelegate
name|getSessionDelegate
parameter_list|()
block|{
return|return
name|delegate
return|;
block|}
name|SessionNamespaces
name|getNamespaces
parameter_list|()
block|{
return|return
name|namespaces
return|;
block|}
specifier|public
specifier|abstract
name|Session
name|getSession
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|Workspace
name|getWorkspace
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|LockManager
name|getLockManager
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|NodeTypeManager
name|getNodeTypeManager
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|VersionManager
name|getVersionManager
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
specifier|public
specifier|abstract
name|EffectiveNodeTypeProvider
name|getEffectiveNodeTypeProvider
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|DefinitionProvider
name|getDefinitionProvider
parameter_list|()
function_decl|;
specifier|public
name|NodeImpl
name|createNodeOrNull
parameter_list|(
name|NodeDelegate
name|nd
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|nd
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|PropertyDelegate
name|pd
init|=
name|nd
operator|.
name|getPropertyOrNull
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|pd
operator|!=
literal|null
condition|?
name|pd
operator|.
name|getString
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|JcrConstants
operator|.
name|NT_VERSION
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|VersionManagerDelegate
name|delegate
init|=
name|VersionManagerDelegate
operator|.
name|create
argument_list|(
name|getSessionDelegate
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|VersionImpl
argument_list|(
name|delegate
operator|.
name|createVersion
argument_list|(
name|nd
argument_list|)
argument_list|,
name|this
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|JcrConstants
operator|.
name|NT_VERSIONHISTORY
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|VersionManagerDelegate
name|delegate
init|=
name|VersionManagerDelegate
operator|.
name|create
argument_list|(
name|getSessionDelegate
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|VersionHistoryImpl
argument_list|(
name|delegate
operator|.
name|createVersionHistory
argument_list|(
name|nd
argument_list|)
argument_list|,
name|this
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|NodeImpl
argument_list|<
name|NodeDelegate
argument_list|>
argument_list|(
name|nd
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
specifier|public
name|ValueFactory
name|getValueFactory
parameter_list|()
block|{
return|return
name|valueFactory
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|AccessControlManager
name|getAccessControlManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|accessControlManager
operator|==
literal|null
condition|)
block|{
name|SecurityProvider
name|securityProvider
init|=
name|repository
operator|.
name|getSecurityProvider
argument_list|()
decl_stmt|;
name|accessControlManager
operator|=
name|securityProvider
operator|.
name|getAccessControlConfiguration
argument_list|()
operator|.
name|getAccessControlManager
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
return|return
name|accessControlManager
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|PrincipalManager
name|getPrincipalManager
parameter_list|()
block|{
if|if
condition|(
name|principalManager
operator|==
literal|null
condition|)
block|{
name|SecurityProvider
name|securityProvider
init|=
name|repository
operator|.
name|getSecurityProvider
argument_list|()
decl_stmt|;
name|principalManager
operator|=
name|securityProvider
operator|.
name|getPrincipalConfiguration
argument_list|()
operator|.
name|getPrincipalManager
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
return|return
name|principalManager
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|UserManager
name|getUserManager
parameter_list|()
block|{
if|if
condition|(
name|userManager
operator|==
literal|null
condition|)
block|{
name|SecurityProvider
name|securityProvider
init|=
name|repository
operator|.
name|getSecurityProvider
argument_list|()
decl_stmt|;
name|userManager
operator|=
name|securityProvider
operator|.
name|getUserConfiguration
argument_list|()
operator|.
name|getUserManager
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
return|return
name|userManager
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|PrivilegeManager
name|getPrivilegeManager
parameter_list|()
block|{
if|if
condition|(
name|privilegeManager
operator|==
literal|null
condition|)
block|{
name|SecurityProvider
name|securityProvider
init|=
name|repository
operator|.
name|getSecurityProvider
argument_list|()
decl_stmt|;
name|privilegeManager
operator|=
name|securityProvider
operator|.
name|getPrivilegeConfiguration
argument_list|()
operator|.
name|getPrivilegeManager
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
return|return
name|privilegeManager
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|List
argument_list|<
name|ProtectedItemImporter
argument_list|>
name|getProtectedItemImporters
parameter_list|()
block|{
comment|// TODO: take non-security related importers into account as well (proper configuration)
name|List
argument_list|<
name|ProtectedItemImporter
argument_list|>
name|importers
init|=
operator|new
name|ArrayList
argument_list|<
name|ProtectedItemImporter
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SecurityConfiguration
name|sc
range|:
name|repository
operator|.
name|getSecurityProvider
argument_list|()
operator|.
name|getSecurityConfigurations
argument_list|()
control|)
block|{
name|importers
operator|.
name|addAll
argument_list|(
name|sc
operator|.
name|getProtectedItemImporters
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|importers
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|ObservationManager
name|getObservationManager
parameter_list|()
block|{
if|if
condition|(
name|observationManager
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|OAK_775
condition|)
block|{
name|observationManager
operator|=
operator|new
name|ObservationManagerImpl2
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
argument_list|,
name|namePathMapper
argument_list|,
name|repository
operator|.
name|getObservationExecutor
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|observationManager
operator|=
operator|new
name|ObservationManagerImpl
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
argument_list|,
name|namePathMapper
argument_list|,
name|repository
operator|.
name|getObservationExecutor
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|observationManager
return|;
block|}
specifier|public
name|boolean
name|hasPendingEvents
parameter_list|()
block|{
return|return
name|observationManager
operator|!=
literal|null
operator|&&
operator|(
name|OAK_775
condition|?
operator|(
operator|(
name|ObservationManagerImpl2
operator|)
name|observationManager
operator|)
operator|.
name|hasEvents
argument_list|()
else|:
operator|(
operator|(
name|ObservationManagerImpl
operator|)
name|observationManager
operator|)
operator|.
name|hasEvents
argument_list|()
operator|)
return|;
block|}
comment|//-----------------------------------------------------< NamePathMapper>---
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|getOakName
parameter_list|(
name|String
name|jcrName
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|namePathMapper
operator|.
name|getOakName
argument_list|(
name|jcrName
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|String
name|getOakNameOrNull
parameter_list|(
annotation|@
name|Nonnull
name|String
name|jcrName
parameter_list|)
block|{
return|return
name|namePathMapper
operator|.
name|getOakNameOrNull
argument_list|(
name|jcrName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasSessionLocalMappings
parameter_list|()
block|{
return|return
operator|!
name|namespaces
operator|.
name|getSessionLocalMappings
argument_list|()
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getJcrName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|oakName
parameter_list|)
block|{
return|return
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|oakName
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|String
name|getOakPath
parameter_list|(
name|String
name|jcrPath
parameter_list|)
block|{
return|return
name|namePathMapper
operator|.
name|getOakPath
argument_list|(
name|jcrPath
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|String
name|getOakPathKeepIndex
parameter_list|(
name|String
name|jcrPath
parameter_list|)
block|{
return|return
name|namePathMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
name|jcrPath
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|getJcrPath
parameter_list|(
name|String
name|oakPath
parameter_list|)
block|{
return|return
name|namePathMapper
operator|.
name|getJcrPath
argument_list|(
name|oakPath
argument_list|)
return|;
block|}
comment|/**      * Returns the Oak path for the given JCR path, or throws a      * {@link javax.jcr.RepositoryException} if the path can not be mapped.      *      * @param jcrPath JCR path      * @return Oak path      * @throws javax.jcr.RepositoryException if the path can not be mapped      */
annotation|@
name|Nonnull
specifier|public
name|String
name|getOakPathOrThrow
parameter_list|(
name|String
name|jcrPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|getOakPath
argument_list|(
name|jcrPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPath
operator|!=
literal|null
condition|)
block|{
return|return
name|oakPath
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Invalid name or path: "
operator|+
name|jcrPath
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the Oak path for the given JCR path, or throws a      * {@link javax.jcr.PathNotFoundException} if the path can not be mapped.      *      * @param jcrPath JCR path      * @return Oak path      * @throws javax.jcr.PathNotFoundException if the path can not be mapped      */
annotation|@
name|Nonnull
specifier|public
name|String
name|getOakPathOrThrowNotFound
parameter_list|(
name|String
name|jcrPath
parameter_list|)
throws|throws
name|PathNotFoundException
block|{
name|String
name|oakPath
init|=
name|getOakPath
argument_list|(
name|jcrPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPath
operator|!=
literal|null
condition|)
block|{
return|return
name|oakPath
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|jcrPath
argument_list|)
throw|;
block|}
block|}
comment|//-----------------------------------------------------------< internal>---
annotation|@
name|Nonnull
name|AccessManager
name|getAccessManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|AccessManager
argument_list|(
name|getPermissionProvider
argument_list|()
argument_list|)
return|;
block|}
name|void
name|dispose
parameter_list|()
block|{
if|if
condition|(
name|observationManager
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|OAK_775
condition|)
block|{
operator|(
operator|(
name|ObservationManagerImpl2
operator|)
name|observationManager
operator|)
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
else|else
block|{
operator|(
operator|(
name|ObservationManagerImpl
operator|)
name|observationManager
operator|)
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
name|namespaces
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|void
name|refresh
parameter_list|()
block|{
if|if
condition|(
name|permissionProvider
operator|!=
literal|null
condition|)
block|{
name|permissionProvider
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
annotation|@
name|Nonnull
specifier|private
name|PermissionProvider
name|getPermissionProvider
parameter_list|()
block|{
if|if
condition|(
name|permissionProvider
operator|==
literal|null
condition|)
block|{
name|SecurityProvider
name|securityProvider
init|=
name|repository
operator|.
name|getSecurityProvider
argument_list|()
decl_stmt|;
name|permissionProvider
operator|=
name|securityProvider
operator|.
name|getAccessControlConfiguration
argument_list|()
operator|.
name|getPermissionProvider
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
argument_list|,
name|delegate
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|permissionProvider
return|;
block|}
block|}
end_class

end_unit

