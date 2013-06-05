begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|core
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
name|getName
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
name|security
operator|.
name|PrivilegedAction
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
name|Collections
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
name|security
operator|.
name|auth
operator|.
name|Subject
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
name|Oak
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
name|Blob
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
name|BlobFactory
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
name|plugins
operator|.
name|index
operator|.
name|diffindex
operator|.
name|UUIDDiffIndexProviderWrapper
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
name|observation
operator|.
name|PostCommitHook
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
name|query
operator|.
name|QueryEngineImpl
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
name|commit
operator|.
name|CommitHook
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
name|commit
operator|.
name|CompositeEditorProvider
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
name|commit
operator|.
name|CompositeHook
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
name|commit
operator|.
name|EditorHook
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
name|commit
operator|.
name|EmptyHook
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
name|commit
operator|.
name|PostValidationHook
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
name|commit
operator|.
name|ValidatorProvider
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
name|observation
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
name|spi
operator|.
name|query
operator|.
name|CompositeQueryIndexProvider
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
name|query
operator|.
name|QueryIndexProvider
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
name|Context
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
name|OpenSecurityProvider
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
name|SecurityConfiguration
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
name|AccessControlConfiguration
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
name|state
operator|.
name|NodeBuilder
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
name|state
operator|.
name|NodeState
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
name|state
operator|.
name|NodeStateDiff
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
name|state
operator|.
name|NodeStore
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
name|state
operator|.
name|NodeStoreBranch
import|;
end_import

begin_class
specifier|public
class|class
name|RootImpl
implements|implements
name|Root
block|{
comment|/**      * Number of {@link #updated} calls for which changes are kept in memory.      */
specifier|private
specifier|static
specifier|final
name|int
name|PURGE_LIMIT
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.root.purgeLimit"
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
comment|/**      * The underlying store to which this root belongs      */
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|CommitHook
name|hook
decl_stmt|;
specifier|private
specifier|final
name|PostCommitHook
name|postHook
decl_stmt|;
specifier|private
specifier|final
name|String
name|workspaceName
decl_stmt|;
specifier|private
specifier|final
name|Subject
name|subject
decl_stmt|;
specifier|private
specifier|final
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
specifier|final
name|QueryIndexProvider
name|indexProvider
decl_stmt|;
comment|/**      * Current root {@code Tree}      */
specifier|private
specifier|final
name|TreeImpl
name|rootTree
decl_stmt|;
comment|/**      * Current branch this root operates on      */
specifier|private
name|NodeStoreBranch
name|branch
decl_stmt|;
comment|/**      * Secure view of the head of the branch underlying this root.      */
specifier|private
name|NodeState
name|secureHead
decl_stmt|;
comment|/** Sentinel for the next move operation to take place on the this root */
specifier|private
name|Move
name|lastMove
init|=
operator|new
name|Move
argument_list|()
decl_stmt|;
comment|/**      * Number of {@link #updated} occurred so since the last      * purge.      */
specifier|private
name|int
name|modCount
decl_stmt|;
specifier|private
name|PermissionProvider
name|permissionProvider
decl_stmt|;
comment|/**      * New instance bases on a given {@link NodeStore} and a workspace      *      * @param store            node store      * @param hook             the commit hook      * @param workspaceName    name of the workspace      * @param subject          the subject.      * @param securityProvider the security configuration.      * @param indexProvider    the query index provider.      */
specifier|public
name|RootImpl
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|CommitHook
name|hook
parameter_list|,
name|PostCommitHook
name|postHook
parameter_list|,
name|String
name|workspaceName
parameter_list|,
name|Subject
name|subject
parameter_list|,
name|SecurityProvider
name|securityProvider
parameter_list|,
name|QueryIndexProvider
name|indexProvider
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|hook
operator|=
name|checkNotNull
argument_list|(
name|hook
argument_list|)
expr_stmt|;
name|this
operator|.
name|postHook
operator|=
name|postHook
expr_stmt|;
name|this
operator|.
name|workspaceName
operator|=
name|checkNotNull
argument_list|(
name|workspaceName
argument_list|)
expr_stmt|;
name|this
operator|.
name|subject
operator|=
name|checkNotNull
argument_list|(
name|subject
argument_list|)
expr_stmt|;
name|this
operator|.
name|securityProvider
operator|=
name|checkNotNull
argument_list|(
name|securityProvider
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexProvider
operator|=
name|indexProvider
expr_stmt|;
name|branch
operator|=
name|this
operator|.
name|store
operator|.
name|branch
argument_list|()
expr_stmt|;
name|NodeState
name|root
init|=
name|branch
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|secureHead
operator|=
operator|new
name|SecureNodeState
argument_list|(
name|root
argument_list|,
name|getRootContext
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|rootTree
operator|=
operator|new
name|TreeImpl
argument_list|(
name|this
argument_list|,
name|secureHead
operator|.
name|builder
argument_list|()
argument_list|,
name|lastMove
argument_list|)
expr_stmt|;
block|}
comment|// TODO: review if these constructors really make sense and cannot be replaced.
specifier|public
name|RootImpl
parameter_list|(
name|NodeStore
name|store
parameter_list|)
block|{
name|this
argument_list|(
name|store
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RootImpl
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|CommitHook
name|hook
parameter_list|)
block|{
comment|// FIXME: define proper default or pass workspace name with the constructor
name|this
argument_list|(
name|store
argument_list|,
name|hook
argument_list|,
name|PostCommitHook
operator|.
name|EMPTY
argument_list|,
name|Oak
operator|.
name|DEFAULT_WORKSPACE_NAME
argument_list|,
name|SystemSubject
operator|.
name|INSTANCE
argument_list|,
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|,
operator|new
name|CompositeQueryIndexProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Called whenever a method on this instance or on any {@code Tree} instance      * obtained from this {@code Root} is called. This default implementation      * does nothing. Sub classes may override this method and throw an exception      * indicating that this {@code Root} instance is not live anymore (e.g. because      * the session has been logged out already).      */
specifier|protected
name|void
name|checkLive
parameter_list|()
block|{      }
comment|//---------------------------------------------------------------< Root>---
annotation|@
name|Override
specifier|public
name|ContentSession
name|getContentSession
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|move
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|)
block|{
name|checkLive
argument_list|()
expr_stmt|;
name|TreeImpl
name|destParent
init|=
name|rootTree
operator|.
name|getTree
argument_list|(
name|getParentPath
argument_list|(
name|destPath
argument_list|)
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
return|return
literal|false
return|;
block|}
name|purgePendingChanges
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
name|branch
operator|.
name|move
argument_list|(
name|sourcePath
argument_list|,
name|destPath
argument_list|)
decl_stmt|;
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|getTree
argument_list|(
name|getParentPath
argument_list|(
name|sourcePath
argument_list|)
argument_list|)
operator|.
name|updateChildOrder
argument_list|()
expr_stmt|;
name|getTree
argument_list|(
name|getParentPath
argument_list|(
name|destPath
argument_list|)
argument_list|)
operator|.
name|updateChildOrder
argument_list|()
expr_stmt|;
name|lastMove
operator|=
name|lastMove
operator|.
name|setMove
argument_list|(
name|sourcePath
argument_list|,
name|destParent
argument_list|,
name|getName
argument_list|(
name|destPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|copy
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|)
block|{
name|checkLive
argument_list|()
expr_stmt|;
name|purgePendingChanges
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
name|branch
operator|.
name|copy
argument_list|(
name|sourcePath
argument_list|,
name|destPath
argument_list|)
decl_stmt|;
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|getTree
argument_list|(
name|getParentPath
argument_list|(
name|destPath
argument_list|)
argument_list|)
operator|.
name|updateChildOrder
argument_list|()
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
annotation|@
name|Override
specifier|public
name|TreeImpl
name|getTree
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
name|checkLive
argument_list|()
expr_stmt|;
return|return
name|rootTree
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|rebase
parameter_list|()
block|{
name|checkLive
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|equals
argument_list|(
name|getBaseState
argument_list|()
argument_list|)
condition|)
block|{
name|purgePendingChanges
argument_list|()
expr_stmt|;
name|branch
operator|.
name|rebase
argument_list|()
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|permissionProvider
operator|!=
literal|null
condition|)
block|{
name|permissionProvider
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|refresh
parameter_list|()
block|{
name|checkLive
argument_list|()
expr_stmt|;
name|branch
operator|=
name|store
operator|.
name|branch
argument_list|()
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
name|modCount
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|permissionProvider
operator|!=
literal|null
condition|)
block|{
name|permissionProvider
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|checkLive
argument_list|()
expr_stmt|;
name|rebase
argument_list|()
expr_stmt|;
name|purgePendingChanges
argument_list|()
expr_stmt|;
name|CommitFailedException
name|exception
init|=
name|Subject
operator|.
name|doAs
argument_list|(
name|getCommitSubject
argument_list|()
argument_list|,
operator|new
name|PrivilegedAction
argument_list|<
name|CommitFailedException
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|CommitFailedException
name|run
parameter_list|()
block|{
try|try
block|{
name|NodeState
name|base
init|=
name|branch
operator|.
name|getBase
argument_list|()
decl_stmt|;
name|NodeState
name|newHead
init|=
name|branch
operator|.
name|merge
argument_list|(
name|getCommitHook
argument_list|()
argument_list|)
decl_stmt|;
name|postHook
operator|.
name|contentChanged
argument_list|(
name|base
argument_list|,
name|newHead
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
return|return
name|e
return|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
name|exception
throw|;
block|}
name|refresh
argument_list|()
expr_stmt|;
block|}
comment|/**      * Combine the globally defined commit hook(s) with the hooks and      * validators defined by the various security related configurations.      *      * @return A commit hook combining repository global commit hook(s) with      *         the pluggable hooks defined with the security modules.      */
specifier|private
name|CommitHook
name|getCommitHook
parameter_list|()
block|{
name|List
argument_list|<
name|CommitHook
argument_list|>
name|commitHooks
init|=
operator|new
name|ArrayList
argument_list|<
name|CommitHook
argument_list|>
argument_list|()
decl_stmt|;
name|commitHooks
operator|.
name|add
argument_list|(
name|hook
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|CommitHook
argument_list|>
name|postValidationHooks
init|=
operator|new
name|ArrayList
argument_list|<
name|CommitHook
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SecurityConfiguration
name|sc
range|:
name|securityProvider
operator|.
name|getConfigurations
argument_list|()
control|)
block|{
for|for
control|(
name|CommitHook
name|ch
range|:
name|sc
operator|.
name|getCommitHooks
argument_list|(
name|workspaceName
argument_list|)
control|)
block|{
if|if
condition|(
name|ch
operator|instanceof
name|PostValidationHook
condition|)
block|{
name|postValidationHooks
operator|.
name|add
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ch
operator|!=
name|EmptyHook
operator|.
name|INSTANCE
condition|)
block|{
name|commitHooks
operator|.
name|add
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|?
extends|extends
name|ValidatorProvider
argument_list|>
name|validators
init|=
name|sc
operator|.
name|getValidators
argument_list|(
name|workspaceName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|validators
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|commitHooks
operator|.
name|add
argument_list|(
operator|new
name|EditorHook
argument_list|(
name|CompositeEditorProvider
operator|.
name|compose
argument_list|(
name|validators
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|commitHooks
operator|.
name|addAll
argument_list|(
name|postValidationHooks
argument_list|)
expr_stmt|;
return|return
name|CompositeHook
operator|.
name|compose
argument_list|(
name|commitHooks
argument_list|)
return|;
block|}
comment|/**      * TODO: review again once the permission validation is completed.      * Build a read only subject for the {@link #commit()} call that makes the      * principals and the permission provider available to the commit hooks.      *      * @return a new read only subject.      */
specifier|private
name|Subject
name|getCommitSubject
parameter_list|()
block|{
return|return
operator|new
name|Subject
argument_list|(
literal|true
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|()
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|getPermissionProvider
argument_list|()
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|emptySet
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPendingChanges
parameter_list|()
block|{
name|checkLive
argument_list|()
expr_stmt|;
return|return
operator|!
name|getSecureBase
argument_list|()
operator|.
name|equals
argument_list|(
name|getSecureRootState
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|ChangeExtractor
name|getChangeExtractor
parameter_list|()
block|{
name|checkLive
argument_list|()
expr_stmt|;
return|return
operator|new
name|ChangeExtractor
argument_list|()
block|{
specifier|private
name|NodeState
name|baseLine
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|getChanges
parameter_list|(
name|NodeStateDiff
name|diff
parameter_list|)
block|{
name|NodeState
name|head
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|head
operator|.
name|compareAgainstBaseState
argument_list|(
name|baseLine
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|baseLine
operator|=
name|head
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|QueryEngine
name|getQueryEngine
parameter_list|()
block|{
name|checkLive
argument_list|()
expr_stmt|;
return|return
operator|new
name|QueryEngineImpl
argument_list|(
name|getIndexProvider
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|NodeState
name|getRootState
parameter_list|()
block|{
return|return
name|RootImpl
operator|.
name|this
operator|.
name|getRootState
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Tree
name|getRootTree
parameter_list|()
block|{
return|return
name|rootTree
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|BlobFactory
name|getBlobFactory
parameter_list|()
block|{
name|checkLive
argument_list|()
expr_stmt|;
return|return
operator|new
name|BlobFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Blob
name|createBlob
parameter_list|(
name|InputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
block|{
name|checkLive
argument_list|()
expr_stmt|;
return|return
name|store
operator|.
name|createBlob
argument_list|(
name|inputStream
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|QueryIndexProvider
name|getIndexProvider
parameter_list|()
block|{
if|if
condition|(
name|hasPendingChanges
argument_list|()
condition|)
block|{
return|return
operator|new
name|UUIDDiffIndexProviderWrapper
argument_list|(
name|indexProvider
argument_list|,
name|getBaseState
argument_list|()
argument_list|,
name|getRootState
argument_list|()
argument_list|)
return|;
block|}
return|return
name|indexProvider
return|;
block|}
comment|//-----------------------------------------------------------< internal>---
comment|/**      * Returns the node state from which the current branch was created.      *      * @return base node state      */
annotation|@
name|Nonnull
name|NodeState
name|getBaseState
parameter_list|()
block|{
return|return
name|branch
operator|.
name|getBase
argument_list|()
return|;
block|}
comment|/**      * Returns the secure view of the base state from which the current branch was creates.      *      * @return secure base node state      */
name|NodeState
name|getSecureBase
parameter_list|()
block|{
name|NodeState
name|root
init|=
name|branch
operator|.
name|getBase
argument_list|()
decl_stmt|;
return|return
operator|new
name|SecureNodeState
argument_list|(
name|root
argument_list|,
name|getRootContext
argument_list|(
name|root
argument_list|)
argument_list|)
return|;
block|}
comment|// TODO better way to determine purge limit. See OAK-175
name|void
name|updated
parameter_list|()
block|{
if|if
condition|(
operator|++
name|modCount
operator|>
name|PURGE_LIMIT
condition|)
block|{
name|modCount
operator|=
literal|0
expr_stmt|;
name|purgePendingChanges
argument_list|()
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
comment|/**      * Root node state of the tree including all transient changes at the time of      * this call.      *      * @return root node state      */
annotation|@
name|Nonnull
specifier|private
name|NodeState
name|getRootState
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|branch
operator|.
name|getHead
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
return|return
name|SecuredNodeRebaseDiff
operator|.
name|rebase
argument_list|(
name|secureHead
argument_list|,
name|getSecureRootState
argument_list|()
argument_list|,
name|builder
argument_list|)
return|;
block|}
comment|/**      * Secure view of the root node state of the tree including all transient changes      * at the time of this call.      *      * @return secure root node state      */
annotation|@
name|Nonnull
specifier|private
name|NodeState
name|getSecureRootState
parameter_list|()
block|{
return|return
name|rootTree
operator|.
name|getNodeState
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|SecurityContext
name|getRootContext
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
return|return
operator|new
name|SecurityContext
argument_list|(
name|root
argument_list|,
name|getPermissionProvider
argument_list|()
argument_list|,
name|getAcContext
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|PermissionProvider
name|getPermissionProvider
parameter_list|()
block|{
if|if
condition|(
name|permissionProvider
operator|==
literal|null
condition|)
block|{
name|permissionProvider
operator|=
name|createPermissionProvider
argument_list|()
expr_stmt|;
block|}
return|return
name|permissionProvider
return|;
block|}
comment|/**      * Purge all pending changes to the underlying {@link NodeStoreBranch}.      */
specifier|private
name|void
name|purgePendingChanges
parameter_list|()
block|{
name|branch
operator|.
name|setRoot
argument_list|(
name|getRootState
argument_list|()
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**      * Reset the root builder to the branch's current root state      */
specifier|private
name|void
name|reset
parameter_list|()
block|{
name|NodeState
name|root
init|=
name|branch
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|secureHead
operator|=
operator|new
name|SecureNodeState
argument_list|(
name|root
argument_list|,
name|getRootContext
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|rootTree
operator|.
name|reset
argument_list|(
name|secureHead
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|private
name|PermissionProvider
name|createPermissionProvider
parameter_list|()
block|{
return|return
name|getAcConfig
argument_list|()
operator|.
name|getPermissionProvider
argument_list|(
name|this
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|Context
name|getAcContext
parameter_list|()
block|{
return|return
name|getAcConfig
argument_list|()
operator|.
name|getContext
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|AccessControlConfiguration
name|getAcConfig
parameter_list|()
block|{
return|return
name|securityProvider
operator|.
name|getConfiguration
argument_list|(
name|AccessControlConfiguration
operator|.
name|class
argument_list|)
return|;
block|}
comment|//---------------------------------------------------------< MoveRecord>---
comment|/**      * Instances of this class record move operations which took place on this root.      * They form a singly linked list where each move instance points to the next one.      * The last entry in the list is always an empty slot to be filled in by calling      * {@code setMove()}. This fills the slot with the source and destination of the move      * and links this move to the next one which will be the new empty slot.      *      * Moves can be applied to {@code TreeImpl} instances by calling {@code apply()},      * which will execute all moves in the list on the passed tree instance      */
class|class
name|Move
block|{
comment|/** source path */
specifier|private
name|String
name|source
decl_stmt|;
comment|/** Parent tree of the destination */
specifier|private
name|TreeImpl
name|destParent
decl_stmt|;
comment|/** Name at the destination */
specifier|private
name|String
name|destName
decl_stmt|;
comment|/** Pointer to the next move. {@code null} if this is the last, empty slot */
specifier|private
name|Move
name|next
decl_stmt|;
comment|/**          * Set this move to the given source and destination. Creates a new empty slot,          * sets this as the next move and returns it.          */
name|Move
name|setMove
parameter_list|(
name|String
name|source
parameter_list|,
name|TreeImpl
name|destParent
parameter_list|,
name|String
name|destName
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|destParent
operator|=
name|destParent
expr_stmt|;
name|this
operator|.
name|destName
operator|=
name|destName
expr_stmt|;
return|return
name|next
operator|=
operator|new
name|Move
argument_list|()
return|;
block|}
comment|/**          * Apply this and all subsequent moves to the passed tree instance.          */
name|Move
name|apply
parameter_list|(
name|TreeImpl
name|tree
parameter_list|)
block|{
name|Move
name|move
init|=
name|this
decl_stmt|;
while|while
condition|(
name|move
operator|.
name|next
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|move
operator|.
name|source
operator|.
name|equals
argument_list|(
name|tree
operator|.
name|getPathInternal
argument_list|()
argument_list|)
condition|)
block|{
name|tree
operator|.
name|moveTo
argument_list|(
name|move
operator|.
name|destParent
argument_list|,
name|move
operator|.
name|destName
argument_list|)
expr_stmt|;
block|}
name|move
operator|=
name|move
operator|.
name|next
expr_stmt|;
block|}
return|return
name|move
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|source
operator|==
literal|null
condition|?
literal|"NIL"
else|:
literal|'>'
operator|+
name|source
operator|+
literal|':'
operator|+
name|PathUtils
operator|.
name|concat
argument_list|(
name|destParent
operator|.
name|getPathInternal
argument_list|()
argument_list|,
name|destName
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

