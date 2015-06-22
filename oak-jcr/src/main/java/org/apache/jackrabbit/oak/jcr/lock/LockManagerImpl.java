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
name|lock
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Boolean
operator|.
name|TRUE
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
name|jcr
operator|.
name|repository
operator|.
name|RepositoryImpl
operator|.
name|RELAXED_LOCKING
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
name|lock
operator|.
name|Lock
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
name|LockException
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
name|Authorizable
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
name|User
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
operator|.
name|Status
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
name|session
operator|.
name|SessionContext
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
name|NodeDelegate
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
name|session
operator|.
name|operation
operator|.
name|SessionOperation
import|;
end_import

begin_comment
comment|/**  * Simple lock manager implementation that just keeps track of a set of lock  * tokens and delegates all locking operations back to the {@link Session}  * and {@link Node} implementations.  */
end_comment

begin_class
specifier|public
class|class
name|LockManagerImpl
implements|implements
name|LockManager
block|{
specifier|private
specifier|final
name|SessionContext
name|sessionContext
decl_stmt|;
specifier|private
specifier|final
name|SessionDelegate
name|delegate
decl_stmt|;
specifier|public
name|LockManagerImpl
parameter_list|(
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
name|delegate
operator|=
name|sessionContext
operator|.
name|getSessionDelegate
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getLockTokens
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|delegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|String
index|[]
argument_list|>
argument_list|(
literal|"getLockTokens"
argument_list|)
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
index|[]
name|perform
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|tokens
init|=
name|sessionContext
operator|.
name|getOpenScopedLocks
argument_list|()
decl_stmt|;
return|return
name|tokens
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|tokens
operator|.
name|size
argument_list|()
index|]
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
name|void
name|addLockToken
parameter_list|(
specifier|final
name|String
name|lockToken
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|delegate
operator|.
name|performVoid
argument_list|(
operator|new
name|LockOperation
argument_list|<
name|Void
argument_list|>
argument_list|(
name|sessionContext
argument_list|,
name|lockToken
argument_list|,
literal|"addLockToken"
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|performVoid
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|node
parameter_list|)
throws|throws
name|LockException
block|{
if|if
condition|(
name|node
operator|.
name|holdsLock
argument_list|(
literal|false
argument_list|)
condition|)
block|{
comment|// TODO: check ownership?
name|String
name|token
init|=
name|node
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|sessionContext
operator|.
name|getOpenScopedLocks
argument_list|()
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Invalid lock token: "
operator|+
name|lockToken
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// TODO: better exception
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Invalid lock token: "
operator|+
name|lockToken
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeLockToken
parameter_list|(
specifier|final
name|String
name|lockToken
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|delegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|(
literal|"removeLockToken"
argument_list|)
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
block|{
comment|// TODO: name mapping?
return|return
name|sessionContext
operator|.
name|getOpenScopedLocks
argument_list|()
operator|.
name|remove
argument_list|(
name|lockToken
argument_list|)
return|;
block|}
block|}
block|)
block|)
block|{
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Lock token "
operator|+
name|lockToken
operator|+
literal|" is not held by this session"
argument_list|)
throw|;
block|}
end_class

begin_function
unit|}      @
name|Override
specifier|public
name|boolean
name|isLocked
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|delegate
operator|.
name|perform
argument_list|(
operator|new
name|LockOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|(
name|sessionContext
argument_list|,
name|absPath
argument_list|,
literal|"isLocked"
argument_list|)
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|protected
name|Boolean
name|perform
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|node
parameter_list|)
block|{
return|return
name|node
operator|.
name|isLocked
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|boolean
name|holdsLock
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|delegate
operator|.
name|perform
argument_list|(
operator|new
name|LockOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|(
name|sessionContext
argument_list|,
name|absPath
argument_list|,
literal|"holdsLock"
argument_list|)
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|protected
name|Boolean
name|perform
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|node
parameter_list|)
block|{
return|return
name|node
operator|.
name|holdsLock
argument_list|(
literal|false
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Lock
name|getLock
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|NodeDelegate
name|lock
init|=
name|delegate
operator|.
name|perform
argument_list|(
operator|new
name|LockOperation
argument_list|<
name|NodeDelegate
argument_list|>
argument_list|(
name|sessionContext
argument_list|,
name|absPath
argument_list|,
literal|"getLock"
argument_list|)
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|protected
name|NodeDelegate
name|perform
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|node
parameter_list|)
throws|throws
name|LockException
block|{
name|NodeDelegate
name|lock
init|=
name|node
operator|.
name|getLock
argument_list|()
decl_stmt|;
if|if
condition|(
name|lock
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Node "
operator|+
name|absPath
operator|+
literal|" is not locked"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|lock
return|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
return|return
operator|new
name|LockImpl
argument_list|(
name|sessionContext
argument_list|,
name|lock
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Lock
name|lock
parameter_list|(
name|String
name|absPath
parameter_list|,
specifier|final
name|boolean
name|isDeep
parameter_list|,
specifier|final
name|boolean
name|isSessionScoped
parameter_list|,
name|long
name|timeoutHint
parameter_list|,
name|String
name|ownerInfo
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|LockImpl
argument_list|(
name|sessionContext
argument_list|,
name|delegate
operator|.
name|perform
argument_list|(
operator|new
name|LockOperation
argument_list|<
name|NodeDelegate
argument_list|>
argument_list|(
name|sessionContext
argument_list|,
name|absPath
argument_list|,
literal|"lock"
argument_list|)
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|protected
name|NodeDelegate
name|perform
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|node
operator|.
name|getStatus
argument_list|()
operator|!=
name|Status
operator|.
name|UNCHANGED
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|(
literal|"Unable to lock a node with pending changes"
argument_list|)
throw|;
block|}
name|node
operator|.
name|lock
argument_list|(
name|isDeep
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|node
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|isSessionScoped
condition|)
block|{
name|sessionContext
operator|.
name|getSessionScopedLocks
argument_list|()
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sessionContext
operator|.
name|getOpenScopedLocks
argument_list|()
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|unlock
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|delegate
operator|.
name|performVoid
argument_list|(
operator|new
name|LockOperation
argument_list|<
name|Void
argument_list|>
argument_list|(
name|sessionContext
argument_list|,
name|absPath
argument_list|,
literal|"unlock"
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|performVoid
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|path
init|=
name|node
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|canUnlock
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|node
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|sessionContext
operator|.
name|getSessionScopedLocks
argument_list|()
operator|.
name|remove
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|sessionContext
operator|.
name|getOpenScopedLocks
argument_list|()
operator|.
name|remove
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|session
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Not an owner of the lock "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**      * Verifies if the current<tt>sessionContext</tt> can unlock the specified<tt>node</tt>      *       * @param node the node state to check      *       * @return true if the current<tt>sessionContext</tt> can unlock the specified<tt>node</tt>      */
end_comment

begin_function
specifier|public
name|boolean
name|canUnlock
parameter_list|(
name|NodeDelegate
name|node
parameter_list|)
block|{
name|String
name|path
init|=
name|node
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|sessionContext
operator|.
name|getSessionScopedLocks
argument_list|()
operator|.
name|contains
argument_list|(
name|path
argument_list|)
operator|||
name|sessionContext
operator|.
name|getOpenScopedLocks
argument_list|()
operator|.
name|contains
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|sessionContext
operator|.
name|getAttributes
argument_list|()
operator|.
name|get
argument_list|(
name|RELAXED_LOCKING
argument_list|)
operator|==
name|TRUE
condition|)
block|{
name|String
name|user
init|=
name|sessionContext
operator|.
name|getSessionDelegate
argument_list|()
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getUserID
argument_list|()
decl_stmt|;
return|return
name|node
operator|.
name|isLockOwner
argument_list|(
name|user
argument_list|)
operator|||
name|isAdmin
argument_list|(
name|sessionContext
argument_list|,
name|user
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
end_function

begin_function
specifier|private
name|boolean
name|isAdmin
parameter_list|(
name|SessionContext
name|sessionContext
parameter_list|,
name|String
name|user
parameter_list|)
block|{
try|try
block|{
name|Authorizable
name|a
init|=
name|sessionContext
operator|.
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
operator|&&
operator|!
name|a
operator|.
name|isGroup
argument_list|()
condition|)
block|{
return|return
operator|(
operator|(
name|User
operator|)
name|a
operator|)
operator|.
name|isAdmin
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// ?
block|}
return|return
literal|false
return|;
block|}
end_function

unit|}
end_unit

