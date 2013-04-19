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
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|junit
operator|.
name|Ignore
import|;
end_import

begin_comment
comment|/**  * UserImportTest...  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"OAK-414"
argument_list|)
comment|// TODO: OAK-414
specifier|public
class|class
name|UserImportWithActionsTest
extends|extends
name|AbstractUserTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|USERPATH
init|=
literal|"/rep:security/rep:authorizables/rep:users"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|GROUPPATH
init|=
literal|"/rep:security/rep:authorizables/rep:groups"
decl_stmt|;
specifier|private
name|JackrabbitSession
name|jrSession
decl_stmt|;
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
comment|// avoid collision with testing a-folders that may have been created
comment|// with another test (but not removed as user/groups got removed)
name|String
name|path
init|=
name|USERPATH
operator|+
literal|"/t"
decl_stmt|;
if|if
condition|(
name|superuser
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|path
operator|=
name|GROUPPATH
operator|+
literal|"/g"
expr_stmt|;
if|if
condition|(
name|superuser
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|jrSession
operator|=
operator|(
name|JackrabbitSession
operator|)
name|superuser
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|//TODO
try|try
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
block|}
specifier|private
name|void
name|setAuthorizableActions
parameter_list|(
name|AuthorizableAction
name|action
parameter_list|)
block|{
comment|// TODO clarify how to test AuthorizableActions in Oak
comment|// userMgr.setAuthorizableActions(new AuthorizableAction[] {testAction});
block|}
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
try|try
block|{
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
finally|finally
block|{
name|jrSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
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
try|try
block|{
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
finally|finally
block|{
name|jrSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
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
name|setUserPrivilegeNames
argument_list|(
name|Privilege
operator|.
name|JCR_ALL
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
try|try
block|{
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
name|jrSession
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
finally|finally
block|{
name|jrSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
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
name|setUserPrivilegeNames
argument_list|(
name|Privilege
operator|.
name|JCR_ALL
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
try|try
block|{
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
name|jrSession
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
finally|finally
block|{
name|jrSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
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
name|setGroupPrivilegeNames
argument_list|(
name|Privilege
operator|.
name|JCR_READ
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
try|try
block|{
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
name|jrSession
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
finally|finally
block|{
name|jrSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|doImport
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|xml
parameter_list|)
throws|throws
name|IOException
throws|,
name|RepositoryException
block|{
name|InputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|xml
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|superuser
operator|.
name|importXML
argument_list|(
name|parentPath
argument_list|,
name|in
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_THROW
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doImport
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|xml
parameter_list|,
name|int
name|importUUIDBehavior
parameter_list|)
throws|throws
name|IOException
throws|,
name|RepositoryException
block|{
name|InputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|xml
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|superuser
operator|.
name|importXML
argument_list|(
name|parentPath
argument_list|,
name|in
argument_list|,
name|importUUIDBehavior
argument_list|)
expr_stmt|;
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
throws|throws
name|RepositoryException
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
throws|throws
name|RepositoryException
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

