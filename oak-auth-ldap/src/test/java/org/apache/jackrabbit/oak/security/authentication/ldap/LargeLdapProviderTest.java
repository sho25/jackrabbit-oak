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
name|authentication
operator|.
name|ldap
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
name|Arrays
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
name|LinkedList
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
name|authentication
operator|.
name|ldap
operator|.
name|impl
operator|.
name|LdapIdentityProvider
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
name|security
operator|.
name|authentication
operator|.
name|ldap
operator|.
name|impl
operator|.
name|LdapProviderConfig
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
name|authentication
operator|.
name|external
operator|.
name|ExternalGroup
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
name|authentication
operator|.
name|external
operator|.
name|ExternalIdentity
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
name|authentication
operator|.
name|external
operator|.
name|ExternalIdentityRef
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
name|authentication
operator|.
name|external
operator|.
name|ExternalUser
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
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|LargeLdapProviderTest
block|{
specifier|protected
specifier|static
specifier|final
name|InternalLdapServer
name|LDAP_SERVER
init|=
operator|new
name|InternalLdapServer
argument_list|()
decl_stmt|;
comment|//initialize LDAP server only once (fast, but might turn out to be not sufficiently flexible in the future)
specifier|protected
specifier|static
specifier|final
name|boolean
name|USE_COMMON_LDAP_FIXTURE
init|=
literal|false
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|IDP_NAME
init|=
literal|"ldap"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|GROUP_NAME
init|=
literal|"foobargroup"
decl_stmt|;
specifier|protected
specifier|static
name|String
name|GROUP_DN
decl_stmt|;
specifier|protected
specifier|static
name|String
index|[]
name|TEST_MEMBERS
decl_stmt|;
specifier|protected
specifier|static
name|int
name|NUM_USERS
init|=
literal|100
decl_stmt|;
specifier|protected
specifier|static
name|int
name|SIZE_LIMIT
init|=
literal|50
decl_stmt|;
specifier|protected
name|LdapIdentityProvider
name|idp
decl_stmt|;
specifier|protected
name|LdapProviderConfig
name|providerConfig
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|USE_COMMON_LDAP_FIXTURE
condition|)
block|{
name|LDAP_SERVER
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|LDAP_SERVER
operator|.
name|setMaxSizeLimit
argument_list|(
name|SIZE_LIMIT
argument_list|)
expr_stmt|;
name|initLdapFixture
argument_list|(
name|LDAP_SERVER
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|USE_COMMON_LDAP_FIXTURE
condition|)
block|{
name|LDAP_SERVER
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|USE_COMMON_LDAP_FIXTURE
condition|)
block|{
name|LDAP_SERVER
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|LDAP_SERVER
operator|.
name|setMaxSizeLimit
argument_list|(
name|SIZE_LIMIT
argument_list|)
expr_stmt|;
name|initLdapFixture
argument_list|(
name|LDAP_SERVER
argument_list|)
expr_stmt|;
block|}
name|idp
operator|=
name|createIDP
argument_list|()
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
if|if
condition|(
operator|!
name|USE_COMMON_LDAP_FIXTURE
condition|)
block|{
name|LDAP_SERVER
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
name|idp
operator|.
name|close
argument_list|()
expr_stmt|;
name|idp
operator|=
literal|null
expr_stmt|;
block|}
specifier|protected
name|LdapIdentityProvider
name|createIDP
parameter_list|()
block|{
name|providerConfig
operator|=
operator|new
name|LdapProviderConfig
argument_list|()
operator|.
name|setName
argument_list|(
name|IDP_NAME
argument_list|)
operator|.
name|setHostname
argument_list|(
literal|"127.0.0.1"
argument_list|)
operator|.
name|setPort
argument_list|(
name|LDAP_SERVER
operator|.
name|getPort
argument_list|()
argument_list|)
operator|.
name|setBindDN
argument_list|(
name|USER_DN
argument_list|)
operator|.
name|setBindPassword
argument_list|(
name|USER_PWD
argument_list|)
operator|.
name|setGroupMemberAttribute
argument_list|(
literal|"member"
argument_list|)
expr_stmt|;
name|providerConfig
operator|.
name|getUserConfig
argument_list|()
operator|.
name|setBaseDN
argument_list|(
name|AbstractServer
operator|.
name|EXAMPLE_DN
argument_list|)
operator|.
name|setObjectClasses
argument_list|(
literal|"inetOrgPerson"
argument_list|)
expr_stmt|;
name|providerConfig
operator|.
name|getGroupConfig
argument_list|()
operator|.
name|setBaseDN
argument_list|(
name|AbstractServer
operator|.
name|EXAMPLE_DN
argument_list|)
operator|.
name|setObjectClasses
argument_list|(
name|InternalLdapServer
operator|.
name|GROUP_CLASS_ATTR
argument_list|)
expr_stmt|;
name|providerConfig
operator|.
name|getAdminPoolConfig
argument_list|()
operator|.
name|setMaxActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|providerConfig
operator|.
name|getUserPoolConfig
argument_list|()
operator|.
name|setMaxActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
operator|new
name|LdapIdentityProvider
argument_list|(
name|providerConfig
argument_list|)
return|;
block|}
specifier|protected
specifier|static
specifier|final
name|String
name|USER_ID
init|=
literal|"foobar"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|USER_PWD
init|=
literal|"foobar"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|USER_FIRSTNAME
init|=
literal|"Foo"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|USER_LASTNAME
init|=
literal|"Bar"
decl_stmt|;
specifier|protected
specifier|static
name|String
name|USER_DN
decl_stmt|;
specifier|protected
specifier|static
name|void
name|initLdapFixture
parameter_list|(
name|InternalLdapServer
name|server
parameter_list|)
throws|throws
name|Exception
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|members
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|USER_DN
operator|=
name|LDAP_SERVER
operator|.
name|addUser
argument_list|(
name|USER_FIRSTNAME
argument_list|,
name|USER_LASTNAME
argument_list|,
name|USER_ID
argument_list|,
name|USER_PWD
argument_list|)
expr_stmt|;
name|GROUP_DN
operator|=
name|server
operator|.
name|addGroup
argument_list|(
name|GROUP_NAME
argument_list|,
name|USER_DN
argument_list|)
expr_stmt|;
name|members
operator|.
name|add
argument_list|(
name|USER_DN
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_USERS
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|userId
init|=
literal|"user-"
operator|+
name|i
decl_stmt|;
name|String
name|userDN
init|=
name|server
operator|.
name|addUser
argument_list|(
name|userId
argument_list|,
literal|"test"
argument_list|,
name|userId
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|LDAP_SERVER
operator|.
name|addMember
argument_list|(
name|GROUP_DN
argument_list|,
name|userDN
argument_list|)
expr_stmt|;
name|members
operator|.
name|add
argument_list|(
name|userDN
argument_list|)
expr_stmt|;
block|}
name|TEST_MEMBERS
operator|=
name|members
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|members
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetMembers
parameter_list|()
throws|throws
name|Exception
block|{
name|ExternalIdentityRef
name|ref
init|=
operator|new
name|ExternalIdentityRef
argument_list|(
name|GROUP_DN
argument_list|,
name|IDP_NAME
argument_list|)
decl_stmt|;
name|ExternalIdentity
name|id
init|=
name|idp
operator|.
name|getIdentity
argument_list|(
name|ref
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Group instance"
argument_list|,
name|id
operator|instanceof
name|ExternalGroup
argument_list|)
expr_stmt|;
name|ExternalGroup
name|grp
init|=
operator|(
name|ExternalGroup
operator|)
name|id
decl_stmt|;
name|assertIfEquals
argument_list|(
literal|"Group members"
argument_list|,
name|TEST_MEMBERS
argument_list|,
name|grp
operator|.
name|getDeclaredMembers
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-2874"
argument_list|)
specifier|public
name|void
name|testListUsers
parameter_list|()
throws|throws
name|Exception
block|{
name|Iterator
argument_list|<
name|ExternalUser
argument_list|>
name|users
init|=
name|idp
operator|.
name|listUsers
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ExternalIdentityRef
argument_list|>
name|refs
init|=
operator|new
name|ArrayList
argument_list|<
name|ExternalIdentityRef
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|users
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|refs
operator|.
name|add
argument_list|(
name|users
operator|.
name|next
argument_list|()
operator|.
name|getExternalId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertIfEquals
argument_list|(
literal|"Test users"
argument_list|,
name|TEST_MEMBERS
argument_list|,
name|refs
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|assertIfEquals
parameter_list|(
name|String
name|message
parameter_list|,
name|String
index|[]
name|expected
parameter_list|,
name|Iterable
argument_list|<
name|ExternalIdentityRef
argument_list|>
name|result
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|dns
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ExternalIdentityRef
name|ref
range|:
name|result
control|)
block|{
name|dns
operator|.
name|add
argument_list|(
name|ref
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|dns
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|String
name|exp
init|=
name|Text
operator|.
name|implode
argument_list|(
name|expected
argument_list|,
literal|",\n"
argument_list|)
decl_stmt|;
name|String
name|res
init|=
name|Text
operator|.
name|implode
argument_list|(
name|dns
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|dns
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
literal|",\n"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
name|exp
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

