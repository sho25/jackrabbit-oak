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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|InvalidSerializedDataException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NamespaceRegistry
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
name|query
operator|.
name|QueryManager
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
name|Version
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
name|api
operator|.
name|JackrabbitWorkspace
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
name|commons
operator|.
name|xml
operator|.
name|ParsingContentHandler
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
name|CommitFailedException
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
name|lock
operator|.
name|LockManagerImpl
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
name|query
operator|.
name|QueryManagerImpl
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
name|VersionManagerImpl
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
name|ImportHandler
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
name|name
operator|.
name|ReadWriteNamespaceRegistry
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
name|write
operator|.
name|ReadWriteNodeTypeManager
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
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
name|SAXException
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
name|commons
operator|.
name|PathUtils
operator|.
name|getParentPath
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
name|plugins
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
import|;
end_import

begin_comment
comment|/**  * TODO document  */
end_comment

begin_class
specifier|public
class|class
name|WorkspaceImpl
implements|implements
name|JackrabbitWorkspace
block|{
specifier|private
specifier|final
name|SessionContext
name|sessionContext
decl_stmt|;
specifier|private
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|private
specifier|final
name|QueryManagerImpl
name|queryManager
decl_stmt|;
specifier|private
specifier|final
name|LockManagerImpl
name|lockManager
decl_stmt|;
specifier|private
specifier|final
name|VersionManagerImpl
name|versionManager
decl_stmt|;
specifier|private
specifier|final
name|ReadWriteNodeTypeManager
name|nodeTypeManager
decl_stmt|;
specifier|public
name|WorkspaceImpl
parameter_list|(
specifier|final
name|SessionContext
name|sessionContext
parameter_list|)
block|{
name|this
operator|.
name|sessionContext
operator|=
name|sessionContext
expr_stmt|;
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionContext
operator|.
name|getSessionDelegate
argument_list|()
expr_stmt|;
name|this
operator|.
name|queryManager
operator|=
operator|new
name|QueryManagerImpl
argument_list|(
name|sessionContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|lockManager
operator|=
operator|new
name|LockManagerImpl
argument_list|(
name|sessionContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|versionManager
operator|=
operator|new
name|VersionManagerImpl
argument_list|(
name|sessionContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeTypeManager
operator|=
operator|new
name|ReadWriteNodeTypeManager
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|refresh
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|getSession
argument_list|()
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Tree
name|getTypes
parameter_list|()
block|{
return|return
name|sessionDelegate
operator|.
name|getRoot
argument_list|()
operator|.
name|getTree
argument_list|(
name|NODE_TYPES_PATH
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|protected
name|Root
name|getWriteRoot
parameter_list|()
block|{
return|return
name|sessionDelegate
operator|.
name|getContentSession
argument_list|()
operator|.
name|getLatestRoot
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|protected
name|ValueFactory
name|getValueFactory
parameter_list|()
block|{
return|return
name|sessionContext
operator|.
name|getValueFactory
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|protected
name|NamePathMapper
name|getNamePathMapper
parameter_list|()
block|{
return|return
name|sessionContext
return|;
block|}
block|}
expr_stmt|;
block|}
comment|//----------------------------------------------------------< Workspace>---
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Session
name|getSession
parameter_list|()
block|{
return|return
name|sessionContext
operator|.
name|getSession
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|sessionDelegate
operator|.
name|getWorkspaceName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|copy
parameter_list|(
name|String
name|srcAbsPath
parameter_list|,
name|String
name|destAbsPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|copy
argument_list|(
name|getName
argument_list|()
argument_list|,
name|srcAbsPath
argument_list|,
name|destAbsPath
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|getOakPathOrThrowNotFound
parameter_list|(
name|String
name|srcAbsPath
parameter_list|)
throws|throws
name|PathNotFoundException
block|{
return|return
name|sessionContext
operator|.
name|getOakPathOrThrowNotFound
argument_list|(
name|srcAbsPath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|copy
parameter_list|(
name|String
name|srcWorkspace
parameter_list|,
name|String
name|srcAbsPath
parameter_list|,
name|String
name|destAbsPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|String
name|srcOakPath
init|=
name|getOakPathOrThrowNotFound
argument_list|(
name|srcAbsPath
argument_list|)
decl_stmt|;
specifier|final
name|String
name|destOakPath
init|=
name|getOakPathOrThrowNotFound
argument_list|(
name|destAbsPath
argument_list|)
decl_stmt|;
comment|// TODO: use perform()
name|ensureIsAlive
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|srcWorkspace
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"Not implemented."
argument_list|)
throw|;
block|}
name|sessionDelegate
operator|.
name|checkProtectedNode
argument_list|(
name|getParentPath
argument_list|(
name|srcOakPath
argument_list|)
argument_list|)
expr_stmt|;
name|sessionDelegate
operator|.
name|checkProtectedNode
argument_list|(
name|getParentPath
argument_list|(
name|destOakPath
argument_list|)
argument_list|)
expr_stmt|;
name|SessionImpl
operator|.
name|checkIndexOnName
argument_list|(
name|sessionContext
argument_list|,
name|destAbsPath
argument_list|)
expr_stmt|;
name|sessionDelegate
operator|.
name|copy
argument_list|(
name|srcOakPath
argument_list|,
name|destOakPath
argument_list|,
name|sessionContext
operator|.
name|getAccessManager
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clone
parameter_list|(
name|String
name|srcWorkspace
parameter_list|,
name|String
name|srcAbsPath
parameter_list|,
name|String
name|destAbsPath
parameter_list|,
name|boolean
name|removeExisting
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|String
name|srcOakPath
init|=
name|getOakPathOrThrowNotFound
argument_list|(
name|srcAbsPath
argument_list|)
decl_stmt|;
specifier|final
name|String
name|destOakPath
init|=
name|getOakPathOrThrowNotFound
argument_list|(
name|destAbsPath
argument_list|)
decl_stmt|;
comment|// TODO: use perform()
name|ensureIsAlive
argument_list|()
expr_stmt|;
name|sessionDelegate
operator|.
name|checkProtectedNode
argument_list|(
name|getParentPath
argument_list|(
name|srcOakPath
argument_list|)
argument_list|)
expr_stmt|;
name|sessionDelegate
operator|.
name|checkProtectedNode
argument_list|(
name|getParentPath
argument_list|(
name|destOakPath
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"Not implemented."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|move
parameter_list|(
name|String
name|srcAbsPath
parameter_list|,
name|String
name|destAbsPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|String
name|srcOakPath
init|=
name|getOakPathOrThrowNotFound
argument_list|(
name|srcAbsPath
argument_list|)
decl_stmt|;
specifier|final
name|String
name|destOakPath
init|=
name|getOakPathOrThrowNotFound
argument_list|(
name|destAbsPath
argument_list|)
decl_stmt|;
comment|// TODO: use perform()
name|ensureIsAlive
argument_list|()
expr_stmt|;
name|sessionDelegate
operator|.
name|checkProtectedNode
argument_list|(
name|getParentPath
argument_list|(
name|srcOakPath
argument_list|)
argument_list|)
expr_stmt|;
name|sessionDelegate
operator|.
name|checkProtectedNode
argument_list|(
name|getParentPath
argument_list|(
name|destOakPath
argument_list|)
argument_list|)
expr_stmt|;
name|SessionImpl
operator|.
name|checkIndexOnName
argument_list|(
name|sessionContext
argument_list|,
name|destAbsPath
argument_list|)
expr_stmt|;
name|sessionDelegate
operator|.
name|move
argument_list|(
name|srcOakPath
argument_list|,
name|destOakPath
argument_list|,
literal|false
argument_list|,
name|sessionContext
operator|.
name|getAccessManager
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restore
parameter_list|(
name|Version
index|[]
name|versions
parameter_list|,
name|boolean
name|removeExisting
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|getVersionManager
argument_list|()
operator|.
name|restore
argument_list|(
name|versions
argument_list|,
name|removeExisting
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|LockManagerImpl
name|getLockManager
parameter_list|()
block|{
return|return
name|lockManager
return|;
block|}
annotation|@
name|Override
specifier|public
name|QueryManager
name|getQueryManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
return|return
name|queryManager
return|;
block|}
annotation|@
name|Override
specifier|public
name|NamespaceRegistry
name|getNamespaceRegistry
parameter_list|()
block|{
return|return
operator|new
name|ReadWriteNamespaceRegistry
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Tree
name|getReadTree
parameter_list|()
block|{
return|return
name|sessionDelegate
operator|.
name|getRoot
argument_list|()
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Root
name|getWriteRoot
parameter_list|()
block|{
return|return
name|sessionDelegate
operator|.
name|getContentSession
argument_list|()
operator|.
name|getLatestRoot
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|refresh
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|getSession
argument_list|()
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|ReadWriteNodeTypeManager
name|getNodeTypeManager
parameter_list|()
block|{
return|return
name|nodeTypeManager
return|;
block|}
annotation|@
name|Override
specifier|public
name|ObservationManager
name|getObservationManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
return|return
name|sessionContext
operator|.
name|getObservationManager
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
name|ensureIsAlive
argument_list|()
expr_stmt|;
return|return
name|versionManager
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getAccessibleWorkspaceNames
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
comment|// TODO -> SPI
return|return
operator|new
name|String
index|[]
block|{
name|getName
argument_list|()
block|}
return|;
block|}
annotation|@
name|Override
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
name|ensureIsAlive
argument_list|()
expr_stmt|;
return|return
operator|new
name|ImportHandler
argument_list|(
name|parentAbsPath
argument_list|,
name|sessionContext
argument_list|,
name|uuidBehavior
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|importXML
parameter_list|(
name|String
name|parentAbsPath
parameter_list|,
name|InputStream
name|in
parameter_list|,
name|int
name|uuidBehavior
parameter_list|)
throws|throws
name|IOException
throws|,
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
try|try
block|{
name|ContentHandler
name|handler
init|=
name|getImportContentHandler
argument_list|(
name|parentAbsPath
argument_list|,
name|uuidBehavior
argument_list|)
decl_stmt|;
operator|new
name|ParsingContentHandler
argument_list|(
name|handler
argument_list|)
operator|.
name|parse
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|Throwable
name|exception
init|=
name|e
operator|.
name|getException
argument_list|()
decl_stmt|;
if|if
condition|(
name|exception
operator|instanceof
name|RepositoryException
condition|)
block|{
throw|throw
operator|(
name|RepositoryException
operator|)
name|exception
throw|;
block|}
elseif|else
if|if
condition|(
name|exception
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|exception
throw|;
block|}
elseif|else
if|if
condition|(
name|exception
operator|instanceof
name|CommitFailedException
condition|)
block|{
throw|throw
operator|(
operator|(
name|CommitFailedException
operator|)
name|exception
operator|)
operator|.
name|asRepositoryException
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|InvalidSerializedDataException
argument_list|(
literal|"XML parse error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
comment|// JCR-2903
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{                 }
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|createWorkspace
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
comment|// TODO -> SPI
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"TODO: Workspace.createWorkspace"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|createWorkspace
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|srcWorkspace
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
comment|// TODO -> SPI
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"TODO: Workspace.createWorkspace"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deleteWorkspace
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
comment|// TODO -> SPI
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"TODO: Workspace.deleteWorkspace"
argument_list|)
throw|;
block|}
comment|//------------------------------------------------< JackrabbitWorkspace>---
annotation|@
name|Override
specifier|public
name|void
name|createWorkspace
parameter_list|(
name|String
name|workspaceName
parameter_list|,
name|InputSource
name|workspaceTemplate
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
comment|// TODO -> SPI
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"TODO: Workspace.createWorkspace"
argument_list|)
throw|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.JackrabbitWorkspace#getPrivilegeManager()      */
annotation|@
name|Override
specifier|public
name|PrivilegeManager
name|getPrivilegeManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|sessionContext
operator|.
name|getPrivilegeManager
argument_list|()
return|;
block|}
comment|//------------------------------------------------------------< private>---
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
name|sessionDelegate
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

