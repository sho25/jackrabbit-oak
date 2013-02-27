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
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Type
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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
name|util
operator|.
name|ISO8601
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
name|ImmutableList
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
annotation|@
name|Test
comment|// TODO: Fix cross-segment amortization code
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
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|getSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|getAmortizedSize
argument_list|(
name|builder
argument_list|)
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
name|assertEquals
argument_list|(
literal|24
argument_list|,
name|getSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|24
argument_list|,
name|getAmortizedSize
argument_list|(
name|builder
argument_list|)
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
name|assertEquals
argument_list|(
literal|40
argument_list|,
name|getSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|40
argument_list|,
name|getAmortizedSize
argument_list|(
name|builder
argument_list|)
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
name|assertEquals
argument_list|(
literal|28
argument_list|,
name|getSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|28
argument_list|,
name|getAmortizedSize
argument_list|(
name|builder
argument_list|)
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
name|assertEquals
argument_list|(
literal|56
argument_list|,
name|getSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|56
argument_list|,
name|getAmortizedSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDuplicateStrings
parameter_list|()
block|{
name|String
name|string
init|=
literal|"More than just a few bytes of example content."
decl_stmt|;
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
name|builder
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"test"
argument_list|,
name|Collections
operator|.
name|nCopies
argument_list|(
literal|1
argument_list|,
name|string
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|base
init|=
name|getSize
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"test"
argument_list|,
name|Collections
operator|.
name|nCopies
argument_list|(
literal|12
argument_list|,
name|string
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|base
operator|+
literal|12
operator|*
name|Segment
operator|.
name|RECORD_ID_BYTES
argument_list|,
name|getSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"test"
argument_list|,
name|Collections
operator|.
name|nCopies
argument_list|(
literal|100
argument_list|,
name|string
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|base
operator|+
literal|100
operator|*
name|Segment
operator|.
name|RECORD_ID_BYTES
argument_list|,
name|getSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDuplicateDates
parameter_list|()
block|{
name|String
name|now
init|=
name|ISO8601
operator|.
name|format
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
decl_stmt|;
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
name|builder
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"test"
argument_list|,
name|Collections
operator|.
name|nCopies
argument_list|(
literal|1
argument_list|,
name|now
argument_list|)
argument_list|,
name|Type
operator|.
name|DATES
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|base
init|=
name|getSize
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"test"
argument_list|,
name|Collections
operator|.
name|nCopies
argument_list|(
literal|12
argument_list|,
name|now
argument_list|)
argument_list|,
name|Type
operator|.
name|DATES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|base
operator|+
literal|12
operator|*
name|Segment
operator|.
name|RECORD_ID_BYTES
argument_list|,
name|getSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"test"
argument_list|,
name|Collections
operator|.
name|nCopies
argument_list|(
literal|100
argument_list|,
name|now
argument_list|)
argument_list|,
name|Type
operator|.
name|DATES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|base
operator|+
literal|100
operator|*
name|Segment
operator|.
name|RECORD_ID_BYTES
argument_list|,
name|getSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAccessControlNodes
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
name|builder
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"rep:ACL"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|getSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|getAmortizedSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|NodeBuilder
name|deny
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"deny"
argument_list|)
decl_stmt|;
name|deny
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"rep:DenyACE"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|deny
operator|.
name|setProperty
argument_list|(
literal|"rep:principalName"
argument_list|,
literal|"everyone"
argument_list|)
expr_stmt|;
name|deny
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"rep:privileges"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"jcr:read"
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|144
argument_list|,
name|getSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|144
argument_list|,
name|getAmortizedSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|NodeBuilder
name|allow
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"allow"
argument_list|)
decl_stmt|;
name|allow
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"rep:GrantACE"
argument_list|)
expr_stmt|;
name|allow
operator|.
name|setProperty
argument_list|(
literal|"rep:principalName"
argument_list|,
literal|"administrators"
argument_list|)
expr_stmt|;
name|allow
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"rep:privileges"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"jcr:all"
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|264
argument_list|,
name|getSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|264
argument_list|,
name|getAmortizedSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|NodeBuilder
name|deny0
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"deny0"
argument_list|)
decl_stmt|;
name|deny0
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"rep:DenyACE"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|deny0
operator|.
name|setProperty
argument_list|(
literal|"rep:principalName"
argument_list|,
literal|"everyone"
argument_list|)
expr_stmt|;
name|deny0
operator|.
name|setProperty
argument_list|(
literal|"rep:glob"
argument_list|,
literal|"*/activities/*"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"rep:privileges"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"jcr:read"
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|356
argument_list|,
name|getSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|356
argument_list|,
name|getAmortizedSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|NodeBuilder
name|allow0
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"allow0"
argument_list|)
decl_stmt|;
name|allow0
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"rep:GrantACE"
argument_list|)
expr_stmt|;
name|allow0
operator|.
name|setProperty
argument_list|(
literal|"rep:principalName"
argument_list|,
literal|"user-administrators"
argument_list|)
expr_stmt|;
name|allow0
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"rep:privileges"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"jcr:all"
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|412
argument_list|,
name|getSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|412
argument_list|,
name|getAmortizedSize
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFlatNodeUpdate
parameter_list|()
block|{
name|SegmentStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
name|SegmentReader
name|reader
init|=
operator|new
name|SegmentReader
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|SegmentWriter
name|writer
init|=
operator|new
name|SegmentWriter
argument_list|(
name|store
argument_list|,
name|reader
argument_list|)
decl_stmt|;
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"child"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|SegmentNodeState
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
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|state
operator|.
name|getRecordId
argument_list|()
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|26788
argument_list|,
name|segment
operator|.
name|getData
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|builder
operator|=
name|state
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"child1000"
argument_list|)
expr_stmt|;
name|state
operator|=
name|writer
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|segment
operator|=
name|store
operator|.
name|readSegment
argument_list|(
name|state
operator|.
name|getRecordId
argument_list|()
operator|.
name|getSegmentId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|260
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
specifier|private
name|int
name|getSize
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|SegmentStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
name|SegmentWriter
name|writer
init|=
operator|new
name|SegmentWriter
argument_list|(
name|store
argument_list|,
operator|new
name|SegmentReader
argument_list|(
name|store
argument_list|)
argument_list|)
decl_stmt|;
name|RecordId
name|id
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
operator|.
name|getRecordId
argument_list|()
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
return|return
name|segment
operator|.
name|getData
argument_list|()
operator|.
name|length
return|;
block|}
specifier|private
name|int
name|getAmortizedSize
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|SegmentStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
name|SegmentWriter
name|writer
init|=
operator|new
name|SegmentWriter
argument_list|(
name|store
argument_list|,
operator|new
name|SegmentReader
argument_list|(
name|store
argument_list|)
argument_list|)
decl_stmt|;
name|NodeState
name|state
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|writer
operator|.
name|writeNode
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|RecordId
name|id
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|state
argument_list|)
operator|.
name|getRecordId
argument_list|()
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
return|return
name|segment
operator|.
name|getData
argument_list|()
operator|.
name|length
return|;
block|}
block|}
end_class

end_unit

