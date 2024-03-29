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
name|Map
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|Maps
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
name|PropertyState
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
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
specifier|abstract
class|class
name|MembershipBaseTest
extends|extends
name|AbstractSecurityTest
implements|implements
name|UserConstants
block|{
specifier|static
specifier|final
name|int
name|SIZE_TH
init|=
literal|10
decl_stmt|;
specifier|static
specifier|final
name|int
name|NUM_USERS
init|=
literal|50
decl_stmt|;
specifier|static
specifier|final
name|int
name|NUM_GROUPS
init|=
literal|50
decl_stmt|;
name|UserManagerImpl
name|userMgr
decl_stmt|;
name|MembershipProvider
name|mp
decl_stmt|;
specifier|private
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
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|testGroups
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
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
name|mp
operator|=
name|userMgr
operator|.
name|getMembershipProvider
argument_list|()
expr_stmt|;
comment|// set the threshold low for testing
name|mp
operator|.
name|setMembershipSizeThreshold
argument_list|(
name|SIZE_TH
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
for|for
control|(
name|String
name|path
range|:
name|Iterables
operator|.
name|concat
argument_list|(
name|testUsers
argument_list|,
name|testGroups
argument_list|)
control|)
block|{
name|Authorizable
name|auth
init|=
name|userMgr
operator|.
name|getAuthorizableByPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
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
block|}
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|testUsers
operator|.
name|clear
argument_list|()
expr_stmt|;
name|testGroups
operator|.
name|clear
argument_list|()
expr_stmt|;
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|NotNull
name|User
name|createUser
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|userId
init|=
literal|"testUser"
operator|+
name|testUsers
operator|.
name|size
argument_list|()
decl_stmt|;
name|User
name|usr
init|=
name|userMgr
operator|.
name|createUser
argument_list|(
name|userId
argument_list|,
literal|"pw"
argument_list|)
decl_stmt|;
name|testUsers
operator|.
name|add
argument_list|(
name|usr
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|usr
return|;
block|}
annotation|@
name|NotNull
name|Group
name|createGroup
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|groupName
init|=
literal|"testGroup"
operator|+
name|testGroups
operator|.
name|size
argument_list|()
decl_stmt|;
name|Group
name|grp
init|=
name|userMgr
operator|.
name|createGroup
argument_list|(
name|groupName
argument_list|)
decl_stmt|;
name|testGroups
operator|.
name|add
argument_list|(
name|grp
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|grp
return|;
block|}
annotation|@
name|NotNull
name|List
argument_list|<
name|String
argument_list|>
name|createMembers
parameter_list|(
annotation|@
name|NotNull
name|Group
name|g
parameter_list|,
name|int
name|cnt
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|memberPaths
init|=
operator|new
name|ArrayList
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
operator|<=
name|cnt
condition|;
name|i
operator|++
control|)
block|{
name|User
name|u
init|=
name|createUser
argument_list|()
decl_stmt|;
name|Group
name|gr
init|=
name|createGroup
argument_list|()
decl_stmt|;
name|g
operator|.
name|addMembers
argument_list|(
name|u
operator|.
name|getID
argument_list|()
argument_list|,
name|gr
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|memberPaths
operator|.
name|add
argument_list|(
name|u
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|memberPaths
operator|.
name|add
argument_list|(
name|gr
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|memberPaths
return|;
block|}
annotation|@
name|NotNull
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|createIdMap
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|memberIds
init|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|String
name|memberId
init|=
literal|"member"
operator|+
name|i
decl_stmt|;
name|memberIds
operator|.
name|put
argument_list|(
name|getContentID
argument_list|(
name|memberId
argument_list|)
argument_list|,
name|memberId
argument_list|)
expr_stmt|;
block|}
return|return
name|memberIds
return|;
block|}
annotation|@
name|NotNull
name|String
name|getContentID
parameter_list|(
annotation|@
name|NotNull
name|String
name|memberId
parameter_list|)
block|{
return|return
name|userMgr
operator|.
name|getMembershipProvider
argument_list|()
operator|.
name|getContentID
argument_list|(
name|memberId
argument_list|)
return|;
block|}
annotation|@
name|Nullable
name|String
name|getContentID
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|TreeUtil
operator|.
name|getString
argument_list|(
name|tree
argument_list|,
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|)
return|;
block|}
annotation|@
name|NotNull
name|Tree
name|getTree
parameter_list|(
annotation|@
name|NotNull
name|Authorizable
name|a
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|a
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|NotNull
name|Tree
name|getTree
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
return|;
block|}
specifier|static
name|void
name|assertMembers
parameter_list|(
name|Group
name|grp
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|ms
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|members
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|ms
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|iter
init|=
name|grp
operator|.
name|getMembers
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Authorizable
name|member
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Group must have member"
argument_list|,
name|members
operator|.
name|remove
argument_list|(
name|member
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Group must have all members"
argument_list|,
literal|0
argument_list|,
name|members
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|static
name|void
name|assertMemberList
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|groupTree
parameter_list|,
name|int
name|cntRefTrees
parameter_list|,
name|int
name|cnt
parameter_list|)
block|{
name|Tree
name|list
init|=
name|groupTree
operator|.
name|getChild
argument_list|(
name|REP_MEMBERS_LIST
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cntRefTrees
argument_list|,
name|list
operator|.
name|getChildrenCount
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Tree
name|c
range|:
name|list
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|PropertyState
name|repMembers
init|=
name|c
operator|.
name|getProperty
argument_list|(
name|REP_MEMBERS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|repMembers
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|SIZE_TH
operator|==
name|repMembers
operator|.
name|count
argument_list|()
operator|||
name|cnt
operator|==
name|repMembers
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

