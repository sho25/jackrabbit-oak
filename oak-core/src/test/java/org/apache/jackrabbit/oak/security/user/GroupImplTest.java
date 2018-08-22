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
name|Iterator
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterators
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
name|spi
operator|.
name|security
operator|.
name|principal
operator|.
name|EveryonePrincipal
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
name|mockito
operator|.
name|Mockito
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
name|assertSame
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
name|GroupImplTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|final
name|String
name|groupId
init|=
literal|"gr"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
specifier|private
name|UserManagerImpl
name|uMgr
decl_stmt|;
specifier|private
name|GroupImpl
name|group
decl_stmt|;
annotation|@
name|Override
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
name|uMgr
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
name|Group
name|g
init|=
name|uMgr
operator|.
name|createGroup
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
name|group
operator|=
operator|new
name|GroupImpl
argument_list|(
name|groupId
argument_list|,
name|root
operator|.
name|getTree
argument_list|(
name|g
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
name|uMgr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|testCheckValidTree
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|GroupImpl
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|,
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
argument_list|,
name|uMgr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddMemberInvalidAuthorizable
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|group
operator|.
name|addMember
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|Authorizable
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddMemberEveryone
parameter_list|()
throws|throws
name|Exception
block|{
name|Group
name|everyoneGroup
init|=
name|uMgr
operator|.
name|createGroup
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|group
operator|.
name|addMember
argument_list|(
name|everyoneGroup
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddMemberItself
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|group
operator|.
name|addMember
argument_list|(
name|group
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveMemberInvalidAuthorizable
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|group
operator|.
name|removeMember
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|Authorizable
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveNotMember
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|group
operator|.
name|removeMember
argument_list|(
name|getTestUser
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsMemberInvalidAuthorizable
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|group
operator|.
name|isMember
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|Authorizable
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|Principal
name|groupPrincipal
init|=
name|group
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|groupPrincipal
operator|instanceof
name|AbstractGroupPrincipal
argument_list|)
expr_stmt|;
name|AbstractGroupPrincipal
name|agp
init|=
operator|(
name|AbstractGroupPrincipal
operator|)
name|groupPrincipal
decl_stmt|;
name|assertSame
argument_list|(
name|uMgr
argument_list|,
name|agp
operator|.
name|getUserManager
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|group
operator|.
name|isEveryone
argument_list|()
argument_list|,
name|agp
operator|.
name|isEveryone
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupPrincipalIsMember
parameter_list|()
throws|throws
name|Exception
block|{
name|group
operator|.
name|addMember
argument_list|(
name|getTestUser
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractGroupPrincipal
name|groupPrincipal
init|=
operator|(
name|AbstractGroupPrincipal
operator|)
name|group
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|groupPrincipal
operator|.
name|isMember
argument_list|(
name|getTestUser
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupPrincipalMembers
parameter_list|()
throws|throws
name|Exception
block|{
name|group
operator|.
name|addMember
argument_list|(
name|getTestUser
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractGroupPrincipal
name|groupPrincipal
init|=
operator|(
name|AbstractGroupPrincipal
operator|)
name|group
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|members
init|=
name|groupPrincipal
operator|.
name|getMembers
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|Iterators
operator|.
name|elementsEqual
argument_list|(
name|group
operator|.
name|getMembers
argument_list|()
argument_list|,
name|members
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

