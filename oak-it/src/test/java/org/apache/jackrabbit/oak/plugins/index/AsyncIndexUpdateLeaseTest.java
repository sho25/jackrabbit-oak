begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|index
package|;
end_package

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
name|index
operator|.
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
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
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
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
name|index
operator|.
name|IndexUtils
operator|.
name|createIndexDefinition
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|OakBaseTest
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
name|fixture
operator|.
name|NodeStoreFixture
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
name|AsyncIndexUpdate
operator|.
name|AsyncIndexStats
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
name|AsyncIndexUpdate
operator|.
name|AsyncUpdateCallback
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
name|property
operator|.
name|PropertyIndexEditorProvider
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

begin_class
specifier|public
class|class
name|AsyncIndexUpdateLeaseTest
extends|extends
name|OakBaseTest
block|{
specifier|private
specifier|final
name|String
name|name
init|=
literal|"async"
decl_stmt|;
specifier|private
name|IndexEditorProvider
name|provider
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|executed
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
name|AsyncIndexUpdateLeaseTest
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|provider
operator|=
operator|new
name|PropertyIndexEditorProvider
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
name|createIndexDefinition
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"rootIndex"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|null
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"testRoot"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
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
name|executed
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
literal|"Test method was not executed"
argument_list|,
name|executed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|referenced
init|=
name|getReferenceCp
argument_list|(
name|store
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Reference checkpoint doesn't exist"
argument_list|,
name|referenced
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Failed indexer must not clean successful indexer's checkpoint"
argument_list|,
name|store
operator|.
name|retrieve
argument_list|(
name|referenced
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPrePrepare
parameter_list|()
throws|throws
name|Exception
block|{
comment|// take care of initial reindex before
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
specifier|final
name|IndexStatusListener
name|l1
init|=
operator|new
name|IndexStatusListener
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|prePrepare
parameter_list|()
block|{
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertRunOk
argument_list|(
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|assertRunKo
argument_list|(
operator|new
name|SpecialAsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|,
name|l1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPostPrepare
parameter_list|()
block|{
comment|// take care of initial reindex before
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
specifier|final
name|IndexStatusListener
name|l1
init|=
operator|new
name|IndexStatusListener
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|postPrepare
parameter_list|()
block|{
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// lease must prevent this run
name|assertRunKo
argument_list|(
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|assertRunOk
argument_list|(
operator|new
name|SpecialAsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|,
name|l1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPreIndexUpdate
parameter_list|()
throws|throws
name|Exception
block|{
comment|// take care of initial reindex before
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
name|testContent
argument_list|(
name|store
argument_list|)
expr_stmt|;
specifier|final
name|IndexStatusListener
name|l1
init|=
operator|new
name|IndexStatusListener
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|preIndexUpdate
parameter_list|()
block|{
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertRunKo
argument_list|(
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|assertRunOk
argument_list|(
operator|new
name|SpecialAsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|,
name|l1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPostIndexUpdate
parameter_list|()
throws|throws
name|Exception
block|{
comment|// take care of initial reindex before
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
name|testContent
argument_list|(
name|store
argument_list|)
expr_stmt|;
specifier|final
name|IndexStatusListener
name|l1
init|=
operator|new
name|IndexStatusListener
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|postIndexUpdate
parameter_list|()
block|{
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertRunKo
argument_list|(
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|assertRunOk
argument_list|(
operator|new
name|SpecialAsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|,
name|l1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPreClose
parameter_list|()
throws|throws
name|Exception
block|{
comment|// take care of initial reindex before
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
name|testContent
argument_list|(
name|store
argument_list|)
expr_stmt|;
specifier|final
name|IndexStatusListener
name|l1
init|=
operator|new
name|IndexStatusListener
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|preClose
parameter_list|()
block|{
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertRunKo
argument_list|(
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|assertRunOk
argument_list|(
operator|new
name|SpecialAsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|,
name|l1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPostPrepareLeaseExpired
parameter_list|()
throws|throws
name|Exception
block|{
comment|// take care of initial reindex before
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
specifier|final
name|long
name|lease
init|=
literal|50
decl_stmt|;
specifier|final
name|IndexStatusListener
name|l1
init|=
operator|new
name|IndexStatusListener
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|postPrepare
parameter_list|()
block|{
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|lease
operator|*
literal|3
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//
block|}
name|assertRunOk
argument_list|(
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|assertRunKo
argument_list|(
operator|new
name|SpecialAsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|,
name|l1
argument_list|)
operator|.
name|setLeaseTimeOut
argument_list|(
name|lease
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPreIndexUpdateLeaseExpired
parameter_list|()
throws|throws
name|Exception
block|{
comment|// take care of initial reindex before
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
comment|// add extra indexed content
name|testContent
argument_list|(
name|store
argument_list|)
expr_stmt|;
specifier|final
name|long
name|lease
init|=
literal|50
decl_stmt|;
specifier|final
name|IndexStatusListener
name|l1
init|=
operator|new
name|IndexStatusListener
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|preIndexUpdate
parameter_list|()
block|{
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|lease
operator|*
literal|3
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//
block|}
name|assertRunOk
argument_list|(
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|assertRunKo
argument_list|(
operator|new
name|SpecialAsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|,
name|l1
argument_list|)
operator|.
name|setLeaseTimeOut
argument_list|(
name|lease
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPostIndexUpdateLeaseExpired
parameter_list|()
throws|throws
name|Exception
block|{
comment|// take care of initial reindex before
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
comment|// add extra indexed content
name|testContent
argument_list|(
name|store
argument_list|)
expr_stmt|;
specifier|final
name|long
name|lease
init|=
literal|50
decl_stmt|;
specifier|final
name|IndexStatusListener
name|l1
init|=
operator|new
name|IndexStatusListener
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|postIndexUpdate
parameter_list|()
block|{
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|lease
operator|*
literal|3
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//
block|}
name|assertRunOk
argument_list|(
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|assertRunKo
argument_list|(
operator|new
name|SpecialAsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|,
name|l1
argument_list|)
operator|.
name|setLeaseTimeOut
argument_list|(
name|lease
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPrePrepareRexindex
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|IndexStatusListener
name|l1
init|=
operator|new
name|IndexStatusListener
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|prePrepare
parameter_list|()
block|{
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertRunOk
argument_list|(
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|assertRunOk
argument_list|(
operator|new
name|SpecialAsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|,
name|l1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPostPrepareReindex
parameter_list|()
block|{
specifier|final
name|IndexStatusListener
name|l1
init|=
operator|new
name|IndexStatusListener
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|postPrepare
parameter_list|()
block|{
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// lease must prevent this run
name|assertRunKo
argument_list|(
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|assertRunOk
argument_list|(
operator|new
name|SpecialAsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|,
name|l1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPreIndexUpdateReindex
parameter_list|()
throws|throws
name|Exception
block|{
name|testContent
argument_list|(
name|store
argument_list|)
expr_stmt|;
specifier|final
name|IndexStatusListener
name|l1
init|=
operator|new
name|IndexStatusListener
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|preIndexUpdate
parameter_list|()
block|{
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertRunKo
argument_list|(
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|assertRunOk
argument_list|(
operator|new
name|SpecialAsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|,
name|l1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPostIndexUpdateReindex
parameter_list|()
throws|throws
name|Exception
block|{
name|testContent
argument_list|(
name|store
argument_list|)
expr_stmt|;
specifier|final
name|IndexStatusListener
name|l1
init|=
operator|new
name|IndexStatusListener
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|postIndexUpdate
parameter_list|()
block|{
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertRunKo
argument_list|(
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|assertRunOk
argument_list|(
operator|new
name|SpecialAsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|,
name|l1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPostPrepareReindexLeaseExpired
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|long
name|lease
init|=
literal|50
decl_stmt|;
specifier|final
name|IndexStatusListener
name|l1
init|=
operator|new
name|IndexStatusListener
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|postPrepare
parameter_list|()
block|{
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|lease
operator|*
literal|3
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//
block|}
name|assertRunOk
argument_list|(
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|assertRunKo
argument_list|(
operator|new
name|SpecialAsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|,
name|l1
argument_list|)
operator|.
name|setLeaseTimeOut
argument_list|(
name|lease
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPreIndexUpdateReindexLeaseExpired
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|long
name|lease
init|=
literal|50
decl_stmt|;
specifier|final
name|IndexStatusListener
name|l1
init|=
operator|new
name|IndexStatusListener
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|preIndexUpdate
parameter_list|()
block|{
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|lease
operator|*
literal|3
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//
block|}
name|assertRunOk
argument_list|(
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|assertRunKo
argument_list|(
operator|new
name|SpecialAsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|,
name|l1
argument_list|)
operator|.
name|setLeaseTimeOut
argument_list|(
name|lease
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPostIndexUpdateReindexLeaseExpired
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|long
name|lease
init|=
literal|50
decl_stmt|;
specifier|final
name|IndexStatusListener
name|l1
init|=
operator|new
name|IndexStatusListener
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|postIndexUpdate
parameter_list|()
block|{
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|lease
operator|*
literal|3
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//
block|}
name|assertRunOk
argument_list|(
operator|new
name|AsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|assertRunKo
argument_list|(
operator|new
name|SpecialAsyncIndexUpdate
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|,
name|l1
argument_list|)
operator|.
name|setLeaseTimeOut
argument_list|(
name|lease
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// -------------------------------------------------------------------
specifier|private
specifier|static
name|String
name|getReferenceCp
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|AsyncIndexUpdate
operator|.
name|ASYNC
argument_list|)
operator|.
name|getString
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|private
name|void
name|assertRunOk
parameter_list|(
name|AsyncIndexUpdate
name|a
parameter_list|)
block|{
name|assertRun
argument_list|(
name|a
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertRunKo
parameter_list|(
name|AsyncIndexUpdate
name|a
parameter_list|)
block|{
name|assertRun
argument_list|(
name|a
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertConcurrentUpdate
argument_list|(
name|a
operator|.
name|getIndexStats
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertRun
parameter_list|(
name|AsyncIndexUpdate
name|a
parameter_list|,
name|boolean
name|failing
parameter_list|)
block|{
name|a
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected failiure flag"
argument_list|,
name|failing
argument_list|,
name|a
operator|.
name|isFailing
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertConcurrentUpdate
parameter_list|(
name|AsyncIndexStats
name|stats
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Error must be of type 'Concurrent update'"
argument_list|,
name|stats
operator|.
name|getLatestError
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Concurrent update detected"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|testContent
parameter_list|(
name|NodeStore
name|store
parameter_list|)
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
name|builder
operator|.
name|child
argument_list|(
literal|"testRoot"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc "
operator|+
name|System
operator|.
name|currentTimeMillis
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
specifier|private
specifier|static
class|class
name|SpecialAsyncIndexUpdate
extends|extends
name|AsyncIndexUpdate
block|{
specifier|private
specifier|final
name|IndexStatusListener
name|listener
decl_stmt|;
specifier|public
name|SpecialAsyncIndexUpdate
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeStore
name|store
parameter_list|,
name|IndexEditorProvider
name|provider
parameter_list|,
name|IndexStatusListener
name|listener
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|run
parameter_list|()
block|{
name|super
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|AsyncUpdateCallback
name|newAsyncUpdateCallback
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|String
name|name
parameter_list|,
name|long
name|leaseTimeOut
parameter_list|,
name|String
name|checkpoint
parameter_list|,
name|AsyncIndexStats
name|indexStats
parameter_list|,
name|AtomicBoolean
name|stopFlag
parameter_list|)
block|{
return|return
operator|new
name|SpecialAsyncUpdateCallback
argument_list|(
name|store
argument_list|,
name|name
argument_list|,
name|leaseTimeOut
argument_list|,
name|checkpoint
argument_list|,
name|indexStats
argument_list|,
name|stopFlag
argument_list|,
name|listener
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|SpecialAsyncUpdateCallback
extends|extends
name|AsyncUpdateCallback
block|{
specifier|private
name|IndexStatusListener
name|listener
decl_stmt|;
specifier|public
name|SpecialAsyncUpdateCallback
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|String
name|name
parameter_list|,
name|long
name|leaseTimeOut
parameter_list|,
name|String
name|checkpoint
parameter_list|,
name|AsyncIndexStats
name|indexStats
parameter_list|,
name|AtomicBoolean
name|stopFlag
parameter_list|,
name|IndexStatusListener
name|listener
parameter_list|)
block|{
name|super
argument_list|(
name|store
argument_list|,
name|name
argument_list|,
name|leaseTimeOut
argument_list|,
name|checkpoint
argument_list|,
name|indexStats
argument_list|,
name|stopFlag
argument_list|)
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|prepare
parameter_list|(
name|String
name|afterCheckpoint
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|listener
operator|.
name|prePrepare
argument_list|()
expr_stmt|;
name|super
operator|.
name|prepare
argument_list|(
name|afterCheckpoint
argument_list|)
expr_stmt|;
name|listener
operator|.
name|postPrepare
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|indexUpdate
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|listener
operator|.
name|preIndexUpdate
argument_list|()
expr_stmt|;
name|super
operator|.
name|indexUpdate
argument_list|()
expr_stmt|;
name|listener
operator|.
name|postIndexUpdate
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|close
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|listener
operator|.
name|preClose
argument_list|()
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|listener
operator|.
name|postClose
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|abstract
specifier|static
class|class
name|IndexStatusListener
block|{
specifier|protected
name|void
name|prePrepare
parameter_list|()
block|{         }
specifier|protected
name|void
name|postPrepare
parameter_list|()
block|{         }
specifier|protected
name|void
name|preIndexUpdate
parameter_list|()
block|{         }
specifier|protected
name|void
name|postIndexUpdate
parameter_list|()
block|{         }
specifier|protected
name|void
name|preClose
parameter_list|()
block|{         }
specifier|protected
name|void
name|postClose
parameter_list|()
block|{         }
block|}
block|}
end_class

end_unit

