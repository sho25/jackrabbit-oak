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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
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
name|Root
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
name|security
operator|.
name|AbstractSecurityTest
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

begin_comment
comment|/**  * ExternalLoginModuleTest...  */
end_comment

begin_class
specifier|public
class|class
name|ExternalLoginModuleTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
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
specifier|private
name|String
name|userId
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|UserManager
name|userManager
decl_stmt|;
specifier|private
name|Root
name|root
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
name|userId
operator|=
name|TestLoginModule
operator|.
name|externalUser
operator|.
name|getId
argument_list|()
expr_stmt|;
name|ids
operator|.
name|add
argument_list|(
name|userId
argument_list|)
expr_stmt|;
for|for
control|(
name|ExternalGroup
name|group
range|:
name|TestLoginModule
operator|.
name|externalGroups
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|root
operator|=
name|admin
operator|.
name|getLatestRoot
argument_list|()
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
try|try
block|{
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
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
literal|"unknown"
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
name|userId
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
name|userId
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|userId
argument_list|)
condition|)
block|{
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|TestLoginModule
operator|.
name|externalUser
operator|.
name|getProperties
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
name|prop
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|userId
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|assertNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|userId
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|userId
argument_list|)
condition|)
block|{
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|TestLoginModule
operator|.
name|externalUser
operator|.
name|getProperties
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
name|prop
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertNotNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|UPDATE
argument_list|)
expr_stmt|;
comment|// create user upfront in order to test update mode
name|ExternalUser
name|externalUser
init|=
name|TestLoginModule
operator|.
name|externalUser
decl_stmt|;
name|Authorizable
name|user
init|=
name|userManager
operator|.
name|createUser
argument_list|(
name|externalUser
operator|.
name|getId
argument_list|()
argument_list|,
name|externalUser
operator|.
name|getPassword
argument_list|()
argument_list|)
decl_stmt|;
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
name|userId
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|userId
argument_list|)
condition|)
block|{
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|TestLoginModule
operator|.
name|externalUser
operator|.
name|getProperties
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
name|prop
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|testSyncUpdateAndGroups
parameter_list|()
throws|throws
name|Exception
block|{
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
name|UPDATE
block|,
name|SyncMode
operator|.
name|CREATE_GROUP
block|}
argument_list|)
expr_stmt|;
comment|// create user upfront in order to test update mode
name|ExternalUser
name|externalUser
init|=
name|TestLoginModule
operator|.
name|externalUser
decl_stmt|;
name|Authorizable
name|user
init|=
name|userManager
operator|.
name|createUser
argument_list|(
name|externalUser
operator|.
name|getId
argument_list|()
argument_list|,
name|externalUser
operator|.
name|getPassword
argument_list|()
argument_list|)
decl_stmt|;
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
name|userId
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|userId
argument_list|)
condition|)
block|{
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|TestLoginModule
operator|.
name|externalUser
operator|.
name|getProperties
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
name|prop
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertNotNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|testDefaultSync
parameter_list|()
throws|throws
name|Exception
block|{
name|options
operator|.
name|put
argument_list|(
name|ExternalLoginModule
operator|.
name|PARAM_SYNC_MODE
argument_list|,
literal|null
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
name|userId
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|userId
argument_list|)
condition|)
block|{
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|TestLoginModule
operator|.
name|externalUser
operator|.
name|getProperties
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
name|prop
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertNotNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|userId
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|assertNull
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|AppConfigurationEntry
name|entry
init|=
operator|new
name|AppConfigurationEntry
argument_list|(
name|TestLoginModule
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
decl_stmt|;
return|return
operator|new
name|AppConfigurationEntry
index|[]
block|{
name|entry
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

