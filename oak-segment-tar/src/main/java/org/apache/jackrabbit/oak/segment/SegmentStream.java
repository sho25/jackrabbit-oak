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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|base
operator|.
name|Charsets
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
name|io
operator|.
name|ByteStreams
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
name|segment
operator|.
name|spi
operator|.
name|persistence
operator|.
name|Buffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * For reading any record of type "VALUE" as binary streams.  */
end_comment

begin_class
specifier|public
class|class
name|SegmentStream
extends|extends
name|InputStream
block|{
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|1
operator|<<
literal|12
decl_stmt|;
comment|// 4kB
annotation|@
name|Nullable
specifier|public
specifier|static
name|RecordId
name|getRecordIdIfAvailable
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|SegmentStore
name|store
parameter_list|)
block|{
if|if
condition|(
name|stream
operator|instanceof
name|SegmentStream
condition|)
block|{
name|SegmentStream
name|sStream
init|=
operator|(
name|SegmentStream
operator|)
name|stream
decl_stmt|;
name|RecordId
name|id
init|=
name|sStream
operator|.
name|recordId
decl_stmt|;
if|if
condition|(
name|sStream
operator|.
name|position
operator|==
literal|0
operator|&&
name|id
operator|.
name|getSegmentId
argument_list|()
operator|.
name|sameStore
argument_list|(
name|store
argument_list|)
condition|)
block|{
return|return
name|id
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|final
name|RecordId
name|recordId
decl_stmt|;
specifier|private
specifier|final
name|Buffer
name|inline
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
name|RecordId
name|recordId
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
name|recordId
operator|=
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|this
operator|.
name|inline
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|checkNotNull
argument_list|(
name|blocks
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|length
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
name|SegmentStream
parameter_list|(
name|RecordId
name|recordId
parameter_list|,
name|Buffer
name|inline
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|recordId
operator|=
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|this
operator|.
name|inline
operator|=
name|inline
operator|.
name|duplicate
argument_list|()
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
name|List
argument_list|<
name|RecordId
argument_list|>
name|getBlockIds
parameter_list|()
block|{
if|if
condition|(
name|blocks
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|blocks
operator|.
name|getEntries
argument_list|()
return|;
block|}
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
specifier|public
name|String
name|getString
parameter_list|()
block|{
if|if
condition|(
name|inline
operator|!=
literal|null
condition|)
block|{
return|return
name|inline
operator|.
name|decode
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|length
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Too long value: "
operator|+
name|length
argument_list|)
throw|;
block|}
else|else
block|{
name|SegmentStream
name|stream
init|=
operator|new
name|SegmentStream
argument_list|(
name|recordId
argument_list|,
name|blocks
argument_list|,
name|length
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|length
index|]
decl_stmt|;
name|ByteStreams
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|data
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unexpected IOException"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
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
name|readLimit
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
argument_list|,
literal|0
argument_list|,
literal|1
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
annotation|@
name|NotNull
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
comment|//> 0 given the earlier check
block|}
if|if
condition|(
name|inline
operator|!=
literal|null
condition|)
block|{
name|inline
operator|.
name|position
argument_list|(
operator|(
name|int
operator|)
name|position
argument_list|)
expr_stmt|;
name|inline
operator|.
name|get
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
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
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|int
name|blockCount
init|=
operator|(
name|blockOffset
operator|+
name|len
operator|+
name|BLOCK_SIZE
operator|-
literal|1
operator|)
comment|// round up
operator|/
name|BLOCK_SIZE
decl_stmt|;
name|int
name|remaining
init|=
name|len
decl_stmt|;
name|List
argument_list|<
name|RecordId
argument_list|>
name|ids
init|=
name|blocks
operator|.
name|getEntries
argument_list|(
name|blockIndex
argument_list|,
name|blockCount
argument_list|)
decl_stmt|;
name|RecordId
name|previousId
init|=
name|ids
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// guaranteed to contain at least one
name|int
name|consecutiveBlocks
init|=
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|ids
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RecordId
name|id
init|=
literal|null
decl_stmt|;
comment|// id is null for the last iteration only
if|if
condition|(
name|i
operator|<
name|ids
operator|.
name|size
argument_list|()
condition|)
block|{
name|id
operator|=
name|ids
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|// Id can only be null in the last iteration. But then previousId cannot be
comment|// null as this would imply id was null in the previous iteration.
assert|assert
name|id
operator|==
literal|null
operator|||
name|previousId
operator|!=
literal|null
assert|;
if|if
condition|(
name|id
operator|!=
literal|null
comment|// blocks are in the same segment
operator|&&
name|id
operator|.
name|getSegmentId
argument_list|()
operator|.
name|equals
argument_list|(
name|previousId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
comment|// blocks are consecutive
operator|&&
name|id
operator|.
name|getRecordNumber
argument_list|()
operator|==
name|previousId
operator|.
name|getRecordNumber
argument_list|()
operator|+
name|consecutiveBlocks
operator|*
name|BLOCK_SIZE
condition|)
block|{
name|consecutiveBlocks
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|// read the current chunk of consecutive blocks
name|int
name|blockSize
init|=
name|Math
operator|.
name|min
argument_list|(
name|blockOffset
operator|+
name|remaining
argument_list|,
name|consecutiveBlocks
operator|*
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|BlockRecord
name|block
init|=
operator|new
name|BlockRecord
argument_list|(
name|previousId
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|int
name|n
init|=
name|blockSize
operator|-
name|blockOffset
decl_stmt|;
name|checkState
argument_list|(
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
name|n
argument_list|)
operator|==
name|n
argument_list|)
expr_stmt|;
name|off
operator|+=
name|n
expr_stmt|;
name|remaining
operator|-=
name|n
expr_stmt|;
name|previousId
operator|=
name|id
expr_stmt|;
name|consecutiveBlocks
operator|=
literal|1
expr_stmt|;
name|blockOffset
operator|=
literal|0
expr_stmt|;
block|}
block|}
name|checkState
argument_list|(
name|remaining
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
name|position
operator|+=
name|len
expr_stmt|;
return|return
name|len
return|;
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
name|int
name|available
parameter_list|()
block|{
if|if
condition|(
name|inline
operator|!=
literal|null
condition|)
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|length
operator|-
name|position
argument_list|)
return|;
comment|//<= inline.length
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
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

