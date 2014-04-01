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
operator|.
name|file
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_class
class|class
name|RandomAccess
implements|implements
name|FileAccess
block|{
specifier|private
specifier|final
name|RandomAccessFile
name|file
decl_stmt|;
specifier|private
name|boolean
name|updated
init|=
literal|false
decl_stmt|;
name|RandomAccess
parameter_list|(
annotation|@
name|Nonnull
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
name|checkNotNull
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
name|long
name|crc32
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
name|CRC32
name|checksum
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
name|checksum
operator|.
name|update
argument_list|(
name|read
argument_list|(
name|position
argument_list|,
name|length
argument_list|)
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|checksum
operator|.
name|getValue
argument_list|()
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
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|length
argument_list|)
decl_stmt|;
name|file
operator|.
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|file
operator|.
name|readFully
argument_list|(
name|entry
operator|.
name|array
argument_list|()
argument_list|)
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
name|write
parameter_list|(
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
throws|throws
name|IOException
block|{
name|file
operator|.
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|file
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|updated
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|updated
condition|)
block|{
name|file
operator|.
name|getFD
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
name|updated
operator|=
literal|false
expr_stmt|;
block|}
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
comment|// will automatically sync unsaved changes
block|}
block|}
end_class

end_unit

