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
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
name|List
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|NoSuchWorkspaceException
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
name|jcr
operator|.
name|ValueFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|Privilege
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
name|JackrabbitAccessControlManager
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
name|authorization
operator|.
name|PrivilegeManager
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
name|commit
operator|.
name|ConflictValidatorProvider
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
name|commit
operator|.
name|JcrConflictHandler
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
name|index
operator|.
name|reference
operator|.
name|ReferenceEditorProvider
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
name|reference
operator|.
name|ReferenceIndexProvider
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
name|name
operator|.
name|NamespaceEditorProvider
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
name|TypeEditorProvider
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
name|authentication
operator|.
name|ConfigurationUtil
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
name|authorization
operator|.
name|AuthorizationConfiguration
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
name|privilege
operator|.
name|PrivilegeConfiguration
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
name|util
operator|.
name|UserUtil
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

begin_comment
comment|/**  * AbstractOakTest is the base class for oak test execution.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractSecurityTest
block|{
specifier|private
name|ContentRepository
name|contentRepository
decl_stmt|;
specifier|private
name|UserManager
name|userManager
decl_stmt|;
specifier|private
name|User
name|testUser
decl_stmt|;
specifier|private
name|PrivilegeManager
name|privMgr
decl_stmt|;
specifier|protected
name|NamePathMapper
name|namePathMapper
init|=
name|NamePathMapper
operator|.
name|DEFAULT
decl_stmt|;
specifier|protected
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|protected
name|ContentSession
name|adminSession
decl_stmt|;
specifier|protected
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
name|Oak
name|oak
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
name|JcrConflictHandler
operator|.
name|createJcrConflictHandler
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|NamespaceEditorProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|ReferenceEditorProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|ReferenceIndexProvider
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
name|TypeEditorProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|ConflictValidatorProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
decl_stmt|;
name|withEditors
argument_list|(
name|oak
argument_list|)
expr_stmt|;
name|contentRepository
operator|=
name|oak
operator|.
name|createContentRepository
argument_list|()
expr_stmt|;
name|adminSession
operator|=
name|login
argument_list|(
name|getAdminCredentials
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|=
name|adminSession
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|Configuration
operator|.
name|setConfiguration
argument_list|(
name|getConfiguration
argument_list|()
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
if|if
condition|(
name|testUser
operator|!=
literal|null
condition|)
block|{
name|testUser
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
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
name|Configuration
operator|.
name|setConfiguration
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|SecurityProvider
name|getSecurityProvider
parameter_list|()
block|{
if|if
condition|(
name|securityProvider
operator|==
literal|null
condition|)
block|{
name|securityProvider
operator|=
operator|new
name|SecurityProviderImpl
argument_list|(
name|getSecurityConfigParameters
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|securityProvider
return|;
block|}
specifier|protected
name|Oak
name|withEditors
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
return|return
name|oak
return|;
block|}
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
return|return
name|ConfigurationParameters
operator|.
name|EMPTY
return|;
block|}
specifier|protected
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|ConfigurationUtil
operator|.
name|getDefaultConfiguration
argument_list|(
name|getSecurityConfigParameters
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|ContentSession
name|login
parameter_list|(
annotation|@
name|Nullable
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|LoginException
throws|,
name|NoSuchWorkspaceException
block|{
return|return
name|contentRepository
operator|.
name|login
argument_list|(
name|credentials
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|protected
name|Credentials
name|getAdminCredentials
parameter_list|()
block|{
name|String
name|adminId
init|=
name|UserUtil
operator|.
name|getAdminId
argument_list|(
name|getUserConfiguration
argument_list|()
operator|.
name|getParameters
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|SimpleCredentials
argument_list|(
name|adminId
argument_list|,
name|adminId
operator|.
name|toCharArray
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|NamePathMapper
name|getNamePathMapper
parameter_list|()
block|{
return|return
name|namePathMapper
return|;
block|}
specifier|protected
name|UserConfiguration
name|getUserConfiguration
parameter_list|()
block|{
return|return
name|getConfig
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
return|;
block|}
specifier|protected
name|UserManager
name|getUserManager
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
if|if
condition|(
name|userManager
operator|==
literal|null
condition|)
block|{
name|userManager
operator|=
name|getConfig
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getUserManager
argument_list|(
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|userManager
return|;
block|}
specifier|protected
name|PrincipalManager
name|getPrincipalManager
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
return|return
name|getConfig
argument_list|(
name|PrincipalConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getPrincipalManager
argument_list|(
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|JackrabbitAccessControlManager
name|getAccessControlManager
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
name|AccessControlManager
name|acMgr
init|=
name|getConfig
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getAccessControlManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
name|acMgr
operator|instanceof
name|JackrabbitAccessControlManager
condition|)
block|{
return|return
operator|(
name|JackrabbitAccessControlManager
operator|)
name|acMgr
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Expected JackrabbitAccessControlManager found "
operator|+
name|acMgr
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|Privilege
index|[]
name|privilegesFromNames
parameter_list|(
name|String
modifier|...
name|privilegeNames
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|privilegesFromNames
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|privilegeNames
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|Privilege
index|[]
name|privilegesFromNames
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|privilegeNames
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|PrivilegeManager
name|manager
init|=
name|getPrivilegeManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Privilege
argument_list|>
name|privs
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|privilegeNames
control|)
block|{
name|privs
operator|.
name|add
argument_list|(
name|manager
operator|.
name|getPrivilege
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|privs
operator|.
name|toArray
argument_list|(
operator|new
name|Privilege
index|[
name|privs
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|protected
name|PrivilegeManager
name|getPrivilegeManager
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
if|if
condition|(
name|privMgr
operator|==
literal|null
condition|)
block|{
name|privMgr
operator|=
name|getConfig
argument_list|(
name|PrivilegeConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getPrivilegeManager
argument_list|(
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|privMgr
return|;
block|}
specifier|protected
name|ValueFactory
name|getValueFactory
parameter_list|()
block|{
return|return
operator|new
name|ValueFactoryImpl
argument_list|(
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|long
name|waitForSystemTimeIncrement
parameter_list|(
name|long
name|old
parameter_list|)
block|{
while|while
condition|(
name|old
operator|==
name|System
operator|.
name|currentTimeMillis
argument_list|()
condition|)
block|{
comment|// wait for system timer to move
block|}
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
return|;
block|}
specifier|protected
name|User
name|getTestUser
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|testUser
operator|==
literal|null
condition|)
block|{
name|String
name|uid
init|=
literal|"testUser"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|testUser
operator|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createUser
argument_list|(
name|uid
argument_list|,
name|uid
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
return|return
name|testUser
return|;
block|}
specifier|protected
name|ContentSession
name|createTestSession
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|uid
init|=
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
decl_stmt|;
return|return
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|uid
argument_list|,
name|uid
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|T
name|getConfig
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|configClass
parameter_list|)
block|{
return|return
name|getSecurityProvider
argument_list|()
operator|.
name|getConfiguration
argument_list|(
name|configClass
argument_list|)
return|;
block|}
block|}
end_class

end_unit

