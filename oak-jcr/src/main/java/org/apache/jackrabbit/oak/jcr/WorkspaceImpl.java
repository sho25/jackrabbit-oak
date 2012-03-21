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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|json
operator|.
name|FullJsonParser
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
name|json
operator|.
name|JsonValue
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
name|json
operator|.
name|JsonValue
operator|.
name|JsonObject
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
name|json
operator|.
name|UnescapingJsonTokenizer
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
name|Workspace
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
operator|.
name|Entry
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
name|Workspace
block|{
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_WORKSPACE_NAME
init|=
literal|"default"
decl_stmt|;
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
name|getOakSession
argument_list|()
operator|.
name|checkSupportedOption
argument_list|(
name|Repository
operator|.
name|LEVEL_2_SUPPORTED
argument_list|)
expr_stmt|;
name|getOakSession
argument_list|()
operator|.
name|checkIsAlive
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
name|getOakSession
argument_list|()
operator|.
name|checkSupportedOption
argument_list|(
name|Repository
operator|.
name|LEVEL_2_SUPPORTED
argument_list|)
expr_stmt|;
name|getOakSession
argument_list|()
operator|.
name|checkIsAlive
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
name|getOakSession
argument_list|()
operator|.
name|checkSupportedOption
argument_list|(
name|Repository
operator|.
name|LEVEL_2_SUPPORTED
argument_list|)
expr_stmt|;
name|getOakSession
argument_list|()
operator|.
name|checkIsAlive
argument_list|()
expr_stmt|;
comment|// TODO -> SPI
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
name|getOakSession
argument_list|()
operator|.
name|checkIsAlive
argument_list|()
expr_stmt|;
name|getOakSession
argument_list|()
operator|.
name|checkSupportedOption
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
name|getOakSession
argument_list|()
operator|.
name|checkIsAlive
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
name|NamespaceRegistry
name|getNamespaceRegistry
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|getOakSession
argument_list|()
operator|.
name|checkIsAlive
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
name|NodeTypeManager
name|getNodeTypeManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|getOakSession
argument_list|()
operator|.
name|checkIsAlive
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
name|getOakSession
argument_list|()
operator|.
name|checkSupportedOption
argument_list|(
name|Repository
operator|.
name|OPTION_OBSERVATION_SUPPORTED
argument_list|)
expr_stmt|;
name|getOakSession
argument_list|()
operator|.
name|checkIsAlive
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
name|getOakSession
argument_list|()
operator|.
name|checkIsAlive
argument_list|()
expr_stmt|;
name|getOakSession
argument_list|()
operator|.
name|checkSupportedOption
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
name|getOakSession
argument_list|()
operator|.
name|checkIsAlive
argument_list|()
expr_stmt|;
name|MicroKernel
name|microKernel
init|=
name|sessionContext
operator|.
name|getMicrokernel
argument_list|()
decl_stmt|;
name|String
name|revision
init|=
name|sessionContext
operator|.
name|getRevision
argument_list|()
decl_stmt|;
name|String
name|json
init|=
name|microKernel
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|revision
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|JsonObject
name|jsonObject
init|=
name|FullJsonParser
operator|.
name|parseObject
argument_list|(
operator|new
name|UnescapingJsonTokenizer
argument_list|(
name|json
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|workspaces
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|JsonValue
argument_list|>
name|entry
range|:
name|jsonObject
operator|.
name|value
argument_list|()
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
name|isObject
argument_list|()
condition|)
block|{
name|workspaces
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
return|return
name|workspaces
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|workspaces
operator|.
name|size
argument_list|()
index|]
argument_list|)
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
name|getOakSession
argument_list|()
operator|.
name|checkSupportedOption
argument_list|(
name|Repository
operator|.
name|LEVEL_2_SUPPORTED
argument_list|)
expr_stmt|;
name|getOakSession
argument_list|()
operator|.
name|checkIsAlive
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
name|getOakSession
argument_list|()
operator|.
name|checkSupportedOption
argument_list|(
name|Repository
operator|.
name|LEVEL_2_SUPPORTED
argument_list|)
expr_stmt|;
name|getOakSession
argument_list|()
operator|.
name|checkIsAlive
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
name|getOakSession
argument_list|()
operator|.
name|checkIsAlive
argument_list|()
expr_stmt|;
name|getOakSession
argument_list|()
operator|.
name|checkSupportedOption
argument_list|(
name|Repository
operator|.
name|OPTION_WORKSPACE_MANAGEMENT_SUPPORTED
argument_list|)
expr_stmt|;
name|createWorkspace
argument_list|(
name|sessionContext
operator|.
name|getMicrokernel
argument_list|()
argument_list|,
name|name
argument_list|)
expr_stmt|;
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
name|getOakSession
argument_list|()
operator|.
name|checkIsAlive
argument_list|()
expr_stmt|;
name|getOakSession
argument_list|()
operator|.
name|checkSupportedOption
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
name|getOakSession
argument_list|()
operator|.
name|checkIsAlive
argument_list|()
expr_stmt|;
name|getOakSession
argument_list|()
operator|.
name|checkSupportedOption
argument_list|(
name|Repository
operator|.
name|OPTION_WORKSPACE_MANAGEMENT_SUPPORTED
argument_list|)
expr_stmt|;
name|MicroKernel
name|microKernel
init|=
name|sessionContext
operator|.
name|getMicrokernel
argument_list|()
decl_stmt|;
name|String
name|revision
init|=
name|microKernel
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|microKernel
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"- \""
operator|+
name|name
operator|+
literal|'\"'
argument_list|,
name|revision
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|SessionImpl
name|getOakSession
parameter_list|()
block|{
return|return
name|sessionContext
operator|.
name|getSession
argument_list|()
return|;
block|}
specifier|static
name|void
name|createWorkspace
parameter_list|(
name|MicroKernel
name|microKernel
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|String
name|revision
init|=
name|microKernel
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|microKernel
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \""
operator|+
name|name
operator|+
literal|"\" : {}"
argument_list|,
name|revision
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

