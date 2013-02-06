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
name|plugins
operator|.
name|segment
operator|.
name|SegmentWriter
operator|.
name|BLOCK_SIZE
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
name|InputStream
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentStream
extends|extends
name|InputStream
block|{
specifier|private
specifier|final
name|SegmentReader
name|reader
decl_stmt|;
specifier|private
specifier|final
name|ListRecord
name|blocks
decl_stmt|;
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
specifier|private
name|long
name|position
init|=
literal|0
decl_stmt|;
specifier|private
name|long
name|mark
init|=
literal|0
decl_stmt|;
name|SegmentStream
parameter_list|(
name|SegmentReader
name|reader
parameter_list|,
name|ListRecord
name|blocks
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|mark
parameter_list|(
name|int
name|readlimit
parameter_list|)
block|{
name|mark
operator|=
name|position
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
name|position
operator|=
name|mark
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|read
argument_list|(
name|b
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|b
index|[
literal|0
index|]
operator|&
literal|0xff
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|checkNotNull
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|checkPositionIndexes
argument_list|(
name|off
argument_list|,
name|off
operator|+
name|len
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|position
operator|==
name|length
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
name|int
name|blockIndex
init|=
call|(
name|int
call|)
argument_list|(
name|position
operator|/
name|SegmentWriter
operator|.
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|int
name|blockOffset
init|=
call|(
name|int
call|)
argument_list|(
name|position
operator|%
name|SegmentWriter
operator|.
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|blockOffset
operator|+
name|len
operator|>
name|SegmentWriter
operator|.
name|BLOCK_SIZE
condition|)
block|{
name|len
operator|=
name|SegmentWriter
operator|.
name|BLOCK_SIZE
operator|-
name|blockOffset
expr_stmt|;
block|}
if|if
condition|(
name|position
operator|+
name|len
operator|>
name|length
condition|)
block|{
name|len
operator|=
call|(
name|int
call|)
argument_list|(
name|length
operator|-
name|position
argument_list|)
expr_stmt|;
block|}
name|BlockRecord
name|block
init|=
name|reader
operator|.
name|readBlock
argument_list|(
name|blocks
operator|.
name|getEntry
argument_list|(
name|blockIndex
argument_list|)
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|len
operator|=
name|block
operator|.
name|read
argument_list|(
name|blockOffset
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|position
operator|+=
name|len
expr_stmt|;
return|return
name|len
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|n
parameter_list|)
block|{
if|if
condition|(
name|position
operator|+
name|n
operator|>
name|length
condition|)
block|{
name|n
operator|=
name|length
operator|-
name|position
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|position
operator|+
name|n
operator|<
literal|0
condition|)
block|{
name|n
operator|=
operator|-
name|position
expr_stmt|;
block|}
name|position
operator|+=
name|n
expr_stmt|;
return|return
name|n
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|position
operator|=
name|length
expr_stmt|;
block|}
block|}
end_class

end_unit

