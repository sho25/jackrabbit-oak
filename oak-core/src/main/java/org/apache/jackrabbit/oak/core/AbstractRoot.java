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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
name|isAncestor
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
name|annotation
operator|.
name|Nullable
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
name|query
operator|.
name|ExecutionContext
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
name|CommitInfo
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
name|util
operator|.
name|LazyValue
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractRoot
implements|implements
name|Root
block|{
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
name|MutableTree
name|rootTree
decl_stmt|;
comment|/**      * Unsecured builder for the root tree      */
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
comment|/**      * Secured builder for the root tree      */
specifier|private
specifier|final
name|SecureNodeBuilder
name|secureBuilder
decl_stmt|;
comment|/**      * Base state of the root tree      */
specifier|private
name|NodeState
name|base
decl_stmt|;
comment|/**      * Sentinel for the next move operation to take place on the this root      */
specifier|private
name|Move
name|lastMove
init|=
operator|new
name|Move
argument_list|()
decl_stmt|;
comment|/**      * Number of {@link #updated} occurred.      */
specifier|private
name|long
name|modCount
decl_stmt|;
specifier|private
specifier|final
name|LazyValue
argument_list|<
name|PermissionProvider
argument_list|>
name|permissionProvider
init|=
operator|new
name|LazyValue
argument_list|<
name|PermissionProvider
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|PermissionProvider
name|createValue
parameter_list|()
block|{
return|return
name|getAcConfig
argument_list|()
operator|.
name|getPermissionProvider
argument_list|(
name|AbstractRoot
operator|.
name|this
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**      * New instance bases on a given {@link NodeStore} and a workspace      *      * @param store            node store      * @param hook             the commit hook      * @param workspaceName    name of the workspace      * @param subject          the subject.      * @param securityProvider the security configuration.      * @param indexProvider    the query index provider.      */
specifier|protected
name|AbstractRoot
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|CommitHook
name|hook
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
name|base
operator|=
name|store
operator|.
name|getRoot
argument_list|()
expr_stmt|;
name|builder
operator|=
name|base
operator|.
name|builder
argument_list|()
expr_stmt|;
name|secureBuilder
operator|=
operator|new
name|SecureNodeBuilder
argument_list|(
name|builder
argument_list|,
name|permissionProvider
argument_list|,
name|getAcContext
argument_list|()
argument_list|)
expr_stmt|;
name|rootTree
operator|=
operator|new
name|MutableTree
argument_list|(
name|this
argument_list|,
name|secureBuilder
argument_list|,
name|lastMove
argument_list|)
expr_stmt|;
block|}
comment|/**      * Called whenever a method on this instance or on any {@code Tree} instance      * obtained from this {@code Root} is called. This default implementation      * does nothing. Sub classes may override this method and throw an exception      * indicating that this {@code Root} instance is not live anymore (e.g. because      * the session has been logged out already).      */
specifier|protected
name|void
name|checkLive
parameter_list|()
block|{     }
specifier|protected
name|String
name|getUserData
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|//---------------------------------------------------------------< Root>---
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
if|if
condition|(
name|isAncestor
argument_list|(
name|checkNotNull
argument_list|(
name|sourcePath
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|destPath
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|sourcePath
operator|.
name|equals
argument_list|(
name|destPath
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|checkLive
argument_list|()
expr_stmt|;
name|MutableTree
name|source
init|=
name|rootTree
operator|.
name|getTree
argument_list|(
name|sourcePath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|source
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|newName
init|=
name|getName
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
name|MutableTree
name|newParent
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
name|newParent
operator|.
name|exists
argument_list|()
operator|||
name|newParent
operator|.
name|hasChild
argument_list|(
name|newName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|success
init|=
name|source
operator|.
name|moveTo
argument_list|(
name|newParent
argument_list|,
name|newName
argument_list|)
decl_stmt|;
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
name|newParent
argument_list|,
name|newName
argument_list|)
expr_stmt|;
name|updated
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
name|MutableTree
name|source
init|=
name|rootTree
operator|.
name|getTree
argument_list|(
name|sourcePath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|source
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|newName
init|=
name|getName
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
name|MutableTree
name|newParent
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
name|newParent
operator|.
name|exists
argument_list|()
operator|||
name|newParent
operator|.
name|hasChild
argument_list|(
name|newName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|success
init|=
name|source
operator|.
name|copyTo
argument_list|(
name|newParent
argument_list|,
name|newName
argument_list|)
decl_stmt|;
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
name|updated
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
name|MutableTree
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
name|store
operator|.
name|rebase
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|secureBuilder
operator|.
name|baseChanged
argument_list|()
expr_stmt|;
if|if
condition|(
name|permissionProvider
operator|.
name|hasValue
argument_list|()
condition|)
block|{
name|permissionProvider
operator|.
name|get
argument_list|()
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
name|base
operator|=
name|store
operator|.
name|reset
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|secureBuilder
operator|.
name|baseChanged
argument_list|()
expr_stmt|;
name|modCount
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|permissionProvider
operator|.
name|hasValue
argument_list|()
condition|)
block|{
name|permissionProvider
operator|.
name|get
argument_list|()
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
name|commit
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|commit
parameter_list|(
annotation|@
name|Nullable
name|String
name|message
parameter_list|,
annotation|@
name|Nullable
name|CommitHook
name|hook
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|checkLive
argument_list|()
expr_stmt|;
name|ContentSession
name|session
init|=
name|getContentSession
argument_list|()
decl_stmt|;
name|CommitInfo
name|info
init|=
operator|new
name|CommitInfo
argument_list|(
name|session
operator|.
name|toString
argument_list|()
argument_list|,
name|session
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getUserID
argument_list|()
argument_list|,
name|message
argument_list|)
decl_stmt|;
name|base
operator|=
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|getCommitHook
argument_list|(
name|hook
argument_list|)
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|secureBuilder
operator|.
name|baseChanged
argument_list|()
expr_stmt|;
name|modCount
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|permissionProvider
operator|.
name|hasValue
argument_list|()
condition|)
block|{
name|permissionProvider
operator|.
name|get
argument_list|()
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Combine the passed {@code hook}, the globally defined commit hook(s)      * and the hooks and validators defined by the various security related      * configurations.      *      * @param hook extra hook to be used for just this commit, or {@code null}      * @return A commit hook combining repository global commit hook(s) with the pluggable hooks      *         defined with the security modules and the padded {@code hooks}.      */
specifier|private
name|CommitHook
name|getCommitHook
parameter_list|(
annotation|@
name|Nullable
name|CommitHook
name|extraHook
parameter_list|)
block|{
name|List
argument_list|<
name|CommitHook
argument_list|>
name|hooks
init|=
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|extraHook
operator|!=
literal|null
condition|)
block|{
name|hooks
operator|.
name|add
argument_list|(
name|extraHook
argument_list|)
expr_stmt|;
block|}
name|hooks
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
name|hooks
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
argument_list|,
name|getCommitSubject
argument_list|()
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
name|hooks
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
name|hooks
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
name|hooks
argument_list|)
return|;
block|}
comment|/**      * TODO: review again once the permission validation is completed.      * Build a read only subject for the {@link #commit(CommitHook...)} call that makes the      * principals and the permission provider available to the commit hooks.      *      * @return a new read only subject.      */
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
name|permissionProvider
operator|.
name|get
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
name|modCount
operator|>
literal|0
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
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ExecutionContext
name|getExecutionContext
parameter_list|()
block|{
name|QueryIndexProvider
name|provider
init|=
name|indexProvider
decl_stmt|;
if|if
condition|(
name|hasPendingChanges
argument_list|()
condition|)
block|{
name|provider
operator|=
operator|new
name|UUIDDiffIndexProviderWrapper
argument_list|(
name|provider
argument_list|,
name|getBaseState
argument_list|()
argument_list|,
name|getRootState
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ExecutionContext
argument_list|(
name|getBaseState
argument_list|()
argument_list|,
name|rootTree
argument_list|,
name|provider
argument_list|)
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
comment|//-----------------------------------------------------------< internal>---
comment|/**      * Returns the node state from the time this root was created, that      * is this root's base state.      *      * @return base node state      */
annotation|@
name|Nonnull
name|NodeState
name|getBaseState
parameter_list|()
block|{
return|return
name|base
return|;
block|}
comment|/**      * Returns the secure view of this root's base state.      *      * @return secure base node state      */
name|NodeState
name|getSecureBase
parameter_list|()
block|{
return|return
operator|new
name|SecureNodeState
argument_list|(
name|base
argument_list|,
name|permissionProvider
operator|.
name|get
argument_list|()
argument_list|,
name|getAcContext
argument_list|()
argument_list|)
return|;
block|}
name|void
name|updated
parameter_list|()
block|{
name|modCount
operator|++
expr_stmt|;
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
return|return
name|builder
operator|.
name|getNodeState
argument_list|()
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
name|AuthorizationConfiguration
name|getAcConfig
parameter_list|()
block|{
return|return
name|securityProvider
operator|.
name|getConfiguration
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
return|;
block|}
comment|//---------------------------------------------------------< MoveRecord>---
comment|/**      * Instances of this class record move operations which took place on this root.      * They form a singly linked list where each move instance points to the next one.      * The last entry in the list is always an empty slot to be filled in by calling      * {@code setMove()}. This fills the slot with the source and destination of the move      * and links this move to the next one which will be the new empty slot.      *<p/>      * Moves can be applied to {@code MutableTree} instances by calling {@code apply()},      * which will execute all moves in the list on the passed tree instance      */
class|class
name|Move
block|{
comment|/**          * source path          */
specifier|private
name|String
name|source
decl_stmt|;
comment|/**          * Parent tree of the destination          */
specifier|private
name|MutableTree
name|destParent
decl_stmt|;
comment|/**          * Name at the destination          */
specifier|private
name|String
name|destName
decl_stmt|;
comment|/**          * Pointer to the next move. {@code null} if this is the last, empty slot          */
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
name|MutableTree
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
name|MutableTree
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
name|setParentAndName
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

