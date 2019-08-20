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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|cug
operator|.
name|impl
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
name|Set
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
name|AccessDeniedException
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
name|AccessControlPolicyIterator
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
name|base
operator|.
name|Preconditions
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
name|ImmutableSet
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
name|plugins
operator|.
name|tree
operator|.
name|TreeUtil
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
name|authorization
operator|.
name|cug
operator|.
name|CugExclude
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
name|cug
operator|.
name|CugPolicy
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
name|permission
operator|.
name|PermissionProvider
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
name|permission
operator|.
name|TreePermission
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
name|apache
operator|.
name|jackrabbit
operator|.
name|util
operator|.
name|Text
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

begin_comment
comment|/**  * Base class for CUG related test that setup the authorization configuration  * to expose the CUG specific implementations of {@code AccessControlManager}  * and {@code PermissionProvider}.  */
end_comment

begin_class
specifier|public
class|class
name|AbstractCugTest
extends|extends
name|AbstractSecurityTest
implements|implements
name|CugConstants
implements|,
name|NodeTypeConstants
block|{
specifier|static
specifier|final
name|String
name|SUPPORTED_PATH
init|=
literal|"/content"
decl_stmt|;
specifier|static
specifier|final
name|String
name|SUPPORTED_PATH2
init|=
literal|"/content2"
decl_stmt|;
specifier|static
specifier|final
name|String
name|SUPPORTED_PATH3
init|=
literal|"/some/content/tree"
decl_stmt|;
specifier|static
specifier|final
name|String
name|UNSUPPORTED_PATH
init|=
literal|"/testNode"
decl_stmt|;
specifier|static
specifier|final
name|String
name|INVALID_PATH
init|=
literal|"/path/to/non/existing/tree"
decl_stmt|;
specifier|static
specifier|final
name|String
index|[]
name|SUPPORTED_PATHS
init|=
block|{
name|SUPPORTED_PATH
block|,
name|SUPPORTED_PATH2
block|,
name|SUPPORTED_PATH3
block|}
decl_stmt|;
specifier|static
specifier|final
name|ConfigurationParameters
name|CUG_CONFIG
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_SUPPORTED_PATHS
argument_list|,
name|SUPPORTED_PATHS
argument_list|,
name|CugConstants
operator|.
name|PARAM_CUG_ENABLED
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_GROUP_ID
init|=
literal|"testGroup"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_USER2_ID
init|=
literal|"testUser2"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
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
comment|/**          * Create tree structure:          *          * + root          *   + content          *     + subtree          *   + content2          *   + some          *     + content          *       + tree          *   + testNode          *     + child          */
name|Tree
name|rootNode
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|createTrees
argument_list|(
name|rootNode
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|,
literal|"content"
argument_list|,
literal|"subtree"
argument_list|)
expr_stmt|;
name|createTrees
argument_list|(
name|rootNode
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|,
literal|"content2"
argument_list|)
expr_stmt|;
name|createTrees
argument_list|(
name|rootNode
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|,
literal|"some"
argument_list|,
literal|"content"
argument_list|,
literal|"tree"
argument_list|)
expr_stmt|;
name|createTrees
argument_list|(
name|rootNode
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|,
literal|"testNode"
argument_list|,
literal|"child"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
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
comment|// revert transient pending changes (that might be invalid)
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
comment|// remove the test group and second test user
name|Authorizable
name|testGroup
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|TEST_GROUP_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|testGroup
operator|!=
literal|null
condition|)
block|{
name|testGroup
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|Authorizable
name|testUser2
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|TEST_USER2_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|testUser2
operator|!=
literal|null
condition|)
block|{
name|testUser2
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|p
range|:
operator|new
name|String
index|[]
block|{
name|SUPPORTED_PATH
block|,
name|SUPPORTED_PATH2
block|,
name|Text
operator|.
name|getAbsoluteParent
argument_list|(
name|SUPPORTED_PATH3
argument_list|,
literal|0
argument_list|)
block|,
name|UNSUPPORTED_PATH
block|}
control|)
block|{
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|exists
argument_list|()
condition|)
block|{
name|t
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
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
annotation|@
name|Override
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
name|CugSecurityProvider
operator|.
name|newTestSecurityProvider
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
annotation|@
name|Override
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|AuthorizationConfiguration
operator|.
name|NAME
argument_list|,
name|CUG_CONFIG
argument_list|)
return|;
block|}
name|CugPermissionProvider
name|createCugPermissionProvider
parameter_list|(
annotation|@
name|NotNull
name|Set
argument_list|<
name|String
argument_list|>
name|supportedPaths
parameter_list|,
annotation|@
name|NotNull
name|Principal
modifier|...
name|principals
parameter_list|)
block|{
return|return
operator|new
name|CugPermissionProvider
argument_list|(
name|root
argument_list|,
name|root
operator|.
name|getContentSession
argument_list|()
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|principals
argument_list|)
argument_list|,
name|supportedPaths
argument_list|,
name|getConfig
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getContext
argument_list|()
argument_list|,
name|getRootProvider
argument_list|()
argument_list|,
name|getTreeProvider
argument_list|()
argument_list|)
return|;
block|}
name|void
name|createTrees
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|,
annotation|@
name|NotNull
name|String
name|ntName
parameter_list|,
annotation|@
name|NotNull
name|String
modifier|...
name|names
parameter_list|)
throws|throws
name|AccessDeniedException
block|{
name|Tree
name|parent
init|=
name|tree
decl_stmt|;
for|for
control|(
name|String
name|n
range|:
name|names
control|)
block|{
name|parent
operator|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|parent
argument_list|,
name|n
argument_list|,
name|ntName
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|setupCugsAndAcls
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManager
name|uMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|Principal
name|testGroupPrincipal
init|=
name|getTestGroupPrincipal
argument_list|()
decl_stmt|;
name|User
name|testUser2
init|=
name|uMgr
operator|.
name|createUser
argument_list|(
name|TEST_USER2_ID
argument_list|,
name|TEST_USER2_ID
argument_list|)
decl_stmt|;
operator|(
operator|(
name|Group
operator|)
name|uMgr
operator|.
name|getAuthorizable
argument_list|(
name|testGroupPrincipal
argument_list|)
operator|)
operator|.
name|addMember
argument_list|(
name|testUser2
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|User
name|testUser
init|=
name|getTestUser
argument_list|()
decl_stmt|;
comment|// add more child nodes
name|Tree
name|n
init|=
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH
argument_list|)
decl_stmt|;
name|createTrees
argument_list|(
name|n
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|createTrees
argument_list|(
name|n
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|,
literal|"aa"
argument_list|,
literal|"bb"
argument_list|,
literal|"cc"
argument_list|)
expr_stmt|;
comment|// create cugs
comment|// - /content/a     : allow testGroup, deny everyone
comment|// - /content/aa/bb : allow testGroup, deny everyone
comment|// - /content/a/b/c : allow everyone,  deny testGroup (isolated)
comment|// - /content2      : allow everyone,  deny testGroup (isolated)
name|createCug
argument_list|(
literal|"/content/a"
argument_list|,
name|testGroupPrincipal
argument_list|)
expr_stmt|;
name|createCug
argument_list|(
literal|"/content/aa/bb"
argument_list|,
name|testGroupPrincipal
argument_list|)
expr_stmt|;
name|createCug
argument_list|(
literal|"/content/a/b/c"
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|createCug
argument_list|(
literal|"/content2"
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
comment|// setup regular acl at /content:
comment|// - testUser  ; allow ; jcr:read
comment|// - testGroup ; allow ; jcr:read, jcr:write, jcr:readAccessControl
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|AccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
literal|"/content"
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|testGroupPrincipal
argument_list|,
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_WRITE
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
literal|"/content"
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
name|CugExclude
name|getExclude
parameter_list|()
block|{
return|return
operator|new
name|CugExclude
operator|.
name|Default
argument_list|()
return|;
block|}
name|void
name|createCug
parameter_list|(
annotation|@
name|NotNull
name|String
name|absPath
parameter_list|,
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|)
throws|throws
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
name|AccessControlPolicyIterator
name|it
init|=
name|acMgr
operator|.
name|getApplicablePolicies
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|AccessControlPolicy
name|policy
init|=
name|it
operator|.
name|nextAccessControlPolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|policy
operator|instanceof
name|CugPolicy
condition|)
block|{
operator|(
operator|(
name|CugPolicy
operator|)
name|policy
operator|)
operator|.
name|addPrincipals
argument_list|(
name|principal
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|absPath
argument_list|,
name|policy
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to create CUG at "
operator|+
name|absPath
argument_list|)
throw|;
block|}
specifier|static
name|void
name|createCug
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|String
name|path
parameter_list|,
annotation|@
name|NotNull
name|String
name|principalName
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|tree
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|TreeUtil
operator|.
name|addMixin
argument_list|(
name|tree
argument_list|,
name|MIX_REP_CUG_MIXIN
argument_list|,
name|root
operator|.
name|getTree
argument_list|(
name|NODE_TYPES_PATH
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|tree
argument_list|,
name|REP_CUG_POLICY
argument_list|,
name|NT_REP_CUG_POLICY
argument_list|)
operator|.
name|setProperty
argument_list|(
name|REP_PRINCIPAL_NAMES
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|principalName
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
block|}
name|Principal
name|getTestGroupPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManager
name|uMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|Group
name|g
init|=
name|uMgr
operator|.
name|getAuthorizable
argument_list|(
name|TEST_GROUP_ID
argument_list|,
name|Group
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|g
operator|==
literal|null
condition|)
block|{
name|g
operator|=
name|uMgr
operator|.
name|createGroup
argument_list|(
name|TEST_GROUP_ID
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
return|return
name|g
operator|.
name|getPrincipal
argument_list|()
return|;
block|}
name|ContentSession
name|createTestSession2
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|TEST_USER2_ID
argument_list|,
name|TEST_USER2_ID
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|static
name|void
name|assertCugPermission
parameter_list|(
annotation|@
name|NotNull
name|TreePermission
name|tp
parameter_list|,
name|boolean
name|isSupportedPath
parameter_list|)
block|{
if|if
condition|(
name|isSupportedPath
condition|)
block|{
name|assertTrue
argument_list|(
name|tp
operator|instanceof
name|CugTreePermission
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|tp
operator|instanceof
name|EmptyCugTreePermission
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|TreePermission
name|getTreePermission
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|String
name|path
parameter_list|,
annotation|@
name|NotNull
name|PermissionProvider
name|pp
parameter_list|)
block|{
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|TreePermission
name|tp
init|=
name|pp
operator|.
name|getTreePermission
argument_list|(
name|t
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|segm
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|t
operator|=
name|t
operator|.
name|getChild
argument_list|(
name|segm
argument_list|)
expr_stmt|;
name|tp
operator|=
name|pp
operator|.
name|getTreePermission
argument_list|(
name|t
argument_list|,
name|tp
argument_list|)
expr_stmt|;
block|}
return|return
name|tp
return|;
block|}
block|}
end_class

end_unit

