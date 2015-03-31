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
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|security
operator|.
name|AccessControlEntry
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
name|AccessControlList
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
name|AccessControlPolicy
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
name|AccessControlAction
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
name|xml
operator|.
name|ImportBehavior
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

begin_class
specifier|public
class|class
name|UserImportWithActionsTest
extends|extends
name|AbstractImportTest
block|{
specifier|private
specifier|final
name|TestActionProvider
name|actionProvider
init|=
operator|new
name|TestActionProvider
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|String
name|getImportBehavior
parameter_list|()
block|{
return|return
name|ImportBehavior
operator|.
name|NAME_BESTEFFORT
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getTargetPath
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|private
name|void
name|setAuthorizableActions
parameter_list|(
name|AuthorizableAction
name|action
parameter_list|)
block|{
name|actionProvider
operator|.
name|addAction
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ConfigurationParameters
name|getConfigurationParameters
parameter_list|()
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
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|userParams
operator|.
name|put
argument_list|(
name|UserConstants
operator|.
name|PARAM_AUTHORIZABLE_ACTION_PROVIDER
argument_list|,
name|actionProvider
argument_list|)
expr_stmt|;
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|userParams
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testActionExecutionForUser
parameter_list|()
throws|throws
name|Exception
block|{
name|TestAction
name|testAction
init|=
operator|new
name|TestAction
argument_list|()
decl_stmt|;
name|setAuthorizableActions
argument_list|(
name|testAction
argument_list|)
expr_stmt|;
comment|// import user
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<sv:node sv:name=\"t\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:User</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>e358efa4-89f5-3062-b10d-d7316b65649e</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:password\" sv:type=\"String\"><sv:value>pw</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>tPrincipal</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|doImport
argument_list|(
name|USERPATH
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testAction
operator|.
name|id
argument_list|,
literal|"t"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testAction
operator|.
name|pw
argument_list|,
literal|"pw"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testActionExecutionForGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|TestAction
name|testAction
init|=
operator|new
name|TestAction
argument_list|()
decl_stmt|;
name|setAuthorizableActions
argument_list|(
name|testAction
argument_list|)
expr_stmt|;
comment|// import group
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<sv:node sv:name=\"g\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>b2f5ff47-4366-31b6-a533-d8dc3614845d</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>gPrincipal</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|doImport
argument_list|(
name|GROUPPATH
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testAction
operator|.
name|id
argument_list|,
literal|"g"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|testAction
operator|.
name|pw
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAccessControlActionExecutionForUser
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessControlAction
name|a1
init|=
operator|new
name|AccessControlAction
argument_list|()
decl_stmt|;
name|a1
operator|.
name|init
argument_list|(
name|securityProvider
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|AccessControlAction
operator|.
name|USER_PRIVILEGE_NAMES
argument_list|,
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_ALL
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|setAuthorizableActions
argument_list|(
name|a1
argument_list|)
expr_stmt|;
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<sv:node sv:name=\"t\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:User</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>e358efa4-89f5-3062-b10d-d7316b65649e</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:password\" sv:type=\"String\"><sv:value>{sha1}8efd86fb78a56a5145ed7739dcb00c78581c5375</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>tPrincipal</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|doImport
argument_list|(
name|USERPATH
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"t"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|isGroup
argument_list|()
argument_list|)
expr_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|adminSession
operator|.
name|getAccessControlManager
argument_list|()
decl_stmt|;
name|AccessControlPolicy
index|[]
name|policies
init|=
name|acMgr
operator|.
name|getPolicies
argument_list|(
name|a
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|policies
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|policies
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|policies
index|[
literal|0
index|]
operator|instanceof
name|AccessControlList
argument_list|)
expr_stmt|;
name|AccessControlEntry
index|[]
name|aces
init|=
operator|(
operator|(
name|AccessControlList
operator|)
name|policies
index|[
literal|0
index|]
operator|)
operator|.
name|getAccessControlEntries
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|aces
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tPrincipal"
argument_list|,
name|aces
index|[
literal|0
index|]
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
name|testAccessControlActionExecutionForUser2
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessControlAction
name|a1
init|=
operator|new
name|AccessControlAction
argument_list|()
decl_stmt|;
name|a1
operator|.
name|init
argument_list|(
name|securityProvider
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|AccessControlAction
operator|.
name|USER_PRIVILEGE_NAMES
argument_list|,
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_ALL
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|setAuthorizableActions
argument_list|(
name|a1
argument_list|)
expr_stmt|;
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<sv:node sv:name=\"t\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:User</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>e358efa4-89f5-3062-b10d-d7316b65649e</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>tPrincipal</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:password\" sv:type=\"String\"><sv:value>{sha1}8efd86fb78a56a5145ed7739dcb00c78581c5375</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|doImport
argument_list|(
name|USERPATH
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"t"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|isGroup
argument_list|()
argument_list|)
expr_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|adminSession
operator|.
name|getAccessControlManager
argument_list|()
decl_stmt|;
name|AccessControlPolicy
index|[]
name|policies
init|=
name|acMgr
operator|.
name|getPolicies
argument_list|(
name|a
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|policies
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|policies
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|policies
index|[
literal|0
index|]
operator|instanceof
name|AccessControlList
argument_list|)
expr_stmt|;
name|AccessControlEntry
index|[]
name|aces
init|=
operator|(
operator|(
name|AccessControlList
operator|)
name|policies
index|[
literal|0
index|]
operator|)
operator|.
name|getAccessControlEntries
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|aces
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tPrincipal"
argument_list|,
name|aces
index|[
literal|0
index|]
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
name|testAccessControlActionExecutionForGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessControlAction
name|a1
init|=
operator|new
name|AccessControlAction
argument_list|()
decl_stmt|;
name|a1
operator|.
name|init
argument_list|(
name|securityProvider
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|AccessControlAction
operator|.
name|GROUP_PRIVILEGE_NAMES
argument_list|,
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_READ
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|setAuthorizableActions
argument_list|(
name|a1
argument_list|)
expr_stmt|;
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<sv:node sv:name=\"g\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>b2f5ff47-4366-31b6-a533-d8dc3614845d</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>gPrincipal</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|doImport
argument_list|(
name|GROUPPATH
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"g"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|isGroup
argument_list|()
argument_list|)
expr_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|adminSession
operator|.
name|getAccessControlManager
argument_list|()
decl_stmt|;
name|AccessControlPolicy
index|[]
name|policies
init|=
name|acMgr
operator|.
name|getPolicies
argument_list|(
name|a
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|policies
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|policies
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|policies
index|[
literal|0
index|]
operator|instanceof
name|AccessControlList
argument_list|)
expr_stmt|;
name|AccessControlEntry
index|[]
name|aces
init|=
operator|(
operator|(
name|AccessControlList
operator|)
name|policies
index|[
literal|0
index|]
operator|)
operator|.
name|getAccessControlEntries
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|aces
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"gPrincipal"
argument_list|,
name|aces
index|[
literal|0
index|]
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
class|class
name|TestActionProvider
implements|implements
name|AuthorizableActionProvider
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|AuthorizableAction
argument_list|>
name|actions
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
name|void
name|addAction
parameter_list|(
name|AuthorizableAction
name|action
parameter_list|)
block|{
name|actions
operator|.
name|add
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
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
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
return|return
name|actions
return|;
block|}
block|}
specifier|private
specifier|final
class|class
name|TestAction
implements|implements
name|AuthorizableAction
block|{
specifier|private
name|String
name|id
decl_stmt|;
specifier|private
name|String
name|pw
decl_stmt|;
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
name|id
operator|=
name|group
operator|.
name|getID
argument_list|()
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
name|id
operator|=
name|user
operator|.
name|getID
argument_list|()
expr_stmt|;
name|pw
operator|=
name|password
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
block|{
comment|// ignore
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
block|{
name|pw
operator|=
name|newPassword
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

