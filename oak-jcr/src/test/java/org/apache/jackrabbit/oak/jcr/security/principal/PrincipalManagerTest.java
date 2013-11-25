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
name|principal
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
name|security
operator|.
name|acl
operator|.
name|Group
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|Set
import|;
end_import

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
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
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
name|AbstractJCRTest
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
comment|/**  * {@code PrincipalManagerTest}...  */
end_comment

begin_class
specifier|public
class|class
name|PrincipalManagerTest
extends|extends
name|AbstractJCRTest
block|{
specifier|private
name|PrincipalManager
name|principalMgr
decl_stmt|;
specifier|private
name|Group
name|everyone
decl_stmt|;
specifier|private
name|Principal
index|[]
name|adminPrincipals
decl_stmt|;
annotation|@
name|Before
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
if|if
condition|(
operator|!
operator|(
name|superuser
operator|instanceof
name|JackrabbitSession
operator|)
condition|)
block|{
name|superuser
operator|.
name|logout
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|NotExecutableException
argument_list|()
throw|;
block|}
name|principalMgr
operator|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|superuser
operator|)
operator|.
name|getPrincipalManager
argument_list|()
expr_stmt|;
name|everyone
operator|=
operator|(
name|Group
operator|)
name|principalMgr
operator|.
name|getEveryone
argument_list|()
expr_stmt|;
name|adminPrincipals
operator|=
name|getPrincipals
argument_list|(
name|getHelper
argument_list|()
operator|.
name|getSuperuserCredentials
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Principal
index|[]
name|getPrincipals
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|Exception
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
if|if
condition|(
name|credentials
operator|instanceof
name|SimpleCredentials
condition|)
block|{
name|Principal
name|p
init|=
name|principalMgr
operator|.
name|getPrincipal
argument_list|(
operator|(
operator|(
name|SimpleCredentials
operator|)
name|credentials
operator|)
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
name|principalMgr
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
block|}
return|return
name|principals
operator|.
name|toArray
argument_list|(
operator|new
name|Principal
index|[
name|principals
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isGroup
parameter_list|(
name|Principal
name|p
parameter_list|)
block|{
return|return
name|p
operator|instanceof
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetEveryone
parameter_list|()
block|{
name|Principal
name|principal
init|=
name|principalMgr
operator|.
name|getEveryone
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|principal
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isGroup
argument_list|(
name|principal
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testGetEveryoneByName
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|principalMgr
operator|.
name|hasPrincipal
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|principalMgr
operator|.
name|getPrincipal
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|principalMgr
operator|.
name|getPrincipal
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSuperUserIsEveryOne
parameter_list|()
block|{
for|for
control|(
name|Principal
name|pcpl
range|:
name|adminPrincipals
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|pcpl
operator|.
name|equals
argument_list|(
name|everyone
argument_list|)
operator|)
condition|)
block|{
name|assertTrue
argument_list|(
name|everyone
operator|.
name|isMember
argument_list|(
name|pcpl
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadOnlyIsEveryOne
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s
init|=
name|getHelper
argument_list|()
operator|.
name|getReadOnlySession
argument_list|()
decl_stmt|;
try|try
block|{
name|Principal
index|[]
name|pcpls
init|=
name|getPrincipals
argument_list|(
name|getHelper
argument_list|()
operator|.
name|getReadOnlyCredentials
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Principal
name|pcpl
range|:
name|pcpls
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|pcpl
operator|.
name|equals
argument_list|(
name|everyone
argument_list|)
operator|)
condition|)
block|{
name|assertTrue
argument_list|(
name|everyone
operator|.
name|isMember
argument_list|(
name|pcpl
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|s
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
name|testHasPrincipal
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|principalMgr
operator|.
name|hasPrincipal
argument_list|(
name|everyone
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Principal
name|pcpl
range|:
name|adminPrincipals
control|)
block|{
name|assertTrue
argument_list|(
name|principalMgr
operator|.
name|hasPrincipal
argument_list|(
name|pcpl
operator|.
name|getName
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
name|testGetPrincipal
parameter_list|()
block|{
name|Principal
name|p
init|=
name|principalMgr
operator|.
name|getPrincipal
argument_list|(
name|everyone
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|everyone
argument_list|,
name|p
argument_list|)
expr_stmt|;
for|for
control|(
name|Principal
name|pcpl
range|:
name|adminPrincipals
control|)
block|{
name|Principal
name|pp
init|=
name|principalMgr
operator|.
name|getPrincipal
argument_list|(
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"PrincipalManager.getPrincipal returned Principal with different Name"
argument_list|,
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|,
name|pp
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalGetName
parameter_list|()
block|{
for|for
control|(
name|Principal
name|pcpl
range|:
name|adminPrincipals
control|)
block|{
name|Principal
name|pp
init|=
name|principalMgr
operator|.
name|getPrincipal
argument_list|(
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"PrincipalManager.getPrincipal returned Principal with different Name"
argument_list|,
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|,
name|pp
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipals
parameter_list|()
block|{
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
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
name|Principal
name|p
init|=
name|it
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|isGroup
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetGroupPrincipals
parameter_list|()
block|{
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
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
name|Principal
name|p
init|=
name|it
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|isGroup
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAllPrincipals
parameter_list|()
block|{
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
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
name|Principal
name|p
init|=
name|it
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|principalMgr
operator|.
name|hasPrincipal
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|principalMgr
operator|.
name|getPrincipal
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMembers
parameter_list|()
block|{
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
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
name|Principal
name|p
init|=
name|it
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|equals
argument_list|(
name|principalMgr
operator|.
name|getEveryone
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|isGroup
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|Enumeration
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|en
init|=
operator|(
operator|(
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
operator|)
name|p
operator|)
operator|.
name|members
argument_list|()
decl_stmt|;
while|while
condition|(
name|en
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Principal
name|memb
init|=
name|en
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|principalMgr
operator|.
name|hasPrincipal
argument_list|(
name|memb
operator|.
name|getName
argument_list|()
argument_list|)
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
name|testMembers2
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|gr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|gr
operator|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|superuser
operator|)
operator|.
name|getUserManager
argument_list|()
operator|.
name|createGroup
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
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
name|Principal
name|p
init|=
name|it
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|equals
argument_list|(
name|principalMgr
operator|.
name|getEveryone
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|isGroup
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|Enumeration
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|en
init|=
operator|(
operator|(
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
operator|)
name|p
operator|)
operator|.
name|members
argument_list|()
decl_stmt|;
while|while
condition|(
name|en
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Principal
name|memb
init|=
name|en
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|principalMgr
operator|.
name|hasPrincipal
argument_list|(
name|memb
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|testGroupMembership
parameter_list|()
block|{
name|testMembership
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|)
expr_stmt|;
name|testMembership
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
expr_stmt|;
name|testMembership
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testMembership
parameter_list|(
name|int
name|searchType
parameter_list|)
block|{
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|getPrincipals
argument_list|(
name|searchType
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
name|Principal
name|p
init|=
name|it
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|equals
argument_list|(
name|everyone
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|boolean
name|atleastEveryone
init|=
literal|false
decl_stmt|;
for|for
control|(
name|PrincipalIterator
name|membership
init|=
name|principalMgr
operator|.
name|getGroupMembership
argument_list|(
name|p
argument_list|)
init|;
name|membership
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Principal
name|gr
init|=
name|membership
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|isGroup
argument_list|(
name|gr
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|gr
operator|.
name|equals
argument_list|(
name|everyone
argument_list|)
condition|)
block|{
name|atleastEveryone
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"All principals (except everyone) must be member of the everyone group."
argument_list|,
name|atleastEveryone
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEveryoneGroupMembership
parameter_list|()
block|{
name|Principal
name|everyone
init|=
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
decl_stmt|;
for|for
control|(
name|PrincipalIterator
name|membership
init|=
name|principalMgr
operator|.
name|getGroupMembership
argument_list|(
name|everyone
argument_list|)
init|;
name|membership
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Principal
name|gr
init|=
name|membership
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|isGroup
argument_list|(
name|gr
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|gr
operator|.
name|equals
argument_list|(
name|everyone
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Everyone must never be a member of the EveryOne group."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetMembersConsistentWithMembership
parameter_list|()
block|{
name|Principal
name|everyone
init|=
name|principalMgr
operator|.
name|getEveryone
argument_list|()
decl_stmt|;
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
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
name|Principal
name|p
init|=
name|it
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|equals
argument_list|(
name|everyone
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|assertTrue
argument_list|(
name|isGroup
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|Enumeration
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|members
init|=
operator|(
operator|(
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
operator|)
name|p
operator|)
operator|.
name|members
argument_list|()
decl_stmt|;
while|while
condition|(
name|members
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Principal
name|memb
init|=
name|members
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|Principal
name|group
init|=
literal|null
decl_stmt|;
name|PrincipalIterator
name|mship
init|=
name|principalMgr
operator|.
name|getGroupMembership
argument_list|(
name|memb
argument_list|)
decl_stmt|;
while|while
condition|(
name|mship
operator|.
name|hasNext
argument_list|()
operator|&&
name|group
operator|==
literal|null
condition|)
block|{
name|Principal
name|gr
init|=
name|mship
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|equals
argument_list|(
name|gr
argument_list|)
condition|)
block|{
name|group
operator|=
name|gr
expr_stmt|;
block|}
block|}
name|assertNotNull
argument_list|(
literal|"Group member "
operator|+
name|memb
operator|.
name|getName
argument_list|()
operator|+
literal|"does not reveal group upon getGroupMembership"
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipal
parameter_list|()
block|{
for|for
control|(
name|Principal
name|pcpl
range|:
name|adminPrincipals
control|)
block|{
if|if
condition|(
name|pcpl
operator|.
name|equals
argument_list|(
name|everyone
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|assertTrue
argument_list|(
name|principalMgr
operator|.
name|hasPrincipal
argument_list|(
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// search must find at least a single principal
name|assertTrue
argument_list|(
literal|"findPrincipals does not find principal with filter '"
operator|+
name|pcpl
operator|.
name|getName
argument_list|()
operator|+
literal|'\''
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipalByType
parameter_list|()
block|{
for|for
control|(
name|Principal
name|pcpl
range|:
name|adminPrincipals
control|)
block|{
if|if
condition|(
name|pcpl
operator|.
name|equals
argument_list|(
name|everyone
argument_list|)
condition|)
block|{
comment|// special case covered by another test
continue|continue;
block|}
name|assertTrue
argument_list|(
name|principalMgr
operator|.
name|hasPrincipal
argument_list|(
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|isGroup
argument_list|(
name|pcpl
argument_list|)
condition|)
block|{
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
decl_stmt|;
comment|// search must find at least a single matching group principal
name|assertTrue
argument_list|(
literal|"findPrincipals does not find principal with filter "
operator|+
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|)
decl_stmt|;
comment|// search must find at least a single matching non-group principal
name|assertTrue
argument_list|(
literal|"findPrincipals does not find principal with filter '"
operator|+
name|pcpl
operator|.
name|getName
argument_list|()
operator|+
literal|"' and type "
operator|+
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipalByTypeAll
parameter_list|()
block|{
for|for
control|(
name|Principal
name|pcpl
range|:
name|adminPrincipals
control|)
block|{
if|if
condition|(
name|pcpl
operator|.
name|equals
argument_list|(
name|everyone
argument_list|)
condition|)
block|{
comment|// special case covered by another test
continue|continue;
block|}
name|assertTrue
argument_list|(
name|principalMgr
operator|.
name|hasPrincipal
argument_list|(
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
decl_stmt|;
name|PrincipalIterator
name|it2
init|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|pcpl
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Principal "
operator|+
name|pcpl
operator|.
name|getName
argument_list|()
operator|+
literal|" not found"
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Principal "
operator|+
name|pcpl
operator|.
name|getName
argument_list|()
operator|+
literal|" not found"
argument_list|,
name|it2
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// both search must reveal the same result and size
name|assertTrue
argument_list|(
name|it
operator|.
name|getSize
argument_list|()
operator|==
name|it2
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|s1
init|=
operator|new
name|HashSet
argument_list|<
name|Principal
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|s2
init|=
operator|new
name|HashSet
argument_list|<
name|Principal
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
name|it2
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|s1
operator|.
name|add
argument_list|(
name|it
operator|.
name|nextPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|s2
operator|.
name|add
argument_list|(
name|it2
operator|.
name|nextPrincipal
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
name|it2
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindEveryone
parameter_list|()
block|{
name|Principal
name|everyone
init|=
name|principalMgr
operator|.
name|getEveryone
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|principalMgr
operator|.
name|hasPrincipal
argument_list|(
name|everyone
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|containedInResult
init|=
literal|false
decl_stmt|;
comment|// untyped search -> everyone must be part of the result set
name|PrincipalIterator
name|it
init|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|everyone
operator|.
name|getName
argument_list|()
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
name|Principal
name|p
init|=
name|it
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|everyone
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|containedInResult
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|containedInResult
argument_list|)
expr_stmt|;
comment|// search group only -> everyone must be part of the result set
name|containedInResult
operator|=
literal|false
expr_stmt|;
name|it
operator|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|everyone
operator|.
name|getName
argument_list|()
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
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
name|Principal
name|p
init|=
name|it
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|everyone
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|containedInResult
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|containedInResult
argument_list|)
expr_stmt|;
comment|// search non-group only -> everyone should not be part of the result set
name|containedInResult
operator|=
literal|false
expr_stmt|;
name|it
operator|=
name|principalMgr
operator|.
name|findPrincipals
argument_list|(
name|everyone
operator|.
name|getName
argument_list|()
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
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
name|Principal
name|p
init|=
name|it
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|everyone
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|containedInResult
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertFalse
argument_list|(
name|containedInResult
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

