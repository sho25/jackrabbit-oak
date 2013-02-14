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
name|authentication
operator|.
name|LoginContext
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
name|state
operator|.
name|NodeStore
import|;
end_import

begin_comment
comment|/**  * {@code MicroKernel}-based implementation of  * the {@link ContentRepository} interface.  */
end_comment

begin_class
specifier|public
class|class
name|ContentRepositoryImpl
implements|implements
name|ContentRepository
block|{
specifier|private
specifier|final
name|String
name|defaultWorkspaceName
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
specifier|private
specifier|final
name|NodeStore
name|nodeStore
decl_stmt|;
comment|/**      * Creates an content repository instance based on the given, already      * initialized components.      *      * @param nodeStore            the node store this repository is based upon.      * @param defaultWorkspaceName the default workspace name;      * @param indexProvider        index provider      * @param securityProvider     The configured security provider.      */
specifier|public
name|ContentRepositoryImpl
parameter_list|(
annotation|@
name|Nonnull
name|NodeStore
name|nodeStore
parameter_list|,
annotation|@
name|Nonnull
name|String
name|defaultWorkspaceName
parameter_list|,
annotation|@
name|Nullable
name|QueryIndexProvider
name|indexProvider
parameter_list|,
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
name|this
operator|.
name|defaultWorkspaceName
operator|=
name|defaultWorkspaceName
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
name|securityProvider
operator|=
name|securityProvider
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
name|defaultWorkspaceName
expr_stmt|;
block|}
comment|// TODO: support multiple workspaces. See OAK-118
if|if
condition|(
operator|!
name|defaultWorkspaceName
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
name|LoginContextProvider
name|lcProvider
init|=
name|securityProvider
operator|.
name|getAuthenticationConfiguration
argument_list|()
operator|.
name|getLoginContextProvider
argument_list|(
name|nodeStore
argument_list|,
name|indexProvider
argument_list|)
decl_stmt|;
name|LoginContext
name|loginContext
init|=
name|lcProvider
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
name|AccessControlConfiguration
name|acConfiguration
init|=
name|securityProvider
operator|.
name|getAccessControlConfiguration
argument_list|()
decl_stmt|;
return|return
operator|new
name|ContentSessionImpl
argument_list|(
name|loginContext
argument_list|,
name|acConfiguration
argument_list|,
name|workspaceName
argument_list|,
name|nodeStore
argument_list|,
name|indexProvider
argument_list|)
return|;
block|}
specifier|public
name|NodeStore
name|getNodeStore
parameter_list|()
block|{
return|return
name|nodeStore
return|;
block|}
block|}
end_class

end_unit

