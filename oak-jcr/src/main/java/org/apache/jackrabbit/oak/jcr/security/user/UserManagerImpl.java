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
name|security
operator|.
name|user
package|;
end_package

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
name|jcr
operator|.
name|ItemNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
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
name|PropertyType
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
name|ItemBasedPrincipal
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
name|CoreValue
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
name|jcr
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
name|value
operator|.
name|ValueConverter
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
name|UserProviderImpl
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
name|user
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
name|UserManagerConfig
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
name|UserProvider
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
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|private
specifier|final
name|UserManagerConfig
name|config
decl_stmt|;
specifier|private
specifier|final
name|UserProvider
name|userProvider
decl_stmt|;
specifier|private
specifier|final
name|NodeTreeUtil
name|util
decl_stmt|;
specifier|private
name|MembershipManager
name|membershipManager
decl_stmt|;
specifier|public
name|UserManagerImpl
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|Root
name|root
parameter_list|,
name|UserManagerConfig
name|config
parameter_list|)
block|{
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionDelegate
expr_stmt|;
name|this
operator|.
name|config
operator|=
operator|(
name|config
operator|==
literal|null
operator|)
condition|?
operator|new
name|UserManagerConfig
argument_list|(
literal|"admin"
argument_list|)
else|:
name|config
expr_stmt|;
name|userProvider
operator|=
operator|new
name|UserProviderImpl
argument_list|(
name|root
argument_list|,
name|sessionDelegate
operator|.
name|getContentSession
argument_list|()
operator|.
name|getCoreValueFactory
argument_list|()
argument_list|,
name|sessionDelegate
operator|.
name|getNamePathMapper
argument_list|()
argument_list|,
name|this
operator|.
name|config
argument_list|)
expr_stmt|;
comment|// FIXME: remove again. only tmp workaround
name|this
operator|.
name|util
operator|=
operator|new
name|NodeTreeUtil
argument_list|(
name|sessionDelegate
operator|.
name|getSession
argument_list|()
argument_list|,
name|root
argument_list|,
name|sessionDelegate
operator|.
name|getNamePathMapper
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------------< UserManager>---
comment|/**      * @see UserManager#getAuthorizable(String)      */
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
name|Authorizable
name|authorizable
init|=
literal|null
decl_stmt|;
comment|// TODO: replace
comment|//        Tree tree = userProvider.getAuthorizable(id);
comment|//        if (tree != null) {
comment|//            authorizable = getAuthorizable(tree);
comment|//        }
try|try
block|{
name|Node
name|node
init|=
name|getSession
argument_list|()
operator|.
name|getNodeByIdentifier
argument_list|(
name|userProvider
operator|.
name|getContentID
argument_list|(
name|id
argument_list|)
argument_list|)
decl_stmt|;
name|authorizable
operator|=
name|getAuthorizable
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ItemNotFoundException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No authorizable with ID "
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
return|return
name|authorizable
return|;
block|}
comment|/**      * @see UserManager#getAuthorizable(Principal)      */
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
name|Session
name|session
init|=
name|getSession
argument_list|()
decl_stmt|;
name|Authorizable
name|authorizable
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|principal
operator|instanceof
name|ItemBasedPrincipal
condition|)
block|{
name|String
name|authPath
init|=
operator|(
operator|(
name|ItemBasedPrincipal
operator|)
name|principal
operator|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|session
operator|.
name|nodeExists
argument_list|(
name|authPath
argument_list|)
condition|)
block|{
name|Node
name|n
init|=
name|session
operator|.
name|getNode
argument_list|(
name|authPath
argument_list|)
decl_stmt|;
name|authorizable
operator|=
name|getAuthorizable
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// another Principal implementation.
name|String
name|name
init|=
name|principal
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Authorizable
name|a
init|=
name|getAuthorizable
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
operator|&&
name|name
operator|.
name|equals
argument_list|(
name|a
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|authorizable
operator|=
name|a
expr_stmt|;
block|}
else|else
block|{
name|String
name|propName
init|=
name|getJcrName
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|result
init|=
name|findAuthorizables
argument_list|(
name|propName
argument_list|,
name|name
argument_list|,
name|SEARCH_TYPE_AUTHORIZABLE
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|authorizable
operator|=
name|result
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// build the corresponding authorizable object
return|return
name|authorizable
return|;
block|}
comment|/**      * @see UserManager#getAuthorizableByPath(String)      */
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
name|Session
name|session
init|=
name|getSession
argument_list|()
decl_stmt|;
if|if
condition|(
name|session
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
name|getAuthorizable
argument_list|(
name|session
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
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
comment|// TODO : create and execute a query
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not Implemented"
argument_list|)
throw|;
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
comment|// TODO : create and execute a query
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not Implemented"
argument_list|)
throw|;
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
comment|// TODO : execute the specified query
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not Implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|User
name|createUser
parameter_list|(
specifier|final
name|String
name|userID
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
name|Principal
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
name|userID
return|;
block|}
block|}
decl_stmt|;
return|return
name|createUser
argument_list|(
name|userID
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
name|String
name|intermediatePath
parameter_list|)
throws|throws
name|RepositoryException
block|{
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
name|setPassword
argument_list|(
name|userTree
argument_list|,
name|password
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|User
name|user
init|=
operator|new
name|UserImpl
argument_list|(
name|util
operator|.
name|getNode
argument_list|(
name|userTree
argument_list|)
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
specifier|final
name|String
name|groupID
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Principal
name|principal
init|=
operator|new
name|Principal
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
name|groupID
return|;
block|}
block|}
decl_stmt|;
return|return
name|createGroup
argument_list|(
name|groupID
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
name|String
name|intermediatePath
parameter_list|)
throws|throws
name|RepositoryException
block|{
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
name|util
operator|.
name|getNode
argument_list|(
name|groupTree
argument_list|)
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
comment|/**      * Let the configured {@code AuthorizableAction}s perform additional      * tasks associated with the creation of the new user before the      * corresponding new node is persisted.      *      * @param user The new user.      * @param password The password.      * @throws RepositoryException If an exception occurs.      */
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
name|config
operator|.
name|getAuthorizableActions
argument_list|()
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
name|getSession
argument_list|()
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
name|config
operator|.
name|getAuthorizableActions
argument_list|()
control|)
block|{
name|action
operator|.
name|onCreate
argument_list|(
name|group
argument_list|,
name|getSession
argument_list|()
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
name|config
operator|.
name|getAuthorizableActions
argument_list|()
control|)
block|{
name|action
operator|.
name|onRemove
argument_list|(
name|authorizable
argument_list|,
name|getSession
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Let the configured {@code AuthorizableAction}s perform additional      * tasks associated with password changing of a given user before the      * corresponding property is being changed.      *      * @param user The target user.      * @param password The new password.      * @throws RepositoryException If an exception occurs.      */
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
name|config
operator|.
name|getAuthorizableActions
argument_list|()
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
name|getSession
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//--------------------------------------------------------------------------
comment|/**      * @param userID A userID.      * @return true if the given userID belongs to the administrator user.      */
name|boolean
name|isAdminId
parameter_list|(
name|String
name|userID
parameter_list|)
block|{
return|return
name|config
operator|.
name|getAdminId
argument_list|()
operator|.
name|equals
argument_list|(
name|userID
argument_list|)
return|;
block|}
comment|/**      *      *      * @param userNode The node representing the user.      * @param password The plaintext password to set.      * @param forceHash If true the specified password will always be hashed.      * @throws javax.jcr.RepositoryException If an error occurs      */
name|void
name|setPassword
parameter_list|(
name|Tree
name|userNode
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
if|if
condition|(
name|password
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Password is null."
argument_list|)
expr_stmt|;
return|return;
block|}
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
name|String
name|algorithm
init|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|UserManagerConfig
operator|.
name|PARAM_PASSWORD_HASH_ALGORITHM
argument_list|,
name|PasswordUtility
operator|.
name|DEFAULT_ALGORITHM
argument_list|)
decl_stmt|;
name|int
name|iterations
init|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|UserManagerConfig
operator|.
name|PARAM_PASSWORD_HASH_ITERATIONS
argument_list|,
name|PasswordUtility
operator|.
name|DEFAULT_ITERATIONS
argument_list|)
decl_stmt|;
name|int
name|saltSize
init|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|UserManagerConfig
operator|.
name|PARAM_PASSWORD_SALT_SIZE
argument_list|,
name|PasswordUtility
operator|.
name|DEFAULT_SALT_SIZE
argument_list|)
decl_stmt|;
name|pwHash
operator|=
name|PasswordUtility
operator|.
name|buildPasswordHash
argument_list|(
name|password
argument_list|,
name|algorithm
argument_list|,
name|saltSize
argument_list|,
name|iterations
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
name|setInternalProperty
argument_list|(
name|userNode
argument_list|,
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|,
name|pwHash
argument_list|,
name|PropertyType
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
name|void
name|setPrincipal
parameter_list|(
name|Tree
name|userNode
parameter_list|,
name|Principal
name|principal
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|userNode
operator|.
name|getStatus
argument_list|()
operator|!=
name|Tree
operator|.
name|Status
operator|.
name|NEW
operator|||
name|userNode
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"rep:principalName can only be set once on a new node."
argument_list|)
throw|;
block|}
name|setInternalProperty
argument_list|(
name|userNode
argument_list|,
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
name|principal
operator|.
name|getName
argument_list|()
argument_list|,
name|PropertyType
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
name|void
name|setInternalProperty
parameter_list|(
name|Tree
name|userNode
parameter_list|,
name|String
name|oakName
parameter_list|,
name|String
name|value
parameter_list|,
name|int
name|type
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|CoreValue
name|cv
init|=
name|ValueConverter
operator|.
name|toCoreValue
argument_list|(
name|value
argument_list|,
name|type
argument_list|,
name|sessionDelegate
argument_list|)
decl_stmt|;
name|userNode
operator|.
name|setProperty
argument_list|(
name|oakName
argument_list|,
name|cv
argument_list|)
expr_stmt|;
block|}
name|void
name|setInternalProperty
parameter_list|(
name|Tree
name|userNode
parameter_list|,
name|String
name|oakName
parameter_list|,
name|String
index|[]
name|values
parameter_list|,
name|int
name|type
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|List
argument_list|<
name|CoreValue
argument_list|>
name|cvs
init|=
name|ValueConverter
operator|.
name|toCoreValues
argument_list|(
name|values
argument_list|,
name|type
argument_list|,
name|sessionDelegate
argument_list|)
decl_stmt|;
name|userNode
operator|.
name|setProperty
argument_list|(
name|oakName
argument_list|,
name|cvs
argument_list|)
expr_stmt|;
block|}
name|void
name|setInternalProperty
parameter_list|(
name|Tree
name|userNode
parameter_list|,
name|String
name|oakName
parameter_list|,
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|userNode
operator|.
name|setProperty
argument_list|(
name|oakName
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
name|void
name|removeInternalProperty
parameter_list|(
name|Tree
name|userNode
parameter_list|,
name|String
name|oakName
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|PropertyState
name|pd
init|=
name|userNode
operator|.
name|getProperty
argument_list|(
name|oakName
argument_list|)
decl_stmt|;
if|if
condition|(
name|pd
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
literal|"Missing authorizable property "
operator|+
name|oakName
argument_list|)
throw|;
block|}
else|else
block|{
name|userNode
operator|.
name|removeProperty
argument_list|(
name|oakName
argument_list|)
expr_stmt|;
block|}
block|}
name|Session
name|getSession
parameter_list|()
block|{
return|return
name|sessionDelegate
operator|.
name|getSession
argument_list|()
return|;
block|}
name|SessionDelegate
name|getSessionDelegate
parameter_list|()
block|{
return|return
name|sessionDelegate
return|;
block|}
name|MembershipManager
name|getMembershipManager
parameter_list|()
block|{
if|if
condition|(
name|membershipManager
operator|==
literal|null
condition|)
block|{
name|int
name|splitSize
init|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|UserManagerConfig
operator|.
name|PARAM_GROUP_MEMBERSHIP_SPLIT_SIZE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|splitSize
operator|<
literal|4
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Invalid value {} for {}. Expected integer>= 4"
argument_list|,
name|splitSize
argument_list|,
name|UserManagerConfig
operator|.
name|PARAM_GROUP_MEMBERSHIP_SPLIT_SIZE
argument_list|)
expr_stmt|;
name|splitSize
operator|=
literal|0
expr_stmt|;
block|}
name|membershipManager
operator|=
operator|new
name|MembershipManager
argument_list|(
name|this
argument_list|,
name|splitSize
argument_list|)
expr_stmt|;
block|}
return|return
name|membershipManager
return|;
block|}
name|Authorizable
name|getAuthorizable
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|node
operator|.
name|isNodeType
argument_list|(
name|getJcrName
argument_list|(
name|UserConstants
operator|.
name|NT_REP_USER
argument_list|)
argument_list|)
condition|)
block|{
return|return
operator|new
name|UserImpl
argument_list|(
name|node
argument_list|,
name|util
operator|.
name|getTree
argument_list|(
name|node
argument_list|)
argument_list|,
name|this
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|isNodeType
argument_list|(
name|getJcrName
argument_list|(
name|UserConstants
operator|.
name|NT_REP_GROUP
argument_list|)
argument_list|)
condition|)
block|{
return|return
operator|new
name|GroupImpl
argument_list|(
name|node
argument_list|,
name|util
operator|.
name|getTree
argument_list|(
name|node
argument_list|)
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
literal|"Unexpected node type "
operator|+
name|node
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|". Expected rep:User or rep:Group."
argument_list|)
throw|;
block|}
block|}
name|String
name|getJcrName
parameter_list|(
name|String
name|oakName
parameter_list|)
block|{
return|return
name|sessionDelegate
operator|.
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrName
argument_list|(
name|oakName
argument_list|)
return|;
block|}
specifier|private
name|void
name|checkValidID
parameter_list|(
name|String
name|ID
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|ID
operator|==
literal|null
operator|||
name|ID
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
name|ID
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|getAuthorizable
argument_list|(
name|ID
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
name|ID
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
block|}
block|}
end_class

end_unit

