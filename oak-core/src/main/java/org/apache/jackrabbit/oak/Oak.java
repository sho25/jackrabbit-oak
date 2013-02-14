begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

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
name|jcr
operator|.
name|NoSuchWorkspaceException
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
name|login
operator|.
name|LoginException
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
name|mk
operator|.
name|core
operator|.
name|MicroKernelImpl
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
name|ContentRepository
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
name|core
operator|.
name|ContentRepositoryImpl
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
name|kernel
operator|.
name|KernelNodeStore
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
name|commit
operator|.
name|ConflictHook
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
name|CompositeIndexHookProvider
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
name|IndexHookManager
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
name|IndexHookProvider
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
name|CommitHookProvider
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
name|CompositeValidatorProvider
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
name|ConflictHandler
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
name|ValidatingHook
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
name|Validator
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
name|lifecycle
operator|.
name|CompositeInitializer
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
name|lifecycle
operator|.
name|OakInitializer
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
name|lifecycle
operator|.
name|RepositoryInitializer
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
name|state
operator|.
name|NodeState
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

begin_comment
comment|/**  * Builder class for constructing {@link ContentRepository} instances with  * a set of specified plugin components. This class acts as a public facade  * that hides the internal implementation classes and the details of how  * they get instantiated and wired together.  *  * @since Oak 0.6  */
end_comment

begin_class
specifier|public
class|class
name|Oak
block|{
comment|/**      * Constant for the default workspace name      */
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_WORKSPACE_NAME
init|=
literal|"default"
decl_stmt|;
specifier|private
specifier|final
name|MicroKernel
name|kernel
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|RepositoryInitializer
argument_list|>
name|initializers
init|=
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|QueryIndexProvider
argument_list|>
name|queryIndexProviders
init|=
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|IndexHookProvider
argument_list|>
name|indexHookProviders
init|=
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|CommitHook
argument_list|>
name|commitHooks
init|=
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|ValidatorProvider
argument_list|>
name|validatorProviders
init|=
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|CommitHookProvider
argument_list|>
name|securityHookProviders
init|=
name|newArrayList
argument_list|()
decl_stmt|;
comment|// TODO: review if we really want to have the OpenSecurityProvider as default.
specifier|private
name|SecurityProvider
name|securityProvider
init|=
operator|new
name|OpenSecurityProvider
argument_list|()
decl_stmt|;
specifier|private
name|String
name|defaultWorkspaceName
init|=
name|DEFAULT_WORKSPACE_NAME
decl_stmt|;
specifier|public
name|Oak
parameter_list|(
name|MicroKernel
name|kernel
parameter_list|)
block|{
name|this
operator|.
name|kernel
operator|=
name|kernel
expr_stmt|;
block|}
specifier|public
name|Oak
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|MicroKernelImpl
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the default workspace name that should be used in case of login      * with {@code null} workspace name. If this method has not been called      * some internal default value will be used.      *      * @param defaultWorkspaceName The name of the default workspace.      * @return this builder.      */
annotation|@
name|Nonnull
specifier|public
name|Oak
name|with
parameter_list|(
annotation|@
name|Nonnull
name|String
name|defaultWorkspaceName
parameter_list|)
block|{
name|this
operator|.
name|defaultWorkspaceName
operator|=
name|defaultWorkspaceName
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Oak
name|with
parameter_list|(
annotation|@
name|Nonnull
name|RepositoryInitializer
name|initializer
parameter_list|)
block|{
name|initializers
operator|.
name|add
argument_list|(
name|checkNotNull
argument_list|(
name|initializer
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Associates the given query index provider with the repository to      * be created.      *      * @param provider query index provider      * @return this builder      */
annotation|@
name|Nonnull
specifier|public
name|Oak
name|with
parameter_list|(
annotation|@
name|Nonnull
name|QueryIndexProvider
name|provider
parameter_list|)
block|{
name|queryIndexProviders
operator|.
name|add
argument_list|(
name|provider
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Associates the given index hook provider with the repository to      * be created.      *      * @param provider index hook provider      * @return this builder      */
annotation|@
name|Nonnull
specifier|public
name|Oak
name|with
parameter_list|(
annotation|@
name|Nonnull
name|IndexHookProvider
name|provider
parameter_list|)
block|{
name|indexHookProviders
operator|.
name|add
argument_list|(
name|provider
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Associates the given commit hook with the repository to be created.      *      * @param hook commit hook      * @return this builder      */
annotation|@
name|Nonnull
specifier|public
name|Oak
name|with
parameter_list|(
annotation|@
name|Nonnull
name|CommitHook
name|hook
parameter_list|)
block|{
name|withValidatorHook
argument_list|()
expr_stmt|;
name|commitHooks
operator|.
name|add
argument_list|(
name|hook
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Turns all currently tracked validators to a validating commit hook      * and associates that hook with the repository to be created. This way      * a sequence of {@code with()} calls that alternates between validators      * and other commit hooks will have all the validators in the correct      * order while still being able to leverage the performance gains of      * multiple validators iterating over the changes simultaneously.      */
specifier|private
name|void
name|withValidatorHook
parameter_list|()
block|{
if|if
condition|(
operator|!
name|validatorProviders
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
name|ValidatingHook
argument_list|(
name|CompositeValidatorProvider
operator|.
name|compose
argument_list|(
name|validatorProviders
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|validatorProviders
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Adds all currently tracked security related hooks to the commit hook that      * is used to create the content repository.      */
specifier|private
name|void
name|withSecurityHooks
parameter_list|()
block|{
if|if
condition|(
operator|!
name|securityHookProviders
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|CommitHookProvider
name|provider
range|:
name|securityHookProviders
control|)
block|{
comment|// FIXME: hack to pass the workspace name into the commit hook
comment|// FIXME: this needs to be re-factored once we add support for multiple workspaces support (OAK-118)
name|CommitHook
name|hook
init|=
name|provider
operator|.
name|getCommitHook
argument_list|(
name|defaultWorkspaceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|hook
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
name|hook
argument_list|)
expr_stmt|;
block|}
block|}
name|securityHookProviders
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Associates the given validator provider with the repository to      * be created.      *      * @param provider validator provider      * @return this builder      */
annotation|@
name|Nonnull
specifier|public
name|Oak
name|with
parameter_list|(
annotation|@
name|Nonnull
name|ValidatorProvider
name|provider
parameter_list|)
block|{
name|validatorProviders
operator|.
name|add
argument_list|(
name|provider
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Associates the given validator with the repository to be created.      *      * @param validator validator      * @return this builder      */
annotation|@
name|Nonnull
specifier|public
name|Oak
name|with
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Validator
name|validator
parameter_list|)
block|{
return|return
name|with
argument_list|(
operator|new
name|ValidatorProvider
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Validator
name|getRootValidator
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|validator
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Oak
name|with
parameter_list|(
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|this
operator|.
name|securityProvider
operator|=
name|securityProvider
expr_stmt|;
for|for
control|(
name|SecurityConfiguration
name|sc
range|:
name|securityProvider
operator|.
name|getSecurityConfigurations
argument_list|()
control|)
block|{
name|validatorProviders
operator|.
name|addAll
argument_list|(
name|sc
operator|.
name|getValidatorProviders
argument_list|()
argument_list|)
expr_stmt|;
name|securityHookProviders
operator|.
name|add
argument_list|(
name|sc
operator|.
name|getCommitHookProvider
argument_list|()
argument_list|)
expr_stmt|;
name|initializers
operator|.
name|add
argument_list|(
name|sc
operator|.
name|getRepositoryInitializer
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Associates the given conflict handler with the repository to be created.      *      * @param conflictHandler conflict handler      * @return this builder      */
annotation|@
name|Nonnull
specifier|public
name|Oak
name|with
parameter_list|(
annotation|@
name|Nonnull
name|ConflictHandler
name|conflictHandler
parameter_list|)
block|{
name|withValidatorHook
argument_list|()
expr_stmt|;
name|commitHooks
operator|.
name|add
argument_list|(
operator|new
name|ConflictHook
argument_list|(
name|conflictHandler
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ContentRepository
name|createContentRepository
parameter_list|()
block|{
name|KernelNodeStore
name|store
init|=
operator|new
name|KernelNodeStore
argument_list|(
name|kernel
argument_list|)
decl_stmt|;
name|IndexHookProvider
name|indexHooks
init|=
name|CompositeIndexHookProvider
operator|.
name|compose
argument_list|(
name|indexHookProviders
argument_list|)
decl_stmt|;
name|OakInitializer
operator|.
name|initialize
argument_list|(
name|store
argument_list|,
operator|new
name|CompositeInitializer
argument_list|(
name|initializers
argument_list|)
argument_list|,
name|indexHooks
argument_list|)
expr_stmt|;
name|commitHooks
operator|.
name|add
argument_list|(
name|IndexHookManager
operator|.
name|of
argument_list|(
name|indexHooks
argument_list|)
argument_list|)
expr_stmt|;
name|withValidatorHook
argument_list|()
expr_stmt|;
name|withSecurityHooks
argument_list|()
expr_stmt|;
name|store
operator|.
name|setHook
argument_list|(
name|CompositeHook
operator|.
name|compose
argument_list|(
name|commitHooks
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|ContentRepositoryImpl
argument_list|(
name|store
argument_list|,
name|defaultWorkspaceName
argument_list|,
name|CompositeQueryIndexProvider
operator|.
name|compose
argument_list|(
name|queryIndexProviders
argument_list|)
argument_list|,
name|securityProvider
argument_list|)
return|;
block|}
comment|/**      * Creates a content repository with the given configuration      * and logs in to the default workspace with no credentials,      * returning the resulting content session.      *<p/>      * This method exists mostly as a convenience for one-off tests,      * as there's no way to create other sessions for accessing the      * same repository.      *<p/>      * There is typically no need to explicitly close the returned      * session unless the repository has explicitly been configured      * to reserve some resources until all sessions have been closed.      * The repository will be garbage collected once the session is no      * longer used.      *      * @return content session      */
specifier|public
name|ContentSession
name|createContentSession
parameter_list|()
block|{
try|try
block|{
return|return
name|createContentRepository
argument_list|()
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchWorkspaceException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Default workspace not found"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Anonymous login not allowed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Creates a content repository with the given configuration      * and returns a {@link Root} instance after logging in to the      * default workspace with no credentials.      *<p/>      * This method exists mostly as a convenience for one-off tests, as      * the returned root is the only way to access the session or the      * repository.      *<p/>      * Note that since there is no way to close the underlying content      * session, this method should only be used when no components that      * require sessions to be closed have been configured. The repository      * and the session will be garbage collected once the root is no longer      * used.      *      * @return root instance      */
specifier|public
name|Root
name|createRoot
parameter_list|()
block|{
return|return
name|createContentSession
argument_list|()
operator|.
name|getLatestRoot
argument_list|()
return|;
block|}
block|}
end_class

end_unit

