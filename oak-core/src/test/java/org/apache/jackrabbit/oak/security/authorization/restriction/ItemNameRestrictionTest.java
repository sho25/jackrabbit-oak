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
name|Collections
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
name|PropertyType
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
name|ImmutableMap
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
name|NodeTypeConstants
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
name|jcr
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

begin_class
specifier|public
class|class
name|ItemNameRestrictionTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|ValueFactory
name|vf
decl_stmt|;
specifier|private
name|ContentSession
name|testSession
decl_stmt|;
specifier|private
name|Principal
name|testPrincipal
decl_stmt|;
specifier|private
name|Group
name|testGroup
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
name|Tree
name|rootTree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|NodeUtil
name|f
init|=
operator|new
name|NodeUtil
argument_list|(
name|rootTree
argument_list|)
operator|.
name|getOrAddTree
argument_list|(
literal|"a/d/b/e/c/f"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|NodeUtil
name|c
init|=
name|f
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|c
operator|.
name|setString
argument_list|(
literal|"prop"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|c
operator|.
name|setString
argument_list|(
literal|"a"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|testPrincipal
operator|=
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
expr_stmt|;
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
literal|"/a"
argument_list|)
decl_stmt|;
name|vf
operator|=
operator|new
name|ValueFactoryImpl
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addEntry
argument_list|(
name|testPrincipal
argument_list|,
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_ADD_PROPERTIES
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
argument_list|,
literal|true
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Value
operator|>
name|emptyMap
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|AccessControlConstants
operator|.
name|REP_ITEM_NAMES
argument_list|,
operator|new
name|Value
index|[]
block|{
name|vf
operator|.
name|createValue
argument_list|(
literal|"a"
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
block|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"b"
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
block|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"c"
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|acl
operator|.
name|getPath
argument_list|()
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|UserManager
name|uMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|testGroup
operator|=
name|uMgr
operator|.
name|createGroup
argument_list|(
literal|"testGroup"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|testSession
operator|=
name|createTestSession
argument_list|()
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
name|testSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|a
operator|.
name|exists
argument_list|()
condition|)
block|{
name|a
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
name|testRead
parameter_list|()
block|{
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|visible
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a"
argument_list|,
literal|"/a/d/b"
argument_list|,
literal|"/a/d/b/e/c"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|visible
control|)
block|{
name|assertTrue
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
name|p
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|invisible
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/"
argument_list|,
literal|"/a/d"
argument_list|,
literal|"/a/d/b/e"
argument_list|,
literal|"/a/d/b/e/c/f"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|invisible
control|)
block|{
name|assertFalse
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
name|p
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Tree
name|c
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a/d/b/e/c"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|c
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|c
operator|.
name|getProperty
argument_list|(
literal|"prop"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|c
operator|.
name|getProperty
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a"
argument_list|,
literal|"/a/d/b"
argument_list|,
literal|"/a/d/b/e/c"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|paths
control|)
block|{
name|Tree
name|t
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
literal|"b"
argument_list|,
literal|"anyvalue"
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|p
range|:
name|paths
control|)
block|{
name|Tree
name|t
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
name|p
argument_list|)
decl_stmt|;
try|try
block|{
name|t
operator|.
name|setProperty
argument_list|(
literal|"notAllowed"
argument_list|,
literal|"anyvalue"
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModifyProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|c
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a/d/b/e/c"
argument_list|)
decl_stmt|;
try|try
block|{
name|c
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
literal|"anyvalue"
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddChild
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a"
argument_list|,
literal|"/a/d/b"
argument_list|,
literal|"/a/d/b/e/c"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|paths
control|)
block|{
name|NodeUtil
name|t
init|=
operator|new
name|NodeUtil
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
name|p
argument_list|)
argument_list|)
decl_stmt|;
name|t
operator|.
name|addChild
argument_list|(
literal|"c"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveTree
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a/d/b/e/c"
argument_list|,
literal|"/a/d/b"
argument_list|,
literal|"/a"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|paths
control|)
block|{
try|try
block|{
name|testRoot
operator|.
name|getTree
argument_list|(
name|p
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
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveTree2
parameter_list|()
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
literal|"/a"
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addEntry
argument_list|(
name|testPrincipal
argument_list|,
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_CHILD_NODES
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|acl
operator|.
name|getPath
argument_list|()
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a/d/b/e/c"
argument_list|,
literal|"/a/d/b"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|paths
control|)
block|{
name|testRoot
operator|.
name|getTree
argument_list|(
name|p
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
block|}
try|try
block|{
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a"
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
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModifyMembersOnly
parameter_list|()
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
name|String
name|path
init|=
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|,
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
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
name|testPrincipal
argument_list|,
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addEntry
argument_list|(
name|testPrincipal
argument_list|,
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_USER_MANAGEMENT
argument_list|)
argument_list|,
literal|true
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Value
operator|>
name|emptyMap
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Value
index|[]
operator|>
name|of
argument_list|(
name|AccessControlConstants
operator|.
name|REP_ITEM_NAMES
argument_list|,
operator|new
name|Value
index|[]
block|{
name|vf
operator|.
name|createValue
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|acl
operator|.
name|getPath
argument_list|()
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|UserManager
name|uMgr
init|=
name|getUserManager
argument_list|(
name|testRoot
argument_list|)
decl_stmt|;
comment|// adding a group member must succeed
name|Group
name|gr
init|=
name|uMgr
operator|.
name|getAuthorizable
argument_list|(
name|testGroup
operator|.
name|getID
argument_list|()
argument_list|,
name|Group
operator|.
name|class
argument_list|)
decl_stmt|;
name|User
name|u
init|=
name|uMgr
operator|.
name|getAuthorizable
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|,
name|User
operator|.
name|class
argument_list|)
decl_stmt|;
name|gr
operator|.
name|addMember
argument_list|(
name|u
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// changing the pw property of the test user must fail
try|try
block|{
name|u
operator|.
name|changePassword
argument_list|(
literal|"blub"
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
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
name|acl
operator|!=
literal|null
condition|)
block|{
name|acMgr
operator|.
name|removePolicy
argument_list|(
name|acl
operator|.
name|getPath
argument_list|()
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
block|}
block|}
block|}
end_class

end_unit

