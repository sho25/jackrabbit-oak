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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
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
name|MAX_SEGMENT_SIZE
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
name|RECORD_ALIGN_BITS
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|CompactionMapTest
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
comment|// check the memory use of really large mappings, 1M compacted
comment|// segments with 10 records each.
name|Runtime
name|runtime
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
decl_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|(
name|runtime
operator|.
name|totalMemory
argument_list|()
operator|-
name|runtime
operator|.
name|freeMemory
argument_list|()
operator|)
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
argument_list|)
expr_stmt|;
name|SegmentTracker
name|factory
init|=
operator|new
name|MemoryStore
argument_list|()
operator|.
name|getTracker
argument_list|()
decl_stmt|;
name|CompactionMap
name|map
init|=
operator|new
name|CompactionMap
argument_list|(
literal|100000
argument_list|,
name|factory
argument_list|)
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
literal|1000000
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|1000
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|": "
operator|+
operator|(
name|runtime
operator|.
name|totalMemory
argument_list|()
operator|-
name|runtime
operator|.
name|freeMemory
argument_list|()
operator|)
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
operator|+
literal|"MB"
argument_list|)
expr_stmt|;
block|}
name|SegmentId
name|sid
init|=
name|factory
operator|.
name|newDataSegmentId
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|RecordId
name|rid
init|=
operator|new
name|RecordId
argument_list|(
name|sid
argument_list|,
name|j
operator|<<
name|RECORD_ALIGN_BITS
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|rid
argument_list|,
name|rid
argument_list|)
expr_stmt|;
block|}
block|}
name|map
operator|.
name|compress
argument_list|()
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"final: "
operator|+
operator|(
name|runtime
operator|.
name|totalMemory
argument_list|()
operator|-
name|runtime
operator|.
name|freeMemory
argument_list|()
operator|)
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
operator|+
literal|"MB"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Compaction map: "
operator|+
name|map
operator|.
name|getCompactionStats
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCompactionMap
parameter_list|()
block|{
name|int
name|maxSegments
init|=
literal|1000
decl_stmt|;
name|int
name|maxEntriesPerSegment
init|=
literal|10
decl_stmt|;
name|int
name|seed
init|=
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|SegmentTracker
name|factory
init|=
operator|new
name|MemoryStore
argument_list|()
operator|.
name|getTracker
argument_list|()
decl_stmt|;
name|CompactionMap
name|map
init|=
operator|new
name|CompactionMap
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|maxSegments
operator|/
literal|2
argument_list|)
argument_list|,
name|factory
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|RecordId
argument_list|,
name|RecordId
argument_list|>
name|entries
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|int
name|segments
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxSegments
argument_list|)
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
name|segments
condition|;
name|i
operator|++
control|)
block|{
name|SegmentId
name|id
init|=
name|factory
operator|.
name|newDataSegmentId
argument_list|()
decl_stmt|;
name|int
name|n
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxEntriesPerSegment
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
name|MAX_SEGMENT_SIZE
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|n
condition|;
name|j
operator|++
control|)
block|{
name|offset
operator|=
name|newValidOffset
argument_list|(
name|r
argument_list|,
operator|(
name|n
operator|-
name|j
operator|)
operator|<<
name|RECORD_ALIGN_BITS
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|RecordId
name|before
init|=
operator|new
name|RecordId
argument_list|(
name|id
argument_list|,
name|offset
argument_list|)
decl_stmt|;
name|RecordId
name|after
init|=
operator|new
name|RecordId
argument_list|(
name|factory
operator|.
name|newDataSegmentId
argument_list|()
argument_list|,
name|newValidOffset
argument_list|(
name|r
argument_list|,
literal|0
argument_list|,
name|MAX_SEGMENT_SIZE
argument_list|)
argument_list|)
decl_stmt|;
name|entries
operator|.
name|put
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Failed with seed "
operator|+
name|seed
argument_list|,
name|map
operator|.
name|wasCompactedTo
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Failed with seed "
operator|+
name|seed
argument_list|,
name|map
operator|.
name|wasCompactedTo
argument_list|(
name|after
argument_list|,
name|before
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|map
operator|.
name|compress
argument_list|()
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|RecordId
argument_list|,
name|RecordId
argument_list|>
name|entry
range|:
name|entries
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
literal|"Failed with seed "
operator|+
name|seed
argument_list|,
name|map
operator|.
name|wasCompactedTo
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Failed with seed "
operator|+
name|seed
argument_list|,
name|map
operator|.
name|wasCompactedTo
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns a new valid record offset, between {@code a} and {@code b},      * exclusive.      */
specifier|private
specifier|static
name|int
name|newValidOffset
parameter_list|(
name|Random
name|random
parameter_list|,
name|int
name|a
parameter_list|,
name|int
name|b
parameter_list|)
block|{
name|int
name|p
init|=
operator|(
name|a
operator|>>
name|RECORD_ALIGN_BITS
operator|)
operator|+
literal|1
decl_stmt|;
name|int
name|q
init|=
operator|(
name|b
operator|>>
name|RECORD_ALIGN_BITS
operator|)
decl_stmt|;
return|return
operator|(
name|p
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|q
operator|-
name|p
argument_list|)
operator|)
operator|<<
name|RECORD_ALIGN_BITS
return|;
block|}
block|}
end_class

end_unit

