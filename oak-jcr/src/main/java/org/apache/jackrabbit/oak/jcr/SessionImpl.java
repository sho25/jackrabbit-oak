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
name|oak
operator|.
name|api
operator|.
name|Branch
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
name|api
operator|.
name|ContentTree
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
name|InvalidItemStateException
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
name|AccessControlManager
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
name|security
operator|.
name|AccessControlException
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
name|GlobalContext
name|globalContext
decl_stmt|;
specifier|private
specifier|final
name|ContentSession
name|contentSession
decl_stmt|;
specifier|private
specifier|final
name|ValueFactory
name|valueFactory
decl_stmt|;
specifier|private
specifier|final
name|Workspace
name|workspace
decl_stmt|;
specifier|private
specifier|final
name|SessionContext
argument_list|<
name|SessionImpl
argument_list|>
name|sessionContext
init|=
operator|new
name|Context
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|isAlive
init|=
literal|true
decl_stmt|;
specifier|private
name|Branch
name|branch
decl_stmt|;
name|SessionImpl
parameter_list|(
name|GlobalContext
name|globalContext
parameter_list|,
name|ContentSession
name|contentSession
parameter_list|)
block|{
name|this
operator|.
name|globalContext
operator|=
name|globalContext
expr_stmt|;
name|this
operator|.
name|contentSession
operator|=
name|contentSession
expr_stmt|;
name|this
operator|.
name|valueFactory
operator|=
operator|new
name|ValueFactoryImpl
argument_list|()
expr_stmt|;
name|workspace
operator|=
operator|new
name|WorkspaceImpl
argument_list|(
name|sessionContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|branch
operator|=
name|contentSession
operator|.
name|branchRoot
argument_list|()
expr_stmt|;
block|}
comment|//------------------------------------------------------------< Session>---
annotation|@
name|Override
specifier|public
name|Repository
name|getRepository
parameter_list|()
block|{
return|return
name|globalContext
operator|.
name|getInstance
argument_list|(
name|Repository
operator|.
name|class
argument_list|)
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
name|contentSession
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
name|contentSession
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
name|contentSession
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
specifier|public
name|Workspace
name|getWorkspace
parameter_list|()
block|{
return|return
name|workspace
return|;
block|}
comment|/**      * @see javax.jcr.Session#impersonate(Credentials)      */
annotation|@
name|Override
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
comment|// TODO
return|return
literal|null
return|;
block|}
annotation|@
name|Override
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
name|sessionContext
operator|.
name|getValueFactory
argument_list|()
return|;
block|}
comment|//------------------------------------------------------------< Reading>---
annotation|@
name|Override
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
operator|new
name|NodeImpl
argument_list|(
name|sessionContext
argument_list|,
name|branch
operator|.
name|getContentTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|Node
name|getNodeByIdentifier
parameter_list|(
name|String
name|id
parameter_list|)
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
comment|//------------------------------------------------------------< Writing>---
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
name|branch
operator|.
name|move
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< state>---
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
try|try
block|{
name|contentSession
operator|.
name|commit
argument_list|(
name|branch
argument_list|)
expr_stmt|;
name|contentSession
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|branch
operator|=
name|contentSession
operator|.
name|branchRoot
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
name|contentSession
operator|.
name|refresh
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|keepChanges
condition|)
block|{
name|branch
operator|=
name|contentSession
operator|.
name|branchRoot
argument_list|()
expr_stmt|;
block|}
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
comment|// todo implement hasPendingChanges
return|return
literal|false
return|;
block|}
comment|//----------------------------------------------------------< Lifecycle>---
annotation|@
name|Override
specifier|public
name|boolean
name|isLive
parameter_list|()
block|{
return|return
name|isAlive
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|logout
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isAlive
condition|)
block|{
comment|// ignore
return|return;
block|}
name|isAlive
operator|=
literal|false
expr_stmt|;
comment|// TODO
try|try
block|{
name|contentSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error while closing connection"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|//----------------------------------------------------< Import / Export>---
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
return|return
literal|null
return|;
block|}
comment|//------------------------------------------------------------< Locking>---
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
name|getWorkspace
argument_list|()
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
specifier|public
name|String
index|[]
name|getLockTokens
parameter_list|()
block|{
try|try
block|{
return|return
name|getWorkspace
argument_list|()
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
name|getWorkspace
argument_list|()
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
comment|//------------------------------------------------------< AccessControl>---
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
comment|// TODO
return|return
literal|false
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
specifier|public
name|AccessControlManager
name|getAccessControlManager
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
comment|//----------------------------------------------------------< Retention>---
annotation|@
name|Override
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
comment|//--------------------------------------------------< JackrabbitSession>---
annotation|@
name|Override
specifier|public
name|PrincipalManager
name|getPrincipalManager
parameter_list|()
throws|throws
name|AccessDeniedException
throws|,
name|UnsupportedRepositoryOperationException
throws|,
name|RepositoryException
block|{
comment|// TODO
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Implementation missing"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|UserManager
name|getUserManager
parameter_list|()
throws|throws
name|AccessDeniedException
throws|,
name|UnsupportedRepositoryOperationException
throws|,
name|RepositoryException
block|{
comment|// TODO
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Implementation missing"
argument_list|)
throw|;
block|}
comment|//--------------------------------------------------------------------------
comment|/**      * Ensure that this session is alive and throw an exception otherwise.      *      * @throws RepositoryException if this session has been rendered invalid      * for some reason (e.g. if this session has been closed explicitly by logout)      */
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
name|isAlive
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
comment|/**      * Returns true if the repository supports the given option. False otherwise.      *      * @param option Any of the option constants defined by {@link Repository}      * that either returns 'true' or 'false'. I.e.      *<ul>      *<li>{@link Repository#LEVEL_1_SUPPORTED}</li>      *<li>{@link Repository#LEVEL_2_SUPPORTED}</li>      *<li>{@link Repository#OPTION_ACCESS_CONTROL_SUPPORTED}</li>      *<li>{@link Repository#OPTION_ACTIVITIES_SUPPORTED}</li>      *<li>{@link Repository#OPTION_BASELINES_SUPPORTED}</li>      *<li>{@link Repository#OPTION_JOURNALED_OBSERVATION_SUPPORTED}</li>      *<li>{@link Repository#OPTION_LIFECYCLE_SUPPORTED}</li>      *<li>{@link Repository#OPTION_LOCKING_SUPPORTED}</li>      *<li>{@link Repository#OPTION_NODE_AND_PROPERTY_WITH_SAME_NAME_SUPPORTED}</li>      *<li>{@link Repository#OPTION_NODE_TYPE_MANAGEMENT_SUPPORTED}</li>      *<li>{@link Repository#OPTION_OBSERVATION_SUPPORTED}</li>      *<li>{@link Repository#OPTION_QUERY_SQL_SUPPORTED}</li>      *<li>{@link Repository#OPTION_RETENTION_SUPPORTED}</li>      *<li>{@link Repository#OPTION_SHAREABLE_NODES_SUPPORTED}</li>      *<li>{@link Repository#OPTION_SIMPLE_VERSIONING_SUPPORTED}</li>      *<li>{@link Repository#OPTION_TRANSACTIONS_SUPPORTED}</li>      *<li>{@link Repository#OPTION_UNFILED_CONTENT_SUPPORTED}</li>      *<li>{@link Repository#OPTION_UPDATE_MIXIN_NODE_TYPES_SUPPORTED}</li>      *<li>{@link Repository#OPTION_UPDATE_PRIMARY_NODE_TYPE_SUPPORTED}</li>      *<li>{@link Repository#OPTION_VERSIONING_SUPPORTED}</li>      *<li>{@link Repository#OPTION_WORKSPACE_MANAGEMENT_SUPPORTED}</li>      *<li>{@link Repository#OPTION_XML_EXPORT_SUPPORTED}</li>      *<li>{@link Repository#OPTION_XML_IMPORT_SUPPORTED}</li>      *<li>{@link Repository#WRITE_SUPPORTED}</li>      *</ul>      * @return true if the repository supports the given option. False otherwise.      */
name|boolean
name|isSupportedOption
parameter_list|(
name|String
name|option
parameter_list|)
block|{
name|String
name|desc
init|=
name|getRepository
argument_list|()
operator|.
name|getDescriptor
argument_list|(
name|option
argument_list|)
decl_stmt|;
comment|// if the descriptors are not available return true. the missing
comment|// functionality of the given SPI impl will in this case be detected
comment|// upon the corresponding SPI call (see JCR-3143).
return|return
operator|(
name|desc
operator|==
literal|null
operator|)
condition|?
literal|true
else|:
name|Boolean
operator|.
name|valueOf
argument_list|(
name|desc
argument_list|)
return|;
block|}
comment|/**      * Make sure the repository supports the option indicated by the given string      * and throw an exception otherwise.      *      * @param option Any of the option constants defined by {@link Repository}      * that either returns 'true' or 'false'. I.e.      *<ul>      *<li>{@link Repository#LEVEL_1_SUPPORTED}</li>      *<li>{@link Repository#LEVEL_2_SUPPORTED}</li>      *<li>{@link Repository#OPTION_ACCESS_CONTROL_SUPPORTED}</li>      *<li>{@link Repository#OPTION_ACTIVITIES_SUPPORTED}</li>      *<li>{@link Repository#OPTION_BASELINES_SUPPORTED}</li>      *<li>{@link Repository#OPTION_JOURNALED_OBSERVATION_SUPPORTED}</li>      *<li>{@link Repository#OPTION_LIFECYCLE_SUPPORTED}</li>      *<li>{@link Repository#OPTION_LOCKING_SUPPORTED}</li>      *<li>{@link Repository#OPTION_NODE_AND_PROPERTY_WITH_SAME_NAME_SUPPORTED}</li>      *<li>{@link Repository#OPTION_NODE_TYPE_MANAGEMENT_SUPPORTED}</li>      *<li>{@link Repository#OPTION_OBSERVATION_SUPPORTED}</li>      *<li>{@link Repository#OPTION_QUERY_SQL_SUPPORTED}</li>      *<li>{@link Repository#OPTION_RETENTION_SUPPORTED}</li>      *<li>{@link Repository#OPTION_SHAREABLE_NODES_SUPPORTED}</li>      *<li>{@link Repository#OPTION_SIMPLE_VERSIONING_SUPPORTED}</li>      *<li>{@link Repository#OPTION_TRANSACTIONS_SUPPORTED}</li>      *<li>{@link Repository#OPTION_UNFILED_CONTENT_SUPPORTED}</li>      *<li>{@link Repository#OPTION_UPDATE_MIXIN_NODE_TYPES_SUPPORTED}</li>      *<li>{@link Repository#OPTION_UPDATE_PRIMARY_NODE_TYPE_SUPPORTED}</li>      *<li>{@link Repository#OPTION_VERSIONING_SUPPORTED}</li>      *<li>{@link Repository#OPTION_WORKSPACE_MANAGEMENT_SUPPORTED}</li>      *<li>{@link Repository#OPTION_XML_EXPORT_SUPPORTED}</li>      *<li>{@link Repository#OPTION_XML_IMPORT_SUPPORTED}</li>      *<li>{@link Repository#WRITE_SUPPORTED}</li>      *</ul>      * @throws UnsupportedRepositoryOperationException If the given option is      * not supported.      * @throws RepositoryException If another error occurs.      * @see javax.jcr.Repository#getDescriptorKeys()      */
name|void
name|ensureSupportsOption
parameter_list|(
name|String
name|option
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|isSupportedOption
argument_list|(
name|option
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
name|option
operator|+
literal|" is not supported by this repository."
argument_list|)
throw|;
block|}
block|}
comment|/**      * Ensure that this session has no pending changes and throw an exception      * otherwise.      *      * @throws InvalidItemStateException if this nodes session has pending changes      * @throws RepositoryException      */
name|void
name|ensureNoPendingChanges
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// check for pending changes
if|if
condition|(
name|hasPendingChanges
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
literal|"Unable to perform operation. Session has pending changes."
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|InvalidItemStateException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
comment|//--------------------------------------------------------------------------
specifier|private
class|class
name|Context
implements|implements
name|SessionContext
argument_list|<
name|SessionImpl
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|SessionImpl
name|getSession
parameter_list|()
block|{
return|return
name|SessionImpl
operator|.
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|GlobalContext
name|getGlobalContext
parameter_list|()
block|{
return|return
name|globalContext
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getWorkspaceName
parameter_list|()
block|{
return|return
name|contentSession
operator|.
name|getWorkspaceName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ContentSession
name|getContentSession
parameter_list|()
block|{
return|return
name|contentSession
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValueFactory
name|getValueFactory
parameter_list|()
block|{
return|return
name|valueFactory
return|;
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
return|return
name|getWorkspace
argument_list|()
operator|.
name|getLockManager
argument_list|()
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
return|return
name|getWorkspace
argument_list|()
operator|.
name|getNodeTypeManager
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
return|return
name|getWorkspace
argument_list|()
operator|.
name|getVersionManager
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Branch
name|getBranch
parameter_list|()
block|{
return|return
name|branch
return|;
block|}
annotation|@
name|Override
specifier|public
name|ContentTree
name|getContentTree
parameter_list|(
name|NodeImpl
name|node
parameter_list|)
block|{
return|return
name|branch
operator|.
name|getContentTree
argument_list|(
name|node
operator|.
name|path
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

