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
operator|.
name|autosave
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
name|Iterator
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
name|Value
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
name|Impersonation
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
name|junit
operator|.
name|Test
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

begin_class
specifier|public
class|class
name|AutoSaveEnabledManagerTest
extends|extends
name|AbstractAutoSaveTest
block|{
annotation|@
name|Override
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|a
init|=
name|mgr
operator|.
name|getAuthorizable
argument_list|(
literal|"u"
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
name|a
operator|=
name|mgr
operator|.
name|getAuthorizable
argument_list|(
literal|"g"
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
condition|)
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
name|ConfigurationParameters
name|userConfig
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConstants
operator|.
name|PARAM_SUPPORT_AUTOSAVE
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
decl_stmt|;
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|,
name|userConfig
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAutoSaveEnabled
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|assertTrue
argument_list|(
name|mgr
operator|instanceof
name|AutoSaveEnabledManager
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mgr
operator|.
name|isAutoSave
argument_list|()
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|autoSave
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mgr
operator|.
name|isAutoSave
argument_list|()
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|autoSave
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAuthorizable
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Authorizable
name|a
init|=
name|mgr
operator|.
name|getAuthorizable
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_ANONYMOUS_ID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|instanceof
name|AuthorizableImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|instanceof
name|UserImpl
argument_list|)
expr_stmt|;
name|a
operator|=
name|mgr
operator|.
name|getAuthorizableByPath
argument_list|(
name|a
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|instanceof
name|AuthorizableImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|instanceof
name|UserImpl
argument_list|)
expr_stmt|;
name|a
operator|=
name|mgr
operator|.
name|getAuthorizable
argument_list|(
name|a
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|instanceof
name|AuthorizableImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|instanceof
name|UserImpl
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|mgr
operator|.
name|getAuthorizable
argument_list|(
literal|"unknown"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindAuthorizable
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|res
init|=
name|mgr
operator|.
name|findAuthorizables
argument_list|(
name|UserConstants
operator|.
name|REP_AUTHORIZABLE_ID
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_ANONYMOUS_ID
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|res
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Authorizable
name|a
init|=
name|res
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|instanceof
name|AuthorizableImpl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsAutoSave
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|mgr
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
name|RepositoryException
block|{
name|mgr
operator|.
name|autoSave
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|autoSave
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateRemoveUser
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|User
name|u
init|=
name|mgr
operator|.
name|createUser
argument_list|(
literal|"u"
argument_list|,
literal|"u"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|u
operator|.
name|remove
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateRemoveGroup
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Group
name|g
init|=
name|mgr
operator|.
name|createGroup
argument_list|(
literal|"g"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|g
operator|.
name|remove
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCommitFailedRevertChanges
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|User
name|u
init|=
name|mgr
operator|.
name|createUser
argument_list|(
literal|"u"
argument_list|,
literal|"u"
argument_list|)
decl_stmt|;
try|try
block|{
name|User
name|u2
init|=
name|mgr
operator|.
name|createUser
argument_list|(
literal|"u"
argument_list|,
literal|"u"
argument_list|)
decl_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// success
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthorizable
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|u
init|=
name|mgr
operator|.
name|createUser
argument_list|(
literal|"u"
argument_list|,
literal|"u"
argument_list|)
decl_stmt|;
name|u
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|u
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
operator|new
name|Value
index|[]
block|{
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|true
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|u
operator|.
name|removeProperty
argument_list|(
literal|"prop"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUser
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|u
init|=
name|mgr
operator|.
name|createUser
argument_list|(
literal|"u"
argument_list|,
literal|"u"
argument_list|)
decl_stmt|;
name|u
operator|.
name|disable
argument_list|(
literal|"disabled"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|u
operator|.
name|isDisabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|u
operator|.
name|disable
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|u
operator|.
name|isDisabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|u
operator|.
name|changePassword
argument_list|(
literal|"t"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|u
operator|.
name|changePassword
argument_list|(
literal|"tt"
argument_list|,
literal|"t"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImpersonation
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|u
init|=
name|mgr
operator|.
name|createUser
argument_list|(
literal|"u"
argument_list|,
literal|"u"
argument_list|)
decl_stmt|;
name|Impersonation
name|imp
init|=
name|u
operator|.
name|getImpersonation
argument_list|()
decl_stmt|;
name|Principal
name|p
init|=
name|mgr
operator|.
name|getAuthorizable
argument_list|(
literal|"anonymous"
argument_list|)
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|imp
operator|.
name|grantImpersonation
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|imp
operator|.
name|revokeImpersonation
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|u
init|=
name|mgr
operator|.
name|createUser
argument_list|(
literal|"u"
argument_list|,
literal|"u"
argument_list|)
decl_stmt|;
name|Group
name|g
init|=
name|mgr
operator|.
name|createGroup
argument_list|(
literal|"g"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|g
operator|.
name|addMember
argument_list|(
name|u
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|g
operator|.
name|isDeclaredMember
argument_list|(
name|u
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|it
init|=
name|g
operator|.
name|getDeclaredMembers
argument_list|()
decl_stmt|;
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Authorizable
name|a
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|a
operator|instanceof
name|AuthorizableImpl
argument_list|)
expr_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"blub"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|it
operator|=
name|g
operator|.
name|getMembers
argument_list|()
expr_stmt|;
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Authorizable
name|a
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|a
operator|instanceof
name|AuthorizableImpl
argument_list|)
expr_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"blub"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|g
operator|.
name|removeMember
argument_list|(
name|u
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|g
operator|.
name|isDeclaredMember
argument_list|(
name|u
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeclaredMemberOf
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|u
init|=
name|mgr
operator|.
name|createUser
argument_list|(
literal|"u"
argument_list|,
literal|"u"
argument_list|)
decl_stmt|;
name|Group
name|g
init|=
name|mgr
operator|.
name|createGroup
argument_list|(
literal|"g"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|g
operator|.
name|addMember
argument_list|(
name|u
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Group
argument_list|>
name|groups
init|=
name|u
operator|.
name|declaredMemberOf
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Group
name|gAgain
init|=
name|groups
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|gAgain
operator|instanceof
name|GroupImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|gAgain
operator|.
name|removeMember
argument_list|(
name|u
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|u
operator|.
name|declaredMemberOf
argument_list|()
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
name|testMemberOf
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|u
init|=
name|mgr
operator|.
name|createUser
argument_list|(
literal|"u"
argument_list|,
literal|"u"
argument_list|)
decl_stmt|;
name|Group
name|g
init|=
name|mgr
operator|.
name|createGroup
argument_list|(
literal|"g"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|g
operator|.
name|addMember
argument_list|(
name|u
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Group
argument_list|>
name|groups
init|=
name|u
operator|.
name|memberOf
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Group
name|gAgain
init|=
name|groups
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|gAgain
operator|instanceof
name|GroupImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|gAgain
operator|.
name|removeMember
argument_list|(
name|u
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|u
operator|.
name|declaredMemberOf
argument_list|()
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
name|testAddMembers
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|u
init|=
name|mgr
operator|.
name|createUser
argument_list|(
literal|"u"
argument_list|,
literal|"u"
argument_list|)
decl_stmt|;
name|Group
name|g
init|=
name|mgr
operator|.
name|createGroup
argument_list|(
literal|"g"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|g
operator|.
name|addMembers
argument_list|(
name|u
operator|.
name|getID
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveMembers
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|u
init|=
name|mgr
operator|.
name|createUser
argument_list|(
literal|"u"
argument_list|,
literal|"u"
argument_list|)
decl_stmt|;
name|Group
name|g
init|=
name|mgr
operator|.
name|createGroup
argument_list|(
literal|"g"
argument_list|)
decl_stmt|;
name|g
operator|.
name|addMember
argument_list|(
name|u
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|g
operator|.
name|removeMembers
argument_list|(
name|u
operator|.
name|getID
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

