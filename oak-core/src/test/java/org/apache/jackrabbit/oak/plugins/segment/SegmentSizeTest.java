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
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
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
name|MemoryNodeState
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
name|SegmentSizeTest
block|{
specifier|private
name|SegmentStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
specifier|private
name|SegmentWriter
name|writer
init|=
operator|new
name|SegmentWriter
argument_list|(
name|store
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testNodeSize
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|MemoryNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|assertNodeSize
argument_list|(
literal|16
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|=
name|MemoryNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|assertNodeSize
argument_list|(
literal|70
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|=
name|MemoryNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"baz"
argument_list|,
literal|123
argument_list|)
expr_stmt|;
name|assertNodeSize
argument_list|(
literal|124
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|=
name|MemoryNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|assertNodeSize
argument_list|(
literal|59
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|=
name|MemoryNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|assertNodeSize
argument_list|(
literal|102
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertNodeSize
parameter_list|(
name|int
name|expected
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|RecordId
name|id
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|id
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|segment
operator|.
name|getData
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

