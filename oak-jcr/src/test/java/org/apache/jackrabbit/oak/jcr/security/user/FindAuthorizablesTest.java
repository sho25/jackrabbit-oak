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
name|principal
operator|.
name|PrincipalIterator
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
name|principal
operator|.
name|PrincipalManager
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
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests for the query API exposed by {@link UserManager}.  */
end_comment

begin_class
specifier|public
class|class
name|FindAuthorizablesTest
extends|extends
name|AbstractUserTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testFindAuthorizable
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|new
name|HashSet
argument_list|<
name|Principal
argument_list|>
argument_list|()
decl_stmt|;
name|PrincipalManager
name|pMgr
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|superuser
operator|)
operator|.
name|getPrincipalManager
argument_list|()
decl_stmt|;
name|Principal
name|p
init|=
name|pMgr
operator|.
name|getPrincipal
argument_list|(
name|superuser
operator|.
name|getUserID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|principals
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|PrincipalIterator
name|principalIterator
init|=
name|pMgr
operator|.
name|getGroupMembership
argument_list|(
name|p
argument_list|)
decl_stmt|;
while|while
condition|(
name|principalIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|principals
operator|.
name|add
argument_list|(
name|principalIterator
operator|.
name|nextPrincipal
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Authorizable
name|auth
decl_stmt|;
for|for
control|(
name|Principal
name|principal
range|:
name|principals
control|)
block|{
name|auth
operator|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|principal
argument_list|)
expr_stmt|;
if|if
condition|(
name|auth
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|auth
operator|.
name|isGroup
argument_list|()
operator|&&
name|auth
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
condition|)
block|{
name|String
name|val
init|=
name|auth
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
index|[
literal|0
index|]
operator|.
name|getString
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|users
init|=
name|userMgr
operator|.
name|findAuthorizables
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
name|val
argument_list|)
decl_stmt|;
comment|// the result must contain 1 authorizable
name|assertTrue
argument_list|(
name|users
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Authorizable
name|first
init|=
name|users
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|getID
argument_list|()
argument_list|,
name|val
argument_list|)
expr_stmt|;
comment|// since id is unique -> there should be no more users in
comment|// the iterator left
name|assertFalse
argument_list|(
name|users
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindAuthorizableByAddedProperty
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
name|Authorizable
name|auth
init|=
literal|null
decl_stmt|;
try|try
block|{
name|auth
operator|=
name|userMgr
operator|.
name|createGroup
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|auth
operator|.
name|setProperty
argument_list|(
literal|"E-Mail"
argument_list|,
operator|new
name|Value
index|[]
block|{
name|superuser
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"anyVal"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
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
literal|"E-Mail"
argument_list|,
literal|"anyVal"
argument_list|)
decl_stmt|;
while|while
condition|(
name|result
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Authorizable
name|a
init|=
name|result
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|getID
argument_list|()
operator|.
name|equals
argument_list|(
name|auth
operator|.
name|getID
argument_list|()
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|found
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// remove the create group again.
if|if
condition|(
name|auth
operator|!=
literal|null
condition|)
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindUserInAllUsers
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|User
name|u
init|=
literal|null
decl_stmt|;
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
name|createUserId
argument_list|()
decl_stmt|;
name|u
operator|=
name|userMgr
operator|.
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
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|it
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
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_USER
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|found
condition|)
block|{
name|User
name|nu
init|=
operator|(
name|User
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|found
operator|=
name|nu
operator|.
name|getID
argument_list|()
operator|.
name|equals
argument_list|(
name|uid
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Searching for 'null' must find the created user."
argument_list|,
name|found
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|u
operator|!=
literal|null
condition|)
block|{
name|u
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindUserInAllUsers2
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|User
name|u
init|=
literal|null
decl_stmt|;
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
name|createUserId
argument_list|()
decl_stmt|;
name|u
operator|=
name|userMgr
operator|.
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
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|it
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
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_USER
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|found
condition|)
block|{
name|User
name|nu
init|=
operator|(
name|User
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|found
operator|=
name|nu
operator|.
name|getID
argument_list|()
operator|.
name|equals
argument_list|(
name|uid
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Searching for 'null' must find the created user."
argument_list|,
name|found
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|u
operator|!=
literal|null
condition|)
block|{
name|u
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindUserInAllGroups
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|User
name|u
init|=
literal|null
decl_stmt|;
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
name|createUserId
argument_list|()
decl_stmt|;
name|u
operator|=
name|userMgr
operator|.
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
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|it
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
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|it
operator|.
name|next
argument_list|()
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Searching for Groups should never find a user"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|u
operator|!=
literal|null
condition|)
block|{
name|u
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindUserByPrincipalName
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|User
name|u
init|=
literal|null
decl_stmt|;
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
name|createUserId
argument_list|()
decl_stmt|;
name|u
operator|=
name|userMgr
operator|.
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
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|it
init|=
name|userMgr
operator|.
name|findAuthorizables
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_USER
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|found
condition|)
block|{
name|User
name|nu
init|=
operator|(
name|User
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|found
operator|=
name|nu
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Searching for principal-name must find the created user."
argument_list|,
name|found
argument_list|)
expr_stmt|;
comment|// but search groups should not find anything
name|it
operator|=
name|userMgr
operator|.
name|findAuthorizables
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|u
operator|!=
literal|null
condition|)
block|{
name|u
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindUserWithGroupType
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|User
name|u
init|=
literal|null
decl_stmt|;
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
name|createUserId
argument_list|()
decl_stmt|;
name|u
operator|=
name|userMgr
operator|.
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
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// but search groups should not find anything
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|it
init|=
name|userMgr
operator|.
name|findAuthorizables
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Searching for Groups should not find the user"
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|u
operator|!=
literal|null
condition|)
block|{
name|u
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindGroupInAllGroups
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Group
name|gr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|gr
operator|=
name|userMgr
operator|.
name|createGroup
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|it
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
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|found
condition|)
block|{
name|Group
name|ng
init|=
operator|(
name|Group
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|found
operator|=
name|ng
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Searching for 'null' must find the created group."
argument_list|,
name|found
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|gr
operator|!=
literal|null
condition|)
block|{
name|gr
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindGroupByPrinicpalName
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Group
name|gr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|gr
operator|=
name|userMgr
operator|.
name|createGroup
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|it
init|=
name|userMgr
operator|.
name|findAuthorizables
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Group
name|ng
init|=
operator|(
name|Group
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Searching for principal-name must find the created group."
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|ng
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Only a single group must be found for a given principal name."
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|gr
operator|!=
literal|null
condition|)
block|{
name|gr
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindGroupWithUserType
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Group
name|gr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|gr
operator|=
name|userMgr
operator|.
name|createGroup
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|it
init|=
name|userMgr
operator|.
name|findAuthorizables
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_USER
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|gr
operator|!=
literal|null
condition|)
block|{
name|gr
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindGroupInAllUsers
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Group
name|gr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|gr
operator|=
name|userMgr
operator|.
name|createGroup
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|it
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
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_USER
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|it
operator|.
name|next
argument_list|()
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Searching for Users should never find a group"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|gr
operator|!=
literal|null
condition|)
block|{
name|gr
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindAllUsersDoesNotContainGroup
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|it
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
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_USER
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|assertFalse
argument_list|(
name|it
operator|.
name|next
argument_list|()
operator|.
name|isGroup
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindAllGroupsDoesNotContainUser
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|it
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
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|it
operator|.
name|next
argument_list|()
operator|.
name|isGroup
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindUserWithSpecialCharIdByPrincipalName
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"'"
argument_list|,
literal|"]"
argument_list|,
literal|"']"
argument_list|,
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
literal|"']"
argument_list|)
argument_list|,
name|Text
operator|.
name|escape
argument_list|(
literal|"']"
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|User
name|user
init|=
literal|null
decl_stmt|;
try|try
block|{
name|user
operator|=
name|userMgr
operator|.
name|createUser
argument_list|(
name|id
argument_list|,
literal|"pw"
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|it
init|=
name|userMgr
operator|.
name|findAuthorizables
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
name|id
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_USER
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|found
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
name|found
operator|=
name|id
operator|.
name|equals
argument_list|(
name|a
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|found
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|user
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
block|}
block|}
block|}
block|}
end_class

end_unit

