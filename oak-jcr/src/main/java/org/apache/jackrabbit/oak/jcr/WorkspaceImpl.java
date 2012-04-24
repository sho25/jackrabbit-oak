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
name|ContentSession
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
name|privileges
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
name|namepath
operator|.
name|Paths
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
name|SessionContext
argument_list|<
name|SessionImpl
argument_list|>
name|sessionContext
decl_stmt|;
specifier|private
name|QueryManagerImpl
name|queryManager
decl_stmt|;
specifier|private
name|NamespaceRegistry
name|nsRegistry
decl_stmt|;
specifier|public
name|WorkspaceImpl
parameter_list|(
name|SessionContext
argument_list|<
name|SessionImpl
argument_list|>
name|sessionContext
parameter_list|)
block|{
name|this
operator|.
name|sessionContext
operator|=
name|sessionContext
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
name|sessionContext
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
name|ensureSupportedOption
argument_list|(
name|Repository
operator|.
name|LEVEL_2_SUPPORTED
argument_list|)
expr_stmt|;
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
try|try
block|{
name|ContentSession
name|contentSession
init|=
name|sessionContext
operator|.
name|getContentSession
argument_list|()
decl_stmt|;
name|Root
name|root
init|=
name|contentSession
operator|.
name|getCurrentRoot
argument_list|()
decl_stmt|;
name|String
name|srcPath
init|=
name|Paths
operator|.
name|relativize
argument_list|(
literal|"/"
argument_list|,
name|srcAbsPath
argument_list|)
decl_stmt|;
name|String
name|destPath
init|=
name|Paths
operator|.
name|relativize
argument_list|(
literal|"/"
argument_list|,
name|destAbsPath
argument_list|)
decl_stmt|;
name|root
operator|.
name|copy
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
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
name|ensureSupportedOption
argument_list|(
name|Repository
operator|.
name|LEVEL_2_SUPPORTED
argument_list|)
expr_stmt|;
name|ensureIsAlive
argument_list|()
expr_stmt|;
comment|// TODO -> SPI
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
name|ensureSupportedOption
argument_list|(
name|Repository
operator|.
name|LEVEL_2_SUPPORTED
argument_list|)
expr_stmt|;
name|ensureIsAlive
argument_list|()
expr_stmt|;
try|try
block|{
name|ContentSession
name|contentSession
init|=
name|sessionContext
operator|.
name|getContentSession
argument_list|()
decl_stmt|;
name|Root
name|root
init|=
name|contentSession
operator|.
name|getCurrentRoot
argument_list|()
decl_stmt|;
name|String
name|srcPath
init|=
name|Paths
operator|.
name|relativize
argument_list|(
literal|"/"
argument_list|,
name|srcAbsPath
argument_list|)
decl_stmt|;
name|String
name|destPath
init|=
name|Paths
operator|.
name|relativize
argument_list|(
literal|"/"
argument_list|,
name|destAbsPath
argument_list|)
decl_stmt|;
name|root
operator|.
name|move
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
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
name|ensureSupportedOption
argument_list|(
name|Repository
operator|.
name|OPTION_LOCKING_SUPPORTED
argument_list|)
expr_stmt|;
comment|// TODO
return|return
literal|null
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
name|QueryManagerImpl
argument_list|(
name|this
argument_list|,
name|sessionContext
argument_list|)
expr_stmt|;
block|}
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
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
if|if
condition|(
name|nsRegistry
operator|==
literal|null
condition|)
block|{
name|nsRegistry
operator|=
operator|new
name|NamespaceRegistryImpl
argument_list|()
expr_stmt|;
block|}
return|return
name|nsRegistry
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeManager
name|getNodeTypeManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|ensureIsAlive
argument_list|()
expr_stmt|;
comment|// TODO
return|return
literal|null
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
name|ensureSupportedOption
argument_list|(
name|Repository
operator|.
name|OPTION_OBSERVATION_SUPPORTED
argument_list|)
expr_stmt|;
name|ensureIsAlive
argument_list|()
expr_stmt|;
comment|// TODO
return|return
literal|null
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
name|ensureSupportedOption
argument_list|(
name|Repository
operator|.
name|OPTION_VERSIONING_SUPPORTED
argument_list|)
expr_stmt|;
comment|// TODO
return|return
literal|null
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
name|ensureSupportedOption
argument_list|(
name|Repository
operator|.
name|LEVEL_2_SUPPORTED
argument_list|)
expr_stmt|;
name|ensureIsAlive
argument_list|()
expr_stmt|;
comment|// TODO
return|return
literal|null
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
name|ensureSupportedOption
argument_list|(
name|Repository
operator|.
name|LEVEL_2_SUPPORTED
argument_list|)
expr_stmt|;
name|ensureIsAlive
argument_list|()
expr_stmt|;
comment|// TODO -> SPI
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
name|ensureSupportedOption
argument_list|(
name|Repository
operator|.
name|OPTION_WORKSPACE_MANAGEMENT_SUPPORTED
argument_list|)
expr_stmt|;
comment|// TODO -> SPI
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
name|ensureSupportedOption
argument_list|(
name|Repository
operator|.
name|OPTION_WORKSPACE_MANAGEMENT_SUPPORTED
argument_list|)
expr_stmt|;
comment|// TODO -> SPI
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
name|ensureSupportedOption
argument_list|(
name|Repository
operator|.
name|OPTION_WORKSPACE_MANAGEMENT_SUPPORTED
argument_list|)
expr_stmt|;
comment|// TODO -> SPI
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
name|ensureSupportedOption
argument_list|(
name|Repository
operator|.
name|OPTION_WORKSPACE_MANAGEMENT_SUPPORTED
argument_list|)
expr_stmt|;
comment|// TODO -> SPI
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
name|sessionContext
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
name|sessionContext
operator|.
name|getSession
argument_list|()
operator|.
name|ensureIsAlive
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|ensureSupportedOption
parameter_list|(
name|String
name|option
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|sessionContext
operator|.
name|getSession
argument_list|()
operator|.
name|ensureSupportsOption
argument_list|(
name|option
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

