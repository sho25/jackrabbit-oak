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
name|newTreeMap
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
name|newTreeSet
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

begin_comment
comment|/**  * Immutable, space-optimized mapping of compacted record identifiers.  * Used to optimize record equality comparisons across a compaction operation  * without holding references to the {@link SegmentId} instances of the old,  * compacted segments.  *<p>  * The data structure used by this class consists of four parts:  *<ol>  *<li>The {@link #recent} map of recently compacted entries is maintained  *       while the compaction is in progress and new entries need to be added.  *       These entries are periodically compressed into the more  *       memory-efficient structure described below.  *<li>The {@link #msbs} and {@link #lsbs} arrays store the identifiers  *       of all old, compacted segments. The identifiers are stored in  *       increasing order, with the i'th identifier stored in the  *       {@code msbs[i]} and {@code lsbs[i]} slots. Interpolation search  *       is used to quickly locate any given identifier.  *<li>Each compacted segment identifier is associated with a list of  *       mapping entries that point from a record offset within that  *       segment to the new identifier of the compacted record. The  *       {@link #entryIndex} array is used to to locate these lists within  *       the larger entry arrays described below. The list of entries for  *       the i'th identifier consists of entries from {@code entryIndex[i]}  *       (inclusive) to {@code entryIndex[i+1]} (exclusive). An extra  *       sentinel slot is added at the end of the array to make the above  *       rule work also for the last compacted segment identifier.  *<li>The mapping entries are stored in the {@link #beforeOffsets},  *       {@link #afterSegmentIds} and {@link #afterOffsets} arrays. Once the  *       list of entries for a given compacted segment is found, the  *       before record offsets are scanned to find a match. If a match is  *       found, the corresponding compacted record will be identified by the  *       respective after segment identifier and offset.  *</ol>  *<p>  * Assuming each compacted segment contains {@code n} compacted records on  * average, the amortized size of each entry in this mapping is about  * {@code 20/n + 8} bytes, assuming compressed pointers.  */
end_comment

begin_class
class|class
name|CompactionMap
block|{
specifier|private
specifier|final
name|int
name|compressInterval
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|RecordId
argument_list|,
name|RecordId
argument_list|>
name|recent
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
name|long
index|[]
name|msbs
init|=
operator|new
name|long
index|[
literal|0
index|]
decl_stmt|;
specifier|private
name|long
index|[]
name|lsbs
init|=
operator|new
name|long
index|[
literal|0
index|]
decl_stmt|;
specifier|private
name|int
index|[]
name|entryIndex
init|=
operator|new
name|int
index|[
literal|0
index|]
decl_stmt|;
specifier|private
name|short
index|[]
name|beforeOffsets
init|=
operator|new
name|short
index|[
literal|0
index|]
decl_stmt|;
specifier|private
name|SegmentId
index|[]
name|afterSegmentIds
init|=
operator|new
name|SegmentId
index|[
literal|0
index|]
decl_stmt|;
specifier|private
name|short
index|[]
name|afterOffsets
init|=
operator|new
name|short
index|[
literal|0
index|]
decl_stmt|;
name|CompactionMap
parameter_list|(
name|int
name|compressInterval
parameter_list|)
block|{
name|this
operator|.
name|compressInterval
operator|=
name|compressInterval
expr_stmt|;
block|}
comment|/**      * Checks whether the record with the given {@code before} identifier was      * compacted to a new record with the given {@code after} identifier.      *      * @param before before record identifier      * @param after after record identifier      * @return whether {@code before} was compacted to {@code after}      */
name|boolean
name|wasCompactedTo
parameter_list|(
name|RecordId
name|before
parameter_list|,
name|RecordId
name|after
parameter_list|)
block|{
return|return
name|after
operator|.
name|equals
argument_list|(
name|get
argument_list|(
name|before
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RecordId
name|get
parameter_list|(
name|RecordId
name|before
parameter_list|)
block|{
name|RecordId
name|after
init|=
name|recent
operator|.
name|get
argument_list|(
name|before
argument_list|)
decl_stmt|;
if|if
condition|(
name|after
operator|!=
literal|null
condition|)
block|{
return|return
name|after
return|;
block|}
name|SegmentId
name|segmentId
init|=
name|before
operator|.
name|getSegmentId
argument_list|()
decl_stmt|;
name|long
name|msb
init|=
name|segmentId
operator|.
name|getMostSignificantBits
argument_list|()
decl_stmt|;
name|long
name|lsb
init|=
name|segmentId
operator|.
name|getLeastSignificantBits
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|before
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|int
name|entry
init|=
name|findEntry
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
operator|-
literal|1
condition|)
block|{
name|int
name|index
init|=
name|entryIndex
index|[
name|entry
index|]
decl_stmt|;
name|int
name|limit
init|=
name|entryIndex
index|[
name|entry
operator|+
literal|1
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|index
init|;
name|i
operator|<
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|int
name|o
init|=
operator|(
name|beforeOffsets
index|[
name|i
index|]
operator|&
literal|0xffff
operator|)
operator|<<
name|RECORD_ALIGN_BITS
decl_stmt|;
if|if
condition|(
name|o
operator|==
name|offset
condition|)
block|{
comment|// found it!
return|return
operator|new
name|RecordId
argument_list|(
name|afterSegmentIds
index|[
name|i
index|]
argument_list|,
operator|(
name|afterOffsets
index|[
name|i
index|]
operator|&
literal|0xffff
operator|)
operator|<<
name|RECORD_ALIGN_BITS
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|o
operator|>
name|offset
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
name|void
name|put
parameter_list|(
name|RecordId
name|before
parameter_list|,
name|RecordId
name|after
parameter_list|)
block|{
name|recent
operator|.
name|put
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
if|if
condition|(
name|recent
operator|.
name|size
argument_list|()
operator|>=
name|compressInterval
condition|)
block|{
name|compress
argument_list|()
expr_stmt|;
block|}
block|}
name|void
name|compress
parameter_list|()
block|{
name|Set
argument_list|<
name|UUID
argument_list|>
name|uuids
init|=
name|newTreeSet
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|UUID
argument_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|RecordId
argument_list|>
argument_list|>
name|mapping
init|=
name|newTreeMap
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
name|recent
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|RecordId
name|before
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|SegmentId
name|id
init|=
name|before
operator|.
name|getSegmentId
argument_list|()
decl_stmt|;
name|UUID
name|uuid
init|=
operator|new
name|UUID
argument_list|(
name|id
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
decl_stmt|;
name|uuids
operator|.
name|add
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|RecordId
argument_list|>
name|map
init|=
name|mapping
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
name|map
operator|=
name|newTreeMap
argument_list|()
expr_stmt|;
name|mapping
operator|.
name|put
argument_list|(
name|uuid
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
name|before
operator|.
name|getOffset
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|msbs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|uuids
operator|.
name|add
argument_list|(
operator|new
name|UUID
argument_list|(
name|msbs
index|[
name|i
index|]
argument_list|,
name|lsbs
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|long
index|[]
name|newmsbs
init|=
operator|new
name|long
index|[
name|uuids
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|long
index|[]
name|newlsbs
init|=
operator|new
name|long
index|[
name|uuids
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
index|[]
name|newEntryIndex
init|=
operator|new
name|int
index|[
name|uuids
operator|.
name|size
argument_list|()
operator|+
literal|1
index|]
decl_stmt|;
name|int
name|newEntries
init|=
name|beforeOffsets
operator|.
name|length
operator|+
name|recent
operator|.
name|size
argument_list|()
decl_stmt|;
name|short
index|[]
name|newBeforeOffsets
init|=
operator|new
name|short
index|[
name|newEntries
index|]
decl_stmt|;
name|SegmentId
index|[]
name|newAfterSegmentIds
init|=
operator|new
name|SegmentId
index|[
name|newEntries
index|]
decl_stmt|;
name|short
index|[]
name|newAfterOffsets
init|=
operator|new
name|short
index|[
name|newEntries
index|]
decl_stmt|;
name|int
name|newIndex
init|=
literal|0
decl_stmt|;
name|int
name|newEntry
init|=
literal|0
decl_stmt|;
name|int
name|oldEntry
init|=
literal|0
decl_stmt|;
for|for
control|(
name|UUID
name|uuid
range|:
name|uuids
control|)
block|{
name|newmsbs
index|[
name|newEntry
index|]
operator|=
name|uuid
operator|.
name|getMostSignificantBits
argument_list|()
expr_stmt|;
name|newlsbs
index|[
name|newEntry
index|]
operator|=
name|uuid
operator|.
name|getLeastSignificantBits
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|RecordId
argument_list|>
name|map
init|=
name|mapping
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
name|map
operator|=
name|newTreeMap
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|oldEntry
operator|<
name|msbs
operator|.
name|length
operator|&&
name|msbs
index|[
name|oldEntry
index|]
operator|==
name|newmsbs
index|[
name|newEntry
index|]
operator|&&
name|lsbs
index|[
name|oldEntry
index|]
operator|==
name|newlsbs
index|[
name|newEntry
index|]
condition|)
block|{
name|int
name|index
init|=
name|entryIndex
index|[
name|oldEntry
index|]
decl_stmt|;
name|int
name|limit
init|=
name|entryIndex
index|[
name|oldEntry
operator|+
literal|1
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|index
init|;
name|i
operator|<
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|map
operator|.
name|put
argument_list|(
operator|(
name|beforeOffsets
index|[
name|i
index|]
operator|&
literal|0xffff
operator|)
operator|<<
name|RECORD_ALIGN_BITS
argument_list|,
operator|new
name|RecordId
argument_list|(
name|afterSegmentIds
index|[
name|i
index|]
argument_list|,
operator|(
name|afterOffsets
index|[
name|i
index|]
operator|&
literal|0xffff
operator|)
operator|<<
name|RECORD_ALIGN_BITS
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|oldEntry
operator|++
expr_stmt|;
block|}
name|newEntryIndex
index|[
name|newEntry
operator|++
index|]
operator|=
name|newIndex
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Integer
argument_list|,
name|RecordId
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|int
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|RecordId
name|id
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|newBeforeOffsets
index|[
name|newIndex
index|]
operator|=
call|(
name|short
call|)
argument_list|(
name|key
operator|>>
name|RECORD_ALIGN_BITS
argument_list|)
expr_stmt|;
name|newAfterSegmentIds
index|[
name|newIndex
index|]
operator|=
name|id
operator|.
name|getSegmentId
argument_list|()
expr_stmt|;
name|newAfterOffsets
index|[
name|newIndex
index|]
operator|=
call|(
name|short
call|)
argument_list|(
name|id
operator|.
name|getOffset
argument_list|()
operator|>>
name|RECORD_ALIGN_BITS
argument_list|)
expr_stmt|;
name|newIndex
operator|++
expr_stmt|;
block|}
block|}
name|newEntryIndex
index|[
name|newEntry
index|]
operator|=
name|newIndex
expr_stmt|;
name|this
operator|.
name|msbs
operator|=
name|newmsbs
expr_stmt|;
name|this
operator|.
name|lsbs
operator|=
name|newlsbs
expr_stmt|;
name|this
operator|.
name|entryIndex
operator|=
name|newEntryIndex
expr_stmt|;
name|this
operator|.
name|beforeOffsets
operator|=
name|newBeforeOffsets
expr_stmt|;
name|this
operator|.
name|afterSegmentIds
operator|=
name|newAfterSegmentIds
expr_stmt|;
name|this
operator|.
name|afterOffsets
operator|=
name|newAfterOffsets
expr_stmt|;
name|recent
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * Finds the given segment identifier (UUID) within the list of      * identifiers of compacted segments tracked by this instance.      * Since the UUIDs are randomly generated and we keep the list      * sorted, we can use interpolation search to achieve      * {@code O(log log n)} lookup performance.      *      * @param msb most significant bits of the UUID      * @param lsb least significant bits of the UUID      * @return entry index, or {@code -1} if not found      */
specifier|private
specifier|final
name|int
name|findEntry
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
block|{
name|int
name|lowIndex
init|=
literal|0
decl_stmt|;
name|int
name|highIndex
init|=
name|msbs
operator|.
name|length
operator|-
literal|1
decl_stmt|;
comment|// Use floats to prevent integer overflow during interpolation.
comment|// Lost accuracy is no problem, since we use interpolation only
comment|// as a guess of where the target value is located and the actual
comment|// comparisons are still done using the original values.
name|float
name|lowValue
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
name|float
name|highValue
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|float
name|targetValue
init|=
name|msb
decl_stmt|;
while|while
condition|(
name|lowIndex
operator|<=
name|highIndex
condition|)
block|{
name|int
name|guessIndex
init|=
name|lowIndex
decl_stmt|;
name|float
name|valueRange
init|=
name|highValue
operator|-
name|lowValue
decl_stmt|;
if|if
condition|(
name|valueRange
operator|>=
literal|1
condition|)
block|{
comment|// no point in interpolating further
comment|// Math.round() also prevents IndexOutOfBoundsExceptions
comment|// caused by possible inaccuracy in the float computations.
name|guessIndex
operator|+=
name|Math
operator|.
name|round
argument_list|(
operator|(
name|highIndex
operator|-
name|lowIndex
operator|)
operator|*
operator|(
name|targetValue
operator|-
name|lowValue
operator|)
operator|/
name|valueRange
argument_list|)
expr_stmt|;
block|}
name|long
name|m
init|=
name|msbs
index|[
name|guessIndex
index|]
decl_stmt|;
if|if
condition|(
name|msb
operator|<
name|m
condition|)
block|{
name|highIndex
operator|=
name|guessIndex
operator|-
literal|1
expr_stmt|;
name|highValue
operator|=
name|m
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|msb
operator|>
name|m
condition|)
block|{
name|lowIndex
operator|=
name|guessIndex
operator|+
literal|1
expr_stmt|;
name|lowValue
operator|=
name|m
expr_stmt|;
block|}
else|else
block|{
comment|// getting close...
name|long
name|l
init|=
name|lsbs
index|[
name|guessIndex
index|]
decl_stmt|;
if|if
condition|(
name|lsb
operator|<
name|l
condition|)
block|{
name|highIndex
operator|=
name|guessIndex
operator|-
literal|1
expr_stmt|;
name|highValue
operator|=
name|m
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lsb
operator|>
name|l
condition|)
block|{
name|highIndex
operator|=
name|guessIndex
operator|+
literal|1
expr_stmt|;
name|highValue
operator|=
name|m
expr_stmt|;
block|}
else|else
block|{
comment|// found it!
return|return
name|guessIndex
return|;
block|}
block|}
block|}
comment|// not found
return|return
operator|-
literal|1
return|;
block|}
block|}
end_class

end_unit

