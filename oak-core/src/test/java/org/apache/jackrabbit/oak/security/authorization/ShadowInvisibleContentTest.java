begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|security
operator|.
name|AccessControlManager
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
name|JackrabbitAccessControlList
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
name|CommitFailedException
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
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
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
name|ShadowInvisibleContentTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|USER_ID
init|=
literal|"test"
decl_stmt|;
specifier|private
name|Principal
name|userPrincipal
decl_stmt|;
annotation|@
name|Before
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
name|User
name|user
init|=
name|getUserManager
argument_list|()
operator|.
name|createUser
argument_list|(
name|USER_ID
argument_list|,
name|USER_ID
argument_list|)
decl_stmt|;
name|userPrincipal
operator|=
name|user
operator|.
name|getPrincipal
argument_list|()
expr_stmt|;
name|NodeUtil
name|a
init|=
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|a
operator|.
name|setString
argument_list|(
literal|"x"
argument_list|,
literal|"xValue"
argument_list|)
expr_stmt|;
name|NodeUtil
name|b
init|=
name|a
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|b
operator|.
name|setString
argument_list|(
literal|"y"
argument_list|,
literal|"yValue"
argument_list|)
expr_stmt|;
name|NodeUtil
name|c
init|=
name|b
operator|.
name|addChild
argument_list|(
literal|"c"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|c
operator|.
name|setString
argument_list|(
literal|"z"
argument_list|,
literal|"zValue"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setupPermission
parameter_list|(
name|Principal
name|principal
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
name|String
name|privilegeName
parameter_list|)
throws|throws
name|CommitFailedException
throws|,
name|RepositoryException
block|{
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addEntry
argument_list|(
name|principal
argument_list|,
name|privilegesFromNames
argument_list|(
name|privilegeName
argument_list|)
argument_list|,
name|isAllow
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|path
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Root
name|getLatestRoot
parameter_list|()
throws|throws
name|LoginException
throws|,
name|NoSuchWorkspaceException
block|{
name|ContentSession
name|contentSession
init|=
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|USER_ID
argument_list|,
name|USER_ID
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|contentSession
operator|.
name|getLatestRoot
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testShadowInvisibleNode
parameter_list|()
throws|throws
name|CommitFailedException
throws|,
name|RepositoryException
throws|,
name|LoginException
block|{
name|setupPermission
argument_list|(
name|userPrincipal
argument_list|,
literal|"/a"
argument_list|,
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|userPrincipal
argument_list|,
literal|"/a/b"
argument_list|,
literal|false
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|userPrincipal
argument_list|,
literal|"/a/b/c"
argument_list|,
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
expr_stmt|;
name|Root
name|root
init|=
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|a
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
comment|// /b not visible to this session
name|assertFalse
argument_list|(
name|a
operator|.
name|hasChild
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
comment|// shadow /b with transient node of the same name
name|Tree
name|b
init|=
name|a
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|hasChild
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|hasChild
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|Ignore
comment|// TODO incomplete implementation of PermissionValidator.childNodeChanged()
specifier|public
name|void
name|testShadowInvisibleProperty
parameter_list|()
throws|throws
name|CommitFailedException
throws|,
name|RepositoryException
throws|,
name|LoginException
block|{
name|setupPermission
argument_list|(
name|userPrincipal
argument_list|,
literal|"/a"
argument_list|,
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|userPrincipal
argument_list|,
literal|"/a"
argument_list|,
literal|false
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
expr_stmt|;
name|Root
name|root
init|=
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|a
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
comment|// /a/x not visible to this session
name|assertNull
argument_list|(
name|a
operator|.
name|getProperty
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
comment|// shadow /a/x with transient property of the same name
name|a
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
literal|"xValue1"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|a
operator|.
name|getProperty
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|Ignore
comment|// FIXME how do we handle the case where the shadowing item is the same as the shadowing item?
specifier|public
name|void
name|testShadowInvisibleProperty2
parameter_list|()
throws|throws
name|CommitFailedException
throws|,
name|RepositoryException
throws|,
name|LoginException
block|{
name|setupPermission
argument_list|(
name|userPrincipal
argument_list|,
literal|"/a"
argument_list|,
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|userPrincipal
argument_list|,
literal|"/a"
argument_list|,
literal|false
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
expr_stmt|;
name|Root
name|root
init|=
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|a
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
comment|// /a/x not visible to this session
name|assertNull
argument_list|(
name|a
operator|.
name|getProperty
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
comment|// shadow /a/x with transient property of the same name
name|a
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
literal|"xValue"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|a
operator|.
name|getProperty
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

