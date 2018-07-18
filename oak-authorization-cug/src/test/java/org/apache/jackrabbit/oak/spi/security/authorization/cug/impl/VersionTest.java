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
name|List
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
name|PropertyState
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
name|version
operator|.
name|VersionConstants
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

begin_comment
comment|/**  * Test read access to version related information both in the regular  * content and in the version storage.  */
end_comment

begin_class
specifier|public
class|class
name|VersionTest
extends|extends
name|AbstractCugTest
implements|implements
name|NodeTypeConstants
implements|,
name|VersionConstants
block|{
specifier|private
name|ContentSession
name|testSession
decl_stmt|;
specifier|private
name|Root
name|testRoot
decl_stmt|;
specifier|private
name|ReadOnlyVersionManager
name|versionManager
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|readAccess
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|noReadAccess
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
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
comment|// create cugs
comment|// - /content/a     : allow testGroup, deny everyone
comment|// - /content/aa/bb : allow testGroup, deny everyone
comment|// - /content/a/b/c : allow everyone,  deny testGroup (isolated)
comment|// - /content2      : allow everyone,  deny testGroup (isolated)
name|setupCugsAndAcls
argument_list|()
expr_stmt|;
name|readAccess
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|SUPPORTED_PATH
argument_list|,
literal|"/content/subtree"
argument_list|,
literal|"/content/aa"
argument_list|)
expr_stmt|;
name|noReadAccess
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|UNSUPPORTED_PATH
argument_list|,
comment|/* no access */
literal|"/content2"
argument_list|,
comment|/* granted by cug only */
literal|"/content/a"
argument_list|,
comment|/* granted by ace, denied by cug */
literal|"/content/aa/bb"
comment|/* granted by ace, denied by cug */
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|Iterables
operator|.
name|concat
argument_list|(
name|readAccess
argument_list|,
name|noReadAccess
argument_list|)
control|)
block|{
name|addVersionContent
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
name|testSession
operator|=
name|createTestSession
argument_list|()
expr_stmt|;
name|testRoot
operator|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|versionManager
operator|=
name|ReadOnlyVersionManager
operator|.
name|getInstance
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
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
if|if
condition|(
name|testSession
operator|!=
literal|null
condition|)
block|{
name|testSession
operator|.
name|close
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
specifier|private
name|Tree
name|addVersionContent
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Tree
name|typesRoot
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
name|TreeUtil
operator|.
name|addMixin
argument_list|(
name|t
argument_list|,
name|JcrConstants
operator|.
name|MIX_VERSIONABLE
argument_list|,
name|typesRoot
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// force the creation of a version with frozen node
name|t
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
name|t
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
if|if
condition|(
name|testRoot
operator|!=
literal|null
condition|)
block|{
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
return|return
name|t
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadVersionContent
parameter_list|()
throws|throws
name|Exception
block|{
name|IdentifierManager
name|idMgr
init|=
operator|new
name|IdentifierManager
argument_list|(
name|testRoot
argument_list|)
decl_stmt|;
name|ReadOnlyVersionManager
name|vMgr
init|=
name|ReadOnlyVersionManager
operator|.
name|getInstance
argument_list|(
name|testRoot
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|readAccess
control|)
block|{
name|Tree
name|t
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|path
argument_list|,
name|t
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|PropertyState
name|ps
init|=
name|t
operator|.
name|getProperty
argument_list|(
name|JCR_VERSIONHISTORY
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|String
name|vhUUID
init|=
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|vhUUID
argument_list|,
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|versionHistory
init|=
name|vMgr
operator|.
name|getVersionHistory
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|versionHistory
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|versionHistory
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|versionHistory
operator|.
name|getChild
argument_list|(
name|JCR_ROOTVERSION
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|versionHistory
operator|.
name|getParent
argument_list|()
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Tree
name|vhTree
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
name|versionHistory
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vhTree
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|vhPath
init|=
name|idMgr
operator|.
name|resolveUUID
argument_list|(
name|vhUUID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|vhPath
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|versionHistory
operator|.
name|getPath
argument_list|()
argument_list|,
name|vhPath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
name|vhPath
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
name|vhPath
operator|+
literal|'/'
operator|+
name|JCR_ROOTVERSION
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadVersionContentNoAccess
parameter_list|()
throws|throws
name|Exception
block|{
name|IdentifierManager
name|idMgr
init|=
operator|new
name|IdentifierManager
argument_list|(
name|testRoot
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|noReadAccess
control|)
block|{
name|String
name|vhUUID
init|=
name|checkNotNull
argument_list|(
name|TreeUtil
operator|.
name|getString
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
argument_list|,
name|JCR_VERSIONHISTORY
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|vhPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|VERSION_STORE_PATH
argument_list|,
name|versionManager
operator|.
name|getVersionHistoryPath
argument_list|(
name|vhUUID
argument_list|)
argument_list|)
decl_stmt|;
name|Tree
name|vHistory
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
name|vhPath
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|vHistory
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vHistory
operator|.
name|getParent
argument_list|()
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vHistory
operator|.
name|getChild
argument_list|(
name|JCR_ROOTVERSION
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
name|vhPath
operator|+
literal|'/'
operator|+
name|JCR_ROOTVERSION
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|vh
init|=
name|idMgr
operator|.
name|resolveUUID
argument_list|(
name|vhUUID
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|path
argument_list|,
name|vh
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadVersionStorage
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
name|VersionConstants
operator|.
name|VERSION_STORE_PATH
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSupportedPermissions
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|versionable
init|=
name|addVersionContent
argument_list|(
literal|"/content/a/b/c"
argument_list|)
decl_stmt|;
name|CugPermissionProvider
name|pp
init|=
name|createCugPermissionProvider
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|SUPPORTED_PATH2
argument_list|)
argument_list|)
decl_stmt|;
name|Tree
name|versionStorage
init|=
name|root
operator|.
name|getTree
argument_list|(
name|VersionConstants
operator|.
name|VERSION_STORE_PATH
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Permissions
operator|.
name|NO_PERMISSION
argument_list|,
name|pp
operator|.
name|supportedPermissions
argument_list|(
name|versionStorage
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Permissions
operator|.
name|NO_PERMISSION
argument_list|,
name|pp
operator|.
name|supportedPermissions
argument_list|(
name|versionStorage
operator|.
name|getParent
argument_list|()
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
comment|// tree with cug (access is granted)
name|Tree
name|vh
init|=
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|versionable
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Permissions
operator|.
name|READ
argument_list|,
name|pp
operator|.
name|supportedPermissions
argument_list|(
name|vh
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
comment|// tree with cug (but no access granted)
name|vh
operator|=
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/content2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Permissions
operator|.
name|READ
argument_list|,
name|pp
operator|.
name|supportedPermissions
argument_list|(
name|vh
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
comment|// tree without cug
name|vh
operator|=
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|UNSUPPORTED_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Permissions
operator|.
name|NO_PERMISSION
argument_list|,
name|pp
operator|.
name|supportedPermissions
argument_list|(
name|vh
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
comment|// tree without cug
name|vh
operator|=
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Permissions
operator|.
name|NO_PERMISSION
argument_list|,
name|pp
operator|.
name|supportedPermissions
argument_list|(
name|vh
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVersionableRemoved
parameter_list|()
throws|throws
name|Exception
block|{
comment|// cug at /content/a/b/c grants access
name|Tree
name|versionable
init|=
name|addVersionContent
argument_list|(
literal|"/content/a/b/c"
argument_list|)
decl_stmt|;
name|Tree
name|vh
init|=
name|checkNotNull
argument_list|(
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|versionable
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
name|vh
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|versionable
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// the cug-permission provider still supports the path as there exists
comment|// a cug higher up in the hierarchy
comment|// -> the parent cug takes effect now
name|CugPermissionProvider
name|pp
init|=
name|createCugPermissionProvider
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|SUPPORTED_PATH2
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Permissions
operator|.
name|READ
argument_list|,
name|pp
operator|.
name|supportedPermissions
argument_list|(
name|vh
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|vh
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
comment|// the vh associated with /content/a/b/c is no longer accessible
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
name|vh
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVersionableRemoved2
parameter_list|()
throws|throws
name|Exception
block|{
comment|// cug at /content/a/b/c denies access
name|Tree
name|versionable
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/content/a"
argument_list|)
decl_stmt|;
name|Tree
name|vh
init|=
name|checkNotNull
argument_list|(
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|versionable
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
name|vh
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|versionable
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// removing this versionable node removes the CUG in this tree
comment|// -> the permission provider is no longer responsible
name|CugPermissionProvider
name|pp
init|=
name|createCugPermissionProvider
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|SUPPORTED_PATH2
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Permissions
operator|.
name|NO_PERMISSION
argument_list|,
name|pp
operator|.
name|supportedPermissions
argument_list|(
name|vh
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|vh
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
comment|// subsequently the deny of the former CUG is gone as well
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
name|vh
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTreePermissionVersionable
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|versionable
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/content/a"
argument_list|)
decl_stmt|;
name|Tree
name|vh
init|=
name|checkNotNull
argument_list|(
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|versionable
argument_list|)
argument_list|)
decl_stmt|;
name|CugPermissionProvider
name|pp
init|=
name|createCugPermissionProvider
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|SUPPORTED_PATH2
argument_list|)
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
decl_stmt|;
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
name|String
name|path
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|vh
operator|.
name|getPath
argument_list|()
argument_list|,
literal|"1.0"
argument_list|,
name|JCR_FROZENNODE
argument_list|,
literal|"b/c"
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
if|if
condition|(
name|JCR_SYSTEM
operator|.
name|equals
argument_list|(
name|segm
argument_list|)
operator|||
name|ReadOnlyVersionManager
operator|.
name|isVersionStoreTree
argument_list|(
name|t
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|,
name|tp
operator|instanceof
name|EmptyCugTreePermission
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|,
name|tp
operator|instanceof
name|CugTreePermission
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|,
literal|"c"
operator|.
name|equals
argument_list|(
name|segm
argument_list|)
argument_list|,
name|tp
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTreePermissionVersionable2
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|versionable
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/content"
argument_list|)
decl_stmt|;
name|Tree
name|vh
init|=
name|checkNotNull
argument_list|(
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|versionable
argument_list|)
argument_list|)
decl_stmt|;
name|CugPermissionProvider
name|pp
init|=
name|createCugPermissionProvider
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|SUPPORTED_PATH2
argument_list|)
argument_list|)
decl_stmt|;
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
name|String
name|path
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|vh
operator|.
name|getPath
argument_list|()
argument_list|,
literal|"1.0"
argument_list|,
name|JCR_FROZENNODE
argument_list|,
literal|"aa"
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
if|if
condition|(
name|JCR_SYSTEM
operator|.
name|equals
argument_list|(
name|segm
argument_list|)
operator|||
name|ReadOnlyVersionManager
operator|.
name|isVersionStoreTree
argument_list|(
name|t
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|,
name|tp
operator|instanceof
name|EmptyCugTreePermission
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|,
name|tp
operator|instanceof
name|CugTreePermission
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTreePermissionVersionableUnsupportedPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|versionable
init|=
name|root
operator|.
name|getTree
argument_list|(
name|UNSUPPORTED_PATH
argument_list|)
decl_stmt|;
name|Tree
name|vh
init|=
name|checkNotNull
argument_list|(
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|versionable
argument_list|)
argument_list|)
decl_stmt|;
name|CugPermissionProvider
name|pp
init|=
name|createCugPermissionProvider
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|SUPPORTED_PATH2
argument_list|)
argument_list|)
decl_stmt|;
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
name|vh
operator|.
name|getPath
argument_list|()
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
if|if
condition|(
name|JCR_SYSTEM
operator|.
name|equals
argument_list|(
name|segm
argument_list|)
operator|||
name|ReadOnlyVersionManager
operator|.
name|isVersionStoreTree
argument_list|(
name|t
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|,
name|tp
operator|instanceof
name|EmptyCugTreePermission
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertSame
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|,
name|TreePermission
operator|.
name|NO_RECOURSE
argument_list|,
name|tp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTreePermissionAtVersionableAboveSupported
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|vh
init|=
name|checkNotNull
argument_list|(
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|CugPermissionProvider
name|pp
init|=
name|createCugPermissionProvider
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|SUPPORTED_PATH
operator|+
literal|"/a"
argument_list|)
argument_list|)
decl_stmt|;
name|TreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|root
argument_list|,
name|vh
operator|.
name|getPath
argument_list|()
argument_list|,
name|pp
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|instanceof
name|EmptyCugTreePermission
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCugAtRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|versionable
init|=
name|root
operator|.
name|getTree
argument_list|(
name|UNSUPPORTED_PATH
argument_list|)
decl_stmt|;
name|String
name|vhPath
init|=
name|checkNotNull
argument_list|(
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|versionable
argument_list|)
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
try|try
block|{
name|createCug
argument_list|(
name|root
argument_list|,
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|,
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|CugPermissionProvider
name|pp
init|=
name|createCugPermissionProvider
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
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
name|assertTrue
argument_list|(
name|tp
operator|instanceof
name|CugTreePermission
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|segm
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|vhPath
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
name|assertTrue
argument_list|(
name|tp
operator|instanceof
name|CugTreePermission
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|removeProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|)
expr_stmt|;
name|Tree
name|cug
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/rep:cugPolicy"
argument_list|)
decl_stmt|;
if|if
condition|(
name|cug
operator|.
name|exists
argument_list|()
condition|)
block|{
name|cug
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVersionableWithUnsupportedType
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|versionable
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/content"
argument_list|)
decl_stmt|;
name|Tree
name|vh
init|=
name|checkNotNull
argument_list|(
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|versionable
argument_list|)
argument_list|)
decl_stmt|;
name|Tree
name|frozen
init|=
name|vh
operator|.
name|getChild
argument_list|(
literal|"1.0"
argument_list|)
operator|.
name|getChild
argument_list|(
name|JCR_FROZENNODE
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|Tree
name|invalidFrozen
init|=
name|frozen
operator|.
name|addChild
argument_list|(
name|REP_CUG_POLICY
argument_list|)
decl_stmt|;
name|invalidFrozen
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_CUG_POLICY
argument_list|)
expr_stmt|;
name|CugPermissionProvider
name|pp
init|=
name|createCugPermissionProvider
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|SUPPORTED_PATH2
argument_list|)
argument_list|)
decl_stmt|;
name|TreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|root
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|vh
operator|.
name|getPath
argument_list|()
argument_list|,
literal|"1.0"
argument_list|,
name|JCR_FROZENNODE
argument_list|,
literal|"a/b/c"
argument_list|)
argument_list|,
name|pp
argument_list|)
decl_stmt|;
name|TreePermission
name|tpForUnsupportedType
init|=
name|pp
operator|.
name|getTreePermission
argument_list|(
name|invalidFrozen
argument_list|,
name|TreeType
operator|.
name|VERSION
argument_list|,
name|tp
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TreePermission
operator|.
name|NO_RECOURSE
argument_list|,
name|tpForUnsupportedType
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVersionableWithCugParent
parameter_list|()
throws|throws
name|Exception
block|{
name|addVersionContent
argument_list|(
literal|"/content/aa/bb/cc"
argument_list|)
expr_stmt|;
name|Tree
name|cc
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/content/aa/bb/cc"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|CugUtil
operator|.
name|hasCug
argument_list|(
name|cc
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|vh
init|=
name|checkNotNull
argument_list|(
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|cc
argument_list|)
argument_list|)
decl_stmt|;
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
name|CugPermissionProvider
name|pp
init|=
name|createCugPermissionProvider
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|SUPPORTED_PATH2
argument_list|)
argument_list|,
name|getTestGroupPrincipal
argument_list|()
argument_list|)
decl_stmt|;
name|TreePermission
name|tp
init|=
name|getTreePermission
argument_list|(
name|root
argument_list|,
name|vh
operator|.
name|getPath
argument_list|()
argument_list|,
name|pp
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|instanceof
name|CugTreePermission
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|CugTreePermission
operator|)
name|tp
operator|)
operator|.
name|isInCug
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|CugTreePermission
operator|)
name|tp
operator|)
operator|.
name|isAllow
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

