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
name|plugins
operator|.
name|tree
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|assertNotEquals
import|;
end_import

begin_class
specifier|public
class|class
name|TreeTypeProviderTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|TreeTypeProvider
name|typeProvider
decl_stmt|;
specifier|private
name|List
argument_list|<
name|TypeTest
argument_list|>
name|tests
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
name|typeProvider
operator|=
operator|new
name|TreeTypeProvider
argument_list|(
name|getConfig
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|tests
operator|=
operator|new
name|ArrayList
argument_list|<
name|TypeTest
argument_list|>
argument_list|()
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
literal|"/"
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
literal|"/content"
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
literal|'/'
operator|+
name|JcrConstants
operator|.
name|JCR_SYSTEM
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
operator|+
literal|"/rep:system/rep:namedChildNodeDefinitions/jcr:versionStorage"
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
operator|+
literal|"/rep:system/rep:namedChildNodeDefinitions/jcr:activities"
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
operator|+
literal|"/rep:system/rep:namedChildNodeDefinitions/jcr:configurations"
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
operator|+
literal|"/rep:AccessControllable/rep:namedChildNodeDefinitions/rep:policy"
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
operator|+
literal|"/rep:AccessControllable/rep:namedChildNodeDefinitions/rep:policy/rep:Policy"
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
operator|+
literal|"/rep:ACL/rep:residualChildNodeDefinitions/rep:ACE"
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
operator|+
literal|"/rep:GrantACE/rep:namedChildNodeDefinitions/rep:restrictions"
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
operator|+
literal|"/rep:RepoAccessControllable/rep:namedChildNodeDefinitions/rep:repoPolicy"
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
operator|+
literal|"/rep:PermissionStore"
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
literal|"/:hidden"
argument_list|,
name|TreeType
operator|.
name|HIDDEN
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
literal|"/:hidden/child"
argument_list|,
name|TreeType
operator|.
name|HIDDEN
argument_list|,
name|TreeType
operator|.
name|HIDDEN
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
literal|"/oak:index/nodetype/:index"
argument_list|,
name|TreeType
operator|.
name|HIDDEN
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
literal|"/oak:index/nodetype/:index/child"
argument_list|,
name|TreeType
operator|.
name|HIDDEN
argument_list|,
name|TreeType
operator|.
name|HIDDEN
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|versionPath
range|:
name|VersionConstants
operator|.
name|SYSTEM_PATHS
control|)
block|{
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|versionPath
argument_list|,
name|TreeType
operator|.
name|VERSION
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|versionPath
operator|+
literal|"/a/b/child"
argument_list|,
name|TreeType
operator|.
name|VERSION
argument_list|,
name|TreeType
operator|.
name|VERSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|PermissionConstants
operator|.
name|PERMISSIONS_STORE_PATH
argument_list|,
name|TreeType
operator|.
name|INTERNAL
argument_list|)
argument_list|)
expr_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|PermissionConstants
operator|.
name|PERMISSIONS_STORE_PATH
operator|+
literal|"/a/b/child"
argument_list|,
name|TreeType
operator|.
name|INTERNAL
argument_list|,
name|TreeType
operator|.
name|INTERNAL
argument_list|)
argument_list|)
expr_stmt|;
name|NodeUtil
name|testTree
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
literal|"test"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|AccessControlConstants
operator|.
name|POLICY_NODE_NAMES
control|)
block|{
name|NodeUtil
name|acl
init|=
name|testTree
operator|.
name|addChild
argument_list|(
name|name
argument_list|,
name|AccessControlConstants
operator|.
name|NT_REP_ACL
argument_list|)
decl_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|acl
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|TreeType
operator|.
name|ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
name|NodeUtil
name|ace
init|=
name|acl
operator|.
name|addChild
argument_list|(
literal|"ace"
argument_list|,
name|AccessControlConstants
operator|.
name|NT_REP_DENY_ACE
argument_list|)
decl_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|ace
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|TreeType
operator|.
name|ACCESS_CONTROL
argument_list|,
name|TreeType
operator|.
name|ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
name|NodeUtil
name|ace2
init|=
name|acl
operator|.
name|addChild
argument_list|(
literal|"ace2"
argument_list|,
name|AccessControlConstants
operator|.
name|NT_REP_GRANT_ACE
argument_list|)
decl_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|ace2
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|TreeType
operator|.
name|ACCESS_CONTROL
argument_list|,
name|TreeType
operator|.
name|ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
name|NodeUtil
name|rest
init|=
name|ace2
operator|.
name|addChild
argument_list|(
name|AccessControlConstants
operator|.
name|REP_RESTRICTIONS
argument_list|,
name|AccessControlConstants
operator|.
name|NT_REP_RESTRICTIONS
argument_list|)
decl_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|rest
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|TreeType
operator|.
name|ACCESS_CONTROL
argument_list|,
name|TreeType
operator|.
name|ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
name|NodeUtil
name|invalid
init|=
name|rest
operator|.
name|addChild
argument_list|(
literal|"invalid"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|tests
operator|.
name|add
argument_list|(
operator|new
name|TypeTest
argument_list|(
name|invalid
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|TreeType
operator|.
name|ACCESS_CONTROL
argument_list|,
name|TreeType
operator|.
name|ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|refresh
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
name|Test
specifier|public
name|void
name|testGetType
parameter_list|()
block|{
for|for
control|(
name|TypeTest
name|test
range|:
name|tests
control|)
block|{
name|assertEquals
argument_list|(
name|test
operator|.
name|path
argument_list|,
name|test
operator|.
name|type
argument_list|,
name|typeProvider
operator|.
name|getType
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|test
operator|.
name|path
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
name|testGetTypeWithParentType
parameter_list|()
block|{
for|for
control|(
name|TypeTest
name|test
range|:
name|tests
control|)
block|{
name|assertEquals
argument_list|(
name|test
operator|.
name|path
argument_list|,
name|test
operator|.
name|type
argument_list|,
name|typeProvider
operator|.
name|getType
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|test
operator|.
name|path
argument_list|)
argument_list|,
name|test
operator|.
name|parentType
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTypeWithDefaultParentType
parameter_list|()
block|{
for|for
control|(
name|TypeTest
name|test
range|:
name|tests
control|)
block|{
name|TreeType
name|typeIfParentDefault
init|=
name|typeProvider
operator|.
name|getType
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|test
operator|.
name|path
argument_list|)
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
name|TreeType
operator|.
name|DEFAULT
operator|==
name|test
operator|.
name|parentType
condition|)
block|{
name|assertEquals
argument_list|(
name|test
operator|.
name|path
argument_list|,
name|test
operator|.
name|type
argument_list|,
name|typeIfParentDefault
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNotEquals
argument_list|(
name|test
operator|.
name|path
argument_list|,
name|test
operator|.
name|type
argument_list|,
name|typeIfParentDefault
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTypeForRootTree
parameter_list|()
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
name|assertEquals
argument_list|(
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|typeProvider
operator|.
name|getType
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
comment|// the type of the root tree is always 'DEFAULT' irrespective of the passed parent type.
name|assertEquals
argument_list|(
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|typeProvider
operator|.
name|getType
argument_list|(
name|t
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|typeProvider
operator|.
name|getType
argument_list|(
name|t
argument_list|,
name|TreeType
operator|.
name|HIDDEN
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|typeProvider
operator|.
name|getType
argument_list|(
name|t
argument_list|,
name|TreeType
operator|.
name|VERSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTypeForImmutableTree
parameter_list|()
block|{
for|for
control|(
name|String
name|path
range|:
operator|new
name|String
index|[]
block|{
literal|"/"
block|,
literal|"/testPath"
block|}
control|)
block|{
name|Tree
name|t
init|=
name|RootFactory
operator|.
name|createReadOnlyRoot
argument_list|(
name|root
argument_list|)
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|typeProvider
operator|.
name|getType
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
comment|// also for repeated calls
name|assertEquals
argument_list|(
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|typeProvider
operator|.
name|getType
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
comment|// the type of an immutable tree is set after the first call irrespective of the passed parent type.
name|assertEquals
argument_list|(
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|typeProvider
operator|.
name|getType
argument_list|(
name|t
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TreeType
operator|.
name|DEFAULT
argument_list|,
name|typeProvider
operator|.
name|getType
argument_list|(
name|t
argument_list|,
name|TreeType
operator|.
name|HIDDEN
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTypeForImmutableTreeWithParent
parameter_list|()
block|{
name|Tree
name|t
init|=
name|RootFactory
operator|.
name|createReadOnlyRoot
argument_list|(
name|root
argument_list|)
operator|.
name|getTree
argument_list|(
literal|"/:hidden/testPath"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TreeType
operator|.
name|HIDDEN
argument_list|,
name|typeProvider
operator|.
name|getType
argument_list|(
name|t
argument_list|,
name|TreeType
operator|.
name|HIDDEN
argument_list|)
argument_list|)
expr_stmt|;
comment|// the type of an immutable tree is set after the first call irrespective of the passed parent type.
name|assertEquals
argument_list|(
name|TreeType
operator|.
name|HIDDEN
argument_list|,
name|typeProvider
operator|.
name|getType
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TreeType
operator|.
name|HIDDEN
argument_list|,
name|typeProvider
operator|.
name|getType
argument_list|(
name|t
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TreeType
operator|.
name|HIDDEN
argument_list|,
name|typeProvider
operator|.
name|getType
argument_list|(
name|t
argument_list|,
name|TreeType
operator|.
name|ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TreeType
operator|.
name|HIDDEN
argument_list|,
name|typeProvider
operator|.
name|getType
argument_list|(
name|t
argument_list|,
name|TreeType
operator|.
name|VERSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TypeTest
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|TreeType
name|type
decl_stmt|;
specifier|private
specifier|final
name|TreeType
name|parentType
decl_stmt|;
specifier|private
name|TypeTest
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
name|TreeType
name|type
parameter_list|)
block|{
name|this
argument_list|(
name|path
argument_list|,
name|type
argument_list|,
name|TreeType
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
specifier|private
name|TypeTest
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
name|TreeType
name|type
parameter_list|,
name|TreeType
name|parentType
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|parentType
operator|=
name|parentType
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

