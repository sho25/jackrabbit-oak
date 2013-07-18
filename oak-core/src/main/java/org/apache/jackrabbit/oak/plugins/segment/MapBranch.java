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
name|checkArgument
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
name|checkState
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
name|Iterables
operator|.
name|concat
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
name|Iterables
operator|.
name|transform
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
name|bitCount
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|RECORD_ID_BYTES
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|base
operator|.
name|Function
import|;
end_import

begin_class
class|class
name|MapBranch
extends|extends
name|MapRecord
block|{
specifier|private
specifier|final
name|int
name|bitmap
decl_stmt|;
name|MapBranch
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
parameter_list|,
name|int
name|bitmap
parameter_list|)
block|{
name|super
argument_list|(
name|store
argument_list|,
name|id
argument_list|,
name|size
argument_list|,
name|level
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|size
operator|>
name|BUCKETS_PER_LEVEL
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|level
operator|<
name|MAX_NUMBER_OF_LEVELS
argument_list|)
expr_stmt|;
name|this
operator|.
name|bitmap
operator|=
name|bitmap
expr_stmt|;
block|}
name|RecordId
index|[]
name|getBuckets
parameter_list|()
block|{
name|Segment
name|segment
init|=
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|getOffset
argument_list|()
operator|+
literal|8
decl_stmt|;
name|RecordId
index|[]
name|buckets
init|=
operator|new
name|RecordId
index|[
name|BUCKETS_PER_LEVEL
index|]
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
name|buckets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|bitmap
operator|&
operator|(
literal|1
operator|<<
name|i
operator|)
operator|)
operator|!=
literal|0
condition|)
block|{
name|buckets
index|[
name|i
index|]
operator|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|RECORD_ID_BYTES
expr_stmt|;
block|}
else|else
block|{
name|buckets
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
name|buckets
return|;
block|}
annotation|@
name|Override
name|RecordId
name|getEntry
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|int
name|mask
init|=
name|BUCKETS_PER_LEVEL
operator|-
literal|1
decl_stmt|;
name|int
name|shift
init|=
name|level
operator|*
name|LEVEL_BITS
decl_stmt|;
name|int
name|index
init|=
operator|(
name|key
operator|.
name|hashCode
argument_list|()
operator|>>
name|shift
operator|)
operator|&
name|mask
decl_stmt|;
name|int
name|bit
init|=
literal|1
operator|<<
name|index
decl_stmt|;
if|if
condition|(
operator|(
name|bitmap
operator|&
name|bit
operator|)
operator|!=
literal|0
condition|)
block|{
name|int
name|offset
init|=
name|getOffset
argument_list|()
operator|+
literal|8
operator|+
name|bitCount
argument_list|(
name|bitmap
operator|&
operator|(
name|bit
operator|-
literal|1
operator|)
argument_list|)
operator|*
name|RECORD_ID_BYTES
decl_stmt|;
name|RecordId
name|id
init|=
name|getSegment
argument_list|()
operator|.
name|readRecordId
argument_list|(
name|offset
argument_list|)
decl_stmt|;
return|return
name|MapRecord
operator|.
name|readMap
argument_list|(
name|store
argument_list|,
name|id
argument_list|)
operator|.
name|getEntry
argument_list|(
name|key
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
name|Iterable
argument_list|<
name|String
argument_list|>
name|getKeys
parameter_list|()
block|{
return|return
name|concat
argument_list|(
name|transform
argument_list|(
name|asList
argument_list|(
name|getBuckets
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Function
argument_list|<
name|RecordId
argument_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|apply
parameter_list|(
annotation|@
name|Nullable
name|RecordId
name|input
parameter_list|)
block|{
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
block|{
return|return
name|MapRecord
operator|.
name|readMap
argument_list|(
name|store
argument_list|,
name|input
argument_list|)
operator|.
name|getKeys
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
name|Iterable
argument_list|<
name|MapEntry
argument_list|>
name|getEntries
parameter_list|()
block|{
return|return
name|concat
argument_list|(
name|transform
argument_list|(
name|asList
argument_list|(
name|getBuckets
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Function
argument_list|<
name|RecordId
argument_list|,
name|Iterable
argument_list|<
name|MapEntry
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Iterable
argument_list|<
name|MapEntry
argument_list|>
name|apply
parameter_list|(
annotation|@
name|Nullable
name|RecordId
name|input
parameter_list|)
block|{
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
block|{
return|return
name|MapRecord
operator|.
name|readMap
argument_list|(
name|store
argument_list|,
name|input
argument_list|)
operator|.
name|getEntries
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
name|boolean
name|compare
parameter_list|(
name|MapRecord
name|base
parameter_list|,
name|MapDiff
name|diff
parameter_list|)
block|{
if|if
condition|(
name|base
operator|instanceof
name|MapBranch
condition|)
block|{
return|return
name|compare
argument_list|(
operator|(
name|MapBranch
operator|)
name|base
argument_list|,
name|diff
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|compare
argument_list|(
name|base
argument_list|,
name|diff
argument_list|)
return|;
block|}
block|}
specifier|private
name|boolean
name|compare
parameter_list|(
name|MapBranch
name|before
parameter_list|,
name|MapDiff
name|diff
parameter_list|)
block|{
name|MapBranch
name|after
init|=
name|this
decl_stmt|;
name|checkState
argument_list|(
name|after
operator|.
name|level
operator|==
name|before
operator|.
name|level
argument_list|)
expr_stmt|;
name|RecordId
index|[]
name|afterBuckets
init|=
name|after
operator|.
name|getBuckets
argument_list|()
decl_stmt|;
name|RecordId
index|[]
name|beforeBuckets
init|=
name|before
operator|.
name|getBuckets
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
name|BUCKETS_PER_LEVEL
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|afterBuckets
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|beforeBuckets
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|MapRecord
name|map
init|=
name|MapRecord
operator|.
name|readMap
argument_list|(
name|store
argument_list|,
name|beforeBuckets
index|[
name|i
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|MapEntry
name|entry
range|:
name|map
operator|.
name|getEntries
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|entryDeleted
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
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
block|}
elseif|else
if|if
condition|(
name|beforeBuckets
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|MapRecord
name|map
init|=
name|MapRecord
operator|.
name|readMap
argument_list|(
name|store
argument_list|,
name|afterBuckets
index|[
name|i
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|MapEntry
name|entry
range|:
name|map
operator|.
name|getEntries
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|entryAdded
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
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
elseif|else
if|if
condition|(
operator|!
name|afterBuckets
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|beforeBuckets
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|MapRecord
name|afterMap
init|=
name|MapRecord
operator|.
name|readMap
argument_list|(
name|store
argument_list|,
name|afterBuckets
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|MapRecord
name|beforeMap
init|=
name|MapRecord
operator|.
name|readMap
argument_list|(
name|store
argument_list|,
name|beforeBuckets
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|afterMap
operator|.
name|compare
argument_list|(
name|beforeMap
argument_list|,
name|diff
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
block|}
end_class

end_unit

