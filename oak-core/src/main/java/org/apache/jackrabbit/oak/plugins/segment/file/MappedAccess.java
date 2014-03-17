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
name|READ_WRITE
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

begin_class
class|class
name|MappedAccess
implements|implements
name|FileAccess
block|{
specifier|private
specifier|final
name|MappedByteBuffer
name|buffer
decl_stmt|;
specifier|private
name|boolean
name|updated
init|=
literal|false
decl_stmt|;
name|MappedAccess
parameter_list|(
name|RandomAccessFile
name|file
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|long
name|l
init|=
name|file
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|0
condition|)
block|{
comment|// it's a new file
name|l
operator|=
name|length
expr_stmt|;
name|updated
operator|=
literal|true
expr_stmt|;
block|}
name|buffer
operator|=
name|file
operator|.
name|getChannel
argument_list|()
operator|.
name|map
argument_list|(
name|READ_WRITE
argument_list|,
literal|0
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|limit
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
name|position
argument_list|)
expr_stmt|;
name|entry
operator|.
name|limit
argument_list|(
name|position
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
specifier|synchronized
name|void
name|write
parameter_list|(
name|int
name|position
parameter_list|,
name|byte
index|[]
name|b
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
name|ByteBuffer
name|entry
init|=
name|buffer
operator|.
name|duplicate
argument_list|()
decl_stmt|;
name|entry
operator|.
name|position
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|entry
operator|.
name|put
argument_list|(
name|b
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
block|{
if|if
condition|(
name|updated
condition|)
block|{
name|buffer
operator|.
name|force
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
name|void
name|close
parameter_list|()
block|{     }
block|}
end_class

end_unit

