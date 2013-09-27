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
name|base
operator|.
name|Preconditions
operator|.
name|checkElementIndex
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|base
operator|.
name|Preconditions
operator|.
name|checkPositionIndex
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
name|lang
operator|.
name|Integer
operator|.
name|highestOneBit
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Integer
operator|.
name|numberOfTrailingZeros
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

begin_class
specifier|abstract
class|class
name|MapRecord
extends|extends
name|Record
block|{
comment|/**      * Number of bits of the hash code to look at on each level of the trie.      */
specifier|protected
specifier|static
specifier|final
name|int
name|BITS_PER_LEVEL
init|=
literal|5
decl_stmt|;
comment|/**      * Number of buckets at each level of the trie.      */
specifier|protected
specifier|static
specifier|final
name|int
name|BUCKETS_PER_LEVEL
init|=
literal|1
operator|<<
name|BITS_PER_LEVEL
decl_stmt|;
comment|// 32
comment|/**      * Maximum number of trie levels.      */
specifier|protected
specifier|static
specifier|final
name|int
name|MAX_NUMBER_OF_LEVELS
init|=
operator|(
literal|32
operator|+
name|BITS_PER_LEVEL
operator|-
literal|1
operator|)
operator|/
name|BITS_PER_LEVEL
decl_stmt|;
comment|// 7
comment|/**      * Number of bits needed to indicate the current trie level.      */
specifier|protected
specifier|static
specifier|final
name|int
name|LEVEL_BITS
init|=
comment|// 4, using nextPowerOfTwo():
name|numberOfTrailingZeros
argument_list|(
name|highestOneBit
argument_list|(
name|MAX_NUMBER_OF_LEVELS
argument_list|)
operator|<<
literal|1
argument_list|)
decl_stmt|;
comment|/**      * Number of bits used to indicate the size of a map.      */
specifier|protected
specifier|static
specifier|final
name|int
name|SIZE_BITS
init|=
literal|32
operator|-
name|LEVEL_BITS
decl_stmt|;
comment|/**      * Maximum size of a map.      */
specifier|protected
specifier|static
specifier|final
name|int
name|MAX_SIZE
init|=
operator|(
literal|1
operator|<<
name|SIZE_BITS
operator|)
operator|-
literal|1
decl_stmt|;
comment|// ~268e6
specifier|static
name|MapRecord
name|readMap
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|RecordId
name|id
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
name|checkNotNull
argument_list|(
name|store
argument_list|)
operator|.
name|readSegment
argument_list|(
name|id
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|head
init|=
name|segment
operator|.
name|readInt
argument_list|(
name|id
operator|.
name|getOffset
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|level
init|=
name|head
operator|>>>
name|SIZE_BITS
decl_stmt|;
name|int
name|size
init|=
name|head
operator|&
operator|(
operator|(
literal|1
operator|<<
name|SIZE_BITS
operator|)
operator|-
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|size
operator|>
name|BUCKETS_PER_LEVEL
operator|&&
name|level
operator|<
name|MAX_NUMBER_OF_LEVELS
condition|)
block|{
name|int
name|bitmap
init|=
name|segment
operator|.
name|readInt
argument_list|(
name|id
operator|.
name|getOffset
argument_list|()
operator|+
literal|4
argument_list|)
decl_stmt|;
return|return
operator|new
name|MapBranch
argument_list|(
name|store
argument_list|,
name|id
argument_list|,
name|size
argument_list|,
name|level
argument_list|,
name|bitmap
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|MapLeaf
argument_list|(
name|store
argument_list|,
name|id
argument_list|,
name|size
argument_list|,
name|level
argument_list|)
return|;
block|}
block|}
specifier|protected
specifier|final
name|SegmentStore
name|store
decl_stmt|;
specifier|protected
specifier|final
name|int
name|size
decl_stmt|;
specifier|protected
specifier|final
name|int
name|level
decl_stmt|;
specifier|protected
name|MapRecord
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|RecordId
name|id
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|super
argument_list|(
name|store
operator|.
name|getWriter
argument_list|()
operator|.
name|getDummySegment
argument_list|()
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|checkElementIndex
argument_list|(
name|size
argument_list|,
name|MAX_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|level
operator|=
name|checkPositionIndex
argument_list|(
name|level
argument_list|,
name|MAX_NUMBER_OF_LEVELS
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Segment
name|getSegment
parameter_list|()
block|{
return|return
name|getSegment
argument_list|(
name|getRecordId
argument_list|()
operator|.
name|getSegmentId
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|Segment
name|getSegment
parameter_list|(
name|UUID
name|uuid
parameter_list|)
block|{
return|return
name|store
operator|.
name|readSegment
argument_list|(
name|uuid
argument_list|)
return|;
block|}
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
specifier|abstract
name|RecordId
name|getEntry
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
specifier|abstract
name|Iterable
argument_list|<
name|String
argument_list|>
name|getKeys
parameter_list|()
function_decl|;
specifier|abstract
name|Iterable
argument_list|<
name|MapEntry
argument_list|>
name|getEntries
parameter_list|()
function_decl|;
specifier|abstract
name|boolean
name|compareAgainstEmptyMap
parameter_list|(
name|MapDiff
name|diff
parameter_list|)
function_decl|;
interface|interface
name|MapDiff
block|{
name|boolean
name|entryAdded
parameter_list|(
name|String
name|key
parameter_list|,
name|RecordId
name|after
parameter_list|)
function_decl|;
name|boolean
name|entryChanged
parameter_list|(
name|String
name|key
parameter_list|,
name|RecordId
name|before
parameter_list|,
name|RecordId
name|after
parameter_list|)
function_decl|;
name|boolean
name|entryDeleted
parameter_list|(
name|String
name|key
parameter_list|,
name|RecordId
name|before
parameter_list|)
function_decl|;
block|}
name|boolean
name|compare
parameter_list|(
name|MapRecord
name|that
parameter_list|,
name|MapDiff
name|diff
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|keys
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|MapEntry
name|entry
range|:
name|getEntries
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
name|RecordId
name|thisId
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|RecordId
name|thatId
init|=
name|that
operator|.
name|getEntry
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|thatId
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|entryAdded
argument_list|(
name|name
argument_list|,
name|thisId
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|thisId
operator|.
name|equals
argument_list|(
name|thatId
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|entryChanged
argument_list|(
name|name
argument_list|,
name|thatId
argument_list|,
name|thisId
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
name|keys
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|MapEntry
name|entry
range|:
name|that
operator|.
name|getEntries
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|keys
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|entryDeleted
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
literal|null
decl_stmt|;
for|for
control|(
name|MapEntry
name|entry
range|:
name|getEntries
argument_list|()
control|)
block|{
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
name|builder
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|"{ "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
return|return
literal|"{}"
return|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" }"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

