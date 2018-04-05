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
name|azure
operator|.
name|queue
package|;
end_package

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
name|segment
operator|.
name|azure
operator|.
name|AzureSegmentArchiveEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|UUID
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentWriteAction
block|{
specifier|private
specifier|final
name|AzureSegmentArchiveEntry
name|indexEntry
decl_stmt|;
specifier|private
specifier|final
name|byte
index|[]
name|buffer
decl_stmt|;
specifier|private
specifier|final
name|int
name|offset
decl_stmt|;
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
specifier|public
name|SegmentWriteAction
parameter_list|(
name|AzureSegmentArchiveEntry
name|indexEntry
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
name|this
operator|.
name|indexEntry
operator|=
name|indexEntry
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|buffer
index|[
name|i
index|]
operator|=
name|buffer
index|[
name|i
operator|+
name|offset
index|]
expr_stmt|;
block|}
name|this
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
specifier|public
name|UUID
name|getUuid
parameter_list|()
block|{
return|return
operator|new
name|UUID
argument_list|(
name|indexEntry
operator|.
name|getMsb
argument_list|()
argument_list|,
name|indexEntry
operator|.
name|getLsb
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|ByteBuffer
name|toByteBuffer
parameter_list|()
block|{
return|return
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
name|void
name|passTo
parameter_list|(
name|SegmentWriteQueue
operator|.
name|SegmentConsumer
name|consumer
parameter_list|)
throws|throws
name|IOException
block|{
name|consumer
operator|.
name|consume
argument_list|(
name|indexEntry
argument_list|,
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getUuid
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit
