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
name|run
operator|.
name|osgi
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|Node
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
name|RepositoryFactory
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
name|SimpleCredentials
import|;
end_import

begin_import
import|import
name|de
operator|.
name|kalpatec
operator|.
name|pojosr
operator|.
name|framework
operator|.
name|launch
operator|.
name|PojoServiceRegistry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|commons
operator|.
name|JcrUtils
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
name|namepath
operator|.
name|NamePathMapper
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
name|user
operator|.
name|action
operator|.
name|AbstractAuthorizableAction
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
name|user
operator|.
name|action
operator|.
name|AuthorizableAction
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
name|user
operator|.
name|action
operator|.
name|AuthorizableActionProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FilenameUtils
operator|.
name|concat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"OAK-1522"
argument_list|)
specifier|public
class|class
name|OakOSGiRepositoryFactoryTest
block|{
specifier|private
name|String
name|repositoryHome
decl_stmt|;
specifier|private
name|RepositoryFactory
name|repositoryFactory
init|=
operator|new
name|CustomOakFactory
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|config
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|String
name|newPassword
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|repositoryHome
operator|=
name|concat
argument_list|(
name|getBaseDir
argument_list|()
argument_list|,
literal|"target/repository"
argument_list|)
expr_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"org.apache.jackrabbit.repository.home"
argument_list|,
name|repositoryHome
argument_list|)
expr_stmt|;
name|File
name|repoHome
init|=
operator|new
name|File
argument_list|(
name|repositoryHome
argument_list|)
decl_stmt|;
if|if
condition|(
name|repoHome
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|cleanDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|repositoryHome
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|copyConfig
argument_list|(
literal|"common"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRepositoryTar
parameter_list|()
throws|throws
name|Exception
block|{
name|copyConfig
argument_list|(
literal|"tar"
argument_list|)
expr_stmt|;
name|Repository
name|repository
init|=
name|repositoryFactory
operator|.
name|getRepository
argument_list|(
name|config
argument_list|)
decl_stmt|;
comment|//Give time for system to stablize :(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|repository
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Repository started "
argument_list|)
expr_stmt|;
name|basicCrudTest
argument_list|(
name|repository
argument_list|)
expr_stmt|;
comment|//For now SecurityConfig is giving some issue
comment|//so disable that
name|testCallback
argument_list|(
name|repository
argument_list|)
expr_stmt|;
name|shutdown
argument_list|(
name|repository
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testCallback
parameter_list|(
name|Repository
name|repository
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|JackrabbitSession
name|session
init|=
operator|(
name|JackrabbitSession
operator|)
name|repository
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|testUserId
init|=
literal|"footest"
decl_stmt|;
name|User
name|testUser
init|=
operator|(
name|User
operator|)
name|session
operator|.
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
name|testUserId
argument_list|)
decl_stmt|;
if|if
condition|(
name|testUser
operator|==
literal|null
condition|)
block|{
name|testUser
operator|=
name|session
operator|.
name|getUserManager
argument_list|()
operator|.
name|createUser
argument_list|(
name|testUserId
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|testUser
operator|.
name|changePassword
argument_list|(
literal|"newPassword"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"newPassword"
argument_list|,
name|newPassword
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|basicCrudTest
parameter_list|(
name|Repository
name|repository
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|repository
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Node
name|rootNode
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|Node
name|child
init|=
name|JcrUtils
operator|.
name|getOrAddNode
argument_list|(
name|rootNode
argument_list|,
literal|"child"
argument_list|,
literal|"oak:Unstructured"
argument_list|)
decl_stmt|;
name|child
operator|.
name|setProperty
argument_list|(
literal|"foo3"
argument_list|,
literal|"bar3"
argument_list|)
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Basic test passed"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|shutdown
parameter_list|(
name|Repository
name|repository
parameter_list|)
block|{
if|if
condition|(
name|repository
operator|instanceof
name|JackrabbitRepository
condition|)
block|{
operator|(
operator|(
name|JackrabbitRepository
operator|)
name|repository
operator|)
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|copyConfig
parameter_list|(
name|String
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|concat
argument_list|(
name|getBaseDir
argument_list|()
argument_list|,
literal|"src/test/resources/config-"
operator|+
name|type
argument_list|)
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|concat
argument_list|(
name|repositoryHome
argument_list|,
literal|"config"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|getBaseDir
parameter_list|()
block|{
return|return
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
specifier|private
class|class
name|CustomOakFactory
extends|extends
name|OakOSGiRepositoryFactory
block|{
annotation|@
name|Override
specifier|protected
name|void
name|postProcessRegistry
parameter_list|(
name|PojoServiceRegistry
name|registry
parameter_list|)
block|{
name|registry
operator|.
name|registerService
argument_list|(
name|AuthorizableActionProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
operator|new
name|AuthorizableActionProvider
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|AuthorizableAction
argument_list|>
name|getAuthorizableActions
parameter_list|(
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|TestAction
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|TestAction
extends|extends
name|AbstractAuthorizableAction
block|{
annotation|@
name|Override
specifier|public
name|void
name|onPasswordChange
parameter_list|(
annotation|@
name|Nonnull
name|User
name|user
parameter_list|,
annotation|@
name|Nullable
name|String
name|newPassword
parameter_list|,
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|OakOSGiRepositoryFactoryTest
operator|.
name|this
operator|.
name|newPassword
operator|=
name|newPassword
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

