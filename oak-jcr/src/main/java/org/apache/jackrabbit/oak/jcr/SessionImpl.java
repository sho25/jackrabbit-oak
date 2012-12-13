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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|Credentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Item
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
name|NamespaceException
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
name|Property
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
name|retention
operator|.
name|RetentionManager
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
name|AccessControlException
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
name|security
operator|.
name|AccessControlPolicy
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
name|AccessControlPolicyIterator
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
name|Privilege
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
name|JackrabbitSession
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
name|commons
operator|.
name|AbstractSession
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
name|commons
operator|.
name|iterator
operator|.
name|AccessControlPolicyIteratorAdapter
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
name|jcr
operator|.
name|xml
operator|.
name|XmlImportHandler
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
name|ImpersonationCredentials
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
name|TODO
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
name|apache
operator|.
name|jackrabbit
operator|.
name|util
operator|.
name|XMLChar
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_comment
comment|/**  * {@code SessionImpl}...  */
end_comment

begin_class
specifier|public
class|class
name|SessionImpl
extends|extends
name|AbstractSession
implements|implements
name|JackrabbitSession
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
name|SessionImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SessionDelegate
name|dlg
decl_stmt|;
comment|/**      * Local namespace remappings. Prefixes as keys and namespace URIs as values.      *<p>      * This map is only accessed from synchronized methods (see      *<a href="https://issues.apache.org/jira/browse/JCR-1793">JCR-1793</a>).      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
decl_stmt|;
name|SessionImpl
parameter_list|(
name|SessionDelegate
name|dlg
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|)
block|{
name|this
operator|.
name|dlg
operator|=
name|dlg
expr_stmt|;
name|this
operator|.
name|namespaces
operator|=
name|namespaces
expr_stmt|;
block|}
comment|//------------------------------------------------------------< Session>---
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Repository
name|getRepository
parameter_list|()
block|{
return|return
name|dlg
operator|.
name|getRepository
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getUserID
parameter_list|()
block|{
return|return
name|dlg
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getUserID
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getAttributeNames
parameter_list|()
block|{
return|return
name|dlg
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getAttributeNames
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|dlg
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Workspace
name|getWorkspace
parameter_list|()
block|{
return|return
name|dlg
operator|.
name|getWorkspace
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Session
name|impersonate
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
name|ImpersonationCredentials
name|impCreds
init|=
operator|new
name|ImpersonationCredentials
argument_list|(
name|credentials
argument_list|,
name|dlg
operator|.
name|getAuthInfo
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|impCreds
argument_list|,
name|dlg
operator|.
name|getWorkspaceName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|ValueFactory
name|getValueFactory
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
return|return
name|dlg
operator|.
name|getValueFactory
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Node
name|getRootNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
return|return
name|dlg
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|NodeImpl
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeImpl
name|perform
parameter_list|()
throws|throws
name|AccessDeniedException
block|{
name|NodeDelegate
name|nd
init|=
name|dlg
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|nd
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AccessDeniedException
argument_list|(
literal|"Root node is not accessible."
argument_list|)
throw|;
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
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Node
name|getNodeByUUID
parameter_list|(
name|String
name|uuid
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|getNodeByIdentifier
argument_list|(
name|uuid
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Node
name|getNodeByIdentifier
parameter_list|(
specifier|final
name|String
name|id
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
return|return
name|dlg
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|NodeImpl
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeImpl
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|NodeDelegate
name|d
init|=
name|dlg
operator|.
name|getNodeByIdentifier
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ItemNotFoundException
argument_list|(
literal|"Node with id "
operator|+
name|id
operator|+
literal|" does not exist."
argument_list|)
throw|;
block|}
return|return
operator|new
name|NodeImpl
argument_list|<
name|NodeDelegate
argument_list|>
argument_list|(
name|d
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Item
name|getItem
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|nodeExists
argument_list|(
name|absPath
argument_list|)
condition|)
block|{
return|return
name|getNode
argument_list|(
name|absPath
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getProperty
argument_list|(
name|absPath
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|itemExists
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|nodeExists
argument_list|(
name|absPath
argument_list|)
operator|||
name|propertyExists
argument_list|(
name|absPath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|getNode
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
return|return
name|dlg
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|NodeImpl
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeImpl
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|dlg
operator|.
name|getOakPathOrThrow
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
name|NodeDelegate
name|d
init|=
name|dlg
operator|.
name|getNode
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
literal|"Node with path "
operator|+
name|absPath
operator|+
literal|" does not exist."
argument_list|)
throw|;
block|}
return|return
operator|new
name|NodeImpl
argument_list|<
name|NodeDelegate
argument_list|>
argument_list|(
name|d
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|nodeExists
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
return|return
name|dlg
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|dlg
operator|.
name|getOakPathOrThrow
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
return|return
name|dlg
operator|.
name|getNode
argument_list|(
name|oakPath
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Property
name|getProperty
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|absPath
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"The root node is not a property"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|dlg
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|PropertyImpl
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PropertyImpl
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|dlg
operator|.
name|getOakPathOrThrowNotFound
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
name|TreeLocation
name|loc
init|=
name|dlg
operator|.
name|getLocation
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|loc
operator|.
name|getProperty
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|absPath
argument_list|)
throw|;
block|}
else|else
block|{
return|return
operator|new
name|PropertyImpl
argument_list|(
operator|new
name|PropertyDelegate
argument_list|(
name|dlg
argument_list|,
name|loc
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyExists
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|absPath
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"The root node is not a property"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|dlg
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|dlg
operator|.
name|getOakPathOrThrowNotFound
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
name|TreeLocation
name|loc
init|=
name|dlg
operator|.
name|getLocation
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
return|return
name|loc
operator|.
name|getProperty
argument_list|()
operator|!=
literal|null
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|move
parameter_list|(
specifier|final
name|String
name|srcAbsPath
parameter_list|,
specifier|final
name|String
name|destAbsPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
name|dlg
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|dlg
operator|.
name|checkProtectedNodes
argument_list|(
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|srcAbsPath
argument_list|,
literal|1
argument_list|)
argument_list|,
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|destAbsPath
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|oakPath
init|=
name|dlg
operator|.
name|getOakPathKeepIndexOrThrowNotFound
argument_list|(
name|destAbsPath
argument_list|)
decl_stmt|;
name|String
name|oakName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
comment|// handle index
if|if
condition|(
name|oakName
operator|.
name|contains
argument_list|(
literal|"["
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Cannot create a new node using a name including an index"
argument_list|)
throw|;
block|}
name|dlg
operator|.
name|move
argument_list|(
name|dlg
operator|.
name|getOakPathOrThrowNotFound
argument_list|(
name|srcAbsPath
argument_list|)
argument_list|,
name|dlg
operator|.
name|getOakPathOrThrowNotFound
argument_list|(
name|oakPath
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|save
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
name|dlg
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|refresh
parameter_list|(
name|boolean
name|keepChanges
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
name|dlg
operator|.
name|refresh
argument_list|(
name|keepChanges
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPendingChanges
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
return|return
name|dlg
operator|.
name|hasPendingChanges
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isLive
parameter_list|()
block|{
return|return
name|dlg
operator|.
name|isAlive
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|logout
parameter_list|()
block|{
name|dlg
operator|.
name|logout
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|namespaces
init|)
block|{
name|namespaces
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|ContentHandler
name|getImportContentHandler
parameter_list|(
name|String
name|parentAbsPath
parameter_list|,
name|int
name|uuidBehavior
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|Node
name|parent
init|=
name|getNode
argument_list|(
name|parentAbsPath
argument_list|)
decl_stmt|;
return|return
operator|new
name|XmlImportHandler
argument_list|(
name|parent
argument_list|,
name|uuidBehavior
argument_list|)
return|;
block|}
comment|/**      * @see javax.jcr.Session#addLockToken(String)      */
annotation|@
name|Override
specifier|public
name|void
name|addLockToken
parameter_list|(
name|String
name|lt
parameter_list|)
block|{
try|try
block|{
name|dlg
operator|.
name|getLockManager
argument_list|()
operator|.
name|addLockToken
argument_list|(
name|lt
argument_list|)
expr_stmt|;
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
literal|"Unable to add lock token '{}' to this session: {}"
argument_list|,
name|lt
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @see javax.jcr.Session#getLockTokens()      */
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
index|[]
name|getLockTokens
parameter_list|()
block|{
try|try
block|{
return|return
name|dlg
operator|.
name|getLockManager
argument_list|()
operator|.
name|getLockTokens
argument_list|()
return|;
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
literal|"Unable to retrieve lock tokens for this session: {}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
block|}
comment|/**      * @see javax.jcr.Session#removeLockToken(String)      */
annotation|@
name|Override
specifier|public
name|void
name|removeLockToken
parameter_list|(
name|String
name|lt
parameter_list|)
block|{
try|try
block|{
name|dlg
operator|.
name|getLockManager
argument_list|()
operator|.
name|addLockToken
argument_list|(
name|lt
argument_list|)
expr_stmt|;
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
literal|"Unable to add lock token '{}' to this session: {}"
argument_list|,
name|lt
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPermission
parameter_list|(
name|String
name|absPath
parameter_list|,
name|String
name|actions
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
name|String
name|oakPath
init|=
name|dlg
operator|.
name|getOakPathOrNull
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPath
operator|==
literal|null
condition|)
block|{
comment|// TODO should we throw an exception here?
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
literal|false
argument_list|)
return|;
block|}
comment|// TODO implement hasPermission
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
literal|true
argument_list|)
return|;
block|}
comment|/**      * @see javax.jcr.Session#checkPermission(String, String)      */
annotation|@
name|Override
specifier|public
name|void
name|checkPermission
parameter_list|(
name|String
name|absPath
parameter_list|,
name|String
name|actions
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|hasPermission
argument_list|(
name|absPath
argument_list|,
name|actions
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Access control violation: path = "
operator|+
name|absPath
operator|+
literal|", actions = "
operator|+
name|actions
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasCapability
parameter_list|(
name|String
name|methodName
parameter_list|,
name|Object
name|target
parameter_list|,
name|Object
index|[]
name|arguments
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
comment|// TODO
return|return
literal|false
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|AccessControlManager
name|getAccessControlManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
operator|new
name|AccessControlManager
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setPolicy
parameter_list|(
name|String
name|absPath
parameter_list|,
name|AccessControlPolicy
name|policy
parameter_list|)
throws|throws
name|AccessControlException
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
name|policy
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removePolicy
parameter_list|(
name|String
name|absPath
parameter_list|,
name|AccessControlPolicy
name|policy
parameter_list|)
throws|throws
name|AccessControlException
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
name|policy
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Privilege
name|privilegeFromName
parameter_list|(
name|String
name|privilegeName
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|RepositoryException
block|{
return|return
name|dlg
operator|.
name|getPrivilegeManager
argument_list|()
operator|.
name|getPrivilege
argument_list|(
name|privilegeName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPrivileges
parameter_list|(
name|String
name|absPath
parameter_list|,
name|Privilege
index|[]
name|privileges
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Privilege
index|[]
name|getSupportedPrivileges
parameter_list|(
name|String
name|absPath
parameter_list|)
block|{
return|return
operator|new
name|Privilege
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|Privilege
index|[]
name|getPrivileges
parameter_list|(
name|String
name|absPath
parameter_list|)
block|{
return|return
operator|new
name|Privilege
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|AccessControlPolicy
index|[]
name|getPolicies
parameter_list|(
name|String
name|absPath
parameter_list|)
block|{
return|return
operator|new
name|AccessControlPolicy
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|AccessControlPolicy
index|[]
name|getEffectivePolicies
parameter_list|(
name|String
name|absPath
parameter_list|)
block|{
return|return
operator|new
name|AccessControlPolicy
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|AccessControlPolicyIterator
name|getApplicablePolicies
parameter_list|(
name|String
name|absPath
parameter_list|)
block|{
return|return
name|AccessControlPolicyIteratorAdapter
operator|.
name|EMPTY
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * @see javax.jcr.Session#getRetentionManager()      */
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|RetentionManager
name|getRetentionManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"Retention Management is not supported."
argument_list|)
throw|;
block|}
comment|//---------------------------------------------------------< Namespaces>---
comment|// The code below was initially copied from JCR Commons AbstractSession, but
comment|// provides information the "hasRemappings" information
annotation|@
name|Override
specifier|public
name|void
name|setNamespacePrefix
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Prefix must not be null"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Namespace must not be null"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|prefix
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NamespaceException
argument_list|(
literal|"Empty prefix is reserved and can not be remapped"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|uri
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NamespaceException
argument_list|(
literal|"Default namespace is reserved and can not be remapped"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|prefix
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"xml"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NamespaceException
argument_list|(
literal|"XML prefixes are reserved: "
operator|+
name|prefix
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|XMLChar
operator|.
name|isValidNCName
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NamespaceException
argument_list|(
literal|"Prefix is not a valid XML NCName: "
operator|+
name|prefix
argument_list|)
throw|;
block|}
synchronized|synchronized
init|(
name|namespaces
init|)
block|{
comment|// Remove existing mapping for the given prefix
name|namespaces
operator|.
name|remove
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
comment|// Remove existing mapping(s) for the given URI
name|Set
argument_list|<
name|String
argument_list|>
name|prefixes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|namespaces
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|prefixes
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|namespaces
operator|.
name|keySet
argument_list|()
operator|.
name|removeAll
argument_list|(
name|prefixes
argument_list|)
expr_stmt|;
comment|// Add the new mapping
name|namespaces
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getNamespacePrefixes
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|uris
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|uris
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|getWorkspace
argument_list|()
operator|.
name|getNamespaceRegistry
argument_list|()
operator|.
name|getURIs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|namespaces
init|)
block|{
comment|// Add namespace uris only visible to session
name|uris
operator|.
name|addAll
argument_list|(
name|namespaces
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|prefixes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|uri
range|:
name|uris
control|)
block|{
name|prefixes
operator|.
name|add
argument_list|(
name|getNamespacePrefix
argument_list|(
name|uri
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|prefixes
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|prefixes
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceURI
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|RepositoryException
block|{
synchronized|synchronized
init|(
name|namespaces
init|)
block|{
name|String
name|uri
init|=
name|namespaces
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
comment|// Not in local mappings, try the global ones
name|uri
operator|=
name|getWorkspace
argument_list|()
operator|.
name|getNamespaceRegistry
argument_list|()
operator|.
name|getURI
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
if|if
condition|(
name|namespaces
operator|.
name|containsValue
argument_list|(
name|uri
argument_list|)
condition|)
block|{
comment|// The global URI is locally mapped to some other prefix,
comment|// so there are no mappings for this prefix
throw|throw
operator|new
name|NamespaceException
argument_list|(
literal|"Namespace not found: "
operator|+
name|prefix
argument_list|)
throw|;
block|}
block|}
return|return
name|uri
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespacePrefix
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|RepositoryException
block|{
synchronized|synchronized
init|(
name|namespaces
init|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|namespaces
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
return|return
name|entry
operator|.
name|getKey
argument_list|()
return|;
block|}
block|}
comment|// The following throws an exception if the URI is not found, that's OK
name|String
name|prefix
init|=
name|getWorkspace
argument_list|()
operator|.
name|getNamespaceRegistry
argument_list|()
operator|.
name|getPrefix
argument_list|(
name|uri
argument_list|)
decl_stmt|;
comment|// Generate a new prefix if the global mapping is already taken
name|String
name|base
init|=
name|prefix
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|2
init|;
name|namespaces
operator|.
name|containsKey
argument_list|(
name|prefix
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|prefix
operator|=
name|base
operator|+
name|i
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|base
operator|.
name|equals
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|namespaces
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
return|return
name|prefix
return|;
block|}
block|}
name|boolean
name|hasSessionLocalMappings
parameter_list|()
block|{
return|return
operator|!
name|namespaces
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|//--------------------------------------------------< JackrabbitSession>---
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|PrincipalManager
name|getPrincipalManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|dlg
operator|.
name|getPrincipalManager
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|UserManager
name|getUserManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|dlg
operator|.
name|getUserManager
argument_list|()
return|;
block|}
comment|//------------------------------------------------------------< private>---
comment|/**      * Ensure that this session is alive and throw an exception otherwise.      *      * @throws RepositoryException if this session has been rendered invalid      * for some reason (e.g. if this session has been closed explicitly by logout)      */
specifier|private
name|void
name|ensureIsAlive
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// check session status
if|if
condition|(
operator|!
name|dlg
operator|.
name|isAlive
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"This session has been closed."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

