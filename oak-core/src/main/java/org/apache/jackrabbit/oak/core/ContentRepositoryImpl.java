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
name|mk
operator|.
name|index
operator|.
name|Indexer
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
name|kernel
operator|.
name|KernelNodeStore2
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
name|javax
operator|.
name|jcr
operator|.
name|GuestCredentials
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
name|jcr
operator|.
name|SimpleCredentials
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
name|log
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
specifier|final
name|MicroKernel
name|microKernel
decl_stmt|;
specifier|private
specifier|final
name|QueryEngine
name|queryEngine
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|nodeStore
decl_stmt|;
comment|/**      * Utility constructor that creates a new in-memory repository for use      * mostly in test cases.      */
specifier|public
name|ContentRepositoryImpl
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
specifier|private
name|ContentRepositoryImpl
parameter_list|(
name|MicroKernel
name|mk
parameter_list|)
block|{
name|this
argument_list|(
name|mk
argument_list|,
name|getDefaultIndexProvider
argument_list|(
name|mk
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|QueryIndexProvider
name|getDefaultIndexProvider
parameter_list|(
name|MicroKernel
name|mk
parameter_list|)
block|{
name|QueryIndexProvider
name|provider
init|=
operator|new
name|Indexer
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|provider
operator|.
name|init
argument_list|()
expr_stmt|;
return|return
name|provider
return|;
block|}
comment|/**      * Creates an Oak repository instance based on the given, already      * initialized components.      *      * @param mk underlying kernel instance      * @param indexProvider index provider      */
specifier|public
name|ContentRepositoryImpl
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|QueryIndexProvider
name|indexProvider
parameter_list|)
block|{
name|microKernel
operator|=
name|mk
expr_stmt|;
name|nodeStore
operator|=
operator|new
name|KernelNodeStore2
argument_list|(
name|microKernel
argument_list|)
expr_stmt|;
name|queryEngine
operator|=
operator|new
name|QueryEngineImpl
argument_list|(
name|nodeStore
argument_list|,
name|microKernel
argument_list|,
name|indexProvider
argument_list|)
expr_stmt|;
comment|// FIXME: workspace setup must be done elsewhere...
name|queryEngine
operator|.
name|init
argument_list|()
expr_stmt|;
name|NodeState
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeState
name|wspNode
init|=
name|root
operator|.
name|getChildNode
argument_list|(
name|DEFAULT_WORKSPACE_NAME
argument_list|)
decl_stmt|;
comment|// FIXME: depends on CoreValue's name mangling
name|String
name|ntUnstructured
init|=
literal|"nam:nt:unstructured"
decl_stmt|;
if|if
condition|(
name|wspNode
operator|==
literal|null
condition|)
block|{
name|microKernel
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\""
operator|+
name|DEFAULT_WORKSPACE_NAME
operator|+
literal|"\":{}"
operator|+
literal|"^\""
operator|+
name|DEFAULT_WORKSPACE_NAME
operator|+
literal|"/jcr:primaryType\":\""
operator|+
name|ntUnstructured
operator|+
literal|"\" "
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|ContentSession
name|login
parameter_list|(
name|Object
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
comment|// TODO: add proper implementation
comment|// TODO  - authentication against configurable spi-authentication
comment|// TODO  - validation of workspace name (including access rights for the given 'user')
specifier|final
name|SimpleCredentials
name|sc
decl_stmt|;
if|if
condition|(
name|credentials
operator|==
literal|null
operator|||
name|credentials
operator|instanceof
name|GuestCredentials
condition|)
block|{
name|sc
operator|=
operator|new
name|SimpleCredentials
argument_list|(
literal|"anonymous"
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|credentials
operator|instanceof
name|SimpleCredentials
condition|)
block|{
name|sc
operator|=
operator|(
name|SimpleCredentials
operator|)
name|credentials
expr_stmt|;
block|}
else|else
block|{
name|sc
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|sc
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|LoginException
argument_list|(
literal|"login failed"
argument_list|)
throw|;
block|}
name|NodeState
name|wspRoot
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|workspaceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|wspRoot
operator|==
literal|null
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
return|return
operator|new
name|ContentSessionImpl
argument_list|(
name|sc
argument_list|,
name|workspaceName
argument_list|,
name|nodeStore
argument_list|,
name|queryEngine
argument_list|)
return|;
block|}
block|}
end_class

end_unit

