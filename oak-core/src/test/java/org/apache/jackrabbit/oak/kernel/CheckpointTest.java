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
name|kernel
package|;
end_package

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
import|import static
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|value
operator|=
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|CheckpointTest
block|{
annotation|@
name|Parameters
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|fixtures
parameter_list|()
block|{
name|Object
index|[]
index|[]
name|fixtures
init|=
operator|new
name|Object
index|[]
index|[]
block|{
block|{
name|NodeStoreFixture
operator|.
name|MONGO_NS
block|}
block|,
block|{
name|NodeStoreFixture
operator|.
name|SEGMENT_MK
block|}
block|,
block|{
name|NodeStoreFixture
operator|.
name|MEMORY_NS
block|}
block|,         }
decl_stmt|;
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|fixtures
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|NodeStoreFixture
name|fixture
decl_stmt|;
specifier|private
name|NodeStore
name|store
decl_stmt|;
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
name|this
operator|.
name|fixture
operator|=
name|fixture
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
name|store
operator|=
name|fixture
operator|.
name|createNodeStore
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
block|}
end_class

end_unit

