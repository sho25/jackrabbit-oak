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
name|newHashMapWithExpectedSize
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
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|WeakReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/**  * Hash table of weak references to segment identifiers.  */
end_comment

begin_class
specifier|public
class|class
name|SegmentIdTable
block|{
comment|/**      * The list of weak references to segment identifiers that are currently      * being accessed. This represents a hash table that uses open addressing      * with linear probing. It is not a hash map, to speed up read access.      *<p>      * The size of the table is always a power of two, so that we can use      * bitwise "and" instead of modulo.      *<p>      * The table is indexed by the random identifier bits, which guarantees      * uniform distribution of entries.      *<p>      * Open addressing with linear probing is used. Each table entry is either      * null (when there are no matching identifiers), a weak references to the      * matching identifier, or a weak reference to another identifier.      * There are no tombstone entries as there is no explicit remove operation,      * but a referent can become null if the entry is garbage collected.      *<p>      * The array is not sorted (we could; lookup might be faster, but adding      * entries would be slower).      */
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|WeakReference
argument_list|<
name|SegmentId
argument_list|>
argument_list|>
name|references
init|=
name|newArrayList
argument_list|(
name|nCopies
argument_list|(
literal|1024
argument_list|,
operator|(
name|WeakReference
argument_list|<
name|SegmentId
argument_list|>
operator|)
literal|null
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SegmentIdTable
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * The refresh count (for diagnostics and testing).      */
specifier|private
name|int
name|rebuildCount
decl_stmt|;
comment|/**      * The number of used entries (WeakReferences) in this table.      */
specifier|private
name|int
name|entryCount
decl_stmt|;
comment|/**      * Get the segment id, and reference it in the weak references map. If the      * pair of MSB/LSB is not tracked by this table, a new instance of {@link      * SegmentId} is created using the provided {@link SegmentIdFactory} and      * tracked by this table.      *      * @param msb   The most significant bits of the {@link SegmentId}.      * @param lsb   The least significant bits of the {@link SegmentId}.      * @param maker A non-{@code null} instance of {@link SegmentIdFactory}.      * @return the segment id      */
annotation|@
name|Nonnull
specifier|synchronized
name|SegmentId
name|newSegmentId
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|,
name|SegmentIdFactory
name|maker
parameter_list|)
block|{
name|int
name|index
init|=
name|getIndex
argument_list|(
name|lsb
argument_list|)
decl_stmt|;
name|boolean
name|shouldRefresh
init|=
literal|false
decl_stmt|;
name|WeakReference
argument_list|<
name|SegmentId
argument_list|>
name|reference
init|=
name|references
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
while|while
condition|(
name|reference
operator|!=
literal|null
condition|)
block|{
name|SegmentId
name|id
init|=
name|reference
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
name|id
operator|.
name|getMostSignificantBits
argument_list|()
operator|==
name|msb
operator|&&
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
operator|==
name|lsb
condition|)
block|{
return|return
name|id
return|;
block|}
comment|// shouldRefresh if we have a garbage collected entry
name|shouldRefresh
operator|=
name|shouldRefresh
operator|||
name|id
operator|==
literal|null
expr_stmt|;
comment|// open addressing / linear probing
name|index
operator|=
operator|(
name|index
operator|+
literal|1
operator|)
operator|%
name|references
operator|.
name|size
argument_list|()
expr_stmt|;
name|reference
operator|=
name|references
operator|.
name|get
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
name|SegmentId
name|id
init|=
name|maker
operator|.
name|newSegmentId
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
decl_stmt|;
name|references
operator|.
name|set
argument_list|(
name|index
argument_list|,
operator|new
name|WeakReference
argument_list|<
name|SegmentId
argument_list|>
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|entryCount
operator|++
expr_stmt|;
if|if
condition|(
name|entryCount
operator|>
name|references
operator|.
name|size
argument_list|()
operator|*
literal|0.75
condition|)
block|{
comment|// more than 75% full
name|shouldRefresh
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|shouldRefresh
condition|)
block|{
name|refresh
argument_list|()
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
comment|/**      * Returns all segment identifiers that are currently referenced in memory.      *      * @param ids referenced segment identifiers      */
name|void
name|collectReferencedIds
parameter_list|(
name|Collection
argument_list|<
name|SegmentId
argument_list|>
name|ids
parameter_list|)
block|{
name|ids
operator|.
name|addAll
argument_list|(
name|refresh
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|synchronized
name|Collection
argument_list|<
name|SegmentId
argument_list|>
name|refresh
parameter_list|()
block|{
name|int
name|size
init|=
name|references
operator|.
name|size
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|SegmentId
argument_list|,
name|WeakReference
argument_list|<
name|SegmentId
argument_list|>
argument_list|>
name|ids
init|=
name|newHashMapWithExpectedSize
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|boolean
name|hashCollisions
init|=
literal|false
decl_stmt|;
name|boolean
name|emptyReferences
init|=
literal|false
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|WeakReference
argument_list|<
name|SegmentId
argument_list|>
name|reference
init|=
name|references
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|reference
operator|!=
literal|null
condition|)
block|{
name|SegmentId
name|id
init|=
name|reference
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|ids
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|reference
argument_list|)
expr_stmt|;
name|hashCollisions
operator|=
name|hashCollisions
operator|||
operator|(
name|i
operator|!=
name|getIndex
argument_list|(
name|id
argument_list|)
operator|)
expr_stmt|;
block|}
else|else
block|{
name|references
operator|.
name|set
argument_list|(
name|i
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|entryCount
operator|--
expr_stmt|;
name|emptyReferences
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|entryCount
operator|!=
name|ids
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// something is wrong, possibly a concurrency problem, a SegmentId
comment|// hashcode or equals bug, or a problem with this hash table
comment|// algorithm
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unexpected entry count mismatch, expected {} got {}"
argument_list|,
name|entryCount
argument_list|,
name|ids
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// we fix the count, because having a wrong entry count would be
comment|// very problematic; even worse than having a concurrency problem
name|entryCount
operator|=
name|ids
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
literal|2
operator|*
name|ids
operator|.
name|size
argument_list|()
operator|>
name|size
condition|)
block|{
name|size
operator|*=
literal|2
expr_stmt|;
block|}
comment|// we need to re-build the table if the new size is different,
comment|// but also if we removed some of the entries (because an entry was
comment|// garbage collected) and there is at least one entry at the "wrong"
comment|// location (due to open addressing)
if|if
condition|(
operator|(
name|hashCollisions
operator|&&
name|emptyReferences
operator|)
operator|||
name|size
operator|!=
name|references
operator|.
name|size
argument_list|()
condition|)
block|{
name|rebuildCount
operator|++
expr_stmt|;
name|references
operator|.
name|clear
argument_list|()
expr_stmt|;
name|references
operator|.
name|addAll
argument_list|(
name|nCopies
argument_list|(
name|size
argument_list|,
operator|(
name|WeakReference
argument_list|<
name|SegmentId
argument_list|>
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|SegmentId
argument_list|,
name|WeakReference
argument_list|<
name|SegmentId
argument_list|>
argument_list|>
name|entry
range|:
name|ids
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|int
name|index
init|=
name|getIndex
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|references
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|index
operator|=
operator|(
name|index
operator|+
literal|1
operator|)
operator|%
name|size
expr_stmt|;
block|}
name|references
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ids
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|private
name|int
name|getIndex
parameter_list|(
name|SegmentId
name|id
parameter_list|)
block|{
return|return
name|getIndex
argument_list|(
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|int
name|getIndex
parameter_list|(
name|long
name|lsb
parameter_list|)
block|{
return|return
operator|(
operator|(
name|int
operator|)
name|lsb
operator|)
operator|&
operator|(
name|references
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|)
return|;
block|}
specifier|synchronized
name|void
name|clearSegmentIdTables
parameter_list|(
annotation|@
name|Nonnull
name|Set
argument_list|<
name|UUID
argument_list|>
name|reclaimed
parameter_list|,
annotation|@
name|Nonnull
name|String
name|gcInfo
parameter_list|)
block|{
for|for
control|(
name|WeakReference
argument_list|<
name|SegmentId
argument_list|>
name|reference
range|:
name|references
control|)
block|{
if|if
condition|(
name|reference
operator|!=
literal|null
condition|)
block|{
name|SegmentId
name|id
init|=
name|reference
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
name|reclaimed
operator|.
name|contains
argument_list|(
name|id
operator|.
name|asUUID
argument_list|()
argument_list|)
condition|)
block|{
name|id
operator|.
name|reclaimed
argument_list|(
name|gcInfo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Get the number of map rebuild operations (used for testing and diagnostics).      *       * @return the rebuild count      */
name|int
name|getMapRebuildCount
parameter_list|()
block|{
return|return
name|rebuildCount
return|;
block|}
comment|/**      * Get the entry count (used for testing and diagnostics).      *       * @return the entry count      */
name|int
name|getEntryCount
parameter_list|()
block|{
return|return
name|entryCount
return|;
block|}
comment|/**      * Get the size of the internal map (used for testing and diagnostics).      *       * @return the map size      */
name|int
name|getMapSize
parameter_list|()
block|{
return|return
name|references
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * Get the raw list of segment ids (used for testing).      *       * @return the raw list      */
name|List
argument_list|<
name|SegmentId
argument_list|>
name|getRawSegmentIdList
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|SegmentId
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|SegmentId
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|WeakReference
argument_list|<
name|SegmentId
argument_list|>
name|ref
range|:
name|references
control|)
block|{
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
name|SegmentId
name|id
init|=
name|ref
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|list
return|;
block|}
block|}
end_class

end_unit

