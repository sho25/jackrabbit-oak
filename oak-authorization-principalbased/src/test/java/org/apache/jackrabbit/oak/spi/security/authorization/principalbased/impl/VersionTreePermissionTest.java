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
name|identifier
operator|.
name|IdentifierManager
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
name|TreeType
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
name|plugins
operator|.
name|version
operator|.
name|ReadOnlyVersionManager
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
name|spi
operator|.
name|version
operator|.
name|VersionConstants
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
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|JCR_BASEVERSION
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
name|JCR_FROZENNODE
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
name|JCR_ISCHECKEDOUT
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
name|JCR_VERSIONHISTORY
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
name|NT_VERSIONEDCHILD
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
name|authorization
operator|.
name|principalbased
operator|.
name|impl
operator|.
name|MockUtility
operator|.
name|mockNodeState
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
name|assertNotEquals
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
name|VersionTreePermissionTest
extends|extends
name|AbstractPrincipalBasedTest
block|{
specifier|private
name|Principal
name|testPrincipal
decl_stmt|;
specifier|private
name|PrincipalBasedPermissionProvider
name|permissionProvider
decl_stmt|;
specifier|private
name|String
name|contentPath
decl_stmt|;
specifier|private
name|String
name|childPath
decl_stmt|;
specifier|private
name|String
name|grandchildPath
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
name|testPrincipal
operator|=
name|getTestSystemUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
expr_stmt|;
name|setupContentTrees
argument_list|(
name|TEST_OAK_PATH
argument_list|)
expr_stmt|;
name|contentPath
operator|=
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|TEST_OAK_PATH
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|childPath
operator|=
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|TEST_OAK_PATH
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|grandchildPath
operator|=
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|TEST_OAK_PATH
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// setup permissions on childPath + TEST_OAK_PATH
name|PrincipalPolicyImpl
name|policy
init|=
name|setupPrincipalBasedAccessControl
argument_list|(
name|testPrincipal
argument_list|,
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrPath
argument_list|(
name|childPath
argument_list|)
argument_list|,
name|JCR_READ
argument_list|)
decl_stmt|;
name|addPrincipalBasedEntry
argument_list|(
name|policy
argument_list|,
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrPath
argument_list|(
name|TEST_OAK_PATH
argument_list|)
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_VERSION_MANAGEMENT
argument_list|)
expr_stmt|;
comment|// versionabel nodes: contentPath + grandChildPath + TEST_OAK_PATH
comment|// -> TEST_OAK_PATH versionable node holds policy, grandchildPath get permissions inherited, and contentPath has no permissions granted
name|Tree
name|typeRoot
init|=
name|root
operator|.
name|getTree
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
operator|new
name|String
index|[]
block|{
name|contentPath
block|,
name|grandchildPath
block|,
name|TEST_OAK_PATH
block|}
control|)
block|{
name|Tree
name|versionable
init|=
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|TreeUtil
operator|.
name|addMixin
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
argument_list|,
name|NodeTypeConstants
operator|.
name|MIX_VERSIONABLE
argument_list|,
name|typeRoot
argument_list|,
literal|"uid"
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// force creation of a new versions (except for TEST_OAK_PATH)
for|for
control|(
name|String
name|path
range|:
operator|new
name|String
index|[]
block|{
name|contentPath
block|,
name|grandchildPath
block|}
control|)
block|{
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_ISCHECKEDOUT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_ISCHECKEDOUT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|permissionProvider
operator|=
name|createPermissionProvider
argument_list|(
name|root
argument_list|,
name|testPrincipal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|NamePathMapper
name|getNamePathMapper
parameter_list|()
block|{
return|return
name|NamePathMapper
operator|.
name|DEFAULT
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|String
name|getPathFromReference
parameter_list|(
annotation|@
name|NotNull
name|String
name|treePath
parameter_list|,
annotation|@
name|NotNull
name|String
name|refProperty
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|treePath
argument_list|)
decl_stmt|;
name|String
name|path
init|=
operator|new
name|IdentifierManager
argument_list|(
name|root
argument_list|)
operator|.
name|getPath
argument_list|(
name|tree
operator|.
name|getProperty
argument_list|(
name|refProperty
argument_list|)
argument_list|)
decl_stmt|;
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|AbstractTreePermission
name|getTreePermission
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
decl_stmt|;
name|TreePermission
name|tp
init|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|elem
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|tree
operator|=
name|tree
operator|.
name|getChild
argument_list|(
name|elem
argument_list|)
expr_stmt|;
name|tp
operator|=
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|tp
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|tp
operator|instanceof
name|AbstractTreePermission
argument_list|)
expr_stmt|;
return|return
operator|(
name|AbstractTreePermission
operator|)
name|tp
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTreePermissionVersionHistory
parameter_list|()
block|{
name|String
name|vhPath
init|=
name|getPathFromReference
argument_list|(
name|TEST_OAK_PATH
argument_list|,
name|JCR_VERSIONHISTORY
argument_list|)
decl_stmt|;
name|AbstractTreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|vhPath
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|TreeType
operator|.
name|VERSION
argument_list|,
name|tp
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|vhPath
argument_list|,
name|tp
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|permissionProvider
argument_list|,
name|tp
operator|.
name|getPermissionProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTreePermissionVersion
parameter_list|()
block|{
name|String
name|versionPath
init|=
name|getPathFromReference
argument_list|(
name|TEST_OAK_PATH
argument_list|,
name|JCR_BASEVERSION
argument_list|)
decl_stmt|;
name|AbstractTreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|versionPath
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|TreeType
operator|.
name|VERSION
argument_list|,
name|tp
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|versionPath
argument_list|,
name|tp
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|permissionProvider
argument_list|,
name|tp
operator|.
name|getPermissionProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetChildPermissionRootVersionNode
parameter_list|()
block|{
name|String
name|vhPath
init|=
name|getPathFromReference
argument_list|(
name|contentPath
argument_list|,
name|JCR_VERSIONHISTORY
argument_list|)
decl_stmt|;
name|AbstractTreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|vhPath
argument_list|)
decl_stmt|;
name|TreePermission
name|rootversionTp
init|=
name|tp
operator|.
name|getChildPermission
argument_list|(
name|VersionConstants
operator|.
name|JCR_ROOTVERSION
argument_list|,
name|mockNodeState
argument_list|(
name|VersionConstants
operator|.
name|NT_VERSION
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rootversionTp
operator|instanceof
name|AbstractTreePermission
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|tp
operator|.
name|getTree
argument_list|()
argument_list|,
operator|(
operator|(
name|AbstractTreePermission
operator|)
name|rootversionTp
operator|)
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetChildPermissionVersion1Node
parameter_list|()
block|{
name|String
name|vhPath
init|=
name|getPathFromReference
argument_list|(
name|TEST_OAK_PATH
argument_list|,
name|JCR_VERSIONHISTORY
argument_list|)
decl_stmt|;
name|AbstractTreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|vhPath
argument_list|)
decl_stmt|;
name|TreePermission
name|versionTp
init|=
name|tp
operator|.
name|getChildPermission
argument_list|(
literal|"1.0"
argument_list|,
name|mockNodeState
argument_list|(
name|VersionConstants
operator|.
name|NT_VERSION
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|versionTp
operator|instanceof
name|AbstractTreePermission
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|tp
operator|.
name|getTree
argument_list|()
argument_list|,
operator|(
operator|(
name|AbstractTreePermission
operator|)
name|versionTp
operator|)
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetChildPermissionLabelsNode
parameter_list|()
block|{
name|String
name|vhPath
init|=
name|getPathFromReference
argument_list|(
name|contentPath
argument_list|,
name|JCR_VERSIONHISTORY
argument_list|)
decl_stmt|;
name|AbstractTreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|vhPath
argument_list|)
decl_stmt|;
name|TreePermission
name|labelsTp
init|=
name|tp
operator|.
name|getChildPermission
argument_list|(
name|VersionConstants
operator|.
name|JCR_VERSIONLABELS
argument_list|,
name|mockNodeState
argument_list|(
name|VersionConstants
operator|.
name|NT_VERSIONLABELS
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|labelsTp
operator|instanceof
name|AbstractTreePermission
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|tp
operator|.
name|getTree
argument_list|()
argument_list|,
operator|(
operator|(
name|AbstractTreePermission
operator|)
name|labelsTp
operator|)
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetChildPermissionFrozenNode
parameter_list|()
block|{
name|String
name|v1Path
init|=
name|getPathFromReference
argument_list|(
name|grandchildPath
argument_list|,
name|JCR_BASEVERSION
argument_list|)
decl_stmt|;
name|Tree
name|v1Tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|v1Path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|v1Tree
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractTreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|v1Path
argument_list|)
decl_stmt|;
name|TreePermission
name|frozenTp
init|=
name|tp
operator|.
name|getChildPermission
argument_list|(
name|VersionConstants
operator|.
name|JCR_FROZENNODE
argument_list|,
name|getTreeProvider
argument_list|()
operator|.
name|asNodeState
argument_list|(
name|v1Tree
operator|.
name|getChild
argument_list|(
name|JCR_FROZENNODE
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|tp
operator|.
name|getTree
argument_list|()
argument_list|,
operator|(
operator|(
name|AbstractTreePermission
operator|)
name|frozenTp
operator|)
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVersionedChildNode
parameter_list|()
block|{
name|String
name|path
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getPathFromReference
argument_list|(
name|contentPath
argument_list|,
name|JCR_BASEVERSION
argument_list|)
argument_list|,
name|VersionConstants
operator|.
name|JCR_FROZENNODE
argument_list|,
name|PathUtils
operator|.
name|getName
argument_list|(
name|childPath
argument_list|)
argument_list|)
decl_stmt|;
name|AbstractTreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|versionedChildName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|grandchildPath
argument_list|)
decl_stmt|;
name|String
name|versionedChildPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|versionedChildName
argument_list|)
decl_stmt|;
name|Tree
name|versionedChildTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|versionedChildPath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NT_VERSIONEDCHILD
argument_list|,
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|versionedChildTree
argument_list|)
argument_list|)
expr_stmt|;
name|TreePermission
name|versionedChildTp
init|=
name|tp
operator|.
name|getChildPermission
argument_list|(
name|versionedChildName
argument_list|,
name|getTreeProvider
argument_list|()
operator|.
name|asNodeState
argument_list|(
name|versionedChildTree
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|versionedChildTp
operator|instanceof
name|AbstractTreePermission
argument_list|)
expr_stmt|;
name|Tree
name|childVersionHistory
init|=
name|root
operator|.
name|getTree
argument_list|(
name|getPathFromReference
argument_list|(
name|versionedChildPath
argument_list|,
name|VersionConstants
operator|.
name|JCR_CHILD_VERSION_HISTORY
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|childVersionHistory
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Tree
name|versionable
init|=
name|ReadOnlyVersionManager
operator|.
name|getInstance
argument_list|(
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
operator|.
name|getVersionable
argument_list|(
name|childVersionHistory
argument_list|,
name|root
operator|.
name|getContentSession
argument_list|()
operator|.
name|getWorkspaceName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|versionable
operator|.
name|getPath
argument_list|()
argument_list|,
operator|(
operator|(
name|AbstractTreePermission
operator|)
name|versionedChildTp
operator|)
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVersionedChildNodePointingToRemovedVH
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|root
operator|.
name|getTree
argument_list|(
name|TEST_OAK_PATH
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
name|permissionProvider
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|String
name|path
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getPathFromReference
argument_list|(
name|grandchildPath
argument_list|,
name|JCR_BASEVERSION
argument_list|)
argument_list|,
name|JCR_FROZENNODE
argument_list|)
decl_stmt|;
name|AbstractTreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|versionedChildName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|TEST_OAK_PATH
argument_list|)
decl_stmt|;
name|String
name|versionedChildPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|versionedChildName
argument_list|)
decl_stmt|;
name|Tree
name|versionedChildTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|versionedChildPath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NT_VERSIONEDCHILD
argument_list|,
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|versionedChildTree
argument_list|)
argument_list|)
expr_stmt|;
name|TreePermission
name|versionedChildTp
init|=
name|tp
operator|.
name|getChildPermission
argument_list|(
name|versionedChildName
argument_list|,
name|getTreeProvider
argument_list|()
operator|.
name|asNodeState
argument_list|(
name|versionedChildTree
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|versionedChildTp
operator|instanceof
name|AbstractTreePermission
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TEST_OAK_PATH
argument_list|,
operator|(
operator|(
name|AbstractTreePermission
operator|)
name|versionedChildTp
operator|)
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|versionedChildTp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
comment|// version-mgt permission still granted because it is not stored on the removed node
name|assertTrue
argument_list|(
name|versionedChildTp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|VERSION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetChildPermissionCopiedChild
parameter_list|()
block|{
name|String
name|frozenPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getPathFromReference
argument_list|(
name|contentPath
argument_list|,
name|JCR_BASEVERSION
argument_list|)
argument_list|,
name|JCR_FROZENNODE
argument_list|)
decl_stmt|;
name|AbstractTreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|frozenPath
argument_list|)
decl_stmt|;
name|String
name|copiedChildName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|childPath
argument_list|)
decl_stmt|;
name|Tree
name|copiedChildTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|frozenPath
argument_list|,
name|copiedChildName
argument_list|)
argument_list|)
decl_stmt|;
name|TreePermission
name|copiedChildTp
init|=
name|tp
operator|.
name|getChildPermission
argument_list|(
name|copiedChildName
argument_list|,
name|getTreeProvider
argument_list|()
operator|.
name|asNodeState
argument_list|(
name|copiedChildTree
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|copiedChildTp
operator|instanceof
name|AbstractTreePermission
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|childPath
argument_list|,
operator|(
operator|(
name|AbstractTreePermission
operator|)
name|copiedChildTp
operator|)
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGrantedVersionHistoryNode
parameter_list|()
block|{
name|String
name|vhPath
init|=
name|getPathFromReference
argument_list|(
name|contentPath
argument_list|,
name|JCR_VERSIONHISTORY
argument_list|)
decl_stmt|;
name|AbstractTreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|vhPath
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|VERSION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|vhPath
operator|=
name|getPathFromReference
argument_list|(
name|grandchildPath
argument_list|,
name|JCR_VERSIONHISTORY
argument_list|)
expr_stmt|;
name|tp
operator|=
name|getTreePermission
argument_list|(
name|vhPath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|VERSION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|vhPath
operator|=
name|getPathFromReference
argument_list|(
name|TEST_OAK_PATH
argument_list|,
name|JCR_VERSIONHISTORY
argument_list|)
expr_stmt|;
name|tp
operator|=
name|getTreePermission
argument_list|(
name|vhPath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|VERSION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGrantedVersionNode
parameter_list|()
block|{
name|String
name|vPath
init|=
name|getPathFromReference
argument_list|(
name|contentPath
argument_list|,
name|JCR_BASEVERSION
argument_list|)
decl_stmt|;
name|AbstractTreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|vPath
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|VERSION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|vPath
operator|=
name|getPathFromReference
argument_list|(
name|grandchildPath
argument_list|,
name|JCR_BASEVERSION
argument_list|)
expr_stmt|;
name|tp
operator|=
name|getTreePermission
argument_list|(
name|vPath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|VERSION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|vPath
operator|=
name|getPathFromReference
argument_list|(
name|TEST_OAK_PATH
argument_list|,
name|JCR_BASEVERSION
argument_list|)
expr_stmt|;
name|tp
operator|=
name|getTreePermission
argument_list|(
name|vPath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|VERSION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGrantedFrozenNode
parameter_list|()
block|{
name|String
name|frozenPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getPathFromReference
argument_list|(
name|contentPath
argument_list|,
name|JCR_BASEVERSION
argument_list|)
argument_list|,
name|JCR_FROZENNODE
argument_list|)
decl_stmt|;
name|AbstractTreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|frozenPath
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|VERSION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|frozenPath
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getPathFromReference
argument_list|(
name|grandchildPath
argument_list|,
name|JCR_BASEVERSION
argument_list|)
argument_list|,
name|JCR_FROZENNODE
argument_list|)
expr_stmt|;
name|tp
operator|=
name|getTreePermission
argument_list|(
name|frozenPath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|VERSION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|frozenPath
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getPathFromReference
argument_list|(
name|TEST_OAK_PATH
argument_list|,
name|JCR_BASEVERSION
argument_list|)
argument_list|,
name|JCR_FROZENNODE
argument_list|)
expr_stmt|;
name|tp
operator|=
name|getTreePermission
argument_list|(
name|frozenPath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|VERSION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGrantedCopiedChild
parameter_list|()
block|{
name|String
name|copiedChildPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getPathFromReference
argument_list|(
name|contentPath
argument_list|,
name|JCR_BASEVERSION
argument_list|)
argument_list|,
name|JCR_FROZENNODE
argument_list|,
name|PathUtils
operator|.
name|getName
argument_list|(
name|childPath
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|copiedChildPath
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractTreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|copiedChildPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|VERSION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsGrantedVersionedChild
parameter_list|()
block|{
name|String
name|versionedChildPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getPathFromReference
argument_list|(
name|grandchildPath
argument_list|,
name|JCR_BASEVERSION
argument_list|)
argument_list|,
name|JCR_FROZENNODE
argument_list|,
name|PathUtils
operator|.
name|getName
argument_list|(
name|TEST_OAK_PATH
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|versionedChildPath
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractTreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|versionedChildPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|.
name|isGranted
argument_list|(
name|Permissions
operator|.
name|VERSION_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

