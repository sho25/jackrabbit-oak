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
name|index
operator|.
name|lucene
operator|.
name|util
package|;
end_package

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
name|Preconditions
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
name|AbstractIterator
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
name|Random
import|;
end_import

begin_comment
comment|/**  * Sampling algorithm that picks 'k' random samples from streaming input.  * The algorithm would maintain 'k/N' probability to pick any of the item  * where 'N' is the number of items seen currently.  *  * While the input could be streaming, the algorithm requires {@code N} to be known  * before hand.  *  * The algorithm produces random saamples without replacement and hence has O(1) extra  * memory complexity  *  * Implementation inspired from "JONES,T.G. A note on sampling a tape file"  * (https://dl.acm.org/citation.cfm?id=368159)  */
end_comment

begin_class
specifier|public
class|class
name|TapeSampling
parameter_list|<
name|T
parameter_list|>
block|{
specifier|private
specifier|final
name|Random
name|rGen
decl_stmt|;
specifier|private
specifier|final
name|Iterator
argument_list|<
name|T
argument_list|>
name|input
decl_stmt|;
specifier|private
specifier|final
name|int
name|N
decl_stmt|;
specifier|private
specifier|final
name|int
name|k
decl_stmt|;
specifier|public
name|TapeSampling
parameter_list|(
specifier|final
name|Random
name|rGen
parameter_list|,
specifier|final
name|Iterator
argument_list|<
name|T
argument_list|>
name|input
parameter_list|,
specifier|final
name|int
name|N
parameter_list|,
specifier|final
name|int
name|k
parameter_list|)
block|{
name|this
operator|.
name|rGen
operator|=
name|rGen
expr_stmt|;
name|this
operator|.
name|input
operator|=
name|input
expr_stmt|;
name|this
operator|.
name|N
operator|=
name|N
expr_stmt|;
name|this
operator|.
name|k
operator|=
name|k
expr_stmt|;
block|}
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|getSamples
parameter_list|()
block|{
return|return
operator|new
name|AbstractIterator
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
name|int
name|sampled
init|=
literal|0
decl_stmt|;
name|int
name|seen
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|T
name|computeNext
parameter_list|()
block|{
if|if
condition|(
name|sampled
operator|==
name|k
condition|)
block|{
return|return
name|endOfData
argument_list|()
return|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|input
operator|.
name|hasNext
argument_list|()
argument_list|,
literal|"Not enough input items provided. Declared: "
operator|+
name|N
operator|+
literal|"; got "
operator|+
name|seen
operator|+
literal|"; sampled: "
operator|+
name|sampled
argument_list|)
expr_stmt|;
name|T
name|i
init|=
name|input
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|r
init|=
name|rGen
operator|.
name|nextInt
argument_list|(
name|N
operator|-
name|seen
argument_list|)
operator|+
literal|1
decl_stmt|;
name|seen
operator|++
expr_stmt|;
if|if
condition|(
name|r
operator|<=
name|k
operator|-
name|sampled
condition|)
block|{
name|sampled
operator|++
expr_stmt|;
return|return
name|i
return|;
block|}
block|}
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

