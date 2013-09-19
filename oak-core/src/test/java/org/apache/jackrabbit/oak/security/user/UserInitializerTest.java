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
name|PrivilegedExceptionAction
import|;
end_import

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
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|GuestCredentials
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
name|Subject
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|Oak
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
name|ContentRepository
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
name|index
operator|.
name|IndexConstants
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
name|index
operator|.
name|property
operator|.
name|PropertyIndexEditorProvider
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
name|index
operator|.
name|property
operator|.
name|PropertyIndexProvider
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
name|nodetype
operator|.
name|RegistrationEditorProvider
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
name|nodetype
operator|.
name|write
operator|.
name|InitialContent
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
name|SecurityProviderImpl
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
name|SystemSubject
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
name|ConfigurationParameters
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
name|SecurityProvider
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
name|AdminPrincipal
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
name|util
operator|.
name|UserUtil
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
name|util
operator|.
name|TreeUtil
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
name|assertArrayEquals
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

begin_comment
comment|/**  * @since OAK 1.0  */
end_comment

begin_class
specifier|public
class|class
name|UserInitializerTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|UserManager
name|userMgr
decl_stmt|;
specifier|private
name|ConfigurationParameters
name|config
decl_stmt|;
annotation|@
name|Override
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
name|getUserManager
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|config
operator|=
name|getUserConfiguration
argument_list|()
operator|.
name|getParameters
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBuildInUserExist
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|UserUtil
operator|.
name|getAdminId
argument_list|(
name|config
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|UserUtil
operator|.
name|getAnonymousId
argument_list|(
name|config
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAdminUser
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|UserUtil
operator|.
name|getAdminId
argument_list|(
name|config
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|isGroup
argument_list|()
argument_list|)
expr_stmt|;
name|User
name|admin
init|=
operator|(
name|User
operator|)
name|a
decl_stmt|;
name|assertTrue
argument_list|(
name|admin
operator|.
name|isAdmin
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|admin
operator|.
name|getPrincipal
argument_list|()
operator|instanceof
name|AdminPrincipal
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|admin
operator|.
name|getPrincipal
argument_list|()
operator|instanceof
name|TreeBasedPrincipal
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|admin
operator|.
name|getID
argument_list|()
argument_list|,
name|admin
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
name|testAnonymous
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|UserUtil
operator|.
name|getAnonymousId
argument_list|(
name|config
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|isGroup
argument_list|()
argument_list|)
expr_stmt|;
name|User
name|anonymous
init|=
operator|(
name|User
operator|)
name|a
decl_stmt|;
name|assertFalse
argument_list|(
name|anonymous
operator|.
name|isAdmin
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|anonymous
operator|.
name|getPrincipal
argument_list|()
operator|instanceof
name|AdminPrincipal
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|anonymous
operator|.
name|getPrincipal
argument_list|()
operator|instanceof
name|TreeBasedPrincipal
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|anonymous
operator|.
name|getID
argument_list|()
argument_list|,
name|anonymous
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
name|testUserContent
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|UserUtil
operator|.
name|getAdminId
argument_list|(
name|config
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|a
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|UserUtil
operator|.
name|getAnonymousId
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|a
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserIndexDefinitions
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|oakIndex
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|'/'
operator|+
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|oakIndex
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Tree
name|id
init|=
name|oakIndex
operator|.
name|getChild
argument_list|(
literal|"authorizableId"
argument_list|)
decl_stmt|;
name|assertIndexDefinition
argument_list|(
name|id
argument_list|,
name|UserConstants
operator|.
name|REP_AUTHORIZABLE_ID
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Tree
name|princName
init|=
name|oakIndex
operator|.
name|getChild
argument_list|(
literal|"principalName"
argument_list|)
decl_stmt|;
name|assertIndexDefinition
argument_list|(
name|princName
argument_list|,
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|declaringNtNames
init|=
name|TreeUtil
operator|.
name|getStrings
argument_list|(
name|princName
argument_list|,
name|IndexConstants
operator|.
name|DECLARING_NODE_TYPES
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
name|UserConstants
operator|.
name|NT_REP_AUTHORIZABLE
block|}
argument_list|,
name|Iterables
operator|.
name|toArray
argument_list|(
name|declaringNtNames
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|members
init|=
name|oakIndex
operator|.
name|getChild
argument_list|(
literal|"members"
argument_list|)
decl_stmt|;
name|assertIndexDefinition
argument_list|(
name|members
argument_list|,
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * @since OAK 1.0 The configuration defines if the password of the      * admin user is being set.      */
annotation|@
name|Test
specifier|public
name|void
name|testAdminConfiguration
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|userParams
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|userParams
operator|.
name|put
argument_list|(
name|UserConstants
operator|.
name|PARAM_ADMIN_ID
argument_list|,
literal|"admin"
argument_list|)
expr_stmt|;
name|userParams
operator|.
name|put
argument_list|(
name|UserConstants
operator|.
name|PARAM_OMIT_ADMIN_PW
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ConfigurationParameters
name|params
init|=
operator|new
name|ConfigurationParameters
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|,
operator|new
name|ConfigurationParameters
argument_list|(
name|userParams
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SecurityProvider
name|sp
init|=
operator|new
name|SecurityProviderImpl
argument_list|(
name|params
argument_list|)
decl_stmt|;
specifier|final
name|ContentRepository
name|repo
init|=
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|PropertyIndexEditorProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|PropertyIndexProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|RegistrationEditorProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|sp
argument_list|)
operator|.
name|createContentRepository
argument_list|()
decl_stmt|;
name|ContentSession
name|cs
init|=
name|Subject
operator|.
name|doAs
argument_list|(
name|SystemSubject
operator|.
name|INSTANCE
argument_list|,
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|ContentSession
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ContentSession
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|repo
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|root
init|=
name|cs
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|UserConfiguration
name|uc
init|=
name|sp
operator|.
name|getConfiguration
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|UserManager
name|umgr
init|=
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
decl_stmt|;
name|Authorizable
name|adminUser
init|=
name|umgr
operator|.
name|getAuthorizable
argument_list|(
literal|"admin"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|adminUser
argument_list|)
expr_stmt|;
name|Tree
name|adminTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|adminUser
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|adminTree
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|adminTree
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// login as admin should fail
name|ContentSession
name|adminSession
init|=
literal|null
decl_stmt|;
try|try
block|{
name|adminSession
operator|=
name|repo
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
comment|//success
block|}
finally|finally
block|{
if|if
condition|(
name|adminSession
operator|!=
literal|null
condition|)
block|{
name|adminSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @since OAK 1.0 The anonymous user is optional.      */
annotation|@
name|Test
specifier|public
name|void
name|testAnonymousConfiguration
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|userParams
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|userParams
operator|.
name|put
argument_list|(
name|UserConstants
operator|.
name|PARAM_ANONYMOUS_ID
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|ConfigurationParameters
name|params
init|=
operator|new
name|ConfigurationParameters
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|,
operator|new
name|ConfigurationParameters
argument_list|(
name|userParams
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SecurityProvider
name|sp
init|=
operator|new
name|SecurityProviderImpl
argument_list|(
name|params
argument_list|)
decl_stmt|;
specifier|final
name|ContentRepository
name|repo
init|=
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|PropertyIndexEditorProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|PropertyIndexProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|RegistrationEditorProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|sp
argument_list|)
operator|.
name|createContentRepository
argument_list|()
decl_stmt|;
name|ContentSession
name|cs
init|=
name|Subject
operator|.
name|doAs
argument_list|(
name|SystemSubject
operator|.
name|INSTANCE
argument_list|,
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|ContentSession
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ContentSession
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|repo
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|root
init|=
name|cs
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|UserConfiguration
name|uc
init|=
name|sp
operator|.
name|getConfiguration
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|UserManager
name|umgr
init|=
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
decl_stmt|;
name|Authorizable
name|anonymous
init|=
name|umgr
operator|.
name|getAuthorizable
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_ANONYMOUS_ID
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|anonymous
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// login as admin should fail
name|ContentSession
name|anonymousSession
init|=
literal|null
decl_stmt|;
try|try
block|{
name|anonymousSession
operator|=
name|repo
operator|.
name|login
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
comment|//success
block|}
finally|finally
block|{
if|if
condition|(
name|anonymousSession
operator|!=
literal|null
condition|)
block|{
name|anonymousSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|assertIndexDefinition
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|String
name|propName
parameter_list|,
name|boolean
name|isUnique
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|tree
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|isUnique
argument_list|,
name|TreeUtil
operator|.
name|getBoolean
argument_list|(
name|tree
argument_list|,
name|IndexConstants
operator|.
name|UNIQUE_PROPERTY_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|propName
argument_list|,
operator|new
name|String
index|[]
block|{
name|propName
block|}
argument_list|,
name|Iterables
operator|.
name|toArray
argument_list|(
name|TreeUtil
operator|.
name|getStrings
argument_list|(
name|tree
argument_list|,
name|IndexConstants
operator|.
name|PROPERTY_NAMES
argument_list|)
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

