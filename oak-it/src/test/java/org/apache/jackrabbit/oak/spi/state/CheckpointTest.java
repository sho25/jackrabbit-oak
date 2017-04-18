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
name|spi
operator|.
name|state
package|;
end_package

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
name|AtomicInteger
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|commit
operator|.
name|Observable
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
name|Observer
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

begin_class
specifier|public
class|class
name|CheckpointTest
extends|extends
name|OakBaseTest
block|{
specifier|private
name|NodeState
name|root
decl_stmt|;
specifier|public
name|CheckpointTest
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
name|setUp
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
name|test
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|test
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|test
operator|.
name|setProperty
argument_list|(
literal|"b"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|test
operator|.
name|setProperty
argument_list|(
literal|"c"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|test
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|test
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|test
operator|.
name|child
argument_list|(
literal|"z"
argument_list|)
expr_stmt|;
name|root
operator|=
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
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|fixture
operator|.
name|dispose
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|checkpoint
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|String
name|cp
init|=
name|store
operator|.
name|checkpoint
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
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
name|setChildNode
argument_list|(
literal|"new"
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
name|assertFalse
argument_list|(
name|root
operator|.
name|equals
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|root
argument_list|,
name|store
operator|.
name|retrieve
argument_list|(
name|cp
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|release
argument_list|(
name|cp
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|store
operator|.
name|retrieve
argument_list|(
name|cp
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|checkpointInfo
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"one"
argument_list|,
literal|"1"
argument_list|,
literal|"two"
argument_list|,
literal|"2"
argument_list|,
literal|"three"
argument_list|,
literal|"2"
argument_list|)
decl_stmt|;
name|String
name|cp
init|=
name|store
operator|.
name|checkpoint
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|props
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|props
argument_list|,
name|store
operator|.
name|checkpointInfo
argument_list|(
name|cp
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noContentChangeForCheckpoints
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicInteger
name|invocationCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
operator|(
operator|(
name|Observable
operator|)
name|store
operator|)
operator|.
name|addObserver
argument_list|(
operator|new
name|Observer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|contentChanged
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|,
annotation|@
name|Nonnull
name|CommitInfo
name|info
parameter_list|)
block|{
name|invocationCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|invocationCount
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|String
name|cp
init|=
name|store
operator|.
name|checkpoint
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|invocationCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|release
argument_list|(
name|cp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|invocationCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|retrieveAny
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|store
operator|.
name|retrieve
argument_list|(
literal|"r42-0-0"
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

