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
name|authorization
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|javax
operator|.
name|jcr
operator|.
name|AccessDeniedException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NodeIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlEntry
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|Privilege
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
name|Lists
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
name|JackrabbitAccessControlList
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
name|spi
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
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
name|util
operator|.
name|Text
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

begin_comment
comment|/**  * Testing permission evaluation for user management operations.  *  * @since OAK 1.0 As of OAK user mgt related operations require a specific  * user management permission (unless the system in configured to behave like  * jackrabbit 2x).  */
end_comment

begin_class
specifier|public
class|class
name|UserManagementTest
extends|extends
name|AbstractEvaluationTest
block|{
specifier|private
specifier|final
name|String
name|userId
init|=
literal|"testUser2"
decl_stmt|;
specifier|private
specifier|final
name|String
name|groupId
init|=
literal|"testGroup2"
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|authorizablesToRemove
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|userId
argument_list|,
name|groupId
argument_list|)
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|testSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|UserManager
name|userMgr
init|=
name|getUserManager
argument_list|(
name|superuser
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|authorizablesToRemove
control|)
block|{
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|id
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
block|}
block|}
name|JackrabbitAccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|acl
operator|!=
literal|null
condition|)
block|{
name|boolean
name|modified
init|=
literal|false
decl_stmt|;
for|for
control|(
name|AccessControlEntry
name|entry
range|:
name|acl
operator|.
name|getAccessControlEntries
argument_list|()
control|)
block|{
if|if
condition|(
name|testUser
operator|.
name|getPrincipal
argument_list|()
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getPrincipal
argument_list|()
argument_list|)
condition|)
block|{
name|acl
operator|.
name|removeAccessControlEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|modified
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|modified
condition|)
block|{
name|acMgr
operator|.
name|setPolicy
argument_list|(
literal|"/"
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
block|}
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|createUser
parameter_list|(
name|String
name|userId
parameter_list|)
throws|throws
name|Exception
block|{
name|getUserManager
argument_list|(
name|superuser
argument_list|)
operator|.
name|createUser
argument_list|(
name|userId
argument_list|,
literal|"pw"
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|testSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateUserWithoutPermission
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
comment|// testSession has read-only access
try|try
block|{
name|testUserMgr
operator|.
name|createUser
argument_list|(
name|userId
argument_list|,
literal|"pw"
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Test session doesn't have sufficient permission -> creating user should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
comment|// testSession has write permission but no user-mgt permission
comment|// -> should still fail
name|modify
argument_list|(
literal|"/"
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_WRITE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|testUserMgr
operator|.
name|createUser
argument_list|(
name|userId
argument_list|,
literal|"pw"
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Test session doesn't have sufficient permission -> creating user should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
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
name|testCreateUser
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|modify
argument_list|(
literal|"/"
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_USER_MANAGEMENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// creating user should succeed
name|testUserMgr
operator|.
name|createUser
argument_list|(
name|userId
argument_list|,
literal|"pw"
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateUser2
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|Privilege
index|[]
name|privs
init|=
name|privilegesFromNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|PrivilegeConstants
operator|.
name|REP_USER_MANAGEMENT
block|,
name|PrivilegeConstants
operator|.
name|REP_WRITE
block|}
argument_list|)
decl_stmt|;
name|allow
argument_list|(
literal|"/"
argument_list|,
name|privs
argument_list|)
expr_stmt|;
comment|// creating user should succeed
name|testUserMgr
operator|.
name|createUser
argument_list|(
name|userId
argument_list|,
literal|"pw"
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|modify
argument_list|(
literal|"/"
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_USER_MANAGEMENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// creating group should succeed
name|Group
name|gr
init|=
name|testUserMgr
operator|.
name|createGroup
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateGroup2
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|Privilege
index|[]
name|privs
init|=
name|privilegesFromNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|PrivilegeConstants
operator|.
name|REP_USER_MANAGEMENT
block|,
name|PrivilegeConstants
operator|.
name|REP_WRITE
block|}
argument_list|)
decl_stmt|;
name|allow
argument_list|(
literal|"/"
argument_list|,
name|privs
argument_list|)
expr_stmt|;
comment|// creating group should succeed
name|Group
name|gr
init|=
name|testUserMgr
operator|.
name|createGroup
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChangePasswordWithoutPermission
parameter_list|()
throws|throws
name|Exception
block|{
name|createUser
argument_list|(
name|userId
argument_list|)
expr_stmt|;
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|User
name|user
init|=
operator|(
name|User
operator|)
name|testUserMgr
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
try|try
block|{
name|user
operator|.
name|changePassword
argument_list|(
literal|"pw2"
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
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
name|testChangePasswordWithoutPermission2
parameter_list|()
throws|throws
name|Exception
block|{
name|createUser
argument_list|(
name|userId
argument_list|)
expr_stmt|;
name|modify
argument_list|(
literal|"/"
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_WRITE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|User
name|user
init|=
operator|(
name|User
operator|)
name|testUserMgr
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
try|try
block|{
name|user
operator|.
name|changePassword
argument_list|(
literal|"pw2"
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
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
name|testChangePassword
parameter_list|()
throws|throws
name|Exception
block|{
name|createUser
argument_list|(
name|userId
argument_list|)
expr_stmt|;
comment|// after granting user-mgt privilege changing the pw must succeed.
name|modify
argument_list|(
literal|"/"
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_USER_MANAGEMENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|User
name|user
init|=
operator|(
name|User
operator|)
name|testUserMgr
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|user
operator|.
name|changePassword
argument_list|(
literal|"pw2"
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDisableUserWithoutPermission
parameter_list|()
throws|throws
name|Exception
block|{
name|createUser
argument_list|(
name|userId
argument_list|)
expr_stmt|;
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|User
name|user
init|=
operator|(
name|User
operator|)
name|testUserMgr
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
try|try
block|{
name|user
operator|.
name|disable
argument_list|(
literal|"disabled!"
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
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
name|testDisableUserWithoutPermission2
parameter_list|()
throws|throws
name|Exception
block|{
name|createUser
argument_list|(
name|userId
argument_list|)
expr_stmt|;
name|modify
argument_list|(
literal|"/"
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_WRITE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|User
name|user
init|=
operator|(
name|User
operator|)
name|testUserMgr
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
try|try
block|{
name|user
operator|.
name|disable
argument_list|(
literal|"disabled!"
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
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
name|testDisableUser
parameter_list|()
throws|throws
name|Exception
block|{
name|createUser
argument_list|(
name|userId
argument_list|)
expr_stmt|;
comment|// after granting user-mgt privilege changing the pw must succeed.
name|modify
argument_list|(
literal|"/"
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_USER_MANAGEMENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|User
name|user
init|=
operator|(
name|User
operator|)
name|testUserMgr
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|user
operator|.
name|disable
argument_list|(
literal|"disabled!"
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveUserWithoutPermission
parameter_list|()
throws|throws
name|Exception
block|{
name|createUser
argument_list|(
name|userId
argument_list|)
expr_stmt|;
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
comment|// testSession has read-only access
try|try
block|{
name|Authorizable
name|a
init|=
name|testUserMgr
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|a
operator|.
name|remove
argument_list|()
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Test session doesn't have sufficient permission to remove a user."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
comment|// testSession has write permission but no user-mgt permission
comment|// -> should still fail
name|modify
argument_list|(
literal|"/"
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_WRITE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|Authorizable
name|a
init|=
name|testUserMgr
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|a
operator|.
name|remove
argument_list|()
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Test session doesn't have sufficient permission to remove a user."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
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
name|testRemoveUser
parameter_list|()
throws|throws
name|Exception
block|{
name|createUser
argument_list|(
name|userId
argument_list|)
expr_stmt|;
comment|// testSession has user-mgt permission -> removal should succeed.
name|modify
argument_list|(
literal|"/"
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_USER_MANAGEMENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|Authorizable
name|a
init|=
name|testUserMgr
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|a
operator|.
name|remove
argument_list|()
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveUser2
parameter_list|()
throws|throws
name|Exception
block|{
name|createUser
argument_list|(
name|userId
argument_list|)
expr_stmt|;
comment|// testSession has user-mgt permission -> removal should succeed.
name|Privilege
index|[]
name|privs
init|=
name|privilegesFromNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|PrivilegeConstants
operator|.
name|REP_USER_MANAGEMENT
block|,
name|PrivilegeConstants
operator|.
name|REP_WRITE
block|}
argument_list|)
decl_stmt|;
name|allow
argument_list|(
literal|"/"
argument_list|,
name|privs
argument_list|)
expr_stmt|;
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|Authorizable
name|a
init|=
name|testUserMgr
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|a
operator|.
name|remove
argument_list|()
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChangeUserPropertiesWithoutPermission
parameter_list|()
throws|throws
name|Exception
block|{
name|createUser
argument_list|(
name|userId
argument_list|)
expr_stmt|;
comment|// testSession has read-only access
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
try|try
block|{
name|Authorizable
name|a
init|=
name|testUserMgr
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|"someProp"
argument_list|,
name|testSession
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Test session doesn't have sufficient permission to alter user properties."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
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
name|testChangeUserPropertiesWithoutPermission2
parameter_list|()
throws|throws
name|Exception
block|{
name|createUser
argument_list|(
name|userId
argument_list|)
expr_stmt|;
comment|// testSession has read and user-mgt permission but lacks permission to
comment|// alter regular properties
name|modify
argument_list|(
literal|"/"
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_USER_MANAGEMENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
try|try
block|{
name|Authorizable
name|a
init|=
name|testUserMgr
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|"someProp"
argument_list|,
name|testSession
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Test session doesn't have sufficient permission to alter user properties."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
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
name|testChangeUserProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|createUser
argument_list|(
name|userId
argument_list|)
expr_stmt|;
comment|// make sure user can create/modify/remove regular properties
name|modify
argument_list|(
literal|"/"
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_MODIFY_PROPERTIES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|Authorizable
name|a
init|=
name|testUserMgr
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|"someProp"
argument_list|,
name|testSession
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|"someProperty"
argument_list|,
name|testSession
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"modified"
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|a
operator|.
name|removeProperty
argument_list|(
literal|"someProperty"
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/JCR-3412">JCR-3412 :      * UserManager.findAuthorizables() does not work, if session does not have      * read access to common root of all user and groups.</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testFindAuthorizables
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|home
init|=
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|deny
argument_list|(
name|home
argument_list|,
name|privilegesFromName
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|getUserManager
argument_list|(
name|superuser
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|testSession
operator|.
name|getUserID
argument_list|()
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|,
name|privilegesFromName
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
argument_list|)
expr_stmt|;
name|UserManager
name|testUserMgr
init|=
name|getUserManager
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|result
init|=
name|testUserMgr
operator|.
name|findAuthorizables
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
literal|null
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_USER
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|result
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|result
operator|.
name|next
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|ids
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|NodeIterator
name|nodeIterator
init|=
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getQueryManager
argument_list|()
operator|.
name|createQuery
argument_list|(
literal|"/jcr:root//element(*,rep:User)"
argument_list|,
name|Query
operator|.
name|XPATH
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|getNodes
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|nodeIterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|nodeIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|userId
init|=
name|nodeIterator
operator|.
name|nextNode
argument_list|()
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_AUTHORIZABLE_ID
argument_list|)
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|ids
operator|.
name|remove
argument_list|(
name|userId
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"UserId "
operator|+
name|userId
operator|+
literal|" missing in result set."
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Result mismatch"
argument_list|,
name|ids
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

