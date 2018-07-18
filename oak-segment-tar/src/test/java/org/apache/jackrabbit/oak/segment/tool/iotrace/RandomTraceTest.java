begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
name|segment
operator|.
name|tool
operator|.
name|iotrace
package|;
end_package

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
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
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
name|commons
operator|.
name|PathUtils
operator|.
name|elements
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
name|assertTrue
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|RandomTraceTest
block|{
annotation|@
name|NotNull
specifier|private
specifier|static
name|NodeState
name|createTree
parameter_list|(
annotation|@
name|NotNull
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
name|NodeBuilder
name|root
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|createNode
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|root
operator|.
name|getNodeState
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|void
name|createNode
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|root
parameter_list|,
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
block|{
name|NodeBuilder
name|child
init|=
name|root
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|child
operator|=
name|child
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTraverseEmptyTree
parameter_list|()
block|{
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|trace
init|=
name|newArrayList
argument_list|()
decl_stmt|;
operator|new
name|RandomAccessTrace
argument_list|(
name|emptyList
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
name|trace
operator|::
name|add
argument_list|)
operator|.
name|run
argument_list|(
name|createTree
argument_list|(
name|emptyList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|trace
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTraverseNonExistingPath
parameter_list|()
block|{
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|trace
init|=
name|newArrayList
argument_list|()
decl_stmt|;
operator|new
name|RandomAccessTrace
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/not/here"
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|trace
operator|::
name|add
argument_list|)
operator|.
name|run
argument_list|(
name|createTree
argument_list|(
name|emptyList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|trace
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/not/here"
argument_list|)
argument_list|,
name|trace
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTraverse
parameter_list|()
block|{
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|trace
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a/b/c"
argument_list|,
literal|"/d/e/f"
argument_list|)
decl_stmt|;
operator|new
name|RandomAccessTrace
argument_list|(
name|paths
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
name|trace
operator|::
name|add
argument_list|)
operator|.
name|run
argument_list|(
name|createTree
argument_list|(
name|paths
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|trace
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|paths
operator|.
name|contains
argument_list|(
name|trace
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|paths
operator|.
name|contains
argument_list|(
name|trace
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

