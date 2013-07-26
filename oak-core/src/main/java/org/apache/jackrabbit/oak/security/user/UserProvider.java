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
name|AccessDeniedException
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
name|query
operator|.
name|PropertyValues
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
name|user
operator|.
name|AuthorizableNodeName
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
name|util
operator|.
name|UserUtil
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
comment|/**  * User provider implementation and manager for group memberships with the  * following characteristics:  *<p/>  *<h1>UserProvider</h1>  *<p/>  *<h2>User and Group Creation</h2>  * This implementation creates the JCR nodes corresponding the a given  * authorizable ID with the following behavior:  *<ul>  *<li>Users are created below /rep:security/rep:authorizables/rep:users or  * the path configured in the {@link org.apache.jackrabbit.oak.spi.security.user.UserConstants#PARAM_USER_PATH}  * respectively.</li>  *<li>Groups are created below /rep:security/rep:authorizables/rep:groups or  * the path configured in the {@link org.apache.jackrabbit.oak.spi.security.user.UserConstants#PARAM_GROUP_PATH}  * respectively.</li>  *<li>Below each category authorizables are created within a human readable  * structure based on the defined intermediate path or some internal logic  * with a depth defined by the {@code defaultDepth} config option.<br>  * E.g. creating a user node for an ID 'aSmith' would result in the following  * structure assuming defaultDepth == 2 is used:  *<pre>  * + rep:security            [rep:AuthorizableFolder]  *   + rep:authorizables     [rep:AuthorizableFolder]  *     + rep:users           [rep:AuthorizableFolder]  *       + a                 [rep:AuthorizableFolder]  *         + aS              [rep:AuthorizableFolder]  * ->        + aSmith        [rep:User]  *</pre>  *</li>  *<li>The node name is calculated from the specified authorizable ID according  * to the logic provided by the configured {@link AuthorizableNodeName}  * implementation. If no name generator is present in the configuration  * the {@link AuthorizableNodeName#DEFAULT default} implementation is used. The  * name of the configuration option is {@link UserConstants#PARAM_AUTHORIZABLE_NODE_NAME}</li>  *<li>If no intermediate path is passed the names of the intermediate  * folders are calculated from the leading chars of the escaped node name.</li>  *<li>If the escaped node name is shorter than the {@code defaultDepth}  * the last char is repeated.<br>  * E.g. creating a user node for an ID 'a' would result in the following  * structure assuming defaultDepth == 2 is used:  *<pre>  * + rep:security            [rep:AuthorizableFolder]  *   + rep:authorizables     [rep:AuthorizableFolder]  *     + rep:users           [rep:AuthorizableFolder]  *       + a                 [rep:AuthorizableFolder]  *         + aa              [rep:AuthorizableFolder]  * ->        + a             [rep:User]  *</pre></li>  *  *<h3>Conflicts</h3>  *  *<ul>  *<li>If the authorizable node to be created would collide with an existing  * folder the conflict is resolved by using the colling folder as target.</li>  *<li>The current implementation asserts that authorizable nodes are always  * created underneath an node of type {@code rep:AuthorizableFolder}. If this  * condition is violated a {@code ConstraintViolationException} is thrown.</li>  *<li>If the specified intermediate path results in an authorizable node  * being located outside of the configured content structure a  * {@code ConstraintViolationException} is thrown.</li>  *</ul>  *  *<h3>Configuration Options</h3>  *<ul>  *<li>{@link UserConstants#PARAM_USER_PATH}: Underneath this structure  * all user nodes are created. Default value is  * "/rep:security/rep:authorizables/rep:users"</li>  *<li>{@link UserConstants#PARAM_GROUP_PATH}: Underneath this structure  * all group nodes are created. Default value is  * "/rep:security/rep:authorizables/rep:groups"</li>  *<li>{@link UserConstants#PARAM_DEFAULT_DEPTH}: A positive {@code integer}  * greater than zero defining the depth of the default structure that is  * always created. Default value: 2</li>  *<li>{@link UserConstants#PARAM_AUTHORIZABLE_NODE_NAME}: An implementation  * of {@link AuthorizableNodeName} used to create a node name for a given  * authorizableId. By {@link AuthorizableNodeName.Default default} the  * ID itself is used as node name. (since OAK 1.0)</li>  *</ul>  *  *<h3>Compatibility with Jackrabbit 2.x</h3>  *  * Due to the fact that this JCR implementation is expected to deal with huge amount  * of child nodes the following configuration options are no longer supported:  *<ul>  *<li>autoExpandTree</li>  *<li>autoExpandSize</li>  *</ul>  *  *<h2>User and Group Access</h2>  *<h3>By ID</h3>  * Accessing authorizables by ID is achieved by calculating the ContentId  * associated with that user/group and using {@link org.apache.jackrabbit.oak.api.QueryEngine}  * to find the corresponding {@code Tree}. The result is validated to really  * represent a user/group tree.  *  *<h3>By Path</h3>  * Access by path consists of a simple lookup by path such as exposed by  * {@link Root#getTree(String)}. The resulting tree is validated to really  * represent a user/group tree.  *  *<h3>By Principal</h3>  * If the principal instance passed to {@link #getAuthorizableByPrincipal(java.security.Principal)}  * is a {@code TreeBasedPrincipal} the lookup is equivalent to  * {@link #getAuthorizableByPath(String)}. Otherwise the user/group is search  * for using {@link org.apache.jackrabbit.oak.api.QueryEngine} looking  * for a property {@link UserConstants#REP_PRINCIPAL_NAME} that matches the  * name of the specified principal.  */
end_comment

begin_class
class|class
name|UserProvider
extends|extends
name|AuthorizableBaseProvider
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
name|UserProvider
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
name|UserProvider
parameter_list|(
name|Root
name|root
parameter_list|,
name|ConfigurationParameters
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
name|PARAM_USER_PATH
argument_list|,
name|DEFAULT_USER_PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
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
name|Nonnull
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
name|CheckForNull
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
name|CheckForNull
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
name|CheckForNull
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
name|PropertyValues
operator|.
name|newString
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
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
name|CheckForNull
specifier|static
name|String
name|getAuthorizableId
parameter_list|(
annotation|@
name|Nonnull
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
name|UserUtil
operator|.
name|isType
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
name|getNodeName
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
comment|/**      * Create folder structure for the authorizable to be created. The structure      * consists of a tree of rep:AuthorizableFolder node(s) starting at the      * configured user or group path. Note that Authorizable nodes are never      * nested.      *      * @param authorizableId   The desired authorizable ID.      * @param nodeName         The name of the authorizable node.      * @param isGroup          Flag indicating whether the new authorizable is a group or a user.      * @param intermediatePath An optional intermediate path.      * @return The folder node.      * @throws RepositoryException If an error occurs      */
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
name|String
name|folderPath
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|authRoot
argument_list|)
operator|.
name|append
argument_list|(
name|getFolderPath
argument_list|(
name|authorizableId
argument_list|,
name|intermediatePath
argument_list|,
name|authRoot
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|NodeUtil
name|folder
decl_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|folderPath
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|tree
operator|.
name|isRoot
argument_list|()
operator|&&
operator|!
name|tree
operator|.
name|exists
argument_list|()
condition|)
block|{
name|tree
operator|=
name|tree
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|tree
operator|.
name|exists
argument_list|()
condition|)
block|{
name|folder
operator|=
operator|new
name|NodeUtil
argument_list|(
name|tree
argument_list|)
expr_stmt|;
name|String
name|relativePath
init|=
name|PathUtils
operator|.
name|relativize
argument_list|(
name|tree
operator|.
name|getPath
argument_list|()
argument_list|,
name|folderPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|relativePath
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|folder
operator|=
name|folder
operator|.
name|getOrAddTree
argument_list|(
name|relativePath
argument_list|,
name|NT_REP_AUTHORIZABLE_FOLDER
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|AccessDeniedException
argument_list|(
literal|"Missing permission to create intermediate authorizable folders."
argument_list|)
throw|;
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
name|getPrimaryNodeTypeName
argument_list|()
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
name|DELIMITER
argument_list|)
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
specifier|private
name|String
name|getNodeName
parameter_list|(
name|String
name|authorizableId
parameter_list|)
block|{
name|AuthorizableNodeName
name|generator
init|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|PARAM_AUTHORIZABLE_NODE_NAME
argument_list|,
name|AuthorizableNodeName
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
return|return
name|generator
operator|.
name|generateNodeName
argument_list|(
name|authorizableId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

