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
name|index
operator|.
name|lucene
operator|.
name|property
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
name|Collection
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
name|Random
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
name|AtomicInteger
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
name|TreeTraverser
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
name|FixturesHelper
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
name|DocumentMemoryFixture
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
name|MemoryFixture
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
name|lucene
operator|.
name|TestUtil
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
name|ChildNodeEntry
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|RecursiveDeleteTest
block|{
specifier|private
specifier|final
name|NodeStoreFixture
name|fixture
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|nodeStore
decl_stmt|;
specifier|private
name|String
name|testNodePath
init|=
literal|"/content/testNode"
decl_stmt|;
specifier|private
name|Random
name|rnd
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
name|int
name|maxBucketSize
init|=
literal|100
decl_stmt|;
specifier|private
name|int
name|maxDepth
init|=
literal|4
decl_stmt|;
specifier|public
name|RecursiveDeleteTest
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|fixture
operator|.
name|createNodeStore
argument_list|()
expr_stmt|;
name|this
operator|.
name|fixture
operator|=
name|fixture
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|fixture
operator|.
name|dispose
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
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
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|MemoryFixture
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|DocumentMemoryFixture
argument_list|()
block|}
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|recursiveDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|actualCount
init|=
name|createSubtree
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|actualCount
argument_list|,
name|getSubtreeCount
argument_list|(
name|getNode
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
name|testNodePath
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|RecursiveDelete
name|rd
init|=
operator|new
name|RecursiveDelete
argument_list|(
name|nodeStore
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
parameter_list|()
lambda|->
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|rd
operator|.
name|setBatchSize
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|rd
operator|.
name|run
argument_list|(
name|testNodePath
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|actualCount
argument_list|,
name|rd
operator|.
name|getNumRemoved
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|getNode
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
name|testNodePath
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|rd
operator|.
name|getMergeCount
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|actualCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|multiplePaths
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|count
init|=
literal|121
decl_stmt|;
name|NodeBuilder
name|nb
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|nb
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|nb
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c"
operator|+
name|i
argument_list|)
expr_stmt|;
name|nb
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|nodeStore
operator|.
name|merge
argument_list|(
name|nb
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
name|RecursiveDelete
name|rd
init|=
operator|new
name|RecursiveDelete
argument_list|(
name|nodeStore
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
parameter_list|()
lambda|->
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|rd
operator|.
name|setBatchSize
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|rd
operator|.
name|run
argument_list|(
name|asList
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|rd
operator|.
name|getMergeCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
operator|*
name|count
operator|+
literal|2
argument_list|,
name|rd
operator|.
name|getNumRemoved
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|getNode
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"/a"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|getNode
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"/b"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getNode
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"/c"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|createSubtree
parameter_list|(
name|int
name|maxNodesCount
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|child
init|=
name|TestUtil
operator|.
name|child
argument_list|(
name|builder
argument_list|,
name|testNodePath
argument_list|)
decl_stmt|;
name|AtomicInteger
name|maxNodes
init|=
operator|new
name|AtomicInteger
argument_list|(
name|maxNodesCount
argument_list|)
decl_stmt|;
name|int
name|actualCount
init|=
name|createChildren
argument_list|(
name|child
argument_list|,
name|maxNodes
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|nodeStore
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
return|return
name|actualCount
operator|+
literal|1
return|;
block|}
specifier|private
name|int
name|createChildren
parameter_list|(
name|NodeBuilder
name|child
parameter_list|,
name|AtomicInteger
name|maxNodes
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
if|if
condition|(
name|maxNodes
operator|.
name|get
argument_list|()
operator|<=
literal|0
operator|||
name|depth
operator|>
name|maxDepth
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|totalCount
init|=
literal|0
decl_stmt|;
name|int
name|childCount
init|=
name|rnd
operator|.
name|nextInt
argument_list|(
name|maxBucketSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|childCount
operator|==
literal|0
condition|)
block|{
name|childCount
operator|=
literal|1
expr_stmt|;
block|}
name|List
argument_list|<
name|NodeBuilder
argument_list|>
name|children
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
argument_list|<
name|childCount
operator|&&
name|maxNodes
operator|.
name|get
operator|(
operator|)
argument_list|>
literal|0
condition|;
name|i
operator|++
control|)
block|{
name|maxNodes
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
name|totalCount
operator|++
expr_stmt|;
name|children
operator|.
name|add
argument_list|(
name|child
operator|.
name|child
argument_list|(
literal|"c"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|NodeBuilder
name|c
range|:
name|children
control|)
block|{
name|totalCount
operator|+=
name|createChildren
argument_list|(
name|c
argument_list|,
name|maxNodes
argument_list|,
name|depth
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|totalCount
return|;
block|}
specifier|private
name|int
name|getSubtreeCount
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|TreeTraverser
argument_list|<
name|NodeState
argument_list|>
name|t
init|=
operator|new
name|TreeTraverser
argument_list|<
name|NodeState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|NodeState
argument_list|>
name|children
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|root
operator|.
name|getChildNodeEntries
argument_list|()
argument_list|,
name|ChildNodeEntry
operator|::
name|getNodeState
argument_list|)
return|;
block|}
block|}
decl_stmt|;
return|return
name|t
operator|.
name|preOrderTraversal
argument_list|(
name|state
argument_list|)
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

