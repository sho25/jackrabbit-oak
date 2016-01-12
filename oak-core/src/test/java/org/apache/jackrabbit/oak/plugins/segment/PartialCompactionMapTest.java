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
name|collect
operator|.
name|Iterables
operator|.
name|get
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|io
operator|.
name|File
operator|.
name|createTempFile
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
name|commons
operator|.
name|io
operator|.
name|FileUtils
operator|.
name|deleteDirectory
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
name|IOUtils
operator|.
name|humanReadableByteCount
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
name|benchmark
operator|.
name|MicroBenchmark
operator|.
name|run
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
name|SegmentVersion
operator|.
name|V_11
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
name|TestUtils
operator|.
name|newValidOffset
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
name|TestUtils
operator|.
name|randomRecordIdMap
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
name|file
operator|.
name|FileStore
operator|.
name|newFileStore
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
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|commons
operator|.
name|math3
operator|.
name|stat
operator|.
name|descriptive
operator|.
name|DescriptiveStatistics
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
name|benchmark
operator|.
name|MicroBenchmark
operator|.
name|Benchmark
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
name|file
operator|.
name|FileStore
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *<p>  * This is a unit test + benchmark test for all the compaction map  * implementations.  *</p>  *<p>  * The benchmark tests are<b>disabled</b> by default, to run one of them you  * need to set the specific {@code benchmark.*} system property:<br>  * {@code mvn test -Dtest.opts.memory=-Xmx5G -Dtest=PartialCompactionMapTest -Dbenchmark.benchLargeMap=true -Dbenchmark.benchPut=true -Dbenchmark.benchGet=true}  *</p>  */
end_comment

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
name|PartialCompactionMapTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PartialCompactionMapTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|SEED
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"SEED"
argument_list|,
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Random
name|rnd
init|=
operator|new
name|Random
argument_list|(
name|SEED
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|usePersistedMap
decl_stmt|;
specifier|private
name|File
name|directory
decl_stmt|;
specifier|private
name|FileStore
name|segmentStore
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|RecordId
argument_list|,
name|RecordId
argument_list|>
name|reference
decl_stmt|;
specifier|private
name|PartialCompactionMap
name|map
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
specifier|public
specifier|static
name|List
argument_list|<
name|Boolean
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
name|Boolean
index|[]
block|{
literal|true
block|}
argument_list|,
operator|new
name|Boolean
index|[]
block|{
literal|false
block|}
argument_list|)
return|;
block|}
specifier|public
name|PartialCompactionMapTest
parameter_list|(
name|boolean
name|usePersistedMap
parameter_list|)
block|{
name|this
operator|.
name|usePersistedMap
operator|=
name|usePersistedMap
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|directory
operator|=
name|createTempFile
argument_list|(
name|PartialCompactionMapTest
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|"dir"
argument_list|,
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
expr_stmt|;
name|directory
operator|.
name|delete
argument_list|()
expr_stmt|;
name|directory
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|segmentStore
operator|=
name|newFileStore
argument_list|(
name|directory
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|segmentStore
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|deleteDirectory
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//
block|}
block|}
specifier|private
name|SegmentTracker
name|getTracker
parameter_list|()
block|{
return|return
name|segmentStore
operator|.
name|getTracker
argument_list|()
return|;
block|}
specifier|private
name|PartialCompactionMap
name|createCompactionMap
parameter_list|()
block|{
name|SegmentWriter
name|writer
init|=
operator|new
name|SegmentWriter
argument_list|(
name|segmentStore
argument_list|,
name|V_11
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|usePersistedMap
condition|)
block|{
return|return
operator|new
name|PersistedCompactionMap
argument_list|(
name|segmentStore
operator|.
name|getTracker
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|InMemoryCompactionMap
argument_list|(
name|segmentStore
operator|.
name|getTracker
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|private
name|void
name|addAll
parameter_list|(
name|Map
argument_list|<
name|RecordId
argument_list|,
name|RecordId
argument_list|>
name|toAdd
parameter_list|)
block|{
assert|assert
name|map
operator|!=
literal|null
assert|;
for|for
control|(
name|Entry
argument_list|<
name|RecordId
argument_list|,
name|RecordId
argument_list|>
name|tuple
range|:
name|toAdd
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|reference
operator|!=
literal|null
condition|)
block|{
name|reference
operator|.
name|put
argument_list|(
name|tuple
operator|.
name|getKey
argument_list|()
argument_list|,
name|tuple
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
name|tuple
operator|.
name|getKey
argument_list|()
argument_list|,
name|tuple
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addRandomEntries
parameter_list|(
name|int
name|segmentCount
parameter_list|,
name|int
name|entriesPerSegment
parameter_list|)
block|{
assert|assert
name|map
operator|!=
literal|null
assert|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|segmentCount
operator|/
literal|1000
condition|;
name|k
operator|++
control|)
block|{
name|addAll
argument_list|(
name|randomRecordIdMap
argument_list|(
name|rnd
argument_list|,
name|getTracker
argument_list|()
argument_list|,
literal|1000
argument_list|,
name|entriesPerSegment
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|addAll
argument_list|(
name|randomRecordIdMap
argument_list|(
name|rnd
argument_list|,
name|getTracker
argument_list|()
argument_list|,
name|segmentCount
operator|%
literal|1000
argument_list|,
name|entriesPerSegment
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|removeRandomEntries
parameter_list|(
name|int
name|count
parameter_list|)
block|{
assert|assert
name|reference
operator|!=
literal|null
assert|;
assert|assert
name|map
operator|!=
literal|null
assert|;
name|Set
argument_list|<
name|SegmentId
argument_list|>
name|remove
init|=
name|newHashSet
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
name|count
operator|&&
operator|!
name|reference
operator|.
name|isEmpty
argument_list|()
condition|;
name|k
operator|++
control|)
block|{
name|int
name|j
init|=
name|rnd
operator|.
name|nextInt
argument_list|(
name|reference
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|remove
operator|.
name|add
argument_list|(
name|get
argument_list|(
name|reference
operator|.
name|keySet
argument_list|()
argument_list|,
name|j
argument_list|)
operator|.
name|getSegmentId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|UUID
argument_list|>
name|removeUUIDs
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|SegmentId
name|sid
range|:
name|remove
control|)
block|{
name|removeUUIDs
operator|.
name|add
argument_list|(
operator|new
name|UUID
argument_list|(
name|sid
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|sid
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|RecordId
argument_list|>
name|it
init|=
name|reference
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|sid
operator|.
name|equals
argument_list|(
name|it
operator|.
name|next
argument_list|()
operator|.
name|getSegmentId
argument_list|()
argument_list|)
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|map
operator|.
name|remove
argument_list|(
name|removeUUIDs
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkMap
parameter_list|()
block|{
assert|assert
name|reference
operator|!=
literal|null
assert|;
assert|assert
name|map
operator|!=
literal|null
assert|;
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
name|reference
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
literal|"Failed with seed "
operator|+
name|SEED
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
name|SEED
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
annotation|@
name|Test
specifier|public
name|void
name|single
parameter_list|()
block|{
name|map
operator|=
name|createCompactionMap
argument_list|()
expr_stmt|;
name|RecordId
name|before
init|=
name|RecordId
operator|.
name|fromString
argument_list|(
name|getTracker
argument_list|()
argument_list|,
literal|"00000000-0000-0000-0000-000000000000.0000"
argument_list|)
decl_stmt|;
name|RecordId
name|after
init|=
name|RecordId
operator|.
name|fromString
argument_list|(
name|getTracker
argument_list|()
argument_list|,
literal|"11111111-1111-1111-1111-111111111111.1111"
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|after
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|before
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|compress
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|after
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|before
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|getRecordCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|getSegmentCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|map
operator|=
name|createCompactionMap
argument_list|()
expr_stmt|;
name|RecordId
name|before1
init|=
name|RecordId
operator|.
name|fromString
argument_list|(
name|getTracker
argument_list|()
argument_list|,
literal|"00000000-0000-0000-0000-000000000000.0000"
argument_list|)
decl_stmt|;
name|RecordId
name|before2
init|=
name|RecordId
operator|.
name|fromString
argument_list|(
name|getTracker
argument_list|()
argument_list|,
literal|"00000000-0000-0000-0000-000000000000.1111"
argument_list|)
decl_stmt|;
name|RecordId
name|after1
init|=
name|RecordId
operator|.
name|fromString
argument_list|(
name|getTracker
argument_list|()
argument_list|,
literal|"11111111-1111-1111-1111-111111111111.0000"
argument_list|)
decl_stmt|;
name|RecordId
name|after2
init|=
name|RecordId
operator|.
name|fromString
argument_list|(
name|getTracker
argument_list|()
argument_list|,
literal|"11111111-1111-1111-1111-111111111111.1111"
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|before1
argument_list|,
name|after1
argument_list|)
expr_stmt|;
name|map
operator|.
name|compress
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|before2
argument_list|,
name|after2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|after1
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|before1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|after2
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|before2
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
name|newHashSet
argument_list|(
name|before1
operator|.
name|asUUID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|before1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|before2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|map
operator|.
name|getRecordCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|map
operator|.
name|getSegmentCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|UUID
argument_list|>
name|toUUID
parameter_list|(
name|Set
argument_list|<
name|RecordId
argument_list|>
name|recordIds
parameter_list|)
block|{
name|Set
argument_list|<
name|UUID
argument_list|>
name|uuids
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|RecordId
name|recordId
range|:
name|recordIds
control|)
block|{
name|uuids
operator|.
name|add
argument_list|(
name|recordId
operator|.
name|asUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|uuids
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|random
parameter_list|()
block|{
name|int
name|maxSegments
init|=
literal|1000
decl_stmt|;
name|int
name|entriesPerSegment
init|=
literal|10
decl_stmt|;
name|reference
operator|=
name|newHashMap
argument_list|()
expr_stmt|;
name|map
operator|=
name|createCompactionMap
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|10
condition|;
name|k
operator|++
control|)
block|{
name|addRandomEntries
argument_list|(
name|rnd
operator|.
name|nextInt
argument_list|(
name|maxSegments
argument_list|)
operator|+
literal|1
argument_list|,
name|rnd
operator|.
name|nextInt
argument_list|(
name|entriesPerSegment
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|reference
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|removeRandomEntries
argument_list|(
name|rnd
operator|.
name|nextInt
argument_list|(
name|reference
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|checkMap
argument_list|()
expr_stmt|;
block|}
name|map
operator|.
name|compress
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|reference
operator|.
name|size
argument_list|()
argument_list|,
name|map
operator|.
name|getRecordCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|toUUID
argument_list|(
name|reference
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|map
operator|.
name|getSegmentCount
argument_list|()
argument_list|)
expr_stmt|;
name|checkMap
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertHeapSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|long
name|mem
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|maxMemory
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Need "
operator|+
name|humanReadableByteCount
argument_list|(
name|size
argument_list|)
operator|+
literal|", only found "
operator|+
name|humanReadableByteCount
argument_list|(
name|mem
argument_list|)
argument_list|,
name|mem
operator|>=
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|benchLargeMap
parameter_list|()
block|{
name|assumeTrue
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"benchmark.benchLargeMap"
argument_list|)
argument_list|)
expr_stmt|;
name|assertHeapSize
argument_list|(
literal|4000000000L
argument_list|)
expr_stmt|;
name|map
operator|=
name|createCompactionMap
argument_list|()
expr_stmt|;
comment|// check the memory use of really large mappings, 1M compacted segments with 10 records each.
name|Runtime
name|runtime
init|=
name|Runtime
operator|.
name|getRuntime
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
name|Map
argument_list|<
name|RecordId
argument_list|,
name|RecordId
argument_list|>
name|ids
init|=
name|randomRecordIdMap
argument_list|(
name|rnd
argument_list|,
name|getTracker
argument_list|()
argument_list|,
literal|10000
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
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
name|ids
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|map
operator|.
name|put
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
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Bench Large Map #"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
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
literal|"MB, "
operator|+
operator|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|start
operator|)
operator|/
literal|1000000
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|benchPut
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"benchmark.benchPut"
argument_list|)
argument_list|)
expr_stmt|;
name|assertHeapSize
argument_list|(
literal|4000000000L
argument_list|)
expr_stmt|;
name|run
argument_list|(
operator|new
name|PutBenchmark
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|run
argument_list|(
operator|new
name|PutBenchmark
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|run
argument_list|(
operator|new
name|PutBenchmark
argument_list|(
literal|100
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|run
argument_list|(
operator|new
name|PutBenchmark
argument_list|(
literal|1000
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|run
argument_list|(
operator|new
name|PutBenchmark
argument_list|(
literal|10000
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|run
argument_list|(
operator|new
name|PutBenchmark
argument_list|(
literal|100000
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|run
argument_list|(
operator|new
name|PutBenchmark
argument_list|(
literal|1000000
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|benchGet
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"benchmark.benchGet"
argument_list|)
argument_list|)
expr_stmt|;
name|assertHeapSize
argument_list|(
literal|4000000000L
argument_list|)
expr_stmt|;
name|run
argument_list|(
operator|new
name|GetBenchmark
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|run
argument_list|(
operator|new
name|GetBenchmark
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|run
argument_list|(
operator|new
name|GetBenchmark
argument_list|(
literal|100
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|run
argument_list|(
operator|new
name|GetBenchmark
argument_list|(
literal|1000
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|run
argument_list|(
operator|new
name|GetBenchmark
argument_list|(
literal|10000
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|run
argument_list|(
operator|new
name|GetBenchmark
argument_list|(
literal|100000
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|run
argument_list|(
operator|new
name|GetBenchmark
argument_list|(
literal|1000000
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|abstract
specifier|static
class|class
name|LoggingBenchmark
extends|extends
name|Benchmark
block|{
annotation|@
name|Override
specifier|public
name|void
name|result
parameter_list|(
name|DescriptiveStatistics
name|statistics
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"{}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|statistics
operator|.
name|getN
argument_list|()
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%6s  %6s  %6s  %6s  %6s  %6s  %6s  %6s"
argument_list|,
literal|"min"
argument_list|,
literal|"10%"
argument_list|,
literal|"50%"
argument_list|,
literal|"90%"
argument_list|,
literal|"max"
argument_list|,
literal|"mean"
argument_list|,
literal|"stdev"
argument_list|,
literal|"N"
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%6.0f  %6.0f  %6.0f  %6.0f  %6.0f  %6.0f  %6.0f  %6d"
argument_list|,
name|statistics
operator|.
name|getMin
argument_list|()
operator|/
literal|1000000
argument_list|,
name|statistics
operator|.
name|getPercentile
argument_list|(
literal|10.0
argument_list|)
operator|/
literal|1000000
argument_list|,
name|statistics
operator|.
name|getPercentile
argument_list|(
literal|50.0
argument_list|)
operator|/
literal|1000000
argument_list|,
name|statistics
operator|.
name|getPercentile
argument_list|(
literal|90.0
argument_list|)
operator|/
literal|1000000
argument_list|,
name|statistics
operator|.
name|getMax
argument_list|()
operator|/
literal|1000000
argument_list|,
name|statistics
operator|.
name|getMean
argument_list|()
operator|/
literal|1000000
argument_list|,
name|statistics
operator|.
name|getStandardDeviation
argument_list|()
operator|/
literal|1000000
argument_list|,
name|statistics
operator|.
name|getN
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"No results"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|PutBenchmark
extends|extends
name|LoggingBenchmark
block|{
specifier|private
specifier|final
name|int
name|segmentCount
decl_stmt|;
specifier|private
specifier|final
name|int
name|entriesPerSegment
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|RecordId
argument_list|,
name|RecordId
argument_list|>
name|putIds
decl_stmt|;
specifier|public
name|PutBenchmark
parameter_list|(
name|int
name|segmentCount
parameter_list|,
name|int
name|entriesPerSegment
parameter_list|)
block|{
name|this
operator|.
name|segmentCount
operator|=
name|segmentCount
expr_stmt|;
name|this
operator|.
name|entriesPerSegment
operator|=
name|entriesPerSegment
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|map
operator|=
name|createCompactionMap
argument_list|()
expr_stmt|;
if|if
condition|(
name|segmentCount
operator|>
literal|0
condition|)
block|{
name|addRandomEntries
argument_list|(
name|segmentCount
argument_list|,
name|entriesPerSegment
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeRun
parameter_list|()
throws|throws
name|Exception
block|{
name|putIds
operator|=
name|randomRecordIdMap
argument_list|(
name|rnd
argument_list|,
name|getTracker
argument_list|()
argument_list|,
literal|10000
operator|/
name|entriesPerSegment
argument_list|,
name|entriesPerSegment
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|Entry
argument_list|<
name|RecordId
argument_list|,
name|RecordId
argument_list|>
name|tuple
range|:
name|putIds
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|tuple
operator|.
name|getKey
argument_list|()
argument_list|,
name|tuple
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Put benchmark: SegmentCount="
operator|+
name|segmentCount
operator|+
literal|", entriesPerSegment="
operator|+
name|entriesPerSegment
return|;
block|}
block|}
specifier|private
class|class
name|GetBenchmark
extends|extends
name|LoggingBenchmark
block|{
specifier|private
specifier|final
name|int
name|segmentCount
decl_stmt|;
specifier|private
specifier|final
name|int
name|entriesPerSegment
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|RecordId
argument_list|>
name|getCandidateIds
init|=
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|RecordId
argument_list|>
name|getIds
init|=
name|newArrayList
argument_list|()
decl_stmt|;
specifier|public
name|GetBenchmark
parameter_list|(
name|int
name|segmentCount
parameter_list|,
name|int
name|entriesPerSegment
parameter_list|)
block|{
name|this
operator|.
name|segmentCount
operator|=
name|segmentCount
expr_stmt|;
name|this
operator|.
name|entriesPerSegment
operator|=
name|entriesPerSegment
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|map
operator|=
name|createCompactionMap
argument_list|()
expr_stmt|;
name|reference
operator|=
operator|new
name|HashMap
argument_list|<
name|RecordId
argument_list|,
name|RecordId
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RecordId
name|put
parameter_list|(
name|RecordId
name|key
parameter_list|,
name|RecordId
name|value
parameter_list|)
block|{
comment|// Wow, what a horrendous hack!!
if|if
condition|(
name|key
operator|.
name|getSegmentId
argument_list|()
operator|.
name|getMostSignificantBits
argument_list|()
operator|%
literal|10000
operator|==
literal|0
condition|)
block|{
name|getCandidateIds
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
expr_stmt|;
name|addRandomEntries
argument_list|(
name|segmentCount
argument_list|,
name|entriesPerSegment
argument_list|)
expr_stmt|;
name|map
operator|.
name|compress
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|10000
condition|;
name|k
operator|++
control|)
block|{
name|getCandidateIds
operator|.
name|add
argument_list|(
operator|new
name|RecordId
argument_list|(
name|getTracker
argument_list|()
operator|.
name|newDataSegmentId
argument_list|()
argument_list|,
name|newValidOffset
argument_list|(
name|rnd
argument_list|,
literal|0
argument_list|,
name|MAX_SEGMENT_SIZE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeRun
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|10000
condition|;
name|k
operator|++
control|)
block|{
name|getIds
operator|.
name|add
argument_list|(
name|getCandidateIds
operator|.
name|get
argument_list|(
name|rnd
operator|.
name|nextInt
argument_list|(
name|getCandidateIds
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|RecordId
name|id
range|:
name|getIds
control|)
block|{
name|map
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterRun
parameter_list|()
throws|throws
name|Exception
block|{
name|getIds
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Get benchmark: segmentCount="
operator|+
name|segmentCount
operator|+
literal|", entriesPerSegment="
operator|+
name|entriesPerSegment
return|;
block|}
block|}
block|}
end_class

end_unit

