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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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
name|UserIdCredentials
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|javax
operator|.
name|jcr
operator|.
name|Property
operator|.
name|JCR_PRIMARY_TYPE
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
name|NT_REP_GROUP
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
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|UserImplTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|User
name|user
decl_stmt|;
annotation|@
name|Override
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
name|user
operator|=
name|getTestUser
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|NotNull
specifier|private
name|User
name|getAdminUser
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|admin
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_ADMIN_ID
argument_list|,
name|User
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|admin
argument_list|)
expr_stmt|;
return|return
name|admin
return|;
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
name|testCreateFromInvalidTree
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
name|getProperty
argument_list|(
name|JCR_PRIMARY_TYPE
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|JCR_PRIMARY_TYPE
argument_list|,
name|NT_REP_GROUP
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|User
name|u
init|=
operator|new
name|UserImpl
argument_list|(
literal|"uid"
argument_list|,
name|t
argument_list|,
operator|(
name|UserManagerImpl
operator|)
name|getUserManager
argument_list|(
name|root
argument_list|)
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsAdmin
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|user
operator|.
name|isAdmin
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAdministratorIsAdmin
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|getAdminUser
argument_list|()
operator|.
name|isAdmin
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsSystemUser
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|user
operator|.
name|isSystemUser
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGroup
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|user
operator|.
name|isGroup
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|id
init|=
name|user
operator|.
name|getID
argument_list|()
decl_stmt|;
name|user
operator|.
name|remove
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|,
name|User
operator|.
name|class
argument_list|)
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
name|testRemoveAdmin
parameter_list|()
throws|throws
name|Exception
block|{
name|getAdminUser
argument_list|()
operator|.
name|remove
argument_list|()
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
name|testChangePasswordToNull
parameter_list|()
throws|throws
name|Exception
block|{
name|user
operator|.
name|changePassword
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChangePassword
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|pwHash
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
name|assertTrue
argument_list|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|user
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|user
operator|.
name|changePassword
argument_list|(
literal|"different"
argument_list|)
expr_stmt|;
name|String
name|pwHash2
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
name|assertTrue
argument_list|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|pwHash2
argument_list|,
literal|"different"
argument_list|)
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
name|testChangePasswordWithOldMismatch
parameter_list|()
throws|throws
name|Exception
block|{
name|user
operator|.
name|changePassword
argument_list|(
literal|"different"
argument_list|,
literal|"wrongOldPassword"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChangePasswordWithOld
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|pwHash
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
name|assertTrue
argument_list|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|user
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|user
operator|.
name|changePassword
argument_list|(
literal|"different"
argument_list|,
name|user
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|pwHash2
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
name|assertTrue
argument_list|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|pwHash2
argument_list|,
literal|"different"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDisable
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|user
operator|.
name|getDisabledReason
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|user
operator|.
name|isDisabled
argument_list|()
argument_list|)
expr_stmt|;
name|user
operator|.
name|disable
argument_list|(
literal|"reason"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"reason"
argument_list|,
name|user
operator|.
name|getDisabledReason
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|user
operator|.
name|isDisabled
argument_list|()
argument_list|)
expr_stmt|;
name|user
operator|.
name|disable
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|user
operator|.
name|getDisabledReason
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|user
operator|.
name|isDisabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDisableNullReason
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|user
operator|.
name|getDisabledReason
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|user
operator|.
name|isDisabled
argument_list|()
argument_list|)
expr_stmt|;
name|user
operator|.
name|disable
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|user
operator|.
name|getDisabledReason
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|user
operator|.
name|isDisabled
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
name|testDisableAdministrator
parameter_list|()
throws|throws
name|Exception
block|{
name|getAdminUser
argument_list|()
operator|.
name|disable
argument_list|(
literal|"reason"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetCredentials
parameter_list|()
throws|throws
name|Exception
block|{
name|Credentials
name|creds
init|=
name|user
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|creds
operator|instanceof
name|CredentialsImpl
argument_list|)
expr_stmt|;
name|CredentialsImpl
name|cImpl
init|=
operator|(
name|CredentialsImpl
operator|)
name|creds
decl_stmt|;
name|assertEquals
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|,
name|cImpl
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|cImpl
operator|.
name|getPasswordHash
argument_list|()
argument_list|,
name|user
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetCredentialsUserWithoutPassword
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|u
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createUser
argument_list|(
literal|"u"
operator|+
name|UUIDUtils
operator|.
name|generateUUID
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Credentials
name|creds
init|=
name|u
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|creds
operator|instanceof
name|UserIdCredentials
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|u
operator|.
name|getID
argument_list|()
argument_list|,
operator|(
operator|(
name|UserIdCredentials
operator|)
name|creds
operator|)
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

