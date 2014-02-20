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
name|oak
operator|.
name|kernel
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
name|InputStream
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
name|plugins
operator|.
name|memory
operator|.
name|AbstractBlob
import|;
end_import

begin_comment
comment|/**  * This {@code Blob} implementation is backed by a binary stored in  * a {@code MicroKernel}.  */
end_comment

begin_class
specifier|public
class|class
name|KernelBlob
extends|extends
name|AbstractBlob
block|{
specifier|private
specifier|final
name|String
name|binaryID
decl_stmt|;
specifier|private
specifier|final
name|MicroKernel
name|kernel
decl_stmt|;
comment|/**      * The id returned from {@link MicroKernel#write(java.io.InputStream)}      * @return  the binary id of this blob      */
specifier|public
name|String
name|getBinaryID
parameter_list|()
block|{
return|return
name|binaryID
return|;
block|}
comment|/**      * Create a new instance for a binary id and a Microkernel.      * @param binaryID  id of the binary      * @param kernel      */
specifier|public
name|KernelBlob
parameter_list|(
name|String
name|binaryID
parameter_list|,
name|MicroKernel
name|kernel
parameter_list|)
block|{
name|this
operator|.
name|binaryID
operator|=
name|binaryID
expr_stmt|;
name|this
operator|.
name|kernel
operator|=
name|kernel
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|InputStream
name|getNewStream
parameter_list|()
block|{
return|return
operator|new
name|MicroKernelInputStream
argument_list|(
name|kernel
argument_list|,
name|binaryID
argument_list|)
return|;
block|}
comment|/**      * This implementation delegates the calculation of the length back      * to the underlying {@code MicroKernel}.      */
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|kernel
operator|.
name|getLength
argument_list|(
name|binaryID
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getReference
parameter_list|()
block|{
return|return
name|binaryID
return|;
block|}
comment|/**      * This implementation delegates back to the underlying {@code Microkernel}      * if other is also of type {@code KernelBlob}.      */
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|KernelBlob
condition|)
block|{
name|KernelBlob
name|that
init|=
operator|(
name|KernelBlob
operator|)
name|other
decl_stmt|;
return|return
name|binaryID
operator|.
name|equals
argument_list|(
name|that
operator|.
name|binaryID
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
return|;
block|}
specifier|private
specifier|static
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
block|}
block|}
end_class

end_unit

