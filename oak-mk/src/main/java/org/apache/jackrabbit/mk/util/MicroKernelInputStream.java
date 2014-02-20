begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|IOUtils
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

begin_comment
comment|/**  * An input stream to simplify reading a blob from a {@code MicroKernel}.  */
end_comment

begin_class
specifier|public
class|class
name|MicroKernelInputStream
extends|extends
name|InputStream
block|{
specifier|private
specifier|final
name|MicroKernel
name|mk
decl_stmt|;
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
specifier|private
name|long
name|pos
decl_stmt|;
specifier|private
name|long
name|length
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|byte
index|[]
name|oneByteBuff
decl_stmt|;
specifier|public
name|MicroKernelInputStream
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|mk
operator|=
name|mk
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
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
name|n
operator|<
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|length
operator|==
operator|-
literal|1
condition|)
block|{
name|length
operator|=
name|mk
operator|.
name|getLength
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|n
operator|=
name|Math
operator|.
name|min
argument_list|(
name|n
argument_list|,
name|length
operator|-
name|pos
argument_list|)
expr_stmt|;
name|pos
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
block|{
name|int
name|l
init|=
name|mk
operator|.
name|read
argument_list|(
name|id
argument_list|,
name|pos
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|<
literal|0
condition|)
block|{
return|return
name|l
return|;
block|}
name|pos
operator|+=
name|l
expr_stmt|;
return|return
name|l
return|;
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
name|oneByteBuff
operator|==
literal|null
condition|)
block|{
name|oneByteBuff
operator|=
operator|new
name|byte
index|[
literal|1
index|]
expr_stmt|;
block|}
name|int
name|len
init|=
name|read
argument_list|(
name|oneByteBuff
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|<
literal|0
condition|)
block|{
return|return
name|len
return|;
block|}
return|return
name|oneByteBuff
index|[
literal|0
index|]
operator|&
literal|0xff
return|;
block|}
specifier|public
specifier|static
name|byte
index|[]
name|readFully
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|len
init|=
operator|(
name|int
operator|)
name|mk
operator|.
name|getLength
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buff
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|MicroKernelInputStream
name|in
init|=
operator|new
name|MicroKernelInputStream
argument_list|(
name|mk
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|buff
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|buff
return|;
block|}
block|}
end_class

end_unit

