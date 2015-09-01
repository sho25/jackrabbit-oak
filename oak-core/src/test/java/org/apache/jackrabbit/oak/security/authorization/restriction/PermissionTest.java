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
name|authorization
operator|.
name|restriction
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
name|Value
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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
name|accesscontrol
operator|.
name|AccessControlConstants
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
name|apache
operator|.
name|jackrabbit
operator|.
name|value
operator|.
name|StringValue
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
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
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
class|class
name|PermissionTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TEST_ROOT_PATH
init|=
literal|"/testRoot"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_A_PATH
init|=
literal|"/testRoot/a"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_B_PATH
init|=
literal|"/testRoot/a/b"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_C_PATH
init|=
literal|"/testRoot/a/b/c"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_D_PATH
init|=
literal|"/testRoot/a/b/c/d"
decl_stmt|;
specifier|private
name|NodeUtil
name|testRootNode
decl_stmt|;
specifier|private
name|Principal
name|testPrincipal
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
name|NodeUtil
name|rootNode
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
decl_stmt|;
name|testRootNode
operator|=
name|rootNode
operator|.
name|addChild
argument_list|(
literal|"testRoot"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|NodeUtil
name|a
init|=
name|testRootNode
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
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
name|addChild
argument_list|(
literal|"d"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|testPrincipal
operator|=
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
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
comment|// revert uncommitted changes
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
comment|// remove all test content
name|root
operator|.
name|getTree
argument_list|(
name|TEST_ROOT_PATH
argument_list|)
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
finally|finally
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addEntry
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|grant
parameter_list|,
name|String
name|restriction
parameter_list|,
name|String
modifier|...
name|privilegeNames
parameter_list|)
throws|throws
name|Exception
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
if|if
condition|(
name|restriction
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|rs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
argument_list|()
decl_stmt|;
name|rs
operator|.
name|put
argument_list|(
literal|"rep:glob"
argument_list|,
operator|new
name|StringValue
argument_list|(
name|restriction
argument_list|)
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addEntry
argument_list|(
name|testPrincipal
argument_list|,
name|AccessControlUtils
operator|.
name|privilegesFromNames
argument_list|(
name|acMgr
argument_list|,
name|privilegeNames
argument_list|)
argument_list|,
name|grant
argument_list|,
name|rs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|acl
operator|.
name|addEntry
argument_list|(
name|testPrincipal
argument_list|,
name|AccessControlUtils
operator|.
name|privilegesFromNames
argument_list|(
name|acMgr
argument_list|,
name|privilegeNames
argument_list|)
argument_list|,
name|grant
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|Test
specifier|public
name|void
name|testHasPermission
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create permissions
comment|// allow rep:write      /testroot
comment|// allow jcr:removeNode /testroot/a/b
comment|// deny  jcr:removeNode /testroot/a/b/c
name|addEntry
argument_list|(
name|TEST_ROOT_PATH
argument_list|,
literal|true
argument_list|,
literal|""
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_WRITE
argument_list|)
expr_stmt|;
name|addEntry
argument_list|(
name|TEST_B_PATH
argument_list|,
literal|true
argument_list|,
literal|""
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
expr_stmt|;
name|addEntry
argument_list|(
name|TEST_C_PATH
argument_list|,
literal|false
argument_list|,
literal|""
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
expr_stmt|;
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
try|try
block|{
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|testRoot
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"user should not have remove node on /a/b/c"
argument_list|,
name|acMgr
operator|.
name|hasPrivileges
argument_list|(
name|TEST_C_PATH
argument_list|,
name|AccessControlUtils
operator|.
name|privilegesFromNames
argument_list|(
name|acMgr
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"user should have remove node on /a/b"
argument_list|,
name|acMgr
operator|.
name|hasPrivileges
argument_list|(
name|TEST_B_PATH
argument_list|,
name|AccessControlUtils
operator|.
name|privilegesFromNames
argument_list|(
name|acMgr
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|testRoot
operator|.
name|getTree
argument_list|(
name|TEST_C_PATH
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"removing node on /a/b/c should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// all ok
block|}
block|}
finally|finally
block|{
name|testSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-3324"
argument_list|)
specifier|public
name|void
name|testHasPermissionWithRestrictions
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create permissions
comment|// allow rep:write      /testroot
comment|// deny  jcr:removeNode /testroot/a  glob=*/c
comment|// allow jcr:removeNode /testroot/a  glob=*/b
comment|// allow jcr:removeNode /testroot/a  glob=*/c/*
name|addEntry
argument_list|(
name|TEST_ROOT_PATH
argument_list|,
literal|true
argument_list|,
literal|""
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_WRITE
argument_list|)
expr_stmt|;
name|addEntry
argument_list|(
name|TEST_A_PATH
argument_list|,
literal|false
argument_list|,
literal|"*/c"
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
expr_stmt|;
name|addEntry
argument_list|(
name|TEST_A_PATH
argument_list|,
literal|true
argument_list|,
literal|"*/b"
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
expr_stmt|;
name|addEntry
argument_list|(
name|TEST_A_PATH
argument_list|,
literal|true
argument_list|,
literal|"*/c/*"
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
expr_stmt|;
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
try|try
block|{
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|testRoot
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"user should not have remove node on /a/b/c"
argument_list|,
name|acMgr
operator|.
name|hasPrivileges
argument_list|(
name|TEST_C_PATH
argument_list|,
name|AccessControlUtils
operator|.
name|privilegesFromNames
argument_list|(
name|acMgr
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"user should have remove node on /a/b"
argument_list|,
name|acMgr
operator|.
name|hasPrivileges
argument_list|(
name|TEST_B_PATH
argument_list|,
name|AccessControlUtils
operator|.
name|privilegesFromNames
argument_list|(
name|acMgr
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"user should have remove node on /a/b/c/d"
argument_list|,
name|acMgr
operator|.
name|hasPrivileges
argument_list|(
name|TEST_D_PATH
argument_list|,
name|AccessControlUtils
operator|.
name|privilegesFromNames
argument_list|(
name|acMgr
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// should be able to remove /a/b/c/d
name|testRoot
operator|.
name|getTree
argument_list|(
name|TEST_D_PATH
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
name|testRoot
operator|.
name|getTree
argument_list|(
name|TEST_C_PATH
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"removing node on /a/b/c should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// all ok
block|}
block|}
finally|finally
block|{
name|testSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

