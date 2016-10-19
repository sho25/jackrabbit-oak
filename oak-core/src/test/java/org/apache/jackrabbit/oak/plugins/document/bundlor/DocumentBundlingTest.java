begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|document
operator|.
name|bundlor
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|document
operator|.
name|Collection
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|DocumentMKBuilderProvider
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
name|document
operator|.
name|DocumentNodeStore
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
name|document
operator|.
name|memory
operator|.
name|MemoryDocumentStore
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
name|document
operator|.
name|util
operator|.
name|Utils
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
name|EqualsDiff
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
name|NodeStateUtils
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
name|Rule
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
name|collect
operator|.
name|ImmutableList
operator|.
name|copyOf
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
name|oak
operator|.
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|state
operator|.
name|NodeStateUtils
operator|.
name|getNode
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|hasItems
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
name|assertThat
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
name|DocumentBundlingTest
block|{
annotation|@
name|Rule
specifier|public
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|store
decl_stmt|;
specifier|private
name|RecordingDocumentStore
name|ds
init|=
operator|new
name|RecordingDocumentStore
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUpBundlor
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|store
operator|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|ds
argument_list|)
operator|.
name|memoryCacheSize
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|NodeState
name|registryState
init|=
name|BundledTypesRegistry
operator|.
name|builder
argument_list|()
operator|.
name|forType
argument_list|(
literal|"nt:file"
argument_list|,
literal|"jcr:content"
argument_list|)
operator|.
name|registry
argument_list|()
operator|.
name|forType
argument_list|(
literal|"app:Asset"
argument_list|)
operator|.
name|include
argument_list|(
literal|"jcr:content"
argument_list|)
operator|.
name|include
argument_list|(
literal|"jcr:content/metadata"
argument_list|)
operator|.
name|include
argument_list|(
literal|"jcr:content/renditions"
argument_list|)
operator|.
name|include
argument_list|(
literal|"jcr:content/renditions/**"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
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
literal|"jcr:system"
argument_list|)
operator|.
name|child
argument_list|(
literal|"documentstore"
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"bundlor"
argument_list|,
name|registryState
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|saveAndReadNtFile
parameter_list|()
throws|throws
name|Exception
block|{
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
name|fileNode
init|=
name|newNode
argument_list|(
literal|"nt:file"
argument_list|)
decl_stmt|;
name|fileNode
operator|.
name|child
argument_list|(
literal|"jcr:content"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:data"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"book.jpg"
argument_list|,
name|fileNode
operator|.
name|getNodeState
argument_list|()
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|store
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|"2:/test/book.jpg"
argument_list|)
operator|.
name|asString
argument_list|()
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeState
name|fileNodeState
init|=
name|root
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fileNodeState
operator|.
name|getChildNode
argument_list|(
literal|"book.jpg"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileNodeState
operator|.
name|getChildNode
argument_list|(
literal|"book.jpg"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"jcr:content"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PartialEqualsDiff
operator|.
name|equals
argument_list|(
name|fileNode
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|fileNodeState
operator|.
name|getChildNode
argument_list|(
literal|"book.jpg"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//TODO Test _bin being set
annotation|@
name|Test
specifier|public
name|void
name|bundledParent
parameter_list|()
throws|throws
name|Exception
block|{
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
name|appNB
init|=
name|newNode
argument_list|(
literal|"app:Asset"
argument_list|)
decl_stmt|;
name|createChild
argument_list|(
name|appNB
argument_list|,
literal|"jcr:content"
argument_list|,
comment|//Bundled
literal|"jcr:content/comments"
comment|//Not bundled. Parent bundled
argument_list|)
expr_stmt|;
name|dump
argument_list|(
name|appNB
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"book.jpg"
argument_list|,
name|appNB
operator|.
name|getNodeState
argument_list|()
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|queryChildren
parameter_list|()
throws|throws
name|Exception
block|{
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
name|appNB
init|=
name|newNode
argument_list|(
literal|"app:Asset"
argument_list|)
decl_stmt|;
name|createChild
argument_list|(
name|appNB
argument_list|,
literal|"jcr:content"
argument_list|,
literal|"jcr:content/comments"
argument_list|,
comment|//not bundled
literal|"jcr:content/metadata"
argument_list|,
literal|"jcr:content/metadata/xmp"
argument_list|,
comment|//not bundled
literal|"jcr:content/renditions"
argument_list|,
comment|//includes all
literal|"jcr:content/renditions/original"
argument_list|,
literal|"jcr:content/renditions/original/jcr:content"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"book.jpg"
argument_list|,
name|appNB
operator|.
name|getNodeState
argument_list|()
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
name|NodeState
name|appNode
init|=
name|getNode
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"test/book.jpg"
argument_list|)
decl_stmt|;
name|ds
operator|.
name|reset
argument_list|()
expr_stmt|;
name|int
name|childCount
init|=
name|Iterables
operator|.
name|size
argument_list|(
name|appNode
operator|.
name|getChildNodeEntries
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|childCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ds
operator|.
name|queryPaths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|childNames
argument_list|(
name|appNode
argument_list|,
literal|"jcr:content"
argument_list|)
argument_list|,
name|hasItems
argument_list|(
literal|"comments"
argument_list|,
literal|"metadata"
argument_list|,
literal|"renditions"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|getNode
argument_list|(
name|appNode
argument_list|,
literal|"jcr:content"
argument_list|)
operator|.
name|getChildNodeCount
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|childNames
argument_list|(
name|appNode
argument_list|,
literal|"jcr:content/metadata"
argument_list|)
argument_list|,
name|hasItems
argument_list|(
literal|"xmp"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getNode
argument_list|(
name|appNode
argument_list|,
literal|"jcr:content/metadata"
argument_list|)
operator|.
name|getChildNodeCount
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|ds
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|//For bundled case no query should be fired
name|assertThat
argument_list|(
name|childNames
argument_list|(
name|appNode
argument_list|,
literal|"jcr:content/renditions"
argument_list|)
argument_list|,
name|hasItems
argument_list|(
literal|"original"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getNode
argument_list|(
name|appNode
argument_list|,
literal|"jcr:content/renditions"
argument_list|)
operator|.
name|getChildNodeCount
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ds
operator|.
name|queryPaths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|childNames
argument_list|(
name|appNode
argument_list|,
literal|"jcr:content/renditions/original"
argument_list|)
argument_list|,
name|hasItems
argument_list|(
literal|"jcr:content"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getNode
argument_list|(
name|appNode
argument_list|,
literal|"jcr:content/renditions/original"
argument_list|)
operator|.
name|getChildNodeCount
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ds
operator|.
name|queryPaths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PartialEqualsDiff
operator|.
name|equals
argument_list|(
name|appNB
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|appNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addBundledNodePostInitialCreation
parameter_list|()
throws|throws
name|Exception
block|{
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
name|appNB
init|=
name|newNode
argument_list|(
literal|"app:Asset"
argument_list|)
decl_stmt|;
name|createChild
argument_list|(
name|appNB
argument_list|,
literal|"jcr:content"
argument_list|,
literal|"jcr:content/comments"
argument_list|,
comment|//not bundled
literal|"jcr:content/metadata"
argument_list|,
literal|"jcr:content/metadata/xmp"
argument_list|,
comment|//not bundled
literal|"jcr:content/renditions"
argument_list|,
comment|//includes all
literal|"jcr:content/renditions/original"
argument_list|,
literal|"jcr:content/renditions/original/jcr:content"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"book.jpg"
argument_list|,
name|appNB
operator|.
name|getNodeState
argument_list|()
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
name|builder
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|NodeBuilder
name|renditions
init|=
name|childBuilder
argument_list|(
name|builder
argument_list|,
literal|"/test/book.jpg/jcr:content/renditions"
argument_list|)
decl_stmt|;
name|renditions
operator|.
name|child
argument_list|(
literal|"small"
argument_list|)
operator|.
name|child
argument_list|(
literal|"jcr:content"
argument_list|)
expr_stmt|;
name|NodeState
name|appNode_v2
init|=
name|childBuilder
argument_list|(
name|builder
argument_list|,
literal|"/test/book.jpg"
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
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
name|assertThat
argument_list|(
name|childNames
argument_list|(
name|getLatestNode
argument_list|(
literal|"/test/book.jpg"
argument_list|)
argument_list|,
literal|"jcr:content/renditions"
argument_list|)
argument_list|,
name|hasItems
argument_list|(
literal|"original"
argument_list|,
literal|"small"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PartialEqualsDiff
operator|.
name|equals
argument_list|(
name|getLatestNode
argument_list|(
literal|"/test/book.jpg"
argument_list|)
argument_list|,
name|appNode_v2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|modifyBundledChild
parameter_list|()
throws|throws
name|Exception
block|{
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
name|appNB
init|=
name|newNode
argument_list|(
literal|"app:Asset"
argument_list|)
decl_stmt|;
name|createChild
argument_list|(
name|appNB
argument_list|,
literal|"jcr:content"
argument_list|,
literal|"jcr:content/comments"
argument_list|,
comment|//not bundled
literal|"jcr:content/metadata"
argument_list|,
literal|"jcr:content/metadata/xmp"
argument_list|,
comment|//not bundled
literal|"jcr:content/renditions"
argument_list|,
comment|//includes all
literal|"jcr:content/renditions/original"
argument_list|,
literal|"jcr:content/renditions/original/jcr:content"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"book.jpg"
argument_list|,
name|appNB
operator|.
name|getNodeState
argument_list|()
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
comment|//Modify bundled property
name|builder
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|childBuilder
argument_list|(
name|builder
argument_list|,
literal|"/test/book.jpg/jcr:content"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
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
name|NodeState
name|state
init|=
name|childBuilder
argument_list|(
name|builder
argument_list|,
literal|"/test/book.jpg"
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|getLatestNode
argument_list|(
literal|"/test/book.jpg/jcr:content"
argument_list|)
operator|.
name|getString
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PartialEqualsDiff
operator|.
name|equals
argument_list|(
name|state
argument_list|,
name|getLatestNode
argument_list|(
literal|"/test/book.jpg"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//Modify deep bundled property
name|builder
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|childBuilder
argument_list|(
name|builder
argument_list|,
literal|"/test/book.jpg/jcr:content/renditions"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
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
name|state
operator|=
name|childBuilder
argument_list|(
name|builder
argument_list|,
literal|"/test/book.jpg"
argument_list|)
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|getLatestNode
argument_list|(
literal|"/test/book.jpg/jcr:content/renditions"
argument_list|)
operator|.
name|getString
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PartialEqualsDiff
operator|.
name|equals
argument_list|(
name|state
argument_list|,
name|getLatestNode
argument_list|(
literal|"/test/book.jpg"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//Modify deep unbundled property - jcr:content/comments/@foo
name|builder
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|childBuilder
argument_list|(
name|builder
argument_list|,
literal|"/test/book.jpg/jcr:content/comments"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
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
name|state
operator|=
name|childBuilder
argument_list|(
name|builder
argument_list|,
literal|"/test/book.jpg"
argument_list|)
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|getLatestNode
argument_list|(
literal|"/test/book.jpg/jcr:content/comments"
argument_list|)
operator|.
name|getString
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PartialEqualsDiff
operator|.
name|equals
argument_list|(
name|state
argument_list|,
name|getLatestNode
argument_list|(
literal|"/test/book.jpg"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeState
name|getLatestNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|getNode
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|,
name|path
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|dump
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|NodeStateUtils
operator|.
name|toString
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|childNames
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
name|copyOf
argument_list|(
name|getNode
argument_list|(
name|state
argument_list|,
name|path
argument_list|)
operator|.
name|getChildNodeNames
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|newNode
parameter_list|(
name|String
name|typeName
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|typeName
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|createChild
parameter_list|(
name|NodeBuilder
name|root
parameter_list|,
name|String
modifier|...
name|paths
parameter_list|)
block|{
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|childBuilder
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|root
return|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|childBuilder
parameter_list|(
name|NodeBuilder
name|root
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|NodeBuilder
name|nb
init|=
name|root
decl_stmt|;
for|for
control|(
name|String
name|nodeName
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|nb
operator|=
name|nb
operator|.
name|child
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
block|}
return|return
name|nb
return|;
block|}
specifier|private
specifier|static
class|class
name|RecordingDocumentStore
extends|extends
name|MemoryDocumentStore
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|queryPaths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|findPaths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|find
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|,
name|int
name|maxCacheAge
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
condition|)
block|{
name|findPaths
operator|.
name|add
argument_list|(
name|Utils
operator|.
name|getPathFromId
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|find
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|query
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|fromKey
parameter_list|,
name|String
name|toKey
parameter_list|,
name|String
name|indexedProperty
parameter_list|,
name|long
name|startValue
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
condition|)
block|{
name|queryPaths
operator|.
name|add
argument_list|(
name|Utils
operator|.
name|getPathFromId
argument_list|(
name|Utils
operator|.
name|getParentIdFromLowerLimit
argument_list|(
name|fromKey
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|query
argument_list|(
name|collection
argument_list|,
name|fromKey
argument_list|,
name|toKey
argument_list|,
name|indexedProperty
argument_list|,
name|startValue
argument_list|,
name|limit
argument_list|)
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|queryPaths
operator|.
name|clear
argument_list|()
expr_stmt|;
name|findPaths
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|PartialEqualsDiff
extends|extends
name|EqualsDiff
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|ignoredProps
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|DocumentBundlor
operator|.
name|META_PROP_PATTERN
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|boolean
name|equals
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|before
operator|.
name|exists
argument_list|()
operator|==
name|after
operator|.
name|exists
argument_list|()
operator|&&
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|PartialEqualsDiff
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
name|ignore
argument_list|(
name|after
argument_list|)
condition|)
return|return
literal|true
return|;
return|return
name|super
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
name|ignore
argument_list|(
name|after
argument_list|)
condition|)
return|return
literal|true
return|;
return|return
name|super
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
if|if
condition|(
name|ignore
argument_list|(
name|before
argument_list|)
condition|)
return|return
literal|true
return|;
return|return
name|super
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|ignore
parameter_list|(
name|PropertyState
name|state
parameter_list|)
block|{
return|return
name|ignoredProps
operator|.
name|contains
argument_list|(
name|state
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

