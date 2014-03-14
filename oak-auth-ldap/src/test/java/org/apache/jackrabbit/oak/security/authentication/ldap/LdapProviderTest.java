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
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|server
operator|.
name|constants
operator|.
name|ServerDNConstants
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
name|ExternalIdentityProvider
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
name|assertNotNull
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
name|assertNull
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

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|LdapProviderTest
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
specifier|private
specifier|static
specifier|final
name|String
name|TUTORIAL_LDIF
init|=
literal|"apache-ds-tutorial.ldif"
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
name|ExternalIdentityProvider
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
block|}
specifier|protected
name|ExternalIdentityProvider
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
name|ServerDNConstants
operator|.
name|ADMIN_SYSTEM_DN
argument_list|)
operator|.
name|setBindPassword
argument_list|(
name|InternalLdapServer
operator|.
name|ADMIN_PW
argument_list|)
operator|.
name|setGroupMemberAttribute
argument_list|(
literal|"uniquemember"
argument_list|)
expr_stmt|;
name|providerConfig
operator|.
name|getUserConfig
argument_list|()
operator|.
name|setBaseDN
argument_list|(
name|ServerDNConstants
operator|.
name|USERS_SYSTEM_DN
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
name|ServerDNConstants
operator|.
name|GROUPS_SYSTEM_DN
argument_list|)
operator|.
name|setObjectClasses
argument_list|(
literal|"groupOfUniqueNames"
argument_list|)
expr_stmt|;
name|LdapIdentityProvider
name|ldapIDP
init|=
operator|new
name|LdapIdentityProvider
argument_list|(
name|providerConfig
argument_list|)
decl_stmt|;
name|ldapIDP
operator|.
name|disableConnectionPooling
operator|=
literal|true
expr_stmt|;
return|return
name|ldapIDP
return|;
block|}
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
name|InputStream
name|tutorialLDIF
init|=
name|LdapProviderTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|TUTORIAL_LDIF
argument_list|)
decl_stmt|;
name|server
operator|.
name|loadLdif
argument_list|(
name|tutorialLDIF
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
specifier|final
name|String
name|TEST_USER0_DN
init|=
literal|"cn=Rat Ratterson,ou=users,ou=system"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_USER0_UID
init|=
literal|"ratty"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_USER1_DN
init|=
literal|"cn=Horatio Hornblower,ou=users,ou=system"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_USER1_UID
init|=
literal|"hhornblo"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_USER1_PATH
init|=
literal|"cn=Horatio Hornblower/ou=users/ou=system"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_USER2_DN
init|=
literal|"cn=William Bush,ou=users,ou=system"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_USER3_DN
init|=
literal|"cn=Thomas Quist,ou=users,ou=system"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_USER4_DN
init|=
literal|"cn=Moultrie Crystal,ou=users,ou=system"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_USER5_UID
init|=
literal|"=007="
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_USER5_DN
init|=
literal|"cn=Special\\, Agent [007],ou=users,ou=system"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_USER5_PATH
init|=
literal|"cn=Special\\, Agent %5B007%5D/ou=users/ou=system"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_GROUP1_DN
init|=
literal|"cn=HMS Lydia,ou=crews,ou=groups,ou=system"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_GROUP1_NAME
init|=
literal|"HMS Lydia"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|TEST_GROUP1_MEMBERS
init|=
block|{
name|TEST_USER0_DN
block|,
name|TEST_USER1_DN
block|,
name|TEST_USER2_DN
block|,
name|TEST_USER3_DN
block|,
name|TEST_USER4_DN
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_GROUP2_DN
init|=
literal|"cn=HMS Victory,ou=crews,ou=groups,ou=system"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_GROUP2_NAME
init|=
literal|"HMS Victory"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_GROUP3_DN
init|=
literal|"cn=HMS Bounty,ou=crews,ou=groups,ou=system"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_GROUP3_NAME
init|=
literal|"HMS Bounty"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|TEST_USER0_GROUPS
init|=
block|{
name|TEST_GROUP1_DN
block|,
name|TEST_GROUP2_DN
block|,
name|TEST_GROUP3_DN
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|TEST_USER1_GROUPS
init|=
block|{
name|TEST_GROUP1_DN
block|}
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testGetUserByRef
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
name|TEST_USER1_DN
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
literal|"User instance"
argument_list|,
name|id
operator|instanceof
name|ExternalUser
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"User ID"
argument_list|,
name|TEST_USER1_UID
argument_list|,
name|id
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetUserByUserId
parameter_list|()
throws|throws
name|Exception
block|{
name|ExternalUser
name|user
init|=
name|idp
operator|.
name|getUser
argument_list|(
name|TEST_USER1_UID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"User 1 must exist"
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"User Ref"
argument_list|,
name|TEST_USER1_DN
argument_list|,
name|user
operator|.
name|getExternalId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthenticate
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleCredentials
name|creds
init|=
operator|new
name|SimpleCredentials
argument_list|(
name|TEST_USER1_UID
argument_list|,
literal|"pass"
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
name|ExternalUser
name|user
init|=
name|idp
operator|.
name|authenticate
argument_list|(
name|creds
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"User 1 must authenticate"
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"User Ref"
argument_list|,
name|TEST_USER1_DN
argument_list|,
name|user
operator|.
name|getExternalId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthenticateFail
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleCredentials
name|creds
init|=
operator|new
name|SimpleCredentials
argument_list|(
name|TEST_USER1_UID
argument_list|,
literal|"foobar"
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|idp
operator|.
name|authenticate
argument_list|(
name|creds
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Authenticate must fail with LoginException for wrong password"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
comment|// ok
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthenticateMissing
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleCredentials
name|creds
init|=
operator|new
name|SimpleCredentials
argument_list|(
literal|"foobar"
operator|+
name|TEST_USER1_UID
argument_list|,
literal|"pass"
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
name|ExternalUser
name|user
init|=
name|idp
operator|.
name|authenticate
argument_list|(
name|creds
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"Authenticate must return NULL for unknown user"
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetUserByForeignRef
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
name|TEST_USER1_DN
argument_list|,
literal|"foobar"
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
name|assertNull
argument_list|(
literal|"Foreign ref must be null"
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetUnknownUserByRef
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
literal|"bla=foo,"
operator|+
name|TEST_USER1_DN
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
name|assertNull
argument_list|(
literal|"Unknown user must return null"
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetGroupByRef
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
name|TEST_GROUP1_DN
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
name|assertEquals
argument_list|(
literal|"Group Name"
argument_list|,
name|TEST_GROUP1_NAME
argument_list|,
name|id
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetGroupByName
parameter_list|()
throws|throws
name|Exception
block|{
name|ExternalGroup
name|group
init|=
name|idp
operator|.
name|getGroup
argument_list|(
name|TEST_GROUP1_NAME
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Group 1 must exist"
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Group Ref"
argument_list|,
name|TEST_GROUP1_DN
argument_list|,
name|group
operator|.
name|getExternalId
argument_list|()
operator|.
name|getId
argument_list|()
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
name|TEST_GROUP1_DN
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
name|TEST_GROUP1_MEMBERS
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
specifier|public
name|void
name|testGetGroups
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
name|TEST_USER1_DN
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
literal|"User instance"
argument_list|,
name|id
operator|instanceof
name|ExternalUser
argument_list|)
expr_stmt|;
name|assertIfEquals
argument_list|(
literal|"Groups"
argument_list|,
name|TEST_USER1_GROUPS
argument_list|,
name|id
operator|.
name|getDeclaredGroups
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetGroups2
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
name|TEST_USER0_DN
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
literal|"User instance"
argument_list|,
name|id
operator|instanceof
name|ExternalUser
argument_list|)
expr_stmt|;
name|assertIfEquals
argument_list|(
literal|"Groups"
argument_list|,
name|TEST_USER0_GROUPS
argument_list|,
name|id
operator|.
name|getDeclaredGroups
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNullIntermediatePath
parameter_list|()
throws|throws
name|Exception
block|{
name|providerConfig
operator|.
name|getUserConfig
argument_list|()
operator|.
name|setMakeDnPath
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ExternalUser
name|user
init|=
name|idp
operator|.
name|getUser
argument_list|(
name|TEST_USER1_UID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"User 1 must exist"
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Intermediate path must be null"
argument_list|,
name|user
operator|.
name|getIntermediatePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSplitDNIntermediatePath
parameter_list|()
throws|throws
name|Exception
block|{
name|providerConfig
operator|.
name|getUserConfig
argument_list|()
operator|.
name|setMakeDnPath
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ExternalUser
name|user
init|=
name|idp
operator|.
name|getUser
argument_list|(
name|TEST_USER1_UID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"User 1 must exist"
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Intermediate path must be the split dn"
argument_list|,
name|TEST_USER1_PATH
argument_list|,
name|user
operator|.
name|getIntermediatePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSplitDNIntermediatePath2
parameter_list|()
throws|throws
name|Exception
block|{
name|providerConfig
operator|.
name|getUserConfig
argument_list|()
operator|.
name|setMakeDnPath
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ExternalUser
name|user
init|=
name|idp
operator|.
name|getUser
argument_list|(
name|TEST_USER5_UID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"User 5 must exist"
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Intermediate path must be the split dn"
argument_list|,
name|TEST_USER5_PATH
argument_list|,
name|user
operator|.
name|getIntermediatePath
argument_list|()
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

