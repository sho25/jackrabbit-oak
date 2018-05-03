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
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Privilege
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
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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
name|restriction
operator|.
name|RestrictionProvider
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|mockito
operator|.
name|Matchers
operator|.
name|anyLong
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|MountPermissionStoreTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TEST_NAME
init|=
literal|"MultiplexingProviderTest"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_PATH
init|=
literal|"/"
operator|+
name|TEST_NAME
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONTENT_NAME
init|=
literal|"content"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONTENT_PATH
init|=
name|TEST_PATH
operator|+
literal|"/"
operator|+
name|CONTENT_NAME
decl_stmt|;
specifier|private
name|MountInfoProvider
name|mountInfoProvider
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"testMount"
argument_list|,
name|TEST_PATH
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
name|AuthorizationConfiguration
name|config
decl_stmt|;
specifier|private
name|PermissionStore
name|permissionStore
decl_stmt|;
annotation|@
name|Override
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
name|Tree
name|test
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|rootNode
argument_list|,
name|TEST_NAME
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Tree
name|content
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|test
argument_list|,
name|CONTENT_NAME
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Tree
name|child
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|content
argument_list|,
literal|"child"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|Privilege
index|[]
name|privileges
init|=
name|AccessControlUtils
operator|.
name|privilegesFromNames
argument_list|(
name|acMgr
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
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
name|content
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|acl
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|privileges
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|content
operator|.
name|getPath
argument_list|()
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|AccessControlList
name|acl2
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|child
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|acl2
argument_list|)
expr_stmt|;
name|acl2
operator|.
name|addAccessControlEntry
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|privileges
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|child
operator|.
name|getPath
argument_list|()
argument_list|,
name|acl2
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|wspName
init|=
name|adminSession
operator|.
name|getWorkspaceName
argument_list|()
decl_stmt|;
name|PermissionProvider
name|pp
init|=
name|config
operator|.
name|getPermissionProvider
argument_list|(
name|root
argument_list|,
name|wspName
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|instanceof
name|MountPermissionProvider
argument_list|)
expr_stmt|;
name|permissionStore
operator|=
operator|(
operator|(
name|MountPermissionProvider
operator|)
name|pp
operator|)
operator|.
name|getPermissionStore
argument_list|(
name|root
argument_list|,
name|wspName
argument_list|,
name|RestrictionProvider
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
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
name|refresh
argument_list|()
expr_stmt|;
name|Tree
name|test
init|=
name|root
operator|.
name|getTree
argument_list|(
name|TEST_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|exists
argument_list|()
condition|)
block|{
name|test
operator|.
name|remove
argument_list|()
expr_stmt|;
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
name|initSecurityProvider
parameter_list|()
block|{
name|SecurityProvider
name|sp
init|=
name|super
operator|.
name|initSecurityProvider
argument_list|()
decl_stmt|;
name|config
operator|=
name|MountUtils
operator|.
name|bindMountInfoProvider
argument_list|(
name|sp
argument_list|,
name|mountInfoProvider
argument_list|)
expr_stmt|;
return|return
name|sp
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoadByAccessControlledPath
parameter_list|()
block|{
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|entries
init|=
name|permissionStore
operator|.
name|load
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|CONTENT_PATH
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|entries
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|entries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoadByNonAccessControlledPath
parameter_list|()
block|{
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|entries
init|=
name|permissionStore
operator|.
name|load
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|TEST_PATH
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|entries
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoadByPrincipalNameWithEntries
parameter_list|()
block|{
name|PrincipalPermissionEntries
name|ppe
init|=
name|permissionStore
operator|.
name|load
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ppe
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ppe
operator|.
name|isFullyLoaded
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|ppe
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoadByUnknownPrincipalName
parameter_list|()
block|{
name|PrincipalPermissionEntries
name|ppe
init|=
name|permissionStore
operator|.
name|load
argument_list|(
literal|"unknown"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ppe
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ppe
operator|.
name|isFullyLoaded
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ppe
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetNumEntries
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|permissionStore
operator|.
name|getNumEntries
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
literal|10
argument_list|)
operator|.
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetNumEntriesMaxReachedExact
parameter_list|()
throws|throws
name|Exception
block|{
name|PermissionStoreImpl
name|mock
init|=
name|insertMockStore
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|mock
operator|.
name|getNumEntries
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|NumEntries
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|NumEntries
name|ne
init|=
name|permissionStore
operator|.
name|getNumEntries
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NumEntries
operator|.
name|valueOf
argument_list|(
literal|4
argument_list|,
literal|true
argument_list|)
argument_list|,
name|ne
argument_list|)
expr_stmt|;
name|ne
operator|=
name|permissionStore
operator|.
name|getNumEntries
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NumEntries
operator|.
name|valueOf
argument_list|(
literal|4
argument_list|,
literal|true
argument_list|)
argument_list|,
name|ne
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetNumEntriesMaxReachedNotExact
parameter_list|()
throws|throws
name|Exception
block|{
name|PermissionStoreImpl
name|mock
init|=
name|insertMockStore
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|mock
operator|.
name|getNumEntries
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|NumEntries
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|NumEntries
name|ne
init|=
name|permissionStore
operator|.
name|getNumEntries
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NumEntries
operator|.
name|valueOf
argument_list|(
literal|4
argument_list|,
literal|false
argument_list|)
argument_list|,
name|ne
argument_list|)
expr_stmt|;
name|ne
operator|=
name|permissionStore
operator|.
name|getNumEntries
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NumEntries
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|,
literal|false
argument_list|)
argument_list|,
name|ne
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetNumEntriesUnknownPrincipalName
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|permissionStore
operator|.
name|getNumEntries
argument_list|(
literal|"unknown"
argument_list|,
literal|10
argument_list|)
operator|.
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFlush
parameter_list|()
throws|throws
name|Exception
block|{
name|PermissionStoreImpl
name|mock
init|=
name|insertMockStore
argument_list|()
decl_stmt|;
name|permissionStore
operator|.
name|flush
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mock
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|flush
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
specifier|private
name|PermissionStoreImpl
name|insertMockStore
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|f
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.jackrabbit.oak.security.authorization.permission.MountPermissionProvider$MountPermissionStore"
argument_list|)
operator|.
name|getDeclaredField
argument_list|(
literal|"stores"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PermissionStoreImpl
name|mock
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|PermissionStoreImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|PermissionStoreImpl
argument_list|>
name|stores
init|=
operator|(
name|List
argument_list|<
name|PermissionStoreImpl
argument_list|>
operator|)
name|f
operator|.
name|get
argument_list|(
name|permissionStore
argument_list|)
decl_stmt|;
name|stores
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|mock
argument_list|)
expr_stmt|;
return|return
name|mock
return|;
block|}
block|}
end_class

end_unit

