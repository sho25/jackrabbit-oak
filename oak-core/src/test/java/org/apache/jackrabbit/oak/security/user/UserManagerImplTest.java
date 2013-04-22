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
name|Iterator
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
name|UnsupportedRepositoryOperationException
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
name|AbstractSecurityTest
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
name|CommitFailedException
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
name|Tree
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
name|Type
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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|user
operator|.
name|util
operator|.
name|PasswordUtility
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
name|util
operator|.
name|NodeUtil
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
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
name|assertNull
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
name|fail
import|;
end_import

begin_comment
comment|/**  * @since OAK 1.0  */
end_comment

begin_class
specifier|public
class|class
name|UserManagerImplTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|UserManagerImpl
name|userMgr
decl_stmt|;
specifier|private
name|String
name|testUserId
init|=
literal|"testUser"
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|userMgr
operator|=
operator|new
name|UserManagerImpl
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Authorizable
name|testUser
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|testUserId
argument_list|)
decl_stmt|;
if|if
condition|(
name|testUser
operator|!=
literal|null
condition|)
block|{
name|testUser
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetPassword
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|user
init|=
name|userMgr
operator|.
name|createUser
argument_list|(
name|testUserId
argument_list|,
literal|"pw"
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|pwds
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|pwds
operator|.
name|add
argument_list|(
literal|"pw"
argument_list|)
expr_stmt|;
name|pwds
operator|.
name|add
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|pwds
operator|.
name|add
argument_list|(
literal|"{sha1}pw"
argument_list|)
expr_stmt|;
name|Tree
name|userTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|pw
range|:
name|pwds
control|)
block|{
name|userMgr
operator|.
name|setPassword
argument_list|(
name|userTree
argument_list|,
name|pw
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|pwHash
init|=
name|userTree
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|pwHash
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PasswordUtility
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|pw
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|pw
range|:
name|pwds
control|)
block|{
name|userMgr
operator|.
name|setPassword
argument_list|(
name|userTree
argument_list|,
name|pw
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|pwHash
init|=
name|userTree
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|pwHash
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|pw
operator|.
name|startsWith
argument_list|(
literal|"{"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|PasswordUtility
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|pw
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|PasswordUtility
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|pw
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pw
argument_list|,
name|pwHash
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|setPasswordNull
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|user
init|=
name|userMgr
operator|.
name|createUser
argument_list|(
name|testUserId
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Tree
name|userTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|userMgr
operator|.
name|setPassword
argument_list|(
name|userTree
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"setting null password should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|userMgr
operator|.
name|setPassword
argument_list|(
name|userTree
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"setting null password should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPasswordHash
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|user
init|=
name|userMgr
operator|.
name|createUser
argument_list|(
name|testUserId
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Tree
name|userTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|userTree
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsAutoSave
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|userMgr
operator|.
name|isAutoSave
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAutoSave
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|userMgr
operator|.
name|autoSave
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedRepositoryOperationException
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
name|testEnforceAuthorizableFolderHierarchy
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|CommitFailedException
block|{
name|User
name|user
init|=
name|userMgr
operator|.
name|createUser
argument_list|(
name|testUserId
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|NodeUtil
name|userNode
init|=
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|NodeUtil
name|folder
init|=
name|userNode
operator|.
name|addChild
argument_list|(
literal|"folder"
argument_list|,
name|UserConstants
operator|.
name|NT_REP_AUTHORIZABLE_FOLDER
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|folder
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|// authNode - authFolder -> create User
try|try
block|{
name|Principal
name|p
init|=
operator|new
name|PrincipalImpl
argument_list|(
literal|"test2"
argument_list|)
decl_stmt|;
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|createUser
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|p
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Users may not be nested."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"test2"
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
condition|)
block|{
name|a
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|NodeUtil
name|someContent
init|=
name|userNode
operator|.
name|addChild
argument_list|(
literal|"mystuff"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|path
operator|=
name|someContent
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
expr_stmt|;
try|try
block|{
comment|// authNode - anyNode -> create User
try|try
block|{
name|Principal
name|p
init|=
operator|new
name|PrincipalImpl
argument_list|(
literal|"test3"
argument_list|)
decl_stmt|;
name|userMgr
operator|.
name|createUser
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|p
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Users may not be nested."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"test3"
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
condition|)
block|{
name|a
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
comment|// authNode - anyNode - authFolder -> create User
name|folder
operator|=
name|someContent
operator|.
name|addChild
argument_list|(
literal|"folder"
argument_list|,
name|UserConstants
operator|.
name|NT_REP_AUTHORIZABLE_FOLDER
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// this time save node structure
try|try
block|{
name|Principal
name|p
init|=
operator|new
name|PrincipalImpl
argument_list|(
literal|"test4"
argument_list|)
decl_stmt|;
name|userMgr
operator|.
name|createUser
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|p
argument_list|,
name|folder
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Users may not be nested."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"test4"
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
condition|)
block|{
name|a
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|t
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindWithNullValue
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|result
init|=
name|userMgr
operator|.
name|findAuthorizables
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|userMgr
operator|.
name|findAuthorizables
argument_list|(
literal|"./"
operator|+
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

