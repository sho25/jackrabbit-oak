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
name|commons
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
name|CharBuffer
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
operator|.
name|MapMode
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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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

begin_comment
comment|/**  * This is a wrapper around {@link ByteBuffer}. It maintains the same semantics  * and mechanisms of the {@link ByteBuffer}.  *<p>  * Java 9 introduced API changes to some methods in {@link ByteBuffer}. Instead  * of returning instances of {@link java.nio.Buffer Buffer}, those methods were  * rewritten to return instances of {@link ByteBuffer} instead. While this is  * perfectly fine at compile time, running "modern" code on Java 8 and earlier  * throws {@link NoSuchMethodError}. In order to prevent occurrences of this  * exceptions in the future, {@link Buffer} is used consistently in place of  * {@link ByteBuffer}. Since it is not possible to directly convert a {@link  * Buffer} into a {@link ByteBuffer} and the other way around, {@link Buffer}  * makes it less likely to develop dangerous code in the future.  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|Buffer
block|{
specifier|private
specifier|final
name|ByteBuffer
name|buffer
decl_stmt|;
specifier|private
name|Buffer
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|)
block|{
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
block|}
specifier|public
specifier|static
name|Buffer
name|map
parameter_list|(
name|FileChannel
name|channel
parameter_list|,
name|MapMode
name|mode
parameter_list|,
name|long
name|position
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Buffer
argument_list|(
name|channel
operator|.
name|map
argument_list|(
name|mode
argument_list|,
name|position
argument_list|,
name|size
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Buffer
name|wrap
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|)
block|{
return|return
operator|new
name|Buffer
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Buffer
name|wrap
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|pos
parameter_list|,
name|int
name|len
parameter_list|)
block|{
return|return
operator|new
name|Buffer
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|,
name|pos
argument_list|,
name|len
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Buffer
name|allocate
parameter_list|(
name|int
name|cap
parameter_list|)
block|{
return|return
operator|new
name|Buffer
argument_list|(
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|cap
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Buffer
name|allocateDirect
parameter_list|(
name|int
name|cap
parameter_list|)
block|{
return|return
operator|new
name|Buffer
argument_list|(
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|cap
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|remaining
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|remaining
argument_list|()
return|;
block|}
specifier|public
name|Buffer
name|asReadOnlyBuffer
parameter_list|()
block|{
return|return
operator|new
name|Buffer
argument_list|(
name|buffer
operator|.
name|asReadOnlyBuffer
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Buffer
name|position
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
operator|(
operator|(
name|java
operator|.
name|nio
operator|.
name|Buffer
operator|)
name|buffer
operator|)
operator|.
name|position
argument_list|(
name|pos
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|int
name|position
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|position
argument_list|()
return|;
block|}
specifier|public
name|Buffer
name|limit
parameter_list|(
name|int
name|lim
parameter_list|)
block|{
operator|(
operator|(
name|java
operator|.
name|nio
operator|.
name|Buffer
operator|)
name|buffer
operator|)
operator|.
name|limit
argument_list|(
name|lim
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|int
name|limit
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|limit
argument_list|()
return|;
block|}
specifier|public
name|Buffer
name|slice
parameter_list|()
block|{
return|return
operator|new
name|Buffer
argument_list|(
name|buffer
operator|.
name|slice
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|int
name|readFully
parameter_list|(
name|FileChannel
name|channel
parameter_list|,
name|int
name|position
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|buffer
operator|.
name|remaining
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|count
init|=
name|channel
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|position
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|<
literal|0
condition|)
block|{
break|break;
block|}
name|result
operator|+=
name|count
expr_stmt|;
name|position
operator|+=
name|count
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|Buffer
name|flip
parameter_list|()
block|{
operator|(
operator|(
name|java
operator|.
name|nio
operator|.
name|Buffer
operator|)
name|buffer
operator|)
operator|.
name|flip
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|int
name|getInt
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|getInt
argument_list|()
return|;
block|}
specifier|public
name|int
name|getInt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|buffer
operator|.
name|getInt
argument_list|(
name|pos
argument_list|)
return|;
block|}
specifier|public
name|Buffer
name|mark
parameter_list|()
block|{
operator|(
operator|(
name|java
operator|.
name|nio
operator|.
name|Buffer
operator|)
name|buffer
operator|)
operator|.
name|mark
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Buffer
name|get
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
block|{
name|buffer
operator|.
name|get
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Buffer
name|get
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|pos
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|buffer
operator|.
name|get
argument_list|(
name|b
argument_list|,
name|pos
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|byte
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|buffer
operator|.
name|get
argument_list|(
name|pos
argument_list|)
return|;
block|}
specifier|public
name|byte
name|get
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|Buffer
name|reset
parameter_list|()
block|{
operator|(
operator|(
name|java
operator|.
name|nio
operator|.
name|Buffer
operator|)
name|buffer
operator|)
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|void
name|update
parameter_list|(
name|CRC32
name|checksum
parameter_list|)
block|{
name|checksum
operator|.
name|update
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
specifier|public
name|byte
index|[]
name|array
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|array
argument_list|()
return|;
block|}
specifier|public
name|int
name|capacity
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|capacity
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isDirect
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|isDirect
argument_list|()
return|;
block|}
specifier|public
name|Buffer
name|put
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
block|{
name|buffer
operator|.
name|put
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Buffer
name|put
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|pos
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|buffer
operator|.
name|put
argument_list|(
name|buf
argument_list|,
name|pos
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Buffer
name|put
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
name|buffer
operator|.
name|put
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Buffer
name|put
parameter_list|(
name|Buffer
name|b
parameter_list|)
block|{
name|buffer
operator|.
name|put
argument_list|(
name|b
operator|.
name|buffer
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Buffer
name|rewind
parameter_list|()
block|{
operator|(
operator|(
name|java
operator|.
name|nio
operator|.
name|Buffer
operator|)
name|buffer
operator|)
operator|.
name|rewind
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getLong
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|buffer
operator|.
name|getLong
argument_list|(
name|pos
argument_list|)
return|;
block|}
specifier|public
name|long
name|getLong
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|getLong
argument_list|()
return|;
block|}
specifier|public
name|short
name|getShort
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|buffer
operator|.
name|getShort
argument_list|(
name|pos
argument_list|)
return|;
block|}
specifier|public
name|Buffer
name|duplicate
parameter_list|()
block|{
return|return
operator|new
name|Buffer
argument_list|(
name|buffer
operator|.
name|duplicate
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|CharBuffer
name|decode
parameter_list|(
name|Charset
name|charset
parameter_list|)
block|{
return|return
name|charset
operator|.
name|decode
argument_list|(
name|buffer
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|hasRemaining
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|hasRemaining
argument_list|()
return|;
block|}
specifier|public
name|int
name|write
parameter_list|(
name|WritableByteChannel
name|channel
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|channel
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
return|;
block|}
specifier|public
name|Buffer
name|putInt
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|buffer
operator|.
name|putInt
argument_list|(
name|i
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Buffer
name|putLong
parameter_list|(
name|long
name|l
parameter_list|)
block|{
name|buffer
operator|.
name|putLong
argument_list|(
name|l
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|obj
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|instanceof
name|Buffer
condition|)
block|{
return|return
name|buffer
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|Buffer
operator|)
name|obj
operator|)
operator|.
name|buffer
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

