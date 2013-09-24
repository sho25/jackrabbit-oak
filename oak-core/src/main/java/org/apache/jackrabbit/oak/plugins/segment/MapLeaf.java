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
name|Iterator
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
name|NoSuchElementException
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
name|Maps
import|;
end_import

begin_class
class|class
name|MapLeaf
extends|extends
name|MapRecord
block|{
name|MapLeaf
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
operator|!=
literal|0
operator|||
name|level
operator|==
literal|0
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|size
operator|<=
name|BUCKETS_PER_LEVEL
operator|||
name|level
operator|==
name|MAX_NUMBER_OF_LEVELS
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|MapEntry
argument_list|>
name|getMapEntries
parameter_list|()
block|{
name|RecordId
index|[]
name|keys
init|=
operator|new
name|RecordId
index|[
name|size
index|]
decl_stmt|;
name|RecordId
index|[]
name|values
init|=
operator|new
name|RecordId
index|[
name|size
index|]
decl_stmt|;
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
literal|4
operator|+
name|size
operator|*
literal|4
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
name|keys
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
name|values
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
name|Map
argument_list|<
name|String
argument_list|,
name|MapEntry
argument_list|>
name|entries
init|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|segment
operator|.
name|readString
argument_list|(
name|keys
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|entries
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|MapEntry
argument_list|(
name|store
argument_list|,
name|name
argument_list|,
name|keys
index|[
name|i
index|]
argument_list|,
name|values
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|entries
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
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|int
name|hash
init|=
name|key
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|Segment
name|segment
init|=
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|index
operator|<
name|size
operator|&&
name|getHash
argument_list|(
name|segment
argument_list|,
name|index
argument_list|)
operator|<
name|hash
condition|)
block|{
name|index
operator|++
expr_stmt|;
block|}
while|while
condition|(
name|index
operator|<
name|size
operator|&&
name|getHash
argument_list|(
name|segment
argument_list|,
name|index
argument_list|)
operator|==
name|hash
condition|)
block|{
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|getKey
argument_list|(
name|segment
argument_list|,
name|index
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|getValue
argument_list|(
name|segment
argument_list|,
name|index
argument_list|)
return|;
block|}
name|index
operator|++
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
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
operator|new
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|getKeyIterator
argument_list|()
return|;
block|}
block|}
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
name|getMapEntries
argument_list|()
operator|.
name|values
argument_list|()
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
name|MapLeaf
condition|)
block|{
return|return
name|compare
argument_list|(
operator|(
name|MapLeaf
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
name|MapLeaf
name|before
parameter_list|,
name|MapDiff
name|diff
parameter_list|)
block|{
name|Segment
name|bs
init|=
name|before
operator|.
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|bi
init|=
literal|0
decl_stmt|;
name|MapLeaf
name|after
init|=
name|this
decl_stmt|;
name|Segment
name|as
init|=
name|after
operator|.
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|ai
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|ai
operator|<
name|after
operator|.
name|size
condition|)
block|{
name|int
name|afterHash
init|=
name|after
operator|.
name|getHash
argument_list|(
name|as
argument_list|,
name|ai
argument_list|)
decl_stmt|;
name|String
name|afterKey
init|=
name|after
operator|.
name|getKey
argument_list|(
name|as
argument_list|,
name|ai
argument_list|)
decl_stmt|;
name|RecordId
name|afterValue
init|=
name|after
operator|.
name|getValue
argument_list|(
name|as
argument_list|,
name|ai
argument_list|)
decl_stmt|;
while|while
condition|(
name|bi
operator|<
name|before
operator|.
name|size
operator|&&
operator|(
name|before
operator|.
name|getHash
argument_list|(
name|bs
argument_list|,
name|bi
argument_list|)
operator|<
name|afterHash
operator|||
operator|(
name|before
operator|.
name|getHash
argument_list|(
name|bs
argument_list|,
name|bi
argument_list|)
operator|==
name|afterHash
operator|&&
name|before
operator|.
name|getKey
argument_list|(
name|bs
argument_list|,
name|bi
argument_list|)
operator|.
name|compareTo
argument_list|(
name|afterKey
argument_list|)
operator|<
literal|0
operator|)
operator|)
condition|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|entryDeleted
argument_list|(
name|before
operator|.
name|getKey
argument_list|(
name|bs
argument_list|,
name|bi
argument_list|)
argument_list|,
name|before
operator|.
name|getValue
argument_list|(
name|bs
argument_list|,
name|bi
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|bi
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|bi
operator|<
name|before
operator|.
name|size
operator|&&
name|before
operator|.
name|getHash
argument_list|(
name|bs
argument_list|,
name|bi
argument_list|)
operator|==
name|afterHash
operator|&&
name|before
operator|.
name|getKey
argument_list|(
name|bs
argument_list|,
name|bi
argument_list|)
operator|.
name|equals
argument_list|(
name|afterKey
argument_list|)
condition|)
block|{
name|RecordId
name|beforeValue
init|=
name|before
operator|.
name|getValue
argument_list|(
name|bs
argument_list|,
name|bi
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|afterValue
operator|.
name|equals
argument_list|(
name|beforeValue
argument_list|)
operator|&&
operator|!
name|diff
operator|.
name|entryChanged
argument_list|(
name|afterKey
argument_list|,
name|beforeValue
argument_list|,
name|afterValue
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|bi
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|diff
operator|.
name|entryAdded
argument_list|(
name|afterKey
argument_list|,
name|afterValue
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ai
operator|++
expr_stmt|;
block|}
while|while
condition|(
name|bi
operator|<
name|before
operator|.
name|size
condition|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|entryDeleted
argument_list|(
name|before
operator|.
name|getKey
argument_list|(
name|bs
argument_list|,
name|bi
argument_list|)
argument_list|,
name|before
operator|.
name|getValue
argument_list|(
name|bs
argument_list|,
name|bi
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|bi
operator|++
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|compareAgainstEmptyMap
parameter_list|(
name|MapDiff
name|diff
parameter_list|)
block|{
name|Segment
name|segment
init|=
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|keyOffset
init|=
name|getOffset
argument_list|()
operator|+
literal|4
operator|+
name|size
operator|*
literal|4
decl_stmt|;
name|int
name|valueOffset
init|=
name|keyOffset
operator|+
name|size
operator|*
name|RECORD_ID_BYTES
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
name|RecordId
name|key
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|keyOffset
operator|+
name|i
operator|*
name|RECORD_ID_BYTES
argument_list|)
decl_stmt|;
name|RecordId
name|value
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|valueOffset
operator|+
name|i
operator|*
name|RECORD_ID_BYTES
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|diff
operator|.
name|entryAdded
argument_list|(
name|segment
operator|.
name|readString
argument_list|(
name|key
argument_list|)
argument_list|,
name|value
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|//-----------------------------------------------------------< private>--
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|getKeyIterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|Segment
name|segment
init|=
name|getSegment
argument_list|()
decl_stmt|;
specifier|private
name|int
name|index
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|index
operator|<
name|size
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
name|int
name|i
init|=
name|index
operator|++
decl_stmt|;
if|if
condition|(
name|i
operator|<
name|size
condition|)
block|{
return|return
name|getKey
argument_list|(
name|segment
argument_list|,
name|i
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
specifier|private
name|int
name|getHash
parameter_list|(
name|Segment
name|segment
parameter_list|,
name|int
name|index
parameter_list|)
block|{
return|return
name|checkNotNull
argument_list|(
name|segment
argument_list|)
operator|.
name|readInt
argument_list|(
name|getOffset
argument_list|()
operator|+
literal|4
operator|+
name|index
operator|*
literal|4
argument_list|)
return|;
block|}
specifier|private
name|String
name|getKey
parameter_list|(
name|Segment
name|segment
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|segment
argument_list|)
expr_stmt|;
name|int
name|offset
init|=
name|getOffset
argument_list|()
operator|+
literal|4
operator|+
name|size
operator|*
literal|4
operator|+
name|index
operator|*
name|RECORD_ID_BYTES
decl_stmt|;
return|return
name|segment
operator|.
name|readString
argument_list|(
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|RecordId
name|getValue
parameter_list|(
name|Segment
name|segment
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|int
name|offset
init|=
name|getOffset
argument_list|()
operator|+
literal|4
operator|+
name|size
operator|*
literal|4
operator|+
name|size
operator|*
name|RECORD_ID_BYTES
operator|+
name|index
operator|*
name|RECORD_ID_BYTES
decl_stmt|;
return|return
name|checkNotNull
argument_list|(
name|segment
argument_list|)
operator|.
name|readRecordId
argument_list|(
name|offset
argument_list|)
return|;
block|}
block|}
end_class

end_unit

