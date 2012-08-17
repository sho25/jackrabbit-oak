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
name|java
operator|.
name|util
operator|.
name|Collections
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
name|security
operator|.
name|authorization
operator|.
name|AccessControlContextImpl
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
name|commit
operator|.
name|CommitEditor
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
name|ValidatingEditor
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
name|AccessControlContext
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
specifier|final
name|LoginContextProvider
name|loginContextProvider
decl_stmt|;
specifier|private
specifier|final
name|QueryEngineImpl
name|queryEngine
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
name|MicroKernelImpl
argument_list|()
argument_list|,
literal|null
argument_list|,
operator|new
name|ValidatingEditor
argument_list|(
operator|new
name|CompositeValidatorProvider
argument_list|(
name|Collections
operator|.
expr|<
name|ValidatorProvider
operator|>
name|emptyList
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// this(new IndexWrapper(new MicroKernelImpl()), null, null);
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
name|indexProvider
argument_list|,
operator|new
name|ValidatingEditor
argument_list|(
name|validatorProvider
operator|!=
literal|null
condition|?
name|validatorProvider
else|:
operator|new
name|CompositeValidatorProvider
argument_list|(
name|Collections
operator|.
expr|<
name|ValidatorProvider
operator|>
name|emptyList
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates an Oak repository instance based on the given, already      * initialized components.      *      * @param microKernel underlying kernel instance      * @param indexProvider index provider      * @param commitEditor the commit editor      */
specifier|public
name|ContentRepositoryImpl
parameter_list|(
name|MicroKernel
name|microKernel
parameter_list|,
name|QueryIndexProvider
name|indexProvider
parameter_list|,
name|CommitEditor
name|commitEditor
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
name|setEditor
argument_list|(
name|commitEditor
argument_list|)
expr_stmt|;
name|QueryIndexProvider
name|qip
init|=
operator|(
name|indexProvider
operator|==
literal|null
operator|)
condition|?
name|getDefaultIndexProvider
argument_list|(
name|microKernel
argument_list|)
else|:
name|indexProvider
decl_stmt|;
name|queryEngine
operator|=
operator|new
name|QueryEngineImpl
argument_list|(
name|nodeStore
argument_list|,
name|microKernel
argument_list|,
name|qip
argument_list|)
expr_stmt|;
comment|// TODO: use configurable context provider
name|loginContextProvider
operator|=
operator|new
name|LoginContextProviderImpl
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// FIXME: repository setup must be done elsewhere...
comment|// FIXME: depends on CoreValue's name mangling
name|NodeState
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
if|if
condition|(
name|root
operator|.
name|hasChildNode
argument_list|(
literal|"jcr:system"
argument_list|)
condition|)
block|{
name|microKernel
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"jcr:primaryType\":\"nam:rep:root\" "
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|microKernel
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"jcr:primaryType\":\"nam:rep:root\""
operator|+
literal|"+\"jcr:system\":{"
operator|+
literal|"\"jcr:primaryType\"    :\"nam:rep:system\","
operator|+
literal|"\"jcr:versionStorage\" :{\"jcr:primaryType\":\"nam:rep:versionStorage\"},"
operator|+
literal|"\"jcr:nodeTypes\"      :{\"jcr:primaryType\":\"nam:rep:nodeTypes\"},"
operator|+
literal|"\"jcr:activities\"     :{\"jcr:primaryType\":\"nam:rep:Activities\"},"
operator|+
literal|"\"rep:privileges\"     :{\"jcr:primaryType\":\"nam:rep:Privileges\"}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
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
return|return
name|Indexer
operator|.
name|getInstance
argument_list|(
name|mk
argument_list|)
return|;
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
comment|// TODO make configurable
name|AccessControlContext
name|acContext
init|=
operator|new
name|AccessControlContextImpl
argument_list|()
decl_stmt|;
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
name|queryEngine
argument_list|,
name|acContext
argument_list|)
return|;
block|}
block|}
end_class

end_unit

