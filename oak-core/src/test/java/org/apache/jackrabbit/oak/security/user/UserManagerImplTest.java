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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|commons
operator|.
name|PathUtils
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
name|commons
operator|.
name|UUIDUtils
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
name|namepath
operator|.
name|impl
operator|.
name|LocalNameMapper
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
name|impl
operator|.
name|NamePathMapperImpl
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
name|tree
operator|.
name|TreeUtil
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
name|value
operator|.
name|jcr
operator|.
name|PartialValueFactory
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
name|action
operator|.
name|AuthorizableActionProvider
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
name|GroupAction
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
name|PasswordUtil
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
name|Assert
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
name|Collections
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import static
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
operator|.
name|PARAM_AUTHORIZABLE_ACTION_PROVIDER
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
name|assertNotNull
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
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
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
name|getPartialValueFactory
argument_list|()
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
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
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
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-3191">OAK-3191</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testGetAuthorizableByEmptyId
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-3191">OAK-3191</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testGetTypedAuthorizableByEmptyId
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|""
argument_list|,
name|User
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-3191">OAK-3191</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testGetAuthorizableByNullId
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-3191">OAK-3191</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testGetTypedAuthorizableByNullId
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|null
argument_list|,
name|User
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-3191">OAK-3191</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testGetTypedAuthorizableByNullPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
operator|(
name|Principal
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAuthorizableByPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|authorizable
init|=
name|getTestUser
argument_list|()
decl_stmt|;
name|Authorizable
name|byPath
init|=
name|userMgr
operator|.
name|getAuthorizableByPath
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
argument_list|,
name|byPath
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testAuthorizableByUnresolvablePath
parameter_list|()
throws|throws
name|Exception
block|{
name|NamePathMapper
name|mapper
init|=
operator|new
name|NamePathMapperImpl
argument_list|(
operator|new
name|LocalNameMapper
argument_list|(
name|root
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"internal"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|UserManagerImpl
name|um
init|=
operator|new
name|UserManagerImpl
argument_list|(
name|root
argument_list|,
operator|new
name|PartialValueFactory
argument_list|(
name|mapper
argument_list|)
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
decl_stmt|;
name|um
operator|.
name|getAuthorizableByPath
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAuthorizableFromTree
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAuthorizableFromNullTree
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
operator|(
name|Tree
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAuthorizableFromNonExistingTree
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|t
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGtAuthorizableFromInvalidTree
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
name|testUserId
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
name|assertTrue
argument_list|(
name|PasswordUtil
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
name|testUserId
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
name|PasswordUtil
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
name|PasswordUtil
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
name|Exception
block|{
name|User
name|user
init|=
name|getTestUser
argument_list|()
decl_stmt|;
name|Tree
name|userNode
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
name|Tree
name|folder
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|userNode
argument_list|,
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
name|Tree
name|someContent
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|userNode
argument_list|,
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
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|someContent
argument_list|,
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
operator|.
name|exists
argument_list|()
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindWithNullValue2
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
literal|"./"
operator|+
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConcurrentCreateUser
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|Exception
argument_list|>
name|exceptions
init|=
operator|new
name|ArrayList
argument_list|<
name|Exception
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|workers
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|userId
init|=
literal|"foo-user-"
operator|+
name|i
decl_stmt|;
name|workers
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|ContentSession
name|admin
init|=
name|login
argument_list|(
name|getAdminCredentials
argument_list|()
argument_list|)
decl_stmt|;
name|Root
name|root
init|=
name|admin
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|UserManager
name|userManager
init|=
operator|new
name|UserManagerImpl
argument_list|(
name|root
argument_list|,
name|getPartialValueFactory
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
decl_stmt|;
name|userManager
operator|.
name|createUser
argument_list|(
name|userId
argument_list|,
literal|"pass"
argument_list|,
operator|new
name|PrincipalImpl
argument_list|(
name|userId
argument_list|)
argument_list|,
literal|"relPath"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|admin
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|workers
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|workers
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Exception
name|e
range|:
name|exceptions
control|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|exceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
name|exceptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
throw|;
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
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
operator|+
literal|"/relPath"
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|exists
argument_list|()
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
comment|/**      * Test related to OAK-1922: Asserting that the default behavior is such that      * no rep:pwd node is created upon user-creation.      *      * @since Oak 1.1      */
annotation|@
name|Test
specifier|public
name|void
name|testNewUserHasNoPwdNode
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|newUserId
init|=
literal|"newuser"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|User
name|user
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createUser
argument_list|(
name|newUserId
argument_list|,
name|newUserId
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
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
operator|.
name|hasChild
argument_list|(
name|UserConstants
operator|.
name|REP_PWD
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|user
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PWD
operator|+
literal|"/"
operator|+
name|UserConstants
operator|.
name|REP_PASSWORD_LAST_MODIFIED
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testCreateUserWithEmptyId
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|userMgr
operator|.
name|createUser
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testCreateUserWithNullId
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|userMgr
operator|.
name|createUser
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|PrincipalImpl
argument_list|(
literal|"userPrincipalName"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testCreateSystemUserWithEmptyId
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|userMgr
operator|.
name|createSystemUser
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testCreateSystemUserWithNullId
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|userMgr
operator|.
name|createSystemUser
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testCreateGroupWithEmptyId
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|userMgr
operator|.
name|createGroup
argument_list|(
literal|""
argument_list|,
operator|new
name|PrincipalImpl
argument_list|(
literal|"groupPrincipalName"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testCreateGroupWithNullId
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|userMgr
operator|.
name|createGroup
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|,
operator|new
name|PrincipalImpl
argument_list|(
literal|"groupPrincipalName"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testCreateUserWithEmptyPrincipalName
parameter_list|()
throws|throws
name|Exception
block|{
name|userMgr
operator|.
name|createUser
argument_list|(
literal|"another"
argument_list|,
literal|null
argument_list|,
operator|new
name|Principal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testCreateGroupWithNullPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|userMgr
operator|.
name|createGroup
argument_list|(
literal|"another"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AuthorizableExistsException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testCreateUserWithExistingPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|u
init|=
name|getTestUser
argument_list|()
decl_stmt|;
name|userMgr
operator|.
name|createUser
argument_list|(
literal|"another"
argument_list|,
literal|null
argument_list|,
name|u
operator|.
name|getPrincipal
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AuthorizableExistsException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testCreateGroupWithExistingPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|u
init|=
name|getTestUser
argument_list|()
decl_stmt|;
name|userMgr
operator|.
name|createGroup
argument_list|(
name|u
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOnMembersAddedByContentId
parameter_list|()
throws|throws
name|Exception
block|{
name|GroupAction
name|groupAction
init|=
name|mock
argument_list|(
name|GroupAction
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
name|actions
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|groupAction
argument_list|)
decl_stmt|;
name|AuthorizableActionProvider
name|actionProvider
init|=
name|mock
argument_list|(
name|AuthorizableActionProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|actionProvider
operator|.
name|getAuthorizableActions
argument_list|(
name|any
argument_list|(
name|SecurityProvider
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|actions
argument_list|)
expr_stmt|;
name|ConfigurationParameters
name|params
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|PARAM_AUTHORIZABLE_ACTION_PROVIDER
argument_list|,
name|actionProvider
argument_list|)
decl_stmt|;
name|UserConfiguration
name|uc
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getParameters
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|params
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|SecurityProvider
name|sp
init|=
name|mock
argument_list|(
name|SecurityProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|uc
argument_list|)
expr_stmt|;
name|UserManagerImpl
name|um
init|=
operator|new
name|UserManagerImpl
argument_list|(
name|root
argument_list|,
operator|new
name|PartialValueFactory
argument_list|(
name|getNamePathMapper
argument_list|()
argument_list|)
argument_list|,
name|sp
argument_list|)
decl_stmt|;
name|Group
name|testGroup
init|=
name|mock
argument_list|(
name|Group
operator|.
name|class
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|membersIds
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|UUIDUtils
operator|.
name|generateUUID
argument_list|()
argument_list|)
decl_stmt|;
name|um
operator|.
name|onGroupUpdate
argument_list|(
name|testGroup
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|membersIds
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|groupAction
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|onMembersAddedContentId
argument_list|(
name|testGroup
argument_list|,
name|membersIds
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|,
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

