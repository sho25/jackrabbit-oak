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
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
operator|.
name|wrap
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
name|io
operator|.
name|RandomAccessFile
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
name|zip
operator|.
name|CRC32
import|;
end_import

begin_class
specifier|public
class|class
name|IndexLoader
block|{
specifier|public
specifier|static
name|IndexLoader
name|newIndexLoader
parameter_list|(
name|int
name|blockSize
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|blockSize
operator|>
literal|0
argument_list|,
literal|"Invalid block size"
argument_list|)
expr_stmt|;
return|return
operator|new
name|IndexLoader
argument_list|(
name|blockSize
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|int
name|blockSize
decl_stmt|;
specifier|private
specifier|final
name|IndexLoaderV1
name|v1
decl_stmt|;
specifier|private
specifier|final
name|IndexLoaderV2
name|v2
decl_stmt|;
specifier|private
name|IndexLoader
parameter_list|(
name|int
name|blockSize
parameter_list|)
block|{
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
name|this
operator|.
name|v1
operator|=
operator|new
name|IndexLoaderV1
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|v2
operator|=
operator|new
name|IndexLoaderV2
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Index
name|loadIndex
parameter_list|(
name|RandomAccessFile
name|file
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidIndexException
block|{
name|long
name|length
init|=
name|file
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|%
name|blockSize
operator|!=
literal|0
operator|||
name|length
argument_list|<
literal|6
operator|*
name|blockSize
operator|||
name|length
argument_list|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unexpected size %d"
argument_list|,
name|length
argument_list|)
argument_list|)
throw|;
block|}
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
name|file
operator|.
name|seek
argument_list|(
name|length
operator|-
literal|2
operator|*
name|blockSize
operator|-
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|file
operator|.
name|readFully
argument_list|(
name|buffer
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|magic
init|=
name|buffer
operator|.
name|getInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|magic
operator|==
name|IndexLoaderV1
operator|.
name|MAGIC
condition|)
block|{
return|return
name|v1
operator|.
name|loadIndex
argument_list|(
name|file
argument_list|)
return|;
block|}
if|if
condition|(
name|magic
operator|==
name|IndexLoaderV2
operator|.
name|MAGIC
condition|)
block|{
return|return
name|v2
operator|.
name|loadIndex
argument_list|(
name|file
argument_list|)
return|;
block|}
throw|throw
operator|new
name|InvalidIndexException
argument_list|(
literal|"Unrecognized magic number"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

