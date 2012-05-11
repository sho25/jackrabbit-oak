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
name|test
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
name|Random
import|;
end_import

begin_comment
comment|/**  * An {@code InputStream} based on pseudo-random data useful for testing.  *<p/>  * Instances created with identical parameter values do produce identical byte  * sequences.  */
end_comment

begin_class
specifier|public
class|class
name|TestInputStream
extends|extends
name|InputStream
block|{
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
specifier|private
name|long
name|pos
decl_stmt|;
specifier|private
name|boolean
name|closed
decl_stmt|;
specifier|public
name|TestInputStream
parameter_list|(
name|long
name|length
parameter_list|)
block|{
name|this
argument_list|(
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TestInputStream
parameter_list|(
name|long
name|seed
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|random
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
if|if
condition|(
name|length
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"length cannot be negative"
argument_list|)
throw|;
block|}
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
name|closed
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|closed
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
throws|throws
name|IOException
block|{
if|if
condition|(
name|n
operator|<=
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|long
name|skipped
decl_stmt|;
for|for
control|(
name|skipped
operator|=
literal|0
init|;
name|skipped
operator|<
name|n
condition|;
name|skipped
operator|++
control|)
block|{
if|if
condition|(
name|read
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
block|}
return|return
name|skipped
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|length
operator|-
name|pos
argument_list|)
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
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|closed
operator|=
literal|true
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
name|pos
operator|>=
name|length
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|pos
operator|++
expr_stmt|;
return|return
name|random
operator|.
name|nextInt
argument_list|()
operator|&
literal|0xff
return|;
block|}
block|}
end_class

end_unit

