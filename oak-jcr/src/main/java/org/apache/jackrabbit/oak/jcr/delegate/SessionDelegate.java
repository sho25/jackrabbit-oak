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
name|delegate
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MILLISECONDS
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MINUTES
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|SECONDS
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
name|ItemExistsException
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
name|nodetype
operator|.
name|ConstraintViolationException
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
name|AuthInfo
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
name|QueryEngine
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
name|core
operator|.
name|IdentifierManager
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
name|operation
operator|.
name|SessionOperation
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
name|AccessManager
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
name|SecurityProvider
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
name|authorization
operator|.
name|AuthorizationConfiguration
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
name|authorization
operator|.
name|permission
operator|.
name|PermissionProvider
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
name|authorization
operator|.
name|permission
operator|.
name|Permissions
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

begin_comment
comment|/**  * TODO document  */
end_comment

begin_class
specifier|public
class|class
name|SessionDelegate
block|{
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SessionDelegate
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ContentSession
name|contentSession
decl_stmt|;
specifier|private
specifier|final
name|long
name|refreshInterval
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|private
specifier|final
name|IdentifierManager
name|idManager
decl_stmt|;
specifier|private
specifier|final
name|Exception
name|initStackTrace
decl_stmt|;
specifier|private
specifier|final
name|PermissionProvider
name|permissionProvider
decl_stmt|;
specifier|private
name|boolean
name|isAlive
init|=
literal|true
decl_stmt|;
specifier|private
name|int
name|sessionOpCount
decl_stmt|;
specifier|private
name|long
name|updateCount
init|=
literal|0
decl_stmt|;
specifier|private
name|long
name|lastAccessed
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|warnIfIdle
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|refreshAtNextAccess
init|=
literal|false
decl_stmt|;
comment|/**      * Create a new session delegate for a {@code ContentSession}. The refresh behaviour of the      * session is governed by the value of the {@code refreshInterval} argument: if the session      * has been idle longer than that value, an implicit refresh will take place.      * In addition a refresh can always be scheduled from the next access by an explicit call      * to {@link #refreshAtNextAccess()}. This is typically done from within the observation event      * dispatcher in order.      *      * @param contentSession  the content session      * @param securityProvider the security provider      * @param refreshInterval  refresh interval in seconds.      */
specifier|public
name|SessionDelegate
parameter_list|(
annotation|@
name|Nonnull
name|ContentSession
name|contentSession
parameter_list|,
name|SecurityProvider
name|securityProvider
parameter_list|,
name|long
name|refreshInterval
parameter_list|)
block|{
name|this
operator|.
name|contentSession
operator|=
name|checkNotNull
argument_list|(
name|contentSession
argument_list|)
expr_stmt|;
name|this
operator|.
name|refreshInterval
operator|=
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|refreshInterval
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|contentSession
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|this
operator|.
name|idManager
operator|=
operator|new
name|IdentifierManager
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|this
operator|.
name|initStackTrace
operator|=
operator|new
name|Exception
argument_list|(
literal|"The session was created here:"
argument_list|)
expr_stmt|;
name|this
operator|.
name|permissionProvider
operator|=
name|securityProvider
operator|.
name|getConfiguration
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getPermissionProvider
argument_list|(
name|root
argument_list|,
name|contentSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|refreshAtNextAccess
parameter_list|()
block|{
name|refreshAtNextAccess
operator|=
literal|true
expr_stmt|;
block|}
comment|/**      * Performs the passed {@code SessionOperation} in a safe execution context. This      * context ensures that the session is refreshed if necessary and that refreshing      * occurs before the session operation is performed and the refreshing is done only      * once.      *      * @param sessionOperation  the {@code SessionOperation} to perform      * @param<T>  return type of {@code sessionOperation}      * @return  the result of {@code sessionOperation.perform()}      * @throws RepositoryException      * @see #getRoot()      */
specifier|public
specifier|synchronized
parameter_list|<
name|T
parameter_list|>
name|T
name|perform
parameter_list|(
name|SessionOperation
argument_list|<
name|T
argument_list|>
name|sessionOperation
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// Synchronize to avoid conflicting refreshes from concurrent JCR API calls
if|if
condition|(
name|sessionOpCount
operator|==
literal|0
condition|)
block|{
comment|// Refresh and checks only for non re-entrant session operations
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|timeElapsed
init|=
name|now
operator|-
name|lastAccessed
decl_stmt|;
comment|// Don't refresh if this operation is a refresh operation itself
if|if
condition|(
operator|!
name|sessionOperation
operator|.
name|isRefresh
argument_list|()
condition|)
block|{
if|if
condition|(
name|warnIfIdle
operator|&&
operator|!
name|refreshAtNextAccess
operator|&&
name|timeElapsed
operator|>
name|MILLISECONDS
operator|.
name|convert
argument_list|(
literal|1
argument_list|,
name|MINUTES
argument_list|)
condition|)
block|{
comment|// Warn once if this session has been idle too long
name|log
operator|.
name|warn
argument_list|(
literal|"This session has been idle for "
operator|+
name|MINUTES
operator|.
name|convert
argument_list|(
name|timeElapsed
argument_list|,
name|MILLISECONDS
argument_list|)
operator|+
literal|" minutes and might be out of date. Consider using a fresh session or explicitly"
operator|+
literal|" refresh the session."
argument_list|,
name|initStackTrace
argument_list|)
expr_stmt|;
name|warnIfIdle
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|refreshAtNextAccess
operator|||
name|timeElapsed
operator|>=
name|refreshInterval
condition|)
block|{
comment|// Refresh if forced or if the session has been idle too long
name|refreshAtNextAccess
operator|=
literal|false
expr_stmt|;
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|updateCount
operator|++
expr_stmt|;
block|}
block|}
name|lastAccessed
operator|=
name|now
expr_stmt|;
name|sessionOperation
operator|.
name|checkPreconditions
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|sessionOpCount
operator|++
expr_stmt|;
return|return
name|sessionOperation
operator|.
name|perform
argument_list|()
return|;
block|}
finally|finally
block|{
name|sessionOpCount
operator|--
expr_stmt|;
if|if
condition|(
name|sessionOperation
operator|.
name|isUpdate
argument_list|()
condition|)
block|{
name|updateCount
operator|++
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Same as {@link #perform(SessionOperation)} unless this method expects      * {@link SessionOperation#perform}<em>not</em> to throw a {@code RepositoryException}.      * Such exceptions will be wrapped into a {@code RuntimeException} and rethrown as they      * are considered an internal error.      *      * @param sessionOperation  the {@code SessionOperation} to perform      * @param<T>  return type of {@code sessionOperation}      * @return  the result of {@code sessionOperation.perform()}      * @see #getRoot()      */
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|safePerform
parameter_list|(
name|SessionOperation
argument_list|<
name|T
argument_list|>
name|sessionOperation
parameter_list|)
block|{
try|try
block|{
return|return
name|perform
argument_list|(
name|sessionOperation
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected exception thrown by operation "
operator|+
name|sessionOperation
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nonnull
specifier|public
name|ContentSession
name|getContentSession
parameter_list|()
block|{
return|return
name|contentSession
return|;
block|}
comment|/**      * Determine whether this session is alive and has not been logged      * out or become stale by other means.      * @return {@code true} if this session is alive, {@code false} otherwise.      */
specifier|public
name|boolean
name|isAlive
parameter_list|()
block|{
return|return
name|isAlive
return|;
block|}
comment|/**      * Check that this session is alive.      * @throws RepositoryException if this session is not alive      * @see #isAlive()      */
specifier|public
name|void
name|checkAlive
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
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
comment|/**      * @return session update counter      */
specifier|public
name|long
name|getUpdateCount
parameter_list|()
block|{
return|return
name|updateCount
return|;
block|}
specifier|public
name|void
name|checkProtectedNode
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|NodeDelegate
name|node
init|=
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
literal|"Node "
operator|+
name|path
operator|+
literal|" does not exist."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|isProtected
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Node "
operator|+
name|path
operator|+
literal|" is protected."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nonnull
specifier|public
name|AuthInfo
name|getAuthInfo
parameter_list|()
block|{
return|return
name|contentSession
operator|.
name|getAuthInfo
argument_list|()
return|;
block|}
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
annotation|@
name|Nonnull
specifier|public
name|IdentifierManager
name|getIdManager
parameter_list|()
block|{
return|return
name|idManager
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|NodeDelegate
name|getRootNode
parameter_list|()
block|{
return|return
name|getNode
argument_list|(
literal|"/"
argument_list|)
return|;
block|}
comment|/**      * {@code NodeDelegate} at the given path      * @param path Oak path      * @return  The {@code NodeDelegate} at {@code path} or {@code null} if      * none exists or not accessible.      */
annotation|@
name|CheckForNull
specifier|public
name|NodeDelegate
name|getNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|NodeDelegate
operator|.
name|create
argument_list|(
name|this
argument_list|,
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns the node or property delegate at the given path.      *      * @param path Oak path      * @return node or property delegate, or {@code null} if none exists      */
annotation|@
name|CheckForNull
specifier|public
name|ItemDelegate
name|getItem
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|getRootNode
argument_list|()
return|;
block|}
else|else
block|{
name|Tree
name|parent
init|=
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|PropertyDelegate
operator|.
name|create
argument_list|(
name|this
argument_list|,
name|parent
argument_list|,
name|name
argument_list|)
return|;
block|}
name|Tree
name|child
init|=
name|parent
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|NodeDelegate
operator|.
name|create
argument_list|(
name|this
argument_list|,
name|child
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
block|}
annotation|@
name|CheckForNull
specifier|public
name|NodeDelegate
name|getNodeByIdentifier
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|idManager
operator|.
name|getTree
argument_list|(
name|id
argument_list|)
decl_stmt|;
return|return
operator|(
name|tree
operator|==
literal|null
operator|||
operator|!
name|tree
operator|.
name|exists
argument_list|()
operator|)
condition|?
literal|null
else|:
operator|new
name|NodeDelegate
argument_list|(
name|this
argument_list|,
name|tree
argument_list|)
return|;
block|}
comment|/**      * {@code PropertyDelegate} at the given path      * @param path Oak path      * @return  The {@code PropertyDelegate} at {@code path} or {@code null} if      * none exists or not accessible.      */
annotation|@
name|CheckForNull
specifier|public
name|PropertyDelegate
name|getProperty
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|Tree
name|parent
init|=
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|PropertyDelegate
operator|.
name|create
argument_list|(
name|this
argument_list|,
name|parent
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|hasPendingChanges
parameter_list|()
block|{
return|return
name|root
operator|.
name|hasPendingChanges
argument_list|()
return|;
block|}
specifier|public
name|void
name|save
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
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
name|newRepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|permissionProvider
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|refresh
parameter_list|(
name|boolean
name|keepChanges
parameter_list|)
block|{
if|if
condition|(
name|keepChanges
operator|&&
name|hasPendingChanges
argument_list|()
condition|)
block|{
name|root
operator|.
name|rebase
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
name|permissionProvider
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
comment|//----------------------------------------------------------< Workspace>---
annotation|@
name|Nonnull
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
comment|/**      * Copy a node      * @param srcPath  oak path to the source node to copy      * @param destPath  oak path to the destination      * @throws RepositoryException      */
specifier|public
name|void
name|copy
parameter_list|(
name|String
name|srcPath
parameter_list|,
name|String
name|destPath
parameter_list|,
name|AccessManager
name|accessManager
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// check destination
name|Tree
name|dest
init|=
name|root
operator|.
name|getTree
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ItemExistsException
argument_list|(
name|destPath
argument_list|)
throw|;
block|}
comment|// check parent of destination
name|String
name|destParentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
name|Tree
name|destParent
init|=
name|root
operator|.
name|getTree
argument_list|(
name|destParentPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|destParent
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|destPath
argument_list|)
argument_list|)
throw|;
block|}
comment|// check source exists
name|Tree
name|src
init|=
name|root
operator|.
name|getTree
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|src
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|srcPath
argument_list|)
throw|;
block|}
name|accessManager
operator|.
name|checkPermissions
argument_list|(
name|destPath
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|NODE_TYPE_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|Root
name|currentRoot
init|=
name|contentSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|currentRoot
operator|.
name|copy
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Cannot copy node at "
operator|+
name|srcPath
operator|+
literal|" to "
operator|+
name|destPath
argument_list|)
throw|;
block|}
name|currentRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
name|newRepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Move a node      * @param srcPath  oak path to the source node to copy      * @param destPath  oak path to the destination      * @param transientOp  whether or not to perform the move in transient space      * @throws RepositoryException      */
specifier|public
name|void
name|move
parameter_list|(
name|String
name|srcPath
parameter_list|,
name|String
name|destPath
parameter_list|,
name|boolean
name|transientOp
parameter_list|,
name|AccessManager
name|accessManager
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Root
name|moveRoot
init|=
name|transientOp
condition|?
name|root
else|:
name|contentSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
comment|// check destination
name|Tree
name|dest
init|=
name|moveRoot
operator|.
name|getTree
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ItemExistsException
argument_list|(
name|destPath
argument_list|)
throw|;
block|}
comment|// check parent of destination
name|String
name|destParentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
name|Tree
name|destParent
init|=
name|moveRoot
operator|.
name|getTree
argument_list|(
name|destParentPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|destParent
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|destPath
argument_list|)
argument_list|)
throw|;
block|}
comment|// check source exists
name|Tree
name|src
init|=
name|moveRoot
operator|.
name|getTree
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|src
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|srcPath
argument_list|)
throw|;
block|}
name|accessManager
operator|.
name|checkPermissions
argument_list|(
name|destPath
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|NODE_TYPE_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|moveRoot
operator|.
name|move
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Cannot move node at "
operator|+
name|srcPath
operator|+
literal|" to "
operator|+
name|destPath
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|transientOp
condition|)
block|{
name|moveRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
name|newRepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nonnull
specifier|public
name|QueryEngine
name|getQueryEngine
parameter_list|()
block|{
return|return
name|root
operator|.
name|getQueryEngine
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|PermissionProvider
name|getPermissionProvider
parameter_list|()
block|{
return|return
name|permissionProvider
return|;
block|}
comment|/**      * The current {@code Root} instance this session delegate instance operates on.      * To ensure the returned root reflects the correct repository revision access      * should only be done from within a {@link SessionOperation} closure through      * {@link #perform(SessionOperation)}.      *      * @return  current root      */
annotation|@
name|Nonnull
specifier|public
name|Root
name|getRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
comment|//-----------------------------------------------------------< internal>---
comment|/**      * Wraps the given {@link CommitFailedException} instance using the      * appropriate {@link RepositoryException} subclass based on the      * {@link CommitFailedException#getType() type} of the given exception.      *      * @param exception typed commit failure exception      * @return matching repository exception      */
specifier|private
specifier|static
name|RepositoryException
name|newRepositoryException
parameter_list|(
name|CommitFailedException
name|exception
parameter_list|)
block|{
return|return
name|exception
operator|.
name|asRepositoryException
argument_list|()
return|;
block|}
block|}
end_class

end_unit

