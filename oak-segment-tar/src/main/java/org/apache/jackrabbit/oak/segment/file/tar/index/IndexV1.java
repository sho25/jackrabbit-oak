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
operator|.
name|file
operator|.
name|tar
operator|.
name|index
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
name|Sets
operator|.
name|newHashSetWithExpectedSize
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
class|class
name|IndexV1
implements|implements
name|Index
block|{
specifier|static
specifier|final
name|int
name|FOOTER_SIZE
init|=
literal|16
decl_stmt|;
specifier|private
specifier|final
name|ByteBuffer
name|index
decl_stmt|;
name|IndexV1
parameter_list|(
name|ByteBuffer
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|UUID
argument_list|>
name|getUUIDs
parameter_list|()
block|{
name|Set
argument_list|<
name|UUID
argument_list|>
name|uuids
init|=
name|newHashSetWithExpectedSize
argument_list|(
name|index
operator|.
name|remaining
argument_list|()
operator|/
name|IndexEntryV1
operator|.
name|SIZE
argument_list|)
decl_stmt|;
name|int
name|position
init|=
name|index
operator|.
name|position
argument_list|()
decl_stmt|;
while|while
condition|(
name|position
operator|<
name|index
operator|.
name|limit
argument_list|()
condition|)
block|{
name|long
name|msb
init|=
name|index
operator|.
name|getLong
argument_list|(
name|position
argument_list|)
decl_stmt|;
name|long
name|lsb
init|=
name|index
operator|.
name|getLong
argument_list|(
name|position
operator|+
literal|8
argument_list|)
decl_stmt|;
name|uuids
operator|.
name|add
argument_list|(
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
argument_list|)
expr_stmt|;
name|position
operator|+=
name|IndexEntryV1
operator|.
name|SIZE
expr_stmt|;
block|}
return|return
name|uuids
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexEntryV1
name|findEntry
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
block|{
comment|// The segment identifiers are randomly generated with uniform
comment|// distribution, so we can use interpolation search to find the
comment|// matching entry in the index. The average runtime is O(log log n).
name|int
name|lowIndex
init|=
literal|0
decl_stmt|;
name|int
name|highIndex
init|=
name|index
operator|.
name|remaining
argument_list|()
operator|/
name|IndexEntryV1
operator|.
name|SIZE
operator|-
literal|1
decl_stmt|;
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
operator|+
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
operator|(
name|highValue
operator|-
name|lowValue
operator|)
argument_list|)
decl_stmt|;
name|int
name|position
init|=
name|index
operator|.
name|position
argument_list|()
operator|+
name|guessIndex
operator|*
name|IndexEntryV1
operator|.
name|SIZE
decl_stmt|;
name|long
name|m
init|=
name|index
operator|.
name|getLong
argument_list|(
name|position
argument_list|)
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
name|index
operator|.
name|getLong
argument_list|(
name|position
operator|+
literal|8
argument_list|)
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
return|return
operator|new
name|IndexEntryV1
argument_list|(
name|index
argument_list|,
name|position
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|index
operator|.
name|remaining
argument_list|()
operator|+
name|FOOTER_SIZE
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|entryCount
parameter_list|()
block|{
return|return
name|index
operator|.
name|remaining
argument_list|()
operator|/
name|IndexEntryV1
operator|.
name|SIZE
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexEntryV1
name|entry
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
operator|new
name|IndexEntryV1
argument_list|(
name|index
argument_list|,
name|index
operator|.
name|position
argument_list|()
operator|+
name|i
operator|*
name|IndexEntryV1
operator|.
name|SIZE
argument_list|)
return|;
block|}
block|}
end_class

end_unit

