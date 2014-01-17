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
name|permission
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Set
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
name|core
operator|.
name|ImmutableRoot
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
name|core
operator|.
name|TreeTypeProvider
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
name|NamespaceConstants
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
name|authorization
operator|.
name|permission
operator|.
name|PermissionConstants
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
name|Permissions
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
name|RepositoryPermission
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
name|assertSame
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
name|PermissionProviderImplTest
extends|extends
name|AbstractSecurityTest
implements|implements
name|AccessControlConstants
block|{
specifier|private
specifier|static
specifier|final
name|String
name|ADMINISTRATOR_GROUP
init|=
literal|"administrators"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|READ_PATHS
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|NamespaceConstants
operator|.
name|NAMESPACES_PATH
argument_list|,
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
argument_list|,
name|PrivilegeConstants
operator|.
name|PRIVILEGES_PATH
argument_list|,
literal|"/test"
argument_list|)
decl_stmt|;
specifier|private
name|Group
name|adminstrators
decl_stmt|;
specifier|private
name|AuthorizationConfiguration
name|config
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
literal|"test"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
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
name|adminstrators
operator|=
name|uMgr
operator|.
name|createGroup
argument_list|(
name|ADMINISTRATOR_GROUP
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|config
operator|=
name|getSecurityProvider
argument_list|()
operator|.
name|getConfiguration
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
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
name|root
operator|.
name|getTree
argument_list|(
literal|"/test"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|UserManager
name|uMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
if|if
condition|(
name|adminstrators
operator|!=
literal|null
condition|)
block|{
name|uMgr
operator|.
name|getAuthorizable
argument_list|(
name|adminstrators
operator|.
name|getID
argument_list|()
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
condition|)
block|{
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
name|Override
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
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
name|map
operator|.
name|put
argument_list|(
name|PermissionConstants
operator|.
name|PARAM_READ_PATHS
argument_list|,
name|READ_PATHS
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PermissionConstants
operator|.
name|PARAM_ADMINISTRATIVE_PRINCIPALS
argument_list|,
operator|new
name|String
index|[]
block|{
name|ADMINISTRATOR_GROUP
block|}
argument_list|)
expr_stmt|;
name|ConfigurationParameters
name|acConfig
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|map
argument_list|)
decl_stmt|;
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|AuthorizationConfiguration
operator|.
name|NAME
argument_list|,
name|acConfig
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|PermissionProvider
name|createPermissionProvider
parameter_list|(
name|ContentSession
name|session
parameter_list|)
block|{
return|return
name|config
operator|.
name|getPermissionProvider
argument_list|(
name|session
operator|.
name|getLatestRoot
argument_list|()
argument_list|,
name|session
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|session
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasPrivileges
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
try|try
block|{
name|PermissionProvider
name|pp
init|=
name|createPermissionProvider
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|hasPrivileges
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|hasPrivileges
argument_list|(
literal|null
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|hasPrivileges
argument_list|(
literal|null
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_WORKSPACE_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
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
specifier|public
name|void
name|testReadPath
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
try|try
block|{
name|Root
name|r
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|PermissionProvider
name|pp
init|=
name|createPermissionProvider
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|Tree
name|tree
init|=
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tree
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|READ_PATHS
control|)
block|{
name|tree
operator|=
name|r
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
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
specifier|public
name|void
name|testIsGrantedForReadPaths
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
try|try
block|{
name|PermissionProvider
name|pp
init|=
name|createPermissionProvider
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|READ_PATHS
control|)
block|{
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|path
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|path
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|READ_NODE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|path
operator|+
literal|'/'
operator|+
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|path
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|path
range|:
name|READ_PATHS
control|)
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
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ_NODE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
name|tree
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|,
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|RepositoryPermission
name|rp
init|=
name|pp
operator|.
name|getRepositoryPermission
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|rp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|READ_NODE
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
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
specifier|public
name|void
name|testGetPrivilegesForReadPaths
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
try|try
block|{
name|PermissionProvider
name|pp
init|=
name|createPermissionProvider
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|READ_PATHS
control|)
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
name|assertEquals
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|,
name|pp
operator|.
name|getPrivileges
argument_list|(
name|tree
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|,
name|pp
operator|.
name|getPrivileges
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
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
specifier|public
name|void
name|testHasPrivilegesForReadPaths
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
try|try
block|{
name|PermissionProvider
name|pp
init|=
name|createPermissionProvider
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|READ_PATHS
control|)
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
name|assertTrue
argument_list|(
name|pp
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|hasPrivileges
argument_list|(
name|tree
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|pp
operator|.
name|hasPrivileges
argument_list|(
literal|null
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
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
specifier|public
name|void
name|testAdministatorConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|adminstrators
operator|.
name|addMember
argument_list|(
name|getTestUser
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
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
name|r
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Root
name|immutableRoot
init|=
operator|new
name|ImmutableRoot
argument_list|(
name|r
argument_list|,
name|TreeTypeProvider
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|PermissionProvider
name|pp
init|=
name|createPermissionProvider
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|TreePermission
name|tp
init|=
name|pp
operator|.
name|getTreePermission
argument_list|(
name|immutableRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|TreePermission
operator|.
name|ALL
argument_list|,
name|tp
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|READ_PATHS
control|)
block|{
name|Tree
name|tree
init|=
name|r
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|TreePermission
operator|.
name|ALL
argument_list|,
name|pp
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
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

