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
name|mk
operator|.
name|server
package|;
end_package

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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Stream that reads bytes until it sees a given string boundary, preceded  * by CR+LF, as used in multipart/form-data uploads.  */
end_comment

begin_class
class|class
name|BoundaryInputStream
extends|extends
name|InputStream
block|{
specifier|private
name|InputStream
name|in
decl_stmt|;
specifier|private
specifier|final
name|byte
index|[]
name|boundary
decl_stmt|;
specifier|private
specifier|final
name|byte
index|[]
name|buf
decl_stmt|;
specifier|private
name|int
name|offset
decl_stmt|;
specifier|private
name|int
name|count
decl_stmt|;
specifier|private
name|int
name|boundaryIndex
decl_stmt|;
specifier|private
name|boolean
name|eos
decl_stmt|;
comment|/**      * Create a new instance of this class.      *      * @param in base input      * @param boundary boundary      */
specifier|public
name|BoundaryInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|String
name|boundary
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|boundary
argument_list|,
literal|8192
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new instance of this class.      *      * @param in base input      * @param boundary boundary      * @param size size of internal read-ahead buffer      */
specifier|public
name|BoundaryInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|String
name|boundary
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|boundary
operator|=
operator|(
literal|"\r\n"
operator|+
name|boundary
operator|)
operator|.
name|getBytes
argument_list|()
expr_stmt|;
comment|// Must be able to unread this many bytes
if|if
condition|(
name|size
operator|<
name|this
operator|.
name|boundary
operator|.
name|length
operator|+
literal|1
condition|)
block|{
name|size
operator|=
name|this
operator|.
name|boundary
operator|.
name|length
operator|+
literal|1
expr_stmt|;
block|}
name|buf
operator|=
operator|new
name|byte
index|[
name|size
index|]
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
if|if
condition|(
name|eos
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
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
name|int
name|count
init|=
name|read
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|b
index|[
literal|0
index|]
operator|&
literal|0xff
return|;
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
if|if
condition|(
name|eos
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|offset
operator|==
name|count
condition|)
block|{
name|fillBuffer
argument_list|()
expr_stmt|;
if|if
condition|(
name|eos
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
return|return
name|copy
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
specifier|private
name|void
name|fillBuffer
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|boundaryIndex
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|boundary
argument_list|,
literal|0
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|boundaryIndex
argument_list|)
expr_stmt|;
block|}
name|offset
operator|=
name|boundaryIndex
expr_stmt|;
name|count
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|offset
argument_list|,
name|buf
operator|.
name|length
operator|-
name|offset
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|<
literal|0
condition|)
block|{
name|eos
operator|=
literal|true
expr_stmt|;
block|}
block|}
specifier|private
name|int
name|copy
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
name|int
name|i
init|=
literal|0
decl_stmt|,
name|j
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|count
operator|&&
name|j
operator|<
name|len
condition|)
block|{
if|if
condition|(
name|boundary
index|[
name|boundaryIndex
index|]
operator|==
name|buf
index|[
name|offset
operator|+
name|i
index|]
condition|)
block|{
name|boundaryIndex
operator|++
expr_stmt|;
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|boundaryIndex
operator|==
name|boundary
operator|.
name|length
condition|)
block|{
name|eos
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
if|if
condition|(
name|boundaryIndex
operator|>
literal|0
condition|)
block|{
name|i
operator|-=
name|boundaryIndex
expr_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
condition|)
block|{
name|offset
operator|+=
name|i
expr_stmt|;
name|count
operator|+=
operator|-
name|i
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
block|}
name|boundaryIndex
operator|=
literal|0
expr_stmt|;
block|}
name|b
index|[
name|off
operator|+
name|j
index|]
operator|=
name|buf
index|[
name|offset
operator|+
name|i
index|]
expr_stmt|;
name|i
operator|++
expr_stmt|;
name|j
operator|++
expr_stmt|;
block|}
block|}
name|offset
operator|+=
name|i
expr_stmt|;
return|return
name|j
operator|==
literal|0
operator|&&
name|eos
condition|?
operator|-
literal|1
else|:
name|j
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
name|in
operator|=
literal|null
expr_stmt|;
name|eos
operator|=
literal|true
expr_stmt|;
block|}
block|}
end_class

end_unit

