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
name|nodetype
operator|.
name|NodeTypeManagerImpl
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
name|security
operator|.
name|privilege
operator|.
name|PrivilegeManagerImpl
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
name|NamespaceRegistryImpl
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

begin_comment
comment|/**  * {@code WorkspaceImpl}...  */
end_comment

begin_class
specifier|public
class|class
name|WorkspaceImpl
implements|implements
name|JackrabbitWorkspace
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
name|WorkspaceImpl
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
name|NodeTypeManager
name|nodeTypeManager
decl_stmt|;
specifier|private
specifier|final
name|QueryManagerImpl
name|queryManager
decl_stmt|;
specifier|public
name|WorkspaceImpl
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionDelegate
expr_stmt|;
name|this
operator|.
name|nodeTypeManager
operator|=
operator|new
name|NodeTypeManagerImpl
argument_list|(
name|sessionDelegate
operator|.
name|getValueFactory
argument_list|()
argument_list|,
name|sessionDelegate
operator|.
name|getNamePathMapper
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryManager
operator|=
operator|new
name|QueryManagerImpl
argument_list|(
name|sessionDelegate
argument_list|)
expr_stmt|;
block|}
comment|//----------------------------------------------------------< Workspace>---
annotation|@
name|Override
specifier|public
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
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
name|String
name|oakPath
init|=
name|sessionDelegate
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
name|sessionDelegate
operator|.
name|copy
argument_list|(
name|sessionDelegate
operator|.
name|getOakPathOrThrowNotFound
argument_list|(
name|srcAbsPath
argument_list|)
argument_list|,
name|sessionDelegate
operator|.
name|getOakPathOrThrowNotFound
argument_list|(
name|oakPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
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
name|ensureIsAlive
argument_list|()
expr_stmt|;
comment|// TODO -> SPI
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"Not implemented."
argument_list|)
throw|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
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
name|ensureIsAlive
argument_list|()
expr_stmt|;
name|String
name|oakPath
init|=
name|sessionDelegate
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
name|sessionDelegate
operator|.
name|move
argument_list|(
name|sessionDelegate
operator|.
name|getOakPathOrThrowNotFound
argument_list|(
name|srcAbsPath
argument_list|)
argument_list|,
name|sessionDelegate
operator|.
name|getOakPathOrThrowNotFound
argument_list|(
name|oakPath
argument_list|)
argument_list|,
literal|false
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
name|LockManager
name|getLockManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"TODO: Workspace.getLockManager"
argument_list|)
throw|;
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
name|NamespaceRegistryImpl
argument_list|(
name|sessionDelegate
operator|.
name|getContentSession
argument_list|()
argument_list|)
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
name|sessionDelegate
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
comment|// TODO
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"TODO: Workspace.getVersionManager"
argument_list|)
throw|;
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
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
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
comment|// TODO
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"TODO: Workspace.getImportContentHandler"
argument_list|)
throw|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
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
comment|// TODO -> SPI
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"TODO: Workspace.importXML"
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
operator|new
name|PrivilegeManagerImpl
argument_list|(
name|sessionDelegate
argument_list|)
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

