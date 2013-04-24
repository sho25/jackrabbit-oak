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
name|principal
operator|.
name|EveryonePrincipal
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
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests for the group associated with {@code EveryonePrincipal}  */
end_comment

begin_class
specifier|public
class|class
name|EveryoneGroupTest
extends|extends
name|AbstractUserTest
block|{
specifier|private
name|Group
name|everyone
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|everyone
operator|=
name|userMgr
operator|.
name|createGroup
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|everyone
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
finally|finally
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEveryoneGroup
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|assertEquals
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|everyone
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
name|testPrincipal
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|assertEquals
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|everyone
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
name|testGroupPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|Principal
name|everyonePrincipal
init|=
name|everyone
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|everyonePrincipal
operator|instanceof
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|everyonePrincipal
operator|.
name|equals
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
operator|.
name|equals
argument_list|(
name|everyonePrincipal
argument_list|)
argument_list|)
expr_stmt|;
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
name|gr
init|=
operator|(
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
operator|)
name|everyonePrincipal
decl_stmt|;
name|assertFalse
argument_list|(
name|gr
operator|.
name|isMember
argument_list|(
name|everyonePrincipal
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|gr
operator|.
name|isMember
argument_list|(
name|getTestUser
argument_list|(
name|superuser
argument_list|)
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|gr
operator|.
name|isMember
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMembers
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|assertTrue
argument_list|(
name|everyone
operator|.
name|isDeclaredMember
argument_list|(
name|getTestUser
argument_list|(
name|superuser
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|everyone
operator|.
name|isMember
argument_list|(
name|getTestUser
argument_list|(
name|superuser
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|it
init|=
name|everyone
operator|.
name|getDeclaredMembers
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Authorizable
argument_list|>
name|members
init|=
operator|new
name|HashSet
argument_list|<
name|Authorizable
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|members
operator|.
name|add
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|it
operator|=
name|everyone
operator|.
name|getMembers
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
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
name|members
operator|.
name|contains
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEditMembers
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|assertFalse
argument_list|(
name|everyone
operator|.
name|addMember
argument_list|(
name|getTestUser
argument_list|(
name|superuser
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|everyone
operator|.
name|removeMember
argument_list|(
name|getTestUser
argument_list|(
name|superuser
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Group
name|anotherGroup
init|=
literal|null
decl_stmt|;
try|try
block|{
name|anotherGroup
operator|=
name|userMgr
operator|.
name|createGroup
argument_list|(
literal|"testGroup"
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|everyone
operator|.
name|addMember
argument_list|(
name|anotherGroup
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|everyone
operator|.
name|removeMember
argument_list|(
name|anotherGroup
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|anotherGroup
operator|.
name|addMember
argument_list|(
name|everyone
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|anotherGroup
operator|.
name|removeMember
argument_list|(
name|everyone
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|anotherGroup
operator|!=
literal|null
condition|)
block|{
name|anotherGroup
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
end_class

end_unit

