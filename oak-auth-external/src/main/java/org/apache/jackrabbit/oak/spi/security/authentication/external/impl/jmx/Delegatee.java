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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedActionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|Iterator
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
name|annotation
operator|.
name|Nullable
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
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicates
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterators
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
name|JackrabbitRepository
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
name|json
operator|.
name|JsonUtil
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
name|SystemSubject
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
name|external
operator|.
name|ExternalIdentity
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
name|external
operator|.
name|ExternalIdentityException
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
name|external
operator|.
name|ExternalIdentityProvider
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
name|external
operator|.
name|ExternalIdentityRef
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
name|external
operator|.
name|ExternalUser
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
name|external
operator|.
name|SyncContext
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
name|external
operator|.
name|SyncException
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
name|external
operator|.
name|SyncHandler
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
name|external
operator|.
name|SyncResult
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
name|external
operator|.
name|SyncedIdentity
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
name|external
operator|.
name|basic
operator|.
name|DefaultSyncResultImpl
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
name|external
operator|.
name|basic
operator|.
name|DefaultSyncedIdentity
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

begin_class
specifier|final
class|class
name|Delegatee
block|{
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
name|Delegatee
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ERROR_CREATE_DELEGATEE
init|=
literal|"Unable to create delegatee"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ERROR_SYNC_USER
init|=
literal|"Error while syncing user {}"
decl_stmt|;
specifier|private
specifier|final
name|SyncHandler
name|handler
decl_stmt|;
specifier|private
specifier|final
name|ExternalIdentityProvider
name|idp
decl_stmt|;
specifier|private
specifier|final
name|UserManager
name|userMgr
decl_stmt|;
specifier|private
specifier|final
name|Session
name|systemSession
decl_stmt|;
specifier|private
name|SyncContext
name|context
decl_stmt|;
specifier|private
name|Delegatee
parameter_list|(
annotation|@
name|Nonnull
name|SyncHandler
name|handler
parameter_list|,
annotation|@
name|Nonnull
name|ExternalIdentityProvider
name|idp
parameter_list|,
annotation|@
name|Nonnull
name|JackrabbitSession
name|systemSession
parameter_list|)
throws|throws
name|SyncException
throws|,
name|RepositoryException
block|{
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
name|this
operator|.
name|idp
operator|=
name|idp
expr_stmt|;
name|this
operator|.
name|systemSession
operator|=
name|systemSession
expr_stmt|;
name|this
operator|.
name|userMgr
operator|=
name|systemSession
operator|.
name|getUserManager
argument_list|()
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|handler
operator|.
name|createContext
argument_list|(
name|idp
argument_list|,
name|userMgr
argument_list|,
name|systemSession
operator|.
name|getValueFactory
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created delegatee for SyncMBean with session: {} {}"
argument_list|,
name|systemSession
argument_list|,
name|systemSession
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|static
name|Delegatee
name|createInstance
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Repository
name|repository
parameter_list|,
annotation|@
name|Nonnull
name|SyncHandler
name|handler
parameter_list|,
annotation|@
name|Nonnull
name|ExternalIdentityProvider
name|idp
parameter_list|)
block|{
name|Session
name|systemSession
decl_stmt|;
try|try
block|{
name|systemSession
operator|=
name|Subject
operator|.
name|doAs
argument_list|(
name|SystemSubject
operator|.
name|INSTANCE
argument_list|,
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Session
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Session
name|run
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|repository
operator|instanceof
name|JackrabbitRepository
condition|)
block|{
comment|// this is to bypass GuestCredentials injection in the "AbstractSlingRepository2"
return|return
operator|(
operator|(
name|JackrabbitRepository
operator|)
name|repository
operator|)
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PrivilegedActionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SyncRuntimeException
argument_list|(
name|ERROR_CREATE_DELEGATEE
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|systemSession
operator|instanceof
name|JackrabbitSession
operator|)
condition|)
block|{
name|systemSession
operator|.
name|logout
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SyncRuntimeException
argument_list|(
literal|"Unable to create SyncContext: JackrabbitSession required."
argument_list|)
throw|;
block|}
try|try
block|{
return|return
operator|new
name|Delegatee
argument_list|(
name|handler
argument_list|,
name|idp
argument_list|,
operator|(
name|JackrabbitSession
operator|)
name|systemSession
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|systemSession
operator|.
name|logout
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SyncRuntimeException
argument_list|(
name|ERROR_CREATE_DELEGATEE
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SyncException
name|e
parameter_list|)
block|{
name|systemSession
operator|.
name|logout
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SyncRuntimeException
argument_list|(
name|ERROR_CREATE_DELEGATEE
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
name|context
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|systemSession
operator|.
name|isLive
argument_list|()
condition|)
block|{
name|systemSession
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @see SynchronizationMBean#syncUsers(String[], boolean)      */
annotation|@
name|Nonnull
name|String
index|[]
name|syncUsers
parameter_list|(
annotation|@
name|Nonnull
name|String
index|[]
name|userIds
parameter_list|,
name|boolean
name|purge
parameter_list|)
block|{
name|context
operator|.
name|setKeepMissing
argument_list|(
operator|!
name|purge
argument_list|)
operator|.
name|setForceGroupSync
argument_list|(
literal|true
argument_list|)
operator|.
name|setForceUserSync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list
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
name|String
name|userId
range|:
name|userIds
control|)
block|{
try|try
block|{
name|append
argument_list|(
name|list
argument_list|,
name|syncUser
argument_list|(
name|userId
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SyncException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|ERROR_SYNC_USER
argument_list|,
name|userId
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|list
argument_list|,
operator|new
name|DefaultSyncedIdentity
argument_list|(
name|userId
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**      * @see SynchronizationMBean#syncAllUsers(boolean)      */
annotation|@
name|Nonnull
name|String
index|[]
name|syncAllUsers
parameter_list|(
name|boolean
name|purge
parameter_list|)
block|{
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|context
operator|.
name|setKeepMissing
argument_list|(
operator|!
name|purge
argument_list|)
operator|.
name|setForceGroupSync
argument_list|(
literal|true
argument_list|)
operator|.
name|setForceUserSync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|SyncedIdentity
argument_list|>
name|iter
init|=
name|handler
operator|.
name|listIdentities
argument_list|(
name|userMgr
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SyncedIdentity
name|id
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|isMyIDP
argument_list|(
name|id
argument_list|)
condition|)
block|{
try|try
block|{
name|append
argument_list|(
name|list
argument_list|,
name|syncUser
argument_list|(
name|id
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SyncException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|ERROR_SYNC_USER
argument_list|,
name|id
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|list
argument_list|,
name|id
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
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
name|IllegalStateException
argument_list|(
literal|"Error retrieving users for syncing"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see SynchronizationMBean#syncExternalUsers(String[])      */
annotation|@
name|Nonnull
name|String
index|[]
name|syncExternalUsers
parameter_list|(
annotation|@
name|Nonnull
name|String
index|[]
name|externalIds
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|context
operator|.
name|setForceGroupSync
argument_list|(
literal|true
argument_list|)
operator|.
name|setForceUserSync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|externalId
range|:
name|externalIds
control|)
block|{
name|ExternalIdentityRef
name|ref
init|=
name|ExternalIdentityRef
operator|.
name|fromString
argument_list|(
name|externalId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|idp
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|ref
operator|.
name|getProviderName
argument_list|()
argument_list|)
condition|)
block|{
name|append
argument_list|(
name|list
argument_list|,
operator|new
name|DefaultSyncResultImpl
argument_list|(
operator|new
name|DefaultSyncedIdentity
argument_list|(
name|ref
operator|.
name|getId
argument_list|()
argument_list|,
name|ref
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
name|SyncResult
operator|.
name|Status
operator|.
name|FOREIGN
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|ExternalIdentity
name|id
init|=
name|idp
operator|.
name|getIdentity
argument_list|(
name|ref
argument_list|)
decl_stmt|;
name|SyncResult
name|r
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|r
operator|=
name|syncUser
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|r
operator|=
operator|new
name|DefaultSyncResultImpl
argument_list|(
operator|new
name|DefaultSyncedIdentity
argument_list|(
literal|""
argument_list|,
name|ref
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
name|SyncResult
operator|.
name|Status
operator|.
name|NO_SUCH_IDENTITY
argument_list|)
expr_stmt|;
block|}
name|append
argument_list|(
name|list
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExternalIdentityException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"error while fetching the external identity {}"
argument_list|,
name|externalId
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|list
argument_list|,
name|ref
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SyncException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|ERROR_SYNC_USER
argument_list|,
name|ref
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|list
argument_list|,
name|ref
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**      * @see SynchronizationMBean#syncAllExternalUsers()      */
annotation|@
name|Nonnull
name|String
index|[]
name|syncAllExternalUsers
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|context
operator|.
name|setForceGroupSync
argument_list|(
literal|true
argument_list|)
operator|.
name|setForceUserSync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|Iterator
argument_list|<
name|ExternalUser
argument_list|>
name|iter
init|=
name|idp
operator|.
name|listUsers
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ExternalUser
name|user
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|SyncResult
name|r
init|=
name|syncUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|getIdentity
argument_list|()
operator|==
literal|null
condition|)
block|{
name|r
operator|=
operator|new
name|DefaultSyncResultImpl
argument_list|(
operator|new
name|DefaultSyncedIdentity
argument_list|(
name|user
operator|.
name|getId
argument_list|()
argument_list|,
name|user
operator|.
name|getExternalId
argument_list|()
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
name|SyncResult
operator|.
name|Status
operator|.
name|NO_SUCH_IDENTITY
argument_list|)
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"sync failed. {}"
argument_list|,
name|r
operator|.
name|getIdentity
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"synced {}"
argument_list|,
name|r
operator|.
name|getIdentity
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|append
argument_list|(
name|list
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SyncException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|ERROR_SYNC_USER
argument_list|,
name|user
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|list
argument_list|,
name|user
operator|.
name|getExternalId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExternalIdentityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SyncRuntimeException
argument_list|(
literal|"Unable to retrieve external users"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see SynchronizationMBean#listOrphanedUsers()      */
annotation|@
name|Nonnull
name|String
index|[]
name|listOrphanedUsers
parameter_list|()
block|{
return|return
name|Iterators
operator|.
name|toArray
argument_list|(
name|internalListOrphanedIdentities
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**      * @see SynchronizationMBean#purgeOrphanedUsers()      */
annotation|@
name|Nonnull
name|String
index|[]
name|purgeOrphanedUsers
parameter_list|()
block|{
name|context
operator|.
name|setKeepMissing
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|orphanedIdentities
init|=
name|internalListOrphanedIdentities
argument_list|()
decl_stmt|;
while|while
condition|(
name|orphanedIdentities
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|userId
init|=
name|orphanedIdentities
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|append
argument_list|(
name|list
argument_list|,
name|syncUser
argument_list|(
name|userId
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SyncException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|ERROR_SYNC_USER
argument_list|,
name|userId
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|list
argument_list|,
operator|new
name|DefaultSyncedIdentity
argument_list|(
name|userId
argument_list|,
operator|new
name|ExternalIdentityRef
argument_list|(
name|userId
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|boolean
name|isMyIDP
parameter_list|(
annotation|@
name|Nonnull
name|SyncedIdentity
name|id
parameter_list|)
block|{
name|ExternalIdentityRef
name|ref
init|=
name|id
operator|.
name|getExternalIdRef
argument_list|()
decl_stmt|;
name|String
name|providerName
init|=
operator|(
name|ref
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|ref
operator|.
name|getProviderName
argument_list|()
decl_stmt|;
return|return
name|providerName
operator|!=
literal|null
operator|&&
operator|(
name|providerName
operator|.
name|isEmpty
argument_list|()
operator|||
name|providerName
operator|.
name|equals
argument_list|(
name|idp
operator|.
name|getName
argument_list|()
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|SyncResult
name|syncUser
parameter_list|(
annotation|@
name|Nonnull
name|ExternalIdentity
name|id
parameter_list|)
throws|throws
name|SyncException
block|{
try|try
block|{
name|SyncResult
name|r
init|=
name|context
operator|.
name|sync
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|systemSession
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
name|r
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
name|SyncException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nonnull
specifier|private
name|SyncResult
name|syncUser
parameter_list|(
annotation|@
name|Nonnull
name|String
name|userId
parameter_list|)
throws|throws
name|SyncException
block|{
try|try
block|{
name|SyncResult
name|r
init|=
name|context
operator|.
name|sync
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|systemSession
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
name|r
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
name|SyncException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nonnull
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|internalListOrphanedIdentities
parameter_list|()
block|{
try|try
block|{
name|Iterator
argument_list|<
name|SyncedIdentity
argument_list|>
name|iter
init|=
name|handler
operator|.
name|listIdentities
argument_list|(
name|userMgr
argument_list|)
decl_stmt|;
return|return
name|Iterators
operator|.
name|filter
argument_list|(
name|Iterators
operator|.
name|transform
argument_list|(
name|iter
argument_list|,
operator|new
name|Function
argument_list|<
name|SyncedIdentity
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
annotation|@
name|Nullable
name|SyncedIdentity
name|syncedIdentity
parameter_list|)
block|{
if|if
condition|(
name|syncedIdentity
operator|!=
literal|null
operator|&&
name|isMyIDP
argument_list|(
name|syncedIdentity
argument_list|)
condition|)
block|{
name|ExternalIdentityRef
name|ref
init|=
name|syncedIdentity
operator|.
name|getExternalIdRef
argument_list|()
decl_stmt|;
try|try
block|{
name|ExternalIdentity
name|extId
init|=
operator|(
name|ref
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|idp
operator|.
name|getIdentity
argument_list|(
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
name|extId
operator|==
literal|null
condition|)
block|{
return|return
name|syncedIdentity
operator|.
name|getId
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|ExternalIdentityException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error while fetching external identity {}"
argument_list|,
name|syncedIdentity
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
argument_list|,
name|Predicates
operator|.
name|notNull
argument_list|()
argument_list|)
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
name|error
argument_list|(
literal|"Error while listing orphaned users"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|Iterators
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
name|void
name|append
parameter_list|(
annotation|@
name|Nonnull
name|List
argument_list|<
name|String
argument_list|>
name|list
parameter_list|,
annotation|@
name|Nonnull
name|SyncResult
name|r
parameter_list|)
block|{
name|SyncedIdentity
name|syncedIdentity
init|=
name|r
operator|.
name|getIdentity
argument_list|()
decl_stmt|;
name|String
name|uid
init|=
name|JsonUtil
operator|.
name|getJsonString
argument_list|(
operator|(
name|syncedIdentity
operator|==
literal|null
condition|?
literal|null
else|:
name|syncedIdentity
operator|.
name|getId
argument_list|()
operator|)
argument_list|)
decl_stmt|;
name|ExternalIdentityRef
name|externalIdentityRef
init|=
operator|(
name|syncedIdentity
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|syncedIdentity
operator|.
name|getExternalIdRef
argument_list|()
decl_stmt|;
name|String
name|eid
init|=
operator|(
name|externalIdentityRef
operator|==
literal|null
operator|)
condition|?
literal|"\"\""
else|:
name|JsonUtil
operator|.
name|getJsonString
argument_list|(
name|externalIdentityRef
operator|.
name|getString
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|jsonStr
init|=
name|String
operator|.
name|format
argument_list|(
literal|"{op:\"%s\",uid:%s,eid:%s}"
argument_list|,
name|getOperationFromStatus
argument_list|(
name|r
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|,
name|uid
argument_list|,
name|eid
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|jsonStr
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|append
parameter_list|(
annotation|@
name|Nonnull
name|List
argument_list|<
name|String
argument_list|>
name|list
parameter_list|,
annotation|@
name|Nonnull
name|ExternalIdentityRef
name|idRef
parameter_list|,
annotation|@
name|Nonnull
name|Exception
name|e
parameter_list|)
block|{
name|String
name|uid
init|=
name|JsonUtil
operator|.
name|getJsonString
argument_list|(
name|idRef
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|eid
init|=
name|JsonUtil
operator|.
name|getJsonString
argument_list|(
name|idRef
operator|.
name|getString
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
name|JsonUtil
operator|.
name|getJsonString
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|jsonStr
init|=
name|String
operator|.
name|format
argument_list|(
literal|"{op:\"ERR\",uid:%s,eid:%s,msg:%s}"
argument_list|,
name|uid
argument_list|,
name|eid
argument_list|,
name|msg
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|jsonStr
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|append
parameter_list|(
annotation|@
name|Nonnull
name|List
argument_list|<
name|String
argument_list|>
name|list
parameter_list|,
annotation|@
name|Nonnull
name|SyncedIdentity
name|id
parameter_list|,
annotation|@
name|Nonnull
name|Exception
name|e
parameter_list|)
block|{
name|String
name|uid
init|=
name|JsonUtil
operator|.
name|getJsonString
argument_list|(
name|id
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|ExternalIdentityRef
name|ref
init|=
name|id
operator|.
name|getExternalIdRef
argument_list|()
decl_stmt|;
name|String
name|eid
init|=
operator|(
name|ref
operator|==
literal|null
operator|)
condition|?
literal|"\"\""
else|:
name|JsonUtil
operator|.
name|getJsonString
argument_list|(
name|ref
operator|.
name|getString
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
name|JsonUtil
operator|.
name|getJsonString
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"{op:\"ERR\",uid:%s,eid:%s,msg:%s}"
argument_list|,
name|uid
argument_list|,
name|eid
argument_list|,
name|msg
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|getOperationFromStatus
parameter_list|(
name|SyncResult
operator|.
name|Status
name|syncStatus
parameter_list|)
block|{
name|String
name|op
decl_stmt|;
switch|switch
condition|(
name|syncStatus
condition|)
block|{
case|case
name|NOP
case|:
name|op
operator|=
literal|"nop"
expr_stmt|;
break|break;
case|case
name|ADD
case|:
name|op
operator|=
literal|"add"
expr_stmt|;
break|break;
case|case
name|UPDATE
case|:
name|op
operator|=
literal|"upd"
expr_stmt|;
break|break;
case|case
name|DELETE
case|:
name|op
operator|=
literal|"del"
expr_stmt|;
break|break;
case|case
name|NO_SUCH_AUTHORIZABLE
case|:
name|op
operator|=
literal|"nsa"
expr_stmt|;
break|break;
case|case
name|NO_SUCH_IDENTITY
case|:
name|op
operator|=
literal|"nsi"
expr_stmt|;
break|break;
case|case
name|MISSING
case|:
name|op
operator|=
literal|"mis"
expr_stmt|;
break|break;
case|case
name|FOREIGN
case|:
name|op
operator|=
literal|"for"
expr_stmt|;
break|break;
default|default:
name|op
operator|=
literal|""
expr_stmt|;
block|}
return|return
name|op
return|;
block|}
block|}
end_class

end_unit

