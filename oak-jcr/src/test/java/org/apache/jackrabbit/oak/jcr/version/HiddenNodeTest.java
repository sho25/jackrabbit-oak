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
name|jcr
operator|.
name|version
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
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
name|version
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|VersionManager
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
name|jcr
operator|.
name|Jcr
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
name|segment
operator|.
name|SegmentNodeStore
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
name|segment
operator|.
name|memory
operator|.
name|MemoryStore
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
name|impl
operator|.
name|TreeConstants
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
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|EmptyHook
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
name|state
operator|.
name|NodeBuilder
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
name|state
operator|.
name|NodeState
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
name|state
operator|.
name|NodeStore
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
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|jcr
operator|.
name|AbstractRepositoryTest
operator|.
name|dispose
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

begin_comment
comment|/**  * Checks if hidden nodes are properly handled on checkin and restore (OAK-1219, OAK-OAK-1226).  */
end_comment

begin_class
specifier|public
class|class
name|HiddenNodeTest
block|{
specifier|private
name|NodeStore
name|store
decl_stmt|;
specifier|private
name|Repository
name|repo
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|VersionManager
name|vMgr
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
name|store
operator|=
operator|new
name|SegmentNodeStore
argument_list|(
operator|new
name|MemoryStore
argument_list|()
argument_list|)
expr_stmt|;
name|repo
operator|=
operator|new
name|Jcr
argument_list|(
name|store
argument_list|)
operator|.
name|createRepository
argument_list|()
expr_stmt|;
name|session
operator|=
name|repo
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|vMgr
operator|=
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getVersionManager
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
name|session
operator|=
literal|null
expr_stmt|;
block|}
name|repo
operator|=
name|dispose
argument_list|(
name|repo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|hiddenTree
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|test
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|test
operator|.
name|addMixin
argument_list|(
literal|"mix:versionable"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|child
argument_list|(
literal|":hidden"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"property"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Version
name|v1
init|=
name|vMgr
operator|.
name|checkpoint
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|Version
name|v2
init|=
name|vMgr
operator|.
name|checkpoint
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|NodeState
name|state
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|v2
operator|.
name|getPath
argument_list|()
argument_list|)
control|)
block|{
name|state
operator|=
name|state
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|state
operator|=
name|state
operator|.
name|getChildNode
argument_list|(
literal|"jcr:frozenNode"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|state
operator|.
name|hasChildNode
argument_list|(
literal|":hidden"
argument_list|)
argument_list|)
expr_stmt|;
name|vMgr
operator|.
name|restore
argument_list|(
name|v1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|state
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|hasChildNode
argument_list|(
literal|":hidden"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|hiddenProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|test
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|test
operator|.
name|addMixin
argument_list|(
literal|"mix:versionable"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|testBuilder
init|=
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|testBuilder
operator|.
name|setProperty
argument_list|(
literal|":hiddenProperty"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Version
name|v1
init|=
name|vMgr
operator|.
name|checkpoint
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|Version
name|v2
init|=
name|vMgr
operator|.
name|checkpoint
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|NodeState
name|state
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|v2
operator|.
name|getPath
argument_list|()
argument_list|)
control|)
block|{
name|state
operator|=
name|state
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|state
operator|=
name|state
operator|.
name|getChildNode
argument_list|(
name|VersionConstants
operator|.
name|JCR_FROZENNODE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|state
operator|.
name|hasProperty
argument_list|(
literal|":hiddenProperty"
argument_list|)
argument_list|)
expr_stmt|;
name|vMgr
operator|.
name|restore
argument_list|(
name|v1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|state
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|state
operator|.
name|hasProperty
argument_list|(
literal|":hiddenProperty"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|hiddenOrderProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|test
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|test
operator|.
name|addNode
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|test
operator|.
name|addNode
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|test
operator|.
name|addNode
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|test
operator|.
name|addMixin
argument_list|(
literal|"mix:versionable"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|NodeBuilder
name|testBuilder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|testBuilder
operator|.
name|hasProperty
argument_list|(
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Version
name|v1
init|=
name|vMgr
operator|.
name|checkpoint
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|Version
name|v2
init|=
name|vMgr
operator|.
name|checkpoint
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|NodeState
name|state
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|v2
operator|.
name|getPath
argument_list|()
argument_list|)
control|)
block|{
name|state
operator|=
name|state
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|state
operator|=
name|state
operator|.
name|getChildNode
argument_list|(
name|VersionConstants
operator|.
name|JCR_FROZENNODE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|hasProperty
argument_list|(
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
argument_list|)
argument_list|)
expr_stmt|;
name|vMgr
operator|.
name|restore
argument_list|(
name|v1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|state
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|hasProperty
argument_list|(
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|hiddenChildNode
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|test
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|test
operator|.
name|addMixin
argument_list|(
literal|"mix:versionable"
argument_list|)
expr_stmt|;
name|test
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|testBuilder
init|=
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"child"
argument_list|)
decl_stmt|;
name|testBuilder
operator|.
name|child
argument_list|(
literal|":hidden"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"property"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Version
name|v1
init|=
name|vMgr
operator|.
name|checkpoint
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|Version
name|v2
init|=
name|vMgr
operator|.
name|checkpoint
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|NodeState
name|state
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|v2
operator|.
name|getPath
argument_list|()
argument_list|)
control|)
block|{
name|state
operator|=
name|state
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|state
operator|=
name|state
operator|.
name|getChildNode
argument_list|(
literal|"jcr:frozenNode"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"child"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|state
operator|.
name|hasChildNode
argument_list|(
literal|":hidden"
argument_list|)
argument_list|)
expr_stmt|;
name|vMgr
operator|.
name|restore
argument_list|(
name|v1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|state
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"child"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|state
operator|.
name|hasChildNode
argument_list|(
literal|":hidden"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|hiddenChildProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|test
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|test
operator|.
name|addMixin
argument_list|(
literal|"mix:versionable"
argument_list|)
expr_stmt|;
name|test
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|testBuilder
init|=
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"child"
argument_list|)
decl_stmt|;
name|testBuilder
operator|.
name|setProperty
argument_list|(
literal|":hiddenProperty"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Version
name|v1
init|=
name|vMgr
operator|.
name|checkpoint
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|Version
name|v2
init|=
name|vMgr
operator|.
name|checkpoint
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|NodeState
name|state
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|v2
operator|.
name|getPath
argument_list|()
argument_list|)
control|)
block|{
name|state
operator|=
name|state
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|state
operator|=
name|state
operator|.
name|getChildNode
argument_list|(
name|VersionConstants
operator|.
name|JCR_FROZENNODE
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"child"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|state
operator|.
name|hasProperty
argument_list|(
literal|":hiddenProperty"
argument_list|)
argument_list|)
expr_stmt|;
name|vMgr
operator|.
name|restore
argument_list|(
name|v1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|state
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"child"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|state
operator|.
name|hasProperty
argument_list|(
literal|":hiddenProperty"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|hiddenChildOrderProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|test
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|Node
name|child
init|=
name|test
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|)
decl_stmt|;
name|child
operator|.
name|addNode
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|child
operator|.
name|addNode
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|child
operator|.
name|addNode
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|test
operator|.
name|addMixin
argument_list|(
literal|"mix:versionable"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|NodeBuilder
name|testBuilder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"child"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|testBuilder
operator|.
name|hasProperty
argument_list|(
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Version
name|v1
init|=
name|vMgr
operator|.
name|checkpoint
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|Version
name|v2
init|=
name|vMgr
operator|.
name|checkpoint
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|NodeState
name|state
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|v2
operator|.
name|getPath
argument_list|()
argument_list|)
control|)
block|{
name|state
operator|=
name|state
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|state
operator|=
name|state
operator|.
name|getChildNode
argument_list|(
name|VersionConstants
operator|.
name|JCR_FROZENNODE
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"child"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|hasProperty
argument_list|(
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
argument_list|)
argument_list|)
expr_stmt|;
name|vMgr
operator|.
name|restore
argument_list|(
name|v1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|state
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"child"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|hasProperty
argument_list|(
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

