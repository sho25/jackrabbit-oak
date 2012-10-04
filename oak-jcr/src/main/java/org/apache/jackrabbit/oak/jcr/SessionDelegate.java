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
name|util
operator|.
name|concurrent
operator|.
name|ScheduledExecutorService
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
name|ChangeExtractor
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
name|SessionQueryEngine
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
name|observation
operator|.
name|ObservationManagerImpl
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
name|principal
operator|.
name|PrincipalManagerImpl
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
name|namepath
operator|.
name|NamePathMapperImpl
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
name|identifier
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
name|principal
operator|.
name|OpenPrincipalProvider
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
name|oak
operator|.
name|value
operator|.
name|ValueFactoryImpl
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
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|Repository
name|repository
decl_stmt|;
specifier|private
specifier|final
name|ScheduledExecutorService
name|executor
decl_stmt|;
specifier|private
specifier|final
name|ContentSession
name|contentSession
decl_stmt|;
specifier|private
specifier|final
name|ValueFactoryImpl
name|valueFactory
decl_stmt|;
specifier|private
specifier|final
name|Workspace
name|workspace
decl_stmt|;
specifier|private
specifier|final
name|Session
name|session
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|autoRefresh
decl_stmt|;
specifier|private
specifier|final
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
specifier|final
name|IdentifierManager
name|idManager
decl_stmt|;
specifier|private
name|ObservationManagerImpl
name|observationManager
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
name|SessionDelegate
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|ScheduledExecutorService
name|executor
parameter_list|,
name|ContentSession
name|contentSession
parameter_list|,
name|SecurityProvider
name|securityProvider
parameter_list|,
name|boolean
name|autoRefresh
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|repository
operator|=
name|checkNotNull
argument_list|(
name|repository
argument_list|)
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
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
name|securityProvider
operator|=
name|securityProvider
expr_stmt|;
name|this
operator|.
name|autoRefresh
operator|=
name|autoRefresh
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
name|workspace
operator|=
operator|new
name|WorkspaceImpl
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|session
operator|=
operator|new
name|SessionImpl
argument_list|(
name|this
argument_list|)
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
name|namePathMapper
operator|=
operator|new
name|NamePathMapperImpl
argument_list|(
operator|new
name|SessionNameMapper
argument_list|(
name|this
argument_list|)
argument_list|,
name|idManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|valueFactory
operator|=
operator|new
name|ValueFactoryImpl
argument_list|(
name|contentSession
operator|.
name|getCoreValueFactory
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
comment|/**      * Performs the passed {@code SessionOperation} in a safe execution context. This      * context ensures that the session is refreshed if necessary and that refreshing      * occurs before the session operation is performed and the refreshing is done only      * once.      *      * @param sessionOperation  the {@code SessionOperation} to perform      * @param<T>  return type of {@code sessionOperation}      * @return  the result of {@code sessionOperation.perform()}      * @throws RepositoryException      */
specifier|public
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
try|try
block|{
name|sessionOpCount
operator|++
expr_stmt|;
if|if
condition|(
name|needsRefresh
argument_list|()
condition|)
block|{
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
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
block|}
block|}
specifier|private
name|boolean
name|needsRefresh
parameter_list|()
block|{
comment|// Refresh is always needed if this is an auto refresh session. Otherwise
comment|// refresh in only needed for non re-entrant session operations and only if
comment|// observation events have actually been delivered
return|return
name|autoRefresh
operator|||
operator|(
name|sessionOpCount
operator|<=
literal|1
operator|&&
name|observationManager
operator|!=
literal|null
operator|&&
name|observationManager
operator|.
name|hasEvents
argument_list|()
operator|)
return|;
block|}
specifier|public
name|boolean
name|isAlive
parameter_list|()
block|{
return|return
name|isAlive
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Session
name|getSession
parameter_list|()
block|{
return|return
name|session
return|;
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
annotation|@
name|Nonnull
specifier|public
name|Repository
name|getRepository
parameter_list|()
block|{
return|return
name|repository
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
if|if
condition|(
name|observationManager
operator|!=
literal|null
condition|)
block|{
name|observationManager
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
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
annotation|@
name|CheckForNull
specifier|public
name|Root
name|getRoot
parameter_list|()
block|{
return|return
name|root
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
name|getLocation
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
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
annotation|@
name|CheckForNull
comment|/**      * {@code PropertyDelegate} at the given path      * @param path Oak path      * @return  The {@code PropertyDelegate} at {@code path} or {@code null} if      * none exists or not accessible.      */
specifier|public
name|PropertyDelegate
name|getProperty
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|TreeLocation
name|location
init|=
name|root
operator|.
name|getLocation
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|location
operator|.
name|getProperty
argument_list|()
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|PropertyDelegate
argument_list|(
name|this
argument_list|,
name|location
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|ValueFactoryImpl
name|getValueFactory
parameter_list|()
block|{
return|return
name|valueFactory
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|NamePathMapper
name|getNamePathMapper
parameter_list|()
block|{
return|return
name|namePathMapper
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
name|e
operator|.
name|throwRepositoryException
argument_list|()
expr_stmt|;
block|}
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
block|}
comment|/**      * Returns the Oak name for the given JCR name, or throws a      * {@link RepositoryException} if the name is invalid or can      * otherwise not be mapped.      *      * @param jcrName JCR name      * @return Oak name      * @throws RepositoryException if the name is invalid      */
annotation|@
name|Nonnull
specifier|public
name|String
name|getOakNameOrThrow
parameter_list|(
name|String
name|jcrName
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|oakName
init|=
name|getNamePathMapper
argument_list|()
operator|.
name|getOakName
argument_list|(
name|jcrName
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakName
operator|!=
literal|null
condition|)
block|{
return|return
name|oakName
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Invalid name: "
operator|+
name|jcrName
argument_list|)
throw|;
block|}
block|}
comment|/**      * Shortcut for {@code SessionDelegate.getNamePathMapper().getOakPath(jcrPath)}.      *      * @param jcrPath JCR path      * @return Oak path, or {@code null}      */
annotation|@
name|CheckForNull
specifier|public
name|String
name|getOakPathOrNull
parameter_list|(
name|String
name|jcrPath
parameter_list|)
block|{
return|return
name|getNamePathMapper
argument_list|()
operator|.
name|getOakPath
argument_list|(
name|jcrPath
argument_list|)
return|;
block|}
comment|/**      * Shortcut for {@code SessionDelegate.getOakPathKeepIndex(jcrPath)}.      *      * @param jcrPath JCR path      * @return Oak path, or {@code null}, with indexes left intact      * @throws PathNotFoundException       */
annotation|@
name|Nonnull
specifier|public
name|String
name|getOakPathKeepIndexOrThrowNotFound
parameter_list|(
name|String
name|jcrPath
parameter_list|)
throws|throws
name|PathNotFoundException
block|{
name|String
name|oakPath
init|=
name|getNamePathMapper
argument_list|()
operator|.
name|getOakPathKeepIndex
argument_list|(
name|jcrPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPath
operator|!=
literal|null
condition|)
block|{
return|return
name|oakPath
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|jcrPath
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the Oak path for the given JCR path, or throws a      * {@link PathNotFoundException} if the path can not be mapped.      *      * @param jcrPath JCR path      * @return Oak path      * @throws PathNotFoundException if the path can not be mapped      */
annotation|@
name|Nonnull
specifier|public
name|String
name|getOakPathOrThrowNotFound
parameter_list|(
name|String
name|jcrPath
parameter_list|)
throws|throws
name|PathNotFoundException
block|{
name|String
name|oakPath
init|=
name|getOakPathOrNull
argument_list|(
name|jcrPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPath
operator|!=
literal|null
condition|)
block|{
return|return
name|oakPath
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|jcrPath
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the Oak path for the given JCR path, or throws a      * {@link RepositoryException} if the path can not be mapped.      *      * @param jcrPath JCR path      * @return Oak path      * @throws RepositoryException if the path can not be mapped      */
annotation|@
name|Nonnull
specifier|public
name|String
name|getOakPathOrThrow
parameter_list|(
name|String
name|jcrPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|getOakPathOrNull
argument_list|(
name|jcrPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPath
operator|!=
literal|null
condition|)
block|{
return|return
name|oakPath
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Invalid name or path: "
operator|+
name|jcrPath
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nonnull
specifier|public
name|ChangeExtractor
name|getChangeExtractor
parameter_list|()
block|{
return|return
name|root
operator|.
name|getChangeExtractor
argument_list|()
return|;
block|}
comment|//----------------------------------------------------------< Workspace>---
annotation|@
name|Nonnull
specifier|public
name|Workspace
name|getWorkspace
parameter_list|()
block|{
return|return
name|workspace
return|;
block|}
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
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// check destination
name|Tree
name|dest
init|=
name|getTree
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|!=
literal|null
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
name|getTree
argument_list|(
name|destParentPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|destParent
operator|==
literal|null
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
name|getTree
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|src
operator|==
literal|null
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
name|currentRoot
operator|.
name|copy
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|currentRoot
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
name|e
operator|.
name|throwRepositoryException
argument_list|()
expr_stmt|;
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
operator|!=
literal|null
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
name|destParent
operator|==
literal|null
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
name|src
operator|==
literal|null
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
try|try
block|{
name|moveRoot
operator|.
name|move
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
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
block|}
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|throwRepositoryException
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Nonnull
specifier|public
name|LockManager
name|getLockManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|workspace
operator|.
name|getLockManager
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|SessionQueryEngine
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
name|QueryManager
name|getQueryManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|workspace
operator|.
name|getQueryManager
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|NodeTypeManager
name|getNodeTypeManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|workspace
operator|.
name|getNodeTypeManager
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|VersionManager
name|getVersionManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|workspace
operator|.
name|getVersionManager
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|ObservationManager
name|getObservationManager
parameter_list|()
block|{
if|if
condition|(
name|observationManager
operator|==
literal|null
condition|)
block|{
name|observationManager
operator|=
operator|new
name|ObservationManagerImpl
argument_list|(
name|this
argument_list|,
name|executor
argument_list|)
expr_stmt|;
block|}
return|return
name|observationManager
return|;
block|}
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
comment|//-----------------------------------------------------------< internal>---
comment|/**      * Get the {@code Tree} with the given path      * @param path  oak path      * @return  tree at the given path or {@code null} if no such tree exists or      * if the tree at {@code path} is not accessible.      */
annotation|@
name|CheckForNull
name|Tree
name|getTree
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
name|TreeLocation
name|getLocation
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|root
operator|.
name|getLocation
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
name|PrincipalManager
name|getPrincipalManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// TODO
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
operator|new
name|PrincipalManagerImpl
argument_list|(
operator|new
name|OpenPrincipalProvider
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
name|UserManager
name|getUserManager
parameter_list|()
throws|throws
name|UnsupportedRepositoryOperationException
block|{
if|if
condition|(
name|securityProvider
operator|!=
literal|null
condition|)
block|{
return|return
name|securityProvider
operator|.
name|getUserContext
argument_list|()
operator|.
name|getUserManager
argument_list|(
name|session
argument_list|,
name|contentSession
argument_list|,
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"User management not supported."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

