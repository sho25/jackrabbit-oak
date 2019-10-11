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
name|principalbased
operator|.
name|impl
package|;
end_package

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
name|Function
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|Iterators
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
name|Sets
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
name|ReadPolicy
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
name|Before
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
name|PathNotFoundException
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
name|security
operator|.
name|PrivilegedExceptionAction
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
name|Iterator
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
import|import static
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
operator|.
name|ROOT_PATH
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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
operator|.
name|JCR_READ
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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
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

begin_class
specifier|public
class|class
name|ReadablePathsAccessControlTest
extends|extends
name|AbstractPrincipalBasedTest
block|{
specifier|private
name|Principal
name|testPrincipal
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|readablePaths
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|readableChildPaths
decl_stmt|;
specifier|private
name|JackrabbitAccessControlManager
name|acMgr
decl_stmt|;
annotation|@
name|Before
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
name|acMgr
operator|=
operator|new
name|PrincipalBasedAccessControlManager
argument_list|(
name|getMgrProvider
argument_list|(
name|root
argument_list|)
argument_list|,
name|getFilterProvider
argument_list|()
argument_list|)
expr_stmt|;
name|testPrincipal
operator|=
name|getTestSystemUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|getConfig
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|PermissionConstants
operator|.
name|PARAM_READ_PATHS
argument_list|,
name|PermissionConstants
operator|.
name|DEFAULT_READ_PATHS
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|paths
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|readablePaths
operator|=
name|Iterators
operator|.
name|cycle
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|paths
argument_list|,
operator|(
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|f
lambda|->
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrPath
argument_list|(
name|f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|childPaths
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|Iterables
operator|.
name|addAll
argument_list|(
name|childPaths
argument_list|,
name|Iterables
operator|.
name|transform
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
operator|.
name|getChildren
argument_list|()
argument_list|,
name|tree
lambda|->
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrPath
argument_list|(
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|readableChildPaths
operator|=
name|Iterators
operator|.
name|cycle
argument_list|(
name|childPaths
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Subject
name|getTestSubject
parameter_list|()
block|{
return|return
operator|new
name|Subject
argument_list|(
literal|true
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|testPrincipal
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasPrivilege
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ContentSession
name|cs
init|=
name|Subject
operator|.
name|doAsPrivileged
argument_list|(
name|getTestSubject
argument_list|()
argument_list|,
call|(
name|PrivilegedExceptionAction
argument_list|<
name|ContentSession
argument_list|>
call|)
argument_list|()
operator|->
name|getContentRepository
argument_list|()
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
init|)
block|{
name|PrincipalBasedAccessControlManager
name|testAcMgr
init|=
operator|new
name|PrincipalBasedAccessControlManager
argument_list|(
name|getMgrProvider
argument_list|(
name|cs
operator|.
name|getLatestRoot
argument_list|()
argument_list|)
argument_list|,
name|getFilterProvider
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|testPrincipal
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|JCR_READ
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|REP_READ_NODES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|REP_READ_NODES
argument_list|,
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotHasPrivilege
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ContentSession
name|cs
init|=
name|Subject
operator|.
name|doAsPrivileged
argument_list|(
name|getTestSubject
argument_list|()
argument_list|,
call|(
name|PrivilegedExceptionAction
argument_list|<
name|ContentSession
argument_list|>
call|)
argument_list|()
operator|->
name|getContentRepository
argument_list|()
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
init|)
block|{
name|PrincipalBasedAccessControlManager
name|testAcMgr
init|=
operator|new
name|PrincipalBasedAccessControlManager
argument_list|(
name|getMgrProvider
argument_list|(
name|cs
operator|.
name|getLatestRoot
argument_list|()
argument_list|)
argument_list|,
name|getFilterProvider
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|testPrincipal
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|REP_READ_NODES
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_MODIFY_PROPERTIES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|REP_READ_NODES
argument_list|,
name|REP_READ_PROPERTIES
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_NAMESPACE_MANAGEMENT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasPrivilegePrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|testPrincipal
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|acMgr
operator|.
name|hasPrivileges
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|JCR_READ
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acMgr
operator|.
name|hasPrivileges
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acMgr
operator|.
name|hasPrivileges
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|REP_READ_NODES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acMgr
operator|.
name|hasPrivileges
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|REP_READ_NODES
argument_list|,
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotHasPrivilegePrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|testPrincipal
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|acMgr
operator|.
name|hasPrivileges
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_MODIFY_PROPERTIES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acMgr
operator|.
name|hasPrivileges
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acMgr
operator|.
name|hasPrivileges
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|REP_READ_NODES
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acMgr
operator|.
name|hasPrivileges
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_VERSION_MANAGEMENT
argument_list|,
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acMgr
operator|.
name|hasPrivileges
argument_list|(
name|ROOT_PATH
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|JCR_READ
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|systemPath
init|=
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrPath
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|ROOT_PATH
argument_list|,
name|JcrConstants
operator|.
name|JCR_SYSTEM
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|acMgr
operator|.
name|hasPrivileges
argument_list|(
name|systemPath
argument_list|,
name|principals
argument_list|,
name|privilegesFromNames
argument_list|(
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivileges
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ContentSession
name|cs
init|=
name|Subject
operator|.
name|doAsPrivileged
argument_list|(
name|getTestSubject
argument_list|()
argument_list|,
call|(
name|PrivilegedExceptionAction
argument_list|<
name|ContentSession
argument_list|>
call|)
argument_list|()
operator|->
name|getContentRepository
argument_list|()
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
init|)
block|{
name|PrincipalBasedAccessControlManager
name|testAcMgr
init|=
operator|new
name|PrincipalBasedAccessControlManager
argument_list|(
name|getMgrProvider
argument_list|(
name|cs
operator|.
name|getLatestRoot
argument_list|()
argument_list|)
argument_list|,
name|getFilterProvider
argument_list|()
argument_list|)
decl_stmt|;
name|Privilege
index|[]
name|expected
init|=
name|privilegesFromNames
argument_list|(
name|JCR_READ
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|testAcMgr
operator|.
name|getPrivileges
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|testAcMgr
operator|.
name|getPrivileges
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PathNotFoundException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetPrivilegesAtRoot
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ContentSession
name|cs
init|=
name|Subject
operator|.
name|doAsPrivileged
argument_list|(
name|getTestSubject
argument_list|()
argument_list|,
call|(
name|PrivilegedExceptionAction
argument_list|<
name|ContentSession
argument_list|>
call|)
argument_list|()
operator|->
name|getContentRepository
argument_list|()
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
init|)
block|{
name|PrincipalBasedAccessControlManager
name|testAcMgr
init|=
operator|new
name|PrincipalBasedAccessControlManager
argument_list|(
name|getMgrProvider
argument_list|(
name|cs
operator|.
name|getLatestRoot
argument_list|()
argument_list|)
argument_list|,
name|getFilterProvider
argument_list|()
argument_list|)
decl_stmt|;
name|testAcMgr
operator|.
name|getPrivileges
argument_list|(
name|ROOT_PATH
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegesByPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|Privilege
index|[]
name|expected
init|=
name|privilegesFromNames
argument_list|(
name|JCR_READ
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|testPrincipal
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|acMgr
operator|.
name|getPrivileges
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|acMgr
operator|.
name|getPrivileges
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|,
name|principals
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|acMgr
operator|.
name|getPrivileges
argument_list|(
name|ROOT_PATH
argument_list|,
name|principals
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|acMgr
operator|.
name|getPrivileges
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|ROOT_PATH
argument_list|,
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrName
argument_list|(
name|JcrConstants
operator|.
name|JCR_SYSTEM
argument_list|)
argument_list|)
argument_list|,
name|principals
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetEffectivePolicies
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessControlPolicy
index|[]
name|expected
init|=
operator|new
name|AccessControlPolicy
index|[]
block|{
name|ReadPolicy
operator|.
name|INSTANCE
block|}
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|acMgr
operator|.
name|getEffectivePolicies
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|acMgr
operator|.
name|getEffectivePolicies
argument_list|(
name|readableChildPaths
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetEffectivePoliciesNullPath
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|acMgr
operator|.
name|getEffectivePolicies
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AccessDeniedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetEffectivePoliciesLimitedAccess
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ContentSession
name|cs
init|=
name|Subject
operator|.
name|doAsPrivileged
argument_list|(
name|getTestSubject
argument_list|()
argument_list|,
call|(
name|PrivilegedExceptionAction
argument_list|<
name|ContentSession
argument_list|>
call|)
argument_list|()
operator|->
name|getContentRepository
argument_list|()
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
init|)
block|{
name|PrincipalBasedAccessControlManager
name|testAcMgr
init|=
operator|new
name|PrincipalBasedAccessControlManager
argument_list|(
name|getMgrProvider
argument_list|(
name|cs
operator|.
name|getLatestRoot
argument_list|()
argument_list|)
argument_list|,
name|getFilterProvider
argument_list|()
argument_list|)
decl_stmt|;
name|testAcMgr
operator|.
name|getEffectivePolicies
argument_list|(
name|readablePaths
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetEffectivePoliciesLimitedAccess2
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
name|readablePaths
operator|.
name|next
argument_list|()
decl_stmt|;
name|setupPrincipalBasedAccessControl
argument_list|(
name|testPrincipal
argument_list|,
name|path
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
expr_stmt|;
comment|// default: grant read-ac at root node as nodetype/namespace roots cannot have their mixin changed
name|addDefaultEntry
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|,
name|testPrincipal
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// test-session can read-ac at readable path but cannot access principal-based policy
try|try
init|(
name|ContentSession
name|cs
init|=
name|Subject
operator|.
name|doAsPrivileged
argument_list|(
name|getTestSubject
argument_list|()
argument_list|,
call|(
name|PrivilegedExceptionAction
argument_list|<
name|ContentSession
argument_list|>
call|)
argument_list|()
operator|->
name|getContentRepository
argument_list|()
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
init|)
block|{
name|PrincipalBasedAccessControlManager
name|testAcMgr
init|=
operator|new
name|PrincipalBasedAccessControlManager
argument_list|(
name|getMgrProvider
argument_list|(
name|cs
operator|.
name|getLatestRoot
argument_list|()
argument_list|)
argument_list|,
name|getFilterProvider
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|AccessControlPolicy
argument_list|>
name|effective
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|testAcMgr
operator|.
name|getEffectivePolicies
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|effective
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|effective
operator|.
name|contains
argument_list|(
name|ReadPolicy
operator|.
name|INSTANCE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetEffectivePoliciesLimitedAccess3
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
name|readablePaths
operator|.
name|next
argument_list|()
decl_stmt|;
name|setupPrincipalBasedAccessControl
argument_list|(
name|testPrincipal
argument_list|,
name|path
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
expr_stmt|;
name|setupPrincipalBasedAccessControl
argument_list|(
name|testPrincipal
argument_list|,
name|getTestSystemUser
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
expr_stmt|;
comment|// default: grant read and read-ac at root node to make sure both policies are accessible
name|addDefaultEntry
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|,
name|testPrincipal
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// test-session can read-ac at readable path and at principal-based policy
try|try
init|(
name|ContentSession
name|cs
init|=
name|Subject
operator|.
name|doAsPrivileged
argument_list|(
name|getTestSubject
argument_list|()
argument_list|,
call|(
name|PrivilegedExceptionAction
argument_list|<
name|ContentSession
argument_list|>
call|)
argument_list|()
operator|->
name|getContentRepository
argument_list|()
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
init|)
block|{
name|PrincipalBasedAccessControlManager
name|testAcMgr
init|=
operator|new
name|PrincipalBasedAccessControlManager
argument_list|(
name|getMgrProvider
argument_list|(
name|cs
operator|.
name|getLatestRoot
argument_list|()
argument_list|)
argument_list|,
name|getFilterProvider
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|AccessControlPolicy
argument_list|>
name|effective
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|testAcMgr
operator|.
name|getEffectivePolicies
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|effective
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|effective
operator|.
name|remove
argument_list|(
name|ReadPolicy
operator|.
name|INSTANCE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|effective
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|instanceof
name|ImmutablePrincipalPolicy
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetEffectivePoliciesByPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
comment|// NOTE: lookup by principal currently doesn't include READ_POLICY in accordance to default ac implementation
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|acMgr
operator|.
name|getEffectivePolicies
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|testPrincipal
argument_list|)
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

