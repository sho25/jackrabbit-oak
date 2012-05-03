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
name|Credentials
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
name|SessionDelegate
name|dlg
decl_stmt|;
name|SessionImpl
parameter_list|(
name|SessionDelegate
name|dlg
parameter_list|)
block|{
name|this
operator|.
name|dlg
operator|=
name|dlg
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
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"TODO: Session.impersonate"
argument_list|)
throw|;
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
name|dlg
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
operator|new
name|NodeDelegate
argument_list|(
name|dlg
argument_list|,
name|dlg
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
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
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"TODO: Session.getNodeByUUID"
argument_list|)
throw|;
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
if|if
condition|(
name|id
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
condition|)
block|{
return|return
name|getNode
argument_list|(
name|id
argument_list|)
return|;
block|}
else|else
block|{
comment|// TODO
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"TODO: Session.getNodeByIdentifier"
argument_list|)
throw|;
block|}
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
name|dlg
operator|.
name|move
argument_list|(
name|toOakPath
argument_list|(
name|srcAbsPath
argument_list|)
argument_list|,
name|toOakPath
argument_list|(
name|destAbsPath
argument_list|)
argument_list|,
literal|true
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
comment|//----------------------------------------------------------< Lifecycle>---
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
return|return
name|internalGetImportContentHandler
argument_list|(
name|toOakPath
argument_list|(
name|parentAbsPath
argument_list|)
argument_list|,
name|uuidBehavior
argument_list|)
return|;
block|}
specifier|private
name|ContentHandler
name|internalGetImportContentHandler
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
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"TODO: Session.getImportContentHandler"
argument_list|)
throw|;
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
return|return
name|internalHasPermission
argument_list|(
name|toOakPath
argument_list|(
name|absPath
argument_list|)
argument_list|,
name|actions
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|internalHasPermission
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
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"TODO: Session.getAccessControlManager"
argument_list|)
throw|;
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
specifier|private
name|String
name|toOakPath
parameter_list|(
name|String
name|jcrPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
return|return
name|dlg
operator|.
name|getNamePathMapper
argument_list|()
operator|.
name|toOakPath
argument_list|(
name|jcrPath
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// TODO we shouldn't have to catch this one
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

