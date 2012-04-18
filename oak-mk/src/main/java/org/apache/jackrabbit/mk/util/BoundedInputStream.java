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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilterInputStream
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
comment|/**  * Implementation of an {@code InputStream} that is bounded by a limit  * and will return {@code -1} on reads when this limit is exceeded.  */
end_comment

begin_class
specifier|public
class|class
name|BoundedInputStream
extends|extends
name|FilterInputStream
block|{
specifier|private
specifier|final
name|int
name|limit
decl_stmt|;
specifier|private
name|int
name|count
decl_stmt|;
comment|/**      * Create a new instance of this class.      *      * @param in input stream      * @param limit limit      */
specifier|public
name|BoundedInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|limit
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
name|count
operator|<
name|limit
condition|)
block|{
name|int
name|c
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|!=
operator|-
literal|1
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
return|return
operator|-
literal|1
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
name|count
operator|<
name|limit
condition|)
block|{
if|if
condition|(
name|limit
operator|-
name|count
operator|<
name|len
condition|)
block|{
name|len
operator|=
name|limit
operator|-
name|count
expr_stmt|;
block|}
name|int
name|n
init|=
name|in
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|count
operator|+=
name|n
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**      * Close this input stream. Finishes reading any pending chunks until      * the last chunk is received. Does<b>not</b> close the underlying input      * stream.      *      * @see java.io.FilterInputStream#close()      */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
return|return;
block|}
try|try
block|{
name|int
name|remains
init|=
name|limit
operator|-
name|count
decl_stmt|;
if|if
condition|(
name|remains
operator|>
literal|0
condition|)
block|{
name|in
operator|.
name|skip
argument_list|(
name|remains
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|in
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

