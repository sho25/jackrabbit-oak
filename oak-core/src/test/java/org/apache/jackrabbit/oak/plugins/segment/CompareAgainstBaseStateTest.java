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
name|segment
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
name|easymock
operator|.
name|EasyMock
operator|.
name|createMock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|expect
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|replay
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|verify
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
name|NodeStateDiff
import|;
end_import

begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|EasyMock
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

begin_comment
comment|/**  * Test case for ensuring that segment size remains within bounds.  */
end_comment

begin_class
specifier|public
class|class
name|CompareAgainstBaseStateTest
block|{
specifier|private
specifier|final
name|SegmentStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SegmentWriter
name|writer
init|=
operator|new
name|SegmentWriter
argument_list|(
name|store
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeStateDiff
name|diff
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|NodeStateDiff
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"bar"
argument_list|,
literal|123
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"baz"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSameState
parameter_list|()
block|{
name|NodeState
name|node
init|=
name|persist
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|node
operator|.
name|compareAgainstBaseState
argument_list|(
name|node
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEqualState
parameter_list|()
block|{
name|NodeState
name|before
init|=
name|persist
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|NodeState
name|after
init|=
name|persist
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertyAdded
parameter_list|()
block|{
name|NodeState
name|before
init|=
name|persist
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|persist
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|diff
operator|.
name|propertyAdded
argument_list|(
name|after
operator|.
name|getProperty
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertyChanged
parameter_list|()
block|{
name|NodeState
name|before
init|=
name|persist
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|persist
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|diff
operator|.
name|propertyChanged
argument_list|(
name|before
operator|.
name|getProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|after
operator|.
name|getProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertyDeleted
parameter_list|()
block|{
name|NodeState
name|before
init|=
name|persist
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|builder
operator|.
name|removeProperty
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|persist
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|diff
operator|.
name|propertyDeleted
argument_list|(
name|before
operator|.
name|getProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChildNodeAdded
parameter_list|()
block|{
name|NodeState
name|before
init|=
name|persist
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|persist
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|diff
operator|.
name|childNodeAdded
argument_list|(
literal|"test"
argument_list|,
name|after
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChildNodeChanged
parameter_list|()
block|{
name|NodeState
name|before
init|=
name|persist
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"baz"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|persist
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|diff
operator|.
name|childNodeChanged
argument_list|(
literal|"baz"
argument_list|,
name|before
operator|.
name|getChildNode
argument_list|(
literal|"baz"
argument_list|)
argument_list|,
name|after
operator|.
name|getChildNode
argument_list|(
literal|"baz"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChildNodeDeleted
parameter_list|()
block|{
name|NodeState
name|before
init|=
name|persist
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|builder
operator|.
name|removeChildNode
argument_list|(
literal|"baz"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|persist
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|diff
operator|.
name|childNodeDeleted
argument_list|(
literal|"baz"
argument_list|,
name|before
operator|.
name|getChildNode
argument_list|(
literal|"baz"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeState
name|persist
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|NodeState
name|state
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|state
return|;
block|}
block|}
end_class

end_unit

