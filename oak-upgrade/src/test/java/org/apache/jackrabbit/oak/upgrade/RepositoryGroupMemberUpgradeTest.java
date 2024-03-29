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
name|upgrade
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|Set
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
name|Session
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
name|NodeType
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
name|RepositoryGroupMemberUpgradeTest
extends|extends
name|AbstractRepositoryUpgradeTest
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|TEST_USER_PREFIX
init|=
literal|"TestUser-"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|TEST_GROUP_PREFIX
init|=
literal|"TestGroup-"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|REPO_CONFIG
init|=
literal|"repository-groupmember.xml"
decl_stmt|;
annotation|@
name|Override
specifier|public
name|InputStream
name|getRepositoryConfig
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|REPO_CONFIG
argument_list|)
return|;
block|}
specifier|public
name|int
name|getNumUsers
parameter_list|()
block|{
return|return
literal|5
return|;
block|}
specifier|public
name|int
name|getNumGroups
parameter_list|()
block|{
return|return
literal|2
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createSourceContent
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|Exception
block|{
name|UserManager
name|userMgr
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|session
operator|)
operator|.
name|getUserManager
argument_list|()
decl_stmt|;
name|userMgr
operator|.
name|autoSave
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|User
name|users
index|[]
init|=
operator|new
name|User
index|[
name|getNumUsers
argument_list|()
index|]
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
name|users
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|userId
init|=
name|TEST_USER_PREFIX
operator|+
name|i
decl_stmt|;
name|users
index|[
name|i
index|]
operator|=
name|userMgr
operator|.
name|createUser
argument_list|(
name|userId
argument_list|,
name|userId
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|getNumGroups
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Group
name|g
init|=
name|userMgr
operator|.
name|createGroup
argument_list|(
name|TEST_GROUP_PREFIX
operator|+
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|User
name|user
range|:
name|users
control|)
block|{
name|g
operator|.
name|addMember
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyGroupNodeTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|JackrabbitSession
name|session
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|UserManager
name|userMgr
init|=
name|session
operator|.
name|getUserManager
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
name|getNumGroups
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Group
name|grp
init|=
operator|(
name|Group
operator|)
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|TEST_GROUP_PREFIX
operator|+
name|i
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|grp
argument_list|)
expr_stmt|;
name|Node
name|grpNode
init|=
name|session
operator|.
name|getNode
argument_list|(
name|grp
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|NodeType
name|nt
init|=
name|grpNode
operator|.
name|getPrimaryNodeType
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Migrated group needs to be rep:Group"
argument_list|,
name|UserConstants
operator|.
name|NT_REP_GROUP
argument_list|,
name|nt
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Migrated group needs to be new node type"
argument_list|,
name|nt
operator|.
name|isNodeType
argument_list|(
name|UserConstants
operator|.
name|NT_REP_MEMBER_REFERENCES
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyMembers
parameter_list|()
throws|throws
name|Exception
block|{
name|JackrabbitSession
name|session
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|UserManager
name|userMgr
init|=
name|session
operator|.
name|getUserManager
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
name|getNumGroups
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Group
name|grp
init|=
operator|(
name|Group
operator|)
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|TEST_GROUP_PREFIX
operator|+
name|i
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|grp
argument_list|)
expr_stmt|;
comment|// check if groups have all members
name|Set
argument_list|<
name|String
argument_list|>
name|testUsers
init|=
operator|new
name|HashSet
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
operator|<
name|getNumUsers
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|testUsers
operator|.
name|add
argument_list|(
name|TEST_USER_PREFIX
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|declaredMembers
init|=
name|grp
operator|.
name|getDeclaredMembers
argument_list|()
decl_stmt|;
while|while
condition|(
name|declaredMembers
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Authorizable
name|auth
init|=
name|declaredMembers
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"group must have member "
operator|+
name|auth
operator|.
name|getID
argument_list|()
argument_list|,
name|testUsers
operator|.
name|remove
argument_list|(
name|auth
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"group must have all members"
argument_list|,
literal|0
argument_list|,
name|testUsers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyMemberOf
parameter_list|()
throws|throws
name|Exception
block|{
name|JackrabbitSession
name|session
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|UserManager
name|userMgr
init|=
name|session
operator|.
name|getUserManager
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
name|getNumUsers
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|User
name|user
init|=
operator|(
name|User
operator|)
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|TEST_USER_PREFIX
operator|+
name|i
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|groups
init|=
operator|new
name|HashSet
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
operator|<
name|getNumGroups
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|groups
operator|.
name|add
argument_list|(
name|TEST_GROUP_PREFIX
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|Group
argument_list|>
name|groupIterator
init|=
name|user
operator|.
name|declaredMemberOf
argument_list|()
decl_stmt|;
while|while
condition|(
name|groupIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Group
name|grp
init|=
name|groupIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"user must be member of group "
operator|+
name|grp
operator|.
name|getID
argument_list|()
argument_list|,
name|groups
operator|.
name|remove
argument_list|(
name|grp
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"user "
operator|+
name|user
operator|.
name|getID
argument_list|()
operator|+
literal|" must be member of all groups"
argument_list|,
literal|0
argument_list|,
name|groups
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

