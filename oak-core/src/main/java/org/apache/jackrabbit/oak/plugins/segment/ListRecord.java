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
name|checkElementIndex
import|;
end_import

begin_class
class|class
name|ListRecord
extends|extends
name|Record
block|{
specifier|static
specifier|final
name|int
name|LEVEL_SIZE
init|=
literal|1
operator|<<
literal|8
decl_stmt|;
comment|// 256
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
specifier|private
specifier|final
name|int
name|bucketSize
decl_stmt|;
name|ListRecord
parameter_list|(
name|RecordId
name|id
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|size
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|int
name|bs
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|bs
operator|*
name|LEVEL_SIZE
operator|<
name|size
condition|)
block|{
name|bs
operator|*=
name|LEVEL_SIZE
expr_stmt|;
block|}
name|this
operator|.
name|bucketSize
operator|=
name|bs
expr_stmt|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
specifier|public
name|RecordId
name|getEntry
parameter_list|(
name|SegmentReader
name|reader
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|checkElementIndex
argument_list|(
name|index
argument_list|,
name|size
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|==
literal|1
condition|)
block|{
return|return
name|getRecordId
argument_list|()
return|;
block|}
else|else
block|{
name|int
name|bucketIndex
init|=
name|index
operator|/
name|bucketSize
decl_stmt|;
name|int
name|bucketOffset
init|=
name|index
operator|%
name|bucketSize
decl_stmt|;
name|RecordId
name|bucketId
init|=
name|reader
operator|.
name|readRecordId
argument_list|(
name|getRecordId
argument_list|()
argument_list|,
name|bucketIndex
operator|*
literal|4
argument_list|)
decl_stmt|;
name|ListRecord
name|bucket
init|=
operator|new
name|ListRecord
argument_list|(
name|bucketId
argument_list|,
name|bucketSize
argument_list|)
decl_stmt|;
return|return
name|bucket
operator|.
name|getEntry
argument_list|(
name|reader
argument_list|,
name|bucketOffset
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

