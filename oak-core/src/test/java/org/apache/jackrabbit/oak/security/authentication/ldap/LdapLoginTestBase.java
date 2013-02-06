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
name|HashMap
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
name|AppConfigurationEntry
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
name|Configuration
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|ExternalLoginModule
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
name|SyncMode
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
name|AbstractSecurityTest
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
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|options
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
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
name|Override
specifier|protected
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
operator|new
name|Configuration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AppConfigurationEntry
index|[]
name|getAppConfigurationEntry
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
operator|new
name|AppConfigurationEntry
index|[]
block|{
operator|new
name|AppConfigurationEntry
argument_list|(
name|LdapLoginModule
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|REQUIRED
argument_list|,
name|options
argument_list|)
block|}
return|;
block|}
block|}
return|;
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
block|}
name|options
operator|.
name|put
argument_list|(
name|LdapSettings
operator|.
name|KEY_HOST
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|LdapSettings
operator|.
name|KEY_PORT
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|LDAP_SERVER
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|LdapSettings
operator|.
name|KEY_AUTHDN
argument_list|,
name|ServerDNConstants
operator|.
name|ADMIN_SYSTEM_DN
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|LdapSettings
operator|.
name|KEY_AUTHPW
argument_list|,
name|InternalLdapServer
operator|.
name|ADMIN_PW
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|LdapSettings
operator|.
name|KEY_USERROOT
argument_list|,
name|ServerDNConstants
operator|.
name|USERS_SYSTEM_DN
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|LdapSettings
operator|.
name|KEY_GROUPROOT
argument_list|,
name|ServerDNConstants
operator|.
name|GROUPS_SYSTEM_DN
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|LdapSettings
operator|.
name|KEY_AUTOCREATEUSER
operator|+
name|USER_ATTR
argument_list|,
name|USER_PROP
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|LdapSettings
operator|.
name|KEY_AUTOCREATEGROUP
operator|+
name|InternalLdapServer
operator|.
name|GROUP_MEMBER_ATTR
argument_list|,
name|GROUP_PROP
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|LdapSettings
operator|.
name|KEY_GROUPFILTER
argument_list|,
literal|"(objectclass="
operator|+
name|InternalLdapServer
operator|.
name|GROUP_CLASS_ATTR
operator|+
literal|')'
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|LdapSettings
operator|.
name|KEY_GROUPMEMBERSHIPATTRIBUTE
argument_list|,
name|InternalLdapServer
operator|.
name|GROUP_MEMBER_ATTR
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|ExternalLoginModule
operator|.
name|PARAM_SYNC_MODE
argument_list|,
name|SyncMode
operator|.
name|CREATE_USER
argument_list|)
expr_stmt|;
name|userManager
operator|=
name|securityProvider
operator|.
name|getUserConfiguration
argument_list|()
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
name|Test
specifier|public
name|void
name|testLoginFailed
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
name|createLdapFixture
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
operator|!
name|USE_COMMON_LDAP_FIXTURE
condition|)
block|{
name|createLdapFixture
argument_list|()
expr_stmt|;
block|}
name|options
operator|.
name|put
argument_list|(
name|ExternalLoginModule
operator|.
name|PARAM_SYNC_MODE
argument_list|,
name|SyncMode
operator|.
name|CREATE_USER
argument_list|)
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
if|if
condition|(
operator|!
name|USE_COMMON_LDAP_FIXTURE
condition|)
block|{
name|createLdapFixture
argument_list|()
expr_stmt|;
block|}
name|options
operator|.
name|put
argument_list|(
name|ExternalLoginModule
operator|.
name|PARAM_SYNC_MODE
argument_list|,
name|SyncMode
operator|.
name|CREATE_GROUP
argument_list|)
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
name|testSyncCreateUserAndGroups
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
name|createLdapFixture
argument_list|()
expr_stmt|;
block|}
name|options
operator|.
name|put
argument_list|(
name|ExternalLoginModule
operator|.
name|PARAM_SYNC_MODE
argument_list|,
operator|new
name|String
index|[]
block|{
name|SyncMode
operator|.
name|CREATE_USER
block|,
name|SyncMode
operator|.
name|CREATE_GROUP
block|}
argument_list|)
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
name|Authorizable
name|group
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|GROUP_DN
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|group
operator|.
name|hasProperty
argument_list|(
name|GROUP_PROP
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|group
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
name|testNoSync
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
name|createLdapFixture
argument_list|()
expr_stmt|;
block|}
name|options
operator|.
name|put
argument_list|(
name|ExternalLoginModule
operator|.
name|PARAM_SYNC_MODE
argument_list|,
literal|""
argument_list|)
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
block|}
block|}
end_class

end_unit

