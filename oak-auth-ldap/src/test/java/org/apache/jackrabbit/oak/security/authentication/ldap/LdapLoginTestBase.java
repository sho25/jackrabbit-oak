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
name|ArrayList
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
name|api
operator|.
name|AuthInfo
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
name|ContentSession
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
name|namepath
operator|.
name|NamePathMapper
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
name|value
operator|.
name|ValueFactoryImpl
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
name|ExternalLoginModuleTestBase
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
name|impl
operator|.
name|DefaultSyncConfig
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
name|PrincipalConfiguration
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
name|PrincipalProvider
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
specifier|abstract
class|class
name|LdapLoginTestBase
extends|extends
name|ExternalLoginModuleTestBase
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
specifier|final
name|String
name|USER_ATTR
init|=
literal|"givenName"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|USER_PROP
init|=
literal|"profile/name"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|GROUP_PROP
init|=
literal|"profile/member"
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
name|int
name|NUM_CONCURRENT_LOGINS
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
name|String
index|[]
name|CONCURRENT_TEST_USERS
init|=
operator|new
name|String
index|[
name|NUM_CONCURRENT_LOGINS
index|]
decl_stmt|;
specifier|private
specifier|static
name|String
index|[]
name|CONCURRENT_GROUP_TEST_USERS
init|=
operator|new
name|String
index|[
name|NUM_CONCURRENT_LOGINS
index|]
decl_stmt|;
comment|//initialize LDAP server only once (fast, but might turn out to be not sufficiently flexible in the future)
specifier|protected
specifier|static
specifier|final
name|boolean
name|USE_COMMON_LDAP_FIXTURE
init|=
literal|true
decl_stmt|;
specifier|protected
name|UserManager
name|userManager
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
name|createLdapFixture
argument_list|()
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
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
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
name|createLdapFixture
argument_list|()
expr_stmt|;
block|}
name|UserConfiguration
name|uc
init|=
name|securityProvider
operator|.
name|getConfiguration
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|userManager
operator|=
name|uc
operator|.
name|getUserManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
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
try|try
block|{
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
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
if|if
condition|(
name|GROUP_DN
operator|!=
literal|null
condition|)
block|{
name|a
operator|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|GROUP_DN
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
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
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
name|Override
specifier|protected
name|void
name|setSyncConfig
parameter_list|(
name|DefaultSyncConfig
name|cfg
parameter_list|)
block|{
if|if
condition|(
name|cfg
operator|!=
literal|null
condition|)
block|{
name|cfg
operator|.
name|user
argument_list|()
operator|.
name|getPropertyMapping
argument_list|()
operator|.
name|put
argument_list|(
name|USER_PROP
argument_list|,
name|USER_ATTR
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|setSyncConfig
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ExternalIdentityProvider
name|createIDP
parameter_list|()
block|{
name|LdapProviderConfig
name|cfg
init|=
operator|new
name|LdapProviderConfig
argument_list|()
operator|.
name|setName
argument_list|(
literal|"ldap"
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
name|InternalLdapServer
operator|.
name|GROUP_MEMBER_ATTR
argument_list|)
decl_stmt|;
name|cfg
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
name|cfg
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
name|InternalLdapServer
operator|.
name|GROUP_CLASS_ATTR
argument_list|)
expr_stmt|;
name|LdapIdentityProvider
name|ldapIDP
init|=
operator|new
name|LdapIdentityProvider
argument_list|(
name|cfg
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
annotation|@
name|Override
specifier|protected
name|void
name|destroyIDP
parameter_list|(
name|ExternalIdentityProvider
name|idp
parameter_list|)
block|{
operator|(
operator|(
name|LdapIdentityProvider
operator|)
name|idp
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Null login must fail.      *      * @throws Exception      * @see org.apache.jackrabbit.oak.security.authentication.ldap.GuestTokenDefaultLdapLoginModuleTest      */
annotation|@
name|Test
specifier|public
name|void
name|testNullLogin
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|login
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected null login to fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoginFailed
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|ContentSession
name|cs
init|=
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"login failure expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|assertNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncCreateUser
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
name|USER_PWD
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|Authorizable
name|user
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|user
operator|.
name|hasProperty
argument_list|(
name|USER_PROP
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|userTree
init|=
name|cs
operator|.
name|getLatestRoot
argument_list|()
operator|.
name|getTree
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|userTree
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|GROUP_DN
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|options
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncCreateGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
name|USER_PWD
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|GROUP_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|options
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyncUpdate
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create user upfront in order to test update mode
name|Authorizable
name|user
init|=
name|userManager
operator|.
name|createUser
argument_list|(
name|USER_ID
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ExternalUser
name|externalUser
init|=
name|idp
operator|.
name|getUser
argument_list|(
name|USER_ID
argument_list|)
decl_stmt|;
name|user
operator|.
name|setProperty
argument_list|(
literal|"rep:externalId"
argument_list|,
operator|new
name|ValueFactoryImpl
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
operator|.
name|createValue
argument_list|(
name|externalUser
operator|.
name|getExternalId
argument_list|()
operator|.
name|getString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
name|USER_PWD
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|user
operator|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|user
operator|.
name|hasProperty
argument_list|(
name|USER_PROP
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|GROUP_DN
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|options
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoginSetsAuthInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|SimpleCredentials
name|sc
init|=
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
name|USER_PWD
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|"attr"
argument_list|,
literal|"val"
argument_list|)
expr_stmt|;
name|cs
operator|=
name|login
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|AuthInfo
name|ai
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|USER_ID
argument_list|,
name|ai
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"val"
argument_list|,
name|ai
operator|.
name|getAttribute
argument_list|(
literal|"attr"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPrincipalsFromAuthInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|SimpleCredentials
name|sc
init|=
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
name|USER_PWD
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|"attr"
argument_list|,
literal|"val"
argument_list|)
expr_stmt|;
name|cs
operator|=
name|login
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|AuthInfo
name|ai
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
decl_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|PrincipalProvider
name|pp
init|=
name|getSecurityProvider
argument_list|()
operator|.
name|getConfiguration
argument_list|(
name|PrincipalConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getPrincipalProvider
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|expected
init|=
name|pp
operator|.
name|getPrincipals
argument_list|(
name|USER_ID
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|expected
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|ai
operator|.
name|getPrincipals
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReLogin
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
name|USER_PWD
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|Authorizable
name|user
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|USER_ID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
argument_list|)
expr_stmt|;
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// login again
name|cs
operator|=
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
name|USER_PWD
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|USER_ID
argument_list|,
name|cs
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|options
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConcurrentLogin
parameter_list|()
throws|throws
name|Exception
block|{
name|concurrentLogin
argument_list|(
name|CONCURRENT_TEST_USERS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-1679"
argument_list|)
comment|// FIXME OAK-1679
annotation|@
name|Test
specifier|public
name|void
name|testConcurrentLoginSameGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|concurrentLogin
argument_list|(
name|CONCURRENT_GROUP_TEST_USERS
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|concurrentLogin
parameter_list|(
name|String
index|[]
name|users
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|Exception
argument_list|>
name|exceptions
init|=
operator|new
name|ArrayList
argument_list|<
name|Exception
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|workers
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|userId
range|:
name|users
control|)
block|{
specifier|final
name|String
name|uid
init|=
name|userId
decl_stmt|;
name|workers
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|uid
argument_list|,
name|USER_PWD
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|workers
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|workers
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Exception
name|e
range|:
name|exceptions
control|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|exceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
name|exceptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
throw|;
block|}
block|}
specifier|protected
specifier|static
name|void
name|createLdapFixture
parameter_list|()
throws|throws
name|Exception
block|{
name|LDAP_SERVER
operator|.
name|addMember
argument_list|(
name|GROUP_DN
operator|=
name|LDAP_SERVER
operator|.
name|addGroup
argument_list|(
name|GROUP_NAME
argument_list|)
argument_list|,
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
name|NUM_CONCURRENT_LOGINS
operator|*
literal|2
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
name|LDAP_SERVER
operator|.
name|addUser
argument_list|(
name|userId
argument_list|,
literal|"test"
argument_list|,
name|userId
argument_list|,
name|USER_PWD
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|CONCURRENT_GROUP_TEST_USERS
index|[
name|i
operator|/
literal|2
index|]
operator|=
name|userId
expr_stmt|;
name|LDAP_SERVER
operator|.
name|addMember
argument_list|(
name|GROUP_DN
argument_list|,
name|userDN
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|CONCURRENT_TEST_USERS
index|[
name|i
operator|/
literal|2
index|]
operator|=
name|userId
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

