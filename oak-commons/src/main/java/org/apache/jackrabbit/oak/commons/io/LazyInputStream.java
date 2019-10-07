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
operator|.
name|io
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
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
name|ByteSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|input
operator|.
name|ClosedInputStream
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
name|GuavaDeprecation
import|;
end_import

begin_comment
comment|/**  * * This input stream delays accessing the {@link InputStream} until the first byte is read  */
end_comment

begin_class
specifier|public
class|class
name|LazyInputStream
extends|extends
name|FilterInputStream
block|{
specifier|private
specifier|final
name|ByteSource
name|byteSource
decl_stmt|;
specifier|private
specifier|final
name|Supplier
argument_list|<
name|InputStream
argument_list|>
name|inputStreamSupplier
decl_stmt|;
specifier|private
name|boolean
name|opened
decl_stmt|;
specifier|public
name|LazyInputStream
parameter_list|(
name|Supplier
argument_list|<
name|InputStream
argument_list|>
name|inputStreamSupplier
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|byteSource
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|inputStreamSupplier
operator|=
name|inputStreamSupplier
expr_stmt|;
block|}
comment|/**      * @deprecated Use {@link #LazyInputStream(Supplier)} instead      */
annotation|@
name|Deprecated
specifier|public
name|LazyInputStream
parameter_list|(
name|ByteSource
name|byteSource
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|byteSource
operator|=
name|byteSource
expr_stmt|;
name|this
operator|.
name|inputStreamSupplier
operator|=
literal|null
expr_stmt|;
name|GuavaDeprecation
operator|.
name|handleCall
argument_list|(
literal|"OAK-8661"
argument_list|)
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
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|read
argument_list|()
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
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|read
argument_list|(
name|b
argument_list|)
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
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
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
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|skip
argument_list|(
name|n
argument_list|)
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
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|available
argument_list|()
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
comment|// make sure the file is not opened afterwards
name|opened
operator|=
literal|true
expr_stmt|;
comment|// only close the file if it was in fact opened
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|in
operator|=
name|ClosedInputStream
operator|.
name|CLOSED_INPUT_STREAM
expr_stmt|;
block|}
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
name|ensureOpenWithUnCheckedException
argument_list|()
expr_stmt|;
name|super
operator|.
name|mark
argument_list|(
name|readlimit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
name|ensureOpenWithUnCheckedException
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|markSupported
argument_list|()
return|;
block|}
specifier|private
name|void
name|ensureOpen
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|opened
condition|)
block|{
name|opened
operator|=
literal|true
expr_stmt|;
name|in
operator|=
name|inputStreamSupplier
operator|!=
literal|null
condition|?
name|inputStreamSupplier
operator|.
name|get
argument_list|()
else|:
name|byteSource
operator|.
name|openStream
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|ensureOpenWithUnCheckedException
parameter_list|()
block|{
try|try
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
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
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

