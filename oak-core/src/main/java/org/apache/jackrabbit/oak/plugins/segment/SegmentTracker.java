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
name|Lists
operator|.
name|newLinkedList
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
name|Queues
operator|.
name|newArrayDeque
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
name|newIdentityHashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|blob
operator|.
name|ReferenceCollector
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
comment|/**  * Tracker of references to segment identifiers and segment instances  * that are currently kept in memory.  *<p>  * It is also responsible to cache segment objects in memory.  */
end_comment

begin_class
specifier|public
class|class
name|SegmentTracker
block|{
comment|/** Logger instance */
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
name|SegmentTracker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|MSB_MASK
init|=
operator|~
operator|(
literal|0xfL
operator|<<
literal|12
operator|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|VERSION
init|=
operator|(
literal|0x4L
operator|<<
literal|12
operator|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|LSB_MASK
init|=
operator|~
operator|(
literal|0xfL
operator|<<
literal|60
operator|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|DATA
init|=
literal|0xAL
operator|<<
literal|60
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|BULK
init|=
literal|0xBL
operator|<<
literal|60
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_MEMORY_CACHE_SIZE
init|=
literal|256
decl_stmt|;
comment|/**      * The random number source for generating new segment identifiers.      */
specifier|private
specifier|final
name|SecureRandom
name|random
init|=
operator|new
name|SecureRandom
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SegmentStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|SegmentWriter
name|writer
decl_stmt|;
comment|/**      * Serialized map that contains the link between old record      * identifiers and identifiers of the corresponding records      * after compaction.      */
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|CompactionMap
argument_list|>
name|compactionMap
init|=
operator|new
name|AtomicReference
argument_list|<
name|CompactionMap
argument_list|>
argument_list|(
operator|new
name|CompactionMap
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|long
name|cacheSize
decl_stmt|;
comment|/**      * Hash table of weak references to segment identifiers that are      * currently being accessed. The size of the table is always a power      * of two, which optimizes the {@link #expand()} operation. The table is      * indexed by the random identifier bits, which guarantees uniform      * distribution of entries. Each table entry is either {@code null}      * (when there are no matching identifiers) or a list of weak references      * to the matching identifiers.      */
specifier|private
specifier|final
name|SegmentIdTable
index|[]
name|tables
init|=
operator|new
name|SegmentIdTable
index|[
literal|32
index|]
decl_stmt|;
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|Segment
argument_list|>
name|segments
init|=
name|newLinkedList
argument_list|()
decl_stmt|;
specifier|private
name|long
name|currentSize
init|=
literal|0
decl_stmt|;
specifier|public
name|SegmentTracker
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|int
name|cacheSizeMB
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|tables
index|[
name|i
index|]
operator|=
operator|new
name|SegmentIdTable
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|writer
operator|=
operator|new
name|SegmentWriter
argument_list|(
name|store
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|cacheSize
operator|=
name|cacheSizeMB
operator|*
name|MB
expr_stmt|;
block|}
specifier|public
name|SegmentTracker
parameter_list|(
name|SegmentStore
name|store
parameter_list|)
block|{
name|this
argument_list|(
name|store
argument_list|,
name|DEFAULT_MEMORY_CACHE_SIZE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SegmentWriter
name|getWriter
parameter_list|()
block|{
return|return
name|writer
return|;
block|}
specifier|public
name|SegmentStore
name|getStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
name|Segment
name|getSegment
parameter_list|(
name|SegmentId
name|id
parameter_list|)
block|{
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|setSegment
argument_list|(
name|id
argument_list|,
name|segment
argument_list|)
expr_stmt|;
return|return
name|segment
return|;
block|}
name|void
name|setSegment
parameter_list|(
name|SegmentId
name|id
parameter_list|,
name|Segment
name|segment
parameter_list|)
block|{
comment|// done before synchronization to allow concurrent segment access
comment|// while we update the cache below
name|id
operator|.
name|setSegment
argument_list|(
name|segment
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|long
name|size
init|=
name|segment
operator|.
name|getCacheSize
argument_list|()
decl_stmt|;
name|segments
operator|.
name|addFirst
argument_list|(
name|segment
argument_list|)
expr_stmt|;
name|currentSize
operator|+=
name|size
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Added segment {} to tracker cache ({} bytes)"
argument_list|,
name|id
argument_list|,
name|size
argument_list|)
expr_stmt|;
comment|// TODO possibly this cache could be improved
while|while
condition|(
name|currentSize
operator|>
name|cacheSize
operator|&&
name|segments
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|Segment
name|last
init|=
name|segments
operator|.
name|removeLast
argument_list|()
decl_stmt|;
name|SegmentId
name|lastId
init|=
name|last
operator|.
name|getSegmentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|last
operator|.
name|accessed
argument_list|()
condition|)
block|{
name|segments
operator|.
name|addFirst
argument_list|(
name|last
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Segment {} was recently used, keeping in cache"
argument_list|,
name|lastId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|long
name|lastSize
init|=
name|last
operator|.
name|getCacheSize
argument_list|()
decl_stmt|;
name|lastId
operator|.
name|setSegment
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|currentSize
operator|-=
name|lastSize
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Removed segment {} from tracker cache ({} bytes)"
argument_list|,
name|lastId
argument_list|,
name|lastSize
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|void
name|setCompactionMap
parameter_list|(
name|CompactionMap
name|compaction
parameter_list|)
block|{
name|compactionMap
operator|.
name|set
argument_list|(
name|compaction
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
name|CompactionMap
name|getCompactionMap
parameter_list|()
block|{
return|return
name|compactionMap
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * Returns all segment identifiers that are currently referenced in memory.      *      * @return referenced segment identifiers      */
specifier|public
specifier|synchronized
name|Set
argument_list|<
name|SegmentId
argument_list|>
name|getReferencedSegmentIds
parameter_list|()
block|{
name|Set
argument_list|<
name|SegmentId
argument_list|>
name|ids
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|SegmentIdTable
name|table
range|:
name|tables
control|)
block|{
name|table
operator|.
name|collectReferencedIds
argument_list|(
name|ids
argument_list|)
expr_stmt|;
block|}
return|return
name|ids
return|;
block|}
comment|/**      * Finds all external blob references that are currently accessible      * in this repository and adds them to the given collector. Useful      * for collecting garbage in an external data store.      *<p>      * Note that this method only collects blob references that are already      * stored in the repository (at the time when this method is called), so      * the garbage collector will need some other mechanism for tracking      * in-memory references and references stored while this method is      * running.      */
specifier|public
name|void
name|collectBlobReferences
parameter_list|(
name|ReferenceCollector
name|collector
parameter_list|)
block|{
name|Set
argument_list|<
name|SegmentId
argument_list|>
name|processed
init|=
name|newIdentityHashSet
argument_list|()
decl_stmt|;
name|Queue
argument_list|<
name|SegmentId
argument_list|>
name|queue
init|=
name|newArrayDeque
argument_list|(
name|getReferencedSegmentIds
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// force the current segment to have root record info
while|while
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|SegmentId
name|id
init|=
name|queue
operator|.
name|remove
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|isDataSegmentId
argument_list|()
operator|&&
name|processed
operator|.
name|add
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|Segment
name|segment
init|=
name|id
operator|.
name|getSegment
argument_list|()
decl_stmt|;
name|segment
operator|.
name|collectBlobReferences
argument_list|(
name|collector
argument_list|)
expr_stmt|;
for|for
control|(
name|SegmentId
name|refid
range|:
name|segment
operator|.
name|getReferencedIds
argument_list|()
control|)
block|{
if|if
condition|(
name|refid
operator|.
name|isDataSegmentId
argument_list|()
operator|&&
operator|!
name|processed
operator|.
name|contains
argument_list|(
name|refid
argument_list|)
condition|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|refid
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**      *       * @param msb      * @param lsb      * @return the segment id      */
specifier|public
name|SegmentId
name|getSegmentId
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
block|{
name|int
name|index
init|=
operator|(
operator|(
name|int
operator|)
name|msb
operator|)
operator|&
operator|(
name|tables
operator|.
name|length
operator|-
literal|1
operator|)
decl_stmt|;
return|return
name|tables
index|[
name|index
index|]
operator|.
name|getSegmentId
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
return|;
block|}
name|SegmentId
name|newDataSegmentId
parameter_list|()
block|{
return|return
name|newSegmentId
argument_list|(
name|DATA
argument_list|)
return|;
block|}
name|SegmentId
name|newBulkSegmentId
parameter_list|()
block|{
return|return
name|newSegmentId
argument_list|(
name|BULK
argument_list|)
return|;
block|}
specifier|private
name|SegmentId
name|newSegmentId
parameter_list|(
name|long
name|type
parameter_list|)
block|{
name|long
name|msb
init|=
operator|(
name|random
operator|.
name|nextLong
argument_list|()
operator|&
name|MSB_MASK
operator|)
operator||
name|VERSION
decl_stmt|;
name|long
name|lsb
init|=
operator|(
name|random
operator|.
name|nextLong
argument_list|()
operator|&
name|LSB_MASK
operator|)
operator||
name|type
decl_stmt|;
return|return
name|getSegmentId
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
return|;
block|}
block|}
end_class

end_unit

