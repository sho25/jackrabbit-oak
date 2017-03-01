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
name|collect
operator|.
name|Iterables
operator|.
name|elementsEqual
import|;
end_import

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
name|memory
operator|.
name|MemoryStore
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
name|MutableSegmentReferencesTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|referencesShouldBeGreaterThanZero
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
name|SegmentId
name|id
init|=
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
operator|.
name|newDataSegmentId
argument_list|()
decl_stmt|;
name|MutableSegmentReferences
name|table
init|=
operator|new
name|MutableSegmentReferences
argument_list|()
decl_stmt|;
name|int
name|reference
init|=
name|table
operator|.
name|addOrReference
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reference
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|referencesShouldBeIncrementing
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
name|SegmentId
name|first
init|=
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
operator|.
name|newDataSegmentId
argument_list|()
decl_stmt|;
name|SegmentId
name|second
init|=
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
operator|.
name|newDataSegmentId
argument_list|()
decl_stmt|;
name|MutableSegmentReferences
name|table
init|=
operator|new
name|MutableSegmentReferences
argument_list|()
decl_stmt|;
name|int
name|firstReference
init|=
name|table
operator|.
name|addOrReference
argument_list|(
name|first
argument_list|)
decl_stmt|;
name|int
name|secondReference
init|=
name|table
operator|.
name|addOrReference
argument_list|(
name|second
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|firstReference
operator|+
literal|1
argument_list|,
name|secondReference
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldAddNewSegmentReference
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
name|SegmentId
name|id
init|=
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
operator|.
name|newDataSegmentId
argument_list|()
decl_stmt|;
name|MutableSegmentReferences
name|table
init|=
operator|new
name|MutableSegmentReferences
argument_list|()
decl_stmt|;
name|int
name|reference
init|=
name|table
operator|.
name|addOrReference
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|id
argument_list|,
name|table
operator|.
name|getSegmentId
argument_list|(
name|reference
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldNotAddSameSegmentIdTwice
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
name|SegmentId
name|id
init|=
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
operator|.
name|newDataSegmentId
argument_list|()
decl_stmt|;
name|MutableSegmentReferences
name|table
init|=
operator|new
name|MutableSegmentReferences
argument_list|()
decl_stmt|;
name|int
name|first
init|=
name|table
operator|.
name|addOrReference
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|int
name|second
init|=
name|table
operator|.
name|addOrReference
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|first
argument_list|,
name|second
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldMaintainSize
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
name|SegmentId
name|id
init|=
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
operator|.
name|newDataSegmentId
argument_list|()
decl_stmt|;
name|MutableSegmentReferences
name|table
init|=
operator|new
name|MutableSegmentReferences
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|table
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addOrReference
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|table
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
name|shouldContainAddedSegment
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
name|SegmentId
name|id
init|=
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
operator|.
name|newDataSegmentId
argument_list|()
decl_stmt|;
name|MutableSegmentReferences
name|table
init|=
operator|new
name|MutableSegmentReferences
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|table
operator|.
name|contains
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|addOrReference
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|table
operator|.
name|contains
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldIterateInInsertionOrder
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
name|SegmentId
name|first
init|=
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
operator|.
name|newDataSegmentId
argument_list|()
decl_stmt|;
name|SegmentId
name|second
init|=
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
operator|.
name|newDataSegmentId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SegmentId
argument_list|>
name|ids
init|=
name|newArrayList
argument_list|(
name|first
argument_list|,
name|second
argument_list|)
decl_stmt|;
name|MutableSegmentReferences
name|table
init|=
operator|new
name|MutableSegmentReferences
argument_list|()
decl_stmt|;
name|table
operator|.
name|addOrReference
argument_list|(
name|first
argument_list|)
expr_stmt|;
name|table
operator|.
name|addOrReference
argument_list|(
name|second
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|elementsEqual
argument_list|(
name|ids
argument_list|,
name|table
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

