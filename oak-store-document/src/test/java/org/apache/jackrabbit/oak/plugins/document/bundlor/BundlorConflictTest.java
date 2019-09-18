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
name|DocumentStore
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
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
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
name|JcrConstants
operator|.
name|NT_FILE
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
name|NT_RESOURCE
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
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
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|BundlorConflictTest
block|{
annotation|@
name|Rule
specifier|public
name|ExpectedException
name|thrown
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
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
name|store1
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|store2
decl_stmt|;
specifier|private
name|DocumentStore
name|ds
init|=
operator|new
name|MemoryDocumentStore
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
name|store1
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
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|store2
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
name|setClusterId
argument_list|(
literal|2
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|store1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|prevState
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"oldState"
argument_list|)
decl_stmt|;
comment|//Old state nt:file
name|createFile
argument_list|(
name|prevState
argument_list|,
literal|"file"
argument_list|)
expr_stmt|;
comment|//Old state app:Asset
name|createAsset
argument_list|(
name|prevState
argument_list|,
literal|"assset"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store1
argument_list|,
name|builder
argument_list|)
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
name|builder
operator|=
name|store1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
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
name|merge
argument_list|(
name|store1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|syncStores
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|simpleConflict
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|root
init|=
name|store1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|getRendBuilder
argument_list|(
name|createAsset
argument_list|(
name|root
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"rend-orig"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"meta"
argument_list|,
literal|"orig"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store1
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|syncStores
argument_list|()
expr_stmt|;
name|NodeBuilder
name|root1
init|=
name|store1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|getRendBuilder
argument_list|(
name|root1
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
operator|.
name|child
argument_list|(
literal|"rend1"
argument_list|)
expr_stmt|;
comment|//create a new rendition
name|NodeBuilder
name|root2
init|=
name|store2
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|getRendBuilder
argument_list|(
name|root2
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
comment|//remove rendition parent
name|merge
argument_list|(
name|store1
argument_list|,
name|root1
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expect
argument_list|(
name|CommitFailedException
operator|.
name|class
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store2
argument_list|,
name|root2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeChangedNode
parameter_list|()
throws|throws
name|Exception
block|{
comment|// do not perform retries on merge
name|store2
operator|.
name|setMaxBackOffMillis
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|store1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createFile
argument_list|(
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
argument_list|,
literal|"book.jpg"
argument_list|)
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
literal|"test"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|syncStores
argument_list|()
expr_stmt|;
name|NodeBuilder
name|b1
init|=
name|store1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|b2
init|=
name|store2
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|childBuilder
argument_list|(
name|b1
argument_list|,
literal|"/test/book.jpg/jcr:content"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:data"
argument_list|,
literal|"modified"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store1
argument_list|,
name|b1
argument_list|)
expr_stmt|;
name|childBuilder
argument_list|(
name|b2
argument_list|,
literal|"/test/book.jpg/jcr:content"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
try|try
block|{
name|merge
argument_list|(
name|store2
argument_list|,
name|b2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must fail with CommitFailedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"ConflictException"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|changeRemovedNode
parameter_list|()
throws|throws
name|Exception
block|{
comment|// do not perform retries on merge
name|store2
operator|.
name|setMaxBackOffMillis
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|store1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createFile
argument_list|(
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
argument_list|,
literal|"book.jpg"
argument_list|)
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
literal|"test"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|syncStores
argument_list|()
expr_stmt|;
name|NodeBuilder
name|b1
init|=
name|store1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|b2
init|=
name|store2
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|childBuilder
argument_list|(
name|b1
argument_list|,
literal|"/test/book.jpg/jcr:content"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|merge
argument_list|(
name|store1
argument_list|,
name|b1
argument_list|)
expr_stmt|;
name|childBuilder
argument_list|(
name|b2
argument_list|,
literal|"/test/book.jpg/jcr:content"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:data"
argument_list|,
literal|"modified"
argument_list|)
expr_stmt|;
try|try
block|{
name|merge
argument_list|(
name|store2
argument_list|,
name|b2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must fail with CommitFailedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"ConflictException"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeRemovedNode
parameter_list|()
throws|throws
name|Exception
block|{
comment|// do not perform retries on merge
name|store2
operator|.
name|setMaxBackOffMillis
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|store1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createFile
argument_list|(
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
argument_list|,
literal|"book.jpg"
argument_list|)
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
literal|"test"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|syncStores
argument_list|()
expr_stmt|;
name|NodeBuilder
name|b1
init|=
name|store1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|b2
init|=
name|store2
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|childBuilder
argument_list|(
name|b1
argument_list|,
literal|"/test/book.jpg/jcr:content"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|merge
argument_list|(
name|store1
argument_list|,
name|b1
argument_list|)
expr_stmt|;
name|childBuilder
argument_list|(
name|b2
argument_list|,
literal|"/test/book.jpg/jcr:content"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
try|try
block|{
name|merge
argument_list|(
name|store2
argument_list|,
name|b2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must fail with CommitFailedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"ConflictException"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|addChildOnRemovedNode
parameter_list|()
throws|throws
name|Exception
block|{
comment|// do not perform retries on merge
name|store2
operator|.
name|setMaxBackOffMillis
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|store1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createFile
argument_list|(
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
argument_list|,
literal|"book.jpg"
argument_list|)
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
literal|"test"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|syncStores
argument_list|()
expr_stmt|;
name|NodeBuilder
name|b1
init|=
name|store1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|b2
init|=
name|store2
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|childBuilder
argument_list|(
name|b1
argument_list|,
literal|"/test/book.jpg/jcr:content"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|merge
argument_list|(
name|store1
argument_list|,
name|b1
argument_list|)
expr_stmt|;
name|childBuilder
argument_list|(
name|b2
argument_list|,
literal|"/test/book.jpg/jcr:content"
argument_list|)
operator|.
name|child
argument_list|(
literal|"extra"
argument_list|)
expr_stmt|;
try|try
block|{
name|merge
argument_list|(
name|store2
argument_list|,
name|b2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must fail with CommitFailedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"ConflictException"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeStaleTree
parameter_list|()
throws|throws
name|Exception
block|{
comment|// do not perform retries on merge
name|store2
operator|.
name|setMaxBackOffMillis
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|store1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createFile
argument_list|(
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
argument_list|,
literal|"book.jpg"
argument_list|)
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
literal|"test"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|syncStores
argument_list|()
expr_stmt|;
name|NodeBuilder
name|b1
init|=
name|store1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|b2
init|=
name|store2
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|childBuilder
argument_list|(
name|b1
argument_list|,
literal|"/test/book.jpg/jcr:content"
argument_list|)
operator|.
name|child
argument_list|(
literal|"extra"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store1
argument_list|,
name|b1
argument_list|)
expr_stmt|;
name|childBuilder
argument_list|(
name|b2
argument_list|,
literal|"/test/book.jpg/jcr:content"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
try|try
block|{
name|merge
argument_list|(
name|store2
argument_list|,
name|b2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must fail with CommitFailedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"ConflictException"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|merge
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|NodeBuilder
name|root
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|store
operator|.
name|merge
argument_list|(
name|root
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
specifier|private
name|NodeBuilder
name|createFile
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|childName
parameter_list|)
block|{
name|NodeBuilder
name|file
init|=
name|type
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|childName
argument_list|)
argument_list|,
name|NT_FILE
argument_list|)
decl_stmt|;
name|type
argument_list|(
name|file
operator|.
name|child
argument_list|(
literal|"jcr:content"
argument_list|)
argument_list|,
name|NT_RESOURCE
argument_list|)
expr_stmt|;
return|return
name|file
return|;
block|}
specifier|private
name|NodeBuilder
name|getRendBuilder
parameter_list|(
name|NodeBuilder
name|assetBuilder
parameter_list|)
block|{
return|return
name|assetBuilder
operator|.
name|getChildNode
argument_list|(
literal|"jcr:content"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"renditions"
argument_list|)
return|;
block|}
specifier|private
name|NodeBuilder
name|createAsset
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|childName
parameter_list|)
block|{
name|NodeBuilder
name|asset
init|=
name|type
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|childName
argument_list|)
argument_list|,
literal|"app:Asset"
argument_list|)
decl_stmt|;
name|NodeBuilder
name|assetJC
init|=
name|asset
operator|.
name|child
argument_list|(
literal|"jcr:content"
argument_list|)
decl_stmt|;
name|assetJC
operator|.
name|child
argument_list|(
literal|"metadata"
argument_list|)
expr_stmt|;
name|assetJC
operator|.
name|child
argument_list|(
literal|"renditions"
argument_list|)
operator|.
name|child
argument_list|(
literal|"rend-orig"
argument_list|)
expr_stmt|;
name|assetJC
operator|.
name|child
argument_list|(
literal|"comments"
argument_list|)
expr_stmt|;
return|return
name|asset
return|;
block|}
specifier|private
name|NodeBuilder
name|type
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|typeName
parameter_list|)
block|{
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
name|void
name|syncStores
parameter_list|()
block|{
name|store1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|store2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

