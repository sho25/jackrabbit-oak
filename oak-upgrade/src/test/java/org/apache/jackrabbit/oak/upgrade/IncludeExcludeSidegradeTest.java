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
name|upgrade
package|;
end_package

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
name|jcr
operator|.
name|SimpleCredentials
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
name|JcrConstants
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
name|commons
operator|.
name|jackrabbit
operator|.
name|authorization
operator|.
name|AccessControlUtils
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
name|jcr
operator|.
name|Jcr
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
name|jcr
operator|.
name|repository
operator|.
name|RepositoryImpl
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
name|memory
operator|.
name|MemoryNodeStore
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
name|internal
operator|.
name|SecurityProviderBuilder
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
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|assertFalse
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|IncludeExcludeSidegradeTest
block|{
specifier|public
specifier|static
specifier|final
name|Credentials
name|CREDENTIALS
init|=
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
decl_stmt|;
specifier|private
name|NodeStore
name|sourceNodeStore
decl_stmt|;
specifier|private
name|NodeStore
name|targetNodeStore
decl_stmt|;
specifier|private
name|RepositoryImpl
name|targetRepository
decl_stmt|;
specifier|private
name|Session
name|targetSession
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|prepareNodeStores
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|sourceNodeStore
operator|=
operator|new
name|MemoryNodeStore
argument_list|()
expr_stmt|;
name|withSession
argument_list|(
name|sourceNodeStore
argument_list|,
name|s
lambda|->
block|{
name|createCommonContent
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|createSourceContent
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|targetNodeStore
operator|=
operator|new
name|MemoryNodeStore
argument_list|()
expr_stmt|;
name|withSession
argument_list|(
name|targetNodeStore
argument_list|,
name|s
lambda|->
block|{
name|createCommonContent
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|performSidegrade
argument_list|()
expr_stmt|;
name|targetRepository
operator|=
name|getRepository
argument_list|(
name|targetNodeStore
argument_list|)
expr_stmt|;
name|targetSession
operator|=
name|targetRepository
operator|.
name|login
argument_list|(
name|CREDENTIALS
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|targetRepository
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldHaveIncludedPaths
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|assertExists
argument_list|(
literal|"/content/foo/en"
argument_list|,
literal|"/content/assets/foo/2015/02"
argument_list|,
literal|"/content/assets/foo/2015/01"
argument_list|,
literal|"/content/assets/foo/2014"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldLackPathsThatWereNotIncluded
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|assertMissing
argument_list|(
literal|"/content/foo/de"
argument_list|,
literal|"/content/foo/fr"
argument_list|,
literal|"/content/foo/it"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldLackExcludedPaths
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|assertMissing
argument_list|(
literal|"/content/assets/foo/2013"
argument_list|,
literal|"/content/assets/foo/2012"
argument_list|,
literal|"/content/assets/foo/2011"
argument_list|,
literal|"/content/assets/foo/2010"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPermissions
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|aliceSession
init|=
name|targetRepository
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"alice"
argument_list|,
literal|"bar"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Session
name|bobSession
init|=
name|targetRepository
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"bob"
argument_list|,
literal|"bar"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertExists
argument_list|(
name|aliceSession
argument_list|,
literal|"/content/assets/foo/2015/02"
argument_list|,
literal|"/content/assets/foo/2015/01"
argument_list|,
literal|"/content/assets/foo/2014"
argument_list|)
expr_stmt|;
name|assertMissing
argument_list|(
name|aliceSession
argument_list|,
literal|"/content/foo/en"
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
name|bobSession
argument_list|,
literal|"/content/foo/en"
argument_list|)
expr_stmt|;
name|assertMissing
argument_list|(
name|bobSession
argument_list|,
literal|"/content/assets/foo/2015/02"
argument_list|,
literal|"/content/assets/foo/2015/01"
argument_list|,
literal|"/content/assets/foo/2014"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createCommonContent
parameter_list|(
name|JackrabbitSession
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|UserManager
name|um
init|=
name|session
operator|.
name|getUserManager
argument_list|()
decl_stmt|;
name|um
operator|.
name|createUser
argument_list|(
literal|"alice"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|um
operator|.
name|createUser
argument_list|(
literal|"bob"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createSourceContent
parameter_list|(
name|JackrabbitSession
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|String
name|p
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"/content/foo/de"
argument_list|,
literal|"/content/foo/en"
argument_list|,
literal|"/content/foo/fr"
argument_list|,
literal|"/content/foo/it"
argument_list|,
literal|"/content/assets/foo"
argument_list|,
literal|"/content/assets/foo/2015"
argument_list|,
literal|"/content/assets/foo/2015/02"
argument_list|,
literal|"/content/assets/foo/2015/01"
argument_list|,
literal|"/content/assets/foo/2014"
argument_list|,
literal|"/content/assets/foo/2013"
argument_list|,
literal|"/content/assets/foo/2012"
argument_list|,
literal|"/content/assets/foo/2011"
argument_list|,
literal|"/content/assets/foo/2010/12"
argument_list|)
control|)
block|{
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
name|p
argument_list|,
name|JcrConstants
operator|.
name|NT_FOLDER
argument_list|,
name|JcrConstants
operator|.
name|NT_FOLDER
argument_list|,
name|session
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|AccessControlUtils
operator|.
name|denyAllToEveryone
argument_list|(
name|session
argument_list|,
literal|"/content/foo/en"
argument_list|)
expr_stmt|;
name|AccessControlUtils
operator|.
name|allow
argument_list|(
name|session
operator|.
name|getNode
argument_list|(
literal|"/content/foo/en"
argument_list|)
argument_list|,
literal|"bob"
argument_list|,
literal|"jcr:read"
argument_list|)
expr_stmt|;
name|AccessControlUtils
operator|.
name|denyAllToEveryone
argument_list|(
name|session
argument_list|,
literal|"/content/assets/foo"
argument_list|)
expr_stmt|;
name|AccessControlUtils
operator|.
name|allow
argument_list|(
name|session
operator|.
name|getNode
argument_list|(
literal|"/content/assets/foo"
argument_list|)
argument_list|,
literal|"alice"
argument_list|,
literal|"jcr:read"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|performSidegrade
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|RepositorySidegrade
name|sidegrade
init|=
operator|new
name|RepositorySidegrade
argument_list|(
name|sourceNodeStore
argument_list|,
name|targetNodeStore
argument_list|)
decl_stmt|;
name|sidegrade
operator|.
name|setIncludes
argument_list|(
literal|"/content/foo/en"
argument_list|,
literal|"/content/assets/foo"
argument_list|,
literal|"/content/other"
argument_list|)
expr_stmt|;
name|sidegrade
operator|.
name|setExcludes
argument_list|(
literal|"/content/assets/foo/2013"
argument_list|,
literal|"/content/assets/foo/2012"
argument_list|,
literal|"/content/assets/foo/2011"
argument_list|,
literal|"/content/assets/foo/2010"
argument_list|)
expr_stmt|;
name|sidegrade
operator|.
name|copy
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|RepositoryImpl
name|getRepository
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{
return|return
operator|(
name|RepositoryImpl
operator|)
operator|new
name|Jcr
argument_list|(
operator|new
name|Oak
argument_list|(
name|nodeStore
argument_list|)
operator|.
name|with
argument_list|(
name|SecurityProviderBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
operator|.
name|createRepository
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|void
name|withSession
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|SessionConsumer
name|sessionConsumer
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|RepositoryImpl
name|repository
init|=
name|getRepository
argument_list|(
name|nodeStore
argument_list|)
decl_stmt|;
name|Session
name|session
init|=
name|repository
operator|.
name|login
argument_list|(
name|CREDENTIALS
argument_list|)
decl_stmt|;
try|try
block|{
name|sessionConsumer
operator|.
name|accept
argument_list|(
operator|(
name|JackrabbitSession
operator|)
name|session
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
name|repository
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertExists
parameter_list|(
name|String
modifier|...
name|paths
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|assertExists
argument_list|(
name|targetSession
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertExists
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
modifier|...
name|paths
parameter_list|)
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|assertTrue
argument_list|(
literal|"node "
operator|+
name|path
operator|+
literal|" should exist"
argument_list|,
name|session
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertMissing
parameter_list|(
name|String
modifier|...
name|paths
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|assertMissing
argument_list|(
name|targetSession
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertMissing
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
modifier|...
name|paths
parameter_list|)
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|assertFalse
argument_list|(
literal|"node "
operator|+
name|path
operator|+
literal|" should not exist"
argument_list|,
name|session
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
interface|interface
name|SessionConsumer
block|{
name|void
name|accept
parameter_list|(
name|JackrabbitSession
name|session
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
block|}
block|}
end_class

end_unit

