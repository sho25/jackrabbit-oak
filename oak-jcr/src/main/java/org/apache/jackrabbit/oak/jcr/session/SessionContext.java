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
name|session
package|;
end_package

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

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
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
name|collect
operator|.
name|Sets
operator|.
name|newTreeSet
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|UnsupportedRepositoryOperationException
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
name|JackrabbitAccessControlManager
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
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
name|AccessControlManagerDelegator
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
name|JackrabbitAccessControlManagerDelegator
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
name|PrincipalManagerDelegator
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
name|PrivilegeManagerDelegator
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
name|UserManagerDelegator
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
name|nodetype
operator|.
name|ReadOnlyNodeTypeManager
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
name|CommitRateLimiter
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
name|privilege
operator|.
name|PrivilegeConfiguration
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
name|user
operator|.
name|UserConfiguration
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
name|whiteboard
operator|.
name|Whiteboard
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
name|jackrabbit
operator|.
name|oak
operator|.
name|stats
operator|.
name|StatisticManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Instances of this class are passed to all JCR implementation classes  * (e.g. {@code SessionImpl}, {@code NodeImpl}, etc.) and provide access to  * the session scoped instances generally needed (e.g. {@code NamePathMapper},  * {@code ValueFactory}, etc.).  */
end_comment

begin_class
specifier|public
class|class
name|SessionContext
implements|implements
name|NamePathMapper
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SessionContext
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Repository
name|repository
decl_stmt|;
specifier|private
specifier|final
name|StatisticManager
name|statisticManager
decl_stmt|;
specifier|private
specifier|final
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
specifier|final
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
decl_stmt|;
specifier|private
specifier|final
name|SessionDelegate
name|delegate
decl_stmt|;
specifier|private
specifier|final
name|int
name|observationQueueLength
decl_stmt|;
specifier|private
specifier|final
name|CommitRateLimiter
name|commitRateLimiter
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
name|SessionImpl
name|session
init|=
literal|null
decl_stmt|;
specifier|private
name|WorkspaceImpl
name|workspace
init|=
literal|null
decl_stmt|;
specifier|private
name|AccessControlManager
name|accessControlManager
decl_stmt|;
specifier|private
name|AccessManager
name|accessManager
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
name|ObservationManagerImpl
name|observationManager
decl_stmt|;
comment|/** Paths (tokens) of all open scoped locks held by this session. */
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|openScopedLocks
init|=
name|newTreeSet
argument_list|()
decl_stmt|;
comment|/** Paths of all session scoped locks held by this session. */
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|sessionScopedLocks
init|=
name|newHashSet
argument_list|()
decl_stmt|;
specifier|public
name|SessionContext
parameter_list|(
annotation|@
name|Nonnull
name|Repository
name|repository
parameter_list|,
annotation|@
name|Nonnull
name|StatisticManager
name|statisticManager
parameter_list|,
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|,
annotation|@
name|Nonnull
name|Whiteboard
name|whiteboard
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|SessionDelegate
name|delegate
parameter_list|,
name|int
name|observationQueueLength
parameter_list|,
name|CommitRateLimiter
name|commitRateLimiter
parameter_list|)
block|{
name|this
operator|.
name|repository
operator|=
name|checkNotNull
argument_list|(
name|repository
argument_list|)
expr_stmt|;
name|this
operator|.
name|statisticManager
operator|=
name|statisticManager
expr_stmt|;
name|this
operator|.
name|securityProvider
operator|=
name|checkNotNull
argument_list|(
name|securityProvider
argument_list|)
expr_stmt|;
name|this
operator|.
name|whiteboard
operator|=
name|checkNotNull
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|this
operator|.
name|attributes
operator|=
name|checkNotNull
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|checkNotNull
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
name|this
operator|.
name|observationQueueLength
operator|=
name|observationQueueLength
expr_stmt|;
name|this
operator|.
name|commitRateLimiter
operator|=
name|commitRateLimiter
expr_stmt|;
name|SessionStats
name|sessionStats
init|=
name|delegate
operator|.
name|getSessionStats
argument_list|()
decl_stmt|;
name|sessionStats
operator|.
name|setAttributes
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
name|this
operator|.
name|namespaces
operator|=
operator|new
name|SessionNamespaces
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
operator|new
name|NamePathMapperImpl
argument_list|(
name|namespaces
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
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getAttributes
parameter_list|()
block|{
return|return
name|attributes
return|;
block|}
specifier|public
specifier|final
specifier|synchronized
name|SessionImpl
name|getSession
parameter_list|()
block|{
if|if
condition|(
name|session
operator|==
literal|null
condition|)
block|{
name|session
operator|=
name|createSession
argument_list|()
expr_stmt|;
block|}
return|return
name|session
return|;
block|}
specifier|public
specifier|final
specifier|synchronized
name|WorkspaceImpl
name|getWorkspace
parameter_list|()
block|{
if|if
condition|(
name|workspace
operator|==
literal|null
condition|)
block|{
name|workspace
operator|=
name|createWorkspace
argument_list|()
expr_stmt|;
block|}
return|return
name|workspace
return|;
block|}
comment|/**      * Factory method for creating the {@link Session} instance for this      * context. Called by {@link #getSession()} when first accessed. Can be      * overridden by subclasses to customize the session implementation.      *      * @return session instance      */
specifier|protected
name|SessionImpl
name|createSession
parameter_list|()
block|{
return|return
operator|new
name|SessionImpl
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * Factory method for creating the {@link Workspace} instance for this      * context. Called by {@link #getWorkspace()} when first accessed. Can be      * overridden by subclasses to customize the workspace implementation.      *      * @return session instance      */
specifier|protected
name|WorkspaceImpl
name|createWorkspace
parameter_list|()
block|{
return|return
operator|new
name|WorkspaceImpl
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|StatisticManager
name|getStatisticManager
parameter_list|()
block|{
return|return
name|statisticManager
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|AtomicLong
name|getCounter
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
return|return
name|statisticManager
operator|.
name|getCounter
argument_list|(
name|type
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Repository
name|getRepository
parameter_list|()
block|{
return|return
name|repository
return|;
block|}
annotation|@
name|Nonnull
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
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
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
name|AccessControlManager
name|acm
init|=
name|getConfig
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
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
decl_stmt|;
if|if
condition|(
name|acm
operator|instanceof
name|JackrabbitAccessControlManager
condition|)
block|{
name|accessControlManager
operator|=
operator|new
name|JackrabbitAccessControlManagerDelegator
argument_list|(
name|delegate
argument_list|,
operator|(
name|JackrabbitAccessControlManager
operator|)
name|acm
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|accessControlManager
operator|=
operator|new
name|AccessControlManagerDelegator
argument_list|(
name|delegate
argument_list|,
name|acm
argument_list|)
expr_stmt|;
block|}
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
name|principalManager
operator|=
operator|new
name|PrincipalManagerDelegator
argument_list|(
name|delegate
argument_list|,
name|getConfig
argument_list|(
name|PrincipalConfiguration
operator|.
name|class
argument_list|)
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
name|userManager
operator|=
operator|new
name|UserManagerDelegator
argument_list|(
name|delegate
argument_list|,
name|getConfig
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
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
name|privilegeManager
operator|=
operator|new
name|PrivilegeManagerDelegator
argument_list|(
name|delegate
argument_list|,
name|getConfig
argument_list|(
name|PrivilegeConfiguration
operator|.
name|class
argument_list|)
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
name|securityProvider
operator|.
name|getConfigurations
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
throws|throws
name|UnsupportedRepositoryOperationException
block|{
if|if
condition|(
name|observationManager
operator|==
literal|null
condition|)
block|{
name|observationManager
operator|=
operator|new
name|ObservationManagerImpl
argument_list|(
name|this
argument_list|,
name|ReadOnlyNodeTypeManager
operator|.
name|getInstance
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
argument_list|,
name|whiteboard
argument_list|,
name|observationQueueLength
argument_list|,
name|commitRateLimiter
argument_list|)
expr_stmt|;
block|}
return|return
name|observationManager
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getOpenScopedLocks
parameter_list|()
block|{
return|return
name|openScopedLocks
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getSessionScopedLocks
parameter_list|()
block|{
return|return
name|sessionScopedLocks
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
annotation|@
name|Nonnull
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
name|Nonnull
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
annotation|@
name|Nonnull
specifier|public
name|AccessManager
name|getAccessManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|accessManager
operator|==
literal|null
condition|)
block|{
name|accessManager
operator|=
operator|new
name|AccessManager
argument_list|(
name|delegate
argument_list|,
name|delegate
operator|.
name|getPermissionProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|accessManager
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|SecurityProvider
name|getSecurityProvider
parameter_list|()
block|{
return|return
name|securityProvider
return|;
block|}
comment|//-----------------------------------------------------------< internal>---
name|void
name|dispose
parameter_list|()
block|{
try|try
block|{
name|unlockAllSessionScopedLocks
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected repository error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|observationManager
operator|!=
literal|null
condition|)
block|{
name|observationManager
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
name|namespaces
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * Unlocks all existing session-scoped locks (if any). Used for cleanup      * when a session is being closed.      *      * @throws RepositoryException if an unexpected problem occurs      */
comment|// TODO: should this be in SessionImpl?
specifier|private
name|void
name|unlockAllSessionScopedLocks
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|delegate
operator|.
name|performVoid
argument_list|(
operator|new
name|SessionOperation
argument_list|(
literal|"unlockAllSessionScopedLocks"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|performVoid
parameter_list|()
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
init|=
name|sessionScopedLocks
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|NodeDelegate
name|node
init|=
name|delegate
operator|.
name|getNode
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|node
operator|.
name|unlock
argument_list|()
expr_stmt|;
comment|// TODO: use a single commit
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to unlock a session scoped lock"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|private
parameter_list|<
name|T
parameter_list|>
name|T
name|getConfig
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clss
parameter_list|)
block|{
return|return
name|securityProvider
operator|.
name|getConfiguration
argument_list|(
name|clss
argument_list|)
return|;
block|}
block|}
end_class

end_unit

