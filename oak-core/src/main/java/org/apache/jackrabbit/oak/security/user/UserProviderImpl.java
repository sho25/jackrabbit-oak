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
name|text
operator|.
name|ParseException
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
name|Iterator
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
name|nodetype
operator|.
name|ConstraintViolationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
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
name|user
operator|.
name|Impersonation
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
name|Result
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
name|ResultRow
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
name|memory
operator|.
name|PropertyStates
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
name|security
operator|.
name|principal
operator|.
name|TreeBasedPrincipal
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
name|UserConfig
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
name|util
operator|.
name|NodeUtil
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
name|util
operator|.
name|Text
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
name|Type
operator|.
name|STRING
import|;
end_import

begin_comment
comment|/**  * User provider implementation and manager for group memberships with the  * following characteristics:  *  *<h1>UserProvider</h1>  *  *<h2>User and Group Creation</h2>  * This implementation creates the JCR nodes corresponding the a given  * authorizable ID with the following behavior:  *<ul>  *<li>Users are created below /rep:security/rep:authorizables/rep:users or  * the path configured in the {@link org.apache.jackrabbit.oak.spi.security.user.UserConfig#PARAM_USER_PATH}  * respectively.</li>  *<li>Groups are created below /rep:security/rep:authorizables/rep:groups or  * the path configured in the {@link org.apache.jackrabbit.oak.spi.security.user.UserConfig#PARAM_GROUP_PATH}  * respectively.</li>  *<li>Below each category authorizables are created within a human readable  * structure based on the defined intermediate path or some internal logic  * with a depth defined by the {@code defaultDepth} config option.<br>  * E.g. creating a user node for an ID 'aSmith' would result in the following  * structure assuming defaultDepth == 2 is used:  *<pre>  * + rep:security            [rep:AuthorizableFolder]  *   + rep:authorizables     [rep:AuthorizableFolder]  *     + rep:users           [rep:AuthorizableFolder]  *       + a                 [rep:AuthorizableFolder]  *         + aS              [rep:AuthorizableFolder]  * ->        + aSmith        [rep:User]  *</pre>  *</li>  *<li>The node name is calculated from the specified authorizable ID  * {@link org.apache.jackrabbit.util.Text#escapeIllegalJcrChars(String) escaping} any illegal JCR chars.</li>  *<li>If no intermediate path is passed the names of the intermediate  * folders are calculated from the leading chars of the escaped node name.</li>  *<li>If the escaped node name is shorter than the {@code defaultDepth}  * the last char is repeated.<br>  * E.g. creating a user node for an ID 'a' would result in the following  * structure assuming defaultDepth == 2 is used:  *<pre>  * + rep:security            [rep:AuthorizableFolder]  *   + rep:authorizables     [rep:AuthorizableFolder]  *     + rep:users           [rep:AuthorizableFolder]  *       + a                 [rep:AuthorizableFolder]  *         + aa              [rep:AuthorizableFolder]  * ->        + a             [rep:User]  *</pre></li>  *  *<h3>Conflicts</h3>  *  *<ul>  *<li>If the authorizable node to be created would collide with an existing  *     folder the conflict is resolved by using the colling folder as target.</li>  *<li>The current implementation asserts that authorizable nodes are always  *     created underneath an node of type {@code rep:AuthorizableFolder}. If this  *     condition is violated a {@code ConstraintViolationException} is thrown.</li>  *<li>If the specified intermediate path results in an authorizable node  *     being located outside of the configured content structure a  *     {@code ConstraintViolationException} is thrown.</li>  *</ul>  *  *<h3>Configuration Options</h3>  *<ul>  *<li>{@link org.apache.jackrabbit.oak.spi.security.user.UserConfig#PARAM_USER_PATH}: Underneath this structure  *     all user nodes are created. Default value is  *     "/rep:security/rep:authorizables/rep:users"</li>  *<li>{@link org.apache.jackrabbit.oak.spi.security.user.UserConfig#PARAM_GROUP_PATH}: Underneath this structure  *     all group nodes are created. Default value is  *     "/rep:security/rep:authorizables/rep:groups"</li>  *<li>{@link org.apache.jackrabbit.oak.spi.security.user.UserConfig#PARAM_DEFAULT_DEPTH}: A positive {@code integer}  *     greater than zero defining the depth of the default structure that is  *     always created. Default value: 2</li>  *</ul>  *  *<h3>Compatibility with Jackrabbit 2.x</h3>  *  * Due to the fact that this JCR implementation is expected to deal with huge amount  * of child nodes the following configuration options are no longer supported:  *<ul>  *<li>autoExpandTree</li>  *<li>autoExpandSize</li>  *</ul>  *  *<h2>User and Group Access</h2>  *<h3>By ID</h3>  * TODO  *<h3>By Path</h3>  * TODO  *<h3>By Principal Name</h3>  * TODO  *  *<h3>Search for authorizables</h3>  *  * TODO  */
end_comment

begin_class
class|class
name|UserProviderImpl
extends|extends
name|AuthorizableBaseProvider
implements|implements
name|UserProvider
block|{
comment|/**      * logger instance      */
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
name|UserProviderImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DELIMITER
init|=
literal|"/"
decl_stmt|;
specifier|private
specifier|final
name|int
name|defaultDepth
decl_stmt|;
specifier|private
specifier|final
name|String
name|groupPath
decl_stmt|;
specifier|private
specifier|final
name|String
name|userPath
decl_stmt|;
name|UserProviderImpl
parameter_list|(
name|Root
name|root
parameter_list|,
name|UserConfig
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|root
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|defaultDepth
operator|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|UserConfig
operator|.
name|PARAM_DEFAULT_DEPTH
argument_list|,
name|DEFAULT_DEPTH
argument_list|)
expr_stmt|;
name|groupPath
operator|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|UserConfig
operator|.
name|PARAM_GROUP_PATH
argument_list|,
name|DEFAULT_GROUP_PATH
argument_list|)
expr_stmt|;
name|userPath
operator|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|UserConfig
operator|.
name|PARAM_USER_PATH
argument_list|,
name|DEFAULT_USER_PATH
argument_list|)
expr_stmt|;
block|}
comment|//-------------------------------------------------------< UserProvider>---
annotation|@
name|Override
specifier|public
name|Tree
name|createUser
parameter_list|(
name|String
name|userID
parameter_list|,
name|String
name|intermediateJcrPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|createAuthorizableNode
argument_list|(
name|userID
argument_list|,
literal|false
argument_list|,
name|intermediateJcrPath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|createGroup
parameter_list|(
name|String
name|groupID
parameter_list|,
name|String
name|intermediateJcrPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|createAuthorizableNode
argument_list|(
name|groupID
argument_list|,
literal|true
argument_list|,
name|intermediateJcrPath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|getAuthorizable
parameter_list|(
name|String
name|authorizableId
parameter_list|)
block|{
return|return
name|getByID
argument_list|(
name|authorizableId
argument_list|,
name|AuthorizableType
operator|.
name|AUTHORIZABLE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|getAuthorizable
parameter_list|(
name|String
name|authorizableId
parameter_list|,
name|AuthorizableType
name|authorizableType
parameter_list|)
block|{
return|return
name|getByID
argument_list|(
name|authorizableId
argument_list|,
name|authorizableType
argument_list|)
return|;
block|}
annotation|@
name|Override
argument_list|()
specifier|public
name|Tree
name|getAuthorizableByPath
parameter_list|(
name|String
name|authorizableOakPath
parameter_list|)
block|{
return|return
name|getByPath
argument_list|(
name|authorizableOakPath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|getAuthorizableByPrincipal
parameter_list|(
name|Principal
name|principal
parameter_list|)
block|{
if|if
condition|(
name|principal
operator|instanceof
name|TreeBasedPrincipal
condition|)
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
operator|(
operator|(
name|TreeBasedPrincipal
operator|)
name|principal
operator|)
operator|.
name|getOakPath
argument_list|()
argument_list|)
return|;
block|}
comment|// NOTE: in contrast to JR2 the extra shortcut for ID==principalName
comment|// can be omitted as principals names are stored in user defined
comment|// index as well.
try|try
block|{
name|CoreValue
name|bindValue
init|=
name|valueFactory
operator|.
name|createValue
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuilder
name|stmt
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|stmt
operator|.
name|append
argument_list|(
literal|"SELECT * FROM ["
argument_list|)
operator|.
name|append
argument_list|(
name|UserConstants
operator|.
name|NT_REP_AUTHORIZABLE
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|append
argument_list|(
literal|"WHERE ["
argument_list|)
operator|.
name|append
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
operator|.
name|append
argument_list|(
literal|"] = $principalName"
argument_list|)
expr_stmt|;
name|Result
name|result
init|=
name|root
operator|.
name|getQueryEngine
argument_list|()
operator|.
name|executeQuery
argument_list|(
name|stmt
operator|.
name|toString
argument_list|()
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"principalName"
argument_list|,
name|bindValue
argument_list|)
argument_list|,
name|root
argument_list|,
operator|new
name|NamePathMapper
operator|.
name|Default
argument_list|()
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|rows
init|=
name|result
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|rows
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|path
init|=
name|rows
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
return|return
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|ParseException
name|ex
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to retrieve authorizable by principal"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAuthorizableId
parameter_list|(
name|Tree
name|authorizableTree
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|authorizableTree
argument_list|)
expr_stmt|;
if|if
condition|(
name|isAuthorizableTree
argument_list|(
name|authorizableTree
argument_list|,
name|AuthorizableType
operator|.
name|AUTHORIZABLE
argument_list|)
condition|)
block|{
name|PropertyState
name|idProp
init|=
name|authorizableTree
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_AUTHORIZABLE_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|idProp
operator|!=
literal|null
condition|)
block|{
return|return
name|idProp
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Text
operator|.
name|unescapeIllegalJcrChars
argument_list|(
name|authorizableTree
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Tree
argument_list|>
name|findAuthorizables
parameter_list|(
name|String
index|[]
name|propertyRelPaths
parameter_list|,
name|String
name|value
parameter_list|,
name|String
index|[]
name|ntNames
parameter_list|,
name|boolean
name|exact
parameter_list|,
name|long
name|maxSize
parameter_list|,
name|AuthorizableType
name|authorizableType
parameter_list|)
block|{
comment|// TODO
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not yet implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAuthorizableType
parameter_list|(
name|Tree
name|authorizableTree
parameter_list|,
name|AuthorizableType
name|authorizableType
parameter_list|)
block|{
return|return
name|isAuthorizableTree
argument_list|(
name|authorizableTree
argument_list|,
name|authorizableType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAdminUser
parameter_list|(
name|Tree
name|userTree
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|userTree
argument_list|)
expr_stmt|;
return|return
name|isAuthorizableType
argument_list|(
name|userTree
argument_list|,
name|AuthorizableType
operator|.
name|USER
argument_list|)
operator|&&
name|config
operator|.
name|getAdminId
argument_list|()
operator|.
name|equals
argument_list|(
name|getAuthorizableId
argument_list|(
name|userTree
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPasswordHash
parameter_list|(
name|Tree
name|userTree
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|userTree
argument_list|)
expr_stmt|;
name|NodeUtil
name|n
init|=
operator|new
name|NodeUtil
argument_list|(
name|userTree
argument_list|,
name|valueFactory
argument_list|)
decl_stmt|;
return|return
name|n
operator|.
name|getString
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
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
name|setProtectedProperty
argument_list|(
name|userTree
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
annotation|@
name|Override
specifier|public
name|Impersonation
name|getImpersonation
parameter_list|(
name|Tree
name|userTree
parameter_list|,
name|PrincipalProvider
name|principalProvider
parameter_list|)
block|{
comment|// FIXME: for login the impersonation could be based on the tree directly -> improve
return|return
operator|new
name|ImpersonationImpl
argument_list|(
name|getAuthorizableId
argument_list|(
name|userTree
argument_list|)
argument_list|,
name|this
argument_list|,
name|principalProvider
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDisabled
parameter_list|(
name|Tree
name|userTree
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|userTree
argument_list|)
expr_stmt|;
return|return
name|userTree
operator|.
name|hasProperty
argument_list|(
name|REP_DISABLED
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDisableReason
parameter_list|(
name|Tree
name|userTree
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|userTree
argument_list|)
expr_stmt|;
name|PropertyState
name|disabled
init|=
name|userTree
operator|.
name|getProperty
argument_list|(
name|REP_DISABLED
argument_list|)
decl_stmt|;
if|if
condition|(
name|disabled
operator|!=
literal|null
condition|)
block|{
return|return
name|disabled
operator|.
name|getValue
argument_list|(
name|STRING
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
name|void
name|disable
parameter_list|(
name|Tree
name|userTree
parameter_list|,
name|String
name|reason
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkNotNull
argument_list|(
name|userTree
argument_list|)
expr_stmt|;
if|if
condition|(
name|isAdminUser
argument_list|(
name|userTree
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"The administrator user cannot be disabled."
argument_list|)
throw|;
block|}
if|if
condition|(
name|reason
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|isDisabled
argument_list|(
name|userTree
argument_list|)
condition|)
block|{
comment|// enable the user again.
name|setProtectedProperty
argument_list|(
name|userTree
argument_list|,
name|REP_DISABLED
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|,
name|PropertyType
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
comment|// else: not disabled -> nothing to
block|}
else|else
block|{
name|setProtectedProperty
argument_list|(
name|userTree
argument_list|,
name|REP_DISABLED
argument_list|,
name|reason
argument_list|,
name|PropertyType
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProtectedProperty
parameter_list|(
name|Tree
name|authorizableTree
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|String
name|value
parameter_list|,
name|int
name|propertyType
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|authorizableTree
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|authorizableTree
operator|.
name|removeProperty
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|authorizableTree
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|propertyName
argument_list|,
name|value
argument_list|,
name|propertyType
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProtectedProperty
parameter_list|(
name|Tree
name|authorizableTree
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|String
index|[]
name|values
parameter_list|,
name|int
name|propertyType
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|authorizableTree
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|authorizableTree
operator|.
name|removeProperty
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|NodeUtil
name|node
init|=
operator|new
name|NodeUtil
argument_list|(
name|authorizableTree
argument_list|,
name|valueFactory
argument_list|)
decl_stmt|;
name|node
operator|.
name|setValues
argument_list|(
name|propertyName
argument_list|,
name|values
argument_list|,
name|propertyType
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|Tree
name|createAuthorizableNode
parameter_list|(
name|String
name|authorizableId
parameter_list|,
name|boolean
name|isGroup
parameter_list|,
name|String
name|intermediatePath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|nodeName
init|=
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
name|authorizableId
argument_list|)
decl_stmt|;
name|NodeUtil
name|folder
init|=
name|createFolderNodes
argument_list|(
name|authorizableId
argument_list|,
name|nodeName
argument_list|,
name|isGroup
argument_list|,
name|intermediatePath
argument_list|)
decl_stmt|;
name|String
name|ntName
init|=
operator|(
name|isGroup
operator|)
condition|?
name|NT_REP_GROUP
else|:
name|NT_REP_USER
decl_stmt|;
name|NodeUtil
name|authorizableNode
init|=
name|folder
operator|.
name|addChild
argument_list|(
name|nodeName
argument_list|,
name|ntName
argument_list|)
decl_stmt|;
name|String
name|nodeID
init|=
name|getContentID
argument_list|(
name|authorizableId
argument_list|)
decl_stmt|;
name|authorizableNode
operator|.
name|setString
argument_list|(
name|REP_AUTHORIZABLE_ID
argument_list|,
name|authorizableId
argument_list|)
expr_stmt|;
name|authorizableNode
operator|.
name|setString
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|,
name|nodeID
argument_list|)
expr_stmt|;
return|return
name|authorizableNode
operator|.
name|getTree
argument_list|()
return|;
block|}
comment|/**      * Create folder structure for the authorizable to be created. The structure      * consists of a tree of rep:AuthorizableFolder node(s) starting at the      * configured user or group path. Note that Authorizable nodes are never      * nested.      *      * @param authorizableId The desired authorizable ID.      * @param nodeName The name of the authorizable node.      * @param isGroup Flag indicating whether the new authorizable is a group or a user.      * @param intermediatePath An optional intermediate path.      * @return The folder node.      * @throws RepositoryException If an error occurs      */
specifier|private
name|NodeUtil
name|createFolderNodes
parameter_list|(
name|String
name|authorizableId
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|boolean
name|isGroup
parameter_list|,
name|String
name|intermediatePath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|authRoot
init|=
operator|(
name|isGroup
operator|)
condition|?
name|groupPath
else|:
name|userPath
decl_stmt|;
name|NodeUtil
name|folder
decl_stmt|;
name|Tree
name|authTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|authRoot
argument_list|)
decl_stmt|;
if|if
condition|(
name|authTree
operator|==
literal|null
condition|)
block|{
name|folder
operator|=
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|valueFactory
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|Text
operator|.
name|explode
argument_list|(
name|authRoot
argument_list|,
literal|'/'
argument_list|,
literal|false
argument_list|)
control|)
block|{
name|folder
operator|=
name|folder
operator|.
name|getOrAddChild
argument_list|(
name|name
argument_list|,
name|NT_REP_AUTHORIZABLE_FOLDER
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|folder
operator|=
operator|new
name|NodeUtil
argument_list|(
name|authTree
argument_list|,
name|valueFactory
argument_list|)
expr_stmt|;
block|}
comment|// verification of hierarchy and node types is delegated to UserValidator upon commit
name|String
name|folderPath
init|=
name|getFolderPath
argument_list|(
name|authorizableId
argument_list|,
name|intermediatePath
argument_list|,
name|authRoot
argument_list|)
decl_stmt|;
name|String
index|[]
name|segmts
init|=
name|Text
operator|.
name|explode
argument_list|(
name|folderPath
argument_list|,
literal|'/'
argument_list|,
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|segment
range|:
name|segmts
control|)
block|{
if|if
condition|(
literal|"."
operator|.
name|equals
argument_list|(
name|segment
argument_list|)
condition|)
block|{
comment|// nothing to do
block|}
elseif|else
if|if
condition|(
literal|".."
operator|.
name|equals
argument_list|(
name|segment
argument_list|)
condition|)
block|{
name|folder
operator|=
name|folder
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|folder
operator|=
name|folder
operator|.
name|getOrAddChild
argument_list|(
name|segment
argument_list|,
name|NT_REP_AUTHORIZABLE_FOLDER
argument_list|)
expr_stmt|;
block|}
block|}
comment|// test for colliding folder child node.
while|while
condition|(
name|folder
operator|.
name|hasChild
argument_list|(
name|nodeName
argument_list|)
condition|)
block|{
name|NodeUtil
name|colliding
init|=
name|folder
operator|.
name|getChild
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|colliding
operator|.
name|hasPrimaryNodeTypeName
argument_list|(
name|NT_REP_AUTHORIZABLE_FOLDER
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Existing folder node collides with user/group to be created. Expanding path by: "
operator|+
name|colliding
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|folder
operator|=
name|colliding
expr_stmt|;
block|}
else|else
block|{
name|String
name|msg
init|=
literal|"Failed to create authorizable with id '"
operator|+
name|authorizableId
operator|+
literal|"' : "
operator|+
literal|"Detected conflicting node of unexpected node type '"
operator|+
name|colliding
operator|.
name|getString
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
literal|null
argument_list|)
operator|+
literal|"'."
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
return|return
name|folder
return|;
block|}
specifier|private
name|String
name|getFolderPath
parameter_list|(
name|String
name|authorizableId
parameter_list|,
name|String
name|intermediatePath
parameter_list|,
name|String
name|authRoot
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
if|if
condition|(
name|intermediatePath
operator|!=
literal|null
operator|&&
name|intermediatePath
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
condition|)
block|{
if|if
condition|(
operator|!
name|intermediatePath
operator|.
name|startsWith
argument_list|(
name|authRoot
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Attempt to create authorizable outside of configured tree"
argument_list|)
throw|;
block|}
else|else
block|{
name|intermediatePath
operator|=
name|intermediatePath
operator|.
name|substring
argument_list|(
name|authRoot
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|intermediatePath
operator|!=
literal|null
operator|&&
operator|!
name|intermediatePath
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|intermediatePath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|idLength
init|=
name|authorizableId
operator|.
name|length
argument_list|()
decl_stmt|;
name|StringBuilder
name|segment
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|defaultDepth
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|idLength
operator|>
name|i
condition|)
block|{
name|segment
operator|.
name|append
argument_list|(
name|authorizableId
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// escapedID is too short -> append the last char again
name|segment
operator|.
name|append
argument_list|(
name|authorizableId
operator|.
name|charAt
argument_list|(
name|idLength
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|DELIMITER
argument_list|)
operator|.
name|append
argument_list|(
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
name|segment
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

