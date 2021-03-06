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
package|;
end_package

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
name|json
operator|.
name|JsopDiff
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
name|Ignore
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
import|import
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
name|DocumentNodeStoreBuilder
operator|.
name|UPDATE_LIMIT
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
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|DocumentNodeBuilderTest
block|{
specifier|private
specifier|final
name|NodeStoreFixture
name|fixture
decl_stmt|;
specifier|private
name|NodeStore
name|ns
decl_stmt|;
specifier|public
name|DocumentNodeBuilderTest
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
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
operator|new
name|MemoryFixture
argument_list|()
block|}
block|,
block|{
operator|new
name|DocumentMemoryFixture
argument_list|()
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|ns
operator|=
name|fixture
operator|.
name|createNodeStore
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
name|fixture
operator|.
name|dispose
argument_list|(
name|ns
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getBaseState
parameter_list|()
block|{
name|NodeState
name|state
init|=
name|ns
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|state
operator|.
name|builder
argument_list|()
decl_stmt|;
name|assertStateEquals
argument_list|(
name|state
argument_list|,
name|builder
operator|.
name|getBaseState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getBaseStateFromModifiedBuilder
parameter_list|()
block|{
name|NodeState
name|state
init|=
name|ns
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|state
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|assertStateEquals
argument_list|(
name|state
argument_list|,
name|builder
operator|.
name|getBaseState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getBaseStateFromModifiedBuilderBranched
parameter_list|()
block|{
name|NodeState
name|state
init|=
name|ns
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|state
operator|.
name|builder
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
operator|<
name|UPDATE_LIMIT
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"c-"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
block|}
name|assertStateEquals
argument_list|(
name|state
argument_list|,
name|builder
operator|.
name|getBaseState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getBaseStateFromBuilderFromStateFromModifiedBuilder
parameter_list|()
block|{
name|NodeState
name|state
init|=
name|ns
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|state
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|NodeState
name|modifiedState
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|assertStateEquals
argument_list|(
name|modifiedState
argument_list|,
name|modifiedState
operator|.
name|builder
argument_list|()
operator|.
name|getBaseState
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertStateEquals
parameter_list|(
name|NodeState
name|one
parameter_list|,
name|NodeState
name|two
parameter_list|)
block|{
name|String
name|changesOnTwo
init|=
name|JsopDiff
operator|.
name|diffToJsop
argument_list|(
name|one
argument_list|,
name|two
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Node states are not equal: "
operator|+
name|changesOnTwo
argument_list|,
name|EqualsDiff
operator|.
name|equals
argument_list|(
name|one
argument_list|,
name|two
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

