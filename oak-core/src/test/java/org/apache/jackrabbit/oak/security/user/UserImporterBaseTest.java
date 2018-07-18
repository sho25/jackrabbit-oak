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
name|javax
operator|.
name|jcr
operator|.
name|ImportUUIDBehavior
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|PropertyDefinition
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
name|ImmutableList
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
name|Lists
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
name|JcrConstants
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
name|api
operator|.
name|Type
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
name|commons
operator|.
name|PathUtils
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
name|nodetype
operator|.
name|ReadOnlyNodeTypeManager
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
name|action
operator|.
name|AuthorizableAction
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
name|action
operator|.
name|AuthorizableActionProvider
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
name|action
operator|.
name|GroupAction
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
name|xml
operator|.
name|ImportBehavior
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
name|xml
operator|.
name|NodeInfo
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
name|xml
operator|.
name|PropInfo
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
name|xml
operator|.
name|ProtectedItemImporter
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
name|xml
operator|.
name|ReferenceChangeTracker
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
name|xml
operator|.
name|TextValue
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
name|NodeUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|UserImporterBaseTest
extends|extends
name|AbstractSecurityTest
implements|implements
name|UserConstants
block|{
specifier|static
specifier|final
name|String
name|TEST_USER_ID
init|=
literal|"uid"
decl_stmt|;
specifier|static
specifier|final
name|String
name|TEST_GROUP_ID
init|=
literal|"gid"
decl_stmt|;
name|TestAction
name|testAction
decl_stmt|;
name|AuthorizableActionProvider
name|actionProvider
init|=
operator|new
name|AuthorizableActionProvider
argument_list|()
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|AuthorizableAction
argument_list|>
name|getAuthorizableActions
parameter_list|(
annotation|@
name|NotNull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
return|return
operator|(
name|testAction
operator|==
literal|null
operator|)
condition|?
name|ImmutableList
operator|.
expr|<
name|AuthorizableAction
operator|>
name|of
argument_list|()
else|:
name|ImmutableList
operator|.
name|of
argument_list|(
name|testAction
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|User
name|testUser
decl_stmt|;
name|ReferenceChangeTracker
name|refTracker
init|=
operator|new
name|ReferenceChangeTracker
argument_list|()
decl_stmt|;
name|UserImporter
name|importer
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
name|testUser
operator|=
name|getTestUser
argument_list|()
expr_stmt|;
name|importer
operator|=
operator|new
name|UserImporter
argument_list|(
name|getImportConfig
argument_list|()
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
name|refTracker
operator|.
name|clear
argument_list|()
expr_stmt|;
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
name|ConfigurationParameters
name|getImportConfig
parameter_list|()
block|{
return|return
name|getSecurityConfigParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
return|;
block|}
name|String
name|getImportBehavior
parameter_list|()
block|{
return|return
name|ImportBehavior
operator|.
name|NAME_IGNORE
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
name|ConfigurationParameters
name|userParams
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConstants
operator|.
name|PARAM_AUTHORIZABLE_ACTION_PROVIDER
argument_list|,
name|actionProvider
argument_list|,
name|ProtectedItemImporter
operator|.
name|PARAM_IMPORT_BEHAVIOR
argument_list|,
name|getImportBehavior
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|,
name|userParams
argument_list|)
return|;
block|}
name|Session
name|mockJackrabbitSession
parameter_list|()
throws|throws
name|Exception
block|{
name|JackrabbitSession
name|s
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|JackrabbitSession
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|s
operator|.
name|getUserManager
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|getUserManager
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
name|boolean
name|isWorkspaceImport
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|init
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|init
argument_list|(
literal|false
argument_list|)
return|;
block|}
name|boolean
name|init
parameter_list|(
name|boolean
name|createAction
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|createAction
condition|)
block|{
name|testAction
operator|=
operator|new
name|TestAction
argument_list|()
expr_stmt|;
block|}
return|return
name|importer
operator|.
name|init
argument_list|(
name|mockJackrabbitSession
argument_list|()
argument_list|,
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|isWorkspaceImport
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REMOVE_EXISTING
argument_list|,
name|refTracker
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
return|;
block|}
name|Tree
name|createUserTree
parameter_list|()
block|{
name|Tree
name|folder
init|=
name|root
operator|.
name|getTree
argument_list|(
name|getUserConfiguration
argument_list|()
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|PARAM_USER_PATH
argument_list|,
name|DEFAULT_USER_PATH
argument_list|)
argument_list|)
decl_stmt|;
name|Tree
name|userTree
init|=
name|folder
operator|.
name|addChild
argument_list|(
literal|"userTree"
argument_list|)
decl_stmt|;
name|userTree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_USER
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|userTree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|,
operator|new
name|UserProvider
argument_list|(
name|root
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
operator|.
name|getContentID
argument_list|(
name|TEST_USER_ID
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|userTree
return|;
block|}
name|Tree
name|createSystemUserTree
parameter_list|()
block|{
name|Tree
name|folder
init|=
name|root
operator|.
name|getTree
argument_list|(
name|getUserConfiguration
argument_list|()
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|PARAM_USER_PATH
argument_list|,
name|DEFAULT_USER_PATH
argument_list|)
argument_list|)
decl_stmt|;
name|Tree
name|userTree
init|=
name|folder
operator|.
name|addChild
argument_list|(
literal|"systemUserTree"
argument_list|)
decl_stmt|;
name|userTree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_SYSTEM_USER
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|userTree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|,
operator|new
name|UserProvider
argument_list|(
name|root
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
operator|.
name|getContentID
argument_list|(
name|TEST_USER_ID
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|userTree
return|;
block|}
name|Tree
name|createGroupTree
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|groupPath
init|=
name|getUserConfiguration
argument_list|()
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|PARAM_GROUP_PATH
argument_list|,
name|DEFAULT_GROUP_PATH
argument_list|)
decl_stmt|;
name|NodeUtil
name|node
init|=
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
argument_list|)
decl_stmt|;
name|NodeUtil
name|groupRoot
init|=
name|node
operator|.
name|getOrAddTree
argument_list|(
name|PathUtils
operator|.
name|relativize
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|,
name|groupPath
argument_list|)
argument_list|,
name|NT_REP_AUTHORIZABLE_FOLDER
argument_list|)
decl_stmt|;
name|Tree
name|groupTree
init|=
name|groupRoot
operator|.
name|addChild
argument_list|(
literal|"testGroup"
argument_list|,
name|NT_REP_GROUP
argument_list|)
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|groupTree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|,
operator|new
name|UserProvider
argument_list|(
name|root
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
operator|.
name|getContentID
argument_list|(
name|TEST_GROUP_ID
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|groupTree
return|;
block|}
name|PropInfo
name|createPropInfo
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
specifier|final
name|String
modifier|...
name|values
parameter_list|)
block|{
name|List
argument_list|<
name|TextValue
argument_list|>
name|txtValues
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|v
range|:
name|values
control|)
block|{
name|txtValues
operator|.
name|add
argument_list|(
operator|new
name|TextValue
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
name|v
return|;
block|}
annotation|@
name|Override
specifier|public
name|Value
name|getValue
parameter_list|(
name|int
name|targetType
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|getValueFactory
argument_list|(
name|root
argument_list|)
operator|.
name|createValue
argument_list|(
name|v
argument_list|,
name|targetType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|()
block|{
comment|//nop
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|PropInfo
argument_list|(
name|name
argument_list|,
name|PropertyType
operator|.
name|STRING
argument_list|,
name|txtValues
argument_list|)
return|;
block|}
name|PropertyDefinition
name|mockPropertyDefinition
parameter_list|(
annotation|@
name|NotNull
name|String
name|declaringNt
parameter_list|,
name|boolean
name|mv
parameter_list|)
throws|throws
name|Exception
block|{
name|PropertyDefinition
name|def
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|PropertyDefinition
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|def
operator|.
name|isMultiple
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mv
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|def
operator|.
name|getDeclaringNodeType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ReadOnlyNodeTypeManager
operator|.
name|getInstance
argument_list|(
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
operator|.
name|getNodeType
argument_list|(
name|declaringNt
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|def
return|;
block|}
name|NodeInfo
name|createNodeInfo
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
annotation|@
name|NotNull
name|String
name|primaryTypeName
parameter_list|)
block|{
return|return
operator|new
name|NodeInfo
argument_list|(
name|name
argument_list|,
name|primaryTypeName
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|//--------------------------------------------------------------------------
specifier|final
class|class
name|TestAction
implements|implements
name|AuthorizableAction
implements|,
name|GroupAction
block|{
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|methodCalls
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
name|void
name|clear
parameter_list|()
block|{
name|methodCalls
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|void
name|checkMethods
parameter_list|(
name|String
modifier|...
name|expected
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|expected
argument_list|)
argument_list|,
name|methodCalls
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|(
name|SecurityProvider
name|securityProvider
parameter_list|,
name|ConfigurationParameters
name|config
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|onCreate
parameter_list|(
name|Group
name|group
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|methodCalls
operator|.
name|add
argument_list|(
literal|"onCreate-Group"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onCreate
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|password
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|methodCalls
operator|.
name|add
argument_list|(
literal|"onCreate-User"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onRemove
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|methodCalls
operator|.
name|add
argument_list|(
literal|"onRemove"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onPasswordChange
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|newPassword
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|methodCalls
operator|.
name|add
argument_list|(
literal|"onPasswordChange"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMemberAdded
parameter_list|(
name|Group
name|group
parameter_list|,
name|Authorizable
name|member
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|methodCalls
operator|.
name|add
argument_list|(
literal|"onMemberAdded"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMembersAdded
parameter_list|(
name|Group
name|group
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|memberIds
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|failedIds
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|methodCalls
operator|.
name|add
argument_list|(
literal|"onMembersAdded"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMembersAddedContentId
parameter_list|(
name|Group
name|group
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|memberContentIds
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|failedIds
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|methodCalls
operator|.
name|add
argument_list|(
literal|"onMembersAddedContentId"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMemberRemoved
parameter_list|(
name|Group
name|group
parameter_list|,
name|Authorizable
name|member
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|methodCalls
operator|.
name|add
argument_list|(
literal|"onMemberRemoved"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMembersRemoved
parameter_list|(
name|Group
name|group
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|memberIds
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|failedIds
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|methodCalls
operator|.
name|add
argument_list|(
literal|"onMembersRemoved"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

