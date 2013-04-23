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
name|jcr
operator|.
name|security
operator|.
name|user
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
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
name|nodetype
operator|.
name|ConstraintViolationException
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
name|AuthorizableExistsException
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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|user
operator|.
name|UserConstants
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
name|test
operator|.
name|NotExecutableException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
comment|/**  * Tests for {@code User} creation.  */
end_comment

begin_class
specifier|public
class|class
name|CreateUserTest
extends|extends
name|AbstractUserTest
block|{
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CreateUserTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Authorizable
argument_list|>
name|createdUsers
init|=
operator|new
name|ArrayList
argument_list|<
name|Authorizable
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|After
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|superuser
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// remove all created groups again
for|for
control|(
name|Object
name|createdUser
range|:
name|createdUsers
control|)
block|{
name|Authorizable
name|auth
init|=
operator|(
name|Authorizable
operator|)
name|createdUser
decl_stmt|;
try|try
block|{
name|auth
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to remove User "
operator|+
name|auth
operator|.
name|getID
argument_list|()
operator|+
literal|" during tearDown."
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|private
name|User
name|createUser
parameter_list|(
name|String
name|uid
parameter_list|,
name|String
name|pw
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|User
name|u
init|=
name|userMgr
operator|.
name|createUser
argument_list|(
name|uid
argument_list|,
name|pw
argument_list|)
decl_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
name|u
return|;
block|}
specifier|private
name|User
name|createUser
parameter_list|(
name|String
name|uid
parameter_list|,
name|String
name|pw
parameter_list|,
name|Principal
name|p
parameter_list|,
name|String
name|iPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|User
name|u
init|=
name|userMgr
operator|.
name|createUser
argument_list|(
name|uid
argument_list|,
name|pw
argument_list|,
name|p
argument_list|,
name|iPath
argument_list|)
decl_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
name|u
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateUser
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|String
name|uid
init|=
name|p
operator|.
name|getName
argument_list|()
decl_stmt|;
name|User
name|user
init|=
name|createUser
argument_list|(
name|uid
argument_list|,
literal|"pw"
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|user
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * @since OAK 1.0 In contrast to Jackrabbit core the intermediate path may      * not be an absolute path in OAK.      */
annotation|@
name|Test
specifier|public
name|void
name|testCreateUserWithAbsolutePath
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|String
name|uid
init|=
name|p
operator|.
name|getName
argument_list|()
decl_stmt|;
try|try
block|{
name|User
name|user
init|=
name|createUser
argument_list|(
name|uid
argument_list|,
literal|"pw"
argument_list|,
name|p
argument_list|,
literal|"/any/path/to/the/new/user"
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ConstraintViolationException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateGroupWithAbsolutePath2
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|String
name|uid
init|=
name|p
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|userRoot
init|=
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
decl_stmt|;
name|String
name|path
init|=
name|userRoot
operator|+
literal|"/any/path/to/the/new/user"
decl_stmt|;
name|User
name|user
init|=
name|createUser
argument_list|(
name|uid
argument_list|,
literal|"pw"
argument_list|,
name|p
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateUserWithRelativePath
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|String
name|uid
init|=
name|p
operator|.
name|getName
argument_list|()
decl_stmt|;
name|User
name|user
init|=
name|createUser
argument_list|(
name|uid
argument_list|,
literal|"pw"
argument_list|,
name|p
argument_list|,
literal|"any/path"
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
operator|.
name|contains
argument_list|(
literal|"any/path"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateUserWithDifferentPrincipalName
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|String
name|uid
init|=
name|getTestPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|User
name|user
init|=
name|createUser
argument_list|(
name|uid
argument_list|,
literal|"pw"
argument_list|,
name|p
argument_list|,
literal|"any/path"
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|user
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateUserWithNullParamerters
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|User
name|user
init|=
name|createUser
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A User cannot be built from 'null' parameters"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ok
block|}
try|try
block|{
name|User
name|user
init|=
name|createUser
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A User cannot be built from 'null' parameters"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ok
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateUserWithNullUserID
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|User
name|user
init|=
name|createUser
argument_list|(
literal|null
argument_list|,
literal|"anyPW"
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A User cannot be built with 'null' userID"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ok
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateUserWithEmptyUserID
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|User
name|user
init|=
name|createUser
argument_list|(
literal|""
argument_list|,
literal|"anyPW"
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A User cannot be built with \"\" userID"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ok
block|}
try|try
block|{
name|User
name|user
init|=
name|createUser
argument_list|(
literal|""
argument_list|,
literal|"anyPW"
argument_list|,
name|getTestPrincipal
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A User cannot be built with \"\" userID"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ok
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateUserWithEmptyPassword
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|User
name|user
init|=
name|createUser
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateUserWithNullPrincipal
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|String
name|uid
init|=
name|p
operator|.
name|getName
argument_list|()
decl_stmt|;
name|User
name|user
init|=
name|createUser
argument_list|(
name|uid
argument_list|,
literal|"pw"
argument_list|,
literal|null
argument_list|,
literal|"/a/b/c"
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A User cannot be built with 'null' Principal"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ok
block|}
block|}
specifier|public
name|void
name|testCreateUserWithEmptyPrincipal
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|String
name|uid
init|=
name|p
operator|.
name|getName
argument_list|()
decl_stmt|;
name|User
name|user
init|=
name|createUser
argument_list|(
name|uid
argument_list|,
literal|"pw"
argument_list|,
name|p
argument_list|,
literal|"/a/b/c"
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A User cannot be built with ''-named Principal"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ok
block|}
try|try
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|String
name|uid
init|=
name|p
operator|.
name|getName
argument_list|()
decl_stmt|;
name|User
name|user
init|=
name|createUser
argument_list|(
name|uid
argument_list|,
literal|"pw"
argument_list|,
name|p
argument_list|,
literal|"/a/b/c"
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A User cannot be built with ''-named Principal"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ok
block|}
block|}
specifier|public
name|void
name|testCreateTwiceWithSameUserID
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|String
name|uid
init|=
name|getTestPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|User
name|user
init|=
name|createUser
argument_list|(
name|uid
argument_list|,
literal|"pw"
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
try|try
block|{
name|User
name|user2
init|=
name|createUser
argument_list|(
name|uid
argument_list|,
literal|"anyPW"
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Creating 2 users with the same UserID should throw AuthorizableExistsException."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthorizableExistsException
name|e
parameter_list|)
block|{
comment|// success.
block|}
block|}
comment|/**      * @since OAK 1.0 : RepositoryException is thrown instead of AuthorizableExistsException      */
specifier|public
name|void
name|testCreateTwiceWithSamePrincipal
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|String
name|uid
init|=
name|p
operator|.
name|getName
argument_list|()
decl_stmt|;
name|User
name|user
init|=
name|createUser
argument_list|(
name|uid
argument_list|,
literal|"pw"
argument_list|,
name|p
argument_list|,
literal|"a/b/c"
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
try|try
block|{
name|uid
operator|=
name|getTestPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
name|User
name|user2
init|=
name|createUser
argument_list|(
name|uid
argument_list|,
literal|"pw"
argument_list|,
name|p
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Creating 2 users with the same Principal should throw AuthorizableExistsException."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// success.
block|}
block|}
specifier|public
name|void
name|testGetUserAfterCreation
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|String
name|uid
init|=
name|p
operator|.
name|getName
argument_list|()
decl_stmt|;
name|User
name|user
init|=
name|createUser
argument_list|(
name|uid
argument_list|,
literal|"pw"
argument_list|)
decl_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

