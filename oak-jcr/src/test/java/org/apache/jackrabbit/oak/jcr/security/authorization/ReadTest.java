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
name|authorization
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
name|PathNotFoundException
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
name|Privilege
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|util
operator|.
name|TraversingItemVisitor
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
name|commons
operator|.
name|jackrabbit
operator|.
name|authorization
operator|.
name|AccessControlUtils
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
name|EveryonePrincipal
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
name|PrivilegeConstants
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

begin_comment
comment|/**  * Permission evaluation tests related to {@link javax.jcr.security.Privilege#JCR_READ} privilege.  */
end_comment

begin_class
specifier|public
class|class
name|ReadTest
extends|extends
name|AbstractEvaluationTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testChildNodes
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* create some new nodes below 'path' */
name|Node
name|n
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|n
operator|=
name|n
operator|.
name|addNode
argument_list|(
name|nodeName4
argument_list|,
name|testNodeType
argument_list|)
expr_stmt|;
block|}
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|/* make sure the same privileges/permissions are granted as at path. */
name|testSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|String
name|childPath
init|=
name|n
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|readPrivileges
argument_list|,
name|testAcMgr
operator|.
name|getPrivileges
argument_list|(
name|childPath
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|checkPermission
argument_list|(
name|childPath
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonExistingItem
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*           precondition:           testuser must have READ-only permission on the root node and below         */
name|String
name|rootPath
init|=
name|testSession
operator|.
name|getRootNode
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|assertReadOnly
argument_list|(
name|rootPath
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|checkPermission
argument_list|(
name|rootPath
operator|+
literal|"nonExistingItem"
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetItem
parameter_list|()
throws|throws
name|Exception
block|{
comment|// withdraw READ privilege to 'testUser' at 'path'
name|deny
argument_list|(
name|path
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|childNPath
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|getItem
argument_list|(
name|childNPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testItemExists
parameter_list|()
throws|throws
name|Exception
block|{
comment|// withdraw READ privilege to 'testUser' at 'path'
name|deny
argument_list|(
name|path
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|childNPath
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|itemExists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|itemExists
argument_list|(
name|childNPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeniedReadOnSubTree
parameter_list|()
throws|throws
name|Exception
block|{
comment|// withdraw READ privilege to 'testUser' at 'path'
name|deny
argument_list|(
name|childNPath
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
comment|/*          testuser must now have          - READ-only permission at path          - READ-only permission for the child-props of path           testuser must not have          - any permission on child-node and all its subtree         */
comment|// must still have read-access to path, ...
name|assertTrue
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|path
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
comment|// ... siblings of childN
name|testSession
operator|.
name|getNode
argument_list|(
name|childNPath2
argument_list|)
expr_stmt|;
comment|// ... and props of path
name|assertTrue
argument_list|(
name|n
operator|.
name|getProperties
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|//testSession must not have access to 'childNPath'
name|assertFalse
argument_list|(
name|testSession
operator|.
name|itemExists
argument_list|(
name|childNPath
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|testSession
operator|.
name|getNode
argument_list|(
name|childNPath
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Read access has been denied -> cannot retrieve child node."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
comment|// ok.
block|}
comment|/*         -> must not have access to subtree below 'childNPath'         */
name|assertFalse
argument_list|(
name|testSession
operator|.
name|itemExists
argument_list|(
name|childchildPPath
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|testSession
operator|.
name|getItem
argument_list|(
name|childchildPPath
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Read access has been denied -> cannot retrieve prop below child node."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
comment|// ok.
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllowWriteDenyRead
parameter_list|()
throws|throws
name|Exception
block|{
comment|// allow 'testUser' to write at 'path'
name|allow
argument_list|(
name|path
argument_list|,
name|repWritePrivileges
argument_list|)
expr_stmt|;
comment|// deny read access
name|deny
argument_list|(
name|path
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
comment|// testuser must not be able to access that node
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDenyRoot
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|deny
argument_list|(
literal|"/"
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|getRootNode
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"root should not be accessible"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// expected exception
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDenyPath
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|deny
argument_list|(
name|path
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"nodet should not be accessible"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// expected exception
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadDenied
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* deny READ privilege for testUser at 'path' */
name|deny
argument_list|(
name|path
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
comment|/*          allow READ privilege for testUser at 'childNPath'          */
name|allow
argument_list|(
name|childNPath
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|childNPath
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|childNPath
argument_list|)
decl_stmt|;
name|n
operator|.
name|getDefinition
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDenyUserAllowGroup
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*          deny READ privilege for testUser at 'path'          */
name|deny
argument_list|(
name|path
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
comment|/*          allow READ privilege for group at 'path'          */
name|allow
argument_list|(
name|path
argument_list|,
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllowGroupDenyUser
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*         allow READ privilege for group at 'path'         */
name|allow
argument_list|(
name|path
argument_list|,
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
comment|/*         deny READ privilege for testUser at 'path'         */
name|deny
argument_list|(
name|path
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllowUserDenyGroup
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*          allow READ privilege for testUser at 'path'          */
name|allow
argument_list|(
name|path
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
comment|/*          deny READ privilege for group at 'path'          */
name|deny
argument_list|(
name|path
argument_list|,
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDenyGroupAllowUser
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*          deny READ privilege for group at 'path'          */
name|deny
argument_list|(
name|path
argument_list|,
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
comment|/*          allow READ privilege for testUser at 'path'          */
name|allow
argument_list|(
name|path
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDenyGroupAllowEveryone
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*          deny READ privilege for group at 'path'          */
name|deny
argument_list|(
name|path
argument_list|,
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
comment|/*          allow READ privilege for everyone at 'path'          */
name|allow
argument_list|(
name|path
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllowEveryoneDenyGroup
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*          allow READ privilege for everyone at 'path'          */
name|allow
argument_list|(
name|path
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
comment|/*          deny READ privilege for group at 'path'          */
name|deny
argument_list|(
name|path
argument_list|,
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDenyGroupPathAllowEveryoneChildPath
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*          deny READ privilege for group at 'path'          */
name|deny
argument_list|(
name|path
argument_list|,
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
comment|/*          allow READ privilege for everyone at 'childNPath'          */
name|allow
argument_list|(
name|path
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|childNPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllowEveryonePathDenyGroupChildPath
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*          allow READ privilege for everyone at 'path'          */
name|allow
argument_list|(
name|path
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
comment|/*          deny READ privilege for group at 'childNPath'          */
name|deny
argument_list|(
name|childNPath
argument_list|,
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|childNPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllowUserPathDenyGroupChildPath
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*          allow READ privilege for testUser at 'path'          */
name|allow
argument_list|(
name|path
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
comment|/*          deny READ privilege for group at 'childPath'          */
name|deny
argument_list|(
name|path
argument_list|,
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|childNPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDenyGroupPathAllowUserChildPath
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*          deny READ privilege for group at 'path'          */
name|deny
argument_list|(
name|path
argument_list|,
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
comment|/*          allow READ privilege for testUser at 'childNPath'          */
name|allow
argument_list|(
name|path
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|childNPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDenyUserPathAllowGroupChildPath
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*          deny READ privilege for testUser at 'path'          */
name|deny
argument_list|(
name|path
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
comment|/*          allow READ privilege for group at 'childNPath'          */
name|allow
argument_list|(
name|path
argument_list|,
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|childNPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllowGroupPathDenyUserChildPath
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*         allow READ privilege for the group at 'path'         */
name|allow
argument_list|(
name|path
argument_list|,
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
comment|/*         deny READ privilege for testUser at 'childNPath'         */
name|deny
argument_list|(
name|path
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|childNPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGlobRestriction
parameter_list|()
throws|throws
name|Exception
block|{
name|deny
argument_list|(
name|path
argument_list|,
name|readPrivileges
argument_list|,
name|createGlobRestriction
argument_list|(
literal|"*/"
operator|+
name|jcrPrimaryType
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|readPrivileges
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|path
argument_list|,
name|javax
operator|.
name|jcr
operator|.
name|Session
operator|.
name|ACTION_READ
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|childNPath
argument_list|,
name|readPrivileges
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|childNPath
argument_list|,
name|javax
operator|.
name|jcr
operator|.
name|Session
operator|.
name|ACTION_READ
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|getNode
argument_list|(
name|childNPath
argument_list|)
expr_stmt|;
name|String
name|propPath
init|=
name|path
operator|+
literal|'/'
operator|+
name|jcrPrimaryType
decl_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|propPath
argument_list|,
name|javax
operator|.
name|jcr
operator|.
name|Session
operator|.
name|ACTION_READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|propertyExists
argument_list|(
name|propPath
argument_list|)
argument_list|)
expr_stmt|;
name|propPath
operator|=
name|childNPath
operator|+
literal|'/'
operator|+
name|jcrPrimaryType
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|propPath
argument_list|,
name|javax
operator|.
name|jcr
operator|.
name|Session
operator|.
name|ACTION_READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|propertyExists
argument_list|(
name|propPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGlobRestriction2
parameter_list|()
throws|throws
name|Exception
block|{
name|Group
name|group2
init|=
name|getUserManager
argument_list|(
name|superuser
argument_list|)
operator|.
name|createGroup
argument_list|(
literal|"group2"
argument_list|)
decl_stmt|;
name|Group
name|group3
init|=
name|getUserManager
argument_list|(
name|superuser
argument_list|)
operator|.
name|createGroup
argument_list|(
literal|"group3"
argument_list|)
decl_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
try|try
block|{
name|Privilege
index|[]
name|readPrivs
init|=
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|)
decl_stmt|;
name|modify
argument_list|(
name|path
argument_list|,
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivs
argument_list|,
literal|true
argument_list|,
name|createGlobRestriction
argument_list|(
literal|"/*"
argument_list|)
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|path
argument_list|,
name|group2
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivs
argument_list|)
expr_stmt|;
name|deny
argument_list|(
name|path
argument_list|,
name|group3
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivs
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|new
name|HashSet
argument_list|<
name|Principal
argument_list|>
argument_list|()
decl_stmt|;
name|principals
operator|.
name|add
argument_list|(
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|principals
operator|.
name|add
argument_list|(
name|group2
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|principals
operator|.
name|add
argument_list|(
name|group3
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|(
operator|(
name|JackrabbitAccessControlManager
operator|)
name|acMgr
operator|)
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|principals
argument_list|,
name|readPrivs
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|(
operator|(
name|JackrabbitAccessControlManager
operator|)
name|acMgr
operator|)
operator|.
name|hasPrivileges
argument_list|(
name|childNPath
argument_list|,
name|principals
argument_list|,
name|readPrivs
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|group2
operator|.
name|remove
argument_list|()
expr_stmt|;
name|group3
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
annotation|@
name|Test
specifier|public
name|void
name|testGlobRestriction3
parameter_list|()
throws|throws
name|Exception
block|{
name|Group
name|group2
init|=
name|getUserManager
argument_list|(
name|superuser
argument_list|)
operator|.
name|createGroup
argument_list|(
literal|"group2"
argument_list|)
decl_stmt|;
name|Group
name|group3
init|=
name|getUserManager
argument_list|(
name|superuser
argument_list|)
operator|.
name|createGroup
argument_list|(
literal|"group3"
argument_list|)
decl_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
try|try
block|{
name|Privilege
index|[]
name|readPrivs
init|=
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|)
decl_stmt|;
name|allow
argument_list|(
name|path
argument_list|,
name|group2
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivs
argument_list|)
expr_stmt|;
name|deny
argument_list|(
name|path
argument_list|,
name|group3
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivs
argument_list|)
expr_stmt|;
name|modify
argument_list|(
name|path
argument_list|,
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivs
argument_list|,
literal|true
argument_list|,
name|createGlobRestriction
argument_list|(
literal|"/*"
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|new
name|HashSet
argument_list|<
name|Principal
argument_list|>
argument_list|()
decl_stmt|;
name|principals
operator|.
name|add
argument_list|(
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|principals
operator|.
name|add
argument_list|(
name|group2
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|principals
operator|.
name|add
argument_list|(
name|group3
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|(
operator|(
name|JackrabbitAccessControlManager
operator|)
name|acMgr
operator|)
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|principals
argument_list|,
name|readPrivs
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|JackrabbitAccessControlManager
operator|)
name|acMgr
operator|)
operator|.
name|hasPrivileges
argument_list|(
name|childNPath
argument_list|,
name|principals
argument_list|,
name|readPrivs
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|group2
operator|.
name|remove
argument_list|()
expr_stmt|;
name|group3
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
annotation|@
name|Test
specifier|public
name|void
name|testGlobRestriction4
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|a
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|allow
argument_list|(
name|path
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|deny
argument_list|(
name|path
argument_list|,
name|readPrivileges
argument_list|,
name|createGlobRestriction
argument_list|(
literal|"*/anotherpath"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|aPath
init|=
name|a
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|aPath
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|aPath
argument_list|)
decl_stmt|;
name|Node
name|test
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|test
operator|.
name|hasNode
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n2
init|=
name|test
operator|.
name|getNode
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isSame
argument_list|(
name|n2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGlobRestriction5
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|a
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|allow
argument_list|(
name|path
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|deny
argument_list|(
name|path
argument_list|,
name|readPrivileges
argument_list|,
name|createGlobRestriction
argument_list|(
literal|"*/anotherpath"
argument_list|)
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|a
operator|.
name|getPath
argument_list|()
argument_list|,
name|repWritePrivileges
argument_list|)
expr_stmt|;
name|String
name|aPath
init|=
name|a
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|aPath
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|aPath
argument_list|)
decl_stmt|;
name|Node
name|test
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|test
operator|.
name|hasNode
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n2
init|=
name|test
operator|.
name|getNode
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isSame
argument_list|(
name|n2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-878">OAK-878 :      * IllegalArgumentException while adding/removing permission to user/group</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testImplicitReorder
parameter_list|()
throws|throws
name|Exception
block|{
name|allow
argument_list|(
name|path
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertEntry
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|path
argument_list|,
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertEntry
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|deny
argument_list|(
name|path
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertEntry
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|deny
argument_list|(
name|path
argument_list|,
name|getTestGroup
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertEntry
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|path
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChildNodesWithAccessCheck
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|nodeToDeny
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"nodeToDeny"
argument_list|)
decl_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|//Deny access to one of the child node
name|deny
argument_list|(
name|nodeToDeny
operator|.
name|getPath
argument_list|()
argument_list|,
name|privilegesFromName
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|NodeIterator
name|it
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
operator|.
name|getNodes
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|childNodeNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
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
name|Node
name|n
init|=
name|it
operator|.
name|nextNode
argument_list|()
decl_stmt|;
name|childNodeNames
operator|.
name|add
argument_list|(
name|n
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//Denied node should not show up in the child node names list
name|assertFalse
argument_list|(
name|childNodeNames
operator|.
name|contains
argument_list|(
literal|"nodeToDeny"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertEntry
parameter_list|(
specifier|final
name|int
name|index
parameter_list|,
specifier|final
name|boolean
name|isAllow
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|AccessControlEntry
name|first
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|superuser
argument_list|,
name|path
argument_list|)
operator|.
name|getAccessControlEntries
argument_list|()
index|[
name|index
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|first
operator|.
name|getPrincipal
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
literal|"/jcr:system/rep:permissionStore/default/"
operator|+
name|testUser
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|TraversingItemVisitor
name|v
init|=
operator|new
name|TraversingItemVisitor
operator|.
name|Default
argument_list|(
literal|true
argument_list|,
operator|-
literal|1
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|entering
parameter_list|(
name|Node
name|node
parameter_list|,
name|int
name|level
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|node
operator|.
name|isNodeType
argument_list|(
literal|"rep:Permissions"
argument_list|)
operator|&&
name|node
operator|.
name|hasProperty
argument_list|(
literal|"rep:accessControlledPath"
argument_list|)
operator|&&
name|path
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getProperty
argument_list|(
literal|"rep:accessControlledPath"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|index
argument_list|,
name|node
operator|.
name|getProperty
argument_list|(
literal|"rep:index"
argument_list|)
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|isAllow
argument_list|,
name|node
operator|.
name|getProperty
argument_list|(
literal|"rep:isAllow"
argument_list|)
operator|.
name|getBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

