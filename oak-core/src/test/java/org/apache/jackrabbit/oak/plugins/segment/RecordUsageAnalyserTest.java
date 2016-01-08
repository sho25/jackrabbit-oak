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
name|segment
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
name|base
operator|.
name|Strings
operator|.
name|repeat
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
name|nCopies
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
name|api
operator|.
name|Type
operator|.
name|LONGS
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
name|api
operator|.
name|Type
operator|.
name|NAME
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
name|api
operator|.
name|Type
operator|.
name|NAMES
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
name|api
operator|.
name|Type
operator|.
name|STRINGS
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|segment
operator|.
name|ListRecord
operator|.
name|LEVEL_SIZE
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
name|segment
operator|.
name|Segment
operator|.
name|MEDIUM_LIMIT
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
name|segment
operator|.
name|Segment
operator|.
name|SMALL_LIMIT
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
name|segment
operator|.
name|SegmentVersion
operator|.
name|V_10
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
name|segment
operator|.
name|SegmentVersion
operator|.
name|V_11
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
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|api
operator|.
name|Blob
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
name|ArrayBasedBlob
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
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|RecordUsageAnalyserTest
block|{
specifier|private
specifier|final
name|SegmentVersion
name|segmentVersion
decl_stmt|;
specifier|private
name|SegmentStore
name|store
decl_stmt|;
specifier|private
name|SegmentWriter
name|writer
decl_stmt|;
specifier|private
name|RecordUsageAnalyser
name|analyser
init|=
operator|new
name|RecordUsageAnalyser
argument_list|()
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
specifier|public
specifier|static
name|List
argument_list|<
name|SegmentVersion
index|[]
argument_list|>
name|fixtures
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|SegmentVersion
index|[]
block|{
name|V_10
block|}
argument_list|,
operator|new
name|SegmentVersion
index|[]
block|{
name|V_11
block|}
argument_list|)
return|;
block|}
specifier|public
name|RecordUsageAnalyserTest
parameter_list|(
name|SegmentVersion
name|segmentVersion
parameter_list|)
block|{
name|this
operator|.
name|segmentVersion
operator|=
name|segmentVersion
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|store
operator|=
name|mock
argument_list|(
name|SegmentStore
operator|.
name|class
argument_list|)
expr_stmt|;
name|SegmentTracker
name|tracker
init|=
operator|new
name|SegmentTracker
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|store
operator|.
name|getTracker
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|tracker
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|SegmentWriter
argument_list|(
name|store
argument_list|,
name|segmentVersion
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|analyser
operator|=
operator|new
name|RecordUsageAnalyser
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|emptyNode
parameter_list|()
throws|throws
name|IOException
block|{
name|SegmentNodeState
name|node
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|EMPTY_NODE
argument_list|)
decl_stmt|;
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithInt
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"one"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|,
literal|8
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithString
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"two"
argument_list|,
literal|"222"
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|8
argument_list|,
literal|8
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithMultipleProperties
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"one"
argument_list|,
literal|"11"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"two"
argument_list|,
literal|"22"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"three"
argument_list|,
literal|"33"
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|segmentVersion
operator|==
name|V_11
condition|)
block|{
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|18
argument_list|,
literal|23
argument_list|,
literal|10
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|23
argument_list|,
literal|16
argument_list|,
literal|12
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithMediumString
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"medium"
argument_list|,
name|repeat
argument_list|(
literal|"a"
argument_list|,
name|SMALL_LIMIT
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|138
argument_list|,
literal|8
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithLargeString
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"large"
argument_list|,
name|repeat
argument_list|(
literal|"b"
argument_list|,
name|MEDIUM_LIMIT
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|15
argument_list|,
literal|16530
argument_list|,
literal|8
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithSameString
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"two"
argument_list|,
literal|"two"
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|,
literal|8
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithInts
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"multi"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|1L
argument_list|,
literal|2L
argument_list|,
literal|3L
argument_list|,
literal|4L
argument_list|)
argument_list|,
name|LONGS
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|12
argument_list|,
literal|21
argument_list|,
literal|8
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithManyInts
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"multi"
argument_list|,
name|nCopies
argument_list|(
name|LEVEL_SIZE
operator|+
literal|1
argument_list|,
literal|1L
argument_list|)
argument_list|,
name|LONGS
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|771
argument_list|,
literal|15
argument_list|,
literal|8
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithManyIntsAndOne
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"multi"
argument_list|,
name|nCopies
argument_list|(
name|LEVEL_SIZE
operator|+
literal|2
argument_list|,
literal|1L
argument_list|)
argument_list|,
name|LONGS
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|777
argument_list|,
literal|15
argument_list|,
literal|8
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithStrings
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"multi"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"one"
argument_list|,
literal|"one"
argument_list|,
literal|"two"
argument_list|,
literal|"two"
argument_list|,
literal|"three"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|15
argument_list|,
literal|27
argument_list|,
literal|8
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithBlob
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"blob"
argument_list|,
name|createRandomBlob
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|8
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithMediumBlob
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"mediumBlob"
argument_list|,
name|createRandomBlob
argument_list|(
name|SMALL_LIMIT
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|142
argument_list|,
literal|8
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithLargeBlob
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"largeBlob"
argument_list|,
name|createRandomBlob
argument_list|(
name|MEDIUM_LIMIT
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|15
argument_list|,
literal|16534
argument_list|,
literal|8
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithPrimaryType
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
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
literal|"type"
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|,
literal|7
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithMixinTypes
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"jcr:mixinTypes"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"type1"
argument_list|,
literal|"type2"
argument_list|)
argument_list|,
name|NAMES
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|12
argument_list|,
literal|10
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|singleChild
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"child"
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|,
literal|11
argument_list|,
literal|9
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|multiChild
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"child1"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"child2"
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|24
argument_list|,
literal|0
argument_list|,
literal|14
argument_list|,
literal|8
argument_list|,
literal|12
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|manyChild
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|MapRecord
operator|.
name|BUCKETS_PER_LEVEL
operator|+
literal|1
condition|;
name|k
operator|++
control|)
block|{
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"child"
operator|+
name|k
argument_list|)
expr_stmt|;
block|}
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|457
argument_list|,
literal|0
argument_list|,
literal|254
argument_list|,
literal|8
argument_list|,
literal|105
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|changedChild
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"child1"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"child2"
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|24
argument_list|,
literal|0
argument_list|,
literal|14
argument_list|,
literal|8
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|builder
operator|=
name|node
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"child1"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"q"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|store
operator|.
name|containsSegment
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
operator|.
name|getSegmentId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|node
operator|=
operator|(
name|SegmentNodeState
operator|)
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSizes
argument_list|(
name|analyser
argument_list|,
literal|41
argument_list|,
literal|0
argument_list|,
literal|18
argument_list|,
literal|16
argument_list|,
literal|24
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|counts
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"child1"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"child2"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"mediumString"
argument_list|,
name|repeat
argument_list|(
literal|"m"
argument_list|,
name|SMALL_LIMIT
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"longString"
argument_list|,
name|repeat
argument_list|(
literal|"l"
argument_list|,
name|MEDIUM_LIMIT
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"smallBlob"
argument_list|,
name|createRandomBlob
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"mediumBlob"
argument_list|,
name|createRandomBlob
argument_list|(
name|SMALL_LIMIT
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"longBlob"
argument_list|,
name|createRandomBlob
argument_list|(
name|MEDIUM_LIMIT
argument_list|)
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|node
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
name|analyser
operator|.
name|analyseNode
argument_list|(
name|node
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|segmentVersion
operator|==
name|V_11
condition|)
block|{
name|assertCounts
argument_list|(
name|analyser
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertCounts
argument_list|(
name|analyser
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|6
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Blob
name|createRandomBlob
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
return|return
operator|new
name|ArrayBasedBlob
argument_list|(
name|bytes
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|assertSizes
parameter_list|(
name|RecordUsageAnalyser
name|analyser
parameter_list|,
name|long
name|maps
parameter_list|,
name|long
name|lists
parameter_list|,
name|long
name|values
parameter_list|,
name|long
name|templates
parameter_list|,
name|long
name|nodes
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"maps sizes mismatch"
argument_list|,
name|maps
argument_list|,
name|analyser
operator|.
name|getMapSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"lists sizes mismatch"
argument_list|,
name|lists
argument_list|,
name|analyser
operator|.
name|getListSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value sizes mismatch"
argument_list|,
name|values
argument_list|,
name|analyser
operator|.
name|getValueSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"template sizes mismatch"
argument_list|,
name|templates
argument_list|,
name|analyser
operator|.
name|getTemplateSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"nodes sizes mismatch"
argument_list|,
name|nodes
argument_list|,
name|analyser
operator|.
name|getNodeSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertCounts
parameter_list|(
name|RecordUsageAnalyser
name|analyser
parameter_list|,
name|long
name|mapCount
parameter_list|,
name|long
name|listCount
parameter_list|,
name|long
name|propertyCount
parameter_list|,
name|long
name|smallBlobCount
parameter_list|,
name|long
name|mediumBlobCount
parameter_list|,
name|long
name|longBlobCount
parameter_list|,
name|long
name|externalBlobCount
parameter_list|,
name|long
name|smallStringCount
parameter_list|,
name|long
name|mediumStringCount
parameter_list|,
name|long
name|longStringCount
parameter_list|,
name|long
name|templateCount
parameter_list|,
name|long
name|nodeCount
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"map count mismatch"
argument_list|,
name|mapCount
argument_list|,
name|analyser
operator|.
name|getMapCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"list count mismatch"
argument_list|,
name|listCount
argument_list|,
name|analyser
operator|.
name|getListCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"property count mismatch"
argument_list|,
name|propertyCount
argument_list|,
name|analyser
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"small blob count mismatch"
argument_list|,
name|smallBlobCount
argument_list|,
name|analyser
operator|.
name|getSmallBlobCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"medium blob mismatch"
argument_list|,
name|mediumBlobCount
argument_list|,
name|analyser
operator|.
name|getMediumBlobCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"long blob count mismatch"
argument_list|,
name|longBlobCount
argument_list|,
name|analyser
operator|.
name|getLongBlobCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"external blob count mismatch"
argument_list|,
name|externalBlobCount
argument_list|,
name|analyser
operator|.
name|getExternalBlobCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"small string count mismatch"
argument_list|,
name|smallStringCount
argument_list|,
name|analyser
operator|.
name|getSmallStringCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"medium string count mismatch"
argument_list|,
name|mediumStringCount
argument_list|,
name|analyser
operator|.
name|getMediumStringCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"long string count mismatch"
argument_list|,
name|longStringCount
argument_list|,
name|analyser
operator|.
name|getLongStringCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"template count mismatch"
argument_list|,
name|templateCount
argument_list|,
name|analyser
operator|.
name|getTemplateCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"node count mismatch"
argument_list|,
name|nodeCount
argument_list|,
name|analyser
operator|.
name|getNodeCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

