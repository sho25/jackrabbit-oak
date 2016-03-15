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
name|benchmark
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|Session
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
name|Authorizable
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
name|Group
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
name|fixture
operator|.
name|JcrCreator
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
name|fixture
operator|.
name|OakRepositoryFixture
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
name|fixture
operator|.
name|RepositoryFixture
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
name|security
operator|.
name|SecurityProviderImpl
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
name|ConfigurationParameters
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
name|security
operator|.
name|principal
operator|.
name|PrincipalImpl
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
name|UserConfiguration
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
name|xml
operator|.
name|ImportBehavior
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
name|xml
operator|.
name|ProtectedItemImporter
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
name|util
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  * Test the performance of removing members from groups. The  * following parameters can be used to run the benchmark:  *  * - numberOfMembers : the number of members that should be added in the test setup  *   to each group (and removed during the test-run)  * - batchSize : the size of the memberID-array to be passed to the removeMembers call  *  * Note the members to be removed are picked randomly and may or may not/no longer  * be member of the target group.  */
end_comment

begin_class
specifier|public
class|class
name|RemoveMembersTest
extends|extends
name|AbstractTest
block|{
specifier|static
specifier|final
name|String
name|REL_TEST_PATH
init|=
literal|"testPath"
decl_stmt|;
specifier|static
specifier|final
name|String
name|USER
init|=
literal|"user"
decl_stmt|;
specifier|static
specifier|final
name|String
name|GROUP
init|=
literal|"group"
decl_stmt|;
specifier|static
specifier|final
name|int
name|GROUP_CNT
init|=
literal|100
decl_stmt|;
specifier|static
specifier|final
name|int
name|DEFAULT_BATCH_SIZE
init|=
literal|1
decl_stmt|;
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numberOfMembers
decl_stmt|;
specifier|final
name|int
name|batchSize
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|groupPaths
init|=
operator|new
name|ArrayList
argument_list|(
name|GROUP_CNT
argument_list|)
decl_stmt|;
specifier|public
name|RemoveMembersTest
parameter_list|(
name|int
name|numberOfMembers
parameter_list|,
name|int
name|batchSize
parameter_list|)
block|{
name|this
operator|.
name|numberOfMembers
operator|=
name|numberOfMembers
expr_stmt|;
name|this
operator|.
name|batchSize
operator|=
name|batchSize
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|beforeSuite
argument_list|()
expr_stmt|;
name|Session
name|s
init|=
name|loginAdministrative
argument_list|()
decl_stmt|;
try|try
block|{
name|UserManager
name|userManager
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|s
operator|)
operator|.
name|getUserManager
argument_list|()
decl_stmt|;
name|createUsers
argument_list|(
name|userManager
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|GROUP_CNT
condition|;
name|i
operator|++
control|)
block|{
name|Group
name|g
init|=
name|userManager
operator|.
name|createGroup
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|GROUP
operator|+
name|i
argument_list|)
argument_list|,
name|REL_TEST_PATH
argument_list|)
decl_stmt|;
name|groupPaths
operator|.
name|add
argument_list|(
name|g
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<=
name|numberOfMembers
condition|;
name|j
operator|++
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|USER
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
name|g
operator|.
name|addMembers
argument_list|(
name|ids
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|ids
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"setup done"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|createUsers
parameter_list|(
annotation|@
name|Nonnull
name|UserManager
name|userManager
parameter_list|)
throws|throws
name|Exception
block|{
comment|// nothing to do here as we add|remove members by ID in the setup and the test
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s
init|=
name|loginAdministrative
argument_list|()
decl_stmt|;
try|try
block|{
name|Authorizable
name|authorizable
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|s
operator|)
operator|.
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
name|GROUP
operator|+
literal|"0"
argument_list|)
decl_stmt|;
if|if
condition|(
name|authorizable
operator|!=
literal|null
condition|)
block|{
name|Node
name|n
init|=
name|s
operator|.
name|getNode
argument_list|(
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|n
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|// remove test-users if they have been created
name|authorizable
operator|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|s
operator|)
operator|.
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
name|USER
operator|+
literal|"0"
argument_list|)
expr_stmt|;
if|if
condition|(
name|authorizable
operator|!=
literal|null
condition|)
block|{
name|Node
name|n
init|=
name|s
operator|.
name|getNode
argument_list|(
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|n
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|Repository
index|[]
name|createRepository
parameter_list|(
name|RepositoryFixture
name|fixture
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|fixture
operator|instanceof
name|OakRepositoryFixture
condition|)
block|{
return|return
operator|(
operator|(
name|OakRepositoryFixture
operator|)
name|fixture
operator|)
operator|.
name|setUpCluster
argument_list|(
literal|1
argument_list|,
operator|new
name|JcrCreator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Jcr
name|customize
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
name|SecurityProvider
name|sp
init|=
operator|new
name|SecurityProviderImpl
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|ProtectedItemImporter
operator|.
name|PARAM_IMPORT_BEHAVIOR
argument_list|,
name|ImportBehavior
operator|.
name|NAME_BESTEFFORT
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
operator|.
name|with
argument_list|(
name|sp
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|createRepository
argument_list|(
name|fixture
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// use system session login to avoid measuring the login-performance here
name|s
operator|=
name|Subject
operator|.
name|doAsPrivileged
argument_list|(
name|SystemSubject
operator|.
name|INSTANCE
argument_list|,
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Session
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Session
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getRepository
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
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|UserManager
name|userManager
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|s
operator|)
operator|.
name|getUserManager
argument_list|()
decl_stmt|;
name|String
name|groupPath
init|=
name|groupPaths
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|GROUP_CNT
argument_list|)
argument_list|)
decl_stmt|;
name|Group
name|g
init|=
operator|(
name|Group
operator|)
name|userManager
operator|.
name|getAuthorizableByPath
argument_list|(
name|groupPath
argument_list|)
decl_stmt|;
name|removeMembers
argument_list|(
name|userManager
argument_list|,
name|g
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
if|if
condition|(
name|s
operator|.
name|hasPendingChanges
argument_list|()
condition|)
block|{
name|s
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|removeMembers
parameter_list|(
annotation|@
name|Nonnull
name|UserManager
name|userManger
parameter_list|,
annotation|@
name|Nonnull
name|Group
name|group
parameter_list|,
annotation|@
name|Nonnull
name|Session
name|s
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|numberOfMembers
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|batchSize
operator|<=
name|DEFAULT_BATCH_SIZE
condition|)
block|{
name|group
operator|.
name|removeMembers
argument_list|(
name|USER
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|numberOfMembers
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|batchSize
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|batchSize
condition|;
name|j
operator|++
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|USER
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|numberOfMembers
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|group
operator|.
name|removeMembers
argument_list|(
name|ids
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|ids
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

