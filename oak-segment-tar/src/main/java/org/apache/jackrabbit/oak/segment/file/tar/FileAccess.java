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
name|segment
operator|.
name|file
operator|.
name|tar
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
name|checkState
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
operator|.
name|MapMode
operator|.
name|READ_ONLY
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
name|commons
operator|.
name|IOUtils
operator|.
name|readFully
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|nio
operator|.
name|MappedByteBuffer
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
name|FileChannel
import|;
end_import

begin_comment
comment|/**  * A wrapper around either memory mapped files or random access files, to allow  * reading from a file.  */
end_comment

begin_class
specifier|abstract
class|class
name|FileAccess
block|{
specifier|abstract
name|boolean
name|isMemoryMapped
parameter_list|()
function_decl|;
specifier|abstract
name|int
name|length
parameter_list|()
throws|throws
name|IOException
function_decl|;
specifier|abstract
name|ByteBuffer
name|read
parameter_list|(
name|int
name|position
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|//-----------------------------------------------------------< private>--
comment|/**      * The implementation that uses memory mapped files.      */
specifier|static
class|class
name|Mapped
extends|extends
name|FileAccess
block|{
specifier|private
specifier|final
name|RandomAccessFile
name|file
decl_stmt|;
specifier|private
name|MappedByteBuffer
name|buffer
decl_stmt|;
name|Mapped
parameter_list|(
name|RandomAccessFile
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
name|file
operator|.
name|getChannel
argument_list|()
operator|.
name|map
argument_list|(
name|READ_ONLY
argument_list|,
literal|0
argument_list|,
name|file
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|boolean
name|isMemoryMapped
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|remaining
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ByteBuffer
name|read
parameter_list|(
name|int
name|position
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|ByteBuffer
name|entry
init|=
name|buffer
operator|.
name|asReadOnlyBuffer
argument_list|()
decl_stmt|;
name|entry
operator|.
name|position
argument_list|(
name|entry
operator|.
name|position
argument_list|()
operator|+
name|position
argument_list|)
expr_stmt|;
name|entry
operator|.
name|limit
argument_list|(
name|entry
operator|.
name|position
argument_list|()
operator|+
name|length
argument_list|)
expr_stmt|;
return|return
name|entry
operator|.
name|slice
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|buffer
operator|=
literal|null
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * The implementation that uses random access file (reads are synchronized).      */
specifier|static
class|class
name|Random
extends|extends
name|FileAccess
block|{
specifier|private
specifier|final
name|RandomAccessFile
name|file
decl_stmt|;
specifier|protected
specifier|final
name|FileChannel
name|channel
decl_stmt|;
name|Random
parameter_list|(
name|RandomAccessFile
name|file
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|file
operator|.
name|getChannel
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
name|boolean
name|isMemoryMapped
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|int
name|length
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|length
init|=
name|file
operator|.
name|length
argument_list|()
decl_stmt|;
name|checkState
argument_list|(
name|length
operator|<
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
return|return
operator|(
name|int
operator|)
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|ByteBuffer
name|read
parameter_list|(
name|int
name|position
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteBuffer
name|entry
decl_stmt|;
name|entry
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|readFully
argument_list|(
name|channel
argument_list|,
name|position
argument_list|,
name|entry
argument_list|)
operator|<
name|length
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
name|entry
operator|.
name|flip
argument_list|()
expr_stmt|;
return|return
name|entry
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * The implementation that uses random access file (reads are synchronized)      * and off heap access.      */
specifier|static
class|class
name|RandomOffHeap
extends|extends
name|Random
block|{
name|RandomOffHeap
parameter_list|(
name|RandomAccessFile
name|file
parameter_list|)
block|{
name|super
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|ByteBuffer
name|read
parameter_list|(
name|int
name|position
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteBuffer
name|entry
decl_stmt|;
name|entry
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|readFully
argument_list|(
name|channel
argument_list|,
name|position
argument_list|,
name|entry
argument_list|)
operator|<
name|length
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
name|entry
operator|.
name|flip
argument_list|()
expr_stmt|;
return|return
name|entry
return|;
block|}
block|}
block|}
end_class

end_unit

