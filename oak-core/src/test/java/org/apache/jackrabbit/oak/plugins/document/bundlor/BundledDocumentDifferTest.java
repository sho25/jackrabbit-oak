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
name|nodetype
operator|.
name|write
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
name|bundlor
operator|.
name|DocumentBundlingTest
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
name|AbstractDocumentNodeState
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
block|}
end_class

end_unit

