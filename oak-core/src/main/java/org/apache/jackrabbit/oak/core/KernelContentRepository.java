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
name|api
operator|.
name|Branch
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
name|query
operator|.
name|QueryEngineImpl
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
name|KernelContentRepository
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
name|KernelContentRepository
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
name|KernelNodeStore
name|nodeStore
decl_stmt|;
specifier|public
name|KernelContentRepository
parameter_list|(
name|MicroKernel
name|mk
parameter_list|)
block|{
name|microKernel
operator|=
name|mk
expr_stmt|;
name|nodeStore
operator|=
operator|new
name|KernelNodeStore
argument_list|(
name|microKernel
argument_list|)
expr_stmt|;
comment|// FIXME: workspace setup must be done elsewhere...
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
if|if
condition|(
name|wspNode
operator|==
literal|null
condition|)
block|{
name|Branch
name|branch
init|=
name|nodeStore
operator|.
name|branch
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|branch
operator|.
name|getNode
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addNode
argument_list|(
name|DEFAULT_WORKSPACE_NAME
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|branch
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
name|QueryEngine
name|queryEngine
init|=
operator|new
name|QueryEngineImpl
argument_list|(
name|microKernel
argument_list|)
decl_stmt|;
comment|// TODO set revision!?
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
name|KernelContentSession
argument_list|(
name|sc
argument_list|,
name|workspaceName
argument_list|,
name|nodeStore
argument_list|,
name|wspRoot
argument_list|,
name|queryEngine
argument_list|)
return|;
block|}
block|}
end_class

end_unit

