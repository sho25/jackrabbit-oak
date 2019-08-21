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
name|data
package|;
end_package

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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|Channels
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|WritableByteChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|HexDump
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
name|commons
operator|.
name|Buffer
import|;
end_import

begin_class
class|class
name|SegmentDataUtils
block|{
specifier|private
name|SegmentDataUtils
parameter_list|()
block|{
comment|// Prevent instantiation
block|}
specifier|private
specifier|static
specifier|final
name|int
name|MAX_SEGMENT_SIZE
init|=
literal|1
operator|<<
literal|18
decl_stmt|;
specifier|static
name|void
name|hexDump
parameter_list|(
name|Buffer
name|buffer
parameter_list|,
name|OutputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|buffer
operator|.
name|remaining
argument_list|()
index|]
decl_stmt|;
name|buffer
operator|.
name|duplicate
argument_list|()
operator|.
name|get
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|HexDump
operator|.
name|dump
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|stream
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|static
name|void
name|binDump
parameter_list|(
name|Buffer
name|buffer
parameter_list|,
name|OutputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|Buffer
name|data
init|=
name|buffer
operator|.
name|duplicate
argument_list|()
decl_stmt|;
try|try
init|(
name|WritableByteChannel
name|channel
init|=
name|Channels
operator|.
name|newChannel
argument_list|(
name|stream
argument_list|)
init|)
block|{
while|while
condition|(
name|data
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|data
operator|.
name|write
argument_list|(
name|channel
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|static
name|int
name|estimateMemoryUsage
parameter_list|(
name|Buffer
name|buffer
parameter_list|)
block|{
return|return
name|buffer
operator|.
name|isDirect
argument_list|()
condition|?
literal|0
else|:
name|buffer
operator|.
name|remaining
argument_list|()
return|;
block|}
specifier|static
name|Buffer
name|readBytes
parameter_list|(
name|Buffer
name|buffer
parameter_list|,
name|int
name|index
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|Buffer
name|duplicate
init|=
name|buffer
operator|.
name|duplicate
argument_list|()
decl_stmt|;
name|duplicate
operator|.
name|position
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|duplicate
operator|.
name|limit
argument_list|(
name|index
operator|+
name|size
argument_list|)
expr_stmt|;
return|return
name|duplicate
operator|.
name|slice
argument_list|()
return|;
block|}
specifier|static
name|int
name|index
parameter_list|(
name|Buffer
name|buffer
parameter_list|,
name|int
name|recordReferenceOffset
parameter_list|)
block|{
return|return
name|buffer
operator|.
name|limit
argument_list|()
operator|-
operator|(
name|MAX_SEGMENT_SIZE
operator|-
name|recordReferenceOffset
operator|)
return|;
block|}
block|}
end_class

end_unit

