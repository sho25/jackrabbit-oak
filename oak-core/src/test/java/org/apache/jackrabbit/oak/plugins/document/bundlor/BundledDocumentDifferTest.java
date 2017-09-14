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
name|Collections
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
name|ArrayListMultimap
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
name|ListMultimap
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
name|commons
operator|.
name|json
operator|.
name|JsopBuilder
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
name|json
operator|.
name|JsopWriter
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
name|AbstractDocumentNodeState
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
name|DocumentNodeState
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
name|secondary
operator|.
name|DelegatingDocumentNodeState
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
name|secondary
operator|.
name|SecondaryStoreBuilder
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
name|secondary
operator|.
name|SecondaryStoreCache
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
name|secondary
operator|.
name|SecondaryStoreObserver
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
name|index
operator|.
name|PathFilter
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
name|memory
operator|.
name|MemoryNodeStore
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
name|InitialContent
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
name|DefaultNodeStateDiff
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
name|of
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
name|document
operator|.
name|DocumentNodeStore
operator|.
name|SYS_PROP_DISABLE_JOURNAL
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
name|document
operator|.
name|TestUtils
operator|.
name|childBuilder
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
name|document
operator|.
name|TestUtils
operator|.
name|createChild
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
name|document
operator|.
name|TestUtils
operator|.
name|merge
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
name|document
operator|.
name|bundlor
operator|.
name|BundlingConfigHandler
operator|.
name|BUNDLOR
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
name|document
operator|.
name|bundlor
operator|.
name|BundlingConfigHandler
operator|.
name|DOCUMENT_NODE_STORE
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
name|document
operator|.
name|TestUtils
operator|.
name|asDocumentState
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
name|document
operator|.
name|bundlor
operator|.
name|DocumentBundlingTest
operator|.
name|newNode
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
name|hasItem
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
name|BundledDocumentDifferTest
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
name|String
name|journalDisabledProp
decl_stmt|;
specifier|private
name|BundledDocumentDiffer
name|differ
decl_stmt|;
specifier|private
name|MemoryNodeStore
name|secondary
init|=
operator|new
name|MemoryNodeStore
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
name|journalDisabledProp
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|SYS_PROP_DISABLE_JOURNAL
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|SYS_PROP_DISABLE_JOURNAL
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|store
operator|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
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
operator|new
name|InitialContent
argument_list|()
operator|.
name|initialize
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|BundlingConfigInitializer
operator|.
name|INSTANCE
operator|.
name|initialize
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"jcr:system"
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|DOCUMENT_NODE_STORE
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|BUNDLOR
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"app:Asset"
argument_list|,
name|registryState
operator|.
name|getChildNode
argument_list|(
literal|"app:Asset"
argument_list|)
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|differ
operator|=
operator|new
name|BundledDocumentDiffer
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|resetJournalUsage
parameter_list|()
block|{
if|if
condition|(
name|journalDisabledProp
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|SYS_PROP_DISABLE_JOURNAL
argument_list|,
name|journalDisabledProp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|SYS_PROP_DISABLE_JOURNAL
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDiff
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|builder
init|=
name|createContentStructure
argument_list|()
decl_stmt|;
name|NodeState
name|r1
init|=
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
decl_stmt|;
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
name|NodeState
name|r2
init|=
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|JsopWriter
name|w
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|String
name|path
init|=
literal|"/test"
decl_stmt|;
name|assertTrue
argument_list|(
name|differ
operator|.
name|diff
argument_list|(
name|dns
argument_list|(
name|r1
argument_list|,
name|path
argument_list|)
argument_list|,
name|dns
argument_list|(
name|r2
argument_list|,
name|path
argument_list|)
argument_list|,
name|w
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|w
operator|.
name|toString
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|=
operator|new
name|JsopBuilder
argument_list|()
expr_stmt|;
name|path
operator|=
literal|"/test/book.jpg"
expr_stmt|;
name|assertFalse
argument_list|(
name|differ
operator|.
name|diff
argument_list|(
name|dns
argument_list|(
name|r1
argument_list|,
name|path
argument_list|)
argument_list|,
name|dns
argument_list|(
name|r2
argument_list|,
name|path
argument_list|)
argument_list|,
name|w
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"^\"jcr:content\":{}"
argument_list|,
name|w
operator|.
name|toString
argument_list|()
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
name|childBuilder
argument_list|(
name|builder
argument_list|,
literal|"/test/book.jpg/foo"
argument_list|)
expr_stmt|;
name|NodeState
name|r3
init|=
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|w
operator|=
operator|new
name|JsopBuilder
argument_list|()
expr_stmt|;
name|path
operator|=
literal|"/test/book.jpg"
expr_stmt|;
comment|//As there is a non bundled child differ should return true to continue diffing
name|assertTrue
argument_list|(
name|differ
operator|.
name|diff
argument_list|(
name|dns
argument_list|(
name|r1
argument_list|,
name|path
argument_list|)
argument_list|,
name|dns
argument_list|(
name|r3
argument_list|,
name|path
argument_list|)
argument_list|,
name|w
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"^\"jcr:content\":{}"
argument_list|,
name|w
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|diffWithSecondary
parameter_list|()
throws|throws
name|Exception
block|{
name|configureSecondary
argument_list|()
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|createContentStructure
argument_list|()
decl_stmt|;
name|NodeState
name|r1
init|=
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|NodeState
name|rs1
init|=
name|DelegatingDocumentNodeState
operator|.
name|wrap
argument_list|(
name|secondary
operator|.
name|getRoot
argument_list|()
argument_list|,
name|store
argument_list|)
decl_stmt|;
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
name|NodeState
name|r2
init|=
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|JsopWriter
name|w
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|String
name|path
init|=
literal|"/test/book.jpg"
decl_stmt|;
name|assertFalse
argument_list|(
name|differ
operator|.
name|diff
argument_list|(
name|adns
argument_list|(
name|rs1
argument_list|,
name|path
argument_list|)
argument_list|,
name|adns
argument_list|(
name|r2
argument_list|,
name|path
argument_list|)
argument_list|,
name|w
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"^\"jcr:content\":{}"
argument_list|,
name|w
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|diffFewChildren
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|builder
init|=
name|createContentStructure
argument_list|()
decl_stmt|;
name|NodeState
name|r1
init|=
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
decl_stmt|;
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
name|childBuilder
argument_list|(
name|builder
argument_list|,
literal|"/test/book.jpg/jcr:content/renditions/newChild2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|childBuilder
argument_list|(
name|builder
argument_list|,
literal|"/test/book.jpg/newChild1"
argument_list|)
expr_stmt|;
name|NodeState
name|r2
init|=
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|String
name|path
init|=
literal|"/test/book.jpg"
decl_stmt|;
name|CollectingDiff
name|diff
init|=
operator|new
name|CollectingDiff
argument_list|()
decl_stmt|;
name|adns
argument_list|(
name|r2
argument_list|,
name|path
argument_list|)
operator|.
name|compareAgainstBaseState
argument_list|(
name|adns
argument_list|(
name|r1
argument_list|,
name|path
argument_list|)
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|diff
operator|.
name|changes
operator|.
name|get
argument_list|(
literal|"added"
argument_list|)
argument_list|,
name|hasItem
argument_list|(
literal|"newChild1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|diff
operator|.
name|changes
operator|.
name|get
argument_list|(
literal|"changed"
argument_list|)
argument_list|,
name|hasItem
argument_list|(
literal|"jcr:content"
argument_list|)
argument_list|)
expr_stmt|;
name|diff
operator|=
operator|new
name|CollectingDiff
argument_list|()
expr_stmt|;
name|path
operator|=
literal|"/test/book.jpg/jcr:content/renditions"
expr_stmt|;
name|adns
argument_list|(
name|r2
argument_list|,
name|path
argument_list|)
operator|.
name|compareAgainstBaseState
argument_list|(
name|adns
argument_list|(
name|r1
argument_list|,
name|path
argument_list|)
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|diff
operator|.
name|changes
operator|.
name|get
argument_list|(
literal|"added"
argument_list|)
argument_list|,
name|hasItem
argument_list|(
literal|"newChild2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|jsopDiff
parameter_list|()
throws|throws
name|Exception
block|{
name|JsopWriter
name|w
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|differ
operator|.
name|diffChildren
argument_list|(
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|of
argument_list|(
literal|"b"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
name|w
argument_list|)
expr_stmt|;
comment|//removed a
comment|//changed b
comment|//added b
name|assertEquals
argument_list|(
literal|"-\"a\"^\"b\":{}+\"c\":{}"
argument_list|,
name|w
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeBuilder
name|createContentStructure
parameter_list|()
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
return|return
name|builder
return|;
block|}
specifier|private
specifier|static
class|class
name|CollectingDiff
extends|extends
name|DefaultNodeStateDiff
block|{
specifier|private
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|changes
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|changes
operator|.
name|get
argument_list|(
literal|"added"
argument_list|)
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|changes
operator|.
name|get
argument_list|(
literal|"changed"
argument_list|)
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
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
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|changes
operator|.
name|get
argument_list|(
literal|"deleted"
argument_list|)
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|changes
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|private
name|DocumentNodeState
name|dns
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
name|asDocumentState
argument_list|(
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|AbstractDocumentNodeState
name|adns
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
operator|(
name|AbstractDocumentNodeState
operator|)
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
return|;
block|}
specifier|private
name|SecondaryStoreCache
name|configureSecondary
parameter_list|()
block|{
name|SecondaryStoreBuilder
name|builder
init|=
name|createBuilder
argument_list|(
operator|new
name|PathFilter
argument_list|(
name|of
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|metaPropNames
argument_list|(
name|DocumentNodeStore
operator|.
name|META_PROP_NAMES
argument_list|)
expr_stmt|;
name|SecondaryStoreCache
name|cache
init|=
name|builder
operator|.
name|buildCache
argument_list|()
decl_stmt|;
name|SecondaryStoreObserver
name|observer
init|=
name|builder
operator|.
name|buildObserver
argument_list|(
name|cache
argument_list|)
decl_stmt|;
name|store
operator|.
name|addObserver
argument_list|(
name|observer
argument_list|)
expr_stmt|;
return|return
name|cache
return|;
block|}
specifier|private
name|SecondaryStoreBuilder
name|createBuilder
parameter_list|(
name|PathFilter
name|pathFilter
parameter_list|)
block|{
return|return
operator|new
name|SecondaryStoreBuilder
argument_list|(
name|secondary
argument_list|)
operator|.
name|pathFilter
argument_list|(
name|pathFilter
argument_list|)
return|;
block|}
block|}
end_class

end_unit

