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
name|core
package|;
end_package

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
name|Credentials
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
name|LoginContext
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
name|AnnotatingConflictHandlerProvider
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
name|LoginContextProviderImpl
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
name|ConflictHandlerProvider
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
name|DefaultValidatorProvider
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
name|authentication
operator|.
name|LoginContextProvider
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
comment|/**  * {@link MicroKernel}-based implementation of  * the {@link ContentRepository} interface.  */
end_comment

begin_class
specifier|public
class|class
name|ContentRepositoryImpl
implements|implements
name|ContentRepository
block|{
comment|/** Logger instance */
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ContentRepositoryImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// TODO: retrieve default wsp-name from configuration
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_WORKSPACE_NAME
init|=
literal|"default"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ConflictHandlerProvider
name|DEFAULT_CONFLICT_HANDLER_PROVIDER
init|=
operator|new
name|AnnotatingConflictHandlerProvider
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|LoginContextProvider
name|loginContextProvider
decl_stmt|;
specifier|private
specifier|final
name|QueryIndexProvider
name|indexProvider
decl_stmt|;
specifier|private
specifier|final
name|KernelNodeStore
name|nodeStore
decl_stmt|;
comment|/**      * Utility constructor that creates a new in-memory repository with default      * query index provider. This constructor is intended to be used within      * test cases only.      */
specifier|public
name|ContentRepositoryImpl
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|CompositeHook
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ContentRepositoryImpl
parameter_list|(
name|CommitHook
name|hook
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|MicroKernelImpl
argument_list|()
argument_list|,
operator|new
name|LoginContextProviderImpl
argument_list|()
argument_list|,
operator|new
name|CompositeQueryIndexProvider
argument_list|()
argument_list|,
name|hook
argument_list|)
expr_stmt|;
block|}
comment|/**      * Utility constructor, intended to be used within test cases only.      *      * Creates an Oak repository instance based on the given, already      * initialized components.      *      * @param microKernel      *            underlying kernel instance      * @param indexProvider      *            index provider      * @param validatorProvider      *            the validation provider      */
specifier|public
name|ContentRepositoryImpl
parameter_list|(
name|MicroKernel
name|microKernel
parameter_list|,
name|QueryIndexProvider
name|indexProvider
parameter_list|,
name|ValidatorProvider
name|validatorProvider
parameter_list|)
block|{
name|this
argument_list|(
name|microKernel
argument_list|,
operator|new
name|LoginContextProviderImpl
argument_list|()
argument_list|,
name|indexProvider
argument_list|,
operator|new
name|ValidatingHook
argument_list|(
name|validatorProvider
operator|!=
literal|null
condition|?
name|validatorProvider
else|:
name|DefaultValidatorProvider
operator|.
name|INSTANCE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ContentRepositoryImpl
parameter_list|(
name|MicroKernel
name|microKernel
parameter_list|,
name|ValidatorProvider
name|validatorProvider
parameter_list|)
block|{
name|this
argument_list|(
name|microKernel
argument_list|,
literal|null
argument_list|,
name|validatorProvider
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates an Oak repository instance based on the given, already      * initialized components.      *      * @param microKernel underlying kernel instance      * @param loginContextProvider login context provider      * @param indexProvider index provider      * @param commitHook the commit hook      */
specifier|public
name|ContentRepositoryImpl
parameter_list|(
name|MicroKernel
name|microKernel
parameter_list|,
name|LoginContextProvider
name|loginContextProvider
parameter_list|,
name|QueryIndexProvider
name|indexProvider
parameter_list|,
name|CommitHook
name|commitHook
parameter_list|)
block|{
name|nodeStore
operator|=
operator|new
name|KernelNodeStore
argument_list|(
name|microKernel
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|setHook
argument_list|(
name|commitHook
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexProvider
operator|=
name|indexProvider
operator|!=
literal|null
condition|?
name|indexProvider
else|:
operator|new
name|CompositeQueryIndexProvider
argument_list|()
expr_stmt|;
name|this
operator|.
name|loginContextProvider
operator|=
name|loginContextProvider
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|ContentSession
name|login
parameter_list|(
name|Credentials
name|credentials
parameter_list|,
name|String
name|workspaceName
parameter_list|)
throws|throws
name|LoginException
throws|,
name|NoSuchWorkspaceException
block|{
if|if
condition|(
name|workspaceName
operator|==
literal|null
condition|)
block|{
name|workspaceName
operator|=
name|DEFAULT_WORKSPACE_NAME
expr_stmt|;
block|}
comment|// TODO: support multiple workspaces. See OAK-118
if|if
condition|(
operator|!
name|DEFAULT_WORKSPACE_NAME
operator|.
name|equals
argument_list|(
name|workspaceName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NoSuchWorkspaceException
argument_list|(
name|workspaceName
argument_list|)
throw|;
block|}
name|LoginContext
name|loginContext
init|=
name|loginContextProvider
operator|.
name|getLoginContext
argument_list|(
name|credentials
argument_list|,
name|workspaceName
argument_list|)
decl_stmt|;
name|loginContext
operator|.
name|login
argument_list|()
expr_stmt|;
return|return
operator|new
name|ContentSessionImpl
argument_list|(
name|loginContext
argument_list|,
name|workspaceName
argument_list|,
name|nodeStore
argument_list|,
name|DEFAULT_CONFLICT_HANDLER_PROVIDER
argument_list|,
name|indexProvider
argument_list|)
return|;
block|}
block|}
end_class

end_unit

