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
name|ItemExistsException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NodeIterator
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
name|nodetype
operator|.
name|ConstraintViolationException
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
name|principal
operator|.
name|PrincipalIterator
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
name|Impersonation
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
name|security
operator|.
name|principal
operator|.
name|PrincipalImpl
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
name|test
operator|.
name|NotExecutableException
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
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * UserImportTest...  */
end_comment

begin_class
specifier|public
class|class
name|UserImportTest
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
annotation|@
name|Test
specifier|public
name|void
name|testImportUser
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
throws|,
name|SAXException
block|{
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
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:disabled\" sv:type=\"String\"><sv:value>disabledUser</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|USERPATH
argument_list|)
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
name|assertTrue
argument_list|(
name|target
operator|.
name|isModified
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|superuser
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|Authorizable
name|newUser
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
name|newUser
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|newUser
operator|.
name|isGroup
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"t"
argument_list|,
name|newUser
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"t"
argument_list|,
name|newUser
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|User
operator|)
name|newUser
operator|)
operator|.
name|isDisabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"disabledUser"
argument_list|,
operator|(
operator|(
name|User
operator|)
name|newUser
operator|)
operator|.
name|getDisabledReason
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|newUser
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isNew
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|getParent
argument_list|()
operator|.
name|isSame
argument_list|(
name|target
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"t"
argument_list|,
name|n
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"t"
argument_list|,
name|n
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{sha1}8efd86fb78a56a5145ed7739dcb00c78581c5375"
argument_list|,
name|n
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"disabledUser"
argument_list|,
name|n
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_DISABLED
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
comment|// saving changes of the import -> must succeed. add mandatory
comment|// props should have been created.
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|superuser
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"t"
argument_list|)
condition|)
block|{
name|target
operator|.
name|getNode
argument_list|(
literal|"t"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportGroup
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
throws|,
name|SAXException
block|{
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
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|GROUPPATH
argument_list|)
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
name|assertTrue
argument_list|(
name|target
operator|.
name|isModified
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|superuser
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|Authorizable
name|newGroup
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
name|newGroup
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|newGroup
operator|.
name|isGroup
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"g"
argument_list|,
name|newGroup
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"g"
argument_list|,
name|newGroup
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|newGroup
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isNew
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|getParent
argument_list|()
operator|.
name|isSame
argument_list|(
name|target
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"g"
argument_list|,
name|n
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"g"
argument_list|,
name|n
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
comment|// saving changes of the import -> must succeed. add mandatory
comment|// props should have been created.
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|superuser
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"g"
argument_list|)
condition|)
block|{
name|target
operator|.
name|getNode
argument_list|(
literal|"g"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportGroupIntoUsersTree
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|NotExecutableException
block|{
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
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
comment|/*          importing a group below the users-path:          - nonProtected node rep:Group must be created.          - protected properties are ignored          - UserManager.getAuthorizable must return null.          - saving changes must fail with ConstraintViolationEx.          */
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|USERPATH
argument_list|)
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
name|assertTrue
argument_list|(
name|target
operator|.
name|isModified
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|superuser
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|Authorizable
name|newGroup
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"g"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|newGroup
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"g"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|target
operator|.
name|hasProperty
argument_list|(
literal|"g/rep:principalName"
argument_list|)
argument_list|)
expr_stmt|;
comment|// saving changes of the import -> must fail as mandatory prop is missing
try|try
block|{
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Import must be incomplete. Saving changes must fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
finally|finally
block|{
name|superuser
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"g"
argument_list|)
condition|)
block|{
name|target
operator|.
name|getNode
argument_list|(
literal|"g"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportAuthorizableId
parameter_list|()
throws|throws
name|IOException
throws|,
name|RepositoryException
throws|,
name|SAXException
throws|,
name|NotExecutableException
block|{
comment|// importing an authorizable with an jcr:uuid that doesn't match the
comment|// hash of the given ID -> getAuthorizable(String id) will not find the
comment|// authorizable.
comment|//String calculatedUUID = "e358efa4-89f5-3062-b10d-d7316b65649e";
name|String
name|mismatchUUID
init|=
literal|"a358efa4-89f5-3062-b10d-d7316b65649e"
decl_stmt|;
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"t\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:User</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>"
operator|+
name|mismatchUUID
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:password\" sv:type=\"String\"><sv:value>{sha1}8efd86fb78a56a5145ed7739dcb00c78581c5375</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property></sv:node>"
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|USERPATH
argument_list|)
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
name|assertTrue
argument_list|(
name|target
operator|.
name|isModified
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|superuser
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
comment|// node must be present:
name|assertTrue
argument_list|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"t"
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|target
operator|.
name|getNode
argument_list|(
literal|"t"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|mismatchUUID
argument_list|,
name|n
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
comment|// but UserManager.getAuthorizable(String) will not find the
comment|// authorizable
name|Authorizable
name|newUser
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"t"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|newUser
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|superuser
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExistingPrincipal
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|Principal
name|existing
init|=
literal|null
decl_stmt|;
name|PrincipalIterator
name|principalIterator
init|=
name|jrSession
operator|.
name|getPrincipalManager
argument_list|()
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
decl_stmt|;
while|while
condition|(
name|principalIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Principal
name|p
init|=
name|principalIterator
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|p
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|existing
operator|=
name|p
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|existing
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotExecutableException
argument_list|()
throw|;
block|}
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
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>"
operator|+
name|existing
operator|.
name|getName
argument_list|()
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|USERPATH
argument_list|)
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
name|fail
argument_list|(
literal|"Import must detect conflicting principals."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
comment|// success
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
annotation|@
name|Test
specifier|public
name|void
name|testConflictingPrincipalsWithinImport
parameter_list|()
throws|throws
name|IOException
throws|,
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<sv:node sv:name=\"gFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\">"
operator|+
literal|"<sv:value>rep:AuthorizableFolder</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"<sv:node sv:name=\"g\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>b2f5ff47-4366-31b6-a533-d8dc3614845d</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"<sv:node sv:name=\"g1\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>0120a4f9-196a-3f9e-b9f5-23f31f914da7</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|GROUPPATH
argument_list|)
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
name|fail
argument_list|(
literal|"Import must detect conflicting principals."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
comment|// success
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
annotation|@
name|Test
specifier|public
name|void
name|testMultiValuedPrincipalName
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|NotExecutableException
block|{
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
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g</sv:value><sv:value>g2</sv:value><sv:value>g</sv:value></sv:property></sv:node>"
decl_stmt|;
comment|/*          importing a group with a multi-valued rep:principalName property          - nonProtected node rep:Group must be created.          - property rep:principalName must be created regularly without being protected          - saving changes must fail with ConstraintViolationEx. as the protected            mandatory property rep:principalName is missing          */
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|GROUPPATH
argument_list|)
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
name|assertTrue
argument_list|(
name|target
operator|.
name|isModified
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|jrSession
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|Authorizable
name|newGroup
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
name|newGroup
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"g"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|target
operator|.
name|hasProperty
argument_list|(
literal|"g/rep:principalName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|target
operator|.
name|getProperty
argument_list|(
literal|"g/rep:principalName"
argument_list|)
operator|.
name|getDefinition
argument_list|()
operator|.
name|isProtected
argument_list|()
argument_list|)
expr_stmt|;
comment|// saving changes of the import -> must fail as mandatory prop is missing
try|try
block|{
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Import must be incomplete. Saving changes must fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
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
if|if
condition|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"g"
argument_list|)
condition|)
block|{
name|target
operator|.
name|getNode
argument_list|(
literal|"g"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPlainTextPassword
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|NotExecutableException
block|{
name|String
name|plainPw
init|=
literal|"myPassword"
decl_stmt|;
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
literal|"<sv:property sv:name=\"rep:password\" sv:type=\"String\"><sv:value>"
operator|+
name|plainPw
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|USERPATH
argument_list|)
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
name|assertTrue
argument_list|(
name|target
operator|.
name|isModified
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|jrSession
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|Authorizable
name|newUser
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"t"
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|newUser
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|pwValue
init|=
name|n
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
operator|.
name|getString
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|plainPw
operator|.
name|equals
argument_list|(
name|pwValue
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pwValue
operator|.
name|toLowerCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"{sha"
argument_list|)
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
annotation|@
name|Test
specifier|public
name|void
name|testMultiValuedPassword
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|NotExecutableException
block|{
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
literal|"<sv:property sv:name=\"rep:password\" sv:type=\"String\"><sv:value>{sha1}8efd86fb78a56a5145ed7739dcb00c78581c5375</sv:value><sv:value>{sha1}8efd86fb78a56a5145ed7739dcb00c78581c5375</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
comment|/*          importing a user with a multi-valued rep:password property          - nonProtected node rep:User must be created.          - property rep:password must be created regularly without being protected          - saving changes must fail with ConstraintViolationEx. as the protected            mandatory property rep:password is missing          */
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|USERPATH
argument_list|)
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
name|assertTrue
argument_list|(
name|target
operator|.
name|isModified
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|jrSession
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|Authorizable
name|newUser
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
name|newUser
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"t"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|target
operator|.
name|hasProperty
argument_list|(
literal|"t/rep:password"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|target
operator|.
name|getProperty
argument_list|(
literal|"t/rep:password"
argument_list|)
operator|.
name|getDefinition
argument_list|()
operator|.
name|isProtected
argument_list|()
argument_list|)
expr_stmt|;
comment|// saving changes of the import -> must fail as mandatory prop is missing
try|try
block|{
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Import must be incomplete. Saving changes must fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
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
if|if
condition|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"t"
argument_list|)
condition|)
block|{
name|target
operator|.
name|getNode
argument_list|(
literal|"t"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIncompleteUser
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|NotExecutableException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|incompleteXml
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|incompleteXml
operator|.
name|add
argument_list|(
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
literal|"</sv:node>"
argument_list|)
expr_stmt|;
name|incompleteXml
operator|.
name|add
argument_list|(
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<sv:node sv:name=\"t\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:User</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>e358efa4-89f5-3062-b10d-d7316b65649e</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|xml
range|:
name|incompleteXml
control|)
block|{
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|USERPATH
argument_list|)
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
comment|// saving changes of the import -> must fail as mandatory prop is missing
try|try
block|{
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Import must be incomplete. Saving changes must fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
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
if|if
condition|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"t"
argument_list|)
condition|)
block|{
name|target
operator|.
name|getNode
argument_list|(
literal|"t"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIncompleteGroup
parameter_list|()
throws|throws
name|IOException
throws|,
name|RepositoryException
throws|,
name|SAXException
throws|,
name|NotExecutableException
block|{
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"g\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>b2f5ff47-4366-31b6-a533-d8dc3614845d</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
comment|/*          importing a group without rep:principalName property          - saving changes must fail with ConstraintViolationEx.          */
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|GROUPPATH
argument_list|)
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
comment|// saving changes of the import -> must fail as mandatory prop is missing
try|try
block|{
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Import must be incomplete. Saving changes must fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
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
if|if
condition|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"g"
argument_list|)
condition|)
block|{
name|target
operator|.
name|getNode
argument_list|(
literal|"g"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportWithIntermediatePath
parameter_list|()
throws|throws
name|IOException
throws|,
name|RepositoryException
throws|,
name|SAXException
throws|,
name|NotExecutableException
block|{
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<sv:node sv:name=\"some\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>d5433be9-68d0-4fba-bf96-efc29f461993</sv:value></sv:property>"
operator|+
literal|"<sv:node sv:name=\"intermediate\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>d87354a4-037e-4756-a8fb-deb2eb7c5149</sv:value></sv:property>"
operator|+
literal|"<sv:node sv:name=\"path\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>24263272-b789-4568-957a-3bcaf99dbab3</sv:value></sv:property>"
operator|+
literal|"<sv:node sv:name=\"t3\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:User</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>0b8854ad-38f0-36c6-9807-928d28195609</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:password\" sv:type=\"String\"><sv:value>{sha1}4358694eeb098c6708ae914a10562ce722bbbc34</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t3</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|USERPATH
argument_list|)
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
name|assertTrue
argument_list|(
name|target
operator|.
name|isModified
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|jrSession
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|Authorizable
name|newUser
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"t3"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newUser
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|newUser
operator|.
name|isGroup
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"t3"
argument_list|,
name|newUser
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"t3"
argument_list|,
name|newUser
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|newUser
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isNew
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|parent
init|=
name|n
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|n
operator|.
name|isSame
argument_list|(
name|target
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|parent
operator|.
name|isNodeType
argument_list|(
name|UserConstants
operator|.
name|NT_REP_AUTHORIZABLE_FOLDER
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|parent
operator|.
name|getDefinition
argument_list|()
operator|.
name|isProtected
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"some"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"some/intermediate/path"
argument_list|)
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
annotation|@
name|Test
specifier|public
name|void
name|testImportNewMembers
parameter_list|()
throws|throws
name|IOException
throws|,
name|RepositoryException
throws|,
name|SAXException
throws|,
name|NotExecutableException
block|{
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"gFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\">"
operator|+
literal|"<sv:value>rep:AuthorizableFolder</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"<sv:node sv:name=\"g\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>b2f5ff47-4366-31b6-a533-d8dc3614845d</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"<sv:node sv:name=\"g1\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>0120a4f9-196a-3f9e-b9f5-23f31f914da7</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g1</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:members\" sv:type=\"WeakReference\"><sv:value>b2f5ff47-4366-31b6-a533-d8dc3614845d</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|GROUPPATH
argument_list|)
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
name|Group
name|g
init|=
operator|(
name|Group
operator|)
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"g"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|g
argument_list|)
expr_stmt|;
name|Group
name|g1
init|=
operator|(
name|Group
operator|)
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"g1"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|g1
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|jrSession
operator|.
name|getNode
argument_list|(
name|g1
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
operator|||
name|n
operator|.
name|hasNode
argument_list|(
name|UserConstants
operator|.
name|NT_REP_MEMBERS
argument_list|)
argument_list|)
expr_stmt|;
comment|// getWeakReferences only works upon save.
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|g1
operator|.
name|isMember
argument_list|(
name|g
argument_list|)
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
if|if
condition|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"gFolder"
argument_list|)
condition|)
block|{
name|target
operator|.
name|getNode
argument_list|(
literal|"gFolder"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportNewMembersReverseOrder
parameter_list|()
throws|throws
name|IOException
throws|,
name|RepositoryException
throws|,
name|SAXException
throws|,
name|NotExecutableException
block|{
comment|// group is imported before the member
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"gFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>"
operator|+
literal|"<sv:node sv:name=\"g1\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>0120a4f9-196a-3f9e-b9f5-23f31f914da7</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g1</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:members\" sv:type=\"WeakReference\"><sv:value>b2f5ff47-4366-31b6-a533-d8dc3614845d</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"<sv:node sv:name=\"g\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>b2f5ff47-4366-31b6-a533-d8dc3614845d</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|GROUPPATH
argument_list|)
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
name|Group
name|g
init|=
operator|(
name|Group
operator|)
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"g"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|g
argument_list|)
expr_stmt|;
name|Group
name|g1
init|=
operator|(
name|Group
operator|)
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"g1"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|g1
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|jrSession
operator|.
name|getNode
argument_list|(
name|g1
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
operator|||
name|n
operator|.
name|hasNode
argument_list|(
name|UserConstants
operator|.
name|NT_REP_MEMBERS
argument_list|)
argument_list|)
expr_stmt|;
comment|// getWeakReferences only works upon save.
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|g1
operator|.
name|isMember
argument_list|(
name|g
argument_list|)
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
if|if
condition|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"gFolder"
argument_list|)
condition|)
block|{
name|target
operator|.
name|getNode
argument_list|(
literal|"gFolder"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportMembers
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|NotExecutableException
block|{
name|Authorizable
name|admin
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"admin"
argument_list|)
decl_stmt|;
if|if
condition|(
name|admin
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotExecutableException
argument_list|()
throw|;
block|}
name|String
name|uuid
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|admin
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getUUID
argument_list|()
decl_stmt|;
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"gFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>"
operator|+
literal|"<sv:node sv:name=\"g1\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>0120a4f9-196a-3f9e-b9f5-23f31f914da7</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g1</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:members\" sv:type=\"WeakReference\"><sv:value>"
operator|+
name|uuid
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|GROUPPATH
argument_list|)
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
name|Group
name|g1
init|=
operator|(
name|Group
operator|)
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"g1"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|g1
argument_list|)
expr_stmt|;
comment|// getWeakReferences only works upon save.
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|g1
operator|.
name|isMember
argument_list|(
name|admin
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Group
argument_list|>
name|it
init|=
name|admin
operator|.
name|declaredMemberOf
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|found
condition|;
control|)
block|{
name|found
operator|=
literal|"g1"
operator|.
name|equals
argument_list|(
name|it
operator|.
name|next
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|found
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
name|target
operator|.
name|getNode
argument_list|(
literal|"gFolder"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportNonExistingMemberIgnore
parameter_list|()
throws|throws
name|IOException
throws|,
name|RepositoryException
throws|,
name|SAXException
throws|,
name|NotExecutableException
block|{
name|Node
name|n
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|,
name|ntUnstructured
argument_list|)
decl_stmt|;
name|n
operator|.
name|addMixin
argument_list|(
name|mixReferenceable
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|invalid
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|invalid
operator|.
name|add
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// random uuid
name|invalid
operator|.
name|add
argument_list|(
name|n
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
comment|// uuid of non-authorizable node
for|for
control|(
name|String
name|id
range|:
name|invalid
control|)
block|{
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"gFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>"
operator|+
literal|"<sv:node sv:name=\"g1\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>0120a4f9-196a-3f9e-b9f5-23f31f914da7</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g1</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:members\" sv:type=\"WeakReference\"><sv:value>"
operator|+
name|id
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|GROUPPATH
argument_list|)
decl_stmt|;
try|try
block|{
name|doImport
argument_list|(
name|GROUPPATH
argument_list|,
name|xml
comment|/*, UserImporter.ImportBehavior.IGNORE*/
argument_list|)
expr_stmt|;
comment|// there should be no exception during import,
comment|// but invalid members must be ignored.
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"g1"
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|isGroup
argument_list|()
condition|)
block|{
name|assertNotDeclaredMember
argument_list|(
operator|(
name|Group
operator|)
name|a
argument_list|,
name|id
argument_list|,
name|superuser
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"'g1' was not imported as Group."
argument_list|)
expr_stmt|;
block|}
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
block|}
comment|//TODO test UserImporter.ImportBehavior != ignore
comment|//    public void testImportNonExistingMemberAbort() throws IOException, RepositoryException, SAXException, NotExecutableException {
comment|//        Node n = testRootNode.addNode(nodeName1, ntUnstructured);
comment|//        n.addMixin(mixReferenceable);
comment|//
comment|//        List<String> invalid = new ArrayList<String>();
comment|//        invalid.add(UUID.randomUUID().toString()); // random uuid
comment|//        invalid.add(n.getUUID()); // uuid of non-authorizable node
comment|//
comment|//        for (String id : invalid) {
comment|//            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
comment|//                    "<sv:node sv:name=\"gFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">" +
comment|//                    "<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>" +
comment|//                        "<sv:node sv:name=\"g1\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>" +
comment|//                        "<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>0120a4f9-196a-3f9e-b9f5-23f31f914da7</sv:value></sv:property>" +
comment|//                        "<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g1</sv:value></sv:property>" +
comment|//                        "<sv:property sv:name=\"rep:members\" sv:type=\"WeakReference\"><sv:value>" +id+ "</sv:value></sv:property>" +
comment|//                        "</sv:node>" +
comment|//                    "</sv:node>";
comment|//            NodeImpl target = (NodeImpl) sImpl.getNode(umgr.getGroupsPath());
comment|//            try {
comment|//                doImport(target, xml, UserImporter.ImportBehavior.ABORT);
comment|//                // import behavior ABORT -> should throw.
comment|//                fail("importing invalid members -> must throw.");
comment|//            } catch (SAXException e) {
comment|//                // success as well
comment|//            } finally {
comment|//                sImpl.refresh(false);
comment|//            }
comment|//        }
comment|//    }
comment|//
comment|//    public void testImportNonExistingMemberBestEffort() throws IOException, RepositoryException, SAXException, NotExecutableException {
comment|//        if (umgr.hasMemberSplitSize()) {
comment|//            throw new NotExecutableException();
comment|//        }
comment|//
comment|//        Node n = testRootNode.addNode(nodeName1, ntUnstructured);
comment|//        n.addMixin(mixReferenceable);
comment|//
comment|//        List<String> invalid = new ArrayList<String>();
comment|//        invalid.add(UUID.randomUUID().toString()); // random uuid
comment|//        invalid.add(n.getUUID()); // uuid of non-authorizable node
comment|//
comment|//        for (String id : invalid) {
comment|//            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
comment|//                    "<sv:node sv:name=\"gFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">" +
comment|//                    "<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>" +
comment|//                        "<sv:node sv:name=\"g1\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>" +
comment|//                        "<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>0120a4f9-196a-3f9e-b9f5-23f31f914da7</sv:value></sv:property>" +
comment|//                        "<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g1</sv:value></sv:property>" +
comment|//                        "<sv:property sv:name=\"rep:members\" sv:type=\"WeakReference\"><sv:value>" +id+ "</sv:value></sv:property>" +
comment|//                        "</sv:node>" +
comment|//                    "</sv:node>";
comment|//            NodeImpl target = (NodeImpl) sImpl.getNode(umgr.getGroupsPath());
comment|//            try {
comment|//                // BESTEFFORT behavior -> must import non-existing members.
comment|//                doImport(target, xml, UserImporter.ImportBehavior.BESTEFFORT);
comment|//                Authorizable a = umgr.getAuthorizable("g1");
comment|//                if (a.isGroup()) {
comment|//                    // the rep:members property must contain the invalid value
comment|//                    boolean found = false;
comment|//                    NodeImpl grNode = ((AuthorizableImpl) a).getNode();
comment|//                    for (Value memberValue : grNode.getProperty(UserConstants.P_MEMBERS).getValues()) {
comment|//                        assertEquals(PropertyType.WEAKREFERENCE, memberValue.getType());
comment|//                        if (id.equals(memberValue.getString())) {
comment|//                            found = true;
comment|//                            break;
comment|//                        }
comment|//                    }
comment|//                    assertTrue("ImportBehavior.BESTEFFORT must import non-existing members.",found);
comment|//
comment|//                    // declared members must not list the invalid entry.
comment|//                    assertNotDeclaredMember((Group) a, id);
comment|//                } else {
comment|//                    fail("'g1' was not imported as Group.");
comment|//                }
comment|//            } finally {
comment|//                sImpl.refresh(false);
comment|//            }
comment|//        }
comment|//    }
comment|//
comment|//    public void testImportNonExistingMemberBestEffort2() throws IOException, RepositoryException, SAXException, NotExecutableException {
comment|//
comment|//        String g1Id = "0120a4f9-196a-3f9e-b9f5-23f31f914da7";
comment|//        String nonExistingId = "b2f5ff47-4366-31b6-a533-d8dc3614845d"; // groupId of 'g' group.
comment|//        if (umgr.getAuthorizable("g") != null || umgr.hasMemberSplitSize()) {
comment|//            throw new NotExecutableException();
comment|//        }
comment|//
comment|//        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
comment|//                "<sv:node sv:name=\"gFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">" +
comment|//                "<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>" +
comment|//                "<sv:node sv:name=\"g1\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>" +
comment|//                "<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>" + g1Id + "</sv:value></sv:property>" +
comment|//                "<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g1</sv:value></sv:property>" +
comment|//                "<sv:property sv:name=\"rep:members\" sv:type=\"WeakReference\"><sv:value>" +nonExistingId+ "</sv:value></sv:property>" +
comment|//                "</sv:node>" +
comment|//                "</sv:node>";
comment|//
comment|//        String xml2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
comment|//                "<sv:node sv:name=\"g\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">" +
comment|//                "<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>" +
comment|//                "<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>" + nonExistingId + "</sv:value></sv:property>" +
comment|//                "<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g</sv:value></sv:property>" +
comment|//                "<sv:property sv:name=\"rep:members\" sv:type=\"WeakReference\"><sv:value>" + g1Id + "</sv:value></sv:property>" +
comment|//                "</sv:node>";
comment|//
comment|//        NodeImpl target = (NodeImpl) sImpl.getNode(umgr.getGroupsPath());
comment|//        try {
comment|//            // BESTEFFORT behavior -> must import non-existing members.
comment|//            doImport(target, xml, UserImporter.ImportBehavior.BESTEFFORT);
comment|//            Authorizable g1 = umgr.getAuthorizable("g1");
comment|//            if (g1.isGroup()) {
comment|//                // the rep:members property must contain the invalid value
comment|//                boolean found = false;
comment|//                NodeImpl grNode = ((AuthorizableImpl) g1).getNode();
comment|//                for (Value memberValue : grNode.getProperty(UserConstants.P_MEMBERS).getValues()) {
comment|//                    assertEquals(PropertyType.WEAKREFERENCE, memberValue.getType());
comment|//                    if (nonExistingId.equals(memberValue.getString())) {
comment|//                        found = true;
comment|//                        break;
comment|//                    }
comment|//                }
comment|//                assertTrue("ImportBehavior.BESTEFFORT must import non-existing members.",found);
comment|//            } else {
comment|//                fail("'g1' was not imported as Group.");
comment|//            }
comment|//
comment|//            /*
comment|//            now try to import the 'g' group that has a circular group
comment|//            membership references.
comment|//            expected:
comment|//            - group is imported
comment|//            - circular membership is ignored
comment|//            - g is member of g1
comment|//            - g1 isn't member of g
comment|//            */
comment|//            target = (NodeImpl) target.getNode("gFolder");
comment|//            doImport(target, xml2, UserImporter.ImportBehavior.BESTEFFORT);
comment|//
comment|//            Authorizable g = umgr.getAuthorizable("g");
comment|//            assertNotNull(g);
comment|//            if (g.isGroup()) {
comment|//                assertNotDeclaredMember((Group) g, g1Id);
comment|//            } else {
comment|//                fail("'g' was not imported as Group.");
comment|//            }
comment|//
comment|//        } finally {
comment|//            sImpl.refresh(false);
comment|//        }
comment|//    }
annotation|@
name|Test
specifier|public
name|void
name|testImportSelfAsGroupIgnore
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|invalidId
init|=
literal|"0120a4f9-196a-3f9e-b9f5-23f31f914da7"
decl_stmt|;
comment|// uuid of the group itself
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"gFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>"
operator|+
literal|"<sv:node sv:name=\"g1\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>"
operator|+
name|invalidId
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g1</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:members\" sv:type=\"WeakReference\"><sv:value>"
operator|+
name|invalidId
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|GROUPPATH
argument_list|)
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
comment|// no exception during import -> member must have been ignored though.
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"g1"
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|isGroup
argument_list|()
condition|)
block|{
name|assertNotDeclaredMember
argument_list|(
operator|(
name|Group
operator|)
name|a
argument_list|,
name|invalidId
argument_list|,
name|superuser
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"'g1' was not imported as Group."
argument_list|)
expr_stmt|;
block|}
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
comment|//TODO test UserImporter.ImportBehavior != ignore
comment|//    public void testImportSelfAsGroupAbort() throws Exception {
comment|//
comment|//        String invalidId = "0120a4f9-196a-3f9e-b9f5-23f31f914da7"; // uuid of the group itself
comment|//        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
comment|//                "<sv:node sv:name=\"gFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">" +
comment|//                "<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>" +
comment|//                "<sv:node sv:name=\"g1\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>" +
comment|//                "<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>"+invalidId+"</sv:value></sv:property>" +
comment|//                "<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g1</sv:value></sv:property>" +
comment|//                "<sv:property sv:name=\"rep:members\" sv:type=\"WeakReference\"><sv:value>" +invalidId+ "</sv:value></sv:property>" +
comment|//                "</sv:node>" +
comment|//                "</sv:node>";
comment|//        NodeImpl target = (NodeImpl) sImpl.getNode(umgr.getGroupsPath());
comment|//        try {
comment|//            doImport(target, xml, UserImporter.ImportBehavior.ABORT);
comment|//            fail("Importing self as group with ImportBehavior.ABORT must fail.");
comment|//        } catch (SAXException e) {
comment|//            // success.
comment|//        }finally {
comment|//            sImpl.refresh(false);
comment|//        }
comment|//    }
annotation|@
name|Test
specifier|public
name|void
name|testImportImpersonation
parameter_list|()
throws|throws
name|IOException
throws|,
name|RepositoryException
throws|,
name|SAXException
throws|,
name|NotExecutableException
block|{
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"uFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>"
operator|+
literal|"<sv:node sv:name=\"t\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:User</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>e358efa4-89f5-3062-b10d-d7316b65649e</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:impersonators\" sv:type=\"String\"><sv:value>g</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"<sv:node sv:name=\"g\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:User</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>b2f5ff47-4366-31b6-a533-d8dc3614845d</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|USERPATH
argument_list|)
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
name|newUser
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
name|newUser
argument_list|)
expr_stmt|;
name|Authorizable
name|u2
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
name|u2
argument_list|)
expr_stmt|;
name|Subject
name|subj
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|subj
operator|.
name|getPrincipals
argument_list|()
operator|.
name|add
argument_list|(
name|u2
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|Impersonation
name|imp
init|=
operator|(
operator|(
name|User
operator|)
name|newUser
operator|)
operator|.
name|getImpersonation
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|imp
operator|.
name|allows
argument_list|(
name|subj
argument_list|)
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
annotation|@
name|Test
specifier|public
name|void
name|testImportInvalidImpersonationIgnore
parameter_list|()
throws|throws
name|IOException
throws|,
name|RepositoryException
throws|,
name|SAXException
throws|,
name|NotExecutableException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|invalid
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|invalid
operator|.
name|add
argument_list|(
literal|"anybody"
argument_list|)
expr_stmt|;
comment|// an non-existing princ-name
name|invalid
operator|.
name|add
argument_list|(
literal|"administrators"
argument_list|)
expr_stmt|;
comment|// a group
name|invalid
operator|.
name|add
argument_list|(
literal|"t"
argument_list|)
expr_stmt|;
comment|// principal of the user itself.
for|for
control|(
name|String
name|principalName
range|:
name|invalid
control|)
block|{
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
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property><sv:property sv:name=\"rep:impersonators\" sv:type=\"String\"><sv:value>"
operator|+
name|principalName
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Subject
name|subj
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|subj
operator|.
name|getPrincipals
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|principalName
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|USERPATH
argument_list|)
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
comment|// no exception during import: no impersonation must be granted
comment|// for the invalid principal name
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
if|if
condition|(
operator|!
name|a
operator|.
name|isGroup
argument_list|()
condition|)
block|{
name|Impersonation
name|imp
init|=
operator|(
operator|(
name|User
operator|)
name|a
operator|)
operator|.
name|getImpersonation
argument_list|()
decl_stmt|;
name|Subject
name|s
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|s
operator|.
name|getPrincipals
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|principalName
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|imp
operator|.
name|allows
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|PrincipalIterator
name|it
init|=
name|imp
operator|.
name|getImpersonators
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|assertFalse
argument_list|(
name|principalName
operator|.
name|equals
argument_list|(
name|it
operator|.
name|nextPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Importing 't' didn't create a User."
argument_list|)
expr_stmt|;
block|}
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
block|}
comment|//TODO test UserImporter.ImportBehavior != ignore
comment|//    public void testImportInvalidImpersonationAbort() throws IOException, RepositoryException, SAXException, NotExecutableException {
comment|//        List<String> invalid = new ArrayList<String>();
comment|//        invalid.add("anybody"); // an non-existing princ-name
comment|//        invalid.add("administrators"); // a group
comment|//        invalid.add("t"); // principal of the user itself.
comment|//
comment|//        for (String principalName : invalid) {
comment|//            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
comment|//                    "<sv:node sv:name=\"t\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">" +
comment|//                    "<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:User</sv:value></sv:property>" +
comment|//                    "<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>e358efa4-89f5-3062-b10d-d7316b65649e</sv:value></sv:property>" +
comment|//                    "<sv:property sv:name=\"rep:password\" sv:type=\"String\"><sv:value>{sha1}8efd86fb78a56a5145ed7739dcb00c78581c5375</sv:value></sv:property>" +
comment|//                    "<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property><sv:property sv:name=\"rep:impersonators\" sv:type=\"String\"><sv:value>" +principalName+ "</sv:value></sv:property>" +
comment|//                    "</sv:node>";
comment|//            Subject subj = new Subject();
comment|//            subj.getPrincipals().add(new PrincipalImpl(principalName));
comment|//
comment|//            NodeImpl target = (NodeImpl) sImpl.getNode(umgr.getUsersPath());
comment|//            try {
comment|//                doImport(target, xml, UserImporter.ImportBehavior.ABORT);
comment|//                fail("UserImporter.ImportBehavior.ABORT -> importing invalid impersonators must throw.");
comment|//            } catch (SAXException e) {
comment|//                // success
comment|//            } finally {
comment|//                sImpl.refresh(false);
comment|//            }
comment|//        }
comment|//    }
annotation|@
name|Test
specifier|public
name|void
name|testImportUuidCollisionRemoveExisting
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
throws|,
name|SAXException
block|{
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
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|USERPATH
argument_list|)
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
comment|//TODO different IgnoreBehavior needed?
comment|// re-import should succeed if UUID-behavior is set accordingly
name|doImport
argument_list|(
name|USERPATH
argument_list|,
name|xml
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REMOVE_EXISTING
argument_list|)
expr_stmt|;
comment|// saving changes of the import -> must succeed. add mandatory
comment|// props should have been created.
name|jrSession
operator|.
name|save
argument_list|()
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
if|if
condition|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"t"
argument_list|)
condition|)
block|{
name|target
operator|.
name|getNode
argument_list|(
literal|"t"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Same as {@link #testImportUuidCollisionRemoveExisting} with the single      * difference that the inital import is saved before being overwritten.      *      * @throws RepositoryException      * @throws IOException      * @throws SAXException      */
annotation|@
name|Test
specifier|public
name|void
name|testImportUuidCollisionRemoveExisting2
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
throws|,
name|SAXException
block|{
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
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|USERPATH
argument_list|)
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
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
comment|//TODO different IgnoreBehavior needed?
comment|// re-import should succeed if UUID-behavior is set accordingly
name|doImport
argument_list|(
name|USERPATH
argument_list|,
name|xml
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REMOVE_EXISTING
argument_list|)
expr_stmt|;
comment|// saving changes of the import -> must succeed. add mandatory
comment|// props should have been created.
name|jrSession
operator|.
name|save
argument_list|()
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
if|if
condition|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"t"
argument_list|)
condition|)
block|{
name|target
operator|.
name|getNode
argument_list|(
literal|"t"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportUuidCollisionThrow
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
throws|,
name|SAXException
block|{
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
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|USERPATH
argument_list|)
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
name|doImport
argument_list|(
name|USERPATH
argument_list|,
name|xml
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_THROW
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"UUID collision must be handled according to the uuid behavior."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getException
argument_list|()
operator|instanceof
name|ItemExistsException
argument_list|)
expr_stmt|;
comment|// success.
block|}
catch|catch
parameter_list|(
name|ItemExistsException
name|e
parameter_list|)
block|{
comment|// success.
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
if|if
condition|(
name|target
operator|.
name|hasNode
argument_list|(
literal|"t"
argument_list|)
condition|)
block|{
name|target
operator|.
name|getNode
argument_list|(
literal|"t"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportGroupMembersFromNodes
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?><sv:node sv:name=\"s\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:sling=\"http://sling.apache.org/jcr/sling/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property><sv:property sv:name=\"jcr:created\" sv:type=\"Date\"><sv:value>2010-08-17T18:22:20.086+02:00</sv:value></sv:property><sv:property sv:name=\"jcr:createdBy\" sv:type=\"String\"><sv:value>admin</sv:value></sv:property><sv:node sv:name=\"sh\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property><sv:property sv:name=\"jcr:created\" sv:type=\"Date\"><sv:value>2010-08-17T18:22:20.086+02:00</sv:value></sv:property><sv:property sv:name=\"jcr:createdBy\" sv:type=\"String\"><sv:value>admin</sv:value></sv:property><sv:node sv:name=\"shrimps\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property><sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>08429aec-6f09-30db-8c83-1a2a57fc760c</sv:value></sv:property><sv:property sv:name=\"jcr:created\" sv:type=\"Date\">"
operator|+
literal|"<sv:value>2010-08-17T18:22:20.086+02:00</sv:value></sv:property><sv:property sv:name=\"jcr:createdBy\" sv:type=\"String\"><sv:value>admin</sv:value></sv:property><sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>shrimps</sv:value></sv:property><sv:node sv:name=\"rep:members\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Members</sv:value></sv:property><sv:node sv:name=\"adi\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Members</sv:value></sv:property><sv:node sv:name=\"adi\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Members</sv:value></sv:property><sv:property sv:name=\"adi\" sv:type=\"WeakReference\"><sv:value>c46335eb-267e-3e1c-9e5b-017acb4cd799</sv:value></sv:property><sv:property sv:name=\"admin\" sv:type=\"WeakReference\"><sv:value>21232f29-7a57-35a7-8389-4a0e4a801fc3</sv:value></sv:property></sv:node><sv:node sv:name=\"angi\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Members</sv:value></sv:property><sv:property sv:name=\"angi\" sv:type=\"WeakReference\"><sv:value>a468b64f-b1df-377c-b325-20d97aaa1ad9</sv:value></sv:property><sv:property sv:name=\"anonymous\" sv:type=\"WeakReference\"><sv:value>294de355-7d9d-30b3-92d8-a1e6aab028cf</sv:value></sv:property><sv:property sv:name=\"cati\" sv:type=\"WeakReference\"><sv:value>f08910b6-41c8-3cb9-a648-1dddd14b132d</sv:value></sv:property></sv:node></sv:node><sv:n"
operator|+
literal|"ode sv:name=\"debbi\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Members</sv:value></sv:property><sv:node sv:name=\"debbi\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Members</sv:value></sv:property><sv:property sv:name=\"debbi\" sv:type=\"WeakReference\"><sv:value>d53bedf9-ebb8-3117-a8b8-162d32b4bee2</sv:value></sv:property><sv:property sv:name=\"eddi\" sv:type=\"WeakReference\"><sv:value>1795fa1a-3d20-3a64-996e-eaaeb520a01e</sv:value></sv:property><sv:property sv:name=\"gabi\" sv:type=\"WeakReference\"><sv:value>a0d499c7-5105-3663-8611-a32779a57104</sv:value></sv:property><sv:property sv:name=\"hansi\" sv:type=\"WeakReference\"><sv:value>9ea4d671-8ed1-399a-8401-59487a14d00a</sv:value></sv:property></sv:node><sv:node sv:name=\"hari\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Members</sv:value></sv:property><sv:property sv:name=\"hari\" sv:type=\"WeakReference\"><sv:value>a9bcf1e4-d7b9-3a22-a297-5c812d938889</sv:value></sv:property><sv:property sv:name=\"lisi\" sv:type=\"WeakReference\"><sv:value>dc3a8f16-70d6-3bea-a9b7-b65048a0ac40</sv:value></sv:property></sv:node><sv:node sv:name=\"luzi\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Members</sv:value></sv:property><sv:property sv:name=\"luzi\" sv:type=\"WeakReference\"><sv:value>9ec299fd-3461-3f1a-9749-92a76f2516eb</sv:value></sv:property><sv:property sv:name=\"pipi\" sv:type="
operator|+
literal|"\"WeakReference\"><sv:value>16d5d24f-5b09-3199-9bd4-e5f57bf11237</sv:value></sv:property><sv:property sv:name=\"susi\" sv:type=\"WeakReference\"><sv:value>536931d8-0dec-318c-b3db-9612bdd004d4</sv:value></sv:property></sv:node></sv:node></sv:node></sv:node></sv:node></sv:node>"
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|createdUsers
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Node
name|target
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|GROUPPATH
argument_list|)
decl_stmt|;
try|try
block|{
name|String
index|[]
name|users
init|=
block|{
literal|"angi"
block|,
literal|"adi"
block|,
literal|"hansi"
block|,
literal|"lisi"
block|,
literal|"luzi"
block|,
literal|"susi"
block|,
literal|"pipi"
block|,
literal|"hari"
block|,
literal|"gabi"
block|,
literal|"eddi"
block|,
literal|"debbi"
block|,
literal|"cati"
block|,
literal|"admin"
block|,
literal|"anonymous"
block|}
decl_stmt|;
for|for
control|(
name|String
name|user
range|:
name|users
control|)
block|{
if|if
condition|(
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|user
argument_list|)
operator|==
literal|null
condition|)
block|{
name|userMgr
operator|.
name|createUser
argument_list|(
name|user
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|createdUsers
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|userMgr
operator|.
name|isAutoSave
argument_list|()
condition|)
block|{
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
name|doImport
argument_list|(
name|GROUPPATH
argument_list|,
name|xml
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|userMgr
operator|.
name|isAutoSave
argument_list|()
condition|)
block|{
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
name|Authorizable
name|aShrimps
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"shrimps"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|aShrimps
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aShrimps
operator|.
name|isGroup
argument_list|()
argument_list|)
expr_stmt|;
name|Group
name|gShrimps
init|=
operator|(
name|Group
operator|)
name|aShrimps
decl_stmt|;
for|for
control|(
name|String
name|user
range|:
name|users
control|)
block|{
name|assertTrue
argument_list|(
name|user
operator|+
literal|" should be member of "
operator|+
name|gShrimps
argument_list|,
name|gShrimps
operator|.
name|isMember
argument_list|(
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|user
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|String
name|user
range|:
name|createdUsers
control|)
block|{
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
operator|&&
operator|!
name|a
operator|.
name|isGroup
argument_list|()
condition|)
block|{
name|a
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|userMgr
operator|.
name|isAutoSave
argument_list|()
condition|)
block|{
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|NodeIterator
name|it
init|=
name|target
operator|.
name|getNodes
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|it
operator|.
name|nextNode
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|userMgr
operator|.
name|isAutoSave
argument_list|()
condition|)
block|{
name|jrSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|//TODO test UserImporter.ImportBehavior != ignore
comment|//    public void testImportGroupMembersFromNodesBestEffort() throws RepositoryException, IOException, SAXException {
comment|//        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><sv:node sv:name=\"s\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:sling=\"http://sling.apache.org/jcr/sling/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property><sv:property sv:name=\"jcr:created\" sv:type=\"Date\"><sv:value>2010-08-17T18:22:20.086+02:00</sv:value></sv:property><sv:property sv:name=\"jcr:createdBy\" sv:type=\"String\"><sv:value>admin</sv:value></sv:property><sv:node sv:name=\"sh\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property><sv:property sv:name=\"jcr:created\" sv:type=\"Date\"><sv:value>2010-08-17T18:22:20.086+02:00</sv:value></sv:property><sv:property sv:name=\"jcr:createdBy\" sv:type=\"String\"><sv:value>admin</sv:value></sv:property><sv:node sv:name=\"shrimps\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property><sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>08429aec-6f09-30db-8c83-1a2a57fc760c</sv:value></sv:property><sv:property sv:name=\"jcr:created\" sv:type=\"Date\">" +
comment|//                     "<sv:value>2010-08-17T18:22:20.086+02:00</sv:value></sv:property><sv:property sv:name=\"jcr:createdBy\" sv:type=\"String\"><sv:value>admin</sv:value></sv:property><sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>shrimps</sv:value></sv:property><sv:node sv:name=\"rep:members\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Members</sv:value></sv:property><sv:node sv:name=\"adi\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Members</sv:value></sv:property><sv:node sv:name=\"adi\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Members</sv:value></sv:property><sv:property sv:name=\"adi\" sv:type=\"WeakReference\"><sv:value>c46335eb-267e-3e1c-9e5b-017acb4cd799</sv:value></sv:property><sv:property sv:name=\"admin\" sv:type=\"WeakReference\"><sv:value>21232f29-7a57-35a7-8389-4a0e4a801fc3</sv:value></sv:property></sv:node><sv:node sv:name=\"angi\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Members</sv:value></sv:property><sv:property sv:name=\"angi\" sv:type=\"WeakReference\"><sv:value>a468b64f-b1df-377c-b325-20d97aaa1ad9</sv:value></sv:property><sv:property sv:name=\"anonymous\" sv:type=\"WeakReference\"><sv:value>294de355-7d9d-30b3-92d8-a1e6aab028cf</sv:value></sv:property><sv:property sv:name=\"cati\" sv:type=\"WeakReference\"><sv:value>f08910b6-41c8-3cb9-a648-1dddd14b132d</sv:value></sv:property></sv:node></sv:node><sv:n" +
comment|//                     "ode sv:name=\"debbi\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Members</sv:value></sv:property><sv:node sv:name=\"debbi\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Members</sv:value></sv:property><sv:property sv:name=\"debbi\" sv:type=\"WeakReference\"><sv:value>d53bedf9-ebb8-3117-a8b8-162d32b4bee2</sv:value></sv:property><sv:property sv:name=\"eddi\" sv:type=\"WeakReference\"><sv:value>1795fa1a-3d20-3a64-996e-eaaeb520a01e</sv:value></sv:property><sv:property sv:name=\"gabi\" sv:type=\"WeakReference\"><sv:value>a0d499c7-5105-3663-8611-a32779a57104</sv:value></sv:property><sv:property sv:name=\"hansi\" sv:type=\"WeakReference\"><sv:value>9ea4d671-8ed1-399a-8401-59487a14d00a</sv:value></sv:property></sv:node><sv:node sv:name=\"hari\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Members</sv:value></sv:property><sv:property sv:name=\"hari\" sv:type=\"WeakReference\"><sv:value>a9bcf1e4-d7b9-3a22-a297-5c812d938889</sv:value></sv:property><sv:property sv:name=\"lisi\" sv:type=\"WeakReference\"><sv:value>dc3a8f16-70d6-3bea-a9b7-b65048a0ac40</sv:value></sv:property></sv:node><sv:node sv:name=\"luzi\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Members</sv:value></sv:property><sv:property sv:name=\"luzi\" sv:type=\"WeakReference\"><sv:value>9ec299fd-3461-3f1a-9749-92a76f2516eb</sv:value></sv:property><sv:property sv:name=\"pipi\" sv:type=" +
comment|//                     "\"WeakReference\"><sv:value>16d5d24f-5b09-3199-9bd4-e5f57bf11237</sv:value></sv:property><sv:property sv:name=\"susi\" sv:type=\"WeakReference\"><sv:value>536931d8-0dec-318c-b3db-9612bdd004d4</sv:value></sv:property></sv:node></sv:node></sv:node></sv:node></sv:node></sv:node>";
comment|//
comment|//        List<String> createdUsers = new LinkedList<String>();
comment|//        NodeImpl groupsNode = (NodeImpl) sImpl.getNode(umgr.getGroupsPath());
comment|//        try {
comment|//            String[] users = {"angi", "adi", "hansi", "lisi", "luzi", "susi", "pipi", "hari", "gabi", "eddi",
comment|//                              "debbi", "cati", "admin", "anonymous"};
comment|//
comment|//            doImport(groupsNode, xml, UserImporter.ImportBehavior.BESTEFFORT);
comment|//            if (!umgr.isAutoSave()) {
comment|//                sImpl.save();
comment|//            }
comment|//
comment|//            for (String user : users) {
comment|//                if (umgr.getAuthorizable(user) == null) {
comment|//                    umgr.createUser(user, user);
comment|//                    createdUsers.add(user);
comment|//                }
comment|//            }
comment|//            if (!umgr.isAutoSave()) {
comment|//                sImpl.save();
comment|//            }
comment|//
comment|//            Authorizable aShrimps = umgr.getAuthorizable("shrimps");
comment|//            assertNotNull(aShrimps);
comment|//            assertTrue(aShrimps.isGroup());
comment|//
comment|//            Group gShrimps = (Group) aShrimps;
comment|//            for (String user : users) {
comment|//                assertTrue(user + " should be member of " + gShrimps, gShrimps.isMember(umgr.getAuthorizable(user)));
comment|//            }
comment|//
comment|//
comment|//        } finally {
comment|//            sImpl.refresh(false);
comment|//            for (String user : createdUsers) {
comment|//                Authorizable a = umgr.getAuthorizable(user);
comment|//                if (a != null&& !a.isGroup()) {
comment|//                    a.remove();
comment|//                }
comment|//            }
comment|//            if (!umgr.isAutoSave()) {
comment|//                sImpl.save();
comment|//            }
comment|//            for (NodeIterator it = groupsNode.getNodes(); it.hasNext(); ) {
comment|//                it.nextNode().remove();
comment|//            }
comment|//            if (!umgr.isAutoSave()) {
comment|//                sImpl.save();
comment|//            }
comment|//        }
comment|//    }
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
name|SAXException
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
name|SAXException
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
specifier|static
name|void
name|assertNotDeclaredMember
parameter_list|(
name|Group
name|gr
parameter_list|,
name|String
name|potentialID
parameter_list|,
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// declared members must not list the invalid entry.
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|it
init|=
name|gr
operator|.
name|getDeclaredMembers
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Authorizable
name|member
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|potentialID
operator|.
name|equals
argument_list|(
name|session
operator|.
name|getNode
argument_list|(
name|member
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getIdentifier
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

