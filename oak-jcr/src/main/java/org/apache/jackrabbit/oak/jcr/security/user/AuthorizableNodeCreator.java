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
name|nodetype
operator|.
name|ConstraintViolationException
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
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * Utility class creating the JCR nodes corresponding the a given  * authorizable ID with the following behavior:  *<ul>  *<li>Users are created below /home/users or  * the corresponding path configured.</li>  *<li>Groups are created below /home/groups or  * the corresponding path configured.</li>  *<li>Below each category authorizables are created within a human readable  * structure based on the defined intermediate path or some internal logic  * with a depth defined by the {@code defaultDepth} config option.<br>  * E.g. creating a user node for an ID 'aSmith' would result in the following  * structure assuming defaultDepth == 2 is used:  *<pre>  * + rep:security            [rep:AuthorizableFolder]  *   + rep:authorizables     [rep:AuthorizableFolder]  *     + rep:users           [rep:AuthorizableFolder]  *       + a                 [rep:AuthorizableFolder]  *         + aS              [rep:AuthorizableFolder]  * ->        + aSmith        [rep:User]  *</pre>  *</li>  *<li>In case of a user the node name is calculated from the specified UserID  * {@link Text#escapeIllegalJcrChars(String) escaping} any illegal JCR chars.  * In case of a Group the node name is calculated from the specified principal  * name circumventing any conflicts with existing ids and escaping illegal chars.</li>  *<li>If no intermediate path is passed the names of the intermediate  * folders are calculated from the leading chars of the escaped node name.</li>  *<li>If the escaped node name is shorter than the {@code defaultDepth}  * the last char is repeated.<br>  * E.g. creating a user node for an ID 'a' would result in the following  * structure assuming defaultDepth == 2 is used:  *<pre>  * + rep:security            [rep:AuthorizableFolder]  *   + rep:authorizables     [rep:AuthorizableFolder]  *     + rep:users           [rep:AuthorizableFolder]  *       + a                 [rep:AuthorizableFolder]  *         + aa              [rep:AuthorizableFolder]  * ->        + a             [rep:User]  *</pre>  *</li>  *<li>If the {@code autoExpandTree} option is {@code true} the  * user tree will be automatically expanded using additional levels if  * {@code autoExpandSize} is exceeded within a given level.</li>  *</ul>  *  * The auto-expansion of the authorizable tree is defined by the following  * steps and exceptional cases:  *<ul>  *<li>As long as {@code autoExpandSize} isn't reached authorizable  * nodes are created within the structure defined by the  * {@code defaultDepth}. (see above)</li>  *<li>If {@code autoExpandSize} is reached additional intermediate  * folders will be created.<br>  * E.g. creating a user node for an ID 'aSmith1001' would result in the  * following structure:  *<pre>  * + rep:security            [rep:AuthorizableFolder]  *   + rep:authorizables     [rep:AuthorizableFolder]  *     + rep:users           [rep:AuthorizableFolder]  *       + a                 [rep:AuthorizableFolder]  *         + aS              [rep:AuthorizableFolder]  *           + aSmith1       [rep:User]  *           + aSmith2       [rep:User]  *           [...]  *           + aSmith1000    [rep:User]  * ->        + aSm           [rep:AuthorizableFolder]  * ->          + aSmith1001  [rep:User]  *</pre>  *</li>  *<li>Conflicts: In order to prevent any conflicts that would arise from  * creating a authorizable node that upon later expansion could conflict  * with an authorizable folder, intermediate levels are always created if  * the node name equals any of the names reserved for the next level of  * folders.<br>  * In the example above any attempt to create a user with ID 'aSm' would  * result in an intermediate level irrespective if max-size has been  * reached or not:  *<pre>  * + rep:security            [rep:AuthorizableFolder]  *   + rep:authorizables     [rep:AuthorizableFolder]  *     + rep:users           [rep:AuthorizableFolder]  *       + a                 [rep:AuthorizableFolder]  *         + aS              [rep:AuthorizableFolder]  * ->        + aSm           [rep:AuthorizableFolder]  * ->          + aSm         [rep:User]  *</pre>  *</li>  *<li>Special case: If the name of the authorizable node to be created is  * shorter or equal to the length of the folder at level N, the authorizable  * node is created even if max-size has been reached before.<br>  * An attempt to create the users 'aS' and 'aSm' in a structure containing  * tons of 'aSmith' users will therefore result in:  *<pre>  * + rep:security            [rep:AuthorizableFolder]  *   + rep:authorizables     [rep:AuthorizableFolder]  *     + rep:users           [rep:AuthorizableFolder]  *       + a                 [rep:AuthorizableFolder]  *         + aS              [rep:AuthorizableFolder]  *           + aSmith1       [rep:User]  *           + aSmith2       [rep:User]  *           [...]  *           + aSmith1000    [rep:User]  * ->        + aS            [rep:User]  *           + aSm           [rep:AuthorizableFolder]  *             + aSmith1001  [rep:User]  * ->          + aSm         [rep:User]  *</pre>  *</li>  *<li>Special case: If {@code autoExpandTree} is enabled later on  * AND any of the existing authorizable nodes collides with an intermediate  * folder to be created the auto-expansion is aborted and the new  * authorizable is inserted at the last valid level irrespective of  * max-size being reached.  *</li>  *</ul>  *  * The configuration options:  *<ul>  *<li><strong>defaultDepth</strong>:<br>  * A positive {@code integer} greater than zero defining the depth of  * the default structure that is always created.<br>  * Default value: 2</li>  *<li><strong>autoExpandTree</strong>:<br>  * {@code boolean} defining if the tree gets automatically expanded  * if within a level the maximum number of child nodes is reached.<br>  * Default value: {@code false}</li>  *<li><strong>autoExpandSize</strong>:<br>  * A positive {@code long} greater than zero defining the maximum  * number of child nodes that are allowed at a given level.<br>  * Default value: 1000<br>  * NOTE: that total number of child nodes may still be greater that  * autoExpandSize.</li>  *</ul>  */
end_comment

begin_class
class|class
name|AuthorizableNodeCreator
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
name|AuthorizableNodeCreator
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
specifier|static
specifier|final
name|int
name|DEFAULT_DEPTH
init|=
literal|2
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_SIZE
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|private
specifier|final
name|int
name|defaultDepth
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|autoExpandTree
decl_stmt|;
specifier|private
specifier|final
name|long
name|autoExpandSize
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
specifier|private
specifier|final
name|String
name|ntAuthorizableFolder
decl_stmt|;
specifier|private
specifier|final
name|String
name|ntAuthorizable
decl_stmt|;
name|AuthorizableNodeCreator
parameter_list|(
name|SessionDelegate
name|sessionDelegate
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
name|defaultDepth
operator|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|UserManagerConfig
operator|.
name|PARAM_DEFAULT_DEPTH
argument_list|,
name|DEFAULT_DEPTH
argument_list|)
expr_stmt|;
name|autoExpandTree
operator|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|UserManagerConfig
operator|.
name|PARAM_AUTO_EXPAND_TREE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|autoExpandSize
operator|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|UserManagerConfig
operator|.
name|PARAM_AUTO_EXPAND_SIZE
argument_list|,
name|DEFAULT_SIZE
argument_list|)
expr_stmt|;
name|groupPath
operator|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|UserManagerConfig
operator|.
name|PARAM_GROUP_PATH
argument_list|,
literal|"/rep:security/rep:authorizables/rep:groups"
argument_list|)
expr_stmt|;
name|userPath
operator|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|UserManagerConfig
operator|.
name|PARAM_USER_PATH
argument_list|,
literal|"/rep:security/rep:authorizables/rep:users"
argument_list|)
expr_stmt|;
name|NamePathMapper
name|namePathMapper
init|=
name|sessionDelegate
operator|.
name|getNamePathMapper
argument_list|()
decl_stmt|;
name|ntAuthorizableFolder
operator|=
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|UserConstants
operator|.
name|NT_REP_AUTHORIZABLE_FOLDER
argument_list|)
expr_stmt|;
name|ntAuthorizable
operator|=
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|UserConstants
operator|.
name|NT_REP_AUTHORIZABLE
argument_list|)
expr_stmt|;
block|}
name|String
name|getNodeID
parameter_list|(
name|String
name|authorizableId
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|UUID
name|uuid
init|=
name|UUID
operator|.
name|nameUUIDFromBytes
argument_list|(
name|authorizableId
operator|.
name|toLowerCase
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|uuid
operator|.
name|toString
argument_list|()
return|;
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
literal|"Unexpected error while creating authorizable node"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|Node
name|createUserNode
parameter_list|(
name|String
name|userID
parameter_list|,
name|String
name|intermediatePath
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
name|intermediatePath
argument_list|)
return|;
block|}
name|Node
name|createGroupNode
parameter_list|(
name|String
name|groupID
parameter_list|,
name|String
name|intermediatePath
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
name|intermediatePath
argument_list|)
return|;
block|}
specifier|private
name|Node
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
name|Node
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
name|UserConstants
operator|.
name|NT_REP_GROUP
else|:
name|UserConstants
operator|.
name|NT_REP_USER
decl_stmt|;
name|Node
name|authorizableNode
init|=
name|folder
operator|.
name|addNode
argument_list|(
name|nodeName
argument_list|,
name|ntName
argument_list|)
decl_stmt|;
name|String
name|nodeID
init|=
name|getNodeID
argument_list|(
name|authorizableId
argument_list|)
decl_stmt|;
name|CoreValue
name|idValue
init|=
name|ValueConverter
operator|.
name|toCoreValue
argument_list|(
name|nodeID
argument_list|,
name|PropertyType
operator|.
name|STRING
argument_list|,
name|sessionDelegate
argument_list|)
decl_stmt|;
name|sessionDelegate
operator|.
name|getNode
argument_list|(
name|authorizableNode
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|,
name|idValue
argument_list|)
expr_stmt|;
return|return
name|folder
operator|.
name|getNode
argument_list|(
name|nodeName
argument_list|)
return|;
block|}
comment|/**      * Create folder structure for the authorizable to be created. The structure      * consists of a tree of rep:AuthorizableFolder node(s) starting at the      * configured user or group path. Note that Authorizable nodes are never      * nested.      *      * @param authorizableId      * @param nodeName      * @param isGroup      * @param intermediatePath      * @return      * @throws RepositoryException      */
specifier|private
name|Node
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
name|Session
name|session
init|=
name|sessionDelegate
operator|.
name|getSession
argument_list|()
decl_stmt|;
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
name|Node
name|folder
decl_stmt|;
if|if
condition|(
operator|!
name|session
operator|.
name|nodeExists
argument_list|(
name|authRoot
argument_list|)
condition|)
block|{
name|folder
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
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
if|if
condition|(
name|folder
operator|.
name|hasNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|folder
operator|=
name|folder
operator|.
name|getNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|folder
operator|=
name|folder
operator|.
name|addNode
argument_list|(
name|name
argument_list|,
name|ntAuthorizableFolder
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|folder
operator|=
name|session
operator|.
name|getNode
argument_list|(
name|authRoot
argument_list|)
expr_stmt|;
block|}
name|String
name|folderPath
init|=
name|getFolderPath
argument_list|(
name|authorizableId
argument_list|,
name|intermediatePath
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
name|folder
operator|.
name|hasNode
argument_list|(
name|segment
argument_list|)
condition|)
block|{
name|folder
operator|=
name|folder
operator|.
name|getNode
argument_list|(
name|segment
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|folder
operator|.
name|isNodeType
argument_list|(
name|ntAuthorizableFolder
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Cannot create user/group: Intermediate folders must be of type rep:AuthorizableFolder."
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|folder
operator|=
name|folder
operator|.
name|addNode
argument_list|(
name|segment
argument_list|,
name|ntAuthorizableFolder
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|intermediatePath
operator|==
literal|null
operator|&&
name|autoExpandTree
condition|)
block|{
name|folder
operator|=
name|expandTree
argument_list|(
name|authorizableId
argument_list|,
name|nodeName
argument_list|,
name|folder
argument_list|)
expr_stmt|;
block|}
comment|// test for colliding folder child node.
while|while
condition|(
name|folder
operator|.
name|hasNode
argument_list|(
name|nodeName
argument_list|)
condition|)
block|{
name|Node
name|colliding
init|=
name|folder
operator|.
name|getNode
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|colliding
operator|.
name|isNodeType
argument_list|(
name|UserConstants
operator|.
name|NT_REP_AUTHORIZABLE_FOLDER
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Existing folder node collides with user/group to be created. Expanding path: "
operator|+
name|colliding
operator|.
name|getPath
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
literal|"' : Detected conflicting node of unexpected node type '"
operator|+
name|colliding
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
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
if|if
condition|(
operator|!
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|authRoot
argument_list|,
name|folder
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Attempt to create user/group outside of configured scope "
operator|+
name|authRoot
argument_list|)
throw|;
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
parameter_list|)
block|{
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
comment|/**      * Expand the tree structure adding additional folders if any of the      * following conditions is met:      *<ul>      *<li>number of child node exceeds the configured max value</li>      *<li>the authorizable node collides with an intermediate folder</li>      *</ul>      *      * @param authorizableId The authorizable id      * @param nodeName The name of the authorizable node.      * @param folder The folder node.      * @return The node in the authorizable folder tree underneath with the      * authorizable node will be created.      * @throws RepositoryException If an error occurs.      */
specifier|private
name|Node
name|expandTree
parameter_list|(
name|String
name|authorizableId
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|Node
name|folder
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|int
name|segmLength
init|=
name|defaultDepth
operator|+
literal|1
decl_stmt|;
while|while
condition|(
name|isExpand
argument_list|(
name|folder
argument_list|,
name|nodeName
operator|.
name|length
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|folderName
init|=
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
name|authorizableId
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|segmLength
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|folder
operator|.
name|hasNode
argument_list|(
name|folderName
argument_list|)
condition|)
block|{
name|Node
name|n
init|=
name|folder
operator|.
name|getNode
argument_list|(
name|folderName
argument_list|)
decl_stmt|;
comment|// assert that the folder is of type rep:AuthorizableFolder
if|if
condition|(
name|n
operator|.
name|isNodeType
argument_list|(
name|ntAuthorizableFolder
argument_list|)
condition|)
block|{
name|folder
operator|=
name|n
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|n
operator|.
name|isNodeType
argument_list|(
name|ntAuthorizable
argument_list|)
condition|)
block|{
comment|/*                      an authorizable node has been created before with the                      name of the intermediate folder to be created.                      this may only occur if the 'autoExpandTree' option has                      been enabled later on.                      Resolution:                      - abort auto-expanding and create the authorizable                        at the current level, ignoring that max-size is reached.                      - note, that this behavior has been preferred over tmp.                        removing and recreating the colliding authorizable node.                     */
name|log
operator|.
name|warn
argument_list|(
literal|"Auto-expanding aborted. An existing authorizable node '"
operator|+
name|n
operator|.
name|getName
argument_list|()
operator|+
literal|"' conflicts with intermediate folder to be created."
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
block|{
comment|// should never get here: some other, unexpected node type
name|String
name|msg
init|=
literal|"Failed to create authorizable node: Detected conflict with node of unexpected nodetype '"
operator|+
name|n
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
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
else|else
block|{
comment|// folder doesn't exist nor does another colliding child node.
name|folder
operator|=
name|folder
operator|.
name|addNode
argument_list|(
name|folderName
argument_list|,
name|ntAuthorizableFolder
argument_list|)
expr_stmt|;
block|}
name|segmLength
operator|++
expr_stmt|;
block|}
return|return
name|folder
return|;
block|}
specifier|private
name|boolean
name|isExpand
parameter_list|(
name|Node
name|folder
parameter_list|,
name|int
name|nameLength
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|int
name|folderNameLength
init|=
name|folder
operator|.
name|getName
argument_list|()
operator|.
name|length
argument_list|()
decl_stmt|;
comment|// don't create additional intermediate folders for ids that are
comment|// shorter or equally long as the folder name. In this case the
comment|// MAX_SIZE flag is ignored.
if|if
condition|(
name|nameLength
operator|<=
name|folderNameLength
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// test for potential (or existing) collision in which case the
comment|// intermediate node is created irrespective of the MAX_SIZE and the
comment|// existing number of children.
if|if
condition|(
name|nameLength
operator|==
name|folderNameLength
operator|+
literal|1
condition|)
block|{
comment|// max-size may not yet be reached yet on folder but the node to
comment|// be created potentially collides with an intermediate folder.
comment|// e.g.:
comment|// existing folder structure: a/ab
comment|// authID to be created     : abt
comment|// OR
comment|// existing collision that would result from
comment|// existing folder structure: a/ab/abt
comment|// authID to be create      : abt
return|return
literal|true
return|;
block|}
comment|// last possibility: max-size is reached.
if|if
condition|(
name|folder
operator|.
name|getNodes
argument_list|()
operator|.
name|getSize
argument_list|()
operator|>=
name|autoExpandSize
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// no collision and no need to create an additional intermediate
comment|// folder due to max-size reached
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

