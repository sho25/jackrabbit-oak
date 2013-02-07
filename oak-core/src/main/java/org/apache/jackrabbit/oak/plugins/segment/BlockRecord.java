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
name|checkPositionIndexes
import|;
end_import

begin_class
class|class
name|BlockRecord
extends|extends
name|Record
block|{
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
name|BlockRecord
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
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
comment|/**      * Reads bytes from this block. Up to the given number of bytes are      * read starting from the given position within this block. The number      * of bytes read is returned.      *      * @param reader segment reader      * @param position position within this block      * @param buffer target buffer      * @param offset offset within the target buffer      * @param length maximum number of bytes to read      * @return number of bytes that could be read      */
specifier|public
name|int
name|read
parameter_list|(
name|SegmentReader
name|reader
parameter_list|,
name|int
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|checkElementIndex
argument_list|(
name|position
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|checkPositionIndexes
argument_list|(
name|offset
argument_list|,
name|offset
operator|+
name|length
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|position
operator|+
name|length
operator|>
name|size
condition|)
block|{
name|length
operator|=
name|size
operator|-
name|position
expr_stmt|;
block|}
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|reader
operator|.
name|readBytes
argument_list|(
name|getRecordId
argument_list|()
argument_list|,
name|position
argument_list|,
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
return|return
name|length
return|;
block|}
block|}
end_class

end_unit

