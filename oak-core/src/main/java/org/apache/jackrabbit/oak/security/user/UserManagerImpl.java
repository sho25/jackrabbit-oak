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
name|user
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
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

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
name|RepositoryException
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
name|Authorizable
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
name|AuthorizableExistsException
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
name|Group
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
name|Query
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
name|User
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
name|security
operator|.
name|user
operator|.
name|query
operator|.
name|UserQueryManager
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
name|PrincipalImpl
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
name|AuthorizableType
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
name|security
operator|.
name|user
operator|.
name|UserConstants
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
name|action
operator|.
name|AuthorizableAction
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
name|util
operator|.
name|PasswordUtility
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
name|util
operator|.
name|UserUtility
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
comment|/**  * UserManagerImpl...  */
end_comment

begin_class
specifier|public
class|class
name|UserManagerImpl
implements|implements
name|UserManager
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
name|UserManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
specifier|final
name|UserProvider
name|userProvider
decl_stmt|;
specifier|private
specifier|final
name|MembershipProvider
name|membershipProvider
decl_stmt|;
specifier|private
specifier|final
name|ConfigurationParameters
name|config
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|AuthorizableAction
argument_list|>
name|authorizableActions
decl_stmt|;
specifier|private
name|UserQueryManager
name|queryManager
decl_stmt|;
specifier|private
name|ReadOnlyNodeTypeManager
name|ntMgr
decl_stmt|;
specifier|public
name|UserManagerImpl
parameter_list|(
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|,
name|SecurityProvider
name|securityProvider
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
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
name|this
operator|.
name|securityProvider
operator|=
name|securityProvider
expr_stmt|;
name|UserConfiguration
name|uc
init|=
name|securityProvider
operator|.
name|getUserConfiguration
argument_list|()
decl_stmt|;
name|this
operator|.
name|config
operator|=
name|uc
operator|.
name|getConfigurationParameters
argument_list|()
expr_stmt|;
name|this
operator|.
name|userProvider
operator|=
operator|new
name|UserProvider
argument_list|(
name|root
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|membershipProvider
operator|=
operator|new
name|MembershipProvider
argument_list|(
name|root
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|authorizableActions
operator|=
name|uc
operator|.
name|getAuthorizableActionProvider
argument_list|()
operator|.
name|getAuthorizableActions
argument_list|()
expr_stmt|;
block|}
comment|//--------------------------------------------------------< UserManager>---
annotation|@
name|Override
specifier|public
name|Authorizable
name|getAuthorizable
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkIsLive
argument_list|()
expr_stmt|;
name|Authorizable
name|authorizable
init|=
literal|null
decl_stmt|;
name|Tree
name|tree
init|=
name|userProvider
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|tree
operator|!=
literal|null
condition|)
block|{
name|authorizable
operator|=
name|getAuthorizable
argument_list|(
name|id
argument_list|,
name|tree
argument_list|)
expr_stmt|;
block|}
return|return
name|authorizable
return|;
block|}
annotation|@
name|Override
specifier|public
name|Authorizable
name|getAuthorizable
parameter_list|(
name|Principal
name|principal
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkIsLive
argument_list|()
expr_stmt|;
return|return
name|getAuthorizable
argument_list|(
name|userProvider
operator|.
name|getAuthorizableByPrincipal
argument_list|(
name|principal
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Authorizable
name|getAuthorizableByPath
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkIsLive
argument_list|()
expr_stmt|;
name|String
name|oakPath
init|=
name|namePathMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Invalid path "
operator|+
name|path
argument_list|)
throw|;
block|}
return|return
name|getAuthorizable
argument_list|(
name|userProvider
operator|.
name|getAuthorizableByPath
argument_list|(
name|oakPath
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|findAuthorizables
parameter_list|(
name|String
name|relPath
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|findAuthorizables
argument_list|(
name|relPath
argument_list|,
name|value
argument_list|,
name|SEARCH_TYPE_AUTHORIZABLE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|findAuthorizables
parameter_list|(
name|String
name|relPath
parameter_list|,
name|String
name|value
parameter_list|,
name|int
name|searchType
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkIsLive
argument_list|()
expr_stmt|;
return|return
name|getQueryManager
argument_list|()
operator|.
name|findAuthorizables
argument_list|(
name|relPath
argument_list|,
name|value
argument_list|,
name|AuthorizableType
operator|.
name|getType
argument_list|(
name|searchType
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|findAuthorizables
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkIsLive
argument_list|()
expr_stmt|;
return|return
name|getQueryManager
argument_list|()
operator|.
name|findAuthorizables
argument_list|(
name|query
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|User
name|createUser
parameter_list|(
specifier|final
name|String
name|userId
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Principal
name|principal
init|=
operator|new
name|PrincipalImpl
argument_list|(
name|userId
argument_list|)
decl_stmt|;
return|return
name|createUser
argument_list|(
name|userId
argument_list|,
name|password
argument_list|,
name|principal
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|User
name|createUser
parameter_list|(
name|String
name|userID
parameter_list|,
name|String
name|password
parameter_list|,
name|Principal
name|principal
parameter_list|,
annotation|@
name|Nullable
name|String
name|intermediatePath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkIsLive
argument_list|()
expr_stmt|;
name|checkValidID
argument_list|(
name|userID
argument_list|)
expr_stmt|;
name|checkValidPrincipal
argument_list|(
name|principal
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|intermediatePath
operator|!=
literal|null
condition|)
block|{
name|intermediatePath
operator|=
name|namePathMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
name|intermediatePath
argument_list|)
expr_stmt|;
block|}
name|Tree
name|userTree
init|=
name|userProvider
operator|.
name|createUser
argument_list|(
name|userID
argument_list|,
name|intermediatePath
argument_list|)
decl_stmt|;
name|setPrincipal
argument_list|(
name|userTree
argument_list|,
name|principal
argument_list|)
expr_stmt|;
if|if
condition|(
name|password
operator|!=
literal|null
condition|)
block|{
name|setPassword
argument_list|(
name|userTree
argument_list|,
name|password
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|User
name|user
init|=
operator|new
name|UserImpl
argument_list|(
name|userID
argument_list|,
name|userTree
argument_list|,
name|this
argument_list|)
decl_stmt|;
name|onCreate
argument_list|(
name|user
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"User created: "
operator|+
name|userID
argument_list|)
expr_stmt|;
return|return
name|user
return|;
block|}
annotation|@
name|Override
specifier|public
name|Group
name|createGroup
parameter_list|(
name|String
name|groupId
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Principal
name|principal
init|=
operator|new
name|PrincipalImpl
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
return|return
name|createGroup
argument_list|(
name|groupId
argument_list|,
name|principal
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Group
name|createGroup
parameter_list|(
name|Principal
name|principal
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|createGroup
argument_list|(
name|principal
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Group
name|createGroup
parameter_list|(
name|Principal
name|principal
parameter_list|,
annotation|@
name|Nullable
name|String
name|intermediatePath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|createGroup
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|,
name|principal
argument_list|,
name|intermediatePath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Group
name|createGroup
parameter_list|(
name|String
name|groupID
parameter_list|,
name|Principal
name|principal
parameter_list|,
annotation|@
name|Nullable
name|String
name|intermediatePath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkIsLive
argument_list|()
expr_stmt|;
name|checkValidID
argument_list|(
name|groupID
argument_list|)
expr_stmt|;
name|checkValidPrincipal
argument_list|(
name|principal
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|intermediatePath
operator|!=
literal|null
condition|)
block|{
name|intermediatePath
operator|=
name|namePathMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
name|intermediatePath
argument_list|)
expr_stmt|;
block|}
name|Tree
name|groupTree
init|=
name|userProvider
operator|.
name|createGroup
argument_list|(
name|groupID
argument_list|,
name|intermediatePath
argument_list|)
decl_stmt|;
name|setPrincipal
argument_list|(
name|groupTree
argument_list|,
name|principal
argument_list|)
expr_stmt|;
name|Group
name|group
init|=
operator|new
name|GroupImpl
argument_list|(
name|groupID
argument_list|,
name|groupTree
argument_list|,
name|this
argument_list|)
decl_stmt|;
name|onCreate
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Group created: "
operator|+
name|groupID
argument_list|)
expr_stmt|;
return|return
name|group
return|;
block|}
comment|/**      * Always returns {@code false}. Any modifications made to this user      * manager instance require a subsequent call to {@link javax.jcr.Session#save()}      * in order to have the changes persisted.      *      * @see org.apache.jackrabbit.api.security.user.UserManager#isAutoSave()      */
annotation|@
name|Override
specifier|public
name|boolean
name|isAutoSave
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Changing the auto-save behavior is not supported by this implementation      * and this method always throws {@code UnsupportedRepositoryOperationException}      *      * @see UserManager#autoSave(boolean)      */
annotation|@
name|Override
specifier|public
name|void
name|autoSave
parameter_list|(
name|boolean
name|enable
parameter_list|)
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"Session#save() is always required."
argument_list|)
throw|;
block|}
comment|//--------------------------------------------------------------------------
comment|/**      * Let the configured {@code AuthorizableAction}s perform additional      * tasks associated with the creation of the new user before the      * corresponding new node is persisted.      *      * @param user     The new user.      * @param password The password.      * @throws RepositoryException If an exception occurs.      */
name|void
name|onCreate
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|AuthorizableAction
name|action
range|:
name|authorizableActions
control|)
block|{
name|action
operator|.
name|onCreate
argument_list|(
name|user
argument_list|,
name|password
argument_list|,
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Let the configured {@code AuthorizableAction}s perform additional      * tasks associated with the creation of the new group before the      * corresponding new node is persisted.      *      * @param group The new group.      * @throws RepositoryException If an exception occurs.      */
name|void
name|onCreate
parameter_list|(
name|Group
name|group
parameter_list|)
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|AuthorizableAction
name|action
range|:
name|authorizableActions
control|)
block|{
name|action
operator|.
name|onCreate
argument_list|(
name|group
argument_list|,
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Let the configured {@code AuthorizableAction}s perform any clean      * up tasks related to the authorizable removal (before the corresponding      * node gets removed).      *      * @param authorizable The authorizable to be removed.      * @throws RepositoryException If an exception occurs.      */
name|void
name|onRemove
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|AuthorizableAction
name|action
range|:
name|authorizableActions
control|)
block|{
name|action
operator|.
name|onRemove
argument_list|(
name|authorizable
argument_list|,
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Let the configured {@code AuthorizableAction}s perform additional      * tasks associated with password changing of a given user before the      * corresponding property is being changed.      *      * @param user     The target user.      * @param password The new password.      * @throws RepositoryException If an exception occurs.      */
name|void
name|onPasswordChange
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|AuthorizableAction
name|action
range|:
name|authorizableActions
control|)
block|{
name|action
operator|.
name|onPasswordChange
argument_list|(
name|user
argument_list|,
name|password
argument_list|,
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
block|}
comment|//--------------------------------------------------------------------------
annotation|@
name|CheckForNull
name|Authorizable
name|getAuthorizable
parameter_list|(
name|Tree
name|tree
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|tree
operator|==
literal|null
operator|||
operator|!
name|tree
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|getAuthorizable
argument_list|(
name|userProvider
operator|.
name|getAuthorizableId
argument_list|(
name|tree
argument_list|)
argument_list|,
name|tree
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
name|NamePathMapper
name|getNamePathMapper
parameter_list|()
block|{
return|return
name|namePathMapper
return|;
block|}
annotation|@
name|Nonnull
name|ReadOnlyNodeTypeManager
name|getNodeTypeManager
parameter_list|()
block|{
if|if
condition|(
name|ntMgr
operator|==
literal|null
condition|)
block|{
name|ntMgr
operator|=
name|ReadOnlyNodeTypeManager
operator|.
name|getInstance
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
return|return
name|ntMgr
return|;
block|}
annotation|@
name|Nonnull
name|MembershipProvider
name|getMembershipProvider
parameter_list|()
block|{
return|return
name|membershipProvider
return|;
block|}
annotation|@
name|Nonnull
name|PrincipalManager
name|getPrincipalManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|securityProvider
operator|.
name|getPrincipalConfiguration
argument_list|()
operator|.
name|getPrincipalManager
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
name|SecurityProvider
name|getSecurityProvider
parameter_list|()
block|{
return|return
name|securityProvider
return|;
block|}
annotation|@
name|Nonnull
name|ConfigurationParameters
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
name|Authorizable
name|getAuthorizable
parameter_list|(
name|String
name|id
parameter_list|,
name|Tree
name|tree
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|id
operator|==
literal|null
operator|||
name|tree
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|UserUtility
operator|.
name|isType
argument_list|(
name|tree
argument_list|,
name|AuthorizableType
operator|.
name|USER
argument_list|)
condition|)
block|{
return|return
operator|new
name|UserImpl
argument_list|(
name|userProvider
operator|.
name|getAuthorizableId
argument_list|(
name|tree
argument_list|)
argument_list|,
name|tree
argument_list|,
name|this
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|UserUtility
operator|.
name|isType
argument_list|(
name|tree
argument_list|,
name|AuthorizableType
operator|.
name|GROUP
argument_list|)
condition|)
block|{
return|return
operator|new
name|GroupImpl
argument_list|(
name|userProvider
operator|.
name|getAuthorizableId
argument_list|(
name|tree
argument_list|)
argument_list|,
name|tree
argument_list|,
name|this
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Not a user or group tree "
operator|+
name|tree
operator|.
name|getPath
argument_list|()
operator|+
literal|'.'
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|checkValidID
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|id
operator|==
literal|null
operator|||
name|id
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid ID "
operator|+
name|id
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|getAuthorizable
argument_list|(
name|id
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|AuthorizableExistsException
argument_list|(
literal|"Authorizable with ID "
operator|+
name|id
operator|+
literal|" already exists"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|checkValidPrincipal
parameter_list|(
name|Principal
name|principal
parameter_list|,
name|boolean
name|isGroup
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|principal
operator|==
literal|null
operator|||
name|principal
operator|.
name|getName
argument_list|()
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Principal may not be null and must have a valid name."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|isGroup
operator|&&
name|EveryonePrincipal
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"'everyone' is a reserved group principal name."
argument_list|)
throw|;
block|}
if|if
condition|(
name|getAuthorizable
argument_list|(
name|principal
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|AuthorizableExistsException
argument_list|(
literal|"Authorizable with principal "
operator|+
name|principal
operator|.
name|getName
argument_list|()
operator|+
literal|" already exists."
argument_list|)
throw|;
block|}
block|}
name|void
name|setPrincipal
parameter_list|(
name|Tree
name|authorizableTree
parameter_list|,
name|Principal
name|principal
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|principal
argument_list|)
expr_stmt|;
name|authorizableTree
operator|.
name|setProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|void
name|setPassword
parameter_list|(
name|Tree
name|userTree
parameter_list|,
name|String
name|password
parameter_list|,
name|boolean
name|forceHash
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|pwHash
decl_stmt|;
if|if
condition|(
name|forceHash
operator|||
name|PasswordUtility
operator|.
name|isPlainTextPassword
argument_list|(
name|password
argument_list|)
condition|)
block|{
try|try
block|{
name|pwHash
operator|=
name|PasswordUtility
operator|.
name|buildPasswordHash
argument_list|(
name|password
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|pwHash
operator|=
name|password
expr_stmt|;
block|}
name|userTree
operator|.
name|setProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|,
name|pwHash
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkIsLive
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
comment|// FIXME: checkIsLive is not part of the public root interface... execute the check using another method.
name|root
operator|.
name|getBlobFactory
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"User manager is no longer alive."
argument_list|)
throw|;
block|}
block|}
specifier|private
name|UserQueryManager
name|getQueryManager
parameter_list|()
block|{
if|if
condition|(
name|queryManager
operator|==
literal|null
condition|)
block|{
name|queryManager
operator|=
operator|new
name|UserQueryManager
argument_list|(
name|this
argument_list|,
name|namePathMapper
argument_list|,
name|config
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
return|return
name|queryManager
return|;
block|}
block|}
end_class

end_unit

